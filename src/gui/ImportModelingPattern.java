/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ImportModelingPattern.java
 *
 * Created on 23-Mar-2012, 3:56:55 PM
 */
package src.gui;

import java.io.File;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeModel;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

import src.languages.xml.XMLUtilities;
import src.util.fileio.FileInput;

/**
 *
 * @author tiago
 */
public class ImportModelingPattern extends javax.swing.JDialog {
    
    private ItSIMPLE itsimple = null;
    private Element project = null;
    private Element patternsData = null;
    private Element selectedPatternHeader = null;
    private Element selectedPatternData = null;    
    DefaultTableModel tableModel = null;
    private ArrayList<Element> currentPatterns = new ArrayList<Element>();
    private ItComboBox existingRoles = null;
    

    /** Creates new form ImportModelingPattern */
    public ImportModelingPattern(java.awt.Frame parent, boolean modal, Element selectedproject) {
        super(parent, modal);
        initComponents();
        
        itsimple = (ItSIMPLE)parent;
        project = selectedproject;
        
        
        //set up the roles' table
        rolesTable.setRowHeight(25);
        //set up the roles tables columns
        tableModel = (DefaultTableModel)rolesTable.getModel();
        //Role column
        tableModel.addColumn("Role");
        //New name column
        tableModel.addColumn("Name");        
        existingRoles = new ItComboBox();
        existingRoles.setEditable(true);
        filloutExistingRolesCombobox();        
        TableColumn type = rolesTable.getColumnModel().getColumn(1);		
	type.setCellEditor(new DefaultCellEditor(existingRoles));
        
        
        
        //read file with the information about available patterns
        Document patternsDoc = null;
        try {
                patternsDoc = XMLUtilities.readFromFile("resources/patterns/itPatterns.xml");
        } catch (Exception e) {
                e.printStackTrace();
        }                 
        if (patternsDoc != null){
                patternsData = patternsDoc.getRootElement();
        }
        
        if (patternsData != null){        
        //fill out the list of patterns
            getPatternsList();

        //select the first one in the list
        }
        
    }
    
    private void filloutExistingRolesCombobox(){
        //add classes
        existingRoles.removeAllItems();
	//existingRoles.addItem("",null);
	List<?> classes =null;
        try {
                XPath path = new JDOMXPath("project/elements/classes/*[type!='Primitive']");
                classes = path.selectNodes(project.getDocument());
        } catch (JaxenException e2) {			
                e2.printStackTrace();
        }
		
        for (Iterator<?> iter = classes.iterator(); iter.hasNext();) {
                Element classNode = (Element) iter.next();			
                existingRoles.addItem(classNode.getChildText("name"),classNode);
        }
    }
    
    
    
    private void getPatternsList() {
        DefaultListModel patternListModel = (DefaultListModel)patternList.getModel();
        patternListModel.clear();
        //patternList
        currentPatterns.clear();

        Iterator<?> operators = patternsData.getChild("modelingpatterns").getChildren("modelingpattern").iterator();
        while (operators.hasNext()){
            Element pattern = (Element)operators.next();
            patternListModel.addElement(pattern.getChildText("name"));
            currentPatterns.add(pattern);
        }        
    }
    

