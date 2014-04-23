/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2009 Universidade de Sao Paulo
* 
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
* Authors:	Tiago S. Vaquero
**/

package itSIMPLE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
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
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SpringLayout;

import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;
import languages.xml.XMLUtilities;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

import sourceEditor.ItHilightedDocument;

/**
 * This class is the diolag for capturing (inserting) metrics and criteria about solution plans
 * @author tiago
 */
public class EditMetricAndCriteriaDialog extends JDialog implements TableModelListener{

		private static final long serialVersionUID = -6974543794597743033L;	
	
	private Element metricData; //xml node that hols the metric/criterion
	private JCheckBoxListItem item; //the select item of metrics list at itSIMPLE
        private JCheckBoxList metricsList; //the visual list element for repaiting purposes
        private Element domain; //xml node of the domain
        private Element problem; //xml node of the problem

        
        private JTabbedPane mainPane = null;
	
        // Base
	private ItPanel basePanel;
        private JTextField nameField;
	private JComboBox typeComboBox;
        private JTextField weightField;
	private JTextPane descriptionTextPane;
        
        //Variable
        private ItPanel variablesPanel;        
	private JTree variablesTree = null;
	private DefaultTreeModel variablesTreeModel;
        private JComboBox typeVarIntentionComboBox;
        
        //Free expression
        private ItPanel expressionPanel;
        private JTextPane functionPane;
        private JComboBox typeExpIntentionComboBox;

        //Single action counterression
        private ItPanel actionCounterPanel;
        private JComboBox operatorCounterListComboBox;
        private JComboBox typeActionCounterIntentionComboBox;
        private ItPanel parametersPanel = null;
	private DefaultTableModel parametersTableModel = null;
	private JTable parametersTable = null;
	private ItComboBox parameterObject = null;
	private ArrayList<Element> currentParameters = new ArrayList<Element>();
        private ArrayList<Element> originalOperatorList = new ArrayList<Element>();
                
        //Preference on the metric (functions that maps metric value to grades at interval [0,1]
        private ItPanel preferenceFunctionsPanel;
        private JTextField ruleField;
	private JToolBar preferenceFunctionsToolBar = null;
	private JList preferenceFunctionsList = null;
	private DefaultListModel preferenceFunctionsListModel = null;
	private ArrayList<Element> currentPreferenceFunctions =null;
        private ItPanel editAndNewFunctionPanel;
        private Element selectedFunctionData = null;
        private JTextField rule;
        private JComboBox lowerboundType;
        private JComboBox lowerboundValue;
        private JComboBox upperboundType;
        private JComboBox upperboundValue;        
        

