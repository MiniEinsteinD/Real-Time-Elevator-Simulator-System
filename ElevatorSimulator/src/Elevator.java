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
            Command command = scheduler.getCommand();
            commands.add(command);
            System.out.println("Elevator received Command:\n" + command + "\n");

            //Checks whether the elevator should go up or down
            if (state.getFloorLevel() > command.getFloor()) {
                state.setDirection(Direction.DOWN);
            else {
                state.setDirection(Direction.UP)
            }

            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            //Checks if a command is serviced
            for (int i = 0; i < commands.size(); i++) {
                if (state.getFloorLever() == commands.get(i).getFloor()) {
                    scheduler.placeServicedCommand(commands.get(i));
                    System.out.println("Elevator finished Command:\n" + commands.get(i) + "\n");
                    commands.remove(i);
                }
            }

            // Elevator goes up or down based on the direction of the elevator.
            if (state.getDirection() == Direction.UP && state.getIdleStatus() == false) {
                state.goUp(); //Increments the current floor level of the elevator by 1.
                state.setIdleStatus(false);
            }
            else {
                state.goDown(); //Decrements the current floor level of the elevator by 1.
            }

            // Sets idle status to true if there are no requests
            if (commands.size() == 0)
                state.setIdleStatus(true);

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
}