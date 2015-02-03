/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007-2012 University of Sao Paulo, University of Toronto
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

package src.domainanalysis;

import src.planning.*;

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
import src.gui.ItTreeNode;
import src.languages.pddl.ToXPDDL;
import src.languages.pddl.XPDDLToPDDL;
import src.languages.xml.XMLUtilities;
import src.util.fileio.FileInput;

/**
 *
 * @author tiago
 */
public class TorchLightAnalyzer {


        /**
         * This method runs the TorchLight System for analyzing local minimas
         * and potential issues in the operator's effects
         * The systemas was designed by Joerg Hoffman
         */
        public static void getTorchLightAnalysis(ItTreeNode selectedNode, String pddlVersion) {


            
            Element node = selectedNode.getData();

            String pddlDomain = "";
            String pddlProblem = "";
            
            //System.out.println(node.getName());
            
            //Check whether this is a UML project or a PDDL project
            //UML
            if (node.getName().equals("problem")){                            
                Element domainProject = node.getDocument().getRootElement();
                Element domain = node.getParentElement().getParentElement();
                Element problem = node;

                // generate PDDL domain
                Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(domainProject, pddlVersion, null);
                //XMLUtilities.printXML(xpddlDomain);

                // generate PDDL problem
                Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, pddlVersion);
                //XMLUtilities.printXML(xpddlProblem);

                //Change domain requirements (if necessary) based on the chosen problem
                ToXPDDL.adjustRequirements(xpddlDomain, xpddlProblem, pddlVersion);

                pddlDomain = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "");
                pddlProblem = XPDDLToPDDL.parseXPDDLToPDDL(xpddlProblem, "");
            }
            //PDDL
            else if(node.getName().equals("pddlproblem")) {
                
                //domain file and content
                ItTreeNode project = (ItTreeNode)selectedNode.getParent();
                //XMLUtilities.printXML(project.getData());
                File theitProjectFile = new File(project.getInfoString());
                String domainpath = project.getInfoString().replaceFirst(theitProjectFile.getName(), "");                
                Element domainNode = project.getData().getChild("pddldomains").getChild("pddldomain");
                domainpath += domainNode.getChildText("name").trim();
                //System.out.println(domainpath);
                pddlDomain = FileInput.readFile(domainpath);
                                               
                //problem instance file and content
                String problempath = node.getAttributeValue("file");
                //System.out.println(problempath);
                pddlProblem = FileInput.readFile(problempath);

            }

            // save in auxiliary files and solve it
            if (!pddlDomain.trim().equals("") && !pddlProblem.trim().equals("")){
                
                File domainFile = new File("myAnalyzers/domain.pddl");
                File problemFile = new File("myAnalyzers/problem.pddl");

                try {
                    FileWriter domainWriter = new FileWriter(domainFile);
                    domainWriter.write(pddlDomain);
                    domainWriter.close();

                    FileWriter problemWriter = new FileWriter(problemFile);
                    problemWriter.write(pddlProblem);
                    problemWriter.close();
                } catch (IOException e1) {
                        e1.printStackTrace();
                }


                double time = 0;
                Process process = null;
                String toolMessage = "";


                //and buildling the command line arguments
                ArrayList<String> commandArguments = new ArrayList<String>();

                commandArguments.add("myAnalyzers/torchlight");
                commandArguments.add("-o");
                commandArguments.add("myAnalyzers/domain.pddl");
                commandArguments.add("-f");
                commandArguments.add("myAnalyzers/problem.pddl");
                System.out.println(commandArguments);

                //Prepare the command line
                String[] command = new String[commandArguments.size()];
                //System.out.println(commandArguments);

                for (int i = 0; i < commandArguments.size(); i++) {
                        command[i] = commandArguments.get(i);
                        //System.out.println(command[i]);
                }


                //1st call of TorchLight
                ItSIMPLE.getInstance().appendAnalysisOutputPanelText("\n");
                ItSIMPLE.getInstance().appendAnalysisOutputPanelText(">> TorchLight. 1ST CALL for generating VARIABLES.txt file. \n");
                //ItSIMPLE.getInstance().appendOutputPanelText("\n");
                //ItSIMPLE.getInstance().appendOutputPanelText(">> TorchLight. 1ST CALL for generating VARIABLES.txt file. \n");



                time = 0;
                //set inicial time
                double start_time = System.currentTimeMillis();
                //ItSIMPLE.getInstance().appendOutputPanelText(">> Calling TorchLight (1). \n");
                //Call the validator
                boolean gotError = false;
                try {
                    process = Runtime.getRuntime().exec(command);
                } catch (Exception e) {
                    String message = "## Error while running TorchLight. Please check the executable file, permissions, and operating system compatibility. \n";
                    System.out.println(message);
                    toolMessage += message;
                    ItSIMPLE.getInstance().appendAnalysisOutputPanelText(message);
                    //ItSIMPLE.getInstance().appendOutputPanelText(message);
                    gotError = true;
                }

                //check if there is a error while running the validator
                if (!gotError){

                    //ItSIMPLE.getInstance().appendOutputPanelText("TorchLight output: \n");
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

                        //ItSIMPLE.getInstance().appendOutputPanelText(line + "\n");
                    }
                    sc.close();
                    try {
                        process.waitFor();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(TorchLightAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                    //ItSIMPLE.getInstance().appendOutputPanelText("\n>> Validator's output read. \n");

                    time = System.currentTimeMillis() - start_time;
                    // Must divide per 1000 (time/1000) in order to get the time in seconds.
                    //ItSIMPLE.getInstance().appendOutputPanelText("TorchLight's output read ("+String.valueOf(time/1000)+" s). \n");

                    String output = consoleOutputString.toString();

                    process.destroy();



                    //2nd call of TorchLigh
                    ItSIMPLE.getInstance().appendAnalysisOutputPanelText("\n");
                    ItSIMPLE.getInstance().appendAnalysisOutputPanelText(">> TorchLight - 2ND CALL. Analyzing...\n");
                    ItSIMPLE.getInstance().appendAnalysisOutputPanelText("\n");        
                    //ItSIMPLE.getInstance().appendOutputPanelText("\n");
                    //ItSIMPLE.getInstance().appendOutputPanelText(">> TorchLight - 2ND CALL. Analyzing...\n");
                    //ItSIMPLE.getInstance().appendOutputPanelText("\n");

                    //and buildling the command line arguments
                    commandArguments = new ArrayList<String>();

                    commandArguments.add("myAnalyzers/torchlight");
                    commandArguments.add("-o");
                    commandArguments.add("myAnalyzers/domain.pddl");
                    commandArguments.add("-f");
                    commandArguments.add("myAnalyzers/problem.pddl");
                    commandArguments.add("-M");
                    System.out.println(commandArguments);

                    //Prepare the command line
                    command = new String[commandArguments.size()];
                    //System.out.println(commandArguments);

                    for (int i = 0; i < commandArguments.size(); i++) {
                            command[i] = commandArguments.get(i);
                            //System.out.println(command[i]);
                    }

                    time = 0;
                    //set inicial time
                    start_time = System.currentTimeMillis();
                    ItSIMPLE.getInstance().appendAnalysisOutputPanelText(">> Calling TorchLight (2). \n");
                    //ItSIMPLE.getInstance().appendOutputPanelText(">> Calling TorchLight (2). \n");

                    //Call the validator
                    gotError = false;
                    try {
                        process = Runtime.getRuntime().exec(command);
                    } catch (Exception e) {
                        String message = "## Error while running TorchLight. Please check the executable file, permissions, and operating system compatibility. \n";
                        System.out.println(message);
                        toolMessage += message;
                        ItSIMPLE.getInstance().appendAnalysisOutputPanelText(message);
                        //ItSIMPLE.getInstance().appendOutputPanelText(message);
                        gotError = true;
                    }

                    //check if there is a error while running the validator
                    if (!gotError){

                        ItSIMPLE.getInstance().appendAnalysisOutputPanelText("TorchLight output: \n");
                        //ItSIMPLE.getInstance().appendOutputPanelText("TorchLight output: \n");
                        sc = new Scanner(process.getInputStream());
                        consoleOutput = new ArrayList<String>();
                        consoleOutputString = new StringBuffer();

                        //Get the validator answer exposed in the console
                        while (sc.hasNextLine()) {
                            //consoleOutput.add(sc.nextLine());
                            String line = sc.nextLine();
                            consoleOutput.add(line);
                            consoleOutputString.append(line);
                            //System.out.println(line);

                            ItSIMPLE.getInstance().appendAnalysisOutputPanelText(line + "\n");
                            //ItSIMPLE.getInstance().appendOutputPanelText(line + "\n");
                        }
                        sc.close();
                        try {
                            process.waitFor();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(TorchLightAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        //ItSIMPLE.getInstance().appendOutputPanelText("\n>> Validator's output read. \n");

                        time = System.currentTimeMillis() - start_time;
                        // Must divide per 1000 (time/1000) in order to get the time in seconds.
                        ItSIMPLE.getInstance().appendAnalysisOutputPanelText("TorchLight's output read ("+String.valueOf(time/1000)+" s). \n");
                        //ItSIMPLE.getInstance().appendOutputPanelText("TorchLight's output read ("+String.valueOf(time/1000)+" s). \n");

                        output = consoleOutputString.toString();


                        //Read output file: ModelFeedback.txt

                    }
                    process.destroy();
                }
            


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
