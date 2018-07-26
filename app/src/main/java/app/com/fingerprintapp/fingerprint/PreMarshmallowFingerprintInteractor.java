package app.com.fingerprintapp.fingerprint;

import app.com.fingerprintapp.fingerprint.enums.FingerprintSensorState;
import app.com.fingerprintapp.fingerprint.model.FingerprintEventData;
import io.reactivex.Observable;
import io.reactivex.Single;

public class PreMarshmallowFingerprintInteractor implements IFingerprintInteractor {

    @Override
    public Single<FingerprintSensorState> checkFingerprintSensorState() {
        return Single
                .create(singleSubscriber ->
                        singleSubscriber.onSuccess(FingerprintSensorState.NOT_SUPPORTED));
    }

    @Override
    public Observable<FingerprintEventData> startFingerprint() {
        return Observable.empty();
    }

    @Override
    public void stopFingerPrint() {
        // Nothing to do
    }
}
