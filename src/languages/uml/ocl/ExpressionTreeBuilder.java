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

package src.languages.uml.ocl;


import java.util.Stack;
import java.util.StringTokenizer;
import java.util.Vector;

import org.jdom.Element;

import src.languages.uml.ocl.OCLOperator.NotAnOperatorException;
import src.languages.xml.XMLUtilities;


public class ExpressionTreeBuilder{
	
	private  Vector<String> separators = new Vector<String>();
	private  Vector<String> keywords = new Vector<String>();
	private  Vector<String> input;
	private  Stack<Object> operators;
	private  Stack<Object> operands;
	private String expressionText;
	
	public ExpressionTreeBuilder (String expressionText){
		this.expressionText = expressionText;
		
		separators.addElement(" ");
		separators.addElement("\n");
		separators.addElement("@");
		separators.addElement("@pre");
		separators.addElement("|");
		
		separators.addElement("+"); 
		separators.addElement("-"); 
		separators.addElement("*"); 
		separators.addElement("/");
		
		separators.addElement("("); 
		separators.addElement(")");
		
		separators.addElement(".");
		separators.addElement("->");
		separators.addElement(",");
		separators.addElement(":");
		
		separators.addElement("=");
		//separators.addElement("==");	
		separators.addElement("<"); 
		separators.addElement(">");
		separators.addElement(">=");
		separators.addElement("<=");
		separators.addElement("<>");
		
		keywords.addElement("and");
		keywords.addElement("attr");
		keywords.addElement("context");
		keywords.addElement("def");
		keywords.addElement("else");
		keywords.addElement("endif");
		keywords.addElement("endpackage");
		keywords.addElement("if");
		keywords.addElement("implies");
		keywords.addElement("in");
		keywords.addElement("inv");
		keywords.addElement("let");
		keywords.addElement("not");
		keywords.addElement("oper");
		keywords.addElement("or");
		keywords.addElement("package");
		keywords.addElement("post");
		keywords.addElement("pre");
		keywords.addElement("then");
		keywords.addElement("xor");
	}
	
	public Element getExpressionTree(){
		return buildExpressionTree(expressionText);
	}
	
	
	private void processText(String text){
		String currentCharStr = "";
		String word = "";		
		
		for(int position = 0;position < text.length(); position++){
			currentCharStr = String.valueOf(text.charAt(position));
			
			if(separators.contains(currentCharStr)){
				if(!word.trim().equals("")){
					input.addElement(word);					
				}
				word = "";
				switch(currentCharStr.charAt(0)){
				case '@':{
					String atPre = text.substring(position, position+4);
					if(separators.contains(atPre)){
						input.addElement(atPre);
						position += 3;						
					}
					else{
						System.out.println("Syntax error found. Position = " + position);						
					}
				}
				break;
				case '<': case '>':{
					String operator = text.substring(position, position+2);
					if(separators.contains(operator)){
						input.addElement(operator);
						position++;
					}
					else{
						input.addElement(currentCharStr);
					}
				}
				break;
				case '-':{
						String operator = text.substring(position, position+2);
						if(separators.contains(operator)){
							input.addElement(operator);
							position++;
						}
						else if(operator.equals("--")){
							// comment case
							while(position < text.length() && text.charAt(position) != '\n')
								position++;
						}	
						else{
							input.addElement(currentCharStr);
						}
					
				}				
				break;
				case ' ': case '\n':{
					//do nothing
				}
				break;
				case '.':{
					// checks if the dot is in a number
					try{
						Double.parseDouble((String)input.get(input.size()-1));
						
						String number = (String)input.remove(input.size()-1);
						word = number + currentCharStr;
					}
					catch(NumberFormatException e){
						// not a number
						input.addElement(currentCharStr);
					}
				}
				break;
				case '(':{
					if(input.size() > 0){
						int parenthesisCount = 1;
						String lastToken = (String)input.get(input.size()-1);
						if(!keywords.contains(lastToken) && !separators.contains(lastToken)){
							word = (String)input.remove(input.size()-1) + currentCharStr;
							while(parenthesisCount > 0){//(text.charAt(position) != ')'){								
								position ++;
								word += text.charAt(position);
								if(text.charAt(position) == ')'){
								parenthesisCount--;
								}
								else if(text.charAt(position) == '('){
									parenthesisCount++;
								}
							}
						}
						else{
							input.addElement(currentCharStr);
						}
					}
					else{
						input.addElement(currentCharStr);
					}

				}
				break;
				default:{
					input.addElement(currentCharStr);
				}
				}
			}
			else{
				word = word + currentCharStr;
			}
			
		}
		if(!word.trim().equals("")){
			input.addElement(word);
		}		
	}
	
