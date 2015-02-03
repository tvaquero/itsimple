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
* Authors:	Tiago S. Vaquero
*
**/

package rationale;


import alice.tuprolog.InvalidTheoryException;
import alice.tuprolog.MalformedGoalException;
import alice.tuprolog.NoMoreSolutionException;
import alice.tuprolog.NoSolutionException;
import alice.tuprolog.Prolog;
import alice.tuprolog.SolveInfo;
import alice.tuprolog.Theory;
import alice.tuprolog.UnknownVarException;
import itSIMPLE.ItSIMPLE;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JLabel;
import languages.psl.XMLtoPSL;
import languages.xml.XMLUtilities;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;

import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;
import planning.PlanAnalyzer;
import util.database.DataBase;
import util.fileio.FileInput;

/**
 *
 * @author tiago
 */
public class RationaleAnalyzer {


    public static String reuseExistingRationales(Element xmlPlan, Element problem, String currentPlanID, JLabel outputLabel){

        String result = "";

        Element domain = problem.getParentElement().getParentElement();
        Element project = domain.getParentElement().getParentElement().getParentElement();

        try {

            outputLabel.setText("Status: Building Prolog database");

            String theoryText = "";

            //1. Get the PSL basic elements and aximos
            String pslBasics = FileInput.readFile("resources/settings/pslBasics.pl");
            theoryText += pslBasics;

            //2. Get project elements in PSL
            theoryText += "\n \n";
            theoryText += "%Project \n";
            String pslProject = XMLtoPSL.generatePslProjectProlog(project);
            theoryText += pslProject;

            //3. get domain elements in PSL
            theoryText += "\n \n";
            theoryText += "%Domains \n";
            String pslDomain = XMLtoPSL.generatePslDomainProlog(project, domain);
            theoryText += pslDomain;

            //4. get problem elements in PSL
            theoryText += "\n \n";
            theoryText += "%Problem Instances \n";
            String pslProblem = XMLtoPSL.generatePslProblemProlog(project, domain, problem);
            theoryText += pslProblem;

            theoryText += "\n \n";
            theoryText += "%Plans \n";
            //5. get plan element in PSL
            String pslPlan = XMLtoPSL.generatePslPlanProlog(xmlPlan, currentPlanID);
            theoryText += pslPlan;

            //5. get rationales
            String pslRationales = XMLtoPSL.generatePslRationalePrologfromDatabase(xmlPlan,currentPlanID);
            theoryText += pslRationales;


            System.out.println(theoryText);
            ArrayList<String> rationalesIDs = new ArrayList<String>();
            ArrayList<String> rationalesComments = new ArrayList<String>();
            outputLabel.setText("Status: Reasoning with Prolog database. Please wait...");

            //set the nick name of the current plan in the prolog database
            String planid = "plan";
            if (currentPlanID.equals("-1")){
                planid += "New";
            }else{
                planid += currentPlanID;
            }

            //Access prolog (query)
            Prolog engine = new Prolog();
            Theory t = new Theory(theoryText);
            engine.setTheory(t);
            SolveInfo answer = engine.solve("quality_rationale_of(X,"+planid+",J).");
            //SolveInfo answer = engine.solve("prior(s0,o1plan44).");
            while (answer.isSuccess()) {
                //System.out.println("solution: " + answer.getSolution() +" - bindings: " + answer);
                //System.out.println("X: " + answer.getTerm("X"));
                String rationaleID = answer.getTerm("X").toString().trim().replace("r", "");
                rationalesIDs.add(rationaleID);
                //TODO: get the right comment
                String justification = formatJustification(answer.getTerm("J").toString().trim());
                String comment = justification;
                rationalesComments.add(comment);

                if (engine.hasOpenAlternatives()) {
                    answer = engine.solveNext();
                } else {
                    break;
                }
            }

            /*
            SolveInfo answer = engine.solve("quality_rationale_of(X,"+planid+").");
            //SolveInfo answer = engine.solve("prior(s0,o1plan44).");
            while (answer.isSuccess()) {
                //System.out.println("solution: " + answer.getSolution() +" - bindings: " + answer);
                //System.out.println("X: " + answer.getTerm("X"));
                String rationaleID = answer.getTerm("X").toString().trim().replace("r", "");
                rationalesIDs.add(rationaleID);
                //TODO: get the right comment
                String comment = " comment "+ rationaleID;
                rationalesComments.add(comment);

                if (engine.hasOpenAlternatives()) {
                    answer = engine.solveNext();
                } else {
                    break;
                }
            }
             */

            outputLabel.setText("Status: Reasoning done!");

            //Check whether there are rationales to be added
            if (rationalesIDs.size() > 0){
                outputLabel.setText("Status: Reading rationales...");
                Element rationales = xmlPlan.getChild("evaluation").getChild("rationales");
                Element newRationales = new Element("rationales");

                for (int i = 0; i < rationalesIDs.size(); i++) {
                    String id = rationalesIDs.get(i);
                    System.out.println("Rationale ID: " + id);
                    String comment = rationalesComments.get(i);
                    System.out.println("Comment: " + comment);

                    //Select the rationale and put it in the plan
                    DataBase eSelectType = new DataBase();
                    eSelectType.setColumnList("id, xmlrationale, planid"); //please, don't use *
                    eSelectType.setTableName("rationale");
                    eSelectType.setWhereClause("id = "+id);
                    eSelectType.Select();
                    //Get results
                    ResultSet rs = eSelectType.getRs();
                    try {
                        while (rs.next()) {
                            //String rationaleID = rs.getString("id");
                            String rationaleID = rs.getString(1);
                            String xmlRationaleString = rs.getString("xmlrationale");
                            String targetPlanID = rs.getString("planid");

                            //convert xml string to a xml element
                            SAXBuilder builder = new SAXBuilder();
                            Reader in = new StringReader(xmlRationaleString);
                            Document doc = null;
                            Element theRationale = null;
                            try {
                                doc = builder.build(in);
                                theRationale = doc.getRootElement();
                            } catch (JDOMException ex) {
                                Logger.getLogger(RationaleAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (IOException ex) {
                                Logger.getLogger(RationaleAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            if (theRationale != null){
                                theRationale.setAttribute("id", id);
                                if (targetPlanID!=null){
                                    theRationale.setAttribute("targetplanid", targetPlanID);
                                }
                                //comments
                                Element comments = theRationale.getChild("comments");
                                Element theComment = new Element("comment");
                                theComment.setText(comment);
                                comments.addContent(theComment);

                                newRationales.addContent((Element)theRationale.clone());
                                //rationales.addContent((Element)theRationale.clone());
                            }
                            //XMLUtilities.printXML(theRationale);
                        }
                    } catch (SQLException se) {
                      se.printStackTrace();
                      //System.exit(1);
                    }
                }

                //merge the current one and the new founds
                for (Iterator<Element> it = rationales.getChildren().iterator(); it.hasNext();) {
                    Element each = it.next();
                    String rationaleID = each.getAttributeValue("id");
                    String targetPlanID = each.getAttributeValue("targetplanid");
                    String relationalID = each.getAttributeValue("relationalid");

                    if (!rationaleID.equals("") && !targetPlanID.equals(currentPlanID)){
                        //try to find the old in the new list
                        // get the object class
                        List<Element> match = null;
                        try {
                                XPath path = new JDOMXPath("rationale[@id='"+ rationaleID +"']");
                                match = path.selectNodes(newRationales);
                        } catch (JaxenException e1) {
                                e1.printStackTrace();
                        }
                        //if we have a match update the rationale
                        if (match!=null && match.size() > 0){
                            //1. clean up the comments
                            Element comments = each.getChild("comments");
                            comments.removeContent();
                            //2. update response/comment (why) of the rationale
                            for (int i = 0; i < match.size(); i++) {
                                Element current = match.get(i); //current rationale discovery
                                //add current comment to the rationale
                                Element currentComment = current.getChild("comments").getChild("comment");
                                if (currentComment!=null){
                                   comments.addContent((Element)currentComment.clone());
                                }
                                //when there is a match remove it from the new rationales lis
                                // it will remain only the new ones
                                newRationales.removeContent(current);
                            }

                            //3. mark to update if the reference was already created
                            if (!relationalID.equals("")){
                                Element instruction = each.getChild("instruction");
                                if (instruction==null){
                                    instruction = new Element("instruction");
                                    each.addContent(instruction);
                                }
                                instruction.setAttribute("perform", "update-reference");
                            }

                        }else{
                            //if not a match then the rationale is not applied anymore for the current plan
                            // so mark to delete reference
                            if (!relationalID.equals("")){
                                Element instruction = each.getChild("instruction");
                                if (instruction==null){
                                    instruction = new Element("instruction");
                                    each.addContent(instruction);
                                }
                                instruction.setAttribute("perform", "delete-reference");
                            }
                        }
                    }
                }

                //add the new ones
                for (Iterator<Element> it = newRationales.getChildren().iterator(); it.hasNext();) {
                    Element each = it.next();
                    String currentRationaleID = each.getAttributeValue("id");
                    if (!currentRationaleID.equals("")){
                        Element match = null; //match at the existing old rationales
                        try {
                            XPath path = new JDOMXPath("rationale[@id='"+ currentRationaleID +"']");
                            match = (Element)path.selectSingleNode(rationales);
                        } catch (JaxenException e1) {
                                e1.printStackTrace();
                        }
                        if (match!=null){
                            Element currentComment = each.getChild("comments").getChild("comment");
                                if (currentComment!=null){
                                   match.getChild("comments").addContent((Element)currentComment.clone());
                                }
                        }else{
                            rationales.addContent((Element)each.clone());
                        }                        
                    }

                }
                XMLUtilities.printXML(rationales);



                outputLabel.setText("Status: Reuse done.");

                //show the new added rationales
                String evaluationhtml = PlanAnalyzer.generatePlanMetricsEvaluationSummary(xmlPlan);
                ItSIMPLE.getInstance().setPlanEvaluationInfoPanelText(evaluationhtml);

            }else{
                ItSIMPLE.getInstance().appendOutputPanelText(">> No rationale satisfy on this plan.\n");
            }

            //System.out.println("finish");

        } catch (NoMoreSolutionException ex) {
            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (NoSolutionException ex) {
            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownVarException ex) {
            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidTheoryException ex) {
            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedGoalException ex) {
            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
        }


        return result;


    }


    
    /**
     * Verify if the given rationale is valid for the given plan within the plannning problem
     * @param xmlPlan
     * @param problem
     * @param theRationale 
     * @param currentPlanID plan id in the database
     * @param outputLabel output messages
     * @return
     */
    public static String validateRationaleWithPlan(Element xmlPlan, Element problem, Element theRationale, String currentPlanID, JLabel outputLabel) throws NoMoreSolutionException{

        String result = "";

        Element domain = problem.getParentElement().getParentElement();
        Element project = domain.getParentElement().getParentElement().getParentElement();

        try {

            outputLabel.setText("Status: Building Prolog database");

            String theoryText = "";

            //1. Get the PSL basic elements and aximos
            String pslBasics = FileInput.readFile("resources/settings/pslBasics.pl");
            theoryText += pslBasics;

            //2. Get project elements in PSL
            theoryText += "\n \n";
            theoryText += "%Project \n";
            String pslProject = XMLtoPSL.generatePslProjectProlog(project);
            theoryText += pslProject;

            //3. get domain elements in PSL
            theoryText += "\n \n";
            theoryText += "%Domains \n";
            String pslDomain = XMLtoPSL.generatePslDomainProlog(project, domain);
            theoryText += pslDomain;

            //4. get problem elements in PSL
            theoryText += "\n \n";
            theoryText += "%Problem Instances \n";
            String pslProblem = XMLtoPSL.generatePslProblemProlog(project, domain, problem);
            theoryText += pslProblem;

            theoryText += "\n \n";
            theoryText += "%Plans \n";
            //5. get plan element in PSL
            String pslPlan = XMLtoPSL.generatePslPlanProlog(xmlPlan, currentPlanID);
            theoryText += pslPlan;

            //set the nick name of the current plan in the prolog database
            String planid = "plan";
            if (currentPlanID.equals("-1")){
                planid += "New";
            }else{
                planid += currentPlanID;
            }

            theoryText += "\n \n";
            theoryText += "%Rationales \n";
            //5. get rationales
            String rationaleID = theRationale.getAttributeValue("id").trim();
            if (rationaleID.equals("")){
                rationaleID = "New";
            }
            String pslRationales = XMLtoPSL.createRationaleStatement(theRationale, rationaleID);
            theoryText += pslRationales;


            System.out.println(theoryText);
            outputLabel.setText("Status: Reasoning with Prolog database. Please wait...");

            //clean up comments
            Element rationalesComments = theRationale.getChild("comments");
            if (rationalesComments.getChildren().size() > 0){
                rationalesComments.removeContent();
                setUpdateReference(theRationale);
            }

            //Access prolog (query)
            Prolog engine = new Prolog();
            Theory t = new Theory(theoryText);
            try {
                engine.setTheory(t);
            } catch (Exception e) {
                setInValidRationale(theRationale);
                outputLabel.setText("INVALID");
                System.out.println("Error while reading the generated Prolog database.");
                return "no";
            }

            //System.out.println("quality_rationale_of(r"+rationaleID+","+planid+").");;
            //SolveInfo answer = engine.solve("quality_rationale_of(r"+rationaleID+","+planid+").");
            //SolveInfo answer = engine.solve("quality_rationale_of(r"+rationaleID+","+planid+",_).");
            SolveInfo answer = engine.solve("quality_rationale_of(r"+rationaleID+","+planid+",J).");
            //System.out.println(answer.isSuccess());
            //SolveInfo answer = engine.solve("prior(s0,o1plan44).");
            boolean isValid = false;

            while (answer.isSuccess()) {
                //System.out.println("solution: " + answer.getSolution() +" - bindings: " + answer);
                //System.out.println("X: " + answer.getTerm("X"));
                isValid = true;
                //TODO: get the right comment
                String justification = formatJustification(answer.getTerm("J").toString().trim());
                Element comment = new Element("comment");
                comment.setText(justification);
                rationalesComments.addContent(comment);

                if (engine.hasOpenAlternatives()) {
                    answer = engine.solveNext();
                } else {
                    break;
                }
            }

            if (isValid) {
                setValidRationale(theRationale);
                setUpdateReference(theRationale);
                result = "yes";
                outputLabel.setText("VALID");

            }else{//not succeded (FALSE)
                System.out.println("Answer not succeed from the Prolog database (false).");
                setInValidRationale(theRationale);
                result = "no";
                outputLabel.setText("INVALID");
            }

            engine.clearTheory();

            //XMLUtilities.printXML(theRationale);

            /*
            if (answer.isSuccess()) {
                System.out.println("solution: " + answer.getSolution() +" - bindings: " + answer);
                //System.out.println("X: " + answer.getTerm("X"));

                if (answer.toString().startsWith("yes")){
                    setValidRationale(theRationale);
                    result = "yes";
                    outputLabel.setText("VALID");
                }
                else{
                    setInValidRationale(theRationale);
                    result = "no";
                    outputLabel.setText("INVALID");
                }

            }else{//not succeded (FALSE)
                System.out.println("Answer not succeed from the Prolog database (false).");
                setInValidRationale(theRationale);
                result = "no";
                outputLabel.setText("INVALID");
            }
             *
             */


        } catch (NoSolutionException ex) {
            Logger.getLogger(RationaleAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnknownVarException ex) {
            Logger.getLogger(RationaleAnalyzer.class.getName()).log(Level.SEVERE, null, ex);
        //} catch (NoSolutionException ex) {
        //    Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InvalidTheoryException ex) {
            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
        } catch (MalformedGoalException ex) {
            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
        }


        return result;


    }


    private static void setValidRationale(Element theRationale){        
        theRationale.getChild("validity").setAttribute("isValid", "true");
        //set modified
        if (!theRationale.getAttributeValue("id").trim().equals("")){
            if (theRationale.getChild("modified")==null){
                Element modified = new Element("modified");
                theRationale.addContent(modified);
            }
        }
        System.out.println("The rationale is valid for the current plan");
    }

    
    private static void setInValidRationale(Element theRationale){
        theRationale.getChild("validity").setAttribute("isValid", "false");
        //set modified
        if (!theRationale.getAttributeValue("id").trim().equals("")){
            if (theRationale.getChild("modified")==null){
                Element modified = new Element("modified");
                theRationale.addContent(modified);
            }
        }
        System.out.println("The rationale is NOT valid for the current plan");
    }

    private static void setUpdateReference(Element theRationale){
        //set modified
        if (!theRationale.getAttributeValue("id").trim().equals("") && !theRationale.getAttributeValue("relationalid").trim().equals("")){
            if (theRationale.getChild("instruction")==null){
                Element instruction = new Element("instruction");
                instruction.setAttribute("perform", "update-reference");
                theRationale.addContent(instruction);
            }
        }
    }


    /**
     * This method formats a list of string in to Prolog format to a simpe string
     * e.g.: ['hello ', 'world'] to 'hello word'.
     * @param rawStr
     * @return
     */
    private static String formatJustification(String rawStr){
        String justification = "";

        if (rawStr.startsWith("[") && rawStr.endsWith("]")){
            //clean up braquets
            String content = rawStr.substring(rawStr.indexOf("[")+1,rawStr.lastIndexOf("]"));

            String[] elements = content.split(",");
            for (int i = 0; i < elements.length; i++) {
                String current = elements[i];
                String aString = "";
                if (current.startsWith("'") && current.endsWith("'")){
                    aString = current.substring(current.indexOf("'")+1,current.lastIndexOf("'"));
                }else if (current.startsWith("'")){
                    aString = current.substring(current.indexOf("'")+1) + ",";
                }else if (current.endsWith("'")){
                    aString = current.substring(0, current.lastIndexOf("'"));
                }else{
                   aString = current+" ";
                }
                justification += aString;
            }

        }else{
            justification = rawStr;
        }
        return justification;
    }




}
