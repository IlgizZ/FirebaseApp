package ru.innopolis.zamaleev.firebaseapp.data.entity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.Locale;

/**
 * Created by Ilgiz on 6/14/2017.
 */

public class Location implements Serializable{
    private String city;
    private String street;
    private Integer flat;
    private Integer home;
    private Double lg;
    private Double ln;

    public Location() {
    }

    public Location(LatLng position, Context context) throws IOException {
        this.ln = position.latitude;
        this.lg = position.longitude;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(ln, lg, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        this.street = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        this.city = addresses.get(0).getLocality();
        this.flat = -1;
        this.home = -1;
    }

    public Location(String city, String street, Integer flat, Integer home, Double lg, Double ln) {
        this.city = city;
        this.street = street;
        this.flat = flat;
        this.home = home;
        this.lg = lg;
        this.ln = ln;
    }

    public Location(LatLng position) {
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public Integer getFlat() {
        return flat;
    }

    public void setFlat(Integer flat) {
        this.flat = flat;
    }

    public Integer getHome() {
        return home;
    }

    public void setHome(Integer home) {
        this.home = home;
    }

    public Double getLg() {
        return lg;
    }

    public void setLg(Double lg) {
        this.lg = lg;
    }

    public Double getLn() {
        return ln;
    }

    public void setLn(Double ln) {
        this.ln = ln;
    }
}
