import Island.Island;
import Person.Position;
import org.junit.Test;
import static org.junit.Assert.*;

public class PositionTest {

    @Test
    public void getpositionLocationTest() {
        Position p = new Position(300, 400);
        assertEquals(300, p.getX());
        assertEquals(400, p.getY());

    }

    @Test
    public void rePositionTest() {
        Position p = new Position(300, 400);
        p.rePosition(200,300);
        assertEquals(500, p.getX());
        assertEquals(700, p.getY());
    }

}