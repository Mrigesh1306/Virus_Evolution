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
        createVirusStrainMap();
        Virus.Virions.VirionFamily();

    }

    private static void startEvolution() throws IOException {

        //reading the config file to fetch the value of variables
        Ini ini = new Ini(new File("./config.properties"));
        //connecting the file to map for ease in reading and fetching
        Map<String, String> map = ini.get("default");
        Map<String, String> fitnesMap = ini.get("fitness_Value");
        Map<String, String> mutationMapTMp = ini.get("mutation_position_probablity");
        System.out.println(mutationMapTMp.get(mutationMapTMp.keySet().toArray()[5]));

        for (Map.Entry<String,String> entry : mutationMapTMp.entrySet())
            mutationMap.put(entry.getKey().charAt(0),Integer.parseInt(entry.getValue()));

        List<String> fitnessVal=Arrays.asList(fitnesMap.get("10BCDEFGHIJ").split(","));
        fitnessHashTable.put("10BCDEFGHIJ",fitnessVal);
        int numberOfDays=Integer.parseInt(map.get("days"));
        //number of days of simulation
        for(int i=1; i<=numberOfDays ;i++)
        {
            //1. random person selection - infected
            Random random=new Random();
            int randomNumber=random.nextInt(10);

            //2. calculate new genotype
            String newGT= calculateNewGenotype();

            //3. fitness values of genotype

            //4. repaint the simulation

            //5.inner loop for spread
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

