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
**/

package src.itgraph;

import src.gui.AdditionalPropertiesTabbedPane;
import src.gui.ItSIMPLE;
import src.gui.ItTabbedPane;
import src.gui.ItToolBar;
import src.gui.ItTree;
import src.gui.ItTreeNode;
import src.gui.PropertiesTabbedPane;
import src.itgraph.petrinets.Arc;
import src.itgraph.petrinets.PlaceCell;
import src.itgraph.petrinets.TransitionCell;
import src.itgraph.uml.ActionAssociation;
import src.itgraph.uml.ActorCell;
import src.itgraph.uml.BasicAssociation;
import src.itgraph.uml.ClassAssociation;
import src.itgraph.uml.ClassCell;
import src.itgraph.uml.Dependency;
import src.itgraph.uml.EnumerationCell;
import src.itgraph.uml.FinalStateCell;
import src.itgraph.uml.Generalization;
import src.itgraph.uml.InitialStateCell;
import src.itgraph.uml.ObjectAssociation;
import src.itgraph.uml.ObjectCell;
import src.itgraph.uml.StateCell;
import src.itgraph.uml.UseCaseAssociation;
import src.itgraph.uml.UseCaseCell;
import src.languages.uml.ocl.OCLUtilities;
import src.languages.xml.XMLUtilities;
import src.util.filefilter.PNGFileFilter;

import java.awt.Color;
import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JEditorPane;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.KeyStroke;
import javax.swing.tree.DefaultTreeModel;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jgraph.JGraph;
import org.jgraph.event.GraphModelEvent;
import org.jgraph.event.GraphModelListener;
import org.jgraph.event.GraphSelectionEvent;
import org.jgraph.event.GraphSelectionListener;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.DefaultGraphModel;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.GraphLayoutCache;
import org.jgraph.graph.GraphModel;



