/*
 * GoalsCriteriaPanel.java
 *
 * Created on 30 July 2007, 15:40
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This panel displays the Goals vs Criteria Matrix
 *
 * @author Simon Skrzypczak
 */
import com.compendium.ProjectCompendium;
import com.compendium.ui.owl.UICriteriaTree2;

import com.compendium.ui.owl.UIGoalsTree;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;

public class GoalsCriteriaPanel extends JPanel implements ActionListener,TableModelListener {
    
    private JPanel northPanel;
    private JPanel northPanelContent;
    private JPanel westPanel;
    private JPanel westPanelContent;

    private JTable gcTable;
    private static GCMatrix2 goalsCritTable;
    private JScrollPane gcScrollPane;
    
    private JLabel goalsLabel;
    private JLabel criteriaLabel;
    private JLabel tableLabel;
    
    private JButton addGoalsButton;
    private JButton removeGoalsButton;
    private JButton addCriteriaButton;
    private JButton removeCriteriaButton;
    private JButton showRelatedButton;
    private JButton saveButton;
    private JButton critOWLButton;
    private JButton critIssueButton;
    private JButton critProjectButton;
    private JButton removeAllButton;
    
    private JTextArea blankSpace1;
    private JTextArea blankSpace2;
    private JTextArea blankSpace3;
    
    private final String ADDGLS = "addgls";
    private final String REMGLS = "remgls";
    private final String ADDCRT = "addcrt";
    private final String REMCRT = "remcrt";
    private final String SAVIT = "savit";
    private final String SHOWREL= "showrel";
    private final String CRTOWL =  "crtowl";
    private final String CRTISSUE= "crtissue";
    private final String CRTPRJ = "crtprj";
    private final String RMVALL = "rmvall";
    /** Creates a new instance of GoalsCriteriaPanel */
    public GoalsCriteriaPanel() {
        this.setLayout(new BorderLayout());

        
//      North Panel Content        
        northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        
        northPanelContent = new JPanel();
        northPanelContent.setLayout(new GridLayout(1, 0, 10, 10));
        
        blankSpace1 = new JTextArea();
        blankSpace1.setEditable(false);
        blankSpace1.setBackground(northPanelContent.getBackground());
        northPanelContent.add(blankSpace1);
        
        goalsLabel = new JLabel("Goals");
        
        addGoalsButton = new JButton("Add");
        addGoalsButton.addActionListener(this);
        addGoalsButton.setActionCommand(ADDGLS);
        
        removeGoalsButton = new JButton("Remove");
        removeGoalsButton.addActionListener(this);
        removeGoalsButton.setActionCommand(REMGLS);
        
        northPanelContent.add(blankSpace1);
        northPanelContent.add(goalsLabel);
        northPanelContent.add(addGoalsButton);
        northPanelContent.add(removeGoalsButton);
        
        northPanel.add(northPanelContent);
        this.add("North", northPanel);
        
//      West Panel Content
        westPanel = new JPanel();
        westPanel.setLayout(new BorderLayout());
        westPanelContent = new JPanel();
        westPanelContent.setLayout(new GridLayout(0,1,5,5));
        
        blankSpace2 = new JTextArea();
        blankSpace2.setEditable(false);
        blankSpace2.setBackground(westPanelContent.getBackground());
        westPanelContent.add(blankSpace2);
        
        blankSpace3 = new JTextArea();
        blankSpace3.setEditable(false);
        blankSpace3.setBackground(westPanelContent.getBackground());
        westPanelContent.add(blankSpace3);
        
        criteriaLabel = new JLabel("Criteria");
        westPanelContent.add(criteriaLabel);
        
        addCriteriaButton = new JButton("Add");
        addCriteriaButton.addActionListener(this);
        addCriteriaButton.setActionCommand(ADDCRT);
        westPanelContent.add(addCriteriaButton);
        
        removeCriteriaButton = new JButton("Remove");
        removeCriteriaButton.addActionListener(this);
        removeCriteriaButton.setActionCommand(REMCRT);
        westPanelContent.add(removeCriteriaButton);

        removeAllButton = new JButton("Remove all criteria");
        removeAllButton.addActionListener(this);
        removeAllButton.setActionCommand(RMVALL);
        westPanelContent.add(removeAllButton);
        
        tableLabel = new JLabel("Table");
        westPanelContent.add(tableLabel);

        showRelatedButton = new JButton("Show related");
        showRelatedButton.addActionListener(this);
        showRelatedButton.setActionCommand(SHOWREL);
        westPanelContent.add(showRelatedButton);
        
        critOWLButton = new JButton("Show criteria from OWL");
        critOWLButton.addActionListener(this);
        critOWLButton.setActionCommand(CRTOWL);
        westPanelContent.add(critOWLButton);

        critProjectButton = new JButton ("Show criteria used in this project");
        critProjectButton.addActionListener( this);
        critProjectButton.setActionCommand(CRTPRJ);
        westPanelContent.add(critProjectButton);

        critIssueButton = new JButton ("Show criteria used in this issue");
        critIssueButton.addActionListener(this);
        critIssueButton.setActionCommand(CRTISSUE);
        westPanelContent.add(critIssueButton);
        
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        saveButton.setActionCommand(SAVIT);
        westPanelContent.add(saveButton);
        
        westPanel.add("North", westPanelContent);
        this.add("West", westPanel);
        
//      Center Panel Content
        goalsCritTable = new GCMatrix2("Criteria");
        goalsCritTable.addTableModelListener(this);
        gcTable = new JTable(goalsCritTable);
        gcScrollPane = new JScrollPane(gcTable);
        
        this.add("Center", gcScrollPane);
        
        this.setVisible(true);
    }
  
