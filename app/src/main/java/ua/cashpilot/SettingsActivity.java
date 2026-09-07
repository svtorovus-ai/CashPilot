package ua.cashpilot;

import android.app.*;
import android.appwidget.*;
import android.content.*;
import android.graphics.Color;
import android.os.Bundle;
import android.view.*;
import android.widget.*;
import android.net.Uri;

public class SettingsActivity extends Activity {
    EditText url, token, install;
    LinearLayout root;
    @Override public void onCreate(Bundle b) {
        super.onCreate(b);
        root=new LinearLayout(this); root.setOrientation(LinearLayout.VERTICAL); root.setPadding(dp(20),dp(24),dp(20),dp(32)); root.setBackgroundResource(R.drawable.settings_bg);
        TextView title=new TextView(this); title.setText("CashPilot"); title.setTextColor(Color.parseColor("#B8D7FF")); title.setTypeface(null, android.graphics.Typeface.BOLD); title.setTextSize(32); root.addView(title);
        TextView hint=new TextView(this); hint.setText("Підключення до telemetry-сервера. Віджет лише читає сервер; зміни статусів зберігаються локально."); hint.setTextColor(Color.parseColor("#8B97A5")); hint.setPadding(0,16,0,32); hint.setTextSize(14); root.addView(hint);
        url=field("URL сервера", "http://204.168.225.114:8787"); token=field("Токен telemetry", ""); install=field("Ідентифікатор користувача/install_id", "");
        EditText statsUrl = field("URL статистики сервера (необов'язково)", "Якщо порожньо — відкриється URL telemetry-сервера");
        TextView ratesTitle = new TextView(this); ratesTitle.setText("Ставки додаткової винагороди, грн"); ratesTitle.setTextColor(Color.parseColor("#B8D7FF")); ratesTitle.setTypeface(null, android.graphics.Typeface.BOLD); ratesTitle.setPadding(4, 28, 0, 8); ratesTitle.setTextSize(14); root.addView(ratesTitle);
        EditText workRate = field("Працював", "100000");
        EditText dutyRate = field("Чергував / без даних", "30000");
        TextView modeTitle = new TextView(this); modeTitle.setText("Формат ставки"); modeTitle.setTextColor(Color.parseColor("#8B97A5")); modeTitle.setTextSize(13); modeTitle.setTypeface(null, android.graphics.Typeface.BOLD); modeTitle.setPadding(4, 16, 0, 4); root.addView(modeTitle);
        RadioGroup rateMode = new RadioGroup(this); rateMode.setOrientation(RadioGroup.HORIZONTAL);
        RadioButton monthly = new RadioButton(this); monthly.setText("За місяць"); monthly.setTextColor(Color.WHITE);
        RadioButton daily = new RadioButton(this); daily.setText("За день"); daily.setTextColor(Color.WHITE);
        rateMode.addView(monthly); rateMode.addView(daily); root.addView(rateMode);
        LinearLayout rateButtons = new LinearLayout(this); rateButtons.setOrientation(LinearLayout.HORIZONTAL);
        Button military = new Button(this); military.setText("ЗСУ 100000 / 30000"); military.setTextColor(Color.WHITE); military.setBackgroundResource(R.drawable.settings_button_bg);
        rateButtons.addView(military, new LinearLayout.LayoutParams(0, -2, 1)); root.addView(rateButtons);
        Button save=new Button(this); save.setText("Зберегти"); save.setTextColor(Color.WHITE); save.setBackgroundResource(R.drawable.settings_button_bg);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2); lp.setMargins(0, 64, 0, 0); save.setLayoutParams(lp); root.addView(save);
        TextView note=new TextView(this); note.setText("Після збереження додай віджет CashPilot на головний екран."); note.setTextColor(Color.parseColor("#8B97A5")); note.setPadding(0,24,0,0); note.setTextSize(12); note.setGravity(Gravity.CENTER); root.addView(note);
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true); scroll.addView(root); setContentView(scroll);
        android.content.SharedPreferences p=getSharedPreferences(CashPilotWidget.PREFS,0); url.setText(p.getString("url","http://204.168.225.114:8787"));token.setText(p.getString("token",""));install.setText(p.getString("install",""));statsUrl.setText(p.getString("stats_url",""));
        workRate.setText(String.valueOf(p.contains("rate_work") ? p.getInt("rate_work", 100000) : p.getInt("rate_work_k", 100) * 1000)); dutyRate.setText(String.valueOf(p.contains("rate_duty") ? p.getInt("rate_duty", 30000) : p.getInt("rate_duty_k", 30) * 1000));
        boolean dailyMode = p.getBoolean("rate_mode_daily", false); daily.setChecked(dailyMode); monthly.setChecked(!dailyMode);
        military.setOnClickListener(v -> { workRate.setText("100000"); dutyRate.setText("30000"); });
        save.setOnClickListener(v->{
            int work = parseRate(workRate.getText().toString(), 100000), duty = parseRate(dutyRate.getText().toString(), 30000);
            p.edit().putString("url",url.getText().toString().trim()).putString("token",token.getText().toString().trim()).putString("install",install.getText().toString().trim()).putString("stats_url",statsUrl.getText().toString().trim()).putInt("rate_work", work).putInt("rate_duty", duty).putBoolean("rate_mode_daily", daily.isChecked()).apply(); updateWidgets(); Toast.makeText(this,"Збережено",Toast.LENGTH_SHORT).show();
        });
        Button serverStats = new Button(this); serverStats.setText("Відкрити статистику сервера"); serverStats.setTextColor(Color.WHITE); serverStats.setBackgroundResource(R.drawable.settings_button_bg);
        LinearLayout.LayoutParams statsLp = new LinearLayout.LayoutParams(-1, -2); statsLp.setMargins(0, 16, 0, 0); root.addView(serverStats, statsLp);
        serverStats.setOnClickListener(v -> { String address = statsUrl.getText().toString().trim(); if (address.isEmpty()) address = url.getText().toString().trim(); if (address.isEmpty()) { Toast.makeText(this, "Вкажи URL сервера", Toast.LENGTH_SHORT).show(); return; } try { startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(address))); } catch (Exception e) { Toast.makeText(this, "Не вдалося відкрити URL", Toast.LENGTH_SHORT).show(); } });
    }
    EditText field(String label,String value){
        TextView l=new TextView(this); l.setText(label); l.setTextColor(Color.parseColor("#8B97A5")); l.setPadding(4,16,0,8); l.setTextSize(13); l.setTypeface(null, android.graphics.Typeface.BOLD); root.addView(l);
        EditText e=new EditText(this); e.setHint(value); e.setSingleLine(true); e.setTextColor(Color.WHITE); e.setHintTextColor(Color.GRAY);
        e.setBackgroundResource(R.drawable.settings_field_bg); e.setPadding(dp(18),dp(16),dp(18),dp(16));
        root.addView(e); return e;
    }
    int dp(int value) { return Math.round(value * getResources().getDisplayMetrics().density); }
    void updateWidgets(){AppWidgetManager m=AppWidgetManager.getInstance(this);int[] ids=m.getAppWidgetIds(new ComponentName(this,CashPilotWidget.class));new CashPilotWidget().onUpdate(this,m,ids);}
    int parseRate(String value, int fallback) { try { int parsed = Integer.parseInt(value.trim()); return parsed >= 0 && parsed <= 100000000 ? parsed : fallback; } catch (Exception e) { return fallback; } }
}
