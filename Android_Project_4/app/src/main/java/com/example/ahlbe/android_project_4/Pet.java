package com.example.ahlbe.android_project_4;

import com.google.firebase.Timestamp;


import java.util.ArrayList;

public class Pet
{
    private String pName;
    private String pNotes;
    private Long pStatus;
    private Timestamp mTimestamp;
    private String conanID;
    private Boolean notifyPingedUser;
    private ArrayList<String> owners;

    Pet(String pName, String pNotes, Long pStatus, Timestamp timestamp, String conanID, Boolean notifyPingedUser, ArrayList<String> owners)
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

    public void setNotifyPingedUser(Boolean notifyPingedUser)
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

    public void setpStatus(Long pStatus)
    {
        this.pStatus = pStatus;
    }

    public void setTimestamp(Timestamp timestamp)
    {
        mTimestamp = timestamp;
    }

    public Boolean isNotifyPingedUser()
    {
        return notifyPingedUser;
    }

    public ArrayList<String> getOwners()
    {
        return owners;
    }

    public Long getpStatus()
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
