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

import java.awt.geom.Rectangle2D;


import javax.swing.ImageIcon;

import org.jdom.Element;
import org.jgraph.graph.GraphConstants;

import src.itgraph.BasicCell;

public class InitialStateCell extends BasicCell {	

	
	/**
	 * 
	 */
	private static final long serialVersionUID = -988963598173853921L;

	public InitialStateCell(Element data){
		super("");		
		this.data = data;
		GraphConstants.setAutoSize(attributes, true);
		setVisual();
		
	}	
	
	public void setVisual() {
		Element graphics = data.getChild("graphics");
		ImageIcon icon = new ImageIcon("resources/images/" + graphics.getChildText("icon"));
		
				
		GraphConstants.setIcon(getAttributes(), icon);
		
		int x = Integer.parseInt(graphics.getChild("position").getAttributeValue("x"));
		int y = Integer.parseInt(graphics.getChild("position").getAttributeValue("y"));
		int width = Integer.parseInt(graphics.getChild("size").getAttributeValue("width"));
		int height = Integer.parseInt(graphics.getChild("size").getAttributeValue("height"));
		GraphConstants.setBounds(getAttributes(), new Rectangle2D.Double(x,y,width,height));		
	}

}