    private void showPattern(Element selected) {
        //System.out.println(selected.getChildText("name"));
        
        //clear table rows
        while (tableModel.getRowCount() > 0){
                tableModel.removeRow(0);
        }              
        
        //show text description
        //String description = selected.getChildText("description");
        //description += "<img src=\"file:resources/patterns/images/carrier.png\" />";
        
        String description = FileInput.readFile("resources/patterns/"+selected.getChildText("descriptionfile"));

        descriptionPane.setText(description);
        
        
        
        //access the pattern model that was selected
        Document patternDoc = null;
        try {
                patternDoc = XMLUtilities.readFromFile("resources/patterns/"+selected.getChildText("file"));
        } catch (Exception e) {
                e.printStackTrace();
        }                 
        if (patternDoc != null){
                selectedPatternData = patternDoc.getRootElement();
        } 
        //get main classes, roles from the model (file)
        if (selectedPatternData != null){
            List<Element> theRoles = null;
            try {
                XPath path = new JDOMXPath("project/elements/classes/*[type!='Primitive']");
                theRoles = path.selectNodes(selectedPatternData.getDocument());
            } catch (JaxenException e1) {
                e1.printStackTrace();
            }
            
            if (theRoles != null){
                for (Iterator<?> iter = theRoles.iterator(); iter.hasNext();) {
                    Element role = (Element) iter.next();
                    //System.out.println(role.getChildText("name"));
                    Vector<String> aRow = new Vector<String>();
                    aRow.add(role.getChildText("name"));
                    aRow.add(role.getChildText("name"));
                    tableModel.addRow(aRow);
                }
            }    
        }
        
        
        
    }    
    
    
    private void importSeletecedPattern() {
        //System.out.println(selected.getChildText("name"));
        
        String patternID =  selectedPatternHeader.getAttributeValue("id");
            
        //1. IMPORT OR MERGE CLASSES (ROLES)
        Element projectClassesNode = project.getChild("elements").getChild("classes");

        List<Element> theRoles = null;
        try {
            XPath path = new JDOMXPath("project/elements/classes/*[type!='Primitive']");
            theRoles = path.selectNodes(selectedPatternData.getDocument());
        } catch (JaxenException e1) {
            e1.printStackTrace();
        }

        if (theRoles != null){
                       
            //1. do the changes in the classes first in the pattern data
            int counter = 0;
            for (Iterator<?> iter = theRoles.iterator(); iter.hasNext();) {
                Element role = (Element) iter.next(); 
                
                //Change the names of the roles based on the user input (from table)
                String newname = (String) tableModel.getValueAt(counter, 1);
                System.out.println(role.getChildText("name") + " -> " + newname);
                role.getChild("name").setText(newname.trim());
                counter++;
                //XMLUtilities.printXML(role);
                
                //check if the role exists in the model (same name)                
                Element sameRole = null;
                try {
                        XPath path = new JDOMXPath("project/elements/classes/*[lower-case(name)='"+ newname.toLowerCase() +"' and type!='Primitive']");
                        sameRole = (Element)path.selectSingleNode(project.getDocument());
                } catch (JaxenException e2) {			
                        e2.printStackTrace();
                }

                String oldid = role.getAttributeValue("id");
                String newid = "";
                
                boolean hasSameRole = false;
                //if there is a role with the same name
                if(sameRole != null){
                    hasSameRole = true;
                    JOptionPane.showMessageDialog(this,"<html>Role "+newname+" already exists in your model. The system will merge the roles.</html>");
                    newid = sameRole.getAttributeValue("id");
                    role.setAttribute("merge", "true");
                    role.setAttribute("merge-id", sameRole.getAttributeValue("id"));
                    
                }
                //if not, a new id will be created in the model
                else{                    
                    newid = String.valueOf(XMLUtilities.getId(projectClassesNode));                    
                    //temporarly inser class in the project. It will be delete before inserting the right definitive one
                    Element temclass = (Element)role.clone();
                    temclass.setAttribute("id", newid);
                    temclass.setAttribute("temp", "true");
                    projectClassesNode.addContent(temclass); 
                }
                
                //Set up new ID in the role (class)
                role.setAttribute("id", newid);
                //mark element as imported from a pattern
                role.setAttribute("pattern-id", patternID);


                //change ID in associations
                List<Element> theAssociationEnds = null;
                try {
                    XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation/associationEnds/associationEnd[@element='class' and @element-id='"+oldid+"' and not(@pattern-id)]");
                    theAssociationEnds = path.selectNodes(selectedPatternData.getDocument());
                } catch (JaxenException e1) {
                    e1.printStackTrace();
                }
                if (theAssociationEnds != null){
                    for (Iterator<?> iter1 = theAssociationEnds.iterator(); iter1.hasNext();) {
                        Element assend = (Element) iter1.next();
                        assend.setAttribute("element-id", newid);
                        //mark element as imported from a pattern
                        assend.setAttribute("pattern-id", patternID);
                        //System.out.println("changed assoc end");
                        //XMLUtilities.printXML(assend);
                    }
                }
                
                //change ID in the attributes and parameterized attributes
                //class/attributes/attribute/type 
                //class/attributes/attribute/parameters/parameter/type                
                //change ID in the operators operators
                //class/operators/operator/parameters/parameter/type                
                List<Element> theTypes = null;
                try {
                    XPath path = new JDOMXPath("project/elements/classes/descendant::*[type='"+oldid+"' and not(@pattern-id)]");
                    theTypes = path.selectNodes(selectedPatternData.getDocument());
                } catch (JaxenException e1) {
                }
                if (theTypes != null){
                    for (Iterator<?> iter1 = theTypes.iterator(); iter1.hasNext();) {
                        Element ttype = (Element) iter1.next();
                        ttype.getChild("type").setText(newid);
                        //mark element as imported from a pattern
                        ttype.setAttribute("pattern-id", patternID);
                    }
                }
                                
                
                //change ID in the generalizations
                //class/generalization[@element='class' and @id='"+oldid+"']
                List<Element> theGeneralizations = null;
                try {
                    XPath path = new JDOMXPath("project/elements/classes/class/generalization[@element='class' and @id='"+oldid+"' and not(@pattern-id)]");
                    theGeneralizations = path.selectNodes(selectedPatternData.getDocument());
                } catch (JaxenException e1) {
                }
                if (theGeneralizations != null){
                    for (Iterator<?> iter1 = theGeneralizations.iterator(); iter1.hasNext();) {
                        Element general = (Element) iter1.next();
                        general.setAttribute("id", newid);
                        //mark element as imported from a pattern
                        general.setAttribute("pattern-id", patternID);                        
                    }
                }
                

                //change ID in the class diagrams
                List<Element> theRoleRefs = null;
                try {
                    XPath path = new JDOMXPath("project/diagrams/classDiagrams/classDiagram/classes/*[@id='"+oldid+"' and not(@pattern-id)]");
                    theRoleRefs = path.selectNodes(selectedPatternData.getDocument());
                } catch (JaxenException e1) {
                    e1.printStackTrace();
                }
                if (theRoleRefs != null){
                    for (Iterator<?> iter2 = theRoleRefs.iterator(); iter2.hasNext();) {
                        Element roleref = (Element) iter2.next();
                        roleref.setAttribute("id", newid);
                        if (hasSameRole){
                            roleref.setAttribute("merge", "true");
                        }
                        //mark element as imported from a pattern
                        roleref.setAttribute("pattern-id", patternID);                           
                    }
                }

                //change ID in the state machine diagrams
                List<Element> theStateMachnies = null;
                try {
                    XPath path = new JDOMXPath("project/diagrams/stateMachineDiagrams/stateMachineDiagram[class='"+oldid+"' and not(@pattern-id)]");
                    theStateMachnies = path.selectNodes(selectedPatternData.getDocument());
                } catch (JaxenException e1) {
                    e1.printStackTrace();
                }
                if (theStateMachnies != null){
                    for (Iterator<?> iter2 = theStateMachnies.iterator(); iter2.hasNext();) {
                        Element statemachine = (Element) iter2.next();
                        statemachine.getChild("class").setText(newid);
                        //mark element as imported from a pattern
                        statemachine.setAttribute("pattern-id", patternID);
                    }                
                }
                
                //change actions in the state machines
                List<Element> theSMs = null;
                try {
                    XPath path = new JDOMXPath("project/diagrams/stateMachineDiagrams/stateMachineDiagram/associations/action[not(@pattern-id)]/reference[@class='"+oldid +"']");
                    theSMs = path.selectNodes(selectedPatternData.getDocument());
                } catch (JaxenException e1) {
                }
                if (theSMs != null){
                    for (Iterator<?> iter2 = theSMs.iterator(); iter2.hasNext();) {
                        Element actionref = (Element) iter2.next();
                        actionref.setAttribute("class", newid); 
                        Element action = actionref.getParentElement();
                        //mark element as imported from a pattern
                        action.setAttribute("pattern-id", patternID);

                    }
                } 

            }
            
            
            //2. Delete all temporary new classes
            List<Element> theTempClasses = null;
            try {
                XPath path = new JDOMXPath("project/elements/classes/*[@temp='true']");
                theTempClasses = path.selectNodes(project.getDocument());
            } catch (JaxenException e1) {
                e1.printStackTrace();
            }
            for (Iterator<?> iter = theTempClasses.iterator(); iter.hasNext();) {
                 Element temp = (Element) iter.next(); 
                 projectClassesNode.removeContent(temp);
            }
            
            //3. then insert the new classes in the model
            for (Iterator<?> iter = theRoles.iterator(); iter.hasNext();) {
                Element role = (Element) iter.next();
                
                Element sameRole = null;
                boolean hasSameRole = false;
                if (role.getAttribute("merge") != null){
                    try {
                        XPath path = new JDOMXPath("project/elements/classes/*[@id='"+ role.getAttributeValue("merge-id") +"' and type!='Primitive']");
                        sameRole = (Element)path.selectSingleNode(project.getDocument());
                    } catch (JaxenException e2) {			
                            e2.printStackTrace();
                    }
                    if (sameRole != null){
                        hasSameRole = true;
                    }                    
                }
                
                //add role into the project if it does not need to be merged
                if (!hasSameRole){
                    
                    projectClassesNode.addContent((Element)role.clone());                   
                    
                }
                //if merge is needed...
                else{
                    
                    System.out.println("Do merging for role "+role.getChildText("name"));
                    
                    //MERGE attributes
                    //project attributes
                    Element sameRoleAttributes = sameRole.getChild("attributes");
                    //pattern attributes
                    Element roleAttributes = role.getChild("attributes");
                    for (Iterator<?> iter1 = roleAttributes.getChildren().iterator(); iter1.hasNext();) {
                        Element attr = (Element) iter1.next();
                        String oldidattr = attr.getAttributeValue("id");
                        String newidattr = String.valueOf(XMLUtilities.getId(sameRoleAttributes));                        
                
                        //change ID in the association
                        attr.setAttribute("id", newidattr);
                                                
                        //add attribute to the existing role/class
                        sameRoleAttributes.addContent((Element)attr.clone());                                               
                    }
                    
                    
                    //MERGE operators                                       
                    //project operators
                    Element sameRoleOperators = sameRole.getChild("operators");
                    //pattern operators
                    Element roleOperators = role.getChild("operators");
                    for (Iterator<?> iter1 = roleOperators.getChildren().iterator(); iter1.hasNext();) {
                        Element op = (Element) iter1.next();
                        String oldidop = op.getAttributeValue("id");
                        String newidop = String.valueOf(XMLUtilities.getId(sameRoleOperators));                        
                
                        //change ID of the operator
                        op.setAttribute("id", newidop);
                        
                        //change op ID in the state machine action
                        List<Element> theSMs = null;
                        try {
                            XPath path = new JDOMXPath("project/diagrams/stateMachineDiagrams/stateMachineDiagram/associations/action/reference[@class='"+role.getAttributeValue("id") +"' and @operator='"+oldidop+"' and not(@pattern-id)]");
                            theSMs = path.selectNodes(selectedPatternData.getDocument());
                        } catch (JaxenException e1) {
                        }
                        if (theSMs != null){
                            for (Iterator<?> iter2 = theSMs.iterator(); iter2.hasNext();) {
                                Element ref = (Element) iter2.next();
                                ref.setAttribute("operator", newidop);
                                //mark element as imported from a pattern
                                ref.setAttribute("pattern-id", patternID);   
                            }
                        }                        
                        
                         //add attribute to the existing role/class
                         sameRoleOperators.addContent((Element)op.clone());                                               
                    }
  
                }                

            }
            
        }
                
                
        //2. IMPORT CLASS ASSOCIATIONS
        Element projectAssociationsNode = project.getChild("elements").getChild("classAssociations");
        List<Element> theAssociations = null;
        try {
            XPath path = new JDOMXPath("project/elements/classAssociations/*");
            theAssociations = path.selectNodes(selectedPatternData.getDocument());
        } catch (JaxenException e1) {
            e1.printStackTrace();
        }

        if (theAssociations != null){
            for (Iterator<?> iter3 = theAssociations.iterator(); iter3.hasNext();) {
                Element assoc = (Element) iter3.next();
                 //Set up new ID in the role
                String oldid = assoc.getAttributeValue("id");
                String newid = String.valueOf(XMLUtilities.getId(projectAssociationsNode));
                
                //change ID in the association
                assoc.setAttribute("id", newid);
                //mark element as imported from a pattern
                assoc.setAttribute("pattern-id", patternID);
                                
                //change ID in the class diagrams
                List<Element> theAssocRefs = null;
                try {
                    XPath path = new JDOMXPath("project/diagrams/classDiagrams/classDiagram/associations/classAssociation[@id='"+oldid+"' and not(@pattern-id)]");
                    theAssocRefs = path.selectNodes(selectedPatternData.getDocument());
                } catch (JaxenException e1) {
                    e1.printStackTrace();
                }
                if (theAssocRefs != null){
                     for (Iterator<?> iter2 = theAssocRefs.iterator(); iter2.hasNext();) {
                        Element assocref = (Element) iter2.next();
                        assocref.setAttribute("id", newid);
                        //mark element as imported from a pattern
                        assocref.setAttribute("pattern-id", patternID);
                    }                                                
                }
                
                //add class association to the project
                projectAssociationsNode.addContent((Element)assoc.clone());
            }
        }
                
                                


        //3. IMPORT CLASS DIAGRAM ELEMENTS in an existing class diagram in the project
        Element patternClassDiagramNode = selectedPatternData.getChild("diagrams").getChild("classDiagrams").getChild("classDiagram");
        Element projectClassDiagramNode = project.getChild("diagrams").getChild("classDiagrams").getChild("classDiagram");
        //classes ref.
        for (Iterator<?> iter2 = patternClassDiagramNode.getChild("classes").getChildren().iterator(); iter2.hasNext();) {
                Element roleRef = (Element) iter2.next();
                //if the ref of the role does not need to be merged then add it
                if (roleRef.getAttribute("merge") == null){
                    projectClassDiagramNode.getChild("classes").addContent((Element)roleRef.clone());
                }                
        }
        //association ref.                        
        for (Iterator<?> iter2 = patternClassDiagramNode.getChild("associations").getChildren().iterator(); iter2.hasNext();) {
                Element assocRef = (Element) iter2.next();                    
                projectClassDiagramNode.getChild("associations").addContent((Element)assocRef.clone());                                        
        }
        
        
        //4. IMPORT STATEMACHINE DIAGRAMS
        Element patternStateMachinesNode = selectedPatternData.getChild("diagrams").getChild("stateMachineDiagrams");
        Element projectStateMachinesNode = project.getChild("diagrams").getChild("stateMachineDiagrams");
        for (Iterator<?> iter2 = patternStateMachinesNode.getChildren().iterator(); iter2.hasNext();) {
                Element sm = (Element) iter2.next();
                String oldid = sm.getAttributeValue("id");
                String newid = String.valueOf(XMLUtilities.getId(projectStateMachinesNode));                
                //change ID in the sm diagram
                sm.setAttribute("id", newid);
                
                projectStateMachinesNode.addContent((Element)sm.clone());                                 
        }
        
        
       
 
    }    
    
    

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        patternListPanel = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        patternList = new javax.swing.JList();
        contentPanel = new javax.swing.JPanel();
        patternDescriptionPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        descriptionPane = new javax.swing.JEditorPane();
        roleNamesPanel = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        rolesTable = new javax.swing.JTable();
        jLabel2 = new javax.swing.JLabel();
        bottomPanel = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        cancelButton = new javax.swing.JButton();
        importButton = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("Import Modeling Patterns");
        setIconImage(null);
        setLocationByPlatform(true);
        setMinimumSize(new java.awt.Dimension(500, 500));
        setName("patternsdialog"); // NOI18N

