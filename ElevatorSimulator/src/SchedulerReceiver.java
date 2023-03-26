import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.logging.Logger;

/**
 * @author Hasan Al-Hasoo
 * @version 1.0
 * The job of this thread is to act as an intermediate host. The thread will receive an array of bytes from the floor
 * class. Then, the byte[] array will be converted into an Arraylist of commands and sent to SchedulerTransmitter class
 */

public class SchedulerReceiver implements Runnable {

    private DatagramPacket receivePacket;

    private DatagramPacket sendPacket;

    private DatagramSocket sendReceiveSocket;

    private SchedulerTransmitter transmitter;

    private final static String subsystemName = "SchedulerSubsystem";

    private final static Logger LOGGER = Logger.getLogger(subsystemName);// Logger for system inspection

    /**
     * Constructor for SchedulerReceiver class. Instantiates DatagramSocket and opens port 23
     */
    public SchedulerReceiver(SchedulerTransmitter transmitter, DatagramSocket sendReceiveSocket){
        this.sendReceiveSocket = sendReceiveSocket;
        this.transmitter = transmitter;
    }


    /**
     * @author Hasan Al-Hasoo
     * The job of this thread is to listen for commands, deserialize the array of bytes received, and
     * send it to the SchedulerTransmitter
     */
    //@Override
    public void run() {

        byte data[] = new byte[1000];

        byte echoArray[] = new byte[0];

        receivePacket = new DatagramPacket(data, data.length); //packet passed byte array and its length (100)


        boolean exitStatus = false; //should the thread exit

        while (!exitStatus){
            try {
                sendReceiveSocket.receive(receivePacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (receivePacket.getLength() == 0){
                exitStatus = true;
            } else {
                //grabs byte[] array and creates an ArrayList of command objects
                for (Object obj : Marshalling.deserialize(receivePacket.getData(), ArrayList.class)) {
                    if (obj instanceof Command) {
                        //place the commands from the list onto the transmitter class
                        transmitter.placeCommand((Command) obj);

                        LOGGER.info("Scheduler Receiver has received the following command: " + (Command) obj);
                    } else {
                        throw new RuntimeException("Unexpected object type "
                               + "obtained from Floor subsystem.");
                    }
                }
            }

            sendPacket = new DatagramPacket(echoArray, echoArray.length, receivePacket.getAddress(), receivePacket.getPort());

            try {
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        transmitter.exitThreads();

        sendReceiveSocket.close();
    }


    public static void main(String args[]) {
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

        try {
            SubsystemLogger.setup(subsystemName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        transmitter = new SchedulerTransmitter();
        receiver = new SchedulerReceiver(transmitter, sendReceiveSocket);

        Thread transmitterThread = new Thread(transmitter, "transmitter");
        Thread receiverThread = new Thread(receiver, "receiver");

        transmitterThread.start();
        receiverThread.start();
    }

}
