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


package itSIMPLE;

import languages.xml.XMLUtilities;
import itGraph.ItCellViewFactory;
import itGraph.ItGraph;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButton;
import javax.swing.JRootPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.*;
import javax.swing.JScrollPane;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.JEditorPane;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdesktop.swingx.JXTaskPane;
import org.jdesktop.swingx.JXTaskPaneContainer;

import org.jdom.*;
import org.jfree.chart.ChartPanel;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

//import database.DataBase;
//import database.ImportFromDBDialog;

import util.filefilter.PDDLFileFilter;
import util.filefilter.XMLFileFilter;

import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Event;
import java.awt.Font;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
//import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import planning.PlanSimulator;
import languages.pddl.ToXPDDL;
import languages.pddl.XPDDLToPDDL;
import languages.petrinets.toPNML;
import planning.ExecPlanner;
import sourceEditor.ItHilightedDocument;




public class ItSIMPLE extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2513131333082085568L;
	/**
	 * 
	 */
	
	private static ItSIMPLE instance = null;
	
	// main window	
	private JPanel mainPanel = null;
	private JTabbedPane mainTabbedPane = null;
	private JToolBar toolBar = null;
	// itSIMPLE menu bar
	private JMenuBar itMenuBar = null;
        //File
	private JMenu fileMenu = null;
	private JMenuItem newMenuItem = null;
	private JMenuItem openMenuItem = null;	
	private JMenuItem saveMenuItem = null;
	private JMenuItem saveAsMenuItem = null;
	private JMenuItem saveAllMenuItem = null;
	//private JMenuItem exportToDataBaseMenuItem = null;
	//private JMenuItem importFromDataBaseMenuItem = null;
	private JMenuItem exitMenuItem = null;
        //Settings
	private JMenu settingsMenu = null;
	private JMenu appearanceMenu = null;
	private JMenuItem windowsMenuItem = null;
	private JMenuItem metalMenuItem = null;
	private JMenuItem motifMenuItem = null;
	private JMenuItem defaultMenuItem = null;
	private JMenuItem plannersSettingsMenuItem = null;
        //Help
        private JMenu helpMenu = null;
        private JMenuItem aboutMenuItem = null;
        //Diagrams
	private JMenu newDiagramMenu = null;
	private JMenu newProblemMenu = null;
	private JMenu newDomainMenu = null;
	private JMenu openAsDomainMenu = null;
	private JMenu openAsProblemMenu = null;
	private JMenu openAsPetriNetMenu = null;
        //About box
        private JDialog aboutBox;
	
	// uml pane
	private JSplitPane umlSplitPane = null;
	private JSplitPane propertySplitPane = null;
	private JSplitPane diagramsSplitPane = null;
	
	//tree
	private ItFramePanel treeFramePanel = null;
	private ItTree projectsTree = null;
	private ItTreeNode treeRoot;
	private JPopupMenu treePopupMenu = null;
	
	// properties
	private JPanel propertiesPanel = null;
	private PropertiesTabbedPane propertiesPane = null;
	
	// graph
	private ItTabbedPane graphTabbedPane = null;
	private JPanel graphPanel = null;
	
	// pddl
	private JSplitPane pddlSplitPane = null;
	private JSplitPane pddlTextSplitPane = null;
	private JPanel topPddlPanel = null;
	private JPanel bottomPddlPanel = null;
	private JScrollPane topPddlScrollPane = null;
	private JScrollPane bottomPddlScrollPane = null;
	private JTextPane domainPddlTextPane = null;
	private JTextPane problemPddlTextPane = null;
	
	private JXTaskPaneContainer domainPddlContainer = null;
	private JXTaskPane domainPddlTaskPane = null;
	private JXTaskPane problemPddlTaskPane = null;
	private JXTaskPane detailPddlTaskPane = null;
	private JXTaskPane settingsPddlTaskPane = null;
	private ButtonGroup pddlButtonsGroup = null;
	private JPanel pddlPanel = null;
	private JToolBar domainPddlToolBar = null;
	private JToolBar problemPddlToolBar = null;
	private JTextPane detailsTextPane = null;
	
	//petri net
	private JSplitPane petriSplitPane = null;
	private JSplitPane petriEditorSplitPane = null;
	private JPanel petriPanel = null;
	private JXTaskPane projectPetriTaskPane = null;
	private JXTaskPane stateMachinePetriTaskPane = null;
	private JXTaskPane detailPetriTaskPane = null;
	private JTextPane petriDetailsTextPane = null;
	private JXTaskPaneContainer projectPetriContainer = null;
	private JPanel stateMachinePetriPanel = null;
	private JButton stateMachineButton = null;
	private JList stateMachineJList = null;
	private ArrayList<Element> stateMachinesList = new ArrayList<Element>();
	private Element selectedPetriNetProject = null;
	private JRootPane topPetriPane = null;
	private JPanel bottomPetriPanel = null;
	private JEditorPane petriEditorPane = null;
	private JScrollPane topPetriScrollPane = null;
	private JScrollPane bottomPetriScrollPane = null;
	private ItToolBar petriToolBar = null;
	private ItGraph petriDiagramGraph = null;
	private JEditorPane petriInfoEditorPane = null;
	
	// plan simulation
	private JPanel planSimPane = null;
	private JSplitPane planSimSplitPane = null;
	private JSplitPane planDetailsSplitPane = null;
	private JSplitPane planInfoSplitPane = null;
	private JSplitPane planVisualizationPane = null;
	private ItFramePanel planTreeFramePanel = null;
	private JTabbedPane planTreeTabbedPane = null;        
	private JTree problemsPlanTree = null;
	private DefaultTreeModel problemsPlanTreeModel = null;
	private JTree variablesPlanTree = null;
	private DefaultTreeModel variablesPlanTreeModel = null;
	private JTree selectedVariablesPlanTree = null;
	private DefaultTreeModel selectedVariablesPlanTreeModel = null;
	private JComboBox plannersComboBox = null;
	private JButton solveProblemButton = null;
	private JButton setPlannerButton = null;	
	private ItFramePanel planListFramePanel = null;
	private JList planList = null;
	private DefaultListModel planListModel = null;
	private JButton addPlanActionButton = null;
	private JButton removePlanActionButton = null;
	private JButton editPlanActionButton = null;
	private JButton importPlanButton = null;
	private JButton exportPlanButton = null;
	private ItFramePanel planAnalysisFramePanel = null;
	private JLabel planSimStatusBar = null;
	private ItFramePanel planInfoFramePanel = null;
	private JEditorPane planInfoEditorPane = null;
	private JPanel chartsPanel = null;
	private Element xmlPlan = null;
	private Thread currentThread = null;
	private ExecPlanner exe;
	private JButton replanButton;
	
	// movie maker
	private Element movie = null;
	private JPanel movieMakerPanel = null;
	private JSplitPane movieMakerSplitPane = null;
	
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static Element commonData;
	private static Element itSettings;
	private static Element itPlanners;
	private static ArrayList<Object> copyPaste = new ArrayList<Object>();
	private static ArrayList<Object> copyPasteSenders = new ArrayList<Object>();
	
	private JSplitPane graphSplitPane = null;
	private JPanel informationPanel = null;
	private JEditorPane infoEditorPane = null;
	private ItFramePanel infoPanel = null;
        private JMenuBar planNavigationMenuBar = null;
        JMenu replanMenu = null;
        private JMenuItem replanMenuItem = null;
	

	
	// ACTIONS	
	private ImageIcon openIcon = new ImageIcon("resources/images/openFolder.png");
	private Action openProjectAction = new AbstractAction("Open Project...", openIcon){
		/**
		 * 
		 */
		private static final long serialVersionUID = 2621194910304683442L;
		
		public void actionPerformed(ActionEvent e) {
			
			String lastOpenFolder = "";
			Element lastOpenFolderElement = itSettings.getChild("generalSettings").getChild("lastOpenFolder");
			if (lastOpenFolderElement != null){
				lastOpenFolder = lastOpenFolderElement.getText();
			}				
			JFileChooser fc = new JFileChooser(lastOpenFolder);
			fc.setDialogTitle("Open Project");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new XMLFileFilter());					
			int returnVal = fc.showOpenDialog(ItSIMPLE.this);					
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				openProjectFromPath(file.getPath());
				if (lastOpenFolderElement != null){
					//Holds the last open folder
					if (!lastOpenFolderElement.getText().equals(file.getParent())){
						lastOpenFolderElement.setText(file.getParent());
						XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
					}
				}			
			}
		}
	};
	
	private ImageIcon saveIcon = new ImageIcon("resources/images/save.png");
	private Action saveAction = new AbstractAction("Save Project", saveIcon){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1560163641541291473L;

		public void actionPerformed(ActionEvent e) {
			String filePath = "";
			Element project = null;
			boolean isRoot = false; 
			
			//Check if the selected node is the root
			if (((ItTreeNode)projectsTree.getLastSelectedPathComponent()) == treeRoot.getRoot()){
				isRoot = true;
			}
			
			if (treeRoot.getChildCount() > 0){
				if (treeRoot.getChildCount() == 1){
					ItTreeNode projectNode = (ItTreeNode)treeRoot.getChildAt(0);
					
					filePath = projectNode.getReference().getChildText("filePath");						
					project = projectNode.getData();
				}
				else if (!isRoot && ((ItTreeNode)projectsTree.getLastSelectedPathComponent()) != null){						
					ItTreeNode projectNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
					while (!projectNode.getData().getName().equals("project")){
						projectNode = (ItTreeNode)projectNode.getParent();
					}
				
					filePath = projectNode.getReference().getChildText("filePath");						
					project = projectNode.getData();
				}
				
				if (project != null){
					if (filePath.indexOf("*itSIMPLE*") < 0){
				
						Element tempId = project.getChild("generalInformation").getChild("id");
						project.getChild("generalInformation").removeChild("id");
						XMLUtilities.writeToFile(filePath, project.getDocument());
						project.getChild("generalInformation").addContent(tempId);				
					}
					//otherwise it is a new project
					else{
						saveAsAction.actionPerformed(null);				
					}
				}
			}	

		}
	};
	
	private ImageIcon saveAsIcon = new ImageIcon("resources/images/saveAs.png");
	private Action saveAsAction = new AbstractAction ("Save As", saveAsIcon){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1560163641541291473L;

		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Save As");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);					
			fc.setFileFilter(new XMLFileFilter());
			
			
			int returnVal = fc.showSaveDialog(ItSIMPLE.this);
			if (returnVal == JFileChooser.APPROVE_OPTION){						
				File file = fc.getSelectedFile();
				
				Element project;
				ItTreeNode currentNode;
				if (treeRoot.getChildCount() == 1){
					currentNode = (ItTreeNode)treeRoot.getChildAt(0);													
					project = currentNode.getData();
				}
				else{
					ItTreeNode projectNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();					
					while (!projectNode.getData().getName().equals("project")){
						projectNode = (ItTreeNode)projectNode.getParent();								
					}
					project = projectNode.getData();
					currentNode = projectNode;
				}
				String filePath;
				if (file.getName().toLowerCase().endsWith(".xml"))
					filePath = file.getPath();
				else
					filePath = file.getPath()+".xml";
				currentNode.getReference().getChild("filePath").setText(filePath);
				
				Element tempId = project.getChild("generalInformation").getChild("id");
				project.getChild("generalInformation").removeChild("id");						
				XMLUtilities.writeToFile(filePath, project.getDocument());
				project.getChild("generalInformation").addContent(tempId);
				
				// refresh recent projects
				Element recentProjects = itSettings.getChild("recentProjects");				
				Element inList = null;
				for(Iterator<?> iter = recentProjects.getChildren().iterator(); iter.hasNext();){
					Element recentProject = (Element)iter.next();
					String currentFilePath = recentProject.getChildText("filePath");							
					if (currentFilePath.equals(filePath)){						
						inList = recentProject;
						break;
					}					
				}
				if (inList != null){
					recentProjects.removeContent(inList);
				}
				
				if (recentProjects.getChildren().size() >= 5){
					Element last = (Element)recentProjects.getChildren().get(4);
					recentProjects.removeContent(last);
				}
				
				Element recent = new Element("project");
				
				Element name = new Element("name");
				name.setText(project.getChildText("name"));
				recent.addContent(name);
				
				Element filePathElement = new Element("filePath");
				filePathElement.setText(filePath);
				recent.addContent(filePathElement);
				
				recentProjects.addContent(0, recent);
				
				XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
				
				// rebuilds the menu because of the recent projects
				getFileMenu();
				
			}
			
		
		}
	};
	
	/*private Action exportToDataBaseAction = new AbstractAction ("Export to Database"){
		*//**
		 * 
		 *//*
		private static final long serialVersionUID = 1560163641541291473L;

		public void actionPerformed(ActionEvent e) {
			String lastOpenFolder = "";
			Element lastOpenFolderElement = itSettings.getChild("generalSettings").getChild("lastOpenFolder");
			if (lastOpenFolderElement != null){
				lastOpenFolder = lastOpenFolderElement.getText();
			}				
			JFileChooser fc = new JFileChooser(lastOpenFolder);
			fc.setDialogTitle("Export to Database");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new XMLFileFilter());					
			int returnVal = fc.showDialog(ItSIMPLE.this, "Export");					
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				Document doc = null;
				try{
					doc = XMLUtilities.readFromFile(file.getPath());				
				}
				catch(Exception e1){
					e1.printStackTrace();
				}
				String domainName = doc.getRootElement().getChildText("name");
				exportProjectToDataBase(domainName,getLatestVersion(domainName),file.getPath());
				if (lastOpenFolderElement != null){
					//Holds the last open folder
					if (!lastOpenFolderElement.getText().equals(file.getParent())){
						lastOpenFolderElement.setText(file.getParent());
						XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
					}
				}			
			}
		}
	};*/
	
	/*private Action importFromDataBaseAction = new AbstractAction ("Import from Database"){
		*//**
		 * 
		 *//*
		private static final long serialVersionUID = 1560163641541291473L;

		public void actionPerformed(ActionEvent e) {
			ImportFromDBDialog importDialog = new ImportFromDBDialog(ItSIMPLE.this);
			importDialog.setVisible(true);
		}
	};*/
	
	private ImageIcon saveAllIcon = new ImageIcon("resources/images/saveAll.png");
	private Action saveAllAction = new AbstractAction("Save All", saveAllIcon){
		/**
		 * 
		 */
		private static final long serialVersionUID = -5151166226993763827L;

		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i<treeRoot.getChildCount(); i++){
				ItTreeNode currentNode = (ItTreeNode)treeRoot.getChildAt(i);
				
				String filePath = currentNode.getReference().getChildText("filePath");					
				Element project = currentNode.getData();
				
				Element tempId = project.getChild("generalInformation").getChild("id");
				project.getChild("generalInformation").removeChild("id");						
				XMLUtilities.writeToFile(filePath, project.getDocument());
				project.getChild("generalInformation").addContent(tempId);
			}
		}
	};
	
	private Action newProjectAction = new AbstractAction("New Project",new ImageIcon("resources/images/new24.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -2864445237015276324L;

		public void actionPerformed(ActionEvent e) {
			
			org.jdom.Document doc = null;
			try{
				doc = XMLUtilities.readFromFile("resources/settings/DefaultProject.xml");				
			}
			catch(Exception e1){
				e1.printStackTrace();
			}
			if (doc != null){
				Element xmlRoot = doc.getRootElement();
				xmlRoot.getChild("generalInformation").getChild("version").setText(
						commonData.getChild("generalInformation").getChildText("version"));
				projectsTree.buildStructure(xmlRoot, null);
								
				// update petri net panels
				updatePetriNetPanels();
				
				// update pddl panels
				updatePDDLPanels();				
				
				// plan simulation problem tree
				ItTreeNode problemsPlanTreeRoot = (ItTreeNode)problemsPlanTreeModel.getRoot();				
				ItTreeNode planProjectNode = new ItTreeNode(xmlRoot.getChildText("name"), xmlRoot, null, null);
				planProjectNode.setIcon(new ImageIcon("resources/images/project.png"));
				problemsPlanTreeModel.insertNodeInto(planProjectNode, problemsPlanTreeRoot, problemsPlanTreeRoot.getChildCount());				
				List<?> domains = doc.getRootElement().getChild("diagrams").getChild("planningDomains").getChildren("domain");
				for (Iterator<?> iter = domains.iterator(); iter.hasNext();) {
					Element domain = (Element) iter.next();
					ItTreeNode planDomainNode = new ItTreeNode(domain.getChildText("name"), domain, null, null);
					planDomainNode.setIcon(new ImageIcon("resources/images/domain.png"));
					problemsPlanTreeModel.insertNodeInto(planDomainNode, planProjectNode, planProjectNode.getChildCount());
					List<?> problems = domain.getChild("planningProblems").getChildren("problem");
					for (Iterator<?> iterator = problems.iterator(); iterator.hasNext();) {
						Element problem = (Element) iterator.next();
						ItTreeNode planProblemNode = new ItTreeNode(problem.getChildText("name"), problem, null, null);
						planProblemNode.setIcon(new ImageIcon("resources/images/planningProblem.png"));
						problemsPlanTreeModel.insertNodeInto(planProblemNode, planDomainNode, planDomainNode.getChildCount());
					}
				}
				//problemsPlanTreeModel.reload();
				problemsPlanTree.expandRow(0);
			}			
						
			// Expand Root Node of the tree
			if (treeRoot.getChildCount()> 0){
				projectsTree.expandRow(0);
				saveAllMenuItem.setEnabled(true);
			}
			if (treeRoot.getChildCount() == 1){
				saveMenuItem.setEnabled(true);
				saveAsMenuItem.setEnabled(true);
			}
			else{
				saveMenuItem.setEnabled(false);
				saveAsMenuItem.setEnabled(false);
			}

			
		}
	};
	
	private Action newUseCaseDiagramAction = new AbstractAction("Use Case Diagram",new ImageIcon("resources/images/useCaseDiagram.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -2864445237015276324L;

		public void actionPerformed(ActionEvent e) {
			createNewDiagramAction("useCaseDiagram");			
		}
	};
	
	private Action newClassDiagramAction = new AbstractAction("Class Diagram",new ImageIcon("resources/images/classDiagram.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -1344774838104367132L;

		public void actionPerformed(ActionEvent e) {
			createNewDiagramAction("classDiagram");
			
		}
	};
	
	private Action newStateMachineDiagramAction = new AbstractAction("State Machine Diagram",new ImageIcon("resources/images/stateMachineDiagram.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 3889905344903899612L;

		public void actionPerformed(ActionEvent e) {
			createNewDiagramAction("stateMachineDiagram");
		}
	};
	
	private Action newActivityDiagramAction = new AbstractAction("Activity Diagram",new ImageIcon("resources/images/activityDiagram.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 755442300480255147L;

		public void actionPerformed(ActionEvent e) {
			createNewDiagramAction("activityDiagram");
		}
	};
	
	private Action newDomainAction = new AbstractAction("Planning Domain",new ImageIcon("resources/images/planningProblem.png")){

		/**
		 * 
		 */
		private static final long serialVersionUID = 7519816366429037909L;

		public void actionPerformed(ActionEvent e) {
			// uml tree
			ItTreeNode selectedNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			Element domain = (Element)commonData.getChild("definedNodes").getChild("elements").getChild("structure").getChild("domain").clone();
			
			domain.setAttribute("id",
					String.valueOf(XMLUtilities.getId(
							selectedNode.getData().getChild("diagrams").getChild("planningDomains"))));
			
			domain.getChild("name").setText(domain.getChildText("name") + domain.getAttributeValue("id"));
			selectedNode.getData().getChild("diagrams").getChild("planningDomains").addContent(domain);
			projectsTree.buildDomainNode(domain, selectedNode);
			
			// problems plan tree
			ItTreeNode problemsTreeProjectNode = (ItTreeNode)
				((ItTreeNode)problemsPlanTreeModel.getRoot()).getChildAt(treeRoot.getIndex(selectedNode));
			
			ItTreeNode problemsTreeDomainNode = new ItTreeNode(domain.getChildText("name"), domain, null, null);
			problemsTreeDomainNode.setIcon(new ImageIcon("resources/images/domain.png"));
			
			problemsPlanTreeModel.insertNodeInto(
					problemsTreeDomainNode, problemsTreeProjectNode, problemsTreeProjectNode.getChildCount());
			
			List<?> problems = domain.getChild("planningProblems").getChildren("problem");
			for (Iterator<?> iterator = problems.iterator(); iterator.hasNext();) {
				Element problem = (Element) iterator.next();
				ItTreeNode planProblemNode = new ItTreeNode(problem.getChildText("name"), problem, null, null);
				planProblemNode.setIcon(new ImageIcon("resources/images/planningProblem.png"));
				problemsPlanTreeModel.insertNodeInto(
						planProblemNode, problemsTreeDomainNode, problemsTreeDomainNode.getChildCount());
			}
		}
	};
	
	private Action newProblemAction = new AbstractAction("Problem",new ImageIcon("resources/images/planningProblem.png")){


		/**
		 * 
		 */
		private static final long serialVersionUID = -1827112480646045838L;

		public void actionPerformed(ActionEvent e) {
			// projects tree
			ItTreeNode selectedNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			Element problem = (Element)commonData.getChild("definedNodes")
				.getChild("elements").getChild("structure").getChild("problem").clone();
			
			problem.setAttribute("id",
					String.valueOf(XMLUtilities.getId(selectedNode.getData().getChild("planningProblems"))));
			problem.getChild("name").setText(problem.getChildText("name") + problem.getAttributeValue("id"));
			selectedNode.getData().getChild("planningProblems").addContent(problem);
			projectsTree.buildProblemNode(problem, selectedNode);
			
			// problems plan tree
			ItTreeNode problemsTreeProject = (ItTreeNode)((ItTreeNode)problemsPlanTreeModel.getRoot())
												.getChildAt(treeRoot.getIndex(selectedNode.getParent()));
		
			ItTreeNode problemsTreeDomain = null;
			// look for the domain in problems plan tree
			for(int i = 0; i < problemsTreeProject.getChildCount(); i++){
				ItTreeNode child = (ItTreeNode)problemsTreeProject.getChildAt(i);
				if(child.getData() == selectedNode.getData()){
					problemsTreeDomain = child;
					break;
				}
			}
			
			if(problemsTreeDomain != null){
				ItTreeNode problemsTreeProblem = 
					new ItTreeNode(problem.getChildText("name"), problem, null, null);
				
				problemsTreeProblem.setIcon(new ImageIcon("resources/images/planningProblem.png"));
				
				problemsPlanTreeModel.insertNodeInto(
						problemsTreeProblem, problemsTreeDomain, problemsTreeDomain.getChildCount());
			}
		}
	};
	
	private Action newObjectDiagramAction = new AbstractAction("Object Diagram",new ImageIcon("resources/images/objectDiagram.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 3222888101201665143L;

		public void actionPerformed(ActionEvent e) {
			createNewDiagramAction("objectDiagram");
		}
	};
	
	
	private Action deleteDiagramAction = new AbstractAction("Delete Diagram",new ImageIcon("resources/images/delete.png")){
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 4318760938713713101L;

		public void actionPerformed(ActionEvent e) {			
			ItTreeNode diagram = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			ItTreeNode project = null;
			Element projectHeader = null;
			Element openTabs = ItTabbedPane.getOpenTabs();
			String diagramType = diagram.getData().getName();
			
			// 1. Close tab if it is open
			
			String xpath;
			if (diagramType.equals("objectDiagram")){
				project = (ItTreeNode)diagram.getParent().getParent().getParent();
				projectHeader = project.getReference();
				
				xpath = "openTab[@projectID='" + projectHeader.getAttributeValue("id") +
					"' and @diagramID='" + diagram.getData().getAttributeValue("id") +
					"' and type='" + diagramType + "' and additional='" +
					diagram.getData().getParentElement().getParentElement().getAttributeValue("id") + "']";
			}else{
				project = (ItTreeNode)diagram.getParent();
				projectHeader = project.getReference();
				
				xpath = "openTab[@projectID='" + projectHeader.getAttributeValue("id") +
					"' and @diagramID='" + diagram.getData().getAttributeValue("id") +
					"' and type='" + diagramType + "']"; 
			}
			
			Element deletingDiagram = null;
			try {
				XPath path = new JDOMXPath(xpath);
				deletingDiagram = (Element)path.selectSingleNode(openTabs);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			if (deletingDiagram != null){
				graphTabbedPane.closeTab(deletingDiagram.getParent().indexOf(deletingDiagram));
			}


			
			
			
			Element parent = diagram.getData().getParentElement();
			boolean removed = parent.removeContent(diagram.getData());
			if (removed){
				projectsTree.setSelectionPath(new TreePath(project.getPath()));
				
				DefaultTreeModel model = (DefaultTreeModel)projectsTree.getModel();				
				model.removeNodeFromParent(diagram);
			}
		}
	};
	
	private Action deleteDomainAction = new AbstractAction("Delete Domain",new ImageIcon("resources/images/delete.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 4318760938713713101L;

		public void actionPerformed(ActionEvent e) {
			ItTreeNode domain = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			ItTreeNode project = (ItTreeNode)domain.getParent();
			
			Element projectHeader = project.getReference();
			Element openTabs = ItTabbedPane.getOpenTabs();
			
			// close all open object diagrams from this problem
			List<?> result = null;
			try {
				XPath path = new JDOMXPath("openTab[@projectID='"+projectHeader.getAttributeValue("id")+
						"' and (type='objectDiagram' or type='repositoryDiagram') and domain='" + 
						domain.getData().getAttributeValue("id") + "']");
				result = path.selectNodes(openTabs);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			for (int i = 0; i < result.size(); i++){
				Element openTab = (Element)result.get(i);
				graphTabbedPane.closeTab(openTab.getParent().indexOf(openTab));
			}
			
			
			boolean removed = project.getData()
				.getChild("diagrams").getChild("planningDomains").removeContent(domain.getData());
			if (removed){
				// projects tree
				projectsTree.setSelectionPath(new TreePath(project.getPath()));
				
				DefaultTreeModel model = (DefaultTreeModel)projectsTree.getModel();				
				model.removeNodeFromParent(domain);
				
				// problems plan tree
				ItTreeNode problemsTreeProject = (ItTreeNode)
					((ItTreeNode)problemsPlanTreeModel.getRoot()).getChildAt(treeRoot.getIndex(project));
				
				ItTreeNode problemsTreeDomain = null;
				// look for the domain in problems plan tree
				for(int i = 0; i < problemsTreeProject.getChildCount(); i++){
					ItTreeNode child = (ItTreeNode)problemsTreeProject.getChildAt(i);
					if(child.getData() == domain.getData()){
						problemsTreeDomain = child;
						break;
					}
				}
				
				if(problemsTreeDomain != null){
					problemsPlanTreeModel.removeNodeFromParent(problemsTreeDomain);
				}
			}
			
		}
	};
	
	
	private Action deleteProblemAction = new AbstractAction("Delete Problem",new ImageIcon("resources/images/delete.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 4318760938713713101L;

		public void actionPerformed(ActionEvent e) {
			ItTreeNode problem = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			ItTreeNode domain = (ItTreeNode)problem.getParent();
			ItTreeNode project = (ItTreeNode)domain.getParent();
			
			Element projectHeader = project.getReference();
			Element openTabs = ItTabbedPane.getOpenTabs();
			
			// close all open object diagrams from this problem
			List<?> result = null;			
			try {
				XPath path = new JDOMXPath("openTab[@projectID='"+projectHeader.getAttributeValue("id")+
						"' and type='objectDiagram' and problem='" + 
						problem.getData().getAttributeValue("id") + 
						"' and domain='"+ problem.getData().getParentElement().getParentElement().getAttributeValue("id") +"']");
				
				result = path.selectNodes(openTabs);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			for (int i = 0; i < result.size(); i++){
				Element openTab = (Element)result.get(i);
				graphTabbedPane.closeTab(openTab.getParent().indexOf(openTab));
			}
			//									problem		planningProblems	
			Element planningProblems = problem.getData().getParentElement();
			boolean removed = planningProblems.removeContent(problem.getData());
			if (removed){
				// projects tree
				projectsTree.setSelectionPath(new TreePath(project.getPath()));
				
				DefaultTreeModel model = (DefaultTreeModel)projectsTree.getModel();				
				model.removeNodeFromParent(problem);
				
				// problems plan tree				
				ItTreeNode problemsTreeProject = (ItTreeNode)((ItTreeNode)problemsPlanTreeModel.getRoot())
													.getChildAt(treeRoot.getIndex(project));

				ItTreeNode problemsTreeDomain = null;
				// look for the domain in problems plan tree
				for(int i = 0; i < problemsTreeProject.getChildCount(); i++){
					ItTreeNode child = (ItTreeNode)problemsTreeProject.getChildAt(i);
					if(child.getData() == domain.getData()){
						problemsTreeDomain = child;
						break;
					}
				}
				
				if(problemsTreeDomain != null){
					
					ItTreeNode problemsTreeProblem = null;
					// look for the problem in problems plan tree
					for(int i = 0; i < problemsTreeDomain.getChildCount(); i++){
						ItTreeNode child = (ItTreeNode)problemsTreeDomain.getChildAt(i);
						if(child.getData() == problem.getData()){
							problemsTreeProblem = child;
							break;
						}
					}
					
					problemsPlanTreeModel.removeNodeFromParent(problemsTreeProblem);
				}
			}
			
		}
	};
	
	private Action openDiagramAction = new AbstractAction("Open Diagram"){
		/**
		 * 
		 */
		private static final long serialVersionUID = -5894081679981634741L;

		public void actionPerformed(ActionEvent e) {
				
			ItTreeNode diagram = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			ItTreeNode project = null;
			if (diagram.getLevel() == 2) {
				project = (ItTreeNode)diagram.getParent();
			}
			else if (diagram.getLevel() == 3) {
				project = (ItTreeNode)diagram.getParent().getParent();
			}
			else if (diagram.getLevel() == 4) {
				project = (ItTreeNode)diagram.getParent().getParent().getParent();
			}
			
			String tabTitle = diagram.getData().getChildText("name");
			
			if (diagram.getData().getName().equals("objectDiagram")) {
				ItTreeNode domainNode = (ItTreeNode)diagram.getParent().getParent();
				if (!diagram.getData().getChildText("sequenceReference").trim().equals("")){
					tabTitle += "(" + diagram.getData().getChildText("sequenceReference")+") - "
					+ domainNode.getData().getChildText("name");	
				}
			}
			
			else if (diagram.getData().getName().equals("repositoryDiagram")) {
				ItTreeNode domainNode = (ItTreeNode)diagram.getParent();
				tabTitle += " - " + domainNode.getData().getChildText("name");
			}
			else{
				tabTitle += " - " + project.getData().getChildText("name");
			}
			
			graphTabbedPane.openTab(diagram.getData(), diagram.getData().getAttributeValue("id"),
					tabTitle, diagram.getData().getName(), project.getData(), commonData, project.getReference(),"UML");
			
		}
	};
	
	private Action closeProjectAction = new AbstractAction("Close Project",new ImageIcon("resources/images/close.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -6565724121144728532L;

		public void actionPerformed(ActionEvent e) {
			ItTreeNode project = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			DefaultTreeModel model = (DefaultTreeModel)projectsTree.getModel();
			//DefaultTreeModel pddlTreeModel = (DefaultTreeModel)pddlTree.getModel();
			//Element data = project.getData();
			ItTreeNode projectsRoot = (ItTreeNode)model.getRoot();
			int index = projectsRoot.getIndex(project);
			boolean needToClose = true;
			int option = JOptionPane.showOptionDialog(instance,
					"<html><center>Do you want to save this" +
					"<br>project before closing it?</center></html>",
					"Close Project " + project.getData().getChildText("name"),
					JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);			
			switch(option){
				case JOptionPane.YES_OPTION:{					
					saveAction.actionPerformed(null);
					
					projectsTree.setSelectionRow(0);
					model.removeNodeFromParent(project);				
				}
				break;
				case JOptionPane.NO_OPTION:{				
					projectsTree.setSelectionRow(0);
					model.removeNodeFromParent(project);				
				}
				break;
			case JOptionPane.CANCEL_OPTION:{				
					needToClose = false;				
				}
				break;
			}
			
			if (needToClose){
//				Close Open tabs
				Element projectHeader = project.getReference();
				Element openTabs = ItTabbedPane.getOpenTabs();
				
				List<?> result = null;
				try {
					XPath path = new JDOMXPath("openTab[@projectID='"+projectHeader.getAttributeValue("id")+"']");
					result = path.selectNodes(openTabs);
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				
				for (int i = 0; i < result.size(); i++){
					Element openTab = (Element)result.get(i);
					graphTabbedPane.closeTab(openTab.getParent().indexOf(openTab));
				}
				
				// update Petri Net panels
				updatePetriNetPanels();
				
				// update PDDL panels
				updatePDDLPanels();
				
				//Close the projects in the plan simulation problem tree
				ItTreeNode problemsPlanTreeRoot = (ItTreeNode)problemsPlanTreeModel.getRoot();				
				problemsPlanTreeModel.removeNodeFromParent((ItTreeNode)problemsPlanTreeRoot.getChildAt(index));			
			}
		
		}
	};
	
	private Action saveDomainToFile = new AbstractAction("Save to PDDL", new ImageIcon("resources/images/savePDDL.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 755442300480255147L;

		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Save to PDDL");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);					
			fc.setFileFilter(new PDDLFileFilter());
			
			
			int returnVal = fc.showSaveDialog(ItSIMPLE.this);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File selectedFile = fc.getSelectedFile();
				String path = selectedFile.getPath();
				if (!path.toLowerCase().endsWith(".pddl")){
					path += ".pddl";
				}
				try {
					FileWriter file = new FileWriter(path);
					file.write(domainPddlTextPane.getText());
					file.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}			
		}
	};
	
	private Action saveProblemToFile = new AbstractAction("Save to PDDL", new ImageIcon("resources/images/savePDDL.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 755442300480255147L;

		public void actionPerformed(ActionEvent e) {
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Save to PDDL");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);					
			fc.setFileFilter(new PDDLFileFilter());
			
			
			int returnVal = fc.showSaveDialog(ItSIMPLE.this);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File selectedFile = fc.getSelectedFile();
				String path = selectedFile.getPath();
				if (!path.toLowerCase().endsWith(".pddl")){
					path += ".pddl";				
				}
				try {
					FileWriter file = new FileWriter(path);
					file.write(problemPddlTextPane.getText());
					file.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	};
	
	private Action openDomainAsPDDL = new AbstractAction("PDDL Domain", new ImageIcon("resources/images/new.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 3289467959165751577L;
	
		public void actionPerformed(ActionEvent e) {
			ItTreeNode domain = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			ItTreeNode project = null;
			if (domain.getLevel() == 2) {
				project = (ItTreeNode)domain.getParent();
				
				String tabTitle = domain.getData().getChildText("name") + "(PDDL) - " + project.getData().getChildText("name");
				
				graphTabbedPane.openTab(domain.getData(), domain.getData().getAttributeValue("id"),
						tabTitle, domain.getData().getName(), project.getData(), commonData, project.getReference(),"PDDL");
			}
			
		}
	};
	
	private Action openProblemAsPDDL = new AbstractAction("PDDL Problem", new ImageIcon("resources/images/new.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 3289467959165751577L;
	
		public void actionPerformed(ActionEvent e) {
			ItTreeNode problem = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			ItTreeNode project = null;
			if (problem.getLevel() == 3) {
				project = (ItTreeNode)problem.getParent().getParent();
				
				String tabTitle = problem.getData().getChildText("name") + "(PDDL) - " + project.getData().getChildText("name");
				
				graphTabbedPane.openTab(problem.getData(), problem.getData().getAttributeValue("id"),
						tabTitle, problem.getData().getName(), project.getData(), commonData, project.getReference(),"PDDL");
			}
		}
	};

	private Action openAsPetriNet = new AbstractAction("Petri Net", new ImageIcon("resources/images/new.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 3289467959165751577L;
	
		public void actionPerformed(ActionEvent e) {
			ItTreeNode stateMachine = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			ItTreeNode project = null;
			if (stateMachine.getLevel() == 2) {
				project = (ItTreeNode)stateMachine.getParent();
				
				String tabTitle = stateMachine.getData().getChildText("name") + "(PetriNet) - " + project.getData().getChildText("name");
				
				List<Element> diagramList = new ArrayList<Element>();
				diagramList.add(stateMachine.getData());
				Element extendedNet = toPNML.modularPNMLToExtendedPTNet(toPNML.stateMachinesListToModularPNML(diagramList, project.getData()));
				
				graphTabbedPane.openTab(extendedNet, stateMachine.getData().getAttributeValue("id"),
						tabTitle, extendedNet.getName(), project.getData(), commonData, project.getReference(),"PetriNet");
			}
		}
	};
	
	private Action openPetriNetGroup = new AbstractAction("Build", new ImageIcon("resources/images/new.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 3289467959165751577L;
	
		public void actionPerformed(ActionEvent e) {
			int[] selectedIndices = stateMachineJList.getSelectedIndices();
			if(selectedIndices.length > 0){
				/*Object[] remove = DefaultGraphModel.getAll(petriDiagramGraph.getModel());
				((DefaultGraphModel)petriDiagramGraph.getModel()).remove(remove);*/			
				List<Element> diagramList = new ArrayList<Element>();
				for(int i = 0; i < selectedIndices.length; i++){
					diagramList.add(stateMachinesList.get(selectedIndices[i]));
				}
				Element ptNET = toPNML.modularPNMLToExtendedPTNet(toPNML.stateMachinesListToModularPNML(diagramList, selectedPetriNetProject));
				
				petriDiagramGraph.setProject(selectedPetriNetProject);
				petriDiagramGraph.setDiagram(ptNET);
				
				petriDiagramGraph.setVisible(false);
				petriDiagramGraph.buildDiagram();
				petriDiagramGraph.setVisible(true);
			}
		}
	};
	
	private Action drawChartAction = new AbstractAction("Draw Chart", new ImageIcon("resources/images/chart.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 8345816254094616510L;

		public void actionPerformed(ActionEvent e){
			ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
			if(selectedNode != null && selectedNode.getLevel() == 3){
				final Element problem = selectedNode.getData();
				Element domain = ((ItTreeNode)selectedNode.getParent()).getData();
				
				// create and prepare the xml data to draw the charts
				final Element analysis = (Element)commonData.getChild("planSimulationNodes")
					.getChild("analysis").clone();
				analysis.getChild("domain").setAttribute("id", domain.getAttributeValue("id"));
				analysis.getChild("problem").setAttribute("id", problem.getAttributeValue("id"));
				
				Object[] checked = CheckBoxNode.getCheckedNodes(
						(CheckBoxNode)variablesPlanTree.getModel().getRoot());
				if(checked.length > 0){
					for (int i = 0; i < checked.length; i++) {
						CheckBoxNode node = (CheckBoxNode)checked[i];
						
						if(node != null && (node.getUserObject().equals("States") || node.getLevel() == 3)){
							Element attribute = node.getData();
							CheckBoxNode objectNode = (node.getLevel() == 3)
													?(CheckBoxNode)node.getParent().getParent()
													:(CheckBoxNode)node.getParent();
							Element object = objectNode.getData();
							
							Element variable = (Element)commonData.getChild("planSimulationNodes").getChild("variable").clone();
							variable.setAttribute("type", "attr");
							variable.setAttribute("id", String.valueOf(i));
							variable.getChild("object").setAttribute("id", object.getAttributeValue("id"));
							variable.getChild("object").setAttribute("class", object.getChildText("class"));
							Element xmlAttribute = new Element("attribute");
							xmlAttribute.setAttribute("id", attribute.getAttributeValue("id"));
							xmlAttribute.setAttribute("type", attribute.getChildText("type"));
							xmlAttribute.setAttribute("class", attribute.getParentElement().getParentElement().getAttributeValue("id"));
							variable.getChild("object").addContent(xmlAttribute);
							
							analysis.getChild("variables").addContent(variable);
						}
					}
                                        
					
					// the thread is created so the status bar can be refreshed
                                        // fill out xml data with values to draw the charts and draw it.
					new Thread(){
						public void run() {
							
                                                        
                                                        //fill out the analysis xml data
                                                        PlanSimulator.buildPlanAnalysisDataset(analysis, xmlPlan, problem);                                                      

                                                        //Prepare HTML version of the analysis (incluiding charts).
                                                        String html = PlanSimulator.createHTMLPlanAnalysis(analysis, xmlPlan, problem);
                                                        System.out.println(html);
                                                        
							//draw the charts in the iterface 
							List<ChartPanel> chartPanels = PlanSimulator.drawCharts(analysis, problem);
							chartsPanel.removeAll();
							for (Iterator<ChartPanel> iter = chartPanels.iterator(); iter.hasNext();) {
								ChartPanel chartPanel = iter.next();						
								chartsPanel.add(chartPanel);
							}
							chartsPanel.revalidate();
						}
					}.start();
									

				}
			}					
		}
		
	};
	
	private Action importPlanAction = new AbstractAction("", new ImageIcon("resources/images/import.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 2911608937435901074L;                
		public void actionPerformed(ActionEvent e){
			String lastOpenFolder = "";
			Element lastOpenFolderElement = itSettings.getChild("generalSettings").getChild("lastOpenFolder");
			if (lastOpenFolderElement != null){
				lastOpenFolder = lastOpenFolderElement.getText();
			}				
			JFileChooser fc = new JFileChooser(lastOpenFolder);
			fc.setDialogTitle("Import Plan");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new XMLFileFilter());// TODO add other file types		
			int returnVal = fc.showOpenDialog(ItSIMPLE.this);					
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();
				planListModel.clear();
				xmlPlan = null;
				try{
					xmlPlan = XMLUtilities.readFromFile(file.getPath()).getRootElement();
				}
				catch (Exception e1){
					e1.printStackTrace();
				}
				setPlanList(xmlPlan);
				if (lastOpenFolderElement != null){
					//Holds the last open folder
					if (!lastOpenFolderElement.getText().equals(file.getParent())){
						lastOpenFolderElement.setText(file.getParent());
						XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
					}
				}			
			}
		}
	};
	
	private Action exportPlanAction = new AbstractAction("", new ImageIcon("resources/images/export.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -2250355844729990390L;

		public void actionPerformed(ActionEvent e){
			JFileChooser fc = new JFileChooser();
			fc.setDialogTitle("Export Plan");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);					
			fc.setFileFilter(new XMLFileFilter());
			
			
			int returnVal = fc.showSaveDialog(ItSIMPLE.this);
			if (returnVal == JFileChooser.APPROVE_OPTION){						
				File file = fc.getSelectedFile();
				String filePath;
				if (file.getName().toLowerCase().endsWith(".xml"))
					filePath = file.getPath();
				else
					filePath = file.getPath()+".xml";
				
				XMLUtilities.writeToFile(filePath, new Document((Element)xmlPlan.clone()));
			}
		}
	};

	private Action addPlanAction = new AbstractAction("", new ImageIcon("resources/images/new.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 0L;

		public void actionPerformed(ActionEvent e){
			// get the selected problem
			ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();			
			PlanActionDialog dialog = new PlanActionDialog(selectedNode.getData(), xmlPlan);
			dialog.setVisible(true);
		}
	};
	
	private Action removePlanAction = new AbstractAction("", new ImageIcon("resources/images/delete.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -128458169944328489L;

		public void actionPerformed(ActionEvent e){
			int selected = planList.getSelectedIndex();
			
			List<?> actions = xmlPlan.getChild("plan").getChildren("action");		
			
			if(actions.size() > 0){
				// remove the action
				Element action = (Element)actions.get(selected);
				action.detach();
			}
			
			setPlanList(xmlPlan);
		}
	};
	
	private Action editPlanAction = new AbstractAction("", new ImageIcon("resources/images/edit.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -5835074259270839760L;

		public void actionPerformed(ActionEvent e){
			int selected = planList.getSelectedIndex();
			
			List<?> actions = xmlPlan.getChild("plan").getChildren("action");		
			
			if(actions.size() > 0){
				// edit the action
				Element action = (Element)actions.get(selected);
				ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();		
				PlanActionDialog dialog = new PlanActionDialog(selectedNode.getData(), action, xmlPlan);
				dialog.setVisible(true);
			}
		}
	};
	
	private Action plannersSettingsAction = new AbstractAction("Planners Settings"){
		/**
		 * 
		 */
		private static final long serialVersionUID = -4809817973430889953L;

		public void actionPerformed(ActionEvent e){
			final PlannersSettingsDialog dialog = new PlannersSettingsDialog(ItSIMPLE.this);
			
			dialog.setVisible(true);			
		}
	};
        
        /**
         * This action opens the About box dialog
         */
 	private Action aboutAction = new AbstractAction("About..."){
		public void actionPerformed(ActionEvent e){
                                if (aboutBox == null) {
                                    final AboutBox dialog = new AboutBox(ItSIMPLE.this);
                                    dialog.setLocationRelativeTo(ItSIMPLE.this);
                                    dialog.setVisible(true);
                                 }
		}
	};       
	
	private Action replanAction = new AbstractAction("Replan"){
		/**
		 * 
		 */
		private static final long serialVersionUID = -5055681236004995432L;

		@Override
		public void actionPerformed(ActionEvent e) {
			PlanNavigationList.getInstance().replan();
		}
	};
        
        private Action resourcesAvailabilityAction = new AbstractAction("Resources Availability"){		

		@Override
		public void actionPerformed(ActionEvent e) {
			//TODO
		}
	};
	
	public void openProjectFromPath(String path){
		
		//1. Open the project at the itSIMPLE if it is not already open
		boolean newProject = true;						
		for (int i =0; i<treeRoot.getChildCount(); i++){
			ItTreeNode currentNode = (ItTreeNode)treeRoot.getChildAt(i);
			String currentFilePath = currentNode.getReference().getChildText("filePath");							
			if (currentFilePath.equals(path)){
				newProject = false;
				break;
			}							
		}
		
		if (newProject){						
			Document doc = null;
			try{
				doc = XMLUtilities.readFromFile(path);				
			}
			catch(Exception e1){
				e1.printStackTrace();
			}
			if (doc != null){
				Element xmlRoot = doc.getRootElement();
				
				//1.0 update the version
				VersionUpdater.updateVersion(xmlRoot);				
				
				// 1.1 Add the project to the recent projects list
				Element recentProjects = itSettings.getChild("recentProjects");				
				Element inList = null;
				for(Iterator<?> iter = recentProjects.getChildren().iterator(); iter.hasNext();){
					Element recentProject = (Element)iter.next();
					String currentFilePath = recentProject.getChildText("filePath");							
					if (currentFilePath.equals(path)){						
						inList = recentProject;
						break;
					}					
				}
				if (inList != null){
					recentProjects.removeContent(inList);
				}
				
				if (recentProjects.getChildren().size() >= 5){
					Element last = (Element)recentProjects.getChildren().get(4);
					recentProjects.removeContent(last);
				}
				
				Element recent = new Element("project");
				
				Element name = new Element("name");
				name.setText(xmlRoot.getChildText("name"));
				recent.addContent(name);
				
				Element filePath = new Element("filePath");
				filePath.setText(path);
				recent.addContent(filePath);
				
				recentProjects.addContent(0, recent);
				
				XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
				
				// rebuilds the menu because of the recent projects
				getFileMenu();
												
				projectsTree.buildStructure(xmlRoot, path);
				
				ItTreeNode newProjectNode = new ItTreeNode();
				newProjectNode.setData(xmlRoot);
				newProjectNode.setUserObject(xmlRoot.getChildText("name"));
				newProjectNode.setIcon(new ImageIcon("resources/images/project.png"));
				
				// Expand Root Node of the tree
				if (treeRoot.getChildCount()> 0){
					projectsTree.expandRow(0);
					saveAllMenuItem.setEnabled(true);
				}
				if (treeRoot.getChildCount() == 1){
					saveMenuItem.setEnabled(true);
					saveAsMenuItem.setEnabled(true);
				}
				else{
					saveMenuItem.setEnabled(false);
					saveAsMenuItem.setEnabled(false);
				}
				
				// update Petri Net panels
				updatePetriNetPanels();
				
				// update PDDL panels
				updatePDDLPanels();
				
				// plan simulation problem tree
				ItTreeNode problemsPlanTreeRoot = (ItTreeNode)problemsPlanTreeModel.getRoot();				
				ItTreeNode planProjectNode = (ItTreeNode)newProjectNode.clone();
				planProjectNode.setIcon(new ImageIcon("resources/images/project.png"));
				problemsPlanTreeModel.insertNodeInto(planProjectNode, problemsPlanTreeRoot, problemsPlanTreeRoot.getChildCount());				
				List<?> domains = doc.getRootElement().getChild("diagrams").getChild("planningDomains").getChildren("domain");
				for (Iterator<?> iter = domains.iterator(); iter.hasNext();) {
					Element domain = (Element) iter.next();
					ItTreeNode planDomainNode = new ItTreeNode(domain.getChildText("name"), domain, null, null);
					planDomainNode.setIcon(new ImageIcon("resources/images/domain.png"));
					problemsPlanTreeModel.insertNodeInto(planDomainNode, planProjectNode, planProjectNode.getChildCount());
					List<?> problems = domain.getChild("planningProblems").getChildren("problem");
					for (Iterator<?> iterator = problems.iterator(); iterator.hasNext();) {
						Element problem = (Element) iterator.next();
						ItTreeNode planProblemNode = new ItTreeNode(problem.getChildText("name"), problem, null, null);
						planProblemNode.setIcon(new ImageIcon("resources/images/planningProblem.png"));
						problemsPlanTreeModel.insertNodeInto(planProblemNode, planDomainNode, planDomainNode.getChildCount());
					}
				}
				problemsPlanTree.expandRow(0);
			}
		}
	}
	
	
	/**
	 * This method exports a project to the database
	 * @param name
	 * @param version
	 * @param path
	 */
	/*private void exportProjectToDataBase(String name, String version, String path){
		File xmlFile = new File(path);
		DataBase eInsertType = new DataBase();
		
		eInsertType.setTableName("itsimple");
		eInsertType.setColumnList("itssname, itssversion, itsxmodel"); // please, don't use ()
		eInsertType.setValueList("?, ?, ?"); // please, don't use ()
		eInsertType.addToParametersList(name);
		eInsertType.addToParametersList(version);
		eInsertType.addToParametersList(xmlFile);
		eInsertType.Insert();
	}*/
	
	/**
	 * This class returns the latest version of the project
	 * @param name
	 */
	/*private String getLatestVersion(String name){
		DataBase eSelectType = new DataBase();
		
		eSelectType.setColumnList("itssversion"); //please, don't use *
		eSelectType.setTableName("itsimple"); //
		eSelectType.setWhereClause("itssname = ?"); //where clause, null if not applicable
		eSelectType.setOrderClause(null); //order by clause, null if not applicable
		eSelectType.setGroupClause(null); //group by clause, null if not applicable
		eSelectType.setHavingClause(null); //having clause, null if not applicable
		eSelectType.addToParametersList(name);
		eSelectType.Select();
		
		double maxVersion = 1.0;
		try {
			 while (eSelectType.getRs().next()) {
				String version = eSelectType.getRs().getString("itssversion");
				double currentVersion = Double.parseDouble(version);
					if(maxVersion < currentVersion){
						maxVersion = currentVersion;
					}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		eSelectType.Close();
		return Double.toString(maxVersion+0.1);
	}*/
	
	
	private void createNewDiagramAction(String diagramType){
		ItTreeNode selectedNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
		Element diagram = (Element)commonData.getChild("definedNodes").getChild("diagrams").getChild(diagramType).clone();
		if (selectedNode.getData().getName().equals("problem")){
			String id = String.valueOf(XMLUtilities.getId(selectedNode.getData().getChild("objectDiagrams")));
			diagram.setAttribute("id", id);
			diagram.getChild("name").setText(diagram.getChildText("name") + " " + diagram.getAttributeValue("id"));
			selectedNode.getData().getChild("objectDiagrams").addContent(diagram);
			ItTreeNode diagramNode = new ItTreeNode(diagram.getChildText("name"), diagram, null, null);
			ImageIcon icon = new ImageIcon("resources/images/" + diagramType + ".png");
			diagramNode.setIcon(icon);
			((DefaultTreeModel)projectsTree.getModel()).insertNodeInto(diagramNode, selectedNode, selectedNode.getChildCount());
		} else {						
			diagram.setAttribute("id",
					String.valueOf(XMLUtilities.getId(selectedNode.getData().getChild("diagrams").getChild(diagramType + "s"))));
			diagram.getChild("name").setText(diagram.getChildText("name") + " " + diagram.getAttributeValue("id"));
			selectedNode.getData().getChild("diagrams").getChild(diagramType + "s").addContent(diagram);
			ItTreeNode diagramNode = new ItTreeNode(diagram.getChildText("name"), diagram, null, null);
			ImageIcon icon = new ImageIcon("resources/images/" + diagramType + ".png");
			diagramNode.setIcon(icon);
			((DefaultTreeModel)projectsTree.getModel()).insertNodeInto(diagramNode, selectedNode, selectedNode.getChildCount());
		}

	}
	
	/**
	 * This method initializes itMenuBar	
	 * 	
	 * @return javax.swing.JMenuBar	
	 */
	private JMenuBar getItMenuBar() {
		if (itMenuBar == null) {
			itMenuBar = new JMenuBar();
			itMenuBar.add(getFileMenu());
			itMenuBar.add(getSettingsMenu());
                        itMenuBar.add(getHelpMenu());
		}
		return itMenuBar;
	}

	/**
	 * This method initializes jMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getFileMenu() {
		if (fileMenu == null) {
			fileMenu = new JMenu();
			fileMenu.setText("File");
			fileMenu.setMnemonic(KeyEvent.VK_F);
		}
		fileMenu.removeAll();
		fileMenu.add(getNewMenuItem());
		fileMenu.add(getOpenMenuItem());
		fileMenu.addSeparator();
		fileMenu.add(getSaveMenuItem());
		fileMenu.add(getSaveAsMenuItem());
		fileMenu.add(getSaveAllMenuItem());
		fileMenu.addSeparator();
		//Database
		//TODO left for the futures versions 
		//fileMenu.add(getExportToDataBaseMenuItem());
		//fileMenu.add(getImportFromDataBaseMenuItem());
		//fileMenu.addSeparator();
		
		List<?> recentProjects = itSettings.getChild("recentProjects").getChildren("project");
		if (recentProjects.size() > 0){
			int projectCounter = 1;
			for (Iterator<?> iter = recentProjects.iterator(); iter.hasNext();){
				Element recentProject = (Element)iter.next();
				String path = recentProject.getChildText("filePath");
				String fileName = path.substring(path.lastIndexOf("\\")+1, path.length());					
		   			Action action = new AbstractAction(projectCounter + ". " +
		   					recentProject.getChildText("name") + " [" + fileName +"]"){
					/**
					 *
					 */
					private static final long serialVersionUID = -9179932634109698814L;

					public void actionPerformed(ActionEvent e) {
                    	//System.out.println(this.getValue("data"));
                    	Element projectElement = (Element)this.getValue("data");
						if (projectElement != null){
							openProjectFromPath(projectElement.getChildText("filePath"));
						}
                    }
                };
                //action.putValue(Action.SMALL_ICON, new ImageIcon("resources/images/project.png"));
                action.putValue(Action.SHORT_DESCRIPTION, path);
                action.putValue("data", recentProject);
                JMenuItem recentProjectItem = new JMenuItem(action);
                recentProjectItem.setMnemonic((int)(projectCounter + "").charAt(0));
                fileMenu.add(recentProjectItem);
                projectCounter++;
			}
			
			fileMenu.addSeparator();				
		}		
		
		fileMenu.add(getExitMenuItem());
			
		return fileMenu;
	}
	
	/**
	 * This method initializes newMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getNewMenuItem() {
		if (newMenuItem == null) {
			newMenuItem = new JMenuItem(newProjectAction);
			//newMenuItem.setIcon(new ImageIcon("resources/images/new24.png"));
			newMenuItem.setMnemonic(KeyEvent.VK_N);
			newMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK, true));
		}
		return newMenuItem;
	}

	/**
	 * This method initializes openMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getOpenMenuItem() {
		if (openMenuItem == null) {
			openMenuItem = new JMenuItem(openProjectAction);
			openMenuItem.setMnemonic(KeyEvent.VK_O);
			openMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Event.CTRL_MASK, true));			
		}
		return openMenuItem;
	}
	
	/**
	 * This method initializes saveMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveMenuItem() {
		if (saveMenuItem == null) {
			saveMenuItem = new JMenuItem(saveAction);
			saveMenuItem.setEnabled(false);
			saveMenuItem.setMnemonic(KeyEvent.VK_S);
			saveMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Event.CTRL_MASK, true));			
		}
		return saveMenuItem;
	}

	/**
	 * This method initializes saveAsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveAsMenuItem() {
		if (saveAsMenuItem == null) {
			saveAsMenuItem = new JMenuItem(saveAsAction);
			saveAsMenuItem.setEnabled(false);			
			
		}
		return saveAsMenuItem;
	}
	
	/**
	 * This method initializes saveAllMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getSaveAllMenuItem() {
		if (saveAllMenuItem == null) {
			saveAllMenuItem = new JMenuItem(saveAllAction);
			saveAllMenuItem.setEnabled(false);			
		}
		return saveAllMenuItem;
	}
	
	/*private JMenuItem getExportToDataBaseMenuItem(){
		if(exportToDataBaseMenuItem == null){
			exportToDataBaseMenuItem = new JMenuItem(exportToDataBaseAction);
			exportToDataBaseMenuItem.setEnabled(true);
			
		}
		return exportToDataBaseMenuItem;
	}*/
	
	/*private JMenuItem getImportFromDataBaseMenuItem(){
		if(importFromDataBaseMenuItem == null){
			importFromDataBaseMenuItem = new JMenuItem(importFromDataBaseAction);
			importFromDataBaseMenuItem.setEnabled(true);
		}
		return importFromDataBaseMenuItem;
	}*/
	
	/**
	 * This method initializes exitItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getExitMenuItem() {
		if (exitMenuItem == null) {
			exitMenuItem = new JMenuItem();
			exitMenuItem.setText("Exit");
			exitMenuItem.setMnemonic(KeyEvent.VK_E);
			ImageIcon exitIcon = new ImageIcon("resources/images/exit.png");
			exitMenuItem.setIcon(exitIcon);
			exitMenuItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					System.exit(0);
				}
			});
		}
		return exitMenuItem;
	}

	/**
	 * This method initializes settingsMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getSettingsMenu() {
		if (settingsMenu == null) {
			settingsMenu = new JMenu();
			settingsMenu.setText("Settings");
			settingsMenu.setMnemonic(KeyEvent.VK_S);
			settingsMenu.add(getAppearanceMenu());
			settingsMenu.add(getPlannersSettingsMenuItem());
		}
		return settingsMenu;
	}

	/**
	 * This method initializes appearanceMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getAppearanceMenu() {
		if (appearanceMenu == null) {
			appearanceMenu = new JMenu();
			appearanceMenu.setText("Appearance");
			appearanceMenu.add(getDefaultMenuItem());
			appearanceMenu.add(getWindowsMenuItem());
			appearanceMenu.add(getMetalMenuItem());
			appearanceMenu.add(getMotifMenuItem());			
		}
		return appearanceMenu;
	}

	/**
	 * This method initializes windowsItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getWindowsMenuItem() {
		if (windowsMenuItem == null) {
			windowsMenuItem = new JMenuItem();
			windowsMenuItem.setText("Windows");
			windowsMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try{
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
						SwingUtilities.updateComponentTreeUI(instance);
						
					}
					catch(Exception e1) {
				          e1.printStackTrace();
				    }
				}
			});
		}
		return windowsMenuItem;
	}

	/**
	 * This method initializes metalItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getMetalMenuItem() {
		if (metalMenuItem == null) {
			metalMenuItem = new JMenuItem();
			metalMenuItem.setText("Metal");
			metalMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try{
 						UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
						SwingUtilities.updateComponentTreeUI(instance);
						instance.pack();
					}
					catch(Exception e1) {
				          e1.printStackTrace();
				    }
				}
			});
		}
		return metalMenuItem;
	}

	/**
	 * This method initializes motifItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getMotifMenuItem() {
		if (motifMenuItem == null) {
			motifMenuItem = new JMenuItem();
			motifMenuItem.setText("CDE/Motif");
			motifMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try{
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
						SwingUtilities.updateComponentTreeUI(instance);
					}
					catch(Exception e1) {
				          e1.printStackTrace();
				    }
				}
			});
		}
		return motifMenuItem;
	}

	/**
	 * This method initializes defaultItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getDefaultMenuItem() {
		if (defaultMenuItem == null) {
			defaultMenuItem = new JMenuItem();
			defaultMenuItem.setText("Default");
			defaultMenuItem.addActionListener(new java.awt.event.ActionListener() {
				public void actionPerformed(java.awt.event.ActionEvent e) {
					try{
						UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
						SwingUtilities.updateComponentTreeUI(instance);
					}
					catch(Exception e1) {
				          e1.printStackTrace();
				    }
				}
			});
		}
		return defaultMenuItem;
	}
	
	/**
	 * This method initializes plannersSettingsMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getPlannersSettingsMenuItem() {
		if (plannersSettingsMenuItem == null) {
			plannersSettingsMenuItem = new JMenuItem();
			plannersSettingsMenuItem.setText("Planners");
			plannersSettingsMenuItem.setAction(plannersSettingsAction);
		}
		return plannersSettingsMenuItem;
	}	

        

	/**
	 * This method initializes helpMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getHelpMenu() {
		if (helpMenu == null) {
			helpMenu = new JMenu();
			helpMenu.setText("Help");
			helpMenu.setMnemonic(KeyEvent.VK_S);
			helpMenu.add(getAboutMenuItem());
		}
		return helpMenu;
	} 
        
	/**
	 * This method initializes aboutMenuItem	
	 * 	
	 * @return javax.swing.JMenuItem	
	 */
	private JMenuItem getAboutMenuItem() {
		if (aboutMenuItem == null) {
			aboutMenuItem = new JMenuItem();
			aboutMenuItem.setText("About...");
			aboutMenuItem.setAction(aboutAction);
		}
		return aboutMenuItem;
	}        
        
	/**
	 * This method initializes umlSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getUmlSplitPane() {
		if (umlSplitPane == null) {
			umlSplitPane = new JSplitPane();
			umlSplitPane.setContinuousLayout(true);
			umlSplitPane.setOneTouchExpandable(true);
			umlSplitPane.setDividerLocation(screenSize.width/5);
			umlSplitPane.setDividerSize(8);
			//umlSplitPane.setPreferredSize(screenSize);			
			umlSplitPane.setRightComponent(getGraphSplitPane());
			umlSplitPane.setLeftComponent(getPropertySplitPane());
		}
		return umlSplitPane;
	}

	/**
	 * This method initializes splitPane1	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getPropertySplitPane() {
		if (propertySplitPane == null) {
			propertySplitPane = new JSplitPane();
			propertySplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			propertySplitPane.setOneTouchExpandable(false);
			propertySplitPane.setDividerSize(3);
			propertySplitPane.setDividerLocation(screenSize.width/4);
			propertySplitPane.setResizeWeight(0.8);
			propertySplitPane.setMinimumSize(new java.awt.Dimension(150,10));
			propertySplitPane.setPreferredSize(new Dimension(screenSize.width/4-20, screenSize.height/2 - 50));			
			propertySplitPane.setTopComponent(getTreeFramePanel());
			propertySplitPane.setBottomComponent(getPropertiesPanel());
			propertySplitPane.setContinuousLayout(true);
		}
		return propertySplitPane;
	}	

	/**
	 * This method initializes propertiesPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getPropertiesPanel() {
		if (propertiesPanel == null) {
			propertiesPanel = new JPanel();
			propertiesPanel.setMinimumSize(new java.awt.Dimension(50,150));
			propertiesPanel.setPreferredSize(new java.awt.Dimension(100,150));
			propertiesPanel.setLayout(new BorderLayout());
			ItFramePanel propertiesFramePanel = new ItFramePanel(":: Properties", ItFramePanel.MINIMIZE_MAXIMIZE);
			propertiesFramePanel.setContent(getPropertiesPane(), false);
			propertiesFramePanel.setParentSplitPane(propertySplitPane);			
			propertiesPanel.add(propertiesFramePanel, BorderLayout.CENTER);
			graphTabbedPane.setPropertiesPane(propertiesPane);
		}
		return propertiesPanel;
	}

	/**
	 * This method initializes tree	
	 * 	
	 * @return javax.swing.JTree	
	 */
	private ItTree getProjectsTree() {
		if (projectsTree == null) {
			treeRoot = new ItTreeNode("itSIMPLE Projects");
			treeRoot.setIcon(new ImageIcon("resources/images/projects.png"));
			projectsTree = new ItTree(treeRoot);
			projectsTree.setCellRenderer(new ItTreeCellRenderer());
			projectsTree.setShowsRootHandles(true);			
			projectsTree.setVisible(true);			
			projectsTree.setEditable(false);
			projectsTree.add(getTreePopupMenu());
			projectsTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
				public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
					ItTreeNode selectedNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
					if (treeRoot.getChildCount() == 1){
						saveMenuItem.setEnabled(true);
						saveAsMenuItem.setEnabled(true);						
					}
					else{
						saveMenuItem.setEnabled(false);
						saveAsMenuItem.setEnabled(false);
					}					
					
					if (selectedNode != null){						
						if (selectedNode != treeRoot){
							saveMenuItem.setEnabled(true);
							saveAsMenuItem.setEnabled(true);
							Element selected = selectedNode.getData();							
							if (selected != null){
								propertiesPane.showProperties(selectedNode, projectsTree);						
							}
							else{
								propertiesPane.setNoSelection();
							}	
						}
						else{
							propertiesPane.setNoSelection();
						}

					}					
					
													
				}
			});
			projectsTree.addMouseListener(new java.awt.event.MouseAdapter() {
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (e.getClickCount() == 2){
						TreePath path = projectsTree.getPathForLocation(e.getX(), e.getY());
						if (path != null && projectsTree.getLastSelectedPathComponent() != projectsTree.getModel().getRoot()){							
							ItTreeNode selectedNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
							//Diagrams
							if (selectedNode.getLevel() == 2){
								if (!selectedNode.getData().getName().equals("problem") &&
										!selectedNode.getData().getName().equals("domain")) {
									openDiagramAction.actionPerformed(null);
								}
							}
							//Planning Problems
							else if (selectedNode.getData().getName().equals("objectDiagram") || 
									selectedNode.getData().getName().equals("repositoryDiagram")){
								openDiagramAction.actionPerformed(null);
							}
						}
					}
				}
				public void mouseReleased(java.awt.event.MouseEvent e) {// this is for Windows
					if (e.isPopupTrigger()){
						TreePath path = projectsTree.getPathForLocation(e.getX(), e.getY());
						if (path != null){
							projectsTree.setSelectionPath(path);
							getTreePopupMenu().show(projectsTree, e.getX(), e.getY());
						}						
					}
				}
				public void mousePressed(java.awt.event.MouseEvent e) {// this is for Linux
					if (e.isPopupTrigger()){
						TreePath path = projectsTree.getPathForLocation(e.getX(), e.getY());
						if (path != null){
							projectsTree.setSelectionPath(path);
							getTreePopupMenu().show(projectsTree, e.getX(), e.getY());
						}						
					}
				}
			});
			projectsTree.addKeyListener(new java.awt.event.KeyAdapter() {
				public void keyPressed(java.awt.event.KeyEvent e) {					
					if (e.getKeyCode() == KeyEvent.VK_ENTER){
						if (projectsTree.getLastSelectedPathComponent() != projectsTree.getModel().getRoot()){
							ItTreeNode selectedNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
							
							if ((selectedNode.getLevel() == 2 && !selectedNode.getData().getName().equals("domain")) ||
									selectedNode.getData().getName().equals("repositoryDiagram") ||
									selectedNode.getData().getName().equals("objectDiagram")){
								openDiagramAction.actionPerformed(null);
							}						
						}
					}
				}
			});
			projectsTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);			
		}
		return projectsTree;
	}
		

	/**
	 * This method initializes graphTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private ItTabbedPane getGraphTabbedPane() {
		if (graphTabbedPane == null) {
			graphTabbedPane = new ItTabbedPane();			
		}
		return graphTabbedPane;
	}

	/**
	 * This method initializes jScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private ItFramePanel getTreeFramePanel() {
		if (treeFramePanel == null) {
			treeFramePanel = new ItFramePanel(":: Project Explorer", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
			treeFramePanel.setContent(getProjectsTree(), true);
			treeFramePanel.setPreferredSize(new Dimension(50,screenSize.height/2 - 50));
		}
		return treeFramePanel;
	}
	
	/**
	 * This method initializes diagramsSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getDiagramsSplitPane() {
		if (diagramsSplitPane == null) {
			diagramsSplitPane = new JSplitPane();
			diagramsSplitPane.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
			diagramsSplitPane.setOneTouchExpandable(true);
			diagramsSplitPane.setDividerSize(6);
			diagramsSplitPane.setDividerLocation(screenSize.width);			
			diagramsSplitPane.setResizeWeight(0.5);
			//diagramsSplitPane.setMinimumSize(new java.awt.Dimension(150,10));
			//diagramsSplitPane.setPreferredSize(new java.awt.Dimension(150,10));
			diagramsSplitPane.setContinuousLayout(true);
			diagramsSplitPane.setLeftComponent(getGraphPanel());
			
			ItFramePanel settingsPanel = new ItFramePanel(":: Additional Properties", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
			settingsPanel.setMinimumSize(new Dimension(150,50));
			settingsPanel.setContent(AdditionalPropertiesTabbedPane.getInstance(), false);
			diagramsSplitPane.setBottomComponent(settingsPanel);
			
		}
		return diagramsSplitPane;
	}
	

	/**
	 * This method initializes graphPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getGraphPanel() {
		if (graphPanel == null) {
			graphPanel = new JPanel();
			graphPanel.setLayout(new BorderLayout());
			ItFramePanel diagramsPanel = new ItFramePanel(":: Diagrams", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
			diagramsPanel.setContent(getGraphTabbedPane(), false);
			graphPanel.add(diagramsPanel, BorderLayout.CENTER);			
		}
		return graphPanel;
	}
	
	public static Element getIconsPathElement(){		
		return itSettings.getChild("generalSettings").getChild("graphics").getChild("iconsPath");
	}

	/**
	 * This method initializes newDiagramMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getNewDiagramMenu() {
		if (newDiagramMenu == null) {
			newDiagramMenu = new JMenu();
			newDiagramMenu.setIcon(new ImageIcon("resources/images/new.png"));
			newDiagramMenu.setText("New");
			newDiagramMenu.add(newUseCaseDiagramAction);
			newDiagramMenu.add(newClassDiagramAction);
			newDiagramMenu.add(newStateMachineDiagramAction);
			newDiagramMenu.add(newActivityDiagramAction);
			newDiagramMenu.add(newDomainAction);				
		}
		return newDiagramMenu;
	}
	
	/**
	 * This method initializes newDiagramMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getNewProblemMenu() {
		if (newProblemMenu == null) {
			newProblemMenu = new JMenu();
			newProblemMenu.setIcon(new ImageIcon("resources/images/new.png"));
			newProblemMenu.setText("New");
			newProblemMenu.add(newObjectDiagramAction);			
		}
		return newProblemMenu;
	}

	/**
	 * This method initializes newDiagramMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getNewDomainMenu() {
		if (newDomainMenu == null) {
			newDomainMenu = new JMenu();
			newDomainMenu.setIcon(new ImageIcon("resources/images/new.png"));
			newDomainMenu.setText("New");
			newDomainMenu.add(newProblemAction);			
		}
		return newDomainMenu;
	}
	
	/**
	 * This method initializes openAsDomainMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getOpenAsDomainMenu() {
		if (openAsDomainMenu == null) {
			openAsDomainMenu = new JMenu();
			openAsDomainMenu.setIcon(new ImageIcon("resources/images/new.png"));
			openAsDomainMenu.setText("Open As");
			openAsDomainMenu.add(openDomainAsPDDL);			
		}
		return openAsDomainMenu;
	}
	
	/**
	 * This method initializes newDiagramMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getOpenAsProblemMenu() {
		if (openAsProblemMenu == null) {
			openAsProblemMenu = new JMenu();
			openAsProblemMenu.setIcon(new ImageIcon("resources/images/new.png"));
			openAsProblemMenu.setText("Open As");
			openAsProblemMenu.add(openProblemAsPDDL);			
		}
		return openAsProblemMenu;
	}	
	
	/**
	 * This method initializes openAsPetriNetMenu	
	 * 	
	 * @return javax.swing.JMenu	
	 */
	private JMenu getOpenAsPetriNetMenu() {
		if (openAsPetriNetMenu == null) {
			openAsPetriNetMenu = new JMenu();
			openAsPetriNetMenu.setIcon(new ImageIcon("resources/images/new.png"));
			openAsPetriNetMenu.setText("Open As");
			openAsPetriNetMenu.add(openAsPetriNet);			
		}
		return openAsPetriNetMenu;
	}
	
	
	/**
	 * This method initializes treePopupMenu	
	 * 	
	 * @return javax.swing.JPopupMenu	
	 */
	private JPopupMenu getTreePopupMenu() {
		
		if (treePopupMenu == null) {
			treePopupMenu = new JPopupMenu();
		}
		else{
			treePopupMenu.removeAll();
			ItTreeNode selected = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			switch (selected.getLevel()) {
			case 1:{
				treePopupMenu.add(getNewDiagramMenu());
				treePopupMenu.addSeparator();
				treePopupMenu.add(closeProjectAction);
			}
			break;
			case 2:{
				if (selected.getData().getName().equals("domain")){	
					// domain menus
					treePopupMenu.add(getNewDomainMenu());
					treePopupMenu.add(getOpenAsDomainMenu());
					treePopupMenu.addSeparator();				
					treePopupMenu.add(deleteDomainAction);					
				}
				else if (selected.getData().getName().equals("stateMachineDiagram")){
					treePopupMenu.add(openDiagramAction);
					treePopupMenu.add(getOpenAsPetriNetMenu());
					treePopupMenu.addSeparator();				
					treePopupMenu.add(deleteDiagramAction);	
				}
				else{
					treePopupMenu.add(openDiagramAction);
					treePopupMenu.addSeparator();				
					treePopupMenu.add(deleteDiagramAction);	
	
				}
			}
			break;
			case 3:{
				if (selected.getData().getName().equals("problem")){					
					// problem menus
					treePopupMenu.add(getNewProblemMenu());	
					treePopupMenu.add(getOpenAsProblemMenu());	
					treePopupMenu.addSeparator();
					treePopupMenu.add(deleteProblemAction);	
				}
				else if(selected.getData().getName().equals("repositoryDiagram")){
					// repository diagram
					treePopupMenu.add(openDiagramAction);	
				}
			}
			break;	
			case 4:{
				if (selected.getData().getName().equals("objectDiagram")){
					treePopupMenu.add(openDiagramAction);
					treePopupMenu.addSeparator();
					treePopupMenu.add(deleteDiagramAction);					
				}
			}
			break;
			default:
				break;
			}
		}
		return treePopupMenu;
	}
	

	/**
	 * This method initializes propertiesPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private PropertiesTabbedPane getPropertiesPane() {
		if (propertiesPane == null) {
			propertiesPane = new PropertiesTabbedPane();
		}
		return propertiesPane;
	}

	/**
	 * @return Returns the copyPaste.
	 */
	public static ArrayList<Object> getCopyPaste() {
		return copyPaste;
	}

	/**
	 * @return Returns the copyPasteSenders.
	 */
	public static ArrayList<Object> getCopyPasteSenders() {
		return copyPasteSenders;
	}

	/**
	 * This method initializes mainTabbedPane	
	 * 	
	 * @return javax.swing.JTabbedPane	
	 */
	private JTabbedPane getMainTabbedPane() {
		if (mainTabbedPane == null) {
			mainTabbedPane = new JTabbedPane();
			mainTabbedPane.setTabPlacement(JTabbedPane.BOTTOM);			
			mainTabbedPane.addTab("UML", getUmlSplitPane());
			mainTabbedPane.addTab("Petri Net", getPetriSplitPane());
			mainTabbedPane.addTab("PDDL", getPddlSplitPane());			
			mainTabbedPane.addTab("Plan Sim", getPlanSimPane());
			mainTabbedPane.addChangeListener(new ChangeListener(){
		        public void stateChanged(ChangeEvent evt) {
		            switch(mainTabbedPane.getSelectedIndex()){		            
		            
		            case 1:{// petri net
		            	updatePetriNetPanels();
		            }
		            break;
		            
		            case 2:{// pddl
		            	updatePDDLPanels();
		            }
		            break;
		            
		            case 3:{// plan sim
		            	updatePlanSimTrees();
		            }
		            break;
		            
		            }
		        }
		    });

		}
		return mainTabbedPane;
	}

	/**
	 * This method initializes pddlSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getPddlSplitPane() {
		if (pddlSplitPane == null) {
			pddlSplitPane = new JSplitPane();
			pddlSplitPane.setContinuousLayout(true);
			pddlSplitPane.setOneTouchExpandable(true);
			pddlSplitPane.setDividerSize(8);
			pddlSplitPane.setRightComponent(getPddlTextSplitPane());
			pddlSplitPane.setLeftComponent(getPddlPanel());
		}
		return pddlSplitPane;
	}
	
	/**
	 * This method initializes pddlSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getPetriSplitPane() {
		if (petriSplitPane == null) {
			petriSplitPane = new JSplitPane();
			petriSplitPane.setContinuousLayout(true);
			petriSplitPane.setOneTouchExpandable(true);
			petriSplitPane.setDividerSize(8);
			petriSplitPane.setRightComponent(getPetriEditorSplitPane());
			petriSplitPane.setLeftComponent(getPetriPanel());
		}
		return petriSplitPane;
	}
	

	/**
	 * This method initializes planSimPane
	 * 
	 * @return the planSimPane
	 */
	private JPanel getPlanSimPane() {
		
		if(planSimPane == null){
			planSimPane = new JPanel(new BorderLayout());
			planSimPane.add(getPlanSimSplitPane(), BorderLayout.CENTER);
			
			planSimStatusBar = new JLabel("Status:");
			planSimStatusBar.setHorizontalAlignment(SwingConstants.RIGHT);
			planSimPane.add(planSimStatusBar, BorderLayout.SOUTH);
		}
		
		return planSimPane;
	}


	/**
	 * This method initializes planSimSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getPlanSimSplitPane() {
		if (planSimSplitPane == null) {
			planSimSplitPane = new JSplitPane();
			planSimSplitPane.setContinuousLayout(true);
			planSimSplitPane.setOneTouchExpandable(true);
			planSimSplitPane.setDividerSize(8);
			planSimSplitPane.setDividerLocation(screenSize.width/3);
			planSimSplitPane.setLeftComponent(getPlanDetailsSplitPane());
			planSimSplitPane.setRightComponent(getPlanInfoSplitPane());			
		}
		return planSimSplitPane;
	}
	
	/**
	 * This method initializes planDetailsSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getPlanDetailsSplitPane() {
		if (planDetailsSplitPane == null) {
			planDetailsSplitPane = new JSplitPane();
			planDetailsSplitPane.setContinuousLayout(true);		
			planDetailsSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			planDetailsSplitPane.setDividerLocation(screenSize.height/3);
			planDetailsSplitPane.setDividerSize(8);
			planDetailsSplitPane.setPreferredSize(new Dimension(screenSize.width/4-20, screenSize.height/2 - 50));
			planDetailsSplitPane.setTopComponent(getPlanTreeFramePanel());
			planDetailsSplitPane.setBottomComponent(getPlanListFramePanel());
		}
		return planDetailsSplitPane;
	}
	
	/**
	 * This method initializes planSimSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getPlanVisualizationPane() {
		if (planVisualizationPane == null) {
			planVisualizationPane = new JSplitPane();
			planVisualizationPane.setContinuousLayout(true);
			planVisualizationPane.setOneTouchExpandable(true);
			planVisualizationPane.setDividerSize(8);
			planVisualizationPane.setDividerLocation(screenSize.width);
			planVisualizationPane.setResizeWeight(1);
			planVisualizationPane.setLeftComponent(getPlanAnalysisFramePanel());
			
			JPanel planVisualizationMainPane = new JPanel(new BorderLayout());
			
			//tool bar
			//replanButton = new JButton(replanAction);
			//replanButton.setEnabled(false);
						
			
                        planNavigationMenuBar = new JMenuBar();                        
                        
			/*JToolBar planNavigationToolBar = new JToolBar();
			planNavigationToolBar.add(replanButton);
                        planNavigationToolBar.setRollover(true);*/
			
                        replanMenu = new JMenu();
                        replanMenu.setText("Replanning");
                        replanMenu.setEnabled(false);
                        planNavigationMenuBar.add(replanMenu);
                        
                        JMenuItem replanMenuItem = new JMenuItem(replanAction);
                        //replanMenuItem.setEnabled(false);
                        replanMenu.add(replanMenuItem);
                                               
                        JMenuItem resourcesAvailabilityMenuItem = new JMenuItem(resourcesAvailabilityAction);
                        
			planVisualizationMainPane.add(planNavigationMenuBar, BorderLayout.NORTH);			
			planVisualizationMainPane.add(new JScrollPane(PlanNavigationList.getInstance()), BorderLayout.CENTER);						
			ItFramePanel planNavigationPanel = new ItFramePanel(":: Plan Navigation", 
					ItFramePanel.NO_MINIMIZE_MAXIMIZE);
			planNavigationPanel.setMinimumSize(new Dimension(150,50));
                        
			planNavigationPanel.setContent(planVisualizationMainPane, false);
			
			planVisualizationPane.setRightComponent(planNavigationPanel);				
		}
		return planVisualizationPane;
	}
	
	/**
	 * This method initializes planDetailsSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getPlanInfoSplitPane() {
		if (planInfoSplitPane == null) {
			planInfoSplitPane = new JSplitPane();
			planInfoSplitPane.setContinuousLayout(true);		
			planInfoSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			planInfoSplitPane.setDividerLocation(2*screenSize.height/3);
			planInfoSplitPane.setDividerSize(8);
			planInfoSplitPane.setPreferredSize(new Dimension(screenSize.width/4-20, screenSize.height/2 - 50));
			planInfoSplitPane.setTopComponent(getPlanVisualizationPane());
			planInfoSplitPane.setBottomComponent(getPlanInfoFramePanel());
		}
		return planInfoSplitPane;
	}
	
	/**
	 * @return the planTreeFramePanel
	 */
	private ItFramePanel getPlanTreeFramePanel() {
		if(planTreeFramePanel == null){
			planTreeFramePanel = new ItFramePanel(":: Problem Selection", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
			
			ItTreeNode root = new ItTreeNode("Projects");
			root.setIcon(new ImageIcon("resources/images/projects.png"));
			problemsPlanTreeModel = new DefaultTreeModel(root);	
			problemsPlanTree = new JTree(problemsPlanTreeModel);
			problemsPlanTree.setShowsRootHandles(true);
			problemsPlanTree.setCellRenderer(new ItTreeCellRenderer());
			
			problemsPlanTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			problemsPlanTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
				public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
					ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
					if(selectedNode != null && selectedNode.getLevel() == 3){
						
						planAnalysisFramePanel.setTitle(":: Plan Analysis - Problem: "+ selectedNode.getUserObject());
						solveProblemButton.setEnabled(true);
						setPlannerButton.setEnabled(true);
						addPlanActionButton.setEnabled(true);
						importPlanButton.setEnabled(true);
						planListModel.clear();
						xmlPlan = null;
						
						//fill the combo box with the existing available planners		
						plannersComboBox.removeAllItems();
						List<?> planners = itPlanners.getChild("planners").getChildren("planner");
						for (Iterator<?> iter = planners.iterator(); iter.hasNext();) {
							Element planner = (Element) iter.next();
                                                        //String plannerFile = planner.getChild("settings").getChildText("filePath");
                                                        //System.out.println(plannerFile);
                                                        //File f = new File(plannerFile);
                                                        //if (f.exists()) {
                                                            plannersComboBox.addItem(planner.getChildText("name") + " - Version: " + planner.getChildText("version"));
                                                        //}
						}
						
						CheckBoxNode variablesPlanTreeRoot = (CheckBoxNode)variablesPlanTreeModel.getRoot();
						
						// delete old tree nodes
						if(variablesPlanTreeRoot.getChildCount() > 0){
							variablesPlanTreeRoot = new CheckBoxNode("Objects");
							variablesPlanTreeRoot.setIcon(new ImageIcon("resources/images/projects.png"));
							variablesPlanTreeModel.setRoot(variablesPlanTreeRoot);
							variablesPlanTreeModel.reload();
						}
						
						// build the variables tree
						Element problem = selectedNode.getData();
											//planningProblems			domain
						List<?> objects = problem.getParentElement().getParentElement()
							.getChild("elements").getChild("objects").getChildren("object");
						for (Iterator<?> iter = objects.iterator(); iter.hasNext();) {
							Element object = (Element) iter.next();							
							
							CheckBoxNode objectNode = new CheckBoxNode(object.getChildText("name"), object, null, null);
							objectNode.setIcon(new ImageIcon("resources/images/object.png"));						
							
							//CheckBoxNode statesNode = new CheckBoxNode("States");
							//statesNode.setIcon(new ImageIcon("resources/images/state.png"));
							//variablesPlanTreeModel.insertNodeInto(statesNode, objectNode, objectNode.getChildCount());
							
							
							// add a node for each object attribute
							// get the object class
							Element objectClass = null;
							try {
								XPath path = new JDOMXPath("project/elements/classes/class[@id='"+ object.getChildText("class") +"']");			
								objectClass = (Element)path.selectSingleNode(object.getDocument());								
								
							} catch (JaxenException e1) {			
								e1.printStackTrace();
							}
							// get the parent classes
							List<?> parents = XMLUtilities.getClassAscendents(objectClass);
							
							// prepares a list of attributes node							
							List<CheckBoxNode> attributes = new ArrayList<CheckBoxNode>();
							for (Iterator<?> iterator = objectClass.getChild("attributes").getChildren("attribute").iterator();
									iterator.hasNext();) {
								Element attribute = (Element) iterator.next();
								
								if(attribute.getChild("parameters").getChildren().size() == 0){// not parameterized attributes
									CheckBoxNode attributeNode = new CheckBoxNode(attribute.getChildText("name"), attribute, null, null);
									attributeNode.setIcon(new ImageIcon("resources/images/attribute.png"));
									attributes.add(attributeNode);
									//variablesPlanTreeModel.insertNodeInto(attributeNode, attributesNode, attributesNode.getChildCount());
								}
							}
							
							for (Iterator<?> iterator = parents.iterator(); iterator.hasNext();) {
								Element parentClass = (Element) iterator.next();
								for (Iterator<?> iter2 = parentClass.getChild("attributes").getChildren("attribute").iterator();
										iter2.hasNext();) {
									Element attribute = (Element) iter2.next();
									
									if(attribute.getChild("parameters").getChildren().size() == 0){// not parameterized attributes									
										CheckBoxNode attributeNode = new CheckBoxNode(attribute.getChildText("name"), attribute, null, null);
										attributeNode.setIcon(new ImageIcon("resources/images/attribute.png"));
										attributes.add(attributeNode);
										//variablesPlanTreeModel.insertNodeInto(attributeNode, attributesNode, attributesNode.getChildCount());
									}

								}
								
							}							
							
							
							// only add attributes node if the object has attributes
							if(attributes.size() > 0){
								CheckBoxNode attributesNode = new CheckBoxNode("Attributes");
								attributesNode.setIcon(new ImageIcon("resources/images/attribute.png"));
								variablesPlanTreeModel.insertNodeInto(attributesNode, 
										objectNode, objectNode.getChildCount());
								
								for (Iterator<CheckBoxNode> iterator = attributes.iterator(); iterator
										.hasNext();) {
									CheckBoxNode attributeNode = iterator.next();
									variablesPlanTreeModel.insertNodeInto(attributeNode, attributesNode, attributesNode.getChildCount());
								}
							}
							
							// if the object node is not a leaf, add it to the tree
							if(!objectNode.isLeaf()){
								variablesPlanTreeModel.insertNodeInto(objectNode, variablesPlanTreeRoot, variablesPlanTreeRoot.getChildCount());
							}
							
						}
						variablesPlanTree.expandRow(0);                                                
                                                
						
					}
					else{
						//clear the depending areas
						planAnalysisFramePanel.setTitle(":: Plan Analysis");
						solveProblemButton.setEnabled(false);
						setPlannerButton.setEnabled(false);
						addPlanActionButton.setEnabled(false);
						importPlanButton.setEnabled(false);
						planListModel.clear();
						xmlPlan = null;
						plannersComboBox.removeAllItems();
						
						//clear the variables tree, whether necessary
						CheckBoxNode variablesPlanTreeRoot = (CheckBoxNode)variablesPlanTreeModel.getRoot();
						if(variablesPlanTreeRoot.getChildCount() > 0){
							CheckBoxNode root = new CheckBoxNode("Objects");
							root.setIcon(new ImageIcon("resources/images/projects.png"));
							variablesPlanTreeModel.setRoot(root);
							variablesPlanTreeModel.reload();							
						}
					}
				}
			});
			CheckBoxNode variablesTreeRoot = new CheckBoxNode("Objects");
			variablesTreeRoot.setIcon(new ImageIcon("resources/images/projects.png"));
			variablesPlanTreeModel = new DefaultTreeModel(variablesTreeRoot);			
			variablesPlanTree = new JTree(variablesPlanTreeModel);
			variablesPlanTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			variablesPlanTree.setShowsRootHandles(true);
			variablesPlanTree.setCellRenderer(new CheckBoxNodeRenderer());
			variablesPlanTree.setCellEditor(new CheckBoxNodeEditor(variablesPlanTree));
			variablesPlanTree.setEditable(true);
			
			ItTreeNode selectionTreeRoot = new ItTreeNode("Selections");		
			selectionTreeRoot.setIcon(new ImageIcon("resources/images/projects.png"));
			selectedVariablesPlanTreeModel = new DefaultTreeModel(selectionTreeRoot);
			selectedVariablesPlanTree = new JTree(selectedVariablesPlanTreeModel);
			selectedVariablesPlanTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			// get the selected nodes in variables plan tree and add it to selectedVariablesPlanTree			
			variablesPlanTree.addMouseListener(new MouseListener(){

				public void mouseClicked(MouseEvent e) {
					TreePath path = variablesPlanTree.getPathForLocation(e.getX(), e.getY());
					
					if(path != null){
						ItTreeNode root = new ItTreeNode("Selections");		
						root.setIcon(new ImageIcon("resources/images/projects.png"));
						
						// get checked nodes
						Object[] checked = CheckBoxNode.getCheckedNodes(
								(CheckBoxNode)variablesPlanTree.getModel().getRoot());
						
						for (int i = 0; i < checked.length; i++) {							
							CheckBoxNode node = (CheckBoxNode)checked[i];
							
							if(node.getUserObject().toString().equals("States") || node.getLevel() == 3){
								CheckBoxNode objectNode = (node.getLevel() == 3)
														?(CheckBoxNode)node.getParent().getParent()
														:(CheckBoxNode)node.getParent();
								String nodeName = (node.getLevel() == 3)
													?objectNode.getData().getChildText("name")+ "." +node.getData().getChildText("name")
													:"States of "+ objectNode.getData().getChildText("name");
								ImageIcon icon = new ImageIcon((node.getLevel() == 3)
																?"resources/images/attribute.png"
																:"resources/images/state.png");
								
								ItTreeNode newNode = new ItTreeNode(nodeName, node.getData(), null, null);
								newNode.setIcon(icon);
								root.add(newNode);
							}
						}
		        		
						selectedVariablesPlanTreeModel.setRoot(root);
			        	selectedVariablesPlanTreeModel.reload();		        
		        		selectedVariablesPlanTree.expandRow(0);
					}
				}

				public void mouseEntered(MouseEvent arg0) {}
				public void mouseExited(MouseEvent arg0) {}
				public void mousePressed(MouseEvent arg0) {}
				public void mouseReleased(MouseEvent arg0) {}				
			});
			selectedVariablesPlanTree.setShowsRootHandles(true);
			selectedVariablesPlanTree.setCellRenderer(new ItTreeCellRenderer());
			
			// create a main pane
			JPanel mainTreePanel = new JPanel(new BorderLayout());
			
			// tabbed panes with jtrees
			planTreeTabbedPane = new JTabbedPane();
			planTreeTabbedPane.addTab("Problems", new JScrollPane(problemsPlanTree));
			planTreeTabbedPane.addTab("Variables", new JScrollPane(variablesPlanTree));
			planTreeTabbedPane.addTab("Selected", new JScrollPane(selectedVariablesPlanTree));
			mainTreePanel.add(planTreeTabbedPane, BorderLayout.CENTER);
			
			// tool panel
			JPanel toolsPanel = new JPanel();
			// combobox with planners
			plannersComboBox = new JComboBox();
			
			// add to the panel
			toolsPanel.add(plannersComboBox);
			
			// solve problem button
			solveProblemButton = new JButton("Solve");
			solveProblemButton.setEnabled(false);
			solveProblemButton.setActionCommand("solve");
			solveProblemButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent e) {
					
					if(solveProblemButton.getActionCommand().equals("solve")){						
						ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
						
						if(selectedNode != null){
							Element problem = selectedNode.getData();							
							if(problem != null){
								// clear plan list and plan info pane					
								setPlanList(null);
								setPlanInfoPanelText("");
								
								String pddlVersion = pddlButtonsGroup.getSelection().getActionCommand();
								
								// generate PDDL domain
								Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(problem.getDocument().getRootElement(),
										pddlVersion, null);
								String pddlDomain = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "");
								
								// generate PDDL problem
								Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, pddlVersion);
								String pddlProblem = XPDDLToPDDL.parseXPDDLToPDDL(xpddlProblem, "");
								
								// save in auxiliary files
								File domainFile = new File("resources/planners/domain.pddl");
								File problemFile = new File("resources/planners/problem.pddl");

								try {
					                FileWriter domainWriter = new FileWriter(domainFile);
					                domainWriter.write(pddlDomain);
					                domainWriter.close();
									
					                FileWriter problemWriter = new FileWriter(problemFile);
									problemWriter.write(pddlProblem);
									problemWriter.close();
								} catch (IOException e1) {
									e1.printStackTrace();
								}
								
								// execute planner					
								
								List<?> planners = itPlanners.getChild("planners").getChildren("planner");
								Element chosenPlanner = (Element)planners.get(plannersComboBox.getSelectedIndex());

								exe = new ExecPlanner(chosenPlanner, 
										domainFile.getPath(), problemFile.getPath(), false);
								
								exe.setXMLDomain(problem.getParentElement().getParentElement());
								exe.setXMLProblem(problem);								

								currentThread = new Thread(exe);						
								currentThread.start();							
								
								// changes the button action command
								solveProblemButton.setActionCommand("stop");
								solveProblemButton.setText("Stop");
							}
						}
					}
					else{
						if(currentThread.isAlive()){
							exe.destroyProcess();
							try {
								// waits for the thread to return
								currentThread.join(2000);// 2 seconds time-out
							} catch (InterruptedException e1) {								
								e1.printStackTrace();
							}
							if(currentThread.isAlive()){
								currentThread.interrupt();
							}
							
							planSimStatusBar.setText("Status: Planning process stopped.");
							
							// changes the button action command
							solveProblemButton.setActionCommand("solve");
							solveProblemButton.setText("Solve");
						}
					}					
				}
				
			});
			// add to the panel
			toolsPanel.add(solveProblemButton);
			
			// set planner button
			//setPlannerButton = new JButton("Settings");
                        setPlannerButton = new JButton(new ImageIcon("resources/images/edit.png"));
			setPlannerButton.setEnabled(false);
                        setPlannerButton.setToolTipText("Set planner parameters");
			setPlannerButton.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent arg0) {
					
					//get selected planner
					/*List<?> planners = 
						itPlanners.getChild("planners").getChildren("planner");
					Element chosenPlanner = 
						(Element)planners.get(plannersComboBox.getSelectedIndex());*/
					
					PlannersSettingsDialog dialog =	new PlannersSettingsDialog(
							ItSIMPLE.this, plannersComboBox.getSelectedIndex());
					
					dialog.setVisible(true);					
				}
				
			});
			
			toolsPanel.add(setPlannerButton);;
			
			// add the tools panel to the main panel
			mainTreePanel.add(toolsPanel, BorderLayout.SOUTH);
			
			planTreeFramePanel.setContent(mainTreePanel, false);
		}
		
		return planTreeFramePanel;
	}
	
	public void solveReplaningProblem(Element project, Element problem){		
		if(problem != null){
			// clear plan list and plan info pane					
			setPlanList(null);
			setPlanInfoPanelText("");
			
			String pddlVersion = pddlButtonsGroup.getSelection().getActionCommand();
			
			// generate PDDL domain							// root element
			Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(project, pddlVersion, null);
			String pddlDomain = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "");
			
			// generate PDDL problem
			Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, pddlVersion);
			String pddlProblem = XPDDLToPDDL.parseXPDDLToPDDL(xpddlProblem, "");
			
			// save in auxiliary files
			File domainFile = new File("resources/planners/domain.pddl");
			File problemFile = new File("resources/planners/problem.pddl");

			try {
                FileWriter domainWriter = new FileWriter(domainFile);
                domainWriter.write(pddlDomain);
                domainWriter.close();
				
                FileWriter problemWriter = new FileWriter(problemFile);
				problemWriter.write(pddlProblem);
				problemWriter.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			
			// execute planner					
			
			List<?> planners = itPlanners.getChild("planners").getChildren("planner");
			Element chosenPlanner = (Element)planners.get(plannersComboBox.getSelectedIndex());

			exe = new ExecPlanner(chosenPlanner, 
					domainFile.getPath(), problemFile.getPath(), true);
			
			exe.setXMLDomain(problem.getParentElement().getParentElement());
			exe.setXMLProblem(problem);

			currentThread = new Thread(exe);						
			currentThread.start();							
			
			// changes the button action command
			//solveProblemButton.setActionCommand("stop");
			//solveProblemButton.setText("Stop");
		}
	}
	
	/**
	 * Sets the solveProblemButton action command to "solve".
	 * Has to be called when a plan is succesfully solved.
	 */
	public void setSolveProblemButton(){
		solveProblemButton.setActionCommand("solve");
		solveProblemButton.setText("Solve");
	}
	
	public Element getProblemsPlanTreeSelectedProblem(){
		Element problem = ((ItTreeNode)problemsPlanTree.getLastSelectedPathComponent()).getData();
		return problem;
	}


	/**
	 * @return the planListFramePanel
	 */
	private ItFramePanel getPlanListFramePanel() {
		if(planListFramePanel == null){
			planListFramePanel = new ItFramePanel(":: Plan", ItFramePanel.MINIMIZE_MAXIMIZE);
			planListFramePanel.setParentSplitPane(planDetailsSplitPane);
			
			planListModel = new DefaultListModel();
			planList = new JList(planListModel);
			planList.getSelectionModel().addListSelectionListener(new ListSelectionListener(){
				public void valueChanged(ListSelectionEvent e){
					// this is done to enable or disable the remove plan action button
					if(planList.getSelectedIndex() < 0){
						removePlanActionButton.setEnabled(false);
						editPlanActionButton.setEnabled(false);
					}
					else{
						removePlanActionButton.setEnabled(true);
						editPlanActionButton.setEnabled(true);
					}
				}
			});
			
			planList.addMouseListener(new MouseListener(){
				public void mouseClicked(MouseEvent e) {
					if(planList.getSelectedIndex() > 0 && e.getClickCount() == 2){
						editPlanAction.actionPerformed(null);
					}					
				}
				public void mouseEntered(MouseEvent arg0) {}
				public void mouseExited(MouseEvent arg0) {}
				public void mousePressed(MouseEvent arg0) {}
				public void mouseReleased(MouseEvent arg0) {}
				
			});
                        

			// initialize the buttons
			addPlanActionButton = new JButton(addPlanAction);
                        addPlanActionButton.setToolTipText("Add an action to the plan");
			removePlanActionButton = new JButton(removePlanAction);
                        removePlanActionButton.setToolTipText("Remove selected action");
			editPlanActionButton = new JButton(editPlanAction);
                        editPlanActionButton.setToolTipText("Edit selected action");
			importPlanButton = new JButton(importPlanAction);
                        importPlanButton.setToolTipText("Import plan");
			exportPlanButton = new JButton(exportPlanAction);
                        exportPlanButton.setToolTipText("Export current plan");
			addPlanActionButton.setEnabled(false);
			removePlanActionButton.setEnabled(false);
			editPlanActionButton.setEnabled(false);
			importPlanButton.setEnabled(false);
			exportPlanButton.setEnabled(false);
			JToolBar planListToolBar = new JToolBar();
			planListToolBar.add(addPlanActionButton);
			planListToolBar.add(removePlanActionButton);
			planListToolBar.add(editPlanActionButton);
			planListToolBar.add(importPlanButton);
			planListToolBar.add(exportPlanButton);
			
						
			JScrollPane listScrollPane = new JScrollPane(planList);			
			
			JPanel listPanel = new JPanel(new BorderLayout());
			listPanel.add(listScrollPane, BorderLayout.CENTER);
			listPanel.add(planListToolBar, BorderLayout.SOUTH);
			
			planListFramePanel.setContent(listPanel, false);
			
		}
		
		
		return planListFramePanel;
	}


	/**
	 * This method sets the plan list in the plan simulation perspective
	 * @param xmlPlan the XML structure of the plan
	 */
	public void setPlanList(Element xmlPlan) {
		this.xmlPlan = xmlPlan;
		planListModel.clear();		
		if (xmlPlan != null) {			
			exportPlanButton.setEnabled(true);
			
			if (xmlPlan != null) {

				Element planNode = xmlPlan.getChild("plan");
				List<?> actions = planNode.getChildren("action");
				for (Iterator<?> iter = actions.iterator(); iter.hasNext();) {
					Element action = (Element) iter.next();

					// start time
					String line = action.getChildText("startTime") + ": ";

					// action name
					line += "(" + action.getAttributeValue("id") + " ";

					// action parameters
					List<?> parameters = action.getChild("parameters")
							.getChildren("parameter");
					for (Iterator<?> iterator = parameters.iterator(); iterator
							.hasNext();) {
						Element parameter = (Element) iterator.next();
						line += parameter.getAttributeValue("id");
						if (iterator.hasNext()) {
							line += " ";
						}
					}
					line += ")";

					// action duration
					String duration = action.getChildText("duration");
					if (!duration.equals("")) {
						line += " [" + duration + "]";
					}

					// add the line to the list
					planListModel.addElement(line);
				}
			} else {
				// do nothing, set the button disabled
				exportPlanButton.setEnabled(false);
			}
		}		

	}




	/**
	 * @return Returns the planAnalysisFramePanel.
	 */
	private ItFramePanel getPlanAnalysisFramePanel(){
		if (planAnalysisFramePanel == null) {
			planAnalysisFramePanel = new ItFramePanel(":: Plan Analysis", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
			
			// tool bar
			JToolBar chartsToolBar = new JToolBar();
			chartsToolBar.add(new JButton(drawChartAction));
			
			// charts panel
			chartsPanel = new JPanel();
			chartsPanel.setLayout(new BoxLayout(chartsPanel, BoxLayout.Y_AXIS));
			
			// main charts panel - used to locate the tool bar above the charts panel
			JPanel mainChartsPanel = new JPanel(new BorderLayout());
			mainChartsPanel.add(chartsToolBar, BorderLayout.NORTH);
			mainChartsPanel.add(new JScrollPane(chartsPanel), BorderLayout.CENTER);
			
			JTabbedPane planAnalysisTabbedPane = new JTabbedPane();
			planAnalysisTabbedPane.addTab("Variable Tracking", mainChartsPanel);
			planAnalysisTabbedPane.addTab("Movie Maker", getMovieMakerPanel());
			
			JPanel planAnalysisPanel = new JPanel(new BorderLayout());
			//planAnalysisPanel.add(chartsToolBar, BorderLayout.NORTH);
			planAnalysisPanel.add(planAnalysisTabbedPane, BorderLayout.CENTER);
			
			
			planAnalysisFramePanel.setContent(planAnalysisPanel, false);
			
		}
		
		return planAnalysisFramePanel;
	}
	
	/**
	 * This method initializes movieMakerPanel
	 * 
	 * @return javax.swing.JPanel
	 */
	private JPanel getMovieMakerPanel(){
		
		if(movieMakerPanel == null){
			movieMakerPanel = new JPanel(new BorderLayout());
			movieMakerPanel.add(getMovieMakerToolBar(), BorderLayout.NORTH);
			movieMakerPanel.add(getMovieMakerSplitPane(), BorderLayout.CENTER);
		}
		
		return movieMakerPanel;
	}
	
	
	
	/**
	 * This method initializes movieMakerSplitPane
	 * 
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getMovieMakerSplitPane(){
		
		if(movieMakerSplitPane == null){
			movieMakerSplitPane = new JSplitPane();
			movieMakerSplitPane.setContinuousLayout(true);
			movieMakerSplitPane.setOneTouchExpandable(true);
			movieMakerSplitPane.setDividerLocation(screenSize.width/4);
			movieMakerSplitPane.setDividerSize(8);			

			movieMakerSplitPane.setLeftComponent(new JPanel(new BorderLayout()));
			movieMakerSplitPane.setRightComponent(new JPanel(new BorderLayout()));
		}
		
		return movieMakerSplitPane;
	}
	
	/**
	 * This method initializes planInfoFramePanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private ItFramePanel getPlanInfoFramePanel() {
		if (planInfoFramePanel == null) {
			JPanel planInfoPanel = new JPanel(new BorderLayout());
			planInfoPanel.setMinimumSize(new Dimension(100, 20));			
			planInfoFramePanel = new ItFramePanel(":: Plan Information", ItFramePanel.MINIMIZE_MAXIMIZE);
			//informationPanel.setMinimumSize(new Dimension(100,25));
			planInfoEditorPane = new JEditorPane();
			planInfoEditorPane.setContentType("text/html");
			planInfoEditorPane.setEditable(false);
			planInfoEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			planInfoFramePanel.setContent(planInfoEditorPane, true);
			planInfoFramePanel.setParentSplitPane(planInfoSplitPane);			
			planInfoPanel.add(planInfoFramePanel, BorderLayout.CENTER);					
		}
		return planInfoFramePanel;
	}
	
	public void setPlanInfoPanelText(String text){
		planInfoEditorPane.setText(text);
	}
	
	
	// sets the problems in the list
	public void setProblemList(Element domain){
		if (domain != null){
			
			problemPddlTaskPane.removeAll();
			Iterator<?> problems = domain.getChild("planningProblems").getChildren("problem").iterator();
			while (problems.hasNext()){
				
				Element problem = (Element)problems.next();
				Action action = new AbstractAction(problem.getChildText("name")) {
                    /**
					 * 
					 */
					private static final long serialVersionUID = -131526563961355654L;

					public void actionPerformed(ActionEvent e) {
                    	Element problemElement = (Element)this.getValue("data");
                    	//Element domainElement = (Element)this.getValue("domain");
						if (problemElement != null){
							String details = "<html><font size='-1' face='Arial'><b>"+problemElement.getChildText("name")+
							"</b><br>Problem<br>";
						if (problemElement.getChildText("description").trim().equals("")){
							details = details +"<br>No description...</font></html>";
						}
						else{
							details = details + "<br>" + problemElement.getChildText("description") + "</font></html>";
						}
						detailsTextPane.setText(details);

						Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problemElement, pddlButtonsGroup.getSelection().getActionCommand());
						String problemText = XPDDLToPDDL.parseXPDDLToPDDL(xpddlProblem, "  ");
						problemPddlTextPane.setText(problemText);
						}
                    }
                };
                action.putValue(Action.SMALL_ICON, new ImageIcon("resources/images/planningProblem.png"));
                //action.putValue(Action.SHORT_DESCRIPTION, problem.getChild("description"));
                action.putValue("data", problem);
                action.putValue("domain", domain);
    			problemPddlTaskPane.add(action);
			}
			//Refresh pane
			problemPddlTaskPane.setVisible(false);
			problemPddlTaskPane.setVisible(true);

		}
	}
	
