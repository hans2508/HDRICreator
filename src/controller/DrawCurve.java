/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 *
 * @author Hans CK
 */
public class DrawCurve extends JFrame {

    double[][] G;

    public DrawCurve(double[][] G) {
        super("HDRI Creator");

        this.G = G;
        JPanel chartPanel = createChart();
        add(chartPanel, BorderLayout.CENTER);

        setSize(640, 480);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
    }

    private JPanel createChart() {
        String chartTitle = "Camera Response Curve";
        String xAxisLabel = "log Exposure G(Z)";
        String yAxisLabel = "Intensity pixel Z";

        XYDataset dataset = createDataset();

        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle,
                xAxisLabel, yAxisLabel, dataset);

        //Set custom color and thickness for line curve
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, false);

        // sets paint color for each series
        renderer.setSeriesPaint(0, Color.RED);
        renderer.setSeriesPaint(1, Color.GREEN);
        renderer.setSeriesPaint(2, Color.BLUE);

        // sets thickness for series (using strokes)
        renderer.setSeriesStroke(0, new BasicStroke(1.5f));
        renderer.setSeriesStroke(1, new BasicStroke(1.5f));
        renderer.setSeriesStroke(2, new BasicStroke(1.5f));

        XYPlot plot = chart.getXYPlot();
        plot.setRenderer(renderer);

        return new ChartPanel(chart);
    }

    private XYDataset createDataset() {
        XYSeriesCollection dataset = new XYSeriesCollection();
        XYSeries series1 = new XYSeries("Red", false);
        XYSeries series2 = new XYSeries("Green", false);
        XYSeries series3 = new XYSeries("Blue", false);

        for (int Z = 0; Z < 256; Z++) {
            for (int i = 0; i < 3; i++) {
                if (i == 0) {
                    series1.add(G[i][Z], Z);
                } else if (i == 1) {
                    series2.add(G[i][Z], Z);
                } else {
                    series3.add(G[i][Z], Z);
                }
            }
        }

        dataset.addSeries(series1);
        dataset.addSeries(series2);
        dataset.addSeries(series3);

        return dataset;
    }
}
