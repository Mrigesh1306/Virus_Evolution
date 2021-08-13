package Simulator;

import Mutation.Mutation;
import Person.Person;
import Person.PersonDirectory;
import Virus.VirusStrainMap;
import org.ini4j.Ini;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Timer;
import java.util.*;

public class Simulator extends JPanel implements Runnable {

    //read the config file for configurable valuesu
    Ini ini = new Ini(new File("./config.properties"));
    Map<String, String> map = ini.get("default");
    Color currentColor = new Color(255, 255, 255);

    Person mutationPerson;
    String newGT = "";
    boolean newVariantFlag = true;

    int spreadCountPercentage = Integer.parseInt(map.get("spread_count"));
    int humanPopulation = Integer.parseInt(map.get("human_population"));
    int numberOfDays = Integer.parseInt(map.get("days"));
    int spreadDays = Integer.parseInt(map.get("spread_days"));
    int MaxMutation = Integer.parseInt(map.get("mutation"));
    float vaccineRate = Float.parseFloat(map.get("vaccination_rate"));
    int recoveryDays = Integer.parseInt(map.get("recovery_days"));
    int dailyVaccination = (int) (vaccineRate * humanPopulation);

    int presentDay = 1;
    int eachSpreadDay = 0;

    public Mutation mutation = new Mutation();
    public static Hashtable<String, List<String>> fitnessHashTable = new Hashtable<>();
    public static Map<Character, Integer> mutationMap = new HashMap<>();


    public Timer timer = new Timer();

    //constructor
    public Simulator() throws IOException {
        super();
        this.setBackground(new Color(245, 214, 167));
    }

