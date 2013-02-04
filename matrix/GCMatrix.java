/*
 * GCMatrix.java
 *
 * Created on 11 July 2007, 15:03
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This class is the table for the Global Parameters table and the
 * Goals vs Criteria matrix
 *
 * @author Simon Skrzypczak
 */

import com.compendium.ProjectCompendium;
import java.util.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;

public class GCMatrix extends DefaultTableModel {
    
    /** Creates a new instance of GCMatrix */
    public GCMatrix(String fName) {
        
        try {
//          Try getting the table contents from the named file
            getGCMatrixFromFile(fName);
        }
        catch (FileNotFoundException fileNotFound) {
//          If file not available then begin creating a new table based on
//          the filename fName passed to the constructor
            if (fName.equals("Criteria")) {
                this.addColumn("Criteria");
            } else if (fName.equals("GlobalParameters")) {
                this.addColumn("Parameter");
                this.addColumn("Value");
                this.addColumn("Unit");
            }
        }
    }


    
//  Add a new row to the matrix
    public void addDataRow(String response) {
        Object[] newRow = new Object[this.getColumnCount()];
//      Add the 'response' to the first column of the new row
        newRow[0] = response;
        for (int i = 1 ; i < this.getColumnCount() ; i++) {
            newRow[i] = "";
        }
        this.addRow(newRow);
    }

//***************************************** Krishna added: ************************************************************************
    /**
     * Krishna added: adds a row with three necessory parameters.
     * @param paramName name of the global parameter.
     * @param value value of the global parameter.
     * @param unit unit of the global parameter.
     */
    public void addDataRow(String paramName,String value, String unit){
         Object[] newRow = new Object[this.getColumnCount()];
//      Add the 'response' to the first column of the new row
        newRow[0] = paramName;
        newRow[1] = value;
        newRow[2] = unit;
        for (int i = 3 ; i < this.getColumnCount() ; i++) {
            newRow[i] = "";
        }
        this.addRow(newRow);
    }
//***************************************** end of Krishna added: ************************************************************************
    
//  Remove a column from the matrix
    public void remDataColumn(int column) {
        Vector newHeadings = new Vector(this.getColumnCount() - 1);
        for (int col = 0 ; col < column ; col++) {
            newHeadings.add(this.getColumnName(col));
        }
        for (int col = column + 1 ; col < this.getColumnCount() ; col++) {
            newHeadings.add(this.getColumnName(col));
        }
        Vector newData = this.getDataVector();
        int numRows = newData.size();
        for (int row = 0 ; row < numRows ; row++) {
            Vector thisRow = (Vector)newData.elementAt(row);
            thisRow.removeElementAt(column);
        }
        this.setDataVector(newData, newHeadings);
    }
    
//  Set any non-editable cells - overrides the method in the parent class
    public boolean isCellEditable(int row, int col) {
        boolean result = true;
        if (col == 0) {
            result = false;
        }
        return result;
    }
    
//  Get the contents of the matrix from the file for display
    public void getGCMatrixFromFile(String fileName) throws FileNotFoundException {
        
//      Use the ProjectCompendium class to get the homepath of the installation
//      as this may vary depending on the platform and the way the user installed it
        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Project Files/";
        String projectName = ProjectCompendium.APP.getProjectName();
        File mFile = new File(pathName + projectName + "/" + fileName + ".csv");

         //File mFile = new File ("c:/program files/compendium/system/resources/project files/globalparameters.csv");
        
        Scanner lineFromFile = new Scanner(mFile);
        
        String line = lineFromFile.nextLine();
        Scanner readLine = new Scanner(line).useDelimiter(",");
        
//      Get the line containing the column heading information from the file  
        while (readLine.hasNext()) {
            this.addColumn(readLine.next());
        }
        
//      Get the remaining rows from the file which contains the data and totals information  
        while (lineFromFile.hasNext()) {
            line = lineFromFile.nextLine();
            readLine = new Scanner(line).useDelimiter(",");
            Object[] data = new Object[this.getColumnCount()];
            int count = 0;
            while (readLine.hasNext()) {
                data[count] = readLine.next();
                count = count + 1;
            }
            this.addRow(data);
        }
    }
    
//  Save the matrix to a file
    public void saveGCMatrixToFile(String fileName) throws IOException {

//      Use the ProjectCompendium class to get the homepath of the installation
//      as this may vary depending on the platform and the way the user installed it
        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Project Files/";
        String projectName = ProjectCompendium.APP.getProjectName();
        String dirPath = pathName + projectName;
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
        
//      Write the column headings to the file
        String headings = "";
        for (int col = 0 ; col < this.getColumnCount()-1 ; col++) {
            headings = headings + this.getColumnName(col) + ",";
        }
        headings = headings + (this.getColumnName(this.getColumnCount()-1));
        
        newFile.write(headings + "\r\n");
        
//      Write the data, node Id, totals and decision rows to the file
        Vector data = this.getDataVector();
        for (int row = 0 ; row < this.getRowCount() ; row++) {
            String dataLine = "";
            for (int col = 0 ; col < this.getColumnCount()-1 ; col++) {
                Object thisElement = ((Vector)data.elementAt(row)).elementAt(col);
                dataLine = dataLine + thisElement + ",";
            }
            Object lastElement = ((Vector)data.elementAt(row)).elementAt(this.getColumnCount()-1);
            dataLine = dataLine + lastElement;
            
            newFile.write(dataLine + "\r\n");
        }
        newFile.close();
    }
}