/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package itSIMPLE; 

import java.awt.Component;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;

import java.util.EventObject;

import javax.swing.AbstractCellEditor;
import javax.swing.JCheckBox;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreePath;

/**
 *
 * @author designlab
 */
public class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

	  /**
		 * 
		 */
		private static final long serialVersionUID = 803817034598705051L;

	CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();

	  JTree tree;
	  
	  ChangeEvent changeEvent = null;

	  public CheckBoxNodeEditor(JTree tree) {
	    this.tree = tree;
	  }

	  public Object getCellEditorValue() {
	    JCheckBox checkbox = renderer.getLeafRenderer();
	    CheckBoxUserObject checkBoxNode = new CheckBoxUserObject(checkbox.getText(),
	        checkbox.isSelected());
	    return checkBoxNode;
	  }

	  public boolean isCellEditable(EventObject event) {
	    boolean returnValue = false;
	    if (event instanceof MouseEvent) {
	      MouseEvent mouseEvent = (MouseEvent) event;
	      TreePath path = tree.getPathForLocation(mouseEvent.getX(),
	          mouseEvent.getY());
	      if (path != null) {
	        Object node = path.getLastPathComponent();
	        if ((node != null) && (node instanceof CheckBoxNode)) {
	        	CheckBoxNode treeNode = (CheckBoxNode) node;
	          Object userObject = treeNode.getUserObject();
	          returnValue = ((treeNode.isLeaf()) && (userObject instanceof CheckBoxUserObject));
	        }
	      }
	    }
	    return returnValue;
	  }

	  public Component getTreeCellEditorComponent(JTree tree, Object value,
	      boolean selected, boolean expanded, boolean leaf, int row) {

	    Component editor = renderer.getTreeCellRendererComponent(tree, value,
	        true, expanded, leaf, row, true);

	    // editor always selected / focused
	    ItemListener itemListener = new ItemListener() {
	      public void itemStateChanged(ItemEvent itemEvent) {
	        if (stopCellEditing()) {
	          fireEditingStopped();
	        }
	      }
	    };
	    if (editor instanceof JCheckBox) {
	      ((JCheckBox) editor).addItemListener(itemListener);
	    }

	    return editor;
	  }

	}
