
/*
 * UICriteriaTree.java
 *
 * Created on 30-Jun-2009, 19:16:30
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.compendium.ui.owl;


import com.compendium.ProjectCompendium;
import com.compendium.ui.dialogs.UIDialog;
import com.compendium.ui.matrix.GCMatrix;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;

import java.awt.Dimension;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import javax.swing.JButton;
import javax.swing.JOptionPane;

/**
 * It displays ontology of criteria in a tree form.
 * It updates OCMatrix with the selected criteria.
 *
 * @author Krishna Sapkota
 */
public class UIParametersTree extends UIDialog implements TreeSelectionListener , ActionListener{
    private static final long serialVersionUID = 1;
    
    /** The JTree object to contain a tree of criteria */
    private static JTree            tree;

    /** The vector object to hold list of criteria data objects */
    static Vector<OWLData>          paramListAll;

    /** The OCMatrix object that is needed to be updated */
    private static GCMatrix         gpTable;
    private static ParameterData    paramData;

    private static final String     CREATPARAM      =   "creatparam" ;
    private static final String     CANCEL          =   "cancel";

/**
 * Constructor for the UIParametersTree.
 * Creates the top node of the tree and size of the scroll pane.
 */
    public UIParametersTree(JFrame parent, GCMatrix gcMatrix) {
        super(parent,false);
        gpTable = gcMatrix;

        setLayout(new BorderLayout());
        setLocationRelativeTo(this);
        setTitle("Global parameter selection dialog");
         
        addCenter();
        addNorth();
        addSouth();
        addEast();
        addWest();

        //Display the window.
        setVisible(true);
        pack();
    }

        private void addCenter(){
         //Create the nodes.
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("ONTOLOGY OF PARAMETERS");
        createTree(top);

        //Create a tree that allows one selection at a time.
        tree = new JTree(top);
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);

        //Listen for when the selection changes.
        tree.addTreeSelectionListener(this);

        //Create the scroll pane and add the tree to it.
        JScrollPane treeView = new JScrollPane(tree);
        treeView.setPreferredSize(new Dimension(400,400));

        // adding the scroll pane to this panel
        add("Center",treeView);
    }

    private void addNorth(){
        JPanel northPanel   =   new JPanel();
        Label labelTree= new Label("Please choose a parameter from the list below");
        northPanel.add(labelTree);
        add("North",northPanel);
    }

    private void addSouth(){
        JPanel southPanel = new JPanel();
        JButton btnCreateCriterion = new JButton("Create a new parameter ");
        btnCreateCriterion.addActionListener(this);
        btnCreateCriterion.setActionCommand(CREATPARAM);
        southPanel.add(btnCreateCriterion);

        JButton btnCancel = new JButton("Cancel");
        btnCancel.addActionListener(this);
        btnCancel.setActionCommand(CANCEL);
        southPanel.add(btnCancel);
        add("South",southPanel);
    }

    private void addEast(){
        JPanel eastPanel    =   new JPanel();
        add("East",eastPanel);
    }

    private void addWest(){
        JPanel westPanel    =   new JPanel();
        add("West",westPanel);
    }

   /**
    * It dispays the tree and returns the option vs criteria matrix after updating it.
    * @param gpTable  will be updated with the newly selected criteria.
    * @return updated option vs criteria matrix.
    */
   public GCMatrix getMatrix() {
   return gpTable;

}

/**
 * Creates tree. Adds every element of the vector to the the tree as nodes.
 * @param top is the highest level of the node.
 */
    private void createTree(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode book = null;

        // gets the paramListAll vector from the ConnectionJenaOWL file
        ConnectionJenaOWL cList= new ConnectionJenaOWL();
        paramListAll  = cList.getParameters();

        // gets each element of the the vector as OWLData.
        for (int i = 0; i<paramListAll.size(); i++){
            OWLData cData= paramListAll.elementAt(i);

            // adds parameter type  from OWLData to the tree node.
            category = new DefaultMutableTreeNode(cData.getDataType().toString());
            top.add(category);

            // gets another vector from the OWLData.
            Vector paramList = cData.getDataList();

            // gets each element of the second vector as string (parameter) and adss in the tree.
            for (int j = 0; j<paramList.size(); j++){
                ParameterData param = (ParameterData) paramList.elementAt(j);
                book = new DefaultMutableTreeNode(param.getName()+ "  (value="+param.getValue()+ ", unit="+param.getUnit()+")");
               category.add(book);
            }
        }
    }

        /**
      * Required by TreeSelectionListener interface
      * The tree selection listener to handle when one node in the tree is selected.
      * @param e is the tree selection event
      */
    public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        // If selected node is null, it does not do anty thing.
        if (node == null) return;
        // If  selected node is leaf, it calls the addParameter function.
        if (node.isLeaf()) {
            addParameter(node);
            this.onCancel();
        } 
    }

    /**
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();
//      Close the dialog
        if (comm.equals(CREATPARAM)) {
            new UIParameterTypeSelection(ProjectCompendium.APP);
            this.onCancel();
        }
        if (comm.equals(CANCEL)) {
            this.onCancel();
        }
    }

     /**
      * It prompts whether to add the parameter to the   matrix
      * and adds it to the matrix.
      * @param node is a selected node ( parameter).
      */
     public void addParameter(DefaultMutableTreeNode node){

        // It gets an integer value for yes or no from the comfirm dialog.
        int respAdd  =  JOptionPane.showConfirmDialog(null,
                "You have selected \""+ node.toString()+"\" \n Do you want to add this parameter to the matrix?",
                "Parameter selection",
                JOptionPane.YES_NO_OPTION);

        // If yes is selected in the confirm dialog, the parameter will be added to the matrix.
        if (respAdd== JOptionPane.YES_OPTION) {

            // gets each element of the the vector as OWLData.
            for (int i = 0; i<paramListAll.size(); i++){
                OWLData cData= paramListAll.elementAt(i);
                // gets another vector from the OWLData.
                Vector paramList = cData.getDataList();

                // gets each element of the second vector as string (parameter) and adss in the tree.
                for (int j = 0; j<paramList.size(); j++){
                    ParameterData param = (ParameterData) paramList.elementAt(j);
                    String paramName = param.getName();
                    if (node.toString().startsWith(paramName)){
                        paramData= param;
                    }
                }
            }

         gpTable.addDataRow(paramData.getName(), paramData.getValue(), paramData.getUnit());

          // The message dialog to display the confirmation of added criteria to the matrix.
          JOptionPane.showMessageDialog(null,
                  " Parameter added ! \n \""+ paramData.getName()+"\" is added to the matrix.",
                  "Confirmation of selection",
                  JOptionPane.CLOSED_OPTION);
        }
    }

}
