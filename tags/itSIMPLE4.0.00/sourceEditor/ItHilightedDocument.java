/*** 
* itSIMPLE: Integrated Tool Software Interface for Modeling PLanning Environments
* 
* Copyright (C) 2007-2012 University of Sao Paulo, University of Toronto
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

package sourceEditor;

import com.Ostermiller.Syntax.HighlightedDocument;
import javax.swing.JTextPane;
import org.jdom.Element;

public class ItHilightedDocument extends HighlightedDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8699913777343514435L;

	public static final Object OCL_STYLE = OCLLexer.class;
	public static final Object PDDL_STYLE = PDDLLexer.class;
        
        public Element data = null;
        public JTextPane textPane = null;


	public ItHilightedDocument() {
		super();
	}

        public Element getData() {
            return data;
        }

        public void setData(Element data) {
            this.data = data;
        }        
        
        public JTextPane getTextPane() {
            return textPane;
        }
        
        public void setTextPane(JTextPane textpane) {
            this.textPane = textpane;
        }
	
	
}
