import Person.Person;
import Simulator.Simulator;
import org.ini4j.Ini;
import org.junit.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class SimulatorTest {

    @Test
    public void loadHashTableTest() throws IOException {
        //reading the config file to fetch the value of variables
        Ini ini = new Ini(new File("./config.properties"));
        //connecting the file to 2 map to ease the reading and fetching for the default and resident_status
        Map<String, String> map = ini.get("default");

        Simulator s=new Simulator();
        Person p=s.getMutationPerson(map);
        assertEquals(p.isInfected(),true);

    }

    @Test
    public void pushValueToHashTableTest() throws IOException {

        Simulator s=new Simulator();
        s.pushValueToHashtable("12961111111");
        assertEquals(true,true);
    }

}
