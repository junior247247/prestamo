package com.dantsu.thermalprinter;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Date;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmResults;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ViewHolder> {
        RealmResults<Cliente> list;
        Context context;
        Realm realm=Realm.getDefaultInstance();
        RealmResults<Pagos> realmResultsPayment;
        PagosTotal pagosTotal;
    public  ClientAdapter(RealmResults<Cliente> list,Context context){
        this.list=list;
        this.context=context;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_items_clients,parent,false));
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Cliente cliente=list.get(position);

        holder.textViewAdress.setText(cliente.getAddress().toUpperCase());
        holder.textViewName.setText(cliente.getName().toUpperCase() +" "+ cliente.getLastname().toUpperCase());
        holder.textViewPhone.setText(cliente.getPhone());
        holder.textViewId.setText(cliente.getId()+" • ");
       holder.linerItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                realmResultsPayment=realm.where(Pagos.class).equalTo("idClient",cliente.getId()).findAll();
                AlertDialog.Builder alet= new AlertDialog.Builder(context).setMessage(cliente.getName().toUpperCase() +" "+ cliente.getLastname().toUpperCase()).setTitle("Prestamo").setPositiveButton("Pago", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if(realmResultsPayment.size()>0){
                            Intent intent= new Intent(context,UpdatePaymentActivity.class);
                            int id=cliente.getId();
                            intent.putExtra("id",String.valueOf(id));
                            context.startActivity(intent);

                        }else{
                            Intent intent= new Intent(context,PagosDetailsActivity.class);
                            int id=cliente.getId();
                            intent.putExtra("id",String.valueOf(id));
                            intent.putExtra("displayName",cliente.getName()+" "+cliente.getLastname());
                            context.startActivity(intent);
                        }
                    }
                }).setNegativeButton("Historial", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent= new Intent(context,HistoryActivity.class);
                        int id=cliente.getId();
                        intent.putExtra("id",String.valueOf(id));
                        context.startActivity(intent);
                    }
                }).setNeutralButton("Eliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(context).setTitle("Eliminando Cliente").setMessage("¿Realmente deceas eliminar este cliente?").setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pagosTotal=realm.where(PagosTotal.class).equalTo("idCliente",cliente.getId()).findFirst();
                                realm.beginTransaction();
                                pagosTotal.deleteFromRealm();
                                realm.commitTransaction();

                                for(Pagos p:realmResultsPayment ){
                                    realm.beginTransaction();
                                    p.deleteFromRealm();
                                    realm.commitTransaction();
                                }


                                realm.beginTransaction();
                                cliente.deleteFromRealm();
                                realm.commitTransaction();
                                notifyDataSetChanged();
                                Toast.makeText(context, "Cliente eliminado con exito", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                            }
                        }).show();

                    }
                });
                alet.show();



            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public  class ViewHolder extends RecyclerView.ViewHolder{
        TextView textViewName,textViewPhone,textViewAdress,textViewId;
        LinearLayout linerItems;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            this.textViewName=itemView.findViewById(R.id.textViewName);
            this.textViewPhone=itemView.findViewById(R.id.textViewPhone);
            this.textViewAdress=itemView.findViewById(R.id.textViewAdress);
            this.linerItems=itemView.findViewById(R.id.linerItems);
            this.textViewId=itemView.findViewById(R.id.textViewId);
        }
    }
}
