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

package itSIMPLE;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JToolBar;
import languages.xml.XMLUtilities;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Element;

/**
 *
 * @author tiago
 */
public class TimingDiagramPanel extends JPanel {

	private Element diagram = null;
	private Element project = null;
    private Element lifelines = null;
    private JToolBar mainToolBar = null;
    private JPanel lifelineMainPanel = null;
    private List<LifelinePanel> lifelinePanelList = null;
	private JTextField durationTimingDiagramField = null;


	public TimingDiagramPanel(Element timingDiagram, Element project) {
        super();
        this.setLayout(new BorderLayout());
	    this.diagram = timingDiagram;
        this.project = project;

        //main tool bar
        mainToolBar = new JToolBar();
        this.add(mainToolBar, BorderLayout.NORTH);
        setMainToolBar();
        //lifeline panel
        lifelineMainPanel = new JPanel();
        lifelineMainPanel.setLayout(new BoxLayout(lifelineMainPanel, BoxLayout.Y_AXIS));
        lifelineMainPanel.setBackground(Color.WHITE);
        JScrollPane listScrollPane = new JScrollPane(lifelineMainPanel);
        listScrollPane.setBackground(Color.WHITE);
        this.add(listScrollPane, BorderLayout.CENTER);
        buildTimingDiagram();
	}


    /**
     * this method sets the main toolbar for the timing diagram
     */
    public void setMainToolBar(){

        //add a new lifeline
        JButton button = new JButton("New lifeline",new ImageIcon("resources/images/new.png"));
        button.setAction(newLifeLine);
        mainToolBar.add(button);
        //button.setBorderPainted(false);
        button.setToolTipText("Add new lifeline to the diagram.");

        mainToolBar.addSeparator();


        //DURATION (Lifeline duration)
        mainToolBar.add(new JLabel("Duration: "));

        durationTimingDiagramField = new JTextField();
        //durationTimingDiagramField.addKeyListener(this);
        durationTimingDiagramField.setText(diagram.getChild("frame").getChildText("duration"));
        durationTimingDiagramField.setToolTipText("Press 'Enter' to update the time range in the diagram.");
        //durationTimingDiagramField.setSize(100, 15);
        durationTimingDiagramField.setPreferredSize(new Dimension(100,25));
        durationTimingDiagramField.addKeyListener(new KeyListener() {

            public void keyTyped(KeyEvent e) {

            }

            public void keyPressed(KeyEvent e) {
//                //if (!durationTimingDiagramField.getText().trim().equals("")){
//                    //diagram.getChild("frame").getChild("duration").setText(durationTimingDiagramField.getText());
//                    if (e.getKeyCode() == KeyEvent.VK_ENTER){
//                        refreshTimelineDurationForAllLifelines();
//                    }
//                //}
    			
            }

            public void keyReleased(KeyEvent e) {
                //if (!durationTimingDiagramField.getText().trim().equals("")){
                    diagram.getChild("frame").getChild("duration").setText(durationTimingDiagramField.getText());
                    if (e.getKeyCode() == KeyEvent.VK_ENTER){
                        refreshTimelineDurationForAllLifelines();
                    }
                //}

            }
        });
        mainToolBar.add(durationTimingDiagramField);

        mainToolBar.add(refreshAll);

    }


/**
     * this method created and set the graph for showing the timing diagram based
     * on the variable diagram
     */
    public void buildTimingDiagram(){

        //1. get type and context
        String dtype = diagram.getChildText("type");
        String context = diagram.getChildText("context");
        //the frame and lifeline nodes
        Element frame = diagram.getChild("frame");
        lifelines = frame.getChild("lifelines");
        String durationStr = frame.getChildText("duration");


        lifelinePanelList =  new ArrayList<LifelinePanel>();


        for (Iterator<Element> it = lifelines.getChildren("lifeline").iterator(); it.hasNext();) {
            Element lifeline = it.next();
            LifelinePanel lifelineGraph = new LifelinePanel(lifeline, diagram, project, this);
            lifelineMainPanel.add(lifelineGraph);
            lifelinePanelList.add(lifelineGraph);

        }

    }

    public void removeLifeline(LifelinePanel lifelinepanel){
        Element lifeline = lifelinepanel.getData();
        lifelines.removeContent(lifeline);
        lifelinePanelList.remove(lifelinepanel);
        lifelineMainPanel.remove(lifelinepanel);
        lifelineMainPanel.repaint();
        lifelineMainPanel.revalidate();
        this.repaint();

    }

    public void addLifeline(Element lifeline){
        lifelines.addContent(lifeline);
        LifelinePanel lifelineGraph = new LifelinePanel(lifeline, diagram, project, TimingDiagramPanel.this);
        lifelinePanelList.add(lifelineGraph);
        lifelineMainPanel.add(lifelineGraph);
        lifelineMainPanel.repaint();
        lifelineMainPanel.revalidate();
        TimingDiagramPanel.this.repaint();

    }

    public void refreshTimelineDurationForAllLifelines(){

        for (Iterator<LifelinePanel> it = lifelinePanelList.iterator(); it.hasNext();) {
            LifelinePanel lifelinePanel = it.next();
            lifelinePanel.refreshLifeline();
        }
    }


 

	private Action newLifeLine = new AbstractAction("New lifeline", new ImageIcon("resources/images/new.png")){
		/**
		 *
		 */
		private static final long serialVersionUID = -2820018622516075618L;


        public void actionPerformed(ActionEvent e) {
            Element lifeline = (Element)ItSIMPLE.getCommonData().getChild("definedNodes").getChild("elements")
										.getChild("model").getChild("lifeline").clone();
            String id = String.valueOf(XMLUtilities.getId(lifelines));
			lifeline.setAttribute("id", id);

            //1. get type and context
            String dtype = diagram.getChildText("type");
            String context = diagram.getChildText("context");
            boolean canOpenDialog = false;

            if (dtype.equals("")){
                JOptionPane.showMessageDialog(TimingDiagramPanel.this,"Please, choose the type of the timing diagram!");
            }
            else if (context.equals("")){
                JOptionPane.showMessageDialog(TimingDiagramPanel.this,"Please, choose the context of the timing diagram!");
            }

            //Check if the minimal required data is available for creating a lifeline
            if (dtype.equals("condition")){
                if (context.equals("action")){
                    //check if the action was defined
                    Element action = diagram.getChild("action");
                    if (!action.getAttributeValue("class").equals("") && !action.getAttributeValue("id").equals("")){
                        canOpenDialog = true;
                    }
                    else{
                        JOptionPane.showMessageDialog(TimingDiagramPanel.this,"Please, choose the operator/action!");
                    }

                }
            }
            if (canOpenDialog){
                NewLifelineDialog dialog = new NewLifelineDialog(diagram, lifeline,  TimingDiagramPanel.this);
                dialog.setVisible(true);
            }

        }
	};

    private Action refreshAll = new AbstractAction("Refresh all lifelines", new ImageIcon("resources/images/refresh.png")){
            /**
             *
             */

            public void actionPerformed(ActionEvent e) {
                refreshTimelineDurationForAllLifelines();
            }
        };




}
