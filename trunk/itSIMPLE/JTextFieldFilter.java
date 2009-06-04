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

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;


//or import javax.swing.text.*; if Java 2

public class JTextFieldFilter extends PlainDocument {
	/**
		 * 
		 */
		private static final long serialVersionUID = -5402506106189501503L;
	public static final String LOWERCASE  =
	     "abcdefghijklmnopqrstuvwxyz";
	public static final String UPPERCASE  =
	     "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	public static final String ALPHA   = 
	     LOWERCASE + UPPERCASE;
	public static final String NUMERIC = 
	     "0123456789";
	public static final String FLOAT = 
	     NUMERIC + ".";
	public static final String ALPHA_NUMERIC = 
	     ALPHA + NUMERIC;
	
	protected String acceptedChars = null;
	protected boolean negativeAccepted = false;
	protected int limit = -1;
	
	public JTextFieldFilter() {
	  this(ALPHA_NUMERIC);
	  }
	public JTextFieldFilter(String acceptedchars) {
	  acceptedChars = acceptedchars;
	  }
	
	public void setNegativeAccepted(boolean negativeaccepted) {
	  if (acceptedChars.equals(NUMERIC) ||
	      acceptedChars.equals(FLOAT) ||
	      acceptedChars.equals(ALPHA_NUMERIC)){
	      negativeAccepted = negativeaccepted;
	     acceptedChars += "-";
	     }
	   }
	
	public void insertString
	   (int offset, String  str, AttributeSet attr)
	      throws BadLocationException {
	  if (str == null) return;
	
	  if (acceptedChars.equals(UPPERCASE))
	     str = str.toUpperCase();
	  else if (acceptedChars.equals(LOWERCASE))
	     str = str.toLowerCase();
	
	  for (int i=0; i < str.length(); i++) {
	    if (acceptedChars.indexOf(String.valueOf(str.charAt(i))) == -1)
	      return;
	    }
	
	  if (acceptedChars.equals(FLOAT) || 
	     (acceptedChars.equals(FLOAT + "-") && negativeAccepted)) {
	     if (str.indexOf(".") != -1) {
	        if (getText(0, getLength()).indexOf(".") != -1) {
	           return;
	           }
	        }
	     }
	
	  if (negativeAccepted && str.indexOf("-") != -1) {
	     if (str.indexOf("-") != 0 || offset != 0 ) {
	        return;
	        }
	     }
	  
	  if(limit == -1){
		  // there is no limit set
		  super.insertString(offset, str, attr);
	  }
	  else{
		  // there is a limit set, so check the string length to insert string
		  if ((getLength() + str.length()) <= limit) {
			  super.insertString(offset, str, attr);
		  }
	  }  

	}
	
	public void setLimit(int limit){
		this.limit = limit;
	}
}
