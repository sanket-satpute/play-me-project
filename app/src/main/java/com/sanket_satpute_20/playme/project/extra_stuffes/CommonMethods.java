package com.sanket_satpute_20.playme.project.extra_stuffes;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CommonMethods {

    public static final int REQUEST_PERMISSION_DELETE = 201;
    public static final int REQUEST_PERMISSION_DELETE_ALBUM = 202;

    //    using but not functionality
    public static Uri getContentUri(Long id) {
        return Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, String.valueOf(id));
    }

    public static int deleteApi28OrLess(Context context, Uri songUri) {
        try {
            ContentResolver contentResolver = context.getContentResolver();
            return contentResolver.delete(songUri, null, null);
        } catch (Exception e) {
            return -2;
        }
    }

    public static void deleteSongAPI29OrGreater(Context context, ArrayList<Uri> uri) {
        ContentResolver contentResolver = context.getContentResolver();
        List<Uri> uriList = new ArrayList<>();
        for (Uri u: uri) {
            Collections.addAll(uriList, u);
        }

        try {
            PendingIntent pendingIntent = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                pendingIntent = MediaStore.createDeleteRequest(contentResolver, uriList);
            }
            if (pendingIntent != null) {
                ((Activity) context).startIntentSenderForResult(pendingIntent.getIntentSender(), REQUEST_PERMISSION_DELETE, null, 0,
                        0, 0, null);
            } else {
                Toast.makeText(context, "File can't be deleted", Toast.LENGTH_SHORT).show();
            }
        } catch (IntentSender.SendIntentException e) {
            e.printStackTrace();
            Toast.makeText(context, "File can't be deleted", Toast.LENGTH_SHORT).show();
        }
    }

    public static int getCurrentVersion(Context context) {
        int current_version;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            current_version = pInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // Handle the error, maybe set a default value
            current_version = 21;
        }
        return current_version;
    }

    public static String getVersionName(Context context) {
        String versionName;
        try {
            PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            versionName = pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            // Handle the error, maybe set a default value
            versionName = "Not Found";
        }
        return versionName;
    }
}
