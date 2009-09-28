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

/*
Definitive Guide to Swing for Java 2, Second Edition
By John Zukowski     
ISBN: 1-893115-78-X
Publisher: APress
*/

//import java.awt.BorderLayout;


import java.util.ArrayList;
import java.util.Iterator;


import org.jdom.Element;

public class CheckBoxNode extends ItTreeNode{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5640575137266634456L;

	public CheckBoxNode(Object userObject) {
		super(new CheckBoxUserObject(userObject.toString(), false));
	}

	
	public CheckBoxNode(Object userObject, Element nodeData, Element nodeReference, Element addData){
		super(new CheckBoxUserObject(userObject.toString(), false),
				nodeData, nodeReference, addData);		
	}
	
	public boolean isChecked(){
		CheckBoxUserObject userObject = (CheckBoxUserObject)getUserObject();
		return userObject.isSelected();
	}
        
        public void setSelected(boolean newValue){
		CheckBoxUserObject userObject = (CheckBoxUserObject)getUserObject();
		userObject.setSelected(newValue);
	}
	
	  
	  public static Object[] getCheckedNodes(CheckBoxNode node){
		  ArrayList<CheckBoxNode> checked = new ArrayList<CheckBoxNode>();

		  if(node.isLeaf()){
			 if(node.isChecked()){
				 checked.add(node);
			  }
		  }
		  else{
			  for (Iterator iter = node.children.iterator(); iter.hasNext();) {
				CheckBoxNode child = (CheckBoxNode) iter.next();
				Object[] checkedChildren = getCheckedNodes(child);
				for (int i = 0; i < checkedChildren.length; i++) {
					checked.add((CheckBoxNode)checkedChildren[i]);
				}
			}
		  }

		  
		  return checked.toArray();
	  }
	
}

	

	class CheckBoxUserObject {
	  String text;

	  boolean selected;

	  public CheckBoxUserObject(String text, boolean selected) {
	    this.text = text;
	    this.selected = selected;
	  }

	  public boolean isSelected() {
	    return selected;
	  }

	  public void setSelected(boolean newValue) {
	    selected = newValue;
	  }

	  public String getText() {
	    return text;
	  }

	  public void setText(String newValue) {
	    text = newValue;
	  }

	  public String toString() {	   
		  return text;
	  }
	}
	