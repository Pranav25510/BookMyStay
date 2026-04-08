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
 * RoomInventory - manages room availability
 */
class RoomInventory {
    private Map<String, Integer> availability = new HashMap<>();

    public RoomInventory() {
        availability.put("Single Room", 5);
        availability.put("Double Room", 3);
        availability.put("Suite Room", 2);
    }

    public void validateBooking(String roomType, int requestedRooms) throws InvalidBookingException {
        if (!availability.containsKey(roomType))
            throw new InvalidBookingException("Invalid room type: " + roomType);
        int available = availability.get(roomType);
        if (requestedRooms <= 0)
            throw new InvalidBookingException("Requested rooms must be greater than zero.");
        if (requestedRooms > available)
            throw new InvalidBookingException("Not enough rooms available for " + roomType
                    + ". Requested: " + requestedRooms + ", Available: " + available);
    }

    public void allocateRooms(String roomType, int requestedRooms) throws InvalidBookingException {
        validateBooking(roomType, requestedRooms);
        availability.put(roomType, availability.get(roomType) - requestedRooms);
    }

    public void releaseRooms(String roomType, int releasedRooms) {
        availability.put(roomType, availability.getOrDefault(roomType, 0) + releasedRooms);
        System.out.println("Released " + releasedRooms + " " + roomType + "(s). Available now: " + availability.get(roomType));
    }

    public Map<String, Integer> getAvailability() {
        return Collections.unmodifiableMap(availability);
    }
}

/**
 * Reservation - guest booking details
 */
class Reservation {
    private String reservationId;
    private String guestName;
    private String roomType;
    private int roomsBooked;

    public Reservation(String reservationId, String guestName, String roomType, int roomsBooked) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomsBooked = roomsBooked;
    }

    public String getReservationId() { return reservationId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public int getRoomsBooked() { return roomsBooked; }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType + " | Rooms: " + roomsBooked;
    }
}

/**
 * BookingService - manages bookings with cancellation support
 */
class BookingService {
    private RoomInventory inventory;
    private Map<String, Reservation> confirmedBookings = new LinkedHashMap<>();
    private Stack<String> cancellationStack = new Stack<>();
    private int reservationCounter = 1;

    public BookingService(RoomInventory inventory) {
        this.inventory = inventory;
    }

    public String bookRoom(String guestName, String roomType, int roomsRequested) {
        String reservationId = "R" + (reservationCounter++);
        try {
            inventory.allocateRooms(roomType, roomsRequested);
            Reservation reservation = new Reservation(reservationId, guestName, roomType, roomsRequested);
            confirmedBookings.put(reservationId, reservation);
            cancellationStack.push(reservationId);
            System.out.println("Booking confirmed: " + reservation);
        } catch (InvalidBookingException e) {
            System.out.println("Booking failed for " + guestName + ": " + e.getMessage());
            return null;
        }
        return reservationId;
    }

    public void cancelBooking(String reservationId) {
        if (!confirmedBookings.containsKey(reservationId)) {
            System.out.println("Cancellation failed: Reservation " + reservationId + " does not exist or already cancelled.");
            return;
        }
        Reservation reservation = confirmedBookings.remove(reservationId);
        inventory.releaseRooms(reservation.getRoomType(), reservation.getRoomsBooked());
        cancellationStack.remove(reservationId); // Remove from rollback stack
        System.out.println("Cancellation successful: " + reservation);
    }

    public void displayBookings() {
        System.out.println("\n=== Current Bookings ===");
        if (confirmedBookings.isEmpty()) {
            System.out.println("No active bookings.");
            return;
        }
        confirmedBookings.values().forEach(System.out::println);
    }
}

/**
 * Application Entry Point
 */
public class BookMyStay {

    public static void main(String[] args) {

        RoomInventory inventory = new RoomInventory();
        BookingService bookingService = new BookingService(inventory);

        // Bookings
        String r1 = bookingService.bookRoom("Alice", "Single Room", 2);
        String r2 = bookingService.bookRoom("Bob", "Suite Room", 1);
        String r3 = bookingService.bookRoom("Charlie", "Double Room", 2);

        // Display current bookings
        bookingService.displayBookings();

        // Cancel a booking
        bookingService.cancelBooking(r2); // Bob cancels
        bookingService.cancelBooking("R99"); // Invalid cancellation

        // Display bookings after cancellation
        bookingService.displayBookings();

        // Show remaining inventory
        System.out.println("\nRemaining Inventory: " + inventory.getAvailability());
    }
}