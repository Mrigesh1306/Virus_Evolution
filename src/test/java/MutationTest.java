import Mutation.Mutation;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;

import static org.junit.Assert.*;

public class MutationTest {

    @Test
    public void calculateFitnessValue() throws IOException {
       Mutation m=new Mutation();
       int result=m.calculateGenotypeFitness("ABCDEFGHIJ",100);
       System.out.println(result);
       assertEquals(30180,result);
    }

    @Test
    public void insertNewMutation() throws IOException {
        Mutation m=new Mutation();
        m.insertIntoMutationList(4);
        assertEquals(true,true);
    }
    @Test
    public void fetchMutation() throws IOException {
        Mutation m=new Mutation();
        m.getMutationColor().put(4,new Color(179,133,29));
        Color c=m.fetchmutationColor(4);
        assertEquals(new Color(179,133,29),c);
    }

}
