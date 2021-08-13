package Person;

import Island.Island;
import Mutation.Mutation;
import org.ini4j.Ini;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.*;

/**
 * @author sayali mahajan
 */
public class PersonDirectory {

    private static PersonDirectory personDirectory;
    private List<Person> PersonList = new ArrayList<Person>();
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
        Island island = new Island(300, 300);
        for (int i = 0; i < Integer.parseInt(map.get("human_population")); i += 4) {
            Random random = new Random();
            int x = (int) (100 * random.nextGaussian() + island.getCenterX());
            int y = (int) (100 * random.nextGaussian() + island.getCenterY());
            if (x > 700) x = 700;
            PersonList.add(new Person(x, y, island, "A1"));
            x = (int) (100 * random.nextGaussian() + island.getCenterX());
            y = (int) (100 * random.nextGaussian() + island.getCenterY());
            if (x > 700) x = 700;
            PersonList.add(new Person(x, y, island, "A2"));
            x = (int) (100 * random.nextGaussian() + island.getCenterX());
            y = (int) (100 * random.nextGaussian() + island.getCenterY());
            if (x > 700) x = 700;
            PersonList.add(new Person(x, y, island, "B1"));
            x = (int) (100 * random.nextGaussian() + island.getCenterX());
            y = (int) (100 * random.nextGaussian() + island.getCenterY());
            if (x > 700) x = 700;
            PersonList.add(new Person(x, y, island, "B2"));
        }
    }

    public List<Person> getPersonList() {
        return PersonList;
    }

    public void setPersonList(List<Person> personList) {
        PersonList = personList;
    }

    public List<Person> getInfectedList() {
        List<Person> infectedList = new ArrayList<>();
        for (Person p : PersonList) {
            if (p.isInfected() && !p.isDead())
                infectedList.add(p);
        }
        return infectedList;
    }

    public List<Person> getCurrentInfectedList(int currMutationCount) {
        List<Person> currentInfectedList = new ArrayList<>();
        for (Person p : PersonList) {
            if (p.isInfected() && p.getMutation_count() == currMutationCount && !p.isDead())
                currentInfectedList.add(p);
        }

//        if(currentInfectedList.size()==0)
//        {
//            Random r=new Random();
//            currentInfectedList.add(PersonList.get(r.nextInt(PersonList.size())));
//        }
        return currentInfectedList;
    }

    public List<Person> getCurrentNonInfectedList() {
        List<Person> currentNonInfectedList = new ArrayList<>();
        for (Person p : PersonList) {
            if (!p.isInfected() && (p.getInfection_Status().equals("Naive") || p.getInfection_Status().equals("Recovered") || p.getInfection_Status().equals("Vaccinated")) && !p.isDead())
                currentNonInfectedList.add(p);
        }
        return currentNonInfectedList;
    }

    public List<Person> getCurrentNonInfectedAndNonVaccinatedList() {
        List<Person> currentNonInfectedList = new ArrayList<>();
        for (Person p : PersonList) {
            if (!p.isInfected() && (p.getInfection_Status().equals("Naive") || p.getInfection_Status().equals("Recovered")) && !p.isDead() && !p.getInfection_Status().equals("Vaccinated"))
                currentNonInfectedList.add(p);
        }
        return currentNonInfectedList;
    }

    public int getPersonMutationCount() {
        List<Person> person = new ArrayList<>();
        HashMap<Integer, Color> mutationColor = Mutation.mutationColor;
        int mutation = 0;
        for (Integer i : mutationColor.keySet()) {

            for (Person p : PersonList) {

                if (i.equals(p.mutation_count)) {
                    mutation++;
                }
            }
        }
        System.out.println("mutation " + mutation);
        return mutation;
    }


    public int getInfectedCount(int currMutationCount) {
        int currentInfectedCount = 0;
        for (Person p : PersonList) {
            //if(p.isInfected() && p.getMutation_count()== currMutationCount && !p.isDead())
            if (p.isInfected() && !p.isDead())
                currentInfectedCount++;
        }

        return currentInfectedCount;
    }

    public int getRecoveredCount() {
        int currentNonInfectedCount = 0;
        for (Person p : PersonList) {
            if (!p.isInfected() && (p.getInfection_Status().equals("Recovered")) && !p.isDead())
                currentNonInfectedCount++;
        }
        return currentNonInfectedCount;

    }

    public int getVaccinatedCount() {
        int currentVaccinatedCount = 0;
        for (Person p : PersonList) {
            if (p.isVaccinated && !p.isInfected() && !p.isDead())
                currentVaccinatedCount++;
        }
        return currentVaccinatedCount;

    }

    public int getNaiveCount() {
        int currentVaccinatedCount = 0;
        for (Person p : PersonList) {
            if (!p.isInfected() && p.getInfection_Status().equals("Naive") && !p.isDead())
                currentVaccinatedCount++;
        }
        return currentVaccinatedCount;

    }

    public int getInfectedCount() {
        int currentInfectedCount = 0;
        for (Person p : PersonList) {
            if (p.isInfected() && !p.isDead())
                currentInfectedCount++;
        }
        return currentInfectedCount;

    }

    public int getDeadCount() {
        int currentDeadCount = 0;
        for (Person p : PersonList) {
            if (p.isDead())
                currentDeadCount++;
        }
        return currentDeadCount;

    }
}
