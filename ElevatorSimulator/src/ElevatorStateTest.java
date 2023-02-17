import static org.junit.Assert.*;

/**
 * Unit tests for the ElevatorState class. The constructor, getter methods, and setter methods
 * are tests in this class.
 */
public class ElevatorStateTest {

    @org.junit.Test
    public void testConstructorandGetters() {
        ElevatorState state = new ElevatorState();
        assertEquals(1, state.getFloorLevel());
        assertTrue(state.isIdleStatus());
        assertEquals(Direction.UP , state.getDirection());
    }



    @org.junit.Test
    public void setDirection() {
        ElevatorState state = new ElevatorState();
        state.setDirection(Direction.DOWN);
        assertEquals(Direction.DOWN , state.getDirection());
    }

    @org.junit.Test
    public void setFloorLevel() {
        ElevatorState state = new ElevatorState();
        state.setFloorLevel(4);
        assertEquals(4 , state.getFloorLevel());
    }

    @org.junit.Test
    public void setIdleStatus() {
        ElevatorState state = new ElevatorState();
        state.setIdleStatus(false);
        assertFalse(state.isIdleStatus());
    }
}