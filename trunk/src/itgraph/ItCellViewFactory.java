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

package src.itgraph;

import src.itgraph.petrinets.*;

import org.jgraph.graph.DefaultCellViewFactory;
import org.jgraph.graph.EdgeView;
import org.jgraph.graph.VertexView;

import src.itgraph.petrinets.Arc;
import src.itgraph.petrinets.ArcView;
import src.itgraph.petrinets.PlaceCell;
import src.itgraph.petrinets.PlaceView;
import src.itgraph.petrinets.TransitionCell;
import src.itgraph.petrinets.TransitionView;
import src.itgraph.uml.ActionAssociation;
import src.itgraph.uml.ActionAssociationView;
import src.itgraph.uml.ActorCell;
import src.itgraph.uml.ActorView;
import src.itgraph.uml.ClassAssociation;
import src.itgraph.uml.ClassAssociationView;
import src.itgraph.uml.ClassCell;
import src.itgraph.uml.ClassView;
import src.itgraph.uml.Dependency;
import src.itgraph.uml.DependencyView;
import src.itgraph.uml.EnumerationCell;
import src.itgraph.uml.EnumerationView;
import src.itgraph.uml.FinalStateCell;
import src.itgraph.uml.FinalStateView;
import src.itgraph.uml.Generalization;
import src.itgraph.uml.GeneralizationView;
import src.itgraph.uml.InitialStateCell;
import src.itgraph.uml.InitialStateView;
import src.itgraph.uml.ObjectAssociation;
import src.itgraph.uml.ObjectAssociationView;
import src.itgraph.uml.ObjectCell;
import src.itgraph.uml.ObjectView;
import src.itgraph.uml.StateCell;
import src.itgraph.uml.StateView;
import src.itgraph.uml.UseCaseAssociation;
import src.itgraph.uml.UseCaseAssociationView;
import src.itgraph.uml.UseCaseCell;
import src.itgraph.uml.UseCaseView;

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
