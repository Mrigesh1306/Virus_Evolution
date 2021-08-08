package Simulator;

import Mutation.Mutation;
import Person.Person;
import Person.PersonDirectory;
import Virus.VirusStrainMap;
import org.ini4j.Ini;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class Simulator extends JPanel implements Runnable {


    //random walk code
    //reading the config file to fetch the value of variables
    Ini ini = new Ini(new File("./config.properties"));
    //connecting the file to 2 map to ease the reading and fetching for the default and resident_status
    Map<String, String> map = ini.get("default");
    Map<String, String> resident_status = ini.get("resident_status");
    public Mutation mutation = new Mutation();

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
            graphics.setColor(mutation.fetchmutationColor(person.getMutation_count()));
            person.checkHealth();
            graphics.fillOval(person.getX(), person.getY(), 20, 20);
        }
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


}
