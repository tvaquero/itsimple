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

package itGraph;

import org.jdom.Element;
import org.jgraph.graph.DefaultGraphCell;

public abstract class BasicCell extends DefaultGraphCell{

	/**
	 * 
	 */
	private static final long serialVersionUID = -4074315407953204562L;
	
	protected Element data;
	protected Element reference;
	protected Element additionalData;		
	

	public BasicCell() {
		super();
		this.addPort();
		this.addPort();
	}

	public BasicCell(Object userObject) {
		super(userObject);
		this.addPort();
		this.addPort();
	}	
	
	public Element getData(){
		return data;
	}
	
	public void setData(Element cellData){
		data = cellData;
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

	public abstract void setVisual();
	
}
