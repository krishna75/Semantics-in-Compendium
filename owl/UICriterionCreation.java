/*
 * UICriteriaTypeSelection.java
 *
 * Created on 02-Jul-2009, 11:33:58
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compendium.ui.owl;

import com.compendium.ui.dialogs.UIDialog;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

/**
 *
 * @author Krishna Sapkota
 */
public class UICriterionCreation extends UIDialog implements  ActionListener{
    private static final long serialVersionUID = 1;

    private static final String     CANCEL      =   "cancel";
    private static final String     SAVEIT      =   "save";
    private static String           selectedClassName;
    private static JTextField       tfCriteria;
    private static String           TYPED       =   "typed";

    public UICriterionCreation(JFrame parent,String selectedText){
        super(parent,false);
        selectedClassName = selectedText;
      
        setLayout(new BorderLayout());
        setLocationRelativeTo(this);
        setTitle("Criteria creation");
 
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
        JTextArea txtArea = new JTextArea("You are creating a new criterion under\n \""+ selectedClassName +"\" \nPlease type a new criteria and press save button");
        txtArea.setOpaque(false);
        txtArea.setFont(Font.getFont("Arial"));
        txtArea.setEditable(false);
        northPanel.add(txtArea);
        add("North",northPanel);
    }

    private void addSouth(){
        JPanel southPanel   =   new JPanel();
        JButton btnCreateCriterion = new JButton("Save");
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
            addIndividual();
         }
        
        if (comm.equals(CANCEL)) {
            this.onCancel();
        }
    }

     /**
      * creates an individual of a given class and its description  and adds it to the ontology.
      */
     private void addIndividual(){
        if (!tfCriteria.getText().equals("")){
         ConnectionJenaOWL owl   = new ConnectionJenaOWL();
         owl.addCriterion(tfCriteria.getText(), selectedClassName);

        // The message dialog to display the confirmation of added criteria to the matrix.
        JOptionPane.showMessageDialog(null,
                "New criteria is created successfully!",
                "Confirmation of selection",
                JOptionPane.CLOSED_OPTION);
        this.onCancel();
        } else {
             JOptionPane.showMessageDialog(null, "Empty name is not allowed",
                             "Error on name", JOptionPane.WARNING_MESSAGE);
        }
     }
}
