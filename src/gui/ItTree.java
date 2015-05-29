/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2012 University of Sao Paulo
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

package src.gui;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Iterator;

import javax.swing.ImageIcon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.Element;

import src.languages.xml.XMLUtilities;
import src.util.filefilter.PDDLFileFilter;

/**
 * A JTree extension which uses XML structures to manage its data.
 * This class uses the JDom library to set/get values of XML tags and to
 * navigate through the XML nodes.
 *  
 */
public class ItTree extends JTree{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4566958866222711984L;	
	
	
	private DefaultTreeModel treeModel = (DefaultTreeModel)this.getModel();

	public ItTree(TreeNode node) {
		super(node);
	}
	
	public void buildDiagramNode(Element xmlProject, ItTreeNode treeProject){					
		Iterator diagramsIterator = xmlProject.getChild("diagrams").getChildren().iterator();
		while(diagramsIterator.hasNext()){		
			Element diagrams = (Element) diagramsIterator.next();			
			
                        //chevere KaosDiagram
                        if(diagrams.getName().equals("KaosDiagrams")){
                            
                            Element kaosDiagram = diagrams.getChild("name");
                            Element goalDiagram = diagrams.getChild("GoalDiagram");
                            Element operDiagram = diagrams.getChild("OperDiagram");
                            Element objectDiagram = diagrams.getChild("ObjectDiagram");
                            Element agentDiagram = diagrams.getChild("AgentDiagram");
                            ItTreeNode treeKAOS = new ItTreeNode(kaosDiagram.getText(),kaosDiagram, null, null);
                            
                            ItTreeNode treeGoal = new ItTreeNode(goalDiagram.getChild("name").getText(),goalDiagram, null, null);
                            //treeGoal.setIcon(new ImageIcon("resources/images/useCaseDiagram.png"));
                            treeGoal.setIcon(new ImageIcon("resources/images/goal_icono.png"));
                            
                            
                            //Insering all goals expectation for a goal diagram
                            Iterator list_goals_expectation =  goalDiagram.getChild("goalsExpe").getChildren("goal").iterator();//(Element)goalDiagram.getChildren("goals");
                             
                            while (list_goals_expectation.hasNext()){
					Element goal = (Element)list_goals_expectation.next();
					ItTreeNode treeDiagram = new ItTreeNode(goal.getChildText("name"),
							                                goal, null, null);
					treeDiagram.setIcon(new ImageIcon("resources/images/expetativa.png"));
					treeModel.insertNodeInto(treeDiagram, treeGoal, treeGoal.getChildCount());
									
				}
                            //Insering all goals expectation for a goal diagram
                            Iterator list_goals_requi =  goalDiagram.getChild("goalsReq").getChildren("goal").iterator();//(Element)goalDiagram.getChildren("goals");
                             
                            while (list_goals_requi.hasNext()){
					Element goal = (Element)list_goals_requi.next();
					ItTreeNode treeDiagram = new ItTreeNode(goal.getChildText("name"),
							                                goal, null, null);
					treeDiagram.setIcon(new ImageIcon("resources/images/requirement.png"));
					treeModel.insertNodeInto(treeDiagram, treeGoal, treeGoal.getChildCount());
									
				}
                            
                            //Insering all agents for agoal diagram.
                            Iterator list_agents =  goalDiagram.getChild("agents").getChildren("agent").iterator();//(Element)goalDiagram.getChildren("goals");
                             
                            while (list_agents.hasNext()){
					Element agent = (Element)list_agents.next();
					ItTreeNode treeDiagram = new ItTreeNode(agent.getChildText("name"),
							                                agent, null, null);
					treeDiagram.setIcon(new ImageIcon("resources/images/agent1.png"));
					treeModel.insertNodeInto(treeDiagram, treeGoal, treeGoal.getChildCount());
									
				}
                         
                            
                            
                            
                            ItTreeNode treeOp = new ItTreeNode(operDiagram.getChild("name").getText(),operDiagram, null, null);
                            treeOp.setIcon(new ImageIcon("resources/images/useCaseDiagram.png"));
                            
                            ItTreeNode treeObj = new ItTreeNode(objectDiagram.getChild("name").getText(),objectDiagram, null, null);
                            treeObj.setIcon(new ImageIcon("resources/images/useCaseDiagram.png"));
                            
                            ItTreeNode treeAg = new ItTreeNode(agentDiagram.getChild("name").getText(),agentDiagram, null, null);
                            treeAg.setIcon(new ImageIcon("resources/images/useCaseDiagram.png"));
                        
                            treeModel.insertNodeInto(treeGoal, treeKAOS, treeKAOS.getChildCount());
                            treeModel.insertNodeInto(treeOp, treeKAOS, treeKAOS.getChildCount());
                            treeModel.insertNodeInto(treeObj, treeKAOS, treeKAOS.getChildCount());
                            treeModel.insertNodeInto(treeAg, treeKAOS, treeKAOS.getChildCount());
                            
                       
                            treeKAOS.setIcon(new ImageIcon("resources/images/useCaseDiagram.png"));
			    treeModel.insertNodeInto(treeKAOS, treeProject, treeProject.getChildCount());
                            
                            
                                
                               /* Element kaosDiagram = diagrams.getChild("name");
                                ItTreeNode name = new ItTreeNode(kaosDiagram.getText(), kaosDiagram, null, null);
                                
                                
                                
                                
                                Element goalDiagram1 = diagrams.getChild("GoalDiagram");
                                
                                
                                name.setIcon(new ImageIcon("resources/images/useCaseDiagram.png"));
                                treeModel.insertNodeInto(name, treeProject, treeProject.getChildCount());
                                
				Iterator goalDiagrams = diagrams.getChildren("GoalDiagram").iterator();				
				while (goalDiagrams.hasNext()){
                                    
					Element goalDiagram = (Element)goalDiagrams.next();
					ItTreeNode treeDiagram = new ItTreeNode(goalDiagram.getChildText("name"),
							                                goalDiagram, null, null);
					treeDiagram.setIcon(new ImageIcon("resources/images/useCaseDiagram.png"));
					treeModel.insertNodeInto(treeDiagram, treeProject, treeProject.getChildCount());
					
					Iterator agents = goalDiagram.getChild("agents").getChildren("agent").iterator();
					while (agents.hasNext()){
						Element actor = (Element) agents.next();
						ItTreeNode treeActor = new ItTreeNode(actor.getChildText("name"), actor, null, null);
						treeActor.setIcon(new ImageIcon("resources/images/agent1.png"));
						treeDiagram.add(treeActor);				
					}
					
					Iterator useCases = goalDiagram.getChild("goals").getChildren("goal").iterator();
					while (useCases.hasNext()){
						Element useCase = (Element) useCases.next();
						ItTreeNode treeUseCase = new ItTreeNode(useCase.getChildText("name"), useCase, null, null);
						treeUseCase.setIcon(new ImageIcon("resources/images/goal1.png"));
						treeDiagram.add(treeUseCase);
					}					
				}*//*
                                Iterator agentDiagram = diagrams.getChildren("AgentDiagram").iterator();				
				while (goalDiagrams.hasNext()){
					Element useCaseDiagram = (Element)goalDiagrams.next();
					ItTreeNode treeDiagram = new ItTreeNode(useCaseDiagram.getChildText("name"),
							                                useCaseDiagram, null, null);
					treeDiagram.setIcon(new ImageIcon("resources/images/useCaseDiagram.png"));
					treeModel.insertNodeInto(treeDiagram, treeProject, treeProject.getChildCount());
					
					Iterator agents = useCaseDiagram.getChild("agents").getChildren("agent").iterator();
					while (agents.hasNext()){
						Element actor = (Element) agents.next();
						ItTreeNode treeActor = new ItTreeNode(actor.getChildText("name"), actor, null, null);
						treeActor.setIcon(new ImageIcon("resources/images/agent1.png"));
						treeDiagram.add(treeActor);				
					}
					
					Iterator useCases = useCaseDiagram.getChild("goals").getChildren("goal").iterator();
					while (useCases.hasNext()){
						Element useCase = (Element) useCases.next();
						ItTreeNode treeUseCase = new ItTreeNode(useCase.getChildText("name"), useCase, null, null);
						treeUseCase.setIcon(new ImageIcon("resources/images/goal1.png"));
						treeDiagram.add(treeUseCase);
					}					
				}*/
			}
                        //chevere
                        
			//Use Case Diagrams
			if(diagrams.getName().equals("useCaseDiagrams")){
				Iterator useCaseDiagrams = diagrams.getChildren("useCaseDiagram").iterator();				
				while (useCaseDiagrams.hasNext()){
					Element useCaseDiagram = (Element)useCaseDiagrams.next();
					ItTreeNode treeDiagram = new ItTreeNode(useCaseDiagram.getChildText("name"),
							                                useCaseDiagram, null, null);
					treeDiagram.setIcon(new ImageIcon("resources/images/useCaseDiagram.png"));
					treeModel.insertNodeInto(treeDiagram, treeProject, treeProject.getChildCount());
					
					Iterator actors = useCaseDiagram.getChild("actors").getChildren("actor").iterator();
					while (actors.hasNext()){
						Element actor = (Element) actors.next();
						ItTreeNode treeActor = new ItTreeNode(actor.getChildText("name"), actor, null, null);
						treeActor.setIcon(new ImageIcon("resources/images/actor.png"));
						treeDiagram.add(treeActor);				
					}
					
					Iterator useCases = useCaseDiagram.getChild("useCases").getChildren("useCase").iterator();
					while (useCases.hasNext()){
						Element useCase = (Element) useCases.next();
						ItTreeNode treeUseCase = new ItTreeNode(useCase.getChildText("name"), useCase, null, null);
						treeUseCase.setIcon(new ImageIcon("resources/images/useCase.png"));
						treeDiagram.add(treeUseCase);
					}					
				}
			}
			
			//Class Diagrams
			else if(diagrams.getName().equals("classDiagrams")){
				Iterator classDiagrams = diagrams.getChildren("classDiagram").iterator();
				while (classDiagrams.hasNext()){
					Element classDiagram = (Element)classDiagrams.next();
					ItTreeNode treeDiagram = new ItTreeNode(classDiagram.getChildText("name"),
                            							classDiagram, null, null);
					treeDiagram.setIcon(new ImageIcon("resources/images/classDiagram.png"));
					treeModel.insertNodeInto(treeDiagram, treeProject, treeProject.getChildCount());
					
					Iterator classes = classDiagram.getChild("classes").getChildren("class").iterator();
					while (classes.hasNext()){
						Element classReference = (Element) classes.next();
						Element Class = getElement(xmlProject, "classes", classReference.getAttributeValue("id"));
						if (Class != null){
							ItTreeNode treeClass = new ItTreeNode(Class.getChildText("name"),
	    													Class, classReference, null);
							treeClass.setIcon(new ImageIcon("resources/images/class.png"));
							treeDiagram.add(treeClass);
							
							Iterator attributes = Class.getChild("attributes").getChildren("attribute").iterator();
							for (int i=0;attributes.hasNext();i++){
								Element attribute = (Element)attributes.next();
								ItTreeNode treeAttribute = new ItTreeNode (attribute.getChildText("name"),attribute,
																			null, null);
								treeAttribute.setIcon(new ImageIcon("resources/images/attribute.png"));
								treeClass.add(treeAttribute);
							}
							
							Iterator operators = Class.getChild("operators").getChildren("operator").iterator();
							while (operators.hasNext()){						
								Element operator = (Element)operators.next();
								ItTreeNode treeOperator = new ItTreeNode (operator.getChildText("name"),operator,
																			null, null);
								treeOperator.setIcon(new ImageIcon("resources/images/operator.png"));
								treeClass.add(treeOperator);
							}
						}
					}
				}
			}
			
			//State Chart Diagrams			
			else if (diagrams.getName().equals("stateMachineDiagrams")){
				Iterator stateDiagrams = diagrams.getChildren("stateMachineDiagram").iterator();
				while(stateDiagrams.hasNext()){
					Element stateDiagram = (Element)stateDiagrams.next();
					
					//Get class
					Element classReference = null;
					if (!stateDiagram.getChildText("class").equals("")){
						try {
							XPath path = new JDOMXPath("project/elements/classes/class[@id="+stateDiagram.getChildText("class")+"]");
							classReference = (Element)path.selectSingleNode(stateDiagram.getDocument());
						} catch (JaxenException e2) {			
							e2.printStackTrace();
						}
					}		
					
					ItTreeNode treeDiagram = new ItTreeNode(stateDiagram.getChildText("name"), stateDiagram,
															null, classReference);
					treeDiagram.setIcon(new ImageIcon("resources/images/stateMachineDiagram.png"));
					treeModel.insertNodeInto(treeDiagram, treeProject, treeProject.getChildCount());					
					
					Iterator states = stateDiagram.getChild("states").getChildren("state").iterator();
					while(states.hasNext()){
						Element state = (Element)states.next();
						ItTreeNode treeState = new ItTreeNode(state.getChildText("name"), state,
														null, null);
						treeState.setIcon(new ImageIcon("resources/images/state.png"));						
						treeDiagram.add(treeState);
					}
				}
			}

			//Activity Diagrams			
			else if (diagrams.getName().equals("activityDiagrams")){
				Iterator activityDiagrams = diagrams.getChildren("activityDiagram").iterator();
				while(activityDiagrams.hasNext()){
					Element activityDiagram = (Element)activityDiagrams.next();
					ItTreeNode treeDiagram = new ItTreeNode(activityDiagram.getChildText("name"),
															activityDiagram, null, null);
					treeDiagram.setIcon(new ImageIcon("resources/images/activityDiagram.png"));
					treeModel.insertNodeInto(treeDiagram, treeProject, treeProject.getChildCount());					

				}				
				
			}
			//Timing Diagrams
			else if (diagrams.getName().equals("timingDiagrams")){
				Iterator timingDiagrams = diagrams.getChildren("timingDiagram").iterator();
				while(timingDiagrams.hasNext()){
					Element timingDiagram = (Element)timingDiagrams.next();
					ItTreeNode treeDiagram = new ItTreeNode(timingDiagram.getChildText("name"),
															timingDiagram, null, null);
					treeDiagram.setIcon(new ImageIcon("resources/images/activityDiagram.png"));
					treeModel.insertNodeInto(treeDiagram, treeProject, treeProject.getChildCount());

				}

			}

			//Object Diagrams				
			else if (diagrams.getName().equals("objectDiagrams")){
				Iterator objectDiagrams = diagrams.getChildren("objectDiagram").iterator();
				while(objectDiagrams.hasNext()){
					Element objectDiagram = (Element)objectDiagrams.next();
					ItTreeNode treeDiagram = new ItTreeNode(objectDiagram.getChildText("name"),
														objectDiagram, null, null);
					treeDiagram.setIcon(new ImageIcon("resources/images/objectDiagram.png"));
					treeModel.insertNodeInto(treeDiagram, treeProject, treeProject.getChildCount());
					
					Iterator objects = objectDiagram.getChild("objects").getChildren("object").iterator();
					while (objects.hasNext()){
						Element objectReference = (Element)objects.next();
						Element object = getElement(xmlProject, "objects", objectReference.getAttributeValue("id"));
						if(object != null){
							Element itsClass = getElement(xmlProject, "classes", object.getChildText("class"));							
							ItTreeNode treeObject = new ItTreeNode(object.getChildText("name"),
													object, objectReference, itsClass);
							treeObject.setIcon(new ImageIcon("resources/images/object.png"));
							treeDiagram.add(treeObject);														
						}						
					}					
				}				

			}
			// Planning domains
			else if (diagrams.getName().equals("planningDomains")){
				Iterator domains = diagrams.getChildren("domain").iterator();
				while(domains.hasNext()){
					Element domain = (Element)domains.next();
					buildDomainNode(domain, treeProject);
				}				

			}		
			
			//Planning Problems			
			/*else if (diagrams.getName().equals("planningProblems")){
				Iterator problems = diagrams.getChildren("problem").iterator();
				while(problems.hasNext()){
					Element problem = (Element)problems.next();				
					
					buildProblemNode(problem, treeProject);
				}				

			}*/			
			
		}
	}
	


