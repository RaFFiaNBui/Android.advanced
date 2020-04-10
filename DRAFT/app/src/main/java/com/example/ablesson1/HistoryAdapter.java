package com.example.ablesson1;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ablesson1.history.HistorySource;
import com.example.ablesson1.history.LineOfHistory;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

//реализация с ArrayList неиспользуется после подключения Room
public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

    //private ArrayList<String> dataCity;
    //private ArrayList<String> dataTemp;

    private Activity activity;
    //источник данных
    private HistorySource dataSource;
    //позиция в списке, на которой было нажато меню
    private long menuPosition;

    HistoryAdapter(/*ArrayList<String> dataSity, ArrayList<String> dataTemp*/
            HistorySource dataSource, Activity activity) {
        /*this.dataCity = dataSity;
        this.dataTemp = dataTemp;*/
        this.dataSource = dataSource;
        this.activity = activity;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        //holder.setData(dataCity.get(position), dataTemp.get(position));
        List<LineOfHistory> lines = dataSource.getLines();
        LineOfHistory lineOfHistory = lines.get(position);
        holder.setData(lineOfHistory.cityName, lineOfHistory.cityTemp, lineOfHistory.date);

        //определяем какой пункт меню был нажат
        holder.cardView.setOnLongClickListener(view -> {
            menuPosition = position;
            return false;
        });

        //регистрируем контекстное меню
        if (activity != null) {
            activity.registerForContextMenu(holder.cardView);
        }
    }

    @Override
    /*public int getItemCount() {
        return dataCity.size();
    }*/
    public int getItemCount() {
        return (int) dataSource.getCountLines();
    }

    public long getMenuPosition() {
        return menuPosition;
    }

    class HistoryHolder extends RecyclerView.ViewHolder {

        private TextView textViewCity;
        private TextView textViewTemp;
        private TextView textViewDate;
        View cardView;

        HistoryHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView;
            textViewCity = itemView.findViewById(R.id.item_history_city);
            textViewTemp = itemView.findViewById(R.id.item_history_temp);
            textViewDate = itemView.findViewById(R.id.item_history_date);
        }

        void setData(final String city, final String temp, long date) {
            textViewCity.setText(city);
            textViewTemp.setText(temp);
            SimpleDateFormat smp = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
            String str = String.format(Locale.getDefault(), "%s", smp.format(date * 1000L));
            textViewDate.setText(str);
        }
    }
}
