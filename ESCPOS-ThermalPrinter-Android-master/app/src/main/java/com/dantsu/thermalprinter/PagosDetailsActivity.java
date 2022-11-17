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
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.dantsu.escposprinter.connection.DeviceConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothConnection;
import com.dantsu.escposprinter.connection.bluetooth.BluetoothPrintersConnections;
import com.dantsu.escposprinter.connection.usb.UsbConnection;
import com.dantsu.escposprinter.connection.usb.UsbPrintersConnections;
import com.dantsu.escposprinter.textparser.PrinterTextParserImg;
import com.dantsu.thermalprinter.async.AsyncBluetoothEscPosPrint;
import com.dantsu.thermalprinter.async.AsyncEscPosPrint;
import com.dantsu.thermalprinter.async.AsyncEscPosPrinter;
import com.dantsu.thermalprinter.async.AsyncUsbEscPosPrint;
import com.google.firebase.firestore.FirebaseFirestore;

import org.xml.sax.Parser;

import java.text.SimpleDateFormat;
import java.util.Date;

import io.realm.Realm;
import io.realm.RealmResults;

public class PagosDetailsActivity extends AppCompatActivity {
    EditText editextMonto;
    Realm realm;
    String idClient = "";
    String displayname = "";

    FirebaseFirestore firestore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pagos_details);
        realm = Realm.getDefaultInstance();

        this.editextMonto = findViewById(R.id.editextMonto);
        this.idClient = getIntent().getExtras().getString("id");
        this.displayname = getIntent().getStringExtra("displayName");
        firestore=FirebaseFirestore.getInstance();

        this.findViewById(R.id.buttonSave).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

               // d.printBluetooth("Ju nior","0","0","0");

                if (!editextMonto.getText().toString().trim().isEmpty()) {

                    create(editextMonto.getText().toString(), idClient);

                } else {
                    Toast.makeText(PagosDetailsActivity.this, "Debes de ingresar el monto", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    void create(String monto, String idClient) {


        PagosFire pagosFire= new PagosFire();
        String id=firestore.collection("PagosFire").document().getId();
        pagosFire.setDate(new Date());
        pagosFire.setPagado(0);
        pagosFire.setId(id);
        pagosFire.setPrestamo(Long.valueOf(monto));
        pagosFire.setPendiente(Long.valueOf(monto));
        pagosFire.setDisplayName(displayname);
        pagosFire.setIdClient(idClient);
        pagosFire.setTipo("PAGO");
        pagosFire.setTimestamp(new Date().getTime());

        firestore.collection("PagosFire").document(id).set(pagosFire);
       // Pagos pagos = new Pagos(Integer.valueOf(idClient), 0, Long.valueOf(monto), Long.valueOf(monto), new Date(), displayname);

        //realm.beginTransaction();
        //realm.insert(pagos);
        //realm.commitTransaction();

        FirePagoTotal pagoTotal=new FirePagoTotal();
        pagoTotal.setId(idClient);
        pagoTotal.setPagado(0);
        pagoTotal.setPendiente(Long.valueOf(monto));
        pagoTotal.setPrestamo(Long.valueOf(monto));
        pagoTotal.setIdCliente(idClient);
        pagoTotal.setTimestamp(new Date().getTime());


        firestore.collection("PagosTotal").document(idClient).set(pagoTotal);
      //  realm.beginTransaction();
       // PagosTotal pagosTotal = new PagosTotal(Integer.valueOf(idClient), Long.valueOf(monto), 0, Long.valueOf(monto));
        //realm.insert(pagosTotal);
        //realm.commitTransaction();
        Toast.makeText(this, "Pago Registrado Con Exito", Toast.LENGTH_LONG).show();
         printBluetooth(displayname, monto, monto,"0");

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
                    this.printBluetooth(displayname, editextMonto.getText().toString(), editextMonto.getText().toString(),"");
                    break;
            }
        }
    }

    private BluetoothConnection selectedDevice;

    public void browseBluetoothDevice() {
        final BluetoothConnection[] bluetoothDevicesList = (new BluetoothPrintersConnections()).getList();

        if (bluetoothDevicesList != null) {
            final String[] items = new String[bluetoothDevicesList.length + 1];
            items[0] = "Default printer";
            int i = 0;
            for (BluetoothConnection device : bluetoothDevicesList) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.BLUETOOTH_CONNECT) != PackageManager.PERMISSION_GRANTED) {
                    return;
                }
                items[++i] = device.getDevice().getName();
            }

            AlertDialog.Builder alertDialog = new AlertDialog.Builder(PagosDetailsActivity.this);
            alertDialog.setTitle("Bluetooth printer selection");
            alertDialog.setItems(items, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    int index = i - 1;
                    if (index == -1) {
                        selectedDevice = null;
                    } else {
                        selectedDevice = bluetoothDevicesList[index];
                    }
                    // Button button = (Button) findViewById(R.id.button_bluetooth_browse);
                    //button.setText(items[i]);
                }
            });

            AlertDialog alert = alertDialog.create();
            alert.setCanceledOnTouchOutside(false);
            alert.show();

        }
    }

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
            if (PagosDetailsActivity.ACTION_USB_PERMISSION.equals(action)) {
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
                                            finish();
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
                new Intent(PagosDetailsActivity.ACTION_USB_PERMISSION),
                android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE : 0
        );
        IntentFilter filter = new IntentFilter(PagosDetailsActivity.ACTION_USB_PERMISSION);
        registerReceiver(this.usbReceiver, filter);
        usbManager.requestPermission(usbConnection.getDevice(), permissionIntent);
    }

    /**
     * Asynchronous printing
     *
     */

    String text="      \"[L]\\n\" +\n" +
            "                        \"[C]<u><font size='big'>ORDER N°045</font></u>\\n\" +\n" +
            "                        \"[L]\\n\" +\n" +
            "                        \"[C]<u type='double'>\" + format.format(new Date()) + \"</u>\\n\" +\n" +
            "                        \"[C]\\n\" +\n" +
            "                        \"[C]================================\\n\" +\n" +
            "                        \"[L]\\n\" +\n" +
            "                        \"[L]<b>Cliente\" +cliente.toUpperCase() +\"\\n\"+\n" +
            "                        \"[L] Prestamo: \" + prestamo +\"\\n\"+\n" +
            "                        \"[L]\\n\" +\n" +
            "                        \"[L]Pendiente: \" + pendiente+\"\\n\"+\n" +
            "                        \"[L]Pagado: \" + \"0\"+\"\\n\"+\n" +
            "                        \"[L]\\n\" +\n" +
            "                        \"[C]--------------------------------\\n\" +\n" +
            "                        \"[R]TOTAL PRICE :[R]34.98€\\n\" +\n" +
            "                        \"[C]<barcode type='ean13' height='10'>831254784551</barcode>\\n\" +\n" +
            "                        \"[L]\\n\" +\n" +
            "                        \"[C]<qrcode size='20'>http://www.developpeur-web.dantsu.com/</qrcode>\\n\"";


    @SuppressLint("SimpleDateFormat")
    public AsyncEscPosPrinter getAsyncEscPosPrinter(DeviceConnection printerConnection, String cliente, String pendiente, String prestamo,String pagado) {
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
                        "[L]         ID:"+idClient +"\n"+
                        "[L]     Cliente:"+cliente.toUpperCase() +"\n"+
                        "[L]     Abonado:"+ "0" +"\n"+
                        "[L]    Prestamo:"+ prestamo +"\n"+
                        "[L]   Pendiente:"+ pendiente+"\n"+
                        "[L]     Pagado:"+ pagado +"\n"+
                        "[L]\n" +
                        "[C]--------------------------------\n" +
                        "[C]<barcode type='ean13' height='10'>831254784551</barcode>\n"



        );
    }






}