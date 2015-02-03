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

package src.gui;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.UIManager;
import javax.swing.plaf.basic.BasicTabbedPaneUI;

public class ClosingTabbedPaneUI extends BasicTabbedPaneUI{
	
	private final static ImageIcon mouseExitedIcon = new ImageIcon("resources/images/MouseExitedCloseButton.gif");
	private final static ImageIcon mouseEnteredIcon = new ImageIcon("resources/images/MouseEnteredCloseButton.gif");	
	private final static Color SELECTED_TAB_COLOR = new Color(10, 36, 106);
	
	//public static boolean visible = false;
	
//	override to return our layoutmanager 
	protected LayoutManager createLayoutManager(){ 
		return new TabLayout(); 
	} 
	//add 40 to the tab size to allow room for the close button and 8 to the height 
	protected Insets getTabInsets(int tabPlacement,int tabIndex){ 
		//note that the insets that are returned to us are not copies. 
		Insets defaultInsets = (Insets)super.getTabInsets(tabPlacement,tabIndex).clone(); 
		defaultInsets.right += 40; 
		defaultInsets.top += 4; 
		defaultInsets.bottom += 4; 
		return defaultInsets; 
	}
	
	class TabLayout extends TabbedPaneLayout {
		//a list of our close buttons 
		java.util.ArrayList closeButtons = new java.util.ArrayList(); 
		@SuppressWarnings("unchecked")
		public void layoutContainer(Container parent){ 
			super.layoutContainer(parent);			
			//ensure that there are at least as many close buttons as tabs
			//((J) tab)
			while(tabPane.getTabCount() > closeButtons.size()){ 
				//CloseButton button = new CloseButton(closeButtons.size());
				//button.setVisible(visible);
				closeButtons.add(new CloseButton(closeButtons.size())); 
			} 
			Rectangle rect = new Rectangle(); 
			int i; 
			for(i = 0; i < tabPane.getTabCount();i++){ 
				rect = getTabBounds(i,rect); 
				final JButton closeButton = (JButton)closeButtons.get(i);					
				//shift the close button 3 down from the top of the pane and 20 to the left 
				closeButton.setLocation(rect.x+rect.width-20,rect.y+5); 
				closeButton.setSize(13,13);
				closeButton.setBorder(null);
				closeButton.setBackground(null);
				closeButton.setForeground(null);
				closeButton.setFocusPainted(false);
				closeButton.setRolloverIcon(mouseEnteredIcon);				
				tabPane.add(closeButton);				;
				
				/*tabPane.addMouseListener(new java.awt.event.MouseAdapter() {
					public void mouseEntered(java.awt.event.MouseEvent e) {
						//System.out.println("mouseEntered()"); // TODO Auto-generated Event stub mouseEntered()
						int tabNumber = tabPane.getUI().tabForCoordinate(tabPane, e.getX(), e.getY());
						if (tabNumber < 0) return;
						Rectangle rect=(tabPane.getBoundsAt(tabNumber));//(CloseTabIcon)getIconAt(tabNumber)).getBounds();
					    if (rect.contains(e.getX(), e.getY())) {		      
					    	System.out.println("mouseEntered()");
					    	
					    }
					}
				});*/
			} 
			for(;i < closeButtons.size();i++){ 
				//remove any extra close buttons 
				tabPane.remove((JButton)closeButtons.get(i)); 
			} 
		}
		
		//implement UIResource so that when we add this button to the tabbedpane, it doesn't try to make a tab for it! 
		class CloseButton extends JButton implements javax.swing.plaf.UIResource 
		{
			private static final long serialVersionUID = 6753284468539647472L;

			public CloseButton(int index){ 
				super(new CloseButtonAction(index));
				//remove the typical padding for the button 
				setMargin(new Insets(0,0,0,0));						
			} 
		
		} 
		class CloseButtonAction extends AbstractAction 
		{ 
			/**
			 * 
			 */
			private static final long serialVersionUID = 6177932751565495261L;
			int index; 
			public CloseButtonAction(int index){ 										
				super(null, mouseExitedIcon);					
				this.index = index;					
			} 
			public void actionPerformed(ActionEvent e){ 
				if (tabPane instanceof ItTabbedPane){
					ItTabbedPane closingPane = (ItTabbedPane) tabPane;					
					closingPane.closeTab(index);
				}
				else
					tabPane.remove(index);				
			} 
		} 
	}
	
	// Methods that paint the eclipse colors in the tab
	//@Override
    protected void paintTabBorder(Graphics g, int tabPlacement, int tabIndex, 
            int x, int y, int w, int h, boolean isSelected) {

        g.setColor(Color.GRAY);
      
        if (tabPlacement == BOTTOM) {
            g.drawLine(x, y + h, x + w, y + h);
        }
        
        // right
        g.drawLine(x + w - 1, y, x + w - 1, y + h);
        
        if (tabPlacement == TOP) {
            // And a white line to the left and top
            g.setColor(Color.WHITE);
            g.drawLine(x, y, x, y + h);
            
            g.drawLine(x, y, x + w - 2, y);
        }
        
        if (tabPlacement == BOTTOM && isSelected) {
            g.setColor(Color.WHITE);

            // Top
            g.drawLine(x + 1, y + 1, x + 1, y + h);
            // Right
            g.drawLine(x + w - 2, y, x + w - 2, y + h);
            // Left
            g.drawLine(x + 1, y + 1, x + w - 2, y + 1);
            // Bottom
            g.drawLine(x + 1, y + h - 1, x + w - 2, y + h - 1);
        }
    }

