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

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

public class ComboBoxRenderer extends JLabel implements ListCellRenderer{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2041563434777066734L;	

	

	public ComboBoxRenderer() {
		super();
		setOpaque(true);
        setHorizontalAlignment(LEFT);
        setVerticalAlignment(CENTER);

	}

	public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {		

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }
       
       if (value instanceof ImageIcon){    	   
    	   ImageIcon icon = (ImageIcon)value;    	   
    	   setIcon(icon);
    	   setText(icon.getDescription().substring(0, icon.getDescription().lastIndexOf(".")));    	   
       }
       else if(value instanceof String){
    	   setIcon(null);
    	   setText((String)value);
       }
		
		return this;
	}
	
	

}
