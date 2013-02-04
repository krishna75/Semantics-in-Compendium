/*
 * OcGcConnector.java
 *
 * Created on 15-Jul-2009, 21:28:29
 */

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compendium.ui.owl;

import com.compendium.ProjectCompendium;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.Vector;

/**
 * This file connects OCMatrix to GCMatrix2.
 * Both matrices are located in the package "matrix".
 * It reads list of criteria from from OCMatrix (reads from a text file) and stores in a vector.
 * The vector is used in GCMatrix2.
 * @author Krishna Sapkota
 */
public class OcGcConnector {

    /*  As the name suggests, it holds a list of criteria. */
   private static Vector<String> critList;

/**
 * Constructor. It does not do much.
 */
    public OcGcConnector(){

//        try {
//            new CriteriaCollector();
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(OcGcConnector.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }
    
 /**
  * gets the file containing OCMatrix data.
  * @param nodeId is id of the node.
  * @return the file containing OCMatrix.
  */
    public File getOCMatrixFile(String nodeId){
//      Use the ProjectCompendium class to get the homepath of the installation
//      as this may vary depending on the platform and the way the user installed it
        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Project Files/";
        String projectName = ProjectCompendium.APP.getProjectName();
        String filePath = pathName + projectName + "/Matrix Files/" + nodeId + ".csv";
        return new File(filePath);
    }

/**
 * It reads the list of criteria from a text file exactly the same way as OCMatrix does.
 * @param nodeId is required to locate the related file name.
 * @throws java.io.FileNotFoundException
 */
   public Vector<String> getCriteriaFromOCMatrix(String nodeId) throws FileNotFoundException {
        Scanner scan = new Scanner(getOCMatrixFile(nodeId));

//      Get first line from file which contains the number of rows and columns
        String firstLine = scan.nextLine();
        Scanner readLine = new Scanner(firstLine).useDelimiter(",");
        int rows = readLine.nextInt();
        int cols = readLine.nextInt();

//      Get the line containing the column heading information from the file
//      This line is ignored.
        scan.nextLine();
        critList = new Vector<String>();
        for (int r = 0 ; r < rows-4 ; r++) {

//          reads rows of data containing, criterion name as the first value.
            String line = scan.nextLine();
            readLine = new Scanner(line).useDelimiter(",");
            String criterion = readLine.next();

            //adds a criterion to the list. This line does the main job of this method.
            critList.add(criterion);

            for (int c = 1 ; c < cols ; c++) {                                  // reads the rest of the line (unnecessory places) and goes to the next line.
                if (readLine.hasNext()) {                                       // reads value
                    readLine.next();
                }
                if (readLine.hasNext()) {                                       // reads formula
                    readLine.next();
                }
            }
        }
        return critList;
}
   
/**
 * It returns list of criteria.
 * @return list of criteria.
 */
   public Vector<String> getCriteria(){
       return critList;
   }

   /**
    * It adds the criterion to the criteria list.
    * @param criterion is the needed criterion to be added to the list.
    */
   public void addCriterion(String criterion){
       critList.add(criterion);
   }

   /**
    * removes a criterion from the list.
    * @param index of in th criteria list, to be removed.
    */
   public void removeCriterion(int index){
       critList.remove(index);
   }
}
