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
* Authors:	Tiago S. Vaquero, 
*		 	Victor Romero.
**/

package languages.xml;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

public class XMLUtilities {
	
	/**
	 * Read a xml type file and returns its document
	 * @param fname the file name
	 * @return the xml document
	 * @throws JDOMException
	 * @throws IOException
	 */
	public static Document readFromFile(String fname)
	  throws JDOMException, IOException {
		SAXBuilder sxb = new SAXBuilder();		
		return sxb.build(new File(fname));
	}
	
	/**
	 * Save a xml document in a file
	 * @param fname the name of the file to be created
	 * @param doc the xml document
	 */
	public static void writeToFile(String fname, Document doc){
		try{
			FileOutputStream out = new FileOutputStream(fname);
			
			Format format = Format.getPrettyFormat();
			format.setIndent("	");
			format.setEncoding("ISO-8859-1");
			XMLOutputter op = new XMLOutputter(format);
			op.output(doc,out);
			out.flush();
			out.close();
		}
		catch (IOException e){
			System.err.println(e);
		}
	}
	
	/**
	 * Used to get a new id when creating a node in the model
	 * @param parent the parent of the new node
	 * @return the id to be used, higher than the highest current id
	 */
	public static int getId(Element parent){
		List children = parent.getChildren();
		Iterator it = children.iterator();
		int id, idM=0;		
		while(it.hasNext()){
			Element el1 = (Element) it.next();
			id = Integer.valueOf(el1.getAttributeValue("id")).intValue();
			if (id>idM){
				idM=id;
			}
		}
		return idM+1;
	}
	
	/**
	 * Returns the element in parent with the given id
	 * @param parent the parent node
	 * @param id the element id
	 * @return the element in parent with the given id
	 */
	public static Element getElement(Element parent, String id){		
		Element element = null;
		try {
			XPath path = new JDOMXPath("child::*[@id='"+ id +"']");									
			element = (Element)path.selectSingleNode(parent);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		return element;
	}
	
	public static void printXML(Element element){
		Format format = Format.getPrettyFormat();
		format.setIndent("\t");
		XMLOutputter op = new XMLOutputter(format);
		if(element == null){
			System.err.println("Null element");
		}
		else{
			try {
				op.output(element, System.out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println();
		}
	}
	
	public static void printXML(Document doc){
		Format format = Format.getPrettyFormat();
		format.setIndent("\t");
		XMLOutputter op = new XMLOutputter(format);
		if(doc == null){
			System.err.println("Null document");
		}
		else{
			try {
				op.output(doc, System.out);
			} catch (IOException e) {
				e.printStackTrace();
			}
			System.out.println();
		}
	}

    /**
     * This method returns an Element as a string
     * @param element
     * @return
     */
	public static String toString(Element element){
        String content = "";
		Format format = Format.getPrettyFormat();
		format.setIndent("\t");
		XMLOutputter op = new XMLOutputter(format);
		if(element == null){
			System.err.println("Null element");
		}
		else{
			content = op.outputString(element);
		}
        return content;
	}

	
	
	/**
	 * Returns all class ascendents data until the first class in the hierarchy
	 * 
	 * @param childClass the class data from which we want the ascendents
	 * @return a List with all the class ascendents
	 */
	public static List<Element> getClassAscendents(Element childClass){
		List<Element> parents = new ArrayList<Element>();
		boolean hasParent = true;
		Element parent = childClass;
		while (hasParent){
			//check if it's not a primitive class
			if (!parent.getChildText("type").equals("Primitive")){
				if (!parent.getChild("generalization").getAttributeValue("id").equals("")){
					parent = XMLUtilities.getElement(parent.getParentElement(), parent.getChild("generalization").getAttributeValue("id"));
					hasParent = true;
					parents.add(parent);
				}
				else{
					hasParent = false;
				}
			}
			else{
				hasParent = false;									
			}	
		}
		
		return parents;
	}
	
	/**
	 * Returns all class descendents data until the first class in the hierarchy
	 * 
	 * @param parentClass the class data from which we want the descendents
	 * @return a List with all the class descendents
	 */
	public static List<Element> getClassDescendents(Element parentClass){
		List<Element> descendents = new ArrayList<Element>();		
		getClassDescendents(parentClass, descendents);
		
		return descendents;
	}
	
	/**
	 * Adds to the descendents list all the descendents parentClass descendents
	 * 
	 * @param parentClass the class data from which we want to the descendents
	 * @param descendents the list which the descendets will the be added to
	 */
	private static void getClassDescendents(Element parentClass, List<Element> descendents){
		List result = null;
		try {
			XPath path = new JDOMXPath("class[generalization/@element='class' and generalization/@id='"
					+ parentClass.getAttributeValue("id") + "']");
			result = path.selectNodes(parentClass.getParentElement());
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		for (Iterator iter = result.iterator(); iter.hasNext();) {
			Element child = (Element) iter.next();
			descendents.add(child);
			getClassDescendents(child, descendents);
		}

	}
	
	/**
	 * Adds to the descendents list all the descendents parentClass descendents
	 * 
	 * @param parentClass the class data from which we want to the descendents
	 * @param descendents the list which the descendets will the be added to
	 */
	public static Element getClassesHierarchyTree(Element project){
		Element tree = new Element("hierarchy");
		
		List classes = null;
		try {
			XPath path = new JDOMXPath("project/elements/classes/class[stereotype!='utility' and type!='Primitive' and generalization[@id='']]");
			classes = path.selectNodes(project.getDocument());
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		for(Iterator iter = classes.iterator(); iter.hasNext();){
			Element currentClass = (Element)iter.next();
			
			Element highClassTree = getClassDescendenceTree(currentClass);
			tree.addContent(highClassTree);
		}
		return tree;
	}

	private static Element getClassDescendenceTree(Element child) {
		Element highClass = new Element("class");
		highClass.setAttribute("name", child.getChildText("name"));
		highClass.setAttribute("id", child.getAttributeValue("id"));
		
		List result = null;
		try {
			XPath path = new JDOMXPath("class[generalization/@element='class' and generalization/@id='"
					+ child.getAttributeValue("id") + "']");
			result = path.selectNodes(child.getParentElement());
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		for (Iterator iterator = result.iterator(); iterator.hasNext();) {
			Element grandchild = (Element) iterator.next();				
			Element grandchildTree = getClassDescendenceTree(grandchild);
			highClass.addContent(grandchildTree);
		}
		
		return highClass;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//ToXPDDL xpddl = new ToXPDDL();
		Element project = null;
		try {
			project = XMLUtilities.readFromFile("examples/DepotDomain.xml").getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(project != null){
			Element tree = getClassesHierarchyTree(project);
			XMLUtilities.printXML(tree);
		}
		
	}
	
	
}
