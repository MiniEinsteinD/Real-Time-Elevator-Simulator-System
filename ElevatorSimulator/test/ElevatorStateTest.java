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
        /* Tests that the constructor works fine */
        assertEquals(1, state.getFloorLevel());
        assertTrue(state.isIdleStatus());
        assertEquals(Direction.UP , state.getDirection());
    }

    @org.junit.Test
    public void setDirection() {
        state.setDirection(Direction.DOWN); //direction is changed from the default UP to DOWN
        assertEquals(Direction.DOWN , state.getDirection());
    }

    @org.junit.Test
    public void setFloorLevel() {
        state.setFloorLevel(4); //floor level is changed from the default 1 to 4
        assertEquals(4 , state.getFloorLevel());
    }

    @org.junit.Test
    public void setIdleStatus() {
        state.setIdleStatus(false); //the idle status is changed from the default false to true
        assertFalse(state.isIdleStatus());
    }
}