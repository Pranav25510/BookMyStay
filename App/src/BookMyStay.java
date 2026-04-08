import java.util.*;
import java.util.concurrent.*;

/**
 * Thread-safe RoomInventory
 */
class ConcurrentRoomInventory {
    private final Map<String, Integer> availability = new HashMap<>();

    public ConcurrentRoomInventory() {
        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    // Thread-safe allocation
    public synchronized boolean allocateRooms(String roomType, int count) {
        int available = availability.getOrDefault(roomType, 0);
        if (count <= 0 || count > available) {
            return false;
        }
        availability.put(roomType, available - count);
        return true;
    }

    // Thread-safe release
    public synchronized void releaseRooms(String roomType, int count) {
        availability.put(roomType, availability.getOrDefault(roomType, 0) + count);
    }

    public synchronized Map<String, Integer> getAvailability() {
        return new HashMap<>(availability);
    }
}

/**
 * Reservation class
 */
class Reservation {
    private final String reservationId;
    private final String guestName;
    private final String roomType;
    private final int roomsBooked;

    public Reservation(String reservationId, String guestName, String roomType, int roomsBooked) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomsBooked = roomsBooked;
    }

    public String getReservationId() { return reservationId; }
    public String getRoomType() { return roomType; }
    public int getRoomsBooked() { return roomsBooked; }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType + " | Rooms: " + roomsBooked;
    }
}

/**
 * Thread-safe BookingService
 */
class ConcurrentBookingService {
    private final ConcurrentRoomInventory inventory;
    private final Map<String, Reservation> confirmedBookings = new ConcurrentHashMap<>();
    private int reservationCounter = 1;

    public ConcurrentBookingService(ConcurrentRoomInventory inventory) {
        this.inventory = inventory;
    }

    public synchronized String bookRoom(String guestName, String roomType, int roomsRequested) {
        String reservationId = "R" + (reservationCounter++);
        boolean allocated = inventory.allocateRooms(roomType, roomsRequested);
        if (!allocated) {
            System.out.println(guestName + " booking failed for " + roomType + ". Not enough availability.");
            return null;
        }
        Reservation reservation = new Reservation(reservationId, guestName, roomType, roomsRequested);
        confirmedBookings.put(reservationId, reservation);
        System.out.println("Booking confirmed: " + reservation);
        return reservationId;
    }

    public void displayBookings() {
        System.out.println("\n=== Current Bookings ===");
        confirmedBookings.values().forEach(System.out::println);
    }

    public Map<String, Reservation> getConfirmedBookings() {
        return confirmedBookings;
    }
}

/**
 * Guest booking task for threading
 */
class GuestBookingTask implements Runnable {
    private final String guestName;
    private final String roomType;
    private final int roomsRequested;
    private final ConcurrentBookingService bookingService;

    public GuestBookingTask(String guestName, String roomType, int roomsRequested, ConcurrentBookingService service) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomsRequested = roomsRequested;
        this.bookingService = service;
    }

    @Override
    public void run() {
        bookingService.bookRoom(guestName, roomType, roomsRequested);
    }
}

/**
 * Application Entry Point
 */
public class BookMyStay {

    public static void main(String[] args) throws InterruptedException {

        ConcurrentRoomInventory inventory = new ConcurrentRoomInventory();
        ConcurrentBookingService bookingService = new ConcurrentBookingService(inventory);

        // Simulate multiple guests booking concurrently
        List<Thread> threads = new ArrayList<>();
        threads.add(new Thread(new GuestBookingTask("Alice", "Single Room", 2, bookingService)));
        threads.add(new Thread(new GuestBookingTask("Bob", "Suite Room", 1, bookingService)));
        threads.add(new Thread(new GuestBookingTask("Charlie", "Double Room", 2, bookingService)));
        threads.add(new Thread(new GuestBookingTask("Diana", "Single Room", 3, bookingService))); // Should fail if not enough rooms
        threads.add(new Thread(new GuestBookingTask("Eve", "Suite Room", 1, bookingService))); // Should fail if none left

        // Start all threads
        threads.forEach(Thread::start);

        // Wait for all threads to finish
        for (Thread t : threads) {
            t.join();
        }

        // Display final booking state
        bookingService.displayBookings();

        // Display remaining inventory
        System.out.println("\nRemaining Inventory: " + inventory.getAvailability());
    }
}