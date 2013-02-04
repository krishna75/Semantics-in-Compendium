/*
 * UIGoalTypeSelection.java
 *
 * Created on 02-Jul-2009, 11:33:58
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compendium.ui.owl;

import com.compendium.ProjectCompendium;
import com.compendium.ui.dialogs.UIDialog;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Label;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeSelectionModel;


/**
 *
 * @author Krishna Sapkota
 */
public class UIGoalTypeSelection extends UIDialog implements TreeSelectionListener , ActionListener{
    private static final long serialVersionUID = 1;

     /** The JTree object to contain a tree of Goals */
    private  static JTree            tree;

    /** The vector object to hold list of Goals data objects */
    private  Vector<OWLData>    goalListAll;

    private static final String     GOALTYPE   =   "creattype" ;
    private static final String     CANCEL      =   "cancel";

    public UIGoalTypeSelection(JFrame parent){
        super(parent,false);
  
        setLayout(new BorderLayout());
        setLocationRelativeTo(this);
        setTitle("Goals type selection");

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
        DefaultMutableTreeNode top = new DefaultMutableTreeNode("GOAL TYPES");
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
        Label labelTree= new Label("Select a goal type");
        northPanel.add(labelTree);
        add("North",northPanel);
    }

    private void addSouth(){
        JPanel southPanel = new JPanel();
        JButton btnCreateGoal = new JButton("Create a new type ");
        btnCreateGoal.addActionListener(this);
        btnCreateGoal.setActionCommand(GOALTYPE);
        southPanel.add(btnCreateGoal);

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

    private void createTree(DefaultMutableTreeNode top) {
        DefaultMutableTreeNode category = null;

        goalListAll  = UIGoalsTree.goalListAll;

        // gets each element of the the victor as OWLData.
        for (int i = 0; i<goalListAll.size(); i++){
            OWLData cData= goalListAll.elementAt(i);

            // adds Goals type  from Goals data to the tree node.
            category = new DefaultMutableTreeNode(cData.getDataType().toString());
            top.add(category);
        }
    }

     public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();
//      Close the dialog
        if (comm.equals(GOALTYPE)) {
            new UIGoalTypeCreation(ProjectCompendium.APP);
            this.onCancel();
        }
        if (comm.equals(CANCEL)) {
            this.onCancel();
        }
    }

      public void valueChanged(TreeSelectionEvent e) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tree.getLastSelectedPathComponent();

        // If selected node is null, it does not do anty thing.
        if (node == null) return;

        // If  selected node is leaf, it calls the addGoals function.
        if (node.isLeaf()) {
           new UIGoalCreation(ProjectCompendium.APP,  node.toString());
           this.onCancel();  
        } 
    }
}
