/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.*;
import org.opencv.core.Mat;

/**
 *
 * @author Hans CK
 */
public class ToneMapping {

    private int w, h;
    private RadianceMap hdrMap;
    private double[] min, max;
    private double[][] L, Ltm, Lraw;
    private final double a;
    private final double delta = 0.000000001;

    public ToneMapping(ImageSet set, RadianceMap hdrMap, double a) {
        this.hdrMap = hdrMap;
        this.a = a / 100;
        w = set.getM(1).width();
        h = set.getM(1).height();
    }

    private void getLuminance() {

        double[][] tempR = new double[h][w];
        double[][] tempG = new double[h][w];
        double[][] tempB = new double[h][w];
        Lraw = new double[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double red = Math.exp(hdrMap.getrMap_pixel(0, y, x)) * 0.2125;
                double green = Math.exp(hdrMap.getrMap_pixel(1, y, x)) * 0.7154;
                double blue = Math.exp(hdrMap.getrMap_pixel(2, y, x)) * 0.0721;
                double sum = red + green + blue;
                Lraw[y][x] = sum;
            }
        }
    }

    private double getMax(double[][] Lraw) {
        double max = Lraw[0][0];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                max = Math.max(Lraw[i][j], max);
            }
        }
        return max;
    }

    private double getMin(double[][] Lraw) {
        double min = Lraw[0][0];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                min = Math.min(Lraw[i][j], min);
            }
        }
        return min;
    }

    private double[][] mapToRange(double[][] Lraw) {

        double max = getMax(Lraw);
        double min = getMin(Lraw);
        double[][] Ltemp = new double[h][w];
        for (int i = 0; i < h; i++) {
            for (int j = 0; j < w; j++) {
                Ltemp[i][j] = (Lraw[i][j] - min) / (max - min);
            }
        }
        return Ltemp;
    }

    private double calcLumAvg() {

        L = mapToRange(Lraw);
        double sum = 0;
        double N = w * h;
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                sum += Math.log(L[y][x] + delta);
            }
        }
        double Lavg = Math.exp(sum / N);
        return Lavg;
    }

    private RadianceMap toneMapped(double Lavg) {

        double[][] tempR = new double[h][w];
        double[][] tempG = new double[h][w];
        double[][] tempB = new double[h][w];
        Ltm = new double[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                double Ls = (a / Lavg) * L[y][x];
                Ltm[y][x] = Math.round((Ls / (1 + Ls)) * 255);
                tempR[y][x] = Ltm[y][x];
                tempG[y][x] = Ltm[y][x];
                tempB[y][x] = Ltm[y][x];
            }
        }
        RadianceMap temp = new RadianceMap(tempR, tempG, tempB);
        return temp;
    }

    public Mat reinhardTMO() {

        getLuminance();
        double Lavg = calcLumAvg();
        RadianceMap temp = toneMapped(Lavg);

        double[] newValue = new double[3];
        Mat hdri = new Mat(h, w, 16);
        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                for (int i = 0; i < 3; i++) {
                    double value = temp.getrMap_pixel(i, y, x);
                    newValue[i] = value;
                }
//                System.out.print(newValue[0] + "," + newValue[1] + "," + newValue[2]+";");
                hdri.put(y, x, newValue[2], newValue[1], newValue[0]);
            }
//            System.out.println("");
        }
        return hdri;
    }
}
