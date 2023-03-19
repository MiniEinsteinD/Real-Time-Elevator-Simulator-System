import java.io.Serializable;

/**
 * The components are all assembled into 1 object that can be broken down to obtain all attributes.
 * Will be used in the elevator class to enqueue a list of commands for the
 * elevator (will support multiple elevators in future iterations)
 * @author Hasan Al-Hasoo
 * @author Edited By Mohammed
 * @version 1.0
 */
public class Command implements Serializable {

    private int time; //Integer representation of the time
    private int floor; //The floor number we wish to travel to
    private int elevatorButton; //The button selected in the elevator car
    private Direction directionButton; //Direction enum (can be up or down)

    private boolean recoverableFault;
    private boolean permanentFault;

    /**
     * Constructor takes in a string AFTER being processed into their individual components.
     * @param time the event has been composed
     * @param floor the floor number
     * @param directionButton the direction the elevator mist travel (up or down)
     * @param elevatorButton the button selected in the elevator car
     */

    public Command(int time, int floor, Direction directionButton, int elevatorButton){
        this.time = time;
        this.floor = floor;
        this.directionButton = directionButton;
        this.elevatorButton = elevatorButton;
    }

    public Command(int time, int floor, Direction directionButton, int elevatorButton, boolean recoverableFault, boolean permanentFault){
        this.time = time;
        this.floor = floor;
        this.directionButton = directionButton;
        this.elevatorButton = elevatorButton;
        this.recoverableFault = recoverableFault;
        this.permanentFault = permanentFault;
    }

    /**
     * Second command constructor that takes an unprocessed string and performs the appropriate operations
     * in order to assemble all fields
     * @param command an input stream that is picked up from the file received by floor
     */
    public Command (String command){
        String[] split = command.split(" ");
        this.time = Integer.parseInt(split[0]);
        this.floor = Integer.parseInt(split[1]);
        this.directionButton = Direction.valueOf(split[2].toUpperCase());
        this.elevatorButton = Integer.parseInt(split[3]);
        this.recoverableFault = false;
        this.permanentFault = false;
    }

    /**
     * Setter method for permanentFault field for the command object's creation.
     * @param permanentFault boolean
     */
    public void setPermanentFault(boolean permanentFault) {
        this.permanentFault = permanentFault;
    }

    /**
     * Setter method for recoverableFault field for the command object's creation.
     * @param recoverableFault boolean
     */
    public void setRecoverableFault(boolean recoverableFault) {
        this.recoverableFault = recoverableFault;
    }

    /**
     * Getter method for recoverableFault field for the command object's creation.
     * @return recoverableFault field
     */
    public boolean isRecoverableFault() {
        return recoverableFault;
    }

    /**
     * Getter method for permanentFault field for the command object's creation.
     * @return permanentFault field
     */
    public boolean isPermanentFault() {
        return permanentFault;
    }

    /**
     * Getter method for time field for the command object's creation.
     * @return time field
     */
    public int getTime() {
        return time;
    }

    /**
     * Getter method for floor field
     * @return floor field
     */
    public int getFloor() {
        return floor;
    }

    /**
     * Getter method for directionButton (can either be up or down)
     * @return directiommButton field
     */
    public Direction getDirectionButton(){
       return directionButton;
    }

    /**
     * Getter method for elevatorbutton (can be any value in the range <= floor number)
     * @return elevatorButton field
     */
    public int getElevatorButton() {
        return elevatorButton;
    }

    /**
     * Returns the string representation of all components of the command
     * @return String representation of the command's components
     */
    @Override
    public String toString() {
      String string = "Command Composed Of:\nTime: " + getTime()
                + "\nFloor: " + getFloor() + "\nElevator Button Pressed: " +
                getElevatorButton() + "\nDirection: " + getDirectionButton() + "\n";
        return string;
    }

    /**
     * Compares this Command object with the specified object for equality. Returns true if and only if
     * the given object is also a Command object and both objects have the same values for time, floor,
     * elevatorButton, and directionButton.
     * @param other
     * @return
     */
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Command)) {
            return false;
        }
        Command otherCommand = (Command) other;
        return this.time == otherCommand.time &&
                this.floor == otherCommand.floor &&
                this.directionButton == otherCommand.directionButton &&
                this.elevatorButton == otherCommand.elevatorButton;
    }
}