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

    private Direction direction;
    private int floorLevel;
    private boolean idleStatus;

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
    public Direction getDirection() {
        return direction;
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
}
