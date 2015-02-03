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

package src.itgraph.uml;


import java.util.Iterator;

import org.jdom.Element;

import src.itgraph.BasicCell;

public class ObjectAssociation extends BasicAssociation {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7774523257939855078L;
	
	private String sourceRolename = "";
	private String targetRolename = "";
	
	private int sourceArrowType = 0;
	private int targetArrowType = 0;
	
	public ObjectAssociation(Object userObject){
		super(userObject);		
	}
	
	public ObjectAssociation(Object userObject, int sourceArrow, int targetArrow){
		super(userObject);
		
		sourceArrowType = sourceArrow;
		targetArrowType = targetArrow;
	}
	
	public ObjectAssociation(Element association, Element associationReference, BasicCell source, BasicCell target){
		super();
		data = association;
		reference = associationReference;
		
		if (reference != null){
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
	}
	
	public void setSourceRolename(String name){
		sourceRolename = name;
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
	
	public void setSourceArrowType(int arrow){
		sourceArrowType = arrow;
	}
	
	public int getSourceArrowType(){
		return sourceArrowType;
	}
	
	public void setTargetArrowType(int arrow){
		targetArrowType = arrow;
	}
	
	public int getTargetArrowType(){
		return targetArrowType;
	}
	
	public void setVisual() {		
		//graphics
		this.setUserObject(reference.getChildText("name"));
		
		Iterator associationEnds = data.getChild("associationEnds").
										getChildren("objectAssociationEnd").iterator();
		while(associationEnds.hasNext()){
			Element referenceSource = (Element)associationEnds.next();			
			Iterator sourceAssociations = reference.getChild("associationEnds").getChildren("associationEnd").iterator();
			Element source = null;
			
			while (sourceAssociations.hasNext()){
				Element current = (Element)sourceAssociations.next();
				if(current.getAttributeValue("id").equals(referenceSource.getAttributeValue("id")))	
					source = current;		
			}
			
			sourceRolename = source.getChild("rolename").getChildText("value");			
			if (Boolean.parseBoolean(source.getAttributeValue("navigation"))){
				if(source.getChildText("type").equals("simple")){
					sourceArrowType = ClassAssociation.ARROW_SIMPLE;
				}
				else if (source.getChildText("type").equals("aggregation")){
					sourceArrowType = ClassAssociation.ARROW_AGGREGATION;
				}
				else if (source.getChildText("type").equals("composition")){
					sourceArrowType = ClassAssociation.ARROW_COMPOSITION;
				}
				else if (source.getChildText("type").equals("none")){
					sourceArrowType = ClassAssociation.ARROW_NONE;
				}
			}
			else{
				sourceArrowType = ClassAssociation.ARROW_NONE;
			}
			
			Element referenceTarget = (Element)associationEnds.next();			
			Iterator targetAssociations = reference.getChild("associationEnds").getChildren("associationEnd").iterator();
			Element target = null;
			while (targetAssociations.hasNext()){
				Element current = (Element)targetAssociations.next();								
				if(current.getAttributeValue("id").equals(referenceTarget.getAttributeValue("id"))){
					target = current;			
				}
			}			
			targetRolename = target.getChild("rolename").getChildText("value");
						
			if (Boolean.parseBoolean(target.getAttributeValue("navigation"))){			
				if(target.getChildText("type").equals("simple")){
					targetArrowType = ClassAssociation.ARROW_SIMPLE;
				}
				else if (target.getChildText("type").equals("aggregation")){
					targetArrowType = ClassAssociation.ARROW_AGGREGATION;
				}
				else if (target.getChildText("type").equals("composition")){
					targetArrowType = ClassAssociation.ARROW_COMPOSITION;
				}
				else if (target.getChildText("type").equals("none")){
					targetArrowType = ClassAssociation.ARROW_NONE;
				}
			}
			else{
				targetArrowType = ClassAssociation.ARROW_NONE;
			}
			
		}	
		
	}

}
