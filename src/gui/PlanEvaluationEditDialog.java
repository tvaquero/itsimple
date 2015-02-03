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
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.ToolTipManager;
import org.jdom.Element;
import src.languages.xml.XMLUtilities;
import src.planning.PlanAnalyzer;

public class PlanEvaluationEditDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2828980904853019592L;
	
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        private Element xmlPlan = null;

	private JTabbedPane mainTabbedPane = null;
	

	public PlanEvaluationEditDialog(Frame itSIMPLEFrame, final Element xmlPlan) {
		super(itSIMPLEFrame);

                this.xmlPlan = xmlPlan;
		setTitle("Edit Plan Evaluation");
		
		int w = 750;
		int h = 550;
		
		setBounds(screenSize.width/2-w/2, screenSize.height/2-h/2, w, h);
		
		setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
		setResizable(true);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                                // Show tool tips after a second (since we change for immediately in the PlannersSettingsPanel)
                                ToolTipManager.sharedInstance().setInitialDelay(750);
				dispose();
			}
		});
		
		bottomPanel.add(okButton);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

                //main panel
		//mainPanel.add(new PlanEvaluationEditPanel(xmlPlan), BorderLayout.CENTER);
                
                mainTabbedPane = new JTabbedPane();
                mainTabbedPane.setTabPlacement(JTabbedPane.NORTH);
                mainTabbedPane.addTab("Metrics",new PlanEvaluationEditPanel(xmlPlan));
                mainTabbedPane.addTab("Rationales", new PlanEvaluationRationalesEditPanel(xmlPlan));
                mainPanel.add(mainTabbedPane, BorderLayout.CENTER);

                add(mainPanel);
		


	}


}
