import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

class Car {
    private String carId;
    private String brand;
    private String model;
    private double basePricePerDay;
    private boolean isAvailable;

    public Car(String carId, String brand, String model, double basePricePerDay) {
        this.carId = carId;
        this.brand = brand;
        this.model = model;
        this.basePricePerDay = basePricePerDay;
        this.isAvailable = true;
    }
    public String getCarId() {
        return carId;
    }

    public String getBrand() {
        return brand;
    }

    public String getModel() {
        return model;
    }

    public double calculatePrice(int rentalDays) {
        return basePricePerDay * rentalDays;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void rent() {
        isAvailable = false;
    }

    public void returnCar() {
        isAvailable = true;
    }
}

class Customer {
    private String customerId;
    private String name;

    public Customer(String customerId, String name) {
        this.customerId = customerId;
        this.name = name;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getName() {
        return name;
    }
}

class Rental {
    private Car car;
    private Customer customer;
    private int days;

    public Rental(Car car, Customer customer, int days) {
        this.car = car;
        this.customer = customer;
        this.days = days;
    }

    public Car getCar() {
        return car;
    }

    public Customer getCustomer() {
        return customer;
    }

    public int getDays() {
        return days;
    }
}

class CarRentalSystem {
    private List<Car> cars;
    private List<Customer> customers;
    private List<Rental> rentals;

    public CarRentalSystem() {
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();
    }

    public void addCar(Car car) {
        cars.add(car);
    }

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void rentCar(Car car, Customer customer, int days) {
        if (car.isAvailable()) {
            car.rent();
            rentals.add(new Rental(car, customer, days));

        } else {
            System.out.println("Car is not available for rent.");
        }
    }

    public void returnCar(Car car) {
        car.returnCar();
        Rental rentalToRemove = null;
        for (Rental rental : rentals) {
            if (rental.getCar() == car) {
                rentalToRemove = rental;
                break;
            }
        }
        if (rentalToRemove != null) {
            rentals.remove(rentalToRemove);

        } else {
            System.out.println("Car was not rented.");
        }
    }

    public void menu() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("===== Car Rental System =====");
            System.out.println("1. Rent a Car");
            System.out.println("2. Return a Car");
            System.out.println("3. Exit");
            System.out.print("Enter your choice: ");

            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (choice == 1) {
                System.out.println("\n== Rent a Car ==\n");
                System.out.print("Enter your name: ");
                String customerName = scanner.nextLine();

                System.out.println("\nAvailable Cars:");
                for (Car car : cars) {
                    if (car.isAvailable()) {
                        System.out.println(car.getCarId() + " - " + car.getBrand() + " " + car.getModel());
                    }
                }

                System.out.print("\nEnter the car ID you want to rent: ");
                String carId = scanner.nextLine();

                System.out.print("Enter the number of days for rental: ");
                int rentalDays = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                Customer newCustomer = new Customer("CUS" + (customers.size() + 1), customerName);
                addCustomer(newCustomer);

                Car selectedCar = null;
                for (Car car : cars) {
                    if (car.getCarId().equals(carId) && car.isAvailable()) {
                        selectedCar = car;
                        break;
                    }
                }

                if (selectedCar != null) {
                    double totalPrice = selectedCar.calculatePrice(rentalDays);
                    System.out.println("\n== Rental Information ==\n");
                    System.out.println("Customer ID: " + newCustomer.getCustomerId());
                    System.out.println("Customer Name: " + newCustomer.getName());
                    System.out.println("Car: " + selectedCar.getBrand() + " " + selectedCar.getModel());
                    System.out.println("Rental Days: " + rentalDays);
                    System.out.printf("Total Price: $%.2f%n", totalPrice);

                    System.out.print("\nConfirm rental (Y/N): ");
                    String confirm = scanner.nextLine();

                    if (confirm.equalsIgnoreCase("Y")) {
                        rentCar(selectedCar, newCustomer, rentalDays);
                        System.out.println("\nCar rented successfully.");
                    } else {
                        System.out.println("\nRental canceled.");
                    }
                } else {
                    System.out.println("\nInvalid car selection or car not available for rent.");
                }
            } else if (choice == 2) {
                System.out.println("\n== Return a Car ==\n");
                System.out.print("Enter the car ID you want to return: ");
                String carId = scanner.nextLine();

                Car carToReturn = null;
                for (Car car : cars) {
                    if (car.getCarId().equals(carId) && !car.isAvailable()) {
                        carToReturn = car;
                        break;
                    }
                }

                if (carToReturn != null) {
                    Customer customer = null;
                    for (Rental rental : rentals) {
                        if (rental.getCar() == carToReturn) {
                            customer = rental.getCustomer();
                            break;
                        }
                    }

                    if (customer != null) {
                        returnCar(carToReturn);
                        System.out.println("Car returned successfully by " + customer.getName());
                    } else {
                        System.out.println("Car was not rented or rental information is missing.");
                    }
                } else {
                    System.out.println("Invalid car ID or car is not rented.");
                }
            } else if (choice == 3) {
                break;
            } else {
                System.out.println("Invalid choice. Please enter a valid option.");
            }
        }

