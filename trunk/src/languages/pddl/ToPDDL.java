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

package src.languages.pddl;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.*;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;

public class ToPDDL {
	
	public static String requirements = ":typing";
	
	public static void print(Element xml) throws IOException
	{
		XMLOutputter outputter = new XMLOutputter();
		//outputter.output(xml,System.out);
		
	}
//	 ###################################################################################################################################	
	public static Element readFromFile(String filename) throws JDOMException, IOException
	{
		SAXBuilder sxb = new SAXBuilder();
		//Document doc = new Document();
		Document doc = sxb.build(new File(filename));
		Element xml = doc.getRootElement();
		return xml;
	}	
//	 ###################################################################################################################################	
	public static Element findID(Element xml, String si)
	{
		String s2 = "";
		//System.out.println("I�m looking for an ID !!! "+si);
		//System.out.println(xml.getName());
		List lxml = xml.getChildren();
		Element classe = (Element) lxml.get(0);
		int i=1;
		s2 = classe.getAttributeValue("id"); 
		while (i<lxml.size() && !s2.equals(si))
		{
			classe=(Element) lxml.get(i);
			s2 = classe.getAttributeValue("id") ;
			i++;
		}
		if (!s2.equals(si)) classe=null;
		return classe;
	}
//	 ###################################################################################################################################
	
//	 ###################################################################################################################################	
	public static String findIDClass(Element xml, String si)
	{
		String s = "";
		String s2 = "";
		List lxml = xml.getChildren();
		Element classe = (Element) lxml.get(0);
		int i=1;
		s2 = classe.getAttributeValue("id"); 
		while (i<lxml.size() && !s2.equals(si))
		{
			classe=(Element) lxml.get(i);
			s2 = classe.getAttributeValue("id") ;
			i++;
		}
		if (s2.equals(si)) 
			{ s="Object";
			  if (!classe.getChild("type").getText().equals("Primitive")) s=classe.getChild("name").getText();
			
			}
		return s;
	}
// ###################################################################################################################################	
	public static Element getTypes(Element xml)
	{   Element classe, aux, aux2;
		Element newXML = new Element("types");
		String generalize, s;
		List lxml = xml.getChildren();
		
		
		for (int i = 0; i<lxml.size(); i++)
		{
			classe=(Element)lxml.get(i);
			aux = classe.getChild("generalization");
			generalize = "Object";
			if (aux!=null) {
				s = aux.getAttributeValue("id");
				aux2 = findID(xml,s);
				if (aux2!=null) 
					if (!aux2.getChild("type").getText().equals("Primitive")) generalize=aux2.getChild("name").getText();		
			}
			if (!classe.getChild("type").getText().equals("Primitive")) 
			{
				Element n = new Element("type");
				n.setAttribute("name",classe.getChild("name").getText());
				n.setAttribute("type",generalize);
			
				newXML.addContent(n);
				//System.out.println(classe.getChild("name").getText()+" ("+classe.getAttributeValue("id")+")  - "+generalize);
			}
		}
		
	return newXML;
	}
//	 ###################################################################################################################################
	public static Element getPredicates(Element xml)
	{
		int i,j,from,to;
		Element newXML = new Element("predicates");
		Element aux,aux2;
		List classes = xml.getChild("classes").getChildren();
		List Associations = xml.getChild("classAssociations").getChildren();
		List attributes, associationEnd;
	    // to extract information of boolean attributes -> unary predicates 
		for (i=0; i<classes.size(); i++)
		{
			aux = (Element) classes.get(i);
			if (aux.getChild("attributes")!=null)
			{
				attributes = aux.getChild("attributes").getChildren();
				for (j=0; j<attributes.size(); j++)
				{
					aux2 = (Element) attributes.get(j);
					if (aux2.getChild("type")!=null)
					{
						String s = aux2.getChild("type").getText();
						Element aux3 = null;
						if (!s.equals("")) 
							aux3 = findID(xml.getChild("classes"),s);
						if (aux3!=null && aux3.getChild("name").getText().equals("Boolean"))
							{
							Element n = new Element("predicate");
							n.setAttribute("name",aux2.getChild("name").getText());
							Element n2 = new Element("parameter");
							n2.setAttribute("type",aux.getChild("name").getText());
							n.addContent(n2);						
							newXML.addContent(n);
							}
						//if it is another class non Primitive
						else if(!aux3.getChild("type").getText().equals("Primitive")){
							Element n = new Element("predicate");
							n.setAttribute("name",aux2.getChild("name").getText());
							Element n2 = new Element("parameter");
							n2.setAttribute("type",aux.getChild("name").getText());
							Element n3 = new Element("parameter");
							n3.setAttribute("type",aux3.getChild("name").getText());
							n.addContent(n2);	
							n.addContent(n3);
							newXML.addContent(n);
						}
					}
				}
			}
		}
		// to extract information of association -> binary predicates
		for (i=0; i<Associations.size(); i++)
		{
			aux = (Element) Associations.get(i);
			if (aux.getChild("associationEnds")!=null)
			{
				associationEnd = aux.getChild("associationEnds").getChildren();
				from = to =0;
				for (j=0; j<associationEnd.size(); j++)
				{
					aux2 = (Element) associationEnd.get(j);
					if (aux2.getAttributeValue("navigation")!=null)
					{
						if (aux2.getAttributeValue("navigation").equals("true")) to=j;
						if (aux2.getAttributeValue("navigation").equals("false")) from=j;
					}
				}
				if (from!=0 || to!=0)
				{
					Element n = new Element("predicate");
					n.setAttribute("name",aux.getChild("name").getText());	
					Element n2 = new Element("parameter");
					aux2 = (Element) associationEnd.get(from);
					n2.setAttribute("type",findIDClass(xml.getChild("classes"),aux2.getAttributeValue("element-id")));
					n.addContent(n2);
					Element n3 = new Element("parameter");
					aux2 = (Element) associationEnd.get(to);
					n3.setAttribute("type",findIDClass(xml.getChild("classes"),aux2.getAttributeValue("element-id")));
					n.addContent(n3);
					newXML.addContent(n);
				}
			}
		}
		
		return newXML;
	}
	
//	 ###################################################################################################################################
	public static Element getObjectPredicates(Element xml, Element xmlSnapshot, Element xmlObj)
	{
		int i,j;
		Element newXML = new Element("predicates");
		Element aux,aux2;
		List objects = xmlSnapshot.getChild("objects").getChildren();
		List Associations = xmlSnapshot.getChild("associations").getChildren();
		List attributes, associationEnd;
	    // to extract information of boolean attributes -> unary predicates 
		for (i=0; i<objects.size(); i++)
		{
			aux = (Element) objects.get(i);
			String auxID = aux.getAttributeValue("id"); 
			Element auxFind = findID(xmlObj.getChild("objects"),auxID);
			String objectName = auxFind.getChild("name").getText();
			if (aux.getChild("attributes")!=null)
			{
				attributes = aux.getChild("attributes").getChildren();
				for (j=0; j<attributes.size(); j++)
				{
					aux2 = (Element) attributes.get(j);
					if (!aux2.getChildText("value").trim().equals("")){
						String classID = aux2.getAttributeValue("class");
						
						String ID = aux2.getAttributeValue("id");
						Element foundClass = findID(xml.getChild("elements").getChild("classes"),classID);
						Element foundAttribute = findID(foundClass.getChild("attributes"),ID);
	
						if (foundAttribute.getChild("type")!=null && aux2.getChild("value").getText()!=null)
						{
							String s = foundAttribute.getChild("type").getText();
							String sname = foundAttribute.getChild("name").getText();
							Element aux3 = null;
							if (!s.equals("")) aux3 = findID(xml.getChild("elements").getChild("classes"),s);
							s=aux3.getChild("name").getText();
							String s1 = aux3.getChild("type").getText();
							if (!s1.equals("Primitive") || s.equals("String"))
							{
								Element n = new Element("predicate");
								n.setAttribute("name",sname);
								n.setAttribute("style","regular");
								Element nn1 = new Element("parameters");
								Element n2 = new Element("parameter");
								n2.setAttribute("name",objectName);
								nn1.addContent(n2);					
								Element n3 = new Element("parameter");
								n3.setAttribute("name",aux2.getChild("value").getText());
								nn1.addContent(n3);
								n.addContent(nn1);
								newXML.addContent(n);
								
							} else
							{
								if (s.equals("Boolean"))
								{
									//TODO the false value should be shown by (not (predicate <parameter-list>))
									if (!aux2.getChildText("value").trim().equals("false")){
										Element n = new Element("predicate");
										n.setAttribute("name",sname);
										n.setAttribute("style","regular");
										Element nn1 = new Element("parameters");
										Element n2 = new Element("parameter");
										n2.setAttribute("name",objectName);
										nn1.addContent(n2);	
										n.addContent(nn1);
										newXML.addContent(n);	
									}
														
									
								} else
								{
								
									Element n = new Element("predicate");
									n.setAttribute("name",sname);
									n.setAttribute("style","assign");
									Element nn1 = new Element("parameters");
									Element n2 = new Element("parameter");
									n2.setAttribute("name",objectName);
									nn1.addContent(n2);					
									Element n3 = new Element("parameter");
									n3.setAttribute("name",aux2.getChild("value").getText());
									nn1.addContent(n3);
									n.addContent(nn1);
									newXML.addContent(n);
								}
								
								
							}
							
						}
						
					}
				}
			}
		}
		// to extract information of association -> binary predicates
		for (i=0; i<Associations.size(); i++)
		{
			aux = (Element) Associations.get(i);
			String objAssociationID = aux.getChild("classAssociation").getText();
			Element ClassAssociated = findID(xml.getChild("elements").getChild("classAssociations"),objAssociationID);
			String nameAssociation = ClassAssociated.getChild("name").getText();
			Element n = new Element("predicate");
			n.setAttribute("name",nameAssociation);	
			n.setAttribute("style","regular");
			Element nn1 =new Element("parameters");
			if (aux.getChild("associationEnds")!=null)
			{
				associationEnd = aux.getChild("associationEnds").getChildren();
				Element n1 = new Element("parameter");
				Element n2 = new Element("parameter");
				for (j=0; j<associationEnd.size(); j++)
				{
					aux2 = (Element) associationEnd.get(j);
					String AssocID = aux2.getAttributeValue("id");
					Element AssocEndsClass = ClassAssociated.getChild("associationEnds");
					Element AssocEnd = findID(xmlObj.getChild("objects"),aux2.getAttributeValue("element-id"));
					if (findID(AssocEndsClass,AssocID).getAttributeValue("navigation").equals("false"))
						n1.setAttribute("name",AssocEnd.getChild("name").getText());
					else n2.setAttribute("name",AssocEnd.getChild("name").getText());
									
				}
				nn1.addContent(n1);
				nn1.addContent(n2);
				
			}
			n.addContent(nn1);
			newXML.addContent(n);
		}
		
		return newXML;
	}
	
//	 ###################################################################################################################################
	public static String deleteSpace(String s)
	{
		String s1,s2;
		int i=s.indexOf(" ");
		while (i!=-1)
		{
			s1=s.substring(0,i);
			s2=s.substring(i+1);
			s=s1+s2;
			i=s.indexOf(" ");
		}
		return s;
		
	}
	
	
// #####################################################################################################################################
	public static String getTypeParameter(String param, Element params)
	{
		String s="";
		List parameters = params.getChildren();
		int i=0;
		while (i< parameters.size() && (!((Element) parameters.get(i)).getAttributeValue("name").equals(param))) i++;
		if (((Element) parameters.get(i)).getAttributeValue("name").equals(param)) s=((Element) parameters.get(i)).getAttributeValue("type");
		return s;
	}
	
//	 ###################################################################################################################################
	public static String transfPredicate(String s, boolean equality, Element params)
	{
		String s1="",s2,s3,sout="";
		int i,j,k;
		String op="";
		
		
		// First... testing the situation of increase and decrease instances
		if ((s.indexOf(" + ")!=-1 || s.indexOf(" - ")!=-1) || (s.indexOf(" * ")!=-1 || s.indexOf(" / ")!=-1))
		{
			i=-1;
			s = deleteSpace(s);
			//int i1,i2,i3;
			if (s.indexOf("+")!=-1) {i=s.indexOf("+"); op="increase";}
			if (s.indexOf("-")!=-1) {i=s.indexOf("-"); op="decrease";}
			if (s.indexOf("*")!=-1) {i=s.indexOf("*"); op="*";}
			if (s.indexOf("/")!=-1) {i=s.indexOf("/"); op="/";}
			s1=s.substring(0,i);
			s3=s.substring(i+1);
			i=s1.indexOf("=");
			s2=s1.substring(i+1);
			s1=s1.substring(0,i);			
			int i1 = s1.indexOf(".");
			//int i2 = s2.indexOf(".");
			int i3 = s3.indexOf(".");
			if (i1!=-1)
			{
				String ss1 = s1.substring(0,i1);
				String ss2 = s1.substring(i1+1);
				//sout = "("+ss2+" ?"+getTypeParameter(ss1,params)+ss2+")";
				sout = "("+ss2+" ?"+ss1+")";
			} else sout=s1;
			// s2 must be equal s1
			if (i3!=-1)
			{
				String ss1 = s3.substring(0,i3);
				String ss2 = s3.substring(i3+1);
				//sout = sout+" ("+ss2+" ?"+getTypeParameter(ss1,params)+ss2+")";
				sout = "("+ss2+" ?"+ss1+")";
			} else sout=sout+" "+s3;
			sout="("+op+" "+sout+")";
			
		} else
		{   s = deleteSpace(s);
			// Second... testing the situation of a comparison or equality
			k=s.indexOf(".");
			j=s.indexOf(".",k+1);
			// a comparison must have an operator and two dots
			if (((k!=-1) && (j!=-1)) && ((s.indexOf(">")!=-1 || s.indexOf("<")!=-1) || (s.indexOf("=")!=-1))) 
			{
				i=-1;
				if (s.indexOf(">")!=-1) {i=s.indexOf(">"); op=">";}
				if (s.indexOf("<")!=-1) {i=s.indexOf("<"); op="<";}
				if (s.indexOf("=")!=-1) 
					{
						i=s.indexOf("="); 
						if (equality) 
							{
							if (requirements.indexOf(":equality")==-1) requirements=requirements+" :equality";
							op="=";
							} else op="assign";
							
					}
				s1=s.substring(0,i);
				s2=s.substring(i+1);
				int i1 = s1.indexOf(".");
				int i2 = s2.indexOf(".");
				String ss1 = s1.substring(0,i1);
				String ss2 = s1.substring(i1+1);
				String ss3 = s2.substring(0,i2);
				String ss4 = s2.substring(i2+1); 
				//ss1 = "("+ss2+" ?"+getTypeParameter(ss1,params)+ss2+")";
				//ss3 = "("+ss4+" ?"+getTypeParameter(ss3,params)+ss4+")";
				ss1 = "("+ss2+" ?"+ss1+")";
				ss3 = "("+ss4+" ?"+ss3+")";
				sout = "("+op+" "+ss1+" "+ss3+")";
								
			} else
			{
				//this is a classical predicate
				// situation 1 - There is no dot and the variable is boolean
				if (s.indexOf(".")==-1)
				{
					i=s.indexOf("=");
					s1=s.substring(0,i);
					s2=s.substring(i+1);
					if (s2.toUpperCase().equals("FALSE") || s2.toUpperCase().equals("TRUE")) s2="";
					if (s2.equals("")) sout="("+s1+")"; else sout="("+s1+" ?"+s2+")";
										
				}
				//situation 2 - There is a dot and the variable is boolean or an attribute
				if (s.indexOf(".")!=-1)
				{
					i=s.indexOf("=");
					s1=s.substring(0,i);
					s3=s.substring(i+1);
					i=s1.indexOf(".");
					s2=s1.substring(i+1);
					s1=s1.substring(0,i);
					if (s3.toUpperCase().equals("FALSE") || s3.toUpperCase().equals("TRUE"))
					{ //situation 2 - There is dot and the relation is boolean
						sout="("+s2+" ?"+s1+")";
					} else
					{//situation 3 - There is dot and the relation is not boolean
						sout="("+s2+" ?"+s1+" ?"+s3+")";
					
					}		
				}
			
			}	
		}
		
		
		
		
		return sout;
	}
//	 ###################################################################################################################################
	/**
	 * This method adds those constraints from multiplicity of the class diagram (domain structure) into the constraints node
	 * 
	 * @return Element
	 */
	public static Element extractDomainConstraint(Element project, Element constraints){
		
		//1. Find association with multiplicities "1"
		List result = null;
		try {
			XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation/associationEnds/associationEnd[multiplicity='1' or multiplicity='0..1']");
			result = path.selectNodes(project.getDocument());
		} catch (JaxenException e2) {			
			e2.printStackTrace();
		}
			
		//2. For each one create constraint
		for (int i = 0; i < result.size(); i++){
			Element associationEndRef = (Element)result.get(i);
			
			Element associationEndSec = null;
			
			Element classAssociation = associationEndRef.getParentElement().getParentElement();
			
			Iterator associationEnds = classAssociation.getChild("associationEnds").getChildren("associationEnd").iterator();
			while (associationEnds.hasNext()){
				Element associationEnd = (Element)associationEnds.next();
				if (!associationEnd.equals(associationEndRef)){
					associationEndSec = associationEnd;
				}
			}
			
			//System.out.println(classAssociation.getChildText("name"));
			//System.out.println(associationEndRef.getAttributeValue("id")+"-"+associationEndSec.getAttributeValue("id"));
			
			//2.1 Finding the classes
			Element classRef = null;
			Element classSec = null;
			
			//2.1.1 Get reference class
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[@id="+associationEndRef.getAttributeValue("element-id")+"]");
				classRef = (Element)path.selectSingleNode(project.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			//2.1.2 Get secundary class
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[@id="+associationEndSec.getAttributeValue("element-id")+"]");
				classSec = (Element)path.selectSingleNode(project.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			//System.out.println(classRef.getChildText("name")+"-"+classSec.getChildText("name"));
			
			String comment = "Each "+ classSec.getChildText("name") +" can be '"+classAssociation.getChildText("name")+"' at most one "+
			classRef.getChildText("name") + " at a time.";	
			
			String constraintString = "(forall (?x1 ?x2 - " + classRef.getChildText("name") + 
			" ?y1 - " + classSec.getChildText("name") + ") (always (implies (and (" ;
			if (associationEndRef.getAttributeValue("navigation").equals("false")){
				constraintString = constraintString + classAssociation.getChildText("name") + " ?x1 ?y1) ("+
				classAssociation.getChildText("name") + " ?x2 ?y1)) (= ?x1 ?x2))))";
			}
			else{
				constraintString = constraintString + classAssociation.getChildText("name") + " ?y1 ?x1) ("+
				classAssociation.getChildText("name") + " ?y1 ?x2)) (= ?x1 ?x2))))";
				
			}
			Element constraint = new Element("constraint");
			Element value = new Element("value");
			Element description = new Element("description");
			constraint.addContent(value);
			constraint.addContent(description);
			
			constraint.getChild("value").setText(constraintString);
			constraint.getChild("description").setText(comment);
			
			constraints.addContent(constraint);
		}
		return constraints;
		
	}
	
	
	
	
	/**
	 * This method adds those constraints from the constants attributes into the constraints node
	 * 
	 * @return Element
	 */
	public static Element extractConstantAttributes(Element project, Element problem, Element constraints){
		
		
		//1. Get the init snapshot
		Element initSnapshot = null;
		try {
			XPath path = new JDOMXPath("objectDiagrams/objectDiagram[sequenceReference='init']");
			initSnapshot = (Element)path.selectSingleNode(problem);
		} catch (JaxenException e2) {			
			e2.printStackTrace();
		}		
		if (initSnapshot != null) {
			
			//2. Get all attributes	
			List result = null;
			try {
				XPath path = new JDOMXPath("objects/object/attributes/attribute");
				result = path.selectNodes(initSnapshot);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			for (int i = 0; i < result.size(); i++) {
				Element objectAttribute = (Element)result.get(i);
				
				// if the attribute has value then it require analysis
				if (!objectAttribute.getChildText("value").trim().equals("")) {
					//			attributes		objects
					Element objectReference = objectAttribute.getParentElement().getParentElement();
					
					//2.1 Find its object
					Element object = null;
					try {
						XPath path = new JDOMXPath("elements/objects/object[@id='"+objectReference.getAttributeValue("id")+"']");
						object = (Element)path.selectSingleNode(problem);
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					
					//2.2 get the class attribute
					Element classAttribute = null;
					try {
						XPath path = new JDOMXPath("project/elements/classes/class[@id='"+objectAttribute.getAttributeValue("class")+
								"']/attributes/attribute[@id='"+objectAttribute.getAttributeValue("id")+"']");
						classAttribute = (Element)path.selectSingleNode(problem.getDocument());
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					
					if (classAttribute != null) {
						if (classAttribute.getChildText("changeability").equals("frozen")) {
							Element constraint = new Element("constraint");
							Element value = new Element("value");
							Element description = new Element("description");
							constraint.addContent(value);
							constraint.addContent(description);
							String constraintString = null;
							String comment = null;
							
							//2. Get the type class
							Element typeClass = null;
							try {
								XPath path = new JDOMXPath("project/elements/classes/class[@id='"+classAttribute.getChildText("type")+"']");
								typeClass = (Element)path.selectSingleNode(problem.getDocument());
							} catch (JaxenException e2) {			
								e2.printStackTrace();
							}
							
							if (typeClass != null){
								
								//Primitives Booloean, Int, Float, String
								if (typeClass.getChildText("type").equals("Primitive")){
									if (typeClass.getChildText("name").equals("Boolean")) {
										constraintString = "(always ("+classAttribute.getChildText("name")+ " "+object.getChildText("name") + "))";
										comment = object.getChildText("name") + " is always " + classAttribute.getChildText("name") + " (Constant)"; 
									} 
									else if (typeClass.getChildText("name").equals("Int") || typeClass.getChildText("name").equals("Float")){
										constraintString = "(always (= ("+classAttribute.getChildText("name")+ " "+object.getChildText("name") + ") " + objectAttribute.getChildText("value") + "))";
										comment = "The " + classAttribute.getChildText("name") + " of the " + object.getChildText("name") + " is always " + objectAttribute.getChildText("value") + " (Constant)"; 
									} 
									else if (typeClass.getChildText("name").equals("String")){
										constraintString = "(always ("+classAttribute.getChildText("name")+ " "+object.getChildText("name") + " " + objectAttribute.getChildText("value") + "))";
										comment = object.getChildText("name") + " is always '" + classAttribute.getChildText("name") + "' "+ objectAttribute.getChildText("value") + " (Constant)"; 
									}
								}
								//Classes types
								else{
									constraintString = "(always ("+classAttribute.getChildText("name")+ " "+object.getChildText("name") + " " + objectAttribute.getChildText("value") + "))"; 
									comment = object.getChildText("name") + " is always '" + classAttribute.getChildText("name") + "' "+ objectAttribute.getChildText("value")  + " (Constant)"; 
								}
								
								constraint.getChild("value").setText(constraintString);
								constraint.getChild("description").setText(comment);
								
								constraints.addContent(constraint);
								
							}
							

						}
						
						
					}
				}
				
			}
			
			
		}
		
		return constraints;
		
	}
	
//	 ###################################################################################################################################
	
	
	public static Element findActions(List diagrams, String numberOfClass, String numberOfOperator, Element params) throws IOException
	{
		int i,j,k,h;	
		Element newAction = new Element("features");
		String textPrec="", textDelete="", textInsert="";
		Element precondition = new Element("precondition");
		Element effects = new Element("effects");
		Element deleteList = new Element("deleteList");
		Element insertList = new Element("insertList");
		newAction.addContent(precondition);
		effects.addContent(deleteList);
		effects.addContent(insertList);
		newAction.addContent(effects);
		for (i=0; i<diagrams.size(); i++)
		{
			Element diagram = (Element) diagrams.get(i);
			List associations = diagram.getChild("associations").getChildren();
			for (j=0; j<associations.size();j++)
			{
				Element action = (Element) associations.get(j);
				String classe = action.getChild("reference").getAttributeValue("class");
				String operator = action.getChild("reference").getAttributeValue("operator");
				if (classe.equals(numberOfClass) && operator.equals(numberOfOperator))
				{
					//if the nymber of the action is correct, then extract pre and post conditions
					// first... extracting precondition
					String cond = action.getChild("precondition").getText();
					String[] prec = cond.split(" and ");
					if (!cond.equals("")) 
					{
						for (k=0; k<prec.length; k++)
						{
							String s = prec[k];
							String sUpper = deleteSpace(s).toUpperCase();
							if (sUpper.indexOf("=FALSE")==-1)
							{
								//text = newAction.getChild("precondition").getText();
								textPrec = textPrec +" "+transfPredicate(s,true,params);
								//newAction.getChild("precondition").setText(text);
													
							}
							
						}
					}
					// now ... extracting post condition (it can be delete or insert list
					cond = action.getChild("postcondition").getText();
					String [] post = cond.split(" and ");
					if (!cond.equals("")) 
					{
						for (k=0; k<post.length; k++)
						{
							String s = post[k];
							String sUpper = deleteSpace(s).toUpperCase();
							if (sUpper.indexOf("=FALSE")==-1)
							{
								//text = newAction.getChild("effects").getChild("insertList").getText();
								textInsert = textInsert +" "+transfPredicate(s,false,params);
								//newAction.getChild("effects").getChild("insertList").setText(text);
													
							} else {
								textDelete = textDelete +" (not "+transfPredicate(s,false,params)+")";
								//newAction.getChild("effects").getChild("deleteList").setText(transfPredicate(s));
							}							
						}
					}	
					//finding and complete precond and postcond with features in the States definitions
					List states = action.getChild("associationEnds").getChildren();
					for (h=0; h<states.size(); h++)
					{
						Element actionEnd = (Element) states.get(h);
						String stateID = actionEnd.getAttributeValue("element-id");
						Element state = findID(diagram.getChild("states"),stateID);
						if (state!=null)
						{
						  if (actionEnd.getAttributeValue("navigation").equals("false"))
						  {
							cond="";
							if (state.getChild("condition")!=null) cond = state.getChild("condition").getText();
							prec = cond.split(" and ");
							if (!cond.equals("")) 
							{
								for (k=0; k<prec.length; k++)
								{
									String s = prec[k];
									String sUpper = deleteSpace(s).toUpperCase();
									if (sUpper.indexOf("=FALSE")==-1)
									{
										textPrec = textPrec +" "+transfPredicate(s,true,params);
										textDelete = textDelete +" (not "+transfPredicate(s,false,params)+")";
															
									}						
								}
							}		
						  } else {
							cond="";
							if (state.getChild("condition")!=null) cond = state.getChild("condition").getText();
							post = cond.split(" and ");
							if (!cond.equals("")) 
							{
								for (k=0; k<post.length; k++)
								{
									String s = post[k];
									String sUpper = deleteSpace(s).toUpperCase();
									if (sUpper.indexOf("=FALSE")==-1)
									{
										textInsert = textInsert +" "+transfPredicate(s,false,params);					
									} else {
										textDelete = textDelete +" (not "+transfPredicate(s,false,params)+")";
									}							
								}
							}	
							
							
						  }
						}
						
					}
					
				}	
				
			}
			
			
		}
	newAction.getChild("precondition").setText(textPrec);
	newAction.getChild("effects").getChild("insertList").setText(textInsert);
	newAction.getChild("effects").getChild("deleteList").setText(textDelete);
	return (newAction)	;
		
	}
//	 ###################################################################################################################################
	public static Element getParameters(Element operator, Element classes)
	{
		List parameters = operator.getChild("parameters").getChildren();
		Element params = new Element("parameters");
		for (int i=0; i<parameters.size(); i++)
		{
			Element parameter = (Element) parameters.get(i);
			String name = parameter.getChild("name").getText();
			String type = parameter.getChild("type").getText();
			type = findIDClass(classes,type);
			Element param = new Element("parameter");
			param.setAttribute("name",name);
			param.setAttribute("type",type);
			params.addContent(param);
			
		}
	return (params);
	
	}
	
	
//	 ###################################################################################################################################
	public static Element getActions(Element xml) throws IOException
	{
		int l,h;
		Element newXML = new Element("actions");
		List classes = xml.getChild("elements").getChild("classes").getChildren();
		List diagrams = xml.getChild("diagrams").getChild("stateChartDiagrams").getChildren();
		
		
		// find each action in Class Diagram
		l=classes.size();
		for (l=0; l<classes.size(); l++)
		{
			Element class2=(Element) classes.get(l);
			List operators = null;
			if (class2.getChild("operators")!=null) operators=class2.getChild("operators").getChildren();
			String numberOfClass = ((Element) classes.get(l)).getAttributeValue("id"); 
			//System.out.println("numero da classe ="+numberOfClass);
			//h=operators.size();
			if (operators!=null && operators.size()>0)
			{
				for (h=0; h<operators.size(); h++)
				{
					Element act= new Element("action");
					Element oneOperator=(Element)operators.get(h);
					String numberOfOperator=oneOperator.getAttributeValue("id");
					//System.out.println("A a��o da classe id="+numberOfClass+" Operator = "+numberOfOperator);
					act.setAttribute("name",oneOperator.getChild("name").getText());
					//Finding the correspondent actions in StateChartDiagrams	
					if (oneOperator.getChild("timeConstraints").getAttributeValue("timed").equals("true"))
					{
						String stime =	oneOperator.getChild("timeConstraints").getChild("duraction").getText();					
						Element duration = new Element("duration");
						duration.setAttribute("value",stime);
						act.addContent(duration);
						
					}
					Element parameters = getParameters(oneOperator, xml.getChild("elements").getChild("classes"));
					Element newActionFeatures = findActions(diagrams, numberOfClass, numberOfOperator, parameters);
					act.addContent(parameters);
					act.addContent(newActionFeatures);
					newXML.addContent(act);
					
				}	
				
				
			}
						
		}
		
			
		return newXML;
		
		
	}
	
// ####################################################################################################################################
	public static Element getFluents(Element xml)
	{
		Element newXML = new Element("fluents");
		int i,j;
		Element aux,aux2;
		String nameOfClass="";
		List classes = xml.getChild("classes").getChildren();
		List attributes;
	     // to extract information of integer and float attributes -> function of fluents 
		for (i=0; i<classes.size(); i++)
		{
			aux = (Element) classes.get(i);
			nameOfClass = aux.getChild("name").getText();
			if (aux.getChild("attributes")!=null)
			{
				attributes = aux.getChild("attributes").getChildren();
				for (j=0; j<attributes.size(); j++)
				{
					aux2 = (Element) attributes.get(j);
					if (aux2.getChild("type")!=null)
					{
						String s = aux2.getChild("type").getText();
						Element aux3 = null;
						if (!s.equals("")) aux3 = findID(xml.getChild("classes"),s);
						if ((aux3!=null) && (aux3.getChild("name").getText().equals("Int") || aux3.getChild("name").getText().equals("Float")))
						{
							Element n = new Element("fluent");
							n.setAttribute("name",aux2.getChild("name").getText());
							n.setAttribute("var",nameOfClass+aux2.getChild("name").getText());
							n.setAttribute("type",nameOfClass);
							if (aux2.getChild("initialValue")!=null && !aux2.getChild("initialValue").getText().equals("")) 
							{
								Element n2 = new Element("init");
								n2.setAttribute("value",aux2.getChild("initialValue").getText());
								n.addContent(n2);
								
							}
							newXML.addContent(n);
						}
					}
				}
			}
		}
		
		return newXML;
	}
	
// ####################################################################################################################################
	public static Element getStates(Element xml, Element problem)
	{
			Element name = new Element("name");
			name.addContent(problem.getChild("name").getText());
			Element prob = new Element("problem");
			prob.addContent(name);
			Element objects = new Element("objects");
			List objs = problem.getChild("elements").getChild("objects").getChildren();
			for (int k=0; k<objs.size();k++)
			{
				Element obj = (Element) objs.get(k);
				Element newObj = new Element("object");
				newObj.setAttribute("name",obj.getChild("name").getText());
				newObj.setAttribute("type",findIDClass(xml.getChild("elements").getChild("classes"),obj.getChild("class").getText()));
				objects.addContent(newObj);		
			}
			prob.addContent(objects);
			Element constraints = new Element("constraints");
			extractDomainConstraint(xml,constraints);
			extractConstantAttributes(xml, problem, constraints);
			Element snapshots = new Element ("snapshots");
			List shots = problem.getChild("objectDiagrams").getChildren();
			for (int k = 0; k<shots.size(); k++)
			{
				Element shot = (Element) shots.get(k);
				Element snapshot = new Element ("snapshot");
				String reference = shot.getChild("sequenceReference").getText();
				if (reference.equals("init") || reference.equals("goal"))
				{
					snapshot.setAttribute("name",shot.getChild("sequenceReference").getText());
					snapshot.addContent(getObjectPredicates(xml,shot,problem.getChild("elements")));
					snapshots.addContent(snapshot);
				} else
				{
				 Element preds = getObjectPredicates(xml,shot,problem.getChild("elements"));	
				 String s1="("+reference+" (and ";
				 List predicates = preds.getChildren();
					for (int p=0; p<predicates.size(); p++)
					{
						Element predicate = (Element) predicates.get(p);
						if (predicate.getAttributeValue("style").equals("regular"))
						{
							List parameters = predicate.getChild("parameters").getChildren();
							// check if any parameter is ampty
							//boolean isEmpty = false;
							//for (int l=0; l<parameters.size(); l++){
							//	if(((Element) parameters.get(l)).getAttributeValue("name").trim().equals("")){
							//		isEmpty = true;
							//		break;
							//	}
							//}
							//if (!isEmpty){
								s1 = s1 + " ("+predicate.getAttributeValue("name");//+" ";
								for (int l=0; l<parameters.size(); l++){
									s1 = s1 +" " + ((Element) parameters.get(l)).getAttributeValue("name");
								}	
								s1 = s1 +")";
							//}			
						} else 
						{
							List parameters = predicate.getChild("parameters").getChildren();
							// check if any parameter is ampty
							//boolean isEmpty = false;
							//if (((Element) parameters.get(0)).getAttributeValue("name").trim().equals("") || 
							//		((Element) parameters.get(1)).getAttributeValue("name").trim().equals("")){
							//		isEmpty = true;
							//}
							//if (!isEmpty){
								s1 = s1 + " (= ("+predicate.getAttributeValue("name");
								s1 = s1 + " "+ ((Element) parameters.get(0)).getAttributeValue("name")+")";
								s1 = s1 + " "+ ((Element) parameters.get(1)).getAttributeValue("name")+")";
							//}
							
						}
						
					}
				Element value = new Element("value");
				value.addContent(s1);
				Element description = new Element("description");
				description.addContent("Plan constraints");
				Element constraint = new Element("constraint");
				constraint.addContent(description);
				constraint.addContent(value);
				constraints.addContent(constraint);
				
					
				}
			}
			prob.addContent(snapshots);
			
			prob.addContent(constraints);
			
			
		
		
		
		return prob;
	}
	
	
	
	
//	 ###################################################################################################################################
	public static ArrayList createPDDL(Element xml)
	{
		ArrayList s = new ArrayList();
		s.add(";;");
		List descrip = xml.getChild("descriptions").getChildren();
		for (int i=0; i<descrip.size(); i++)
		{
			Element desc = (Element) descrip.get(i);
			s.add(";; "+desc.getText());
		}
		s.add(";;");
		s.add(" ");
		s.add(" ");
		// extracting types
		List types = xml.getChild("types").getChildren();
		s.add("(:types");
		for (int i=0; i<types.size(); i++)
		{
			Element type = (Element) types.get(i);
			s.add("     "+type.getAttributeValue("name")+" - "+type.getAttributeValue("type"));
			
		}
		s.add(")");
		s.add(" ");
		s.add(" ");
		// extracting functions (fluents)
		
		List fluents = xml.getChild("fluents").getChildren();
		if (fluents!=null && fluents.size()>0) 
			{
				s.add("(:function");
				for (int i=0; i<fluents.size(); i++)
				{
				Element fluent = (Element) fluents.get(i);
				if (requirements.indexOf(":fluents")==-1) requirements=requirements+" :fluents";
				//String sfluent = "     ("+fluent.getAttributeValue("name")+" ?"+fluent.getAttributeValue("var")+" - "+fluent.getAttributeValue("type")+") ";
				String sfluent = "     ("+fluent.getAttributeValue("name")+" ?"+fluent.getAttributeValue("type").toLowerCase()+(i+1)+" - "+fluent.getAttributeValue("type")+") ";
				//if (fluent.getChild("init")!=null && !fluent.getChild("init").getAttributeValue("value").equals(""))
					//sfluent = "( = "+sfluent + " "+fluent.getChild("init").getAttributeValue("value")+")";
				s.add(sfluent);
				
				}
			s.add(")");
			s.add(" ");
			s.add(" ");
			}
			
		
		// extracting predicates
		List preds = xml.getChild("predicates").getChildren();
		s.add("(:predicates");
		for (int i=0; i<preds.size(); i++)
		{
			Element pred = (Element) preds.get(i);
			List params = pred.getChildren();
			String parameters="";
			for (int k=0; k<params.size(); k++)
			{
				String s1 = ((Element) params.get(k)).getAttributeValue("type");
				int l=0;
				String aux="?"+s1.toLowerCase();
				String aux2=aux+" ";
				while (parameters.indexOf(aux2)!=-1) {
					l++;
					aux2=aux+l+" ";
				}
				parameters = parameters +" "+aux2+" - " + s1;
			}
			s.add("     ("+pred.getAttributeValue("name")+parameters+")");
			
		}
		s.add(")");
		s.add(" ");
		s.add(" ");
		// extracting actions
		
		List acts = xml.getChild("actions").getChildren();
		for (int i=0; i<acts.size(); i++)
		{
			Element act = (Element) acts.get(i);
			if (act.getChild("duration")!=null) 
				{
				if (requirements.indexOf(":durative")==-1) requirements=requirements+" :durative-actions";
				s.add("(:durative-action "+act.getAttributeValue("name")); 
				} else s.add("(:action "+act.getAttributeValue("name"));
			List paramsAct = act.getChild("parameters").getChildren();
			String param = "   :parameters     (";
			for (int k=0; k<paramsAct.size(); k++)
			{
				Element paramAct = (Element) paramsAct.get(k);
				param = param + "  ?"+paramAct.getAttributeValue("name")+" - "+paramAct.getAttributeValue("type");
				
			}
			s.add(param +")");
			if (act.getChild("duration")!=null)
			{
				s.add("   :duration       (= ?duration "+act.getChild("duration").getAttributeValue("value")+")");
			}
			Element features = act.getChild("features");
			String precond = features.getChild("precondition").getText();
			precond = "(and "+precond+")"; //else precond = "("+precond+")";
			s.add("   :precondition   "+precond);
			String effects = "   :effect        (and ";
			String deleteList = features.getChild("effects").getChild("deleteList").getText();
			String insertList = features.getChild("effects").getChild("insertList").getText();
			effects = effects+deleteList+" "+insertList+")";
			s.add(effects);
			s.add(")");
			
			
		}//*/
		//if ((requirements.indexOf(":durative-actions")==-1 && requirements.indexOf(":fluents")==-1) && (requirements.indexOf(":equality")==-1))requirements = ":strips "+requirements;
		requirements = ":constraints "+requirements;
		s.add(6,"(:requirements "+requirements+")");
		s.add(6,"(define (domain "+((String)xml.getAttributeValue("name")).replaceAll(" ", "")+")");
		s.add(")");
		return s;
	
	}
	
// #####################################################################################################################################
	public static ArrayList createPDDLProb(Element xml)
	{
		ArrayList s = new ArrayList();
		List objs = xml.getChild("problem").getChild("objects").getChildren();
		List snapshots = xml.getChild("problem").getChild("snapshots").getChildren();
		
		s.add(";;");
		s.add(" ");
		s.add("(define (problem "+((String)xml.getChild("problem").getChild("name").getText()).replaceAll(" ","")+")");
		s.add("(:domain "+((String)xml.getAttributeValue("domain")).replaceAll(" ","")+")");
		s.add("(:objects");
		
		String s1 = "";
		//s.add(" (and ");//-----
		for (int k=0; k<objs.size(); k++)
		{
			Element obj = (Element) objs.get(k);
			
			//s1 = s1+"("+obj.getAttributeValue("name")+" - "+obj.getAttributeValue("type")+") ";
			s.add("	 "+obj.getAttributeValue("name")+" - "+obj.getAttributeValue("type")+" ");	
		}
		//s1= s1+"))";
		//s.add(s1);
		//s.add("))");
		s.add(")");
		
		s.add("");
		s.add("(:init");
		//s1 = "(and ";
		int i=0; 
		while (i<snapshots.size() && !(((Element) snapshots.get(i)).getAttributeValue("name").equals("init"))) i++;
		if (i<snapshots.size()) 
		{
			List predicates = ((Element) snapshots.get(i)).getChild("predicates").getChildren();
			for (int k=0; k<predicates.size(); k++)
			{
				Element predicate = (Element) predicates.get(k);
				if (predicate.getAttributeValue("style").equals("regular"))
				{
					s1 = s1 + "("+predicate.getAttributeValue("name");
					List parameters = predicate.getChild("parameters").getChildren();
					for (int l=0; l<parameters.size(); l++){
						s1 = s1 + " "+((Element) parameters.get(l)).getAttributeValue("name");
					}
					s1 = s1 +") ";			
				} else 
				{
					s1 = s1 + "(= ("+predicate.getAttributeValue("name")+" ";
					List parameters = predicate.getChild("parameters").getChildren();
					s1 = s1 + ((Element) parameters.get(0)).getAttributeValue("name")+") ";
					s1 = s1 + ((Element) parameters.get(1)).getAttributeValue("name")+") ";
					
				}
				
			}
			
		} 
		//s1=s1+")";
		s.add(s1);
		s.add(")");
		
		s.add("");
		s.add("(:goal");
		s1 = "(and ";
		i=0; 
		while (i<snapshots.size() && !(((Element) snapshots.get(i)).getAttributeValue("name").equals("goal"))) i++;
		if (i<snapshots.size()) 
		{
			List predicates = ((Element) snapshots.get(i)).getChild("predicates").getChildren();
			for (int k=0; k<predicates.size(); k++)
			{
				Element predicate = (Element) predicates.get(k);
				if (predicate.getAttributeValue("style").equals("regular"))
				{
					s1 = s1 + "("+predicate.getAttributeValue("name");
					List parameters = predicate.getChild("parameters").getChildren();
					for (int l=0; l<parameters.size(); l++){
						s1 = s1 + " " + ((Element) parameters.get(l)).getAttributeValue("name");
					}
					s1 = s1 +") ";			
				} else 
				{
					s1 = s1 + "(= ("+predicate.getAttributeValue("name")+" ";
					List parameters = predicate.getChild("parameters").getChildren();
					s1 = s1 + ((Element) parameters.get(0)).getAttributeValue("name")+") ";
					s1 = s1 + ((Element) parameters.get(1)).getAttributeValue("name")+") ";
					
				}
				
			}
			
		} 
		s1=s1+")";
		s.add(s1);
		
		List constraints = xml.getChild("problem").getChild("constraints").getChildren();
		if (constraints!=null)
		{
			s.add("");			
			s.add("(:constraints");
			s.add("(and");
			
			for (int j = 0; j < constraints.size(); j++) {
				Element constraint = (Element) constraints.get(j);
				s.add(" ");
				s.add(";; "+constraint.getChildText("description"));
				s.add(constraint.getChildText("value"));		
			}
			s.add("))");
		}
		
		s.add(")");
	
		
		return s;	
		
	}
	
	
//	 ###################################################################################################################################
	public static void printPDDL(ArrayList s)
	{
		int i;
		for (i=0; i<s.size();i++) System.out.println(s.get(i));
	}
	
// ####################################################################################################################################
	public static ArrayList XMLtoPDDLProblem(Element xml, Element xmlProb) throws IOException
	{
	Element newXML = new Element("projectProblem");
	newXML.setAttribute("domain",xml.getChild("name").getText());
	newXML.addContent(getStates(xml,xmlProb));
	print(newXML);
	ArrayList pddlProb = createPDDLProb(newXML);
	//printPDDL(pddlProb);
	
	return pddlProb;
		
	}
	
	
//#####################################################################################################################################
	public static ArrayList XMLtoPDDLDomain(Element xml) throws IOException
	{
		Element newXML = new Element("project");
		newXML.setAttribute("name",xml.getChild("name").getText());
		Element descriptions = new Element("descriptions");
		Element description1 = new Element("description");
		description1.setText(xml.getChild("description").getText());
		descriptions.addContent(description1);
		Element description2 = new Element("description");
		description2.setText("Designed by tool "+xml.getChild("generalInformation").getChild("tool").getText()+" - "+xml.getChild("generalInformation").getChild("version").getText() );
		descriptions.addContent(description2);
		newXML.addContent(descriptions);
		newXML.addContent(getTypes(xml.getChild("elements").getChild("classes")));
		newXML.addContent(getPredicates(xml.getChild("elements")));
		newXML.addContent(getFluents(xml.getChild("elements")));
		newXML.addContent(getActions(xml));
		//print(newXML);
		
		ArrayList s2 = createPDDL(newXML);
		return(s2);
		
		
	}
	
	
//	 ###################################################################################################################################
	/*public static void main(String[] args) throws JDOMException, IOException
	{
		Element xml = readFromFile("C:/JAVA/logisticDomain-v2.xml");
		//ArrayList pddl = XMLtoPDDLDomain(xml);
		ArrayList pddl = XMLtoPDDLProblem(xml,findID(xml.getChild("diagrams").getChild("planningProblems"),"2"));
		printPDDL(pddl);
		
				 
	}*/
			
}
