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

package src.gui;

import javax.swing.Icon;
import javax.swing.tree.DefaultMutableTreeNode;

import org.jdom.Element;

public class ItTreeNode extends DefaultMutableTreeNode {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5734354191887109420L;
	
	private Element data = null;
	private Element reference = null;
	private Element additionalData = null;
        private String infostring = "";
	private Icon icon = null;

	public ItTreeNode() {
		super();		
	}

	public ItTreeNode(Object userObject) {
		super(userObject);		
	}
	
	public ItTreeNode(Object userObject, Element nodeData, Element nodeReference, Element addData){
		super(userObject);
		
		data = nodeData;
		reference = nodeReference;
		additionalData = addData;
		
	}
	
	public void setData(Element nodeData){
		data = nodeData;
	}
	
	public Element getData(){
		return data;
	}
	
	public void setReference(Element nodeReference){
		reference = nodeReference;
	}
	
	public Element getReference(){
		return reference;
	}	
	
	public void setAdditionalData(Element addData){
		additionalData = addData;
	}	
	
	public Element getAdditionalData(){
		return additionalData;
	}
	
	public void setTitle(){
		this.setUserObject(data.getChildText("name"));
	}
	
	public Icon getIcon(){
		return icon;
	}
	
	public void setIcon(Icon nodeIcon){
		icon = nodeIcon;
	}
        
        public void setInfoString(String info){
		infostring = info;
	}
	
	public String getInfoString(){
		return infostring;
	}
        

}
