/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007-2010 Universidade de Sao Paulo
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
**/

package src.planning;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;

import src.gui.ItSIMPLE;
import src.languages.pddl.XPDDLToPDDL;
import src.languages.xml.XMLUtilities;

/**
 *
 * @author tiago
 */
public class PlanValidator {


        

        public static void checkPlanValidityWithVAL(Element xmlPlan) {


            double time = 0;
            Process process = null;
            String toolMessage = "";

            Element xmlvalidity = xmlPlan.getChild("validity");


            //1. Gather the files

            //String pddlDomain = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "");
            //String pddlProblem = XPDDLToPDDL.parseXPDDLToPDDL(xpddlProblem, "");

            //Domain and Problem files
            File domainFile = new File("resources/planners/domain.pddl");
            File problemFile = new File("resources/planners/problem.pddl");
            File planFile = new File("resources/validators/plan");

            //Plan file
            //xml plan to pddl plan format
	    String pddlPlan = XPDDLToPDDL.parseXMLPlanToPDDL(xmlPlan);
            //write            
            try {
                FileWriter domainWriter = new FileWriter(planFile);
                domainWriter.write(pddlPlan.toString());
                domainWriter.close();
            } catch (IOException e1) {
                    e1.printStackTrace();
            }
            
            //System.out.println(pddlPlan);

            //Chosing the validator

            Element chosenValidator = null;
             //check the os for chosing the right validator
            String osys = getOperatingSystem();
            try {
                    XPath path = new JDOMXPath("validators/validator[platform/"+osys+"]");
                    chosenValidator = (Element)path.selectSingleNode(ItSIMPLE.getItValidators());
            } catch (JaxenException e) {
                    e.printStackTrace();
            }

            if (chosenValidator != null){
                //Check if the validator support the pddl version selected
                boolean pddlversionSupported = true;
                String pddlVersion = ItSIMPLE.getInstance().getSelectedPDDLversion();
                //System.out.println(pddlVersion);
                Element supportedVersion = chosenValidator.getChild("requirements").getChild(pddlVersion);
                if (supportedVersion == null){
                   pddlversionSupported = false;
                }



                //and buildling the command line arguments
                ArrayList<String> commandArguments = new ArrayList<String>();
                commandArguments.add(chosenValidator.getChild("settings").getChildText("filePath"));
                //arguments
                Element settings = chosenValidator.getChild("settings");
                List<?> additionalArgs = null;
                try {
                    XPath path = new JDOMXPath("arguments/*[enable='true']");
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
                            if (argument.getName().equals("domain")){
                                commandArguments.add("resources/planners/domain.pddl");
                            }
                            else if (argument.getName().equals("problem")){
                                commandArguments.add("resources/planners/problem.pddl");
                            }
                            else if (argument.getName().equals("plan")){
                                commandArguments.add("resources/validators/plan");
                            }
                            else if (!argument.getChildText("value").trim().equals("")) {
                                commandArguments.add(argument.getChildText("value").trim());
                            }
                        }
                    }
                }
                //commandArguments.add("resources/planners/domain.pddl");
                //commandArguments.add("resources/planners/problem.pddl");
                //commandArguments.add("resources/validators/plan");
                //System.out.println(commandArguments);

                //Prepare the command line
                String[] command = new String[commandArguments.size()];
                //System.out.println(commandArguments);

                for (int i = 0; i < commandArguments.size(); i++) {
                        command[i] = commandArguments.get(i);
                        //System.out.println(command[i]);
                }


                time = 0;
                //set inicial time
                double start_time = System.currentTimeMillis();
                ItSIMPLE.getInstance().appendOutputPanelText("\n>> Plan validation process requested (enabled). \n");
                ItSIMPLE.getInstance().appendOutputPanelText(">> Calling validator "
                        +chosenValidator.getChildText("name")+ " " + chosenValidator.getChildText("version")
                        +  " (for more details see "+chosenValidator.getChildText("link")+"). \n");
                //Call the validator
                boolean gotError = false;
                try {
                    process = Runtime.getRuntime().exec(command);
                } catch (Exception e) {
                    String message = "## Error while running the validator. Please check the validator's executable file, permissions, and operating system compatibility. \n";
                    System.out.println(message);
                    toolMessage += message;
                    ItSIMPLE.getInstance().appendOutputPanelText(message);
                    gotError = true;
                }

