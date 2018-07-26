package app.com.fingerprintapp.fingerprint;

import android.support.annotation.Nullable;

public interface IPinStorage {

    void saveEncryptedPinCode(String pinCode);

    @Nullable
    String getEncryptedPin();

    boolean hasPinCode();
}
