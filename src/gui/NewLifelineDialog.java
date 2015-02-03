/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007,2008,2009 Universidade de Sao Paulo
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
* Authors:	Tiago S. Vaquero
**/

package src.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SpringLayout;
import org.jdom.Element;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;

import src.languages.xml.XMLUtilities;

/**
 *
 * @author tiago
 */
public class NewLifelineDialog extends JDialog{

    private Element lifeline = null;
    private Element diagram = null;
    private static TimingDiagramPanel parent = null;

    private JPanel mainPanel = null;

    //condition of action case
    private JComboBox parametersComboBox = null;
    private JComboBox attributesComboBox = null;
    private List<Element> parametersList = null;
    private List<Element> attributesList = null;


    @SuppressWarnings("static-access")
    public NewLifelineDialog(Element timingDiagram, Element newlifeline, TimingDiagramPanel parent) {
		super(ItSIMPLE.getItSIMPLEFrame());
		setTitle("New lifeline");
		setModal(true);

        this.lifeline = newlifeline;
        this.diagram = timingDiagram;
        this.parent = parent;


		this.setSize(300,180);
		this.setLocation(280,200);
		this.add(getMainPanel(), BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        //The Cancel button at the bottom
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                dispose();
            }
		});
		buttonPanel.add(cancelButton);

        //The OK button at the bottom
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                boolean canCreate = true;
                String dtype = diagram.getChildText("type");
                String context = diagram.getChildText("context");

                if (dtype.equals("condition")){
                    if (context.equals("action")){
                        if (parametersComboBox.getSelectedIndex() < 1 || attributesComboBox.getSelectedIndex() < 1)
                            canCreate = false;

                    }
                }

                if (canCreate){
                    NewLifelineDialog.parent.addLifeline(lifeline);
                    dispose();
                }
                
            }
		});
		buttonPanel.add(okButton);

        

        this.add(buttonPanel, BorderLayout.SOUTH);


    }

    private JPanel getMainPanel() {


        if (mainPanel == null){
            mainPanel = new JPanel();

            //build components

            //1. get type and context
            String dtype = diagram.getChildText("type");
            String context = diagram.getChildText("context");

            if (dtype.equals("condition")){
                if (context.equals("action")){
                    //we need the object (action's parameter and the its attribute)

                    JPanel topPanel = new JPanel(new SpringLayout());
                    topPanel.setPreferredSize(new Dimension(300,90));
                    //1. add action's parameters list to be selected
                    topPanel.add(new JLabel("Action:"));

                    JLabel actionNameLabel = new JLabel();

                    parametersComboBox = new JComboBox();
                    attributesComboBox = new JComboBox();
                    parametersList = new ArrayList<Element>();
                    attributesList = new ArrayList<Element>();
                    //get action/operator
                    Element operatorRef = diagram.getChild("action");
                    Element operator = null;
                    try {
                        XPath path = new JDOMXPath("elements/classes/class[@id='"+operatorRef.getAttributeValue("class")+"']/operators/operator[@id='"+ operatorRef.getAttributeValue("id") +"']");
                        operator = (Element)path.selectSingleNode(diagram.getDocument().getRootElement());
                    } catch (JaxenException e2) {
                        e2.printStackTrace();
                    }
                    if (operator !=null){
                        List<Element> parameters = null;
                        try {
                            XPath path = new JDOMXPath("parameters/parameter");
                            parameters = path.selectNodes(operator);
                        } catch (JaxenException e2) {
                            e2.printStackTrace();
                        }
                        if (parameters !=null && parameters.size()>0){
                            parametersComboBox.addItem("");
                            parametersList.add(null);

                            for (Iterator<Element> it = parameters.iterator(); it.hasNext();) {
                                Element element = it.next();
                                parametersList.add(element);
                                parametersComboBox.addItem(element.getChildText("name"));
                            }
                        }
                        actionNameLabel.setText(operator.getChildText("name"));
                    }

                    topPanel.add(actionNameLabel);

                    //1. fill out parameters combobox

                    topPanel.add(new JLabel("Parameter:"));
                    parametersComboBox.addItemListener(new ItemListener(){
                        public void itemStateChanged(ItemEvent e) {
                            Element selected = null;
                            //System.out.println(parametersComboBox.getSelectedIndex());
                            if (parametersComboBox.getSelectedIndex() > -1){
                                selected = (Element)parametersList.get(parametersComboBox.getSelectedIndex());
                            }
                            //System.out.println(selected);
                            if(selected!=null){
                                //set properties of the selected object (a parameter of an action
                                //XMLUtilities.printXML(selected);
                                //XMLUtilities.printXML(lifeline);
                                Element obj = lifeline.getChild("object");
                                obj.setAttribute("class", selected.getChildText("type"));
                                obj.setAttribute("element", "parameter");
                                obj.setAttribute("id", selected.getAttributeValue("id"));

                                //fill out object attributes
                                Element objCla = null;
                                try {
                                    XPath path = new JDOMXPath("elements/classes/class[@id='"+selected.getChildText("type")+"']");
                                    objCla = (Element)path.selectSingleNode(diagram.getDocument().getRootElement());
                                } catch (JaxenException e2) {
                                    e2.printStackTrace();
                                }
                                if (objCla!=null){
                                    //System.out.println(objCla);
                                    // get the parent classes
                                    List<?> parents = XMLUtilities.getClassAscendents(objCla);

                                    // prepares a list of attributes node
                                    List<Element> theattributes = new ArrayList<Element>();
                                    //get attributes from the objclass
                                    for (Iterator<?> iterator = objCla.getChild("attributes").getChildren("attribute").iterator();
                                            iterator.hasNext();) {
                                        Element attribute = (Element) iterator.next();
                                        theattributes.add(attribute);
                                    }
                                    //get attributes from ascendent classes
                                    for (Iterator<?> iterator = parents.iterator(); iterator.hasNext();) {
                                        Element parentClass = (Element) iterator.next();
                                        for (Iterator<?> iter2 = parentClass.getChild("attributes").getChildren("attribute").iterator();
                                                iter2.hasNext();) {
                                            Element attribute = (Element) iter2.next();
                                            theattributes.add(attribute);
                                        }
                                    }

                                    // only add attributes
                                    attributesComboBox.removeAllItems();
                                    attributesList.clear();
                                    attributesComboBox.addItem("");
                                    attributesList.add(null);
                                    if(theattributes.size() > 0){
                                        for (Iterator<Element> iterator = theattributes.iterator(); iterator.hasNext();) {
                                            Element attributeNode = iterator.next();
                                            //TODO: do it for all types of attributes. We restricted it for boolean only
                                            if (attributeNode.getChildText("type").equals("1")){
                                                attributesList.add(attributeNode);
                                                attributesComboBox.addItem(attributeNode.getChildText("name"));
                                            }
   
                                        }
                                    }

                                }



                            }
                            else{//if it is null clear attribute combobox
                                attributesComboBox.removeAllItems();
                                attributesList.clear();
                                attributesComboBox.addItem("");
                                attributesList.add(null);
                            }


                        }
                    });
                    topPanel.add(parametersComboBox);

                    
                    topPanel.add(new JLabel("Attribute:"));
                    attributesComboBox.addItemListener(new ItemListener(){
                        public void itemStateChanged(ItemEvent e) {
                            Element selected = null;
                             //System.out.println(attributesComboBox.getSelectedIndex());
                            if(attributesComboBox.getSelectedIndex() > -1 && attributesList.size() > 0) {
                                selected = attributesList.get(attributesComboBox.getSelectedIndex());
                            }
                            if (selected!=null){
                                //System.out.println(selected);
                                //set properties of the selected object (a parameter of an action
                                Element attr = lifeline.getChild("attribute");
                                attr.setAttribute("class", selected.getParentElement().getParentElement().getAttributeValue("id"));
                                attr.setAttribute("id", selected.getAttributeValue("id"));
                            }                          
                        }
                    });
                    attributesComboBox.addItem("");
                    attributesList.add(null);
                    topPanel.add(attributesComboBox);

                    SpringUtilities.makeCompactGrid(topPanel, 3, 2, 5, 5, 5, 5);

                    mainPanel.add(topPanel, BorderLayout.CENTER);





                    //2. add parameter's attribute list (can be restricted to boolean only)



                }
            }
            else if (dtype.equals("state")){

            }


        }
		return mainPanel;
        //

    }







}
