/*
 * MatrixCell.java
 *
 * Created on 16-Jul-2009, 11:11:57
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compendium.ui.owl;

/**
 * It holds information (row and column) about a cell in a matrix.
 * It is used in ConsistencyChecker to hold list of MatrixCell which are editable
 * @author Krishna Sapkota
 */
public class MatrixCell {
    private int row;
    private int col;
    
    /**
     * constructor to create a cell location.
     * @param row of the cell.
     * @param col of the cell.
     */
    public MatrixCell(int row, int col){
        this.row = row;
        this.col = col;
    }

    /**
     * It returns the row of this MatrixCell
     * @return a row.
     */
    public int getRow(){
        return row;
    }

    /**
     * It returns the column of this MatrixCell.
     * @return a column
     */
    public int getColumn(){
        return col;
    }

    public void setRow(int row){
        this.row = row;
    }

    public void setColumn(int col){
        this.col = col;
    }
}
