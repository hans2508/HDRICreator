/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.swing.SwingUtilities;
import model.ImageSet;

public class CameraResponse {

    private final int nImage = 3;
    private int w, h;
    private double[][] G;
    private double[][] lnE;
    private ImageSet set;

    public ImageSet getSet() {
        return set;
    }

    public double getG(int i, int Z) {
        return G[i][Z];
    }

    public CameraResponse(ImageSet set) {
        this.set = set;
        this.w = set.getM(0).width();
        this.h = set.getM(0).height();

        calcMinMax();
        init();
        estimateLnE();
        calculateCRF();
    }

    private void init() {
        // initialize G for width and height = 256 (total intensity)
        G = new double[3][256];
        for (int i = 0; i < 3; i++) {
            for (int Z = 0; Z < 256; Z++) {
                G[i][Z] = Math.log(Z);
            }
        }
    }

    private void calculateCRF() {
        // initialize sums and counts for each Z value
        double[][] sumG = new double[nImage][256];
        int[][] count = new int[nImage][256];
        for (int i = 0; i < 3; i++) {
            for (int Z = 0; Z < 256; Z++) {
                sumG[i][Z] = 0;
                count[i][Z] = 0;
            }
        }

        // collect data
        int factor = 5;
        for (int j = 0; j < nImage; j++) {
            for (int y = 0; y < h; y += factor) {
                for (int x = 0; x < w; x += factor) {
                    double color[] = set.getM(j).get(y, x);
                    for (int i = 0; i < 3; i++) {
                        int Zij = 0;
                        switch (i) {
                            case 0:
                                Zij = (int) color[2];
                                break;
                            case 1:
                                Zij = (int) color[1];
                                break;
                            case 2:
                                Zij = (int) color[0];
                                break;
                        }
                        if (lnE[y][x] > 0) {
                            sumG[i][Zij] += lnE[y][x] + set.getExposure(j);
                            count[i][Zij] += 1;
                        }
                    }
                }
            }
        }

        // calculate final G values
        for (int i = 0; i < 3; i++) {
            for (int Z = 0; Z < 256; Z++) {
                if (count[i][Z] > 0) {
                    G[i][Z] = (sumG[i][Z] / count[i][Z]);
                }
            }
        }
    }

    private void estimateLnE() {

        lnE = new double[h][w];
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int count = 0;
                double sum = 0;

                // Calculate combine lnE for all pixel in all image and all channel
                for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < nImage; j++) {
                        double color[] = set.getM(j).get(y, x);
                        int Zij = 0;
                        switch (i) {
                            case 0:
                                Zij = (int) color[2];
                                break;
                            case 1:
                                Zij = (int) color[1];
                                break;
                            case 2:
                                Zij = (int) color[0];
                                break;
                        }
                        if (Zij > 0)
                        {
                            sum += getWeight(i, j, Zij, set.getMin(), set.getMax()) * (G[i][Zij] - set.getExposure(j));
                            count += getWeight(i, j, Zij, set.getMin(), set.getMax());
                        }
                    }
                }
                lnE[y][x] = sum / count;
            }
        }
    }

    private void calcMinMax() {

        double[][] min = new double[3][3];
        double[][] max = new double[3][3];
        for (int j = 0; j < 3; j++) {
            for (int i = 0; i < 3; i++) {
                min[j][i] = 255;
            }
        }

        for (int j = 0; j < 3; j++) {
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    double color[] = set.getM(j).get(y, x);
                    for (int i = 0; i < 3; i++) {
                        switch (i) {
                            case 0:
                                if (color[2] < min[j][i]) {
                                    min[j][i] = color[2];
                                }
                                if (color[2] > max[j][i]) {
                                    max[j][i] = color[2];
                                }
                                break;
                            case 1:
                                if (color[1] < min[j][i]) {
                                    min[j][i] = color[1];
                                }
                                if (color[1] > max[j][i]) {
                                    max[j][i] = color[1];
                                }
                                break;
                            case 2:
                                if (color[0] < min[j][i]) {
                                    min[j][i] = color[0];
                                }
                                if (color[0] > max[j][i]) {
                                    max[j][i] = color[0];
                                }
                                break;
                        }
                    }
                }
            }
        }
        set.setMin(min);
        set.setMax(max);
    }

    public static double getWeight(int i, int j, int Z, double[][] min, double[][] max) {
        double x = 0.5 * (min[j][i] + max[j][i]);
        double Wz = 0;
        if (Z <= x) {
            Wz = Z - min[j][i];
        } else if (Z > x) {
            Wz = max[j][i] - Z;
        }
        return Wz;
    }

    public void drawCRF() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new DrawCurve(G).setVisible(true);
            }
        });
    }

}
