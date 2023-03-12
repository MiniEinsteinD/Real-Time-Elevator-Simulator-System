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
    private DatagramSocket sendReceiveSocket = null;
    private Thread schedulerThread;

    @Before
    public void setUp() {
        transmitter = new SchedulerTransmitter();
        schedulerThread = new Thread(transmitter);
        schedulerThread.start();
    }

    @After
    public void tearDown() {
        transmitter.exitThreads();
    }

    @Test
    public void testSchedulerTransmitter() throws IOException {
        //create two command
        Command command = new Command(24,1, Direction.UP, 3);
        Command command2 = new Command(24,10, Direction.DOWN, 1);

        //insert the two commands onto the transmitter
        transmitter.placeCommand(command);
        transmitter.placeCommand(command2);

        //create socket that simulates the elevator
        DatagramSocket socket = new DatagramSocket(71);
        InetAddress address = InetAddress.getLocalHost();


        // create ElevatorState to send to the SchedulerTransmitter
        ElevatorState elevatorState = new ElevatorState();

        //create the sendPacket
        byte[] serializedState = Elevator.serializeState(elevatorState);
        DatagramPacket packet = new DatagramPacket(serializedState, serializedState.length, InetAddress.getLocalHost(), 69);

        //create the receive packet
        byte[] receiveData = new byte[1000];
        DatagramPacket receivepacket = new DatagramPacket(receiveData, receiveData.length);

        //send the packet
        socket.send(packet);

        // receive the packet
        socket.receive(receivepacket);

        //get the command
        Command receivedCommand = Elevator.deserialize(receivepacket.getData());

        assertEquals(command, receivedCommand);
    }


}