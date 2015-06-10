package src.languages.rmpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jdom.JDOMException;

import src.gui.ItSIMPLE;
import src.languages.uml.ocl.ExpressionTreeBuilder;
import src.languages.uml.ocl.OCLUtilities;
import src.languages.xml.XMLUtilities;

public class ToXRMPL {

	//public ToXRMPL() {
		// TODO Auto-generated constructor stub
	//}
	public static final String RMPL_1_0 = "rmpl10";
	private static final String PRECONDITION = "precondition";
	private static final String POSTCONDITION = "postcondition";
	private static final String TRUE_VALUE = "yes";
	private static final String FALSE_VALUE = "no";
	private static Element xrmplmodel;
	
	
	/**
	 * Translates the UML model to a XRMPL model
	 * @param project
	 * @return
	 */
	public static Element XMLToXRMPLModel(Element project, Element problem){
		xrmplmodel = null;
		
		// get the templates container
		Element templateNodes = null;
		try {
			templateNodes = (Element)XMLUtilities.readFromFile("resources/settings/commonData.xml").getRootElement().getChild("xrmplNodes").clone();
		} catch (JDOMException e) {
		} catch (IOException e) {
		}
		
		if(templateNodes != null){
			xrmplmodel = (Element)templateNodes.getChild("RMPL").clone();
			
			//Domain Specification
			
			// 1. Enumeration and Classes
			
			//1.1. Primitive classes
			List<?> result = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[type='Primitive']");
				result = path.selectNodes(project.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			for (int i = 0; i < result.size(); i++){
				Element Class = (Element)result.get(i);
				// Boolean class
				if (Class.getChildText("name").equals("Boolean")){
					Element boolean_class = (Element)templateNodes.getChild("Class").clone();
					boolean_class.setAttribute("name","Boolean");
					Element true_mode = new Element("Mode");
					true_mode.setAttribute("name", TRUE_VALUE);
					Element false_mode =  new Element("Mode");
					false_mode.setAttribute("name", FALSE_VALUE);
					boolean_class.addContent(true_mode);
					boolean_class.addContent(false_mode);
					xrmplmodel.addContent(boolean_class);
				}
			}
			
			//1.2. Enumerations (class with modes only)
			List<?> enumerations = project.getChild("elements").getChild("classes").getChildren("enumeration");
			for (Iterator<?> iter = enumerations.iterator(); iter.hasNext();) {
				Element enumeration = (Element) iter.next();
				Element enumeration_class = (Element)templateNodes.getChild("Class").clone();
				enumeration_class.setAttribute("name", enumeration.getChildText("name"));
				
				// Modes
				List<?> literals = enumeration.getChild("literals").getChildren("literal");
				for (Iterator<?> iterator = literals.iterator(); iterator.hasNext();) {
					Element literal = (Element) iterator.next();
					Element mode = new Element("Mode");
					mode.setAttribute("name", literal.getChildText("name"));
					enumeration_class.addContent(mode);
				}
				xrmplmodel.addContent(enumeration_class);
			}
			
			
			//1.3. General Classes
			//TODO: handle class hierarchy
			result = null;
			try {
				XPath path = new JDOMXPath("project/elements/classes/class[type!='Primitive']");
				result = path.selectNodes(project.getDocument());
			} catch (JaxenException e2) {			
				e2.printStackTrace();
			}
			
			for (int i = 0; i < result.size(); i++){
				Element Class = (Element)result.get(i);
				Element rmpl_class = (Element)templateNodes.getChild("Class").clone();
				rmpl_class.setAttribute("name",Class.getChildText("name"));
				
	
				// Class's fields/attributes
				List<?> attributes = null;
				try {
		            XPath path = new JDOMXPath("attributes/attribute");
					attributes = path.selectNodes(Class);
				} catch (JaxenException e) {
					e.printStackTrace();
				}
				for(Iterator<?> iter = attributes.iterator(); iter.hasNext();){
					Element attribute = (Element)iter.next();
					
					Element rmpl_field = (Element)templateNodes.getChild("Field").clone();
					Element varspec = rmpl_field.getChild("VarSpec");
					// field name
					varspec.setAttribute("name", attribute.getChildText("name"));
					// field type
					Element attributeType = null;
                    try {
                        XPath path = new JDOMXPath("project/elements/classes/*[@id='" + attribute.getChildText("type") + "']");
                        attributeType = (Element)path.selectSingleNode(project.getDocument());
                    } catch (JaxenException e) {
                        e.printStackTrace();
                    }
                    if(attributeType != null){
                    	varspec.setAttribute("type", attributeType.getChildText("name"));                    
                    }
					rmpl_class.addContent(rmpl_field);
					
					//TODO: attribute's parameters
				}
				
				// Constructor
				List<?> attributesWithInitialValues = null;
				try {
		            XPath path = new JDOMXPath("attributes/attribute[initialValue!='']");
		            attributesWithInitialValues = path.selectNodes(Class);
				} catch (JaxenException e) {
					e.printStackTrace();
				}
				if (attributesWithInitialValues != null && attributesWithInitialValues.size() > 0){
					Element class_constructor = (Element)templateNodes.getChild("Constructor").clone();
					class_constructor.setAttribute("name", Class.getChildText("name"));
					for(Iterator<?> iter = attributesWithInitialValues.iterator(); iter.hasNext();){
						Element attribute = (Element)iter.next();
						Element initialvalue_assignment = (Element)templateNodes.getChild("Assignment").clone();
						initialvalue_assignment.setAttribute("lhs", attribute.getChildText("name"));					
						Element identifier = (Element)templateNodes.getChild("Identifier").clone();
						String value = attribute.getChildText("initialValue");
						if (value.trim().toLowerCase().equals("true")){
							value = TRUE_VALUE;
						}
						else if (value.trim().toLowerCase().equals("false")){
							value = FALSE_VALUE;
						}
						identifier.setAttribute("name", value);
						initialvalue_assignment.addContent(identifier);
						class_constructor.addContent(initialvalue_assignment);
					}
					rmpl_class.addContent(class_constructor);
				}
				
			
				// Methods
				List<?> operators = null;
				try {
					XPath path = new JDOMXPath("operators/operator");
					operators = path.selectNodes(Class);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				
				for (Iterator<?> iter = operators.iterator(); iter.hasNext();) {
					Element operator = (Element) iter.next();
					Element rmpl_method = (Element)templateNodes.getChild("Method").clone();
					// name
					rmpl_method.setAttribute("name", operator.getChildText("name"));
					// parameters
	                for (Iterator<?> iterator = operator.getChild("parameters").getChildren("parameter").iterator(); iterator.hasNext();) {
	                        Element parameter = (Element) iterator.next();
	                        Element parameterClass = null;
	                        try {
	                                XPath path = new JDOMXPath("project/elements/classes/*[@id='" + parameter.getChildText("type") + "']");
	                                parameterClass = (Element)path.selectSingleNode(project.getDocument());
	                        } catch (JaxenException e) {			
	                                e.printStackTrace();
	                        }
	                        if(parameterClass != null){
	                                Element rmpl_method_parameter = new Element("VarSpec");
	                                rmpl_method_parameter.setAttribute("type", parameterClass.getChildText("name"));
	                                rmpl_method_parameter.setAttribute("name", parameter.getChildText("name"));
	                                rmpl_method.getChild("ParameterList").addContent(rmpl_method_parameter);
	                             
	                        }
	                }
	                //Primitive method
	                // Add Element  primitive tag
	                Element primitive = new Element ("Primitive");
	                rmpl_method.addContent(primitive);
	                

	                // Duration [lowerbound, upperbound]
	                boolean isDurative = Boolean.parseBoolean(operator.getChild("timeConstraints").getAttributeValue("timed"));

	                //check if there is any timing diagram for this operator
	                Element timingDiagram = null;
	                try {
	                    XPath path = new JDOMXPath("project/diagrams/timingDiagrams/timingDiagram[action/@class='" +
	                            operator.getParentElement().getParentElement().getAttributeValue("id") +
	                            "' and action/@id='"+operator.getAttributeValue("id")+"']");
	                    timingDiagram = (Element)path.selectSingleNode(project.getDocument());
	                } catch (JaxenException e) {
	                    e.printStackTrace();
	                }

	                if(isDurative){
	                	Element duration = (Element)templateNodes.getChild("Duration").clone();
	                    String durationStr = operator.getChild("timeConstraints").getChildText("duration");
	                    
	                    if (durationStr.trim().equals("")){
	                        //see if it can be found in the timing diagram
	                        if (timingDiagram!=null){
	                            durationStr = timingDiagram.getChild("frame").getChildText("duration").trim();
	                        }
	                    }
	                    durationStr = durationStr.trim();
	                    if(durationStr.equals("")){
	                        ItSIMPLE.getInstance().appendModelTranslationOutputPanelText("(!) WARNING: duration of durative-action \""+ operator.getChildText("name")+ "\" not specified.\n");
	                        System.out.println("WARNING: duration of action \""+ operator.getChildText("name")+ "\" not specified.\n");
	                    }
	                    else{
	                    	//TODO: Handle expressions in the duration/bound specs
	                    	// check if the duration has bounds
	                    	if (durationStr.startsWith("[") && durationStr.endsWith("]") && durationStr.contains(",")){
	    	                	Element controllable_bounds = (Element)templateNodes.getChild("Controllable").clone();
	    	                	
	    	                	durationStr = durationStr.replace("[", "");
	    	                	durationStr = durationStr.replace("]", "");
	    	                	durationStr = durationStr.replace("self.", "");
	    	                	String[] bounds = durationStr.split(",");
	    	                	
	    	                	controllable_bounds.setAttribute("lbound", bounds[0].trim());
	    	                	controllable_bounds.setAttribute("ubound", bounds[1].trim());
	                    		duration.addContent(controllable_bounds);
	                    		
	                    	}
	                    	// if it is a single value or expression
	                    	else {
	                    		Element controllable_bounds = (Element)templateNodes.getChild("Controllable").clone();
	                    		controllable_bounds.setAttribute("lbound", durationStr);
	                    		controllable_bounds.setAttribute("ubound", durationStr);
	                    		duration.addContent(controllable_bounds);
	                    	}
	                    }
	                    rmpl_method.addContent(duration);
	                }					
	                                
	                
					// Precondition and postcondition
					Element rmpl_method_behavior = (Element)templateNodes.getChild("Behavior").clone();
					rmpl_method.addContent(rmpl_method_behavior);

					// gathering condition strings
					String constraintsExpression = operator.getChildText("constraints").trim();
	                String precondition;
	                String postcondition;
	                Element annotatedConditions = null;
	                //TODO: temporally annotated conditions 
	                
	                if(!constraintsExpression.equals("")){
	                    // precondition
	                    precondition = constraintsExpression.substring(
	                                    constraintsExpression.indexOf("pre:"), 
	                                    constraintsExpression.indexOf("post:")).trim();
	                    // take off "pre:"
	                    precondition = precondition.substring(4, precondition.length()).trim();

	                    // postcondition		
	                    postcondition = constraintsExpression.substring(
	                                    constraintsExpression.indexOf("post:"), 
	                                    constraintsExpression.length()).trim();
	                    // take off "post:"
	                    postcondition = postcondition.substring(5, postcondition.length()).trim();				

	                }
	                else{
                        Element actionNode = OCLUtilities.buildConditions(operator);
                        //XMLUtilities.printXML(actionNode);

                        // Preconditions
                        List<?> preconditions = null;
                        try {
                                XPath path = new JDOMXPath("preconditions/conditions/condition");
                                preconditions = path.selectNodes(actionNode);
                        } catch (JaxenException e) {			
                                e.printStackTrace();
                        }
                        precondition = "";
                        if(preconditions.size() > 0){
                                for (Iterator<?> iterator = preconditions.iterator(); iterator.hasNext();) {
                                        Element condition = (Element) iterator.next();
                                        precondition += condition.getText();
                                        if(iterator.hasNext())
                                                precondition += " and ";
                                }
                        }

                        // Postconditions
                        List<?> postconditions = null;
                        try {
                                XPath path = new JDOMXPath("postconditions/conditions/condition");
                                postconditions = path.selectNodes(actionNode);
                        } catch (JaxenException e) {			
                                e.printStackTrace();
                        }
                        postcondition = "";
                        if(postconditions.size() > 0){
                                for (Iterator<?> iterator = postconditions.iterator(); iterator.hasNext();) {
                                        Element condition = (Element) iterator.next();
                                        postcondition += condition.getText();
                                        if(iterator.hasNext())
                                                postcondition += " and ";
                                }
                        }
                        
                        annotatedConditions = actionNode.getChild("annotatedoclexpressions");
	                }
					
					// precondition
	                // clean up preconditions
	                if (precondition.indexOf("&#xd;") > -1){				
	                    precondition.replaceAll("&#xd;", " "); // return carriage in xml
	                }
	                
	                if(!precondition.trim().equals("")){
	                    try{					
	                        ExpressionTreeBuilder builder = new ExpressionTreeBuilder(precondition);
	                        Element preconditionExpressionTree = builder.getExpressionTree();
	                        //XMLUtilities.printXML(preconditionExpressionTree);
	                        Element xrmplPrecondition = buildCondition(preconditionExpressionTree, rmpl_method, null, PRECONDITION);
	                        rmpl_method_behavior.getChild("PreCondition").addContent(xrmplPrecondition);
	                        
	                    }
	                    catch(Exception e){
	                        e.printStackTrace();
	                        ItSIMPLE.getInstance().appendModelTranslationOutputPanelText("(!!) ERROR: error on method: \"" + rmpl_method.getAttributeValue("name") + "\", with the precondition: \"" + precondition + "\"." +
	                                        "\nInvalid or unsupported syntax.\n");
	                        System.err.println("Error on action: \"" + rmpl_method.getAttributeValue("name") + "\", with the precondition: \"" + precondition + "\"." +
	                                        "\nInvalid or unsupported syntax.\n");
	                    }
	                }

	                
					// postcondition
	                if (postcondition.indexOf("&#xd;") > -1){				
	                    postcondition.replaceAll("&#xd;", " "); // return carriage in xml
	                }

	                if(!postcondition.trim().equals("")){
	                    try{
	                        ExpressionTreeBuilder builder = new ExpressionTreeBuilder(postcondition);
	                        Element postconditionExpressionTree = builder.getExpressionTree();
	                        //XMLUtilities.printXML(postconditionExpressionTree);
	                        Element effect = buildCondition(postconditionExpressionTree, rmpl_method, null, POSTCONDITION);
	                        rmpl_method_behavior.getChild("PostCondition").addContent(effect);
	                    }
	                    catch(Exception e){
	                        e.printStackTrace();
	                        ItSIMPLE.getInstance().appendModelTranslationOutputPanelText("(!!) ERROR: error on action: \"" + rmpl_method.getAttributeValue("name") + "\", with the postcondition: \"" + postcondition + "\"." +
	                                        "\nInvalid or unsupported syntax.\n");
	                        System.err.println("Error on action: \"" + rmpl_method.getAttributeValue("name") + "\", with the postcondition: \"" + postcondition + "\"." +
	                                        "\nInvalid or unsupported syntax.\n");
	                    }
	                }
	               
					rmpl_class.addContent(rmpl_method);
				}
				
				xrmplmodel.addContent(rmpl_class);
			}
			
			
			
			
			
			
			
			// PROBLEM SPECIFICATION
			
			// Main class and its Constructor
			Element main_class = (Element)templateNodes.getChild("Class").clone();
			main_class.setAttribute("name","Main");
			Element class_constructor = (Element)templateNodes.getChild("Constructor").clone();
			class_constructor.setAttribute("name","Main");
			
			// Main Fields and Object Instances in the Constructor
			// Objects
			Element domain = problem.getParentElement().getParentElement();           
            //Approach: putting just the objects that are used.
            Element domainObjects = domain.getChild("elements").getChild("objects");
            List<?> tobjectDiagrams = problem.getChild("objectDiagrams").getChildren("objectDiagram");
			for (Iterator<?> iter = tobjectDiagrams.iterator(); iter.hasNext();) {
				Element objectDiagram = (Element) iter.next();
                List<?> objects = objectDiagram.getChild("objects").getChildren("object");
                for (Iterator<?> iter2 = objects.iterator(); iter2.hasNext();) {
                    Element object = (Element) iter2.next();

                    //find the object in the domain
                    Element theDomainObject = null;
                    try {
                        XPath path = new JDOMXPath("object[@id='" + object.getAttributeValue("id") + "']");
                        theDomainObject = (Element)path.selectSingleNode(domainObjects);
                    } catch (JaxenException e) {
                        e.printStackTrace();
                    }
                    if(theDomainObject!=null){
                        //check if there is already an object with the same name in the xrmpl
                        Element xrmplObject = null;
                        try {
                            XPath path = new JDOMXPath("Field/VarSpec[@name='" + theDomainObject.getChildText("name") + "']");
                            xrmplObject = (Element)path.selectSingleNode(main_class);
                        } catch (JaxenException e) {
                            e.printStackTrace();
                        }
                        //if there is not such object yet then create it
                        if(xrmplObject==null){
                            Element objectType = null;
                            try {
                                XPath path = new JDOMXPath("elements/classes/class[@id='" + theDomainObject.getChildText("class") + "']");
                                objectType = (Element)path.selectSingleNode(project);
                            } catch (JaxenException e) {
                                e.printStackTrace();
                            }
                            if(objectType != null && !objectType.getChildText("stereotype").equals("utility")){
                            	
                            	//Main Class attributes
                            	Element object_instance = (Element)templateNodes.getChild("Field").clone();
                            	Element varspec = object_instance.getChild("VarSpec");
                            	varspec.setAttribute("type", objectType.getChildText("name"));
                            	varspec.setAttribute("name", theDomainObject.getChildText("name"));
                            	main_class.addContent(object_instance);
                            	
                            	
                            	//Class Main's constructor - object instances
                            	//check if the object was already instantiated
                            	Element objectInstanceNew = null;
                                try {
                                    XPath path = new JDOMXPath("Assignment[@lhs='" + theDomainObject.getChildText("name") + "']/New[@class='"+objectType.getChildText("name")+"']");
                                    objectInstanceNew = (Element)path.selectSingleNode(class_constructor);
                                } catch (JaxenException e) {
                                    e.printStackTrace();
                                }
                            	if (objectInstanceNew==null){
	                            	Element object_instance_assigment = (Element)templateNodes.getChild("Assignment").clone();
	                            	object_instance_assigment.setAttribute("lhs", theDomainObject.getChildText("name"));
	                            	Element new_obj  = (Element)templateNodes.getChild("New").clone();
	                            	new_obj.setAttribute("class", objectType.getChildText("name"));
	                            	object_instance_assigment.addContent(new_obj);
	                            	class_constructor.addContent(object_instance_assigment);
                            	}
                            }
                        }
                    }
                }
			}
			main_class.addContent(class_constructor);
			

		
			// Constructor (initial State) and Method Run - GOAL (limited to a Sequence for now) 
			//TODO: We are currently limited by a sequence of snapshot (in the order they appear) 
			//      as the goal to be achieve. We need to make this more general by allowing nested
			//      representation and parallel goals. We will be to potentially use the 
			//      Activity Diagram here.
			
			//2.3 Method Run
			Element main_method = (Element)templateNodes.getChild("Method").clone();
			main_method.setAttribute("name","run");
			Element main_body = (Element)templateNodes.getChild("Body").clone();
			main_method.addContent(main_body);
			Element sequence = (Element)templateNodes.getChild("Sequence").clone();
			main_body.addContent(sequence);
			// fill out the body
			
			List<?> objectDiagrams = problem.getChild("objectDiagrams").getChildren("objectDiagram");
			for (Iterator<?> iter = objectDiagrams.iterator(); iter.hasNext();) {
				Element objectDiagram = (Element) iter.next();
				
				// Constructor (initial State)
				if(objectDiagram.getChildText("sequenceReference").equals("init")){
					parseObjectDiagram(objectDiagram, class_constructor, templateNodes, true);
				}
				// Method Run - Goal
				else{
					// Create Body and Achieve for each diagram - in the sequence they appear
					Element body = (Element)templateNodes.getChild("Body").clone();
					Element achieve = (Element)templateNodes.getChild("Achieve").clone();
					body.addContent(achieve);
					Element conjunction = new Element("Conjunction");
					achieve.addContent(conjunction);
					parseObjectDiagram(objectDiagram, conjunction, templateNodes, false);
					sequence.addContent(body);
				}
				

			}
			
		
			
			main_class.addContent(main_method);
			
			xrmplmodel.addContent(main_class);
		}
		

		//XMLUtilities.printXML(xrmplmodel);
		return xrmplmodel;		
		
	}
	

	/**
	 * This method creates the RMPL conditions in XML format.
	 * @param expressionTreeRoot
	 * @param method
	 * @param operation
	 * @param conditionType
	 * @return
	 */
	private static Element buildCondition(Element expressionTreeRoot, Element method, 
            Element operation, String conditionType){
		//XMLUtilities.printXML(expressionTreeRoot);
		Element node = null;
		String data = expressionTreeRoot.getAttributeValue("data");
		
        //System.out.println(" - Data: " + data);

        //leaf nodes
		if(expressionTreeRoot.getChildren().size() == 0){
		    // it's not an operator
			node = new Element("Identifier");
			String value = data;
			if (value.trim().toLowerCase().equals("true")){
				value = TRUE_VALUE;
			}
			else if (value.trim().toLowerCase().equals("false")){
				value = FALSE_VALUE;
			}
			node.setAttribute("name", value);
		}
		
		else if (data.equals(".")){
			Element firstNode = (Element)expressionTreeRoot.getChildren().get(0);
			Element lastNode = (Element)expressionTreeRoot.getChildren().get(expressionTreeRoot.getChildren().size()-1);
			// the last node is a function or a predicate
			String lastNodeData = lastNode.getAttributeValue("data");
						
			//TODO: consider parameterized attributes
			
			String identifier_str = "";
			//eliminate self
			if (!firstNode.getAttributeValue("data").equals("self"))
				identifier_str = firstNode.getAttributeValue("data") +".";
			identifier_str += lastNodeData;
			node = new Element("Identifier");
			node.setAttribute("name", identifier_str);
		}
				
		
		//not leaf nodes
		else if(data.equals("=") || data.equals("<>")){			
			Element left = buildCondition((Element)expressionTreeRoot.getChildren().get(0), method, operation, conditionType);
			Element right = buildCondition((Element)expressionTreeRoot.getChildren().get(1), method, operation, conditionType);
			if (left!=null && right!=null){
				if(left.getName().equals("Identifier") || right.getName().equals("predicate")){
                    if(conditionType.equals(PRECONDITION) && data.equals("=")){
                    	//case of A = B
	                    node = new Element("Equals");
                    }
                    else if(conditionType.equals(PRECONDITION) && data.equals("<>")){
                    	//case of A = B
                        node = new Element("Not-equal");
                    }
                    else if(conditionType.equals(POSTCONDITION)){
                    	//case of A = B
                        node = new Element("Assign");
                    }
                    node.addContent(left);
                    node.addContent(right);
                }
			}
		}
				
		else{			
			data = data.toLowerCase().trim();
			if(data.equals("and")){
				node = new Element("Conjunction");
			}
			else if(data.equals("or")){
				node = new Element("Disjunction");
			}
			else if(data.equals(">")){
				node = new Element("GreaterThan");
			}
			else if(data.equals("<")){
				node = new Element("LessThan");
			}
			else if(data.equals(">=")){
				node = new Element("GreatEqual");
			}
			else if(data.equals("<=")){
				node = new Element("LessEqual");
			}
			else if(data.equals("+")){
				node = new Element("Add");
			}
			else if(data.equals("-")){
				node = new Element("Subtract");
			}
			else if(data.equals("*")){
				node = new Element("Multiply");
			}
			else if(data.equals("/")){
				node = new Element("Divide");
			}
            else if(data.equals("not")){
                node = new Element("Not");
            }
			/*else if(data.equals("|")){
				node = new Element("suchthat");
			}*/
			else{
                ItSIMPLE.getInstance().appendModelTranslationOutputPanelText("(!!) ERROR: undentify element '"+data+"'.\n");
                System.err.println(data);
			}
			for (Iterator<?> iter = expressionTreeRoot.getChildren().iterator(); iter.hasNext();) {
				Element child = (Element) iter.next();
				Element xrmplChild = buildCondition(child, method, operation, conditionType);				
				
				if(xrmplChild != null){
					node.addContent(xrmplChild);					
				}
				else{
	                ItSIMPLE.getInstance().appendModelTranslationOutputPanelText("(!!) ERROR: error parsing " + conditionType + "\n Action: " + method.getAttributeValue("name") + ", Data: " + child.getAttributeValue("data")+"\n");
	                System.err.println("Error parsing " + conditionType + "\n Action: " + method.getAttributeValue("name") + ", Data: " + child.getAttributeValue("data"));
				}
			}
			
		}
		
		
		return node;
	}
	
	
	
	/**
	 * Used to parse object diagrams from the XML model to the XRMPL language 
	 * @param objectDiagram the object diagram to be parsed
	 * @param containerXRMPLNode the xRMPL node where the result is to be put
	 */
	private static void parseObjectDiagram(Element objectDiagram, Element containerXRMPLNode, Element templateNodes, boolean isConstructor){
		Element domain;
		//Element project;
		if(objectDiagram.getName().equals("repositoryDiagram")){
			domain = objectDiagram.getParentElement().getParentElement();
		}
		else{
			domain = objectDiagram.getParentElement().getParentElement().getParentElement().getParentElement();
		}		
		Element project = domain.getParentElement().getParentElement().getParentElement();
		
		if(containerXRMPLNode != null){
			
            // PREDICATES and FUNCTIONS			

            // object ASSOCIATIONS
			List<?> objectAssociations = objectDiagram.getChild("associations").getChildren("objectAssociation");
			for (Iterator<?> iterator = objectAssociations.iterator(); iterator.hasNext();) {
				Element objectAssociation = (Element) iterator.next();
				// get the corresponding class association
				Element classAssociation = null;
				try {				
					XPath path = new JDOMXPath("elements/classAssociations/classAssociation[@id='"+
							objectAssociation.getChildText("classAssociation") +"']");
					classAssociation = (Element)path.selectSingleNode(project);
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(classAssociation != null){
					// get the association ends
					Element sourceEnd = (Element)classAssociation.getChild("associationEnds").getChildren("associationEnd").get(0);
					Element targetEnd = (Element)classAssociation.getChild("associationEnds").getChildren("associationEnd").get(1);
					Element sourceObjectEnd = null;
					Element targetObjectEnd = null;
					try {
						XPath path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='"+ sourceEnd.getAttributeValue("id") +"']");
						sourceObjectEnd = (Element)path.selectSingleNode(objectAssociation);
						path = new JDOMXPath("associationEnds/objectAssociationEnd[@id='"+ targetEnd.getAttributeValue("id") +"']");
						targetObjectEnd = (Element)path.selectSingleNode(objectAssociation);
					} catch (JaxenException e) {			
						e.printStackTrace();
					}
					//finding the objects
					Element sourceObject = null;
					Element targetObject = null;					
					try {
						XPath path = new JDOMXPath("elements/objects/object[@id='"+ sourceObjectEnd.getAttributeValue("element-id") +"']");
						//sourceObject = (Element)path.selectSingleNode(problem);
						sourceObject = (Element)path.selectSingleNode(domain);
						path = new JDOMXPath("elements/objects/object[@id='"+ targetObjectEnd.getAttributeValue("element-id") +"']");
						//targetObject = (Element)path.selectSingleNode(problem);
						targetObject = (Element)path.selectSingleNode(domain);								
					} catch (JaxenException e) {			
						e.printStackTrace();
					}							
					if(sourceObject != null && targetObject != null){						
						if(!targetEnd.getChild("rolename").getChildText("value").trim().equals("")){
                            boolean isMultipliticy1or01 = false;
                            if (targetEnd.getChild("multiplicity").getChildText("value").equals("1") || targetEnd.getChild("multiplicity").getChildText("value").equals("0..1")){
                                isMultipliticy1or01 = true;
                            }
                            if (isConstructor){
	                            Element predfunc = (Element)templateNodes.getChild("Assignment").clone();
	                            predfunc.setAttribute("lhs", sourceObject.getChildText("name") + "." + targetEnd.getChild("rolename").getChildText("value"));
	                            Element identifier = (Element)templateNodes.getChild("Identifier").clone();
	                            identifier.setAttribute("name", targetObject.getChildText("name"));
	                            predfunc.addContent(identifier);
	                            containerXRMPLNode.addContent(predfunc);
                            }
                            else {
                            	Element operator = new Element("Equals");
                            	Element identifier1 = (Element)templateNodes.getChild("Identifier").clone();
                            	identifier1.setAttribute("name", sourceObject.getChildText("name") + "." + targetEnd.getChild("rolename").getChildText("value"));
	                            Element identifier2 = (Element)templateNodes.getChild("Identifier").clone();
	                            identifier2.setAttribute("name", targetObject.getChildText("name"));
	                            operator.addContent(identifier1);
	                            operator.addContent(identifier2);
	                            containerXRMPLNode.addContent(operator);
                            }
						}
						if(!sourceEnd.getChild("rolename").getChildText("value").trim().equals("")){
                            boolean isMultipliticy1or01 = false;
                            if (sourceEnd.getChild("multiplicity").getChildText("value").equals("1") || sourceEnd.getChild("multiplicity").getChildText("value").equals("0..1")){
                                isMultipliticy1or01 = true;
                            }
                            if (isConstructor){
                                Element predfunc = (Element)templateNodes.getChild("Assignment").clone();
                                predfunc.setAttribute("lhs", targetObject.getChildText("name") + "." + sourceEnd.getChild("rolename").getChildText("value"));
                                Element identifier = (Element)templateNodes.getChild("Identifier").clone();
                                identifier.setAttribute("name", sourceObject.getChildText("name"));
                                predfunc.addContent(identifier);
                                containerXRMPLNode.addContent(predfunc);
                            }
                            else {
                            	Element operator = new Element("Equals");
                            	Element identifier1 = (Element)templateNodes.getChild("Identifier").clone();
                            	identifier1.setAttribute("name", targetObject.getChildText("name") + "." + sourceEnd.getChild("rolename").getChildText("value"));
	                            Element identifier2 = (Element)templateNodes.getChild("Identifier").clone();
	                            identifier2.setAttribute("name", sourceObject.getChildText("name"));
	                            operator.addContent(identifier1);
	                            operator.addContent(identifier2);
	                            containerXRMPLNode.addContent(operator);
                            }
                            
                            
						}
						if(sourceEnd.getChild("rolename").getChildText("value").trim().equals("") && 
								targetEnd.getChild("rolename").getChildText("value").trim().equals("")){
							// 4.1.2 Associations without rolenames							
							boolean sourceHasNavigation = Boolean.parseBoolean(sourceEnd.getAttributeValue("navigation"));
							boolean targetHasNavigation = Boolean.parseBoolean(targetEnd.getAttributeValue("navigation"));					
							// 4.1.2.1 Double navigation or without navigation: the order of the parameters is not important
							if((sourceHasNavigation && targetHasNavigation) ||
									(!sourceHasNavigation && !targetHasNavigation)){
								
								if (isConstructor){
		                            Element predfunc = (Element)templateNodes.getChild("Assignment").clone();
		                            predfunc.setAttribute("lhs", sourceObject.getChildText("name") + "." + targetEnd.getChild("rolename").getChildText("value"));
		                            Element identifier = (Element)templateNodes.getChild("Identifier").clone();
		                            identifier.setAttribute("name", targetObject.getChildText("name"));
		                            predfunc.addContent(identifier);
		                            containerXRMPLNode.addContent(predfunc);
		                            
		                            Element predfunc2 = (Element)templateNodes.getChild("Assignment").clone();
		                            predfunc2.setAttribute("lhs", targetObject.getChildText("name") + "." + sourceEnd.getChild("rolename").getChildText("value"));
		                            Element identifier2 = (Element)templateNodes.getChild("Identifier").clone();
		                            identifier2.setAttribute("name", sourceObject.getChildText("name"));
		                            predfunc2.addContent(identifier2);
		                            containerXRMPLNode.addContent(predfunc2);
								}
								else {
	                            	Element operator = new Element("Equals");
	                            	Element identifier1 = (Element)templateNodes.getChild("Identifier").clone();
	                            	identifier1.setAttribute("name", sourceObject.getChildText("name") + "." + targetEnd.getChild("rolename").getChildText("value"));
		                            Element identifier2 = (Element)templateNodes.getChild("Identifier").clone();
		                            identifier2.setAttribute("name", targetObject.getChildText("name"));
		                            operator.addContent(identifier1);
		                            operator.addContent(identifier2);
		                            containerXRMPLNode.addContent(operator);
		                            
	                            	operator = new Element("Equals");
	                            	identifier1 = (Element)templateNodes.getChild("Identifier").clone();
	                            	identifier1.setAttribute("name", targetObject.getChildText("name") + "." + sourceEnd.getChild("rolename").getChildText("value"));
		                            identifier2 = (Element)templateNodes.getChild("Identifier").clone();
		                            identifier2.setAttribute("name", sourceObject.getChildText("name"));
		                            operator.addContent(identifier1);
		                            operator.addContent(identifier2);
		                            containerXRMPLNode.addContent(operator);
								}
								
								
							}
							else{// Single navigation: the order is set by the navigation
								Element associationSource = (sourceHasNavigation) ?sourceObject :targetObject;
								Element associationTarget = (sourceHasNavigation) ?targetObject :sourceObject;
							
								if (isConstructor){
		                            Element predfunc = (Element)templateNodes.getChild("Assignment").clone();
		                            predfunc.setAttribute("lhs", associationTarget.getChildText("name") + "." + classAssociation.getChildText("name"));
		                            Element identifier = (Element)templateNodes.getChild("Identifier").clone();
		                            identifier.setAttribute("name", associationSource.getChildText("name"));
		                            predfunc.addContent(identifier);
		                            containerXRMPLNode.addContent(predfunc);
								}
								else {
	                            	Element operator = new Element("Equals");
	                            	Element identifier1 = (Element)templateNodes.getChild("Identifier").clone();
	                            	identifier1.setAttribute("name", associationTarget.getChildText("name") + "." + classAssociation.getChildText("name"));
		                            Element identifier2 = (Element)templateNodes.getChild("Identifier").clone();
		                            identifier2.setAttribute("name", associationSource.getChildText("name"));
		                            operator.addContent(identifier1);
		                            operator.addContent(identifier2);
		                            containerXRMPLNode.addContent(operator);
								}
							}									
						}
					}
				}
			}
			
			// Object ATTRIBUTES
			List<?> refObjects = objectDiagram.getChild("objects").getChildren("object");
			for (Iterator<?> iterator = refObjects.iterator(); iterator.hasNext();) {
				// get all object references in the diagram
				Element refObject = (Element) iterator.next();
				// get the corresponding object
				Element object = null;
				try {
					XPath path = new JDOMXPath("elements/objects/object[@id='"+ refObject.getAttributeValue("id") +"']");
					//object = (Element)path.selectSingleNode(problem);
					object = (Element)path.selectSingleNode(domain);							
				} catch (JaxenException e) {			
					e.printStackTrace();
				}
				if(object != null){
					// get the object attributes
					List<?> objectAttributes = refObject.getChild("attributes").getChildren("attribute");
					
					for (Iterator<?> attrIterator = objectAttributes.iterator(); attrIterator.hasNext();) {
						Element objectAttribute = (Element) attrIterator.next();
						// get the corresponding class attribute
						
						Element classAttribute = null;
						try {
							XPath path = new JDOMXPath("elements/classes/class[@id='"+ objectAttribute.getAttributeValue("class") +
									"']/attributes/attribute[@id='"+ objectAttribute.getAttributeValue("id") +"']");
							classAttribute = (Element)path.selectSingleNode(project);
						} catch (JaxenException e) {			
							e.printStackTrace();
						}								

						if(classAttribute != null && !classAttribute.getChildText("type").equals("4")){ //excluding the String case
							
							Element predfunc = null;
							
							if (isConstructor){
								predfunc = (Element)templateNodes.getChild("Assignment").clone();
								predfunc.setAttribute("lhs", object.getChildText("name") + "." + classAttribute.getChildText("name"));							
							}
							else{
								predfunc = new Element("Equals");
	                            Element identifier = (Element)templateNodes.getChild("Identifier").clone();
	                            identifier.setAttribute("name", object.getChildText("name") + "." + classAttribute.getChildText("name"));
	                            predfunc.addContent(identifier);
								
							}
							// this list will be used to add predicates or functions
							// in the parameterized attribute case
							List<Element> predOrFuncList = new ArrayList<Element>();
							
														
							//TODO: parameterized attribute
							if(objectAttribute.getChild("value").getChildren().size() > 0){
							}
                            // not parameterized attribute
							else{
								
								if(!objectAttribute.getChildText("value").trim().equals("")){
									// boolean attributes -> predicate
									if(classAttribute.getChildText("type").equals("1")){

										if(objectAttribute.getChildText("value").equals("true")){
											Element identifier = (Element)templateNodes.getChild("Identifier").clone();
				                            identifier.setAttribute("name", TRUE_VALUE);
				                            predfunc.addContent(identifier);
				                            containerXRMPLNode.addContent(predfunc);
										}
                                        //checking false value and only it if it in the init state
                                        else if(objectAttribute.getChildText("value").equals("false")){
											Element identifier = (Element)templateNodes.getChild("Identifier").clone();
				                            identifier.setAttribute("name", FALSE_VALUE);
				                            predfunc.addContent(identifier);
				                            containerXRMPLNode.addContent(predfunc);
										}
									}
									// numeric attributes -> function
									else if(classAttribute.getChildText("type").equals("2") ||
											classAttribute.getChildText("type").equals("3")){
										
										Element identifier = (Element)templateNodes.getChild("Identifier").clone();
			                            identifier.setAttribute("name", objectAttribute.getChildText("value"));
			                            predfunc.addContent(identifier);
			                            containerXRMPLNode.addContent(predfunc);
									}
									// string and non primitive attributes -> predicate
									else{
										Element identifier = (Element)templateNodes.getChild("Identifier").clone();
			                            identifier.setAttribute("name", objectAttribute.getChildText("value"));
			                            predfunc.addContent(identifier);
			                            containerXRMPLNode.addContent(predfunc);									
                                   }
								}
							}
						}
					}
				}
			}            
		}
	}
	
	
	

}
