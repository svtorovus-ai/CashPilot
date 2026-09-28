package ua.cashpilot;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import java.util.Objects;

public enum AppTheme {
    DARK_CLASSIC("dark_classic", "🌌 CashPilot Dark (Класична)",
            0xFF080C12, 0xFF080C12, 0xE51D2532, 0xF003060B, 0x667A8795,
            0xFF9FADBD, 0xFFF2F6FB, 0xFFB8E7FF, 0xFF9BA9BA,
            0x704C5866, 0x40202730, 0xFFD6E3EE,
            0xE51B7652, 0xF005241C, 0xFFB7F5D8, 0x666F879B, // work
            0xE58A6508, 0xF0332204, 0xFFFFE6A0, 0x666F879B, // duty
            0xE5225C9B, 0xF00B2344, 0xFFB9DEFF, 0x666F879B, // vacation
            0xA5161C25, 0xF003060A, 0xFFC5CED8, 0x666F879B, // idle
            0),

    UAV_DRONE("uav_drone", "🛸 БпЛА / Дрони (UAV Sky)",
            0xFF0B1F33, 0xFF0B1F33, 0xF018385C, 0xF00B1A2B, 0x8840C4FF,
            0xFF80D8FF, 0xFFE0F7FA, 0xFF00E5FF, 0xFF80D8FF,
            0x80104060, 0x50082030, 0xFF40C4FF,
            0xE50B6E50, 0xF003291D, 0xFFB2FFD6, 0x9900FFCC, // work (UAV Airplane)
            0xE58F6000, 0xF03B2600, 0xFFFFE57F, 0x99FFD54F, // duty (Flying Wing)
            0xE500579B, 0xF0002244, 0xFF80D8FF, 0x9940C4FF, // vacation (Palm Tree)
            0x9910283B, 0xF0081420, 0xFFB0BEC5, 0x6637474F, // idle
            0),

    HELLO_KITTY("hello_kitty", "🎀 Hello Kitty (Cute Pink)",
            0xFF2D1420, 0xFF2D1420, 0xF03D1A2B, 0xF0200D16, 0x88FF80AB,
            0xFFFFB2DD, 0xFFFFF0F5, 0xFFFF80AB, 0xFFFFC2E2,
            0x808C2053, 0x50400C24, 0xFFFFA4D2,
            0xE5C2185B, 0xF04A0B22, 0xFFFFD1DC, 0x99FF80AB, // work (Heart)
            0xE58E24AA, 0xF0380060, 0xFFE1BEE7, 0x99CE93D8, // duty (Cloud)
            0xE500838F, 0xF000363A, 0xFF80DEEA, 0x994DD0E1, // vacation (Palm Tree)
            0x993D1B2D, 0xF01C0A13, 0xFFF8BBD0, 0x66880E4F, // idle
            R.drawable.theme_kitty_bg),

    PIXEL_CAMO("pixel_camo", "🪖 Піксель ЗСУ (MM-14 Camo)",
            0xFF121A12, 0xFF121A12, 0xF01D2B1D, 0xF00D140D, 0x884D6A42,
            0xFFC5D3A3, 0xFFF1F5E6, 0xFFFFD54F, 0xFFAEC49A,
            0x802D3E29, 0x50152013, 0xFFD8E4BC,
            0xE52E5B27, 0xF010260D, 0xFFC8E6C9, 0x9981C784, // work (₴ Hryvnia)
            0xE57F6100, 0xF0332600, 0xFFFFF59D, 0x99FFF176, // duty (Middle Finger)
            0xE51D526A, 0xF009222E, 0xFFB2EBF2, 0x994DD0E1, // vacation (Palm Tree)
            0x99192419, 0xF00D130D, 0xFFCFD8DC, 0x66455A64, // idle
            R.drawable.theme_pixel_bg),

    NEON_RADAR("neon_radar", "📡 Векторний Радар (Cyber Radar)",
            0xFF000E06, 0xFF000E06, 0xF0001A0B, 0xF0000A04, 0x8800FF66,
            0xFF00FF66, 0xFFE0FFEC, 0xFF00FFCC, 0xFF69FF94,
            0x80003D18, 0x50001A0A, 0xFF00FF66,
            0xE5006622, 0xF000260B, 0xFFB3FFCC, 0x9900FF66, // work (UAV Airplane)
            0xE5856B00, 0xF0332900, 0xFFFFE066, 0x99FFD700, // duty (Quadcopter)
            0xE5005B85, 0xF0002133, 0xFF99ECFF, 0x9933CCFF, // vacation (Palm Tree)
            0x9900220F, 0xF0000F06, 0xFF99FFC6, 0x66006622, // idle
            R.drawable.theme_radar_bg);

