package Person;

/**
 * @author sayali mahajan
 */
public class RandomWalk {

    private int xAxis;
    private int yAxis;
    private boolean rePositioned = false;

    public RandomWalk(int xAxis, int yAxis) {
        this.xAxis = xAxis;
        this.yAxis = yAxis;
    }

    public int getxAxis() {
        return xAxis;
    }

    public void setxAxis(int xAxis) {
        this.xAxis = xAxis;
    }

    public int getyAxis() {
        return yAxis;
    }

    public void setyAxis(int yAxis) {
        this.yAxis = yAxis;
    }

    public boolean isRePositioned() {
        return rePositioned;
    }

    public void setRePositioned(boolean rePositioned) {
        this.rePositioned = rePositioned;
    }
}
