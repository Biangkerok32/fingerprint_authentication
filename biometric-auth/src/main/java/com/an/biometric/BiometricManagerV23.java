package com.an.biometric;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;

import androidx.core.hardware.fingerprint.FingerprintManagerCompat;
import androidx.core.os.CancellationSignal;


@TargetApi(Build.VERSION_CODES.M)
public class BiometricManagerV23 {

    private FingerprintManagerCompat.CryptoObject cryptoObject;
    protected Context context;
    protected String title;
    protected String subtitle;
    protected String description;
    protected String negativeButtonText;
    private BiometricDialogV23 biometricDialogV23;
    protected CancellationSignal mCancellationSignalV23 = new CancellationSignal();
    private boolean justAuthenticate = false;

    public void displayBiometricPromptV23(final BiometricCallback biometricCallback) {
        justAuthenticate = true;
        FingerprintManagerCompat fingerprintManagerCompat = FingerprintManagerCompat.from(context);
        fingerprintManagerCompat.authenticate(null, 0, mCancellationSignalV23,
                new FingerprintManagerCompat.AuthenticationCallback() {
                    @Override
                    public void onAuthenticationError(int errMsgId, CharSequence errString) {
                        super.onAuthenticationError(errMsgId, errString);
                        updateStatus(String.valueOf(errString));
                        biometricCallback.onAuthenticationError(errMsgId, errString);
                        if (justAuthenticate) {
                            dismissDialog();
                        } else {
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    dismissDialog();
                                }
                            }, 1500);
                        }
                    }

                    @Override
                    public void onAuthenticationHelp(int helpMsgId, CharSequence helpString) {
                        super.onAuthenticationHelp(helpMsgId, helpString);
                        updateStatus(String.valueOf(helpString));
                        biometricCallback.onAuthenticationHelp(helpMsgId, helpString);
                        justAuthenticate = false;
                    }

                    @Override
                    public void onAuthenticationSucceeded(FingerprintManagerCompat.AuthenticationResult result) {
                        super.onAuthenticationSucceeded(result);
                        dismissDialog();
                        biometricCallback.onAuthenticationSuccessful();
                        justAuthenticate = false;
                    }

                    @Override
                    public void onAuthenticationFailed() {
                        super.onAuthenticationFailed();
                        updateStatus(context.getString(R.string.biometric_failed));
                        biometricCallback.onAuthenticationFailed();
                        justAuthenticate = false;
                    }
                }, null);

        displayBiometricDialog(biometricCallback);
    }

    private void displayBiometricDialog(final BiometricCallback biometricCallback) {
        biometricDialogV23 = new BiometricDialogV23(context, biometricCallback);
        biometricDialogV23.setTitle(title);
        biometricDialogV23.setSubtitle(subtitle);
        biometricDialogV23.setDescription(description);
        biometricDialogV23.setButtonText(negativeButtonText);
        biometricDialogV23.show();
    }


    public void dismissDialog() {
        if (biometricDialogV23 != null) {
            biometricDialogV23.dismiss();
        }
    }

    private void updateStatus(String status) {
        if (biometricDialogV23 != null) {
            biometricDialogV23.updateStatus(status);
        }
    }


}
