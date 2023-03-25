// Reading input file.
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.ArrayList;

// The usual junit imports
import org.junit.Before;
import org.junit.Test;
import org.junit.Assert;

import static org.mockito.ArgumentMatchers.any;
// Mockito imports to isolate Elevator
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

/**
 * Tests for the Floor class.
 *
 * @author Ethan Leir 101146422
 * @version 1.0
 *
 * @author Ali El-Khatib 101189859
 * @version 2.0
 */
@ExtendWith(MockitoExtension.class)
public class FloorTest {
    private Floor floor;
    private Command command1;
    private Command command2;
    private Command command3;
    private Command command4;
    private DatagramSocket socketMock;

    private ArrayList<DatagramPacket> sendPackets;

    @Before
    public void setupMocks() {
        sendPackets = new ArrayList<DatagramPacket>();
        File file = new File("floorTestText.txt");
        this.command1 = new Command("7 3 up 6");
        this.command2 = new Command("10 4 down 2");
        this.command3 = new Command("10 6 down 4");
        this.command4 = new Command("8 5 up 3");
        socketMock = mock(DatagramSocket.class);

        this.floor = new Floor(file, socketMock);

        // Track packets being sent by socketMock.send()
        try {
            doAnswer(
                    new Answer<Void>() {
                        public Void answer(InvocationOnMock invocation) {
                            Object[] args = invocation.getArguments();
                            Object mock = invocation.getMock();

                            if (args[0] instanceof DatagramPacket) {
                                sendPackets.add((DatagramPacket) args[0]);
                            }
                            return null;
                        }
                    }).when(socketMock).send(any(DatagramPacket.class));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Test for the Floor.run() method.
     * Asserts that the correct number of sends were made.
     * Asserts that the messages passed to send are correct.
     * @throws InterruptedException
     */
    @Test
    public void testRun() throws InterruptedException {
        floor.startSubsystem();

        // See if we sent the correct command the correct number of times.
        Assert.assertEquals(sendPackets.size(), 4);
        try {
            verify(socketMock, times(1)).send(sendPackets.get(0));
            verify(socketMock, times(1)).send(sendPackets.get(1));
            verify(socketMock, times(1)).send(sendPackets.get(2));
            verify(socketMock, times(1)).send(sendPackets.get(3));
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        // Can't verify receive because we don't have its arguments.

        // See if the messages are correct.
        ArrayList<Command> commands1 = deserialize(sendPackets.get(0).getData());
        Assert.assertEquals(commands1.size(), 1);
        Assert.assertEquals(commands1.get(0), command1);

        //Check if the sorting worked and the second command is at time 8
        ArrayList<Command> commands2 = deserialize(sendPackets.get(1).getData());
        Assert.assertEquals(commands2.size(), 1);
        Assert.assertEquals(commands2.get(0), command4);

        //Check if the 2 commands with same time are sent at the same time
        ArrayList<Command> commands3 = deserialize(sendPackets.get(2).getData());
        Assert.assertEquals(commands3.size(), 2);
        Assert.assertEquals(commands3.get(0), command2);
        Assert.assertEquals(commands3.get(1), command3);

    }

    /**
     * @author Mohammed Abu Alkhair
     * EDITED BY: HASAN Al-HASOO
     * Deserializes a byte array into an ArrayList of command objects.
     * @param serializedMessage a byte array representing the serialized Command object
     * @return the deserialized ArrayList of commands, or null if an error occurs
     */
    private ArrayList<Command> deserialize(byte[] serializedMessage) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(serializedMessage);
            ObjectInputStream objIn = new ObjectInputStream(in);
            return (ArrayList<Command>) objIn.readObject();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
