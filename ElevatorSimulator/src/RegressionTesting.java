import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.net.*;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import org.xml.sax.*;

public class RegressionTesting {
    private static InetAddress name;
    private static DatagramSocket floorSocket;
    private static DatagramSocket receiverSocket;

    public RegressionTesting() {
        //get name of the local host
        try {
            name = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }

        //create sockets
        try {
            floorSocket = new DatagramSocket(69);
            receiverSocket = new DatagramSocket(69);
        } catch (SocketException se) {
            se.printStackTrace();
            System.exit(1);
        }
    }

    private static void testSchedulerTransmitterReceiveSendPackets() throws InterruptedException, IOException {
        //delete already existing logging files
        clearFiles();

        //modify the input file
        String commandString = "1 2 UP 4 \n";
        writeToFile("InputFileForTesting.txt", commandString);

        //create floor
        Floor floor = new Floor(new File("InputFileForTesting.txt"), floorSocket, name);

        //create schedulers
        SchedulerTransmitter tranmitter = new SchedulerTransmitter();
        SchedulerReceiver receiver = new SchedulerReceiver(tranmitter, receiverSocket);
        Thread schedulerThread = new Thread(receiver, "receiver");

        //create elevator
        Elevator elevator = new Elevator(123, name);
        Thread elevatorThread = new Thread(elevator, "elevator");

        //start threads
        schedulerThread.start();
        floor.startSubsystem();
        elevatorThread.start();


        //wait for the elevator thread to complete
        elevatorThread.join();

        //print the XML files
        printFile("ElevatorSubsystem.xml");
        printFile("FloorSubsystem.xml");
        printFile("SchedulerSubsystem.xml");

        String floorTest1 = "Floor system sent a CommandList";
        String receiverTest1 = "Scheduler Receiver has received the following command: " + "Command Composed Of:\n" + "Time: 1\n" + "Floor: 2\n" + "Elevator Button Pressed: 4\n" + "Direction: UP";
        //String transmitterTest1 =


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

    private static void clearFiles() {
        deleteFile("SchedulerSubsystem.xml");
        deleteFile("FloorSubsystem.xml");
        deleteFile("ElevatorSubsystem.xml");
    }

    /**
     * Returns the path to the directory containing the .class file for the specified class.
     *
     * @param className the name of the Java class
     * @return the path to the directory containing the .class file for the specified class
     * @throws ClassNotFoundException if the specified class cannot be found
     */
    public static String getClassDirectory(String className) {
        Class<?> clazz = null;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        URL url = clazz.getProtectionDomain().getCodeSource().getLocation();
        String classPath = url.getPath();
        File classDirFile = new File(classPath).getParentFile();
        return classDirFile.getAbsolutePath();
    }

    /**
     * Deletes the file with the given file name.
     *
     * @param fileName the name of the file to be deleted
     * @throws NullPointerException if fileName is null
     */
    public static void deleteFile(String fileName) {
        File file = new File(fileName);
        if (file.exists()) {
            file.delete();
            System.out.println("File " + fileName + " deleted successfully.");
        } else {
            System.out.println("File " + fileName + " does not exist.");
        }
    }

    public static void printXmlFile(String fileName) {
        System.out.println("Printing" + fileName);

        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(fileName));
            document.getDocumentElement().normalize();
            System.out.println("Root element: " + document.getDocumentElement().getNodeName());
            NodeList nodeList = document.getElementsByTagName("*");
            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    System.out.println(element.getNodeName() + ": " + element.getTextContent());
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the contents of a text file and prints them to the console.
     *
     * @param fileName the name of the file to be printed
     * @throws IOException if an I/O error occurs while reading the file
     * @throws NullPointerException if fileName is null
     */
    public static void printFile(String fileName) throws IOException, NullPointerException {
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Searches for a string in a text file and returns true if the string is found.
     *
     * @param fileName the name of the file to search
     * @param targetString the string to search for
     * @return true if the target string is found in the file, false otherwise
     * @throws IOException if an I/O error occurs while reading the file
     * @throws NullPointerException if fileName or targetString is null
     */
    public static boolean stringExistsInFile(String fileName, String targetString) throws IOException, NullPointerException {
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

    public static void main(String args[]) throws InterruptedException, IOException {
        testSchedulerTransmitterReceiveSendPackets();
    }
}
