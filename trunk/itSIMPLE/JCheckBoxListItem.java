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

import javax.swing.JCheckBox;
import org.jdom.Element;

/**
 *
 * @author Tiago
 */
class JCheckBoxListItem extends JCheckBox{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6499707482265479493L;
	
        /** This attribute must have a node named "enabled", where it will be
         stored wheter the item is checked or not **/
        private Element data;
	
	JCheckBoxListItem(Element data, boolean initialSelection){
		this.data = data;
		setText(data.getChildText("name"));
		setSelected(initialSelection);
	}
	
	Element getData(){
		return data;
	}
	
}
