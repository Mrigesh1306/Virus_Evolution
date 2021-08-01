import Virus.VirusGenomeJFrameTest;
import org.ini4j.Ini;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Map;

public class main {


    public static void main(String args[]) throws IOException {

        //createGraph();

        createVirusGenome();

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
        frame.setSize(1200, 600);
        frame.setResizable(false);
        frame.setLocation(50, 50);

    }

    private static void createAndShowGui() throws IOException {
        JFrame frame = new JFrame("Virus Genome");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(new VirusGenomeJFrameTest());
        frame.setSize(1200, 600);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setLocation(50, 50);

    }
}

