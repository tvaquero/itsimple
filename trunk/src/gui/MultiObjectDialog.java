/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2011 University of Sao Paulo, University of Toronto
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

package src.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

public class MultiObjectDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7331520762187291891L;
	
	Element project;
	Object[] parameters;	
	
	JComboBox classesComboBox;
	JTextField numberTextField;
        JTextField listTextField;
	//JButton createObjectsButton;
	
	public MultiObjectDialog(Element project, Object[] parameters, int x, int y){
		super(ItSIMPLE.getItSIMPLEFrame());		
		this.project = project;
		this.parameters = parameters;
		setTitle("Create Multiple Objects");
		setModal(true);
		setResizable(false);
		setSize(380,210);
		setLocation(x,y);
		add(getMainPane());
	}
	
	@SuppressWarnings("unchecked")
	private JPanel getMainPane(){		
		JLabel classLabel = new JLabel("Class:");
		classesComboBox = new JComboBox();
		classesComboBox.addItem("Undefined");
		
		List<Element> classes = null;		
		try {
			XPath path = new JDOMXPath("elements/classes/class[@id>4]");
			classes = path.selectNodes(project);					
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		for (Iterator<Element> iter = classes.iterator(); iter.hasNext();) {
			Element aClass = iter.next();
			classesComboBox.addItem(new ItemElement(aClass));
		}
		
		JLabel numberLabel = new JLabel("Number of objects:");
		// only integer and non negative numbers will be accepted
		numberTextField = new JTextField();
		JTextFieldFilter filter = new JTextFieldFilter(JTextFieldFilter.NUMERIC);
		filter.setNegativeAccepted(false);
		filter.setLimit(3);
		numberTextField.setDocument(filter);
		numberTextField.setColumns(3);
                
                
                JLabel listLabel = new JLabel("Or, list their names:");
		// only integer and non negative numbers will be accepted
		listTextField = new JTextField();
		listTextField.setColumns(3);
                listTextField.setToolTipText("List the names of the objects, separated by comma (e.g., loc1, loc2, loc3).");
                
		
		JPanel definitionsPane = new JPanel(new SpringLayout());
		definitionsPane.add(classLabel);
		definitionsPane.add(classesComboBox);
		definitionsPane.add(numberLabel);
		definitionsPane.add(numberTextField);
                definitionsPane.add(listLabel);
		definitionsPane.add(listTextField);
		SpringUtilities.makeCompactGrid(definitionsPane, 3, 2, 5, 5, 5, 5);		
		
		//createObjectsButton = new JButton("Create");		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.add(new JButton(okAction));
		buttonsPanel.add(new JButton(cancelAction));
		
		JPanel mainPane = new JPanel(new BorderLayout());
		mainPane.add(definitionsPane, BorderLayout.CENTER);
		mainPane.add(buttonsPanel, BorderLayout.SOUTH);
		
		
		classesComboBox.addKeyListener(new KeyAdapter() {		
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					okAction.actionPerformed(null);
				}
		
			}
		
		});
		
		
		numberTextField.addKeyListener(new KeyAdapter() {		
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					okAction.actionPerformed(null);
				}
		
			}
		
		});
                
                listTextField.addKeyListener(new KeyAdapter() {		
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER){
					okAction.actionPerformed(null);
				}
		
			}
		
		});
		
		return mainPane;
	}
	
	//Actions
	private Action okAction = new AbstractAction("OK"){


		/**
		 * 
		 */
		private static final long serialVersionUID = 4765366613330731246L;

		public void actionPerformed(ActionEvent e){
			String text = numberTextField.getText();
                        String listtext = listTextField.getText().trim();
                        
                        if(classesComboBox.getSelectedItem().equals("Undefined")){
                            parameters[0] = classesComboBox.getSelectedItem();
                        }
                        else{
                            Element selectedClass = ((ItemElement)classesComboBox.getSelectedItem()).data;
                            parameters[0] = selectedClass;
                        }

			if(!text.equals("")){
                            if (!text.equals("0")){                                
				// if the text is null or equals to "0", do nothing
				// otherwise, set the parameters
				parameters[1] = text;
                            }
			}
                        if(!listtext.equals("")){
                            // if the text is null do nothing
                            List<String> names = Arrays.asList(listtext.split(","));
                            parameters[2] = names;
                            //System.out.print(names);
                            
                            
                        }
			
			// close the dialog
			MultiObjectDialog.this.dispose();
		}
		
	};
	
	private Action cancelAction = new AbstractAction("Cancel"){
		

		/**
		 * 
		 */
		private static final long serialVersionUID = -4928363674830362670L;

		public void actionPerformed(ActionEvent e){
			MultiObjectDialog.this.dispose();
		}
	};
	
	private class ItemElement{
		Element data;
		
		private ItemElement(Element data){
			this.data = data;
		}
		
		public String toString(){
			return data.getChildText("name");
		}
	}

}