    public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();

//***************************************** Krishna added: ************************************************************************
//     If add criterion button is pressed, it shows a tree panel to choose a criterion from. The tree also provides options to create criterion and criterion type.
        if (comm.equals(ADDCRT)) {
            // Krishna added
            UICriteriaTree2 critTree2= new UICriteriaTree2(ProjectCompendium.APP, goalsCritTable);
            goalsCritTable = critTree2.getMatrix();
            }
        if (comm.equals(CRTOWL)){
          // refreshTable();
            goalsCritTable.removeAllRows();
            goalsCritTable.addCriteriaFromOWL();
        }
       if (comm.equals(CRTPRJ)){
            goalsCritTable.removeAllRows();
            goalsCritTable.addCriteriaFromProject();
        }
        if (comm.equals(CRTISSUE)){
            goalsCritTable.removeAllRows();
            goalsCritTable.addCriteriaFromIssue();
        }

//***************************************** end of Krishna added: ************************************************************************
        
//      Remove a criteria from the matrix selected by the user
        if (comm.equals(REMCRT)) {
            if (goalsCritTable.getRowCount() > 0) {
                int size = goalsCritTable.getRowCount();
//              Create an array of the criteria in the table
                Object[] critInTable = new Object[size];
                for (int i = 0 ; i < size ; i++) {
                    critInTable[i] = goalsCritTable.getValueAt(i, 0);
                }
//              Response is the selected criteria to remove from the matrix
                Object response = JOptionPane.showInputDialog(null,
                        "Please select the criteria to remove:", "Remove Criteria",
                        JOptionPane.QUESTION_MESSAGE, null, critInTable, "Select Criteria");
//              If there is a response then find it's position in the list of
//              criteria so that it can be removed
                if (!response.equals(null)) {
                    int count = 0;
                    boolean found = false;
                    while (!found && count < critInTable.length) {
                        if (response.equals(critInTable[count])) {
                            found = true;
                        } else {
                            count = count + 1;
                        }
                    }
                    goalsCritTable.removeRow(count);
                }
            } else {
//              If no criteria then display the following message
                JOptionPane.showMessageDialog(null, "There are no criteria to remove");
            }
        }

//***************************************** Krishna added: ************************************************************************



        if (comm.equals(RMVALL)){
            goalsCritTable.removeAllRows();
        }
        if (comm.equals(SHOWREL)){
            goalsCritTable.checkAllBoxes();
            JOptionPane.showMessageDialog(null, "Related cells are checked");
        }
//      If add goals button is pressed, it shows a tree panel to choose a goal from. The tree also provides options to create goal and goal type.
        if (comm.equals(ADDGLS)) {
            UIGoalsTree goalsTree = new UIGoalsTree(ProjectCompendium.APP, goalsCritTable);
            goalsCritTable = goalsTree.getMatrix();
        }
//***************************************** end of Krishna added: ************************************************************************

//      Remove a goal from the matrix selected by the user
        if (comm.equals(REMGLS)) {
            if (goalsCritTable.getColumnCount() > 1) {
                int size = goalsCritTable.getColumnCount();
//              Create an array of the goals in the table
                Object[] goalsInTable = new Object[size-1];
                for (int i = 1 ; i < size ; i++) {
                    goalsInTable[i-1] = goalsCritTable.getColumnName(i);
                }
//              Response is the goal to be removed
                Object response = JOptionPane.showInputDialog(null, 
                        "Please select the goal to remove:", "Remove Goal", 
                        JOptionPane.QUESTION_MESSAGE, null, goalsInTable, "");
//              If there is a response then find it's position in the table
//              so that it can be removed
                if (!response.equals(null)) {
                    int count = 0;
                    boolean found = false;
                    while (!found && count < goalsInTable.length) {
                        if (response.equals(goalsInTable[count])) {
                            found = true;
                        } else {
                            count = count + 1;
                        }
                    }
                    goalsCritTable.remDataColumn(count + 1);
                }
            } else {
//              If no goals then display the following message
                JOptionPane.showMessageDialog(null, "There are no goals to remove.");
            }      
        }

//      Save the matrix to the file
        if (comm.equals(SAVIT)) {
            try {
                goalsCritTable.saveGCMatrixToFile("Criteria");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                goalsCritTable.saveGCMatrixToFileToCriteriaList("Criteria");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Changes have been saved.");
        }
    }

    public void closeButtonMessenge(){
        int cont = 0;
                    String message = "Do you want to save these criteria? " ;
            cont = JOptionPane.showConfirmDialog(null, message, "Save Check",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
           if (cont == 0){
            try {
                goalsCritTable.saveGCMatrixToFile("Criteria");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            try {
                goalsCritTable.saveGCMatrixToFileToCriteriaList("Criteria");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Changes have been saved.");
           }
    }

    public void tableChanged(TableModelEvent e) {          //?? Dont know what is this for
     // goalsCritTable.removeTableModelListener( this);      //**TableModelListener defines the interface for an object that listens to changes in a TableModel
        int col = e.getColumn();
        int row = e.getFirstRow();


        if (col > -1 && row > -1) {
//          If a cell has been edited
            Object o =goalsCritTable.getValueAt(row, col);
                if (o.equals(new Boolean(true))){
                    goalsCritTable.addEditableCell(row, col);                   
                }else {
                    goalsCritTable.removeEditableCell(row, col);       
                }
        } else {
//          If a cell hasn't been edited

        }
        //goalsCritTable.addTableModelListener( this);
    }
}
