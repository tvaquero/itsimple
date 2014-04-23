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

import java.awt.geom.Point2D;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jgraph.graph.AttributeMap;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.GraphConstants;

public class BasicAssociationView extends EdgeView {



	/**
	 * 
	 */
	private static final long serialVersionUID = -6490103045995707413L;

	public BasicAssociationView(Object cell) {
		super(cell);
		
		BasicAssociation association = (BasicAssociation)cell;
		AttributeMap attributes = association.getAttributes();
		
		// main label position
		Element labels;
		if(cell instanceof ClassAssociation){
			labels = association.getReference().getChild("graphics").getChild("labels");
		} else{
			labels = association.getData().getChild("graphics").getChild("labels");
		}
		Element mainLabelPosition = null;
		try {
			XPath path = new JDOMXPath("label[@type='main']/position");
			mainLabelPosition = (Element)path.selectSingleNode(labels);
		} catch (JaxenException e) {
			e.printStackTrace();
		}
		
		if(mainLabelPosition != null){
			if(mainLabelPosition.getAttributeValue("x").equals("") || mainLabelPosition.getAttributeValue("y").equals("")){
				GraphConstants.setLabelPosition(attributes, new Point2D.Double(GraphConstants.PERMILLE/2, -15));

			} else{
				int x = Integer.parseInt(mainLabelPosition.getAttributeValue("x"));
				int y = Integer.parseInt(mainLabelPosition.getAttributeValue("y"));
				GraphConstants.setLabelPosition(attributes, new Point2D.Double(x, y));
			}			
		}
	}

}
