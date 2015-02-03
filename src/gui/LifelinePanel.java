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
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import src.languages.xml.XMLUtilities;

/**
 *
 * @author tiago
 */
public class LifelinePanel extends JPanel {

	private Element lifeline = null;
    private Element diagram = null;
	private Element project = null;
    private JFreeChart chart = null;
    private TimingDiagramPanel timingDiagramPanel = null;
    private JPanel buttonPanel = null;
    private String lifelineName = "";
    private double timingRulerAdditional = 0.2;




	public LifelinePanel(Element lifeline, Element timingDiagram, Element project, TimingDiagramPanel parent) {
        super();
        this.setLayout(new BorderLayout());
        this.setBackground(Color.WHITE);

        this.lifeline = lifeline;
	    this.diagram = timingDiagram;
        this.project = project;
        this.timingDiagramPanel = parent;
        buildLifeLine();
        buildBottonToolBar();

	    /*Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	    setLayout(null);*/
	}

    /**
     * this method created and set the graph for showing the timing diagram based
     * on the variable diagram
     */
    public void buildLifeLine(){

        //1. get type and context
        String dtype = diagram.getChildText("type");
        String context = diagram.getChildText("context");
        //the frame and lifeline nodes
        Element frame = diagram.getChild("frame");
        String durationStr = frame.getChildText("duration");
        lifelineName = "";
        String objectClassName ="";
        String yAxisName = "";

        float lastIntervalDuration = 0;



        //condition lifeline
        if (dtype.equals("condition")){

            //check if the context is a action
            if (context.equals("action")){

                //get action/operator
                Element operatorRef = diagram.getChild("action");
                Element operator = null;
                try {
                    XPath path = new JDOMXPath("elements/classes/class[@id='"+operatorRef.getAttributeValue("class")+"']/operators/operator[@id='"+ operatorRef.getAttributeValue("id") +"']");
                    operator = (Element)path.selectSingleNode(project);
                } catch (JaxenException e2) {
                    e2.printStackTrace();
                }

                if (operator !=null){
                    // System.out.println(operator.getChildText("name"));
                    //System.out.println("Life line id "+ lifeline.getAttributeValue("id"));

                    //get the object (can be a parametr. literal, or object)
                    Element objRef = lifeline.getChild("object");
                    Element attrRef = lifeline.getChild("attribute");

                    //get object class
                    Element objClass = null;
                    try {
                        XPath path = new JDOMXPath("elements/classes/class[@id='"+objRef.getAttributeValue("class")+"']");
                        objClass = (Element)path.selectSingleNode(project);
                    } catch (JaxenException e2) {
                        e2.printStackTrace();
                    }

                    Element attribute = null;
                    try {
                        XPath path = new JDOMXPath("elements/classes/class[@id='"+attrRef .getAttributeValue("class")+"']/attributes/attribute[@id='"+ attrRef.getAttributeValue("id") +"']");
                        attribute = (Element)path.selectSingleNode(project);
                    } catch (JaxenException e2) {
                        e2.printStackTrace();
                    }

                    yAxisName = attribute.getChildText("name");

                    //if (objClass!=null)
                    Element object = null;

                    //check what is this object (parameterof an action, object, literal)
                    if (objRef.getAttributeValue("element").equals("parameter")){
                        //get parameter in the action

                        try {
                            XPath path = new JDOMXPath("parameters/parameter[@id='"+objRef.getAttributeValue("id")+"']");
                            object = (Element)path.selectSingleNode(operator);
                        } catch (JaxenException e2) {
                            e2.printStackTrace();
                        }
                        String parameterStr = object.getChildText("name");

                        lifelineName = parameterStr + ":" + objClass.getChildText("name");
                        objectClassName = parameterStr + ":" + objClass.getChildText("name");
                    }
                    //

                    //set suround border
                    Border etchedBdr = BorderFactory.createEtchedBorder();
                    Border titledBdr = BorderFactory.createTitledBorder(etchedBdr, "lifeline("+ lifelineName+")");
                    //Border titledBdr = BorderFactory.createTitledBorder(etchedBdr, "");
                    Border emptyBdr  = BorderFactory.createEmptyBorder(10,10,10,10);
                    Border compoundBdr=BorderFactory.createCompoundBorder(titledBdr, emptyBdr);
                    this.setBorder(compoundBdr);


                    //Boolean attribute
                    if (attribute.getChildText("type").equals("1")){
                        lifelineName += " - " + attribute.getChildText("name");

                        Element timeIntervals = lifeline.getChild("timeIntervals");


                        XYSeriesCollection dataset = new XYSeriesCollection();
                        XYSeries series = new XYSeries("Boolean");
                        for (Iterator<Element> it1 = timeIntervals.getChildren().iterator(); it1.hasNext();) {
                            Element timeInterval = it1.next();
                            boolean insertPoint = true;

                            Element durationConstratint = timeInterval.getChild("durationConstratint");
                            Element lowerbound = durationConstratint.getChild("lowerbound");
                            Element upperbound = durationConstratint.getChild("upperbound");
                            Element value = timeInterval.getChild("value");

                            //Add for both lower and upper bound

                            //lower bound
                            float lowerTimePoint = 0;
                            try {
                                 lowerTimePoint = Float.parseFloat(lowerbound.getAttributeValue("value"));
                                 lastIntervalDuration = lowerTimePoint;
                            } catch (Exception e) {
                                insertPoint = false;
                            }
                            //System.out.println("    > point     x= "+ Float.toString(lowerTimePoint)+ " ,  y= "+ lowerbound.getAttributeValue("value"));
                            if (insertPoint){
                                series.add(lowerTimePoint, (value.getText().equals("false") ?0 :1));
                            }

                            //upper bound
                            float upperTimePoint = 0;
                            try {
                                 upperTimePoint = Float.parseFloat(upperbound.getAttributeValue("value"));
                                 lastIntervalDuration = upperTimePoint;
                            } catch (Exception e) {
                                insertPoint = false;
                            }
                            //System.out.println("    > point     x= "+ Float.toString(upperTimePoint)+ " ,  y= "+ lowerbound.getAttributeValue("value"));
                            if (insertPoint && upperTimePoint != lowerTimePoint){
                                series.add(upperTimePoint, (value.getText().equals("false") ?0 :1));
                            }

                        }
                        dataset.addSeries(series);

                        //chart = ChartFactory.createXYStepChart(lifelineName, "time", "value", dataset, PlotOrientation.VERTICAL, false, true, false);
                        chart = ChartFactory.createXYStepChart(attribute.getChildText("name"), "time", "value", dataset, PlotOrientation.VERTICAL, false, true, false);
                        chart.setBackgroundPaint(Color.WHITE);

                        XYPlot plot = (XYPlot)chart.getPlot();
                        plot.setBackgroundPaint(Color.WHITE);


                        NumberAxis domainAxis = new NumberAxis("Time");
                        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                        domainAxis.setAutoRangeIncludesZero(false);

                        //set timing ruler
                        if (durationStr.trim().equals("")){
                            if (lastIntervalDuration > 0)
                                domainAxis.setUpperBound(lastIntervalDuration+timingRulerAdditional);
                            else
                                domainAxis.setUpperBound(10.0);                            
                        }
                        else{
                            try {
                                float dur = Float.parseFloat(durationStr);
                                if (dur >= lastIntervalDuration){
                                    domainAxis.setUpperBound(dur+timingRulerAdditional);
                                }
                                else{
                                    domainAxis.setUpperBound(lastIntervalDuration+timingRulerAdditional);
                                }
                            } catch (Exception e) {
                                if (lastIntervalDuration > 0)
                                    domainAxis.setUpperBound(lastIntervalDuration+timingRulerAdditional);
                                else
                                    domainAxis.setUpperBound(10.0);
                            }

                        }


                        plot.setDomainAxis(domainAxis);

                        String[] values = {"false", "true"};
                        //SymbolAxis rangeAxis = new SymbolAxis("Values", values);
                        SymbolAxis rangeAxis = new SymbolAxis(yAxisName, values);
                        plot.setRangeAxis(rangeAxis);

                        ChartPanel chartPanel = new ChartPanel(chart);
                        chartPanel.setPreferredSize(new Dimension(chartPanel.getSize().width, 175));

                        JLabel title = new JLabel("<html><b><u>"+objectClassName+"</u></b></html>");
                        title.setBackground(Color.WHITE);

                        this.add(title, BorderLayout.WEST);
                        this.add(chartPanel, BorderLayout.CENTER);

                    }


                }


            }
            //if this is a possible sequence of action being modeled to a condition
            else if (context.equals("general")){

            }


        }
        else if (dtype.equals("state")){




        }


    }

