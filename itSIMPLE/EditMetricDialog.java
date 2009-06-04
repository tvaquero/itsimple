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

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;

import org.jdom.Element;

import sourceEditor.ItHilightedDocument;

public class EditMetricDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6974543794597743033L;	
	
	private Element metricData;
	private JCheckBoxListItem item;
	
	private JTextField nameField;
	private JComboBox typeComboBox;
	private JTextPane functionPane;
        
        private JCheckBoxList metricsList; //the visual list element for repaiting purposes
	
	public EditMetricDialog(Element metricData, JCheckBoxListItem item, JCheckBoxList list){
		super(ItSIMPLE.getItSIMPLEFrame());
		setTitle("Edit metric");
		setModal(true);
		
		this.metricData = metricData;
		this.item = item;
                this.metricsList = list;
		
		setSize(400,400);
		setLocation(200,200);
		add(getMainPanel());
	}
        
	
	private JPanel getMainPanel(){
		JPanel main = new JPanel(new BorderLayout());
		
		JPanel top = new JPanel(new SpringLayout());		
		top.add(new JLabel("Name: "));
		nameField = new JTextField(metricData.getChildText("name"));
		nameField.addKeyListener(new KeyAdapter(){
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					metricData.getChild("name").setText(nameField.getText());
					item.setText(nameField.getText());
					item.revalidate();
                                        metricsList.repaint();
				}
			}
		});
		top.add(nameField);
		
		top.add(new JLabel("Type: "));
		typeComboBox = new JComboBox();
		typeComboBox.addItem("");
		typeComboBox.addItem("minimize");
		typeComboBox.addItem("maximize");
		typeComboBox.setSelectedItem(metricData.getChildText("type"));
		typeComboBox.addItemListener(new ItemListener(){
			public void itemStateChanged(ItemEvent e) {
				String selection = (String)typeComboBox.getSelectedItem();
				metricData.getChild("type").setText(selection);
			}
			
		});
			
		top.add(typeComboBox);
		
		SpringUtilities.makeCompactGrid(top, 2, 2, 5, 5, 5, 5);
		
		JPanel bottom = new JPanel(new SpringLayout());
		bottom.add(new JLabel("Expression (OCL):"));
		ItHilightedDocument document = new ItHilightedDocument();
		document.setHighlightStyle(ItHilightedDocument.OCL_STYLE);
		functionPane = new JTextPane(document);
		functionPane.setText(metricData.getChildText("function"));
		functionPane.addKeyListener(new KeyAdapter(){
			@Override
			public void keyReleased(KeyEvent e) {
				metricData.getChild("function").setText(functionPane.getText());
			}
		});
		bottom.add(new JScrollPane(functionPane));
		
		SpringUtilities.makeCompactGrid(bottom,2,1,5,5,5,5);
		
                //The OK button at the bottom
                JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                                if (!metricData.getChildText("name").equals(nameField.getText())){
                                    metricData.getChild("name").setText(nameField.getText());
                                    item.setText(nameField.getText());
                                    item.revalidate();
                                    metricsList.repaint();
                                }
				dispose();
			}
		});
		buttonPanel.add(okButton);
	
		main.add(top, BorderLayout.NORTH);
		main.add(bottom, BorderLayout.CENTER);     
                main.add(buttonPanel, BorderLayout.SOUTH);
		
		return main;
	}


}
