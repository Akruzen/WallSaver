package com.akruzen.wallpapersaver;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;
import java.util.Locale;

public class SaveWallpaperActivity extends AppCompatActivity {

    ImageView backgroundImageView;
    Uri imageUri;
    Intent intent;
    private static final int REQUEST_CODE_SAVE_FILE = 1;
    private static final int REQUEST_CODE_READ_STORAGE_PERMISSION = 2;

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
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setImage();
        }
    }

    private void setImage() {
        if (intent != null) {
            backgroundImageView.setImageURI(imageUri);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SAVE_FILE && resultCode == Activity.RESULT_OK) {
            if (data != null && data.getData() != null && imageUri != null) {
                try {
                    Uri selectedFileUri = data.getData();
                    InputStream inputStream = getContentResolver().openInputStream(imageUri);
                    OutputStream outputStream = getContentResolver().openOutputStream(selectedFileUri);
                    if (inputStream != null && outputStream != null) {
                        // Read the image data from inputStream and write it to outputStream.
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = inputStream.read(buffer)) != -1) {
                            outputStream.write(buffer, 0, bytesRead);
                        }
                        outputStream.close();
                        inputStream.close();
                        Toast.makeText(this, "File saved successfully", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        Toast.makeText(this, "IO Stream is null", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException | NullPointerException e) {
                    Toast.makeText(this, "IO Exception", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_wallpaper);
        // Find view by ID
        backgroundImageView = findViewById(R.id.backgroundImageView);
        intent = getIntent();
        if (intent != null) {
            imageUri = intent.getData();
        }
        if (!isPermissionGranted()) {
            askForPermission();
        } else {
            setImage();
        }
    }
}