package com.example.ablesson_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class WeekAdapter extends RecyclerView.Adapter<WeekAdapter.RecyclerViewHolder> {

    private String[] data;

    WeekAdapter(String[] data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_week, parent, false);
        return new RecyclerViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder holder, int position) {
        holder.setData(data[position]);
    }

    @Override
    public int getItemCount() {
        return data.length;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView textViewDate;
        private TextView textViewDay;
        private TextView textViewTDay;
        private TextView textViewTNight;
        private ImageView imageView;

        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            textViewDate = itemView.findViewById(R.id.item_date);
            textViewDay = itemView.findViewById(R.id.item_day);
            textViewTDay = itemView.findViewById(R.id.item_t_day);
            textViewTNight = itemView.findViewById(R.id.item_t_night);
            imageView = itemView.findViewById(R.id.item_img);
        }

        void setData(final String data) {

            textViewDate.setText("22.01");
            textViewDay.setText(data);
            textViewTDay.setText("+20");
            textViewTNight.setText("+10");
            imageView.setImageResource(R.drawable.t22);
        }
    }
}
