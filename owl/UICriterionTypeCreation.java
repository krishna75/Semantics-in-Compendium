/*
 * UICriteriaTypeSelection.java
 *
 * Created on 02-Jul-2009, 11:33:58
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
public class UICriterionTypeCreation extends UIDialog implements  ActionListener{
    private static final long serialVersionUID = 1;

    private static final String     CANCEL      =   "cancel";
    private static final String     SAVEIT      =   "save";
    private static final String     TYPED       =   "typed";
    private static JTextField       tfCriteria;
    private static Boolean          typeIsAdded =   false;

    public UICriterionTypeCreation(JFrame parent){
        super(parent,false);
    
        setLayout(new BorderLayout());
        setLocationRelativeTo(this);
        setTitle("Criteria type creation");

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
        tfCriteria = new JTextField();
        tfCriteria.addActionListener(this);
        tfCriteria.setActionCommand(TYPED);
        add("Center",tfCriteria);
    }

    private void addNorth(){
        // create four panels to add in the dialog
        JPanel northPanel   =   new JPanel();

        JTextArea txtArea = new JTextArea("Please type a new criteria type");
        txtArea.setOpaque(false);
        txtArea.setEditable(false);
        northPanel.add(txtArea);
        add("North",northPanel);
    }

    private void addSouth(){
        JPanel southPanel   =   new JPanel();
        JButton btnCreateCriterion = new JButton("Save and create a new criterion");
        btnCreateCriterion.addActionListener(this);
        btnCreateCriterion.setActionCommand(SAVEIT);
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
 * responds to the mouse click on the availabe buttons
 * @param e is an action event perfomed.
 */
     public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();
        if (comm.equals(SAVEIT)) { 
            addCriteriaType();
            if (typeIsAdded) {
                showCriteriaAddingOption();
            }
        }

        if (comm.equals(CANCEL)) {
            this.onCancel();
        }
    }

/**
 * creates new criterion with its label and adds it to the ontology.
 */
     private void addCriteriaType(){
         if (!tfCriteria.getText().equals("")){
             ConnectionJenaOWL cList   = new ConnectionJenaOWL();
             cList.addCriterionType(tfCriteria.getText());
             typeIsAdded = true;
         } else {
             JOptionPane.showMessageDialog(null, "Empty name is not allowed",
                             "Error on name", JOptionPane.WARNING_MESSAGE);
             typeIsAdded = false;
         }
     }

     private void showCriteriaAddingOption(){
      // Question to ask in the showConfirmDialog
            String strQuestion = "New criteria type \"" + tfCriteria.getText() +
                    "\" has been created sucessfully ! \n" +
                    "Do you want to add a new criterion to this type?";

            // It gets an integer value for yes or no from the comfirm dialog.
            int respAdd  =  JOptionPane.showConfirmDialog(null, strQuestion, "Criteria type created", JOptionPane.YES_NO_OPTION);

            // If yes is selected in the confirm dialog, criterion creation option will be shown.
            if (respAdd== JOptionPane.YES_OPTION) {
                new UICriterionCreation(ProjectCompendium.APP,tfCriteria.getText());
                this.onCancel();
            } else {
                this.onCancel();
            }
     }


}
