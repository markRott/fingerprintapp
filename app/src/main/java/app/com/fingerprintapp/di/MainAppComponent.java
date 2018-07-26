package app.com.fingerprintapp.di;

import javax.inject.Singleton;

import app.com.fingerprintapp.ui.MainActivity;
import app.com.fingerprintapp.ui.SignUpActivity;
import dagger.Component;

@Singleton
@Component(modules = {
        ApplicationModule.class,
        FingerprintModule.class,
})
public interface MainAppComponent {

    void inject(SignUpActivity signUpActivity);

    void inject(MainActivity loginActivity);
}
