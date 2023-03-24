/**
 * The Elevator class represents the subsystem of
 * elevators in the simulaton. It only is meant for
 * 1 elevator for iteration 1
 *
 * @author Ali El-Khatib
 * @version 1.0
 */

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Logger;
import java.util.*;

/**
 * The Elevator class represents the subsystem of
 * elevators in the simulaton. It only is meant for
 * 1 elevator for iteration 1
 *
 * @author Ali El-Khatib
 * @version 1.0
 *
 * @author Daniah and Ali
 * @version 2.0
 */
public class Elevator implements Runnable{

    private int id; //Represents the id of the elevator

    private ElevatorState state; //state of the elevator

    private Command command;


    private DatagramPacket sendPacket, receivePacket; //Send and recieve packets to communicate with the scheduler

    private DatagramSocket sendRecieveSocket; //SendRecieve sockets to communicate with the scheduler

    private List<Integer> destinationFloors;

    private InetAddress SchedulerAddress;

    private boolean shouldExit;

    private final static String subsystemName = "ElevatorSubsystem";

    private final static Logger LOGGER = Logger.getLogger(subsystemName);// Logger for system inspection

    /**
     * Constructs and elevator using a scheduler and id
     *
     * @param id the id of the elevator
     */
    public Elevator(int id, InetAddress SchedulerAddress) {
        this.id = id;
        state = new ElevatorState();
        command = null;
        destinationFloors = new ArrayList<Integer>();
        shouldExit = false;

        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            // send UDP Datagram packets.
            sendRecieveSocket = new DatagramSocket();


        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }

        this.SchedulerAddress = SchedulerAddress;
    }



    /**
     * Continuously gets commands from the scheduler until
     * there are no more commands and the floor is done
     * reading the file
     */
    //@Override
    public void run() {
        while (!shouldExit || !destinationFloors.isEmpty() || command != null) {
            //Send request to elevator at the start
            if (command == null && !shouldExit) {
                this.rpcSend();
            }
            //Checks whether the elevator should go up or down
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            moveFloor(); //Moves floor based on idle status and direction
        }
    }

    /**
     * Getter method for the state of elevator
     * @return the ElevatorState of the elevator
     */
    public ElevatorState getState() {
        return state;
    }

    /**
     * Puts the command into the command field. Also,
     * the method sets up direction and sets idle status to false
     * @param command the command that will be put into command
     */
    public void putCommand(Command command){
        while (!state.isIdleStatus()){
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        this.addCommand(command);
        notifyAll();
    }


    /**
     * Gets the command from the elevator
     * @return the command from the elevator
     */
    public Command getCommand(){

        while (state.isIdleStatus()){
            try {
                wait();
            } catch (InterruptedException e) {
            }
        }
        Command temp = command;
        command = null;
        return temp;
    }


    /**
     * Moves the elevator up or down based on the direction and idle
     * status of the elevator
     */
    private void moveFloor(){
    	//check if any passengers need to get off
        int i = 0;
        boolean hasBelow = false;
        boolean hasAbove = false;
        while (i < destinationFloors.size()) {
            if (state.getFloorLevel() == destinationFloors.get(i)) {
                System.out.println("Arrived at floor \n" + destinationFloors.get(i) + "\n");
                destinationFloors.remove(i);
            } else if (state.getFloorLevel() < destinationFloors.get(i)) {
            	hasAbove = true;
            	i++;
            } else {
            	hasBelow = true;
            	i++;
            }
        }
        // Sidestep tricky problems when finished everything but destinations by checking for null
        // Check if elevator floor and command floor are equal
        if (command != null && state.getFloorLevel() == command.getFloor()) {
            System.out.println("Elevator Picking Up Passengers with command:\n" + command + "\n");
            destinationFloors.add(command.getElevatorButton());
            command = null;
        } else {
        	// Change direction if needed.
        	if (state.getDirection() == Direction.DOWN
        			&& (hasAbove || ((command != null) && (state.getFloorLevel() < command.getFloor())))
        			&& !hasBelow) {
        		state.setDirection(Direction.UP);
        	} else if (state.getDirection() == Direction.UP
        			&& (hasBelow || ((command != null) && (state.getFloorLevel() > command.getFloor())))
        			&& !hasAbove) {
        		state.setDirection(Direction.DOWN);
        	}
        	
	        //Move depending on destination
	        if (state.getDirection() == Direction.UP){
	            state.goUp();
	        } else {
	            state.goDown();
	        }
	
	        //State the new floor
	        System.out.println("Elevator is now on floor: " + state.getFloorLevel() + "\n");
        }

    }

    /**
     * Send a message to Intermediate, then receive from Intermediate.
     * @return String reply from Intermediate.
     */
    public void rpcSend() {

        //Sent data to scheduler
        byte[] sendData = serializeState(state);
        sendPacket = new DatagramPacket(sendData, sendData.length,
                SchedulerAddress, 69);
        System.out.println("Elevator: Sending Packet:");

        // Send the datagram packet to the scheduler via the send socket.
        try {
            sendRecieveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Elevator: Packet sent");


        //--------------------------------//
        //Getting command from scheduler//
        //--------------------------------//
        byte data[] = new byte[1000];
        receivePacket = new DatagramPacket(data, data.length);
        System.out.println("Elevator: Waiting for Packet.\n");

        // Block until a datagram packet is received from receiveSocket.
        try {
            System.out.println("Waiting..."); // so we know we're waiting
            sendRecieveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        // Process the received datagram.
        System.out.println("Elevator: Packet Received:");

        int len = receivePacket.getLength();
        if (len == 0) {
            shouldExit = true;
        } else {
            this.addCommand(deserialize(data));
        }


        // Slow things down (wait 2 seconds)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
        return;
    }

    /**
     * Updates the state of the elevator with the new command
     * @param command
     */
    private void addCommand(Command command){
        this.command = command;
        System.out.println("Elevator received Command:\n" + command + "\n");
        //Determine which direction to go by comparing the state and command
        if (destinationFloors.isEmpty()) {
            if (state.getFloorLevel() > command.getFloor()) {
                state.setDirection(Direction.DOWN);
            } else {
                state.setDirection(Direction.UP);
            }
        }
        state.setIdleStatus(false);
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
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static void main(String args[]) {
        InetAddress name;
        if (args.length == 0) {
            try {
                //get the name of the machine
                name = InetAddress.getLocalHost();
            } catch (UnknownHostException e) {
                throw new RuntimeException(e);
            }
        }
        else {
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
        Elevator elevator = new Elevator(24, name);
        Thread elevatorThread = new Thread(elevator, "Elevator");
        elevatorThread.start();
    }
}
