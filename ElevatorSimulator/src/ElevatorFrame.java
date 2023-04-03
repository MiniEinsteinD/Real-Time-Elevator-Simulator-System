public class ElevatorFrame implements ElevatorView {
    private int numberOfElevators;
    private int numberOfFloors;

    private void startupFrame() {

    }

    @Override
    public int getNumberOfElevators() {
        return numberOfElevators;
    }

    @Override
    public int getNumberOfFloors() {
        return numberOfFloors;
    }
}
