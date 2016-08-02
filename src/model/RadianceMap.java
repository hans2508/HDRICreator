/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;


/**
 *
 * @author Hans CK
 */
public class RadianceMap {

    private double[][] rMap_red;
    private double[][] rMap_green;
    private double[][] rMap_blue;

    public RadianceMap(double[][] red, double[][] green, double[][] blue) {
        this.rMap_red = red;
        this.rMap_green = green;
        this.rMap_blue = blue;
    }

    public double getrMap_pixel(int i, int y, int x) {
        double lnE = 0;
        switch (i) {
            case 0:
                lnE = rMap_red[y][x];
                break;
            case 1:
                lnE = rMap_green[y][x];
                break;
            case 2:
                lnE = rMap_blue[y][x];
                break;
        }
        return lnE;
    }

    public double[][] getrMap_red() {
        return rMap_red;
    }

    public void setrMap_red(double[][] rMap_red) {
        this.rMap_red = rMap_red;
    }

    public double[][] getrMap_green() {
        return rMap_green;
    }

    public void setrMap_green(double[][] rMap_green) {
        this.rMap_green = rMap_green;
    }

    public double[][] getrMap_blue() {
        return rMap_blue;
    }

    public void setrMap_blue(double[][] rMap_blue) {
        this.rMap_blue = rMap_blue;
    }

}
