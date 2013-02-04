/*
 * GoalsCriteriaDialog.java
 *
 * Created on 29 July 2007, 16:00
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This dialog displays the GoalsCriteriaPanel
 *
 * @author Simon Skrzypczak
 */
import com.compendium.ProjectCompendium;
import com.compendium.ui.dialogs.UIDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class GoalsCriteriaDialog extends UIDialog implements ActionListener {
    
    private GoalsCriteriaPanel goalsCriteriaPanel;
    private JPanel southPanel;
    private JPanel southPanelContent;
    private JButton closeWindow;
    private JButton helpbutton;
    private final String CLOSIT = "closit";
    private final String HELP = "help";

    private JTextArea blankSpace1 = new JTextArea();
    private JTextArea blankSpace2 = new JTextArea();

    
    /** Creates a new instance of GoalsCriteriaDialog */
    public GoalsCriteriaDialog(JFrame parent) {
        
        super(parent, false);
        
        this.setTitle("Goals vs Criteria Matrix");

       // this.addWindowListener(this);
        
//      Set the minimum size for the dialog
      //  Dimension d = new Dimension(500, 500);
      //  this.setMinimumSize(d);
        
       this.setLayout(new BorderLayout());

        goalsCriteriaPanel = new GoalsCriteriaPanel();
        

        this.add("Center", goalsCriteriaPanel);
        

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


        helpbutton = new JButton("Help");
        helpbutton.addActionListener(this);
        helpbutton.setActionCommand(HELP);
        southPanelContent.add(helpbutton);

        southPanel.add(southPanelContent);
        this.add("South", southPanel);
        this.pack();

        this.setVisible(true);
    }
    
    public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();
//      Close the dialog
        if (comm.equals(CLOSIT)) {
   
            this.onCancel();
        }
                if (comm.equals(HELP)){
            try {

                HelpDialog help = new HelpDialog(ProjectCompendium.APP,"HelpCriteriaPanel");
            } catch (IOException ex) {
                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }


    
}
