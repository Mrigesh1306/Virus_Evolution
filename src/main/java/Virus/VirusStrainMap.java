package Virus;

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
    static XYSeries series = new XYSeries("Infected People");
    static XYSeries series2 = new XYSeries("Recovered People");
    static XYSeries series3 = new XYSeries("Vaccinated People");

    //creating label to print the numbers
    static JLabel lbl = new JLabel();

    //map to continuously update the label data
    public static Map<String, Integer> data = new HashMap();


    public VirusStrainMap() throws IOException {

        XYDataset dataset = createDataset(series);
        XYDataset dataset2 = createDataset(series2);
        XYDataset dataset3 = createDataset(series3);
        ChartPanel chart = createChart(dataset, Color.RED);
        ChartPanel chart2 = createChart(dataset2, Color.GREEN);
        ChartPanel chart3 = createChart(dataset3, Color.BLACK);

        //diving the graphs and label in two panel for better presenattion
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JPanel mainPanel2 = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        JPanel p1 = new JPanel();
        chart.setBackground(Color.white);
        p1.add(chart);
        JPanel p2 = new JPanel();
        chart2.setBackground(Color.white);
        p2.add(chart2);
        JPanel p3 = new JPanel();
        chart3.setBackground(Color.white);
        p3.add(chart3);

        //setting the label font, size and border
        JPanel p4 = new JPanel();
        lbl.setFont(new Font(lbl.getFont().getName(), Font.PLAIN, 18));
        p4.setSize(300,250);
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

    public static XYSeries getSeries(){
        return series;
    }

    public static XYSeries getSeries2(){
        return series2;
    }

    public static XYSeries getSeries3(){
        return series3;
    }

    //creating DataSet from series
    private XYDataset createDataset(XYSeries s) {
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(s);
        return dataset;
    }

    //method to update the label data by reading map values
    public static void updateLbl(){
        String res = "<html>";
        for (String name: data.keySet()) {
            String key = name;
            String value = data.get(name).toString();
            res+=key + " " + value + "<br/>";
        }
        res+="</html>";
        lbl.setText(res);
    }

    //drawing the charts on the panel using this method
    private ChartPanel createChart(XYDataset dataset, Color c) {
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
                return new Dimension(250, 250);
            }
        };
    }

}
