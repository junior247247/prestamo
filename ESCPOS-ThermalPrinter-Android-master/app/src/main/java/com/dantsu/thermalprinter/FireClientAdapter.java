package com.dantsu.thermalprinter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class FireClientAdapter extends FirestoreRecyclerAdapter<ClientesFire,FireClientAdapter.ViewHolder> {

    Context context;
    FirebaseFirestore firestore;


    public FireClientAdapter(@NonNull FirestoreRecyclerOptions<ClientesFire> options,Context context) {
        super(options);
        this.context=context;
        firestore=FirebaseFirestore.getInstance();
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull ClientesFire model) {

        holder.textViewAdress.setText(model.getAddress().toUpperCase());
        holder.textViewName.setText(model.getName().toUpperCase() +" "+ model.getLastname().toUpperCase());
        holder.textViewPhone.setText(model.getPhone());
        holder.textViewId.setText(" • ");
        holder.linerItems.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder alet= new AlertDialog.Builder(context).setMessage(model.getName().toUpperCase() +" "+ model.getLastname().toUpperCase()).setTitle("Prestamo").setPositiveButton("Pago", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {



                    firestore.collection("PagosTotal").document(model.getId()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {

                            if(documentSnapshot.exists()){
                                Intent intent= new Intent(context,UpdatePaymentActivity.class);
                                intent.putExtra("displayName",model.getName()+" "+model.getLastname());
                                intent.putExtra("id",model.getId());
                                context.startActivity(intent);
                                //Toast.makeText(context, "existe", Toast.LENGTH_SHORT).show();

                            }else{
                               // Toast.makeText(context, "no existr", Toast.LENGTH_SHORT).show();
                                Intent intent= new Intent(context,PagosDetailsActivity.class);
                                intent.putExtra("id",model.getId());
                                intent.putExtra("displayName",model.getName()+" "+model.getLastname());
                                context.startActivity(intent);
                            }
                        }
                    });

                    }
                }).setNegativeButton("Historial", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent= new Intent(context,HistoryActivity.class);

                        intent.putExtra("id",model.getId());
                        context.startActivity(intent);
                    }
                }).setNeutralButton("Edliminar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new AlertDialog.Builder(context).setMessage("¿Realmente deceas eliminar este cliente?").setTitle("Eliminar Cliente").setPositiveButton("SI", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                    firestore.collection("Clientes").document(model.getId()).delete();
                                    firestore.collection("PagosTotal").document(model.getId()).delete();
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

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_items_clients,parent,false));
    }

    public  class  ViewHolder extends RecyclerView.ViewHolder{
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
