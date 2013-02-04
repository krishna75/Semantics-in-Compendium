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
public class UIGoalCreation extends UIDialog implements  ActionListener{
    private static final long serialVersionUID = 1;

    private static final String     CANCEL      =   "cancel";
    private static final String     SAVEIT      =   "save";
    private static String           selectedClassName;
    private static JTextField       tfGoal;
    private static String           TYPED       =   "typed";

    public UIGoalCreation(JFrame parent,String selectedText){
        super(parent,false);
        selectedClassName = selectedText;
      
        setLayout(new BorderLayout());
        setLocationRelativeTo(this);
        setTitle("Goals creation");

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
        tfGoal.addActionListener(this);
        tfGoal.setActionCommand(TYPED);
        add("Center",tfGoal);
    }

    private void addNorth(){
        // create four panels to add in the dialog
        JPanel northPanel   =   new JPanel();

        JTextArea txtArea = new JTextArea("You are creating a new goal under\n \""+ selectedClassName +"\" \nPlease type a new goal and press save button");
        txtArea.setOpaque(false);
        txtArea.setFont(Font.getFont("Arial"));
        txtArea.setEditable(false);
        northPanel.add(txtArea);

        add("North",northPanel);
    }

    private void addSouth(){
        JPanel southPanel   =   new JPanel();
        JButton btnCreateGoal = new JButton("Save");
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
         if(!tfGoal.getText().equals("")){
             ConnectionJenaOWL cList   = new ConnectionJenaOWL();
             cList.addGoal(tfGoal.getText(), selectedClassName);

            // The message dialog to display the confirmation of added Goals to the matrix.
            JOptionPane.showMessageDialog(null,
                    "New Goals is created successfully!",
                    "Confirmation of selection",
                    JOptionPane.CLOSED_OPTION);
             this.onCancel();
         } else {
           JOptionPane.showMessageDialog(null,
                   "Empty name is not allowed",
                   "Error on name",
                   JOptionPane.WARNING_MESSAGE);

         }
     }
     

}
