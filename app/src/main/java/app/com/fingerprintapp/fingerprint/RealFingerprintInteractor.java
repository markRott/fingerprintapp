package app.com.fingerprintapp.fingerprint;

import android.app.KeyguardManager;
import android.support.annotation.NonNull;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import io.reactivex.Single;

public class RealFingerprintInteractor implements IFingerprintInteractor {

    @NonNull
    private final FingerprintManagerCompat fingerprintManager;
    @NonNull
    private final KeyguardManager keyguardManager;

    public RealFingerprintInteractor(
            @NonNull FingerprintManagerCompat fingerprintManager,
            @NonNull KeyguardManager keyguardManager) {
        this.fingerprintManager = fingerprintManager;
        this.keyguardManager = keyguardManager;
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
}
