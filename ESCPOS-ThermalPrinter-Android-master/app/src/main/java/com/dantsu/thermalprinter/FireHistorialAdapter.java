package com.dantsu.thermalprinter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import java.text.SimpleDateFormat;

public class FireHistorialAdapter extends FirestoreRecyclerAdapter<PagosFire,FireHistorialAdapter.ViewHolder> {


    public FireHistorialAdapter(@NonNull FirestoreRecyclerOptions<PagosFire> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull PagosFire model) {
        if(model.getPagado()>0){
            holder.itemView.setVisibility(View.VISIBLE);
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd - HH:mm:ss");
            holder.textViewFecha.setText(format.format(model.getDate()));
            holder.textViewMonto.setText(model.getPagado()+" $RD");
        }else{
            holder.itemView.setVisibility(View.INVISIBLE);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_historial,parent,false));
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewMonto,textViewFecha;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewMonto=itemView.findViewById(R.id.textViewMonto);
            this.textViewFecha=itemView.findViewById(R.id.textViewFecha);
        }
    }
}
