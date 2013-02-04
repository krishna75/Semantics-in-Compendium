/*
 * UIGoalsTypeSelection.java
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 *
 * @author Krishna Sapkota
 */
public class UIGoalTypeCreation extends UIDialog implements  ActionListener{
    private static final long serialVersionUID = 1;

    private static final String     CANCEL      =   "cancel";
    private static final String     SAVEIT      =   "save";
    private static final String     TYPED       =   "typed";
    private static JTextField       tfGoal;
    private static Boolean          typeIsAdded =   false;

    public UIGoalTypeCreation(JFrame parent){
        super(parent,false);

        setLayout(new BorderLayout());
        setLocationRelativeTo(this);
        setTitle("Goals type creation");

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
        tfGoal = new JTextField();
        add("Center",tfGoal);
    }

    private void addNorth(){
        // create four panels to add in the dialog
        JPanel northPanel   =   new JPanel();

        JTextArea txtArea = new JTextArea("Please type a new goal type");
        txtArea.setOpaque(false);
        txtArea.setEditable(false);
        northPanel.add(txtArea);

        add("North",northPanel);
    }

    private void addSouth(){
        JPanel southPanel   =   new JPanel();
        JButton btnCreateGoal = new JButton("Save and create a new goal");
        btnCreateGoal.addActionListener(this);
        btnCreateGoal.setActionCommand(SAVEIT);
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

/**
 * responds to the mouse click on the availabe buttons
 * @param e is an action event perfomed.
 */
     public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();
        if (comm.equals(SAVEIT)) { 
            addGoalsType();
            if (typeIsAdded){
                showGoalAddingOption();
            }
        }
        if (comm.equals(CANCEL)) {
            this.onCancel();
        }
    }

/**
 * creates new Goal with its label and adds it to the ontology.
 */
     private void addGoalsType(){
         if (!tfGoal.getText().equals("")){
             ConnectionJenaOWL cList   = new ConnectionJenaOWL();
             cList.addGoalType(tfGoal.getText());
             typeIsAdded = true;
         }else{
              JOptionPane.showMessageDialog(null, "Empty name is not allowed",
                             "Error on name", JOptionPane.WARNING_MESSAGE);
             typeIsAdded = false;
         }
     }

     private void showGoalAddingOption(){
           // Question to ask in the showConfirmDialog
            String strQuestion = "New goal type \"" + tfGoal.getText() +
                    "\" has been created sucessfully ! \n" +
                    "Do you want to add a new goal to this type?";

            // It gets an integer value for yes or no from the comfirm dialog.
            int respAdd  =  JOptionPane.showConfirmDialog(null, strQuestion, "Goal type created", JOptionPane.YES_NO_OPTION);

            // If yes is selected in the confirm dialog, the Goals will be added to the matrix.
            if (respAdd== JOptionPane.YES_OPTION) {
                new UIGoalCreation(ProjectCompendium.APP,tfGoal.getText());
                this.onCancel();
            } else {
                this.onCancel();
            }
     }
}
