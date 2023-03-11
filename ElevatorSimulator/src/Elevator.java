/**
 * The Elevator class represents the subsystem of
 * elevators in the simulaton. It only is meant for
 * 1 elevator for iteration 1
 * 
 * @author Ali El-Khatib
 * @version 1.0
 */

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;

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

    private SchedulerTransmitter scheduler; //Represents the shared scheduler between the elevator and the floor
    private int id; //Represents the id of the elevator

    private ElevatorState state; //state of the elevator

    private Command command;

    private DatagramPacket sendPacket, receivePacket; //Send and recieve packets to communicate with the scheduler

    private DatagramSocket sendSocket, receiveSocket; //Send and recieve sockets to communicate with the scheduler

    private List<Integer> destinationFloors;

    private boolean shouldExit;

    /**
     * Constructs and elevator using a scheduler and id
     *
     * @param scheduler the shared scheduler between the elevator and the floor
     * @param id the id of the elevator
     */
    public Elevator(Scheduler scheduler, int id) {
        this.scheduler = scheduler;
        this.id = id;
        state = new ElevatorState();
        command = null;
        destinationFloors = new ArrayList<Integer>();
        shouldExit = false;

        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            // send UDP Datagram packets.
            sendSocket = new DatagramSocket();

            // on the local host machine. This socket will be used to
            // receive UDP Datagram packets.
            receiveSocket = new DatagramSocket();

        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }



    /**
     * Continuously gets commands from the scheduler until
     * there are no more commands and the floor is done
     * reading the file
     */
    //@Override
    public void run() {
        while (true) {
            //Send reequest to elevator at the start
            if (command == null && destinationFloors.isEmpty()) {
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
    public synchronized ElevatorState getState() {
        return state;
    }

    /**
     * Puts the command into the command field. Also,
     * the method sets up direction and sets idle status to false
     * @param command the command that will be put into command
     */
    public synchronized void putCommand(Command command){
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
    public synchronized Command getCommand(){

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
     * @param command the Command to execute
     */
    private synchronized void moveFloor(){
        // return if the elevator is idle.
        if (state.isIdleStatus())
            return;

        //Move depending on destination
        if (state.getDirection == Direction.UP){
            state.goUp();
        } else {
            state.goDown();
        }

        //State the new floor
        System.out.println("Elevator is now on floor: " + state.getFloorLevel() + "\n");

        //Check if elevator floor and command floor are equal
        if (state.getFloorLevel() == command.getFloor()) {
            System.out.println("Elevator Picking Up Passengers with command:\n" + command + "\n");
            destinationFloors.add(command.getElevatorButton());
            if (state.getFloorLevel() > command.getElevatorButton()) {
                state.setDirection(Direction.DOWN);
            }
            else{
                state.setDirection(Direction.UP);
            }
            command = null;
            this.rpcSend(); //Get another command
        }

        //check if any passengers need to get off
        for (int d: destinationFloors) {
            if (state.getFloorLevel() == d) {
                System.out.println("Arrived at floor \n" + c + "\n");
                destinationFloors.remove(d);
                //Set idle status to true since the command is done and there are no destinations
                if (destinationFloors.isEmpty() && command == null) {
                    state.setIdleStatus(true);
                }
            }
        }

        //Exit if all commands are serviced and the scheduler told us to exit
        if (shouldExit && destinationFloors.isEmpty() && command == null) {
            System.exit(0);
        }

    }

    /**
     * Send a message to Intermediate, then receive from Intermediate.
     * @return String reply from Intermediate.
     */
    public String rpcSend() {

        //Sent data to scheduler
        byte[] sendData = serializeState(state);
        try {
            sendPacket = new DatagramPacket(sendData, sendData.length,
                    InetAddress.getLocalHost(), 69);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        System.out.println("Elevator: Sending Packet:");

        // Send the datagram packet to the scheduler via the send socket.
        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Elevator: Packet sent");


        //--------------------------------//
        //Getting command from scheduler//
        //--------------------------------//
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);
        System.out.println("Elevator: Waiting for Packet.\n");

        // Block until a datagram packet is received from receiveSocket.
        try {
            System.out.println("Waiting..."); // so we know we're waiting
            receiveSocket.receive(receivePacket);
        } catch (IOException e) {
            System.out.print("IO Exception: likely:");
            System.out.println("Receive Socket Timed Out.\n" + e);
            e.printStackTrace();
            System.exit(1);
        }

        // Process the received datagram.
        System.out.println("Elevator: Packet Received:");

        len = receivePacket.getLength();
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
        return received;
    }

    /**
     * Updates the state of the elevator with the new command
     * @param command
     */
    private void addCommand(Command command){
        this.command = command;
        System.out.println("Elevator received Command:\n" + command + "\n");
        //Determine which direction to go by comparing the state and command
        if (state.getFloorLevel() > command.getFloor()) {
            state.setDirection(Direction.DOWN);
        }
        else{
            state.setDirection(Direction.UP);
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
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}