	public void buildPDDLNode(Element xmlProject, ItTreeNode treeProject, String path){


                //domain files
		Iterator domainsIterator = xmlProject.getChild("pddldomains").getChildren().iterator();
		while(domainsIterator.hasNext()){
			Element pddldomain = (Element) domainsIterator.next();
                        ItTreeNode treePDDL = new ItTreeNode(pddldomain.getChildText("name"),pddldomain, null, null);
			treePDDL.setIcon(new ImageIcon("resources/images/domain.png"));
			treeModel.insertNodeInto(treePDDL, treeProject, treeProject.getChildCount());
			

		}

                //Problem Instance files
                Element problemInstancesRoot = xmlProject.getChild("problemInstances");
                ItTreeNode treeProblemInstPDDL = new ItTreeNode("Problem Instances", problemInstancesRoot, null, null);
		treeProblemInstPDDL.setIcon(new ImageIcon("resources/images/openreport.png"));
		treeModel.insertNodeInto(treeProblemInstPDDL, treeProject, treeProject.getChildCount());
                
                
                File theproject = new File(path);
                //File folder = new File(theproject.getParent());
                File folder = theproject.getParentFile();

                FilenameFilter filter = new FilenameFilter() {
                    public boolean accept(File dir, String name)
                    {
                            if (name != null &&
                                name.toLowerCase().endsWith(".pddl"))
                            {
                                return true;
                            }
                            else
                            {
                                return false;
                            }
                        }

                };

                //PDDLFileFilter ff = new PDDLFileFilter();
                File[] listOfFiles = folder.listFiles(filter);

                //sorting the files (alphabetical order)
                Arrays.sort(listOfFiles, new Comparator<File>() {
                     public int compare(File a, File b) {
                       return a.getName().compareTo(b.getName());
                     }
                   });
                

                for (int i = 0; i < listOfFiles.length; i++) {
                    File eachfile = listOfFiles[i];
                    //System.out.println("File " + eachfile.getName());
                    String filename = eachfile.getName();
                    
                    //check if file is a domain file                    
                    Element pddldomainNode = null;
                    try {
                            XPath dpath = new JDOMXPath("pddlproject/pddldomains/pddldomain[name='"+ filename +"']");
                            pddldomainNode = (Element)dpath.selectSingleNode(xmlProject.getDocument());

                    } catch (JaxenException e1) {
                            e1.printStackTrace();
                    }

                    if (pddldomainNode == null){
                        Element problemInstance = new Element("pddlproblem");
                        //problemInstance.setText(filename);
                        problemInstance.setAttribute("filename", filename);
                        problemInstance.setAttribute("file", eachfile.getAbsolutePath());
                        Element thename = new Element("name");
                        thename.setText(filename);
                        problemInstance.addContent(thename);
                        problemInstancesRoot.addContent(problemInstance);

                        ItTreeNode nodeProblemInstPDDL = new ItTreeNode(filename,problemInstance, null, null);
                        nodeProblemInstPDDL.setIcon(new ImageIcon("resources/images/problem.png"));
                        treeModel.insertNodeInto(nodeProblemInstPDDL, treeProblemInstPDDL, treeProblemInstPDDL.getChildCount());

                    }

                    
                }

                



                





	}



