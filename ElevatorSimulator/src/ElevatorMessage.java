import java.io.Serializable;

/**
 * Represents a message that can be sent between the elevator and the scheduler.
 * The message can be null, exit, or a command.
 *
 * @author Mohammed Abu Alkhair
 *
 */
public class ElevatorMessage implements Serializable {

    public enum Type {NULL, EXIT, Command};
    private Type type;
    private Command command;

    /**
     * Constructor for class ElevatorMessage
     */
    public ElevatorMessage(Type type)
    {
        this.type = type;
    }

    /**
     * Constructor for class ElevatorMessage
     */
    public ElevatorMessage(Command command)
    {
        this.command = command;
        this.type = Type.Command;
    }

    /**
     * Return the type of message sent
     * @return the type of the message
     */
    public Type getType() {
        return type;
    }

    /**
     * Return the command in the message
     * @return command
     */
    public Command getCommand() {
        return command;
    }
}
