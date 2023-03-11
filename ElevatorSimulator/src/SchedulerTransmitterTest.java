import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.ArrayList;

import static org.junit.Assert.*;

public class SchedulerTransmitterTest {

    private SchedulerTransmitter transmitter;

    @Before
    public void setUp() {
        transmitter = new SchedulerTransmitter();
        Thread schedulerThread = new Thread(transmitter);
        schedulerThread.start();
    }

    @After
    public void tearDown() {
        transmitter.exitThreads();
    }

    @Test
    public void testPlaceCommand() {
        Command command = new Command(24,1, Direction.UP, 3);
        transmitter.placeCommand(command);
        assertEquals(command, transmitter.getCommands());
    }

    @Test
    public void testPlaceCommandList() {
        ArrayList<Command> commandList = new ArrayList<>();
        commandList.add(new Command(24, 1, Direction.UP, 3));
        commandList.add(new Command(25, 2, Direction.DOWN, 5));
        transmitter.placeCommandList(commandList);
        assertEquals(2, (transmitter.getCommandList()).size());
    }

    /*
    @Test
    public void testSocket() throws IOException {
        //create command and insert it into the transmiter
        Command command = new Command(24,1, Direction.UP, 3);
        transmitter.placeCommand(command);

        //create socket that simulates the elevator
        DatagramSocket socket = new DatagramSocket(71);
        InetAddress address = InetAddress.getLocalHost();


        // create ElevatorState to send to the SchedulerTransmitter
        ElevatorState elevatorState = new ElevatorState();
        elevatorState.setIdleStatus(false);

        //create the sendPacket
        byte[] serializedState = SchedulerTransmitter.serializeState(elevatorState);
        DatagramPacket packet = new DatagramPacket(serializedState, serializedState.length, InetAddress.getLocalHost(), 69);

        //create the receive packet
        byte[] receiveData = new byte[100];
        packet = new DatagramPacket(receiveData, receiveData.length);

        //send the packet
        socket.send(packet);

        // receive the packet
        socket.receive(packet);

        //get the command
        Command receivedCommand = SchedulerTransmitter.deserialize(packet.getData());

        assertEquals(command, receivedCommand);



    }
     */

}