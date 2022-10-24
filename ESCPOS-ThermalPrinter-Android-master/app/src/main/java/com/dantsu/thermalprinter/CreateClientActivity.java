package com.dantsu.thermalprinter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;

public class CreateClientActivity extends AppCompatActivity {
    EditText editextName,editextLastname,editextAdress,editextPhone;
    Button buttonSave;
    Realm realm;
    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_client);
        this.editextName=findViewById(R.id.editextName);
        this.editextLastname=findViewById(R.id.editextLastname);
        this.editextAdress=findViewById(R.id.editextAdress);
        this.editextPhone=findViewById(R.id.editextPhone);
        this.buttonSave=findViewById(R.id.buttonSave);
        realm=Realm.getDefaultInstance();
        firestore=FirebaseFirestore.getInstance();

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!editextAdress.getText().toString().trim().isEmpty() && !editextName.getText().toString().trim().isEmpty()
                && !editextLastname.getText().toString().trim().isEmpty() && !editextPhone.getText().toString().trim().isEmpty()

                ){
                    ClientesFire cliente= new ClientesFire();
                    String id=firestore.collection("Clientes").document().getId();
                    cliente.setAddress(editextAdress.getText().toString());
                    cliente.setLastname(editextLastname.getText().toString());
                    cliente.setId(id);
                    cliente.setPhone(editextPhone.getText().toString());
                    cliente.setName(editextName.getText().toString());
                    cliente.setTimestamp(new Date().getTime());
                    firestore.collection("Clientes").document(id).set(cliente);
                  //  realm.beginTransaction();
                   // realm.insert(cliente);
                   // realm.commitTransaction();
                    Toast.makeText(CreateClientActivity.this, "Cliente Guardado", Toast.LENGTH_SHORT).show();
                    finish();

                }else{
                    Toast.makeText(CreateClientActivity.this, "COMPLETA TODOS LOS CAMPOS", Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}