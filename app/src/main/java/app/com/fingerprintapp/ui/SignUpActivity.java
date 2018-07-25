package app.com.fingerprintapp.ui;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import app.com.fingerprintapp.MyApp;
import app.com.fingerprintapp.R;
import app.com.fingerprintapp.fingerprint.FingerprintSensorState;
import app.com.fingerprintapp.fingerprint.IFingerprintInteractor;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

public class SignUpActivity extends BaseActivity {

    public static final String KEY_PASSWORD = "KEY_PASSWORD";

    private EditText newPinCode;
    private Button setupPinCode;
    private SharedPreferences sharedPreferences;


    @Inject
    IFingerprintInteractor fingerprintInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        MyApp.getINSTANCE().getMainAppComponent().inject(this);
        initViews();
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFingerprintState();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearDispose();
    }

    private void initViews() {
        newPinCode = findViewById(R.id.edt_new_pin_code);
        setupPinCode = findViewById(R.id.btn_setup_pin_code);
        setupPinCode.setOnClickListener(v -> savePinCode());
    }

    private void savePinCode() {
        final String pinCode = newPinCode.getText().toString().trim();
//        final String cryptoPinCode = Utils.encryptString(pinCode);
//        sharedPreferences.edit().putString(KEY_PASSWORD, cryptoPinCode).apply();
    }

    private void checkFingerprintState() {
        Disposable fingerprintDisposable = fingerprintInteractor
                .checkFingerprintSensorState()
                .subscribeWith(new DisposableSingleObserver<FingerprintSensorState>() {
                    @Override
                    public void onSuccess(FingerprintSensorState fingerprintSensorState) {
                        handleFingerprintSensorState(fingerprintSensorState);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }
                });
        addDisposable(fingerprintDisposable);
    }

    private void handleFingerprintSensorState(FingerprintSensorState fingerprintSensorState) {
        switch (fingerprintSensorState) {
            case NOT_SUPPORTED:
                System.out.println("NOT_SUPPORTED");
                break;
            case NOT_SET_UP:
                System.out.println("NOT_SET_UP");
                break;
            case READY:
                System.out.println("READY");
                break;
        }
    }
}
