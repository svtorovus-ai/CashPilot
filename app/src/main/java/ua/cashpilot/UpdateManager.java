package ua.cashpilot;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.Settings;
import android.widget.Toast;

import androidx.core.content.FileProvider;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Locale;

final class UpdateManager {
    private static final String MANIFEST_URL =
            "https://github.com/svtorovus-ai/CashPilot/releases/latest/download/update.json";
    private static final String PREF_LAST_CHECK = "update_last_check_ms";
    private static final long CHECK_INTERVAL_MS = 6L * 60L * 60L * 1000L;

    private UpdateManager() {}

    static void checkAndPrompt(Activity activity, boolean interactive) {
        if (activity.isFinishing()) return;
        SharedPreferences prefs = activity.getSharedPreferences(CashPilotWidget.PREFS, 0);
        long now = System.currentTimeMillis();
        if (!interactive && now - prefs.getLong(PREF_LAST_CHECK, 0L) < CHECK_INTERVAL_MS) return;
        prefs.edit().putLong(PREF_LAST_CHECK, now).apply();

        new Thread(() -> {
            UpdateInfo info = null;
            Exception failure = null;
            try {
                info = fetchManifest();
            } catch (Exception e) {
                failure = e;
            }
            UpdateInfo result = info;
            Exception error = failure;
            activity.runOnUiThread(() -> {
                if (activity.isFinishing()) return;
                if (result == null || result.versionCode <= installedVersionCode(activity)) {
                    if (interactive) Toast.makeText(activity, "Оновлень немає", Toast.LENGTH_SHORT).show();
                    return;
                }
                showUpdateDialog(activity, result);
            });
        }, "CashPilot-update-check").start();
    }

    private static int installedVersionCode(Context context) {
        try {
            android.content.pm.PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
            return info.versionCode;
        } catch (Exception ignored) {
            return Integer.MAX_VALUE;
        }
    }

    private static UpdateInfo fetchManifest() throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(MANIFEST_URL).openConnection();
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(15000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");
        connection.setRequestProperty("Cache-Control", "no-cache");
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IllegalStateException("Update manifest HTTP " + connection.getResponseCode());
        }
        StringBuilder body = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                connection.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) body.append(line);
        } finally {
            connection.disconnect();
        }
        JSONObject json = new JSONObject(body.toString());
        int versionCode = json.getInt("versionCode");
        String versionName = json.optString("versionName", String.valueOf(versionCode));
        String apkUrl = json.getString("apkUrl");
        String sha256 = json.optString("sha256", "").trim().toLowerCase(Locale.US);
        if (versionCode <= 0 || apkUrl.trim().isEmpty()) throw new IllegalStateException("Invalid update manifest");
        return new UpdateInfo(versionCode, versionName, apkUrl, sha256);
    }

    private static void showUpdateDialog(Activity activity, UpdateInfo info) {
        new android.app.AlertDialog.Builder(activity)
                .setTitle("Доступне оновлення CashPilot")
                .setMessage("Нова версія " + info.versionName + " знайдена автоматично. Завантажити та встановити її зараз?")
                .setNegativeButton("Пізніше", null)
                .setPositiveButton("Оновити", (dialog, which) -> downloadAndInstall(activity, info))
                .show();
    }

    private static void downloadAndInstall(Activity activity, UpdateInfo info) {
        Toast.makeText(activity, "Завантаження оновлення…", Toast.LENGTH_SHORT).show();
        new Thread(() -> {
            File apk = null;
            Exception failure = null;
            try {
                File directory = activity.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                if (directory == null) throw new IllegalStateException("No app download directory");
                if (!directory.exists() && !directory.mkdirs()) throw new IllegalStateException("Cannot create download directory");
                apk = new File(directory, "CashPilot-update.apk");
                File partial = new File(directory, "CashPilot-update.apk.part");
                download(info.apkUrl, partial);
                if (!info.sha256.isEmpty() && !info.sha256.equals(sha256(partial))) {
                    throw new SecurityException("Update checksum mismatch");
                }
                if (apk.exists() && !apk.delete()) throw new IllegalStateException("Cannot replace old update");
                if (!partial.renameTo(apk)) throw new IllegalStateException("Cannot finalize update");
            } catch (Exception e) {
                failure = e;
                if (apk != null) apk.delete();
            }
            File result = apk;
            Exception error = failure;
            activity.runOnUiThread(() -> {
                if (activity.isFinishing()) return;
                if (error != null || result == null) {
                    Toast.makeText(activity, "Не вдалося завантажити оновлення", Toast.LENGTH_LONG).show();
                    return;
                }
                install(activity, result);
            });
        }, "CashPilot-update-download").start();
    }

    private static void download(String address, File target) throws Exception {
        HttpURLConnection connection = (HttpURLConnection) new URL(address).openConnection();
        connection.setConnectTimeout(15000);
        connection.setReadTimeout(30000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.android.package-archive");
        if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
            throw new IllegalStateException("APK HTTP " + connection.getResponseCode());
        }
        try (BufferedInputStream input = new BufferedInputStream(connection.getInputStream());
             FileOutputStream output = new FileOutputStream(target)) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) != -1) output.write(buffer, 0, count);
        } finally {
            connection.disconnect();
        }
    }

    private static String sha256(File file) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        try (FileInputStream input = new FileInputStream(file)) {
            byte[] buffer = new byte[8192];
            int count;
            while ((count = input.read(buffer)) != -1) digest.update(buffer, 0, count);
        }
        StringBuilder result = new StringBuilder();
        for (byte value : digest.digest()) result.append(String.format(Locale.US, "%02x", value));
        return result.toString();
    }

    private static void install(Activity activity, File apk) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O &&
                !activity.getPackageManager().canRequestPackageInstalls()) {
            Toast.makeText(activity, "Дозволь встановлення з цього джерела й натисни «Оновити» ще раз", Toast.LENGTH_LONG).show();
            Intent settings = new Intent(Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:" + activity.getPackageName()));
            activity.startActivity(settings);
            return;
        }
        Uri uri = FileProvider.getUriForFile(activity, activity.getPackageName() + ".fileprovider", apk);
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setDataAndType(uri, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(install);
    }

    private static final class UpdateInfo {
        final int versionCode;
        final String versionName;
        final String apkUrl;
        final String sha256;

        UpdateInfo(int versionCode, String versionName, String apkUrl, String sha256) {
            this.versionCode = versionCode;
            this.versionName = versionName;
            this.apkUrl = apkUrl;
            this.sha256 = sha256;
        }
    }
}
