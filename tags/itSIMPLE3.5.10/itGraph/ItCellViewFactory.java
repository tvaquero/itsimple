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

package itGraph;

import itGraph.PetriNetElements.*;
import itGraph.UMLElements.ActionAssociation;
import itGraph.UMLElements.ActionAssociationView;
import itGraph.UMLElements.ActorCell;
import itGraph.UMLElements.ActorView;
import itGraph.UMLElements.ClassAssociation;
import itGraph.UMLElements.ClassAssociationView;
import itGraph.UMLElements.ClassCell;
import itGraph.UMLElements.ClassView;
import itGraph.UMLElements.Dependency;
import itGraph.UMLElements.DependencyView;
import itGraph.UMLElements.EnumerationCell;
import itGraph.UMLElements.EnumerationView;
import itGraph.UMLElements.FinalStateCell;
import itGraph.UMLElements.FinalStateView;
import itGraph.UMLElements.Generalization;
import itGraph.UMLElements.GeneralizationView;
import itGraph.UMLElements.InitialStateCell;
import itGraph.UMLElements.InitialStateView;
import itGraph.UMLElements.ObjectAssociation;
import itGraph.UMLElements.ObjectAssociationView;
import itGraph.UMLElements.ObjectCell;
import itGraph.UMLElements.ObjectView;
import itGraph.UMLElements.StateCell;
import itGraph.UMLElements.StateView;
import itGraph.UMLElements.UseCaseAssociation;
import itGraph.UMLElements.UseCaseAssociationView;
import itGraph.UMLElements.UseCaseCell;
import itGraph.UMLElements.UseCaseView;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

public class ItCellViewFactory extends DefaultCellViewFactory {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2346752196639817463L;

	public ItCellViewFactory() {
		super();
	}
	
	protected EdgeView createEdgeView (Object e){
		EdgeView edge = null;
		
		if (e instanceof ClassAssociation){
			edge = new ClassAssociationView(e);
		}
		
		else if (e instanceof UseCaseAssociation){
			edge = new UseCaseAssociationView(e);
		}
		
		else if (e instanceof Generalization){
			edge = new GeneralizationView(e);
		}
		
		else if(e instanceof ActionAssociation){
			edge = new ActionAssociationView(e);
		}
		
		else if(e instanceof ObjectAssociation){
			edge = new ObjectAssociationView(e);
		}
		
		else if(e instanceof Dependency){
			edge = new DependencyView(e);
		}
		
		else if(e instanceof Arc){
			edge = new ArcView(e);
		}
		
		else {
			edge = new EdgeView(e);
		}
		
		return edge;
	}
	
	protected VertexView createVertexView (Object v){
		
		VertexView vertex = null;
	    
		if(v instanceof ActorCell){
			vertex = new ActorView(v);
		}
		
		else if (v instanceof UseCaseCell){
	    	vertex = new UseCaseView(v);
	    }
		
		else if(v instanceof ClassCell){
			vertex = new ClassView(v);
		}
		
		else if(v instanceof ObjectCell){
			vertex = new ObjectView(v);
		}
		
		else if(v instanceof StateCell){
			vertex = new StateView(v);
		}
		
		else if(v instanceof PlaceCell){
			vertex = new PlaceView(v);
		}
		
		else if(v instanceof TransitionCell){
			vertex = new TransitionView(v);
		}
		
		else if(v instanceof InitialStateCell){
			vertex = new InitialStateView(v);
		}
		
		else if(v instanceof FinalStateCell){
			vertex = new FinalStateView(v);
		}
		
		else if(v instanceof GraphLabel){
			vertex = new GraphLabelView(v);
		}
		
		else if(v instanceof EnumerationCell){
			vertex = new EnumerationView(v);
		}
		
		else{
			vertex = new VertexView(v);
		}
	    
	    return vertex;		
	}

}
