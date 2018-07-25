package app.com.fingerprintapp.di;

import android.content.Context;

import javax.inject.Singleton;

import app.com.fingerprintapp.fingerprint.IFingerprintInteractor;
import app.com.fingerprintapp.ui.LoginActivity;
import app.com.fingerprintapp.ui.SignUpActivity;
import dagger.Component;

@Singleton
@Component(modules = {ContextModule.class, FingerprintModule.class})
public interface MainAppComponent {

    void inject(SignUpActivity signUpActivity);

    void inject(LoginActivity loginActivity);

    Context context();

    IFingerprintInteractor fingerprintInteractor();
}
