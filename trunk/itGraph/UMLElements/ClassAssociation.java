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


public class ClassAssociation extends BasicAssociation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4840264550851643515L;
	
	
	public static final int ARROW_NONE = 0;
	public static final int ARROW_SIMPLE = 1;
	public static final int ARROW_AGGREGATION = 2;
	public static final int ARROW_COMPOSITION = 3;
	
	private String sourceMultiplicity = "";
	private String targetMultiplicity = "";
	private String sourceRolename = "";
	private String targetRolename = "";
	private String changeability = "";
	
	private int sourceArrowType = 0;
	private int targetArrowType = 0;
	



	public ClassAssociation(){
		super();
	}
	
	public ClassAssociation(Object userObject){
		super(userObject);
	}
	
	public ClassAssociation(Element association, Element associationReference, BasicCell source, BasicCell target){
		super();
		data = association;
		reference = associationReference;

		
		setSource(source.getChildAt(0));
		if(source == target){
			// self-loop
			setTarget(source.getChildAt(1));
			loopEdge = source;
			
		}

		else{
			setTarget(target.getChildAt(0));
		}
		setVisual();
	}

	public ClassAssociation(Object userObject, int sourceArrow, int targetArrow){
		super(userObject);
		
		sourceArrowType = sourceArrow;
		targetArrowType = targetArrow;
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
	

	/**
	 * @return Returns the constraints.
	 */
	public String getChangeability() {
		return changeability;
	}

	/**
	 * @param changeability The constraints to set.
	 */
	public void setChangeability(String changeability) {
		this.changeability = changeability;
	}
	
	public void setVisual() {		
		//graphics
		setUserObject(data.getChildText("name"));
		
		changeability = data.getChild("changeability").getChildText("value");

		Iterator associationEnds = data.getChild("associationEnds").
										getChildren("associationEnd").iterator();
		while(associationEnds.hasNext()){
			Element source = (Element)associationEnds.next();
			sourceRolename = source.getChild("rolename").getChildText("value");
			sourceMultiplicity = source.getChild("multiplicity").getChildText("value");
			if (Boolean.parseBoolean(source.getAttributeValue("navigation"))){
				if(source.getChildText("type").equals("simple")){
					sourceArrowType = ARROW_SIMPLE;
				}
				else if (source.getChildText("type").equals("aggregation")){
					sourceArrowType = ARROW_AGGREGATION;
				}
				else if (source.getChildText("type").equals("composition")){
					sourceArrowType = ARROW_COMPOSITION;
				}
				else if (source.getChildText("type").equals("none")){
					sourceArrowType = ARROW_NONE;
				}
			}
			else{
				sourceArrowType = ARROW_NONE;
			}
			
			Element target = (Element)associationEnds.next();			
			targetRolename = target.getChild("rolename").getChildText("value");
			targetMultiplicity = target.getChild("multiplicity").getChildText("value");
			
			if (Boolean.parseBoolean(target.getAttributeValue("navigation"))){			
				if(target.getChildText("type").equals("simple")){
					targetArrowType = ARROW_SIMPLE;
				}
				else if (target.getChildText("type").equals("aggregation")){
					targetArrowType = ARROW_AGGREGATION;
				}
				else if (target.getChildText("type").equals("composition")){
					targetArrowType = ARROW_COMPOSITION;
				}
				else if (target.getChildText("type").equals("none")){
					targetArrowType = ARROW_NONE;
				}
			}
			else{
				targetArrowType = ARROW_NONE;
			}
			
		}	
		
	}
}
