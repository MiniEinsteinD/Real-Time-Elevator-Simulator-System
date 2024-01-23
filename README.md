# Real-Time-Elevator-Simulator-System

General Use Instructions:


The File Iteration1_Group5 Is Packaged In a ZIP Format Containing The Following Contents:

-README.txt
-UML Class Diagram
-UML Sequence Diagram
-UML State Diagram
-Test Instructions (.txt format) 
-Code (Product) Packaged In a Zip Format

------------------------------------------------------------------------------------------------------------
Group Responsibilities Iteration 1:

Hasan: Command Class, Partial Contribution To Scheduler, README.txt
Daniah: Floor Class + Sequence Diagram
Ali: Elevator Class + Reviewing Command Class / Edit Command Class
Ethan: Junit Testing, Review Pull Requests (Quality Check), Modify/Help With Floor Class (If Needed)
Mohammed: Programmed the Scheduler Class

UML Class Diagram: Full Group Contribution

-------------------------------------------------------------------------------------------------------------
Group Responsibilities Iteration 2:

Hasan: findBestCommand() method, reviewed Scheduler run(), README.txt (getCommand() and putCommand() methods reworked completely by Ali, Daniah)
Ali: Elevator Class and JUnit tests
Daniah: Elevator Class and JUnit tests, sequence diagram
Mohammed: ElevatorState Class, ElevatorStateTest, updated Floor Class
Ethan: Scheduler run(), JUnit tests, fixed bugs and helped others complete tasks

Pair #1: Ali, Daniah
Pair #2: Ethan, Hasan 
Pair #3: Mohammed

ALL contributed towards state machine diagrams, UML diagrams
--------------------------------------------------------------------------------------------------------------
Group Responsibilities Iteration 3:

Hasan: SchedulerReceiver Thread, SchedulerReceiverTest
Ali: Elevator Thread, ElevatorTest (run() method)
Daniah: Elevator Thread
Mohammed: SchedulerTransmitter, SchedulerTransmitterTest
Ethan: Floor, Helped With Elevator and Elevator Testing, Fix Bugs in All classes, helped with SchedulerReceiver Testing

Pair #1: Hasan, Mohammed
Pair #2: Ethan
Pair #3: Ali, Daniah

All contributed towards UML class diagram, Sequence Diagram(Ethan, Daniah, Mohammed), ScheduleTransmitter State Machine(Mohammed), SchedulerReceiver State Mechine (Hasan), Elevator State Machine (Ali).
---------------------------------------------------------------------------------------------------------------

Group Responsibilities Iteration 4 (Current Iteration):

Hasan: SchedulerTransmitterTest, UML, Review Requests, Participated In Acceptance Testing, Timing Diagram
Ali: Floor, FloorTest, Participated In Acceptance Testing, UML
Daniah: Floor, Lead Acceptance Testing
Mohammed: Elevator, Scheduler, Floor, Marshalling, Regression Testing, Code Refactoring, Timing Diagram [Played Along All Classes]
Ethan: Elevator, Scheduler, Floor, Marshalling, Regression Testing, Code Refactoring [Played Along All Classes]

Pair #1: Hasan, Ali, Daniah
Pair #2: Ethan, Mohammed


---------------------------------------------------------------------------------------------------------------

Test Setup / Instructions: 

1. Click File on eclipse
2. Click import
3. Select Existing Projects into Workspace under the General folder
4. Select archive file and use browse button to select the downloaded zipped file
5. Select ElevatorSim under the Projects section (If it doesnt show try clicking Refresh button)
6. Click Finish
7. Right click on main under the src/main/java and right click on Simulator.java under the default package and run as java application.
8. Right click on test under the src folder, click run as, and configure to use JUnit by double clicking on JUnit on the left sidebar.
9. You can also run tests by navigating to src/test/java, and right clicking the test files and running them using JUnit 4.
10. The source files can be found in src/main/java and the test files are under src/test/java
