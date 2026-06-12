package com.parking.parking;



import com.parking.vehicalInfo.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;


public class TestParkingService {

    private ParkingSpot parkingSpot;
    @BeforeEach
    public void setUp() {
        parkingSpot = new ParkingSpot(15);
    }


    @Test
    @DisplayName("Should successfully park a motorcycle in the first available slot")
    public void shouldParkMotorcycleSuccessfully() {

        // Arrange
        Vehical bike = new MotarCycle("M-123");

        // Act
        boolean result = parkingSpot.parkVehical(bike);

        // Assert
        assertTrue( result,"Motorcycle should park successfully");
    }

    @Test
    @DisplayName("Should block a Car from parking inside a SMALL designated spot")
    public void shouldNotAllowCarInSmallSpot() {

        // Arrange
        // Create a custom mini-lot with only 1 small spot to isolate behavior
        ParkingSpot tinyLot = new ParkingSpot(1);
        Vehical car = new Car("C-999");

        // Act
        boolean result = tinyLot.parkVehical(car);

        // Assert
        assertFalse(result, "Car should fail to park if only a SMALL spot is open");
    }

    @Test
    @DisplayName("Should successfully park a Car inside a MEDIUM designated spot")
    public void shouldParkCarInMediumSpot() {

        // Arrange
        Vehical car = new Car("C-123");

        // Act
        boolean result = parkingSpot.parkVehical(car);


        // Assert
        assertTrue(result, "Car should successfully park in its matching spot type");
    }

    @Test
    @DisplayName("Should successfully allocate 5 consecutive spots for a Bus")
    public void shouldAllocateFiveConsecutiveSpotsForBus() {

        // Arrange
        Vehical bus = new Bus("B-BUS-55");

        // Act
        boolean result = parkingSpot.parkVehical(bus);

        // Assert
        assertTrue(result, "Bus should secure 5 consecutive LARGE slots");
    }

    @Test
    @DisplayName("Should reject a Bus if 5 consecutive slots are unavailable")
    public void shouldRejectBusWhenNotEnoughConsecutiveSpots() {

        // Arrange
        // A lot with only 3 large spots total (Spots 7, 8, 9)
        ParkingSpot smallBusLot = new ParkingSpot(9);
        Vehical bus = new Bus("B-FAIL");

        // Act
        boolean result = smallBusLot.parkVehical(bus);

        // Assert
        assertFalse(result, "Bus should be rejected if it cannot find 5 consecutive slots");
    }

    @Test
    @DisplayName("Should deny entry gracefully when the complete lot infrastructure is saturated")
    public void shouldDenyEntryWhenLotIsCompletelyFull() {

        // Arrange
        // A mini lot with exactly 3 spots (1 Small, 1 Medium, 1 Large)
        ParkingSpot microLot = new ParkingSpot(3);

        Vehical car1 = new Car("CAR-FIRST");
        Vehical car2 = new Car("CAR-SECOND");

        // Act
        microLot.parkVehical(car1); // Takes the only compatible spot
        boolean secondCarResult = microLot.parkVehical(car2); // No valid spots remaining

        // Assert
        assertFalse(secondCarResult, "Second car should fail to park because the lot is full");
    }

    @Test
    @DisplayName("Should handle heavily multi-threaded parking requests concurrently without data corruption")
    public void testConcurrentParkingWithExecutorService() throws InterruptedException {
        // Arrange: Create a limited lot with exactly 15 slots total
        // Spots 1-5: SMALL, Spots 6-10: MEDIUM, Spots 11-15: LARGE (Exactly enough space for 1 Bus)
        ParkingSpot concurrentLot = new ParkingSpot(15);

        // We will send 2 Buses. Only ONE Bus should find 5 consecutive LARGE slots.
        // The other Bus must be rejected gracefully because space is limited.
        Vehical bus1 = new Bus("BUS-GATE-A");
        Vehical bus2 = new Bus("BUS-GATE-B");
        Vehical car1 = new Car("CAR-GATE-C");
        Vehical car2 = new Car("CAR-GATE-D");
        Vehical bike = new MotarCycle("BIKE-GATE-E");

        Vehical[] chaoticTraffic = {bus1, car1, bus2, car2, bike};

        // Initialize an ExecutorService with a fixed pool of 4 concurrent worker threads
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors.newFixedThreadPool(4);

        // Atomic Counters to securely track passing and failing operations across threads
        java.util.concurrent.atomic.AtomicInteger totalSuccesses = new java.util.concurrent.atomic.AtomicInteger(0);
        java.util.concurrent.atomic.AtomicInteger totalFailures = new java.util.concurrent.atomic.AtomicInteger(0);

        // Act: Submit all parking tasks simultaneously to the background workers
        for (Vehical vehicle : chaoticTraffic) {
            executor.submit(() -> {
                boolean allocationResult = concurrentLot.parkVehical(vehicle);
                if (allocationResult) {
                    totalSuccesses.incrementAndGet();
                    System.out.println("Async Success: Parked " + vehicle.getLicensePlate());
                } else {
                    totalFailures.incrementAndGet();
                    System.out.println("Async Denied: No space for " + vehicle.getLicensePlate());
                }
            });
        }

        // Gracefully shut down the executor pool queue
        executor.shutdown();

        // Block the test runner until all background threads finish executing (max 5 seconds timeout)
        boolean threadsFinishedCleanly = executor.awaitTermination(5, java.util.concurrent.TimeUnit.SECONDS);
        assertTrue(threadsFinishedCleanly, "The concurrent tasks took too long and timed out!");

        assertEquals(4, totalSuccesses.get(), "Exactly 4 vehicles should successfully secure slots");
        assertEquals(1, totalFailures.get(), "Exactly 1 vehicle (the second Bus) should be rejected");
    }

}
