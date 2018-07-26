package app.com.fingerprintapp.di;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import javax.inject.Singleton;

import app.com.fingerprintapp.MyApp;
import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private MyApp application;

    public ApplicationModule(MyApp application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Context provideContext() {
        return application;
    }

    @Provides
    @Singleton
    public SharedPreferences provideSharedPreferences(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context);
    }
}