    @Override
    public void paint(Graphics graphics) {

        super.paint(graphics);
        List<Person> people = PersonDirectory.getInstance().getPersonList();
        for (Person person : people) {

            if (person.isInfected()) {
                graphics.setColor(person.getMutationColor());
            } else if (!person.isInfected()) {

                switch (person.getInfection_Status()) {
                    case "Naive": {
                        graphics.setColor(new Color(255, 255, 255));
                        break;
                    }
                    case "Recovered": {
                        graphics.setColor(new Color(177, 177, 177));
                        break;
                    }
                    case "Vaccinated": {
                        graphics.setColor(new Color(90, 255, 0));
                        break;
                    }
                }
            }
            person.checkHealth();
            graphics.fillOval(person.getX(), person.getY(), 10, 10);
        }
        try {

            startEvolution();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void startEvolution() throws IOException, InterruptedException {

        //number of days of simulation
        if (presentDay % spreadDays == 0) {

            //2. calculate new genotype
            newGT = calculateNewGenotype();

            //put new 12 mutation fitness values in hashtable
            pushValueToHashtable(newGT);

            mutationPerson = getMutationPerson(map);
            //set this after adding all new values in fitness table
            mutationPerson.setMutation_count(fitnessHashTable.size());

            //3. fitness values of genotype of one selected person (host wise)
            double mutationFactor = InfectionFactor(mutationPerson.getHuman_genome(), mutationPerson.getInfection_Status(), mutationPerson);
            double mutationValue = mutation.calculateGenotypeFitness(newGT, mutationFactor);

            //check if value is above variant threshold
            newVariantFlag = mutation.calculateMutationFactor(newGT, mutationValue, fitnessHashTable);
            if (newVariantFlag) {
                currentColor = mutation.getMutationColor().get(mutationPerson.getMutation_count());
            }

            System.out.println("Mutation " + fitnessHashTable.size() + " : " + newGT + " is Variant : " + newVariantFlag);
        }
        //5.inner loop for spread

        //infect people loop - daily
        List<Person> currNonInfectedList = PersonDirectory.getInstance().getCurrentNonInfectedList();
        Random randomSpread = new Random();
        int loopCount = randomSpread.nextInt((spreadCountPercentage * humanPopulation / 100));
        for (int k = 0; k < loopCount; k++) {
            Random r = new Random();
            //update noninfected list
            currNonInfectedList = PersonDirectory.getInstance().getCurrentNonInfectedList();
            Person currPerson = currNonInfectedList.get(r.nextInt(currNonInfectedList.size()));
            currPerson.setInfection_count(currPerson.getInfection_count() + 1);
            currPerson.setMutation_count(mutationPerson.getMutation_count());
            if (!currPerson.isVaccinated())
                currPerson.setRecovery_day(recoveryDays);
            else
                currPerson.setRecovery_day(recoveryDays / 2);
            currPerson.setInfected(true);
            currPerson.setMutationColor(currentColor);
        }

        //update all person's properties including recovery_days and status.
        for (Person p : PersonDirectory.getInstance().getPersonList()) {
            if (p.getRecovery_day() == 0 && p.isInfected()) {
                if (p.isVaccinated()) {
                    p.setInfected(false);
                    p.setInfection_Status("Vaccinated");
                    p.setMutationColor(new Color(90, 255, 0));
                } else {
                    if (p.getHuman_genome().equals("B2")) {
                        p.setDead(true);
                        p.setMutationColor(new Color(0, 0, 0));
                    }
                    p.setInfected(false);
                    p.setInfection_Status("Recovered");
                    p.setMutationColor(new Color(177, 177, 177));
                }
            }
            if (p.isInfected()) {
                p.setRecovery_day(p.getRecovery_day() - 1);
            }
            //check for person is dead or not
            if (p.infection_count >= 25) {
                p.setDead(true);
                p.setMutationColor(new Color(0, 0, 0));
            }

        }

        //vaccination logic

        if (presentDay > (numberOfDays / 2)) {
            for (int i = 1; i <= dailyVaccination; i++) {
                List<Person> currentNonPersonList = PersonDirectory.getInstance().getCurrentNonInfectedAndNonVaccinatedList();
                Random random = new Random();
                int number = random.nextInt(currentNonPersonList.size());
                Person p = currentNonPersonList.get(number);
                p.setInfection_Status("Vaccinated");
                p.setMutationColor(new Color(90, 255, 0));
                p.setVaccinated(true);


            }
        }
        //break the paint() method
        if (presentDay < numberOfDays) {
            Thread.sleep(500);
            System.out.println("Day : " + presentDay + " | Naive : " + PersonDirectory.getInstance().getNaiveCount() + " | Infected : " + PersonDirectory.getInstance().getInfectedCount() + " | Recovered : " + PersonDirectory.getInstance().getRecoveredCount() + " | Vaccinated : " + PersonDirectory.getInstance().getVaccinatedCount() + " | Dead : " + PersonDirectory.getInstance().getDeadCount());
            presentDay++;
            repaint();
        } else
            System.exit(0);


    }

    public void pushValueToHashtable(String newGT) throws IOException {
        List<Integer> UFactorPerMutation = MutationFactor();
        List<String> hostFitnessValues = new ArrayList<>();
        for (Integer i : UFactorPerMutation) {
            double eachFitnessValue = mutation.calculateGenotypeFitness(newGT, i);
            hostFitnessValues.add(String.valueOf(eachFitnessValue));
        }
        fitnessHashTable.put(newGT, hostFitnessValues);
    }

    public void loadHashTable() throws IOException {
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> fitnesMap = ini.get("fitness_Value");
        List<String> fitnessVal = Arrays.asList(fitnesMap.entrySet().iterator().next().getValue().split(","));
        newGT = fitnesMap.entrySet().iterator().next().getKey();
        fitnessHashTable.put(fitnesMap.entrySet().iterator().next().getKey(), fitnessVal);
    }

    public void generateMutationMap() throws IOException {
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> mutationMapTMp = ini.get("gene_length");
        for (Map.Entry<String, String> entry : mutationMapTMp.entrySet())
            mutationMap.put(entry.getKey().charAt(0), ((Integer.parseInt(entry.getValue()) * MaxMutation) / humanPopulation) == 0 ? 1 : ((Integer.parseInt(entry.getValue()) * MaxMutation) / humanPopulation));
    }

    @NotNull
    public Person getMutationPerson(Map<String, String> map) {
        //1. random person selection - infected
        List<Person> currInfectedList = PersonDirectory.getInstance().getCurrentInfectedList(fitnessHashTable.size());
        Random random = new Random();
        if (currInfectedList.size() == 0)
            mutationPerson = PersonDirectory.getInstance().getPersonList().get(random.nextInt(PersonDirectory.getInstance().getPersonList().size()));
        else
            mutationPerson = currInfectedList.get(random.nextInt(currInfectedList.size()));
        //Person mutationPerson=currInfectedList.get(randomPerson);
        //change properties of person.
        //mutaion count hashtable value +1
        mutationPerson.setMutation_count(fitnessHashTable.size() + 1);
        //infection status = infected
        //isinfected true
        mutationPerson.setInfected(true);
        //mutationPerson.setInfection_Status("Infected");
        //recover days
        mutationPerson.setRecovery_day(recoveryDays);
        //infection count+1
        mutationPerson.setInfection_count(mutationPerson.getInfection_count() + 1);
        return mutationPerson;
    }

    public String calculateNewGenotype() {

        StringBuilder currMutationSb;
        while (true) {
            Random random = new Random();
            int randomNumber = random.nextInt(10);
            char randomChar = (char) (randomNumber + 'A');
            if (mutationMap.get(randomChar) > 0) {
                String CurrMutation = "";
                mutationMap.replace(randomChar, Integer.parseInt(String.valueOf(mutationMap.get(randomChar))) - 1);

                currMutationSb = new StringBuilder(newGT);

                if (currMutationSb.length() == 10) {
                    if (Character.isDigit(currMutationSb.charAt(randomNumber)) && currMutationSb.charAt(randomNumber) != '9') {
                        currMutationSb.replace(randomNumber, randomNumber + 1, String.valueOf(Integer.parseInt(String.valueOf(currMutationSb.charAt(randomNumber))) + 1));
                    } else if (Character.isDigit(currMutationSb.charAt(randomNumber)) && currMutationSb.charAt(randomNumber) == '9') {
                        currMutationSb.replace(randomNumber, randomNumber + 1, String.valueOf(1));
                        currMutationSb.insert(1, 0);
                    } else {
                        currMutationSb.replace(randomNumber, randomNumber + 1, "1");
                    }
                } else {

                    if (randomNumber != 0) {
                        if (Character.isDigit(currMutationSb.charAt(randomNumber + 1))) {
                            currMutationSb.replace(randomNumber + 1, randomNumber + 2, String.valueOf(Integer.parseInt(String.valueOf(currMutationSb.charAt(randomNumber))) + 1));
                        } else {
                            currMutationSb.replace(randomNumber + 1, randomNumber + 2, "1");
                        }
                    } else {
                        currMutationSb.replace(1, 2, String.valueOf(Character.getNumericValue(currMutationSb.charAt(1)) + 1));
                    }
                }
                break;
            }
        }

        return currMutationSb.toString();
    }

    public void initializeLoad() throws IOException {
        generateMutationMap();
        loadHashTable();
        UpdateMutationColorMap(fitnessHashTable.size());
        mutationPerson = getMutationPerson(map);
        System.out.println("Mutation (Initial mutation) " + fitnessHashTable.size() + " : " + newGT + " is Variant : " + newVariantFlag);


    }

    private void UpdateMutationColorMap(int size) {
        mutation.insertIntoMutationList(fitnessHashTable.size());
        currentColor = mutation.getMutationColor().get(fitnessHashTable.size());
        System.out.println(currentColor);
    }
    // end

    //Demo Code *Needs to be updated*
    class MyTimerTask extends TimerTask {
        @Override
        public void run() {
            //adding the updated data to series in Charts class to update the charts
            VirusStrainMap.getInfected().add(presentDay , PersonDirectory.getInstance().getInfectedCount(fitnessHashTable.size()));
            VirusStrainMap.getRecovered().add(presentDay, PersonDirectory.getInstance().getRecoveredCount());
            VirusStrainMap.getVaccinated().add(presentDay, PersonDirectory.getInstance().getVaccinatedCount());

            //updating the map in Charts class to update the label data along with graph
            VirusStrainMap.data.put("Total Population: ", Integer.parseInt(map.get("human_population")));
            VirusStrainMap.data.put("Total Infected People: ", PersonDirectory.getInstance().getInfectedCount(fitnessHashTable.size()));
            VirusStrainMap.data.put("Total Recovered Cases: ", PersonDirectory.getInstance().getRecoveredCount());
            VirusStrainMap.data.put("Total Vaccinated Patients: ", PersonDirectory.getInstance().getVaccinatedCount());

            VirusStrainMap.updText();
        }
    }

    @Override
    public void run() {
        timer.schedule(new MyTimerTask(), 0, 100);
    }

    public double InfectionFactor(String human_genome, String infection_Status, Person person) {
        Map<String, String> naive = ini.get("Naive_infection_factor");
        Map<String, String> recovered = ini.get("Recovered_infection_factor");
        Map<String, String> vaccinated = ini.get("Vaccinated_infection_factor");
        String host_genotype = person.getHuman_genome();
        String host_type = person.getInfection_Status();
        double U = 0;
        int rec_day = 10;
        Hashtable<String, List<String>> fitnessHashTable = new Hashtable<>();
        List<Person> currInfectedList = PersonDirectory.getInstance().getCurrentInfectedList(fitnessHashTable.size());
        if (host_genotype == "A1") {
            if (host_type == "Naive") {
                U = Double.parseDouble(naive.get("N_A1"));
                while (rec_day != 0) {
                    U = U + 0.4;
                    rec_day--;
                }
            } else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_A1"));
                while (rec_day != 0) {
                    U = U + 0.7;
                    rec_day--;
                }
            } else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_A1"));
                while (rec_day != 0) {
                    U = U + 0.9;
                    rec_day--;
                }

            }
        } else if (host_genotype == "A2") {
            if (host_type == "Naive") {
                U = Double.parseDouble(naive.get("N_A2"));
                while (rec_day != 0) {
                    U = U + 0.4;
                    rec_day--;
                }
            } else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_A2"));
                while (rec_day != 0) {
                    U = U + 0.7;
                    rec_day--;
                }
            } else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_A2"));
                while (rec_day != 0) {
                    U = U + 0.9;
                    rec_day--;
                }
            }
        } else if (host_genotype == "B1") {
            if (host_type == "Naive") {
                U = Double.parseDouble(naive.get("N_B1"));
                while (rec_day != 0) {
                    U = U + 0.4;
                    rec_day--;
                }
            } else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_B1"));
                while (rec_day != 0) {
                    U = U + 0.7;
                    rec_day--;
                }
            } else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_B1"));
                while (rec_day != 0) {
                    U = U + 0.9;
                    rec_day--;
                }
            }
        } else if (host_genotype == "B2") {
            if (host_type == "Naive") {
                U = Double.parseDouble(naive.get("N_B2"));
                while (rec_day != 0) {
                    U = U + 0.4;
                    rec_day--;
                }
            } else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_B2"));
                while (rec_day != 0) {
                    U = U + 0.7;
                    rec_day--;
                }
            } else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_B2"));
                while (rec_day != 0) {
                    U = U + 0.9;
                    rec_day--;
                }
            }
        }
        return U;
    }

    public List<Integer> MutationFactor() {
        Map<String, String> naive = ini.get("Naive_infection_factor");
        Map<String, String> recovered = ini.get("Recovered_infection_factor");
        Map<String, String> vaccinated = ini.get("Vaccinated_infection_factor");
        int U = 0;
        int rec_day = 10;
        List<Integer> UFactor = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            U = Integer.parseInt(naive.get(naive.keySet().toArray()[i]));
            UFactor.add(U);
        }
        for (int i = 0; i < 4; i++) {
            U = Integer.parseInt(recovered.get(recovered.keySet().toArray()[i]));
            UFactor.add(U);
        }
        for (int i = 0; i < 4; i++) {
            U = Integer.parseInt(vaccinated.get(vaccinated.keySet().toArray()[i]));
            UFactor.add(U);
        }
        return UFactor;
    }


}
