package com.example.ablesson1;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ablesson1.history.App;
import com.example.ablesson1.history.HistoryDao;
import com.example.ablesson1.history.HistorySource;

public class HistoryFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_history, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //неиспользуется после подключения Room
/*        //подтягиваем наш список городов
        ArrayList<String> dataCity = CityFragment.getCitiesList();
        ArrayList<String> dataTemp = CityFragment.getTemperatureList();
        if(dataCity.size() == 0){
            dataCity.add(getString(R.string.story_array_empty));
            dataTemp.add(" ");
        }*/

        //инициализируем RecyclerView
        RecyclerView recyclerView = view.findViewById(R.id.history_recycler_view);
        //подсказываем, что наш список конечный
        recyclerView.setHasFixedSize(true);
        //инициализируем LayoutManager (повторно, он был проинициализирован в fragment_main)
        if (getContext() != null) {
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), RecyclerView.VERTICAL, false));
        }
        //инициализируем средства для работы с БД Room
        HistoryDao historyDao = App.getInstance().getHistoryDao();
        HistorySource historySource = new HistorySource(historyDao);
        //инициализируем адаптер
        HistoryAdapter historyAdapter = new HistoryAdapter(historySource);
        //устанавливаем нашему списку адаптер
        recyclerView.setAdapter(historyAdapter);
    }
}
