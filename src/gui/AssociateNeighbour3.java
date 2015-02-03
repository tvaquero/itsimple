/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.Border;
import org.jaxen.JaxenException;
import org.jaxen.XPath;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.DataConversionException;
import org.jdom.Element;

/**
 *
 * @author J
 */
public class AssociateNeighbour3 extends javax.swing.JDialog {

    /**
     * Creates new form AssociateNeighbour3
     */
    
     Element objectDiagram = null;
     //Object selected_object = null;
     Object[] selected_objects = null;
     boolean showForm = false;
     String association;
     
    public AssociateNeighbour3(java.awt.Frame parent, boolean modal) {
        super(parent, modal);
        initComponents();
        ini();
    }
    
    public AssociateNeighbour3(Element one_objectDiagram, Object[] group_selected_object){
    
        objectDiagram = one_objectDiagram;
        selected_objects = group_selected_object;
        
        
        initComponents();
        ini();        
    }
    
    public void ini(){
    
          ArrayList<Object> associations = getAssociations();
          
          fillCombo(associations);
        
          jComboBox1.addItemListener(new ItemListener(){
            @Override
            public void itemStateChanged(ItemEvent e){
                association = (String)e.getItem();
                if(!"...shoose an association...".equals(association)){
                 //  enabledAllObjects(true);
                    jButton1.setEnabled(true);
                    enabledAllObjects(true);
                
                    Object association = jComboBox1.getModel().getSelectedItem();
                    Object idAssociation = getIdClass(association.toString());
                    if(!validateAssociations(selected_objects, idAssociation)){
                         JOptionPane.showMessageDialog(null, "Association "+ association.toString() +" not linked all this selected objects.\n Please, select another relationship.");;
                         enabledAllObjects(false);
                         jButton1.setEnabled(false);
                    }
                        
                }
                //else
                //    enabledAllObjects(false);
            }
            
        });
          
         
          jButton1.setEnabled(false);
          jLabel2.setForeground(Color.blue);
           
          Font f = new Font("Arial Bold",Font.BOLD, 22);
          jLabel2.setFont(f);
          
          if(selected_objects.length > 1) {
            jLabel2.setText("Selected Object");
          }
          else {
            jLabel2.setText(selected_objects[0].toString());
        }
            //jPanel3.setSize(50, 50);
            jPanel3.setBorder(null);
         enabledAllObjects(false);
   /*
            
           // if(selected_object != null)
        //{
            //Validate if the selected object is linked with the relationship
            //jLabel2.setHorizontalTextPosition(SwingConstants.CENTER);
            //jLabel2.setForeground(Color.blue);
            //jLabel2.setText(selected_object.toString());
            
            //Get the choose object ID 
            String classe = getClassObject(selected_object.toString());
            ArrayList<Object> associations_class = getAssociationsClass(classe);
            
            Object association = null;
            
            association = jComboBox1.getModel().getSelectedItem();
            Object id = getIdClass(association.toString());
                       
            
            if(associations_class.contains(id)){
                int a = 0;            
            }
            else{
                jLabel2.setForeground(Color.red);
                jLabel2.setText("Selected Object and selected Association must be linked");
            

            }
             //Get class object
            //Get associations class
            
            enabledAllObjects(false);
            Dimension d = new Dimension(155, 108);
            jPanel3.setPreferredSize(d);
       // }*/
    }
    
    private void fillCombo(ArrayList<Object> associations){
        jComboBox1.removeAllItems();
          enabledAllObjects(false);
          
          jComboBox1.addItem("...shoose an association...");
          for(int i = 0; i < associations.size(); i++){
            jComboBox1.addItem(associations.get(i).toString());
          }    
    }
    
    //This method validate if all objects are associate by a select assoaciate 
    private boolean validateAssociations(Object[] objects, Object idAssociation){
    
       for(int i = 0; i < objects.length; i++){
            String classe; // Object class
            ArrayList<Object> associations_class; // Class associations 
        
            classe = getClassObject(objects[i].toString());
            associations_class = getAssociationsClass(classe);
            if(!associations_class.contains(idAssociation)) {
               return false;
           }        
        }
       return true;
        /*
         
          String classe = getClassObject(selected_object.toString());
            ArrayList<Object> associations_class = getAssociationsClass(classe);
            
            Object association = null;
            
            association = jComboBox1.getModel().getSelectedItem();
            Object id = getIdClass(association.toString());
                       
            
            if(associations_class.contains(id)){
                int a = 0;            
            }
            else{
                jLabel2.setForeground(Color.red);
                jLabel2.setText("Selected Object and selected Association must be linked");
            

            }*/

    }
            
