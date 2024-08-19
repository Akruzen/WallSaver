package com.akruzen.wallpapersaver;

import static com.akruzen.wallpapersaver.Common.Methods.askForStoragePermission;
import static com.akruzen.wallpapersaver.Common.Methods.getReadyForSavingWallpaper;
import static com.akruzen.wallpapersaver.Common.Methods.isPermissionGranted;
import static com.akruzen.wallpapersaver.Common.Methods.writeWallpaperFile;

import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.akruzen.wallpapersaver.Common.Methods;
import com.akruzen.wallpapersaver.Interfaces.ClicksHandlerInterface;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

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

    public void applyWallpaper(View view) {
        Map<Integer, ClicksHandlerInterface> map = new HashMap<>();
        map.put(R.id.homeScreenFAB, setInterfaceMethods(WallpaperManager.FLAG_SYSTEM));
        map.put(R.id.lockScreenFAB, setInterfaceMethods(WallpaperManager.FLAG_LOCK));
        AlertDialog dialog = Methods.showMaterialDialog(this, R.layout.material_dialog_template,
                getString(R.string.set_wallpaper), getString(R.string.apply_wallpaper_to), true, map);
        dialog.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            ((TextView) findViewById(R.id.PermissionGrantedTV)).setText(getString(R.string.permission_granted));
        }
    }

    private void setImage() {
        if (intent != null) {
            try {
                findViewById(R.id.actionsLinearLayout).setVisibility(View.VISIBLE);
                findViewById(R.id.PermissionGrantedTV).setVisibility(View.GONE);
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
            setImage();
        }
    }

    private ClicksHandlerInterface setInterfaceMethods (int wallpaperFlag) {
        return new ClicksHandlerInterface() {
            @Override
            public void onClickEvent() {
                try {
                    WallpaperManager wallpaperManager = WallpaperManager.getInstance(getApplicationContext());
                    wallpaperManager.setBitmap(imageBitmap, null, true,
                            wallpaperFlag == WallpaperManager.FLAG_LOCK ? WallpaperManager.FLAG_LOCK : WallpaperManager.FLAG_SYSTEM);
                    Toast.makeText(getApplicationContext(), "Wallpaper applied!", Toast.LENGTH_SHORT).show();
                    finish();
                } catch (IOException e) {
                    Toast.makeText(getApplicationContext(), "Unable to apply wallpaper", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        };
    }
}