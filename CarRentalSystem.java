import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.text.NumberFormat;
import java.util.Locale;

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

    public String getCarId() { return carId; }
    public String getBrand() { return brand; }
    public String getModel() { return model; }
    public double calculatePrice(int days) { return basePricePerDay * days; }
    public boolean isAvailable() { return isAvailable; }
    public void rent() { isAvailable = false; }
    public void returnCar() { isAvailable = true; }

    public String toString() {
        return carId + " - " + brand + " " + model + (isAvailable ? " (Available)" : " (Rented)");
    }
}

class Customer {
    private String customerId;
    private String name;
    private String phoneNumber;
    private List<Rental> rentalHistory;

    public Customer(String customerId, String name, String phoneNumber) {
        this.customerId = customerId;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.rentalHistory = new ArrayList<>();
    }

    public String getCustomerId() { return customerId; }
    public String getName() { return name; }
    public String getPhoneNumber() { return phoneNumber; }
    public List<Rental> getRentalHistory() { return rentalHistory; }
    public void addRental(Rental rental) { rentalHistory.add(rental); }
}

class Rental {
    private Car car;
    private Customer customer;
    private int days;
    private LocalDateTime rentalDate;
    private LocalDateTime returnDate;
    private boolean isReturned;
    private double totalPrice;

    public Rental(Car car, Customer customer, int days) {
        this.car = car;
        this.customer = customer;
        this.days = days;
        this.rentalDate = LocalDateTime.now();
        this.isReturned = false;
        this.totalPrice = car.calculatePrice(days);
    }

    public Car getCar() { return car; }
    public Customer getCustomer() { return customer; }
    public int getDays() { return days; }
    public LocalDateTime getRentalDate() { return rentalDate; }
    public LocalDateTime getReturnDate() { return returnDate; }
    public boolean isReturned() { return isReturned; }
    public double getTotalPrice() { return totalPrice; }
    
    public void markAsReturned() { 
        this.isReturned = true;
        this.returnDate = LocalDateTime.now();
    }
}

public class CarRentalSystem {
    private JFrame frame;
    private DefaultListModel<Car> carListModel;
    private DefaultListModel<Rental> rentalListModel;
    private JList<Car> carJList;
    private JList<Rental> rentalJList;
    private final List<Car> cars;
    private final List<Customer> customers;
    private final List<Rental> rentals;
    private JTextField searchField;
    private JComboBox<String> filterComboBox;
    private JLabel statsLabel;
    
    // Currency formatter for Indian Rupees
    private static final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    
    // Color scheme
    private static final Color PRIMARY_COLOR = new Color(41, 128, 185);    // Blue
    private static final Color SECONDARY_COLOR = new Color(52, 152, 219);  // Light Blue
    private static final Color BACKGROUND_COLOR = new Color(236, 240, 241); // Light Gray
    private static final Color TEXT_COLOR = new Color(44, 62, 80);         // Dark Blue
    private static final Color SUCCESS_COLOR = new Color(46, 204, 113);    // Green
    private static final Color WARNING_COLOR = new Color(231, 76, 60);     // Red

    public CarRentalSystem() {
        // Initialize lists
        cars = new ArrayList<>();
        customers = new ArrayList<>();
        rentals = new ArrayList<>();

        // Setup Cars with prices in INR
        cars.add(new Car("C001", "Toyota", "Camry", 5000.0));  // ₹5,000 per day
        cars.add(new Car("C002", "Honda", "Accord", 6000.0));  // ₹6,000 per day
        cars.add(new Car("C003", "Mahindra", "Thar", 12000.0)); // ₹12,000 per day

        // Setup GUI
        frame = new JFrame("Car Rental System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 600);
        frame.setLayout(new BorderLayout(10, 10));
        frame.getContentPane().setBackground(BACKGROUND_COLOR);

        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(PRIMARY_COLOR);
        titlePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JLabel titleLabel = new JLabel("Car Rental System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        frame.add(titlePanel, BorderLayout.NORTH);

        // Create main content panel
        JPanel contentPanel = new JPanel(new BorderLayout(10, 0));
        contentPanel.setBackground(BACKGROUND_COLOR);

        // Left panel - Car List
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel carListLabel = new JLabel("Available Cars", SwingConstants.CENTER);
        carListLabel.setFont(new Font("Arial", Font.BOLD, 16));
        carListLabel.setForeground(TEXT_COLOR);
        leftPanel.add(carListLabel, BorderLayout.NORTH);

        carListModel = new DefaultListModel<>();
        cars.forEach(carListModel::addElement);

        carJList = new JList<>(carListModel);
        carJList.setCellRenderer(new CarListRenderer());
        carJList.setBackground(Color.WHITE);
        carJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        carJList.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane carScrollPane = new JScrollPane(carJList);
        carScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2)
        ));
        leftPanel.add(carScrollPane, BorderLayout.CENTER);

