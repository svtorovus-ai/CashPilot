package ua.cashpilot;

import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.graphics.Color;
import android.os.*;
import android.widget.RemoteViews;
import org.json.*;
import java.io.*;
import java.net.*;
import java.text.*;
import java.util.*;

public class CashPilotWidget extends AppWidgetProvider {
    static final String PREV = "ua.cashpilot.PREV_MONTH", NEXT = "ua.cashpilot.NEXT_MONTH";
    static final String RESET = "ua.cashpilot.RESET", TOGGLE = "ua.cashpilot.TOGGLE_DAY";
    static final String NO_OP = "ua.cashpilot.NO_OP", PREFS = "cashpilot";
    static final String[] MONTHS = {"Січень","Лютий","Березень","Квітень","Травень","Червень","Липень","Серпень","Вересень","Жовтень","Листопад","Грудень"};

    @Override public void onUpdate(Context c, AppWidgetManager m, int[] ids) {
        for (int id : ids) refresh(c, id, true);
    }
    @Override public void onReceive(Context c, Intent i) {
        super.onReceive(c, i);
        String action = i.getAction();
        if (NO_OP.equals(action)) return;
        int id = i.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        if (id == AppWidgetManager.INVALID_APPWIDGET_ID) return;

        if (PREV.equals(action) || NEXT.equals(action)) {
            String month = monthFor(c, id);
            String nextMonth = shift(month, NEXT.equals(action) ? 1 : -1);
            c.getSharedPreferences(PREFS, 0).edit().putString("month_" + id, nextMonth).apply();
            refresh(c, id, true);
        } else if (RESET.equals(action)) {
            String month = monthFor(c, id);
            c.getSharedPreferences(PREFS, 0).edit().remove(sharedOverrideKey(month)).remove("override_" + id + "_" + month).apply();
            refresh(c, id, true);
        } else if (TOGGLE.equals(action)) {
            String date = i.getStringExtra("date");
            if (date != null) {
                toggle(c, id, date);
                refresh(c, id, false);
            }
        }
    }

    static void refresh(Context c, int id, boolean force) {
        SharedPreferences prefs = c.getSharedPreferences(PREFS, 0);
        String month = monthFor(c, id);
        long requestId = System.nanoTime();
        prefs.edit().putLong("request_" + id, requestId).apply();

        try {
            String cachedRaw = prefs.getString(sharedCacheKey(month), prefs.getString("cache_" + id + "_" + month, null));
            RemoteViews initialV = render(c, id, month, cachedRaw, cachedRaw != null, force);
            AppWidgetManager.getInstance(c).updateAppWidget(id, initialV);
        } catch (Exception ignored) {}

        if (!force) return;

        new Thread(() -> {
            try {
                SharedPreferences p = c.getSharedPreferences(PREFS, 0);
                String baseUrl = p.getString("url", "");
                String raw = fetch(baseUrl, p.getString("token", ""), p.getString("install", ""), month);
                boolean cached = false;
                if (isValidPayload(raw)) {
                    p.edit().putString("cache_" + id + "_" + month, raw).apply();
                    p.edit().putString(sharedCacheKey(month), raw).apply();
                } else {
                    raw = p.getString(sharedCacheKey(month), p.getString("cache_" + id + "_" + month, null));
                    cached = true;
                }

                if (p.getLong("request_" + id, 0L) != requestId || !month.equals(monthFor(c, id))) return;

                RemoteViews v = render(c, id, month, raw, cached, false);
                new Handler(Looper.getMainLooper()).post(() -> AppWidgetManager.getInstance(c).updateAppWidget(id, v));
            } catch (Exception e) {
                try {
                    if (!month.equals(monthFor(c, id))) return;
                    SharedPreferences p = c.getSharedPreferences(PREFS, 0);
                    String raw = p.getString(sharedCacheKey(month), p.getString("cache_" + id + "_" + month, null));
                    RemoteViews v = render(c, id, month, raw, true, false);
                    new Handler(Looper.getMainLooper()).post(() -> AppWidgetManager.getInstance(c).updateAppWidget(id, v));
                } catch (Exception ignored) {}
            }
        }).start();
    }

