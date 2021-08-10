import Person.Position;
import Person.RandomWalk;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class RandomWalkTest {

    @Test
    public void getpositionLocationTest() {
        RandomWalk r = new RandomWalk(300, 400);
        assertEquals(300, r.getxAxis());
        assertEquals(400, r.getyAxis());

    }


}