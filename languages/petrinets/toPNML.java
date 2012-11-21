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
*			Fernando Sette,
*			Victor Romero.
**/

package languages.petrinets;

import itSIMPLE.ItSIMPLE;
import languages.xml.XMLUtilities;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.LinkedList;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Parent;

public class toPNML {
	
	private static Element project = null;
	private static final int MODULE_OFFSET = 10;
	private static int maxY;
	private static int currentY;
	
	@SuppressWarnings("unchecked")
	public static Element buildPetriNet(Element project){
		
		toPNML.project = project;
		Element pnmlNodes = null;
		Element pnml = null;
		
		try {
			pnmlNodes = (Element)XMLUtilities.readFromFile("resources/settings/commonData.xml").getRootElement().getChild("petriNetNodes").getChild("pnmlNodes").clone();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(pnmlNodes != null){
			
			//1 get statesMachines
			List<Element> stateMachines = null;
			try {
				XPath path = new JDOMXPath("project/diagrams/stateMachineDiagrams/stateMachineDiagram");
				stateMachines = path.selectNodes(project.getDocument());
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			//stateCharts.remove(0);
			//stateCharts.remove(0);
			Element modular = stateMachinesListToModularPNML(stateMachines, project);
			pnml = modularPNMLToExtendedPTNet(modular);
			
		}
		return pnml;
	}
	
	@SuppressWarnings("unchecked")
	public static Element modularPNMLToExtendedPTNet(Element modularPetriNet){
		Element modularPNML = (Element) modularPetriNet.clone();
		Element pnml = null;
		Element pnmlNodes = null;
		
		try {
			pnmlNodes = (Element)XMLUtilities.readFromFile("resources/settings/commonData.xml").getRootElement().getChild("petriNetNodes").getChild("pnmlNodes");
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		Element modularPNMLNet = (Element) modularPNML.getChild("net").clone();
		if(pnmlNodes != null){
			pnml = (Element)pnmlNodes.getChild("pnml").clone();
			
			//1.0 set net
			Element net = (Element)pnmlNodes.getChild("net").clone();
			net.setAttribute("id","n1");
			net.setAttribute("type","http://www.pnml.org/version-2009/grammar/ptnet");
			//TODO net.setAttribute("type","http://www.informatik.hu-berlin.de/top/pnml/ptNetb");
			pnml.addContent(net);
			Element page = new Element("page");
                        page.setAttribute("id", "n1");
                        net.addContent(page);
			//get net places
			List<Element> placeList = modularPNML.getChild("net").getChildren("place");
			
			//get net transitions
			List<Element> transitionList = modularPNML.getChild("net").getChildren("transition");
			
			//get net arcs
			List<Element> arcList = modularPNML.getChild("net").getChildren("arc");
	
			List<Element> modulePlaceList = null;
			List<Element> moduleTransitionList = null;
			List<Element> moduleArcList = null;
			List<Element> moduleList = modularPNML.getChildren("module");
			//2. add all places, transitions and arcs
			
			for(Iterator<Element> moduleIter = moduleList.iterator(); moduleIter.hasNext();){
				Element module = moduleIter.next();
				
				//2.1 get module places
				modulePlaceList = module.getChildren("place");
				for (Iterator<Element> placeIter = modulePlaceList.iterator(); placeIter.hasNext();) {
					Element place = placeIter.next();
					String oldID = place.getAttributeValue("id");
					String newID = module.getAttributeValue("name") + oldID;
					place.setAttribute("id",newID);
					int y = Integer.parseInt(place.getChild("graphics").getChild("position").getAttributeValue("y"));
					place.getChild("graphics").getChild("position").setAttribute("y", Integer.toString(y));
					placeList.add((Element) place.clone());
					//if the place is a pseudoBox
				}
				
				//2.2 get module transitions
				moduleTransitionList = module.getChildren("transition");
				for (Iterator<Element> transitionIter = moduleTransitionList.iterator(); transitionIter.hasNext();) {
					Element transition = transitionIter.next();
					String newID = module.getAttributeValue("name") + transition.getAttributeValue("id");
					transition.setAttribute("id", newID);
					int y = Integer.parseInt(transition.getChild("graphics").getChild("position").getAttributeValue("y"));
					transition.getChild("graphics").getChild("position").setAttribute("y", Integer.toString(y));
					transitionList.add((Element) transition.clone());
				}
				
				//2.3 get module arcs
				
				//get arcs
				moduleArcList = module.getChildren("arc");
				for (Iterator<Element> arcIter = moduleArcList.iterator(); arcIter.hasNext();) {
					Element arc = arcIter.next();
					String newID = module.getAttributeValue("name") + arc.getAttributeValue("id");
					arc.setAttribute("id", newID);
					String sourceID = arc.getAttributeValue("source");

                                        /*Element toolspecific = new Element("toolspecific");
                                        toolspecific.setAttribute("tool", "itSIMPLE");
                                        Element itSettingsNode = ItSIMPLE.getItSettings();
                                        String version = "";
                                	if(itSettingsNode.getChild("version") != null)
                                            version = itSettingsNode.getChildText("version");

                                        toolspecific.setAttribute("version", version);
                                        Element type = arc.getChild("type");
                                        type.detach();
                                        toolspecific.addContent(type);
                                        arc.addContent(toolspecific);*/
					
					if(!sourceID.substring(0,1).equals("M")){
						Element referenceTransition = null;
						try {
							XPath path = new JDOMXPath("referenceTransition[@id='"+sourceID+"']");
							referenceTransition = (Element) path.selectSingleNode(module);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						String newSource = "";
						if(referenceTransition == null){
							Element referencePlace = null;
							try {
								XPath path = new JDOMXPath("referencePlace[@id='"+sourceID+"']");
								referencePlace = (Element) path.selectSingleNode(module);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							if(referencePlace == null)
								newSource = module.getAttributeValue("name") + arc.getAttributeValue("source");
							else{
								Element instanceImportPlace = null;
								try {
									XPath path = new JDOMXPath("instance[@ref='"+module.getAttributeValue("name")+"']/importPlace[@parameter='"+referencePlace.getAttributeValue("ref")+"']");
									instanceImportPlace = (Element) path.selectSingleNode(modularPNMLNet);
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
								if(instanceImportPlace != null){
										newSource = instanceImportPlace.getAttributeValue("ref");
								}
							}
						}	
						else{
							Element instanceImportTransition = null;
							try {
								XPath path = new JDOMXPath("instance[@ref='"+module.getAttributeValue("name")+"']/importTransition[@parameter='"+referenceTransition.getAttributeValue("ref")+"']");
								instanceImportTransition = (Element) path.selectSingleNode(modularPNMLNet);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							if(instanceImportTransition != null){
								String importRefID = instanceImportTransition.getAttributeValue("ref");
								String instanceID = instanceImportTransition.getAttributeValue("instance");
								Element instanceExportTransition = null;
								try {
									XPath path = new JDOMXPath("module[@name='"+instanceID+"']/interface/exportTransition[@id='"+importRefID+"']");
									instanceExportTransition = (Element) path.selectSingleNode(modularPetriNet);
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
								if(instanceExportTransition != null){
									newSource = instanceID + instanceExportTransition.getAttributeValue("ref");
								}
								else{
									Element netTransition = null;
									try {
										XPath path = new JDOMXPath("transition[@id='"+importRefID+"']");
										netTransition = (Element) path.selectSingleNode(modularPNMLNet);
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									newSource = netTransition.getAttributeValue("id");
								}
							}
						}
						arc.setAttribute("source", newSource);
					}
					String targetID = arc.getAttributeValue("target");
					if(!targetID.substring(0,1).equals("M")){
						Element referenceTransition = null;
						try {
							XPath path = new JDOMXPath("referenceTransition[@id='"+targetID+"']");
							referenceTransition = (Element) path.selectSingleNode(module);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						String newTarget = "";
						if(referenceTransition == null){
							Element referencePlace = null;
							try {
								XPath path = new JDOMXPath("referencePlace[@id='"+targetID+"']");
								referencePlace = (Element) path.selectSingleNode(module);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							if(referencePlace == null){
								newTarget = module.getAttributeValue("name") + arc.getAttributeValue("target");
							}
							else{
								Element instanceImportPlace = null;
								try {
									XPath path = new JDOMXPath("instance[@ref='"+module.getAttributeValue("name")+"']/importPlace[@parameter='"+referencePlace.getAttributeValue("ref")+"']");
									instanceImportPlace = (Element) path.selectSingleNode(modularPNMLNet);
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
								if(instanceImportPlace != null){
										newTarget = instanceImportPlace.getAttributeValue("ref");
								}
							}
							
						}
						else{
							Element instanceImportTransition = null;
							try {
								XPath path = new JDOMXPath("instance[@ref='"+module.getAttributeValue("name")+"']/importTransition[@parameter='"+referenceTransition.getAttributeValue("ref")+"']");
								instanceImportTransition = (Element) path.selectSingleNode(modularPNMLNet);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							if(instanceImportTransition != null){
								String importRefID = instanceImportTransition.getAttributeValue("ref");
								String instanceID = instanceImportTransition.getAttributeValue("instance");
								Element instanceExportTransition = null;
								try {
									XPath path = new JDOMXPath("module[@name='"+instanceID+"']/interface/exportTransition[@id='"+importRefID+"']");
									instanceExportTransition = (Element) path.selectSingleNode(modularPetriNet);
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
								if(instanceExportTransition != null){
									newTarget = instanceID + instanceExportTransition.getAttributeValue("ref");
								}
								else{
									Element netTransition = null;
									try {
										XPath path = new JDOMXPath("transition[@id='"+importRefID+"']");
										netTransition = (Element) path.selectSingleNode(modularPNMLNet);
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									if(netTransition != null){
										newTarget = netTransition.getAttributeValue("id");
									}
								}
							}
						}
						arc.setAttribute("target", newTarget);
					}

					arcList.add((Element) arc.clone());
				}

			}
			
			//3.1 add places
			for (Iterator<Element> PlaceIter = placeList.iterator(); PlaceIter.hasNext();) {
				Element place = PlaceIter.next();
				page.addContent((Element) place.clone());
			}
			
			//3.2 add transitions
			for (Iterator<Element> transitionIter = transitionList.iterator(); transitionIter.hasNext();) {
				Element transition = transitionIter.next();
				page.addContent((Element) transition.clone());
			}
			
			//3.3 add arcs
			for (Iterator<Element> arcIter = arcList.iterator(); arcIter.hasNext();) {
				Element arc = arcIter.next();
				page.addContent((Element) arc.clone());
			}
		}
		//XMLUtilities.printXML(pnml);
		return pnml;
	}
	
	@SuppressWarnings("unchecked")
	public static Element stateMachinesListToModularPNML(List<Element> stateMachineList, Element project){
		Element modularPNML = null;
		if(stateMachineList != null){
			toPNML.project = project;
			Element pnmlNodes = null;
			
			try {
				pnmlNodes = (Element)XMLUtilities.readFromFile("resources/settings/commonData.xml").getRootElement().getChild("petriNetNodes").getChild("pnmlNodes");
			} catch (JDOMException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			modularPNML = (Element)pnmlNodes.getChild("pnml").clone();
			
			//1.0 create net
			Element net = (Element)pnmlNodes.getChild("net").clone();
			
			net.setAttribute("id","n1");
			//TODO when all the modules are in the list, the net type is "http://www.informatik.hu-berlin.de/top/pnml/ptNetb"
			net.setAttribute("type","Extended P/T Net");
			
			modularPNML.addContent(net);
			
			//2.0 create modules
			maxY = 0;
			currentY = 0;
			
			//find if all the modules are present
			List<Element> projectStateMachinesList = null;
			try {
				XPath path = new JDOMXPath("project/diagrams/stateMachineDiagrams/stateMachineDiagram");
				projectStateMachinesList = path.selectNodes(project.getDocument());
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			
			boolean allModules = (stateMachineList.size()==projectStateMachinesList.size());
			
			for(Iterator<Element> stateMachinesIter = stateMachineList.iterator(); stateMachinesIter.hasNext();){
				Element stateMachineDiagram = stateMachinesIter.next();
				Element stPNML = stateMachineDiagramToPetriModule(stateMachineDiagram, allModules);
				modularPNML.addContent(stPNML);
			}
			
			//3.0 get modules and treat for repeated actions
			
			//3.1 get modules
			List<Element> moduleList = modularPNML.getChildren("module");
			//3.2 Treat modules for repeated actions
			
			//Get all import transitions ids
			HashSet<String> interfaceTransitionsSet = new HashSet<String>();
			for(Iterator<Element> moduleIter = moduleList.iterator(); moduleIter.hasNext();){
				Element module = moduleIter.next();
				List<Element> importTransitionsList = module.getChild("interface").getChildren("importTransition");
				for (Iterator<Element> transitionIter = importTransitionsList.iterator(); transitionIter.hasNext();) {
					Element importTransition = transitionIter.next();
					String id = importTransition.getAttributeValue("id");
					interfaceTransitionsSet.add(id);
				}
			}
			//Get all export transitions ids
			for(Iterator<Element> moduleIter = moduleList.iterator(); moduleIter.hasNext();){
				Element module = moduleIter.next();
				List<Element> importTransitionsList = module.getChild("interface").getChildren("exportTransition");
				for (Iterator<Element> transitionIter = importTransitionsList.iterator(); transitionIter.hasNext();) {
					Element importTransition = transitionIter.next();
					String id = importTransition.getAttributeValue("id");
					interfaceTransitionsSet.add(id);
				}
			}
			
			
			//get the number of each transitions necessary to solve the problem
			ArrayList<Comparable> interfaceTransitionsList = new ArrayList<Comparable>();
			for (Iterator<String> setIter = interfaceTransitionsSet.iterator(); setIter.hasNext();) {
				String id = setIter.next();
				interfaceTransitionsList.add(id);
				int cartesianProduct = 1;
				for(Iterator<Element> moduleIter = moduleList.iterator(); moduleIter.hasNext();){
					Element module = moduleIter.next();
					
					List<Element> importTransitionsList = null;
					try {
						XPath path = new JDOMXPath("interface/importTransition[@id='"+id+"']");
						importTransitionsList = path.selectNodes(module);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					
					if(importTransitionsList.size() == 0){
						List<Element> exportTransitionsList = null;
						try {
							XPath path = new JDOMXPath("interface/exportTransition[@id='"+id+"']");
							exportTransitionsList = path.selectNodes(module);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						if(exportTransitionsList.size() != 0){
							cartesianProduct = cartesianProduct*exportTransitionsList.size();
							//set precondition group
							for (int i = 0; i<exportTransitionsList.size(); i++) {
								Element exportTransition = exportTransitionsList.get(i);
								String refID = exportTransition.getAttributeValue("ref");
								
								Element transition = null;
								try {
									XPath path = new JDOMXPath("transition[@id='"+refID+"']");
									transition = (Element) path.selectSingleNode(module);
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
								transition.getChild("toolspecific").getChild("preconditionGroup").setText(Integer.toString(i));
							}
						}
					}
					else{	
						cartesianProduct = cartesianProduct*importTransitionsList.size();
						
						//set precondition group
						List<Element> referenceTransitionsList = null;
						try {
							XPath path = new JDOMXPath("referenceTransition[@ref='"+id+"']");
							referenceTransitionsList = path.selectNodes(module);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}

						for (int i = 0; i<referenceTransitionsList.size();i++) {
							Element referenceTransition = referenceTransitionsList.get(i);
							referenceTransition.getChild("toolspecific").getChild("preconditionGroup").setText(Integer.toString(i));
						}	
					}
				}
				interfaceTransitionsList.add(cartesianProduct);
			}
			
			//add extra transitions
			for (int i = 0; i < interfaceTransitionsList.size(); i++) {
				if(i%2 == 0){
					String id = (String) interfaceTransitionsList.get(i);
					int cartesianProduct = (Integer)interfaceTransitionsList.get(i+1);
					for(Iterator<Element> moduleIter = moduleList.iterator(); moduleIter.hasNext();){
						Element module = moduleIter.next();
						
						List<Element> importTransitionsList = null;
						try {
							XPath path = new JDOMXPath("interface/importTransition[@id='"+id+"']");
							importTransitionsList = path.selectNodes(module);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}

						if(importTransitionsList.size() == 0){
							List<Element> exportTransitionsList = null;
							try {
								XPath path = new JDOMXPath("interface/exportTransition[@id='"+id+"']");
								exportTransitionsList = path.selectNodes(module);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							if(exportTransitionsList.size() > 0){
								//add extra exportTransitions
								int extraTransitionsNumber = cartesianProduct/exportTransitionsList.size()-1;
								if(extraTransitionsNumber > 0) { 
									for (Iterator<Element> exportTransitionsIter = exportTransitionsList.iterator(); exportTransitionsIter.hasNext();) {
										Element exportTransition = exportTransitionsIter.next();
										String oldID = exportTransition.getAttributeValue("ref");
										exportTransition.setAttribute("ref", oldID + "_1");
										Element transition = null;
										try {
											XPath path = new JDOMXPath("transition[@id='"+oldID+"']");
											transition = (Element) path.selectSingleNode(module);
										} catch (JaxenException e) {			
											e.printStackTrace();
										}
										transition.setAttribute("id", oldID + "_1");
										
										List<Element> sourceArcsList = null;
										try {
											XPath path = new JDOMXPath("arc[@source='"+oldID+"']");
											sourceArcsList = path.selectNodes(module);
										} catch (JaxenException e) {			
											e.printStackTrace();
										}
										List<Element> targetArcsList = null;
										try {
											XPath path = new JDOMXPath("arc[@target='"+oldID+"']");
											targetArcsList = path.selectNodes(module);
										} catch (JaxenException e) {			
											e.printStackTrace();
										}
										int offset = 50;
										for(int j = 0; j < extraTransitionsNumber; j++){
											//System.out.println(j);
											Element extraExportTransition = (Element) exportTransition.clone();
											String newID = oldID + "_" +Integer.toString(j+2);
											extraExportTransition.setAttribute("ref", newID);
											
											Element extraTransition = (Element) transition.clone();
											extraTransition.setAttribute("id", newID);
											
											int currentY = Integer.parseInt(extraTransition.getChild("graphics").getChild("position").getAttributeValue("y"));
											extraTransition.getChild("graphics").getChild("position").setAttribute("y",Integer.toString(currentY+offset));									
											offset = offset + 50;
											module.getChild("interface").addContent(extraExportTransition);
											module.addContent(extraTransition);
											
											//XMLUtilities.printXML(extraTransition);
											
											//add extra arcs
											int k = 0;
											for (Iterator<Element> sourceArcsIter = sourceArcsList.iterator(); sourceArcsIter.hasNext();) {
												Element sourceArc = sourceArcsIter.next();
												String oldArcID = sourceArc.getAttributeValue("id");
												sourceArc.setAttribute("id", oldArcID + "_1");
												sourceArc.setAttribute("source", oldID + "_1");
												
												Element extraSourceArc = (Element) sourceArc.clone();
												extraSourceArc.setAttribute("id", oldArcID + "_" + Integer.toString(k+2));
												extraSourceArc.setAttribute("source", newID);
												//XMLUtilities.printXML(extraSourceArc);
												module.addContent(extraSourceArc);
												k++;
												List<Element> positionsList = extraSourceArc.getChild("graphics").getChildren("position");
												for (Iterator<Element> positionIter = positionsList.iterator(); positionIter.hasNext();) {
													Element position = positionIter.next();
													int y = Integer.parseInt(position.getAttributeValue("y")) + offset - 50;
													position.setAttribute("y", Integer.toString(y));
												}
											}
											
											//System.out.println(targetArcsList.size());
											k = 0;
											for (Iterator<Element> targetArcsIter = targetArcsList.iterator(); targetArcsIter.hasNext();) {
												Element targetArc = targetArcsIter.next();
												String oldArcID = targetArc.getAttributeValue("id");
												targetArc.setAttribute("id", oldArcID + "_1");
												targetArc.setAttribute("target", oldID + "_1");
												
												Element extraTargetArc = (Element) targetArc.clone();
												extraTargetArc.setAttribute("id", oldArcID + "_" + Integer.toString(k+2));
												extraTargetArc.setAttribute("target", newID);
												//XMLUtilities.printXML(extraTargetArc);
												module.addContent(extraTargetArc);
												k++;
												List<Element> positionsList = extraTargetArc.getChild("graphics").getChildren("position");
												for (Iterator<Element> positionIter = positionsList.iterator(); positionIter.hasNext();) {
													Element position = positionIter.next();
													int y = Integer.parseInt(position.getAttributeValue("y")) + offset - 50;
													position.setAttribute("y", Integer.toString(y));
												}
											}
										}
									}
								}
							}
						}
						else{
							//add extra importTransitions
							int extraTransitionsNumber = cartesianProduct/importTransitionsList.size()-1;
							int group = 0;
							if(extraTransitionsNumber > 0){
								for (Iterator<Element> importTransitionsIter = importTransitionsList.iterator(); importTransitionsIter.hasNext();) {
									Element importTransition = importTransitionsIter.next();
									Element toolspecific = null;
									try {
										XPath path = new JDOMXPath("referenceTransition[@ref='"+id+"']/toolspecific[preconditionGroup='" + Integer.toString(group) + "']");
										toolspecific = (Element) path.selectSingleNode(module);
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									Element referenceTransition = toolspecific.getParentElement();
									String oldID = referenceTransition.getAttributeValue("id");
									referenceTransition.setAttribute("id", oldID+"_1");
									
									List<Element> sourceArcsList = null;
									try {
										XPath path = new JDOMXPath("arc[@source='"+oldID+"']");
										sourceArcsList = path.selectNodes(module);
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									List<Element> targetArcsList = null;
									try {
										XPath path = new JDOMXPath("arc[@target='"+oldID+"']");
										targetArcsList = path.selectNodes(module);
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									int offset = 50;
									for(int j = 0; j < extraTransitionsNumber; j++){
										Element extraImportTransition = (Element) importTransition.clone();
										String newID = oldID + "_" + Integer.toString(j+2);
										Element extraRefTransition = (Element) referenceTransition.clone();
										extraRefTransition.setAttribute("id", newID);
										int currentY = Integer.parseInt(extraRefTransition.getChild("graphics").getChild("position").getAttributeValue("y"));
										extraRefTransition.getChild("graphics").getChild("position").setAttribute("y", Integer.toString(currentY+offset));
										offset = offset + 50;
										
										module.getChild("interface").addContent(extraImportTransition);
										module.addContent(extraRefTransition);

										//add extra arcs
										int k = 0;
										for (Iterator<Element> sourceArcsIter = sourceArcsList.iterator(); sourceArcsIter.hasNext();) {
											Element sourceArc = sourceArcsIter.next();
											String oldArcID = sourceArc.getAttributeValue("id");
											sourceArc.setAttribute("id", oldArcID + "_1");
											sourceArc.setAttribute("source",  oldID + "_1");

											Element extraSourceArc = (Element) sourceArc.clone();
											extraSourceArc.setAttribute("id", oldArcID + "_" + Integer.toString(k+2));
											extraSourceArc.setAttribute("source", newID);

											module.addContent(extraSourceArc);
											k++;
											List<Element> positionsList = extraSourceArc.getChild("graphics").getChildren("position");
											for (Iterator<Element> positionIter = positionsList.iterator(); positionIter.hasNext();) {
												Element position = positionIter.next();
												int y = Integer.parseInt(position.getAttributeValue("y")) + offset - 50;
												position.setAttribute("y", Integer.toString(y));
											}
										}

										k = 0;
										for (Iterator<Element> targetArcsIter = targetArcsList.iterator(); targetArcsIter.hasNext();) {
											Element targetArc = targetArcsIter.next();
											String oldArcID = targetArc.getAttributeValue("id");
											targetArc.setAttribute("id", oldArcID + "_1");
											targetArc.setAttribute("target", oldID + "_1");

											Element extraTargetArc = (Element) targetArc.clone();
											extraTargetArc.setAttribute("id", oldArcID + "_" + Integer.toString(k+2));
											extraTargetArc.setAttribute("target", newID);

											module.addContent(extraTargetArc);
											k++;
											List<Element> positionsList = extraTargetArc.getChild("graphics").getChildren("position");
											for (Iterator<Element> positionIter = positionsList.iterator(); positionIter.hasNext();) {
												Element position = positionIter.next();
												int y = Integer.parseInt(position.getAttributeValue("y")) + offset - 50;
												position.setAttribute("y", Integer.toString(y));
											}
										}

									}
									group++;
								}
							}
						}
					}
				}
			}
			
			/*for (Iterator iter = moduleList.iterator(); iter.hasNext();) {
				Element module = (Element) iter.next();
				XMLUtilities.printXML(module);
			}*/
			//make conections
			for(int i = 0; i < interfaceTransitionsList.size(); i++){
				if(i%2 == 0){
					String interfaceTransitionID = (String) interfaceTransitionsList.get(i);
					int cartesiaProduct = (Integer) interfaceTransitionsList.get(i+1);
					if(cartesiaProduct > 1){
						Element parentExportModule = null;
						ArrayList<Element> importModulesList = new ArrayList<Element>();
						//find which module export and which modules import the transition
						for (Iterator<Element> moduleIter = moduleList.iterator(); moduleIter.hasNext();) {
							Element module = moduleIter.next();
							Element exportTransition = null;
							try {
								XPath path = new JDOMXPath("interface/exportTransition[@id='"+interfaceTransitionID+"']");
								exportTransition = (Element) path.selectSingleNode(module);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							
							Element importTransition = null;
							try {
								XPath path = new JDOMXPath("interface/importTransition[@id='"+interfaceTransitionID+"']");
								importTransition = (Element) path.selectSingleNode(module);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							
							//if the module exports the transition
							if(exportTransition != null){
								parentExportModule = module;
							}
							
							//if the module exports the transition
							if(importTransition != null){
								importModulesList.add(module);
							}
						}
						
						//make links
						Element linkedModule = parentExportModule;
						for (Iterator<Element> importModulesIter = importModulesList.iterator(); importModulesIter.hasNext();) {
							Element moduleToBeLinked = importModulesIter.next();
							List<Element> linkedInterfaceTransitionsList = null;
							
							List<Element> transitionsToBeLinkedList = null;
							List<Element> linkedTransitionsList = null;
							
							//get transitions that are already linked
							if(linkedModule == parentExportModule){
								linkedTransitionsList = new ArrayList<Element>();
								try {
									XPath path = new JDOMXPath("interface/exportTransition[@id='"+interfaceTransitionID+"']");
									linkedInterfaceTransitionsList = path.selectNodes(linkedModule);
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
								
								for (Iterator<Element> linkedInterfaceTransitionsIter = linkedInterfaceTransitionsList.iterator(); linkedInterfaceTransitionsIter.hasNext();) {
									Element linkedInterfaceTransition = linkedInterfaceTransitionsIter.next();
									String transitionID = linkedInterfaceTransition.getAttributeValue("ref");
									
									Element linkedTransition = null;
									try {
										XPath path = new JDOMXPath("transition[@id='"+transitionID+"']");
										linkedTransition = (Element) path.selectSingleNode(linkedModule);
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
						
									linkedTransitionsList.add(linkedTransition);
								}
							}
							else{
								try {
									XPath path = new JDOMXPath("referenceTransition[@ref='"+interfaceTransitionID+"']");
									linkedTransitionsList = path.selectNodes(linkedModule);
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
							}
							
							//get transitions that must be linked
							try {
								XPath path = new JDOMXPath("referenceTransition[@ref='"+interfaceTransitionID+"']");
								transitionsToBeLinkedList = path.selectNodes(moduleToBeLinked);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							
							int groupA = 0;
							int groupB = 0;
							int count = 0;
						
							for (int j = 0; j<linkedTransitionsList.size(); j++) {
								Element linkedTransition = linkedTransitionsList.get(j);
								int group = Integer.parseInt(linkedTransition.getChild("toolspecific").getChildText("preconditionGroup"));
								if(group == groupA){
									boolean notLinked = true;
									Element transitionToBeLinked = null;
									//int nolink = 0;
									for (int k = 0; k<transitionsToBeLinkedList.size(); k++) {
										if(notLinked){
											transitionToBeLinked = transitionsToBeLinkedList.get(k);
											int transitionGroup = Integer.parseInt(transitionToBeLinked.getChild("toolspecific").getChildText("preconditionGroup"));
											if(transitionGroup == groupB){
												//System.out.println("Grupo "+groupA+" com Grupo "+groupB);
												String transitionID = linkedModule.getAttributeValue("name") + linkedTransition.getAttributeValue("id"); 
												String oldID = transitionToBeLinked.getAttributeValue("id");
												transitionToBeLinked.setAttribute("id", transitionID);
												
												//change arc ids
												List<Element> sourceArcsList = null;
												try {
													XPath path = new JDOMXPath("arc[@source='"+oldID+"']");
													sourceArcsList = path.selectNodes(moduleToBeLinked);
												} catch (JaxenException e) {			
													e.printStackTrace();
												}
												int arcCount = 0;
												for (Iterator<Element> sourceArcsIter = sourceArcsList.iterator(); sourceArcsIter.hasNext();) {
													Element sourceArc = sourceArcsIter.next();
													sourceArc.setAttribute("source", transitionID);
													arcCount++;
												}

												List<Element> targetArcsList = null;
												try {
													XPath path = new JDOMXPath("arc[@target='"+oldID+"']");
													targetArcsList = path.selectNodes(moduleToBeLinked);
												} catch (JaxenException e) {			
													e.printStackTrace();
												}
												arcCount = 0;
												for (Iterator<Element> targetArcsIter = targetArcsList.iterator(); targetArcsIter.hasNext();) {
													Element targetArc = targetArcsIter.next();
													targetArc.setAttribute("target", transitionID);
													arcCount++;
												}
												
												notLinked = false;
												groupB++;
											}
										}
									}
									if(transitionToBeLinked != null)
										transitionsToBeLinkedList.remove(transitionToBeLinked);
								}
								else
									count++;
								if(count < linkedTransitionsList.size() && j == linkedTransitionsList.size()-1){
									j = -1;
									groupA++;
									groupB = 0;
									count = 0;
								}
							}
							//update interfaces
							if(linkedModule == parentExportModule){
								for (Iterator<Element> linkedInterfaceTransitionsIter = linkedInterfaceTransitionsList.iterator(); linkedInterfaceTransitionsIter.hasNext();) {
									Element exportTransition = linkedInterfaceTransitionsIter.next();
									String newID = exportTransition.getAttributeValue("id") + linkedModule.getAttributeValue("name") + exportTransition.getAttributeValue("ref");
									exportTransition.setAttribute("id", newID);
								}
							}
							else{
								Element importTransition = null;
								do{
									importTransition = null;
									try {
										XPath path = new JDOMXPath("interface/importTransition[@id='"+interfaceTransitionID+"']");
										importTransition = (Element) path.selectSingleNode(linkedModule);
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									
									Element refTransition = null;
									try {
										XPath path = new JDOMXPath("referenceTransition[@ref='"+interfaceTransitionID+"']");
										refTransition = (Element) path.selectSingleNode(linkedModule);
									} catch (JaxenException e) {			
										e.printStackTrace();
									}
									if(refTransition != null){
										String newID = interfaceTransitionID + refTransition.getAttributeValue("id");
										importTransition.setAttribute("id", newID);
										refTransition.setAttribute("ref", newID);
									}
									
								}
								while(importTransition != null);
							}
							
							Element importTransition = null;
							do{
								importTransition = null;
								try {
									XPath path = new JDOMXPath("interface/importTransition[@id='"+interfaceTransitionID+"']");
									importTransition = (Element) path.selectSingleNode(moduleToBeLinked);
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
								
								Element refTransition = null;
								try {
									XPath path = new JDOMXPath("referenceTransition[@ref='"+interfaceTransitionID+"']");
									refTransition = (Element) path.selectSingleNode(moduleToBeLinked);
								} catch (JaxenException e) {			
									e.printStackTrace();
								}
								if(refTransition != null){
									String newID = interfaceTransitionID + refTransition.getAttributeValue("id");
									importTransition.setAttribute("id", newID);
									refTransition.setAttribute("ref", newID);
								}
								
							}
							while(importTransition != null);
							
							
							linkedModule = moduleToBeLinked;
						}
					}
				}
			}
		/*	for (Iterator iter = moduleList.iterator(); iter
					.hasNext();) {
				Element module = (Element) iter.next();
				XMLUtilities.printXML(module);
				
			}*/
			
			//4.0 create module instance in the net
			currentY = 0;
			maxY = 0;
			for(Iterator<Element> moduleIter = moduleList.iterator(); moduleIter.hasNext();){
				Element module = moduleIter.next();
				
				//4.1 create module instance
				Element instance = (Element)pnmlNodes.getChild("instanceNodes").getChild("instance").clone();
				instance.setAttribute("id",module.getAttributeValue("name"));
				instance.setAttribute("ref",module.getAttributeValue("name"));
				
				//4.2 deal with import transitions
				List<Element> importElements = module.getChild("interface").getChildren("importTransition");
				
				for (Iterator<Element> importIter = importElements.iterator(); importIter.hasNext();) {
					Element importTransition = importIter.next();
					Element exportTransition = null;
					try {
						XPath path = new JDOMXPath("module/interface/exportTransition[@id='"+importTransition.getAttributeValue("id")+"']");
						exportTransition = (Element)path.selectSingleNode(modularPNML);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					// if the transition is in another module
					if(exportTransition != null){
						Element exportModule = exportTransition.getParentElement().getParentElement();
						Element importTransitionInstance = (Element)pnmlNodes.getChild("instanceNodes").getChild("importTransition").clone();
						importTransitionInstance.setAttribute("parameter",importTransition.getAttributeValue("id"));
						Attribute attributeInstance = new Attribute("instance",exportModule.getAttributeValue("name")); 
						importTransitionInstance.setAttribute(attributeInstance);
						importTransitionInstance.setAttribute("ref",exportTransition.getAttributeValue("id"));
						instance.addContent(importTransitionInstance);	
					}
					//if the transition is not in another module
					else{
						//check if there is such transition in the net
						Element transitionToBeImported = null;
						try {
							XPath path = new JDOMXPath("net/transition[@id='"+importTransition.getAttributeValue("id")+"']");
							transitionToBeImported = (Element)path.selectSingleNode(modularPNML);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						//if no such transition exists
						Element importedTransition = null;
						if(transitionToBeImported == null){
							Element referenceTransition = null;
							try {
								XPath path = new JDOMXPath("module/referenceTransition[@ref='"+importTransition.getAttributeValue("id")+"']");
								referenceTransition = (Element)path.selectSingleNode(modularPNML);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							
							//add pseudobox
							
							//check if such pseudoBoxExists
							String classID = referenceTransition.getChild("toolspecific").getChildText("class");
							String operatorID = referenceTransition.getChild("toolspecific").getChildText("operator");
							Element existingPseudoBoxToolSpecific = null;
							try {
								XPath path = new JDOMXPath("net/place/toolspecific[class='"+classID+"' and operator='"+operatorID+"']");
								existingPseudoBoxToolSpecific = (Element) path.selectSingleNode(modularPNML);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							
							Element pseudoBox = null;
							if(existingPseudoBoxToolSpecific == null){
								pseudoBox = (Element)pnmlNodes.getChild("place").clone();
								pseudoBox.setAttribute("id", "ps-" + "cl" + classID + "op" + operatorID);
								pseudoBox.getChild("name").getChild("text").setText("external condition of " + referenceTransition.getChild("name").getChildText("text"));
								pseudoBox.getChild("toolspecific").getChild("type").setText("pseudoBox");
								//set pseudo box graphics
								String xBox = Integer.toString(60 + Integer.parseInt(referenceTransition.getChild("graphics").getChild("position").getAttributeValue("x")));
								String yBox = Integer.toString(60 + Integer.parseInt(referenceTransition.getChild("graphics").getChild("position").getAttributeValue("y")));
								pseudoBox.getChild("graphics").getChild("position").setAttribute("x",xBox);
								pseudoBox.getChild("graphics").getChild("position").setAttribute("y",yBox);
								//set toolspecific
								pseudoBox.removeChild("toolspecific");
								pseudoBox.addContent((Element) referenceTransition.getChild("toolspecific").clone());
								pseudoBox.getChild("toolspecific").removeChild("preconditionGroup");
								pseudoBox.getChild("toolspecific").getChild("type").setText("pseudoBox");
								//set marking
								pseudoBox.getChild("initialMarking").getChild("text").setText("1");
								
								net.addContent(pseudoBox);
							}
							else{
								pseudoBox = existingPseudoBoxToolSpecific.getParentElement();
							}
							//add transition to be imported
							importedTransition = (Element)pnmlNodes.getChild("transition").clone();
							importedTransition.setAttribute("id", importTransition.getAttributeValue("id"));
							importedTransition.getChild("name").getChild("text").setText(referenceTransition.getChild("name").getChildText("text"));
							String xTransition = referenceTransition.getChild("graphics").getChild("position").getAttributeValue("x"); 
							String yTransition = referenceTransition.getChild("graphics").getChild("position").getAttributeValue("y");
							importedTransition.getChild("graphics").getChild("position").setAttribute("x",xTransition);
							importedTransition.getChild("graphics").getChild("position").setAttribute("y",yTransition);
							//set tool specific
							importedTransition.removeChild("toolspecific");
							importedTransition.addContent((Element) referenceTransition.getChild("toolspecific").clone());
							importedTransition.getChild("toolspecific").removeChild("preconditionGroup");
							//add read arc
							Element arc = (Element)pnmlNodes.getChild("arc").clone();
							arc.setAttribute("source", pseudoBox.getAttributeValue("id"));
							arc.setAttribute("target",importedTransition.getAttributeValue("id"));
							arc.getChild("type").setAttribute("value", "read");
							
							net.addContent(importedTransition);
							net.addContent(arc);
						}
						else
							importedTransition = transitionToBeImported;
						//set and add instance elements
						Element importTransitionInstance = (Element)pnmlNodes.getChild("instanceNodes").getChild("importTransition").clone();
						importTransitionInstance.setAttribute("parameter",importedTransition.getAttributeValue("id"));
						importTransitionInstance.setAttribute("ref",importTransition.getAttributeValue("id"));
						instance.addContent(importTransitionInstance);
						
					}
				}
				//4.3 deal with export transitions
				List<Element> exportElements = null;
				try {
					XPath path = new JDOMXPath("interface/exportTransition");
					exportElements = path.selectNodes(module);
				} catch (JaxenException e) {
					e.printStackTrace();
				}
				int pseudoBoxCounter = 0;
				int importPlaceCounter = 0;
				for (Iterator<Element> exportIter = exportElements.iterator(); exportIter.hasNext();) {
					Element exportTransition = exportIter.next();
					List<Element> importTransitions = null;
					try {
						XPath path = new JDOMXPath("module/interface/importTransition[@id='"+exportTransition.getAttributeValue("id")+"']");
						importTransitions = path.selectNodes(modularPNML);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					
					//check how many classes import the action
					Element refTransition = null;
					try {
						XPath path = new JDOMXPath("transition[@id='"+exportTransition.getAttributeValue("ref")+"']");
						refTransition = (Element) path.selectSingleNode(module);
					} catch (JaxenException e) {
						e.printStackTrace();
					}
					List<Element> importClasses = null;
					String classID = refTransition.getChild("toolspecific").getChildText("class");
					String operatorID = refTransition.getChild("toolspecific").getChildText("operator");
					
					try {
						//TODO do the xpath considering the superclasses
						XPath path = new JDOMXPath("project/diagrams/stateMachineDiagrams/stateMachineDiagram[class!='"+classID+"']/associations/action[reference/@class='"+classID+"' and reference/@operator='"+operatorID+"']");
						importClasses = path.selectNodes(toPNML.project.getDocument());
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					HashSet<Parent> classSet = new HashSet<Parent>();
					//String id = "";
					for (Iterator<Element> iter = importClasses.iterator(); iter.hasNext();) {
						Element stateMachineDiagram = iter.next();
						classSet.add(stateMachineDiagram.getParent().getParent());
					}
					int numImports = classSet.size()-importTransitions.size();
					
					// if not all modules that import the transition are present in the net
					if(numImports != 0){
						//check if such pseudoBoxExists
						Element existingPseudoBoxToolSpecific = null;
						try {
							XPath path = new JDOMXPath("net/place/toolspecific[class='"+classID+"' and operator='"+operatorID+"']");
							existingPseudoBoxToolSpecific = (Element) path.selectSingleNode(modularPNML);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						Element pseudoBox = null;
						String xBox = Integer.toString(60 + Integer.parseInt(refTransition.getChild("graphics").getChild("position").getAttributeValue("x")));
						String yBox = Integer.toString(60 + Integer.parseInt(refTransition.getChild("graphics").getChild("position").getAttributeValue("y")));
						if(existingPseudoBoxToolSpecific == null){
							//add pseudobox
							pseudoBox = (Element)pnmlNodes.getChild("place").clone();
							pseudoBox.setAttribute("id", "ps" + Integer.toString(pseudoBoxCounter) + "-" + module.getAttributeValue("name"));	
							pseudoBoxCounter++;
							pseudoBox.getChild("name").getChild("text").setText("external condition of " + refTransition.getChild("name").getChildText("text"));
							//set initialMarking
							pseudoBox.getChild("initialMarking").getChild("text").setText("1");
							//set pseudo box graphics
							pseudoBox.getChild("graphics").getChild("position").setAttribute("x",xBox);
							pseudoBox.getChild("graphics").getChild("position").setAttribute("y",yBox);
							//set toolspecific
							pseudoBox.removeChild("toolspecific");
							pseudoBox.addContent((Element) refTransition.getChild("toolspecific").clone());
							pseudoBox.getChild("toolspecific").removeChild("preconditionGroup");
							pseudoBox.getChild("toolspecific").getChild("type").setText("pseudoBox");
							//set marking
							pseudoBox.getChild("initialMarking").getChild("text").setText("1");
							
							net.addContent(pseudoBox);
						}
							else{
							pseudoBox = existingPseudoBoxToolSpecific.getParentElement();
						}
							//add import place
							String importID = "ps" + Integer.toString(importPlaceCounter);
							Element importedPseudoBox = (Element)pnmlNodes.getChild("moduleNodes").getChild("importPlace").clone();
							importedPseudoBox.setAttribute("id", importID);
							Element moduleInterface = module.getChild("interface");
							moduleInterface.addContent(importedPseudoBox);
							//add reference place
							Element pseudoBoxReference = (Element)pnmlNodes.getChild("referencePlace").clone();
							pseudoBoxReference.setAttribute("id", "rp" + Integer.toString(importPlaceCounter));
							pseudoBoxReference.setAttribute("ref", importID);
							pseudoBoxReference.getChild("name").getChild("text").setText("external condition of " + refTransition.getChild("name").getChildText("text"));
							pseudoBoxReference.getChild("graphics").getChild("position").setAttribute("x",xBox);
							pseudoBoxReference.getChild("graphics").getChild("position").setAttribute("y",yBox);
							pseudoBoxReference.removeChild("toolspecific");
							//set reference pseudo box graphics
							pseudoBox.getChild("graphics").getChild("position").setAttribute("x",xBox);
							pseudoBox.getChild("graphics").getChild("position").setAttribute("y",yBox);
							//add read arc
							Element arc = (Element)pnmlNodes.getChild("arc").clone();
							arc.setAttribute("source",pseudoBoxReference.getAttributeValue("id"));
							arc.setAttribute("target",refTransition.getAttributeValue("id"));
							arc.getChild("type").setAttribute("value", "read");	
							module.addContent(pseudoBoxReference);
							module.addContent(arc);
							//	set and add instance elements
							Element importPlaceInstance = (Element)pnmlNodes.getChild("instanceNodes").getChild("importPlace").clone();
							importPlaceInstance.setAttribute("parameter", importID);
							importPlaceInstance.setAttribute("ref",pseudoBox.getAttributeValue("id"));
							instance.addContent(importPlaceInstance);
							importPlaceCounter++;
					}
				}
				net.addContent(instance);
			}
		}
		//XMLUtilities.printXML(pnml);
		return modularPNML;
	}
	
	@SuppressWarnings("unchecked")
	public static Element stateMachineDiagramToPetriModule(Element stateMachineDiagram, boolean allModules){
		
		Element pnmlNodes = null;
		Element itSettingsNode = null;
		Element module = null;
		
		pnmlNodes = (Element)ItSIMPLE.getCommonData().getChild("petriNetNodes").getChild("pnmlNodes").clone();
		itSettingsNode = ItSIMPLE.getItSettings();		
		
		String version = "";
		if(pnmlNodes != null){
			
			module = (Element)pnmlNodes.getChild("moduleNodes").getChild("module").clone();
			if(itSettingsNode.getChild("version") != null){
				version = itSettingsNode.getChildText("version");
			}
			//1 Get StateChart Class
			String classID = stateMachineDiagram.getChildText("class");
			
			// 2 create Petri net module of class
			module.setAttribute("name","M"+classID);
			
			//3 create places
			
			//get initial state id
			List<Element> firstActionEnds = null;
			HashSet<String> initialStateIDs = new HashSet<String>();
			try {
				XPath path = new JDOMXPath("associations/action/associationEnds/actionEnd[@element='initialState']");
				firstActionEnds = path.selectNodes(stateMachineDiagram);
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			
			for (Iterator<Element> iter = firstActionEnds.iterator(); iter.hasNext();) {
				Element firstActionEnd = iter.next();

				if(firstActionEnd != null){
					Element associationEnds = firstActionEnd.getParentElement();
					Element secondActionEnd = null;
					try {
						XPath path = new JDOMXPath("actionEnd[@id='2']");
						secondActionEnd = (Element)path.selectSingleNode(associationEnds);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(secondActionEnd != null){
						initialStateIDs.add(secondActionEnd.getAttributeValue("element-id"));
					}
				}
			}
			
			//3.1 Get states
			List<Element> states = stateMachineDiagram.getChild("states").getChildren("state");
			int placeNumberID = 0;
			for(Iterator<Element> statesIter = states.iterator(); statesIter.hasNext();){
				//3.2 get state
				Element state = statesIter.next();
				
				//3.3 add place to Petri net
				
				Element place = (Element)pnmlNodes.getChild("place").clone();
				
				//3.3.1 set place id
				place.setAttribute("id","p" + placeNumberID);
				
				//3.3.2 set place name
				place.getChild("name").getChild("text").setText(state.getChildText("name"));
				
				//3.3.3 set place graphics
				int stateX = Integer.parseInt(state.getChild("graphics").getChild("position").getAttributeValue("x"));
				int stateY = Integer.parseInt(state.getChild("graphics").getChild("position").getAttributeValue("y"));
				int stateWidth = Integer.parseInt(state.getChild("graphics").getChild("size").getAttributeValue("width"));
				int stateHeight = Integer.parseInt(state.getChild("graphics").getChild("size").getAttributeValue("height"));
				int x = stateX + stateWidth/2;
				int y = stateY + stateHeight/2 + currentY;
				//set place position considering the size of the place image
				place.getChild("graphics").getChild("position").setAttribute("x", Integer.toString(x-19));
				place.getChild("graphics").getChild("position").setAttribute("y", Integer.toString(y-19));
				if(y > maxY){
					maxY = y;
				}
				
				//set toolspecific
				place.getChild("toolspecific").setAttribute("version",version);
				place.getChild("toolspecific").getChild("state").setText(state.getAttributeValue("id"));
				place.getChild("toolspecific").getChild("class").setText(stateMachineDiagram.getChildText("class"));
				
				//set marking
				if(initialStateIDs.contains(state.getAttributeValue("id"))){
					place.getChild("initialMarking").getChild("text").setText("1");
				}
				
				//3.3.4 add place
				module.addContent(place);
				
				placeNumberID++;
				
			}
			
			//5 create transtitions and arcs
			
			//5.1 get actions
			List<Element> actions = stateMachineDiagram.getChild("associations").getChildren("action");
			LinkedList<Element> arcList = new LinkedList<Element>();
			int arcNumberID = 0;
			int transitionNumberID = 0;
			for(Iterator<Element> actionsIter = actions.iterator(); actionsIter.hasNext();){
				
				//5.2 get action
				Element action = actionsIter.next();
				
				//5.3 create transitions
				
				//5.3.1 set transition id
				Element transition = null;
				
				//5.3.2 get transition type
				Element operator = null;
				Element operatorClass = null;
				try {
					XPath path = new JDOMXPath("project/elements/classes/class[@id='"+action.getChild("reference").getAttributeValue("class")+"']" +
							"/operators/operator[@id='"+action.getChild("reference").getAttributeValue("operator")+"']");
					operator = (Element)path.selectSingleNode(project.getDocument());
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				String transitionID = "";
				String transitionName = "";
				if(operator != null){
					operatorClass = operator.getParentElement().getParentElement();
					transitionID = "t" + transitionNumberID;
					//String transitionID = "cl"+operatorClass.getAttributeValue("id")+"op"+operator.getAttributeValue("id");
					transitionName = "";
					//import case
					if(!operatorClass.getAttributeValue("id").equals(classID)){
						transitionName = operatorClass.getChildText("name") + "::" + operator.getChildText("name");
						Element moduleInterface = (Element)module.getChild("interface");
						Element importTransition = (Element)pnmlNodes.getChild("moduleNodes").getChild("importTransition").clone();
						importTransition.setAttribute("id","cl"+operatorClass.getAttributeValue("id")+"op"+operator.getAttributeValue("id"));
						moduleInterface.addContent(importTransition);
						transition = (Element)pnmlNodes.getChild("referenceTransition").clone();
						transition.setAttribute("ref","cl"+operatorClass.getAttributeValue("id")+"op"+operator.getAttributeValue("id"));
						transition.getChild("toolspecific").getChild("type").setText("import");
					}
					else{
						//check if action is present in other diagrams
						List<Element> exportedActions = null;
						try {
							XPath path = new JDOMXPath("project/diagrams/stateMachineDiagrams/stateMachineDiagram[@id!="+stateMachineDiagram.getAttributeValue("id")+"]/associations/action[reference/@class="+classID+" and reference/@operator="+operator.getAttributeValue("id")+"]");
							exportedActions = path.selectNodes(project.getDocument());
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
						//export
						if(exportedActions.size()>0){
							transitionName = operator.getChildText("name");
							Element moduleInterface = (Element)module.getChild("interface");
							Element exportTransition = (Element)pnmlNodes.getChild("moduleNodes").getChild("exportTransition").clone();
							exportTransition.setAttribute("id","cl"+operatorClass.getAttributeValue("id")+"op"+operator.getAttributeValue("id"));
							exportTransition.setAttribute("ref",transitionID);
							moduleInterface.addContent(exportTransition);
							transition = (Element)pnmlNodes.getChild("transition").clone();
							if(!allModules)
								transition.getChild("toolspecific").getChild("type").setText("export");
						}
						//internal
						else{
							transitionName = operator.getChildText("name");
							transition = (Element)pnmlNodes.getChild("transition").clone();
							transitionID = "t" + transitionNumberID;
						}
					}
					//set toolspecific
					transition.getChild("toolspecific").setAttribute("version",version);
					transition.getChild("toolspecific").getChild("class").setText(operatorClass.getAttributeValue("id"));
					transition.getChild("toolspecific").getChild("operator").setText(operator.getAttributeValue("id"));
					
					//set transition name
					transition.getChild("name").getChild("text").setText(transitionName);
				}
				else{
					transition = (Element)pnmlNodes.getChild("transition").clone();
					transitionID = "t" + transitionNumberID;
					//transition.removeChild("name");
					//set toolspecific
					transition.getChild("toolspecific").setAttribute("version",version);
					transition.getChild("toolspecific").removeChild("class");
					transition.getChild("toolspecific").removeChild("operator");
					transition.getChild("toolspecific").removeChild("preconditionGroup");
				}
				transition.setAttribute("id", transitionID);
				//5.3.3 set transition graphics
				List<Element> actionPoints = action.getChild("graphics").getChild("points").getChildren("point");
				
				List<Element> actionEnds = null;
				try {
					XPath path = new JDOMXPath("associationEnds/actionEnd");
					actionEnds = path.selectNodes(action);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				
				//5.3.3.1 get position
				
				String sourceID = null;
				String targetID = null;
				
				for(Iterator<Element> associationIter = actionEnds.iterator(); associationIter.hasNext();){
					Element actionEnd = associationIter.next();
					if(actionEnd.getAttributeValue("navigation").equals("false")){
						sourceID = actionEnd.getAttributeValue("element-id");
					}
					else if(actionEnd.getAttributeValue("navigation").equals("true")){
						targetID = actionEnd.getAttributeValue("element-id");
					}
				}
				Element sourceState = null;
				try {
					XPath path = new JDOMXPath("states/state[@id='"+sourceID+"']");
					sourceState = (Element)path.selectSingleNode(stateMachineDiagram);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				
				Element targetState = null;
				try {
					XPath path = new JDOMXPath("states/state[@id='"+targetID+"']");
					targetState = (Element)path.selectSingleNode(stateMachineDiagram);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(sourceState != null && targetState != null){
					int x = 0;
					int y = 0;
					int actionPointsSize = actionPoints.size();
					//no points
					if(actionPointsSize == 0){

						int stateX = Integer.parseInt(sourceState.getChild("graphics").getChild("position").getAttributeValue("x"));
						int stateY = Integer.parseInt(sourceState.getChild("graphics").getChild("position").getAttributeValue("y"));
						int stateWidth = Integer.parseInt(sourceState.getChild("graphics").getChild("size").getAttributeValue("width"));
						int stateHeight = Integer.parseInt(sourceState.getChild("graphics").getChild("size").getAttributeValue("height"));
						int xPositionSource = stateX + stateWidth/2;
						int yPositionSource = stateY + stateHeight/2;

						stateX = Integer.parseInt(targetState.getChild("graphics").getChild("position").getAttributeValue("x"));
						stateY = Integer.parseInt(targetState.getChild("graphics").getChild("position").getAttributeValue("y"));
						stateWidth = Integer.parseInt(targetState.getChild("graphics").getChild("size").getAttributeValue("width"));
						stateHeight = Integer.parseInt(targetState.getChild("graphics").getChild("size").getAttributeValue("height"));
						int xPositionTarget = stateX + stateWidth/2;
						int yPositionTarget = stateY + stateHeight/2;

						x = (xPositionSource+xPositionTarget)/2;
						y = currentY + (yPositionSource+yPositionTarget)/2;
						if(y > maxY){
							maxY = y;
						}
					}
					//even number of points
					else if(actionPointsSize % 2 == 0){
						Element pointOne = actionPoints.get(actionPointsSize/2-1);
						Element pointTwo = actionPoints.get(actionPointsSize/2);

						int xPositionOne = Integer.parseInt(pointOne.getAttributeValue("x"));
						int xPositionTwo = Integer.parseInt(pointTwo.getAttributeValue("x"));
						int yPositionOne = Integer.parseInt(pointOne.getAttributeValue("y"));
						int yPositionTwo = Integer.parseInt(pointTwo.getAttributeValue("y"));

						x = (xPositionOne+xPositionTwo)/2;
						y = currentY + (yPositionOne+yPositionTwo)/2;
						if(y > maxY){
							maxY = y;
						}
					}
					//odd number of points
					else if(actionPointsSize % 2 != 0){
						Element point = actionPoints.get(actionPointsSize/2);

						x = Integer.parseInt(point.getAttributeValue("x"));
						y = currentY + Integer.parseInt(point.getAttributeValue("y"));
						if(y > maxY){
							maxY = y;
						}
					}

					//set the position considering the size of the image 
					transition.getChild("graphics").getChild("position").setAttribute("x",Integer.toString(x-12));
					transition.getChild("graphics").getChild("position").setAttribute("y",Integer.toString(y-19));

					//5.3.4 add transition
					module.addContent(transition);
					transitionNumberID ++;

					//5.4 add arcs

					Element sourcePlace = null;

					try {
						XPath path = new JDOMXPath("place[toolspecific/class='" + classID + "' and toolspecific/state='" + sourceState.getAttributeValue("id") + "']");
						sourcePlace = (Element) path.selectSingleNode(module);
					} catch (JaxenException e) {
						e.printStackTrace();
					}

					Element targetPlace = null;
					try {
						XPath path = new JDOMXPath("place[toolspecific/class='" + classID + "' and toolspecific/state='" + targetState.getAttributeValue("id") + "']");
						targetPlace = (Element) path.selectSingleNode(module);
					} catch (JaxenException e) {
						e.printStackTrace();
					}

					Element arc1 = (Element)pnmlNodes.getChild("arc").clone();
					arc1.setAttribute("id","arc" + Integer.toString(arcNumberID));
					arc1.setAttribute("source",sourcePlace.getAttributeValue("id"));
					arc1.setAttribute("target",transition.getAttributeValue("id"));
					arc1.getChild("type").setAttribute("value", "normal");

					arcNumberID = arcNumberID+1;
					Element arc2 = (Element)pnmlNodes.getChild("arc").clone();
					arc2.setAttribute("id","arc" + Integer.toString(arcNumberID));
					arc2.setAttribute("source",transition.getAttributeValue("id"));
					arc2.setAttribute("target",targetPlace.getAttributeValue("id"));
					arc2.getChild("type").setAttribute("value", "normal");

					if(actionPointsSize > 0){
						if(actionPointsSize % 2 == 0){
							for(int i = 0; i < actionPointsSize; i++){
								Element point = actionPoints.get(i);
								Element position = (Element)pnmlNodes.getChild("position").clone();
								int positionX = Integer.parseInt(point.getAttributeValue("x"));
								int positionY = currentY + Integer.parseInt(point.getAttributeValue("y"));
								if(positionY > maxY){
									maxY = positionY;
								}
								position.setAttribute("x", Integer.toString(positionX));
								position.setAttribute("y", Integer.toString(positionY));
								if(i < actionPointsSize/2){
									arc1.getChild("graphics").addContent(position);
								}
								else{
									arc2.getChild("graphics").addContent(position);
								}
							}
						}
					}

					//5.5 set arc graphics
					arcList.add(arc1);
					arcList.add(arc2);
					arcNumberID++;
				}
			}
			for(Iterator<Element> arcIter = arcList.iterator(); arcIter.hasNext();){
				Element currentArc = arcIter.next();
				module.addContent(currentArc);
			}
			currentY = maxY + MODULE_OFFSET;
		}
		return module;
	}
	
	/*//Teste
	public static void main(String[] args){
		Element project = null;
		try {
			project = XMLUtilities.readFromFile("examples/BlocksDomainv1.xml").getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(project != null){
			Element tree = toPNML.buildPetriNet(project);
			XMLUtilities.printXML(tree);
		}
	}*/
}
