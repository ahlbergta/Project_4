package com.example.ahlbe.android_project_4;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.util.ArrayList;

public class Pet
{
    private String pName;
    private String pNotes;
    private int pStatus;
    private Timestamp mTimestamp;
    private String conanID;
    private boolean notifyPingedUser;
    private ArrayList<String> owners;

    Pet(String pName, String pNotes, int pStatus, Timestamp timestamp, String conanID, boolean notifyPingedUser, ArrayList<String> owners)
    {
        this.pName = pName;
        this.pNotes = pNotes;
        this.pStatus = pStatus;
        this.mTimestamp = timestamp;
        this.conanID = conanID;
        this.notifyPingedUser = notifyPingedUser;
        this.owners = owners;
    }

    public void setConanID(String conanID)
    {
        this.conanID = conanID;
    }

    public void setNotifyPingedUser(boolean notifyPingedUser)
    {
        this.notifyPingedUser = notifyPingedUser;
    }

    public void setOwners(ArrayList<String> owners)
    {
        this.owners = owners;
    }

    public void setpName(String pName)
    {
        this.pName = pName;
    }

    public void setpNotes(String pNotes)
    {
        this.pNotes = pNotes;
    }

    public void setpStatus(int pStatus)
    {
        this.pStatus = pStatus;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        mTimestamp = timestamp;
    }

    public boolean isNotifyPingedUser()
    {
        return notifyPingedUser;
    }

    public ArrayList<String> getOwners()
    {
        return owners;
    }

    public int getpStatus()
    {
        return pStatus;
    }

    public String getConanID()
    {
        return conanID;
    }

    public String getpName()
    {
        return pName;
    }

    public String getpNotes()
    {
        return pNotes;
    }

    public Timestamp getTimestamp()
    {
        return mTimestamp;
    }
}
