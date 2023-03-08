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

    private Scheduler scheduler; //Represents the shared scheduler between the elevator and the floor
    private int id; //Represents the id of the elevator

    private ElevatorState state; //state of the elevator

    private Command command;

    private DatagramPacket sendPacket, receivePacket; //Send and recieve packets to communicate with the scheduler

    private DatagramSocket sendSocket, receiveSocket; //Send and recieve sockets to communicate with the scheduler

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

        try {
            // Construct a datagram socket and bind it to any available
            // port on the local host machine. This socket will be used to
            // send UDP Datagram packets.
            sendSocket = new DatagramSocket();

            // Construct a datagram socket and bind it to port 69
            // on the local host machine. This socket will be used to
            // receive UDP Datagram packets.
            receiveSocket = new DatagramSocket(69);

        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    /**
     * Process data from scheduler
     * @param data from scheduler
     *
     */
    public String processData(byte[] data) {
        if (data[0] != 0) {
            throw new Error("Invalid request.");
        }
        //if the scheduler sends a special command, the floor must quit

    }


    /**
     * Continuously gets commands from the scheduler until
     * there are no more commands and the floor is done
     * reading the file
     */
    //@Override
    public void run() {
        while (!scheduler.shouldExit()) {
            Command command = ;
            if(this.command == null){
                String request = this.rpcSend(null);
                String acknowledgement = this.processData(request.getBytes());
                this.rpcSend(acknowledgement);
                command = this.getCommand();
            }

            //Checks whether the elevator should go up or down
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }

            moveFloor(command); //Moves floor based on idle status and direction
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
    private synchronized void moveFloor(Command command){
        // return if the elevator is idle.
        if (state.isIdleStatus())
            return;
        state.setFloorLevel(command.getFloor());
        System.out.println("Elevator is now on floor: " + state.getFloorLevel() + "\n");

        //Check if elevator floor and command floor are equal
        if (state.getFloorLevel() == command.getFloor()) {
            System.out.println("Elevator finished Command:\n" + command + "\n");
            state.setIdleStatus(true); //Set idle status to true since the command is done
        }
        notifyAll();
    }

    /**
     * Send a message to Intermediate, then receive from Intermediate.
     * @param sendStr The message to send, or null if empty.
     * @return String reply from Intermediate.
     */
    public String rpcSend(String sendStr) {

        //Sent data to scheduler
        byte[] sendData = {};
        if (sendStr != null) {
            sendData = sendStr.getBytes();
        }
        try {
            sendPacket = new DatagramPacket(sendData, sendData.length,
                    InetAddress.getLocalHost(), 24);
        } catch (UnknownHostException e1) {
            e1.printStackTrace();
        }
        System.out.println("Elevator: Sending packet:");
        System.out.println("To host: " + sendPacket.getAddress());
        System.out.println("Destination host port: " + sendPacket.getPort());
        int len = sendPacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing: ");
        System.out.println(new String(sendPacket.getData(),0,len));

        // Send the datagram packet to the scheduler via the send socket.
        try {
            sendSocket.send(sendPacket);
        } catch (IOException e) {
            e.printStackTrace();
            System.exit(1);
        }
        System.out.println("Elevator: packet sent");


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
        System.out.println("Elevator: Packet received:");
        System.out.println("From host: " + receivePacket.getAddress());
        System.out.println("Host port: " + receivePacket.getPort());
        len = receivePacket.getLength();
        System.out.println("Length: " + len);
        System.out.print("Containing:\n");
        System.out.print(data + "\n");

        String received = new String(data,0,len);
        System.out.println(received + "\n");

        //when the elevator informs the scheduler it is empty
        if(sendStr == null){
            this.putCommand(new Command(data));
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

    //make a function that would check if the elevator is sending nothing or an acknowledgement statement





}