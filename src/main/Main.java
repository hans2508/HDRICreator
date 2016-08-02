/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.io.IOException;
import view.MainView;

/**
 *
 * @author Hans CK
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        
        System.load (new File("lib/opencv_java300.dll").getAbsolutePath());
        MainView mainVew = new MainView();
        mainVew.setVisible(true); 
    }

}
