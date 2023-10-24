package com.akruzen.wallpapersaver.Common;

import static com.akruzen.wallpapersaver.Common.Constants.REQUEST_CODE_SAVE_FILE;
import static com.akruzen.wallpapersaver.Common.Constants.REQUEST_CODE_STORAGE_PERMISSION;
import static com.akruzen.wallpapersaver.Common.Constants.REQUEST_MANAGE_STORAGE_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;


public class Methods {

    public static boolean isTiramisuOrHigher() {
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU;
    }

    public static void askForStoragePermission(Activity activity) {
        String perm = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perm = Manifest.permission.READ_MEDIA_IMAGES;
            try {
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION);
                Uri uri = Uri.fromParts("package", activity.getPackageName(), null);
                intent.setData(uri);
                activity.startActivityForResult(intent, REQUEST_MANAGE_STORAGE_PERMISSION);
            }catch (Exception e){
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_MANAGE_ALL_FILES_ACCESS_PERMISSION);
                activity.startActivityForResult(intent, REQUEST_MANAGE_STORAGE_PERMISSION);
            }
        }
        ActivityCompat.requestPermissions(activity, new String[]{perm}, REQUEST_CODE_STORAGE_PERMISSION);
    }

    public static boolean isPermissionGranted(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return Environment.isExternalStorageManager() &&
                    (activity.checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED);
        }
        return activity.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void getReadyForSavingWallpaper(Activity activity) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/image"); // Set the desired MIME type of the file to save.
        intent.putExtra(Intent.EXTRA_TITLE, currentDateAndTime + ".png");
        activity.startActivityForResult(intent, REQUEST_CODE_SAVE_FILE);
    }

    public static void writeWallpaperFile(Activity activity, int requestCode, int resultCode, @Nullable Intent data, Bitmap wallpaperBitmap) {
        if (requestCode == REQUEST_CODE_SAVE_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null) {
                try {
                    Uri selectedFileUri = data.getData();
                    OutputStream outputStream = activity.getContentResolver().openOutputStream(selectedFileUri);
                    wallpaperBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    Toast.makeText(activity, "Save Successful!", Toast.LENGTH_SHORT).show();
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                    Toast.makeText(activity, "Error saving the file!", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

}
