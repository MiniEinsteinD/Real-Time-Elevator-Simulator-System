/**
 * The components are all assembled into 1 object that can be broken down to obtain all attributes.
 * Will be used in the elevator class to enqueue a list of commands for the
 * elevator (will support multiple elevators in future iterations)
 * @author Hasan Al-Hasoo
 * @author Edited By Mohammed
 * @version 1.0
 */
public class Command {

    private int time; //Integer representation of the time
    private int floor; //The floor number we wish to travel to
    private int elevatorButton; //The button selected in the elevator car
    private Direction directionButton; //Direction enum (can be up or down)

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
}