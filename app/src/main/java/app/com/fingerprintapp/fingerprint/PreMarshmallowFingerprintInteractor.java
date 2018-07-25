package app.com.fingerprintapp.fingerprint;

import io.reactivex.Single;

public class PreMarshmallowFingerprintInteractor implements IFingerprintInteractor {

    @Override
    public Single<FingerprintSensorState> checkFingerprintSensorState() {
        return Single
                .create(singleSubscriber ->
                        singleSubscriber.onSuccess(FingerprintSensorState.NOT_SUPPORTED));
    }
}
