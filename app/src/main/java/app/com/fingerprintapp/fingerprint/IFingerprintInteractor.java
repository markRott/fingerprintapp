package app.com.fingerprintapp.fingerprint;

import io.reactivex.Observable;
import io.reactivex.Single;

public interface IFingerprintInteractor {

    Single<FingerprintSensorState> checkFingerprintSensorState();

    Observable<FingerprintEventData> startFingerprint();

    void stopFingerPrint();
}
