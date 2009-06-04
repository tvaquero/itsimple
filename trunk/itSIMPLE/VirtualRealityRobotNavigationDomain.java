/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package itSIMPLE;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import languages.xml.XMLUtilities;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

/**
 *
 * @author designlab
 */
public class VirtualRealityRobotNavigationDomain {
    public static void generateBackgroundFile(Element problem, Element xmlPlan){
        
        if(problem != null){
            //create txt file for background generation

            Element domain = problem.getParentElement().getParentElement();        

            String bgDef = "\nStart_Dominio\n";

            //1. Grid
            //get class of grid object
            Element gridClass = null;
            try {
                    XPath path = new JDOMXPath("project/elements/classes/class[name='Grid']");
                    gridClass = (Element)path.selectSingleNode(problem.getDocument());
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            //get rows attribute
            Element rowsAttribute = null;
            try {
                    XPath path = new JDOMXPath("attributes/attribute[name='rows']");
                    rowsAttribute = (Element)path.selectSingleNode(gridClass);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            //get columns attribute
            Element columnsAttribute = null;
            try {
                    XPath path = new JDOMXPath("attributes/attribute[name='columns']");
                    columnsAttribute = (Element)path.selectSingleNode(gridClass);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            // get grid object
            Element gridObject = null;
            try {
                    XPath path = new JDOMXPath("elements/objects/object[class='"+ gridClass.getAttributeValue("id") +"']");
                    gridObject = (Element)path.selectSingleNode(domain);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            // get grid object reference
            Element gridObjectRef = null;
            try {
                    XPath path = new JDOMXPath("objectDiagrams/objectDiagram[sequenceReference='init']/objects/object[@id='"+ gridObject.getAttributeValue("id") +"']");
                    gridObjectRef = (Element)path.selectSingleNode(problem);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            // get rows attribute reference
            Element rowsAttributeRef = null;
            try {
                    XPath path = new JDOMXPath("attributes/attribute[@class='"+ 
                            gridClass.getAttributeValue("id") +"' and @id='"+ rowsAttribute.getAttributeValue("id") +"']");
                    rowsAttributeRef = (Element)path.selectSingleNode(gridObjectRef);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            } 

            // get columns attribute reference
            Element columnsAttributeRef = null;
            try {
                    XPath path = new JDOMXPath("attributes/attribute[@class='"+ 
                            gridClass.getAttributeValue("id") +"' and @id='"+ columnsAttribute.getAttributeValue("id") +"']");
                    columnsAttributeRef = (Element)path.selectSingleNode(gridObjectRef);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            // add to text
            bgDef += rowsAttributeRef.getChildText("value") + "," + columnsAttributeRef.getChildText("value") + "\nEnd_Dominio\n\n";


            //2.Obstacles
            bgDef += "Start_Obstaculo\n";

            // get spot class
            Element spotClass = null;
            try {
                    XPath path = new JDOMXPath("project/elements/classes/class[name='Spot']");
                    spotClass = (Element)path.selectSingleNode(problem.getDocument());
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }


            // get element class
            Element elementClass = null;
            try {
                    XPath path = new JDOMXPath("project/elements/classes/class[name='Element']");
                    elementClass = (Element)path.selectSingleNode(problem.getDocument());
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }       

            // get type class attribute
            Element typeAttribute = null;
            try {
                    XPath path = new JDOMXPath("attributes/attribute[name='type']");
                    typeAttribute = (Element)path.selectSingleNode(spotClass);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            // get px class attribute
            Element pxAttribute = null;
            try {
                    XPath path = new JDOMXPath("attributes/attribute[name='px']");
                    pxAttribute = (Element)path.selectSingleNode(elementClass);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            Element pyAttribute = null;
            try {
                    XPath path = new JDOMXPath("attributes/attribute[name='py']");
                    pyAttribute = (Element)path.selectSingleNode(elementClass);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            // get all obstacle objects
            List obstacles = null;
            try {
                    XPath path = new JDOMXPath("elements/objects/object[class='"+ spotClass.getAttributeValue("id") +"']");
                    obstacles = path.selectNodes(domain);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            for(Iterator iter = obstacles.iterator(); iter.hasNext();){
                Element obstacleObject = (Element)iter.next();

                // get obstacle object reference
                Element obstacleObjectRef = null;
                try {
                        XPath path = new JDOMXPath("objectDiagrams/objectDiagram[sequenceReference='init']/objects/object[@id='"+ obstacleObject.getAttributeValue("id") 
                                +"' and attributes/attribute/@class='"+ spotClass.getAttributeValue("id") +"' and attributes/attribute/@id='"
                                + typeAttribute.getAttributeValue("id") +"' and lower-case(attributes/attribute/value)!='terrain']");
                        obstacleObjectRef = (Element)path.selectSingleNode(problem);
                } catch (JaxenException e) {			
                        e.printStackTrace();
                }

                if(obstacleObjectRef != null){
                    // get type attribute reference
                    Element typeAttributeRef = null;
                    try {
                            XPath path = new JDOMXPath("attributes/attribute[@class='"+ 
                                    spotClass.getAttributeValue("id") +"' and @id='"+ typeAttribute.getAttributeValue("id") +"']");
                            typeAttributeRef = (Element)path.selectSingleNode(obstacleObjectRef);
                    } catch (JaxenException e) {			
                            e.printStackTrace();
                    }

                    // get px attribute reference
                    Element pxAttributeRef = null;
                    try {
                            XPath path = new JDOMXPath("attributes/attribute[@class='"+ 
                                    elementClass.getAttributeValue("id") +"' and @id='"+ pxAttribute.getAttributeValue("id") +"']");
                            pxAttributeRef = (Element)path.selectSingleNode(obstacleObjectRef);
                    } catch (JaxenException e) {			
                            e.printStackTrace();
                    }

                    // get py attribute reference
                    Element pyAttributeRef = null;
                    try {
                            XPath path = new JDOMXPath("attributes/attribute[@class='"+ 
                                    elementClass.getAttributeValue("id") +"' and @id='"+ pyAttribute.getAttributeValue("id") +"']");
                            pyAttributeRef = (Element)path.selectSingleNode(obstacleObjectRef);
                    } catch (JaxenException e) {			
                            e.printStackTrace();
                    }

                    bgDef += typeAttributeRef.getChildText("value").toUpperCase() + "," 
                            + pxAttributeRef.getChildText("value") + "," + pyAttributeRef.getChildText("value") + "\n";                
                }


            } 
            bgDef += "End_Obstaculo\n\n";

            // 3. Robots
            bgDef += "Start_Actor\n";

            // get obstacle class
            Element robotClass = null;
            try {
                    XPath path = new JDOMXPath("project/elements/classes/class[name='Robot']");
                    robotClass = (Element)path.selectSingleNode(problem.getDocument());
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            // get direction class attribute
            Element directionAttribute = null;
            try {
                    XPath path = new JDOMXPath("attributes/attribute[name='dir']");
                    directionAttribute = (Element)path.selectSingleNode(robotClass);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }

            // get all robot objects
            List robots = null;
            try {
                    XPath path = new JDOMXPath("elements/objects/object[class='"+ robotClass.getAttributeValue("id") +"']");
                    robots = path.selectNodes(domain);
            } catch (JaxenException e) {			
                    e.printStackTrace();
            }        

            for(Iterator iter = robots.iterator(); iter.hasNext();){
                Element robotObject = (Element)iter.next();

                // get obstacle object reference
                Element robotObjectRef = null;
                try {
                        XPath path = new JDOMXPath("objectDiagrams/objectDiagram[sequenceReference='init']/objects/object[@id='"+ robotObject.getAttributeValue("id") +"']");
                        robotObjectRef = (Element)path.selectSingleNode(problem);
                } catch (JaxenException e) {			
                        e.printStackTrace();
                }

                if(robotObjectRef != null){
                    // get type attribute reference
                    Element directionAttributeRef = null;
                    try {
                            XPath path = new JDOMXPath("attributes/attribute[@class='"+ 
                                    robotClass.getAttributeValue("id") +"' and @id='"+ directionAttribute.getAttributeValue("id") +"']");
                            directionAttributeRef = (Element)path.selectSingleNode(robotObjectRef);
                    } catch (JaxenException e) {			
                            e.printStackTrace();
                    }

                    // get px attribute reference
                    Element pxAttributeRef = null;
                    try {
                            XPath path = new JDOMXPath("attributes/attribute[@class='"+ 
                                    elementClass.getAttributeValue("id") +"' and @id='"+ pxAttribute.getAttributeValue("id") +"']");
                            pxAttributeRef = (Element)path.selectSingleNode(robotObjectRef);
                    } catch (JaxenException e) {			
                            e.printStackTrace();
                    }

                    // get py attribute reference
                    Element pyAttributeRef = null;
                    try {
                            XPath path = new JDOMXPath("attributes/attribute[@class='"+ 
                                    elementClass.getAttributeValue("id") +"' and @id='"+ pyAttribute.getAttributeValue("id") +"']");
                            pyAttributeRef = (Element)path.selectSingleNode(robotObjectRef);
                    } catch (JaxenException e) {			
                            e.printStackTrace();
                    }

                    bgDef += robotObject.getChildText("name").toUpperCase() + "," + pxAttributeRef.getChildText("value").toUpperCase() + "," 
                            + pyAttributeRef.getChildText("value") + "," + directionAttributeRef.getChildText("value").toUpperCase() + "\n";                 
                }
            }
            bgDef += "End_Actor\n";

            System.out.print(bgDef);
            
            try {
                    FileWriter domainFile = new FileWriter("resources/virtualreality/domain.txt");                    
                    domainFile.write(bgDef);                    
                    domainFile.close();                    
            } catch (IOException e) {
                    e.printStackTrace();
            }
        }
        if (xmlPlan != null){
            
            // create plan file           
            String plan = "";

            List<Element> actions = xmlPlan.getChild("plan").getChildren("action");
            for(Iterator<Element> iter = actions.iterator(); iter.hasNext();){
                Element action = iter.next();
                //index
                String startTime = action.getChildText("startTime");
                //SGPlan 5 displays it with a dot (.)
                String index;
                if(startTime.indexOf(".") > 0)
                    index = startTime.substring(0, startTime.indexOf("."));
                else
                    index = startTime;
                String actionId = action.getAttributeValue("id");
                // MIPS XXL changes "_" for "-"
                actionId = actionId.replaceAll("-", "_");
                String robotName = action.getChild("parameters").getChild("parameter").getAttributeValue("id");


                plan += index + ": " + actionId + " " + robotName + "\n";
            }

            //System.out.println(plan);
            
            // save the string to files
            try {
                    FileWriter planFile = new FileWriter("resources/virtualreality/plan.txt");                    
                    planFile.write(plan);                    
                    planFile.close();
            } catch (IOException e) {
                    e.printStackTrace();
            }            
        } 
        
    }
}
