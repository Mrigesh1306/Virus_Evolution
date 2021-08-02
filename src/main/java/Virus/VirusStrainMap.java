package Virus;

import org.knowm.xchart.SwingWrapper;
import org.knowm.xchart.XChartPanel;
import org.knowm.xchart.XYChart;
import org.knowm.xchart.XYChartBuilder;
import org.knowm.xchart.style.Styler;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.LinkedList;

public class VirusStrainMap extends JFrame {

    public SwingWrapper<XYChart> sw;
    public XYChart chart;
    public JFrame frame;
    public JPanel chartPanel;
    private HashMap<String,JLabel> labels;

    static public int mut1_infected=100;
    static public int mut2_infected=200;
    static public int mut3_infected=300;

    static public VirusStrainMap instance;

    //Chart properties
    public static LinkedList<Integer> xData = new LinkedList<>();
    public static LinkedList<Integer> yData_Mutation1 = new LinkedList<>();
    public static LinkedList<Integer> yData_Mutation2 = new LinkedList<>();
    public static LinkedList<Integer> yData_Mutation3 = new LinkedList<>();

    public VirusStrainMap(){
        System.out.println("Hello");
        xData.add(100);
        yData_Mutation1.add(mut1_infected);
        yData_Mutation2.add(mut2_infected);
        yData_Mutation3.add(mut3_infected);
        this.chart = new XYChartBuilder().width(1200).height(600).title("Virus Mutation Simulation").xAxisTitle("Time (Days)").yAxisTitle("Number of people").theme(Styler.ChartTheme.Matlab).build();
        chart.addSeries("Mutation 1", xData, yData_Mutation1);
        chart.addSeries("Mutation 2", xData, yData_Mutation2);
        chart.addSeries("Mutation 3", xData, yData_Mutation3);
        this.chartPanel = new XChartPanel<XYChart>(chart);
        chartPanel.setLocation(0, 0);
        chartPanel.setSize(700, 450);

        frame = new JFrame();
        frame.getContentPane().setBackground(Color.LIGHT_GRAY);
        frame.setResizable(false);
        frame.setTitle("Real Time Data");
        frame.setSize(new Dimension(700, 450));
        frame.getContentPane().setLayout(null);
        frame.getContentPane().add(chartPanel);
        frame.setVisible(true);
        frame.setLocation(650, 0);

    }

    public static void addChartValue(LinkedList<Integer> Data, Integer value)
    {
        Data.add(value);
    }

    public void updateChart() {
       addChartValue(xData,(1000));
        addChartValue(yData_Mutation1, mut1_infected);
        addChartValue(yData_Mutation2, mut2_infected);
        addChartValue(yData_Mutation3, mut3_infected);
        realTimeupdates();
    }

    public  void realTimeupdates() {
        chart.updateXYSeries("Mutation 1", xData, yData_Mutation1, null);
        chart.updateXYSeries("Mutation 2", xData, yData_Mutation2, null);
        chart.updateXYSeries("Mutation 3", xData, yData_Mutation3, null);
        frame.repaint();
    }

}
