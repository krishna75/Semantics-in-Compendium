/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compendium.ui.owl;

import java.util.Vector;

/**
 * It stores data ,a string and a vector.
 * It is used in ConnectionJenaOWL with dataListAll.
 * @author Krishna_Desktop
 */
public class OWLData {
    private String dataType;
    private Vector<String> dataList;

    /**
     * Constructor to organise Criteria, Goals and Paramters in a structure, which can be held in a vector.
     * @param dataType
     * @param dataList
     */
    public OWLData(String dataType, Vector<String> dataList){
        this.dataType = dataType;
        this.dataList = dataList;
    }

    /**
     * Returns the datatype or category of Criteira, Goals or Parameters.
     * @return
     */
    public String getDataType(){
        return dataType;
    }

    /**
     * Returns a list of Criteria, Goals or ParameterData
     * @return
     */
    public Vector<String> getDataList(){
        return dataList;
    }

    /**
     * Sets the category type.
     * @param dataType
     */
    public void setDataType(String dataType){
        this.dataType = dataType;
    }

    /**
     * Sets the list of Criteria or Goals or PrameterData.
     * @param dataList
     */
    public void setDataList(Vector<String> dataList){
        this.dataList = dataList;
    }

}
