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
 */
@ExtendWith(MockitoExtension.class)
public class FloorTest {
    private Floor floor;
    private Command command;
    private DatagramSocket socketMock;
    
    private ArrayList<DatagramPacket> sendPackets;

    @Before
    public void setupMocks() {
    	sendPackets = new ArrayList<DatagramPacket>();
        File file = new File("singleCommand.txt");
        this.command = new Command("10 4 down 2");
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
     * Asserts that the correct message is printed.
     * Asserts that Scheduler.exitThreads() was called the correct
     * number of times when only one command is provided.
     */
    @Test
    public void testRun() {
        floor.startSubsystem();

        // See if we sent the correct command the correct number of times.
        Assert.assertEquals(sendPackets.size(), 2);
        try {
			verify(socketMock, times(1)).send(sendPackets.get(0));
			verify(socketMock, times(1)).send(sendPackets.get(1));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // Can't verify receive because we don't have its arguments.
        
        // See if the messages are correct. 
        ArrayList<Command> commands = deserialize(sendPackets.get(0).getData());
        Assert.assertEquals(commands.size(), 1);
        Assert.assertEquals(commands.get(0), command);
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
