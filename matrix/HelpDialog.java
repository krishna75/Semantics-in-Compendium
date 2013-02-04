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
import com.compendium.ui.dialogs.UIDialog;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import javax.swing.*;
import java.awt.*;

public class HelpDialog extends UIDialog implements ActionListener {

    private HelpPanel helpPanel;
    private JPanel southPanel;
    private JButton closeWindow;
    private final String CLOSIT = "closit";

    /** Creates a new instance of GoalsCriteriaDialog */
    public HelpDialog(JFrame parent, String choose) throws IOException {

        super(parent, false);

        if(choose.equals("HelpMatrix")){
            this.setTitle("Help Main Matrix");
        }
        if(choose.equals("HelpGlobalParameters")){
            this.setTitle("Help Global Parameters");
        }
        if(choose.equals("HelpELECTRE")){
            this.setTitle("Help ELECTRE Family");
        }

//      Set the minimum size for the dialog
        Dimension d = new Dimension(650, 850);
        this.setMinimumSize(d);

        helpPanel = new HelpPanel(choose);



        this.setLayout(new BorderLayout());
        this.add("Center", helpPanel);


//      South Panel Content
        southPanel = new JPanel();
        closeWindow = new JButton("Close");
        closeWindow.addActionListener(this);
        closeWindow.setActionCommand(CLOSIT);
        southPanel.add(closeWindow);

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
    }

}



