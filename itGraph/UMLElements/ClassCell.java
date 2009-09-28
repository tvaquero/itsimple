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

import itGraph.BasicCell;
import itSIMPLE.ItSIMPLE;

import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.ImageIcon;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jgraph.graph.GraphConstants;

public class ClassCell extends BasicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1102235788890104541L;
	
	private String classAttributes;
	private String classOperators;
	
	public ClassCell(Element graphClass, Element classReference){
		super(graphClass.getChildText("name"));	
		
		data = graphClass;
		reference = classReference;
		this.setVisual();
				
	}
	
	public String getClassAttributes(){
		return classAttributes;
	}
	
	public void setClassAttributes(String attributes){
		classAttributes = attributes;
	}
	
	public String getClassOperators(){
		return classOperators;
	}
	
	public void setClassOperators(String operators){
		classOperators = operators;
	}
	
	public void setVisual() {
		
		Iterator attributes = data.getChild("attributes").getChildren("attribute").iterator();
		
		classAttributes = "";
		classOperators = "";
		
		
		String text = "";
		while(attributes.hasNext()){
			Element attribute = (Element)attributes.next();
			
			//Get Attribute type
			String strType = "";
			Element typeClass = null;
			try {
				XPath path = new JDOMXPath("*[@id='"+attribute.getChildText("type")+"']");
				typeClass = (Element)path.selectSingleNode(data.getParentElement());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			if (typeClass != null){
				strType = typeClass.getChildText("name");
			}
			String eachAttribute = attribute.getChildText("name");
			
			//1. Parameters
			Element parameters = attribute.getChild("parameters");
			if (parameters.getChildren().size() > 0){
				String strParameters = "";
				Iterator iter = parameters.getChildren("parameter").iterator();
				while(iter.hasNext()){
					Element parameter = (Element)iter.next();
					
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
					
					if (iter.hasNext()){
						strParameters = strParameters + ", ";
					}
				}
				eachAttribute = eachAttribute + "(" + strParameters + ")";
			}
			
			// 2. Multiplicity
			String multiplicity = attribute.getChildText("multiplicity");
			if (!multiplicity.equals("")){
				eachAttribute = eachAttribute + " [" + multiplicity + "]";
			}
			
			//3. Include attribute Type
			eachAttribute = eachAttribute + " : "+ strType;
			
			
			//4. Initial Value
			if (!attribute.getChildText("initialValue").trim().equals("")){
				eachAttribute = eachAttribute + " = " + attribute.getChildText("initialValue").trim();
			}				
			
			// 5. changeability
			String changeability = attribute.getChildText("changeability");
			if (!(changeability.equals("") || changeability.equals("changeable"))){
				eachAttribute = eachAttribute + " {" + changeability + "}";
			}
			
			//6. Final format and color
			String color = reference.getChild("graphics").getChildText("color").trim();
			if(color.equals("")){
				color = "black";
			}
			else{
				color = "#" + color;
			}
			text = text + "<font size='3' face='Arial' color='"+ color +"'>" + eachAttribute + "</font>";
			
			if (attributes.hasNext()){
				text = text+"<br>";
			}
			
		}
		classAttributes = (text.equals("")) ?"" :"<html>"+text+"</html>";
		

		text = "";
		Iterator operators = data.getChild("operators").getChildren("operator").iterator();
		while(operators.hasNext()){
			Element operator = (Element)operators.next();
			
			//Get Operator parameters
			String strParameters = "";
			Iterator parameters = operator.getChild("parameters").getChildren("parameter").iterator();
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
			text = (text + "<font size='3' face='Arial'>"+operator.getChildText("name") +"("+ strParameters +")"+"</font>");
			if (operators.hasNext()){
				text = text+"<br>";
			}			
		}
		classOperators = (text.equals("")) ?"" :"<html>"+text+"</html>";
		
		
		Element graphics 		= reference.getChild("graphics");
		Element dataGraphics 	= data.getChild("graphics");
		ImageIcon icon = new ImageIcon(ItSIMPLE.getIconsPathElement().getText() + dataGraphics.getChildText("icon"));			
		GraphConstants.setIcon(this.getAttributes(), icon);
		
		int x = Integer.parseInt(graphics.getChild("position").getAttributeValue("x"));
		int y = Integer.parseInt(graphics.getChild("position").getAttributeValue("y"));
		int width = Integer.parseInt(graphics.getChild("size").getAttributeValue("width"));
		int height = Integer.parseInt(graphics.getChild("size").getAttributeValue("height"));
		GraphConstants.setBounds(this.getAttributes(), new Rectangle2D.Double(x,y,width,height));		
	
	}

}
