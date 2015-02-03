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

import java.util.ArrayList;
import javax.swing.JComboBox;

public class ItComboBox extends JComboBox{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6052896352827411231L;
	
	private ArrayList<Object> dataList;

	public ItComboBox() {
		super();
		dataList = new ArrayList<Object>();
	}
	
	public void addItem(Object item, Object dataItem){
		super.addItem(item);
		this.dataList.add(dataItem);
	}
	
	public Object getDataItem(int index){
		return dataList.get(index);
	}
	
	public void removeAllItems(){
		super.removeAllItems();
		dataList = new ArrayList<Object>();
	}
	
	public void removeItemAt(int index){
		super.removeItemAt(index);
		dataList.remove(index);
	}	
	
	public void setDataItem(int index, Object dataItem){
		dataList.set(index, dataItem);
	}	
	
}
