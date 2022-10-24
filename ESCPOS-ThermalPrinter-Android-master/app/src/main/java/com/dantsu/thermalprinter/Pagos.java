package com.dantsu.thermalprinter;

import androidx.annotation.NonNull;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Pagos extends RealmObject {
    @PrimaryKey private int id;
    @NonNull private  int idClient;

    @NonNull private long pagado;
    @NonNull private long prestamo;
    @NonNull private long pendiente;
    @NonNull private Date date;
    @NonNull private String DisplayName;


    public  Pagos(){

    }
    public Pagos( int idClient, long pagado, long prestamo, long pendiente, @NonNull Date date, @NonNull String displayName) {
        this.id = MyAplication.idPago.incrementAndGet();
        this.idClient = idClient;
        this.pagado = pagado;
        this.prestamo = prestamo;
        this.pendiente = pendiente;
        this.date = date;
        DisplayName = displayName;
    }

    public void setPagado(long pagado) {
        this.pagado = pagado;
    }

    public long getPagado() {
        return pagado;
    }

    public void setDisplayName(@NonNull String displayName) {
        DisplayName = displayName;
    }

    @NonNull
    public String getDisplayName() {
        return DisplayName;
    }

    public void setPendiente(long pendiente) {
        this.pendiente = pendiente;
    }

    public long getPendiente() {
        return pendiente;
    }

    public void setPrestamo(long prestamo) {
        this.prestamo = prestamo;
    }

    public long getPrestamo() {
        return prestamo;
    }





    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdClient() {
        return idClient;
    }

    public void setIdClient(int idClient) {
        this.idClient = idClient;
    }

    public double getMonto() {
        return pagado;
    }

    public void setMonto(long monto) {
        this.pagado = monto;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }
}
