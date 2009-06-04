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

package itSIMPLE;

import itGraph.ItGraph;

import java.awt.BorderLayout;
import java.util.Iterator;
import java.util.List;

import javax.swing.BoundedRangeModel;
import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;

import planning.PlanSimulator;

public class PlanNavigationList extends JPanel implements ChangeListener{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3381161492254556581L;
	
	private static PlanNavigationList instance = null;
	
	private JList planList;
	private DefaultListModel planListModel;
	private Element movie = null;
	private Element domain = null;
	private Element xmlPlan = null;
	
	private JPanel before = null;
	private JPanel after = null;
	
	private ItGraph beforeGraph = null;
	private ItGraph afterGraph = null;
	
	private int lastEditedDiagramIndex = -1;
	private int replanIndex = -1;
	private Element replanProblem;
	
	// movie panel synchronizing	
	
	private BoundedRangeModel beforeHorScrollModel;
	private BoundedRangeModel afterHorScrollModel;
	private BoundedRangeModel beforeVertScrollModel;
	private BoundedRangeModel afterVertScrollModel;
	
	public PlanNavigationList(){
		super(new BorderLayout());
		
		planListModel = new DefaultListModel();
		planList = new JList(planListModel);
		planList.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		planList.addListSelectionListener(new ListSelectionListener(){

			public void valueChanged(ListSelectionEvent e) {
				
				if(!e.getValueIsAdjusting()){// this makes the listener to be called only once
//					 get action from plan
					int index = planList.getSelectedIndex();
					if(index > -1){
						
						// get before and after scenes
						Element beforeDiagram = (Element)movie.getChildren("objectDiagram").get(index);
						Element afterDiagram = (Element)movie.getChildren("objectDiagram").get(index+1);
						
						if(domain != null){
							Element project = domain.getDocument().getRootElement();
							
							// create the graphs
							//ItGraph beforeGraph = new ItGraph(project, beforeDiagram, "UML");
							beforeGraph.setProject(project);
							beforeGraph.setDiagram(beforeDiagram);							
							beforeGraph.setDomain(domain);
							beforeGraph.setVisible(false);
							beforeGraph.buildDiagram();
							beforeGraph.setVisible(true);
							before.revalidate();
						
							//ItGraph afterGraph = new ItGraph(project, afterDiagram, "UML");
							afterGraph.setProject(project);
							afterGraph.setDiagram(afterDiagram);	
							afterGraph.setDomain(domain);
							afterGraph.setVisible(false);
							afterGraph.buildDiagram();
							afterGraph.setVisible(true);
							after.revalidate();							
							
						}
					}	
				}
			}
			
		});
		
		add(planList, BorderLayout.CENTER);
		
		beforeGraph = new ItGraph(null, null, "UML");
		afterGraph = new ItGraph(null, null, "UML");
	}
	
	public static PlanNavigationList getInstance(){
		if (instance == null){
			instance = new PlanNavigationList();
		}
		return instance;
	}
	
	// if xmlPlan is null, just clear the list
	@SuppressWarnings("unchecked")
	public void setList(Element xmlPlan, Element movie, Element domain, JPanel before, JPanel after){
		planListModel.clear();
		
		this.xmlPlan = xmlPlan;
		this.movie = movie;
		this.domain = domain;
		
		this.before = before;
		this.after = after;
		
		int lineIndex = 0;
		
		if(xmlPlan != null && movie != null){
			Element planNode = xmlPlan.getChild("plan");
			List actions = planNode.getChildren("action");
			for (Iterator iter = actions.iterator(); iter.hasNext();) {
				Element action = (Element) iter.next();

				// start time
				String line = action.getChildText("startTime") + ": ";

				// action name
				line += "(" + action.getAttributeValue("id") + " ";

				// action parameters
				List parameters = action.getChild("parameters")
						.getChildren("parameter");
				for (Iterator iterator = parameters.iterator(); iterator
						.hasNext();) {
					Element parameter = (Element) iterator.next();
					line += parameter.getAttributeValue("id");
					if (iterator.hasNext()) {
						line += " ";
					}
				}
				line += ")";

				// action duration
				String duration = action.getChildText("duration");
				if (!duration.equals("")) {
					line += " [" + duration + "]";
				}
				
				if(lastEditedDiagramIndex > -1 && lineIndex == lastEditedDiagramIndex){
					line += "*";
				}

				// add the line to the list
				planListModel.addElement(line);
				
				lineIndex++;
			}
			
			if(actions.size() > 0){				
				initializeScrollPanes();				
				planList.setSelectedIndex(0);
			}

		}
	}
	
