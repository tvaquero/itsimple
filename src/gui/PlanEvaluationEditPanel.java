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
*
**/

package src.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;

import org.jdom.Element;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.text.DecimalFormat;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.ToolTipManager;

import src.planning.PlanAnalyzer;

public class PlanEvaluationEditPanel extends ItPanel 
		implements ItemListener, TableModelListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2719862281148936012L;
	

	private JTable metricsTable;
	private DefaultTableModel tableModel;

        private JTextField overallPlanEvaluationValue = null;
        private Element xmlPlan = null;
        private JLabel thecostLabel = null;
        private JButton reEvaluateButton = null;

	
	//@SuppressWarnings("unchecked")
	public PlanEvaluationEditPanel(Element thePlan){
		super();
                this.xmlPlan = thePlan;
                initialize();
	}

	private void initialize(){
                // Get current delay
                //int initialDelay = ToolTipManager.sharedInstance().getInitialDelay();
                //System.out.print(initialDelay);

                // Show tool tips immediately for the information icons
                ToolTipManager.sharedInstance().setInitialDelay(0);
		
		setLayout(new BorderLayout());


                
                ItFramePanel metricsFramePanel = new ItFramePanel("Metrics evaluation", ItFramePanel.NO_MINIMIZE_MAXIMIZE);

                ItFramePanel planFramePanel = new ItFramePanel("Plan evaluation", ItFramePanel.NO_MINIMIZE_MAXIMIZE);



                JPanel metricsEvaluationPanel = new JPanel(new BorderLayout());

		FormLayout layout = new FormLayout(
				"pref, 4px", // columns
				"pref, 4px"); // rows
                JPanel topPanel = new JPanel(layout);
		CellConstraints cc = new CellConstraints();
		topPanel.add(new JLabel("Specified metrics:"), cc.xy (1, 1));
                metricsEvaluationPanel.add(topPanel, BorderLayout.NORTH);

	
		// create parameters table		
		JScrollPane scrollMetriccPane = new JScrollPane(getMetricsTable());
		JPanel metricsPane = new JPanel(new BorderLayout());
		metricsPane.add(scrollMetriccPane, BorderLayout.CENTER);
		metricsPane.setPreferredSize(new Dimension(600, 210));
                metricsEvaluationPanel.add(metricsPane, BorderLayout.CENTER);



		//cost and overall plan evaluation panel
                JPanel planEvaluationPanel = new JPanel(new BorderLayout());
                
                FormLayout layoutcost = new FormLayout(
                                "pref, 4px, 100px, 4px, pref", // columns
                                "4px, pref, 4px, pref"); // rows
                JPanel costoverallPanel = new JPanel(layoutcost);
                JLabel costLabel = new JLabel("<html><strong>Plan cost:</strong></html>");
                thecostLabel = new JLabel();
                //plan overall evaluation
                JLabel evaluationLabel = new JLabel("<html><strong>Plan evaluation:</strong></html>");
                overallPlanEvaluationValue = new JTextField(30);
                JTextFieldFilter filter = new JTextFieldFilter(JTextFieldFilter.FLOAT);
                filter.setNegativeAccepted(false);
                //filter.setLimit(3);
                overallPlanEvaluationValue.setDocument(filter);
                overallPlanEvaluationValue.setColumns(9);
                overallPlanEvaluationValue.addKeyListener(this);

                //reEvaluateButton = new JButton("Recalculate", new ImageIcon("resources/images/refresh2.png"));
                reEvaluateButton = new JButton(new ImageIcon("resources/images/refresh2.png"));
                reEvaluateButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                            calculatePlanEvaluation();
			}
		});
                reEvaluateButton.setToolTipText("<html>Recalculate plan evaluation <br>(recommended when metric evaluations were modified)</html>");

                setCostAndEvaluation();



                CellConstraints c = new CellConstraints();
                costoverallPanel.add(costLabel, c.xy (1, 2));
                costoverallPanel.add(thecostLabel, c.xy(3, 2));
                costoverallPanel.add(evaluationLabel, c.xy(1, 4));
                costoverallPanel.add(overallPlanEvaluationValue, c.xy(3, 4));
                costoverallPanel.add(reEvaluateButton, c.xy(5, 4));
                planEvaluationPanel.add(costoverallPanel, BorderLayout.CENTER);


                
                metricsFramePanel.setContent(metricsEvaluationPanel, false);
                planFramePanel.setContent(planEvaluationPanel, false);

                add(metricsFramePanel, BorderLayout.CENTER);
                add(planFramePanel, BorderLayout.SOUTH);


	
	}

	

	private JTable getMetricsTable(){

		tableModel = new DefaultTableModel();
		metricsTable = new JTable(tableModel){
                        @Override
                        public boolean isCellEditable(int row, int column) {
                            //avoid columns that can not edit (leaving only the evaluation to edit)
                            if(column < 4){
                                return false;

                            }else{
                                return true;
                            }
                        }
                };
                
		tableModel.addTableModelListener(this);
		
		tableModel.addColumn("#");
		tableModel.addColumn("Metric");
		tableModel.addColumn("Weight");
		tableModel.addColumn("Value");
		tableModel.addColumn("Score");

		metricsTable.getColumnModel().getColumn(0).setMaxWidth(25);
		metricsTable.getColumnModel().getColumn(2).setMinWidth(30);
                metricsTable.getColumnModel().getColumn(2).setMaxWidth(70);
		metricsTable.getColumnModel().getColumn(3).setMinWidth(100);
		metricsTable.getColumnModel().getColumn(3).setMaxWidth(200);
                metricsTable.getColumnModel().getColumn(4).setMinWidth(90);
		metricsTable.getColumnModel().getColumn(4).setMaxWidth(150);

                if (xmlPlan != null){
                    setMetricsTable();
                }
		
		// create true, false combo box
//		JComboBox enableCombo = new JComboBox();
//		enableCombo.addItem("true");
//		enableCombo.addItem("false");
//		metricsTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(enableCombo));
		
		return metricsTable;
	}


	/**
         * Fill out metrics table
         */
	//@SuppressWarnings("unchecked")
	private void setMetricsTable(){
		int argIndex = 1;
                
                //XMLUtilities.printXML(xmlPlan);
                Element metrics = xmlPlan.getChild("metrics");
                
                if (metrics != null){
                    
                    for (Iterator<Element> iterator = metrics.getChildren().iterator(); 
			iterator.hasNext();) {
			Element eachQualityMetric = iterator.next();
			Vector<String> rowData = new Vector<String>();
			
			rowData.add(String.valueOf(argIndex++));
			rowData.add(eachQualityMetric.getChildText("name"));
			rowData.add(eachQualityMetric.getChildText("weight"));

                        Element value = eachQualityMetric.getChild("value");
                        Element evaluation = eachQualityMetric.getChild("evaluation");
                        if (value==null){
                            System.out.println("entrei");
                            value = new Element("value");
                            eachQualityMetric.addContent(value);
                            value.setText(Float.toString(PlanAnalyzer.getMetricValue(eachQualityMetric)));
                        }
                        if (evaluation==null){
                            evaluation = new Element("evaluation");
                            evaluation.setAttribute("value", Double.toString(PlanAnalyzer.evaluateMetric(eachQualityMetric)));
                            eachQualityMetric.addContent(evaluation);
                        }

                        rowData.add(eachQualityMetric.getChildText("value"));
			rowData.add(eachQualityMetric.getChild("evaluation").getAttributeValue("value"));

			//rowData.add(eachQualityMetric.getChildText("enable"));
			//rowData.add(eachQualityMetric.getChildText("value"));
			
			tableModel.addRow(rowData);
                    }
                    
                }

	}

        /**
         * This method set the cost and plan evaluation values
         */
        private void setCostAndEvaluation(){
            if (xmlPlan!= null){
                //set cost
                double thecost = PlanAnalyzer.evaluateCostAward(xmlPlan.getChild("metrics"));
                thecostLabel.setText(Double.toString(thecost));

                //plan evaluation
                Element planEvaluation = xmlPlan.getChild("evaluation");
                if (planEvaluation!=null){
                    overallPlanEvaluationValue.setText(planEvaluation.getAttributeValue("value"));
                }

            }else{
                overallPlanEvaluationValue.setEnabled(false);
                reEvaluateButton.setEnabled(false);
            }

        }


