package com.gamespace.repository;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import rikka.shizuku.Shizuku;

public class AppRepository {
    private SharedPreferences prefs;
    private PackageManager packageManager;
    private ExecutorService executor = Executors.newFixedThreadPool(4);

    private static final Set<String> FATAL_SYSTEM_APPS = new HashSet<>(Arrays.asList(
            "android",
            "android.ext.services",
            "android.process.media",
            "com.android.bluetooth",
            "com.android.captiveportallogin",
            "com.android.carrierconfig",
            "com.android.cellbroadcastservice",
            "com.android.externalstorage",
            "com.android.keychain",
            "com.android.launcher3",
            "com.android.localtransport",
            "com.android.location.fused",
            "com.android.mms.service",
            "com.android.networkstack",
            "com.android.networkstack.permissionconfig",
            "com.android.networkstack.tethering",
            "com.android.networkstack.tethering.xiaomi_msm8953",
            "com.android.pacprocessor",
            "com.android.permissioncontroller",
            "com.android.phone",
            "com.android.providers.blockednumber",
            "com.android.providers.contacts",
            "com.android.providers.downloads",
            "com.android.providers.media",
            "com.android.providers.media.module",
            "com.android.providers.settings",
            "com.android.providers.telephony",
            "com.android.proxyhandler",
            "com.android.se",
            "com.android.server.telecom",
            "com.android.settings",
            "com.android.shell",
            "com.android.statementservice",
            "com.android.systemui",
            "com.android.wifi.resources",
            "com.android.wifi.resources.xiaomi_msm8953",
            "com.atomicadd.tinylauncher",
            "com.dot.packageinstaller",
            "com.google.android.configupdater",
            "com.google.android.ext.shared",
            "com.google.android.gms",
            "com.google.android.gms.dynamite_cronetdynamite",
            "com.google.android.gms.dynamite_dynamiteloader",
            "com.google.android.gms.dynamite_dynamitemodulesa",
            "com.google.android.gms.dynamite_dynamitemodulesc",
            "com.google.android.gms.dynamite_googlecertificates",
            "com.google.android.gms.dynamite_mapsdynamite",
            "com.google.android.gms.dynamite_measurementdynamite",
            "com.google.android.gms.policy_ads_fdr_dynamite",
            "com.google.android.gms.ui",
            "com.google.android.gsf",
            "com.google.android.ims",
            "com.google.android.safetycore",
            "com.google.android.webview",
            "com.google.process.gapps",
            "com.google.process.gservices",
            "com.magicalstory.MemoryKiller",
            "com.proximabeta.mf.liteuamo",
            "com.qti.dpmserviceapp",
            "com.qti.qualcomm.datastatusnotification",
            "com.qualcomm.embms",
            "com.qualcomm.qcrilmsgtunnel",
            "com.qualcomm.qti.cne",
            "com.qualcomm.qti.ims",
            "com.qualcomm.qti.telephonyservice",
            "com.qualcomm.qti.uceShimService",
            "com.qualcomm.timeservice",
            "moe.shizuku.privileged.api",
            "org.codeaurora.ims",
            "rkr.simplekeyboard.inputmethod",
            "vendor.qti.iwlan",
            "com.proximabeta.mf.uamo",
            "com.ubisoft.rainbowsixmobile.r6.fps.pvp.shooter"
    ));

    public AppRepository(Context context) {
        prefs = context.getSharedPreferences("gamespace_prefs", Context.MODE_PRIVATE);
        packageManager = context.getPackageManager();
    }

    public void enableGameMode(GameModeCallback callback) {
        executor.execute(() -> {
            try {
                List<ApplicationInfo> packages = packageManager.getInstalledApplications(0);
                List<String> suspendedApps = new ArrayList<>();
                int total = packages.size();

                for (int i = 0; i < total; i++) {
                    String pkg = packages.get(i).packageName;
                    callback.onProgress(i + 1, total, pkg);

                    if (FATAL_SYSTEM_APPS.contains(pkg)) {
                        continue;
                    }

                    String[] cmd = {"pm", "suspend", pkg};
                    Process process = Shizuku.newProcess(cmd, null, null);
                    if (process.waitFor() == 0) {
                        suspendedApps.add(pkg);
                    }
                }

                prefs.edit().putString("suspended_apps", TextUtils.join(",", suspendedApps)).apply();
                callback.onResult(suspendedApps.size(), null);
            } catch (Exception e) {
                callback.onResult(0, e.getMessage());
            }
        });
    }

    public void disableGameMode(GameModeCallback callback) {
        executor.execute(() -> {
            try {
                String saved = prefs.getString("suspended_apps", "");
                if (saved.isEmpty()) {
                    callback.onResult(0, null);
                    return;
                }

                String[] toUnsuspend = saved.split(",");
                int total = toUnsuspend.length;

                for (int i = 0; i < total; i++) {
                    String app = toUnsuspend[i];
                    if (!app.isEmpty()) {
                        callback.onProgress(i + 1, total, app);
                        String[] cmd = {"pm", "unsuspend", app};
                        Shizuku.newProcess(cmd, null, null).waitFor();
                    }
                }
                
                prefs.edit().remove("suspended_apps").apply();
                callback.onResult(total, null);
            } catch (Exception e) {
                callback.onResult(0, e.getMessage());
            }
        });
    }
}