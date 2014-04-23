/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package util.fileio;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tiago
 */
public class FileInput {


    public static String readFile(String path){

        StringBuffer content = new StringBuffer();

        FileReader file = null;
        BufferedReader br = null;
        try {
            file = new FileReader(path);            
            br = new BufferedReader(file);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileInput.class.getName()).log(Level.SEVERE, null, ex);
        }       

        String line = null;
        try {
            while ((line = br.readLine()) != null) {
                content.append(line + "\n");
            }
        } catch (IOException ex) {
            Logger.getLogger(FileInput.class.getName()).log(Level.SEVERE, null, ex);
        }

        return content.toString();
    }


}
