import java.io.IOException;
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
    private List<Integer> IDList;


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
        IDList = new ArrayList<>();
    }

    /**
     * This method is invoked when the thread is started
     *
     * Receives a request from Elevators and send back the best
     * command for the elevator to service.
     */
    public void run()
    {
        //Create Receive packet from elevator
        byte data[] = new byte[100];
        receivePacket = new DatagramPacket(data, data.length);

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

            //

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


}
