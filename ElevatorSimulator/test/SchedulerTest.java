import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

//Mockito imports to isolate Elevator
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

/**
 * Tests for the Scheduler class.
 * 
 * @author Ethan Leir 101146422
 * @version 1.0
 */
public class SchedulerTest {
	private Scheduler scheduler;
	private Command command1;
	private Command command2;
	private Command command3;
	
	private ArrayList<Command> executedCommands;

	private Elevator elevatorMock;
	private ElevatorState elevatorStateMock;
	
	private final ByteArrayOutputStream commandlineOutput = new ByteArrayOutputStream();
	private final PrintStream originalOutput = System.out;
	
	/**
	 * Sets up the Scheduler object and its dependencies.
	 */
	@Before
	public void setupScheduler() {
		// Setup a Scheduler and two Commands to be used in the tests.
		this.scheduler = new Scheduler();
		this.command1 = new Command(5, 3, Direction.UP, 7);
		this.command2 = new Command(5, 2, Direction.DOWN, 1);
		this.command3 = new Command(5, 7, Direction.UP, 9);
		
		// Setup the buffer to store commands in the order they were given.
		this.executedCommands = new ArrayList<Command>();
		
		// Setup Mock Elevator and ElevatorState objects
		elevatorMock = mock(Elevator.class);
		scheduler.setElevator(elevatorMock);
		elevatorStateMock = mock(ElevatorState.class);
		
		// Stub Elevator.getState() so that it returns the mocked state.
		when(elevatorMock.getState()).thenReturn(this.elevatorStateMock);
		
		// Stub ElevatorState.isIdle() so that it always returns that the elevator is idle.
		when(elevatorStateMock.isIdleStatus()).thenReturn(true);
		
		// Stub Elevator.putCommand() so that it stores the command and
		// immediately updates the floor level.
		// We need a different syntax for any() params otherwise Mockito stops working.
		doAnswer(
				new Answer<Void>() {
					public Void answer(InvocationOnMock invocation) {
			             Object[] args = invocation.getArguments();
			             Object mock = invocation.getMock();
			             
			             if (args[0] instanceof Command) {
			            	 Command c = (Command) args[0];
			            	 int level = c.getFloor();
			            	 
			            	 executedCommands.add(c);
			            	 
			            	 elevatorStateMock.setFloorLevel(level);
			             }
			             return null;
					}
				}).when(elevatorMock).putCommand(any(Command.class));
		
		// Let the rest of the important elevatorStateMock methods invoke their real counterpart
		when(elevatorStateMock.getDirection()).thenCallRealMethod();
		doCallRealMethod().when(elevatorStateMock).setDirection(any(Direction.class));
		when(elevatorStateMock.getFloorLevel()).thenCallRealMethod();
		doCallRealMethod().when(elevatorStateMock).setFloorLevel(anyInt());
		
		// ElevatorState constructor was never called. Initialize manually.
		elevatorStateMock.setDirection(Direction.UP);
		elevatorStateMock.setFloorLevel(1);
		// Idle status unneeded.
	}
	
	/**
	 * Sets the System.out stream to one that can be tracked by the test.
	 */
	@Before
	public void setupStreams() {
		System.setOut(new PrintStream(commandlineOutput));
	}
	
	/**
	 * Sets the System.out stream back to the original.
	 */
	@After
	public void restoreStreams() {
	    System.setOut(originalOutput);
	}

	/**
	 * Test for the Scheduler.run() method.
	 * Places two commands, calls run, then checks if the commands were
	 * evaluated in the correct order, and that each ElevatorState.isIdleStatus()
	 * and Elevator.putCommand() were called the correct number of times.
	 */
	@Test
	public void testRun() {
		// Place the commands.
		ArrayList<Command> commandList = new ArrayList<Command>();
		commandList.add(command3);
		commandList.add(command2);
		commandList.add(command1);
		scheduler.placeCommandList(commandList);
		
		// Let the Scheduler know that it can exit as soon as the commands are processed.
		scheduler.exitThreads();
		
		// Execute tested function.
		scheduler.run();
		
		// Check the order of commands.
		Assert.assertEquals(command1, executedCommands.get(0));
		Assert.assertEquals(command3, executedCommands.get(1));
		Assert.assertEquals(command2, executedCommands.get(2));
		
		// Check that all immediately relevant functions were called the expected
		// number of times.
		verify(elevatorStateMock, times(3)).isIdleStatus();
		verify(elevatorMock, times(1)).putCommand(command1);
		verify(elevatorMock, times(1)).putCommand(command2);
		verify(elevatorMock, times(1)).putCommand(command3);
	}
	
	/**
	 * Test for the Scheduler.placeCommand() and Scheduler.getCommand methods.
	 * Asserts that the command returned from getCommand() is the same as the
	 * command passed to placeCommand().
	 * Asserts that the command line output is correct.
	 */
	@Test
	public void testPlaceGetCommand() {
		// Only test place->get because stubbing out wait would be
		// troublesome, and unit tests would rather not have concurrency.
		
		scheduler.placeCommand(command1);
		Command retCommand = scheduler.getCommand();
		
		Assert.assertEquals(command1, retCommand);
		
		String expected = "Scheduler has received the following command from the floor subsystem:\n"
				+ command1
				+ "\r\n"
				+ "Scheduler has passed the following command to the elevator :\n"
				+ command1;
		expected = expected.trim();
		String actual = commandlineOutput.toString().trim();

		Assert.assertEquals(expected, actual);
	}
	
	/**
	 * Test for the Scheduler.exitThreads() and Scheduler.ShouldExit methods.
	 * Asserts that shouldExit() returns false until a call to exitThreads(),
	 * after which it returns true.
	 */
	@Test
	public void testExitThreadsShouldExit() {
		Assert.assertEquals(false, scheduler.shouldExit());
		scheduler.exitThreads();
		Assert.assertEquals(true, scheduler.shouldExit());
	}
	
}