	public Element buildExpressionTree(String expression){
		input = new Vector<String>();
		operands = new Stack<Object>();
		operators = new Stack<Object>();		
		
		processText(expression);
		
		/*for(int i = 0; i < input.size(); i++){
			System.out.println(input.get(i));
		}*/
		
		for(int i = 0; i < input.size(); i++){
			String currentToken = (String)input.get(i);
			
			if(currentToken.equals("(") || currentToken.equals("if")){
				operators.push(currentToken);
			}
			else if(currentToken.equals(")")){
				while(!operators.peek().equals("(")){
					buildOperandTree();
				}
				operators.pop();
			}
			// then
			else if(currentToken.equals("then")){
				operators.push(currentToken);
			}
			// else
			else if(currentToken.equals("else")){

				operators.push(currentToken);
			}
			//endif
			else if(currentToken.equals("endif")){
				// create the if node
				Element ifNode = createExpressionTreeNode("opr", "if-then-else");
				
				// else or then instructions
				while(!operators.peek().equals("then") && !operators.peek().equals("else")){
					buildOperandTree();
				}
				// add the else or then node
				ifNode.addContent((Element)operands.pop());
				
				if(operators.pop().equals("else")){
					// then instructions
					while(!operators.peek().equals("then")){
						buildOperandTree();
					}
					// add the then node
					ifNode.addContent(0, (Element)operands.pop());
					
					operators.pop();
				}
				// condition
				while(!operators.peek().equals("if")){
					buildOperandTree();
				}
				
				// add the condition node
				ifNode.addContent(0, (Element)operands.pop());
				
				operators.pop();				

				operands.push(ifNode);
			}

			// operator
			else if(OCLOperator.isOCLOperator(currentToken)){
				OCLOperator operator = null;
				try {
					operator = new OCLOperator(currentToken);
				} catch (NotAnOperatorException e) {
					e.printStackTrace();
				}
				
				if (operator != null){
					while(!operators.isEmpty() && !operators.peek().equals("(")
							 && !operators.peek().equals("if")
							 && !operators.peek().equals("then")
							 && !operators.peek().equals("else")
							&& ((OCLOperator)operators.peek()).getPrecedence() >= operator.getPrecedence()){
						buildOperandTree();
					}
					operators.push(operator);
				}
			}
			// operation
			else if(currentToken.indexOf("(") > 0 && currentToken.indexOf(")") > 0 ){
				String symbol = currentToken.substring(0, currentToken.indexOf("("));
				if(OCLOperator.isOCLOperation(symbol)){
					// create the operation structure 
					Element operation = createExpressionTreeNode("opn", symbol);
					
					if(currentToken.indexOf("|") > 0){
						// parameters (forAll and exists only)
						String strParameters = currentToken.substring(currentToken.indexOf("(")+1, currentToken.indexOf("|")).replaceAll(" ", "");
						String paramType = strParameters.substring(strParameters.indexOf(":"));
						StringTokenizer st = new StringTokenizer(strParameters.substring(0, strParameters.indexOf(":")), ",");
						while(st.hasMoreTokens()){
							String param = st.nextToken().trim();
							operation.addContent(createExpressionTreeNode("par", param + paramType));
						}
						//suchthat
						Element suchthat = createExpressionTreeNode("opr", "|");
						String suchthatExpression = currentToken.substring(currentToken.indexOf("|")+1, currentToken.lastIndexOf(")"));
						ExpressionTreeBuilder builder = new ExpressionTreeBuilder(suchthatExpression);
						suchthat.addContent(builder.getExpressionTree());
						
						operation.addContent(suchthat);
						
					}
					else{
						// no parameters (includes, including, excludes, excluding)
						String argumentStr = currentToken.substring(currentToken.indexOf("(")+1, currentToken.indexOf(")"));
						ExpressionTreeBuilder builder = new ExpressionTreeBuilder(argumentStr);
						operation.addContent(builder.getExpressionTree());
					}
					
					operands.push(operation);
				}
				else{
					//parameterized attribute
					operands.push(createExpressionTreeNode("opd", currentToken));
				}
			}
			// operand			
			else{
				operands.push(createExpressionTreeNode("opd", currentToken));
			}
		}
		
		while(!operators.isEmpty()){
			buildOperandTree();
		}
		//System.out.println(operands.peek()+".");
		Element root = (Element)operands.pop();
		//root.setAttribute("order", "0");

		return root;		
	}
	
