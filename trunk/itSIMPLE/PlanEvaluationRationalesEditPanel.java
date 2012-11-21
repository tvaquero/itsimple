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

import alice.tuprolog.NoMoreSolutionException;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

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
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JToolBar;
import javax.swing.ToolTipManager;
import javax.swing.event.ListSelectionListener;
import languages.xml.XMLUtilities;
import org.jdom.Namespace;
import rationale.RationaleAnalyzer;

public class PlanEvaluationRationalesEditPanel extends ItPanel 
		implements ItemListener, KeyListener{

    /**
     *
     */
    private static final long serialVersionUID = -2719862281148936012L;


    private Element xmlPlan = null;

    private DefaultListModel rationaleListModel = null;
    private JList rationaleList = null;
    private ArrayList<Element> currentRationales = new ArrayList<Element>();
    private Element selectedRationale;
    private Element rationales;


    private JTextField rationaleNameField = null;
    private JTextPane descriptionTextPane = null;
    private JTextPane formalDescriptionTextPane = null;
    private JComboBox qualityImpactCombo = null;
    private JComboBox rationaleLevelCombo = null;
    private JLabel validityLabel = null;
    private JButton checkRationaleButton = null;

    private int currentDBPlanID = -1;
    private boolean isPlanFromDB = false;
    private boolean readyforEdit = false;

    private Thread currentThread = null;




    //@SuppressWarnings("unchecked")
    public PlanEvaluationRationalesEditPanel(Element thePlan){
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

        ItFramePanel rationalesFramePanel = new ItFramePanel("Evaluation rationales", ItFramePanel.NO_MINIMIZE_MAXIMIZE);
        JPanel rationalesEvaluationPanel = new JPanel(new BorderLayout());

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setPreferredSize(new Dimension(250,200));
        leftPanel.add(getRationaleListPanel(), BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(getRationaleDescriptionPanel(), BorderLayout.CENTER);


        rationalesEvaluationPanel.add(leftPanel, BorderLayout.WEST);
        rationalesEvaluationPanel.add(rightPanel, BorderLayout.CENTER);
        rationalesFramePanel.setContent(rationalesEvaluationPanel, false);

        add(rationalesFramePanel, BorderLayout.CENTER);


        //if xml is not empty show existing rationales
        if (xmlPlan !=null){
            Element theRationales = xmlPlan.getChild("evaluation").getChild("rationales");
            if (theRationales==null){
                theRationales = new Element("rationales");
                xmlPlan.getChild("evaluation").addContent(theRationales);
            }
            rationales = xmlPlan.getChild("evaluation").getChild("rationales");

            setRationaleListPanel();

            isPlanFromDB = ItSIMPLE.getInstance().isCurrentPlanFromDatabase();
            currentDBPlanID = ItSIMPLE.getInstance().getCurrentPlanFromDatabaseID();

        }
        setUnableRationalEdit();




    }


    private Action newRationale = new AbstractAction("New",new ImageIcon("resources/images/new.png")){
            /**
             *
             */
            private static final long serialVersionUID = -2864445237015276324L;

            public void actionPerformed(ActionEvent e) {
                    if (xmlPlan != null && rationales != null){
                            Element rationale = (Element)ItSIMPLE.getCommonData().getChild("planAnalysis").getChild("rationale").clone();
                            //String id = String.valueOf(XMLUtilities.getId(rationales));
                            //rationale.getAttribute("id").setValue(id);
                            //rationale.getChild("name").setText("rationale "+id);
                            rationale.getChild("name").setText("New rationale");
                            rationale.setAttribute("id", "");
                            rationales.addContent(rationale);

                            //check if the plan is from db
                            //if so we insert the new rationale into



                            currentRationales.add(rationale);
                            rationaleListModel.addElement(rationale.getChildText("name"));

                    }
            }
    };

    private Action deleteRationale = new AbstractAction("Delete",new ImageIcon("resources/images/delete.png")){
            /**
             *
             */
        private static final long serialVersionUID = -2864445237015276324L;

        public void actionPerformed(ActionEvent e) {
            int row = rationaleList.getSelectedIndex();
            if (row > -1){
                if (selectedRationale.getAttributeValue("id").equals("")){
                    selectedRationale = currentRationales.get(row);
                    rationales.removeContent(selectedRationale);
                    currentRationales.remove(row);
                    rationaleListModel.removeElementAt(row);
                }else{
                    JOptionPane.showMessageDialog(PlanEvaluationRationalesEditPanel.this,
                            "<html>This rationale is registered in the database. <br>"
                            + "If you wish to delete you must use the 'Rationale Database' panel.</html>");
                }

            }
        }
    };


    private Action setNotAppliedRationale = new AbstractAction("Set rationale to 'NOT APPLIED' for this plan",new ImageIcon("resources/images/evalquick.png")){
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                    int row = rationaleList.getSelectedIndex();
                    if (row > -1){
                            selectedRationale = currentRationales.get(row);
                            selectedRationale.setAttribute("enabled", "false");
                            //set new label
                            rationaleListModel.set(rationaleList.getSelectedIndex(),getRationaleName(selectedRationale));
                            setReferenceModified();
                            setApplicabilityButtons(selectedRationale);
                            //XMLUtilities.printXML(selectedRationale);
                    }
            }
    };

    private Action setAppliedRationale = new AbstractAction("Set rationale back to 'APPLIED' for this plan",new ImageIcon("resources/images/validate.png")){
            /**
             *
             */
            public void actionPerformed(ActionEvent e) {
                    int row = rationaleList.getSelectedIndex();
                    if (row > -1){
                            selectedRationale = currentRationales.get(row);
                            selectedRationale.setAttribute("enabled", "true");
                            //set new label
                            rationaleListModel.set(rationaleList.getSelectedIndex(),getRationaleName(selectedRationale));
                            setReferenceModified();
                            setApplicabilityButtons(selectedRationale);
                            //XMLUtilities.printXML(selectedRationale);
                    }
            }
    };


    private JPanel getRationaleListPanel(){
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setPreferredSize(new Dimension(250,200));

        JPanel centralPanel = new ItPanel(new BorderLayout());
        JPanel bottomPanel = new ItPanel(new BorderLayout());

        JToolBar rationaleToolBar = new JToolBar();
        rationaleToolBar.add(newRationale).setToolTipText("New rationale");
        rationaleToolBar.add(deleteRationale).setToolTipText("Delete selected rationale");
        //rationaleToolBar.add(editRationale).setToolTipText("Edit selected eachRationale");
        rationaleToolBar.addSeparator();
        rationaleToolBar.add(setNotAppliedRationale).setToolTipText("Set rationale to 'NOT APPLIED' for this plan");
        rationaleToolBar.add(setAppliedRationale).setToolTipText("Set rationale back to 'APPLIED' for this plan");

        rationaleListModel = new DefaultListModel();
        rationaleList = new JList(rationaleListModel);
        rationaleList.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                //throw new UnsupportedOperationException("Not supported yet.");
                if (e.getValueIsAdjusting() == false) {

                        if (rationaleList.getSelectedIndex() == -1) {
                            setUnableRationalEdit();
                        } else {
                            selectedRationale = currentRationales.get(rationaleList.getSelectedIndex());
                            if (selectedRationale != null){
                                setEnableRationalEdit();
                                readyforEdit = false;
                                setRationaleContent();
                                readyforEdit = true;
                            }else{
                                setUnableRationalEdit();
                            }
                        }
                    }
            }
        });
        ItListRenderer renderer = new ItListRenderer();


        renderer.setIcon(new ImageIcon("resources/images/feedbackrationale.png"));
        rationaleList.setCellRenderer(renderer);

        JScrollPane scrollText = new JScrollPane();
        scrollText.setViewportView(rationaleList);
        //rationaleList.addMouseListener(this);
        rationaleList.addKeyListener(this);

        centralPanel.add(scrollText, BorderLayout.CENTER);
        bottomPanel.add(rationaleToolBar, BorderLayout.NORTH);

        mainPanel.add(centralPanel, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
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
        checkRationaleButton.setActionCommand("verify");
        checkRationaleButton.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    //check whether the rational is valid
                    final Element problem = ItSIMPLE.getInstance().getSelectedProblemTreeNode();
                    if (problem != null && problem.getName().equals("problem")){

                        if (checkRationaleButton.getActionCommand().equals("verify")){

                            checkRationaleButton.setText("Stop");
                            checkRationaleButton.setActionCommand("stop");
                            currentThread = new Thread(){
                                public void run() {

                                    validityLabel.setText("Analyzing...");
                                    validityLabel.repaint();
                                    String valResult = null;
                                    try {
                                        valResult = RationaleAnalyzer.validateRationaleWithPlan(xmlPlan, problem, selectedRationale, Integer.toString(currentDBPlanID), validityLabel);
                                    } catch (NoMoreSolutionException ex) {
                                        Logger.getLogger(PlanEvaluationRationalesEditPanel.class.getName()).log(Level.SEVERE, null, ex);
                                    }

                                    checkRationaleButton.setActionCommand("verify");
                                    checkRationaleButton.setText("Verify");
                                    
                                    if (valResult.equals("yes")){
                                        JOptionPane.showMessageDialog(PlanEvaluationRationalesEditPanel.this,"<html>The rationale is VALID for the current plan.</html>");
                                    }else if (valResult.equals("no")){
                                        JOptionPane.showMessageDialog(PlanEvaluationRationalesEditPanel.this,"<html>The specified rationale is NOT valid for the current plan.<br>Please insert a valid rationale. </html>");
                                    }
                                }
                            };
                            currentThread.start();

                        }else{
                            //stop the thread
                            if(currentThread.isAlive()){
                                try {
                                        // waits for the thread to return
                                        currentThread.join(2000);// 2 seconds time-out
                                } catch (InterruptedException e1) {
                                        e1.printStackTrace();
                                }
                                if(currentThread.isAlive()){
                                    currentThread.interrupt();
                                }
                            }
                            checkRationaleButton.setActionCommand("verify");
                            checkRationaleButton.setText("Verify");
                            validityLabel.setText("Stopped");

                        }
                        
                    }else{
                        JOptionPane.showMessageDialog(PlanEvaluationRationalesEditPanel.this,"<html>In order to validate the rationale, it is required <br>to select the corresponding planning problem. </html>");
                    }
                }
        });
        checkRationaleButton.setToolTipText("<html>Verify whether the rationale is valid for the given plan. <br>It checks whether quality_rationale_of(<this rationale>,<this plan>) is true</html>");

        CellConstraints c = new CellConstraints();
        bottonPanel.add(impactLabel, c.xy (1, 2));
        bottonPanel.add(qualityImpactCombo, c.xy(3, 2));
        bottonPanel.add(levelLabel, c.xy (1, 4));
        bottonPanel.add(rationaleLevelCombo, c.xy(3, 4));
        bottonPanel.add(validationLabel, c.xy(1, 6));
        bottonPanel.add(validityLabel, c.xy(3, 6));
        bottonPanel.add(checkRationaleButton, c.xy(1, 8));


        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        mainPanel.add(bottonPanel, BorderLayout.SOUTH);


	return mainPanel;
    }




    /**
     * This methos fill out the rationale list
     */
    private void setRationaleListPanel(){
        //1. Fill out eachRationale list
        rationaleListModel.clear();
        currentRationales.clear();

        Iterator<?> therationales = rationales.getChildren("rationale").iterator();
        while (therationales.hasNext()){
                Element eachRationale = (Element)therationales.next();
                //rationaleListModel.addElement(eachRationale.getChildText("name"));
                rationaleListModel.addElement(getRationaleName(eachRationale));
                currentRationales.add(eachRationale);
        }
    }

    /**
     * This method set the cost and plan evaluation values
     */
    private void setRationaleContent(){
        if (xmlPlan!= null){
            setEnableRationalEdit();
            //set name
            rationaleNameField.setText(selectedRationale.getChildText("name"));
            descriptionTextPane.setText(selectedRationale.getChildText("description"));
            formalDescriptionTextPane.setText(selectedRationale.getChildText("rule"));

            //impact
            String qualityimpact = selectedRationale.getChild("impact").getAttributeValue("quality").trim();
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
            String range = selectedRationale.getChild("abstractionlevel").getAttributeValue("range").trim();
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
            if (selectedRationale.getChild("validity").getAttributeValue("isValid").trim().equals("")){
                validityStr = "not verified";
            }else if (selectedRationale.getChild("validity").getAttributeValue("isValid").trim().equals("true")){
                validityStr = "VALID";
            }else if (selectedRationale.getChild("validity").getAttributeValue("isValid").trim().equals("false")){
                validityStr = "IVALID";
            }
            validityLabel.setText(validityStr);

            setApplicabilityButtons(selectedRationale);

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
        if (!selectedRationale.getAttributeValue("id").trim().equals("")){
            if (selectedRationale.getChild("modified")==null){
                Element modified = new Element("modified");
                selectedRationale.addContent(modified);
            }
        }
    }


      /**
     * This method marks that the reference of rationale was modified
     */
    private void setReferenceModified(){
        if (!selectedRationale.getAttributeValue("relationalid").trim().equals("")){
            if (selectedRationale.getChild("instruction")==null){
                Element instruction = new Element("instruction");
                instruction.setAttribute("perform", "update-reference");
                selectedRationale.addContent(instruction);
            }
        }
    }
    
    
    private String getRationaleName(Element rationale){
        String name = "";
        if (rationale != null){
            if (rationale.getAttributeValue("enabled").toLowerCase().equals("false")){
                name = "(NOT APPLIED) ";                
            }
            name += rationale.getChildText("name");
        }
        return name;
    }


    private void setApplicabilityButtons(Element rationale){
        if (rationale != null){
            //check if is enabled or not
            if (rationale.getAttributeValue("enabled").equals("true")){
                setNotAppliedRationale.setEnabled(true);
                setAppliedRationale.setEnabled(false);
            }else{
                setNotAppliedRationale.setEnabled(false);
                setAppliedRationale.setEnabled(true);
            }
        }
    }









    @Override
    public void itemStateChanged(ItemEvent e) {
        if(e.getSource() == rationaleLevelCombo && e.getStateChange() == ItemEvent.SELECTED){
            if (xmlPlan != null && selectedRationale != null && readyforEdit){
                selectedRationale.getChild("abstractionlevel").setAttribute("range", (String)rationaleLevelCombo.getSelectedItem());
                setModified();
                System.out.println("Here L");
            }

                // clear the table
//			tableModel.setRowCount(0);
//
//			// clear check boxes
//			windowsCheck.setSelected(false);
//			linuxCheck.setSelected(false);
//                        macCheck.setSelected(false);
//                        //clear timeout
//                        individualTimeOutCheck.setSelected(false);
//                        individualTimeOutValue.setText("");
//                        individualTimeOutCheck.setEnabled(false);
//                        individualTimeOutValue.setEnabled(false);
//
//                        //clear favorites
//                        favoriteCheck.setSelected(false);
//                        favoriteCheck.setEnabled(false);
//
//
//
//			int selectedIndex = rationaleLevelCombo.getSelectedIndex();
//			if(selectedIndex > 0){// discard "Select" item
//				// get the xml node for the planner
//				selectedRationale = rationales.get(selectedIndex - 1);
//				setMetricsTable();
//			}
        }
        else if(e.getSource() == qualityImpactCombo && e.getStateChange() == ItemEvent.SELECTED){
            if (xmlPlan != null && selectedRationale != null && readyforEdit){
                selectedRationale.getChild("impact").setAttribute("quality", (String)qualityImpactCombo.getSelectedItem());
                setModified();
                System.out.println("Here Q");
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
            if (xmlPlan != null && selectedRationale != null){
                if (!selectedRationale.getChildText("name").equals(rationaleNameField.getText())){
                    selectedRationale.getChild("name").setText(rationaleNameField.getText());
                    rationaleListModel.set(rationaleList.getSelectedIndex(),getRationaleName(selectedRationale));
                    setModified();
                }
            }
        }
        else if(e.getSource() == descriptionTextPane){
            //set plan evaluation
            if (xmlPlan != null && selectedRationale != null){
                selectedRationale.getChild("description").setText(descriptionTextPane.getText());
                setModified();
            }
        }
        else if(e.getSource() == formalDescriptionTextPane){
            //set plan evaluation
            if (xmlPlan != null && selectedRationale != null){
                selectedRationale.getChild("rule").setText(formalDescriptionTextPane.getText());
                setModified();
            }
        }


    }








}
