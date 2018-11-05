package com.example.ahlbe.android_project_4;

import android.location.Location;

import java.util.Date;

public class Alert {
    private Location location;
    private Date time;
    private String conan;

    public Alert(Location location, Date time, String conan) {
        this.location = location;
        this.time = time;
        this.conan = conan;
    }

    public Location getLocation() {
        return location;
    }

    public Date getTime() {
        return time;
    }

    public String getConan() {
        return conan;
    }
}
