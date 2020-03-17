package com.example.ablesson_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryHolder> {

    private ArrayList<String> dataCity;
    private ArrayList<String> dataTemp;

    HistoryAdapter(ArrayList<String> dataSity, ArrayList<String> dataTemp) {
        this.dataCity = dataSity;
        this.dataTemp = dataTemp;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_history, parent, false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        holder.setData(dataCity.get(position), dataTemp.get(position));
    }

    @Override
    public int getItemCount() {
        return dataCity.size();
    }

    class HistoryHolder extends RecyclerView.ViewHolder {

        private TextView textViewCity;
        private TextView textViewTemp;

        HistoryHolder(@NonNull View itemView) {
            super(itemView);
            textViewCity = itemView.findViewById(R.id.item_history_city);
            textViewTemp = itemView.findViewById(R.id.item_history_temp);
        }

        void setData(final String city, final String temp) {
            textViewCity.setText(city);
            textViewTemp.setText(temp);
        }
    }
}
