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
     @param serializedClass the class of the serialized object
     @return the deserialized object either a Command or ElevatorState, or null if an error occurs
     */
    public static <T> T deserialize(byte[] serializedMessage, Class<T> serializedClass) {
        try {
            ByteArrayInputStream in = new ByteArrayInputStream(serializedMessage);
            ObjectInputStream objIn = new ObjectInputStream(in);
            Object obj = objIn.readObject();

            //check the type of object
            try {
                return serializedClass.cast(obj);
            } catch (ClassCastException e) {
                throw new IllegalArgumentException("Incorrect class for serialized object.");
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
        Command cc = Marshalling.deserialize(arr, Command.class);
        System.out.println(cc);

        ElevatorState es = new ElevatorState();
        byte[] ar = Marshalling.serialize(es);
        ElevatorState ess = Marshalling.deserialize(ar, ElevatorState.class);
        System.out.println(ess.getFloorLevel() + "");
    }
}
