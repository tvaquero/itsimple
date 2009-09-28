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

package languages.uml.ocl;

import itSIMPLE.ItSIMPLE;
import languages.xml.XMLUtilities;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import java.util.StringTokenizer;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jdom.JDOMException;

public class OCLUtilities {
	
	public static Element buildConditions(Element operator){
		
		
		
		Element commonData = ItSIMPLE.getCommonData();
		//TODO synchronize with ItSIMPLE commonData
		try {
			if(commonData == null)commonData = XMLUtilities.readFromFile("resources/settings/commonData.xml").getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Element actionNode = null;
		
//		0. Get the main elements
		//ActionAssociation action = (ActionAssociation)cell;	
		
		//Element actionData = action.getData();
		//Element oclOperator  = action.getReference();
		
		//String oclAction = "";			
		
		if (operator != null){
								// 			operators			class				
			Element operatorClass = operator.getParentElement().getParentElement();
			
			
			//Auxiliar Node
			actionNode = (Element)commonData.getChild("internalUse").getChild("action").clone();

			Element preconditionNode = actionNode.getChild("preconditions");
			Element postconditionNode = actionNode.getChild("postconditions");			
			
			//1. Get Operator parameters
			String strParameters = "";
			Iterator parameters = operator.getChild("parameters").getChildren("parameter").iterator();
			while(parameters.hasNext()){
				Element parameter = (Element)parameters.next();
				
				Element tyClass = null;
				try {
					XPath path = new JDOMXPath("project/elements/classes/class[@id='"+parameter.getChildText("type")+"']");
					tyClass = (Element)path.selectSingleNode(operator.getDocument());
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
			//2. Get action name in OCL
			
			actionNode.getChild("class").setText(operatorClass.getChildText("name"));
			actionNode.getChild("name").setText(operator.getChildText("name")+ "(" + strParameters + ")");
			actionNode.getChild("parameters").setText(strParameters);
			
			//String actionContext = operatorClass.getChildText("name")+ "::" + operator.getChildText("name")+ "(" + strParameters + ")";


			//3. Get the pre and post condition in the stateMachine diagrams							
			
			//3.1. Find all state machine diagrams with this operator		
			List stateMachineDiagrams = null;				
			try {
				XPath path = new JDOMXPath("project/diagrams/stateMachineDiagrams/stateMachineDiagram[associations/action/reference/@class='" +
						operatorClass.getAttributeValue("id") + "' and associations/action/reference/@operator='" + operator.getAttributeValue("id")+
						"']");
				stateMachineDiagrams = path.selectNodes(operator.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			//3.2. For each diagram get the pre and post conditions 
			for (int i = 0; i < stateMachineDiagrams.size(); i++){
				Element stateMachine = (Element)stateMachineDiagrams.get(i);
				
				//new condition section in the action template
				Element diagramPreConditions = (Element)commonData.getChild("internalUse").getChild("conditions").clone();
				Element diagramPostConditions = (Element)commonData.getChild("internalUse").getChild("conditions").clone();
				
				preconditionNode.addContent(diagramPreConditions);
				postconditionNode.addContent(diagramPostConditions);
				
				//get the class of the diagram
				if (!stateMachine.getChildText("class").trim().equals("")){
					Element tyClass = null;
					try {
						XPath path = new JDOMXPath("project/elements/classes/class[@id='"+stateMachine.getChildText("class")+"']");
						tyClass = (Element)path.selectSingleNode(operator.getDocument());
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					if (tyClass != null){
						diagramPreConditions.setAttribute("class",tyClass.getChildText("name"));
						diagramPostConditions.setAttribute("class",tyClass.getChildText("name"));						
					}				
				}
				
					
				//3.2.1. Get all preconditions and postconditions of this operator and also the States
				List result = null;				
				try {
					XPath path = new JDOMXPath("associations/action[reference/@class='" +
							operatorClass.getAttributeValue("id") + "' and reference/@operator='" + 
							operator.getAttributeValue("id") +	"']");
					result = path.selectNodes(stateMachine);
				} catch (JaxenException e2) {			
					e2.printStackTrace();
				}
				//3.2.2 Deal with each transition of the selected action in the following cases:
				//	case 1: one transition only - simple
				//  case 2: many transitions - work with general structure (operator OR in preconditions and IF THEN ENDIF in postconditions)
	
				Element conditionGroup = new Element("conditionGroup");
				Element preconditionGroup = new Element("preconditionGroup"); 
				Element postconditionGroup = new Element("postconditionGroup");
				conditionGroup.addContent(preconditionGroup);
				conditionGroup.addContent(postconditionGroup);
				
				for (int j = 0; j < result.size(); j++){
					
					Element eachAction = (Element)result.get(j);
					
					//transition group
					Element groupPre = new Element("group");
					groupPre.setAttribute("transition", eachAction.getAttributeValue("id"));
					Element groupPost = new Element("group");
					groupPost.setAttribute("transition", eachAction.getAttributeValue("id"));
										
					Element eachActionPreCondition = new Element("condition");
					Element eachActionPostCondition = new Element("condition");
									
					eachActionPreCondition.setText(eachAction.getChild("precondition").getText().replaceAll("\n", " "));
					eachActionPostCondition.setText(eachAction.getChild("postcondition").getText().replaceAll("\n", " "));
					
					Element currentActionEndFrom = null;
					Element currentActionEndTo = null;					
					
					//1. Get the actionEnd for the State FROM
					try {
						XPath path = new JDOMXPath("associationEnds/actionEnd[@navigation='false']");
						currentActionEndFrom = (Element)path.selectSingleNode(eachAction);
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					//2. Get the actionEnd for the State TO
					try {
						XPath path = new JDOMXPath("associationEnds/actionEnd[@navigation='true']");
						currentActionEndTo = (Element)path.selectSingleNode(eachAction);
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}					
					
					Element currentStateFrom = null;
					Element currentStateTo = null;	
					//3. Get the State FROM for the current action
					try {
						XPath path = new JDOMXPath("states/state[@id='"+currentActionEndFrom.getAttributeValue("element-id")+"']");
						currentStateFrom = (Element)path.selectSingleNode(stateMachine);
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}
					//4. Get the State TO for the current action
					try {
						XPath path = new JDOMXPath("states/state[@id='"+currentActionEndTo.getAttributeValue("element-id")+"']");
						currentStateTo = (Element)path.selectSingleNode(stateMachine);
					} catch (JaxenException e2) {			
						e2.printStackTrace();
					}					
					
					//5. Add State FROM and State TO to the auxiliar node
					Element eachStatePreCondition = new Element("condition");
					
					Element eachStatePostCondition = new Element("condition");
					
					if (currentStateFrom != null)eachStatePreCondition.setText(currentStateFrom.getChildText("condition").replaceAll("\n", " "));	
					if (currentStateTo != null)eachStatePostCondition.setText(currentStateTo.getChildText("condition").replaceAll("\n", " "));		
					
					
					/*//Add preconditions (Action + State)
					if(!eachActionPreCondition.getText().trim().equals(""))
						diagramPreConditions.addContent(eachActionPreCondition);
					if(!eachStatePreCondition.getText().trim().equals(""))
						diagramPreConditions.addContent(eachStatePreCondition);
					
					//Add postconditions (Action + State)
					if(!eachActionPostCondition.getText().trim().equals(""))
						diagramPostConditions.addContent(eachActionPostCondition);
					if(!eachStatePostCondition.getText().trim().equals(""))
						diagramPostConditions.addContent(eachStatePostCondition);
					*/
					
					// Set precondition group (Action + State)
					String preconditionStr = "";
					if(!eachStatePreCondition.getText().trim().equals(""))
						preconditionStr = eachStatePreCondition.getText().trim();
					if(!eachActionPreCondition.getText().trim().equals("")){
						if (!preconditionStr.trim().equals("")){
							preconditionStr = preconditionStr + " and " +eachActionPreCondition.getText().trim(); 
						}else{
							preconditionStr = eachActionPreCondition.getText().trim();
						}
					}
					// Set postcondition group(Action + State)
					String postconditionStr = "";
					if(!eachStatePostCondition.getText().trim().equals(""))
						postconditionStr = eachStatePostCondition.getText().trim();
					if(!eachActionPostCondition.getText().trim().equals("")){
						if (!postconditionStr.trim().equals("")){
							postconditionStr = postconditionStr + " and " +eachActionPostCondition.getText().trim(); 
						}else{
							postconditionStr = eachActionPostCondition.getText().trim();
						}
					}
					
					groupPre.setText(preconditionStr);
					//System.out.println("Precondition: " + preconditionStr);
					
					groupPost.setText(postconditionStr);
					//System.out.println("Postcondition: " + preconditionStr);
					
					if (!groupPre.getText().trim().equals("")) preconditionGroup.addContent(groupPre);
					if (!groupPost.getText().trim().equals("")) postconditionGroup.addContent(groupPost);
					
			
				}// End of transition iteration			
				
				//XMLUtilities.printXML(conditionGroup);
				
				//3.3 Having all transitions in the diagram colected it is necessary to 
				// insert pre and post conditions in the action 
				// (dealing with single and multiple transitions in a diagram)
				
				//3.3.1 Dealing with preconditions
				if (preconditionGroup.getChildren().size() > 0){
					Element condition = new Element("condition");
					// Case of one transition representing the action in the diagram
					if (preconditionGroup.getChildren().size()== 1){
						condition.setText(preconditionGroup.getChildText("group"));
						diagramPreConditions.addContent(condition);
					}else{
						//Case of many transition representing the action in the diagram (use the operator OR)
						String preconditionString = "";
						for (int j = 0; j < preconditionGroup.getChildren().size(); j++){
							Element each = (Element)preconditionGroup.getChildren().get(j);
							preconditionString = preconditionString + "(" + each.getTextTrim() + ")";
							if (j < preconditionGroup.getChildren().size() - 1)
								preconditionString = preconditionString + " or ";
						}
						condition.setText(preconditionString);
						diagramPreConditions.addContent(condition);
					}
				}
				
				//3.3.2 Dealing with postconditions
				if (postconditionGroup.getChildren().size() > 0){
					Element condition = new Element("condition");
					//Case of one transition representing the action in the diagram
					if (postconditionGroup.getChildren().size()== 1){
						condition.setText(postconditionGroup.getChildText("group"));
						diagramPostConditions.addContent(condition);
					}else{
						// Case of many transition representing the action in the diagram (use the the IF THEN ENDIF structure)
						String postconditionString = "";
						for (int j = 0; j < postconditionGroup.getChildren().size(); j++){
							Element each = (Element)postconditionGroup.getChildren().get(j);
							//Get <precondition> of  IF <precondition> THEN ... ENDIF
							Element preconditionOfIF = null;	
							//3. Get the State FROM for the current action
							try {
								XPath path = new JDOMXPath("group[@transition='"+each.getAttributeValue("transition")+"']");
								preconditionOfIF = (Element)path.selectSingleNode(preconditionGroup);
							} catch (JaxenException e2) {			
								e2.printStackTrace();
							}
							if (preconditionOfIF != null){
								postconditionString = postconditionString + "if (" + preconditionOfIF.getTextTrim() + ") then " + each.getTextTrim() + " endif";
							}else{
								postconditionString = postconditionString + each.getTextTrim();
							}
							if (j < preconditionGroup.getChildren().size() - 1)
								postconditionString = postconditionString + " and ";
						}
						condition.setText(postconditionString);
						diagramPostConditions.addContent(condition);
					}
				}				
				
				
			}// End of diagram iteration

            //4. get pre and post condition from the ocl constraints (pre: and post:) defined in the operator
            //Check if there is something at the constraints
            if (!operator.getChildText("constraints").trim().equals("")){
                String opExpression = operator.getChildText("constraints").trim();
                StringTokenizer tokenizer = new StringTokenizer(opExpression);
                String preconditionString = "";
                String postconditionString = "";
                boolean isPrecondition = false;
                boolean isPostcondition = false;
                while (tokenizer.hasMoreTokens()) {
                    String token = tokenizer.nextToken();
                    boolean consider = true;
                    ;
                    if (token.equals("pre:")){//this is either the end or the beggining
                        isPrecondition = true;
                        isPostcondition = false;
                        consider = false;
                    }
                    else if (token.equals("post:")){
                        isPostcondition = true;
                        isPrecondition = false;
                        consider = false;
                    }
                    if (consider){
                        if (isPrecondition){
                            preconditionString += token + " ";
                        }
                        else if (isPostcondition){
                            postconditionString += token + " ";
                        }
                    }
                }
                if (!preconditionString.trim().equals("")){
                    //System.out.println("Precondition: " + preconditionString);

                    //find the constraint of the class
                    Element currentClassConstraints = null;
                    Element preConditions = null;
                    try {
						XPath path = new JDOMXPath("conditions[@class='"+operatorClass.getChildText("name")+"']");
						currentClassConstraints = (Element)path.selectSingleNode(preconditionNode);
					} catch (JaxenException e2) {
						e2.printStackTrace();
					}
                    if (currentClassConstraints!=null){
                        preConditions = currentClassConstraints;
                    }
                    else{
                        preConditions = (Element)commonData.getChild("internalUse").getChild("conditions").clone();
                        preConditions.setAttribute("class",operatorClass.getChildText("name"));
                    }
                
                    //insert in the preconditions
                    Element condition = new Element("condition");
                    condition.setText(preconditionString);
                    preConditions.addContent(condition);
                    preconditionNode.addContent(preConditions);

                }
                if (!postconditionString.trim().equals("")){
                    //System.out.println("Postcondition: " + postconditionString);
                    //find the constraint of the class
                    Element currentClassConstraints = null;
                    Element postConditions = null;
                    try {
						XPath path = new JDOMXPath("conditions[@class='"+operatorClass.getChildText("name")+"']");
						currentClassConstraints = (Element)path.selectSingleNode(postconditionNode);
					} catch (JaxenException e2) {
						e2.printStackTrace();
					}
                    if (currentClassConstraints!=null){
                        postConditions = currentClassConstraints;
                    }
                    else{
                        postConditions = (Element)commonData.getChild("internalUse").getChild("conditions").clone();
                        postConditions.setAttribute("class",operatorClass.getChildText("name"));
                    }

                    Element condition = new Element("condition");
                    condition.setText(postconditionString);
                    postConditions.addContent(condition);
                    postconditionNode.addContent(postConditions);
                }
	
            }



			
		}
		
		return actionNode;
	}

}
