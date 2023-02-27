// Reading input file.
import java.io.File;

// The usual junit imports
import org.junit.Before;
import org.junit.Test;

// Mockito imports to isolate Elevator
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;

/**
 * Tests for the Floor class.
 * 
 * @author Ethan Leir 101146422
 * @version 1.0
 */
@ExtendWith(MockitoExtension.class)
public class FloorTest {
    private Floor floor;
    private Command command;
    private Scheduler schedulerMock;

    @Before
    public void setupMocks() {
        File file = new File("singleCommand.txt");
        this.command = new Command("10 4 down 2");
        schedulerMock = mock(Scheduler.class);

        this.floor = new Floor(schedulerMock, file);
    }

    /**
     * Test for the Floor.run() method.
     * Asserts that the correct message is printed.
     * Asserts that Scheduler.exitThreads() was called the correct
     * number of times when only one command is provided.
     */
    @Test
    public void testRun() {
        floor.run();

        //See if scheduler.exitThreads was called.
        verify(schedulerMock, times(1)).exitThreads();
    }

}
