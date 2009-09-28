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
*			Victor Romero.
**/

package planning;

import languages.uml.ocl.OCLUtilities;
import languages.uml.ocl.ExpressionTreeBuilder;
import itSIMPLE.ItSIMPLE;
import languages.xml.XMLUtilities;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JLabel;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class PlanSimulator {
	
	private static final String changeColor = "009900";
    private static  Element theproblem = null;
    private static  Element thedomain = null;
    private static  Element theproject = null;

	
	/**
     * In the simulation process it creates the next state given a 'from'
     * snapshot and a action with its parameters
     * @param action
     * @param parameters
     * @param snapshot
     * @param domain
     * @param project
     * @return
     */
    public static Element nextState(String action, List<String> parameters, Element snapshot, Element domain, Element project){
		//System.out.println("Action: " + action + " " +parameters);
		
		Element next = null;		
		//1. look for the operator in the domain
		Element domainOperator = null;
		try {
			XPath path = new JDOMXPath("elements/classes/class/operators/operator[lower-case(name)='"+ action.toLowerCase() +"']");			
			domainOperator = (Element)path.selectSingleNode(project);
			
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		if(domainOperator != null){
			//2. initialize an action node
			Element instanceAction = new Element("action");
			instanceAction.addContent((Element)domainOperator.getChild("name").clone());
			Element instanceParameters = new Element("parameters");
			int index = 0;
			for (Iterator<?> iter = domainOperator.getChild("parameters").getChildren("parameter").iterator(); iter.hasNext();) {
				Element opParam = (Element) iter.next();
				Element instanceParam = new Element("parameter");
				
				Element paramValue = new Element("value");
				paramValue.setText(parameters.get(index++));
				instanceParam.addContent((Element)opParam.getChild("name").clone());
				instanceParam.addContent((Element)opParam.getChild("type").clone());
				instanceParam.addContent(paramValue);
				
				instanceParameters.addContent(instanceParam);
			}
			instanceAction.addContent(instanceParameters);
			instanceAction.addContent(new Element("precondition"));
			instanceAction.addContent(new Element("postcondition"));

			
			//3. look for the same action at the state diagrams
			Element actionConditions = OCLUtilities.buildConditions(domainOperator);

            //XMLUtilities.printXML(actionConditions);
			
			//4. build the post condition
			List<?> conditions = null;
			try {
				XPath path = new JDOMXPath("postconditions/descendant::condition");			
				conditions = path.selectNodes(actionConditions);			
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			String oclExpression = "";
			for (Iterator<?> iter = conditions.iterator(); iter.hasNext();) {
				Element condition = (Element) iter.next();
				oclExpression += condition.getText().trim();
				
				if(iter.hasNext()){
					oclExpression += " and ";
				}
			}

			//System.out.println(oclExpression);
			//5. create the expression tree
			ExpressionTreeBuilder treeBuilder = new ExpressionTreeBuilder(oclExpression);			
			Element expTree = treeBuilder.getExpressionTree();
			//XMLUtilities.printXML(expTree);
			
			//6. replace the parameters values
			for (Iterator<?> iter = instanceAction.getChild("parameters").getChildren("parameter").iterator(); iter.hasNext();) {
				Element parameter = (Element) iter.next();
				// look for the parameters in the tree
				List<?> expTreeParameters = null;
				try {
					XPath path = new JDOMXPath("descendant::node[@data='"+ parameter.getChildText("name")
							+"' and @instance='f']");			
					expTreeParameters = path.selectNodes(expTree);			
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				for (Iterator<?> iterator = expTreeParameters.iterator(); iterator.hasNext();) {
					Element expTreeParam = (Element) iterator.next();
					expTreeParam.setAttribute("data", parameter.getChildText("value"));
					expTreeParam.setAttribute("instance", "t");
				}
			}			
			//XMLUtilities.printXML(expTree);
			
			
			//7. evaluate the expression tree
			//7.1 checks if the expression tree root is an "and" node
			// if it's not, add the expression tree to a list so it can be iterated
			// this is done just to avoid code repetition
			List<Element> children;
			if(expTree.getAttributeValue("data").equals("and")){
				children = expTree.getChildren();
			}
			else{
				children = new ArrayList<Element>();
				children.add(expTree);
			}
			
			List<Element> nodesToBeAdded = new ArrayList<Element>();
			List<Element> nodesToBeDeleted = new ArrayList<Element>();
			for (Iterator<?> iter = children.iterator(); iter.hasNext();) {
				Element node = (Element) iter.next();
				if(node.getAttributeValue("data").equals("=")){
					// node with expression
					Element left = (Element)node.getChildren().get(0);
					Element right = (Element)node.getChildren().get(1);
					
					Element attrOrAssoc = null;
					// get the attribute being set
					if(left.getAttributeValue("data").equals(".")){
						attrOrAssoc = (Element)left.getChildren().get(1);
					}
					else if(left.getAttributeValue("type").equals("opd")){
						attrOrAssoc = left;
					}					
					
//					check whether the attribute is parameterized
					String attrOrAssocData = attrOrAssoc.getAttributeValue("data");					
					String attrOrAssocName;
					
					if(attrOrAssocData.indexOf('(') > 0){
						// the attribute is parameterized
						// replace the parameter values
						attrOrAssocName = attrOrAssocData.substring(0, attrOrAssocData.indexOf('('));
						String attrParameters = attrOrAssocData.substring(attrOrAssocData.indexOf('(')+1, attrOrAssocData.lastIndexOf(')'));
						String attrValues = "(";
						StringTokenizer st = new StringTokenizer(attrParameters, ",");
						while(st.hasMoreTokens()){
							String parameter = st.nextToken().trim().toLowerCase();
							Element instanceParameter = null;
							try {
								XPath path = new JDOMXPath("parameter[lower-case(name)='"+ parameter +"']");			
								instanceParameter = (Element)path.selectSingleNode(instanceParameters);						
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							if(instanceParameter != null){
								attrValues += instanceParameter.getChildText("value");
								
								if(st.hasMoreTokens()){
									attrValues += ",";
								}
							}
							else{
								//System.out.println("Parameter \""+ parameter +"\" not found in parameters list.");
                                //TODO: Check if it is a literal
                                Element literal = null;
                                try {
                                    XPath path = new JDOMXPath("project/elements/classes/enumeration/literals/literal[lower-case(name)='"+ parameter +"']");
                                    literal = (Element)path.selectSingleNode(domain.getDocument());
                                } catch (JaxenException e) {
                                    e.printStackTrace();
                                }
                                if(literal!=null){
                                    attrValues += parameter;
                                }else{
                                    attrValues += "#error";
                                }

								
							}
						}
						attrValues += ")";
						
						String newAttrData = attrOrAssocName + attrValues;
						attrOrAssoc.setAttribute("data", newAttrData);
						attrOrAssoc.setAttribute("instance", "t");
					}
					else{
						attrOrAssocName = attrOrAssocData;
					}
					
					if(right.getAttributeValue("type").equals("opr")){
						// deal with int and float difference
						// look for the type of the attribute (int or float)
						Element modelAttrOrAssoc = null;
						try {
							XPath path = new JDOMXPath("elements/classes/class/attributes/attribute[lower-case(name)='"+ 
									attrOrAssocName.toLowerCase() + "'] | elements/classAssociations/" +
									"classAssociation[associationEnds/associationEnd/rolename[lower-case(value)='"+
									attrOrAssocName.toLowerCase() +"'] or lower-case(name)='"+ 
									attrOrAssocName.toLowerCase() +"']");			
							modelAttrOrAssoc = (Element)path.selectSingleNode(project);						
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						if(modelAttrOrAssoc != null){
							Element newRight = evaluateExpressionNode(right, instanceParameters, snapshot, domain, project);							
							
							if(modelAttrOrAssoc.getChild("type") != null && 
									modelAttrOrAssoc.getChildText("type").equals("2")){//int
								int value = (int)Double.parseDouble(newRight.getAttributeValue("data"));
								newRight.setAttribute("data", String.valueOf(value));
								newRight.setAttribute("instance", "t");
							}
							
							if(newRight != null){
								node.removeContent(right);						
								node.addContent(newRight);
							}
							else{
								//System.out.println("Error in evaluating procces");
							}
						}
						
					}				
					
				}
				
				else if(node.getAttributeValue("data").equals("->")){
					Element left = (Element)node.getChildren().get(0);
					Element right = (Element)node.getChildren().get(1);
					
					// operations
					if(right.getAttributeValue("type").equals("opn")){
						
						// forAll
						if(right.getAttributeValue("data").equals("forAll")){
							// the left node represents a lisf of objects
							Element newLeft = evaluateExpressionNode(left, instanceParameters, snapshot, domain, project);
														
							// get forAll parameters (only one is supported currently)
							List<?> forAllParameters = null;
							try {
								XPath path = new JDOMXPath("node[@type='par']");			
								forAllParameters = path.selectNodes(right);						
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							
							
							if(forAllParameters != null && forAllParameters.size() == 1){
								Element forAllParam = (Element)forAllParameters.get(0);
								String paramData = forAllParam.getAttributeValue("data");
								String paramName = paramData.substring(0, paramData.indexOf(':'));
								int i = paramData.indexOf(':');
								String paramType = "";
								if(i > 0){
									paramType = paramData.substring(i+1, paramData.length());
								}
								
								// get the model class representing the parameter type (if any)
								Element modelClass = null;								
								if(!paramType.equals("")){
									try {
										XPath path = new JDOMXPath("elements/classes/class[lower-case(name)='" +
												paramType.toLowerCase() +"']");			
										modelClass = (Element)path.selectSingleNode(project);						
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
								}
								
								for (Iterator<?> iterator = newLeft.getChildren().iterator();
										iterator.hasNext();) {
								
									Element object = (Element) iterator.next();
									
									// look for the model object
									Element modelObject = null;
									try {
										XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='" +
												object.getAttributeValue("data").toLowerCase() +"']");			
										modelObject = (Element)path.selectSingleNode(domain);						
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									
									// if the parameter type is defined, check whether the object
									// is an instance of that type
									boolean rightType = true;
									if(modelClass != null){
										if(!modelObject.getChildText("class").equals(
												modelClass.getAttributeValue("id"))){
											rightType = false;
										}
									}
									
									if(rightType){
										// clone the node under "suchthat" node and replace
										// the forAll parameter with the object name
										// this way, for each object, an atribution will be created
										Element attributionClone = (Element)((Element)right.getChildren().get(
												right.getChildren().size()-1)).getChild("node").clone();
										
										if(attributionClone.getAttributeValue("data").equals("=")){
//											 replace the parameter value in all nodes where it appears
											List<?> nodesWithParameter = null;
											try {
												XPath path = new JDOMXPath("descendant::node[@data='"+ paramName +"']");			
												nodesWithParameter = path.selectNodes(attributionClone);						
											} catch (JaxenException e) {			
												e.printStackTrace();
											}
											
											for (Iterator iterator2 = nodesWithParameter
													.iterator(); iterator2.hasNext();) {
												Element nodeWithParameter = (Element) iterator2.next();
												nodeWithParameter.setAttribute("data", 
														modelObject.getChildText("name").toUpperCase());
											}
											
											Element arrowNode = null;
											try {
												XPath path = new JDOMXPath("descendant::node[@data='->']");			
												arrowNode = (Element)path.selectSingleNode(attributionClone);						
											} catch (JaxenException e) {			
												e.printStackTrace();
											}
											
											if(arrowNode != null){
												
																								
												// get including/excluding node (if any)
												Element incOrExc = null;
												try {
													XPath path = new JDOMXPath("node[@data='including' or @data='excluding']");			
													incOrExc = (Element)path.selectSingleNode(arrowNode);						
												} catch (JaxenException e) {			
													e.printStackTrace();
												}
												
												if(incOrExc != null){
													// replace it in arrowNode place
													arrowNode.getParentElement().addContent(incOrExc.detach());
													arrowNode.detach();
												}
												
												
											}
											
											// add the atribution in the add list
											nodesToBeAdded.add((Element)attributionClone);
																						
										}
									}
									
								}
								
								// add to the remove list
								nodesToBeDeleted.add(node);
							}
						}
					}
				}
			}
			
			for (Iterator iter = nodesToBeDeleted.iterator(); iter.hasNext();) {
				Element toBeDeleted = (Element) iter.next();
				toBeDeleted.detach();
			}
			
			for (Iterator iter = nodesToBeAdded.iterator(); iter.hasNext();) {				
				Element toBeAdded = (Element) iter.next();				
					expTree.addContent(toBeAdded);	
			}
			
			//XMLUtilities.printXML(expTree);
			
			//8. create the next snapshot
			//8.1 clone the current snapshot
			next = (Element)snapshot.clone();
			
			// reset the colors
			List<?> colors = null;			
			try {
				XPath path = new JDOMXPath("descendant::color[text()!='']");			
				colors = path.selectNodes(next);
				
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			
			for (Iterator<?> iter = colors.iterator(); iter.hasNext();) {
				Element color = (Element) iter.next();
				color.setText("");
			}
			
			//8.2 for each attribution in the postcondition, make the change it in the snapshot
			List<Element> attributions;
			if(!expTree.getAttributeValue("data").equals("and")){
				// there is only one node in the tree
				attributions = new ArrayList<Element>();
				attributions.add(expTree);
			}
			else{
				attributions = expTree.getChildren("node");
			}
			for (Iterator<?> iter = attributions.iterator(); iter.hasNext();) {
				Element node = (Element) iter.next();
				if(node.getAttributeValue("data").equals("=")){
					Element left = (Element)node.getChildren().get(0);
					Element right = (Element)node.getChildren().get(1);
					
										
					//look for the "." (dot) operator (non global attributes) in left
					if(left.getAttributeValue("data").equals(".")){
						
						// check whether the left node represents an attribute
						Element objectNode = (Element)left.getChildren().get(0);
						Element attrOrAssoc = (Element)left.getChildren().get(1);
						
						// look for the instance parameter
						Element instanceParameter = null;
						try {
							XPath path = new JDOMXPath("parameter[lower-case(value)='"+ objectNode.getAttributeValue("data").toLowerCase() +"']");			
							instanceParameter = (Element)path.selectSingleNode(instanceParameters);						
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						
						if(instanceParameter != null){
							// look for the object class
							Element objectClass = null;
							try {
								XPath path = new JDOMXPath("elements/classes/class[@id='"+ instanceParameter.getChildText("type") +"']");			
								objectClass = (Element)path.selectSingleNode(project);						
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							if(objectClass != null){
//								look for the object in the snapshot domain								
								Element domainObject = null;
								try {
									XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='"
											+ instanceParameter.getChildText("value").toLowerCase() +"']");			
									domainObject = (Element)path.selectSingleNode(domain);						
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
								if(domainObject != null){
									// look for the object in the snapshot
									Element snapshotObject = null;
									try {
										XPath path = new JDOMXPath("objects/object[@id='"+ domainObject.getAttributeValue("id") +"']");			
										snapshotObject = (Element)path.selectSingleNode(next);						
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									if(snapshotObject != null){
										// look for an attribute (from class), association (from elements in project) or rolename (from elements in project)
										Element oclNode = null;
										String oclAttributeStr = attrOrAssoc.getAttributeValue("data");
										String attrName;
										if(oclAttributeStr.indexOf('(') > 0){
											// parameterized attribute
											attrName = oclAttributeStr.substring(0, oclAttributeStr.indexOf('('));
										}
										else{
											attrName = oclAttributeStr;
										}
										try {
											XPath path = new JDOMXPath("elements/classes/class/attributes/attribute[lower-case(name)='"
													+ attrName.toLowerCase() +
													"'] | elements/classAssociations/classAssociation[" +
													"associationEnds/associationEnd/rolename[lower-case(value)='"+
													attrName.toLowerCase() +"'] or lower-case(name)='"+ attrName.toLowerCase() +"']");			
											oclNode = (Element)path.selectSingleNode(project);				
										} catch (JaxenException e) {			
											e.printStackTrace();
										}
										if(oclNode != null){										
											//attributes
											if(oclNode.getName().equals("attribute")){
												
												List<?> attrParameters = oclNode.getChild("parameters").getChildren("parameter");
												if(attrParameters.size() > 0){
													//parameterized attribute
													String attrParams = oclAttributeStr.substring(oclAttributeStr.indexOf('(')+1, oclAttributeStr.lastIndexOf(')'));
													String query = "attributes/attribute[@id='"+ oclNode.getAttributeValue("id") +"']/value/parameterizedValue[";
													StringTokenizer st = new StringTokenizer(attrParams, ",");
													for (Iterator<?> iterator = attrParameters.iterator(); iterator.hasNext();) {
														Element attrParam = (Element) iterator.next();
														String paramValue = "";
														if(st.hasMoreTokens()){
															paramValue = st.nextToken().trim().toLowerCase();
														}
														else{											
															//System.out.println("There are fewer parameters than expected at attribute: "+ oclNode.getChildText("name"));
														}
														query += "parameters/parameter[@id='"+ attrParam.getAttributeValue("id")
															+"' and lower-case(value)='"+ paramValue + "']";
														
														if(iterator.hasNext()){
															query += " and ";
														}
													}									
													query += "]";
													
													if(st.hasMoreTokens()){
														//System.out.println("There are more parameters than expected at attribute: "+ oclNode.getChildText("name"));
													}
													Element parameterizedValue = null;
													try {
														XPath path = new JDOMXPath(query);			
														parameterizedValue = (Element)path.selectSingleNode(snapshotObject);						
													} catch (JaxenException e) {			
														e.printStackTrace();
													}
													if(parameterizedValue != null){
														//System.out.println("Before: "+ domainObject.getChildText("name")+ " - "+ oclNode.getChildText("name"));
														//XMLUtilities.printXML(parameterizedValue);
														
														//refresh the value in the snapshot										
														parameterizedValue.getChild("value").setText(right.getAttributeValue("data"));
														
														
														//set highlight color
														parameterizedValue.getChild("graphics").getChild("color").setText(changeColor);
														
														//System.out.println("After:");
														//XMLUtilities.printXML(parameterizedValue);
														//System.out.println("-----------------------------------------------");
													}
													else{
														//System.out.println("Parameterized value \""+ oclAttributeStr
														//		+"\" not found in snapshot object.");
													}
												
												}
												else{
													// not parameterized attributes
													Element objectAttribute = null;
													try {
														XPath path = new JDOMXPath("attributes/attribute[@id='"+ oclNode.getAttributeValue("id")
																+"' and @class='"+ oclNode.getParentElement().getParentElement().getAttributeValue("id") +"']");			
														objectAttribute = (Element)path.selectSingleNode(snapshotObject);						
													} catch (JaxenException e) {			
														e.printStackTrace();
													}
													if(objectAttribute != null){												
														//check whether the value is to be changed												
														if(!right.getAttributeValue("data").toLowerCase().equals(objectAttribute.getChildText("value").toLowerCase())){
															//System.out.println("Before: "+ domainObject.getChildText("name")+ " - "+ oclNode.getChildText("name"));
															//XMLUtilities.printXML(objectAttribute);
															// do the change
															objectAttribute.getChild("value").setText(right.getAttributeValue("data"));
															
															// set highlight color
															objectAttribute.getChild("graphics").getChild("color").setText(changeColor);
															
															//System.out.println("After:");
															//XMLUtilities.printXML(objectAttribute);
															//System.out.println("-----------------------------------------------");
														}
													}
													else{
														//System.out.println("Attribute \"" + oclAttributeStr + "\" not found in snapshot object.");
													}
												}
											}
											
											// associations
											else if(oclNode.getName().equals("classAssociation")){

												Element targetAssociationEnd = null;
												try {
													XPath path = new JDOMXPath("associationEnds/associationEnd[rolename[lower-case(value)='"+ 
															oclAttributeStr.toLowerCase() +"']]");			
													targetAssociationEnd = (Element)path.selectSingleNode(oclNode);
												} catch (JaxenException e) {			
													e.printStackTrace();
												}
												
												if(targetAssociationEnd == null){
													// if it's null, there is no rolename, so check navigation
													try {
														XPath path = new JDOMXPath("associationEnds/associationEnd[@navigation='true']");			
														targetAssociationEnd = (Element)path.selectSingleNode(oclNode);						
													} catch (JaxenException e) {			
														e.printStackTrace();
													}
												}
												
												//TODO case with both association ends navigation true or false is not currently dealed
												
												
												if(targetAssociationEnd != null){
													//rolename
													String objectAssociationEndID = (targetAssociationEnd.getAttributeValue("id").equals("1")) ?"2" :"1";
													Element objectAssociation = null;
													try {
														XPath path = new JDOMXPath("associations/objectAssociation[classAssociation='"+ oclNode.getAttributeValue("id")
																+"' and associationEnds/objectAssociationEnd[@element-id='"+ 
																domainObject.getAttributeValue("id") +"' and @id='"+ objectAssociationEndID +"']]");														
														objectAssociation = (Element)path.selectSingleNode(next);						
													} catch (JaxenException e) {			
														e.printStackTrace();
													}
													if(right.getAttributeValue("data").toLowerCase().equals("null")){
														if(objectAssociation != null){
															// delete the object association
															Element associations = objectAssociation.getParentElement();
															associations.removeContent(objectAssociation);
															
															//System.out.println("Association removed: " + oclNode.getChildText("name"));															
														}
													}
													else if(right.getAttributeValue("data").equals("set")){
														// the only currently supported structure is: a.b = a.b->including/excluding(c)
														
														Element evalLeft = evaluateExpressionNode(left, instanceParameters, 
																snapshot, domain, project);														
														
														List<?> leftChildren = evalLeft.getChildren();
														List<?> rightChildren = right.getChildren();
														
														// if right set has more nodes than left set, it's an including operation
														if(rightChildren.size() > leftChildren.size()){
															Element rightClone = (Element)right.clone(); // done to keep the expression tree
															for (Iterator<?> iterator = leftChildren.iterator(); iterator.hasNext();) {
																Element leftChild = (Element) iterator.next();
																// look for the child in right set
																Element rightChild = null;
																try {
																	XPath path = new JDOMXPath("node[@data='"+ leftChild.getAttributeValue("data") +"']");														
																	rightChild = (Element)path.selectSingleNode(rightClone);						
																} catch (JaxenException e) {			
																	e.printStackTrace();
																}
																if(rightChild != null){
																	// remove from set
																	rightChild.detach();
																}
															}
															
															// at the end, the only remaining node in right set is the one to be included
															if(rightClone.getChildren().size() == 1){
																Element nodeToInclude = rightClone.getChild("node");
																
																// find the second object, i. e., the object to which the association points
																Element valueObject = null;
																try {
																	XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='"+ 
																			nodeToInclude.getAttributeValue("data").toLowerCase() +"']");														
																	valueObject = (Element)path.selectSingleNode(domain);						
																} catch (JaxenException e) {			
																	e.printStackTrace();
																}
																
																if(valueObject != null){

																	// create a new object association
																	Element commonData = ItSIMPLE.getCommonData();
																	
																	Element newObjectAssociation = (Element)commonData.getChild("definedNodes").getChild("elements")
																		.getChild("model").getChild("objectAssociation").clone();
																	
																	newObjectAssociation.getChild("classAssociation").setText(oclNode.getAttributeValue("id"));
																																		
																	Element newSourceAssociationEnd = null;
																	Element newTargetAssociationEnd = null; 
																	//(Element)newObjectAssociation.getChild("associationEnds").getChildren("objectAssociationEnd").get(1);
																	try {
																		XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='" +
																				targetAssociationEnd.getAttributeValue("id") +"']");			
																		newTargetAssociationEnd = (Element)path.selectSingleNode(newObjectAssociation);						
																	} catch (JaxenException e) {			
																		e.printStackTrace();
																	}
																	
																	if(newTargetAssociationEnd != null){
																		
																		String sourceId = (newTargetAssociationEnd.getAttributeValue("id").equals("1")) ?"2" :"1";
																		try {
																			XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='" +
																					sourceId +"']");			
																			newSourceAssociationEnd = (Element)path.selectSingleNode(newObjectAssociation);						
																		} catch (JaxenException e) {			
																			e.printStackTrace();
																		}
																		
																		if(newSourceAssociationEnd != null){
																			
																		}
																	}
																	
																	// 1st association end
																	newSourceAssociationEnd.setAttribute("element", "object");
																	newSourceAssociationEnd.setAttribute("element-id", domainObject.getAttributeValue("id"));
																	
																	// 2nd association end
																	newTargetAssociationEnd.setAttribute("element", "object");
																	newTargetAssociationEnd.setAttribute("element-id", valueObject.getAttributeValue("id"));
																	
																	newObjectAssociation.setAttribute("id", String.valueOf(XMLUtilities.getId(snapshot.getChild("associations"))));
																	
																	//set the highlight color
																	newObjectAssociation.getChild("graphics").getChild("color").setText(changeColor);
																	
																	// add it to the snapshot
																	next.getChild("associations").addContent(newObjectAssociation);
																	
																	//System.out.println("Object association created: "
																	//		+ oclNode.getChildText("name")+ " " 
																	//		+ domainObject.getChildText("name")+ " ---- " + valueObject.getChildText("name"));		
																	//System.out.println("-----------------------------------------------");
																}
															}
															else{
																//System.out.println("ERROR: Set has more nodes than expected");
															}

														}
														
														// if left set has more nodes than right set, it's an excluding operation
														else if(leftChildren.size() > rightChildren.size()){															
															for (Iterator<?> iterator = rightChildren.iterator(); iterator.hasNext();) {
																Element rightChild = (Element) iterator.next();
																// look for the child in right set
																Element leftChild = null;
																try {
																	XPath path = new JDOMXPath("node[@data='"+ rightChild.getAttributeValue("data") +"']");														
																	leftChild = (Element)path.selectSingleNode(evalLeft);						
																} catch (JaxenException e) {			
																	e.printStackTrace();
																}
																if(rightChild != null){
																	// remove from set
																	leftChild.detach();
																}
															}
															
															// at the end, the only remaining node in left is the one to be excluded
															if(evalLeft.getChildren().size() == 1){
																Element nodeToExclude = evalLeft.getChild("node");
																
																Element valueObject = null;
																try {
																	XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='"+ 
																			nodeToExclude.getAttributeValue("data").toLowerCase() +"']");														
																	valueObject = (Element)path.selectSingleNode(domain);						
																} catch (JaxenException e) {			
																	e.printStackTrace();
																}																
																if(valueObject != null){
																	//String objectAssociationEndID = (associationEnd.getAttributeValue("id").equals("1")) ?"2" :"1";
																	Element association = null;
																	try {
																		XPath path = new JDOMXPath("associations/objectAssociation[classAssociation='"
																				+ oclNode.getAttributeValue("id")
																				+"' and associationEnds/objectAssociationEnd[@element-id='"+ 
																				valueObject.getAttributeValue("id") +"' and @id='"+ 
																				targetAssociationEnd.getAttributeValue("id") +"']]");														
																		association = (Element)path.selectSingleNode(next);						
																	} catch (JaxenException e) {			
																		e.printStackTrace();
																	}
																	
																	if(association != null){
																		association.detach();
																		//System.out.println("Association removed: " + oclNode.getChildText("name"));
																		
																		//System.out.println("-----------------------------------------------");
																	}
																}

															}
															else{
																//System.out.println("ERROR: Set has more nodes than expected");
															}
														}
														

														
													}
													
													else if(right.getAttributeValue("data").equals("including")){
														// the only currently supported structure is: a.b = a.b->including/excluding(c)
														
														Element nodeToInclude = right.getChild("node");
														
														Element evalLeft = evaluateExpressionNode(left, instanceParameters, 
																snapshot, domain, project);
														Element sameNode = null;
														try {
															XPath path = new JDOMXPath("node[@data='"+ 
																	nodeToInclude.getAttributeValue("data") +"']");														
															sameNode = (Element)path.selectSingleNode(evalLeft);						
														} catch (JaxenException e) {			
															e.printStackTrace();
														}
														
														if(sameNode == null){
															// adds the node
//															 find the second object, i. e., the object to which the association points
															Element valueObject = null;
															try {
																XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='"+ 
																		nodeToInclude.getAttributeValue("data").toLowerCase() +"']");														
																valueObject = (Element)path.selectSingleNode(domain);						
															} catch (JaxenException e) {			
																e.printStackTrace();
															}
															
															if(valueObject != null){

																// create a new object association
																Element commonData = ItSIMPLE.getCommonData();
																
																Element newObjectAssociation = (Element)commonData.getChild("definedNodes").getChild("elements")
																	.getChild("model").getChild("objectAssociation").clone();
																
																newObjectAssociation.getChild("classAssociation").setText(oclNode.getAttributeValue("id"));
																																	
																Element newSourceAssociationEnd = null;
																Element newTargetAssociationEnd = null; 
																//(Element)newObjectAssociation.getChild("associationEnds").getChildren("objectAssociationEnd").get(1);
																try {
																	XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='" +
																			targetAssociationEnd.getAttributeValue("id") +"']");			
																	newTargetAssociationEnd = (Element)path.selectSingleNode(newObjectAssociation);						
																} catch (JaxenException e) {			
																	e.printStackTrace();
																}
																
																if(newTargetAssociationEnd != null){
																	
																	String sourceId = (newTargetAssociationEnd.getAttributeValue("id").equals("1")) ?"2" :"1";
																	try {
																		XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='" +
																				sourceId +"']");			
																		newSourceAssociationEnd = (Element)path.selectSingleNode(newObjectAssociation);						
																	} catch (JaxenException e) {			
																		e.printStackTrace();
																	}
																	
																	if(newSourceAssociationEnd != null){
																		
																	}
																}
																
																// 1st association end
																newSourceAssociationEnd.setAttribute("element", "object");
																newSourceAssociationEnd.setAttribute("element-id", domainObject.getAttributeValue("id"));
																
																// 2nd association end
																newTargetAssociationEnd.setAttribute("element", "object");
																newTargetAssociationEnd.setAttribute("element-id", valueObject.getAttributeValue("id"));
																
																newObjectAssociation.setAttribute("id", String.valueOf(XMLUtilities.getId(snapshot.getChild("associations"))));
																
																//set the highlight color
																newObjectAssociation.getChild("graphics").getChild("color").setText(changeColor);
																
																// add it to the snapshot
																next.getChild("associations").addContent(newObjectAssociation);
																
																//System.out.println("Object association created: "
																//		+ oclNode.getChildText("name")+ " " 
																//		+ domainObject.getChildText("name")+ " ---- " + valueObject.getChildText("name"));		
																//System.out.println("-----------------------------------------------");
															}
														}
														
													}
													
													else if(right.getAttributeValue("data").equals("excluding")){
														// the only currently supported structure is: a.b = a.b->including/excluding(c)
														
														Element nodeToExclude = right.getChild("node");
														
														Element evalLeft = evaluateExpressionNode(left, instanceParameters, 
																snapshot, domain, project);
														Element sameNode = null;
														try {
															XPath path = new JDOMXPath("node[@data='"+ 
																	nodeToExclude.getAttributeValue("data") +"']");														
															sameNode = (Element)path.selectSingleNode(evalLeft);						
														} catch (JaxenException e) {			
															e.printStackTrace();
														}
														
														if(sameNode != null){
															// excludes node
															//nodeToExclude = evalLeft.getChild("node");
															
															Element valueObject = null;
															try {
																XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='"+ 
																		nodeToExclude.getAttributeValue("data").toLowerCase() +"']");														
																valueObject = (Element)path.selectSingleNode(domain);						
															} catch (JaxenException e) {			
																e.printStackTrace();
															}																
															if(valueObject != null){
																//String objectAssociationEndID = (associationEnd.getAttributeValue("id").equals("1")) ?"2" :"1";
																Element association = null;
																try {
																	XPath path = new JDOMXPath("associations/objectAssociation[classAssociation='"
																			+ oclNode.getAttributeValue("id")
																			+"' and associationEnds/objectAssociationEnd[@element-id='"+ 
																			valueObject.getAttributeValue("id") +"' and @id='"+ 
																			targetAssociationEnd.getAttributeValue("id") +"']]");														
																	association = (Element)path.selectSingleNode(next);						
																} catch (JaxenException e) {			
																	e.printStackTrace();
																}
																
																if(association != null){
																	association.detach();
																	//System.out.println("Association removed: " + oclNode.getChildText("name"));
																	
																	//System.out.println("-----------------------------------------------");
																}
															}
														}
													}
													
													else{//there is a value
														// look for the value in domain
														Element valueInstanceParameter = null;
														try {
															XPath path = new JDOMXPath("parameter[value='"+ right.getAttributeValue("data") +"']");			
															valueInstanceParameter = (Element)path.selectSingleNode(instanceParameters);						
														} catch (JaxenException e) {			
															e.printStackTrace();
														}
														if(valueInstanceParameter != null){
															Element valueObject = null;
															try {
																XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='"+
																		valueInstanceParameter.getChildText("value").toLowerCase() +"']");			
																valueObject = (Element)path.selectSingleNode(domain);						
															} catch (JaxenException e) {			
																e.printStackTrace();
															}															
															if(valueObject != null){
																boolean createAssociation;
																if(objectAssociation == null){
																	createAssociation = true;
																}
																else{
																	Element firstAssociationEnd = (Element)objectAssociation.getChild("associationEnds").getChildren("objectAssociationEnd").get(0);
																	Element secondAssociationEnd = (Element)objectAssociation.getChild("associationEnds").getChildren("objectAssociationEnd").get(1);
																	String firstElementID = firstAssociationEnd.getAttributeValue("element-id");
																	String secondElementID = secondAssociationEnd.getAttributeValue("element-id");
																	if(firstElementID.equals(domainObject.getAttributeValue("id")) &&
																			secondElementID.equals(valueObject.getAttributeValue("id"))){
																		//don't create a new object association	
																		createAssociation = false;
																	}
																	else{
																		// create a new association
																		createAssociation = true;
																	}
																}
																if(createAssociation){
																	// create a new object association
																	Element commonData = ItSIMPLE.getCommonData();
																	
																	Element newObjectAssociation = (Element)commonData.getChild("definedNodes").getChild("elements")
																		.getChild("model").getChild("objectAssociation").clone();
																	newObjectAssociation.getChild("classAssociation").setText(oclNode.getAttributeValue("id"));
																	Element newSourceAssociationEnd = null;
																	Element newTargetAssociationEnd = null; 
																	//(Element)newObjectAssociation.getChild("associationEnds").getChildren("objectAssociationEnd").get(1);
																	try {
																		XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='" +
																				targetAssociationEnd.getAttributeValue("id") +"']");			
																		newTargetAssociationEnd = (Element)path.selectSingleNode(newObjectAssociation);						
																	} catch (JaxenException e) {			
																		e.printStackTrace();
																	}
																	
																	if(newTargetAssociationEnd != null){
																		
																		String sourceId = (newTargetAssociationEnd.getAttributeValue("id").equals("1")) ?"2" :"1";
																		try {
																			XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='" +
																					sourceId +"']");			
																			newSourceAssociationEnd = (Element)path.selectSingleNode(newObjectAssociation);						
																		} catch (JaxenException e) {			
																			e.printStackTrace();
																		}
																		
																		if(newSourceAssociationEnd != null){
//																			 1st association end
																			newSourceAssociationEnd.setAttribute("element", "object");
																			newSourceAssociationEnd.setAttribute("element-id", domainObject.getAttributeValue("id"));
																			
																			// 2nd association end
																			newTargetAssociationEnd.setAttribute("element", "object");
																			newTargetAssociationEnd.setAttribute("element-id", valueObject.getAttributeValue("id"));
																			
																			newObjectAssociation.setAttribute("id", String.valueOf(XMLUtilities.getId(next.getChild("associations"))));
																			
																			// set the highlight color
																			newObjectAssociation.getChild("graphics").getChild("color").setText(changeColor);
																			
																			next.getChild("associations").addContent(newObjectAssociation);
																			
																			//System.out.println("Object association created: "+ oclNode.getChildText("name")+ " " 
																			//		+ domainObject.getChildText("name")+ " ---- " + valueObject.getChildText("name"));
																			
																			
																			if(objectAssociation != null){
																				// delete the old object association
																				Element associations = objectAssociation.getParentElement();
																				associations.removeContent(objectAssociation);
																				
																				//System.out.println("Association removed: " + oclNode.getChildText("name"));
																				
																				//System.out.println("-----------------------------------------------");
																			}
																		}
																		
																	}
																	
																}															
															}
															else{
																//System.out.println("Object \""+ valueInstanceParameter.getChildText("value") +
																//		"\"  not found in domain.");
															}
														}
														else{
															//System.out.println("Value \""+ right.getAttributeValue("data") +"\" not found in parameters list.");
														}
													}
												}
												else{
													//association
													//TODO associations without rolenames
													
												}
											}																			
										}
										else{
											//System.out.println("Attribute or association \"" + attrName + "\" not found in project.");
										}
									}
									else{
										//System.out.println("Object \"" + domainObject.getAttributeValue("id") + "\" not found in next snapshot.");
									}
								}
								else{
									//System.out.println("Object \"" + instanceParameter.getChildText("value") + "\"  not found in domain.");
								}
							}
							else{
								//System.out.println("Class \"" + instanceParameter.getChildText("type") + "\" not found in domain.");
							}
						}
						else{
							//System.out.println("Object \"" + objectNode.getAttributeValue("data") + "\" not found in parameters list.");
						}
						
					}
					else{
						// global attributes
						Element classAttr = null;
						String attrName;
						if(left.getAttributeValue("data").indexOf("(") > 0){
							// parameterized attribute
							attrName = left.getAttributeValue("data").substring(0, left.getAttributeValue("data").indexOf("(")).toLowerCase();
						}
						else{
							attrName = left.getAttributeValue("data").toLowerCase();
						}
						try {
							XPath path = new JDOMXPath("elements/classes/class[stereotype='utility']/attributes/attribute[lower-case(name)='"+
									attrName + "']");			
							classAttr = (Element)path.selectSingleNode(project);						
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						if(classAttr != null){
							Element utilityClass = classAttr.getParentElement().getParentElement();
//							 not parameterized attribute
							
							// look for the domain object
							Element domainObject = null;
							try {
								XPath path = new JDOMXPath("elements/objects/object[class='"+ utilityClass.getAttributeValue("id") +"']");			
								domainObject = (Element)path.selectSingleNode(domain);						
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							if(domainObject != null){
								// look for the snapshot object
								Element snapshotObject = null;
								try {
									XPath path = new JDOMXPath("objects/object[@id='"+ domainObject.getAttributeValue("id") +"']");			
									snapshotObject = (Element)path.selectSingleNode(next);						
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
								if(snapshotObject != null){
									Element objectAttribute = null;
									try {
										XPath path = new JDOMXPath("attributes/attribute[@id='"+ classAttr.getAttributeValue("id")
												+"' and @class='"+ classAttr.getParentElement().getParentElement().getAttributeValue("id") +"']");			
										objectAttribute = (Element)path.selectSingleNode(snapshotObject);						
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									if(objectAttribute != null){
										List<?> attrParameters = classAttr.getChild("parameters").getChildren("parameter");
										if(attrParameters.size() > 0){
											
											String leftData = left.getAttributeValue("data");

											//parameterized attribute
											String attrParams = leftData.substring(leftData.indexOf('(')+1, leftData.lastIndexOf(')'));
											String query = "attributes/attribute[@id='"+ classAttr.getAttributeValue("id") +"']/value/parameterizedValue[";
											StringTokenizer st = new StringTokenizer(attrParams, ",");
											for (Iterator<?> iterator = attrParameters.iterator(); iterator.hasNext();) {
												Element attrParam = (Element) iterator.next();
												String paramValue = "";
												if(st.hasMoreTokens()){
													paramValue = st.nextToken().trim().toLowerCase();
												}
												else{											
													//System.out.println("There are fewer parameters than expected at attribute: "+ classAttr.getChildText("name"));
												}
												query += "lower-case(parameters/parameter[@id='"+ attrParam.getAttributeValue("id")
													+"']/value)='"+ paramValue +"'";
												
												if(iterator.hasNext()){
													query += " and ";
												}
											}									
											query += "]";											
											if(st.hasMoreTokens()){
												//System.out.println("There are more parameters than expected at attribute: "+ classAttr.getChildText("name"));
											}
											Element parameterizedValue = null;
											try {
												XPath path = new JDOMXPath(query);			
												parameterizedValue = (Element)path.selectSingleNode(snapshotObject);						
											} catch (JaxenException e) {			
												e.printStackTrace();
											}
											if(parameterizedValue != null){
												//System.out.println("Before: "+ domainObject.getChildText("name")+ " - "+ classAttr.getChildText("name"));
												//XMLUtilities.printXML(parameterizedValue);
												
												//refresh the value in the snapshot										
												parameterizedValue.getChild("value").setText(right.getAttributeValue("data"));
												
												//set the highlight color
												parameterizedValue.getChild("graphics").getChild("color").setText(changeColor);
												
												//System.out.println("After:");
												//XMLUtilities.printXML(parameterizedValue);
												//System.out.println("-----------------------------------------------");
											}
											else{
												//System.out.println("Parameterized value \""+ left.getAttributeValue("data")
												//		+"\" not found in snapshot object.");
											}
											
											
										}
										else{
											// not parameterized value
//											check whether the value is to be changed												
											if(!right.getAttributeValue("data").toLowerCase().equals(objectAttribute.getChildText("value").toLowerCase())){
												//System.out.println("Before: "+ domainObject.getChildText("name")+ " - "+ classAttr.getChildText("name"));
												//XMLUtilities.printXML(objectAttribute);
												// do the change
												objectAttribute.getChild("value").setText(right.getAttributeValue("data"));
												
												// set the highlight color
												//set the highlight color
												objectAttribute.getChild("graphics").getChild("color").setText(changeColor);
												
												//System.out.println("After:");
												//XMLUtilities.printXML(objectAttribute);	
												//System.out.println("-----------------------------------------------");
											}
										}
									}
									else{
										//System.out.println("Attribute \"" + classAttr.getChildText("name") + "\" not found in snapshot object.");
									}
								}
								else{
									//System.out.println("Object with utility stereotype not found in snapshot.");
								}
							}
							else{
								//System.out.println("Object with utility stereotype not found in domain.");
							}							
						}
						else{
							//System.out.println("Global attribute \""+ left.getAttributeValue("data") +"\" not found.");
						}
					}
				}
			}
		}
		else{
			//System.out.println("Operator \""+ action +"\" not found.");
		}
		
		
		return next;
	}
	

    /**
     * This method evaluate a expression node in a given snaphot considering
     * a list of action's parameters.
     * This method is depricated; use evaluateExpressionNodeInSnapshot instead.
     * @param node
     * @param instanceParameters
     * @param snapshot
     * @param domain
     * @param project
     * @return
     */
    @Deprecated
    private static Element evaluateExpressionNode(Element node, Element instanceParameters,
			Element snapshot, Element domain, Element project){
		Element evaluatedNode = null;
		String nodeData = node.getAttributeValue("data");
		if(nodeData.equals(".")){
			Element objectNode = (Element)node.getChildren().get(0);
			Element attrOrAssocNode = (Element)node.getChildren().get(1);			
			String data = attrOrAssocNode.getAttributeValue("data");
			
			String attrOrAssocName;
			if(data.indexOf("(") > 0){
				// parameterized attribute (take off parenthesis)
				attrOrAssocName = data.substring(0, data.indexOf('(')).toLowerCase();
				
				// replace parameter values
				String attrName = data.substring(0, data.indexOf('('));
				String attrParameters = data.substring(data.indexOf('(')+1, data.lastIndexOf(')'));
				String attrValues = "(";
				StringTokenizer st = new StringTokenizer(attrParameters, ",");
				while(st.hasMoreTokens()){
					String parameter = st.nextToken().trim().toLowerCase();
					Element instanceParameter = null;
					try {
						XPath path = new JDOMXPath("parameter[lower-case(name)='"+ parameter +"']");			
						instanceParameter = (Element)path.selectSingleNode(instanceParameters);						
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(instanceParameter != null){
						attrValues += instanceParameter.getChildText("value");
						
						if(st.hasMoreTokens()){
							attrValues += ",";
						}
					}
					else{
                        //System.out.println("Parameter \""+ parameter +"\" not found in parameters list.");
                        //TODO: Check if it is a literal
                        Element literal = null;
                        try {
                            XPath path = new JDOMXPath("project/elements/classes/enumeration/literals/literal[lower-case(name)='"+ parameter +"']");
                            literal = (Element)path.selectSingleNode(domain.getDocument());
                        } catch (JaxenException e) {
                            e.printStackTrace();
                        }
                        if(literal!=null){
                            attrValues += parameter;
                        }else{
                            attrValues += "#error";
                        }
						
						//attrValues += "#error";
					}
				}
				attrValues += ")";
				
				String newAttrData = attrName + attrValues;
				attrOrAssocNode.setAttribute("data", newAttrData);
				attrOrAssocNode.setAttribute("instance", "t");
				
				// update data
				data = newAttrData;
			}
			else{
				attrOrAssocName = data.toLowerCase();
			}
			
//			look for the instance parameter
			Element instanceParameter = null;
			try {
				XPath path = new JDOMXPath("parameter[lower-case(value)='"+ objectNode.getAttributeValue("data").toLowerCase() +"']");			
				instanceParameter = (Element)path.selectSingleNode(instanceParameters);						
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			
			if(instanceParameter != null){
//				 look for the object class
				Element objectClass = null;
				try {
					XPath path = new JDOMXPath("elements/classes/class[@id='"+ instanceParameter.getChildText("type") +"']");			
					objectClass = (Element)path.selectSingleNode(project);						
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(objectClass != null){
//					look for the object in the snapshot domain					
					Element domainObject = null;
					try {
						XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='"
								+ instanceParameter.getChildText("value").toLowerCase() +"']");			
						domainObject = (Element)path.selectSingleNode(domain);						
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(domainObject != null){
//						 look for the object in the snapshot
						Element snapshotObject = null;
						try {
							XPath path = new JDOMXPath("objects/object[@id='"+ domainObject.getAttributeValue("id") +"']");			
							snapshotObject = (Element)path.selectSingleNode(snapshot);						
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						if(snapshotObject != null){
							// check whether the second node is an attribute or an association
							Element attrOrAssocModelNode = null;
							try {
								XPath path = new JDOMXPath("elements/classes/class/attributes/attribute[lower-case(name)='"+
										attrOrAssocName +"'] | elements/classAssociations/" +
										"classAssociation[associationEnds/associationEnd/rolename[lower-case(value)='"+
										attrOrAssocName +"'] or lower-case(name)='"+ 
										attrOrAssocName +"']");			
								attrOrAssocModelNode = (Element)path.selectSingleNode(project);						
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							
							if(attrOrAssocModelNode != null){
								
								// attribute
								if(attrOrAssocModelNode.getName().equals("attribute")){
									// look for the attribute in the object class 
									Element classAttr = null;								
									
									try {
										XPath path = new JDOMXPath("elements/classes/class/attributes/attribute[lower-case(name)='"
												+ attrOrAssocName + "']");			
										classAttr = (Element)path.selectSingleNode(project);						
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									if(classAttr != null){
										List<?> attrParameters = classAttr.getChild("parameters").getChildren();
										if(attrParameters.size() > 0){
											// parameterized attribute									
											String attrParams = data.toLowerCase().substring(data.indexOf('(')+1, data.lastIndexOf(')'));
											String query = "attributes/attribute[@id='"+ classAttr.getAttributeValue("id") +"']/value/parameterizedValue[";
											StringTokenizer st = new StringTokenizer(attrParams, ",");
											for (Iterator<?> iter = attrParameters.iterator(); iter.hasNext();) {
												Element attrParam = (Element) iter.next();
												String value = "";
												if(st.hasMoreTokens()){
													value = st.nextToken().trim().toLowerCase();
												}
												else{											
													//System.out.println("There are less parameters than expected at attribute: "+ classAttr.getChildText("name"));
												}
												query += "parameters/parameter[@id='"+ attrParam.getAttributeValue("id")
													+"' and lower-case(value)='"+ value +"']";
												
												if(iter.hasNext()){
													query += " and ";
												}
											}									
											query += "]";
											
											if(st.hasMoreTokens()){
												//System.out.println("There are more parameters than expected at attribute: "+ classAttr.getChildText("name"));
											}
											Element parameterizedValue = null;
											try {
												XPath path = new JDOMXPath(query);			
												parameterizedValue = (Element)path.selectSingleNode(snapshotObject);						
											} catch (JaxenException e) {			
												e.printStackTrace();
											}
											if(parameterizedValue != null){
												//returns the value given in the snapshot										
												evaluatedNode = (Element)node.clone();
												evaluatedNode.removeContent();
												evaluatedNode.setAttribute("type", "opd");
												evaluatedNode.setAttribute("data", parameterizedValue.getChildText("value"));
												evaluatedNode.setAttribute("instance", "t");										
											}
											else{
												//System.out.println("Parameterized value "+ attrOrAssocName +" not found in snapshot object.");
											}
										}
										else{
											// not parameterized attribute
											Element objectAttribute = null;
											try {
												XPath path = new JDOMXPath("attributes/attribute[@id='"+ classAttr.getAttributeValue("id")
														+"' and @class='"+ classAttr.getParentElement().getParentElement().getAttributeValue("id") +"']");			
												objectAttribute = (Element)path.selectSingleNode(snapshotObject);						
											} catch (JaxenException e) {			
												e.printStackTrace();
											}
											if(objectAttribute != null){
												// returns the value given in the snapshot										
												evaluatedNode = (Element)node.clone();
												evaluatedNode.removeContent();
												evaluatedNode.setAttribute("type", "opd");
												evaluatedNode.setAttribute("data", objectAttribute.getChildText("value"));
												evaluatedNode.setAttribute("instance", "t");
											}
											else{
												//System.out.println("Object attribute \"" + classAttr.getChildText("name") + "\" not found in snapshot.");
											}	
										}
									}
									else{
										//System.out.println("Attribute \"" + attrOrAssocNode.getAttributeValue("data") + "\" not found in project.");
									}
								}
								else{
									// association
									//get the second object association end has the rolename or the navigation false
									Element secondObjectAssociationEnd = null;
									// try with rolename
									try {
										XPath path = new JDOMXPath("associationEnds/associationEnd[lower-case(rolename/value)='" +
												attrOrAssocName.toLowerCase() +"']");			
										secondObjectAssociationEnd = (Element)path.selectSingleNode(attrOrAssocModelNode);						
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									if(secondObjectAssociationEnd == null){
										// if it's null, there is no rolename, so check navigation
										try {
											XPath path = new JDOMXPath("associationEnds/associationEnd[@navigation='true']");			
											secondObjectAssociationEnd = (Element)path.selectSingleNode(attrOrAssocModelNode);						
										} catch (JaxenException e) {			
											e.printStackTrace();
										}
									}
									
									//TODO case with both association ends navigation true or false is not currently dealed
									
									// get the first object of the association
									Element firstObject = null;
									try {
										XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='" +
												objectNode.getAttributeValue("data").toLowerCase() +"']");			
										firstObject = (Element)path.selectSingleNode(domain);						
									} catch (JaxenException e) {			
										e.printStackTrace();
									}

									if(secondObjectAssociationEnd != null && firstObject != null){	
										// get the class association
										Element classAssociation = secondObjectAssociationEnd.getParentElement().getParentElement();
										
										//get the first object association end id
										String secondObjectAssociationEndId = secondObjectAssociationEnd.getAttributeValue("id");
										String firstObjectAssociationEndId = (secondObjectAssociationEndId.equals("1")) ?"2" :"1";
										       
										// get all object associations with that name and with the same first object
										List<?> objectAssociations = null;
										try {
											XPath path = new JDOMXPath("associations/objectAssociation[classAssociation='" +
													classAssociation.getAttributeValue("id") +"' and " +
													"associationEnds/objectAssociationEnd[@element='object' and " +
													"@element-id='"+ firstObject.getAttributeValue("id") +"' and " +
													"@id='"+ firstObjectAssociationEndId +"']]");
											objectAssociations = path.selectNodes(snapshot);						
										} catch (JaxenException e) {			
											e.printStackTrace();
										}
										
										// create the set node to receive the objects
										evaluatedNode = new Element("node");
										evaluatedNode.setAttribute("type", "opd");
										evaluatedNode.setAttribute("data", "set");
										evaluatedNode.setAttribute("instance", "t");
										
										for (Iterator<?> iter = objectAssociations.iterator(); iter.hasNext();) {
											Element objectAssociation = (Element) iter.next();
											
											// for each object association, get the second object
											Element currentSecondObjectAssociationEnd = null;
											try {
												XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='" +
														secondObjectAssociationEndId +"']");		
												currentSecondObjectAssociationEnd = (Element)path.selectSingleNode(objectAssociation);																
											} catch (JaxenException e) {			
												e.printStackTrace();
											}
											
											if(currentSecondObjectAssociationEnd != null){
												// get the object corresponding to this association end
												Element currentSecondObject = null;
												try {
													XPath path = new JDOMXPath("elements/objects/object[@id='" +
															currentSecondObjectAssociationEnd.getAttributeValue("element-id") +"']");		
													currentSecondObject = (Element)path.selectSingleNode(domain);		
												} catch (JaxenException e) {			
													e.printStackTrace();
												}
												
												if(currentSecondObject != null){
													// add a node to the set
													Element newNode = new Element("node");
													newNode.setAttribute("type", "opd");
													newNode.setAttribute("data", currentSecondObject.getChildText("name").toUpperCase());
													newNode.setAttribute("instance", "t");
													
													evaluatedNode.addContent(newNode);
												}
											}
										}
									}
								}
							}

						}
						else{
							//System.out.println("Object \"" + domainObject.getAttributeValue("id") + "\" not found in  snapshot.");
						}
					}
					else{
						//System.out.println("Object \"" + instanceParameter.getChildText("value") + "\"  not found in domain.");
					}
				}
				else{
					//System.out.println("Class \"" + instanceParameter.getChildText("type") + "\" not found in domain.");
				}
			}
			else{
				//System.out.println("Object \"" + objectNode.getAttributeValue("data") + "\" not found in parameters list.");
			}
			
		}
		else if(nodeData.equals("->")){// including, excluding
			Element left = (Element)node.getChildren().get(0);
			Element right = (Element)node.getChildren().get(1);
			String rightData = right.getAttributeValue("data");
			Element set = evaluateExpressionNode(left, instanceParameters, snapshot, domain, project);

			// with including or excluding, it will return the result of the set
			// after the inclusion or exclusion
			if(rightData.equals("including") || rightData.equals("excluding")){		
				Element param = evaluateExpressionNode(right.getChild("node"), instanceParameters, snapshot, domain, project);
				if(rightData.equals("including")){
					// add the node to the set
					set.addContent(param);
				}
				else{// excluding
					// look for the node in the set
					Element nodeToBeDeleted = null;
					try {
						XPath path = new JDOMXPath("node[lower-case(@data)='"+ 
								param.getAttributeValue("data").toLowerCase() +"']");		
						nodeToBeDeleted = (Element)path.selectSingleNode(set);		
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(nodeToBeDeleted != null){
						// exclude the node from set
						nodeToBeDeleted.detach();
					}
					else{
						//System.out.println("Object to be excluded \""+ param.getAttributeValue("data") 
						//		+"\" not found.");
					}
				}
				evaluatedNode = set;
			}
		}
		
		
		else if(nodeData.equals("+") ||
				nodeData.equals("-") ||
				nodeData.equals("*") ||
				nodeData.equals("/")){
			Element left = (Element)node.getChildren().get(0);
			Element right = (Element)node.getChildren().get(1);

            //XMLUtilities.printXML(left);
            //XMLUtilities.printXML(right);

			Element evalLeft = evaluateExpressionNode(left, instanceParameters, snapshot, domain, project);
			Element evalRight = evaluateExpressionNode(right, instanceParameters, snapshot, domain, project);
            //XMLUtilities.printXML(evalLeft);
            //XMLUtilities.printXML(evalRight);
			
			evaluatedNode = (Element)node.clone();
			evaluatedNode.removeContent();
			evaluatedNode.setAttribute("type", "opd");
			evaluatedNode.setAttribute("instance", "t");
			
			switch(node.getAttributeValue("data").charAt(0)){
			case '+':{
				double result = Double.parseDouble(evalLeft.getAttributeValue("data")) + 
					Double.parseDouble(evalRight.getAttributeValue("data"));
				
				evaluatedNode.setAttribute("data", String.valueOf(result));
			}
			break;
			case '-':{
				double result = Double.parseDouble(evalLeft.getAttributeValue("data")) - 
					Double.parseDouble(evalRight.getAttributeValue("data"));
				
				evaluatedNode.setAttribute("data", String.valueOf(result));
			}
			break;
			case '*':{
				double result = Double.parseDouble(evalLeft.getAttributeValue("data")) * 
					Double.parseDouble(evalRight.getAttributeValue("data"));
				
				evaluatedNode.setAttribute("data", String.valueOf(result));
			}
			break;
			case '/':{
				double result = Double.parseDouble(evalLeft.getAttributeValue("data")) / 
					Double.parseDouble(evalRight.getAttributeValue("data"));
				
				evaluatedNode.setAttribute("data", String.valueOf(result));
			}
			break;
			}
		}
		else if(node.getAttributeValue("type").equals("opd")){		
			// check whether the attribute is global (stereotype 'utility')
			String attrData = node.getAttributeValue("data");
			String attrNodeName;
			if(attrData.indexOf("(") > 0){
				// parameterized attribute
				attrNodeName = attrData.substring(0, attrData.indexOf("(")).toLowerCase();
			}
			else{
				attrNodeName = attrData.toLowerCase();
			}
			Element classAttr = null;						
			try {
				XPath path = new JDOMXPath("elements/classes/class[stereotype='utility']/attributes/attribute[lower-case(name)='"+
						attrNodeName + "']");			
				classAttr = (Element)path.selectSingleNode(project);					
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			if(classAttr != null){
				Element utilityClass = classAttr.getParentElement().getParentElement();
				
//				 look for the domain object
				Element domainObject = null;
				try {
					XPath path = new JDOMXPath("elements/objects/object[class='"+ utilityClass.getAttributeValue("id") +"']");			
					domainObject = (Element)path.selectSingleNode(domain);						
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(domainObject != null){
					// look for the snapshot object
					Element snapshotObject = null;
					try {
						XPath path = new JDOMXPath("objects/object[@id='"+ domainObject.getAttributeValue("id") +"']");			
						snapshotObject = (Element)path.selectSingleNode(snapshot);						
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(snapshotObject != null){
						Element objectAttribute = null;
						try {
							XPath path = new JDOMXPath("attributes/attribute[@id='"+ classAttr.getAttributeValue("id")
									+"' and @class='"+ classAttr.getParentElement().getParentElement().getAttributeValue("id") +"']");			
							objectAttribute = (Element)path.selectSingleNode(snapshotObject);						
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						if(objectAttribute != null){												
							List<?> attrParameters = classAttr.getChild("parameters").getChildren();
							if(attrParameters.size() > 0){
								// parameterized attribute									
								String attrParams = attrData.substring(attrData.indexOf('(')+1, attrData.lastIndexOf(')'));
								String query = "attributes/attribute[@id='"+ objectAttribute.getAttributeValue("id") +"']/value/parameterizedValue[";
								StringTokenizer st = new StringTokenizer(attrParams, ",");
								for (Iterator<?> iter = attrParameters.iterator(); iter.hasNext();) {
									Element attrParam = (Element) iter.next();
									String attrParamName = "";
									if(st.hasMoreTokens()){
										attrParamName = st.nextToken().trim().toLowerCase();
									}
									else{											
										//System.out.println("There are less parameters than expected at attribute: "+ classAttr.getChildText("name"));
									}
									
									// look for the attribute parameter value in instance parameters
									Element instanceParameter = null;
									try {
										XPath path = new JDOMXPath("parameter[lower-case(name)='" +
												attrParamName.toLowerCase() +"']");			
										instanceParameter = (Element)path.selectSingleNode(instanceParameters);						
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									
									if(instanceParameter != null){

										query += "lower-case(parameters/parameter[@id='"+ attrParam.getAttributeValue("id")
											+"']/value)='"+ instanceParameter.getChildText("value").toLowerCase() +"'";
										
										if(iter.hasNext()){
											query += " and ";
										}
									}
									else{
										//System.out.println("Error evaluating parameterized value: " +
										//		"instance parameter \""+ attrParamName +"\" not found.");
									}
									
								}									
								query += "]";
								//System.out.println(query);
								if(st.hasMoreTokens()){
									//System.out.println("There are more parameters than expected at attribute: "+ classAttr.getChildText("name"));
								}
								Element parameterizedValue = null;
								try {
									XPath path = new JDOMXPath(query);			
									parameterizedValue = (Element)path.selectSingleNode(snapshotObject);						
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
								if(parameterizedValue != null){
									//returns the value given in the snapshot										
									evaluatedNode = (Element)node.clone();
									evaluatedNode.removeContent();
									evaluatedNode.setAttribute("type", "opd");
									evaluatedNode.setAttribute("data", parameterizedValue.getChildText("value"));
									evaluatedNode.setAttribute("instance", "t");
								}
								else{
									//System.out.println("Parameterized value "+ attrData +"not found in snapshot object.");
								}
							}
							else{																		
								evaluatedNode = (Element)node.clone();
								evaluatedNode.removeContent();
								evaluatedNode.setAttribute("type", "opd");
								evaluatedNode.setAttribute("data", objectAttribute.getChildText("value"));
								evaluatedNode.setAttribute("instance", "t");
							}
						}
						else{
							//System.out.println("Attribute \"" + classAttr.getChildText("name") + "\" not found in snapshot object.");
						}
					}
					else{
						//System.out.println("Object with utility stereotype not found in snapshot.");
					}
				}
				else{
					//System.out.println("Object with utility stereotype not found in domain.");
				}
			}
			else{
				// returns the node itself
				evaluatedNode = (Element)node.clone();
			}
		}
		return evaluatedNode;
		
	}
	


    /**
     * This method evaluate a expression node in a given snaphot based considering
     * a list of action's parameters (if such list exists). If the list do not exist
     * it will look for key elements in the domain.
     * @param node
     * @param instanceParameters
     * @param snapshot
     * @param domain
     * @param project
     * @return
     */
    private static Element evaluateExpressionNodeInSnapshot(Element node, Element instanceParameters,
			Element snapshot, Element domain, Element project){
		Element evaluatedNode = null;
		String nodeData = node.getAttributeValue("data");
		if(nodeData.equals(".")){
			Element objectNode = (Element)node.getChildren().get(0);
			Element attrOrAssocNode = (Element)node.getChildren().get(1);
			String data = attrOrAssocNode.getAttributeValue("data");

			String attrOrAssocName;
			if(data.indexOf("(") > 0){
				// parameterized attribute (take off parenthesis)
				attrOrAssocName = data.substring(0, data.indexOf('(')).toLowerCase();

				// replace parameter values
				String attrName = data.substring(0, data.indexOf('('));
				String attrParameters = data.substring(data.indexOf('(')+1, data.lastIndexOf(')'));
				String attrValues = "(";
				StringTokenizer st = new StringTokenizer(attrParameters, ",");
				while(st.hasMoreTokens()){
                    boolean foundIt = false;
					String parameter = st.nextToken().trim().toLowerCase();
					Element instanceParameter = null;

                    //1. try to find if in the parameters
					try {
						XPath path = new JDOMXPath("parameter[lower-case(name)='"+ parameter +"']");
						instanceParameter = (Element)path.selectSingleNode(instanceParameters);
					} catch (JaxenException e) {
						e.printStackTrace();
					}
					if(instanceParameter != null){
						attrValues += instanceParameter.getChildText("value");
                        foundIt = true;

						if(st.hasMoreTokens()){
							attrValues += ",";
						}
					}
                    //2.try to see if it is an enumeration
                    if (!foundIt){
                        //System.out.println("Parameter \""+ parameter +"\" not found in parameters list.");
                        //TODO: Check if it is a literal
                        Element literal = null;
                        try {
                            XPath path = new JDOMXPath("project/elements/classes/enumeration/literals/literal[lower-case(name)='"+ parameter +"']");
                            literal = (Element)path.selectSingleNode(domain.getDocument());
                        } catch (JaxenException e) {
                            e.printStackTrace();
                        }
                        if(literal!=null){
                            attrValues += parameter;
                            foundIt = true;
                        }
					}
                    //3.try to see if it is an object from the domain
                    if (!foundIt){
                        //System.out.println("Parameter \""+ parameter +"\" not found in parameters list.");
                        //TODO: Check if it is a literal
                        Element object = null;
                        try {
                            XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='"+ parameter +"']");
                            object = (Element)path.selectSingleNode(domain);
                        } catch (JaxenException e) {
                            e.printStackTrace();
                        }
                        if(object!=null){
                            attrValues += parameter;
                            foundIt = true;
                        }
					}

                    //If it was not found at all sen error
                    if (!foundIt){
                        attrValues += "#error";
                    }

				}
				attrValues += ")";

				String newAttrData = attrName + attrValues;
				attrOrAssocNode.setAttribute("data", newAttrData);
				attrOrAssocNode.setAttribute("instance", "t");

				// update data
				data = newAttrData;
			}
			else{
				attrOrAssocName = data.toLowerCase();
			}


            boolean foundObjectNode = false;
            Element domainObject = null;
            Element objectClass = null;


            //1. check if it is a instance parameter
			Element instanceParameter = null;
			try {
				XPath path = new JDOMXPath("parameter[lower-case(value)='"+ objectNode.getAttributeValue("data").toLowerCase() +"']");
				instanceParameter = (Element)path.selectSingleNode(instanceParameters);
			} catch (JaxenException e) {
				e.printStackTrace();
			}
            if(instanceParameter != null){
                //look for the object class
				try {
					XPath path = new JDOMXPath("elements/classes/class[@id='"+ instanceParameter.getChildText("type") +"']");
					objectClass = (Element)path.selectSingleNode(project);
				} catch (JaxenException e) {
					e.printStackTrace();
				}
				if(objectClass != null){
                    //look for the object in the snapshot domain
					try {
						XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='"
								+ instanceParameter.getChildText("value").toLowerCase() +"']");
						domainObject = (Element)path.selectSingleNode(domain);
					} catch (JaxenException e) {
						e.printStackTrace();
					}
					if(domainObject != null){
                        foundObjectNode = true;
					}
                }
			}

            //2. check if it is an object from the domain
            if (!foundObjectNode){
                try {
                    XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='"+ objectNode.getAttributeValue("data").toLowerCase() +"']");
                    domainObject = (Element)path.selectSingleNode(domain);
                } catch (JaxenException e) {
                    e.printStackTrace();
                }
                if(domainObject!=null){
                    //look for the object class
                    try {
                        XPath path = new JDOMXPath("elements/classes/class[@id='"+ domainObject.getChildText("class") +"']");
                        objectClass = (Element)path.selectSingleNode(project);
                    } catch (JaxenException e) {
                        e.printStackTrace();
                    }
                    if(objectClass!=null){
                        foundObjectNode = true;
                    }
                }
            }




            //If it was not found at all sen error
            if (!foundObjectNode){
                System.out.println("object \"" + objectNode.getAttributeValue("data") + "\" not found in domain.");

            }

            //if we found the object
            if (foundObjectNode){
                //look for the object in the snapshot
                Element snapshotObject = null;
                try {
                    XPath path = new JDOMXPath("objects/object[@id='"+ domainObject.getAttributeValue("id") +"']");
                    snapshotObject = (Element)path.selectSingleNode(snapshot);
                } catch (JaxenException e) {
                    e.printStackTrace();
                }
                if(snapshotObject != null){
                    // check whether the second node is an attribute or an association
                    Element attrOrAssocModelNode = null;
                    try {
                        XPath path = new JDOMXPath("elements/classes/class/attributes/attribute[lower-case(name)='"+
                                attrOrAssocName +"'] | elements/classAssociations/" +
                                "classAssociation[associationEnds/associationEnd/rolename[lower-case(value)='"+
                                attrOrAssocName +"'] or lower-case(name)='"+
                                attrOrAssocName +"']");
                        attrOrAssocModelNode = (Element)path.selectSingleNode(project);
                    } catch (JaxenException e) {
                        e.printStackTrace();
                    }

                    if(attrOrAssocModelNode != null){

                        // attribute
                        if(attrOrAssocModelNode.getName().equals("attribute")){
                            // look for the attribute in the object class
                            Element classAttr = null;

                            try {
                                XPath path = new JDOMXPath("elements/classes/class/attributes/attribute[lower-case(name)='"
                                        + attrOrAssocName + "']");
                                classAttr = (Element)path.selectSingleNode(project);
                            } catch (JaxenException e) {
                                e.printStackTrace();
                            }
                            if(classAttr != null){
                                List<?> attrParameters = classAttr.getChild("parameters").getChildren();
                                if(attrParameters.size() > 0){
                                    // parameterized attribute
                                    String attrParams = data.toLowerCase().substring(data.indexOf('(')+1, data.lastIndexOf(')'));
                                    String query = "attributes/attribute[@id='"+ classAttr.getAttributeValue("id") +"']/value/parameterizedValue[";
                                    StringTokenizer st = new StringTokenizer(attrParams, ",");
                                    for (Iterator<?> iter = attrParameters.iterator(); iter.hasNext();) {
                                        Element attrParam = (Element) iter.next();
                                        String value = "";
                                        if(st.hasMoreTokens()){
                                            value = st.nextToken().trim().toLowerCase();
                                        }
                                        else{
                                            //System.out.println("There are less parameters than expected at attribute: "+ classAttr.getChildText("name"));
                                        }
                                        query += "parameters/parameter[@id='"+ attrParam.getAttributeValue("id")
                                            +"' and lower-case(value)='"+ value +"']";

                                        if(iter.hasNext()){
                                            query += " and ";
                                        }
                                    }
                                    query += "]";

                                    if(st.hasMoreTokens()){
                                        //System.out.println("There are more parameters than expected at attribute: "+ classAttr.getChildText("name"));
                                    }
                                    Element parameterizedValue = null;
                                    try {
                                        XPath path = new JDOMXPath(query);
                                        parameterizedValue = (Element)path.selectSingleNode(snapshotObject);
                                    } catch (JaxenException e) {
                                        e.printStackTrace();
                                    }
                                    if(parameterizedValue != null){
                                        //returns the value given in the snapshot
                                        evaluatedNode = (Element)node.clone();
                                        evaluatedNode.removeContent();
                                        evaluatedNode.setAttribute("type", "opd");
                                        evaluatedNode.setAttribute("data", parameterizedValue.getChildText("value"));
                                        evaluatedNode.setAttribute("instance", "t");
                                    }
                                    else{
                                        //System.out.println("Parameterized value "+ attrOrAssocName +" not found in snapshot object.");
                                    }
                                }
                                else{
                                    // not parameterized attribute
                                    Element objectAttribute = null;
                                    try {
                                        XPath path = new JDOMXPath("attributes/attribute[@id='"+ classAttr.getAttributeValue("id")
                                                +"' and @class='"+ classAttr.getParentElement().getParentElement().getAttributeValue("id") +"']");
                                        objectAttribute = (Element)path.selectSingleNode(snapshotObject);
                                    } catch (JaxenException e) {
                                        e.printStackTrace();
                                    }
                                    if(objectAttribute != null){
                                        // returns the value given in the snapshot
                                        evaluatedNode = (Element)node.clone();
                                        evaluatedNode.removeContent();
                                        evaluatedNode.setAttribute("type", "opd");
                                        evaluatedNode.setAttribute("data", objectAttribute.getChildText("value"));
                                        evaluatedNode.setAttribute("instance", "t");
                                    }
                                    else{
                                        //System.out.println("Object attribute \"" + classAttr.getChildText("name") + "\" not found in snapshot.");
                                    }
                                }
                            }
                            else{
                                //System.out.println("Attribute \"" + attrOrAssocNode.getAttributeValue("data") + "\" not found in project.");
                            }
                        }
                        else{
                            // association
                            //get the second object association end has the rolename or the navigation false
                            Element secondObjectAssociationEnd = null;
                            // try with rolename
                            try {
                                XPath path = new JDOMXPath("associationEnds/associationEnd[lower-case(rolename/value)='" +
                                        attrOrAssocName.toLowerCase() +"']");
                                secondObjectAssociationEnd = (Element)path.selectSingleNode(attrOrAssocModelNode);
                            } catch (JaxenException e) {
                                e.printStackTrace();
                            }
                            if(secondObjectAssociationEnd == null){
                                // if it's null, there is no rolename, so check navigation
                                try {
                                    XPath path = new JDOMXPath("associationEnds/associationEnd[@navigation='true']");
                                    secondObjectAssociationEnd = (Element)path.selectSingleNode(attrOrAssocModelNode);
                                } catch (JaxenException e) {
                                    e.printStackTrace();
                                }
                            }

                            //TODO case with both association ends navigation true or false is not currently dealed

                            // get the first object of the association
                            Element firstObject = null;
                            try {
                                XPath path = new JDOMXPath("elements/objects/object[lower-case(name)='" +
                                        objectNode.getAttributeValue("data").toLowerCase() +"']");
                                firstObject = (Element)path.selectSingleNode(domain);
                            } catch (JaxenException e) {
                                e.printStackTrace();
                            }

                            if(secondObjectAssociationEnd != null && firstObject != null){
                                // get the class association
                                Element classAssociation = secondObjectAssociationEnd.getParentElement().getParentElement();

                                //get the first object association end id
                                String secondObjectAssociationEndId = secondObjectAssociationEnd.getAttributeValue("id");
                                String firstObjectAssociationEndId = (secondObjectAssociationEndId.equals("1")) ?"2" :"1";

                                // get all object associations with that name and with the same first object
                                List<?> objectAssociations = null;
                                try {
                                    XPath path = new JDOMXPath("associations/objectAssociation[classAssociation='" +
                                            classAssociation.getAttributeValue("id") +"' and " +
                                            "associationEnds/objectAssociationEnd[@element='object' and " +
                                            "@element-id='"+ firstObject.getAttributeValue("id") +"' and " +
                                            "@id='"+ firstObjectAssociationEndId +"']]");
                                    objectAssociations = path.selectNodes(snapshot);
                                } catch (JaxenException e) {
                                    e.printStackTrace();
                                }

                                // create the set node to receive the objects
                                evaluatedNode = new Element("node");
                                evaluatedNode.setAttribute("type", "opd");
                                evaluatedNode.setAttribute("data", "set");
                                evaluatedNode.setAttribute("instance", "t");

                                for (Iterator<?> iter = objectAssociations.iterator(); iter.hasNext();) {
                                    Element objectAssociation = (Element) iter.next();

                                    // for each object association, get the second object
                                    Element currentSecondObjectAssociationEnd = null;
                                    try {
                                        XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='" +
                                                secondObjectAssociationEndId +"']");
                                        currentSecondObjectAssociationEnd = (Element)path.selectSingleNode(objectAssociation);
                                    } catch (JaxenException e) {
                                        e.printStackTrace();
                                    }

                                    if(currentSecondObjectAssociationEnd != null){
                                        // get the object corresponding to this association end
                                        Element currentSecondObject = null;
                                        try {
                                            XPath path = new JDOMXPath("elements/objects/object[@id='" +
                                                    currentSecondObjectAssociationEnd.getAttributeValue("element-id") +"']");
                                            currentSecondObject = (Element)path.selectSingleNode(domain);
                                        } catch (JaxenException e) {
                                            e.printStackTrace();
                                        }

                                        if(currentSecondObject != null){
                                            // add a node to the set
                                            Element newNode = new Element("node");
                                            newNode.setAttribute("type", "opd");
                                            newNode.setAttribute("data", currentSecondObject.getChildText("name").toUpperCase());
                                            newNode.setAttribute("instance", "t");

                                            evaluatedNode.addContent(newNode);
                                        }
                                    }
                                }
                            }
                        }
                    }

                }
                else{
                    System.out.println("Object \"" + domainObject.getAttributeValue("id") + "\" not found in  snapshot.");
                }

            }

		}
		else if(nodeData.equals("->")){// including, excluding
			Element left = (Element)node.getChildren().get(0);
			Element right = (Element)node.getChildren().get(1);
			String rightData = right.getAttributeValue("data");
			Element set = evaluateExpressionNodeInSnapshot(left, instanceParameters, snapshot, domain, project);

			// with including or excluding, it will return the result of the set
			// after the inclusion or exclusion
			if(rightData.equals("including") || rightData.equals("excluding")){
				Element param = evaluateExpressionNodeInSnapshot(right.getChild("node"), instanceParameters, snapshot, domain, project);
				if(rightData.equals("including")){
					// add the node to the set
					set.addContent(param);
				}
				else{// excluding
					// look for the node in the set
					Element nodeToBeDeleted = null;
					try {
						XPath path = new JDOMXPath("node[lower-case(@data)='"+
								param.getAttributeValue("data").toLowerCase() +"']");
						nodeToBeDeleted = (Element)path.selectSingleNode(set);
					} catch (JaxenException e) {
						e.printStackTrace();
					}
					if(nodeToBeDeleted != null){
						// exclude the node from set
						nodeToBeDeleted.detach();
					}
					else{
						//System.out.println("Object to be excluded \""+ param.getAttributeValue("data")
						//		+"\" not found.");
					}
				}
				evaluatedNode = set;
			}
		}


		else if(nodeData.equals("+") ||
				nodeData.equals("-") ||
				nodeData.equals("*") ||
				nodeData.equals("/")){
			Element left = (Element)node.getChildren().get(0);
			Element right = (Element)node.getChildren().get(1);

            //XMLUtilities.printXML(left);
            //XMLUtilities.printXML(right);

			Element evalLeft = evaluateExpressionNodeInSnapshot(left, instanceParameters, snapshot, domain, project);
			Element evalRight = evaluateExpressionNodeInSnapshot(right, instanceParameters, snapshot, domain, project);
            //XMLUtilities.printXML(evalLeft);
            //XMLUtilities.printXML(evalRight);

			evaluatedNode = (Element)node.clone();
			evaluatedNode.removeContent();
			evaluatedNode.setAttribute("type", "opd");
			evaluatedNode.setAttribute("instance", "t");

			switch(node.getAttributeValue("data").charAt(0)){
			case '+':{
				double result = Double.parseDouble(evalLeft.getAttributeValue("data")) +
					Double.parseDouble(evalRight.getAttributeValue("data"));

				evaluatedNode.setAttribute("data", String.valueOf(result));
			}
			break;
			case '-':{
				double result = Double.parseDouble(evalLeft.getAttributeValue("data")) -
					Double.parseDouble(evalRight.getAttributeValue("data"));

				evaluatedNode.setAttribute("data", String.valueOf(result));
			}
			break;
			case '*':{
				double result = Double.parseDouble(evalLeft.getAttributeValue("data")) *
					Double.parseDouble(evalRight.getAttributeValue("data"));

				evaluatedNode.setAttribute("data", String.valueOf(result));
			}
			break;
			case '/':{
				double result = Double.parseDouble(evalLeft.getAttributeValue("data")) /
					Double.parseDouble(evalRight.getAttributeValue("data"));

				evaluatedNode.setAttribute("data", String.valueOf(result));
			}
			break;
			}
		}
		else if(node.getAttributeValue("type").equals("opd")){
			// check whether the attribute is global (stereotype 'utility')
			String attrData = node.getAttributeValue("data");
			String attrNodeName;
			if(attrData.indexOf("(") > 0){
				// parameterized attribute
				attrNodeName = attrData.substring(0, attrData.indexOf("(")).toLowerCase();
			}
			else{
				attrNodeName = attrData.toLowerCase();
			}
			Element classAttr = null;
			try {
				XPath path = new JDOMXPath("elements/classes/class[stereotype='utility']/attributes/attribute[lower-case(name)='"+
						attrNodeName + "']");
				classAttr = (Element)path.selectSingleNode(project);
			} catch (JaxenException e) {
				e.printStackTrace();
			}
			if(classAttr != null){
				Element utilityClass = classAttr.getParentElement().getParentElement();

//				 look for the domain object
				Element domainObject = null;
				try {
					XPath path = new JDOMXPath("elements/objects/object[class='"+ utilityClass.getAttributeValue("id") +"']");
					domainObject = (Element)path.selectSingleNode(domain);
				} catch (JaxenException e) {
					e.printStackTrace();
				}
				if(domainObject != null){
					// look for the snapshot object
					Element snapshotObject = null;
					try {
						XPath path = new JDOMXPath("objects/object[@id='"+ domainObject.getAttributeValue("id") +"']");
						snapshotObject = (Element)path.selectSingleNode(snapshot);
					} catch (JaxenException e) {
						e.printStackTrace();
					}
					if(snapshotObject != null){
						Element objectAttribute = null;
						try {
							XPath path = new JDOMXPath("attributes/attribute[@id='"+ classAttr.getAttributeValue("id")
									+"' and @class='"+ classAttr.getParentElement().getParentElement().getAttributeValue("id") +"']");
							objectAttribute = (Element)path.selectSingleNode(snapshotObject);
						} catch (JaxenException e) {
							e.printStackTrace();
						}
						if(objectAttribute != null){
							List<?> attrParameters = classAttr.getChild("parameters").getChildren();
							if(attrParameters.size() > 0){
								// parameterized attribute
								String attrParams = attrData.substring(attrData.indexOf('(')+1, attrData.lastIndexOf(')'));
								String query = "attributes/attribute[@id='"+ objectAttribute.getAttributeValue("id") +"']/value/parameterizedValue[";
								StringTokenizer st = new StringTokenizer(attrParams, ",");
								for (Iterator<?> iter = attrParameters.iterator(); iter.hasNext();) {
									Element attrParam = (Element) iter.next();
									String attrParamName = "";
									if(st.hasMoreTokens()){
										attrParamName = st.nextToken().trim().toLowerCase();
									}
									else{
										//System.out.println("There are less parameters than expected at attribute: "+ classAttr.getChildText("name"));
									}

									// look for the attribute parameter value in instance parameters
									Element instanceParameter = null;
									try {
										XPath path = new JDOMXPath("parameter[lower-case(name)='" +
												attrParamName.toLowerCase() +"']");
										instanceParameter = (Element)path.selectSingleNode(instanceParameters);
									} catch (JaxenException e) {
										e.printStackTrace();
									}

									if(instanceParameter != null){

										query += "lower-case(parameters/parameter[@id='"+ attrParam.getAttributeValue("id")
											+"']/value)='"+ instanceParameter.getChildText("value").toLowerCase() +"'";

										if(iter.hasNext()){
											query += " and ";
										}
									}
									else{
										//System.out.println("Error evaluating parameterized value: " +
										//		"instance parameter \""+ attrParamName +"\" not found.");
									}

								}
								query += "]";
								//System.out.println(query);
								if(st.hasMoreTokens()){
									//System.out.println("There are more parameters than expected at attribute: "+ classAttr.getChildText("name"));
								}
								Element parameterizedValue = null;
								try {
									XPath path = new JDOMXPath(query);
									parameterizedValue = (Element)path.selectSingleNode(snapshotObject);
								} catch (JaxenException e) {
									e.printStackTrace();
								}
								if(parameterizedValue != null){
									//returns the value given in the snapshot
									evaluatedNode = (Element)node.clone();
									evaluatedNode.removeContent();
									evaluatedNode.setAttribute("type", "opd");
									evaluatedNode.setAttribute("data", parameterizedValue.getChildText("value"));
									evaluatedNode.setAttribute("instance", "t");
								}
								else{
									//System.out.println("Parameterized value "+ attrData +"not found in snapshot object.");
								}
							}
							else{
								evaluatedNode = (Element)node.clone();
								evaluatedNode.removeContent();
								evaluatedNode.setAttribute("type", "opd");
								evaluatedNode.setAttribute("data", objectAttribute.getChildText("value"));
								evaluatedNode.setAttribute("instance", "t");
							}
						}
						else{
							//System.out.println("Attribute \"" + classAttr.getChildText("name") + "\" not found in snapshot object.");
						}
					}
					else{
						//System.out.println("Object with utility stereotype not found in snapshot.");
					}
				}
				else{
					//System.out.println("Object with utility stereotype not found in domain.");
				}
			}
			else{
				// returns the node itself
				evaluatedNode = (Element)node.clone();
			}
		}
		return evaluatedNode;

	}



    /**
     * This method creates a sequence of object diagrams (snapshots) for a given planning problem using a plan as a guide.
     * Starting from the initial state, the process fires each action in the plan compousing a movie.
     * @param xmlPlan
     * @param problem
     * @return
     */
    public static Element getMovie(Element xmlPlan, Element problem){

        //hold problem, domain and project
        theproblem = problem;
		thedomain = problem.getParentElement().getParentElement();
		theproject = problem.getDocument().getRootElement();

		//set movie node
        Element movie = new Element("movie");
	
        //GET MOVIE
		// get init snapshot
		Element currentSnapshot = null;
		try {
			XPath path = new JDOMXPath(
					"objectDiagrams/objectDiagram[sequenceReference='init']");
			currentSnapshot = (Element) path.selectSingleNode(problem);
		} catch (JaxenException e) {
			e.printStackTrace();
		}
	
		if(currentSnapshot != null){
            // add it to movie (first scene)
            currentSnapshot = (Element)currentSnapshot.clone();
            movie.addContent(currentSnapshot);

			// set id = 1 (first scene)
			currentSnapshot.setAttribute("id", "1");

            // get metrics from inital snapshot
            //gatherMetricsDataset(metrics, null, currentSnapshot);
			
			// iterate over all actions in xmlPlan
			List<?> actions = xmlPlan.getChild("plan").getChildren("action");
			// diagrams sequence
			int i = 2;
			
			JLabel status = ItSIMPLE.getInstance().getPlanSimStatusBar();
			status.setText("Status: Generating movie... (0%)");
			int progressIndex = 0;
			
			for (Iterator<?> iter = actions.iterator(); iter.hasNext();) {
				Element action = (Element) iter.next();
				// get next snap shot
				String actionName = action.getAttributeValue("id");
				List<String> actionParams = new ArrayList<String>();
				for (Iterator<?> iterator = action.getChild("parameters")
						.getChildren("parameter").iterator(); iterator
						.hasNext();) {
					Element param = (Element) iterator.next();
					actionParams.add(param.getAttributeValue("id"));
				}
				//	objectDiagrams	domain
				Element domain = problem.getParentElement()
						.getParentElement();
				Element project = problem.getDocument().getRootElement();

				currentSnapshot = nextState(actionName, actionParams,
						currentSnapshot, domain, project);
				// set the id
				currentSnapshot.setAttribute("id", String.valueOf(i++));
				
                
                //get action start time (restricted so far to INTEGER
                int startTime = i - 1 ;
                try {
                    startTime = Integer.parseInt(action.getChildText("startTime"));
                } catch (Exception e) {
                }
                //get action duration (restricted so far to INTEGER
                int duration = 1;
                try {
                    duration = Integer.parseInt(action.getChildText("duration"));
                } catch (Exception e) {
                }

                int snapshotRef  = startTime + duration;

                // set the sequence time
				//currentSnapshot.getChild("sequenceReference").setText(action.getChildText("startTime"));
                currentSnapshot.getChild("sequenceReference").setText(Integer.toString(snapshotRef));
				
				movie.addContent(currentSnapshot);

                // get metrics from inital snapshot
                //gatherMetricsDataset(metrics, action, currentSnapshot);

                // refresh the status bar
				int progressPercentage = (int)((double)++progressIndex/(double)actions.size() * 100);
				status.setText("Status: Generating movie... ("+ progressPercentage +"%)");

			}
			
			status.setText("Status: Done generating movie!");
		}

        //XMLUtilities.printXML(metrics);


        //PUT METRICS IN THE PLAN
        //xmlPlan.removeChildren("metrics"); //clear metrics
        //xmlPlan.addContent(metrics); //put the metrics in the plan

		//XMLUtilities.printXML(movie);
		return movie;
	}

	
	public static void buildPlanAnalysisDataset(Element analysis, Element xmlPlan, Element problem){
		
		if (analysis != null && xmlPlan != null && problem != null) {
			Element currentSnapshot = null;
			try {
				XPath path = new JDOMXPath(
						"objectDiagrams/objectDiagram[sequenceReference='init']");
				currentSnapshot = (Element) path.selectSingleNode(problem);
			} catch (JaxenException e) {
				e.printStackTrace();
			}
			Element planNode = xmlPlan.getChild("plan");
			List<?> actions = planNode.getChildren("action");
			
			//get the status bar
			JLabel status = ItSIMPLE.getInstance().getPlanSimStatusBar();
			int progressIndex = 0;
			status.setText("Status: Generating chart... (0%)");
			
			if (currentSnapshot != null) {
				for (int i = 0; i <= actions.size(); i++) {

					// get the values first
					List<?> variables = analysis.getChild("variables").getChildren("variable");
					for (Iterator<?> iterator = variables.iterator(); iterator
							.hasNext();) {
						Element variable = (Element) iterator.next();
						if (variable.getAttributeValue("type").equals("attr")) {
							// attribute

							// get the attribute in the current snapshot
							Element snapshotObjectAttr = null;
							try {
								XPath path = new JDOMXPath("objects/object[@id='"+ variable.getChild("object").getAttributeValue("id")
												+ "']/attributes/attribute[@class='"+ variable.getChild("object")
														.getChild("attribute")
														.getAttributeValue("class")
												+ "' and @id='"	+ variable.getChild("object").getChild("attribute")
														.getAttributeValue("id")+ "']");
								snapshotObjectAttr = (Element) path.selectSingleNode(currentSnapshot);
							} catch (JaxenException e) {
								e.printStackTrace();
							}
							if (snapshotObjectAttr != null) {
								if (snapshotObjectAttr.getChild("value").getChildren().size() == 0) {
									// not parameterized attribute
									Element value = new Element("value");
									value.setAttribute("id", String.valueOf(variable.getChild("values").getChildren("value").size()));
									value.setText(snapshotObjectAttr.getChildText("value"));
									variable.getChild("values").addContent(value);
								} else {
									// TODO parameterized attribute
								}
							}
						}
					}

					// get next snap shot
                    if(i < actions.size()){// in the last state, there is no "next state"
                        Element action = (Element)actions.get(i);
                        String actionName = action.getAttributeValue("id");
                        List<String> actionParams = new ArrayList<String>();
                        for (Iterator<?> iterator = action.getChild("parameters")
                                        .getChildren("parameter").iterator(); iterator
                                        .hasNext();) {
                                Element param = (Element) iterator.next();
                                actionParams.add(param.getAttributeValue("id"));
                        }
                        //	objectDiagrams	domain
                        Element domain = problem.getParentElement()
                                        .getParentElement();
                        Element project = problem.getDocument().getRootElement();

                        currentSnapshot = nextState(actionName, actionParams,
                                        currentSnapshot, domain, project);

                        // refresh the status bar
                        int progressPercentage = (int)((double)++progressIndex/(double)actions.size() * 100);
                        status.setText("Status: Generating chart... ("+ progressPercentage +"%)");
                    }
									
				}
				
				status.setText("Status: Done generating chart!");

			} 
			
			else {
				System.out.println("No initial snapshot was found in problem \""
								+ problem.getChildText("name") + "\".");
			}

            XMLUtilities.printXML(analysis);
		}
				
	}
	

    public static List<ChartPanel> drawCharts(Element analysis, Element problem){
		List<ChartPanel> charts = new ArrayList<ChartPanel>();
		
		// draw the charts
		List<?> variables = analysis.getChild("variables").getChildren("variable");
		for (Iterator<?> iter = variables.iterator(); iter.hasNext();) {
			Element variable = (Element) iter.next();
			
			Element domainObject = null;
			try {
				XPath path = new JDOMXPath("elements/objects/object[@id='"+ variable.getChild("object").getAttributeValue("id") +"']");		
				domainObject = (Element)path.selectSingleNode(problem.getParentElement().getParentElement());						
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			
			Element attribute = variable.getChild("object").getChild("attribute");
			Element classAttr = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[@id='"+ attribute.getAttributeValue("class")
						+"']/attributes/attribute[@id='"+ attribute.getAttributeValue("id") +"']");			
				classAttr = (Element)path.selectSingleNode(problem.getDocument());						
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			
			String chartTitle = domainObject.getChildText("name")+ "." +classAttr.getChildText("name");
                        
			
			if(variable.getAttributeValue("type").equals("attr")){
				// attribute
				String attrType = variable.getChild("object").getChild("attribute").getAttributeValue("type");
				if(attrType.equals("1")){ //BOOLEAN attribute
					
					XYSeriesCollection dataset = new XYSeriesCollection();
					XYSeries series = new XYSeries("Boolean");
					int stepIndex = 0;
					for (Iterator<?> iterator = variable.getChild("values").getChildren("value").iterator(); iterator
							.hasNext();) {
						Element value = (Element) iterator.next();
						series.add(stepIndex++, (value.getText().equals("false") ?0 :1));
					}
					dataset.addSeries(series);
					
					JFreeChart chart = ChartFactory.createXYStepChart(chartTitle, "Values", "Steps", dataset, PlotOrientation.VERTICAL, false, true, false); 
					
					XYPlot plot = (XYPlot)chart.getPlot(); 
					NumberAxis domainAxis = new NumberAxis("Steps"); 
					domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); 
					domainAxis.setAutoRangeIncludesZero(false); 
					plot.setDomainAxis(domainAxis);
					   
					String[] values = {"false", "true"}; 
					SymbolAxis rangeAxis = new SymbolAxis("Values", values); 
					plot.setRangeAxis(rangeAxis);
					
					ChartPanel chartPanel = new ChartPanel(chart);					
					chartPanel.setPreferredSize(new Dimension(chartPanel.getSize().width, 175));
					 
					charts.add(chartPanel);
					 
				}
				else if(attrType.equals("2") || attrType.equals("3")){ //NUMERIC attributes
					
					XYSeriesCollection dataset = new XYSeriesCollection(); 
                                        XYSeries series = new XYSeries("variable");
                                        int stepIndex = 0;
                                        for (Iterator<?> iterator = variable.getChild("values").getChildren("value").iterator(); iterator.hasNext();) {
                                                Element value = (Element) iterator.next();
                                                series.add(stepIndex++, Double.parseDouble(value.getText()));	                	
                                        }
                                        dataset.addSeries(series);

                                        JFreeChart chart = ChartFactory.createXYLineChart(chartTitle, "Steps",  "Values", dataset, PlotOrientation.VERTICAL, false, true, false);

                                        XYPlot xyPlot = (XYPlot)chart.getPlot(); 
                                        XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) xyPlot.getRenderer(); 
                                        renderer.setShapesVisible(true); 
                                        renderer.setShapesFilled(true); 

                                        NumberAxis rangeAxis = (NumberAxis) xyPlot.getRangeAxis();	                 
                                        rangeAxis.setAutoRangeIncludesZero(true); 
                                        rangeAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                                        if(attrType.equals("2")){
                                                NumberAxis domainAxis = (NumberAxis) xyPlot.getDomainAxis();
                                                domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                                        }

                                        ChartPanel chartPanel = new ChartPanel(chart);
					 
					charts.add(chartPanel);
				}
				else if(!attrType.equals("4")){//NOT PRIMITIVE attributes
					
					Element attrClass = null;
					try {
						XPath path = new JDOMXPath("project/elements/classes/class[@id='"+ classAttr.getChildText("type") +"']");			
						attrClass = (Element)path.selectSingleNode(problem.getDocument());						
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					
					if(attrClass != null){
						
						List<Element> classes = XMLUtilities.getClassDescendents(attrClass);						
						String query = "elements/objects/object[";
						for (Iterator<?> iterator = classes.iterator(); iterator
								.hasNext();) {
							Element childClass = (Element) iterator.next();
							query += "class='"+ childClass.getAttributeValue("id") +"'";

							query += " or ";// the last or is for the parent class							
							
						}
						query += "class='"+ attrClass.getAttributeValue("id") +"']";
						
						// get all the objects of all descendant classes, including the parent class
						List<?> objects = null;
						try {
							XPath path = new JDOMXPath(query);
							objects = path.selectNodes(problem.getParentElement().getParentElement());
						} catch (JaxenException e) {
							e.printStackTrace();
						}
						if(objects.size() > 0){
							//build a list with all the objects names							
							String[] names = new String[objects.size()+1];// the array is for the axis
							names[0] = "null";// default null value
							
							List<String> objectNames = new ArrayList<String>();
							int i = 1;
							for (Iterator<?> iterator = objects.iterator(); iterator
									.hasNext();) {
								Element object = (Element) iterator.next();
								names[i++] = object.getChildText("name");
								objectNames.add(object.getChildText("name").toLowerCase());
							}
							
							XYSeriesCollection dataset = new XYSeriesCollection();
							XYSeries series = new XYSeries("Objects");
							int stepIndex = 0;
							for (Iterator<?> iterator = variable.getChild("values").getChildren("value").iterator(); iterator
									.hasNext();) {
								Element value = (Element)iterator.next();
								
								series.add(stepIndex++, objectNames.indexOf(value.getText().toLowerCase())+1);
							}
							dataset.addSeries(series);
							
							
							// draw the chart
							JFreeChart chart = ChartFactory.createXYStepChart(chartTitle, "Objects", "Steps", dataset, PlotOrientation.VERTICAL, false, true, false); 
							
							XYPlot plot = (XYPlot)chart.getPlot(); 
							NumberAxis domainAxis = new NumberAxis("Steps"); 
							domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); 
							domainAxis.setAutoRangeIncludesZero(false); 
							plot.setDomainAxis(domainAxis);							
							   
							 
							SymbolAxis rangeAxis = new SymbolAxis("Objects", names); 
							plot.setRangeAxis(rangeAxis); 
							
							ChartPanel chartPanel = new ChartPanel(chart);
							 
							charts.add(chartPanel);
						}
					}
				}
			}
		}
		
		
		return charts;
	}
	
    /**
     * This method creates a html version of the plan analysis with charts based on the seceted variables.
     * @param analysis the xml node with the values of all attributes being analyzed
     * @param the plan (in a xml format)
     * @param the problem (in xml format)
     * @return a string with representing the content of a html file.
     */
    public static String createHTMLPlanAnalysis(Element analysis, Element xmlPlan, Element problem){
               
        String html ="<html> \n" +
                     "   <head> \n" +
                     "      <meta http-equiv=\"Content-Type\" content=\"text/html; charset=iso-8859-1\" /> \n" +
                     "      <title>itSIMPLE Plan Analysis - Variable Tracking</title> \n" +
                     "      <script language=\"JavaScript\" src=\"jsclass/FusionCharts.js\"></script> \n" +
                     "   </head> \n" +
                     "   <body> \n";

        //Set html Title
        html += "       <h1 align=\"center\">"+ problem.getChildText("name")+"</h1> <br><br> \n";


        Integer chartIndex = 0;
        // draw the charts
		List<?> variables = analysis.getChild("variables").getChildren("variable");
		for (Iterator<?> iter = variables.iterator(); iter.hasNext();) {
			Element variable = (Element) iter.next();
			//Get object (from domain, to get the name and properties)
			Element domainObject = null;
			try {
				XPath path = new JDOMXPath("elements/objects/object[@id='"+ variable.getChild("object").getAttributeValue("id") +"']");		
				domainObject = (Element)path.selectSingleNode(problem.getParentElement().getParentElement());						
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			//get attribute (from domain, to get the name and its properties)
			Element attribute = variable.getChild("object").getChild("attribute");
			Element classAttr = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[@id='"+ attribute.getAttributeValue("class")
						+"']/attributes/attribute[@id='"+ attribute.getAttributeValue("id") +"']");			
				classAttr = (Element)path.selectSingleNode(problem.getDocument());						
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			
			String chartTitle = domainObject.getChildText("name")+ "." +classAttr.getChildText("name");
                        //html += "       <h2><img src=\"LogoSmalTest.png\"> " + chartTitle + "</h2> \n"; 
			html += "       <h2>" + chartTitle + "</h2> \n"; 
                        
			if(variable.getAttributeValue("type").equals("attr")){
				// attribute
				String attrType = variable.getChild("object").getChild("attribute").getAttributeValue("type");
                                String attrTypeStr = "";
                                String dataXML = ""; //XML data in the FusionChart format
				
                                //XMLUtilities.printXML(variable);
                                
                                //BOOLEAN attribute
                                if(attrType.equals("1")){
                                    attrTypeStr = "Boolean";
                                    
                                    int horizLines = variable.getChild("values").getChildren("value").size()-1;
                                    //Set the string containing the xml data of the chart (FusionChart format)
                                    dataXML ="<graph caption='Attribute " + chartTitle + "' subcaption='(" + attrTypeStr + ")' " +
                                                "xAxisName='Steps' yAxisName='"+classAttr.getChildText("name")+"' yAxisMinValue='0' yAxisMaxValue='3' " +
                                                "zeroPlaneThickness='0' showhovercap='0' numVDivLines='"+horizLines+"' " +
                                                "numdivlines='2' showLimits='0' showDivLineValue='0' decimalPrecision='0' " +
                                                "formatNumberScale='0' showNames='1' showValues='0' showAlternateHGridColor='1' " +
                                                "AlternateHGridColor='ff5904' divLineColor='FF0000' divLineAlpha='20' alternateHGridAlpha='5' "  +
                                                "showShadow='0'>";
                                    
                                    String categories = "<categories>";
                                    String dataSetTrue = "<dataset seriesname='True' color='1D8BD1' showValue='1' alpha='100' showAnchors='1' lineThickness='5'>";
                                    String dataSetFalse = "<dataset seriesname='False' color='FF0000' showValue='1' alpha='80' showAnchors='1' lineThickness='5'>";
                                      
                                    //read each value (X,Y) and creat each set of the graph
                                    for (Iterator<?> iterator = variable.getChild("values").getChildren("value").iterator(); iterator.hasNext();) {
                                        Element value = (Element) iterator.next();
                                        
                                        categories += "<category name='" + value.getAttributeValue("id") + "' />";
                                        dataSetTrue += "<set value='2' alpha='" + (value.getText().equals("false") ?"1" :"100") + "' />";
                                        dataSetFalse += "<set value='1' alpha='" + (value.getText().equals("false") ?"100" :"1") + "' />";
                                        
                                        //Just to create a final line to show what happen after the plan.
                                        if (!iterator.hasNext()) {
                                            //int duringLastStep = Integer.parseInt(value.getAttributeValue("id")) + 1;
                                            //categories += "<category name='" + duringLastStep + "' />";
                                            categories += "<category name='' />";
                                            dataSetTrue += "<set value='2' alpha='" + (value.getText().equals("false") ?"1" :"100") + "' />";
                                            dataSetFalse += "<set value='1' alpha='" + (value.getText().equals("false") ?"100" :"1") + "' />";
                                        }
                                        
                                    }
                                    
                                    categories += "</categories>";
                                    dataSetTrue += "</dataset>";
                                    dataSetFalse += "</dataset>";
                                    
                                    dataXML += categories + dataSetTrue + dataSetFalse;
                                    
                                    dataXML +="<trendlines>" +
                                            "<line startvalue='2' displayValue='true' color='009999' thickness='1' isTrendZone='0'/>" +
                                            "<line startvalue='1' displayValue='false' color='FF0000' thickness='1' isTrendZone='0'/>" +
                                            "</trendlines>";
                                    
                                    dataXML +="</graph>";
                                    
                                    //Create the html chart component with the dataXML
                                    html += "       <table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n" +
                                            "           <tr> \n" +
                                            "               <td valign=\"top\" class=\"text\" align=\"center\"> \n" + 
                                            "                   <div id=\"chartdiv" + chartIndex + "\" align=\"center\">" + chartTitle + "</div> \n" +
                                            "                       <script type=\"text/javascript\"> \n" +
                                            "                           var chart"+chartIndex+" = new FusionCharts(\"charts/FCF_MSLine.swf\", \"ChartId\", \"750\", \"400\"); \n" +
                                            "                           chart"+chartIndex+".setDataXML(\"" + dataXML + "\"); \n" +
                                            "                           chart"+chartIndex+".render(\"chartdiv" + chartIndex + "\"); \n" +
                                            "                       </script> \n" +
                                            "               </td> \n" +
                                            "           </tr> \n" +
                                            "       </table> \n";
                
				}
                                
                //NUMERIC attributes
				else if(attrType.equals("2") || attrType.equals("3")){
                                    
                                    if (attrType.equals("2"))
                                        attrTypeStr = "Integer";
                                    else
                                        attrTypeStr = "Float";
                                    
                                                                       //Set the string containing the xml data of the chart (FusionChart format)
                                    dataXML ="<graph caption='Attribute " + chartTitle + "' subcaption='(" + attrTypeStr + ")' " +
                                                "xAxisName='Steps' yAxisMinValue='0' yAxisName='Value' " + 
                                                "decimalPrecision='0' formatNumberScale='0' showNames='1' " +
                                                "showValues='0' showAlternateHGridColor='1' AlternateHGridColor='ff5904' " +
                                                "divLineColor='ff5904' divLineAlpha='20' alternateHGridAlpha='5' >";
                                                    
                                    //read each value (X,Y) and creat each set of the graph
                                    for (Iterator<?> iterator = variable.getChild("values").getChildren("value").iterator(); iterator.hasNext();) {
                                        Element value = (Element) iterator.next();
                                        dataXML += "<set name='" + value.getAttributeValue("id") + "' value='" + value.getText() + "' hoverText='" + value.getAttributeValue("id") + "'/>";
                                        //html += "       <p>Chart Point: (X=" + value.getAttributeValue("id") + "; Y=" + value.getText() + ")</p> \n";                	
                                        
                                        //extend the X Axis by one unit for convinience when viewing the last points
                                        if (!iterator.hasNext()) {
                                            int lastIndex = Integer.parseInt(value.getAttributeValue("id")) + 1;
                                            dataXML += "<set name='" + lastIndex + "' value='' hoverText='" + lastIndex + "'/>";
                                        } 
                                    }
                                    
                                    dataXML +="</graph>";
                                    
                                    //Create the html chart component with the dataXML
                                    html += "       <table width=\"98%\" border=\"0\" cellspacing=\"0\" cellpadding=\"3\" align=\"center\"> \n" +
                                            "           <tr> \n" +
                                            "               <td valign=\"top\" class=\"text\" align=\"center\"> \n" + 
                                            "                   <div id=\"chartdiv" + chartIndex + "\" align=\"center\">" + chartTitle + "</div> \n" +
                                            "                       <script type=\"text/javascript\"> \n" +
                                            "                           var chart"+chartIndex+" = new FusionCharts(\"charts/FCF_Line.swf\", \"ChartId\", \"750\", \"400\"); \n" +
                                            "                           chart"+chartIndex+".setDataXML(\"" + dataXML + "\"); \n" +
                                            "                           chart"+chartIndex+".render(\"chartdiv" + chartIndex + "\"); \n" +
                                            "                       </script> \n" +
                                            "               </td> \n" +
                                            "           </tr> \n" +
                                            "       </table> \n";
				}
                                
                //NOT PRIMITIVE attributes
				else if(!attrType.equals("4")){
					
                                    //Get the corresponding class
					Element attrClass = null;
					try {
						XPath path = new JDOMXPath("project/elements/classes/class[@id='"+ classAttr.getChildText("type") +"']");			
						attrClass = (Element)path.selectSingleNode(problem.getDocument());						
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					
					if(attrClass != null){
                                                attrTypeStr = attrClass.getChildText("name");
                                            
						List<Element> classes = XMLUtilities.getClassDescendents(attrClass);						
						String query = "elements/objects/object[";
						for (Iterator<?> iterator = classes.iterator(); iterator
								.hasNext();) {
							Element childClass = (Element) iterator.next();
							query += "class='"+ childClass.getAttributeValue("id") +"'";

							query += " or ";// the last or is for the parent class							
							
						}
						query += "class='"+ attrClass.getAttributeValue("id") +"']";
						
						// get all the objects of all descendant classes, including the parent class
						List<?> objects = null;
						try {
							XPath path = new JDOMXPath(query);
							objects = path.selectNodes(problem.getParentElement().getParentElement());
						} catch (JaxenException e) {
							e.printStackTrace();
						}
						if(objects.size() > 0){
							//build a list with all the objects names							
							String[] names = new String[objects.size()+1];// the array is for the axis
							names[0] = "null";// default null value
							
							List<String> objectNames = new ArrayList<String>();
							int i = 1;
							for (Iterator<?> iterator = objects.iterator(); iterator
									.hasNext();) {
								Element object = (Element) iterator.next();
								names[i++] = object.getChildText("name");
								objectNames.add(object.getChildText("name").toLowerCase());
							}
							
                                                        /**
							XYSeriesCollection dataset = new XYSeriesCollection();
							XYSeries series = new XYSeries("Objects");
							int stepIndex = 0;
							for (Iterator<?> iterator = variable.getChild("values").getChildren("value").iterator(); iterator
									.hasNext();) {
								Element value = (Element)iterator.next();
								
								series.add(stepIndex++, objectNames.indexOf(value.getText().toLowerCase())+1);
							}
							dataset.addSeries(series);
							
							
							// draw the chart
							JFreeChart chart = ChartFactory.createXYStepChart(chartTitle, "Objects", "Steps", dataset, PlotOrientation.VERTICAL, false, true, false); 
							
							XYPlot plot = (XYPlot)chart.getPlot(); 
							NumberAxis domainAxis = new NumberAxis("Steps"); 
							domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits()); 
							domainAxis.setAutoRangeIncludesZero(false); 
							plot.setDomainAxis(domainAxis);							
							   
							 
							SymbolAxis rangeAxis = new SymbolAxis("Objects", names); 
							plot.setRangeAxis(rangeAxis); 
							
							ChartPanel chartPanel = new ChartPanel(chart);
							 
							charts.add(chartPanel);
                                                        **/
						}
					}
				}
			}
            chartIndex++;
		}
            
        html = html + "  </body> \n"+
                "</html>";

        /**
        String html ="<html> \n" +
                 "   <body> \n" +
                 "      <h1>Some header</h1> \n" +
                 "      <p>A paragraph with a <a href=\"http://www.google.com\">link</a>.</p> \n" +
                 "  </body> \n"+
                "</html>";
         **/
            
        return html;
    }
        

	/**
     * This method fill out the metrics' datasets for a given snapshot
     * @param metrics the set of metrics been capture/analyzed
     * @param action the action that produced the snapshot (null when is the init)
     * @param snapshot the snapshot
     */
    public static void setMetricDatasetsForSnapshot(Element metrics, Element action, Element snapshot){
        
        if (metrics != null && snapshot != null) {
			
			//get the status bar
			//JLabel status = ItSIMPLE.getInstance().getPlanSimStatusBar();
			//int progressIndex = 0;
			//status.setText("Status: Generating chart... (0%)");
            for (Iterator it = metrics.getChildren().iterator(); it.hasNext();) {
                Element eacMetric = (Element)it.next();
                
                //Sets the sequence reference
                String sequenceReference = snapshot.getChildText("sequenceReference");
                //System.out.println(sequenceReference);
                if (sequenceReference.equals("init")){
                    sequenceReference = "0";
                }else{
                    try {
                        //Parse to int (TODO, treat the other cases)
                        Integer ref = Integer.parseInt(sequenceReference);
                        sequenceReference = ref.toString();
                    } catch (Exception e) {
                        System.out.println("Unable to identify reference "+sequenceReference+" on the plan");
                    }
                }

                //Check the type
                //VARIABLE - ATTRIBUE
                if (eacMetric.getChildText("type").equals("variable")) {
					// variable
                    /**
                     * qualityMetric
                     *  name
                     *  type
                     *  variable
                     *      chosenVariable
                     *          object
                     *              variable
                     *  dataset
                     *      set
                     *      set
                     *      ...
                     */
                    Element chosenVariable = eacMetric.getChild("variable").getChild("chosenVariable");
                    Element chosenObject = chosenVariable.getChild("object");
                    Element chosenAttribute =chosenObject.getChild("attribute");

                    // get the attribute in the current snapshot
                    Element snapshotObjectAttr = null;
                    try {
                        XPath path = new JDOMXPath("objects/object[@id='"+ chosenObject.getAttributeValue("id")
                                        + "']/attributes/attribute[@class='"+ chosenAttribute.getAttributeValue("class")
                                        + "' and @id='"	+ chosenAttribute.getAttributeValue("id")+ "']");
                        snapshotObjectAttr = (Element) path.selectSingleNode(snapshot);
                    } catch (JaxenException e) {
                        e.printStackTrace();
                    }
                    if (snapshotObjectAttr != null) {
                        if (snapshotObjectAttr.getChild("value").getChildren().size() == 0) {
                            // not parameterized attribute
                            Element set = new Element("set");
                            set.setAttribute("name", sequenceReference);
                            set.setAttribute("value", snapshotObjectAttr.getChildText("value"));
                            String comments = "";
                            if (action != null){
                                comments = action.getAttributeValue("id");
                            }
                            else{
                                comments = "init";
                            }
                            set.setAttribute("comments", comments);
                            eacMetric.getChild("dataset").addContent(set);
                        } else {
                            //TODO: parameterized attribute
                        }
                    }
				}
                //If it's a expression
                //TODO: evaluate an expression with objects and global variables.
                else if (eacMetric.getChildText("type").equals("expression")) {
                    // expression
                    /**
                     * qualityMetric
                     *  name
                     *  type
                     *  expression
                     *      rule
                     *  dataset
                     *      set
                     *      set
                     *      ...
                     */
                    Element expressionNode = eacMetric.getChild("expression");
                    String rule = expressionNode.getChildText("rule");
                    ExpressionTreeBuilder treeBuilder = new ExpressionTreeBuilder(rule);
                    Element expTree = treeBuilder.getExpressionTree();
                    //XMLUtilities.printXML(expTree);

                    Element eval = evaluateExpressionNodeInSnapshot(expTree, null, snapshot, thedomain, theproject);
                    //XMLUtilities.printXML(eval);

                    Element set = new Element("set");
                    set.setAttribute("name", sequenceReference);
                    if (eval != null){
                        set.setAttribute("value", eval.getAttributeValue("data"));
                    }
                    //put zero if something went wrong
                    else{
                        set.setAttribute("value", "0");
                        System.out.println("System cound not parse the metric expression at snapshot reference '"+ sequenceReference +"'");
                    }

                    String comments = "";
                    if (action != null){comments = action.getAttributeValue("id");}
                    else{comments = "init";}

                    set.setAttribute("comments", comments);
                    eacMetric.getChild("dataset").addContent(set);

                }

                //if it's an actionCounter
                else if (eacMetric.getChildText("type").equals("actionCounter")) {
                    // actionCounter
                    /**
                     * qualityMetric
                     *  name
                     *  type
                     *  actionCounter
                     *      chosenOperator
                     *          parameters
                     *              parameter
                     *  dataset
                     *      set
                     *      set
                     *      ...
                     */
                    Element chosenOperator = eacMetric.getChild("actionCounter").getChild("chosenOperator");
                    Element chosenParameters =chosenOperator.getChild("parameters");
                    Element counterValue = eacMetric.getChild("actionCounter").getChild("value");


                    if(action != null){
                        String operatorName = chosenOperator.getAttributeValue("name").trim().toLowerCase();
                        String actionName = action.getAttributeValue("id").trim().toLowerCase();
                        if (actionName.equals(operatorName)){
                            boolean canCount = true;
                            //check parameters
                            List actionParametersList = action.getChild("parameters").getChildren();
                            List chosenParametersList = chosenParameters.getChildren();
                            //Check each parameter to see if is the chosen action
                            for (int i = 0; i < actionParametersList.size(); i++) {
                                Element actionPar = (Element)actionParametersList.get(i);
                                Element chosenPar = (Element)chosenParametersList.get(i);

                                String actionParStr = actionPar.getAttributeValue("id").trim().toLowerCase();
                                String chosenParStr = chosenPar.getAttributeValue("objectName").trim().toLowerCase();

                                if (!chosenParStr.equals("")){//if there is something specify
                                    if(!actionParStr.equals(chosenParStr)){//if it is not the parameter
                                        canCount = false;
                                    }
                                }
                            }

                            //update the counter
                            if (canCount){
                                int thevalue = Integer.parseInt(counterValue.getText());
                                thevalue += 1;
                                counterValue.setText(Integer.toString(thevalue));
                            }

                        }
                    }

                    //add set element to dataset
                    Element set = new Element("set");
                    set.setAttribute("name", sequenceReference);
                    set.setAttribute("value", counterValue.getText());
                    String comments = "";
                    if (action != null){
                        comments = action.getAttributeValue("id");
                    }
                    else{
                        comments = "init";
                    }
                    set.setAttribute("comments", comments);
                    eacMetric.getChild("dataset").addContent(set);

                }



  
            }
			
            
		}
        //XMLUtilities.printXML(metrics);
				
	}


    /**
     * This method fill out the metrics' data set only when they do not require simulation (e.g., action counter)
     * @param metrics the reference metrics (domain and problem, all together). It is the result of createMetricNode method
     * @param xmlPlan the plan
     */
    public static void setMetricDatasetsForNoSimulation(Element metrics, Element xmlPlan){

        if (metrics != null && xmlPlan != null) {

            //create the first set/value 0 which represent the initial state
            for (Iterator it = metrics.getChildren().iterator(); it.hasNext();) {
                Element eacMetric = (Element)it.next();

                //add set element to dataset
                Element set = new Element("set");
                set.setAttribute("name", "0");
                set.setAttribute("value", "0");
                String comments = "init";
                set.setAttribute("comments", comments);
                eacMetric.getChild("dataset").addContent(set);
            }


            //create the other set/value for the plan
            List<Element> actions = xmlPlan.getChild("plan").getChildren("action");
            int planIndex = 0;

            for (Iterator<Element> ita = actions.iterator(); ita.hasNext();) {
                Element action = ita.next();

                //analyze each metric
                for (Iterator it = metrics.getChildren().iterator(); it.hasNext();) {
                    Element eacMetric = (Element)it.next();

                    //get action start time (restricted so far to INTEGER)
                    int startTime = planIndex ;
                    try {
                        startTime = Integer.parseInt(action.getChildText("startTime"));
                    } catch (Exception e) {
                    }
                    //get action duration (restricted so far to INTEGER)
                    int duration = 1;
                    try {
                        duration = Integer.parseInt(action.getChildText("duration"));
                    } catch (Exception e) {
                    }
                    //Action index
                    int snapshotRef  = startTime + duration;

                    //Sets the sequence reference
                    String sequenceReference = Integer.toString(snapshotRef);
                    //System.out.println(sequenceReference);

                    //if it's an actionCounter
                    if (eacMetric.getChildText("type").equals("actionCounter")) {
                        // variable
                        /**
                         * qualityMetric
                         *  name
                         *  type
                         *  actionCounter
                         *      chosenOperator
                         *          parameters
                         *              parameter
                         *              ...
                         *  dataset
                         *      set
                         *      set
                         *      ...
                         */
                        Element chosenOperator = eacMetric.getChild("actionCounter").getChild("chosenOperator");
                        Element chosenParameters =chosenOperator.getChild("parameters");
                        Element counterValue = eacMetric.getChild("actionCounter").getChild("value");

                        String operatorName = chosenOperator.getAttributeValue("name").trim().toLowerCase();
                        String actionName = action.getAttributeValue("id").trim().toLowerCase();
                        if (actionName.equals(operatorName)){
                            boolean canCount = true;
                            //check parameters
                            List actionParametersList = action.getChild("parameters").getChildren();
                            List chosenParametersList = chosenParameters.getChildren();
                            //Check each parameter to see if is the chosen action
                            for (int i = 0; i < actionParametersList.size(); i++) {
                                Element actionPar = (Element)actionParametersList.get(i);
                                Element chosenPar = (Element)chosenParametersList.get(i);

                                String actionParStr = actionPar.getAttributeValue("id").trim().toLowerCase();
                                String chosenParStr = chosenPar.getAttributeValue("objectName").trim().toLowerCase();

                                if (!chosenParStr.equals("")){//if there is something specify
                                    if(!actionParStr.equals(chosenParStr)){//if it is not the parameter
                                        canCount = false;
                                    }
                                }
                            }

                            //update the counter
                            if (canCount){
                                int thevalue = Integer.parseInt(counterValue.getText());
                                thevalue += 1;
                                counterValue.setText(Integer.toString(thevalue));
                            }

                        }

                        //add set element to dataset
                        Element set = new Element("set");
                        set.setAttribute("name", sequenceReference);
                        set.setAttribute("value", counterValue.getText());
                        String comments = action.getAttributeValue("id");
                        set.setAttribute("comments", comments);
                        eacMetric.getChild("dataset").addContent(set);

                    }
                    //OTHER CASES (FUTURE)


                }

                planIndex++;
            }
            



		}
        //XMLUtilities.printXML(metrics);

	}


    /**
     * This method creates the metrics node for containing all metrics (together) from domain and problem
     * for filling the datasets
     * @param problem
     * @param domain
     */
    public static Element createMetricsNode(Element problem, Element domain){

        //hold problem, domain and project
        theproblem = problem;
		thedomain = domain;
		theproject = domain.getDocument().getRootElement();


        //METRICS PREPARATION
        //set metrics node
        Element metrics = new Element("metrics");
        //collect all metrics
        List<Element> metricList = new ArrayList<Element>();
        //gather metrics from problem
        List problemMetrics = null;
        try {
            XPath path = new JDOMXPath("metrics/qualityMetric");
            problemMetrics = path.selectNodes(problem);
        } catch (JaxenException e) {
            e.printStackTrace();
        }
        if(problemMetrics !=null){
            metricList.addAll(problemMetrics);
        }
        //gather metrics from domain
        List domainMetrics = null;
        try {
            XPath path = new JDOMXPath("metrics/qualityMetric");
            domainMetrics = path.selectNodes(problem.getParentElement().getParentElement());
        } catch (JaxenException e) {
            e.printStackTrace();
        }
        if(domainMetrics !=null){
            metricList.addAll(domainMetrics);
        }
        for (Iterator it = metricList.iterator(); it.hasNext();) {
            Element eaMetric = (Element)it.next();

            Element refMetric = new Element(eaMetric.getName());
            /**
             * qualityMetric
             *  name
             *  type
             *  type node (expession, variavle, or actionCounter)
             *  dataset
             */
            //set main attributes of node quality metric
            Element levelParent = eaMetric.getParentElement().getParentElement();
            refMetric.setAttribute("level",levelParent.getName());
            refMetric.setAttribute("id", eaMetric.getAttributeValue("id"));
            //cloning the basics
            refMetric.addContent((Element)eaMetric.getChild("name").clone()); //name
            refMetric.addContent((Element)eaMetric.getChild("type").clone()); //type
            refMetric.addContent((Element)eaMetric.getChild("weight").clone()); //weight
            String type = eaMetric.getChildText("type");
            refMetric.addContent((Element)eaMetric.getChild(type).clone()); //type node (variable, expression, actionCounter)

            refMetric.addContent((Element)eaMetric.getChild("preferenceFunction").clone()); //preferenceFunction

            //facilitate search and action counter, put the name of the
            //  operator at the qualitMetric and also the name of parameters
            if(type.equals("actionCounter")){
                    //find operator in the project
                    Element chosenOperator = refMetric.getChild("actionCounter").getChild("chosenOperator");
                    Element chosenParameters = chosenOperator.getChild("parameters");

                    //Put the name of the operator
                    Element operator = null;
                    try {
                        XPath path = new JDOMXPath("project/elements/classes/class[@id='"+chosenOperator.getAttributeValue("class")+"']/operators/operator[@id='"+ chosenOperator.getAttributeValue("id")+"']");
                        operator = (Element) path.selectSingleNode(problem.getDocument());
                    } catch (JaxenException e) {
                        e.printStackTrace();
                    }
                    if (operator!=null){
                        //set the name as a attribute of the node "name"
                        chosenOperator.setAttribute("name", operator.getChildText("name"));
                    }

                    //put the name of the parameters
                    for (Iterator it1 = chosenParameters.getChildren().iterator(); it1.hasNext();) {
                        Element param = (Element)it1.next();
                        String objectName = "";
                        if (!param.getAttributeValue("object").equals("")){
                            //find object in the domain
                            Element pObject = null;
                            try {
                                XPath path = new JDOMXPath("elements/objects/object[@id='"+param.getAttributeValue("object")+"']");
                                pObject = (Element) path.selectSingleNode(problem.getParentElement().getParentElement());
                            } catch (JaxenException e) {
                                e.printStackTrace();
                            }
                            if (pObject!=null){
                                objectName = pObject.getChildText("name");
                            }
                        }
                        param.setAttribute("objectName", objectName);
                    }


                    //add this node for storing the counter amount (total for example
                    Element value = new Element("value");
                    value.setText("0");
                    refMetric.getChild("actionCounter").addContent(value);
            }

            refMetric.addContent(new Element("dataset"));
            metrics.addContent(refMetric);


        }
        //XMLUtilities.printXML(metrics);


        return metrics;
    }


    /**
     * This method fill out the metric dataset for the problem solution given the plan. 
     * Since some metrics cases require simulation, the methods uses the movie for 
     * creating the sets/values. If a movie is provided it uses for fill out the 
     * value sets; otherwise,the methods can create it by itself.
     * @param metrics the reference metrics (domain and problem, all together). It is the result of createMetricNode method
     * @param xmlPlan the plan
     * @param problem the problem
     * @param domain the domain
     * @param movie the movie (can also be null or a given one)
     */
    public static void createMetricDatasets(Element metrics, Element xmlPlan, Element problem, Element domain, Element movie){

        //1. Check if the metrics need simualtion.
        //  The cases in which simulation is required are the types: variable, expression
        //  The cases that simulation is NOT required: actionCounter.

        boolean needSilumation = false;
        List simulationMetrics = null;
        try {
            XPath path = new JDOMXPath("qualityMetric[type='variable' or type='expression']");
            simulationMetrics = path.selectNodes(metrics);
        } catch (JaxenException e) {
            e.printStackTrace();
        }
        if (simulationMetrics!=null && simulationMetrics.size() > 0){
            needSilumation = true;
        }

        //if we need simulation we need the movie
        if (needSilumation){
            if (movie == null){
                movie = getMovie(xmlPlan, problem);
            }
            Element snapshotInit = null;
            try {
                XPath path = new JDOMXPath("objectDiagram[sequenceReference='init']");
                snapshotInit = (Element) path.selectSingleNode(movie);
            } catch (JaxenException e) {
                e.printStackTrace();
            }
            if (snapshotInit != null){
                // get metrics from inital snapshot
                setMetricDatasetsForSnapshot(metrics, null, snapshotInit);

                int actionIndex = 0;
                List<Element> actions = xmlPlan.getChild("plan").getChildren("action");
                List<Element> snapshots = movie.getChildren("objectDiagram");
                for (Iterator<?> iter = snapshots.iterator(); iter.hasNext();) {
                    Element snapshot = (Element) iter.next();
                    //jump init scene because its was already done
                    if(!snapshot.getChildText("sequenceReference").equals("init")){

                        Element currentAction = actions.get(actionIndex);
                        // get metrics from inital snapshot
                        setMetricDatasetsForSnapshot(metrics, currentAction, snapshot);

                        actionIndex++;
                    }
                }
            }


        }
        //in case there is no need for simulation
        else{
            //System.out.println("No simulation Case");
            setMetricDatasetsForNoSimulation(metrics, xmlPlan);
        }

        //XMLUtilities.printXML(metrics);

    }





    /*public static void main(String[] args){
		Element project = null;
		try {
			project = XMLUtilities.readFromFile("examples/LogisticDomainv2.xml").getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element domain = (Element)project.getChild("diagrams").getChild("planningDomains").getChildren("domain").get(1);
		
		List<String> parameters = new ArrayList<String>();
		parameters.add("SATELLITE0");
		parameters.add("INSTRUMENT0");
		parameters.add("STAR0");
		parameters.add("IMAGE2");		
		
		Element snapshot = null;
		try {
			XPath path = new JDOMXPath("diagrams/planningDomains/domain[2]/planningProblems/problem[1]/objectDiagrams/objectDiagram[1]");			
			snapshot = (Element)path.selectSingleNode(project);						
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		System.out.println("##################### Step 1 #####################");
		parameters.add("TRUCK2");
		parameters.add("PKG2");
		parameters.add("LOCATIONRJ1");
		//parameters.add("TABLE1");
		Element next1 = nextState("LOADTRUCK", parameters, snapshot, domain, project);
		System.out.println("##################################################\n");
		
		System.out.println("##################### Step 2 #####################");
		parameters.clear();
		parameters.add("TRUCK1");
		parameters.add("PKG1");		
		parameters.add("LOCATIONSP1");
		Element next2 = nextState("LOADTRUCK", parameters, next1, domain, project);
		System.out.println("##################################################\n");
		
		System.out.println("##################### Step 3 #####################");
		parameters.clear();
		parameters.add("AIRPLANE2");
		parameters.add("AIRPORTRJ1");		
		parameters.add("AIRPORTSP1");
		//parameters.add("TABLE1");
		Element next3 = nextState("FLY", parameters, next2, domain, project);
		System.out.println("##################################################\n");
		
		System.out.println("##################### Step 4 #####################");
		parameters.clear();
		parameters.add("TRUCK2");
		parameters.add("LOCATIONRJ1");		
		parameters.add("AIRPORTRJ1");
		parameters.add("RIODEJANEIRO");
		Element next4 = nextState("DRIVE", parameters, next3, domain, project);
		System.out.println("##################################################\n");
		
		System.out.println("##################### Step 5 #####################");
		parameters.clear();
		parameters.add("TRUCK2");		
		parameters.add("PKG2");
		parameters.add("AIRPORTRJ1");
		Element next5 = nextState("UNLOADTRUCK", parameters, next4, domain, project);
		System.out.println("##################################################\n");
		
		System.out.println("##################### Step 6 #####################");
		parameters.clear();
		parameters.add("TRUCK2");
		parameters.add("PKG3");		
		parameters.add("AIRPORTRJ1");
		//parameters.add("TABLE1");
		Element next6 = nextState("UNLOADTRUCK", parameters, next5, domain, project);
		System.out.println("##################################################\n");
		
		System.out.println("##################### Step 7 #####################");
		parameters.clear();		
		parameters.add("TRUCK1");		
		parameters.add("LOCATIONSP1");
		parameters.add("AIRPORTSP1");
		parameters.add("SAOPAULO");
		Element next7 = nextState("DRIVE", parameters, next6, domain, project);
		System.out.println("##################################################\n");
		
		System.out.println("##################### Step 8 #####################");
		parameters.clear();
		parameters.add("TRUCK1");
		parameters.add("PKG1");		
		parameters.add("AIRPORTSP1");
		//parameters.add("TABLE1");
		Element next8 = nextState("UNLOADTRUCK", parameters, next7, domain, project);
		System.out.println("##################################################\n");
		
		System.out.println("##################### Step 9 #####################");
		parameters.clear();
		parameters.add("AIRPLANE2");
		parameters.add("PKG1");		
		parameters.add("AIRPORTSP1");
		//parameters.add("TABLE1");
		Element next9 = nextState("LOADAIRPLANE", parameters, next8, domain, project);
		System.out.println("##################################################\n");
		
		System.out.println("##################### Step 10 #####################");
		parameters.clear();
		parameters.add("AIRPLANE2");
		parameters.add("PKG1");		
		parameters.add("AIRPORTSP1");
		//parameters.add("TABLE1");
		nextState("UNLOADAIRPLANE", parameters, next9, domain, project);
		System.out.println("##################################################\n");
	}*/
}
