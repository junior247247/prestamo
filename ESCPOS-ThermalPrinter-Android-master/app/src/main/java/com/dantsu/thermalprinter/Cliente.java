package com.dantsu.thermalprinter;

import androidx.annotation.NonNull;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;
import io.realm.annotations.Required;

public class Cliente extends RealmObject {

    @PrimaryKey private int id;
    @NonNull private String  name;
    @NonNull private  String lastname;
    @NonNull private  String address;
    @NonNull private  String phone;

public  Cliente(){

}
    public Cliente(  String name,  String lastname,  String address,String phone) {
        this.id = MyAplication.idClient.incrementAndGet();
        this.name = name;
        this.lastname = lastname;
        this.address = address;
        this.phone=phone;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone( String phone) {
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getName() {
        return name;
    }

    public void setName( String name) {
        this.name = name;
    }


    public String getLastname() {
        return lastname;
    }

    public void setLastname( String lastname) {
        this.lastname = lastname;
    }


    public String getAddress() {
        return address;
    }

    public void setAddress( String address) {
        this.address = address;
    }
}
