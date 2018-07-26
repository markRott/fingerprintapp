package app.com.fingerprintapp.fingerprint.storage;

import android.content.SharedPreferences;
import android.support.annotation.Nullable;

public class PrefFingerprintStorage implements IPinStorage {

    private static final String KEY_PASSWORD = "KEY_PIN_CODE";

    private SharedPreferences sharedPreferences;

    public PrefFingerprintStorage(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    @Override
    public void saveEncryptedPinCode(String pinCode) {
        if (!hasPinCode()) {
            sharedPreferences.edit().putString(KEY_PASSWORD, pinCode).apply();
        }
    }

    @Nullable
    @Override
    public String getEncryptedPin() {
        return sharedPreferences.getString(KEY_PASSWORD, "");
    }

    @Override
    public boolean hasPinCode() {
        final String pinCode = getEncryptedPin();
        return (pinCode != null && !pinCode.isEmpty());
    }
}
