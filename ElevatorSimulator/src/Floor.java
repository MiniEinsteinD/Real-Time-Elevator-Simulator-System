import java.io.*;
import java.util.ArrayList;

/**
 * The Floor class represents the floor subsystem
 * 
 * @author Daniah Mohammed
 * @version 01
 */

 public class Floor implements Runnable{
    private File file; // File stores the commands that needs to be processed
    private boolean ordersFinished; // boolean expression that represent that the command finished executing
    private Scheduler scheduler; // The shared scheduler between the elevator and the floor

    /**
     * Constructs a floor using the scheduler and a file
     * @param scheduler the shared scheduler between the elevator and the floor
     * @param file File type that stores the commands that needs to be processed
     */
    public Floor(Scheduler scheduler,File file){
        this.file =  file;
        this.ordersFinished = false;
        this.scheduler = scheduler;
    }

    /**
     * Populate an ArrayList with the strings of commands from file
     * Continuously sends commands and receives responses from the scheduler until
     * there are no more commands
     */
    @Override
    public void run() {

        // read lines from file and store them as strings in an ArrayList
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new FileReader(file.getName()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        ArrayList<String> listOfLines = new ArrayList<>();

        String line = null;
        try {
            line = bufReader.readLine();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        while (line != null) {
            listOfLines.add(line);
            try {
                line = bufReader.readLine();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        try {
            bufReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for(String command: listOfLines){
            scheduler.placeCommand(new Command(command));
            //scheduler.getReply();
            //ordersFinished = true;
        }
    }

    //This function might not be needed, I might be able to include it in the run function
    public void orderCompleted(Command command){
        while (!ordersFinished) { 
            command = scheduler.getCommand();
            System.out.println("Received Command:\n" + command + "\n");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
            }
            System.out.println("Finished Command:\n" + command + "\n");
        }
    }

    //Not sure if we need this
    public enum Direction {UP, DOWN}
}