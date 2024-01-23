# Real-Time Elevator Simulator System

## Overview

Welcome to the Real-Time Elevator Simulator System project! This collaborative effort involves the design and implementation of a comprehensive system, including an elevator controller (the Scheduler), an elevator car simulator (featuring lights, buttons, doors, and motors), and a floors simulator (complete with buttons, lights, and individuals opting for elevators over stairs).

## Key Features

- **Multi-threaded Scheduler:** The elevator controller is designed to be multi-threaded, effectively managing multiple elevator cars concurrently.

- **Configurability:** Customize your simulation based on project requirements. Adjust the number of floors, elevators, door operation times, and travel durations between floors.

- **Distributed Simulation:** As part of the project's scope, the simulation will eventually be distributed across multiple computers. The controller will run independently on one machine, while the simulator(s) operate on a separate computer.

## General Use Instructions

The `File Iteration1_Group5` is packaged in a ZIP format containing the following contents:

- README.txt
- UML Class Diagram
- UML Sequence Diagram
- UML State Diagram
- Test Instructions (.txt format)
- Code (Product) Packaged in a Zip Format

---

## Group Responsibilities - Iteration 1

- **Hasan:** Command Class, Partial Contribution To Scheduler, README.txt
- **Daniah:** Floor Class + Sequence Diagram
- **Ali:** Elevator Class + Reviewing Command Class / Edit Command Class
- **Ethan:** Junit Testing, Review Pull Requests (Quality Check), Modify/Help With Floor Class (If Needed)
- **Mohammed:** Programmed the Scheduler Class

**UML Class Diagram:** Full Group Contribution

---

## Group Responsibilities - Iteration 2

- **Hasan:** `findBestCommand()` method, reviewed Scheduler `run()`, README.txt
- **Ali:** Elevator Class and JUnit tests
- **Daniah:** Elevator Class and JUnit tests, sequence diagram
- **Mohammed:** ElevatorState Class, ElevatorStateTest, updated Floor Class
- **Ethan:** Scheduler `run()`, JUnit tests, fixed bugs, and helped others complete tasks

**Pairs:**
- Pair #1: Ali, Daniah
- Pair #2: Ethan, Hasan
- Pair #3: Mohammed

---

## Group Responsibilities - Iteration 3

- **Hasan:** SchedulerReceiver Thread, SchedulerReceiverTest
- **Ali:** Elevator Thread, ElevatorTest (run() method)
- **Daniah:** Elevator Thread
- **Mohammed:** SchedulerTransmitter, SchedulerTransmitterTest
- **Ethan:** Floor, Helped With Elevator and Elevator Testing, Fix Bugs in All classes, helped with SchedulerReceiver Testing

**Pairs:**
- Pair #1: Hasan, Mohammed
- Pair #2: Ethan
- Pair #3: Ali, Daniah

---

## Group Responsibilities - Iteration 4 (Current Iteration)

- **Hasan:** SchedulerTransmitterTest, UML, Review Requests, Participated In Acceptance Testing, Timing Diagram
- **Ali:** Floor, FloorTest, Participated In Acceptance Testing, UML
- **Daniah:** Floor, Lead Acceptance Testing
- **Mohammed:** Elevator, Scheduler, Floor, Marshalling, Regression Testing, Code Refactoring, Timing Diagram [Played Along All Classes]
- **Ethan:** Elevator, Scheduler, Floor, Marshalling, Regression Testing, Code Refactoring [Played Along All Classes]

**Pairs:**
- Pair #1: Hasan, Ali, Daniah
- Pair #2: Ethan, Mohammed

---

## Test Setup / Instructions

1. Click on **File** in Eclipse.
2. Click **Import**.
3. Select **Existing Projects into Workspace** under the **General** folder.
4. Select archive file and use the browse button to select the downloaded zipped file.
5. Select **ElevatorSim** under the **Projects** section (If it doesn't show, try clicking the **Refresh** button).
6. Click **Finish**.
7. Right-click on **main** under the **src/main/java**, and run **Simulator.java** under the default package as a Java application.
8. Right-click on **test** under the **src** folder, click **Run As**, and configure to use JUnit by double-clicking on **JUnit** on the left sidebar.
9. You can also run tests by navigating to **src/test/java**, right-clicking the test files, and running them using **JUnit 4**.
10. The source files can be found in **src/main/java**, and the test files are under **src/test/java**.
