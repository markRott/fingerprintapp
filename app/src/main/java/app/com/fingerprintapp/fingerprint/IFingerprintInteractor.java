package app.com.fingerprintapp.fingerprint;

import app.com.fingerprintapp.fingerprint.enums.FingerprintSensorState;
import app.com.fingerprintapp.fingerprint.model.FingerprintEventData;
import io.reactivex.Observable;
import io.reactivex.Single;

public interface IFingerprintInteractor {

    Single<FingerprintSensorState> checkFingerprintSensorState();

    Observable<FingerprintEventData> startFingerprint();

    void stopFingerPrint();
}
