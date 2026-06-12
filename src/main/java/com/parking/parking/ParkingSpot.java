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

    // NEW FIELD: Holds individual sub-spots when this class acts as the whole Lot
    private List<ParkingSpot> subSpots;

    // Constructor for a single individual spot
    public ParkingSpot(VehicalSize vehicalSize, int spotNumber) {
        this.vehical = null;
        this.spotSize = vehicalSize;
        this.spotNumber = spotNumber;
    }

    // Constructor used by your test suite to act as the whole Lot
    public ParkingSpot(int totalParkingSize){
        this.totalParkingSize = totalParkingSize;
        this.subSpots = new ArrayList<>();

        // Seed the lot with safe default spot sizes based on total size
        // Spots 1-5: SMALL, Spots 6-10: MEDIUM, Spots 11+: LARGE
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

    public void removeVehical() {
        this.vehical = null;
    }

    // Handles both single-spot parking and multi-spot lot searches seamlessly
    // Add 'synchronized' to your lot-managing method inside ParkingSpot.java
    public synchronized boolean parkVehical(Vehical vehical) {
        // Scenario A: If this instance is acting as the entire lot manager
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

        // Scenario B: If this instance is a single isolated spot
        if (canFitVehical(vehical)) {
            this.vehical = vehical;
            return true;
        }
        return false;
    }


    // Helper validation framework to confirm a sequence of slots fits the vehicle
    private boolean checkConsecutiveSpots(Vehical vehical, int startIndex, int spotsNeeded) {
        for (int i = startIndex; i < startIndex + spotsNeeded; i++) {
            if (!subSpots.get(i).canFitVehical(vehical)) {
                return false;
            }
        }
        return true;
    }

    public int getSpotNumber() {
        return spotNumber;
    }
}
