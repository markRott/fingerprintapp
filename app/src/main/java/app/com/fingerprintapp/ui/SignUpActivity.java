package app.com.fingerprintapp.ui;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import javax.inject.Inject;

import app.com.fingerprintapp.MyApp;
import app.com.fingerprintapp.R;
import app.com.fingerprintapp.fingerprint.FingerprintSensorState;
import app.com.fingerprintapp.fingerprint.IFingerprintInteractor;
import app.com.fingerprintapp.fingerprint.IPinStorage;
import app.com.fingerprintapp.fingerprint.SecureInteractor;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

public class SignUpActivity extends BaseActivity {

    private Button login;
    private EditText newPinCode;
    private Button setupPinCode;

    @Inject
    IPinStorage pinStorage;
    @Inject
    IFingerprintInteractor fingerprintInteractor;
    @Inject
    SecureInteractor secureInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        MyApp.getInstance().getMainAppComponent().inject(this);
        initViews();
    }

    private void initViews() {
        newPinCode = findViewById(R.id.edt_new_pin_code);
        setupPinCode = findViewById(R.id.btn_setup_pin_code);
        login = findViewById(R.id.btn_login);

        setupPinCode.setOnClickListener(v -> savePinCode());
        login.setOnClickListener(v -> openMainScreen());
    }

    private void openMainScreen() {
        if (pinStorage.hasPinCode()) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Please, setup pin code", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFingerprintState();
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

    @TargetApi(Build.VERSION_CODES.M)
    private void savePinCode() {
        if (secureInteractor.checkSensorState()) {
            if (!pinStorage.hasPinCode()) {
                final String pinCode = newPinCode.getText().toString().trim();
                final String cryptoPinCode = secureInteractor.encryptString(pinCode);
                pinStorage.saveEncryptedPinCode(cryptoPinCode);
                Toast.makeText(this, "Setup pin code action DONE", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "you have the pin code installed", Toast.LENGTH_LONG).show();
            }
        }
    }
}