	public EditMetricAndCriteriaDialog(Element metricData, JCheckBoxListItem item, JCheckBoxList list){
		super(ItSIMPLE.getItSIMPLEFrame());
		setTitle("Edit Metric & Criterion");
		setModal(true);
		
		this.metricData = metricData;
		this.item = item;
                this.metricsList = list;

                Element parent = metricData.getParentElement().getParentElement();

                //In case user selected a general metric (domain level)
                if (parent.getName().equals("domain")){
                  this.domain = parent;
                  this.problem = null;
                }
                //In case user selected a specific metrics (domain level)
                else{
                  this.domain = parent.getParentElement().getParentElement();
                  this.problem = parent;
                }


                //this.domain = metricData.getParentElement().getParentElement();
		
		setSize(500,500);
		setLocation(200,200);
		add(getMainPanel(), BorderLayout.CENTER);
                
                 //The OK button at the bottom
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				dispose();
			}
		});
		buttonPanel.add(okButton);
                add(buttonPanel, BorderLayout.SOUTH);
                
	}

        private ItPanel getBasePanel() {
		basePanel = new ItPanel(new BorderLayout());
		
		JPanel top = new JPanel(new SpringLayout());		
		top.add(new JLabel("Name: "));
		nameField = new JTextField(metricData.getChildText("name"));
		nameField.addKeyListener(new KeyAdapter(){
			@Override
                        //public void keyPressed(KeyEvent e) {
			public void keyReleased(KeyEvent e) { 
					metricData.getChild("name").setText(nameField.getText());
					item.setText(nameField.getText());
					item.revalidate();
                                        metricsList.repaint();
			}
		});
		top.add(nameField);
		
		top.add(new JLabel("Type: "));
		typeComboBox = new JComboBox();
		typeComboBox.addItem("");
		typeComboBox.addItem("expression");
                typeComboBox.addItem("variable");
		typeComboBox.addItem("actionCounter");
                //typeComboBox.addItem("state");
		//typeComboBox.addItem("action and state counter");                
		typeComboBox.setSelectedItem(metricData.getChildText("type"));
		typeComboBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
                                if (!metricData.getChildText("type").equals((String)typeComboBox.getSelectedItem())){
                                    String selection = (String)typeComboBox.getSelectedItem();
                                    metricData.getChild("type").setText(selection);
                                    
                                    setSelectedTypeTabs(selection);          
                                }
                                
			}
			
		});
			
		top.add(typeComboBox);

                top.add(new JLabel("Weight: "));

                if (metricData.getChild("weight") == null){
                   metricData.addContent(new Element("weight"));
                }
                weightField = new JTextField(metricData.getChildText("weight"));
		weightField.addKeyListener(new KeyAdapter(){
			@Override
                        //public void keyPressed(KeyEvent e) {
			public void keyReleased(KeyEvent e) {
					metricData.getChild("weight").setText(weightField.getText());
			}
		});
		top.add(weightField);    

		
		SpringUtilities.makeCompactGrid(top, 3, 2, 5, 5, 5, 5);
		
		JPanel bottom = new JPanel(new SpringLayout());
		bottom.add(new JLabel("Description:"));
                descriptionTextPane = new JTextPane();	
                descriptionTextPane.setText(metricData.getChildText("description"));
		descriptionTextPane.addKeyListener(new KeyAdapter(){
			@Override
                        public void keyReleased(KeyEvent e) {
				metricData.getChild("description").setText(descriptionTextPane.getText());
			}
		});
                bottom.add(new JScrollPane(descriptionTextPane));
		SpringUtilities.makeCompactGrid(bottom,2,1,5,5,5,5);
		
		basePanel.add(top, BorderLayout.NORTH);
		basePanel.add(bottom, BorderLayout.CENTER);     
		     
        return basePanel;
    }

        private ItPanel getVariablesPanel() {
        variablesPanel  = new ItPanel(new BorderLayout());
        
        JPanel top = new JPanel(new SpringLayout());
        top.add(new JLabel("Chose a variable:"));
        //Variable tree
        
        CheckBoxNode variablesTreeRoot = new CheckBoxNode("Objects");
	variablesTreeRoot.setIcon(new ImageIcon("resources/images/projects.png"));
	variablesTreeModel = new DefaultTreeModel(variablesTreeRoot);			
        variablesTree = new JTree(variablesTreeModel);
        variablesTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
        variablesTree.setShowsRootHandles(true);
        variablesTree.setCellRenderer(new CheckBoxNodeRenderer());
        variablesTree.setCellEditor(new CheckBoxNodeEditor(variablesTree));
        variablesTree.setEditable(true);
        
        top.add(new JScrollPane(variablesTree));
        SpringUtilities.makeCompactGrid(top,2,1,5,5,5,5);

//        ItTreeNode selectionTreeRoot = new ItTreeNode("Selections");		
//        selectionTreeRoot.setIcon(new ImageIcon("resources/images/projects.png"));
//        selectedVariablesPlanTreeModel = new DefaultTreeModel(selectionTreeRoot);
//        selectedVariablesPlanTree = new JTree(selectedVariablesPlanTreeModel);
//        selectedVariablesPlanTree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
         //get the selected nodes in variables plan tree and add it to selectedVariablesPlanTree			
	variablesTree.addMouseListener(new MouseListener(){
				public void mouseClicked(MouseEvent e) {
					TreePath path = variablesTree.getPathForLocation(e.getX(), e.getY());
					if(path != null){
                                            
                                            ItTreeNode selectedNode = (ItTreeNode)variablesTree.getLastSelectedPathComponent();
                                            if(selectedNode != null && selectedNode.getLevel() == 3){
                                                CheckBoxNode objectNode = (CheckBoxNode)selectedNode.getParent().getParent();
                                                String selectedNodeName = objectNode.getData().getChildText("name")+ "." +selectedNode.getData().getChildText("name");
                                                //System.out.println(nodeName);
                                                
                                                // get checked nodes
						Object[] checked = CheckBoxNode.getCheckedNodes(
								(CheckBoxNode)variablesTree.getModel().getRoot());
                                                //unselected them
						for (int i = 0; i < checked.length; i++) {							
                                                    CheckBoxNode node = (CheckBoxNode)checked[i];
                                                    objectNode = (CheckBoxNode)node.getParent().getParent();
                                                    String nodeName = objectNode.getData().getChildText("name")+ "." +node.getData().getChildText("name");
                                                    
                                                    if (!selectedNodeName.equals(nodeName)){
                                                        node.setSelected(false);
                                                    }   
						}
                                                Element chosenVariable = metricData.getChild("variable").getChild("chosenVariable");
                                                Element chosenObject = chosenVariable.getChild("object");
                                                Element chosenAttribute = chosenObject.getChild("attribute");
                                                chosenVariable.setAttribute("type","attr");
                                                chosenObject.setAttribute("id",objectNode.getData().getAttributeValue("id"));
                                                chosenObject.setAttribute("class",objectNode.getData().getChildText("class"));                                                
                                                chosenAttribute.setAttribute("type", selectedNode.getData().getChildText("type"));
                                                chosenAttribute.setAttribute("class", selectedNode.getData().getParentElement().getParentElement().getAttributeValue("id"));
                                                chosenAttribute.setAttribute("id", selectedNode.getData().getAttributeValue("id"));
                                                
                                                //XMLUtilities.printXML(selectedNode.getData());                                                
                                            }
                                            variablesTree.repaint();
					}
				}

				public void mouseEntered(MouseEvent arg0) {}
				public void mouseExited(MouseEvent arg0) {}
				public void mousePressed(MouseEvent arg0) {}
				public void mouseReleased(MouseEvent arg0) {}
			});
                        
        buildVariableTree();
        
        
        JPanel botton = new JPanel(new SpringLayout());
        botton.add(new JLabel("Intention: "));
	typeVarIntentionComboBox = new JComboBox();
        typeVarIntentionComboBox.addItem(""); 
        typeVarIntentionComboBox.addItem("observe");          
        typeVarIntentionComboBox.addItem("minimize");
        typeVarIntentionComboBox.addItem("maximize");           
	typeVarIntentionComboBox.setSelectedItem(metricData.getChildText("intention"));
        typeVarIntentionComboBox.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                        String selection = (String)typeVarIntentionComboBox.getSelectedItem();
				metricData.getChild("intention").setText(selection);
                }

        });
        typeVarIntentionComboBox.setSize(20, 21);
	botton.add(typeVarIntentionComboBox);
        SpringUtilities.makeCompactGrid(botton,2,1,5,5,5,5);
        
        variablesPanel.add(top, BorderLayout.CENTER); 
        variablesPanel.add(botton, BorderLayout.SOUTH);        
        return variablesPanel;
    }
    
        private ItPanel getExpressionPanel() {
            expressionPanel = new ItPanel(new BorderLayout());
            JPanel top = new JPanel(new SpringLayout());
            top.add(new JLabel("Expression (OCL):"));
            ItHilightedDocument document = new ItHilightedDocument();
            document.setHighlightStyle(ItHilightedDocument.OCL_STYLE);
            functionPane = new JTextPane(document);
            functionPane.setText(metricData.getChild("expression").getChildText("rule"));
            functionPane.addKeyListener(new KeyAdapter(){
                    @Override
                    public void keyReleased(KeyEvent e) {
                            metricData.getChild("expression").getChild("rule").setText(functionPane.getText());
                    }
            });
            top.add(new JScrollPane(functionPane));
            SpringUtilities.makeCompactGrid(top,2,1,5,5,5,5);

            JPanel botton = new JPanel(new SpringLayout());
            botton.add(new JLabel("Intention: "));
            typeExpIntentionComboBox = new JComboBox();
            typeExpIntentionComboBox.addItem("");
            typeExpIntentionComboBox.addItem("observe");
            typeExpIntentionComboBox.addItem("minimize");
            typeExpIntentionComboBox.addItem("maximize");
            typeExpIntentionComboBox.setSelectedItem(metricData.getChildText("intention"));
            typeExpIntentionComboBox.addItemListener(new ItemListener(){
                    public void itemStateChanged(ItemEvent e) {
                            String selection = (String)typeExpIntentionComboBox.getSelectedItem();
                                    metricData.getChild("intention").setText(selection);
                    }

            });
            typeExpIntentionComboBox.setSize(20, 21);
            botton.add(typeExpIntentionComboBox);
            SpringUtilities.makeCompactGrid(botton,2,1,5,5,5,5);

            expressionPanel.add(top, BorderLayout.CENTER);
            expressionPanel.add(botton, BorderLayout.SOUTH);


            return expressionPanel;
        }


        private ItPanel getActionCounterPanel() {
            actionCounterPanel = new ItPanel(new BorderLayout());


            //1.check if there is a node of actionCounter in the selected quality metric
            if (metricData.getChild("actionCounter") == null){
                Element newActionCounter = (Element)ItSIMPLE.getCommonData().getChild("definedNodes")
				.getChild("elements").getChild("model").getChild("qualityMetric").getChild("actionCounter").clone();

                metricData.addContent(newActionCounter);
            }

            Element chosenOperator = metricData.getChild("actionCounter").getChild("chosenOperator");

            //2. make the panel
            JPanel top = new JPanel(new SpringLayout());
            top.add(new JLabel("Select operator:"));
            operatorCounterListComboBox = new JComboBox();
            //fill out the combox
            operatorCounterListComboBox.addItem("");
            originalOperatorList.add(null);
            
            List<Element> operators = null;
            try {
                    XPath path = new JDOMXPath("project/elements/classes/class/operators/operator");
                    operators = path.selectNodes(metricData.getDocument());								

            } catch (JaxenException e1) {			
                    e1.printStackTrace();
            }
            for (Iterator<Element> it = operators.iterator(); it.hasNext();) {
                Element eachOperator = it.next();
                Element opClass = eachOperator.getParentElement().getParentElement();
                String label = opClass.getChildText("name") + "::" + eachOperator.getChildText("name");
                operatorCounterListComboBox.addItem(label);
                originalOperatorList.add(eachOperator);
            }

            operatorCounterListComboBox.addItemListener(new ItemListener(){
                    public void itemStateChanged(ItemEvent e) {
                        //TODO:
                        Element selected = originalOperatorList.get(operatorCounterListComboBox.getSelectedIndex());
                        //XMLUtilities.printXML(selected);
                        Element chosenOperator = metricData.getChild("actionCounter").getChild("chosenOperator");
                        if (selected != null){
                            Element theClass = selected.getParentElement().getParentElement();
                            //check if it changed
                            if(!chosenOperator.getAttributeValue("class").equals(theClass.getAttributeValue("id")) || !chosenOperator.getAttributeValue("id").equals(selected.getAttributeValue("id"))){
                                setChosenOperator(selected);
                            }

                        }else{
                            setChosenOperator(null);
                        }
                            //String selection = (String)operatorCounterListComboBox.getSelectedItem();
                            //metricData.getChild("intention").setText(selection);
                    }

            });
            top.add(operatorCounterListComboBox);

            //ADD parameters table
            top.add(new JLabel("Parameters:"));
            // Table
            parametersTableModel = new DefaultTableModel(){
			/**
			 *Define what column is editable
			 */
			// only the object column is editable
                        @Override
			public boolean isCellEditable(int row, int col) {
		        if (col == 1) {
		            return true;
		        }
		        else {
		            return false;
		        }
		    }
            };
            parametersTableModel.addTableModelListener(this);
            parametersTable = new JTable(parametersTableModel);

            // set size and add scrollPane
            parametersTable.setRowHeight(20);
            JScrollPane scrollText = new JScrollPane();
            scrollText.setViewportView(parametersTable);

            //Name column
            parametersTableModel.addColumn("Name");

            // Type column
            parametersTableModel.addColumn("Object");

            parameterObject = new ItComboBox();
            parameterObject.addItem("Any",null);
            List<?> result = null;
            try {
                    XPath path = new JDOMXPath("elements/objects/object");
                    result = path.selectNodes(domain);
            } catch (JaxenException e2) {
                    e2.printStackTrace();
            }

            for (int i = 0; i < result.size(); i++){
                    Element obj = (Element)result.get(i);
                    parameterObject.addItem(obj.getChildText("name"),obj);
            }


            TableColumn type = parametersTable.getColumnModel().getColumn(1);
            type.setCellEditor(new DefaultCellEditor(parameterObject));

            top.add(scrollText);
            SpringUtilities.makeCompactGrid(top,4,1,5,5,5,5);


            //make the initial selection
            //check if there is something (operator) already specified
            if (!chosenOperator.getAttributeValue("class").equals("")){
                 //look for the class and operator
                String currentLabel = "";

                Element opt = null;
                try {
                    XPath path = new JDOMXPath("project/elements/classes/class[@id='"+chosenOperator.getAttributeValue("class")+"']/operators/operator[@id='"+chosenOperator.getAttributeValue("id")+"']");
                    opt = (Element)path.selectSingleNode(domain.getDocument());
                } catch (JaxenException e2) {
                    e2.printStackTrace();
                }
                if (opt != null){
                            Element theClass = opt.getParentElement().getParentElement();
                            currentLabel = theClass.getChildText("name") +"::"+  opt.getChildText("name");
                            operatorCounterListComboBox.setSelectedItem(currentLabel);
                }
                 // fill out parameters
                currentParameters.clear();
                Iterator<?> parameters = chosenOperator.getChild("parameters").getChildren("parameter").iterator();
                while (parameters.hasNext()){
                        Element parameter = (Element)parameters.next();
                        currentParameters.add(parameter);
                        showParameter(parameter);
                }
            }

            JPanel botton = new JPanel(new SpringLayout());
            botton.add(new JLabel("Intention: "));
            typeActionCounterIntentionComboBox = new JComboBox();
            typeActionCounterIntentionComboBox.addItem("");
            typeActionCounterIntentionComboBox.addItem("observe");
            //typeActionCounterIntentionComboBox.addItem("minimize");
            //typeActionCounterIntentionComboBox.addItem("maximize");
            typeActionCounterIntentionComboBox.setSelectedItem(metricData.getChildText("intention"));
            typeActionCounterIntentionComboBox.addItemListener(new ItemListener(){
                    public void itemStateChanged(ItemEvent e) {
                            String selection = (String)typeActionCounterIntentionComboBox.getSelectedItem();
                                    metricData.getChild("intention").setText(selection);
                    }

            });
            typeActionCounterIntentionComboBox.setSize(20, 21);
            botton.add(typeActionCounterIntentionComboBox);
            SpringUtilities.makeCompactGrid(botton,2,1,5,5,5,5);

            actionCounterPanel.add(top, BorderLayout.CENTER);
            actionCounterPanel.add(botton, BorderLayout.SOUTH);


            return actionCounterPanel ;
        }


        private ItPanel getPreferenceFunctionsPanel() {
            preferenceFunctionsPanel  = new ItPanel(new BorderLayout());

		//preferenceFunctionsPanel.setPreferredSize(new Dimension(250,200));			

		ItPanel top = new ItPanel(new BorderLayout());
		ItPanel bottom = new ItPanel(new BorderLayout());
                ItPanel listPanel = new ItPanel(new BorderLayout());
                
                top.add(new JLabel("Function and intervals:"), BorderLayout.NORTH);
                
		//Function tree
                preferenceFunctionsListModel = new DefaultListModel();		
		preferenceFunctionsList = new JList(preferenceFunctionsListModel);		
		ItListRenderer renderer = new ItListRenderer();	
                renderer.setIcon(new ImageIcon("resources/images/operator.png"));
		preferenceFunctionsList.setCellRenderer(renderer);

                preferenceFunctionsList.addListSelectionListener(new ListSelectionListener(){
                        public void valueChanged(ListSelectionEvent e) {
                            // When the user release the mouse button and completes the selection,
                            // getValueIsAdjusting() becomes false
                            if (!e.getValueIsAdjusting()) {
                                if (preferenceFunctionsList.getSelectedIndex() > -1){
                                    Element selected = currentPreferenceFunctions.get(preferenceFunctionsList.getSelectedIndex());
                                    if (selected != null){
                                        selectedFunctionData = selected;
                                        //show at the editAndNewFunctionPanel
                                        fillEditFunctionPanel();
                                        editAndNewFunctionPanel.setVisible(true);
                                    }                                    
                                }
  
                            }
                        }
                });
                
                
		JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(preferenceFunctionsList);
                

                builPreferenceFunctionList();	
		top.add(scrollText, BorderLayout.CENTER);		
                
                //tool bar with buttons
                preferenceFunctionsToolBar = new JToolBar();
		preferenceFunctionsToolBar.add(newPreferenceFunction).setToolTipText("New function interval");
		preferenceFunctionsToolBar.add(deletePreferenceFunction).setToolTipText("Delete function interval");

		bottom.add(preferenceFunctionsToolBar, BorderLayout.NORTH);
                
		
                listPanel.add(top, BorderLayout.CENTER);
                listPanel.add(bottom, BorderLayout.SOUTH);
                
                // Edit Function panel
                //The function/expression itself
                editAndNewFunctionPanel = new ItPanel(new BorderLayout());
                editAndNewFunctionPanel.setPreferredSize(new Dimension(250,60));	
                editAndNewFunctionPanel.add(new JLabel("Function: f(x)="), BorderLayout.WEST);
                rule =  new JTextField();
                rule.addKeyListener(new KeyAdapter(){
			@Override
                        //public void keyPressed(KeyEvent e) {
			public void keyReleased(KeyEvent e) { 
                            selectedFunctionData.getChild("rule").setText(rule.getText());
                            preferenceFunctionsListModel.set(preferenceFunctionsList.getSelectedIndex(), getFunctionLabel(selectedFunctionData));
			}
		});
                
                editAndNewFunctionPanel.add(rule, BorderLayout.CENTER);
                
                
                //The Intervals
                JPanel bounds = new JPanel(new SpringLayout());	
                JLabel intervalLabel = new JLabel("Interval: ");
                
                lowerboundType = new JComboBox();
		lowerboundType.addItem("[");
		lowerboundType.addItem("(");
                lowerboundType.addItemListener(new ItemListener(){
                        public void itemStateChanged(ItemEvent e) {
                                String selection = (String)lowerboundType.getSelectedItem();
                                if (selection.equals("[")){
                                    selectedFunctionData.getChild("domain").getChild("lowerbound").setAttribute("included","true");
                                }else{
                                   selectedFunctionData.getChild("domain").getChild("lowerbound").setAttribute("included","false"); 
                                }
                                preferenceFunctionsListModel.set(preferenceFunctionsList.getSelectedIndex(), getFunctionLabel(selectedFunctionData));
                        }

                });
                              
                lowerboundValue = new JComboBox();
		lowerboundValue.addItem("");
		lowerboundValue.addItem("0");
                lowerboundValue.addItem("-1");
                lowerboundValue.addItem("1");
                lowerboundValue.addItem("-inf"); 
                lowerboundValue.setEditable(true);
                lowerboundValue.addItemListener(new ItemListener(){
                        public void itemStateChanged(ItemEvent e) {
                                String selection = (String)lowerboundValue.getSelectedItem();
                                selectedFunctionData.getChild("domain").getChild("lowerbound").setAttribute("value",selection);
                                preferenceFunctionsListModel.set(preferenceFunctionsList.getSelectedIndex(), getFunctionLabel(selectedFunctionData));
                        }

                });
                
                JLabel commaLabel = new JLabel(" , ");
                
                upperboundType = new JComboBox();
		upperboundType.addItem("]");
		upperboundType.addItem(")");
                upperboundType.addItemListener(new ItemListener(){
                        public void itemStateChanged(ItemEvent e) {
                                String selection = (String)upperboundType.getSelectedItem();
                                if (selection.equals("]")){
                                    selectedFunctionData.getChild("domain").getChild("upperbound").setAttribute("included","true");
                                }else{
                                    selectedFunctionData.getChild("domain").getChild("upperbound").setAttribute("included","false"); 
                                }
                                preferenceFunctionsListModel.set(preferenceFunctionsList.getSelectedIndex(), getFunctionLabel(selectedFunctionData));
                        }

                });                
                              
                upperboundValue = new JComboBox();
		upperboundValue.addItem("");
		upperboundValue.addItem("0");
                upperboundValue.addItem("-1");
                upperboundValue.addItem("1");
                upperboundValue.addItem("+inf");
                upperboundValue.setEditable(true);
                upperboundValue.addItemListener(new ItemListener(){
                        public void itemStateChanged(ItemEvent e) {
                                String selection = (String)upperboundValue.getSelectedItem();
                                selectedFunctionData.getChild("domain").getChild("upperbound").setAttribute("value",selection);
                                preferenceFunctionsListModel.set(preferenceFunctionsList.getSelectedIndex(), getFunctionLabel(selectedFunctionData));
                        }

                });
                
                bounds.add(intervalLabel);
                bounds.add(lowerboundType);
                bounds.add(lowerboundValue);
                bounds.add(commaLabel);
                bounds.add(upperboundValue);               
                bounds.add(upperboundType);
                
                
                SpringUtilities.makeCompactGrid(bounds, 1, 6, 5, 5, 5, 5);
                editAndNewFunctionPanel.add(bounds, BorderLayout.SOUTH);
                
                
                editAndNewFunctionPanel.setVisible(false);
                
                preferenceFunctionsPanel.add(listPanel, BorderLayout.CENTER);
                preferenceFunctionsPanel.add(editAndNewFunctionPanel, BorderLayout.SOUTH);
                
               
        
        return preferenceFunctionsPanel;
    }
    
	
        private JTabbedPane getMainPanel(){
            if (mainPane == null){			
                mainPane = new JTabbedPane();
                
                //build components
                getBasePanel();
                getExpressionPanel();
                getVariablesPanel();
                getActionCounterPanel();
                getPreferenceFunctionsPanel();                
                
                //get the type of metric and arrange the tabs for it;
                String type = metricData.getChildText("type");
                mainPane.addTab("Base", basePanel);
                setSelectedTypeTabs(type);                
            }	
		return mainPane;
	}


        private void buildVariableTree (){
        
        CheckBoxNode variablesTreeRoot = (CheckBoxNode)variablesTreeModel.getRoot();
						
        // delete old tree nodes
        if(variablesTreeRoot.getChildCount() > 0){
                variablesTreeRoot = new CheckBoxNode("Objects");
                variablesTreeRoot.setIcon(new ImageIcon("resources/images/projects.png"));
                variablesTreeModel.setRoot(variablesTreeRoot);
                variablesTreeModel.reload();
        }

        
        String chosenObjectID = "";
        String chosenObjectClassID = "";        
        String chosenAttributeID = "";
        String chosenAttributeClassID = "";
        chosenObjectID = metricData.getChild("variable").getChild("chosenVariable").getChild("object").getAttributeValue("id");
        chosenAttributeID = metricData.getChild("variable").getChild("chosenVariable").getChild("object").getChild("attribute").getAttributeValue("id");
        chosenObjectClassID = metricData.getChild("variable").getChild("chosenVariable").getChild("object").getAttributeValue("class");
        chosenAttributeClassID = metricData.getChild("variable").getChild("chosenVariable").getChild("object").getChild("attribute").getAttributeValue("class");  
        boolean sameSelectedObject = false;
        boolean sameVariableFound = false;
        
        
        // build the variables tree

        List<?> objects = domain.getChild("elements").getChild("objects").getChildren("object");
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
                             

                sameSelectedObject = false;
                if (object.getAttributeValue("id").equals(chosenObjectID) && object.getChildText("class").equals(chosenObjectClassID)){
                    sameSelectedObject = true;
                    //System.out.println("Found object");
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
                                //variablesTreeModel.insertNodeInto(attributeNode, attributesNode, attributesNode.getChildCount());
                                if (sameSelectedObject && !sameVariableFound){
                                    if(attribute.getAttributeValue("id").equals(chosenAttributeID) && objectClass.getAttributeValue("id").equals(chosenAttributeClassID)){
                                        attributeNode.setSelected(true);
                                        sameVariableFound = true;
                                    }
                                }
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
                                        //variablesTreeModel.insertNodeInto(attributeNode, attributesNode, attributesNode.getChildCount());
                                        
                                        if (sameSelectedObject && !sameVariableFound){
                                            if(attribute.getAttributeValue("id").equals(chosenAttributeID) && parentClass.getAttributeValue("id").equals(chosenAttributeClassID)){
                                                attributeNode.setSelected(true);
                                                sameVariableFound = true;
                                            }
                                        }                                              
                                }

                        }

                }							


                // only add attributes node if the object has attributes
                if(attributes.size() > 0){
                        CheckBoxNode attributesNode = new CheckBoxNode("Attributes");
                        attributesNode.setIcon(new ImageIcon("resources/images/attribute.png"));
                        variablesTreeModel.insertNodeInto(attributesNode, 
                                        objectNode, objectNode.getChildCount());

                        for (Iterator<CheckBoxNode> iterator = attributes.iterator(); iterator
                                        .hasNext();) {
                                CheckBoxNode attributeNode = iterator.next();
                                variablesTreeModel.insertNodeInto(attributeNode, attributesNode, attributesNode.getChildCount());
                        }
                }

                // if the object node is not a leaf, add it to the tree
                if(!objectNode.isLeaf()){
                        variablesTreeModel.insertNodeInto(objectNode, variablesTreeRoot, variablesTreeRoot.getChildCount());
                }

        }
        //variablesTree.expandRow(0);
        for (int i = 0; i < variablesTree.getRowCount(); i++) {
            variablesTree.expandRow(i);
        }
    }
        
        private void builPreferenceFunctionList(){
            
            if (currentPreferenceFunctions == null) {
                currentPreferenceFunctions = new ArrayList<Element>();
            }else{
                currentPreferenceFunctions.clear();
            }
            
            List<?> functions = metricData.getChild("preferenceFunction").getChildren("function");
            for (Iterator<?> iter = functions.iterator(); iter.hasNext();) {
                Element function = (Element) iter.next();
                currentPreferenceFunctions.add(function);
                preferenceFunctionsListModel.addElement(getFunctionLabel(function));
            }

        }
        
        private String getFunctionLabel(Element function){
            
            Element lowerbound = function.getChild("domain").getChild("lowerbound");
            Element upperbound = function.getChild("domain").getChild("upperbound");                    
            String functionLabel = (lowerbound.getAttributeValue("included").equals("true")?"[":"(") + 
                    lowerbound.getAttributeValue("value") + "," + upperbound.getAttributeValue("value") +
                    (upperbound.getAttributeValue("included").equals("true")?"]":")")  + " f(x) = " +  function.getChildText("rule");            
            return functionLabel;
        }
        
        
        private void fillEditFunctionPanel(){
            if (selectedFunctionData != null){
               rule.setText(selectedFunctionData.getChildText("rule")); 
               Element lowerbound = selectedFunctionData.getChild("domain").getChild("lowerbound");
               Element upperbound = selectedFunctionData.getChild("domain").getChild("upperbound");   
               lowerboundType.setSelectedIndex((lowerbound.getAttributeValue("included").equals("true")? 0 : 1));
               upperboundType.setSelectedIndex((upperbound.getAttributeValue("included").equals("true")? 0 : 1));
               lowerboundValue.setSelectedItem(lowerbound.getAttributeValue("value"));
               upperboundValue.setSelectedItem(upperbound.getAttributeValue("value"));
               
            }
            
        }
        
        private void setSelectedTypeTabs(String selection){
            if (mainPane.getTabCount() > 1){
                for (int i = 1; i < mainPane.getTabCount(); i++) {
                    mainPane.remove(i);                                        
                }
            }

            if (selection.equals("expression")){
                mainPane.add("Expression", expressionPanel);
                mainPane.add("Preferences", preferenceFunctionsPanel);
            }
            else if (selection.equals("variable")){
                mainPane.add("Variable", variablesPanel);
                mainPane.add("Preferences", preferenceFunctionsPanel);
            }
            else if (selection.equals("actionCounter")){
                mainPane.add("Action Counter", actionCounterPanel);
                mainPane.add("Preferences", preferenceFunctionsPanel);
            }
        }


        /**
         * Show a operator's parameter in the table
         * @param parameter
         */
        private void showParameter(Element opparameter) {

            //DefaultTableModel tableModel = new DefaultTableModel();
            //tableModel = (DefaultTableModel)parametersTable.getModel();

            Vector<String> attRow = new Vector<String>();

            //1.find the original parameter.

            Element chosenOperator = opparameter.getParentElement().getParentElement();

            String parameterLabel = "";

            Element parameter = null;
                    Element typeClass = null;

            try {
                XPath path = new JDOMXPath("project/elements/classes/class[@id='"+chosenOperator.getAttributeValue("class")+"']/operators/operator[@id='"+chosenOperator.getAttributeValue("id")+"']/parameters/parameter[@id='"+opparameter.getAttributeValue("id")+"']");
                parameter = (Element)path.selectSingleNode(domain.getDocument());
            } catch (JaxenException e2) {
                e2.printStackTrace();
            }
            if (parameter != null){
                        parameterLabel = parameter.getChildText("name");

                        try {
                                XPath path = new JDOMXPath("project/elements/classes/class[@id='"+parameter.getChildText("type")+"']");
                                typeClass = (Element)path.selectSingleNode(parameter.getDocument());
                        } catch (JaxenException e2) {
                                e2.printStackTrace();
                        }
                        if (typeClass != null){
                                parameterLabel += "("+ typeClass.getChildText("name") + ")";
                        }
            }
            //first column
            attRow.add(parameterLabel);

            Element object = null;
            try {
                XPath path = new JDOMXPath("elements/objects/object[@id='"+opparameter.getAttributeValue("object")+"']");
                object = (Element)path.selectSingleNode(domain);
            } catch (JaxenException e2) {
                e2.printStackTrace();
            }
            //second column
            if (object != null){
                attRow.add(object.getChildText("name"));
            }
            else{
                attRow.add("Any");
            }

            parametersTableModel.addRow(attRow);
        }


        /**
         * Set the chosen operator.(parameters,references, etc)
         * @param operator
         */
        private void setChosenOperator(Element operator) {

            //clear table of parameters
            //deletes ALL the rows
            parametersTableModel.getDataVector().removeAllElements();
            //repaints the table and notify all listeners (only once!)
            parametersTableModel.fireTableDataChanged();
            
            //while (parametersTableModel.getRowCount() > 0) {
            //    parametersTableModel.removeRow(0);
            //}

            Element chosenOperator = metricData.getChild("actionCounter").getChild("chosenOperator");

            if (operator != null){
                Element theClass = operator.getParentElement().getParentElement();

                //set main references
                chosenOperator.setAttribute("class", theClass.getAttributeValue("id"));
                chosenOperator.setAttribute("id", operator.getAttributeValue("id"));
                               

                //Clear parameters
                chosenOperator.getChild("parameters").removeContent();


                currentParameters.clear();


                //create the new parameter references
                for (Iterator<Element> it = operator.getChild("parameters").getChildren().iterator(); it.hasNext();) {
                    Element parameter = it.next();

                    Element parameterRef = new Element("parameter");
                    parameterRef.setAttribute("id", parameter.getAttributeValue("id"));
                    parameterRef.setAttribute("object", "");
                    chosenOperator.getChild("parameters").addContent(parameterRef);
                    // fill out parameters
                    currentParameters.add(parameterRef);
                    //show it in the table
                    showParameter(parameterRef);

                }
                //XMLUtilities.printXML(chosenOperator);

            }
            else{//if it is null just clean the values
                //clearmain references
                chosenOperator.setAttribute("class", "");
                chosenOperator.setAttribute("id", "");

                //Clear parameters
                chosenOperator.getChild("parameters").removeContent();

            }

        }

    @Override
    public void tableChanged(TableModelEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
		int row = e.getFirstRow();
                int col = e.getColumn();
                if(row > -1 && col > -1){
                    if (e.getSource() == parametersTableModel){
                        Element chosenOperator = metricData.getChild("actionCounter").getChild("chosenOperator");
                        Element selectedParameter = currentParameters.get(row);
                        switch(col){
                            //case 0:{// name
                            //    selectedParameter.getChild("name").setText(strdata);
                            //}
                            //break;
                            case 1:{// type
                                Element object = (Element)parameterObject.getDataItem(parameterObject.getSelectedIndex());
                                if(object != null){
                                        if(!object.getAttributeValue("id").equals(selectedParameter.getAttributeValue("object"))){
                                                selectedParameter.setAttribute("object", object.getAttributeValue("id"));
                                        }
                                }
                                else{
                                        selectedParameter.setAttribute("object","");
                                }
                            }
                            break;
                        }
                        //XMLUtilities.printXML(chosenOperator);
                    }
                }
    }



            //Actions

        /**
         * This action adds a new preference function domain
         */
        private Action newPreferenceFunction = new AbstractAction("New",new ImageIcon("resources/images/new.png")){

		public void actionPerformed(ActionEvent e) {
                    Element function = (Element)ItSIMPLE.getCommonData().getChild("definedNodes").getChild("elements").getChild("model").getChild("function").clone();
                    String id = String.valueOf(XMLUtilities.getId(metricData.getChild("preferenceFunction")));
                    function.getAttribute("id").setValue(id);
                    function.getChild("rule").setText("1"); //set basic function as start
                    metricData.getChild("preferenceFunction").addContent(function);

                    preferenceFunctionsListModel.addElement(getFunctionLabel(function));

                    currentPreferenceFunctions.add(function);

                    editAndNewFunctionPanel.setVisible(true);

                    selectedFunctionData = function;

                    preferenceFunctionsList.setSelectedIndex(preferenceFunctionsListModel.size()-1);


		}
	};

        /**
         * This action deletes a preference function domain
         */
	private Action deletePreferenceFunction = new AbstractAction("Delete",new ImageIcon("resources/images/delete.png")){

		public void actionPerformed(ActionEvent e) {
			int row = preferenceFunctionsList.getSelectedIndex();
			if (row > -1){
				Element selectedFunction = currentPreferenceFunctions.get(row);
				metricData.getChild("preferenceFunction").removeContent(selectedFunction);
				currentPreferenceFunctions.remove(row);
				preferenceFunctionsListModel.removeElementAt(row);
                                preferenceFunctionsList.setSelectedIndex(preferenceFunctionsListModel.size()-1);
                                //when there is no more functions the hide the edit panel
                                if (preferenceFunctionsListModel.size() == 0){
                                    selectedFunctionData = null;
                                    editAndNewFunctionPanel.setVisible(false);
                                }


			}
		}
	};




}

    