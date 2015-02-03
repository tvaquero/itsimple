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

package src.itgraph.uml;


import java.awt.geom.Rectangle2D;
import java.util.Iterator;

import javax.swing.ImageIcon;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jgraph.graph.GraphConstants;

import src.gui.ItSIMPLE;
import src.itgraph.BasicCell;
import src.languages.xml.XMLUtilities;

public class ObjectCell extends BasicCell {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4257773899116675556L;
	
	private String objectAttributes = "";
	
	public ObjectCell(Element object, Element objectReference, Element itsClass){
		super();
		data = object;
		reference = objectReference;
		additionalData = itsClass;
		this.setVisual();
		
	}
		
	public String getObjectAttributes(){
		return objectAttributes;
	}
	
	public void setObjectAttributes(String attributes){
		objectAttributes = attributes;
	}
	
	public void setVisual() {
		this.setUserObject(data.getChildText("name"));
		
		if (additionalData == null){
			reference.getChild("attributes").removeChildren("attribute");
		}
		
		objectAttributes = "";
		
		Iterator attributes = reference.getChild("attributes").getChildren("attribute").iterator();
		while(attributes.hasNext()){
			Element attribute = (Element)attributes.next();
			Element classAttribute = null;
			if (attribute.getAttributeValue("class").equals(additionalData.getAttributeValue("id"))){
				classAttribute = XMLUtilities.getElement(additionalData.getChild("attributes"),attribute.getAttributeValue("id"));	
			}
			else{				
				try {
					XPath path = new JDOMXPath("project/elements/classes/class[@id="
							+attribute.getAttributeValue("class")+"]/attributes/attribute[@id='" +
							attribute.getAttributeValue("id") +"']");
					classAttribute = (Element)path.selectSingleNode(data.getDocument());
				} catch (JaxenException e) {			
					e.printStackTrace();
				}

							
			}	
			
			
			//Get Attribute type
			String strType = "";
			Element typeClass = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/*[@id="+classAttribute.getChildText("type")+"]");
				typeClass = (Element)path.selectSingleNode(classAttribute.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			if (typeClass != null){
				strType = typeClass.getChildText("name");
			}
			
			String strAttribute = "";
			strAttribute = classAttribute.getChildText("name"); 
			Element parameters = classAttribute.getChild("parameters");
			
			// Check if the classAttribute has parameters
			if (parameters.getChildren().size() > 0){
				//objectAttributes = objectAttributes + "\n";
				String eachStrParameter = "";
				
				Iterator iter = attribute.getChild("value").getChildren("parameterizedValue").iterator();
				if(iter.hasNext()){
					objectAttributes = objectAttributes + "<br>";
				}
				while(iter.hasNext()){					
					Element parameterizedValue = (Element) iter.next();
					
					//1. Build parameters values string
					String strParameterValue = "";
					Iterator iterParameter = parameterizedValue.getChild("parameters").getChildren("parameter").iterator();
					while(iterParameter.hasNext()){
						Element parameter = (Element)iterParameter.next();
											
						strParameterValue = strParameterValue + parameter.getChildText("value");
						
						if (iterParameter.hasNext()){
							strParameterValue = strParameterValue + ", ";
						}
					}
					
					//2. Add this parameterized
					eachStrParameter = strAttribute + "("+ strParameterValue +") = ";
					if(strType.equals("String")){
						eachStrParameter = eachStrParameter + "\"" +parameterizedValue.getChildText("value") + "\"";
					}
					else{
						eachStrParameter = eachStrParameter + parameterizedValue.getChildText("value");					
					}
					
					String color = parameterizedValue.getChild("graphics").getChildText("color").trim();
					if(color.equals("")){
						color = "black";
					}
					else{
						color = "#" + color;
					}
					
					eachStrParameter = "<font size='3' face='Arial' color='"+ color +"'>" + eachStrParameter + "</font>";
					
					objectAttributes = (objectAttributes +eachStrParameter + "<br>");
					
				}							
				//objectAttributes = objectAttributes + "\n";
			}
			// No parameters
			else{
				strAttribute = strAttribute + "("+ strType +") = ";
				if(strType.equals("String")){
					strAttribute = strAttribute + "\"" +attribute.getChildText("value") + "\"";
				}
				else{
					strAttribute = strAttribute + attribute.getChildText("value");					
				}
				
				String color = attribute.getChild("graphics").getChildText("color").trim();
				if(color.equals("")){
					color = "black";
				}
				else{
					color = "#" + color;
				}
				
				strAttribute = "<font size='3' face='Arial' color='"+ color +"'>" + strAttribute + "</font>";
				
				objectAttributes = (objectAttributes +strAttribute + "<br>");
				
			}
			
		}
		// set the html format
		objectAttributes = "<html>"+ objectAttributes + "</html>";

		if (additionalData != null){
			ImageIcon icon = new ImageIcon(ItSIMPLE.getIconsPathElement().getText() + additionalData.getChild("graphics").getChildText("icon"));			
			GraphConstants.setIcon(this.getAttributes(), icon);
		}
		
		Element graphics = reference.getChild("graphics");
		int x = Integer.parseInt(graphics.getChild("position").getAttributeValue("x"));
		int y = Integer.parseInt(graphics.getChild("position").getAttributeValue("y"));
		int width = Integer.parseInt(graphics.getChild("size").getAttributeValue("width"));
		int height = Integer.parseInt(graphics.getChild("size").getAttributeValue("height"));
		GraphConstants.setBounds(this.getAttributes(), new Rectangle2D.Double(x,y,width,height));
	}

}
