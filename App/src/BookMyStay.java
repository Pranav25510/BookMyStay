/**
 * Abstract class representing a generic Room
 */
abstract class Room {
    private int beds;
    private double price;
    private String type;

    public Room(String type, int beds, double price) {
        this.type = type;
        this.beds = beds;
        this.price = price;
    }

    public int getBeds() {
        return beds;
    }

    public double getPrice() {
        return price;
    }

    public String getType() {
        return type;
    }

    // Abstract method (can be extended later)
    public abstract void displayDetails();
}

/**
 * Single Room implementation
 */
class SingleRoom extends Room {

    public SingleRoom() {
        super("Single Room", 1, 2000);
    }

    @Override
    public void displayDetails() {
        System.out.println(getType() + " | Beds: " + getBeds() + " | Price: ₹" + getPrice());
    }
}

/**
 * Double Room implementation
 */
class DoubleRoom extends Room {

    public DoubleRoom() {
        super("Double Room", 2, 3500);
    }

    @Override
    public void displayDetails() {
        System.out.println(getType() + " | Beds: " + getBeds() + " | Price: ₹" + getPrice());
    }
}

/**
 * Suite Room implementation
 */
class SuiteRoom extends Room {

    public SuiteRoom() {
        super("Suite Room", 3, 6000);
    }

    @Override
    public void displayDetails() {
        System.out.println(getType() + " | Beds: " + getBeds() + " | Price: ₹" + getPrice());
    }
}

/**
 * Application Entry Point
 */
public class BookMyStay {

    public static void main(String[] args) {

        System.out.println("==== Available Room Types ====\n");

        // Polymorphism: using Room reference
        Room single = new SingleRoom();
        Room doubleRoom = new DoubleRoom();
        Room suite = new SuiteRoom();

        // Static availability (simple variables)
        int singleAvailable = 5;
        int doubleAvailable = 3;
        int suiteAvailable = 2;

        // Display room details + availability
        single.displayDetails();
        System.out.println("Available: " + singleAvailable + "\n");

        doubleRoom.displayDetails();
        System.out.println("Available: " + doubleAvailable + "\n");

        suite.displayDetails();
        System.out.println("Available: " + suiteAvailable + "\n");

        System.out.println("Application समाप्त.");
    }
}