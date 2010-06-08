/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2009 Universidade de Sao Paulo
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
*			Victor Romero.
**/

package itGraph;

import itGraph.UMLElements.ActorCell;
import itGraph.UMLElements.BasicAssociation;
import itGraph.UMLElements.ClassAssociation;
import itGraph.UMLElements.ClassCell;
import itGraph.UMLElements.Dependency;
import itGraph.UMLElements.ActionAssociation;
import itGraph.UMLElements.EnumerationCell;
import itGraph.UMLElements.FinalStateCell;
import itGraph.UMLElements.Generalization;
import itGraph.UMLElements.InitialStateCell;
import itGraph.UMLElements.ObjectAssociation;
import itGraph.UMLElements.ObjectCell;
import itGraph.UMLElements.StateCell;
import itGraph.UMLElements.UseCaseAssociation;
import itGraph.UMLElements.UseCaseCell;
import itSIMPLE.ItSIMPLE;
import itSIMPLE.ItToolBar;
import itSIMPLE.ItTreeNode;
import itSIMPLE.MultiObjectDialog;
import languages.xml.XMLUtilities;

import java.awt.Cursor;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Rectangle2D;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.tree.DefaultTreeModel;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

import org.jgraph.graph.CellView;
import org.jgraph.plaf.basic.BasicGraphUI;



public class ItGraphUI extends BasicGraphUI {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8014160838413365590L;
	
	
	public ItGraphUI() {
		super();
	}
	
	/**
	 * Creates the listener responsible for calling the correct handlers based
	 * on mouse events, and to select invidual cells.
	 */
	protected MouseListener createMouseListener() {
		return new MyMouseHandler();
	}
	
	/**
	 * TreeMouseListener is responsible for updating the selection based on
	 * mouse events.
	 */
	public class MyMouseHandler extends MouseAdapter implements
			MouseMotionListener, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7890054986774522800L;

		/* The cell under the mousepointer. */
		protected CellView cell;

		/* The object that handles mouse operations. */
		protected Object handler;

		protected transient Cursor previousCursor = null;
		
		private boolean newAssociation = true;
		private BasicAssociation association = null;
		private BasicCell sourceCell;
		
		
		/*// copy
		private Action copyElement = new AbstractAction("Copy"){
			public void actionPerformed(ActionEvent arg0) {
				ItSIMPLE.getCopyPaste().clear();
				ItSIMPLE.getCopyPaste().add(graph.getSelectionCell());			
			}
			
		};
		
		// paste
		private Action pasteElement = new AbstractAction("Paste"){
			public void actionPerformed(ActionEvent arg0) {
				if(ItSIMPLE.getCopyPaste().size() > 0){
					if (ItSIMPLE.getCopyPaste().get(0) instanceof ObjectCell){
						System.out.println("colei");
					}
					
				}						
			}
			
		};*/
		
