package com.example.ablesson_1;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;

public class BaseActivity extends AppCompatActivity implements Constants, NavigationView.OnNavigationItemSelectedListener {

    private static final int SETTINGS_CODE = 555;
    private boolean currentThemeIsDark; //true - текущая тема ТЕМНАЯ

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        //установим тему
        if (isDarkTheme()) {
            setTheme(R.style.DarkTheme);
            currentThemeIsDark = true;
        } else {
            setTheme(R.style.AppTheme);
            currentThemeIsDark = false;
        }
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        // проверяем не изменилась ли наша тема
        if (isDarkTheme() != currentThemeIsDark) {
            recreate();
        }
        super.onStart();
    }

    // Чтение настроек, параметр тема
    protected boolean isDarkTheme() {
        // Работаем через специальный класс сохранения и чтения настроек
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        //Прочитать тему, если настройка не найдена - взять по умолчанию false
        return sharedPref.getBoolean(IS_DARK_THEME, false);
    }

    // Сохранение настроек
    protected void setDarkTheme(boolean isDarkTheme) {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        // Настройки сохраняются посредством специального класса editor.
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putBoolean(IS_DARK_THEME, isDarkTheme);
        editor.apply();
    }

    //создаем меню
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        //инициализация пункта меню - Поиск
        MenuItem search = menu.findItem(R.id.search_bar);
        //строка поиска
        final SearchView searchText = (SearchView) search.getActionView();
        searchText.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            //реакция на окончание ввода поиска
            @Override
            public boolean onQueryTextSubmit(String query) {
                Snackbar.make(searchText, query, Snackbar.LENGTH_LONG).show();
                return true;
            }
            //Реагирует на нажатие каждой клавиши
            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    //привязываем активити настроек к меню
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_CODE);
                return true;
            case R.id.about_developers:
                Intent intentDev = new Intent(this, DevelopersActivity.class);
                startActivity(intentDev);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //отображение настроек
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode != SETTINGS_CODE) {
            super.onActivityResult(requestCode, resultCode, data);
            return;
        }
        if (resultCode == RESULT_OK) {
            changeSettings();
            recreate();
        }
    }

    private void changeSettings() {
        SharedPreferences sharedPref = getSharedPreferences(SHARED_PREFERENCE_KEY, MODE_PRIVATE);
        //отрисовка восхода и заката
        boolean tempSun = sharedPref.getBoolean(SUN, true);
        TableRow TRSunset = findViewById(R.id.sunset_row);
        if (TRSunset != null) {
            TableRow TRSunrise = findViewById(R.id.sunrise_row);
            TRSunset.setVisibility(tempSun ? View.VISIBLE : View.GONE);
            TRSunrise.setVisibility(tempSun ? View.VISIBLE : View.GONE);
            //отрисовка давления
            boolean tempPressure = sharedPref.getBoolean(PRESSURE, true);
            TableRow TRPressure = findViewById(R.id.pressure_row);
            TRPressure.setVisibility(tempPressure ? View.VISIBLE : View.GONE);
            //отрисовка ветра
            boolean tempWind = sharedPref.getBoolean(WIND, true);
            TableRow TRWind = findViewById(R.id.wind_row);
            TRWind.setVisibility(tempWind ? View.VISIBLE : View.GONE);
        }
    }

    void initMenu() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case (R.id.nav_Start) :
                MainFragment mainFragment = new MainFragment();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                ft.add(R.id.first_fragment, mainFragment);
                ft.commit();
                break;
            case (R.id.nav_Settings) :
                Intent intent = new Intent(this, SettingsActivity.class);
                startActivityForResult(intent, SETTINGS_CODE);
                break;
            case (R.id.nav_history) :
                FragmentTransaction ft1 = getSupportFragmentManager().beginTransaction();
                HistoryFragment historyFragment = new HistoryFragment();
                ft1.replace(R.id.first_fragment, historyFragment);
                ft1.addToBackStack("Start");
                ft1.commit();
                break;
            case (R.id.nav_Developers) :
                Intent intentDev = new Intent(this, DevelopersActivity.class);
                startActivity(intentDev);
                break;
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer != null && drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
