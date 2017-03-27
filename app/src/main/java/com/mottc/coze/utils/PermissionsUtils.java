package com.mottc.coze.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;

/**
 * Created with Android Studio
 * User: mottc
 * Date: 2017/3/21
 * Time: 19:48
 */


public class PermissionsUtils {


    private static final int REQUEST_EXTERNAL_STORAGE = 125;
    final public static int REQUEST_CODE_ASK_RECORD_AUDIO = 124;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private static String[] PERMISSIONS_RECORD_AUDIO = {
            Manifest.permission.RECORD_AUDIO
    };

    public static void verifyStoragePermissions(Activity activity) {
// Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity,
                Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE);
        }
    }

    public static void getRecordPermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECORD_AUDIO);
        if (permission != PackageManager.PERMISSION_GRANTED) {
// We don't have permission so prompt the user
            ActivityCompat.requestPermissions(activity, PERMISSIONS_RECORD_AUDIO,
            REQUEST_CODE_ASK_RECORD_AUDIO);
        }

    }


}
