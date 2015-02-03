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


import java.util.Iterator;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jgraph.graph.GraphConstants;

import src.itgraph.BasicCell;

public class ActionAssociation extends BasicAssociation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7781515244620751386L;
	
	private String sourceRolename = "";
	private String targetRolename = "";

	public ActionAssociation() {
		super();
	}

	public ActionAssociation(Object userObject) {
		super(userObject);
	}
	
	public ActionAssociation(Element action, Element actionReference, BasicCell source, BasicCell target){
		super();
		data = action;
		reference = actionReference;
	
		GraphConstants.setEditable(getAttributes(), false);
		
		setVisual();
		
		setSource(source.getChildAt(0));
		if(source == target){
			// self-loop
			setTarget(source.getChildAt(1));
			loopEdge = source;
			
		}

		else{
			setTarget(target.getChildAt(0));
		}
	}
	
	public void setSourceRolename(String name){
		sourceRolename = name;
	}
	
	public String getSourceRolename(){
		return sourceRolename;
	}
	
	public void setTargetRolename(String name){
		targetRolename = name;
	}
	
	public String getTargetRolename(){
		return targetRolename;
	}
	
	public void setVisual(){
		String actionName = "";
		String operator = "";
		String preCond = "";
		String postCond = "";
		
		if (reference != null){
			operator = reference.getChildText("name");
			
			//Get Operator parameters
			String strParameters = "";
			Iterator<?> parameters = reference.getChild("parameters").getChildren("parameter").iterator();
			while(parameters.hasNext()){
				Element parameter = (Element)parameters.next();
				
				Element tyClass = null;
				try {
					XPath path = new JDOMXPath("project/elements/classes/*[@id='"+parameter.getChildText("type")+"']");
					tyClass = (Element)path.selectSingleNode(data.getDocument());
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				String strParameterClass = "";
				if (tyClass != null){
					strParameterClass = ((Element)tyClass).getChildText("name");
				}
				
				strParameters = strParameters + parameter.getChildText("name") + ((!strParameterClass.equals("")) ? ": " + strParameterClass : "" );				

				if (parameters.hasNext()){
					strParameters = strParameters + ", ";
				}
			}
			operator = operator + "(" + strParameters + ")";
			
		}
		else {
			data.getChild("reference").setAttribute("class","");
			data.getChild("reference").setAttribute("operator","");
			operator = data.getChildText("name");
		}
		
		preCond = data.getChildText("precondition");
		postCond = data.getChildText("postcondition");
		
		actionName = operator;
		if (!preCond.trim().equals("")){
			//actionName = actionName + " [" + preCond + "]";
			actionName += "[...]";
		}
		if (!postCond.trim().equals("")){
			//actionName = actionName + " /" + postCond;
			actionName += "/...";
		}
		this.setUserObject(actionName);
	}
	

}
