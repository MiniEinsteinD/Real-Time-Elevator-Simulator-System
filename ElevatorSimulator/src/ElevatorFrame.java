import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class ElevatorFrame implements ElevatorView {
    private int numberOfElevators;
    private int numberOfFloors;

    private DefaultTableModel tableModel;
    public ElevatorFrame(int numberOfElevators, int numberOfFloors) {
        tableModel = new DefaultTableModel(new Object[]{"Elevator_ID", "Direction", "Floor"}, numberOfFloors);
        JTable table = new JTable(tableModel);
        JFrame f = new JFrame();
        f.setSize(550, 350);
        f.add(new JScrollPane(table));
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setSize(500, 300);
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

    public void update(ElevatorState elevatorState) {
        tableModel.setValueAt(elevatorState.getId(), elevatorState.getId() - 1, 0);
        tableModel.setValueAt(elevatorState.getDirection(), elevatorState.getId() - 1, 1);
        tableModel.setValueAt(elevatorState.getFloorLevel(), elevatorState.getId() - 1, 2);
    }
}
