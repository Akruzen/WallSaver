package com.akruzen.wallpapersaver;

import static com.akruzen.wallpapersaver.Common.Constants.REQUEST_CODE_SAVE_FILE;
import static com.akruzen.wallpapersaver.Common.Methods.askForStoragePermission;
import static com.akruzen.wallpapersaver.Common.Methods.getReadyForSavingWallpaper;
import static com.akruzen.wallpapersaver.Common.Methods.isPermissionGranted;
import static com.akruzen.wallpapersaver.Common.Methods.writeWallpaperFile;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.view.WindowCompat;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;

public class SaveWallpaperActivity extends AppCompatActivity {

    ImageView backgroundImageView;
    Uri imageUri;
    Bitmap imageBitmap;
    Intent intent;

    private void setActivityFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }

    public void saveWallpaper(View view) {
        getReadyForSavingWallpaper(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ((TextView) findViewById(R.id.PermissionGrantedTV)).setText(getString(R.string.permission_granted));
            System.out.println("Bull: Grant results are true");
        }
    }

    private void setImage() {
        if (intent != null) {
            try {
                findViewById(R.id.saveWallFAB).setVisibility(View.VISIBLE);
                findViewById(R.id.PermissionGrantedTV).setVisibility(View.GONE);
                System.out.println("Bull: inside try of setImage");
                imageUri = intent.getData();
                InputStream inputStream = getContentResolver().openInputStream(imageUri);
                if (inputStream != null) {
                    imageBitmap = BitmapFactory.decodeStream(inputStream);
                    backgroundImageView.setImageBitmap(imageBitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        writeWallpaperFile(this, requestCode, resultCode, data, imageBitmap);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityFullscreen();
        setContentView(R.layout.activity_save_wallpaper);
        // Find view by ID
        backgroundImageView = findViewById(R.id.backgroundImageView);
        intent = getIntent();
        if (!isPermissionGranted(this)) {
            askForStoragePermission(this);
        } else {
            System.out.println("Bull: inside else of isPermissionGranted");
            setImage();
        }
    }
}