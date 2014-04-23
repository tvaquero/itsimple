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

import languages.xml.XMLUtilities;
import java.io.File;
import java.util.Iterator;
import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;

public class UpdateProjects {
	
	
	public static void main(String[] args) {
		
		if(args.length == 1){
			/*Document doc = null;
			//Document commonDoc = null;
			
			try {
				System.out.println("abrindo arquivo: " + args[0]);
				doc = XMLUtilities.readFromFile(args[0]);				
				//commonDoc = XMLUtilities.readFromFile("resources/settings/commonData.xml");
				docCopy = (Document)doc.clone();
			} catch (Exception e) {			
				e.printStackTrace();
			}*/
			
			boolean succes = true;
			Document docCopy = null;
			Document doc = null;
			File dirPath = new File(args[0]);
			String[] files = dirPath.list();
			for (int i = 0; i < files.length; i++) {
				try {				
					
					File current = new File(args[0] + File.separator + files[i]);
					
					if(current.isFile()){
						System.out.println("abrindo arquivo: " + files[i]);
						
						doc = XMLUtilities.readFromFile(current.getPath());					
						docCopy = (Document)doc.clone();
					}
					else{
						doc = null;
						docCopy = null;
					}

				} catch (Exception e) {			
					e.printStackTrace();
				}
				
				//version 2.0.20 -> 2.0.21				
				// adding graphics node in parameterizedValue and object attribute nodes
				
				if(doc != null){
					try{
						Element project = doc.getRootElement();
						
						project.getChild("generalInformation").getChild("version").setText("2.0.21");
						
						List parameterizedValues = null;
						try {
							XPath path = new JDOMXPath("descendant::parameterizedValue");
							parameterizedValues = path.selectNodes(project);
						} catch (JaxenException e) {
							e.printStackTrace();
						}
						
						for (Iterator iter = parameterizedValues.iterator(); iter
								.hasNext();) {
							Element parameterizedValue = (Element) iter.next();
							
							Element graphics = new Element("graphics");
							graphics.addContent(new Element("color"));
							
							parameterizedValue.addContent(graphics);
						}
						
						List attributes = null;
						try {
							XPath path = new JDOMXPath("/project/diagrams/planningDomains/domain/repositoryDiagrams/" +
									"repositoryDiagram/objects/object/attributes/attribute | " +
									"/project/diagrams/planningDomains/domain/planningProblems/problem/objectDiagrams/" +
									"objectDiagram/objects/object/attributes/attribute");
							attributes = path.selectNodes(project);
						} catch (JaxenException e) {
							e.printStackTrace();
						}
						
						for (Iterator iter = attributes.iterator(); iter
								.hasNext();) {
							Element attribute = (Element) iter.next();
							
							Element graphics = new Element("graphics");
							graphics.addContent(new Element("color"));
							
							attribute.addContent(graphics);
						}
					
					} catch (RuntimeException e) {				
						e.printStackTrace();
						succes = false;
						break;
					}
					
					if (succes) {
						// saves old file
						String oldFileName = args[0] + File.separator + files[i] + "~";
						System.out.println("salvando arquivo: " + oldFileName);
						XMLUtilities.writeToFile(oldFileName, docCopy);	
						
						XMLUtilities.writeToFile(args[0] + File.separator + files[i], doc);
						System.out.println("salvando arquivo: " + files[i]);
					}
				}				
				
			}
			
			
			try {		
				
				
				/*
				// version 2.0 -> 2.0.20
				//update version
				Element project = doc.getRootElement();
				project.getChild("generalInformation").getChild("version").setText("2.0.20");
				
				//model classes
				List modelClasses = null;//project.getChild("elements").getChild("classes").getChildren("class");				
				try {
					XPath path = new JDOMXPath("elements/classes/class[@id > '4']");
					modelClasses = path.selectNodes(project);
				} catch (JaxenException e) {
					e.printStackTrace();
				}				
				for (Iterator iter = modelClasses.iterator(); iter.hasNext();) {
					Element modelClass = (Element) iter.next();
					
					// attributes
					List attributes = modelClass.getChild("attributes").getChildren("attribute");
					for (Iterator iterator = attributes.iterator(); iterator
							.hasNext();) {
						Element attribute = (Element) iterator.next();
						Element graphics = new Element("graphics");
						graphics.addContent(new Element("color"));
						attribute.addContent(graphics);
					}
					
					//operators
					List operators = modelClass.getChild("operators").getChildren("operator");
					for (Iterator iterator = operators.iterator(); iterator
							.hasNext();) {
						Element operator = (Element) iterator.next();
						operator.addContent(new Element("constraints"));
						Element graphics = new Element("graphics");
						graphics.addContent(new Element("color"));
						operator.addContent(graphics);
						
						operator.getChild("timeConstraints").getChild("duraction").setName("duration");
					}
					
					//generalization
					Element genGraphics = modelClass.getChild("generalization").getChild("graphics");
					
					// label
					Element labels = genGraphics.getChild("labels");
					Element mainLabel = labels.getChild("mainLabel");
					labels.removeContent(mainLabel);
					Element label = new Element("label");
					label.setAttribute("type", "main");
					label.setAttribute("identifier", "");									
					label.addContent(mainLabel.getChild("position").detach());
					labels.addContent(label);
					
					genGraphics.addContent(new Element("color"));
					genGraphics.addContent(new Element("lineStyle"));
					
					// class color
					modelClass.getChild("graphics").addContent(new Element("color"));				
				}
				
				// model class association and reference classAssociation (supposing only one class diagram in the project)
				List classAssociations = project.getChild("elements").getChild("classAssociations").getChildren("classAssociation");
				for (Iterator iter = classAssociations.iterator(); iter.hasNext();) {
					Element classAssociation = (Element) iter.next();
					// create the labels strucutre - the labels will be all in the reference, not in the model
					Element main = new Element("label");				
					main.setAttribute("type", "main");
					main.setAttribute("identifier", "");
					
					Element rolename1 = new Element("label");
					rolename1.setAttribute("type", "rolename");
					rolename1.setAttribute("identifier", "1");
					
					Element rolename2 = new Element("label");
					rolename2.setAttribute("type", "rolename");
					rolename2.setAttribute("identifier", "2");
					
					Element multiplicity1 = new Element("label");
					multiplicity1.setAttribute("type", "multiplicity");
					multiplicity1.setAttribute("identifier", "1");
					
					Element multiplicity2 = new Element("label");
					multiplicity2.setAttribute("type", "multiplicity");
					multiplicity2.setAttribute("identifier", "2");
					
					Element changeability = new Element("label");				
					changeability.setAttribute("type", "changeability");
					changeability.setAttribute("identifier", "");
					
					// set the position values that are currently in the model, except for the main label				
					List associationEnds = classAssociation.getChild("associationEnds").getChildren("associationEnd");
					Element ae1 = (Element)associationEnds.get(0);
					Element ae2 = (Element)associationEnds.get(1);
					
					rolename1.addContent(ae1.getChild("rolename").getChild("graphics").getChild("label").getChild("position").detach());
					rolename2.addContent(ae2.getChild("rolename").getChild("graphics").getChild("label").getChild("position").detach());
					multiplicity1.addContent(ae1.getChild("multiplicity").getChild("graphics").getChild("label").getChild("position").detach());
					multiplicity2.addContent(ae2.getChild("multiplicity").getChild("graphics").getChild("label").getChild("position").detach());
					changeability.addContent(classAssociation.getChild("changeability").getChild("graphics").getChild("label").getChild("position").detach());
					
					// find the reference for that classAssociation
					Element associationRef = null;
					try {
						XPath path = new JDOMXPath("diagrams/classDiagrams/classDiagram/associations/classAssociation[@id='"+
								classAssociation.getAttributeValue("id")+"']");
						associationRef = (Element)path.selectSingleNode(project);
					} catch (JaxenException e) {				
						e.printStackTrace();
					}
					if(associationRef != null){
						Element graphics = associationRef.getChild("graphics");
						Element labels = graphics.getChild("labels");
						
						//set the main label position (which is in the reference), and remove the old one
						main.addContent(labels.getChild("mainLabel").getChild("position").detach());
						labels.removeChild("mainLabel");
						
						// add the label nodes
						labels.addContent(main);
						labels.addContent(rolename1);
						labels.addContent(rolename2);
						labels.addContent(multiplicity1);
						labels.addContent(multiplicity2);
						labels.addContent(changeability);
						
						// pass the points node from the model to the reference
						graphics.removeChild("points");
						graphics.addContent(classAssociation.getChild("graphics").getChild("points").detach());
						
						// add color and lineStyle nodes
						graphics.addContent(new Element("color"));
						graphics.addContent(new Element("lineStyle"));
						

						
						// remove all graphics nodes from the model (rolename, mult., chang., etc.)
						classAssociation.removeChild("graphics");
						classAssociation.getChild("changeability").removeChild("graphics");
						ae1.getChild("rolename").removeChild("graphics");
						ae2.getChild("rolename").removeChild("graphics");
						ae1.getChild("multiplicity").removeChild("graphics");
						ae2.getChild("multiplicity").removeChild("graphics");
					}
				}
				
				// diagrams
				// use case diagrams
				List useCaseDiagrams = project.getChild("diagrams").getChild("useCaseDiagrams").getChildren("useCaseDiagram");
				for (Iterator iter = useCaseDiagrams.iterator(); iter.hasNext();) {
					Element useCaseDiagram = (Element) iter.next();
					
					// actors
					List actors = useCaseDiagram.getChild("actors").getChildren("actor");
					for (Iterator iterator = actors.iterator(); iterator.hasNext();) {
						Element actor = (Element) iterator.next();
						actor.getChild("graphics").addContent(new Element("color"));
					}
					
					// use cases
					List useCases = useCaseDiagram.getChild("useCases").getChildren("useCase");
					for (Iterator iterator = useCases.iterator(); iterator.hasNext();) {
						Element useCase = (Element) iterator.next();
						useCase.getChild("graphics").addContent(new Element("color"));
					}
					
					// use case associations and dependencies
					List associations = useCaseDiagram.getChild("associations").getChildren();
					for (Iterator iterator = associations.iterator(); iterator
							.hasNext();) {
						Element association = (Element) iterator.next();
						// create the labels strucutre
						Element main = new Element("label");				
						main.setAttribute("type", "main");
						main.setAttribute("identifier", "");
						
						Element rolename1 = new Element("label");
						rolename1.setAttribute("type", "rolename");
						rolename1.setAttribute("identifier", "1");
						
						Element rolename2 = new Element("label");
						rolename2.setAttribute("type", "rolename");
						rolename2.setAttribute("identifier", "2");
						
						Element multiplicity1 = new Element("label");
						multiplicity1.setAttribute("type", "multiplicity");
						multiplicity1.setAttribute("identifier", "1");
						
						Element multiplicity2 = new Element("label");
						multiplicity2.setAttribute("type", "multiplicity");
						multiplicity2.setAttribute("identifier", "2");
						
						// set the position values that are currently in the model			
						List associationEnds = association.getChild("associationEnds").getChildren("associationEnd");
						Element ae1 = (Element)associationEnds.get(0);
						Element ae2 = (Element)associationEnds.get(1);
						
						main.addContent(association.getChild("graphics").getChild("labels").getChild("mainLabel").getChild("position").detach());
						rolename1.addContent(ae1.getChild("rolename").getChild("graphics").getChild("label").getChild("position").detach());
						rolename2.addContent(ae2.getChild("rolename").getChild("graphics").getChild("label").getChild("position").detach());
						multiplicity1.addContent(ae1.getChild("multiplicity").getChild("graphics").getChild("label").getChild("position").detach());
						multiplicity2.addContent(ae2.getChild("multiplicity").getChild("graphics").getChild("label").getChild("position").detach());
						
						//remove the old main label					
						Element graphics = association.getChild("graphics");
						Element labels = graphics.getChild("labels");
						labels.removeChild("mainLabel");
						
						// add the label nodes
						labels.addContent(main);
						labels.addContent(rolename1);
						labels.addContent(rolename2);
						labels.addContent(multiplicity1);
						labels.addContent(multiplicity2);					
						
						// add color and lineStyle nodes
						graphics.addContent(new Element("color"));
						graphics.addContent(new Element("lineStyle"));
						
						// remove all other graphics nodes from the model (rolename, mult., chang., etc.)				
						ae1.getChild("rolename").removeChild("graphics");
						ae2.getChild("rolename").removeChild("graphics");
						ae1.getChild("multiplicity").removeChild("graphics");
						ae2.getChild("multiplicity").removeChild("graphics");					
					}
				}
				
				// class diagrams
				List classDiagrams = project.getChild("diagrams").getChild("classDiagrams").getChildren("classDiagram");
				for (Iterator iter = classDiagrams.iterator(); iter.hasNext();) {
					Element classDiagram = (Element) iter.next();
					List classes = classDiagram.getChild("classes").getChildren("class");
					for (Iterator iterator = classes.iterator(); iterator.hasNext();) {
						Element classRef = (Element) iterator.next();
						classRef.getChild("graphics").addContent(new Element("color"));
					}
				}
				
				// state machine diagrams
				Element stateDiagrams = project.getChild("diagrams").getChild("stateChartDiagrams");
				stateDiagrams.setName("stateMachineDiagrams");
				List stateDiagramsList = stateDiagrams.getChildren("stateChartDiagram");
				for (Iterator iter = stateDiagramsList.iterator(); iter.hasNext();) {
					Element stateDiagram = (Element) iter.next();
					stateDiagram.setName("stateMachineDiagram");
					
					// sets the diagram name (when possible)
					String name = stateDiagram.getChildText("name");
					if(name.indexOf("State Chart Diagram") == 0){
						name = "State Machine Diagram" + name.substring(19, name.length());
						stateDiagram.getChild("name").setText(name);
					}
					
					List states = stateDiagram.getChild("states").getChildren("state");
					for (Iterator iterator = states.iterator(); iterator.hasNext();) {
						Element state = (Element) iterator.next();
						state.getChild("graphics").addContent(new Element("color"));
					}
					
					List actions = stateDiagram.getChild("associations").getChildren("action");
					for (Iterator iterator = actions.iterator(); iterator.hasNext();) {
						Element action = (Element) iterator.next();
						Element graphics = action.getChild("graphics");
						
						// create new main label
						Element main = new Element("label");				
						main.setAttribute("type", "main");
						main.setAttribute("identifier", "");
						
						// set the position
						main.addContent(graphics.getChild("labels").getChild("mainLabel").getChild("position").detach());
						
						// remove the old label
						graphics.getChild("labels").removeChild("mainLabel");
						
						// add new label
						graphics.getChild("labels").addContent(main);
						
						// add color, lineStyle
						graphics.addContent(new Element("color"));
						graphics.addContent(new Element("lineStyle"));
					}
				}
				
				// domains
				List domains = project.getChild("diagrams").getChild("planningDomains").getChildren("domain");
				for (Iterator iter = domains.iterator(); iter.hasNext();) {
					Element domain = (Element) iter.next();
					List objects = domain.getChild("elements").getChild("objects").getChildren("object");
					for (Iterator iterator = objects.iterator(); iterator.hasNext();) {
						Element object = (Element) iterator.next();
						object.getChild("graphics").addContent(new Element("color"));
					}
					
					// repository diagrams
					List repositoryDiagrams = domain.getChild("repositoryDiagrams").getChildren("repositoryDiagram");
					for (Iterator iterator = repositoryDiagrams.iterator(); iterator
							.hasNext();) {
						Element repositoryDiagram = (Element) iterator.next();
						if(repositoryDiagram.getChildText("name").equals("The Objects")){
							repositoryDiagram.getChild("name").setText("Object Repository");
							
							List objectRefs = repositoryDiagram.getChild("objects").getChildren("object");
							for (Iterator iterator2 = objectRefs.iterator(); iterator2
									.hasNext();) {
								Element objectRef = (Element) iterator2.next();
								objectRef.getChild("graphics").addContent(new Element("color"));
							}
							
							List objectAssociations = repositoryDiagram.getChild("associations").getChildren("objectAssociation");
							for (Iterator iterator3 = objectAssociations.iterator(); iterator3
									.hasNext();) {
								Element objectAssociation = (Element) iterator3.next();
								Element graphics = objectAssociation.getChild("graphics");
								Element labels = graphics.getChild("labels");
								
								// create the labels strucutre
								Element main = new Element("label");				
								main.setAttribute("type", "main");
								main.setAttribute("identifier", "");
								
								Element rolename1 = new Element("label");
								rolename1.setAttribute("type", "rolename");
								rolename1.setAttribute("identifier", "1");
								Element position = new Element("position");
								position.setAttribute("x", "");
								position.setAttribute("y", "");
								rolename1.addContent(position);
								
								Element rolename2 = new Element("label");
								rolename2.setAttribute("type", "rolename");
								rolename2.setAttribute("identifier", "2");
								rolename2.addContent((Element)position.clone());
								
								//set the main label position (which is in the reference), and remove the old one
								main.addContent(labels.getChild("mainLabel").getChild("position").detach());
								labels.removeChild("mainLabel");
								
								// add the label nodes
								labels.addContent(main);
								labels.addContent(rolename1);
								labels.addContent(rolename2);
								
								graphics.addContent(new Element("color"));
								graphics.addContent(new Element("lineStyle"));
							}
						}
					}
					
					// problems
					List problems = domain.getChild("planningProblems").getChildren("problem");
					for (Iterator iterator = problems.iterator(); iterator
							.hasNext();) {
						Element problem = (Element) iterator.next();
						
						// object diagrams
						List objectDiagrams = problem.getChild("objectDiagrams").getChildren("objectDiagram");
						for (Iterator iterator1 = objectDiagrams.iterator(); iterator1
								.hasNext();) {
							Element objectDiagram = (Element) iterator1.next();
							
							List objectRefs = objectDiagram.getChild("objects").getChildren("object");
							for (Iterator iterator2 = objectRefs.iterator(); iterator2
									.hasNext();) {
								Element objectRef = (Element) iterator2.next();
								objectRef.getChild("graphics").addContent(new Element("color"));
								
								List attributes = objectRef.getChild("attributes").getChildren("attribute");
								for (Iterator iterator3 = attributes.iterator(); iterator3
										.hasNext();) {
									// removes the "repository" attribute
									Element attribute = (Element) iterator3.next();
									Element value = new Element("value");
									value.setText(attribute.getChildText("value"));
									attribute.removeChild("value");
									attribute.addContent(value);
								}
							}
							
							List objectAssociations = objectDiagram.getChild("associations").getChildren("objectAssociation");
							for (Iterator iterator3 = objectAssociations.iterator(); iterator3
									.hasNext();) {
								Element objectAssociation = (Element) iterator3.next();
								Element graphics = objectAssociation.getChild("graphics");
								Element labels = graphics.getChild("labels");
								
								// create the labels strucutre
								Element main = new Element("label");				
								main.setAttribute("type", "main");
								main.setAttribute("identifier", "");
								
								Element rolename1 = new Element("label");
								rolename1.setAttribute("type", "rolename");
								rolename1.setAttribute("identifier", "1");
								Element position = new Element("position");
								position.setAttribute("x", "");
								position.setAttribute("y", "");
								rolename1.addContent(position);
								
								Element rolename2 = new Element("label");
								rolename2.setAttribute("type", "rolename");
								rolename2.setAttribute("identifier", "2");
								rolename2.addContent((Element)position.clone());
								
								//set the main label position (which is in the reference), and remove the old one
								main.addContent(labels.getChild("mainLabel").getChild("position").detach());
								labels.removeChild("mainLabel");
								
								// add the label nodes
								labels.addContent(main);
								labels.addContent(rolename1);
								labels.addContent(rolename2);
								
								graphics.addContent(new Element("color"));
								graphics.addContent(new Element("lineStyle"));
							}
						}
					}
				}*/	
				
				
				/*Element graphics = new Element("graphics");
				  Element label = new Element("label");
				  Element position = new Element("position");
				  position.setAttribute("x", "");
				  position.setAttribute("y", "");
				  label.addContent(position);
				  graphics.addContent(label);				  
				
				// adding rolename and multiplicity "graphics" and "value" nodes 01/06/2007
				List result = null;
				try {
					XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation/associationEnds/associationEnd/rolename | " +
							"project/elements/classAssociations/classAssociation/associationEnds/associationEnd/multiplicity | " +
							"project/elements/classAssociations/classAssociation/changeability | " +
							"project/diagrams/useCaseDiagrams/useCaseDiagram/associations/useCaseAssociation/associationEnds/associationEnd/rolename | " +
							"project/diagrams/useCaseDiagrams/useCaseDiagram/associations/useCaseAssociation/associationEnds/associationEnd/multiplicity | " +
							"project/diagrams/useCaseDiagrams/useCaseDiagram/associations/dependency/associationEnds/associationEnd/rolename | " +
							"project/diagrams/useCaseDiagrams/useCaseDiagram/associations/dependency/associationEnds/associationEnd/multiplicity");
					result = path.selectNodes(doc);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				
				if (result.size() > 0){
					System.out.println(result.size() + " elementos encontrados");
					System.out.println("adicionando novos elementos");
					for (Iterator iter = result.iterator(); iter.hasNext();) {
						Element node = (Element) iter.next();
						String valueStr = node.getText().trim();
						node.setText("");
						Element value = new Element("value");
						value.setText(valueStr);
						node.addContent(value);
						node.addContent((Element)graphics.clone());
					}
					
				XMLUtilities.writeToFile(args[0], doc);
				System.out.println("salvando arquivo: " + args[0]);
					
				}*/
				
			} catch (RuntimeException e) {				
				e.printStackTrace();
				succes = false;
			}
			
			/*if (succes) {
				// saves old file
				String oldFileName = files[i].substring(0, args[0].lastIndexOf('.'))+ "~" + args[0].substring(args[0].lastIndexOf('.'), args[0].length());
				System.out.println("salvando arquivo: " + oldFileName);
				XMLUtilities.writeToFile(oldFileName, docCopy);	
				
				XMLUtilities.writeToFile(args[0], doc);
				System.out.println("salvando arquivo: " + args[0]);
			}*/
			
			// adding "labels" node in associations (2006)
			/*List result = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class/generalization/graphics | " +
						"project/elements/classAssociations/classAssociation/graphics | " +
						"project/diagrams/useCaseDiagrams/useCaseDiagram/associations/useCaseAssociation/graphics | " +
						"project/diagrams/useCaseDiagrams/useCaseDiagram/associations/dependency/graphics | " +
						"project/diagrams/classDiagrams/classDiagram/associations/classAssociation/graphics | " +
						"project/diagrams/stateChartDiagrams/stateChartDiagram/associations/action/graphics | " +
						"project/diagrams/planningProblems/problem/objectDiagrams/objectDiagram/associations/objectAssociation/graphics");
				result = path.selectNodes(doc);
			} catch (JaxenException e) {			
				e.printStackTrace();
			}
			
			if (result.size() > 0){
				System.out.println(result.size() + " elementos encontrados");
				System.out.println("adicionando novos elementos");
				for (Iterator iter = result.iterator(); iter.hasNext();) {
					Element graphics = (Element) iter.next();
					graphics.addContent(0, (Element)newElements.getChild("labels").clone());
				}
				
			}*/

			
		}
	}

}
