/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2012 University of Sao Paulo, University of Toronto
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
 *              Rosimarci Tonaco
**/

package src.gui;

import java.awt.Dialog;
import java.awt.event.*;
import src.itgraph.uml.*;
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
//import javax.swing.text.Element;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;


import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractButton;
import javax.swing.JComponent;
//import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
//import javax.swing.text.StyledDocument;
import javax.swing.text.JTextComponent;
import javax.swing.text.BadLocationException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import src.itgraph.BasicCell;
import src.itgraph.ItGraph;
import src.languages.petrinets.PetriNetAnalysis;
import src.languages.xml.XMLUtilities;
import src.sourceeditor.ItHilightedDocument;
import src.util.filefilter.PDDLFileFilter;
import src.util.filefilter.PNMLFileFilter;

import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoManager;



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
        //domain
        private JButton pddlTypeTemplateButton = null;
        private JButton pddlPredicateTemplateButton = null;
        private JButton pddlFunctionTemplateButton = null;
        private JButton pddlDerivedPredicateTemplateButton = null;
        private JButton pddlConstraintTemplateButton = null;
        //problem
        private JButton pddlObjectTemplateButton = null;
        private JButton pddlMetricTemplateButton = null;
        private JButton pddlProblemConstraintTemplateButton = null;
        //gerenal
        private JButton pddlCommentButton = null;
        private JButton pddlCommentLineButton = null;
        private JButton pddlUndoButton = null;
        private JButton pddlRedoButton = null;
        
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
        
        //PDDL Editor buttons
        private static String action = "(:action action_name_here\n"+
                    "  :parameters (  )\n"+
                    "  :precondition (  )\n"+
                    "  :effect (  )\n"+
                    ")";

        private static String durativeAction = "(:durative-action durative_action_name_here\n"+
                    " :parameters (  )\n"+
                    " :condition (  )\n"+
                    " :effect (  )\n"+
                    ")";

        private static String function = "(:functions \n"+
                                              " (func1)\n"+
                                              " (func2)\n"+
                                              " (func3)\n" +
                                              ")";

        private static String predicates = "(:predicates \n"+
                                              " (pred1)\n"+
                                              " (pred2)\n"+
                                              " (pred3)\n" +
                                              ")";

        private static String derivedPredicates = "(:derived (pred1)\n"+
                                                     " (derived_pred)\n" +
                                                     ")";                
        private static String type = "(:types \n"+
                                         "obj1 obj2 obj3 - Type)\n";

        private static String constraints = "(:constraints  \n"+
                                            ")\n";

        private static String comment = ";; Type your comment here.\n";

        private static String strips = ":strips\n";

        private static String typing = ":typing\n";

        private static String negativePreconditions = ":negative-preconditions\n";

        private static String disjunctivePreconditions = ":disjunctive-preconditions\n";

        private static String equality = ":equality\n";

        private static String existentialPreconditions = ":existential-preconditions\n";

        private static String universalPreconditions = ":universal-preconditions\n";

        private static String quantifiedPreconditions = ":quantified-preconditions\n";

        private static String conditionalEffects = ":conditional-effects\n";

        private static String fluent = ":fluent\n";

        private static String adl = ":adl\n";

        private static String durativeActionsInRequirements = ":durative-actions\n";

        private static String derivedPredicatesInRequirements = ":derived-predicates\n";

        private static String timedInitialLiterals = ":timed-initial-literals\n";

        private static String preferences = ":preferences\n";

        private static String constraintsInRequirements = ":constraints\n";

        private static String object = "(:objects obj1 obj2 obj3 - Type)\n";

        private static String metric = "(:metric  )\n";

        private static String commentLine = ";;";

        protected UndoManager undo = new UndoManager();
        
        //
        public static AssociateNeighbour3 associateFrame;
        public static ObjectGridWizard objectGridFrame;
        //
        
        private javax.swing.JButton redoButton;
        private javax.swing.JButton undoButton;
        //
        
        public static JMenuItem neighborsItem;
        
    private void insertText(String text)
    {
        ItHilightedDocument doc = (ItHilightedDocument) textPane.getStyledDocument();
        int offset = textPane.getCaretPosition();
        try
        {
            doc.insertString(offset, text, null);
        }
        catch (BadLocationException ex)
        {
            Logger.getLogger(ItToolBar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void insertText(int offset, String text)
    {
        ItHilightedDocument doc = (ItHilightedDocument) textPane.getStyledDocument();
        //int offset = textPane.getCaretPosition();
        try
        {
            doc.insertString(offset, text, null);
        }
        catch (BadLocationException ex)
        {
            Logger.getLogger(ItToolBar.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static int getLineAtCaret(JTextComponent component)
    {
            int caretPosition = component.getCaretPosition();
            return component.getDocument().getDefaultRootElement()
                    .getElementIndex( caretPosition ) + 1;
    }

    public static void gotoStartOfLine(JTextComponent component, int line)
    {
            //Element root = component.getDocument().getDefaultRootElement();
            line = Math.max(line, 1);
            line = Math.min(line, component.getDocument().getDefaultRootElement().getElementCount());
            component.setCaretPosition( component.getDocument().getDefaultRootElement().getElement( line - 1 ).getStartOffset() );
    }
  
//    private class MyUndoableEditListener implements UndoableEditListener
//    {
//        public void undoableEditHappened(UndoableEditEvent e)
//        {
//            //Remember the edit and update the menus
//            undo.addEdit(e.getEdit());
//            updateUndoState();
//            updateRedoState();
//        }
//    }

    public void setUndoManager(UndoManager undo)
    {
        this.undo = undo;
    }

    private void updateUndoState()
    {
        if (undo.canUndo())
            pddlUndoButton.setEnabled(true);
        else
            pddlUndoButton.setEnabled(false);
    }

    private void updateRedoState()
    {
        if (undo.canRedo())
            pddlRedoButton.setEnabled(true);
        else
            pddlRedoButton.setEnabled(false);
    }
	
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
			else if(diagramType.equals("repositoryDiagram")){
				add(getObjectButton());
				add(getMultipleObjectsButton());
				add(getObjectAssociationButton());
                                add(getSnapshotWizards());
			}		
                        else if(diagramType.equals("objectDiagram")){				
				add(getObjectAssociationButton());
                                add(getSnapshotWizards());
                                
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
                    System.out.print("Ola: " + diagramType);                        
                        if(diagramType.equals("pddldomain")){
                            add(getpddlUndoButton());
                            add(getpddlRedoButton());
                            addSeparator();
                            
                            add(getpddlTypeTemplateButton());
                            add(getpddlPredicateTemplateButton());
                            add(getpddlFunctionTemplateButton());
                            add(getPDDLActionsTemplateList());
                            add(getpddlDerivedPredicateTemplateButton());                            
                            add(getpddlConstraintTemplateButton());                            
                            add(getListRequirementButtons());
                            
                            addSeparator();
                            add(getpddlCommentButton());
                            add(getpddlCommentLineButton());                            
                            
                        }else if(diagramType.equals("pddlproblem")){
                            //xxx
                            add(getpddlUndoButton());
                            add(getpddlRedoButton());
                            addSeparator();
                            add(getpddlObjectTemplateButton());
                            add(getpddlMetricTemplateButton());
                            add(getpddlProblemConstraintTemplateButton());
                            addSeparator();
                            add(getpddlCommentButton());
                            add(getpddlCommentLineButton());
                        }else{
                            add(getSaveToPDDLFileButton());
                        }
			
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
        
        
    //Snapthot Wizards
        private  AbstractButton getSnapshotWizards() {
            final JToggleButton moreButton = new JToggleButton("Wizards");
            moreButton.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        createAndShowWizardMenu((JComponent) e.getSource(), moreButton);
                    }
                    Object[] cells = graph.getSelectionCells();		
                    if(cells.length > 0)
                        neighborsItem.setEnabled(true);
                    else
                        neighborsItem.setEnabled(false);
}
            });
            moreButton.setFocusable(false);
            moreButton.setHorizontalTextPosition(SwingConstants.LEADING);
            return moreButton;
        }
        
       
       
        private void createAndShowWizardMenu(final JComponent component, final AbstractButton moreButton) {
            JPopupMenu menu = new JPopupMenu();

            neighborsItem = new JMenuItem("Associate neighbours");
            neighborsItem.setToolTipText("Select one or more objects");
           
            //stripsitem.setActionCommand();
            neighborsItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: Associate Neighbors
   
                    Element diagram = null;
                    Object object = null;
                    
                    Object[] cells = graph.getSelectionCells();
                    /*if(cells.length == 1)
                        object = cells[0];*/
                    if(cells.length > 0){
                            
                            if(ItSIMPLE.getTypeDiagram() == 0){
                                diagram = ItSIMPLE.getDiagramRespository();                        
                            }//Diagram Repository
                            else if(ItSIMPLE.getTypeDiagram() == 1){ //Object Diagram
                                diagram = ItSIMPLE.getObjectDiagram();                               
                                }
                            
                        //if(associateFrame == null){
                             associateFrame = new AssociateNeighbour3(diagram, cells);
                        // }
                       // else{
                             //associateFrame.setObjectDiagram(diagram);
                            // associateFrame.setObject(object);
                            // associateFrame.ini();
                        //    }
                        associateFrame.setModal(true);
                        associateFrame.setTitle("Associate neighbours");
                        associateFrame.pack();
                        associateFrame.setVisible(true);
                        associateFrame.setShowForm(true);
   
                            
                            
                            /*
				List<Object> deletingCells = new ArrayList<Object>();
				// 1. Delete all associations
				BasicAssociation selectedAssociation = null;
				
				// this will mark if there is an object cell to be deleted
				// so after it's deleted, the repository list is updated
				boolean object = false;
				
				for(int i = 0; i < cells.length; i++){
					Object cell = cells[i];			
					if (cell instanceof ActorCell ||
							cell instanceof UseCaseCell ||							
							cell instanceof StateCell ||
							cell instanceof InitialStateCell ||
							cell instanceof FinalStateCell ||
							cell instanceof ObjectCell ||
							cell instanceof ClassCell ||
							cell instanceof EnumerationCell){
						// Add cells to a list
						deletingCells.add(cell);
						
						if(!object && cell instanceof ObjectCell){
							object = true;
						}
					}
					
					
					else if(cell instanceof UseCaseAssociation ||
					cell instanceof Dependency ||							
					cell instanceof ClassAssociation ||
					cell instanceof ObjectAssociation ||
					cell instanceof ActionAssociation ||
					cell instanceof Generalization){						
						selectedAssociation = (BasicAssociation)cell;						
						deleteElement(selectedAssociation.getData(), selectedAssociation.getReference(), null, false);
						selectedAssociation.setData(null);
					}					
				}
				// 2. Delete all cells
				for (Iterator<Object> iter = deletingCells.iterator(); iter.hasNext();) {
					BasicCell selectedCell = (BasicCell) iter.next();
					deleteElement(selectedCell.getData(), selectedCell.getReference(),
							selectedCell.getAdditionalData(), false);
					selectedCell.setData(null);
				}

				ItGraph.this.getGraphLayoutCache().remove(cells, true, true);
				propertiesPane.setNoSelection();
				if(object){
					// update the repository list
					AdditionalPropertiesTabbedPane.getInstance().setRepositoryPanel();
				}
                                * 
                                */
                        }
            }
            });                    
            JMenuItem gridItem = new JMenuItem("Create Grid of Objects");
            //stripsitem.setActionCommand();
            gridItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                   
                    //Taking a diagramRepository from itSimple main form
                   // Element diagramRepository = ItSIMPLE.getDiagramRespository();
                    Element diagram = null;
                    
                    if(ItSIMPLE.getTypeDiagram() == 0){
                        diagram = ItSIMPLE.getDiagramRespository();                        
                    }//Diagram Repository
                    else if(ItSIMPLE.getTypeDiagram() == 1){ //Object Diagram
                        diagram = ItSIMPLE.getObjectDiagram();                               
                        }
                    objectGridFrame = new ObjectGridWizard(diagram);
                    objectGridFrame.setModal(true);
                    //objectGridFrame.pack();
                    objectGridFrame.setVisible(true);
                 }
            });
            
            menu.add(neighborsItem);
            menu.add(gridItem);

            menu.addPopupMenuListener(new PopupMenuListener() {
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                }
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    moreButton.setSelected(false);
                }
                public void popupMenuCanceled(PopupMenuEvent e) {
                    moreButton.setSelected(false);
                }
            });
            menu.show(component, 0, component.getHeight());
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
        
        
        //PDDL Buttons
        private JButton getpddlTypeTemplateButton() {
            if (pddlTypeTemplateButton == null) {
                pddlTypeTemplateButton = new JButton(new ImageIcon("resources/images/class.png"));
                pddlTypeTemplateButton.setText("Types");
                pddlTypeTemplateButton.setToolTipText("New object type");
                pddlTypeTemplateButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        //TODO: NEW TYPE ACTION;
                        insertText(type); //Código feito em 15/05 - Rosi.
                    }
                });
            }
            return pddlTypeTemplateButton;
	}
        
        private JButton getpddlPredicateTemplateButton() {
            if (pddlPredicateTemplateButton == null) {
                pddlPredicateTemplateButton = new JButton(new ImageIcon("resources/images/attribute.png"));
                pddlPredicateTemplateButton.setText("Predicates");
                pddlPredicateTemplateButton.setToolTipText("New predicate");
                pddlPredicateTemplateButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        //TODO: NEW PREDICATE ACTION;
                        insertText(predicates); //Código feito em 15/05 - Rosi.
                    }
                });
            }
            return pddlPredicateTemplateButton;
	}
        
        private JButton getpddlFunctionTemplateButton() {
            if (pddlFunctionTemplateButton == null) {
                pddlFunctionTemplateButton = new JButton(new ImageIcon("resources/images/operator2.png"));
                pddlFunctionTemplateButton.setText("Functions");
                pddlFunctionTemplateButton.setToolTipText("New predicate");
                pddlFunctionTemplateButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        //TODO: NEW PREDICATE ACTION;
                        insertText(function); //Código feito em 15/05 - Rosi.
                    }
                });
            }
            return pddlFunctionTemplateButton;
	}
        
