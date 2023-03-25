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

    // Attributes determining the state of the Elevator!
    private boolean shouldExit;

    private Direction direction; // direction the elevator is going toward.
    private int floorLevel; // the floor the elevator is currently at.
    private boolean idleStatus; // whether elevator is servicing a command or
                                // not.
    private boolean hasUTurnCommand; // the next scheduled command wants us to
                                     // immediately change directions.

    // Constants for avoiding code smell.
    private static final int MAX_FLOOR_LEVEL = 9;
    private static final int MIN_FLOOR_LEVEL = 1;

    private static final long OPEN_CLOSE_TIME = 4831; //Time to open or close
                                                      //the elevator door in ms
    private static final long MOVE_ONE_FLOOR_TIME = 7838; //Time to move one
                                                          //floor in ms

    private Timer faultTimer = new Timer(); // Timer for interrupting on a
                                            // fault.

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
        hasUTurnCommand = false;

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
        while (!(shouldExit && idleStatus)) {
            shouldContinue = false;
            //Send request to elevator at the start.
            //When we have been told to exit that means the scheduler has no
            //commands left.
            //When we have a U-Turn Command we don't want to accept commands
            //in the direction we're moving.
            if (!(shouldExit || hasUTurnCommand)) {
                shouldContinue = this.retrieveCommandFromScheduler();
            }
            //Checks whether the elevator should go up or down.
            //Forces Elevators to take an equal number of commands
            //because they can't hog send requests to the scheduler.
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
            }
            //Keep sending until we're told to we can continue to moving
            //and we aren't idle meaning we have a command to service.
            if (!(shouldContinue || idleStatus)) {
                reachFloor(); //Handles all actions associated with reaching a
                              //floor.
                //If we're out of commands and destinations, let the scheduler
                //know they can make us change direction.
                if (commands.size() == 0 && destinationFloors.size() == 0) {
                    System.out.println("Elevator " + id + " is now idle.");
                    idleStatus = true;
                } else {
                    moveFloor(); //Moves floor based on idle status and
                                 //direction.
                }
            }
        }

        System.out.println("Elevator " + id + " is exiting.");
    }


    /**
     * Handles all actions associated with reaching a floor:
     *  - Doors open
     *  - Passengers leave
     *  - Passengers enter
     *  - Doors close
     *
     *  Handles any associated faults (door won't open, door stuck open)
     */
    private void reachFloor() {
        boolean isRecoverableFaultFloor = false; // Whether or not we need to
                                                 // fault.
        boolean faultExercised = false; // True when we've already stalled on
                                        // a fault.
        final Thread currentThread = Thread.currentThread();
        TimerTask recoverableFaultHandler = new TimerTask() {
            public void run() {
                currentThread.interrupt();
            }
        };

        int i = 0;
        Command c;

        // Determine if we're on a recoverable fault floor.
        while (i < commands.size()) {
            c = commands.get(i);
            if (c.isRecoverableFault() && (floorLevel == c.getFloor())) {
                isRecoverableFaultFloor = true;
            }
            ++i;
        }

        // Schedule an interrupt if we're stuck on a fault.
        // Repeat in case fault persists.
        faultTimer.schedule(recoverableFaultHandler, 4 * OPEN_CLOSE_TIME,
                4 * OPEN_CLOSE_TIME);

        do {
            if (isRecoverableFaultFloor && !faultExercised) {
                // Simulate problems with doors opening.
                // Uhoh, who put this loop here??
                while(!Thread.currentThread().isInterrupted()) {
                    try {
                        // Amount of time is meaningless. Go with a higher value
                        // so that we don't burn clock cycles.
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {}
                }
                // Don't enter this if block again.
                faultExercised = true;
            } else {
                try {
                    // The doors open successfully. Execute normally.
                    Thread.sleep(OPEN_CLOSE_TIME); // Doors are opening.
                    passengersLeaving(); //Deal with destinations that we've
                                         //reached.
                    passengersEntering(); //Deal with commands that we've
                                          //reached.
                    Thread.sleep(OPEN_CLOSE_TIME); // Doors are closing.
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        } while(Thread.interrupted());
        // Cleanup.
        try {
            recoverableFaultHandler.cancel();
        } catch (IllegalStateException e) {}
        // One more call to eliminate any race conditions (say we're
        // interrupted immediately after leaving the while loop).
        Thread.interrupted();
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
                System.out.println("Elevator " + id + " Arrived at floor "
                        + destinationFloors.get(i) + "\n");
                destinationFloors.remove(i);
            } else {
                ++i;
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
                // Change directions if the command is the special case where
                // we had to move the direction opposite to where the passenger
                // wants to go so that we could pick up the passenger.
                // We only accept these one at a time.
                if (hasUTurnCommand) {
                    direction = commands.get(i).getDirectionButton();
                    System.out.println("Elevator " + id + " is now moving "
                            + direction);

                    hasUTurnCommand = false;
                }
                commands.remove(i);
            } else {
                ++i;
            }
        }
    }


    /**
     * Moves the elevator up or down based on the direction and idle
     * status of the elevator
     *
     * Handles associated faults (elevator stuck between destinations)
     */
    private void moveFloor(){
        boolean isPermanentFaultFloor = false; // Whether or not we need to
                                                 // fault.
        final Thread currentThread = Thread.currentThread();
        TimerTask permanentFaultHandler = new TimerTask() {
            public void run() {
                currentThread.interrupt();
            }
        };
        boolean hasSameDirection = false;
        int i = 0;
        Command c;

        // Determine if we're on a permanent fault floor.
        while (i < commands.size()) {
            c = commands.get(i);
            if (c.isPermanentFault() && (floorLevel == c.getFloor())) {
                isPermanentFaultFloor = true;
            }
            ++i;
        }

        // Schedule an interrupt if we're stuck on a fault.
        faultTimer.schedule(permanentFaultHandler, 2 * MOVE_ONE_FLOOR_TIME);

        // Simulate elevator being stuck in place.
        if (isPermanentFaultFloor) {
            // Uhoh, who put this loop here??
            while (true) {
                try {
                    // Amount of time is meaningless. Go with a higher value
                    // so that we don't burn clock cycles.
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Elevator stalled between "
                    + "floors");
                }
            }
        }

        //Wait for the amount of time for an elevator moving between floors that
        //we measured.
        try {
            Thread.sleep(MOVE_ONE_FLOOR_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //Move depending on destination
        if (direction == Direction.UP
                && floorLevel < MAX_FLOOR_LEVEL){
            floorLevel++;
        } else if (direction == Direction.DOWN
                && floorLevel > MIN_FLOOR_LEVEL) {
            floorLevel--;
        } else {
            throw new RuntimeException("Elevator wants to move past max/min "
                    + "floor (likely skipped a command/destination)");
        }

        //State the new floor
        System.out.println("Elevator " + id + " is now on floor: "
                + floorLevel + "\n");

        // Cleanup.
        try {
            permanentFaultHandler.cancel();
        } catch (IllegalStateException e) {}
        // One more call to eliminate any race conditions (say we're
        // interrupted immediately after leaving the while loop).
        Thread.interrupted();
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

        // Handle exit/no-available-command/command messages differently
        if (receivePacket.getLength() == 0) {
            throw new RuntimeException("Invalid length for message received "
                + "from Scheduler.");
        } else if (data[0] == 0) {
            // Exit message
            shouldExit = true;
            shouldContinue = false;
        } else if (data[0] == 1) {
            // No available command message
            shouldContinue = false;
        } else if (data[0] == 2) {
            // Command message
            Command command = Marshalling.deserialize(Arrays.copyOfRange(data, 1, data.length),
                Command.class);
            addCommand(command);
        } else {
            throw new RuntimeException("Invalid format for message received "
                + "from Scheduler.");
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
            //Find the direction to move to get to the passenger.
            if (floorLevel < command.getFloor()) {
                direction = Direction.UP;
            } else if (floorLevel > command.getFloor()) {
                direction = Direction.DOWN;
            } else {
                direction = command.getDirectionButton();
            }
            //The passenger wants to move in the direction opposite from where
            //we move to pick them up.
            //We need to change directions immediately after picking up the
            //next passenger.
            if (direction != command.getDirectionButton()) {
                hasUTurnCommand = true;
            }
            System.out.println("Elevator " + id + " is now moving "
                    + direction);
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
