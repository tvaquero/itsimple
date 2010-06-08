/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2010 Universidade de Sao Paulo
* 
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
*		 	Victor Romero.
**/

package update;

import itSIMPLE.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

public class VersionUpdater {
	
	// currently supported version: 3.0.10
	
	public static void updateVersion(Element project){
		
		
		Element version = project.getChild("generalInformation").getChild("version");
		
		if(version.getText().equals("2.0.20")){
			// update version node
			version.setText("2.0.21");
			
			// add graphic nodes to parameterized values
			List<?> parameterizedValues = null;
			try {
				XPath path = new JDOMXPath("descendant::parameterizedValue");
				parameterizedValues = path.selectNodes(project);
			} catch (JaxenException e) {
				e.printStackTrace();
			}
			
			for (Iterator<?> iter = parameterizedValues.iterator(); iter
					.hasNext();) {
				Element parameterizedValue = (Element) iter.next();
				
				Element graphics = new Element("graphics");
				graphics.addContent(new Element("color"));
				
				parameterizedValue.addContent(graphics);
			}
			
			// add graphic nodes to object attributes nodes
			List<?> attributes = null;
			try {
				XPath path = new JDOMXPath("/project/diagrams/planningDomains/domain/repositoryDiagrams/" +
						"repositoryDiagram/objects/object/attributes/attribute | " +
						"/project/diagrams/planningDomains/domain/planningProblems/problem/objectDiagrams/" +
						"objectDiagram/objects/object/attributes/attribute");
				attributes = path.selectNodes(project);
			} catch (JaxenException e) {
				e.printStackTrace();
			}
			
			for (Iterator<?> iter = attributes.iterator(); iter
					.hasNext();) {
				Element attribute = (Element) iter.next();
				
				Element graphics = new Element("graphics");
				graphics.addContent(new Element("color"));
				
				attribute.addContent(graphics);
			}
		}
		
		if(version.getText().equals("2.0.21")){
			// update version node
			version.setText("2.0.22");
			
			// add metrics node to all problems
			
			List<?> problems = null;
			try {
				XPath path = new JDOMXPath("diagrams/planningDomains/domain/planningProblems/problem");
				problems = path.selectNodes(project);
			} catch (JaxenException e) {
				e.printStackTrace();
			}
			
			for (Iterator<?> iterator = problems.iterator(); iterator.hasNext();) {
				Element problem = (Element) iterator.next();
				
				problem.addContent(new Element("metrics"));
			
				
			}
			
		}
		
		if(version.getText().equals("2.0.22")){
			version.setText("2.0.30");
		}
                
                if(version.getText().equals("2.0.30")){
			version.setText("2.1.10");
		}
                if(version.getText().equals("2.1.10")){
                    //1. Insert metrics to the domain
                    //2. Change metrics to quality metrics at every problem
                    //3. Add timing diagrams node to the diagrams


                        version.setText("3.0.10");
                        
                        //1. add metrics node to all domains
			List<?> domains = null;
			try {
				XPath path = new JDOMXPath("diagrams/planningDomains/domain");
				domains = path.selectNodes(project);
			} catch (JaxenException e) {
				e.printStackTrace();
			}
			
			for (Iterator<?> iterator = domains.iterator(); iterator.hasNext();) {
				Element domain = (Element) iterator.next();
                                if (domain.getChild("metrics") == null){
                                    domain.addContent(new Element("metrics"));
                                }
				
			}
                        
                        //2. change metrics to quality metrics at every problem.

                        List<?> metricsSet = null;
			try {
				XPath path = new JDOMXPath("diagrams/planningDomains/domain/planningProblems/problem/metrics");
				metricsSet = path.selectNodes(project);
			} catch (JaxenException e) {
				e.printStackTrace();
			}


                        for (Iterator<?> iterator = metricsSet.iterator(); iterator.hasNext();) {

                            Element metricSet = (Element) iterator.next();
                            List<Element> newMetrics = new ArrayList();

                            //gather old metrics and create a new quality metric for them
                            List<Element> metrics = metricSet.getChildren("metric");
                            for (Iterator<?> iter = metrics.iterator(); iter.hasNext();) {
                                    Element metric = (Element) iter.next();
                                    //we will change it for a qualityMetric (type: expression)
                                    Element qualityMetric = (Element)ItSIMPLE.getCommonData().getChild("definedNodes").getChild("elements").getChild("model").getChild("qualityMetric").clone();

                                    qualityMetric.setAttribute("id", metric.getAttributeValue("id"));
                                    qualityMetric.getChild("name").setText(metric.getChildText("name"));
                                    qualityMetric.getChild("enabled").setText(metric.getChildText("enabled"));
                                    qualityMetric.getChild("type").setText("expression");
                                    qualityMetric.getChild("intention").setText(metric.getChildText("type"));
                                    qualityMetric.getChild("expression").getChild("rule").setText(metric.getChildText("function"));

                                    newMetrics.add(qualityMetric);
                            }

                            //remove allif(version.getText().equals("2.1.10")){ old metrics
                            metricSet.removeChildren("metric");

                            //add new quality metrics
                            for (Iterator<?> iter1 = newMetrics.iterator(); iter1.hasNext();) {
                                    Element qMetric = (Element) iter1.next();
                                    //we will change it for a qualityMetric (type: expression)
                                    metricSet.addContent(qMetric);
                            }


                        }


                        //3. add timing diagram node
                        if (project.getChild("diagrams").getChild("timingDiagrams") == null){
                            project.getChild("diagrams").addContent(new Element("timingDiagrams"));
                        }


		}
                if(version.getText().equals("3.0.10")){
                    version.setText("3.1.10");

                }
		
		
	}
	
}
