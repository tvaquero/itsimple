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

// SplashScreen.java
//A simple application to show a title screen in the center of the screen
//for the amount of time given in the constructor. This class includes
//a sample main() method to test the splash screen, but it's meant for use
//with other applications.
//

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JWindow;


public class SplashScreen extends JWindow implements Runnable {
  /**
	 * 
	 */
	private static final long serialVersionUID = -7720533134526695367L;
	private int duration;
	private Image splash;
    int width;
    int height;
    
    public SplashScreen(int d) {
	    duration = d;
	    setAlwaysOnTop(true);
	    addMouseListener(new MouseAdapter(){
	    	public void mouseClicked(MouseEvent e){    		
    		setVisible(false);
    		dispose();
    	}
    });
  }

  // A simple little method to show a title screen in the center
  // of the screen for the amount of time given in the constructor
    public void showSplash() {
	   
	    ImageIcon splashImage = new ImageIcon("resources/images/Splash.png");
	    splash = splashImage.getImage();
	    width = splashImage.getIconWidth();
	    height = splashImage.getIconHeight();    
	    
	    // Set the window's bounds, centering the window
	    Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
	    int x = (screen.width - width) / 2;
	    int y = (screen.height - height) / 2;
	    setBounds(x, y, width, height);
	
	    // Display it
	    setVisible(true);
	    
	    // wait
	    pauseExec(duration);
	    
	    // close it
	    setVisible(false);
    }

	  public void showSplashAndExit() {
	    showSplash();
	    System.exit(0);
	  }  
  
 
	 private void pauseExec(long dur){
			try{
				Thread.sleep(dur);
			}
			catch (InterruptedException ie){}
	 }


	public void paint(Graphics g) {	
	    g.drawImage(splash,0,0,width, height, this);
	}

	public void run() {
		showSplash();	
	}
}
