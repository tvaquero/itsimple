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

import java.awt.Component;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

public class GraphLabelRenderer extends VertexRenderer {
	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5027430767968184641L;

	public GraphLabelRenderer() {
		super();
	}
	
	public Component getRendererComponent(JGraph graph, CellView view,
			boolean sel, boolean focus, boolean preview) {
		gridColor = graph.getGridColor();
		highlightColor = graph.getHighlightColor();
		lockedHandleColor = graph.getLockedHandleColor();
		isDoubleBuffered = graph.isDoubleBuffered();
		if (view instanceof VertexView) {
			this.view = (VertexView) view;
			setComponentOrientation(graph.getComponentOrientation());
			if (graph.getEditingCell() != view.getCell()) {
				Object label = graph.convertValueToString(view);
				if (label != null)
					setText("<html><font size='3'><b>"+label.toString()+"</b></font></html>");
				else
					setText("");
			} else
				setText("");
			this.hasFocus = focus;
			this.childrenSelected = graph.getSelectionModel()
					.isChildrenSelected(view.getCell());
			this.selected = sel;
			this.preview = preview;
			if (this.view.isLeaf()
					|| GraphConstants.isGroupOpaque(view.getAllAttributes()))
				installAttributes(view);
			else
				resetAttributes();
			return this;
		}
		return null;
	}

}
