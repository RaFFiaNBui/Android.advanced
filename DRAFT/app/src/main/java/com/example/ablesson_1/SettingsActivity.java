package com.example.ablesson_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

public class SettingsActivity extends BaseActivity {

    private CheckBox checkBoxSun;
    private CheckBox checkBoxPressure;
    private CheckBox checkBoxWind;


    private final View.OnClickListener saveCheckBoxSettings = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            switch (v.getId()) {
                case R.id.checkBox_sun:
                    editor.putBoolean(SUN, checkBoxSun.isChecked());
                    break;
                case R.id.checkBox_pressure:
                    editor.putBoolean(PRESSURE, checkBoxPressure.isChecked());
                    break;
                case R.id.checkBox_wind:
                    editor.putBoolean(WIND, checkBoxWind.isChecked());
                    break;
            }
            editor.apply();
        }
    };
    private final View.OnClickListener exitFromSettings = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
            boolean bool = sharedPref.getBoolean(SUN, true);
            String string = Boolean.toString(bool);
            boolean boo222 = checkBoxSun.isChecked();
            String str = Boolean.toString(boo222);
            Log.d("fff", string + str);
            Intent result = new Intent();
            setResult(RESULT_OK, result);
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        //установка темы
        Switch switchTheme = findViewById(R.id.switch_theme);
        //устанавливаем свичу значение, лежащее в SharedPreferences
        switchTheme.setChecked(isDarkTheme());
        //вешаем слушатель
        switchTheme.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //сохраняем настройки темы в SharedPreferences
                setDarkTheme(isChecked);
                //и сразу перезапускаем активити с новыми настройками
                recreate();
            }
        });
        checkBoxSun = findViewById(R.id.checkBox_sun);
        checkBoxPressure = findViewById(R.id.checkBox_pressure);
        checkBoxWind = findViewById(R.id.checkBox_wind);
        //установка остальных параметров
        setOtherSettings();
        //вешаем лисенеры на чекбоксы, которые сохраняет значения свих чекбоксов в SharedPreferences
        checkBoxSun.setOnClickListener(saveCheckBoxSettings);
        checkBoxPressure.setOnClickListener(saveCheckBoxSettings);
        checkBoxWind.setOnClickListener(saveCheckBoxSettings);
        //Listener на кнопку Сохранить
        Button btnSave = findViewById(R.id.button_save);
        btnSave.setOnClickListener(exitFromSettings);
    }

    //Чтение настроек, остальные параметры
    private void setOtherSettings() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        //устанавливаем чекбоксам значение, лежащее в SharedPreferences или поумолчанию - видимы
        checkBoxSun.setChecked(sharedPref.getBoolean(SUN, true));
        checkBoxPressure.setChecked(sharedPref.getBoolean(PRESSURE, true));
        checkBoxWind.setChecked(sharedPref.getBoolean(WIND, true));
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Toast.makeText(getApplicationContext(), "Настройки сохранены", Toast.LENGTH_SHORT).show();
    }
}