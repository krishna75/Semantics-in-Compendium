package com.compendium.ui.matrix;


import com.compendium.ProjectCompendium;
import com.compendium.ui.owl.ConsistencyChecker;
import com.compendium.ui.owl.CriteriaCollector;

import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public  class GCMatrix2 extends DefaultTableModel  {

    ConsistencyChecker checker  = new ConsistencyChecker(this);              // Krishna added:

    public  GCMatrix2(String fName) {
        
        try {
//          Try getting the table contents from the named file
            getGCMatrixFromFile(fName);
        } catch (FileNotFoundException fileNotFound) {
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
      addCriteriaFromIssue();
    }


    public void checkAllBoxes(){
        int totRows = getRowCount();
        int totCols = getColumnCount();
        for (int i=0;i<totRows;i++){
            for (int j=1;j<totCols;j++){
                 if  (checker.isCriterionOfGoal(i,j)){
                    setValueAt(true, i, j);
                }else {
                    setValueAt(false, i, j);
                }
            }
        }
    }

    public void addEditableCell(int row, int col){
        checker.addEditableCell(row, col);
    }
    public void removeEditableCell(int row, int col){
        checker.removeEditableCell(row, col);
    }

   
    //  Set any non-editable cells - overrides the method in the parent class
    @Override
    public boolean isCellEditable(int row, int col) {
        boolean result = true;
        if (col == 0) {
            result = false;
        }      
        if (!checker.areRelated(row,col)){
            result = false;
        }else {
            result = true;
        }
        return result;
    }


    @Override
    public Class<?> getColumnClass(int index) {
        return getValueAt(0, index).getClass();
    }
//  Add a new row to the matrix
    public  void addDataRow(String response) {
        Object[] newRow = new Object[this.getColumnCount()];
//      Add the 'response' to the first column of the new row
        newRow[0] = response;
        for (int i = 1; i < this.getColumnCount(); i++) {
            newRow[i] = new Boolean(false);

        }
        this.addRow(newRow);

    }
    /**
     * Krishna added:
     * Adds all the criteria in a vector to the table.
     * @param criteria is added to the matrix (table).
     */
        public void addDataRows(Vector<String> responseList){
        Boolean isOkToAdd ;

        Collections.sort(responseList);
        // repeats for each response
        for (int i=0; i<responseList.size();i++){
            isOkToAdd = true;
            int totalRows = getRowCount();

            // checks with every row in GCMatrix2
            for (int j=0;j<totalRows;j++){
               String aRow = (String) getValueAt(j, 0);

              //  checks if GCMatrix2 already has the criterion.
               if (aRow.equals(responseList.elementAt(i))){
                   isOkToAdd = false;
               }
            }
            if (isOkToAdd){
            addDataRow(responseList.elementAt(i));
            }
        }    
    }


    public void addCriteriaFromOWL(){
        CriteriaCollector collector = new CriteriaCollector();
        Vector<String> critList = collector.getOWLCriteria();
        addDataRows(critList);
    }

    public void addCriteriaFromProject(){
        CriteriaCollector collector = new CriteriaCollector();
        Vector<String> critList = collector.getProjectCriteria();
        addDataRows(critList);
    }

    public void addCriteriaFromIssue(){
        CriteriaCollector collector = new CriteriaCollector();
        Vector<String> critList = collector.getIssueCriteria();
        addDataRows(critList);
    }

    /*this method has been overridden to eliminate a problem when new criteria are added to a table with existing
     *options.*/
    @Override
    public void addColumn(Object o) {

        Vector<Vector> oldRows = getDataVector();
        Vector<Vector> newRows = new Vector(oldRows.size());
        int numRows = oldRows.size();

        for (Vector oldR : oldRows) {
            Vector newR = new Vector();
            newR.addAll(oldR);
            newR.add(new Boolean(false));
            newRows.add(newR);
        }
        int k;
        for (k = 0; k < numRows; k++) {
            removeRow(0);
        }
        super.addColumn(o);
        for (Vector v : newRows) {
            addRow(v);
        }
       
    }
    //  Get the contents of the matrix from the file for display
    public void getGCMatrixFromFile(String fileName) throws FileNotFoundException {

//      Use the ProjectCompendium class to get the homepath of the installation
//      as this may vary depending on the platform and the way the user installed it
        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Project Files/";
        String projectName = ProjectCompendium.APP.getProjectName();
        File mFile = new File(pathName + projectName + "/" + fileName + ".mat");

        FileInputStream fInput = new FileInputStream(mFile);
        try {
            ObjectInputStream oInput = new ObjectInputStream(fInput);
            Vector columns = (Vector) oInput.readObject();
            Vector rows = (Vector) oInput.readObject();
            setDataVector(rows, columns);
            oInput.close();
        } catch (IOException ioE) {
            JOptionPane.showMessageDialog(null, ioE.toString());
        } catch (ClassNotFoundException cnfE) {
            JOptionPane.showMessageDialog(null, cnfE.toString());
        }
    }

    /**
     * Krishna added:
     * removes all the rows of the table and makes it empty.
     */
    public void removeAllRows(){
        int size = getRowCount();
        if (size>0){
            for (int i = 0 ; i < size ; i++) {
                    removeRow(getRowCount()-1);                                 // getRowCount()-1 will remove row from buttom to top; if 0, top to buttom
                }
        }
    }

//  Remove a column from the matrix
    public void remDataColumn(int column) {
        Vector newHeadings = new Vector(this.getColumnCount() - 1);
        for (int col = 0; col < column; col++) {
            newHeadings.add(this.getColumnName(col));
        }
        for (int col = column + 1; col < this.getColumnCount(); col++) {
            newHeadings.add(this.getColumnName(col));
        }
        Vector newData = this.getDataVector();
        int numRows = newData.size();
        for (int row = 0; row < numRows; row++) {
            Vector thisRow = (Vector) newData.elementAt(row);
            thisRow.removeElementAt(column);
        }
        this.setDataVector(newData, newHeadings);
    }


    //  Save the matrix to a file
    public void saveGCMatrixToFile(String fileName) throws IOException {

//      Use the ProjectCompendium class to get the homepath of the installation
//      as this may vary depending on the platform and the way the user installed it
        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Project Files/";
        String projectName = ProjectCompendium.APP.getProjectName();
        String dirPath = pathName + projectName;
        String filePath = dirPath + "/" + fileName + ".mat";

        File directory = new File(dirPath);

        if (!directory.exists()) {
            if (!directory.mkdirs()) {
                JOptionPane.showMessageDialog(null, "The necessary folders were not able to be" +
                        "created.\nPlease check the folder locations.", "Folder Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        FileOutputStream newFile = new FileOutputStream(filePath);
        ObjectOutputStream outStream = new ObjectOutputStream(newFile);
        Vector<String> columns = new Vector();
        int colCount;
        for (colCount = 0; colCount < getColumnCount(); colCount++) {
            columns.add(getColumnName(colCount));
        }
        outStream.writeObject(columns);
        outStream.writeObject(getDataVector());

        outStream.close();
        newFile.close();
    }
        public void saveGCMatrixToFileToCriteriaList(String fileName) throws IOException {

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
        Vector data = this.getDataVector();
        for (int row = 0 ; row < this.getRowCount() ; row++) {
            String dataLine = "";
                Object thisElement = ((Vector)data.elementAt(row)).elementAt(0);

            dataLine = dataLine + thisElement;

            newFile.write(dataLine + "\r\n");
        }
        newFile.close();
    }
}