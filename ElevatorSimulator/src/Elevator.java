import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.logging.Logger;
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

    private final ElevatorFrame frame;
    private int id; //Represents the id of the elevator

    private ArrayList<Command> commands;

    private DatagramPacket sendPacket, receivePacket; //Send and recieve packets
                                                      //to communicate with the
                                                      //scheduler

    private DatagramSocket sendRecieveSocket; //SendRecieve sockets to
                                              //communicate with the scheduler

    private List<Integer> destinationFloors;
    private List<Integer> recoverableFaultFloors;
    private List<Integer> permanentFaultFloors;

    private int closestFloor; // The next floor we need to reach for a
                                  // command we're servicing.

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
    private final static String subsystemName = "ElevatorSubsystem";

    private final static Logger LOGGER = Logger.getLogger(subsystemName);// Logger for system inspection

    private Timer faultTimer = new Timer(); // Timer for interrupting on a
                                            // fault.


    /**
     * Constructs and elevator using a scheduler and id
     *
     * @param id the id of the elevator
     */
    public Elevator(int id, InetAddress SchedulerAddress, ElevatorFrame frame) {
        this.id = id;
        commands = new ArrayList<Command>();
        destinationFloors = new ArrayList<Integer>();
        recoverableFaultFloors = new ArrayList<Integer>();
        permanentFaultFloors = new ArrayList<Integer>();

        // Set closestFloor to an illegal value to start.
        closestFloor = MAX_FLOOR_LEVEL + 1;

        shouldExit = false;
        direction = Direction.UP;
        floorLevel = 1;
        idleStatus = true;
        hasUTurnCommand = false;
        this.frame = frame;
        frame.update(new ElevatorState(direction, floorLevel, true, id));

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
            do {
                shouldContinue = false;
                //Send request to elevator at the start.
                //When we have been told to exit that means the scheduler has no
                //commands left.
                //When we have a U-Turn Command we don't want to accept commands
                //in the direction we're moving.
                if (!(shouldExit || hasUTurnCommand)) {
                    shouldContinue = this.retrieveCommandFromScheduler();
                }
                //Forces Elevators to take an equal number of commands
                //because they can't hog send requests to the scheduler.
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {}
                //Keep sending while we're told by the Scheduler we can
                //continue.
            } while (shouldContinue);

            // We're on the next floor we needed to get to.
            if (closestFloor == floorLevel) {
                //Handles all actions associated with reaching a
                //floor.
                reachFloor();
                //We reached a floor; the next one we must go to may have
                //changed.
                updateClosestFloor();
            }
            //If we're out of commands and destinations, let the scheduler
            //know they can make us change direction.
            if (commands.size() == 0 && destinationFloors.size() == 0) {
                System.out.println("Elevator " + id + " is now idle."
                        + "\n");
                idleStatus = true;
            } else {
                moveFloor(); //Moves floor based on idle status and
                             //direction.
            }
            frame.update(new ElevatorState(direction, floorLevel, idleStatus, id));
        }

        System.out.println("Elevator " + id + " is exiting." + "\n");
        faultTimer.cancel();
        faultTimer.purge();
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
        boolean actionComplete = false; // Successfully opened/closed the doors.

        int i = 0;
        int faultFloor;

        // Determine if we're on a recoverable fault floor.
        while (i < recoverableFaultFloors.size()) {
            faultFloor = recoverableFaultFloors.get(i);
            if (floorLevel == faultFloor) {
                isRecoverableFaultFloor = true;
                recoverableFaultFloors.remove(i);
            }
            ++i;
        }

        // Schedule an interrupt if we're stuck on a fault.
        // Repeat in case fault persists.
        faultTimer.schedule(recoverableFaultHandler, 4 * OPEN_CLOSE_TIME,
                4 * OPEN_CLOSE_TIME);

        while (!actionComplete) {
            if (isRecoverableFaultFloor && !faultExercised) {
                // Simulate problems with doors opening.
                // Uhoh, who put this loop here??
                while(true) {
                    try {
                        // Amount of time is meaningless. Go with a higher value
                        // so that we don't burn clock cycles.
                        Thread.sleep(4000);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                System.out.println("Elevator " + id + " handled door issues "
                        + "on floor " + floorLevel + "\n");
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
                    actionComplete = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        // Cleanup.
        try {
            recoverableFaultHandler.cancel();
        } catch (IllegalStateException e) {}
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
                System.out.println("A passenger on Elevator " + id + " arrived "
                        + "at floor " + destinationFloors.get(i) + "\n");
                LOGGER.info("Elevator "+ id + " has arrived to the destination");
                destinationFloors.remove(i);
            } else {
                ++i;
            }
        }
    }

    /**
     * Lets passengers onto the elevator.
     * Removes commands at the current floor from the internal list.
     * Records destinations and faults in internal lists.
     */
    private void passengersEntering() {
        // Check if elevator floor and a command floor are equal.
        int i = 0;
        while (i < commands.size()) {
            if (floorLevel == commands.get(i).getFloor()) {
                System.out.println("Elevator " + id + " picking up passenger "
                        + "with command:\n" + commands.get(i) + "\n");
                LOGGER.info("Elevator "+ id + " has picked up the passengers");
                destinationFloors.add(commands.get(i).getElevatorButton());
                if (commands.get(i).isRecoverableFault()) {
                    recoverableFaultFloors.add(commands.get(i).getFaultLocation());
                } else if (commands.get(i).isPermanentFault()) {
                    permanentFaultFloors.add(commands.get(i).getFaultLocation());
                }
                // Change directions if the command is the special case where
                // we had to move the direction opposite to where the passenger
                // wants to go so that we could pick up the passenger.
                // We only accept these one at a time.
                if (hasUTurnCommand) {
                    direction = commands.get(i).getDirectionButton();
                    System.out.println("Elevator " + id + " is now moving "
                            + direction + "\n");

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
        int permanentFaultFloor;

        // Determine if we're on a permanent fault floor.
        while (i < permanentFaultFloors.size()) {
            permanentFaultFloor = permanentFaultFloors.get(i);
            if (floorLevel == permanentFaultFloor) {
                isPermanentFaultFloor = true;
                permanentFaultFloors.remove(i);
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
                    faultTimer.cancel();
                    faultTimer.purge();
                    throw new RuntimeException("Elevator " + id
                            + " stalled between floors");
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
            faultTimer.cancel();
            faultTimer.purge();
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

        new ElevatorState(direction, floorLevel, idleStatus, id);
        //Sent data to scheduler
        byte[] sendData = Marshalling.serialize(
                new ElevatorState(direction, floorLevel, idleStatus, id));
        sendPacket = new DatagramPacket(sendData, sendData.length,
                SchedulerAddress, 69);
        System.out.println("Elevator " + id + ": Sending Packet:");
        LOGGER.info("Elevator "+ id + ": sends packet to transmitter " + new ElevatorState(direction, floorLevel, idleStatus, id));//TODO - create a variable that stores the state

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
        System.out.println("Elevator " + id + ": Waiting for Packet.");

        // Block until a datagram packet is received from receiveSocket.
        try {
            System.out.println("Waiting...\n"); // so we know we're waiting
            sendRecieveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        // Process the received datagram.
        System.out.println("Elevator " + id + ": Packet Received:\n");

        // Handle exit/no-available-command/command messages differently
        if (receivePacket.getLength() == 0) {
            faultTimer.cancel();
            faultTimer.purge();
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
            LOGGER.info("Elevator "+ id + " received packet from transmitter" + command);
        } else {
            faultTimer.cancel();
            faultTimer.purge();
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
            // We are only moving towards one floor so it's closest.
            closestFloor = command.getFloor();
            System.out.println("Elevator " + id + " is now moving "
                    + direction + "\n");
        } else {
            // Update closest if it's beaten by the new contender.
            closestFloor = directionalLeast(closestFloor, command.getFloor(),
                    direction);
        }
        // We have something to move towards!
        idleStatus = false;
    }


    /**
     * Determines which of two floors is least dependent on the direction.
     * If the direction is Direction.UP, lower floors are preferred.
     * If the direction is Direction.DOWN, higher floors are preferred.
     * @param aFloor a floor to be compared.
     * @param bFloor a floor to be compared.
     * @param direction the direction to do the comparison.
     * @return int, the floor that is least in the direction
     */
    private static int directionalLeast(int aFloor, int bFloor,
            Direction direction) {
        if (direction == Direction.UP) {
            if (aFloor < bFloor) {
                return aFloor;
            } else {
                return bFloor;
            }
        } else {
            if (aFloor < bFloor) {
                return bFloor;
            } else {
                return aFloor;
            }
        }
    }


    /**
     * Updates our record of the closest floor based on all of the floors we
     * must reach.
     */
    private void updateClosestFloor() {
        int closestFloor;
        int i = 0;
        // Set closestFloor to greater than max/min possible value so it gets
        // overwritten by first command.
        if (direction == Direction.UP) {
            closestFloor = MAX_FLOOR_LEVEL + 1;
        } else {
            closestFloor = MIN_FLOOR_LEVEL - 1;
        }
        while (i < commands.size()) {
            closestFloor = directionalLeast(closestFloor,
                    commands.get(i).getFloor(), direction);
            ++i;
        }

        i = 0;
        while (i < destinationFloors.size()) {
            closestFloor = directionalLeast(closestFloor,
                    destinationFloors.get(i), direction);
            ++i;
        }

        this.closestFloor = closestFloor;
    }



    /*
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

        Elevator elevator1 = new Elevator(1, name);
        Elevator elevator2 = new Elevator(2, name);
        Elevator elevator3 = new Elevator(3, name);
        Thread elevatorThread1 = new Thread(elevator1, "Elevator");
        Thread elevatorThread2 = new Thread(elevator2, "Elevator");
        Thread elevatorThread3 = new Thread(elevator3, "Elevator");
        elevatorThread1.start();
        elevatorThread2.start();
        elevatorThread3.start();
    }*/
}
