/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007,2008 Universidade de Sao Paulo
* 

* This file is part of itSIMPLE.
*
* itSIMPLE is free software: you can redistribute it and/or modify
* it under the terms of the GNU General Public License as published by
* the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version. Other licenses might be available
* upon written agreement.
* 
* itSIMPLE is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU General Public License for more details.
* 
* You should have received a copy of the GNU General Public License
* along with itSIMPLE.  If not, see <http://www.gnu.org/licenses/>.
* 
* Authors:	Tiago S. Vaquero, 
*		 	Victor Romero.
**/

package src.planning;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import org.jdom.Element;

public class CallPlan {
	
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
	
	/**
	  * Fetch the entire contents of a text file, and return it in a List of String.
	  * This style of implementation does not throw Exceptions to the caller.
	  *
	  * @param aFile is a file which already exists and can be read.
	  * @return ArrayList<String> with the content
	  */
	  static public ArrayList<String> getContentsAsList(File aFile) {
		  
			
		    //...checks on aFile are elided
		  	ArrayList<String> contents = new ArrayList<String>();

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
		        contents.add(line);
		        //contents.append(System.getProperty("line.separator"));
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
		    return contents;
		  }

	/**
	 * @param args
	 * @throws IOException 
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws IOException, InterruptedException {
		
		
		List<String> consoleOutput = new ArrayList<String>();
		List<String> output = new ArrayList<String>();
		List<String> result = new ArrayList<String>();
		String outputStr = "";
		String status = "";
    	String[] command = new String[5];
		
		String domain = "oil_domain.pddl";
		
		
		
		for (int i = 1; i < 40; i++) {
			String problem = "oil_problem" + i +".pddl";
			

			//Prepare the command line 		
			command[0]= "myPlanners/mips-xxl";
			//command[0]= "myPlanners/sgplan522";
			command[1]= "-o";
			//command[2]= "/resources/planners/domain.pddl"; //caminho e nome do dominio
			command[2]= "/root/Planning/Domains/OilSupplyDomain/"+domain;
			command[3]= "-f";
			//command[4]= "resources/planners/problem.pddl";
			command[4]= "/root/Planning/Domains/OilSupplyDomain/"+problem;

	      	           	
	      	//set inicial time
	      	double start_time = System.currentTimeMillis();
	    	//Call the planner     
	    	Process process = Runtime.getRuntime().exec(command); 

	/*    	Scanner sc = new Scanner(process.getInputStream());            		                    
	    	//Get the planner answer exposed in the console
	    	if (consoleOutput != null) {
	            while (sc.hasNextLine()) {
	            	consoleOutput.add(sc.nextLine());
				}
			}                
	        sc.close();*/                
	        process.waitFor();           
	        process.destroy();
	        

	        //Calculate the time
	        double end_time = System.currentTimeMillis() - start_time;
	        // Must divide per 1000 (time/1000) in order to get the time in seconds.
	        end_time = end_time/1000; //time in seconds
	        
	        
	        
	        /**
	         * LIMPANDO OS ARQUIVOS QUE O MIPS-XXL GERA
	         */
			        
			        //Get the planner answer exposed in the solution Output File
			        File outputFile = new File("ffSolution.soln");
			        
			        if (outputFile.exists()) {
			        	//Get output
			        	output = getContentsAsList(outputFile);
			        	outputStr = getContentsAsString(outputFile);
			
			        	//remove output solution file (only if the plan create it)
			            outputFile.delete();
			            //TODO check permission
					}else{
						System.out.println("Could not find the planner output solution file! \n");
					}
			        
			        // delete additional generated files 
			        File file = new File("ffPSolution.soln");	
					if(file.exists())
						// delete the file
						file.delete();
					
	        /**
	         * FIM DA LIMPEZA DOS ARQUIVOS QUE O MIPS-XXL GERA
	         */		
			
			
			
			//Print the time spent
			
			
	        
	        if (outputStr.trim().equals("")){
	        	status = "X";
	        }else{
	        	status = "OK";
	        }
	        
	        //System.out.println("Time: "+ end_time);
	        //Print the output (the solution). If its empty, it probaly didnt find any solution
			//System.out.println(outputStr);
//			System.out.println(output.toString());
	        String line = problem + "	" + end_time + "	" + "OK";
			result.add(line);
			System.out.println(line);
		}
		
		//PRINT THE RESULT
		System.out.println(" ");
		System.out.println("Final Result:");
		
		for (int i = 0; i < result.size(); i++) {
			String line = result.get(i);
			System.out.println(line);
		}
		

	}

}
