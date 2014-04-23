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

package languages.uml.ocl;

public class OCLOperator{
	public static final int UNARY = 1;
	public static final int BINARY = 2;
	
	private int precedence;
	private String operator;
	private int type;

	OCLOperator(String operator) throws NotAnOperatorException{
		if(isOCLOperator(operator)){
			this.operator = operator.toLowerCase();

			if(operator.toLowerCase().equals("implies")){
				precedence = 0;
				type = BINARY;
			}
			
			else if (/*operator.equals("||") ||*/
						operator.toLowerCase().equals("or") ||
						operator.toLowerCase().equals("xor")){
				precedence = 1;
				type = BINARY;
			}	

			else if(/*operator.equals("&&") || */operator.toLowerCase().equals("and")){
				precedence = 2;
				type = BINARY;
			}

			else if(/*operator.equals("==") ||*/
						operator.equals("=") ||
						/*operator.equals("!=") ||*/
						operator.equals("<>")){
				precedence = 3;
				type = BINARY;
			}

			else if(operator.equals("<") ||
						operator.equals(">") ||
						operator.equals("<=") ||
						operator.equals(">=")){
				precedence = 4;
				type = BINARY;
			}			

			else if (operator.equals("+") ||
						operator.equals("-")){
				precedence = 5;
				type = BINARY;
			}			

			else if (operator.equals("*") ||
						operator.equals("/")){
				precedence = 6;
				type = BINARY;
			}

			else if(/*operator.equals("!") ||*/
						operator.toLowerCase().equals("not")){
				precedence = 7;
				type = UNARY;
			}
			else if (operator.equals(".") || operator.equals("->")){
				precedence = 8;
				type = BINARY;
			}
			else if (operator.equals("@pre")){
				precedence = 9;
				type = UNARY;
			}
		}
		
		else{
			// not an operator
			throw (new NotAnOperatorException(operator));
		}

	}

	public String toString(){
		return operator;
	}

	/**
	 * @return Returns the precendence.
	 */
	public int getPrecedence(){
		return precedence;
	}

	public static boolean isOCLOperator(String query){
		if(query.equals("*") ||
				query.equals("/") ||
				query.equals("+") ||
				query.equals("-") ||
				query.equals("<") ||
				query.equals(">") ||
				query.equals("<=") ||
				query.equals(">=") ||
				//query.equals("==") ||
				query.equals("=") ||
				//query.equals("!=") ||
				query.equals("<>") ||
				//query.equals("&&") ||
				query.toLowerCase().equals("and") ||
				//query.equals("||") ||
				query.toLowerCase().equals("or") ||
				//query.equals("!") ||
				query.toLowerCase().equals("xor") ||
				query.toLowerCase().equals("not") ||
				query.toLowerCase().equals("implies") ||
				query.equals(".") ||
				query.equals("->") ||
				query.toLowerCase().equals("@pre"))
			return true;
		return false;
	}
	
	public static boolean isOCLOperation(String query){
		if(query.toLowerCase().equals("exists") ||
				query.toLowerCase().equals("forall") ||
				query.toLowerCase().equals("select") ||
				query.toLowerCase().equals("collect") ||
				query.toLowerCase().equals("reject") ||
				query.toLowerCase().equals("includes") ||
				query.toLowerCase().equals("excludes") ||
				query.toLowerCase().equals("including") ||
				query.toLowerCase().equals("excluding") ||
				query.toLowerCase().equals("isempty()") ||
				query.toLowerCase().equals("notempty()"))
			return true;
		return false;
	}

	/**
	 * @return Returns the type.
	 */
	public int getType() {
		return type;
	}
	
	public class NotAnOperatorException extends Exception{

		/**
		 * 
		 */
		private static final long serialVersionUID = 16585375424557253L;
		
		String notAnOperator;
		public NotAnOperatorException(String notAnOperator){
			super();
			this.notAnOperator = notAnOperator;
		}
		
		public String getMessage(){
			return ("'" + notAnOperator + "' is not a valid operator");
		}
	}

}
