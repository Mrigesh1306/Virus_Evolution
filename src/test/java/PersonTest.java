import Island.Island;
import Person.Person;
import Person.PersonDirectory;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.*;

public class PersonTest {

    @Test
    public void getPersonListTest() throws IOException {
        PersonDirectory pd=new PersonDirectory();
        Person p=new Person(100,100,new Island(300,300),"A1");
        pd.getPersonList().add(p);
        Person result=pd.getPersonList().get(0);
        assertEquals(p.getHuman_genome(),result.getHuman_genome());
    }

    @Test
    public void getCurrentInfectedListTest() throws IOException {
        PersonDirectory pd=new PersonDirectory();
        Person p=new Person(100,100,new Island(300,300),"A1");
        p.setMutation_count(2);
        p.setInfected(true);
        pd.getPersonList().add(p);
        Person result=pd.getCurrentInfectedList(2).get(0);
        assertEquals(p.getMutation_count(),result.getMutation_count());
    }

    @Test
    public void getCurrentNonInfectedListTest() throws IOException {
        PersonDirectory pd=new PersonDirectory();
        Person p=new Person(100,100,new Island(300,300),"A1");
        pd.getPersonList().add(p);
        Person result=pd.getCurrentNonInfectedList().get(0);
        assertEquals(p.isInfected(),result.isInfected());
    }




}
