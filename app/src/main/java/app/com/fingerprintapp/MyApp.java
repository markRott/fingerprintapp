package app.com.fingerprintapp;

import android.app.Application;

import app.com.fingerprintapp.di.ContextModule;
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
                .contextModule(new ContextModule(this))
                .fingerprintModule(new FingerprintModule())
                .build();
        System.out.println("mainAppComponent = " + mainAppComponent);
    }

    public static MyApp getINSTANCE() {
        return INSTANCE;
    }

    public MainAppComponent getMainAppComponent() {
        return mainAppComponent;
    }
}
