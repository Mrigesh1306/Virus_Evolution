package Person;

import Island.Island;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class Person extends Position {

    private RandomWalk randomWalk;
    Ini ini = new Ini(new File("./config.properties"));
    Map<String, String> map = ini.get("default");
    //This shows current health of residents
    Map<String, String> host_type = ini.get("host_type");

    private double xAxis;
    private double yAxis;
    private Island island;
    private boolean isInfected = false;
    private boolean isDead = false;
    public String human_genome = "";
    public String infection_Status = "Naive";
    public int infection_count;
    public double infection_Factor;
    public double death_factor;
    public int recovery_day;
    public int mutation_count;

    public void doRandomMove() {

        // Random random=new Random();
        if (randomWalk == null || randomWalk.isRePositioned()) {
            double targetX = MathUtil.stdGaussian(100, xAxis);
            double targetY = MathUtil.stdGaussian(100, yAxis);
            randomWalk = new RandomWalk((int) targetX, (int) targetY);
        }
        if ((getY() - 400) * (randomWalk.getxAxis() - 400) < 0) {
            if (randomWalk.getyAxis() < 400) {
                island = new Island(200, 200);
            } else {
                island = new Island(500, 500);
            }
        }
        int dX = randomWalk.getxAxis() - getX();
        int dY = randomWalk.getyAxis() - getY();
        double length = Math.sqrt(Math.pow(dX, 2) + Math.pow(dY, 2));

        if (length < 1) {
            randomWalk.setRePositioned(true);
            return;
        }

        int udX = (int) (dX / length);
        if (udX == 0 && dX != 0) {
            if (dX > 0) {
                udX = 1;
            } else {
                udX = -1;
            }
        }

        int udY = (int) (dY / length);
        if (udY == 0 && dY != 0) {
            if (dY > 0) {
                udY = 1;
            } else {
                udY = -1;
            }
        }
        if (getX() > Integer.parseInt(map.get("island_width")) || getX() < 0) {
            randomWalk = null;
            if (udX > 0) {
                udX = -udX;
            }
        }
        if (getY() > Integer.parseInt(map.get("island_height")) || getY() < 0) {
            randomWalk = null;
            if (udY > 0) {
                udY = -udY;
            }
        }
        rePosition(udX, udY);
    }

    public void checkHealth() {
        double targetX = MathUtil.stdGaussian(100, xAxis);
        double targetY = MathUtil.stdGaussian(100, yAxis);
        randomWalk = new RandomWalk((int) targetX, (int) targetY);
        doRandomMove();
    }

    public Person(int xAxis, int yAxis, Island island, String human_genome) throws IOException {
        super(xAxis, yAxis);
        this.xAxis = MathUtil.stdGaussian(100, xAxis);
        this.yAxis = MathUtil.stdGaussian(100, yAxis);
        this.human_genome = human_genome;
        this.island = island;
    }

    public RandomWalk getRandomWalk() {
        return randomWalk;
    }

    public void setRandomWalk(RandomWalk randomWalk) {
        this.randomWalk = randomWalk;
    }

    public double getxAxis() {
        return xAxis;
    }

    public void setxAxis(double xAxis) {
        this.xAxis = xAxis;
    }

    public double getyAxis() {
        return yAxis;
    }

    public void setyAxis(double yAxis) {
        this.yAxis = yAxis;
    }

    public Island getIsland() {
        return island;
    }

    public void setIsland(Island island) {
        this.island = island;
    }

    public boolean isInfected() {
        return isInfected;
    }

    public void setInfected(boolean infected) {
        isInfected = infected;
    }

    public String getHuman_genome() {
        return human_genome;
    }

    public void setHuman_genome(String human_genome) {
        this.human_genome = human_genome;
    }

    public String getInfection_Status() {
        return infection_Status;
    }

    public void setInfection_Status(String infection_Status) {
        this.infection_Status = infection_Status;
    }

    public int getInfection_count() {
        return infection_count;
    }

    public void setInfection_count(int infection_count) {
        this.infection_count = infection_count;
    }

    public double getInfection_Factor() {
        return infection_Factor;
    }

    public void setInfection_Factor(double infection_Factor) {
        this.infection_Factor = infection_Factor;
    }

    public double getDeath_factor() {
        return death_factor;
    }

    public void setDeath_factor(double death_factor) {
        this.death_factor = death_factor;
    }

    public int getRecovery_day() {
        return recovery_day;
    }

    public void setRecovery_day(int recovery_day) {
        this.recovery_day = recovery_day;
    }

    public int getMutation_count() {
        return mutation_count;
    }

    public void setMutation_count(int mutation_count) {
        this.mutation_count = mutation_count;
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        isDead = dead;
    }

    public double InfectionFactor(String human_genome, String infection_Status) {
        Map<String, String> naive = ini.get("Naive_infection_factor");
        Map<String, String> recovered = ini.get("Recovered_infection_factor");
        Map<String, String> vaccinated = ini.get("Vaccinated_infection_factor");
        String host_genotype = this.human_genome;
        String host_type = this.infection_Status;
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
