import javax.swing.*;
/**
 * The ElevatorSystemPopup class is a JFrame that displays a pop-up window for configuring an elevator system.
 * This class provides getters for retrieving the selected values from the UI components.
 */
public class ElevatorSystemPopup extends JFrame {
    private JComboBox<Integer> elevatorsComboBox;
    private JComboBox<Integer> floorsComboBox;
    private JRadioButton singleComputerRadioButton;
    private JRadioButton multipleComputerRadioButton;
    private JComboBox<String> subsystemComboBox;
    private JTextField hostNameTextField;
    private int numElevators;
    private int numFloors;
    private String hostName;
    private boolean multipleComputers;
    private SystemType type;
    public enum SystemType{FloorSubsystem, SchedulerSubsystem, ElevatorSubsystem}

    /**
     * Constructs an ElevatorSystemPopup object with a JFrame for configuring the elevator system.
     */
    public ElevatorSystemPopup() {
        // Set up the frame
        super("Elevator System Configuration");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(500, 300);
        setLocationRelativeTo(null);

        // Set up the UI components
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        add(panel);

        // Set up the number of elevators and floors combo boxes
        JLabel elevatorsLabel = new JLabel("Number of Elevators:");
        elevatorsComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5});
        JPanel elevatorsPanel = new JPanel();
        elevatorsPanel.add(elevatorsLabel);
        elevatorsPanel.add(elevatorsComboBox);
        panel.add(elevatorsPanel);

        JLabel floorsLabel = new JLabel("Number of Floors:");
        floorsComboBox = new JComboBox<>(new Integer[]{5, 6, 7, 8, 9, 10});
        JPanel floorsPanel = new JPanel();
        floorsPanel.add(floorsLabel);
        floorsPanel.add(floorsComboBox);
        panel.add(floorsPanel);

        // Set up the single/multiple computer radio buttons
        singleComputerRadioButton = new JRadioButton("Single Computer", true);
        multipleComputerRadioButton = new JRadioButton("Multiple Computers");
        ButtonGroup computerGroup = new ButtonGroup();
        computerGroup.add(singleComputerRadioButton);
        computerGroup.add(multipleComputerRadioButton);
        JPanel computerPanel = new JPanel();
        computerPanel.add(singleComputerRadioButton);
        computerPanel.add(multipleComputerRadioButton);
        panel.add(computerPanel);

        // Set up the subsystem combo box and host name text field
        JLabel subsystemLabel = new JLabel("Subsystem:");
        subsystemComboBox = new JComboBox<>(new String[]{"FloorSubsystem", "ElevatorSubsystem", "SchedulerSubsystem"});
        JLabel hostNameLabel = new JLabel("Host Name:");
        hostNameTextField = new JTextField(10);
        hostNameTextField.setEnabled(false);
        JPanel subsystemPanel = new JPanel();
        subsystemPanel.add(subsystemLabel);
        subsystemPanel.add(subsystemComboBox);
        subsystemPanel.add(hostNameLabel);
        subsystemPanel.add(hostNameTextField);
        panel.add(subsystemPanel);

        // Add listener to the multipleComputerRadioButton to enable/disable the hostNameTextField
        multipleComputerRadioButton.addActionListener(e -> {
            hostNameTextField.setEnabled(true);
            subsystemComboBox.setEnabled(true);
        });

        // Add listener to the singleComputerRadioButton to disable the hostNameTextField
        singleComputerRadioButton.addActionListener(e -> {
            hostNameTextField.setEnabled(false);
            subsystemComboBox.setEnabled(false);
        });

        // Set up the OK button
        JButton okButton = new JButton("OK");
        okButton.addActionListener(e -> {
            if (singleComputerRadioButton.isSelected()) {
                // If single computer is selected, set the subsystem to FloorSubsystem
                type = SystemType.FloorSubsystem;
                hostName = null;
            } else {
                // If multiple computers are selected, get the selected subsystem and host name
                type = SystemType.valueOf((String) subsystemComboBox.getSelectedItem());
                hostName = hostNameTextField.getText();
            }

            // Get the selected number of elevators and floors
            numElevators = (Integer) elevatorsComboBox.getSelectedItem();
            numFloors = (Integer) floorsComboBox.getSelectedItem();

            // Set the multipleComputers flag based on the radio button selection
            multipleComputers = multipleComputerRadioButton.isSelected();

            // Close the pop-up window
            dispose();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(okButton);
        panel.add(buttonPanel);

        // Display the frame
        setVisible(true);

        // Wait for the frame to be closed
        try {
            while (isVisible()) {
                Thread.sleep(100);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get the host name entered in the pop-up window.
     * @return the host name entered in the pop-up window, or null if the single
     *         computer option was selected
     */
    public String getHostName() {
        return hostName;
    }

    /**
     * Gets the number of elevators selected in the pop-up window.
     * @return the number of elevators selected in the pop-up window
     */
    public int getNumElevators() {
        return numElevators;
    }

    /**
     * Gets the number of floors selected in the pop-up window.
     * @return the number of floors selected in the pop-up window
     */
    public int getNumFloors() {
        return numFloors;
    }

    /**
     * Returns whether the multiple computers option was selected in the pop-up
     * window.
     * @return true if the multiple computers option was selected, false otherwise
     */
    public boolean isMultipleComputers() {
        return multipleComputers;
    }

    /**
     * Gets the subsystem type selected in the pop-up window.
     * @return the subsystem type selected in the pop-up window
     */
    public SystemType getSystemType() {
        return type;
    }

    public static void main(String[] args) {
        ElevatorSystemPopup popup = new ElevatorSystemPopup();
        System.out.println(popup.getHostName());
    }
}