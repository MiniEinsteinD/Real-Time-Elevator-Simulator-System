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
    private Floor floor;


    /**
     * Constructor for Scheduler object that initializes the list of commands which stores all commands
     * in the system
     */
    public Scheduler (){

        this.commands = new ArrayList<>();
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
        System.out.println("Scheduler has received the following command:\n " + command);
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
        System.out.println("Scheduler has passed the following command to the elevator :\n "
                + command);
        notifyAll();
        floor.orderCompleted(command);
        return command;
    }

    /**
     * To be completed at a different iteration
     */
    @Override
    public void run() {

    }

    /**
     * Method used to add the floor subsystem to the scheduler
     * @param floor to be added to the scheduler
     */
    public void setFloor(Floor floor) {
        this.floor = floor;
    }
}