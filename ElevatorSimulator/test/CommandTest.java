import org.junit.Assert;

import org.junit.Before;
import org.junit.Test;

/**
 * Tests for the Command class.
 * 
 * @author Ethan Leir 101146422
 * @version 1.0
 */
public class CommandTest {
	Command commandParams;
	Command commandString;
	
	/**
	 * Sets up the Command objects.
	 */
	@Before
	public void setup() {
		this.commandParams = new Command(5, 2, Direction.UP, 9);
		this.commandString = new Command("10 4 down 2");
	}
	
	/**
	 * Test for the Command.getTime() method.
	 */
	@Test
	public void testGetTime() {
		Assert.assertEquals(5, this.commandParams.getTime());
		Assert.assertEquals(10, this.commandString.getTime());
	}

	/**
	 * Test for the Command.getFloor() method.
	 */
	@Test
	public void testGetFloor() {
		Assert.assertEquals(2, this.commandParams.getFloor());
		Assert.assertEquals(4, this.commandString.getFloor());
	}

	/**
	 * Test for the Command.getDirectionButton() method.
	 */
	@Test
	public void testGetDirectionButton() {
		Assert.assertEquals(Direction.UP, this.commandParams.getDirectionButton());
		Assert.assertEquals(Direction.DOWN, this.commandString.getDirectionButton());
	}

	/**
	 * Test for the Command.getElevatorButton() method.
	 */
	@Test
	public void testGetElevatorButton() {
		Assert.assertEquals(9, this.commandParams.getElevatorButton());
		Assert.assertEquals(2, this.commandString.getElevatorButton());
	}

	/**
	 * Test for the Command.toString() method.
	 */
	@Test
	public void testToString() {
		Assert.assertEquals(
				"Command Composed Of:\n"
				+ "Time: 5\n"
				+ "Floor: 2\n"
				+ "Elevator Button Pressed: 9\n"
				+ "Direction: UP\n",
				this.commandParams.toString()
				);
		Assert.assertEquals(
				"Command Composed Of:\n"
				+ "Time: 10\n"
				+ "Floor: 4\n"
				+ "Elevator Button Pressed: 2\n"
				+ "Direction: DOWN\n",
				this.commandString.toString()
				);
	}

}
