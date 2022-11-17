package com.dantsu.thermalprinter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.dantsu.thermalprinter.async.AsyncBluetoothEscPosPrint;
import com.dantsu.thermalprinter.async.AsyncEscPosPrint;
import com.dantsu.thermalprinter.async.AsyncEscPosPrinter;
import com.dantsu.thermalprinter.async.AsyncUsbEscPosPrint;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
                long pagado=documentSnapshot.getLong("pagado");
                long parse=Long.parseLong(monto);
                pendiente=pendiente+parse;
                prestamo=prestamo+parse;
                Map<String,Object> map= new HashMap<>();
                map.put("prestamo",prestamo);
                map.put("pendiente",pendiente);
                firestore.collection("PagosTotal").document(idClient).update(map);

                PagosFire pagosFire= new PagosFire();
                String id=firestore.collection("PagosFire").document().getId();
                pagosFire.setDate(new Date());
                pagosFire.setPagado(0);
                pagosFire.setId(id);
                pagosFire.setPrestamo(Long.valueOf(monto));
                pagosFire.setPendiente(Long.valueOf(monto));
                pagosFire.setDisplayName("");
                pagosFire.setIdClient(idClient);
                pagosFire.setTipo("INCREMENTO");
                pagosFire.setTimestamp(new Date().getTime());

                firestore.collection("PagosFire").document(id).set(pagosFire);



                Toast.makeText(SumarActivity.this, "Prestamos Incrementado", Toast.LENGTH_SHORT).show();
                printBluetooth(textViewName.getText().toString(),String.valueOf(prestamo),String.valueOf(pendiente),String.valueOf(pagado));
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

    }


    public static final int PERMISSION_BLUETOOTH = 1;
    public static final int PERMISSION_BLUETOOTH_ADMIN = 2;
    public static final int PERMISSION_BLUETOOTH_CONNECT = 3;
    public static final int PERMISSION_BLUETOOTH_SCAN = 4;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            switch (requestCode) {
                case PagosDetailsActivity.PERMISSION_BLUETOOTH:
                case PagosDetailsActivity.PERMISSION_BLUETOOTH_ADMIN:
                case PagosDetailsActivity.PERMISSION_BLUETOOTH_CONNECT:
                case PagosDetailsActivity.PERMISSION_BLUETOOTH_SCAN:
                    this.printBluetooth(textViewName.getText().toString(), editextMonto.getText().toString(), editextMonto.getText().toString(),"");
                    break;
            }
        }
    }

    private BluetoothConnection selectedDevice;

    public    void printBluetooth(String cliente,String prestamo ,String pendiente,String pagado) {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH}, MainActivity.PERMISSION_BLUETOOTH);
        } else if (ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_ADMIN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_ADMIN}, MainActivity.PERMISSION_BLUETOOTH_ADMIN);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_CONNECT}, MainActivity.PERMISSION_BLUETOOTH_CONNECT);
        } else if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S && ContextCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_SCAN) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.BLUETOOTH_SCAN}, MainActivity.PERMISSION_BLUETOOTH_SCAN);
        } else {
            new AsyncBluetoothEscPosPrint(
                    this,
                    new AsyncEscPosPrint.OnPrintFinished() {
                        @Override
                        public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                            Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                            finish();


                        }

                        @Override
                        public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                            Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                            finish();
                        }
                    }
            )
                    .execute(this.getAsyncEscPosPrinter(selectedDevice,cliente,pendiente,prestamo,pagado));
        }
    }

    /*==============================================================================================
    ===========================================USB PART=============================================
    ==============================================================================================*/

    private static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    private final BroadcastReceiver usbReceiver = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (SumarActivity.ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbManager usbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
                    UsbDevice usbDevice = (UsbDevice) intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        if (usbManager != null && usbDevice != null) {
                            new AsyncUsbEscPosPrint(
                                    context,
                                    new AsyncEscPosPrint.OnPrintFinished() {
                                        @Override
                                        public void onError(AsyncEscPosPrinter asyncEscPosPrinter, int codeException) {
                                            Log.e("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : An error occurred !");
                                        }

                                        @Override
                                        public void onSuccess(AsyncEscPosPrinter asyncEscPosPrinter) {
                                            Log.i("Async.OnPrintFinished", "AsyncEscPosPrint.OnPrintFinished : Print is finished !");
                                        }
                                    }
                            )
                                    .execute(getAsyncEscPosPrinter(new UsbConnection(usbManager, usbDevice),"","","",""));
                        }
                    }
                }
            }
        }
    };

    public void printUsb() {
        UsbConnection usbConnection = UsbPrintersConnections.selectFirstConnected(this);
        UsbManager usbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);

        if (usbConnection == null || usbManager == null) {
            new AlertDialog.Builder(this)
                    .setTitle("USB Connection")
                    .setMessage("No USB printer found.")
                    .show();
            return;
        }

        PendingIntent permissionIntent = PendingIntent.getBroadcast(
                this,
                0,
                new Intent(SumarActivity.ACTION_USB_PERMISSION),
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
        );
        IntentFilter filter = new IntentFilter(SumarActivity.ACTION_USB_PERMISSION);
        registerReceiver(this.usbReceiver, filter);
        usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);
    }

    /**
     * Asynchronous printing
     *
     */



    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection, String cliente, String pendiente, String prestamo, String pagado) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd '--' HH:mm:ss");
        AsyncEscPosPrinter printer = new AsyncEscPosPrinter(printerConnection, 203, 48f, 32);
        return printer.addTextToPrint(
                // "[C]<img>" + PrinterTextParserImg.bitmapToHexadecimalString(printer, this.getApplicationContext().getResources().getDrawableForDensity(R.drawable.logo, DisplayMetrics.DENSITY_MEDIUM)) + "</img>\n" +
                //     "[L]\n" +
                //    "[C]<u><font size='big'>ORDER N°045</font></u>\n" +

                "[L]\n" +

                        "[C]    INVERSIONES CORDERO \n" +
                        "[C]  PRESTAMOS PERSONALES,HIPOTECARIOS,ACCESORIA LEGAL \n" +
                        "[C]================================\n" +
                        "[L]   CALLE LIBERTAD #4, Buenos Aires de Herrera, Sto Dgo Oeste RD\n" +
                        "[L]   FECHA:" + format.format(new Date()) +"\n" +
                        "[C]================================\n" +
                        "[L]         ID:"+String.valueOf(new Random(100000).nextInt()) +"\n"+
                        "[L]     Cliente:"+cliente.toUpperCase() +"\n"+
                        "[L]    Sumado:"+ editextMonto.getText().toString().trim() +"\n"+
                        "[L]    Prestamo:"+ prestamo +"\n"+
                        "[L]   Pendiente:"+ pendiente+"\n"+
                        "[L]     Pagado:"+ pagado +"\n"+
                        "[L]\n" +
                        "[C]--------------------------------\n" +
                        "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n"



        );
    }




}