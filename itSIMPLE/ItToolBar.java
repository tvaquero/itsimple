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

package itSIMPLE;

import languages.xml.XMLUtilities;
import util.filefilter.PDDLFileFilter;
import util.filefilter.PNMLFileFilter;
import itGraph.ItGraph;

import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextPane;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;

import languages.petrinets.PetriNetAnalysis;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

public class ItToolBar extends JToolBar {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4422915166797883770L;
	
	public static final int SELECT = 0;
	public static final int ACTOR = 1;
	public static final int USE_CASE = 2;
	public static final int CLASS = 3;
	public static final int ENUMERATION = 15;
	public static final int OBJECT = 4;
	public static final int STATE = 5;
	public static final int MULTIPLE_OBJECTS = 14;
	public static final int INITIAL_STATE = 12;
	public static final int FINAL_STATE = 13;
	public static final int USE_CASE_ASSOCIATION = 6;
	public static final int CLASS_ASSOCIATION = 7;
	public static final int ACTION_ASSOCIATION = 8;
	public static final int GENERALIZATION = 9;
	public static final int OBJECT_ASSOCIATION = 10;
	public static final int DEPENDENCY = 11;
	
	public int selectedButton = SELECT;		
	private ItGraph graph = null;	
	private JTextPane textPane = null;
		
	//UML Buttons
	private ButtonGroup group = new ButtonGroup();
	private JToggleButton selectButton = null;	
	private JToggleButton actorButton = null;	
	private JToggleButton useCaseButton = null;	
	private JToggleButton useCaseAssociationButton = null;	
	private JToggleButton classButton = null;
	private JToggleButton enumerationButton = null;
	private JToggleButton classAssociationButton = null;
	private JToggleButton actionAssociationButton = null;
	private JToggleButton generalizationButton = null;
	private JToggleButton objectButton = null;
	private JToggleButton multipleObjectsButton = null;
	private JToggleButton stateButton = null;	
	private JToggleButton initialStateButton = null;	
	private JToggleButton finalStateButton = null;
	private JToggleButton objectAssociationButton = null;
	private JToggleButton dependencyButton = null;	
	private JButton zoomInButton = null;
	private JButton zoomOutButton = null;	
	private JButton oneToOneScaleButton = null;	
	private JButton summaryButton = null;
	private JButton closeEditButton = null;
	
	//PDDL Buttons
	
	private JButton saveToPDDLFileButton = null;
	double currentDelay = 1000.0;
	private static double MAX_DELAY = 2000.0;
	
	//Petri Nets 
	
	private PetriNetRunThread thread = null;
	JSlider simulationDelay = null;
	private String firedTransitions = "";
	
	//Buttons
	
	private JButton saveToPNMLFileButton = null;
	private JButton runPetriNetButton = null;
	private JButton stepPetriNetButton = null;
	private JButton pausePetriNetButton = null;
	private JButton stopPetriNetButton = null;
	
	public ItToolBar(String diagramType, String language) {
		super();
		//initialize();
		setRollover(true);
		if (language.equals("UML")){
			add(getSelectButton());		
			if (diagramType.equals("classDiagram")){
				add(getClassButton());
				add(getEnumerationButton());
				add(getClassAssociationButton());
				add(getGeneralizationButton());
				
			}
			else if(diagramType.equals("useCaseDiagram")){
				add(getActorButton());
				add(getUseCaseButton());
				add(getUseCaseAssociationButton());
				add(getDependencyButton());

			}
			else if(diagramType.equals("stateMachineDiagram")){
				add(getInitialStateButton());
				add(getStateButton());				
				add(getFinalStateButton());
				add(getActionAssociationButton());
			}
			else if(diagramType.equals("objectDiagram")){				
				add(getObjectAssociationButton());
			}
			else if(diagramType.equals("repositoryDiagram")){
				add(getObjectButton());
				add(getMultipleObjectsButton());
				add(getObjectAssociationButton());
			}		
			
			addSeparator();
			add(getZoomInButton());
			add(getZoomOutButton());
			add(getOneToOneScaleButton());
			
			if(diagramType.equals("useCaseDiagram")){
				addSeparator();
				add(getSummaryButton());
			}
			
		}
		else if (language.equals("PDDL")){
			add(getSaveToPDDLFileButton());
		}
		else if (language.equals("PetriNet")){
		// TODO	change language to PNML instead of PetriNet
			add(getSaveToPNMLFileButton());
			addSeparator();
			add(new JLabel("Slow"));
			add(getSimulationDelaySlider());
			add(new JLabel("Fast"));
			add(getRunPetriNetButton());
			add(getPausePetriNetButton());
			add(getStopPetriNetButton());
			add(getStepPetriNetButton());
			addSeparator();
			add(getZoomInButton());
			add(getZoomOutButton());
			add(getOneToOneScaleButton());
			
			
		}
	}
	

