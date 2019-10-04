package com.example.ema_diary;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.hardware.biometrics.BiometricManager;
import android.os.Build;

import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;
import androidx.core.hardware.fingerprint.FingerprintManagerCompat;

public class BiometricUtils {

    static Context c;

    public BiometricUtils(Context c){
        this.c = c;
    }

    public static boolean isBiometricPromptEnabled() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P);
    }

    public static boolean isSdkVersionSupported() {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M);

    }

    public static int isHardwareSupported(Context context) {

        BiometricManager bio = null;
        FingerprintManagerCompat fm = FingerprintManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            bio = context.getSystemService(BiometricManager.class);
            return  bio.canAuthenticate();
        }
        else{
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if(!fm.isHardwareDetected()){
                    return BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE;
                }
                else if(fm.hasEnrolledFingerprints()){
                    return BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED;
                }
            }
        }
        return BiometricManager.BIOMETRIC_SUCCESS;
    }

    private static void notifyUser(String str, Context c){
        new AlertDialog.Builder(c, R.style.AlertDialogStyle)
                .setTitle("Error")
                .setMessage(str)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Continue with delete operation
                    }
                })
                .show();
    }

    public static boolean isPermissionGranted(Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.USE_FINGERPRINT) ==
                PackageManager.PERMISSION_GRANTED;
    }


}
