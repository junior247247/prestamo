package com.dantsu.thermalprinter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class SumarActivity extends AppCompatActivity {
    Realm realm;
    //PagosTotal pagosTotal;
   // Cliente cliente;
    String idClient="";
    TextView textViewName;
    EditText editextMonto;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sumar);
        realm=Realm.getDefaultInstance();
        firestore=FirebaseFirestore.getInstance();
        this.idClient=getIntent().getStringExtra("id");
        //pagosTotal=realm.where(PagosTotal.class).equalTo("idCliente",Integer.parseInt(idClient)).findFirst();
       // cliente=realm.where(Cliente.class).equalTo("id",Integer.parseInt(idClient)).findFirst();
        this.textViewName=findViewById(R.id.textViewName);
         showClient();

        this.editextMonto=findViewById(R.id.editextMonto);
        this.findViewById(R.id.buttonSumar).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!editextMonto.getText().toString().trim().isEmpty()){
                      create(editextMonto.getText().toString());
                }else{
                    Toast.makeText(SumarActivity.this, "Debes de ingresar el monto", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    void showClient(){
        firestore.collection("Clientes").document(idClient).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String name=documentSnapshot.getString("name");
                    String lastname=documentSnapshot.getString("lastname");
                    textViewName.setText(name.toUpperCase()+" "+ lastname.toUpperCase());
                }
            }
        });
    }
    void create(String monto){
        firestore.collection("PagosTotal").document(idClient).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                long pendiente=documentSnapshot.getLong("pendiente");
                long prestamo=documentSnapshot.getLong("prestamo");
                long parse=Long.parseLong(monto);
                pendiente=pendiente+parse;
                prestamo=prestamo+parse;
                Map<String,Object> map= new HashMap<>();
                map.put("prestamo",prestamo);
                map.put("pendiente",pendiente);
                firestore.collection("PagosTotal").document(idClient).update(map);
            }
        });

        //long pendiente=pagosTotal.getPendiente();
        //long prestamo=pagosTotal.getPrestamo();
        //long parse=Long.parseLong(monto);
        //pendiente=pendiente+parse;
       // prestamo=prestamo+parse;

        //realm.beginTransaction();
        //pagosTotal.setPendiente(pendiente);
        //pagosTotal.setPrestamo(prestamo);
      //  realm.commitTransaction();
        Toast.makeText(this, "Prestamos Incrementado", Toast.LENGTH_SHORT).show();
        finish();
    }
}