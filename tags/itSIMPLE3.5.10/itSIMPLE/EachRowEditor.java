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

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Hashtable;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.CellEditorListener;
import javax.swing.table.TableCellEditor;

public class EachRowEditor implements TableCellEditor{

	  protected Hashtable editors;

	  protected TableCellEditor editor, defaultEditor;

	  JTable table;

	  /**
	   * Constructs a EachRowEditor. create default editor
	   * 
	   * @see TableCellEditor
	   * @see DefaultCellEditor
	   */
	  public EachRowEditor(JTable table) {
	    this.table = table;
	    editors = new Hashtable();
	    defaultEditor = new DefaultCellEditor(new JTextField());
	  }

	  /**
	   * @param row
	   *            table row
	   * @param editor
	   *            table cell editor
	   */
	public void setEditorAt(int row, TableCellEditor editor) {
	    editors.put(new Integer(row), editor);
	  }

	  public Component getTableCellEditorComponent(JTable table, Object value,
	    boolean isSelected, int row, int column) {
	    editor = (TableCellEditor)editors.get(new Integer(row));
	    if (editor == null) {
	      editor = defaultEditor;
	    }
	    return editor.getTableCellEditorComponent(table, value, isSelected,
	        row, column);
	  }

	  public Object getCellEditorValue() {
	    return editor.getCellEditorValue();
	  }

	  public boolean stopCellEditing() {
	    return editor.stopCellEditing();
	  }

	  public void cancelCellEditing() {
		  if (editor != null)
			  editor.cancelCellEditing();
	  }

	  public boolean isCellEditable(EventObject anEvent) {
	    selectEditor((MouseEvent) anEvent);
	    return editor.isCellEditable(anEvent);
	  }

	  public void addCellEditorListener(CellEditorListener l) {
	    editor.addCellEditorListener(l);
	  }

	  public void removeCellEditorListener(CellEditorListener l) {
	    editor.removeCellEditorListener(l);
	  }

	  public boolean shouldSelectCell(EventObject anEvent) {
	    selectEditor((MouseEvent) anEvent);
	    return editor.shouldSelectCell(anEvent);
	  }

	  protected void selectEditor(MouseEvent e) {
	    int row;
	    if (e == null) {
	      row = table.getSelectionModel().getAnchorSelectionIndex();
	    } else {
	      row = table.rowAtPoint(e.getPoint());
	    }
	    editor = (TableCellEditor) editors.get(new Integer(row));
	    if (editor == null) {
	      editor = defaultEditor;
	    }
	  }
	
}
