/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.util.ArrayList;
import model.*;

/**
 *
 * @author Hans CK
 */
public class HDRConstruction {

    private int w, h;
    private static int nImage = 3;
    private final int lamdaC = 7;
    private final int d = 1;
    private ArrayList<double[][]> list_low, list_high;
    private ImageSet set;
    private double[][] globalW_low, globalW_high;

    public HDRConstruction(ImageSet set, ArrayList<JointPDF> listPDF) {
        this.set = set;
        w = set.getM(1).width();
        h = set.getM(1).height();
        
        double[][] pdf_red, pdf_green, pdf_blue;

        // for condition 0 initialize
        pdf_red = listPDF.get(0).getPDF_red();
        pdf_green = listPDF.get(0).getPDF_green();
        pdf_blue = listPDF.get(0).getPDF_blue();
        list_low = new ArrayList<>();
        list_low.add(pdf_red);
        list_low.add(pdf_green);
        list_low.add(pdf_blue);

        // for condition 1 initialize
        pdf_red = listPDF.get(1).getPDF_red();
        pdf_green = listPDF.get(1).getPDF_green();
        pdf_blue = listPDF.get(1).getPDF_blue();
        list_high = new ArrayList<>();
        list_high.add(pdf_red);
        list_high.add(pdf_green);
        list_high.add(pdf_blue);
        
        calcGlobalIntensity();
    }

    public RadianceMap constructHDR() {

        double[][] tempR = new double[h][w];
        double[][] tempG = new double[h][w];
        double[][] tempB = new double[h][w];
        double[] sum, count, sumTot, countTot;
        int temp;

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double colorP[] = set.getM(1).get(y, x);
                sum = new double[3];
                count = new double[3];
                for (int j = 0; j < nImage; j++) {
                    double colorQ[] = set.getM(j).get(y, x);
                    int Zp = 0, Zq = 0;
                    for (int i = 0; i < 3; i++) {
                        switch (i) {
                            case 0:
                                Zp = (int) colorP[2];
                                Zq = (int) colorQ[2];
                                break;
                            case 1:
                                Zp = (int) colorP[1];
                                Zq = (int) colorQ[1];
                                break;
                            case 2:
                                Zp = (int) colorP[0];
                                Zq = (int) colorQ[0];
                                break;
                        }
                        double w = CameraResponse.getWeight(i, j, Zq, set.getMin(), set.getMax());
                        int cond = 0;
                        if (j == 2) {
                            cond = 1;
                        }
                        double c = calcColorWeight(cond, Zp, Zq);
                        sum[i] += (w * c * d * set.getrMap(j).getrMap_pixel(i, y, x));
                        count[i] += (w * c * d);
                    }
                }
                tempR[y][x] = sum[0] / count[0];
                tempG[y][x] = sum[1] / count[1];
                tempB[y][x] = sum[2] / count[2];
                if (Double.isNaN(tempR[y][x])) {
                    tempR[y][x] = 0;
                }
                if (Double.isNaN(tempG[y][x])) {
                    tempG[y][x] = 0;
                }
                if (Double.isNaN(tempB[y][x])) {
                    tempB[y][x] = 0;
                }
//                    System.out.print(String.format("%.2f",tempR[y][x]) + "," + String.format("%.2f",tempG[y][x]) + "," + String.format("%.2f",tempB[y][x]) +";");
            }
//            System.out.println("");
        }
        RadianceMap HDRmap = new RadianceMap(tempR, tempG, tempB);
        return HDRmap;
    }

    private double calcColorWeight(int cond, int p, int q) {
        double sum = 0;

        for (int color = 0; color < 3; color++) {
            sum += calcYHat(cond, p, q, color);
        }
        return Math.exp((sum * -1) / Math.pow(lamdaC, 2));
    }

    private double calcYHat(int cond, int p, int q, int color) {
        // condition 0 -> image 0&1 or 1&1, condition 1 -> image 1&2
        double yHat = 0;
        if (cond == 0) {
            yHat = p - globalW_low[q][color];
        } else if (cond == 1) {
            yHat = globalW_high[p][color] - q;
        }
        return yHat;
    }

    private void calcGlobalIntensity() {

        globalW_low = new double[256][3];
        globalW_high = new double[256][3];
  
        for (int Z = 0; Z < 256; Z++) {
            double[] sum_low = new double[3];
            double[] sum_high = new double[3];
            double[] count_low = new double[3];
            double[] count_high = new double[3];
            for (int x = 0; x < 256; x++) {
                for (int i = 0; i < 3; i++) {
                    // condition 0
                    sum_low[i] += list_low.get(i)[Z][x] * x;
                    count_low[i] += list_low.get(i)[Z][x];
                    
                    // condition 1
                    sum_high[i] += list_high.get(i)[x][Z] * x;
                    count_high[i] += list_high.get(i)[x][Z];
                }
            }
            for (int i = 0; i < 3; i++) {
                if (sum_low[i] > 0) {
                    globalW_low[Z][i] = sum_low[i] / count_low[i];
                } else {
                    globalW_low[Z][i] = 0;
                }
                if (sum_high[i] > 0) {
                    globalW_high[Z][i] = sum_high[i] / count_high[i];
                } else {
                    globalW_high[Z][i] = 0;
                }
            }
        }
//        for (int Z = 0; Z < 256; Z++) {
//            for (int i = 0; i < 3; i++) {
//                if (globalW_high[Z][i] > 0 && i == 2) {
//                    System.out.println(globalW_high[Z][i]);
//                }
//            }
//        }
    }
}
