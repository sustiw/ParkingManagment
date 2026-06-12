package com.parking.vehicalInfo;

import com.parking.parking.ParkingSpot;

public class MotarCycle extends Vehical{
    public MotarCycle(String licensePlate) {
        super(licensePlate, VehicalSize.SMALL);
    }


    @Override
    public boolean canFitInParking(ParkingSpot spot) {
        return spot.getSpotSize()==VehicalSize.SMALL;
    }
}
