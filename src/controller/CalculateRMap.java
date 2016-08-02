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
public class CalculateRMap {

    private final int nImage = 3;
    private int w, h;
    private ImageSet set;
    private CameraResponse camera;
    private ArrayList<int[][]> listGhost;
    private RadianceMap combineRMap;

    public ImageSet getSet() {
        return set;
    }

    public CalculateRMap(ImageSet set, CameraResponse camera, ArrayList<int[][]> listGhost) {

        this.set = set;
        this.camera = camera;
        this.listGhost = listGhost;

        w = set.getM(1).width();
        h = set.getM(1).height();

        combineRMap();
        refineRMap();
    }

    public void combineRMap() {

        int ghost = 1;

        double[][] tempR = new double[h][w];
        double[][] tempG = new double[h][w];
        double[][] tempB = new double[h][w];

        for (int y = 0; y < h; y++) {
            for (int x = 0; x < w; x++) {
                int count[] = new int[3];
                double sum[] = new double[3];
                double lnE;
                for (int j = 0; j < nImage; j++) {
                    if (j == 0) {
                        ghost = listGhost.get(0)[y][x];
                    } else if (j == 1) {
                        ghost = 1;
                    } else if (j == 2) {
                        ghost = listGhost.get(1)[y][x];
                    }
                    if (ghost == 1) {
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
                            sum[i] += camera.getWeight(i, j, Zij, set.getMin(), set.getMax()) * (camera.getG(i, Zij) - set.getExposure(j));
                            count[i] += camera.getWeight(i, j, Zij, set.getMin(), set.getMax());
//                            if (y == 204 && x == 400) {
//                                System.out.println("COMBINE. " + y + " " + x + " j:" + j + " i:" + i + " Z:" + Zij + " w:" + camera.getWeight(i, j, Zij, set.getMin(), set.getMax())
//                                        + " sum:" + sum[i] + " count:" + count[i] + " lnE: " + (camera.getG(i, Zij) - set.getExposure(j)));
//                            }
                        }
                    }
                }
                for (int i = 0; i < 3; i++) {
                    lnE = sum[i] / count[i];
                    if (Double.isNaN(lnE)) {
                        lnE = 0;
                        //System.out.println("B. " + y + " " + x + " " + k + " " + sum[k] + " count:" + count[k] + " lnE:" + lnE);
                    }
                    switch (i) {
                        case 0:
                            tempR[y][x] = lnE;
                            break;
                        case 1:
                            tempG[y][x] = lnE;
                            break;
                        case 2:
                            tempB[y][x] = lnE;
                            break;
                    }
                }
//                System.out.print(String.format("%.2f",tempR[y][x]) + "," + String.format("%.2f",tempG[y][x]) + "," + String.format("%.2f",tempB[y][x])+";");
            }
//            System.out.println("");
        }
        
        combineRMap = new RadianceMap(tempR, tempG, tempB);
    }

    public void refineRMap() {

        int ghost = 1;

        for (int j = 0; j < nImage; j++) {
            double[][] tempR = new double[h][w];
            double[][] tempG = new double[h][w];
            double[][] tempB = new double[h][w];
            double count[][] = new double[256][3];
            double sum[][] = new double[256][3];
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    if (j == 0) {
                        ghost = listGhost.get(0)[y][x];
                    } else if (j == 1) {
                        ghost = 1;
                    } else if (j == 2) {
                        ghost = listGhost.get(1)[y][x];
                    }

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
                        sum[Zij][i] += ghost * combineRMap.getrMap_pixel(i, y, x);
                        count[Zij][i] += ghost;
                    }
                }
            }
            double[][] lnE = new double[256][3];
            for (int Z = 0; Z < 256; Z++) {
                for (int i = 0; i < 3; i++) {
                    if (sum[Z][i] == 0) {
                        lnE[Z][i] = 0;
                    } else {
                        lnE[Z][i] = sum[Z][i] / count[Z][i];
                    }
                }
            }
            for (int y = 0; y < h; y++) {
                for (int x = 0; x < w; x++) {
                    double color[] = set.getM(j).get(y, x);
                    for (int i = 0; i < 3; i++) {
                        int Zij = 0;
                        switch (i) {
                            case 0:
                                Zij = (int) color[2];
                                tempR[y][x] = lnE[Zij][i];
                                break;
                            case 1:
                                Zij = (int) color[1];
                                tempG[y][x] = lnE[Zij][i];
                                break;
                            case 2:
                                Zij = (int) color[0];
                                tempB[y][x] = lnE[Zij][i];
                                break;
                        }
                    }
                }
            }
            set.setrMap(j, new RadianceMap(tempR, tempG, tempB));
        }
    }
}
