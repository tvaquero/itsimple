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

import java.awt.Graphics;
import java.awt.Image;

import javax.swing.ImageIcon;
import javax.swing.JPanel;


class ImagePanel extends JPanel {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3906814017130894231L;
	private Image img = null;

	  public ImagePanel() {
		  super();		  
	  }	  
	  
	  public ImagePanel(String imgStr) {
		  this(new ImageIcon(imgStr).getImage());		  
	  }

	  public ImagePanel(Image img) {
		super();  
	    this.img = img;
	    /*Dimension size = new Dimension(img.getWidth(null), img.getHeight(null));
	    setPreferredSize(size);
	    setMinimumSize(size);
	    setMaximumSize(size);
	    setSize(size);
	    setLayout(null);*/
	  }

	  public void paintComponent(Graphics g) {
		  if (img != null){
			  g.drawImage(img, 0, 0, this.getWidth(), this.getHeight(), this);  
		  }
		  else{
			  super.paintComponent(g);
		  }

	  }

	}

