package Simulator;

import Virus.VirusStrainMap;

import java.util.TimerTask;

public class Simulator extends TimerTask {

    static int speed = 2;
    static boolean playing = true;
    public int actualTicks;
    public static int simTicks;
    VirusStrainMap vmap;

    public Simulator(VirusStrainMap vmap){
        actualTicks=0;
        simTicks = 0;
        this.vmap=vmap;
    }

    //Demo Code *Needs to be updated*
    @Override
    public void run() {
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
