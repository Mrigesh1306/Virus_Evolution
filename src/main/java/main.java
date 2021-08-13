import Simulator.Simulator;
import Virus.VirusStrainMap;
import org.ini4j.Ini;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class main {


    public static void main(String args[]) throws IOException {

        Simulator graph = new Simulator();
        createGraph(graph);
        graph.initializeLoad();
    }

    private static void createGraph(Simulator graph) throws IOException {
        //reading the config file to fetch the value of variables
        Ini ini = new Ini(new File("./config.properties"));
        //connecting the file to map for ease in reading and fetching
        Map<String, String> map = ini.get("default");

        //creating the main frame and panel
        JFrame frame = new JFrame();
        JPanel ui = new JPanel();
        ui.setLayout(new GridLayout(1, 3));

        //calling the class which creates the graph and passing it to thread to ensure continuous movement

        Thread panelThread = new Thread(graph);

        //adding the graph and line charts to main panel and setting the background color
        ui.add(graph);
        ui.add(new VirusStrainMap(graph));
        ui.setBackground(Color.BLACK);

        //adding the ui to frame and adjusting its attribute
        frame.add(ui);
        frame.setVisible(true);
        frame.setTitle(map.get("virus_evolution") + " of " + map.get("human_population") + " Residents on Island");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1200, 800);
        frame.setResizable(false);
        frame.setLocation(50, 50);

        //starting the thread
        panelThread.start();
    }

}

