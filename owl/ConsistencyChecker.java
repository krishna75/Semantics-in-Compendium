/*
 * ConsistencyChecker.java
 *
 * Created on 16-Jul-2009, 12:39:23
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compendium.ui.owl;

import com.compendium.ui.matrix.GCMatrix2;
import java.util.Vector;
import javax.swing.JOptionPane;

/**
 * This class is designed to check consistancy between a goal and a criterion.
 * It is usedful in GCMatrix2, which needs this class to determine whether to make check box editable.
 * @author Krishna Sapkota
 */
public class ConsistencyChecker {

    //private static Vector<MatrixCell> editableCellList;
    private GCMatrix2 matrix;
    private static ConnectionJenaOWL owl;

    /**
     * constructor
     * @param matrix a GCMatrix2 to be checked for consistancy.
     */
    public ConsistencyChecker(GCMatrix2 matrix){
        this.matrix = matrix;
       // editableCellList = new Vector<MatrixCell>();
        owl= new ConnectionJenaOWL();
    }
    public ConsistencyChecker(){
       // editableCellList= new Vector<MatrixCell>();
    }

     /**
     *
     *  Finds out whether the cell has related criterion and goal.
     * @param row
     * @param col
     * @return
     */
    public boolean areRelated(int row, int col){
             boolean result=false;
             if  (isCriterionOfGoal(row,col)){
                 result = true;
             } else {
                    String goal =  matrix.getColumnName(col);
                    Vector v = (Vector ) (matrix.getDataVector().get(row));
                    String criterion =  v.elementAt(0).toString();
                    int response =  JOptionPane.showConfirmDialog(null, "Criterion \""+ criterion +"\""+
                                            "\n is not related to  "+
                                            "\ngoal \""+ goal+"\""+
                                            "\nIt is not editable. Do you want to make  this box editable anyway? ",
                                            "Relation check",
                                            JOptionPane.YES_NO_OPTION);
                   if (response == JOptionPane.YES_OPTION){
                       JOptionPane.showMessageDialog(null, "THIS CELL IS NOW EDITABLE !" +
                               "\nPlease, click on the box again to make  it checked" +
                               "\nONLY IF the criterion \""+criterion+"\" meets the goal \""+ goal+"\".");
                       result = true;
                   }else {
                       result = false;
                   }
             }
      return result;
    }

    public boolean isCriterionOfGoal(int row, int col){
        boolean result = false;
        String goal =  matrix.getColumnName(col);
        Vector v = (Vector ) (matrix.getDataVector().get(row));
        String criterion =  v.elementAt(0).toString();
        if  (owl.isCriteriaOfGoal(criterion, goal)){
             result = true;
        } else {
            result = false;
        }
        return result;
    }

///**
// * If user checked the check box to make it editable already, it returns true.
// * @param row of the cell.
// * @param col of the cell.
// * @return if the cell is editable.
// */
//      public boolean isCheckedAnyway(int row, int col){
//          boolean result = false;
//          for (int i=0;i<editableCellList.size();i++){
//              MatrixCell cellInTheList = editableCellList.elementAt(i);
//              if (cellInTheList.getRow()== row && cellInTheList.getColumn()== col){
//                  result = true;
//              }
//          }
//          return result;
//      }

  /**
   * If user tries to make an uneditable cell to editable cell, it adds the relation to the OWL ontology file.
   * @param row
   * @param col
   */
  public void addEditableCell(int row, int col){
        String goal =  matrix.getColumnName(col);
        Vector v = (Vector ) (matrix.getDataVector().get(row));
        String criterion =  v.elementAt(0).toString();
        owl.addHasCriteria(goal, criterion);
  }

  /**
   * If user wants to remove the editable cell, it removes the relation from the OWL ontoloy file.
   * @param row
   * @param col
   */
  public void removeEditableCell(int row, int col){
        String goal =  matrix.getColumnName(col);
        Vector v = (Vector ) (matrix.getDataVector().get(row));
        String criterion =  v.elementAt(0).toString();
        owl.removeHasCriteria(goal, criterion);
  }
}
