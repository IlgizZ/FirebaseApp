package ru.innopolis.zamaleev.firebaseapp.data.entity;

import com.google.firebase.database.IgnoreExtraProperties;

/**
 * Created by Ilgiz on 6/16/2017.
 */
@IgnoreExtraProperties

public class Requirement {
    private String description;

    public Requirement() {
    }

    public Requirement(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
