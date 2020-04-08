package com.example.ablesson_1;

import android.os.Bundle;

import androidx.fragment.app.FragmentTransaction;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initMenu();

        //вывод на начальный экран главного фрагмента
        if (savedInstanceState == null) {
            CityFragment cityFragment = new CityFragment();
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(R.id.first_fragment, cityFragment);
            ft.commit();
        }
    }
}
