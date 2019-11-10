package com.example.semigo;

import java.util.HashMap;

public class PastTrips extends Trips
{
    public HashMap<User,Integer> amount = new HashMap<>();

    public PastTrips(){}

    public PastTrips(String user1, Location dest1, String st_time, String end_time,String date, String org)
    {
        super(user1,dest1,st_time,end_time,date,org);
    }

}