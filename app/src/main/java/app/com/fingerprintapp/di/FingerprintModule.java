package app.com.fingerprintapp.di;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import java.util.Objects;

import javax.inject.Singleton;

import app.com.fingerprintapp.fingerprint.IFingerprintManager;
import app.com.fingerprintapp.fingerprint.secure.ISecureContract;
import app.com.fingerprintapp.fingerprint.storage.IPinStorage;
import app.com.fingerprintapp.fingerprint.PreMarshmallowFingerprintManager;
import app.com.fingerprintapp.fingerprint.storage.PrefFingerprintStorage;
import app.com.fingerprintapp.fingerprint.RealFingerprintManager;
import app.com.fingerprintapp.fingerprint.secure.SecureManager;
import dagger.Module;
import dagger.Provides;

@Module
public class FingerprintModule {

    @Provides
    @Singleton
    public IFingerprintManager provideFingerprintInteractor(
            Context context,
            ISecureContract secureInteractor,
            IPinStorage pinStorage) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new RealFingerprintManager(
                    FingerprintManagerCompat.from(context),
                    Objects.requireNonNull(context.getSystemService(KeyguardManager.class)),
                    secureInteractor,
                    pinStorage);
        } else {
            return new PreMarshmallowFingerprintManager();
        }
    }

    @Provides
    @Singleton
    public ISecureContract provideSecureInteractor(Context context) {
        return new SecureManager(context);
    }

    @Provides
    @Singleton
    public IPinStorage providePinStorage(SharedPreferences sharedPreferences) {
        return new PrefFingerprintStorage(sharedPreferences);
    }
}
