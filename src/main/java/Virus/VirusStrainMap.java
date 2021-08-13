package Virus;

import Simulator.Simulator;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.block.BlockBorder;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.TextTitle;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class VirusStrainMap extends JPanel {

    public JFrame frame;

    //creating a 3 series for holding data to draw charts
    static XYSeries infected = new XYSeries("Infected People");
    static XYSeries recovered = new XYSeries("Recovered People");
    static XYSeries vaccinated = new XYSeries("Vaccinated People");

    //creating label to print the numbers
    static JLabel lbl = new JLabel();

    //map to continuously update the label data
    public static Map<String, Integer> data = new HashMap();

    XYDataset infDataSet = createDataset(infected);
    XYDataset recDataSet = createDataset(recovered);
    XYDataset vacDataSet = createDataset(vaccinated);
    ChartPanel infChart = createChart(infDataSet, Color.RED);
    ChartPanel recChart = createChart(recDataSet, Color.GRAY);
    ChartPanel vacChart = createChart(vacDataSet, Color.GREEN);

    public VirusStrainMap(Simulator s) throws IOException {

        //diving the graphs and label in two panel for better presenattion
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JPanel mainPanel2 = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        JPanel p1 = new JPanel();
        infChart.setBackground(Color.white);
        p1.add(infChart);
        JPanel p2 = new JPanel();
        recChart.setBackground(Color.white);
        p2.add(recChart);
        JPanel p3 = new JPanel();
        vacChart.setBackground(Color.white);
        p3.add(vacChart);

        //setting the label font, size and border
        JPanel p4 = new JPanel();
        lbl.setFont(new Font(lbl.getFont().getName(), Font.PLAIN, 18));
        p4.setSize(400, 250);
        p4.setBorder(BorderFactory.createLineBorder(Color.black));
        p4.add(lbl);

        //adding the panels to main window panel in main using this keyword
        mainPanel.add(p1);
        mainPanel.add(p2);
        mainPanel2.add(p3);
        mainPanel2.add(p4);
        this.add(mainPanel);
        this.add(mainPanel2);

    }

    public static XYSeries getInfected() {
        return infected;
    }

    public static XYSeries getRecovered() {
        return recovered;
    }

    public static XYSeries getVaccinated() {
        return vaccinated;
    }

    //creating DataSet from series
    private static XYDataset createDataset(XYSeries s) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(s);
        return dataset;
    }

    //method to update the label data by reading map values
    public static void updText() {
        String res = "<html>";
        for (String name : data.keySet()) {
            String key = name;
            String value = data.get(name).toString();
            res += key + " " + value + "<br/>";
        }
        res += "</html>";
        lbl.setText(res);
    }

    //drawing the charts on the panel using this method
    private static ChartPanel createChart(XYDataset dataset, Color c) {
        JFreeChart chart = ChartFactory.createXYLineChart(
                "",
                "Days",
                "Number of people",
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false
        );

        XYPlot plot = chart.getXYPlot();

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesPaint(0, c);
        renderer.setSeriesStroke(0, new BasicStroke(1.0f));

        plot.setRenderer(renderer);
        plot.setBackgroundPaint(Color.white);

        plot.setRangeGridlinesVisible(true);
        plot.setRangeGridlinePaint(Color.BLACK);

        plot.setDomainGridlinesVisible(true);
        plot.setDomainGridlinePaint(Color.BLACK);

        chart.getLegend().setFrame(BlockBorder.NONE);

        chart.setTitle(new TextTitle("",
                        new Font("Serif", Font.BOLD, 15)
                )
        );

        //setting dimensions for the chart
        return new ChartPanel(chart) {
            public Dimension getPreferredSize() {
                return new Dimension(270, 270);
            }
        };
    }

}
