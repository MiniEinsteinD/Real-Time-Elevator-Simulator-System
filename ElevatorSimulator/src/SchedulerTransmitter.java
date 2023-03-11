import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This scheduler class communicates with the elevator class to distribute the placed commands to
 * the elevators
 *
 * @author Mohammed Abu Alkhair
 * @version 1.0
 */
public class SchedulerTransmitter implements Runnable {

    private List<Command> commands;
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
        commands = Collections.synchronizedList(new ArrayList<Command>());

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
        portList = new ArrayList<>();
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
        byte sendData[] = new byte[100];

        //Create Receive packet from elevator
        byte receiveData[] = new byte[100];
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
            sendData = serialize(command);
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
        byte exit[] = new byte[0];

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
            portList.remove(receivePacket.getPort());

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

    }

    /**
     * Method used to add a list of commands that need to be
     * serviced
     * @param commandList arrayList of commands to be serviced
     */
    public void placeCommandList(ArrayList<Command> commandList) {

        commands.addAll(commandList);
        System.out.println("Scheduler has added the command list from the floor subsystem\n");
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
    private void removeCommand(Command command){
        for (int i = 0; i < commands.size(); i++){
            if(commands.get(i) == command){
                commands.remove(i);
            }
        }
    }

    /**
     * Method that finds the command whose floor level is closest to the state parameter passed in. The directions of
     * the found command and the state must both match. If so, return the closest command so the elevator can service it.
     * Otherwise, if no suitable command is found, return null
     * @param state is the current state of the elevator
     * @return the command whose closest in floor level and pertains to the same direction as state
     * @author Hasan Al-Hasoo
     * @version 1.1
     */
    private synchronized Command findBestCommand(ElevatorState state) {

        while (commands.isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        Command closest = null;

        ArrayList<Command> upCommands = new ArrayList<>();

        ArrayList<Command> downCommands = new ArrayList<>();

        for (int i = 0; i < commands.size(); i++) {

            if (commands.get(i).getDirectionButton() == Direction.UP) {
                upCommands.add(commands.get(i));
            }
            if (commands.get(i).getDirectionButton() == Direction.DOWN) {
                downCommands.add(commands.get(i));
            }
        }

        if (upCommands.isEmpty()) {
            state.setDirection(Direction.DOWN);
        }

        if (downCommands.isEmpty()) {
            state.setDirection(Direction.UP);
        }

        if (state.getDirection() == Direction.UP) {
            closest = upCommands.get(0);
            for (Command upCommand : upCommands) {
                if (Math.abs(upCommand.getFloor() - state.getFloorLevel()) < Math.abs(closest.getFloor() - state.getFloorLevel())) {
                    closest = upCommand;
                }
            }
            removeCommand(closest);
            return closest;
        }


        if (state.getDirection() == Direction.DOWN) {
            closest = downCommands.get(0);
            for (Command downCommand : downCommands) {
                if (Math.abs(downCommand.getFloor() - state.getFloorLevel()) < Math.abs(closest.getFloor() - state.getFloorLevel())) {
                    closest = downCommand;
                }
            }
            removeCommand(closest);
            return closest;
        }

        return null;
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
        } catch (IOException | ClassNotFoundException e) {
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
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
}