    static String fetch(String base, String token, String install, String month) {
        String normalizedInstall = normalizeInstall(install);
        if (base.trim().isEmpty() || token.trim().isEmpty() || normalizedInstall.isEmpty()) return null;
        try {
            String q = "/user-stats-data?install_id=" + enc(normalizedInstall) + "&month=" + enc(month);
            HttpURLConnection x = (HttpURLConnection) new URL(base.replaceAll("/$", "") + q).openConnection();
            x.setConnectTimeout(10000); x.setReadTimeout(15000); x.setRequestMethod("GET");
            x.setRequestProperty("Accept", "application/json");
            x.setRequestProperty("X-PITUSHNYA-TOKEN", token);
            x.setDoInput(true);
            if (x.getResponseCode() != 200) return null;
            BufferedReader r = new BufferedReader(new InputStreamReader(x.getInputStream(), "UTF-8"));
            StringBuilder s = new StringBuilder(); String line;
            while ((line = r.readLine()) != null) s.append(line); r.close();
            return s.toString();
        } catch (Exception e) { return null; }
    }

    static String normalizeInstall(String value) {
        if (value == null) return "";
        String s = value.trim();
        s = s.replace("&amp;", "&");
        try {
            if (s.startsWith("http://") || s.startsWith("https://")) {
                URI uri = new URI(s);
                s = queryParameter(uri.getRawQuery(), "install_id");
            } else {
                int i = s.indexOf("install_id=");
                if (i >= 0) { s = s.substring(i + 11); int amp = s.indexOf('&'); if (amp >= 0) s = s.substring(0, amp); }
            }
            // URLDecoder treats '+' as a space; protect the plus in Signal identities.
            return URLDecoder.decode(s.replace("+", "%2B"), "UTF-8").trim();
        } catch (Exception e) { return ""; }
    }

    static String queryParameter(String query, String wanted) {
        if (query == null) return "";
        for (String part : query.split("&")) {
            String[] pair = part.split("=", 2);
            try {
                if (pair.length == 2 && wanted.equals(URLDecoder.decode(pair[0].replace("+", "%2B"), "UTF-8")))
                    return pair[1];
            } catch (Exception ignored) { return ""; }
        }
        return "";
    }

    static boolean isValidPayload(String raw) {
        if (raw == null || raw.trim().isEmpty()) return false;
        try {
            JSONObject root = new JSONObject(raw);
            JSONObject stats = root.optJSONObject("stats");
            String month = stats == null ? "" : stats.optString("month", "");
            JSONArray days = stats == null ? null : stats.optJSONArray("days");
            return root.optBoolean("ok", false) && stats != null && month.matches("\\d{4}-\\d{2}")
                    && days != null && days.length() >= 28;
        }
        catch (Exception e) { return false; }
    }

