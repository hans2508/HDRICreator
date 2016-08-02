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
public class JointPDF {

    private double[][] PDF_red;
    private double[][] PDF_green;
    private double[][] PDF_blue;

    public JointPDF(Mat imgR, Mat imgO) {
        int x, y;
        double count_red, count_green, count_blue, total_red = 0, total_green = 0, total_blue = 0;
        PDF_red = new double[256][256];
        PDF_green = new double[256][256];
        PDF_blue = new double[256][256];

        // Reference Image = x, Other Image = y
        // Make Joint Histogram
        for (int i = 0; i < imgR.rows(); i++) {
            for (int j = 0; j < imgR.cols(); j++) {
                double[] rgbR = imgR.get(i, j);
                double[] rgbO = imgO.get(i, j);

                // Search for Blue PDF
                y = (int) rgbO[0];
                x = (int) rgbR[0];
                PDF_blue[y][x] += 1;

                // Search for Green PDF
                y = (int) rgbO[1];
                x = (int) rgbR[1];
                PDF_green[y][x] += 1;

                // Search for Red PDF
                y = (int) rgbO[2];
                x = (int) rgbR[2];
                PDF_red[y][x] += 1;
            }
        }

//        System.out.println("ORIGINAL");
//        for (int i = 0; i < 256; i++) {
//            for (int j = 0; j < 256; j++) {
//                if (PDF_blue[i][j] > 0) {
//                    System.out.println("(" + i + "," + j + "):" + PDF_blue[i][j]);
//                }
//            }
//        }
        // Divide all pixel with Max number of pixel
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                count_blue = PDF_blue[i][j];
                count_green = PDF_green[i][j];
                count_red = PDF_red[i][j];

                if (count_blue != 0) {
                    PDF_blue[i][j] = count_blue / imgR.total();
                    total_blue += PDF_blue[i][j];
                }
                if (count_green != 0) {
                    PDF_green[i][j] = count_green / imgR.total();
                    total_green += PDF_green[i][j];
                }
                if (count_red != 0) {
                    PDF_red[i][j] = count_red / imgR.total();
                    total_red += PDF_red[i][j];
                }
            }
        }

        // Normalize all pixel so total sum pixel is equal to 1
        for (int i = 0; i < 256; i++) {
            for (int j = 0; j < 256; j++) {
                count_blue = PDF_blue[i][j];
                count_green = PDF_green[i][j];
                count_red = PDF_red[i][j];

                if (count_blue != 0) {
                    PDF_blue[i][j] = count_blue / total_blue;
                }
                if (count_green != 0) {
                    PDF_green[i][j] = count_green / total_green;
                }
                if (count_red != 0) {
                    PDF_red[i][j] = count_red / total_red;
                }
            }
        }
//        System.out.println("NORMALIZE");
//        for (int i = 0; i < 256; i++) {
//            for (int j = 0; j < 256; j++) {
//                if (PDF_red[i][j] > 0) {
//                    System.out.println("(" + i + "," + j + "):" + String.format("%.4f",PDF_red[i][j]));
//                }
//            }
//        }
    }

    public JointPDF(double[][] PDF_red, double[][] PDF_green, double[][] PDF_blue) {
        this.PDF_red = PDF_red;
        this.PDF_green = PDF_green;
        this.PDF_blue = PDF_blue;
    }

    public double[][] getPDF_red() {
        return PDF_red;
    }

    public void setPDF_red(double[][] PDF_red) {
        this.PDF_red = PDF_red;
    }

    public double[][] getPDF_green() {
        return PDF_green;
    }

    public void setPDF_green(double[][] PDF_green) {
        this.PDF_green = PDF_green;
    }

    public double[][] getPDF_blue() {
        return PDF_blue;
    }

    public void setPDF_blue(double[][] PDF_blue) {
        this.PDF_blue = PDF_blue;
    }
}