    public final String key;
    public final String title;
    public final int statusNavBgColor;
    public final int outerBgColor;
    public final int bgGradientTop;
    public final int bgGradientBottom;
    public final int bgBorderColor;
    public final int subtitleColor;
    public final int titleColor;
    public final int moneyColor;
    public final int weekdaysColor;
    public final int btnGradTop;
    public final int btnGradBottom;
    public final int btnStrokeColor;

    public final int workGradTop, workGradBottom, workTextColor, workBorderColor;
    public final int dutyGradTop, dutyGradBottom, dutyTextColor, dutyBorderColor;
    public final int vacationGradTop, vacationGradBottom, vacationTextColor, vacationBorderColor;
    public final int idleGradTop, idleGradBottom, idleTextColor, idleBorderColor;
    public final int bgDrawableResId;

    AppTheme(String key, String title, int statusNavBgColor, int outerBgColor, int bgGradientTop, int bgGradientBottom, int bgBorderColor,
             int subtitleColor, int titleColor, int moneyColor, int weekdaysColor,
             int btnGradTop, int btnGradBottom, int btnStrokeColor,
             int workGradTop, int workGradBottom, int workTextColor, int workBorderColor,
             int dutyGradTop, int dutyGradBottom, int dutyTextColor, int dutyBorderColor,
             int vacationGradTop, int vacationGradBottom, int vacationTextColor, int vacationBorderColor,
             int idleGradTop, int idleGradBottom, int idleTextColor, int idleBorderColor,
             int bgDrawableResId) {
        this.key = key;
        this.title = title;
        this.statusNavBgColor = statusNavBgColor;
        this.outerBgColor = outerBgColor;
        this.bgGradientTop = bgGradientTop;
        this.bgGradientBottom = bgGradientBottom;
        this.bgBorderColor = bgBorderColor;
        this.subtitleColor = subtitleColor;
        this.titleColor = titleColor;
        this.moneyColor = moneyColor;
        this.weekdaysColor = weekdaysColor;
        this.btnGradTop = btnGradTop;
        this.btnGradBottom = btnGradBottom;
        this.btnStrokeColor = btnStrokeColor;
        this.workGradTop = workGradTop;
        this.workGradBottom = workGradBottom;
        this.workTextColor = workTextColor;
        this.workBorderColor = workBorderColor;
        this.dutyGradTop = dutyGradTop;
        this.dutyGradBottom = dutyGradBottom;
        this.dutyTextColor = dutyTextColor;
        this.dutyBorderColor = dutyBorderColor;
        this.vacationGradTop = vacationGradTop;
        this.vacationGradBottom = vacationGradBottom;
        this.vacationTextColor = vacationTextColor;
        this.vacationBorderColor = vacationBorderColor;
        this.idleGradTop = idleGradTop;
        this.idleGradBottom = idleGradBottom;
        this.idleTextColor = idleTextColor;
        this.idleBorderColor = idleBorderColor;
        this.bgDrawableResId = bgDrawableResId;
    }

    public static AppTheme fromPrefs(SharedPreferences prefs) {
        String savedKey = prefs.getString("app_theme", DARK_CLASSIC.key);
        for (AppTheme t : values()) {
            if (Objects.equals(t.key, savedKey)) return t;
        }
        return DARK_CLASSIC;
    }

    public static void saveToPrefs(SharedPreferences prefs, AppTheme theme) {
        prefs.edit().putString("app_theme", theme.key).apply();
    }

