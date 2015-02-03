/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2013 University of Sao Paulo
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

import java.awt.Insets;
import java.awt.LayoutManager;

import javax.swing.JPanel;

public class ItPanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2343057519108007232L;

	public ItPanel(LayoutManager arg0, boolean arg1) {
		super(arg0, arg1);
		// TODO Auto-generated constructor stub
	}

	public ItPanel(LayoutManager arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ItPanel(boolean arg0) {
		super(arg0);
		// TODO Auto-generated constructor stub
	}

	public ItPanel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public Insets getInsets(){
		return new Insets(2,2,2,2);
	}

}
