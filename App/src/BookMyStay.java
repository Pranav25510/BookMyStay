import java.util.*;

/**
 * Service class - represents an optional add-on service
 */
class Service {
    private String name;
    private double cost;

    public Service(String name, double cost) {
        this.name = name;
        this.cost = cost;
    }

    public String getName() {
        return name;
    }

    public double getCost() {
        return cost;
    }

    @Override
    public String toString() {
        return name + " (₹" + cost + ")";
    }
}

/**
 * Add-On Service Manager
 * Maps reservation IDs to selected services
 */
class AddOnServiceManager {
    private Map<String, List<Service>> reservationServices = new HashMap<>();

    /**
     * Attach one or more services to a reservation
     */
    public void addServices(String reservationId, List<Service> services) {
        reservationServices.putIfAbsent(reservationId, new ArrayList<>());
        reservationServices.get(reservationId).addAll(services);
        System.out.println("Services added to reservation " + reservationId + ": " + services);
    }

    /**
     * Calculate total add-on cost for a reservation
     */
    public double calculateTotalCost(String reservationId) {
        List<Service> services = reservationServices.getOrDefault(reservationId, Collections.emptyList());
        return services.stream().mapToDouble(Service::getCost).sum();
    }

    /**
     * Display all services for each reservation
     */
    public void displayServices() {
        System.out.println("\n=== Add-On Services by Reservation ===");
        for (Map.Entry<String, List<Service>> entry : reservationServices.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
        }
    }
}

/**
 * Simulated booking service (simplified)
 */
class BookingService {
    private Map<String, String> confirmedReservations = new HashMap<>(); // reservationID -> guestName
    private int reservationCounter = 1;

    /**
     * Confirm a reservation (returns unique reservation ID)
     */
    public String confirmReservation(String guestName) {
        String reservationId = "R" + (reservationCounter++);
        confirmedReservations.put(reservationId, guestName);
        System.out.println("Reservation confirmed for " + guestName + " | ID: " + reservationId);
        return reservationId;
    }
}

/**
 * Application Entry Point
 */
public class BookMyStay {

    public static void main(String[] args) {

        BookingService bookingService = new BookingService();
        AddOnServiceManager addOnManager = new AddOnServiceManager();

        // Confirm some reservations
        String res1 = bookingService.confirmReservation("Alice");
        String res2 = bookingService.confirmReservation("Bob");

        // Define optional services
        Service breakfast = new Service("Breakfast", 500);
        Service spa = new Service("Spa", 1200);
        Service airportPickup = new Service("Airport Pickup", 800);

        // Add services to reservations
        addOnManager.addServices(res1, Arrays.asList(breakfast, spa));
        addOnManager.addServices(res2, Collections.singletonList(airportPickup));

        // Display service mapping
        addOnManager.displayServices();

        // Calculate total add-on costs
        System.out.println("\nTotal add-on cost for " + res1 + ": ₹" + addOnManager.calculateTotalCost(res1));
        System.out.println("Total add-on cost for " + res2 + ": ₹" + addOnManager.calculateTotalCost(res2));
    }
}