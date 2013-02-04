/*
 * UIParametersTypeSelection.java
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
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JOptionPane;

/**
 *
 * @author Krishna Sapkota
 */
public class UIParameterCreation extends UIDialog implements  ActionListener{
    private static final long serialVersionUID = 1;

    private static final String     CANCEL      =   "cancel";
    private static final String     SAVEIT      =   "save";
    private static String           selectedClassName;
    private static JTextField       tfParameter;
    private static JTextField       tfValue;
    private static JTextField       tfUnit;

    public UIParameterCreation(JFrame parent,String selectedText){
        super(parent,false);
        selectedClassName = selectedText;
      
        setLayout(new BorderLayout());
        setLocationRelativeTo(this);
        setTitle("Parameter creation");

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
        JPanel centerPanel= new JPanel();
        centerPanel.setLayout(new GridLayout(3,2));

        JLabel lblParam= new JLabel("Name");
        centerPanel.add(lblParam);
        tfParameter = new JTextField();
        centerPanel.add(tfParameter);

        JLabel lblValue= new JLabel("Value (Symbol)");
        centerPanel.add(lblValue);
        tfValue = new JTextField();
        centerPanel.add("Center",tfValue);

        JLabel lblUnit= new JLabel("Unit");
        centerPanel.add(lblUnit);
        tfUnit = new JTextField();
        centerPanel.add("Center",tfUnit);

        add("Center",centerPanel);
    }

    private void addNorth(){
        // create four panels to add in the dialog
        JPanel northPanel   =   new JPanel();

        JTextArea txtArea = new JTextArea("Creating a new parameter under\n \""+ selectedClassName +"\" \nPlease enter details ");
        txtArea.setOpaque(false);
        txtArea.setFont(Font.getFont("Arial"));
        txtArea.setEditable(false);
        northPanel.add(txtArea);

        add("North",northPanel);
    }

    private void addSouth(){
        JPanel southPanel   =   new JPanel();
        JButton btnCreateParameter = new JButton("Save");
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
         if (!tfParameter.getText().equals("")){
             ConnectionJenaOWL cList   = new ConnectionJenaOWL();
             ParameterData paramData = new ParameterData(tfParameter.getText(),tfValue.getText(),tfUnit.getText());
             cList.addParameter(paramData, selectedClassName);

            // The message dialog to display the confirmation of added Parameters to the matrix.
            JOptionPane.showMessageDialog(null,
                    "New Parameters is created successfully!",
                    "Confirmation of selection",
                    JOptionPane.CLOSED_OPTION);
            this.onCancel();
         } else{
              JOptionPane.showMessageDialog(null,
                   "Empty name is not allowed",
                   "Error on name",
                   JOptionPane.WARNING_MESSAGE);

         }
     }

}