public class ItGraph extends JGraph implements GraphModelListener, GraphSelectionListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8399363338919894142L;	
	
	private ItToolBar toolBar = null;
	private PropertiesTabbedPane propertiesPane = null;	
	private Element project = null;
	private Element diagram = null;
	private Element additional = null;
	private Element commonData = null;
	private JPopupMenu popupMenu = null;
	private String language;
	private JEditorPane infoPane = null;
	
	// used for movie maker
	private Element domain = null;
	

	public ItGraph() {
		super();				
		setUI(new ItGraphUI());
		setDisconnectable(false);
	}

	public ItGraph(GraphModel model) {
		super(model);
		setUI(new ItGraphUI());
		setDisconnectable(false);
	}
	
	public ItGraph(Element project, Element diagram, String language){
		super(new GraphLayoutCache(new DefaultGraphModel(), new ItCellViewFactory()));
		
		this.project = project;
		this.diagram = diagram;
		this.language = language;
		
		setUI(new ItGraphUI());
		setDisconnectable(false);
		setEdgeLabelsMovable(true);
	}
	
	public ItGraph(GraphLayoutCache cache, ItToolBar graphToolBar, PropertiesTabbedPane propertiesPane,
			Element xmlProject, Element xmlDiagram, Element xmlCommonData, String language) {
		super(cache);
		this.toolBar = graphToolBar;
		this.propertiesPane = propertiesPane;
		this.project = xmlProject;
		this.diagram = xmlDiagram;
		this.commonData = xmlCommonData;
		this.language = language;
		setUI(new ItGraphUI());
		setDisconnectable(false);
		setEdgeLabelsMovable(true);
		getModel().addGraphModelListener(this);
		addGraphSelectionListener(this);
		add(getPopupMenu(null));
		addKeyListener(this);
	}
	
	public void setToolBar(ItToolBar bar){
		toolBar = bar;
	}
	
	public ItToolBar getToolBar(){
		return toolBar;
	}
	
	public void setProject(Element xmlProject){
		project = xmlProject;
	}
	
	public Element getProject(){
		return project;
	}
	
	public void setDiagram(Element xmlDiagram){
		diagram = xmlDiagram;
	}
	
	public Element getDiagram(){
		return diagram;
	}
	
	public void setDomain(Element domain){
		this.domain = domain;
	}
	
	public List<?> getUseCases(){
		List<?> useCases = null;
		try {
			XPath path = new JDOMXPath("/project/diagrams/useCaseDiagrams/useCaseDiagram/useCases/useCase");
			useCases = path.selectNodes(project);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		return useCases;
	}
	
	public Element getCommonData(){
		return commonData;
	}
	
	public void buildDiagram(){
		Object[] remove = DefaultGraphModel.getAll(this.getModel());
		((DefaultGraphModel)this.getModel()).remove(remove);
		
		if(language.equals("UML")){
			if (diagram.getName().equals("classDiagram")){
				Iterator<?> references = diagram.getChild("classes").getChildren("class").iterator();
				while (references.hasNext()){
					Element reference = (Element)references.next();
					Element Class = getElement(project, "classes", reference.getAttributeValue("id"));
					createGraphElement(Class, reference,null);
				}
				
				Iterator<?> classes = diagram.getChild("classes").getChildren("class").iterator();
				while (classes.hasNext()){
					Element reference = (Element)classes.next();
					Element Class = getElement(project, "classes", reference.getAttributeValue("id"));
					Element generalization = Class.getChild("generalization");

					if (!generalization.getAttributeValue("element").equals("")
							&& !generalization.getAttributeValue("id").equals("")){
						Element superClass = getElement(project, "classes", generalization.getAttributeValue("id"));		
						BasicCell sourceCell = getCell(Class.getAttributeValue("id"), Class.getName());								
						BasicCell targetCell = getCell(superClass.getAttributeValue("id"), superClass.getName());								
					
						if(sourceCell != null && targetCell != null){
							Generalization graphGeneralization = new Generalization(generalization, superClass, sourceCell, targetCell);
							graphGeneralization.setRoutePoints();
							repaintElement(graphGeneralization);
							getGraphLayoutCache().insert(graphGeneralization);
						}	
					}				
				}
				
				List<?> enumerations = diagram.getChild("classes").getChildren("enumeration");
				for (Iterator<?> iter = enumerations.iterator(); iter.hasNext();) {
					Element enumerationRef = (Element) iter.next();
					Element enumerationData = getElement(project, "classes", enumerationRef.getAttributeValue("id"));
					createGraphElement(enumerationData, enumerationRef, null);
				}
				
				Iterator<?> associationReferences = diagram.getChild("associations").getChildren("classAssociation").iterator();
				while(associationReferences.hasNext()){
					Element reference = (Element)associationReferences.next();
					Element association = this.getElement(project, "classAssociations", reference.getAttributeValue("id"));
					this.createGraphAssociation(association, reference);
				}
			}
			else if(diagram.getName().equals("useCaseDiagram")){
				Iterator<?> actorReferences = diagram.getChild("actors").getChildren("actor").iterator();
				while (actorReferences.hasNext()){
					Element actor = (Element)actorReferences.next();
					//Element actor = this.getElement(project, "classes", reference.getAttributeValue("id"));
					this.createGraphElement(actor, null, null);
				}
				
				Iterator<?> useCases = diagram.getChild("useCases").getChildren("useCase").iterator();
				while(useCases.hasNext()){
					Element useCase = (Element)useCases.next();
					//Element useCase = this.getElement(project, "classes", reference.getAttributeValue("id"));
					this.createGraphElement(useCase, null, null);
				}
				
				Iterator<?> useCaseAssociations = diagram.getChild("associations").getChildren().iterator();
				while(useCaseAssociations.hasNext()){
					Element useCaseAssociation = (Element)useCaseAssociations.next();
					this.createGraphAssociation(useCaseAssociation, null);
				}
				
			}
			
			else if (diagram.getName().equals("stateMachineDiagram")){
				Iterator<?> states = diagram.getChild("states").getChildren("state").iterator();
				while (states.hasNext()){
					Element state = (Element)states.next();
					createGraphElement(state, null, null);
				}
				
				for (Iterator<?> iter = diagram.getChild("states").getChildren("initialState").iterator(); iter.hasNext();) {
					Element initialState = (Element) iter.next();
					createGraphElement(initialState, null, null);
					
				}
				
				for (Iterator<?> iter = diagram.getChild("states").getChildren("finalState").iterator(); iter.hasNext();) {
					Element finalState = (Element) iter.next();
					createGraphElement(finalState, null, null);
					
				}

				Iterator<?> actions = diagram.getChild("associations").getChildren("action").iterator();
				while(actions.hasNext()){				
					Element action = (Element)actions.next();				
					Element itsClass = this.getElement(project, "classes", action.getChild("reference").getAttributeValue("class"));
					Element operator = null;
					if (itsClass != null){					
						Iterator<?> operators = itsClass.getChild("operators").getChildren("operator").iterator();
						while(operators.hasNext()){
							Element current = (Element)operators.next();
							if (current.getAttributeValue("id").equals(action.getChild("reference").getAttributeValue("operator")))
								operator = current;						
						}
					}
					createGraphAssociation(action, operator);
				}
			}
			
			else if (diagram.getName().equals("repositoryDiagram")){
				//Find out if it is a planning problem
				
				
				Element parentElement = diagram.getParentElement().getParentElement();
				
				//Object Diagram from planning Problem			
				Iterator<?> objectsReferences = diagram.getChild("objects").getChildren("object").iterator();
				while (objectsReferences.hasNext()){
					Element reference = (Element)objectsReferences.next();
					
					
					Element object = this.getElement(parentElement, "objects", reference.getAttributeValue("id"));
					Element itsClass = this.getElement(project, "classes", object.getChildText("class"));							
					createGraphElement(object,reference,itsClass);
				}	
				Iterator<?> objectsAssociations = diagram.getChild("associations").getChildren("objectAssociation").iterator();
				while (objectsAssociations.hasNext()){
					Element reference = (Element)objectsAssociations.next();
					Element association = this.getElement(project, "classAssociations", reference.getChildText("classAssociation"));
					createGraphAssociation(reference, association);
				}				


			}	
			
			else if (diagram.getName().equals("objectDiagram")){			
				
				if(domain == null){											//objectDiagrams	problem			planningProblems	domain			
					domain = diagram.getParentElement().getParentElement().getParentElement().getParentElement();
				}
				
				setEditable(false);
				
				//Object Diagram from planning Problem			
				Iterator<?> objectsReferences = diagram.getChild("objects").getChildren("object").iterator();
				while (objectsReferences.hasNext()){
					Element reference = (Element)objectsReferences.next();
					
					
					Element object = getElement(domain, "objects", reference.getAttributeValue("id"));
					Element itsClass = getElement(project, "classes", object.getChildText("class"));							
					createGraphElement(object,reference,itsClass);
				}	
				Iterator<?> objectsAssociations = diagram.getChild("associations").getChildren("objectAssociation").iterator();
				while (objectsAssociations.hasNext()){
					Element reference = (Element)objectsAssociations.next();
					Element association = getElement(project, "classAssociations", reference.getChildText("classAssociation"));
					createGraphAssociation(reference, association);
				}				


			}
			
		}
		else if(language.equals("PetriNet")){
			if(diagram.getName().equals("pnml")){
				
				
				
				additional = (Element) diagram.clone();
				
				/*List diagramList = new ArrayList();
				diagramList.add(diagram);
				Element modular = toPNML.stateChartsToModularPetriNet(diagramList, project);*/
				//XMLUtilities.printXML(modular);
				
				
				//1.0 set net
				//XMLUtilities.printXML(diagram);
				Element net = diagram.getChild("net");
                                Element page = net.getChild("page");
				
				// 1.1 add places
				List<?> netPlaceList = page.getChildren("place");
				
				for (Iterator<?> placeIter = netPlaceList.iterator(); placeIter.hasNext();) {
					Element place = (Element) placeIter.next();
					PlaceCell placeCell = null;
					if(place.getChild("toolspecific").getChildText("type").equals("pseudoBox"))
						placeCell = new PlaceCell(place, PlaceCell.IMPORT_PLACE, 0);
					else
						placeCell = new PlaceCell(place, PlaceCell.NORMAL_PLACE, 0);
					getGraphLayoutCache().insert(placeCell);
					
					// add the label
					Element placePosition = place.getChild("graphics").getChild("position");
					int x = Integer.parseInt(placePosition.getAttributeValue("x"));
					int y = Integer.parseInt(placePosition.getAttributeValue("y")) + 40;
					GraphLabel placeLabel = new GraphLabel(place.getChild("name"), x, y);
					getGraphLayoutCache().insert(placeLabel);
				}
				
				//2.4 add transitions
				List<?> netTransitionList = page.getChildren("transition");
				for (Iterator<?> transitionIter = netTransitionList.iterator(); transitionIter.hasNext();) {
					Element transition = (Element) transitionIter.next();
					TransitionCell transitionCell = null;
					String type = transition.getChild("toolspecific").getChildText("type");
					if(type.equals("import"))
						transitionCell = new TransitionCell(transition, TransitionCell.IMPORT_TRANSITION, 0);
					else if(type.equals("export"))
						transitionCell = new TransitionCell(transition, TransitionCell.EXPORT_TRANSITION, 0);
					else
						transitionCell = new TransitionCell(transition, TransitionCell.NORMAL_TRANSITION, 0);
					getGraphLayoutCache().insert(transitionCell);
					
					//add the label
					Element transitionPosition = transition.getChild("graphics").getChild("position");
					int x = Integer.parseInt(transitionPosition.getAttributeValue("x"));
					int y = Integer.parseInt(transitionPosition.getAttributeValue("y")) + 40;
					GraphLabel transitionLabel = new GraphLabel(transition.getChild("name"), x, y);
					getGraphLayoutCache().insert(transitionLabel);
					
				}
				
				//2.5 add arcs
				List<?> netArcList = page.getChildren("arc");
				for (Iterator<?> arcIter = netArcList.iterator(); arcIter.hasNext();) {
					Element arc = (Element) arcIter.next();
					String sourceID = arc.getAttributeValue("source");
					String targetID = arc.getAttributeValue("target");
					BasicCell sourceCell = getCell(sourceID, "");
					BasicCell targetCell = getCell(targetID, "");
					Arc arcCell = new Arc(arc, sourceCell, targetCell);
					arcCell.setRoutePoints();
					repaintElement(arcCell);
					getGraphLayoutCache().insert(arcCell);
					
				}
			}
		}
				
	}
	
	public void buildEditStateDiagram(Element domain){
				
		setEditable(false);
		
		//Object Diagram from planning Problem			
		Iterator<?> objectsReferences = diagram.getChild("objects").getChildren("object").iterator();
		while (objectsReferences.hasNext()){
			Element reference = (Element)objectsReferences.next();
			
			
			Element object = getElement(domain, "objects", reference.getAttributeValue("id"));
			Element itsClass = getElement(project, "classes", object.getChildText("class"));							
			createGraphElement(object,reference,itsClass);
		}	
		Iterator<?> objectsAssociations = diagram.getChild("associations").getChildren("objectAssociation").iterator();
		while (objectsAssociations.hasNext()){
			Element reference = (Element)objectsAssociations.next();
			Element association = getElement(project, "classAssociations", reference.getChildText("classAssociation"));
			createGraphAssociation(reference, association);
		}
	}
	
	public void createGraphElement(Element graphElement, Element reference, Element additional){
		if (graphElement.getName().equals("class")){
			ClassCell classCell = new ClassCell(graphElement, reference);			
			getGraphLayoutCache().insert(classCell);			
		}
		
		else if (graphElement.getName().equals("enumeration")){
			EnumerationCell enumeration = new EnumerationCell(graphElement, reference);			
			getGraphLayoutCache().insert(enumeration);			
		}
		
		else if (graphElement.getName().equals("actor")){
			ActorCell actor = new ActorCell(graphElement);
			getGraphLayoutCache().insert(actor);			
		}
		
		else if (graphElement.getName().equals("useCase")){
			UseCaseCell useCase = new UseCaseCell(graphElement);
			getGraphLayoutCache().insert(useCase);
		}
		
		else if (graphElement.getName().equals("state")){
			StateCell state = new StateCell(graphElement);
			getGraphLayoutCache().insert(state);
		}
		
		else if (graphElement.getName().equals("initialState")){
			InitialStateCell initialState = new InitialStateCell(graphElement);
			getGraphLayoutCache().insert(initialState);
		}
		
		else if (graphElement.getName().equals("finalState")){
			FinalStateCell finalState = new FinalStateCell(graphElement);
			getGraphLayoutCache().insert(finalState);
		}
		
		else if (graphElement.getName().equals("object")){
			ObjectCell object = new ObjectCell(graphElement, reference, additional);
			getGraphLayoutCache().insert(object);
		}
	}
	
	public void createGraphAssociation(Element association, Element reference){
		BasicAssociation graphAssociation = null;
		BasicCell sourceCell = null;
		BasicCell targetCell = null;
		Iterator<?> associationEnds;
		if (association.getName().equals("action")){
			associationEnds = association.getChild("associationEnds").getChildren("actionEnd").iterator();
		}
		
		else if(association.getName().equals("objectAssociation")){
			associationEnds = association.getChild("associationEnds").getChildren("objectAssociationEnd").iterator();
		}
		
		else{
			associationEnds = association.getChild("associationEnds").getChildren("associationEnd").iterator();
		}			
				
		while(associationEnds.hasNext()){
			Element source = (Element)associationEnds.next();			
			sourceCell = this.getCell(source.getAttributeValue("element-id"), source.getAttributeValue("element"));
			
			Element target = (Element)associationEnds.next();			
			targetCell = this.getCell(target.getAttributeValue("element-id"), target.getAttributeValue("element"));
		}
		
		if(sourceCell != null && targetCell != null){			
			if(association.getName().equals("classAssociation")){
				graphAssociation = new ClassAssociation(association, reference, sourceCell, targetCell);
			}
			
			else if(association.getName().equals("useCaseAssociation")){				
				graphAssociation = new UseCaseAssociation(association, sourceCell, targetCell);											
			}
			
			else if(association.getName().equals("dependency")){				
				graphAssociation = new Dependency(association, sourceCell, targetCell);											
			}
			
			else if (association.getName().equals("action")){
				graphAssociation = new ActionAssociation(association, reference, sourceCell, targetCell);								
			}
			
			else if (association.getName().equals("objectAssociation")){
				graphAssociation = new ObjectAssociation(association, reference, sourceCell, targetCell);
			}
		}
		if (graphAssociation != null){
			graphAssociation.setRoutePoints();
			repaintElement(graphAssociation);
			getGraphLayoutCache().insert(graphAssociation);
		}		
		
	}
	
	public BasicCell getCell(String id, String type){
		GraphModel model = this.getModel();
		BasicCell cell = null;
		BasicCell current = null;
		for(int i = 0; i < model.getRootCount(); i++){
			Object currentObject = model.getRootAt(i);
			if (currentObject instanceof ActorCell ||
				currentObject instanceof UseCaseCell ||
				currentObject instanceof ClassCell ||
				currentObject instanceof StateCell ||
				currentObject instanceof InitialStateCell ||
				currentObject instanceof FinalStateCell ||
				currentObject instanceof ObjectCell){
				
				current = (BasicCell) currentObject;
				Element data = current.getData();
				if (id.equals(data.getAttributeValue("id"))
						&& type.equals(data.getName())){
					cell = current;
					break;
				}
			}
			else if(currentObject instanceof PlaceCell ||
				currentObject instanceof TransitionCell){
				
				current = (BasicCell) currentObject;
				Element data = current.getData();
				
				if (id.equals(data.getAttributeValue("id"))){
					cell = current;
					break;
				}
			}
		}	
		return cell;
	}
	
	public BasicCell getCellFromData(Element data){
		GraphModel model = this.getModel();
		BasicCell cell = null;
		BasicCell current = null;
		for(int i = 0; i < model.getRootCount(); i++){			
			Object currentObject = model.getRootAt(i);
			if (currentObject instanceof ActorCell ||
				currentObject instanceof UseCaseCell ||
				currentObject instanceof ClassCell ||
				currentObject instanceof StateCell ||
				currentObject instanceof ObjectCell ||
				currentObject instanceof PlaceCell ||
				currentObject instanceof TransitionCell ||
				currentObject instanceof GraphLabel){
				
				current = (BasicCell) currentObject;				
				if (current.getData().equals(data)){
					cell = current;
					break;
				}
			}			
		}
		return cell;
	}
	
	public BasicAssociation getAssociationFromData(Element data){
		GraphModel model = this.getModel();
		BasicAssociation association = null;
		BasicAssociation current = null;
		for(int i = 0; i < model.getRootCount(); i++){			
			Object currentObject = model.getRootAt(i);
			if (currentObject instanceof ActionAssociation ||
				currentObject instanceof ClassAssociation ||
				currentObject instanceof Dependency ||
				currentObject instanceof Generalization ||
				currentObject instanceof ObjectAssociation ||
				currentObject instanceof UseCaseAssociation){
				
				current = (BasicAssociation) currentObject;				
				if (current.getData().equals(data)){
					association = current;
					break;
				}
			}			
		}
		return association;
	}
	
	public Element getElement(Element project, String group, String id){
		Element element = null;
		Iterator<?> elements = project.getChild("elements").getChild(group).getChildren().iterator();
		while(elements.hasNext()){
			Element currentElement = (Element)elements.next();
			if (currentElement.getAttributeValue("id").equals(id)){
				element = currentElement;
				break;
			}	
		}
		
		return element;		
	}
	
	
	
	
	// delete action in the popup menu
	private Action deleteAction = new AbstractAction("Delete from diagram", new ImageIcon("resources/images/delete.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -228705252976758558L;

		public void actionPerformed(ActionEvent e) {
			
			Object[] cells = ItGraph.this.getSelectionCells();		
			if(cells.length > 0){
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

				
			}
		}
	};


	// delete from model element action in the popup menu
	private Action deleteFromModelAction = new AbstractAction("Delete from model", new ImageIcon("resources/images/delete.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -228705252976758558L;

		public void actionPerformed(ActionEvent e) {
			
			Object[] cells = ItGraph.this.getSelectionCells();		
			if(cells.length > 0){
				List<Object> deletingCells = new ArrayList<Object>();
				BasicAssociation selectedAssociation = null;
				
				//1. Delete all associations
				for(int i = 0; i < cells.length; i++){
					Object cell = cells[i];
					
					if (cell instanceof ObjectCell ||
							cell instanceof ClassCell  ||
							cell instanceof EnumerationCell ||
							cell instanceof ActorCell ||
							cell instanceof UseCaseCell ||							
							cell instanceof StateCell ||
							cell instanceof InitialStateCell ||
							cell instanceof FinalStateCell){
						// Add cells to a list
						deletingCells.add(cell);
					}
					else if(cell instanceof  ClassAssociation){
						selectedAssociation = (BasicAssociation)cell;
						deleteElement(selectedAssociation.getData(), selectedAssociation.getReference(), null, false);
						selectedAssociation.setData(null);
					}
					else if(cell instanceof UseCaseAssociation ||
							cell instanceof Dependency ||
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
					BasicCell cell = (BasicCell) iter.next();					
					if (cell instanceof ObjectCell ||
							cell instanceof ClassCell ||
                                                        cell instanceof EnumerationCell){
						deleteElement(cell.getData(), null, null, true);
						cell.setData(null);
					}
					//if (cell instanceof ObjectCell ||
					//		cell instanceof EnumerationCell){
					//	deleteElement(cell.getData(), null, null, true);
					//	cell.setData(null);
					//}
					else if (cell instanceof ActorCell ||
							cell instanceof UseCaseCell ||							
							cell instanceof StateCell ||
							cell instanceof InitialStateCell ||
							cell instanceof FinalStateCell){
						deleteElement(cell.getData(), cell.getReference(), cell.getAdditionalData(), false);
						cell.setData(null);
					}					
					
				}				
				
				
				ItGraph.this.getGraphLayoutCache().remove(cells, true, true);
				
			}
		}
	};
	
	// save image action in the popup menu
	private Action saveImage = new AbstractAction("Save diagram image", new ImageIcon("resources/images/image.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -228705252976758558L;

		public void actionPerformed(ActionEvent e) {

			JFileChooser fc = new JFileChooser("C:/");
			fc.setDialogTitle("Save Diagram Image As");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);					
			fc.setFileFilter(new PNGFileFilter());
			
			int returnVal = fc.showSaveDialog(ItGraph.this);
			if (returnVal == JFileChooser.APPROVE_OPTION){						
				File file = fc.getSelectedFile();

				if (!file.getName().toLowerCase().endsWith(".png")){
					String filePath = file.getPath()+".png";
					file = new File(filePath);
				}
				
				Color bg = null;
				bg = ItGraph.this.getBackground();
				BufferedImage img = ItGraph.this.getImage(bg,1);
				
				try {
					ImageIO.write(img,"png",file); //bmp, jpg, png
				} catch (IOException e1) {
					e1.printStackTrace();
				}								
			}
			
		}
	};
	
	// copy
	private Action copyElement = new AbstractAction("Copy"){

		/**
		 * 
		 */
		private static final long serialVersionUID = -4758405455667171543L;

		@SuppressWarnings("unchecked")
		public void actionPerformed(ActionEvent arg0) {
			ItSIMPLE.getCopyPaste().clear();
			ItSIMPLE.getCopyPasteSenders().clear();
			Object[] selected = ItGraph.this.getSelectionCells();
			for (int i = 0; i < selected.length; i++){
				ItSIMPLE.getCopyPaste().add(selected[i]);				
				ItSIMPLE.getCopyPasteSenders().add(ItGraph.this);
			}
		}
		
	};
	
	// paste
	private Action pasteElement = new AbstractAction("Paste"){
		/**
		 * 
		 */
		private static final long serialVersionUID = 7952171003069089212L;

		public void actionPerformed(ActionEvent arg0) {
			if(ItSIMPLE.getCopyPaste().size() > 0){
				ArrayList<Object> associations = new ArrayList<Object>();
				for (int i = 0; i < ItSIMPLE.getCopyPaste().size(); i++){
					Object cell = ItSIMPLE.getCopyPaste().get(i);
					Object sender = ItSIMPLE.getCopyPasteSenders().get(i);
					if (sender != ItGraph.this) {
						if (cell instanceof ObjectCell){
							ObjectCell objectCell = (ObjectCell)cell;
							Element elementData = objectCell.getData();
							Element reference = (Element)objectCell.getReference().clone();
							Element itsClass = objectCell.getAdditionalData();
							BasicCell existingObject = ItGraph.this.getCellFromData(elementData);
							if (existingObject == null) {
								//add this object to the diagram
								diagram.getChild("objects").addContent(reference);
								createGraphElement(elementData,reference,itsClass);
								//graph.getGraphLayoutCache().insert(cell);						
							} else{
								JOptionPane.showMessageDialog(ItGraph.this,
										"<html><center>The element "+cell.toString()+" being pasted already exists in this diagram.</center></html>",
										"Element Already exists",
										JOptionPane.WARNING_MESSAGE);	
							}
						} else if (cell instanceof ObjectAssociation) {
							associations.add(cell);
						}
					} 
				}
				for (int i = 0; i < associations.size(); i++){
					ObjectAssociation objectAssociation = (ObjectAssociation)associations.get(i);
					Element elementData = (Element)objectAssociation.getData().clone();
					Element reference =objectAssociation.getReference();					
					BasicAssociation exObjectAssociation = ItGraph.this.getAssociationFromData(elementData);
					if (exObjectAssociation == null) {
						// add the association
						diagram.getChild("associations").addContent(elementData);						
						createGraphAssociation(elementData, reference);
						
					} else {
						JOptionPane.showMessageDialog(ItGraph.this,
								"<html><center>The element "+objectAssociation.toString()+" being pasted already exists in this diagram.</center></html>",
								"Element Already exists",
								JOptionPane.WARNING_MESSAGE);
					}
					
					//createGraphAssociation(reference, association);
				}
			}						
		}
		
	};
	
	// create the popup menu and adds the actions
	public JPopupMenu getPopupMenu(Object cell){
		if (popupMenu == null){
			popupMenu = new JPopupMenu();
		}
		else{
			popupMenu.removeAll();
			if (cell == null){
				popupMenu.add(saveImage);
				popupMenu.addSeparator();
				JMenuItem paste = popupMenu.add(pasteElement);
				if (ItSIMPLE.getCopyPaste().size() == 0){
					paste.setEnabled(false);
				}
			}
			
			else if(cell instanceof ClassCell){
				
				// delete item				
				JMenuItem delete = new JMenuItem(deleteAction);
				delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Event.CTRL_MASK, true));
				popupMenu.add(delete);				
				
				// delete from model item
				 JMenuItem deleteFromModel = new JMenuItem(deleteFromModelAction);
				deleteFromModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true));	
				popupMenu.add(deleteFromModel);
			}
			else if(cell instanceof EnumerationCell){

				// delete item
				JMenuItem delete = new JMenuItem(deleteAction);
				delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Event.CTRL_MASK, true));
				popupMenu.add(delete);

				// delete from model item
				 JMenuItem deleteFromModel = new JMenuItem(deleteFromModelAction);
				deleteFromModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true));
				popupMenu.add(deleteFromModel);
			}
			
			else if(cell instanceof ObjectCell){		
								
				// delete item
				if(diagram.getName().equals("repositoryDiagram")){
					// delete from model item
					 JMenuItem deleteFromModel = new JMenuItem(deleteFromModelAction);
					deleteFromModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true));	
					popupMenu.add(deleteFromModel);					
				}			
				else{
					JMenuItem delete = new JMenuItem(deleteAction);
					delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0, true));
					popupMenu.add(delete);
					
					// delete from model item
					/* JMenuItem deleteFromModel = new JMenuItem(deleteFromModelAction);
					deleteFromModel.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Event.CTRL_MASK, true));	
					popupMenu.add(deleteFromModel);	*/
				}
			

				popupMenu.addSeparator();
				
				// copy item
				JMenuItem copy = new JMenuItem(copyElement);
				copy.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, Event.CTRL_MASK, true));
				popupMenu.add(copy);
				
				// paste item
				JMenuItem paste = new JMenuItem(pasteElement); 
				paste.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, Event.CTRL_MASK, true));
				popupMenu.add(paste);
				if (ItSIMPLE.getCopyPaste().size() == 0){
					paste.setEnabled(false);
				}

			}
			else if(cell instanceof ActorCell ||
					cell instanceof UseCaseCell ||
					cell instanceof StateCell ||
					cell instanceof Dependency ||							
					cell instanceof ClassAssociation ||
					cell instanceof ObjectAssociation ||
					cell instanceof ActionAssociation ||
					cell instanceof Generalization){
				
				// delete item
				JMenuItem delete = new JMenuItem(deleteAction);
				delete.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, Event.CTRL_MASK, true));
				popupMenu.add(delete);
			}
		}
		
		return popupMenu;
	}
	
	private void deleteElement(Element data, Element reference, Element additional, boolean deleteFromModel){
		String name = data.getName();
		ItTree tree = ItSIMPLE.getInstance().getItTree();
		ItTreeNode projectNode = tree.findProjectNode(project);
		if (name.equals("actor") || name.equals("useCase") || name.equals("state") || name.equals("initialState") || name.equals("finalState")){
			
			// deleting useCaseAssociations to this actor
			
			Iterator<?> associations = diagram.getChild("associations").getChildren().iterator();
			// adds the elements to be deleted so the hasNext method doesn`t lose its reference
			ArrayList<Element> listData = new ArrayList<Element>();
			
			while(associations.hasNext()){
				Element association = (Element)associations.next();				
				Iterator<?> associationEnds = association.getChild("associationEnds").getChildren().iterator();
				while(associationEnds.hasNext()){
					Element associationEnd = (Element)associationEnds.next();
					if (associationEnd.getAttributeValue("element").equals(name)
							&& associationEnd.getAttributeValue("element-id").equals(data.getAttributeValue("id"))){						
						listData.add(association);
						break;
					}
				}
			}
			
			if (listData.size() > 0){				
				for (int i = 0; i < listData.size(); i++){					
					deleteElement(listData.get(i), null, null, false);
				}
			}
			// delete elements nodes from tree
			if(projectNode != null){
				tree.deleteTreeNodeFromData(projectNode, data);
			}
			
			// deleting element
			Element parent = data.getParentElement();
			parent.removeContent(data);
		}

		else if (name.equals("class")){
			if (deleteFromModel){
				//1. Delete all reference of generalization
				Iterator<?> classes = project.getChild("elements").getChild("classes").getChildren("class").iterator();
				while(classes.hasNext()){
					Element Class = (Element)classes.next();
					if (!Class.getChildText("type").equals("Primitive")){						
						if(Class.getChild("generalization").getAttributeValue("element").equals(name) &&
						Class.getChild("generalization").getAttributeValue("element").equals(data.getAttributeValue("id"))){
						
							Class.getChild("generalization").setAttribute("element","");
							Class.getChild("generalization").setAttribute("id","");
						}
					}								
				}			
							
				//2. Delete all reference on Class diagrams
				Iterator<?> classDiagrams = project.getChild("diagrams").getChild("classDiagrams").getChildren().iterator();
				while(classDiagrams.hasNext()){
					Element classDiagram = (Element)classDiagrams.next();
					
					//2.1 Checking class references
					Element classReference = XMLUtilities.getElement(classDiagram.getChild("classes"),data.getAttributeValue("id"));
					if (classReference != null) {
						//deleting class reference
						classDiagram.getChild("classes").removeContent(classReference);
					}
					
					//2.2 Checking association class references		
					Iterator<?> associations = classDiagram.getChild("associations").getChildren().iterator();				
					ArrayList<Element> listReference = new ArrayList<Element>();
					ArrayList<Element> listData = new ArrayList<Element>();	
					while(associations.hasNext()){
						Element association = (Element)associations.next();	
						Element classAssociation = XMLUtilities.getElement(project.getChild("elements").getChild("classAssociations"),association.getAttributeValue("id"));					
						if (classAssociation != null){
							Iterator<?> associationEnds = classAssociation.getChild("associationEnds").getChildren().iterator();					
							while(associationEnds.hasNext()){
								Element associationEnd = (Element)associationEnds.next();
								if (associationEnd.getAttributeValue("element").equals(name)
										&& associationEnd.getAttributeValue("element-id").equals(data.getAttributeValue("id"))){						
									listReference.add(association);
									listData.add(classAssociation);
									break;
								}
							}					
				
						}				
					}
					
					//2.3 delete association reference and data
					Element parent = classDiagram.getChild("associations");
					if (listReference.size() > 0){				
						for (int i = 0; i < listReference.size(); i++){
							parent.removeContent(listReference.get(i));
							deleteElement(listData.get(i), null, null, true);
						}				
					}				
				}
			
				//3.  Delete references from StateMachine diagrams
				Iterator<?> stateMachineDiagrams = project.getChild("diagrams").getChild("stateMachineDiagrams").getChildren().iterator();
				while(stateMachineDiagrams.hasNext()){
					//3.1 Delete reference from stateMachine diagram at node "class"
					Element stateMachineDiagram = (Element)stateMachineDiagrams.next();
					if (stateMachineDiagram.getChildText("class").equals(data.getAttributeValue("id"))){
						stateMachineDiagram.getChild("class").setText("");
					}
					
					//3.1 Delete reference actionAssociations				
					Iterator<?> associations = stateMachineDiagram.getChild("associations").getChildren().iterator();
					while(associations.hasNext()){
						Element action = (Element)associations.next();
						if (action.getChild("reference").getAttributeValue("class").equals(data.getAttributeValue("id"))){						
							action.getChild("reference").setAttribute("class","");
							action.getChild("reference").setAttribute("operator","");
						}
					}				
				}						

				
				
				//4.  Delete all reference on Objects
				List<?> result = null;
				try {
					XPath path = new JDOMXPath("project/diagrams/planningDomains/domain/elements/objects/object[class='"+data.getAttributeValue("id")+"']");
					result = path.selectNodes(data.getDocument());
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				
				for (int i = 0; i < result.size(); i++){
					Element object = (Element)result.get(i);
											//objects			 elements			domain
					Element domain = object.getParentElement().getParentElement().getParentElement();
					
					// 4.1 Find all object references and associations and delete them
					List<?> resultReferences = null;
					try {
						XPath path = new JDOMXPath("repositoryDiagrams/repositoryDiagram/objects/object[@id='" +object.getAttributeValue("id")+"'] | " +
								"planningProblems/problem/objectDiagrams/objectDiagram/objects/object[@id='" +object.getAttributeValue("id")+"'] | " +
								"repositoryDiagrams/repositoryDiagram/associations/objectAssociation[associationEnds/objectAssociationEnd/@element-id='"+object.getAttributeValue("id")+"'] | "+
								"planningProblems/problem/objectDiagrams/objectDiagram/associations/objectAssociation[associationEnds/objectAssociationEnd/@element-id='"+object.getAttributeValue("id")+"']");
						resultReferences = path.selectNodes(domain);
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					for (int r = 0; r < resultReferences.size(); r++){
						Element referenceElement = (Element)resultReferences.get(r);
						
						// 4.1.1 object references - delete it
						if (referenceElement.getName().equals("object")){
							referenceElement.getChild("attributes").removeContent();
							
						}
						//4.1.2 object association references - delete it
						else if(referenceElement.getName().equals("objectAssociation")){
							Element associations = referenceElement.getParentElement();
							associations.removeContent(referenceElement);
						}
					}
					object.getChild("class").setText("");
				}
				
//				 delete elements nodes from tree
				if(projectNode != null){
					tree.deleteTreeNodeFromData(projectNode, data);
				}
						
				//5. deleting element
				Element parent = data.getParentElement();
				parent.removeContent(data);	
				
			} else{
				// checks if this is the only reference in the model
				List<?> result = null;
				try {
					XPath path = new JDOMXPath("classDiagram/classes/class[@id='" + reference.getAttributeValue("id") + "']");
					result = path.selectNodes(diagram.getParentElement());
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if (result.size() > 1){
					// delete only the reference
						
					// deleting classAssociations to this class			
					Iterator<?> associations = diagram.getChild("associations").getChildren().iterator();
					// adds the elements to be deleted so the hasNext method doesn`t lose its reference
					ArrayList<Element> listReference = new ArrayList<Element>();
					ArrayList<Element> listData = new ArrayList<Element>();	
					while(associations.hasNext()){
						Element association = (Element)associations.next();	
						Element classAssociation = XMLUtilities.getElement(project.getChild("elements").getChild("classAssociations"),association.getAttributeValue("id"));
						if (classAssociation != null){
							Iterator<?> associationEnds = classAssociation.getChild("associationEnds").getChildren().iterator();					
							while(associationEnds.hasNext()){
								Element associationEnd = (Element)associationEnds.next();
								if (associationEnd.getAttributeValue("element").equals(name)
										&& associationEnd.getAttributeValue("element-id").equals(data.getAttributeValue("id"))){						
									listReference.add(association);
									listData.add(classAssociation);
									break;
								}
							}					
						}
	
					}
					
					if (listReference.size() > 0){				
						for (int i = 0; i < listReference.size(); i++){					
							deleteElement(listData.get(i), listReference.get(i), null, false);
						}
					}
					
					//delete elements nodes from tree
					if(projectNode != null){
						tree.deleteTreeNodeFromReference(projectNode, data);
					}
				
					// deleting element
					Element parent = reference.getParentElement();
					parent.removeContent(reference);
				
				} else{
					//delete elements nodes from tree
					if(projectNode != null){
						tree.deleteTreeNodeFromData(projectNode, data);
					}
					
					//delete from model as well
					deleteElement(data, null, null, true);		
				}
			}		
		}
                else if (name.equals("enumeration")){

                                //1. Delete all reference on Class diagrams
				Iterator<?> classDiagrams = project.getChild("diagrams").getChild("classDiagrams").getChildren().iterator();
				while(classDiagrams.hasNext()){
					Element classDiagram = (Element)classDiagrams.next();

					//2.1 Checking class references
					Element classReference = XMLUtilities.getElement(classDiagram.getChild("classes"),data.getAttributeValue("id"));
					if (classReference != null) {
						//deleting class reference
						classDiagram.getChild("classes").removeContent(classReference);
					}

				}

//				 delete elements nodes from tree
				if(projectNode != null){
					tree.deleteTreeNodeFromData(projectNode, data);
				}

				//5. deleting element
				Element parent = data.getParentElement();
				parent.removeContent(data);

                }
		else if (name.equals("object")){
                    if (deleteFromModel){

                            Element domain = null;

                            if (diagram.getName().equals("objectDiagram")){
                                                                    //objectDiagrams	problem		  	    planningProblems	domain
                                    domain = diagram.getParentElement().getParentElement().getParentElement().getParentElement();

                            }else if (diagram.getName().equals("repositoryDiagram")){
                                                                    //repositoryDiagrams	domain
                                    domain = diagram.getParentElement().getParentElement();
                            }

                            //1.1 Get all objects and associations from the domain
                            List<?> result = null;
                            try {
                                    XPath path = new JDOMXPath("repositoryDiagrams/repositoryDiagram/objects/object[@id='" +data.getAttributeValue("id")+"'] | " +
                                                    "planningProblems/problem/objectDiagrams/objectDiagram/objects/object[@id='" +data.getAttributeValue("id")+"'] | " +
                                                    "repositoryDiagrams/repositoryDiagram/associations/objectAssociation[associationEnds/objectAssociationEnd/@element-id='"+data.getAttributeValue("id")+"'] | "+
                                                    "planningProblems/problem/objectDiagrams/objectDiagram/associations/objectAssociation[associationEnds/objectAssociationEnd/@element-id='"+data.getAttributeValue("id")+"']");
                                    result = path.selectNodes(domain);
                            } catch (JaxenException e2) {
                                    e2.printStackTrace();
                            }

                            //1.2 delete all references
                            for (int i = 0; i < result.size(); i++){
                                    Element element = (Element)result.get(i);
                                    Element parent = element.getParentElement();
                                    if (parent != null){
    //					 delete elements nodes from tree
                                            if(projectNode != null && element.getName().equals("object")){
                                                    tree.deleteTreeNodeFromReference(projectNode, element);
                                            }

                                            parent.removeContent(element);
                                    }

                            }

    //			 delete elements nodes from tree
                            if(projectNode != null){
                                    tree.deleteTreeNodeFromData(projectNode, data);
                            }

                            // deleting element
                            Element parent = data.getParentElement();
                            parent.removeContent(data);

                    }
                    else{
			// delete only the object references
			
			// deleting classAssociations to this class			
			Iterator<?> associations = diagram.getChild("associations").getChildren().iterator();
			
			// adds the elements to be deleted so the hasNext method doesn`t lose its reference
			//ArrayList listReference = new ArrayList();
			ArrayList<Element> listData = new ArrayList<Element>();	
			while(associations.hasNext()){
				Element association = (Element)associations.next();				
				Iterator<?> associationEnds = association.getChild("associationEnds").getChildren().iterator();
				while(associationEnds.hasNext()){
					Element associationEnd = (Element)associationEnds.next();
					if (associationEnd.getAttributeValue("element").equals(name)
							&& associationEnd.getAttributeValue("element-id").equals(data.getAttributeValue("id"))){						
						listData.add(association);
						break;
					}
				}
			}
			
			if (listData.size() > 0){				
				for (int i = 0; i < listData.size(); i++){
					deleteElement(listData.get(i), null, null, false);
				}
			}
			// delete elements nodes from tree
			if(projectNode != null){
				tree.deleteTreeNodeFromReference(projectNode, reference);
			}
		
			// deleting element
			Element parent = reference.getParentElement();
			parent.removeContent(reference);

			}			

		}			
		
		else if(name.equals("useCaseAssociation") || name.equals("dependency") ){
			// deleting element
			Element parent = data.getParentElement();
			parent.removeContent(data);			
		}
		else if(name.equals("classAssociation")){
			if (deleteFromModel){
				//1. clean up all object reference attributes
				List<?> result = null;
				try {
					XPath path = new JDOMXPath("project/diagrams/planningDomains/domain/planningProblems/problem/objectDiagrams/objectDiagram/associations/objectAssociation[classAssociation="+data.getAttributeValue("id")+"] | " +
							"project/diagrams/planningDomains/domain/repositoryDiagrams/repositoryDiagram/associations/objectAssociation[classAssociation="+data.getAttributeValue("id")+"]");
					result = path.selectNodes(data.getDocument());
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				
				for (int i = 0; i < result.size(); i++){
					Element objectAssociation = (Element)result.get(i);
					Element associations = objectAssociation.getParentElement();
					associations.removeContent(objectAssociation);
				}				
				
				// deleting element	
				Element parent = data.getParentElement();
				parent.removeContent(data);		
			
			}else{
				// checks if this is the only reference in the model
				List<?> result = null;
				try {
					XPath path = new JDOMXPath("classDiagram/associations/classAssociation[@id='"
							+ reference.getAttributeValue("id") + "']");
					result = path.selectNodes(diagram.getParentElement());
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				
				// delete local reference
				Element parent = reference.getParentElement();
				parent.removeContent(reference);				
				
				if (result.size() == 1){					
					// delete from model as well
					deleteElement(data, null, null, true);
				}


			}
		}	
		else if(name.equals("action")){
			// deleting element				
			Element parent = data.getParentElement();
			parent.removeContent(data);		
		}
		else if(name.equals("objectAssociation") ){
			// deleting element
			Element parent = data.getParentElement();
			parent.removeContent(data);			
		}
		else if(name.equals("generalization")){			
			// 1. Gel all class ascendents
			List<?> ascendents = XMLUtilities.getClassAscendents(data.getParentElement());
			List<?> descendents = XMLUtilities.getClassDescendents(data.getParentElement());
			
			// create the queries for xpath
			String descendentsQuery = "";
			String ascendentsQuery = "";
			String associationsQuery = "";
			
			for (Iterator<?> iter = descendents.iterator(); iter.hasNext();) {
				Element descendent = (Element) iter.next();
				String each = "";
				each = "class='" + descendent.getAttributeValue("id") + "'";
				if (iter.hasNext()){
					each = each + " or ";
				}
				descendentsQuery = descendentsQuery + each;
			}
			if (descendentsQuery.equals(""))
				descendentsQuery = "class='" + data.getParentElement().getAttributeValue("id") + "'";			
			else
				descendentsQuery = descendentsQuery + " or class='" + data.getParentElement().getAttributeValue("id") + "'";
			
			for (Iterator<?> iter = ascendents.iterator(); iter.hasNext();) {
				Element ascendent = (Element) iter.next();
				String each = "";
				String eachAssociation = "";
				each = "@class='" + ascendent.getAttributeValue("id") + "'";
				eachAssociation = "associationEnds/associationEnd/@element-id='" + ascendent.getAttributeValue("id") + "'";
				if (iter.hasNext()){
					each = each + " or ";
					eachAssociation = eachAssociation + " or ";
				}	
				ascendentsQuery = ascendentsQuery  + each;
				associationsQuery = associationsQuery + eachAssociation;
			}
			
			
			// 2. Clean object attributes and associations with which involve any ascendent type
			// 2.1 get all instances
			List<?> objects = null;
			
			try {
				XPath path = new JDOMXPath("project/diagrams/planningDomains/domain/elements/objects/object" +
						"[" + descendentsQuery + "]");
				objects = path.selectNodes(data.getDocument());
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			
			if (objects.size() > 0){
			
				// 2.2 get all class associations
				List<?> classAssociations = null;			
				try {
					XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation" + 
							"[" + associationsQuery + "]");	
					classAssociations = path.selectNodes(data.getDocument());
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				
				String objectAssociationsQuery = "";				
				for (Iterator<?> iter = classAssociations.iterator(); iter.hasNext();) {
					Element association = (Element) iter.next();
					String each = "";
					each = "classAssociation='" + association.getAttributeValue("id") + "'";
					if (iter.hasNext()){
						each = each + " or ";
					}
					objectAssociationsQuery = objectAssociationsQuery + each;
				}				
				
				for (Iterator<?> iter = objects.iterator(); iter.hasNext();) {
					Element object = (Element) iter.next();
					
					
					// 2.3 get all the objects references
									//		objects				elements			domain
					Element domain = object.getParentElement().getParentElement().getParentElement();

					List<?> objectReferences = null;
					try {
						XPath path = new JDOMXPath("planningProblems/problem/objectDiagrams/objectDiagram/objects/object[@id='"
								+ object.getAttributeValue("id") + "'] | repositoryDiagrams/repositoryDiagram/objects/object[@id='"
								+ object.getAttributeValue("id") + "']");
						objectReferences = path.selectNodes(domain);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					
					if (objectReferences.size() > 0){
						
						
						for (Iterator<?> iter1 = objectReferences.iterator(); iter1.hasNext();) {
							Element objectReference = (Element) iter1.next();
							// 2.3.1 for each reference, removes the attribute originally from the super class
							List<?> attributes = null;
							try {
								XPath path = new JDOMXPath("attributes/attribute[" + ascendentsQuery + "]");
								attributes = path.selectNodes(objectReference);
							} catch (JaxenException e) {			
								e.printStackTrace();
							}
							for (Iterator<?> iterator = attributes.iterator(); iterator.hasNext();) {
								Element attribute = (Element) iterator.next();
								objectReference.getChild("attributes").removeContent(attribute);
							}
							
							
						}
					}
					
					//2.4 get all object associations
					if (!objectAssociationsQuery.equals("")){
						List<?> objectAssociations = null;
						try {
							XPath path = new JDOMXPath("planningProblems/problem/objectDiagrams/objectDiagram/associations/objectAssociation[("
									+ objectAssociationsQuery + ") and (associationEnds/objectAssociationEnd/@element-id='"
									+ object.getAttributeValue("id") + "')] | repositoryDiagrams/repositoryDiagram/associations/objectAssociation[("
									+ objectAssociationsQuery + ") and (associationEnds/objectAssociationEnd/@element-id='"
									+ object.getAttributeValue("id") + "')]");
							objectAssociations = path.selectNodes(domain);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}
							
						for (Iterator<?> iter1 = objectAssociations.iterator(); iter1.hasNext();) {
							// 2.4.1 remove each association
							Element objectAssociation = (Element) iter1.next();
							
							Element associations = objectAssociation.getParentElement();
							associations.removeContent(objectAssociation);	
							
						}

					}					
				}	
			}	
			// 3. resets empty generalization			
			data.setAttribute("element","class");
			data.setAttribute("id","");
			data.getChild("name").setText("");
			data.getChild("graphics").getChild("points").removeContent();
		}
	}	
	

	public void graphChanged(GraphModelEvent e) {
		ItTree tree = ItSIMPLE.getInstance().getItTree();
		if (e.getChange().toString().indexOf("Inserted:") < 0 && e.getChange().toString().indexOf("Removed:") < 0){
			Object[] ob = e.getChange().getChanged();			
			for (int i = 0; i < ob.length; i++) {
				BasicAssociation selectedAssociation = null;
				BasicCell selectedCell = null;
				Object cell = ob[i];				
				if (cell instanceof ActorCell ||
									cell instanceof UseCaseCell ||
									cell instanceof ClassCell ||
									cell instanceof EnumerationCell ||
									cell instanceof StateCell ||
									cell instanceof InitialStateCell ||
									cell instanceof FinalStateCell ||
									cell instanceof ObjectCell){
					selectedCell = (BasicCell)cell;
					
					//1. Check name changes
					String text = (String)selectedCell.getUserObject();
					if(!selectedCell.getData().getChildText("name").equals(text)){
						

						if (cell instanceof ClassCell) {							
							//check the presence of "-" in the name
							if(text.indexOf("-") > -1){
								JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
										"<html><center>The character \"-\" " +
										"can not be used.</center></html>",
										"Not Allowed Character",
										JOptionPane.WARNING_MESSAGE);								
								selectedCell.setUserObject(selectedCell.getData().getChildText("name"));
							}
							
							else if(text.trim().equals("")){
								JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
										"<html><center>Empty names are not allowed.</center></html>",
										"Empty name",
										JOptionPane.WARNING_MESSAGE);								
								selectedCell.setUserObject(selectedCell.getData().getChildText("name"));
							}
							
							else{
								//class cells must have unique names
								boolean result = true;
								try {
									XPath path = new JDOMXPath(
											"project/elements/classes/class[lower-case(name)='"+ text.toLowerCase() 
											+"' and @id!='"+ selectedCell.getData().getAttributeValue("id") +"']");
									result = path.booleanValueOf(selectedCell.getData().getDocument());
								} catch (JaxenException e2) {			
									e2.printStackTrace();
								}
								
								if (!result){
									selectedCell.getData().getChild("name").setText(text);
									propertiesPane.setBaseName(text);
									ItTreeNode node =tree.findNodeFromData(project, selectedCell.getData());
									node.setUserObject(text);
									DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
									model.nodeChanged(node);
									
									// repaint open diagrams
                                                                        ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();
                                                                        tabbed.repaintOpenDiagrams("repositoryDiagram");
                                                                        tabbed.repaintOpenDiagrams("objectDiagram");
									
								}
								else{
									JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
											"<html><center>There is already a class named '"+
											text +"'. A class name must be unique.</center></html>",
											"Not Allowed Name",
											JOptionPane.WARNING_MESSAGE);								
									selectedCell.setUserObject(selectedCell.getData().getChildText("name"));	
								}
							}
						}
						// Object cells have unique names
						else if(cell instanceof ObjectCell){
							boolean result = true;
							
							try {
								XPath path = new JDOMXPath("object[lower-case(name)='"+ text.toLowerCase() 
										+"' and @id!='"+ selectedCell.getData().getAttributeValue("id")+ "']");
								result = path.booleanValueOf(selectedCell.getData().getParentElement());								
							} catch (JaxenException e2) {			
								e2.printStackTrace();
							}
							
							if (!result){
								selectedCell.getData().getChild("name").setText(selectedCell.getUserObject().toString());
								propertiesPane.setBaseName(text);
								ItTreeNode node = tree.findNodeFromData(project, selectedCell.getData());
								node.setUserObject(text);
								DefaultTreeModel model = (DefaultTreeModel) tree.getModel();
								model.nodeChanged(node);
								
								// repaint open diagrams
				            	ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();				            	
				            	tabbed.repaintOpenDiagrams("objectDiagram");
							}
							else{
								JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
										"<html><center>There is already an object named '"+ selectedCell.getUserObject().toString()+
										"'. An object name must be unique.</center></html>",
										"Not Allowed Name",
										JOptionPane.WARNING_MESSAGE);								
								selectedCell.setUserObject(selectedCell.getData().getChildText("name"));	
							}							
						}
						else{
							selectedCell.getData().getChild("name").setText(selectedCell.getUserObject().toString());
							propertiesPane.setBaseName(text);
							ItTreeNode node = tree.findNodeFromData(project, selectedCell.getData());
							if(node != null){
								node.setUserObject(text);
								DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
								model.nodeChanged(node);
							}
						}
									
					}
					
				
					//2. Check size changes
					Element graphics = null;			
					
					if (cell instanceof ActorCell ||
							cell instanceof UseCaseCell ||
							cell instanceof StateCell ||
							cell instanceof InitialStateCell ||
							cell instanceof FinalStateCell) {
						graphics = selectedCell.getData().getChild("graphics"); 
					}
					else if (cell instanceof ClassCell ||
							cell instanceof EnumerationCell ||
							cell instanceof ObjectCell) {
						graphics = selectedCell.getReference().getChild("graphics");
					}							
					
					String width = graphics.getChild("size").getAttributeValue("width");
					String height = graphics.getChild("size").getAttributeValue("height");
										
					String widthG = String.valueOf((int)GraphConstants.getBounds(selectedCell.getAttributes()).getWidth());
					String heightG = String.valueOf((int)GraphConstants.getBounds(selectedCell.getAttributes()).getHeight());
					
					if(!width.equals(widthG)){
						graphics.getChild("size").setAttribute("width",widthG);
					}				
					if(!height.equals(heightG)){
						graphics.getChild("size").setAttribute("height",heightG);
					}
					
					//3.  Check position changes					
					String x = graphics.getChild("position").getAttributeValue("x");
					String y = graphics.getChild("position").getAttributeValue("y");
										
					String xGposition = String.valueOf((int)GraphConstants.getBounds(selectedCell.getAttributes()).getX());
					String yGposition = String.valueOf((int)GraphConstants.getBounds(selectedCell.getAttributes()).getY());
					
					if(!x.equals(xGposition)){
						graphics.getChild("position").setAttribute("x",xGposition);
					}				
					if(!y.equals(yGposition)){
						graphics.getChild("position").setAttribute("y",yGposition);
					}
					
				}
				//associations
				else if(cell instanceof UseCaseAssociation ||
						 cell instanceof Dependency ||
						 cell instanceof ClassAssociation ||
						 cell instanceof Generalization ||
						 cell instanceof ActionAssociation ||
						 cell instanceof ObjectAssociation){
					
					selectedAssociation = (BasicAssociation)cell;
					
					// 1. Check Label position changes
					Element labels;
					if(cell instanceof ClassAssociation){
						labels = selectedAssociation.getReference().getChild("graphics").getChild("labels");
					} else{
						labels = selectedAssociation.getData().getChild("graphics").getChild("labels");
					}
					Element mainLabelPositionElement = null;
					try {
						XPath path = new JDOMXPath("label[@type='main']/position");
						mainLabelPositionElement = (Element)path.selectSingleNode(labels);
					} catch (JaxenException e1) {
						e1.printStackTrace();
					}
					Point2D mainLabelPosition = GraphConstants.getLabelPosition(selectedAssociation.getAttributes());
					if(mainLabelPosition != null && mainLabelPositionElement != null){
						String xMainLabelPosition = String.valueOf((int)mainLabelPosition.getX());
						String yMainLabelPosition = String.valueOf((int)mainLabelPosition.getY());
						if(!mainLabelPositionElement.getAttributeValue("x").equals(xMainLabelPosition) ||
								!mainLabelPositionElement.getAttributeValue("y").equals(yMainLabelPosition)){
							mainLabelPositionElement.setAttribute("x", xMainLabelPosition);
							mainLabelPositionElement.setAttribute("y", yMainLabelPosition);
							
						}
					}					
					
					if (cell instanceof UseCaseAssociation ||
							 cell instanceof Dependency ||
							 cell instanceof ClassAssociation ||
							 cell instanceof Generalization ||
							 cell instanceof ObjectAssociation){
		
						
						//1. Check Name changes
						String newName = (String)selectedAssociation.getUserObject();
						if(!(cell instanceof ObjectAssociation) &&
								!selectedAssociation.getData().getChildText("name").equals(newName)){
							
							if (cell instanceof ClassAssociation) {
								Boolean result = new Boolean(false);
								try {
									XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation/name='"+newName+"'");
									result = (Boolean)path.selectSingleNode(selectedAssociation.getData().getDocument());
								} catch (JaxenException e2) {			
									e2.printStackTrace();
								}
								
								if (!result.booleanValue()){
									selectedAssociation.getData().getChild("name").setText(newName);
									
									// repaint open diagrams
					            	ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();
					            	tabbed.repaintOpenDiagrams("repositoryDiagram");
					            	tabbed.repaintOpenDiagrams("objectDiagram");
								}
								else{
									JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
											"<html><center>There is already an association named '"+
											newName+"'. An association name must be unique.</center></html>",
											"Not Allowed Name",
											JOptionPane.WARNING_MESSAGE);								
									selectedAssociation.setUserObject(selectedAssociation.getData().getChildText("name"));	
								}	
							}
							else{
								selectedAssociation.getData().getChild("name").setText(newName);								
								
							}
							
							propertiesPane.setBaseName(newName);
							
						}				
						
						//2. check changes on multiplicities and rolenames
						// rolenames for the class association, dependency, use case association and object association
						// multiplcities for class association, dependency, use case association
						if(!(cell instanceof Generalization)){
							BasicAssociation association = (BasicAssociation)cell;
							Point2D[] points = GraphConstants.getExtraLabelPositions(association.getAttributes());
							
							if(points != null){
								// rolenames
								Element sourceRolename = null;
								Element targetRolename = null;														
								try {
									XPath path = new JDOMXPath("label[@type='rolename' and @identifier='1']/position");
									sourceRolename = (Element)path.selectSingleNode(labels);
									
									path = new JDOMXPath("label[@type='rolename' and @identifier='2']/position");
									targetRolename = (Element)path.selectSingleNode(labels);									
								} catch (JaxenException e1) {
									e1.printStackTrace();
								}
								
								if(sourceRolename != null && targetRolename != null){
									sourceRolename.setAttribute("x", String.valueOf((int)points[0].getX()));
									sourceRolename.setAttribute("y", String.valueOf((int)points[0].getY()));
									
									targetRolename.setAttribute("x", String.valueOf((int)points[1].getX()));
									targetRolename.setAttribute("y", String.valueOf((int)points[1].getY()));
								}
								
								if(!(cell instanceof ObjectAssociation)){
									// multiplicities
									Element sourceMultiplicity = null;
									Element targetMultiplicity = null;								
									try {
										XPath path = new JDOMXPath("label[@type='multiplicity' and @identifier='1']/position");
										sourceMultiplicity = (Element)path.selectSingleNode(labels);

										path = new JDOMXPath("label[@type='multiplicity' and @identifier='2']/position");
										targetMultiplicity = (Element)path.selectSingleNode(labels);									
									} catch (JaxenException e1) {
										e1.printStackTrace();
									}
									
									if(sourceMultiplicity != null && targetMultiplicity != null){										
										sourceMultiplicity.setAttribute("x", String.valueOf((int)points[2].getX()));
										sourceMultiplicity.setAttribute("y", String.valueOf((int)points[2].getY()));
										
										targetMultiplicity.setAttribute("x", String.valueOf((int)points[3].getX()));
										targetMultiplicity.setAttribute("y", String.valueOf((int)points[3].getY()));
									}
								}
								
								// changeability (only class association)
								if(cell instanceof ClassAssociation){
									Element changeability = null;
									try {
										XPath path = new JDOMXPath("label[@type='changeability']/position");
										changeability = (Element)path.selectSingleNode(labels);
									} catch (JaxenException e1) {
										e1.printStackTrace();
									}
									
									if(changeability != null){
										changeability.setAttribute("x", String.valueOf((int)points[4].getX()));
										changeability.setAttribute("y", String.valueOf((int)points[4].getY()));
									}
								}								
								
							}						

						}
						
					}				
					
					//2. Check Points changes
					List<?> points = GraphConstants.getPoints(selectedAssociation.getAttributes());					
					if (points != null){
						Element routePoints;
						if(selectedAssociation instanceof ClassAssociation){
							routePoints = selectedAssociation.getReference().getChild("graphics").getChild("points");
						}
						else{
							routePoints = selectedAssociation.getData().getChild("graphics").getChild("points");
						}						
						
						// clears all the points
						routePoints.removeChildren("point");
						// it doesn't save the first and last points (source and target)
						for (int j = 1; j < points.size()-1; j++){
							Element point = (Element)commonData.getChild("definedNodes").getChild("elements")
								.getChild("model").getChild("point").clone();
							Point2D currentPoint = (Point2D)points.get(j);
							int x = (int)currentPoint.getX();
							int y = (int)currentPoint.getY();
							point.setAttribute("x", String.valueOf(x));
							point.setAttribute("y", String.valueOf(y));
							routePoints.addContent(point);
						}						
					}
					
				}
				//place and transition
				else if(cell instanceof PlaceCell || cell instanceof TransitionCell){
					//Check position changes
					selectedCell = (BasicCell)cell;
					Element graphics = selectedCell.getData().getChild("graphics");
					String x = graphics.getChild("position").getAttributeValue("x");
					String y = graphics.getChild("position").getAttributeValue("y");
										
					String xGposition = String.valueOf((int)GraphConstants.getBounds(selectedCell.getAttributes()).getX());
					String yGposition = String.valueOf((int)GraphConstants.getBounds(selectedCell.getAttributes()).getY());
					
					if(!x.equals(xGposition)){
						graphics.getChild("position").setAttribute("x",xGposition);
					}				
					if(!y.equals(yGposition)){
						graphics.getChild("position").setAttribute("y",yGposition);
					}
				}				
				//labels
				else if(cell instanceof GraphLabel){
					GraphLabel label = (GraphLabel)cell;
					BasicCell graphCell = getCellFromData(label.getData().getParentElement());
					int xLabel = (int)GraphConstants.getBounds(label.getAttributes()).getX();
					int yLabel = (int)GraphConstants.getBounds(label.getAttributes()).getY();
					int xCell = (int)GraphConstants.getBounds(graphCell.getAttributes()).getX();
					int yCell = (int)GraphConstants.getBounds(graphCell.getAttributes()).getY();					
					int xoffset = xLabel - xCell;
					int yoffset = yLabel - yCell;
					label.getData().getChild("graphics").getChild("offset").getAttribute("x").setValue(String.valueOf(xoffset));
					label.getData().getChild("graphics").getChild("offset").getAttribute("y").setValue(String.valueOf(yoffset));					
				}
			}
		}	
	}

	public void valueChanged(GraphSelectionEvent e) {
		
		if(isVisible()){
			Object cell = e.getCells()[0];
			if(cell != null){
				if(propertiesPane != null)
					propertiesPane.showProperties(cell, this);			
				showInfoDetails(cell);
				if(cell instanceof PlaceCell || cell instanceof TransitionCell){
					BasicCell basicCell = (BasicCell) cell;
					GraphLabel label = (GraphLabel)getCellFromData(basicCell.getData().getChild("name"));					
					addSelectionCell(label);
				}
			}
		}
	}
	
	public void showInfoDetails(Object cell){
		
		//Show Action information
		if(cell instanceof ActionAssociation){
			// TODO
			//0. Get the main elements
			ActionAssociation action = (ActionAssociation)cell;	
			
			Element actionData = action.getData();
			Element actionRef  = action.getReference();
			
			String oclAction = "";			
			
			if (actionRef != null && actionData != null){
									// 			operators			class				
				Element actionClass = actionRef.getParentElement().getParentElement();	
				
				Element actionNode = OCLUtilities.buildConditions(actionRef);
				
				//4. Show action context in OCL
				String strParameters = actionNode.getChildText("parameters");
				String actionContext = actionClass.getChildText("name")+ "::" + actionRef.getChildText("name")+ "(" + strParameters + ")";
				//4.1. action header 
				oclAction = "<html><body><font size=3 face=arial color=black><i><b>context </b></i>" + actionContext+"<br>";
			
				
				//4.2. action preconditions	
				Element preconditionNode = actionNode.getChild("preconditions");
				oclAction = oclAction + "<i><b>pre: </b></i><br>";				
				
				if (preconditionNode.getChildren().size() > 0){
					Iterator<?> diagramConditions = preconditionNode.getChildren().iterator();
					while (diagramConditions.hasNext()) {
						Element conditions = (Element) diagramConditions.next();
						if (conditions.getChildren().size() > 0){
							//Show the class point of view
							oclAction = oclAction +"<i>&nbsp;&nbsp;  -- " + conditions.getAttributeValue("class") + " conditions </i><br>";							
							Iterator<?> groupConditions = conditions.getChildren().iterator();
							while (groupConditions.hasNext()) {
								Element eachCondition = (Element) groupConditions.next();
								//Show each condition
								String eachConditionText = eachCondition.getText();
								if (!eachConditionText.trim().equals("")){
									eachConditionText = eachConditionText.replaceAll("<", "&lt;");
									eachConditionText = eachConditionText.replaceAll(">", "&gt;");
									oclAction = oclAction +"&nbsp;&nbsp;&nbsp;&nbsp; " + eachConditionText;
									if(groupConditions.hasNext()){
										oclAction += " and ";
									}
									oclAction += "<br>";									
								}									
							}								
						}				
					}					
				}
				
				//4.3. action postconditions
				Element postconditionNode = actionNode.getChild("postconditions");
				oclAction = oclAction + "<br><i><b>post: </b></i><br>";
				//oclAction = oclAction + "<br><font size=3 face=arial color=black><i><b>post: </b></i></font> <br>";
				if (postconditionNode.getChildren().size() > 0){
					Iterator<?> diagramConditions = postconditionNode.getChildren().iterator();
					while (diagramConditions.hasNext()) {
						Element conditions = (Element) diagramConditions.next();
						if (conditions.getChildren().size() > 0){
							//Show the class point of view
							oclAction = oclAction +"<i>&nbsp;&nbsp;  -- " + conditions.getAttributeValue("class") + " conditions </i><br>";							
							Iterator<?> groupConditions = conditions.getChildren().iterator();
							while (groupConditions.hasNext()) {
								Element eachCondition = (Element) groupConditions.next();
								String eachConditionText = eachCondition.getText();
								//Show each condition
								if (!eachConditionText.trim().equals("")){
									eachConditionText = eachConditionText.replaceAll("<", "&lt;");
									eachConditionText = eachConditionText.replaceAll(">", "&gt;");
									oclAction = oclAction +"&nbsp;&nbsp;&nbsp;&nbsp; " + eachConditionText;									
									if(groupConditions.hasNext()){
										oclAction += " and ";
									}
									oclAction += "<br>";
								}									
							}								
						}				
					}					
				}					
				oclAction = oclAction + "</font></body></html>";			
			}			
			
			ItSIMPLE.getInstance().getInfoEditorPane().setText(oclAction);			
		}	
		
		// Show Use Case information
		
		else if (cell instanceof UseCaseCell){
			UseCaseCell usecaseCell = (UseCaseCell)cell;
			Element usecase = usecaseCell.getData();
			if(usecase != null){
				String info = getUseCaseDefinition(usecase);
				ItSIMPLE.getInstance().getInfoEditorPane().setText(info);
			}
			
		}
		
		// Show Actor information
		
		else if (cell instanceof ActorCell){
			ActorCell actorCell = (ActorCell)cell;
			String info = "";
			if(actorCell.getData() != null)
				info = getActorDefinition(actorCell.getData());		
			ItSIMPLE.getInstance().getInfoEditorPane().setText(info);
		}		
		else{
			ItSIMPLE.getInstance().getInfoEditorPane().setText("");
		}	
	}	
	
	
	public void repaintElement(DefaultGraphCell element){
		Map<DefaultGraphCell, AttributeMap> nested = new Hashtable<DefaultGraphCell, AttributeMap>();				
		nested.put(element, element.getAttributes());
		getGraphLayoutCache().edit(nested);		
	}
	
	public void repaintAllElements(){
		Object[] remove = DefaultGraphModel.getAll(getModel());
		for (Object cell : remove) {
			if(cell != null){
				if(cell instanceof ClassCell ||					
						cell instanceof ObjectCell ||
						cell instanceof TransitionCell || 
						cell instanceof PlaceCell){
						BasicCell currentCell = (BasicCell) cell;
						
						if(currentCell.getData().getParent() == null){
							// the cell was removed
							Object[] cells = {currentCell};
							getGraphLayoutCache().remove(cells, true, true);
						}
						else{
							// repaint it							
							currentCell.setVisual();
							repaintElement(currentCell);
						}

					}
					else if(cell instanceof ClassAssociation ||
							cell instanceof ObjectAssociation ||
							cell instanceof ActionAssociation){
						BasicAssociation currentAssociation = (BasicAssociation) cell;
						
						if(currentAssociation.getData().getParent() == null ||
								(currentAssociation.getReference() != null &&
										currentAssociation.getReference().getParent() == null)){
							// the cell was removed
							Object[] cells = {currentAssociation};
							getGraphLayoutCache().remove(cells, true, true);
						}
						else{
							// repaint the cells
							currentAssociation.setVisual();
							repaintElement(currentAssociation);	
						}

					}
			}
		}
	}


	public void keyPressed(KeyEvent e){
		
		 if(e.getKeyCode() == KeyEvent.VK_C && e.isControlDown()){
			 copyElement.actionPerformed(null);
		 } else if(e.getKeyCode() == KeyEvent.VK_V && e.isControlDown()){
			 pasteElement.actionPerformed(null);
		 }		 
		 else if(e.getKeyCode() == KeyEvent.VK_DELETE){
			 if(diagram.getName().equals("repositoryDiagram")){
				 deleteFromModelAction.actionPerformed(null); 
			 }
			 else if(diagram.getName().equals("objectDiagram")){
				 if(e.isControlDown()){
					 deleteFromModelAction.actionPerformed(null);     
				 }
				 else{
					 deleteAction.actionPerformed(null); 
				 } 
			 }
			 else{
				 if(e.isControlDown()){
					 deleteAction.actionPerformed(null);     
				 }
				 else{
					 deleteFromModelAction.actionPerformed(null); 
				 } 
			 }
 
		 }
			 

      }

	public void keyTyped(KeyEvent arg0) {
		//do nothing
	}

	public void keyReleased(KeyEvent arg0) {
		//do nothing
	}
	
	public String getUseCaseDefinition(Element usecase){
		//1 Get the main elements
		Element useCaseDiagram = usecase.getParentElement().getParentElement();
		
		//2 Get Associations
		List<?> useCaseAssociations = null;
		try {
			XPath path = new JDOMXPath("associations/useCaseAssociation/associationEnds/associationEnd[@element='useCase' and @element-id='"+usecase.getAttributeValue("id")+"']");
			useCaseAssociations = path.selectNodes(useCaseDiagram);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		
		//3 Get actors names
	
		String actorsNames = "";
		if(useCaseAssociations != null){
			for (Iterator<?> iter1 = useCaseAssociations.iterator(); iter1.hasNext();) {
				
				//3.1 Get association ends 
				
				Element useCaseEnd = (Element) iter1.next();
				Element associationEnds = useCaseEnd.getParentElement();
				
				//3.2 Get actor end
				
				Element actorEnd = null;
				try {
					XPath path = new JDOMXPath("associationEnd[@element='actor']");
					actorEnd = (Element) path.selectSingleNode(associationEnds);
					} catch (JaxenException e2) {			
					e2.printStackTrace();
					}
				if(actorEnd != null){
					Element actor = null;
					try {
						XPath path = new JDOMXPath("actors/actor[@id='"+actorEnd.getAttributeValue("element-id")+"']");
						actor = (Element) path.selectSingleNode(useCaseDiagram);
						} catch (JaxenException e2) {			
						e2.printStackTrace();
						}
					if(actor != null){
						if(actorsNames.equals(""))
							actorsNames = actor.getChildText("name");
						else
							actorsNames = actorsNames+", "+actor.getChildText("name");
					}
				}
			}
		}
		
		//4 Get use case name
		
		String useCaseName = usecase.getChildText("name");
		
		//5 Get use case description
		
		String useCaseDescription = usecase.getChildText("description").replaceAll("\n","<br>");
		
		//6 Get use case definitions
		
		String preconditions = "";
		String postconditions = "";
		String invariants = "";
		String temporalConstraints = "";
		String additionalInfo = "";
		String issues = "";
		// TODO Flow events
		
		Element useCaseDefinition = usecase.getChild("definition");
		
		if(useCaseDefinition != null){
			preconditions = useCaseDefinition.getChildText("precondition").replaceAll("\n","<br>");
			postconditions = useCaseDefinition.getChildText("postcondition").replaceAll("\n","<br>");
			invariants = useCaseDefinition.getChildText("invariants").replaceAll("\n","<br>");
			temporalConstraints = useCaseDefinition.getChildText("temporalConstraints").replaceAll("\n","<br>");
			additionalInfo = useCaseDefinition.getChildText("additionalIformation").replaceAll("\n","<br>");
			issues = useCaseDefinition.getChildText("issues").replaceAll("\n","<br>");
//			TODO Flow events
		}
		else {
			//commonData/definedNodes/elements/model/useCase/definition
			useCaseDefinition = (Element)commonData.getChild("definedNodes").getChild("elements").getChild("model").getChild("useCase").getChild("definition").clone();
			useCaseDefinition = usecase.addContent(useCaseDefinition);
		}
		
		String info = "<TABLE width='100%' BORDER='0' align='center'>";
		info = info+"<TR><TD bgcolor='gray'><font size=4 face=arial color='FFFFFF'><B>Use Case:<i> "+useCaseName+"</i></b></font></TD></TR>";
		info = info + "<TR></TR>";
		if(!actorsNames.equals(""))
			info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Actors:</b> "+actorsNames+"</font> </TD> </TR>";
		info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Description:</b> "+useCaseDescription+"</font> </TD> </TR>";
		if(!(preconditions.equals("")&&postconditions.equals("")&&invariants.equals("")&&temporalConstraints.equals(""))){
			info = info + "<TR></TR>";
			info = info +  "<TR><TD bgcolor='white'><font size=4 face=arial color='black'><B><i>Constraints:</i></b></font></TD></TR>";
			if(!preconditions.equals(""))
				info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Pre-conditions</b><BR> "+preconditions+"</font> </TD> </TR>";
			if(!postconditions.equals(""))
				info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Post-conditions</b><BR> "+postconditions+"</font> </TD> </TR>";
			if(!invariants.equals(""))
				info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Invariants</b><BR> "+invariants+"</font> </TD> </TR>";
			if(!temporalConstraints.equals(""))
				info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Temporal Constraints</b><BR> "+temporalConstraints+"</font> </TD> </TR>";
		}
		
		//TODO Flow of events
		//info = info + "<TR><TD bgcolor='black'><font size=5 face=arial color='FFFFFF'><B>Flow of events</b></font></TD></TR>";
		//info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Basic Flow:</b><i> Sequence of interactions between actors and system required to achieve goal</i></font> </TD> </TR>";
		//info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Alternative Flows:</b><i> Any alternative flows of events which may occur, including failure situations</i></font> </TD> </TR>";
		if(!(additionalInfo.equals("")&&issues.equals(""))){
			info = info + "<TR></TR>";
			info = info + "<TR><TD bgcolor='white'><font size=4 face=arial color='black'><B><i>Further Details</i></b></font></TD></TR>";
			if(!additionalInfo.equals(""))
				info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Additional Information</b><BR> "+additionalInfo+" </font> </TD> </TR>";
			if(!issues.equals(""))
				info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Issues:</b><BR>"+issues+"</font> </TD> </TR>";
		}
		info = info + "</TABLE>";
		
		return info;
	}

	public String getActorDefinition(Element actorCell){

		//1 Get the main elements
		Element useCaseDiagram = actorCell.getParentElement().getParentElement();
		
		//2 Get Associations
		List<?> useCaseAssociations = null;
		try {
			XPath path = new JDOMXPath("associations/useCaseAssociation/associationEnds/associationEnd[@element='actor' and @element-id='"+actorCell.getAttributeValue("id")+"']");
			useCaseAssociations = path.selectNodes(useCaseDiagram);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		
		//3 Get use case names
	
		String useCaseNames = "";
		if(useCaseAssociations != null){
			for (Iterator<?> iter1 = useCaseAssociations.iterator(); iter1.hasNext();) {
				
				//3.1 Get association ends 
				
				Element actorEnd = (Element) iter1.next();
				Element associationEnds = actorEnd.getParentElement();
				
				//3.2 Get actor end
				
				Element useCaseEnd = null;
				try {
					XPath path = new JDOMXPath("associationEnd[@element='useCase']");
					useCaseEnd = (Element) path.selectSingleNode(associationEnds);
					} catch (JaxenException e2) {			
					e2.printStackTrace();
					}
				if(useCaseEnd != null){
					Element actor = null;
					try {
						XPath path = new JDOMXPath("useCases/useCase[@id='"+useCaseEnd.getAttributeValue("element-id")+"']");
						actor = (Element) path.selectSingleNode(useCaseDiagram);
						} catch (JaxenException e2) {			
						e2.printStackTrace();
						}
					if(actor != null){
						if(useCaseNames.equals(""))
							useCaseNames = "'"+actor.getChildText("name")+"'";
						else
							useCaseNames = useCaseNames+", '"+actor.getChildText("name")+"'";
					}
				}
			}
		}
		
		//4 Get use case name
		
		String actorName = actorCell.getChildText("name");
		
		//5 Get use case description
		
		String ActorDescription = actorCell.getChildText("description").replaceAll("\n","<br>");
		
				
		String info = "<TABLE width='100%' BORDER='0' align='center'>";
		info = info+"<TR><TD bgcolor='333399'><font size=4 face=arial color='FFFFFF'><B>Actor:<i> "+actorName+"</i></b></font></TD></TR>";
		info = info + "<TR></TR>";
		if(!useCaseNames.equals(""))
			info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Use Cases:</b><i> "+useCaseNames+"</i></font> </TD> </TR>";
		info = info + "<TR> <TD> <font size=3 face=arial color='black'><b>Description:</b> "+ActorDescription+"</font> </TD> </TR>";
		info = info + "</TABLE>";
		
		return info;
	}

	public JEditorPane getInfoPane() {
		return infoPane;
	}

	public void setInfoPane(JEditorPane infoPane) {
		this.infoPane = infoPane;
	}

	public Element getAdditional() {
		return additional;
	}

	public void setAdditional(Element additional) {
		this.additional = additional;
	}		
	
}

