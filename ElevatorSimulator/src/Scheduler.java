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
    private List<Command> servicedCommands; //The list of commands already serviced by the elevators
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
     * To be completed at a different iteration
     */
    //@Override
    public void run() {

    }

    /**
     * Method used to add commands already serviced by elevator
     * to the ArrayList of commands
     * @param command
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
    public boolean shouldExit()
    {
        return exitStatus;
    }

    /**
     * Sets the elevator that is connected to the Scheduler
     * @param elevator The elevator connected to the scheduler
     */
    public void setElevator(Elevator elevator) {
        this.elevator = elevator;
    }
}