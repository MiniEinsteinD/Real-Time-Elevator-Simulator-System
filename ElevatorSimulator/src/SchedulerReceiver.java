import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.ArrayList;

/**
 * @author Hasan Al-Hasoo
 * @version 1.0
 * The job of this thread is to act as an intermediate host. The thread will receive an array of bytes from the floor
 * class. Then, the byte[] array will be converted into an Arraylist of commands and sent to SchedulerTransmiter class
 */

public class SchedulerReceiver implements Runnable {

    private DatagramPacket receivePacket;

    private DatagramSocket sendReceiveSocket;

    private SchedulerTransmitter transmiter;

    /**
     * Constructor for SchedulerReceiver class. Instantiates DatagramSocket and opens port 23
     */
    public SchedulerReceiver(SchedulerTransmitter transmiter, DatagramSocket sendReceiveSocket){
        this.sendReceiveSocket = sendReceiveSocket;
        this.transmiter = transmiter;
    }


    /**
     * @author Hasan Al-Hasoo
     * The job of this thread is to listen for commands, deserialize the array of bytes received, and
     * send it to the SchedulerTransmitter
     */
    @Override
    public void run() {

        byte data[] = new byte[100];

        receivePacket = new DatagramPacket(data, data.length); //packet passed byte array and its length (100)

        boolean exitStatus = false; //should the thread exit

        while (!exitStatus){
            try{
                try {
                    sendReceiveSocket.receive(receivePacket);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

                if (receivePacket.getLength() == 0){
                    exitStatus = true;
                }
                else{
                    ArrayList<Command> commands = deserialize(receivePacket.getData()); //grabs byte[] array and creates an ArrayList of commands
                    transmiter.placeCommandList(commands); //place the commands from the list onto the transmitter class
                }
            } catch (RuntimeException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @author Mohammed Abu Alkhair
     * EDITED BY: HASAN Al-HASOO
     * Deserializes a byte array into an ArrayList of command objects.
     * @param serializedMessage a byte array representing the serialized Command object
     * @return the deserialized ArrayList of commands, or null if an error occurs
     */
    public static ArrayList<Command> deserialize(byte[] serializedMessage) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(serializedMessage);
            ObjectInputStream objIn = new ObjectInputStream(in);
            return (ArrayList<Command>) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


}