    public void drawWatermark(Canvas c, Paint p, Path path, float width, float height, float d, Bitmap loadedBgBitmap) {
        p.setStyle(Paint.Style.FILL);
        p.setShader(null);

        if (loadedBgBitmap != null && !loadedBgBitmap.isRecycled()) {
            if (this == HELLO_KITTY) {
                // Tile Hello Kitty pattern nicely across canvas so kittens stay detailed and not zoomed in
                int bw = loadedBgBitmap.getWidth(), bh = loadedBgBitmap.getHeight();
                if (bw > 0 && bh > 0) {
                    float targetW = d * 220f;
                    float scale = targetW / (float) bw;
                    int scaledW = Math.max(10, (int) (bw * scale));
                    int scaledH = Math.max(10, (int) (bh * scale));
                    Bitmap scaled = Bitmap.createScaledBitmap(loadedBgBitmap, scaledW, scaledH, true);
                    for (int y = 0; y < height; y += scaledH) {
                        for (int x = 0; x < width; x += scaledW) {
                            c.drawBitmap(scaled, x, y, p);
                        }
                    }
                }
                // Dark translucent overlay to ensure text contrast
                p.setColor(0x88260E1A);
                c.drawRect(0, 0, width, height, p);
            } else if (this == PIXEL_CAMO) {
                // Scale/tile pixel camo image across screen
                Rect src = new Rect(0, 0, loadedBgBitmap.getWidth(), loadedBgBitmap.getHeight());
                RectF dst = new RectF(0, 0, width, height);
                c.drawBitmap(loadedBgBitmap, src, dst, p);
                p.setColor(0xAA101A10);
                c.drawRect(0, 0, width, height, p);
            } else if (this == NEON_RADAR) {
                // Scale radar background image to fill screen
                Rect src = new Rect(0, 0, loadedBgBitmap.getWidth(), loadedBgBitmap.getHeight());
                RectF dst = new RectF(0, 0, width, height);
                c.drawBitmap(loadedBgBitmap, src, dst, p);
                p.setColor(0x55000A04);
                c.drawRect(0, 0, width, height, p);
            }
        } else if (this == UAV_DRONE) {
            // Sky background with scattered small airplanes and quadcopter drones
            p.setStyle(Paint.Style.STROKE);
            p.setStrokeWidth(d * 1.5f);
            p.setColor(0x2200E5FF);

            // Draw floating small UAV planes
            drawUavAirplane(c, p, path, width * 0.2f, height * 0.25f, d * 18);
            drawUavAirplane(c, p, path, width * 0.8f, height * 0.35f, d * 14);
            drawUavAirplane(c, p, path, width * 0.3f, height * 0.75f, d * 22);

            // Draw floating small Quadcopters
            drawQuadcopter(c, p, path, width * 0.75f, height * 0.70f, d * 16);
            drawQuadcopter(c, p, path, width * 0.18f, height * 0.52f, d * 14);
        }
        p.setStyle(Paint.Style.FILL);
    }

    public void drawDayIcon(Canvas c, Paint p, Path path, String state, float cx, float cy, float d) {
        if ("idle".equals(state)) return;

        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setStrokeWidth(d * 1.2f);

        if ("vacation".equals(state)) {
            // ALL themes use a beautifully drawn Palm Tree for Vacation
            p.setColor(vacationTextColor);
            drawPalmTree(c, p, path, cx, cy, d * 8f);
            return;
        }

        if (this == HELLO_KITTY) {
            if ("work".equals(state)) {
                p.setColor(workTextColor);
                drawHeart(c, p, path, cx, cy, d * 7f);
            } else if ("duty".equals(state)) {
                p.setColor(dutyTextColor);
                drawCloud(c, p, path, cx, cy, d * 7f);
            }
        } else if (this == PIXEL_CAMO) {
            if ("work".equals(state)) {
                p.setColor(workTextColor);
                drawHryvniaSymbol(c, p, path, cx, cy, d * 7f);
            } else if ("duty".equals(state)) {
                p.setColor(dutyTextColor);
                drawMiddleFinger(c, p, path, cx, cy, d * 7f);
            }
        } else if (this == NEON_RADAR) {
            if ("work".equals(state)) {
                p.setColor(workTextColor);
                drawUavAirplane(c, p, path, cx, cy, d * 7f);
            } else if ("duty".equals(state)) {
                p.setColor(dutyTextColor);
                drawQuadcopter(c, p, path, cx, cy, d * 7f);
            }
        } else if (this == UAV_DRONE) {
            if ("work".equals(state)) {
                p.setColor(workTextColor);
                drawUavAirplane(c, p, path, cx, cy, d * 7.5f);
            } else if ("duty".equals(state)) {
                p.setColor(dutyTextColor);
                drawFlyingWing(c, p, path, cx, cy, d * 7.5f);
            }
        } else {
            // DARK_CLASSIC
            if ("work".equals(state)) {
                p.setColor(workTextColor);
                c.drawCircle(cx, cy, d * 3.5f, p);
            } else if ("duty".equals(state)) {
                p.setColor(dutyTextColor);
                drawStar(c, p, path, cx, cy, d * 5f);
            }
        }
        p.setStyle(Paint.Style.FILL);
    }

