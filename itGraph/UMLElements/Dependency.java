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

package itGraph.UMLElements;

import itGraph.BasicCell;

import java.util.Iterator;

import org.jdom.Element;


public class Dependency extends BasicAssociation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7829235430511284350L;
	
	private String sourceMultiplicity = "";
	private String targetMultiplicity = "";
	private String sourceRolename = "";
	private String targetRolename = "";
	
	public Dependency(Object userObject){
		super(userObject);		
	}
	
	public Dependency(Element data, BasicCell source, BasicCell target){
		super();
		this.data = data;
		this.setVisual();		
		
		this.setSource(source.getChildAt(0));
		if(source == target){
			// self-loop
			setTarget(source.getChildAt(1));
			loopEdge = source;			
		}

		else{
			setTarget(target.getChildAt(0));
		}		
	}
	
	public void setSourceMultiplicity(String multiplicity){
		sourceMultiplicity = multiplicity;
	}
	
	public String getSourceMultiplicity(){
		return sourceMultiplicity;
	}
	
	public void setTargetMultiplicity(String multiplicity){
		targetMultiplicity = multiplicity;
	}
	
	public String getTargetMultiplicity(){
		return targetMultiplicity;
	}
	
	public void setSourceRolename(String name){
		sourceMultiplicity = name;
	}
	
	public String getSourceRolename(){
		return sourceRolename;
	}
	
	public void setTargetRolename(String name){
		targetRolename = name;
	}
	
	public String getTargetRolename(){
		return targetRolename;
	}
	
	public void setVisual() {		
		this.setUserObject(data.getChildText("name"));
		
		Iterator associationEnds = data.getChild("associationEnds").
										getChildren("associationEnd").iterator();
		
			Element source = (Element)associationEnds.next();
			sourceRolename = source.getChildText("rolename");
			sourceMultiplicity = source.getChildText("multiplicity");
			
			Element target = (Element)associationEnds.next();
			targetRolename = target.getChildText("rolename");
			targetMultiplicity = target.getChildText("multiplicity");
			
		
	}

}
