package com.example.ablesson_1;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;


public class CitiesAdapter extends RecyclerView.Adapter<CitiesAdapter.RecyclerViewHolder> {

    private String[] data;
    private RecyclerItemClickListener clickListener = null;

    CitiesAdapter(String[] data) {
        this.data = data;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_cities, parent, false);
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

    void setClickListener(RecyclerItemClickListener clickListener) {
        this.clickListener = clickListener;
    }

    class RecyclerViewHolder extends RecyclerView.ViewHolder {

        private TextView textView;

        RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.item_textView);
        }

        void setData(final String data) {
            textView.setText(data);
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickListener.onItemClick(data);
                    Snackbar.make(v, "Вы выбрали " + data, Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
            });
        }
    }

    public interface RecyclerItemClickListener {
        void onItemClick(String data);
    }
}