    /**
	 * Builds the tree node representing the project,
	 * which is structured in a XML type file
	 * 
	 * @param xmlProject an Element which contains the data of the project 
	 * @param path a String with the path of the file
	 * 	 		   where the project is saved
	 */
	public void buildStructure(Element xmlProject, String path){
		if (xmlProject != null){



			ItTreeNode root = (ItTreeNode)treeModel.getRoot();
			
			/*
			 * The project node is created with an
			 * id higher than the current highest id
			 */
			int currentIdValue, maxIdValue=0;
			for(int i=0; i<root.getChildCount(); i++){
				ItTreeNode current = (ItTreeNode)root.getChildAt(i);
				currentIdValue = Integer.parseInt(current.getReference().getAttributeValue("id"));
				if (currentIdValue > maxIdValue)
					maxIdValue = currentIdValue;				
			}			
			String idValue = String.valueOf(maxIdValue+1);			
			Element projectId = new Element("id");
			projectId.setText(idValue);
			xmlProject.getChild("generalInformation").addContent(projectId);			
			
			Element projectHeader = new Element("projectHeader");
			Attribute headerId = new Attribute("id", idValue);			
			Element filePath = new Element("filePath");
			
			if (path !=null){
				filePath.setText(path);
			}
			//If it is null then it is a new project (*itSIMPLE* is a internal control) 
			else{
				filePath.setText("*itSIMPLE*"+idValue);
				xmlProject.getChild("name").setText(xmlProject.getChildText("name")+" "+idValue);
			}				
			
			projectHeader.setAttribute(headerId);
			projectHeader.addContent(filePath);
			
			ItTreeNode treeProject = new ItTreeNode(xmlProject.getChildText("name"), xmlProject, projectHeader, null);
			//treeProject.setIcon(new ImageIcon("resources/images/project.png"));
			treeModel.insertNodeInto(treeProject, root, root.getChildCount());			

                        if (xmlProject.getName().equals("project")){
                            treeProject.setIcon(new ImageIcon("resources/images/project.png"));
                            buildDiagramNode(xmlProject, treeProject);
                        }
                        else if (xmlProject.getName().equals("pddlproject")){
                            //System.out.println(path);
                            treeProject.setIcon(new ImageIcon("resources/images/virtualprototype.png"));
                            //System.out.println("PDDL project open");
                            buildPDDLNode(xmlProject, treeProject, path);
                        }

			
				
		}
	}
	
