package com.example.biometricsample;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.an.biometric.BiometricCallback;
import com.an.biometric.BiometricManager;

import security.KeystoreUtils;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    EditText userNameEt, passwordEt;
    Button saveBtn, fetchBtn, deleteBtn;
    TextView fetchDataTv;
    Context context;
    String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;

        userNameEt = (EditText) findViewById(R.id.user_name_et);
        passwordEt = (EditText) findViewById(R.id.password_et);
        saveBtn = (Button) findViewById(R.id.save_button);
        saveBtn.setOnClickListener(this);
        deleteBtn = (Button) findViewById(R.id.delete_btn);
        deleteBtn.setOnClickListener(this);
        fetchBtn = (Button) findViewById(R.id.fetch_button);
        fetchBtn.setOnClickListener(this);
        fetchDataTv = (TextView) findViewById(R.id.fetch_data_tv);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.save_button:
                String dataToSave = userNameEt.getText().toString() + "," + passwordEt.getText().toString();
                KeystoreUtils.saveCredentialForFingerprint(context, dataToSave);
                break;

            case R.id.fetch_button:
                BiometricManager mBiometricManager = new BiometricManager.BiometricBuilder(context)
                        .setTitle("Title")
                        .setSubtitle("Subtitle")
                        .setDescription("description")
                        .setNegativeButtonText("Cancel")
                        .build();

                //start authentication
                mBiometricManager.authenticate(getBiometricCallback());
                break;

            case R.id.delete_btn:
                KeystoreUtils.deleteSavedData(context);
                break;
        }
    }

    private BiometricCallback getBiometricCallback() {
        return new BiometricCallback() {
            @Override
            public void onSdkVersionNotSupported() {
                Log.d(TAG, "onSdkVersionNotSupported");
            }

            @Override
            public void onBiometricAuthenticationNotSupported() {
                Log.d(TAG, "onBiometricAuthenticationNotSupported");
            }

            @Override
            public void onBiometricAuthenticationNotAvailable() {
                Log.d(TAG, "onBiometricAuthenticationNotAvailable");
            }

            @Override
            public void onBiometricAuthenticationPermissionNotGranted() {
                Log.d(TAG, "onBiometricAuthenticationPermissionNotGranted");
            }

            @Override
            public void onBiometricAuthenticationInternalError(String error) {
                Log.d(TAG, "onBiometricAuthenticationInternalError");
            }

            @Override
            public void onAuthenticationFailed() {
                Log.d(TAG, "onAuthenticationFailed");
            }

            @Override
            public void onAuthenticationCancelled() {
                Log.d(TAG, "onAuthenticationCancelled");
            }

            @Override
            public void onAuthenticationSuccessful() {
                try {
                    Log.d(TAG, "onAuthenticationSuccessful");
                    String[] values = KeystoreUtils.fetchCredentialForFingerprint(context);
                    if (values != null) {
                        String text = "";
                        for (int i = 0; i < values.length; i++) {
                            text = text + values[i] + "\n";
                        }
                        fetchDataTv.setText(text);
                    } else {
                        fetchDataTv.setText("");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
                Log.d(TAG, "onAuthenticationHelp, helpCode : " + helpCode + ", helpString : " + helpString);
            }

            @Override
            public void onAuthenticationError(int errorCode, CharSequence errString) {
                Log.d(TAG, "onAuthenticationError, errorCode : " + errorCode + ", errString : " + errString);
            }
        };
    }
}
