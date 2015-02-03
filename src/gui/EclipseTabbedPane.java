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

import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JPopupMenu;
import javax.swing.JTabbedPane;



//import nl.boplicity.bt.orderbuilder.workbench.ui.Editor;
//import nl.boplicity.swing.plaf.basic.EclipseTabbedPaneUI;


/**
 * Eclipse style tabbedpane with elipse-look tabs and popup menu on tabs.
 * 
 * FIXME: save dirty tabs first
 * 
 * @author kees
 * @date 9-feb-2006
 *
 */

public class EclipseTabbedPane extends JTabbedPane {
	
	private static final long serialVersionUID = 1176020466013529902L;
    private JPopupMenu popupMenu;
    private int selectedTabIndex;
    
    public EclipseTabbedPane() {
        super();
        setUI(new EclipseTabbedPaneUI());
        
        createPopupMenu();
        
        addMouseListener(new MouseAdapter() {

            //@Override
            public void mouseReleased(MouseEvent mouseEvent) {
                EclipseTabbedPane.this.mouseReleased(mouseEvent);
            }
            
        });
    }
    
    public void mouseReleased(MouseEvent mouseEvent) {
        
        if (mouseEvent.isPopupTrigger()) {
            Integer index = new Integer(indexAtLocation(mouseEvent.getX(), mouseEvent.getY()));
            selectedTabIndex = index.intValue();
         
            // Only show for top row
            if (getTabPlacement() == JTabbedPane.TOP) {
                popupMenu.show(this, mouseEvent.getX(), mouseEvent.getY());
            }
        }
        
    }
    
    private JPopupMenu createPopupMenu() {
        
        popupMenu = new JPopupMenu();
        
        popupMenu.add(new CloseAction("Close"));
        popupMenu.add(new CloseOthersAction("Close Others"));
        popupMenu.add(new CloseAllAction("Close All"));
        
        return popupMenu;
    }
    
    private class CloseAction extends AbstractAction {

        private static final long serialVersionUID = -2625928077474199856L;

        public CloseAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent actionEvent) {
            closeTab(selectedTabIndex);
        }

		
    }
    
    private class CloseOthersAction extends AbstractAction {

        private static final long serialVersionUID = -2625928077474199856L;

        public CloseOthersAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent actionEvent) {
            
            // First remove higher indexes 
            int tabCount = getTabCount();
            
            if (selectedTabIndex < tabCount - 1) {
                for (int i = selectedTabIndex + 1; i < tabCount; i++) {
                    closeTab(selectedTabIndex + 1);
                }
            }
            
            if (selectedTabIndex > 0) {
                for (int i = 0; i < selectedTabIndex; i++) {
                    closeTab(0);
                }
            }
        }

    }

    private class CloseAllAction extends AbstractAction {

        private static final long serialVersionUID = -2625928077474199856L;

        public CloseAllAction(String name) {
            super(name);
        }

        public void actionPerformed(ActionEvent actionEvent) {
            
            int tabCount = getTabCount();
            
            for (int i = 0; i < tabCount; i++) {
                closeTab(0);
            }
        }
    }


	private void closeTab(int index) {
		// TODO Auto-generated method stub
		this.remove(index);		
	}
    
}
