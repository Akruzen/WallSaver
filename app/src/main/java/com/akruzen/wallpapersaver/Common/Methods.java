package com.akruzen.wallpapersaver.Common;

import static com.akruzen.wallpapersaver.Common.Constants.REQUEST_CODE_SAVE_FILE;
import static com.akruzen.wallpapersaver.Common.Constants.REQUEST_CODE_STORAGE_PERMISSION;
import static com.akruzen.wallpapersaver.Common.Constants.REQUEST_MANAGE_STORAGE_PERMISSION;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.app.ActivityCompat;

import com.akruzen.wallpapersaver.Interfaces.ClicksHandlerInterface;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;

import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;
import java.util.Map;


public class Methods {

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

    // Get Material Extended Floating Action Button
    public static ExtendedFloatingActionButton getExtendedFAB(Activity activity, String title) {
        ExtendedFloatingActionButton homeScreenFAB = new ExtendedFloatingActionButton(activity);
        homeScreenFAB.setText(title);
        homeScreenFAB.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
        return homeScreenFAB;
    }

    public static AlertDialog showMaterialDialog(@NonNull Activity activity, @Nullable Integer layoutResourceId,
                                                 @Nullable String title, @Nullable String content, @Nullable Boolean isDismissible,
                                                 @Nullable Map<Integer, ClicksHandlerInterface> viewIdsMappedToMethods)
            throws ClassCastException {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(activity);
        if (layoutResourceId != null) {
            // This means user wants to add views he has passed any
            LayoutInflater inflater = LayoutInflater.from(activity);
            View layoutView = inflater.inflate(layoutResourceId, null);
            if (viewIdsMappedToMethods != null) {
                // Traverse through map and apply interface methods to views
                for (Map.Entry<Integer, ClicksHandlerInterface> entry : viewIdsMappedToMethods.entrySet()) {
                    View view = layoutView.findViewById(entry.getKey());
                    view.setOnClickListener(v -> entry.getValue().onClickEvent());
                }
            }
            dialogBuilder.setView(layoutView);
        }
        if (title != null) dialogBuilder.setTitle(title);
        if (content != null) dialogBuilder.setMessage(content);
        dialogBuilder.setCancelable(isDismissible != null && isDismissible);
        return dialogBuilder.create();
    }
    @NonNull
    public static AlertDialog showMaterialDialog(@NonNull Activity activity, @NonNull String title, @NonNull String content,
                                                 @Nullable Pair<@NotNull String, @org.jetbrains.annotations.Nullable ClicksHandlerInterface> onPositiveClick,
                                                 @Nullable Pair<@NotNull String, @org.jetbrains.annotations.Nullable ClicksHandlerInterface> onNegativeClick,
                                                 @Nullable Pair<@NotNull String, @org.jetbrains.annotations.Nullable ClicksHandlerInterface> onNeutralClick) {
        MaterialAlertDialogBuilder dialogBuilder = new MaterialAlertDialogBuilder(activity);
        dialogBuilder.setTitle(title);
        dialogBuilder.setMessage(content);
        dialogBuilder.setCancelable(true);
        if (onPositiveClick != null) {
            dialogBuilder.setPositiveButton(onPositiveClick.first,
                    (dialog, which) -> {
                        if (onPositiveClick.second != null) onPositiveClick.second.onClickEvent();
                        dialog.dismiss();
                    });
        }
        if (onNegativeClick != null) {
            dialogBuilder.setNegativeButton(onNegativeClick.first,
                    (dialog, which) -> {
                        if (onNegativeClick.second != null) onNegativeClick.second.onClickEvent();
                        dialog.dismiss();
                    });
        }
        if (onNeutralClick != null) {
            dialogBuilder.setNeutralButton(onNeutralClick.first,
                    (dialog, which) -> {
                        if (onNeutralClick.second != null) onNeutralClick.second.onClickEvent();
                        dialog.dismiss();
                    });
        }
        return dialogBuilder.create();
    }

}
