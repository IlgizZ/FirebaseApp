package ru.innopolis.zamaleev.firebaseapp.data.entity;

import com.google.firebase.database.IgnoreExtraProperties;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Ilgiz on 6/16/2017.
 */
@IgnoreExtraProperties
public class EventMap implements Serializable{
    private Map<String,User> creator;
    private String description;
    private String date_begin;
    private String date_end;
    private String time_begin;
    private String time_end;
    private String title;
    private String tag;
    private Location location;
    private Map<String, User> participants;
    private Map<String, Requirement> requirements;
    private Integer required_people_count;

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
        this.tag = "";
        this.title = "";
        this.location = new Location();
        this.required_people_count = 0;
    }


    public EventMap(Map<String, User> creator, String description, String date_begin, String date_end, String time_begin, String time_end, String title, String tag, Location location, Map<String, User> participants, Map<String, Requirement> requirements, Integer required_people_count) {
        this.creator = creator;
        this.description = description;
        this.date_begin = date_begin;
        this.date_end = date_end;
        this.time_begin = time_begin;
        this.time_end = time_end;
        this.title = title;
        this.tag = tag;
        this.location = location;
        this.participants = participants;
        this.requirements = requirements;
        this.required_people_count = required_people_count;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
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

    public Map<String, User> getCreator() {
        return creator;
    }

    public void setCreator(Map<String, User> creator) {
        this.creator = creator;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public Map<String, User> getParticipants() {
        return participants;
    }

    public void setParticipants(Map<String, User> participants) {
        this.participants = participants;
    }

    public Map<String, Requirement> getRequirements() {
        return requirements;
    }

    public void setRequirements(Map<String, Requirement> requirements) {
        this.requirements = requirements;
    }

    public Integer getRequired_people_count() {
        return required_people_count;
    }

    public void setRequired_people_count(Integer required_people_count) {
        this.required_people_count = required_people_count;
    }
}

