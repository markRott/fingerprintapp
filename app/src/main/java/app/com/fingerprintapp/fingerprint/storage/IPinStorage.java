package app.com.fingerprintapp.fingerprint.storage;

import android.support.annotation.Nullable;

public interface IPinStorage {

    void saveEncryptedPinCode(String pinCode);

    @Nullable
    String getEncryptedPin();

    boolean hasPinCode();
}
