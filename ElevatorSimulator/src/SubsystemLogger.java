import java.io.IOException;
import java.util.logging.*;

/**
 * SubsystemLogger sets up a logger for a specific subsystem, based on the
 * subsystem's name.
 *
 * For system inspection in regression and acceptance tests.
 *
 * @author Ethan Leir
 * @version 1.0
 */
public class SubsystemLogger {
    static private FileHandler file;
    static private Formatter formatter;

    /**
     * Setup the logger based on the subsystemName.
     * @param subsystemName String, the name of the subsystem
     */
    static public void setup(String subsystemName) throws IOException {

        // get the subsystem logger to configure it
        Logger logger = Logger.getLogger(subsystemName);

        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        // log INFO level messages and higher
        logger.setLevel(Level.INFO);
        file = new FileHandler(subsystemName + ".log");

        // create an XML formatter (comes with extra useful info)
        formatter = new SimpleFormatter();
        file.setFormatter(formatter);
        logger.addHandler(file);
    }


}
