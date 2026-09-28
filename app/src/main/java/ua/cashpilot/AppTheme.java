package ua.cashpilot;

import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import java.util.Objects;

public enum AppTheme {
    DARK_CLASSIC("dark_classic", "🌌 CashPilot Dark (Класична)",
            0xFF080C12, 0xFF080C12, 0xE51D2532, 0xF003060B, 0x667A8795,
            0xFF9FADBD, 0xFFF2F6FB, 0xFFB8E7FF, 0xFF9BA9BA,
            0x704C5866, 0x40202730, 0xFFD6E3EE,
            0xE51B7652, 0xF005241C, 0xFFB7F5D8, 0x666F879B, // work
            0xE58A6508, 0xF0332204, 0xFFFFE6A0, 0x666F879B, // duty
            0xE5225C9B, 0xF00B2344, 0xFFB9DEFF, 0x666F879B, // vacation
            0xA5161C25, 0xF003060A, 0xFFC5CED8, 0x666F879B), // idle

    UAV_DRONE("uav_drone", "🛸 БпЛА / Дрони (UAV Tactical)",
            0xFF060D0B, 0xFF060D0B, 0xF00C1B16, 0xF0040A08, 0x881E4B38,
            0xFF00E5FF, 0xFFE0F7FA, 0xFF00FFCC, 0xFF76FF03,
            0x80103A2B, 0x50061A13, 0xFF00FFCC,
            0xE50B6E42, 0xF0032918, 0xFFB2FFD6, 0x9900FFCC, // work (PD-2)
            0xE58F6000, 0xF03B2600, 0xFFFFE57F, 0x99FFD54F, // duty (Лелека-100)
            0xE500579B, 0xF0002244, 0xFF80D8FF, 0x9940C4FF, // vacation (FPV)
            0x9910201A, 0xF0060E0B, 0xFFB0BEC5, 0x6637474F), // idle

    HELLO_KITTY("hello_kitty", "🎀 Hello Kitty (Cute Pink)",
            0xFF26121C, 0xFF26121C, 0xF03B1E2E, 0xF0211019, 0x88FF80AB,
            0xFFFFB2DD, 0xFFFFF0F5, 0xFFFF80AB, 0xFFFFC2E2,
            0x808C2053, 0x50400C24, 0xFFFFA4D2,
            0xE5C2185B, 0xF04A0B22, 0xFFFFD1DC, 0x99FF80AB, // work (Heart)
            0xE5AD1457, 0xF03D001D, 0xFFFFE0B2, 0x99FFB74D, // duty (Bow)
            0xE57B1FA2, 0xF02A004A, 0xFFE1BEE7, 0x99CE93D8, // vacation (Star)
            0x993D1B2D, 0xF01C0A13, 0xFFF8BBD0, 0x66880E4F), // idle

    PIXEL_CAMO("pixel_camo", "🪖 Піксель ЗСУ (MM-14 Camo)",
            0xFF0E140E, 0xFF0E140E, 0xF01A261A, 0xF00A0F0A, 0x884D6A42,
            0xFFC5D3A3, 0xFFF1F5E6, 0xFFFFD54F, 0xFFAEC49A,
            0x802D3E29, 0x50152013, 0xFFD8E4BC,
            0xE52E5B27, 0xF010260D, 0xFFC8E6C9, 0x9981C784, // work (Double Chevron)
            0xE57F6100, 0xF0332600, 0xFFFFF59D, 0x99FFF176, // duty (Single Chevron)
            0xE51D526A, 0xF009222E, 0xFFB2EBF2, 0x994DD0E1, // vacation (Trident)
            0x99192419, 0xF00D130D, 0xFFCFD8DC, 0x66455A64), // idle

    NEON_RADAR("neon_radar", "📡 Векторний Радар (Cyber Radar)",
            0xFF000E06, 0xFF000E06, 0xF0001A0B, 0xF0000A04, 0x8800FF66,
            0xFF00FF66, 0xFFE0FFEC, 0xFF00FFCC, 0xFF69FF94,
            0x80003D18, 0x50001A0A, 0xFF00FF66,
            0xE5006622, 0xF000260B, 0xFFB3FFCC, 0x9900FF66, // work (Target Lock)
            0xE5856B00, 0xF0332900, 0xFFFFE066, 0x99FFD700, // duty (Diamond)
            0xE5005B85, 0xF0002133, 0xFF99ECFF, 0x9933CCFF, // vacation (X-Target)
            0x9900220F, 0xF0000F06, 0xFF99FFC6, 0x66006622); // idle

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

    // Day status colors
    public final int workGradTop, workGradBottom, workTextColor, workBorderColor;
    public final int dutyGradTop, dutyGradBottom, dutyTextColor, dutyBorderColor;
    public final int vacationGradTop, vacationGradBottom, vacationTextColor, vacationBorderColor;
    public final int idleGradTop, idleGradBottom, idleTextColor, idleBorderColor;

