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

package itGraph.PetriNetElements;

import itGraph.BasicCell;

import org.jdom.Element;

public class Arc extends BasicArc {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1437421005902158179L;
	
	public static final int NORMAL_ARC = 1;
	public static final int READ_ARC = 2;
	public static final int INHIBITOR_ARC = 3;
	
	//protected Element data;
	
	private String weight;
	private int arcType;
	
	public Arc() {
		super();
	}

	public Arc(Object userObject) {
		super(userObject);
	}
	
	public Arc(Element arc, BasicCell source, BasicCell target){
		super();
		data = arc;
		weight = arc.getChild("inscription").getChildText("text");
		String type = arc.getChild("type").getAttributeValue("value");
		if(type.equals("normal"))
			arcType = NORMAL_ARC;
		else if(type.equals("read"))
			arcType = READ_ARC;
		else if(type.equals("inhibitor"))
			arcType = INHIBITOR_ARC;
		setVisual();
		
		setSource(source.getChildAt(0));
		setTarget(target.getChildAt(0));		
		
	}

	
	public void setVisual() {		
		//graphics
		if(!weight.equals("1"))
			setUserObject(weight);
		
	}

	public Element getData() {
		return data;
	}

	public int getArcType() {
		return arcType;
	}

}