                //check if there is a error while running the validator
                if (!gotError){

                    ItSIMPLE.getInstance().appendOutputPanelText("Validator output: \n");
                    Scanner sc = new Scanner(process.getInputStream());
                    List<String> consoleOutput = new ArrayList<String>();
                    StringBuffer consoleOutputString = new StringBuffer();

                    //Get the validator answer exposed in the console
                    while (sc.hasNextLine()) {
                        //consoleOutput.add(sc.nextLine());
                        String line = sc.nextLine();
                        consoleOutput.add(line);
                        consoleOutputString.append(line);
                        //System.out.println(line);

                        ItSIMPLE.getInstance().appendOutputPanelText(line + "\n");
                    }
                    sc.close();
                    try {
                        process.waitFor();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(PlanValidator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    process.destroy();

                    //ItSIMPLE.getInstance().appendOutputPanelText("\n>> Validator's output read. \n");

                    time = System.currentTimeMillis() - start_time;
                    // Must divide per 1000 (time/1000) in order to get the time in seconds.
                    ItSIMPLE.getInstance().appendOutputPanelText("Validator's output read ("+String.valueOf(time/1000)+" s). \n");

                    String output = consoleOutputString.toString();

                    boolean canRegisterValidator = true;

                    if (output.indexOf("Plan valid") != -1){
                        ItSIMPLE.getInstance().appendOutputPanelText("\n (!) Result: VALID PLAN \n");
                        xmlvalidity.setAttribute("isValid", "true");
                    }else if (output.indexOf("Plan failed to execute") != -1){
                        ItSIMPLE.getInstance().appendOutputPanelText("\n (!) Result: INVALID PLAN. Plan failed to execute.  \n");
                        xmlvalidity.setAttribute("isValid", "false");
                        xmlvalidity.setText("Plan failed to execute");
                    }else if (output.indexOf("Goal not satisfied") != -1){
                        ItSIMPLE.getInstance().appendOutputPanelText("\n (!) Result: INVALID PLAN. Goal not satisfied.  \n");
                        xmlvalidity.setAttribute("isValid", "false");
                        xmlvalidity.setText("Goal not satisfied");
                    }else {
                        String restultString = "Unable to validate with "+ chosenValidator.getChildText("name")+ " " +chosenValidator.getChildText("version") + ". Unable to identify the cause.";
                        if (!pddlversionSupported){
                            restultString = "Unable to validate with "+ chosenValidator.getChildText("name")+ " " +chosenValidator.getChildText("version") + ". Potential cause: pddl version not supported.";
                        }
                        ItSIMPLE.getInstance().appendOutputPanelText("\n (!) Result: UNKNOWN. "+restultString+" \n");
                        //xmlvalidity.setAttribute("isValid", "false");
                        xmlvalidity.setText(restultString);
                        canRegisterValidator = false;
                    }

                    //record the validator that checked the plan in the xml
                    if (canRegisterValidator){
                        Element validator = xmlPlan.getChild("validator");
                        if (validator==null){
                            validator = new Element("validator");
                            xmlPlan.addContent(validator);
                        }
                        //3.1 set the validator id
                        validator.setAttribute("id", chosenValidator.getAttributeValue("id"));

                        //3.2 add validator's characteristics
                        validator.addContent(chosenValidator.cloneContent());
                        validator.removeContent(validator.getChild("settings")); //except for setting

                    }



                }


            }
            //in the case we could not find any validator for the current os
            else{
                    String message = "## System could not find a validator for you operational system. \n";
                    System.out.println(message);
                    toolMessage += message;
                    ItSIMPLE.getInstance().appendOutputPanelText(message);
            }

            


        }

    /**
     * This method get the operating system name on which the itSIMPLE running
     * @return
     */
    private static String getOperatingSystem(){

        String operatingSystem = System.getProperty("os.name").toLowerCase();

        if (operatingSystem.indexOf("linux")==0)
            operatingSystem = "linux";
        else if (operatingSystem.indexOf("windows")==0)
             operatingSystem = "windows";
        else if (operatingSystem.indexOf("mac")==0)
             operatingSystem = "mac";

        return operatingSystem;
    }



}
