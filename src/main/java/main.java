import Person.Person;
import Person.PersonDirectory;
import Simulator.Simulator;
import Virus.VirusGenomeJFrameTest;
import Virus.VirusStrainMap;
import org.ini4j.Ini;
import Virus.Virions;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class main {


    public static Hashtable<String, List<String>> fitnessHashTable = new Hashtable<>();
    public static Map<Character,Integer> mutationMap= new HashMap<>();

    public static void main(String args[]) throws IOException {

        //createGraph();
        //createVirusGenome();
        startEvolution();
        //createVirusStrainMap();
        //Virus.Virions.VirionFamily();

    }

    private static void startEvolution() throws IOException {

        //reading the config file to fetch the value of variables
        Ini ini = new Ini(new File("./config.properties"));
        //connecting the file to map for ease in reading and fetching
        Map<String, String> map = ini.get("default");
        Map<String, String> fitnesMap = ini.get("fitness_Value");
        Map<String, String> mutationMapTMp = ini.get("gene_length");
        //System.out.println(mutationMapTMp.get(mutationMapTMp.keySet().toArray()[5]));
        int spreadCountPercentage=Integer.parseInt(map.get("spread_count"));
        int humanPopulation = Integer.parseInt(map.get("human_population"));
        for (Map.Entry<String,String> entry : mutationMapTMp.entrySet())
            mutationMap.put(entry.getKey().charAt(0),((Integer.parseInt(entry.getValue())*30)/1000)==0?1:((Integer.parseInt(entry.getValue())*30)/1000));

        List<String> fitnessVal=Arrays.asList(fitnesMap.get("10BCDEFGHIJ").split(","));
        fitnessHashTable.put("10BCDEFGHIJ",fitnessVal);
        int numberOfDays=Integer.parseInt(map.get("days"));
        //number of days of simulation
        for(int i=1; i<=numberOfDays ;i=i+12)
        {
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

            //2. calculate new genotype
            String newGT= calculateNewGenotype();

            //3. fitness values of genotype
            //akshay function of U
            //int mutationFactor=getVCalueofUfactor(mutationPerson.getHuman_genome(), mutationPerson.getInfection_Status());
            //double mutationValue=calcFitnessVal(newGT,mutationFactor,fitnessHashTable);
            //check if value is above variant threshold

            mutationPerson.setMutation_count(fitnessHashTable.size());

            //5.inner loop for spread
            for(int j=i;j<=Integer.parseInt(map.get("spread_days"));j++)
            {
                //infect people loop - daily
                List<Person> currNonInfectedList= PersonDirectory.getInstance().getCurrentNonInfectedList();
                for(int k=0;i<(spreadCountPercentage * humanPopulation/100);k++)
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

            }

            //4. repaint the simulation

        }

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

    private static void createVirusStrainMap() throws IOException {
        VirusStrainMap vmap =  new VirusStrainMap();
        Simulator sim = new Simulator(vmap);
        //new Timer().schedule(sim, 10, 15);
    }


    private static void createVirusGenome() {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                try {
                    createAndShowGui();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private static void createGraph() throws IOException {
        //reading the config file to fetch the value of variables
        Ini ini = new Ini(new File("./config.properties"));
        //connecting the file to map for ease in reading and fetching
        Map<String, String> map = ini.get("default");

        //creating the main frame and panel
        JFrame frame = new JFrame();
        JPanel ui = new JPanel();
        ui.setLayout(new GridLayout(1,3));

        //calling the class which creates the graph and passing it to thread to ensure continuous movement
        Simulator graph = new Simulator();
        Thread panelThread = new Thread(graph);

        //adding the graph and line charts to main panel and setting the background color
        ui.add(graph);
        //ui.add(new Charts());
        ui.setBackground(Color.BLACK);

        //adding the ui to frame and adjusting its attribute
        frame.add(ui);
        frame.setVisible(true);
        frame.setTitle(map.get("virus_simulation")+" of "+map.get("city_population")+" Residents of Boston");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setResizable(false);
        frame.setLocation(50, 50);

        //starting the thread
        panelThread.start();
    }

    private static void createAndShowGui() throws IOException {
        JFrame frame = new JFrame("Virus Genome");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new VirusGenomeJFrameTest());
        frame.setSize(400, 500);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setLocation(600, 400);

    }
}

