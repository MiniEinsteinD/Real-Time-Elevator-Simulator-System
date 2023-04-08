import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 * This Frame represents the elevator subsystem. It shows each elevator along with its
 * status and its relevant information.
 * @author Ali El-Khatib
 * @version v1
 */
public class ElevatorFrame implements ElevatorView {
    private int numberOfElevators;
    private int numberOfFloors;

    private DefaultTableModel tableModel;

    /**
     * Creates a table along with columns for relevant elevator details
     * @param numberOfElevators The number of elevators
     * @param numberOfFloors The number of floors
     */
    public ElevatorFrame(int numberOfElevators, int numberOfFloors) {
        tableModel = new DefaultTableModel(new Object[]{"Elevator_ID", "Direction", "Floor", "Idle Status",
                "Passengers", "Recoverable Fault", "Permanent Fault"}, numberOfFloors);

        JTable table = new JTable(tableModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                //all cells false
                return false;
            }
        };

        JFrame f = new JFrame();
        f.setSize(550, 350);
        f.add(new JScrollPane(table));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(800, 300);
        f.setLocationRelativeTo(null);
        f.setVisible(true);
    }

    @Override
    public int getNumberOfElevators() {
        return numberOfElevators;
    }

    @Override
    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    /**
     * Updates the table with the new state
     * @param elevatorState The elevator state
     */
    public void update(ElevatorState elevatorState) {
        tableModel.setValueAt(elevatorState.getId(), elevatorState.getId() - 1, 0);
        tableModel.setValueAt(elevatorState.getDirection(), elevatorState.getId() - 1, 1);
        tableModel.setValueAt(elevatorState.getFloorLevel(), elevatorState.getId() - 1, 2);
        tableModel.setValueAt(elevatorState.isIdleStatus(), elevatorState.getId() - 1, 3);
        tableModel.setValueAt(elevatorState.getPassengerCount(), elevatorState.getId() - 1, 4);
        tableModel.setValueAt(elevatorState.isRecoverableFault(), elevatorState.getId() - 1, 5);
        tableModel.setValueAt(elevatorState.isPermanentFault(), elevatorState.getId() - 1, 6);
    }
}
