import java.io.*;
import java.util.ArrayList;
import java.net.*;

/**
 * The Floor class represents the floor subsystem
 * 
 * @author Daniah Mohammed
 * @version 01
 */

 public class Floor {
    private File file; // File stores the commands that needs to be processed
    private ArrayList<Command> commandList; //List of all commands found in the text file
    private DatagramSocket sendReceiveSocket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;

    /**
     * Constructs a floor using the scheduler and a file
     * @param file File type that stores the commands that needs to be processed
     */
    public Floor(File file){
        this.file =  file;
        commandList = new ArrayList<Command>();
        // Networking
        try {
            sendReceiveSocket = new DatagramSocket();
            sendPacket = null;
            receivePacket = null;
        } catch (SocketException se) {
            sendReceiveSocket.close();
            throw new RuntimeException(se);
        }
    }

    /**
     * Populate an ArrayList with the strings of commands from file
     * Continuously sends commands and receives responses from the scheduler until
     * there are no more commands
     */
    public void startSubsystem() {

        // read lines from file and store them as strings in an ArrayList
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new FileReader(file.getName()));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

        //creat an array list for the lines to be read from file
        ArrayList<String> listOfLines = new ArrayList<String>();

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
                try {
                    bufReader.close();
                } catch (IOException ioe) {}
                throw new RuntimeException(e);
            }
        }

        try {
            bufReader.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        //create a command from each line read from the text file
        for(String command: listOfLines){
            commandList.add(new Command(command));
        }
        //send all the commands to the scheduler
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        try {
            ObjectOutputStream oos = new ObjectOutputStream(bos);
            oos.writeObject(commandList);
            oos.flush();
            byte[] data = bos.toByteArray();

            // Send data to SchedulerReciever
            sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 23);
            System.out.println("Floor: Sending commands to Scheduler subsystem.");
            sendReceiveSocket.send(sendPacket);

            // Recieve confirmation from SchedulerReciever.
            byte[] response = new byte[100];
            receivePacket = new DatagramPacket(response, response.length);
            System.out.println("Floor: Receiving confirmation of completion from the Scheduler subsystem.");
            sendReceiveSocket.receive(receivePacket);

            // In the future we'll handle commands arriving at different times here.

        } catch (IOException ioe) {} finally {
            // Cleanup
            try {
                bos.close();
            } catch (IOException ioe) {}
        }

        // Let the scheduler know we're done
        try {
            byte[] data = new byte[0];
            // Send data to SchedulerReciever
            sendPacket = new DatagramPacket(data, data.length, InetAddress.getLocalHost(), 23);
            System.out.println("Floor: Sending commands to Scheduler subsystem.");
            sendReceiveSocket.send(sendPacket);

            // Recieve confirmation from SchedulerReciever.
            byte[] response = new byte[100];
            receivePacket = new DatagramPacket(response, response.length);
            System.out.println("Floor: Receiving confirmation of completion from the Scheduler subsystem.");
            sendReceiveSocket.receive(receivePacket);
        } catch (IOException ioe) {} finally {
            // Cleanup
            sendReceiveSocket.close();
        }
    }

    public static void main(String args[]) {
        Floor f = new Floor(new File ("commandFile.txt"));
        f.startSubsystem();
    }
}