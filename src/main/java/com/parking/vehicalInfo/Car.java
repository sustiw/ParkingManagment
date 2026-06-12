package com.parking.vehicalInfo;

import com.parking.parking.ParkingSpot;

public class Car extends Vehical{
    public Car(String licensePlate) {
        super(licensePlate, VehicalSize.MEDIUM);
    }


    @Override
    public boolean canFitInParking(ParkingSpot spot) {
        return spot.getSpotSize()==VehicalSize.MEDIUM;
    }
}
