package com.example.ablesson1;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ablesson1.history.App;
import com.example.ablesson1.history.HistoryDao;
import com.example.ablesson1.history.HistorySource;
import com.example.ablesson1.history.LineOfHistory;

import java.util.Objects;

public class HistoryFragment extends Fragment {

    private HistoryAdapter historyAdapter;
    private HistorySource historySource;

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
        historySource = new HistorySource(historyDao);
        //инициализируем адаптер
        historyAdapter = new HistoryAdapter(historySource, getActivity());
        //устанавливаем нашему списку адаптер
        recyclerView.setAdapter(historyAdapter);
    }

    @Override
    public void onCreateContextMenu(@NonNull ContextMenu menu, @NonNull View v, @Nullable ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        MenuInflater inflater = Objects.requireNonNull(getActivity()).getMenuInflater();
        inflater.inflate(R.menu.context_menu_history, menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.history_filter_by_city:
                historySource.getHistoryByName("Кострома");
                historyAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "111111111111", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.history_filter_by_date:
                historySource.getHistoryByDate(1586390400);
                historyAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "22222222222222", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.history_filter_by_temp:
                historySource.getHistoryByTemperature("14");
                historyAdapter.notifyDataSetChanged();
                Toast.makeText(getContext(), "333333333333", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.history_delete:
                LineOfHistory lineOfHistory = historySource.getLines().get((int) historyAdapter.getMenuPosition());
                historySource.deleteLine(lineOfHistory);
                historyAdapter.notifyItemRemoved((int) historyAdapter.getMenuPosition());
                Toast.makeText(getContext(), "44444444444444", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.history_delete_all:
                Toast.makeText(getContext(), "555555555555", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onContextItemSelected(item);
    }
}