/**
         * This method set computes the plan evaluation value
         */
        private void calculatePlanEvaluation(){
            if (xmlPlan!= null){
                //set cost
                double theEvaluation = PlanAnalyzer.reEvaluatePlan(xmlPlan);
                DecimalFormat theformat = new DecimalFormat("0.00");
                String evaluationString = theformat.format(theEvaluation);
                //plan evaluation
                Element planEvaluation = xmlPlan.getChild("evaluation");
                if (planEvaluation!=null){
                    planEvaluation.setAttribute("value", evaluationString);
                    overallPlanEvaluationValue.setText(evaluationString);
                }
            }
        }


	@Override
	public void tableChanged(TableModelEvent e) {
		if(e.getSource() == tableModel){
			int row = e.getFirstRow();
			int col = e.getColumn();			
			
			if (row > -1 && col > -1) {
				String value = (String) tableModel.getValueAt(row, col);
				//Element argument = (Element) selectedPlanner.getChild("settings").getChild("arguments").getChildren().get(row);
                                Element metric = (Element) xmlPlan.getChild("metrics").getChildren().get(row);
				switch (col) {
				case 1://metric name
					metric.getChild("name").setText(value);
					break;
				case 2://weight
					metric.getChild("weight").setText(value);
					break;
				case 3://value
					metric.getChild("value").setText(value);
					break;
				case 4://evaluation score
					metric.getChild("evaluation").setAttribute("value", value);
				}
			}
		}
		
	}


        public void keyTyped(KeyEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void keyPressed(KeyEvent e) {
            //throw new UnsupportedOperationException("Not supported yet.");
        }

        public void keyReleased(KeyEvent e) {
            if(e.getSource() == overallPlanEvaluationValue){
                //set plan evaluation
                if (xmlPlan != null){
                    xmlPlan.getChild("evaluation").setAttribute("value", overallPlanEvaluationValue.getText());
                }
            }
        }

    public void itemStateChanged(ItemEvent e) {
        throw new UnsupportedOperationException("Not supported yet.");
    }




}
