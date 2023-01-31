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

        notifyAll();
        return commands.get(0);
    }

    /**
     * To be completed
     */
    @Override
    public void run() {

    }
}