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

    /**
     * Constructs and elevator using a scheduler and id
     *
     * @param scheduler the shared scheduler between the elevator and the floor
     * @param id the id of the elevator
     */
    public Elevator(Scheduler scheduler, int id) {
        this.scheduler = scheduler;
        this.id = id;
    }

    /**
     * Continuosly gets commands from the scheduler until
     * there are no more commands and the floor is done
     * reading the file
     */
    @Override
    public void run() {
        //while true condition will be changed once we figure out how to determine end of file
        while (true) {
            Command command = scheduler.getCommand();
            System.out.println("Received Command:\n" + command + "\n");
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            System.out.println("Finished Command:\n" + command + "\n");
            /*
            I am asssuming the finishedCommand is some way to send data
            back to the scheduler, and the scheduler will send that data
            back to the floor.
            scheduler.finishedCommand(command);
             */
        }
    }
}