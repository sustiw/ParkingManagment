package com.parking.vehicalInfo;

import com.parking.parking.ParkingSpot;

public abstract class Vehical {
    private String licensePlate;
    private int spotNeeded;
    private VehicalSize vehicalSize;

    public Vehical(String licensePlate,VehicalSize vehicalSize){
        this.licensePlate=licensePlate;
        this.vehicalSize=vehicalSize;
        this.spotNeeded=vehicalSize==VehicalSize.LARGE?5:1;

    }

    public String getLicensePlate(){
        return licensePlate;
    }

    public int getSpotNeeded(){
        return spotNeeded;
    }

    public VehicalSize getVehicalSize(){
        return vehicalSize;
    }

    public abstract  boolean canFitInParking(ParkingSpot spot);

}