        System.out.println("\nThank you for using the Car Rental System!");
    }

}

class CarRentalGUI {
    private JFrame frame;
    private DefaultListModel<Car> carListModel;
    private JList<Car> carJList;
    private List<Car> cars;
    private List<Customer> customers;
    private List<Rental> rentals;

    public CarRentalGUI() {
        // Initialize lists
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();

        // Setup Cars
        cars.add(new Car("C001", "Toyota", "Camry", 60.0));
        cars.add(new Car("C002", "Honda", "Accord", 70.0));
        cars.add(new Car("C003", "Mahindra", "Thar", 150.0));

        // Setup GUI
        frame = new JFrame("Car Rental System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 400);
        frame.setLayout(new BorderLayout());

        // Create car list
        carListModel = new DefaultListModel<>();
        cars.forEach(carListModel::addElement);

        carJList = new JList<>(carListModel);
        carJList.setCellRenderer(new CarListRenderer());
        JScrollPane scrollPane = new JScrollPane(carJList);
        frame.add(scrollPane, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonPanel = new JPanel();
        JButton rentButton = new JButton("Rent Car");
        JButton returnButton = new JButton("Return Car");
        JButton addCarButton = new JButton("Add New Car");

        rentButton.addActionListener(e -> rentCar());
        returnButton.addActionListener(e -> returnCar());
        addCarButton.addActionListener(e -> addNewCar());

        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(addCarButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    private void rentCar() {
        Car selectedCar = carJList.getSelectedValue();
        if (selectedCar == null || !selectedCar.isAvailable()) {
            JOptionPane.showMessageDialog(frame, "Please select an available car.");
            return;
        }

        String name = JOptionPane.showInputDialog(frame, "Enter customer name:");
        if (name == null || name.trim().isEmpty()) return;

        String daysStr = JOptionPane.showInputDialog(frame, "Enter number of days:");
        int days;
        try {
            days = Integer.parseInt(daysStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid number of days.");
            return;
        }

        double price = selectedCar.calculatePrice(days);
        int confirm = JOptionPane.showConfirmDialog(frame, 
            String.format("Total price: $%.2f\nConfirm rental?", price), 
            "Confirm Rental", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Customer customer = new Customer("CUS" + (customers.size() + 1), name);
            customers.add(customer);
            selectedCar.rent();
            rentals.add(new Rental(selectedCar, customer, days));
            refreshCarList();
            JOptionPane.showMessageDialog(frame, "Car rented successfully!");
        }
    }

    private void returnCar() {
        Car selectedCar = carJList.getSelectedValue();
        if (selectedCar == null || selectedCar.isAvailable()) {
            JOptionPane.showMessageDialog(frame, "Please select a rented car.");
            return;
        }

        Rental rentalToRemove = null;
        for (Rental rental : rentals) {
            if (rental.getCar() == selectedCar) {
                rentalToRemove = rental;
                break;
            }
        }

        if (rentalToRemove != null) {
            Customer customer = rentalToRemove.getCustomer();
            selectedCar.returnCar();
            rentals.remove(rentalToRemove);
            refreshCarList();
            JOptionPane.showMessageDialog(frame, "Car returned by " + customer.getName());
        }
    }

    private void addNewCar() {
        String carId = JOptionPane.showInputDialog(frame, "Enter car ID:");
        if (carId == null || carId.trim().isEmpty()) return;

        String brand = JOptionPane.showInputDialog(frame, "Enter car brand:");
        if (brand == null || brand.trim().isEmpty()) return;

        String model = JOptionPane.showInputDialog(frame, "Enter car model:");
        if (model == null || model.trim().isEmpty()) return;

        String priceStr = JOptionPane.showInputDialog(frame, "Enter base price per day:");
        double price;
        try {
            price = Double.parseDouble(priceStr);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(frame, "Please enter a valid price.");
            return;
        }

        Car newCar = new Car(carId, brand, model, price);
        cars.add(newCar);
        refreshCarList();
        JOptionPane.showMessageDialog(frame, "New car added successfully!");
    }

    private void refreshCarList() {
        carListModel.clear();
        for (Car car : cars) {
            carListModel.addElement(car);
        }
    }

    private class CarListRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Car) {
                Car car = (Car) value;
                setText(String.format("%s - %s %s (%s)", 
                    car.getCarId(), 
                    car.getBrand(), 
                    car.getModel(),
                    car.isAvailable() ? "Available" : "Rented"));
            }
            return this;
        }
    }
}

public class Carrentalesystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CarRentalGUI());
    }
}