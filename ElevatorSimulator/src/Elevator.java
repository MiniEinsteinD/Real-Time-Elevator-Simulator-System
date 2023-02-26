/**
 * The Elevator class represents the subsystem of
 * elevators in the simulaton. It only is meant for
 * 1 elevator for iteration 1
 *
 * @author Ali El-Khatib
 * @version 1.0
 */
public class Elevator implements Runnable{

    private Scheduler scheduler; //Represents the shared scheduler between the elevator and the floor
    private int id; //Represents the id of the elevator

    private ElevatorState state; //state of the elevator

    private Command command;

    /**
     * Constructs and elevator using a scheduler and id
     *
     * @param scheduler the shared scheduler between the elevator and the floor
     * @param id the id of the elevator
     */
    public Elevator(Scheduler scheduler, int id) {
        this.scheduler = scheduler;
        this.id = id;
        state = new ElevatorState();
        command = null;
    }

    /**
     * Continuosly gets commands from the scheduler until
     * there are no more commands and the floor is done
     * reading the file
     */
    //@Override
    public void run() {

        while (!scheduler.shouldExit()) {
            Command command = this.getCommand();

            //Checks whether the elevator should go up or down
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            moveFloor(command); //Moves floor based on idle status and direction

        }
    }

    /**
     * Getter method for the state of elevator
     * @return the ElevatorState of the elevator
     */
    public synchronized ElevatorState getState() {
        return state;
    }

    /**
     * Puts the command into the command field. Also,
     * the method sets up direction and sets idle status to false
     * @param command the command that will be put into command
     */
    public synchronized void putCommand(Command command){
        while (!state.isIdleStatus()){
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        this.command = command;
        System.out.println("Elevator received Command:\n" + command + "\n");

        //Determine which direction to go by comparing the state and command
        if (state.getFloorLevel() > command.getFloor()) {
            state.setDirection(Direction.DOWN);
        }
        else{
            state.setDirection(Direction.UP);
        }
        state.setIdleStatus(false);
        notifyAll();
    }

    /**
     * Gets the command from the elevator
     * @return the command from the elevator
     */
    public synchronized Command getCommand(){

        while (state.isIdleStatus()){
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        Command temp = command;
        command = null;
        return temp;
    }


    /**
     * Moves the elevator up or down based on the direction and idle
     * status of the elevator
     * @param command the Command to execute
     */
    private synchronized void moveFloor(Command command){
        // return if the elevator is idle.
        if (state.isIdleStatus())
            return;
        state.setFloorLevel(command.getFloor());
        System.out.println("Elevator is now on floor: " + state.getFloorLevel() + "\n");

        //Check if elevator floor and command floor are equal
        if (state.getFloorLevel() == command.getFloor()) {
            System.out.println("Elevator finished Command:\n" + command + "\n");
            state.setIdleStatus(true); //Set idle status to true since the command is done
        }
        notifyAll();
    }

}