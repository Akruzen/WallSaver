package com.akruzen.wallpapersaver;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;

public class AboutActivity extends AppCompatActivity {

    TextView versionNameTextView;

    public void contactPressed(View view) {
        String uriString = "https://www.linkedin.com/in/omkar-phadke-b97b741ba/";
        if (view.getId() == R.id.githubButton) {
            uriString = "https://github.com/Akruzen";
        } else if (view.getId() == R.id.discordButton) {
            uriString = "https://discordapp.com/users/akruzen#2652";
        } else if (view.getId() == R.id.sourceCodeButton) {
            uriString = "https://github.com/Akruzen/Wallsaver";
        }
        // Else default behaviour will be to open linked in
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
    }

    public void onContactEmailPressed (View view) {
        // Bug report logic
        String emailAddress = "omkarphadke.dev@gmail.com";
        String manufacturer = android.os.Build.MANUFACTURER;
        String model = android.os.Build.MODEL;
        String androidVersion = Build.VERSION.RELEASE;
        String extraTextString = "Hey Omkar,\n\n\n*** Add your content here ***\n\n\n" + "My phone configuration is:\n" + "Manufacturer: " + manufacturer
                + "\nDevice model: " + model + "\nAndroid Version: " + androidVersion;
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:"));
        intent.putExtra(Intent.EXTRA_EMAIL, new String[] {emailAddress}); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_SUBJECT, "Wallsaver Feedback");
        intent.putExtra(Intent.EXTRA_TEXT, extraTextString);
        try {
            startActivity(intent);
        } catch (Exception e) {
            Toast.makeText(this, "Make sure you have at least one default email app set", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    public void changeLogPressed(View view) {
        showChangeLogBottomSheet();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // Find views by id
        versionNameTextView = findViewById(R.id.versionNameTextView);
        // Method Calls
        setUpTextViews();
    }

    private void setUpTextViews() {
        String version = "Version: Unknown";
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            version = "Version: " + pInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        } finally {
            versionNameTextView.setText(version);
        }
    }

    private void showChangeLogBottomSheet() {
        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(R.layout.changelog_bottom_sheet);
        bottomSheetDialog.show();
    }

}