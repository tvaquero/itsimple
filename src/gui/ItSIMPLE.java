/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007-2013 University of Sao Paulo, University of Toronto
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
*			Victor Romero,
*           Matheus Haddad.
**/

package src.gui;

import alice.util.Sleep;
import bibliothek.gui.dock.common.CContentArea;
import bibliothek.gui.dock.common.CControl;
import bibliothek.gui.dock.common.CGrid;
import bibliothek.gui.dock.common.DefaultSingleCDockable;
import bibliothek.gui.dock.common.SingleCDockable;
import bibliothek.gui.dock.common.perspective.CControlPerspective;
import bibliothek.gui.dock.common.perspective.CGridPerspective;
import bibliothek.gui.dock.common.perspective.CPerspective;
import bibliothek.gui.dock.common.perspective.SingleCDockablePerspective;
import bibliothek.gui.dock.common.theme.ThemeMap;

import com.jgoodies.forms.factories.Borders.EmptyBorder;
import edu.stanford.ejalbert.BrowserLauncher;
import edu.stanford.ejalbert.exception.BrowserLaunchingInitializingException;
import edu.stanford.ejalbert.exception.UnsupportedOperatingSystemException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;

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

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
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
import org.json.JSONException;
import org.json.JSONObject;

//import database.DataBase;
//import database.ImportFromDBDialog;
//import java.sql.SQLException;


import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Desktop;
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
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JDialog;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableModel;
import org.jdom.input.SAXBuilder;
import java.io.*;

import javax.swing.BorderFactory;
import javax.swing.JToggleButton;
import javax.swing.ListSelectionModel;


import src.domainanalysis.TorchLightAnalyzer;
import src.itgraph.BasicCell;
import src.itgraph.ItCellViewFactory;
import src.itgraph.ItGraph;
import src.languages.pddl.PDDLToXPDDL;
import src.languages.pddl.ToXPDDL;
import src.languages.pddl.XPDDLToPDDL;
import src.languages.pddl.XPDDLToUML;
import src.languages.petrinets.toPNML;
import src.languages.rmpl.ToXRMPL;
import src.languages.rmpl.XRMPLtoRMPL;
import src.languages.xml.XMLUtilities;
import src.planning.ExecPlanner;
import src.planning.PlanAnalyzer;
import src.planning.PlanSimulator;
import src.planning.PlanValidator;
import src.planning.PlannerSuggestion;
import src.planning.TimeKiller;
import src.rationale.RationaleAnalyzer;
import src.sourceeditor.ItHilightedDocument;
import src.util.database.DataBase;
import src.util.download.Downloader;
import src.util.filefilter.PDDLFileFilter;
import src.util.filefilter.RMPLFileFilter;
import src.util.filefilter.XMLFileFilter;
import src.util.fileio.FileInput;
import src.util.fileio.FileOutput;
import src.util.update.VersionUpdater;
import src.util.websocket.EmptyClient;
import src.virtualprototyping.VirtualPrototypingBlender;





