/*
 * GlobalParamDialog.java
 *
 * Created on 27 July 2007, 11:43
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This dialog displays the GlobalParamPanel
 *
 * @author Simon Skrzypczak
 */
import com.compendium.ProjectCompendium;
import com.compendium.ui.dialogs.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GlobalParamDialog extends UIDialog implements ActionListener, WindowListener {
    
    private JFrame oParent;
    private GlobalParamPanel globalParametersPanel;

    private JPanel southPanel;
    private JPanel southPanelContent;
    private JButton closeWindow;
    private JButton helpbutton;
    private final String CLOSIT = "closit";
    private final String HELP = "help";

    private JTextArea blankSpace1 = new JTextArea();
    private JTextArea blankSpace2 = new JTextArea();
    private JTextArea blankSpace3 = new JTextArea();
    
//  This static variable is used to ensure that at any time only one
//  object of this class will be instantiated
    private static boolean state = false;
    
    /** Creates a new instance of GlobalParamDialog */
    public GlobalParamDialog(JFrame parent) {
        super(parent, false);
        
        oParent = parent;
        
//      Show that there is an instance of this class now so that no
//      more can be instantiated
      //  setState(true);
        
        this.setTitle("Global Parameters");
        
//      Add the window listener to listen out for when the window is closed
//      then when this happens set state to false to show that it can be
//      instantiated again
        this.addWindowListener(this);
        
        
        this.setLayout(new BorderLayout());
        
        globalParametersPanel = new GlobalParamPanel(oParent);
        
        this.add("Center", globalParametersPanel);
        
//      South Panel Content

        southPanel = new JPanel();
        southPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        southPanelContent = new JPanel();
        southPanelContent.setLayout(new GridLayout(1, 0, 10, 10));

        southPanel = new JPanel();
        closeWindow = new JButton("Close");
        closeWindow.addActionListener(this);
        closeWindow.setActionCommand(CLOSIT);
        southPanelContent.add(closeWindow);

        blankSpace1.setEditable(false);
        blankSpace1.setBackground(this.getBackground());
        southPanelContent.add(blankSpace1);

        blankSpace2.setEditable(false);
        blankSpace2.setBackground(this.getBackground());
        southPanelContent.add(blankSpace2);

        blankSpace3.setEditable(false);
        blankSpace3.setBackground(this.getBackground());
        southPanelContent.add(blankSpace3);
       

        southPanel = new JPanel();
        helpbutton = new JButton("Help");
        helpbutton.addActionListener(this);
        helpbutton.setActionCommand(HELP);
        southPanelContent.add(helpbutton);

        southPanel.add(southPanelContent);
        this.add("South", southPanel);
        this.pack();
        
        this.setVisible(true);
        
    }
    
//  Get the current state of the class
    public static boolean getState() {
        return state;
    }
    
//  Set the state of the class  
    public static void setState(boolean s) {
        state = s;
    }
    
    public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();
//      Close the dialog
        if (comm.equals(CLOSIT)) {
            globalParametersPanel.closeButtonMessenge();
            setState(false);
            this.onCancel();
        }
        if (comm.equals(HELP)){
            try {

                HelpDialog help = new HelpDialog(ProjectCompendium.APP,"HelpGlobalParameters");
            } catch (IOException ex) {
                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
    }

    public void windowOpened(WindowEvent e) {
    }

    public void windowClosing(WindowEvent e) {
    }

//  Only want to act when the window is closed to set state to false
//  to allow the GlobalParamDialog to be instantiated again
    public void windowClosed(WindowEvent e) {
        setState(false);
    }

    public void windowIconified(WindowEvent e) {
    }

    public void windowDeiconified(WindowEvent e) {
    }

    public void windowActivated(WindowEvent e) {
    }

    public void windowDeactivated(WindowEvent e) {
    }
    
}
