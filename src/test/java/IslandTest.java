import Island.Island;
import org.junit.Test;
import static org.junit.Assert.*;

public class IslandTest {

    @Test
    public void testIslandGetCenter()
    {
        Island i = new Island(300,300);
        assertEquals(300,i.getCenterX());
    }

    @Test
    public void testIslandSetCenter()
    {
        Island i = new Island(300,300);
        i.setCenterX(200);
        i.setCenterY(400);
        assertEquals(200,i.getCenterX());
        assertEquals(400,i.getCenterY());
    }

}
