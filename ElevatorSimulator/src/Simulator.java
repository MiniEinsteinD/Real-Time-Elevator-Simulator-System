import java.io.File;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

/**
 * The Simulator class is responsible for creating the elevator system and running it.
 */
public class Simulator {

    /**
     * Creates the elevator system and starts its threads.
     * @throws SocketException if there is an error with creating a socket
     * @throws UnknownHostException if there is an error with the host name
     * @throws InterruptedException if there is an error with thread interruption
     */
    public Simulator() throws SocketException, UnknownHostException, InterruptedException {
        //start the pop-up
        ElevatorSystemPopup popup = new ElevatorSystemPopup();

        //get the info from the pop-up
        int numElevators = popup.getNumElevators();
        int numFloors = popup.getNumFloors();
        String hostName = popup.getHostName();
        boolean useMultipleComputers = popup.isMultipleComputers();
        ElevatorSystemPopup.SystemType subsystem = popup.getSystemType();

        // create threads
        if(useMultipleComputers) { //multiple computers
            switch(subsystem){
                case SchedulerSubsystem:
                    // create transmitter and receiver
                    SchedulerTransmitter transmitter = new SchedulerTransmitter();
                    SchedulerReceiver receiver = new SchedulerReceiver(transmitter, new DatagramSocket(23));

                    // create threads
                    Thread transmitterThread = new Thread(transmitter, "transmitter");
                    Thread receiverThread = new Thread(receiver, "receiver");

                    // start threads
                    transmitterThread.start();
                    receiverThread.start();
                    break;

                case FloorSubsystem:
                    Floor floor = new Floor(new File("commandFile.txt"), new DatagramSocket(20),
                            InetAddress.getByName(hostName));
                    floor.startSubsystem();
                    break;

                case ElevatorSubsystem:
                    makeElevators(numElevators, numFloors, hostName);
                    break;
            }

        }
        else { //single computer
            // create transmitter and receiver
            SchedulerTransmitter transmitter = new SchedulerTransmitter();
            SchedulerReceiver receiver = new SchedulerReceiver(transmitter, new DatagramSocket(23));

            // create threads
            Thread transmitterThread = new Thread(transmitter, "transmitter");
            Thread receiverThread = new Thread(receiver, "receiver");

            // start threads
            transmitterThread.start();
            receiverThread.start();

            //create floor subsystem
            Floor floor = new Floor(new File("InputFileForTesting.txt"), new DatagramSocket(20),
                    InetAddress.getLocalHost());
            floor.startSubsystem();

            //create Elevators
            makeElevators(numElevators, numFloors, null);
        }


    }

    /**
     * Creates the Elevator threads.
     * @param numElevators the number of elevators to create
     * @param numFloors the number of floors in the building
     * @param hostName the host name to connect to
     * @throws UnknownHostException if there is an error with the host name
     */
    private void makeElevators(int numElevators, int numFloors, String hostName) throws UnknownHostException {

        ElevatorFrame elevatorFrame = new ElevatorFrame(numElevators, numFloors);
        for(int i = 1; i <= numElevators; i++) {
            Elevator elevator;
            if(hostName == null) {
                elevator = new Elevator(i, InetAddress.getLocalHost(), elevatorFrame);
            }
            else {
                elevator = new Elevator(i, InetAddress.getByName(hostName), elevatorFrame);
            }
            Thread elevatorThread = new Thread(elevator, "Elevator " + i);
            elevatorThread.start();
        }
    }

    public static void main(String[] args) throws SocketException, UnknownHostException, InterruptedException {
        Simulator s = new Simulator();

    }
}
