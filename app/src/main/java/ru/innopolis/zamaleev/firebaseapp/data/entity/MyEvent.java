package ru.innopolis.zamaleev.firebaseapp.data.entity;

import android.support.annotation.NonNull;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Ilgiz on 25.05.2017.
 */

public class MyEvent implements Comparable, Serializable {

    private String event_id;
    private String city;
    private String date_begin;
    private String name;
    private String tag;
    private String description;

    public MyEvent() {
    }

    public MyEvent(String id, String city, String date_begin, String name, String tag, String description) {
        this.event_id = id;
        this.city = city;
        this.date_begin = date_begin;
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


    public void setCity(String city) {
        this.city = city;
    }

    public void setDate_begin(String date_begin) {
        this.date_begin = date_begin;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public String getName() {
        return name;
    }

    public String getCity() {
        return city;
    }

    public String getDate_begin() {
        return date_begin;
    }

    public String getTag() {
        return tag;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof MyEvent)
            return event_id.equals(((MyEvent)obj).getEvent_id());

        return super.equals(obj);
    }

    @Override
    public int compareTo(@NonNull Object o) {
        SimpleDateFormat format = new SimpleDateFormat("EEE, d MMM");
        Date dateThis;
        Date dateObj;
        try {
            dateThis = format.parse(date_begin);
        } catch (ParseException e) {
            e.printStackTrace();
            return -1;
        }
        try {
            dateObj = format.parse(((MyEvent) o).getDate_begin());
        } catch (ParseException e) {
            e.printStackTrace();
            return 1;
        }
        return dateThis.compareTo(dateObj);
    }
}
