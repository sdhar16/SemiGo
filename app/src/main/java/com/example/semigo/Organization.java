package com.example.semigo;

public class Organization
{
    public String org_name;
    public LatLng ll;
    public String email_extension;

    public Organization(){}

    public Organization(String org, LatLng ll, String ext)
    {
        org_name = org;
        this.ll = ll;
        email_extension = ext;
    }
}