    // Vector Palm Tree (🌴) - Used for ALL themes on Vacation
    public static void drawPalmTree(Canvas c, Paint p, Path path, float cx, float cy, float s) {
        // Trunk
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(s * 0.22f);
        path.reset();
        path.moveTo(cx - s * 0.15f, cy + s * 0.8f);
        path.quadTo(cx - s * 0.05f, cy + s * 0.2f, cx + s * 0.05f, cy - s * 0.1f);
        c.drawPath(path, p);

        // 5 Arching Palm Fronds
        p.setStrokeWidth(s * 0.16f);
        float topX = cx + s * 0.05f, topY = cy - s * 0.1f;

        // Frond 1 (Left-down)
        path.reset(); path.moveTo(topX, topY); path.quadTo(topX - s * 0.5f, topY - s * 0.2f, topX - s * 0.8f, topY + s * 0.2f); c.drawPath(path, p);
        // Frond 2 (Left-up)
        path.reset(); path.moveTo(topX, topY); path.quadTo(topX - s * 0.4f, topY - s * 0.7f, topX - s * 0.7f, topY - s * 0.6f); c.drawPath(path, p);
        // Frond 3 (Top-center)
        path.reset(); path.moveTo(topX, topY); path.quadTo(topX, topY - s * 0.8f, topX + s * 0.1f, topY - s * 0.9f); c.drawPath(path, p);
        // Frond 4 (Right-up)
        path.reset(); path.moveTo(topX, topY); path.quadTo(topX + s * 0.4f, topY - s * 0.7f, topX + s * 0.7f, topY - s * 0.6f); c.drawPath(path, p);
        // Frond 5 (Right-down)
        path.reset(); path.moveTo(topX, topY); path.quadTo(topX + s * 0.5f, topY - s * 0.2f, topX + s * 0.8f, topY + s * 0.2f); c.drawPath(path, p);

        // Coconuts
        p.setStyle(Paint.Style.FILL);
        c.drawCircle(topX - s * 0.12f, topY + s * 0.05f, s * 0.12f, p);
        c.drawCircle(topX + s * 0.12f, topY + s * 0.05f, s * 0.12f, p);
    }

    // Vector Heart (❤️)
    public static void drawHeart(Canvas c, Paint p, Path path, float cx, float cy, float s) {
        path.reset();
        path.moveTo(cx, cy + s * 0.7f);
        path.cubicTo(cx - s * 1.2f, cy, cx - s * 1.1f, cy - s * 0.9f, cx, cy - s * 0.3f);
        path.cubicTo(cx + s * 1.1f, cy - s * 0.9f, cx + s * 1.2f, cy, cx, cy + s * 0.7f);
        path.close();
        c.drawPath(path, p);
    }

    // Vector Cloud (☁️)
    public static void drawCloud(Canvas c, Paint p, Path path, float cx, float cy, float s) {
        path.reset();
        float left = cx - s * 0.8f, right = cx + s * 0.8f, bottom = cy + s * 0.4f;
        c.drawCircle(cx - s * 0.3f, cy - s * 0.1f, s * 0.45f, p);
        c.drawCircle(cx + s * 0.15f, cy - s * 0.2f, s * 0.52f, p);
        c.drawCircle(cx + s * 0.45f, cy, s * 0.38f, p);
        c.drawCircle(cx - s * 0.45f, cy + s * 0.1f, s * 0.35f, p);
        path.moveTo(left, bottom);
        path.lineTo(right, bottom);
        path.lineTo(right, cy);
        path.lineTo(left, cy);
        path.close();
        c.drawPath(path, p);
    }

