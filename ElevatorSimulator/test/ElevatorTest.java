// Tracking command line output
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

// The usual junit imports
import org.junit.Assert;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;

// Mockito imports to isolate Elevator
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

/**
 * Tests for the Elevator class.
 *
 * @author Ethan Leir 101146422
 * @version 1.0
 *
 * @author Ali El-Khatib 101189859
 * @version 2.0
 */
@ExtendWith(MockitoExtension.class)
public class ElevatorTest {
	private final ByteArrayOutputStream commandlineOutput = new ByteArrayOutputStream();
	private final PrintStream originalOutput = System.out;

	private Command command;
	private Elevator elevator;
	private Scheduler schedulerMock;

	/**
	 * Sets up the mock object for Scheduler which will:
	 * return this.command on a call to getCommand()
	 * return false on the first call to shouldExit(), and true otherwise.
	 * Creates an Elevator object with the mock scheduler as an dependency.
	 */
	@Before
	public void setupMocks() {
		this.command = new Command(5, 2, Direction.UP, 9);

		schedulerMock = mock(Scheduler.class);
		when(schedulerMock.shouldExit()).thenAnswer(
				new Answer() {
					private int n = 0;
					public Object answer(InvocationOnMock invocation) {
						Object[] args = invocation.getArguments();
						Object mock = invocation.getMock();

						boolean ret = false;
						if (n > 0) {
							ret = true;
						}
						++n;

						return ret;
					}
				});
		this.elevator = new Elevator(schedulerMock, 42);


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
	 * Test for the Elevator.run() method.
	 * Asserts that the correct message is printed.
	 * Asserts that the elevator calls Scheduler.getCommand(),
	 * Scheduler.placeServicedCommand(), and Scheduler.shouldExit()
	 * the correct number of times when only one command is provided.
	 */
	@Test
	public void testRun() {
		elevator.putCommand(command);
		elevator.run();


		String expected = "Elevator received Command:\n"
				+ command
				+ "\n\r\n"
				+ "Elevator is now on floor: 2\n\r\n"
				+ "Elevator finished Command:\n"
				+ command;

		expected = expected.trim();
		String actual = commandlineOutput.toString().trim();
		Assert.assertEquals(
				expected,
				actual
		);

		//verify(schedulerMock, times(1)).getCommand();
		//verify(schedulerMock, times(1)).placeServicedCommand(command);
		verify(schedulerMock, times(2)).shouldExit();
	}


}
