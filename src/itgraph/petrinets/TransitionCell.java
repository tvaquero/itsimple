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

package src.itgraph.petrinets;


import java.awt.geom.Rectangle2D;

import javax.swing.ImageIcon;

import org.jdom.Element;
import org.jgraph.graph.GraphConstants;

import src.itgraph.BasicCell;

public class TransitionCell extends BasicCell{

	/**
	 * 
	 */
	private static final long serialVersionUID = 7327640497994720042L;
	
	public static final int NORMAL_TRANSITION = 1;
	public static final int IMPORT_TRANSITION = 2;
	public static final int EXPORT_TRANSITION = 3;
	private int transitionType;
	private int yOffSet;
	
	public TransitionCell(Element transition, int transitionType, int yOffSet){
		super();
		this.transitionType = transitionType;
		this.yOffSet = yOffSet;
		data = transition;
		
		GraphConstants.setAutoSize(attributes, true);
		this.setVisual();
		
	}	
	
	public void setVisual() {
		Element graphics = data.getChild("graphics");
		ImageIcon icon = null;
		switch(transitionType){
		case NORMAL_TRANSITION:{
			if(data.getChild("toolspecific").getChild("graphics").getChildText("color").equals("black"))
				icon = new ImageIcon("resources/images/basicTransition_black.png");
			else if(data.getChild("toolspecific").getChild("graphics").getChildText("color").equals("green"))
				icon = new ImageIcon("resources/images/basicTransition_green.png");
		}
		break;
		case IMPORT_TRANSITION:{
			if(data.getChild("toolspecific").getChild("graphics").getChildText("color").equals("black"))
				icon = new ImageIcon("resources/images/importTransition_black.png");
			else if(data.getChild("toolspecific").getChild("graphics").getChildText("color").equals("green"))
				icon = new ImageIcon("resources/images/importTransition_green.png");
		}
		break;
		case EXPORT_TRANSITION:{
			if(data.getChild("toolspecific").getChild("graphics").getChildText("color").equals("black"))
				icon = new ImageIcon("resources/images/exportTransition_back.png");
			else if(data.getChild("toolspecific").getChild("graphics").getChildText("color").equals("green"))
				icon = new ImageIcon("resources/images/exportTransition_green.png");
		}
		break;
		}
				
		GraphConstants.setIcon(getAttributes(), icon);
		
		int x = Integer.parseInt(graphics.getChild("position").getAttributeValue("x"));
		int y = Integer.parseInt(graphics.getChild("position").getAttributeValue("y")) + yOffSet;		
		GraphConstants.setBounds(getAttributes(), new Rectangle2D.Double(x,y,50,50));		
	}

}
