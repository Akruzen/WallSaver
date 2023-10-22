package com.akruzen.wallpapersaver;

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
    private static final int REQUEST_CODE_SAVE_FILE = 1;
    private static final int REQUEST_CODE_READ_STORAGE_PERMISSION = 2;

    private void setActivityFullscreen() {
        WindowCompat.setDecorFitsSystemWindows(getWindow(), false);
    }

    private void askForPermission() {
        String perm = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perm = Manifest.permission.READ_MEDIA_IMAGES;
        }
        ActivityCompat.requestPermissions(this, new String[]{perm}, REQUEST_CODE_READ_STORAGE_PERMISSION);
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                System.out.println("Bull: isPermissionGranted is true");
                return true;
            }
            Toast.makeText(this, "Read Permission Required", Toast.LENGTH_SHORT).show();
        } else {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            }
            Toast.makeText(this, "Read Permission Required", Toast.LENGTH_SHORT).show();
        }
        return false;
    }

    public void saveWallpaper(View view) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault());
        String currentDateAndTime = sdf.format(new Date());
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/image"); // Set the desired MIME type of the file to save.
        intent.putExtra(Intent.EXTRA_TITLE, currentDateAndTime + ".png");
        startActivityForResult(intent, REQUEST_CODE_SAVE_FILE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        System.out.println("Bull: inside onRequestPermissionsResult");
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

        if (requestCode == REQUEST_CODE_SAVE_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null && imageUri != null) {
                try {
                    Uri selectedFileUri = data.getData();
                    OutputStream outputStream = getContentResolver().openOutputStream(selectedFileUri);
                    imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                    Toast.makeText(this, "Save Successful", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (IOException | NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setActivityFullscreen();
        setContentView(R.layout.activity_save_wallpaper);
        // Find view by ID
        backgroundImageView = findViewById(R.id.backgroundImageView);
        intent = getIntent();
        if (!isPermissionGranted()) {
            askForPermission();
        } else {
            System.out.println("Bull: inside else of isPermissionGranted");
            setImage();
        }
    }
}