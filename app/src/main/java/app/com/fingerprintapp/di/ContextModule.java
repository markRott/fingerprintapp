package app.com.fingerprintapp.di;

import android.content.Context;

import javax.inject.Singleton;

import app.com.fingerprintapp.MyApp;
import dagger.Module;
import dagger.Provides;

@Module
public class ContextModule {

    private MyApp application;

    public ContextModule(MyApp application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }
}