        // Right panel - Rental History
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(BACKGROUND_COLOR);

        // Create rental history header panel
        JPanel rentalHeaderPanel = new JPanel(new BorderLayout());
        rentalHeaderPanel.setBackground(BACKGROUND_COLOR);
        
        JLabel rentalListLabel = new JLabel("Rental History", SwingConstants.CENTER);
        rentalListLabel.setFont(new Font("Arial", Font.BOLD, 16));
        rentalListLabel.setForeground(TEXT_COLOR);
        rentalHeaderPanel.add(rentalListLabel, BorderLayout.NORTH);

        // Create search and filter panel
        JPanel searchFilterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchFilterPanel.setBackground(BACKGROUND_COLOR);
        
        searchField = new JTextField(15);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));
        searchField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                filterRentals();
            }
        });
        
        String[] filterOptions = {"All", "Active", "Returned", "Last 7 Days", "Last 30 Days"};
        filterComboBox = new JComboBox<>(filterOptions);
        filterComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        filterComboBox.addActionListener(e -> filterRentals());
        
        searchFilterPanel.add(new JLabel("Search: "));
        searchFilterPanel.add(searchField);
        searchFilterPanel.add(new JLabel("Filter: "));
        searchFilterPanel.add(filterComboBox);
        
        rentalHeaderPanel.add(searchFilterPanel, BorderLayout.CENTER);
        rightPanel.add(rentalHeaderPanel, BorderLayout.NORTH);

        // Create rental list
        rentalListModel = new DefaultListModel<>();
        rentalJList = new JList<>(rentalListModel);
        rentalJList.setCellRenderer(new RentalListRenderer());
        rentalJList.setBackground(Color.WHITE);
        rentalJList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rentalJList.setFont(new Font("Arial", Font.PLAIN, 14));
        
        JScrollPane rentalScrollPane = new JScrollPane(rentalJList);
        rentalScrollPane.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(10, 10, 10, 10),
            BorderFactory.createLineBorder(PRIMARY_COLOR, 2)
        ));
        rightPanel.add(rentalScrollPane, BorderLayout.CENTER);

        // Create statistics panel
        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        statsPanel.setBackground(BACKGROUND_COLOR);
        statsLabel = new JLabel();
        statsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        statsLabel.setForeground(TEXT_COLOR);
        statsPanel.add(statsLabel);
        rightPanel.add(statsPanel, BorderLayout.SOUTH);

        // Add panels to content panel
        contentPanel.add(leftPanel, BorderLayout.WEST);
        contentPanel.add(rightPanel, BorderLayout.CENTER);
        frame.add(contentPanel, BorderLayout.CENTER);

        // Create buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        
        JButton rentButton = createStyledButton("Rent Car", SUCCESS_COLOR);
        JButton returnButton = createStyledButton("Return Car", WARNING_COLOR);
        JButton addCarButton = createStyledButton("Add New Car", SECONDARY_COLOR);

        rentButton.addActionListener(e -> rentCar());
        returnButton.addActionListener(e -> returnCar());
        addCarButton.addActionListener(e -> addNewCar());

        buttonPanel.add(rentButton);
        buttonPanel.add(returnButton);
        buttonPanel.add(addCarButton);
        frame.add(buttonPanel, BorderLayout.SOUTH);

        // Center the frame on screen
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        updateStatistics();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(color);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                button.setBackground(color.darker());
            }
            public void mouseExited(MouseEvent e) {
                button.setBackground(color);
            }
        });
        
        return button;
    }

    private void rentCar() {
        Car selectedCar = carJList.getSelectedValue();
        if (selectedCar == null || !selectedCar.isAvailable()) {
            JOptionPane.showMessageDialog(frame, "Please select an available car.");
            return;
        }

        String name = JOptionPane.showInputDialog(frame, "Enter customer name:");
        if (name == null || name.trim().isEmpty()) return;

        String phone = JOptionPane.showInputDialog(frame, "Enter customer phone number:");
        if (phone == null || phone.trim().isEmpty()) return;

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
            String.format("Total price: %s\nConfirm rental?", currencyFormatter.format(price)), 
            "Confirm Rental", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            Customer customer = new Customer("CUS" + (customers.size() + 1), name, phone);
            customers.add(customer);
            selectedCar.rent();
            Rental rental = new Rental(selectedCar, customer, days);
            rentals.add(rental);
            customer.addRental(rental);
            refreshCarList();
            refreshRentalList();
            updateStatistics();
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
            if (rental.getCar() == selectedCar && !rental.isReturned()) {
                rentalToRemove = rental;
                break;
            }
        }

        if (rentalToRemove != null) {
            Customer customer = rentalToRemove.getCustomer();
            selectedCar.returnCar();
            rentalToRemove.markAsReturned();
            refreshCarList();
            refreshRentalList();
            updateStatistics();
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

    private void refreshRentalList() {
        filterRentals(); // This will also update statistics
    }

    private void filterRentals() {
        String searchText = searchField.getText().toLowerCase();
        String filterOption = (String) filterComboBox.getSelectedItem();
        
        rentalListModel.clear();
        for (Rental rental : rentals) {
            if (matchesFilter(rental, searchText, filterOption)) {
                rentalListModel.addElement(rental);
            }
        }
        updateStatistics();
    }

    private boolean matchesFilter(Rental rental, String searchText, String filterOption) {
        // Search text matching
        boolean matchesSearch = searchText.isEmpty() ||
            rental.getCar().getCarId().toLowerCase().contains(searchText) ||
            rental.getCar().getBrand().toLowerCase().contains(searchText) ||
            rental.getCar().getModel().toLowerCase().contains(searchText) ||
            rental.getCustomer().getName().toLowerCase().contains(searchText) ||
            rental.getCustomer().getPhoneNumber().contains(searchText);

        if (!matchesSearch) return false;

        // Filter option matching
        switch (filterOption) {
            case "Active":
                return !rental.isReturned();
            case "Returned":
                return rental.isReturned();
            case "Last 7 Days":
                return ChronoUnit.DAYS.between(rental.getRentalDate(), LocalDateTime.now()) <= 7;
            case "Last 30 Days":
                return ChronoUnit.DAYS.between(rental.getRentalDate(), LocalDateTime.now()) <= 30;
            default:
                return true;
        }
    }

    private void updateStatistics() {
        int totalRentals = rentals.size();
        int activeRentals = (int) rentals.stream().filter(r -> !r.isReturned()).count();
        double totalRevenue = rentals.stream().mapToDouble(Rental::getTotalPrice).sum();
        
        statsLabel.setText(String.format(
            "Total Rentals: %d | Active Rentals: %d | Total Revenue: %s",
            totalRentals, activeRentals, currencyFormatter.format(totalRevenue)
        ));
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
                
                setFont(new Font("Arial", Font.PLAIN, 14));
                setForeground(TEXT_COLOR);
                
                if (isSelected) {
                    setBackground(SECONDARY_COLOR);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(car.isAvailable() ? Color.WHITE : new Color(255, 240, 240));
                }
                
                // Add padding
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            }
            return this;
        }
    }

    private class RentalListRenderer extends DefaultListCellRenderer {
        private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            if (value instanceof Rental) {
                Rental rental = (Rental) value;
                Car car = rental.getCar();
                Customer customer = rental.getCustomer();
                
                String status = rental.isReturned() ? "Returned" : "Active";
                Color statusColor = rental.isReturned() ? SUCCESS_COLOR : WARNING_COLOR;
                
                setText(String.format("<html><b>%s</b> - %s %s<br>Customer: %s (%s)<br>Rented on: %s<br>Duration: %d days<br>Price: %s<br>Status: <font color='%s'>%s</font></html>",
                    car.getCarId(),
                    car.getBrand(),
                    car.getModel(),
                    customer.getName(),
                    customer.getPhoneNumber(),
                    rental.getRentalDate().format(formatter),
                    rental.getDays(),
                    currencyFormatter.format(rental.getTotalPrice()),
                    String.format("#%02x%02x%02x", statusColor.getRed(), statusColor.getGreen(), statusColor.getBlue()),
                    status
                ));
                
                setFont(new Font("Arial", Font.PLAIN, 14));
                setForeground(TEXT_COLOR);
                
                if (isSelected) {
                    setBackground(SECONDARY_COLOR);
                    setForeground(Color.WHITE);
                } else {
                    setBackground(rental.isReturned() ? new Color(240, 255, 240) : new Color(255, 240, 240));
                }
                
                setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            }
            return this;
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> new CarRentalSystem());
    }
} 