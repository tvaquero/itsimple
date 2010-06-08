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

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultCellEditor;
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
import javax.swing.JTextField;
import javax.swing.SwingConstants;

public class PlannersSettingsPanel extends ItPanel 
		implements ItemListener, TableModelListener, KeyListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = -2719862281148936012L;
	
	private JComboBox plannersCombo;
	private JCheckBox windowsCheck;
	private JCheckBox linuxCheck;
    private JCheckBox macCheck;
    private JCheckBox timeOutCheck;
    private JTextField timeOutValue;
    private JCheckBox individualTimeOutCheck;
    private JTextField individualTimeOutValue;
    private JCheckBox favoriteCheck;
	private JTable paramTable;
	private DefaultTableModel tableModel;
	private List<Element> planners;
	private Element selectedPlanner;

	
	@SuppressWarnings("unchecked")
	public PlannersSettingsPanel(int selectedPlannerIndex){
		super();
		planners = ItSIMPLE.getItPlanners().getChild("planners").getChildren("planner");
		initialize();
		
		if(selectedPlannerIndex > -1){
			plannersCombo.setSelectedIndex(selectedPlannerIndex);
		}
	}

	private void initialize(){
		
		setLayout(new BorderLayout());

        JPanel header = new JPanel(new BorderLayout());
		
		FormLayout layout = new FormLayout(
				"pref, 4px, 200px", // columns
				"pref, 4px, pref, 4px"); // rows	
		JPanel topPanel = new JPanel(layout);	
		
		plannersCombo = new JComboBox();
		populatePlannersComboBox();
		plannersCombo.addItemListener(this);

	
		CellConstraints cc = new CellConstraints();
		topPanel.add(new JLabel("Planners:"), cc.xy (1, 1));
		topPanel.add(plannersCombo, cc.xy(3, 1));
		topPanel.add(new JLabel("Parameters:"), cc.xy (1, 3));
		
		//add(topPanel, BorderLayout.NORTH);
        header.add(topPanel, BorderLayout.WEST);

        // time-out
        JPanel timeOutPanel = getTimeOutPanel();
        header.add(timeOutPanel,BorderLayout.CENTER);

        add(header,BorderLayout.NORTH);
		
		// create parameters table		
		JScrollPane scrollParamPane = new JScrollPane(getParametersTable());		
		JPanel paramPane = new JPanel(new BorderLayout());
		paramPane.add(scrollParamPane, BorderLayout.CENTER);
		paramPane.setPreferredSize(new Dimension(600, 210));
		
		add(paramPane, BorderLayout.CENTER);
		
		// operational system check boxes		
		JPanel platformPanel = getPlatformPanel();
        JPanel individualTimeoutPanel = getIndividualTimeoutPanel();

        JPanel favoritePanel = getFavoritePanel();

        JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(platformPanel, BorderLayout.NORTH);
        bottom.add(individualTimeoutPanel, BorderLayout.CENTER);
        bottom.add(favoritePanel, BorderLayout.SOUTH);
		
        add(bottom, BorderLayout.SOUTH);
        //add(platformPanel, BorderLayout.SOUTH);


		
	}
	
	private void populatePlannersComboBox(){
		plannersCombo.removeAllItems();
		plannersCombo.addItem("Select");
		
		for (Iterator<Element> iterator = planners.iterator(); iterator.hasNext();) {
			Element planner = iterator.next();
			plannersCombo.addItem(planner.getChildText("name") + 
					" - " + planner.getChildText("version"));
		}
	}
	
	private JTable getParametersTable(){
		tableModel = new DefaultTableModel();
		paramTable = new JTable(tableModel);
		tableModel.addTableModelListener(this);
		
		tableModel.addColumn("#");
		tableModel.addColumn("Parameter");
		tableModel.addColumn("Description");
		tableModel.addColumn("Enable");
		tableModel.addColumn("Default Value");
		
		paramTable.getColumnModel().getColumn(0).setMaxWidth(25);		
		paramTable.getColumnModel().getColumn(1).setMaxWidth(70);
		paramTable.getColumnModel().getColumn(3).setMaxWidth(50);
		paramTable.getColumnModel().getColumn(4).setMinWidth(90);
		paramTable.getColumnModel().getColumn(4).setMaxWidth(100);
		
		// create true, false combo box
		JComboBox enableCombo = new JComboBox();
		enableCombo.addItem("true");
		enableCombo.addItem("false");		
		paramTable.getColumnModel().getColumn(3).setCellEditor(new DefaultCellEditor(enableCombo));
		
		return paramTable;
	}
	
	@SuppressWarnings("unchecked")
	private void setParametersTable(){
		int argIndex = 1;
		
		for (Iterator<Element> iterator = 
			selectedPlanner.getChild("settings")
				.getChild("arguments").getChildren().iterator(); 
			iterator.hasNext();) {
			Element argument = iterator.next();
			Vector<String> rowData = new Vector<String>();
			
			rowData.add(String.valueOf(argIndex++));
			rowData.add(argument.getAttributeValue("parameter"));
			rowData.add(argument.getChildText("description"));
			rowData.add(argument.getChildText("enable"));
			rowData.add(argument.getChildText("value"));
			
			tableModel.addRow(rowData);
		}
		
		// set platform check boxes
		if(selectedPlanner.getChild("platform").getChild("windows") != null)
			windowsCheck.setSelected(true);
		if(selectedPlanner.getChild("platform").getChild("linux") != null)
			linuxCheck.setSelected(true);
		if(selectedPlanner.getChild("platform").getChild("mac") != null)
			macCheck.setSelected(true);

        // timeout
        // individual timeout values
        individualTimeOutCheck.setEnabled(true);
        individualTimeOutValue.setEnabled(true);
        //System.out.print(selectedPlanner.getChild("settings").getChild("timeout").getAttributeValue("enabled"));
	individualTimeOutCheck.setSelected(selectedPlanner.getChild("settings").getChild("timeout").getAttributeValue("enabled").equals("true"));
        individualTimeOutValue.setText(selectedPlanner.getChild("settings").getChildText("timeout"));

        //favorites
        favoriteCheck.setEnabled(true);
        favoriteCheck.setSelected(selectedPlanner.getChild("settings").getChild("runAllComparison").getAttributeValue("enabled").equals("true"));


	}
	
	private JPanel getPlatformPanel(){
		
		FormLayout layout = new FormLayout(
				"pref, 4dlu, pref, 2dlu, pref, 8dlu, pref, 2dlu pref, 8dlu, pref, 2dlu, pref", "pref");
		
		JPanel osPanel = new JPanel(layout);
		
		JLabel platformLabel = new JLabel("Platform");		
		JLabel windowsLabel = new JLabel("Windows");
		windowsCheck = new JCheckBox();
		JLabel linuxLabel = new JLabel("Linux");
		linuxCheck = new JCheckBox();
		JLabel macLabel = new JLabel("Mac OS");
		macCheck = new JCheckBox();
		
		CellConstraints cc = new CellConstraints();
		
		platformLabel.setEnabled(false);
		osPanel.add(platformLabel, cc.xy (1, 1));
		windowsCheck.setEnabled(false);
		osPanel.add(windowsCheck, cc.xy(3, 1));	
		windowsLabel.setEnabled(false);
		osPanel.add(windowsLabel, cc.xy(5, 1));
		
		linuxCheck.setEnabled(false);
		osPanel.add(linuxCheck, cc.xy(7, 1));		
		linuxLabel.setEnabled(false);
		osPanel.add(linuxLabel, cc.xy(9, 1));
		
		macCheck.setEnabled(false);
		osPanel.add(macCheck, cc.xy(11, 1));		
		macLabel.setEnabled(false);
		osPanel.add(macLabel, cc.xy(13, 1));
		
		return osPanel;
	}

    private JPanel getTimeOutPanel(){

        //master timeout

        FormLayout layout = new FormLayout(
        "left:30px,left:75px,center:90px,right:55,left:80px", // columns
        "center:25px,center:25px"); // rows

		JPanel timeOutPanel = new JPanel(layout);

		JLabel timeOutLabel = new JLabel("Timeout:");
		timeOutCheck = new JCheckBox("Enabled");
        //timeOutCheck.addItemListener(this);
		JLabel timeLabel = new JLabel("Time (s)");
        timeOutValue = new JTextField(50);
        JTextFieldFilter filter = new JTextFieldFilter(JTextFieldFilter.FLOAT);
		filter.setNegativeAccepted(false);
		//filter.setLimit(3);
		timeOutValue.setDocument(filter);
		timeOutValue.setColumns(9);
        timeOutValue.addKeyListener(this);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (timeOutCheck.isSelected() == true){
                    Element itPlannersRoot = ItSIMPLE.getItPlanners();
                    itPlannersRoot.getChild("settings").getChild("timeout").setAttribute("enabled", "true");
                }
                else{
                    Element itPlannersRoot = ItSIMPLE.getItPlanners();
                    itPlannersRoot.getChild("settings").getChild("timeout").setAttribute("enabled", "false");
                }
            }
        };
        timeOutCheck.addActionListener(actionListener);

        //timeOutCheck.setHorizontalTextPosition(SwingConstants.LEFT);
	CellConstraints cc = new CellConstraints();
	timeOutPanel.add(timeOutLabel, cc.xy (2, 1));
        timeOutPanel.add(timeOutCheck, cc.xy(3, 1));
        timeOutPanel.add(timeLabel, cc.xy(4, 1));
        timeOutPanel.add(timeOutValue, cc.xy(5, 1));






        Element itPlannersRoot = ItSIMPLE.getItPlanners();
        // master timeout values
	timeOutCheck.setSelected(itPlannersRoot.getChild("settings").getChild("timeout").getAttributeValue("enabled").equals("true"));
        timeOutValue.setText(itPlannersRoot.getChild("settings").getChildText("timeout"));

        
		return timeOutPanel;
	}

    private JPanel getIndividualTimeoutPanel() {
        FormLayout layout = new FormLayout(
        "left:140px,center:180px,right:55,left:80px", // columns
        "center:25px"); // rows

		JPanel timeOutPanel = new JPanel(layout);

		JLabel timeOutLabel = new JLabel("Individual timeout:");

		individualTimeOutCheck = new JCheckBox("Use specific timeout");
        //individualTimeOutCheck.addItemListener(this);
        
        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedPlanner != null){
                    if (individualTimeOutCheck.isSelected() == true){
                        selectedPlanner.getChild("settings").getChild("timeout").setAttribute("enabled", "true");
                    }
                    else{
                        selectedPlanner.getChild("settings").getChild("timeout").setAttribute("enabled", "false");
                    }
                }
            }
        };
        individualTimeOutCheck.addActionListener(actionListener);


		JLabel timeLabel = new JLabel("Time (s)");
        individualTimeOutValue = new JTextField(30);
        JTextFieldFilter filter = new JTextFieldFilter(JTextFieldFilter.FLOAT);
		filter.setNegativeAccepted(false);
		//filter.setLimit(3);
		individualTimeOutValue.setDocument(filter);
		individualTimeOutValue.setColumns(9);
        individualTimeOutValue.addKeyListener(this);


        //individualTimeOutCheck.setHorizontalTextPosition(SwingConstants.LEFT);
	CellConstraints cc = new CellConstraints();
	timeOutPanel.add(timeOutLabel, cc.xy (1, 1));
        timeOutPanel.add(individualTimeOutCheck, cc.xy(2, 1));
        timeOutPanel.add(timeLabel, cc.xy(3, 1));
        timeOutPanel.add(individualTimeOutValue, cc.xy(4, 1));

        individualTimeOutCheck.setEnabled(false);
        individualTimeOutValue.setEnabled(false);

        return timeOutPanel;
    }



    private JPanel getFavoritePanel() {
        FormLayout layout = new FormLayout(
        "left:90px,center:90px", // columns
        "center:25px"); // rows

	JPanel favoritePanel = new JPanel(layout);

	JLabel favoriteLabel = new JLabel("Favorites:");

	favoriteCheck = new JCheckBox("included");
        //favoriteCheck.addItemListener(this);

        ActionListener actionListener = new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (selectedPlanner != null){
                    if (favoriteCheck.isSelected() == true){
                        selectedPlanner.getChild("settings").getChild("runAllComparison").setAttribute("enabled", "true");
                    }
                    else{
                        selectedPlanner.getChild("settings").getChild("runAllComparison").setAttribute("enabled", "false");
                    }
                }
            }
        };
        favoriteCheck.addActionListener(actionListener);


        //favoriteCheck.setHorizontalTextPosition(SwingConstants.LEFT);
	CellConstraints cc = new CellConstraints();
	favoritePanel.add(favoriteLabel, cc.xy (1, 1));
        favoritePanel.add(favoriteCheck, cc.xy(2, 1));

        favoriteCheck.setEnabled(false);

        return favoritePanel;
    }



    @Override
    public void itemStateChanged(ItemEvent e) {
		if(e.getSource() == plannersCombo && e.getStateChange() == ItemEvent.SELECTED){
			// clear the table
			tableModel.setRowCount(0);
			
			// clear check boxes
			windowsCheck.setSelected(false);
			linuxCheck.setSelected(false);
                        macCheck.setSelected(false);
                        //clear timeout
                        individualTimeOutCheck.setSelected(false);
                        individualTimeOutValue.setText("");
                        individualTimeOutCheck.setEnabled(false);
                        individualTimeOutValue.setEnabled(false);

                        //clear favorites
                        favoriteCheck.setSelected(false);
                        favoriteCheck.setEnabled(false);


		
			int selectedIndex = plannersCombo.getSelectedIndex();
			if(selectedIndex > 0){// discard "Select" item
				// get the xml node for the planner
				selectedPlanner = planners.get(selectedIndex - 1);
				setParametersTable();
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
				Element argument = (Element) selectedPlanner.getChild(
						"settings").getChild("arguments").getChildren()
						.get(row);
				switch (col) {
				case 1://parameter
					argument.setAttribute("parameter", value);
					break;
				case 2://description
					argument.getChild("description").setText(value);
					break;
				case 3://enable
					argument.getChild("enable").setText(value);
					break;
				case 4://default value
					argument.getChild("value").setText(value);
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
        if(e.getSource() == timeOutValue){
            //set master timeout
            Element itPlannersRoot = ItSIMPLE.getItPlanners();
            itPlannersRoot.getChild("settings").getChild("timeout").setText(timeOutValue.getText());
		}
        else if(e.getSource() == individualTimeOutValue){
            if (selectedPlanner !=null){
                //set individual timeout
                selectedPlanner.getChild("settings").getChild("timeout").setText(individualTimeOutValue.getText());
            }

        }
    }




}
