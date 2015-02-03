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

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.ToolTipManager;
import org.jdom.Element;

import src.languages.xml.XMLUtilities;
import src.util.database.DataBase;

public class RationaleEditDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2828980904853019592L;
	
	private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

        private Element xmlRationale = null;
        private RationaleEditPanel editpanel = null;
        private boolean isSaved = false;
	

	public RationaleEditDialog(Frame itSIMPLEFrame, final Element xmlRationale) {
		super(itSIMPLEFrame);

                this.xmlRationale = xmlRationale;
		setTitle("Edit Evatuation Rationale from Database");
		
		int w = 750;
		int h = 550;
		
		setBounds(screenSize.width/2-w/2, screenSize.height/2-h/2, w, h);
		
		setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
		setResizable(true);
		
		JPanel mainPanel = new JPanel(new BorderLayout());

                editpanel = new RationaleEditPanel(xmlRationale);

                mainPanel.add(editpanel, BorderLayout.CENTER);


                JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

		JButton okButton = new JButton("Save");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                            // Show tool tips after a second (since we change for immediately in the PlannersSettingsPanel)

                            //Update rationale
                            Element rationale = editpanel.getXMLRationale();
                            String rationaleID = rationale.getAttributeValue("id");
                            //4. Update if the rationale was modified (i.e., it has a child node called 'modified')

                            Element modified = rationale.getChild("modified");
                            if (modified!=null && !rationaleID.equals("")){
                                DataBase updateType = new DataBase();
                                Element theRationale = (Element)rationale.clone();
                                theRationale.setAttribute("id", "");
                                theRationale.setAttribute("targetplanid", "");
                                theRationale.setAttribute("relationalid", "");
                                theRationale.removeContent(theRationale.getChild("modified"));

                                String xmlRationaleStr = XMLUtilities.toString(theRationale).replace("'", "''");
                                String rationaleName = rationale.getChildText("name").replace("'", "''");
                                String rationaleDescription = rationale.getChildText("description").replace("'", "''");
                                String rationaleRule = rationale.getChildText("rule").replace("'", "''");
                                String rationaleQualityImpact = rationale.getChild("impact").getAttributeValue("quality");
                                String rationaleRange = rationale.getChild("abstractionlevel").getAttributeValue("range");
                                String rationaleValidity = rationale.getChild("validity").getAttributeValue("isValid").trim().toUpperCase();
                                if (rationaleValidity.equals("")){
                                    rationaleValidity = "NULL";
                                }

                                //System.out.println(rationaleRule);
                                updateType.setTableName("rationale");
                                updateType.setUpdateValueList("xmlrationale = '"+xmlRationaleStr+"', name = '" + rationaleName + "', description = '" + rationaleDescription +
                                        "', rule = '" + rationaleRule + "', abstractionlevel = '" + rationaleRange+"', validity = "+rationaleValidity+", qualityimpact = '"+rationaleQualityImpact+"'");
                                updateType.setWhereClause("id = " + rationaleID); //allways use WHERE
                                updateType.Update();

                                rationale.removeContent(modified);

                                updateType.Close();
                                isSaved = true;

                                System.out.println("Modified rationale updated - "+ rationaleID);
                                ItSIMPLE.getInstance().appendOutputPanelText(">> Rationale (id "+rationaleID+") updated. \n");

                            }

                            

                            ToolTipManager.sharedInstance().setInitialDelay(750);
                            dispose();
			}
		});
		bottomPanel.add(okButton);
                
                JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                            // Show tool tips after a second (since we change for immediately in the PlannersSettingsPanel)
                            ToolTipManager.sharedInstance().setInitialDelay(750);
                            dispose();
			}
		});                                		
		bottomPanel.add(cancelButton);

		mainPanel.add(bottomPanel, BorderLayout.SOUTH);

		
		add(mainPanel);
	}

     /**
     * This method returns the xml rationale
     */
    public Element getXMLRationale(){
        Element theRationale = null;
        if (editpanel!=null){
           theRationale =  editpanel.getXMLRationale();
        }
        return theRationale;
    }

    /**
     * This method returns true if the rationale was saved
     */
    public boolean isSaved(){
        return isSaved;
    }


}
