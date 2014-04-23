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

package itGraph.UMLElements;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;

import java.io.Serializable;

import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextPane;

import org.jgraph.JGraph;
import org.jgraph.graph.CellView;
import org.jgraph.graph.CellViewRenderer;
import org.jgraph.graph.GraphConstants;

public class EnumerationRenderer extends JComponent implements Serializable,
		CellViewRenderer {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6891208959000371378L;
	
	private JLabel header = null;	
	private JTextPane literalsPane = null;
	//private ClassView view = null;
	private boolean hasFocus, selected;
	//private JGraph graph;
	
	private Insets insets = new Insets(5, 5, 5, 5);	
	

	public EnumerationRenderer() {
        //defaultForeground = UIManager.getColor("Tree.textForeground");
        //defaultBackground = UIManager.getColor("Tree.textBackground");

        setLayout(new BorderLayout());
        setEnabled(false);

        // The header of the class box
        //ClassCell cell = (ClassCell)graph.getEditingCell();
        Icon icon = null; //centralize the text      
        header = new JLabel(icon)
            {
                /**
				 * 
				 */
				private static final long serialVersionUID = 5452121868761872773L;

				public Insets getInsets ()
                {
                    return insets;
                }
            };
        //header.setLineWrap(false);
        header.setVerticalTextPosition(JLabel.BOTTOM);
        header.setHorizontalTextPosition(JLabel.CENTER);
        header.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        add(header, BorderLayout.NORTH);

        // The attributes area of the class box
        literalsPane = new JTextPane(){
                /**
				 * 
				 */
				private static final long serialVersionUID = 8241372750817029513L;

				public Insets getInsets()
                {
                    return insets;
                }
        };
        literalsPane.setContentType("text/html");
        add(literalsPane, BorderLayout.CENTER);        

	}
	
	
	protected void installAttributes (CellView view){
		
		Map map = view.getAllAttributes();
        EnumerationCell cell = (EnumerationCell) view.getCell();

        // store the fill and the line color
        Color bordercolor = GraphConstants.getBorderColor(map);

        // apply the parameter to the bounding box only
        setOpaque(GraphConstants.isOpaque(map));

        bordercolor = GraphConstants.getBorderColor(map);
        /*if (bordercolor == null)
        {
           bordercolor = GraphConstants.getBorderColor(map);
        }*/
        if (getBorder() == null && bordercolor != null)
        {
            setBorder(BorderFactory.createLineBorder(bordercolor, 1));
        }

        // Set the foreground color
        Color foreground = GraphConstants.getForeground(map);
        //if (foreground == null)
        //{
        //   foreground = defaultForeground;
        //}

        // The FillColor takes precedence over the background color
        Color background = GraphConstants.getBackground(map);
        //if (background == null)
        //{
        //  background = defaultBackground;
        //}
        setBackground(background);

        Font font = GraphConstants.getFont(map);        

        header.setOpaque(false);
        header.setFont(font);
        header.setForeground(foreground);        
        Icon icon = GraphConstants.getIcon(map);
        header.setIcon(icon);        

        literalsPane.setFont(font);
        literalsPane.setOpaque(false);
        //suppressAttribute = UMLGraphConstants.getSuppressAttribute(map);
        literalsPane.setVisible(/*!suppressAttribute*/true);

        //setHTML(literalsPane,cell.getClassAttributes());
        literalsPane.setText(cell.getLiterals());
		
	}
	
/*	public Point2D getPerimeterPoint(VertexView view, Point2D source, Point2D p) {
		Rectangle2D bounds = view.getBounds();
		double x = bounds.getX();
		double y = bounds.getY();
		double width = bounds.getWidth();
		double height = bounds.getHeight();
		double xCenter = x + width / 2;
		double yCenter = y + height / 2;
		double dx = p.getX() - xCenter; // Compute Angle
		double dy = p.getY() - yCenter;
		double alpha = Math.atan2(dy, dx);
		double xout = 0, yout = 0;
		double pi = Math.PI;
		double pi2 = Math.PI / 2.0;
		double beta = pi2 - alpha;
		double t = Math.atan2(height, width);
		if (alpha < -pi + t || alpha > pi - t) { // Left edge
			xout = x;
			yout = yCenter - width * Math.tan(alpha) / 2;
		} else if (alpha < -t) { // Top Edge
			yout = y;
			xout = xCenter - height * Math.tan(beta) / 2;
		} else if (alpha < t) { // Right Edge
			xout = x + width;
			yout = yCenter + width * Math.tan(alpha) / 2;
		} else { // Bottom Edge
			yout = y + height;
			xout = xCenter + height * Math.tan(beta) / 2;
		}
		return new Point2D.Double(xout, yout);
	}*/

	public Component getRendererComponent(JGraph graph, CellView view,
			boolean sel, boolean focus, boolean preview) {
		
		if (view instanceof EnumerationView)
        {
            //this.view = (ClassView) view;
            setComponentOrientation(graph.getComponentOrientation());
            if (graph.getEditingCell() != view.getCell())
            {
                Object label = graph.convertValueToString(view.getCell());
                if (label != null)
                {
                	String text = "<center><font size='4'><b>"+label.toString()+"</b></font></center>";
                    EnumerationCell cell = (EnumerationCell) view.getCell();
                    if (!cell.getData().getChildText("stereotype").equals("")){                    	
                    	text = "<font size='2.5'>&lt;&lt;" + cell.getData().getChildText("stereotype") +
                    		"&gt;&gt;</font><br>" + text;
                    }
                    text = "<html>" + text + "</html>";
                    header.setText(text);
                }
                else
                {
                    header.setText("");
                }
            }
            else
            {
                header.setText("");
            }

            //this.graph = graph;
            this.hasFocus = focus;
            this.selected = sel;
            //this.preview = preview;
            if (view.isLeaf())
            {
               installAttributes(view);
            }
            else
            {
                header.setText(null);
                setBorder(null);
                setOpaque(false);
            }
            return this;
        }
		
		return null;
	}
	
	public void paint(Graphics g){
		
		Graphics2D g2d = (Graphics2D) g;

        if (selected && hasFocus){
            g2d.setStroke(GraphConstants.SELECTION_STROKE);
        }

        else if (hasFocus){
                //g.setColor(graph.getGridColor());            	
         	}
        else if (selected){
                //g.setColor(graph.getHighlightColor());  
            	//g2d.setStroke(GraphConstants.SELECTION_STROKE);
         }
        

        Dimension d = getSize();
        int w = d.width - 1;
        int h = d.height - 1;

        // fill rectangle with background color
        //if (getBackground() != null)
        //{
            g2d.setColor(Color.BLACK);
            g2d.drawRect(0, 0, w, h);
            g2d.setColor(Color.WHITE);
            g2d.fillRect(1,1,w-1,h-1);
        //}

        //g2d.setColor(getBorderColor());
        g2d.setColor(Color.BLACK);

        // seperator line
        //if (!suppressAttribute)
        //{
            literalsPane.setSize(d);
            Rectangle ar = literalsPane.getBounds();
            // inset ?
            g2d.drawLine(0, ar.y, w, ar.y);
       // }


        // reset color
        g2d.setColor(getForeground());

        super.paint(g2d);
		
	}

}
