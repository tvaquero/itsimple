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
import java.awt.BorderLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

public class PlanActionDialog extends JDialog implements ItemListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3231218832140675884L;
	
	private JTextField startTimeTextField = null;
	private JTextField durationTextField = null;
	private ItComboBox actionComboBox = null;
	private DefaultTableModel parametersTableModel = null;
	private JTable parametersTable = null;
	
	private Element problem;
	private Element planAction;
	private Element xmlPlan;
	//private int actionIndex;
	
	public PlanActionDialog(Element problem, Element xmlPlan){
		this(problem, null, xmlPlan);
	}
	
	public PlanActionDialog(Element problem, Element planAction, Element xmlPlan){
		super(ItSIMPLE.getItSIMPLEFrame());
		this.problem = problem;
		this.planAction = planAction;
		this.xmlPlan = xmlPlan;
		//this.actionIndex = actionIndex;
		
		setTitle("Action Properties");
		setModal(true);
		setSize(200,250);
		setLocation(Toolkit.getDefaultToolkit().getScreenSize().width/4,Toolkit.getDefaultToolkit().getScreenSize().height/3);
		setContentPane(getMainPane());
		setMainPane();
	}
	
	private JPanel getMainPane(){
		// start time
		JLabel startTimeLabel = new JLabel("Start Time");
		startTimeTextField = new JTextField(4);
		startTimeTextField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
		
		// duration
		JLabel durationLabel = new JLabel("Duration");
		durationTextField = new JTextField(4);
		durationTextField.setDocument(new JTextFieldFilter(JTextFieldFilter.FLOAT));
		
		// action
		JLabel actionLabel = new JLabel("Action");
		actionComboBox = new ItComboBox();		
		
		// parameters
		parametersTableModel = new DefaultTableModel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 3708846077170790781L;

			public boolean isCellEditable(int row, int col){
				if(col == 0) return false;
				
				return true;
			}
		};
		parametersTable = new JTable(parametersTableModel);
		
		JScrollPane scroll = new JScrollPane(parametersTable);
		
		parametersTableModel.addColumn("Parameter");
		parametersTableModel.addColumn("Value");
		
		
		// tool bar
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(new JButton(cancelAction));
		buttonsPanel.add(new JButton(okAction));
		
		JPanel dataPanel = new JPanel(new SpringLayout());
		dataPanel.add(startTimeLabel);
		dataPanel.add(startTimeTextField);
		dataPanel.add(durationLabel);
		dataPanel.add(durationTextField);
		dataPanel.add(actionLabel);
		dataPanel.add(actionComboBox);
		SpringUtilities.makeCompactGrid(dataPanel,3,2,5,5,5,5);
		
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(dataPanel, BorderLayout.NORTH);
		mainPane.add(scroll, BorderLayout.CENTER);
		mainPane.add(buttonsPanel, BorderLayout.SOUTH);
		
		
		return mainPane;
	}
	
	private void setMainPane(){
		
		List operators = null;
		try {
			XPath path = new JDOMXPath("project/elements/classes/class/operators/operator");			
			operators = path.selectNodes(problem.getDocument());		
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		if(operators != null){
			for (Iterator iter = operators.iterator(); iter.hasNext();) {
				Element operator = (Element) iter.next();
				actionComboBox.addItem(operator.getChildText("name"), operator);
			}
		}
		
		if(planAction != null){
			//edit action case
			
			startTimeTextField.setText(planAction.getChildText("startTime"));
			//startTimeTextField.setEditable(false);
			
			durationTextField.setText(planAction.getChildText("duration"));
			
			//set the selected action
			Element operator = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class/operators/operator[lower-case(name)='"+
						planAction.getAttributeValue("id").toLowerCase()+"']");
				operator = (Element)path.selectSingleNode(problem.getDocument());
			} catch (JaxenException e) {				
				e.printStackTrace();
			}
			
			if(operator != null){
				actionComboBox.setSelectedItem(operator.getChildText("name"));
			}			
		}

		setParametersTable();
		actionComboBox.addItemListener(this);
	}
	
	//actions
	private Action cancelAction = new AbstractAction("Cancel"){
		/**
		 * 
		 */
		private static final long serialVersionUID = -6482058498634864954L;

		public void actionPerformed(ActionEvent e){
			PlanActionDialog.this.dispose();
		}
	};
	
	private Action okAction = new AbstractAction("OK"){
		/**
		 * 
		 */
		private static final long serialVersionUID = 3589539836054925724L;

		public void actionPerformed(ActionEvent e){
			
			// verify if all fields are filled in
			boolean unfilledParameter = false;
			for (int i = 0; i < parametersTableModel.getRowCount(); i++) {				
				String param = (String) parametersTableModel.getValueAt(i, 1);
				if(param.equals("")){
					unfilledParameter = true;
					break;
				}
			}
			
			if(startTimeTextField.getText().trim().equals("") ||
					durationTextField.getText().trim().equals("") ||
					unfilledParameter){
				JOptionPane.showMessageDialog(ItSIMPLE.getItSIMPLEFrame(),
						"<html><center>All fields must be filled in.</center></html>",
						"Unfilled fields",
						JOptionPane.WARNING_MESSAGE);
			}
			else{ 
				if(planAction == null){
					// add action case
				
					if(xmlPlan == null){
						try {
							xmlPlan = XMLUtilities.readFromFile("resources/planners/DefaultPlan.xml").getRootElement();
						} catch (Exception e1) {
	
							e1.printStackTrace();
						}		
					}
					
					if (xmlPlan != null){					
						// build up the action node
						planAction = new Element("action");
						
						// set name
						planAction.setAttribute("id", ((String)actionComboBox.getSelectedItem()).toUpperCase());
						
						// parameters
						Element parameters = new Element("parameters");
						for (int i = 0; i < parametersTableModel.getRowCount(); i++) {
							Element parameter = new Element("parameter");
							parameter.setAttribute("id", ((String) parametersTableModel.getValueAt(i, 1)).toUpperCase());
							parameters.addContent(parameter);				
						}
						planAction.addContent(parameters);
						
						// start time
						Element startTime = new Element("startTime");
						startTime.setText(startTimeTextField.getText());
						planAction.addContent(startTime);
						
						// duration
						Element duration = new Element("duration");
						duration.setText(durationTextField.getText());
						planAction.addContent(duration);
						
						// notes
						Element notes = new Element("notes");
						planAction.addContent(notes);
						
						// add the action to the plan
						xmlPlan.getChild("plan").addContent(planAction);					
					}								
				}
				else{
					// edit action case				
					// set name
					planAction.setAttribute("id", ((String)actionComboBox.getSelectedItem()).toUpperCase());
					
					//set start time
					planAction.getChild("startTime").setText(startTimeTextField.getText());
					
					//set duration
					planAction.getChild("duration").setText(durationTextField.getText());
					
					// set parameters
					List parameters = planAction.getChild("parameters").getChildren("parameter");
					int row = 0;
					for (Iterator iter = parameters.iterator(); iter.hasNext();) {
						Element parameter = (Element) iter.next();
						parameter.setText(((String)parametersTable.getValueAt(row++, 1)).toUpperCase());
					}				
				}
				
				
				// close the dialog and refresh the plan action list
				PlanActionDialog.this.dispose();
				ItSIMPLE.getInstance().setPlanList(xmlPlan);	
			}

		}
	};
	
	private void setParametersTable(){
		Element operator = (Element)actionComboBox.getDataItem(actionComboBox.getSelectedIndex());				
		parametersTableModel.setRowCount(0);
		
		//create the editor for each row
		TableColumn column = parametersTable.getColumnModel().getColumn(1);
		EachRowEditor editor = new EachRowEditor(parametersTable);
		column.setCellEditor(editor);
		
		int parameterIndex = 0;
		for (Iterator iterator = operator.getChild("parameters").getChildren("parameter").iterator(); iterator
		.hasNext();) {
			// set the parameter names
			Element parameter = (Element) iterator.next();
			String[] rowData = {parameter.getChildText("name"), ""};
			parametersTableModel.addRow(rowData);
			
			// set the parameter possible values in a combo box
			Element typeClass = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[@id='" + parameter.getChildText("type")+"']");
				typeClass = (Element)path.selectSingleNode(problem.getDocument());					
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			ItComboBox value = new ItComboBox();	
			//value.addItem("");
			Element domain = problem.getParentElement().getParentElement();
			

			//Get all descendent classes of typeClass
			List descendents = XMLUtilities.getClassDescendents(typeClass);
								
			//create the queries for xpath
			String descendentsQuery = "";
			
			for (Iterator iter = descendents.iterator(); iter.hasNext();) {
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
			
			List result = null;			
			try {					
				XPath path = new JDOMXPath("elements/objects/object["+ descendentsQuery +"]");
				result = path.selectNodes(domain);
				
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			if (result != null){
				Iterator objects = result.iterator();
				while(objects.hasNext()){
					Element object = (Element)objects.next();
					value.addItem(object.getChildText("name").toUpperCase());
				}
				
			
			}
			editor.setEditorAt(parameterIndex++, new DefaultCellEditor(value));
		}
		
		if(planAction != null){
			//set the parameters values
			List parameters = planAction.getChild("parameters").getChildren("parameter");
			int row = 0;
			for (Iterator iter = parameters.iterator(); iter.hasNext();) {
				Element parameter = (Element) iter.next();
				parametersTable.setValueAt(parameter.getAttributeValue("id"), row++, 1);
			}
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if(e.getSource() == actionComboBox && e.getStateChange() == ItemEvent.SELECTED){			
			setParametersTable();
		}
	}


}
