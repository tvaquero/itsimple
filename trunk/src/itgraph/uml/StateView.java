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

import java.awt.geom.Point2D;

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

public class StateView extends VertexView {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5193393744966901099L;
	
	private StateRenderer RENDERER = new StateRenderer();

	public StateView(Object cell) {
		super(cell);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * Returns the intersection of the bounding rectangle and the straight line
	 * between the source and the specified point p. The specified point is
	 * expected not to intersect the bounds. Note: You must override this method
	 * if you use a different renderer. This is because this method relies on
	 * the VertexRenderer interface, which can not be safely assumed for
	 * subclassers.
	 */
	public Point2D getPerimeterPoint(EdgeView edge, Point2D source, Point2D p) {
		if (getRenderer() instanceof StateRenderer)
			return ((StateRenderer) getRenderer()).getPerimeterPoint(this,
					source, p);
		return super.getPerimeterPoint(edge, source, p);
	}
	
	public CellViewRenderer getRenderer(){
		return RENDERER;
	}

}
