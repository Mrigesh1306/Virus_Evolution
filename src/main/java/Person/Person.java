package Person;

import Island.Island;
import org.ini4j.Ini;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class Person extends Position{

private RandomWalk randomWalk;
    Ini ini = new Ini(new File("./config.properties"));
    Map<String, String> map = ini.get("default");
    //This shows current health of residents
    Map<String, String> host_type = ini.get("host_type");

    private double xAxis;
    private double yAxis;
    private Island island;
    private boolean isInfected;

    public Person(int xAxis, int yAxis, Island island) throws IOException {
        super(xAxis,yAxis);
        this.xAxis = MathUtil.stdGaussian(100, xAxis);
        this.yAxis = MathUtil.stdGaussian(100, yAxis);
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

    public boolean isInfected() {
        return isInfected;
    }

    public void setInfected(boolean infected) {
        isInfected = infected;
    }
}
