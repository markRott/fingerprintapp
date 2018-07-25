package app.com.fingerprintapp.di;

import android.app.KeyguardManager;
import android.content.Context;
import android.os.Build;
import android.support.v4.hardware.fingerprint.FingerprintManagerCompat;

import java.util.Objects;

import javax.inject.Singleton;

import app.com.fingerprintapp.fingerprint.IFingerprintInteractor;
import app.com.fingerprintapp.fingerprint.PreMarshmallowFingerprintInteractor;
import app.com.fingerprintapp.fingerprint.RealFingerprintInteractor;
import dagger.Module;
import dagger.Provides;

@Module
public class FingerprintModule {

    @Provides
    @Singleton
    public IFingerprintInteractor provideFingerprintInteractor(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return new RealFingerprintInteractor(
                    FingerprintManagerCompat.from(context),
                    Objects.requireNonNull(context.getSystemService(KeyguardManager.class)));
        } else {
            return new PreMarshmallowFingerprintInteractor();
        }
    }
}
