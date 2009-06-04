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

import java.awt.Color;
import java.awt.geom.Point2D;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.GraphConstants;

public class ObjectAssociationView extends BasicAssociationView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2794383010754231271L;
	
	public ObjectAssociationView(Object cell) {
		super(cell);
		
		ObjectAssociation association = (ObjectAssociation) cell;
		
		//set color
		String strColor = association.getData().getChild("graphics").getChildText("color");		
		if(!strColor.equals("")){
			Color color = new Color(Integer.parseInt(strColor, 16));
			GraphConstants.setLineColor(attributes, color);
		}
		
		Element labelsNode = association.getData().getChild("graphics").getChild("labels");
		// positions
		Element sourceRolename = null;
		Element targetRolename = null;

		try {
			XPath path = new JDOMXPath("label[@type='rolename' and @identifier='1']/position");
			sourceRolename = (Element)path.selectSingleNode(labelsNode);
			
			path = new JDOMXPath("label[@type='rolename' and @identifier='2']/position");
			targetRolename = (Element)path.selectSingleNode(labelsNode);
		} catch (JaxenException e1) {
			e1.printStackTrace();
		}
		
		if(sourceRolename != null && targetRolename != null){
			// source rolename
			String sourceRolenameX = sourceRolename.getAttributeValue("x");
			String sourceRolenameY = sourceRolename.getAttributeValue("y");
			
			Point2D sourceRolenamePoint = new Point2D.Double();
			if(sourceRolenameX.equals("") || sourceRolenameY.equals("")){
				sourceRolenamePoint.setLocation(100, 15);
			}
			else{
				sourceRolenamePoint.setLocation(
						Double.parseDouble(sourceRolenameX),
						Double.parseDouble(sourceRolenameY));
			}
			
			// target rolename
			String targetRolenameX = targetRolename.getAttributeValue("x");
			String targetRolenameY = targetRolename.getAttributeValue("y");
			
			Point2D targetRolenamePoint = new Point2D.Double();
			if(targetRolenameX.equals("") || targetRolenameY.equals("")){
				targetRolenamePoint.setLocation(GraphConstants.PERMILLE-100, 15);
			}
			else{
				targetRolenamePoint.setLocation(
						Double.parseDouble(targetRolenameX),
						Double.parseDouble(targetRolenameY));
			}
		
			AttributeMap attributes = getAttributes();
			Object[] labels = {association.getSourceRolename(),
					           association.getTargetRolename()};
			Point2D[] labelsPositions = {sourceRolenamePoint,
										 targetRolenamePoint};
			GraphConstants.setExtraLabelPositions(attributes, labelsPositions);
			GraphConstants.setExtraLabels(attributes, labels);		
			GraphConstants.setBeginSize(attributes, 13);
			GraphConstants.setEndSize(attributes, 13);
		}
		if (association.getSourceArrowType() == ClassAssociation.ARROW_SIMPLE){
			GraphConstants.setLineBegin(attributes, GraphConstants.ARROW_SIMPLE);			
		}
		
		else if(association.getSourceArrowType() == ClassAssociation.ARROW_AGGREGATION){
			GraphConstants.setLineBegin(attributes, GraphConstants.ARROW_DIAMOND);
			GraphConstants.setBeginFill(attributes, false);
		}
		
		else if(association.getSourceArrowType() == ClassAssociation.ARROW_COMPOSITION){
			GraphConstants.setLineBegin(attributes, GraphConstants.ARROW_DIAMOND);
			GraphConstants.setBeginFill(attributes, true);
		}
		else if(association.getSourceArrowType() == ClassAssociation.ARROW_NONE){
			GraphConstants.setLineBegin(attributes, GraphConstants.ARROW_NONE);
			GraphConstants.setBeginFill(attributes, false);
		}
		
		if (association.getTargetArrowType() == ClassAssociation.ARROW_SIMPLE){
			GraphConstants.setLineEnd(attributes, GraphConstants.ARROW_SIMPLE);			
		}
		
		else if(association.getTargetArrowType() == ClassAssociation.ARROW_AGGREGATION){
			GraphConstants.setLineEnd(attributes, GraphConstants.ARROW_DIAMOND);
			GraphConstants.setEndFill(attributes, false);
		}
		
		else if(association.getTargetArrowType() == ClassAssociation.ARROW_COMPOSITION){
			GraphConstants.setLineEnd(attributes, GraphConstants.ARROW_DIAMOND);
			GraphConstants.setEndFill(attributes, true);
		}
		else if(association.getTargetArrowType() == ClassAssociation.ARROW_NONE){
			GraphConstants.setLineEnd(attributes, GraphConstants.ARROW_NONE);
			GraphConstants.setEndFill(attributes, false);
		}
		
	}

}
