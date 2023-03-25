import static org.junit.Assert.*;
import org.junit.*;

public class MarshallingTest {
    private Command command;
    private ElevatorState elevatorState;

    @Before
    public void setUp() {
        command = new Command(24, 2, Direction.UP, 4);
        elevatorState = new ElevatorState();
    }

    @After
    public void tearDown() {
        command = null;
        elevatorState = null;
    }

    @Test
    public void testSerializeAndDeserializeCommand() {
        byte[] serialized = Marshalling.serialize(command);
        Command deserialized = Marshalling.deserialize(serialized, Command.class);
        assertEquals(command, deserialized);
    }

    @Test
    public void testSerializeAndDeserializeElevatorState() {
        byte[] serialized = Marshalling.serialize(elevatorState);
        ElevatorState deserialized = Marshalling.deserialize(serialized, ElevatorState.class);
        assertEquals(elevatorState, deserialized);
    }

    @Test
    public void testDeserializeIncorrectClass() {
        byte[] serialized = Marshalling.serialize(command);
        try {
            ElevatorState deserialized = Marshalling.deserialize(serialized, ElevatorState.class);
            fail("Expected IllegalArgumentException not thrown.");
        } catch (IllegalArgumentException e) {
            // Pass
        }
    }

    @Test
    public void testDeserializeInvalidObject() {
        byte[] serializedObject = new byte[] { 0, 1, 2, 3 };
        Command deserializedObject = Marshalling.deserialize(serializedObject, Command.class);
        assertNull(deserializedObject);
    }
}