	private void buildOperandTree(){
		OCLOperator operator = (OCLOperator)operators.pop();		
		Vector<Object> operandsVector = new Vector<Object>();
		for(int i = operator.getType()-1; i >= 0; i--){			
			Element operandNode = (Element)operands.pop();
			
			if(operator.toString().equals(operandNode.getAttributeValue("data")) && 
					(operator.toString().equals("and") || operator.toString().equals("or")
							|| operator.toString().equals("."))){
				for(int j = operandNode.getChildren().size()-1; j >= 0 ; j--){
					Element operandChildNode = (Element)operandNode.removeContent(j);
					operandsVector.addElement(operandChildNode);
				}
			}
			else{					
				if(operandNode != null)
					operandsVector.addElement(operandNode);
				else
					System.err.println("Error: null operand node");
			}
		}		
		Element operatorNode = createExpressionTreeNode("opr", operator.toString());
		//int order = 0;
		for(int i = operandsVector.size()-1; !operandsVector.isEmpty(); i--){
			Element operand = (Element)operandsVector.remove(i);
			//operand.setAttribute("order", String.valueOf(order++));
			operatorNode.addContent(operand);
		}
		//operatorNode.setAttribute("childs", String.valueOf(order));
		operands.push(operatorNode);
	}
	
	private static Element createExpressionTreeNode(String type, String data){
		Element node = new Element("node");		
		node.setAttribute("type", type);
		//node.setAttribute("childs", childs);
		node.setAttribute("data", data);
		//node.setAttribute("order", order);
		node.setAttribute("instance", "f");// this attribute is used to simulate the plan,
										   //so we know this value was already substituted by an instance value
		
		return node;
	}
	
/*	*//**
	 * Checks whether the given nodes are exactly the same.
	 * @param node1 an expression tree node
	 * @param node2 another expression tree node
	 * @return true if the nodes are equal, false otherwise
	 *//*
	@SuppressWarnings("unchecked")
	public static boolean equals(Element node1, Element node2){
		//boolean returnValue = true;
		
		if(!node1.getName().equals("node") || !node2.getName().equals("node")){
			// invalid nodes
			return false;
		}
		List children = node1.getChildren();
		int size1 = children.size();
		int size2 = node2.getChildren().size();
		// leaf nodes
		if(size1 == 0 || size2 == 0){
			if(!node1.getAttributeValue("type").equals(node2.getAttributeValue("type")) ||
					!node1.getAttributeValue("data").equals(node2.getAttributeValue("data")) ||
					!node1.getAttributeValue("instance").equals(node2.getAttributeValue("instance"))){
				return false;
			}
			return true;
		}
		
		// check the number of children
		else if(size1 == size2){
			// compare each node child
			List<Element> children1 = node1.getChildren();
			List<Element> children2 = node2.getChildren();
			for(int i = 0; i < size1; i++){
				if(!equals(children1.get(i), children2.get(i)))
					return false;
			}
			return true;
		}
		else return false;

	}*/

	
	
	public static void main(String[] args){
		ExpressionTreeBuilder builder = new ExpressionTreeBuilder(
				/*"if (hand.holding = x and hand.handempty = false and hand.at = tablefrom and tableto.available = true " +
				"and hand.hasAccessTo->exists(t : Table | t = tableto)) then hand.holding = x and " +
				"hand.handempty = false and hand.at = tableto and tablefrom.available = true and " +
				"tableto.available = false else x = 0 endif and hand.at = tableto");*/
				
				//"hand.at = tablefrom and tableto.available = true and " +
				//"hand.hasAccessTo->includes(tableto)");
				//"hand.hasAccessTo->exists(t : Table | t = tableto)");
				"g1.hasSecret->forAll(s: Secret| g2.hasSecret = g2.hasSecret->including(s))");
                                //"a.clear = b.clear and c.assoc = d.assoc");

		XMLUtilities.printXML(builder.getExpressionTree());
		
	}
	
}
