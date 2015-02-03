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

import java.awt.Component;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.GraphConstants;
import org.jgraph.graph.VertexRenderer;
import org.jgraph.graph.VertexView;

public class TransitionRenderer extends VertexRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3924288191797410744L;

	public TransitionRenderer() {
		super();
		// TODO Auto-generated constructor stub
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
					setText("<html><center><font size='1'><b>"+label.toString()+"</b></center></font></html>");
				else
					setText(null);
			} else
				setText(null);
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
