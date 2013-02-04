/*
 * UIparameterTypeSelection.java
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
public class UIParameterTypeCreation extends UIDialog implements  ActionListener{
    private static final long serialVersionUID = 1;

    private static final String     CANCEL      =   "cancel";
    private static final String     SAVEIT      =   "save";
    private static final String     TYPED       =   "typed";
    private static JTextField       tfParameter;
    private static boolean          typeIsAdded =   false;

    public UIParameterTypeCreation(JFrame parent){
        super(parent,false);

        setLayout(new BorderLayout());
        setLocationRelativeTo(this);
        setTitle("Parameter type creation");

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
        tfParameter = new JTextField();
        tfParameter.addActionListener(this);
        tfParameter.setActionCommand(TYPED);
        add("Center",tfParameter);
    }

    private void addNorth(){
        // create four panels to add in the dialog
        JPanel northPanel   =   new JPanel();

        JTextArea txtArea = new JTextArea("Please type a new parameter type");
        txtArea.setOpaque(false);
        txtArea.setEditable(false);
        northPanel.add(txtArea);

        add("North", northPanel);
    }

    private void addSouth(){
        JPanel southPanel   =   new JPanel();
        JButton btnCreateParameter = new JButton("Save and create a new parameter");
        btnCreateParameter.addActionListener(this);
        btnCreateParameter.setActionCommand(SAVEIT);
        southPanel.add(btnCreateParameter);

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
            addParameterType();
            if(typeIsAdded){
                showParameterAddingOption();
            }
        }
        if (comm.equals(CANCEL)) {
            this.onCancel();
        }
    }

/**
 * creates new Parameter type with its label and adds it to the ontology.
 */
     private void addParameterType(){
         if (!tfParameter.getText().equals("")) {
             ConnectionJenaOWL cList   = new ConnectionJenaOWL();
             cList.addParameterType(tfParameter.getText());
             typeIsAdded = true;
         } else {
             JOptionPane.showMessageDialog(null,
                   "Empty name is not allowed",
                   "Error on name",
                   JOptionPane.WARNING_MESSAGE);
             typeIsAdded = false;
         }
     }

     private void showParameterAddingOption(){

             // Question to ask in the showConfirmDialog
            String strQuestion = "New Parameter type \"" + tfParameter.getText() +
                    "\" has been created sucessfully ! \n" +
                    "Do you want to add a new Parameter to this type?";

            // It gets an integer value for yes or no from the comfirm dialog.
            int respAdd  =  JOptionPane.showConfirmDialog(null, strQuestion, "Parameter type created", JOptionPane.YES_NO_OPTION);

            // If yes is selected in the confirm dialog, the Parameter will be added to the matrix.
            if (respAdd== JOptionPane.YES_OPTION) {
                new UIParameterCreation(ProjectCompendium.APP,tfParameter.getText());
                this.onCancel();
            } else {
                this.onCancel();
            }
     }
}
