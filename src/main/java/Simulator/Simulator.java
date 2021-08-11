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
import java.util.*;
import java.util.List;
import java.util.Timer;

public class Simulator extends JPanel implements Runnable {

    //reading the config file to fetch the value of variables
    Ini ini = new Ini(new File("./config.properties"));
    //connecting the file to 2 map to ease the reading and fetching for the default and resident_status
    Map<String, String> map = ini.get("default");
    Map<String, String> resident_status = ini.get("resident_status");
    Map<String, String> fitnesMap = ini.get("fitness_Value");
    Color currentColor=new Color(255, 255, 255);

    Person mutationPerson;
    String newGT="";
    boolean newVariantFlag=true;

    int spreadCountPercentage=Integer.parseInt(map.get("spread_count"));
    int humanPopulation = Integer.parseInt(map.get("human_population"));
    int numberOfDays=Integer.parseInt(map.get("days"));
    int spreadDays = Integer.parseInt(map.get("spread_days"));
    int MaxMutation=Integer.parseInt(map.get("mutation"));
    float vaccineRate=Float.parseFloat(map.get("vaccination_rate"));

    int dailyVaccination = (int)(vaccineRate*numberOfDays)/humanPopulation ;

    int presentDay =1;
    int eachSpreadDay=0;

    public Mutation mutation = new Mutation();
    public static Hashtable<String, List<String>> fitnessHashTable = new Hashtable<>();
    public static Map<Character,Integer> mutationMap= new HashMap<>();


    public Timer timer = new Timer();

    //constructor
    public Simulator() throws IOException {
        super();
        this.setBackground(new Color(224, 195, 145));
    }

