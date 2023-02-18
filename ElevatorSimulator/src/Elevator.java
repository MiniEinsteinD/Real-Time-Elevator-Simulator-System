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

    private List<Command> commands;

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
        commands = new ArrayList<>():
    }

    /**
     * Continuosly gets commands from the scheduler until
     * there are no more commands and the floor is done
     * reading the file
     */
    //@Override
    public void run() {

        while (!scheduler.shouldExit()) {
            //Command command = scheduler.getCommand();
            //commands.add(command);

            //Checks whether the elevator should go up or down
            if (!state.getIdleStatus() && state.getFloorLevel() > commands.get(0).getFloor()) {
                state.setDirection(Direction.DOWN);
            }
            else{
                state.setDirection(Direction.UP)
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            //Checks if a command is serviced, and removes it if it is serviced
            for (Command command: commands) {
                if (state.getFloorLevel() == command.getFloor()) {
                    removeCommand(command);
                }
            }

            moveFloor(); //Moves floor based on idle status and direction


            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
            }


        }
    }

    /**
     * Getter method for the state of elevator
     * @return the ElevatorState of the elevator
     */
    public ElevatorState getState() {
        return state;
    }

    /**
     * Adds a command to the commands list
     * @param command the command that will be added to commands
     */
    public void putCommand(Command command){
        commands.add(command);
        System.out.println("Elevator received Command:\n" + command + "\n");
        state.setIdleStatus(false);
    }

    /**
     * Removes a command from the commands list
     * @param command the command that will be removed from commands
     */
    private void removeCommand(Command command){
        System.out.println("Elevator finished Command:\n" + command + "\n");
        scheduler.placeServicedCommand(command);
        commands.remove(command);
        if (commands.isEmpty()) {
            state.setIdleStatus(true);
        }
    }

    /**
     * Moves the elevator up or down based on the direction and idle
     * status of the elevator
     */
    private void moveFloor(){
        // return if the elevator is idle.
        if (state.getIdleStatus())
            return;
        if (state.getDirection() == Direction.UP) {
            state.goUp(); //Increments the current floor level of the elevator by 1.
        } else {
            state.goDown(); //Decrements the current floor level of the elevator by 1.
        }
        System.out.println("Elevator is now on floor: " + state.getFloorLevel()));
    }

}