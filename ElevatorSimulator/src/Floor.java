import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * The Floor class represtents a floor subsystem 
 * 
 * @author Daniah Mohammed
 * @version 01
 */

 public class Floor implements Runnable{
    private File file;
    private boolean ordersFinished;
    private Scheduler scheduler;

    public Floor(Scheduler sched,File file){
        this.file =  file;
        this.ordersFinished = false;
        this.scheduler = sched;
    }
    
    @Override
    public void run() {

        // read lines and store them as strings in an ArrayList
        BufferedReader bufReader = new BufferedReader(new FileReader(file.getName()));
        ArrayList<String> listOfLines = new ArrayList<>();

        String line = bufReader.readLine();
        while (line != null) {
            listOfLines.add(line);
            line = bufReader.readLine();
        }

        bufReader.close();
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

    public enum Direction {up, down}
}