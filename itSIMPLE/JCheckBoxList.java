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

import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import java.util.List;
import java.util.ArrayList;
import javax.swing.DefaultListModel;
import javax.swing.JCheckBox;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.jdom.Element;

 class JCheckBoxList extends javax.swing.JList{
    /**
	 * 
	 */
	private static final long serialVersionUID = -7752259972021956509L;
	
	protected static Border noFocusBorder = new EmptyBorder(1, 1, 1, 1);
        
        final private boolean uniqueSelection;
 
    public JCheckBoxList(final boolean uniqueSelection){   	
    	this.uniqueSelection = uniqueSelection;
    	
        setCellRenderer(new CheckBoxCellRenderer());
 
        addMouseListener(new MouseAdapter(){
            public void mousePressed(MouseEvent e){
                int index = locationToIndex(e.getPoint());
                if (index != -1){
                	JCheckBoxListItem checkbox = (JCheckBoxListItem) getModel().getElementAt(index);
                	
                	boolean selected = !checkbox.isSelected();
                	if(selected){
                		DefaultListModel model = (DefaultListModel)JCheckBoxList.this.getModel();
                		
                		// unselect all items
                                if(uniqueSelection){
                                    for (int i = 0; i < model.getSize(); i++) {
                                        JCheckBoxListItem currentCheckBox = (JCheckBoxListItem)model.get(i);
                                        currentCheckBox.setSelected(false);
                                        currentCheckBox.getData().getChild("enabled").setText(String.valueOf(false));
                                    }                                    
                                }

                	}
                        // select current item
            		checkbox.setSelected(selected);
                        checkbox.getData().getChild("enabled").setText(String.valueOf(selected));
                        
                        
                        repaint();
                }
                
            }
        });
 
        addKeyListener(new KeyAdapter(){
            public void keyPressed(KeyEvent e){
                if (e.getKeyCode() == KeyEvent.VK_SPACE){
                    int index = getSelectedIndex();
                    if (index != -1){
                    	JCheckBoxListItem checkbox = (JCheckBoxListItem) getModel().getElementAt(index);
                        checkbox.setSelected(!checkbox.isSelected());
                        repaint();
                    }
                }
            }
        });
 
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    }
    
    public List getSelectedData(){
        List selected = new ArrayList();
        
        DefaultListModel model = (DefaultListModel)JCheckBoxList.this.getModel();
            for (int i = 0; i < model.getSize(); i++) {
                JCheckBoxListItem currentCheckBox = (JCheckBoxListItem)model.get(i);
                Element data = currentCheckBox.getData();
                if(data.getChildText("enabled").equals("true")){
                    selected.add(data);                  
                }

            }         
        
        
        return selected;
    }
 
    protected class CheckBoxCellRenderer implements ListCellRenderer{
        public Component getListCellRendererComponent(JList list, Object value, int index,
                                                      boolean isSelected, boolean cellHasFocus){
        	JCheckBoxListItem checkbox = (JCheckBoxListItem) value;
            checkbox.setBackground(isSelected ? getSelectionBackground() : getBackground());
            checkbox.setForeground(isSelected ? getSelectionForeground() : getForeground());
 
            checkbox.setEnabled(isEnabled());
            checkbox.setFont(getFont());
            checkbox.setFocusPainted(false);
 
            checkbox.setBorderPainted(true);
            checkbox.setBorder(isSelected ? UIManager.getBorder("List.focusCellHighlightBorder") : noFocusBorder);
 
            return checkbox;
        }
    } 

 
 
    /*public static void main(String args[])
    {
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout());
 
        JCheckBoxList cbList = new JCheckBoxList();
 
        Object[] cbArray = new Object[3];
        cbArray[0] = new JCheckBox("one");
        cbArray[1] = new JCheckBox("two");
        cbArray[2] = new JCheckBox("three");
 
        cbList.setListData(cbArray);
 
        frame.getContentPane().add(cbList);
        frame.pack();
        frame.setVisible(true);
    }*/
}

 
 
 
 /* class JCheckBoxListItem extends JCheckBox{
	/**
	 * 
	 */
//	private static final long serialVersionUID = -6499707482265479493L;
	
        /** This attribute must have a node named "enabled", where it will be
         stored wheter the item is checked or not **/
//        private Element data;
	
//	JCheckBoxListItem(Element data, boolean initialSelection){
//		this.data = data;
//		setText(data.getChildText("name"));
		
//		setSelected(initialSelection);
//	}
	
//	Element getData(){
//		return data;
//	}
	
//} 

