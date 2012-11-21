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

package itSIMPLE;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;

import org.jdom.Element;

import com.jgoodies.forms.layout.CellConstraints;
import com.jgoodies.forms.layout.FormLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionListener;
import languages.xml.XMLUtilities;

public class RationaleEditPanel extends ItPanel
		implements ItemListener, KeyListener{

    /**
     *
     */
    private static final long serialVersionUID = -2719862281148936012L;


    private Element xmlRationale = null;

    private JTextField rationaleNameField = null;
    private JTextPane descriptionTextPane = null;
    private JTextPane formalDescriptionTextPane = null;
    private JComboBox qualityImpactCombo = null;
    private JComboBox rationaleLevelCombo = null;
    private JLabel validityLabel = null;
    private JButton checkRationaleButton = null;



    //@SuppressWarnings("unchecked")
    public RationaleEditPanel(Element theRationale){
            super();
            this.xmlRationale = theRationale;
            initialize();
    }

    private void initialize(){
        // Get current delay
        //int initialDelay = ToolTipManager.sharedInstance().getInitialDelay();
        //System.out.print(initialDelay);

        // Show tool tips immediately for the information icons
        ToolTipManager.sharedInstance().setInitialDelay(0);

        setLayout(new BorderLayout());

        ItFramePanel rationaleFramePanel = new ItFramePanel("Rationale", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
        JPanel rationalesEvaluationPanel = new JPanel(new BorderLayout());

        //JPanel leftPanel = new JPanel(new BorderLayout());
        //leftPanel.setPreferredSize(new Dimension(250,200));
        //leftPanel.add(getRationaleListPanel(), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(getRationaleDescriptionPanel(), BorderLayout.CENTER);


        //rationalesEvaluationPanel.add(leftPanel, BorderLayout.WEST);
        rationalesEvaluationPanel.add(rightPanel, BorderLayout.CENTER);
        rationaleFramePanel.setContent(rationalesEvaluationPanel, false);

        add(rationaleFramePanel, BorderLayout.CENTER);


        //check whether xml is not empty
        if (xmlRationale !=null){
            setEnableRationalEdit();
            setRationaleContent();

        }else{
            setUnableRationalEdit();
        }




    }



    private JPanel getRationaleDescriptionPanel(){
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new BorderLayout());

        JPanel namePanel = new JPanel(new BorderLayout());
        JLabel nameLabel = new JLabel("Name: ");
        rationaleNameField = new JTextField(200);
        rationaleNameField.addKeyListener(this);
        namePanel.add(nameLabel, BorderLayout.WEST);
        namePanel.add(rationaleNameField, BorderLayout.CENTER);


        JPanel descriptionPanel = new JPanel(new BorderLayout());
        JLabel descriptionLabel = new JLabel("<html><br>Description: </html>");
        descriptionTextPane = new JTextPane();
        descriptionTextPane.addKeyListener(this);
        descriptionTextPane.setBackground(Color.WHITE);
        descriptionTextPane.setPreferredSize(new Dimension(250,100));
        JScrollPane scrollText = new JScrollPane();
        scrollText.setViewportView(descriptionTextPane);
        descriptionPanel.add(descriptionLabel, BorderLayout.NORTH);
        descriptionPanel.add(scrollText, BorderLayout.CENTER);
        descriptionPanel.setPreferredSize(new Dimension(250,120));


        topPanel.add(namePanel, BorderLayout.NORTH);
        topPanel.add(descriptionPanel, BorderLayout.CENTER);


        JPanel centerPanel = new JPanel(new BorderLayout());

        JPanel formalDescriptionPanel = new JPanel(new BorderLayout());
        JLabel formalDescriptionLabel = new JLabel("<html><br>Formal description (PSL based formalism): </html>");
        formalDescriptionTextPane = new JTextPane();
        formalDescriptionTextPane.addKeyListener(this);
        formalDescriptionTextPane.setBackground(Color.WHITE);
        formalDescriptionTextPane.setPreferredSize(new Dimension(250,100));
        formalDescriptionTextPane.setFont(new Font("Monospaced", Font.PLAIN, 14));

        JScrollPane formalscrollText = new JScrollPane();
        formalscrollText.setViewportView(formalDescriptionTextPane);
        formalDescriptionPanel.add(formalDescriptionLabel, BorderLayout.NORTH);
        formalDescriptionPanel.add(formalscrollText, BorderLayout.CENTER);

        centerPanel.add(formalDescriptionPanel, BorderLayout.CENTER);


         FormLayout layoutbotton = new FormLayout(
                        "pref, 4px, 200px, 4px, pref", // columns
                        "4px, pref, 4px, pref, 4px, pref, 4px, pref"); // rows
        JPanel bottonPanel = new JPanel(layoutbotton);

        JLabel impactLabel = new JLabel("Quality impact: ");
        qualityImpactCombo = new JComboBox();
        qualityImpactCombo.addItem("");
        qualityImpactCombo.addItem("increase");
        qualityImpactCombo.addItem("decrease");
        qualityImpactCombo.addItem("none");
        qualityImpactCombo.addItemListener(this);

        //abstraction level
        //JPanel levelPanel = new JPanel(new BorderLayout());
        JLabel levelLabel = new JLabel("Abstraction level: ");
        rationaleLevelCombo = new JComboBox();
        rationaleLevelCombo.addItem("");
        rationaleLevelCombo.addItem("problem-specific");
        rationaleLevelCombo.addItem("domain-specific");
        rationaleLevelCombo.addItem("project-specific");
        rationaleLevelCombo.addItem("project-independent");
        rationaleLevelCombo.addItemListener(this);
        //levelPanel.add(levelLabel, BorderLayout.WEST);
        //levelPanel.add(rationaleLevelCombo, BorderLayout.CENTER);

        //validity
        //JPanel validityPanel = new JPanel(new BorderLayout());
        JLabel validationLabel = new JLabel("Validity: ");
        validityLabel = new JLabel("<html><strong>not verified</strong></html>");
        //validityPanel.add(validationLabel, BorderLayout.WEST);
        //validityPanel.add(validityLabel, BorderLayout.CENTER);


        //reEvaluateButton = new JButton("Recalculate", new ImageIcon("resources/images/refresh2.png"));
        checkRationaleButton = new JButton("Verify", new ImageIcon("resources/images/validate.png"));
        checkRationaleButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    //check whether the rational is valid
                }
        });
        checkRationaleButton.setToolTipText("<html>Verify whether the rationale is valid in the given context</html>");

        CellConstraints c = new CellConstraints();
        bottonPanel.add(impactLabel, c.xy (1, 2));
        bottonPanel.add(qualityImpactCombo, c.xy(3, 2));
        bottonPanel.add(levelLabel, c.xy (1, 4));
        bottonPanel.add(rationaleLevelCombo, c.xy(3, 4));
        bottonPanel.add(validationLabel, c.xy(1, 6));
        bottonPanel.add(validityLabel, c.xy(3, 6));
        //bottonPanel.add(checkRationaleButton, c.xy(1, 8));


        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottonPanel, BorderLayout.SOUTH);


	return mainPanel;
    }


    /**
     * This method set the cost and plan evaluation values
     */
    private void setRationaleContent(){
        if (xmlRationale!= null){
            setEnableRationalEdit();
            //set name
            rationaleNameField.setText(xmlRationale.getChildText("name"));
            descriptionTextPane.setText(xmlRationale.getChildText("description"));
            formalDescriptionTextPane.setText(xmlRationale.getChildText("rule"));

            //impact
            String qualityimpact = xmlRationale.getChild("impact").getAttributeValue("quality").trim();
            if (qualityimpact.equals("increase")){
                qualityImpactCombo.setSelectedIndex(1);
            }
            else if (qualityimpact.equals("decrease")){
                qualityImpactCombo.setSelectedIndex(2);
            }
            else if (qualityimpact.equals("none")){
                qualityImpactCombo.setSelectedIndex(3);
            }
            else{
                qualityImpactCombo.setSelectedIndex(0);
            }

            //range
            String range = xmlRationale.getChild("abstractionlevel").getAttributeValue("range").trim();
            if (range.equals("problem-specific")){
                rationaleLevelCombo.setSelectedIndex(1);
            }
            else if (range.equals("domain-specific")){
                rationaleLevelCombo.setSelectedIndex(2);
            }
            else if (range.equals("project-specific")){
                rationaleLevelCombo.setSelectedIndex(3);
            }
            else if (range.equals("project-independent")){
                rationaleLevelCombo.setSelectedIndex(4);
            }
            else{
                rationaleLevelCombo.setSelectedIndex(0);
            }

            String validityStr = "";
            if (xmlRationale.getChild("validity").getAttributeValue("isValid").trim().equals("")){
                validityStr = "not verified";
            }else if (xmlRationale.getChild("validity").getAttributeValue("isValid").trim().equals("true")){
                validityStr = "VALID";
            }else if (xmlRationale.getChild("validity").getAttributeValue("isValid").trim().equals("false")){
                validityStr = "INVALID";
            }
            validityLabel.setText(validityStr);
        }

    }


    /**
     * This method disable the rational edit components
     */
    private void setUnableRationalEdit(){
        rationaleNameField.setEnabled(false);
        descriptionTextPane.setEnabled(false);
        formalDescriptionTextPane.setEnabled(false);
        qualityImpactCombo.setEnabled(false);
        rationaleLevelCombo.setEnabled(false);
        validityLabel.setEnabled(false);
        checkRationaleButton.setEnabled(false);

        //clean up components
        rationaleNameField.setText("");
        descriptionTextPane.setText("");
        formalDescriptionTextPane.setText("");
        qualityImpactCombo.setSelectedIndex(0);
        rationaleLevelCombo.setSelectedIndex(0);
        validityLabel.setText("");
    }
    
    /**
     * This method disables the rational edit components
     */
    private void setEnableRationalEdit(){
        rationaleNameField.setEnabled(true);
        descriptionTextPane.setEnabled(true);
        formalDescriptionTextPane.setEnabled(true);
        qualityImpactCombo.setEnabled(true);
        rationaleLevelCombo.setEnabled(true);
        validityLabel.setEnabled(true);
        checkRationaleButton.setEnabled(true);
    }
    
    
    /**
     * This method marks that the rationale was modified
     */
    private void setModified(){
        if (!xmlRationale.getAttributeValue("id").trim().equals("")){
            if (xmlRationale.getChild("modified")==null){
                Element modified = new Element("modified");
                xmlRationale.addContent(modified);
            }
        }
    }

    /**
     * This method returns the xml rationale
     */
    public Element getXMLRationale(){
        return xmlRationale;
    }



    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource() == rationaleLevelCombo && e.getStateChange() == ItemEvent.SELECTED){
            if (xmlRationale != null){
                xmlRationale.getChild("abstractionlevel").setAttribute("range", (String)rationaleLevelCombo.getSelectedItem());
                setModified();
            }
        }
        else if(e.getSource() == qualityImpactCombo && e.getStateChange() == ItemEvent.SELECTED){
            if (xmlRationale != null){
                xmlRationale.getChild("impact").setAttribute("quality", (String)qualityImpactCombo.getSelectedItem());
                setModified();
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
        if(e.getSource() == rationaleNameField){
            //set plan evaluation
            if (xmlRationale != null && xmlRationale != null){
                if (!xmlRationale.getChildText("name").equals(rationaleNameField.getText())){
                    xmlRationale.getChild("name").setText(rationaleNameField.getText());
                    setModified();
                }
            }
        }
        else if(e.getSource() == descriptionTextPane){
            //set plan evaluation
            if (xmlRationale != null && xmlRationale != null){
                xmlRationale.getChild("description").setText(descriptionTextPane.getText());
                setModified();
            }
        }
        else if(e.getSource() == formalDescriptionTextPane){
            //set plan evaluation
            if (xmlRationale != null && xmlRationale != null){
                xmlRationale.getChild("rule").setText(formalDescriptionTextPane.getText());
                setModified();
            }
        }


    }








}
