package com.dantsu.thermalprinter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class HistoryActivity extends AppCompatActivity implements RealmChangeListener<RealmResults<Pagos>> {
    Realm realm;
    RealmResults<Pagos> realmResults;
    RecyclerView recycleViewHistorial;
    HistorialAdapter historialAdapter;
    FireHistorialAdapter fireHistorialAdapter;
    TextView textViewPagado,textViewPendiente,textiViewPrestamo;
    FirebaseFirestore firestore;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        recycleViewHistorial=this.findViewById(R.id.recycleViewHistorial);
        realm=Realm.getDefaultInstance();
         id=getIntent().getStringExtra("id");
         firestore=FirebaseFirestore.getInstance();
        textViewPagado=this.findViewById(R.id.textViewPagado);
        textViewPendiente=this.findViewById(R.id.textViewPendiente);
        textiViewPrestamo=this.findViewById(R.id.textiViewPrestamo);
        //realmResults=realm.where(Pagos.class).equalTo("idClient",Integer.valueOf(id)).findAll().sort("id", Sort.DESCENDING);

       // PagosTotal pagosTotal=realm.where(PagosTotal.class).equalTo("idCliente",Integer.valueOf(id)).findFirst();


           // textViewPagado.setText("PAGADO:"+pagosTotal.getPagado());
            //textViewPendiente.setText("PENDIENTE:"+pagosTotal.getPendiente());
            //textiViewPrestamo.setText("PRESTAMO:"+pagosTotal.getPrestamo());


       // historialAdapter= new HistorialAdapter(realmResults);
        //recycleViewHistorial.setLayoutManager(new LinearLayoutManager(this));
        //recycleViewHistorial.setAdapter(historialAdapter);

        show();
    }

    @Override
    public void onChange(RealmResults<Pagos> realmResults) {
       // historialAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Query query=firestore.collection("PagosFire").whereEqualTo("idClient",id).orderBy("timestamp", Query.Direction.DESCENDING);
        FirestoreRecyclerOptions<PagosFire> options= new FirestoreRecyclerOptions.Builder<PagosFire>().setQuery(query,PagosFire.class).build();
        fireHistorialAdapter= new FireHistorialAdapter(options);
        recycleViewHistorial.setLayoutManager(new LinearLayoutManager(this));
        recycleViewHistorial.setAdapter(fireHistorialAdapter);
        fireHistorialAdapter.startListening();
    }

    void show(){
        firestore.collection("PagosTotal").document(id).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
               if(documentSnapshot.exists()){
                   long pagado=documentSnapshot.getLong("pagado");
                   long pendiente =documentSnapshot.getLong("pendiente");
                   long prestamo=documentSnapshot.getLong("prestamo");
                   textViewPagado.setText("PAGADO:"+pagado);
                   textViewPendiente.setText("PENDIENTE:"+pendiente);
                   textiViewPrestamo.setText("PRESTAMO:"+prestamo);
               }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(fireHistorialAdapter!=null){
            fireHistorialAdapter.stopListening();
        }
    }
}