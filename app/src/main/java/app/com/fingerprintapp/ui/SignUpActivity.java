package app.com.fingerprintapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;

import javax.inject.Inject;

import app.com.fingerprintapp.MyApp;
import app.com.fingerprintapp.R;
import app.com.fingerprintapp.fingerprint.IFingerprintManager;
import app.com.fingerprintapp.fingerprint.enums.FingerprintSensorState;
import app.com.fingerprintapp.fingerprint.secure.ISecureContract;
import app.com.fingerprintapp.fingerprint.storage.IPinStorage;
import app.com.fingerprintapp.utils.ToastFactory;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.observers.DisposableSingleObserver;

public class SignUpActivity extends BaseActivity {

    public static final int PIN_CODE_LENGTH = 4;
    private static final String TAG = SignUpActivity.class.getSimpleName();

    private EditText newPinCode;
    private Button setupPinCode;

    @Inject
    IPinStorage pinStorage;
    @Inject
    IFingerprintManager fingerprintInteractor;
    @Inject
    ISecureContract secureInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        MyApp.getInstance().getMainAppComponent().inject(this);
        initViews();
        if (pinStorage.hasPinCode()) {
            openMainScreen();
        }
    }

    private void initViews() {
        newPinCode = findViewById(R.id.edt_new_pin_code);
        setupPinCode = findViewById(R.id.btn_setup_pin_code);
        setupPinCode.setOnClickListener(v -> savePinCode());
    }

    private void openMainScreen() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        checkFingerprintState();
    }

    private void checkFingerprintState() {
        Disposable fingerprintDisposable = fingerprintInteractor
                .checkFingerprintSensorState()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableSingleObserver<FingerprintSensorState>() {
                    @Override
                    public void onSuccess(FingerprintSensorState fingerprintSensorState) {
                        handleFingerprintSensorState(fingerprintSensorState);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Fail check fingerprint state", e);
                    }
                });
        addDisposable(fingerprintDisposable);
    }

    private void handleFingerprintSensorState(@NonNull FingerprintSensorState fingerprintSensorState) {
        switch (fingerprintSensorState) {
            case NOT_SUPPORTED:
                ToastFactory.showToast(this, "NOT SUPPORTED FINGERPRINT");
                break;
            case NOT_SET_UP:
                System.out.println("NOT_SET_UP");
                ToastFactory.showToast(this, "NOT SETUP FINGERPRINT");
                break;
            case READY:
                ToastFactory.showToast(this, "READY FINGERPRINT");
                break;
        }
    }

    private void savePinCode() {
        final String pinCode = newPinCode.getText().toString().trim();
        if (TextUtils.isEmpty(pinCode)) {
            ToastFactory.showToast(this, "Please setup pin code");
            return;
        }
        if (pinCode.length() < PIN_CODE_LENGTH) {
            ToastFactory.showToast(this, "Pin code must contain at least 4 digits");
            return;
        }
        if (secureInteractor.checkSensorState() && !pinStorage.hasPinCode()) {
            final String cryptoPinCode = secureInteractor.encryptString(pinCode);
            pinStorage.saveEncryptedPinCode(cryptoPinCode);
            ToastFactory.showToast(this, "Setup pin code action DONE");
            openMainScreen();
        }
    }
}
