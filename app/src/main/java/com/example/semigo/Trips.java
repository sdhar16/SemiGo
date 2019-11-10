package com.example.semigo;


public class Trips
{
    public String id;
    public String user1, user2;
    public Location dest1, dest2;
    public String bookie;
    public String st_time;
    public String end_time;
    public String date;
    public String org;


    public Trips()
    {
    }

    public Trips(String user1, Location dest1, String st_time, String end_time, String date, String org)
    {
        this.id = id;
        this.user1 = user1;
        this.user2 = "";
        this.dest1 = dest1;
        this.dest2 = new Location();
        this.bookie = user1;
        this.st_time = st_time;
        this.end_time = end_time;
        this.date = date;
        this.org = org;
    }

    public void updateTrip(String user2, Location dest2, String bookie, String st_time, String ends_time)
    {
        this.user2 = user2;
        this.dest2 = dest2;
        this.bookie = bookie;
        this.st_time = st_time;
        this.end_time = ends_time;
    }

}