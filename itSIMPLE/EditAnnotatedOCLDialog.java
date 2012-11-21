/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2012 University of Sao Paulo, University of Toronto
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
* Authors:	Tiago S. Vaquero, 
*			Victor Romero.
**/

package itSIMPLE;

import languages.xml.XMLUtilities;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.HeadlessException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.CellEditor;
import javax.swing.DefaultCellEditor;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.tree.DefaultTreeModel;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;

import org.jdom.Element;

import sourceEditor.ItHilightedDocument;


public class EditAnnotatedOCLDialog extends JDialog implements KeyListener, ItemListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2611427807326279073L;
	
	private JTabbedPane mainPane = null;
	
	// Base
	private ItPanel basePanel = null;

	private ItComboBox annotationText = null;
	
	// Constraints panel
	private JTextPane constraintsTextPane = null;	
	
	private Element data, additional;
	private Object senderObject;
	private PropertiesTabbedPane propertiesPane;
	private final Element commonData = ItSIMPLE.getCommonData();

	public EditAnnotatedOCLDialog(final Element data, Element additional, final Object senderObject, final PropertiesTabbedPane propertiesPane) throws HeadlessException {
		super(ItSIMPLE.getItSIMPLEFrame());
		setModal(true);
		this.data = data;
		this.additional = additional;
		this.senderObject = senderObject;
		this.propertiesPane = propertiesPane;
		setSize(300,450);
		setLocation(200,200);
                setTitle("Annotated OCL expression");
		add(getMainPane());
                                //The OK button at the bottom
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){                                                                                    
                            dispose();
                            if (senderObject instanceof JTable){
                                JTable table = (JTable)senderObject;
                                DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
                                tableModel.setValueAt(data.getChild("annotation").getText(), table.getSelectedRow(), 0);
                                tableModel.setValueAt(data.getChild("condition").getText(), table.getSelectedRow(), 1);
                            }
			}
		});
		buttonPanel.add(okButton);
                add(buttonPanel, BorderLayout.SOUTH);
	}

	
	
	private JTabbedPane getMainPane(){
		if (mainPane == null){						
                    mainPane = new JTabbedPane();
                    mainPane.addTab("Base", getBasePanel());						
		}	
		return mainPane;
	}

	
	private ItPanel getBasePanel(){
		if (basePanel == null){
			basePanel = new ItPanel(new BorderLayout());
			
			ItPanel nameBasePanel = new ItPanel(new BorderLayout());
				
			ItPanel bottomBasePanel = new ItPanel(new BorderLayout());			
			ItPanel topBasePanel = new ItPanel(new BorderLayout());
			
                        //Annotation 
			JLabel nameLabel = new JLabel("Annotation: ");
                        annotationText = new ItComboBox();			
			//attributeMultiplicity.setRenderer(renderer);
			annotationText.setEditable(true);
			annotationText.addItem("");
			annotationText.addItem("@start");
			annotationText.addItem("@end");
			annotationText.addItem("@overall");			
			annotationText.addItemListener(this);
                        
                                                			
			//OCL expression Text pane
			JLabel descriptionLabel = new JLabel("OCL expression");
                        ItHilightedDocument document = new ItHilightedDocument();
                        document.setHighlightStyle(ItHilightedDocument.OCL_STYLE);
                        constraintsTextPane = new JTextPane(document);		
                        constraintsTextPane.addKeyListener(this);
                        JScrollPane scrollText = new JScrollPane(constraintsTextPane);
                        
                        			
			nameBasePanel.add(nameLabel, BorderLayout.WEST);
			nameBasePanel.add(annotationText, BorderLayout.CENTER);
			topBasePanel.add(nameBasePanel, BorderLayout.NORTH);
			
			bottomBasePanel.add(descriptionLabel, BorderLayout.NORTH);
			bottomBasePanel.add(scrollText, BorderLayout.CENTER);
			
			basePanel.add(topBasePanel, BorderLayout.NORTH);
			basePanel.add(bottomBasePanel, BorderLayout.CENTER);
			
			// set the fieds values
			annotationText.setSelectedItem(data.getChildText("annotation"));
			constraintsTextPane.setText(data.getChildText("condition"));		
						
		}	
		
		return basePanel;
	}
	
		

        
        public void setTextComponentChange(ComponentEvent e){
            if (e.getSource() == annotationText){
                data.getChild("annotation").setText(annotationText.getSelectedItem().toString());
                if (senderObject instanceof JTable){				
                        JTable table = (JTable)senderObject;
                        DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
                        tableModel.setValueAt(annotationText.getSelectedItem().toString(), table.getSelectedRow(), 0);
                }
            }
        }
        
	
	public void keyTyped(KeyEvent e) {
		
	}

	public void keyPressed(KeyEvent e) {
		if (e.getKeyCode() == KeyEvent.VK_ENTER){
			setTextComponentChange(e);
		}		
	}
	
        

	public void keyReleased(KeyEvent e) {
		if(e.getSource() == constraintsTextPane){
			data.getChild("condition").setText(constraintsTextPane.getText());
                        if (senderObject instanceof JTable){				
                            JTable table = (JTable)senderObject;
                            DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
                            tableModel.setValueAt(constraintsTextPane.getText(), table.getSelectedRow(), 1);
                        }
		}
	}

	public void itemStateChanged(ItemEvent e) {
		if (e.getSource() == annotationText && e.getStateChange() == ItemEvent.SELECTED){
			data.getChild("annotation").setText((String)annotationText.getSelectedItem());
                        if (senderObject instanceof JTable){				
                            JTable table = (JTable)senderObject;
                            DefaultTableModel tableModel = (DefaultTableModel)table.getModel();
                            tableModel.setValueAt(annotationText.getSelectedItem().toString(), table.getSelectedRow(), 0);
                        }
		}		
		propertiesPane.repaintSelectedElement();
	}
	


}
