package app.com.fingerprintapp.fingerprint.secure;

import android.app.KeyguardManager;
import android.content.Context;
import android.hardware.fingerprint.FingerprintManager;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyPermanentlyInvalidatedException;
import android.security.keystore.KeyProperties;
import android.support.annotation.Nullable;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;
import android.util.Base64;
import android.util.Log;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;

import static android.content.Context.FINGERPRINT_SERVICE;

public class SecureManager implements ISecureContract {

    private static final String KEY_ALIAS = "FINGERPRINT_KEY_PAIR_ALIAS";
    private static final String KEY_STORE = "AndroidKeyStore";
    private static final String TAG = SecureManager.class.getSimpleName();

    private Context context;
    private Cipher cipher;
    private KeyStore keyStore;
    private KeyPairGenerator keyPairGenerator;

    public SecureManager(Context context) {
        this.context = context;
    }

    @Override
    public boolean checkSensorState() {
        FingerprintManager fingerprintManager = (FingerprintManager) context.getSystemService(FINGERPRINT_SERVICE);
        if (fingerprintManager.isHardwareDetected()) {
            KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
            return keyguardManager.isKeyguardSecure() && fingerprintManager.hasEnrolledFingerprints();
        } else return false;
    }

    @Override
    @Nullable
    public String encryptString(String pinCode) {
        try {
            if (isReady() && initCipherMode(Cipher.ENCRYPT_MODE)) {
                byte[] bytes = cipher.doFinal(pinCode.getBytes());
                return Base64.encodeToString(bytes, Base64.NO_WRAP);
            }
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            Log.e(TAG, "Error encrypt string", e);
        }
        return null;
    }

    @Override
    @Nullable
    public String decryptString(String string, Cipher cipher) {
        try {
            byte[] bytes = Base64.decode(string, Base64.NO_WRAP);
            final String pin = new String(cipher.doFinal(bytes));
            return pin;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            Log.e(TAG, "Error decrypt string", e);
        }
        return null;
    }

    @Override
    @Nullable
    public FingerprintManagerCompat.CryptoObject createCryptoObject() {
        if (isReady() && initCipherMode(Cipher.DECRYPT_MODE)) {
            return new FingerprintManagerCompat.CryptoObject(cipher);
        }
        return null;
    }

    private boolean initKeyStore() {
        try {
            keyStore = KeyStore.getInstance(KEY_STORE);
            keyStore.load(null);
            return true;
        } catch (KeyStoreException | IOException | NoSuchAlgorithmException | CertificateException e) {
            Log.e(TAG, "Error init keystore", e);
        }
        return false;
    }

    private boolean initCipher() {
        try {
            cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            return true;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            Log.e(TAG, "Error init cipher", e);
        }
        return false;
    }

    private boolean initKey() {
        try {
            return keyStore.containsAlias(KEY_ALIAS) || generateNewKey();
        } catch (KeyStoreException e) {
            Log.e(TAG, "Error init key", e);
        }
        return false;
    }

    private boolean generateNewKey() {
        if (initKeyGenerator()) {
            try {
                keyPairGenerator.initialize(initKeyGenParameterSpec());
                keyPairGenerator.generateKeyPair();
                return true;
            } catch (InvalidAlgorithmParameterException e) {
                Log.e(TAG, "Error generate New Key", e);
            }
        }
        return false;
    }

    private boolean initKeyGenerator() {
        try {
            keyPairGenerator = KeyPairGenerator.getInstance(KeyProperties.KEY_ALGORITHM_RSA, KEY_STORE);
            return true;
        } catch (NoSuchAlgorithmException | NoSuchProviderException e) {
            Log.e(TAG, "Error init Key Generator", e);
        }
        return false;
    }

    private KeyGenParameterSpec initKeyGenParameterSpec() {
        return new KeyGenParameterSpec.Builder(
                KEY_ALIAS,
                KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                .setDigests(KeyProperties.DIGEST_SHA256, KeyProperties.DIGEST_SHA512)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_OAEP)
                .setUserAuthenticationRequired(true)
                .build();
    }

    private boolean initCipherMode(int mode) {
        try {
            keyStore.load(null);
            switch (mode) {
                case Cipher.ENCRYPT_MODE:
                    initEncodeCipher(mode);
                    break;
                case Cipher.DECRYPT_MODE:
                    initDecodeCipher(mode);
                    break;
                default:
                    return false;
            }
            return true;

        } catch (KeyPermanentlyInvalidatedException e) {

            deleteInvalidKey();
            Log.e(TAG, "Delete invalid key", e);

        } catch (KeyStoreException | CertificateException | UnrecoverableKeyException | IOException
                | NoSuchAlgorithmException | InvalidKeyException | InvalidAlgorithmParameterException
                | InvalidKeySpecException e) {
            Log.e(TAG, "Error init cipher mode", e);
        }
        return false;
    }

    private void initEncodeCipher(int mode) throws KeyStoreException, InvalidKeySpecException,
            NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException {
        PublicKey key = keyStore.getCertificate(KEY_ALIAS).getPublicKey();
        PublicKey unrestricted = KeyFactory.getInstance(key.getAlgorithm())
                .generatePublic(new X509EncodedKeySpec(key.getEncoded()));
        OAEPParameterSpec spec = new OAEPParameterSpec(
                "SHA-256",
                "MGF1",
                MGF1ParameterSpec.SHA1,
                PSource.PSpecified.DEFAULT);
        cipher.init(mode, unrestricted, spec);
    }

    private void initDecodeCipher(int mode) throws KeyStoreException, NoSuchAlgorithmException,
            UnrecoverableKeyException, InvalidKeyException {
        PrivateKey key = (PrivateKey) keyStore.getKey(KEY_ALIAS, null);
        cipher.init(mode, key);
    }

    private void deleteInvalidKey() {
        if (keyStore != null) {
            try {
                keyStore.deleteEntry(KEY_ALIAS);
            } catch (KeyStoreException e) {
                Log.e(TAG, "Error deleting key", e);
            }
        }
    }

    private boolean isReady() {
        return initKeyStore() && initCipher() && initKey();
    }
}
