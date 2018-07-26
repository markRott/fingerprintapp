package app.com.fingerprintapp;

import android.app.Application;

import app.com.fingerprintapp.di.ApplicationModule;
import app.com.fingerprintapp.di.DaggerMainAppComponent;
import app.com.fingerprintapp.di.FingerprintModule;
import app.com.fingerprintapp.di.MainAppComponent;

public class MyApp extends Application {

    private static MyApp INSTANCE;
    private MainAppComponent mainAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        INSTANCE = this;

        mainAppComponent = DaggerMainAppComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .fingerprintModule(new FingerprintModule())
                .build();
    }

    public static MyApp getInstance() {
        return INSTANCE;
    }

    public MainAppComponent getMainAppComponent() {
        return mainAppComponent;
    }
}
