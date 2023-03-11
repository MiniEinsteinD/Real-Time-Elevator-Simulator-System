import org.junit.Before;
import org.mockito.junit.MockitoJUnitRunner;
import org.junit.Test;

import static org.mockito.Mockito.*;
import org.mockito.Mock;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramSocket;
import java.net.DatagramPacket;
import java.util.ArrayList;


public class SchedulerReceiverTest {

    SchedulerReceiver schedulerReceiver;

    DatagramSocket socket;

    SchedulerTransmitter transmitter;

    Command command;

    Command command2;

    Command command3;


    DatagramPacket packet;



    @Before
    protected void setup(){
        byte[] data = new byte[100];
        socket = new mock(DatagramSocket.class);
        transmitter = new mock(SchedulerTransmitter.class);
        schedulerReceiver = new SchedulerReceiver(transmitter, socket);
        ArrayList<Command> commands = new ArrayList<>();
         command = new Command(0001, 3, Direction.UP, 5);
         command2 = new Command(0003, 1, Direction.UP, 2);
         command3 = new Command(0007, 2, Direction.UP, 4);
         data = serialize(commands);

        byte[] finalData = data;
        doAnswer(
                new Answer<Void>() {
                    public Void answer(InvocationOnMock invocation) {
                        Object[] args = invocation.getArguments();
                        Object mock = invocation.getMock();

                        if (args[0] instanceof DatagramPacket) {
                            DatagramPacket d = (DatagramPacket) args[0];
                            d.setData(finalData);
                            d.setLength(finalData.length);

                            executedCommands.add(c);

                            elevatorStateMock.setFloorLevel(level);
                        }
                        return null;
                    }
                }).when(socket).receive(any(DatagramPacket.class));
    }

    @Test
    public void testRun(){

        ArrayList<Command> commands = new ArrayList<>();

        commands.add(command);
        commands.add(command2);
        commands.add(command3);















    }

    /**
     * @author Mohammed Abu Alkhair
     * EDITED BY: HASAN Al-HASOO
     * Deserializes a byte array into an ArrayList of command objects.
     * @param serializedMessage a byte array representing the serialized Command object
     * @return the deserialized ArrayList of commands, or null if an error occurs
     */
    private static ArrayList<Command> deserialize(byte[] serializedMessage) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(serializedMessage);
            ObjectInputStream objIn = new ObjectInputStream(in);
            return (ArrayList<Command>) objIn.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Serializes a Command object into a byte array.
     * @param command the Command object to be serialized
     * @return a byte array representing the serialized Command object, or null if an error occurs
     *
     */
    public static byte[] serialize(ArrayList<Command> commands) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(commands);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


}
