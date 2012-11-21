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
    
    
/**
	  * Fetch the entire contents of a text file, and return it in a String.
	  * This style of implementation does not throw Exceptions to the caller.
	  *
	  * @param aFile is a file which already exists and can be read.
	  * @return String with the content.
	  */

	  static public String getContentsAsString(File aFile) {
		  
		
	    //...checks on aFile are elided
	    StringBuffer contents = new StringBuffer();

	    //declared here only to make visible to finally clause
	    BufferedReader input = null;
	    try {
	      //use buffering, reading one line at a time
	      //FileReader always assumes default encoding is OK!
	      input = new BufferedReader( new FileReader(aFile) );
	      String line = null; //not declared within while loop
	      /*
	      * readLine is a bit quirky :
	      * it returns the content of a line MINUS the newline.
	      * it returns null only for the END of the stream.
	      * it returns an empty String if two newlines appear in a row.
	      */
	      while (( line = input.readLine()) != null){
	        contents.append(line);
	        contents.append(System.getProperty("line.separator"));

	      }
	    }
	    catch (FileNotFoundException ex) {
	      ex.printStackTrace();
	    }
	    catch (IOException ex){
	      ex.printStackTrace();
	    }
	    finally {
	      try {
	        if (input!= null) {
	          //flush and close both "input" and its underlying FileReader
	          input.close();
	        }
	      }
	      catch (IOException ex) {
	        ex.printStackTrace();
	      }
	    }
	    return contents.toString();
	  }    


}
