import java.util.*;

/**
 * Reservation class - guest's booking request
 */
class Reservation {
    private String guestName;
    private String roomType;
    private int requestedRooms;

    public Reservation(String guestName, String roomType, int requestedRooms) {
        this.guestName = guestName;
        this.roomType = roomType;
        this.requestedRooms = requestedRooms;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getRequestedRooms() {
        return requestedRooms;
    }

    @Override
    public String toString() {
        return guestName + " requests " + requestedRooms + " " + roomType;
    }
}

/**
 * Centralized Room Inventory
 */
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public boolean decrementAvailability(String roomType, int count) {
        int current = inventory.getOrDefault(roomType, 0);
        if (current >= count) {
            inventory.put(roomType, current - count);
            return true;
        }
        return false; // insufficient availability
    }

    public void displayInventory() {
        System.out.println("\n=== Current Inventory ===");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/**
 * Booking Request Queue (FIFO)
 */
class BookingRequestQueue {
    private Queue<Reservation> queue = new LinkedList<>();

    public void submitRequest(Reservation reservation) {
        queue.add(reservation);
        System.out.println("Booking request submitted: " + reservation);
    }

    public Reservation pollNextRequest() {
        return queue.poll();
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}

/**
 * Booking Service - confirms reservations and allocates rooms
 */
class BookingService {

    private RoomInventory inventory;
    private Map<String, Set<String>> allocatedRooms; // roomType -> assigned room IDs
    private int roomIdCounter = 100; // simple unique room ID generator

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
        this.allocatedRooms = new HashMap<>();
    }

    /**
     * Process a reservation request
     */
    public void processReservation(Reservation reservation) {

        String type = reservation.getRoomType();
        int count = reservation.getRequestedRooms();

        // Check inventory
        if (inventory.getAvailability(type) < count) {
            System.out.println("Reservation failed for " + reservation.getGuestName() +
                    ": Not enough " + type + " available.");
            return;
        }

        // Allocate unique room IDs
        Set<String> assigned = allocatedRooms.getOrDefault(type, new HashSet<>());
        List<String> newRoomIds = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            String roomId = type.substring(0, 2).toUpperCase() + roomIdCounter++;
            assigned.add(roomId);
            newRoomIds.add(roomId);
        }
        allocatedRooms.put(type, assigned);

        // Update inventory
        inventory.decrementAvailability(type, count);

        // Confirmation message
        System.out.println("Reservation confirmed for " + reservation.getGuestName() +
                " | Room IDs: " + newRoomIds);
    }

    public void displayAllocatedRooms() {
        System.out.println("\n=== Allocated Rooms ===");
        for (Map.Entry<String, Set<String>> entry : allocatedRooms.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/**
 * Application Entry Point
 */
public class BookMyStay {

    public static void main(String[] args) {

        // Initialize inventory and queue
        RoomInventory inventory = new RoomInventory();
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Submit booking requests
        bookingQueue.submitRequest(new Reservation("Alice", "Single Room", 2));
        bookingQueue.submitRequest(new Reservation("Bob", "Suite Room", 1));
        bookingQueue.submitRequest(new Reservation("Charlie", "Double Room", 3));
        bookingQueue.submitRequest(new Reservation("Diana", "Suite Room", 2)); // should fail

        // Initialize booking service
        BookingService bookingService = new BookingService(inventory);

        // Process requests in FIFO order
        while (!bookingQueue.isEmpty()) {
            Reservation r = bookingQueue.pollNextRequest();
            bookingService.processReservation(r);
        }

        // Display final allocated rooms
        bookingService.displayAllocatedRooms();

        // Display remaining inventory
        inventory.displayInventory();
    }
}