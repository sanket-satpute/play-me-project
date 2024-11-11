package com.sanket_satpute_20.playme.project.account.extra_stuffes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

public class SaveUserImageCache {

    public static Bitmap getImage(Context context, String path) {
        String second_path = context.getCacheDir() + "/" +path;
        File file = new File(second_path);
        Bitmap bitmap = null;
        if (file.exists()) {
            try {
                bitmap = BitmapFactory.decodeStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                e.getMessage();
            }
        }
        return bitmap;
    }

    public static Uri putImage(Context context, String path, Bitmap bitmap) {
        String second_path = context.getCacheDir() + "/" + path;
        File file = new File(second_path);
        Log.d("lgv", second_path);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            String uriPath = MediaStore.Images.Media.insertImage(context.getContentResolver(), bitmap, "user_profile_image_"
                            + Calendar.getInstance().getTime(),
                    null);
            return Uri.parse(uriPath);
        } catch (FileNotFoundException | NullPointerException e ) {
            e.printStackTrace();
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                } catch (IOException e) {
                    e.getMessage();
                }
            }
        }
        return null;
    }
}
