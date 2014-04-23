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
import itGraph.ItCellViewFactory;
import itGraph.ItGraph;


import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.*;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JPopupMenu;
import javax.swing.JRootPane;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;

import languages.pddl.ToXPDDL;
import languages.pddl.XPDDLToPDDL;

import sourceEditor.ItHilightedDocument;

public class ItTabbedPane extends JTabbedPane implements
		MouseListener, ChangeListener{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2264424271420304723L;
	
	private static Element openTabs = new Element ("openTabs");
	//private Document xmlOpenTab = new Document(openTabs);	
	private JPopupMenu popupMenu;
	private int selectedTabIndex;
	private int editStateTabIndex = -1;
	private PropertiesTabbedPane propertiesPane;
	
	public ItTabbedPane() {
	    super();
	    //setUI(new ClosingTabbedPaneUI());
	    createPopupMenu();
	    addMouseListener(this);
	    addChangeListener(this);
	    
	    // Configure the tabs to scroll
	    setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
	}	
	
	public void stateChanged(ChangeEvent e) {
		// TODO Auto-generated method stub	
		selectedTabIndex = getSelectedIndex();		
		
		// this is done because the sizes may be different
		// they are different when a tab is closed
		// TODO deal the tab closing case
		if(selectedTabIndex > -1 && openTabs.getChildren().size() == getTabCount()){			
			Element selectedTab = (Element)openTabs.getChildren().get(selectedTabIndex);
					
			Element project = ItSIMPLE.getInstance().getItTree()
				.getProject(selectedTab.getAttributeValue("projectID"));
			
			String type = selectedTab.getChildText("type");
			if(type.equals("objectDiagram")){
				Element diagram = null;
				try {
					XPath path = new JDOMXPath("diagrams/planningDomains/domain[@id='"+ selectedTab.getChildText("domain") 
							+"']/planningProblems/problem[@id='"+ selectedTab.getChildText("problem") 
							+"']/objectDiagrams/objectDiagram[@id='"+ selectedTab.getAttributeValue("diagramID") +"']");
					diagram = (Element)path.selectSingleNode(project);
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				if(diagram != null){
					// get the current jgraph
					JRootPane panel = (JRootPane)getComponentAt(selectedTabIndex);				
					JScrollPane scroll = (JScrollPane)panel.getComponent(3);// don't know exactly why the scrool pane is added in this position
					ItGraph graph = (ItGraph)scroll.getViewport().getView();
					
					AdditionalPropertiesTabbedPane.getInstance().showAdditionalProperties(diagram, graph);
				}
			}
			else{
				AdditionalPropertiesTabbedPane.getInstance().setNoSelection();
			}
		}		

	}
	 
	public void mouseClicked(MouseEvent e) {}	 
	public void mouseEntered(MouseEvent e) {}    
	  public void mouseExited(MouseEvent e) {}
	  public void mousePressed(MouseEvent mouseEvent) {// this is for Linux
		  if (mouseEvent.isPopupTrigger()) {	           
	            //selectedTabIndex = indexAtLocation(mouseEvent.getX(), mouseEvent.getY()); 
	            
	            // Only show for top row
	            if (getTabPlacement() == JTabbedPane.TOP) {
	            popupMenu.show(this, mouseEvent.getX(), mouseEvent.getY());
	            }
	        }
	  }
	  public void mouseReleased(MouseEvent mouseEvent) {//this is for Windows
		  if (mouseEvent.isPopupTrigger()) {	           
	           // selectedTabIndex = indexAtLocation(mouseEvent.getX(), mouseEvent.getY()); 
	            
	            // Only show for top row
	            if (getTabPlacement() == JTabbedPane.TOP) {
	            popupMenu.show(this, mouseEvent.getX(), mouseEvent.getY());
	            }
	        }
	        
	  }
	
	  public void openTab(Element diagram, String id, String title, 
			  String type, Element project, Element commonData, Element projectHeader, String language) {			
		  	String diagramType = diagram.getName();
			
			// Checks whether the diagram is already open		
			String xpath = "";
			if (language.equals("UML")){
				if (diagramType.equals("objectDiagram")){
					xpath = "openTab[@language='"+ language + "' and @projectID='" + projectHeader.getAttributeValue("id") +
						"' and @diagramID='" + id +
						"' and type='" + diagramType + "' and problem='" +
						diagram.getParentElement().getParentElement().getAttributeValue("id") + 
						"' and domain='" + diagram.getParentElement().getParentElement().getParentElement().getParentElement().getAttributeValue("id") + "']";
				}
				else if (diagramType.equals("repositoryDiagram")){
					xpath = "openTab[@language='"+ language + "' and @projectID='" + projectHeader.getAttributeValue("id") +
						"' and @diagramID='" + id +
						"' and type='" + diagramType + "' and domain='" + diagram.getParentElement().getParentElement().getAttributeValue("id") + "']";
				}			
				else{
					xpath = "openTab[@language='"+ language + "' and @projectID='" + projectHeader.getAttributeValue("id") +
						"' and @diagramID='" + id +
						"' and type='" + diagramType + "']"; 
				}				
			}
			else if (language.equals("PDDL")){
				if (diagramType.equals("domain")){
					xpath = "openTab[@language='"+ language + "' and @projectID='" + projectHeader.getAttributeValue("id") +
					"' and @diagramID='" + id +
					"' and type='" + diagramType + "']";
				}
				else if (diagramType.equals("problem")){
					xpath = "openTab[@language='"+ language + "' and @projectID='" + projectHeader.getAttributeValue("id") +
						"' and @diagramID='" + id +
						"' and type='" + diagramType + "' and domain='" + diagram.getParentElement().getParentElement().getAttributeValue("id") + "']";
				}	
			}
			else if (language.equals("PetriNet")){
				xpath = "openTab[@language='"+ language + "' and @projectID='" + projectHeader.getAttributeValue("id") +
				"' and @diagramID='" + id +
				"' and type='" + diagramType + "']"; 	
			}
			else{
				xpath = "openTab[@language='"+ language + "' and @projectID='" + projectHeader.getAttributeValue("id") +
					"' and @diagramID='" + id +
					"' and type='" + diagramType + "']"; 
			}	
			
			//Checks if it is already opened
			Element openingDiagram = null;
			try {
				XPath path = new JDOMXPath(xpath);
				openingDiagram = (Element)path.selectSingleNode(openTabs);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			if (openingDiagram != null){
				// select the tab if it is already open
				setSelectedIndex(openingDiagram.getParent().indexOf(openingDiagram));
				
			} else {
				
				//New Tab
				Document newDoc = null;
				try {
					newDoc = XMLUtilities.readFromFile("resources/settings/commonData.xml");
				} catch (JDOMException e) {
					
					e.printStackTrace();
				} catch (IOException e) {
					
					e.printStackTrace();
				}
				Element openTab = ((Element) newDoc.getRootElement().getChild("internalUse").getChild("openTab").clone());
				
				Icon icon = null;
				JRootPane panel = null;
				
				//Check Language Type
				if (language.equals("UML")){
					// Open the tab if not
					ItToolBar toolBar = new ItToolBar(type,"UML");				
					toolBar.setName(title);
					GraphModel model = new DefaultGraphModel();
					GraphLayoutCache view = new GraphLayoutCache(model, new ItCellViewFactory());
					ItGraph diagramGraph = new ItGraph(view, toolBar, propertiesPane, project, diagram, commonData, language);
					toolBar.setGraph(diagramGraph);
					diagramGraph.setVisible(false);
					JScrollPane graphScrollPane = new JScrollPane(diagramGraph);
					panel = new JRootPane();
					panel.setLayout(new BorderLayout());
					panel.add(toolBar, BorderLayout.NORTH);
					panel.add(graphScrollPane, BorderLayout.CENTER);
										
					diagramGraph.buildDiagram();
					diagramGraph.setVisible(true);
					// Prepare Tab Icon
					icon = new ImageIcon("resources/images/"+diagram.getName()+".png");

					
					
					//Set Tab properties
					openTab.setAttribute("language", language);
					openTab.setAttribute("diagramID", id);
					openTab.setAttribute("projectID", projectHeader.getAttributeValue("id"));
					openTab.getChild("type").setText(type);				
					
					if (type.equals("objectDiagram")){
																  //object diagrams		problem
						openTab.getChild("problem").setText(diagram.getParentElement().getParentElement().getAttributeValue("id"));
																//  object diagrams    problem           planningProblems     domain
						openTab.getChild("domain").setText(diagram.getParentElement().getParentElement().getParentElement().getParentElement().getAttributeValue("id"));
					}
					
					else if (type.equals("repositoryDiagram")){
																//  repositoryDiagrams  domain
						openTab.getChild("domain").setText(diagram.getParentElement().getParentElement().getAttributeValue("id"));
					}
									
				}
				else if (language.equals("PDDL")){
					
					ItToolBar toolBar = new ItToolBar(type,"PDDL");				
					toolBar.setName(title);					
					
					ItHilightedDocument pddlDocument = new ItHilightedDocument();
					pddlDocument.setHighlightStyle(ItHilightedDocument.PDDL_STYLE);
					JTextPane pddlTextPane = new JTextPane(pddlDocument);
					pddlTextPane.setFont(new Font("Courier", 0, 12));
					
					toolBar.setTextPane(pddlTextPane);
					
					JScrollPane pddlScrollPane = new JScrollPane(pddlTextPane);
					panel = new JRootPane();
					panel.setLayout(new BorderLayout());
					panel.add(toolBar, BorderLayout.NORTH);
					panel.add(pddlScrollPane, BorderLayout.CENTER);
					
					icon = new ImageIcon("resources/images/"+diagram.getName()+".png");
					
					if (type.equals("domain")){
														//PlanningDomains  diagrams           project
						Element projectDomain = diagram.getParentElement().getParentElement().getParentElement();
//						 TODO PDDL 3.0 was made default, but the user should be able to chose it
						Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(projectDomain, ToXPDDL.PDDL_3_0, null);
						String domainText = XPDDLToPDDL.parseXPDDLToPDDL(xpddlDomain, "  ");
						
						pddlTextPane.setText(domainText);	
					}
					else if (type.equals("problem")){
						Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(diagram, ToXPDDL.PDDL_3_0);
						String problemText = XPDDLToPDDL.parseXPDDLToPDDL(xpddlProblem, "  ");
						pddlTextPane.setText(problemText);
					}
					
					
					
					//	Set Tab properties
					openTab.setAttribute("language", language);
					openTab.setAttribute("diagramID", id);
					openTab.setAttribute("projectID", projectHeader.getAttributeValue("id"));
					openTab.getChild("type").setText(type);		
					
					if (type.equals("problem")){
																// planningProblems  domains
						openTab.getChild("domain").setText(diagram.getParentElement().getParentElement().getAttributeValue("id"));
					}
					
				}
				else if (language.equals("PetriNet")){
					// Open the tab if not
					ItToolBar toolBar = new ItToolBar(type,"PetriNet");				
					toolBar.setName(title);
					GraphModel model = new DefaultGraphModel();
					GraphLayoutCache view = new GraphLayoutCache(model, new ItCellViewFactory());
					ItGraph diagramGraph = new ItGraph(view, toolBar, propertiesPane, project, diagram, commonData, language);
					toolBar.setGraph(diagramGraph);
					diagramGraph.setVisible(false);
					diagramGraph.setInfoPane(itSIMPLE.ItSIMPLE.getInstance().getInfoEditorPane());
					JScrollPane graphScrollPane = new JScrollPane(diagramGraph);
					panel = new JRootPane();
					panel.setLayout(new BorderLayout());
					panel.add(toolBar, BorderLayout.NORTH);
					panel.add(graphScrollPane, BorderLayout.CENTER);
					//panel.setContentPane(graphScrollPane);
					
					diagramGraph.buildDiagram();
					diagramGraph.setVisible(true);
					// Prepare Tab Icon
					icon = new ImageIcon("resources/images/"+diagram.getName()+".png");
			
					
					
					//Set Tab properties
					openTab.setAttribute("language", language);
					openTab.setAttribute("diagramID", id);
					openTab.setAttribute("projectID", projectHeader.getAttributeValue("id"));
					openTab.getChild("type").setText(type);				
									
				}				
				
				if(icon != null && panel != null){
					// add the tab
					openTabs.addContent(openTab);				
					addTab(title, icon, panel);
					if (getTabCount() > 1){
						setSelectedIndex(getTabCount()-1);	
					}
				}
				
			}								
		
	  }
	  
	  public void openEditStateTab(Element diagram, Element domain, Element project){
		  	ItToolBar toolBar = new ItToolBar(diagram.getName(),"UML");
		  	toolBar.addCloseEditButton();
			toolBar.setName(diagram.getChildText("name"));
			GraphModel model = new DefaultGraphModel();
			GraphLayoutCache view = new GraphLayoutCache(model, new ItCellViewFactory());
			ItGraph diagramGraph = new ItGraph(view, toolBar, propertiesPane, project, diagram, ItSIMPLE.getCommonData(), "UML");
			toolBar.setGraph(diagramGraph);
			diagramGraph.setVisible(false);
			JScrollPane graphScrollPane = new JScrollPane(diagramGraph);
			JRootPane panel = new JRootPane();
			panel.setLayout(new BorderLayout());			
			panel.add(toolBar, BorderLayout.NORTH);
			panel.add(graphScrollPane, BorderLayout.CENTER);
			
			diagramGraph.buildEditStateDiagram(domain);
			diagramGraph.setVisible(true);
			
			addTab("Current State", new ImageIcon("resources/images/"+diagram.getName()+".png"), panel);
			if (getTabCount() > 1){
				setSelectedIndex(getTabCount()-1);	
			}
			editStateTabIndex = getTabCount() - 1;
			
	  }
	  
	  public void closeEditStateTab(){
		  remove(editStateTabIndex);
		  editStateTabIndex = -1;
	  }
	  
	  private JPopupMenu createPopupMenu() {
	        
	        popupMenu = new JPopupMenu();
	        
	        popupMenu.add(new CloseAction("Close"));
	        popupMenu.add(new CloseOthersAction("Close Others"));
	        popupMenu.add(new CloseAllAction("Close All"));
	        
	        return popupMenu;
	    }
	  
	  private class CloseAction extends AbstractAction {

	        private static final long serialVersionUID = -2625928077474199856L;

	        public CloseAction(String name) {
	            super(name);
	        }

	        public void actionPerformed(ActionEvent actionEvent) {
	            closeTab(selectedTabIndex);
	        }

			
	    }
	    
	    private class CloseOthersAction extends AbstractAction {

	        private static final long serialVersionUID = -2625928077474199856L;

	        public CloseOthersAction(String name) {
	            super(name);
	        }

	        public void actionPerformed(ActionEvent actionEvent) {
	            
	            // First remove higher indexes 
	            int tabCount = getTabCount();
	            
	            if (selectedTabIndex < tabCount - 1) {
	                for (int i = selectedTabIndex + 1; i < tabCount; i++) {
	                    closeTab(selectedTabIndex + 1);
	                }
	            }
	            
	            if (selectedTabIndex > 0) {
	                for (int i = 0; i < selectedTabIndex; i++) {
	                    closeTab(0);
	                }
	            }
	        }

	    }

	    private class CloseAllAction extends AbstractAction {

	        private static final long serialVersionUID = -2625928077474199856L;

	        public CloseAllAction(String name) {
	            super(name);
	        }

	        public void actionPerformed(ActionEvent actionEvent) {
	            
	            int tabCount = getTabCount();
	            
	            for (int i = 0; i < tabCount; i++) {
	                closeTab(0);
	            }
	        }
	    }
	  
	  public void closeTab(int index) {		  
			
		  openTabs.removeContent(index);		  
		  remove(index);
			
	  }
	  
	  public void setPropertiesPane(PropertiesTabbedPane propertiesPane){
		  this.propertiesPane = propertiesPane;
	  }

	/**
	 * @return Returns the openTabs.
	 */
	public static Element getOpenTabs() {
		return openTabs;
	}
	
	/**
	 * Repaints all objects in an open diagram of the specified type
	 * @param diagramType the type of diagrams to be painted
	 */
	public void repaintOpenDiagrams(String diagramType){
		// get all open repository or object diagrams
    	List<?> openDiagrams = null;
    	try{
    		XPath path = new JDOMXPath("openTab[type='"+ diagramType +"']");
    		openDiagrams = path.selectNodes(openTabs);
    	}
    	catch(JaxenException e2){
    		e2.printStackTrace();
    	}
    	
    	// repaint the elements
    	for (Iterator<?> iter = openDiagrams.iterator(); iter
				.hasNext();) {
			Element openTab = (Element) iter.next();
			JRootPane rootPane = (JRootPane)getComponentAt(openTabs.indexOf(openTab));
			ItGraph openGraph = (ItGraph)((JScrollPane)rootPane.getComponent(3)).getViewport().getView();
			openGraph.repaintAllElements();
		}
	}

			
}
