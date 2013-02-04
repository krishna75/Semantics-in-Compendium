
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
import com.compendium.ui.matrix.OCMatrix;

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
public class UICriteriaTree extends UIDialog implements TreeSelectionListener , ActionListener{
    private static final long   serialVersionUID = 1;

    /** The JTree object to contain a tree of criteria */
    private static JTree        tree;

    /** Is it ok to debug?  */
    private static  boolean     DEBUG           =   false;

    /** The vector object to hold list of criteria data objects */
    static Vector<OWLData>      critListAll;

    /** The OCMatrix object that is needed to be updated */
    private static OCMatrix     optCritMatrix;
    private static final String CREATCRIT       =   "creatcrit" ;
    private static final String CANCEL          =   "cancel";

/**
 * Constructor for the UICriteriaTree.
 * Creates the top node of the tree and size of the scroll pane.
 */
    public UICriteriaTree(JFrame parent, OCMatrix ocMatrix) {
        super(parent,false);
        optCritMatrix = ocMatrix;

        // sets the layout
        setLayout(new BorderLayout());
        setLocationRelativeTo(this);
        setTitle("Criteria selection ");

        // adds the components
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
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("ONTOLOGY OF CRITERIA");
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
        Label labelTree= new Label("Please choose a criterion from the list below");
        northPanel.add(labelTree);
        add("North",northPanel);
    }

    private void addSouth(){
        JPanel southPanel = new JPanel();
        JButton btnCreateCriterion = new JButton("Create a new criterion ");
        btnCreateCriterion.addActionListener(this);
        btnCreateCriterion.setActionCommand(CREATCRIT);
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
        * @param optCritMatrix  will be updated with the newly selected criteria.
        * @return updated option vs criteria matrix.
        */
       public OCMatrix getMatrix() {
            return optCritMatrix;
        }

    /**
     * Creates tree. Adds every element of the vector to the the tree as nodes.
     * @param top is the highest level of the node.
     */
    private void createTree(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode category = null;
        DefaultMutableTreeNode book = null;

        // gets the critListAll vector from the ConnectionJenaOWL file
        ConnectionJenaOWL cList= new ConnectionJenaOWL();
        critListAll  = cList.getCriteria();

        // gets each element of the the victor as OWLData.
        for (int i = 0; i<critListAll.size(); i++){
            OWLData cData= critListAll.elementAt(i);

            // adds criteria type  from criteria data to the tree node.
            category = new DefaultMutableTreeNode(cData.getDataType().toString());
            top.add(category);

            // gets another vector from the criteria data.
            Vector critList = cData.getDataList();

            // gets each element of the second vector as string (criteria) and adss in the tree.
            for (int j = 0; j<critList.size(); j++){
                book = new DefaultMutableTreeNode((critList.elementAt(j)));
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

        Object nodeInfo = node.getUserObject();

        // If  selected node is leaf, it calls the addCriteria function.
        if (node.isLeaf()) {
            addCriteria(node);
            this.onCancel();
            if (DEBUG) {
                System.out.println(nodeInfo.toString());
            }
        } else {
        }
        if (DEBUG) {
                 System.out.println(nodeInfo.toString());
        }
    }

    /**
     *
     * @param e
     */
    public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();
        if (comm.equals(CREATCRIT)) {
            new UICriterionTypeSelection(ProjectCompendium.APP);
            this.onCancel();
        }
        if (comm.equals(CANCEL)) {
            this.onCancel();
        }
    }

    /**
      * It prompts whether to add the criteria to the option vs criteria matrix
      * and adds it to the matrix.
      * @param node is a selected node ( criteria).
      */
     public void addCriteria(DefaultMutableTreeNode node){

        // It gets an integer value for yes or no from the comfirm dialog.
        int respAdd  =  JOptionPane.showConfirmDialog(null,
                "You have selected \""+ node.toString()+"\" \n Do you want to add this criteria to the matrix?",
                "Criteria selection",
                JOptionPane.YES_NO_OPTION);

        // If yes is selected in the confirm dialog, the criteria will be added to the matrix.
        if (respAdd== JOptionPane.YES_OPTION) {
            optCritMatrix.addCriteria(node.toString());

            // The message dialog to display the confirmation of added criteria to the matrix.
            JOptionPane.showMessageDialog(null,
                    " Criterion added ! \n \""+node.toString()+"\" is added to the matrix.",
                    "Confirmation of selection",
                    JOptionPane.CLOSED_OPTION);
        }
    }

}
