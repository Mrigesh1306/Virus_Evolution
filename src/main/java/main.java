import Person.Person;
import Person.PersonDirectory;
import Simulator.Simulator;
import Virus.VirusGenomeJFrameTest;
import Virus.VirusStrainMap;
import org.ini4j.Ini;
import Virus.Virions;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;
import java.util.List;
import java.util.Timer;

public class main {



    public static void main(String args[]) throws IOException {

        Simulator graph=new Simulator();
        createGraph(graph);
        //IntializeData();
        //createVirusGenome();
       graph.initializeLoad();
        //createVirusStrainMap();
        //Virus.Virions.VirionFamily();

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

    private static void createGraph(Simulator graph) throws IOException {
        //reading the config file to fetch the value of variables
        Ini ini = new Ini(new File("./config.properties"));
        //connecting the file to map for ease in reading and fetching
        Map<String, String> map = ini.get("default");

        //creating the main frame and panel
        JFrame frame = new JFrame();
        JPanel ui = new JPanel();
        ui.setLayout(new GridLayout(1,3));

        //calling the class which creates the graph and passing it to thread to ensure continuous movement

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

