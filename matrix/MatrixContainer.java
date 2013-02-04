/*
 * MatrixContainer.java
 *
 * Created on 19 July 2007, 15:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This class holds the Options vs Criteria matrix data and calculates
 * all necessary values that are updated automatically
 *
 * @author Simon Skrzypczak
 */
import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.ModelSessionException;
import com.compendium.core.datamodel.NodeSummary;
import com.compendium.core.datamodel.View;
import com.compendium.ui.*;
import com.compendium.ui.owl.OcGcConnector;
import java.text.DecimalFormat;
import javax.swing.*;

import java.util.*;
import java.io.*;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MatrixContainer {
    
    private Vector thisHeadings;
    private static Vector thisData;
    private int numberRows;
    private int numberCols;
    private UINode oINode;
    
    int method = 0;
    int norm = 0;
  //  public static Vector<String> critList;

    public MatrixContainer(){
        
    }
    

    
    /** Creates a new instance of MatrixContainer */
//  If no file exists containing the matrix data then create a new Vector
//  for the matrix starting with the basic rows and columns required. The
//  information for this is passed into the constructor from OCMatrix which
//  has done the preparation work already
    public MatrixContainer(UINode oUINode) {
        oINode = oUINode;
        NodeSummary oNode = oUINode.getNode();
        String nodeId = oNode.getId();
        
        try {
            //JulPath: 1-6-1-1 first, for this class to work properly, if there was a matrix already existing, this will have to be loaded
//          Open the matrix from the stored file if one exists
            getMatrixDataFromFile(nodeId);
            
//            OcGcConnector tempFile = new OcGcConnector();                 // Krishna added: three lines
//            tempFile.getCriteriaFromOCMatrix(nodeId);
//          //  tempFile.writeTempCriteria();
        }
        catch (FileNotFoundException fileNotFound) {
            //JulPath: 1-6-1-2 if it does t exist THIS CREATES A NEW MATRIX.
//          Set the initial number of rows and columns in the matrix
            numberRows = 4;
            numberCols = 0;
                        
//          Set the headings vector
            thisHeadings = new Vector(1, 1);
            thisHeadings.add("Criteria");
            numberCols = numberCols + 1;
            
//          Set the nodeId row
            Vector nodeIdRow = new Vector(1, 1);
            nodeIdRow.add(new OCData("Node Id", ""));
            
//          Code to find all the POSITION nodes that link to the ISSUE node to form
//          the options part of the matrix and to create a column for each one using
//          the label of the node as the column heading
//          This code has been taken from another java class file that exists in
//          Compendium so is not entirely the work of the author of this class. It 
//          has been adapted for use in the context of the options vs criteria matrix.
//          It comes from UINodeViewPanel.java in package com.compendium.ui.panels
            for (Enumeration en = oUINode.getLinks();en.hasMoreElements();) {

               //JulPath: 1-6-1-3 add the Criterion from the possition nodes connected.
                UILink uilink = (UILink)en.nextElement();
                String fromNodeId = uilink.getFromNode().getNode().getId();
                
                if (!fromNodeId.equals(oUINode.getNode().getId())) {
                    if (uilink.getFromNode().getType() == ICoreConstants.POSITION || uilink.getFromNode().getType() == ICoreConstants.POSITION_GREEN) {
                        NodeSummary linkNode = (uilink.getFromNode()).getNode();
                        thisHeadings.add(linkNode.getLabel());
                        //JulPath: 1-6-1-4 send the ID of the position node to the nodeIdrow in the MATRIX
                        nodeIdRow.add(new OCData(linkNode.getId(), ""));
                        numberCols = numberCols + 1;
                    }
                }
            }
            
//          Add the weight and normalisation factor columns to the matrix
             //JulPath: 1-6-1-5 Add the two other Heading into the last columns of the matrix
            thisHeadings.add("Weight");
            thisHeadings.add("Normalisation Factor");
            nodeIdRow.add(new OCData());
            nodeIdRow.add(new OCData());
            numberCols = numberCols + 2;
            
//          Create the data vector
            thisData = new Vector(numberRows, 1);
            for (int i = 0 ; i < numberRows ; i++) {
                thisData.add(new Vector(numberCols, 1));
            }
            
//          Populate the data vector
            
//          Add the totals row
            Vector totalsRow = (Vector)thisData.elementAt(0);
            totalsRow.add(new OCData("Totals", ""));
            for (int i = 1 ; i < numberCols ; i++) {
                totalsRow.add(new OCData());
            }
            
//          Add the system recommended row
            Vector sysRecRow = (Vector)thisData.elementAt(1);
            sysRecRow.add(new OCData("System Recommended", ""));
            for (int i = 1 ; i < numberCols ; i++) {
                sysRecRow.add(new OCData());
            }
            
//          Add the decision row
            Vector decRow = (Vector)thisData.elementAt(2);
            decRow.add(new OCData("Decision", ""));
            for (int i = 1 ; i < numberCols ; i++) {
                decRow.add(new OCData());
            }
            
//          Add the node Id row
            Vector nodeRow = (Vector)thisData.elementAt(3);
            Iterator nodeIdRowIter = nodeIdRow.iterator();
            while (nodeIdRowIter.hasNext()) {
                nodeRow.add(nodeIdRowIter.next());
            }
        }
                   setTotal();
    }
    
//  Constructor for a matrix for which a known file exists
    public MatrixContainer(String nodeId) throws FileNotFoundException {
        getMatrixDataFromFile(nodeId);
                   setTotal();
    }
    
//  Return the vector containing the column headings of the matrix
    public Vector getHeadingsVector(){
        return thisHeadings;
    }

    public Vector getDataVector(){

        return thisData;
    }
// This goes to the ElectrePanel to set the Veto parameters
    public int getRowCount() {

        int result = numberRows - 4;
        return result;
    }

    
//  Return the vector containing the values in the matrix
    public Vector getValuesVector() {
//      Create the new Vector which will hold the values from thisData Vector
        Vector displayValues = new Vector(numberRows - 1);
        for (int i = 0 ; i < numberRows - 1 ; i++) {
            displayValues.add(i, new Vector(numberCols));
        }
        
//      Populate the new Vector values with the values
        for (int row = 0 ; row < numberRows - 1 ; row++) {
            Vector fromRow = (Vector)thisData.elementAt(row);
            Vector toRow = (Vector)displayValues.elementAt(row);
            for (int col = 0 ; col < numberCols ; col++) {
                String thisValue = ((OCData)fromRow.elementAt(col)).getValue();
                String value = "";
                if (!thisValue.equals("")) {
                    value = formatDataValue(row, col, thisValue);
                } else {
                    value = thisValue;
                }
                toRow.add(col, value);
            }
        }
        return displayValues;
    }
    
//  Return the vector containing the formula in the matrix
    public Vector getFormulaVector() {
//      Create the new Vector which will hold the formula from thisData Vector
        Vector formula = new Vector(numberRows - 1);
        for (int i = 0 ; i < numberRows - 1 ; i++) {
            formula.add(i, new Vector(numberCols));
        }
        
//      Populate the new Vector formula with the formula
//      If there is no formula in the current location then display the value instead
        for (int row = 0 ; row < numberRows - 1 ; row++) {
            Vector fromRow = (Vector)thisData.elementAt(row);
            Vector toRow = (Vector)formula.elementAt(row);
            for (int col = 0 ; col < numberCols ; col++) {
                if (!((OCData)fromRow.elementAt(col)).getFormula().equals("")) {
                    toRow.add(col, ((OCData)fromRow.elementAt(col)).getFormula());
                } else {
                    String thisValue = ((OCData)fromRow.elementAt(col)).getValue();
                    String value = "";
                    if (!thisValue.equals("")) {
                        value = formatDataValue(row, col, thisValue);
                    } else {
                        value = thisValue;
                    }
                    toRow.add(col, value);
                }
            }
        }
        return formula;        
    }






    
//  Method to format the data values in the given cell to make it presentable in
//  the matrix in a useful way
    private String formatDataValue(int row, int col, String thisValue) {
        String value = "";
//      If there are rows in the table with data values in them i.e. there are
//      criteria rows appearing in the table
        if (numberRows > 4) {
//          If the value in the cell is simply text then attempting to format it
//          will cause an NumberFormatException. This needs to be caught and dealt
//          with. If the value is just text and not a number then the code below
//          will see that and will not attempt to format it but will just display
//          it as it is.
            try {
                double val = Double.valueOf(thisValue);
                
//              Set the format for the cells that hold data values in the table
//              or holds the Normalisation Factor value
                if ((row > -1 && row < numberRows - 4) && ((col > 0 && col < numberCols - 1) || (col == numberCols - 1))) {
                    DecimalFormat valueFormat;
                    if (val == 0.0) {
                        valueFormat = new DecimalFormat("0");
                    } else if ((val >= 0.0001 && val < 100000) || (val <= -0.0001 && val > -100000)) {
                        valueFormat = new DecimalFormat("0.####");
                    } else {
                        valueFormat = new DecimalFormat("0.0##E0");
                    }
                    value = valueFormat.format(val);
//              Set the format for the cells that hold the totals in the table
                } else if (row == numberRows - 4 && (col > 0 && col < numberCols - 2)) {
                    DecimalFormat valueFormat = new DecimalFormat("0.##");
                    value = valueFormat.format(val);
                } else {
                    value = thisValue;
                }
            }
//          If exception is caught then leave the value as it is without formatting it
            catch (NumberFormatException e) {
                value = thisValue;
            }
        } else {
//          If there are no criteria rows then just format the cells that hold
//          the totals values in them
            if (row == numberRows - 4 && (col > 0 && col < numberCols - 2)) {
                DecimalFormat valueFormat = new DecimalFormat("0.##");
                double val = Double.valueOf(thisValue);
                value = valueFormat.format(val);
            } else {
                value = thisValue;
            }
        }
        return value;
    }
    
//  Add a new row to the matrix for a new criteria to be added
    public void addCriteria(Object criterion) {
        Vector newRow = new Vector(numberCols, 1);
//      Set first element in the vector to be the criterion name
        //JulPath: 1-6-1-6 create a vector with (a given number of columns and 1 row)
        //JulPath: 1-6-1-7 this add the name of the criteria to column 0
        newRow.add(0, new OCData(String.valueOf(criterion), ""));
        //JulPath: 1-6-1-8 this increases the newrow vector to get the given number of data falowing the number of cols.
        for (int i = 1 ; i < numberCols-2 ; i++) {
            newRow.add(i, new OCData());
        }
        //JulPath: 1-6-1-9 this increases the newrow vector to input the WEIGHT
        newRow.add(numberCols-2, new OCData("1", ""));
        //JulPath: 1-6-1-10 this increases the newrow vector to calculate the normalisation
        newRow.add(numberCols-1, new OCData());
//      Insert the new row directly before the totals row in the matrix
        thisData.insertElementAt(newRow, numberRows - 4);
        numberRows = numberRows + 1;

//        OcGcConnector tempFile = new OcGcConnector();                            // Krishna added:
//        tempFile.addCriterion(criterion.toString());
    }
    
//  Remove the row at the given location to remove that criteria from the matrix
    public void removeCriteria(int row) {
        thisData.removeElementAt(row);
        numberRows = numberRows - 1;
//
//        OcGcConnector tempFile = new OcGcConnector();                             // Krishna added:
//        tempFile.removeCriterion(row);
    }
    
//  Method to change the value or formula at the given location
    public void changeDataValues(Object data, int rowNo, int colNo, String changeId) {
//      Get the row containing the cell to be changed
        Vector rowChange = (Vector)thisData.elementAt(rowNo);
//      Get the column of the cell to be changed
        OCData cellChange = (OCData)rowChange.elementAt(colNo);
//      Set the new value or formula of the cell
        if (changeId.equals("VAL")) {
            cellChange.setValue((String)data);
        } else {
            cellChange.setFormula((String)data);
        }
        rowChange.setElementAt(cellChange, colNo);
//      Update the values in the matrix
        updateDataValues();

    }
    
//  This method will update the references that are contained within the matrix
//  by accessing the reference value from the Global Parameters file and getting
//  the current value. Will also update any references to Excel files.
//  This is placed separately to the updateDataValues() method
//  as it is called whenever any change occurs to the matrix such as adding a new
//  row or changing a value in the matrix. Only need to update the references when
//  the Update button is pressed.
    public void updateMatrixReferences() {
        
        if (numberCols > 3 && numberRows > 4) {
//          Update all the formula by getting the latest values from any
//          references contained within them
//          This is done by setting the formula attribute of the OCData type
//          with the formula currently stored so that it is recalculated
            for (int row = 0 ; row < numberRows - 4 ; row++) {
                Vector thisRow = (Vector)thisData.elementAt(row);
                for (int col = 1 ; col < numberCols - 2 ; col++) {
                    OCData temp = (OCData)thisRow.elementAt(col);
                    String tempFormula = temp.getFormula();
                    if (!tempFormula.equals("")) {
                        temp.setFormula(tempFormula);
                    }
                }
            }            
        }

    }
    
//  Update the values in the matrix by recalculating the normalisation factor
//  column, then the totals row, then the system recommendation row


    public void updateDataValues() {



        if (numberCols > 3 && numberRows > 4) {
            
//          Update the normalisation factor values
            for (int normRow = 0 ; normRow < numberRows - 4; normRow++) {
                Vector thisRow = (Vector)thisData.elementAt(normRow);
                OCData temp = (OCData)thisRow.elementAt(numberCols - 1);
                temp.setValue(String.valueOf(calculateNormFactor(normRow)));
                thisRow.setElementAt(temp, numberCols - 1);
            }
//          Update the totals row of the vector matrix
            for (int col = 1 ; col < numberCols - 2 ; col++) {
                Vector thisRow = (Vector)thisData.elementAt(numberRows - 4);
                OCData temp = (OCData)thisRow.elementAt(col);
                temp.setValue(String.valueOf(calculateTotals(col)));
                thisRow.setElementAt(temp, col);
            }
//          Create an array of Totals, SystemRecommendation, Decision and Node ID
//          rows for updating the system recommendation row of the table
            Object[][] compare = new Object[4][numberCols - 3];
                for (int row = 0 ; row < 4 ; row++) {
                    Vector thisRow = (Vector)thisData.elementAt(numberRows - 4 + row);
                    for (int col = 0 ; col < numberCols - 3 ; col++) {
                        if (row == 0) {
                            double total = Double.valueOf(((OCData)thisRow.elementAt(col+1)).getValue());
                            DecimalFormat valueFormat = new DecimalFormat("0.##");
                            String strTotal = valueFormat.format(total);
                            compare[row][col] = strTotal;
                        } else {
                            compare[row][col] = ((OCData)thisRow.elementAt(col+1)).getValue();
                        }
                    }
                }
            makeRecommendation(compare);
//      If there are no criteria in the matrix then set all totals and
//      system recommendations to "". Leave any decisions as they are
//      as they are user controlled
        } else if (numberRows <= 4) {
            for (int row = 0 ; row < 2 ; row++) {
                Vector thisRow = (Vector)thisData.elementAt(row);
                for (int col = 1 ; col < numberCols - 2 ; col++) {
                    OCData temp = (OCData)thisRow.elementAt(col);
                    temp.setValue("");
                    thisRow.setElementAt(temp, col);
                }
            }
        }
                  String positions = null;
                  String bestAlternative = null;
                for (Enumeration en = oINode.getLinks();en.hasMoreElements();) {

               //JulPath: 1-6-1-3 add the Criterion from the possition nodes connected.
                UILink uilink = (UILink)en.nextElement();
                String fromNodeId = uilink.getFromNode().getNode().getId();

                if (!fromNodeId.equals(oINode.getNode().getId())) {
                    if (uilink.getFromNode().getType() == ICoreConstants.POSITION || uilink.getFromNode().getType() == ICoreConstants.POSITION_GREEN) {
                        NodeSummary linkNode = (uilink.getFromNode()).getNode();

                        positions = (linkNode.getLabel());
                        bestAlternative = getRecommendation();
                    try {
                        linkNode.setType(4, "Julian Hunt");
                    } catch (SQLException ex) {
                        Logger.getLogger(MatrixContainer.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ModelSessionException ex) {
                        Logger.getLogger(MatrixContainer.class.getName()).log(Level.SEVERE, null, ex);
                    }

                        if(positions.equals(bestAlternative)){
                            try {
                                linkNode.setType(52, "Julian Hunt");
               
                            } catch (SQLException ex) {
                                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ModelSessionException ex) {
                                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                }
                }
    }
    
//  Calculate the normalisation factor for the given row
    private double calculateNormFactor(int row) {
        
        double normFactor = 1.0;
        double maxValue = Double.NEGATIVE_INFINITY;
        double minValue = Double.POSITIVE_INFINITY;
        
        Vector thisRow = (Vector)thisData.elementAt(row);
        for (int col = 1 ; col < numberCols - 2 ; col++) {
            double val = strToDouble(((OCData)thisRow.elementAt(col)).getValue());
            if (val > maxValue) {
                maxValue = val;
            }
            if (val < minValue) {
                minValue = val;
            }
        }

        if (maxValue == 0.0 && minValue == 0.0) {
            normFactor = 1.0;
        } else if (maxValue > -minValue) {
            normFactor = 1 / maxValue;
        } else {
            normFactor = 1 / -minValue;
        }

        return normFactor;
    }
    
    
    private void setTotal() {
        if (method == 0) {
            totalSum();
                    
        }
        if (method ==1)  {
            totalProduct();

        }
        if (norm == 0)  {
            normal();

        }
        if (norm == 1){
            normalized();

        }

    }
    
//  Set the display to show the values from the matrix  
    public void totalSum() {
        method = 0;
    }
    
//  Set the display to show the formula from the matrix  
    public void totalProduct() {
        method = 1;
    }

    public void normal() {

      norm = 0;

    }

    public void normalized(){

      norm =1;
    }


//  Calculate the total for the given column
    private double calculateTotals(int column) {


    setTotal();
        // check = callingMethod.getVariableAnswer();

        double sum = 1.0;

        if (norm == 0) {
        if (method == 0) {

        for (int row = 0 ; row < numberRows - 4; row++) {
            Vector thisRow = (Vector)thisData.elementAt(row);
   // This IS THE WSM
            sum += (strToDouble(((OCData)thisRow.elementAt(column)).getValue()) *
                    strToDouble(((OCData)thisRow.elementAt(numberCols - 2)).getValue())) ;
                //     strToDouble(((OCData)thisRow.elementAt(numberCols - 1)).getValue()));     //   The numberCols - 1 is the normalization colunm
        }
            sum -= 1; 
    }

        if (method == 1) {


        for (int row = 0 ; row < numberRows - 4; row++) {
            Vector thisRow = (Vector)thisData.elementAt(row);
        
   // THIS IS THE WPM
            sum *= Math.pow(strToDouble(((OCData)thisRow.elementAt(column)).getValue()),strToDouble(((OCData)thisRow.elementAt(numberCols - 2)).getValue()));
        }
        }
        }
        if (norm == 1){

        if (method == 0) {

        for (int row = 0 ; row < numberRows - 4; row++) {
            Vector thisRow = (Vector)thisData.elementAt(row);
   // This IS THE WSM
            sum += (strToDouble(((OCData)thisRow.elementAt(column)).getValue()) *
                    strToDouble(((OCData)thisRow.elementAt(numberCols - 2)).getValue()) *
                    strToDouble(((OCData)thisRow.elementAt(numberCols - 1)).getValue()));     //   The numberCols - 1 is the normalization colunm
        }
            sum -= 1;
    }

        if (method == 1) {


        for (int row = 0 ; row < numberRows - 4; row++) {
            Vector thisRow = (Vector)thisData.elementAt(row);

   // THIS IS THE WPM
            sum *= Math.pow(strToDouble(((OCData)thisRow.elementAt(column)).getValue())*strToDouble(((OCData)thisRow.elementAt(numberCols - 1)).getValue()),strToDouble(((OCData)thisRow.elementAt(numberCols - 2)).getValue()));
        }
        }
        }


        return sum;
    }
    
//  Compare the totals to make the appropriate system recommendation
    private void makeRecommendation(Object[][] compareData) {
        
//      Bubble sort of the compareData array to get the totals in descending order
//      i.e. largest value at the left, smallest at the right
        for (int i = compareData[0].length-1 ; i > -1 ; i--) {
            for (int j = 0 ; j < i ; j++) {
                if (strToDouble(String.valueOf(compareData[0][j])) < strToDouble(String.valueOf(compareData[0][j+1]))) {
                    for (int r = 0 ; r < 4 ; r++) {
                        Object temp = compareData[r][j];
                        compareData[r][j] = compareData[r][j+1];
                        compareData[r][j+1] = temp;
                    }
                }
            }
        }
        
//      Set the System Recommended cells where total is the maximum to "Recommended"
        int i = 0;
        int j = 1;
        compareData[1][i] = "Recommended";
        while ((j < compareData[0].length) && ((strToDouble(String.valueOf(compareData[0][i])) == strToDouble(String.valueOf(compareData[0][j]))))) {
            compareData[1][j] = "Recommended";
            i = i + 1;
            j = j + 1;
        }
//      Set the remaining cells to blank
        for (int k = j ; k < compareData[0].length ; k++) {
            compareData[1][k] = "";
        }
        
//      Set the data vector to reflect the system recommendations
        Vector nodeIdRow = (Vector)thisData.elementAt(numberRows - 1);
        Vector sysRecRow = (Vector)thisData.elementAt(numberRows - 3);
        for (int col = 1 ; col < numberCols - 2 ; col++) {
            for (int compCol = 0 ; compCol < compareData[0].length ; compCol++) {
                if (((OCData)nodeIdRow.elementAt(col)).getValue().equals((String)compareData[3][compCol])) {
                    OCData temp = (OCData)sysRecRow.elementAt(col);
                    temp.setValue(String.valueOf(compareData[1][compCol]));
                    sysRecRow.setElementAt(temp, col);
                }
            }
        }
    }
    
//  Returns the current recommended option (or options)
    public String getRecommendation() {
        Vector nodeIdRow = (Vector)thisData.elementAt(numberRows - 1);
        Vector sysRecRow = (Vector)thisData.elementAt(numberRows - 3);
        String result = "";
        if (numberCols > 3) {
            for (int col = 1 ; col < numberCols - 2 ; col++) {
                if (((OCData)sysRecRow.elementAt(col)).getValue().equals("Recommended")) {
                    if (result.equals("")) {
                        result = result + String.valueOf(thisHeadings.elementAt(col));
                    } else {
                        result = result + ", " + String.valueOf(thisHeadings.elementAt(col));
                    }
                }
            }
        }
        return result;
    }
    
//  Returns boolean result of whether the given parameter appears in the matrix
//  Can be used in conjunction with the sensitivity analysis
    public boolean containsParameter(String paramName) {
        boolean result = false;
        int row = 0;
        if (numberRows > 4 && numberCols > 3) {
            while (!result && row < numberRows - 4) {
                Vector thisRow = (Vector)thisData.elementAt(row);
                for (int col = 1 ; col < numberCols - 2 ; col++) {
                    OCData temp = (OCData)thisRow.elementAt(col);
                    if (temp.getFormula().contains(paramName)) {
                        result = true;
                    }
                }
                row = row + 1;       
            }
        }
        return result;
    }
    
//  Convert the given string to a double value
    private double strToDouble(String str) {
        
        double value = 0.0;
//      If can't convert the string to a double then set the value to 0
        if (!str.equals("")) {
            try {
                value = Double.valueOf(str);
            }
            catch(NumberFormatException invalidInput) {
                value = 0.0;
            }
        }
        return value;
    }
    
//  This method is used to update the matrix structure when the matrix is open
//  and the user presses the update button on the matrix GUI. It performs the
//  update based on the UINode for the Issue node in question. It also updates
//  all references and values in the matrix
    public void updateMatrixContainer(UINode oUINode) {
        
            setTotal();

        FirstInFirstOut decListId = new FIFOLinkedList();
        FirstInFirstOut nodeIdList = new FIFOLinkedList();
//      Checking for new nodes to add to the matrix
        for (Enumeration en = oUINode.getLinks();en.hasMoreElements();) {
            
            UILink uilink = (UILink)en.nextElement();
            String fromNodeId = uilink.getFromNode().getNode().getId();
            
            if (!fromNodeId.equals(oUINode.getNode().getId())) {
                if (uilink.getFromNode().getType() == ICoreConstants.POSITION || uilink.getFromNode().getType() == ICoreConstants.POSITION_GREEN) {
                    
//                  Check if node is already in the matrix and add if it isn't
                    NodeSummary linkNode = (uilink.getFromNode()).getNode();
                    nodeIdList.add(linkNode.getId());
                    
                    Vector nodeIdRow = (Vector)thisData.elementAt(numberRows - 1);
                    int noOfSameNodes = 0;
                    for (int col = 1 ; col < numberCols - 2 ; col++) {
                        if (linkNode.getId().equals(((OCData)nodeIdRow.elementAt(col)).getValue())) {
                            noOfSameNodes = noOfSameNodes + 1;
//                          This code is invoked only if the nodeId already appears in
//                          the matrix and is therefore not a new node. It checks to
//                          see if the label has been changed and if so it updates the
//                          appropriate header in thisHeadings.
                            if (!linkNode.getLabel().equals(String.valueOf(thisHeadings.elementAt(col)))) {
                                thisHeadings.setElementAt(linkNode.getLabel(), col);
                            }                            
                        }
                    }
//                  If the node is new then add it to the matrix
                    if (noOfSameNodes == 0) {
                        addMatrixData(linkNode.getId(), linkNode.getLabel());
                    }
                    
//                  Check if the position node has a decision node attached to it
                    UINode posNode = uilink.getFromNode();
                    for (Enumeration findDec = posNode.getLinks() ; findDec.hasMoreElements() ; ) {
                        
                        UILink posLink = (UILink)findDec.nextElement();
                        String fromPosNodeId = posLink.getFromNode().getNode().getId();
                        
                        String toPosNode = posLink.getToNode().getText();
                        if (!fromPosNodeId.equals(posNode.getNode().getId())) {
                            if (posLink.getFromNode().getType() == ICoreConstants.DECISION) {
                                decListId.add(posNode.getNode().getId());
                            }
                        }
                    }
                }
            }
        }
//      Check for any nodes no longer attached to the ISSUE node and remove
//      from the matrix
        Vector nodeIdRow = (Vector)thisData.elementAt(numberRows - 1);
        for (int col = 1 ; col < numberCols - 2 ; col++) {
            int noOfSameNodes = 0;
            Iterator nodeIdListIter = nodeIdList.iterator();
            while (nodeIdListIter.hasNext()) {
                if (((OCData)nodeIdRow.elementAt(col)).getValue().equals(String.valueOf(nodeIdListIter.next()))) {
                    noOfSameNodes = noOfSameNodes + 1;
                }
            }
//          If the node should no longer be in the matrix remove it
            if (noOfSameNodes == 0) {
                removeMatrixData(col);
//              Set col value back by 1 to ensure that the column that just been shifted
//              into the space left by the column that has been removed is also checked to see if
//              it also needs to be removed from the matrix
                col = col - 1;                
            }
        }
        
//      Set all decision cells to blank and then work through the decListId one by
//      one and set the appropriate cells to "Selected"
        Vector decRow = (Vector)thisData.elementAt(numberRows - 2);
        for (int col = 1 ; col < numberCols - 2 ; col++) {
            OCData temp = (OCData)decRow.elementAt(col);
            temp.setValue("");
            decRow.setElementAt(temp, col);
        }
        Iterator decListIter = decListId.iterator();
        while (decListIter.hasNext()) {
            String nodeId = decListIter.next().toString();
            for (int col = 1 ; col < numberCols - 2 ; col++) {
                OCData temp = (OCData)decRow.elementAt(col);
                if (nodeId.equals(((OCData)nodeIdRow.elementAt(col)).getValue())) {
                    temp.setValue("Selected");
                }
                decRow.setElementAt(temp, col);
            }
        }
//      Once the structure of the matrix has been updated, get the latest values
//      for any references contained within the matrix and update all the values
        updateMatrixReferences();
        updateDataValues();
    }
    
//  This method is used to update the structure of the matrix when the user performs a
//  global propagate across the whole project. A UINode is not available at this point
//  so a list of attached nodes to the Issue node is passed to this method along with
//  details of any Position nodes that have a Decision node attached to them
//  Once the structural update is complete update the references and values in
//  the matrix
    public void updateMatrixContainer(FirstInFirstOut posNodeList, FirstInFirstOut decList) {

            setTotal();

//      Check for new nodes to add to the matrix
        Iterator posNodeListIter = posNodeList.iterator();
        Vector nodeIdRow = (Vector)thisData.elementAt(numberRows - 1);
        while (posNodeListIter.hasNext()) {
            NodeSummary posNode = (NodeSummary)posNodeListIter.next();
            String posNodeId = posNode.getId();
            String posNodeLabel = posNode.getLabel();
            int noOfSameNodes = 0;
//          Check if the current node in the list appears in the matrix
            for (int col = 1 ; col < numberCols - 2 ; col++) {
                if (posNodeId.equals(((OCData)nodeIdRow.elementAt(col)).getValue())) {
                    noOfSameNodes = noOfSameNodes + 1;
//                  This code is invoked only if the nodeId already appears in
//                  the matrix and is therefore not a new node. It checks to
//                  see if the label has been changed and if so it updates the
//                  appropriate header in thisHeadings.
                    if (!posNodeLabel.equals(String.valueOf(thisHeadings.elementAt(col)))) {
                        thisHeadings.setElementAt(posNodeLabel, col);
                    }                    
                }
            }
//          If the node is new then add it to the matrix
            if (noOfSameNodes == 0) {
                addMatrixData(posNodeId, posNodeLabel);
            }
        }
        
//      Check for any nodes no longer attached to the ISSUE node and remove
//      from the matrix
        for (int col = 1 ; col < numberCols - 2 ; col++) {
            int noOfSameNodes = 0;
            Iterator posNodeListIter1 = posNodeList.iterator();
            while (posNodeListIter1.hasNext()) {
                NodeSummary posNode = (NodeSummary)posNodeListIter1.next();
                String posNodeId = posNode.getId();
                if (((OCData)nodeIdRow.elementAt(col)).getValue().equals(posNodeId)) {
                    noOfSameNodes = noOfSameNodes + 1;
                }
            }
//          If the node should no longer be in the matrix remove it
            if (noOfSameNodes == 0) {
                removeMatrixData(col);
//              Set col value back by 1 to ensure that the column that just been shifted
//              into the space left by the column that has been removed is also checked to see if
//              it also needs to be removed from the matrix
                col = col - 1;                
            }
        }
        
//      Set all decision cells to blank and then work through the decListId one by
//      one and set the appropriate cells to "Selected"
        Vector decRow = (Vector)thisData.elementAt(numberRows - 2);
        for (int col = 1 ; col < numberCols - 2 ; col++) {
            OCData temp = (OCData)decRow.elementAt(col);
            temp.setValue("");
            decRow.setElementAt(temp, col);
        }
        Iterator decListIter = decList.iterator();
        while (decListIter.hasNext()) {
            String nodeId = decListIter.next().toString();
            for (int col = 1 ; col < numberCols - 2 ; col++) {
                OCData temp = (OCData)decRow.elementAt(col);
                if (nodeId.equals(((OCData)nodeIdRow.elementAt(col)).getValue())) {
                    temp.setValue("Selected");
                }
                decRow.setElementAt(temp, col);
            }
        }
//      Once the structure of the matrix has been updated, get the latest values
//      for any references contained within the matrix and update all the values
        updateMatrixReferences();
        updateDataValues();
    }
    
//  Add a new option to the matrix using the given node Id and node Label
    public void addMatrixData(String nodeId, String nodeLabel) {
        thisHeadings.insertElementAt(nodeLabel, numberCols - 2);
        
        for (int row = 0 ; row < numberRows ; row++) {
            Vector thisRow = (Vector)thisData.elementAt(row);
            OCData temp;
            if (row == numberRows - 1) {
                temp = new OCData(nodeId, "");
            } else {
                temp = new OCData();    
            }
            thisRow.insertElementAt(temp, numberCols - 2);
        }
        numberCols = numberCols + 1;
    }
    
//  Remove the option at the given column from the matrix
    public void removeMatrixData(int col) {
        thisHeadings.removeElementAt(col);
        
        for (int row = 0 ; row < numberRows ; row++) {
            Vector thisRow = (Vector)thisData.elementAt(row);
            thisRow.removeElementAt(col);
        }
        numberCols = numberCols - 1;
    }
    
//  Open the appropriate matrix file and read in the data from it to populate the
//  matrix
    private void getMatrixDataFromFile(String nodeId) throws FileNotFoundException {
        
//      Use the ProjectCompendium class to get the homepath of the installation
//      as this may vary depending on the platform and the way the user installed it
        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Project Files/";
        
        String projectName = ProjectCompendium.APP.getProjectName();
        String filePath = pathName + projectName + "/Matrix Files/" + nodeId + ".csv";
        
        File file = new File(filePath);
        
        Scanner lineFromFile = new Scanner(file);
//      Get first line from file which contains the number of rows and columns  
        String intLine = lineFromFile.nextLine();
        Scanner readLine = new Scanner(intLine).useDelimiter(",");
        int rows = readLine.nextInt();
        int cols = readLine.nextInt();
        
        numberRows = rows;
        numberCols = cols;
        
//      Get the line containing the column heading information from the file  
        String line = lineFromFile.nextLine();
        readLine = new Scanner(line).useDelimiter(",");
        thisHeadings = new Vector(cols, 1);
        for (int i = 0 ; i < cols ; i++) {
            thisHeadings.add(i, readLine.next());
        }
        
//      Get the remaining rows from the file which contains the data and totals information  
        
//      Create the Vector to store the data in
        thisData = new Vector(rows, 1);
        for (int i = 0 ; i < rows ; i++) {
            thisData.add(i, new Vector(cols, 1));
        }
        
        for (int r = 0 ; r < rows ; r++) {
            Vector newRow = (Vector)thisData.elementAt(r);
            line = lineFromFile.nextLine();
            readLine = new Scanner(line).useDelimiter(",");
            for (int c = 0 ; c < cols ; c++) {
                String value = "";
                String formula = "";
                if (readLine.hasNext()) {
                    value = readLine.next();
                }
                if (readLine.hasNext()) {
                    formula = readLine.next();
                }
                OCData data = new OCData(value, formula);
                newRow.add(c, data);
            }
        }
    }
    
//  Store the current matrix to a file of the given filename
//  The filename will be the node id
    public void saveMatrixDataToFile(String fileName) throws IOException {
        
//      Use the ProjectCompendium class to get the homepath of the installation
//      as this may vary depending on the platform and the way the user installed it
        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Project Files/";
        
        String projectName = ProjectCompendium.APP.getProjectName();
        String dirPath = pathName + projectName + "/Matrix Files";
        String filePath = dirPath + "/" + fileName + ".csv";
        
        File directory = new File(dirPath);
        
        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                JOptionPane.showMessageDialog(null, "The necessary folders were not able to be" +
                        "created.\nPlease check the folder locations.", "Folder Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
        
        FileWriter newFile = new FileWriter(filePath);
        
//      Write the number of rows and columns to the file  
        String rowCol = numberRows + "," + numberCols;
        newFile.write(rowCol + "\r\n");
        
//      Write the column headings to the file
        String headings = "";
        for (int col = 0 ; col < numberCols - 1 ; col++) {
            headings = headings + thisHeadings.elementAt(col) + ",";
        }
        headings = headings + thisHeadings.elementAt(numberCols - 1);
        
        newFile.write(headings + "\r\n");
        
//      Write the data, node Id, totals and decision rows to the file
        for (int row = 0 ; row < numberRows ; row++) {
            String dataLine = "";
            for (int col = 0 ; col < numberCols - 1 ; col++) {
                OCData thisElement = ((OCData)((Vector)thisData.elementAt(row)).elementAt(col));
                String value = thisElement.getValue();
                String formula = thisElement.getFormula();
                dataLine = dataLine + value + "," + formula + ",";
            }
            OCData lastElement = ((OCData)((Vector)thisData.elementAt(row)).elementAt(numberCols - 1));
            String value = lastElement.getValue();
            String formula = lastElement.getFormula();
            dataLine = dataLine + value + "," + formula;
            
            newFile.write(dataLine + "\r\n");
        }
        newFile.close();
    }

    public Vector<String> getCriteria(){
        Vector<String> critList = new Vector<String>();
          for (int i=0; i<thisData.size()-4; i++){                                // <size-4 means omits the last 3 lines of rows, they are total, recommended, decision.
          Vector v=  (Vector) thisData.elementAt(i);
          OCData ocData= (OCData) v.elementAt(0);
          String str = ocData.getValue();
          critList.add(str);
        }
        return critList;
    }

}
