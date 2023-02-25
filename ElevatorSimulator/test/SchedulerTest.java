import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.junit.Assert;

/**
 * Tests for the Scheduler class.
 * 
 * @author Ethan Leir 101146422
 * @version 1.0
 */
public class SchedulerTest {
	private Scheduler scheduler;
	private Command command;
	
	private final ByteArrayOutputStream commandlineOutput = new ByteArrayOutputStream();
	private final PrintStream originalOutput = System.out;
	
	/**
	 * Sets up the Scheduler object and its dependencies.
	 */
	@Before
	public void setupScheduler() {
		this.scheduler = new Scheduler();
		this.command = new Command(5, 2, Direction.UP, 9);
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
	 * Currently does nothing.
	 */
	@Test
	public void testRun() {
		//Nothing to do.
	}
	
	/**
	 * Test for the Scheduler.placeServicedCommand() and Scheduler.getServicedCommand
	 * methods.
	 * Asserts that the command returned from getServicedCommand() is the same as the
	 * command passed to placeServicedCommand().
	 * Asserts that the command line output is correct.
	 */
	@Test
	public void testPlaceGetServicedCommand() {
		// Only test place->get because stubbing out wait would be
		// troublesome, and unit tests would rather not have concurrency.
		
		scheduler.placeServicedCommand(command);
		Command retCommand = scheduler.getServicedCommand();
		
		Assert.assertEquals(command, retCommand);
		
		String expected = "Elevator has finished servicing the following command:\n"
				+ command
				+ "\r\n"
				+ "Floor subsystem has been notified that the following command has been serviced:\n"
				+ command;
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
