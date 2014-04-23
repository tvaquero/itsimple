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

import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.VertexView;

public class UseCaseView extends VertexView {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2199323850187913162L;
	
	private static final UseCaseRenderer RENDERER = new UseCaseRenderer();

	public UseCaseView() {
		super();
		// TODO Auto-generated constructor stub
	}

	public UseCaseView(Object cell) {
		super(cell);
		// TODO Auto-generated constructor stub
	}
	
	public CellViewRenderer getRenderer()
    {
        return RENDERER;
    }

}