    public String getLifelineName() {
        return lifelineName;
    }


    public void buildBottonToolBar() {

        buttonPanel = new JPanel(new BorderLayout());
        buttonPanel.setBackground(Color.WHITE);

        //JLabel test = new JLabel("<html><a href=\"http://www.google.com\">delete</a></html>");
        JPanel leftpanel = new JPanel(new BorderLayout());
        leftpanel.setBackground(Color.WHITE);


        JButton button = new JButton(editLifeLine);
        button.setToolTipText("Edit lifeline");
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        leftpanel.add(button, BorderLayout.WEST);
        button.setRolloverEnabled(true);

        button = new JButton(deleteLifeLine);
        button.setToolTipText("Delete lifeline");
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        leftpanel.add(button, BorderLayout.EAST);
        button.setRolloverEnabled(true);


        //JLabel editlabel = new JLabel("<html><b><u>edit</u></b> </html>");
        //leftpanel.add(editlabel, BorderLayout.WEST);
        //JLabel deletelabel = new JLabel("<html><b><u>delete</u></b></html>");
        //leftpanel.add(deletelabel, BorderLayout.EAST);


        buttonPanel.add(leftpanel, BorderLayout.EAST);
        this.add(buttonPanel, BorderLayout.SOUTH);
        
    }


