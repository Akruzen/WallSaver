package com.akruzen.wallpapersaver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;

public class MainActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_READ_STORAGE_PERMISSION = 2;
    MaterialCardView permissionCardView;

    public void aboutPressed(View view) {
        startActivity(new Intent(this, AboutActivity.class));
    }

    public void grantPermissionTapped(View view) {
        if (isPermissionGranted()) {
            Toast.makeText(this, "Permission already granted!", Toast.LENGTH_SHORT).show();
        } else {
            askForPermission();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_READ_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setVisibilities();
            }
        }
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

    private void askForPermission() {
        String perm = android.Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            perm = android.Manifest.permission.READ_MEDIA_IMAGES;
        }
        ActivityCompat.requestPermissions(this, new String[]{perm}, REQUEST_CODE_READ_STORAGE_PERMISSION);
    }

    private boolean isPermissionGranted() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED;
        }
        return checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void setVisibilities() {
        if (isPermissionGranted()) {
            permissionCardView.setVisibility(View.GONE);
        }
    }

}