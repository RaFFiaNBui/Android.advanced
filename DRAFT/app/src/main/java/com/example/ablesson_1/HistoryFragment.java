package com.example.ablesson_1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //подтягиваем наш список городов
        CityFragment cityFragment = new CityFragment();
        ArrayList<String> dataCity = cityFragment.getCitiesList();
        ArrayList<String> dataTemp = cityFragment.getTemperatureList();
        //инициализируем RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.history_recycler_view);
        //подсказываем, что наш список конечный
        recyclerView.setHasFixedSize(true);
        //инициализируем LayoutManager (повторно, он был проинициализирован в fragment_main)
        if (getContext() != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        //инициализируем адаптер
        HistoryAdapter historyAdapter = new HistoryAdapter(dataCity, dataTemp);
        //устанавливаем нашему списку адаптер
        recyclerView.setAdapter(historyAdapter);

    }
}
