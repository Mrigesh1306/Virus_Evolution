import Simulator.Simulator;
import Virus.VirusGenomeJFrameTest;
import Virus.VirusStrainMap;
import org.ini4j.Ini;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Timer;

public class main {


    public static void main(String args[]) throws IOException {

        createGraph();
        createVirusGenome();
        createVirusStrainMap();
    }

    private static void createVirusStrainMap() {
        VirusStrainMap vmap =  new VirusStrainMap();
        Simulator sim = new Simulator(vmap);
        new Timer().schedule(sim, 10, 15);
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

        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> map = ini.get("default");

        JFrame frame = new JFrame();
        JPanel ui = new JPanel();
        ui.setLayout(new GridLayout(1,3));

        frame.add(ui);
        frame.setVisible(true);
        frame.setTitle("Viral Evolution of SARS CoV-19 on Island");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(700, 650);
        frame.setResizable(false);
        frame.setLocation(0, 0);

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

