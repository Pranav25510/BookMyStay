import java.util.*;

/**
 * Abstract Room class
 */
abstract class Room {
    private String type;
    private int beds;
    private double price;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public int getBeds() {
        return beds;
    }

    public double getPrice() {
        return price;
    }

    public void displayDetails() {
        System.out.println(type + " | Beds: " + beds + " | Price: ₹" + price);
    }
}

/**
 * Concrete Room Types
 */
class SingleRoom extends Room {
    public SingleRoom() {
        super("Single Room", 1, 2000);
    }
}

class DoubleRoom extends Room {
    public DoubleRoom() {
        super("Double Room", 2, 3500);
    }
}

class SuiteRoom extends Room {
    public SuiteRoom() {
        super("Suite Room", 3, 6000);
    }
}

/**
 * Centralized Inventory (Read-only access used here)
 */
class RoomInventory {
    private Map<String, Integer> inventory = new HashMap<>();

    public RoomInventory() {
        inventory.put("Single Room", 5);
        inventory.put("Double Room", 0); // unavailable
        inventory.put("Suite Room", 2);
    }

    public int getAvailability(String roomType) {
        return inventory.getOrDefault(roomType, 0);
    }

    public Map<String, Integer> getAllAvailability() {
        return Collections.unmodifiableMap(inventory); // 🔒 read-only view
    }
}

/**
 * Search Service - Read-only operations
 */
class RoomSearchService {

    public void searchAvailableRooms(List<Room> rooms, RoomInventory inventory) {

        System.out.println("==== Available Rooms ====\n");

        rooms.stream()
                // ✅ Filter only available rooms
                .filter(room -> inventory.getAvailability(room.getType()) > 0)

                // ✅ Display details
                .forEach(room -> {
                    room.displayDetails();
                    System.out.println("Available: " +
                            inventory.getAvailability(room.getType()) + "\n");
                });
    }
}

/**
 * Application Entry Point
 */
public class BookMyStay {

    public static void main(String[] args) {

        // Room definitions (domain model)
        List<Room> rooms = List.of(
                new SingleRoom(),
                new DoubleRoom(),
                new SuiteRoom()
        );

        // Centralized inventory
        RoomInventory inventory = new RoomInventory();

        // Search service (read-only)
        RoomSearchService searchService = new RoomSearchService();

        // Perform search
        searchService.searchAvailableRooms(rooms, inventory);
    }
}