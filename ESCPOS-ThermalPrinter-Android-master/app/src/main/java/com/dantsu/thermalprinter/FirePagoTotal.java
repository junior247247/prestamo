package com.dantsu.thermalprinter;

import androidx.annotation.NonNull;

import io.realm.annotations.PrimaryKey;

public class FirePagoTotal {

    private String id;

    private String idCliente;
     private long pendiente;
     private  long pagado;
     private long prestamo;
     private  long timestamp;

     public  FirePagoTotal(){

     }


    public FirePagoTotal(String id, String idCliente, long pendiente, long pagado, long prestamo,long timestamp) {
        this.id = id;
        this.idCliente = idCliente;
        this.pendiente = pendiente;
        this.pagado = pagado;
        this.prestamo = prestamo;
        this.timestamp=timestamp;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(String idCliente) {
        this.idCliente = idCliente;
    }

    public long getPendiente() {
        return pendiente;
    }

    public void setPendiente(long pendiente) {
        this.pendiente = pendiente;
    }

    public long getPagado() {
        return pagado;
    }

    public void setPagado(long pagado) {
        this.pagado = pagado;
    }

    public long getPrestamo() {
        return prestamo;
    }

    public void setPrestamo(long prestamo) {
        this.prestamo = prestamo;
    }
}
