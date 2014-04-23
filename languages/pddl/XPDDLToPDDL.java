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

package languages.pddl;

import java.util.Iterator;
import java.util.List;

import languages.xml.XMLUtilities;
import org.jdom.Element;

public class XPDDLToPDDL {


    /**
     * Parse XPDDL elements into PDDL elements
     * @param xpddlNode
     * @param identation
     * @return
     */
    public static String parseXPDDLToPDDL(Element xpddlNode, String identation){
		String pddl = "";
			
		//1. Root node
		if(xpddlNode.getName().equals("xpddlDomain")){
			pddl = "(define (domain " + xpddlNode.getChildText("name") + ")\n";
			
			for (Iterator<?> iter = xpddlNode.getChildren().iterator(); iter.hasNext();) {
				Element xpddlChild = (Element) iter.next();
				
				pddl += parseXPDDLToPDDL(xpddlChild, identation + "  ");
			}
			
			pddl += ")\n";
		}
		
		else if(xpddlNode.getName().equals("xpddlProblem")){
			pddl = "(define (problem " + xpddlNode.getChildText("name") + ")\n";
			
			for (Iterator<?> iter = xpddlNode.getChildren().iterator(); iter.hasNext();) {
				Element xpddlChild = (Element) iter.next();
				
				pddl += parseXPDDLToPDDL(xpddlChild, identation + "  ");
			}
			
			pddl += ")\n";
		}

		
		//2. Requirements node
		else if(xpddlNode.getName().equals("requirements")){
			pddl = identation + "(:requirements";
			for (Iterator<?> iter = xpddlNode.getChildren().iterator(); iter.hasNext();) {
				Element requirement = (Element) iter.next();
				pddl += " :" + requirement.getName();
			}
			
			pddl += ")\n";
		}
		
		//3. Types node
		else if(xpddlNode.getName().equals("types")){
			if(xpddlNode.getChildren("type").size() > 0){
				pddl = identation + "(:types\n";
				for (Iterator<?> iter = xpddlNode.getChildren("type").iterator(); iter.hasNext();) {
					Element type = (Element) iter.next();
					pddl += identation + "  " + type.getAttributeValue("name") + " - " + type.getAttributeValue("parent") + "\n";
				}
				pddl += identation + ")\n";
			}

		}
		
		//4. Constants node
		else if(xpddlNode.getName().equals("constants")){
			
			if(xpddlNode.getChildren("constant").size() > 0){
				pddl = identation + "(:constants\n";
				for (Iterator<?> iter = xpddlNode.getChildren("constant").iterator(); iter.hasNext();) {
					Element constant = (Element) iter.next();
					pddl += parseXPDDLToPDDL(constant, identation + "   ") + "\n";				
				}
				pddl += identation + ")\n";
			}

		}
		
		//5. Predicates node
		else if(xpddlNode.getName().equals("predicates")){
			
			if(xpddlNode.getChildren("predicate").size() > 0){
				pddl = identation + "(:predicates\n";
				
				for (Iterator<?> iter = xpddlNode.getChildren("predicate").iterator(); iter.hasNext();) {				
					Element predicate = (Element) iter.next();
					pddl += parseXPDDLToPDDL(predicate, identation + "  ") + "\n";
				}
				pddl += identation + ")\n";
			}
			
		}
		
		// 6. Functions node
		else if(xpddlNode.getName().equals("functions")){
			
			if(xpddlNode.getChildren("function").size() > 0){
				pddl = identation + "(:functions\n";
				
				for (Iterator<?> iter = xpddlNode.getChildren("function").iterator(); iter.hasNext();) {
					Element function = (Element) iter.next();
					pddl += parseXPDDLToPDDL(function, identation + "  ") + "\n";
				}
				pddl += identation + ")\n";
			}
			
		}
		
		//7. constraints
		else if(xpddlNode.getName().equals("constraints")){
			
			if(xpddlNode.getChildren().size() > 0){
				pddl = identation + "(:constraints\n";			
				
				Element constraint = (Element) xpddlNode.getChildren().get(0);
				pddl += parseXPDDLToPDDL(constraint, identation + "  ") + "\n";
			
			pddl += identation + ")\n";
			}			

		}
		
		
		//8. Actions node
		else if(xpddlNode.getName().equals("actions")){
			for (Iterator<?> iter = xpddlNode.getChildren().iterator(); iter.hasNext();) {
				Element action = (Element) iter.next();
				pddl += parseXPDDLToPDDL(action, identation);
			}
		}
		
		// 8.1 Action node
		else if(xpddlNode.getName().equals("action")){
			// open action
			pddl = identation + "(:action " + xpddlNode.getAttributeValue("name") + "\n";
			
			// 8.1.2 Parameters
			String parameters = identation + " :parameters (";
			for (Iterator<?> iter = xpddlNode.getChild("parameters").getChildren("parameter").iterator(); iter.hasNext();) {
				Element parameter = (Element) iter.next();
				parameters += parseXPDDLToPDDL(parameter, null);
				if(iter.hasNext())
					parameters += " ";
			}
			parameters += ")";
			pddl += parameters + "\n";
			
			
			// 8.1.3 Precondition node	
			String precondition = identation + " :precondition \n";
                        String preconditionContent = "";
                        if(xpddlNode.getChild("precondition").getChildren().size() > 0){

                                Element preconditionChild = (Element)xpddlNode.getChild("precondition").getChildren().get(0);
                                preconditionContent = parseXPDDLToPDDL(preconditionChild, identation + "   ");
                                //precondition += parseXPDDLToPDDL(preconditionChild, identation + "   ");
                        }
                        //Ckeck if the precondition is empty
                        if (!preconditionContent.trim().equals("")){                            
                            pddl += precondition + preconditionContent + "\n" ;
                        }
                        //pddl += precondition + "\n" ;

			//8.1.4 Effect node
			String effect = identation + " :effect\n";
			if(xpddlNode.getChild("effect").getChildren().size() > 0){
				Element effectChild = (Element)xpddlNode.getChild("effect").getChildren().get(0);
				effect += parseXPDDLToPDDL(effectChild, identation + "   ");
			}			
			pddl += effect + "\n";
						
			//close action
			pddl += identation + ")\n\n";

		}
		// 8.2 durative action
		else if(xpddlNode.getName().equals("durative-action")){	
			// 8.2.1 open action
			pddl = identation + "(:durative-action " + xpddlNode.getAttributeValue("name") + "\n";
			
			// 8.2.2 Parameters
			String parameters = identation + " :parameters (";
			for (Iterator<?> iter = xpddlNode.getChild("parameters").getChildren("parameter").iterator(); iter.hasNext();) {
				Element parameter = (Element) iter.next();
				parameters += parseXPDDLToPDDL(parameter, null);
				if(iter.hasNext())
					parameters += " ";
			}
			parameters += ")";
			pddl += parameters + "\n";
			
			// 8.2.3 duration
			String durationStr = identation + " :duration (= ?duration ";
			Element durationValue = (Element)xpddlNode.getChild("duration").getChildren().get(0);
			durationStr += parseXPDDLToPDDL(durationValue, "") + ")";
			
			pddl += durationStr + "\n";
			
			// 8.2.4 Condition node	
			String condition = identation + " :condition \n";
                        String conditionContent = "";
			if(xpddlNode.getChild("condition").getChildren().size() > 0){
				Element conditionChild = (Element)xpddlNode.getChild("condition").getChildren().get(0);
                                conditionContent = parseXPDDLToPDDL(conditionChild, identation + "   ");
				//condition += parseXPDDLToPDDL(conditionChild, identation + "   ");
			}
                        //Check if the precondition is empty
			if (!conditionContent.trim().equals("")){
                            pddl += condition + conditionContent + "\n" ;
                        }
			//pddl += condition + "\n" ;


			//8.1.4 Effect node
			String effect = identation + " :effect\n";
			if(xpddlNode.getChild("effect").getChildren().size() > 0){
				Element effectChild = (Element)xpddlNode.getChild("effect").getChildren().get(0);
				effect += parseXPDDLToPDDL(effectChild, identation + "   ");
			}			
			pddl += effect + "\n";
						
			//close action
			pddl += identation + ")\n\n";
		}
		//9. metric
		else if(xpddlNode.getName().equals("metric")){
			pddl = identation + "(:metric ";
			
			// optimization
			pddl += xpddlNode.getChildText("optimization") + " ";
			
			// expression
			pddl += parseXPDDLToPDDL(
					(Element)xpddlNode.getChild("expression").getChildren().get(0), "");
			
			pddl += ")\n";
		}
		
		// 10. Inner nodes
		
		// 10.1 Parameter node
		else if(xpddlNode.getName().equals("parameter")){
			
			/*if(xpddlNode.getAttribute("object") != null){
				pddl = xpddlNode.getAttributeValue("object");
			}
			
			else */if(xpddlNode.getAttribute("id") == null){
				// complete parameter
				pddl = "?" + xpddlNode.getAttributeValue("name") + " - " + xpddlNode.getAttributeValue("type");
			}
			else{
				//reference parameter
				pddl = "?" + xpddlNode.getAttributeValue("id");
			}
		}
		
		//10.2 Constant node
		else if(xpddlNode.getName().equals("constant")){
			if(xpddlNode.getAttribute("id") == null){
				// complete constant
				pddl = identation + xpddlNode.getAttributeValue("name") + " - " + xpddlNode.getAttributeValue("type");
			}
			else{
				// reference constant
				pddl = xpddlNode.getAttributeValue("id");
			}
		}
		
		//10.3 Predicate node and function node
                //else if(xpddlNode.getName().equals("predicate") || xpddlNode.getName().equals("function")){
		else if(xpddlNode.getName().equals("predicate")){
			if(xpddlNode.getAttribute("id") == null){
				// complete predicate
				//open predicate/function
				String strPredicate = "(" + xpddlNode.getAttributeValue("name");
				for (Iterator<?> iterator = xpddlNode.getChildren().iterator(); iterator
						.hasNext();) {
					// add each parameter
					Element parameter = (Element) iterator.next();					
					strPredicate += " " + parseXPDDLToPDDL(parameter, "");
				}

				// close predicate/function
				strPredicate += ")";
				
				// add the predicate/function to the output string
				pddl += identation + strPredicate;
			}
			else{
				// reference predicate/function
				pddl = identation + "(" + xpddlNode.getAttributeValue("id");
				for (Iterator<?> iter = xpddlNode.getChildren().iterator(); iter.hasNext();) {
					Element refParameter = (Element) iter.next();					
					pddl += " " + parseXPDDLToPDDL(refParameter, "");
				}
				pddl += ")";				
			}
		}

                //10.3.1 function node
		else if(xpddlNode.getName().equals("function")){
			if(xpddlNode.getAttribute("id") == null){
				// complete function
				//open function
				String strFunction = "(" + xpddlNode.getAttributeValue("name");
				for (Iterator<?> iterator = xpddlNode.getChildren().iterator(); iterator
						.hasNext();) {
					// add each parameter
					Element parameter = (Element) iterator.next();
					strFunction += " " + parseXPDDLToPDDL(parameter, "");
				}
				// close function
				strFunction += ")";

                                //check the function (PDDL 3.1)
                                String ftype = xpddlNode.getAttributeValue("type");
                                if (ftype != null){
                                    strFunction += " - "+ ftype;
                                }

				// add the function to the output string
				pddl += identation + strFunction;
			}
			else{
				// reference predicate/function
				pddl = identation + "(" + xpddlNode.getAttributeValue("id");
				for (Iterator<?> iter = xpddlNode.getChildren().iterator(); iter.hasNext();) {
					Element refParameter = (Element) iter.next();
					pddl += " " + parseXPDDLToPDDL(refParameter, "");
				}
				pddl += ")";
			}
		}
		
		// 10.4 And & Or nodes
		else if(xpddlNode.getName().equals("and") || xpddlNode.getName().equals("or")){
			pddl = identation + "(";
			pddl += xpddlNode.getName();
			boolean externalNode = true;
			if(//xpddlNode.getParentElement().getName().equals("imply") ||
					//xpddlNode.getParentElement().getName().equals("implies") ||
					xpddlNode.getParentElement().getName().equals("suchthat") ||
					xpddlNode.getParentElement().getName().equals("sometime")){
				externalNode = false;
			}
			pddl += (externalNode) ?"\n" :"";
			for (Iterator<?> iter = xpddlNode.getChildren().iterator(); iter.hasNext();) {
				Element node = (Element) iter.next();
				if(externalNode){
					pddl += parseXPDDLToPDDL(node, identation + "  ") +  "\n";					
				}
				else{
					pddl += parseXPDDLToPDDL(node, " ");
					}				
			}
			pddl += (externalNode) ?(identation + ")") :")";
		}
		
		//10.5 Not node
		else if(xpddlNode.getName().equals("not")){
			Element child = (Element)xpddlNode.getChildren().get(0);
			if(xpddlNode.getParentElement().getName().equals("constraints") ||
					child.getName().equals("forall")){
				pddl = identation + "(not\n" + parseXPDDLToPDDL(child, identation + "  ") + "\n" + identation + ")";
			}
			else{
				pddl = identation + "(not " + parseXPDDLToPDDL(child, "") + ")";
			}
		}
		
		// 10.5 Value node
		else if(xpddlNode.getName().equals("value")){
                        //pddl = xpddlNode.getAttributeValue("number");

                        //Considering PDDL3.1 <value object=""> where object are also considered in function
                        String numberValue = xpddlNode.getAttributeValue("number");
                        String objectValue = xpddlNode.getAttributeValue("object");
                        
                        if (numberValue != null){
                            pddl = numberValue;
                        }else{
                            pddl = objectValue;
                        }
		}
		
		// 10.6 equals node
		else if(xpddlNode.getName().equals("equals")){
			pddl = identation + "(= ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";			
		}
		
		// 10.7 gt node
		else if(xpddlNode.getName().equals("gt")){
			pddl = identation + "(> ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";			
		}
		
		// 10.7 gt node
		else if(xpddlNode.getName().equals("lt")){
			pddl = identation + "(< ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";			
		}
		
		// 10.8 ge node
		else if(xpddlNode.getName().equals("ge")){
			pddl = identation + "(>= ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";			
		}
		
		// 10.9 le node
		else if(xpddlNode.getName().equals("le")){
			pddl = identation + "(<= ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";			
		}
		
		//10.10 add
		else if(xpddlNode.getName().equals("add")){
			pddl = identation + "(+ ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";
		}
		
		//10.11 subtract
		else if(xpddlNode.getName().equals("subtract")){
			pddl = identation + "(- ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";
		}
		
		//10.12 multiply
		else if(xpddlNode.getName().equals("multiply")){
			pddl = identation + "(* ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";
		}
		
		//10.13 divide
		else if(xpddlNode.getName().equals("divide")){
			pddl = identation + "(/ ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";
		}
		
		// 10.14 assign
		else if(xpddlNode.getName().equals("assign")){
			pddl = identation + "(assign ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";
		}
		
		// 10.15 exists
		else if(xpddlNode.getName().equals("exists")){
			pddl = identation + "(exists (";
			// parameters
			for (Iterator<?> iter = xpddlNode.getChildren("parameter").iterator(); iter.hasNext();) {
				Element parameter = (Element) iter.next();
				pddl += parseXPDDLToPDDL(parameter, "");
				if(iter.hasNext()){					
					pddl += " ";
				}
				else{
					// close parameters parenthesis
					pddl += ")";
				}

			}
			
			// last node
			Element node = (Element)xpddlNode.getChild("suchthat").getChildren().get(0);
			//pddl += "(" + parseXPDDLToPDDL(node, " ") + ")";
			pddl += parseXPDDLToPDDL(node, " ");
			
			pddl += ")";
		}
		
		// 10.16 forall
		else if(xpddlNode.getName().equals("forall")){
			pddl = identation + "(forall (";
			// parameters
			for (Iterator<?> iter = xpddlNode.getChildren("parameter").iterator(); iter.hasNext();) {
				Element parameter = (Element) iter.next();
				pddl += parseXPDDLToPDDL(parameter, "");
				if(iter.hasNext()){					
					pddl += " ";
				}
				else{
					// close parameters parenthesis
					pddl += ")\n";
				}

			}
			
			// last node
			Element node = (Element)xpddlNode.getChildren().get(xpddlNode.getChildren().size()-1);
			pddl += parseXPDDLToPDDL(node, identation + "  ") + "\n";
			
			pddl += identation + ")";
		}
		
		//10.17 always
		else if(xpddlNode.getName().equals("always")){
			pddl = identation + "(always\n";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), identation + "  ") + "\n";
			pddl += identation + ")";
		}
		
		// 10.18 imply
		else if(xpddlNode.getName().equals("imply")){
			pddl = identation + "(imply\n";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), identation + "  ") + "\n";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), identation + "  ") + "\n";
			pddl += identation + ")";
		}		
		
		
		// 10.19 implies
		else if(xpddlNode.getName().equals("implies")){
			pddl = parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), identation);
		}
		
		// 10..20 when
		else if(xpddlNode.getName().equals("when")){
			pddl = identation + "(when\n";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), identation + "  ") + "\n";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), identation + "  ") + "\n";
			pddl += identation + ")";
		}		
		
		// 10.21 do
		else if(xpddlNode.getName().equals("do")){
			pddl = parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), identation);
		}
		
		// 10.22 increase
		else if(xpddlNode.getName().equals("increase")){
			pddl = identation + "(increase ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";
		}
		
		// 10.23 decrease
		else if(xpddlNode.getName().equals("decrease")){
			pddl = identation + "(decrease ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";
		}
		
		// 10.24 scale-up
		else if(xpddlNode.getName().equals("scale-up")){
			pddl = identation + "(scale-up ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";
		}
		
		// 10.25 scale-down
		else if(xpddlNode.getName().equals("scale-down")){
			pddl = identation + "(scale-down ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "") + " ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(1), "");
			pddl += ")";
		}
		
		//10.26 at-start
		else if(xpddlNode.getName().equals("at-start")){
            Element chdNode = (Element)xpddlNode.getChildren().get(0);
            if (chdNode.getName().equals("and") || chdNode.getName().equals("or")){
    			pddl = identation + "(at start \n";
    			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), identation + "  ") + "\n";
    			pddl += identation + ")\n";
            }
            else{
                pddl = identation + "(at start ";
                pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), " ");
                pddl += ")";
            }

		}
		
		// 10.27 at-end
		else if(xpddlNode.getName().equals("at-end")){
            Element chdNode = (Element)xpddlNode.getChildren().get(0);
            if (chdNode.getName().equals("and") || chdNode.getName().equals("or")){
    			pddl = identation + "(at end \n";
    			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), identation + "  ") + "\n";
    			pddl += identation + ")\n";
            }
            else{
                pddl = identation + "(at end ";
                pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), " ");
                pddl += ")";
            }

		}
		// 10.27.1 over-all
		else if(xpddlNode.getName().equals("over-all")){
            Element chdNode = (Element)xpddlNode.getChildren().get(0);
            if (chdNode.getName().equals("and") || chdNode.getName().equals("or")){
    			pddl = identation + "(over all \n";
    			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), identation + "  ") + "\n";
    			pddl += identation + ")\n";
            }
            else{
                pddl = identation + "(over all ";
                pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), " ") ;
                pddl += ")";
            }
		}

                // 10.28 timed initial literal
		else if(xpddlNode.getName().equals("at")){
                        Element literal = xpddlNode.getChild("literal");
                        Element timespecifier = xpddlNode.getChild("timespecifier");

			pddl = identation + "(at " + timespecifier.getAttributeValue("number");

			pddl += parseXPDDLToPDDL((Element)literal.getChildren().get(0), " ");
			pddl += identation + ")";
		}
		
		
		
		// 11. domain node
		else if(xpddlNode.getName().equals("domain")){
			pddl = identation + "(:domain " + xpddlNode.getText() + ")\n";
		}
		
		// 12. objects
		else if(xpddlNode.getName().equals("objects")){
			pddl = identation + "(:objects\n";
			List<?> objects = xpddlNode.getChildren("object");
			for (Iterator<?> iter = objects.iterator(); iter.hasNext();) {
				Element object = (Element) iter.next();
				pddl += identation + "  " + parseXPDDLToPDDL(object, "") + "\n";
			}
			pddl += identation + ")\n";
		}
		
		// 13. object
		else if(xpddlNode.getName().equals("object")){
			
			if(xpddlNode.getAttributeValue("id") != null){
				pddl = xpddlNode.getAttributeValue("id");
			}
			else{
				pddl = xpddlNode.getAttributeValue("name") + " - " + xpddlNode.getAttributeValue("type");
			}
		}
		
		// 14. init
		else if(xpddlNode.getName().equals("init")){
			pddl = identation + "(:init\n";
			List<?> objects = xpddlNode.getChildren();
			for (Iterator<?> iter = objects.iterator(); iter.hasNext();) {
				Element object = (Element) iter.next();
				pddl += identation + "  " + parseXPDDLToPDDL(object, "") + "\n";
			}
			pddl += identation + ")\n";
		}
		
		// 15. goal
		else if(xpddlNode.getName().equals("goal")){
			pddl = identation + "(:goal\n";
			List<?> objects = xpddlNode.getChildren();
			for (Iterator<?> iter = objects.iterator(); iter.hasNext();) {
				Element object = (Element) iter.next();
				pddl += parseXPDDLToPDDL(object, identation + "  ") + "\n";
			}
			pddl += identation + ")\n";
		}
		
		// 16. sometime
		else if(xpddlNode.getName().equals("sometime")){
			pddl = identation + "(sometime ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "");
			pddl += ")";
		}
		
		// 17. at-most-once
		else if(xpddlNode.getName().equals("at-most-once")){
			pddl = identation + "(at-most-once ";
			pddl += parseXPDDLToPDDL((Element)xpddlNode.getChildren().get(0), "");
			pddl += ")";
		}

		
		else{
			if(!xpddlNode.getName().equals("name")){
				//System.out.println("##############################\nNot dealed node: " + xpddlNode.getName());
			}
		}			
		
		return pddl;
	}
	
	

    /**
     * Parse a xml plan (itSIMPLE format) to a PDDL plan format
     * @param xmlPlan
     * @return
     */
    public static String parseXMLPlanToPDDL(Element xmlPlan){
        String pddlplan = "";


        //Planner name
        Element planner = xmlPlan.getChild("planner");
        pddlplan = "; Planner "+ planner.getChildText("name") + " " + planner.getChildText("version") + " \n";

        //statistics
        Element statistics = xmlPlan.getChild("statistics");
        pddlplan += "; itSIMPLETime "+ statistics.getChildText("toolTime") + "\n";
        pddlplan += "; Time "+ statistics.getChildText("time") + "\n";
        pddlplan += "; Parsing time "+ statistics.getChildText("parsingTime") + "\n";
        pddlplan += "; NrActions "+ statistics.getChildText("parsingTime") + "\n";
        pddlplan += "; MakeSpan "+ statistics.getChildText("parsingTime") + "\n";
        pddlplan += "; MetricValue "+ statistics.getChildText("metricValue") + "\n";
        pddlplan += "; PlanningTechnique: "+ statistics.getChildText("planningTechnique").trim().replaceAll("\n", " ") + "\n";
        pddlplan += "\n";

        //plan
        List<?> actions = xmlPlan.getChild("plan").getChildren("action");
        if (actions.size() > 0) {
                for (Iterator<?> iter = actions.iterator(); iter.hasNext();) {
                        Element action = (Element) iter.next();
                        // build up the action string
                        // start time
                        String actionStr = action.getChildText("startTime") + ": ";

                        // action name
                        actionStr += "(" + action.getAttributeValue("id") + " ";

                        // action parameters
                        List<?> parameters = action.getChild("parameters")
                                        .getChildren("parameter");
                        for (Iterator<?> iterator = parameters.iterator(); iterator
                                        .hasNext();) {
                                Element parameter = (Element) iterator.next();
                                actionStr += parameter.getAttributeValue("id");
                                if (iterator.hasNext()) {
                                        actionStr += " ";
                                }
                        }
                        actionStr += ")";

                        // action duration
                        String duration = action.getChildText("duration");
                        if (!duration.equals("")) {
                                actionStr += " [" + duration + "]";
                        }
                        
                        if(iter.hasNext()){
                                pddlplan += actionStr +"\n";
                        }
                        else{
                                pddlplan += actionStr;
                        }
                        //pddlplan += actionStr +"\n";
                }
        }


        return pddlplan;
    }


	/*public static void main(String[] args) {
		Element project = null;
		try {
			project = XMLUtilities.readFromFile("examples/DriverLogDomain.xml").getRootElement();
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(project != null){
			Element xpddlDomain = ToXPDDL.XMLToXPDDLDomain(project, ToXPDDL.PDDL_3_0);
			String pddl = parseXPDDLToPDDL(xpddlDomain, "");
			System.out.println(pddl);
			
			List<?> problems = project.getChild("diagrams").getChild("planningProblems").getChildren("problem");
			for (Iterator<?> iter = problems.iterator(); iter.hasNext();) {
				Element problem = (Element) iter.next();
				Element xpddlProblem = ToXPDDL.XMLToXPDDLProblem(problem, ToXPDDL.PDDL_3_0);
				String pddlProblem = parseXPDDLToPDDL(xpddlProblem, "");
				System.out.println(pddlProblem);
				System.out.println("\n-----------------------------------------------------------------------------------------------\n");
			}
		}
		
	}*/
	
}
