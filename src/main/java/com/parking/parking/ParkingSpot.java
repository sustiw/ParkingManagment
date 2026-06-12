package com.parking.parking;

import com.parking.vehicalInfo.Vehical;
import com.parking.vehicalInfo.VehicalSize;
import java.util.ArrayList;
import java.util.List;

public class ParkingSpot {
    private Vehical vehical;
    private VehicalSize spotSize;
    private int spotNumber;
    private int totalParkingSize;

    private List<ParkingSpot> subSpots;

    // Constructor for a single individual spot
    public ParkingSpot(VehicalSize vehicalSize, int spotNumber) {
        this.vehical = null;
        this.spotSize = vehicalSize;
        this.spotNumber = spotNumber;
    }

    public ParkingSpot(int totalParkingSize){
        this.totalParkingSize = totalParkingSize;
        this.subSpots = new ArrayList<>();

        for (int i = 1; i <= totalParkingSize; i++) {
            if (i <= 5) {
                subSpots.add(new ParkingSpot(VehicalSize.SMALL, i));
            } else if (i <= 10) {
                subSpots.add(new ParkingSpot(VehicalSize.MEDIUM, i));
            } else {
                subSpots.add(new ParkingSpot(VehicalSize.LARGE, i));
            }
        }
    }

    public VehicalSize getSpotSize() {
        return spotSize;
    }

    public boolean isAvailable() {
        return vehical == null;
    }

    public boolean canFitVehical(Vehical vehical) {
        return isAvailable() && vehical.canFitInParking(this);
    }


    public synchronized boolean parkVehical(Vehical vehical) {
        if (subSpots != null && !subSpots.isEmpty()) {
            int spotsNeeded = vehical.getSpotNeeded();

            for (int i = 0; i <= subSpots.size() - spotsNeeded; i++) {
                if (checkConsecutiveSpots(vehical, i, spotsNeeded)) {
                    for (int j = i; j < i + spotsNeeded; j++) {
                        subSpots.get(j).parkVehical(vehical); // Use internal method safely
                    }
                    return true;
                }
            }
            return false;
        }

        if (canFitVehical(vehical)) {
            this.vehical = vehical;
            return true;
        }
        return false;
    }


    private boolean checkConsecutiveSpots(Vehical vehical, int startIndex, int spotsNeeded) {
        for (int i = startIndex; i < startIndex + spotsNeeded; i++) {
            if (!subSpots.get(i).canFitVehical(vehical)) {
                return false;
            }
        }
        return true;
    }


}
