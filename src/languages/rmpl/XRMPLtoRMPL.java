package src.languages.rmpl;

import java.util.Iterator;

import org.jdom.Element;

public class XRMPLtoRMPL {

	//public XRMPLtoRMPL() {
		// TODO Auto-generated constructor stub
	//}
	
    /**
     * Parse XRMPL elements into RMPL elements
     * @param rmplNode
     * @param identation
     * @return
     */
    public static String parseXRMPLToRMPL(Element xrmplNode, String identation){
		String rmpl = "";
		
		// Root node
		if(xrmplNode.getName().equals("RMPL")){
			rmpl = "// This model was generated automatically by itSIMPLE.\n\n";
					
			for (Iterator<?> iter = xrmplNode.getChildren().iterator(); iter.hasNext();) {
				Element xpddlChild = (Element) iter.next();
				
				rmpl += parseXRMPLToRMPL(xpddlChild, identation);
			}
			
			rmpl += "\n";
		}
		// Class node
		else if(xrmplNode.getName().equals("Class")){
			//User defined Class
			//if (!xrmplNode.getAttributeValue("name").equals("Main")){
				rmpl += identation + "class "+ xrmplNode.getAttributeValue("name") +" {\n";
				
				for (Iterator<?> iter = xrmplNode.getChildren().iterator(); iter.hasNext();) {
					Element xpddlChild = (Element) iter.next();
					
					rmpl += parseXRMPLToRMPL(xpddlChild, identation + "  ");
				}
				
				rmpl += identation + "}\n\n";
			//}
			//Main Class
			//else{
				//TODO: specify the planning problem
			//}
		}
		
		// Mode node
		else if(xrmplNode.getName().equals("Mode")){
			rmpl += identation + "value "+ xrmplNode.getAttributeValue("name") +";\n";
		}

		// Field node
		else if(xrmplNode.getName().equals("Field")){
			if (!xrmplNode.getChildren().isEmpty()){
				Element varSpec = xrmplNode.getChild("VarSpec");
				rmpl += identation + parseXRMPLToRMPL(varSpec, "") + ";\n";
			}
		}
		
		// VarSpec node
		else if(xrmplNode.getName().equals("VarSpec")){
			rmpl += identation + xrmplNode.getAttributeValue("type") + " "+ xrmplNode.getAttributeValue("name");
		}
		
		// ParameterList
		else if(xrmplNode.getName().equals("ParameterList")){
			for (Iterator<?> iter = xrmplNode.getChildren().iterator(); iter.hasNext();) {
				Element xpddlChild = (Element) iter.next();
				rmpl += parseXRMPLToRMPL(xpddlChild, "");
				if (iter.hasNext()){
					rmpl += ", ";
				}
			}
		}
		
		// Constructor
		else if(xrmplNode.getName().equals("Constructor")){
			rmpl += "\n";
			rmpl += identation + xrmplNode.getAttributeValue("name");
			//Parameter list
			Element parameterList = xrmplNode.getChild("ParameterList");
			rmpl += " (" + parseXRMPLToRMPL(parameterList, "") + ") {\n";
			
			
			// Method content
			String methodContent = "";
			for (Iterator<?> iter = xrmplNode.getChildren().iterator(); iter.hasNext();) {
				Element xpddlChild = (Element) iter.next();
				if (!xpddlChild.getName().equals("ParameterList"))
					methodContent += parseXRMPLToRMPL(xpddlChild, identation + "  ");
			}
			if (!methodContent.trim().equals("")){
				rmpl += methodContent;
			}
			rmpl += identation + "}\n\n"; // close method 
		}
		
		// Assignment node
		else if(xrmplNode.getName().equals("Assignment")){
			Element assignment = (Element)xrmplNode.getChildren().get(0);
			rmpl += identation + xrmplNode.getAttributeValue("lhs") + " = " + parseXRMPLToRMPL(assignment, "") + ";\n";
		}	
		
		// New node
		else if(xrmplNode.getName().equals("New")){
			rmpl += "new " + xrmplNode.getAttributeValue("class") + "()";//TODO: this might gave parameters also
		}	
		
		// Method node
		else if(xrmplNode.getName().equals("Method")){
			
			// Primitive method?
			Element primitive = xrmplNode.getChild("Primitive");
			if (primitive !=null) {
				rmpl += identation + "primitive method "+ xrmplNode.getAttributeValue("name");
			}
			else  {
				rmpl += identation + "method "+ xrmplNode.getAttributeValue("name");
			}
			//rmpl += identation + "primitive method "+ xrmplNode.getAttributeValue("name");
			
			// Parameter list
			Element parameterList = xrmplNode.getChild("ParameterList");
			rmpl += " (" + parseXRMPLToRMPL(parameterList, "") + ")";
			
			// Duration
			Element duration = xrmplNode.getChild("Duration");
			if (duration != null){
				Element temporal_constraints = (Element)duration.getChildren().get(0);
				if (temporal_constraints.getName().equals("Controllable")){
					rmpl += " ["+ temporal_constraints.getAttributeValue("lbound") + ", " + temporal_constraints.getAttributeValue("ubound") + "]";
				}
			}
			
			// Method content
			String methodContent = "";
			for (Iterator<?> iter = xrmplNode.getChildren().iterator(); iter.hasNext();) {
				Element xrmplChild = (Element) iter.next();
				if (!xrmplChild.getName().equals("ParameterList") && !xrmplChild.getName().equals("Duration"))
					methodContent += parseXRMPLToRMPL(xrmplChild, identation + "  ");
			}
			if (!methodContent.trim().equals("")){
				rmpl += methodContent;
			}
			

		}
		
		else if(xrmplNode.getName().equals("Behavior")){
			rmpl += "\n";
			rmpl += identation + "  ";
			String preconditionContent = parseXRMPLToRMPL(xrmplNode.getChild("PreCondition"), "");
			String effectContent = parseXRMPLToRMPL(xrmplNode.getChild("PostCondition"), "");
			if (!preconditionContent.trim().equals("")){
				rmpl += preconditionContent + "\n";
			}
			if (!effectContent.trim().equals("")){
				if (!preconditionContent.trim().equals("")){
					rmpl += identation + "  ";	
				}
				rmpl += "=> " + effectContent;
			}
			rmpl += ";\n"; // close method 
		}
		
		
		
		// PreCondition and PostCondition nodes
		else if(xrmplNode.getName().equals("PreCondition") ||
				xrmplNode.getName().equals("PostCondition")){
			if (xrmplNode.getChildren().size() > 0){
				Element xpddlChild = (Element) xrmplNode.getChildren().get(0);
				rmpl += parseXRMPLToRMPL(xpddlChild, "");
			}
		}
		
		// Conjunction and Disjunction nodes
		else if(xrmplNode.getName().equals("Conjunction") || 
				xrmplNode.getName().equals("Disjunction")){
			// set the operator
			String operator = "&&";
			if (xrmplNode.getName().equals("Disjunction")){
				operator = "||";
			}
			rmpl += "(";
			for (Iterator<?> iter = xrmplNode.getChildren().iterator(); iter.hasNext();) {
				Element xpddlChild = (Element) iter.next();
				rmpl += parseXRMPLToRMPL(xpddlChild, "");
				if (iter.hasNext()){
					rmpl += " " + operator + " ";
				}
			}
			rmpl += ")";
		}
		
		// Equals node
		else if(xrmplNode.getName().equals("Equals")){
			rmpl = identation;
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(0), "") + " == ";
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(1), "");			
		}
		
		// Greater than node
		else if(xrmplNode.getName().equals("GreaterThan")){
			rmpl = identation;;
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(0), "") + " > ";
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(1), "");
		}
		
		// Less than node
		else if(xrmplNode.getName().equals("LessThan")){
			rmpl = identation;
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(0), "") + " < ";
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(1), "");			
		}
		
		// Great than node
		else if(xrmplNode.getName().equals("GreatEqual")){
			rmpl = identation;
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(0), "") + " >= ";
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(1), "");			
		}
		
		// Less then node
		else if(xrmplNode.getName().equals("LessEqual")){
			rmpl = identation;
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(0), "") + " <= ";
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(1), "");			
		}
		
		// Add
		else if(xrmplNode.getName().equals("Add")){
			rmpl = identation;
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(0), "") + " + ";
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(1), "");
		}
		
		// Subtract
		else if(xrmplNode.getName().equals("Subtract")){
			rmpl = identation;
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(0), "") + " - ";
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(1), "");
		}
		
		// Multiply
		else if(xrmplNode.getName().equals("Multiply")){
			rmpl = identation;
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(0), "") + " * ";
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(1), "");
		}
		
		// Divide
		else if(xrmplNode.getName().equals("Divide")){
			rmpl = identation;
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(0), "") + " / ";
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(1), "");
		}
		
		// Assign
		else if(xrmplNode.getName().equals("Assign")){
			rmpl = identation;
			//TODO: in the new RMPL the assign operator is '='
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(0), "") + " == ";
			rmpl += parseXRMPLToRMPL((Element)xrmplNode.getChildren().get(1), "");
		}
		
		// Identifier
		else if(xrmplNode.getName().equals("Identifier")){
			rmpl += xrmplNode.getAttributeValue("name");
		}
		
		
		
		// Body
		else if(xrmplNode.getName().equals("Body")){
			rmpl += "{\n";
			for (Iterator<?> iter = xrmplNode.getChildren().iterator(); iter.hasNext();) {
				Element xrmplChild = (Element) iter.next();
				rmpl += parseXRMPLToRMPL(xrmplChild, identation + "  ");
			}
			rmpl += identation + "}\n";
		}
		
		// Sequence
		else if(xrmplNode.getName().equals("Sequence")){
			rmpl += identation + "sequence ";
			for (Iterator<?> iter = xrmplNode.getChildren().iterator(); iter.hasNext();) {
				Element xrmplChild = (Element) iter.next();
				rmpl += parseXRMPLToRMPL(xrmplChild, identation + "  ");
			}
		}
		
		// Achieve
		else if(xrmplNode.getName().equals("Achieve")){
			//rmpl += identation + "(";
			rmpl += identation;
			for (Iterator<?> iter = xrmplNode.getChildren().iterator(); iter.hasNext();) {
				Element xrmplChild = (Element) iter.next();
				rmpl += parseXRMPLToRMPL(xrmplChild, "");
				//TODO: consider && and || ?
			}
			//rmpl += identation + ");";
			rmpl += ";\n";
		}
		
		
		
		
		
		
		
		return rmpl;
    }
	

}