    public ArrayList<Object> getAssociations(){
    
        ArrayList<Object> name_associations = new ArrayList<Object>();
        List<?> result = null;
        List<?> list_asso_end = null;

        try {
            XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation");
            result = path.selectNodes(objectDiagram.getDocument());
	} catch (JaxenException e2) {			
	e2.printStackTrace();
        }
        for(int i = 0; i < result.size(); i++){ 
            Element association = (Element)result.get(i);     
            Element name = association.getChild("name");
            name_associations.add(name.getValue());
        }    
        return name_associations;
       
    }
    
     public void enabledAllObjects(boolean action){
        //if(action == true){
          
            jCheckBox1.setEnabled(action);
            jCheckBox2.setEnabled(action);
            jCheckBox3.setEnabled(action);
            jCheckBox4.setEnabled(action);
            jCheckBox5.setEnabled(action);
            jCheckBox6.setEnabled(action);
            jCheckBox7.setEnabled(action);
            jCheckBox8.setEnabled(action);
          
 //}
     }
     
   
     
     public String getClassObject(String name_object){
        List<?> result, result1, result2, result3= null;
        String name = null;
  
        try {
            Integer id = null;
            //object Diagram:
            //Element domain = objectDiagram.getParentElement().getParentElement().getParentElement().getParentElement();
            
            //repository diagram:
            Element domain = objectDiagram.getParentElement().getParentElement();
            try {
                id = domain.getAttribute("id").getIntValue();
            } catch (DataConversionException ex) {
                Logger.getLogger(AssociateNeighbour3.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            XPath path = new JDOMXPath("project/diagrams/planningDomains/domain[@id="+id+"]");
            result1 = path.selectNodes(objectDiagram.getDocument());
            
            Element object1  = (Element)(result1.get(0));
            
            result = object1.getChildren("elements");
            
            Element object2 = (Element)result.get(0);
            
            result2 = object2.getChildren("objects");
            
            Element object3 = (Element)result2.get(0);
            
            result3 = object3.getChildren("object");
                   //
        } 
        catch (JaxenException e2) {			
            }
                
       	for (int i = 0; i < result3.size(); i++){
            Element object = (Element)result3.get(i);
            name = (object.getChildText("name"));
            if(name.equals(name_object)) {
                return object.getChildText("class");
            }
        }
        return null;   
    }
     
     public ArrayList<Object> getAssociationsClass(String classe){

        ArrayList<Object> associations = new ArrayList<Object>();
        List<?> result = null;
        List<?> list_asso_end = null;
        
        try {
            XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation");
            result = path.selectNodes(objectDiagram.getDocument());
	} catch (JaxenException e2) {			
	e2.printStackTrace();
        }
        for(int i = 0; i < result.size(); i++){ 
            Element association = (Element)result.get(i);     
            Element ass_ends = association.getChild("associationEnds");             
            list_asso_end = ass_ends.getChildren("associationEnd"); 
            String first_ass = ((Element)list_asso_end.get(0)).getAttributeValue("element-id");
            String second_ass = ((Element)list_asso_end.get(1)).getAttributeValue("element-id");
            
            if(first_ass.equals(classe) || second_ass.equals(classe)){
                associations.add(association.getAttribute("id").getValue());          
            }
           
        }        
        return associations;
    }
     
      public Object getIdClass(String classe){
    
        ArrayList<Object> associations = new ArrayList<Object>();
        List<?> result = null;
        List<?> list_asso_end = null;

        try {
            XPath path = new JDOMXPath("project/elements/classAssociations/classAssociation");
            result = path.selectNodes(objectDiagram.getDocument());
	} catch (JaxenException e2) {			
	e2.printStackTrace();
        }
        for(int i = 0; i < result.size(); i++){ 
            Element association = (Element)result.get(i);            
            if(association.getChild("name").getValue().toString().equals(classe)){
                return association.getAttribute("id").getValue();
            }
        }
        return null;
    }
    public void setObjectDiagram(Element one_objectDiagram){
    
        objectDiagram = one_objectDiagram;
        
    }
    
    public void setObject(Object one_selected_object){
  /*
        selected_object = one_selected_object;
        
        if(selected_object != null){
        String classe = getClassObject(selected_object.toString());
        ArrayList<Object> associations_class = getAssociationsClass(classe);
            
        Object association = null;
            
        association = jComboBox1.getModel().getSelectedItem();
        Object id = getIdClass(association.toString());
        
        if(!"...shoose an association...".equals(association)){
                    //rowCount.setEnabled(true);
                    //rowCount.requestFocus();
                    //if(table != null){
                      //  fillComboTable();
                    //generateB.setEnabled(true);
                    //}
                   enabledAllObjects(true);
                }
        else{
            enabledAllObjects(false);
            JOptionPane.showMessageDialog(null, "You must be choose an association");;
        }
        
        //enabledAllObjects(false);
                       
            
        if(associations_class.contains(id)){
            int a = 0;  
            jLabel2.setForeground(Color.blue);
           
            Font f = new Font("Arial Bold",Font.BOLD, 22);
            jLabel2.setFont(f);
            jLabel2.setText(selected_object.toString());
            //jPanel3.setSize(50, 50);
            jPanel3.setBorder(null);
            enabledAllObjects(true);
        }
        else if("...shoose an association...".equals(association)){
            Font f = new Font("Arial",Font.PLAIN, 16); 
            jLabel2.setFont(f);
            jLabel2.setForeground(Color.red);
            jLabel2.setText("<html>Onselect Object<br> </html>");
            enabledAllObjects(false);
        }
        else{
            
            Font f = new Font("Arial",Font.PLAIN, 16); 
            jLabel2.setFont(f);
            jLabel2.setForeground(Color.red);
            jLabel2.setText("<html>Object must be<br>linked with Association<br> </html>");
            enabledAllObjects(false);

        }
        
  
    }
    */}
    public void setShowForm(boolean val){
    
        showForm = val;
    }
    public boolean showForm(){
        return showForm;
    }
    

    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jComboBox1 = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jCheckBox7 = new javax.swing.JCheckBox();
        jCheckBox5 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();
        jCheckBox8 = new javax.swing.JCheckBox();
        jCheckBox6 = new javax.swing.JCheckBox();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jButton1 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("Association:");

        jPanel1.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jCheckBox7.setText("North-West");

        jCheckBox5.setText("North");
        jCheckBox5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox5ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("North-East");

        jCheckBox1.setText("East");

        jCheckBox3.setText("South-East");

        jCheckBox4.setText("South");
        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox4ActionPerformed(evt);
            }
        });

        jCheckBox8.setText("South-East");

        jCheckBox6.setText("West");

        jPanel3.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0), 2));

        jLabel2.setBackground(new java.awt.Color(242, 240, 240));
        jLabel2.setText("Unselected object");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(52, 52, 52)
                .addComponent(jLabel2)
                .addContainerGap(42, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckBox6)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jCheckBox7)
                        .addGap(36, 36, 36)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jCheckBox4)
                            .addComponent(jCheckBox5)))
                    .addComponent(jCheckBox8))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(jCheckBox1)
                        .addComponent(jCheckBox2))
                    .addComponent(jCheckBox3, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(119, 119, 119)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(162, Short.MAX_VALUE)))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox7)
                    .addComponent(jCheckBox5)
                    .addComponent(jCheckBox2))
                .addGap(57, 57, 57)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox6))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 60, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox8)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox3))
                .addContainerGap())
            .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel1Layout.createSequentialGroup()
                    .addGap(39, 39, 39)
                    .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(42, Short.MAX_VALUE)))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jButton1.setText("Ok");

        jButton2.setText("Cancel");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton1)
                    .addComponent(jButton2))
                .addGap(0, 13, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 229, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 117, Short.MAX_VALUE))
                    .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jCheckBox5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox5ActionPerformed

    private void jCheckBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox4ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jCheckBox4ActionPerformed

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed
        // TODO add your handling code here:
        showForm = false;
        this.dispose();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:

        showForm = false;
    }//GEN-LAST:event_formWindowClosing

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
            java.util.logging.Logger.getLogger(AssociateNeighbour3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(AssociateNeighbour3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(AssociateNeighbour3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(AssociateNeighbour3.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the dialog */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                AssociateNeighbour3 dialog = new AssociateNeighbour3(new javax.swing.JFrame(), true);
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
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JCheckBox jCheckBox5;
    private javax.swing.JCheckBox jCheckBox6;
    private javax.swing.JCheckBox jCheckBox7;
    private javax.swing.JCheckBox jCheckBox8;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    // End of variables declaration//GEN-END:variables
}
