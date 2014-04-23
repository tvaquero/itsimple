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

package languages.pddl;

import java.io.IOException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import itSIMPLE.ItSIMPLE;
import languages.xml.XMLUtilities;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jdom.JDOMException;

import languages.uml.ocl.ExpressionTreeBuilder;
import languages.uml.ocl.OCLUtilities;

public class ToXPDDL {
	
	private static Element xpddlDomain;
	private static Element xpddlProblem;
	//private static Element project = null;
	
	private static final String PRECONDITION = "precondition";
	private static final String POSTCONDITION = "postcondition";	
	public static final String PDDL_2_1 = "pddl21";
	public static final String PDDL_2_2 = "pddl22";
	public static final String PDDL_3_0 = "pddl30";
	public static final String PDDL_3_1 = "pddl31";
	public static final String TIME_METRIC_REPLACEMENT = "total#time";	
	
	private static List<Element> constraintsList = new ArrayList<Element>();


	
	public static Element XMLToXPDDLDomain(Element project, String pddlVersion, List<Element> instructions){
		//ToXPDDL.project = project;
		Element xpddlNodes = null;
		xpddlDomain = null;
		
		try {
			xpddlNodes = (Element)XMLUtilities.readFromFile("resources/settings/commonData.xml").getRootElement().getChild("xpddlNodes").clone();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(xpddlNodes != null){
			xpddlDomain = (Element)xpddlNodes.getChild("xpddlDomain").clone();			
			
			//1. Name
			String domainName = project.getChildText("name").trim().replaceAll(" ", "_");
			if (domainName.toLowerCase().equals("domain")) domainName += "1"; //The "problem" word is a reserved word in the pddl context so it is necessary to change the name in this case
			xpddlDomain.getChild("name").setText(domainName);
			
			
			//2. Types
			//2.1 from classes
			Element classesHierarchyTree = XMLUtilities.getClassesHierarchyTree(project);
			fillOutTypes(xpddlDomain.getChild("types"), classesHierarchyTree);
			
			//2.2 from enumerations
			List<?> enumerations = project.getChild("elements").getChild("classes").getChildren("enumeration");
			for (Iterator<?> iter = enumerations.iterator(); iter.hasNext();) {
				Element enumeration = (Element) iter.next();
				Element type = new Element("type");
				type.setAttribute("name", enumeration.getChildText("name"));
				type.setAttribute("parent", "object");
				xpddlDomain.getChild("types").addContent(type);
				
				// 3. constants
				List<?> literals = enumeration.getChild("literals").getChildren("literal");
				for (Iterator<?> iterator = literals.iterator(); iterator.hasNext();) {
					Element literal = (Element) iterator.next();
					Element constant = new Element("constant");
					constant.setAttribute("name", literal.getChildText("name"));
					constant.setAttribute("type", enumeration.getChildText("name"));
					xpddlDomain.getChild("constants").addContent(constant);
				}
			}

		}
		
		//4. Predicates
		
		//4.1 Associations
		List<?> classAssociations = null;
		try {
			XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation");
			classAssociations = path.selectNodes(project.getDocument());
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		for(Iterator<?> iter = classAssociations.iterator(); iter.hasNext();){
			Element classAssociation = (Element)iter.next();
			Element source = (Element)classAssociation.getChild("associationEnds").getChildren("associationEnd").get(0);
			Element target = (Element)classAssociation.getChild("associationEnds").getChildren("associationEnd").get(1);
			
			Element sourceClass = null;
			Element targetClass = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[@id='" + source.getAttributeValue("element-id") + "']");
				sourceClass = (Element)path.selectSingleNode(project.getDocument());
				path = new JDOMXPath("project/elements/classes/class[@id='" + target.getAttributeValue("element-id") + "']");
				targetClass = (Element)path.selectSingleNode(project.getDocument());
			} catch (JaxenException e) {			
				e.printStackTrace();
			}			
			if(sourceClass != null && targetClass != null){
				// 4.1.1 Associations with rolenames: the preference is given to the rolenames
				// this is done because the classes name may be lower than 3
				int sourceLength = (sourceClass.getChildText("name").length() > 3) ?3 :sourceClass.getChildText("name").length();
				int targetLength = (targetClass.getChildText("name").length() > 3) ?3 :targetClass.getChildText("name").length();
				if(!source.getChild("rolename").getChildText("value").trim().equals("")){

                                        if (!pddlVersion.equals(PDDL_3_1)){
                                            Element predicate = new Element("predicate");
                                            predicate.setAttribute("name", source.getChild("rolename").getChildText("value"));

                                            Element predicateType = new Element("parameter");
                                            predicateType.setAttribute("name", targetClass.getChildText("name").substring(0,targetLength).toLowerCase());
                                            predicateType.setAttribute("type", targetClass.getChildText("name"));
                                            predicate.addContent(predicateType);

                                            Element predicateValueType = new Element("parameter");
                                            if(sourceClass.getChildText("name").substring(0,sourceLength).toLowerCase().equals(targetClass.getChildText("name").substring(0,sourceLength).toLowerCase())){
                                                    predicateValueType.setAttribute("name", sourceClass.getChildText("name").substring(0,sourceLength).toLowerCase() + "1");
                                            }
                                            else{
                                                    predicateValueType.setAttribute("name", sourceClass.getChildText("name").substring(0,sourceLength).toLowerCase());
                                            }
                                            predicateValueType.setAttribute("type", sourceClass.getChildText("name"));
                                            predicate.addContent(predicateValueType);

                                            xpddlDomain.getChild("predicates").addContent(predicate);
                                        }
                                        //If it is PDDL 3.1 or up
                                        else{
                                            boolean isMultipliticy1or01 = false;
                                            String xpddlTag = "predicates";
                                            if (source.getChild("multiplicity").getChildText("value").equals("1") || source.getChild("multiplicity").getChildText("value").equals("0..1")){
                                                isMultipliticy1or01 = true;
                                                xpddlTag = "functions";
                                            }
                                            
                                            Element theAssoc = null;
                                            if (isMultipliticy1or01){
                                                theAssoc = new Element("function");
                                            }else{
                                                theAssoc = new Element("predicate");
                                            }
                                            theAssoc.setAttribute("name", source.getChild("rolename").getChildText("value"));

                                            Element predicateType = new Element("parameter");
                                            predicateType.setAttribute("name", targetClass.getChildText("name").substring(0,targetLength).toLowerCase());
                                            predicateType.setAttribute("type", targetClass.getChildText("name"));
                                            theAssoc.addContent(predicateType);

                                            if (isMultipliticy1or01){
                                                theAssoc.setAttribute("type", sourceClass.getChildText("name"));
                                            }
                                            else{
                                                Element predicateValueType = new Element("parameter");
                                                if(sourceClass.getChildText("name").substring(0,sourceLength).toLowerCase().equals(targetClass.getChildText("name").substring(0,sourceLength).toLowerCase())){
                                                        predicateValueType.setAttribute("name", sourceClass.getChildText("name").substring(0,sourceLength).toLowerCase() + "1");
                                                }
                                                else{
                                                        predicateValueType.setAttribute("name", sourceClass.getChildText("name").substring(0,sourceLength).toLowerCase());
                                                }
                                                predicateValueType.setAttribute("type", sourceClass.getChildText("name"));
                                                theAssoc.addContent(predicateValueType);
                                            }

                                            xpddlDomain.getChild(xpddlTag).addContent(theAssoc);
                                        }
				}
				if(!target.getChild("rolename").getChildText("value").trim().equals("")){

                                    if (!pddlVersion.equals(PDDL_3_1)){
					Element predicate = new Element("predicate");
					predicate.setAttribute("name", target.getChild("rolename").getChildText("value"));
					
					Element predicateType = new Element("parameter");
					predicateType.setAttribute("name", sourceClass.getChildText("name").substring(0,sourceLength).toLowerCase());
					predicateType.setAttribute("type", sourceClass.getChildText("name"));
					predicate.addContent(predicateType);
					
					Element predicateValueType = new Element("parameter");					
					if(targetClass.getChildText("name").substring(0,targetLength).toLowerCase().equals(sourceClass.getChildText("name").substring(0,sourceLength).toLowerCase())){
						predicateValueType.setAttribute("name", targetClass.getChildText("name").substring(0,targetLength).toLowerCase() + "1");
					}
					else{
						predicateValueType.setAttribute("name", targetClass.getChildText("name").substring(0,targetLength).toLowerCase());
					}
					
					predicateValueType.setAttribute("type", targetClass.getChildText("name"));
					predicate.addContent(predicateValueType);
					
					xpddlDomain.getChild("predicates").addContent(predicate);
                                    }
                                    //If it is PDDL 3.1 or up
                                    else{
                                            boolean isMultipliticy1or01 = false;
                                            String xpddlTag = "predicates";
                                            if (target.getChild("multiplicity").getChildText("value").equals("1") || target.getChild("multiplicity").getChildText("value").equals("0..1")){
                                                isMultipliticy1or01 = true;
                                                xpddlTag = "functions";
                                            }

                                            Element theAssoc = null;
                                            if (isMultipliticy1or01){
                                                theAssoc = new Element("function");
                                            }else{
                                                theAssoc = new Element("predicate");
                                            }
                                            theAssoc.setAttribute("name", target.getChild("rolename").getChildText("value"));

                                            Element predicateType = new Element("parameter");
                                            predicateType.setAttribute("name", sourceClass.getChildText("name").substring(0,targetLength).toLowerCase());
                                            predicateType.setAttribute("type", sourceClass.getChildText("name"));
                                            theAssoc.addContent(predicateType);

                                            if (isMultipliticy1or01){
                                                theAssoc.setAttribute("type", targetClass.getChildText("name"));
                                            }
                                            else{
                                                Element predicateValueType = new Element("parameter");
                                                if(targetClass.getChildText("name").substring(0,sourceLength).toLowerCase().equals(sourceClass.getChildText("name").substring(0,sourceLength).toLowerCase())){
                                                        predicateValueType.setAttribute("name", targetClass.getChildText("name").substring(0,sourceLength).toLowerCase() + "1");
                                                }
                                                else{
                                                        predicateValueType.setAttribute("name", targetClass.getChildText("name").substring(0,sourceLength).toLowerCase());
                                                }
                                                predicateValueType.setAttribute("type", targetClass.getChildText("name"));
                                                theAssoc.addContent(predicateValueType);
                                            }

                                            xpddlDomain.getChild(xpddlTag).addContent(theAssoc);
                                        }
					
				}
				if(source.getChild("rolename").getChildText("value").trim().equals("") && 
						target.getChild("rolename").getChildText("value").trim().equals("")){
					// 4.1.2 Associations without rolenames
					Element predicate = new Element("predicate");
					predicate.setAttribute("name", classAssociation.getChildText("name"));
					
					boolean sourceHasNavigation = Boolean.parseBoolean(source.getAttributeValue("navigation"));
					boolean targetHasNavigation = Boolean.parseBoolean(target.getAttributeValue("navigation"));					
					// 4.1.2.1 Double navigation or without navigation: the order of the parameters is not important
					if((sourceHasNavigation && targetHasNavigation) ||
							(!sourceHasNavigation && !targetHasNavigation)){
						
						Element predicateType = new Element("parameter");
						predicateType.setAttribute("name", targetClass.getChildText("name").substring(0,targetLength).toLowerCase());
						predicateType.setAttribute("type", targetClass.getChildText("name"));
						predicate.addContent(predicateType);
						
						Element predicateValueType = new Element("parameter");
						if(sourceClass.getChildText("name").substring(0,targetLength).toLowerCase().equals(targetClass.getChildText("name").substring(0,targetLength).toLowerCase())){
							predicateValueType.setAttribute("name", sourceClass.getChildText("name").substring(0,sourceLength).toLowerCase() + "1");
						}
						else{
							predicateValueType.setAttribute("name", sourceClass.getChildText("name").substring(0,sourceLength).toLowerCase());
						}
						predicateValueType.setAttribute("type", sourceClass.getChildText("name"));
						predicate.addContent(predicateValueType);
					}
					else{
						// 4.1.2.2 Single navigation: the order is set by the navigation
						Element noNavigation = (sourceHasNavigation) ?sourceClass :targetClass;
						Element withNavigation = (sourceHasNavigation) ?targetClass :sourceClass;
						int associationSourceLength = (withNavigation.getChildText("name").length() > 3) ?3 :noNavigation.getChildText("name").length();
						int associationTargetLength = (withNavigation.getChildText("name").length() > 3) ?3 :withNavigation.getChildText("name").length();
						
						Element predicateType = new Element("parameter");
						predicateType.setAttribute("name", withNavigation.getChildText("name").substring(0,associationTargetLength).toLowerCase());
						predicateType.setAttribute("type", withNavigation.getChildText("name"));
						predicate.addContent(predicateType);
						
						Element predicateValueType = new Element("parameter");
						if(noNavigation.getChildText("name").substring(0,associationSourceLength).toLowerCase().equals(withNavigation.getChildText("name").substring(0,associationTargetLength).toLowerCase())){
							predicateValueType.setAttribute("name", noNavigation.getChildText("name").substring(0,associationSourceLength).toLowerCase() + "1");
						}
						else{
							predicateValueType.setAttribute("name", noNavigation.getChildText("name").substring(0,associationSourceLength).toLowerCase());
						}
						predicateValueType.setAttribute("type", noNavigation.getChildText("name"));
						predicate.addContent(predicateValueType);
					}
					xpddlDomain.getChild("predicates").addContent(predicate);
				}
			}			
			
		}
		
		/*
                //4.2 Boolean and non-primitive attributes
		List<?> attributes = null;
		try {
			XPath path = new JDOMXPath("project/elements/classes/class/attributes/attribute[type!='2' and type!='3' and type!='4']");
			attributes = path.selectNodes(project.getDocument());
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		for(Iterator<?> iter = attributes.iterator(); iter.hasNext();){
			Element attribute = (Element)iter.next();
			Element attributeClass = attribute.getParentElement().getParentElement();
			Element predicate = new Element("predicate");
			predicate.setAttribute("name", attribute.getChildText("name"));
			
			// 4.2.1 arguments 1 to n-1 (those are done before because the names are given by the user)
			if(attribute.getChild("parameters").getChildren().size() > 0){
				for (Iterator<?> iterator = attribute.getChild("parameters").getChildren("parameter").iterator(); iterator
						.hasNext();) {
					Element attributeParameter = (Element) iterator.next();
					Element attributeParameterType = null;
					try {
						XPath path = new JDOMXPath("project/elements/classes/*[@id='" + attributeParameter.getChildText("type") + "']");
						attributeParameterType = (Element)path.selectSingleNode(project.getDocument());
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(attributeParameterType != null){
						Element predicateParameter = new Element("parameter");				
						predicateParameter.setAttribute("name", attributeParameter.getChildText("name"));						
						predicateParameter.setAttribute("type", attributeParameterType.getChildText("name"));
						predicate.addContent(predicateParameter);
					}
				}
				
			}
			
			// 4.2.2 argument 0
			if(!attributeClass.getChildText("stereotype").equals("utility")){
				Element predicateType = new Element("parameter");
				int length = (attributeClass.getChildText("name").length() > 3) ?3 :attributeClass.getChildText("name").length();
				String noIndexPredicateTypeName = attributeClass.getChildText("name").substring(0,length).toLowerCase();
				
				List<?> result = null;
				try {
					// checks if there is already a parameter with that name
					XPath path = new JDOMXPath("parameter[starts-with(@name,'" + noIndexPredicateTypeName + "')]");
					result = path.selectNodes(predicate);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(result.size() > 0){						
					boolean hasSameName = true;
					String predicateTypeName = "";
					for(int i = result.size(); hasSameName; i++){
						predicateTypeName = noIndexPredicateTypeName + i;
						Element sameNameParameter = null;
						try {						
							XPath path = new JDOMXPath("parameter[@name='" + predicateTypeName + "']");
							sameNameParameter = (Element)path.selectSingleNode(predicate);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						if(sameNameParameter == null){
							hasSameName = false;
						}
					}
					predicateType.setAttribute("name", predicateTypeName);					
				}
				else{
					predicateType.setAttribute("name", noIndexPredicateTypeName);
				}
				predicateType.setAttribute("type", attributeClass.getChildText("name"));
				predicate.addContent(0, predicateType);
			}
			
			//4.2.3 argument n
			if(!attribute.getChildText("type").equals("1")){
				Element predicateValueType = new Element("parameter");
				Element attributeType = null;
				try {
					XPath path = new JDOMXPath("project/elements/classes/*[@id='" + attribute.getChildText("type") + "']");
					attributeType = (Element)path.selectSingleNode(project.getDocument());
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(attributeType != null){
					//this is done because the attribute type name may be lower than 3
					int length = (attributeType.getChildText("name").length() > 3) ?3 :attributeType.getChildText("name").length();
					String noIndexPredicateValueTypeName = attributeType.getChildText("name").substring(0,length).toLowerCase();
					List<?> result = null;
					try {
						// checks if there is already a parameter with that name
						XPath path = new JDOMXPath("parameter[starts-with(@name,'" + noIndexPredicateValueTypeName + "')]");
						result = path.selectNodes(predicate);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(result.size() > 0){
						boolean hasSameName = true;
						String predicateValueTypeName = "";
						for(int i = result.size(); hasSameName; i++){
							predicateValueTypeName = noIndexPredicateValueTypeName + i;
							Element sameNameParameter = null;
							try {						
								XPath path = new JDOMXPath("parameter[@name='" + predicateValueTypeName + "']");
								sameNameParameter = (Element)path.selectSingleNode(predicate);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							if(sameNameParameter == null){
								hasSameName = false;
							}
						}
						predicateValueType.setAttribute("name", predicateValueTypeName);
					}
					else{
						predicateValueType.setAttribute("name", noIndexPredicateValueTypeName);
					}
					predicateValueType.setAttribute("type", attributeType.getChildText("name"));
					predicate.addContent(predicateValueType);
				}
			}	
			xpddlDomain.getChild("predicates").addContent(predicate);			
		}*/
                
                //4.2 Boolean 
		List<?> attributes = null;
		try {
			XPath path = new JDOMXPath("project/elements/classes/class/attributes/attribute[type='1']");
			attributes = path.selectNodes(project.getDocument());
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		for(Iterator<?> iter = attributes.iterator(); iter.hasNext();){
			Element attribute = (Element)iter.next();
			Element attributeClass = attribute.getParentElement().getParentElement();
			Element predicate = new Element("predicate");
			predicate.setAttribute("name", attribute.getChildText("name"));
			
			// 4.2.1 arguments 1 to n-1 (those are done before because the names are given by the user)
			if(attribute.getChild("parameters").getChildren().size() > 0){
				for (Iterator<?> iterator = attribute.getChild("parameters").getChildren("parameter").iterator(); iterator
						.hasNext();) {
					Element attributeParameter = (Element) iterator.next();
					Element attributeParameterType = null;
					try {
						XPath path = new JDOMXPath("project/elements/classes/*[@id='" + attributeParameter.getChildText("type") + "']");
						attributeParameterType = (Element)path.selectSingleNode(project.getDocument());
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(attributeParameterType != null){
						Element predicateParameter = new Element("parameter");				
						predicateParameter.setAttribute("name", attributeParameter.getChildText("name"));						
						predicateParameter.setAttribute("type", attributeParameterType.getChildText("name"));
						predicate.addContent(predicateParameter);
					}
				}
				
			}
			
			// 4.2.2 argument 0
			if(!attributeClass.getChildText("stereotype").equals("utility")){
				Element predicateType = new Element("parameter");
				int length = (attributeClass.getChildText("name").length() > 3) ?3 :attributeClass.getChildText("name").length();
				String noIndexPredicateTypeName = attributeClass.getChildText("name").substring(0,length).toLowerCase();
				
				List<?> result = null;
				try {
					// checks if there is already a parameter with that name
					XPath path = new JDOMXPath("parameter[starts-with(@name,'" + noIndexPredicateTypeName + "')]");
					result = path.selectNodes(predicate);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(result.size() > 0){						
					boolean hasSameName = true;
					String predicateTypeName = "";
					for(int i = result.size(); hasSameName; i++){
						predicateTypeName = noIndexPredicateTypeName + i;
						Element sameNameParameter = null;
						try {						
							XPath path = new JDOMXPath("parameter[@name='" + predicateTypeName + "']");
							sameNameParameter = (Element)path.selectSingleNode(predicate);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						if(sameNameParameter == null){
							hasSameName = false;
						}
					}
					predicateType.setAttribute("name", predicateTypeName);					
				}
				else{
					predicateType.setAttribute("name", noIndexPredicateTypeName);
				}
				predicateType.setAttribute("type", attributeClass.getChildText("name"));
				predicate.addContent(0, predicateType);
			}
                        //add new predicate to predicates node
			xpddlDomain.getChild("predicates").addContent(predicate);			
		}


                //4.2 non-primitive attributes (they are treat differently depending on the chosen pddl version)
		attributes = null;
		try {
                        XPath path = new JDOMXPath("project/elements/classes/class/attributes/attribute[type!='1' and type!='2' and type!='3' and type!='4']");
			attributes = path.selectNodes(project.getDocument());
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		for(Iterator<?> iter = attributes.iterator(); iter.hasNext();){
			Element attribute = (Element)iter.next();
			Element attributeClass = attribute.getParentElement().getParentElement();
                        //Element predicate = new Element("predicate");
                        //predicate.setAttribute("name", attribute.getChildText("name"));

                        String xpddlTag = ""; //this variable holds where should we put the resulting xppdl attribute
                        Element theXPDDLAttr = null;
                        if (!pddlVersion.equals(PDDL_3_1)){
                            theXPDDLAttr = new Element("predicate");
                            theXPDDLAttr.setAttribute("name", attribute.getChildText("name"));
                            xpddlTag = "predicates";
                        }else{
                            theXPDDLAttr = new Element("function");
                            theXPDDLAttr.setAttribute("name", attribute.getChildText("name"));
                            xpddlTag = "functions";
                        }

                        
			// 4.2.1 arguments 1 to n-1 (those are done before because the names are given by the user)
			if(attribute.getChild("parameters").getChildren().size() > 0){
				for (Iterator<?> iterator = attribute.getChild("parameters").getChildren("parameter").iterator(); iterator
						.hasNext();) {
					Element attributeParameter = (Element) iterator.next();
					Element attributeParameterType = null;
					try {
						XPath path = new JDOMXPath("project/elements/classes/*[@id='" + attributeParameter.getChildText("type") + "']");
						attributeParameterType = (Element)path.selectSingleNode(project.getDocument());
					} catch (JaxenException e) {
						e.printStackTrace();
					}
					if(attributeParameterType != null){
						Element currentParameter = new Element("parameter");
						currentParameter.setAttribute("name", attributeParameter.getChildText("name"));
						currentParameter.setAttribute("type", attributeParameterType.getChildText("name"));
						//predicate.addContent(currentParameter);
                                                theXPDDLAttr.addContent(currentParameter);
					}
				}

			}

			// 4.2.2 argument 0
			if(!attributeClass.getChildText("stereotype").equals("utility")){
				Element theType = new Element("parameter");
				int length = (attributeClass.getChildText("name").length() > 3) ?3 :attributeClass.getChildText("name").length();
				String noIndexPredicateTypeName = attributeClass.getChildText("name").substring(0,length).toLowerCase();

				List<?> result = null;
				try {
					// checks if there is already a parameter with that name
					XPath path = new JDOMXPath("parameter[starts-with(@name,'" + noIndexPredicateTypeName + "')]");
					//result = path.selectNodes(predicate);
                                        result = path.selectNodes(theXPDDLAttr);
				} catch (JaxenException e) {
					e.printStackTrace();
				}
				if(result.size() > 0){
					boolean hasSameName = true;
					String predicateTypeName = "";
					for(int i = result.size(); hasSameName; i++){
						predicateTypeName = noIndexPredicateTypeName + i;
						Element sameNameParameter = null;
						try {
							XPath path = new JDOMXPath("parameter[@name='" + predicateTypeName + "']");
							//sameNameParameter = (Element)path.selectSingleNode(predicate);
                                                        sameNameParameter = (Element)path.selectSingleNode(theXPDDLAttr);
						} catch (JaxenException e) {
							e.printStackTrace();
						}
						if(sameNameParameter == null){
							hasSameName = false;
						}
					}
					theType.setAttribute("name", predicateTypeName);
				}
				else{
					theType.setAttribute("name", noIndexPredicateTypeName);
				}
				theType.setAttribute("type", attributeClass.getChildText("name"));
				//predicate.addContent(0, theType);
                                theXPDDLAttr.addContent(0, theType);
			}

			//4.2.3 argument n


			
                        Element attributeType = null;
                        try {
                                XPath path = new JDOMXPath("project/elements/classes/*[@id='" + attribute.getChildText("type") + "']");
                                attributeType = (Element)path.selectSingleNode(project.getDocument());
                        } catch (JaxenException e) {
                                e.printStackTrace();
                        }
                        if(attributeType != null){

                                if (!pddlVersion.equals(PDDL_3_1)){
                                    Element predicateValueType = new Element("parameter");
                                    //this is done because the attribute type name may be lower than 3
                                    int length = (attributeType.getChildText("name").length() > 3) ?3 :attributeType.getChildText("name").length();
                                    String noIndexPredicateValueTypeName = attributeType.getChildText("name").substring(0,length).toLowerCase();
                                    List<?> result = null;
                                    try {
                                            // checks if there is already a parameter with that name
                                            XPath path = new JDOMXPath("parameter[starts-with(@name,'" + noIndexPredicateValueTypeName + "')]");
                                            //result = path.selectNodes(predicate);
                                            result = path.selectNodes(theXPDDLAttr);
                                    } catch (JaxenException e) {
                                            e.printStackTrace();
                                    }
                                    if(result.size() > 0){
                                            boolean hasSameName = true;
                                            String predicateValueTypeName = "";
                                            for(int i = result.size(); hasSameName; i++){
                                                    predicateValueTypeName = noIndexPredicateValueTypeName + i;
                                                    Element sameNameParameter = null;
                                                    try {
                                                            XPath path = new JDOMXPath("parameter[@name='" + predicateValueTypeName + "']");
                                                            //sameNameParameter = (Element)path.selectSingleNode(predicate);
                                                            sameNameParameter = (Element)path.selectSingleNode(theXPDDLAttr);
                                                    } catch (JaxenException e) {
                                                            e.printStackTrace();
                                                    }
                                                    if(sameNameParameter == null){
                                                            hasSameName = false;
                                                    }
                                            }
                                            predicateValueType.setAttribute("name", predicateValueTypeName);
                                    }
                                    else{
                                            predicateValueType.setAttribute("name", noIndexPredicateValueTypeName);
                                    }
                                    predicateValueType.setAttribute("type", attributeType.getChildText("name"));
                                    //predicate.addContent(predicateValueType);
                                    theXPDDLAttr.addContent(predicateValueType);
                                }
                                //If it is PDDL3.1
                                else{
                                    theXPDDLAttr.setAttribute("type", attributeType.getChildText("name"));
                                }
                        }

                        //Insert the resulting node at the right place
			//xpddlDomain.getChild("predicates").addContent(predicate);
                        xpddlDomain.getChild(xpddlTag).addContent(theXPDDLAttr);
		}

		//5. Functions
		List<?> numericAttributes = null;
		try {
			XPath path = new JDOMXPath("project/elements/classes/class/attributes/attribute[type='2' or type='3']");
			numericAttributes = path.selectNodes(project.getDocument());
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		if(numericAttributes.size() > 0){
			//xpddlDomain.getChild("requirements").addContent(new Element("fluents"));
			
			for (Iterator<?> iter = numericAttributes.iterator(); iter.hasNext();) {
				Element attribute = (Element) iter.next();
				Element attributeClass = attribute.getParentElement().getParentElement();
				Element function = new Element("function");
				function.setAttribute("name", attribute.getChildText("name").trim());
				
				// 5.2 argument 1 to n-1 (those are done before because the names are given by the user)
				if(attribute.getChild("parameters").getChildren().size() > 0){
					for (Iterator<?> iterator = attribute.getChild("parameters").getChildren("parameter").iterator(); iterator
					.hasNext();) {
						Element attributeParameter = (Element) iterator.next();
						Element attributeParameterType = null;
						try {//lock for the class or the enumeration
							XPath path = new JDOMXPath("project/elements/classes/*[@id='" + attributeParameter.getChildText("type") + "']");
							attributeParameterType = (Element)path.selectSingleNode(project.getDocument());
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						if(attributeParameterType != null){
							Element functionParameter = new Element("parameter");
							functionParameter.setAttribute("name", attributeParameter.getChildText("name").trim());							
							functionParameter.setAttribute("type", attributeParameterType.getChildText("name").trim());
							function.addContent(functionParameter);
						}
					}
				}
				
				// 5.1 argument 0
				if(!attributeClass.getChildText("stereotype").equals("utility")){
					Element functionType = new Element("parameter");
					int length = (attributeClass.getChildText("name").length() > 3) ?3 :attributeClass.getChildText("name").length();
					//functionType.setAttribute("name", attributeClass.getChildText("name").substring(0,length).toLowerCase());
					String noIndexFunctionTypeName = attributeClass.getChildText("name").substring(0,length).toLowerCase();
					
					List<?> result = null;
					try {
						// checks if there is already a parameter with that name
						XPath path = new JDOMXPath("parameter[starts-with(@name,'" + noIndexFunctionTypeName + "')]");
						result = path.selectNodes(function);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(result.size() > 0){						
						boolean hasSameName = true;
						String functionTypeName = "";
						for(int i = result.size(); hasSameName; i++){
							functionTypeName = noIndexFunctionTypeName + i;
							Element sameNameParameter = null;
							try {						
								XPath path = new JDOMXPath("parameter[@name='" + functionTypeName + "']");
								sameNameParameter = (Element)path.selectSingleNode(function);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							if(sameNameParameter == null){
								hasSameName = false;
							}
						}
						functionType.setAttribute("name", functionTypeName.trim());					
					}
					else{
						functionType.setAttribute("name", noIndexFunctionTypeName.trim());
					}
					functionType.setAttribute("type", attributeClass.getChildText("name").trim());
					function.addContent(0, functionType);
				}
				
                                //Add the numebr type for PDDL 3.1
                                if (pddlVersion.equals(PDDL_3_1)){
                                    function.setAttribute("type", "number");
                                }

				xpddlDomain.getChild("functions").addContent(function);
			}			
		}
		
		
		//6. Constraints
                Element constraints = xpddlDomain.getChild("constraints");
                Element constraint = new Element("and");
		if(pddlVersion.equals(PDDL_3_0)){
                        // 6.1 Find association with multiplicity "1"
			List<?> result = null;
			try {
				XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation/associationEnds/associationEnd[multiplicity/value='1' or multiplicity/value='0..1']");
				result = path.selectNodes(project.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			// this is the node added to the constraints node
			//Element constraint = null;
			//if(result.size() > 1){
			//	constraint = new Element("and");
			//}
			//6.2. For each one create constraint
			for (int i = 0; i < result.size(); i++){
				Element associationEndRef = (Element)result.get(i);
				
				Element associationEndSec = null;
				
				Element classAssociation = associationEndRef.getParentElement().getParentElement();
				
				Iterator<?> associationEnds = classAssociation.getChild("associationEnds").getChildren("associationEnd").iterator();
				while (associationEnds.hasNext()){
					Element associationEnd = (Element)associationEnds.next();
					if (!associationEnd.equals(associationEndRef)){
						associationEndSec = associationEnd;
					}
				}
				
				if(!associationEndRef.getChild("rolename").getChildText("value").trim().equals("")){
//					2.1 Finding the classes
					Element classRef = null;
					Element classSec = null;
					
					//6.2.1.1 Get reference class
					try {
						XPath path = new JDOMXPath("project/elements/classes/class[@id="+associationEndRef.getAttributeValue("element-id")+"]");
						classRef = (Element)path.selectSingleNode(project.getDocument());
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					//6.2.1.2 Get secondary class
					try {
						XPath path = new JDOMXPath("project/elements/classes/class[@id="+associationEndSec.getAttributeValue("element-id")+"]");
						classSec = (Element)path.selectSingleNode(project.getDocument());
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
								
					Element forall = new Element("forall");
					Element x1 = new Element("parameter");
					int xLength = (classRef.getChildText("name").length() > 3) ?3 :classRef.getChildText("name").length();
					x1.setAttribute("name", classRef.getChildText("name").toLowerCase().substring(0,xLength) + "1");
					x1.setAttribute("type", classRef.getChildText("name"));
					Element x2 = new Element("parameter");
					x2.setAttribute("name", classRef.getChildText("name").toLowerCase().substring(0,xLength) + "2");
					x2.setAttribute("type", classRef.getChildText("name"));			
					Element y1 = new Element("parameter");
					int yLength = (classSec.getChildText("name").length() > 3) ?3 :classSec.getChildText("name").length();
					y1.setAttribute("name", classSec.getChildText("name").toLowerCase().substring(0, yLength));
					y1.setAttribute("type", classSec.getChildText("name"));
					
					Element always = new Element("always");
					Element imply = new Element("imply");
					Element implies = new Element("implies");
					Element and = new Element("and");
					Element equals = new Element("equals");
					Element firstPredicate = new Element("predicate");
					firstPredicate.setAttribute("id", associationEndRef.getChild("rolename").getChildText("value"));
					Element secondPredicate = new Element("predicate");
					secondPredicate.setAttribute("id", associationEndRef.getChild("rolename").getChildText("value"));
					
					Element x1Ref = new Element("parameter");
					x1Ref.setAttribute("id", x1.getAttributeValue("name"));
					Element x2Ref = new Element("parameter");
					x2Ref.setAttribute("id", x2.getAttributeValue("name"));
					Element y1Ref = new Element("parameter");
					y1Ref.setAttribute("id", y1.getAttributeValue("name"));			

					if (associationEndRef.getAttributeValue("navigation").equals("false")){
						// first predicate
						firstPredicate.addContent((Element)x1Ref.clone());
						firstPredicate.addContent((Element)y1Ref.clone());
						
						//second predicate
						secondPredicate.addContent((Element)x2Ref.clone());
						secondPredicate.addContent((Element)y1Ref.clone());
					}
					else{
						// first predicate
						firstPredicate.addContent((Element)y1Ref.clone());
						firstPredicate.addContent((Element)x1Ref.clone());
						
						//second predicate
						secondPredicate.addContent((Element)y1Ref.clone());
						secondPredicate.addContent((Element)x2Ref.clone());
					}			
					equals.addContent((Element)x1Ref.clone());
					equals.addContent((Element)x2Ref.clone());
					
					and.addContent(firstPredicate);
					and.addContent(secondPredicate);
					
					implies.addContent(equals);
					imply.addContent(and);
					imply.addContent(implies);
					
					always.addContent(imply);			
					
					forall.addContent(x1);
					forall.addContent(x2);
					forall.addContent(y1);
					forall.addContent(always);		

					if(constraint == null){
						constraint = forall;
					}
					else{
						constraint.addContent(forall);
					}
				}	
			}
                }
                //6.3 OCL Contraints on classes (inv:)
                if (pddlVersion.equals(PDDL_3_0) || pddlVersion.equals(PDDL_3_1)){

                        List<?> classesWithConstraints = null;
                        try {
                            XPath path = new JDOMXPath("project/elements/classes/class[constraints!='']");
                            classesWithConstraints = path.selectNodes(project.getDocument());
                        } catch (JaxenException e2) {
                            e2.printStackTrace();
                        }

                        for (Iterator<?> it = classesWithConstraints.iterator(); it.hasNext();) {
                            Element theclass = (Element)it.next();
                            //String classConstraintStr = theclass.getChildText("constraints");
                            //System.out.println(classConstraintStr);
                            Element xpddlconst = buildClassConstraint(theclass);
                            constraint.addContent(xpddlconst);
                        }
                            
                }

                //6.4 Add constraints that where found
                if(constraint.getChildren().size() > 0){
                    constraints.addContent(constraint);
                }
		
		
		//7. Actions 
		List<?> operators = null;
		try {
			XPath path = new JDOMXPath("project/elements/classes/class/operators/operator");
			operators = path.selectNodes(project.getDocument());
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		for (Iterator<?> iter = operators.iterator(); iter.hasNext();) {
			Element operator = (Element) iter.next();
                                                
                        //7.0 Check whether operator is inhibited
                        /*boolean inhibited = false;
                        if(instructions != null){
                            for(Iterator iterate = instructions.iterator(); iterate.hasNext();){
                                Element inhibitedOperator = (Element)iterate.next();
                                if(inhibitedOperator.getChildText("name").equals(operator.getChildText("name"))){
                                    inhibited = true;
                                }
                            }
                        }*/
	                        
                        
                        //if(!inhibited){
                            Element action = (Element)xpddlNodes.getChild("action").clone();
                            action.setAttribute("name", operator.getChildText("name"));			

                            //7.1 Parameters
                            for (Iterator<?> iterator = operator.getChild("parameters").getChildren("parameter").iterator(); iterator.hasNext();) {
                                    Element parameter = (Element) iterator.next();
                                    Element parameterClass = null;
                                    try {
                                            XPath path = new JDOMXPath("project/elements/classes/*[@id='" + parameter.getChildText("type") + "']");
                                            parameterClass = (Element)path.selectSingleNode(project.getDocument());
                                    } catch (JaxenException e) {			
                                            e.printStackTrace();
                                    }

                                    if(parameterClass != null){
                                            Element actionParameter = new Element("parameter");
                                            actionParameter.setAttribute("name", parameter.getChildText("name"));
                                            actionParameter.setAttribute("type", parameterClass.getChildText("name"));

                                            action.getChild("parameters").addContent(actionParameter);
                                    }
                            }

                            //7.2 duration
                            boolean isDurative = Boolean.parseBoolean(operator.getChild("timeConstraints").getAttributeValue("timed"));

                            //check if there is any timing diagram for this operator
                            Element timingDiagram = null;
                            try {
                                    XPath path = new JDOMXPath("project/diagrams/timingDiagrams/timingDiagram[action/@class='" +
                                            operator.getParentElement().getParentElement().getAttributeValue("id") +
                                            "' and action/@id='"+operator.getAttributeValue("id")+"']");
                                    timingDiagram = (Element)path.selectSingleNode(project.getDocument());
                            } catch (JaxenException e) {
                                    e.printStackTrace();
                            }

                            if(isDurative){
                                    Element duration = new Element("duration");
                                    Element value = new Element("value");
                                    String durationStr = operator.getChild("timeConstraints").getChildText("duration");
                                    if (durationStr.trim().equals("")){
                                        //see if it can be found in the timing diagram
                                        if (timingDiagram!=null){
                                            durationStr = timingDiagram.getChild("frame").getChildText("duration").trim();
                                        }
                                    }

                                    if(durationStr.trim().equals("")){
                                            value.setAttribute("number", "0");
                                            System.out.println("WARNING: duration of durative action \""+ operator.getChildText("name")+ "\" not specified.\n");
                                    }
                                    else{
                                            try {
                                                    Float.parseFloat(durationStr);
                                                    value.setAttribute("number", durationStr);
                                                    duration.addContent(value);
                                            }
                                            catch(Exception e){
                                                    System.out.println("This is not a number!");
                                                    ExpressionTreeBuilder builder = new ExpressionTreeBuilder(durationStr);
                                                    Element durationExpressionTree = builder.getExpressionTree();
                                                    Element durationExpresion = buildCondition(durationExpressionTree, action, null, PRECONDITION);
                                                    duration.addContent(durationExpresion);
                                            }
                                    }
                                    action.addContent(duration);

                                    action.setName("durative-action");
                            }

                            //7.2 Pre and post conditions

                            // check whether the operator has constraints
                            // if that is the case, translate only the constraints
                            // otherwise, look for the action definition in state machine
                            String constraintsExpression = operator.getChildText("constraints").trim();
                            String precondition;
                            String postcondition;

                            if(!constraintsExpression.equals("")){
                                    // precondition
                                    precondition = constraintsExpression.substring(
                                                    constraintsExpression.indexOf("pre:"), 
                                                    constraintsExpression.indexOf("post:")).trim();

                                    // take off "pre:"
                                    precondition = precondition.substring(4, precondition.length()).trim();

                                    // postcondition		
                                    postcondition = constraintsExpression.substring(
                                                    constraintsExpression.indexOf("post:"), 
                                                    constraintsExpression.length()).trim();

                                    // take off "post:"
                                    postcondition = postcondition.substring(5, postcondition.length()).trim();				

                            }
                            else{
                                    Element actionNode = OCLUtilities.buildConditions(operator);

                                    //7.2.1 Preconditions
                                    List<?> preconditions = null;
                                    try {
                                            XPath path = new JDOMXPath("preconditions/conditions/condition");
                                            preconditions = path.selectNodes(actionNode);
                                    } catch (JaxenException e) {			
                                            e.printStackTrace();
                                    }
                                    precondition = "";
                                    if(preconditions.size() > 0){
                                            for (Iterator<?> iterator = preconditions.iterator(); iterator.hasNext();) {
                                                    Element condition = (Element) iterator.next();
                                                    precondition += condition.getText();
                                                    if(iterator.hasNext())
                                                            precondition += " and ";
                                            }
                                    }

                                    //7.3.2 Postconditions
                                    List<?> postconditions = null;
                                    try {
                                            XPath path = new JDOMXPath("postconditions/conditions/condition");
                                            postconditions = path.selectNodes(actionNode);
                                    } catch (JaxenException e) {			
                                            e.printStackTrace();
                                    }
                                    postcondition = "";
                                    if(postconditions.size() > 0){
                                            for (Iterator<?> iterator = postconditions.iterator(); iterator.hasNext();) {
                                                    Element condition = (Element) iterator.next();
                                                    postcondition += condition.getText();
                                                    if(iterator.hasNext())
                                                            postcondition += " and ";
                                            }
                                    }

                            }


                            if (precondition.indexOf("&#xd;") > -1){				
                                    precondition.replaceAll("&#xd;", " "); // return carriage in xml
                            }
                            if(!precondition.trim().equals("")){
                                    try{					
                                            ExpressionTreeBuilder builder = new ExpressionTreeBuilder(precondition);
                                            Element preconditionExpressionTree = builder.getExpressionTree();
                                            Element xpddlPrecondition = buildCondition(preconditionExpressionTree, action, null, PRECONDITION);
                                            action.getChild("precondition").addContent(xpddlPrecondition);
                                            //XMLUtilities.printXML(preconditionExpressionTree);
                                    }
                                    catch(Exception e){
                                            e.printStackTrace();                                            
                                            System.err.println("Error on action: \"" + action.getAttributeValue("name") + "\", with the precondition: \"" + precondition + "\"." +
                                                            "\nInvalid or unsupported syntax.\n");
                                    }
                            }

                            if (postcondition.indexOf("&#xd;") > -1){				
                                    postcondition.replaceAll("&#xd;", " "); // return carriage in xml
                            }

                            if(!postcondition.trim().equals("")){
                                    try{
                                            ExpressionTreeBuilder builder = new ExpressionTreeBuilder(postcondition);
                                            Element postconditionExpressionTree = builder.getExpressionTree();
                                            Element effect = buildCondition(postconditionExpressionTree, action, null, POSTCONDITION);
                                            action.getChild("effect").addContent(effect);
                                            //XMLUtilities.printXML(postconditionExpressionTree);
                                    }
                                    catch(Exception e){
                                            e.printStackTrace();
                                            System.err.println("Error on action: \"" + action.getAttributeValue("name") + "\", with the postcondition: \"" + postcondition + "\"." +
                                                            "\nInvalid or unsupported syntax.\n");
                                    }
                            }
                            if(precondition.trim().equals("") && postcondition.trim().equals("")){
                                    System.out.println("WARNING: precondition and postcondition of action \"" + action.getAttributeValue("name") + 
                                                    "\" not defined.\n");
                            }

                            // 7.3.3 Analyze preconditions and postconditions for additional statements in postcondition
                            // get all predicates in effect with more than one parameter
                            List<?> effectPredicates = null;
                            try {
                                    XPath path = new JDOMXPath("effect/descendant::predicate[count(*) > 1]");
                                    effectPredicates = path.selectNodes(action);
                            } catch (JaxenException e) {			
                                    e.printStackTrace();
                            }

                            for (Iterator<?> iterator = effectPredicates.iterator(); iterator.hasNext();) {
                                    Element predicate = (Element) iterator.next();

                                    // if the predicate is denied or is a condition to a when node, do nothing
                                    if(!predicate.getParentElement().getName().equals("not")){

                                            // build the query to find the same predicate with xpath
                                            // only the last parameter must be diferent
                                            String query = "descendant::predicate[@id='"+ predicate.getAttributeValue("id") +"'";
                                            List<?> parameters = predicate.getChildren();
                                            int index = 1;
                                            for (Iterator<?> iterator2 = parameters.iterator(); iterator2.hasNext();) {
                                                    // don't use the last parameter
                                                    Element parameter = (Element) iterator2.next();
                                                    if(index != parameters.size()){								
                                                            query += " and *["+ index +"]/@id='"+ parameter.getAttributeValue("id") +"'";
                                                            index++;
                                                    }
                                            }
                                            query += "]";

                                            // check whether the predicate is inside a when node
                                            Element parent = predicate.getParentElement();
                                            while(!parent.getName().equals("when") && !parent.getName().equals("effect")){
                                                    parent = parent.getParentElement();
                                            }
                                            if(parent.getName().equals("effect")){
                                                    // the predicate isn't inside a when node

                                                    // find the same predicate in precondition, with only the last parameter different
                                                    // indicating that this value must be denied

                                                    // look for the predicates in precondition
                                                    List<?> precondPredicates = null;
                                                    try {
                                                            XPath path = new JDOMXPath(query);
                                                            precondPredicates = path.selectNodes(action.getChild("precondition"));
                                                    } catch (JaxenException e) {			
                                                            e.printStackTrace();
                                                    }

                                                    for (Iterator<?> iterator3 = precondPredicates.iterator(); iterator3.hasNext();) {
                                                            Element precondPredicate = (Element) iterator3.next();

                                                            // checks if the last parameter is the same; if so, do nothing
                                                            Element lastParameter = (Element)predicate.getChildren().get(parameters.size()-1);
                                                            Element lastPrecondParameter = (Element)precondPredicate.getChildren().get(parameters.size()-1);
                                                            if(!lastParameter.getAttributeValue("id").equals(lastPrecondParameter.getAttributeValue("id"))){
                                                                    // get the conditions for a when node in post condition
                                                                    List<?> conditions = buildWhenCondition(precondPredicate);

                                                                    if(conditions.size() == 0){
                                                                            // there is no need to create a when node
                                                                            // in this case, deny the precondition predicate directly in the effect node
                                                                            Element not = new Element("not");
                                                                            not.addContent((Element)precondPredicate.clone());
                                                                            Element predicateParent = predicate.getParentElement();
                                                                            if(predicateParent.getName().equals("effect")){
                                                                                    // there is only one statement, so create an and node
                                                                                    Element andNode = new Element("and");
                                                                                    andNode.addContent(predicate.detach());
                                                                                    andNode.addContent(not);
                                                                                    predicateParent.addContent(andNode);
                                                                            }
                                                                            else if(predicateParent.getName().equals("and")){
                                                                                    // just add the not in the and node
                                                                                    predicateParent.addContent(not);
                                                                            }

                                                                    }
                                                                    else{
                                                                            // TODO look for a when with the same conditions
                                                                            // only creates a when node if there is not already a when with the same conditions

                                                                            Element when = new Element("when");
                                                                            Element container;									

                                                                            if(conditions.size() == 1){
                                                                                    container = when;
                                                                            }
                                                                            else{// size > 1
                                                                                    container = new Element("and");
                                                                                    when.addContent(container);
                                                                            }									

                                                                            for (Iterator<?> iterator4 = conditions.iterator(); iterator4.hasNext();) {
                                                                                    Element condition = (Element) iterator4.next();
                                                                                    container.addContent(condition);										
                                                                            }									

                                                                            Element not = new Element("not");
                                                                            not.addContent((Element)precondPredicate.clone());
                                                                            Element doNode = new Element("do");
                                                                            doNode.addContent(not);
                                                                            when.addContent(doNode);

                                                                            // add the when node in the effect
                                                                            Element predicateParent = predicate.getParentElement();
                                                                            if(predicateParent.getName().equals("effect")){
                                                                                    // there is only one statement, so create an and node
                                                                                    Element andNode = new Element("and");
                                                                                    andNode.addContent(predicate.detach());
                                                                                    andNode.addContent(when);
                                                                                    predicateParent.addContent(andNode);
                                                                            }
                                                                            else if(predicateParent.getName().equals("and")){
                                                                                    // just add the when in the and node
                                                                                    predicateParent.addContent(when);
                                                                            }
                                                                    }
                                                            }

                                                    }
                                            }
                                            else{
                                                    // predicate is inside a when node (supposing there is only one when node)

                                                    // look for the predicate in the when conditions
                                                    List<?> whenPredicates = null;
                                                    try {
                                                            XPath path = new JDOMXPath(query);
                                                            whenPredicates = path.selectNodes(parent);
                                                    } catch (JaxenException e) {			
                                                            e.printStackTrace();
                                                    }

                                                    // discard elements inside do node
                                                    List<Element> conditionPredicates = new ArrayList<Element>();
                                                    for (Iterator<?> iterator2 = whenPredicates.iterator(); iterator2
                                                                    .hasNext();) {
                                                            Element conditionPredicate = (Element) iterator2.next();
                                                            Element conditionParent = conditionPredicate.getParentElement();
                                                            while(!conditionParent.getName().equals("do") &&
                                                                            !conditionParent.getName().equals("when")){
                                                                    conditionParent = conditionParent.getParentElement();
                                                            }
                                                            if(conditionParent.getName().equals("when")){
                                                                    conditionPredicates.add(conditionPredicate);
                                                            }
                                                    }

                                                    if(conditionPredicates.size() == 0){
                                                            // the predicate is not in the when conditions
                                                            // so we must look for it in the precondition
                                                            List<?> preconditionPredicates = null;
                                                            try {
                                                                    XPath path = new JDOMXPath(query);
                                                                    preconditionPredicates = path.selectNodes(action.getChild("precondition"));
                                                            } catch (JaxenException e) {			
                                                                    e.printStackTrace();
                                                            }
                                                            for (Iterator<?> iterator2 = preconditionPredicates
                                                                            .iterator(); iterator2.hasNext();) {
                                                                    Element precondPredicate = (Element) iterator2.next();
                                                                    List<?> conditions = buildWhenCondition(precondPredicate);

                                                                    if(conditions.size() == 0){
                                                                            // there is no need to create a when node
                                                                            // in this case, deny the precondition predicate directly in "do" node
                                                                            // this will be the only case to be dealed, since in PDDL
                                                                            // a "when" can't be inside another "when"
                                                                            Element not = new Element("not");
                                                                            not.addContent((Element)precondPredicate.clone());
                                                                            Element predicateParent = predicate.getParentElement();
                                                                            Element and = predicateParent.getChild("and");
                                                                            if(and == null){
                                                                                    // there is only one statement, so create an and node
                                                                                    Element andNode = new Element("and");
                                                                                    andNode.addContent(predicate.detach());
                                                                                    andNode.addContent(not);
                                                                                    predicateParent.addContent(andNode);
                                                                            }
                                                                            else if(predicateParent.getName().equals("and")){
                                                                                    // just add the not in the and node
                                                                                    and.addContent(not);
                                                                            }

                                                                    }
                                                                    // if the size of list conditions is > 0, it means that there are
                                                                    // disjunctions in precondition
                                                                    // this can't be translated to PDDL, since it doesn't supports
                                                                    // a "when" inside another one
                                                                    // therefore, nothing is done in this case
                                                            }
                                                    }
                                                    else{
                                                            // the predicate is in the when conditions
                                                            // so, deny it in the "do" node
                                                            for (Iterator<Element> iterator2 = conditionPredicates.iterator(); iterator2.hasNext();) {
                                                                    Element conditionPredicate = iterator2.next();
                                                                    //Element predicateParent = conditionPredicate.getParentElement();

                                                                    // get when node
                                                                    /*while(!predicateParent.getName().equals("when") && 
                                                                                    !predicateParent.getName().equals("do")){
                                                                            predicateParent = predicateParent.getParentElement();
                                                                    }*/
                                                                    //if(predicateParent.getName().equals("when")){
                                                                            // checks if the last parameter is the same; if so, do nothing
                                                                            Element lastParameter = (Element)predicate.getChildren().get(parameters.size()-1);
                                                                            Element lastPrecondParameter = (Element)conditionPredicate.getChildren().get(parameters.size()-1);
                                                                            if(!lastParameter.getAttributeValue("id").equals(lastPrecondParameter.getAttributeValue("id"))){
                                                                                    // deny the predicate in the "do" node of "when"										
                                                                                    Element not = new Element("not");
                                                                                    not.addContent((Element)conditionPredicate.clone());

                                                                                    Element and = parent.getChild("do").getChild("and");
                                                                                    if(and == null){
                                                                                            // create a and node
                                                                                            and = new Element("and");
                                                                                            and.addContent(predicate.detach());
                                                                                            and.addContent(not);
                                                                                            parent.getChild("do").addContent(and);
                                                                                    }
                                                                                    else{
                                                                                            and.addContent(not);
                                                                                    }
                                                                            }
                                                                    //}
                                                            }
                                                    }
                                            }
                                    }
                            }



    //			 7.4 Deal with null values
                            // option 1: eliminate null values
                            // option 2: change null values for not(exists()) in preconditions and forall(not()) in postconditions
                            int option = 1;// initially, there is only option 1

                            List<?> actionPreconditions = null;
                            try {
                                    // filter not parameterized boolean attributes
                                    XPath path = new JDOMXPath("precondition/descendant::predicate[parameter/@id='null']");
                                    actionPreconditions = path.selectNodes(action);
                            } catch (JaxenException e) {			
                                    e.printStackTrace();
                            }	

                            List<?> actionPostconditions = null;
                            try {
                                    // filter not parameterized boolean attributes
                                    XPath path = new JDOMXPath("effect/descendant::predicate[parameter/@id='null']");
                                    actionPostconditions = path.selectNodes(action);
                            } catch (JaxenException e) {			
                                    e.printStackTrace();
                            }			

                            switch(option){
                            case 1:{
                                    //Delete all predicates in precondition with null values
                                    for (Iterator<?> iterator = actionPreconditions.iterator(); iterator.hasNext();) {
                                            Element predicate = (Element) iterator.next();
                                            Element parent = predicate.getParentElement();
                                            if(parent.getName().equals("not")){
                                                    predicate = parent;
                                                    parent = predicate.getParentElement();
                                            }
                                            parent.removeContent(predicate);
                                            parent.removeContent(predicate);
                                    }

                                    //Delete all predicates in postcondition with null values				
                                    for (Iterator<?> iterator = actionPostconditions.iterator(); iterator.hasNext();) {
                                            Element predicate = (Element) iterator.next();
                                            Element parent = predicate.getParentElement();
                                            if(parent.getName().equals("not")){
                                                    predicate = parent;
                                                    parent = predicate.getParentElement();
                                            }
                                            parent.removeContent(predicate);
                                    }					


                            }break;
                            case 2:{
                                    //Replace all predicates with null values with not(exists(?x - <type>)<predicate>))
                                    for (Iterator<?> iterator = actionPreconditions.iterator(); iterator.hasNext();) {
                                            Element refPredicate = (Element) iterator.next();
                                            Element predicate = null;				
                                            try {
                                                    XPath path = new JDOMXPath("predicates/predicate[@name='" + refPredicate.getAttributeValue("id") + "']");
                                                    predicate = (Element)path.selectSingleNode(xpddlDomain);
                                            } catch (JaxenException e) {			
                                                    e.printStackTrace();
                                            }
                                            if(predicate != null){
                                                    Element predicateParameter = (Element)predicate.getChildren("parameter").get(predicate.getChildren("parameter").size()-1);
                                                    // get the action node
                                                    /*Element action = refPredicate;					
                                                    while(!action.getName().equals("action")){						
                                                            action = action.getParentElement();
                                                    }*/
                                                    // create the parameter
                                                    Element parameter = new Element("parameter");

                                                    // defining the parameter name					
                                                    //this is done because the parameter name may be lower than 3
                                                    int length = predicateParameter.getAttributeValue("name").length();
                                                    if(length > 3){
                                                            length = 3;
                                                    }
                                                    String noIndexParameterName = predicateParameter.getAttributeValue("name").substring(0,length).toLowerCase();
                                                    List<?> result = null;
                                                    try {
                                                            // checks if there is already a parameter with that name
                                                            XPath path = new JDOMXPath("parameter[starts-with(@name,'" + noIndexParameterName + "')]");
                                                            result = path.selectNodes(action);
                                                    } catch (JaxenException e) {			
                                                            e.printStackTrace();
                                                    }
                                                    if(result.size() > 0){						
                                                            boolean hasSameName = true;
                                                            String parameterName = "";
                                                            for(int i = result.size(); hasSameName; i++){
                                                                    parameterName = noIndexParameterName + i;
                                                                    Element sameNameParameter = null;
                                                                    try {						
                                                                            XPath path = new JDOMXPath("parameter[@name='" + parameterName + "']");
                                                                            sameNameParameter = (Element)path.selectSingleNode(action);
                                                                    } catch (JaxenException e) {			
                                                                            e.printStackTrace();
                                                                    }
                                                                    if(sameNameParameter == null){
                                                                            hasSameName = false;
                                                                    }
                                                            }
                                                            parameter.setAttribute("name", parameterName);

                                                    }
                                                    else{
                                                            parameter.setAttribute("name", noIndexParameterName);
                                                    }
                                                    // define the parameter type
                                                    parameter.setAttribute("type", predicateParameter.getAttributeValue("type"));

                                                    // removes the refPredicate from the precondition
                                                    Element refPredicateParent = refPredicate.getParentElement();
                                                    refPredicateParent.removeContent(refPredicate);
                                                    // removes the null parameter
                                                    refPredicate.removeContent(refPredicate.getChildren("parameter").size()-1);
                                                    // creates a new reference parameter
                                                    Element refParameter = new Element("parameter");
                                                    refParameter.setAttribute("id", parameter.getAttributeValue("name"));
                                                    // adds the new reference to the predicate
                                                    refPredicate.addContent(refParameter);
                                                    // adds the predicate to a such that node
                                                    Element suchthat = new Element("suchthat");
                                                    suchthat.addContent(refPredicate);
                                                    // creates the exists node
                                                    Element exists = new Element("exists");
                                                    // adds the parameter and the suchthat node
                                                    exists.addContent(parameter);
                                                    exists.addContent(suchthat);
                                                    // adds the exists node to a not node
                                                    Element not = new Element("not");
                                                    not.addContent(exists);
                                                    // adds the not node to the condition level where the original predicate was
                                                    refPredicateParent.addContent(not);
                                            }

                                    }
    //				Replace all predicates with null values with forall((?x - <type>)(not(<predicate>)))
                                    for (Iterator<?> iterator = actionPostconditions.iterator(); iterator.hasNext();) {
                                            Element refPredicate = (Element) iterator.next();
                                            Element refPredicateParent = refPredicate.getParentElement();
                                            if(refPredicateParent.getName().equals("not")){
                                                    // removes the predicate						
                                                    Element parent = refPredicateParent.getParentElement();
                                                    refPredicateParent.detach();

                                                    // checks if a "and" node has now just 1 parameter
                                                    // if so, remove it
                                                    if(parent.getName().equals("and") && parent.getChildren().size() < 2){
                                                            Element child = (Element)((Element)parent.getChildren().get(0)).detach();
                                                            parent.getParentElement().addContent(child);
                                                            parent.detach();
                                                    }
                                            }
                                            else{
                                                    Element predicate = null;				
                                                    try {
                                                            XPath path = new JDOMXPath("predicates/predicate[@name='" + refPredicate.getAttributeValue("id") + "']");
                                                            predicate = (Element)path.selectSingleNode(xpddlDomain);
                                                    } catch (JaxenException e) {			
                                                            e.printStackTrace();
                                                    }
                                                    if(predicate != null){
                                                            Element predicateParameter = (Element)predicate.getChildren("parameter").get(predicate.getChildren("parameter").size()-1);

                                                            // create the parameter
                                                            Element parameter = new Element("parameter");

                                                            // defining the parameter name
                                                            List<?> result = null;
                                                            //this is done because the parameter name may be lower than 3
                                                            int length = predicateParameter.getAttributeValue("name").length();
                                                            if(length > 3){
                                                                    length = 3;
                                                            }
                                                            String noIndexParameterName = predicateParameter.getAttributeValue("name").substring(0,length).toLowerCase();
                                                            try {
                                                                    // checks if there is already a parameter with that name
                                                                    XPath path = new JDOMXPath("parameter[starts-with(@name,'" + noIndexParameterName + "')]");
                                                                    result = path.selectNodes(action);
                                                            } catch (JaxenException e) {			
                                                                    e.printStackTrace();
                                                            }
                                                            if(result.size() > 0){						
                                                                    boolean hasSameName = true;
                                                                    String parameterName = "";
                                                                    for(int i = result.size(); hasSameName; i++){
                                                                            parameterName = noIndexParameterName + i;
                                                                            Element sameNameParameter = null;
                                                                            try {						
                                                                                    XPath path = new JDOMXPath("parameter[@name='" + parameterName + "']");
                                                                                    sameNameParameter = (Element)path.selectSingleNode(action);
                                                                            } catch (JaxenException e) {			
                                                                                    e.printStackTrace();
                                                                            }
                                                                            if(sameNameParameter == null){
                                                                                    hasSameName = false;
                                                                            }
                                                                    }
                                                                    parameter.setAttribute("name", parameterName);

                                                            }
                                                            else{
                                                                    parameter.setAttribute("name", noIndexParameterName);
                                                            }
                                                            // define the parameter type
                                                            parameter.setAttribute("type", predicateParameter.getAttributeValue("type"));

                                                            // removes the refPredicate from the postcondition							
                                                            refPredicateParent.removeContent(refPredicate);
                                                            // removes the null parameter
                                                            refPredicate.removeContent(refPredicate.getChildren("parameter").size()-1);
                                                            // creates a new reference parameter
                                                            Element refParameter = new Element("parameter");
                                                            refParameter.setAttribute("id", parameter.getAttributeValue("name"));
                                                            // adds the new reference to the predicate
                                                            refPredicate.addContent(refParameter);
                                                            // adds the predicate to a not node
                                                            Element not = new Element("not");
                                                            not.addContent(refPredicate);
                                                            // creates the forall node
                                                            Element forall = new Element("forall");
                                                            // adds the parameter and the not node
                                                            forall.addContent(parameter);
                                                            forall.addContent(not);					
                                                            // adds the forall node to the condition level where the original predicate was
                                                            refPredicateParent.addContent(forall);
                                                    }
                                            }
                                    }
                            }break;
                            }

                            //7.5. Symplify operations
                            simplifyOperations(action);			

                            // 7.6 durative actions (preliminary version)
                            if(isDurative){				
                                    // set precondition node to condition
                                    action.getChild("precondition").setName("condition");



                                    // add at start node in precondition and at-end node in effect
                                    Element condition = action.getChild("condition");	
                                    if(condition.getChildren().size() > 0){
                                            
                                            //XMLUtilities.printXML(condition);

                                            if (timingDiagram!=null){
                                                //check if it is the condition
                                                setTimeIndexToConditions(timingDiagram, operator, condition);
                                            }
                                            else{
                                                Element atStart = new Element("at-start");
                                                atStart.addContent(((Element)condition.getChildren().get(0)).detach());
                                                condition.addContent(atStart);
                                            }

                                    }


                                    Element effect = action.getChild("effect");
                                    if(effect.getChildren().size() > 0){
                                            if (timingDiagram!=null){
                                                //check if it is the condition
                                                setTimeIndexToConditions(timingDiagram, operator, effect);
                                            }
                                            else{
                                                Element atEnd = new Element("at-end");
                                                atEnd.addContent(((Element)effect.getChildren().get(0)).detach());
                                                effect.addContent(atEnd);
                                            }
                                            
                                    }
                            }
                            //XMLUtilities.printXML(action);


                            xpddlDomain.getChild("actions").addContent(action);
                        //}

		}

		// 10. Put the requirements in the XPDDL
		// Analyse each necessary requirement
		
		//10.1 Strips		
		List<?> result = null;
		// look for a boolean attribute in a type with "utility" stereotype
		try {
			XPath path = new JDOMXPath("project/elements/classes/class[stereotype='utility']/attributes/attribute[type='1' and count(parameters/parameter)=0]");
			result = path.selectNodes(project.getDocument());
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		if(result.size() > 0){
			xpddlDomain.getChild("requirements").addContent(new Element("strips"));
		}
		
		//10.2 Typing
		//the itSIMPLE always uses the typing requirement
		xpddlDomain.getChild("requirements").addContent(new Element("typing"));	
		
		//10.3 Fluent		
		if(xpddlDomain.getChild("functions").getChildren().size() > 0){
			xpddlDomain.getChild("requirements").addContent(new Element("fluents"));
		}
			
		//10.4 Negative-preconditions		
		result = null;
		// look for a 'not' in the precondition
		try {
			XPath path = new JDOMXPath("actions/action/precondition/descendant::not");
			result = path.selectNodes(xpddlDomain);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		if(result.size() > 0){
			xpddlDomain.getChild("requirements").addContent(new Element("negative-preconditions"));
		}

		//10.5 disjunctive-preconditions		
		result = null;
		// look for a 'or' in the precondition
		try {
			XPath path = new JDOMXPath("actions/action/precondition/descendant::or");
			result = path.selectNodes(xpddlDomain);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		if(result.size() > 0){
			xpddlDomain.getChild("requirements").addContent(new Element("disjunctive-preconditions"));
		}
		
		//10.6 Equality		
		// look for 'equals' nodes with both children named 'parameter'
		try {
			XPath path = new JDOMXPath("actions/action/descendant::equals[count(parameter)=2]");	
			result = path.selectNodes(xpddlDomain);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		if(result.size() > 0){
			xpddlDomain.getChild("requirements").addContent(new Element("equality"));
		}
		
		
		//10.7 quantified-preconditions		
		result = null;
		// look for 'exists' and 'forall' in the actions and cosntraints
		try {
			XPath path = new JDOMXPath("actions/action/descendant::exists | actions/action/descendant::forall | " +
					"constraints/descendant::exists | constraints/descendant::forall");
			result = path.selectNodes(xpddlDomain);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		if(result.size() > 0){
			xpddlDomain.getChild("requirements").addContent(new Element("quantified-preconditions"));
		}
		
		//10.8 conditional-effects		
		result = null;
		// look for a 'when' in the action
		try {
			XPath path = new JDOMXPath("actions/action/descendant::when");
			result = path.selectNodes(xpddlDomain);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		if(result.size() > 0){
			xpddlDomain.getChild("requirements").addContent(new Element("conditional-effects"));
		}
		
		//10.9 durative-actions		
		result = null;
		// look for durative-action 
		try {
			XPath path = new JDOMXPath("actions/durative-action");
			result = path.selectNodes(xpddlDomain);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		if(result.size() > 0){
			xpddlDomain.getChild("requirements").addContent(new Element("durative-actions"));
		}		
		
		//10.10 Constraints		
		if(xpddlDomain.getChild("constraints").getChildren().size() > 0){
			xpddlDomain.getChild("requirements").addContent(new Element("constraints"));
		}
		
		// 10.11 ADL
		// check the need of adl, if it is the case, replace the other nodes with it
		try {
			XPath path = new JDOMXPath("strips | typing | negative-preconditions | disjunctive-preconditions | " +
					"equality | quantified-preconditions | conditional-effects");
			result = path.selectNodes(xpddlDomain.getChild("requirements"));
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		if(result.size() == 7){
			xpddlDomain.getChild("requirements").removeChild("strips");
			xpddlDomain.getChild("requirements").removeChild("typing");
			xpddlDomain.getChild("requirements").removeChild("negative-preconditions");
			xpddlDomain.getChild("requirements").removeChild("disjunctive-preconditions");
			xpddlDomain.getChild("requirements").removeChild("equality");
			xpddlDomain.getChild("requirements").removeChild("quantified-preconditions");
			xpddlDomain.getChild("requirements").removeChild("conditional-effects");
			
			xpddlDomain.getChild("requirements").addContent(new Element("adl"));
		}

                //XMLUtilities.printXML(xpddlDomain);
		return xpddlDomain;
	}

	private static Element buildCondition(Element expressionTreeRoot, Element action, 
                    Element operation, String conditionType){
        //XMLUtilities.printXML(expressionTreeRoot);
		Element node = null;
		String data = expressionTreeRoot.getAttributeValue("data");

                //System.out.println(" - Data: " + data);

                //leaf nodes
		if(expressionTreeRoot.getChildren().size() == 0){
                        //it's not an operator
			
			// check whether the node is a function/predicate
			// this is the case of the functions/predicates originated from global attributes
			// i.e. the ones that don't belong to a type(the utility stereotype)
			List<?> result = null;
			try {
				XPath path = new JDOMXPath("predicates/predicate[@name='" + data + "'] | " +
						"functions/function[@name='" + data + "']");
				
				result = path.selectNodes(xpddlDomain);
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			if (result.size() > 0) {
				Element predOrFunc = (Element)result.get(0);
				node = new Element(predOrFunc.getName());
				node.setAttribute("id", data);
				
			}
			else{
				
                                // check whether the node is an action parameter
				Element parameter = null;
				try {
					XPath path = new JDOMXPath("parameters/parameter[@name='" + data + "']");
					parameter = (Element)path.selectSingleNode(action);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(parameter != null){
					node = new Element("parameter");
					node.setAttribute("id", data);
				}
				else{
					
                    // checks whether the node is an operation parameter
					try {
						XPath path = new JDOMXPath("parameter[@name='" + data + "']");
						parameter = (Element)path.selectSingleNode(operation);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}					
					if(parameter != null){
						node = new Element("parameter");
						node.setAttribute("id", data);
					}					
					else if(data.equals("null")){
						node = new Element("parameter");
						node.setAttribute("id", "null");
					}
                    else if(data.equals("self")){
						node = new Element("parameter");
						node.setAttribute("id", data);
					}
                                        //parameterized attributes
					else if(data.indexOf("(") > 0 && data.indexOf(")") > 0){
						// parameterized attribute
						String attributeName = data.substring(0,data.indexOf("("));
						String parameters = data.substring(data.indexOf("(")+1, data.indexOf(")"));
						result = null;
						try {
							XPath path = new JDOMXPath("predicates/predicate[@name='" + attributeName + "'] | " +
									"functions/function[@name='" + attributeName + "']");
							result = path.selectNodes(xpddlDomain);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}

						if (result.size() > 0) {
							Element predOrFunc = (Element)result.get(0);
							// create the predicate/function
							node = new Element(predOrFunc.getName());
							node.setAttribute("id", attributeName.trim());
							StringTokenizer st = new StringTokenizer(parameters, ",");
							while(st.hasMoreTokens()){
								// for each parameter, create a node
								String parameterStr = st.nextToken().trim();

                                //Element xpddlParameter = new Element("parameter");
                                //xpddlParameter.setAttribute("id", parameterStr);
                                //node.addContent(xpddlParameter);

                                //TODO: we need to check if this is a parameter,, an object in a problem

                                if (action != null){
                                    //check if this is a constant
                                   Element constant = null;
                                    try {
                                        XPath path = new JDOMXPath("constants/constant[@name='" + parameterStr + "']");
                                        constant = (Element) path.selectSingleNode(xpddlDomain);
                                    } catch (JaxenException e) {
                                        e.printStackTrace();
                                    }
                                    if (constant != null) {//it is a constant
                                        Element xpddlParameter = new Element("object");
                                        xpddlParameter.setAttribute("id", parameterStr);
                                        node.addContent(xpddlParameter);
                                    }else{
                                       Element xpddlParameter = new Element("parameter");
                                       xpddlParameter.setAttribute("id", parameterStr);
                                       node.addContent(xpddlParameter);
                                    }
                                }
                                else{//if it is not in a action context
                                    Element xpddlParameter = new Element("object");
                                    xpddlParameter.setAttribute("id", parameterStr);
                                    node.addContent(xpddlParameter);
                                }
							}
						}
					}
					else if(data.equals(TIME_METRIC_REPLACEMENT)){
						node = new Element("function");
						node.setAttribute("id", "total-time");
					}
					else{
						try{
							Double.parseDouble(data);
							
							node = new Element("value");
							node.setAttribute("number", data);
						}catch(NumberFormatException e){
							// checks whether it's a boolean value
							if(data.toLowerCase().equals("true") || data.toLowerCase().equals("false")){
								node = new Element("boolean");
								node.setAttribute("id", data.toLowerCase());
							}
							else{
								// translate it as a constant
								node = new Element("constant");
								node.setAttribute("id", data);
								
								//check if it is really a constant
								Element constant = null;
								try {
									XPath path = new JDOMXPath("constants/constant[@name='" + data + "']");									
									constant = (Element)path.selectSingleNode(xpddlDomain);
								} catch (JaxenException e1) {			
									e1.printStackTrace();
								}
								
								if(constant == null){
									// print error message
									System.err.println("ERROR: \""+ data +"\" is not a known expression.");
								}

							}

						}
					}
				}					
			}			
		}
		//not leaf nodes
		else if(data.equals("=") || data.equals("<>")){			
			Element left = buildCondition((Element)expressionTreeRoot.getChildren().get(0), action, operation, conditionType);
			Element right = buildCondition((Element)expressionTreeRoot.getChildren().get(1), action, operation, conditionType);
			
			if(left.getName().equals("predicate") || right.getName().equals("predicate")){
				// node is an incomplete predicate
				node = (left.getName().equals("predicate")) ?left :right;
				Element parameter = (!left.getName().equals("predicate")) ?left :right;
				if(parameter.getName().equals("boolean")){
					if(parameter.getAttributeValue("id").equals("false")){						
						Element predicate = node;
						node = new Element("not");
						node.addContent(predicate);
					}
				}
				// non primitive attribute or association
				else if(parameter.getName().equals("parameter") 
						&& parameter.getChildren().size() > 0){
					Element paramPredicate = (Element)parameter.getChild("predicate").detach();
					if(node.getAttribute("id").getValue().equals(paramPredicate.getAttribute("id").getValue()) &&
							node.getChild("parameter").getAttribute("id").getValue().equals(
									paramPredicate.getChild("parameter").getAttribute("id").getValue())){
						// the atribution is consistent:
						//a.y = a.y->including(x) => (a y x)
						node.addContent(parameter);
						
						// excluding
						if(parameter.getAttribute("opn").getValue().equals("excluding")){
							Element predicate = node;
							node = new Element("not");
							node.addContent(predicate);
						}
						
						// remove the mark
						parameter.removeAttribute(parameter.getAttribute("opn"));

						
					}
					else{
						System.err.println("Not handled attribution");
					}
						 
				}
				else{
					node.addContent(parameter);	
				}			
			}
                        //Function
			else{
				if(conditionType.equals(PRECONDITION)){
					node = new Element("equals");

                                        //PDDL 3.1
                                        if (right.getName().equals("parameter")){
                                             if (right.getAttributeValue("id").equals("null")){
                                                 right.setName("value");
                                                 right.removeAttribute("id");
                                                 right.setAttribute("object", "undefined");
                                             }
                                        }
                                        node.addContent(left);
					node.addContent(right);
				}
				else if(conditionType.equals(POSTCONDITION)){
					
					//check the case of increase, decrease, scale-up or scale-down
					/*       =
					 *     /   \
					 *    A    op
					 *        /  \
					 *    	 A   ...		
					 * 
					 */
					
					//check if right is an arithmetic operator
					if(right.getName().equals("add") ||
							right.getName().equals("subtract") ||
							right.getName().equals("multiply") ||
							right.getName().equals("divide")){
						String expression = "function[@id='"+ left.getAttributeValue("id") +"'";
						for (Iterator<?> iter = left.getChildren().iterator(); iter.hasNext();) {
							Element parameterOrObject = (Element) iter.next(); //it can be a paramet
							expression += " and "+parameterOrObject.getName()+"/@id='"+ parameterOrObject.getAttributeValue("id") +"'";
						}						
						expression += "]";

                        //System.out.println(expression);
						
						Element variable = null;
						try {
							XPath path = new JDOMXPath(expression);
							variable = (Element) path.selectSingleNode(right);
						} catch (JaxenException e) {
							e.printStackTrace();
						}
						if(variable == null){
							// not the case of increase, etc.
							node = new Element("assign");
							node.addContent(left);
							node.addContent(right);
						}
						else{
							// case of increase, etc.
							if(right.getName().equals("add"))
								node = new Element("increase");
							else if(right.getName().equals("subtract"))
								node = new Element("decrease");
							else if(right.getName().equals("multiply"))
								node = new Element("scale-up");
							else if(right.getName().equals("divide"))
								node = new Element("scale-down");

							node.addContent(left);
							right.removeContent(variable);
							Element rightChild = (Element)((Element)right.getChildren().get(0)).clone();
							node.addContent(rightChild);
						}
					}
					else{
						//case of A = B
						node = new Element("assign");

                        //PDDL 3.1
                        if (right.getName().equals("parameter")){
                            if (right.getAttributeValue("id").equals("null")){
                                right.setName("value");
                                right.removeAttribute("id");
                                right.setAttribute("object", "undefined");
                            }
                        }
                        node.addContent(left);
						node.addContent(right);
					}
				}
			}
			if(data.equals("<>")){
				Element not = new Element("not");
				not.addContent(node);
				node = not;
			}
		}
		else if (data.equals(".")){
			// the last node is a function or a predicate
			Element lastNode = (Element)expressionTreeRoot.getChildren().get(expressionTreeRoot.getChildren().size()-1);
			Element firstNode = (Element)expressionTreeRoot.getChildren().get(0);
			String lastNodeData = lastNode.getAttributeValue("data");
						
			if(lastNodeData.indexOf("(") > 0 && lastNodeData.indexOf(")") > 0){
                            
                                if(lastNodeData.equals("allInstances()")){
                                    node = new Element("allInstances");
                                    node.setAttribute("type", firstNode.getAttributeValue("data"));
                                }
                                else{
                                    // parameterized attribute
                                    // lastNode has the parameterized attribute token
                                    node = buildCondition(lastNode, action, operation, conditionType);
                                    // the first node represents the first function/predicate parameter
                                    Element parameter = null;
                                    try {
                                            XPath path = new JDOMXPath("parameters/parameter[@name='" + firstNode.getAttributeValue("data") + "']");
                                            parameter = (Element)path.selectSingleNode(action);
                                    } catch (JaxenException e) {			
                                            e.printStackTrace();
                                    }
                                    if(parameter == null){
                                            try {
                                                    XPath path = new JDOMXPath("parameter[@name='" + firstNode.getAttributeValue("data") + "']");
                                                    parameter = (Element)path.selectSingleNode(operation);
                                            } catch (JaxenException e) {			
                                                    e.printStackTrace();
                                            }
                                    }
                                    if(parameter == null){
                                            //	consider the argument as constant, since it's not an action nor an operation parameter
                                            Element constant = new Element("constant");
                                            constant.setAttribute("id", firstNode.getAttributeValue("data"));
                                            node.addContent(0, constant);

                                            //for constraints
                                            if (firstNode.getAttributeValue("data").equals("self")){
                                                constant.setName("parameter");

                                            }
                                    }
                                    else{
                                            // add the parameter to the predicate/function as the first one
                                            Element param = new Element("parameter");
                                            param.setAttribute("id", firstNode.getAttributeValue("data"));
                                            node.addContent(0, param);
                                    }
                                }
				
			}
			else{
				List<?> result = null;
				try {
					XPath path = new JDOMXPath("predicates/predicate[@name='" + lastNodeData + "'] | " +
							"functions/function[@name='" + lastNodeData + "']");
					
					result = path.selectNodes(xpddlDomain);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if (result.size() > 0) {
					Element predOrFunc = (Element)result.get(0);
					node = new Element(predOrFunc.getName());
					node.setAttribute("id", lastNodeData);
					
					Element parameter = null;
					try {
						XPath path = new JDOMXPath("parameters/parameter[@name='" + firstNode.getAttributeValue("data") + "']");
						parameter = (Element)path.selectSingleNode(action);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(parameter == null){
						try {
							XPath path = new JDOMXPath("parameter[@name='" + firstNode.getAttributeValue("data") + "']");
							parameter = (Element)path.selectSingleNode(operation);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
					}
					if(parameter == null){
						// consider the argument as constant, since it's not an action parameter
						Element constant = new Element("constant");
						constant.setAttribute("id", firstNode.getAttributeValue("data"));
						node.addContent(constant);

                                                //constraints
                                                if (firstNode.getAttributeValue("data").equals("self")){
                                                    constant.setName("parameter");
                                                }

						
					}
					else{
						// add the parameter to predicate as the first one
						Element param = new Element("parameter");
						param.setAttribute("id", firstNode.getAttributeValue("data"));
						node.addContent(param);
					}					
				}
				else{
					System.err.println("Error: function or predicate \"" + lastNodeData + "\" not found");					
				}
			}
			
		}
		
		else if(data.equals("->")){
			//the first child holds an incomplete predicate
			Element firstChild = (Element)expressionTreeRoot.getChildren().get(0);
			Element predicate = buildCondition(firstChild, action, operation, conditionType);
			
			//the second node holds the operation
			Element secondChild = (Element)expressionTreeRoot.getChildren().get(1);
			String operationName = secondChild.getAttributeValue("data");
			
			if(operationName.toLowerCase().equals("exists")){
//				 1. exists
				node = new Element("exists");
				
				// set the parameters			
				List<?> result = null;
				try {
					XPath path = new JDOMXPath("child::*[@type='par']");
					result = path.selectNodes(secondChild);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				Element predicates = new Element("predicates");
				for (Iterator<?> iter = result.iterator(); iter.hasNext();) {
					Element parameter = (Element) iter.next();
					Element xpddlParam = new Element("parameter");
					String strParam = parameter.getAttributeValue("data");
					String paramName = strParam.substring(0, strParam.indexOf(":"));
					xpddlParam.setAttribute("name", paramName);
					xpddlParam.setAttribute("type", strParam.substring(strParam.indexOf(":")+1));				
					
					node.addContent(xpddlParam);
					
					// create the predicates
					Element refParam = new Element("parameter");
					refParam.setAttribute("id", paramName);                                        
                                        Element predClone = (Element)predicate.clone();
                                        predClone.addContent(refParam);
                                        predicates.addContent(predClone);
                                       
				}
				
				//suchthat
				Element suchthat = new Element("suchthat");
				// add the predicates
				Element container;
				if(predicates.getChildren().size() > 0){
					container = new Element("and");				
					for (Iterator<?> iter = predicates.getChildren().iterator(); iter.hasNext();) {
						Element pred = (Element) iter.next();
                                                if(!pred.getName().equals("allInstances"))
                                                    container.addContent((Element)pred.clone());
					}
					suchthat.addContent(container);
				}
				else{
					container = suchthat;
				}
				
				// the last statement
				Element statementNode = (Element)((Element)secondChild.getChildren().get(secondChild.getChildren().size()-1)).getChildren().get(0);
				Element xpddlStatement = buildCondition(statementNode, action, node, conditionType);
				if(xpddlStatement.getName().equals("and")){
					for (Iterator<?> iter = xpddlStatement.getChildren().iterator(); iter.hasNext();) {
						Element statement = (Element) iter.next();						
						container.addContent((Element)statement.clone());					
					}
				}
				else{
					container.addContent(xpddlStatement);
				}			
				
				node.addContent(suchthat);                                
			}
			//2. forAll			
			else if(operationName.toLowerCase().equals("forall")){
				try{
					node = new Element("forall");
					
					// set the parameters			
					List<?> result = null;
					try {
						XPath path = new JDOMXPath("child::*[@type='par']");
						result = path.selectNodes(secondChild);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					Element primaryNode;
					Element secondaryNode;
					
					if(conditionType.equals(PRECONDITION)){
						primaryNode = new Element("imply");
						secondaryNode = new Element("implies");
					}
					else{
						primaryNode = new Element("when");
						secondaryNode = new Element("do");
					}
						
					Element and = new Element("and");
					for (Iterator<?> iter = result.iterator(); iter.hasNext();) {
						Element parameter = (Element) iter.next();
						Element xpddlParam = new Element("parameter");
						String strParam = parameter.getAttributeValue("data");
						String paramName = strParam.substring(0, strParam.indexOf(":"));
						xpddlParam.setAttribute("name", paramName);
						xpddlParam.setAttribute("type", strParam.substring(strParam.indexOf(":")+1));				
						
						node.addContent(xpddlParam);
						
						// create the predicates
						Element refParam = new Element("parameter");
						refParam.setAttribute("id", paramName);
						Element predClone = (Element)predicate.clone();
						predClone.addContent(refParam);
						and.addContent(predClone);
					}
					if(and.getChildren().size() > 1){
						primaryNode.addContent(and);
					}
					else{
						primaryNode.addContent(and.removeContent(0));
					}				
								
					//the implication						
					Element statementNode = (Element)((Element)secondChild.getChildren().get(secondChild.getChildren().size()-1)).getChildren().get(0);
					Element xpddlStatement = null;
					try{
						xpddlStatement = buildCondition(statementNode, action, node, conditionType);
					}catch(Exception e){
						e.printStackTrace();
					}
					secondaryNode.addContent(xpddlStatement);
					
					primaryNode.addContent(secondaryNode);
					node.addContent(primaryNode);                                       
				}
				catch(Exception e){
					e.printStackTrace();
				}			
			}
			
			// 3. includes, excludes
			else if(operationName.toLowerCase().equals("includes") ||
					operationName.toLowerCase().equals("excludes")){
				
				Element parameter = buildCondition(secondChild.getChild("node"), action, operation, conditionType);
				predicate.addContent(parameter);

				if(operationName.toLowerCase().equals("includes")){
					node = predicate;
				}
				else{
					// excludes
					node = new Element("not");
					node.addContent(predicate);
				}

			}
			// 4. including, excluding
			else if(operationName.toLowerCase().equals("including") ||
					operationName.toLowerCase().equals("excluding")){
				node = buildCondition(secondChild.getChild("node"), action, operation, conditionType);
				// this attribute will be deleted after
				// it only marks if it's an inclusion or exclusion
				node.setAttribute("opn", operationName.toLowerCase());
				node.addContent(predicate);
			}
			
			
			// 5. isEmpty, notEmpty
			else if(operationName.toLowerCase().equals("isempty()") ||
						operationName.toLowerCase().equals("notempty()")){
				Element parameter = new Element("parameter");
				parameter.setAttribute("id", "null");
				
				predicate.addContent(parameter);
				
				if(operationName.toLowerCase().equals("isempty()")){
					node = predicate;
				}
				else{
					node = new Element("not");
					node.addContent(predicate);
				}
			}
		}

		
		else if(data.equals("if-then-else")){
			// currently "else" is not dealed
			if(conditionType.equals(PRECONDITION)){
				// in precondition, an if-then-else structure is parsed
				// as imply in PDDL

				// the first node is the condition 
				Element conditionNode = (Element)expressionTreeRoot.getChildren().get(0);
				// the second node is "then"
				Element thenNode = (Element)expressionTreeRoot.getChildren().get(1);
				// the third node is "else"
				
				node = new Element("imply");
				node.addContent(buildCondition(conditionNode, action, operation, PRECONDITION));
				Element implies = new Element("implies");
				implies.addContent(buildCondition(thenNode, action, operation, POSTCONDITION));
				node.addContent(implies);
				
			}
			else{
				// postcondition
				// in postcondition, an if-then-else structure is parsed
				// as when in PDDL

				// the first node is the condition 
				Element conditionNode = (Element)expressionTreeRoot.getChildren().get(0);
				// the second node is "then"
				Element thenNode = (Element)expressionTreeRoot.getChildren().get(1);
				// the third node is "else"
				
				node = new Element("when");
				node.addContent(buildCondition(conditionNode, action, operation, PRECONDITION));
				Element doNode = new Element("do");
				doNode.addContent(buildCondition(thenNode, action, operation, POSTCONDITION));
				node.addContent(doNode);				
				
			}
		}
		
		else{			
			data = data.toLowerCase();
			if(data.equals("and") || data.equals("or")){
				node = new Element(data);
			}
			else if(data.equals(">")){
				node = new Element("gt");
			}
			else if(data.equals("<")){
				node = new Element("lt");
			}
			else if(data.equals(">=")){
				node = new Element("ge");
			}
			else if(data.equals("<=")){
				node = new Element("le");
			}
			else if(data.equals("+")){
				node = new Element("add");
			}
			else if(data.equals("-")){
				node = new Element("subtract");
			}
			else if(data.equals("*")){
				node = new Element("multiply");
			}
			else if(data.equals("/")){
				node = new Element("divide");
			}
                        else if(data.equals("not")){
                            node = new Element("not");
                        }
			/*else if(data.equals("|")){
				node = new Element("suchthat");
			}*/
			else{
				System.err.println(data);
			}
			for (Iterator<?> iter = expressionTreeRoot.getChildren().iterator(); iter.hasNext();) {
				Element child = (Element) iter.next();
				Element xpddlChild = buildCondition(child, action, operation, conditionType);				
				
				if(xpddlChild != null){
					node.addContent(xpddlChild);					
				}
				else{
					System.err.println("Error parsing " + conditionType + "\n Action: " + action.getAttributeValue("name") + ", Data: " + child.getAttributeValue("data"));
				}
			}
			
		}
		
		
		
		
		return node;
	}
	
	private static void fillOutTypes(Element types, Element tree){
		if(tree.getName().equals("hierarchy")){
			for (Iterator<?> iter = tree.getChildren().iterator(); iter.hasNext();) {
				Element highClass = (Element) iter.next();
				fillOutTypes(types, highClass);				
			}
		}
		else{
			Element type = new Element("type");
			type.setAttribute("name", tree.getAttributeValue("name"));
			Element parent = tree.getParentElement();
			if(parent.getName().equals("hierarchy"))
				type.setAttribute("parent", "object");
			else
				type.setAttribute("parent", parent.getAttributeValue("name"));
			
			types.addContent(type);
			
			for (Iterator<?> iter = tree.getChildren().iterator(); iter.hasNext();) {
				Element highClass = (Element) iter.next();
				fillOutTypes(types, highClass);				
			}

		}
	}
	
	public static Element XMLToXPDDLProblem(Element problem, String pddlVersion){
		Element xpddlNodes = null;
		xpddlProblem = null;
		constraintsList.clear();
								  //planningPloblem  domain				planningDomains     diagrans			project
		Element project = problem.getParentElement().getParentElement().getParentElement().getParentElement().getParentElement();
		//Element project = problem.getParentElement().getParentElement().getParentElement();
		
		try {
			xpddlNodes = (Element)XMLUtilities.readFromFile("resources/settings/commonData.xml").getRootElement().getChild("xpddlNodes").clone();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(xpddlNodes != null){
			xpddlProblem = (Element)xpddlNodes.getChild("xpddlProblem").clone();
			
			// 1. Name
			String problemName = problem.getChildText("name").trim().replaceAll(" ", "_");
			if (problemName.toLowerCase().equals("problem")) problemName += "1"; //The "problem" word is a reserved word in the pddl context so it is necessary to change the name in this case
			xpddlProblem.getChild("name").setText(problemName);
			
			// 2. Domain
			xpddlProblem.getChild("domain").setText(project.getChildText("name").trim().replaceAll(" ", "_"));
			
			
			// 3. Objects

			//planningProblems  domain 
			Element domain = problem.getParentElement().getParentElement();
			/*Approach putting every object
			List<?> objects = domain.getChild("elements").getChild("objects").getChildren("object");
			for (Iterator<?> iter = objects.iterator(); iter.hasNext();) {
				Element object = (Element) iter.next();				
				
				Element objectType = null;
				try {				
					XPath path = new JDOMXPath("elements/classes/class[@id='" + object.getChildText("class") + "']");
					objectType = (Element)path.selectSingleNode(project);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(objectType != null && !objectType.getChildText("stereotype").equals("utility")){
					Element xpddlObject = new Element("object");
					xpddlObject.setAttribute("name", object.getChildText("name"));
					xpddlObject.setAttribute("type", objectType.getChildText("name"));
					xpddlProblem.getChild("objects").addContent(xpddlObject);
				}				
			}
                        */
                        
                        //Approach: putting just the objects that are used.
                        Element domainObjects = domain.getChild("elements").getChild("objects");
                        List<?> tobjectDiagrams = problem.getChild("objectDiagrams").getChildren("objectDiagram");
			for (Iterator<?> iter = tobjectDiagrams.iterator(); iter.hasNext();) {
				Element objectDiagram = (Element) iter.next();
                                List<?> objects = objectDiagram.getChild("objects").getChildren("object");
                                for (Iterator<?> iter2 = objects.iterator(); iter2.hasNext();) {
                                        Element object = (Element) iter2.next();

                                        //find the object in the domain

                                        Element theDomainObject = null;
                                        try {
                                                XPath path = new JDOMXPath("object[@id='" + object.getAttributeValue("id") + "']");
                                                theDomainObject = (Element)path.selectSingleNode(domainObjects);
                                        } catch (JaxenException e) {
                                                e.printStackTrace();
                                        }
                                        if(theDomainObject!=null){
                                            //check if there is already an object with the same name in the xpddl
                                            Element xpddlObject = null;
                                            try {
                                                    XPath path = new JDOMXPath("object[@name='" + theDomainObject.getChildText("name") + "']");
                                                    xpddlObject = (Element)path.selectSingleNode(xpddlProblem.getChild("objects"));
                                            } catch (JaxenException e) {
                                                    e.printStackTrace();
                                            }
                                            //if there is not such object yet then create it
                                            if(xpddlObject==null){
                                                Element objectType = null;
                                                try {
                                                        XPath path = new JDOMXPath("elements/classes/class[@id='" + theDomainObject.getChildText("class") + "']");
                                                        objectType = (Element)path.selectSingleNode(project);
                                                } catch (JaxenException e) {
                                                        e.printStackTrace();
                                                }
                                                if(objectType != null && !objectType.getChildText("stereotype").equals("utility")){
                                                        //Element xpddlObject = new Element("object");
                                                        xpddlObject = new Element("object");
                                                        xpddlObject.setAttribute("name", theDomainObject.getChildText("name"));
                                                        xpddlObject.setAttribute("type", objectType.getChildText("name"));
                                                        xpddlProblem.getChild("objects").addContent(xpddlObject);
                                                }

                                            }

                                        }

                                }

                        }



			// 3. Repository diagram			
			Element repositoryDiagram = domain.getChild("repositoryDiagrams").getChild("repositoryDiagram");
                        //since it is used just for reusability we do no considered it on the translation process
			//parseObjectDiagram(repositoryDiagram, xpddlProblem.getChild("init"),pddlVersion);
			
			
			// 4. object diagrams
			List<?> objectDiagrams = problem.getChild("objectDiagrams").getChildren("objectDiagram");
			for (Iterator<?> iter = objectDiagrams.iterator(); iter.hasNext();) {
				Element objectDiagram = (Element) iter.next();
				Element containerNode = null;
				if(objectDiagram.getChildText("sequenceReference").equals("init")){
					containerNode = xpddlProblem.getChild("init");
				}
				else {
                                    String seq = objectDiagram.getChildText("sequenceReference");
                                    boolean isTimedLiteral = true;
                                    try{
                                        float x = Float.parseFloat(seq);
                                    }catch(NumberFormatException nFE) {
                                         //System.out.println("Not an Integer");
                                        isTimedLiteral = false;
                                    }

                                    if (isTimedLiteral){
                                        if (pddlVersion.equals(PDDL_2_2) || pddlVersion.equals(PDDL_3_0) || pddlVersion.equals(PDDL_3_1)){
                                            //System.out.println("timed literal");
                                            containerNode = new Element("initialTimedLiteral");
                                            //containerNode = xpddlProblem.getChild("init");
                                        }
                                    }
                                    else{
					// goal and constraints (state trajectory) 
					containerNode = new Element("and");
                                    }
				}
				
				parseObjectDiagram(objectDiagram, containerNode, pddlVersion);
			}
			
			// 5. Constraints
			if(pddlVersion.equals(PDDL_3_0) || pddlVersion.equals(PDDL_3_1)){
//				5.1. Get the init snapshot
				Element initSnapshot = null;
				try {
					XPath path = new JDOMXPath("objectDiagrams/objectDiagram[sequenceReference='init']");
					initSnapshot = (Element)path.selectSingleNode(problem);
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}		
				if (initSnapshot != null) {
					
					//5.2. Get all attributes	
					List<?> result = null;
					try {
						XPath path = new JDOMXPath("objects/object/attributes/attribute");
						result = path.selectNodes(initSnapshot);
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					
					Element containerNode = new Element("and");
					for (int i = 0; i < result.size(); i++) {
						Element objectAttribute = (Element)result.get(i);
						
						// if the attribute has value then it require analysis
						if (!objectAttribute.getChildText("value").trim().equals("")) {
															//			attributes			object
							Element objectReference = objectAttribute.getParentElement().getParentElement();
							
							//5.2.1 Find its object
							Element object = null;
							try {
								XPath path = new JDOMXPath("elements/objects/object[@id='"+objectReference.getAttributeValue("id")+"']");
								//object = (Element)path.selectSingleNode(problem);
								object = (Element)path.selectSingleNode(domain);
							} catch (JaxenException e2) {			
								e2.printStackTrace();
							}
							
							//5.2.2 get the class attribute
							Element classAttribute = null;
							try {
								XPath path = new JDOMXPath("project/elements/classes/class[@id='"+objectAttribute.getAttributeValue("class")+
										"']/attributes/attribute[@id='"+objectAttribute.getAttributeValue("id")+"']");
								classAttribute = (Element)path.selectSingleNode(problem.getDocument());
							} catch (JaxenException e2) {			
								e2.printStackTrace();
							}
							
							if (object != null && classAttribute != null) {
								if (classAttribute.getChildText("changeability").equals("frozen")) {														
									
									//5.2.3. Get the type class
									Element typeClass = null;
									try {
										XPath path = new JDOMXPath("project/elements/classes/class[@id='"+classAttribute.getChildText("type")+"']");
										typeClass = (Element)path.selectSingleNode(problem.getDocument());
									} catch (JaxenException e2) {			
										e2.printStackTrace();
									}
									
									
									if (typeClass != null){									
										Element always = new Element("always");
										
										//Primitives Booloean, Int, Float, String
										if (typeClass.getChildText("type").equals("Primitive")){
											
											if (typeClass.getChildText("name").equals("Boolean")) {											
												Element predicate = new Element("predicate");
												predicate.setAttribute("id", classAttribute.getChildText("name"));											
												Element parameter = new Element("object");
												parameter.setAttribute("id", object.getChildText("name"));											
												predicate.addContent(parameter);	
												
												always.addContent(predicate);											
											} 
											else if (typeClass.getChildText("name").equals("Int") || typeClass.getChildText("name").equals("Float")){
												Element function = new Element("function");
												function.setAttribute("id", classAttribute.getChildText("name"));											
												Element parameter = new Element("object");
												parameter.setAttribute("id", object.getChildText("name"));											
												function.addContent(parameter);
												Element value = new Element("value");
												value.setAttribute("number", objectAttribute.getChildText("value"));
												
												Element equals = new Element("equals");
												equals.addContent(function);
												equals.addContent(value);
												always.addContent(equals);
											} 
											else if (typeClass.getChildText("name").equals("String")){
                                                                                            if (!pddlVersion.equals(PDDL_3_1)){
                                                                                                Element predicate = new Element("predicate");
												predicate.setAttribute("id", classAttribute.getChildText("name"));

												Element firstParameter = new Element("object");
												firstParameter.setAttribute("id", object.getChildText("name"));
												predicate.addContent(firstParameter);

												Element secondParameter = new Element("object");
												secondParameter.setAttribute("id", objectAttribute.getChildText("value"));
												predicate.addContent(secondParameter);

												always.addContent(predicate);
                                                                                            }
                                                                                            else{//pddl3.1 case
                                                                                                Element equals = new Element("equals");
                                                                                                Element function = new Element("function");
                                                                                                function.setAttribute("id", classAttribute.getChildText("name"));

                                                                                                Element firstParameter = new Element("object");
                                                                                                firstParameter.setAttribute("id", object.getChildText("name"));
                                                                                                function.addContent(firstParameter);

                                                                                                Element value = new Element("value");
                                                                                                value.setAttribute("object", objectAttribute.getChildText("value"));

                                                                                                equals.addContent(function);
                                                                                                equals.addContent(value);
                                                                                                always.addContent(equals);
                                                                                            }
													
											}
										}
										//Classes types
										else{
                                                                                    if(!pddlVersion.equals(PDDL_3_1)){
                                                                                        Element predicate = new Element("predicate");
                                                                                        predicate.setAttribute("id", classAttribute.getChildText("name"));

                                                                                        Element firstParameter = new Element("object");
                                                                                        firstParameter.setAttribute("id", object.getChildText("name"));
                                                                                        predicate.addContent(firstParameter);

                                                                                        Element secondParameter = new Element("object");
                                                                                        secondParameter.setAttribute("id", objectAttribute.getChildText("value"));
                                                                                        predicate.addContent(secondParameter);
                                                                                        always.addContent(predicate);
                                                                                    }
                                                                                    else{//pddl3.1 case
                                                                                        Element equals = new Element("equals");
                                                                                        Element function = new Element("function");
                                                                                        function.setAttribute("id", classAttribute.getChildText("name"));

                                                                                        Element firstParameter = new Element("object");
                                                                                        firstParameter.setAttribute("id", object.getChildText("name"));
                                                                                        function.addContent(firstParameter);

                                                                                        Element value = new Element("value");
                                                                                        value.setAttribute("object", objectAttribute.getChildText("value"));

                                                                                        equals.addContent(function);
                                                                                        equals.addContent(value);
                                                                                        always.addContent(equals);
                                                                                    }
			
										}
										
										containerNode.addContent(always);
									}	
								}
							}
						}
					}
					
					//5.3 Get all associations
					List<?> objectAssociations = null;
					try {
						XPath path = new JDOMXPath("associations/objectAssociation");
						objectAssociations = path.selectNodes(initSnapshot);
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					for (Iterator<?> iter = objectAssociations.iterator(); iter.hasNext();) {
						Element objectAssociation = (Element) iter.next();
						// get the class association
						Element classAssociation = null;
						try {
							XPath path = new JDOMXPath("elements/classAssociations/classAssociation[@id='"
									+ objectAssociation.getChildText("classAssociation") +"']");
							classAssociation = (Element)path.selectSingleNode(project);
						} catch (JaxenException e2) {			
							e2.printStackTrace();
						}
						if(classAssociation != null && classAssociation.getChildText("changeability").equals("frozen")){
							// get the association ends
							Element sourceEnd = (Element)classAssociation.getChild("associationEnds").getChildren("associationEnd").get(0);
							Element targetEnd = (Element)classAssociation.getChild("associationEnds").getChildren("associationEnd").get(1);
							Element sourceObjectEnd = null;
							Element targetObjectEnd = null;
							try {
								XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='"+ sourceEnd.getAttributeValue("id") +"']");
								sourceObjectEnd = (Element)path.selectSingleNode(objectAssociation);
								path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='"+ targetEnd.getAttributeValue("id") +"']");
								targetObjectEnd = (Element)path.selectSingleNode(objectAssociation);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							
							Element sourceObject = null;
							Element targetObject = null;							
							try {
								XPath path = new JDOMXPath("elements/objects/object[@id='"+ sourceObjectEnd.getAttributeValue("element-id") +"']");
								//sourceObject = (Element)path.selectSingleNode(problem);
								sourceObject = (Element)path.selectSingleNode(domain);
								path = new JDOMXPath("elements/objects/object[@id='"+ targetObjectEnd.getAttributeValue("element-id") +"']");
								//targetObject = (Element)path.selectSingleNode(problem);
								targetObject = (Element)path.selectSingleNode(domain);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}							
							if(sourceObject != null && targetObject != null){
								// 5.3.1 association with rolenames 
								if(!targetEnd.getChild("rolename").getChildText("value").trim().equals("")){
                                                                    if (!pddlVersion.equals(PDDL_3_1)){
									 Element predicate = new Element("predicate");
									 predicate.setAttribute("id", targetEnd.getChild("rolename").getChildText("value"));
									 
									 Element firstValue = new Element("object");
									 firstValue.setAttribute("id", sourceObject.getChildText("name"));
									 predicate.addContent(firstValue);
									 
									 Element secondValue = new Element("object");
									 secondValue.setAttribute("id", targetObject.getChildText("name"));
									 predicate.addContent(secondValue);
									 
									 Element always = new Element("always");
									 always.addContent(predicate);
									 containerNode.addContent(always);
                                                                    }
                                                                    else{//pddl3.1 case
                                                                        boolean isMultipliticy1or01 = false;
                                                                        String xpddlTag = "predicate";
                                                                        if (targetEnd.getChild("multiplicity").getChildText("value").equals("1") || targetEnd.getChild("multiplicity").getChildText("value").equals("0..1")){
                                                                            isMultipliticy1or01 = true;
                                                                            xpddlTag = "function";
                                                                        }
                                                                        Element predfunc = new Element(xpddlTag);
                                                                        predfunc.setAttribute("id", targetEnd.getChild("rolename").getChildText("value"));
                                                                        Element firstValue = new Element("object");
                                                                        firstValue.setAttribute("id", sourceObject.getChildText("name"));
                                                                        predfunc.addContent(firstValue);
                                                                        Element always = new Element("always");
                                                                        if (!isMultipliticy1or01){
                                                                            Element secondValue = new Element("object");
                                                                            secondValue.setAttribute("id", targetObject.getChildText("name"));
                                                                            predfunc.addContent(secondValue);
                                                                            always.addContent(predfunc);
                                                                        }
                                                                        else{//it is a object equals
                                                                            Element equals = new Element("equals");
                                                                            equals.addContent(predfunc);
                                                                            Element value = new Element("value");
                                                                            value.setAttribute("object", targetObject.getChildText("name"));
                                                                            equals.addContent(value);
                                                                            always.addContent(equals);
                                                                        }
                                                                        containerNode.addContent(always);
                                                                    }
								}
								if(!sourceEnd.getChild("rolename").getChildText("value").trim().equals("")){
                                                                    if (!pddlVersion.equals(PDDL_3_1)){
									 Element predicate = new Element("predicate");
									 predicate.setAttribute("id", sourceEnd.getChild("rolename").getChildText("value"));
									 
									 Element firstValue = new Element("object");
									 firstValue.setAttribute("id", targetObject.getChildText("name"));
									 predicate.addContent(firstValue);
									 
									 Element secondValue = new Element("object");
									 secondValue.setAttribute("id", sourceObject.getChildText("name"));
									 predicate.addContent(secondValue);
									 
									 Element always = new Element("always");
									 always.addContent(predicate);
									 containerNode.addContent(always);
                                                                    }
                                                                    else{//pddl3.1 case
                                                                        boolean isMultipliticy1or01 = false;
                                                                        String xpddlTag = "predicate";
                                                                        if (sourceEnd.getChild("multiplicity").getChildText("value").equals("1") || sourceEnd.getChild("multiplicity").getChildText("value").equals("0..1")){
                                                                            isMultipliticy1or01 = true;
                                                                            xpddlTag = "function";
                                                                        }
                                                                        Element predfunc = new Element(xpddlTag);
                                                                        predfunc.setAttribute("id", sourceEnd.getChild("rolename").getChildText("value"));
                                                                        Element firstValue = new Element("object");
                                                                        firstValue.setAttribute("id", targetObject.getChildText("name"));
                                                                        predfunc.addContent(firstValue);
                                                                        Element always = new Element("always");
                                                                        if (!isMultipliticy1or01){
                                                                            Element secondValue = new Element("object");
                                                                            secondValue.setAttribute("id", sourceObject.getChildText("name"));
                                                                            predfunc.addContent(secondValue);
                                                                            always.addContent(predfunc);
                                                                        }
                                                                        else{//it is a object equals
                                                                            Element equals = new Element("equals");
                                                                            equals.addContent(predfunc);
                                                                            Element value = new Element("value");
                                                                            value.setAttribute("object", sourceObject.getChildText("name"));
                                                                            equals.addContent(value);
                                                                            always.addContent(equals);
                                                                        }
                                                                        containerNode.addContent(always);
                                                                    }
								}
								if(sourceEnd.getChild("rolename").getChildText("value").trim().equals("") && 
										targetEnd.getChild("rolename").getChildText("value").trim().equals("")){
									// 5.3.2 Associations without rolenames
									
									boolean sourceHasNavigation = Boolean.parseBoolean(sourceEnd.getAttributeValue("navigation"));
									boolean targetHasNavigation = Boolean.parseBoolean(targetEnd.getAttributeValue("navigation"));					
									// 5.3.2.1 Double navigation or without navigation: the order of the parameters is not important
									if((sourceHasNavigation && targetHasNavigation) ||
											(!sourceHasNavigation && !targetHasNavigation)){
                                                                            if (!pddlVersion.equals(PDDL_3_1)){
										// create first flow predicate
										Element firstPredicate = new Element("predicate");
										firstPredicate.setAttribute("id", classAssociation.getChildText("name"));
										
										Element firstValue = new Element("object");
										firstValue.setAttribute("id", targetObject.getChildText("name"));
										firstPredicate.addContent(firstValue);
										
										Element secondValue = new Element("object");
										secondValue.setAttribute("id", sourceObject.getChildText("name"));										
										firstPredicate.addContent(secondValue);
										
										// create reverse flow predicate
										Element secondPredicate = new Element("predicate");
										secondPredicate.setAttribute("id", classAssociation.getChildText("name"));

										secondPredicate.addContent((Element)secondValue.clone());
									
										secondPredicate.addContent((Element)firstValue.clone());
										
										Element firstAlways = new Element("always");
										firstAlways.addContent(firstPredicate);
										containerNode.addContent(firstAlways);
										Element secondAlways = new Element("always");
										secondAlways.addContent(secondPredicate);
										containerNode.addContent(secondAlways);
                                                                            }
                                                                            else{//pddl3.1 case
                                                                                boolean sourceisMultipliticy1or01 = false;
                                                                                boolean targetisMultipliticy1or01 = false;
                                                                                if (sourceEnd.getChild("multiplicity").getChildText("value").equals("1") || sourceEnd.getChild("multiplicity").getChildText("value").equals("0..1")){
                                                                                    sourceisMultipliticy1or01 = true;
                                                                                }
                                                                                if (targetEnd.getChild("multiplicity").getChildText("value").equals("1") || targetEnd.getChild("multiplicity").getChildText("value").equals("0..1")){
                                                                                    targetisMultipliticy1or01 = true;
                                                                                }

                                                                                if (targetisMultipliticy1or01 && sourceisMultipliticy1or01){
                                                                                    //first
                                                                                    Element firstEquals = new Element("equals");
                                                                                    Element firstPredFuc = new Element("function");
                                                                                    firstPredFuc.setAttribute("id", classAssociation.getChildText("name"));
                                                                                    Element firstValue = new Element("object");
                                                                                    firstValue.setAttribute("id", targetObject.getChildText("name"));
                                                                                    firstPredFuc.addContent(firstValue);
                                                                                    firstEquals.addContent(firstPredFuc);
                                                                                    Element fValue = new Element("value");
                                                                                    fValue.setAttribute("object", sourceObject.getChildText("name"));
                                                                                    firstEquals.addContent(fValue);

                                                                                    //second
                                                                                    Element secondEquals = new Element("equals");
                                                                                    Element secondPredFuc = new Element("function");
                                                                                    secondPredFuc.setAttribute("id", classAssociation.getChildText("name"));
                                                                                    Element firstSValue = new Element("object");
                                                                                    firstSValue.setAttribute("id", sourceObject.getChildText("name"));
                                                                                    secondPredFuc.addContent(firstSValue);
                                                                                    secondEquals.addContent(secondPredFuc);
                                                                                    Element sValue = new Element("value");
                                                                                    sValue.setAttribute("object", targetObject.getChildText("name"));
                                                                                    secondEquals.addContent(sValue);

                                                                                    Element firstAlways = new Element("always");
                                                                                    firstAlways.addContent(firstEquals);
                                                                                    containerNode.addContent(firstAlways);
                                                                                    Element secondAlways = new Element("always");
                                                                                    secondAlways.addContent(secondEquals);
                                                                                    containerNode.addContent(secondAlways);

                                                                                }else{//treat as predicate
                                                                                    // create first flow predicate
                                                                                    Element firstPredicate = new Element("predicate");
                                                                                    firstPredicate.setAttribute("id", classAssociation.getChildText("name"));

                                                                                    Element firstValue = new Element("object");
                                                                                    firstValue.setAttribute("id", targetObject.getChildText("name"));
                                                                                    firstPredicate.addContent(firstValue);

                                                                                    Element secondValue = new Element("object");
                                                                                    secondValue.setAttribute("id", sourceObject.getChildText("name"));
                                                                                    firstPredicate.addContent(secondValue);

                                                                                    // create reverse flow predicate
                                                                                    Element secondPredicate = new Element("predicate");
                                                                                    secondPredicate.setAttribute("id", classAssociation.getChildText("name"));

                                                                                    secondPredicate.addContent((Element)secondValue.clone());

                                                                                    secondPredicate.addContent((Element)firstValue.clone());

                                                                                    Element firstAlways = new Element("always");
                                                                                    firstAlways.addContent(firstPredicate);
                                                                                    containerNode.addContent(firstAlways);
                                                                                    Element secondAlways = new Element("always");
                                                                                    secondAlways.addContent(secondPredicate);
                                                                                    containerNode.addContent(secondAlways);
                                                                                }

                                                                            }
									}
									else{
                                                                            //if (!pddlVersion.equals(PDDL_3_1)){
                                                                                // 5.3.2.2 Single navigation: the order is set by the navigation
										Element associationSource = (sourceHasNavigation) ?sourceObject :targetObject;
										Element associationTarget = (sourceHasNavigation) ?targetObject :sourceObject;
										Element predicate = new Element("predicate");
										predicate.setAttribute("id", classAssociation.getChildText("name"));
										Element firstValue = new Element("object");
										firstValue.setAttribute("id", associationTarget.getChildText("name"));
										
										predicate.addContent(firstValue);
										
										Element secondValue = new Element("object");										
										secondValue.setAttribute("id", associationSource.getChildText("name"));
										predicate.addContent(secondValue);
										
										Element always = new Element("always");
										always.addContent(predicate);
										containerNode.addContent(always);
                                                                            //}
									}									
								}					
							}
						}
					}
					
					// 6. get other constraints from object diagrams that represent the state trajectory
					for (Iterator<Element> iter = constraintsList.iterator(); iter.hasNext();) {
						Element constraint = iter.next();
						
						containerNode.addContent(constraint);					
					}
					
					if(containerNode.getChildren().size() > 0){
						if(containerNode.getChildren().size() == 1){
							Element node = (Element)containerNode.removeContent(0);
							containerNode = node;
						}
						
						xpddlProblem.getChild("constraints").addContent(containerNode);
					}
				}
			}
			
			//6. metrics

			/*Element metric = null;
			try {
				XPath path = new JDOMXPath("metrics/metric[enabled='true']");
				metric = (Element)path.selectSingleNode(problem);
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			
			if(metric != null){
				Element xpddlMetric = 
					(Element)ItSIMPLE.getCommonData().getChild("xpddlNodes").getChild("metric").clone();
				
				xpddlMetric.getChild("optimization").setText(metric.getChildText("type"));
				String metricExpression = metric.getChildText("function");
				boolean totalTime = false;
				if(metricExpression.indexOf("total-time") >= 0){
					metricExpression = metricExpression.replaceAll("total-time", TIME_METRIC_REPLACEMENT);
					totalTime = true;
				}
				ExpressionTreeBuilder builder = new ExpressionTreeBuilder(metricExpression);
				Element expTree = builder.getExpressionTree();
				Element condition = buildCondition(expTree, null, null, null);
				xpddlMetric.getChild("expression").addContent(buildCondition(expTree, null, null, null));
				
				xpddlProblem.addContent(xpddlMetric);
			}*/

                        /*SELECET A SINGLE METRIC FOR THE PLANNER
                        Element metric = null;
			try {
				XPath path = new JDOMXPath("metrics/qualityMetric[enabled='true' and (intention='minimize' or intention='maximize')]");
				metric = (Element)path.selectSingleNode(problem);
			} catch (JaxenException e) {
				e.printStackTrace();
			}

			if(metric != null){
				Element xpddlMetric =
					(Element)ItSIMPLE.getCommonData().getChild("xpddlNodes").getChild("metric").clone();

				xpddlMetric.getChild("optimization").setText(metric.getChildText("intention"));

                                //if it is a expression
                                if (metric.getChildText("type").equals("expression")){
                                    String metricExpression = metric.getChild("expression").getChildText("rule");
                                    boolean totalTime = false;
                                    if(metricExpression.indexOf("total-time") >= 0){
					metricExpression = metricExpression.replaceAll("total-time", TIME_METRIC_REPLACEMENT);
					totalTime = true;
                                    }
                                    ExpressionTreeBuilder builder = new ExpressionTreeBuilder(metricExpression);
                                    Element expTree = builder.getExpressionTree();
                                    //Element condition = buildCondition(expTree, null, null, null);
                                    xpddlMetric.getChild("expression").addContent(buildCondition(expTree, null, null, null));
                                    xpddlProblem.addContent(xpddlMetric);

                                }
                                // if it is a variable
                                else{

                                    Element variable = metric.getChild("variable").getChild("chosenVariable");
                                    if (variable.getAttributeValue("type").equals("attr")){
                                        //get the selected object
                                        Element chosenObject = null;
                                        try {
                                                XPath path = new JDOMXPath("elements/objects/object[@id='"+ variable.getChild("object").getAttributeValue("id") +"' and class='"+variable.getChild("object").getAttributeValue("class")+"']");
                                                chosenObject = (Element)path.selectSingleNode(domain);

                                        } catch (JaxenException e1) {
                                                e1.printStackTrace();
                                        }

                                        //get the class of the object
                                        Element chosenObjectClass = null;
                                        try {
                                                XPath path = new JDOMXPath("project/elements/classes/class[@id='"+variable.getChild("object").getAttributeValue("class")+"']");
                                                chosenObjectClass = (Element)path.selectSingleNode(domain.getDocument());

                                        } catch (JaxenException e1) {
                                                e1.printStackTrace();
                                        }
                                        
                                        //get the selected attribute
                                        Element chosenAttribute = null;
                                        try {
                                                XPath path = new JDOMXPath("project/elements/classes/class[@id='"+variable.getChild("object").getChild("attribute").getAttributeValue("class")+"']/attributes/attribute[@id='"+ variable.getChild("object").getChild("attribute").getAttributeValue("id") +"']");
                                                chosenAttribute = (Element)path.selectSingleNode(domain.getDocument());

                                        } catch (JaxenException e1) {
                                                e1.printStackTrace();
                                        }

                                        //If both elements were found
                                        if (chosenObject != null && chosenObjectClass != null && chosenAttribute != null ){
                                            String metricExpression = "";
                                            //Check if the object is from a utility class
                                            if (!chosenObjectClass.getChildText("stereotype").equals("utility")){
                                                metricExpression = chosenObject.getChildText("name") + ".";
                                            }
                                            metricExpression += chosenAttribute.getChildText("name");
                                            ExpressionTreeBuilder builder = new ExpressionTreeBuilder(metricExpression);
                                            Element expTree = builder.getExpressionTree();
                                            //Element condition = buildCondition(expTree, null, null, null);
                                            xpddlMetric.getChild("expression").addContent(buildCondition(expTree, null, null, null));
                                            xpddlProblem.addContent(xpddlMetric);
                                        }
                                    }


                                }

				
			}*/

                        List<String> metricExpressionMinimize = new ArrayList();
                        List<String> metricExpressionMaximize = new ArrayList();
                        List<String> metricWeightMinimize = new ArrayList();
                        List<String> metricWeightMaximize = new ArrayList();
                        List<Element> metrics = null;
                        //This process selects only the expression and variable type of metric (actionCounter is not considered yet)
			try {
				XPath path = new JDOMXPath("metrics/qualityMetric[enabled='true' and (intention='minimize' or intention='maximize')]");
				metrics = path.selectNodes(problem);
			} catch (JaxenException e) {
				e.printStackTrace();
			}
                        //Get metrics from domain
                        List<Element> domainmetrics = null;
 			try {
				XPath path = new JDOMXPath("metrics/qualityMetric[enabled='true' and (intention='minimize' or intention='maximize')]");
                                domainmetrics = path.selectNodes(problem.getParentElement().getParentElement());

			} catch (JaxenException e) {
				e.printStackTrace();
			}

                        metrics.addAll(domainmetrics);

                       
                        if (metrics != null && metrics.size() > 0){
                            for (Iterator<Element> iter = metrics.iterator(); iter.hasNext();) {
                                Element metric = iter.next();
                                String metricExpression = "";

                                //xpddlMetric.getChild("optimization").setText(metric.getChildText("intention"));

                                //if it is a expression
                                if (metric.getChildText("type").equals("expression")){
                                    metricExpression = metric.getChild("expression").getChildText("rule");
                                    boolean totalTime = false;
                                    if(metricExpression.indexOf("total-time") >= 0){
                                        metricExpression = metricExpression.replaceAll("total-time", TIME_METRIC_REPLACEMENT);
                                        totalTime = true;
                                    }
                                    
                                   
                                    //ExpressionTreeBuilder builder = new ExpressionTreeBuilder(metricExpression);
                                    //Element expTree = builder.getExpressionTree();
                                    ////Element condition = buildCondition(expTree, null, null, null);
                                    //xpddlMetric.getChild("expression").addContent(buildCondition(expTree, null, null, null));
                                    //xpddlProblem.addContent(xpddlMetric);

                                }
                                // if it is a variable
                                else if (metric.getChildText("type").equals("variable")){

                                    Element variable = metric.getChild("variable").getChild("chosenVariable");
                                    if (variable.getAttributeValue("type").equals("attr")){
                                        //get the selected object
                                        Element chosenObject = null;
                                        try {
                                                XPath path = new JDOMXPath("elements/objects/object[@id='"+ variable.getChild("object").getAttributeValue("id") +"' and class='"+variable.getChild("object").getAttributeValue("class")+"']");
                                                chosenObject = (Element)path.selectSingleNode(domain);

                                        } catch (JaxenException e1) {
                                                e1.printStackTrace();
                                        }

                                        //get the class of the object
                                        Element chosenObjectClass = null;
                                        try {
                                                XPath path = new JDOMXPath("project/elements/classes/class[@id='"+variable.getChild("object").getAttributeValue("class")+"']");
                                                chosenObjectClass = (Element)path.selectSingleNode(domain.getDocument());

                                        } catch (JaxenException e1) {
                                                e1.printStackTrace();
                                        }

                                        //get the selected attribute
                                        Element chosenAttribute = null;
                                        try {
                                                XPath path = new JDOMXPath("project/elements/classes/class[@id='"+variable.getChild("object").getChild("attribute").getAttributeValue("class")+"']/attributes/attribute[@id='"+ variable.getChild("object").getChild("attribute").getAttributeValue("id") +"']");
                                                chosenAttribute = (Element)path.selectSingleNode(domain.getDocument());

                                        } catch (JaxenException e1) {
                                                e1.printStackTrace();
                                        }

                                        //If both elements were found
                                        if (chosenObject != null && chosenObjectClass != null && chosenAttribute != null ){
                                            //Check if the object is from a utility class
                                            if (!chosenObjectClass.getChildText("stereotype").equals("utility")){
                                                metricExpression = chosenObject.getChildText("name") + ".";
                                            }
                                            metricExpression += chosenAttribute.getChildText("name");
                                            //ExpressionTreeBuilder builder = new ExpressionTreeBuilder(metricExpression);
                                            //Element expTree = builder.getExpressionTree();
                                            //Element condition = buildCondition(expTree, null, null, null);
                                            //xpddlMetric.getChild("expression").addContent(buildCondition(expTree, null, null, null));
                                            //xpddlProblem.addContent(xpddlMetric);
                                        }
                                    }

                                }
                                
                                                               
                                //Check if it is to minimize of maximize and gather the weights
                                if (metric.getChildText("intention").equals("minimize")){
                                    metricExpressionMinimize.add(metricExpression); 
                                    metricWeightMinimize.add(metric.getChildText("weight"));
                                }
                                else if (metric.getChildText("intention").equals("maximize")){
                                    metricExpressionMaximize.add(metricExpression);
                                    metricWeightMaximize.add(metric.getChildText("weight"));                                    
                                }

                            }
                            
                            
                            String finalMetricExpression = "";
                            String intention = "";

                            if (metricExpressionMinimize.size() > 0){
                                intention = "minimize";
                                //if it has just one component (expression)
                                if (metricExpressionMinimize.size() == 1){
                                    finalMetricExpression = metricExpressionMinimize.get(0);
                                }
                                //if it has more components
                                else{
                                    for (int i = 0; i < metricExpressionMinimize.size(); i++) {
                                        String element = metricExpressionMinimize.get(i);
                                        String weight = metricWeightMinimize.get(i);

                                        if (weight.trim().equals("") || weight.trim().equals("1")){
                                            weight = "1";
                                            finalMetricExpression += element;
                                        }
                                        else{
                                            finalMetricExpression += "(" + element + ")*" + weight;
                                        }

                                        if (i < metricExpressionMinimize.size()-1){
                                            finalMetricExpression += " + ";
                                        }
                                    }
                                }
                            }
                            else if (metricExpressionMaximize.size() > 0){
                                intention = "maximize";
                                //if it has just one component (expression)
                                if (metricExpressionMaximize.size() == 1){
                                    finalMetricExpression = metricExpressionMaximize.get(0);
                                }
                                //if it has more components
                                else{
                                    for (int i = 0; i < metricExpressionMaximize.size(); i++) {
                                        String element = metricExpressionMaximize.get(i);
                                        String weight = metricWeightMaximize.get(i);

                                        if (weight.trim().equals("") || weight.trim().equals("1")){
                                            weight = "1";
                                            finalMetricExpression += element;
                                        }
                                        else{
                                            finalMetricExpression += "(" + element + ")*" + weight;
                                        }

                                        if (i < metricExpressionMaximize.size()-1){
                                            finalMetricExpression += " + ";
                                        }
                                    }
                                }
                            }
                            
                            //System.out.println(finalMetricExpression);
                            //set the metric node
                            Element xpddlMetric = (Element)ItSIMPLE.getCommonData().getChild("xpddlNodes").getChild("metric").clone();
                            xpddlMetric.getChild("optimization").setText(intention);
                            ExpressionTreeBuilder builder = new ExpressionTreeBuilder(finalMetricExpression);
                            Element expTree = builder.getExpressionTree();
                            //Element condition = buildCondition(expTree, null, null, null);
                            xpddlMetric.getChild("expression").addContent(buildCondition(expTree, null, null, null));
                            xpddlProblem.addContent(xpddlMetric);  
                        }



		}
		//XMLUtilities.printXML(xpddlProblem);
		return xpddlProblem;
	}
	
	
	/**
	 * Used to parse object diagrams from the XML model to the XPDDL language 
	 * @param objectDiagram the object diagram to be parsed
	 * @param containerXPDDLNode the xpddl node where the result is to be put
	 */
	private static void parseObjectDiagram(Element objectDiagram, Element containerXPDDLNode, String pddlVersion){
		Element domain;
		//Element project;
		if(objectDiagram.getName().equals("repositoryDiagram")){
			domain = objectDiagram.getParentElement().getParentElement();
		}
		else{
			domain = objectDiagram.getParentElement().getParentElement().getParentElement().getParentElement();
		}		
		Element project = domain.getParentElement().getParentElement().getParentElement();
		
		if(containerXPDDLNode != null){
			// 4.1 Predicates and Functions			
			//4.1.2 object associations
			List<?> objectAssociations = objectDiagram.getChild("associations").getChildren("objectAssociation");
			for (Iterator<?> iterator = objectAssociations.iterator(); iterator.hasNext();) {
				Element objectAssociation = (Element) iterator.next();
				// get the corresponding class association
				Element classAssociation = null;
				try {				
					XPath path = new JDOMXPath("elements/classAssociations/classAssociation[@id='"+
							objectAssociation.getChildText("classAssociation") +"']");
					classAssociation = (Element)path.selectSingleNode(project);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(classAssociation != null){
					// get the association ends
					Element sourceEnd = (Element)classAssociation.getChild("associationEnds").getChildren("associationEnd").get(0);
					Element targetEnd = (Element)classAssociation.getChild("associationEnds").getChildren("associationEnd").get(1);
					Element sourceObjectEnd = null;
					Element targetObjectEnd = null;
					try {
						XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='"+ sourceEnd.getAttributeValue("id") +"']");
						sourceObjectEnd = (Element)path.selectSingleNode(objectAssociation);
						path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='"+ targetEnd.getAttributeValue("id") +"']");
						targetObjectEnd = (Element)path.selectSingleNode(objectAssociation);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					//finding the objetcs
					Element sourceObject = null;
					Element targetObject = null;					
					try {
						XPath path = new JDOMXPath("elements/objects/object[@id='"+ sourceObjectEnd.getAttributeValue("element-id") +"']");
						//sourceObject = (Element)path.selectSingleNode(problem);
						sourceObject = (Element)path.selectSingleNode(domain);
						path = new JDOMXPath("elements/objects/object[@id='"+ targetObjectEnd.getAttributeValue("element-id") +"']");
						//targetObject = (Element)path.selectSingleNode(problem);
						targetObject = (Element)path.selectSingleNode(domain);								
					} catch (JaxenException e) {			
						e.printStackTrace();
					}							
					if(sourceObject != null && targetObject != null){						
						if(!targetEnd.getChild("rolename").getChildText("value").trim().equals("")){
                                                    if (!pddlVersion.equals(PDDL_3_1)){
							 Element predicate = new Element("predicate");
							 predicate.setAttribute("id", targetEnd.getChild("rolename").getChildText("value"));
							 
							 Element firstValue = new Element("object");
							 firstValue.setAttribute("id", sourceObject.getChildText("name"));
							 predicate.addContent(firstValue);
							 
							 Element secondValue = new Element("object");
							 secondValue.setAttribute("id", targetObject.getChildText("name"));
							 predicate.addContent(secondValue);
							 
							 // checks if it's a repository diagram first, because it doesn't have a sequence reference
							 if(!objectDiagram.getName().equals("repositoryDiagram") &&
									 objectDiagram.getChildText("sequenceReference").equals("init")){								 
                                                             if(!isDuplicated(predicate, containerXPDDLNode)){
                                                                     containerXPDDLNode.addContent(predicate);
                                                             }
							 }
							 else{
                                                            containerXPDDLNode.addContent(predicate);
							 }
                                                    }
                                                    else{//if it is a pddl3.1 case

                                                        boolean isMultipliticy1or01 = false;
                                                        String xpddlTag = "predicate";
                                                        if (targetEnd.getChild("multiplicity").getChildText("value").equals("1") || targetEnd.getChild("multiplicity").getChildText("value").equals("0..1")){
                                                            isMultipliticy1or01 = true;
                                                            xpddlTag = "function";
                                                        }

                                                        Element predfunc = new Element(xpddlTag);
                                                        predfunc.setAttribute("id", targetEnd.getChild("rolename").getChildText("value"));

                                                        Element firstValue = new Element("object");
                                                        firstValue.setAttribute("id", sourceObject.getChildText("name"));
                                                        predfunc.addContent(firstValue);

                                                        if (!isMultipliticy1or01){
                                                            Element secondValue = new Element("object");
                                                            secondValue.setAttribute("id", targetObject.getChildText("name"));
                                                            predfunc.addContent(secondValue);
                                                            // checks if it's a repository diagram first, because it doesn't have a sequence reference
                                                            if(!objectDiagram.getName().equals("repositoryDiagram") &&
                                                                            objectDiagram.getChildText("sequenceReference").equals("init")){
                                                                if(!isDuplicated(predfunc, containerXPDDLNode)){
                                                                        containerXPDDLNode.addContent(predfunc);
                                                                }
                                                            }
                                                            else{
                                                                containerXPDDLNode.addContent(predfunc);
                                                            }
                                                        }
                                                        else{//it is a object equals
                                                            Element equals = new Element("equals");
                                                            equals.addContent(predfunc);
                                                            Element value = new Element("value");
                                                            value.setAttribute("object", targetObject.getChildText("name"));
                                                            equals.addContent(value);
                                                            if(!objectDiagram.getName().equals("repositoryDiagram") &&
                                                                            objectDiagram.getChildText("sequenceReference").equals("init")){
                                                                if(!isDuplicated(equals, containerXPDDLNode)){
                                                                        containerXPDDLNode.addContent(equals);
                                                                }
                                                            }
                                                            else{
                                                                containerXPDDLNode.addContent(equals);
                                                            }
                                                        }

                                                    }

						}
						if(!sourceEnd.getChild("rolename").getChildText("value").trim().equals("")){
                                                    if (!pddlVersion.equals(PDDL_3_1)){
							 Element predicate = new Element("predicate");
							 predicate.setAttribute("id", sourceEnd.getChild("rolename").getChildText("value"));
							 
							 Element firstValue = new Element("object");
							 firstValue.setAttribute("id", targetObject.getChildText("name"));
							 predicate.addContent(firstValue);
							 
							 Element secondValue = new Element("object");
							 secondValue.setAttribute("id", sourceObject.getChildText("name"));
							 predicate.addContent(secondValue);
							 
							 // checks if it's a repository diagram first, because it doesn't have a sequence reference
							 if(!objectDiagram.getName().equals("repositoryDiagram") &&
									 objectDiagram.getChildText("sequenceReference").equals("init")){								 
                                                             if(!isDuplicated(predicate, containerXPDDLNode)){
                                                                     containerXPDDLNode.addContent(predicate);
                                                             }
							 }
							 else{
                                                            containerXPDDLNode.addContent(predicate);
							 }
                                                    }
                                                    else{//if it is a pddl3.1 case
                                                        boolean isMultipliticy1or01 = false;
                                                        String xpddlTag = "predicate";
                                                        if (sourceEnd.getChild("multiplicity").getChildText("value").equals("1") || sourceEnd.getChild("multiplicity").getChildText("value").equals("0..1")){
                                                            isMultipliticy1or01 = true;
                                                            xpddlTag = "function";
                                                        }

                                                        Element predfunc = new Element(xpddlTag);
                                                        predfunc.setAttribute("id", sourceEnd.getChild("rolename").getChildText("value"));

                                                        Element firstValue = new Element("object");
                                                        firstValue.setAttribute("id", targetObject.getChildText("name"));
                                                        predfunc.addContent(firstValue);

                                                        if (!isMultipliticy1or01){
                                                            Element secondValue = new Element("object");
                                                            secondValue.setAttribute("id", sourceObject.getChildText("name"));
                                                            predfunc.addContent(secondValue);
                                                            // checks if it's a repository diagram first, because it doesn't have a sequence reference
                                                            if(!objectDiagram.getName().equals("repositoryDiagram") &&
                                                                            objectDiagram.getChildText("sequenceReference").equals("init")){
                                                                if(!isDuplicated(predfunc, containerXPDDLNode)){
                                                                        containerXPDDLNode.addContent(predfunc);
                                                                }
                                                            }
                                                            else{
                                                                containerXPDDLNode.addContent(predfunc);
                                                            }
                                                        }
                                                        else{//it is a object equals
                                                            Element equals = new Element("equals");
                                                            equals.addContent(predfunc);
                                                            Element value = new Element("value");
                                                            value.setAttribute("object", sourceObject.getChildText("name"));
                                                            equals.addContent(value);
                                                            if(!objectDiagram.getName().equals("repositoryDiagram") &&
                                                                            objectDiagram.getChildText("sequenceReference").equals("init")){
                                                                if(!isDuplicated(equals, containerXPDDLNode)){
                                                                        containerXPDDLNode.addContent(equals);
                                                                }
                                                            }
                                                            else{
                                                                containerXPDDLNode.addContent(equals);
                                                            }
                                                        }

                                                    }
						}
						if(sourceEnd.getChild("rolename").getChildText("value").trim().equals("") && 
								targetEnd.getChild("rolename").getChildText("value").trim().equals("")){
							// 4.1.2 Associations without rolenames							
							boolean sourceHasNavigation = Boolean.parseBoolean(sourceEnd.getAttributeValue("navigation"));
							boolean targetHasNavigation = Boolean.parseBoolean(targetEnd.getAttributeValue("navigation"));					
							// 4.1.2.1 Double navigation or without navigation: the order of the parameters is not important
							if((sourceHasNavigation && targetHasNavigation) ||
									(!sourceHasNavigation && !targetHasNavigation)){
                                                            if (!pddlVersion.equals(PDDL_3_1)){
								// create first flow predicate
								Element firstPredicate = new Element("predicate");
								firstPredicate.setAttribute("id", classAssociation.getChildText("name"));
								
								Element firstValue = new Element("object");
								firstValue.setAttribute("id", targetObject.getChildText("name"));
								firstPredicate.addContent(firstValue);
								
								Element secondValue = new Element("object");
								secondValue.setAttribute("id", sourceObject.getChildText("name"));										
								firstPredicate.addContent(secondValue);
								
								// create reverse flow predicate
								Element secondPredicate = new Element("predicate");
								secondPredicate.setAttribute("id", classAssociation.getChildText("name"));								
								secondPredicate.addContent((Element)secondValue.clone());
							
								secondPredicate.addContent((Element)firstValue.clone());
								
								 if(!objectDiagram.getName().equals("repositoryDiagram") &&
										 objectDiagram.getChildText("sequenceReference").equals("init")){
                                                                     // look for the same predicate in the xpddl file
                                                                     if(!isDuplicated(firstPredicate, containerXPDDLNode)){
                                                                             containerXPDDLNode.addContent(firstPredicate);
                                                                     }
                                                                     if(!isDuplicated(secondPredicate, containerXPDDLNode)){
                                                                             containerXPDDLNode.addContent(secondPredicate);
                                                                     }
										
								 }
								 else{
                                                                    containerXPDDLNode.addContent(firstPredicate);
                                                                    containerXPDDLNode.addContent(secondPredicate);
								 }
                                                            }
                                                            else{//pddl3.1 case
                                                                boolean sourceisMultipliticy1or01 = false;
                                                                boolean targetisMultipliticy1or01 = false;
                                                                if (sourceEnd.getChild("multiplicity").getChildText("value").equals("1") || sourceEnd.getChild("multiplicity").getChildText("value").equals("0..1")){
                                                                    sourceisMultipliticy1or01 = true;
                                                                }
                                                                if (targetEnd.getChild("multiplicity").getChildText("value").equals("1") || targetEnd.getChild("multiplicity").getChildText("value").equals("0..1")){
                                                                    targetisMultipliticy1or01 = true;
                                                                }

                                                                if (targetisMultipliticy1or01 && sourceisMultipliticy1or01){
                                                                    //first
                                                                    Element firstEquals = new Element("equals");
                                                                    Element firstPredFuc = new Element("function");
                                                                    firstPredFuc.setAttribute("id", classAssociation.getChildText("name"));

                                                                    Element firstValue = new Element("object");
                                                                    firstValue.setAttribute("id", targetObject.getChildText("name"));
                                                                    firstPredFuc.addContent(firstValue);

                                                                    firstEquals.addContent(firstPredFuc);

                                                                    Element fValue = new Element("value");
                                                                    fValue.setAttribute("object", sourceObject.getChildText("name"));
                                                                    firstEquals.addContent(fValue);

                                                                    //second
                                                                    Element secondEquals = new Element("equals");
                                                                    Element secondPredFuc = new Element("function");
                                                                    secondPredFuc.setAttribute("id", classAssociation.getChildText("name"));

                                                                    Element firstSValue = new Element("object");
                                                                    firstSValue.setAttribute("id", sourceObject.getChildText("name"));
                                                                    secondPredFuc.addContent(firstSValue);

                                                                    secondEquals.addContent(secondPredFuc);

                                                                    Element sValue = new Element("value");
                                                                    sValue.setAttribute("object", targetObject.getChildText("name"));
                                                                    secondEquals.addContent(sValue);
                                                                    if(!objectDiagram.getName().equals("repositoryDiagram") &&
                                                                                     objectDiagram.getChildText("sequenceReference").equals("init")){
                                                                         // look for the same predicate in the xpddl file
                                                                         if(!isDuplicated(firstEquals, containerXPDDLNode)){
                                                                                 containerXPDDLNode.addContent(firstEquals);
                                                                         }
                                                                         if(!isDuplicated(secondEquals, containerXPDDLNode)){
                                                                                 containerXPDDLNode.addContent(secondEquals);
                                                                         }

                                                                    }
                                                                    else{
                                                                        containerXPDDLNode.addContent(firstEquals);
                                                                        containerXPDDLNode.addContent(secondEquals);
                                                                    }

                                                                }else{//treat as predicate
                                                                    Element firstPredicate = new Element("predicate");
                                                                    firstPredicate.setAttribute("id", classAssociation.getChildText("name"));

                                                                    Element firstValue = new Element("object");
                                                                    firstValue.setAttribute("id", targetObject.getChildText("name"));
                                                                    firstPredicate.addContent(firstValue);

                                                                    Element secondValue = new Element("object");
                                                                    secondValue.setAttribute("id", sourceObject.getChildText("name"));
                                                                    firstPredicate.addContent(secondValue);

                                                                    // create reverse flow predicate
                                                                    Element secondPredicate = new Element("predicate");
                                                                    secondPredicate.setAttribute("id", classAssociation.getChildText("name"));
                                                                    secondPredicate.addContent((Element)secondValue.clone());

                                                                    secondPredicate.addContent((Element)firstValue.clone());

                                                                     if(!objectDiagram.getName().equals("repositoryDiagram") &&
                                                                                     objectDiagram.getChildText("sequenceReference").equals("init")){
                                                                             // look for the same predicate in the xpddl file
                                                                             if(!isDuplicated(firstPredicate, containerXPDDLNode)){
                                                                                     containerXPDDLNode.addContent(firstPredicate);
                                                                             }
                                                                             if(!isDuplicated(secondPredicate, containerXPDDLNode)){
                                                                                     containerXPDDLNode.addContent(secondPredicate);
                                                                             }

                                                                     }
                                                                     else{
                                                                                    containerXPDDLNode.addContent(firstPredicate);
                                                                                    containerXPDDLNode.addContent(secondPredicate);
                                                                     }

                                                                }

                                                            }
							}
							else{// 4.1.2.2 Single navigation: the order is set by the navigation
                                                            //if (!pddlVersion.equals(PDDL_3_1)){
								Element associationSource = (sourceHasNavigation) ?sourceObject :targetObject;
								Element associationTarget = (sourceHasNavigation) ?targetObject :sourceObject;

        							Element predicate = new Element("predicate");
								predicate.setAttribute("id", classAssociation.getChildText("name"));
								Element firstValue = new Element("object");
								firstValue.setAttribute("id", associationTarget.getChildText("name"));
								
								predicate.addContent(firstValue);
								
								Element secondValue = new Element("object");										
								secondValue.setAttribute("id", associationSource.getChildText("name"));
								predicate.addContent(secondValue);
								
								 if(!objectDiagram.getName().equals("repositoryDiagram") &&
										 objectDiagram.getChildText("sequenceReference").equals("init")){								 
									 if(!isDuplicated(predicate, containerXPDDLNode)){
										 containerXPDDLNode.addContent(predicate); 
									 }
								 }
								 else{
									 containerXPDDLNode.addContent(predicate);
								 }
                                                            //}
							}									
						}
						
					}
				}
			}
			
			// 4.1.3 object attributes
			List<?> refObjects = objectDiagram.getChild("objects").getChildren("object");
			for (Iterator<?> iterator = refObjects.iterator(); iterator.hasNext();) {
				// get all object references in the diagram
				Element refObject = (Element) iterator.next();
				// get the corresponding object
				Element object = null;
				try {
					XPath path = new JDOMXPath("elements/objects/object[@id='"+ refObject.getAttributeValue("id") +"']");
					//object = (Element)path.selectSingleNode(problem);
					object = (Element)path.selectSingleNode(domain);							
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(object != null){
					// get the object attributes
					List<?> objectAttributes = refObject.getChild("attributes").getChildren("attribute");
					
					for (Iterator<?> attrIterator = objectAttributes.iterator(); attrIterator.hasNext();) {
						Element objectAttribute = (Element) attrIterator.next();
						// get the corresponding class attribute
						
						Element classAttribute = null;
						try {
							XPath path = new JDOMXPath("elements/classes/class[@id='"+ objectAttribute.getAttributeValue("class") +
									"']/attributes/attribute[@id='"+ objectAttribute.getAttributeValue("id") +"']");
							classAttribute = (Element)path.selectSingleNode(project);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}								

                                                //if(classAttribute != null){ //including string
						if(classAttribute != null && !classAttribute.getChildText("type").equals("4")){ //excluding the String case
							
							Element predOrFunc = new Element("predicate");// tag name will be changed later if function
							predOrFunc.setAttribute("id", classAttribute.getChildText("name"));									
							// this list will be used to add predicates or functions
							// in the parameterized attribute case
							List<Element> predOrFuncList = new ArrayList<Element>();
							
							// argument 0
																// attributes			class
							Element objectClass = classAttribute.getParentElement().getParentElement();
							if(!objectClass.getChildText("stereotype").equals("utility")){
								Element firstParameter = new Element("object");
								firstParameter.setAttribute("id", object.getChildText("name"));
								predOrFunc.addContent(firstParameter);
							}
							
							// parameterized attribute
							if(objectAttribute.getChild("value").getChildren().size() > 0){
								
								List<?> parameterizedValues = objectAttribute.getChild("value").getChildren("parameterizedValue");
								
								for (Iterator<?> valueIter = parameterizedValues.iterator(); valueIter.hasNext();) {											
									Element parameterizedValue = (Element) valueIter.next();
									if(!parameterizedValue.getChildText("value").trim().equals("")){
										List<?> parameters = parameterizedValue.getChild("parameters").getChildren("parameter");
										Element predOrFuncClone = (Element)predOrFunc.clone();
										for (Iterator<?> paramIter = parameters.iterator(); paramIter.hasNext();) {
											Element currentParameter = (Element) paramIter.next();
											Element parameter = new Element("object");
											parameter.setAttribute("id", currentParameter.getChildText("value"));
											predOrFuncClone.addContent(parameter);
										}											
										// boolean attribute -> predicate
										if(classAttribute.getChildText("type").equals("1")){
											if(parameterizedValue.getChildText("value").equals("true")){
												predOrFuncList.add(predOrFuncClone);
											}
											else if(!objectDiagram.getChildText("sequenceReference").equals("init") &&
													parameterizedValue.getChildText("value").equals("false")){
													// deny the predicate
												Element not = new Element("not");
												not.addContent(predOrFuncClone);
												predOrFuncList.add(not);
											}
										}
										// numeric attribute -> function
										else if(classAttribute.getChildText("type").equals("2") ||
												classAttribute.getChildText("type").equals("3")){
											predOrFuncClone.setName("function");
											Element equals = new Element("equals");
											equals.addContent(predOrFuncClone);
											Element value = new Element("value");
											value.setAttribute("number", parameterizedValue.getChildText("value"));
											equals.addContent(value);
											
											predOrFuncList.add(equals);
										}											
										// non primitive attributes or string
										else {
                                                                                    //If it is pddl 3.1 (function)
                                                                                    if (pddlVersion.equals(PDDL_3_1)){
                                                                                        predOrFuncClone.setName("function");
                                                                                        Element equals = new Element("equals");
                                                                                        equals.addContent(predOrFuncClone);
                                                                                        Element value = new Element("value");
                                                                                        value.setAttribute("object", parameterizedValue.getChildText("value"));
                                                                                        equals.addContent(value);
                                                                                        predOrFuncList.add(equals);
                                                                                    }
                                                                                    else{

                                                                                        Element parameter = new Element("object");
											parameter.setAttribute("id", parameterizedValue.getChildText("value"));
											predOrFuncClone.addContent(parameter);
											predOrFuncList.add(predOrFuncClone);
                                                                                    }
										}
										
									}																						
								}
								// add each element of the list to the xpddl problem
								for (Iterator<Element> predOrFuncIter = predOrFuncList.iterator(); predOrFuncIter.hasNext();) {
									Element currentPredOrFunc = predOrFuncIter.next();
									 if(!objectDiagram.getName().equals("repositoryDiagram") &&
											 objectDiagram.getChildText("sequenceReference").equals("init")){								 
										 if(!isDuplicated(currentPredOrFunc, containerXPDDLNode)){
											 containerXPDDLNode.addContent(currentPredOrFunc); 
										 }
									 }
									 else{
										 containerXPDDLNode.addContent(currentPredOrFunc);
									 }
								}
							}
                                                        // not parameterized attribute TODO
							else{
								
								if(!objectAttribute.getChildText("value").trim().equals("")){
									// 4.1.3.1 boolean attributes -> predicate
									if(classAttribute.getChildText("type").equals("1")){

										if(objectAttribute.getChildText("value").equals("true")){
											 if(!objectDiagram.getName().equals("repositoryDiagram") &&
													 objectDiagram.getChildText("sequenceReference").equals("init")){								 
												 if(!isDuplicated(predOrFunc, containerXPDDLNode)){
													 containerXPDDLNode.addContent(predOrFunc); 
												 }
											 }
											 else{
												 containerXPDDLNode.addContent(predOrFunc);
											 }
										}
										else if(!objectDiagram.getChildText("sequenceReference").equals("init") &&
												objectAttribute.getChildText("value").equals("false")){
												// deny the predicate
											Element not = new Element("not");
											not.addContent(predOrFunc);
											containerXPDDLNode.addContent(not);
										}
									}
									//4.1.3.2 numeric attributes -> function
									else if(classAttribute.getChildText("type").equals("2") ||
											classAttribute.getChildText("type").equals("3")){
										predOrFunc.setName("function");
										Element equals = new Element("equals");
										equals.addContent(predOrFunc);
										
										Element value = new Element("value");
										value.setAttribute("number", objectAttribute.getChildText("value"));
										equals.addContent(value);
										
										 if(!objectDiagram.getName().equals("repositoryDiagram") &&
												 objectDiagram.getChildText("sequenceReference").equals("init")){								 
											 if(!isDuplicated(equals, containerXPDDLNode)){
												 containerXPDDLNode.addContent(equals); 
											 }
										 }
										 else{
											 containerXPDDLNode.addContent(equals);
										 }
									}
									// 4.1.3.3 string and non primitive attributes -> predicate
									else{
										//If it is pddl 3.1
                                                                                if (pddlVersion.equals(PDDL_3_1)){
                                                                                    predOrFunc.setName("function");
                                                                                    Element equals = new Element("equals");
                                                                                    equals.addContent(predOrFunc);
                                                                                    Element value = new Element("value");
                                                                                    value.setAttribute("object", objectAttribute.getChildText("value"));
                                                                                    equals.addContent(value);
                                                                                     if(!objectDiagram.getName().equals("repositoryDiagram") &&
                                                                                                     objectDiagram.getChildText("sequenceReference").equals("init")){
                                                                                             if(!isDuplicated(equals, containerXPDDLNode)){
                                                                                                     containerXPDDLNode.addContent(equals);
                                                                                             }
                                                                                     }
                                                                                     else{
                                                                                             containerXPDDLNode.addContent(equals);
                                                                                     }
   
                                                                                }
                                                                                else{
                                                                                    Element parameter = new Element("object");
                                                                                    parameter.setAttribute("id", objectAttribute.getChildText("value"));
                                                                                    predOrFunc.addContent(parameter);
                                                                                    if(!objectDiagram.getName().equals("repositoryDiagram") &&
                                                                                                     objectDiagram.getChildText("sequenceReference").equals("init")){
                                                                                             if(!isDuplicated(predOrFunc, containerXPDDLNode)){
                                                                                                     containerXPDDLNode.addContent(predOrFunc);
                                                                                             }
                                                                                     }
                                                                                     else{
                                                                                             containerXPDDLNode.addContent(predOrFunc);
                                                                                     }

                                                                                }									}
								}
							}
						}
					}
				}
			}

                        //4.1.4 Contraints from Object diagrams (local constraints that will be added as conditions)
                        Element localConstraints = objectDiagram.getChild("constraints");
                        if (localConstraints != null && !localConstraints.getText().trim().equals("")){
                            String constraints = localConstraints.getText().trim();
                            //System.out.println(constraints);
                            ExpressionTreeBuilder builder = new ExpressionTreeBuilder(constraints);
                            Element expTree = builder.getExpressionTree();
                            //Element condition = buildCondition(expTree, null, null, null);
                            Element condition = buildCondition(expTree, null, null, PRECONDITION);
                            //If the container is (and ... and the conditions starts with (and ... to
                            //we can insert the content of the contition directly to the container
                            if (containerXPDDLNode.getName().equals(condition.getName())){
                                //System.out.print("Yes");
                                for (Iterator<Element> itc = condition.getChildren().iterator(); itc.hasNext();) {
                                    Element object = (Element)itc.next();
                                    containerXPDDLNode.addContent((Element)object.clone());
                                }
                            }else{
                                containerXPDDLNode.addContent(condition);
                            }
                            //XMLUtilities.printXML(condition);
                        }

                        

                        if(containerXPDDLNode.getChildren().size() > 0){
				if(!objectDiagram.getName().equals("repositoryDiagram")){
                                    //Goal
                                    if(objectDiagram.getChildText("sequenceReference").equals("goal")){
						if(containerXPDDLNode.getChildren().size() == 1){
							Element singleNode = (Element)containerXPDDLNode.removeContent(0);
							xpddlProblem.getChild("goal").addContent(singleNode);
						}
						else{
							xpddlProblem.getChild("goal").addContent(containerXPDDLNode);
						}						
                                    }

                                    //Constraints or Timed literal
                                    else if(!objectDiagram.getChildText("sequenceReference").equals("init")){
                                        String seq = objectDiagram.getChildText("sequenceReference");
                                        boolean isTimedLiteral = true;
                                        try{
                                            float x = Float.parseFloat(seq);
                                        }catch(NumberFormatException nFE) {
                                            //System.out.println("Not an Integer");
                                            isTimedLiteral = false;
                                        }
                                        //timed inital literal
                                        if (isTimedLiteral){
                                            //XMLUtilities.printXML(containerXPDDLNode);
                                            for (Iterator<?> iter = containerXPDDLNode.getChildren().iterator(); iter.hasNext();) {
                                                Element theliteral = (Element) iter.next();
                                                Element at = new Element("at");
                                                Element literal = new Element("literal");
                                                Element timespicifier = new Element("timespecifier");
                                                timespicifier.setAttribute("number", seq);
                                                at.addContent(literal);
                                                at.addContent(timespicifier);
                                                literal.addContent((Element)theliteral.clone());
                                                //timedGroup.addContent(at);
                                                xpddlProblem.getChild("init").addContent(at);
                                            }

                                        }
                                        //constraint
                                        else{

						// add the values in sometime diagram in the problem constraints
						Element constraint;
						if(objectDiagram.getChildText("sequenceReference").equals("never")){
							// never will be translated as (not (always (...))
							constraint = new Element("always");
						}
						else{
							constraint = new Element(objectDiagram.getChildText("sequenceReference"));
						}

						if(containerXPDDLNode.getChildren().size() == 1){
							Element singleNode = (Element)containerXPDDLNode.removeContent(0);
							constraint.addContent(singleNode);
						}
						else{
							constraint.addContent(containerXPDDLNode);
						}
						
						if(objectDiagram.getChildText("sequenceReference").equals("never")){
							Element not = new Element("not");
							not.addContent(constraint);
							constraint = not;
						}
						
						constraintsList.add(constraint);
                                        }
                                    }
                            }
			}
		}
	}
	
	private static boolean isDuplicated(Element xpddlNode, Element containerXPDDLNode){
		String expression = null;
		//prepares the xpath expression
		
		//predicate
		if(xpddlNode.getName().equals("predicate")){
		    int index = 1;
			expression = "predicate[@id='"+ xpddlNode.getAttributeValue("id")
				+"' and ";
			for (Iterator<?> iter = xpddlNode.getChildren().iterator(); iter.hasNext();) {
				Element parameter = (Element) iter.next();
				expression += "object["+ index +"]/@id='"+ parameter.getAttributeValue("object") +"'";
				if(iter.hasNext()){
					expression += " and ";
				}
				index++;
			}
			expression += "]";
		}
		
		//equals
		if(xpddlNode.getName().equals("equals")){
		    int index = 1;
			expression = "equals[function/@id='"+ xpddlNode.getChild("function").getAttributeValue("id") +"' and ";
			for (Iterator<?> iter = xpddlNode.getChild("function").getChildren().iterator(); iter.hasNext();) {
				Element parameter = (Element) iter.next();
				expression += "function/object["+ index +"]/@id='"+ parameter.getAttributeValue("object") +"' and ";
				index++;
			}
			expression += "value/@number='"+ xpddlNode.getChild("value").getAttributeValue("number") +"']";
			
		}
		
		// do the checking
		Element replicatedPredicate = null;
		try {				
			XPath path = new JDOMXPath(expression);
			replicatedPredicate = (Element)path.selectSingleNode(containerXPDDLNode);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		if(replicatedPredicate == null)
			 return false;		
		else
			return true;
	}

    
    /**
     * This method fix the time index (pddl operators at start, at end and overall) in a given xppdl condition.
     * This a very simplified bersion of the method; it must be worked out to consider all cases and all condition.
     * Right now the method is only considering boolean attributes in the first level (under <and>)
     * @param timingDiagram the timing diagram of the action.
     * @param condition the condition (precondition or postcondition)
     */
    private static void setTimeIndexToConditions(Element timingDiagram, Element operator, Element condition) {

        //This A simplefy version of the
        Element mainNode = null;
        Element newContainer = new Element("newContainer"); //holds the new version of the condition.

        if (condition.getChildren().size()>0){
            Element firstNode = (Element)condition.getChildren().get(0);
            if (condition.getChildren().size() == 1 && (firstNode.getName().equals("and") || firstNode.getName().equals("or")) ){
                mainNode = firstNode;
            }
            else{
                mainNode = condition;
            }

            if (mainNode!=null){

                String durationStr = timingDiagram.getChild("frame").getChildText("duration");

                //go to each node
                for (Iterator<Element> it = mainNode.getChildren().iterator(); it.hasNext();) {
                    Element eaCondition = it.next();
                    Element newCondition = (Element)eaCondition.clone();
                    Element theNode = null;

                    boolean isNegative = false;

                    if (eaCondition.getName().equals("not")){
                        theNode = (Element)eaCondition.getChildren().get(0);
                        isNegative = true;
                    }
                    else{
                        theNode = eaCondition;
                    }

                    boolean changed = false;


                    //System.out.println(eaCondition.getName());

                    if (theNode.getName().equals("predicate") || theNode.getName().equals("function")){

                        //check if it is a attribute. Get it
                        Element predOrFunc = null;
                        try {
                            XPath path = new JDOMXPath("project/elements/classes/class/attributes/attribute[name='"+theNode.getAttributeValue("id")+"']");
                            predOrFunc = (Element)path.selectSingleNode(timingDiagram.getDocument());
                        } catch (JaxenException e) {
                            e.printStackTrace();
                        }

                        if (predOrFunc!=null){
                            //check the type
                            //if it is BOOLEAN
                            if(predOrFunc.getChildText("type").equals("1")){
                                //get the first parameter
                                String parameterName = ((Element)theNode.getChildren().get(0)).getAttributeValue("id");

                                //check if it is a action parameters
                                Element parameter = null;
                                try {
                                    XPath path = new JDOMXPath("parameters/parameter[name='"+parameterName+"']");
                                    parameter = (Element)path.selectSingleNode(operator);
                                } catch (JaxenException e) {
                                    e.printStackTrace();
                                }
                                if(parameter!=null){
                                    //Check if there is a lifeline for this condition
                                    Element lifeline = null;
                                    try {
                                        XPath path = new JDOMXPath("frame/lifelines/lifeline[object/@class='"+parameter.getChildText("type")+
                                                "' and object/@element='parameter' and object/@id='"+parameter.getAttributeValue("id")+
                                                "' and attribute/@class='"+predOrFunc.getParentElement().getParentElement().getAttributeValue("id")+
                                                "' and attribute/@id='"+predOrFunc.getAttributeValue("id")+"']");
                                        lifeline = (Element)path.selectSingleNode(timingDiagram);
                                    } catch (JaxenException e) {
                                        e.printStackTrace();
                                    }
                                    //we found it
                                    if(lifeline!=null){


                                        //check the intervals
                                        for (Iterator<Element> it1 = lifeline.getChild("timeIntervals").getChildren().iterator(); it1.hasNext();) {
                                            Element timeInterval = it1.next();

                                            String valueAtCondition = "true";
                                            if (isNegative){
                                                valueAtCondition = "false";
                                            }
                                            Element value = timeInterval.getChild("value");

                                            //Check if we are comparing true value with true value (and vice versa)
                                            if (value.getText().trim().equals(valueAtCondition)){
                                                Element durationConstratint = timeInterval.getChild("durationConstratint");
                                                Element lowerbound = durationConstratint.getChild("lowerbound");
                                                Element upperbound = durationConstratint.getChild("upperbound");


                                                String lowerboundValue = lowerbound.getAttributeValue("value");
                                                String lowerboundIncluded = lowerbound.getAttributeValue("included");
                                                String upperboundValue = upperbound.getAttributeValue("value");
                                                String upperboundIncluded = upperbound.getAttributeValue("included");



                                                //at start case
                                                if (lowerboundValue.equals("0") && lowerboundIncluded.equals("true")){
                                                    //add at-start node to the container (new condition)
                                                    Element atStart = new Element("at-start");
                                                    atStart.addContent((Element)newCondition.clone());
                                                    newContainer.addContent(atStart);
                                                    changed = true;
                                                }
                                                //at end case
                                                if (upperboundValue.endsWith(durationStr) && upperboundIncluded.equals("true")){
                                                    //add at-end node to the container (new condition)
                                                    Element atEnd = new Element("at-end");
                                                    atEnd.addContent((Element)newCondition.clone());
                                                    newContainer.addContent(atEnd);
                                                    changed = true;
                                                }
                                                //over all case. The overall case is only applyed for precondition (condition) and not for effect
                                                if(condition.getName().equals("condition")){
                                                    
                                                    if((lowerboundValue.equals("0") && lowerboundIncluded.equals("false")
                                                            && upperboundValue.endsWith(durationStr) && upperboundIncluded.equals("false")) //(0,dur)
                                                            ||
                                                            (lowerboundValue.equals("0") && lowerboundIncluded.equals("true")
                                                            && upperboundValue.endsWith(durationStr) && upperboundIncluded.equals("true")) //[0,dur]
                                                            ||
                                                            (lowerboundValue.equals("0") && lowerboundIncluded.equals("true")
                                                            && upperboundValue.endsWith(durationStr) && upperboundIncluded.equals("false")) //[0,dur)
                                                            ||
                                                            (lowerboundValue.equals("0") && lowerboundIncluded.equals("false")
                                                            && upperboundValue.endsWith(durationStr) && upperboundIncluded.equals("true")) //(0,dur]
                                                            ){
                                                        //add over-all node to the container (new condition)
                                                        Element overAll = new Element("over-all");
                                                        overAll.addContent((Element)newCondition.clone());
                                                        newContainer.addContent(overAll);
                                                        changed = true;
                                                    }
                                                }


                                            }

                                        }


                                        

                                    }


                                }
                            }
                            //TODO: else, for the other cases (number, class) and associations

                        }

                        


                    }

                    //if it was nothing changed just put the at-start or at end
                    //if (!newCondition.getName().equals("at-start") && !newCondition.getName().equals("at-end") && !newCondition.getName().equals("over-all")){
                    if (!changed){
                        Element atStartEnd = null;
                        if (condition.getName().equals("condition")){
                            atStartEnd = new Element("at-start");
                        }else if(condition.getName().equals("effect")){
                            atStartEnd = new Element("at-end");
                        }
                        atStartEnd.addContent(newCondition);
                        newContainer.addContent(atStartEnd);
                    }


                }
                
                if (newContainer.getChildren().size()> 0){
                    mainNode.removeContent();
                    
                    //fill out the new condition
                    for (Iterator<Element> it = newContainer.getChildren().iterator(); it.hasNext();) {
                        Element each = it.next();
                        mainNode.addContent((Element)each.clone());
                    }
                    
                    
                    
                }





            }

        }


    }
	
	private static void simplifyOperations(Element action){
		// exists((p - P) (and (predicate a p) (= p b))) => (predicate a b)
		
		// look for this structure
		/*<exists>
			<parameter name="p" type="P" />
			<suchthat>
				<and>
					<predicate id="predicate">
						<parameter id="a" />
						<parameter id="p" />
					</predicate>
					<equals>
						<parameter id="p" />
						<parameter id="b" />
					</equals>
				</and>
				</suchthat>
		</exists>
		
		and build this instead
		
		<predicate id="predicate">
			<parameter id="a" />
			<parameter id="b" />
		</predicate>
		*/
		
		List<?> result = null;
		try {
			XPath path = new JDOMXPath("precondition/descendant::exists | " +
					"effect/descendant::when/descendant::exists");
			result = path.selectNodes(action);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		for (Iterator<?> iter = result.iterator(); iter.hasNext();) {
			Element exists = (Element) iter.next();
			Element and = exists.getChild("suchthat").getChild("and");
			if(and != null && and.getChildren().size() == 2 && 
					exists.getChildren("parameter").size() == 1){
				Element predicate = and.getChild("predicate");
				Element equals = and.getChild("equals");
				if(predicate != null && predicate.getChildren("parameter").size() == 2 && equals != null){
					Element parameter = exists.getChild("parameter");
					Element predicateExistsParameter = null;
					Element equalsExistsParameter = null;
					try {				
						XPath path = new JDOMXPath("parameter[@id='"+ parameter.getAttributeValue("name") +"']");
						predicateExistsParameter = (Element)path.selectSingleNode(predicate);						
						equalsExistsParameter = (Element)path.selectSingleNode(equals);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(predicateExistsParameter != null && equalsExistsParameter != null){
						// the structure was found, so replace it						
						int predicateExistsParameterIndex = predicate.indexOf(predicateExistsParameter);
						int equalsExistsParameterIndex = equals.indexOf(equalsExistsParameter);
						int equalsParameterIndex = (equalsExistsParameterIndex == 0) ?1 :0;
						Element equalsParameter = (Element)equals.getChildren().get(equalsParameterIndex);
						equals.removeContent(equalsParameter);
						
						Element newPredicate = (Element)predicate.clone();
						newPredicate.removeContent(predicateExistsParameterIndex);
						newPredicate.addContent(predicateExistsParameterIndex, equalsParameter);
						
						Element parent = exists.getParentElement();
						int existsIndex = parent.indexOf(exists);
						parent.removeContent(exists);
						parent.addContent(existsIndex, newPredicate);
					}
				}
				
			}
		}
	}

	// this method returns a list with the predicates to be used in a when condition
	// if the list is empty, it indicates that there is no need of when
	private static List<Element> buildWhenCondition(Element predicate){
		List<Element> statements = new ArrayList<Element>();
		buildWhenCondition(predicate, statements);
		return statements;
	}
	
	// this recursive method goes from the predicate up to the precondition node looking for disjunctions
	// if it finds disjunctions, it adds in the list the predicates to be used in a when condition
	// if it finds conjunctions, it returns the same list it receives
	private static void buildWhenCondition(Element node, List<Element> statements){
		
		// get the closest and/or node, or the precondition node
		Element parent = node.getParentElement();
		while(!parent.getName().equals("and") && !parent.getName().equals("or")
				&& !parent.getName().equals("precondition")){
			parent = parent.getParentElement();
		}
		
		if(node.getName().equals("predicate")){
			if(parent.getName().equals("precondition")) {
				// if the parent is precondition, it's a simple statement
				// in this case, do nothing with the list
				return;
			}
			else if(parent.getName().equals("or")){
				// the given node itself it's a condition
				// so, add the node to the list
				statements.add((Element)node.clone());
				
				// call the method with the or node
				buildWhenCondition(parent, statements);
			}
			else{ // and
				// call the method with the and node
				buildWhenCondition(parent, statements);
			}

		}
		else if(node.getName().equals("and")){
			if(parent.getName().equals("precondition")){
				// end the recursion, do nothing with the list
				return;
			}
			else{// or (there are no and nodes inside another and node)
				List<?> children = node.getChildren();
				for (Iterator<?> iter = children.iterator(); iter.hasNext();) {
					Element statement = (Element) iter.next();
					statements.add((Element)statement.clone());
				}
				
				// call the method with the or node
				buildWhenCondition(parent, statements);
			}
		}
		else{ // or
			if(parent.getName().equals("precondition")){
				// end the recursion, do nothing with the list
				return;
			}
			else{ // and (there are no or nodes inside another or node)
				List<?> children = parent.getChildren();
				for (Iterator<?> iter = children.iterator(); iter.hasNext();) {
					Element statement = (Element) iter.next();
					if(statement != node){
						// add the other statements inside the and node
						statements.add((Element)statement.clone());
					}
					//statements.add((Element)node.clone());
				}
				
				// call the method with the or node
				buildWhenCondition(parent, statements);
			}
			
		}
		

	}


         /**
         * This method just check if the domain requirements would probavly need modifications based on the problem.
         * @param xpddlproblem
         * @param pddlVersion
         * @return
         */
        public static boolean needRequirementModification(Element xpddlproblem, String pddlVersion){

            boolean needIt = false;

            if (pddlVersion.equals(PDDL_2_2) || pddlVersion.equals(PDDL_3_0)){

                //XMLUtilities.printXML(xpddlproblem);
                //1. serach for timed initial literal (<at> or <literal> or <timespecifier>)
                List<Element> timedLiterals = xpddlproblem.getChild("init").getChildren("at");
                if (timedLiterals.size() > 0){
                   needIt = true;
                }
            }

               return needIt;

	}

         /**
         * This method check if the pddl requirements at the domain encompass the problem specification
         * @param xpddldomain
         * @param xpddlproblem
         * @param pddlVersion
         */
        public static boolean adjustRequirements(Element xpddldomain, Element xpddlproblem, String pddlVersion){

            boolean modified = false;

            if (pddlVersion.equals(PDDL_2_2) || pddlVersion.equals(PDDL_3_0)){
                //XMLUtilities.printXML(xpddlproblem);

                //1. serach for timed initial literal (<at> or <literal> or <timespecifier>)
                List<Element> timedLiterals = xpddlproblem.getChild("init").getChildren("at");
                if (timedLiterals.size() > 0){
                   //If it has add the requirement tag
                   Element requirements = xpddldomain.getChild("requirements");
                   if (requirements.getChild("timed-initial-literals") == null){
                      Element timedInitialReq = new Element("timed-initial-literals");
                      requirements.addContent(timedInitialReq);
                      modified = true;
                   }

                }
            }

            //XMLUtilities.printXML(xpddldomain);

            return modified;

	}



        /**
         * This function receives a class and creates a xpddl representation of its constraints
         * @param theclass
         * @return
         */
        public static Element buildClassConstraint(Element theclass){
            Element xpddlContraint = null;

            String constraintStr = theclass.getChildText("constraints");

            StringTokenizer tokenizer = new StringTokenizer(constraintStr);

            //Invariants
            String anInvariant = "";
            Element invariants = new Element("invariants");
            Element currentInvariant = null;
            Element defautInvariant = new Element("invariant");
            defautInvariant.addContent(new Element("name"));
            defautInvariant.addContent(new Element("expression"));

            while (tokenizer.hasMoreTokens()) {
                String token = tokenizer.nextToken();
                //System.out.println(token);
                if (token.equals("inv:")){//tis is either the end or the beggining
                    //close the last (if it is no the firt one) and put it in the list
                    if(!anInvariant.equals("") && currentInvariant != null){
                        currentInvariant.getChild("expression").setText(anInvariant);
                        anInvariant = "";
                    }
                    //create(open) a new invariant to come
                    currentInvariant = (Element)defautInvariant.clone();
                    invariants.addContent(currentInvariant);
                }
                else if (token.equals("inv")){//this is either the end or the beggining
                    //In this case we have 'inv NAME:' or 'inv NAME :'
                    String invname = tokenizer.nextToken();
                    if (invname.contains(":")){
                        invname = invname.replace(":", "");
                    }else{
                        String comma = tokenizer.nextToken();
                    }
                    //close the last (if it is no the firt one) and put it in the list
                    if(!anInvariant.equals("")  && currentInvariant != null){
                        currentInvariant.getChild("expression").setText(anInvariant);
                        anInvariant = "";
                    }
                    //create(open) a new invariant to come
                    currentInvariant = (Element)defautInvariant.clone();
                    currentInvariant.getChild("name").setText(invname);
                    invariants.addContent(currentInvariant);
                }
                //if it is not one of the previous case is because we are in the middle of the expression
                else{//collecting the expression
                    anInvariant += token + " ";
                }
                //if it is the last token we need to close the invariant
                if (!tokenizer.hasMoreTokens()){
                    if(!anInvariant.equals("")){
                        //in case there is already a defined invariant
                        if (currentInvariant != null){
                            currentInvariant.getChild("expression").setText(anInvariant);
                            anInvariant = "";
                        }
                        else{//incase no inv has been defines
                            currentInvariant = (Element)defautInvariant.clone();
                            currentInvariant.getChild("expression").setText(anInvariant);
                            invariants.addContent(currentInvariant);
                       }
                   }
                }
            }

            Element invGroup = new Element("always");
            Element andAlwaysnode = new Element("and");


            Element insertInvNode = null;
            if (invariants.getChildren().size() > 1){
                invGroup.addContent(andAlwaysnode);
                insertInvNode = andAlwaysnode;
            }
            else if(invariants.getChildren().size() == 1){
                insertInvNode = invGroup;
            }

            for (Iterator<Element> it = invariants.getChildren().iterator(); it.hasNext();) {
                Element inv = (Element) it.next();
                //if the collected invariant has expression
                if (!inv.getChildText("expression").trim().equals("")){
                    //System.out.println("Invariant name: " +  inv.getChildText("name"));
                    //System.out.println("Expression " +  inv.getChildText("expression"));

                    ExpressionTreeBuilder builder = new ExpressionTreeBuilder(inv.getChildText("expression"));
                    Element constraintExpressionTree = builder.getExpressionTree();
                    //XMLUtilities.printXML(constraintExpressionTree);
                    Element constraintExpresion = buildCondition(constraintExpressionTree, null, null, PRECONDITION);
                    //XMLUtilities.printXML(constraintExpresion);
                    
                    //Aproach: one constraint for each inv
                    /*Element forall = new Element("forall");
                    Element selfParameter = new Element("parameter");
                    selfParameter.setAttribute("name", "self");
                    selfParameter.setAttribute("type", theclass.getChildText("name"));
                    Element always = new Element("always");
                    always.addContent(constraintExpresion);
                    forall.addContent(selfParameter);
                    forall.addContent(always);
                    xpddlContraint = forall;*/

                    //Aproach: one constraint for each inv inside an awalys
                    Element forall = new Element("forall");
                    Element selfParameter = new Element("parameter");
                    selfParameter.setAttribute("name", "self");
                    selfParameter.setAttribute("type", theclass.getChildText("name"));
                    forall.addContent(selfParameter);
                    forall.addContent(constraintExpresion);
                    insertInvNode.addContent(forall);
                }
            }

            if (invGroup.getChildren().size() > 0){
                xpddlContraint = invGroup;
            }

            return xpddlContraint;
        }

	/**
	 * @param args
	 */
	/*public static void main(String[] args) {
		Element project = null;
		try {
			project = XMLUtilities.readFromFile("examples/BlocksDomainv2test.xml").getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element xpddlDomain = XMLToXPDDLDomain(project, PDDL_3_0);
		XMLUtilities.printXML(xpddlDomain);
		
		
		if(project != null){
			List<?> problems = project.getChild("diagrams").getChild("planningDomains").getChild("domain").getChild("planningProblems").getChildren("problem");
			for (Iterator<?> iter = problems.iterator(); iter.hasNext();) {
				Element problem = (Element) iter.next();
				Element xpddlProblem = XMLToXPDDLProblem(problem, PDDL_3_0);
				XMLUtilities.printXML(xpddlProblem);
				System.out.println("\n-----------------------------------------------------------------------------------------------\n");
			}

		}
		
	}*/

}
