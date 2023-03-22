import java.io.*;
import java.net.*;
import java.util.*;

/**
 * This scheduler class communicates with the elevator class to distribute the placed commands to
 * the elevators
 *
 * @author Mohammed Abu Alkhair
 * @version 1.0
 */
public class SchedulerTransmitter implements Runnable {

    private static List<Command> commands;
    private boolean exitStatus;
    private DatagramSocket sendReceiveSocket;
    private DatagramPacket sendPacket, receivePacket;
    private InetAddress name;
    private List<Integer> portList;


    /**
     * Constructor for class SchedulerTransmitter
     */
    public SchedulerTransmitter()
    {
        //Initialize the synchronized arraylist
        commands = new ArrayList<Command>();

        try {
            // Construct a datagram socket and bind it to port 69
            // on the local host machine. This socket will be used to
            // receive UDP Datagram packets from elevators and to send
            // packets back to elevators.
            sendReceiveSocket = new DatagramSocket(69);

        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }

        try {
            //get the name of the machine
            name = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        //set the exit status to false, to stop the thread from exiting early
        exitStatus = false;

        //Initialize the IDList
        portList = new ArrayList<Integer>();
    }

    /**
     * This method is invoked when the thread is started
     *
     * Receives a request from Elevators and send back the best
     * command for the elevator to service.
     */
    public void run()
    {
        //create the sendData byte array
        byte sendData[] = new byte[1000];

        //Create Receive packet from elevator
        byte receiveData[] = new byte[1000];
        receivePacket = new DatagramPacket(receiveData, receiveData.length);

        //create the ElevatorState object to store the received state from the elevators
        ElevatorState state;

        //Create the command object to send back to the elevators
        Command command;

        //reply to all elevators until no commands are left to service and
        //the floor subsystem signals that there are no more commands
        //to be added
        while(!shouldExit())
        {

            // wait to receive the packet
            try {
                // Block until a datagram is received via sendReceiveSocket.
                sendReceiveSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            //get elevator state from the packet received
            state = deserializeState(receivePacket.getData());

            //add the port of the elevator to the portList if it does not there already
            Integer integer = receivePacket.getPort();
            if (!portList.contains(integer)) {
                portList.add(integer);
            }

            //get best command to send
            command = findBestCommand(state);

            //create send packet
            sendData = createCommandByteArray(command);
            sendPacket = new DatagramPacket(sendData, sendData.length,
                    receivePacket.getAddress(), receivePacket.getPort());

            //send the packet
            try {
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        //Exit message to send to elevators
        byte exit[] = new byte[1];
        exit[0] = 0;

        //notify all elevators that it is time to exit
        while(!portList.isEmpty()) {

            // wait to receive the packet
            try {
                // Block until a datagram is received via sendReceiveSocket.
                sendReceiveSocket.receive(receivePacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }

            //remove the port of the elevator from the portList
            portList.remove((Integer) receivePacket.getPort());

            //create the sendPacket
            sendPacket = new DatagramPacket(exit, exit.length,
                    receivePacket.getAddress(), receivePacket.getPort());

            //send the packet
            try {
                sendReceiveSocket.send(sendPacket);
            } catch (IOException e) {
                e.printStackTrace();
                System.exit(1);
            }
        }

        sendReceiveSocket.close();

    }

    /**
     * Method used to add a list of commands that need to be
     * serviced
     * @param commandList arrayList of commands to be serviced
     */
    public synchronized void placeCommandList(ArrayList<Command> commandList) {
        while (!commands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        System.out.println("Scheduler has received the command list from the floor subsystem:\n");
        commands = new ArrayList<Command>(commandList);
        notifyAll();
    }

    /**
     * Method used to add a command to the List of commands
     * @param command command to be added
     */
    public synchronized void placeCommand(Command command){

        commands.add(command);
        System.out.println("Scheduler has added a command from the floor subsystem");
    }

    /**
     * exitThreads signals that it is time to end the threads in this program
     */
    public void exitThreads()
    {
        exitStatus = true;
    }

    /**
     * shouldExit returns whether it is time to end the threads or not
     * @return true if it time to end the threads, false otherwise.
     */
    public synchronized boolean shouldExit()
    {
        return exitStatus && commands.isEmpty();
    }

    /**
     * Searches for the target command passed in as the parameter, if detected remove this element and return void
     * @param command target command
     * @author Ethan Leir
     */
    private synchronized void removeCommand(Command command){
        if(command == null) {
            return;
        }
        for (int i = 0; i < commands.size(); i++){
            if(commands.get(i) == command){
                commands.remove(i);
            }
        }
    }

    /**
     * Method used to obtain the next command for servicing from the ArrayList of commands
     * @return The next command which will be serviced (index 0)
     */
    public synchronized Command getCommands() {
        while (commands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        Command command = commands.remove(0);
        System.out.println("Scheduler has passed the following command to the elevator :\n"
                + command);
        notifyAll();
        return command;
    }

    /**
     * Method used to obtain the command list for servicing from the ArrayList of commands
     * @return The command list
     */
    public synchronized ArrayList<Command> getCommandList() {
        while (commands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.err.println(e);
            }
        }
        ArrayList<Command> commandlist = new ArrayList<Command>(commands);
        System.out.println("Scheduler has passed the command list to the elevator :\n");
        notifyAll();
        return commandlist;
    }

    /**
     * This Method find the best command from the list of command to service.
     * The algorithm first checks if the elevator is idele or not. If it is idle and
     * the command list is empty, null is returned. If the elevator is idle and there are commands,
     * then the closest command is serviced.
     * If the elevator is not idle, then the closest command on the same direction as the elevator
     * will be returned.
     */
    private synchronized Command findBestCommand(ElevatorState state) {
        int elevatorFloor = state.getFloorLevel(); //elevator floor level
        Direction elevatorDirection = state.getDirection(); //direction elevator is headed towards
        Command bestCommand = null; //the best command to be returned
        int minDistance = Integer.MAX_VALUE; //the minimum distance available between the elevator and a command

        //check if the elevator is idle or not
        if (state.isIdleStatus()) { // if elevator is idle
            if (commands.isEmpty()) { // if there are no commands
                return null; // return null
            } else {
                //find the closest command to service
                for (Command command : commands) {
                    int commandFloor = command.getFloor(); //the level at which the passenger is at
                    Direction commandDirection = command.getDirectionButton(); //the direction the passenger want to go toward

                    // calculate the distance between elevator's current floor and command's floor
                    int distance = Math.abs(elevatorFloor - commandFloor);

                    // check if the distance is less than the minimum distance seen so far
                    if (distance < minDistance) {
                        bestCommand = command;
                        minDistance = distance;
                    }
                }
                removeCommand(bestCommand);
                return bestCommand;
            }
        }

        // if elevator is not idle
        for (Command command : commands) {
            int commandFloor = command.getFloor();
            Direction commandDirection = command.getDirectionButton();

            // check if command is on the same path as the elevator's current direction
            boolean samePath = elevatorDirection == Direction.UP ? commandFloor >= elevatorFloor : commandFloor <= elevatorFloor;

            if (samePath) {
                // calculate the distance between elevator's current floor and command's floor
                int distance = Math.abs(elevatorFloor - commandFloor);

                // check if the distance is less than the minimum distance seen so far
                if (distance < minDistance) {
                    // if elevator is already headed in the same direction as the command, choose it
                    if (elevatorDirection == commandDirection) {
                        bestCommand = command;
                        minDistance = distance;
                    }
                }
            }
        }
        removeCommand(bestCommand);
        return bestCommand;
    }

    /**
     * Serializes a Command object into a byte array.
     * @param command the Command object to be serialized
     * @return a byte array representing the serialized Command object, or null if an error occurs
     *
     */
    public static byte[] serialize(Command command) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(command);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     Deserializes a byte array into an Command object.
     @param serializedMessage a byte array representing the serialized Command object
     @return the deserialized Command object, or null if an error occurs
     */
    public static Command deserialize(byte[] serializedMessage) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(serializedMessage);
            ObjectInputStream objIn = new ObjectInputStream(in);
            return (Command) objIn.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Serializes a ElevatorState object into a byte array.
     * @param state the ElevatorState object to be serialized
     * @return a byte array representing the serialized ElevatorState object, or null if an error occurs
     *
     */
    public static byte[] serializeState(ElevatorState state) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(state);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     Deserializes a byte array into an ElevatorState object.
     @param serializedMessage a byte array representing the serialized ElevatorState object
     @return the deserialized ElevatorState object, or null if an error occurs
     */
    public static ElevatorState deserializeState(byte[] serializedMessage) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(serializedMessage);
            ObjectInputStream objIn = new ObjectInputStream(in);
            return (ElevatorState) objIn.readObject();
        } catch (IOException e) {
        	e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Serializes a Command object and inserts the resulting bytes into a new byte array with the first byte as a 2.
     * The resulting byte array will have a length of commandBytes length + 1, with the byte 2 at index 0 and
     * the serialized command bytes starting at index 1.
     *
     * @param command the object to serialize
     * @return a new byte array containing the serialized Command object with a first byte a '2'
     */
    public static byte[] createCommandByteArray(Command command) {

        byte[] commandBytes = serialize(command);
        byte[] result = new byte[commandBytes.length + 1];
        if(command == null) {
            result[0] = 1;
        }
        else {
            result[0] = 2;
        }
        System.arraycopy(commandBytes, 0, result, 1, commandBytes.length);
        return result;
    }

    public static void main(String args[])
    {
    }


}