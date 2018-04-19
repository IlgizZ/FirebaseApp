package ru.innopolis.zamaleev.firebaseapp.data.entity;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.IgnoreExtraProperties;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Created by Ilgiz on 6/16/2017.
 */
@IgnoreExtraProperties
public class EventMap implements Serializable{
    private Map<String,User> creator;
    private String address;
    private Double cost;
    private String country;
    private Double creator_rating;
    private String description;
    private String date_begin;
    private String date_end;
    private String time_begin;
    private String time_end;
    private String event_id;
    private Double lat;
    private Double lng;
    private String locality;
    private String name;
    private String type;
    private Map<String, User> participants;
    private Map<String, String> requirements;
    private Integer people_count;

    public EventMap() {
    }

    public EventMap(Map<String, User> creator) {
        this.creator = creator;
        this.participants = creator;
        this.requirements = new HashMap();
        this.description = "";
        this.date_begin = "";
        this.date_end = "";
        this.time_begin = "";
        this.time_end = "";
        this.type = "";
        this.address = "";
        this.cost = 0d;
        this.country = "";
        this.creator_rating = 5d;
        this.event_id = "";
        this.lat = 0d;
        this.lng = 0d;
        this.locality = "";
        this.name = "";
        this.people_count = 0;
    }

    public EventMap(EventMap eventMap){
        this.creator = eventMap.getCreator();
        this.address = eventMap.getAddress();
        this.cost = eventMap.getCost();
        this.country = eventMap.getCountry();
        this.creator_rating = eventMap.getCreator_rating();
        this.description = eventMap.getDescription();
        this.date_begin = eventMap.getDate_begin();
        this.date_end = eventMap.getDate_end();
        this.time_begin = eventMap.getTime_begin();
        this.time_end = eventMap.getTime_end();
        this.event_id = eventMap.getEvent_id();
        this.lat = eventMap.getLat();
        this.lng = eventMap.getLng();
        this.locality = eventMap.getLocality();
        this.name = eventMap.getName();
        this.type = eventMap.getType();
        this.participants = eventMap.getParticipants();
        this.requirements = eventMap.getRequirements();
        this.people_count = eventMap.getPeople_count();
    }

    public EventMap(Map<String, User> creator, String address, Double cost, String country, Double creator_rating, String description, String date_begin, String date_end, String time_begin, String time_end, String event_id, Double lat, Double lng, String locality, String name, String type, Map<String, User> participants, Map<String, String> requirements, Integer people_count) {
        this.creator = creator;
        this.address = address;
        this.cost = cost;
        this.country = country;
        this.creator_rating = creator_rating;
        this.description = description;
        this.date_begin = date_begin;
        this.date_end = date_end;
        this.time_begin = time_begin;
        this.time_end = time_end;
        this.event_id = event_id;
        this.lat = lat;
        this.lng = lng;
        this.locality = locality;
        this.name = name;
        this.type = type;
        this.participants = participants;
        this.requirements = requirements;
        this.people_count = people_count;
    }

    public void addLocation(LatLng position, Context context) throws IOException{
        this.lat = position.latitude;
        this.lng = position.longitude;
        Geocoder geocoder;
        List<Address> addresses;
        geocoder = new Geocoder(context, Locale.getDefault());

        addresses = geocoder.getFromLocation(lat, lng, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5

        this.address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        this.locality = addresses.get(0).getLocality();
        this.country = addresses.get(0).getCountryName();
    }

    public Map<String, User> getCreator() {
        return creator;
    }

    public void setCreator(Map<String, User> creator) {
        this.creator = creator;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Double getCreator_rating() {
        return creator_rating;
    }

    public void setCreator_rating(Double creator_rating) {
        this.creator_rating = creator_rating;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDate_begin() {
        return date_begin;
    }

    public void setDate_begin(String date_begin) {
        this.date_begin = date_begin;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getTime_begin() {
        return time_begin;
    }

    public void setTime_begin(String time_begin) {
        this.time_begin = time_begin;
    }

    public String getTime_end() {
        return time_end;
    }

    public void setTime_end(String time_end) {
        this.time_end = time_end;
    }

    public String getEvent_id() {
        return event_id;
    }

    public void setEvent_id(String event_id) {
        this.event_id = event_id;
    }

    public Double getLat() {
        return lat;
    }

    public void setLat(Double lat) {
        this.lat = lat;
    }

    public Double getLng() {
        return lng;
    }

    public void setLng(Double lng) {
        this.lng = lng;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Map<String, User> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, User> participants) {
        this.participants = participants;
    }

    public Map<String, String> getRequirements() {
        return requirements;
    }

    public void setRequirements(Map<String, String> requirements) {
        this.requirements = requirements;
    }

    public Integer getPeople_count() {
        return people_count;
    }

    public void setPeople_count(Integer people_count) {
        this.people_count = people_count;
    }
}