    @Override
    public void paint(Graphics graphics) {

        super.paint(graphics);
        List<Person> people = PersonDirectory.getInstance().getPersonList();
        for (Person person : people) {
            if(mutation.getMutationColor().containsKey(person.getMutation_count()))
            {
                graphics.setColor(mutation.fetchmutationColor(person.getMutation_count()));
            }
            else {
                if(person.isInfected())
                {
                    graphics.setColor(currentColor);
                }
                else if(!person.isInfected()) {

                     switch (person.infection_Status)
                     {
                         case "Naive" : {
                             graphics.setColor(new Color(255,255,255));
                             break;
                         }
                         case "Recovered" : {
                             graphics.setColor(new Color(177, 177, 177));
                             break;
                         }
                         case "Vaccinated" : {
                             graphics.setColor(new Color(90, 255, 0));
                             break;
                         }
                     }
                }
            }

            person.checkHealth();
            graphics.fillOval(person.getX(), person.getY(), 5, 5);
        }
        try {

            startEvolution();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

    }

    public void startEvolution() throws IOException, InterruptedException {

        //number of days of simulation
        if(presentDay%spreadDays==0) {

            //2. calculate new genotype
            newGT = calculateNewGenotype();

            //put new 12 mutation fitness values in hashtable
            pushValueToHashtable(newGT);

            //set this after adding all new values in fitness table
            mutationPerson.setMutation_count(fitnessHashTable.size());

            //3. fitness values of genotype of one selected person (host wise)
            double mutationFactor = InfectionFactor(mutationPerson.getHuman_genome(), mutationPerson.getInfection_Status(), mutationPerson);
            double mutationValue = Mutation.calculateGenotypeFitness(newGT, mutationFactor);

            //check if value is above variant threshold
            newVariantFlag = Mutation.calculateMutationFactor(newGT, mutationValue, fitnessHashTable);
            if(newVariantFlag)
            {
                currentColor=Mutation.getMutationColor().get(mutationPerson.getMutation_count());
            }
        }
            //5.inner loop for spread

                //infect people loop - daily
                List<Person> currNonInfectedList= PersonDirectory.getInstance().getCurrentNonInfectedList();
                for(int k=0;k<(spreadCountPercentage * humanPopulation/100);k++)
                {
                    //update noninfected list
                    currNonInfectedList= PersonDirectory.getInstance().getCurrentNonInfectedList();
                    Random r=new Random();
                    Person currPerson=currNonInfectedList.get(r.nextInt(currNonInfectedList.size()));
                    currPerson.setInfection_count(currPerson.getInfection_count()+1);
                    currPerson.setMutation_count(mutationPerson.getMutation_count());
                    currPerson.setRecovery_day(Integer.parseInt(map.get("recovery_days")));
                    currPerson.setInfected(true);
                }

                //update all person's properties including recovery_days and status.
                for(Person p : PersonDirectory.getInstance().getPersonList()){

                    if(p.getRecovery_day()==0)
                        p.setInfected(false);
                    if(p.isInfected())
                        p.setRecovery_day(p.getRecovery_day()-1);

                    //check for person is dead or not
                    if(p.getRecovery_day()!=0 && !p.isInfected())
                    {
                        p.setDead(true);
                    }

                }

                //vaccination logic

        if(presentDay>(numberOfDays/2))
        {
            for(int i=1;i<=dailyVaccination;i++)
            {
                List<Person> currentNonPersonList=PersonDirectory.getInstance().getCurrentNonInfectedAndNonVaccinatedList();
                Random random=new Random();
                int number=random.nextInt(currentNonPersonList.size());
                Person p=currentNonPersonList.get(number);
                p.setInfection_Status("Vaccinated");
                p.setVaccinated(true);


            }
        }
        //break the paint() method
        System.out.println(presentDay);
        if(presentDay<numberOfDays) {
            Thread.sleep(500);
            presentDay++;
            repaint();
        }else
            System.exit(0);


    }

    public void pushValueToHashtable(String newGT) throws IOException {
        List<Integer> UFactorPerMutation= MutationFactor();
        List<String> hostFitnessValues=new ArrayList<>();
        for(Integer i: UFactorPerMutation)
        {
            double eachFitnessValue = Mutation.calculateGenotypeFitness(newGT, i);
            hostFitnessValues.add(String.valueOf(eachFitnessValue));
        }
        fitnessHashTable.put(newGT,hostFitnessValues);
    }

    public void loadHashTable() throws IOException {
        //reading the config file to fetch the value of variables
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> fitnesMap = ini.get("fitness_Value");
        List<String> fitnessVal=Arrays.asList(fitnesMap.entrySet().iterator().next().getValue().split(","));
        newGT=fitnesMap.entrySet().iterator().next().getKey();
        fitnessHashTable.put(fitnesMap.entrySet().iterator().next().getKey(),fitnessVal);
    }

    public void generateMutationMap() throws IOException {
        //reading the config file to fetch the value of variables
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> mutationMapTMp = ini.get("gene_length");
        for (Map.Entry<String,String> entry : mutationMapTMp.entrySet())
            mutationMap.put(entry.getKey().charAt(0),((Integer.parseInt(entry.getValue())*MaxMutation)/humanPopulation)==0?1:((Integer.parseInt(entry.getValue())*MaxMutation)/humanPopulation));
    }

    @NotNull
    public static Person getMutationPerson(Map<String, String> map) {
        //1. random person selection - infected
        List<Person> currInfectedList= PersonDirectory.getInstance().getCurrentInfectedList(fitnessHashTable.size());
        Random random=new Random();
        int randomPerson=random.nextInt(currInfectedList.size());
        Person mutationPerson=currInfectedList.get(randomPerson);
        //change properties of person.
        //mutaion count hashtable value +1
        mutationPerson.setMutation_count(fitnessHashTable.size()+1);
        //infection status = infected
        //isinfected true
        mutationPerson.setInfected(true);
        //mutationPerson.setInfection_Status("Infected");
        //recover days
        mutationPerson.setRecovery_day(Integer.parseInt(map.get("recover_days")));
        //infection count+1
        mutationPerson.setInfection_count(mutationPerson.getInfection_count()+1);
        return mutationPerson;
    }

    public String calculateNewGenotype() {

        StringBuilder currMutationSb;
        while(true){
            Random random=new Random();
            int randomNumber=random.nextInt(10);
            char randomChar= (char) (randomNumber + 'A');
            if(mutationMap.get(randomChar)>0)
            {
                String CurrMutation="";
                mutationMap.replace(randomChar,mutationMap.get(randomChar)-1);
//                for(String k : fitnessHashTable.keySet())
//                {
//                    CurrMutation= k;
//                }

                currMutationSb= new StringBuilder(newGT);

                if(currMutationSb.length()==10)
                {
                    if(Character.isDigit(currMutationSb.charAt(randomNumber)) && currMutationSb.charAt(randomNumber)!='9')
                    {
                        currMutationSb.replace(randomNumber,randomNumber+1,String.valueOf(currMutationSb.charAt(randomNumber)+1));
                    }
                    else if(Character.isDigit(currMutationSb.charAt(randomNumber)) && currMutationSb.charAt(randomNumber)=='9')
                    {
                        currMutationSb.replace(randomNumber,randomNumber+1, String.valueOf(1));
                        currMutationSb.insert(1,0);
                    }
                    else {
                        currMutationSb.replace(randomNumber,randomNumber+1,"1");
                    }
                }
                else {

                    if (randomNumber != 0) {
                        if (Character.isDigit(currMutationSb.charAt(randomNumber+1))) {
                            currMutationSb.replace(randomNumber + 1, randomNumber + 2, String.valueOf(currMutationSb.charAt(randomNumber + 1) + 1));
                        } else {
                            currMutationSb.replace(randomNumber + 1, randomNumber + 2, "1");
                        }
                    } else {
                        //System.out.println(String.valueOf(Character.getNumericValue(currMutationSb.charAt(1)) + 1));
                        currMutationSb.replace(1, 2,String.valueOf(Character.getNumericValue(currMutationSb.charAt(1)) + 1));
                    }

                }
                //   System.out.println(currMutationSb);
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


    }

    private void UpdateMutationColorMap(int size) {
        Mutation.insertIntoMutationList(fitnessHashTable.size());
        currentColor=Mutation.getMutationColor().get(fitnessHashTable.size());
    }

    class MyTimerTask extends TimerTask {
        @Override
        public void run() {

            Simulator.this.repaint();
        }
    }

    // end

    static int speed = 2;
    static boolean playing = true;
    public int actualTicks;
    public static int simTicks;
    VirusStrainMap vmap;

    public Simulator(VirusStrainMap vmap) throws IOException {
        actualTicks=0;
        simTicks = 0;
        this.vmap=vmap;
    }

    //Demo Code *Needs to be updated*
    @Override
    public void run() {
        timer.schedule(new MyTimerTask(), 0, 2000);
        if(playing) {
            actualTicks++;
            if(actualTicks%speed==0) {
                simTicks++;
                vmap.mut1_infected++;
                vmap.mut2_infected++;
                vmap.mut3_infected++;
                //canvas.repaint();
                vmap.updateChart();
                if(simTicks>18000) stopSim();
                //Map.instance.update();

            }
        }
    }


    public static void stopSim() {
        playing = false;
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {

            e.printStackTrace();
        }
        System.exit(0);
    }

    public double InfectionFactor(String human_genome, String infection_Status,Person person) {
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
                    U = U + 0.2;
                    rec_day--;
                }
            }
            else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_A1"));
                while (rec_day != 0) {
                    U = U + 0.5;
                    rec_day--;
                }
            }
            else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_A1"));
                while (rec_day != 0) {
                    U = U + 0.7;
                    rec_day--;
                }

            }
        }
        else if (host_genotype == "A2") {
            if (host_type == "Naive") {
                U = Double.parseDouble(naive.get("N_A2"));
                while (rec_day != 0) {
                    U = U + 0.2;
                    rec_day--;
                }
            }
            else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_A2"));
                while (rec_day != 0) {
                    U = U + 0.5;
                    rec_day--;
                }
            }
            else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_A2"));
                while (rec_day != 0) {
                    U = U + 0.7;
                    rec_day--;
                }
            }
        }
        else if (host_genotype == "B1") {
            if (host_type == "Naive") {
                U = Double.parseDouble(naive.get("N_B1"));
                while (rec_day != 0) {
                    U = U + 0.2;
                    rec_day--;
                }
            }
            else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_B1"));
                while (rec_day != 0) {
                    U = U + 0.5;
                    rec_day--;
                }
            }
            else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_B1"));
                while (rec_day != 0) {
                    U = U + 0.7;
                    rec_day--;
                }
            }
        }
        else if (host_genotype == "B2") {
            if (host_type == "Naive") {
                U = Double.parseDouble(naive.get("N_B2"));
                while (rec_day != 0) {
                    U = U + 0.2;
                    rec_day--;
                }
            }
            else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_B2"));
                while (rec_day != 0) {
                    U = U + 0.5;
                    rec_day--;
                }
            }
            else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_B2"));
                while (rec_day != 0) {
                    U = U + 0.7;
                    rec_day--;
                }
            }
        }
        return U;
    }

    public static Hashtable<String, List<String>> getFitnessHashTable() {
        return fitnessHashTable;
    }

    public static void setFitnessHashTable(Hashtable<String, List<String>> fitnessHashTable) {
        Simulator.fitnessHashTable = fitnessHashTable;
    }

    public static Map<Character, Integer> getMutationMap() {
        return mutationMap;
    }

    public static void setMutationMap(Map<Character, Integer> mutationMap) {
        Simulator.mutationMap = mutationMap;
    }

    public List<Integer> MutationFactor() {
        Map<String, String> naive = ini.get("Naive_infection_factor");
        Map<String, String> recovered = ini.get("Recovered_infection_factor");
        Map<String, String> vaccinated = ini.get("Vaccinated_infection_factor");
//        String host_genotype = human_genome;
//        String host_type = infection_Status;
        int U = 0;
        int rec_day = 10;
        List<Integer> UFactor = new ArrayList<>();
        //U = Integer.parseInt(naive.get("N_A1"));
        for(int i=0; i<4; i++){
            U=Integer.parseInt(naive.get(naive.keySet().toArray()[i]));
            UFactor.add(U);
        }
        for(int i=0; i<4; i++){
            U=Integer.parseInt(recovered.get(recovered.keySet().toArray()[i]));
            UFactor.add(U);
        }
        for(int i=0; i<4; i++){
            U=Integer.parseInt(vaccinated.get(vaccinated.keySet().toArray()[i]));
            UFactor.add(U);
        }
        System.out.println("U Factor"+ UFactor);
        //UFactor.add(Integer.parseInt(naive.get("N_A1")));
        return UFactor;
    }


}