    static RemoteViews render(Context c, int id, String month, String raw, boolean cached, boolean isUpdating) throws JSONException {
        RemoteViews v = new RemoteViews(c.getPackageName(), R.layout.widget_cashpilot);
        v.setTextViewText(R.id.monthTitle, title(month));
        bindButtons(c, v, id, month);

        JSONObject root = raw == null ? new JSONObject() : new JSONObject(raw);
        JSONObject stats = root.optJSONObject("stats");
        JSONArray daysArray = stats == null ? new JSONArray() : stats.optJSONArray("days");
        JSONObject byDate = new JSONObject();
        if (daysArray != null) {
            for (int i = 0; i < daysArray.length(); i++) {
                JSONObject d = daysArray.getJSONObject(i);
                byDate.put(d.optString("date"), d);
            }
        }
        JSONObject overrides = readOverrides(c, id, month);

        Calendar cal = Calendar.getInstance();
        cal.set(Integer.parseInt(month.substring(0, 4)), Integer.parseInt(month.substring(5, 7)) - 1, 1);
        int first = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7;
        int max = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        int work = 0, duty = 0, vac = 0, idle = 0;

        for (int slot = 0; slot < 42; slot++) {
            int rid = c.getResources().getIdentifier("day" + (slot + 1), "id", c.getPackageName());
            if (rid == 0) continue;
            int day = slot - first + 1;
            if (day < 1 || day > max) {
                v.setTextViewText(rid, "");
                v.setInt(rid, "setBackgroundResource", 0);
                v.setOnClickPendingIntent(rid, null);
                continue;
            }
            String date = month + String.format(Locale.US, "-%02d", day);
            JSONObject d = byDate.optJSONObject(date);
            String status = overrides.optString(date, d == null ? "idle" : d.optString("status", "idle"));

            v.setTextViewText(rid, String.valueOf(day));
            v.setInt(rid, "setBackgroundResource", drawable(status));

            Intent click = new Intent(c, CashPilotWidget.class).setAction(TOGGLE)
                    .putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id).putExtra("date", date);
            v.setOnClickPendingIntent(rid, PendingIntent.getBroadcast(c, id * 100 + slot, click, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));

            if ("work".equals(status)) work++;
            else if ("duty".equals(status)) duty++;
            else if ("vacation".equals(status)) vac++;
            else idle++;
        }

        int dim = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
        SharedPreferences ratePrefs = c.getSharedPreferences(PREFS, 0);
        double workRate = rate(ratePrefs, "rate_work", "rate_work_k", 100000);
        double dutyRate = rate(ratePrefs, "rate_duty", "rate_duty_k", 30000);
        boolean daily = ratePrefs.getBoolean("rate_mode_daily", false);
        double total = daily ? work * workRate + (duty + idle) * dutyRate : (work * workRate + (duty + idle) * dutyRate) / dim;

