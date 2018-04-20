package ru.innopolis.zamaleev.firebaseapp.data.entity;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ilgiz on 25.05.2017.
 */

public class Event implements Comparable, Serializable {

    private Long id;
    private String city;
    private String date;
    private String name;
    private String tag;
    private String description;

    public Event() {
    }

    public Event(Long id, String city, String date, String name, String tag, String description) {
        this.id = id;
        this.city = city;
        this.date = date;
        this.name = name;
        this.tag = tag;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getDate() {
        return date;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Event)
            return id.equals(((Event)obj).getId());

        return super.equals(obj);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
        Date dateThis;
        Date dateObj;
        try {
            dateThis = format.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        try {
            dateObj = format.parse(((Event) o).getDate());
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
        return dateThis.compareTo(dateObj);
    }
}
