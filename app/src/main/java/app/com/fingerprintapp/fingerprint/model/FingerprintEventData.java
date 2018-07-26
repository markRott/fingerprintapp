package app.com.fingerprintapp.fingerprint.model;

import app.com.fingerprintapp.fingerprint.enums.FingerprintEventType;

public class FingerprintEventData {

    private String message;
    private FingerprintEventType fingerprintEventType;

    public FingerprintEventData(FingerprintEventType fingerprintEventType, String message) {
        this.fingerprintEventType = fingerprintEventType;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public FingerprintEventType getFingerprintEventType() {
        return fingerprintEventType;
    }

    @Override
    public String toString() {
        return "FingerprintEventData{" +
                "message='" + message + '\'' +
                ", fingerprintEventType=" + fingerprintEventType +
                '}';
    }
}
