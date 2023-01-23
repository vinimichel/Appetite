package com.example.appetite.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import  android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appetite.R;
import com.example.appetite.dataModels.Table;

import java.util.ArrayList;

public class Adapter_Table extends RecyclerView.Adapter<Adapter_Table.MyViewHolder> {

    Context context;
    ArrayList<Table> tables;

    public Adapter_Table(Context context, ArrayList<Table> tables ) {
        this.context = context;
        this.tables = tables;
    }

    @NonNull
    @Override
    public Adapter_Table.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recycler_view_row, parent, false);

        return new Adapter_Table.MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull Adapter_Table.MyViewHolder holder, int position) {
        holder.tvCount.setText("Tischnummer: " + tables.get(position).getID());
        holder.tvNumber.setText("Sitze: " + tables.get(position).getSeatCount());
    }

    @Override
    public int getItemCount() {
        return tables.size();
    }
    public static class MyViewHolder extends RecyclerView.ViewHolder{

        TextView tvNumber, tvCount;
        CheckBox checkBox;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            checkBox = itemView.findViewById(R.id.checkBox);
            tvNumber = itemView.findViewById(R.id.seatCount);
            tvCount = itemView.findViewById(R.id.tischNummer);
        }
    }

}