	public ItTreeNode buildDomainNode(Element domain, ItTreeNode treeProject){
		ItTreeNode treeDomain = new ItTreeNode(domain.getChildText("name"),
				domain, null, null);
		treeDomain.setIcon(new ImageIcon("resources/images/planningProblem.png"));
		treeModel.insertNodeInto(treeDomain, treeProject, treeProject.getChildCount());
		
		//1. Build the Repository Diagram under the Domain Node
		Iterator repositoryDiagrams = domain.getChild("repositoryDiagrams").getChildren("repositoryDiagram").iterator();
		while(repositoryDiagrams.hasNext()){	
			Element repositoryDiagram = (Element)repositoryDiagrams.next();
			ItTreeNode treeDiagram = new ItTreeNode(repositoryDiagram.getChildText("name"),repositoryDiagram, null, null);
			treeDiagram.setIcon(new ImageIcon("resources/images/objectDiagram.png"));
			treeDomain.add(treeDiagram);
			
			Iterator objects = repositoryDiagram.getChild("objects").getChildren("object").iterator();
			while (objects.hasNext()){
				Element objectReference = (Element)objects.next();
				Element object = getElement(domain, "objects", objectReference.getAttributeValue("id"));
				if(object != null){
					Element itsClass = null;
					try {
						XPath path = new JDOMXPath("project/elements/classes/class[@id="+object.getChildText("class")+"]");
						itsClass = (Element)path.selectSingleNode(domain.getDocument());
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					//Element itsClass = getElement(xmlProject, "classes", object.getChildText("class"));							
					ItTreeNode treeObject = new ItTreeNode(object.getChildText("name"),
									object, objectReference, itsClass);
					treeObject.setIcon(new ImageIcon("resources/images/object.png"));
					treeDiagram.add(treeObject);														
				}						
			}					
		}
		
		// 2. Build the problems
		Iterator problems = domain.getChild("planningProblems").getChildren("problem").iterator();
		while(problems.hasNext()){
			Element problem = (Element)problems.next();				
			
			buildProblemNode(problem, treeDomain);
		}		
		
		return treeDomain;
		
	}
	
	public ItTreeNode buildProblemNode(Element problem, ItTreeNode treeProject){
		ItTreeNode treeProblem = new ItTreeNode(problem.getChildText("name"),
				problem, null, null);
		treeProblem.setIcon(new ImageIcon("resources/images/planningProblem.png"));
		treeModel.insertNodeInto(treeProblem, treeProject, treeProject.getChildCount());
		
		//Build each Object Diagram under the Problem Node
		Iterator objectDiagrams = problem.getChild("objectDiagrams").getChildren("objectDiagram").iterator();
		while(objectDiagrams.hasNext()){	
			Element objectDiagram = (Element)objectDiagrams.next();
			ItTreeNode treeDiagram = new ItTreeNode(objectDiagram.getChildText("name") + " - " + 
					objectDiagram.getChildText("sequenceReference"),objectDiagram, null, null);
			treeDiagram.setIcon(new ImageIcon("resources/images/objectDiagram.png"));
			treeProblem.add(treeDiagram);
			
			Iterator objects = objectDiagram.getChild("objects").getChildren("object").iterator();
			while (objects.hasNext()){
				Element objectReference = (Element)objects.next();
				Element object = getElement(problem.getParentElement().getParentElement(), "objects", objectReference.getAttributeValue("id"));
				if(object != null){
					Element itsClass = null;
					try {
						XPath path = new JDOMXPath("project/elements/classes/class[@id="+object.getChildText("class")+"]");
						itsClass = (Element)path.selectSingleNode(problem.getDocument());
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					//Element itsClass = getElement(xmlProject, "classes", object.getChildText("class"));							
					ItTreeNode treeObject = new ItTreeNode(object.getChildText("name"),
									object, objectReference, itsClass);
					treeObject.setIcon(new ImageIcon("resources/images/object.png"));
					treeDiagram.add(treeObject);														
				}						
			}					
		}
		
		return treeProblem;
		
	}
	
	public Element getElement(Element parent, String group, String id){
		Element element = null;
		Iterator elements = parent.getChild("elements").getChild(group).getChildren().iterator();
		while(elements.hasNext()){
			Element currentElement = (Element)elements.next();
			if (currentElement.getAttributeValue("id").equals(id)){
				element = currentElement;
				break;
			}	
		}
		
		return element;		
	}
	
	public ItTreeNode findNodeFromData(ItTreeNode node, Element data){
		
		if (node == null){return null;}
		if (node.getData() == data){
			return node;
		}
		else{
			ItTreeNode element = null;
			ItTreeNode result = null;
			for(int i = 0; i < node.getChildCount(); i++){
				element = (ItTreeNode)node.getChildAt(i);
				result = findNodeFromData(element, data);
				if (result != null){
					return result;
				}
			}
			return null;
		}
	}
	
	public ItTreeNode findNodeFromData(Element project, Element data){
		ItTreeNode node = findProjectNode(project);
		ItTreeNode result = null;
		if(node != null){
			result = findNodeFromData(node, data);
		}
		
		return result;
	}
	
	public ItTreeNode findNodeFromReference(ItTreeNode node, Element reference){
		
		if (node == null){return null;}
		if (node.getReference() == reference){
			return node;
		}
		else{			
			ItTreeNode result = null;
			for(int i = 0; i < node.getChildCount(); i++){
				ItTreeNode element = (ItTreeNode)node.getChildAt(i);
				result = findNodeFromReference(element, reference);
				if (result != null){
					return result;
				}
			}
			return null;
		}
	}
	
	public ItTreeNode findProjectNode(Element project){		
		ItTreeNode root = (ItTreeNode)getModel().getRoot();
		for(int i = 0; i < root.getChildCount(); i++){
			ItTreeNode projectNode = (ItTreeNode)root.getChildAt(i);
			if(projectNode.getData() == project){
				return projectNode;				
			}
		}		
		
		return null;		
	}
	
	// returns the project based on the project header id
	public Element getProject(String id){
		Element project = null;
		
		TreeNode root = (TreeNode)treeModel.getRoot();
		for(int i = 0; i < root.getChildCount(); i++){
			ItTreeNode child = (ItTreeNode)root.getChildAt(i);
			Element projectHeader = child.getReference();
			if(id.equals(projectHeader.getAttributeValue("id"))){
				project = child.getData();
				break;
			}
		}
		return project;
	}
	
	public void deleteTreeNodeFromData(ItTreeNode project, Element data){
		ItTreeNode node = findNodeFromData(project, data);
		if(node != null){			
			treeModel.removeNodeFromParent(node);
		}
	}
	
	public void deleteTreeNodeFromReference(ItTreeNode project, Element reference){
		ItTreeNode node = findNodeFromReference(project, reference);
		if(node != null){			
			treeModel.removeNodeFromParent(node);
		}
	}

	public void rebuildProjectNode(Element projectxml, ItTreeNode projectNode){
        //clear and rebuild tree
            while (projectNode.getChildCount() > 0) {
                ItTreeNode object = (ItTreeNode)this.getModel().getChild(projectNode, 0);
                treeModel.removeNodeFromParent(object);                
            }
            this.buildDiagramNode(projectxml, projectNode);
	}        
        
        
	
}
