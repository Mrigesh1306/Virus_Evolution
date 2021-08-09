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


    //random walk code
    //reading the config file to fetch the value of variables
    Ini ini = new Ini(new File("./config.properties"));
    //connecting the file to 2 map to ease the reading and fetching for the default and resident_status
    Map<String, String> map = ini.get("default");
    Map<String, String> resident_status = ini.get("resident_status");
    public Mutation mutation = new Mutation();

    public static Hashtable<String, List<String>> fitnessHashTable = new Hashtable<>();
    public static Map<Character,Integer> mutationMap= new HashMap<>();


    public Timer timer = new Timer();



    //constructor
    public Simulator() throws IOException {
        super();
        this.setBackground(new Color(0, 0, 0));
    }

    @Override
    public void paint(Graphics graphics) {

        super.paint(graphics);
        List<Person> people = PersonDirectory.getInstance().getPersonList();
        mutation = new Mutation();
        for (Person person : people) {
            graphics.setColor(new Color(0xAAAAAA));
            graphics.setColor(mutation.fetchmutationColor(person.getMutation_count()));
            person.checkHealth();
            graphics.fillOval(person.getX(), person.getY(), 20, 20);
        }
        try {
            startEvolution();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private int count=0;

    public void startEvolution() throws IOException {

        //reading the config file to fetch the value of variables
        Ini ini = new Ini(new File("./config.properties"));
        //connecting the file to map for ease in reading and fetching
        Map<String, String> map = ini.get("default");
        Map<String, String> fitnesMap = ini.get("fitness_Value");


        int spreadCountPercentage=Integer.parseInt(map.get("spread_count"));
        int humanPopulation = Integer.parseInt(map.get("human_population"));
        int numberOfDays=Integer.parseInt(map.get("days"));
        int spreadDays = Integer.parseInt(map.get("spread_days"));

        generateMutationMap();
        loadHashTable();

        //number of days of simulation
        for(int i=1; i<=numberOfDays ;i=i+spreadDays)
        {

            Person mutationPerson = getMutationPerson(map);

            //2. calculate new genotype
            String newGT= calculateNewGenotype();

            //3. fitness values of genotype
            //akshay function of U
            double mutationFactor=InfectionFactor(mutationPerson.getHuman_genome(), mutationPerson.getInfection_Status(),mutationPerson);
            double mutationValue=Mutation.calculateGenotypeFitness(newGT,mutationFactor);

            //put new 12 mutation fitness values in hashtable


            //check if value is above variant threshold
            boolean newVariantFlag=Mutation.calculateMutationFactor(newGT,mutationValue,fitnessHashTable);
            //set this after adding all new values in fitness table
            mutationPerson.setMutation_count(fitnessHashTable.size());

            //5.inner loop for spread
            for(int j=i;j<=spreadDays;j++)
            {
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

                //4. repaint the simulation
                //Simulator.this.repaint();

            }


        }

        System.out.println(count++);
        if(count<10)
            repaint();
        else
            System.exit(0);


    }

    private static void loadHashTable() throws IOException {
        //reading the config file to fetch the value of variables
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> fitnesMap = ini.get("fitness_Value");
        List<String> fitnessVal=Arrays.asList(fitnesMap.entrySet().iterator().next().getValue().split(","));
        fitnessHashTable.put(fitnesMap.entrySet().iterator().next().getKey(),fitnessVal);
    }

    private static void generateMutationMap() throws IOException {
        //reading the config file to fetch the value of variables
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> mutationMapTMp = ini.get("gene_length");
        for (Map.Entry<String,String> entry : mutationMapTMp.entrySet())
            mutationMap.put(entry.getKey().charAt(0),((Integer.parseInt(entry.getValue())*30)/1000)==0?1:((Integer.parseInt(entry.getValue())*30)/1000));
    }

    @NotNull
    private static Person getMutationPerson(Map<String, String> map) {
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

    private static String calculateNewGenotype() {

        StringBuilder currMutationSb;
        while(true){
            Random random=new Random();
            int randomNumber=random.nextInt(10);
            char randomChar= (char) (randomNumber + 'A');
            if(mutationMap.get(randomChar)>0)
            {
                String CurrMutation="";
                mutationMap.replace(randomChar,mutationMap.get(randomChar)-1);
                for(String k : fitnessHashTable.keySet())
                {
                    CurrMutation= k;
                }

                currMutationSb= new StringBuilder(CurrMutation);

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
        timer.schedule(new MyTimerTask(), 0, 100);
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
        String host_genotype = person.human_genome;
        String host_type = person.infection_Status;
        double U = 0;
        int rec_day;
        Hashtable<String, List<String>> fitnessHashTable = new Hashtable<>();
        List<Person> currInfectedList = PersonDirectory.getInstance().getCurrentInfectedList(fitnessHashTable.size());

        if (host_genotype == "A1") {
            if (host_type == "Naive") {
                U = Double.parseDouble(naive.get("N_A1"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.2;
                        rec_day--;
                    }
                }
            }
            else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_A1"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.5;
                        rec_day--;
                    }
                }
            }
            else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_A1"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.7;
                        rec_day--;
                    }

                }
            }
        }
        else if (host_genotype == "A2") {
            if (host_type == "Naive") {
                U = Double.parseDouble(naive.get("N_A2"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.2;
                        rec_day--;
                    }
                }
            } else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_A2"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.5;
                        rec_day--;
                    }
                }
            } else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_A2"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.7;
                        rec_day--;
                    }
                }
            }
        }
        else if (host_genotype == "B1") {
            if (host_type == "Naive") {
                U = Double.parseDouble(naive.get("N_B1"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.2;
                        rec_day--;
                    }
                }
            }
            else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_B1"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.5;
                        rec_day--;
                    }
                }
            }
            else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_B1"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.7;
                        rec_day--;
                    }
                }
            }
        }
        else if (host_genotype == "B2") {
            if (host_type == "Naive") {
                U = Double.parseDouble(naive.get("N_B2"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.2;
                        rec_day--;
                    }
                }
            }
            else if (host_type == "Recovered") {
                U = Double.parseDouble(recovered.get("R_B2"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.5;
                        rec_day--;
                    }
                }
            }
            else if (host_type == "Vaccinated") {
                U = Double.parseDouble(vaccinated.get("V_B2"));
                for (Person p : PersonDirectory.getInstance().getPersonList()) {
                    rec_day = p.getRecovery_day();
                    while (rec_day != 0) {
                        U = U + 0.7;
                        rec_day--;
                    }
                }
            }

        }
        return U;
    }


}
