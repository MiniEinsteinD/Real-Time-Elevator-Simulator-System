import java.util.ArrayList;
import java.util.List;

/**
 * Scheduler class is a thread that stores the list of commands and acts like a queue
 * for all commands in the system
 * @author Hasan Al-Hasoo, 101196381
 * @author Mohammed Abu Alkhair, 101204950
 * @version 1.0
 */
public class Scheduler implements Runnable {

    private List<Command> commands; //The list storing all commands in the system
    private List<Command> servicedCommands; //The list of commands already serviced by the elevators (deprecated)
    private boolean exitStatus;
    private Elevator elevator;



    /**
     * Constructor for Scheduler object that initializes the list of commands which stores all commands
     * in the system
     */
    public Scheduler (){

        this.commands = new ArrayList<Command>();
        this.servicedCommands = new ArrayList<Command>();
        this.exitStatus = false;
    }

    /**
     * Method used to add commands to the ArrayList of commands
     * @param command
     */
    public synchronized void placeCommand(Command command){
        while (!commands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        System.out.println("Scheduler has received the following command from the floor subsystem:\n" + command);
        commands.add(command);
        notifyAll();
    }

    /**
     * Method used to obtain the next command for servicing from the ArrayList of commands
     * @return The next command which will be serviced (index 0)
     */
    public synchronized Command getCommand() {
        while (commands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        Command command = commands.remove(0);
        System.out.println("Scheduler has passed the following command to the elevator :\n"
                + command);
        notifyAll();
        return command;
    }

    
    /**
     * Method that runs when the Scheduler thread is started.
     *
     * Waits until the Elevator thread is idle, then assigns
     * it the correct command based on its state.
     *
     * Returns when there are no commands and it was signalled
     * to exit by the Floor.
     *
     * @author Ethan Leir 101146422
     * @version 1.0
     */
    //@Override
    public void run() {
    	boolean running = true;
    	while (running) {
    		while (!elevator.getState().isIdleStatus()) {
    			// Wait for it to be idle.
    			try {
					Thread.sleep(50); // Could be a wait so that when the elevator notifies the scheduler we're not doing nothing.
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    		
    		elevator.putCommand(findBestCommand(elevator.getState())); // Add command to the Elevator's list of commands then set idle status and notifyall()
    		
    		// Might need to adjust the behaviour so that the Elevators can exit.
    		if (shouldExit()) {
    			running = false;
    		}
    	}
    }

    /**
     * Method used to add commands already serviced by elevator
     * to the ArrayList of commands
     * @param command
     * deprecated
     */
    public synchronized void placeServicedCommand(Command command){
        while (!servicedCommands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        System.out.println("Elevator has finished servicing the following command:\n" + command);
        servicedCommands.add(command);
        notifyAll();
    }

    /**
     * Method used to obtain the commands already serviced by the elevator
     * @return The next command which will be serviced (index 0)
     * deprecated
     */
    public synchronized Command getServicedCommand() {
        while (servicedCommands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        Command command = servicedCommands.remove(0);
        System.out.println("Floor subsystem has been notified that the following command has been serviced:\n"
                + command);
        notifyAll();
        return command;
    }

    /**
     * exitThreads signals that it is time to end the threads in this program
     */
    public void exitThreads()
    {
        exitStatus = true;
    }

    /**
     * shouldExit returns whether it is time to end the threads or not
     * @return true if it time to end the threads, false otherwise.
     */
    public synchronized boolean shouldExit()
    {
        return exitStatus && commands.isEmpty();
    }

    /**
     * Sets the elevator that is connected to the Scheduler
     * @param elevator The elevator connected to the scheduler
     */
    public synchronized void setElevator(Elevator elevator) {
        this.elevator = elevator;
    }
    
    
    /**
     * Method used to add a list of commands that need to be
     * serviced
     * @param commandList arrayList of commands to be serviced
     */
    public synchronized void placeCommandList(ArrayList<Command> commandList) {
        while (!commands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        System.out.println("Scheduler has received the command list from the floor subsystem:\n");
        commands = new ArrayList<>(commandList);
        notifyAll();
    }
    
    /**
     * Method that finds the command whose floor level is closest to the state parameter passed in. The directions of
     * the found command and the state must both match. If so, return the closest command so the elevator can service it.
     * Otherwise, if no suitable command is found, return null
     * @param state is the current state of the elevator
     * @return the command whose closest in floor level and pertains to the same direction as state
     * @author Hasan Al-Hasoo
     * @version 1.1
     */
    private synchronized Command findBestCommand(ElevatorState state) {

        while (commands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Command closest = null;

        ArrayList<Command> upCommands = new ArrayList<>();

        ArrayList<Command> downCommands = new ArrayList<>();

        for (int i = 0; i < commands.size(); i++) {

            if (commands.get(i).getDirectionButton() == Direction.UP) {
                upCommands.add(commands.get(i));
            }
            if (commands.get(i).getDirectionButton() == Direction.DOWN) {
                downCommands.add(commands.get(i));
            }
        }

        if (upCommands.isEmpty()) {
            state.setDirection(Direction.DOWN);
        }

        if (downCommands.isEmpty()) {
            state.setDirection(Direction.UP);
        }

        if (state.getDirection() == Direction.UP) {
            closest = upCommands.get(0);
            for (Command upCommand : upCommands) {
                if (Math.abs(upCommand.getFloor() - state.getFloorLevel()) < Math.abs(closest.getFloor() - state.getFloorLevel())) {
                    closest = upCommand;
                }
            }
            removeCommand(closest);
            return closest;
        }


        if (state.getDirection() == Direction.DOWN) {
            closest = downCommands.get(0);
            for (Command downCommand : downCommands) {
                if (Math.abs(downCommand.getFloor() - state.getFloorLevel()) < Math.abs(closest.getFloor() - state.getFloorLevel())) {
                    closest = downCommand;
                }
            }
            removeCommand(closest);
            return closest;
        }

        return null;
    }

    /**
     * Searches for the target command passed in as the parameter, if detected remove this element and return void
     * @param command target command
     */
    private void removeCommand(Command command){
        for (int i = 0; i < commands.size(); i++){
            if(commands.get(i) == command){
                commands.remove(i);
            }
        }
    }
}
