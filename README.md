## Authenticate or login user into Android application by fingerprint sensor

FingerPrint supports comes in android from version 6.0 (Marshmallow) to higher versions. Officially Google gave two options for use fingerprint authentication for android application i.e.
1.	FingerprintManager
    Contains in package 'android.hardware.fingerprint.FingerprintManager'
    Added in API level 23
    Depricated in API level 28
    
2.	BiometricPrompt
    Contains in package 'android.hardware.biometrics.BiometricPrompt'
    Added in API level 28
    
### Does it mean developers have to implement FingerprintManager for users who have Android devices running versions below Android P and BiometricPromt for from P?
Ans is No

    
Before going forward we should know little bit about androidX here.

# What is androidx ?
AndroidX is a major improvement to the original Android Support Library, which is no longer maintained. androidx packages fully replace the Support Library by providing feature parity and new libraries.

In addition, androidx includes the following features:

1.	All packages in AndroidX live in a consistent namespace starting with the string androidx. The Support Library packages       have been mapped into corresponding android.* packages. For a full mapping of all the old classes and build artifacts to       the new ones, see the Package Refactoring page.
2.	Unlike the Support Library, androidx packages are separately maintained and updated. The androidx packages use strict         Semantic Versioning, starting with version 1.0.0. You can update AndroidX libraries in your project independently.
3.	Version 28.0.0 is the last release of the Support Library. There will be no more android.support library releases. All new     feature development will be in the androidx namespace.


### Now comes to the point again
BiometricPromt class is also available in that androidx that package is androidx.biometric.BiometricPrompt. for this we need to implement a dependency that will work for all the android version from marshmallow to higher.

dependencies {
    implementation "androidx.biometric:biometric:1.0.0-beta"
}

### Drawbacks of using biometric from the androidx package.
1. Not available stable release
2. Need to migrate all supported library to android


If you don’t want to use this unstable version or migrate to all supported library to androidx then developers have to implement FingerprintManager for users who have Android devices running versions below Android P and BiometricPromt from version P and higher.

### NOTE
For use FingerprintManager we have to make minimum supported version to 23 if your application current minimum supported version is less than 23 and you don’t want to upgrade then you have to use FingerprintManagerCompact instead of FingerprintManager that fall in androidx and you have to migrate all the supported library to androidx but fingerprint support will be available only from Marshmallow (6.0) users.

### Where and How we will store user credential : 
In android we have two options for store user credential or any other sensitive information.

### Keychain (system-wide credentials) 
Use the KeyChain API when you want system-wide credentials. When an app requests the use of any credential through the KeyChain API, users get to choose, through a system-provided UI, which of the installed credentials an app can access. This allows several apps to use the same set of credentials with user consent.

### Keystore (Individual Application store)
Use the Android Keystore provider to let an individual app store its own credentials that only the app itself can access. This provides a way for apps to manage credentials that are usable only by itself while providing the same security benefits that the KeyChain API provides for system-wide credentials. This method requires no user interaction to select the credentials.

We used keystore and store credential in application only

### Steps to implement biometric-auth module into your project
1. Import biometric-auth as a module into your project

2. Add dependency into your main project build.gradle file

   implementation project(path: ':biometric-auth')

3. Create BiometricManager object with the help of BiometricBuilder which will take title, subtitle, description as param. You    can pass it as per your app requirement.

```
BiometricManager mBiometricManager = new BiometricManager.BiometricBuilder(context)
        .setTitle("Title")
        .setSubtitle("Subtitle")
        .setDescription("description")
        .setNegativeButtonText("Cancel")
        .build();
```

###	Authenticate user by BiometricManager object which we created.

```
mBiometricManager.authenticate(getBiometricCallback());

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
            Log.d(TAG, "onAuthenticationSuccessful");
        }

        @Override
        public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
            Log.d(TAG, "onAuthenticationHelp, helpCode : " + helpCode +",helpString : " + helpString);
        }

        @Override
        public void onAuthenticationError(int errorCode, CharSequence errString) {
            Log.d(TAG, "onAuthenticationError, errorCode : " + errorCode + ",errString : " + errString);
        }
    };
}
```

### Save your credential by keystore to get it again on successful authentication by fingerprint
```
KeystoreUtils.saveCredentialForFingerprint(context, dataToSave);
```
Above method is taking two parameter 1st is context and second is string data which we want to store for fingerprint authentication. You can send more than one string in same param by concat comma, for example

String dataToSave = username + ”,” + password ;
KeystoreUtils.saveCredentialForFingerprint(context, dataToSave);

### Fetch Saved data with the help of keystore
```
String[] values = KeystoreUtils.fetchCredentialForFingerprint(context);
```
You will get saved values in form of string array. 

### Also you can delete saved credential.
```
KeystoreUtils.deleteSavedData(context);
```