    /**
     * Give selected tab blue color with a gradient!!.
     * 
     * FIXME: with Plastic L&F the unselected background is too dark
     * 
     * @see javax.swing.plaf.basic.BasicTabbedPaneUI#paintTabBackground(java.awt.Graphics, int, int, int, int, int, int, boolean)
     */
    //@Override
    protected void paintTabBackground(Graphics g, int tabPlacement, int tabIndex, 
            int x, int y, int w, int h, boolean isSelected) {
        
        Color color = UIManager.getColor("control");
        
        if (isSelected) {
            if (tabPlacement == TOP) {
                Graphics2D g2 = (Graphics2D)g;
                Paint storedPaint = g2.getPaint();
                g2.setPaint(new GradientPaint(x, y, SELECTED_TAB_COLOR, x + w, y + h, color));
                g2.fillRect(x, y, w, h);
                g2.setPaint(storedPaint);
            }
        } else {
            g.setColor(color);
            g.fillRect(x, y, w - 1, h);
        }
    }

    /**
     * Do not paint a focus indicator.
     * 
     * @see javax.swing.plaf.basic.BasicTabbedPaneUI#paintFocusIndicator(java.awt.Graphics, int, java.awt.Rectangle[], int, java.awt.Rectangle, java.awt.Rectangle, boolean)
     */
    //@Override
    protected void paintFocusIndicator(Graphics arg0, int arg1, Rectangle[] arg2, int arg3, Rectangle arg4, Rectangle arg5, boolean arg6) {
        // Leave it
    }

    /**
     * We do not want the tab to "lift up" when it is selected.
     * 
     * @see javax.swing.plaf.basic.BasicTabbedPaneUI#installDefaults()
     */
    //@Override
    protected void installDefaults() {
        super.installDefaults();

        tabAreaInsets = new Insets(0, 0, 0, 0);
        selectedTabPadInsets = new Insets(0, 0, 0, 0);
        contentBorderInsets = new Insets(1, 0, 0, 0);
    }

    /**
     * Nor do we want the label to move.
     */
    //@Override
    protected int getTabLabelShiftY(int tabPlacement, int tabIndex, boolean isSelected) {

        return 0;
    }

    /**
     * Increase the tab height a bit
     */
    //@Override
    protected int calculateTabHeight(int tabPlacement, int tabIndex, int fontHeight) {
        
        return fontHeight + 10;
    }
    
    //@Override
    protected void layoutLabel(int arg0, FontMetrics arg1, int arg2, String arg3, Icon arg4, Rectangle arg5, Rectangle arg6, Rectangle arg7, boolean arg8) {

        super.layoutLabel(arg0, arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8);
    }

    /**
     * Selected labels have a white color.
     * 
     * @see javax.swing.plaf.basic.BasicTabbedPaneUI#paintText(java.awt.Graphics, int, java.awt.Font, java.awt.FontMetrics, int, java.lang.String, java.awt.Rectangle, boolean)
     */
    //@Override
    protected void paintText(Graphics g, int tabPlacement, Font font, 
            FontMetrics metrics, int tabIndex, String title, Rectangle textRect, 
            boolean isSelected) {
        
        if (isSelected && tabPlacement == TOP) {
            g.setColor(Color.WHITE);
        } else {
            g.setColor(Color.BLACK);
        }
        
        // HACK: Force painting of Tahoma - Plastic L&F renders a big Arial
        Font tabFont = new Font("Tahoma", Font.PLAIN, 11);
            
        g.setFont(tabFont);
        g.drawString(title, textRect.x, textRect.y + metrics.getAscent());
    }        
    
    //@Override
    protected void paintContentBorderTopEdge(Graphics g, int tabPlacement, 
            int selectedIndex, int x, int y, int w, int h) {
        
        if (selectedIndex != -1 && tabPlacement == TOP) {
            g.setColor(Color.GRAY);
            g.drawLine(x, y, x + w, y);
        }
    }

    //@Override
    protected void paintContentBorderBottomEdge(Graphics g, int tabPlacement, 
            int selectedIndex, int x, int y, int w, int h) {

        g.setColor(Color.GRAY);        
        g.drawLine(x, y + h, x + w, y + h);
    }

    //@Override
    protected void paintContentBorderLeftEdge(Graphics g, int tabPlacement, 
            int selectedIndex, int x, int y, int w, int h) {
        // do nothingx, y, x, y + h);
    }

    //@Override
    protected void paintContentBorderRightEdge(Graphics g, int tabPlacement, 
            int selectedIndex, int x, int y, int w, int h) {
        // do nothing
    }	

}
