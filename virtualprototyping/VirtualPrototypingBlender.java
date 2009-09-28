/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007,2008,2009 Universidade de Sao Paulo
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

package virtualprototyping;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import languages.xml.XMLUtilities;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;

/**
 *
 * @author Tiago
 */
public class VirtualPrototypingBlender {
    private static String domainFile = "C:/Users/Tiago/Desktop/BlenderTest/GoldMiner v1/domainfiles/domainMetrics.xml";
    private static String problemFile = "C:/Users/Tiago/Desktop/BlenderTest/GoldMiner v1/domainfiles/problemMetrics.xml";
    private static String planFile = "C:/Users/Tiago/Desktop/BlenderTest/GoldMiner v1/domainfiles/plan.xml";
    private static String solutionFile = "C:/Users/Tiago/Desktop/BlenderTest/GoldMiner v1/domainfiles/solution.txt";

    public static void generatePrototypeFiles(Element domain, Element problem, Element xmlPlan) throws IOException{

        //DOMAIN
        //1. Set domain file
        Document domainDoc = null;
        domainDoc = (Document) domain.getDocument().clone();

        //1.1Domain simplification

        Element rootDomain = domainDoc.getRootElement();

        // Remove things that we are not going to use (yet)
        
        //  Remove the whole diagrams node
        Element diagrams = rootDomain.getChild("diagrams");
        rootDomain.removeContent(diagrams);

        
        //  Put the domain node instead
        Element currentDomain = (Element) domain.clone();
        rootDomain.addContent(currentDomain);
        
        //  Clean up operators (not necessary, yet)
        List operatores = null;
        try {
            XPath path = new JDOMXPath("project/elements/classes/class/operators");
            operatores = path.selectNodes(domainDoc);
        } catch (JaxenException e) {			
            e.printStackTrace();
        }
         for(Iterator iter = operatores.iterator(); iter.hasNext();){
                Element classoperators = (Element)iter.next();
                classoperators.removeContent();
        }

        // Remove repositoryDiagrams node from domain node
        Element repoDiagrams = currentDomain.getChild("repositoryDiagrams");
        currentDomain.removeContent(repoDiagrams);

        // Remove planningProblems node from domain node
        Element planningProblems = currentDomain.getChild("planningProblems");
        currentDomain.removeContent(planningProblems);


        //1.2 Strip the elements
        //  Stripping objects in the domain. Remove class reference
        //  and put the class name instead
        List objects = null;
        try {
            XPath path = new JDOMXPath("elements/objects/object");
            objects = path.selectNodes(currentDomain);
        } catch (JaxenException e) {
            e.printStackTrace();
        }

        for(Iterator iter = objects.iterator(); iter.hasNext();){
                Element eaObject = (Element)iter.next();
                Element objClass = eaObject.getChild("class");
                Element theClass = null;
                try {
                        XPath path = new JDOMXPath("project/elements/classes/class[@id='"+objClass.getText()+"']");
                        theClass = (Element)path.selectSingleNode(domainDoc);
                } catch (JaxenException e) {	
                        e.printStackTrace();
                }
                //Change the class ID for the class name
                objClass.setText(theClass.getChildText("name"));
        }

        //  Stripping general quality metrics of the domain. Adding object and class names and stereotypes
        List metrics = null;
        try {
            XPath path = new JDOMXPath("metrics/qualityMetric");
            metrics = path.selectNodes(currentDomain);
        } catch (JaxenException e) {
            e.printStackTrace();
        }

        for(Iterator iter = metrics.iterator(); iter.hasNext();){
                Element eaMetric = (Element)iter.next();
                String type = eaMetric.getChildText("type");
                //Variable case

                if (type.trim().equals("variable")){
                    Element theObject = eaMetric.getChild("variable").getChild("chosenVariable").getChild("object");
                    Element theAttribute = theObject.getChild("attribute");

                    //Locate the referenced object
                    Element obj = null;
                    try {
                            XPath path = new JDOMXPath("elements/objects/object[@id='"+theObject.getAttributeValue("id")+"']");
                            obj = (Element)path.selectSingleNode(currentDomain);
                    } catch (JaxenException e) {
                            e.printStackTrace();
                    }
                    //Locate the referenced object
                    Element clss = null;
                    try {
                            XPath path = new JDOMXPath("project/elements/classes/class[@id='"+theObject.getAttributeValue("class")+"']");
                            clss = (Element)path.selectSingleNode(domainDoc);
                    } catch (JaxenException e) {
                            e.printStackTrace();
                    }

                    //add obj name, class name and stereotype
                    theObject.addContent((Element) obj.getChild("name").clone());
                    Element clssName = new Element("class");
                    clssName.setText(clss.getChildText("name"));
                    theObject.addContent(clssName);
                    theObject.addContent((Element) clss.getChild("stereotype").clone());


                    //Find the original attribute
                    Element attr = null;
                    try {
                            XPath path = new JDOMXPath("project/elements/classes/class[@id='"+theAttribute.getAttributeValue("class")+"']/attributes/attribute[@id='"+theAttribute.getAttributeValue("id")+"']");
                            attr = (Element)path.selectSingleNode(domainDoc);
                    } catch (JaxenException e) {
                            e.printStackTrace();
                    }
                    theAttribute.addContent((Element) attr.getChild("name").clone());

                    //set the level of the metric in order to make the difference between domain and problem
                    eaMetric.setAttribute("level", "domain");
                }

        }
        //1.3 Save domain File
        XMLUtilities.writeToFile(domainFile, domainDoc);
        //XMLUtilities.writeToFile("resources/settings/test.xml", domainDoc);


        //PROBLEM
        //2. Set problem file
        Document problemDoc = null;
        problemDoc = (Document) problem.getDocument().clone();
        Element rootProblem = problemDoc.getRootElement();

        //2.1 Simplify problem file
        //  Remove things that we are not going to use (yet)

        //  Remove the whole diagrams node and also elements node
        Element pdiagrams = rootProblem.getChild("diagrams");
        rootProblem.removeContent(pdiagrams);
        Element pelements = rootProblem.getChild("elements");
        rootProblem.removeContent(pelements); 
        
        //  Put domain node in the problem 
        Element pcurrentDomain = (Element) domain.clone();
        rootProblem.addContent(pcurrentDomain);
        
        // Remove repositoryDiagrams node from domain node
        pcurrentDomain.removeContent(pcurrentDomain.getChild("repositoryDiagrams"));

        // Remove planningProblems node from domain node
        pcurrentDomain.removeContent(pcurrentDomain.getChild("planningProblems"));
        
        // Remove metrics node from domain node
        Element pmetrics = pcurrentDomain.getChild("metrics");
        if (pmetrics != null){
            pcurrentDomain.removeContent(pmetrics);
        }

        //  Add choosen problem to the domain
        Element currentProblem = (Element) problem.clone();
        pcurrentDomain.addContent(currentProblem);


        //2.2 Strip objects and attributes
        //  Stripping objects in the problem. Remove class reference
        //  and put the class name instead. In addition, strip attributes references
        objects = null;
        try {
            XPath path = new JDOMXPath("objectDiagrams/objectDiagram/objects/object");
            objects = path.selectNodes(currentProblem);
        } catch (JaxenException e) {
            e.printStackTrace();
        }
        
        for(Iterator iter = objects.iterator(); iter.hasNext();){
            Element eaObject = (Element)iter.next(); //this element is just a reference
            
            //find real object in domain/elements/objects/object
            Element theObject = null;
            try {
                XPath path = new JDOMXPath("elements/objects/object[@id='"+eaObject.getAttributeValue("id")+"']");
                theObject = (Element) path.selectSingleNode(pcurrentDomain);
            } catch (JaxenException e) {
                e.printStackTrace();
            }            
            
            Element oName = (Element) theObject.getChild("name").clone();
            Element oClass = (Element) theObject.getChild("class").clone();
            
            eaObject.addContent(oName);
            eaObject.addContent(oClass);

            //find object class
            Element theClass = null;
            try {
                XPath path = new JDOMXPath("project/elements/classes/class[@id='"+oClass.getText()+"']");
                theClass = (Element) path.selectSingleNode(domainDoc);
            } catch (JaxenException e) {
                e.printStackTrace();
            }                 
            oClass.setText(theClass.getChildText("name"));
            
            //Attributes
            List attributes = eaObject.getChild("attributes").getChildren("attribute");
            for(Iterator aiter = attributes.iterator(); aiter.hasNext();){
                Element eaAttr = (Element)aiter.next(); 
                //Add name and type
                //find object class
                Element theAttr = null;
                try {
                    XPath path = new JDOMXPath("project/elements/classes/class[@id='"+eaAttr.getAttributeValue("class")+"']/attributes/attribute[@id='"+eaAttr.getAttributeValue("id")+"']");
                    theAttr = (Element) path.selectSingleNode(domainDoc);
                } catch (JaxenException e) {
                    e.printStackTrace();
                }
                eaAttr.addContent((Element) theAttr.getChild("name").clone());
                eaAttr.addContent((Element) theAttr.getChild("type").clone());
            }

            
        }
        
        //2.2 Strip associations. Put the names (based on rolenames) and the object's name in the assocEnd.
        List associations = null;
        try {
            XPath path = new JDOMXPath("objectDiagrams/objectDiagram/associations/objectAssociation");
            associations = path.selectNodes(currentProblem);
        } catch (JaxenException e) {
            e.printStackTrace();
        }

        for(Iterator iter = associations.iterator(); iter.hasNext();){
            Element eaAssoc = (Element)iter.next(); //this element is just a reference

            //find real object in domain/elements/objects/object
            Element theAssoc = null;
            try {
                XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation[@id='"+eaAssoc.getChildText("classAssociation")+"']");
                theAssoc = (Element) path.selectSingleNode(domainDoc);
            } catch (JaxenException e) {
                e.printStackTrace();
            }

            //Find association end name in which navigation is true
            Element assocEndTrue = null;
            try {
                XPath path = new JDOMXPath("associationEnds/associationEnd[@navigation='true']");
                assocEndTrue = (Element) path.selectSingleNode(theAssoc);
            } catch (JaxenException e) {
                e.printStackTrace();
            }
            Element attrName = new Element("name");
            if (assocEndTrue != null){
                attrName.setText(assocEndTrue.getChild("rolename").getChildText("value"));
            }else{
                attrName.setText(theAssoc.getChildText("name"));
            }
            eaAssoc.addContent(attrName);

            //Fill in object's name
            List associationEnds = eaAssoc.getChild("associationEnds").getChildren("objectAssociationEnd");
            for (Iterator it = associationEnds.iterator(); it.hasNext();) {
                Element aend = (Element)it.next();

                //Find this object in order to replace tha id to real name
                Element endObj = null;
                try {
                    XPath path = new JDOMXPath("elements/objects/object[@id='"+aend.getAttributeValue("element-id")+"']");
                    endObj = (Element) path.selectSingleNode(pcurrentDomain);
                } catch (JaxenException e) {
                    e.printStackTrace();
                }
                aend.setAttribute("element", endObj.getChildText("name"));
            }
            
            //Clean graphics
            eaAssoc.removeContent(eaAssoc.getChild("graphics"));

        }

        //  Stripping specific quality metrics of the problem. Adding object and class names and stereotypes
        metrics = null;
        try {
            XPath path = new JDOMXPath("metrics/qualityMetric");
            metrics = path.selectNodes(currentProblem);
        } catch (JaxenException e) {
            e.printStackTrace();
        }

        for(Iterator iter = metrics.iterator(); iter.hasNext();){
                Element eaMetric = (Element)iter.next();
                String type = eaMetric.getChildText("type");
                //XMLUtilities.printXML(eaMetric);
                //Variable case

                if (type.trim().equals("variable")){
                    Element theObject = eaMetric.getChild("variable").getChild("chosenVariable").getChild("object");
                    Element theAttribute = theObject.getChild("attribute");

                    if (!theObject.getAttributeValue("id").equals("")){
                            //Locate the referenced object
                        Element obj = null;
                        try {
                                XPath path = new JDOMXPath("elements/objects/object[@id='"+theObject.getAttributeValue("id")+"']");
                                obj = (Element)path.selectSingleNode(domain);
                        } catch (JaxenException e) {
                                e.printStackTrace();
                        }

                        //Locate the referenced object
                        Element clss = null;
                        try {
                                XPath path = new JDOMXPath("project/elements/classes/class[@id='"+theObject.getAttributeValue("class")+"']");
                                clss = (Element)path.selectSingleNode(domainDoc);
                        } catch (JaxenException e) {
                                e.printStackTrace();
                        }

                        //add obj name, class name and stereotype
                        theObject.addContent((Element) obj.getChild("name").clone());
                        Element clssName = new Element("class");
                        clssName.setText(clss.getChildText("name"));
                        theObject.addContent(clssName);
                        theObject.addContent((Element) clss.getChild("stereotype").clone());


                        //Find the original attribute
                        Element attr = null;
                        try {
                                XPath path = new JDOMXPath("project/elements/classes/class[@id='"+theAttribute.getAttributeValue("class")+"']/attributes/attribute[@id='"+theAttribute.getAttributeValue("id")+"']");
                                attr = (Element)path.selectSingleNode(domainDoc);
                        } catch (JaxenException e) {
                                e.printStackTrace();
                        }
                        theAttribute.addContent((Element) attr.getChild("name").clone());

                        //set the level of the metric in order to make the difference between domain and problem
                        eaMetric.setAttribute("level", "problem");
                    }

                    
                }

        }




        //  Remove elements node
        pcurrentDomain.removeContent(pcurrentDomain.getChild("elements"));

        //2.3 Save problem file
        XMLUtilities.writeToFile(problemFile, problemDoc);
        


        //PLANNER and PLAN
        //3. Set planner file
        Document plannerDoc = (Document) xmlPlan.getDocument().clone();

        Element rootPlanner = plannerDoc.getRootElement();
        rootPlanner.setName("plannerInfo");

        XMLUtilities.writeToFile(planFile, plannerDoc);
        //XMLUtilities.printXML(xmlPlan);


        //PLAN (PDDL)
        //4. Set plan file
        //  create plan file
        String plan = "";
        List<Element> actions = xmlPlan.getChild("plan").getChildren("action");
        for(Iterator<Element> iter = actions.iterator(); iter.hasNext();){
            Element action = iter.next();
            //index
            String startTime = action.getChildText("startTime");
            //SGPlan 5 displays it with a dot (.)
            String index;
            if(startTime.indexOf(".") > 0)
                index = startTime.substring(0, startTime.indexOf("."));
            else
                index = startTime;
            String actionId = action.getAttributeValue("id");
            // MIPS XXL changes "_" for "-"
            actionId = actionId.replaceAll("-", "_");
            
            String parametersStr = "";
            List parameters = action.getChild("parameters").getChildren("parameter");
            for (Iterator it = parameters.iterator(); it.hasNext();) {
                Element parameter = (Element) it.next();
                parametersStr += " "+parameter.getAttributeValue("id");

            }


            plan += index + ": (" + actionId + parametersStr + ") ["+action.getChildText("duration")+"] \n";
        }
        //System.out.println(plan);

        // save the string to files
        try {
                FileWriter planFile = new FileWriter(solutionFile);
                planFile.write(plan);
                planFile.close();
        } catch (IOException e) {
                e.printStackTrace();
        }


    }

}
