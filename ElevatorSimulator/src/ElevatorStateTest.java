import org.junit.Before;

import static org.junit.Assert.*;

/**
 * Unit tests for the ElevatorState class. The constructor, getter methods, and setter methods
 * are tests in this class.
 */
public class ElevatorStateTest {

    ElevatorState state;

    @Before
    public void setup() {
        state = new ElevatorState();
    }

    @org.junit.Test
    public void testConstructorandGetters() {
        assertEquals(1, state.getFloorLevel());
        assertTrue(state.isIdleStatus());
        assertEquals(Direction.UP , state.getDirection());
    }

    @org.junit.Test
    public void setDirection() {
        state.setDirection(Direction.DOWN);
        assertEquals(Direction.DOWN , state.getDirection());
    }

    @org.junit.Test
    public void setFloorLevel() {
        state.setFloorLevel(4);
        assertEquals(4 , state.getFloorLevel());
    }

    @org.junit.Test
    public void setIdleStatus() {
        state.setIdleStatus(false);
        assertFalse(state.isIdleStatus());
    }
}