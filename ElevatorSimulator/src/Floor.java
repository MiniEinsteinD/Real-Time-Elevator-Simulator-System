import java.io.*;
import java.util.ArrayList;
import java.net.*;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The Floor class represents the floor subsystem
 *
 * @author Daniah Mohammed, Ali El-Khatib
 * @version 02
 */

 public class Floor {
    private File file; // File stores the commands that needs to be processed
    private ArrayList<Command> commandList; //List of all commands found in the text file
    private DatagramSocket sendReceiveSocket;
    private DatagramPacket sendPacket;
    private DatagramPacket receivePacket;
    private InetAddress SchedulerAddress;

    private final static String subsystemName = "FloorSubsystem";

    private final static Logger LOGGER = Logger.getLogger(subsystemName);// Logger for system inspection

    /**
     * Constructs a floor using a file, a socket. The address of the local
     * host is the same as the address of the Scheduler
     * @param file File type that stores the commands that needs to be processed
     */
    public Floor(File file, DatagramSocket sendReceiveSocket){
        this.file =  file;
        commandList = new ArrayList<Command>();
        this.sendReceiveSocket = sendReceiveSocket;
		sendPacket = null;
		receivePacket = null;

        //get name of the local host
        try {
            SchedulerAddress = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            LOGGER.log(Level.SEVERE, "Error getting local host address", e);
            throw new RuntimeException(e);
        }
    }

    /**
     * Constructs a floor using a file, a socket, and the Scheduler address that is
     * passed as a parameter.
     * @param file File type that stores the commands that needs to be processed
     */
    public Floor(File file, DatagramSocket sendReceiveSocket, InetAddress SchedulerAddress){
        this.file =  file;
        commandList = new ArrayList<Command>();
        this.sendReceiveSocket = sendReceiveSocket;
        sendPacket = null;
        receivePacket = null;

        //the name of the scheduler is passes as an argument
        this.SchedulerAddress = SchedulerAddress;
    }

    /**
     * Populate an ArrayList with the strings of commands from file
     * Continuously sends commands and receives responses from the scheduler until
     * there are no more commands
     */
    public void startSubsystem() throws InterruptedException {

        // read lines from file and store them as strings in an ArrayList
        BufferedReader bufReader = null;
        try {
            bufReader = new BufferedReader(new FileReader(file.getName()));
        } catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Error reading file", e);
            throw new RuntimeException(e);
        }

        //creat an array list for the lines to be read from file
        ArrayList<String> listOfLines = new ArrayList<String>();

        String line = null;
        try {
            line = bufReader.readLine();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error reading file", e);
            throw new RuntimeException(e);
        }

        while (line != null) {
            listOfLines.add(line);
            try {
                line = bufReader.readLine();
            } catch (IOException e) {
                LOGGER.log(Level.SEVERE, "Error reading file", e);
                try {
                    bufReader.close();
                } catch (IOException ioe) {
                    LOGGER.log(Level.SEVERE, "Error closing file", ioe);
                }
                LOGGER.log(Level.SEVERE, "Error closing file", e);
                throw new RuntimeException(e);
            }
        }

        try {
            bufReader.close();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error closing file", e);
            throw new RuntimeException(e);
        }

        //create a command from each line read from the text file
        for(String command: listOfLines){
            commandList.add(new Command(command));
        }
        commandList.sort(new Comparator<Command>() {
            /**
             * Sorts starting from The Earliest time
             * @param o1 the first object to be compared.
             * @param o2 the second object to be compared.
             * @return
             */
            @Override
            public int compare(Command o1, Command o2) {
                Integer i1 = o1.getTime();
                Integer i2 = o2.getTime();
                return i1.compareTo(i2);
            }
        });

        int counter = 0;

        while (!commandList.isEmpty()) {
            // Wait until the time specified by the next command
            Thread.sleep((commandList.get(0).getTime() - counter) * 1000);
            counter = commandList.get(0).getTime();

            // Add all commands with the current time to the list to send
            ArrayList<Command> tempList = new ArrayList<>();
            for (Command c: commandList) {
                if (c.getTime() <= counter) {
                    LOGGER.info("Input to an XML file: " + c.toString());
                    tempList.add(c);
                }
            }
            // Remove all commands to be sent from teh original list
            for (Command c: tempList) {
                commandList.remove(c);
            }

            //send all the commands to the scheduler
            try {
                byte[] data = Marshalling.serialize(tempList);

                for (Command c: tempList) {
                    System.out.println(c +" \n\n\n");
                }
                // Send data to SchedulerReceiver
                sendPacket = new DatagramPacket(data, data.length, SchedulerAddress, 23);
                LOGGER.info("Floor: Sending commands to Scheduler subsystem.");
                System.out.println("Floor: Sending commands to Scheduler subsystem.");
                sendReceiveSocket.send(sendPacket);

                //Add the sendPacket to the logger file
                LOGGER.info("Floor system sent a CommandList");

                // Recieve confirmation from SchedulerReciever.
                byte[] response = new byte[100];
                receivePacket = new DatagramPacket(response, response.length);
                LOGGER.info("Floor: Receiving confirmation of completion from the Scheduler subsystem.");
                System.out.println("Floor: Receiving confirmation of completion from the Scheduler subsystem.");
                sendReceiveSocket.receive(receivePacket);

            } catch (IOException ioe) {
                LOGGER.log(Level.SEVERE, "Error serializing file", ioe);
            }
        }


        // Let the scheduler know we're done
        try {
            byte[] data = new byte[0];
            // Send data to SchedulerReceiver
            sendPacket = new DatagramPacket(data, data.length, SchedulerAddress, 23);
            LOGGER.info("Floor: Sending commands to Scheduler subsystem.");
            System.out.println("Floor: Sending commands to Scheduler subsystem.");
            sendReceiveSocket.send(sendPacket);


            //Add to the logger file that the elevator is quiting
            LOGGER.info("Floor Subsystem is quiting");

            // Recieve confirmation from SchedulerReciever.

            byte[] response = new byte[100];
            receivePacket = new DatagramPacket(response, response.length);
            LOGGER.info("Floor: Receiving confirmation of completion from the Scheduler subsystem.");
            System.out.println("Floor: Receiving confirmation of completion from the Scheduler subsystem.");
            sendReceiveSocket.receive(receivePacket);
        } catch (IOException ioe) {
            LOGGER.log(Level.SEVERE, "Error closing file", ioe);
        } finally {
            // Cleanup
            sendReceiveSocket.close();
        }
    }

    public static void main(String args[]) throws InterruptedException {
        Floor f;
        InetAddress name;
        if (args.length != 1) {
            try {
                //get the name of the machine
                name = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        } else {
            try {
                name = InetAddress.getByName(args[0]);
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }

        try {
            SubsystemLogger.setup(subsystemName);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (args.length < 2) {
            try {
                f = new Floor(new File("commandFile.txt"), new DatagramSocket(), name);
                f.startSubsystem();
            } catch (SocketException | InterruptedException e) {
                e.printStackTrace();
            }
        }
        else {
            try {
                f = new Floor(new File("InputFileForTesting.txt"), new DatagramSocket(), name);
                f.startSubsystem();
            } catch (SocketException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