    AppTheme(String key, String title, int statusNavBgColor, int outerBgColor, int bgGradientTop, int bgGradientBottom, int bgBorderColor,
             int subtitleColor, int titleColor, int moneyColor, int weekdaysColor,
             int btnGradTop, int btnGradBottom, int btnStrokeColor,
             int workGradTop, int workGradBottom, int workTextColor, int workBorderColor,
             int dutyGradTop, int dutyGradBottom, int dutyTextColor, int dutyBorderColor,
             int vacationGradTop, int vacationGradBottom, int vacationTextColor, int vacationBorderColor,
             int idleGradTop, int idleGradBottom, int idleTextColor, int idleBorderColor) {
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

    public void drawWatermark(Canvas c, Paint p, Path path, float width, float height, float d) {
        float cx = width / 2f, cy = height * 0.58f;
        p.setStyle(Paint.Style.STROKE);
        p.setShader(null);

        if (this == UAV_DRONE) {
            // Draw UAV Tactical HUD Grid & PD-2 Watermark Silhouette
            p.setColor(0x1800FFCC);
            p.setStrokeWidth(d * 1.5f);
            // Concentric HUD Circles
            c.drawCircle(cx, cy, d * 140, p);
            c.drawCircle(cx, cy, d * 80, p);
            // Crosshairs with ticks
            c.drawLine(cx - d * 160, cy, cx + d * 160, cy, p);
            c.drawLine(cx, cy - d * 160, cx, cy + d * 160, p);
            for (int i = -3; i <= 3; i++) {
                if (i != 0) {
                    c.drawLine(cx + i * d * 40, cy - d * 6, cx + i * d * 40, cy + d * 6, p);
                    c.drawLine(cx - d * 6, cy + i * d * 40, cx + d * 6, cy + i * d * 40, p);
                }
            }

            // Draw PD-2 UAV Silhouette
            p.setColor(0x2200FFCC);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            p.setStrokeWidth(d * 2f);
            path.reset();
            // High Wings with winglets
            path.moveTo(cx, cy - d * 55); // Nose
            path.lineTo(cx + d * 14, cy - d * 25);
            path.lineTo(cx + d * 130, cy - d * 15); // Right wing tip
            path.lineTo(cx + d * 130, cy - d * 25); // Winglet
            path.lineTo(cx + d * 118, cy); // Wing trailing edge
            path.lineTo(cx + d * 20, cy);
            // Twin Tail Booms & Fuselage
            path.lineTo(cx + d * 35, cy + d * 70); // Right boom end
            path.lineTo(cx + d * 18, cy + d * 70); // Right tail-fin
            path.lineTo(cx, cy + d * 58); // Inverted V-tail center
            path.lineTo(cx - d * 18, cy + d * 70); // Left tail-fin
            path.lineTo(cx - d * 35, cy + d * 70); // Left boom end
            path.lineTo(cx - d * 20, cy);
            path.lineTo(cx - d * 118, cy); // Left wing trailing edge
            path.lineTo(cx - d * 130, cy - d * 25); // Winglet
            path.lineTo(cx - d * 130, cy - d * 15); // Left wing tip
            path.lineTo(cx - d * 14, cy - d * 25);
            path.close();
            c.drawPath(path, p);

            // Propeller Arc
            p.setStyle(Paint.Style.STROKE);
            p.setColor(0x3300FFCC);
            c.drawCircle(cx, cy - d * 52, d * 16, p);

        } else if (this == HELLO_KITTY) {
            // Draw Hello Kitty Bow Silhouette
            p.setColor(0x25FF80AB);
            p.setStyle(Paint.Style.FILL_AND_STROKE);
            p.setStrokeWidth(d * 2f);

            // Bow center knot
            c.drawCircle(cx, cy, d * 22, p);

            // Left loop
            path.reset();
            path.moveTo(cx - d * 18, cy - d * 10);
            path.cubicTo(cx - d * 70, cy - d * 55, cx - d * 110, cy - d * 10, cx - d * 85, cy + d * 20);
            path.cubicTo(cx - d * 65, cy + d * 45, cx - d * 20, cy + d * 18, cx - d * 18, cy + d * 10);
            path.close();
            c.drawPath(path, p);

            // Right loop
            path.reset();
            path.moveTo(cx + d * 18, cy - d * 10);
            path.cubicTo(cx + d * 70, cy - d * 55, cx + d * 110, cy - d * 10, cx + d * 85, cy + d * 20);
            path.cubicTo(cx + d * 65, cy + d * 45, cx + d * 20, cy + d * 18, cx + d * 18, cy + d * 10);
            path.close();
            c.drawPath(path, p);

            // Cute floating hearts around bow
            p.setColor(0x18FF80AB);
            drawHeart(c, p, path, cx - d * 100, cy - d * 80, d * 18);
            drawHeart(c, p, path, cx + d * 110, cy - d * 70, d * 22);
            drawHeart(c, p, path, cx + d * 90, cy + d * 90, d * 16);
            drawHeart(c, p, path, cx - d * 110, cy + d * 85, d * 20);

        } else if (this == PIXEL_CAMO) {
            // Draw Digital Camo Pixel Grid (MM-14 Style)
            p.setStyle(Paint.Style.FILL);
            p.setColor(0x14C5D3A3);
            float blockSize = d * 18;
            for (int r = 0; r < 14; r++) {
                for (int col = 0; col < 12; col++) {
                    if ((r * 3 + col * 7) % 5 == 0 || (r * 2 + col * 3) % 7 == 0) {
                        float bx = cx - d * 120 + col * blockSize;
                        float by = cy - d * 120 + r * blockSize;
                        c.drawRect(bx, by, bx + blockSize, by + blockSize, p);
                    }
                }
            }

            // Central Trident Emblem outline
            p.setStyle(Paint.Style.STROKE);
            p.setColor(0x22C5D3A3);
            p.setStrokeWidth(d * 3f);
            path.reset();
            path.moveTo(cx, cy - d * 60); path.lineTo(cx, cy + d * 60); // Center pillar
            path.moveTo(cx - d * 35, cy - d * 40); path.lineTo(cx - d * 35, cy + d * 10);
            path.cubicTo(cx - d * 35, cy + d * 45, cx - d * 10, cy + d * 55, cx, cy + d * 55); // Left arc
            path.moveTo(cx + d * 35, cy - d * 40); path.lineTo(cx + d * 35, cy + d * 10);
            path.cubicTo(cx + d * 35, cy + d * 45, cx + d * 10, cy + d * 55, cx, cy + d * 55); // Right arc
            c.drawPath(path, p);

        } else if (this == NEON_RADAR) {
            // Draw Radar Oscilloscope Grid
            p.setColor(0x2200FF66);
            p.setStrokeWidth(d * 1.5f);
            c.drawCircle(cx, cy, d * 150, p);
            c.drawCircle(cx, cy, d * 100, p);
            c.drawCircle(cx, cy, d * 50, p);
            c.drawLine(cx - d * 170, cy, cx + d * 170, cy, p);
            c.drawLine(cx, cy - d * 170, cx, cy + d * 170, p);

            // Radar Sweep Beam Area
            p.setStyle(Paint.Style.FILL);
            p.setColor(0x0C00FF66);
            path.reset();
            path.moveTo(cx, cy);
            path.lineTo(cx + d * 150, cy - d * 60);
            path.arcTo(cx - d * 150, cy - d * 150, cx + d * 150, cy + d * 150, -22, -60, false);
            path.close();
            c.drawPath(path, p);
        }
        p.setStyle(Paint.Style.FILL);
    }

    public void drawDayIcon(Canvas c, Paint p, Path path, String state, float cx, float cy, float d) {
        if ("idle".equals(state) && this == DARK_CLASSIC) return;

        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setStrokeWidth(d * 1.2f);

        if (this == UAV_DRONE) {
            if ("work".equals(state)) {
                // PD-2 / Shark UAV Silhouette
                p.setColor(workTextColor);
                path.reset();
                path.moveTo(cx, cy - d * 7); // Nose
                path.lineTo(cx + d * 2, cy - d * 3);
                path.lineTo(cx + d * 11, cy - d * 1); // Right wing tip
                path.lineTo(cx + d * 10, cy + d * 2);
                path.lineTo(cx + d * 2, cy + d * 2);
                path.lineTo(cx + d * 3, cy + d * 7); // Right tail
                path.lineTo(cx, cy + d * 6);
                path.lineTo(cx - d * 3, cy + d * 7); // Left tail
                path.lineTo(cx - d * 2, cy + d * 2);
                path.lineTo(cx - d * 10, cy + d * 2);
                path.lineTo(cx - d * 11, cy - d * 1); // Left wing tip
                path.lineTo(cx - d * 2, cy - d * 3);
                path.close();
                c.drawPath(path, p);
            } else if ("duty".equals(state)) {
                // Лелека-100 / Valkyrie Swept Wing
                p.setColor(dutyTextColor);
                path.reset();
                path.moveTo(cx, cy - d * 6);
                path.lineTo(cx + d * 10, cy + d * 4);
                path.lineTo(cx + d * 7, cy + d * 5);
                path.lineTo(cx, cy + d * 2);
                path.lineTo(cx - d * 7, cy + d * 5);
                path.lineTo(cx - d * 10, cy + d * 4);
                path.close();
                c.drawPath(path, p);
            } else if ("vacation".equals(state)) {
                // FPV Quadcopter X-frame
                p.setColor(vacationTextColor);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(d * 1.5f);
                c.drawLine(cx - d * 6, cy - d * 6, cx + d * 6, cy + d * 6, p);
                c.drawLine(cx - d * 6, cy + d * 6, cx + d * 6, cy - d * 6, p);
                c.drawCircle(cx - d * 6, cy - d * 6, d * 2.5f, p);
                c.drawCircle(cx + d * 6, cy - d * 6, d * 2.5f, p);
                c.drawCircle(cx - d * 6, cy + d * 6, d * 2.5f, p);
                c.drawCircle(cx + d * 6, cy + d * 6, d * 2.5f, p);
            }
        } else if (this == HELLO_KITTY) {
            if ("work".equals(state)) {
                p.setColor(workTextColor);
                drawHeart(c, p, path, cx, cy, d * 7);
            } else if ("duty".equals(state)) {
                p.setColor(dutyTextColor);
                // Cute Bow
                c.drawCircle(cx, cy, d * 2.5f, p);
                path.reset();
                path.moveTo(cx - d * 2, cy - d * 1);
                path.lineTo(cx - d * 8, cy - d * 5);
                path.lineTo(cx - d * 8, cy + d * 5);
                path.lineTo(cx - d * 2, cy + d * 1);
                path.close();
                c.drawPath(path, p);
                path.reset();
                path.moveTo(cx + d * 2, cy - d * 1);
                path.lineTo(cx + d * 8, cy - d * 5);
                path.lineTo(cx + d * 8, cy + d * 5);
                path.lineTo(cx + d * 2, cy + d * 1);
                path.close();
                c.drawPath(path, p);
            } else if ("vacation".equals(state)) {
                p.setColor(vacationTextColor);
                drawStar(c, p, path, cx, cy, d * 6);
            }
        } else if (this == PIXEL_CAMO) {
            if ("work".equals(state)) {
                p.setColor(workTextColor);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(d * 2f);
                path.reset();
                path.moveTo(cx - d * 6, cy - d * 2); path.lineTo(cx, cy - d * 7); path.lineTo(cx + d * 6, cy - d * 2);
                path.moveTo(cx - d * 6, cy + d * 3); path.lineTo(cx, cy - d * 2); path.lineTo(cx + d * 6, cy + d * 3);
                c.drawPath(path, p);
            } else if ("duty".equals(state)) {
                p.setColor(dutyTextColor);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(d * 2f);
                path.reset();
                path.moveTo(cx - d * 6, cy + d * 1); path.lineTo(cx, cy - d * 4); path.lineTo(cx + d * 6, cy + d * 1);
                c.drawPath(path, p);
            } else if ("vacation".equals(state)) {
                p.setColor(vacationTextColor);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(d * 1.5f);
                c.drawCircle(cx, cy, d * 5.5f, p);
                c.drawCircle(cx, cy, d * 2f, p);
            }
        } else if (this == NEON_RADAR) {
            if ("work".equals(state)) {
                p.setColor(workTextColor);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(d * 1.5f);
                c.drawCircle(cx, cy, d * 6f, p);
                c.drawCircle(cx, cy, d * 2f, p);
                c.drawLine(cx - d * 8, cy, cx + d * 8, cy, p);
                c.drawLine(cx, cy - d * 8, cx, cy + d * 8, p);
            } else if ("duty".equals(state)) {
                p.setColor(dutyTextColor);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(d * 1.5f);
                path.reset();
                path.moveTo(cx, cy - d * 6); path.lineTo(cx + d * 6, cy); path.lineTo(cx, cy + d * 6); path.lineTo(cx - d * 6, cy); path.close();
                c.drawPath(path, p);
            } else if ("vacation".equals(state)) {
                p.setColor(vacationTextColor);
                p.setStyle(Paint.Style.STROKE);
                p.setStrokeWidth(d * 1.5f);
                c.drawLine(cx - d * 5, cy - d * 5, cx + d * 5, cy + d * 5, p);
                c.drawLine(cx - d * 5, cy + d * 5, cx + d * 5, cy - d * 5, p);
                c.drawCircle(cx, cy, d * 6f, p);
            }
        }
        p.setStyle(Paint.Style.FILL);
    }

    private static void drawHeart(Canvas c, Paint p, Path path, float cx, float cy, float s) {
        path.reset();
        path.moveTo(cx, cy + s * 0.7f);
        path.cubicTo(cx - s * 1.2f, cy, cx - s * 1.1f, cy - s * 0.9f, cx, cy - s * 0.3f);
        path.cubicTo(cx + s * 1.1f, cy - s * 0.9f, cx + s * 1.2f, cy, cx, cy + s * 0.7f);
        path.close();
        c.drawPath(path, p);
    }

    private static void drawStar(Canvas c, Paint p, Path path, float cx, float cy, float r) {
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
