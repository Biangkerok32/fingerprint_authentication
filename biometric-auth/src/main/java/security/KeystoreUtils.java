package security;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

public class KeystoreUtils {

    public static final String MY_PREFERENCES = "MY_PREFERENCES";
    private static final String PREF_FINGERPRINT_DATA = "PREF_FINGERPRINT_DATA";
    private static final String PREF_FINGERPRINT_IV = "PREF_FINGERPRINT_IV";
    private static final String PREF_FINGERPRINT_ENABLE = "PREF_FINGERPRINT_ENABLE";

    private static SharedPreferences sharedPreferences;

    public static void saveCredentialForFingerprint(Context context, String dataToSave) {
        if (context != null) {
            try {
                sharedPreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
                EnCryptor encryptor = new EnCryptor();
                byte[] encryptedText = encryptor.encryptText(dataToSave);

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(PREF_FINGERPRINT_DATA, Base64.encodeToString(encryptedText, Base64.DEFAULT));
                editor.putString(PREF_FINGERPRINT_IV, Base64.encodeToString(encryptor.getIv(), Base64.DEFAULT));
                editor.putBoolean(PREF_FINGERPRINT_ENABLE, true);
                editor.apply();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void deleteSavedData(Context context) {
        if (context != null) {
            sharedPreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PREF_FINGERPRINT_DATA, "");
            editor.putString(PREF_FINGERPRINT_IV, "");
            editor.putBoolean(PREF_FINGERPRINT_ENABLE, false);
            editor.apply();
        }
    }

    public static String[] fetchCredentialForFingerprint(Context context) {
        try {
            DeCryptor decryptor = new DeCryptor();
            sharedPreferences = context.getSharedPreferences(MY_PREFERENCES, Context.MODE_PRIVATE);
            byte[] touchIdDataPref = Base64.decode(sharedPreferences.getString(PREF_FINGERPRINT_DATA, ""), Base64.DEFAULT);
            byte[] touchIdDataIvPref = Base64.decode(sharedPreferences.getString(PREF_FINGERPRINT_IV, ""), Base64.DEFAULT);
            String decryptedText = decryptor.decryptData(touchIdDataPref, touchIdDataIvPref);
            if (decryptedText != null && !decryptedText.isEmpty()) {
                String values[] = decryptedText.split(",");
                return values;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
