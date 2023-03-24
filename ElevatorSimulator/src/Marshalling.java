import java.io.*;

public class Marshalling {

    /**
     * Serializes an object into a byte array.
     * @param object the object to be serialized
     * @return a byte array representing the serialized object, or null if an error occurs
     *
     */
    public static byte[] serialize(Object object) {
        ByteArrayOutputStream out;
        ObjectOutputStream objOut;
        byte[] serializedObject = null;
        try {
            out = new ByteArrayOutputStream();
            objOut = new ObjectOutputStream(out);
            objOut.writeObject(object);
            serializedObject = out.toByteArray();
            objOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return serializedObject;
    }

    /**
     Deserializes a byte array into an object of type Command or ElevatorState.
     @param serializedMessage a byte array representing the serialized object
     @param serializedClass the class of the serialized object
     @return the deserialized object of the given type, or null if an error occurs
     */
    public static <T> T deserialize(byte[] serializedMessage, Class<T> serializedClass) {
        ByteArrayInputStream in;
        ObjectInputStream objIn;
        Object obj;
        try {
            in = new ByteArrayInputStream(serializedMessage);
            objIn = new ObjectInputStream(in);
            obj = objIn.readObject();
            objIn.close();

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