        mainPanel.setLayout(new java.awt.BorderLayout());

        patternListPanel.setName("patternListPanel"); // NOI18N

        patternList.setModel(new DefaultListModel());
        patternList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                listValueChange(evt);
            }
        });
        jScrollPane1.setViewportView(patternList);

        javax.swing.GroupLayout patternListPanelLayout = new javax.swing.GroupLayout(patternListPanel);
        patternListPanel.setLayout(patternListPanelLayout);
        patternListPanelLayout.setHorizontalGroup(
            patternListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patternListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 173, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        patternListPanelLayout.setVerticalGroup(
            patternListPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patternListPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 608, Short.MAX_VALUE))
        );

        mainPanel.add(patternListPanel, java.awt.BorderLayout.LINE_START);

        descriptionPane.setContentType("text/html");
        descriptionPane.setEditable(false);
        jScrollPane3.setViewportView(descriptionPane);

        javax.swing.GroupLayout patternDescriptionPanelLayout = new javax.swing.GroupLayout(patternDescriptionPanel);
        patternDescriptionPanel.setLayout(patternDescriptionPanelLayout);
        patternDescriptionPanelLayout.setHorizontalGroup(
            patternDescriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patternDescriptionPanelLayout.createSequentialGroup()
                .addComponent(jScrollPane3)
                .addContainerGap())
        );
        patternDescriptionPanelLayout.setVerticalGroup(
            patternDescriptionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(patternDescriptionPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 445, Short.MAX_VALUE))
        );

        rolesTable.setModel(new DefaultTableModel(){
            private static final long serialVersionUID = -1515586562196686580L;
            // only the value column is editable
            public boolean isCellEditable(int row, int col) {
                if (col == 1) {
                    return true;
                }
                else {
                    return false;
                }
            }
        });
        jScrollPane2.setViewportView(rolesTable);

        jLabel2.setText("Change the role (class) names below if you like (classes with the same name in your model will be mergerd):");

        javax.swing.GroupLayout roleNamesPanelLayout = new javax.swing.GroupLayout(roleNamesPanel);
        roleNamesPanel.setLayout(roleNamesPanelLayout);
        roleNamesPanelLayout.setHorizontalGroup(
            roleNamesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(roleNamesPanelLayout.createSequentialGroup()
                .addGroup(roleNamesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(roleNamesPanelLayout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addGap(0, 159, Short.MAX_VALUE)))
                .addContainerGap())
        );
        roleNamesPanelLayout.setVerticalGroup(
            roleNamesPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, roleNamesPanelLayout.createSequentialGroup()
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 127, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout contentPanelLayout = new javax.swing.GroupLayout(contentPanel);
        contentPanel.setLayout(contentPanelLayout);
        contentPanelLayout.setHorizontalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(patternDescriptionPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(roleNamesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        contentPanelLayout.setVerticalGroup(
            contentPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(contentPanelLayout.createSequentialGroup()
                .addComponent(patternDescriptionPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(roleNamesPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        mainPanel.add(contentPanel, java.awt.BorderLayout.CENTER);

        bottomPanel.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        cancelButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/delete.png"))); // NOI18N
        cancelButton.setText("Cancel");
        cancelButton.setFocusable(false);
        cancelButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        cancelButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelImport(evt);
            }
        });
        jToolBar1.add(cancelButton);

        importButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/import.png"))); // NOI18N
        importButton.setText("Import");
        importButton.setFocusable(false);
        importButton.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        importButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importSelected(evt);
            }
        });
        jToolBar1.add(importButton);

        bottomPanel.add(jToolBar1, java.awt.BorderLayout.EAST);

        jLabel1.setText("  This is a preliminary (beta) support for modeling patterns. Use it carefully! More patterns will come and the existing ones might change.   ");
        bottomPanel.add(jLabel1, java.awt.BorderLayout.CENTER);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(bottomPanel, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(bottomPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelImport(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelImport
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_cancelImport

    private void importSelected(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importSelected
        // TODO add your handling code here:
        if (patternList.getSelectedIndex() > -1 && selectedPatternData != null && selectedPatternHeader  != null){
            Element selected = currentPatterns.get(patternList.getSelectedIndex());
            System.out.println("Import: "+ selected.getChildText("name"));
            
            //do the import            
            importSeletecedPattern();
                        
            // repaint/rebuild open diagrams
            ItTabbedPane tabbed = itsimple.getItGraphTabbedPane();
            tabbed.reBuildOpenDiagrams("classDiagram");
            
            // rebuild project tree node
            ItTree tree = ItSIMPLE.getInstance().getItTree();            
            ItTreeNode projectNode = tree.findProjectNode(project);
            DefaultTreeModel treeModel = (DefaultTreeModel)tree.getModel();            
            //clear and rebuild tree
            tree.rebuildProjectNode(project, projectNode);

            //try to expand the project?    
            
            
            this.dispose();
            
        }
    }//GEN-LAST:event_importSelected

    private void listValueChange(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_listValueChange
        // TODO add your handling code here:
        if (patternList.getSelectedIndex() > -1){
            //System.out.println(patternList.getSelectedIndex());
            selectedPatternHeader = currentPatterns.get(patternList.getSelectedIndex());
            //show pattern description and roles
            showPattern(selectedPatternHeader);                   
        }
    }//GEN-LAST:event_listValueChange

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(ImportModelingPattern.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(ImportModelingPattern.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(ImportModelingPattern.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(ImportModelingPattern.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {

            public void run() {
                ImportModelingPattern dialog = new ImportModelingPattern(new javax.swing.JFrame(), true,null);
                dialog.addWindowListener(new java.awt.event.WindowAdapter() {

                    @Override
                    public void windowClosing(java.awt.event.WindowEvent e) {
                        System.exit(0);
                    }
                });
                dialog.setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel contentPanel;
    private javax.swing.JEditorPane descriptionPane;
    private javax.swing.JButton importButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JPanel patternDescriptionPanel;
    private javax.swing.JList patternList;
    private javax.swing.JPanel patternListPanel;
    private javax.swing.JPanel roleNamesPanel;
    private javax.swing.JTable rolesTable;
    // End of variables declaration//GEN-END:variables

}
