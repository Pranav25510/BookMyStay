import java.util.*;

/**
 * Reservation class - guest booking details
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

    public String getReservationId() {
        return reservationId;
    }

    public String getGuestName() {
        return guestName;
    }

    public String getRoomType() {
        return roomType;
    }

    public int getRoomsBooked() {
        return roomsBooked;
    }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType + " | Rooms: " + roomsBooked;
    }
}

/**
 * BookingHistory - maintains confirmed reservations in chronological order
 */
class BookingHistory {
    private List<Reservation> history = new ArrayList<>();

    /**
     * Add a confirmed reservation to history
     */
    public void addReservation(Reservation reservation) {
        history.add(reservation);
        System.out.println("Reservation recorded in history: " + reservation);
    }

    /**
     * Retrieve all reservations
     */
    public List<Reservation> getAllReservations() {
        return Collections.unmodifiableList(history);
    }
}

/**
 * BookingReportService - generates reports from booking history
 */
class BookingReportService {
    private BookingHistory bookingHistory;

    public BookingReportService(BookingHistory bookingHistory) {
        this.bookingHistory = bookingHistory;
    }

    /**
     * Print a summary of all bookings
     */
    public void generateReport() {
        System.out.println("\n=== Booking Report ===");
        List<Reservation> reservations = bookingHistory.getAllReservations();
        if (reservations.isEmpty()) {
            System.out.println("No bookings recorded.");
            return;
        }

        Map<String, Integer> roomTypeSummary = new HashMap<>();
        for (Reservation r : reservations) {
            roomTypeSummary.put(r.getRoomType(),
                    roomTypeSummary.getOrDefault(r.getRoomType(), 0) + r.getRoomsBooked());
            System.out.println(r);
        }

        System.out.println("\n--- Summary by Room Type ---");
        for (Map.Entry<String, Integer> entry : roomTypeSummary.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue() + " rooms booked");
        }
    }
}

/**
 * Application Entry Point
 */
public class BookMyStay {

    public static void main(String[] args) {

        // Initialize booking history and report service
        BookingHistory bookingHistory = new BookingHistory();
        BookingReportService reportService = new BookingReportService(bookingHistory);

        // Simulate confirmed reservations
        Reservation r1 = new Reservation("R1", "Alice", "Single Room", 2);
        Reservation r2 = new Reservation("R2", "Bob", "Suite Room", 1);
        Reservation r3 = new Reservation("R3", "Charlie", "Double Room", 3);

        // Add to history
        bookingHistory.addReservation(r1);
        bookingHistory.addReservation(r2);
        bookingHistory.addReservation(r3);

        // Generate report
        reportService.generateReport();
    }
}