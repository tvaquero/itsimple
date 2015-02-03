/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2012 University of Sao Paulo
* 
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



import java.util.logging.Level;
import java.util.logging.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

import javax.swing.JLabel;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

import src.gui.ItSIMPLE;
import src.gui.PlanNavigationList;
import src.languages.xml.XMLUtilities;

public class ExecPlanner implements Runnable{
	
	/**
	 * Planner execution time in milliseconds. Time used to solve the problem or give an answer. 
	 */
	private double time = 0;
	
	private String toolMessage = "";
	
	private Process process;
	
	private Element chosenPlanner;
	private String domainFile;
	private String problemFile;
	private String domainName = "";
	private String problemName = "";
        private String projectName = "";

	
	private Element XMLDomain = null;
	private Element XMLProblem = null;
	
	private boolean replaning;
        private boolean showReport = true;
        private Element thePlan = null;
        private File plannerRunFile = null;



	
	public ExecPlanner(){
		this(null, null, null, false);
	}
	
	public ExecPlanner(Element chosenPlanner, String domainFile, String problemFile, boolean replaning){
		this.chosenPlanner = chosenPlanner;
		this.domainFile = domainFile;
		this.problemFile = problemFile;
		this.replaning = replaning;
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
     * This method creates an empty plan just with data from planner
     */
    public void setEmptyPlan(){
        thePlan = null;

        //1. Get the default plan (empty)
		try {
			thePlan = XMLUtilities.readFromFile("resources/planners/DefaultPlan.xml")
					.getRootElement();
		} catch (Exception e) {
			e.printStackTrace();
        }

        //2 set the project, domain and problem names
        thePlan.getChild("project").setText(projectName);
        thePlan.getChild("domain").setText(domainName);
        thePlan.getChild("problem").setText(problemName);


        //3. set the planner info
        if (chosenPlanner != null){
            Element planner = thePlan.getChild("planner");
            //3.1 set the planner id
            planner.setAttribute("id", chosenPlanner.getAttributeValue("id"));

            //3.2 add planner's characteristics
            planner.addContent(chosenPlanner.cloneContent());
            planner.removeContent(planner.getChild("settings")); //except for setting

        }

    }

		
	public void getPlanAndStatistics(ArrayList<String> Output, ArrayList<String> Plan, ArrayList<String> Statistics){
		//Separate statistics and plan (get plan)
        if (Output != null) {
			for (Iterator<?> iter = Output.iterator(); iter.hasNext();) {
				String element = (String) iter.next();
				//System.out.println(element);
				if (!element.trim().equals("")) {
					//get plan
					if(element.trim().startsWith(";")){
                        Statistics.add(element.trim().substring(1).trim());
					}else{
						// if it is not a standard action then check if is still an action or a statistic
						if (!(element.indexOf(":") > -1)){
                            boolean isAnAction = false;

                            //check if the string can still be an action (e.g. 1 (action p1 p2 ... pn) )
                            if ((element.indexOf("(") > -1) &&  (element.indexOf(")") > -1) ){
                                //check if the first element on the string is the plan index
                                StringTokenizer st = new StringTokenizer(element.trim());
                                String firstItem = "index";
                                if (st.hasMoreTokens()){
                                    firstItem = st.nextToken();                               
                                    try {
                                        double theIndex = Double.parseDouble(firstItem);
                                        isAnAction = true;
                                    } catch (Exception e) {
                                        isAnAction = false;
                                    }
                                }
                                
                                //if it is an action the include the ":" for standarlization
                                if (isAnAction){
                                    String actionBody = "";
                                    while(st.hasMoreTokens()){
                                    // for each parameter, create a node
                                        actionBody += st.nextToken() + " ";
                                    }
                                    element = firstItem + ": " + actionBody;
                                }
                                
                            }

                            if (isAnAction){
                                Plan.add(element.trim());
                            }else{
                                Statistics.add(element.trim());
                            }

							
						}else{//When it is really an action
							Plan.add(element.trim());	
						}
							
					}
				}	
			}
		}
	}
	
	public ArrayList<String> getPlan(ArrayList<String> Output){
		//Separate statistics and plan (get plan)
		ArrayList<String> plan = new ArrayList<String>();
        if (Output != null) {
			for (Iterator<?> iter = Output.iterator(); iter
					.hasNext();) {
				String element = (String) iter.next();
				//System.out.println(element);
				if (!element.trim().equals("")) {
					//get plan
					if(!element.trim().startsWith(";")){
						plan.add(element.trim());
					}
				}	
			}
		}
		return plan;
	}
	
	  
    public ArrayList<String> getPlannerOutput(Element chosenPlanner, String domain, String problem, ArrayList<String> consoleOutput) 
    {
    	ArrayList<String> output = null;
    	
		//Used to know the current OS
    	//System.out.println(System.getProperty("os.name"));

    	//String domain = "resources/planners/domain.pddl";
    	//String problem = "resources/planners/problem.pddl";    	
    	String solutionFile =  "solution.soln";
    	
    	
    	//1.Get main planner's parameters and arguments
    	Element settings = chosenPlanner.getChild("settings");
    	ArrayList<String> commandArguments = new ArrayList<String>();
    	
    	//1.0 Get planner execution file
    	commandArguments.add(settings.getChildText("filePath"));

        String plannerFile = settings.getChildText("filePath");
        //System.out.println(plannerFile);
        File f = new File(plannerFile);
        plannerRunFile = f;
        boolean plannerFileExists = true;
        if (!f.exists()){
            plannerFileExists = false;
            toolMessage += ">> Could not find selected planner '"+ plannerFile +"'. \n" +
                    ">> Please download and copy it in the folder /myPlanners \n";
            ItSIMPLE.getInstance().appendOutputPanelText(toolMessage);
        }    	

        if (plannerFileExists){ //proceed only if planner file exists

        	/*
        	//GET DOMAIN AND PROBLEM FIRST AND THEN ADDITIONAL ARGUMENTS
            //1.1 Get domain arguments
            Element domainElement = settings.getChild("arguments").getChild("domain");
            if (!domainElement.getAttributeValue("parameter").trim().equals("")){
                commandArguments.add(domainElement.getAttributeValue("parameter"));
            }
            commandArguments.add(domain); //domain path

            //1.2 Get problem arguments
            Element problemElement = settings.getChild("arguments").getChild("problem");
            if (!problemElement.getAttributeValue("parameter").trim().equals("")){
                commandArguments.add(problemElement.getAttributeValue("parameter"));
            }
            commandArguments.add(problem); //problem path

            //1.3 Get additional arguments
            List<?> additionalArgs = null;
            try {
                XPath path = new JDOMXPath("arguments/argument[enable='true']");
                additionalArgs = path.selectNodes(settings);
            } catch (JaxenException e1) {
                e1.printStackTrace();
            }
            if (additionalArgs != null) {
                if (additionalArgs.size() > 0) {
                    for (Iterator<?> iter = additionalArgs.iterator(); iter.hasNext();) {
                        Element argument = (Element) iter.next();
                        //System.out.println(argument.getChildText("name"));
                        if (!argument.getAttributeValue("parameter").trim().equals("")) {
                            commandArguments.add(argument.getAttributeValue("parameter"));
                        }
                        //if there is a value for the argument then add to the command
                        if (!argument.getChildText("value").trim().equals("")) {
                            commandArguments.add(argument.getChildText("value").trim());
                        }
                    }
                }
            }            
            */
        	
        	//GET ALL ARGUMENTS (including domain and problem) in the specified ORDER
        	//This makes planners have arguments before and after to the domain and problem arguments
        	//1.1 Get all arguments
        	List<?> theArgs = null;
            try {
                XPath path = new JDOMXPath("arguments/*[enable='true']");
                theArgs = path.selectNodes(settings);
            } catch (JaxenException e1) {
                e1.printStackTrace();
            }
            if (theArgs != null) {
                if (theArgs.size() > 0) {
                    for (Iterator<?> iter = theArgs.iterator(); iter.hasNext();) {
                        Element argument = (Element) iter.next();
                                                
                        //System.out.println(argument.getChildText("name"));
                        if (!argument.getAttributeValue("parameter").trim().equals("")) {
                            commandArguments.add(argument.getAttributeValue("parameter"));
                        }
                        //check if this is a domain argument
                        if (argument.getName().equals("domain")){
                        	commandArguments.add(domain); //domain path
                        }
                        //check if this is a problem argument                        
                        else if (argument.getName().equals("problem")){
                        	commandArguments.add(problem); //problem path                        	
                        }
                        //if there is a value for the argument then add to the command
                        else if (!argument.getChildText("value").trim().equals("")) {
                            commandArguments.add(argument.getChildText("value").trim());
                        }
                    }
                }
            }
        	
        	

            //1.4 Get output arguments
            boolean OutputFile;
            Element outputElement = settings.getChild("output");
            if (outputElement.getAttributeValue("hasOutputFile").equals("true")) {
                OutputFile = true;
                solutionFile = outputElement.getChild("outputFile").getChildText("fileName").trim();
                if (outputElement.getChild("outputFile").getChild("argument").getAttributeValue("needArgument").equals("true")) {
                    commandArguments.add(outputElement.getChild("outputFile").getChild("argument").getAttributeValue("parameter"));
                    commandArguments.add(solutionFile); //problem path
                }
            }else{
                OutputFile = false;
            }

            //System.out.println(commandArguments);
            try {


                //Prepare the command line
                String[] command = new String[commandArguments.size()];
                //System.out.println(commandArguments);

                for (int i = 0; i < commandArguments.size(); i++) {
                        command[i] = commandArguments.get(i);
                        //System.out.println(command[i]);
                }


                this.time = 0;
                //set initial time
                double start_time = System.currentTimeMillis();

                ItSIMPLE.getInstance().appendOutputPanelText("\n>> Calling planner "+ chosenPlanner.getChildText("name")+ "\n ");
                //Call the planner
                boolean gotError = false;
                try {
                    process = Runtime.getRuntime().exec(command);
                } catch (Exception e) {
                    String message = "## Error while running the planner "+ chosenPlanner.getChildText("name")+ ". Please check the planner's executable file, permissions, and operating system compatibility. \n";
                    System.out.println(message);
                    toolMessage += message;
                    ItSIMPLE.getInstance().appendOutputPanelText(message);
                    gotError = true;
                }
                //process = Runtime.getRuntime().exec(command);


                //check if there is a error while running the planner
                if (!gotError){

                    Scanner sc = new Scanner(process.getInputStream());
                    //Get the planner answer exposed in the console
                    //String ongoingConsole = "<html><body><font size=4 face=courier>";
                    if (consoleOutput != null) {
                        while (sc.hasNextLine()) {
                            //consoleOutput.add(sc.nextLine());
                            String line = sc.nextLine();
                            consoleOutput.add(line);
                            //System.out.println(line);
                            

                            //ongoingConsole += line + "<br>";
                            //ItSIMPLE.getInstance().setPlanInfoPanelText(ongoingConsole);
                            //ItSIMPLE.getInstance().setOutputPanelText(ongoingConsole);
                            ItSIMPLE.getInstance().appendOutputPanelText(line + "\n");
                        }
                    }
                    sc.close();
                    process.waitFor();
                    process.destroy();

                    ItSIMPLE.getInstance().appendOutputPanelText("\n>> Planner's output read. \n");


                    this.time = System.currentTimeMillis() - start_time;
                    // Must divide per 1000 (time/1000) in order to get the time in seconds.



                    if (OutputFile){  //The planner does provide a output file

                            //Checks if the planner put some automatic string in the output file name (i.e., .SOL)
                            if (!outputElement.getChild("outputFile").getChildText("fileNameAutomaticIncrement").trim().equals("")) {
                                    solutionFile = solutionFile + outputElement.getChild("outputFile").getChildText("fileNameAutomaticIncrement").trim();
                            }

                            //Get the planner answer exposed in the solution Output File
                            File outputFile = new File(solutionFile);

                            if (outputFile.exists()) {
                                //Get output
                                output = getContentsAsList(outputFile);

                                    //remove output solution file (only if the plan create it)
                                outputFile.delete();
                                //TODO check permission
                            }else{
                                toolMessage += "Could not find the planner output solution file! \n";
                                                    //System.out.println(toolMessage);
                            }

                            // delete additional generated files
                            List<?> generatedFiles = chosenPlanner.getChild("settings").getChild("output").getChild("outputFile")
                                    .getChild("additionalGeneratedFiles").getChildren("fileName");
                            for (Iterator<?> iter = generatedFiles.iterator(); iter.hasNext();) {
                                    Element generatedFile = (Element) iter.next();
                                    File file = new File(generatedFile.getText());
                                    if(file.exists()){
                                        // delete the file
                                        file.delete();
                                    }
                            }


                    }else{  //The planner does not provide a output file, just the console message

                            String planStartIdentifier = outputElement.getChild("consoleOutput").getChildText("planStartIdentifier");
                            int startsAfterNlines = Integer.parseInt(outputElement.getChild("consoleOutput").getChild("planStartIdentifier").getAttributeValue("startsAfterNlines"));
                            String planEndIdentifier = outputElement.getChild("consoleOutput").getChildText("planEndIdentifier");
                            // testing

                            ArrayList<String> planList = new ArrayList<String>();
                            ArrayList<String> statistics = new ArrayList<String>();

                            Boolean isThePlan = false;

                            //System.out.println(planStartIdentifier + ", " + startsAfterNlines + ", " + planEndIdentifier);
                            for (Iterator<?> iter = consoleOutput.iterator(); iter.hasNext();) {
                                String line = (String)iter.next();

                                //Check if line contains start identifier (only if the plan was not found yet)
                                int indexPlanStart = -1;
                                if(!isThePlan){
                                    indexPlanStart = line.indexOf(planStartIdentifier);
                                }

                                if (!isThePlan && indexPlanStart > -1) {//The plan was found
                                    isThePlan = true;
                                    //Jump the necessary lines to reach the first line of the plan
                                    if (startsAfterNlines == 0) {//First action is in the same line as the idetifier.
                                        line = line.substring(indexPlanStart + planStartIdentifier.length(), line.length());
                                        //System.out.println("First line for nlines 0: " +line);
                                    }
                                    else if (startsAfterNlines > 0) {//Jump to the first line of the plan
                                        for (int i = 0; i < startsAfterNlines; i++){
                                            line = (String)iter.next();
                                        }
                                    }
                                    //System.out.println("The plan stats here!");
                                }
                                //The plan ended
                                else if (isThePlan && ((!planEndIdentifier.trim().equals("") && line.trim().indexOf(planEndIdentifier)  > -1) || line.trim().equals(""))){
                                    isThePlan = false;
                                    //System.out.println("The plan ends here!");
                                }

                                //capturing the plan
                                if (isThePlan){

                                    if (line.trim().startsWith(";")){
                                        statistics.add(line.trim());
                                    }else{
                                        //System.out.println("Got it: " + line.trim());
                                        String mline = line;
                                        if (line.indexOf("(") == -1){//checking if it is in a pddl format about Parentheses
                                             //if it is not in pddl format just add "(" after ":" and ")" at the end of the line
                                            int indexOfDoubleDot = line.indexOf(":");
                                            mline = line.substring(0, indexOfDoubleDot + 2) + "(" +
                                                    line.substring(indexOfDoubleDot + 2, line.length()) + ")";
                                        }
                                        if (line.indexOf("[") == -1){//checking if it is in a pddl format about "[" - action duration
                                             //assume duration equals to 1
                                            mline = mline + " [1]";
                                        }
                                        line = mline;
                                        planList.add(line.trim());
                                    }

                                }
                                else if (line.trim().startsWith(";")){
                                    statistics.add(line.trim());
                                }

                             }


                         if (statistics.size() > 0 || planList.size() > 0){
                             output = new ArrayList<String>();
                             if (statistics.size() > 0) {output.addAll(statistics); output.add("");}
                             if (planList.size() > 0) output.addAll(planList);
                         }

                         //if (output != null)
                         //for (Iterator<?> iter = output.iterator(); iter.hasNext();) {
                         //    String planline = (String)iter.next();
                         //    System.out.println(planline);
                         //}

        /*        		//Prepare the command line
                        String[] command = {plannerPath, "-o", domain, "-f", problem};

                        Process process = Runtime.getRuntime().exec(command);
                        Scanner sc = new Scanner(process.getInputStream());

                        //TODO get first the console answer an then find the keyword


                        //Get all the console answer
                        ArrayList<String> plannerAnswer = new ArrayList<String>();
                        while (sc.hasNextLine()) {
                            plannerAnswer.add(sc.nextLine());
                        }
                        //System.out.println(plannerAnswer);

                        if (consoleOutput != null) {
                            consoleOutput = plannerAnswer;
                        }

                        //Extract the plan starting from a keyword
                        String keyword = "step";
                        String steps = sc.findWithinHorizon(keyword, 0);
                        if (steps != null) {
                            String result = "";
                            while (sc.hasNextLine()) {
                                result = sc.nextLine().trim();
                                if (result.equals("")) break;
                                output.add(result);
                            }
                        }else{
                            //TODO show the planner answer
                            System.out.println("The planner could not solve the problem!");
                        }

                        if (plannerAnswer.size() > 0) {
                            output = plannerAnswer;
                        }

                        sc.close();
                        process.waitFor();	*/
                    }
                    
                    
                    
                    
                }

            }
            catch (InterruptedException ie) {
                //ie.printStackTrace();
                // do nothing
            }
            catch(Exception e){
                e.printStackTrace();
            }
            
        }
  
        
        return output;
    }
    

    
    
    /**
     * This method parses the lines of a plan in text format to a XML structure
     * @param plan
     * @return the plan XML structure
     */
    private void parsePlanToXML(Element planNode, List<?> plan){
    	/*<action id="UNSTACK">
			<parameters>
				<parameter id="H1"/>
				<parameter id="A"/>
				<parameter id="B"/>
				<parameter id="TABLE1"/>
			</parameters>
			<startTime/>
			<duration/>
			<notes/>
		</action>*/
    	
    	//Element xmlPlan = thePlan;
        //Element xmlPlan = null;

        //if (xmlPlan == null){
        if (thePlan == null){
            try {
                //xmlPlan = XMLUtilities.readFromFile("resources/planners/DefaultPlan.xml").getRootElement();
                thePlan = XMLUtilities.readFromFile("resources/planners/DefaultPlan.xml").getRootElement();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        
        if(thePlan != null){
			for (Iterator<?> iter = plan.iterator(); iter.hasNext();) {
				String line = (String) iter.next();

				Element action = new Element("action");
				
				//System.out.println(line);
				
				String actionInstance = line.substring(line.indexOf('(')+1, line
						.lastIndexOf(')'));
				StringTokenizer st = new StringTokenizer(actionInstance);

				// the first token is the action name
				String actionName = st.nextToken();
				action.setAttribute("id", actionName.toUpperCase());

				// the other tokens are the parameters
				Element parameters = new Element("parameters");
				while (st.hasMoreTokens()) {
					String parameterStr = st.nextToken();
					Element parameter = new Element("parameter");
					parameter.setAttribute("id", parameterStr.toUpperCase());
					parameters.addContent(parameter);
				}
				action.addContent(parameters);

				// set the startTime name
				String startTimeStr = line.substring(0, line.indexOf(':'));
				Element startTime = new Element("startTime");
				startTime.setText(startTimeStr);
				action.addContent(startTime);

				// set the action duration
                String durationStr = "1";
                if (line.indexOf('[') > - 1){
                    durationStr = line.substring(line.indexOf('[')+1, line.lastIndexOf(']'));
                }
				Element duration = new Element("duration");
				duration.setText(durationStr);
				action.addContent(duration);
				
				// add the notes node
				Element notes = new Element("notes");
				action.addContent(notes);

				// add the action to the plan node
				planNode.addContent(action);
				
				

			}
		}    	

		//XMLUtilities.printXML(planNode);
    }
    
	private void parseStatisticsToXML(Element statisticNode, List<?> statistic) {
		
		//1. set the tool time, i. e., the total time seen by itSIMPLE
        
        //if (statisticNode.getChild("toolTime").getText().trim().equals("")){ //if nobady has already put something on the timeTool write it
            statisticNode.getChild("toolTime").setText(String.valueOf(time/1000));
        //}
		
		
		/*	<statistics>
		<toolTime/>
		<time/>
		<parsingTime/>
		<nrActions/>
		<makeSpan/>
		<metricValue/>
		<planningTechnique/>
		<additional/>
		</statistics>*/
		
		for (Iterator<?> iter = statistic.iterator(); iter.hasNext();) {
			String line = (String) iter.next();
			
			String keyword;
			String value;
			if(line.indexOf(' ') > -1){
				// there is a value
				keyword = line.substring(0, line.indexOf(' ')).trim();
				value = line.substring(line.indexOf(' '), line.length()).trim();
			}
			else{
				keyword = line;
				value = "";
			}
			
			if(keyword.equals("Time")){
				statisticNode.getChild("time").setText(value);
			}
			
			else if(keyword.equals("ParsingTime")){
				statisticNode.getChild("parsingTime").setText(value);
			}
			
			else if(keyword.equals("NrActions")){
				statisticNode.getChild("nrActions").setText(value);
			}
			
			else if(keyword.equals("MakeSpan")){
				statisticNode.getChild("makeSpan").setText(value);
			}
			
			else if(keyword.equals("MetricValue")){
				statisticNode.getChild("metricValue").setText(value);
			}
			
			else if(keyword.equals("PlanningTechnique")){
				statisticNode.getChild("planningTechnique").setText(value);
			}
			
			else{
				String text = statisticNode.getChildText("additional");
				statisticNode.getChild("additional").setText(text + keyword + " " + value + "\n");
			}
		}
 
	}    
    
    
    /**
     * 
     * @param chosenPlanner
     * @param domainFile
     * @param problemFile
     * @return an xml representation of the plan
     */
    public Element solvePlanningProblem(Element chosenPlanner, String domainFile, String problemFile){
            toolMessage ="";
            // create the xml plan format
            //Element xmlPlan = null;
            //Element xmlPlan = thePlan;

            //check if the planner file exists
            Element settings = chosenPlanner.getChild("settings");
            String plannerFile = settings.getChildText("filePath");
            //System.out.println(plannerFile);
            File f = new File(plannerFile);
            boolean plannerFileExists = true;
            if (!f.exists()){
                plannerFileExists = false;
                toolMessage += ">> Could not find selected planner '"+ plannerFile +"'. \n" +
                        ">> Please download and copy it in the folder /myPlanners \n";
                ItSIMPLE.getInstance().appendOutputPanelText(toolMessage);
            }

            if (plannerFileExists){

                //if(xmlPlan == null){
                if(thePlan == null){
                    setEmptyPlan();
                    //xmlPlan = thePlan;

                    //try {
                    //        xmlPlan = XMLUtilities.readFromFile("resources/planners/DefaultPlan.xml").getRootElement();
                    //} catch (Exception e) {
                    //        e.printStackTrace();
                    //}

                }

                if(thePlan != null){
                    //1. get chosen planner output
                    ArrayList<String> output = new ArrayList<String>();
                    ArrayList<String> consoleOutput = new ArrayList<String>();

                    output = getPlannerOutput(chosenPlanner, domainFile, problemFile, consoleOutput);


                    //2. separates the plan and the statistics
                    ArrayList<String> plan = new ArrayList<String>();
                    ArrayList<String> statistic = new ArrayList<String>();
                    getPlanAndStatistics(output, plan, statistic);

                    /* IT IS ALREADY ON THE SETEMPTYPLAN() method
                     *
                    //3. set the project, domain and problem names
                    xmlPlan.getChild("project").setText(projectName);
                    xmlPlan.getChild("domain").setText(domainName);
                    xmlPlan.getChild("problem").setText(problemName);

                    //4. set the planner features
                    Element planner = xmlPlan.getChild("planner");
                    //4.0 set the planner id
                    planner.setAttribute("id", chosenPlanner.getAttributeValue("id"));
                    //4.1 set the planner name
                    //planner.getChild("name").setText(chosenPlanner.getChildText("name"));
                    //4.2 set the planner version
                    //planner.getChild("version").setText(chosenPlanner.getChildText("version"));

                    //4.1 and 4.2 add planner's characteristics
                    planner.addContent(chosenPlanner.cloneContent());
                    planner.removeContent(planner.getChild("settings")); //except for setting
                     * 
                     */

                    //3. set datetime
                    DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date();
                    String dateTime = dateFormat.format(date);
                    //xmlPlan.getChild("datetime").setText(dateTime);
                    thePlan.getChild("datetime").setText(dateTime);


                    //4. set the planner results
                    //Element planner = xmlPlan.getChild("planner");
                    Element planner = thePlan.getChild("planner");

                    //4.3 set the planner console output
                    //4.3.1 build up the text from the string array
                    String consoleOutputStr = "";
                    for (Iterator<?> iter = consoleOutput.iterator(); iter.hasNext();) {
                                    String line = (String) iter.next();
                                    //remove trouble chars
                                    line = XMLUtilities.RemoveTroublesomeCharacters(line);
                                    consoleOutputStr += line + " \n";
                            }
                        //4.3.2 set the value/output
                    planner.addContent(new Element("consoleOutput"));
                    
                    planner.getChild("consoleOutput").setText(consoleOutputStr);

                    //5. set statistics
                    //Element statisticsNode = xmlPlan.getChild("statistics");
                    Element statisticsNode = thePlan.getChild("statistics");
                    parseStatisticsToXML(statisticsNode, statistic);

                    //6. set the plan
                    //Element planNode = xmlPlan.getChild("plan");
                    Element planNode = thePlan.getChild("plan");
                    parsePlanToXML(planNode, plan);

                    if(planNode.getChildren("action").size() > 0){
                            toolMessage += "Planner generated a solution.";
                    }
                    else{
                            toolMessage += "Planner did not generate any solution!";
                    }

                    //7. set tool information message
                    //xmlPlan.getChild("toolInformation").getChild("message").setText(toolMessage);
                    thePlan.getChild("toolInformation").getChild("message").setText(toolMessage);



                    //Plan Validation
                   // System.out.println("Validation");

                    //check if automatic plan validation is enabled (if so call validation with VAL
                    Element validitySettings = ItSIMPLE.getItPlanners().getChild("settings").getChild("planValidation");
                    if (validitySettings != null && validitySettings.getAttributeValue("enabled").equals("true")){
                        if (thePlan.getChild("plan").getChildren().size() > 0){
                            //System.out.println("Starting Validation");
                            try {
                                PlanValidator.checkPlanValidityWithVAL(thePlan);
                            } catch (Exception e) {
                            }
                        }
                    }



                    if (showReport){
                        //8. set the plan info panel
                        //ItSIMPLE.getInstance().showHTMLReport(xmlPlan);
                        ItSIMPLE.getInstance().showHTMLReport(thePlan);
                    }






                }







            }




 
		
    	//return xmlPlan;
        return thePlan;
    }



    /**
     * This methods has the same approach as solvePlanningProblem; however, it
     *  considers that the chosen planner,domain file path and problem path were
     * all set previously using setters.
     * @return
     */
    public Element solveProblem(){
        //Element xmlPlan = null;
        //thePlan = null;

        //xmlPlan = solvePlanningProblem(chosenPlanner, domainFile, problemFile);
        solvePlanningProblem(chosenPlanner, domainFile, problemFile);
        //thePlan = xmlPlan;

        //return xmlPlan;
        return thePlan;
    }



    /**
     * This method creates a HTML version of the information contained in the xmlPlan
     * @param xmlPlan
     * @return a html string containing a simple plan report (basic info). In fact,itSIMPLE class also has 
     * such function (is is duplicated, use itSIMPLE's one) .
     */
    private String generateHTMLReport(Element xmlPlan){

    	
    	/*
        // get the date
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);
    	*/

        String dateTime = xmlPlan.getChildText("datetime");
        // head
        String info = "<TABLE width='100%' BORDER='0' align='center'>"+
                                "<TR><TD bgcolor='333399'><font size=4 face=arial color='FFFFFF'>" +
                                "<b>REPORT</b> - "+ dateTime +"</font></TD></TR>";



        // project, domain and problem
        if(XMLDomain != null && XMLProblem != null){
                Element project = XMLDomain.getParentElement().getParentElement().getParentElement();

                info += "<TR><TD><font size=3 face=arial><b>Project: </b>"+ project.getChildText("name")+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Domain: </b>"+ XMLDomain.getChildText("name")+
                                "</font></TD></TR>" +
                                "<TR><TD><font size=3 face=arial><b>Problem: </b>"+ XMLProblem.getChildText("name")+
                                "</font></TD></TR>";
        }

        info += "<TR><TD bgcolor='FFFFFF'><font size=3 face=arial><b>itSIMPLE message:<br></b>"+
                        toolMessage.replaceAll("\n", "<br>") +"<p></TD></TR>";

        // planner
        Element planner = xmlPlan.getChild("planner");
        Element settingsPlanner = null;
        try {
                XPath path = new JDOMXPath("planners/planner[@id='"+ planner.getAttributeValue("id") +"']");
                settingsPlanner = (Element)path.selectSingleNode(ItSIMPLE.getItPlanners());
        } catch (JaxenException e) {
                e.printStackTrace();
        }

        if(settingsPlanner != null){
                info += "<TR><TD bgcolor='gray'><font size=4 face=arial color='FFFFFF'><b>Planner</b></TD></TR>" +
                                "<TR><TD><font size=3 face=arial><b>Name: </b>"+ settingsPlanner.getChildText("name")+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Version: </b>"+ settingsPlanner.getChildText("version")+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Author(s): </b>"+ settingsPlanner.getChildText("author")+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Institution(s): </b>"+ settingsPlanner.getChildText("institution")+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Link: </b>"+ settingsPlanner.getChildText("link")+
                                "</font></TD></TR>"+
                                "<TR><TD><font size=3 face=arial><b>Description: </b>"+ settingsPlanner.getChildText("description")+
                                "</font><p></TD></TR>";
        }

        // statistics
        Element statistics = xmlPlan.getChild("statistics");
        info += "<TR><TD bgcolor='gray'><font size=4 face=arial color='FFFFFF'><b>Statistics</b>" +
                        "</TD></TR>"+
                        "<TR><TD><font size=3 face=arial><b>Tool total time: </b>"+ statistics.getChildText("toolTime")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Planner time: </b>"+ statistics.getChildText("time")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Parsing time: </b>"+ statistics.getChildText("parsingTime")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Number of actions: </b>"+ statistics.getChildText("nrActions")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Make Span: </b>"+ statistics.getChildText("makeSpan")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Metric value: </b>"+ statistics.getChildText("metricValue")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Planning technique: </b>"+ statistics.getChildText("planningTechnique")+
                        "</font></TD></TR>" +
                        "<TR><TD><font size=3 face=arial><b>Additional: </b>"+ statistics.getChildText("additional").replaceAll("\n", "<br>")+
                        "</font><p></TD></TR>";


        // plan
        info += "<TR><TD bgcolor='gray'><font size=4 face=arial color='FFFFFF'><b>Plan</b></TD></TR>";


        List<?> actions = xmlPlan.getChild("plan").getChildren("action");
        if (actions.size() > 0) {
                for (Iterator<?> iter = actions.iterator(); iter.hasNext();) {
                        Element action = (Element) iter.next();
                        // build up the action string
                        // start time
                        String actionStr = action.getChildText("startTime") + ": ";

                        // action name
                        actionStr += "(" + action.getAttributeValue("id") + " ";

                        // action parameters
                        List<?> parameters = action.getChild("parameters")
                                        .getChildren("parameter");
                        for (Iterator<?> iterator = parameters.iterator(); iterator
                                        .hasNext();) {
                                Element parameter = (Element) iterator.next();
                                actionStr += parameter.getAttributeValue("id");
                                if (iterator.hasNext()) {
                                        actionStr += " ";
                                }
                        }
                        actionStr += ")";

                        // action duration
                        String duration = action.getChildText("duration");
                        if (!duration.equals("")) {
                                actionStr += " [" + duration + "]";
                        }

                        if(iter.hasNext()){
                                info += "<TR><TD><font size=3 face=arial>"+ actionStr +"</font></TD></TR>";
                        }
                        else{
                                info += "<TR><TD><font size=3 face=arial>"+ actionStr +"</font><p></TD></TR>";
                        }
                }
        }
        else{
                info += "<TR><TD><font size=3 face=arial>No plan found.</font><p></TD></TR>";
        }


        // planner console output
        info += "<TR><TD bgcolor='gray'><font size=3 face=arial color='FFFFFF'>" +
                        "<b>Planner Console Output</b></TD></TR>"+
                        "<TR><TD><font size=4 face=courier>" +
                        planner.getChildText("consoleOutput").replaceAll("\n", "<br>")+"</font><p></TD></TR>";

        info += "</TABLE>";


     	return info;
    }
    

	/**
	 * @param domain the XMLDomain to set
	 */
	public void setXMLDomain(Element domain) {
		XMLDomain = domain;
	}

	/**
	 * @param problem the XMLProblem to set
	 */
	public void setXMLProblem(Element problem) {
		XMLProblem = problem;
	}


    /**
     * Set problem name
     * @param name
     */
    public void setProjectName(String name){
		projectName = name;
	}

    /**
     * Set domain name
     * @param name
     */
    public void setDomainName(String name){
        domainName = name;
	}

    /**
     * Set problem name
     * @param name
     */
    public void setProblemName(String name){
		problemName = name;
	}
    public String getProblemName(){
		return problemName;
    }

    public void setChosenPlanner(Element chosenPlanner) {
        this.chosenPlanner = chosenPlanner;
    }

    public Element getChosenPlanner() {
        return chosenPlanner;
    }
    
    public void setShowReport(boolean showReport) {
        this.showReport = showReport;
    }

    public Element getPlan() {
        return thePlan;
    }

    public void setPlan(Element thePlan) {
        this.thePlan = thePlan;
    }


	public void run() {
		if(chosenPlanner != null && domainFile != null && problemFile != null){
			
			JLabel status = ItSIMPLE.getInstance().getPlanSimStatusBar();		
			status.setText("Status: Solving planning problem...");

			Element xmlPlan = solvePlanningProblem(chosenPlanner, domainFile, problemFile);
                        //XMLUtilities.printXML(xmlPlan);

			try {
				if(replaning){
					PlanNavigationList.getInstance().setPlanListAfterReplaning(xmlPlan);
				}
				else{
					ItSIMPLE.getInstance().setPlanList(xmlPlan);
				}				

			} catch (Exception e) {
				e.printStackTrace();
			}


            status.setText("Status: Done solving planning problem!");

			ItSIMPLE.getInstance().setSolveProblemButton();
                        
            //clean up plan database reference in case there is one being used in itsimple
            ItSIMPLE.getInstance().cleanupPlanDatabaseReference();

		}
	}

	
	public void destroyProcess(){
        if (process != null){
            process.destroy();
            
            String operatingSystem = System.getProperty("os.name").toLowerCase();
            if (operatingSystem.indexOf("linux")==0){
                //kill process in linux with comand 'killall -9 <process_name>'
                //System.out.println("Kill" );

                if (plannerRunFile != null && plannerRunFile.exists()){

                    //System.out.println(plannerRunFile.getName());
                    String filename =plannerRunFile.getName();
                    if (!filename.trim().equals("")){
                        String[] command = new String[3];
                        command[0] = "killall";
                        command[1] = "-9";
                        command[2] = filename;

                        try {
                            Runtime.getRuntime().exec(command);
                        } catch (IOException ex) {
                            Logger.getLogger(ExecPlanner.class.getName()).log(Level.SEVERE, null, ex);
                        }

                    }

                    
                }
            }
            


        }
	}
        

  
    
    /*public static void main(String[] args) {    	
    	
    	ExecPlanner s = new ExecPlanner();
    	
    	try {
			s.plannerTesting();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    	
    	
//    	 Get planners from itPlanners		
		org.jdom.Document itPlannersDoc = null;
		try{
			itPlannersDoc = XMLUtilities.readFromFile("resources/planners/itPlanners.xml");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		Element itPlanners = null;
		if (itPlannersDoc != null){
			itPlanners = itPlannersDoc.getRootElement();			
		}
    	
		Element chosenPlanner = null;
		try {
			XPath path = new JDOMXPath("planners/planner[@id='6']");			
			chosenPlanner = (Element)path.selectSingleNode(itPlanners);								
		} catch (JaxenException e1) {			
			e1.printStackTrace();
		}
    	
    	ExecPlanner s = new ExecPlanner(chosenPlanner, "resources/planners/domain.pddl", "resources/planners/problem.pddl");
    	
    	Element xmlPlan = s.solvePlanningProblem(chosenPlanner, "resources/planners/domain.pddl", "resources/planners/problem.pddl");
    	XMLUtilities.printXML(xmlPlan);
	}*/
    
    
    
/*    public void plannerTesting() throws InterruptedException{
    	String[] command = new String[8];
    	
    	command[0] = "myPlanners/lpg-td-1.0.exe";
    	command[1] = "-o";
    	command[2] = "resources/planners/domain.pddl";
    	command[3] = "-f";
    	command[4] = "resources/planners/problem.pddl";
    	command[5] = "-speed";
    	command[6] = "-out";
    	command[7] = "solution";
    	    	
    	
    	ArrayList<String> consoleOutput = new ArrayList<String>();
    	
    	Process process;
		try {
			process = Runtime.getRuntime().exec(command);
				
	    	Scanner sc = new Scanner(process.getInputStream());            		                    
	    	//Get the planner answer exposed in the console
	    	if (consoleOutput != null) {
	            while (sc.hasNextLine()) {
	            	System.out.println(sc.nextLine());
	            	//consoleOutput.add(sc.nextLine());
				}
			}                
	        sc.close();                
	        
	        process.waitFor();
	        process.destroy();
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(consoleOutput);
            
        
    }*/


	
	
}
