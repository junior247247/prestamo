package com.dantsu.thermalprinter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.Format;
import java.text.SimpleDateFormat;

import io.realm.RealmResults;

public class HistorialAdapter extends RecyclerView.Adapter<HistorialAdapter.ViewHolder> {

    RealmResults<Pagos> realmResults;

    public HistorialAdapter(RealmResults<Pagos> realmResults){
        this.realmResults=realmResults;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_historial,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
    Pagos pagos=realmResults.get(position);
    if(pagos.getPagado()>0){
        holder.itemView.setVisibility(View.VISIBLE);
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss");
        holder.textViewFecha.setText(pagos.getDate().toString());
        holder.textViewMonto.setText(pagos.getMonto()+" $RD");
    }else{
        holder.itemView.setVisibility(View.GONE);
    }

    }

    @Override
    public int getItemCount() {
        return realmResults.size();
    }

    public class  ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewMonto,textViewFecha;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewMonto=itemView.findViewById(R.id.textViewMonto);
            this.textViewFecha=itemView.findViewById(R.id.textViewFecha);
        }
    }
}
