package app.com.fingerprintapp.di;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import java.util.Objects;

import javax.inject.Singleton;

import app.com.fingerprintapp.fingerprint.IFingerprintInteractor;
import app.com.fingerprintapp.fingerprint.secure.ISecureContract;
import app.com.fingerprintapp.fingerprint.storage.IPinStorage;
import app.com.fingerprintapp.fingerprint.PreMarshmallowFingerprintInteractor;
import app.com.fingerprintapp.fingerprint.storage.PrefFingerprintStorage;
import app.com.fingerprintapp.fingerprint.RealFingerprintInteractor;
import app.com.fingerprintapp.fingerprint.secure.SecureInteractor;
import dagger.Module;
import dagger.Provides;

@Module
public class FingerprintModule {

    @Provides
    @Singleton
    public IFingerprintInteractor provideFingerprintInteractor(
            Context context,
            ISecureContract secureInteractor,
            IPinStorage pinStorage) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new RealFingerprintInteractor(
                    FingerprintManagerCompat.from(context),
                    Objects.requireNonNull(context.getSystemService(KeyguardManager.class)),
                    secureInteractor,
                    pinStorage);
        } else {
            return new PreMarshmallowFingerprintInteractor();
        }
    }

    @Provides
    @Singleton
    public ISecureContract provideSecureInteractor(Context context) {
        return new SecureInteractor(context);
    }

    @Provides
    @Singleton
    public IPinStorage providePinStorage(SharedPreferences sharedPreferences) {
        return new PrefFingerprintStorage(sharedPreferences);
    }
}