//pddl actions
        private AbstractButton getPDDLActionsTemplateList() {
            final JToggleButton moreButton = new JToggleButton("Actions");
            //moreButton.setIcon(new ImageIcon("resources/images/makemovie.png"));
            moreButton.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        createAndShowActionsTemplateMenu((JComponent) e.getSource(), moreButton);
                    }
                }
            });
            moreButton.setFocusable(false);
            moreButton.setHorizontalTextPosition(SwingConstants.LEADING);
            return moreButton;
        }
        
        
        private void createAndShowActionsTemplateMenu(final JComponent component, final AbstractButton moreButton) {
            JPopupMenu menu = new JPopupMenu();

             JMenuItem actionItem = new JMenuItem("Action");
            //stripsitem.setActionCommand();
            actionItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: Associate Neighbors
                    System.out.println("Action");
                    //ItSIMPLE.getInstance().getDomainPDDLTextPane()
                    insertText(action); //Código feito em 13/05 - Rosi.
                }
            });                    
            JMenuItem durativeActionItem = new JMenuItem("Durative-Action");
            //stripsitem.setActionCommand();
            durativeActionItem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: Create grid of objects
                    System.out.println("Durative-Action");
                    insertText(durativeAction); //Código feito em 15/05 - Rosi.
                }
            });
            
            menu.add(actionItem);
            menu.add(durativeActionItem);

            menu.addPopupMenuListener(new PopupMenuListener() {
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                }
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    moreButton.setSelected(false);
                }
                public void popupMenuCanceled(PopupMenuEvent e) {
                    moreButton.setSelected(false);
                }
            });
            menu.show(component, 0, component.getHeight());
        }        
                
        private JButton getpddlConstraintTemplateButton() {
            if (pddlConstraintTemplateButton == null) {
                pddlConstraintTemplateButton = new JButton(new ImageIcon("resources/images/ucinv.png"));
                pddlConstraintTemplateButton.setText("Constraints");
                pddlConstraintTemplateButton.setToolTipText("New constraint");
                pddlConstraintTemplateButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        //TODO: NEW CONSTRAINT ACTION;
                        insertText(constraints); //Código feito em 15/05 - Rosi.
                    }
                });
            }
            return pddlConstraintTemplateButton;
	}                
                
        
        private AbstractButton getListRequirementButtons() {
            final JToggleButton moreButton = new JToggleButton("Requirements");
            moreButton.addItemListener(new ItemListener() {
                public void itemStateChanged(ItemEvent e) {
                    if (e.getStateChange() == ItemEvent.SELECTED) {
                        createAndShowRequirementsMenu((JComponent) e.getSource(), moreButton);
                    }
                }
            });
            moreButton.setFocusable(false);
            moreButton.setHorizontalTextPosition(SwingConstants.LEADING);
            return moreButton;
        }

        private void createAndShowRequirementsMenu(final JComponent component, final AbstractButton moreButton) {
            JPopupMenu menu = new JPopupMenu();

             JMenuItem stripsitem = new JMenuItem(":strips");
            //stripsitem.setActionCommand();
            stripsitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD STRIPS REQUIREMENTS
                    System.out.println("Add :strips");
                    insertText(strips);
                }
            });        

            JMenuItem fluentitem = new JMenuItem(":fluent");
            //stripsitem.setActionCommand();
            fluentitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD FLUENT REQUIREMENTS
                    System.out.println("Add :fluent");
                    insertText(fluent);
                }
            });

            JMenuItem typingitem = new JMenuItem(":typing");
            //stripsitem.setActionCommand();
            typingitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD TYPING REQUIREMENTS
                    System.out.println("Add :typing");
                    insertText(typing);
                }
            });

            JMenuItem negativepreconditionsitem = new JMenuItem(":negative-preconditions");
            //stripsitem.setActionCommand();
            negativepreconditionsitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD NEGATIVE-PRECONDITIONS REQUIREMENTS
                    System.out.println("Add :negative-preconditions");
                    insertText(negativePreconditions);
                }
            });

            JMenuItem disjunctivepreconditionsitem = new JMenuItem(":disjunctive-preconditions");
            //stripsitem.setActionCommand();
            disjunctivepreconditionsitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD DISJUNCTIVE-PRECONDITIONS REQUIREMENTS
                    System.out.println("Add :disjunctive-preconditions");
                    insertText(disjunctivePreconditions);
                }
            });

            JMenuItem equalityitem = new JMenuItem(":equality");
            //stripsitem.setActionCommand();
            equalityitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD EQUALITY REQUIREMENTS
                    System.out.println("Add :equality");
                    insertText(equality);
                }
            });

            JMenuItem existentialpreconditionsitem = new JMenuItem(":existential-preconditions");
            //stripsitem.setActionCommand();
            existentialpreconditionsitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD EXISTENTIAL-PRECONDITIONS REQUIREMENTS
                    System.out.println("Add :existential-preconditions");
                    insertText(existentialPreconditions);
                }
            });

            JMenuItem universalpreconditionsitem = new JMenuItem(":universal-preconditions");
            //stripsitem.setActionCommand();
            universalpreconditionsitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD UNIVERSAL-PRECONDITIONS REQUIREMENTS
                    System.out.println("Add :universal-preconditions");
                    insertText(universalPreconditions);
                }
            });

            JMenuItem quantifiedpreconditionsitem = new JMenuItem(":quantified-preconditions");
            //stripsitem.setActionCommand();
            quantifiedpreconditionsitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD QUANTIFIED-PRECONDITIONS REQUIREMENTS
                    System.out.println("Add :quantified-preconditions");
                    insertText(quantifiedPreconditions);
                }
            });

            JMenuItem conditionaleffectsitem = new JMenuItem(":conditional-effects");
            //stripsitem.setActionCommand();
            conditionaleffectsitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD CONDITIONAL-EFFECTS REQUIREMENTS
                    System.out.println("Add :conditional-effects");
                    insertText(conditionalEffects);
                }
            });

            JMenuItem adlitem = new JMenuItem(":adl");
            //stripsitem.setActionCommand();
            adlitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD ADL REQUIREMENTS
                    System.out.println("Add :adl");
                    insertText(adl);
                }
            });

            JMenuItem durativeactionitem = new JMenuItem(":durative-action");
            //stripsitem.setActionCommand();
            durativeactionitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD DURATIVE-ACTION REQUIREMENTS
                    System.out.println("Add :durative-action");
                    insertText(durativeActionsInRequirements);
                }
            });

            JMenuItem derivedpredicatesitem = new JMenuItem(":derived-predicates");
            //stripsitem.setActionCommand();
            derivedpredicatesitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD DERIVED-PREDICATES REQUIREMENTS
                    System.out.println("Add :derived-predicates");
                    insertText(derivedPredicatesInRequirements);
                }
            });

            JMenuItem timedinitialliteralsitem = new JMenuItem(":timed-initial-literals");
            //stripsitem.setActionCommand();
            timedinitialliteralsitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD TIMED-INITIAL-LITERALS REQUIREMENTS
                    System.out.println("Add :timed-initial-literals");
                    insertText(timedInitialLiterals);
                }
            });

            JMenuItem preferencesitem = new JMenuItem(":preferences");
            //stripsitem.setActionCommand();
            preferencesitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD TIMED-INITIAL-LITERALS REQUIREMENTS
                    System.out.println("Add :preferences");
                    insertText(preferences);
                }
            });

            JMenuItem constraintsitem = new JMenuItem(":constraints");
            //stripsitem.setActionCommand();
            constraintsitem.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent e) {
                    //TODO: ADD TIMED-INITIAL-LITERALS REQUIREMENTS
                    System.out.println("Add :constraints");
                    insertText(constraintsInRequirements);
                }
            });
            
            menu.add(stripsitem);
            menu.add(fluentitem);
            menu.add(typingitem);
            menu.add(negativepreconditionsitem);
            menu.add(disjunctivepreconditionsitem);
            menu.add(equalityitem);
            menu.add(existentialpreconditionsitem);
            menu.add(universalpreconditionsitem);
            menu.add(quantifiedpreconditionsitem);
            menu.add(conditionaleffectsitem);
            menu.add(adlitem);
            menu.add(durativeactionitem);
            menu.add(derivedpredicatesitem);
            menu.add(timedinitialliteralsitem);
            menu.add(preferencesitem);
            menu.add(constraintsitem);

            menu.addPopupMenuListener(new PopupMenuListener() {
                public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                }
                public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                    moreButton.setSelected(false);
                }
                public void popupMenuCanceled(PopupMenuEvent e) {
                    moreButton.setSelected(false);
                }
            });
            menu.show(component, 0, component.getHeight());
        }
        
        
        
        
        
        private JButton getpddlDerivedPredicateTemplateButton() {
            if (pddlDerivedPredicateTemplateButton == null) {
                pddlDerivedPredicateTemplateButton = new JButton(new ImageIcon("resources/images/operator.png"));
                pddlDerivedPredicateTemplateButton.setText("Derived Predicate");
                pddlDerivedPredicateTemplateButton.setToolTipText("New derived predicate");
                pddlDerivedPredicateTemplateButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        //TODO: NEW DERIVED PREDICATES ACTION;
                        insertText(derivedPredicates); //Código feito em 15/05 - Rosi.
                    }
                });
            }
            return pddlDerivedPredicateTemplateButton;
	}
        
        
        private JButton getpddlObjectTemplateButton() {
            if (pddlObjectTemplateButton == null) {
                pddlObjectTemplateButton = new JButton(new ImageIcon("resources/images/object.png"));
                pddlObjectTemplateButton.setText("Objects");
                pddlObjectTemplateButton.setToolTipText("New object");
                pddlObjectTemplateButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        //TODO: NEW OBJECT ACTION;
                        insertText(object); //Código feito em 21/05 - Rosi.
                    }
                });
            }
            return pddlObjectTemplateButton;
	}
        
        private JButton getpddlMetricTemplateButton() {
            if (pddlMetricTemplateButton == null) {
                pddlMetricTemplateButton = new JButton(new ImageIcon("resources/images/compare.png"));
                pddlMetricTemplateButton.setText("Metric");
                pddlMetricTemplateButton.setToolTipText("New metric");
                pddlMetricTemplateButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        //TODO: NEW Metric ACTION;
                        insertText(metric); //Código feito em 21/05 - Rosi.
                    }
                });
            }
            return pddlMetricTemplateButton;
	}
        
	private JButton getpddlProblemConstraintTemplateButton() {
            if (pddlProblemConstraintTemplateButton == null) {
                pddlProblemConstraintTemplateButton = new JButton(new ImageIcon("resources/images/ucinv.png"));                
                pddlProblemConstraintTemplateButton.setText("Constraints");
                pddlProblemConstraintTemplateButton.setToolTipText("New constraint");
                pddlProblemConstraintTemplateButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        //TODO: NEW PROBLEM CONSTRAINT ACTION;
                        insertText(constraints); //Código feito em 21/05 - Rosi.
                    }
                });
            }
            return pddlProblemConstraintTemplateButton;
	}
        
        
        private JButton getpddlCommentButton() {
            if (pddlCommentButton == null) {
                //pddlCommentButton = new JButton(new ImageIcon("resources/images/stop.png"));
                pddlCommentButton = new JButton();
                pddlCommentButton.setText("Add Comment");
                pddlCommentButton.setToolTipText("Add a comment line");
                pddlCommentButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        //TODO: COMMENT LINE ACTION;
                        insertText(comment); //Código feito em 15/05 - Rosi.
                    }
                });
            }
            return pddlCommentButton;
	}

        private JButton getpddlCommentLineButton() {
            if (pddlCommentLineButton == null) {
                //pddlCommentButton = new JButton(new ImageIcon("resources/images/stop.png"));
                pddlCommentLineButton = new JButton();
                pddlCommentLineButton.setText("Comment Line");
                pddlCommentLineButton.setToolTipText("Comment current line(s)");
                pddlCommentLineButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {                        
                        int line_number = getLineAtCaret(textPane);
                        gotoStartOfLine(textPane, line_number);
                        int offset = textPane.getCaretPosition();
                        insertText(offset, commentLine); //Código feito em 15/05 - Rosi.
                    }
                });
            }
            return pddlCommentLineButton;
	}

	private JButton getpddlUndoButton() {
            if (pddlUndoButton == null) {
                pddlUndoButton = new JButton(new ImageIcon("resources/images/undo.png"));
                //pddlUndoButton.setText("Undo");
                pddlUndoButton.setToolTipText("Undo");                
                pddlUndoButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        //System.out.println("fazendo Undo!");
                        try
                        {
                            if (undo.canUndo())
                            {
                                undo.undo();
                                undo.undo();
                            }
                        }
                        catch (CannotUndoException eUndo)
                        {
                            // do something
                        }
                        //updateUndoState();
                        //updateRedoState();
                    }
                });
            }
            return pddlUndoButton;
	}


	private JButton getpddlRedoButton() {
            if (pddlRedoButton == null) {
                pddlRedoButton = new JButton(new ImageIcon("resources/images/redo.png"));
                //pddlRedoButton.setText("Redo");
                pddlRedoButton.setToolTipText("Redo");
                pddlRedoButton.addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent e) {
                        //System.out.println("fazendo Redo!");
                        try
                        {
                            if (undo.canRedo())
                            {
                                undo.redo();
                                undo.redo();
                            }
                        }
                        catch (CannotRedoException eUndo)
                        {
                            // do something
                        }
                        //updateUndoState();
                        //updateRedoState();
                    }
                });
            }
            return pddlRedoButton;
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


