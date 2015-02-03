/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package src.util.fileio;

import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author tiago
 */
public class FileOutput {


    public static void saveFile(String path, String content){
        try {
            FileWriter file = new FileWriter(path);
            file.write(content);
            file.close();
        } catch (IOException e) {
        }
    }


}
