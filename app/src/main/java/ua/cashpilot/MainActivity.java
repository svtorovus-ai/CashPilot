package ua.cashpilot;

import android.app.Activity;
import android.app.AlertDialog;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.*;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ScrollView;
import android.widget.TextView;
import android.net.Uri;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import org.json.JSONArray;
import org.json.JSONObject;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends Activity {
    private static final String APP_MONTH = "app_month";
    private static final int DATA_ID = 0;
    private SharedPreferences prefs;
    private DashboardView dashboard;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private boolean firstResume = true;
    private long loadGeneration;

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().setStatusBarColor(Color.rgb(8, 12, 18));
        getWindow().setNavigationBarColor(Color.rgb(8, 12, 18));
        prefs = getSharedPreferences(CashPilotWidget.PREFS, 0);
        dashboard = new DashboardView(this);
        setContentView(dashboard);
    }

    @Override protected void onResume() {
        super.onResume();
        if (firstResume || dashboard != null) loadMonth(month());
        firstResume = false;
    }

    @Override protected void onDestroy() {
        executor.shutdownNow();
        super.onDestroy();
    }

    private String month() {
        String value = prefs.getString(APP_MONTH, new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date()));
        return value.matches("\\d{4}-\\d{2}") ? value : new SimpleDateFormat("yyyy-MM", Locale.US).format(new Date());
    }

    private void loadMonth(String selected) {
        final long generation = ++loadGeneration;
        prefs.edit().putString(APP_MONTH, selected).apply();
        dashboard.setLoading(selected);
        String cached = prefs.getString(CashPilotWidget.sharedCacheKey(selected), prefs.getString("cache_" + DATA_ID + "_" + selected, null));
        dashboard.setData(selected, cached, cached != null, "Оновлення…");
        if (hasManualOverrides(selected)) return;
        executor.submit(() -> {
            String raw = null;
            boolean cachedResult = false;
            String message = "Сервер недоступний";
            try {
                raw = CashPilotWidget.fetch(prefs.getString("url", "http://204.168.225.114:8787"),
                        prefs.getString("token", ""), prefs.getString("install", ""), selected);
                if (!validPayload(raw, selected)) throw new IllegalStateException("Некоректна відповідь сервера");
                prefs.edit().putString("cache_" + DATA_ID + "_" + selected, raw).apply();
                prefs.edit().putString(CashPilotWidget.sharedCacheKey(selected), raw).apply();
                String previous = previousMonthValue(selected);
                String previousCache = prefs.getString("cache_" + DATA_ID + "_" + previous, null);
                if (!validPayload(previousCache, previous)) {
                    try {
                        String previousRaw = CashPilotWidget.fetch(prefs.getString("url", "http://204.168.225.114:8787"),
                                prefs.getString("token", ""), prefs.getString("install", ""), previous);
                        if (validPayload(previousRaw, previous)) prefs.edit().putString("cache_" + DATA_ID + "_" + previous, previousRaw).putString(CashPilotWidget.sharedCacheKey(previous), previousRaw).apply();
                    } catch (Exception ignored) { }
                }
                message = "Сервер";
            } catch (Exception failure) {
                raw = prefs.getString(CashPilotWidget.sharedCacheKey(selected), prefs.getString("cache_" + DATA_ID + "_" + selected, null));
                cachedResult = raw != null;
                if (cachedResult) message = "Кеш сервера";
            }
            String result = raw;
            boolean fromCache = cachedResult;
            String label = message;
            runOnUiThread(() -> {
                if (generation == loadGeneration && selected.equals(month())) dashboard.setData(selected, result, fromCache, label);
            });
        });
    }

    private static boolean validPayload(String raw, String month) {
        if (raw == null || raw.trim().isEmpty()) return false;
        try {
            JSONObject root = new JSONObject(raw);
            JSONObject stats = root.optJSONObject("stats");
            JSONArray days = stats == null ? null : stats.optJSONArray("days");
            return root.optBoolean("ok", false) && stats != null && month.equals(stats.optString("month"))
                    && days != null && days.length() >= 28;
        } catch (Exception e) { return false; }
    }

    private void shift(int amount) {
        Calendar value = Calendar.getInstance();
        String[] pieces = month().split("-");
        value.set(Integer.parseInt(pieces[0]), Integer.parseInt(pieces[1]) - 1, 1);
        value.add(Calendar.MONTH, amount);
        String target = String.format(Locale.US, "%04d-%02d", value.get(Calendar.YEAR), value.get(Calendar.MONTH) + 1);
        dashboard.animateMonth(amount, () -> loadMonth(target));
    }

    private void resetMonth() {
        String selected = month();
        prefs.edit().remove(CashPilotWidget.sharedOverrideKey(selected)).remove("override_" + DATA_ID + "_" + selected).apply();
        CashPilotWidget.refreshAll(this);
        loadMonth(selected);
    }

    private void showInfo() {
        ScrollView scroll = new ScrollView(this);
        TextView text = new TextView(this);
        text.setText(infoText()); text.setTextSize(15); text.setTypeface(android.graphics.Typeface.DEFAULT_BOLD); text.setPadding(28, 24, 28, 24);
        scroll.addView(text);
        new AlertDialog.Builder(this).setView(scroll).setPositiveButton("ЗАКРИТИ", null).show();
    }

    private SpannableStringBuilder infoText() {
        SpannableStringBuilder out = new SpannableStringBuilder();
        appendInfo(out, "CASHPILOT— інформація\n\n", 0xFFB8D7FF, true);
        appendInfo(out, "CashPilot - локальний калькулятор + віджет для перегляду статистики. Та прогнозування нарахування \"Премії\" (Додаткової винагороди) Календар показує статуси днів зі статистики telemetry-сервера, або внесені вручну дані. Дані з сервера лише читаються, а ручні зміни статусів зберігаються локально на цьому пристрої.\n\n", 0xFFE8EEF5, false);
        appendInfo(out, "СТАТУСИ\n\n", 0xFFB8D7FF, true);
        appendInfo(out, "Зелений - Працював. (Повний 100к)\n\n", 0xFF4DDB82, true);
        appendInfo(out, "Жовтий — Чергував. (сектор 30к)\n\n", 0xFFFFC928, true);
        appendInfo(out, "Синій — Відпустка, 0 грн.\n\n", 0xFF3AA9FF, true);
        appendInfo(out, "Чорний — Без даних,\n\n", 0xFF9AA8B8, true);
        appendInfo(out, "(Жовтий і чорний статуси дня в календарі обидва рахуються однаково. Це лише зручна візуальна позначка)\n\n", 0xFFE8EEF5, false);
        appendInfo(out, "РОЗРАХУНОК\n\n", 0xFFB8D7FF, true);
        appendInfo(out, "У режимі «За місяць» ставка за день = місячна ставка / кількість днів у місяці. У режимі «За день» введена сума нараховується за кожен відповідний день. Премія складається з робочих днів, чергувань і днів без даних. Відпустка не нараховується. Ставки змінюються в Налаштуваннях; кнопка «ЗСУ 100000 / 30000» повертає стандартні значення.\n\n", 0xFFE8EEF5, false);
        appendInfo(out, "СЕРВЕР\n\n", 0xFFB8D7FF, true);
        appendInfo(out, "Статистика автоматично підтягується з серверу телеметрії, якщо ти користуєшся додатком \"PITUSHNYA MCC\". Робочі дні та чергування вносяться в статистику автоматично, без ручного вводу. CashPilot звертається до URL із Налаштувань, передає token та install_id, перевіряє місяць у JSON-відповіді й кешує останні валідні дані. Якщо сервер недоступний, показується кеш із відповідною позначкою.\n\n", 0xFFE8EEF5, false);
        appendInfo(out, "ВІДЖЕТ\n\n", 0xFFB8D7FF, true);
        appendInfo(out, "CashPilot має віджет для головного екрана. Він показує календар, статистику та суму, дозволяє гортати місяці й змінювати статуси локально. Локальні зміни віджета не записуються на сервер.\n\n", 0xFFE8EEF5, false);
        appendInfo(out, "ПОРІВНЯННЯ\n\n", 0xFFB8D7FF, true);
        appendInfo(out, "Рядок під премією порівнює поточну суму з попереднім місяцем, якщо для нього є валідна статистика.\n\nЗАКРИТИ", 0xFFE8EEF5, false);
        return out;
    }

    private void appendInfo(SpannableStringBuilder out, String value, int color, boolean heading) {
        int start = out.length(); out.append(value); out.setSpan(new ForegroundColorSpan(color), start, out.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        if (heading) out.setSpan(new StyleSpan(android.graphics.Typeface.BOLD), start, out.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
    }

    private String previousMonthValue(String value) {
        Calendar c = Calendar.getInstance(); c.set(Integer.parseInt(value.substring(0, 4)), Integer.parseInt(value.substring(5, 7)) - 1, 1); c.add(Calendar.MONTH, -1);
        return String.format(Locale.US, "%04d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH) + 1);
    }

    private void toggleDay(int day) {
        String selected = month();
        Map<Integer, String> statuses = statuses(selected);
        if (statuses == null) return;
        String key = selected + String.format(Locale.US, "-%02d", day);
        JSONObject overrides = overrides(selected);
        String current = overrides.optString(key, statuses.get(day));
        String next = "work".equals(current) ? "duty" : "duty".equals(current) ? "vacation" : "vacation".equals(current) ? "idle" : "work";
        try { overrides.put(key, next); } catch (Exception ignored) { return; }
        prefs.edit().putString(CashPilotWidget.sharedOverrideKey(selected), overrides.toString()).putString("override_" + DATA_ID + "_" + selected, overrides.toString()).apply();
        CashPilotWidget.refreshAll(this);
        dashboard.setData(selected, prefs.getString(CashPilotWidget.sharedCacheKey(selected), prefs.getString("cache_" + DATA_ID + "_" + selected, null)), false, "Локальні зміни");
    }

    private JSONObject overrides(String selected) {
        try { return new JSONObject(prefs.getString(CashPilotWidget.sharedOverrideKey(selected), prefs.getString("override_" + DATA_ID + "_" + selected, "{}"))); }
        catch (Exception e) { return new JSONObject(); }
    }

    private Map<Integer, String> statuses(String selected) {
        String raw = prefs.getString(CashPilotWidget.sharedCacheKey(selected), prefs.getString("cache_" + DATA_ID + "_" + selected, null));
        if (!validPayload(raw, selected)) return null;
        Map<Integer, String> result = new HashMap<>();
        try {
            JSONArray days = new JSONObject(raw).getJSONObject("stats").getJSONArray("days");
            for (int i = 0; i < days.length(); i++) {
                JSONObject day = days.getJSONObject(i);
                result.put(Integer.parseInt(day.getString("date").substring(8, 10)), day.getString("status"));
            }
            return result;
        } catch (Exception e) { return null; }
    }

    private boolean hasManualOverrides(String selected) { return overrides(selected).length() > 0; }

    private final class DashboardView extends View {
        private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        private final Path path = new Path();
        private String selected;
        private String raw;
        private boolean cached;
        private String source = "Оновлення…";
        private float density;
        private int firstDay, daysInMonth;
        private float downX, downY;
        private float calendarTopPx, controlsTopPx;
        private boolean monthAnimating;
        private float calendarSlideOffset;

        DashboardView(Context context) {
            super(context);
            density = getResources().getDisplayMetrics().density;
            setFocusable(true);
        }

        void setLoading(String month) { selected = month; source = "Оновлення…"; invalidate(); }

        void setData(String month, String response, boolean fromCache, String sourceText) {
            selected = month;
            raw = validPayload(response, month) ? response : null;
            cached = fromCache && raw != null;
            source = sourceText;
            invalidate();
        }

        float d(float value) { return value * density; }
        void color(int value) { paint.setColor(value); paint.setStyle(Paint.Style.FILL); }
        void type(float size, int value, boolean bold) { paint.setTextSize(d(size)); paint.setColor(value); paint.setTypeface(Typeface.create("sans", Typeface.BOLD)); paint.setTextAlign(Paint.Align.LEFT); }

        @Override protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            if (selected == null) selected = month();
            int width = getWidth(), height = getHeight();
            color(Color.rgb(3, 5, 9)); canvas.drawRect(0, 0, width, height, paint);
            float pad = d(18), right = width - pad;
            paint.setShader(new LinearGradient(0, 0, 0, height, 0xE51D2532, 0xF003060B, Shader.TileMode.CLAMP));
            canvas.drawRect(0, 0, width, height, paint);
            paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(d(1)); paint.setColor(0x667A8795);
            canvas.drawRect(0, 0, width - d(1), height - d(1), paint);
            paint.setStyle(Paint.Style.FILL);

            float top = d(28), button = d(44), center = width / 2f;
            type(11, 0xFF9FADBD, true); paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText("Шо там по бабкам¿", center, top + d(8), paint);
            type(19, 0xFFF2F6FB, true); paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(title(selected == null ? month() : selected), center, top + d(31), paint);

            float y = top + button + d(16);
            String rateUnit = prefs.getBoolean("rate_mode_daily", false) ? "/день" : "/міс";
            type(12, 0xFF9FADBD, true); canvas.drawText("Працював " + rateLabel(CashPilotWidget.rate(prefs, "rate_work", "rate_work_k", 100000)) + rateUnit + " · Чергував " + rateLabel(CashPilotWidget.rate(prefs, "rate_duty", "rate_duty_k", 30000)) + rateUnit + " · Відпустка 0", pad, y, paint);
            y += d(31);
            firstDay = firstDayOfMonth(selected);
            daysInMonth = daysInSelectedMonth(selected);
            Map<Integer, String> data = statusesFromRaw();
            JSONObject local = overrides(selected == null ? month() : selected);
            int work = 0, duty = 0, vacation = 0, idle = 0;
            if (data != null) {
                for (int day = 1; day <= daysInMonth; day++) {
                    String key = selected + String.format(Locale.US, "-%02d", day);
                    String state = local.optString(key, data.get(day));
                    if ("work".equals(state)) work++; else if ("duty".equals(state)) duty++; else if ("vacation".equals(state)) vacation++; else idle++;
                }
            }
            boolean compact = width < d(500);
            if (compact) {
                type(10, 0xFF93A4B6, false); paint.setTextAlign(Paint.Align.LEFT); canvas.drawText("Премія", pad, y, paint);
                type(22, 0xFFB8E7FF, true); paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(data == null ? "— грн" : money(work, duty, vacation, idle, daysInMonth), right, y, paint);
                y += d(31);
                drawStats(canvas, pad, y, work, duty, vacation, idle, 12);
                drawStatsSecond(canvas, pad, y + d(18), vacation, idle, 12);
                y += d(27);
                y = drawComparison(canvas, pad, y, selected, work, duty, vacation, idle, daysInMonth);
            } else {
                drawStats(canvas, pad, y, work, duty, vacation, idle, 14);
                drawStatsSecond(canvas, pad, y + d(20), vacation, idle, 14);
                type(25, 0xFFB8E7FF, true); paint.setTextAlign(Paint.Align.RIGHT);
                canvas.drawText(data == null ? "— грн" : money(work, duty, vacation, idle, daysInMonth), right, y + d(11), paint);
                y += d(32);
                y = drawComparison(canvas, pad, y, selected, work, duty, vacation, idle, daysInMonth);
            }
            paint.setColor(0x304C5764); canvas.drawRect(pad, y, right, y + d(1), paint); y += d(21);
            String[] weekdays = {"Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Нд"};
            float cellW = (right - pad) / 7f;
            float controlsTop = height - d(66);
            canvas.save();
            canvas.clipRect(0, y - d(16), width, controlsTop);
            canvas.translate(calendarSlideOffset, 0);
            type(11, 0xFF9BA9BA, false); paint.setTextAlign(Paint.Align.CENTER);
            for (int i = 0; i < 7; i++) canvas.drawText(weekdays[i], pad + cellW * (i + .5f), y, paint);
            y += d(13);
            float gridH = controlsTop - y - d(12), rowH = Math.min(gridH / 6f, cellW * 0.98f);
            controlsTopPx = controlsTop; calendarTopPx = y;
            for (int slot = 0; slot < 42; slot++) {
                int day = slot - firstDay + 1;
                if (day < 1 || day > daysInMonth) continue;
                int row = slot / 7, col = slot % 7;
                float l = pad + col * cellW + d(2), t = y + row * rowH + d(2), rr = pad + (col + 1) * cellW - d(2), b = y + (row + 1) * rowH - d(2);
                String state = data == null ? "idle" : local.optString(selected + String.format(Locale.US, "-%02d", day), data.get(day));
                drawDay(canvas, l, t, rr, b, state, day);
            }
            canvas.restore();
            type(10, 0xFF93A4B6, true); paint.setTextAlign(Paint.Align.LEFT);
            canvas.drawText(sourceLabel(), pad, height - d(14), paint);
            float gap = d(12), buttonsWidth = button * 5 + gap * 4, buttonsLeft = (width - buttonsWidth) / 2f;
            for (int i = 0; i < 5; i++) drawButton(canvas, buttonsLeft + i * (button + gap), controlsTop, button, i);
        }

        private Map<Integer, String> statusesFromRaw() {
            Map<Integer, String> result = new HashMap<>();
            if (!validPayload(raw, selected)) return null;
            try {
                JSONArray days = new JSONObject(raw).getJSONObject("stats").getJSONArray("days");
                for (int i = 0; i < days.length(); i++) { JSONObject d = days.getJSONObject(i); result.put(Integer.parseInt(d.getString("date").substring(8,10)), d.getString("status")); }
                return result;
            } catch (Exception e) { return null; }
        }

        private void drawStats(Canvas c, float x, float baseline, int work, int duty, int vacation, int idle, float size) {
            String workText = "Працював " + (raw == null ? "—" : work);
            String dutyText = "Чергував " + (raw == null ? "—" : duty);
            drawPart(c, workText, x, baseline, size, 0xFF4DDB82); x += paint.measureText(workText);
            drawPart(c, "  ·  ", x, baseline, size, 0xFFB7C3D0); x += paint.measureText("  ·  ");
            drawPart(c, dutyText, x, baseline, size, 0xFFFFC928);
        }

        private void drawStatsSecond(Canvas c, float x, float baseline, int vacation, int idle, float size) {
            String vacationText = "Відпустка " + (raw == null ? "—" : vacation);
            String idleText = "Без даних " + (raw == null ? "—" : idle);
            drawPart(c, vacationText, x, baseline, size, 0xFF3AA9FF); x += paint.measureText(vacationText);
            drawPart(c, "  ·  ", x, baseline, size, 0xFFB7C3D0); x += paint.measureText("  ·  ");
            drawPart(c, idleText, x, baseline, size, 0xFF9AA8B8);
        }

        private void drawPart(Canvas c, String text, float x, float baseline, float size, int color) {
            type(size, color, true); paint.setTextAlign(Paint.Align.LEFT); c.drawText(text, x, baseline, paint);
        }

        private float drawComparison(Canvas c, float x, float y, String selectedMonth, int work, int duty, int vacation, int idle, int days) {
            if (raw == null) return y;
            double previous = premiumFor(previousMonth(selectedMonth));
            if (previous < 0) return y;
            double current = premiumValue(work, duty, vacation, idle, days);
            double difference = current - previous;
            if (Math.abs(difference) < 0.005) return y;
            double percent = previous == 0 ? 0 : Math.abs(difference) * 100d / previous;
            boolean more = difference > 0;
            String text = String.format(new Locale("uk", "UA"), "На ~%.1f%% (%s) %s ніж у попередньому місяці", percent, wholeMoney(Math.abs(difference)), more ? "більше" : "менше");
            type(10, more ? 0xFF4DDB82 : 0xFFFF6B6B, true); paint.setTextAlign(Paint.Align.LEFT); c.drawText(text, x, y + d(14), paint);
            return y + d(23);
        }

        private double premiumFor(String monthValue) {
            String value = prefs.getString("cache_" + DATA_ID + "_" + monthValue, null);
            if (!validPayload(value, monthValue)) return -1;
            try {
                JSONArray days = new JSONObject(value).getJSONObject("stats").getJSONArray("days");
                JSONObject local = overrides(monthValue); int work = 0, duty = 0, vacation = 0, idle = 0;
                int totalDays = daysInSelectedMonth(monthValue);
                for (int i = 0; i < days.length(); i++) {
                    JSONObject day = days.getJSONObject(i); int number = Integer.parseInt(day.getString("date").substring(8, 10));
                    String key = monthValue + String.format(Locale.US, "-%02d", number);
                    String state = local.optString(key, day.getString("status"));
                    if ("work".equals(state)) work++; else if ("duty".equals(state)) duty++; else if ("vacation".equals(state)) vacation++; else idle++;
                }
                return premiumValue(work, duty, vacation, idle, totalDays);
            } catch (Exception e) { return -1; }
        }

        private double premiumValue(int work, int duty, int vacation, int idle, int days) {
            double workRate = CashPilotWidget.rate(prefs, "rate_work", "rate_work_k", 100000);
            double dutyRate = CashPilotWidget.rate(prefs, "rate_duty", "rate_duty_k", 30000);
            if (prefs.getBoolean("rate_mode_daily", false)) return workRate * work + dutyRate * (duty + idle);
            return (workRate * work + dutyRate * (duty + idle)) / days;
        }

        private String money(int work, int duty, int vacation, int idle, int days) { return formatMoney(premiumValue(work, duty, vacation, idle, days), true); }
        private String formatMoney(double value, boolean decimals) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("uk", "UA")); symbols.setGroupingSeparator(' '); symbols.setDecimalSeparator(',');
            DecimalFormat format = new DecimalFormat(decimals ? "#,##0.00 грн" : "#,##0 грн", symbols); return format.format(value);
        }
        private String wholeMoney(double value) { return formatMoney(value, false); }
        private String rateLabel(double value) { return value % 1000 == 0 ? String.format(Locale.US, "%.0fк", value / 1000d) : String.format(Locale.US, "%.0f", value); }
        private String previousMonth(String value) { Calendar c = Calendar.getInstance(); c.set(Integer.parseInt(value.substring(0,4)), Integer.parseInt(value.substring(5,7))-1, 1); c.add(Calendar.MONTH, -1); return String.format(Locale.US, "%04d-%02d", c.get(Calendar.YEAR), c.get(Calendar.MONTH)+1); }
        private String sourceLabel() {
            if (hasManualOverrides(selected)) return "Локальний калькулятор";
            if (cached || source.contains("Кеш")) return "Локальний кеш";
            if (raw != null && !source.equals("Оновлення…") && !source.contains("недоступний")) return "Сервер статистики PIYUSHNYA MCC";
            return "Локальний калькулятор";
        }

        void animateMonth(int amount, Runnable loadTarget) {
            if (monthAnimating) return;
            monthAnimating = true;
            float exit = amount > 0 ? -getWidth() : getWidth();
            ValueAnimator out = ValueAnimator.ofFloat(0f, exit);
            out.setDuration(180);
            out.addUpdateListener(a -> { calendarSlideOffset = (float) a.getAnimatedValue(); invalidate(); });
            out.addListener(new AnimatorListenerAdapter() {
                @Override public void onAnimationEnd(Animator animation) {
                loadTarget.run();
                calendarSlideOffset = -exit;
                ValueAnimator in = ValueAnimator.ofFloat(-exit, 0f);
                in.setDuration(240);
                in.addUpdateListener(a -> { calendarSlideOffset = (float) a.getAnimatedValue(); invalidate(); });
                in.addListener(new AnimatorListenerAdapter() { @Override public void onAnimationEnd(Animator animation) { calendarSlideOffset = 0f; monthAnimating = false; invalidate(); } });
                in.start();
                }
            });
            out.start();
        }

        private void drawDay(Canvas c, float l, float t, float r, float b, String state, int day) {
            int top, bottom, text;
            if ("work".equals(state)) { top = 0xE51B7652; bottom = 0xF005241C; text = 0xFFB7F5D8; }
            else if ("duty".equals(state)) { top = 0xE58A6508; bottom = 0xF0332204; text = 0xFFFFE6A0; }
            else if ("vacation".equals(state)) { top = 0xE5225C9B; bottom = 0xF00B2344; text = 0xFFB9DEFF; }
            else { top = 0xA5161C25; bottom = 0xF003060A; text = 0xFFC5CED8; }
            paint.setShader(new LinearGradient(0, t, 0, b, top, bottom, Shader.TileMode.CLAMP)); c.drawRoundRect(l, t, r, b, d(8), d(8), paint); paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(d(1)); paint.setColor(0x666F879B); c.drawRoundRect(l, t, r, b, d(8), d(8), paint); paint.setStyle(Paint.Style.FILL);
            type(15, text, true); paint.setTextAlign(Paint.Align.CENTER); c.drawText(String.valueOf(day), (l + r) / 2, (t + b) / 2 - (paint.ascent() + paint.descent()) / 2, paint);
        }

        private void drawButton(Canvas c, float x, float y, float size, int icon) {
            paint.setShader(new LinearGradient(0, y, 0, y + size, 0x704C5866, 0x40202730, Shader.TileMode.CLAMP)); c.drawRoundRect(x, y, x + size, y + size, d(12), d(12), paint); paint.setShader(null);
            paint.setStyle(Paint.Style.STROKE); paint.setStrokeWidth(d(3)); paint.setStrokeCap(Paint.Cap.ROUND); paint.setStrokeJoin(Paint.Join.ROUND); paint.setColor(0xFFD6E3EE);
            float cx = x + size / 2, cy = y + size / 2;
            if (icon == 0) { for (int i = -1; i <= 1; i++) { c.drawLine(cx + d(i * 10), y + d(10), cx + d(i * 10), y + d(32), paint); c.drawCircle(cx + d(i * 10), y + d(i == 0 ? 18 : 25), d(3), paint); } }
            else if (icon == 1) { path.reset(); path.moveTo(x + d(25), y + d(11)); path.lineTo(x + d(14), cy); path.lineTo(x + d(25), y + d(31)); c.drawPath(path, paint); }
            else if (icon == 2) { path.reset(); path.moveTo(x + d(17), y + d(11)); path.lineTo(x + d(28), cy); path.lineTo(x + d(17), y + d(31)); c.drawPath(path, paint); }
            else if (icon == 3) { c.drawArc(x + d(10), y + d(10), x + d(32), y + d(32), -55, 275, false, paint); path.reset(); path.moveTo(x + d(28), y + d(9)); path.lineTo(x + d(32), y + d(10)); path.lineTo(x + d(31), y + d(15)); c.drawPath(path, paint); }
            else { c.drawCircle(cx, cy, d(12), paint); c.drawCircle(cx, cy - d(5), d(1.8f), paint); c.drawLine(cx, cy, cx, cy + d(8), paint); }
            paint.setStrokeCap(Paint.Cap.BUTT); paint.setStyle(Paint.Style.FILL);
        }

        private String title(String value) { if (value == null || value.length() < 7) return "CashPilot"; return CashPilotWidget.MONTHS[Integer.parseInt(value.substring(5,7))-1] + " " + value.substring(0,4); }
        private int firstDayOfMonth(String value) { Calendar c = Calendar.getInstance(); c.set(Integer.parseInt(value.substring(0,4)), Integer.parseInt(value.substring(5,7))-1, 1); return (c.get(Calendar.DAY_OF_WEEK) + 5) % 7; }
        private int daysInSelectedMonth(String value) { Calendar c = Calendar.getInstance(); c.set(Integer.parseInt(value.substring(0,4)), Integer.parseInt(value.substring(5,7))-1, 1); return c.getActualMaximum(Calendar.DAY_OF_MONTH); }

        @Override public boolean onTouchEvent(MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_DOWN) { downX = event.getX(); downY = event.getY(); return true; }
            if (event.getAction() != MotionEvent.ACTION_UP) return true;
            float x = event.getX(), y = event.getY(), pad = d(18), button = d(44), top = d(28), right = getWidth() - pad;
            if (Math.abs(x - downX) > d(60) && Math.abs(x - downX) > Math.abs(y - downY)) { shift(x > downX ? -1 : 1); return true; }
            float controlsTop = controlsTopPx > 0 ? controlsTopPx : getHeight() - d(66), gap = d(12), buttonsWidth = button * 5 + gap * 4, buttonsLeft = (getWidth() - buttonsWidth) / 2f;
            if (y >= controlsTop - d(8)) {
                int buttonIndex = (int)((x - buttonsLeft) / (button + gap));
                if (buttonIndex >= 0 && buttonIndex < 5 && x >= buttonsLeft + buttonIndex * (button + gap)
                        && x <= buttonsLeft + buttonIndex * (button + gap) + button) {
                    performClick();
                    if (buttonIndex == 0) startActivity(new Intent(MainActivity.this, SettingsActivity.class));
                    else if (buttonIndex == 1) shift(-1);
                    else if (buttonIndex == 2) shift(1);
                    else if (buttonIndex == 3) resetMonth();
                    else showInfo();
                    return true;
                }
            }
            float calendarTop = calendarTopPx > 0 ? calendarTopPx : top + button + d(16) + d(31) + d(54) + d(21) + d(13);
            float cellW = (right - pad) / 7f, gridH = controlsTop - calendarTop - d(12), rowH = Math.min(gridH / 6f, cellW * 0.98f);
            if (y >= calendarTop && y <= calendarTop + rowH * 6) {
                int col = (int)((x - pad) / cellW), row = (int)((y - calendarTop) / rowH), day = row * 7 + col - firstDay + 1;
                if (col >= 0 && col < 7 && day >= 1 && day <= daysInMonth) toggleDay(day);
            }
            return true;
        }

        @Override public boolean performClick() {
            super.performClick();
            return true;
        }
    }
}
