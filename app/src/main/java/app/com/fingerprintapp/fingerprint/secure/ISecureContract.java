package app.com.fingerprintapp.fingerprint.secure;

import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import javax.crypto.Cipher;

public interface ISecureContract {

    String encryptString(String pinCode);

    String decryptString(String decryptPinCode, Cipher cipher);

    FingerprintManagerCompat.CryptoObject createCryptoObject();

    boolean checkSensorState();
}
