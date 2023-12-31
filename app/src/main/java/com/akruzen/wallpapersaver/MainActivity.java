package com.akruzen.wallpapersaver;

import static com.akruzen.wallpapersaver.Common.Constants.REQUEST_CODE_SAVE_FILE;
import static com.akruzen.wallpapersaver.Common.Constants.REQUEST_CODE_STORAGE_PERMISSION;
import static com.akruzen.wallpapersaver.Common.Constants.REQUEST_MANAGE_STORAGE_PERMISSION;
import static com.akruzen.wallpapersaver.Common.Constants.learnMoreAboutAndroidPermissionUrl;
import static com.akruzen.wallpapersaver.Common.Methods.askForStoragePermission;
import static com.akruzen.wallpapersaver.Common.Methods.isPermissionGranted;
import static com.akruzen.wallpapersaver.Common.Methods.getReadyForSavingWallpaper;
import static com.akruzen.wallpapersaver.Common.Methods.writeWallpaperFile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    MaterialCardView permissionCardView;
    Bitmap wallpaperBitmap;

    public void aboutPressed(View view) {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void learnMoreAndroidPermissionTapped(View view) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(learnMoreAboutAndroidPermissionUrl)));
    }

    public void grantPermissionTapped(View view) {
        if (isPermissionGranted(this)) {
            Toast.makeText(this, "Permission already granted!", Toast.LENGTH_SHORT).show();
        } else {
            askForStoragePermission(this);
        }
    }

    public void saveCurrWallpaperTapped(View view) {
        try {
            boolean permissionChecker;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                permissionChecker = Environment.isExternalStorageManager();
            } else {
                permissionChecker = checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
            }
            if (permissionChecker) {
                final WallpaperManager wallpaperManager = WallpaperManager.getInstance(this);
                final Drawable wallpaperDrawable = wallpaperManager.getDrawable();
                if (wallpaperDrawable instanceof BitmapDrawable) {
                    wallpaperBitmap = ((BitmapDrawable) wallpaperDrawable).getBitmap();
                    getReadyForSavingWallpaper(this);
                } else {
                    // Wallpaper may be of a live wallpaper type
                    Toast.makeText(this, "Current wallpaper image type is incompatible!", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(this, "Storage permission is needed!", Toast.LENGTH_SHORT).show();
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_STORAGE_PERMISSION);
            }
        } catch (SecurityException se) {
            se.printStackTrace();
            Toast.makeText(this, "Permission is not granted!", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION || requestCode == REQUEST_MANAGE_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setVisibilities();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        writeWallpaperFile(this, requestCode, resultCode, data, wallpaperBitmap);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setVisibilities();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Find view by ID
        permissionCardView = findViewById(R.id.permissionCardView);
        // Method calls
        setVisibilities();
    }

    private void setVisibilities() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ((TextView) findViewById(R.id.permissionTextView)).setText(getString(R.string.permission_from_tiramisu));
        }
        if (isPermissionGranted(this)) {
            permissionCardView.setVisibility(View.GONE);
            findViewById(R.id.learnMoreAndroidPermissionTV).setVisibility(View.GONE);
        }
    }

}