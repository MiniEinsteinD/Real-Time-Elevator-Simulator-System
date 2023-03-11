import java.io.File;
import java.net.DatagramSocket;
import java.net.SocketException;

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
        SchedulerReceiver receiver;
        SchedulerTransmitter transmitter;
        DatagramSocket sendReceiveSocket = null;

        try {
            // Construct a datagram socket and bind it to port 69
            // on the local host machine. This socket will be used to
            // receive UDP Datagram packets from elevators and to send
            // packets back to elevators.
            sendReceiveSocket = new DatagramSocket(23);

        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }

        file = new File("commandFile.txt");
        elevator = new Elevator( 42);
        transmitter = new SchedulerTransmitter();
        receiver = new SchedulerReceiver(transmitter, sendReceiveSocket);
        floor = new Floor(file);
        
        scheduler.setElevator(elevator);

        Thread schedulerThread = new Thread(scheduler, "Scheduler");
        Thread elevatorThread = new Thread(elevator, "Elevator");


        schedulerThread.start();
        elevatorThread.start();
        floor.startSubsystem();
    }
}