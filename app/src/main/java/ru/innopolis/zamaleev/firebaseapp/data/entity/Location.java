package ru.innopolis.zamaleev.firebaseapp.data.entity;

/**
 * Created by Ilgiz on 6/14/2017.
 */

public class Location {
    private String city;
    private String street;
    private Integer flat;
    private Integer home;
    private Double lg;
    private Double ln;

    public Location() {
    }

    public Location(String city, String street, Integer flat, Integer home, Double lg, Double ln) {
        this.city = city;
        this.street = street;
        this.flat = flat;
        this.home = home;
        this.lg = lg;
        this.ln = ln;
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
