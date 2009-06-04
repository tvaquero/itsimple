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

import itGraph.ItGraph;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

public class AdditionalPropertiesTabbedPane extends JTabbedPane implements MouseListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -1534339490995371234L;
	
	private static AdditionalPropertiesTabbedPane instance = null;
	private Element diagram;
	private ItGraph graph;
	
	// repository panel
	private JPanel repositoryPanel = null;
	private DefaultListModel objectsListModel = null;
	private JList objectsList = null;
	
	public AdditionalPropertiesTabbedPane(){
		super();		
		setNoSelection();
		
		createRepositoryPanel();
	}
	
	public static AdditionalPropertiesTabbedPane getInstance(){
		if (instance == null){
			instance = new AdditionalPropertiesTabbedPane();
		}
		return instance;
	}
	
	
	public void setNoSelection(){
		removeAll();
		graph = null;
		diagram = null;
	}
	
	private void  createRepositoryPanel(){		
		repositoryPanel = new JPanel(new BorderLayout());		
		
		objectsListModel = new DefaultListModel();
		objectsList = new JList(objectsListModel);
		objectsList.addMouseListener(this);
		
		JToolBar toolBar = new JToolBar();
		toolBar.add(importObjects);
		
		repositoryPanel.add(new JScrollPane(objectsList), BorderLayout.CENTER);
		repositoryPanel.add(toolBar, BorderLayout.NORTH);
		
	}
	
	@SuppressWarnings("unchecked")
	public void setRepositoryPanel(){		
		objectsListModel.clear();
		
		Element[] objects = getObjectsFromRepository(diagram);
		for (int i = 0; i < objects.length; i++) {			
			objectsListModel.addElement(new ObjectsListItem(objects[i]));
		}
	}
	
	public void showAdditionalProperties(Element diagram, ItGraph graph){
		removeAll();
	
		this.graph = graph;
		this.diagram = diagram;
		
		if(diagram.getName().equals("objectDiagram")){
			// show the objects in the repository			
			setRepositoryPanel();
			addTab("Repository", repositoryPanel);
		}
	}	

	// this method returns the objects originally in the repository and
	// that can be imported in object diagrams, i. e., objects that aren't
	// already placed in the object diagram
	@SuppressWarnings("unchecked")
	private Element[] getObjectsFromRepository(Element diagram){
		List<Element> objectsList = new ArrayList<Element>();
		
		Element domain = diagram.getParentElement().getParentElement().getParentElement().getParentElement();		
		List<Element> domainObjects = domain.getChild("elements").getChild("objects").getChildren("object");
		
		for (Iterator<Element> iter = domainObjects.iterator(); iter.hasNext();) {
			Element domainObject = iter.next();
			Boolean inDiagram = null;
			try {
				XPath path = new JDOMXPath("objects/object[@id='"+ domainObject.getAttributeValue("id") +"']");
				inDiagram = (Boolean)path.booleanValueOf(diagram);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			
			if(!inDiagram.booleanValue()){
				// it's not in diagram, so it will be added to the return list
				objectsList.add(domainObject);
			}
		}
		
		Element[] objects = new Element[objectsList.size()];
		objectsList.toArray(objects);		
		
		return objects;
	}
	
	
	private Action importObjects = new AbstractAction("Import",new ImageIcon("resources/images/import.png")){

		/**
		 * 
		 */
		private static final long serialVersionUID = -1906414185343933766L;

		public void actionPerformed(ActionEvent e) {
			int[] selected = objectsList.getSelectedIndices();
			for (int i = 0; i < selected.length; i++) {
				Element object = ((ObjectsListItem)objectsListModel.getElementAt(selected[i])).data;
				
				// find the object in the repository
				Element domain = diagram.getParentElement().getParentElement().getParentElement().getParentElement();
				Element repositoryObject = null;
				try {
					XPath path = new JDOMXPath("repositoryDiagrams/repositoryDiagram/objects/object[@id='"+
							object.getAttributeValue("id") +"']");
					repositoryObject = (Element)path.selectSingleNode(domain);
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				
				if(repositoryObject != null){
					// find the object class
					Element objectClass = null;
					try {
						XPath path = new JDOMXPath("project/elements/classes/class[@id='"+ 
								object.getChildText("class") +"']");
						objectClass = (Element)path.selectSingleNode(diagram.getDocument());
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					
					if(objectClass != null){
						// add the object reference to the diagram
						Element reference = (Element)repositoryObject.clone();
						diagram.getChild("objects").addContent(reference);
						
						// create the object in the graph
						graph.createGraphElement(object, reference, objectClass);
					}
				}
			}
			// refresh the list
			setRepositoryPanel();
		}
	};
	
	// this class will be used in the objects list, so the objects of type org.jdom.Element
	// can be added directly to the list
	// the class will overwrite the toString method, so the list shows the objects name and class
	private class ObjectsListItem{
		Element data;
		
		private ObjectsListItem(Element data){
			this.data = data;
		}
		
		public String toString(){
			String str;
			
			Element objectClass = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[@id='"+ 
						data.getChildText("class") +"']");
				objectClass = (Element)path.selectSingleNode(data.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			if(objectClass != null){
				str = data.getChildText("name")+ ": " +objectClass.getChildText("name");
			}
			else{
				str = data.getChildText("name");
			}
			
			return str;
		}
	}

	public void mouseClicked(MouseEvent e) {
		if(e.getSource() == objectsList && e.getClickCount() == 2){
			importObjects.actionPerformed(null);
		}
		
	}

	public void mouseEntered(MouseEvent e) {}

	public void mouseExited(MouseEvent e) {}

	public void mousePressed(MouseEvent e) {}

	public void mouseReleased(MouseEvent e) {}


}
