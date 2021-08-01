package Virus;

import org.ini4j.Ini;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Map;

@SuppressWarnings("serial")
public class VirusGenomeJFrameTest extends JPanel {

    private JTextArea textArea = new JTextArea(30, 90);

    private VirusGenomeJFrame taOutputStream = new VirusGenomeJFrame(
            textArea, "Virus Genome1");

    public VirusGenomeJFrameTest() throws IOException {

        VirusGenome v = new VirusGenome();
        Ini ini = new Ini(new File("./config.properties"));
        Map<String, String> map = ini.get("default");


        textArea.setFont(new Font("monospaced", Font.PLAIN, 12));
        setLayout(new BorderLayout());
        add(new JScrollPane(textArea, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));
        System.setOut(new PrintStream(taOutputStream));

        v.displayGenotype(map.get("isMutationOccured"),
                map.get("mutation_rate"),map.get("recombination_rate"));

//        int timerDelay = 10;
//        new Timer(timerDelay, arg0 -> v.displayGenotype(map.get("isMutationOccured"),
//                map.get("mutation_rate"),map.get("recombination_rate"))).start();
    }
}
