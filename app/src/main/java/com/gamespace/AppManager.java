package com.gamespace;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import java.io.*;
import java.util.*;
import rikka.shizuku.Shizuku;

public class AppManager {
    private String whitelistPath;
    private String disabledLogPath;
    private PackageManager pm;
    private String packageName;

    private static final String[] FATAL_SYSTEM = {
        "android", "android.ext.services", "com.android.bluetooth",
        "com.android.systemui", "com.android.phone", "com.android.settings",
        "com.google.android.gms", "com.google.android.webview",
        "com.android.permissioncontroller", "com.android.shell"
    };

    public AppManager(Context context) {
        pm = context.getPackageManager();
        packageName = context.getPackageName();
        whitelistPath = new File(context.getFilesDir(), "whitelist.txt").getAbsolutePath();
        disabledLogPath = new File(context.getFilesDir(), "disabled.txt").getAbsolutePath();
    }

    public List<AppItem> getAllApps() {
        List<AppItem> apps = new ArrayList<>();
        try {
            List<ApplicationInfo> infos = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo info : infos) {
                if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                    apps.add(new AppItem(info.loadLabel(pm).toString(), info.packageName));
                }
            }
            Collections.sort(apps);
        } catch (Exception e) {}
        return apps;
    }

    public List<String> getAllPackages() {
        List<String> apps = new ArrayList<>();
        try {
            List<ApplicationInfo> infos = pm.getInstalledApplications(PackageManager.GET_META_DATA);
            for (ApplicationInfo info : infos) {
                apps.add(info.packageName);
            }
        } catch (Exception e) {}
        return apps;
    }

    public List<String> loadWhitelist() {
        List<String> list = new ArrayList<>();
        try {
            File f = new File(whitelistPath);
            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) list.add(line.trim());
                }
                br.close();
            } else {
                Collections.addAll(list, FATAL_SYSTEM);
            }
            list.add(packageName);
            list.add("moe.shizuku.privileged.api");
            saveWhitelist(new HashSet<>(list));
        } catch (Exception e) {}
        return list;
    }

    public void saveWhitelist(Set<String> list) {
        try {
            FileWriter fw = new FileWriter(whitelistPath);
            for (String s : list) {
                fw.write(s + "\n");
            }
            fw.close();
        } catch (Exception e) {}
    }

    public void addToWhitelist(String pkg) {
        Set<String> list = new HashSet<>(loadWhitelist());
        list.add(pkg);
        saveWhitelist(list);
    }

    public List<String> loadDisabledLog() {
        List<String> list = new ArrayList<>();
        try {
            File f = new File(disabledLogPath);
            if (f.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(f));
                String line;
                while ((line = br.readLine()) != null) {
                    if (!line.trim().isEmpty()) list.add(line.trim());
                }
                br.close();
            }
        } catch (Exception e) {}
        return list;
    }

    public void saveDisabledLog(List<String> list) {
        try {
            FileWriter fw = new FileWriter(disabledLogPath);
            for (String s : list) {
                fw.write(s + "\n");
            }
            fw.close();
        } catch (Exception e) {}
    }

    public void clearDisabledLog() {
        new File(disabledLogPath).delete();
    }

    public boolean isGameModeOn() {
        return new File(disabledLogPath).exists();
    }

    public int getFatalSystemCount() {
        return FATAL_SYSTEM.length;
    }

    public void executeDisable(List<String> apps) throws Exception {
        for (String app : apps) {
            String[] cmd = {"pm", "disable-user", "--user", "0", app};
            Shizuku.newProcess(cmd, null, null).waitFor();
        }
    }

    public void executeEnable(List<String> apps) throws Exception {
        for (String app : apps) {
            String[] cmd = {"pm", "enable", app};
            Shizuku.newProcess(cmd, null, null).waitFor();
        }
    }
}