package com.sanket_satpute_20.playme.project.extra_stuffes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.sanket_satpute_20.playme.project.model.MusicFiles;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CacheImageManager {

    public static String getFileName;
    private static final String CACHE_ADDRESS = "/Android/data/com.sanket_satpute_20.playme/cache/";

    public static Bitmap getImage(Context context, MusicFiles mFile) {
        getFileName = context.getCacheDir()+"/"+mFile.getTitle();
        File file = new File(getFileName);
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

    public static Bitmap getImage(Context context, String path) {
        String second_path = context.getCacheDir()+"/"+path;
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

    public static void putImage(Context context, MusicFiles mFile, Bitmap bitmap) {
        String fileName = context.getCacheDir() + "/" +mFile.getTitle();
        File file = new File(fileName);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
        } catch (FileNotFoundException e) {
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
    }

    public static void putImage(Context context, String path, Bitmap bitmap) {
        String second_path = context.getCacheDir()+"/"+path;
        File file = new File(second_path);
        Log.d("lgv", second_path);
        FileOutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, outputStream);
            Log.d("lgv", "Image Saved");
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
    }
}