//	 sets the stateMachines in the list
	public void setStateMachineList(Element project){
		if (project != null){		
			stateMachinesList.clear();
			DefaultListModel model = (DefaultListModel)stateMachineJList.getModel();
			model.removeAllElements();			
			List<?> stateMachines = project.getChild("diagrams").getChild("stateMachineDiagrams")
				.getChildren("stateMachineDiagram");
									
			Iterator<?> stateMachinesIter = stateMachines.iterator();
			while (stateMachinesIter.hasNext()){				
				Element stateMachine = (Element)stateMachinesIter.next();					
				model.addElement(stateMachine.getChildText("name"));
				stateMachinesList.add((Element)stateMachine.clone());
			}
		}
	}

	/**
	 * This method initializes pddlTextSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getPddlTextSplitPane() {
		if (pddlTextSplitPane == null) {
			pddlTextSplitPane = new JSplitPane();
			pddlTextSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			
			//Problem Panel
			ItFramePanel problemPanel = new ItFramePanel(":: Problem", ItFramePanel.MINIMIZE_MAXIMIZE);
			problemPanel.setContent(getBottomPddlPanel(), false);
			problemPanel.setParentSplitPane(pddlTextSplitPane);
			pddlTextSplitPane.setBottomComponent(problemPanel);
			
			//Doamin Panel
			ItFramePanel domainPanel = new ItFramePanel(":: Domain", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
			domainPanel.setContent(getTopPddlPanel(), false);
			//domainPanel.setParentSplitPane(pddlTextSplitPane);	
			pddlTextSplitPane.setTopComponent(domainPanel);			

			pddlTextSplitPane.setDividerSize(3);
			pddlTextSplitPane.setContinuousLayout(true);
			pddlTextSplitPane.setDividerLocation((int)(screenSize.height*0.45));
			pddlTextSplitPane.setResizeWeight(0.5);
		}
		return pddlTextSplitPane;
	}
	
	/**
	 * This method initializes petriEditorSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getPetriEditorSplitPane() {
		if (petriEditorSplitPane == null) {
			petriEditorSplitPane = new JSplitPane();
			petriEditorSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
			
			//Analysis Results Panel
			ItFramePanel analysisPanel = new ItFramePanel(":: Analysis Results", ItFramePanel.MINIMIZE_MAXIMIZE);
			analysisPanel.setContent(getBottomPetriPanel(), false);
			analysisPanel.setParentSplitPane(petriEditorSplitPane);
			petriEditorSplitPane.setBottomComponent(analysisPanel);
			petriInfoEditorPane = new JEditorPane();
			petriInfoEditorPane.setContentType("text/html");
			petriInfoEditorPane.setEditable(false);
			petriInfoEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			analysisPanel.setContent(petriInfoEditorPane, true);
			
			
			//Editor Panel
			ItFramePanel editorPanel = new ItFramePanel(":: Petri Net", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
			editorPanel.setContent(getTopPetriPane(), false);
			
			petriDiagramGraph.setInfoPane(petriInfoEditorPane);
			//domainPanel.setParentSplitPane(pddlTextSplitPane);	
			petriEditorSplitPane.setTopComponent(editorPanel);			

			petriEditorSplitPane.setDividerSize(3);
			petriEditorSplitPane.setContinuousLayout(true);
			petriEditorSplitPane.setDividerLocation((int)(screenSize.height*0.45));
			petriEditorSplitPane.setResizeWeight(0.5);
		}
		return petriEditorSplitPane;
	}
	
	/**
	 * This method initializes topPddlPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getTopPddlPanel() {
		if (topPddlPanel == null) {
			topPddlPanel = new JPanel(new BorderLayout());
			topPddlPanel.add(getTopPddlScrollPane(), BorderLayout.CENTER);
			topPddlPanel.setPreferredSize(new Dimension(screenSize.width/4-20, screenSize.height/2 - 60));
			
			domainPddlToolBar = new JToolBar();
			domainPddlToolBar.setRollover(true);
			JButton save = new JButton(saveDomainToFile);
			domainPddlToolBar.add(save);
			
			topPddlPanel.add(domainPddlToolBar, BorderLayout.NORTH);
		}
		return topPddlPanel;
	}

	/**
	 * This method initializes topPetriPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JRootPane getTopPetriPane() {
		if (topPetriPane == null) {
			petriToolBar = new ItToolBar("","PetriNet");				
			petriToolBar.setName("PetriToolBar");
			GraphModel model = new DefaultGraphModel();
			GraphLayoutCache view = new GraphLayoutCache(model, new ItCellViewFactory());
			petriDiagramGraph = new ItGraph(view, petriToolBar, null, null, null, commonData, "PetriNet");
			petriToolBar.setGraph(petriDiagramGraph);
			petriDiagramGraph.setVisible(false);
			topPetriScrollPane = new JScrollPane(petriDiagramGraph);
			topPetriPane = new JRootPane();
			topPetriPane.setLayout(new BorderLayout());
			topPetriPane.add(petriToolBar, BorderLayout.NORTH);
			topPetriPane.add(topPetriScrollPane, BorderLayout.CENTER);
			//panel.setContentPane(graphScrollPane);
			petriDiagramGraph.setVisible(true);
		}
		return topPetriPane;
	}
	
	/**
	 * This method initializes bottomPddlPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBottomPddlPanel() {
		if (bottomPddlPanel == null) {
			bottomPddlPanel = new JPanel(new BorderLayout());
			bottomPddlPanel.add(getBottomPddlScrollPane(), BorderLayout.CENTER);
			
			problemPddlToolBar = new JToolBar();
			problemPddlToolBar.setRollover(true);
			JButton save = new JButton(saveProblemToFile);
			problemPddlToolBar.add(save);			
			bottomPddlPanel.add(problemPddlToolBar, BorderLayout.NORTH);
		}
		return bottomPddlPanel;
	}
	
	/**
	 * This method initializes bottomPetriPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getBottomPetriPanel() {
		if (bottomPetriPanel == null) {
			bottomPetriPanel = new JPanel(new BorderLayout());
			bottomPetriPanel.add(getBottomPetriScrollPane(), BorderLayout.CENTER);
		}
		return bottomPetriPanel;
	}
	
	/**
	 * This method initializes topPddlScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getTopPddlScrollPane() {
		if (topPddlScrollPane == null) {
			topPddlScrollPane = new JScrollPane();
			topPddlScrollPane.setViewportView(getDomainPddlTextPane());
		}
		return topPddlScrollPane;
	}
	
	/**
	 * This method initializes bottomPddlScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getBottomPddlScrollPane() {
		if (bottomPddlScrollPane == null) {
			bottomPddlScrollPane = new JScrollPane();
			bottomPddlScrollPane.setViewportView(getProblemPddlTextPane());
		}
		return bottomPddlScrollPane;
	}

	/**
	 * This method initializes bottomPetriScrollPane	
	 * 	
	 * @return javax.swing.JScrollPane	
	 */
	private JScrollPane getBottomPetriScrollPane() {
		if (bottomPetriScrollPane == null) {
			bottomPetriScrollPane = new JScrollPane();
			bottomPetriScrollPane.setViewportView(getPetriEditorPane());
		}
		return bottomPetriScrollPane;
	}
	
	/**
	 * This method initializes domainPddlTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getDomainPddlTextPane() {
		if (domainPddlTextPane == null) {
			ItHilightedDocument domainDocument = new ItHilightedDocument();
			domainDocument.setHighlightStyle(ItHilightedDocument.PDDL_STYLE);
			domainPddlTextPane = new JTextPane(domainDocument);
			domainPddlTextPane.setFont(new Font("Courier", 0, 12));
			
		}
		return domainPddlTextPane;
	}

	/**
	 * This method initializes problemPddlTextPane	
	 * 	
	 * @return javax.swing.JTextPane	
	 */
	private JTextPane getProblemPddlTextPane() {
		if (problemPddlTextPane == null) {
			ItHilightedDocument problemDocument = new ItHilightedDocument();
			problemDocument.setHighlightStyle(ItHilightedDocument.PDDL_STYLE);
			problemPddlTextPane = new JTextPane(problemDocument);
			problemPddlTextPane.setFont(new Font("Courier", 0, 12));
		}
		return problemPddlTextPane;
	}

	/**
	 * This method initializes petriEditorPane	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JEditorPane getPetriEditorPane() {
		if (petriEditorPane == null) {
			petriEditorPane = new JEditorPane();
			petriEditorPane.setContentType("text/html");
			petriEditorPane.setEditable(false);
			petriEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));				
			}
			return petriEditorPane;
		}
	
/*	*//**
	 * This method initializes graphSplitPane	
	 * 	
	 * @return javax.swing.JSplitPane	
	 */
	private JSplitPane getGraphSplitPane() {
		if (graphSplitPane == null) {
			graphSplitPane = new JSplitPane();
			graphSplitPane.setContinuousLayout(true);
			//graphSplitPane.setOneTouchExpandable(true);
			graphSplitPane.setDividerSize(3);
			graphSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);			
			graphSplitPane.setBottomComponent(getInformationPanel());
			graphSplitPane.setTopComponent(getDiagramsSplitPane());						
			graphSplitPane.setResizeWeight(1.0);// fixes the botoom component's size						
			graphSplitPane.setDividerLocation((int)screenSize.height*3/4);
		}
		return graphSplitPane;
	}

	/**
	 * This method initializes informationPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getInformationPanel() {
		if (informationPanel == null) {
			informationPanel = new JPanel(new BorderLayout());
			informationPanel.setMinimumSize(new Dimension(100, 20));			
			infoPanel = new ItFramePanel(":: Information", ItFramePanel.MINIMIZE_MAXIMIZE);
			//informationPanel.setMinimumSize(new Dimension(100,25));
			infoEditorPane = new JEditorPane();
			infoEditorPane.setContentType("text/html");
			infoEditorPane.setEditable(false);
			infoEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
			infoPanel.setContent(infoEditorPane, true);
			infoPanel.setParentSplitPane(graphSplitPane);			
			informationPanel.add(infoPanel, BorderLayout.CENTER);					
		}
		return informationPanel;
	}

	/**
	 * This method initializes mainPanel	
	 * 	
	 * @return javax.swing.JPanel	
	 */
	private JPanel getMainPanel() {
		if (mainPanel == null) {
			mainPanel = new JPanel(new BorderLayout());
			mainPanel.add(getToolBar(), BorderLayout.NORTH);
			mainPanel.add(getMainTabbedPane(), BorderLayout.CENTER);
		}
		return mainPanel;
	}
	
	/**
	 * This method initializes toolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getToolBar() {
		if (toolBar == null) {
			toolBar = new JToolBar();
			toolBar.setRollover(true);
			toolBar.add(newProjectAction).setToolTipText("New Project");
			toolBar.add(openProjectAction).setToolTipText("Open Project");
			toolBar.add(saveAction).setToolTipText("Save Project");
			toolBar.add(saveAllAction).setToolTipText("Save All");
		}
		return toolBar;
	}	


	/**
	 * @return Returns the domainPddlContainer.
	 */
	private JXTaskPaneContainer getDomainPddlContainer() {
		if (domainPddlContainer == null) {
            domainPddlContainer = new JXTaskPaneContainer();
	        
			
			//domain
			domainPddlTaskPane = new JXTaskPane();
			domainPddlTaskPane.setTitle("Domain");
			domainPddlTaskPane.setIcon(new ImageIcon("resources/images/projects.png"));
			//domainPddlTaskPane.add(getPddlTree());
			domainPddlTaskPane.setSpecial(true);		
			
			//problems
			problemPddlTaskPane = new JXTaskPane();
			problemPddlTaskPane.setTitle("Problems");
			problemPddlTaskPane.setIcon(new ImageIcon("resources/images/planningProblem.png"));
			
			//details
			detailPddlTaskPane = new JXTaskPane();
			detailPddlTaskPane.setTitle("Details");
			detailPddlTaskPane.setIcon(new ImageIcon("resources/images/details.png"));
			
			detailPddlTaskPane.add(getDetailsTextPane());	
			detailsTextPane.setBackground(detailsTextPane.getParent().getBackground());
			
			// itSettings
			settingsPddlTaskPane = new JXTaskPane();
			settingsPddlTaskPane.setTitle("Settings");			
					
			JRadioButton pddl21 = new JRadioButton("PDDL 2.1");
			JRadioButton pddl22 = new JRadioButton("PDDL 2.2");
			JRadioButton pddl30 = new JRadioButton("PDDL 3.0", true);
			
			pddl21.setOpaque(false);
			pddl21.setActionCommand(ToXPDDL.PDDL_2_1);
			pddl22.setOpaque(false);
			pddl22.setActionCommand(ToXPDDL.PDDL_2_2);
			pddl30.setOpaque(false);
			pddl30.setActionCommand(ToXPDDL.PDDL_3_0);
						
			pddlButtonsGroup = new ButtonGroup();		
			pddlButtonsGroup.add(pddl21);
			pddlButtonsGroup.add(pddl22);
			pddlButtonsGroup.add(pddl30);
			pddlButtonsGroup.setSelected(pddl21.getModel(), true);
			
			JPanel settingsPanel = new JPanel();
			settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
			settingsPanel.setOpaque(false);		
			settingsPanel.add(pddl21);
			settingsPanel.add(pddl22);
			settingsPanel.add(pddl30);
			
			settingsPddlTaskPane.add(settingsPanel);
			
			domainPddlContainer.add(domainPddlTaskPane);
			domainPddlContainer.add(problemPddlTaskPane);
			domainPddlContainer.add(detailPddlTaskPane);
			domainPddlContainer.add(settingsPddlTaskPane);

		}
		
		return domainPddlContainer;
	}
	
	/**
	 * @return Returns the PetriContainer.
	 */
	private JXTaskPaneContainer getPetriContainer() {
		if (projectPetriContainer == null) {
			projectPetriContainer = new JXTaskPaneContainer();
	        
			
			//project
			projectPetriTaskPane = new JXTaskPane();
			projectPetriTaskPane.setTitle("Project");
			projectPetriTaskPane.setIcon(new ImageIcon("resources/images/projects.png"));
			projectPetriTaskPane.setSpecial(true);	
			
			//state chart diagrams
			stateMachinePetriTaskPane = new JXTaskPane();
			stateMachinePetriTaskPane.setTitle("State Chart Diagrams");
			stateMachinePetriTaskPane.setIcon(new ImageIcon("resources/images/planningProblem.png"));
			stateMachinePetriTaskPane.setLayout(new BorderLayout());
			stateMachinePetriPanel = new JPanel(new BorderLayout());			
			
			stateMachineJList = new JList(new DefaultListModel());
			ItListRenderer renderer = new ItListRenderer();			
			renderer.setIcon(new ImageIcon("resources/images/stateMachineDiagram.png"));
			stateMachineJList.setCellRenderer(renderer);
			
			stateMachinePetriPanel.add(stateMachineJList, BorderLayout.CENTER);
			JToolBar toolBar = new JToolBar();
			toolBar.setRollover(true);			
			stateMachineButton = new JButton(openPetriNetGroup);
			toolBar.add(stateMachineButton);
			
			stateMachinePetriTaskPane.add(stateMachinePetriPanel, BorderLayout.CENTER);
			stateMachinePetriTaskPane.add(toolBar, BorderLayout.SOUTH);
			/*petriCheckBoxList = new CheckBoxList();
			stateMachinePetriTaskPane.add(petriCheckBoxList, BorderLayout.CENTER);*/
			
			//details
			detailPetriTaskPane = new JXTaskPane();
			detailPetriTaskPane.setTitle("Details");
			detailPetriTaskPane.setIcon(new ImageIcon("resources/images/details.png"));
			
			detailPetriTaskPane.add(getPetriDetailsTextPane());	
			petriDetailsTextPane.setBackground(petriDetailsTextPane.getParent().getBackground());
			
			projectPetriContainer.add(projectPetriTaskPane);
			projectPetriContainer.add(stateMachinePetriTaskPane);
			projectPetriContainer.add(detailPetriTaskPane);

		}
		
		return projectPetriContainer;
	}
	
	/**
	 * @return Returns the pddlPanel.
	 */
	private JPanel getPddlPanel() {
		if (pddlPanel == null) {
			pddlPanel = new JPanel(new BorderLayout());
			pddlPanel.setPreferredSize(new Dimension(screenSize.width/4 - 20, screenSize.height));
			
			pddlPanel.add(getDomainPddlContainer(), BorderLayout.CENTER);
			//pddlPanel.add(getProblemPddlContainer(), BorderLayout.CENTER);
		}
		
		return pddlPanel;
	}

	/**
	 * @return Returns the petriPanel.
	 */
	private JPanel getPetriPanel() {
		if (petriPanel == null) {
			petriPanel = new JPanel(new BorderLayout());
			petriPanel.setPreferredSize(new Dimension(screenSize.width/4 - 20, screenSize.height));
			
			petriPanel.add(getPetriContainer(), BorderLayout.CENTER);
			//pddlPanel.add(getProblemPddlContainer(), BorderLayout.CENTER);
		}
		
		return petriPanel;
	}
	
	public JTextPane getDetailsTextPane() {
		if (detailsTextPane ==null){
			detailsTextPane = new JTextPane();
			detailsTextPane.setEditable(false);
			detailsTextPane.setContentType("text/html"); 
		}
		return detailsTextPane;
	}	
	
	public JTextPane getPetriDetailsTextPane() {
		if (petriDetailsTextPane ==null){
			petriDetailsTextPane = new JTextPane();
			petriDetailsTextPane.setEditable(false);
			petriDetailsTextPane.setContentType("text/html"); 
		}
		return petriDetailsTextPane;
	}
	
	/**
	 * This method initializes movieMakerToolBar	
	 * 	
	 * @return javax.swing.JToolBar	
	 */
	private JToolBar getMovieMakerToolBar() {

		JToolBar movieMakerToolBar = new JToolBar();
		movieMakerToolBar.setRollover(true);
		
		// create the buttons
		
		// generate movie
		JButton generateMovieButton = new JButton("Generate Movie",
				new ImageIcon("resources/images/new.png"));
		generateMovieButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				
				
				// the thread is created so the status bar can be refreshed
				new Thread(){
					public void run() {
						ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
						if(selectedNode != null && selectedNode.getLevel() == 3){
							Element problem = selectedNode.getData();				
							movie = PlanSimulator.getMovie(xmlPlan, problem);
							
							PlanNavigationList.getInstance().setList(xmlPlan, movie,
									problem.getParentElement().getParentElement(), 
									(JPanel)movieMakerSplitPane.getLeftComponent(),
									(JPanel)movieMakerSplitPane.getRightComponent());
							replanMenu.setEnabled(true);
						}
					}
				}.start();
			}
		});
		movieMakerToolBar.add(generateMovieButton);
		
		// back
		JButton backButton = new JButton("Backward", 
				new ImageIcon("resources/images/backward.png"));
		backButton.setToolTipText("Backward");
		backButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JList navigationList = PlanNavigationList.getInstance().getPlanList();
				final int selected = navigationList.getSelectedIndex();
				if(selected > 0){
					navigationList.setSelectedIndex(selected - 1);
				}
			}
			
		});
		movieMakerToolBar.add(backButton);
		
		// forward
		JButton forwardButton = new JButton("Forward", 
				new ImageIcon("resources/images/forward.png"));
		forwardButton.setToolTipText("Forward");
		forwardButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				JList navigationList = PlanNavigationList.getInstance().getPlanList();
				final int selected = navigationList.getSelectedIndex();
				if(selected > -1 && 
						selected < navigationList.getModel().getSize() - 1){
					navigationList.setSelectedIndex(selected + 1);
				}
			}
			
		});
		movieMakerToolBar.add(forwardButton);
		
		movieMakerToolBar.addSeparator();
		
		// zoom in
		JButton zoomInButton = new JButton("Zoom In",new ImageIcon("resources/images/zoomIN.png"));
		zoomInButton.setToolTipText("Zoom In");
		zoomInButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				
				try {
					ItGraph leftGraph = (ItGraph)((JScrollPane)((JPanel)movieMakerSplitPane.getLeftComponent())
						.getComponent(0)).getViewport().getView();
					leftGraph.setScale(leftGraph.getScale() * 1.25);
					
					ItGraph rightGraph = (ItGraph)((JScrollPane)((JPanel)movieMakerSplitPane.getRightComponent())
							.getComponent(0)).getViewport().getView();
					rightGraph.setScale(rightGraph.getScale() * 1.25);
				} catch (Exception e1) {}
				
				
			}
		});
		movieMakerToolBar.add(zoomInButton);
		
		// zoom out
		JButton zoomOutButton = new JButton("Zoom Out",new ImageIcon("resources/images/zoomOUT.png"));
		zoomOutButton.setToolTipText("Zoom Out");
		zoomOutButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				
				
				try {
					ItGraph leftGraph = (ItGraph)((JScrollPane)((JPanel)movieMakerSplitPane.getLeftComponent())
						.getComponent(0)).getViewport().getView();
					leftGraph.setScale(leftGraph.getScale() * 0.8);
					
					ItGraph rightGraph = (ItGraph)((JScrollPane)((JPanel)movieMakerSplitPane.getRightComponent())
							.getComponent(0)).getViewport().getView();
					rightGraph.setScale(rightGraph.getScale() * 0.8);
				} catch (Exception e1) {}
				
				
			}
		});
		movieMakerToolBar.add(zoomOutButton);
		
		// 1:1
		JButton oneToOneScaleButton = new JButton("1:1");
		oneToOneScaleButton.setToolTipText("1:1");
		oneToOneScaleButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				
				
				try {
					ItGraph leftGraph = (ItGraph)((JScrollPane)((JPanel)movieMakerSplitPane.getLeftComponent())
						.getComponent(0)).getViewport().getView();
					leftGraph.setScale(1.0);
					
					ItGraph rightGraph = (ItGraph)((JScrollPane)((JPanel)movieMakerSplitPane.getRightComponent())
							.getComponent(0)).getViewport().getView();
					rightGraph.setScale(1.0);
				} catch (Exception e1) {}
				
				
			}
		});
		movieMakerToolBar.add(oneToOneScaleButton);
		
		movieMakerToolBar.addSeparator();
		
                // edit state
		JButton editStateButton = new JButton("Edit");
		editStateButton.setToolTipText("Edit current state");
		editStateButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// take the current state				
				Element currentState = PlanNavigationList.getInstance().getCurrentState();
				
				ItTreeNode problemNode = (ItTreeNode) problemsPlanTree.getLastSelectedPathComponent();
				ItTreeNode domainNode = (ItTreeNode)problemNode.getParent();
				ItTreeNode projectNode = (ItTreeNode)domainNode.getParent();
				
				// the object diagram must have parent
				//add the current state to the domain
				Element currentStateClone = (Element)currentState.clone();
				problemNode.getData().getChild("objectDiagrams").addContent(currentStateClone);
				
				graphTabbedPane.openEditStateTab(currentStateClone, domainNode.getData(), projectNode.getData());
				
				mainTabbedPane.setSelectedIndex(0);
			}
		});
		movieMakerToolBar.add(editStateButton);
                
                // virtual reality
		JButton virtualRealityButton = new JButton("Virtual Reality");
		virtualRealityButton.setToolTipText("Generate virtual reality file");
		virtualRealityButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {				
				ItTreeNode problemNode = (ItTreeNode) problemsPlanTree.getLastSelectedPathComponent();
				//ItTreeNode domainNode = (ItTreeNode)problemNode.getParent();
				//ItTreeNode projectNode = (ItTreeNode)domainNode.getParent();
				
				VirtualRealityRobotNavigationDomain.generateBackgroundFile(problemNode.getData(), xmlPlan);
			}
		});
		movieMakerToolBar.add(virtualRealityButton);                
		
		return movieMakerToolBar;
	}
	
	public void setMainTabbedPaneSelectedIndex(int index){
		mainTabbedPane.setSelectedIndex(index);
	}
	
	public void closeEditStateTab(){
		graphTabbedPane.closeEditStateTab();
	}
	
	/**
	 * This method should be called every time a project is opened or closed,
	 * to update the items, like domains and problems, in the Petri Net view.
	 */
	private void updatePetriNetPanels(){
		projectPetriTaskPane.removeAll();
		stateMachineJList.removeAll();
    	detailsTextPane.setText("<html><font size='-1' face='Arial'>Select a project.<html>");
    	
		for (int i = 0; i<treeRoot.getChildCount(); i++){
			ItTreeNode currentNode = (ItTreeNode)treeRoot.getChildAt(i);
						
			Element project = currentNode.getData();
			Action action = new AbstractAction(project.getChildText("name")){
                /**
				 * 
				 */
				private static final long serialVersionUID = -5069133020063854135L;

				public void actionPerformed(ActionEvent e) {
                	Element projectElement = (Element)this.getValue("data");
                	selectedPetriNetProject = projectElement;
					if (projectElement != null){
						String details = "<html><font size='-1' face='Arial'><b>"+projectElement.getChildText("name");
						if (projectElement.getChildText("description").trim().equals("")){
							details = details +"<br>No description...</font></html>";
						}
						else{
							details = details + "<br>" + projectElement.getChildText("description") + "</font></html>";
						}
						detailsTextPane.setText(details);
						setStateMachineList(projectElement);							
					}
                }
            };
            action.putValue(Action.SMALL_ICON, new ImageIcon("resources/images/project.png"));
            //action.putValue(Action.SHORT_DESCRIPTION, project.getChild("description"));
            action.putValue("data", project);
            projectPetriTaskPane.add(action);
		}
	}
	
	/**
	 * This method should be called every time a project is opened or closed,
	 * to update the items, like domains and problems, in the PDDL view.
	 */
	private void updatePDDLPanels(){
		domainPddlTaskPane.removeAll();
    	problemPddlTaskPane.removeAll();
    	detailsTextPane.setText("<html><font size='-1' face='Arial'>Select a domain.<html>");
    	
		for (int i = 0; i<treeRoot.getChildCount(); i++){
			ItTreeNode currentNode = (ItTreeNode)treeRoot.getChildAt(i);
						
			Element project = currentNode.getData();

			for (Iterator<?> iter = project.getChild("diagrams").getChild("planningDomains").getChildren().iterator(); iter.hasNext();) {
				Element domain = (Element) iter.next();
				Action action = new AbstractAction(domain.getChildText("name") + " - " + project.getChildText("name")) {
                   
					/**
					 * 
					 */
					private static final long serialVersionUID = -6429119458268436748L;

					public void actionPerformed(ActionEvent e) {
                    	//System.out.println(this.getValue("data"));
                    	Element domainElement = (Element)this.getValue("data");
						if (domainElement != null){
							String details = "<html><font size='-1' face='Arial'><b>"+domainElement.getChildText("name")+
								"</b><br>Domain<br>";
							if (domainElement.getChildText("description").trim().equals("")){
								details = details +"<br>No description...</font></html>";
							}
							else{
								details = details + "<br>" + domainElement.getChildText("description") + "</font></html>";
							}
							detailsTextPane.setText(details);
							setProblemList(domainElement);										
							Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(
									domainElement.getParentElement().getParentElement().getParentElement(),
									pddlButtonsGroup.getSelection().getActionCommand(), null);
							String domainText = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "  ");
							
							domainPddlTextPane.setText(domainText);							
						}
                    }
                };
                action.putValue(Action.SMALL_ICON, new ImageIcon("resources/images/project.png"));
                //action.putValue(Action.SHORT_DESCRIPTION, project.getChild("description"));
                action.putValue("data", domain);
    			domainPddlTaskPane.add(action);
			}   				
			
		}
	}
	
	/**
	 * This method should be called every time the user changes
	 * the selected tab to the Plan Sim tab.
	 */
	private void updatePlanSimTrees(){
		//1. problems plan tree
		ItTreeNode problemsPlanTreeRoot = (
				ItTreeNode)problemsPlanTreeModel.getRoot();
		
		//1.1 for each project...
		for(int projectIndex = 0; projectIndex < problemsPlanTreeRoot.getChildCount(); projectIndex++){
			ItTreeNode project = 
				(ItTreeNode)problemsPlanTreeRoot.getChildAt(projectIndex);
			
			//1.2 update project name
			String projectName = project.getData().getChildText("name");
			if(!projectName.equals(project.getUserObject())){
				project.setUserObject(projectName);
				problemsPlanTreeModel.nodeChanged(project);
			}
			
			//1.3 for each domain in project...
			for(int domainIndex = 0; domainIndex < project.getChildCount(); domainIndex++){
				ItTreeNode domain = (ItTreeNode)project.getChildAt(domainIndex);
				
				// 1.4 update domain name
				String domainName = domain.getData().getChildText("name");
				if(!domainName.equals(domain.getUserObject())){
					domain.setUserObject(domainName);
					problemsPlanTreeModel.nodeChanged(domain);
				}
				
				//1.5 for each problem in domain...
				for(int problemIndex = 0; problemIndex < domain.getChildCount(); problemIndex++){
					ItTreeNode problem = (ItTreeNode)domain.getChildAt(problemIndex);
					
					//1.6 update problem name
					String problemName = problem.getData().getChildText("name");
					if(!problemName.equals(problem.getUserObject())){
						problem.setUserObject(problemName);
						problemsPlanTreeModel.nodeChanged(problem);
					}
				}
			}
		}
		
		//2. variables plan tree
		ItTreeNode variablesPlanTreeRoot = (ItTreeNode)variablesPlanTreeModel.getRoot();
		
		//2.1 for each object...
		for(int objectIndex = 0; objectIndex < variablesPlanTreeRoot.getChildCount(); objectIndex++){
			ItTreeNode objectNode = (ItTreeNode)variablesPlanTreeRoot.getChildAt(objectIndex);
			Element objectData = objectNode.getData();
			//2.2 check whether the object was already deleted (no parent)
			if(objectData.getParentElement() == null){
				// remove the node
				variablesPlanTreeModel.removeNodeFromParent(objectNode);
			}
			else{
				// 2.3 update the object node name
				String objectName = objectData.getChildText("name");
				if(!objectName.equals(objectNode.getUserObject().toString())){
					objectNode.setUserObject(objectName);
					variablesPlanTreeModel.nodeChanged(objectNode);
				}
			}
			
			//2.4 for each node in object...
			for(int childIndex = 0; 
				childIndex < objectNode.getChildCount(); childIndex++){
				
				ItTreeNode childNode = (ItTreeNode)objectNode.getChildAt(childIndex);
				
				if(childNode.getUserObject().toString().equals("Attributes")){
					//2.5 for each attribute in Attributes node...
					for(int attributeIndex = 0; 
						attributeIndex < childNode.getChildCount(); attributeIndex++){
						
						ItTreeNode attributeNode = (ItTreeNode)childNode.getChildAt(attributeIndex);
						Element attributeData = attributeNode.getData();
						//2.6 check whether the attribute was already deleted (no parent)
						
						if(attributeData.getParentElement() == null){
							// remove the node
							variablesPlanTreeModel.removeNodeFromParent(attributeNode);
						}
						else{
							// 2.7 update the attribute node name
							String attributeName = attributeData.getChildText("name");
							if(!attributeName.equals(attributeNode.getUserObject().toString())){
								attributeNode.setUserObject(attributeName);
								variablesPlanTreeModel.nodeChanged(attributeNode);
							}
						}
					}					
				}
				
				//2.8 if after removing some nodes the childNode is a leaf, remove it too
				if(childNode.isLeaf()){
					variablesPlanTreeModel.removeNodeFromParent(childNode);
				}
			}
			
			//2.9 if the object node is now a leaf, remove it too
			if(objectNode.isLeaf()){
				variablesPlanTreeModel.removeNodeFromParent(objectNode);
			}
			
		}
		
		//3. selected variables tree
		ItTreeNode root = new ItTreeNode("Selections");		
		root.setIcon(new ImageIcon("resources/images/projects.png"));
		
		// get checked nodes
		Object[] checked = CheckBoxNode.getCheckedNodes(
				(CheckBoxNode)variablesPlanTree.getModel().getRoot());
		
		for (int i = 0; i < checked.length; i++) {							
			CheckBoxNode node = (CheckBoxNode)checked[i];
			
			if(node.getUserObject().toString().equals("States") || node.getLevel() == 3){
				CheckBoxNode objectNode = (node.getLevel() == 3)
										?(CheckBoxNode)node.getParent().getParent()
										:(CheckBoxNode)node.getParent();
				String nodeName = (node.getLevel() == 3)
									?objectNode.getData().getChildText("name")+ "." +node.getData().getChildText("name")
									:"States of "+ objectNode.getData().getChildText("name");
				ImageIcon icon = new ImageIcon((node.getLevel() == 3)
												?"resources/images/attribute.png"
												:"resources/images/state.png");
				
				ItTreeNode newNode = new ItTreeNode(nodeName, node.getData(), null, null);
				newNode.setIcon(icon);
				root.add(newNode);
			}
		}
		
		selectedVariablesPlanTreeModel.setRoot(root);
    	selectedVariablesPlanTreeModel.reload();		        
		selectedVariablesPlanTree.expandRow(0);
	}
	

	/**
	 * This method initializes this
	 * 
	 * @return void
	 */
	private void initialize() {
		setSize(795,600);
		//setExtendedState(JFrame.MAXIMIZED_BOTH);
		setContentPane(getMainPanel());
		setJMenuBar(getItMenuBar());
		setTitle("itSIMPLE - Integrated Tool Software Interface for Modeling " +
				"Planning Environments (version "+ 
				itSettings.getChildText("version") +")");
		setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
                


	}
	
	/**
	 * This is the default constructor
	 */
	public ItSIMPLE() {
		super();
                //set icon image for itSIMPLE
                setIconImage(new ImageIcon("resources/images/logo.png").getImage());
                //Class<ItSIMPLE> clazz = ItSIMPLE.class;
                //ImageIcon op = new ImageIcon("resources/images/logo.png");
                /**if(System.getProperty("java.version").compareTo("1.6") >= 0) {
                    setIconImages(Arrays.asList(new Image[] {
                        new ImageIcon(clazz.getResource("resources/images/logo16x16.png")).getImage(),
                        new ImageIcon(clazz.getResource("resources/images/logo24x24.png")).getImage(),
                        new ImageIcon(clazz.getResource("resources/images/logo32x32.png")).getImage(),
                        new ImageIcon(clazz.getResource("resources/images/logo48x48.png")).getImage(),
                        new ImageIcon(clazz.getResource("resources/images/logo256x256.png")).getImage(), 
                        
                        //new ImageIcon(clazz.getResource("resource/DJIcon16x16.png")).getImage(),
                        //new ImageIcon(clazz.getResource("resource/DJIcon24x24.png")).getImage(),
                        //new ImageIcon(clazz.getResource("resource/DJIcon32x32.png")).getImage(),
                        //new ImageIcon(clazz.getResource("resource/DJIcon48x48.png")).getImage(),
                        //new ImageIcon(clazz.getResource("resource/DJIcon256x256.png")).getImage(),
                }));
                } else {
                    setIconImage(new ImageIcon("resources/images/logo48x48.png").getImage());
                //}**/
		initialize();
	}
	
	public static ItSIMPLE getInstance(){
		if(instance == null){
			instance = new ItSIMPLE();			
		}
		return instance;
	}
	
	public static Element getCommonData(){		
		return commonData;
	}
	
	public static Element getItSettings(){		
		return itSettings;
	}
	
	public static Element getItPlanners(){
		return itPlanners;
	}
	
	public ItTree getItTree(){
		return projectsTree;
	}
	
	public ItTabbedPane getItGraphTabbedPane(){
		return graphTabbedPane;
	}
	
	public JEditorPane getInfoEditorPane(){
		return infoEditorPane;
	}


	/**
	 * @return the planSimStatusBar
	 */
	public JLabel getPlanSimStatusBar() {
		return planSimStatusBar;
	}


	public static JFrame getItSIMPLEFrame(){
		return instance;
	}
	
	/*public static PropertiesTabbedPane getItPropertiesPane(){
		return propertiesPane;
	}*/
		
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		//get CommonData
		Document commonDoc = null;
		try {
			commonDoc = XMLUtilities.readFromFile("resources/settings/commonData.xml");
			
		} catch (JDOMException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}		
		if (commonDoc != null){
			commonData = commonDoc.getRootElement();			
		}
				
		// Get itSIMPLE itSettings from itSettings.xml		
		org.jdom.Document itSettingsDoc = null;
		try{
			itSettingsDoc = XMLUtilities.readFromFile("resources/settings/itSettings.xml");
		}
		catch(Exception e1){
			e1.printStackTrace();
		}
				
		if (itSettingsDoc != null){
			itSettings = itSettingsDoc.getRootElement();			
		}
		
		// Get planners from itPlanners		
		org.jdom.Document itPlannersDoc = null;
		try{
			itPlannersDoc = XMLUtilities.readFromFile("resources/planners/itPlanners.xml");
		}
		catch(Exception e){
			e.printStackTrace();
		}
				
		if (itPlannersDoc != null){
			itPlanners = itPlannersDoc.getRootElement();			
		}
	
		
		try{			
			String appearence = itSettings.getChild("generalSettings").getChild("graphics").getChildText("appearence");
			if (appearence.equals("Default")){
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			else if (appearence.equals("Windows")){
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel"); // Windows	
			}
			else if (appearence.equals("Metal")){
				UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");	// Metal	
			}
			else if (appearence.equals("Motif")){
				UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel"); // Motif	
			}			
			else {
				UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			}
			
			// Need installation
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"); // padrï¿½o GTK+
			//UIManager.setLookAndFeel("javax.swing.plaf.mac.MacLookAndFeel"); // Mac
											
		}
		catch(Exception e) {
	          //e.printStackTrace();
	    }
		SplashScreen splash = new SplashScreen(5000);	
		Thread t = new Thread(splash);
		t.start();
		ItSIMPLE.getInstance();		
		//instance = new ItSIMPLE();
                instance.setVisible(true);
		instance.setExtendedState(JFrame.MAXIMIZED_BOTH);
		//instance.repaint();
		//infoPanel.minimize();
                
	
	}




	/*public JEditorPane getPetriInfoEditorPane() {
		return petriInfoEditorPane;
	}*/
	
} 