/**
 * This class represents the state of the elevator system at a given time.
 * The state is used to distinguish between the roles and functions that the elevator
 * system can perform at a given instance. In particuler, the class encapsules the following info about
 * the elevator: floor level, direction elevator is headed to
 *
 * @author Mohammed Abu Alkhair
 *
 */
public class ElevatorState {

    private Direction direction; //direction the elevator is going toward
    private int floorLevel; //the floor the elevator is currently at
    private boolean idleStatus; // whether elevator is servicing a command or not

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
     * Getter method for the direction the elevator is moving towards
     * @return direction of elevator
     */
    public synchronized Direction getDirection() {
        return direction;
    }

    /**
     * Setter method for the direction the elevator is moving towards
     * @param direction the elvator will move toward
     */
    public synchronized void setDirection(Direction direction) {
        this.direction = direction;
    }

    /**
     * Getter method for the floor level the elevator is at
     * @return the floor level the elevator is currently at
     */
    public synchronized int getFloorLevel() {
        return floorLevel;
    }

    /**
     * Setter method for the floor level the elevator will be at
     * @param floorLevel the floor level the elevator is will be at
     */
    public synchronized void setFloorLevel(int floorLevel) {
        this.floorLevel = floorLevel;
    }

    /**
     * Go up 1 floor
     */
    public void goUp() {
        this.floorLevel++;
    }

    /**
     * Go down 1 floor
     */
    public void goDown() {
        this.floorLevel--;
    }

    /**
     * Getter method for the status of elevator
     * @return true if elevator is idle, false otherwise
     */
    public synchronized boolean isIdleStatus() {
        return idleStatus;
    }

    /**
     * Setter method for the status of elevator
     * @param idleStatus true if elevator is idle, false otherwise
     */
    public synchronized void setIdleStatus(boolean idleStatus) {
        this.idleStatus = idleStatus;
    }
}