		private void createAssociation(int associationType, ItToolBar toolBar, int x, int y,
									Element project, Element diagram, Element commonData){
			Object cell = graph.getFirstCellForLocation(x,y);
			if (cell != null &&
					(cell instanceof ActorCell ||
					 cell instanceof ClassCell ||
					 cell instanceof EnumerationCell ||
					 cell instanceof FinalStateCell ||
					 cell instanceof InitialStateCell ||
					 cell instanceof ObjectCell ||
					 cell instanceof StateCell ||
					 cell instanceof UseCaseCell)){						
				if (newAssociation){														
					sourceCell = (BasicCell)cell;
					newAssociation = false;
				}
				else{							
					BasicCell targetCell = (BasicCell)cell;					
					
					switch(associationType){
					case ItToolBar.USE_CASE_ASSOCIATION:{
						Element useCaseAssociationData = (Element)commonData.getChild("definedNodes").getChild("elements")
						.getChild("model").getChild("useCaseAssociation").clone();					
						
						String id = String.valueOf(XMLUtilities.getId(diagram.getChild("associations")));
						
						useCaseAssociationData.setAttribute("id", id);
						//useCaseAssociationData.getChild("name").setText(useCaseAssociationData.getChild("name").getText()+" "+id);
						//useCaseAssociationData.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(x));
						//useCaseAssociationData.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(y));
						
						Iterator associationEnds = useCaseAssociationData.getChild("associationEnds").getChildren().iterator();
						
						Element sourceEnd = (Element)associationEnds.next();						
						sourceEnd.setAttribute("element", sourceCell.getData().getName());
						sourceEnd.setAttribute("element-id", sourceCell.getData().getAttributeValue("id"));
						
						Element targetEnd = (Element)associationEnds.next();
						targetEnd.setAttribute("element", targetCell.getData().getName());
						targetEnd.setAttribute("element-id", targetCell.getData().getAttributeValue("id"));
						
						diagram.getChild("associations").addContent(useCaseAssociationData);
						
						association = new UseCaseAssociation(useCaseAssociationData, sourceCell, targetCell);			
					}
					break;
					case ItToolBar.CLASS_ASSOCIATION:{
						
						if(sourceCell.getData().getName().equals("enumeration") ||
								targetCell.getData().getName().equals("enumeration")){
							// can't associate with enumeration
							JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
									"<html><center>It's not possible to create " +
									"an association with an enumeration.</center></html>",
									"Not Allowed Association",
									JOptionPane.WARNING_MESSAGE);
						}
						else{
							Element classAssociationData = (Element)commonData.getChild("definedNodes").getChild("elements")
							.getChild("model").getChild("classAssociation").clone();
							
							Element classAssociationReference = (Element)commonData.getChild("definedNodes").getChild("elements")
							.getChild("references").getChild("classAssociation").clone();						
							
							String id = String.valueOf(XMLUtilities.getId(project.getChild("elements").getChild("classAssociations")));
							
							classAssociationData.setAttribute("id", id);
							classAssociationReference.setAttribute("id", id);
							
							classAssociationData.getChild("name").setText(classAssociationData.getChild("name").getText()+id);
													
							Iterator associationEnds = classAssociationData.getChild("associationEnds").getChildren().iterator();
							
							Element sourceEnd = (Element)associationEnds.next();						
							sourceEnd.setAttribute("element", sourceCell.getData().getName());
							sourceEnd.setAttribute("element-id", sourceCell.getData().getAttributeValue("id"));
							
							Element targetEnd = (Element)associationEnds.next();
							targetEnd.setAttribute("element", targetCell.getData().getName());
							targetEnd.setAttribute("element-id", targetCell.getData().getAttributeValue("id"));
							
							project.getChild("elements").getChild("classAssociations").addContent(classAssociationData);									
							diagram.getChild("associations").addContent(classAssociationReference);

							association = new ClassAssociation(classAssociationData,classAssociationReference,sourceCell, targetCell);
						}
					}
					break;
					case ItToolBar.ACTION_ASSOCIATION:{
						Element actionAssociationData = (Element)commonData.getChild("definedNodes").getChild("elements")
						.getChild("model").getChild("action").clone();					
						
						String id = String.valueOf(XMLUtilities.getId(diagram.getChild("associations")));
						
						actionAssociationData.setAttribute("id", id);
						//actionAssociationData.getChild("name").setText(useCaseAssociationData.getChild("name").getText()+" "+id);
						//actionAssociationData.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(x));
						//actionAssociationData.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(y));
						
						Iterator associationEnds = actionAssociationData.getChild("associationEnds").getChildren().iterator();
						
						Element sourceEnd = (Element)associationEnds.next();						
						sourceEnd.setAttribute("element", sourceCell.getData().getName());
						sourceEnd.setAttribute("element-id", sourceCell.getData().getAttributeValue("id"));
						
						Element targetEnd = (Element)associationEnds.next();
						targetEnd.setAttribute("element", targetCell.getData().getName());
						targetEnd.setAttribute("element-id", targetCell.getData().getAttributeValue("id"));
									
						diagram.getChild("associations").addContent(actionAssociationData);							
						
						association = new ActionAssociation(actionAssociationData, null, sourceCell, targetCell);						
					}
					break;
					case ItToolBar.GENERALIZATION:{
						if(sourceCell.getData().getName().equals("enumeration") ||
								targetCell.getData().getName().equals("enumeration")){						
							// can't associate with enumeration
							JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
									"<html><center>It's not possible to create " +
									"a generalization with an enumeration.</center></html>",
									"Not Allowed Generalization",
									JOptionPane.WARNING_MESSAGE);
						}
						else if(sourceCell.equals(targetCell)){
							// can't create a generalization with itself
							JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
									"<html><center>It's not possible to create " +
									"a generalization of a class to itself.</center></html>",
									"Not Allowed Generalization",
									JOptionPane.WARNING_MESSAGE);
						}
						else{
							// create the generalization
							Element sourceElement = sourceCell.getData();
							Element targetElement = targetCell.getData();						
							sourceElement.getChild("generalization").setAttribute("element", targetElement.getName());
							sourceElement.getChild("generalization").setAttribute("id", targetElement.getAttributeValue("id"));
							
							association = new Generalization(sourceElement.getChild("generalization"), targetElement,
									 				sourceCell, targetCell);
						
						}
					}
					
					break;
					case ItToolBar.OBJECT_ASSOCIATION:{
						Element sourceElement = sourceCell.getData();
						Element targetElement = targetCell.getData();
						
						Element sourceClass = sourceCell.getAdditionalData();
						Element targetClass = targetCell.getAdditionalData();
						
						boolean canCreateAssociation = true;
						
						ArrayList<Element> listAssociation = new ArrayList<Element>();
						ArrayList<Element> listSourceAssociationEnd = new ArrayList<Element>();
						ArrayList<Element> listTargetAssociationEnd = new ArrayList<Element>();
						Element sourceEnd = null;
						Element targetEnd = null;
						String sourceMultiplicity = null;
						String targetMultiplicity = null;
												
						/* Check the association
							1. Check if both objects have defined classes
							2. If there is a association between these two object
							3. Check the multiplicity
						*/
												
						//	1. Check if both objects have defined classes
						if (sourceClass == null || targetClass == null){
							canCreateAssociation = false;
							JOptionPane.showMessageDialog(graph,
									"<html><center>Not all objects have defined classes!</center></html>",
									"Not Allowed Association",
									JOptionPane.WARNING_MESSAGE);
						}
						
						//  2. If there is an association between these two object						
						if (canCreateAssociation){					
							Iterator associations = project.getChild("elements").getChild("classAssociations").getChildren().iterator();
							
							while(associations.hasNext()){
								Element association = (Element)associations.next();	
								sourceEnd = null;
								targetEnd = null;	
								
								// Looking for the sourceClass, or its superclasses, in the current association
								Element currentClass = sourceClass;
								Iterator associationEnds = null;								
								while (currentClass != null){
									associationEnds = association.getChild("associationEnds").getChildren().iterator();
									while(associationEnds.hasNext()){
										Element associationEnd = (Element)associationEnds.next();
										if (associationEnd.getAttributeValue("element").equals(currentClass.getName())
												&& associationEnd.getAttributeValue("element-id").equals(currentClass.getAttributeValue("id"))){						
											sourceEnd = associationEnd;
											break;
										}
									}
									if (sourceEnd == null && !currentClass.getChild("generalization").getAttributeValue("id").equals("") ){										
										currentClass = XMLUtilities.getElement(project.getChild("elements").getChild("classes"),
												currentClass.getChild("generalization").getAttributeValue("id"));
									}
									else{
										currentClass = null;
									}
								}
								
								
								// Looking for the targetClass, or its superclasses, in the current association
								currentClass = targetClass;
								if (sourceEnd != null){
									while (currentClass != null){
										associationEnds = association.getChild("associationEnds").getChildren().iterator();
										while(associationEnds.hasNext()){
											Element associationEnd = (Element)associationEnds.next();
											if (associationEnd.getAttributeValue("element").equals(currentClass.getName())
													&& associationEnd.getAttributeValue("element-id").equals(currentClass.getAttributeValue("id")) &&
													sourceEnd != associationEnd){						
												targetEnd = associationEnd;
												break;
											}
										}
										if (targetEnd == null && !currentClass.getChild("generalization").getAttributeValue("id").equals("") ){											
											currentClass = XMLUtilities.getElement(project.getChild("elements").getChild("classes"),
													currentClass.getChild("generalization").getAttributeValue("id"));
										}
										else{
											currentClass = null;
										}									
									}		
								}
								
								if (sourceEnd != null && targetEnd != null && !(sourceEnd == targetEnd)){
									listAssociation.add(association);
									listSourceAssociationEnd.add(sourceEnd);
									listTargetAssociationEnd.add(targetEnd);
								}			
							}
							// if there is no possible association then it can not create the association
							if (listAssociation.size() < 1){
								canCreateAssociation = false;
								JOptionPane.showMessageDialog(graph,
										"<html><center>There is no possible association " +
										"between these two objects!</center></html>",
										"Not Allowed Association",
										JOptionPane.WARNING_MESSAGE);								
							}															
						} 
						
						Element classAssociation = null;
						
						//  3. Check the multiplicity of the possible Association
						if (canCreateAssociation){

							Element currentClassAssociation = null;
							Element currentSourceEnd = null;
							Element currentTargetEnd = null;
							
							BasicAssociation objectAssociation = null;
							List<BasicAssociation> listSourceEdges = new ArrayList<BasicAssociation>();
							List<BasicAssociation> listTargetEdges = new ArrayList<BasicAssociation>();
							boolean sourceMultiplicityOK = false;
							boolean targetMultiplicityOK = false;
							String lowerBound = null;
							String upperBound = null;
							int counter = 0;
							
							
							// choose the association that respect the multiplicity rules from the association list 
							while(counter < listAssociation.size()){
								
								//reset the couting lists
								listSourceEdges.clear();
								listTargetEdges.clear();
								
								
								currentClassAssociation = listAssociation.get(counter);
								
								currentSourceEnd = listSourceAssociationEnd.get(counter);
								currentTargetEnd = listTargetAssociationEnd.get(counter);
								sourceMultiplicity = currentSourceEnd.getChild("multiplicity").getChildText("value");
								targetMultiplicity = currentTargetEnd.getChild("multiplicity").getChildText("value");
								
								Element domain = null;
								List assoc = null;
								
								if (diagram.getName().equals("objectDiagram")){
												//   objectDiagrams     problem				planningProblems  domain
									domain = diagram.getParentElement().getParentElement().getParentElement().getParentElement();
									Element problem = diagram.getParentElement().getParentElement();
									
									//Get all association of the currentClassAssociation type
									
									try {
										XPath path = new JDOMXPath("repositoryDiagrams/repositoryDiagram/associations/objectAssociation[classAssociation='"+currentClassAssociation.getAttributeValue("id")+"'] | " +
												"planningProblems/problem[@id="+problem.getAttributeValue("id")+"]/objectDiagrams/objectDiagram[@id="+diagram.getAttributeValue("id")+"]" +
														"/associations/objectAssociation[classAssociation='"+currentClassAssociation.getAttributeValue("id")+"']");
										assoc = path.selectNodes(domain);
									} catch (JaxenException e2) {			
										e2.printStackTrace();
									}
									
									//System.out.println(currentClassAssociation.getChildText("name")+" - "+assoc.size());
								
								}
								else if (diagram.getName().equals("repositoryDiagram")){
										//	    	  repositoryDiagrams domain
									domain = diagram.getParentElement().getParentElement();
									
									//Get all association of the currentClassAssociation type
									try {
										XPath path = new JDOMXPath("associations/objectAssociation[classAssociation='"+currentClassAssociation.getAttributeValue("id")+"']");
										assoc = path.selectNodes(diagram);
									} catch (JaxenException e2) {			
										e2.printStackTrace();
									}
									//System.out.println(currentClassAssociation.getChildText("name")+" REPOSITORY - "+assoc.size());
									
								}
															
								
								
								//3.1 Analizing source multiplicity
								if (targetMultiplicity.trim().equals("") || (targetMultiplicity.indexOf("*") > 0) ){
									sourceMultiplicityOK = true;
								}
								else{
									StringTokenizer str = new StringTokenizer(targetMultiplicity,"..");									

									if (str.countTokens() > 1){
										lowerBound = str.nextToken();									
										upperBound = str.nextToken();
									}
									else if(str.countTokens() == 1){
										lowerBound = str.nextToken();
										upperBound = lowerBound;									
									}
									else {
										break;
									}
									
									//SOURCE
									for (Iterator iter = assoc.iterator(); iter.hasNext();) {
										Element currentAssoc = (Element) iter.next();
										Element assocEnd = null;
										try {
											XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@element-id="+sourceElement.getAttributeValue("id")+"]");
											assocEnd = (Element)path.selectSingleNode(currentAssoc);
										} catch (JaxenException e2) {
											e2.printStackTrace();
										}
										
										if (assocEnd != null){
											String sourceNavigation = currentSourceEnd.getAttributeValue("navigation");
											String sourceRoleName = currentSourceEnd.getChildText("rolename").trim();
											//Find this association End at the Class association 
											Element thisClassAssociationEnd = null;
											//get source object
											try {
												XPath path = new JDOMXPath("associationEnds/associationEnd[@id='"+assocEnd.getAttributeValue("id")+"']");
												thisClassAssociationEnd = (Element)path.selectSingleNode(currentClassAssociation);
											} catch (JaxenException e2) {
												e2.printStackTrace();
											}
											
											
											//System.out.println(currentSourceEnd.getChildText("rolename") + " - > " + thisClassAssociationEnd.getChildText("rolename"));
											if(!sourceRoleName.equals("")){
												if (thisClassAssociationEnd.getChildText("rolename").trim().equals(currentSourceEnd.getChildText("rolename"))){
													listSourceEdges.add(objectAssociation);									
												}
											}
											else if (thisClassAssociationEnd.getAttributeValue("navigation").equals(sourceNavigation)){
												listSourceEdges.add(objectAssociation);									
											}
										}

									}
									//System.out.println("NEW SOURCE->>>> " + currentClassAssociation.getChildText("name")+" - "+listSourceEdges.size());
															
									
									/*// Counting association - source
									Object portSource = graph.getModel().getChild(sourceCell,0);
									if (graph.getModel().isPort(portSource)){
										Iterator edges = graph.getModel().edges(portSource);
										while(edges.hasNext()){
											objectAssociation = (BasicAssociation)edges.next();
											
											if (objectAssociation.getReference().getAttributeValue("id").equals(currentClassAssociation.getAttributeValue("id"))){
												Element objectAssociationData = objectAssociation.getData();					
												String sourceNavigation = currentSourceEnd.getAttributeValue("navigation");
												String sourceRoleName = currentSourceEnd.getChildText("rolename").trim();
												Element thisAssociationEnd = null;
												//get source object
												try {
													XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@element-id="+sourceElement.getAttributeValue("id")+"]");
													thisAssociationEnd = (Element)path.selectSingleNode(objectAssociationData);
												} catch (JaxenException e2) {			
													e2.printStackTrace();
												}
												if (thisAssociationEnd != null){
													//Find this association End at the Class association 
													Element thisClassAssociationEnd = null;
													//get source object
													try {
														XPath path = new JDOMXPath("associationEnds/associationEnd[@id='"+thisAssociationEnd.getAttributeValue("id")+"']");
														thisClassAssociationEnd = (Element)path.selectSingleNode(objectAssociation.getReference());
													} catch (JaxenException e2) {
														e2.printStackTrace();
													}
													if(!sourceRoleName.equals("")){
														if (thisClassAssociationEnd.getChildText("rolename").trim().equals(sourceRoleName)){
															listSourceEdges.add(objectAssociation);									
														}
													}
													else if (thisClassAssociationEnd.getAttributeValue("navigation").equals(sourceNavigation)){
														listSourceEdges.add(objectAssociation);									
													}
	
												}
											}												
										}	
									}
	*/								//System.out.println("SOURCE->>>> " + currentClassAssociation.getChildText("name")+" - "+listSourceEdges.size());
									
									int maxBound = Integer.parseInt(upperBound);
									if (maxBound > listSourceEdges.size()){
										sourceMultiplicityOK = true;
									}
									else{
										sourceMultiplicityOK = false;
									}	
								}

										
								//3.2 Analizing target multiplicity
								if (sourceMultiplicityOK){
									if (sourceMultiplicity.equals("") || (sourceMultiplicity.indexOf("*") > 0) ){
										targetMultiplicityOK = true;
									}
									else{
										StringTokenizer str = new StringTokenizer(sourceMultiplicity,"..");
										
										if (str.countTokens() > 1){
											lowerBound = str.nextToken();									
											upperBound = str.nextToken();
										}
										else if(str.countTokens() == 1){
											lowerBound = str.nextToken();
											upperBound = lowerBound;									
										}
										else {
											break;
										}
										
										//TARGET
										for (Iterator iter = assoc.iterator(); iter.hasNext();) {
											Element currentAssoc = (Element) iter.next();
											Element assocEnd = null;
											try {
												XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@element-id="+targetElement.getAttributeValue("id")+"]");
												assocEnd = (Element)path.selectSingleNode(currentAssoc);
											} catch (JaxenException e2) {			
												e2.printStackTrace();
											}
											
											if (assocEnd != null){
												//Find this association End at the Class association 
												Element thisClassAssociationEnd = null;
												String targetNavigation = currentTargetEnd.getAttributeValue("navigation");
												String targetRoleName = currentTargetEnd.getChildText("rolename").trim();
												//get source object
												try {
													XPath path = new JDOMXPath("associationEnds/associationEnd[@id='"+assocEnd.getAttributeValue("id")+"']");
													thisClassAssociationEnd = (Element)path.selectSingleNode(currentClassAssociation);
												} catch (JaxenException e2) {
													e2.printStackTrace();
												}
												
												
												//System.out.println(currentTargetEnd.getChildText("rolename") + " - > " + thisClassAssociationEnd.getChildText("rolename"));
												if(!targetRoleName.equals("")){
													if (thisClassAssociationEnd.getChildText("rolename").trim().equals(targetRoleName)){
														listTargetEdges.add(objectAssociation);									
													}
												}
												else if (thisClassAssociationEnd.getAttributeValue("navigation").equals(targetNavigation)){
													listTargetEdges.add(objectAssociation);									
												}											
												
											}

										}
										
										//System.out.println("NEW TARGET->>>> " + currentClassAssociation.getChildText("name")+" - "+listTargetEdges.size());
										
										
										
										/*Object portTarget = graph.getModel().getChild(targetCell,0);
										if (graph.getModel().isPort(portTarget)){
											Iterator edges = graph.getModel().edges(portTarget);
											while(edges.hasNext()){
												objectAssociation = (BasicAssociation)edges.next();
												if (objectAssociation.getReference().getAttributeValue("id").equals(currentClassAssociation.getAttributeValue("id"))){
													Element objectAssociationData = objectAssociation.getData();
													String targetNavigation = currentTargetEnd.getAttributeValue("navigation");
													String targetRoleName = currentTargetEnd.getChildText("rolename").trim();
													Element thisAssociationEnd = null;
													//get target object
													try {
														XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@element-id='"+targetElement.getAttributeValue("id")+"']");
														thisAssociationEnd = (Element)path.selectSingleNode(objectAssociationData);
													} catch (JaxenException e2) {			
														e2.printStackTrace();
													}
													if (thisAssociationEnd != null){
														//Find this association End at the Class association 
														Element thisClassAssociationEnd = null;
														try {
															XPath path = new JDOMXPath("associationEnds/associationEnd[@id='"+thisAssociationEnd.getAttributeValue("id")+"']");
															thisClassAssociationEnd = (Element)path.selectSingleNode(objectAssociation.getReference());
														} catch (JaxenException e2) {
															e2.printStackTrace();
														}														
														if (!targetRoleName.equals("")){
															
															if (thisClassAssociationEnd.getChildText("rolename").trim().equals(targetRoleName)){
																listTargetEdges.add(objectAssociation);									
															}
														}
														else if (thisClassAssociationEnd.getAttributeValue("navigation").equals(targetNavigation)){
															listTargetEdges.add(objectAssociation);													
														}

													}
												}									
											}	
										}
*/										
										//System.out.println("TARGET->>>> " + currentClassAssociation.getChildText("name")+" - "+listTargetEdges.size());	
										//System.out.println(upperBound+" > "+listTargetEdges.size());
										int maxBound = Integer.parseInt(upperBound);
										if (maxBound > listTargetEdges.size()){
											targetMultiplicityOK = true;
										}
										else{
											targetMultiplicityOK = false;
										}								
									}
								}									
								
								//System.out.println("target: "+ targetMultiplicityOK + " - Source: " + sourceMultiplicityOK);
								
								if (targetMultiplicityOK && sourceMultiplicityOK){
									classAssociation = currentClassAssociation;
									sourceEnd = currentSourceEnd;
									targetEnd = currentTargetEnd;
									counter = listAssociation.size();
								}
								else{
									counter++;
								}
								
							}
							
							
							
							if (classAssociation == null){
								canCreateAssociation = false;
								JOptionPane.showMessageDialog(graph,
										"<html><center>There are possible associations between these two objects" +
										"<br>but the multiplicities rules do not allow the association!</center></html>",
										"Not Allowed Association",
										JOptionPane.WARNING_MESSAGE);									
							}								
						}
						
						
						// 4. if everything is ok then create the association
						if (canCreateAssociation){
							Element objectAssociationData = (Element)commonData.getChild("definedNodes").getChild("elements")
							.getChild("model").getChild("objectAssociation").clone();
							
							Iterator associationEnds = objectAssociationData.getChild("associationEnds").getChildren().iterator();
							Element associationEnd = null;
							
							//Set classAssociation reference
							String id = String.valueOf(XMLUtilities.getId(diagram.getChild("associations")));
							objectAssociationData.getAttribute("id").setValue(id);
							objectAssociationData.getChild("classAssociation").setText(classAssociation.getAttributeValue("id"));							
							
							//Set source association end
							associationEnd = (Element)associationEnds.next();
							associationEnd.setAttribute("element",sourceElement.getName());
							associationEnd.setAttribute("element-id",sourceElement.getAttributeValue("id"));
							associationEnd.setAttribute("id",sourceEnd.getAttributeValue("id"));
							
							//Set source association end
							associationEnd = (Element)associationEnds.next();
							associationEnd.setAttribute("element",targetElement.getName());
							associationEnd.setAttribute("element-id",targetElement.getAttributeValue("id"));
							associationEnd.setAttribute("id",targetEnd.getAttributeValue("id"));							
																			
							diagram.getChild("associations").addContent(objectAssociationData);
							association = new ObjectAssociation(objectAssociationData, classAssociation, sourceCell, targetCell);
						}
						
						
					}
					break;
					case ItToolBar.DEPENDENCY:{
						Element dependencyAssociationData = (Element)commonData.getChild("definedNodes").getChild("elements")
						.getChild("model").getChild("dependency").clone();					
						
						String id = String.valueOf(XMLUtilities.getId(diagram.getChild("associations")));
						
						dependencyAssociationData.setAttribute("id", id);
						//dependencyAssociationData.getChild("name").setText(useCaseAssociationData.getChild("name").getText()+" "+id);
						//dependencyAssociationData.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(x));
						//dependencyAssociationData.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(y));
						
						Iterator associationEnds = dependencyAssociationData.getChild("associationEnds").getChildren().iterator();
						
						Element sourceEnd = (Element)associationEnds.next();						
						sourceEnd.setAttribute("element", sourceCell.getData().getName());
						sourceEnd.setAttribute("element-id", sourceCell.getData().getAttributeValue("id"));
						
						Element targetEnd = (Element)associationEnds.next();
						targetEnd.setAttribute("element", targetCell.getData().getName());
						targetEnd.setAttribute("element-id", targetCell.getData().getAttributeValue("id"));
						
						
						diagram.getChild("associations").addContent(dependencyAssociationData);						
						
						association = new Dependency(dependencyAssociationData, sourceCell, targetCell);						
					}
					break;
					}					
					if (association != null){
						//association.setSource(sourceCell.getChildAt(0));
						//association.setTarget(targetCell.getChildAt(0));
						association.setRoutePoints();
						graph.getGraphLayoutCache().insert(association);					
						association = null;						
					}
					newAssociation = true;
					toolBar.setSelectedButton(ItToolBar.SELECT);					
				}
			}
			else{				
				newAssociation = true;
				toolBar.setSelectedButton(ItToolBar.SELECT);
			}
		}

		/**
		 * Invoked when a mouse button has been pressed on a component.
		 */
		public void mousePressed(MouseEvent e) {			
			if (!e.isConsumed() && graph.isEnabled()) {
				handler = null;
				graph.requestFocus();
				int s = graph.getTolerance();
				Rectangle2D r = graph.fromScreen(new Rectangle2D.Double(e
						.getX()
						- s, e.getY() - s, 2 * s, 2 * s));
				lastFocus = focus;
				focus = (focus != null && focus.intersects(graph, r)) ? focus
						: null;
				cell = graph.getNextSelectableViewAt(focus, e.getX(), e.getY());
				if (focus == null)
					focus = cell;
				completeEditing();
				if (!isForceMarqueeEvent(e)) {
					if (e.getClickCount() == graph.getEditClickCount()
							&& focus != null && focus.isLeaf()
							&& focus.getParentView() == null
							&& graph.isCellEditable(focus.getCell())
							&& handleEditTrigger(cell.getCell(), e)) {
						e.consume();
						cell = null;
					} else if (!isToggleSelectionEvent(e)) {
						if (handle != null) {
							handle.mousePressed(e);
							handler = handle;
						}
						// Immediate Selection
						if (!e.isConsumed() && cell != null
								&& !graph.isCellSelected(cell.getCell())) {
							selectCellForEvent(cell.getCell(), e);
							focus = cell;
							if (handle != null) {
								handle.mousePressed(e);
								handler = handle;
							}
							e.consume();
							cell = null;
						}
					}
				}
				// Marquee Selection
				if (!e.isConsumed() && marquee != null
						&& (!isToggleSelectionEvent(e) || focus == null)) {
					marquee.mousePressed(e);
					handler = marquee;
				}
				if (e.isPopupTrigger()){// this is for Linux
					if (graph instanceof ItGraph){
						ItGraph itGraph = (ItGraph)graph;					
						itGraph.getPopupMenu(graph.getFirstCellForLocation(e.getX(), e.getY())).show(graph, e.getX(), e.getY());
						
					}				
				}
			}		
					
			if(graph instanceof ItGraph){
				ItGraph itGraph = (ItGraph)graph;
				ItToolBar toolBar = (ItToolBar)itGraph.getToolBar();
				Element project = itGraph.getProject();
				Element diagram = itGraph.getDiagram();
				Element commonData = itGraph.getCommonData();
				ItTreeNode diagramNode = ItSIMPLE.getInstance().getItTree().findNodeFromData(project, diagram);
				DefaultTreeModel treeModel = (DefaultTreeModel)ItSIMPLE.getInstance().getItTree().getModel();
				
				if(toolBar != null){
					switch(toolBar.getSelectedButton()){				
					case ItToolBar.SELECT:{
						// do nothing					
					}
					break;
					case ItToolBar.ACTOR:{					
						Element actorData = (Element)commonData.getChild("definedNodes").getChild("elements")
													.getChild("model").getChild("actor").clone();					
						
						String id = String.valueOf(XMLUtilities.getId(diagram.getChild("actors")));
						
						actorData.setAttribute("id", id);
						actorData.getChild("name").setText(actorData.getChild("name").getText()+id);
						actorData.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(e.getX()));
						actorData.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(e.getY()));
						diagram.getChild("actors").addContent(actorData);					
						
						ActorCell actor = new ActorCell(actorData);
						graph.getGraphLayoutCache().insert(actor);
						
						ItTreeNode actorNode = new ItTreeNode(actorData.getChildText("name"), actorData, null, null);
						actorNode.setIcon(new ImageIcon("resources/images/actor.png"));
						treeModel.insertNodeInto(actorNode, diagramNode, diagramNode.getChildCount());					
						
						toolBar.setSelectedButton(ItToolBar.SELECT);
						
					}
					break;
					case ItToolBar.USE_CASE:{
						Element useCaseData = (Element)commonData.getChild("definedNodes").getChild("elements")
						.getChild("model").getChild("useCase").clone();					
						
						String id = String.valueOf(XMLUtilities.getId(diagram.getChild("useCases")));
						
						useCaseData.setAttribute("id", id);
						useCaseData.getChild("name").setText(useCaseData.getChild("name").getText()+" "+id);
						useCaseData.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(e.getX()));
						useCaseData.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(e.getY()));
						diagram.getChild("useCases").addContent(useCaseData);					
						
						UseCaseCell useCase = new UseCaseCell(useCaseData);
						graph.getGraphLayoutCache().insert(useCase);
						
						ItTreeNode useCaseNode = new ItTreeNode(useCaseData.getChildText("name"), useCaseData, null, null);
						useCaseNode.setIcon(new ImageIcon("resources/images/useCase.png"));
						treeModel.insertNodeInto(useCaseNode, diagramNode, diagramNode.getChildCount());
						
						toolBar.setSelectedButton(ItToolBar.SELECT);					
					}
					break;
					case ItToolBar.CLASS:{
						
						Element classData = (Element)commonData.getChild("definedNodes").getChild("elements")
						.getChild("model").getChild("class").clone();
						
						Element classReference = (Element)commonData.getChild("definedNodes").getChild("elements")
						.getChild("references").getChild("class").clone();						
						
						String id = String.valueOf(XMLUtilities.getId(project.getChild("elements").getChild("classes")));
						// make the id be higher than 20
						if (Integer.parseInt(id) <= 20)
							id = "21";
						
						classData.setAttribute("id", id);
						classReference.setAttribute("id", id);
						
						classData.getChild("name").setText(classData.getChild("name").getText()+id);
						classReference.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(e.getX()));
						classReference.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(e.getY()));
						project.getChild("elements").getChild("classes").addContent(classData);
						diagram.getChild("classes").addContent(classReference);	
						
						
						ClassCell Class = new ClassCell(classData, classReference);
						graph.getGraphLayoutCache().insert(Class);
						
						ItTreeNode classNode = new ItTreeNode(classData.getChildText("name"), classData, classReference, null);
						classNode.setIcon(new ImageIcon("resources/images/class.png"));					
						treeModel.insertNodeInto(classNode, diagramNode, diagramNode.getChildCount());
						
						toolBar.setSelectedButton(ItToolBar.SELECT);
					}
					break;
					
					case ItToolBar.ENUMERATION:{
						
						Element enumeration = (Element)commonData.getChild("definedNodes").getChild("elements")
						.getChild("model").getChild("enumeration").clone();
						
						Element enumerationRef = (Element)commonData.getChild("definedNodes").getChild("elements")
						.getChild("references").getChild("enumeration").clone();						
						
						String id = String.valueOf(XMLUtilities.getId(project.getChild("elements").getChild("classes")));
						// make the id be higher than 20
						if (Integer.parseInt(id) <= 20)
							id = "21";
						
						enumeration.setAttribute("id", id);
						enumerationRef.setAttribute("id", id);
						
						enumeration.getChild("name").setText(enumeration.getChild("name").getText()+id);
						enumerationRef.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(e.getX()));
						enumerationRef.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(e.getY()));
						project.getChild("elements").getChild("classes").addContent(enumeration);
						diagram.getChild("classes").addContent(enumerationRef);	
						
						
						EnumerationCell enumerationCell = new EnumerationCell(enumeration, enumerationRef);
						graph.getGraphLayoutCache().insert(enumerationCell);
						
						ItTreeNode enumerationNode = new ItTreeNode(enumeration.getChildText("name"), enumeration, enumerationRef, null);
						enumerationNode.setIcon(new ImageIcon("resources/images/class.png"));					
						treeModel.insertNodeInto(enumerationNode, diagramNode, diagramNode.getChildCount());
						
						toolBar.setSelectedButton(ItToolBar.SELECT);
					}
					break;
					
					case ItToolBar.OBJECT:{
						
						if (diagram.getName().equals("repositoryDiagram")){
							//Planning
							//Get Domain Node
							Element domain = diagram.getParentElement().getParentElement();
							
							Element objectData = (Element)commonData.getChild("definedNodes").getChild("elements")
							.getChild("model").getChild("object").clone();
							
							Element objectReference = (Element)commonData.getChild("definedNodes").getChild("elements")
							.getChild("references").getChild("object").clone();						
							
							String id = String.valueOf(XMLUtilities.getId(domain.getChild("elements").getChild("objects")));
							
							objectData.setAttribute("id", id);
							objectReference.setAttribute("id", id);
							
							objectData.getChild("name").setText(objectData.getChild("name").getText()+id);
							objectReference.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(e.getX()));
							objectReference.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(e.getY()));
							domain.getChild("elements").getChild("objects").addContent(objectData);
							diagram.getChild("objects").addContent(objectReference);						
							
							ObjectCell object = new ObjectCell(objectData, objectReference, null);										
							graph.getGraphLayoutCache().insert(object);
							
							ItTreeNode objectNode = new ItTreeNode(objectData.getChildText("name"), objectData, objectReference, null);
							objectNode.setIcon(new ImageIcon("resources/images/object.png"));
							treeModel.insertNodeInto(objectNode, diagramNode, diagramNode.getChildCount());
						}
						
						if (diagram.getName().equals("objectDiagram")){
													// object diagrams		problem			planning problems	domain
							Element domain = diagram.getParentElement().getParentElement().getParentElement().getParentElement();
							
							Element objectData = (Element)commonData.getChild("definedNodes").getChild("elements")
							.getChild("model").getChild("object").clone();
							
							Element objectReference = (Element)commonData.getChild("definedNodes").getChild("elements")
							.getChild("references").getChild("object").clone();						
							
							String id = String.valueOf(XMLUtilities.getId(domain.getChild("elements").getChild("objects")));
							
							objectData.setAttribute("id", id);
							objectReference.setAttribute("id", id);
							
							objectData.getChild("name").setText(objectData.getChild("name").getText()+id);
							objectReference.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(e.getX()));
							objectReference.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(e.getY()));
							domain.getChild("elements").getChild("objects").addContent(objectData);
							domain.getChild("repositoryDiagrams").getChild("repositoryDiagram").getChild("objects").addContent((Element)objectReference.clone());	
							
							diagram.getChild("objects").addContent(objectReference);
													
							ObjectCell object = new ObjectCell(objectData, objectReference, null);										
							graph.getGraphLayoutCache().insert(object);
							
							//add in this diagram node
							ItTreeNode objectNode = new ItTreeNode(objectData.getChildText("name"), objectData, objectReference, null);
							objectNode.setIcon(new ImageIcon("resources/images/object.png"));
							treeModel.insertNodeInto(objectNode, diagramNode, diagramNode.getChildCount());
							
							// add in the repository node
							ItTreeNode repositoryNode = ItSIMPLE.getInstance().getItTree().findNodeFromData(project, domain.getChild("repositoryDiagrams").getChild("repositoryDiagram"));
							treeModel.insertNodeInto((ItTreeNode)objectNode.clone(), repositoryNode, repositoryNode.getChildCount());
								
						}
						
						toolBar.setSelectedButton(ItToolBar.SELECT);
					}
					break;
					case ItToolBar.MULTIPLE_OBJECTS:{
						// this array saves the class and the number of objects to be created
						// parameters[0]: class data or "No class"
						// parameters[1]: number of objects
						Object[] parameters = new Object[2];
						
                                                int xpos = itGraph.getLocation().x;
                                                int ypos = itGraph.getLocation().y;
                                                MultiObjectDialog dialog = new MultiObjectDialog(project, parameters, e.getX() + xpos + 150, e.getY() + ypos + 100);
                                                //MultiObjectDialog dialog = new MultiObjectDialog(project, parameters, e.getX()+150, e.getY()+100);//TODO the position is not right
						dialog.setVisible(true);

						
						if(parameters[0] != null && parameters[1] != null){									
							
							int numberOfObjects = Integer.parseInt((String)parameters[1]);
							Element domain = diagram.getParentElement().getParentElement();
							Element objectCommonData = commonData.getChild("definedNodes").getChild("elements")
																		.getChild("model").getChild("object");
							Element objectReferenceCommonData = commonData.getChild("definedNodes").getChild("elements")
																			.getChild("references").getChild("object");
							for(int i = 0; i < numberOfObjects; i++){
								Element objectData = (Element)objectCommonData.clone();
								
								Element objectReference = (Element)objectReferenceCommonData.clone();						
								
								String id = String.valueOf(XMLUtilities.getId(domain.getChild("elements").getChild("objects")));
								
								objectData.setAttribute("id", id);
								objectReference.setAttribute("id", id);
								
								objectData.getChild("name").setText(objectData.getChild("name").getText()+id);
								// the objects will be crated in cascate
								objectReference.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(e.getX()+i*10));
								objectReference.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(e.getY()+i*10));							
								
								
								Element objectClass = null;
								if(!parameters[0].equals("Undefined")){								
									// there is a defined class
									Element classData = (Element)parameters[0];								
									objectData.getChild("class").setText(classData.getAttributeValue("id"));
									
									// make the object bigger
									objectReference.getChild("graphics").getChild("size").setAttribute("width", "120");
									objectReference.getChild("graphics").getChild("size").setAttribute("height", "80");
									
									//sets the class
									objectClass = (Element)parameters[0];
									
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

								}

								
								
								domain.getChild("elements").getChild("objects").addContent(objectData);
								diagram.getChild("objects").addContent(objectReference);						
								
								ObjectCell object = new ObjectCell(objectData, objectReference, objectClass);										
								graph.getGraphLayoutCache().insert(object);
								
								ItTreeNode objectNode = new ItTreeNode(objectData.getChildText("name"), objectData, objectReference, null);
								objectNode.setIcon(new ImageIcon("resources/images/object.png"));
								treeModel.insertNodeInto(objectNode, diagramNode, diagramNode.getChildCount());
								
							}
							
						}
						
						toolBar.setSelectedButton(ItToolBar.SELECT);
						
						// this is done because jgraph was changing the cursor
						graph.setCursor(previousCursor);
					}
					break;
					case ItToolBar.STATE:{
						Element stateData = (Element)commonData.getChild("definedNodes").getChild("elements")
												.getChild("model").getChild("state").clone();					

						String id = String.valueOf(XMLUtilities.getId(diagram.getChild("states")));
						
						stateData.setAttribute("id", id);
						stateData.getChild("name").setText(stateData.getChild("name").getText()+" "+id);
						stateData.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(e.getX()));
						stateData.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(e.getY()));
						diagram.getChild("states").addContent(stateData);					
						
						StateCell state = new StateCell(stateData);										
						graph.getGraphLayoutCache().insert(state);
						
						ItTreeNode stateNode = new ItTreeNode(stateData.getChildText("name"), stateData, null, null);
						stateNode.setIcon(new ImageIcon("resources/images/state.png"));
						treeModel.insertNodeInto(stateNode, diagramNode, diagramNode.getChildCount());
						
						toolBar.setSelectedButton(ItToolBar.SELECT);
					}				
					break;
					case ItToolBar.INITIAL_STATE:{
						Element initialStateData = (Element)commonData.getChild("definedNodes").getChild("elements")
												.getChild("model").getChild("initialState").clone();					

						String id = String.valueOf(XMLUtilities.getId(diagram.getChild("states")));
						
						initialStateData.setAttribute("id", id);
						initialStateData.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(e.getX()));
						initialStateData.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(e.getY()));
						diagram.getChild("states").addContent(initialStateData);					
						
						InitialStateCell initialState = new InitialStateCell(initialStateData);										
						graph.getGraphLayoutCache().insert(initialState);
						
						toolBar.setSelectedButton(ItToolBar.SELECT);
					}				
					break;
					case ItToolBar.FINAL_STATE:{
						Element finalStateData = (Element)commonData.getChild("definedNodes").getChild("elements")
												.getChild("model").getChild("finalState").clone();					

						String id = String.valueOf(XMLUtilities.getId(diagram.getChild("states")));
						
						finalStateData.setAttribute("id", id);
						//initialStateData.getChild("name").setText(initialStateData.getChild("name").getText()+" "+id);
						finalStateData.getChild("graphics").getChild("position").setAttribute("x", String.valueOf(e.getX()));
						finalStateData.getChild("graphics").getChild("position").setAttribute("y", String.valueOf(e.getY()));
						diagram.getChild("states").addContent(finalStateData);					
						
						FinalStateCell finalState = new FinalStateCell(finalStateData);										
						graph.getGraphLayoutCache().insert(finalState);
						
						toolBar.setSelectedButton(ItToolBar.SELECT);
					}				
					break;
					case ItToolBar.USE_CASE_ASSOCIATION:{					
						
						createAssociation(ItToolBar.USE_CASE_ASSOCIATION, toolBar, e.getX(), e.getY(), project, diagram, commonData);					
					}
					break;
					case ItToolBar.CLASS_ASSOCIATION:{					
						createAssociation(ItToolBar.CLASS_ASSOCIATION, toolBar, e.getX(), e.getY(), project, diagram, commonData);					
					}
					break;
					case ItToolBar.ACTION_ASSOCIATION:{					
						createAssociation(ItToolBar.ACTION_ASSOCIATION, toolBar, e.getX(), e.getY(), project, diagram, commonData);					
					}
					break;
					case ItToolBar.GENERALIZATION:{
						createAssociation(ItToolBar.GENERALIZATION, toolBar, e.getX(), e.getY(), project, diagram, commonData);					
					}
					break;
					case ItToolBar.OBJECT_ASSOCIATION:{
						createAssociation(ItToolBar.OBJECT_ASSOCIATION, toolBar, e.getX(), e.getY(), project, diagram, commonData);					
					}
					break;
					case ItToolBar.DEPENDENCY:{
						createAssociation(ItToolBar.DEPENDENCY, toolBar, e.getX(), e.getY(), project, diagram, commonData);					
					}
					break;
					}
				}
				
			}
		}

		/**
		 * Handles edit trigger by starting the edit and return true if the
		 * editing has already started.
		 * 
		 * @param cell
		 *            the cell being edited
		 * @param e
		 *            the mouse event triggering the edit
		 * @return <code>true</code> if the editing has already started
		 */
		protected boolean handleEditTrigger(Object cell, MouseEvent e) {
			graph.scrollCellToVisible(cell);
			if (cell != null)
				startEditing(cell, e);
			return graph.isEditing();
		}

		public void mouseDragged(MouseEvent e) {
			autoscroll(graph, e.getPoint());
			if (graph.isEnabled()) {
				if (handler != null && handler == marquee)
					marquee.mouseDragged(e);
				else if (handler == null && !isEditing(graph) && focus != null) {
					if (!graph.isCellSelected(focus.getCell())) {
						selectCellForEvent(focus.getCell(), e);
						cell = null;
					}
					if (handle != null)
						handle.mousePressed(e);
					handler = handle;
				}
				if (handle != null && handler == handle)
					handle.mouseDragged(e);
			}
		}

		/**
		 * Invoked when the mouse pointer has been moved on a component (with no
		 * buttons down).
		 */
		public void mouseMoved(MouseEvent e) {
			if (previousCursor == null) {
				previousCursor = graph.getCursor();
			}
			if (graph != null && graph.isEnabled()) {
				if (marquee != null)
					marquee.mouseMoved(e);
				if (handle != null)
					handle.mouseMoved(e);
				if (!e.isConsumed() && previousCursor != null) {
					Cursor currentCursor = graph.getCursor();
					if (currentCursor != previousCursor) {
						graph.setCursor(previousCursor);
					}
					previousCursor = null;
				}
			}
		}

		// Event may be null when called to cancel the current operation.
		public void mouseReleased(MouseEvent e) {			
			try {
				if (e != null && !e.isConsumed() && graph != null
						&& graph.isEnabled()) {
					if (handler == marquee && marquee != null)
						marquee.mouseReleased(e);
					else if (handler == handle && handle != null)
						handle.mouseReleased(e);
					if (isDescendant(cell, focus) && e.getModifiers() != 0) {
						// Do not switch to parent if Special Selection
						cell = focus;
					}
					if (!e.isConsumed() && cell != null) {
						Object tmp = cell.getCell();
						boolean wasSelected = graph.isCellSelected(tmp);
						// if (!wasSelected || e.getModifiers() != 0)
						selectCellForEvent(tmp, e);
						focus = cell;
						postProcessSelection(e, tmp, wasSelected);
					}
				}
			} finally {
				handler = null;
				cell = null;
			}
			
			if (e.isPopupTrigger()){// this is for Windows
				if (graph instanceof ItGraph){
					ItGraph itGraph = (ItGraph)graph;					
					itGraph.getPopupMenu(graph.getFirstCellForLocation(e.getX(), e.getY())).show(graph, e.getX(), e.getY());
					
				}				
			}
			
		}

		/**
		 * Invoked after a cell has been selected in the mouseReleased method.
		 * This can be used to do something interesting if the cell was already
		 * selected, in which case this implementation selects the parent.
		 * Override if you want different behaviour, such as start editing.
		 */
		protected void postProcessSelection(MouseEvent e, Object cell,
				boolean wasSelected) {
			if (wasSelected && graph.isCellSelected(cell)
					&& e.getModifiers() != 0) {
				Object parent = cell;
				Object nextParent = null;
				while (((nextParent = graphModel.getParent(parent)) != null)
						&& graphLayoutCache.isVisible(nextParent))
					parent = nextParent;
				selectCellForEvent(parent, e);
				lastFocus = focus;
				focus = graphLayoutCache.getMapping(parent, false);
			}
		}

		protected boolean isDescendant(CellView parentView, CellView childView) {
			if (parentView == null || childView == null) {
				return false;
			}

			Object parent = parentView.getCell();
			Object child = childView.getCell();
			Object ancestor = child;

			do {
				if (ancestor == parent)
					return true;
			} while ((ancestor = graphModel.getParent(ancestor)) != null);

			return false;
		}

	}
	
	
}
