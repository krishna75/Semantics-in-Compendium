/*
 * CriteriaCollector.java
 *
 * Created on 01-Aug-2009, 14:52:26
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compendium.ui.owl;

import com.compendium.ProjectCompendium;
import com.compendium.ui.matrix.MatrixContainer;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.Scanner;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;

/**
 * This class collect list of criteria from three different sources and provides it to the GCMatrix2.
 * The three different sources are current issue, current project or OWL ontology file.
 * @author Krishna Sapkota
 */
public class CriteriaCollector {
   private static Vector<String> projectCritList;
   private static Vector<String> issueCritList;
   private static Vector<String> owlCritList;

    public CriteriaCollector(){
        collectIssueCriteria();
        collectProjectCriteria();
        collectOWLCriteria();      
    }

/**
 * It collects list of Criteria used in current project.
 */
    public void collectProjectCriteria(){
        projectCritList = new Vector<String>();

//      Use the ProjectCompendium class to get the homepath of the installation
//      as this may vary depending on the platform and the way the user installed it
        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Project Files/";
        String projectName = ProjectCompendium.APP.getProjectName();
        String matrixFilePath = pathName + projectName + "/Matrix Files/" ;

        //selects file name ending with .csv only
        File dir = new File(matrixFilePath);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".csv");
            }
        };
        String[] children = dir.list(filter);

        // reads every file in the directory
        if (children == null) {
        } else {
            for (int i=0; i<children.length; i++) {
                File file = new File(matrixFilePath + "/" + children[i]);
                readCSVFile(file);
            }
        }
    }


    public void readCSVFile(File csvFile){
        try {
            Scanner scan = new Scanner(csvFile);
//      Get first line from file which contains the number of rows and columns
            String firstLine = scan.nextLine();
            Scanner readLine = new Scanner(firstLine).useDelimiter(",");
            int rows = readLine.nextInt();
            int cols = readLine.nextInt();
//      Get the line containing the column heading information from the file
//      This line is ignored.
            scan.nextLine();
            for (int r = 0; r < rows - 4; r++) {
//          reads rows of data containing, criterion name as the first value.
                String line = scan.nextLine();
                readLine = new Scanner(line).useDelimiter(",");
                String criterion = readLine.next();
                //adds a criterion to the list. This line does the main job of this method.
                projectCritList.add(criterion);
                for (int c = 1; c < cols; c++) {
                    // reads the rest of the line (unnecessory places) and goes to the next line.
                    if (readLine.hasNext()) {
                        // reads value
                        readLine.next();
                    }
                    if (readLine.hasNext()) {
                        // reads formula
                        readLine.next();
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CriteriaCollector.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

  /**
   * It collects list of Criteria from OWL ontology file.
   */
  public void collectOWLCriteria(){
      owlCritList= new Vector<String>();
      ConnectionJenaOWL owl = new ConnectionJenaOWL();
      for (int i=0;i<owl.getCriteria().size();i++){
          OWLData owlData = owl.getCriteria().elementAt(i);
          Vector<String> v = owlData.getDataList();
          for (int j= 0; j<v.size();j++){
              owlCritList.add(v.elementAt(j));
          }//j
      }//i
  }

  /**
   * It collects list of Criteria used in current issue.
   */
  public void collectIssueCriteria(){
      MatrixContainer mc = new MatrixContainer();
      issueCritList = mc.getCriteria();
  }
    
 /**
    * It returns the criteria collected from the working project.
    * @return list of criteria.
    */
   public Vector<String> getProjectCriteria(){
       return projectCritList;
   }


  /**
   * It returns the criteria collected from the working issue.
   * @return
   */
  public Vector<String> getIssueCriteria(){
      return issueCritList;
  }

  /**
   * It returns the criteria collected from OWL ontology file.
   */
  public Vector<String> getOWLCriteria(){
      return owlCritList;
  }
   /**
    * It adds the criterion to the criteria list.
    * @param criterion is the needed criterion to be added to the list.
    */
   public void addProjectCriterion(String criterion){
       projectCritList.add(criterion);
   }

   /**
    * removes a criterion from the list.
    * @param index of in th criteria list, to be removed.
    */
   public void removeProjectCriterion(int index){
       projectCritList.remove(index);
   }

  /**
   * Prints memebers of a vector,used for testing purpose only;
   * @param List
   */
  public void printCriteria(Vector<String> List){
       String result="";
       for (int i=0;i<List.size();i++){
           result = result+List.elementAt(i)+"\n";
       }
                   JOptionPane.showMessageDialog(null, result );
   }
}
