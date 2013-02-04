/*
 * OCMatrix.java
 *
 * Created on 29 June 2007, 14:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This table model displays the appropriate informtaion from the
 * MatrixContainer class on the screen
 *
 * @author Simon Skrzypczak
 */
import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.ui.*;
import com.compendium.ui.mcdm.ElectreDialog;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.*;
import java.io.*;

import javax.swing.*;
import java.awt.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class OCMatrix extends DefaultTableModel {  //DefaultTableModel is an implementation of TableModel that uses a Vector of Vectors to store the cell value objects
    
    private NodeSummary oNode;                     //** If I need to get a NODE afterwards I will have to refer to this point
    private MatrixContainer matrix;
    private boolean displayValues;


    
    /** Creates a new instance of OCMatrix */
    public OCMatrix(UINode oUINode) throws IOException {
        
        oNode = oUINode.getNode();                              //**Gets the oNode the page that was open refers to
        String nodeId = oNode.getId();                          //**This clarifies which node is it
        
//      Create new instance of MatrixContainer using the UINode
        matrix = new MatrixContainer(oUINode);
        
//      Display the values as the default view
        displayValues = true;
//      Display the details of the MatrixContainer  
        setVector();
    }
    
//  Add a new row to the matrix for a new criteria  
    public void addCriteria(Object criterion) {
        //JulPath: 1-6-1 sends criterion the MatrixContainer to add criterion into the Martix
        matrix.addCriteria(criterion);
      setVector();
    }
    
//  Remove the row containing the criteria selected for removal  
    public void removeCriteria() {
        if (this.getRowCount() > 3) {
//          Create an array of the criteria in the matrix  
            Object[] criteria = new Object[this.getRowCount() - 3];
            for (int i = 0 ; i < this.getRowCount() - 3 ; i++) {
                criteria[i] = this.getValueAt(i, 0);                                      //** Makes a column of the criteiras existent
            }
//          Select the criterion for removal from the 'criteria' array
            Object response = JOptionPane.showInputDialog(null, 
                    "Please select the criteria to remove:", "Remove Criteria", 
                    JOptionPane.QUESTION_MESSAGE, null, criteria, "");
//          If a criterion has been selected from the list then remove it from the
//          matrix
            if (!response.equals(null)) {
                int count = 0;
                boolean found = false;
                while (!found && count < criteria.length) {
                    if (response.equals(criteria[count])) {
                        found = true;
                    } else {
                    count = count + 1;
                    }
                }
                matrix.removeCriteria(count);
            }
        } else {
//          If no criteria in the matrix display the following message
            JOptionPane.showMessageDialog(null, "There are no criteria to remove.");
        }
        setVector();
    }

   public void electreDialog(UINode oUINode) throws FileNotFoundException {

        oNode = oUINode.getNode();
        String nodeId = oNode.getId();
                    ElectreDialog elec = new ElectreDialog(ProjectCompendium.APP, nodeId);
    }

    
//  Set the vector to be displayed in the matrix based on whether the values or
//  formula radio button is selected.
    private void setVector() {
        if (displayValues) {
            displayValues();
        } else {
            displayFormula();
        }
    }
    
//  Set the display to show the values from the matrix  
    public void displayValues() {
        displayValues = true;
        this.setDataVector(matrix.getValuesVector(), matrix.getHeadingsVector());
    }
    
//  Set the display to show the formula from the matrix  
    public void displayFormula() {
        displayValues = false;
        this.setDataVector(matrix.getFormulaVector(), matrix.getHeadingsVector());
    }

    public void chooseMCDMMethod(int method) {

        if (method == 0){
        matrix.totalSum();
        matrix.updateDataValues();
        setVector();
        }
        if (method == 1){
        matrix.totalProduct();
        matrix.updateDataValues();
        setVector();
        }
    }

    public void chooseNormalization(int norm){

        if (norm == 0){
        matrix.normal();
        matrix.updateDataValues();
        setVector();
        }
        if (norm == 1){
        matrix.normalized();
        matrix.updateDataValues();
        setVector();
        }
    }

//  Change the corresponding value/formula in the matrix container to reflect any
//  change made to the matrix displayed in the GUI
    public void changeCellValues(int row, int col) {
        Object data = this.getValueAt(row, col);
        if (displayValues) {
            matrix.changeDataValues(data, row, col, "VAL");
        } else {
            matrix.changeDataValues(data, row, col, "FOR");
        }
        setVector();
    }
    
//  Update the values in the matrix container 
    public void updateCellValues() {
        matrix.updateDataValues();
        setVector();

    }
    
//  Overridden method to set the appropriate cells in the matrix to be non
//  editable
    public boolean isCellEditable(int row, int col) {
        boolean result = true;
        
            if ((row > this.getRowCount() - 4 && row < this.getRowCount())
            || col == this.getColumnCount() - 1 || col == 0) {
                result = false;
            }
//        }
        return result;
    }
    
//  Update the matrix in terms of Options, Decisions, References and 
//  recalculated values
    public void updateMatrix(UINode oUINode) {
        matrix.updateMatrixContainer(oUINode);
        setVector();
    }
    
//  Save the matrix in its current state  
    public void saveMatrixToFile() throws IOException {
        String fileName = oNode.getId();
        matrix.saveMatrixDataToFile(fileName);
    }
    
}
