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

package sourceEditor;

import com.Ostermiller.Syntax.HighlightedDocument;

public class ItHilightedDocument extends HighlightedDocument {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8699913777343514435L;

	public static final Object OCL_STYLE = OCLLexer.class;
	public static final Object PDDL_STYLE = PDDLLexer.class;	
	
	public ItHilightedDocument() {
		super();
	}

	
	
}
