import java.io.Serializable;

/**
 * This class represents the state of the elevator system at a given time.
 * The state is used to distinguish between the roles and functions that the elevator
 * system can perform at a given instance. In particuler, the class encapsules the following info about
 * the elevator: floor level, direction elevator is headed to
 *
 * Used as a serializable event to be sent to the scheduler.
 *
 * @author Mohammed Abu Alkhair
 *
 */
public class ElevatorState implements Serializable {

    private boolean permanentFault;
    private boolean recoverableFault;
    private Direction direction; //direction the elevator is going toward
    private int floorLevel; //the floor the elevator is currently at
    private boolean idleStatus; // whether elevator is servicing a command or not

    private int passengerCount;

    private int id;


    /**
     * Constructor for class ElevatorState. The initial state has the following info:
     * elevator direction is up, floor level is 1, and elevator is idle
     */
    public ElevatorState() {
        direction = Direction.UP;
        floorLevel = 1;
        idleStatus = true;
    }


    /**
     * Constructor that will be used to send information to the scheduler
     * @param direction The elevators direction
     * @param floorLevel The current floor of the elevator
     * @param idleStatus The idle status of the elevator
     * @param id The elevator id
     */
    public ElevatorState(Direction direction, int floorLevel, boolean idleStatus, int id) {
        this.direction = direction;
        this.floorLevel = floorLevel;
        this.idleStatus = idleStatus;
        this.id = id;
    }

    /**
     * Constructor that will be used for the Elevator Subsystem GUI
     * @param direction The elevators direction
     * @param floorLevel The current floor of the elevator
     * @param idleStatus The idle status of the elevator
     * @param id The elevator id
     * @param passengerCount The number of passengers
     * @param recoverableFault True if the elevator is on a recoverable fault floor, false otherwise
     * @param permanentFault True if the elevator is on a permanent fault floor, false otherwise
     */
    public ElevatorState(Direction direction, int floorLevel, boolean idleStatus, int id, int passengerCount,
                         boolean recoverableFault, boolean permanentFault) {
        this.direction = direction;
        this.floorLevel = floorLevel;
        this.idleStatus = idleStatus;
        this.id = id;
        this.passengerCount = passengerCount;
        this.recoverableFault = recoverableFault;
        this.permanentFault = permanentFault;
    }

    /**
     * Getter method for the direction the elevator is moving towards
     * @return direction of elevator
     */
    public Direction getDirection() {
        return direction;
    }

    /**
     * Getter method for the id of the elevator
     *
     * @return direction of elevator
     */
    public int getId() {
        return id;
    }

    /**
     * Setter method for the direction the elevator is moving towards
     * @param direction the elvator will move toward
     */
    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Getter method for the floor level the elevator is at
     * @return the floor level the elevator is currently at
     */
    public int getFloorLevel() {
        return floorLevel;
    }

    /**
     * Setter method for the floor level the elevator will be at
     * @param floorLevel the floor level the elevator is will be at
     */
    public void setFloorLevel(int floorLevel) {
        this.floorLevel = floorLevel;
    }

    /**
     * Getter method for the status of elevator
     * @return true if elevator is idle, false otherwise
     */
    public boolean isIdleStatus() {
        return idleStatus;
    }

    /**
     * Setter method for the status of elevator
     * @param idleStatus true if elevator is idle, false otherwise
     */
    public void setIdleStatus(boolean idleStatus) {
        this.idleStatus = idleStatus;
    }

    /**
     * Compares two ElevatorState objects for equality.
     * Returns true if and only if the two objects have the same floorLevel and direction.
     * @param obj the object to be compared for equality with this ElevatorState object
     * @return true if the specified object is equal to this ElevatorState object, false otherwise
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof ElevatorState)) {
            return false;
        }
        ElevatorState other = (ElevatorState) obj;
        return this.floorLevel == other.floorLevel && this.direction == other.direction;
    }

    /**
     * Returns a string representation of the ElevatorState object. The string representation
     * includes the elevator's direction, floor level and idle status.
     * @return a string representation of the ElevatorState object
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ElevatorState: ");
        sb.append("Floor Level: ").append(floorLevel);
        sb.append(", Direction: ").append(direction);
        sb.append(", Idle Status: ").append(idleStatus);
        return sb.toString();
    }

    public static void main(String args[]) {
        ElevatorState s = new ElevatorState();
        System.out.println(s);
    }

    public boolean isRecoverableFault() {
        return recoverableFault;
    }

    public boolean isPermanentFault() {
        return permanentFault;
    }

    public int getPassengerCount() {
        return passengerCount;
    }
}
