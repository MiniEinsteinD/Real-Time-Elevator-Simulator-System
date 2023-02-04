/**
 * The Simulator class starts up the elevator simulation.
 * Contains the main method.
 *
 * @author Ethan Leir 101146422
 * @version 1.0
 */
public class Simulator{
    /**
     * The main process which starts the elevator simulation.
     * @param args String, arguments to main.
     */
    public static void main(String args[]){
        Scheduler scheduler = new Scheduler();
        Elevator elevator;
        Floor floor;
        File file;

        file = new File("../input/commandFile");
        elevator = new Elevator(scheduler, 42);
        floor = new Floor(scheduler, file);

        schedulerThread = new Thread(scheduler, "Scheduler");
        elevatorThread = new Thread(elevator, "Elevator");
        floorThread = new Thread(floor, "Floor");

        schedulerThread.start();
        elevatorThread.start();
        floorThread.start();
    }
}