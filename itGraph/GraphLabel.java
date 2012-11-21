/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2012 University of Sao Paulo
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

import java.awt.geom.Rectangle2D;

import org.jdom.Element;
import org.jgraph.graph.GraphConstants;

public class GraphLabel extends BasicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6680044356751704607L;
	
	public GraphLabel(Element data, int x, int y){
		super(data.getChildText("text"));
		this.data = data;		
		
		GraphConstants.setAutoSize(getAttributes(), true);		
		setVisual();
	}
	
	@Override
	public void setVisual() {
		Element cellPosition = data.getParentElement().getChild("graphics").getChild("position");
		int x = Integer.parseInt(cellPosition.getAttributeValue("x")) +
			Integer.parseInt(data.getChild("graphics").getChild("offset").getAttributeValue("x"));
		int y = Integer.parseInt(cellPosition.getAttributeValue("y")) +
			Integer.parseInt(data.getChild("graphics").getChild("offset").getAttributeValue("y"));
		GraphConstants.setBounds(getAttributes(), new Rectangle2D.Double(x, y, 0, 0));
	}

}
