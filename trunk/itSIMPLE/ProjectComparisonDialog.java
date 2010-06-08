/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2009 Universidade de Sao Paulo
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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.DefaultListModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import javax.swing.SpringLayout;

import languages.xml.XMLUtilities;
import org.jdom.Element;
import util.filefilter.XMLFileFilter;

/**
 * This class is the diolag for gathering the project files to be compared
 * @author tiago
 */

public class ProjectComparisonDialog extends JDialog {

	/**
	 * 
	 */
    private static final long serialVersionUID = -6974543794597743033L;

    private List<String> files = null;

    private JTextField baseFileName;

    private JToolBar projectFilesToolBar = null;
	private JList comparableProjectsList = null;
	private DefaultListModel comparableProjectsListModel = null;
	private ArrayList<String> currentComparableProjects = null;


	
	//public ProjectComparisonDialog(List filesList){
    public ProjectComparisonDialog(){
		super(ItSIMPLE.getItSIMPLEFrame());
		setTitle("Project DataSet Comparison");
		setModal(true);

        files = new ArrayList<String>();
        //this.files = filesList;
		
		
		setSize(500,400);
		setLocation(200,200);
		add(getMainPanel());
	}
        
	
	private JPanel getMainPanel(){
		JPanel main = new JPanel(new BorderLayout());
		
		JPanel top = new JPanel(new SpringLayout());
		top.add(new JLabel("Base Project: "));
		baseFileName = new JTextField();
        //baseFileName.setEditable(false);
		top.add(baseFileName);
        JButton mainProjectButton = new JButton("Browser");
		mainProjectButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                //collect the filename and close
                String lastOpenFolder = "";
                Element lastOpenFolderElement = ItSIMPLE.getItSettings().getChild("generalSettings").getChild("lastOpenFolder");
                if (lastOpenFolderElement != null){
                    lastOpenFolder = lastOpenFolderElement.getText();
                }
                JFileChooser fc = new JFileChooser(lastOpenFolder);
                fc.setDialogTitle("Open Project");
                fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
                fc.setFileFilter(new XMLFileFilter());
                int returnVal = fc.showOpenDialog(ProjectComparisonDialog.this);
                if (returnVal == JFileChooser.APPROVE_OPTION){
                    File file = fc.getSelectedFile();

                    baseFileName.setText(file.getAbsolutePath());

                    if (lastOpenFolderElement != null){
                        //Holds the last open folder
                        if (!lastOpenFolderElement.getText().equals(file.getParent())){
                            lastOpenFolderElement.setText(file.getParent());
                            XMLUtilities.writeToFile("resources/settings/itSettings.xml", ItSIMPLE.getItSettings().getDocument());
                        }
                    }
                }

			}
		});
        top.add(mainProjectButton);

		SpringUtilities.makeCompactGrid(top, 1, 3, 5, 5, 5, 5);


        ItPanel center = new ItPanel(new BorderLayout());

        center.add(new JLabel("Project to compare with:"), BorderLayout.NORTH);
		//Project files tree
        comparableProjectsListModel = new DefaultListModel();
		comparableProjectsList = new JList(comparableProjectsListModel);
		ItListRenderer renderer = new ItListRenderer();
        renderer.setIcon(new ImageIcon("resources/images/operator.png"));
		comparableProjectsList.setCellRenderer(renderer);
        currentComparableProjects = new ArrayList<String>();

        JScrollPane scrollText = new JScrollPane();
		scrollText.setViewportView(comparableProjectsList);
        center.add(scrollText, BorderLayout.CENTER);

        //tool bar with buttons
        projectFilesToolBar = new JToolBar();
		projectFilesToolBar.add(addProject).setToolTipText("Add Project");
		projectFilesToolBar.add(deleteProject).setToolTipText("Delete project from list");

		center.add(projectFilesToolBar, BorderLayout.SOUTH);

        
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        //The OK button at the bottom
        JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                //collect the filename and close
                files.clear();

                String  baseFile = baseFileName.getText();

                if (!baseFile.trim().equals("")){
                    files.add(baseFile);
                }else{
                    JOptionPane.showMessageDialog(ProjectComparisonDialog.this,"Please, choose the base project.");
                    return;
                }


                if (currentComparableProjects.size() > 0){
                    for (int i = 0; i < currentComparableProjects.size(); i++) {
                        String eaProjectFile = currentComparableProjects.get(i);
                        files.add(eaProjectFile);
                    }
                }else{
                    JOptionPane.showMessageDialog(ProjectComparisonDialog.this,"Please, choose at least one project to compare with.");
                    return;
                }
                

                if (!baseFile.trim().equals("") && files.size() > 1){
                   dispose();
                }

//                files.add("/home/tiago/Desktop/Experiments/GoldMiner/Original/DataSet GoldMiner Original.xml");
//                files.add("/home/tiago/Desktop/Experiments/GoldMiner/Adj A/DataSet GoldMiner Adj A.xml");
//                files.add("/home/tiago/Desktop/Experiments/GoldMiner/Adj B/DataSet GoldMiner Adj B.xml");
//                files.add("/home/tiago/Desktop/Experiments/GoldMiner/Adj AB/DataSet GoldMiner Adj AB.xml");
				
			}
		});
        buttonPanel.add(okButton);

        //The Cancel button at the bottom
        JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
                files.clear();
				dispose();
			}
		});
		buttonPanel.add(cancelButton);

	
		main.add(top, BorderLayout.NORTH);
		main.add(center, BorderLayout.CENTER);
        main.add(buttonPanel, BorderLayout.SOUTH);
		
		return main;
	}

    public List<String> getFiles() {
        return files;
    }
    


    //Actions

    /**
     * This action adds a new preference function domain
     */
    private Action addProject = new AbstractAction("New",new ImageIcon("resources/images/add.png")){

        public void actionPerformed(ActionEvent e) {

            String lastOpenFolder = "";
			Element lastOpenFolderElement = ItSIMPLE.getItSettings().getChild("generalSettings").getChild("lastOpenFolder");
			if (lastOpenFolderElement != null){
				lastOpenFolder = lastOpenFolderElement.getText();
			}
			JFileChooser fc = new JFileChooser(lastOpenFolder);
			fc.setDialogTitle("Open Project");
			fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
			fc.setFileFilter(new XMLFileFilter());
			int returnVal = fc.showOpenDialog(ProjectComparisonDialog.this);
			if (returnVal == JFileChooser.APPROVE_OPTION){
				File file = fc.getSelectedFile();

                comparableProjectsListModel.addElement(file.getName());
                currentComparableProjects.add(file.getAbsolutePath());
                comparableProjectsList.setSelectedIndex(comparableProjectsListModel.size()-1);

				if (lastOpenFolderElement != null){
					//Holds the last open folder
					if (!lastOpenFolderElement.getText().equals(file.getParent())){
						lastOpenFolderElement.setText(file.getParent());
						XMLUtilities.writeToFile("resources/settings/itSettings.xml", ItSIMPLE.getItSettings().getDocument());
					}
				}
			}


           
            

		}
	};

        /**
         * This action deletes a preference function domain
         */
	private Action deleteProject = new AbstractAction("Delete",new ImageIcon("resources/images/delete.png")){

		public void actionPerformed(ActionEvent e) {
			int row = comparableProjectsList.getSelectedIndex();
			if (row > -1){
                currentComparableProjects.remove(row);
				comparableProjectsListModel.removeElementAt(row);
                comparableProjectsList.setSelectedIndex(comparableProjectsListModel.size()-1);

			}
		}
	};


}
