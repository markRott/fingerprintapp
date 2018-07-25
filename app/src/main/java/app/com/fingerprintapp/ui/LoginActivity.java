package app.com.fingerprintapp.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import app.com.fingerprintapp.R;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private EditText pinCode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        pinCode = findViewById(R.id.edt_pin_code);
        login = findViewById(R.id.btn_login);

        login.setOnClickListener(v -> {

        });
    }
}
