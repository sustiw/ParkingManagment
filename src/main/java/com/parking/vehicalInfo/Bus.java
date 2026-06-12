package com.parking.vehicalInfo;

import com.parking.parking.ParkingSpot;

public class Bus extends Vehical {
    public Bus(String licensePlate) {
        super(licensePlate, VehicalSize.LARGE);
    }


    @Override
    public boolean canFitInParking(ParkingSpot spot) {

        return spot.getSpotSize() == VehicalSize.LARGE;
    }
}
