package com.example.semigo;

public class CurrentTrips extends Trips
{
    public String status;  //Whether it is "ongoing" or "upcoming"

    public CurrentTrips(){}

    public CurrentTrips(String user1, Location dest1, String st_time, String end_time, String org, String date,String status)
    {
        super(user1,dest1,st_time,end_time,date,org);
        this.status = status;
    }
}