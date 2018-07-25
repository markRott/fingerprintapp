package app.com.fingerprintapp.fingerprint;

import io.reactivex.Single;

public interface IFingerprintInteractor {

    Single<FingerprintSensorState> checkFingerprintSensorState();
}