/**
 * This is the main class of itSIMPLE tool (GUI)
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
	//private JTabbedPane mainTabbedPane = null;
    private JPanel mainTabbedPane = null;
	private JToolBar toolBar = null;
	// itSIMPLE menu bar
	private JMenuBar itMenuBar = null;
    //File
	private JMenu fileMenu = null;
	private JMenu newMenu = null;
    private JMenuItem newUMLMenuItem = null;
    private JMenuItem newPDDLMenuItem = null;
	private JMenuItem openMenuItem = null;
    private JMenu openRecentMenu = null;
	private JMenuItem saveMenuItem = null;
	private JMenuItem saveAsMenuItem = null;
	private JMenuItem saveAllMenuItem = null;
    private JMenu importMenu = null;
	//private JMenuItem exportToDataBaseMenuItem = null;
	//private JMenuItem importFromDataBaseMenuItem = null;
    private JMenuItem importModelingPatternMenuItem = null;
    private JMenuItem importPDDLProjectMenuItem = null;
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
    
    //Perspective
    CControl dockingcontrol = null;
    CContentArea dockingcontent = null; 
    CGrid dockinggrid = null; 
    private ButtonGroup perspectiveGroup = new ButtonGroup();
	private JToggleButton modelingPerspectiveButton = null;	
	private JToggleButton analysisPerspectiveButton = null;	
	private JToggleButton planningPerspectiveButton = null;	
	private JToggleButton modelTranslationPerspectiveButton = null;	      
	
	// Modeling perspective
	SingleCDockable projectexplorerDock = null;
	SingleCDockable propertiesDock = null;
	SingleCDockable diagramDock = null;
	SingleCDockable modelInfoDock = null;
	SingleCDockable additionalmodelDock = null;
	// Planning perspective
	SingleCDockable problemSelectionDock = null;
	SingleCDockable planDock = null;
	SingleCDockable plananalysisDock = null;
	SingleCDockable consoleDock = null;
	SingleCDockable plannavigationDock = null;
        
               

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


	//Main tree
	private ItFramePanel treeFramePanel = null;
	private ItTree projectsTree = null;
	private ItTreeNode treeRoot;
	private JPopupMenu treePopupMenu = null;

	// properties
	private PropertiesTabbedPane propertiesPane = null;
    private ItFramePanel propertiesFramePanel = null;

	// graph
	private ItTabbedPane graphTabbedPane = null;
	private JPanel graphPanel = null;

    //Additional UML panel
    private ItFramePanel additionalUMLFramePanel = null;


	// Translation
	private JSplitPane translationSplitPane = null;
	private JTabbedPane translationModelTabbedPane = null;
	//  PDDL
	private JSplitPane pddlTextSplitPane = null;
	private JPanel topPddlPanel = null;
	private JPanel bottomPddlPanel = null;
	private JScrollPane topPddlScrollPane = null;
	private JScrollPane bottomPddlScrollPane = null;
	private JTextPane domainPddlTextPane = null;
	private JTextPane problemPddlTextPane = null;                      
	private ButtonGroup languageButtonsGroup = null;
	private JPanel pddlPanel = null;
	private JToolBar domainPddlToolBar = null;
	private JToolBar problemPddlToolBar = null;
	//  RMPL
	private ItFramePanel rmplPanel = null;
	private JTextPane rmplTextPane = null; 
        
    private JTextArea outputPddlTranslationEditorPane = null;
    private JTree modelTranslationTree = null;
    private DefaultTreeModel pddlTranslationTreeModel = null;         
    private JButton translateDomainProblemButton = null;
    
   

    //domain analysis 
    private JPanel analysisPane = null;
    private JSplitPane analysisSplitPane = null;
    private JTextArea outputAnalysisEditorPane = null;
    private JLabel analysisStatusBar = null;
	private JTree projectAnalysisTree = null;
    private DefaultTreeModel projectAnalysisTreeModel = null; 
    
        
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
    private JButton checkExistingRationaleButton = null;

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

    //For keep the repository diagram
    private static Element diagramResposiyory = null;
    private static Element objectDiagram = null;
    private static int typeDiagram = -1;
    
    //For keep the element selected of Object Diagram
    
    /* private BasicCell selectedCell; 
    
    
   public void setSelectedCell(BasicCell one_selectedCell){
    
        selectedCell = one_selectedCell;
    }
    
    public BasicCell getSelectedCell(){
        return selectedCell;
    }*/
        
        
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
                        
                        ItTreeNode lastselected = (ItTreeNode)projectsTree.getLastSelectedPathComponent();                     

			if (treeRoot.getChildCount() > 0){
				if (treeRoot.getChildCount() == 1){
					ItTreeNode projectNode = (ItTreeNode)treeRoot.getChildAt(0);

					filePath = projectNode.getReference().getChildText("filePath");
					project = projectNode.getData();
				}
				else if (!isRoot && ((ItTreeNode)projectsTree.getLastSelectedPathComponent()) != null){
					ItTreeNode projectNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
					while (!projectNode.getData().getName().equals("project") && !projectNode.getData().getName().equals("pddlproject")){
						projectNode = (ItTreeNode)projectNode.getParent();
					}

					filePath = projectNode.getReference().getChildText("filePath");
					project = projectNode.getData();
				}

				if (project != null){
                                    
                                        //System.out.println(project.getName());
                                        
                                        //UML project
                                        if (project.getName().equals("project")){
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
                                        //PDDL project
                                        else if (project.getName().equals("pddlproject")){
                                            Element selected = lastselected.getData();
                                            //XMLUtilities.printXML(selected);
                                            if (selected.getName().equals("pddlproject")){
                                                savepddlprojectNode(selected,filePath);                                                 
                                            }
                                            else if (selected.getName().equals("pddldomain")){
                                                savepddldomainNode(selected,filePath);                                                   
                                            }
                                            else if (selected.getName().equals("pddlproblem")){
                                                savepddlproblemNode(selected);
                                            }
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
        
        private ImageIcon saveAllIcon = new ImageIcon("resources/images/saveAll.png");
	private Action saveAllAction = new AbstractAction("Save All", saveAllIcon){
		/**
		 *
		 */
		private static final long serialVersionUID = -5151166226993763827L;

		public void actionPerformed(ActionEvent e) {
			for (int i = 0; i<treeRoot.getChildCount(); i++){
				ItTreeNode currentNode = (ItTreeNode)treeRoot.getChildAt(i);
                                
                                //XMLUtilities.printXML(currentNode.getData());
                                //XMLUtilities.printXML(currentNode.getReference());
                                
				String filePath = currentNode.getReference().getChildText("filePath");
				Element project = currentNode.getData();

				Element tempId = project.getChild("generalInformation").getChild("id");
				project.getChild("generalInformation").removeChild("id");
                                
                                //UML projects
                                if (project.getName().equals("project")){
                                    //Save iProject.xml file
                                    XMLUtilities.writeToFile(filePath, project.getDocument());
                                }
                                
                                //PDDL projects
                                else if (project.getName().equals("pddlproject")){
                                    
                                    //Domain: save all domains that have been changed
                                    Element domainNode = project.getChild("pddldomains");
                                    for (Iterator<Element> it = domainNode.getChildren().iterator(); it.hasNext();) {
                                        Element element = it.next();
                                        savepddldomainNode(element,filePath);                                                                                 
                                    }
                                                                                                                                                                                
                                    //Problems: Check and save each modified problem instance
                                    Element instancesNode = project.getChild("problemInstances");
                                    for (Iterator<Element> it = instancesNode.getChildren().iterator(); it.hasNext();) {
                                        Element element = it.next();
                                        savepddlproblemNode(element);                                                                            
                                    }
                                                                        
                                    //Clean problems instances
                                    Element projectclone = (Element)project.clone();
                                    projectclone.getChild("problemInstances").removeChildren("pddlproblem");
                                    String iprojectcontent = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n" + XMLUtilities.toString(projectclone);
                                    
                                    //Save iProject.xml file
                                    try {
                                        FileWriter file = new FileWriter(filePath);
                                        file.write(iprojectcontent);
                                        file.close();
                                    } catch (IOException e1) {
                                            e1.printStackTrace();
                                    }                                    
                                }
                                
				project.getChild("generalInformation").addContent(tempId);
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



	/**
         * This action creates a new project the trees
         */
        private Action newProjectAction = new AbstractAction("UML Project",new ImageIcon("resources/images/new24.png")){
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


				// update plan simulation problem tree
                                updateNewProjectParallelTree(problemsPlanTreeModel,problemsPlanTree, doc, xmlRoot, new ItTreeNode(xmlRoot.getChildText("name"), xmlRoot, null, null));                                       
                                
                                //update analysis tree
                                updateNewProjectParallelTree(projectAnalysisTreeModel,projectAnalysisTree, doc, xmlRoot, new ItTreeNode(xmlRoot.getChildText("name"), xmlRoot, null, null));
                                
                                //if (xmlRoot.getName().equals("project")){
                                    //update pddl tree
                                    updateNewProjectParallelTree(pddlTranslationTreeModel,modelTranslationTree, doc, xmlRoot, new ItTreeNode(xmlRoot.getChildText("name"), xmlRoot, null, null));                                
                                //}
  
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


	/**
         * This action creates a new project the trees
         */
        private Action newPddlProjectAction = new AbstractAction("PDDL Project",new ImageIcon("resources/images/new24.png")){
		/**
		 *
		 */

		public void actionPerformed(ActionEvent e) {

			//JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>Yeap, it would be nice to have it!!<br> We are working on that. It is coming soon. </html>");
                        //San Pedro
                        NewPDDLProjectDialog pddlProjectDialog = new NewPDDLProjectDialog(ItSIMPLE.getItSIMPLEFrame(), true);
                        pddlProjectDialog.setVisible(true);
                        //pddlProjectDialog.setLocationRelativeTo(ItSIMPLE.getItSIMPLEFrame());
                        if (pddlProjectDialog.isCanOpen())
                            openProjectFromPath(pddlProjectDialog.getPDDLProjectFilePath());
		}
	};
                
        private Action importPDDLProjectAction = new AbstractAction("Import PDDL Project", new ImageIcon("resources/images/virtualprototype.png")){

		public void actionPerformed(ActionEvent e){
			//final ImportModelingPattern dialog = new ImportModelingPattern(ItSIMPLE.this,false);
			//dialog.setVisible(true);
                       //San Pedro
                    ImportPDDLProjectDialog importPddlProjectDialog = new ImportPDDLProjectDialog(ItSIMPLE.getItSIMPLEFrame(), true);
                    importPddlProjectDialog.setVisible(true);
                    //importPddlProjectDialog.setLocationRelativeTo(ItSIMPLE.getItSIMPLEFrame());
                    if (importPddlProjectDialog.isCanOpen())
                        openProjectFromPath(importPddlProjectDialog.getPDDLProjectFilePath());
		}
	};


        
        /**
         * Change for Modeling Perspective
         */        
        //private ImageIcon analysisIcon = new ImageIcon("resources/images/saveAll.png");
	//private Action petriAnalysisAction = new AbstractAction("Analysis", analysisIcon){
        private Action modelingAction = new AbstractAction("Modeling"){

		public void actionPerformed(ActionEvent e) {
                    System.out.println("Modeling Perspective");
                    //setModelingPerspective("UML");
                    CardLayout cl = (CardLayout)(mainTabbedPane.getLayout());
                    setPerspective("Modeling");
                    cl.show(mainTabbedPane, "Modeling");  
                    

		}
	};

        /**
         * Change for Analysis Perspective
         */
        //private ImageIcon analysisIcon = new ImageIcon("resources/images/saveAll.png");
	//private Action analysisAction = new AbstractAction("Analysis", analysisIcon){
        private Action analysisAction = new AbstractAction("Analysis"){

		public void actionPerformed(ActionEvent e) {
                    System.out.println("Analysis Perspective");
                    updatePetriNetPanels();
                    updateTreeChanges(projectAnalysisTreeModel);
                    CardLayout cl = (CardLayout)(mainTabbedPane.getLayout());
                    cl.show(mainTabbedPane, "Analysis");                  

		}
	};

        /**
         * Change for Planning Perspective
         */
        private Action planningAction = new AbstractAction("Planning"){

		public void actionPerformed(ActionEvent e) {
                System.out.println("Planning Perspective");
                updatePlanSimTrees();
                CardLayout cl = (CardLayout)(mainTabbedPane.getLayout());
                
                setPerspective("Planning");
                //cl.show(mainTabbedPane, "Planning");
                cl.show(mainTabbedPane, "Modeling");
		}
	};
        
        /**
         * Change for Translation Perspective
         */
        private Action pddlTranslationPerspectiveAction = new AbstractAction("Model Translation"){

		public void actionPerformed(ActionEvent e) {
                    updateTreeChanges(pddlTranslationTreeModel);
                    CardLayout cl = (CardLayout)(mainTabbedPane.getLayout());
                    cl.show(mainTabbedPane, "Translation");
		}
	};        

        
        private Action umlPerspectiveAction = new AbstractAction("UML"){
		/**
		 *
		 */

		public void actionPerformed(ActionEvent e) {
                    System.out.println("UML");
                    setModelingPerspective("UML");
                    CardLayout cl = (CardLayout)(mainTabbedPane.getLayout());
                    setPerspective("Modeling");
                    cl.show(mainTabbedPane, "Modeling");
		}
	};

        private Action translationPerspectiveAction = new AbstractAction("Translation"){
		/**
		 *
		 */

		public void actionPerformed(ActionEvent e) {
                    //System.out.println("PDDL");
                    setModelingPerspective("Translation");
                    CardLayout cl = (CardLayout)(mainTabbedPane.getLayout());
                    cl.show(mainTabbedPane, "Modeling");
		}

	};

     /**
      * Set selected perspective, hidding and showing panels (UML, PDDL)
      * @param input
      */
    public void setModelingPerspective(String input){
        if (input.equals("UML")) {
            additionalUMLFramePanel.setVisible(true);
            informationPanel.setVisible(true);
            graphSplitPane.setResizeWeight(1.0);// fixes the botoom component's size
            //graphSplitPane.setDividerLocation((int)screenSize.height*3/4);
            graphSplitPane.setDividerLocation((int)screenSize.height*1/2);
            infoPanel.minimize();

        }
        else if (input.equals("PDDL")){
            additionalUMLFramePanel.setVisible(false);
            informationPanel.setVisible(false);
            
        }

    }


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

			domain.setAttribute("id", String.valueOf(XMLUtilities.getId(
							selectedNode.getData().getChild("diagrams").getChild("planningDomains"))));

			domain.getChild("name").setText(domain.getChildText("name") + domain.getAttributeValue("id"));
			selectedNode.getData().getChild("diagrams").getChild("planningDomains").addContent(domain);
			projectsTree.buildDomainNode(domain, selectedNode);

			// update problems plan tree
                        updateNewDomainProjectParallelTree(problemsPlanTreeModel, domain, selectedNode);
                        
                        // update analysis tree
                        updateNewDomainProjectParallelTree(projectAnalysisTreeModel, domain, selectedNode);
                        
                        // update pddl tree
                        updateNewDomainProjectParallelTree(pddlTranslationTreeModel, domain, selectedNode);
                                                
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

			// update problems plan tree                        
                        updateNewProblemProjectParallelTree(problemsPlanTreeModel, problem, selectedNode, (ItTreeNode)selectedNode.getParent());
                        
                        // update analysis tree
                        updateNewProblemProjectParallelTree(projectAnalysisTreeModel, problem, selectedNode, (ItTreeNode)selectedNode.getParent());			
                        
                        // update pddl tree
                        updateNewProblemProjectParallelTree(pddlTranslationTreeModel, problem, selectedNode, (ItTreeNode)selectedNode.getParent());			
		
                        
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

                            
                            // update problems plan tree
                            updateNewDomainProjectParallelTree(problemsPlanTreeModel, domain, project);
                            
                            //update analysis tree
                            updateNewDomainProjectParallelTree(projectAnalysisTreeModel, domain, project);

                            //update pddl tree
                            updateNewDomainProjectParallelTree(pddlTranslationTreeModel, domain, project);
                            
                            
                            /*
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
                             *
                             */


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

                            // update problems plan tree
                            updateDuplicateProblemProjectParallelTree(problemsPlanTreeModel, problem, selectedNode, project);
                            
                            //update analysis tree
                            updateDuplicateProblemProjectParallelTree(projectAnalysisTreeModel, problem, selectedNode, project);

                            //update pddl tree
                            updateDuplicateProblemProjectParallelTree(pddlTranslationTreeModel, problem, selectedNode, project);                                                        
                             

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

				// update problems plan tree
                                updateDeleteDomainProjectParallelTree(problemsPlanTreeModel, domain, project);
                                
                                // update analysis tree
                                updateDeleteDomainProjectParallelTree(projectAnalysisTreeModel, domain, project);

                                // update pddl tree
                                updateDeleteDomainProjectParallelTree(pddlTranslationTreeModel, domain, project);
                                                                
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

				// update problems plan tree
                                updateDeleteProblemProjectParallelTree(problemsPlanTreeModel, domain, problem, project);
                                
                                // update analysis tree
                                updateDeleteProblemProjectParallelTree(projectAnalysisTreeModel, domain, problem, project);

                                // update pddl tree
                                updateDeleteProblemProjectParallelTree(pddlTranslationTreeModel, domain, problem, project);
                                				
			}

		}
	};

        
        private Action deletePDDLDomainAction = new AbstractAction("Delete Domain",new ImageIcon("resources/images/delete.png")){
		/**
		 *
		 */

		public void actionPerformed(ActionEvent e) {
			ItTreeNode domain = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			ItTreeNode project = (ItTreeNode)domain.getParent();

			Element projectHeader = project.getReference();
			Element openTabs = ItTabbedPane.getOpenTabs();
                        
                        //TODO: do it for PDDL domain files

                        /*
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


                        
			boolean removed = project.getData().getChild("diagrams").getChild("planningDomains").removeContent(domain.getData());
			if (removed){
				// projects tree
				projectsTree.setSelectionPath(new TreePath(project.getPath()));

				DefaultTreeModel model = (DefaultTreeModel)projectsTree.getModel();
				model.removeNodeFromParent(domain);

				// update problems plan tree
                                updateDeleteDomainProjectParallelTree(problemsPlanTreeModel, domain, project);
                                
                                // update analysis tree
                                updateDeleteDomainProjectParallelTree(projectAnalysisTreeModel, domain, project);

                                // update pddl tree
                                updateDeleteDomainProjectParallelTree(pddlTranslationTreeModel, domain, project);
                                                                
			}
                         * 
                         */

		}
	};

        
       
        

        private Action deletePDDLProblemAction = new AbstractAction("Delete Problem",new ImageIcon("resources/images/delete.png")){
		/**
		 *
		 */
		private static final long serialVersionUID = 4318760938713713101L;

		public void actionPerformed(ActionEvent e) {
			ItTreeNode problem = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
			ItTreeNode problemInstances = (ItTreeNode)problem.getParent();
			ItTreeNode project = (ItTreeNode)problemInstances.getParent();
                        Element planningProblems = problemInstances.getData();
                        
                        DefaultTreeModel treeModel = (DefaultTreeModel)projectsTree.getModel();
                       
                        String filename = problem.getData().getAttributeValue("file");
                                                
                        File file = new File(filename);
                        boolean success = file.delete();
                        if (success){            
                            
                            //close tabs
                            Element openTabs = ItTabbedPane.getOpenTabs();
                            //XMLUtilities.printXML(openTabs);
                            String diagramType = problem.getData().getName();
                            Element projectHeader = project.getReference();                            
                            Element result = null;
                            String tabpath = "openTab[@projectID='" + projectHeader.getAttributeValue("id") +
                                    "' and @diagramID='" + problem.getData().getAttributeValue("filename") +
                                    "' and type='" + diagramType + "']";
                            try {
                                    XPath path = new JDOMXPath(tabpath);
                                    result = (Element)path.selectSingleNode(openTabs);
                            } catch (JaxenException e2) {
                                    e2.printStackTrace();
                            }
                            //XMLUtilities.printXML(problem.getData());
                            //System.out.print(tabpath);                        
                            if (result!=null){
                                graphTabbedPane.closeTab(result.getParent().indexOf(result));
                            }

                            //remove node from tree
                            treeModel.removeNodeFromParent(problem);
                            planningProblems.removeContent(problem.getData());
                            projectsTree.setSelectionPath(new TreePath(project.getPath()));
                        }else{
                            JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>System was unable to delete the file. Please check file permission. </html>");
                        }                        

		}
	};
        
        
        
        private Action newPDDLProblemFileAction = new AbstractAction("New Problem File",new ImageIcon("resources/images/new.png")){
		/**
		 *
		 */
		private static final long serialVersionUID = -1344774838104367132L;

		public void actionPerformed(ActionEvent e) {
                    
                    NewPDDLProblemDialog pddlProblemDialog = new NewPDDLProblemDialog(ItSIMPLE.getItSIMPLEFrame(), true, projectsTree);
                    pddlProblemDialog.setVisible(true);
                                            

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
			
                                //Close the projects in the plan simulation problem tree
                                updateCloseProjectParallelTree(problemsPlanTreeModel, index);
                                
                                //Close the projects in the analysis tree
                                updateCloseProjectParallelTree(projectAnalysisTreeModel, index);

                                
                                updateCloseProjectParallelTree(pddlTranslationTreeModel, index);

			}

		}
	};

	private Action saveDomainToFile = new AbstractAction("Save to PDDL", new ImageIcon("resources/images/savePDDL.png")){
		/**
		 *
		 */
		private static final long serialVersionUID = 755442300480255147L;

		public void actionPerformed(ActionEvent e) {
                    
                        String lastOpenFolder = "";
                        Element lastOpenFolderElement = itSettings.getChild("generalSettings").getChild("lastOpenFolder");
                        if (lastOpenFolderElement != null){
                                lastOpenFolder = lastOpenFolderElement.getText();
                        }
                    
                    
			JFileChooser fc = new JFileChooser(lastOpenFolder);
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
                                
                                if (lastOpenFolderElement != null){
                                    //Holds the last open folder
                                    if (!lastOpenFolderElement.getText().equals(selectedFile.getParent())){
                                            lastOpenFolderElement.setText(selectedFile.getParent());
                                            XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
                                    }
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
                    
			String lastOpenFolder = "";
                        Element lastOpenFolderElement = itSettings.getChild("generalSettings").getChild("lastOpenFolder");
                        if (lastOpenFolderElement != null){
                                lastOpenFolder = lastOpenFolderElement.getText();
                        }
                    
                    
			JFileChooser fc = new JFileChooser(lastOpenFolder);
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
                                
                                if (lastOpenFolderElement != null){
                                    //Holds the last open folder
                                    if (!lastOpenFolderElement.getText().equals(selectedFile.getParent())){
                                            lastOpenFolderElement.setText(selectedFile.getParent());
                                            XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
                                    }
                                } 
			}
		}
	};
               
	
	
	private Action saveRMPLModelToFile = new AbstractAction("Save to RMPL", new ImageIcon("resources/images/savePDDL.png")){
		/**
		 *
		 */
		private static final long serialVersionUID = 755442300480255147L;

		public void actionPerformed(ActionEvent e) {
                    
			String lastOpenFolder = "";
            Element lastOpenFolderElement = itSettings.getChild("generalSettings").getChild("lastOpenFolder");
            if (lastOpenFolderElement != null){
                    lastOpenFolder = lastOpenFolderElement.getText();
            }
                   
			JFileChooser fc = new JFileChooser(lastOpenFolder);
			fc.setDialogTitle("Save to RMPL");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new RMPLFileFilter());


			int returnVal = fc.showSaveDialog(ItSIMPLE.this);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File selectedFile = fc.getSelectedFile();
				String path = selectedFile.getPath();
				if (!path.toLowerCase().endsWith(".rmpl")){
					path += ".rmpl";
				}
				try {
					FileWriter file = new FileWriter(path);
					file.write(rmplTextPane.getText());
					file.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
                                
                if (lastOpenFolderElement != null){
                    //Holds the last open folder
                    if (!lastOpenFolderElement.getText().equals(selectedFile.getParent())){
                            lastOpenFolderElement.setText(selectedFile.getParent());
                            XMLUtilities.writeToFile("resources/settings/itSettings.xml", itSettings.getDocument());
                    }
                } 
			}
		}
	};
	
	
	/**
	 * This action connect to the RMPL we editor and sends the resulting model via websocket
	 */
	private Action openRMPLWebEditor = new AbstractAction("Send to Web Editor", new ImageIcon("resources/images/details.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 1L;


		public void actionPerformed(ActionEvent e) {
			WebSocketClient client;
			try {
				
				/*
				//TODO: Open browser with the RMPL editor
				Desktop.getDesktop().browse(new URI("http://bicycle.csail.mit.edu/rmpleditor/"));
				try {
					  Thread.sleep(1000);
				} catch (InterruptedException ie) {
				    //Handle exception
				}*/
				
				client = new EmptyClient(new URI("ws://bicycle.csail.mit.edu:9029/itsimple"), new Draft_10());
				appendModelTranslationOutputPanelText("Connecting to http://bicycle.csail.mit.edu/rmpleditor/ \n");
		        client.connect();
		        int timeout_counter = 0;
		        while (!client.isOpen()) {
					System.out.println("openning " +Integer.toString(timeout_counter));
					timeout_counter += 1;
				}
		        if (client.isOpen()){ 
		        	appendModelTranslationOutputPanelText("Sending RMPL model \n\n");
			        System.out.println();
			        JSONObject obj = new JSONObject();
			        obj.put("command", "display_rmpl");
			        obj.put("data", rmplTextPane.getText());
			        String message = obj.toString();
			        client.send(message);	
			        appendModelTranslationOutputPanelText("Model data sent successfully. \n\n");
		        }
		        else{
		        	appendModelTranslationOutputPanelText("Timeout: System could not connect to http://bicycle.csail.mit.edu/rmpleditor/ \n");
		        }
		        client.close();
		        
			} catch (URISyntaxException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			//catch (IOException e1) {
				// TODO Auto-generated catch block
			//	e1.printStackTrace();
			//}

			
			

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
        
        
        //private Action openPDDLFile = new AbstractAction("Open", new ImageIcon("resources/images/open.png")){
        private Action openPDDLFile = new AbstractAction("Open"){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e) {
                    
                    ItTreeNode pddlnode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
                    
                    ItTreeNode project = null;
                    String path = "";
                    File theFile = null;
                    String id = "";
                    if (pddlnode.getLevel() == 3) {
                            project = (ItTreeNode)pddlnode.getParent().getParent();
                            path = pddlnode.getData().getAttributeValue("file");
                            theFile = new File(path);
                            //id is the filename
                            id = pddlnode.getData().getAttributeValue("filename");
                    }
                    else if (pddlnode.getLevel() == 2){
                            project = (ItTreeNode)pddlnode.getParent();
                            
                            String tpath = project.getReference().getChildText("filePath");
                            File theitProjectFile = new File(tpath);
                            tpath = tpath.replaceFirst(theitProjectFile.getName(), "");
                            path = tpath + pddlnode.getData().getChildText("name");                            
                            theFile = new File(path);
                            //id is the filename
                            id = pddlnode.getData().getChildText("name");
                            //System.out.println(path);
                            //XMLUtilities.printXML(project.getReference());
                    }

                    String tabTitle = pddlnode.getData().getChildText("name") + "(PDDL) - " + project.getData().getChildText("name");

                    graphTabbedPane.openPDDLTab(pddlnode.getData(), id, tabTitle, project.getData(), project.getReference(), theFile);
                    //graphTabbedPane.openTab(problem.getData(), problem.getData().getAttributeValue("id"),
                    //                tabTitle, problem.getData().getName(), project.getData(), commonData, project.getReference(),"PDDL");
                    
                     
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

		public void actionPerformed(ActionEvent e){
			final PlannersSettingsDialog dialog = new PlannersSettingsDialog(ItSIMPLE.this);

			dialog.setVisible(true);
		}
	};
        

        private Action importModelingPatternAction = new AbstractAction("Import Modeling Pattern", new ImageIcon("resources/images/virtualprototype.png")){

		public void actionPerformed(ActionEvent e){
                    //check if there is a uml project selected. If not show message
                    ItTreeNode selectedNode = (ItTreeNode)projectsTree.getLastSelectedPathComponent();
                    if (projectsTree.getSelectionCount() > 0 && selectedNode.getData() != null && selectedNode.getData().getName().equals("project")){
                        Element project = selectedNode.getData();
                       
                        final ImportModelingPattern dialog = new ImportModelingPattern(ItSIMPLE.this,true,project);
                        dialog.setVisible(true);
                    }
                    else{
                        JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>Please select a UML project node (in the Project Explorer panel) <br> where you want to import a modeling pattern. </html>");
                    }                    
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
         * This action opens the dialog for editing plan evaluation
         */
        private Action insertPlanEvaluationRationaleAction = new AbstractAction("Edit Evaluation", new ImageIcon("resources/images/edit.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e){
			final PlanEvaluationRationalesEditDialog dialog = new PlanEvaluationRationalesEditDialog(ItSIMPLE.this, xmlPlan);
			dialog.setVisible(true);
                        if (xmlPlan != null){
                            //String evaluationhtml = PlanAnalyzer.generatePlanMetricsSummary(xmlPlan, xmlPlan.getChild("metrics"));
                            String evaluationhtml = PlanAnalyzer.generatePlanMetricsEvaluationSummary(xmlPlan);
                            setPlanEvaluationInfoPanelText(evaluationhtml);
                            //System.out.println(evaluationhtml);
                        }
		}
	};
        
        
        
        private Action reuseRationalesAction = new AbstractAction("Reuse Rationales", new ImageIcon("resources/images/edit.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e){
                    
                    
                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                    if(selectedNode != null){
                        Element node = selectedNode.getData();
                        String type ="";
                        //check if it is a projects node
                        if (node == null){
                            type = "projects";
                        }else{
                            type = node.getName();
                        }
                        
                        if (type.equals("problem") && xmlPlan != null){
                            
                            reuserationaleThread = new Thread(){
                                public void run() {
                                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                                    Element problem = selectedNode.getData();
                                    RationaleAnalyzer.reuseExistingRationales(xmlPlan, problem, Integer.toString(currentDBPlanID), planSimStatusBar);
                                    checkExistingRationaleButton.setText("Reuse Existing Rationales");
                                    checkExistingRationaleButton.setActionCommand("reuse");
                                }
                            };
                            reuserationaleThread.start();



                        }else{
                            JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>In order to reuse existing rationale, it is required <br>to select the corresponding planning problem. </html>");
                        }
                        
                        
                        
                        
                    }
			
		}
	};


        



        /**
         * This action search in the database for plans matching the selecetd node in the problem list
         */
        private Action searchPlanInDatabaseAction = new AbstractAction("Search", new ImageIcon("resources/images/searchdatabase.png")){
		/**
		 *
		 */
                @SuppressWarnings("empty-statement")
		public void actionPerformed(ActionEvent e){

                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                    if(selectedNode != null){

                        //clean up the result table first
                        resultPlanTableModel.getDataVector().removeAllElements();
                        resultPlanTable.repaint();

                        Element node = selectedNode.getData();
                        String type ="";
                        //check if it is a projects node
                        if (node == null){
                            type = "projects";
                        }
                        else{
                            type = node.getName();
                        }



                        DataBase eSelectType = new DataBase();
                        eSelectType.setColumnList("id, project, domain, problem, plannername, plannerversion,  nactions, cost, quality"); //please, don't use *
                        eSelectType.setTableName("plan");
                        String whereclause = "";

                        String project = "";
                        String domain = "";
                        String problem = "";

                        switch(selectedNode.getLevel()){
                            case 0: //Projects
                                //System.out.println("Search within Projects");

                                whereclause = null;

                                break;
                            case 1: //Project
                                //System.out.println("Search within a specific Project");
                                //project = node.getDocument().getRootElement().getChildText("name");
                                project = node.getChildText("name");

                                whereclause = "project = '"+project+"'";


                                break;
                            case 2: //Domain
                                //System.out.println("Search within a specific Domain");
                                project = node.getDocument().getRootElement().getChildText("name");
                                domain = node.getChildText("name");

                                whereclause = "project = '"+project+"' AND  domain='"+domain+"'";

                                break;
                            case 3: //Problem
                                //System.out.println("Search within a specific Problem");
                                project = node.getDocument().getRootElement().getChildText("name");
                                domain = node.getParentElement().getParentElement().getChildText("name");
                                problem = node.getChildText("name");

                                whereclause = "project = '"+project+"' AND  domain='"+domain+"' AND  problem='"+problem+"'";
                                break;
                        }

                        //check if the user wants to filter the search
                        if (planFilterPanel.isVisible()){
                            String filter = planfilterTextPane.getText().replace("\n", " ").trim();
                            if (!filter.equals("")){
                                if (whereclause!=null){
                                    whereclause += " AND " + filter;
                                }else{
                                    whereclause = filter;
                                }
                            }                            
                        }

                        //Do select in the database
                        eSelectType.setWhereClause(whereclause);
                        eSelectType.setOrderClause("id"); //order by clause, null if not applicable
                        //eSelectType.setGroupClause(null); //group by clause, null if not applicable
                        //eSelectType.setHavingClause(null); //having clause, null if not applicable
                        //eSelectType.addToParametersList(name);
                        eSelectType.Select();


                        //Get results and show them
                        ResultSet rs = eSelectType.getRs();
                        try {
                            int counter = 1;
                            while (rs.next()) {                              
                                Vector<String> rowData = new Vector<String>();

                                rowData.add(rs.getString("id"));
                                //rowData.add(String.valueOf(counter));
                                rowData.add(rs.getString("project"));
                                rowData.add(rs.getString("domain"));
                                rowData.add(rs.getString("problem"));
                                rowData.add(rs.getString("plannername"));
                                //rowData.add(rs.getString("plannername") + " - "+ rs.getString("plannerversion"));
                                rowData.add(rs.getString("nactions"));
                                rowData.add(rs.getString("cost"));
                                String quality = rs.getString("quality").trim();
                                //format quality '0.00'
                                if (!quality.equals("")){
                                    double thequality = Double.parseDouble(quality);
                                    DecimalFormat theformat = new DecimalFormat("0.00");
                                    quality = theformat.format(thequality);
                                }
                                rowData.add(quality);

                                resultPlanTableModel.addRow(rowData);
                                counter++;

                            }

                        } catch (SQLException se) {;
                          se.printStackTrace();
                          //System.exit(1);
                        }

                        eSelectType.Close();

                    }

		}
	};


/**
         * This action inserts the current plan into the database
         */
        private Action insertPlanIntoDatabaseAction = new AbstractAction("Add Plan to Database", new ImageIcon("resources/images/addplantodatabase.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e){
                        if (xmlPlan != null){


                            //Insert plan into the database

                            Element thePlan = (Element)xmlPlan.clone();
                            //Rationales - cleanup rationales to store in the database
                            Element rationales = thePlan.getChild("evaluation").getChild("rationales");
                            rationales.removeContent();
                            String xmlPlanStr = XMLUtilities.toString(thePlan);
                            xmlPlanStr = xmlPlanStr.replace("'", "''");
                            //The XML plan (clean up ' char - database need)
                            //String xmlPlanStr = XMLUtilities.toString(xmlPlan);
                            //xmlPlanStr = xmlPlanStr.replaceAll("'", "");

                            //projec, domain and problem names/identifiers
                            String project = xmlPlan.getChildText("project").replace("'", "''");
                            String domain = xmlPlan.getChildText("domain").replace("'", "''");
                            String problem = xmlPlan.getChildText("problem").replace("'", "''");

                            //planner name/identifier
                            String plannername = "";
                            String plannerversion = "";
                            Element planner = xmlPlan.getChild("planner");
                            if (planner != null && !planner.getAttributeValue("id").equals("")){
                                plannername = planner.getChildText("name");
                                plannerversion = planner.getChildText("version");
                            }

                            //number of actions in the plan
                            String nactions = String.valueOf(xmlPlan.getChild("plan").getChildren().size());

                            //plan cost based on the metrics.
                            double overallCostAward = 0;
                            Element metrics = xmlPlan.getChild("metrics");
                            if(metrics!=null){
                                overallCostAward = PlanAnalyzer.evaluateCostAward(metrics);
                            }

                            //quality of the plan
                            String quality = "";
                            Element evaluation =  xmlPlan.getChild("evaluation");
                            if (evaluation != null){
                                quality = evaluation.getAttributeValue("value");
                            }


                            //Insert plan into the database
                            DataBase insertType = new DataBase();
                            insertType.setTableName("plan");
                            insertType.setColumnList("xmlplan, project, domain, problem, plannername, plannerversion, nactions, cost, quality"); // please, don't use ()
                            insertType.setValueList("'"+ xmlPlanStr+"', '"+project+"', '"+domain+"', '"+problem+"', '"+plannername+"', '"+plannerversion+"', "+nactions+", "+Double.toString(overallCostAward)+", "+quality); // please, don't use ()
                            //updateType.setParametersList(list);
                            insertType.retrieveLastID(true);
                            insertType.Insert();

                            //Get genereted keys
                            //ResultSet rs = updateType.getRs();
                            //System.out.println("Last inserted ID: " + updateType.getLastInsertID());
                            currentDBPlanID = insertType.getLastInsertID();
                            isPlanFromDB = true;
                            
                            //DataBase insertType = new DataBase();
                            DataBase updateType = new DataBase();
                            
                            rationales = xmlPlan.getChild("evaluation").getChild("rationales");

                            for (Iterator<Element> it = rationales.getChildren().iterator(); it.hasNext();) {
                                Element rationale = it.next();

                                String rationaleID = rationale.getAttributeValue("id").trim();
                                String targetPlanID = rationale.getAttributeValue("targetplanid").trim();
                                String relationalID = rationale.getAttributeValue("relationalid").trim();
                                String relationalEnabled = rationale.getAttributeValue("enabled").trim().toUpperCase();


                                //1. Insert rationale into the database if it has been inserted
                                if (rationaleID.equals("")){

                                    Element theRationale = (Element)rationale.clone();
                                    theRationale.setAttribute("targetplanid","");
                                    theRationale.setAttribute("relationalid","");
                                    theRationale.setAttribute("enabled", "true");
                                    theRationale.getChild("comments").removeContent();
                                    
                                    String xmlRationaleStr = XMLUtilities.toString(theRationale).replace("'", "''");
                                    String rationaleName = rationale.getChildText("name").replace("'", "''");
                                    String rationaleDescription = rationale.getChildText("description").replace("'", "''");
                                    String rationaleRule = rationale.getChildText("rule").replace("'", "''");
                                    String rationaleQualityImpact = rationale.getChild("impact").getAttributeValue("quality");
                                    String rationaleRange = rationale.getChild("abstractionlevel").getAttributeValue("range");
                                    String rationaleValidity = rationale.getChild("validity").getAttributeValue("isValid").trim().toUpperCase();
                                    if (rationaleValidity.equals("")){
                                        rationaleValidity = "NULL";
                                    }

                                    insertType.setTableName("rationale");
                                    insertType.setColumnList("xmlrationale, name, description, rule, abstractionlevel, project, domain, problem, planid, validity, qualityimpact"); // please, don't use ()
                                    insertType.setValueList("'"+ xmlRationaleStr+"', '"+rationaleName+"', '"+rationaleDescription+"', '"+rationaleRule+"', '"+rationaleRange+"', '"+project+"', '"+domain+"', '"+problem+"', "+currentDBPlanID+", "+rationaleValidity+", '"+rationaleQualityImpact+"'"); // please, don't use ()
                                    //updateType.setParametersList(list);
                                    insertType.retrieveLastID(true);
                                    insertType.Insert();

                                    //set rationale id
                                    rationaleID = Integer.toString(insertType.getLastInsertID());
                                    rationale.setAttribute("id", rationaleID);

                                    //set target plan id
                                    targetPlanID = Integer.toString(currentDBPlanID);
                                    rationale.setAttribute("targetplanid", targetPlanID);

                                    System.out.println("Rationale created - "+ rationaleID);
                                }
                                
                                
                                //2.  if target plan is not already defined, set for the current plan
                                // this can happen when a plan is removed and another plan has the rationale applied to it
                                // then this plan will assume the rationale's reference
                                if (targetPlanID.equals("") && !rationaleID.equals("")){

                                    //update rationale                                    
                                    updateType.setTableName("rationale");
                                    updateType.setUpdateValueList("planid="+currentDBPlanID+", project='"+project+"', domain='"+domain+"', problem='"+problem+"'");
                                    updateType.setWhereClause("id = " + rationaleID); //allways use WHERE
                                    updateType.Update();
                                    
                                    targetPlanID = Integer.toString(currentDBPlanID);
                                    rationale.setAttribute("targetplanid", targetPlanID);

                                    //set target plan id
                                    System.out.println("Unlinked rationale updated - "+ rationaleID);
                                }

                                //3. Insert record into relational table rationale_pla
                                if (relationalID.equals("")){

                                    //comments
                                    String comment = XMLUtilities.toString(rationale.getChild("comments"));
                                    
                                    insertType.setTableName("rationale_plan");
                                    insertType.setColumnList("rationaleid, planid, comment, enabled");
                                    insertType.setValueList(rationaleID+", "+Integer.toString(currentDBPlanID)+", '"+comment+"', "+relationalEnabled); // please, don't use ()
                                    //updateType.setParametersList(list);
                                    insertType.retrieveLastID(true);
                                    insertType.Insert();

                                    relationalID = Integer.toString(insertType.getLastInsertID());
                                    rationale.setAttribute("relationalid", relationalID);                                    
                                }

                                //4. Update if it was modified (i.e., it has a child node called 'modified')
                                Element modified = rationale.getChild("modified");
                                if (modified!=null && !rationaleID.equals("")){

                                    Element theRationale = (Element)rationale.clone();
                                    theRationale.setAttribute("id", "");
                                    theRationale.setAttribute("targetplanid", "");
                                    theRationale.setAttribute("relationalid", "");
                                    theRationale.setAttribute("enabled", "true");
                                    theRationale.getChild("comments").removeContent();
                                    theRationale.removeContent(theRationale.getChild("modified"));
                                    if(theRationale.getChild("instruction")!=null){
                                        theRationale.removeContent(theRationale.getChild("instruction"));
                                    }

                                    String xmlRationaleStr = XMLUtilities.toString(theRationale).replace("'", "''");
                                    String rationaleName = rationale.getChildText("name").replace("'", "''");
                                    String rationaleDescription = rationale.getChildText("description").replace("'", "''");
                                    String rationaleRule = rationale.getChildText("rule").replace("'", "''");
                                    String rationaleQualityImpact = rationale.getChild("impact").getAttributeValue("quality");
                                    String rationaleRange = rationale.getChild("abstractionlevel").getAttributeValue("range");
                                    String rationaleValidity = rationale.getChild("validity").getAttributeValue("isValid").trim().toUpperCase();
                                    if (rationaleValidity.equals("")){
                                        rationaleValidity = "NULL";
                                    }

                                    updateType.setTableName("rationale");
                                    updateType.setUpdateValueList("xmlrationale = '"+xmlRationaleStr+"', name = '" + rationaleName + "', description = '" + rationaleDescription +
                                            "', rule = '" + rationaleRule + "', abstractionlevel = '" + rationaleRange+"', validity = "+rationaleValidity+ ", qualityimpact = '"+rationaleQualityImpact+"'");
                                    updateType.setWhereClause("id = " + rationaleID); //allways use WHERE
                                    updateType.Update();

                                    rationale.removeContent(modified);

                                    System.out.println("Modified rationale updated - "+ rationaleID);

                                }

                            }

                            updateType.Close();
                            insertType.Close();

                            appendOutputPanelText(">> Plan (id "+currentDBPlanID+") inserted successfully. \n");


                            //System.out.println("Last inserted ID: " + updateType.getLastInsertID());
                            // eInsert template end

                        }
		}
	};


        /**
         * This action loads the current plan from the database
         */
        private Action loadPlanFromDatabaseAction = new AbstractAction("Load", new ImageIcon("resources/images/getfromdatabase.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e){

                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                    if(selectedNode != null){

                        int row = resultPlanTable.getSelectedRow();
                        if (row > -1){

                            String planID = (String) resultPlanTableModel.getValueAt(row, 0);

                            Element node = selectedNode.getData();
                            //String type ="";
                            //check if it is a projects node
                            //if (node == null){
                            //    type = "projects";
                            //}
                            //else{
                            //    type = node.getName();
                            //}


                            DataBase eSelectType = new DataBase();
                            eSelectType.setColumnList("xmlplan"); //please, don't use *
                            eSelectType.setTableName("plan");
                            String whereclause = "id = "+ planID;

                            //Do select in the database
                            eSelectType.setWhereClause(whereclause);
                            //eSelectType.setWhereClause("name = ?"); //where clause, null if not applicable
                            //eSelectType.setOrderClause(null); //order by clause, null if not applicable
                            //eSelectType.setGroupClause(null); //group by clause, null if not applicable
                            //eSelectType.setHavingClause(null); //having clause, null if not applicable
                            //eSelectType.addToParametersList(name);
                            eSelectType.Select();


                            Element thePlan = null;

                            //Get result and show it
                            ResultSet rs = eSelectType.getRs();

                            try {
                                while (rs.next()) {
                                    String xmlplanString = rs.getString("xmlplan");

                                    //convert xml string to a xml element
                                    SAXBuilder builder = new SAXBuilder();
                                    Reader in = new StringReader(xmlplanString);
                                    Document doc = null;
                                    try {
                                        doc = builder.build(in);
                                        thePlan = doc.getRootElement();
                                    } catch (JDOMException ex) {
                                        Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                        thePlan = null;
                                    } catch (IOException ex) {
                                        Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                        thePlan = null;
                                    }

                                   // XMLUtilities.printXML(thePlan);

                                }
                            } catch (SQLException se) {
                              se.printStackTrace();
                              //System.exit(1);
                            }
                            if (thePlan != null){
                                xmlPlan = thePlan;


                                //Gathering Rationales
                                Element rationales = xmlPlan.getChild("evaluation").getChild("rationales");
                                //get rationales registered for this plan
                                //DataBase eSelectTypeRationale = new DataBase();
                                eSelectType.setColumnList("rationale.id, rationale.xmlrationale, rationale.planid, rationale_plan.id, rationale_plan.enabled, rationale_plan.comment"); //please, don't use *
                                eSelectType.setTableName("rationale, rationale_plan");
                                whereclause = "rationale_plan.planid = "+ planID + " AND rationale.id = rationale_plan.rationaleid";
                                //eSelectType.setColumnList("id, xmlrationale"); //please, don't use *
                                //eSelectType.setTableName("rationale");
                                //whereclause = "planid = "+ planID;
                                //Do select in the database
                                eSelectType.setWhereClause(whereclause);
                                eSelectType.setOrderClause("rationale.id"); //order by clause, null if not applicable
                                //eSelectType.setOrderClause("id"); //order by clause, null if not applicable
                                eSelectType.Select();
                                Element theRationale = null;

                                //Get result and show it
                                rs = eSelectType.getRs();

                                try {
                                    while (rs.next()) {
                                        //String rationaleID = rs.getString("id");
                                        String rationaleID = rs.getString(1);
                                        String xmlRationaleString = rs.getString("xmlrationale");
                                        String targetPlanID = rs.getString("planid");
                                        String relationalID = rs.getString(4);
                                        String relationalEnabled = rs.getString(5);
                                        String relationalComment = rs.getString(6);
                                        if(relationalEnabled.trim().equals("t")){
                                            relationalEnabled = "true";
                                        }else if(relationalEnabled.trim().equals("f")){
                                            relationalEnabled = "false";
                                        }

                                        //convert xml string to a xml element
                                        SAXBuilder builder = new SAXBuilder();
                                        Reader in = new StringReader(xmlRationaleString);
                                        Document doc = null;
                                        try {
                                            doc = builder.build(in);
                                            theRationale = doc.getRootElement();
                                        } catch (JDOMException ex) {
                                            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (IOException ex) {
                                            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                        if (theRationale != null){
                                            theRationale.setAttribute("id",rationaleID);                                            
                                            theRationale.setAttribute("relationalid",relationalID);
                                            if (targetPlanID!=null){theRationale.setAttribute("targetplanid",targetPlanID);}
                                            theRationale.setAttribute("enabled",relationalEnabled.trim().toLowerCase());

                                            Element comments = theRationale.getChild("comments");

                                            //add comments
                                            if(relationalComment!=null && !relationalComment.trim().equals("")){
                                                Element currentComments = null;
                                                SAXBuilder commentBuilder = new SAXBuilder();
                                                Reader commentIn = new StringReader(relationalComment);
                                                Document commentDoc = null;
                                                try {
                                                    commentDoc = commentBuilder.build(commentIn);
                                                    currentComments = commentDoc.getRootElement();
                                                } catch (JDOMException ex) {
                                                    Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                                } catch (IOException ex) {
                                                    Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                                }
                                                if (currentComments!=null){
                                                    for (Iterator<Element> it = currentComments.getChildren().iterator(); it.hasNext();) {
                                                        Element currentComment = it.next();
                                                        comments.addContent((Element)currentComment.clone());
                                                    }
                                                }
                                            }

                                            rationales.addContent((Element)theRationale.clone());
                                        }                                        
                                        //XMLUtilities.printXML(theRationale);

                                    }
                                } catch (SQLException se) {
                                  se.printStackTrace();
                                  //System.exit(1);
                                }


                                //XMLUtilities.printXML(thePlan);

                                //show the plan
                                setPlanList(xmlPlan);
                                showHTMLReport(xmlPlan);
                                appendOutputPanelText(">> Plan (id "+planID+") loaded successfully from database. Check the generated Results. \n");

                                currentDBPlanID = Integer.parseInt(planID);
                                isPlanFromDB = true;


                                //Prolog Experiment
                                //String pslplan = PlanAnalyzer.generatePslPlanProlog(xmlPlan,Integer.toString(currentDBPlanID));
                                //System.out.println(pslplan);
                                //System.out.println("");


                            }
                            

                            eSelectType.Close();


                        }
                        

                    }

		}
	};


        /**
         * This action deletes the current plan from the database
         */
        private Action deletePlanFromDatabaseAction = new AbstractAction("Delete", new ImageIcon("resources/images/deleteplanfromdatabase.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e){

                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                    if(selectedNode != null){

                        int row = resultPlanTable.getSelectedRow();
                        if (row > -1){

                            String planID = (String) resultPlanTableModel.getValueAt(row, 0);

                            Element node = selectedNode.getData();
                            //String type ="";
                            //check if it is a projects node
                            //if (node == null){
                            //    type = "projects";
                            //}
                            //else{
                            //    type = node.getName();
                            //}

                            //TODO: check existing rationales
                            //ATTENTION
                            //The rationale will not be deleted
                            //We will only delete the plan references in the rationale_plan table
                            //Using Postgres we set to delete cascade the foreign keys when deleting the plan.
                            // In the rationale table we just set to null automatically when deleting a plan.
                            // All the cascade and set null process is configurated in the constraints of the database postgres


                            String whereclause = "id = "+ planID;

                            DataBase eDeleteType = new DataBase();
                            eDeleteType.setTableName("plan");
                            eDeleteType.setWhereClause(whereclause); //allways use WHERE
                            eDeleteType.Delete();
                            //System.out.println("Rows affected: " + eDeleteType.getRowsAffected());
                            // eDelete template end


                            eDeleteType.Close();

                            resultPlanTableModel.removeRow(row);
                            
                            //check whether the selecet plan is the current loaded
                            int planIDInt = Integer.parseInt(planID);
                            if (currentDBPlanID == planIDInt){
                                cleanupPlanDatabaseReference();
                            }


                            appendOutputPanelText(">> Plan (id "+planID+") deleted successfully from database. \n");
                        }

                    }


		}
	};


        /**
         * This action updates the current plan in the database
         */
        private Action updatePlanFromDatabaseAction = new AbstractAction("Update", new ImageIcon("resources/images/updateplanfromdatabase.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e){
                        if (xmlPlan != null){

                            if (isPlanFromDB){

                                //update plan in the database

                                Element thePlan = (Element)xmlPlan.clone();
                                //Rationales - cleanup rationales to store in the database
                                Element rationales = thePlan.getChild("evaluation").getChild("rationales");
                                rationales.removeContent();
                                //The XML plan (clean up ' char - database need)
                                String xmlPlanStr = XMLUtilities.toString(thePlan);
                                xmlPlanStr = xmlPlanStr.replace("'", "''");

                                //projec, domain and problem names/identifiers
                                String project = xmlPlan.getChildText("project").replace("'", "''");
                                String domain = xmlPlan.getChildText("domain").replace("'", "''");
                                String problem = xmlPlan.getChildText("problem").replace("'", "''");

                                //planner name/identifier
                                String plannername = "";
                                String plannerversion = "";
                                Element planner = xmlPlan.getChild("planner");
                                if (planner != null && !planner.getAttributeValue("id").equals("")){
                                    plannername = planner.getChildText("name");
                                    plannerversion = planner.getChildText("version");
                                }

                                //number of actions in the plan
                                String nactions = String.valueOf(xmlPlan.getChild("plan").getChildren().size());

                                //plan cost based on the metrics.
                                double overallCostAward = 0;
                                Element metrics = xmlPlan.getChild("metrics");
                                if(metrics!=null){
                                    overallCostAward = PlanAnalyzer.evaluateCostAward(metrics);
                                }

                                //quality of the plan
                                String quality = "";
                                Element evaluation =  xmlPlan.getChild("evaluation");
                                if (evaluation != null){
                                    quality = evaluation.getAttributeValue("value");
                                }

                                //update plan in the database
                                DataBase updateType = new DataBase();
                                updateType.setTableName("plan");
                                updateType.setUpdateValueList("xmlplan = '"+xmlPlanStr+"', project = '" + project + "', domain = '" + domain +
                                        "', problem = '" + problem + "', plannername = '" + plannername + "', plannerversion = '" + plannerversion +
                                        "', nactions = " + nactions + ", cost = " + Double.toString(overallCostAward) + ", quality = " + quality);
                                updateType.setWhereClause("id = " + currentDBPlanID); //allways use WHERE
                                updateType.Update();
                                //System.out.println("Rows affected on eUpdate: " + updateType.getRowsAffected());


                                //Rationales

                                DataBase insertType = new DataBase();
                                //DataBase updateType = new DataBase();

                                rationales = xmlPlan.getChild("evaluation").getChild("rationales");

                                for (Iterator<Element> it = rationales.getChildren().iterator(); it.hasNext();) {
                                    Element rationale = it.next();

                                    String rationaleID = rationale.getAttributeValue("id").trim();
                                    String targetPlanID = rationale.getAttributeValue("targetplanid").trim();
                                    String relationalID = rationale.getAttributeValue("relationalid").trim();
                                    String relationalEnabled = rationale.getAttributeValue("enabled").trim().toUpperCase();


                                    //1. Insert rationale into the database if it has been inserted
                                    if (rationaleID.equals("")){

                                        Element theRationale = (Element)rationale.clone();
                                        theRationale.setAttribute("targetplanid","");
                                        theRationale.setAttribute("relationalid","");
                                        theRationale.setAttribute("enabled", "true");
                                        theRationale.getChild("comments").removeContent();
                                        
                                        String xmlRationaleStr = XMLUtilities.toString(theRationale).replace("'", "''");
                                        String rationaleName = rationale.getChildText("name").replace("'", "''");
                                        String rationaleDescription = rationale.getChildText("description").replace("'", "''");
                                        String rationaleRule = rationale.getChildText("rule").replace("'", "''");
                                        String rationaleQualityImpact = rationale.getChild("impact").getAttributeValue("quality");
                                        String rationaleRange = rationale.getChild("abstractionlevel").getAttributeValue("range");
                                        String rationaleValidity = rationale.getChild("validity").getAttributeValue("isValid").trim().toUpperCase();
                                        if (rationaleValidity.equals("")){
                                            rationaleValidity = "NULL";
                                        }

                                        insertType.setTableName("rationale");
                                        insertType.setColumnList("xmlrationale, name, description, rule, abstractionlevel, project, domain, problem, planid, validity, qualityimpact"); // please, don't use ()
                                        insertType.setValueList("'"+ xmlRationaleStr+"', '"+rationaleName+"', '"+rationaleDescription+"', '"+rationaleRule+"', '"+rationaleRange+"', '"+project+"', '"+domain+"', '"+problem+"', "+currentDBPlanID+", "+rationaleValidity+", '"+rationaleQualityImpact+"'"); // please, don't use ()
                                        //updateType.setParametersList(list);
                                        insertType.retrieveLastID(true);
                                        insertType.Insert();

                                        //set rationale id
                                        rationaleID = Integer.toString(insertType.getLastInsertID());
                                        rationale.setAttribute("id", rationaleID);

                                        //set target plan id
                                        targetPlanID = Integer.toString(currentDBPlanID);
                                        rationale.setAttribute("targetplanid", targetPlanID);

                                        System.out.println("Rationale created - "+ rationaleID);
                                    }


                                    //2.  if target plan is not already defined, set for the current plan
                                    // this can happen when a plan is removed and another plan has the rationale applied to it
                                    // then this plan will assume the rationale's reference
                                    if (targetPlanID.equals("") && !rationaleID.equals("")){

                                        //update rationale
                                        updateType.setTableName("rationale");
                                        updateType.setUpdateValueList("planid="+currentDBPlanID+", project='"+project+"', domain='"+domain+"', problem='"+problem+"'");
                                        updateType.setWhereClause("id = " + rationaleID); //allways use WHERE
                                        updateType.Update();

                                        targetPlanID = Integer.toString(currentDBPlanID);
                                        rationale.setAttribute("targetplanid", targetPlanID);

                                        //set target plan id
                                        System.out.println("Unlinked rationale updated - "+ rationaleID);
                                    }

                                    //3. Insert record into relational table rationale_pla
                                    if (relationalID.equals("")){

                                        //comments
                                        String comment = XMLUtilities.toString(rationale.getChild("comments"));

                                        insertType.setTableName("rationale_plan");
                                        insertType.setColumnList("rationaleid, planid, comment, enabled");
                                        insertType.setValueList(rationaleID+", "+Integer.toString(currentDBPlanID)+", '"+comment+"', "+relationalEnabled); // please, don't use ()
                                        //updateType.setParametersList(list);
                                        insertType.retrieveLastID(true);
                                        insertType.Insert();

                                        relationalID = Integer.toString(insertType.getLastInsertID());
                                        rationale.setAttribute("relationalid", relationalID);
                                    }

                                    //4. Update if the rationale was modified (i.e., it has a child node called 'modified')
                                    Element modified = rationale.getChild("modified");
                                    if (modified!=null && !rationaleID.equals("")){

                                        Element theRationale = (Element)rationale.clone();
                                        theRationale.setAttribute("id", "");
                                        theRationale.setAttribute("targetplanid", "");
                                        theRationale.setAttribute("relationalid", "");
                                        theRationale.setAttribute("enabled", "true");
                                        theRationale.getChild("comments").removeContent();
                                        theRationale.removeContent(theRationale.getChild("modified"));
                                        if(theRationale.getChild("instruction")!=null){
                                            theRationale.removeContent(theRationale.getChild("instruction"));
                                        }


                                        String xmlRationaleStr = XMLUtilities.toString(theRationale).replace("'", "''");
                                        String rationaleName = rationale.getChildText("name").replace("'", "''");
                                        String rationaleDescription = rationale.getChildText("description").replace("'", "''");
                                        String rationaleRule = rationale.getChildText("rule").replace("'", "''");
                                        String rationaleQualityImpact = rationale.getChild("impact").getAttributeValue("quality");
                                        String rationaleRange = rationale.getChild("abstractionlevel").getAttributeValue("range");
                                        String rationaleValidity = rationale.getChild("validity").getAttributeValue("isValid").trim().toUpperCase();
                                        if (rationaleValidity.equals("")){
                                            rationaleValidity = "NULL";
                                        }

                                        updateType.setTableName("rationale");
                                        updateType.setUpdateValueList("xmlrationale = '"+xmlRationaleStr+"', name = '" + rationaleName + "', description = '" + rationaleDescription +
                                                "', rule = '" + rationaleRule + "', abstractionlevel = '" + rationaleRange+"', validity = "+rationaleValidity+", qualityimpact = '"+rationaleQualityImpact+"'");
                                        updateType.setWhereClause("id = " + rationaleID); //allways use WHERE
                                        updateType.Update();

                                        rationale.removeContent(modified);

                                        System.out.println("Modified rationale updated - "+ rationaleID);

                                    }

                                    //5. Check if there is anything to perform in the instructions
                                    Element instruction = rationale.getChild("instruction");
                                    if (instruction!=null && !rationaleID.equals("") && !relationalID.equals("")){
                                        String theInstruction = instruction.getAttributeValue("perform");

                                        //delete reference (rationale_plan)
                                        if (theInstruction.equals("delete-reference")){
                                            DataBase eDeleteType = new DataBase();
                                            eDeleteType.setTableName("rationale_plan");
                                            eDeleteType.setWhereClause("id = " + relationalID); //allways use WHERE
                                            eDeleteType.Delete();
                                            eDeleteType.Close();
                                            
                                            rationale.removeContent(instruction);
                                            System.out.println("Rationale reference deleted - "+ relationalID);
                                        }
                                        //update reference (rationale_plan)
                                        else if (theInstruction.equals("update-reference")){
                                            //comments
                                            String comment = XMLUtilities.toString(rationale.getChild("comments"));
                                            updateType.setTableName("rationale_plan");
                                            updateType.setUpdateValueList("comment = '"+comment+"', enabled = " + relationalEnabled);
                                            updateType.setWhereClause("id = " + relationalID); //allways use WHERE
                                            updateType.Update();

                                            rationale.removeContent(instruction);
                                            System.out.println("Rationale reference updated - "+ relationalID);
                                        }
                                    }
                                }

                                updateType.Close();
                                insertType.Close();


                                
                                appendOutputPanelText(">> Plan (id "+currentDBPlanID+") updated. \n");



                            }
                            else{
                                JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>The current plan has not been loaded from the database.<br>Update is not applicable.</html>");
                                //System.out.println("The current plan has not been loaded from the database. \nUpdate is not applicable.");

                            }
                        }
		}
	};


        /**
         * This action search in the database for rationales matching the selecetd node in the problem list
         */
        private Action searchRationaleInDatabaseAction = new AbstractAction("Search", new ImageIcon("resources/images/searchdatabase.png")){
		/**
		 *
		 */
                @SuppressWarnings("empty-statement")
		public void actionPerformed(ActionEvent e){

                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                    if(selectedNode != null){

                        //clean up the result table first
                        resultRationaleTableModel.getDataVector().removeAllElements();
                        resultRationaleTable.repaint();

                        Element node = selectedNode.getData();
                        String type ="";
                        //check if it is a projects node
                        if (node == null){
                            type = "projects";
                        }
                        else{
                            type = node.getName();
                        }



                        DataBase eSelectType = new DataBase();
                        eSelectType.setColumnList("id, name, abstractionlevel, project, domain, problem, planid, qualityimpact"); //please, don't use *
                        eSelectType.setTableName("rationale");
                        String whereclause = "";

                        String project = "";
                        String domain = "";
                        String problem = "";

                        switch(selectedNode.getLevel()){
                            case 0: //Projects
                                //System.out.println("Search within Projects");

                                whereclause = null;

                                break;
                            case 1: //Project
                                //System.out.println("Search within a specific Project");
                                //project = node.getDocument().getRootElement().getChildText("name");
                                project = node.getChildText("name");

                                whereclause = "project = '"+project+"'";


                                break;
                            case 2: //Domain
                                //System.out.println("Search within a specific Domain");
                                project = node.getDocument().getRootElement().getChildText("name");
                                domain = node.getChildText("name");

                                whereclause = "project = '"+project+"' AND  domain='"+domain+"'";

                                break;
                            case 3: //Problem
                                //System.out.println("Search within a specific Problem");
                                project = node.getDocument().getRootElement().getChildText("name");
                                domain = node.getParentElement().getParentElement().getChildText("name");
                                problem = node.getChildText("name");

                                whereclause = "project = '"+project+"' AND  domain='"+domain+"' AND  problem='"+problem+"'";
                                break;
                        }

                        //Do select in the database
                        eSelectType.setWhereClause(whereclause);
                        eSelectType.setOrderClause("id"); //order by clause, null if not applicable
                        //eSelectType.setGroupClause(null); //group by clause, null if not applicable
                        //eSelectType.setHavingClause(null); //having clause, null if not applicable
                        //eSelectType.addToParametersList(name);
                        eSelectType.Select();


                        //Get results and show them
                        ResultSet rs = eSelectType.getRs();
                        try {
                            int counter = 1;
                            while (rs.next()) {
                                Vector<String> rowData = new Vector<String>();

                                rowData.add(rs.getString("id"));
                                //rowData.add(String.valueOf(counter));
                                rowData.add(rs.getString("name"));
                                rowData.add(rs.getString("abstractionlevel"));
                                rowData.add(rs.getString("project"));
                                rowData.add(rs.getString("domain"));
                                rowData.add(rs.getString("problem"));
                                rowData.add(rs.getString("planid"));
                                rowData.add(rs.getString("qualityimpact"));

                                resultRationaleTableModel.addRow(rowData);
                                counter++;

                            }

                        } catch (SQLException se) {;
                          se.printStackTrace();
                          //System.exit(1);
                        }

                        eSelectType.Close();

                    }

		}
	};

        
        /**
         * This action opens the dialog for editing plan evaluation
         */
        private Action editRationaleFromDatabaseAction = new AbstractAction("Edit Rationale", new ImageIcon("resources/images/edit.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e){
                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                    if(selectedNode != null){

                        int row = resultRationaleTable.getSelectedRow();
                        if (row > -1){

                            String rationaleID = (String) resultRationaleTableModel.getValueAt(row, 0);

                            //Element node = selectedNode.getData();
                            //String type ="";
                            //check if it is a projects node
                            //if (node == null){
                            //    type = "projects";
                            //}
                            //else{
                            //    type = node.getName();
                            //}


                            DataBase eSelectType = new DataBase();
                            eSelectType.setColumnList("xmlrationale"); //please, don't use *
                            eSelectType.setTableName("rationale");
                            String whereclause = "id = "+ rationaleID;
                            //Do select in the database
                            eSelectType.setWhereClause(whereclause);
                            eSelectType.Select();


                            Element theRationale = null;

                            //Get result and show it
                            ResultSet rs = eSelectType.getRs();

                            try {
                                while (rs.next()) {
                                    String xmlrationaleString = rs.getString("xmlrationale");

                                    //convert xml string to a xml element
                                    SAXBuilder builder = new SAXBuilder();
                                    Reader in = new StringReader(xmlrationaleString);
                                    Document doc = null;
                                    try {
                                        doc = builder.build(in);
                                        theRationale = doc.getRootElement();
                                    } catch (JDOMException ex) {
                                        Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                        theRationale = null;
                                    } catch (IOException ex) {
                                        Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                        theRationale = null;
                                    }

                                   // XMLUtilities.printXML(theRationale);

                                }
                            } catch (SQLException se) {
                              se.printStackTrace();
                              //System.exit(1);
                            }
                            if (theRationale != null){
                                theRationale.setAttribute("id",rationaleID);
                                final RationaleEditDialog dialog = new RationaleEditDialog(ItSIMPLE.this, theRationale);
                                dialog.setVisible(true);

                                //refresh the row if it was changed and saved
                                if (dialog.isSaved()){
                                    Element modifiedRationale = dialog.getXMLRationale();
                                    //resultRationaleTableModel.setValueAt(modifiedRationale.getChildText("name"), row, 2);
                                    //resultRationaleTableModel.setValueAt(modifiedRationale.getChild("abstractionlevel").getAttributeValue("range"), row, 3);
                                    resultRationaleTableModel.setValueAt(modifiedRationale.getChildText("name"), row, 1);
                                    resultRationaleTableModel.setValueAt(modifiedRationale.getChild("abstractionlevel").getAttributeValue("range"), row, 2);
                                    resultRationaleTableModel.setValueAt(modifiedRationale.getChild("impact").getAttributeValue("quality"), row, 7);
                                }
                            }

                        }
                    }
            }
	};


        /**
         * This action deletes the current selected rationale from the database
         */
        private Action deleteRationaleFromDatabaseAction = new AbstractAction("Delete", new ImageIcon("resources/images/deleteplanfromdatabase.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e){

                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                    if(selectedNode != null){

                        int row = resultRationaleTable.getSelectedRow();
                        if (row > -1){

                            String rationaleID = (String) resultRationaleTableModel.getValueAt(row, 0);

                            Element node = selectedNode.getData();
                            //String type ="";
                            //check if it is a projects node
                            //if (node == null){
                            //    type = "projects";
                            //}
                            //else{
                            //    type = node.getName();
                            //}

                            //ATTENTION
                            //Using Postgres we set to delete cascade the foreign keys when deleting arationale.
                            // All the cascade and set null process is configurated in the constraints of the database postgres

                            String whereclause = "id = "+ rationaleID;

                            DataBase eDeleteType = new DataBase();
                            eDeleteType.setTableName("rationale");
                            eDeleteType.setWhereClause(whereclause); //allways use WHERE
                            eDeleteType.Delete();
                            //System.out.println("Rows affected: " + eDeleteType.getRowsAffected());
                            // eDelete template end

                            eDeleteType.Close();

                            resultRationaleTableModel.removeRow(row);

                            //check whether the selecet plan is the current loaded
                            //int rationaleIDInt = Integer.parseInt(rationaleID);
                            //if (currentDBRationaleID == rationaleIDInt){
                            //    cleanupPlanDatabaseReference();
                            //}


                            appendOutputPanelText(">> Rationale (id "+rationaleID+") deleted successfully from database. \n");
                        }

                    }

		}
	};


        /**
         * This action loads the current plan from the database
         */
        private Action loadReferencePlanFromDatabaseAction = new AbstractAction("Load", new ImageIcon("resources/images/getfromdatabase.png")){
		/**
		 *
		 */
		public void actionPerformed(ActionEvent e){

                    ItTreeNode selectedNode = (ItTreeNode)problemsPlanTree.getLastSelectedPathComponent();
                    if(selectedNode != null){

                        int row = resultRationaleTable.getSelectedRow();
                        if (row > -1){

                            //get the plan ID from the selected rationale
                            //String planID = (String) resultRationaleTableModel.getValueAt(row, 7);
                            String planID = (String) resultRationaleTableModel.getValueAt(row, 6);

                            Element node = selectedNode.getData();
                            //String type ="";
                            //check if it is a projects node
                            //if (node == null){
                            //    type = "projects";
                            //}
                            //else{
                            //    type = node.getName();
                            //}


                            DataBase eSelectType = new DataBase();
                            eSelectType.setColumnList("xmlplan"); //please, don't use *
                            eSelectType.setTableName("plan");
                            String whereclause = "id = "+ planID;

                            //Do select in the database
                            eSelectType.setWhereClause(whereclause);
                            //eSelectType.setWhereClause("name = ?"); //where clause, null if not applicable
                            //eSelectType.setOrderClause(null); //order by clause, null if not applicable
                            //eSelectType.setGroupClause(null); //group by clause, null if not applicable
                            //eSelectType.setHavingClause(null); //having clause, null if not applicable
                            //eSelectType.addToParametersList(name);
                            eSelectType.Select();


                            Element thePlan = null;

                            //Get result and show it
                            ResultSet rs = eSelectType.getRs();

                            try {
                                while (rs.next()) {
                                    String xmlplanString = rs.getString("xmlplan");

                                    //convert xml string to a xml element
                                    SAXBuilder builder = new SAXBuilder();
                                    Reader in = new StringReader(xmlplanString);
                                    Document doc = null;
                                    try {
                                        doc = builder.build(in);
                                        thePlan = doc.getRootElement();
                                    } catch (JDOMException ex) {
                                        Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                        thePlan = null;
                                    } catch (IOException ex) {
                                        Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                        thePlan = null;
                                    }

                                   // XMLUtilities.printXML(thePlan);

                                }
                            } catch (SQLException se) {
                              se.printStackTrace();
                              //System.exit(1);
                            }
                            if (thePlan != null){
                                xmlPlan = thePlan;

                                //Gathering Rationales
                                Element rationales = xmlPlan.getChild("evaluation").getChild("rationales");
                                //get rationales registered for this plan
                                //DataBase eSelectTypeRationale = new DataBase();
                                eSelectType.setColumnList("rationale.id, rationale.xmlrationale, rationale.planid, rationale_plan.id, rationale_plan.enabled"); //please, don't use *
                                eSelectType.setTableName("rationale, rationale_plan");
                                whereclause = "rationale_plan.planid = "+ planID + " and rationale.id = rationale_plan.rationaleid";
                                //eSelectType.setColumnList("id, xmlrationale"); //please, don't use *
                                //eSelectType.setTableName("rationale");
                                //whereclause = "planid = "+ planID;
                                //Do select in the database
                                eSelectType.setWhereClause(whereclause);
                                eSelectType.setOrderClause("rationale.id"); //order by clause, null if not applicable
                                //eSelectType.setOrderClause("id"); //order by clause, null if not applicable
                                eSelectType.Select();
                                Element theRationale = null;

                                //Get result and show it
                                rs = eSelectType.getRs();

                                try {
                                    while (rs.next()) {
                                        //String rationaleID = rs.getString("id");
                                        String rationaleID = rs.getString(1);
                                        String xmlRationaleString = rs.getString("xmlrationale");
                                        String targetPlanID = rs.getString("planid");
                                        String relationalID = rs.getString(4);
                                        String relationalEnabled = rs.getString(5);
                                        if(relationalEnabled.trim().equals("t")){
                                            relationalEnabled = "true";
                                        }else if(relationalEnabled.trim().equals("f")){
                                            relationalEnabled = "false";
                                        }

                                        //convert xml string to a xml element
                                        SAXBuilder builder = new SAXBuilder();
                                        Reader in = new StringReader(xmlRationaleString);
                                        Document doc = null;
                                        try {
                                            doc = builder.build(in);
                                            theRationale = doc.getRootElement();
                                        } catch (JDOMException ex) {
                                            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                        } catch (IOException ex) {
                                            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                                        }

                                        if (theRationale != null){
                                            theRationale.setAttribute("id",rationaleID);
                                            theRationale.setAttribute("relationalid",relationalID);
                                            if (targetPlanID!=null){
                                                theRationale.setAttribute("targetplanid",targetPlanID);}
                                            theRationale.setAttribute("enabled",relationalEnabled.trim().toLowerCase());

                                            rationales.addContent((Element)theRationale.clone());
                                        }
                                        //XMLUtilities.printXML(theRationale);

                                    }
                                } catch (SQLException se) {
                                  se.printStackTrace();
                                  //System.exit(1);
                                }


                                //XMLUtilities.printXML(thePlan);

                                //show the plan
                                setPlanList(xmlPlan);
                                showHTMLReport(xmlPlan);
                                appendOutputPanelText(">> Plan (id "+planID+") loaded successfully from database. Check the generated Results. \n");

                                currentDBPlanID = Integer.parseInt(planID);
                                isPlanFromDB = true;


                                //Prolog Experiment
                                //String pslplan = PlanAnalyzer.generatePslPlanProlog(xmlPlan,Integer.toString(currentDBPlanID));
                                //System.out.println(pslplan);
                                //System.out.println("");


                            }

                            eSelectType.Close();


                        }


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
                                                "Please visit our website http://code.google.com/p/itsimple/</html>");
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
                                newProjectNode.setInfoString(path);

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


				// update plan simulation problem tree
                                updateNewProjectParallelTree(problemsPlanTreeModel,problemsPlanTree,doc, xmlRoot, (ItTreeNode)newProjectNode.clone());                                
                                
                                //update analysis tree
                                updateNewProjectParallelTree(projectAnalysisTreeModel,projectAnalysisTree,doc, xmlRoot, (ItTreeNode)newProjectNode.clone());                                
                                //updateNewNodeProjectAnalysisTree(doc, xmlRoot, (ItTreeNode)newProjectNode.clone());

                                //if (xmlRoot.getName().equals("project")){
                                //    //update pddl tree
                                    updateNewProjectParallelTree(pddlTranslationTreeModel,modelTranslationTree,doc, xmlRoot, (ItTreeNode)newProjectNode.clone());
                                //}
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
		fileMenu.add(getNewMenu());
		fileMenu.add(getOpenMenuItem());
                fileMenu.add(getOpenRecentProjectMenu());
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
                
                //Modeling Pattern
                fileMenu.add(getImportMenu());
                fileMenu.addSeparator();

		fileMenu.add(getExitMenuItem());

		return fileMenu;
	}

	/**
	 * This method initializes newMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getNewMenu() {
		if (newMenu == null) {
                    newMenu = new JMenu();
			newMenu.setText("New Project");
                        newMenu.setIcon(new ImageIcon("resources/images/new24.png"));
			newMenu.add(getNewUMLMenuItem());
			newMenu.add(getNewPDDLMenuItem());
		}
		return newMenu;
	}
        
        /**
	 * This method initializes newMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getNewUMLMenuItem() {
		if (newUMLMenuItem == null) {
                        newUMLMenuItem = new JMenuItem(newProjectAction);
			//newMenuItem.setIcon(new ImageIcon("resources/images/new24.png"));
			newUMLMenuItem.setMnemonic(KeyEvent.VK_N);
			newUMLMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, Event.CTRL_MASK, true));
		}
		return newUMLMenuItem;
	}
        
        /**
	 * This method initializes newMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getNewPDDLMenuItem() {
		if (newPDDLMenuItem == null) {
                        newPDDLMenuItem = new JMenuItem(newPddlProjectAction);
			//newMenuItem.setIcon(new ImageIcon("resources/images/new24.png"));
			newPDDLMenuItem.setMnemonic(KeyEvent.VK_P);
			newPDDLMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P, Event.CTRL_MASK, true));
		}
		return newPDDLMenuItem;
	}
        
        
        
                
/**
	 * This method initializes newMenuItem
	 *
	 * @return javax.swing.JMenuItem
	 */
	private JMenuItem getOpenRecentProjectMenu() {
		if (openRecentMenu == null) {
                    openRecentMenu = new JMenu();
                    openRecentMenu.setText("Open Recent Project");
                }

                if (openRecentMenu != null){
                    openRecentMenu.removeAll();
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
                                            openRecentMenu.add(recentProjectItem);
                                            projectCounter++;
                             }
                    }
                        
			
		}
		return openRecentMenu;
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
        
        //Import and Export
        private JMenuItem getImportMenu() {
		if (importMenu == null) {
                    importMenu = new JMenu();
			importMenu.setText("Import");
                        //newMenu.setIcon(new ImageIcon("resources/images/new24.png"));
                        importMenu.add(getImportPDDLProjectMenuItem());
			importMenu.add(getImportModelingPatternMenuItem());
                        
		}
		return importMenu;
	}
        
        private JMenuItem getImportPDDLProjectMenuItem() {
		if (importPDDLProjectMenuItem == null) {
			importPDDLProjectMenuItem = new JMenuItem();
			importPDDLProjectMenuItem.setText("Import PDDL project");
			importPDDLProjectMenuItem.setAction(importPDDLProjectAction);
		}
		return importPDDLProjectMenuItem;
	}        
        
        private JMenuItem getImportModelingPatternMenuItem() {
		if (importModelingPatternMenuItem == null) {
			importModelingPatternMenuItem = new JMenuItem();
			importModelingPatternMenuItem.setText("Import Modeling Pattern");
			importModelingPatternMenuItem.setAction(importModelingPatternAction);
		}
		return importModelingPatternMenuItem;
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
         * This method sets the title of the properties panel (default ':: Properties')
         * @param title
         */
        public void setPropertiesPanelTitle(String title){
            propertiesFramePanel.setTitle(title);
        }
        //TODO: set the title of the docker

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
							if(selected.getName().equals("repositoryDiagram")){
                                                            setDiagramRespository(selected);
                                                            setTypeDiagram(0);
                                                        }
                                                        if(selected.getName().equals("objectDiagram")){
                                                            setObjectDiagram(selected);                                                            
                                                            setTypeDiagram(1);
                                                        }
                                                        
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
                                                        String projectType = selectedNode.getData().getDocument().getRootElement().getName();

                                                        //check if this is a UML project
                                                        if (projectType.equals("project")){
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
                                                        //check if this is a PDDL project
                                                        else if (projectType.equals("pddlproject")){
                                                            //open pddl text for edit

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
	public ItTabbedPane getGraphTabbedPane() {
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
                        if (selected.getData() != null){
                            
                            String projectType = selected.getData().getDocument().getRootElement().getName();

                            //Check if this is a UML project
                            if (projectType.equals("project")){
                                    switch (selected.getLevel()) {
                                        case 1:{
                                                treePopupMenu.add(getNewDiagramMenu());
                                                treePopupMenu.add(importModelingPatternAction);
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
                            //Check if this is a UML project
                            else if (projectType.equals("pddlproject")) {
                                switch (selected.getLevel()) {
                                        case 1:{
                                            //TODO: getNewMenu (problem)
                                                //treePopupMenu.add(getNewDiagramMenu());
                                                //treePopupMenu.addSeparator();
                                                treePopupMenu.add(closeProjectAction);
                                        }
                                        break;
                                        case 2:{
                                                if (selected.getData().getName().equals("pddldomain")){
                                                        // domain menus
                                                        //treePopupMenu.add(getNewDomainMenu())
                                                        treePopupMenu.add(openPDDLFile);
                                                        //treePopupMenu.add(duplicateAction);
                                                        //treePopupMenu.addSeparator();
                                                        //treePopupMenu.add(deletePDDLDomainAction);

                                                }
                                                else{
                                                        //add new problem instance
                                                        treePopupMenu.add(newPDDLProblemFileAction);                                            
                                                }
                                        }
                                        break;
                                        case 3:{
                                            //will we have anything in this leve;?
                                            treePopupMenu.add(openPDDLFile);
                                            //treePopupMenu.add(duplicateAction);
                                            treePopupMenu.addSeparator();
                                            treePopupMenu.add(deletePDDLProblemAction);
                                        }
                                        break;
                                        default:
                                                break;
                                    }                        

                            }                            
                            
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
	 * This method initializes propertiesPane
	 *
	 * @return javax.swing.ItTabbedPane
	 */
	private ItTabbedPane getDiagramPane() {
		if (graphTabbedPane == null) {
			graphTabbedPane = new ItTabbedPane();
		}
		return graphTabbedPane;
	}
	
	
	/**
	 * This method initializes propertiesPane
	 *
	 * @return javax.swing.ItTabbedPane
	 */
	private JEditorPane getDiagramInfoPane() {
		if (infoEditorPane == null) {
			infoEditorPane = new JEditorPane();
			infoEditorPane.setContentType("text/html");
			infoEditorPane.setEditable(false);
			infoEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
		}
		return infoEditorPane;
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


	private JPanel getMainTabbedPane() {
		if (mainTabbedPane == null) {
			mainTabbedPane = new JPanel(new CardLayout());
			
			
			
			//MODELING Perpective			
			JPanel contentPane = new JPanel(new BorderLayout());
			//LookAndFeelList list = LookAndFeelList.getDefaultList();
			dockingcontrol = new CControl(this);			
			ThemeMap themes = dockingcontrol.getThemes() ;
			themes.select(ThemeMap.KEY_ECLIPSE_THEME);
			//themes.add("custom", new CustomFactory());
			
			/*
			CControlPerspective perspectives = control.getPerspectives();
			CPerspective welcomePerspective = perspectives.createEmptyPerspective();
		       
	        CGridPerspective gridPerspective = welcomePerspective.getContentArea().getCenter();
	       
	        gridPerspective.gridAdd(0,0,6,10, new SingleCDockablePerspective("welcomeDockID"));
	        gridPerspective.gridAdd(6,0,6,10, new SingleCDockablePerspective("newsDockID"));
	       
	        //Set your perspective (i.e. initial layout) in the usual way. Call this the 'Default' perspective.
	        perspectives.setPerspective("welcomePerspective", welcomePerspective);
	       
	        //Optional: For each Default perspective definition, set a secondary unique perspective if you wish to enable the user to reset
	        //back to the default perspective at any point during runtime. Call this the 'Reset' perspective.
	        //If you do not define this line, you will deactivate the ability to reset the default perspective, but Docking Frames will still
	        //continue to track and save the latest changes made to the perspective. Essentially, the Reset setting is a copy of the Default
	        //setting, i.e. each layout perspective has a pair of saved perspectives in the framework. It may help if you append 'Reset' in
	        //the name.
	        perspectives.setPerspective("welcomePerspectiveReset", welcomePerspective);
	        */

		 
			
			//CGrid grid = new CGrid(control);
			dockingcontent = dockingcontrol.getContentArea();
			
			contentPane.add(dockingcontent, BorderLayout.CENTER);
			//ComponentCollector collector = new DockableCollector(control.intern());
			//list.addComponentCollector(collector);
			
			JPanel projectexplorerpanel = new JPanel(new BorderLayout());
			projectexplorerpanel.setOpaque(true);
			projectexplorerpanel.add(getProjectsTree(), BorderLayout.CENTER); //content
			projectexplorerDock = new DefaultSingleCDockable("Project_Explorer", new ImageIcon("resources/images/project-structure-icon.png"), "Project Explorer" , projectexplorerpanel);
			//control.addDockable(projectexplorerpanel);
			//grid.add(0, 0, 0.1, 0.5,  projectexplorerDock);

			JPanel propertiespanel = new JPanel(new BorderLayout());
			propertiespanel.setOpaque(true);
			propertiespanel.add(getPropertiesPane(), BorderLayout.CENTER); //content
			propertiesDock = new DefaultSingleCDockable("Properties", new ImageIcon("resources/images/property-icon.png"), "Properties", propertiespanel); 
			//control.addDockable(propertiespanel);
			//grid.add(0, 0.5, 0.1, 0.5, propertiesDock);
	
			JPanel diagrampanel = new JPanel(new BorderLayout());
			diagrampanel.setOpaque(true);
			diagrampanel.add(getDiagramPane(), BorderLayout.CENTER); //content
			diagramDock = new DefaultSingleCDockable("Diagrams", new ImageIcon("resources/images/property-icon.png"), "Diagrams", diagrampanel); 
			//control.addDockable(propertiespanel);
			//grid.add(1, 0, 0.82, 0.9, diagramDock);
			
			JPanel modelinfopanel = new JPanel(new BorderLayout());
			modelinfopanel.setOpaque(true);
			modelinfopanel.add(getDiagramInfoPane(), BorderLayout.CENTER); //content
			modelInfoDock = new DefaultSingleCDockable("Model_Info", new ImageIcon("resources/images/property-icon.png"), "Info", modelinfopanel); 
			//control.addDockable(propertiespanel);
			//grid.add(0.7, 1, 0.9, 0.1, modelInfoDock);
			
			JPanel additionalModelpanel = new JPanel(new BorderLayout());
			additionalModelpanel.setOpaque(true);
			additionalModelpanel.add(AdditionalPropertiesTabbedPane.getInstance(), BorderLayout.CENTER);
			additionalmodelDock = new DefaultSingleCDockable("Additional", new ImageIcon("resources/images/property-icon.png"), "Additional", additionalModelpanel); 
			//control.addDockable(propertiespanel);
			//grid.add(2, 0, 0.08, 0.9, additionalmodelDock);
			
			
			
			
			
			// PLANNING perspective
			JPanel problem_selection_panel = new JPanel(new BorderLayout());
			problem_selection_panel.setOpaque(true);
			problem_selection_panel.add(getProblemSelectionPanel(), BorderLayout.CENTER); //content
			problemSelectionDock = new DefaultSingleCDockable("Problem_Selection", new ImageIcon("resources/images/project-structure-icon.png"), "Problem Selection" , problem_selection_panel);
			//grid.add(0, 0, 0.1, 0.5,  problemSelectionDock);

			JPanel plan_panel = new JPanel(new BorderLayout());
			plan_panel.setOpaque(true);
			plan_panel.add(getPlanListPanel(), BorderLayout.CENTER); //content
			planDock = new DefaultSingleCDockable("Plan", new ImageIcon("resources/images/property-icon.png"), "Plan", plan_panel); 
			//grid.add(0, 0.5, 0.1, 0.5, planDock);
	
			JPanel plan_analysis_panel = new JPanel(new BorderLayout());
			plan_analysis_panel.setOpaque(true);
			plan_analysis_panel.add(getPlanAnalysisPanel(), BorderLayout.CENTER); //content
			plananalysisDock = new DefaultSingleCDockable("Plan_Analysis", new ImageIcon("resources/images/property-icon.png"), "Plan Analysis", plan_analysis_panel); 
			//grid.add(1, 0, 0.82, 0.9, plananalysisDock);
			
			JPanel console_panel = new JPanel(new BorderLayout());
			console_panel.setOpaque(true);
			console_panel.add(getPlanInfoPanel(), BorderLayout.CENTER); //content
			consoleDock = new DefaultSingleCDockable("Console", new ImageIcon("resources/images/property-icon.png"), "Console", console_panel); 
			//grid.add(0.7, 1, 0.9, 0.1, consoleDock);
			
			JPanel plan_navigation_panel = new JPanel(new BorderLayout());
			plan_navigation_panel.setOpaque(true);
			plan_navigation_panel.add(getPlanNavigationPanel(), BorderLayout.CENTER);
			plannavigationDock = new DefaultSingleCDockable("Plan_Navigation", new ImageIcon("resources/images/property-icon.png"), "Plan_Navigation", plan_navigation_panel); 
			//grid.add(2, 0, 0.08, 0.9, plannavigationDock);
			
			
			
			
			//xxx
			
			
			//content.deploy(grid);
						
			setPerspective("Modeling");
			
			
			
			mainTabbedPane.add(contentPane, "Modeling");
			
            mainTabbedPane.add(getAnalysisPane(), "Analysis");
			//mainTabbedPane.add(getPetriSplitPane(), "Petri Net");
			mainTabbedPane.add(getTranslationSplitPane(), "Translation");

			
			//mainTabbedPane.add(getPlanSimPane(), "Planning");
			
           
            
            
            
            CardLayout cl = (CardLayout)(mainTabbedPane.getLayout());
            cl.show(mainTabbedPane, "UML"); 


		}
		return mainTabbedPane;
	}
	
	
	
	/**
	 * This function sets the perspective
	 * @param perspective
	 */
	private void setPerspective(String perspective){
		//dockingcontent.removeAll();
		dockinggrid = null;
		//System.gc();
		Runtime.getRuntime().gc();
		dockinggrid = new CGrid(dockingcontrol);

		if (perspective.equals("Modeling")){	
			
			dockinggrid.add(0, 0, 0.1, 0.5,  projectexplorerDock);
			dockinggrid.add(0, 0.5, 0.1, 0.5, propertiesDock);
			dockinggrid.add(1, 0, 0.82, 0.9, diagramDock);
			dockinggrid.add(0.7, 1, 0.9, 0.1, modelInfoDock);
			dockinggrid.add(2, 0, 0.08, 0.9, additionalmodelDock);
			dockingcontent.deploy(dockinggrid);
	     
		}
		else if (perspective.equals("Planning")){        
			dockinggrid.add(0, 0, 0.1, 0.5,  problemSelectionDock);
			dockinggrid.add(0, 0.5, 0.1, 0.5, planDock);
			dockinggrid.add(1, 0, 0.82, 0.9, plananalysisDock);
			dockinggrid.add(0.7, 1, 0.9, 0.1, consoleDock);
			dockinggrid.add(2, 0, 0.08, 0.9, plannavigationDock);
			dockingcontent.deploy(dockinggrid);
		}
		
	}
	
	
	
	
	
	


	/**
	 * This method initializes pddlSplitPane
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JSplitPane getTranslationSplitPane() {
		if (translationSplitPane == null) {
			translationSplitPane = new JSplitPane();
			translationSplitPane.setContinuousLayout(true);
			translationSplitPane.setOneTouchExpandable(true);
			translationSplitPane.setDividerSize(8);
			//translationSplitPane.setRightComponent(getPddlTextSplitPane());
            translationSplitPane.setRightComponent(getTranslatedModelPanel());
			translationSplitPane.setLeftComponent(getTranslationSelactionPanel());
		}
		return translationSplitPane;
	}

	/**
	 * This method initializes petriSplitPane
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
         * This method instantiate the analysisSlipPane
         * @return 
         */
        private JSplitPane getAnalysisSplitPane(){
		if (analysisSplitPane == null) {
			analysisSplitPane = new JSplitPane();
			analysisSplitPane.setContinuousLayout(true);
			analysisSplitPane.setOneTouchExpandable(true);
			analysisSplitPane.setDividerSize(8);
                        
                        analysisSplitPane.setLeftComponent(getProjectAnalysisSelectionPane());
                        analysisSplitPane.setRightComponent(getAnalysisMainContentPane());
                        
                        
		}
		return analysisSplitPane;            

        }
        
        
        
        
        
        /**
         * This method creates the panel for all analysis functionalities
         * @return 
         */        
        private JPanel getAnalysisPane(){
            
            if(analysisPane == null){
                analysisPane = new JPanel(new BorderLayout());
                
                //tabbed panel for distinct analysis context
                //TODO: in the future this is going to be just one panel.
                // a tabbed panel won't be necessary any more
                JTabbedPane analysisTabbedPane = new JTabbedPane();
				analysisTabbedPane.setTabPlacement(JTabbedPane.TOP);
				analysisTabbedPane.addTab("General", getAnalysisSplitPane());
                analysisTabbedPane.addTab("Petri Net", getPetriSplitPane());
                
                //status bar for the analysis processes
                analysisStatusBar = new JLabel("Status:");
                analysisStatusBar.setHorizontalAlignment(SwingConstants.RIGHT);
                JPanel bottomPlanSimPane = new JPanel(new BorderLayout());
                bottomPlanSimPane.add(analysisStatusBar, BorderLayout.CENTER);
                             
                
                //analysisPane.add(getAnalysisSplitPane(), BorderLayout.CENTER);
                analysisPane.add(analysisTabbedPane, BorderLayout.CENTER);
                analysisPane.add(bottomPlanSimPane, BorderLayout.SOUTH);
                
  
            }            
            
            return analysisPane;
        }

        
        
        
   private JPanel getStatusBar(){
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
       return bottomPlanSimPane;
	   
   }
        
	
	private JPanel getPlanNavigationPanel(){
		
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
		
		return planVisualizationMainPane;
	}
	
	
	

	/**
	 * This method initializes Problem Selection Panel
	 *
	 * @return javax.swing.JSplitPane
	 */
	private JPanel getProblemSelectionPanel(){
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

					//planAnalysisFramePanel.setTitle(":: Plan Analysis - Problem: "+ selectedNode.getUserObject());

                    solveProblemButton.setEnabled(true);
					setPlannerButton.setEnabled(true);
					addPlanActionButton.setEnabled(true);
					importPlanButton.setEnabled(true);
					planListModel.clear();
					xmlPlan = null;

                    //clean up reference of plans from database
                    cleanupPlanDatabaseReference();

                    String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();
                    Element problem = selectedNode.getData();
                    Element domainProject = problem.getDocument().getRootElement();
                    Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(domainProject, pddlVersion, null);
                    XMLUtilities.printXML(xpddlDomain);

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
		
		return mainTreePanel;
		
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

                                                String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();
                                                Element problem = selectedNode.getData();
                                                Element domainProject = problem.getDocument().getRootElement();
                                                Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(domainProject, pddlVersion, null);
                                                XMLUtilities.printXML(xpddlDomain);

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

			String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();

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
 			String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();
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
        
        
        /**
         * This method translates the given XML problem and put the text in the problemPddlTextPane
         * @param problemElement 
         */
        public void translateProblemToPddl(Element problemElement){            

            if (problemElement != null){
                
                appendModelTranslationOutputPanelText(">> Translating problem '"+problemElement.getChildText("name") +"'...\n\n");
                String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();                

                Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problemElement, pddlVersion);
                String problemText = XPDDLToPDDL.parseXPDDLToPDDL(xpddlProblem, "  ");
                //XMLUtilities.printXML(xpddlProblem);

                problemPddlTextPane.setText(problemText);

                //Check if the chosen problem requires additional PDDL requirement tags in the domain
                if (ToXPDDL.needRequirementModification(xpddlProblem, pddlVersion)){
                    Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(problemElement.getDocument().getRootElement(), pddlVersion, null);
                    ToXPDDL.adjustRequirements(xpddlDomain, xpddlProblem, pddlVersion);
                    String domainText = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "  ");
                    domainPddlTextPane.setText(domainText);
                    appendModelTranslationOutputPanelText("(!) INFO: extra requirements updated in the domain translation.'\n\n");
                }
                
                appendModelTranslationOutputPanelText(">> Problem '"+problemElement.getChildText("name") +"' translated!\n\n");
                
                
            }
 
        }
        
        
        /**
         * This method translate the XML domain to PDDL and put it the domainPddlTextPane
         * @param domainElement 
         */
        public void translateDomainToPddl(Element domainElement){            

            if (domainElement != null){
                
                appendModelTranslationOutputPanelText(">> Translating domain '"+domainElement.getChildText("name") +"'...\n\n");
                Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(
                                domainElement.getParentElement().getParentElement().getParentElement(),
                                languageButtonsGroup.getSelection().getActionCommand(), null);
                String domainText = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "  ");
                //XMLUtilities.printXML(xpddlDomain);
                domainPddlTextPane.setText(domainText);
                //XMLUtilities.printXML(xpddlDomain);
                
                appendModelTranslationOutputPanelText(">> Domain '"+domainElement.getChildText("name") +"' translated!\n\n");
            }
 
        }
        
        
        
        /**
         * This method translate the XML domain to PDDL and put it the domainPddlTextPane
         * @param domainElement 
         */
        public void translateDomainProblemToRmpl(Element problemElement){            

            if (problemElement != null){
                
                appendModelTranslationOutputPanelText(">> Translating problem '"+problemElement.getChildText("name") +"'...\n\n");
                Element xrmpl = ToXRMPL.XMLToXRMPLModel(problemElement.getParentElement().getParentElement().getParentElement().getParentElement().getParentElement(), problemElement);
                String rmpl = XRMPLtoRMPL.parseXRMPLToRMPL(xrmpl, "");
                //System.out.println(rmpl);
                rmplTextPane.setText(rmpl);
                
                appendModelTranslationOutputPanelText(">> Problem '"+problemElement.getChildText("name") +"' translated!\n\n");
            }
 
        }
       
        
        
        

        /**
         * This methods controls the call for the planners
         * @param selectedNode 
         */
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

           //System.out.println(type);
           //System.out.println(selectedNode.getLevel());
           switch(selectedNode.getLevel()){
                case 0: //Projects
                    skipPlannerProblemButton.setVisible(true);
                    this.solveAllProjects(selectedNode, planners);
                    break;
                case 1: //Project
                    skipPlannerProblemButton.setVisible(true);
                    //uml project
                    if (type.equals("project")){
                        this.solveAllDomainsFromProject(selectedNode.getData(), planners);
                    }
                    //pddl project
                    else if (type.equals("pddlproject")){
                        ItTreeNode project = (ItTreeNode)selectedNode;
                        //System.out.println(project.getInfoString());
                        File theitProjectFile = new File(project.getInfoString());
                        String path = project.getInfoString().replaceFirst(theitProjectFile.getName(), "");
                        //System.out.println(path); 
                        theitProjectFile = null;                        
                        this.solveAllProblemsFromPDDLProject(selectedNode.getData(), planners, path);
                        
                        
                    }
                    break;
                case 2: //Domain or pddlproblem                                        
                    //uml domain
                    if (type.equals("domain")){
                        skipPlannerProblemButton.setVisible(true);
                        this.solveAllProblemsFromDomain(selectedNode.getData(), planners);                        
                    }
                    //pddl problem
                    else if (type.equals("pddlproblem")){
                        ItTreeNode project = (ItTreeNode)selectedNode.getParent();
                        //System.out.println(project.getInfoString());
                        File theitProjectFile = new File(project.getInfoString());
                        String path = project.getInfoString().replaceFirst(theitProjectFile.getName(), "");
                        theitProjectFile = null;
                        //System.out.println(path); 
                        if(simpleCase){                            
                            skipPlannerProblemButton.setVisible(false);                            
                            this.solvePDDLProblemWithSinglePlanner(selectedNode.getData(), singlePlanner, path);
                        }
                        else{
                            skipPlannerProblemButton.setVisible(true);
                            this.solvePDDLProblem(selectedNode.getData(), planners, path);
                        }
                        
                    }
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
                    solveResult = solveProjectsWithPlannersList(projects, planners, selectedNode);
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
    
    public Element solveProjectsWithPlannersList(List<Element> projects, List<Element> planners, ItTreeNode mainnode){
        Element container = null;
        if(projects != null && projects.size() > 0){
            container = new Element("projects");

            //solve all problems in all domains
            //for (Iterator<Element> it = projects.iterator(); it.hasNext();) {
            //    Element project = it.next();
            for (int i = 0; i < projects.size(); i++) {
                Element project = projects.get(i);
                ItTreeNode projectNode = (ItTreeNode)mainnode.getChildAt(i);                          
                String type = project.getName();
                
                appendOutputPanelText(">> Starting planning with all projects. \n");
                Element result = null;
                //UML project
                if (type.equals("project")){
                    result = solveProjectProblemsWithPlannersList(project, planners);                    
                }
                //PDDL project
                else if (type.equals("pddlproject")){
                    File theitProjectFile = new File(projectNode.getInfoString());
                    String path = projectNode.getInfoString().replaceFirst(theitProjectFile.getName(), "");
                    //System.out.println(path); 
                    theitProjectFile = null; 
                    result = solvePDDLProjectProblemsWithPlannersList(project, planners, path);                    
                }
                
                if (result !=null){
                    container.addContent(result);
                }
                appendOutputPanelText(">> Planning with all projects done! \n");

            }
        }
        return container;
    }


    
    //UML
    
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

                String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();
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
                
                //record in the log file
                savetologfile("Starting: "+domain.getChildText("name").replaceAll(".pddl", "")+"; "+problem.getChildText("name").replaceAll(".pddl", "")+"; "+planner.getChildText("name"));

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
                    
                    //LOG: record it in the log file
                    savetologfile("Finish: "+domain.getChildText("name").replaceAll(".pddl", "")+"; "+problem.getChildText("name").replaceAll(".pddl", "")+"; "+planner.getChildText("name") +"\n");

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
                        
	                    //Log the resulting plan
	                    logplan(domain, problem, planner, result);
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
                String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();
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
                String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();
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
   

    public void solveProblemWithSinglePlanner(Element problem, Element chosenPlanner){
        if(problem != null){
            // clear plan list and plan info pane
            setPlanList(null);
            setPlanInfoPanelText("");
            setPlanEvaluationInfoPanelText("");
            cleanupPlanDatabaseReference();

            Element domainProject = problem.getDocument().getRootElement();
            Element domain = problem.getParentElement().getParentElement();

            String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();

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


    //PDDL
    
    private void solveAllProblemsFromPDDLProject(final Element project, final List<Element> planners, final String path){
        currentThread = new Thread(){
           public void run() {
               //solveResult = null;
               //solveResult = solveProjectProblemsWithPlannersList(project, planners);

               //preparing the same struture projects, project, domains, domain, problems, problem, plans, xmlPlan
               solveResult = new Element ("projects");
               Element projectRef = solvePDDLProjectProblemsWithPlannersList(project, planners, path);
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
    

    public Element solvePDDLProjectProblemsWithPlannersList(Element project, List<Element> planners, String path){
        Element container = null;
        if(project != null){
            appendOutputPanelText(">> Starting planning with project " + project.getChildText("name") + " with selected planner(s) \n");
            container = new Element("project");
            //container.setAttribute("id", project.getAttributeValue("id"));
            container.addContent((Element) project.getChild("name").clone());
            Element containerDomains = new Element("domains");
            container.addContent(containerDomains);
            //get all problems
            Element domain = null;
            try {
                XPath xpath = new JDOMXPath("pddldomains/pddldomain");
                domain = (Element)xpath.selectSingleNode(project);
            } catch (JaxenException e1) {
                e1.printStackTrace();
            }
            if (domain != null){
                String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();
                

                //solve all problems in the domain                                                                       
                Element domaincontainer = new Element("domain");
                domaincontainer.setAttribute("id", domain.getChildText("name"));
                domaincontainer.addContent((Element) domain.getChild("name").clone());
                Element containerProblems = new Element("problems");
                domaincontainer.addContent(containerProblems);
                //get all problems
                List<Element> problems = null;
                try {
                    XPath xpath = new JDOMXPath("problemInstances/pddlproblem");
                    problems = xpath.selectNodes(project);
                } catch (JaxenException e1) {
                    e1.printStackTrace();
                }
                if (problems != null){
                    
                   
                    //solve all problems
                    for (Iterator<Element> it = problems.iterator(); it.hasNext();) {
                        Element problem = it.next();
                        

                        if (stopRunningPlanners){
                            break;
                        }

                        Element result = solvePDDLProblemWithPlannersList(project, domain, problem, planners, path);
                        if (result !=null){
                            containerProblems.addContent(result);
                        }

                    }
                    //appendOutputPanelText(">> Done with domain " + domain.getChildText("name") + "! \n");
                }
                

                containerDomains.addContent(domaincontainer);
                
                appendOutputPanelText(">> Done with project " + project.getChildText("name") + "! \n");
            }

        }

        return container;
    }

    public void solvePDDLProblemWithSinglePlanner(Element problem, Element chosenPlanner, String path){
        if(problem != null){
            // clear plan list and plan info pane
            setPlanList(null);
            setPlanInfoPanelText("");
            setPlanEvaluationInfoPanelText("");
            cleanupPlanDatabaseReference();

            Element domainProject = problem.getDocument().getRootElement();
            Element domain = problem.getDocument().getRootElement().getChild("pddldomains").getChild("pddldomain");

            String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();                               
                        
            
            String problemPath = problem.getAttributeValue("file");
            //File problemFile = new File(problemPath);
            
            String domainPath = path + domain.getChildText("name");
            //File domainFile = new File(domainPath);
            

            // Save in auxiliary files
            String pddlDomain = FileInput.readFile(domainPath);
            String pddlProblem = FileInput.readFile(problemPath);

            File auxdomainFile = new File("resources/planners/domain.pddl");
            File auxproblemFile = new File("resources/planners/problem.pddl");
            try {
                FileWriter domainWriter = new FileWriter(auxdomainFile);
                domainWriter.write(pddlDomain);
                domainWriter.close();

                FileWriter problemWriter = new FileWriter(auxproblemFile);
                problemWriter.write(pddlProblem);
                problemWriter.close();
            } catch (IOException e1) {
                    e1.printStackTrace();
            }
            
                         
            // execute planner
            //exe = new ExecPlanner(chosenPlanner, domainFile.getPath(), problemFile.getPath(), false);
            exe = new ExecPlanner(chosenPlanner, domainPath, problemPath, false);

            exe.setXMLDomain(domain);
            exe.setXMLProblem(problem);
            exe.setDomainName(domain.getChildText("name"));
            exe.setProblemName(problem.getChildText("name"));            
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
    
    public Element solvePDDLProblemWithPlannersList(Element project, Element domain, Element problem, List<Element> planners, String path){
        Element container = null;

        if(project != null && problem != null){
            
            //System.out.println("got here: " + problem.getChildText("name"));
            
            container = new Element("problem");
            container.setAttribute("id", problem.getChildText("name"));
            container.addContent((Element) problem.getChild("name").clone());

            //add metrics (domain and problem level) to the problem reference (container)
            //Element mainMetrics = PlanSimulator.createMetricsNode(problem, domain);
            //container.addContent(mainMetrics);

            Element thePlans = new Element("plans");
            container.addContent(thePlans);
            
            String problemPath = problem.getAttributeValue("file");
            //File problemFile = new File(problemPath);
            
            String domainPath = path + domain.getChildText("name");
            //File domainFile = new File(domainPath);            
            
            // execute planner
            //exe = new ExecPlanner(null, domainFile.getPath(), problemFile.getPath(), true);
            exe = new ExecPlanner(null, domainPath, problemPath, true);

            exe.setXMLDomain(domain);
            exe.setXMLProblem(problem);
            exe.setProblemName(problem.getChildText("name"));
            exe.setDomainName(domain.getChildText("name"));
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
                
                //put in the log file - TODO: reset it when necessary
                
                savetologfile("Starting: "+domain.getChildText("name").replaceAll(".pddl", "")+"; "+problem.getChildText("name").replaceAll(".pddl", "")+"; "+planner.getChildText("name"));
                /*
                String logfolder= itSettings.getChild("logfolder").getAttributeValue("path");
                //logfolder= "/home/tiago/Dropbox/Experiments/";
                //String logfolder= "resources/log/";
                String logfilepath = logfolder +"logfile.txt";
                
                try {
                    PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logfilepath, true)));
                    out.println("Starting ("+dateTime+"): "+domain.getChildText("name").replaceAll(".pddl", "")+"; "+problem.getChildText("name").replaceAll(".pddl", "")+"; "+planner.getChildText("name"));
                    out.close();
                } catch (IOException e) {
                    //exception handling left as an exercise for the reader
                }
                */
                
                
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
                    
                    //LOG: put in the log file - TODO: reset it when necessary
                    savetologfile("Finish: "+domain.getChildText("name").replaceAll(".pddl", "")+"; "+problem.getChildText("name").replaceAll(".pddl", "")+"; "+planner.getChildText("name") +"\n");
                    /*
                    Date datefinish = new Date();
                    String dateTimefinish = dateFormat.format(datefinish);
                    try {
                        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logfilepath, true)));
                        out.println("Finish ("+dateTimefinish+"): "+domain.getChildText("name").replaceAll(".pddl", "")+"; "+problem.getChildText("name").replaceAll(".pddl", "")+"; "+planner.getChildText("name") +"\n");
                        out.close();
                    } catch (IOException e) {
                        //exception handling left as an exercise for the reader
                    }
                    */

                    //get resulting plan (might have plan)
                    result = exe.getPlan();
                    //End of new approach if timeout and theads

                    if (result != null){
                        //Calculate metrics dataset. Add metrics data to the plan "xmlPlan/metrics"
                        //Element metrics = PlanSimulator.createMetricsNode(problem, domain);
                        //if (metrics != null && metrics.getChildren().size() > 0 && result.getChild("plan").getChildren().size() > 0) {
                        //    appendOutputPanelText(">> Calculating metrics for the plan given by " + planner.getChildText("name") + ". \n");
                        //    PlanSimulator.createMetricDatasets(metrics, result, problem, domain, null);
                        //}
                        //result.addContent(metrics);

                        thePlans.addContent((Element)result.clone());
                                                
                        
                        //Log the resulting plan
                        logplan(domain, problem, planner, result);
               
                        /*
                        //LOG: save file (xml) as a backup
                        String currenttime = new Date().toString(); 
                        String backuppath = logfolder+"plans/"+domain.getChildText("name").replaceAll(".pddl", "")+"_"+problem.getChildText("name").replaceAll(".pddl", "")+"_"+planner.getChildText("name")+"_"+dateTimefinish+".xml";
                        //XMLUtilities.writeToFile(backuppath, result.getDocument());
                        try {
                            FileWriter file = new FileWriter(backuppath);
                            file.write(XMLUtilities.toString(result));
                            file.close();
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        */
                        
                        
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

    private void solvePDDLProblem(final Element problem, final List<Element> planners, final String path){
       currentThread = new Thread(){
           public void run() {
               Element project = problem.getDocument().getRootElement();
               Element domain = problem.getDocument().getRootElement().getChild("pddldomains").getChild("pddldomain");
               //String pddlDomain = "";
               //String pddlProblem = "";
               //solveResult = null;
               //solveResult = solveProblemWithPlannersList(project, domain, problem, pddlDomain, pddlProblem, planners);

               //preparing the same struture projects, project, domains, domain, problems, problem, plans, xmlPlan
               solveResult = new Element ("projects");
               Element projectRef = new Element("project");
               projectRef.addContent((Element)project.getChild("name").clone());
               Element domainsRef = new Element("domains");
               projectRef.addContent(domainsRef);
               Element domainRef = new Element("domain");
               domainRef.setAttribute("id", domain.getChildText("name"));
               domainRef.addContent((Element)domain.getChild("name").clone());
               domainsRef.addContent(domainRef);
               Element problemsRef = new Element("problems");
               domainRef.addContent(problemsRef);

               solveResult.addContent(projectRef);

               Element problemRef = solvePDDLProblemWithPlannersList(project, domain, problem, planners, path);

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

    
    /**
     * Instantiate PlanList Panel
     * @return
     */
    private JPanel getPlanListPanel(){
    	
    	JPanel plan_panel = new JPanel(new BorderLayout());
    	
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

		plan_panel.add(listPanel, BorderLayout.CENTER);
		
		return plan_panel;
    	
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
                        //planListFramePanel.repaint();
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



	
	private JPanel getPlanAnalysisPanel(){
		
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
                                            /*
                                            try {
                                                FileWriter planfile = new FileWriter(planPath);
                                                planfile.write(XMLUtilities.toString(eaplan));
                                                planfile.close();
                                                System.out.println("File '" + planPath + "' created.");
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                            *
                                            */
                                            if (eaplan.getChild("plan").getChildren().size() > 0){
                                                
                                            //TODO: save the plan in PDDL too. It should be done through the XPDDL/PDDL classes
                                            String pddlplan = ToXPDDL.XMLtoXPDDLPlan(eaplan);
                                            String planFileNamePDDL = "solution" + theplanner.getChildText("name") + "-" + theplanner.getChildText("version") + "-" + Integer.toString(plans.getChildren().indexOf(eaplan)) + ".pddl";
                                            String planPathPDDL = folderPath + File.separator + planFileNamePDDL;
                                            
                                            //String cfolderPath = selectedFile.getAbsolutePath().replace(selectedFile.getName(), "");
                                            //String planFileNamePDDL = theplanner.getChildText("name")+"-"+theplanner.getChildText("version") + "-" + folderName+"-solution.pddl";
                                            //String planPathPDDL = cfolderPath + File.separator + planFileNamePDDL;
                                            //if (!theplanner.getChildText("name").contains("MIPS")){
                                            try {
                                                FileWriter planfile = new FileWriter(planPathPDDL);
                                                planfile.write(pddlplan);
                                                planfile.close();
                                                System.out.println("File '" + planPathPDDL + "' created.");
                                            } catch (IOException e1) {
                                                e1.printStackTrace();
                                            }
                                            }//}
                                            
                                            

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
        planAnalysisTabbedPane.addTab("Plan Database", getPlanDatabasePanel());
        planAnalysisTabbedPane.addTab("Rationale Database", getRationaleDatabasePanel());

        JPanel planAnalysisPanel = new JPanel(new BorderLayout());
        //planAnalysisPanel.add(chartsToolBar, BorderLayout.NORTH);
        planAnalysisPanel.add(planAnalysisTabbedPane, BorderLayout.CENTER);
        return planAnalysisPanel;
		
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
	 * This method initializes planDatabasePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getPlanDatabasePanel(){
                //TODO:

		if(planDatabasePanel == null){
			planDatabasePanel = new JPanel(new BorderLayout());
			//planDatabasePanel.add(getPlanDatabaseToolBar(), BorderLayout.NORTH);

                       //top panel (toolbar + filter filed
                        JPanel topPanel = new JPanel(new BorderLayout());
                        topPanel.add(getPlanDatabaseToolBar(), BorderLayout.NORTH);

                        planFilterPanel = new JPanel(new BorderLayout());
                        planfilterTextPane = new JTextPane();
                        planfilterTextPane.setBackground(Color.WHITE);
                        //planfilterTextPane.setPreferredSize(new Dimension(250,100));
                        planfilterTextPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
                        planfilterTextPane.setToolTipText("<html>Please use only the following column names:<br>"
                                + "id<br>project<br>domain<br>plannername<br>plannerversion<br>nactions<br>quality</html>");
                        JScrollPane scrollText = new JScrollPane();
                        scrollText.setViewportView(planfilterTextPane);
                        JLabel descriptionLabel = new JLabel("<html>Filter: </html>");
                        planFilterPanel.add(descriptionLabel, BorderLayout.NORTH);
                        planFilterPanel.add(scrollText, BorderLayout.CENTER);
                        planFilterPanel.setPreferredSize(new Dimension(250,90));
                        planFilterPanel.setVisible(false);
                        topPanel.add(planFilterPanel, BorderLayout.CENTER);


                        planDatabasePanel.add(topPanel, BorderLayout.NORTH);


                        resultPlanTableModel = new DefaultTableModel();
                        resultPlanTable = new JTable(resultPlanTableModel){
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                //avoid columns that can not edit (leaving only the evaluation to edit)
                                return false;
                                //if(column < 4){
                                //    return false;
                                //}else{
                                //    return true;
                                //}
                            }
                        };

                        //resultPlanTableModel.addTableModelListener(this);

                        //allows a single row selection
                        resultPlanTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


                        resultPlanTableModel.addColumn("id");
                        //resultPlanTableModel.addColumn("#");
                        resultPlanTableModel.addColumn("project");
                        resultPlanTableModel.addColumn("domain");
                        resultPlanTableModel.addColumn("problem");
                        //resultPlanTableModel.addColumn("planner");
                        resultPlanTableModel.addColumn("plannername");
                        resultPlanTableModel.addColumn("nactions");
                        //resultPlanTableModel.addColumn("# actions");
                        resultPlanTableModel.addColumn("cost");
                        resultPlanTableModel.addColumn("quality");

                        //resultPlanTable.getColumnModel().getColumn(0).setMinWidth(25);
                        //resultPlanTable.getColumnModel().getColumn(0).setMaxWidth(50);
                        //resultPlanTable.getColumnModel().getColumn(1).setMinWidth(30);
                        //resultPlanTable.getColumnModel().getColumn(1).setMaxWidth(40);
                        //resultPlanTable.getColumnModel().getColumn(2).setMinWidth(150);
                        //resultPlanTable.getColumnModel().getColumn(2).setMaxWidth(300);
                        //resultPlanTable.getColumnModel().getColumn(3).setMinWidth(150);
                        //resultPlanTable.getColumnModel().getColumn(3).setMaxWidth(300);
                        //resultPlanTable.getColumnModel().getColumn(4).setMinWidth(150);
                        //resultPlanTable.getColumnModel().getColumn(4).setMaxWidth(300);
                        //resultPlanTable.getColumnModel().getColumn(6).setMinWidth(55);
                        //resultPlanTable.getColumnModel().getColumn(6).setMaxWidth(75);
                        //resultPlanTable.getColumnModel().getColumn(7).setMinWidth(50);
                        //resultPlanTable.getColumnModel().getColumn(7).setMaxWidth(70);


                        resultPlanTable.getColumnModel().getColumn(0).setMinWidth(25);
                        resultPlanTable.getColumnModel().getColumn(0).setMaxWidth(50);
                        resultPlanTable.getColumnModel().getColumn(1).setMinWidth(150);
                        resultPlanTable.getColumnModel().getColumn(1).setMaxWidth(300);
                        resultPlanTable.getColumnModel().getColumn(2).setMinWidth(150);
                        resultPlanTable.getColumnModel().getColumn(2).setMaxWidth(300);
                        resultPlanTable.getColumnModel().getColumn(3).setMinWidth(150);
                        resultPlanTable.getColumnModel().getColumn(3).setMaxWidth(300);
                        resultPlanTable.getColumnModel().getColumn(5).setMinWidth(55);
                        resultPlanTable.getColumnModel().getColumn(5).setMaxWidth(75);
                        resultPlanTable.getColumnModel().getColumn(6).setMinWidth(50);
                        resultPlanTable.getColumnModel().getColumn(6).setMaxWidth(70);
                        resultPlanTable.getColumnModel().getColumn(7).setMinWidth(50);
                        resultPlanTable.getColumnModel().getColumn(7).setMaxWidth(70);
                        

                        //do not show the id (for now)
                        //resultPlanTable.removeColumn(resultPlanTable.getColumnModel().getColumn(0));


                        JScrollPane scrollPlanResultPane = new JScrollPane(resultPlanTable);


                        planDatabasePanel.add(scrollPlanResultPane, BorderLayout.CENTER);
                        //planDatabasePanel.add(new JPanel(), BorderLayout.CENTER);
		}

		return planDatabasePanel;
	}



	/**
	 * This method initializes rationaleDatabasePanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getRationaleDatabasePanel(){
                //TODO:

		if(rationaleDatabasePanel == null){
			rationaleDatabasePanel = new JPanel(new BorderLayout());
			rationaleDatabasePanel.add(getRationaleDatabaseToolBar(), BorderLayout.NORTH);


                        resultRationaleTableModel = new DefaultTableModel();
                        resultRationaleTable = new JTable(resultRationaleTableModel){
                            @Override
                            public boolean isCellEditable(int row, int column) {
                                //avoid columns that can not edit (leaving only the evaluation to edit)
                                return false;
                                //if(column < 4){
                                //    return false;
                                //}else{
                                //    return true;
                                //}
                            }
                        };

                        //resultRationaleTableModel.addTableModelListener(this);

                        //allows a single row selection
                        resultRationaleTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);


                        resultRationaleTableModel.addColumn("id");
                        //resultRationaleTableModel.addColumn("#");
                        resultRationaleTableModel.addColumn("name");
                        resultRationaleTableModel.addColumn("range");
                        resultRationaleTableModel.addColumn("project");
                        resultRationaleTableModel.addColumn("domain");
                        resultRationaleTableModel.addColumn("problem");
                        resultRationaleTableModel.addColumn("planid");
                        resultRationaleTableModel.addColumn("impact");
                        //resultRationaleTableModel.addColumn("quality");

                        /*
                         resultRationaleTable.getColumnModel().getColumn(0).setMinWidth(25);
                        resultRationaleTable.getColumnModel().getColumn(0).setMaxWidth(50);
                        resultRationaleTable.getColumnModel().getColumn(1).setMaxWidth(25);
                        //resultRationaleTable.getColumnModel().getColumn(2).setMinWidth(200);
                        //resultRationaleTable.getColumnModel().getColumn(2).setMaxWidth(500);
                        resultRationaleTable.getColumnModel().getColumn(3).setMinWidth(100);
                        resultRationaleTable.getColumnModel().getColumn(3).setMaxWidth(400);
                        resultRationaleTable.getColumnModel().getColumn(4).setMinWidth(50);
                        resultRationaleTable.getColumnModel().getColumn(4).setMaxWidth(300);
                        resultRationaleTable.getColumnModel().getColumn(5).setMinWidth(50);
                        resultRationaleTable.getColumnModel().getColumn(5).setMaxWidth(300);
                        resultRationaleTable.getColumnModel().getColumn(6).setMinWidth(50);
                        resultRationaleTable.getColumnModel().getColumn(6).setMaxWidth(300);
                        resultRationaleTable.getColumnModel().getColumn(7).setMinWidth(40);
                        resultRationaleTable.getColumnModel().getColumn(7).setMaxWidth(55);
                         */

                        resultRationaleTable.getColumnModel().getColumn(0).setMinWidth(25);
                        resultRationaleTable.getColumnModel().getColumn(0).setMaxWidth(50);
                        //resultRationaleTable.getColumnModel().getColumn(1).setMinWidth(200);
                        //resultRationaleTable.getColumnModel().getColumn(1).setMaxWidth(500);
                        resultRationaleTable.getColumnModel().getColumn(2).setMinWidth(100);
                        resultRationaleTable.getColumnModel().getColumn(2).setMaxWidth(400);
                        resultRationaleTable.getColumnModel().getColumn(3).setMinWidth(50);
                        resultRationaleTable.getColumnModel().getColumn(3).setMaxWidth(300);
                        resultRationaleTable.getColumnModel().getColumn(4).setMinWidth(50);
                        resultRationaleTable.getColumnModel().getColumn(4).setMaxWidth(300);
                        resultRationaleTable.getColumnModel().getColumn(5).setMinWidth(50);
                        resultRationaleTable.getColumnModel().getColumn(5).setMaxWidth(300);
                        resultRationaleTable.getColumnModel().getColumn(6).setMinWidth(40);
                        resultRationaleTable.getColumnModel().getColumn(6).setMaxWidth(55);
                        resultRationaleTable.getColumnModel().getColumn(7).setMinWidth(60);
                        resultRationaleTable.getColumnModel().getColumn(7).setMaxWidth(70);


                        //do not show the id (for now)
                        //resultRationaleTable.removeColumn(resultPlanTable.getColumnModel().getColumn(0));


                        JScrollPane scrollPlanResultPane = new JScrollPane(resultRationaleTable);


                        rationaleDatabasePanel.add(scrollPlanResultPane, BorderLayout.CENTER);
                        //rationaleDatabasePanel.add(new JPanel(), BorderLayout.CENTER);
		}

		return rationaleDatabasePanel;
	}



	
	
	private JPanel getPlanInfoPanel(){
		
		JPanel planInfoPanel = new JPanel(new BorderLayout());
		planInfoPanel.setMinimumSize(new Dimension(100, 40));
		

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

		
		planInfoPanel.add(outputPane, BorderLayout.CENTER);
		
		return planInfoPanel;
		
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
            
            
            //LOG EVERY TEXT that is printed in the console
            /// set start datetime
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
            Date date = new Date();
            String dateTime = dateFormat.format(date);
            
            //put in the log file - TODO: reset it when necessary
            String logfolder= itSettings.getChild("logfolder").getAttributeValue("path");
            //logfolder= "/home/tiago/Dropbox/Experiments/";
            //String logfolder= "resources/log/";
            String logfilepath = logfolder +"consolelog.txt";
            
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logfilepath, true)));
                out.print("["+dateTime+"]:  "+text);
                out.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
                        

	}
        
        
        /**
         * Set text to the pddl translation output text area
         * @param text 
         */
        public void setPDDLTranslationOutputPanelText(String text){
                    outputPddlTranslationEditorPane.setText(text);
            }

        /*
         * Append text to the pddl translation output text area
         */
        public void appendModelTranslationOutputPanelText(String text){
            try {
                outputPddlTranslationEditorPane.append(text);
                outputPddlTranslationEditorPane.setCaretPosition(outputPddlTranslationEditorPane.getDocument().getLength());
            } catch (Exception e) {
            }
	}
        
        
        /**
         * Set text to the analysis output text area
         * @param text 
         */
        public void setAnalysisOutputPanelText(String text){
                    outputAnalysisEditorPane.setText(text);
            }

        /*
         * Append text to the analysis output text area
         */
        public void appendAnalysisOutputPanelText(String text){
            try {
                outputAnalysisEditorPane.append(text);
                outputAnalysisEditorPane.setCaretPosition(outputAnalysisEditorPane.getDocument().getLength());
            } catch (Exception e) {
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
        
        
        
        private JPanel getTranslatedModelPanel(){
            JPanel anPanel = new JPanel(new BorderLayout());
            
            translationModelTabbedPane = new JTabbedPane();
             
            // PDDL tab
            if (pddlTextSplitPane == null) {
				pddlTextSplitPane = new JSplitPane();
				pddlTextSplitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
	
				//Problem Panel
				ItFramePanel problemPanel = new ItFramePanel(":: Problem", ItFramePanel.MINIMIZE_MAXIMIZE);
				problemPanel.setContent(getBottomPddlPanel(), false);
				problemPanel.setParentSplitPane(pddlTextSplitPane);
				pddlTextSplitPane.setBottomComponent(problemPanel);
	
				//Domain Panel
				ItFramePanel domainPanel = new ItFramePanel(":: Domain", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
				domainPanel.setContent(getTopPddlPanel(), false);
				//domainPanel.setParentSplitPane(pddlTextSplitPane);
				pddlTextSplitPane.setTopComponent(domainPanel);
	
				pddlTextSplitPane.setDividerSize(3);
				pddlTextSplitPane.setContinuousLayout(true);
				pddlTextSplitPane.setDividerLocation((int)(screenSize.height*0.40));
				pddlTextSplitPane.setResizeWeight(0.5);
            }                          
            //anPanel.add(pddlTextSplitPane, BorderLayout.CENTER);
            //translationModelTabbedPane.addTab("PDDL", pddlTextSplitPane);
            
            // RMPL tab
            if (rmplPanel == null){
	            rmplPanel = new ItFramePanel(":: RMPL Model", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
	            rmplPanel.setContent(getRmplViewPanel(), false);
            }
            //translationModelTabbedPane.addTab("RMPL", rmplPanel);
            
            // Dummy panel
            translationModelTabbedPane.addTab("Result",new ItFramePanel(":: Model", ItFramePanel.NO_MINIMIZE_MAXIMIZE));
            
            anPanel.add(translationModelTabbedPane, BorderLayout.CENTER);
            

                                  
                     
             //BOTTOM
             //Console output
            ItFramePanel outputPanel = new ItFramePanel(":: Output console", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
            outputPanel.setPreferredSize(new Dimension(screenSize.width/4 - 20, 120));
            //Results output
            //Content of the FramePanel            
            JPanel resultsPanel = new JPanel(new BorderLayout());
            outputPddlTranslationEditorPane = new JTextArea();
            //analysisInfoEditorPane.setContentType("text/html");
            outputPddlTranslationEditorPane.setEditable(false);
            outputPddlTranslationEditorPane.setLineWrap(true);
            outputPddlTranslationEditorPane.setWrapStyleWord(true);
            outputPddlTranslationEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            outputPddlTranslationEditorPane.setBackground(Color.WHITE);              
            resultsPanel.add(new JScrollPane(outputPddlTranslationEditorPane), BorderLayout.CENTER);
            
            outputPanel.setContent(resultsPanel, false);
   
            anPanel.add(outputPanel, BorderLayout.SOUTH);
            
           
            
            return anPanel;

        }
	
        
        
        /**
         * Creates the project selection panel in the main analysis panel
         * @return 
         */
        private JPanel getProjectPDDLSelectionPane() {
            
            JPanel anPanel = new JPanel(new BorderLayout());
            
            //TOP panel Domain/problem selection
            ItFramePanel projectSelPanel = new ItFramePanel(":: Domain/Problem Selection", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
            projectSelPanel.setPreferredSize(new Dimension(screenSize.width/4 - 20, screenSize.height));
            
            //Instanciate project selection tree
            ItTreeNode root = new ItTreeNode("Projects");
            root.setIcon(new ImageIcon("resources/images/projects.png"));
            pddlTranslationTreeModel = new DefaultTreeModel(root);
            modelTranslationTree = new JTree(pddlTranslationTreeModel);
            modelTranslationTree.setShowsRootHandles(true);
            modelTranslationTree.setCellRenderer(new ItTreeCellRenderer());
            modelTranslationTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            modelTranslationTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
	            public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
		            ItTreeNode selectedNode = (ItTreeNode)modelTranslationTree.getLastSelectedPathComponent();
		            //if(selectedNode != null && selectedNode.getLevel() == 3){
		
		            //}
		            //else{
		
		            //}
	            }
            });

            // create a main pane for putting the tree inside
            JPanel mainTreePanel = new JPanel(new BorderLayout());
            mainTreePanel.add(new JScrollPane(modelTranslationTree), BorderLayout.CENTER);
            
            //Translate button
            translateDomainProblemButton = new JButton("Translate", new ImageIcon("resources/images/play.png"));
            //solveProblemButton.setEnabled(false);
            translateDomainProblemButton.setActionCommand("translate");
            translateDomainProblemButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e) {  
                    ItTreeNode selectedNode = (ItTreeNode)modelTranslationTree.getLastSelectedPathComponent();
                    if(selectedNode != null){
                    	
                    	//Check what language has been selected
                    	String language = languageButtonsGroup.getSelection().getActionCommand();
                    	
                    	//PDDL language selected
                    	if (language.equals(ToXPDDL.PDDL_2_1) || language.equals(ToXPDDL.PDDL_2_2) || 
                    			language.equals(ToXPDDL.PDDL_3_0) || language.equals(ToXPDDL.PDDL_3_1)){
                    		if (selectedNode.getLevel() == 2 && selectedNode.getData().getName().equals("domain")){
                                Element domain = selectedNode.getData();
                                translateDomainToPddl(domain);  
                            }
                            else if (selectedNode.getLevel() == 3 && selectedNode.getData().getName().equals("problem")){
                                Element problem = selectedNode.getData();
                                translateProblemToPddl(problem);
                            }
                            else{
                                JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>Please chose a domain or problem node (from a UML project)<br> in the Domain/Problem Selection tree.</html>");
                            }
                    		// show PDDL tab
                    		translationModelTabbedPane.removeAll();
                    		translationModelTabbedPane.addTab("PDDL", pddlTextSplitPane);
                    		
                    	}
                    	//RMPL language selected
                    	else if (language.equals(ToXRMPL.RMPL_1_0)){
                    		if (selectedNode.getLevel() == 3 && selectedNode.getData().getName().equals("problem")){
                    			Element problem = selectedNode.getData();
                    			translateDomainProblemToRmpl(problem);
                    		}
                    		else{
                                JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>Please chose a problem node (from a UML project)<br> in the Domain/Problem Selection tree.</html>");
                                rmplTextPane.setText("");
                            }
                    		// show RMPL tab
                    		translationModelTabbedPane.removeAll();
                    		translationModelTabbedPane.addTab("RMPL", rmplPanel);   		
                    	}
                    }                        
                }

            });
            mainTreePanel.add(translateDomainProblemButton, BorderLayout.SOUTH);
            
            
            projectSelPanel.setContent(mainTreePanel, false);            
            //projectSelPanel.setParentSplitPane(petriEditorSplitPane);
                                   
            
            anPanel.add(projectSelPanel, BorderLayout.CENTER);
            
            
            
            //BOTTOM panels (Language options)
            ItFramePanel pddlSettingFramePanel = new ItFramePanel(":: Language Selection", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
            //pddlSettingPanel.setPreferredSize(new Dimension(screenSize.width/4 - 20, screenSize.height));
            
            JPanel bottonPanel = new JPanel(new BorderLayout());
            bottonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
            bottonPanel.add(getLanguageVersionSettingsPanel(), BorderLayout.CENTER);
           
            pddlSettingFramePanel.setContent(bottonPanel, false); 
            anPanel.add(pddlSettingFramePanel, BorderLayout.SOUTH);
            
            
            return anPanel;                        
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
         * Creates the project selection panel in the main analysis panel
         * @return 
         */
        private JPanel getProjectAnalysisSelectionPane() {
            
            JPanel anPanel = new JPanel(new BorderLayout());
            
            ItFramePanel projectSelPanel = new ItFramePanel(":: Project Selection", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
            projectSelPanel.setPreferredSize(new Dimension(screenSize.width/4 - 20, screenSize.height));
            
            //Instanciate project selectio tree
            ItTreeNode root = new ItTreeNode("Projects");
            root.setIcon(new ImageIcon("resources/images/projects.png"));
            projectAnalysisTreeModel = new DefaultTreeModel(root);
            projectAnalysisTree = new JTree(projectAnalysisTreeModel);
            projectAnalysisTree.setShowsRootHandles(true);
            projectAnalysisTree.setCellRenderer(new ItTreeCellRenderer());
            projectAnalysisTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
            projectAnalysisTree.addTreeSelectionListener(new javax.swing.event.TreeSelectionListener() {
                    public void valueChanged(javax.swing.event.TreeSelectionEvent e) {
                            ItTreeNode selectedNode = (ItTreeNode)projectAnalysisTree.getLastSelectedPathComponent();
                            //if(selectedNode != null && selectedNode.getLevel() == 3){

                            //}
                            //else{

                            //}
                    }
            });

            // create a main pane for putting the tree inside
            JPanel mainTreePanel = new JPanel(new BorderLayout());
            mainTreePanel.add(new JScrollPane(projectAnalysisTree), BorderLayout.CENTER);
            
            projectSelPanel.setContent(mainTreePanel, false);            
            //projectSelPanel.setParentSplitPane(petriEditorSplitPane);
            
            anPanel.add(projectSelPanel, BorderLayout.CENTER);
            
            return anPanel;                        
        }
        
        /**
         * Creates the main content panel in the main analysis panel
         * @return 
         */
        private JPanel getAnalysisMainContentPane() {
            
            JPanel anPanel = new JPanel(new BorderLayout());
	    
            ItFramePanel mainContentPanel = new ItFramePanel(":: Analysis Techniques and Results", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
            
            //Content of the FramePanel            
            JPanel resultsPanel = new JPanel(new BorderLayout());

            JToolBar analysisToolSetBar = new JToolBar();
            analysisToolSetBar.setRollover(true);

            JButton TorchlightButton = new JButton("TorchLight", new ImageIcon("resources/images/compare.png"));
            TorchlightButton.setToolTipText("<html>Run TorchLight system. <br>TorchLight analyzes h+ search space topology without actually running any search</html>");
            TorchlightButton.addActionListener(new java.awt.event.ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {

                        new Thread(){
                            public void run() {
                                
                               ItTreeNode selectedNode = (ItTreeNode)projectAnalysisTree.getLastSelectedPathComponent();
                               
                               if (selectedNode.getData() != null && selectedNode.getData().getName().indexOf("problem") != -1){
                                   appendAnalysisOutputPanelText("(!) Know more about TorchLight at http://www.loria.fr/~hoffmanj/ \n");
                                   appendAnalysisOutputPanelText(">> Calling TorchLight System... \n");
                                   analysisStatusBar.setText("Status: Running Tourchlight ...");

                                   String pddlVersion = languageButtonsGroup.getSelection().getActionCommand();                                
                                   
                                   //Call TorchLight
                                   TorchLightAnalyzer.getTorchLightAnalysis(selectedNode,pddlVersion);

                                   appendAnalysisOutputPanelText(">> TorchLight analysis done!'\n");
                                   appendAnalysisOutputPanelText(" \n");
                                   analysisStatusBar.setText("Status: Tourchlight analysis done!");                                   
                               }
                               else{
                                   JOptionPane.showMessageDialog(ItSIMPLE.this,"<html>Please chose a problem node at the 'Project Selection' tree. </html>");
                               }
                              
                               
                               

                            }
                        }.start();

                }
            });
            analysisToolSetBar.add(TorchlightButton);


            //Results output
            outputAnalysisEditorPane = new JTextArea();
            //analysisInfoEditorPane.setContentType("text/html");
            outputAnalysisEditorPane.setEditable(false);
            outputAnalysisEditorPane.setLineWrap(true);
            outputAnalysisEditorPane.setWrapStyleWord(true);
            outputAnalysisEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            outputAnalysisEditorPane.setBackground(Color.WHITE);  
                        
            //analysisInfoEditorPane = new JEditorPane();
            //analysisInfoEditorPane.setContentType("text/html");
            //analysisInfoEditorPane.setEditable(false);
            //analysisInfoEditorPane.setCursor(new Cursor(Cursor.TEXT_CURSOR));
            //analysisInfoEditorPane.setBackground(Color.WHITE);                    
                    

            resultsPanel.add(analysisToolSetBar, BorderLayout.NORTH);
            resultsPanel.add(new JScrollPane(outputAnalysisEditorPane), BorderLayout.CENTER);         
            
            
            mainContentPanel.setContent(resultsPanel, false);
            //mainContentPanel.setParentSplitPane(petriEditorSplitPane);           
            anPanel.add(mainContentPanel, BorderLayout.CENTER);
            
            return anPanel;            
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
	 * This method initializes bottomPddlPanel
	 *
	 * @return javax.swing.JPanel
	 */
	private JPanel getRmplViewPanel() {
		JPanel mainPanel = new JPanel(new BorderLayout());
		
		JScrollPane rmplScrollPane = new JScrollPane();
		ItHilightedDocument rmplDocument = new ItHilightedDocument();
		rmplDocument.setHighlightStyle(ItHilightedDocument.PDDL_STYLE);
		rmplTextPane = new JTextPane(rmplDocument);
		rmplTextPane.setFont(new Font("Courier", 0, 12));
		rmplTextPane.setBackground(Color.WHITE);
        rmplScrollPane.setViewportView(rmplTextPane);
		mainPanel.add(rmplScrollPane, BorderLayout.CENTER);


		JToolBar rmplToolBar = new JToolBar();
		rmplToolBar.setRollover(true);
		JButton save = new JButton(saveRMPLModelToFile);
		rmplToolBar.add(save);
		JButton viewWebRMPLEditor = new JButton(openRMPLWebEditor);
		viewWebRMPLEditor.setToolTipText("<html>This will send the model to the RMPL editor<br> at http://bicycle.csail.mit.edu/rmpleditor/. <br>" +
				"You need to have this link open with your preferable browser (e.g., Chrome, Firefox).</html>");
		rmplToolBar.add(viewWebRMPLEditor);
		
		mainPanel.add(rmplToolBar, BorderLayout.NORTH);
		return mainPanel;
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
	public JTextPane getDomainPddlTextPane() {
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
			mainPanel.add(getStatusBar(), BorderLayout.SOUTH);
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
			toolBar.add(newProjectAction).setToolTipText("New UML Project");
			toolBar.add(openProjectAction).setToolTipText("Open Project");
			toolBar.add(saveAction).setToolTipText("Save Project");
			toolBar.add(saveAllAction).setToolTipText("Save All");
                        toolBar.addSeparator();
                        
                        
                        perspectiveGroup = new ButtonGroup();
	
                        
                        //toolBar.add(modelingAction).setToolTipText("Modeling domains");
                        modelingPerspectiveButton = new JToggleButton(modelingAction);
                        modelingPerspectiveButton.setToolTipText("Modeling domains and problems");
                        perspectiveGroup.add(modelingPerspectiveButton);                        
                        toolBar.add(modelingPerspectiveButton);
                        //set modeling perpective selected by dafault
                        perspectiveGroup.setSelected(modelingPerspectiveButton.getModel(), true);
                        

                        //toolBar.add(analysisAction).setToolTipText("Model analysis with different techniques and formalism");
                        analysisPerspectiveButton = new JToggleButton(analysisAction);
                        analysisPerspectiveButton.setToolTipText("Model analysis using different techniques and formalism");
                        perspectiveGroup.add(analysisPerspectiveButton);                        
                        toolBar.add(analysisPerspectiveButton);
                        
                        
                        //toolBar.add(planningAction).setToolTipText("Planning process and experiments with planners and solvers");
                        planningPerspectiveButton = new JToggleButton(planningAction);
                        planningPerspectiveButton.setToolTipText("Planning process and experiments using planners and solvers");
                        perspectiveGroup.add(planningPerspectiveButton);                                                
                        toolBar.add(planningPerspectiveButton);       
                        
                        
                        toolBar.addSeparator();
                        //toolBar.add(pddlTranslationPerspectiveAction).setToolTipText("UML to PDDL translation perspective");
                        modelTranslationPerspectiveButton = new JToggleButton(pddlTranslationPerspectiveAction);
                        modelTranslationPerspectiveButton.setToolTipText("UML to other model languages (e.g. PDDL)");
                        perspectiveGroup.add(modelTranslationPerspectiveButton);                                                
                        toolBar.add(modelTranslationPerspectiveButton);                        
                        
                        
                       //toolBar.addSeparator();
                        //toolBar.add(umlPerspectiveAction).setToolTipText("Set UML perspective");
                        //toolBar.add(pddlPerspectiveAction).setToolTipText("Set PDDL perspective");
                        

                        
		}
		return toolBar;
	}

	
        
        
        /**
         * This method creates the panel containing the pddl version selection component
         * @return 
         */
        private JPanel getLanguageVersionSettingsPanel(){
            
            JPanel settingsPanel = new JPanel();

            //PDDL
            JRadioButton pddl21 = new JRadioButton("PDDL 2.1");
            JRadioButton pddl22 = new JRadioButton("PDDL 2.2");
            JRadioButton pddl30 = new JRadioButton("PDDL 3.0", true);
            JRadioButton pddl31 = new JRadioButton("PDDL 3.1");
            //RMPL
            JRadioButton rmpl10 = new JRadioButton("RMPL");
            
            //PDDL
            pddl21.setOpaque(false);
            pddl21.setActionCommand(ToXPDDL.PDDL_2_1);
            pddl22.setOpaque(false);
            pddl22.setActionCommand(ToXPDDL.PDDL_2_2);
            pddl30.setOpaque(false);
            pddl30.setActionCommand(ToXPDDL.PDDL_3_0);
            pddl31.setOpaque(false);
            pddl31.setActionCommand(ToXPDDL.PDDL_3_1);
            //RMPL
            rmpl10.setOpaque(false);
            rmpl10.setActionCommand(ToXRMPL.RMPL_1_0);
            

            languageButtonsGroup = new ButtonGroup();
            languageButtonsGroup.add(pddl21);
            languageButtonsGroup.add(pddl22);
            languageButtonsGroup.add(pddl30);
            languageButtonsGroup.add(pddl31);
            languageButtonsGroup.add(rmpl10);
            languageButtonsGroup.setSelected(pddl21.getModel(), true);


            settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
            settingsPanel.setOpaque(false);
            settingsPanel.add(pddl21);
            settingsPanel.add(pddl22);
            settingsPanel.add(pddl30);
            settingsPanel.add(pddl31);
            settingsPanel.add(rmpl10);
                        
            return settingsPanel;
            
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
	private JPanel getTranslationSelactionPanel() {
		if (pddlPanel == null) {
			pddlPanel = new JPanel(new BorderLayout());
			pddlPanel.setPreferredSize(new Dimension(screenSize.width/4 - 20, screenSize.height));
			
                        pddlPanel.add(getProjectPDDLSelectionPane(), BorderLayout.CENTER);			
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
		}

		return petriPanel;
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

				//mainTabbedPane.setSelectedIndex(0);
                                CardLayout cl = (CardLayout)(mainTabbedPane.getLayout());
                                cl.show(mainTabbedPane, "UML");
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


            //JButton applyNewEvaluationButton = new JButton("Apply Modified Evaluation",  new ImageIcon("resources/images/apply.png"));
            //planEvaluationToolBar.add(applyNewEvaluationButton);

            planEvaluationToolBar.addSeparator();

            JButton insertRationaleButton = new JButton("Insert Rationale",  new ImageIcon("resources/images/feedbackrationale.png"));
            insertRationaleButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN plan evaluation edit
                        insertPlanEvaluationRationaleAction.actionPerformed(e);
                    }
            });
            planEvaluationToolBar.add(insertRationaleButton);

            checkExistingRationaleButton = new JButton("Reuse Existing Rationales",  new ImageIcon("resources/images/feedbackrationale.png"));
            checkExistingRationaleButton.setActionCommand("reuse");
            checkExistingRationaleButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN rationales reuse
                        if (checkExistingRationaleButton.getActionCommand().equals("reuse")){
                            checkExistingRationaleButton.setActionCommand("stop");
                            checkExistingRationaleButton.setText("Stop Reasoning");
                            reuseRationalesAction.actionPerformed(e);
                        }
                        else{
                            //stop the thread
                            if(reuserationaleThread.isAlive()){
                                try {
                                        // waits for the thread to return
                                        reuserationaleThread.join(2000);// 2 seconds time-out
                                } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                }
                                if(reuserationaleThread.isAlive()){
                                    reuserationaleThread.interrupt();
                                }
                            }
                            checkExistingRationaleButton.setActionCommand("reuse");
                            checkExistingRationaleButton.setText("Reuse Existing Rationales");
                            planSimStatusBar.setText("Status: Reasoning process stopped.");

                        }

                        /*
                        try {

                            
                            String theoryText = "casa(a1). \n"+
                            "casa(a2).\n"+
                            "casa(a3).\n";
                             
                            Prolog engine = new Prolog();
                            Theory t = new Theory(new java.io.FileInputStream("/home/tiago/Desktop/base.pl"));
                            //Theory t = new Theory(theoryText);
                            engine.setTheory(t);
                            //SolveInfo answer = engine.solve("casa(X).");
                            
                            SolveInfo answer = engine.solve("teste(p1,_).");
                            while (answer.isSuccess()) {
                                System.out.println("solution: " + answer.getSolution() + " - bindings: " + answer);                                
                                System.out.println("X: " + answer.getTerm("X"));
                                if (engine.hasOpenAlternatives()) {
                            try {
                                answer = engine.solveNext();
                            } catch (NoMoreSolutionException ex) {
                                Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                            }
                                } else {
                                    break;
                                }
                            }

                            //engine.solveHalt();

                            
                        } catch (UnknownVarException ex) {
                            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (NoSolutionException ex) {
                            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (MalformedGoalException ex) {
                            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (InvalidTheoryException ex) {
                            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                            Logger.getLogger(ItSIMPLE.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        */


                             

                    }
            });
            planEvaluationToolBar.add(checkExistingRationaleButton);

            

            //JButton addPlanToDatabaseButton = new JButton("Add Plan to Database",  new ImageIcon("resources/images/addplantodatabase.png"));
            //addPlanToDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
            //        @Override
            //        public void actionPerformed(ActionEvent e) {
            //            //RUN plan evaluation edit
            //            insertPlanIntoDatabaseAction.actionPerformed(e);
            //        }
            //});
            //planEvaluationToolBar.add(addPlanToDatabaseButton);


            return planEvaluationToolBar;
	}


        private JToolBar getPlanDatabaseToolBar() {

            JToolBar planDatabaseToolBar = new JToolBar();
            planDatabaseToolBar.setRollover(true);

            // create the buttons

            //select plans from data base

            JButton searchButton = new JButton("Search",new ImageIcon("resources/images/searchdatabase.png"));
            searchButton.setToolTipText("Search plans in the database");
            searchButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN search plan from database
                        searchPlanInDatabaseAction.actionPerformed(e);
                    }
            });
            planDatabaseToolBar.add(searchButton);

            final JButton filterButton = new JButton("Filter",new ImageIcon("resources/images/filtersearchdatabase.png"));
            filterButton.setToolTipText("Filter the search for plans in the database (use colunms of table 'plan' only)");
            filterButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //hide/show filter panel
                        if (planFilterPanel.isVisible()){
                           planFilterPanel.setVisible(false);
                           filterButton.setText("Filter");
                        }else{
                           planFilterPanel.setVisible(true);
                           filterButton.setText("Don't filter");
                        }
                    }
            });
            planDatabaseToolBar.add(filterButton);
                        
            planDatabaseToolBar.addSeparator();


            //insert plan into data base
            JButton addPlanToDatabaseButton = new JButton("Add Plan",  new ImageIcon("resources/images/addplantodatabase.png"));
            addPlanToDatabaseButton.setToolTipText("Add current plan to the database");
            addPlanToDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN plan evaluation edit
                        insertPlanIntoDatabaseAction.actionPerformed(e);
                    }
            });
            planDatabaseToolBar.add(addPlanToDatabaseButton);

            
            //load plan from data base
            JButton loadPlanFromDatabaseButton = new JButton("Load Plan",new ImageIcon("resources/images/getfromdatabase.png"));
            loadPlanFromDatabaseButton.setToolTipText("Load selected plan into itSIMPLE");
            loadPlanFromDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN loading plan
                        loadPlanFromDatabaseAction.actionPerformed(e);
                    }
            });
            planDatabaseToolBar.add(loadPlanFromDatabaseButton);


            //delete plan from data base
            JButton deletePlanFromDatabaseButton = new JButton("Delete Plan",new ImageIcon("resources/images/deleteplanfromdatabase.png"));
            deletePlanFromDatabaseButton.setToolTipText("Delete selected plan");
            deletePlanFromDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN loading plan
                        deletePlanFromDatabaseAction.actionPerformed(e);
                    }
            });
            planDatabaseToolBar.add(deletePlanFromDatabaseButton);


            //update plan from data base
            JButton updatePlanFromDatabaseButton = new JButton("Update Plan",new ImageIcon("resources/images/updateplanfromdatabase.png"));
            updatePlanFromDatabaseButton.setToolTipText("Update current plan");
            updatePlanFromDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN update plan
                        updatePlanFromDatabaseAction.actionPerformed(e);
                    }
            });
            planDatabaseToolBar.add(updatePlanFromDatabaseButton);


            return planDatabaseToolBar;
	}



        private JToolBar getRationaleDatabaseToolBar() {

            JToolBar rationaleDatabaseToolBar = new JToolBar();
            rationaleDatabaseToolBar.setRollover(true);

            // create the buttons

            //select rationales from data base
            JButton searchButton = new JButton("Search",new ImageIcon("resources/images/searchdatabase.png"));
            searchButton.setToolTipText("Search rationales in the database");
            searchButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN search plan from database
                        searchRationaleInDatabaseAction.actionPerformed(e);
                    }
            });
            rationaleDatabaseToolBar.add(searchButton);

            rationaleDatabaseToolBar.addSeparator();

            /*
            //insert new rationale into data base
            JButton addRationaleToDatabaseButton = new JButton("Add Plan",  new ImageIcon("resources/images/addplantodatabase.png"));
            addRationaleToDatabaseButton.setToolTipText("Add current plan to the database");
            addRationaleToDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN plan evaluation edit
                        insertPlanIntoDatabaseAction.actionPerformed(e);
                    }
            });
            rationaleDatabaseToolBar.add(addRationaleToDatabaseButton);
             */
            
            //edit rationale from database
            JButton editRatioanleFromDatabaseButton = new JButton("Edit Rationale",new ImageIcon("resources/images/editrationalefromdatabase.png"));
            editRatioanleFromDatabaseButton.setToolTipText("Edit selected rationale");
            editRatioanleFromDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN editing plan
                        editRationaleFromDatabaseAction.actionPerformed(e);
                    }
            });
            rationaleDatabaseToolBar.add(editRatioanleFromDatabaseButton);
            

            //delete rationale from database
            JButton deleteRationaleFromDatabaseButton = new JButton("Delete Rationale",new ImageIcon("resources/images/deleteplanfromdatabase.png"));
            deleteRationaleFromDatabaseButton.setToolTipText("Delete selected rationale");
            deleteRationaleFromDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN loading plan
                        deleteRationaleFromDatabaseAction.actionPerformed(e);
                    }
            });
            rationaleDatabaseToolBar.add(deleteRationaleFromDatabaseButton);


            rationaleDatabaseToolBar.addSeparator();

            //load reference plan from database
            JButton loadReferencePlanFromDatabaseButton = new JButton("Load Plan",new ImageIcon("resources/images/getfromdatabase.png"));
            loadReferencePlanFromDatabaseButton.setToolTipText("Load reference plan of the selected rationale");
            loadReferencePlanFromDatabaseButton.addActionListener(new java.awt.event.ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        //RUN editing plan
                        loadReferencePlanFromDatabaseAction.actionPerformed(e);
                    }
            });
            rationaleDatabaseToolBar.add(loadReferencePlanFromDatabaseButton);


            return rationaleDatabaseToolBar;
	}




        public void setMainTabbedPaneSelectedIndex(int index){
		//mainTabbedPane.setSelectedIndex(index);
                String selectedtab = "";
                switch(index){

		            case 0:{// uml
		            	selectedtab = "UML";
		            }
		            break;

		            case 1:{// petri
		            	selectedtab = "Petri Net";
		            }
		            break;

		            case 2:{// plan sim
		            	selectedtab = "Translation";
		            }
		            break;

                            case 3:{// planning
		            	selectedtab = "Planning";
		            }
		            break;

		            }

                CardLayout cl = (CardLayout)(mainTabbedPane.getLayout());
                cl.show(mainTabbedPane, selectedtab);
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
                petriDetailsTextPane.setText("<html><font size='-1' face='Arial'>Select a project.<html>");

		for (int i = 0; i<treeRoot.getChildCount(); i++){
			ItTreeNode currentNode = (ItTreeNode)treeRoot.getChildAt(i);

			Element project = currentNode.getData();
                        //System.out.print(project);
                        //Check if the project is a UML project (tag named project)
                        if (project.getName().equals("project")){
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
                                                            petriDetailsTextPane.setText(details);
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
	}
	
        
        
/**
	 * This method should be called every time the user changes
	 * the names of elements in the project tree.
	 */
	private void updateTreeChanges(DefaultTreeModel treeModel){
		//1. get tree root
		ItTreeNode problemsPlanTreeRoot = (ItTreeNode)treeModel.getRoot();

		//1.1 for each project...
		for(int projectIndex = 0; projectIndex < problemsPlanTreeRoot.getChildCount(); projectIndex++){
			ItTreeNode project = (ItTreeNode)problemsPlanTreeRoot.getChildAt(projectIndex);

			//1.2 update project name
			String projectName = project.getData().getChildText("name");
			if(!projectName.equals(project.getUserObject())){
				project.setUserObject(projectName);
				treeModel.nodeChanged(project);
			}

                        //Check if it is a UML project
                        if (project.getData().getName().equals("project")){
                            //1.3 for each domain in project...
                            for(int domainIndex = 0; domainIndex < project.getChildCount(); domainIndex++){
                                    ItTreeNode domain = (ItTreeNode)project.getChildAt(domainIndex);

                                    // 1.4 update domain name
                                    String domainName = domain.getData().getChildText("name");
                                    if(!domainName.equals(domain.getUserObject())){
                                            domain.setUserObject(domainName);
                                            treeModel.nodeChanged(domain);
                                    }

                                    //1.5 for each problem in domain...
                                    for(int problemIndex = 0; problemIndex < domain.getChildCount(); problemIndex++){
                                            ItTreeNode problem = (ItTreeNode)domain.getChildAt(problemIndex);

                                            //1.6 update problem name
                                            String problemName = problem.getData().getChildText("name");
                                            if(!problemName.equals(problem.getUserObject())){
                                                    problem.setUserObject(problemName);
                                                    treeModel.nodeChanged(problem);
                                            }
                                    }
                            }

                        }
                        //Check if it is a PDDL project
                        else if (project.getData().getName().equals("pddlproject")){
                            //1.3 for each problem instance in project...
                            for(int problemIndex = 0; problemIndex < project.getChildCount(); problemIndex++){
                                    ItTreeNode problem = (ItTreeNode)project.getChildAt(problemIndex);

                                    // 1.4 update domain name
                                    String domainName = problem.getData().getChildText("name");
                                    if(!domainName.equals(problem.getUserObject())){
                                            problem.setUserObject(domainName);
                                            treeModel.nodeChanged(problem);
                                    }

                            }

                        }
			
		}

	}        
        

	/**
	 * This method should be called every time the user changes
	 * the selected tab to the Plan Sim tab.
	 */
	private void updatePlanSimTrees(){
		//1. problems plan tree
		ItTreeNode problemsPlanTreeRoot = (ItTreeNode)problemsPlanTreeModel.getRoot();

		//1.1 for each project...
		for(int projectIndex = 0; projectIndex < problemsPlanTreeRoot.getChildCount(); projectIndex++){
			ItTreeNode project = (ItTreeNode)problemsPlanTreeRoot.getChildAt(projectIndex);

			//1.2 update project name
			String projectName = project.getData().getChildText("name");
			if(!projectName.equals(project.getUserObject())){
				project.setUserObject(projectName);
				problemsPlanTreeModel.nodeChanged(project);
			}

                        //Check if it is a UML project
                        if (project.getData().getName().equals("project")){
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
                        //Check if it is a UML project
                        else if (project.getData().getName().equals("pddlproject")){
                            //1.3 for each problem instance in project...
                            for(int problemIndex = 0; problemIndex < project.getChildCount(); problemIndex++){
                                    ItTreeNode problem = (ItTreeNode)project.getChildAt(problemIndex);

                                    // 1.4 update domain name
                                    String domainName = problem.getData().getChildText("name");
                                    if(!domainName.equals(problem.getUserObject())){
                                            problem.setUserObject(domainName);
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
		Object[] checked = null;
		try {
			checked = CheckBoxNode.getCheckedNodes(
					(CheckBoxNode)variablesPlanTree.getModel().getRoot());
			
		} catch (Exception e) {
			// TODO: handle exception
		}

		if (checked != null){
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
		}

		selectedVariablesPlanTreeModel.setRoot(root);
                selectedVariablesPlanTreeModel.reload();
		selectedVariablesPlanTree.expandRow(0);
	}
        
        
        
        /**
         * This method updates any parallel tree (e.g., planning, analysis) when a new project is opened or created
         * @param doc
         * @param xmlRoot
         * @param newProjectNode 
         */
        public void updateNewProjectParallelTree(DefaultTreeModel treeModel, JTree tree, Document doc, Element xmlRoot, ItTreeNode newProjectNode){        
                                    
            
             // plan simulation problem tree
            ItTreeNode problemsPlanTreeRoot = (ItTreeNode)treeModel.getRoot();
            //opening
            //ItTreeNode planProjectNode = (ItTreeNode)newProjectNode.clone();
            //new project
            //ItTreeNode planProjectNode = new ItTreeNode(xmlRoot.getChildText("name"), xmlRoot, null, null);
            ItTreeNode planProjectNode = newProjectNode;
            planProjectNode.setIcon(new ImageIcon("resources/images/project.png"));
            treeModel.insertNodeInto(planProjectNode, problemsPlanTreeRoot, problemsPlanTreeRoot.getChildCount());
            //check if this is a UML project
            if (xmlRoot.getName().equals("project")){
                List<?> domains = doc.getRootElement().getChild("diagrams").getChild("planningDomains").getChildren("domain");
                for (Iterator<?> iter = domains.iterator(); iter.hasNext();) {
                        Element domain = (Element) iter.next();
                        ItTreeNode planDomainNode = new ItTreeNode(domain.getChildText("name"), domain, null, null);
                        planDomainNode.setIcon(new ImageIcon("resources/images/domain.png"));
                        treeModel.insertNodeInto(planDomainNode, planProjectNode, planProjectNode.getChildCount());
                        List<?> problems = domain.getChild("planningProblems").getChildren("problem");
                        for (Iterator<?> iterator = problems.iterator(); iterator.hasNext();) {
                                Element problem = (Element) iterator.next();
                                ItTreeNode planProblemNode = new ItTreeNode(problem.getChildText("name"), problem, null, null);
                                planProblemNode.setIcon(new ImageIcon("resources/images/planningProblem.png"));
                                treeModel.insertNodeInto(planProblemNode, planDomainNode, planDomainNode.getChildCount());
                        }
                }


            }
            //check if this is a PDDL project
            else if (xmlRoot.getName().equals("pddlproject")){

                List<?> problems = doc.getRootElement().getChild("problemInstances").getChildren("pddlproblem");
                for (Iterator<?> iter = problems.iterator(); iter.hasNext();) {
                        Element problem = (Element) iter.next();
                        ItTreeNode planpddlProblemNode = new ItTreeNode(problem.getChildText("name"), problem, null, null);
                        planpddlProblemNode.setIcon(new ImageIcon("resources/images/domain.png"));
                        treeModel.insertNodeInto(planpddlProblemNode, planProjectNode, planProjectNode.getChildCount());                                           
                }
            }				
            tree.expandRow(0);
        }
        
        
        public void updateCloseProjectParallelTree(DefaultTreeModel treeModel, int index){
            
            //Close the projects in the plan simulation problem tree
            ItTreeNode treeRoot = (ItTreeNode)treeModel.getRoot();
            treeModel.removeNodeFromParent((ItTreeNode)treeRoot.getChildAt(index));
           
        } 
        
        public void updateNewDomainProjectParallelTree(DefaultTreeModel treeModel, Element domain, ItTreeNode selectedNode){            
                  
            ItTreeNode problemsTreeProjectNode = (ItTreeNode)
				((ItTreeNode)treeModel.getRoot()).getChildAt(treeRoot.getIndex(selectedNode));

            ItTreeNode problemsTreeDomainNode = new ItTreeNode(domain.getChildText("name"), domain, null, null);
            problemsTreeDomainNode.setIcon(new ImageIcon("resources/images/domain.png"));

            treeModel.insertNodeInto(
                            problemsTreeDomainNode, problemsTreeProjectNode, problemsTreeProjectNode.getChildCount());

            List<?> problems = domain.getChild("planningProblems").getChildren("problem");
            for (Iterator<?> iterator = problems.iterator(); iterator.hasNext();) {
                    Element problem = (Element) iterator.next();
                    ItTreeNode planProblemNode = new ItTreeNode(problem.getChildText("name"), problem, null, null);
                    planProblemNode.setIcon(new ImageIcon("resources/images/planningProblem.png"));
                    treeModel.insertNodeInto(
                                    planProblemNode, problemsTreeDomainNode, problemsTreeDomainNode.getChildCount());
            } 
        }
        
        
        public void updateNewProblemProjectParallelTree(DefaultTreeModel treeModel, Element problem, ItTreeNode selectedNode, ItTreeNode project){            
                                  
            ItTreeNode problemsTreeProject = (ItTreeNode)((ItTreeNode)treeModel.getRoot()).getChildAt(treeRoot.getIndex(project));
            //selectedNode.getParent()

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

                    treeModel.insertNodeInto(problemsTreeProblem, problemsTreeDomain, problemsTreeDomain.getChildCount());
            }
            
        }
        

        public void updateDuplicateProblemProjectParallelTree(DefaultTreeModel treeModel, Element problem, ItTreeNode selectedNode, ItTreeNode project){                       
            
            ItTreeNode problemsTreeProject = (ItTreeNode)((ItTreeNode)treeModel.getRoot()).getChildAt(treeRoot.getIndex(project));

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

                    treeModel.insertNodeInto(
                                    problemsTreeProblem, problemsTreeDomain, problemsTreeDomain.getChildCount());
            }
                                    
            
        }
        
        
        public void updateDeleteProblemProjectParallelTree(DefaultTreeModel treeModel, ItTreeNode domain, ItTreeNode problem, ItTreeNode project){                       
        
            ItTreeNode problemsTreeProject = (ItTreeNode)((ItTreeNode)treeModel.getRoot()).getChildAt(treeRoot.getIndex(project));

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

                    treeModel.removeNodeFromParent(problemsTreeProblem);
            }
        }
        
        public void updateDeleteDomainProjectParallelTree(DefaultTreeModel treeModel, ItTreeNode domain, ItTreeNode project){                       
            
            ItTreeNode problemsTreeProject = (ItTreeNode)
					((ItTreeNode)treeModel.getRoot()).getChildAt(treeRoot.getIndex(project));

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
                    treeModel.removeNodeFromParent(problemsTreeDomain);
            }
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

        public void setDiagramRespository(Element one_diagram){
            diagramResposiyory = one_diagram;
        }
        public static Element getDiagramRespository(){
            return diagramResposiyory;
        }
        
        public void setTypeDiagram(int one_Type){
            typeDiagram = one_Type;        
        }
        public static int getTypeDiagram(){
            return typeDiagram;        
        }
        
        public void setObjectDiagram(Element one_diagram){
        
            objectDiagram = one_diagram;
        }
        public static Element getObjectDiagram(){
        
            return objectDiagram;
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
                return languageButtonsGroup.getSelection().getActionCommand();
        }

        public JTextPane getDomainPDDLTextPane(){
            return domainPddlTextPane;
        }
	/*public static PropertiesTabbedPane getItPropertiesPane(){
		return propertiesPane;
	}*/

        
        
        private void savepddlprojectNode(Element project, String filePath){
            //System.out.println("Saving pddl project");
            //Domain: save all domains that have been changed
            Element domainNode = project.getChild("pddldomains");
            for (Iterator<Element> it = domainNode.getChildren().iterator(); it.hasNext();) {
                Element element = it.next();
                savepddldomainNode(element,filePath);                                                                                 
            }

            //Problems: Check and save each modified problem instance
            Element instancesNode = project.getChild("problemInstances");
            for (Iterator<Element> it = instancesNode.getChildren().iterator(); it.hasNext();) {
                Element element = it.next();
                savepddlproblemNode(element);                                                                            
            }

            //Clean problems instances
            Element projectclone = (Element)project.clone();
            projectclone.getChild("problemInstances").removeChildren("pddlproblem");
            projectclone.getChild("generalInformation").removeChildren("id");
            String iprojectcontent = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?> \n" + XMLUtilities.toString(projectclone);

            //Save iProject.xml file
            try {
                FileWriter file = new FileWriter(filePath);
                file.write(iprojectcontent);
                file.close();
            } catch (IOException e1) {
                    e1.printStackTrace();
            }                                    
        }
        
        
        private void savepddldomainNode(Element selected, String filePath){
            //System.out.println("Saving pddl domain");
            String tpath = filePath;
            File theitProjectFile = new File(tpath);
            tpath = tpath.replaceFirst(theitProjectFile.getName(), "");                                       
            String path = tpath + selected.getChildText("name");

            Element content = selected.getChild("content");
            if (content != null){
                FileOutput.saveFile(path, content.getText());                                            
                selected.removeContent(content);                                            
            }
        }
        
        private void savepddlproblemNode(Element selected){
            //System.out.println("Saving pddl problem");
            String path = selected.getAttributeValue("file");
            Element content = selected.getChild("content");
            if (content != null){
                FileOutput.saveFile(path, content.getText());
                selected.removeContent(content);                                            
            }
        }        
              
        private BasicCell selectedCell; 
        
        
       public void setSelectedCell(BasicCell one_selectedCell){
        
            selectedCell = one_selectedCell;
        }
        
        public BasicCell getSelectedCell(){
            return selectedCell;
        }
        

        
        /**
         * LOG Management
         */
        
        /**
         * This function saves 
         * @param domain
         * @param problem
         * @param planner
         */
        private void savetologfile(String message){
        	 /// set start datetime
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
            Date date = new Date();
            String dateTime = dateFormat.format(date);
            
            //put in the log file - TODO: reset it when necessary
            String logfolder= itSettings.getChild("logfolder").getAttributeValue("path");
            //logfolder= "/home/tiago/Dropbox/Experiments/";
            //String logfolder= "resources/log/";
            String logfilepath = logfolder +"logfile.txt";
            
            try {
                PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(logfilepath, true)));
                out.println("["+dateTime+"] " +message);
                out.close();
            } catch (IOException e) {
                //exception handling left as an exercise for the reader
            }
        	
        }        
        
        
        /**
         * This function saves the plan to the log files
         * @param domain
         * @param problem
         * @param planner
         * @param plan
         */
        private void logplan(Element domain, Element problem, Element planner, Element plan){
        	
        	 /// set start datetime
            DateFormat dateFormat = new SimpleDateFormat("HH:mm:ss yyyy-MM-dd");
            Date date = new Date();
            String dateTime = dateFormat.format(date);
            
            //put in the log file - TODO: reset it when necessary
            String logfolder= itSettings.getChild("logfolder").getAttributeValue("path");
        	
        	//LOG: save file (xml) as a backup
            String backuppath = logfolder+"plans/"+domain.getChildText("name").replaceAll(".pddl", "")+"_"+problem.getChildText("name").replaceAll(".pddl", "")+"_"+planner.getChildText("name")+"_"+dateTime+".xml";
            //XMLUtilities.writeToFile(backuppath, result.getDocument());
            try {
                FileWriter file = new FileWriter(backuppath);
                file.write(XMLUtilities.toString(plan));
                file.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        	
        }
        
        
        
        
        
        
        
        
        
	/**
	 * @param argsu
	 */
	public static void main(String[] args) {

		//get CommonDatapddlVer
		Document commonDoc = null;
		try {
			commonDoc = XMLUtilities.readFromFile("resources/settings/commonData.xml");

		} catch (Exception e) {
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
			//UIManager.setLookAndFeel("com.sun.java.swing.plaf.gtk.GTKLookAndFeel"); // GTK+
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
