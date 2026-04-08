import java.util.HashMap;
import java.util.Map;

/**
 * RoomInventory class - manages centralized room availability
 */
class RoomInventory {

    // HashMap to store room type → available count
    private Map<String, Integer> inventory;

    /**
     * Constructor initializes inventory
     */
    public RoomInventory() {
        inventory = new HashMap<>();

        // Initial room availability
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 3);
        inventory.put("Suite Room", 2);
    }

    /**
     * Get availability for a specific room type
     */
    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    /**
     * Update availability (e.g., booking or cancellation)
     */
    public void updateAvailability(String roomType, int change) {
        int current = inventory.getOrDefault(roomType, 0);
        inventory.put(roomType, current + change);
    }

    /**
     * Display full inventory
     */
    public void displayInventory() {
        System.out.println("==== Room Inventory ====");
        for (Map.Entry<String, Integer> entry : inventory.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/**
 * Application Entry Point
 */
public class BookMyStay {

    public static void main(String[] args) {

        // Initialize centralized inventory
        RoomInventory inventory = new RoomInventory();

        // Display initial inventory
        inventory.displayInventory();

        System.out.println("\n--- Booking 1 Single Room ---");
        inventory.updateAvailability("Single Room", -1);

        System.out.println("\n--- Cancelling 1 Suite Room ---");
        inventory.updateAvailability("Suite Room", +1);

        // Display updated inventory
        System.out.println();
        inventory.displayInventory();
    }
}