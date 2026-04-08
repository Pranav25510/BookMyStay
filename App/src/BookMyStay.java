import java.util.LinkedList;
import java.util.Queue;

/**
 * Reservation class represents a guest's booking request
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
 * BookingRequestQueue - manages incoming reservations in FIFO order
 */
class BookingRequestQueue {

    private Queue<Reservation> queue;

    public BookingRequestQueue() {
        queue = new LinkedList<>();
    }

    /**
     * Add a reservation to the queue (first-come-first-served)
     */
    public void submitRequest(Reservation reservation) {
        queue.add(reservation);
        System.out.println("Booking request submitted: " + reservation);
    }

    /**
     * Peek at the next reservation to process without removing it
     */
    public Reservation peekNextRequest() {
        return queue.peek();
    }

    /**
     * Poll the next reservation (used during allocation)
     */
    public Reservation pollNextRequest() {
        return queue.poll();
    }

    /**
     * Display all queued requests
     */
    public void displayQueue() {
        System.out.println("\n=== Current Booking Queue ===");
        if (queue.isEmpty()) {
            System.out.println("No pending requests.");
        } else {
            for (Reservation r : queue) {
                System.out.println(r);
            }
        }
    }
}

/**
 * Application Entry Point
 */
public class BookMyStay {

    public static void main(String[] args) {

        // Create booking queue
        BookingRequestQueue bookingQueue = new BookingRequestQueue();

        // Submit multiple booking requests
        bookingQueue.submitRequest(new Reservation("Alice", "Single Room", 1));
        bookingQueue.submitRequest(new Reservation("Bob", "Suite Room", 2));
        bookingQueue.submitRequest(new Reservation("Charlie", "Double Room", 1));

        // Display queued requests
        bookingQueue.displayQueue();

        // Peek next request (demonstrates FIFO)
        System.out.println("\nNext request to process: " + bookingQueue.peekNextRequest());
    }
}