    // Vector Hryvnia Symbol (₴)
    public static void drawHryvniaSymbol(Canvas c, Paint p, Path path, float cx, float cy, float s) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(s * 0.22f);
        p.setStrokeCap(Paint.Cap.ROUND);
        // 'S' Curve
        path.reset();
        path.moveTo(cx + s * 0.4f, cy - s * 0.5f);
        path.cubicTo(cx - s * 0.4f, cy - s * 0.6f, cx - s * 0.4f, cy, cx + s * 0.1f, cy);
        path.cubicTo(cx + s * 0.5f, cy, cx + s * 0.5f, cy + s * 0.6f, cx - s * 0.4f, cy + s * 0.5f);
        c.drawPath(path, p);
        // Parallel horizontal bars
        c.drawLine(cx - s * 0.5f, cy - s * 0.2f, cx + s * 0.5f, cy - s * 0.2f, p);
        c.drawLine(cx - s * 0.5f, cy + s * 0.2f, cx + s * 0.5f, cy + s * 0.2f, p);
        p.setStyle(Paint.Style.FILL);
    }

    // Vector Middle Finger (🖕)
    public static void drawMiddleFinger(Canvas c, Paint p, Path path, float cx, float cy, float s) {
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setStrokeWidth(s * 0.15f);
        path.reset();
        // Hand fist base
        path.moveTo(cx - s * 0.5f, cy + s * 0.7f);
        path.lineTo(cx + s * 0.5f, cy + s * 0.7f);
        path.lineTo(cx + s * 0.5f, cy + s * 0.1f);
        path.lineTo(cx + s * 0.18f, cy + s * 0.1f);
        // Extended Middle Finger
        path.lineTo(cx + s * 0.18f, cy - s * 0.8f);
        path.cubicTo(cx + s * 0.18f, cy - s * 1.05f, cx - s * 0.18f, cy - s * 1.05f, cx - s * 0.18f, cy - s * 0.8f);
        path.lineTo(cx - s * 0.18f, cy + s * 0.1f);
        path.lineTo(cx - s * 0.5f, cy + s * 0.1f);
        path.close();
        c.drawPath(path, p);
    }

    // Vector UAV Airplane (🛩️)
    public static void drawUavAirplane(Canvas c, Paint p, Path path, float cx, float cy, float s) {
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setStrokeWidth(s * 0.1f);
        path.reset();
        path.moveTo(cx, cy - s * 0.9f); // Nose
        path.lineTo(cx + s * 0.2f, cy - s * 0.3f);
        path.lineTo(cx + s * 1.2f, cy - s * 0.1f); // Right wing tip
        path.lineTo(cx + s * 1.1f, cy + s * 0.2f);
        path.lineTo(cx + s * 0.2f, cy + s * 0.2f);
        path.lineTo(cx + s * 0.3f, cy + s * 0.8f); // Right tail
        path.lineTo(cx, cy + s * 0.7f);
        path.lineTo(cx - s * 0.3f, cy + s * 0.8f); // Left tail
        path.lineTo(cx - s * 0.2f, cy + s * 0.2f);
        path.lineTo(cx - s * 1.1f, cy + s * 0.2f);
        path.lineTo(cx - s * 1.2f, cy - s * 0.1f); // Left wing tip
        path.lineTo(cx - s * 0.2f, cy - s * 0.3f);
        path.close();
        c.drawPath(path, p);
    }

    // Vector Quadcopter Drone (🚁)
    public static void drawQuadcopter(Canvas c, Paint p, Path path, float cx, float cy, float s) {
        p.setStyle(Paint.Style.STROKE);
        p.setStrokeWidth(s * 0.18f);
        // Central body
        c.drawCircle(cx, cy, s * 0.25f, p);
        // 4 diagonal arms
        c.drawLine(cx - s * 0.6f, cy - s * 0.6f, cx + s * 0.6f, cy + s * 0.6f, p);
        c.drawLine(cx - s * 0.6f, cy + s * 0.6f, cx + s * 0.6f, cy - s * 0.6f, p);
        // 4 motor rotor circles
        c.drawCircle(cx - s * 0.6f, cy - s * 0.6f, s * 0.28f, p);
        c.drawCircle(cx + s * 0.6f, cy - s * 0.6f, s * 0.28f, p);
        c.drawCircle(cx - s * 0.6f, cy + s * 0.6f, s * 0.28f, p);
        c.drawCircle(cx + s * 0.6f, cy + s * 0.6f, s * 0.28f, p);
        p.setStyle(Paint.Style.FILL);
    }

    // Vector Flying Wing (✈️)
    public static void drawFlyingWing(Canvas c, Paint p, Path path, float cx, float cy, float s) {
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setStrokeWidth(s * 0.12f);
        path.reset();
        path.moveTo(cx, cy - s * 0.8f);
        path.lineTo(cx + s * 1.1f, cy + s * 0.5f);
        path.lineTo(cx + s * 0.8f, cy + s * 0.7f);
        path.lineTo(cx, cy + s * 0.3f);
        path.lineTo(cx - s * 0.8f, cy + s * 0.7f);
        path.lineTo(cx - s * 1.1f, cy + s * 0.5f);
        path.close();
        c.drawPath(path, p);
    }

    // Vector Star (⭐)
    public static void drawStar(Canvas c, Paint p, Path path, float cx, float cy, float r) {
        path.reset();
        float inner = r * 0.45f;
        for (int i = 0; i < 10; i++) {
            double angle = Math.PI / 5 * i - Math.PI / 2;
            float radius = (i % 2 == 0) ? r : inner;
            float x = (float) (cx + Math.cos(angle) * radius);
            float y = (float) (cy + Math.sin(angle) * radius);
            if (i == 0) path.moveTo(x, y); else path.lineTo(x, y);
        }
        path.close();
        c.drawPath(path, p);
    }
}
