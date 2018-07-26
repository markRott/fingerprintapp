package app.com.fingerprintapp.ui;

import android.os.Bundle;
import android.widget.TextView;

import javax.inject.Inject;

import app.com.fingerprintapp.MyApp;
import app.com.fingerprintapp.R;
import app.com.fingerprintapp.fingerprint.IFingerprintInteractor;
import app.com.fingerprintapp.fingerprint.model.FingerprintEventData;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;

public class MainActivity extends BaseActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private TextView tvPinCode;

    @Inject
    IFingerprintInteractor fingerprintInteractor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MyApp.getInstance().getMainAppComponent().inject(this);

        setContentView(R.layout.activity_main);
        tvPinCode = findViewById(R.id.tv_pin_code);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startFingerprintAuthentication();
    }

    private void startFingerprintAuthentication() {
        addDisposable(
                fingerprintInteractor
                        .startFingerprint()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeWith(new DisposableObserver<FingerprintEventData>() {

                            @Override
                            public void onError(Throwable e) {
                                System.out.println("startFingerprintAuthentication error = " + e);
                            }

                            @Override
                            public void onNext(FingerprintEventData fingerprintEventData) {
                                handleFingerprintEventData(fingerprintEventData);
                            }

                            @Override
                            public void onComplete() {
                                System.out.println("startFingerprintAuthentication onComplete");
                            }
                        }));
    }

    private void handleFingerprintEventData(FingerprintEventData fingerprintEventData) {
        switch (fingerprintEventData.getFingerprintEventType()) {
            case SUCCESS:
                tvPinCode.setText(fingerprintEventData.getMessage());
                break;
            case HINT:
                tvPinCode.setText(fingerprintEventData.getMessage());
                break;
            case FAIL:
                tvPinCode.setText(fingerprintEventData.getMessage());
                break;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearDispose();
        fingerprintInteractor.stopFingerPrint();
    }
}