	/**
	 * This method initializes this
	 * 
	 *
	private void initialize() {
			
	}*/

	/**
	 * This method initializes selectButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getSelectButton() {
		if (selectButton == null) {
			selectButton = new JToggleButton("Select",new ImageIcon("resources/images/select.png"), true);
			selectButton.setToolTipText("Select");
			group.add(selectButton);
			selectButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(SELECT);
				}
			});
			
		}
		return selectButton;
	}
	
	/**
	 * This method initializes actorButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getActorButton() {
		if (actorButton == null) {
			actorButton = new JToggleButton("Actor",new ImageIcon("resources/images/actor.png"), false);
			actorButton.setToolTipText("Actor");
			group.add(actorButton);
			actorButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(ACTOR);
				}
			});			
		}
		return actorButton;
	}	

	/**
	 * This method initializes useCaseButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getUseCaseButton() {
		if (useCaseButton == null) {
			useCaseButton = new JToggleButton("UseCase",new ImageIcon("resources/images/useCase.png"));
			useCaseButton.setToolTipText("UseCase");
			group.add(useCaseButton);
			useCaseButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(USE_CASE);
				}
			});
		}
		return useCaseButton;
	}
	
	/**
	 * This method initializes classButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getClassButton() {
		if (classButton == null) {
			classButton = new JToggleButton("Class",new ImageIcon("resources/images/class.png"));
			classButton.setToolTipText("Class");
			group.add(classButton);
			classButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(CLASS);
				}
			});
		}
		return classButton;
	}
	
	
	/**
	 * This method initializes enumerationButton	
	 * 	
	 * @return javax.swing.JToogleButton	
	 */
	private JToggleButton getEnumerationButton() {
		if (enumerationButton == null) {
			enumerationButton = new JToggleButton("Enumeration",new ImageIcon("resources/images/class.png"));
			enumerationButton.setToolTipText("Enumeration");
			group.add(enumerationButton);
			enumerationButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(ENUMERATION);
				}
			});
		}
		return enumerationButton;
	}
	
	/**
	 * This method initializes multipleObjectsButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getObjectButton() {
		if (objectButton == null) {
			objectButton = new JToggleButton("Object",new ImageIcon("resources/images/object.png"));
			objectButton.setToolTipText("Object");
			group.add(objectButton);
			objectButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(OBJECT);
				}
			});
		}
		return objectButton;
	}
	
	/**
	 * This method initializes objectButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getMultipleObjectsButton() {
		if (multipleObjectsButton == null) {
			multipleObjectsButton = new JToggleButton("Multi Objects",new ImageIcon("resources/images/multiobjects.png"));
			multipleObjectsButton.setToolTipText("Multiple Objects");
			group.add(multipleObjectsButton);
			multipleObjectsButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(MULTIPLE_OBJECTS);
				}
			});
		}
		return multipleObjectsButton;
	}
	
	/**
	 * This method initializes stateButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getStateButton() {
		if (stateButton == null) {
			stateButton = new JToggleButton("State",new ImageIcon("resources/images/state.png"));
			stateButton.setToolTipText("State");
			group.add(stateButton);
			stateButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(STATE);
				}
			});
		}
		return stateButton;
	}
	
	/**
	 * This method initializes initialStateButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getInitialStateButton() {
		if (initialStateButton == null) {
			initialStateButton = new JToggleButton("Initial State",new ImageIcon("resources/images/initialStateButton.png"));
			initialStateButton.setToolTipText("Initial State");
			group.add(initialStateButton);
			initialStateButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(INITIAL_STATE);
				}
			});
		}
		return initialStateButton;
	}
	
	/**
	 * This method initializes finalStateButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getFinalStateButton() {
		if (finalStateButton == null) {
			finalStateButton = new JToggleButton("Final State",new ImageIcon("resources/images/finalStateButton.png"));
			finalStateButton.setToolTipText("Final State");
			group.add(finalStateButton);
			finalStateButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(FINAL_STATE);
				}
			});
		}
		return finalStateButton;
	}

	/**
	 * This method initializes useCaseAssociationButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getUseCaseAssociationButton() {
		if (useCaseAssociationButton == null) {
			useCaseAssociationButton = new JToggleButton("UseCase Association",new ImageIcon("resources/images/association.png"));
			useCaseAssociationButton.setToolTipText("UseCase Association");
			group.add(useCaseAssociationButton);
			useCaseAssociationButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(USE_CASE_ASSOCIATION);
				}
			});
		}
		return useCaseAssociationButton;
	}	

	/**
	 * This method initializes classAssociationButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getClassAssociationButton() {
		if (classAssociationButton == null) {
			classAssociationButton = new JToggleButton("Class Association",new ImageIcon("resources/images/association.png"));
			classAssociationButton.setToolTipText("Class Association");
			group.add(classAssociationButton);
			classAssociationButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(CLASS_ASSOCIATION);
				}
			});
		}
		return classAssociationButton;
	}

	/**
	 * This method initializes actionAssociationButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getActionAssociationButton() {
		if (actionAssociationButton == null) {
			actionAssociationButton = new JToggleButton("Action Association",new ImageIcon("resources/images/association.png"));
			actionAssociationButton.setToolTipText("Action Association");
			group.add(actionAssociationButton);
			actionAssociationButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(ACTION_ASSOCIATION);
				}
			});
		}
		return actionAssociationButton;
	}

	/**
	 * This method initializes generalizationButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getGeneralizationButton() {
		if (generalizationButton == null) {
			generalizationButton = new JToggleButton("Generalization",new ImageIcon("resources/images/generalization.png"));
			generalizationButton.setToolTipText("Generalization");
			group.add(generalizationButton);
			generalizationButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(GENERALIZATION);
				}
			});
		}
		return generalizationButton;
	}
		
	/**
	 * This method initializes objectAssociationButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getObjectAssociationButton() {
		if (objectAssociationButton == null) {
			objectAssociationButton = new JToggleButton("Object Association",new ImageIcon("resources/images/association.png"));
			objectAssociationButton.setToolTipText("Object Association");
			group.add(objectAssociationButton);
			objectAssociationButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(OBJECT_ASSOCIATION);
				}
			});
		}
		return objectAssociationButton;
	}
	
	/**
	 * This method initializes dependencyButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JToggleButton getDependencyButton() {
		if (dependencyButton == null) {
			dependencyButton = new JToggleButton("Dependency",new ImageIcon("resources/images/dependency.png"));
			dependencyButton.setToolTipText("Dependency");
			group.add(dependencyButton);
			dependencyButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					setSelectedButton(DEPENDENCY);
				}
			});
		}
		return dependencyButton;
	}
	
	/**
	 * This method initializes zoomInButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getZoomInButton() {
		if (zoomInButton == null) {
			zoomInButton = new JButton("Zoom In",new ImageIcon("resources/images/zoomIN.png"));
			zoomInButton.setToolTipText("Zoom In");
			zoomInButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					graph.setScale(graph.getScale()*1.25);
				}
			});
		}
		return zoomInButton;
	}

	/**
	 * This method initializes zoomOutButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getZoomOutButton() {
		if (zoomOutButton == null) {
			zoomOutButton = new JButton("Zoom Out",new ImageIcon("resources/images/zoomOUT.png"));
			zoomOutButton.setToolTipText("Zoom Out");
			zoomOutButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					graph.setScale(graph.getScale()*0.80);
				}
			});
		}
		return zoomOutButton;
	}
	
	/**
	 * This method initializes oneToOneScaleButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getOneToOneScaleButton() {
		if (oneToOneScaleButton == null) {
			oneToOneScaleButton = new JButton("1:1");
			oneToOneScaleButton.setToolTipText("1:1");
			oneToOneScaleButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					graph.setScale(1.0);
				}
			});
		}
		return oneToOneScaleButton;
	}
	
	/**
	 * This method initializes closeEditButton	
	 * 	
	 * @return javax.swing.JButton	
	 */
	private JButton getCloseEditButton() {
		if (closeEditButton == null) {
			closeEditButton = new JButton("Close HERE!");
			closeEditButton.setToolTipText("Close editing and back to simulation.");
			closeEditButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					// get the edited diagram
					Element diagram = graph.getDiagram();
					Element diagramParent = diagram.getParentElement();
					diagramParent.removeContent(diagram);
					
					// send the edited diagram to the movie
					PlanNavigationList.getInstance().replaceEditedDiagram(diagram);
					ItSIMPLE.getInstance().setMainTabbedPaneSelectedIndex(3);
					
					//close the tab
					ItSIMPLE.getInstance().closeEditStateTab();
				}
			});
		}
		return closeEditButton;
	}
	
	private JButton getSummaryButton() {
		if (summaryButton == null) {
			summaryButton = new JButton("Summary");
			summaryButton.setToolTipText("Summary");
			summaryButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					
					//Format a full visualization of the Use Case Diagram (Actors and Use Cases);
					
					Element diagram = graph.getDiagram();
					String summary = "";
					
					//1. Find all actors
					
					List<?> actors = null;
					try {
						XPath path = new JDOMXPath("actors/actor");
						actors = path.selectNodes(diagram);
					} catch (JaxenException exc) {			
						exc.printStackTrace();
					}
					
					if (actors != null){
						for (Iterator<?> iter1 = actors.iterator(); iter1.hasNext();) {
							//3.1 Get association ends 
							Element actor = (Element) iter1.next();
							summary = summary + "<BR>" + graph.getActorDefinition(actor);
						}
					}
					
					
					
					//2. Find all use cases
					List<?> usecases = null;
					try {
						XPath path = new JDOMXPath("useCases/useCase");
						usecases = path.selectNodes(diagram);
					} catch (JaxenException exc) {			
						exc.printStackTrace();
					}						

					if (actors != null){
						for (Iterator<?> iter1 = usecases.iterator(); iter1.hasNext();) {
							//3.1 Get association ends 
							Element usecase = (Element) iter1.next();
							summary = summary + "<BR>" + graph.getUseCaseDefinition(usecase);
						}
					}
					
					if (summary.trim().equals("")){
						summary = "No elements in this diagrams.";
					}
					
					ItSIMPLE.getInstance().getInfoEditorPane().setText(summary);	
				  }
			});
		}
		return summaryButton;
	}
	
	public JButton getSaveToPDDLFileButton() {
		if (saveToPDDLFileButton == null) {
			saveToPDDLFileButton = new JButton("Save to PDDL", new ImageIcon("resources/images/savePDDL.png"));
			saveToPDDLFileButton.setToolTipText("Save to PDDL");
			saveToPDDLFileButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					fc.setDialogTitle("Save to PDDL");
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);					
					fc.setFileFilter(new PDDLFileFilter());
					
					
					int returnVal = fc.showSaveDialog(ItToolBar.this);
					if (returnVal == JFileChooser.APPROVE_OPTION){
						File selectedFile = fc.getSelectedFile();
						String path = selectedFile.getPath();
						if (!path.toLowerCase().endsWith(".pddl")){
							path += ".pddl";
						}
						try {
							FileWriter file = new FileWriter(path);
							file.write(textPane.getText());
							file.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				 
				}
			});
		}
		return saveToPDDLFileButton;
	}

	
	public JButton getSaveToPNMLFileButton() {
		if (saveToPNMLFileButton == null) {
			saveToPNMLFileButton = new JButton("Save to PNML", new ImageIcon("resources/images/savePNML.png"));
			saveToPNMLFileButton.setToolTipText("Save to PNML");
			saveToPNMLFileButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					JFileChooser fc = new JFileChooser();
					fc.setDialogTitle("Save to PNML");
					fc.setFileSelectionMode(JFileChooser.FILES_ONLY);					
					fc.setFileFilter(new PNMLFileFilter());
					
					
					int returnVal = fc.showSaveDialog(ItToolBar.this);
					if (returnVal == JFileChooser.APPROVE_OPTION){
						File selectedFile = fc.getSelectedFile();
						String path = selectedFile.getPath();
						if (!path.toLowerCase().endsWith(".pnml")){
							path += ".pnml";
						}
						
						Element pnml = graph.getDiagram();
						XMLUtilities.writeToFile(path, new Document(pnml));
						
						/*try {
							FileWriter file = new FileWriter(path);
							file.write(textPane.getText());
							file.close();
						} catch (IOException e1) {
							e1.printStackTrace();
						}*/
					}
				 
				}
			});
		}
		return saveToPNMLFileButton;
	}
	private JButton getStepPetriNetButton() {
		if (stepPetriNetButton == null) {
			stepPetriNetButton = new JButton(new ImageIcon("resources/images/step.png"));
			stepPetriNetButton.setToolTipText("Step");
			stepPetriNetButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					Element currentPNML = graph.getDiagram();
					PetriNetAnalysis.runPetriNet(currentPNML);
					//graph.buildDiagram();
					graph.repaintAllElements();
					if(PetriNetAnalysis.firedTransitions.trim().equals("")){
						firedTransitions += "<font size=3 face=arial color='red'><b>No enabled transitions found</b></font><br>";
					}
					firedTransitions += "<font size=3 face=arial color='black'>" + PetriNetAnalysis.firedTransitions + "</font>";
					graph.getInfoPane().setText(firedTransitions);
					stopPetriNetButton.setEnabled(true);
				  }
			});
		}
		return stepPetriNetButton;
	}
	private JButton getRunPetriNetButton() {
		if (runPetriNetButton == null) {
			runPetriNetButton = new JButton(new ImageIcon("resources/images/play.png"));
			runPetriNetButton.setToolTipText("Run");
			runPetriNetButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					thread = new PetriNetRunThread();
					thread.setPause(false);
					thread.start();
					runPetriNetButton.setEnabled(false);
					stepPetriNetButton.setEnabled(false);
					stopPetriNetButton.setEnabled(true);
					pausePetriNetButton.setEnabled(true);
				  }
			});
		}
		return runPetriNetButton;
	}
	
	private JButton getPausePetriNetButton() {
		if (pausePetriNetButton == null) {
			pausePetriNetButton = new JButton(new ImageIcon("resources/images/pause.png"));
			pausePetriNetButton.setToolTipText("Pause");
			pausePetriNetButton.setEnabled(false);
			pausePetriNetButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					thread.setPause(true);
					runPetriNetButton.setEnabled(true);
					stepPetriNetButton.setEnabled(true);
					pausePetriNetButton.setEnabled(false);
				  }
			});
		}
		return pausePetriNetButton;
	}
	
	private JButton getStopPetriNetButton() {
		if (stopPetriNetButton == null) {
			stopPetriNetButton = new JButton(new ImageIcon("resources/images/stop.png"));
			stopPetriNetButton.setToolTipText("Stop");
			stopPetriNetButton.setEnabled(false);
			stopPetriNetButton.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					if(thread != null)
						thread.setPause(true);
					runPetriNetButton.setEnabled(true);
					stepPetriNetButton.setEnabled(true);
					firedTransitions = "";
					graph.getInfoPane().setText(firedTransitions);
					graph.setDiagram(graph.getAdditional());
					graph.buildDiagram();
					stopPetriNetButton.setEnabled(false);
					pausePetriNetButton.setEnabled(false);
				  }
			});
		}
		return stopPetriNetButton;
	}
	
	private JSlider getSimulationDelaySlider() {
		if (simulationDelay == null) {
			int minDelay = 0;
			int maxDelay = 100;
			int initialDelay = (int) Math.round(100-100*currentDelay/MAX_DELAY);
			simulationDelay = new JSlider(JSlider.HORIZONTAL, minDelay, maxDelay, initialDelay);
			
			//set labels
			
			/*//Create the label table
			Hashtable labelTable = new Hashtable();
			labelTable.put( new Integer( 0 ), new JLabel("Slow") );
			labelTable.put( new Integer( maxDelay ), new JLabel("Fast") );
			simulationDelay.setLabelTable( labelTable );
			//paint lables
			simulationDelay.setPaintLabels(true);*/

			simulationDelay.setMaximumSize(new java.awt.Dimension(150,150));
			
			simulationDelay.setToolTipText("Simulation speed");
			simulationDelay.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					JSlider source = (JSlider)e.getSource();
				    if (!source.getValueIsAdjusting()) {
				        int fps = (int)source.getValue();
				        if (fps == 0) {
				        	if(thread != null)
				        		thread.setSuspend(true);
				        } else {
				        	if(thread != null)
				        		thread.setSuspend(false);
				            currentDelay = MAX_DELAY*(1-fps/100.0);
				            //System.out.println(currentDelay);
				        }
				    }

				  }
			});
		}
		return simulationDelay;
	}
	
	
	public void setSelectedButton(int button){
		selectedButton = button;
		if(button == SELECT){
			group.setSelected(selectButton.getModel(), true);
		}
	}
	
	public int getSelectedButton(){
		return selectedButton;
	}
	
	public void addCloseEditButton(){
		addSeparator();
		add(getCloseEditButton());
	}


	/**
	 * @param graph The graph to set.
	 */
	public void setGraph(ItGraph graph) {
		this.graph = graph;
	}

	/**
	 * @param textPane The textPane to set.
	 */
	public void setTextPane(JTextPane textPane) {
		this.textPane = textPane;
	}

	private class PetriNetRunThread extends Thread {
		private boolean pause;
		private boolean suspend;
		PetriNetRunThread(){
			super();
			pause = true;
			suspend = false;
			this.setPriority(Thread.MIN_PRIORITY);
		}
		public void run(){
			while(!pause){
				if(!suspend){
					Element currentPNML = graph.getDiagram();
					PetriNetAnalysis.runPetriNet(currentPNML);
					graph.repaintAllElements();
					if(PetriNetAnalysis.firedTransitions.trim().equals("")){
						firedTransitions += "<font size=3 face=arial color='red'><b>No enabled transitions found</b></font><br>";
						pause = true;
						pausePetriNetButton.setEnabled(false);
						runPetriNetButton.setEnabled(false);
						stepPetriNetButton.setEnabled(false);
						stopPetriNetButton.setEnabled(true);
						
					}
					firedTransitions += "<font size=3 face=arial color='black'>" + PetriNetAnalysis.firedTransitions + "</font>";
					graph.getInfoPane().setText(firedTransitions);
				}
				try {
					Thread.sleep(Math.round(currentDelay));
				} catch (InterruptedException e) {					
					e.printStackTrace();
				}
			}
		}
		public void setPause(boolean pause) {
			this.pause = pause;
		}
		public void setSuspend(boolean suspend) {
			this.suspend = suspend;
		}
	}

}



