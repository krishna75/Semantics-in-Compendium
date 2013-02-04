/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compendium.ui.owl;



/**
 * It stores parameter related data (name,value and unit).
 * It is used in ConnectionJenaOWL with paramList.
 * @author Krishna_Desktop
 */
public class ParameterData {
    private String name;
    private String value;
    private String unit;

    /**
     * This class organises the information needed for Parameters, which can be used in a vector.
     * @param name of the Paramter
     * @param value of the Parameter
     * @param unit of the Parameter
     */
    public ParameterData(String name, String value, String unit){
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    /**
     *
     * @return the name of the Parameter
     */
    public String getName(){
        return name;
    }

    /**
     *
     * @return the value of the Parameter
     */
    public String getValue(){
        return value;
    }

    /**
     *
     * @return the unit of the Parameter.
     */
    public String getUnit(){
        return unit;
    }

    /**
     *
     * @param name
     */
    public void setName(String name){
        this.name = name;
    }

    /**
     *
     * @param value
     */
    public void setValue(String value){
        this.value = value;
    }

    /**
     * 
     * @param unit
     */
     public void setUnit(String unit){
        this.unit = unit;
    }
}