    public void setDiagram(Element diagram) {
        this.diagram = diagram;
    }

    public Element getLifeline() {
        return lifeline;
    }



    //ACTIONS

	private Action deleteLifeLine = new AbstractAction("delete", new ImageIcon("resources/images/delete.png")){
		/**
		 *
		 */
		private static final long serialVersionUID = -7885426542495703978L;

		public void actionPerformed(ActionEvent e) {
            timingDiagramPanel.removeLifeline(LifelinePanel.this);
		}


	};


    /**
     * Gets the fileline Element
     * @return
     */
    public Element getData() {
        return lifeline;
    }

	private Action editLifeLine = new AbstractAction("edit", new ImageIcon("resources/images/edit.png")){
		/**
		 *
		 */
		private static final long serialVersionUID = 663096847536402295L;

		public void actionPerformed(ActionEvent e) {

            EditLifelineDialog dialog = new EditLifelineDialog(diagram, lifeline, LifelinePanel.this);
            dialog.setVisible(true);

		}
	};


    public void refreshLifeline() {
        //1. get type and context
        String dtype = diagram.getChildText("type");
        String context = diagram.getChildText("context");
        //the frame and lifeline nodes
        Element frame = diagram.getChild("frame");
        String durationStr = frame.getChildText("duration");

        float lastIntervalDuration = 0;

        //condition lifeline
        if (dtype.equals("condition")){

            //check if the context is a action
            if (context.equals("action")){

                //get action/operator
                Element operatorRef = diagram.getChild("action");
                Element operator = null;
                try {
                    XPath path = new JDOMXPath("elements/classes/class[@id='"+operatorRef.getAttributeValue("class")+"']/operators/operator[@id='"+ operatorRef.getAttributeValue("id") +"']");
                    operator = (Element)path.selectSingleNode(project);
                } catch (JaxenException e2) {
                    e2.printStackTrace();
                }

                if (operator !=null){

                    //get the object (can be a parametr. literal, or object)
                    Element objRef = lifeline.getChild("object");
                    Element attrRef = lifeline.getChild("attribute");

                    //get object class
                    Element objClass = null;
                    try {
                        XPath path = new JDOMXPath("elements/classes/class[@id='"+objRef.getAttributeValue("class")+"']");
                        objClass = (Element)path.selectSingleNode(project);
                    } catch (JaxenException e2) {
                        e2.printStackTrace();
                    }

                    Element attribute = null;
                    try {
                        XPath path = new JDOMXPath("elements/classes/class[@id='"+attrRef .getAttributeValue("class")+"']/attributes/attribute[@id='"+ attrRef.getAttributeValue("id") +"']");
                        attribute = (Element)path.selectSingleNode(project);
                    } catch (JaxenException e2) {
                        e2.printStackTrace();
                    }


                    //if (objClass!=null)
                    Element object = null;

                    //check what is this object (parameterof an action, object, literal)
                    if (objRef.getAttributeValue("element").equals("parameter")){
                        //get parameter in the action

                        try {
                            XPath path = new JDOMXPath("parameters/parameter[@id='"+objRef.getAttributeValue("id")+"']");
                            object = (Element)path.selectSingleNode(operator);
                        } catch (JaxenException e2) {
                            e2.printStackTrace();
                        }
                    }


                    //Boolean attribute
                    if (attribute.getChildText("type").equals("1")){

                        Element timeIntervals = lifeline.getChild("timeIntervals");

                        XYSeriesCollection dataset = new XYSeriesCollection();
                        XYSeries series = new XYSeries("Boolean");
                        for (Iterator<Element> it1 = timeIntervals.getChildren().iterator(); it1.hasNext();) {
                            Element timeInterval = it1.next();
                            boolean insertPoint = true;

                            Element durationConstratint = timeInterval.getChild("durationConstratint");
                            Element lowerbound = durationConstratint.getChild("lowerbound");
                            Element upperbound = durationConstratint.getChild("upperbound");
                            Element value = timeInterval.getChild("value");

                            //Add for both lower and upper bound

                            //lower bound
                            float lowerTimePoint = 0;
                            try {
                                 lowerTimePoint = Float.parseFloat(lowerbound.getAttributeValue("value"));
                                 lastIntervalDuration = lowerTimePoint;
                            } catch (Exception e) {
                                insertPoint = false;
                            }
                            //System.out.println("    > point     x= "+ Float.toString(lowerTimePoint)+ " ,  y= "+ lowerbound.getAttributeValue("value"));
                            if (insertPoint){
                                series.add(lowerTimePoint, (value.getText().equals("false") ?0 :1));
                            }

                            //upper bound
                            float upperTimePoint = 0;
                            try {
                                 upperTimePoint = Float.parseFloat(upperbound.getAttributeValue("value"));
                                 lastIntervalDuration = upperTimePoint;
                            } catch (Exception e) {
                                insertPoint = false;
                            }
                            //System.out.println("    > point     x= "+ Float.toString(upperTimePoint)+ " ,  y= "+ lowerbound.getAttributeValue("value"));
                            if (insertPoint && upperTimePoint != lowerTimePoint){
                                series.add(upperTimePoint, (value.getText().equals("false") ?0 :1));
                            }

                        }
                        dataset.addSeries(series);

                        chart.getXYPlot().setDataset(dataset);
                        XYPlot plot = chart.getXYPlot();

                        //chart = ChartFactory.createXYStepChart(lifelineName, "time", "value", dataset, PlotOrientation.VERTICAL, false, true, false);
                        //chart = ChartFactory.createXYStepChart(attribute.getChildText("name"), "time", "value", dataset, PlotOrientation.VERTICAL, false, true, false);
                        //chart.setBackgroundPaint(Color.WHITE);

                        //XYPlot plot = (XYPlot)chart.getPlot();

                        NumberAxis domainAxis = new NumberAxis("Time");
                        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
                        domainAxis.setAutoRangeIncludesZero(false);

                        //set timing ruler
                        if (durationStr.trim().equals("")){
                            if (lastIntervalDuration > 0)
                                domainAxis.setUpperBound(lastIntervalDuration+timingRulerAdditional);
                            else
                                domainAxis.setUpperBound(10.0);
                        }
                        else{
                            try {
                                float dur = Float.parseFloat(durationStr);
                                if (dur >= lastIntervalDuration){
                                    domainAxis.setUpperBound(dur+timingRulerAdditional);
                                }
                                else{
                                    domainAxis.setUpperBound(lastIntervalDuration+timingRulerAdditional);
                                }
                            } catch (Exception e) {
                                if (lastIntervalDuration > 0)
                                    domainAxis.setUpperBound(lastIntervalDuration+timingRulerAdditional);
                                else
                                    domainAxis.setUpperBound(10.0);
                            }

                        }


                        plot.setDomainAxis(domainAxis);

                    }


                }


            }
            //if this is a possible sequence of action being modeled to a condition
            else if (context.equals("general")){

            }


        }
        else if (dtype.equals("state")){




        }


    }




}
