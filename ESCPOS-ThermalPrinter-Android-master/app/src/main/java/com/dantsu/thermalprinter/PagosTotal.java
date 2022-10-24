package com.dantsu.thermalprinter;

import androidx.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class PagosTotal extends RealmObject {

    @PrimaryKey private int id;
    @NonNull private int idCliente;
    @NonNull private long pendiente;
    @NonNull private  long pagado;
    @NonNull private long prestamo;

    public  PagosTotal(){

    }

    public PagosTotal(int idCliente, long pendiente, long pagado, long prestamo) {
        this.id = MyAplication.idTotal.incrementAndGet();
        this.idCliente = idCliente;
        this.pendiente = pendiente;
        this.pagado = pagado;
        this.prestamo = prestamo;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(int idCliente) {
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
