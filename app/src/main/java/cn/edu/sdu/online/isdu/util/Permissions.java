package cn.edu.sdu.online.isdu.util;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 ****************************************************
 * @author zsj
 * Last Modifier: ZSJ
 * Last Modify Time: 2018/7/10
 *
 * 记录和管理APP权限
 ****************************************************
 */
public class Permissions {

    public static final String VIBRATE = Manifest.permission.VIBRATE;
    public static final String INTERNET = Manifest.permission.INTERNET;
    public static final String CAMERA = Manifest.permission.CAMERA;
    public static final String READ_EXTERNAL_STORAGE = Manifest.permission.READ_EXTERNAL_STORAGE;
    public static final String WRITE_EXTERNAL_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;

    private static final String[] permissions = new String[] {VIBRATE, INTERNET,
            READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE};

    public static void requestPermission(Activity activity, String permission) {
        if (ContextCompat.checkSelfPermission(activity, permission) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity,
                    new String[]{permission}, 2);
        }
    }

    public static void requestAllPermissions(Activity activity) {
        ActivityCompat.requestPermissions(activity, permissions, 3);
    }

}
