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
import itSIMPLE.ItSIMPLE;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jdom.Element;
import org.jgraph.graph.DefaultEdge;
import org.jgraph.graph.DefaultPort;
import org.jgraph.graph.GraphConstants;

public abstract class BasicAssociation extends DefaultEdge{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -832171371976449709L;
	
	protected Element data;
	protected Element reference;
	
	protected BasicCell loopEdge = null;

	public BasicAssociation() {
		super();		
	}

	public BasicAssociation(Object userObject) {
		super(userObject);		
	}	
	
	public Element getData(){
		return data;
	}
	
	public void setData(Element associationData){
		data = associationData;
	}
	
	public Element getReference(){
		return reference;
	}
	
	public void setReference(Element associationReference){
		reference = associationReference;
	}


	public abstract void setVisual();
	
	public void setRoutePoints(){
		List<Point2D> routePoints = new ArrayList<Point2D>();
		
		//First point (source)
		routePoints.add(null);
		
		Element pointsNode;
		if(data.getName().equals("classAssociation")){
			pointsNode = reference.getChild("graphics").getChild("points");
		}
		else{
			pointsNode = data.getChild("graphics").getChild("points");
		}
		
		if (pointsNode.getChildren("point").size() == 0 && loopEdge != null){
			int existingEdges = ((DefaultPort)loopEdge.getChildAt(1)).getEdges().size();
			
			Rectangle2D rect = GraphConstants.getBounds(loopEdge.getAttributes());
			int x1,y1,x2,y2;
			x1 = (int)(rect.getX() + rect.getWidth()/3);
			y1 = Math.max(0, (int)(rect.getY() - 25*(existingEdges + 1)));// 0 if the value is negative
			x2 = (int)(rect.getX() + rect.getWidth()*2/3);
			y2 = Math.max(0, (int)(rect.getY() - 25*(existingEdges + 1)));
			
			Element point1Element = (Element)ItSIMPLE.getCommonData().getChild("definedNodes")
				.getChild("elements").getChild("model").getChild("point").clone();
			Element point2Element = (Element)point1Element.clone();
			
			
			//points for the association
			Point2D point1 = new Point2D.Double(x1, y1);
			Point2D point2 = new Point2D.Double(x2, y2);			
			routePoints.add(point1);
			routePoints.add(point2);
			
			//points for xml
			point1Element.setAttribute("x", String.valueOf(x1));
			point1Element.setAttribute("y", String.valueOf(y1));
			point2Element.setAttribute("x", String.valueOf(x2));
			point2Element.setAttribute("y", String.valueOf(y2));			
			
			data.getChild("graphics").getChild("points").addContent(point1Element);
			data.getChild("graphics").getChild("points").addContent(point2Element);

			
		} else{
			
			//middle points
			Iterator points = pointsNode.getChildren("point").iterator();
			while (points.hasNext()){
				Element point = (Element)points.next();
				int x = Integer.parseInt(point.getAttributeValue("x"));
				int y = Integer.parseInt(point.getAttributeValue("y"));
				Point2D thisPoint = new Point2D.Double(x, y);
				routePoints.add(thisPoint);				
			}
	
		}
		//Last point (target)
		routePoints.add(null);
		
		GraphConstants.setPoints(getAttributes(), routePoints);
	}	

}
