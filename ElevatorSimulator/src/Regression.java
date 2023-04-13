import org.junit.Before;
import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class Regression {

    @Test
    public void regressiontest() {
        //modify the input file
        String commandString = "1 2 UP 4 \n";
        writeToFile("InputFileForTesting.txt", commandString);

        //string to look out for
        String floorTest1 = "INFO: Input to an XML file: Command Composed Of:\n" +
                "Time: 1\n" +
                "Floor: 2\n" +
                "Elevator Button Pressed: 4\n" +
                "Direction: UP\n" +
                "Recoverable Fault: false\n" +
                "Permanent Fault: false\n" +
                "Fault Location: 0";
        String floorTest2 = "INFO: Floor: Sending commands to Scheduler subsystem.";
        String floorTest3 = "INFO: Floor system sent a CommandList";
        String floorTest4 = "INFO: Floor: Receiving confirmation of completion from the Scheduler subsystem.";

        //test if floor string is in logging file
        assertTrue(stringExistsInFile("FloorSubsystem.log", floorTest1));
        assertTrue(stringExistsInFile("FloorSubsystem.log", floorTest2));
        assertTrue(stringExistsInFile("FloorSubsystem.log", floorTest3));
        assertTrue(stringExistsInFile("FloorSubsystem.log", floorTest4));

        //scheduler loggings
        String receiverTest1 = "INFO: Elevator Packet Received: ElevatorState: Floor Level: 1, Direction: UP, Idle Status: true";
        String transmitterTest2 = "INFO: Received command: Command Composed Of:\n" +
                "Time: 1\n" +
                "Floor: 2\n" +
                "Elevator Button Pressed: 4\n" +
                "Direction: UP\n" +
                "Recoverable Fault: false\n" +
                "Permanent Fault: false\n" +
                "Fault Location: 0";
        String transmitterTest3 = "INFO: Transmitter sent a Packet to the elevator: Command Composed Of:\n" +
                "Time: 1\n" +
                "Floor: 2\n" +
                "Elevator Button Pressed: 4\n" +
                "Direction: UP\n" +
                "Recoverable Fault: false\n" +
                "Permanent Fault: false\n" +
                "Fault Location: 0";

        //test if scheduler strings are in the logger file
        assertTrue(stringExistsInFile("SchedulerSubsystem.log", receiverTest1));
        assertTrue(stringExistsInFile("SchedulerSubsystem.log", transmitterTest2));
        assertTrue(stringExistsInFile("SchedulerSubsystem.log", transmitterTest3));

        //elevator loggings
        String elevatorTest1 = "INFO: Elevator 1: sends packet to transmitter ElevatorState: Floor Level: 1, Direction: UP, Idle Status: true";
        String elevatorTest2 = "INFO: Elevator 1 received packet from transmitterCommand Composed Of:\n" +
                "Time: 1\n" +
                "Floor: 2\n" +
                "Elevator Button Pressed: 4\n" +
                "Direction: UP\n" +
                "Recoverable Fault: false\n" +
                "Permanent Fault: false\n" +
                "Fault Location: 0";
        String elevatorTest3 = "INFO: Elevator 1 has picked up the passengers";
        String elevatorTest4 = "INFO: Elevator 1 has arrived to the destination";

        //test if elevator strings are in the logger file
        assertTrue(stringExistsInFile("ElevatorSubsystem.log", elevatorTest1));
        assertTrue(stringExistsInFile("ElevatorSubsystem.log", elevatorTest2));
        assertTrue(stringExistsInFile("ElevatorSubsystem.log", elevatorTest3));
        assertTrue(stringExistsInFile("ElevatorSubsystem.log", elevatorTest4));

    }

    public static void writeToFile(String fileName, String content) {
        try {
            FileWriter fileWriter = new FileWriter(fileName, false); // false flag means "clear the file"
            PrintWriter printWriter = new PrintWriter(fileWriter);
            printWriter.print(content);
            printWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean stringExistsInFile(String fileName, String targetString) {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.contains(targetString)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
