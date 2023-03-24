import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.*;

/**
 * The Elevator class represents the subsystem of
 * elevators in the simulaton.
 *
 * @author Ali El-Khatib
 * @version 1.0
 *
 * @author Daniah and Ali
 * @version 2.0
 *
 * @author Ethan Leir
 * @version 3.0
 */
public class Elevator implements Runnable{

    private int id; //Represents the id of the elevator

    private ArrayList<Command> commands;

    private DatagramPacket sendPacket, receivePacket; //Send and recieve packets
                                                      //to communicate with the
                                                      //scheduler

    private DatagramSocket sendRecieveSocket; //SendRecieve sockets to
                                              //communicate with the scheduler

    private List<Integer> destinationFloors;

    private InetAddress SchedulerAddress;

    private boolean shouldExit;

    private Direction direction; //direction the elevator is going toward
    private int floorLevel; //the floor the elevator is currently at
    private boolean idleStatus; // whether elevator is servicing a command or
                                // not

    private static final int MAX_FLOOR_LEVEL = 9;
    private static final int MIN_FLOOR_LEVEL = 1;


    /**
     * Constructs and elevator using a scheduler and id
     *
     * @param id the id of the elevator
     */
    public Elevator(int id, InetAddress SchedulerAddress) {
        this.id = id;
        commands = new ArrayList<Command>();
        destinationFloors = new ArrayList<Integer>();

        shouldExit = false;
        direction = Direction.UP;
        floorLevel = 1;
        idleStatus = true;

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
        boolean shouldContinue;
        while (!(shouldExit && destinationFloors.isEmpty()
                    && commands.size() == 0)) {
            shouldContinue = false;
            //Send request to elevator at the start.
            if (!shouldExit) {
                shouldContinue = this.retrieveCommandFromScheduler();
            }
            //Checks whether the elevator should go up or down.
            //Forces Elevators to take an equal number of commands
            //because they can't hog send requests to the scheduler.
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
            //Keep sending until we're told to we can continue to moving
            //and we aren't idle meaning we have a command to service.
            if (!(shouldContinue || idleStatus)) {
                passengersLeaving(); //Deal with destinations that we've reached
                passengersEntering(); //Deal with commands that we've reached
                moveFloor(); //Moves floor based on idle status and direction
                //If we're out of commands and destinations, let the scheduler
                //know they can make us change direction.
                if (commands.size() == 0 && destinationFloors.size() == 0) {
                    idleStatus = true;
                }
            }
        }
    }


    /**
     * Lets passengers off of the elevator.
     * Removes destinations at the current floor from the internal list.
     */
    private void passengersLeaving() {
        //check if any passengers need to get off
        int i = 0;
        while (i < destinationFloors.size()) {
            if (floorLevel == destinationFloors.get(i)) {
                System.out.println("Elevator " + id + " Arrived at floor \n"
                        + destinationFloors.get(i) + "\n");
                destinationFloors.remove(i);
            } else {
                i++;
            }
        }
    }


    private void passengersEntering() {
        // Check if elevator floor and a command floor are equal.
        int i = 0;
        while (i < commands.size()) {
            if (floorLevel == commands.get(i).getFloor()) {
                System.out.println("Elevator " + id
                        + " Picking Up passenger with command:\n"
                        + commands.get(i) + "\n");
                destinationFloors.add(commands.get(i).getElevatorButton());
                commands.remove(i);
            }
        }
    }


    /**
     * Moves the elevator up or down based on the direction and idle
     * status of the elevator
     */
    private void moveFloor(){
        //Move depending on destination
        if (direction == Direction.UP
                && floorLevel < MAX_FLOOR_LEVEL){
            floorLevel++;
        } else if (floorLevel > MIN_FLOOR_LEVEL) {
            floorLevel--;
        }

        //State the new floor
        System.out.println("Elevator " + id + " is now on floor: "
                + floorLevel + "\n");

    }


    /**
     * Send a message to the scheduler, then receive a command from the
     * scheduler.
     * @return boolean continue getting commands from the scheduler.
     */
    public boolean retrieveCommandFromScheduler() {

        boolean shouldContinue = true;

        //Sent data to scheduler
        byte[] sendData = Marshalling.serialize(
                new ElevatorState(direction, floorLevel, idleStatus));
        sendPacket = new DatagramPacket(sendData, sendData.length,
                SchedulerAddress, 69);
        System.out.println("Elevator " + id + ": Sending Packet:");

        // Send the datagram packet to the scheduler via the send socket.
        try {
            sendRecieveSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Elevator " + id + ": Packet sent");


        //--------------------------------//
        //Getting command from scheduler//
        //--------------------------------//
        byte data[] = new byte[1000];
        receivePacket = new DatagramPacket(data, data.length);
        System.out.println("Elevator " + id + ": Waiting for Packet.\n");

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
        System.out.println("Elevator " + id + ": Packet Received:");

        int len = receivePacket.getLength();
        if (len == 0) {
            shouldExit = true;
        } else {
            Command command = Marshalling.deserialize(data, Command.class);
            if (command == null) {
                shouldContinue = false;
            } else {
                this.addCommand(command);
            }
        }


        // Slow things down (wait 2 seconds)
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e ) {
            e.printStackTrace();
            System.exit(1);
        }
        return shouldContinue;
    }


    /**
     * Updates the state of the elevator with the new command
     * @param command
     */
    private void addCommand(Command command){
        this.commands.add(command);
        System.out.println("Elevator " + id + " received Command:\n" + command
                + "\n");
        //Determine which direction to go by comparing the state and command
        if (idleStatus == true) {
            if (floorLevel > command.getFloor()) {
                direction = Direction.DOWN;
            } else {
                direction = Direction.UP;
            }
        }
        idleStatus = false;
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
        Elevator elevator1 = new Elevator(1, name);
        Elevator elevator2 = new Elevator(2, name);
        Elevator elevator3 = new Elevator(3, name);
        Thread elevatorThread1 = new Thread(elevator1, "Elevator");
        Thread elevatorThread2 = new Thread(elevator2, "Elevator");
        Thread elevatorThread3 = new Thread(elevator3, "Elevator");
        elevatorThread1.start();
        elevatorThread2.start();
        elevatorThread3.start();
    }
}
