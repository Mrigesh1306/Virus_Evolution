package Person;

import Island.Island;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class PersonDirectory {

    private static PersonDirectory personDirectory;
    private List<Person> PersonList=new ArrayList<Person>();
    Ini ini = new Ini(new File("./config.properties"));
    Map<String, String> host_type = ini.get("host_type");
    Map<String, String> map = ini.get("default");
    static {
        try {
            personDirectory = new PersonDirectory();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PersonDirectory getInstance() {
        return personDirectory;
    }

    public PersonDirectory() throws IOException {
        Island island = new Island(350, 350);
       // personDirectory=new PersonDirectory();
        for (int i = 0; i < Integer.parseInt(map.get("human_population")); i+=4) {
            Random random = new Random();
            int x = (int) (100 * random.nextGaussian() + island.getCenterX());
            int y = (int) (100 * random.nextGaussian() + island.getCenterY());
            if (x > 700) x = 700;
            PersonList.add(new Person(x, y,island,"A1"));
             x = (int) (100 * random.nextGaussian() + island.getCenterX());
             y = (int) (100 * random.nextGaussian() + island.getCenterY());
            if (x > 700) x = 700;
            PersonList.add(new Person(x, y,island,"A2"));
             x = (int) (100 * random.nextGaussian() + island.getCenterX());
             y = (int) (100 * random.nextGaussian() + island.getCenterY());
            if (x > 700) x = 700;
            PersonList.add(new Person(x, y,island,"B1"));
             x = (int) (100 * random.nextGaussian() + island.getCenterX());
             y = (int) (100 * random.nextGaussian() + island.getCenterY());
            if (x > 700) x = 700;
            PersonList.add(new Person(x, y,island,"B2"));
        }
    }

    public List<Person> getPersonList() {
        return PersonList;
    }

    public void setPersonList(List<Person> personList) {
        PersonList = personList;
    }

    public List<Person> getInfectedList(){
    List<Person> infectedList=new ArrayList<Person>();
    for(Person p : PersonList)
        {
            if(p.isInfected())
                infectedList.add(p);
        }
    return infectedList;
    }

    public List<Person> getCurrentInfectedList(int currMutationCount)
    {
        List<Person> currentInfectedList=new ArrayList<Person>();
        for(Person p : PersonList)
        {
            if(p.isInfected() && p.getMutation_count()== currMutationCount)
                currentInfectedList.add(p);
        }

        if(currentInfectedList.size()==0)
        {
            Random r=new Random();
            currentInfectedList.add(PersonList.get(r.nextInt(PersonList.size())));
        }
        return currentInfectedList;
    }

    public List<Person> getCurrentNonInfectedList()
    {
        List<Person> currentNonInfectedList=new ArrayList<Person>();
        for(Person p : PersonList)
        {
            if(!p.isInfected() && (p.getInfection_Status().equals("Naive") || p.getInfection_Status().equals("Recovered")))
                currentNonInfectedList.add(p);
        }
        return currentNonInfectedList;
    }

}
