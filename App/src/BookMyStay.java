import java.io.*;
import java.util.*;

/**
 * Serializable Inventory Class
 */
class PersistentInventory implements Serializable {
    private static final long serialVersionUID = 1L;
    private final Map<String, Integer> availability;

    public PersistentInventory(Map<String, Integer> availability) {
        this.availability = new HashMap<>(availability);
    }

    public Map<String, Integer> getAvailability() {
        return new HashMap<>(availability);
    }

    public void updateAvailability(String roomType, int count) {
        availability.put(roomType, count);
    }

    @Override
    public String toString() {
        return availability.toString();
    }
}

/**
 * Serializable Reservation Class
 */
class PersistentReservation implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String reservationId;
    private final String guestName;
    private final String roomType;
    private final int roomsBooked;

    public PersistentReservation(String reservationId, String guestName, String roomType, int roomsBooked) {
        this.reservationId = reservationId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.roomsBooked = roomsBooked;
    }

    @Override
    public String toString() {
        return reservationId + " | " + guestName + " | " + roomType + " | Rooms: " + roomsBooked;
    }
}

/**
 * Persistence Service
 */
class PersistenceService {

    private static final String INVENTORY_FILE = "inventory.ser";
    private static final String BOOKINGS_FILE = "bookings.ser";

    // Save inventory and booking history
    public static void saveState(PersistentInventory inventory, List<PersistentReservation> bookings) {
        try (ObjectOutputStream invOut = new ObjectOutputStream(new FileOutputStream(INVENTORY_FILE));
             ObjectOutputStream bookOut = new ObjectOutputStream(new FileOutputStream(BOOKINGS_FILE))) {

            invOut.writeObject(inventory);
            bookOut.writeObject(bookings);

            System.out.println("System state saved successfully.");
        } catch (IOException e) {
            System.err.println("Failed to save system state: " + e.getMessage());
        }
    }

    // Load inventory and booking history
    public static Optional<Map.Entry<PersistentInventory, List<PersistentReservation>>> loadState() {
        try (ObjectInputStream invIn = new ObjectInputStream(new FileInputStream(INVENTORY_FILE));
             ObjectInputStream bookIn = new ObjectInputStream(new FileInputStream(BOOKINGS_FILE))) {

            PersistentInventory inventory = (PersistentInventory) invIn.readObject();
            List<PersistentReservation> bookings = (List<PersistentReservation>) bookIn.readObject();
            System.out.println("System state restored successfully.");
            return Optional.of(new AbstractMap.SimpleEntry<>(inventory, bookings));

        } catch (FileNotFoundException e) {
            System.out.println("Persistence files not found. Starting with fresh state.");
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to restore system state: " + e.getMessage());
        }
        return Optional.empty();
    }
}

/**
 * Application Entry
 */
public class BookMyStay {

    public static void main(String[] args) {

        // Attempt to restore previous state
        Optional<Map.Entry<PersistentInventory, List<PersistentReservation>>> restoredState = PersistenceService.loadState();

        PersistentInventory inventory;
        List<PersistentReservation> bookings;

        if (restoredState.isPresent()) {
            inventory = restoredState.get().getKey();
            bookings = restoredState.get().getValue();
        } else {
            // Fresh state
            Map<String, Integer> initialAvailability = new HashMap<>();
            initialAvailability.put("Single Room", 5);
            initialAvailability.put("Double Room", 3);
            initialAvailability.put("Suite Room", 2);
            inventory = new PersistentInventory(initialAvailability);
            bookings = new ArrayList<>();
        }

        // Simulate a booking
        PersistentReservation res1 = new PersistentReservation("R1", "Alice", "Single Room", 1);
        bookings.add(res1);
        inventory.updateAvailability("Single Room", inventory.getAvailability().get("Single Room") - 1);

        PersistentReservation res2 = new PersistentReservation("R2", "Bob", "Suite Room", 1);
        bookings.add(res2);
        inventory.updateAvailability("Suite Room", inventory.getAvailability().get("Suite Room") - 1);

        // Display current state
        System.out.println("\n=== Current Inventory ===");
        System.out.println(inventory);

        System.out.println("\n=== Booking History ===");
        bookings.forEach(System.out::println);

        // Save system state before shutdown
        PersistenceService.saveState(inventory, bookings);
    }
}