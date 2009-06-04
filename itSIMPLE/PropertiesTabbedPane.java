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
import util.filefilter.IconFileFilter;
import itGraph.BasicCell;
import itGraph.ItGraph;
import itGraph.UMLElements.ActionAssociation;
import itGraph.UMLElements.ActorCell;
import itGraph.UMLElements.BasicAssociation;
import itGraph.UMLElements.ClassAssociation;
import itGraph.UMLElements.ClassCell;
import itGraph.UMLElements.Dependency;
import itGraph.UMLElements.EnumerationCell;
import itGraph.UMLElements.FinalStateCell;
import itGraph.UMLElements.Generalization;
import itGraph.UMLElements.InitialStateCell;
import itGraph.UMLElements.ObjectAssociation;
import itGraph.UMLElements.ObjectCell;
import itGraph.UMLElements.StateCell;
import itGraph.UMLElements.UseCaseAssociation;
import itGraph.UMLElements.UseCaseCell;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;


import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jgraph.graph.DefaultGraphModel;

import sourceEditor.ItHilightedDocument;



public class PropertiesTabbedPane extends JTabbedPane
implements KeyListener, ItemListener, TableModelListener, MouseListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6706692504217531980L;
	
	//Base panel
	private JPanel basePanel = null;
	private ItPanel topBasePanel = null;
	private ItPanel bottomBasePanel = null;
	private ItPanel iconPanel = null;
	private ItPanel classPanel = null;
	private JTextField nameTextField = null;
	private JTextPane descriptionTextPane = null;
	private ItComboBox iconComboBox = null;
	private ItComboBox classComboBox = null;
	
	//Base Action panel	
	private JPanel actionBasePanel = null;
	private ItComboBox operatorComboBox = null;
	private JTextPane actionDescriptionTextPane = null;
	
	//Base Object Diagram panel	
	private JTextField objectDiagramTextField = null;
	private JPanel objectDiagramBasePanel = null;
	private ItComboBox sequenceReferenceComboBox = null;
	private JTextPane objectDiagramTextPane = null;
	
	//Base Object Association panel	
	private JPanel objectAssociationBasePanel = null;
	private ItComboBox objectAssociationComboBox = null;
	private JTextPane objectAssocDescriptionTextPane = null;
	
	//Use Case Constraint Panel
	private JTabbedPane useCaseConstraintPane = null;
	private JPanel useCasePrePanel = null;
	private JPanel useCasePosPanel = null;
	private JPanel useCaseInvariantsPanel = null;
	private JPanel useCaseTemporalPanel = null;
	private JTextPane useCasePreTextPane = null;
	private JTextPane useCasePosTextPane = null;
	private JTextPane useCaseInvTextPane = null;
	private JTextPane useCaseTempTextPane = null;
	
	//Use Case Flow of Events Panel
	private JPanel useCaseFlowOfEventsPanel = null;
	
	//Use Case Further Details Panel
	private JTabbedPane useCaseFurtherDetailsPane = null;
	private JPanel useCaseInfoPanel = null;
	private JPanel useCaseIssuePanel = null;
	private JTextPane useCaseInfoTextPane = null;
	private JTextPane useCaseIssueTextPane = null;
	
	//Class Attribute panel	
	private JPanel attributesPanel = null;
	private ItPanel topAttributePanel = null;
	private ItPanel bottomAttributePanel = null;	
	private JTable attributeTable = null;	
	private JToolBar attributeToolBar = null;
	private ItComboBox attributeType = null;
	private ArrayList<Element> currentAttributes = new ArrayList<Element>();
	
	//Class Operator panel	
	private JPanel operatorsPanel = null;
	private ItPanel topOperatorPanel = null;
	private ItPanel bottomOperatorPanel = null;	
	private JToolBar operatorToolBar = null;
	private JList operatorList = null;
	private DefaultListModel operatorListModel = null;
	private ArrayList<Element> currentOperators = new ArrayList<Element>();
	
	// Additional panel
	private JPanel additionalPanel = null;		
	private ItComboBox stereotype = null;
	
	// Literals panel
	private JPanel literalsPanel = null;
	private DefaultTableModel literalsTableModel = null;
	private JTable literalsTable = null;
	private List<Element> currentLiterals = new ArrayList<Element>();
	
	//Conditions panel	
	private JPanel internalConditionsPanel = null;
	private ItPanel topConditionPanel = null;
	private ItPanel bottomConditionPanel = null;
	private JTextPane conditionTextPane = null;	
	private JLabel conditionLabel = null;
	
	//Action Pre and Post Conditions panel	
	private JPanel actionConditionsPanel = null;
	private ItPanel preConditionPanel = null;
	private ItPanel postConditionPanel = null;
	private JTextPane preConditionTextPane = null;
	private JTextPane postConditionTextPane = null;	
	private JLabel preConditionLabel = null;
	private JLabel postConditionLabel = null;	
	
	
	//Association Role A panel	
	private JPanel roleAPanel = null;
	private JLabel classALabel = null;	
	private JTextField roleNameATextField = null;	
	private ItComboBox navigationA = null;	
	private ItComboBox aggregationA = null;
	private ItComboBox multiplicityA = null;
	
	//Association Role B panel	
	private JPanel roleBPanel = null;
	private JLabel classBLabel = null;	
	private JTextField roleNameBTextField = null;	
	private ItComboBox navigationB = null;	
	private ItComboBox aggregationB = null;
	private ItComboBox multiplicityB = null;
	
	// Additional association panel
	private JPanel additionalAssociationPanel = null;		
	private ItComboBox associationChangeability = null;
		
	// Object Attributes Panel
	private JPanel objectAttributesPanel = null;
	private ItPanel objectTopAttributePanel = null;
	private ItPanel objectBottomAttributePanel = null;	
	private JTable objectAttributeTable = null;
	private ArrayList<Element> currentObjectAttributes = new ArrayList<Element>();
	private EachRowEditor objectAttributeValue = null;
	
	// Constraints panel
	private JPanel constraintsPanel = null;
	private JTextPane constraintsTextPane = null;
	
	//Metrics Panel
	private JPanel metricsPanel = null;
	private JCheckBoxList metricsList = null;
	private DefaultListModel metricsListModel = null;
	
	// Common Data
	private Element data = null;
	private Element reference = null;
	private Element additional = null;
	private Element commonData = null;
	
	private ItTreeNode selectedNode = null;
	private BasicCell selectedCell = null;
	private BasicAssociation selectedAssociation = null;
	private Object senderObject = null;
	
	private final String iconsFilePath;	
	
	private String lastSelectTab = "";
	private int lastSelectIndex = 0;
	

	public PropertiesTabbedPane() {
		super();
		iconsFilePath = ItSIMPLE.getIconsPathElement().getText();
		commonData = ItSIMPLE.getCommonData();
		setNoSelection();
		//BASE
		getBasePanel();		
		getIconComboBox();
		getclassComboBox();
		getActionBasePanel();
		getObjectAssociationBasePanel();
		getObjectDiagramBasePanel();
		
		getAttributePanel();
		getOperatorPanel();
		getAdditionalPanel();
		getLiteralsPanel();
		getInternalConditionPanel();
		getActionConditionPanel();
		getRoleAPanel();
		getRoleBPanel();
		getAdditionalAssociationPanel();
		getObjectAttributePanel();
		getConstraintsPanel();
	}
	
	private JPanel getBasePanel(){ 
			basePanel = new JPanel(new BorderLayout());
			basePanel.setPreferredSize(new Dimension(250,200));			
			
			ItPanel nameBasePanel = new ItPanel(new BorderLayout());
			bottomBasePanel = new ItPanel(new BorderLayout());
			
			topBasePanel = new ItPanel(new BorderLayout());

			
			JLabel nameLabel = new JLabel("Name ");			
			nameTextField = new JTextField(15);
			nameTextField.addKeyListener(this);
			JLabel descriptionLabel = new JLabel("Description");
			descriptionTextPane = new JTextPane();			
			descriptionTextPane.addKeyListener(this);
			JScrollPane scrollText = new JScrollPane();
			scrollText.setViewportView(descriptionTextPane);
			
			nameBasePanel.add(nameLabel, BorderLayout.WEST);
			nameBasePanel.add(nameTextField, BorderLayout.CENTER);
			topBasePanel.add(nameBasePanel, BorderLayout.NORTH);
			
			bottomBasePanel.add(descriptionLabel, BorderLayout.NORTH);
			bottomBasePanel.add(scrollText, BorderLayout.CENTER);
			
			basePanel.add(topBasePanel, BorderLayout.NORTH);
			basePanel.add(bottomBasePanel, BorderLayout.CENTER);
		
		return basePanel;
	}
	
	private void setBasePanel(){
		nameTextField.setText(data.getChildText("name"));
		descriptionTextPane.setText(data.getChildText("description"));
		
		//
		if (data.getName().equals("actor") ||
			data.getName().equals("class") ||
			data.getName().equals("state") ||
			data.getName().equals("enumeration")){
			
			iconPanel = new ItPanel(new BorderLayout());
			JLabel iconLabel = new JLabel("Icon    ");			

			iconPanel.add(iconLabel, BorderLayout.WEST);
			String iconName = data.getChild("graphics").getChildText("icon");
			boolean hasIcon = false;
			for (int i = 0; i<iconComboBox.getItemCount(); i++){			
				if (iconName.equals(iconComboBox.getDataItem(i))){					
					iconComboBox.setSelectedIndex(i);
					hasIcon = true;
					break;
				}
			}
			if (!hasIcon){
				iconComboBox.setSelectedIndex(0);
			}
			iconPanel.add(iconComboBox, BorderLayout.CENTER);
			
			topBasePanel.add(iconPanel, BorderLayout.CENTER);			
		}
		else if(data.getName().equals("object") ||
				data.getName().equals("stateMachineDiagram")){
			classPanel = new ItPanel(new BorderLayout());
			JLabel classLabel = new JLabel("Class ");			
			
			classComboBox.removeAllItems();
			
			classComboBox.addItem("Unspecified...",null);
			
			//fill out classes with no primitive classes
			List<?> result = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[type!='Primitive']");
				result = path.selectNodes(data.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			for (int i = 0; i < result.size(); i++){
				Element Class = (Element)result.get(i);
				classComboBox.addItem(Class.getChildText("name"),Class);
			}
			classComboBox.setSelectedIndex(-1);
			
			//Select the class index
			if (additional != null){
				classComboBox.setSelectedItem(additional.getChildText("name"));										
			}
			else{
				classComboBox.setSelectedIndex(0);
			}
								
			classPanel.add(classLabel, BorderLayout.WEST);			
			classPanel.add(classComboBox, BorderLayout.CENTER);			
			topBasePanel.add(classPanel, BorderLayout.CENTER);
			
		}
}	
	
	private void getIconComboBox(){
		if (iconComboBox == null){
			iconComboBox = new ItComboBox();
			ComboBoxRenderer renderer = new ComboBoxRenderer();
			iconComboBox.setRenderer(renderer);
			setIconList();
			iconComboBox.addItemListener(this);
		}
	}
	
	private void getclassComboBox(){
		if (classComboBox == null){
			classComboBox = new ItComboBox();
			classComboBox.addItemListener(this);
		}
	}
		
	private JPanel getActionBasePanel(){ 
		actionBasePanel = new JPanel(new BorderLayout());
		actionBasePanel.setPreferredSize(new Dimension(250,200));			
		
		ItPanel topPanel = new ItPanel(new BorderLayout());
		ItPanel bottomPanel = new ItPanel(new BorderLayout());
		
		
		JLabel actionLabel = new JLabel("Action ");	
		
		operatorComboBox = new ItComboBox();
		operatorComboBox.addItemListener(this);
		
		
		JLabel actionDescriptionLabel = new JLabel("Description");
		actionDescriptionTextPane = new JTextPane();			
		actionDescriptionTextPane.addKeyListener(this);
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(actionDescriptionTextPane);
		
		topPanel.add(actionLabel, BorderLayout.WEST);
		topPanel.add(operatorComboBox, BorderLayout.CENTER);

		
		bottomPanel.add(actionDescriptionLabel, BorderLayout.NORTH);
		bottomPanel.add(scrollText, BorderLayout.CENTER);
		
		actionBasePanel.add(topPanel, BorderLayout.NORTH);
		actionBasePanel.add(bottomPanel, BorderLayout.CENTER);
	
	return actionBasePanel;
}

	private void setActionBasePanel(){
		//1. Fill ou the action combobox
		operatorComboBox.removeAllItems();
		operatorComboBox.addItem("",null);		
		List<?> result = null;
		try {
			XPath path = new JDOMXPath("project/elements/classes/class/operators/operator");
			result = path.selectNodes(data.getDocument());
		} catch (JaxenException e2) {			
			e2.printStackTrace();
		}
		
		for (int i = 0; i < result.size(); i++){
			Element operator = (Element)result.get(i);
			operatorComboBox.addItem(operator.getChildText("name"),operator);
		}	
		
		//Select the operator index
		if (reference != null){
			operatorComboBox.setSelectedItem(reference.getChildText("name"));										
		}
		else{
			operatorComboBox.setSelectedIndex(0);
		}
		
		// set description pane
		actionDescriptionTextPane.setText(data.getChildText("description"));
	}	

	private void getUseCaseConstraintPane(){
		useCaseConstraintPane = new JTabbedPane(JTabbedPane.BOTTOM);
		useCaseConstraintPane.setPreferredSize(new Dimension(250,200));	
		
		getUseCasePrePanel();
		getUseCasePosPanel();
		getUseCaseInvariantsPanel();
		getUseCaseTemporalPanel();
		
		useCaseConstraintPane.addTab("", new ImageIcon("resources/images/ucpre.png"), useCasePrePanel);
		useCaseConstraintPane.addTab("", new ImageIcon("resources/images/ucpos.png"), useCasePosPanel);
		useCaseConstraintPane.addTab("", new ImageIcon("resources/images/ucinv.png"), useCaseInvariantsPanel);
		useCaseConstraintPane.addTab("", new ImageIcon("resources/images/uctemp.png"), useCaseTemporalPanel);
		
	}
	
	private void setUseCaseConstraintPane(){
		Element definition = null;
		try {
			XPath path = new JDOMXPath("definition");
			definition = (Element)path.selectSingleNode(data);
		} catch (JaxenException e2) {			
			e2.printStackTrace();
		}
		
		if(definition != null){
		
			String preConditionText = definition.getChildText("precondition");
			useCasePreTextPane.setText(preConditionText);
		
			String posConditionText = definition.getChildText("postcondition");
			useCasePosTextPane.setText(posConditionText);
		
			String invariantText = definition.getChildText("invariants");
			useCaseInvTextPane.setText(invariantText);
		
			String temporalConstraintText = definition.getChildText("temporalConstraints");
			useCaseTempTextPane.setText(temporalConstraintText);
		}
	}
	
	private void getUseCasePrePanel(){
			useCasePrePanel = new JPanel(new BorderLayout());
			useCasePrePanel.setPreferredSize(new Dimension(250,200));			
			
			ItPanel namePanel = new ItPanel(new BorderLayout());
			
			JLabel nameLabel = new JLabel("Pre-Conditions");			
			
			useCasePreTextPane = new JTextPane();			
			useCasePreTextPane.addKeyListener(this);
			JScrollPane scrollText = new JScrollPane();
			scrollText.setViewportView(useCasePreTextPane);
			
			namePanel.add(nameLabel, BorderLayout.WEST);
			
			useCasePrePanel.add(namePanel, BorderLayout.NORTH);
			useCasePrePanel.add(scrollText, BorderLayout.CENTER);
	}

	private void getUseCasePosPanel(){
		useCasePosPanel = new JPanel(new BorderLayout());
		useCasePosPanel.setPreferredSize(new Dimension(250,200));			
		
		ItPanel namePanel = new ItPanel(new BorderLayout());
		
		JLabel nameLabel = new JLabel("Post-Conditions");			
		
		useCasePosTextPane = new JTextPane();			
		useCasePosTextPane.addKeyListener(this);
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(useCasePosTextPane);
		
		namePanel.add(nameLabel, BorderLayout.WEST);
		
		useCasePosPanel.add(namePanel, BorderLayout.NORTH);
		useCasePosPanel.add(scrollText, BorderLayout.CENTER);
	}
	
	private void getUseCaseInvariantsPanel(){
		useCaseInvariantsPanel = new JPanel(new BorderLayout());
		useCaseInvariantsPanel.setPreferredSize(new Dimension(250,200));			
		
		ItPanel namePanel = new ItPanel(new BorderLayout());
		
		JLabel nameLabel = new JLabel("Invariants");			
		
		useCaseInvTextPane = new JTextPane();			
		useCaseInvTextPane.addKeyListener(this);
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(useCaseInvTextPane);
		
		namePanel.add(nameLabel, BorderLayout.WEST);
		
		useCaseInvariantsPanel.add(namePanel, BorderLayout.NORTH);
		useCaseInvariantsPanel.add(scrollText, BorderLayout.CENTER);
	}
	
	private void getUseCaseTemporalPanel(){
		useCaseTemporalPanel = new JPanel(new BorderLayout());
		useCaseTemporalPanel.setPreferredSize(new Dimension(250,200));			
		
		ItPanel namePanel = new ItPanel(new BorderLayout());
		
		JLabel nameLabel = new JLabel("Temporal Constraints");			
		
		useCaseTempTextPane = new JTextPane();			
		useCaseTempTextPane.addKeyListener(this);
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(useCaseTempTextPane);
		
		namePanel.add(nameLabel, BorderLayout.WEST);
		
		useCaseTemporalPanel.add(namePanel, BorderLayout.NORTH);
		useCaseTemporalPanel.add(scrollText, BorderLayout.CENTER);
	}
	
	private void getUseCaseFlowOfEventsPanel(){
		useCaseFlowOfEventsPanel = new JPanel(new BorderLayout());
		useCaseFlowOfEventsPanel.setPreferredSize(new Dimension(250,200));	
	}
	
	private void setUseCaseFlowOfEventsPanel(){
	}
	
	private void getUseCaseFurtherDetailsPane(){
		useCaseFurtherDetailsPane = new JTabbedPane(JTabbedPane.BOTTOM);
		useCaseFurtherDetailsPane.setPreferredSize(new Dimension(250,200));	
		
		getUseCaseInfoPanel();
		getUseCaseIssuePanel();
		
		useCaseFurtherDetailsPane.addTab("", new ImageIcon("resources/images/ucinfo.png"), useCaseInfoPanel);
		useCaseFurtherDetailsPane.addTab("", new ImageIcon("resources/images/ucissue.png"), useCaseIssuePanel);
	}
		
	private void setUseCaseFurtherDetailsPane(){
		Element definition = null;
		try {
			XPath path = new JDOMXPath("definition");
			definition = (Element)path.selectSingleNode(data);
		} catch (JaxenException e2) {			
			e2.printStackTrace();
		}
		if(definition != null){
			String preConditionText = definition.getChildText("additionalIformation");
			useCaseInfoTextPane.setText(preConditionText);
		
			String posConditionText = definition.getChildText("issues");
			useCaseIssueTextPane.setText(posConditionText);
		}
	}
	
	private void getUseCaseInfoPanel(){
		useCaseInfoPanel = new JPanel(new BorderLayout());
		useCaseInfoPanel.setPreferredSize(new Dimension(250,200));			
		
		ItPanel namePanel = new ItPanel(new BorderLayout());
		
		JLabel nameLabel = new JLabel("Additional Information");			
		
		useCaseInfoTextPane = new JTextPane();			
		useCaseInfoTextPane.addKeyListener(this);
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(useCaseInfoTextPane);
		
		namePanel.add(nameLabel, BorderLayout.WEST);
		
		useCaseInfoPanel.add(namePanel, BorderLayout.NORTH);
		useCaseInfoPanel.add(scrollText, BorderLayout.CENTER);
	}
	
	private void getUseCaseIssuePanel(){
		useCaseIssuePanel = new JPanel(new BorderLayout());
		useCaseIssuePanel.setPreferredSize(new Dimension(250,200));			
		
		ItPanel namePanel = new ItPanel(new BorderLayout());
		
		JLabel nameLabel = new JLabel("Issues");			
		
		useCaseIssueTextPane = new JTextPane();			
		useCaseIssueTextPane.addKeyListener(this);
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(useCaseIssueTextPane);
		
		namePanel.add(nameLabel, BorderLayout.WEST);
		
		useCaseIssuePanel.add(namePanel, BorderLayout.NORTH);
		useCaseIssuePanel.add(scrollText, BorderLayout.CENTER);
	}
	
	private JPanel getObjectDiagramBasePanel(){ 
		objectDiagramBasePanel = new JPanel(new BorderLayout());
		objectDiagramBasePanel.setPreferredSize(new Dimension(250,200));			
		
		ItPanel topPanel = new ItPanel(new SpringLayout());
		ItPanel bottomPanel = new ItPanel(new BorderLayout());
		
		
		
		JLabel diagramNameLabel = new JLabel("Name ");	
		objectDiagramTextField = new JTextField(15);
		objectDiagramTextField.addKeyListener(this);
		
		JLabel sequenceReferenceLabel = new JLabel("Sequence ");
		sequenceReferenceComboBox = new ItComboBox();
		sequenceReferenceComboBox.setEditable(true);
		sequenceReferenceComboBox.addItem("");
		sequenceReferenceComboBox.addItem("init");
		sequenceReferenceComboBox.addItem("goal");
		sequenceReferenceComboBox.addItem("always");
		sequenceReferenceComboBox.addItem("sometime");
		sequenceReferenceComboBox.addItem("at-most-once");
		sequenceReferenceComboBox.addItem("at-least-once");
		sequenceReferenceComboBox.addItem("never");		
		
		sequenceReferenceComboBox.addItemListener(this);
		
		
		JLabel actionDescriptionLabel = new JLabel("Description");
		objectDiagramTextPane = new JTextPane();			
		objectDiagramTextPane.addKeyListener(this);
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(objectDiagramTextPane);
		
		topPanel.add(diagramNameLabel);
		topPanel.add(objectDiagramTextField);
		topPanel.add(sequenceReferenceLabel);
		topPanel.add(sequenceReferenceComboBox);	
		SpringUtilities.makeCompactGrid(topPanel,2,2,5,5,5,5);
		
		bottomPanel.add(actionDescriptionLabel, BorderLayout.NORTH);
		bottomPanel.add(scrollText, BorderLayout.CENTER);
		
		objectDiagramBasePanel.add(topPanel, BorderLayout.NORTH);
		objectDiagramBasePanel.add(bottomPanel, BorderLayout.CENTER);
	
	return objectDiagramBasePanel;
}

	private void setObjectDiagramBasePanel(){
		objectDiagramTextField.setText(data.getChildText("name"));
		
		sequenceReferenceComboBox.setSelectedItem(data.getChildText("sequenceReference"));
		
		objectDiagramTextPane.setText(data.getChildText("description"));
	}	

	private JPanel getObjectAssociationBasePanel(){ 
		
		objectAssociationBasePanel = new JPanel(new BorderLayout());
		objectAssociationBasePanel.setPreferredSize(new Dimension(250,200));			
		
		ItPanel topPanel = new ItPanel(new BorderLayout());
		ItPanel bottomPanel = new ItPanel(new BorderLayout());
		
		
		JLabel actionLabel = new JLabel("Association ");	
		
		objectAssociationComboBox = new ItComboBox();
		objectAssociationComboBox.addItemListener(this);
		
		
		JLabel actionDescriptionLabel = new JLabel("Description");
		objectAssocDescriptionTextPane = new JTextPane();			
		objectAssocDescriptionTextPane.addKeyListener(this);
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(objectAssocDescriptionTextPane);
		
		topPanel.add(actionLabel, BorderLayout.WEST);
		topPanel.add(objectAssociationComboBox, BorderLayout.CENTER);

		
		bottomPanel.add(actionDescriptionLabel, BorderLayout.NORTH);
		bottomPanel.add(scrollText, BorderLayout.CENTER);
		
		objectAssociationBasePanel.add(topPanel, BorderLayout.NORTH);
		objectAssociationBasePanel.add(bottomPanel, BorderLayout.CENTER);
	
	return objectAssociationBasePanel;
}

	private void setObjectAssociationPanel(){
		//1. Get all the classes that may be associated from both objects
		objectAssociationComboBox.removeAllItems();	
		List<?> result = null;
		
		Element sourceClass = null;
		Element targetClass = null;
		Element sourceObject = null;
		Element targetObject = null;		
		
		Element domain = null;
		Element diagram = data.getParentElement().getParentElement();
		if(diagram.getName().equals("repositoryDiagram")){
						//repositoryDiagrams	//domain
			domain = diagram.getParentElement().getParentElement();
		}
		else{
							//objectDiagrams	//problem		//planningProblems		//domain
			domain = diagram.getParentElement().getParentElement().getParentElement().getParentElement();
		}				
		
		Iterator<?> associationEnds = data.getChild("associationEnds").getChildren("objectAssociationEnd").iterator();
		if(associationEnds.hasNext()){
			
			Element associationEnd = (Element)associationEnds.next();
			//get source object
			try {
				XPath path = new JDOMXPath("elements/objects/object[@id="+associationEnd.getAttributeValue("element-id")+"]");
				sourceObject = (Element)path.selectSingleNode(domain);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}	
			//Get source Class
			if(sourceObject != null){
				try {
					XPath path = new JDOMXPath("project/elements/classes/class[@id="+sourceObject.getChildText("class")+"]");
					sourceClass = (Element)path.selectSingleNode(data.getDocument());
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
			}

			associationEnd = (Element)associationEnds.next();	
			//get target object
			try {
				XPath path = new JDOMXPath("elements/objects/object[@id="+associationEnd.getAttributeValue("element-id")+"]");
				targetObject = (Element)path.selectSingleNode(domain);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}				
			//Get target Class
			if(targetObject != null){
				try {
					XPath path = new JDOMXPath("project/elements/classes/class[@id="+targetObject.getChildText("class")+"]");
					targetClass = (Element)path.selectSingleNode(data.getDocument());
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
			}			
		}				
		
		
		// Get the possible associations and fill out the combobox
		if (sourceClass != null && targetClass !=null){
			String sourceXPathExpression = "associationEnds/associationEnd/@element-id='"+ sourceClass.getAttributeValue("id") +"'"; 
			
			String targetXPathExpression = "associationEnds/associationEnd/@element-id='"+ targetClass.getAttributeValue("id") +"'";
			
			ArrayList<Element> sourceSuperClasses = new ArrayList<Element>();
			ArrayList<Element> targetSuperClasses = new ArrayList<Element>();	
			
			getSuperClasses(sourceClass, sourceSuperClasses);
			getSuperClasses(targetClass, targetSuperClasses);
			
			if (sourceSuperClasses.size() > 0){
				for (int i = 0; i < sourceSuperClasses.size(); i++){
					Element superClass = sourceSuperClasses.get(i);
					sourceXPathExpression = sourceXPathExpression + " or associationEnds/associationEnd/@element-id='"+ superClass.getAttributeValue("id") + "'";
				}			
			}
			
			if (targetSuperClasses.size() > 0){
				for (int i = 0; i < targetSuperClasses.size(); i++){
					Element superClass = targetSuperClasses.get(i);
					targetXPathExpression = targetXPathExpression + " or associationEnds/associationEnd/@element-id='"+ superClass.getAttributeValue("id") + "'";
				}			
			}
			
			try {
				XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation[(" +
						sourceXPathExpression+") and (" +targetXPathExpression +")]");
				
				/*XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation[" +
						"associationEnds/associationEnd/@element-id='"+ sourceClassID +"' and " +
						"associationEnds/associationEnd/@element-id='"+ targetClassID +"']");*/
				result = path.selectNodes(data.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			for (int i = 0; i < result.size(); i++){
				Element association = (Element)result.get(i);
				objectAssociationComboBox.addItem(association.getChildText("name"),association);
			}	
			
			//Select the operator index
			if (reference != null){
				objectAssociationComboBox.setSelectedItem(reference.getChildText("name"));										
			}
			else{
				objectAssociationComboBox.setSelectedIndex(0);
			}
			
		}

	}	
		
	private JPanel getAttributePanel(){  

                attributesPanel = new JPanel(new BorderLayout());
		attributesPanel.setPreferredSize(new Dimension(250,200));			

		topAttributePanel = new ItPanel(new BorderLayout());
		bottomAttributePanel = new ItPanel(new BorderLayout());
		JButton button = null;
                attributeToolBar = new JToolBar();
		button = attributeToolBar.add(newAttribute);
                button.setToolTipText("New Attribute");
		button = attributeToolBar.add(deleteAttribute);
                button.setToolTipText("Delete selected attribute");
		button = attributeToolBar.add(editAttribute);
		button.setToolTipText("Edit selected attribute");
		//TABLE
		DefaultTableModel tableModel = new DefaultTableModel();		
		tableModel.addTableModelListener(this);
		
        attributeTable = new JTable(tableModel);
        //set size of all rows
        attributeTable.setRowHeight(20);
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(attributeTable);	
		
		
		
		//Name column
		tableModel.addColumn("Name");

		
		//Type column
		tableModel.addColumn("Type");
		
		
		//Initial Value column
		tableModel.addColumn("Initial Value");
		
		attributeType = new ItComboBox();		
		
		//TableColumn type = new TableColumn();
		TableColumn type = attributeTable.getColumnModel().getColumn(1);		
		type.setCellEditor(new DefaultCellEditor(attributeType));
		
		topAttributePanel.add(scrollText, BorderLayout.CENTER);		
		bottomAttributePanel.add(attributeToolBar, BorderLayout.NORTH);
	
		attributesPanel.add(topAttributePanel, BorderLayout.CENTER);
		attributesPanel.add(bottomAttributePanel, BorderLayout.SOUTH);
	
	return attributesPanel;
}

	private void setAttributePanel(){
		
		//1. Fill ou the type combobox
		attributeType.removeAllItems();
		attributeType.addItem("",null);
		List<?> classes = data.getParentElement().getChildren();
/*		try {
			XPath path = new JDOMXPath("project/elements/classes/class");
			classes = path.selectNodes(data.getDocument());
		} catch (JaxenException e2) {			
			e2.printStackTrace();
		}*/
		
		for (Iterator<?> iter = classes.iterator(); iter.hasNext();) {
			Element classNode = (Element) iter.next();			
			attributeType.addItem(classNode.getChildText("name"),classNode);
		}
		
		/*for (int i = 0; i < classes.size(); i++){
			Element Class = (Element)classes.get(i);
			attributeType.addItem(Class.getChildText("name"),Class);
		}*/		

		//2. Fill out the class attributes		
		DefaultTableModel tableModel = (DefaultTableModel)attributeTable.getModel();
		//2.1 Clean up table
		while (tableModel.getRowCount() > 0){
			tableModel.removeRow(0);
		}
		//	2.2 build attributes
		currentAttributes.clear();
		Iterator<?> attributes = data.getChild("attributes").getChildren("attribute").iterator();
		while (attributes.hasNext()){
			Element attribute = (Element)attributes.next();
			currentAttributes.add(attribute);			
			showAttribute(attribute);
		}	
		
	}
	
	private void showAttribute(Element attribute) {
		DefaultTableModel tableModel = (DefaultTableModel)attributeTable.getModel();
		
		Vector<String> attRow = new Vector<String>();		
		attRow.add(attribute.getChildText("name"));
		
		Element Classes = data.getParentElement();
		Element typeClass = XMLUtilities.getElement(Classes, attribute.getChildText("type"));
		if (typeClass != null){
			attRow.add(typeClass.getChildText("name"));		
		}
		else{
			attRow.add("");	
		}	
		attRow.add(attribute.getChildText("initialValue"));			
		tableModel.addRow(attRow);			
	}	

	private JPanel getOperatorPanel(){  
		operatorsPanel = new JPanel(new BorderLayout());
		operatorsPanel.setPreferredSize(new Dimension(250,200));			

		topOperatorPanel = new ItPanel(new BorderLayout());
		bottomOperatorPanel = new ItPanel(new BorderLayout());
		
                JButton button = null;
		operatorToolBar = new JToolBar();
		operatorToolBar.add(newOperator).setToolTipText("New operator");
		button = operatorToolBar.add(deleteOperator);
                button.setToolTipText("Delete selected operator");
		button = operatorToolBar.add(editOperator);
                button.setToolTipText("Edit selected operator");

		operatorListModel = new DefaultListModel();		
		operatorList = new JList(operatorListModel);		
		ItListRenderer renderer = new ItListRenderer();	
		
				
		renderer.setIcon(new ImageIcon("resources/images/operator.png"));
		operatorList.setCellRenderer(renderer);

		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(operatorList);
		operatorList.addMouseListener(this);
		operatorList.addKeyListener(this);
				
		topOperatorPanel.add(scrollText, BorderLayout.CENTER);		
		bottomOperatorPanel.add(operatorToolBar, BorderLayout.NORTH);
	
		operatorsPanel.add(topOperatorPanel, BorderLayout.CENTER);
		operatorsPanel.add(bottomOperatorPanel, BorderLayout.SOUTH);

	return operatorsPanel;
}
	
	private void setOperatorPanel(){
		
		//1. Fill out operator list
		operatorListModel.clear();
		currentOperators.clear();

		Iterator<?> operators = data.getChild("operators").getChildren("operator").iterator();
		while (operators.hasNext()){
			Element operator = (Element)operators.next();
			//JLabel current = new JLabel(operator.getChildText("name"));
			//current.setIcon(new ImageIcon("resources/images/attribute.png"));
			operatorListModel.addElement(operator.getChildText("name"));
			//operatorListModel.addElement(current);
			//showOperator(operator);
			currentOperators.add(operator);
		}
		
	}
	
	private JPanel getLiteralsPanel(){
		literalsPanel = new JPanel(new BorderLayout());
		
		literalsTableModel = new DefaultTableModel();
		literalsTableModel.addColumn("Literals");
		//literalsTableModel.addTableModelListener(this);
		
		literalsTable = new JTable(literalsTableModel);
		
		literalsPanel.add(new JScrollPane(literalsTable), BorderLayout.CENTER);
		
		JToolBar toolBar = new JToolBar();
		toolBar.add(newLiteral);
		toolBar.add(deleteLiteral);
		
		literalsPanel.add(toolBar, BorderLayout.SOUTH);
		
		return literalsPanel;
	}

	private void setLiteralsPanel(){
		
		literalsTableModel.removeTableModelListener(this);
		
		// clear the table
		while(literalsTableModel.getRowCount() > 0){
			literalsTableModel.removeRow(0);
		}
		
		//build literals
		currentLiterals.clear();
		List<?> literals = data.getChild("literals").getChildren("literal");
		for (Iterator<?> iter = literals.iterator(); iter.hasNext();) {
			Element literal = (Element) iter.next();
			currentLiterals.add(literal);
			
			// show the literal
			showLiteral(literal);
		}
		
		literalsTableModel.addTableModelListener(this);

	}
	
	private void showLiteral(Element literal){
		Vector<String> row = new Vector<String>(1);
		row.add(literal.getChildText("name"));
		literalsTableModel.addRow(row);
	}
	
	
	private JPanel getInternalConditionPanel(){  
		internalConditionsPanel = new JPanel(new BorderLayout());
		internalConditionsPanel.setPreferredSize(new Dimension(250,200));			

		topConditionPanel = new ItPanel(new BorderLayout());
		bottomConditionPanel = new ItPanel(new BorderLayout());
		
		ItHilightedDocument internalConditionDocument = new ItHilightedDocument();
		internalConditionDocument.setHighlightStyle(ItHilightedDocument.OCL_STYLE);
		conditionTextPane = new JTextPane(internalConditionDocument);		
		conditionTextPane.addKeyListener(this);
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(conditionTextPane);	
		conditionLabel = new JLabel("Internal conditions");
		
				
		topConditionPanel.add(conditionLabel, BorderLayout.CENTER);		
		bottomConditionPanel.add(scrollText, BorderLayout.CENTER);
	
		internalConditionsPanel.add(topConditionPanel, BorderLayout.NORTH);
		internalConditionsPanel.add(bottomConditionPanel, BorderLayout.CENTER);

		return internalConditionsPanel;
	}
	
	private void setInternalCondition(){
		conditionTextPane.setText(data.getChildText("condition"));		
	}
	
	private JPanel getActionConditionPanel(){  
		actionConditionsPanel = new JPanel(new BorderLayout());
		actionConditionsPanel.setPreferredSize(new Dimension(250,200));			

		preConditionPanel = new ItPanel(new BorderLayout());
		postConditionPanel = new ItPanel(new BorderLayout());
		
		ItHilightedDocument preConditionDocument = new ItHilightedDocument();
		preConditionDocument.setHighlightStyle(ItHilightedDocument.OCL_STYLE);
		preConditionTextPane = new JTextPane(preConditionDocument);
		preConditionTextPane.addKeyListener(this);
		preConditionTextPane.setFont(new Font("Courier", 0, 12));
		JScrollPane preScrollText = new JScrollPane();
		preScrollText.setViewportView(preConditionTextPane);
		
		ItHilightedDocument postConditionDocument = new ItHilightedDocument();
		postConditionDocument.setHighlightStyle(ItHilightedDocument.OCL_STYLE);
		postConditionTextPane = new JTextPane(postConditionDocument);
		postConditionTextPane.addKeyListener(this);
		preConditionTextPane.setFont(new Font("Courier", 0, 12));
		JScrollPane postScrollText = new JScrollPane();
		postScrollText.setViewportView(postConditionTextPane);
		
		
		preConditionLabel = new JLabel("Pre conditions");
		postConditionLabel = new JLabel("Post conditions");		
		
				
		preConditionPanel.add(preConditionLabel, BorderLayout.NORTH);
		preConditionPanel.add(preScrollText, BorderLayout.CENTER);
		
		preConditionPanel.setPreferredSize(new Dimension(250,160));
		
		postConditionPanel.add(postConditionLabel, BorderLayout.NORTH);		
		postConditionPanel.add(postScrollText, BorderLayout.CENTER);
	
		actionConditionsPanel.add(preConditionPanel, BorderLayout.NORTH);
		actionConditionsPanel.add(postConditionPanel, BorderLayout.CENTER);

		return actionConditionsPanel;
	}
	
	private void setActionConditionPanel(){
		preConditionTextPane.setText(data.getChildText("precondition"));		
		postConditionTextPane.setText(data.getChildText("postcondition"));
	}
	
	private JPanel getRoleAPanel(){
		
		SpringLayout layout = new SpringLayout();

		roleAPanel = new JPanel(new BorderLayout());
		roleAPanel.setPreferredSize(new Dimension(250,200));
		
		JPanel topPanel = new JPanel(layout);
		roleAPanel.add(topPanel, BorderLayout.NORTH);
		
		//TOP
		roleNameATextField = new JTextField(15);
		roleNameATextField.addKeyListener(this);		
		
		JLabel classLabel = new JLabel("Class ");
		JLabel roleNameLabel = new JLabel("Name ");
		JLabel navigationLabel = new JLabel("Navigation ");
		JLabel aggregationLabel = new JLabel("Type ");
		JLabel multiplicityLabel = new JLabel("Multiplicity ");	
		
		classALabel = new JLabel("");		
		navigationA = new ItComboBox();
		aggregationA = new ItComboBox();
		multiplicityA = new ItComboBox();
		multiplicityA.setEditable(true);
		
		ComboBoxRenderer renderer = new ComboBoxRenderer();
		navigationA.setRenderer(renderer);
		aggregationA.setRenderer(renderer);
		
		navigationA.addItem("true");
		navigationA.addItem("false");
		navigationA.addItemListener(this);
		
		aggregationA.addItem("none");
		aggregationA.addItem("simple");		
		aggregationA.addItem("aggregation");
		aggregationA.addItem("composition");
		aggregationA.addItemListener(this);
		
		multiplicityA.addItem("");
		multiplicityA.addItem("1");
		multiplicityA.addItem("0..1");
		multiplicityA.addItem("0..*");
		multiplicityA.addItem("*");	
		multiplicityA.addItem("1..*");
		multiplicityA.addItemListener(this);
		multiplicityA.addKeyListener(this);
		
		topPanel.add(classLabel);
		topPanel.add(classALabel);			
		topPanel.add(roleNameLabel);
		topPanel.add(roleNameATextField);
		
		topPanel.add(navigationLabel);
		topPanel.add(navigationA);			
		topPanel.add(aggregationLabel);
		topPanel.add(aggregationA);	
		topPanel.add(multiplicityLabel);
		topPanel.add(multiplicityA);			
		
		SpringUtilities.makeCompactGrid(topPanel,5,2,5,5,5,5);

		return roleAPanel;
	}
	
	private void setRoleAPanel(){

		
		Element associationEndA = null;
		try {
			XPath path = new JDOMXPath("associationEnds/associationEnd[@id=1]");
			associationEndA = (Element)path.selectSingleNode(data);
		} catch (JaxenException e2) {			
			e2.printStackTrace();
		}
		
		if (associationEndA != null){
			
			//1. Get the source class
			Element sourceClass = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[@id="+associationEndA.getAttributeValue("element-id")+"]");
				sourceClass = (Element)path.selectSingleNode(data.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			if (sourceClass != null){
				classALabel.setText(sourceClass.getChildText("name"));
			}	
			roleNameATextField.setText(associationEndA.getChild("rolename").getChildText("value"));
			
			//2. Set navigation A
			navigationA.setSelectedItem(associationEndA.getAttributeValue("navigation"));
			
			//3. Set aggregation A
			aggregationA.setSelectedItem(associationEndA.getChildText("type"));
			
			//4. Set multiplicity A
			multiplicityA.setSelectedItem(associationEndA.getChild("multiplicity").getChildText("value"));
			
		}
	}	

	private JPanel getRoleBPanel(){ 
		
		
		SpringLayout layout = new SpringLayout();

		roleBPanel = new JPanel(new BorderLayout());
		roleBPanel.setPreferredSize(new Dimension(250,200));
		
		JPanel topPanel = new JPanel(layout);
		roleBPanel.add(topPanel, BorderLayout.NORTH);
		
		//TOP
		roleNameBTextField = new JTextField(15);
		roleNameBTextField.addKeyListener(this);		
		
		JLabel classLabel = new JLabel("Class ");
		JLabel roleNameLabel = new JLabel("Name ");
		JLabel navigationLabel = new JLabel("Navigation ");
		JLabel aggregationLabel = new JLabel("Type ");
		JLabel multiplicityLabel = new JLabel("Multiplicity ");	
		
		classBLabel = new JLabel("");		
		navigationB = new ItComboBox();
		aggregationB = new ItComboBox();
		multiplicityB = new ItComboBox();
		multiplicityB.setEditable(true);
		
		ComboBoxRenderer renderer = new ComboBoxRenderer();		
		navigationB.setRenderer(renderer);
		aggregationB.setRenderer(renderer);
		//multiplicityB.setRenderer(renderer);			
		
		navigationB.addItem("true");
		navigationB.addItem("false");
		navigationB.addItemListener(this);
		
		aggregationB.addItem("none");
		aggregationB.addItem("simple");		
		aggregationB.addItem("aggregation");
		aggregationB.addItem("composition");
		aggregationB.addItemListener(this);
		
		multiplicityB.addItem("");
		multiplicityB.addItem("1");
		multiplicityB.addItem("0..1");
		multiplicityB.addItem("0..*");
		multiplicityB.addItem("*");	
		multiplicityB.addItem("1..*");
		multiplicityB.addItemListener(this);
		multiplicityB.addKeyListener(this);
		
		topPanel.add(classLabel);
		topPanel.add(classBLabel);			
		topPanel.add(roleNameLabel);
		topPanel.add(roleNameBTextField);
		
		topPanel.add(navigationLabel);
		topPanel.add(navigationB);			
		topPanel.add(aggregationLabel);
		topPanel.add(aggregationB);	
		topPanel.add(multiplicityLabel);
		topPanel.add(multiplicityB);			
		
		SpringUtilities.makeCompactGrid(topPanel,5,2,5,5,5,5);
		

		return roleBPanel;
	}
	
	private void setRoleBPanel(){

		Element associationEndA = null;
		try {
			XPath path = new JDOMXPath("associationEnds/associationEnd[@id=2]");
			associationEndA = (Element)path.selectSingleNode(data);
		} catch (JaxenException e2) {			
			e2.printStackTrace();
		}
		
		if (associationEndA != null){
			
			//1. Get the source class 
			Element sourceClass = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[@id="+associationEndA.getAttributeValue("element-id")+"]");
				sourceClass = (Element)path.selectSingleNode(data.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			if (sourceClass != null){
				classBLabel.setText(sourceClass.getChildText("name"));
			}	
			roleNameBTextField.setText(associationEndA.getChild("rolename").getChildText("value"));
			
			//2. Set navigation B
			navigationB.setSelectedItem(associationEndA.getAttributeValue("navigation"));
			
			//3. Set aggregation B
			aggregationB.setSelectedItem(associationEndA.getChildText("type"));
			
			//4. Set multiplicity B
			multiplicityB.setSelectedItem(associationEndA.getChild("multiplicity").getChildText("value"));
			
		}
	
	}
	
	private JPanel getAdditionalAssociationPanel(){
		SpringLayout layout = new SpringLayout();

		additionalAssociationPanel = new JPanel(new BorderLayout());
		additionalAssociationPanel.setPreferredSize(new Dimension(250,200));
		
		JPanel topPanel = new JPanel(layout);
		additionalAssociationPanel.add(topPanel, BorderLayout.NORTH);
		
		JLabel changeabilityLabel = new JLabel("Changeability ");
		associationChangeability = new ItComboBox();
		associationChangeability.setEditable(true);
		
		associationChangeability.addItem("");		
		associationChangeability.addItem("changeable");
		associationChangeability.addItem("addOnly");
		associationChangeability.addItem("frozen");
		associationChangeability.addItemListener(this);
		
		
		topPanel.add(changeabilityLabel);
		topPanel.add(associationChangeability);
		
		SpringUtilities.makeCompactGrid(topPanel,1,2,5,5,5,5);
		
		return additionalAssociationPanel;
	}
	
	private void setAdditionalAssociationPanel(){
		associationChangeability.setSelectedItem(data.getChild("changeability").getChildText("value"));
	}
	
	private JPanel getObjectAttributePanel(){
		objectAttributesPanel = new JPanel(new BorderLayout());
		objectAttributesPanel.setPreferredSize(new Dimension(250,200));			

		objectTopAttributePanel = new ItPanel(new BorderLayout());
		objectBottomAttributePanel = new ItPanel(new BorderLayout());
		
		//TABLE
		DefaultTableModel tableModel = new DefaultTableModel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = -1515586562196686580L;
			// only the value column is editable
			public boolean isCellEditable(int row, int col) {		        
		        if (col == 2) {
		            return true;
		        }
		        else {
		            return false;
		        }
		    }
		};		
		tableModel.addTableModelListener(this);
		
        objectAttributeTable = new JTable(tableModel);
        //set size of all rows
        objectAttributeTable.setRowHeight(20);
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(objectAttributeTable);		
		
		//Name column
		tableModel.addColumn("Name");

		
		//Type column
		tableModel.addColumn("Type");
		
		
		// Value column		
		tableModel.addColumn("Value");
		
		objectAttributeValue = new EachRowEditor(objectAttributeTable);				
		TableColumn valueColumn = objectAttributeTable.getColumnModel().getColumn(2);		
		valueColumn.setCellEditor(objectAttributeValue);

		objectTopAttributePanel.add(scrollText, BorderLayout.CENTER);	
	
		objectAttributesPanel.add(objectTopAttributePanel, BorderLayout.CENTER);
		objectAttributesPanel.add(objectBottomAttributePanel, BorderLayout.SOUTH);
	
		return objectAttributesPanel;
	}
	
	private void setObjectAttributePanel(){		

		//1. Fill out the class attributes
		//DefaultTableModel tableModel = new DefaultTableModel();
		
		DefaultTableModel tableModel = (DefaultTableModel)objectAttributeTable.getModel();
		//1.1 Clean up table
		while (tableModel.getRowCount() > 0){
			tableModel.removeRow(0);
		}
		//	1.2 build attributes
		currentObjectAttributes.clear();
		currentAttributes.clear();
		Iterator<?> objectAttributes = reference.getChild("attributes").getChildren("attribute").iterator();		
		//objectAttributeValue = new EachRowEditor(objectAttributeTable);		
		int row = 0;
		while (objectAttributes.hasNext()){
			Element objectAttribute = (Element)objectAttributes.next();
			currentObjectAttributes.add(objectAttribute);			
			showObjectAttribute(objectAttribute, row++);
			
		}		
	}
	
	private void showObjectAttribute(Element objectAttribute, int row) {

		//DefaultTableModel tableModel = new DefaultTableModel();
		DefaultTableModel tableModel = (DefaultTableModel)objectAttributeTable.getModel();
		
		Element classAttribute = null;		
		try {
			XPath path = new JDOMXPath("project/elements/classes/class[@id='" +objectAttribute.getAttributeValue("class")+
					"']/attributes/attribute[@id='"+objectAttribute.getAttributeValue("id")+"']");
			classAttribute = (Element)path.selectSingleNode(objectAttribute.getDocument());
		} catch (JaxenException e2) {			
			e2.printStackTrace();
		}
		
		if (classAttribute != null){
			// for the attribute parameters table
			currentAttributes.add(classAttribute);
			
			Element typeClass = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[@id='" +classAttribute.getChildText("type")+"'] | " +
						"project/elements/classes/enumeration[@id='" +classAttribute.getChildText("type")+"']");
				typeClass = (Element)path.selectSingleNode(objectAttribute.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}			
			
			Vector<String> attRow = new Vector<String>();			
			// the name is in the class attribute
			attRow.add(classAttribute.getChildText("name"));
			
			// the type is the type class name
			String attributeType = typeClass.getChildText("name");
			attRow.add(attributeType);
			
			// value
			String attributeValue = objectAttribute.getChildText("value");
			
			objectAttributeValue.cancelCellEditing();
			ItComboBox value = new ItComboBox();
			JTextField text = new JTextField();
			
			// attribute with parameters
			if (classAttribute.getChild("parameters").getChildren().size() > 0){
				attRow.add("[List] ...");
				objectAttributeValue.setEditorAt(row, new ButtonEditor(openAttributeParameterValues));
			}
			// attribute without parameters
			else {
				
				if (attributeType.equals("Boolean")){
				value.addItem("");
				value.addItem("true");
				value.addItem("false");
				
				value.setSelectedItem(attributeValue);
				
				objectAttributeValue.setEditorAt(row, new DefaultCellEditor(value));
				}else if (attributeType.equals("Int")) {
					JTextFieldFilter filter = new JTextFieldFilter(JTextFieldFilter.NUMERIC);
					filter.setNegativeAccepted(true);
					text.setDocument(filter);				
					objectAttributeValue.setEditorAt(row, new DefaultCellEditor(text));
				} else if (attributeType.equals("Float")) {
					JTextFieldFilter filter = new JTextFieldFilter(JTextFieldFilter.FLOAT);
					filter.setNegativeAccepted(true);
					text.setDocument(filter);
					objectAttributeValue.setEditorAt(row, new DefaultCellEditor(text));
				} else if(attributeType.equals("String")){
					objectAttributeValue.setEditorAt(row, new DefaultCellEditor(new JTextField()));
				}
				
				else if(typeClass.getName().equals("enumeration")){
					value.addItem("");
					List<?> literals = typeClass.getChild("literals").getChildren("literal");
					for (Iterator<?> iter = literals.iterator(); iter.hasNext();) {
						Element literal = (Element) iter.next();
						value.addItem(literal.getChildText("name"));
					}
					
					value.setSelectedItem(attributeValue);
					
					objectAttributeValue.setEditorAt(row, new DefaultCellEditor(value));
				}
				
				else {
				// if it's not a Boolean, Int, Float or String then its class is in this project
					value.addItem("");				
					Element problem = data.getParentElement().getParentElement().getParentElement();
					
					//Get all descendent classes of typeClass
					List<?> descendents = XMLUtilities.getClassDescendents(typeClass);
										
					//create the queries for xpath
					String descendentsQuery = "";
					
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
						descendentsQuery = "class='" + typeClass.getAttributeValue("id") + "'";			
					else
						descendentsQuery = descendentsQuery + " or class='" + typeClass.getAttributeValue("id") + "'";					
					
					List<?> result = null;	
					try {
						XPath path = new JDOMXPath("elements/objects/object["+descendentsQuery+"]");
						result = path.selectNodes(problem);
						
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					if (result != null){
						Iterator<?> objects = result.iterator();
						while(objects.hasNext()){
							Element object = (Element)objects.next();
							value.addItem(object.getChildText("name"));
						}
						
						value.setSelectedItem(attributeValue);
					
					}
					
					objectAttributeValue.setEditorAt(row, new DefaultCellEditor(value));
				}		
						
				// the value is in the object attribute
				attRow.add(attributeValue);			
			}
			tableModel.addRow(attRow);	
			
		}		
	}
	
	private JPanel getAdditionalPanel(){
		SpringLayout layout = new SpringLayout();

		additionalPanel = new JPanel(new BorderLayout());
		additionalPanel.setPreferredSize(new Dimension(250,200));
		
		JPanel topPanel = new JPanel(layout);
		additionalPanel.add(topPanel, BorderLayout.NORTH);
		
		JLabel stereotypeLabel = new JLabel("Stereotype ");
		stereotype = new ItComboBox();
		stereotype.setEditable(true);
		
		stereotype.addItem("");		
		stereotype.addItem("agent");
		stereotype.addItem("utility");
		stereotype.addItemListener(this);
		
		
		topPanel.add(stereotypeLabel);
		topPanel.add(stereotype);
		
		SpringUtilities.makeCompactGrid(topPanel,1,2,5,5,5,5);
		
		return additionalPanel;
	}

	private void setAdditionalPanel(){
		stereotype.setSelectedItem(data.getChildText("stereotype"));
	}
	
	private JPanel getConstraintsPanel(){
		constraintsPanel = new JPanel(new BorderLayout());		

		ItPanel topPanel = new ItPanel(new BorderLayout());		
		
		ItHilightedDocument document = new ItHilightedDocument();
		document.setHighlightStyle(ItHilightedDocument.OCL_STYLE);
		constraintsTextPane = new JTextPane(document);		
		constraintsTextPane.addKeyListener(this);
		JScrollPane scroll = new JScrollPane(constraintsTextPane);		
		JLabel label = new JLabel("Constraints");
		
				
		topPanel.add(label, BorderLayout.CENTER);
	
	
		constraintsPanel.add(topPanel, BorderLayout.NORTH);
		constraintsPanel.add(scroll, BorderLayout.CENTER);
		
		return constraintsPanel;
	}
	
	private void setConstraintsPanel(){
		constraintsTextPane.setText(data.getChildText("constraints"));
	}
	
	private JPanel getMetricsPanel(){
		metricsPanel = new JPanel(new BorderLayout());
		
		ItPanel topPanel = new ItPanel(new BorderLayout());
		metricsListModel = new DefaultListModel();
		metricsList = new JCheckBoxList(true);
		metricsList.setModel(metricsListModel);
		topPanel.add(new JScrollPane(metricsList), BorderLayout.CENTER);
		
		// tool bar
		JToolBar toolBar = new JToolBar();
		toolBar.add(newMetric);
		toolBar.add(deleteMetric);
		toolBar.add(editMetric);		
		
		metricsPanel.add(topPanel, BorderLayout.CENTER);
		metricsPanel.add(toolBar, BorderLayout.SOUTH);
		
		return metricsPanel;
	}
	
	private void setMetricsPanel(){		
		metricsListModel.clear();
		List<?> metrics = data.getChild("metrics").getChildren("metric");
		for (Iterator<?> iterator = metrics.iterator(); iterator.hasNext();) {
			Element metric = (Element) iterator.next();
			metricsListModel.addElement(
					new JCheckBoxListItem(metric, 
							Boolean.parseBoolean(metric.getChildText("enabled"))));
		}
		
	}
	
	
	public void showProperties(Object element, Object sender){
		
		//get the last selected tab
		if (data != null){
			lastSelectTab = data.getName();
			lastSelectIndex = this.getSelectedIndex();
		}else{
			lastSelectTab = "";
			lastSelectIndex = 0;	
		}
			
		
		selectedNode = null;
		selectedCell = null;
		selectedAssociation = null;
		
		if (element instanceof ItTreeNode){
			selectedNode = (ItTreeNode) element;
			data = selectedNode.getData();
			reference = selectedNode.getReference();
			additional = selectedNode.getAdditionalData();
			senderObject = sender;
		}
		
		else if(element instanceof ActorCell ||
				element instanceof UseCaseCell ||
				element instanceof ClassCell ||
				element instanceof EnumerationCell ||
				element instanceof StateCell ||
				element instanceof InitialStateCell ||
				element instanceof FinalStateCell ||
				element instanceof ObjectCell){
			selectedCell = (BasicCell) element;
			data = selectedCell.getData();
			reference = selectedCell.getReference();
			additional = selectedCell.getAdditionalData();
			senderObject = sender;
		}
		
		else if (element instanceof UseCaseAssociation ||
				element instanceof Dependency ||
				element instanceof ClassAssociation ||
				element instanceof Generalization || 
				element instanceof ActionAssociation ||
				element instanceof ObjectAssociation){
			selectedAssociation = (BasicAssociation) element;
			data = selectedAssociation.getData();
			reference = selectedAssociation.getReference();			
			senderObject = sender;
		}
		
		//if selectedNode is null search it at the tree
		if (selectedNode == null){
			ItTree tree = ItSIMPLE.getInstance().getItTree();
			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			ItTreeNode node = tree.findNodeFromData((ItTreeNode)model.getRoot(), data);
			selectedNode = node;
		}
		
		this.removeAll();
		
		if (data != null){
			if (data.getName().equals("class")){
				//Base
				getBasePanel();
				setBasePanel();			
				setAttributePanel();
				setOperatorPanel();
				setAdditionalPanel();
				setConstraintsPanel();
				addTab("Base", basePanel);
				addTab("Attribute", attributesPanel);
				addTab("Operator", operatorsPanel);
				addTab("Additional", additionalPanel);
				addTab("Constraints", constraintsPanel);
			}
			
			else if(data.getName().equals("enumeration")){
				getBasePanel();
				setBasePanel();
				setLiteralsPanel();
				addTab("Base", basePanel);
				addTab("Literals", literalsPanel);
			}
			
			else if(data.getName().equals("state")){
				//Base
				getBasePanel();
				setBasePanel();			
				setInternalCondition();
				addTab("Base", basePanel);
				addTab("Internal Conditions", internalConditionsPanel);
			}
			else if(data.getName().equals("action")){
				//Base
				setActionBasePanel();
				setActionConditionPanel();
				this.addTab("Base", actionBasePanel);
				this.addTab("Conditions", actionConditionsPanel);
			}		
			else if(data.getName().equals("classAssociation")){
				//Base
				getBasePanel();
				setBasePanel();	
				setRoleAPanel();
				setRoleBPanel();
				setAdditionalAssociationPanel();
				setConstraintsPanel();
				addTab("Base", basePanel);
				addTab("Role A", roleAPanel);
				addTab("Role B", roleBPanel);
				addTab("Additional", additionalAssociationPanel);
				addTab("Constraints", constraintsPanel);
			}
			
			else if(data.getName().equals("classDiagram")){
				getBasePanel();
				setBasePanel();
				setConstraintsPanel();
				addTab("Base", basePanel);
				addTab("Constraints", constraintsPanel);
			}
			
			else if(data.getName().equals("object")){
				getBasePanel();
				setBasePanel();
				setObjectAttributePanel();
				addTab("Base", basePanel);
				addTab("Attribute", objectAttributesPanel);
				
				// block name editing in object diagrams
				if(reference.getParentElement().getParentElement().getName().equals("objectDiagram")){
					nameTextField.setEnabled(false);
					classComboBox.setEnabled(false);
				}
				else{
					nameTextField.setEnabled(true);
					classComboBox.setEnabled(true);
				}
				
			}
			else if(data.getName().equals("objectAssociation")){
				//Base
				setObjectAssociationPanel();
				this.addTab("Base", objectAssociationBasePanel);		
			}
			else if(data.getName().equals("objectDiagram")){
				//Base
				setObjectDiagramBasePanel();
				this.addTab("Base", objectDiagramBasePanel);		
			}
			else if(data.getName().equals("useCase")){
				getBasePanel();
				setBasePanel();
				getUseCaseConstraintPane();
				setUseCaseConstraintPane();
				getUseCaseFlowOfEventsPanel();
				setUseCaseFlowOfEventsPanel();
				getUseCaseFurtherDetailsPane();
				setUseCaseFurtherDetailsPane();
				this.addTab("Base", basePanel);
				this.addTab("Constraints", useCaseConstraintPane);
				this.addTab("Flow of events", useCaseFlowOfEventsPanel);
				this.addTab("Further Details", useCaseFurtherDetailsPane);
			}
			
			else if(data.getName().equals("problem")){
				getBasePanel();
				setBasePanel();
				getMetricsPanel();
				setMetricsPanel();
				addTab("Base", basePanel);
				addTab("Metrics", metricsPanel);
			}
			
			else{
				getBasePanel();
				setBasePanel();
				addTab("Base", basePanel);
			}
			
			//Select the tab that was already selected if the current selected element is of the same type of the last one
			if (data.getName().equals(lastSelectTab)){
				if (lastSelectIndex >= 0){
					this.setSelectedIndex(lastSelectIndex);
				}
			}
			
		}else{
			setNoSelection();
		}

		
	}
	
	public void setNoSelection(){
		this.removeAll();
		JPanel panel = new JPanel();
		this.addTab("Base", panel);
		
	}
	public void keyTyped(KeyEvent e) {
		// do nothing
		}

	public void keyPressed(KeyEvent e) {		
		if (e.getSource() == nameTextField){			
			if (e.getKeyCode() == KeyEvent.VK_ENTER){
				String newName = nameTextField.getText();
				if (!data.getChildText("name").equals(newName)){
					boolean change = true;
					String type = data.getName();
					if(data.getName().equals("class") || data.getName().equals("object")){
						// checks whether there is already an object or class with that name
						Boolean hasSameName = null;
						try {
							XPath path = new JDOMXPath(type+"[lower-case(name)='"+ newName.toLowerCase() +"'" +
									"and @id!='"+ data.getAttributeValue("id") +"']");
							hasSameName = (Boolean)path.booleanValueOf(data.getParentElement());
						} catch (JaxenException e2) {			
							e2.printStackTrace();
						}
						
						if(hasSameName.booleanValue()){
							change = false;
							String message = "";
							if(type.equals("class")){
								message = "There is already a class named '"+
								newName +"'. A class name must be unique.";
							}
							else if(type.equals("object")){
								message = "There is already an object named '"+
								newName +"'. An object name must be unique.";
							}
							
							JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
									"<html><center>"+ message +"</center></html>",
									"Not Allowed Name",
									JOptionPane.WARNING_MESSAGE);
						}
						
						
						// checks the presence of "-" in the name
						if(change && data.getName().equals("class") &&
								newName.indexOf("-") > -1){
							change = false;
							JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
									"<html><center>The character \"-\" " +
									"can not be used.</center></html>",
									"Not Allowed Character",
									JOptionPane.WARNING_MESSAGE);								
							selectedCell.setUserObject(selectedCell.getData().getChildText("name"));							

						}
					}
					
					else if(newName.trim().equals("")){
						change = false;
						JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
								"<html><center>Empty names are not allowed.</center></html>",
								"Empty name",
								JOptionPane.WARNING_MESSAGE);						
					}
					
					if(change){
						// do the change
						data.getChild("name").setText(newName);
						if (selectedNode != null){
							selectedNode.setUserObject(data.getChildText("name"));
							DefaultTreeModel model = (DefaultTreeModel) ItSIMPLE.getInstance().getItTree().getModel();
							model.nodeChanged(selectedNode);
						}
						
						if (selectedCell != null){
							selectedCell.setUserObject(data.getChildText("name"));
							DefaultGraphModel model = (DefaultGraphModel) ((ItGraph)senderObject).getModel();
							Object[] changeds = new Object[1];
							changeds[0] = selectedCell;
							selectedCell.setVisual();
							model.cellsChanged(changeds);
						}
						else if (selectedAssociation != null){
							selectedAssociation.setUserObject(data.getChildText("name"));
							DefaultGraphModel model = (DefaultGraphModel) ((ItGraph)senderObject).getModel();
							Object[] changeds = new Object[1];
							changeds[0] = selectedAssociation;
							selectedAssociation.setVisual();
							model.cellsChanged(changeds);
						}
						
						// repaint open diagrams
						if(data.getName().equals("class") ||
								data.getName().equals("classAssociation")){
			            	ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();
			            	tabbed.repaintOpenDiagrams("repositoryDiagram");
			            	tabbed.repaintOpenDiagrams("objectDiagram");
						}
						
						else if(data.getName().equals("object") &&
								reference.getParentElement().getParentElement()
								.getName().equals("repositoryDiagram")){
							ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();			            	
			            	tabbed.repaintOpenDiagrams("objectDiagram");
						}
					}
					else{
						// retrieves the unchanged name
						nameTextField.setText(data.getChildText("name"));
					}
					
				}				
			}	
		}
		
		else if(e.getSource() == roleNameATextField && e.getKeyCode() == KeyEvent.VK_ENTER){
			
			Element associationEndA = null;
			try {
				XPath path = new JDOMXPath("associationEnds/associationEnd[@id=1]");
				associationEndA = (Element)path.selectSingleNode(data);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			if(associationEndA != null){
				String text = roleNameATextField.getText();
				if(text.indexOf("-") > -1){
					JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
							"<html><center>The character \"-\" " +
							"can not be used.</center></html>",
							"Not Allowed Character",
							JOptionPane.WARNING_MESSAGE);				
					roleNameATextField.setText(associationEndA.getChild("rolename").getChildText("value"));
				}
				else{

					if (selectedAssociation != null){
						associationEndA.getChild("rolename").getChild("value").setText(roleNameATextField.getText());
						selectedAssociation.setVisual();
						ItGraph graph = (ItGraph)senderObject;
						DefaultGraphModel model = (DefaultGraphModel) graph.getModel();
						Object[] changeds = new Object[1];
						changeds[0] = selectedAssociation;				
						model.cellsChanged(changeds);
						graph.repaintElement(selectedAssociation);
						
						// repaint open diagrams
						ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();						
						tabbed.repaintOpenDiagrams("repositoryDiagram");
						tabbed.repaintOpenDiagrams("objectDiagram");
					}
				}
			}
		}
		
		else if(e.getSource() == roleNameBTextField && e.getKeyCode() == KeyEvent.VK_ENTER){
			
			Element associationEndB = null;
			try {
				XPath path = new JDOMXPath("associationEnds/associationEnd[@id=2]");
				associationEndB = (Element)path.selectSingleNode(data);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			if(associationEndB != null){
				String text = roleNameBTextField.getText();
				if(text.indexOf("-") > -1){
					JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
							"<html><center>The character \"-\" " +
							"can not be used.</center></html>",
							"Not Allowed Character",
							JOptionPane.WARNING_MESSAGE);				
					roleNameBTextField.setText(associationEndB.getChild("rolename").getChildText("value"));
				}
				else{

					if (selectedAssociation != null){
						associationEndB.getChild("rolename").getChild("value").setText(roleNameBTextField.getText());
						selectedAssociation.setVisual();
						ItGraph graph = (ItGraph)senderObject;
						DefaultGraphModel model = (DefaultGraphModel) graph.getModel();
						Object[] changeds = new Object[1];
						changeds[0] = selectedAssociation;				
						model.cellsChanged(changeds);
						graph.repaintElement(selectedAssociation);
						
						// repaint open diagrams
						ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();
						tabbed.repaintOpenDiagrams("repositoryDiagram");
						tabbed.repaintOpenDiagrams("objectDiagram");
					}
				}
			}
		}
		
		else if(e.getSource() == objectDiagramTextField && e.getKeyCode() == KeyEvent.VK_ENTER){
			if (data != null){
				data.getChild("name").setText(objectDiagramTextField.getText());
				if (selectedNode != null){
					String sequenceReference = data.getChildText("sequenceReference");
					if (data.getChildText("sequenceReference").equals("")){
						selectedNode.setUserObject(data.getChildText("name"));
					} else {
						selectedNode.setUserObject(data.getChildText("name") + " - " + sequenceReference);
					}					
					DefaultTreeModel model = (DefaultTreeModel) ItSIMPLE.getInstance().getItTree().getModel();
					model.nodeChanged(selectedNode);					
				}
			}
			
		}
		
		else if(e.getSource() == operatorList && e.getKeyCode() == KeyEvent.VK_ENTER && 
				operatorList.getSelectedIndex() > -1){
			EditDialog edit = new EditDialog(currentOperators.get(operatorList.getSelectedIndex()), null, operatorList, PropertiesTabbedPane.this);
			edit.setVisible(true);
		}
		
	}

	public void keyReleased(KeyEvent e) {		
		if(e.getSource() == descriptionTextPane || e.getSource() == objectDiagramTextPane){
			data.getChild("description").setText(descriptionTextPane.getText());			
		}
		else if(e.getSource() == actionDescriptionTextPane){
			data.getChild("description").setText(actionDescriptionTextPane.getText());
		}
		
		else if(e.getSource() == conditionTextPane){
			data.getChild("condition").setText(conditionTextPane.getText());
			if (selectedCell != null){
				selectedCell.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				graph.repaint();				
			}	
		}
		else if(e.getSource() == preConditionTextPane){
			data.getChild("precondition").setText(preConditionTextPane.getText());
			if (selectedAssociation != null){
				selectedAssociation.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				graph.repaint();
			}	
		}		
		else if(e.getSource() == postConditionTextPane){
			data.getChild("postcondition").setText(postConditionTextPane.getText());
			if (selectedAssociation != null){
				selectedAssociation.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				graph.repaint();
			}	
		}
		
		else if(e.getSource() == useCasePreTextPane){
			data.getChild("definition").getChild("precondition").setText(useCasePreTextPane.getText());
		}
		else if(e.getSource() == useCasePosTextPane){
			data.getChild("definition").getChild("postcondition").setText(useCasePosTextPane.getText());
		}
		else if(e.getSource() == useCaseInvTextPane){
			data.getChild("definition").getChild("invariants").setText(useCaseInvTextPane.getText());
		}
		else if(e.getSource() == useCaseTempTextPane){
			data.getChild("definition").getChild("temporalConstraints").setText(useCaseTempTextPane.getText());
		}
		
		else if(e.getSource() == useCaseInfoTextPane){
			data.getChild("definition").getChild("additionalIformation").setText(useCaseInfoTextPane.getText());
		}
		
		else if(e.getSource() == useCaseIssueTextPane){
			data.getChild("definition").getChild("issues").setText(useCaseIssueTextPane.getText());
		}
		else if(e.getSource() == constraintsTextPane){
			data.getChild("constraints").setText(constraintsTextPane.getText());
		}
		
	}

	public void itemStateChanged(ItemEvent e) {
		
		if (e.getSource() == iconComboBox){
			if (e.getStateChange() == ItemEvent.SELECTED){
				data.getChild("graphics").getChild("icon").setText((String)iconComboBox.getDataItem(iconComboBox.getSelectedIndex()));
				//System.out.println(iconComboBox.getDataItem(iconComboBox.getSelectedIndex()));
				if (selectedCell != null){
					selectedCell.setVisual();
					ItGraph graph = (ItGraph)senderObject;
					graph.repaintElement(selectedCell);
				}
				
				// repaint open diagrams
            	ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();
            	tabbed.repaintOpenDiagrams("repositoryDiagram");
            	tabbed.repaintOpenDiagrams("objectDiagram");
			}	
		}
		else if (e.getSource() == classComboBox){
			if (e.getStateChange() == ItemEvent.SELECTED){
				if (classComboBox.getItemCount() > 1){
					if (data.getName().equals("object")){
						
											// objects				elements		domain
						Element domain = data.getParentElement().getParentElement().getParentElement();

						
						//if the user changed the class
						Element selectedClass = (Element)classComboBox.getDataItem(classComboBox.getSelectedIndex());
						if (additional != selectedClass){							
							
							if (selectedClass == null){	
								data.getChild("class").setText("");
								
								//1. clean up all object reference attributes
								List<?> result = null;
								try {
									XPath path = new JDOMXPath("repositoryDiagrams/repositoryDiagram/objects/object[@id="+data.getAttributeValue("id")+"] | " +
											"planningProblems/problem/objectDiagrams/objectDiagram/objects/object[@id="+data.getAttributeValue("id")+"]");
									result = path.selectNodes(domain);
								} catch (JaxenException e2) {			
									e2.printStackTrace();
								}
								
								for (int i = 0; i < result.size(); i++){
									Element object = (Element)result.get(i);
									object.getChild("attributes").removeContent();
								}
								
								
								//2. Clean up object association references
								result = null;
								try {
									XPath path = new JDOMXPath("repositoryDiagrams/repositoryDiagram/associations/objectAssociation[associationEnds/objectAssociationEnd/@element-id="+data.getAttributeValue("id")+"] | " +
											"planningProblems/problem/objectDiagrams/objectDiagram/associations/objectAssociation[associationEnds/objectAssociationEnd/@element-id="+data.getAttributeValue("id")+"]");
									result = path.selectNodes(domain);
								} catch (JaxenException e2) {			
									e2.printStackTrace();
								}
								
								Element associations = null;
								
								for (int i = 0; i < result.size(); i++){
									
									Element objectAssociation = (Element)result.get(i);
									// remove associations from graph
									ItGraph graph = (ItGraph)senderObject;
									Object deleteCell = graph.getAssociationFromData(objectAssociation);									
									if (deleteCell != null){
										Object[] deleteCells =  {deleteCell};
										graph.getGraphLayoutCache().remove(deleteCells, true, true);										
									}
									associations = objectAssociation.getParentElement();
									associations.removeContent(objectAssociation);
								}
								
								
								additional = selectedClass;	
								if (selectedCell != null){
									selectedCell.setAdditionalData(selectedClass);
									selectedCell.setVisual();
									ItGraph graph = (ItGraph)senderObject;
									graph.repaintElement(selectedCell);									
								}
								if (selectedNode != null){
									selectedNode.setAdditionalData(selectedClass);
								}
								
							}
							else {
								data.getChild("class").setText(selectedClass.getAttributeValue("id"));

								//1. Get all class parents and their attributes
								ArrayList<Element> parentList = new ArrayList<Element>();
								ArrayList<Element> attributeList = new ArrayList<Element>();
								boolean hasParent = true;
								Element parent = selectedClass;
								while (hasParent && parent != null){
									//check if it's not a primitive class
									if (!parent.getChildText("type").equals("Primitive")){
										parentList.add(parent);
										
										//1.1 List attributes
										Iterator<?> attributes = parent.getChild("attributes").getChildren("attribute").iterator();
										while(attributes.hasNext()){
											Element attribute = (Element)attributes.next();
											attributeList.add(attribute);										
										}	
										//1.2 Checks if there is a parent class
										if (!parent.getChild("generalization").getAttributeValue("id").equals("")){
											parent = XMLUtilities.getElement(parent.getParentElement(), parent.getChild("generalization").getAttributeValue("id"));
											hasParent = true;
										}
										else{
											hasParent = false;
											parent = null;
										}
									}
									else{
										hasParent = false;									
									}	
								}
								
								//2. Get all object and put the new attributes
								List<?> result = null;
								try {
									XPath path = new JDOMXPath("repositoryDiagrams/repositoryDiagram/objects/object[@id="+data.getAttributeValue("id")+"] | " +
											"planningProblems/problem/objectDiagrams/objectDiagram/objects/object[@id="+data.getAttributeValue("id")+"]");
									result = path.selectNodes(domain);
								} catch (JaxenException e2) {			
									e2.printStackTrace();
								}
								
								for (int i = 0; i < result.size(); i++){
									Element object = (Element)result.get(i);
									object.getChild("attributes").removeContent();
									//Build each attribute reference
									for (Iterator<?> iter = attributeList.iterator(); iter.hasNext();) {
										Element currentAtt = (Element) iter.next();
										
										Element attributeReference = (Element)commonData.getChild("definedNodes").getChild("elements")
										.getChild("references").getChild("attribute").clone();
										
										attributeReference.setAttribute("class", currentAtt.getParentElement().getParentElement().getAttributeValue("id"));
										attributeReference.setAttribute("id", currentAtt.getAttributeValue("id"));
										if (!currentAtt.getChildText("initialValue").equals("")){
											attributeReference.getChild("value").setText(currentAtt.getChildText("initialValue"));
										}
										object.getChild("attributes").addContent(attributeReference);
									}	
								}
								
								//3. Clean up object association references
								result = null;
								try {
									XPath path = new JDOMXPath("repositoryDiagrams/repositoryDiagram/associations/objectAssociation[associationEnds/objectAssociationEnd/@element-id="+data.getAttributeValue("id")+"] | " +
											"planningProblems/problem/objectDiagrams/objectDiagram/associations/objectAssociation[associationEnds/objectAssociationEnd/@element-id="+data.getAttributeValue("id")+"]");
									result = path.selectNodes(domain);
								} catch (JaxenException e2) {			
									e2.printStackTrace();
								}
								
								Element associations = null;
								for (int i = 0; i < result.size(); i++){
									Element objectAssociation = (Element)result.get(i);
									// remove associations from graph
									ItGraph graph = (ItGraph)senderObject;
									Object deleteCell = graph.getAssociationFromData(objectAssociation);									
									if (deleteCell != null){
										Object[] deleteCells =  {deleteCell};
										graph.getGraphLayoutCache().remove(deleteCells, true, true);										
									}
									associations = objectAssociation.getParentElement();
									associations.removeContent(objectAssociation);
									
								}
								
								additional = selectedClass;	
								if (selectedCell != null){
									selectedCell.setAdditionalData(selectedClass);
									selectedCell.setVisual();
									ItGraph graph = (ItGraph)senderObject;
									graph.repaintElement(selectedCell);
								}
								if (selectedNode != null){
									selectedNode.setAdditionalData(selectedClass);
								}								
							}
							
							//TODO repaint open diagrams
			            	ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();				            	
			            	tabbed.repaintOpenDiagrams("objectDiagram");
						}
						
						setObjectAttributePanel();
						
					}
					else if (data.getName().equals("stateMachineDiagram")){
						
						Element Class = (Element)classComboBox.getDataItem(classComboBox.getSelectedIndex());
						//1. get state chart diagram class
						if (Class == null){	
							data.getChild("class").setText("");
							if (selectedNode != null){
								selectedNode.setAdditionalData(null);
							}							
						}
						else {
							if (!Class.getAttributeValue("id").equals(data.getChildText("class"))){
								data.getChild("class").setText(Class.getAttributeValue("id"));
								if (selectedNode != null){
									selectedNode.setAdditionalData(Class);
								}
							}
						}	
					}
						
							
				}
		
			}			

		}		
		else if(e.getSource().equals(navigationA) && e.getStateChange() == ItemEvent.SELECTED){
			Element associationEndA = null;
			try {
				XPath path = new JDOMXPath("associationEnds/associationEnd[@id=1]");
				associationEndA = (Element)path.selectSingleNode(data);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			if (associationEndA != null && selectedAssociation != null){
				associationEndA.getAttribute("navigation").setValue(navigationA.getSelectedItem().toString());
				selectedAssociation.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				DefaultGraphModel model = (DefaultGraphModel) graph.getModel();
				Object[] changeds = new Object[1];
				changeds[0] = selectedAssociation;				
				model.cellsChanged(changeds);
				graph.repaintElement(selectedAssociation);
			}
			
		}
		else if(e.getSource().equals(aggregationA) && e.getStateChange() == ItemEvent.SELECTED){
			Element associationEndA = null;
			try {
				XPath path = new JDOMXPath("associationEnds/associationEnd[@id=1]");
				associationEndA = (Element)path.selectSingleNode(data);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			if (associationEndA != null && selectedAssociation != null){
				associationEndA.getChild("type").setText(aggregationA.getSelectedItem().toString());
				selectedAssociation.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				DefaultGraphModel model = (DefaultGraphModel) graph.getModel();
				Object[] changeds = new Object[1];
				changeds[0] = selectedAssociation;				
				model.cellsChanged(changeds);
				graph.repaintElement(selectedAssociation);
			}
			
		}
		
		else if(e.getSource().equals(multiplicityA) && e.getStateChange() == ItemEvent.SELECTED){
			Element associationEndA = null;
			try {
				XPath path = new JDOMXPath("associationEnds/associationEnd[@id=1]");
				associationEndA = (Element)path.selectSingleNode(data);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			if (associationEndA != null && selectedAssociation != null){
				associationEndA.getChild("multiplicity").getChild("value").setText(multiplicityA.getSelectedItem().toString());
				selectedAssociation.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				DefaultGraphModel model = (DefaultGraphModel) graph.getModel();
				Object[] changeds = new Object[1];
				changeds[0] = selectedAssociation;				
				model.cellsChanged(changeds);
				graph.repaintElement(selectedAssociation);
			}
			
		}
		
		else if(e.getSource().equals(navigationB) && e.getStateChange() == ItemEvent.SELECTED){
			Element associationEndB = null;
			try {
				XPath path = new JDOMXPath("associationEnds/associationEnd[@id=2]");
				associationEndB = (Element)path.selectSingleNode(data);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			if (associationEndB != null && selectedAssociation != null){
				associationEndB.getAttribute("navigation").setValue(navigationB.getSelectedItem().toString());
				selectedAssociation.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				DefaultGraphModel model = (DefaultGraphModel) graph.getModel();
				Object[] changeds = new Object[1];
				changeds[0] = selectedAssociation;				
				model.cellsChanged(changeds);
				graph.repaintElement(selectedAssociation);
			}
			
		}
		else if(e.getSource().equals(aggregationB) && e.getStateChange() == ItemEvent.SELECTED){
			Element associationEndB = null;
			try {
				XPath path = new JDOMXPath("associationEnds/associationEnd[@id=2]");
				associationEndB = (Element)path.selectSingleNode(data);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			if (associationEndB != null && selectedAssociation != null){
				associationEndB.getChild("type").setText((String)aggregationB.getSelectedItem());
				selectedAssociation.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				DefaultGraphModel model = (DefaultGraphModel) graph.getModel();
				Object[] changeds = new Object[1];
				changeds[0] = selectedAssociation;				
				model.cellsChanged(changeds);
				graph.repaintElement(selectedAssociation);
			}
			
		}
		
		else if(e.getSource().equals(multiplicityB) && e.getStateChange() == ItemEvent.SELECTED){
			Element associationEndA = null;
			try {
				XPath path = new JDOMXPath("associationEnds/associationEnd[@id=2]");
				associationEndA = (Element)path.selectSingleNode(data);
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			if (associationEndA != null && selectedAssociation != null){
				associationEndA.getChild("multiplicity").getChild("value").setText(multiplicityB.getSelectedItem().toString());
				selectedAssociation.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				DefaultGraphModel model = (DefaultGraphModel) graph.getModel();
				Object[] changeds = new Object[1];
				changeds[0] = selectedAssociation;				
				model.cellsChanged(changeds);
				graph.repaintElement(selectedAssociation);
			}
			
		}
		else if(e.getSource().equals(operatorComboBox) && e.getStateChange() == ItemEvent.SELECTED){
			Element operator = null;
			if (operatorComboBox.getItemCount() > 1 && selectedAssociation!= null){
				operator = (Element)operatorComboBox.getDataItem(operatorComboBox.getSelectedIndex());				
				if (operator != null){					
					data.getChild("reference").setAttribute("class",operator.getParentElement().getParentElement().getAttributeValue("id"));
					data.getChild("reference").setAttribute("operator",operator.getAttributeValue("id"));
					reference = operator;					
				}
				else{
					data.getChild("reference").setAttribute("class","");
					data.getChild("reference").setAttribute("operator","");
					reference = operator;					
				}
				selectedAssociation.setReference(reference);
				selectedAssociation.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				DefaultGraphModel model = (DefaultGraphModel) graph.getModel();
				Object[] changeds = new Object[1];
				changeds[0] = selectedAssociation;				
				model.cellsChanged(changeds);
				graph.repaintElement(selectedAssociation);
			}
			
						
		}
		
		else if (e.getSource() == stereotype && e.getStateChange() == ItemEvent.SELECTED){
			if (stereotype.getItemCount() > 1 && data != null){
				data.getChild("stereotype").setText(stereotype.getSelectedItem().toString());
				if (selectedCell != null){
					selectedCell.setVisual();
					ItGraph graph = (ItGraph)senderObject;
					graph.repaintElement(selectedCell);
				}
				
			}			
		}
		
		else if (e.getSource() == objectAssociationComboBox && e.getStateChange() == ItemEvent.SELECTED){
			if (objectAssociationComboBox.getItemCount() > 1 && data != null){
				
				Element association = (Element)objectAssociationComboBox.getDataItem(objectAssociationComboBox.getSelectedIndex());
				
				Element sourceObjectAssociationEnd = null;
				Element targetObjectAssociationEnd = null;
				Element sourceAssociationEnd = null;
				Element targetAssociationEnd = null;				
				Element sourceObject = null;
				Element targetObject = null;
				
				Element domain = null;
				Element diagram = data.getParentElement().getParentElement();
				if(diagram.getName().equals("repositoryDiagram")){
								//repositoryDiagrams	//domain
					domain = diagram.getParentElement().getParentElement();
				}
				else{
									//objectDiagrams	//problem		//planningProblems		//domain
					domain = diagram.getParentElement().getParentElement().getParentElement().getParentElement();
				}
				
				//1. Finding the Objects
				//1.1 Finding the source object
				try {
					XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id=1]");
					sourceObjectAssociationEnd = (Element)path.selectSingleNode(data);
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}

				try {
					XPath path = new JDOMXPath("elements/objects/object[@id="+sourceObjectAssociationEnd.getAttributeValue("element-id")+"]");
					sourceObject = (Element)path.selectSingleNode(domain);
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}

				//1.2 Finding the source object
				try {
					XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id=2]");
					targetObjectAssociationEnd = (Element)path.selectSingleNode(data);
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				try {
					XPath path = new JDOMXPath("elements/objects/object[@id="+targetObjectAssociationEnd.getAttributeValue("element-id")+"]");
					targetObject = (Element)path.selectSingleNode(domain);
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				
				
				//2 Get class source association End
				try {
					XPath path = new JDOMXPath("associationEnds/associationEnd[@id=1]");
					sourceAssociationEnd = (Element)path.selectSingleNode(association);
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				try {
					XPath path = new JDOMXPath("associationEnds/associationEnd[@id=2]");
					targetAssociationEnd = (Element)path.selectSingleNode(association);
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				//2.1 Check if the association ends remains ok otherwise change them
				if (!sourceAssociationEnd.getAttributeValue("element-id").equals(sourceObject.getChildText("class"))){
					sourceObjectAssociationEnd.setAttribute("element-id",targetObject.getAttributeValue("id"));
				}
				if (!targetAssociationEnd.getAttributeValue("element-id").equals(targetObject.getChildText("class"))){
					targetObjectAssociationEnd.setAttribute("element-id",sourceObject.getAttributeValue("id"));
				}					

				
				data.getChild("classAssociation").setText(association.getAttributeValue("id"));
				
				reference = association;
				
				if (selectedAssociation != null){
					selectedAssociation.setReference(association);
					
					selectedAssociation.setVisual();
					ItGraph graph = (ItGraph)senderObject;
					DefaultGraphModel model = (DefaultGraphModel) graph.getModel();
					Object[] changeds = new Object[1];
					changeds[0] = selectedAssociation;				
					model.cellsChanged(changeds);
					graph.repaintElement(selectedAssociation);						
					
				}
			}
		}
		else if (e.getSource() == sequenceReferenceComboBox && e.getStateChange() == ItemEvent.SELECTED){
			if (sequenceReferenceComboBox.getItemCount() > 1 && data != null){
				data.getChild("sequenceReference").setText(sequenceReferenceComboBox.getSelectedItem().toString());
				if (selectedNode != null){
					String sequenceReference = data.getChildText("sequenceReference");
					if (data.getChildText("sequenceReference").equals("")){
						selectedNode.setUserObject(data.getChildText("name"));
					} else {
						selectedNode.setUserObject(data.getChildText("name") + " - " + sequenceReference);
					}					
					DefaultTreeModel model = (DefaultTreeModel) ItSIMPLE.getInstance().getItTree().getModel();
					model.nodeChanged(selectedNode);
				}
			}
		}
		
		else if (e.getSource() == associationChangeability && e.getStateChange() == ItemEvent.SELECTED){
			if (associationChangeability.getItemCount() > 1 && data != null){
				data.getChild("changeability").getChild("value").setText((String)associationChangeability.getSelectedItem());
				
				selectedAssociation.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				DefaultGraphModel model = (DefaultGraphModel) graph.getModel();
				Object[] changeds = new Object[1];
				changeds[0] = selectedAssociation;				
				model.cellsChanged(changeds);
				graph.repaintElement(selectedAssociation);
				
			}			
		}
		
	}

	public void setIconList(){
//		 Look for all icons in a folder
		File fl = new File(iconsFilePath);

		String[] icons = fl.list(new IconFileFilter());
		iconComboBox.addItem("Unspecified...", "");
		for(int i=0;i < icons.length; i++){
			ImageIcon icon = new ImageIcon(iconsFilePath + icons[i]);
			icon = new ImageIcon(icon.getImage().getScaledInstance(24,24, Image.SCALE_DEFAULT));
			icon.setDescription(icons[i]);
			iconComboBox.addItem(icon, icons[i]);
		}
	}	

	public void repaintSelectedElement(){
		if (selectedCell != null){
			ItGraph graph = (ItGraph)senderObject;
			selectedCell.setVisual();
			graph.repaintElement(selectedCell);
		}
	}
	
	public void getSubClasses(Element Class, ArrayList<Element> list){
		
		if (Class != null){

				List<?> result = null;
				try {
					XPath path = new JDOMXPath("project/elements/classes/class[generalization/@id='"+Class.getAttributeValue("id")+"']");
					result = path.selectNodes(data.getDocument());
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				
				for (int i = 0; i < result.size(); i++){
					Element subClass = (Element)result.get(i);
					list.add(subClass);
					getSubClasses(subClass,list);
				}		
		}
	}
	
	public void getSuperClasses(Element Class, ArrayList<Element> list){
		
		if (Class != null){
				Element generalization = Class.getChild("generalization");
				if (!generalization.getAttributeValue("id").trim().equals("")){
					Element superClass = null;
					try {
						XPath path = new JDOMXPath("project/elements/classes/class[@id='"+generalization.getAttributeValue("id")+"']");
						superClass = (Element)path.selectSingleNode(data.getDocument());
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					if (superClass != null){
						list.add(superClass);
						getSubClasses(superClass,list);
					}	
					
				}	
		}
	}
	
	public void setBaseName(String text){
		nameTextField.setText(text);
	}
	
	//ACTIONS
	
	private Action newAttribute = new AbstractAction("New",new ImageIcon("resources/images/new.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -2864445237015276324L;

		public void actionPerformed(ActionEvent e) {
			if (data != null){
				//1. Place the attribute in the class 
				Element attribute = (Element)commonData.getChild("definedNodes").getChild("elements")
				.getChild("model").getChild("attribute").clone();
				String id = String.valueOf(XMLUtilities.getId(data.getChild("attributes")));
				attribute.getAttribute("id").setValue(id);
				attribute.getChild("name").setText("attribute"+id);
				data.getChild("attributes").addContent(attribute);				
				
				currentAttributes.add(attribute);			
				showAttribute(attribute);
				
				ItTreeNode classNode = ItSIMPLE.getInstance().getItTree().findNodeFromData(data.getDocument().getRootElement(), data);
				ItTreeNode attrNode = new ItTreeNode (attribute.getChildText("name"),attribute, null, null);
				attrNode.setIcon(new ImageIcon("resources/images/attribute.png"));
				DefaultTreeModel treeModel = (DefaultTreeModel)ItSIMPLE.getInstance().getItTree().getModel();
				treeModel.insertNodeInto(attrNode, classNode, classNode.getChildCount());
				
				//2. Get all Child classes
				ArrayList<Element> subClasses = new ArrayList<Element>();
				getSubClasses(data,subClasses);
				
				String xPathExpression = "class='"+data.getAttributeValue("id")+"'";
				
				if (subClasses.size() > 0){
					
					for (int i = 0; i < subClasses.size(); i++){
						Element subClass = subClasses.get(i);
						if (subClass != null){
							xPathExpression = xPathExpression + " or class='"+	subClass.getAttributeValue("id")+"'";
						}
						
					}
					
					
				}
				
				//3. Place the attribute in all object references of the class and its subclasses
				List<?> result = null;
				try {
					XPath path = new JDOMXPath("project/diagrams/planningDomains/domain/elements/objects/object["+xPathExpression+"]");
					result = path.selectNodes(data.getDocument());
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				
				List<?> resultReference = null;
				for (int i = 0; i < result.size(); i++){
					Element object = (Element)result.get(i);
										//   objects			elements			problem
					Element domain = object.getParentElement().getParentElement().getParentElement();
					resultReference = null;
					try {
						XPath path = new JDOMXPath("planningProblems/problem/objectDiagrams/objectDiagram/objects/object[@id='"+object.getAttributeValue("id")+"'] | " +
								"repositoryDiagrams/repositoryDiagram/objects/object[@id='"+object.getAttributeValue("id")+"']");
						resultReference = path.selectNodes(domain);
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					
					for (int j = 0; j < resultReference.size(); j++){
						Element objectReference = (Element)resultReference.get(j);
						Element objectAttribute = (Element)commonData.getChild("definedNodes").getChild("elements").getChild("references").getChild("attribute").clone();
						
						objectAttribute.setAttribute("class",data.getAttributeValue("id"));
						objectAttribute.setAttribute("id",attribute.getAttributeValue("id"));
						
						objectReference.getChild("attributes").addContent(objectAttribute);
					}
				}				
				
				
				
				
				if (selectedCell != null){
					ItGraph graph = (ItGraph)senderObject;
					selectedCell.setVisual();
					graph.repaintElement(selectedCell);
				}
				
				// repaint open diagrams
				ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();						
				tabbed.repaintOpenDiagrams("repositoryDiagram");
				tabbed.repaintOpenDiagrams("objectDiagram");
			}
		}
	};	
	
	private Action deleteAttribute = new AbstractAction("Delete",new ImageIcon("resources/images/delete.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -2864445237015276324L;

		public void actionPerformed(ActionEvent e) {
			if (data != null){
				int row = attributeTable.getSelectedRow();
				if (row > -1){
					DefaultTableModel tableModel = (DefaultTableModel)attributeTable.getModel();
					Element selectedAttribute = currentAttributes.get(row);
					
					//1. Delete all attribute in the object references
					List<?> result = null;
					try {
						XPath path = new JDOMXPath("project/diagrams/planningDomains/domain/descendant::attribute[@class='"+data.getAttributeValue("id")+
							"' and @id='"+selectedAttribute.getAttributeValue("id")+"']");								
						result = path.selectNodes(data.getDocument());
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					
					for (int i = 0; i < result.size(); i++){
						Element attribute = (Element)result.get(i);
						Element attributes = attribute.getParentElement();
						attributes.removeContent(attribute);
					}
					
					//delete current attribute from its class
					data.getChild("attributes").removeContent(selectedAttribute);
					tableModel.removeRow(row);
					currentAttributes.remove(row);
					
					ItTreeNode projectNode = ItSIMPLE.getInstance().getItTree().findProjectNode(data.getDocument().getRootElement());
					ItSIMPLE.getInstance().getItTree().deleteTreeNodeFromData(projectNode, selectedAttribute);
					
					if (selectedCell != null){
						selectedCell.setVisual();
						ItGraph graph = (ItGraph)senderObject;
						graph.repaintElement(selectedCell);
					}
					
					// repaint open diagrams
					ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();						
					tabbed.repaintOpenDiagrams("repositoryDiagram");
					tabbed.repaintOpenDiagrams("objectDiagram");
				}
			}
		}
	};
	
	private Action editAttribute = new AbstractAction("Edit", new ImageIcon("resources/images/edit.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 4592145959985121566L;

		public void actionPerformed(ActionEvent e) {			
			if (attributeTable.getSelectedRow() > -1){				
				EditDialog edit = new EditDialog(currentAttributes.get(attributeTable.getSelectedRow()), null, attributeTable, PropertiesTabbedPane.this);			
				
				edit.setVisible(true);
			}
		}
	};
	
	private Action newOperator = new AbstractAction("New",new ImageIcon("resources/images/new.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -2864445237015276324L;

		public void actionPerformed(ActionEvent e) {		
			if (data != null){
				Element operator = (Element)commonData.getChild("definedNodes").getChild("elements")
				.getChild("model").getChild("operator").clone();
				String id = String.valueOf(XMLUtilities.getId(data.getChild("operators")));
				operator.getAttribute("id").setValue(id);
				operator.getChild("name").setText(operator.getChildText("name")+id);
				data.getChild("operators").addContent(operator);
				
				currentOperators.add(operator);	
				operatorListModel.addElement(operator.getChildText("name"));
				
				ItTreeNode classNode = ItSIMPLE.getInstance().getItTree().findNodeFromData(data.getDocument().getRootElement(), data);
				ItTreeNode operatorNode = new ItTreeNode (operator.getChildText("name"),operator, null, null);
				operatorNode.setIcon(new ImageIcon("resources/images/operator.png"));
				DefaultTreeModel treeModel = (DefaultTreeModel)ItSIMPLE.getInstance().getItTree().getModel();
				treeModel.insertNodeInto(operatorNode, classNode, classNode.getChildCount());
				
				if (selectedCell != null){
					ItGraph graph = (ItGraph)senderObject;
					selectedCell.setVisual();
					graph.repaintElement(selectedCell);
				}
			}		
		}
	};	
	
	private Action deleteOperator = new AbstractAction("Delete",new ImageIcon("resources/images/delete.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -2864445237015276324L;

		public void actionPerformed(ActionEvent e) {					
			int row = operatorList.getSelectedIndex();
			if (row > -1){
				Element selectedOperator = currentOperators.get(row);
				data.getChild("operators").removeContent(selectedOperator);				
				currentOperators.remove(row);
				operatorListModel.removeElementAt(row);
				
				ItTreeNode projectNode = ItSIMPLE.getInstance().getItTree().findProjectNode(data.getDocument().getRootElement());
				ItSIMPLE.getInstance().getItTree().deleteTreeNodeFromData(projectNode, selectedOperator);
			}
			
			if (selectedCell != null){
				selectedCell.setVisual();
				ItGraph graph = (ItGraph)senderObject;
				graph.repaintElement(selectedCell);
			}
			
			// repaint open diagrams
			ItSIMPLE.getInstance().getItGraphTabbedPane().repaintOpenDiagrams("stateMachineDiagram");
		}
	};
	
	private Action editOperator = new AbstractAction("Edit", new ImageIcon("resources/images/edit.png")){
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -4586214182337070585L;

		public void actionPerformed(ActionEvent e) {			
			if (operatorList.getSelectedIndex() > -1){
				EditDialog edit = new EditDialog(currentOperators.get(operatorList.getSelectedIndex()), null, operatorList, PropertiesTabbedPane.this);
				edit.setVisible(true);
			}
		}
	};
	
	
	private Action newMetric = new AbstractAction("New", new ImageIcon("resources/images/new.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -2820018622516075618L;

		public void actionPerformed(ActionEvent e) {			
			
			Element newMetric = 
				(Element)ItSIMPLE.getCommonData().getChild("definedNodes")
				.getChild("elements").getChild("model").getChild("metric").clone();
			
			String id = String.valueOf(XMLUtilities.getId(data.getChild("metrics")));
			
			newMetric.setAttribute("id", id);
			
			newMetric.getChild("name").setText("metric"+id);
			newMetric.getChild("enabled").setText(String.valueOf(false));
			
			data.getChild("metrics").addContent(newMetric);
			
			setMetricsPanel();
			
			// select the recently added item
			metricsList.setSelectedIndex(metricsListModel.getSize()-1);
			
			editMetric.actionPerformed(null);
		}
	};
	
	private Action deleteMetric = new AbstractAction("Delete", new ImageIcon("resources/images/delete.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = -7885426542495703978L;

		public void actionPerformed(ActionEvent e) {			
			Object listItem = metricsList.getSelectedValue();
			
			if(listItem != null){
				Element metricData = 
					((JCheckBoxListItem)listItem).getData();
				
				metricsListModel.removeElement(listItem);
				metricData.detach();
			}
		}
	};
	
	private Action editMetric = new AbstractAction("Edit", new ImageIcon("resources/images/edit.png")){
		/**
		 * 
		 */
		private static final long serialVersionUID = 663096847536402295L;

		public void actionPerformed(ActionEvent e) {			
			// get metric data
			Object listItem = metricsList.getSelectedValue();
			
			if(listItem != null){
				Element metricData = 
					((JCheckBoxListItem)listItem).getData();
				EditMetricDialog dialog =
					new EditMetricDialog(metricData, (JCheckBoxListItem)listItem, metricsList);
				dialog.setVisible(true);
			}

		}
	};
	
	private Action openAttributeParameterValues = 
		new AbstractAction("Save to PDDL", new ImageIcon("resources/images/savePDDL.png")){


		/**
		 * 
		 */
		private static final long serialVersionUID = 3621108625062530508L;

		public void actionPerformed(ActionEvent e) {
			EditDialog edit = new EditDialog(currentObjectAttributes.get(objectAttributeTable.getSelectedRow()),
					currentAttributes.get(objectAttributeTable.getSelectedRow()), objectAttributeTable, PropertiesTabbedPane.this);
			edit.setVisible(true);
		}
	};
	
	private Action newLiteral = new AbstractAction("New",new ImageIcon("resources/images/new.png")){

		/**
		 * 
		 */
		private static final long serialVersionUID = 8630674137478388801L;

		public void actionPerformed(ActionEvent e) {
			if (data != null){
				//1. Place the attribute in the class 
				Element literal = (Element)commonData.getChild("definedNodes").getChild("elements")
				.getChild("model").getChild("literal").clone();
				String id = String.valueOf(XMLUtilities.getId(data.getChild("literals")));
				literal.getAttribute("id").setValue(id);
				literal.getChild("name").setText("literal"+id);
				data.getChild("literals").addContent(literal);				
				
				currentLiterals.add(literal);			
				showLiteral(literal);
				
				if (selectedCell != null){
					ItGraph graph = (ItGraph)senderObject;
					selectedCell.setVisual();
					graph.repaintElement(selectedCell);
				}
			}
		}
	};	
	
	private Action deleteLiteral = new AbstractAction("Delete",new ImageIcon("resources/images/delete.png")){

		/**
		 * 
		 */
		private static final long serialVersionUID = 4553229967206098112L;

		public void actionPerformed(ActionEvent e) {
			if (data != null){
				int row = literalsTable.getSelectedRow();
				if (row > -1){
					//DefaultTableModel tableModel = (DefaultTableModel)literalsTable.getModel();
					Element selectedLiteral = currentLiterals.get(row);
					
					//delete current attribute from its class
					data.getChild("literals").removeContent(selectedLiteral);
					literalsTableModel.removeRow(row);
					currentLiterals.remove(row);
					
					if (selectedCell != null){
						selectedCell.setVisual();
						ItGraph graph = (ItGraph)senderObject;
						graph.repaintElement(selectedCell);
					}
				}
			}
		}
	};
	

	public void tableChanged(TableModelEvent e) {
		if (e.getSource() == attributeTable.getModel()){
			int row = e.getFirstRow();
	        int column = e.getColumn();        
	        if(row > -1 && column > -1 && attributeType.getSelectedIndex() > -1){
	        	TableModel model = (TableModel)e.getSource();
	            Element selectedAttribute = currentAttributes.get(row);
	            String data = (String)model.getValueAt(row, column);
	            switch(column){
	            case 0:{// name
	            	
	            	// check the presence of "-"
	            	if(data.indexOf("-") > -1){
						JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
								"<html><center>The character \"-\" " +
								"can not be used.</center></html>",
								"Not Allowed Character",
								JOptionPane.WARNING_MESSAGE);	
						
						model.setValueAt(selectedAttribute.getChildText("name"), row, column);
	            	}
	            	
	            	selectedAttribute.getChild("name").setText(data);
	            	
	            	ItTreeNode attrNode = ItSIMPLE.getInstance().getItTree().findNodeFromData(selectedAttribute.getDocument().getRootElement(), selectedAttribute);
	            	attrNode.setUserObject(data);
	            	DefaultTreeModel treeModel = (DefaultTreeModel) ItSIMPLE.getInstance().getItTree().getModel();
	            	treeModel.nodeChanged(attrNode);
	            }
	            break;
	            case 1:{// type	            	
	            	Element Class = (Element)attributeType.getDataItem(attributeType.getSelectedIndex());
	            	if(Class != null){
	            		if(!Class.getAttributeValue("id").equals(selectedAttribute.getChildText("type"))){
	            			selectedAttribute.getChild("type").setText(Class.getAttributeValue("id"));
	    	            	// sets the initial value as null
	    	            	selectedAttribute.getChild("initialValue").setText("");
	    	            	model.setValueAt("",row,column+1);
	            		}            		
	            	}
	            	else{
	            		selectedAttribute.getChild("type").setText("");
	            	}
	            }
	            break;
	            case 2:{// initial value
	            	if (!data.trim().equals("")){
	            		//Check if it is a valid value only for the primitive types
		            	//Get the Attribute Type
		        		Element typeClass = null;
		    			try {
		    				XPath path = new JDOMXPath("project/elements/classes/class[@id="+selectedAttribute.getChildText("type")+"]");
		    				typeClass = (Element)path.selectSingleNode(selectedAttribute.getDocument());
		    			} catch (JaxenException e2) {			
		    				e2.printStackTrace();
		    			}
		    			//Check value
		    			if (typeClass != null){
		    				
		    				if (typeClass.getChildText("type").equals("Primitive")){
		    					int primitiveType = Integer.parseInt(typeClass.getAttributeValue("id"));
		    		            switch(primitiveType){
			    		            case 1:{// boolean
			    	            		data = ((String)model.getValueAt(row, column)).toLowerCase();
			    		            	if (data.equals("true") || data.equals("false")){
			    		            		selectedAttribute.getChild("initialValue").setText(data);
			    		            	}
			    		            	else{
			    		            		model.setValueAt("",row, column);
											JOptionPane.showMessageDialog(this,
													"<html><center>The entered value is an invalid boolean value.</center></html>",
													"Invalid Boolean Value",
													JOptionPane.WARNING_MESSAGE);
			    		            	}
			    		            }
			    		            break;
			    		            case 2:{// Integer			    		            	
			    		            	//int intType = Integer.parseInt(data);			    		            	
			    		            	selectedAttribute.getChild("initialValue").setText(data);
			    		            	
			    		            }
			    		            break;
			    		            case 3:{// Float
			    		            	selectedAttribute.getChild("initialValue").setText(data);
			    		            }
			    		            break;	
			    		            case 4:{// String
			    		            	selectedAttribute.getChild("initialValue").setText(data);
			    		            }
			    		            break;					    		            
		    		            
		    		            }
		    				}
		    				
		    				else{
		    					selectedAttribute.getChild("initialValue").setText(data);
		    				}
		    			}		    			
	            	}
	            	else
	            		selectedAttribute.getChild("initialValue").setText("");
	            }
	            break;
	            }
	        	if (selectedCell != null){
	        		ItGraph graph = (ItGraph) senderObject;        		
	            	selectedCell.setVisual();
	            	graph.repaintElement(selectedCell);
	            	
	            	// repaint open diagrams
	            	ItTabbedPane tabbed = ItSIMPLE.getInstance().getItGraphTabbedPane();
	            	tabbed.repaintOpenDiagrams("repositoryDiagram");
	            	tabbed.repaintOpenDiagrams("objectDiagram");	            	
	            }
	        } 
		}
		
		else if(e.getSource() == objectAttributeTable.getModel()){			
			int row = e.getFirstRow();
			int col = e.getColumn();
			if (row > -1 && col > -1){
	        	TableModel model = (TableModel)e.getSource();
	        	String strdata = (String) model.getValueAt(row, col);
	            Element selectedAttribute = currentAttributes.get(row);
	            Element selectedObjectAttribute = currentObjectAttributes.get(row);
	            				//object		objects			elements			domain
	            Element domain = data.getParentElement().getParentElement().getParentElement();
	            
	            //if the value is the same, do nothing
	            if(!selectedObjectAttribute.getChildText("value").equals(strdata)){
	            	//not parameterized attribute
	            	if (selectedAttribute.getChild("parameters").getChildren("parameter").size() <= 0){
	            		// repository diagram
			            if(selectedCell.getReference().getParentElement().getParentElement().getName().equals("repositoryDiagram")){
			            	selectedObjectAttribute.getChild("value").setText(strdata.trim());
			            	// set this value in all other object diagrams, if it's not empty
			            	if(!strdata.trim().equals("")){
				            	List<?> result = null;
								try {
									XPath path = new JDOMXPath("planningProblems/problem/objectDiagrams/objectDiagram/objects/object[@id='"+
											selectedCell.getData().getAttributeValue("id") +"']/attributes/attribute[@class='"+
											selectedObjectAttribute.getAttributeValue("class")+"' and @id='"+
											selectedObjectAttribute.getAttributeValue("id") +"']");									
									result = path.selectNodes(domain);
								} catch (JaxenException e2) {			
									e2.printStackTrace();
								}
								for (Iterator<?> iter = result.iterator(); iter.hasNext();) {
									Element attribute = (Element) iter.next();
									attribute.getChild("value").setText(strdata.trim());
								}
			            	}
			            }
			            else{
			            	// other object diagrams
			            	// look for the same attribute in the repository
			            	Element object = selectedObjectAttribute.getParentElement().getParentElement();
			            	//Element domain = object.getParentElement().getParentElement().getParentElement().getParentElement().getParentElement().getParentElement();
			            	Element repositoryAttribute = null;
							try {
								XPath path = new JDOMXPath("repositoryDiagrams/repositoryDiagram/objects/object[@id='"+ object.getAttributeValue("id")
										+"']/attributes/attribute[@class='"+ selectedObjectAttribute.getAttributeValue("class")+
										"' and @id='"+ selectedObjectAttribute.getAttributeValue("id") +"']");									
								repositoryAttribute = (Element)path.selectSingleNode(domain);
							} catch (JaxenException e2) {			
								e2.printStackTrace();
							}							
							if(repositoryAttribute != null){
				            	if(repositoryAttribute.getChildText("value").trim().equals("") ||
				            			repositoryAttribute.getChildText("value").trim().equals(strdata.trim()) ||
				            			strdata.trim().equals("")){
				            		// the value can be changed if it's null or if there is no value in the repository
				            		// or if the value is the same
				            		selectedObjectAttribute.getChild("value").setText(strdata.trim());
				            	}
				            	else{
				            		// if the value was set in the repository, it can't be changed
				            		JOptionPane.showMessageDialog(this,
											"<html><center>This value can't be changed since<br>it was defined in the Repository Diagram</center></html>",
											"Not Allowed Change",
											JOptionPane.WARNING_MESSAGE);			            		
				            		model.setValueAt(selectedObjectAttribute.getChildText("value"), row, col);
				            		objectAttributeValue.cancelCellEditing();
				            	}
							}
			            }
		            }
					
		        	if (selectedCell != null){
		        		ItGraph graph = (ItGraph) senderObject;        		
		            	selectedCell.setVisual();
		            	graph.repaintElement(selectedCell);
		            }
	            }
			}
		}
		
		else if(e.getSource() == literalsTableModel){
			int row = e.getFirstRow();
	        int column = e.getColumn();
	        if(row > -1 && column == 0){	        	
	            Element selectedLiteral = currentLiterals.get(row);
	            String name = (String)literalsTableModel.getValueAt(row, column);	           
	            	
            	// check the presence of "-"
            	if(name.indexOf("-") > -1){
					JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
							"<html><center>The character \"-\" " +
							"can not be used.</center></html>",
							"Not Allowed Character",
							JOptionPane.WARNING_MESSAGE);	
					
					literalsTableModel.setValueAt(selectedLiteral.getChildText("name"), row, column);
            	}
            	
            	selectedLiteral.getChild("name").setText(name);
            	
	        	if (selectedCell != null){
	        		ItGraph graph = (ItGraph) senderObject;        		
	            	selectedCell.setVisual();
	            	graph.repaintElement(selectedCell);
	            }
            }

		}

	}
	

	
	


	public void mouseClicked(MouseEvent e) {
		//Operator list
                if(e.getSource() == operatorList && e.getClickCount() == 2 &&
				operatorList.getSelectedIndex() > -1){			
			EditDialog edit = new EditDialog(currentOperators.get(operatorList.getSelectedIndex()), null, operatorList, PropertiesTabbedPane.this);
			edit.setVisible(true);			
		}
	}

	public void mouseEntered(MouseEvent e) {
		
	}

	public void mouseExited(MouseEvent e) {
		
	}

	public void mousePressed(MouseEvent e) {
		
	}

	public void mouseReleased(MouseEvent e) {
		
	}
       
	
}