        v.setTextViewText(R.id.workCount, String.valueOf(work));
        v.setTextViewText(R.id.dutyCount, String.valueOf(duty));
        v.setTextViewText(R.id.vacationCount, String.valueOf(vac));
        v.setTextViewText(R.id.idleCount, String.valueOf(idle));
        
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("uk", "UA"));
        symbols.setGroupingSeparator(' ');
        symbols.setDecimalSeparator(',');
        DecimalFormat df = new DecimalFormat("#,##0.00 грн", symbols);
        v.setTextViewText(R.id.totalPremium, df.format(total));

        String time = new SimpleDateFormat("dd.MM HH:mm", Locale.US).format(new Date());
        if (isUpdating) v.setTextViewText(R.id.statusTitle, "Оновлення...");
        else if (overrides.length() > 0) v.setTextViewText(R.id.statusTitle, "Локальний калькулятор · " + time);
        else if (raw == null && !cached) v.setTextViewText(R.id.statusTitle, "Локальний калькулятор");
        else v.setTextViewText(R.id.statusTitle, (cached ? "Локальний кеш · " : "Сервер статистики PIYUSHNYA MCC · ") + time);

        return v;
    }

    static int drawable(String s) {
        if ("work".equals(s)) return R.drawable.widget_day_work;
        if ("duty".equals(s)) return R.drawable.widget_day_duty;
        if ("vacation".equals(s)) return R.drawable.widget_day_vacation;
        return R.drawable.widget_day_idle;
    }

    static void toggle(Context c, int id, String date) {
        String month = date.substring(0, 7);
        SharedPreferences p = c.getSharedPreferences(PREFS, 0);
        try {
            JSONObject o = readOverrides(c, id, month);
            String cur = o.optString(date, "idle");
            String next = "work".equals(cur) ? "duty" : "duty".equals(cur) ? "vacation" : "vacation".equals(cur) ? "idle" : "work";
            o.put(date, next);
            p.edit().putString(sharedOverrideKey(month), o.toString()).putString("override_" + id + "_" + month, o.toString()).apply();
        } catch (Exception ignored) {}
    }

    static String sharedOverrideKey(String month) { return "override_shared_" + month; }
    static String sharedCacheKey(String month) { return "cache_shared_" + month; }
    static void refreshAll(Context c) {
        AppWidgetManager manager = AppWidgetManager.getInstance(c);
        int[] ids = manager.getAppWidgetIds(new ComponentName(c, CashPilotWidget.class));
        for (int id : ids) refresh(c, id, false);
    }
    static boolean hasOverrides(Context c, int id, String month) { return readOverrides(c, id, month).length() > 0; }
    static JSONObject readOverrides(Context c, int id, String month) {
        SharedPreferences p = c.getSharedPreferences(PREFS, 0);
        try {
            JSONObject common = new JSONObject(p.getString(sharedOverrideKey(month), "{}"));
            JSONObject legacy = new JSONObject(p.getString("override_" + id + "_" + month, "{}"));
            java.util.Iterator<String> keys = legacy.keys();
            while (keys.hasNext()) { String key = keys.next(); if (!common.has(key)) common.put(key, legacy.get(key)); }
            if (common.length() > 0 && !p.getString(sharedOverrideKey(month), "{}").equals(common.toString())) p.edit().putString(sharedOverrideKey(month), common.toString()).apply();
            return common;
        } catch (Exception e) { return new JSONObject(); }
    }
    static double rate(SharedPreferences p, String exactKey, String legacyKey, int fallback) {
        if (p.contains(exactKey)) return Math.max(0, p.getInt(exactKey, fallback));
        return Math.max(0, p.getInt(legacyKey, fallback / 1000) * 1000d);
    }

    static void bindButtons(Context c, RemoteViews v, int id, String month) {
        Intent a = new Intent(c, CashPilotWidget.class).setAction(PREV).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        Intent b = new Intent(c, CashPilotWidget.class).setAction(NEXT).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        Intent r = new Intent(c, CashPilotWidget.class).setAction(RESET).putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, id);
        Intent s = new Intent(c, SettingsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Intent n = new Intent(c, CashPilotWidget.class).setAction(NO_OP);
        
        v.setOnClickPendingIntent(R.id.widget_root, PendingIntent.getBroadcast(c, id * 10 + 9, n, PendingIntent.FLAG_IMMUTABLE));
        v.setOnClickPendingIntent(R.id.prevMonth, PendingIntent.getBroadcast(c, id * 10 + 1, a, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        v.setOnClickPendingIntent(R.id.nextMonth, PendingIntent.getBroadcast(c, id * 10 + 2, b, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        v.setOnClickPendingIntent(R.id.reset, PendingIntent.getBroadcast(c, id * 10 + 3, r, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));
        v.setOnClickPendingIntent(R.id.settings, PendingIntent.getActivity(c, id, s, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE));

        v.setInt(R.id.settings, "setColorFilter", Color.WHITE);
        v.setInt(R.id.prevMonth, "setColorFilter", Color.WHITE);
        v.setInt(R.id.nextMonth, "setColorFilter", Color.WHITE);
        v.setInt(R.id.reset, "setColorFilter", Color.WHITE);
    }

    static String monthFor(Context c, int id) { return c.getSharedPreferences(PREFS, 0).getString("month_" + id, new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date())); }
    static String shift(String m, int n) {
        Calendar c = Calendar.getInstance();
        try { c.set(Integer.parseInt(m.substring(0, 4)), Integer.parseInt(m.substring(5, 7)) - 1, 1); }
        catch (Exception e) { return m; }
        c.add(Calendar.MONTH, n);
        return new SimpleDateFormat("yyyy-MM", Locale.US).format(c.getTime());
    }
    static String title(String m) {
        try { return MONTHS[Integer.parseInt(m.substring(5, 7)) - 1] + " " + m.substring(0, 4); }
        catch (Exception e) { return m; }
    }
    static String enc(String s) throws Exception { return URLEncoder.encode(s, "UTF-8"); }
}
