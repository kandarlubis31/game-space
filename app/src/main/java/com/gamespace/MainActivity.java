package com.gamespace;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.gamespace.repository.AppRepository;
import com.gamespace.repository.GameModeCallback;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    private AppRepository repository;
    private boolean isGameSpaceActive = false;
    private Button btnToggle;
    private ProgressBar progressBar;
    private TextView txtStatus;
    private GridView gridApps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        repository = new AppRepository(this);
        btnToggle = findViewById(R.id.btnToggle);
        progressBar = findViewById(R.id.progressBar);
        txtStatus = findViewById(R.id.txtStatus);
        gridApps = findViewById(R.id.gridApps);

        loadAllowedApps();

        btnToggle.setOnClickListener(v -> {
            if (isGameSpaceActive) {
                stopGameSpace();
            } else {
                startGameSpace();
            }
        });
    }

    private void loadAllowedApps() {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> allApps = pm.getInstalledApplications(0);
        List<ApplicationInfo> allowedApps = new ArrayList<>();

        for (ApplicationInfo app : allApps) {
            if (pm.getLaunchIntentForPackage(app.packageName) != null) {
                allowedApps.add(app);
            }
        }

        AppAdapter adapter = new AppAdapter(this, allowedApps);
        gridApps.setAdapter(adapter);
        
        gridApps.setOnItemClickListener((parent, view, position, id) -> {
            ApplicationInfo app = allowedApps.get(position);
            Intent launchIntent = pm.getLaunchIntentForPackage(app.packageName);
            if (launchIntent != null) {
                startActivity(launchIntent);
            }
        });
    }

    private void startGameSpace() {
        btnToggle.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        txtStatus.setText("Menyiapkan Game Space...");

        repository.enableGameMode(new GameModeCallback() {
            @Override
            public void onProgress(int current, int total, String appName) {
                runOnUiThread(() -> {
                    progressBar.setMax(total);
                    progressBar.setProgress(current);
                    txtStatus.setText("Suspend: " + appName);
                });
            }

            @Override
            public void onResult(int totalProcessed, String error) {
                runOnUiThread(() -> {
                    btnToggle.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    if (error == null) {
                        isGameSpaceActive = true;
                        btnToggle.setText("STOP GAME SPACE");
                        btnToggle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFFF44336));
                        txtStatus.setText("Game Space Aktif!");
                    } else {
                        txtStatus.setText("Error: " + error);
                    }
                });
            }
        });
    }

    private void stopGameSpace() {
        btnToggle.setEnabled(false);
        progressBar.setVisibility(View.VISIBLE);
        txtStatus.setText("Memulihkan sistem...");

        repository.disableGameMode(new GameModeCallback() {
            @Override
            public void onProgress(int current, int total, String appName) {
                runOnUiThread(() -> {
                    progressBar.setMax(total);
                    progressBar.setProgress(current);
                    txtStatus.setText("Unsuspend: " + appName);
                });
            }

            @Override
            public void onResult(int totalProcessed, String error) {
                runOnUiThread(() -> {
                    btnToggle.setEnabled(true);
                    progressBar.setVisibility(View.GONE);
                    if (error == null) {
                        isGameSpaceActive = false;
                        btnToggle.setText("START GAME SPACE");
                        btnToggle.setBackgroundTintList(android.content.res.ColorStateList.valueOf(0xFF4CAF50));
                        txtStatus.setText("Sistem Normal.");
                    } else {
                        txtStatus.setText("Error: " + error);
                    }
                });
            }
        });
    }
}
