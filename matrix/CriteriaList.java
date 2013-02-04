/*
 * CriteriaList.java
 *
 * Created on 05 July 2007, 10:53
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This class creates a list of criteria that can be selected for the
 * Options vs Criteria matrix
 *
 * @author Simon Skrzypczak
 */


//import not_used.CriteriaListOWL_not_used;
import com.compendium.ProjectCompendium;
import javax.swing.*;
import java.io.*;
import java.util.*;

//import com.compendium.ui.owl.*;

public class CriteriaList {
    
    private Scanner criteriaFile;    //** Scanner can do a lot of stuff, check to see what is it doing on this case
    private Scanner criteriaItem;
    
    private String line;
    private String criteria;
    private  Vector critList;

//    private CriteriaListOWL_not_used critListOWL; //** Krishna added
    
    // constructor of CriteriaList
    @SuppressWarnings("unchecked")
    public CriteriaList() {                                                               //** So as you can see you can produce a method with the same name of the class
        
                    critList = new Vector();
            //      Use the ProjectCompendium class to get the homepath of the installation
            //      as this may vary depending on the platform and the way the user installed it
                    // JulPath: 1-1 Go to the Project Files and get the Criteria.csv file to upload
                    String homePath = ProjectCompendium.sHOMEPATH;                                    //** Find the directory where compendium were instaled
                    String pathName = homePath + "/System/resources/Project Files/";                  //** go to the Project Files folder to save the file
                    String projectName = ProjectCompendium.APP.getProjectName();                      //** Get the project name to finally save the file
                    String fileName = pathName + projectName + "/Criteria.csv";                       //** Add the name and format of the file to be saved
//                    String fileName =  "d:/temp/Criteria.csv";
                    int count = 0;
                    try {
                        File cFile = new File(fileName);                         //??
                        criteriaFile = new Scanner(cFile);                       //??
            //          Read the first line which contains column headings
                            // JulPath: 1-2 get the column heads, with the Scanner class, to read the file uploaded the file is from EXCEL
                        if (criteriaFile.hasNextLine()) {                        //??
                            line = criteriaFile.nextLine();
                        }
            //          Read information for any remaining lines in the file to get the
            //          criteria information
                           // JulPath: 1-3 this will create a criList = (Social, Economic, Investiments)
                        while (criteriaFile.hasNextLine()) {                                               //**
                            line = criteriaFile.nextLine();
                            criteriaItem = new Scanner(line);
                            criteriaItem.useDelimiter(",");
                            criteria = criteriaItem.next();
                            critList.add(criteria);
                            count = count + 1;
                        }
                        if (count == 0) {
            //              If no criteria in the file then display this message                            //** In case there is no criteira
                            JOptionPane.showMessageDialog(null, "There are no criteria available." +
                                    "\nPlease populate the Goals vs Criteria matrix", "No Criteria",
                                    JOptionPane.WARNING_MESSAGE);
                        }
                    }
                       // JulPath: 1-4 if file not found this is where the exception is done
                    catch (FileNotFoundException fileNotFound) {
            //          If no file then display this message
                        JOptionPane.showMessageDialog(null, "There is no criteria file to get" +
                                "\ndetails of the criteria from.\nPlease create a file" +
                                " using the\nGoals vs Criteria button below.", "Criteria Warning",
                                JOptionPane.WARNING_MESSAGE);                                                //** In case there is no criteria
                    }
    }
    
//  Return a Vector of the criteria currently available
    public Vector getCriteria() {                             //** Communicate to UINodeMatrixPanel
        return critList;                                      //** Communicate to UINodeMatrixPanel
    }
    
}
