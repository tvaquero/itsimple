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
*		 	Victor Romero.
**/

package src.languages.pddl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jdom.JDOMException;

import src.languages.xml.XMLUtilities;

public class XPDDLToUML {

    private static Element commonData = null;


    public static Element parseXPDDLProbemToUML(Element xpddlProblem, Element domain){

        Element problem = null;

        try {
            commonData = (Element) XMLUtilities.readFromFile("resources/settings/commonData.xml").getRootElement();
        } catch (JDOMException ex) {
            Logger.getLogger(XPDDLToUML.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(XPDDLToUML.class.getName()).log(Level.SEVERE, null, ex);
        }


        problem = (Element) commonData.getChild("definedNodes").getChild("elements").getChild("structure").getChild("problem").clone();

        problem.setAttribute("id",String.valueOf(XMLUtilities.getId(domain.getChild("planningProblems"))));
	problem.getChild("name").setText(xpddlProblem.getChildText("name"));



        //1. Check and create object in the repository

        Element xobjects = xpddlProblem.getChild("objects");

        for (Iterator<Element> it = xobjects.getChildren().iterator(); it.hasNext();) {
            Element xobject = it.next();

            String objname = xobject.getAttributeValue("name");
            String objclassname = xobject.getAttributeValue("type");
            //System.out.println(objname);
            //System.out.println(objclassname);
            addNewDomainObject(objname, objclassname, problem, domain);

        }


        //2. Init state

        Element xinit = xpddlProblem.getChild("init");
        Element initDiagram = null;
        try {
                XPath path = new JDOMXPath("objectDiagrams/objectDiagram[sequenceReference='init']");
                initDiagram = (Element)path.selectSingleNode(problem);
        } catch (JaxenException e2) {
                e2.printStackTrace();
        }
        for (Iterator<Element> it = xinit.getChildren().iterator(); it.hasNext();) {
            Element xfact = it.next();

            //treat timed initial literals in aonther diagram
            if(xfact.getName().equals("at")){
                System.out.println("Timed-initial-literal case.");
                //create or find the snapshop associated to the time sequence
                // and then call set fact with the literal inside.
            }
            else{// put it in the init diagram
                setFactInSnapshop(xfact, initDiagram, domain);
            }

        }


        //3. Goal state


        //4. Constraints


        //5. Metrics




        return problem;
    }


    public static void addNewDomainObject(String objectName, String className, Element problem, Element domain){

        //Check if the object already exists in the domain objects
        Element existingObj = null;
        try {
                XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='"+ objectName.toLowerCase() +"']");
                existingObj = (Element)path.selectSingleNode(domain);
        } catch (JaxenException e2) {
                e2.printStackTrace();
        }
        //if there is no such object create it
        if (existingObj == null){

            System.out.println("New object "+objectName);

            //Find class
            Element theClass = null;
            try {
                    XPath path = new JDOMXPath("project/elements/classes/class[lower-case(name)='"+ className.toLowerCase() +"']");
                    theClass = (Element)path.selectSingleNode(domain.getDocument());
            } catch (JaxenException e2) {
                    e2.printStackTrace();
            }
            if (theClass!=null){

                //System.out.println(theClass.getChildText("name"));

                Element objectCommonData = commonData.getChild("definedNodes").getChild("elements").getChild("model").getChild("object");
                Element objectReferenceCommonData = commonData.getChild("definedNodes").getChild("elements").getChild("references").getChild("object");

                Element objectData = (Element)objectCommonData.clone();

                Element objectReference = (Element)objectReferenceCommonData.clone();

                String id = String.valueOf(XMLUtilities.getId(domain.getChild("elements").getChild("objects")));

                objectData.setAttribute("id", id);
                objectReference.setAttribute("id", id);

                objectData.getChild("name").setText(objectName);
                // the objects will be crated in cascate
                int xposition = 10;
                int yposition = 10;
                objectReference.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(xposition));
                objectReference.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(yposition));


                //Element objectClass = null;

                // there is a defined class
                Element classData = (Element)theClass;
                objectData.getChild("class").setText(classData.getAttributeValue("id"));

                // make the object bigger
                objectReference.getChild("graphics").getChild("size").setAttribute("width", "120");
                objectReference.getChild("graphics").getChild("size").setAttribute("height", "80");

                //sets the classSystem.out.println("New object "+objectName);
                //objectClass = (Element)theClass;


                // populate the object attributes (class and its parents attributes)

                //1. Get all class parents and their attributes
                ArrayList<Element> parentList = new ArrayList<Element>();
                ArrayList<Element> attributeList = new ArrayList<Element>();
                boolean hasParent = true;
                Element parent = classData;
                while (hasParent && parent != null){
                        //check if it's not a primitive class
                        if (!parent.getChildText("type").equals("Primitive")){
                                parentList.add(parent);

                                //1.1 List attributes
                                Iterator attributes = parent.getChild("attributes").getChildren("attribute").iterator();
                                while(attributes.hasNext()){
                                        Element attribute = (Element)attributes.next();
                                        attributeList.add(attribute);
                                }
                                //1.2 Checks if there is a parent class
                                if (!parent.getChild("generalization").getAttributeValue("id").equals("")){
                                        parent = XMLUtilities.getElement(parent.getParentElement(), parent.getChild("generalization").getAttributeValue("id"));
                                        hasParent = true;
                                }
                                else{
                                        hasParent = false;
                                        parent = null;
                                }
                        }
                        else{
                                hasParent = false;
                        }
                }

                //2. Get the object and put the new attributes

                //for (int i = 0; i < result.size(); i++){
                        //Element object = (Element)result.get(i);
                        //objectData.getChild("attributes").removeContent();
                        //Build each attribute reference
                        for (Iterator iter = attributeList.iterator(); iter.hasNext();) {
                                Element currentAtt = (Element) iter.next();

                                Element attributeReference = (Element)commonData.getChild("definedNodes").getChild("elements")
                                .getChild("references").getChild("attribute").clone();

                                attributeReference.setAttribute("class", currentAtt.getParentElement().getParentElement().getAttributeValue("id"));
                                attributeReference.setAttribute("id", currentAtt.getAttributeValue("id"));
                                //if (!currentAtt.getChildText("initialValue").equals("")){
                                //        attributeReference.getChild("value").setText(currentAtt.getChildText("initialValue"));
                                //}
                                objectReference.getChild("attributes").addContent(attributeReference);
                        }





                //add to the objects
                domain.getChild("elements").getChild("objects").addContent(objectData);

                //add to the repository
                domain.getChild("repositoryDiagrams").getChild("repositoryDiagram").getChild("objects").addContent(objectReference);

                //add to the init diagram
                Element initiDiagram = null;
                try {
                        XPath path = new JDOMXPath("objectDiagrams/objectDiagram[sequenceReference='init']");
                        initiDiagram = (Element)path.selectSingleNode(problem);
                } catch (JaxenException e2) {
                        e2.printStackTrace();
                }
                if (initiDiagram!=null){
                    initiDiagram.getChild("objects").addContent((Element)objectReference.clone());
                }





        //        diagram.getChild("objects").addContent(objectReference);
        //
        //        ObjectCell object = new ObjectCell(objectData, objectReference, objectClass);
        //        graph.getGraphLayoutCache().insert(object);
        //
        //        ItTreeNode objectNode = new ItTreeNode(objectData.getChildText("name"), objectData, objectReference, null);
        //        objectNode.setIcon(new ImageIcon("resources/images/object.png"));
        //        treeModel.insertNodeInto(objectNode, diagramNode, diagramNode.getChildCount());



            }
            else{
                System.out.println("type " + className + " not found. Object " + objectName + " not created.");
            }



        }
        //If the object already exists in the domain object create it in the init
        else{


            System.out.println("Existing object "+objectName);
            //Find class
            Element theClass = null;
            try {
                    XPath path = new JDOMXPath("project/elements/classes/class[lower-case(name)='"+ className.toLowerCase() +"']");
                    theClass = (Element)path.selectSingleNode(domain.getDocument());
            } catch (JaxenException e2) {
                    e2.printStackTrace();
            }
            if (theClass!=null){

                //System.out.println(theClass.getChildText("name"));

                Element objectReferenceCommonData = commonData.getChild("definedNodes").getChild("elements").getChild("references").getChild("object");

                Element objectReference = (Element)objectReferenceCommonData.clone();

                String id = String.valueOf(XMLUtilities.getId(domain.getChild("elements").getChild("objects")));

                objectReference.setAttribute("id", existingObj.getAttributeValue("id"));

                // the objects will be crated in cascate
                int xposition = 10;
                int yposition = 10;
                objectReference.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(xposition));
                objectReference.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(yposition));


                //Element objectClass = null;

                // there is a defined class
                Element classData = (Element)theClass;

                // make the object bigger
                objectReference.getChild("graphics").getChild("size").setAttribute("width", "120");
                objectReference.getChild("graphics").getChild("size").setAttribute("height", "80");


                // populate the object attributes (class and its parents attributes)

                //1. Get all class parents and their attributes
                ArrayList<Element> parentList = new ArrayList<Element>();
                ArrayList<Element> attributeList = new ArrayList<Element>();
                boolean hasParent = true;
                Element parent = classData;
                while (hasParent && parent != null){
                        //check if it's not a primitive class
                        if (!parent.getChildText("type").equals("Primitive")){
                                parentList.add(parent);

                                //1.1 List attributes
                                Iterator attributes = parent.getChild("attributes").getChildren("attribute").iterator();
                                while(attributes.hasNext()){
                                        Element attribute = (Element)attributes.next();
                                        attributeList.add(attribute);
                                }
                                //1.2 Checks if there is a parent class
                                if (!parent.getChild("generalization").getAttributeValue("id").equals("")){
                                        parent = XMLUtilities.getElement(parent.getParentElement(), parent.getChild("generalization").getAttributeValue("id"));
                                        hasParent = true;
                                }
                                else{
                                        hasParent = false;
                                        parent = null;
                                }
                        }
                        else{
                                hasParent = false;
                        }
                }

                //2. Get the object and put the new attributes

                //for (int i = 0; i < result.size(); i++){
                        //Element object = (Element)result.get(i);
                        //objectData.getChild("attributes").removeContent();
                        //Build each attribute reference
                        for (Iterator iter = attributeList.iterator(); iter.hasNext();) {
                                Element currentAtt = (Element) iter.next();

                                Element attributeReference = (Element)commonData.getChild("definedNodes").getChild("elements")
                                .getChild("references").getChild("attribute").clone();

                                attributeReference.setAttribute("class", currentAtt.getParentElement().getParentElement().getAttributeValue("id"));
                                attributeReference.setAttribute("id", currentAtt.getAttributeValue("id"));
                                if (!currentAtt.getChildText("initialValue").equals("")){
                                        attributeReference.getChild("value").setText(currentAtt.getChildText("initialValue"));
                                }
                                objectReference.getChild("attributes").addContent(attributeReference);
                        }



                //add to the init diagram
                Element initiDiagram = null;
                try {
                        XPath path = new JDOMXPath("objectDiagrams/objectDiagram[sequenceReference='init']");
                        initiDiagram = (Element)path.selectSingleNode(problem);
                } catch (JaxenException e2) {
                        e2.printStackTrace();
                }
                if (initiDiagram!=null){
                    initiDiagram.getChild("objects").addContent((Element)objectReference.clone());
                }


            }
            else{
                System.out.println("type " + className + " not found. Object " + objectName + " not created.");
            }




        }





       


    }

    private static void setFactInSnapshop(Element xfact, Element objectDiagram, Element domain) {

        //TODO
        if(xfact.getName().equals("predicate")){

        }
        else if(xfact.getName().equals("function")){

        }
        else if(xfact.getName().equals("equals")){

        }
        else if(xfact.getName().equals("at")){

        }

    }

	
	/*public static void main(String[] args) {
		Element project = null;
		try {
			project = XMLUtilities.readFromFile("examples/DriverLogDomain.xml").getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(project != null){
			Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(project, ToXPDDL.PDDL_3_0);
			String pddl = parseXPDDLToPDDL(xpddlDomain, "");
			System.out.println(pddl);
			
			List<?> problems = project.getChild("diagrams").getChild("planningProblems").getChildren("problem");
			for (Iterator<?> iter = problems.iterator(); iter.hasNext();) {
				Element problem = (Element) iter.next();
				Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, ToXPDDL.PDDL_3_0);
				String pddlProblem = parseXPDDLToPDDL(xpddlProblem, "");
				System.out.println(pddlProblem);
				System.out.println("\n-----------------------------------------------------------------------------------------------\n");
			}
		}
		
	}*/
	
}
