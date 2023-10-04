package com.akruzen.wallpapersaver;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class SaveWallpaperActivity extends AppCompatActivity {

    ImageView backgroundImageView;
    Uri imageUri;
    private static final int REQUEST_CODE_SAVE_FILE = 1;

    public void saveWallpaper(View view) {
        Intent intent = new Intent(Intent.ACTION_CREATE_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("application/image"); // Set the desired MIME type of the file to save.
        intent.putExtra(Intent.EXTRA_TITLE, "WallSaver.png");
        startActivityForResult(intent, REQUEST_CODE_SAVE_FILE);
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

        Intent intent = getIntent();
        if (intent != null) {
            imageUri = intent.getData();
            backgroundImageView.setImageURI(imageUri);
        }

    }
}