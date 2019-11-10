package com.example.semigo;

import java.util.ArrayList;

public class User
{
    public String name;
    public String email_id;
//    public ArrayList<CurrentTrips> currentTrips;
//    public ArrayList<PastTrips> pastTrips;
    public String org;

    public User()
    {

    }

    public User(String n, String e, String o)
    {
        name = n;
        email_id = e;
//        currentTrips = new ArrayList<>();
//        pastTrips = new ArrayList<>();
        org = o;
    }

    public void addCurrentTrip(CurrentTrips c)
    {
//        currentTrips.add(c);
    }
    public void addPastTrip(PastTrips p)
    {
//        pastTrips.add(p);
    }
}