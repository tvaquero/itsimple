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
*			Victor Romero.
**/

package itSIMPLE;

import languages.xml.XMLUtilities;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;

public class PlannersSettingsDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2828980904853019592L;
	
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	
	public PlannersSettingsDialog(Frame itSIMPLEFrame){
		this(itSIMPLEFrame, -1);
	}
	
	public PlannersSettingsDialog(Frame itSIMPLEFrame, int selectedPlannerIndex) {
		super(itSIMPLEFrame);
		
		setTitle("Planners Settings");
		
		int w = 700;
		int h = 500;
		
		setBounds(screenSize.width/2-w/2, screenSize.height/2-h/2, w, h);
		
		setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
		setResizable(true);
		
		JPanel mainPanel = new JPanel(new BorderLayout());
		JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				XMLUtilities.writeToFile(
						"resources/planners/itPlanners.xml", ItSIMPLE.getItPlanners().getDocument());

                                // Show tool tips after a second (since we change for immediately in the PlannersSettingsPanel)
                                ToolTipManager.sharedInstance().setInitialDelay(750);
				dispose();
			}
		});
		
		bottomPanel.add(okButton);
		mainPanel.add(bottomPanel, BorderLayout.SOUTH);
		mainPanel.add(new PlannersSettingsPanel(selectedPlannerIndex +1), BorderLayout.CENTER);
		
		add(mainPanel);
	}

}
