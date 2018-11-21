package com.example.ahlbe.android_project_4;

import java.util.Date;

public class ConanCache {
    private static final String TAG = "ConanCache";
    private String id;
    private Date time;

    public ConanCache(String id){
        this.id = id;
        this.time = new Date();
    }

    public long getTime(){
        return time.getTime();
    }

    public String getID(){
        return id;
    }
}
