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
*			Fernando Sette,
*			Victor Romero.
**/

package languages.petrinets;

import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

public class PetriNetAnalysis {
	
	public static String firedTransitions = "";
	
	public static Element runPetriNet(Element pnml){
		//XMLUtilities.printXML(pnml);
		Element net = pnml.getChild("net");
		HashSet transitionsToBeFired = new HashSet();
		HashSet enabledTransitions = new HashSet();
		HashSet conflictedTransitions = new HashSet();
		
		//find all enabled transitions
		List transitions = net.getChildren("transition");
		for (Iterator transitionsIter = transitions.iterator(); transitionsIter.hasNext();) {
			Element transition = (Element) transitionsIter.next();
			transition.getChild("toolspecific").getChild("graphics").getChild("color").setText("black");
			//get all arcs that target the transition
			String transitionID = transition.getAttributeValue("id");
			List arcs = null;
			try {
				XPath path = new JDOMXPath("arc[@target='"+transitionID+"']");
				arcs = path.selectNodes(net);
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			boolean isEnabled = true;
			//check if source places have enough marking
			for (Iterator arcsIter = arcs.iterator(); arcsIter.hasNext();) {
				Element arc = (Element) arcsIter.next();
				int weight = Integer.parseInt(arc.getChild("inscription").getChild("text").getText());
				String sourcePlaceID = arc.getAttributeValue("source");
				Element place = null;
				try {
					XPath path = new JDOMXPath("place[@id='"+sourcePlaceID+"']");
					place = (Element) path.selectSingleNode(net);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(place!=null){
					int marking = Integer.parseInt(place.getChild("initialMarking").getChildText("text"));
					isEnabled = isEnabled && (marking >= weight);
				}
			}
			if(isEnabled)
				enabledTransitions.add(transitionID);
		}

		//deal with conflicts - arcs going from the same place to diferent transitions
		for (Iterator enabledTransitionsiter = enabledTransitions.iterator(); enabledTransitionsiter.hasNext();) {
			String enabledTransitionID = (String) enabledTransitionsiter.next();
			//check if source places are shared with other transition
			List arcs = null;
			try {
				XPath path = new JDOMXPath("arc[@target='"+enabledTransitionID+"']");
				arcs = path.selectNodes(net);
			} catch (JaxenException e) {			
				e.printStackTrace();
			}

			for (Iterator arcsIter = arcs.iterator(); arcsIter.hasNext();) {
				Element arc = (Element) arcsIter.next();
				//int weight = Integer.parseInt(arc.getChild("inscription").getChild("text").getText());
				String sourcePlaceID = arc.getAttributeValue("source");
				Element place = null;
				try {
					XPath path = new JDOMXPath("place[@id='"+sourcePlaceID+"']");
					place = (Element) path.selectSingleNode(net);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(place!=null){
					String placeID = place.getAttributeValue("id");
					List placeArcs = null;
					try {
						XPath path = new JDOMXPath("arc[@source='"+placeID+"']");
						placeArcs = path.selectNodes(net);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					if(placeArcs.size() > 1){
						HashSet conflictTransitions = new HashSet();
						for (Iterator placeArcsIter = placeArcs.iterator(); placeArcsIter.hasNext();) {
							Element placeArc = (Element) placeArcsIter.next();
							String conflicTransitionID = placeArc.getAttributeValue("target");

							if(enabledTransitions.contains(conflicTransitionID))
								conflictTransitions.add(conflicTransitionID);
						}
						if(conflictTransitions.size() > 1){
							Random random = new Random();
							double size = conflictTransitions.size();
							double intervalSize = 1/size;
							double randomNumber = random.nextDouble();
							double transitionToBeFiredIndex = 0;
							while((transitionToBeFiredIndex + 1)*intervalSize < randomNumber)
								transitionToBeFiredIndex++;
							int i = 0;
							for (Iterator conflictTransitionsIter = conflictTransitions.iterator(); conflictTransitionsIter.hasNext();) {
								String id = (String) conflictTransitionsIter.next();
								if(i != transitionToBeFiredIndex && !transitionsToBeFired.contains(id)){
									conflictedTransitions.add(id);
								}
								else{
									if(!conflictedTransitions.contains(id))
										transitionsToBeFired.add(id);
								}
								i++;
							}
						}
					}
				}
			}

		}
		//create firing list
		firedTransitions = "";
		//int size = 0;
		for (Iterator enabledTransitionsIter = enabledTransitions.iterator(); enabledTransitionsIter.hasNext();) {
			String enabledTransitionID = (String) enabledTransitionsIter.next();
			if(!conflictedTransitions.contains(enabledTransitionID)){
				Element transition = null;
				try {
					XPath path = new JDOMXPath("transition[@id='"+enabledTransitionID+"']");
					transition = (Element) path.selectSingleNode(net);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				transition.getChild("toolspecific").getChild("graphics").getChild("color").setText("green");
				//System.out.println(transition.getChild("name").getChildText("text"));
				//XMLUtilities.printXML(transition);
				if(!transition.getChild("name").getChildText("text").trim().equals(""))
					firedTransitions += transition.getChild("name").getChildText("text") + " ";
				else
					firedTransitions += enabledTransitionID + " ";
				transitionsToBeFired.add(enabledTransitionID);
			}
		}
		if(enabledTransitions.size() > 0)
			firedTransitions += "<br>";
		//fire transitions
		for (Iterator transitionsToBeFiredIter = transitionsToBeFired.iterator(); transitionsToBeFiredIter.hasNext();) {
			String id = (String) transitionsToBeFiredIter.next();
			//get pre-conditions
			List sourceArcs = null;
			try {
				XPath path = new JDOMXPath("arc[@target='"+id+"']");
				sourceArcs = path.selectNodes(net);
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			for (Iterator arcsIter = sourceArcs.iterator(); arcsIter.hasNext();) {
				Element arc = (Element) arcsIter.next();
				int weight = Integer.parseInt(arc.getChild("inscription").getChildText("text"));
				Element preconditionPlace = null;
				try {
					XPath path = new JDOMXPath("place[@id='"+arc.getAttributeValue("source")+"']");
					preconditionPlace = (Element) path.selectSingleNode(net);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				//reduce marking
				if(!arc.getChild("type").getAttributeValue("value").equals("read")){
					int currentMarking = Integer.parseInt(preconditionPlace.getChild("initialMarking").getChildText("text"));
					int newMarking = currentMarking - weight;
					preconditionPlace.getChild("initialMarking").getChild("text").setText(Integer.toString(newMarking));
				}
			}
			//get post-conditions
			List targetArcs = null;
			try {
				XPath path = new JDOMXPath("arc[@source='"+id+"']");
				targetArcs = path.selectNodes(net);
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			for (Iterator arcsIter = targetArcs.iterator(); arcsIter.hasNext();) {
				Element arc = (Element) arcsIter.next();
				int weight = Integer.parseInt(arc.getChild("inscription").getChildText("text"));
				Element preconditionPlace = null;
				try {
					XPath path = new JDOMXPath("place[@id='"+arc.getAttributeValue("target")+"']");
					preconditionPlace = (Element) path.selectSingleNode(net);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				//increace marking
				if(!arc.getChild("type").getAttributeValue("value").equals("read")){
					int currentMarking = Integer.parseInt(preconditionPlace.getChild("initialMarking").getChildText("text"));
					int newMarking = currentMarking + weight;
					preconditionPlace.getChild("initialMarking").getChild("text").setText(Integer.toString(newMarking));
				}
			}
		}
		//XMLUtilities.printXML(pnml);
		return pnml;
	}

	//Teste
	/*public static void main(String[] args){
		Element project = null;
		try {
			project = XMLUtilities.readFromFile("examples/BlocksDomainv1.xml").getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(project != null){
			Element tree = toPNML.buildPetriNet(project);
			runPetriNet(tree);
			XMLUtilities.printXML(tree);
		}
	}*/
}
