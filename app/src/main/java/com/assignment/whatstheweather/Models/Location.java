package com.assignment.whatstheweather.Models;

/**
 * Created by Nakul
 */
 
 public class Location {
    private String lattitude,longitude;
    
    public Location()
    {
    }
    
    public Location(String lattitude, String longitude)
    {
        this.lattitude = lattitude;
        this.longitude = longitude;
    }
    
    public String getLattitude(){
        return lattitude;
    }
    
    public String getLongitude(){
        return longitude;
    }
    
    public void setLattitude(String lattitude){
        this.lattitude = lattitude;
    }
    
    public void setLongitude(String longitude){
        this.longitude = longitude;
    }
    
 }
