/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import org.opencv.core.Mat;

/**
 *
 * @author Hans CK
 */
public class ImageSet {

    private final int n = 3;
    private Mat[] m;
    private double[] exposure;
    private RadianceMap[] rMap;
    private double[][] min;
    private double[][] max;

    public ImageSet() {
        this.m = new Mat[n];
        this.exposure = new double[n];
        this.rMap = new RadianceMap[n];
    }

    public Mat getM(int n) {
        return m[n];
    }

    public void setM(int n, Mat m) {
        this.m[n] = m;
    }

    public double getExposure(int n) {
        return exposure[n];
    }

    public void setExposure(int n, double exposure) {
        this.exposure[n] = Math.log(exposure);
    }

    public RadianceMap getrMap(int n) {
        return rMap[n];
    }

    public void setrMap(int n, RadianceMap rMap) {
        this.rMap[n] = rMap;
    }

    public double[][] getMin() {
        return min;
    }

    public void setMin(double[][] min) {
        this.min = min;
    }

    public double[][] getMax() {
        return max;
    }

    public void setMax(double[][] max) {
        this.max = max;
    }
}