	private void initializeScrollPanes(){
		
		before.removeAll();
		JScrollPane beforeScroll = new JScrollPane(beforeGraph);
		
		beforeHorScrollModel = beforeScroll.getHorizontalScrollBar().getModel();
		beforeHorScrollModel.setValue(0);
		beforeHorScrollModel.addChangeListener(PlanNavigationList.this);
		
		beforeVertScrollModel = beforeScroll.getVerticalScrollBar().getModel();
		beforeVertScrollModel.setValue(0);
		beforeVertScrollModel.addChangeListener(PlanNavigationList.this);
		
		before.add(beforeScroll, BorderLayout.CENTER);		
		
		after.removeAll();
		JScrollPane afterScroll = new JScrollPane(afterGraph);
		
		afterHorScrollModel = afterScroll.getHorizontalScrollBar().getModel();
		afterHorScrollModel.setValue(0);
		afterHorScrollModel.addChangeListener(PlanNavigationList.this);
		
		afterVertScrollModel = afterScroll.getVerticalScrollBar().getModel();
		afterVertScrollModel.setValue(0);
		afterVertScrollModel.addChangeListener(PlanNavigationList.this);
		
		after.add(afterScroll, BorderLayout.CENTER);
	}
	
	public void stateChanged(ChangeEvent e) {
		if(e.getSource() == beforeHorScrollModel){		
			
			double horScrollScale = (double)beforeHorScrollModel.getValue()/
				(double)(beforeHorScrollModel.getMaximum() - 
						beforeHorScrollModel.getMinimum() - 
						beforeHorScrollModel.getExtent());// "extent" is the knob length
			
			// turns off after hor scroll listener
			afterHorScrollModel.removeChangeListener(PlanNavigationList.this);	
			
			afterHorScrollModel.setValue((int)((afterHorScrollModel.getMaximum() -
					afterHorScrollModel.getMinimum() - afterHorScrollModel.getExtent())
					* horScrollScale));
			
			// turns the listener on again
			afterHorScrollModel.addChangeListener(PlanNavigationList.this);
		
		}
		
		else if(e.getSource() == afterHorScrollModel){			
			
			double horScrollScale = (double)afterHorScrollModel.getValue()/
				(double)(afterHorScrollModel.getMaximum() - 
						afterHorScrollModel.getMinimum() - 
						afterHorScrollModel.getExtent());
			
			// turns off before hor scroll listener
			beforeHorScrollModel.removeChangeListener(PlanNavigationList.this);
			
			beforeHorScrollModel.setValue((int)((beforeHorScrollModel.getMaximum() -
					beforeHorScrollModel.getMinimum() - beforeHorScrollModel.getExtent())
					* horScrollScale));
			
			// turns the listener on again
			beforeHorScrollModel.addChangeListener(PlanNavigationList.this);
		
		}
		
		else if(e.getSource() == beforeVertScrollModel){
			
			double vertScrollScale = (double)beforeVertScrollModel.getValue()/
				(double)(beforeVertScrollModel.getMaximum() - 
						beforeVertScrollModel.getMinimum() - 
						beforeVertScrollModel.getExtent());
			
			// turns off after vert scroll listener
			afterVertScrollModel.removeChangeListener(PlanNavigationList.this);
			
			afterVertScrollModel.setValue((int)((afterVertScrollModel.getMaximum() -
					afterVertScrollModel.getMinimum() - afterVertScrollModel.getExtent())
					* vertScrollScale));
			
			// turns the listener on again
			afterVertScrollModel.addChangeListener(PlanNavigationList.this);
		}
		
		else if(e.getSource() == afterVertScrollModel){
			
			double vertScrollScale = (double)afterVertScrollModel.getValue()/
				(double)(afterVertScrollModel.getMaximum() - 
						afterVertScrollModel.getMinimum() - 
						afterVertScrollModel.getExtent());
			
			// turns off before vert scroll listener
			beforeVertScrollModel.removeChangeListener(PlanNavigationList.this);
			
			beforeVertScrollModel.setValue((int)((beforeVertScrollModel.getMaximum() -
					beforeVertScrollModel.getMinimum() - beforeVertScrollModel.getExtent())
					* vertScrollScale));
			
			// turns the listener on again
			beforeVertScrollModel.addChangeListener(PlanNavigationList.this);
		}
		
	}

	/**
	 * @return the planList
	 */
	public JList getPlanList() {
		return planList;
	}
	
	public Element getCurrentState(){
		lastEditedDiagramIndex = planList.getSelectedIndex();
		
		return afterGraph.getDiagram();
	}
	
