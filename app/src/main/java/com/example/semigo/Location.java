package com.example.semigo;

public class Location {
    public String name;
    public LatLng ll;

    public Location() {}

    public Location(String name, LatLng ll) {
        this.name = name;
        this.ll = ll;
    }
}