package app.com.fingerprintapp.fingerprint;

import android.app.KeyguardManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.support.v4.os.CancellationSignal;

import javax.crypto.Cipher;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.Single;

public class RealFingerprintInteractor implements IFingerprintInteractor {

    private KeyguardManager keyguardManager;
    private CancellationSignal cancellationSignal;
    private FingerprintManagerCompat fingerprintManager;

    private IPinStorage pinStorage;
    private SecureInteractor secureInteractor;

    public RealFingerprintInteractor(
            @NonNull FingerprintManagerCompat fingerprintManager,
            @NonNull KeyguardManager keyguardManager,
            SecureInteractor secureInteractor,
            IPinStorage pinStorage) {

        this.fingerprintManager = fingerprintManager;
        this.keyguardManager = keyguardManager;
        this.secureInteractor = secureInteractor;
        this.pinStorage = pinStorage;
    }

    @Override
    public Single<FingerprintSensorState> checkFingerprintSensorState() {
        return Single
                .create(singleSubscriber ->
                        singleSubscriber.onSuccess(obtainFingerprintSensorState()));
    }

    @NonNull
    private FingerprintSensorState obtainFingerprintSensorState() {
        final FingerprintSensorState sensorState;
        if (fingerprintManager.isHardwareDetected()) {
            if (!keyguardManager.isKeyguardSecure() || !fingerprintManager.hasEnrolledFingerprints()) {
                sensorState = FingerprintSensorState.NOT_SET_UP;
            } else {
                sensorState = FingerprintSensorState.READY;
            }
        } else {
            sensorState = FingerprintSensorState.NOT_SUPPORTED;
        }
        return sensorState;
    }

    @Override
    public Observable<FingerprintEventData> startFingerprint() {
        return Observable.create(emitter -> {
            checkVersion(emitter);
            FingerprintManagerCompat.CryptoObject cryptoObject = createCryptoObject(emitter);
            cancellationSignal = new CancellationSignal();
            fingerprintManager.authenticate(
                    cryptoObject,
                    0,
                    cancellationSignal,
                    new AuthenticationCallbackWrapper(pinStorage, secureInteractor, emitter),
                    null);
        });
    }

    @Override
    public void stopFingerPrint() {
        if (cancellationSignal != null) {
            cancellationSignal.cancel();
            cancellationSignal = null;
        }
    }

    private void checkVersion(ObservableEmitter<FingerprintEventData> emitter) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            emitter.onError(new Exception("Device not support fingerprint"));
        }
    }

    private FingerprintManagerCompat.CryptoObject createCryptoObject(ObservableEmitter<FingerprintEventData> emitter) {
        FingerprintManagerCompat.CryptoObject cryptoObject = secureInteractor.createCryptoObject();
        if (cryptoObject == null) {
            emitter.onError(new Exception("Fail create crypto object"));
        }
        return cryptoObject;
    }

    private static class AuthenticationCallbackWrapper extends FingerprintManagerCompat.AuthenticationCallback {

        private final IPinStorage pinStorage;
        private final SecureInteractor secureInteractor;
        private final ObservableEmitter<FingerprintEventData> emitter;

        AuthenticationCallbackWrapper(
                IPinStorage pinStorage,
                SecureInteractor secureInteractor,
                ObservableEmitter<FingerprintEventData> emitter) {
            this.pinStorage = pinStorage;
            this.secureInteractor = secureInteractor;
            this.emitter = emitter;
        }

        @Override
        public void onAuthenticationError(int errMsgId, CharSequence errString) {
            if (!emitter.isDisposed()) {
                emitter.onError(new Exception(errString.toString()));

            }
        }

        @Override
        public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
            emitter.onNext(new FingerprintEventData(FingerprintEventType.HINT, helpString.toString()));
        }

        @Override
        public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
            final Cipher cipher = result.getCryptoObject().getCipher();
            final String encryptedPinCode = pinStorage.getEncryptedPin();
            final String decryptPinCode = secureInteractor.decryptString(encryptedPinCode, cipher);

            emitter.onNext(new FingerprintEventData(
                    FingerprintEventType.SUCCESS,
                    decryptPinCode));
            emitter.onComplete();
        }

        @Override
        public void onAuthenticationFailed() {
            emitter.onNext(new FingerprintEventData(FingerprintEventType.FAIL, "onAuthenticationFailed"));
        }
    }
}