	public void replaceEditedDiagram(Element diagram){
		Element oldDiagram = (Element)movie.getChildren().get(lastEditedDiagramIndex);
		movie.removeContent(oldDiagram);
		movie.addContent(lastEditedDiagramIndex+1, diagram);		
		setList(xmlPlan, movie, domain, before, after);
		planList.setSelectedIndex(lastEditedDiagramIndex);
		lastEditedDiagramIndex = -1;
	}
	
	public void replan(){
		//this method is invoked when the replan button is pressed
		
		replanIndex = planList.getSelectedIndex();
		
		// get "init" and goal states				
		Element init = (Element)afterGraph.getDiagram().clone();
		init.getChild("sequenceReference").setText("init");
		
		Element problem = ItSIMPLE.getInstance().getProblemsPlanTreeSelectedProblem();
		Element goal = null;
		try {
			XPath path = new JDOMXPath("objectDiagrams/objectDiagram[sequenceReference='goal']");
			goal = (Element)((Element)path.selectSingleNode(problem)).clone();
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		
		Element project = (Element)domain.getDocument().getRootElement().clone();
		// find the domain inside the cloned project (if we clone the domain, it loses its parents)
		Element domain = null;
		try {				
			XPath path = new JDOMXPath("diagrams/planningDomains/domain[@id='"+ 
											this.domain.getAttributeValue("id") +"']");
			domain = (Element)path.selectSingleNode(project);
		} catch (JaxenException e) {			
			e.printStackTrace();
		}
		
		replanProblem = (Element)ItSIMPLE.getCommonData().getChild("definedNodes")
						.getChild("elements").getChild("structure").getChild("problem").clone();
		replanProblem.setAttribute("id", "0");
		replanProblem.getChild("objectDiagrams").removeContent();
		replanProblem.getChild("objectDiagrams").addContent(init);
		replanProblem.getChild("objectDiagrams").addContent(goal);
		
		domain.getChild("planningProblems").addContent(replanProblem);
				
		ItSIMPLE.getInstance().solveReplaningProblem(new Document(project).getRootElement(), replanProblem);		
	}
	
	
	@SuppressWarnings("unchecked")
	public void setPlanListAfterReplaning(Element xmlPlan){
		// the xmlPlan only has the plan from the current state to goal
		final int replanIndex = this.replanIndex;
		final Element oldPlan = this.xmlPlan;
		final Element movie = PlanSimulator.getMovie(xmlPlan, replanProblem);
		final Element oldMovie = PlanNavigationList.this.movie;
		
		//set new action start times
		setNewPlanStarTimes((Element)oldPlan.getChild("plan").getChildren("action").get(replanIndex), xmlPlan);
		
		//remove old actions from previous plan
		while(oldPlan.getChild("plan").getChildren("action").size() > replanIndex+1){			
			Element action = (Element) oldPlan.getChild("plan").getChildren("action").get(replanIndex+1);
			action.detach();
		}
		
		
		// add new action
		while (!xmlPlan.getChild("plan").getChildren().isEmpty()) {
			Element action = (Element) xmlPlan.getChild("plan").getChildren("action").get(0);
			oldPlan.getChild("plan").addContent(action.detach());
		}
		
		ItSIMPLE.getInstance().setPlanList(oldPlan);
		
		new Thread(){
			public void run() {				
				//remove states from old movie
				while(oldMovie.getChildren("objectDiagram").size() > replanIndex + 1){					
					Element state = (Element)oldMovie.getChildren("objectDiagram")
									.get(oldMovie.getChildren("objectDiagram").size()-1);
					state.detach();
				}
				
				
				// add new states to movie
				while(!movie.getChildren("objectDiagram").isEmpty()){
					Element state = (Element)movie.getChildren("objectDiagram").get(0);
					oldMovie.addContent(state.detach());
				}
				
			}
		}.start();
		//XMLUtilities.printXML(oldMovie);
		setList(oldPlan, oldMovie, domain, before, after);
		planList.setSelectedIndex(replanIndex);
		this.replanIndex = -1;
	}
	
	@SuppressWarnings("unchecked")
	private void setNewPlanStarTimes(Element oldPlanLastAction, Element newPlan){		
		int nextActionStartTime = Integer.parseInt(oldPlanLastAction.getChildText("startTime")) + 
										Integer.parseInt(oldPlanLastAction.getChildText("duration"));
		
		for (Iterator iterator = newPlan.getChild("plan").getChildren("action").iterator(); iterator.hasNext();) {
			Element action = (Element) iterator.next();
			action.getChild("startTime").setText(String.valueOf(nextActionStartTime));
			
			nextActionStartTime = Integer.parseInt(action.getChildText("startTime")) + 
										Integer.parseInt(action.getChildText("duration"));
		}
	}
}
