/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007-2010 Universidade de Sao Paulo 
*
*
* This is the main file of itSIMPLE.
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
*		Victor Romero,
*               Matheus Haddad.
**/

package itSIMPLE;

import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.net.MalformedURLException;
import update.VersionUpdater;
import java.util.logging.Level;
import java.util.logging.Logger;
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
//import java.sql.SQLException;

import util.filefilter.PDDLFileFilter;
import util.filefilter.XMLFileFilter;

import java.awt.BorderLayout;
import java.awt.Color;
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

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import languages.pddl.PDDLToXPDDL;
import planning.PlanSimulator;
import languages.pddl.ToXPDDL;
import languages.pddl.XPDDLToPDDL;
import languages.pddl.XPDDLToUML;
import languages.petrinets.toPNML;
import java.io.*;


import planning.ExecPlanner;
import planning.PlanAnalyzer;
import planning.PlanValidator;
import planning.PlannerSuggestion;
import planning.TimeKiller;
import sourceEditor.ItHilightedDocument;
import util.download.Downloader;
import virtualprototyping.VirtualPrototypingBlender;




/**
 * This is tye main class of itSIMPLE tool (GUI)
 *
 * @author tiago
 */
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
        private JMenuItem checkUpdatesMenuItem = null;

        //Diagrams
	private JMenu newDiagramMenu = null;
	private JMenu newProblemMenu = null;
	private JMenu newDomainMenu = null;
	private JMenu openAsDomainMenu = null;
	private JMenu openAsProblemMenu = null;
	private JMenu openAsPetriNetMenu = null;
        private JMenu importDomainMenu = null;
        private JMenu importProblemMenu = null;

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
        private ItFramePanel propertiesFramePanel = null;

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
        private JButton skipPlannerProblemButton = null;
        private JButton runAllPlannersButton = null;
	private JButton setPlannerButton = null;
	private ItFramePanel planListFramePanel = null;
	private JList planList = null;
	private DefaultListModel planListModel = null;
	private JButton addPlanActionButton = null;
	private JButton removePlanActionButton = null;
	private JButton editPlanActionButton = null;
	private JButton importPlanButton = null;
	private JButton exportPlanButton = null;
	private JButton checkPlanValidityButton = null;
	private JButton quickEvaluateButton = null;
        private JButton fullEvaluationButton = null;
	private ItFramePanel planAnalysisFramePanel = null;
	private JLabel planSimStatusBar = null;
        private JProgressBar simProgressBar = null;
        private JLabel simTimeSpent = null;
	private ItFramePanel planInfoFramePanel = null;
	private JEditorPane planInfoEditorPane = null;
        private JEditorPane planEvaluationInfoEditorPane = null;
	private JTextArea outputEditorPane = null;
	private JPanel chartsPanel = null;
	private Element xmlPlan = null;
	private Thread currentThread = null;
        private Thread plannerThread = null;
	private ExecPlanner exe;
	private JButton replanButton;


	// movie maker
	private Element movie = null;
	private JPanel movieMakerPanel = null;
	private JSplitPane movieMakerSplitPane = null;

        //plan evaluation panel
        private JPanel planEvaluationPanel = null;
        private JTextField overallPlanEvaluationValue = null;
        private Thread reuserationaleThread = null;
        
        //plan database panel
        private JPanel planDatabasePanel = null;
        private JTable resultPlanTable;
	private DefaultTableModel resultPlanTableModel;
        private boolean isPlanFromDB = false;
        private int currentDBPlanID = -1;
        private JTextPane planfilterTextPane = null;
        private JPanel planFilterPanel = null;


        //plan database panel
        private JPanel rationaleDatabasePanel = null;
        private JTable resultRationaleTable;
	private DefaultTableModel resultRationaleTableModel;
        private int currentDBRationaleID = -1;


	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	private static Element commonData;
	private static Element itSettings;
	private static Element itPlanners;
        private static Element itValidators;
	private static ArrayList<Object> copyPaste = new ArrayList<Object>();
	private static ArrayList<Object> copyPasteSenders = new ArrayList<Object>();

	private JSplitPane graphSplitPane = null;
	private JPanel informationPanel = null;
	private JEditorPane infoEditorPane = null;
	private ItFramePanel infoPanel = null;
        private JMenuBar planNavigationMenuBar = null;
        JMenu replanMenu = null;
        private JMenuItem replanMenuItem = null;

        //Planning process
        Element plans = null;
        Element solveResult = null;
        Element theSingleChoosenPlanner = null;

        //Planner List
        private ArrayList<Object> plannersList = new ArrayList<Object>();

        //Planner Suggestion
        PlannerSuggestion plannerSuggestion = new PlannerSuggestion();

        //Force Planning finish
        boolean forceFinish = false;;
        boolean stopRunningPlanners = false;


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

    private Action newTimingDiagramAction = new AbstractAction("Timing Diagram",new ImageIcon("resources/images/timingDiagram.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e) {
			createNewDiagramAction("timingDiagram");
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


        private Action duplicateAction = new AbstractAction("Duplicate"){
		/**
		 *
		 */
		//private static final long serialVersionUID = -5894081679981634741L;

		public void actionPerformed(ActionEvent e) {

                        // uml tree
			ItTreeNode selectedNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			ItTreeNode project = null;
			if (selectedNode.getLevel() == 2 && selectedNode.getData().getName().equals("domain")) {
                            //duplicating a domain
                            project = (ItTreeNode)selectedNode.getParent();
                            Element originalDomain = selectedNode.getData();

                            //Element domainRef = (Element)commonData.getChild("definedNodes").getChild("elements").getChild("structure").getChild("domain");
                            Element domain = (Element)originalDomain.clone();
                            domain.setAttribute("id",String.valueOf(XMLUtilities.getId(originalDomain.getParentElement())));

                            domain.getChild("name").setText(domain.getChildText("name") + " - Copy "+ domain.getAttributeValue("id") + "");
                            originalDomain.getParentElement().addContent(domain);
                            projectsTree.buildDomainNode(domain, project);

                            // problems plan tree
                            ItTreeNode problemsTreeProjectNode = (ItTreeNode)
                                    ((ItTreeNode)problemsPlanTreeModel.getRoot()).getChildAt(treeRoot.getIndex(project));

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
			else if (selectedNode.getLevel() == 3 && selectedNode.getData().getName().equals("problem")) {
                            //duplicating a problem
                            project = (ItTreeNode)selectedNode.getParent().getParent();

                            Element originalProblem = selectedNode.getData();
                            Element problem = (Element)originalProblem.clone();
                            //Element problem = (Element)commonData.getChild("definedNodes")
                            //        .getChild("elements").getChild("structure").getChild("problem").clone();

                            problem.setAttribute("id", String.valueOf(XMLUtilities.getId(originalProblem.getParentElement())));
                            problem.getChild("name").setText(problem.getChildText("name") + " - Copy " + problem.getAttributeValue("id"));
                            originalProblem.getParentElement().addContent(problem);
                            projectsTree.buildProblemNode(problem, (ItTreeNode)selectedNode.getParent());

                            // problems plan tree
                            ItTreeNode problemsTreeProject = (ItTreeNode)((ItTreeNode)problemsPlanTreeModel.getRoot())
                                                                                                    .getChildAt(treeRoot.getIndex(project));

                            ItTreeNode problemsTreeDomain = null;
                            // look for the domain in problems plan tree
                            for(int i = 0; i < problemsTreeProject.getChildCount(); i++){
                                    ItTreeNode child = (ItTreeNode)problemsTreeProject.getChildAt(i);
                                    if(child.getData() == selectedNode.getData().getParentElement().getParentElement()){
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
                petriDiagramGraph.setBackground(Color.WHITE);

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
                                                    //String html = PlanSimulator.createHTMLPlanAnalysis(analysis, xmlPlan, problem);
                                                    //System.out.println(html);

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
			fc.setDialogTitle("Import Plan file");
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
                                //setPlanInfoPanelText(generateHTMLReport(xmlPlan));
                                //setPlanInfoPanelText(PlanAnalyzer.generateHTMLSinglePlanReport(xmlPlan));
                                showHTMLReport(xmlPlan);
                                appendOutputPanelText(">> Plan importerd successfully. Check the generated Results. \n");

                                //clean up reference of plans from database
                                cleanupPlanDatabaseReference();

                                //save lst open folder
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

			String lastOpenFolder = "";
			Element lastOpenFolderElement = itSettings.getChild("generalSettings").getChild("lastOpenFolder");
			if (lastOpenFolderElement != null){
				lastOpenFolder = lastOpenFolderElement.getText();
			}
			JFileChooser fc = new JFileChooser(lastOpenFolder);
			fc.setDialogTitle("Export xml plan");
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

                //save the plan file
                XMLUtilities.writeToFile(filePath, new Document((Element)xmlPlan.clone()));

                //save last open folder
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
                        Element validity = xmlPlan.getChild("validity");
                        if (validity!=null){
                            if (!validity.getAttributeValue("isValid").equals("")){
                                validity.setAttribute("isValid", "");
                                validity.setText("");
                                showHTMLReport(xmlPlan);
                            }
                            
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


 



        /*
         * Action for executing a validator
         */
    private Action checkPlanValidity = new AbstractAction("", new ImageIcon("resources/images/validate.png")){
		/**
		 *
		 */
	public void actionPerformed(ActionEvent e){
            if (xmlPlan!=null){
                if (xmlPlan.getName().equals("xmlPlan")){
                    new Thread(){
                        @Override
                            public void run() {
                                PlanValidator.checkPlanValidityWithVAL(xmlPlan);
                                showHTMLReport(xmlPlan);
                            }
                    }.start();
                }

                }
            }
	};



        /**
         * Import PDDL problem instances
         */
	private Action importPDDLProblem = new AbstractAction("PDDL Problem", new ImageIcon("resources/images/new.png")){

		/**
		 *
		 */
		private static final long serialVersionUID = -1827112480646045838L;

		public void actionPerformed(ActionEvent e) {

                    String lastOpenFolder = "";
			Element lastOpenFolderElement = itSettings.getChild("generalSettings").getChild("lastOpenFolder");
			if (lastOpenFolderElement != null){
				lastOpenFolder = lastOpenFolderElement.getText();
			}
			JFileChooser fc = new JFileChooser(lastOpenFolder);
			fc.setDialogTitle("Import PDDL Problem");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                        fc.setMultiSelectionEnabled(true);
			fc.setFileFilter(new PDDLFileFilter());
			int returnVal = fc.showOpenDialog(ItSIMPLE.this);
			if (returnVal == JFileChooser.APPROVE_OPTION){

                                File[] files = fc.getSelectedFiles();
                                //File file = fc.getSelectedFile();

                                for (int i = 0; i < files.length; i++) {
                                    File file = files[i];

                                    //String problemFile = "/home/tiago/Desktop/LogisticTwoPackages.pddl";
                                    String problemFile = file.getPath();

                                    // get selected node from the projects tree
                                    ItTreeNode selectedNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();

                                    //parse pddl to xpddl
                                    Element xpddlProblem = PDDLToXPDDL.parsePDDLproblemToXPDDL(problemFile);

                                    //parse xpddl to internal uml model
                                    Element problem = XPDDLToUML.parseXPDDLProbemToUML(xpddlProblem, selectedNode.getData());

                                    selectedNode.getData().getChild("planningProblems").addContent(problem);
                                    projectsTree.buildProblemNode(problem, selectedNode);

                                    // problems plan tree
                                    ItTreeNode problemsTreeProject = (ItTreeNode)((ItTreeNode)problemsPlanTreeModel.getRoot())
                                                                                                            .getChildAt(treeRoot.getIndex(selectedNode.getParent()));

                                    ItTreeNode problemsTreeDomain = null;
                                    // look for the domain in problems plan tree
                                    for(int ii = 0; ii < problemsTreeProject.getChildCount(); ii++){
                                            ItTreeNode child = (ItTreeNode)problemsTreeProject.getChildAt(ii);
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



                                    if (lastOpenFolderElement != null){
                                            //Holds the last open folder
                                            if (!lastOpenFolderElement.getText().equals(file.getParent())){
                                                    lastOpenFolderElement.setText(file.getParent());
                                                    XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
                                            }
                                    }


                                }


			}






		}
	};


	private Action plannersSettingsAction = new AbstractAction("Planners Settings", new ImageIcon("resources/images/engine.png")){
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
         * This action opens the dialog for editing plan evaluation
         */
        private Action changePlanEvaluationAction = new AbstractAction("Edit Evaluation", new ImageIcon("resources/images/edit.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e){
			final PlanEvaluationEditDialog dialog = new PlanEvaluationEditDialog(ItSIMPLE.this, xmlPlan);
			dialog.setVisible(true);
                        if (xmlPlan != null){
                            //String evaluationhtml = PlanAnalyzer.generatePlanMetricsSummary(xmlPlan, xmlPlan.getChild("metrics"));
                            String evaluationhtml = PlanAnalyzer.generatePlanMetricsEvaluationSummary(xmlPlan);
                            setPlanEvaluationInfoPanelText(evaluationhtml);
                            //System.out.println(evaluationhtml);
                        }
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

        /**
         * This action opens the About box dialog
         */
 	private Action checkUpdatesAction = new AbstractAction("Check for Updates"){
		public void actionPerformed(ActionEvent e){

            File versionFile = new File("resources/settings/version.xml");
            versionFile.delete();

            new Thread(){
                @Override
					public void run() {
                        try {
                            String urlVersion = "http://dlab.poli.usp.br/twiki/pub/ItSIMPLE/DownLoad/currentVersion.xml";
                            String versionFilePath = "resources/settings/versionControl.xml";
                            

                            File versionFile = new File(versionFilePath);
                            URL url = null;
                            try {
                                url = new URL(urlVersion);
                            } catch (MalformedURLException ex) {
                                Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            Downloader downloader = new Downloader(url, versionFile);

                            Thread goGetIt = new Thread(downloader);
                            goGetIt.start();

                            //TODO: Treat when there is no internet access.
                            //wait until download is completed.
                            
                            //wait to finish normaly or by timeout
                            //long timeout = 10000;
                            //long start = System.currentTimeMillis();
                            //long timespent = 0;
                            //while(!downloader.isCompleted() && timespent < timeout){
                            //    Thread.sleep(500);
                            //    timespent = System.currentTimeMillis() - start;
                            //}

                            //if (timespent >= timeout){
                            //   downloader.cancel();
                            //    goGetIt.interrupt();
                            //    JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>Problem while downloading the file. <br>"+
                            //            "Please ckech your internet connection or try it later.</html>");
                            //            //downloader.getProgressString() + ".</html>");
                            //    return;
                            //}


                            downloader.waitUntilCompleted();

                            if (versionFile.exists()){
                                //read the file an check version.
                                org.jdom.Document versionDoc = null;
                                try{
                                    versionDoc = XMLUtilities.readFromFile(versionFilePath);
                                }
                                catch(Exception e1){
                                    e1.printStackTrace();
                                }

                                if (versionDoc != null){
                                    Element serverVersion = versionDoc.getRootElement().getChild("version");
                                    Element localVersion = itSettings.getChild("version");

                                    String sversion = serverVersion.getText().trim();
                                    String lversion = localVersion.getText().trim();
                                    if (!sversion.equals(lversion)){
                                        //A new version is available. Please visit ...
                                        //You have the latest version of itSIMPLE.
                                        //String link = "http://code.google.com/p/itsimple/";
                                        JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>A new version is available ("+sversion+").<br>"+
                                                "Please visit our website dlab.poli.usp.br</html>");
                                        //"Please visit: <a ref=\""+link+"\">" + link + "</a></html>");

                                    }
                                    else{
                                        //You have the latest version of itSIMPLE.
                                        JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>You have the latest version of itSIMPLE. <br>"+
                                        "Version: <strong>" + sversion + "</strong></html>");
                                    }
                                }
                                

                            }
                            else{
                                //problem while downloading the file
                                JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>Problem while downloading the file. <br>"+
                                        downloader.getProgressString() + ".</html>");
                            }

                            //clear the process
                            if (versionFile.exists()){
                                versionFile.delete();
                            }


                        } catch (Exception e) {
                        }





                    }

            }.start();

           


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


    private Action quickEvaluation = new AbstractAction("", new ImageIcon("resources/images/evalquick.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e){
            if (xmlPlan!=null){
                appendOutputPanelText(">> Plan evaluation process requested. \n");
                String html = "";
                if (xmlPlan.getName().equals("xmlPlan")){
                    //Check if there is a problem being selected in the tree
                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                    if (selectedNode!=null){
                        Element element = selectedNode.getData();
                        //System.out.println(element.getName());
                        if (element.getName().equals("problem")){
                            Element domain = element.getParentElement().getParentElement();
                            Element metrics = PlanSimulator.createMetricsNode(element, domain);
                            String metricshtml = "";
                            if(metrics.getChildren().size() > 0){
                                PlanSimulator.createMetricDatasets(metrics, xmlPlan, element, domain, null);
                                PlanAnalyzer.setMetricsValuesAndEvaluations(metrics, xmlPlan);
                                xmlPlan.removeChildren("metrics"); //clear metrics
                                xmlPlan.addContent(metrics);
                                //metricshtml = PlanAnalyzer.generatePlanMetricsSummary(xmlPlan, metrics);
                                metricshtml = PlanAnalyzer.generatePlanMetricsEvaluationSummary(xmlPlan);
                            }else{
                                metricshtml = "<h2><font color=\"red\">No metric specified.<font></h2>";
                                metricshtml += PlanAnalyzer.generatePlanMetricsEvaluationSummary(xmlPlan);
                                
                                //metricshtml = PlanAnalyzer.generatePlanMetricsEvaluationSummary(xmlPlan);
                                //metricshtml += "<br><h2><font color=\"red\">No metric was specified.<font></h2>";

                                //metricshtml = "<html><h2>No metric was specified.</h2></html>";
                            }


                            //html += "<br><br>";
                            html += metricshtml;
                            //html += "</body>";
                           // html += "</html>";
                        }

                    }

                }

                setPlanEvaluationInfoPanelText(html);

            }
		}
	};


    private Action generateEvaluationReport = new AbstractAction("Evaluation Report", new ImageIcon("resources/images/viewreport.png")){
		/**
		 *
		 */

		public void actionPerformed(ActionEvent e) {

                // the thread is created so the status bar can be refreshed
                    new Thread(){
                            public void run() {
                                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                                    if(selectedNode != null && selectedNode.getLevel() == 3){
                                            Element problem = selectedNode.getData();

                                    if (xmlPlan != null){
                                        //XMLUtilities.printXML(xmlPlan);
                                        appendOutputPanelText(">> Plan evaluation report requested. \n");
                                        Element domain = problem.getParentElement().getParentElement();
                                        //if no metrics exists create it
                                        /*
                                        if (xmlPlan.getChild("metrics") == null){
                                            Element metrics = PlanSimulator.createMetricsNode(problem, domain);
                                            if(metrics.getChildren().size() > 0){
                                                PlanSimulator.createMetricDatasets(metrics, xmlPlan, problem, domain, null);
                                            }
                                            //XMLUtilities.printXML(metrics);
                                            xmlPlan.addContent(metrics);
                                        }
                                         */

                                        appendOutputPanelText("         Creating report (html format)... \n");

                                        //String html = PlanAnalyzer.generateFullPlanReport2(domain, problem, xmlPlan, metrics);
                                        String html = PlanAnalyzer.generateFullPlanReport2(domain, problem, xmlPlan, xmlPlan.getChild("metrics"));




                                            //EXEPERIMENTS WITH GOOGLE CHARTS API
                                            /*
                                            String html = "<html>\n";
                                            html += "<head>\n";
                                            html += "    <script type=\"text/javascript\" src=\"http://www.google.com/jsapi\"></script>\n";

                                            html += "  </head> \n";
                                            html += " \n";
                                            html += "  <body> \n";
                                            html += "    <div id=\"chart_div\"></div> \n";

                                            html += "    <script type=\"text/javascript\">\n";
                                            html += "      google.load(\"visualization\", \"1\", {packages:[\"linechart\"]});\n";
                                            html += "      google.setOnLoadCallback(drawChart);\n";
                                            html += "      function drawChart() {\n";
                                            html += "        var data = new google.visualization.DataTable();\n";
                                            html += "        data.addColumn('string', 'Year');\n";
                                            html += "        data.addColumn('number', 'Sales');\n";
                                            html += "        data.addColumn('number', 'Expenses');\n";
                                            html += "        data.addRows(4);\n";
                                            html += "        data.setValue(0, 0, '2004');\n";
                                            html += "        data.setValue(0, 1, 1000);\n";
                                            html += "        data.setValue(0, 2, 400);\n";
                                            html += "        data.setValue(1, 0, '2005');\n";
                                            html += "        data.setValue(1, 1, 1170);\n";
                                            html += "        data.setValue(1, 2, 460);\n";
                                            html += "        data.setValue(2, 0, '2006');\n";
                                            html += "        data.setValue(2, 1, 860);\n";
                                            html += "        data.setValue(2, 2, 580); \n";
                                            html += "        data.setValue(3, 0, '2007'); \n";
                                            html += "        data.setValue(3, 1, 1030); \n";
                                            html += "        data.setValue(3, 2, 540); \n";
                                            html += " \n";
                                            html += "        var chart = new google.visualization.LineChart(document.getElementById('chart_div')); \n";
                                            html += "        chart.draw(data, {width: 700, height: 400, legend: 'bottom', title: 'Company Performance'}); \n";
                                            html += "      } \n";
                                            html += "    </script> \n";

                                            html += "    <img border='0' alt='Google Chart' src='http://chart.apis.google.com/chart?chxt=x,y&chtt=Test&cht=lc&chxl=&chs=300x200&chd=s:ADjJ2A8&chf=bg,s,' /> \n";
                                            html += "  </body> \n";
                                            html += "</html> \n";
                                            */


                                            //EXPREIMENTS WITH GOOGLE CHART API IMAGE - works in basick panel
                                            //info += "<img border='0' alt='Google Chart' src='http://chart.apis.google.com/chart?chxt=x,y&chtt=Test&cht=lc&chxl=&chs=300x200&chd=s:ADjJ2A8&chf=bg,s,' />";


                                        //System.out.println(html);

                                        appendOutputPanelText(">> The Plan Report was generated! Lauching browser. \n");


                                        //Save file
                                        String path = "resources/report/PlanReport.html";
                                        FileWriter file = null;
                                        try {
                                            file = new FileWriter(path);
                                            file.write(html);
                                            file.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }
                                        //Opens html with defaut browser
                                        File report = new File(path);
                                        path = report.getAbsolutePath();
                                        //System.out.println(path);
                                        try {
                                            BrowserLauncher launcher = new BrowserLauncher();
                                            launcher.openURLinBrowser("file://"+path);

                                        } catch (BrowserLaunchingInitializingException ex) {
                                            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                            appendOutputPanelText("ERROR. Problem while trying to open the default browser. \n");
                                        } catch (UnsupportedOperatingSystemException ex) {
                                            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                            appendOutputPanelText("ERROR. Problem while trying to open the default browser. \n");
                                        }



                                    }

                //XMLUtilities.printXML(xmlPlan);
                                    }
                            }
                    }.start();



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
		DataBase updateType = new DataBase();

		updateType.setTableName("itsimple");
		updateType.setColumnList("itssname, itssversion, itsxmodel"); // please, don't use ()
		updateType.setValueList("?, ?, ?"); // please, don't use ()
		updateType.addToParametersList(name);
		updateType.addToParametersList(version);
		updateType.addToParametersList(xmlFile);
		updateType.Insert();
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
                        itSettings.getChild("generalSettings").getChild("graphics").getChild("appearence").setText("Windows");
                        XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
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
                        itSettings.getChild("generalSettings").getChild("graphics").getChild("appearence").setText("Metal");
                        XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
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
                        itSettings.getChild("generalSettings").getChild("graphics").getChild("appearence").setText("Motif");
                        XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
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
                        itSettings.getChild("generalSettings").getChild("graphics").getChild("appearence").setText("Default");
                        XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
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
            helpMenu.add(getCheckUpdatesMenuItem());

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
	 * This method initializes checkUpdatesMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getCheckUpdatesMenuItem() {
		if (checkUpdatesMenuItem == null) {
			checkUpdatesMenuItem = new JMenuItem();
			checkUpdatesMenuItem.setText("Check for Updates");
			checkUpdatesMenuItem.setAction(checkUpdatesAction);
		}
		return checkUpdatesMenuItem;
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
			propertiesFramePanel = new ItFramePanel(":: Properties", ItFramePanel.MINIMIZE_MAXIMIZE);
			propertiesFramePanel.setContent(getPropertiesPane(), false);
			propertiesFramePanel.setParentSplitPane(propertySplitPane);
			propertiesPanel.add(propertiesFramePanel, BorderLayout.CENTER);
			graphTabbedPane.setPropertiesPane(propertiesPane);
		}
		return propertiesPanel;
	}

        /**
         * This method sets the title of the propoerties panel (default ':: Properties')
         * @param title
         */
        public void setPropertiesPanelTitle(String title){
            propertiesFramePanel.setTitle(title);
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
			//newDiagramMenu.add(newActivityDiagramAction);
			newDiagramMenu.add(newTimingDiagramAction);
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
	 * This method initializes getOpenAsProblemMenu
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
	 * This method initializes getOpenAsProblemMenu
	 *
	 * @return javax.swing.JMenu
	 */
	private JMenu getImportProblemMenu() {
		if (importProblemMenu == null) {
			importProblemMenu = new JMenu();
			importProblemMenu.setIcon(new ImageIcon("resources/images/new.png"));
			importProblemMenu.setText("Import");
			importProblemMenu.add(importPDDLProblem);
		}
		return importProblemMenu;
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
                                        treePopupMenu.add(getImportProblemMenu());
					treePopupMenu.addSeparator();
                                        treePopupMenu.add(duplicateAction);
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
                                        treePopupMenu.add(duplicateAction);
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
			//mainTabbedPane.addTab("Plan Sim", getPlanSimPane());
                        mainTabbedPane.addTab("Planning", getPlanSimPane());
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
            //planSimPane.add(planSimStatusBar, BorderLayout.SOUTH);


            simProgressBar = new JProgressBar(0,100);
            simProgressBar.setValue(0);
            simProgressBar.setStringPainted(true);
            simProgressBar.setPreferredSize(new java.awt.Dimension(200,18));
            simProgressBar.setVisible(false);

            simTimeSpent = new JLabel("");
            JPanel timeCtrlPlanSimPane = new JPanel(new BorderLayout());
            timeCtrlPlanSimPane.add(simProgressBar, BorderLayout.CENTER);
            timeCtrlPlanSimPane.add(simTimeSpent, BorderLayout.WEST);

            JPanel bottomPlanSimPane = new JPanel(new BorderLayout());
            bottomPlanSimPane.add(planSimStatusBar, BorderLayout.CENTER);
            //bottomPlanSimPane.add(simProgressBar, BorderLayout.EAST);
            bottomPlanSimPane.add(timeCtrlPlanSimPane, BorderLayout.EAST);
            planSimPane.add(bottomPlanSimPane, BorderLayout.SOUTH);

			
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

                                                //clean up reference of plans from database
                                                cleanupPlanDatabaseReference();

                                                String pddlVersion = pddlButtonsGroup.getSelection().getActionCommand();
                                                Element problem = selectedNode.getData();
                                                Element domainProject = problem.getDocument().getRootElement();
                                                Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(domainProject, pddlVersion, null);

						//fill the combo box with the existing available planners
						plannersComboBox.removeAllItems();
                                                plannersList.clear();
                                                plannerSuggestion.initialPlannerSelection(xpddlDomain, itPlanners);

                                                //List<?> planners = itPlanners.getChild("planners").getChildren("planner");

                                                plannersComboBox.addItem("-- Supported Planners --");
                                                plannersList.add(null);

                                                // Supported Planners
                                                fillPlannersComboBox(plannerSuggestion.getSuggestedPlanners());
                                                plannersComboBox.addItem("All Supported Planners");
                                                plannersList.add("allSupportedPlanners");

                                                plannersComboBox.addItem(null);
                                                plannersList.add(null);

                                                plannersComboBox.addItem("-- Discarded Planners --");
                                                plannersList.add(null);

                                                // Discarded Planners
                                                fillPlannersComboBox(plannerSuggestion.getDiscardedPlanners());

                                                plannersComboBox.addItem(null);
                                                plannersList.add(null);

                                                plannersComboBox.addItem("All Planners");
                                                plannersList.add("allPlanners");

                                                //This item specify/represent the planners that are seceyed/enable for run all
                                                plannersComboBox.addItem("My Favorite Planners");
                                                plannersList.add("myFavoritePlanners");
                                                //plannersComboBox.addItem("All Selected Planners");
                                                //plannersList.add("allSelectedPlanners");

						CheckBoxNode variablesPlanTreeRoot = (CheckBoxNode)variablesPlanTreeModel.getRoot();

						// delete old tree nodes
						if(variablesPlanTreeRoot.getChildCount() > 0){
							variablesPlanTreeRoot = new CheckBoxNode("Objects");
							variablesPlanTreeRoot.setIcon(new ImageIcon("resources/images/projects.png"));
							variablesPlanTreeModel.setRoot(variablesPlanTreeRoot);
							variablesPlanTreeModel.reload();
						}
                        
											//planningProblems			domain
						List<?> objects = problem.getParentElement().getParentElement().getChild("elements").getChild("objects").getChildren("object");
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
						//setPlannerButton.setEnabled(false);
                                                setPlannerButton.setEnabled(true);
						addPlanActionButton.setEnabled(false);
						importPlanButton.setEnabled(false);
						planListModel.clear();
						xmlPlan = null;

                                                //clean up reference of plans from database
                                                cleanupPlanDatabaseReference();

                                                //fill the combo box with all planners
						plannersComboBox.removeAllItems();
                                                plannersList.clear();

                                                List<?> planners = itPlanners.getChild("planners").getChildren("planner");
                                                fillPlannersComboBox(planners);

                                                plannersComboBox.addItem("All Planners");
                                                plannersList.add("allPlanners");

                                                //This item specify/represent the planners that are seceyed/enable for run all
                                                plannersComboBox.addItem("My Favorite Planners");
                                                plannersList.add("myFavoritePlanners");
                                                //plannersComboBox.addItem("All Selected Planners");
                                                //plannersList.add("allSelectedPlanners");

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


                        //The below approach of put variable selection and problems all together
                        // has confused user, so we decided to put variable selection inside the Variable tracking panel
                        // tabbed panes with jtrees
			//planTreeTabbedPane = new JTabbedPane();
			//planTreeTabbedPane.addTab("Problems", new JScrollPane(problemsPlanTree));
			//planTreeTabbedPane.addTab("Variables", new JScrollPane(variablesPlanTree));
			//planTreeTabbedPane.addTab("Selected", new JScrollPane(selectedVariablesPlanTree));
			//mainTreePanel.add(planTreeTabbedPane, BorderLayout.CENTER);

                        //
                        mainTreePanel.add(new JScrollPane(problemsPlanTree), BorderLayout.CENTER);


            // tool panel
            JPanel toolsPanel = new JPanel(new BorderLayout());

            JPanel topPanel = new JPanel();
            JPanel bottonPanel = new JPanel();

            // combobox with planners
            plannersComboBox = new JComboBox();

            // add to the panel
            //toolsPanel.add(plannersComboBox);
            topPanel.add(plannersComboBox, BorderLayout.CENTER);

            // solve problem button
            solveProblemButton = new JButton("Solve", new ImageIcon("resources/images/engine.png"));
            //solveProblemButton.setEnabled(false);
            solveProblemButton.setActionCommand("solve");
            solveProblemButton.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                            if(solveProblemButton.getActionCommand().equals("solve")){
                                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                                    if(selectedNode != null){
                                        stopRunningPlanners = false;
                                        forceFinish = false;
                                        //Verify selectedNode and solve problems
                                        skipPlannerProblemButton.setEnabled(true);
                                        solve(selectedNode);

                                        /*
                                        Element problem = selectedNode.getData();
                                        if(problem != null){
                                                // clear plan list and plan info pane
                                                setPlanList(null);
                                                setPlanInfoPanelText("");

                                                Element domainProject = problem.getDocument().getRootElement();
                                                Element domain = problem.getParentElement().getParentElement();

                                                String pddlVersion = pddlButtonsGroup.getSelection().getActionCommand();

                                                // generate PDDL domain
                                                Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(domainProject, pddlVersion, null);
                                                //XMLUtilities.printXML(xpddlDomain);

                                                // generate PDDL problem
                                                Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, pddlVersion);
                                                //XMLUtilities.printXML(xpddlProblem);

                                                //Change domain requirements (if necessary) based on the chosen problem
                                                ToXPDDL.adjustRequirements(xpddlDomain, xpddlProblem, pddlVersion);

                                                String pddlDomain = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "");
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

                                                //List<?> planners = itPlanners.getChild("planners").getChildren("planner");
                                                if (plannersList.get(plannersComboBox.getSelectedIndex()).getClass()==Element.class){
                                                    Element chosenPlanner = (Element)plannersList.get(plannersComboBox.getSelectedIndex());

                                                    exe = new ExecPlanner(chosenPlanner, domainFile.getPath(), problemFile.getPath(), false);

                                                    exe.setXMLDomain(problem.getParentElement().getParentElement());
                                                    exe.setXMLProblem(problem);
                                                    exe.setProblemName(problem.getChildText("name"));
                                                    exe.setDomainName(domain.getChildText("name"));
                                                    exe.setProjectName(domainProject.getChildText("name"));

                                                    currentThread = new Thread(exe);
                                                    currentThread.start();

                                                    // changes the button action command
                                                    solveProblemButton.setActionCommand("stop");
                                                    solveProblemButton.setText("Stop");
                                                    solveProblemButton.setIcon(new ImageIcon("resources/images/stop.png"));
                                                }


                                        }*/


                                    }
                            }
                            else{
                                    if(currentThread.isAlive()){
                                        stopRunningPlanners = true;
                                        forceFinish = true;

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

                                            if (plannerThread != null){
                                                if(plannerThread.isAlive()){
                                                    plannerThread.interrupt();
                                                }
                                            }

                                            

                                            planSimStatusBar.setText("Status: Planning process stopped.");
                                            outputEditorPane.append(">> Planning process stopped.");

                                            // changes the button action command
                                            solveProblemButton.setActionCommand("solve");
                                            solveProblemButton.setText("Solve");
                                            solveProblemButton.setIcon(new ImageIcon("resources/images/engine.png"));

                                            //changes the Skip Button
                                            skipPlannerProblemButton.setEnabled(false);
                                            
                                    }
                                    hideSimProgressBar();
                                    simTimeSpent.setText("");
                                    
                            }
                    }

            });

            // skip planner/problem button
            skipPlannerProblemButton = new JButton("Skip", new ImageIcon("resources/images/skip.png"));
            skipPlannerProblemButton.setVisible(false);
            skipPlannerProblemButton.setActionCommand("skip");
            skipPlannerProblemButton.addActionListener(new ActionListener(){
                    public void actionPerformed(ActionEvent e) {
                        forceFinish = true;
                    }
            });


            // add to the panel
            //toolsPanel.add(solveProblemButton);

            /*
            //runAllPlannersButton = new JButton("Run All planners", new ImageIcon("resources/images/edit.png"));
            runAllPlannersButton = new JButton("Run all planners");
            runAllPlannersButton.setToolTipText("Run all available planners");
            runAllPlannersButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                        if(selectedNode != null){
                            Element problem = selectedNode.getData();
                            if(problem != null){
                                solveProblemWithAllPlanners(problem.getDocument().getRootElement(), problem);
                            }

                        }
                    }
            });*/


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

					//PlannersSettingsDialog dialog =	new PlannersSettingsDialog(
					//		ItSIMPLE.this, plannersComboBox.getSelectedIndex());
                    PlannersSettingsDialog dialog =	new PlannersSettingsDialog(
							ItSIMPLE.this);

					dialog.setVisible(true);
				}

			});

			//toolsPanel.add(setPlannerButton);


            topPanel.add(setPlannerButton, BorderLayout.EAST);
            bottonPanel.add(solveProblemButton);
            bottonPanel.add(skipPlannerProblemButton);
            //bottonPanel.add(runAllPlannersButton);
            toolsPanel.add(topPanel, BorderLayout.NORTH);
            toolsPanel.add(bottonPanel, BorderLayout.SOUTH);

			// add the tools panel to the main panel
			mainTreePanel.add(toolsPanel, BorderLayout.SOUTH);

			planTreeFramePanel.setContent(mainTreePanel, false);
		}

		return planTreeFramePanel;
	}

        public void fillPlannersComboBox(List<?> planners){
            for (Iterator<?> iter = planners.iterator(); iter.hasNext();) {
                Element planner = (Element) iter.next();
                //String plannerFile = planner.getChild("settings").getChildText("filePath");
                //System.out.println(plannerFile);
                //File versionFile = new File(plannerFile);
                //if (versionFile.exists()) {
                    plannersComboBox.addItem(planner.getChildText("name") + " - Version: " + planner.getChildText("version"));
                    plannersList.add(planner);
                //}
            }
        }

	public void solveReplaningProblem(Element project, Element problem){
		if(problem != null){
			// clear plan list and plan info pane
			setPlanList(null);
			setPlanInfoPanelText("");
                        setPlanEvaluationInfoPanelText("");
                        cleanupPlanDatabaseReference();

			String pddlVersion = pddlButtonsGroup.getSelection().getActionCommand();

			// generate PDDL domain							// root element
			Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(project, pddlVersion, null);
			// generate PDDL problem
			Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, pddlVersion);

                        ToXPDDL.adjustRequirements(xpddlDomain, xpddlProblem, pddlVersion);

                        String pddlDomain = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "");
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


           //get the selected planner
           Element chosenPlanner = null;
           Object selectedPlannerListItem = null;


            if (plannersComboBox.getSelectedItem() != null){
                selectedPlannerListItem = plannersList.get(plannersComboBox.getSelectedIndex());
            }

            //System.out.println(selectedPlannerListItem);
            if (selectedPlannerListItem!=null){
               if (selectedPlannerListItem.getClass()==Element.class){
                   chosenPlanner = (Element)selectedPlannerListItem;
               }
            }


	        //List<?> planners = itPlanners.getChild("planners").getChildren("planner");
			//Element chosenPlanner = (Element)planners.get(plannersComboBox.getSelectedIndex());

            if (chosenPlanner != null){
                // execute planner
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
            else{
               JOptionPane.showMessageDialog(this,"Please, select a single planner in the list!");
            }

		}
	}


    /**
     * This methods calls all possible planners to solve a planning problem.
     * Every plan (in a XML format) is inserted in a global variavel called
     * (Element) plans
     * @param project the chosen project
     * @param problem the chosen problem
     */
    public void solveProblemWithAllPlanners(Element project, Element problem){
		if(problem != null){
			// clear plan list and plan info pane
			setPlanList(null);
			setPlanInfoPanelText("");
                        setPlanEvaluationInfoPanelText("");
 			String pddlVersion = pddlButtonsGroup.getSelection().getActionCommand();
                        cleanupPlanDatabaseReference();

			// generate PDDL domain							// root element
			Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(project, pddlVersion, null);
			// generate PDDL problem
			Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, pddlVersion);

            ToXPDDL.adjustRequirements(xpddlDomain, xpddlProblem, pddlVersion);

            String pddlDomain = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "");
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


			exe = new ExecPlanner(null,
					domainFile.getPath(), problemFile.getPath(), true);

			exe.setXMLDomain(problem.getParentElement().getParentElement());
			exe.setXMLProblem(problem);
            exe.setProblemName(problem.getChildText("name"));
            exe.setDomainName(problem.getParentElement().getParentElement().getChildText("name"));
            exe.setProjectName(project.getChildText("name"));

            plans = new Element("plans");
            appendOutputPanelText(">> Calling all possible planners to solve the selected problem ");

			currentThread = new Thread(){
					public void run() {

                        JLabel status = ItSIMPLE.getInstance().getPlanSimStatusBar();
                        status.setText("Status: Solving planning problem...");

                        List<Element> planners = itPlanners.getChild("planners").getChildren("planner");
                        for (Iterator<Element> it = planners.iterator(); it.hasNext();) {
                            Element planner = it.next();
                            if (planner.getChild("platform").getChild("windows") == null){
                                try {
                                    status.setText("Status: Solving planning problem with "+planner.getChildText("name")+"...");
                                    skipPlannerProblemButton.setToolTipText("<html>Skip planning:<br /><strong>Planner</strong>:"+planner.getChildText("name")+"<br /><strong>Problem</strong>:"+exe.getProblemName()+"</html>");
                                    exe.setChosenPlanner(planner);
                                    Element xmlPlan = exe.solveProblem();
                                    plans.addContent((Element)xmlPlan.clone());
                                    status.setText("Status: Done solving planning problem with "+planner.getChildText("name")+"!");
                                    skipPlannerProblemButton.setToolTipText("");
                                } catch (Exception e) {

                                }

                            }

                        }

                        status.setText("Status: Done solving planning problem with multiple planners!");
                        ItSIMPLE.getInstance().setSolveProblemButton();

                        //XMLUtilities.printXML(plans);

                        appendOutputPanelText(">> Done with all possible planners to solve the selected problem!");

					}
				};

			currentThread.start();

			// changes the button action command
			solveProblemButton.setActionCommand("stop");
			solveProblemButton.setText("Stop");
                        solveProblemButton.setIcon(new ImageIcon("resources/images/stop.png"));
		}
	}


	/**
	 * Sets the solveProblemButton action command to "solve".
	 * Has to be called when a plan is succesfully solved.
	 */
	public void setSolveProblemButton(){
		solveProblemButton.setActionCommand("solve");
		solveProblemButton.setText("Solve");
                solveProblemButton.setIcon(new ImageIcon("resources/images/engine.png"));
                skipPlannerProblemButton.setVisible(false);
	}

	public Element getProblemsPlanTreeSelectedProblem(){
		Element problem = ((ItTreeNode)problemsPlanTree.getLastSelectedPathComponent()).getData();
		return problem;
	}

    private void solve(ItTreeNode selectedNode) {
        Element node = selectedNode.getData();
        String type ="";
        //check if it is a projects node
        if (node == null){
            type = "projects";
        }
        else{
            type = node.getName();
        }


       boolean simpleCase = false;
       List<Element> planners = new ArrayList<Element>();
       Element singlePlanner = null;
       Object selectedPlannerListItem = null;

       if (plannersComboBox.getSelectedItem() != null){
        selectedPlannerListItem = plannersList.get(plannersComboBox.getSelectedIndex());
       }


       //System.out.println(selectedPlannerListItem);

       if (selectedPlannerListItem!=null){
           if (selectedPlannerListItem.getClass()==Element.class){
               planners.add((Element)selectedPlannerListItem);
               singlePlanner = (Element)selectedPlannerListItem;
               simpleCase =true;
           }else if (selectedPlannerListItem.equals("allSupportedPlanners")){
               planners.addAll(plannerSuggestion.getSuggestedPlanners());
           }else if (selectedPlannerListItem.equals("allPlanners")){
               //for the problem selects both suggested and discarded
               if (type.equals("problem")){
                   planners.addAll(plannerSuggestion.getSuggestedPlanners());
                   planners.addAll(plannerSuggestion.getDiscardedPlanners());
               }
               //if it is a projects, project, or domain get all planners from itPlanners.xml
               else{
                   planners = itPlanners.getChild("planners").getChildren("planner");
               }

           //}else if (selectedPlannerListItem.equals("allSelectedPlanners")){
            }else if (selectedPlannerListItem.equals("myFavoritePlanners")){
                //sellected only the planners that are enable for run all comparison
                try {
                    XPath path = new JDOMXPath("planner[settings/runAllComparison/@enabled='true']");
                    planners = path.selectNodes(itPlanners.getChild("planners"));
                    
                } catch (JaxenException e2) {
                    e2.printStackTrace();
                }

                if (planners == null || planners.size() < 1){
                    appendOutputPanelText("\n   (!) No planner is seleceted for comparison. Running all instead.\n");
                    planners = itPlanners.getChild("planners").getChildren("planner");
                }

           }

           switch(selectedNode.getLevel()){
                case 0: //Projects
                    skipPlannerProblemButton.setVisible(true);
                    this.solveAllProjects(selectedNode, planners);
                    break;
                case 1: //Project
                    skipPlannerProblemButton.setVisible(true);
                    this.solveAllDomainsFromProject(selectedNode.getData(), planners);
                    break;
                case 2: //Domain
                     skipPlannerProblemButton.setVisible(true);
                     this.solveAllProblemsFromDomain(selectedNode.getData(), planners);
                    break;
                case 3: //Problem
                    if(simpleCase){
                        skipPlannerProblemButton.setVisible(false);
                        this.solveProblemWithSinglePlanner(selectedNode.getData(), singlePlanner);
                    }
                    else{
                        skipPlannerProblemButton.setVisible(true);
                        this.solveProblem(selectedNode.getData(), planners);
                    }
                    break;
            }


       }



    }

    private void solveAllProjects(final ItTreeNode selectedNode, final List<Element> planners){
        currentThread = new Thread(){
            public void run() {
                solveResult = null;
                List<Element> projects = new ArrayList<Element>();
                for (int i = 0; i < selectedNode.getChildCount(); i++) {
                    ItTreeNode projectNode = (ItTreeNode)selectedNode.getChildAt(i);
                    Element project = projectNode.getData();
                    projects.add(project);
                }

                if (projects.size() > 0){
                    solveResult = solveProjectsWithPlannersList(projects, planners);
                    setSolveProblemButton();
                    //XMLUtilities.printXML(solveResult);

                    String report = PlanAnalyzer.generatePlannersComparisonReport(solveResult);
                    String comparisonReport = PlanAnalyzer.generateFullPlannersComparisonReport(solveResult);

                    appendOutputPanelText(" (!) Experiment done! \n");
                    planSimStatusBar.setText("Status: Experiment done!");

                    //Save Comparison Report file
                    saveFile("resources/report/Report.html", comparisonReport);

                    setPlanInfoPanelText(report);
                    setPlanEvaluationInfoPanelText("");
                }else{
                    setSolveProblemButton();
                }


           }
        };
        currentThread.start();
        // changes the button action command
        solveProblemButton.setActionCommand("stop");
        solveProblemButton.setText("Stop");
        solveProblemButton.setIcon(new ImageIcon("resources/images/stop.png"));
    }

    private void solveAllDomainsFromProject(final Element project, final List<Element> planners){
        currentThread = new Thread(){
           public void run() {
               //solveResult = null;
               //solveResult = solveProjectProblemsWithPlannersList(project, planners);

               //preparing the same struture projects, project, domains, domain, problems, problem, plans, xmlPlan
               solveResult = new Element ("projects");
               Element projectRef = solveProjectProblemsWithPlannersList(project, planners);
               if (projectRef!=null){
                    solveResult.addContent(projectRef);
               }
               setSolveProblemButton();

               ///XMLUtilities.printXML(solveResult);
               String report = PlanAnalyzer.generatePlannersComparisonReport(solveResult);
               String comparisonReport = PlanAnalyzer.generateFullPlannersComparisonReport(solveResult);

               //Save Comparison Report file
               saveFile("resources/report/Report.html", comparisonReport);

               setPlanInfoPanelText(report);
               setPlanEvaluationInfoPanelText("");



           }
        };
        currentThread.start();
        // changes the button action command
        solveProblemButton.setActionCommand("stop");
        solveProblemButton.setText("Stop");
        solveProblemButton.setIcon(new ImageIcon("resources/images/stop.png"));
    }

    private void solveAllProblemsFromDomain(final Element domain, final List<Element> planners){
       currentThread = new Thread(){
           public void run() {
               //solveResult = null;
               //solveResult = solveDomainProblemsWithPlannersList(domain.getDocument().getRootElement(), domain, null, planners);

               //preparing the same struture projects, project, domains, domain, problems, problem, plans, xmlPlan
               solveResult = new Element ("projects");
               Element projectRef = new Element("project");
               Element project = domain.getDocument().getRootElement();
               projectRef.addContent((Element)project.getChild("name").clone());
               Element domainsRef = new Element("domains");
               projectRef.addContent(domainsRef);

               solveResult.addContent(projectRef);

               Element domainRef = solveDomainProblemsWithPlannersList(domain.getDocument().getRootElement(), domain, null, planners);
               if(domainRef!=null){
                domainsRef.addContent(domainRef);
               }

               setSolveProblemButton();

               //XMLUtilities.printXML(solveResult);
               String report = PlanAnalyzer.generatePlannersComparisonReport(solveResult);
               String comparisonReport = PlanAnalyzer.generateFullPlannersComparisonReport(solveResult);
               //Save Comparison Report file
               saveFile("resources/report/Report.html", comparisonReport);

               setPlanInfoPanelText(report);
               setPlanEvaluationInfoPanelText("");


           }
       };
       currentThread.start();
       // changes the button action command
       solveProblemButton.setActionCommand("stop");
       solveProblemButton.setText("Stop");
       solveProblemButton.setIcon(new ImageIcon("resources/images/stop.png"));
    }

    private void solveProblem(final Element problem, final List<Element> planners){
       currentThread = new Thread(){
           public void run() {
               Element project = problem.getDocument().getRootElement();
               Element domain = problem.getParentElement().getParentElement();
               String pddlDomain = "";
               String pddlProblem = "";
               //solveResult = null;
               //solveResult = solveProblemWithPlannersList(project, domain, problem, pddlDomain, pddlProblem, planners);

               //preparing the same struture projects, project, domains, domain, problems, problem, plans, xmlPlan
               solveResult = new Element ("projects");
               Element projectRef = new Element("project");
               projectRef.addContent((Element)project.getChild("name").clone());
               Element domainsRef = new Element("domains");
               projectRef.addContent(domainsRef);
               Element domainRef = new Element("domain");
               domainRef.setAttribute("id", domain.getAttributeValue("id"));
               domainRef.addContent((Element)domain.getChild("name").clone());
               domainsRef.addContent(domainRef);
               Element problemsRef = new Element("problems");
               domainRef.addContent(problemsRef);

               solveResult.addContent(projectRef);

               Element problemRef = solveProblemWithPlannersList(project, domain, problem, pddlDomain, pddlProblem, planners);

               if (problemRef!=null){
                problemsRef.addContent(problemRef);
               }

               setSolveProblemButton();

               //XMLUtilities.printXML(solveResult);
               String report = PlanAnalyzer.generatePlannersComparisonReport(solveResult);
               String comparisonReport = PlanAnalyzer.generateFullPlannersComparisonReport(solveResult);

               //Save Comparison Report file
               saveFile("resources/report/Report.html", comparisonReport);

               setPlanInfoPanelText(report);
               setPlanEvaluationInfoPanelText("");


           }
       };
       currentThread.start();
       // changes the button action command
       solveProblemButton.setActionCommand("stop");
       solveProblemButton.setText("Stop");
       solveProblemButton.setIcon(new ImageIcon("resources/images/stop.png"));
    }


    public Element solveProblemWithPlannersList(Element project, Element domain, Element problem, String pddlDomain, String pddlProblem, List<Element> planners){
        Element container = null;

		if(project != null && problem != null){
            container = new Element("problem");
            container.setAttribute("id", problem.getAttributeValue("id"));
            container.addContent((Element) problem.getChild("name").clone());

            //add metrics (domain and problem level) to the problem reference (container)
            Element mainMetrics = PlanSimulator.createMetricsNode(problem, domain);
            container.addContent(mainMetrics);

            Element thePlans = new Element("plans");
            container.addContent(thePlans);


            //just in case the pddl is empty or null;
            if (pddlDomain == null || pddlDomain.trim().equals("") || pddlProblem == null || pddlProblem.trim().equals("")){

                String pddlVersion = pddlButtonsGroup.getSelection().getActionCommand();
                // generate PDDL domain							// root element
                Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(project, pddlVersion, null);
                // generate PDDL problem
                Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, pddlVersion);

                ToXPDDL.adjustRequirements(xpddlDomain, xpddlProblem, pddlVersion);

                pddlDomain = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "");
                pddlProblem = XPDDLToPDDL.parseXPDDLToPDDL(xpddlProblem, "");
            }


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

            exe = new ExecPlanner(null, domainFile.getPath(), problemFile.getPath(), true);

            exe.setXMLDomain(problem.getParentElement().getParentElement());
            exe.setXMLProblem(problem);
            exe.setProblemName(problem.getChildText("name"));
            exe.setDomainName(problem.getParentElement().getParentElement().getChildText("name"));
            exe.setProjectName(project.getChildText("name"));
            exe.setShowReport(false);


            appendOutputPanelText(">> Solving " + problem.getChildText("name") + " with selected planner(s) \n");


            JLabel status = ItSIMPLE.getInstance().getPlanSimStatusBar();

            //TODO: check if each planners is enabled to be included in the run all procedure
            for (Iterator<Element> it = planners.iterator(); it.hasNext();) {

                //stop this 'for' if the user press STOP
                if(stopRunningPlanners){
                    break;
                }

                Element planner = it.next();
                //hideSimProgressBar();
                status.setText("Status: Solving planning problem ...");

                /// set start datetime
                DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
                Date date = new Date();
                String dateTime = dateFormat.format(date);

                try {
                    appendOutputPanelText("\n     Solving " + problem.getChildText("name") + " with planner "+planner.getChildText("name") + " \n");
                    status.setText("Status: Solving planning problem " + problem.getChildText("name")+" with planner "+planner.getChildText("name")+"... \n");
                    skipPlannerProblemButton.setToolTipText("<html>Skip planning:<br /><strong>Planner</strong>:"+planner.getChildText("name")+"<br/><strong>Problem</strong>:"+exe.getProblemName()+"<br/><strong>Started at</strong>:"+dateTime+"</html>");
                    exe.setChosenPlanner(planner);
                    //Element result = exe.solveProblem();


                    //New approach if timeout and theads

                    //Garantee that there will be the minimal information in a empty plan
                    exe.setEmptyPlan();

                    Element result = null;
                    plannerThread = new Thread(){
                        public void run() {
                            exe.solveProblem();
                        }
                    };

                    //TODO:
                    // 1. We must get if it was time out or not ant put it in the report (the result xml)

                   // Master Time-Out
                    boolean masterTimeOutEnabled = itPlanners.getChild("settings").getChild("timeout").getAttributeValue("enabled").equals("true");
                    long masterTimeOutValue = 0;
                    if (!itPlanners.getChild("settings").getChildText("timeout").trim().equals("")){
                        masterTimeOutValue = Long.parseLong(itPlanners.getChild("settings").getChildText("timeout"));
                    }

                    // Local Time-out
                    boolean localTimeOutEnabled = planner.getChild("settings").getChild("timeout").getAttributeValue("enabled").equals("true");
                    long localTimeOutValue = 0;

                    if (!planner.getChild("settings").getChildText("timeout").trim().equals("")){
                        localTimeOutValue = Long.parseLong(planner.getChild("settings").getChildText("timeout"));
                    }

                    // Time-out value
                    long timeout = 0;
                    if (localTimeOutEnabled)
                        timeout = localTimeOutValue;
                    else if (masterTimeOutEnabled)
                        timeout = masterTimeOutValue;

                    timeout = timeout*1000; // seconds to milliseconds
                    if (timeout > 0){
                        simProgressBar.setVisible(true);
                        simProgressBar.setValue(0);
                        String barmax = Long.toString(timeout/1000);
                        simProgressBar.setMaximum(Integer.parseInt(barmax));
                        simProgressBar.setString("0 of "+ barmax +" (s)");

                        //System.out.println(barmax);

                        TimeKiller timeKiller = new TimeKiller(plannerThread, timeout); // Timeout
                        long start = System.currentTimeMillis();
                        plannerThread.start();
                        //wait to finish normaly or by timeout
                        long timespent = 0;
                        String timespentStr = "0";
                        DecimalFormat df = new DecimalFormat("###.#");
                        while(!timeKiller.isFinished() && plannerThread.isAlive() && !forceFinish){
                            Thread.sleep(200);  //sleep

                            timespent = System.currentTimeMillis() - start;
                            //timespentStr = Long.toString((System.currentTimeMillis() - start)/1000);
                            timespentStr = Long.toString((timespent)/1000);
                            int barvalue = Integer.parseInt(timespentStr);
                            simProgressBar.setValue(barvalue);
                            String percentage = df.format(simProgressBar.getPercentComplete()*100);
                            simProgressBar.setString(timespentStr + " of "+ barmax +" (s) - (" + percentage + "%)");
                            //simProgressBar.setToolTipText(timespentStr + " of "+ barmax +" (s)");
                            simProgressBar.repaint();
                        }

                       
                        if (forceFinish){
                            timeKiller.setFinished(true);
                            exe.destroyProcess();
                            plannerThread.interrupt();
                            timeKiller.done();
                            forceFinish = false;
                            //wait for plannerThread to finish
                            while (plannerThread.isAlive()) {
                                Thread.sleep(500); //sleep to finish all killing
                            }
                            
                            //set the reason (skipped) and time in the statistics
                            Element plan = exe.getPlan();
                            //System.out.println("forced " + timespentStr +" - "+ planner.getChildText("name"));
                            Element statistics = plan.getChild("statistics");
                            statistics.getChild("toolTime").setText(timespentStr);
                            Element plannerstatus = statistics.getChild("forcedQuit");
                            plannerstatus.setText("skipped");

                            //set datetime
                            dateTime = dateFormat.format(date);
                            plan.getChild("datetime").setText(dateTime);

                        }
                        else if (timeKiller.isTimeoutReached() || timespent >= timeout){
                            timeKiller.setFinished(true);
                            exe.destroyProcess();
                            plannerThread.interrupt();
                            timeKiller.done();
                            //wait for plannerThread to finish
                            while (plannerThread.isAlive()) {
                                Thread.sleep(500); //sleep to finish all killing
                            }

                            //set the reason (timeout) and time in the statistics
                            Element plan = exe.getPlan();
                            String thetimeoutstr = Long.toString(timeout/1000);
                            //System.out.println("timeout "+ thetimeoutstr+" - "+ planner.getChildText("name"));
                            Element statistics = plan.getChild("statistics");
                            statistics.getChild("toolTime").setText(thetimeoutstr);
                            Element plannerstatus = statistics.getChild("forcedQuit");
                            plannerstatus.setText("timeout");
                            
                            //set datetime
                            dateTime = dateFormat.format(date);
                            plan.getChild("datetime").setText(dateTime);
                        }

                    }
                    else{ // without time-out
                        simTimeSpent.setText("");
                        long start = System.currentTimeMillis();
                        plannerThread.start();
                        long timespent = 0;
                        String timespentStr = "0";
                        while(plannerThread.isAlive() && !forceFinish){
                            Thread.sleep(200);  //sleep
                            timespent = System.currentTimeMillis() - start;
                            timespentStr = Long.toString(timespent/1000);
                            simTimeSpent.setText("  Time: " + timespentStr+" (s)");
                            //System.out.println(timespent/1000);
                        }

                        Element plan = exe.getPlan();
                        if (forceFinish){
                            plannerThread.interrupt();
                            forceFinish = false;
                            //set the reason and time in the statistics
                            Element statistics = plan.getChild("statistics");
                            statistics.getChild("toolTime").setText(timespentStr);
                            Element plannerstatus = statistics.getChild("forcedQuit");
                            plannerstatus.setText("skipped");
                        }
                    }
                    hideSimProgressBar();
                    simTimeSpent.setText("");

                    //garantee to destroy process
                    exe.destroyProcess();

                    //get resulting plan (might have plan)
                    result = exe.getPlan();
                    //End of new approach if timeout and theads

                    if (result != null){
                        //Calculate metrics dataset. Add metrics data to the plan "xmlPlan/metrics"
                        Element metrics = PlanSimulator.createMetricsNode(problem, domain);
                        if (metrics != null && metrics.getChildren().size() > 0 && result.getChild("plan").getChildren().size() > 0) {
                            appendOutputPanelText(">> Calculating metrics for the plan given by " + planner.getChildText("name") + ". \n");
                            PlanSimulator.createMetricDatasets(metrics, result, problem, domain, null);
                        }
                        result.addContent(metrics);

                        thePlans.addContent((Element)result.clone());
                    }
                    else{
                        appendOutputPanelText(" ## No plan from " + planner.getChildText("name") + "! \n");
                    }

                    appendOutputPanelText(" (!) Done with "+planner.getChildText("name")+"! \n");
                    skipPlannerProblemButton.setToolTipText("");

                } catch (Exception e) {

                }
            }


            status.setText("Status: Done solving planning problem "+problem.getChildText("name")+" with planner(s)!");


            appendOutputPanelText(">> Done solving problem "+problem.getChildText("name")+" with selected planner(s)! \n");
            


		}

        return container;
	}


    public Element solveDomainProblemsWithPlannersList(Element project, Element domain, Element xpddlDomain, List<Element> planners){
        Element container = null;
        if(project != null && domain != null){
            container = new Element("domain");
            container.setAttribute("id", domain.getAttributeValue("id"));
            container.addContent((Element) domain.getChild("name").clone());
            Element containerProblems = new Element("problems");
            container.addContent(containerProblems);
            //get all problems
            List<Element> problems = null;
            try {
                XPath path = new JDOMXPath("planningProblems/problem");
                problems = path.selectNodes(domain);
            } catch (JaxenException e1) {
                e1.printStackTrace();
            }
            if (problems != null){
                //ger xddldomain if it is null
                String pddlVersion = pddlButtonsGroup.getSelection().getActionCommand();
                if(xpddlDomain == null){
                    // generate PDDL domain							// root element
                    xpddlDomain = ToXPDDL.XMLToXPDDLDomain(project, pddlVersion, null);
                }

                appendOutputPanelText(">> Starting  planning with domain " + domain.getChildText("name") + " with selected planner(s) \n");
                //solve all problems
                for (Iterator<Element> it = problems.iterator(); it.hasNext();) {
                    Element problem = it.next();

                    if (stopRunningPlanners){
                        break;
                    }

                    // generate PDDL problem
                    Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, pddlVersion);
                    ToXPDDL.adjustRequirements(xpddlDomain, xpddlProblem, pddlVersion);

                    String pddlDomain = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "");
                    String pddlProblem = XPDDLToPDDL.parseXPDDLToPDDL(xpddlProblem, "");

                    Element result = solveProblemWithPlannersList(project, domain, problem, pddlDomain, pddlProblem, planners);
                    if (result !=null){
                        containerProblems.addContent(result);
                    }

                }
                appendOutputPanelText(">> Done with domain " + domain.getChildText("name") + "! \n");
            }

        }

        return container;
    }


    public Element solveProjectProblemsWithPlannersList(Element project, List<Element> planners){
        Element container = null;
        if(project != null){
            container = new Element("project");
            //container.setAttribute("id", project.getAttributeValue("id"));
            container.addContent((Element) project.getChild("name").clone());
            Element containerDomains = new Element("domains");
            container.addContent(containerDomains);
            //get all problems
            List<Element> domains = null;
            try {
                XPath path = new JDOMXPath("diagrams/planningDomains/domain");
                domains = path.selectNodes(project);
            } catch (JaxenException e1) {
                e1.printStackTrace();
            }
            if (domains != null){
                String pddlVersion = pddlButtonsGroup.getSelection().getActionCommand();
                // generate PDDL domain							// root element
                Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(project, pddlVersion, null);

                appendOutputPanelText(">> Starting planning with project " + project.getChildText("name") + " with selected planner(s) \n");

                //solve all problems in all domains
                for (Iterator<Element> it = domains.iterator(); it.hasNext();) {

                    if (stopRunningPlanners){
                        break;
                    }
                    
                    Element domain = it.next();
                    //get domain
                    Element result = solveDomainProblemsWithPlannersList(project, domain, xpddlDomain, planners);
                    if (result !=null){
                        containerDomains.addContent(result);
                    }

                }
                appendOutputPanelText(">> Done with project " + project.getChildText("name") + "! \n");
            }

        }

        return container;
    }


    public Element solveProjectsWithPlannersList(List<Element> projects, List<Element> planners){
        Element container = null;
        if(projects != null && projects.size() > 0){
            container = new Element("projects");

            //solve all problems in all domains
            for (Iterator<Element> it = projects.iterator(); it.hasNext();) {
                Element project = it.next();
                //get domain
                appendOutputPanelText(">> Starting planning with all projects. \n");
                Element result = solveProjectProblemsWithPlannersList(project, planners);
                if (result !=null){
                    container.addContent(result);
                }
                appendOutputPanelText(">> Planning with all projects done! \n");

            }
        }
        return container;
    }


    public void solveProblemWithSinglePlanner(Element problem, Element chosenPlanner){
        if(problem != null){
            // clear plan list and plan info pane
            setPlanList(null);
            setPlanInfoPanelText("");
            setPlanEvaluationInfoPanelText("");
            cleanupPlanDatabaseReference();

            Element domainProject = problem.getDocument().getRootElement();
            Element domain = problem.getParentElement().getParentElement();

            String pddlVersion = pddlButtonsGroup.getSelection().getActionCommand();

            // generate PDDL domain
            Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(domainProject, pddlVersion, null);
            //XMLUtilities.printXML(xpddlDomain);

            // generate PDDL problem
            Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, pddlVersion);
            //XMLUtilities.printXML(xpddlProblem);

            //Change domain requirements (if necessary) based on the chosen problem
            ToXPDDL.adjustRequirements(xpddlDomain, xpddlProblem, pddlVersion);

            String pddlDomain = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "");
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

            exe = new ExecPlanner(chosenPlanner, domainFile.getPath(), problemFile.getPath(), false);

            exe.setXMLDomain(problem.getParentElement().getParentElement());
            exe.setXMLProblem(problem);
            exe.setProblemName(problem.getChildText("name"));
            exe.setDomainName(domain.getChildText("name"));
            exe.setProjectName(domainProject.getChildText("name"));
            //exe.setEmptyPlan();

            currentThread = new Thread(exe);
            currentThread.start();

            // changes the button action command
            solveProblemButton.setActionCommand("stop");
            solveProblemButton.setText("Stop");
            solveProblemButton.setIcon(new ImageIcon("resources/images/stop.png"));


        }

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
                        importPlanButton.setToolTipText("Import plan from xml file");

                        exportPlanButton = new JButton(exportPlanAction);
                        exportPlanButton.setToolTipText("Export current plan");

                        checkPlanValidityButton = new JButton(checkPlanValidity);
                        checkPlanValidityButton.setToolTipText("<html>Validate plan with validator VAL <br>(based on the generated PDDL model).</html>");

                        //quickEvaluateButton = new JButton(quickEvaluation);
                        //quickEvaluateButton.setToolTipText("Quick evaluation of the selected plan");

                        //fullEvaluationButton = new JButton(generateEvaluatioReport);
                        //fullEvaluationButton.setToolTipText("<html>Full evaluation of the selected plan. <br> Generate a plan evaluation in the planReport. <br> This is restricted to non-time-based domain only.</html>");
                        //fullEvaluationButton.setText("");

                        addPlanActionButton.setEnabled(false);
                        removePlanActionButton.setEnabled(false);
                        editPlanActionButton.setEnabled(false);
                        checkPlanValidityButton.setEnabled(false);
                        //quickEvaluateButton.setEnabled(false);
                        //fullEvaluationButton.setEnabled(false);
                        importPlanButton.setEnabled(false);
                        exportPlanButton.setEnabled(false);

                        JToolBar planListToolBar = new JToolBar();
                        planListToolBar.add(addPlanActionButton);
                        planListToolBar.add(removePlanActionButton);
                        planListToolBar.add(editPlanActionButton);
                        planListToolBar.addSeparator();
                        planListToolBar.add(checkPlanValidityButton);
                        //planListToolBar.add(quickEvaluateButton);
                        //planListToolBar.add(fullEvaluationButton);
                        planListToolBar.addSeparator();
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
                        checkPlanValidityButton.setEnabled(true);
                        //quickEvaluateButton.setEnabled(true);
                        //fullEvaluationButton.setEnabled(true);
			exportPlanButton.setEnabled(true);

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
                        planListFramePanel.repaint();
                        planList.repaint();
                        planList.revalidate();


		} else {
                    // do nothing, set the button disabled
                    checkPlanValidityButton.setEnabled(false);
                    //quickEvaluateButton.setEnabled(false);
                    //fullEvaluationButton.setEnabled(false);
                    exportPlanButton.setEnabled(false);
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


                        ItFramePanel variableSelectionPanel = new ItFramePanel(".: Select variables to be tracked", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
                        //variableSelectionPanel.setBackground(new Color(151,151,157));

                        JSplitPane split = new JSplitPane();
			split.setContinuousLayout(true);
			split.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
                        split.setDividerLocation(2*screenSize.height/3);
			
			split.setDividerSize(8);
			//split.setPreferredSize(new Dimension(screenSize.width/4-20, screenSize.height/2 - 50));
			//split.setPreferredSize(new Dimension(screenSize.width/4-20, 120));
			split.setLeftComponent(new JScrollPane(variablesPlanTree));
			split.setRightComponent(new JScrollPane(selectedVariablesPlanTree));

                        variableSelectionPanel.setContent(split, false);
                        //variableSelectionPanel.setParentSplitPane()

                        //JPanel variableSelectionPanel  = new JPanel(new BorderLayout());
			//variableSelectionPanel.add(new JScrollPane(variablesPlanTree), BorderLayout.CENTER);
			//variableSelectionPanel.add(new JScrollPane(selectedVariablesPlanTree), BorderLayout.EAST);

                        ItFramePanel variableGraphPanel = new ItFramePanel(".: Chart", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
                        variableGraphPanel.setContent(chartsPanel, true);


                        JSplitPane mainvariablesplit = new JSplitPane();
			mainvariablesplit.setContinuousLayout(true);
			mainvariablesplit.setOrientation(JSplitPane.VERTICAL_SPLIT);
                        mainvariablesplit.setDividerLocation(150);
			mainvariablesplit.setDividerSize(8);
			//mainvariablesplit.setPreferredSize(new Dimension(screenSize.width/4-20, screenSize.height/2 - 50));
			mainvariablesplit.setTopComponent(variableSelectionPanel);
			mainvariablesplit.setBottomComponent(variableGraphPanel);


			// main charts panel - used to locate the tool bar above the charts panel
			JPanel mainChartsPanel = new JPanel(new BorderLayout());
			mainChartsPanel.add(chartsToolBar, BorderLayout.NORTH);
			//mainChartsPanel.add(new JScrollPane(chartsPanel), BorderLayout.CENTER);
                        mainChartsPanel.add(mainvariablesplit, BorderLayout.CENTER);





                        //Results
                        planInfoEditorPane = new JEditorPane();
                        planInfoEditorPane.setContentType("text/html");
                        planInfoEditorPane.setEditable(false);
                        planInfoEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
                        planInfoEditorPane.setBackground(Color.WHITE);


                        JPanel resultsPanel = new JPanel(new BorderLayout());

                        JToolBar resultsToolBar = new JToolBar();
                        resultsToolBar.setRollover(true);

                        JButton planReportButton = new JButton("View Full Report", new ImageIcon("resources/images/viewreport.png"));
                        planReportButton.setToolTipText("<html>View full plan report.<br> For multiple plans you will need " +
                                "access to the Internet.<br> The components used in the report require such access (no data is " +
                                "sent through the Internet).</html>");
                        planReportButton.addActionListener(new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                            //Opens html with defaut browser
                                            String path = "resources/report/Report.html";
                                            File report = new File(path);
                                            path = report.getAbsolutePath();
                                            try {
                                                BrowserLauncher launcher = new BrowserLauncher();
                                                launcher.openURLinBrowser("file://"+path);
                                            } catch (BrowserLaunchingInitializingException ex) {
                                                Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                                appendOutputPanelText("ERROR. Problem while trying to open the default browser. \n");
                                            } catch (UnsupportedOperatingSystemException ex) {
                                                Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                                appendOutputPanelText("ERROR. Problem while trying to open the default browser. \n");
                                            }
                            }
                        });
                        resultsToolBar.add(planReportButton);

                        resultsToolBar.addSeparator();
                        JButton planReportDataButton = new JButton("Save Report Data", new ImageIcon("resources/images/savePDDL.png"));
                        planReportDataButton.setToolTipText("<html>Save report data to file</html>");
                        planReportDataButton.addActionListener(new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                //Save report data
                                if (solveResult != null){
                                    Element lastOpenFolderElement = itSettings.getChild("generalSettings").getChild("lastOpenFolder");
                                    JFileChooser fc = new JFileChooser(lastOpenFolderElement.getText());
                                    fc.setDialogTitle("Save Report Data");
                                    fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                    fc.setFileFilter(new XMLFileFilter());

                                    int returnVal = fc.showSaveDialog(ItSIMPLE.this);
                                    if (returnVal == JFileChooser.APPROVE_OPTION){
                                        File selectedFile = fc.getSelectedFile();
                                        String path = selectedFile.getPath();

                                        if (!path.toLowerCase().endsWith(".xml")){
                                            path += ".xml";
                                        }
                                        //save file (xml)
                                        try {
                                            FileWriter file = new FileWriter(path);
                                            file.write(XMLUtilities.toString(solveResult));
                                            file.close();
                                        } catch (IOException e1) {
                                            e1.printStackTrace();
                                        }

                                        //Save as a last open folder
                                        String folder = selectedFile.getParent();
                                        //Element lastOpenFolderElement = itSettings.getChild("generalSettings").getChild("lastOpenFolder");
                                        lastOpenFolderElement.setText(folder);
                                        XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());



                                        //Ask if the user wants to save plans individually too.
                                        boolean needToSavePlans = false;
                                        int option = JOptionPane.showOptionDialog(instance,
                                                "<html><center>Do you also want to save the plans" +
                                                "<br>in individual files?</center></html>",
                                                "Save plans",
                                                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                                        switch(option){
                                            case JOptionPane.YES_OPTION:{
                                                needToSavePlans = true;
                                            }
                                            break;
                                            case JOptionPane.NO_OPTION:{
                                                needToSavePlans = false;
                                            }
                                            break;
                                        }

                                        if (needToSavePlans){
                                            //Close Open tabs
                                            List<?> problems = null;
                                            try {
                                                XPath ppath = new JDOMXPath("project/domains/domain/problems/problem");
                                                problems = ppath.selectNodes(solveResult);
                                            } catch (JaxenException e2) {
                                                e2.printStackTrace();
                                            }

                                            for (int i = 0; i < problems.size(); i++){
                                                Element problem = (Element)problems.get(i);
                                                //create a folder for each problem and put all plans inside as xml files
                                                String folderName = problem.getChildText("name");
                                                String folderPath = selectedFile.getAbsolutePath().replace(selectedFile.getName(), folderName);
                                                //System.out.println(folderPath);
                                                File planfolder = new File(folderPath);
                                                boolean canSavePlan = false;
                                                try {
                                                    if (planfolder.mkdir()){
                                                        System.out.println("Directory '" + folderPath + "' created.");
                                                        canSavePlan = true;
                                                    }else{
                                                        System.out.println("Directory '" + folderPath + "' was not created.");
                                                    }

                                                } catch (Exception ep) {
                                                    ep.printStackTrace();
                                                }

                                                if (canSavePlan){
                                                    Element plans = problem.getChild("plans");
                                                    for (Iterator<Element> it = plans.getChildren("xmlPlan").iterator(); it.hasNext();) {
                                                        Element eaplan = it.next();
                                                        Element theplanner = eaplan.getChild("planner");
                                                        //save file (xml)
                                                        String planFileName = "solution" + theplanner.getChildText("name") + "-" + theplanner.getChildText("version") + "-" + Integer.toString(plans.getChildren().indexOf(eaplan)) + ".xml";
                                                        String planPath = folderPath + File.separator + planFileName;

                                                        try {
                                                            FileWriter planfile = new FileWriter(planPath);
                                                            planfile.write(XMLUtilities.toString(eaplan));
                                                            planfile.close();
                                                            System.out.println("File '" + planPath + "' created.");
                                                        } catch (IOException e1) {
                                                            e1.printStackTrace();
                                                        }

                                                    }

                                                }



                                            }
                                        }



                                    }
                                }
                                else{
                                    appendOutputPanelText(">> No report data available to save! \n");
                                }

                            }
                        });
                        resultsToolBar.add(planReportDataButton);

                        JButton openPlanReportDataButton = new JButton("Open Report Data", new ImageIcon("resources/images/openreport.png"));
                        openPlanReportDataButton.setToolTipText("<html>Open report data to file</html>");
                        openPlanReportDataButton.addActionListener(new java.awt.event.ActionListener() {
                            @Override
                            public void actionPerformed(ActionEvent e) {
                                planSimStatusBar.setText("Status: Opening File...");
                                appendOutputPanelText(">> Opening File... \n");
                                //Open report data
                                Element lastOpenFolderElement = itSettings.getChild("generalSettings").getChild("lastOpenFolder");
                                JFileChooser fc = new JFileChooser(lastOpenFolderElement.getText());
                                fc.setDialogTitle("Open Report Data");
                                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                                fc.setFileFilter(new XMLFileFilter());

                                int returnVal = fc.showOpenDialog(ItSIMPLE.this);
                                if (returnVal == JFileChooser.APPROVE_OPTION){

                                    File file = fc.getSelectedFile();
                                    // Get itSIMPLE itSettings from itSettings.xml
                                    org.jdom.Document resultsDoc = null;
                                    try{
                                        resultsDoc = XMLUtilities.readFromFile(file.getPath());
                                        solveResult = resultsDoc.getRootElement();
                                        //XMLUtilities.printXML(solveResult);
                                        if (solveResult.getName().equals("projects")){

                                            String report = PlanAnalyzer.generatePlannersComparisonReport(solveResult);
                                            String comparisonReport = PlanAnalyzer.generateFullPlannersComparisonReport(solveResult);
                                            //Save Comparison Report file
                                            saveFile("resources/report/Report.html", comparisonReport);
                                            setPlanInfoPanelText(report);
                                            setPlanEvaluationInfoPanelText("");
                                            appendOutputPanelText(">> Report data read! \n");

                                            //My experiments
                                            PlanAnalyzer.myAnalysis(itPlanners.getChild("planners"), solveResult);
                                        }
                                    }
                                    catch(Exception e1){
                                        e1.printStackTrace();
                                    }

                                    //Save as a last open folder
                                    String folder = fc.getSelectedFile().getParent();
                                    lastOpenFolderElement.setText(folder);
                                    XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());

                                }
                                else{
                                    planSimStatusBar.setText("Status:");
                                    appendOutputPanelText(">> Canceled \n");
                                }

                            }
                        });
                        resultsToolBar.add(openPlanReportDataButton);



                    JButton compareProjectReportDataButton = new JButton("Compare Project Data", new ImageIcon("resources/images/compare.png"));
                    compareProjectReportDataButton.setToolTipText("<html>Compare different project report data <br> This is commonly use to compare diferent domain models with different adjustments.<br>" +
                            "One project data must be chosen as a reference; others will be compared to this referencial one.</html>");
                    compareProjectReportDataButton.addActionListener(new java.awt.event.ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {


                            final ProjectComparisonDialog dialog = new ProjectComparisonDialog();
                            dialog.setVisible(true);

                            final List<String> files = dialog.getFiles();

                            if (files.size() > 1){

                                new Thread(){
                                    public void run() {
                                        appendOutputPanelText(">> Project comparison report requested. Processing... \n");

                                        planSimStatusBar.setText("Status: Reading files ...");
                                        appendOutputPanelText(">> Reading files ... \n");

                                        //base project file
                                        String baseFileName = files.get(0);
                                        appendOutputPanelText(">> Reading file '"+baseFileName+"' \n");
                                        org.jdom.Document baseProjectDoc = null;
                                        try{
                                            baseProjectDoc = XMLUtilities.readFromFile(baseFileName);
                                        }
                                        catch(Exception ec){
                                            ec.printStackTrace();
                                        }
                                        Element baseProject = null;
                                        if (baseProjectDoc != null){
                                            baseProject = baseProjectDoc.getRootElement().getChild("project");
                                        }

                                        //The comparible projects
                                        List<Element> comparableProjects = new ArrayList<Element>();

                                        for (int i = 1; i < files.size(); i++) {
                                            String eafile = files.get(i);
                                            appendOutputPanelText(">> Reading file '"+eafile+"' \n");
                                            org.jdom.Document eaProjectDoc = null;
                                            try{
                                                eaProjectDoc = XMLUtilities.readFromFile(eafile);
                                            }
                                            catch(Exception ec){
                                                ec.printStackTrace();
                                            }
                                            if (eaProjectDoc != null){
                                                comparableProjects.add(eaProjectDoc.getRootElement().getChild("project"));
                                            }

                                        }
                                        appendOutputPanelText(">> Files read. Building report... \n");

                                        String comparisonReport = PlanAnalyzer.generateProjectComparisonReport(baseProject, comparableProjects);
                                        saveFile("resources/report/Report.html", comparisonReport);
                                        appendOutputPanelText(">> Project comparison report generated. Press 'View Full Report'\n");
                                        appendOutputPanelText(" \n");

                                    }
                                }.start();


                            }





                        }
                    });
                    resultsToolBar.add(compareProjectReportDataButton);



                    resultsPanel.add(resultsToolBar, BorderLayout.NORTH);
                    resultsPanel.add(new JScrollPane(planInfoEditorPane), BorderLayout.CENTER);


            JTabbedPane planAnalysisTabbedPane = new JTabbedPane();
            planAnalysisTabbedPane.addTab("Results",  resultsPanel);
            planAnalysisTabbedPane.addTab("Variable Tracking", mainChartsPanel);
            planAnalysisTabbedPane.addTab("Movie Maker", getMovieMakerPanel());
            planAnalysisTabbedPane.addTab("Plan Evaluation", getPlanEvaluationPanel());
            
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
	 * This method initializes planEvaluationPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPlanEvaluationPanel(){
            //TODO:

		if(planEvaluationPanel == null){
			planEvaluationPanel = new JPanel(new BorderLayout());
                        
                        JPanel contentPanel = new JPanel(new BorderLayout());

                        //Plan evaluation summary
                        planEvaluationInfoEditorPane = new JEditorPane();
                        planEvaluationInfoEditorPane.setContentType("text/html");
                        planEvaluationInfoEditorPane.setEditable(false);
                        planEvaluationInfoEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
                        planEvaluationInfoEditorPane.setBackground(Color.WHITE);
                        planEvaluationInfoEditorPane.setPreferredSize(new Dimension(600, 100));
                        contentPanel.add(new JScrollPane(planEvaluationInfoEditorPane), BorderLayout.CENTER);
                        //contentPanel.add(new JScrollPane(planEvaluationInfoEditorPane), BorderLayout.NORTH);



                        //metric table
                        // create parameters table
//                        JScrollPane scrollParamPane = new JScrollPane(getMetricsTable());
//                        JPanel paramPane = new JPanel(new BorderLayout());
//                        paramPane.add(scrollParamPane, BorderLayout.CENTER);
//                        paramPane.setPreferredSize(new Dimension(600, 210));
//                        //add(paramPane, BorderLayout.CENTER);
//                        plannerSettingPanel.add(paramPane, BorderLayout.CENTER);




//                        //cost and overall plan evaluation panel
//                        FormLayout layout = new FormLayout(
//                                        "pref, 4px, 100px", // columns
//                                        "pref, 4px, pref"); // rows
//                        JPanel costoverallPanel = new JPanel(layout);
//
//                        //plan cost
//                        JLabel costLabel = new JLabel("Plan Cost:");
//                        JLabel thecostLabel = new JLabel("...");
//                        //plan overall evaluation
//                        JLabel evaluationLabel = new JLabel("<html><strong>Plan evaluation:</strong></html>");
//                        overallPlanEvaluationValue = new JTextField(30);
//                        JTextFieldFilter filter = new JTextFieldFilter(JTextFieldFilter.FLOAT);
//                        filter.setNegativeAccepted(false);
//                        //filter.setLimit(3);
//                        overallPlanEvaluationValue.setDocument(filter);
//                        overallPlanEvaluationValue.setColumns(9);
//
//                        CellConstraints cc = new CellConstraints();
//                        costoverallPanel.add(costLabel, cc.xy (1, 1));
//                        costoverallPanel.add(thecostLabel, cc.xy(3, 1));
//                        costoverallPanel.add(evaluationLabel, cc.xy(1, 3));
//                        costoverallPanel.add(overallPlanEvaluationValue, cc.xy(3, 3));
//                        contentPanel.add(costoverallPanel, BorderLayout.SOUTH);


			planEvaluationPanel.add(getPlanEvaluationToolBar(), BorderLayout.NORTH);
                        planEvaluationPanel.add(contentPanel, BorderLayout.CENTER);
		}

		return planEvaluationPanel;
	}

	
	/**
	 * This method initializes planInfoFramePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private ItFramePanel getPlanInfoFramePanel() {
		if (planInfoFramePanel == null) {
			JPanel planInfoPanel = new JPanel(new BorderLayout());
			planInfoPanel.setMinimumSize(new Dimension(100, 40));
			planInfoFramePanel = new ItFramePanel(":: Console", ItFramePanel.MINIMIZE_MAXIMIZE);
			//informationPanel.setMinimumSize(new Dimension(100,25));


			//planInfoEditorPane = new JEditorPane();
			//planInfoEditorPane.setContentType("text/html");
			//planInfoEditorPane.setEditable(false);
			//planInfoEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
                        //planInfoEditorPane.setBackground(Color.WHITE);

                        outputEditorPane = new JTextArea();
			//outputEditorPane.setContentType("text/html");
			outputEditorPane.setEditable(false);
                        outputEditorPane.setLineWrap(true);
                        outputEditorPane.setWrapStyleWord(true);
			outputEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));

                        // tabbed panes with jtrees
			JTabbedPane outputPane = new JTabbedPane();
			outputPane.addTab("Output", new JScrollPane(outputEditorPane));
			//outputPane.addTab("Results", new JScrollPane(planInfoEditorPane));

			//planInfoFramePanel.setContent(planInfoEditorPane, true);
                        planInfoFramePanel.setContent(outputPane, false);
			planInfoFramePanel.setParentSplitPane(planInfoSplitPane);

			planInfoPanel.add(planInfoFramePanel, BorderLayout.CENTER);
		}
		return planInfoFramePanel;
	}

	public void setPlanInfoPanelText(String text){
		planInfoEditorPane.setText(text);
	}

	public void setPlanEvaluationInfoPanelText(String text){
		planEvaluationInfoEditorPane.setText(text);
	}

        public void setOutputPanelText(String text){
                    outputEditorPane.setText(text);
            }

        public void appendOutputPanelText(String text){
            try {
                outputEditorPane.append(text);
                outputEditorPane.setCaretPosition(outputEditorPane.getDocument().getLength());
            } catch (Exception e) {
            }
            //String outputtext = outputEditorPane.getText();
            //int pos = outputtext.length();
            //outputEditorPane.setCaretPosition(pos);

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

                                                String pddlVersion = pddlButtonsGroup.getSelection().getActionCommand();

						Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problemElement, pddlVersion);
						String problemText = XPDDLToPDDL.parseXPDDLToPDDL(xpddlProblem, "  ");
                                                XMLUtilities.printXML(xpddlProblem);

						problemPddlTextPane.setText(problemText);

                                                //Check if the chosen problem requires additional PDDL requirement tags in the domain
                                                if (ToXPDDL.needRequirementModification(xpddlProblem, pddlVersion)){
                                                    Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(problemElement.getDocument().getRootElement(), pddlVersion, null);
                                                    ToXPDDL.adjustRequirements(xpddlDomain, xpddlProblem, pddlVersion);
                                                    String domainText = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "  ");
                                                    domainPddlTextPane.setText(domainText);
                                                }

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
            domainPddlTextPane.setBackground(Color.WHITE);

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
            problemPddlTextPane.setBackground(Color.WHITE);
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
			JRadioButton pddl31 = new JRadioButton("PDDL 3.1");

			pddl21.setOpaque(false);
			pddl21.setActionCommand(ToXPDDL.PDDL_2_1);
			pddl22.setOpaque(false);
			pddl22.setActionCommand(ToXPDDL.PDDL_2_2);
			pddl30.setOpaque(false);
			pddl30.setActionCommand(ToXPDDL.PDDL_3_0);
                        pddl31.setOpaque(false);
			pddl31.setActionCommand(ToXPDDL.PDDL_3_1);

			pddlButtonsGroup = new ButtonGroup();
			pddlButtonsGroup.add(pddl21);
			pddlButtonsGroup.add(pddl22);
			pddlButtonsGroup.add(pddl30);
			pddlButtonsGroup.add(pddl31);
			pddlButtonsGroup.setSelected(pddl21.getModel(), true);

			JPanel settingsPanel = new JPanel();
			settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
			settingsPanel.setOpaque(false);
			settingsPanel.add(pddl21);
			settingsPanel.add(pddl22);
			settingsPanel.add(pddl30);
			settingsPanel.add(pddl31);

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
				new ImageIcon("resources/images/makemovie.png"));
		generateMovieButton.addActionListener(new java.awt.event.ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {


				// the thread is created so the status bar can be refreshed
				new Thread(){
					public void run() {
						ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
						if(selectedNode != null && selectedNode.getLevel() == 3){

							Element problem = selectedNode.getData();
							movie = PlanSimulator.getMovie(xmlPlan, problem);
                            //XMLUtilities.printXML(xmlPlan);
                            //XMLUtilities.printXML(movie);

                            //IN CASE WE WANT TO RUN THE METRICS WITH SIMULATION
                            //Element domain = problem.getParentElement().getParentElement();
                            //Element metrics = PlanSimulator.createMetricsNode(problem, domain);
                            //if(metrics.getChildren().size() > 0){
                            //    PlanSimulator.createMetricDatasets(metrics, xmlPlan, problem, domain, movie);
                            //}
                            //XMLUtilities.printXML(metrics);


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
		//JButton zoomInButton = new JButton("Zoom In",new ImageIcon("resources/images/zoomIN.png"));
                JButton zoomInButton = new JButton(new ImageIcon("resources/images/zoomIN.png"));
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
		//JButton zoomOutButton = new JButton("Zoom Out",new ImageIcon("resources/images/zoomOUT.png"));
                JButton zoomOutButton = new JButton(new ImageIcon("resources/images/zoomOUT.png"));
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
		JButton editStateButton = new JButton("Edit", new ImageIcon("resources/images/edit.png"));
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



            movieMakerToolBar.addSeparator();
            // plan evaluation

            /*
		JButton planEvaluationButton = new JButton("Evaluate Plan", new ImageIcon("resources/images/eval.png"));
		 planEvaluationButton.setToolTipText("<html>Generate a plan evaluation in the planReport. <br> This is restricted to non-time-based domain only.</html>");
		 planEvaluationButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
                //RUN plan evaluation
				generateEvaluatioReport.actionPerformed(e);
			}
		});
		movieMakerToolBar.add( planEvaluationButton);
             */



        // virtual reality

		JButton virtualRealityButton = new JButton("Virtual Prototyping", new ImageIcon("resources/images/virtualprototype.png"));
		virtualRealityButton.setToolTipText("Generate virtual prototype files");
		virtualRealityButton.addActionListener(new java.awt.event.ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ItTreeNode problemNode = (ItTreeNode) problemsPlanTree.getLastSelectedPathComponent();
				ItTreeNode domainNode = (ItTreeNode)problemNode.getParent();
                //ItTreeNode projectNode = (ItTreeNode)domainNode.getParent();
                try {

                    VirtualPrototypingBlender.generatePrototypeFiles(domainNode.getData(), problemNode.getData(), xmlPlan);
                    //VirtualRealityRobotNavigationDomain.generateBackgroundFile(problemNode.getData(), xmlPlan);
                } catch (IOException ex) {
                    Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                }
				//VirtualRealityRobotNavigationDomain.generateBackgroundFile(problemNode.getData(), xmlPlan);
			}
		});
		movieMakerToolBar.add(virtualRealityButton);


		return movieMakerToolBar;
	}



    private JToolBar getPlanEvaluationToolBar() {

            JToolBar planEvaluationToolBar = new JToolBar();
            planEvaluationToolBar.setRollover(true);

            // create the buttons

            // plan evaluation

            JButton planEvaluationButton = new JButton("Evaluate Plan", new ImageIcon("resources/images/eval.png"));
            planEvaluationButton.setToolTipText("<html>Evaluate current plan based on the specified metrics. <br> This is restricted to non-time-based domain only.</html>");
            planEvaluationButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN plan evaluation
                         quickEvaluation.actionPerformed(e);
                    }
            });
            planEvaluationToolBar.add(planEvaluationButton);


            JButton planEvaluationReportButton = new JButton("Evaluation Report", new ImageIcon("resources/images/viewreport.png"));
            planEvaluationReportButton.setToolTipText("<html>Generate a html plan evaluation in the planReport. <br> This is restricted to non-time-based domain only.</html>");
            planEvaluationReportButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
            //RUN plan evaluation
                            generateEvaluationReport.actionPerformed(e);
                    }
            });
            planEvaluationToolBar.add(planEvaluationReportButton);

            JButton editEvaluationButton = new JButton("Edit Evaluation", new ImageIcon("resources/images/edit.png"));
            editEvaluationButton.setToolTipText("<html>Modify plan classification and its metrics evaluation.</html>");
            editEvaluationButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN plan evaluation edit
                        changePlanEvaluationAction.actionPerformed(e);
                    }
            });
            planEvaluationToolBar.add(editEvaluationButton);

            return planEvaluationToolBar;
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
                                                        XMLUtilities.printXML(xpddlDomain);
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
     * This method hides the simProgress bar and set 0 to 100 values
     */
    public void hideSimProgressBar(){
        simProgressBar.setVisible(false);
        simProgressBar.setValue(0);
        simProgressBar.setMaximum(100);
    }


    /**
     *
     * @param xmlPlan
     * @return a html string containing a simple plan report (basic info)
     */
    public void showHTMLReport(Element xmlPlan){
        if (xmlPlan!=null){
            String html = "";
            String evaluationhtml = "";
            if (xmlPlan.getName().equals("xmlPlan")){

                html = PlanAnalyzer.generateHTMLSinglePlanReport(xmlPlan);
                //Save Comparison Report file
                saveFile("resources/report/Report.html", html);

                //evaluationhtml = PlanAnalyzer.generatePlanMetricsSummary(xmlPlan, xmlPlan.getChild("metrics"));
                evaluationhtml = PlanAnalyzer.generatePlanMetricsEvaluationSummary(xmlPlan);

                //System.out.println(evaluationhtml);

            }

            //set plan result panel
            setPlanInfoPanelText(html);
            //set plan evaluation panel
            setPlanEvaluationInfoPanelText(evaluationhtml);

        }

        /*
    	// get the date
        DateFormat dateFormat = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
        Date date = new Date();
        String dateTime = dateFormat.format(date);

       ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
       Element problem = null;
       Element domain = null;
        if(selectedNode != null){
			problem = selectedNode.getData();
            domain = problem.getParentElement().getParentElement();
        }

        // head
		String info = "<TABLE width='100%' BORDER='0' align='center'>"+
					"<TR><TD bgcolor='333399'><font size=4 face=arial color='FFFFFF'>" +
					"<b>REPORT</b> - "+ dateTime +"</font></TD></TR>";

		// project, domain and problem
		if(domain != null && problem != null){
			Element project = domain.getParentElement().getParentElement().getParentElement();

			info += "<TR><TD><font size=3 face=arial><b>Project: </b>"+ project.getChildText("name")+
					"</font></TD></TR>"+
					"<TR><TD><font size=3 face=arial><b>Domain: </b>"+ domain.getChildText("name")+
					"</font></TD></TR>" +
					"<TR><TD><font size=3 face=arial><b>Problem: </b>"+ problem.getChildText("name")+
					"</font></TD></TR>";
		}

		info += "<TR><TD bgcolor='FFFFFF'><font size=3 face=arial><b>itSIMPLE message:<br></b>"+
				xmlPlan.getChild("toolInformation").getChild("message").getText().replaceAll("\n", "<br>") +"<p></TD></TR>";

		// planner
		Element planner = xmlPlan.getChild("planner");
		Element settingsPlanner = null;
		try {
			XPath path = new JDOMXPath("planners/planner[@id='"+ planner.getAttributeValue("id") +"']");
			settingsPlanner = (Element)path.selectSingleNode(ItSIMPLE.getItPlanners());
		} catch (JaxenException e) {
			e.printStackTrace();
		}

		if(settingsPlanner != null){
			info += "<TR><TD bgcolor='gray'><font size=4 face=arial color='FFFFFF'><b>Planner</b></TD></TR>" +
					"<TR><TD><font size=3 face=arial><b>Name: </b>"+ settingsPlanner.getChildText("name")+
					"</font></TD></TR>"+
					"<TR><TD><font size=3 face=arial><b>Version: </b>"+ settingsPlanner.getChildText("version")+
					"</font></TD></TR>"+
					"<TR><TD><font size=3 face=arial><b>Author(s): </b>"+ settingsPlanner.getChildText("author")+
					"</font></TD></TR>"+
					"<TR><TD><font size=3 face=arial><b>Institution(s): </b>"+ settingsPlanner.getChildText("institution")+
					"</font></TD></TR>"+
					"<TR><TD><font size=3 face=arial><b>Link: </b>"+ settingsPlanner.getChildText("link")+
					"</font></TD></TR>"+
					"<TR><TD><font size=3 face=arial><b>Description: </b>"+ settingsPlanner.getChildText("description")+
					"</font><p></TD></TR>";
		}

		// statistics
		Element statistics = xmlPlan.getChild("statistics");
		info += "<TR><TD bgcolor='gray'><font size=4 face=arial color='FFFFFF'><b>Statistics</b>" +
				"</TD></TR>"+
				"<TR><TD><font size=3 face=arial><b>Tool total time: </b>"+ statistics.getChildText("toolTime")+
				"</font></TD></TR>" +
				"<TR><TD><font size=3 face=arial><b>Planner time: </b>"+ statistics.getChildText("time")+
				"</font></TD></TR>" +
				"<TR><TD><font size=3 face=arial><b>Parsing time: </b>"+ statistics.getChildText("parsingTime")+
				"</font></TD></TR>" +
				"<TR><TD><font size=3 face=arial><b>Number of actions: </b>"+ statistics.getChildText("nrActions")+
				"</font></TD></TR>" +
				"<TR><TD><font size=3 face=arial><b>Make Span: </b>"+ statistics.getChildText("makeSpan")+
				"</font></TD></TR>" +
				"<TR><TD><font size=3 face=arial><b>Metric value: </b>"+ statistics.getChildText("metricValue")+
				"</font></TD></TR>" +
				"<TR><TD><font size=3 face=arial><b>Planning technique: </b>"+ statistics.getChildText("planningTechnique")+
				"</font></TD></TR>" +
				"<TR><TD><font size=3 face=arial><b>Additional: </b>"+ statistics.getChildText("additional").replaceAll("\n", "<br>")+
				"</font><p></TD></TR>";


		// plan
		info += "<TR><TD bgcolor='gray'><font size=4 face=arial color='FFFFFF'><b>Plan</b></TD></TR>";


		List<?> actions = xmlPlan.getChild("plan").getChildren("action");
		if (actions.size() > 0) {
			for (Iterator<?> iter = actions.iterator(); iter.hasNext();) {
				Element action = (Element) iter.next();
				// build up the action string
				// start time
				String actionStr = action.getChildText("startTime") + ": ";

				// action name
				actionStr += "(" + action.getAttributeValue("id") + " ";

				// action parameters
				List<?> parameters = action.getChild("parameters")
						.getChildren("parameter");
				for (Iterator<?> iterator = parameters.iterator(); iterator
						.hasNext();) {
					Element parameter = (Element) iterator.next();
					actionStr += parameter.getAttributeValue("id");
					if (iterator.hasNext()) {
						actionStr += " ";
					}
				}
				actionStr += ")";

				// action duration
				String duration = action.getChildText("duration");
				if (!duration.equals("")) {
					actionStr += " [" + duration + "]";
				}

				if(iter.hasNext()){
					info += "<TR><TD><font size=3 face=arial>"+ actionStr +"</font></TD></TR>";
				}
				else{
					info += "<TR><TD><font size=3 face=arial>"+ actionStr +"</font><p></TD></TR>";
				}
			}
		}
		else{
			info += "<TR><TD><font size=3 face=arial>No plan found.</font><p></TD></TR>";
		}


		// planner console output
		info += "<TR><TD bgcolor='gray'><font size=3 face=arial color='FFFFFF'>" +
				"<b>Planner Console Output</b></TD></TR>"+
				"<TR><TD><font size=4 face=courier>" +
				planner.getChildText("consoleOutput").replaceAll("\n", "<br>")+"</font><p></TD></TR>";

		info += "</TABLE>";


        appendOutputPanelText(">> Plan importerd successfully. Chech the generated Results. \n");

    	return info;
         * */
    }

    /**
     * This method cleans up any reference of plans loaded from the database
     */
    public void cleanupPlanDatabaseReference(){
        currentDBPlanID = -1;
        isPlanFromDB = false;
    }

    /**
     * This method gets the id of the current plan (if the plan does not come from the database return -1)
     */
    public int getCurrentPlanFromDatabaseID(){
        return currentDBPlanID;
    }

    /**
     * This method checks if the current plan was loaded from the database
     */
    public boolean isCurrentPlanFromDatabase(){
        return isPlanFromDB;
    }


    public void saveFile(String path,String content){
        FileWriter file = null;
        try {
            file = new FileWriter(path);
            file.write(content);
            file.close();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
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
		//setTitle("itSIMPLE - Integrated Tool Software Interface for Modeling " +
				//"Planning Environments (version "+
				//itSettings.getChildText("version") +")");
        setTitle("itSIMPLE (version "+	itSettings.getChildText("version") +")");
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

	public static Element getItValidators(){
		return itValidators;
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


        public Thread getCurrentThread() {
            return currentThread;
        }

        public Thread getPlannerThread() {
            return plannerThread;
        }


        public ExecPlanner getExe() {
            return exe;
        }

        public static JFrame getItSIMPLEFrame(){
                return instance;
        }

        public Element getSelectedProblemTreeNode(){
            Element selected = null;

            ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
            if (selectedNode!=null){
                selected = selectedNode.getData();
            }
            return selected;
        }


        /**
         * get selected pddl version
         * @return
         */
        public String getSelectedPDDLversion(){
                return pddlButtonsGroup.getSelection().getActionCommand();
        }

	/*public static PropertiesTabbedPane getItPropertiesPane(){
		return propertiesPane;
	}*/


	/**
	 * @param args
	 */
	public static void main(String[] args) {

		//get CommonDatapddlVer
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

		// Get settings from itSettings.xml
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

		// Get validators from itValidators
		org.jdom.Document itValidatorsDoc = null;
		try{
			itValidatorsDoc = XMLUtilities.readFromFile("resources/validators/itValidators.xml");
		}
		catch(Exception e){
			e.printStackTrace();
		}

		if (itPlannersDoc != null){
			itValidators = itValidatorsDoc.getRootElement();
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
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"); // padrï¿½goGetIt GTK+
			//UIManager.setLookAndFeel("javax.swing.plaf.mac.MacLookAndFeel"); // Mac

		}
		catch(Exception e) {
	          //e.printStackTrace();
	    }
		SplashScreen splash = new SplashScreen(5000);
		Thread t = new Thread(splash);
		t.start();
		ItSIMPLE.getInstance();

        WindowEventHandler closeWind = new WindowEventHandler();
        closeWind.itsimpleInst = instance;
        instance.addWindowListener(closeWind);
        instance.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

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
class WindowEventHandler extends WindowAdapter {
    public ItSIMPLE itsimpleInst = null;

    @Override
    public void windowClosing(WindowEvent evt) {
      if (itsimpleInst != null){
          //Kill all threads
          if (itsimpleInst.getExe() != null){
            itsimpleInst.getExe().destroyProcess();   
          }
          Thread currentThread = itsimpleInst.getCurrentThread();
          if (currentThread != null && currentThread.isAlive()){
              currentThread.interrupt();              
          }
          Thread plannerThread = itsimpleInst.getPlannerThread();
          if (plannerThread != null && plannerThread.isAlive()){
              plannerThread.interrupt();
          }

      }
      System.exit(0);
    }
}
