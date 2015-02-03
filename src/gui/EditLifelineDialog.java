/***
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
*
* Copyright (C) 2007-2012 University of Sao Paulo, University of Toronto
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
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdom.Element;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;

import src.languages.xml.XMLUtilities;

import java.awt.Color;

/**
 *
 * @author tiago
 */
public class EditLifelineDialog extends JDialog{

    private Element lifeline = null;
    private Element diagram = null;
    private static LifelinePanel parent = null;

    private JPanel mainPanel = null;


    //Intervals
    private ItPanel intervalsPanel;
    private JTextField ruleField;
    private JToolBar intervalsToolBar = null;
    private JList intervalsList = null;
    private DefaultListModel intervalsListModel = null;
    private ArrayList<Element> currentIntervals =null;
    private ItPanel editAndNewIntervalPanel;
    private Element selectedIntervalData = null;
    private JTextField rule;
    private JComboBox lowerboundType;
    private JComboBox lowerboundValue;
    private JComboBox upperboundType;
    private JComboBox upperboundValue;
    private JComboBox condeffectType; //indicates either the interval is a precondition or effect


    @SuppressWarnings("static-access")
    public EditLifelineDialog(Element timingDiagram, Element selectedlifeline, LifelinePanel parent) {
		super(ItSIMPLE.getItSIMPLEFrame());
		setTitle("Edit lifeline ("+ parent.getLifelineName()+")");
        //setTitle("Edit lifeline");
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		addWindowListener(new JDialogWindowAdapter(this));
		setModal(true);

        this.lifeline = selectedlifeline;
        this.diagram = timingDiagram;
        this.parent = parent;


        this.setSize(520,450);
        this.setLocation(280,200);
        this.add(getMainPanel(), BorderLayout.CENTER);


        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));


        //The OK button at the bottom
		JButton refreshButton = new JButton("Refresh",new ImageIcon("resources/images/refresh.png"));
		refreshButton.addActionListener(refreshLifeLinePanel);
        refreshButton.setToolTipText("Refresh current lifeline");
        buttonPanel.add(refreshButton);


        //The OK button at the bottom
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e) {
                String dtype = diagram.getChildText("type");
                String context = diagram.getChildText("context");

                if (dtype.equals("condition")){
                    if (context.equals("action")){

                    }
                }
                refreshLifelineChart();
                dispose();


            }
		});
		buttonPanel.add(okButton);



        this.add(buttonPanel, BorderLayout.SOUTH);

    }

    private JPanel getMainPanel() {


        if (mainPanel == null){
            mainPanel = new JPanel(new BorderLayout());

            //build components

            //1. get type and context
            String dtype = diagram.getChildText("type");
            String context = diagram.getChildText("context");


            if (dtype.equals("condition")){
                if (context.equals("action")){
                    //we need the object (action's parameter and the its attribute)
                }
            }


            mainPanel.add(getIntervalsPanel(), BorderLayout.CENTER);


        }
		return mainPanel;
        //

    }



    private ItPanel getIntervalsPanel() {
        intervalsPanel  = new ItPanel(new BorderLayout());

        //preferenceFunctionsPanel.setPreferredSize(new Dimension(250,200));

        ItPanel top = new ItPanel(new BorderLayout());
        ItPanel bottom = new ItPanel(new BorderLayout());
        ItPanel listPanel = new ItPanel(new BorderLayout());

        top.add(new JLabel("Time intervals:"), BorderLayout.NORTH);

	//Time Interval tree
        intervalsListModel = new DefaultListModel();
        intervalsList = new JList(intervalsListModel);
	ItListRenderer renderer = new ItListRenderer();
        renderer.setIcon(new ImageIcon("resources/images/operator.png"));
	intervalsList.setCellRenderer(renderer);
        intervalsList.setBackground(Color.WHITE);

        intervalsList.addListSelectionListener(new ListSelectionListener(){
                public void valueChanged(ListSelectionEvent e) {
                    // When the user release the mouse button and completes the selection,
                    // getValueIsAdjusting() becomes false
                    if (!e.getValueIsAdjusting()) {
                        if (intervalsList.getSelectedIndex() > -1){
                            Element selected = currentIntervals.get(intervalsList.getSelectedIndex());
                            if (selected != null){
                                selectedIntervalData = selected;
                                //show at the editAndNewFunctionPanel
                                fillEditIntervalPanel();
                                editAndNewIntervalPanel.setVisible(true);
                            }
                        }

                    }
                }
        });


        JScrollPane scrollText = new JScrollPane();
        scrollText.setViewportView(intervalsList);


        buildTimeIntervalsList();
	top.add(scrollText, BorderLayout.CENTER);

        //tool bar with buttons
        intervalsToolBar = new JToolBar();
        intervalsToolBar.add(newTimeInterval).setToolTipText("New time interval");
        intervalsToolBar.add(deleteTimeInterval).setToolTipText("Delete time interval");
        
        intervalsToolBar.setFloatable(false);

        top.add(intervalsToolBar, BorderLayout.SOUTH);
	//bottom.add(intervalsToolBar, BorderLayout.NORTH);

        listPanel.add(top, BorderLayout.CENTER);
        
        listPanel.add(bottom, BorderLayout.SOUTH);

        
        
        // Edit Interval panel
        //The time interval itself
        editAndNewIntervalPanel = new ItPanel(new BorderLayout());
        editAndNewIntervalPanel.setPreferredSize(new Dimension(250,110));
        
        
        rule =  new JTextField();
        rule.setPreferredSize(new Dimension(250,30));
        rule.addKeyListener(new KeyAdapter(){
            @Override
                        //public void keyPressed(KeyEvent e) {
            public void keyReleased(KeyEvent e) {
                selectedIntervalData.getChild("value").setText(rule.getText());
                intervalsListModel.set(intervalsList.getSelectedIndex(), getIntervalLabel(selectedIntervalData));
            }
        });
        //editAndNewIntervalPanel.add(new JLabel("Value: "), BorderLayout.WEST);
        //editAndNewIntervalPanel.add(rule, BorderLayout.CENTER);
        
        ItPanel rulevalue = new ItPanel(new BorderLayout());
        rulevalue.add(new JLabel("Value: "), BorderLayout.WEST);
        rulevalue.add(rule, BorderLayout.CENTER);
        editAndNewIntervalPanel.add(rulevalue, BorderLayout.NORTH);
        


        //The Intervals
        JPanel bounds = new JPanel(new SpringLayout());
        JLabel intervalLabel = new JLabel("Interval: ");

        lowerboundType = new JComboBox();
        lowerboundType.addItem("[");
        lowerboundType.addItem("(");
        lowerboundType.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                        String selection = (String)lowerboundType.getSelectedItem();
                        if (selection.equals("[")){
                            selectedIntervalData.getChild("durationConstratint").getChild("lowerbound").setAttribute("included","true");
                        }else{
                           selectedIntervalData.getChild("durationConstratint").getChild("lowerbound").setAttribute("included","false");
                        }
                        intervalsListModel.set(intervalsList.getSelectedIndex(), getIntervalLabel(selectedIntervalData));
                }

        });

        lowerboundValue = new JComboBox();
        lowerboundValue.addItem("");
        lowerboundValue.addItem("0");
        lowerboundValue.addItem("-1");
        lowerboundValue.addItem("1");
        lowerboundValue.addItem("2");
        lowerboundValue.setEditable(true);
        lowerboundValue.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                        String selection = (String)lowerboundValue.getSelectedItem();
                        selectedIntervalData.getChild("durationConstratint").getChild("lowerbound").setAttribute("value",selection);
                        intervalsListModel.set(intervalsList.getSelectedIndex(), getIntervalLabel(selectedIntervalData));
                }

        });

        JLabel commaLabel = new JLabel(" , ");

        upperboundType = new JComboBox();
        upperboundType.addItem("]");
        upperboundType.addItem(")");
        upperboundType.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                        String selection = (String)upperboundType.getSelectedItem();
                        if (selection.equals("]")){
                            selectedIntervalData.getChild("durationConstratint").getChild("upperbound").setAttribute("included","true");
                        }else{
                            selectedIntervalData.getChild("durationConstratint").getChild("upperbound").setAttribute("included","false");
                        }
                        intervalsListModel.set(intervalsList.getSelectedIndex(), getIntervalLabel(selectedIntervalData));
                }

        });

        upperboundValue = new JComboBox();
        upperboundValue.addItem("");
        upperboundValue.addItem("0");
        upperboundValue.addItem("1");
        upperboundValue.addItem("2");
        upperboundValue.setEditable(true);
        upperboundValue.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                        String selection = (String)upperboundValue.getSelectedItem();
                        selectedIntervalData.getChild("durationConstratint").getChild("upperbound").setAttribute("value",selection);
                        intervalsListModel.set(intervalsList.getSelectedIndex(), getIntervalLabel(selectedIntervalData));
                }

        });

        bounds.add(intervalLabel);
        bounds.add(lowerboundType);
        bounds.add(lowerboundValue);
        bounds.add(commaLabel);
        bounds.add(upperboundValue);
        bounds.add(upperboundType);


        SpringUtilities.makeCompactGrid(bounds, 1, 6, 5, 5, 5, 5);
        //editAndNewIntervalPanel.add(bounds, BorderLayout.SOUTH);
        editAndNewIntervalPanel.add(bounds, BorderLayout.CENTER);

        
        ItPanel condeffect = new ItPanel(new BorderLayout());
        //condeffect.setPreferredSize(new Dimension(250,100));
        condeffect.add(new JLabel("Type: "), BorderLayout.WEST);
        condeffectType = new JComboBox();
        condeffectType.addItem("precondition");
        condeffectType.addItem("effect");
        condeffectType.addItemListener(new ItemListener(){
                public void itemStateChanged(ItemEvent e) {
                        String selection = (String)condeffectType.getSelectedItem();
                        selectedIntervalData.getChild("type").setText(selection);
                }

        });
        condeffect.add(condeffectType, BorderLayout.CENTER);                
        
        editAndNewIntervalPanel.add(condeffect, BorderLayout.SOUTH);
        
        

        editAndNewIntervalPanel.setVisible(false);

        intervalsPanel.add(listPanel, BorderLayout.CENTER);
        intervalsPanel.add(editAndNewIntervalPanel, BorderLayout.SOUTH);



        
        return intervalsPanel;
    }


    private void buildTimeIntervalsList(){

        if (currentIntervals == null) {
            currentIntervals = new ArrayList<Element>();
        }else{
            currentIntervals.clear();
        }

        List<?> intervals = lifeline.getChild("timeIntervals").getChildren("timeInterval");
        for (Iterator<?> iter = intervals.iterator(); iter.hasNext();) {
            Element interval = (Element) iter.next();
            currentIntervals.add(interval);
            intervalsListModel.addElement(getIntervalLabel(interval));
        }

    }


    private String getIntervalLabel(Element interval){

        Element lowerbound = interval.getChild("durationConstratint").getChild("lowerbound");
        Element upperbound = interval.getChild("durationConstratint").getChild("upperbound");
        String intervalLabel = (lowerbound.getAttributeValue("included").equals("true")?"[":"(") +
                lowerbound.getAttributeValue("value") + "," + upperbound.getAttributeValue("value") +
                (upperbound.getAttributeValue("included").equals("true")?"]":")")  + " -> " +  interval.getChildText("value");
        return intervalLabel;
    }


    private void fillEditIntervalPanel(){
        if (selectedIntervalData != null){
           rule.setText(selectedIntervalData.getChildText("value"));
           Element lowerbound = selectedIntervalData.getChild("durationConstratint").getChild("lowerbound");
           Element upperbound = selectedIntervalData.getChild("durationConstratint").getChild("upperbound");
           
           Element thetype = null;
           if (selectedIntervalData.getChild("type") == null){
               thetype = new Element("type");
               thetype.setText("precondition");
               selectedIntervalData.addContent(thetype);
           }
           else{
               thetype = selectedIntervalData.getChild("type");
           }
           lowerboundType.setSelectedIndex((lowerbound.getAttributeValue("included").equals("true")? 0 : 1));
           upperboundType.setSelectedIndex((upperbound.getAttributeValue("included").equals("true")? 0 : 1));
           lowerboundValue.setSelectedItem(lowerbound.getAttributeValue("value"));
           upperboundValue.setSelectedItem(upperbound.getAttributeValue("value"));
           condeffectType.setSelectedIndex((thetype.getText().equals("precondition")? 0 : 1));
        }

    }



            //Actions

        /**
         * This action adds a new time interval
         */
        private Action newTimeInterval = new AbstractAction("New",new ImageIcon("resources/images/new.png")){

		public void actionPerformed(ActionEvent e) {
            Element interval = (Element)ItSIMPLE.getCommonData().getChild("definedNodes").getChild("elements").getChild("model").getChild("timeInterval").clone();
            String id = String.valueOf(XMLUtilities.getId(lifeline.getChild("timeIntervals")));
            interval.getAttribute("id").setValue(id);
            //check the type what we ar dealing with (attribute(boolean, number,etc), state)
            interval.getChild("value").setText("true");
            lifeline.getChild("timeIntervals").addContent(interval);

            intervalsListModel.addElement(getIntervalLabel(interval));

            currentIntervals.add(interval);

            editAndNewIntervalPanel.setVisible(true);

            selectedIntervalData = interval;

            intervalsList.setSelectedIndex(intervalsListModel.size()-1);
		}
	};

    /**
     * This action deletes a time interval
     */
	private Action deleteTimeInterval = new AbstractAction("Delete",new ImageIcon("resources/images/delete.png")){

		public void actionPerformed(ActionEvent e) {
			int row = intervalsList.getSelectedIndex();
			if (row > -1){
				Element selectedInterval = currentIntervals.get(row);
				lifeline.getChild("timeIntervals").removeContent(selectedInterval);
				currentIntervals.remove(row);
				intervalsListModel.removeElementAt(row);
                intervalsList.setSelectedIndex(intervalsListModel.size()-1);
                //when there is no more functions the hide the edit panel
                if (intervalsListModel.size() == 0){
                    selectedIntervalData = null;
                    editAndNewIntervalPanel.setVisible(false);
                }

			}
		}
	};

    /**
     * This action deletes a time interval
     */
	private Action refreshLifeLinePanel = new AbstractAction("Refresh",new ImageIcon("resources/images/refresh.png")){

		public void actionPerformed(ActionEvent e) {
            refreshLifelineChart();
		}
	};


    public void refreshLifelineChart(){
        parent.refreshLifeline();

    }


}

class JDialogWindowAdapter extends WindowAdapter {

	private JDialog m_dialog = null;

	/**
	 * Constructs the adapter.
	 * @param d the dialog to listen to.
	 */
	public JDialogWindowAdapter(JDialog d) {
		m_dialog = d;
	}

    @Override
	public void windowClosing(WindowEvent e) {
        ((EditLifelineDialog)m_dialog).refreshLifelineChart();
		super.windowClosing(e);
		//Dispose the hidden parent so that there are
		//no more references to the dialog and it can
		//be correctly garbage collected.

	   //((Window) m_dialog.getParent()).dispose();
	}
}
