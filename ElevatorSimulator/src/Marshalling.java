import java.io.*;

public class Marshalling {

    /**
     * Serializes an object into a byte array.
     * @param object the object to be serialized
     * @return a byte array representing the serialized object, or null if an error occurs
     *
     */
    public static byte[] serialize(Object object) {
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(object);
            return out.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     Deserializes a byte array into an object of type Command or ElevatorState.
     @param serializedMessage a byte array representing the serialized object
     @return the deserialized object either a Command or ElevatorState, or null if an error occurs
     */
    public static Object deserialize(byte[] serializedMessage) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(serializedMessage);
            ObjectInputStream objIn = new ObjectInputStream(in);
            Object obj = objIn.readObject();

            //check the type of object
            if (obj instanceof Command) {
                return (Command) obj;
            } else if (obj instanceof ElevatorState) {
                return (ElevatorState) obj;
            } else {
                throw new IllegalArgumentException("Unknown object type");
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String args[])
    {
        Command c = new Command(24, 2, Direction.UP, 4);
        byte[] arr = Marshalling.serialize(c);
        Command cc = (Command) Marshalling.deserialize(arr);
        System.out.println(cc);

        ElevatorState es = new ElevatorState();
        byte[] ar = Marshalling.serialize(es);
        ElevatorState ess = (ElevatorState) Marshalling.deserialize(ar);
        System.out.println(ess.getFloorLevel() + "");
    }
}
