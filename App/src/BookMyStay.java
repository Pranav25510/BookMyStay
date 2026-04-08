import java.util.*;

/**
 * Custom exception for invalid bookings
 */
class InvalidBookingException extends Exception {
    public InvalidBookingException(String message) {
        super(message);
    }
}

/**
 * RoomInventory - manages room availability and enforces validation
 */
class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    /**
     * Validate booking request
     */
    public void validateBooking(String roomType, int requestedRooms) throws InvalidBookingException {
        if (!availability.containsKey(roomType)) {
            throw new InvalidBookingException("Invalid room type: " + roomType);
        }
        int available = availability.get(roomType);
        if (requestedRooms <= 0) {
            throw new InvalidBookingException("Requested rooms must be greater than zero.");
        }
        if (requestedRooms > available) {
            throw new InvalidBookingException("Not enough rooms available for " + roomType
                    + ". Requested: " + requestedRooms + ", Available: " + available);
        }
    }

    /**
     * Allocate rooms after validation
     */
    public void allocateRooms(String roomType, int requestedRooms) throws InvalidBookingException {
        validateBooking(roomType, requestedRooms);
        availability.put(roomType, availability.get(roomType) - requestedRooms);
        System.out.println("Allocated " + requestedRooms + " " + roomType + "(s). Remaining: " + availability.get(roomType));
    }

    public Map<String, Integer> getAvailability() {
        return Collections.unmodifiableMap(availability);
    }
}

/**
 * BookingService - handles booking requests with validation
 */
class BookingService {
    private RoomInventory inventory;
    private int reservationCounter = 1;
    private List<String> reservations = new ArrayList<>();

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    /**
     * Confirm reservation with validation
     */
    public String bookRoom(String guestName, String roomType, int roomsRequested) {
        String reservationId = "R" + (reservationCounter++);
        try {
            inventory.allocateRooms(roomType, roomsRequested);
            reservations.add(reservationId + " | " + guestName + " | " + roomType + " | Rooms: " + roomsRequested);
            System.out.println("Booking confirmed: " + reservationId + " for " + guestName);
        } catch (InvalidBookingException e) {
            System.out.println("Booking failed for " + guestName + ": " + e.getMessage());
            return null;
        }
        return reservationId;
    }

    public void displayBookings() {
        System.out.println("\n=== Confirmed Bookings ===");
        if (reservations.isEmpty()) {
            System.out.println("No successful bookings.");
            return;
        }
        reservations.forEach(System.out::println);
    }
}

/**
 * Application Entry Point
 */
public class BookMyStay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        // Valid bookings
        bookingService.bookRoom("Alice", "Single Room", 2);
        bookingService.bookRoom("Bob", "Suite Room", 1);

        // Invalid bookings
        bookingService.bookRoom("Charlie", "Double Room", 5); // exceeds availability
        bookingService.bookRoom("David", "Penthouse", 1);     // invalid room type
        bookingService.bookRoom("Eve", "Single Room", 0);     // zero rooms

        // Display all successful bookings
        bookingService.displayBookings();

        // Display remaining inventory
        System.out.println("\nRemaining Inventory: " + inventory.getAvailability());
    }
}