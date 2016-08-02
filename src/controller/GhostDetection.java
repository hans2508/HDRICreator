/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.JointPDF;
import org.opencv.core.Mat;

/**
 *
 * @author Hans CK
 */
public class GhostDetection {

    public static int[][] detect(Mat imgR, Mat imgO, JointPDF jointPDF) {

        double count_blue, count_green, count_red;
        final double threshold = 0.00001;
        int[][] ghostPixel = new int[imgO.rows()][imgO.cols()];

        // Tresholding
        for (int i = 0; i < imgR.rows(); i++) {
            for (int j = 0; j < imgR.cols(); j++) {
                double[] rgbR = imgR.get(i, j);
                double[] rgbO = imgO.get(i, j);

                count_blue = jointPDF.getPDF_blue()[(int) rgbO[0]][(int) rgbR[0]];
                count_green = jointPDF.getPDF_green()[(int) rgbO[1]][(int) rgbR[1]];
                count_red = jointPDF.getPDF_red()[(int) rgbO[2]][(int) rgbR[2]];

                if (count_blue < threshold || count_green < threshold || count_red < threshold) {
                    ghostPixel[i][j] = 0;
                } else {
                    ghostPixel[i][j] = 1;
                }
            }
        }
//        for (int i = 0; i < 3; i++) {
//            for (int j = 0; j < 5; j++) {
//                double ghost = ghostPixel[i][j];
//                System.out.print(ghost+";");
//            }
//            System.out.println("");
//        }
        return ghostPixel;
    }
}
