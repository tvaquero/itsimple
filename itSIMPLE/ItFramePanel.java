/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007,2008 Universidade de Sao Paulo
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
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

public class ItFramePanel extends JPanel implements PropertyChangeListener{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1068211642187798644L;

	
	private static ImageIcon MINIMIZE_BUTTON_ICON = new ImageIcon("resources/images/arrowDown.png");
	private static ImageIcon MAXIMIZE_BUTTON_ICON = new ImageIcon("resources/images/arrowUp.png");
	private static ImageIcon PRESS_MINIMIZE_BUTTON_ICON = new ImageIcon("resources/images/arrowDown.png");
	private static ImageIcon PRESS_MAXIMIZE_BUTTON_ICON = new ImageIcon("resources/images/arrowUp.png");
	private static Color TITLE_BAR_BG_COLOR = new Color(151,151,157);
	private static int TITLE_BAR_HEIGHT = 21;
	
	public static int MINIMIZE_MAXIMIZE = 0;
	public static int NO_MINIMIZE_MAXIMIZE = 1;
	
	private int mode;	  
	
	private ImagePanel titlePanel;
	private JPanel buttonPanel;
	private JPanel buttonWrapperPanel;
	private JScrollPane infoScrollPane;
	private int titleBarHeight = TITLE_BAR_HEIGHT;
	private JLabel titleLabel;	
	private JSplitPane parentSplitPane = null;
	private ItFramePanelButton minMaxButton;
	private int contentSize = 50;
	private boolean first = true;
	private boolean minimized = false;
	
	public ItFramePanel() {
	    this("", MINIMIZE_MAXIMIZE);
	  }

	  public ItFramePanel(String title, int mode) {
	    this(title, null, mode);
	  }

	  public ItFramePanel(String title, ImageIcon frameIcon, int mode) {	    
		  super.setLayout(new BorderLayout());
		  this.mode = mode;
		  createTitleBar();		  
		  // TODO set icon
		  titleLabel.setText("<html><font color=white><b>&nbsp&nbsp;" + title + "</b></font></html>");
		  add(titlePanel, BorderLayout.NORTH);
		  
	  }
	  protected void createTitleBar() {
		  
		    Image img = new ImageIcon("resources/images/grayheader2.png").getImage();
		    //titlePanel = new JPanel() {		    
		    titlePanel = new ImagePanel(img) {
		      /**
				 * 
				 */
				private static final long serialVersionUID = -948066492361091787L;

			public Dimension getPreferredSize() {
		        return new Dimension(getWidth(), 
		          titleBarHeight);
		      }
		    };
		    titlePanel.setLayout(new BorderLayout());
		    titlePanel.setOpaque(true);
		    titlePanel.setBackground(TITLE_BAR_BG_COLOR);

		    titleLabel = new JLabel();
		    titleLabel.setForeground(Color.black);
		    titlePanel.add(titleLabel, BorderLayout.CENTER);
		    
		    if(mode == MINIMIZE_MAXIMIZE){
		    	minMaxButton = new ItFramePanelButton(MINIMIZE_BUTTON_ICON);
		    	minMaxButton.setPressedIcon(PRESS_MINIMIZE_BUTTON_ICON);
		    	minMaxButton.setFocusable(false);
		    	minMaxButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
		    	minMaxButton.addActionListener(new ActionListener() {
			    	public void actionPerformed(ActionEvent e) {
			    		if(minimized){
				    		maximize();				    		
			    		}else{
				    		minimize();	
			    		}

			    	}
			    });
			    
			    buttonWrapperPanel = new JPanel();
			    buttonWrapperPanel.setOpaque(false);
			    buttonPanel = new JPanel(new GridLayout(1,3));
			    buttonPanel.setOpaque(false);
			    buttonPanel.add(minMaxButton);
			    buttonPanel.setAlignmentX(0.5f);
			    buttonPanel.setAlignmentY(0.5f);
			    buttonWrapperPanel.add(buttonPanel);
			    titlePanel.add(buttonWrapperPanel, BorderLayout.EAST); 
		    }
		}

	  
	  public void setContent(JComponent component, boolean hasScroll){		  
		  if(hasScroll){
			  infoScrollPane = new JScrollPane();
			  infoScrollPane.setViewportView(component);
			  add(infoScrollPane, BorderLayout.CENTER);
		  }
		  else{
			  add(component, BorderLayout.CENTER);
		  }
	  }
	  
	  public void setParentSplitPane(JSplitPane parentSplitPane){
		  this.parentSplitPane = parentSplitPane;
		  if(mode == MINIMIZE_MAXIMIZE){
			  this.parentSplitPane.addPropertyChangeListener(JSplitPane.DIVIDER_LOCATION_PROPERTY, this);
		  }
	  }
	  
	  // custom button class for title bar
	  private class ItFramePanelButton extends JButton 
	  {
	    /**
		 * 
		 */
		private static final long serialVersionUID = 2630578175294290708L;
		Dimension dim;

	    public ItFramePanelButton(ImageIcon ii) {
	      super(ii);
	      dim = new Dimension(ii.getIconWidth(), ii.getIconHeight());
	      setOpaque(false);
	      setContentAreaFilled(false);
	      setBorder(null);
	    }

	    public Dimension getPreferredSize() {
	      return dim;
	    }

	    public Dimension getMinimumSize() {
	      return dim;
	    }

	    public Dimension getMaximumSize() {
	      return dim;
	    }
	  }
	  
	  public int getTitleBarHeight(){
		  return titleBarHeight;
	  }

	public void propertyChange(PropertyChangeEvent arg0) {
		if(first){
			first = false;
		}
		else{
			int height = parentSplitPane.getParent().getSize().height;			
			if(height - parentSplitPane.getDividerLocation() > parentSplitPane.getDividerSize() + titleBarHeight + 3){
				minMaxButton.setIcon(MINIMIZE_BUTTON_ICON);
				minMaxButton.setPressedIcon(PRESS_MINIMIZE_BUTTON_ICON);
				minimized = false;
			}
		}
	}
	
	public void minimize(){
		if(mode == MINIMIZE_MAXIMIZE){
			int height = parentSplitPane.getSize().height;
			contentSize = height - parentSplitPane.getDividerLocation();			
			parentSplitPane.setDividerLocation(height - titleBarHeight - parentSplitPane.getDividerSize());			
			minMaxButton.setIcon(MAXIMIZE_BUTTON_ICON);
			minMaxButton.setPressedIcon(PRESS_MAXIMIZE_BUTTON_ICON);
			minimized = true;
		}
	}
	
	public void maximize(){
		if(mode == MINIMIZE_MAXIMIZE){
			int height = parentSplitPane.getParent().getSize().height;
			parentSplitPane.setDividerLocation(height - contentSize);			
			minMaxButton.setIcon(MINIMIZE_BUTTON_ICON);
			minMaxButton.setPressedIcon(PRESS_MINIMIZE_BUTTON_ICON);
    		minimized = false;
		}
	}

	public void setTitle(String title){
		 titleLabel.setText("<html><font color=white><b>&nbsp&nbsp;" + title + "</b></font></html>");
	}

}
