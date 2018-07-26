package app.com.fingerprintapp.fingerprint;

public class FingerprintEventData {

    private String message;
    private FingerprintEventType fingerprintEventType;

    FingerprintEventData(FingerprintEventType fingerprintEventType, String message) {
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
