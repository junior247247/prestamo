package com.dantsu.thermalprinter;

import androidx.annotation.NonNull;

import java.util.Date;

import io.realm.annotations.PrimaryKey;

public class PagosFire {


    private String id;

    private  String idClient;

     private long pagado;
     private long prestamo;
     private long pendiente;
     private String tipo;
     private Date date;
     private String DisplayName;
     private  long timestamp;

     public PagosFire(){

     }


    public PagosFire(String id, String idClient, long pagado, long prestamo, long pendiente, Date date, String displayName,long timestamp,String tipo) {
        this.id = id;
        this.idClient = idClient;
        this.pagado = pagado;
        this.prestamo = prestamo;
        this.pendiente = pendiente;
        this.date = date;
        DisplayName = displayName;
        this.timestamp=timestamp;
        this.tipo=tipo;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
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

    public String getIdClient() {
        return idClient;
    }

    public void setIdClient(String idClient) {
        this.idClient = idClient;
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

    public long getPendiente() {
        return pendiente;
    }

    public void setPendiente(long pendiente) {
        this.pendiente = pendiente;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getDisplayName() {
        return DisplayName;
    }

    public void setDisplayName(String displayName) {
        DisplayName = displayName;
    }
}
