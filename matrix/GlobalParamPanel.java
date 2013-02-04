/*
 * GlobalParamPanel.java
 *
 * Created on 30 July 2007, 16:09
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This panel displays the Global Parameters table
 *
 * @author Simon Skrzypczak
 */
import com.compendium.ProjectCompendium;
import com.compendium.core.ICoreConstants;
import com.compendium.core.datamodel.*;
import com.compendium.ui.dialogs.UIMessageDialog;
import com.compendium.ui.owl.UIParametersTree;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.io.*;

public class GlobalParamPanel extends JPanel implements ActionListener {
    
    private JFrame oParent;
    
    private JTable globalParamTable;
    private JScrollPane globalParamScrollPane;
    private GCMatrix gpTable;
    
    private JPanel westPanel;
    private JPanel westPanelContent;
    
    private JLabel paramLabel;
    private JLabel tableLabel;
    private JLabel propagateLabel;
    private JLabel sensitivityAnalysisLabel;
    
    private JTextArea blankSpace1;
    private JTextArea blankSpace2;
    
    private JButton addParamButton;
    private JButton removeParamButton;
    private JButton saveButton;
    private JButton propagateButton;
    private JButton sensitivityAnalysisButton;
    
    private final String ADDPAR = "addpar";
    private final String REMPAR = "rempar";
    private final String SAVIT = "savit";
    private final String PROP = "prop";
    private final String SENS = "sens";
    private final String PROPAGATE = "propagate";
    private final String SENSITIVITY = "sensitivity";
    
    /** Creates a new instance of GlobalParamPanel */
    public GlobalParamPanel(JFrame parent) {
        
        oParent = parent;
        
        this.setLayout(new BorderLayout());
        
//      West Panel Content
        westPanel = new JPanel();
        westPanel.setLayout(new BorderLayout());
        westPanelContent = new JPanel();
        westPanelContent.setLayout(new GridLayout(0,1,5,5));
        
        blankSpace1 = new JTextArea();
        blankSpace1.setEditable(false);
        blankSpace1.setBackground(westPanelContent.getBackground());
        westPanelContent.add(blankSpace1);
        blankSpace2 = new JTextArea();
        blankSpace2.setEditable(false);
        blankSpace2.setBackground(westPanelContent.getBackground());
        westPanelContent.add(blankSpace2);
        
        paramLabel = new JLabel("Parameter");
        westPanelContent.add(paramLabel);
        
        addParamButton = new JButton("Add");
        addParamButton.addActionListener(this);
        addParamButton.setActionCommand(ADDPAR);
        westPanelContent.add(addParamButton);
        
        removeParamButton = new JButton("Remove");
        removeParamButton.addActionListener(this);
        removeParamButton.setActionCommand(REMPAR);
        westPanelContent.add(removeParamButton);
        
        tableLabel = new JLabel("Table");
        westPanelContent.add(tableLabel);
        
        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        saveButton.setActionCommand(SAVIT);
        westPanelContent.add(saveButton);
        
        propagateLabel = new JLabel("Propagate changes");
        westPanelContent.add(propagateLabel);
        
        propagateButton = new JButton("Propagate");
        propagateButton.addActionListener(this);
        propagateButton.setActionCommand(PROP);
        westPanelContent.add(propagateButton);
        
        sensitivityAnalysisLabel = new JLabel("Sensitivity Analysis");
        westPanelContent.add(sensitivityAnalysisLabel);
        
        sensitivityAnalysisButton = new JButton("Sensitivity Analysis");
        sensitivityAnalysisButton.addActionListener(this);
        sensitivityAnalysisButton.setActionCommand(SENS);
        westPanelContent.add(sensitivityAnalysisButton);
        
        westPanel.add("North", westPanelContent);
        this.add("West", westPanel);
        

//      Center Panel Content        
        gpTable = new GCMatrix("GlobalParameters");
        globalParamTable = new JTable(gpTable);
        globalParamScrollPane = new JScrollPane(globalParamTable);
        this.add("Center", globalParamScrollPane);
    }
    
    public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();


//***************************************** Krishna added: ************************************************************************
//      Add a parameter to the table
        if (comm.equals(ADDPAR)) {            
          UIParametersTree pTree= new UIParametersTree(ProjectCompendium.APP, gpTable);
          gpTable= pTree.getMatrix();
//***************************************** end of Krishna added: ************************************************************************
        }
//      Remove a parameter from the table
        if (comm.equals(REMPAR)) {
//          If there are parameters in the table display a list of them for the
//          user to select from
            if (gpTable.getRowCount() > 0) {
                int size = gpTable.getRowCount();
                Object[] paramInTable = new Object[size];
                for (int i = 0 ; i < size ; i++) {
                    paramInTable[i] = gpTable.getValueAt(i, 0);
                }
                Object response = JOptionPane.showInputDialog(null,
                        "Please select the parameter to remove:", "Remove Parameter",
                        JOptionPane.QUESTION_MESSAGE, null, paramInTable, "Select Parameter");
                if (!response.equals(null)) {
                    int count = 0;
                    boolean found = false;
                    while (!found && count < paramInTable.length) {
                        if (response.equals(paramInTable[count])) {
                            found = true;
                        } else {
                            count = count + 1;
                        }
                    }
                    gpTable.removeRow(count);
                }
//          Else if no parameters display a message to show this
            } else {
                JOptionPane.showMessageDialog(null, "There are no parameters to remove");
            }
        }
//      Save the table to the file
        if (comm.equals(SAVIT)) {
            try {
                gpTable.saveGCMatrixToFile("GlobalParameters");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Changes have been saved.");
        }
//      Start a global update of all matrix files (propagate)
        if (comm.equals(PROP)) {
            int cont = closeFileMessage(PROPAGATE);
            if (cont == 0) {
                propagateChanges();
            }
        }
//      Start a sensitivity analysis
        if (comm.equals(SENS)) {
            int cont = closeFileMessage(SENSITIVITY);
            if (cont == 0) {
                if (gpTable.getRowCount() > 0) {
                    Vector parameters = gpTable.getDataVector();
                    Object[] paramList = new Object[gpTable.getRowCount()];
                    for (int i = 0 ; i < gpTable.getRowCount() ; i++) {
                        paramList[i] = ((Vector)parameters.elementAt(i)).elementAt(0);
                    }
                    SensitivityDialog sensAnalysis = new SensitivityDialog(ProjectCompendium.APP, paramList);
//                  Retrieve the information from the SensitivityDialog
                    int paramPosition = sensAnalysis.getParameterPosition();
                    String parameter = sensAnalysis.getParameter();
                    double range = sensAnalysis.getRange();
                    int subDivision = sensAnalysis.getSubDiv();
                    double rangeDouble = range / 100;
                    if (!parameter.equals(null) && range != 0) {
                        sensitivityAnalysis(paramPosition, parameter, range, subDivision);
                    } else if (range == 0) {
                        JOptionPane.showMessageDialog(null, "A value of zero will not result in any changes",
                                "Zero value", JOptionPane.WARNING_MESSAGE);
                    }
                } else {
                    JOptionPane.showMessageDialog(null, "There are no parameters to use for the analysis");
                }
            }
        }
    }
    
//  Method to display a message prompting the user to close any matrix windows that
//  are currently open before proceeding and warning of what will happen if the
//  user continues with the process
    public int closeFileMessage(String updateType) {
        int cont = 0;
//      Warning message for the sensitivity analysis
        if (updateType.equals(SENSITIVITY)) {
            String message = "All Issue node windows should be closed before proceeding " +
                        "with this function\nto ensure that it is carried out correctly." +
                        "\n\nFurthermore please note that this function will simulate the effect" +
                    "\nof changing the selected parameter on all stored matrix files that contain" +
                    "\nthe parameter. In the process of doing this the latest values from any" +
                    "\nexternal file references will also be obtained. Please ensure that you" +
                    "\naccount for this when viewing the results." +
                    "\n\nIt may be advisable to perform a global propagate before running the" +
                    "\nsensitivity analysis to ensure that the latest external file references" +
                    "\nare used in the current system recommended options." +
                    "\n\nDo you want to continue?";
            cont = JOptionPane.showConfirmDialog(null, message, "Sensitivity Analysis Warning", 
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
//      Warning message for the propagate function
        } else if (updateType.equals(PROPAGATE)) {
            String message = "Running the propagate function will perform the following operations:" +
                    "\n\n    1. All stored matrix files will be opened one by one" +
                    "\n\n    2. The matrix will be updated with any new Position nodes" +
                    "\n        added and any old Position nodes no longer attached to the" +
                    "\n        Issue node removed from the matrix" +
                    "\n\n    3. Any Decision nodes attached to a Position node will be" +
                    "\n        shown in the matrix as a decision" +
                    "\n\n    4. All references to Global Parameters and external files" +
                    "\n        will be updated" +
                    "\n\n    5. All changes will then be saved to the matrix file and cannot" +
                    "\n        be undone" +
                    "\n\n    6. Any matrix files that are no longer associated with an Issue" +
                    "\n        node in the project will be deleted" +
                    "\n\nTo continue please ensure that any open Issue node windows are closed" +
                    "\nand that any changes to the Global Parameters table have been changed." +
                    "\n\nDo you want to continue?\n\nClick YES to continue\nClick NO to close any open" +
                    " issue node windows\nor to save the Global Parameters table";
            cont = JOptionPane.showConfirmDialog(null, message, "Propagate Warning",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
        }
        
        return cont;
    }


//  Propagate any changes to global parameters or external files throughout all
//  issue node matrix files in the project. Also update the structure of all
//  matrixes in the files
    public void propagateChanges() {
        String decChange = "Node Label#Old Recommended Option(s)#New Recommended Option(s)\n";
        
//      Create a new vector to store the results of the propagate so that
//      they can be displayed to the user at the end of the process
        Vector vtChangeTable = new Vector(1, 1);
        Vector vtChangeTableColNames = new Vector(3, 1);
        vtChangeTableColNames.add("Node Label");
        vtChangeTableColNames.add("Old Recommended Option(s)");
        vtChangeTableColNames.add("New Recommended Option(s)");
        
//      Create new list to store the issue nodes
        FirstInFirstOut issueNodes = new FIFOLinkedList();
        
        try {
            
            int count = 0;
//          Get all the views in this project
            Enumeration views = ProjectCompendium.APP.getModel().getNodeService().getAllActiveViews(ProjectCompendium.APP.getModel().getSession());
            for (Enumeration e = views ; e.hasMoreElements() ; ) {
                View thisView = (View)e.nextElement();
//              Check whether the current view is a MAPVIEW
                if (thisView.getType() == ICoreConstants.MAPVIEW) {
//                  If view not yet initialised then initialise to get details from
//                  the database
                    if (!thisView.isMembersInitialized()) {
                        try {
                            thisView.initializeMembers();
                        } catch (SQLException ex) {
                            ex.printStackTrace();
                        } catch (ModelSessionException ex) {
                            ex.printStackTrace();
                        }
                    }
//                  Get the nodes in the current view
                    Vector nodesInView = thisView.getMemberNodes();
                    Iterator nodesInViewIter = nodesInView.iterator();
                    while (nodesInViewIter.hasNext()) {
//                      Check if node is an ISSUE node
                        NodeSummary thisNode = (NodeSummary)nodesInViewIter.next();
                        if (thisNode.getType() == ICoreConstants.ISSUE) {
//                          Add node to issue node list
                            issueNodes.add(thisNode);
//                          Create new list for POSITION nodes attached to the issue node
                            FirstInFirstOut posNodeList = new FIFOLinkedList();
//                          Create new list for DECISION nodes attached to Position nodes
                            FirstInFirstOut decList = new FIFOLinkedList();
//                          Get all links to the ISSUE node
                            Vector thisIssueNodeLinks = thisView.getLinksForNode(thisNode.getId());
                            Iterator thisIssueNodeLinksIter = thisIssueNodeLinks.iterator();
//                          For each link check if the fromNode is a POSITION node
                            while (thisIssueNodeLinksIter.hasNext()) {
                                Link thisIssueLink = (Link)thisIssueNodeLinksIter.next();
//                              If fromNode is a POSITION node then check if the POSITION node
//                              has a DECISION node attached to it and update the list of
//                              decision nodes and position nodes accordingly
                                if (thisIssueLink.getFrom().getType() == ICoreConstants.POSITION || thisIssueLink.getFrom().getType() == ICoreConstants.POSITION_GREEN) {
                                    Vector thisPosNodeLinks = thisView.getLinksForNode(thisIssueLink.getFrom().getId());
                                    Iterator thisPosNodeLinksIter = thisPosNodeLinks.iterator();
//                                  Check for links from a DECISION node to the POSITION node
                                    while (thisPosNodeLinksIter.hasNext()) {
                                        Link thisPosLink = (Link)thisPosNodeLinksIter.next();
                                        if (thisPosLink.getFrom().getType() == ICoreConstants.DECISION) {
                                            decList.add(thisIssueLink.getFrom().getId());
                                        }
                                    }
//                                  Add the POSITION node to the position node list
                                    posNodeList.add(thisIssueLink.getFrom());
                                }
                            }
//                          Open the matrix file and update the structure and references of the matrix
                            vtChangeTable = updateMatrixFilesPropagate(vtChangeTable, thisNode, posNodeList, decList);
                        }
                    }
                }
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        
//      Get a list of all current issue node filenames (matrix file names)
        String[] fileList = getAllIssueNodeFileNames();
//      Delete any that are no longer associated with an ISSUE node in the project
        deleteOldIssueNodes(issueNodes, fileList);
        
//      Update the decChange string with the contents of the Vector vtChangeTable
//      for copying to the clipboard if required
        Iterator changeTableIter = vtChangeTable.iterator();
        while (changeTableIter.hasNext()) {
            Vector thisRow = (Vector)changeTableIter.next();
            Iterator thisRowIter = thisRow.iterator();
            if (thisRowIter.hasNext()) {
                decChange = decChange + String.valueOf(thisRowIter.next());
            }
            while (thisRowIter.hasNext()) {
                decChange = decChange + "#" + String.valueOf(thisRowIter.next());
            }
            decChange = decChange + "\n";
        }
        
        if (vtChangeTable.size() == 0) {
//          If no changes then display this message
            JOptionPane.showMessageDialog(null, "No decisions have been changed");
        } else {
//          Else display the changes using a UIMessageDialog
//          The code for this is already available in Compendium
            UIMessageDialog changedMatrix = new UIMessageDialog(oParent, decChange);
            changedMatrix.setTitle("Decisions changed as a result of the propagate");
            changedMatrix.addTable(vtChangeTable, vtChangeTableColNames);
            changedMatrix.setVisible(true);
        }
    }
    
    public Vector updateMatrixFilesPropagate(Vector vtChangeTable, NodeSummary thisNode, FirstInFirstOut posNodeList, FirstInFirstOut decList) {
        
//      Update all the matrix files by opening them and getting the latest
//      values from the references, then save the files again.
        String recBeforeUpdate = null;
        String recAfterUpdate = null;
        String nodeId = thisNode.getId();
        MatrixContainer matrix = null;
        try {
            matrix = new MatrixContainer(nodeId);
//          Get the current recommendation
            recBeforeUpdate = matrix.getRecommendation();
//          Update the matrix
            matrix.updateMatrixContainer(posNodeList, decList);
//          Get the new recommendation
            recAfterUpdate = matrix.getRecommendation();
            try {
                matrix.saveMatrixDataToFile(nodeId);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
            return vtChangeTable;
        }
//      If the new recommendation is different to the current one then add the details
//      to the vector created to store the results of the propagate
        if (!recAfterUpdate.equals(null) && (!recAfterUpdate.equals(recBeforeUpdate))) {
            Vector newRow = new Vector(1, 1);
            newRow.add(thisNode.getLabel());
            newRow.add(recBeforeUpdate);
            newRow.add(recAfterUpdate);
            vtChangeTable.add(newRow);
        }
        return vtChangeTable;
    }
    
//  Get a list of all the issue node filenames currently stored for the project
    public String[] getAllIssueNodeFileNames() {
//      Use the ProjectCompendium class to get the homepath of the installation
//      as this may vary depending on the platform and the way the user installed it
        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Project Files/";
        String projectName = com.compendium.ProjectCompendium.APP.getProjectName();
        String dirPath = pathName + projectName + "/Matrix Files";
        
        File myDir = new File(dirPath);
//      Get the listing of the directory
        String[] fileList = myDir.list();
        
        return fileList;
    }
    
//  Delete any matrix files that do not relate to any ISSUE nodes in the project
    public void deleteOldIssueNodes(FirstInFirstOut issueNodes, String[] fileList) {
//      Check to see if any of the files in the directory are for nodes
//      that no longer exist by comparing the fileList array with the
//      list of nodes returned by Compendium. Delete any redundant files.
        
//      Use the ProjectCompendium class to get the homepath of the installation
//      as this may vary depending on the platform and the way the user installed it
        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Project Files/";
        String projectName = com.compendium.ProjectCompendium.APP.getProjectName();
        String dirPath = pathName + projectName + "/Matrix Files";
        
        int fileListLength = fileList.length;
        for (int i = 0 ; i < fileListLength ; i++) {
            int noOfSameFileNames = 0;
            String nodeId = fileList[i];
            Iterator nodeIter = issueNodes.iterator();
            while (nodeIter.hasNext()) {
                NodeSummary node = (NodeSummary)nodeIter.next();
                String compNode = node.getId() + ".csv";
                if (compNode.equals(nodeId)) {
                    noOfSameFileNames = noOfSameFileNames + 1;
                }
            }
            if (noOfSameFileNames == 0) {
                String fileDel = dirPath + "/" + nodeId;
                File delFile = new File(fileDel);
                delFile.delete();
            }
        }
    }
    
//  Perform a sensitivity analysis for the parameter passed as paramName. This will
//  look at the effect of changing the value of paramName by the specified percentage
//  over the specified number of sub-divisions
    public void sensitivityAnalysis(int paramPosition, String paramName, double range, int subDivision) {
//      Get all the ISSUE nodes in the project
        Vector issueNodes = getAllIssueNodes();
        
//      From the vector of issueNodes get all issue node files that have a
//      reference to the parameter in question in them.
//      For each ISSUE node that contains a reference to the parameter add the
//      reference to the MatrixContainer, the label of the node and the
//      current recommendation to the list
//      The three items are added to an array which is then added to the list
        FirstInFirstOut issuesContParam = new FIFOLinkedList();
        Iterator issueNodesIter = issueNodes.iterator();
        while (issueNodesIter.hasNext()) {
            NodeSummary node = (NodeSummary)issueNodesIter.next();
            String nodeId = node.getId();
            String nodeLabel = node.getLabel();
            MatrixContainer matrixFile = null;
            try {
                matrixFile = new MatrixContainer(nodeId);
                Object[] matrixData = {matrixFile, nodeLabel, matrixFile.getRecommendation()};
                if (matrixFile.containsParameter(paramName)) {
                    issuesContParam.add(matrixData);
                }
            }
            catch (FileNotFoundException ex) {
                ex.printStackTrace();
                continue;
            }
        }
//      This string is for sending the results of the analysis to the clipboard if required
        String decChange = "Senstivity Analysis of " + paramName + " varying by +/- " + range + "%\n" +
                "Percentage#Node Label#Old Recommended Option(s)#New Recommended Option(s)\n";
        
//      Create an array of the percentage variation of the parameter
//      based on the number of subdivisions selected by the user
        Double[] divs = new Double[subDivision];
        double totalRange = range * 2;
        double difference = totalRange / subDivision;
        divs[0] = -range;
        divs[subDivision-1] = range;
        if (subDivision > 2) {
            for (int i = 1 ; i < subDivision / 2 ; i++) {
                divs[i] = divs[i-1] + difference;
            }
            for (int i = subDivision - 2 ; i > (subDivision / 2)-1 ; i--) {
                divs[i] = divs[i+1] - difference;
            }
        }
        
//      Create a vector to store the results of the analysis for displaying
//      to the user
        Vector vtChangeTable = new Vector(1, 1);
        Vector vtChangeTableColNames = new Vector(1, 1);
        vtChangeTableColNames.add("Percentage");
        vtChangeTableColNames.add("Node Label");
        vtChangeTableColNames.add("Old Recommended Option(s)");
        vtChangeTableColNames.add("New Recommended Option(s)");
        
//      Hold the original value of the parameter so that it can be reset after
//      the analysis has been completed
        String original = String.valueOf(gpTable.getValueAt(paramPosition, 1));
        double originalValue = Double.valueOf(original);
        
//      Create an array of parameter values to be used in the analysis based
//      on the array of percentage variations already created
        Double[] paramValues = new Double[subDivision];
        for (int i = 0 ; i < subDivision ; i++) {
            paramValues[i] = originalValue + (originalValue * divs[i] / 100);
        }
        
//      For each subdivision value of the parameter held in the array check
//      to see if the decision for each of the ISSUE node matrix files that
//      contain the parameter is affected by the subdivision value
        for (int i = 0 ; i < subDivision ; i++) {
            gpTable.setValueAt(paramValues[i], paramPosition, 1);
            try {
                gpTable.saveGCMatrixToFile("GlobalParameters");
                
//              Work through each matrix file reference held in the list of
//              issue nodes containing a reference to the paramter
                Iterator issuesContParamIter = issuesContParam.iterator();
                while (issuesContParamIter.hasNext()) {
                    String recBeforeUpdate = null;
                    String recAfterUpdate = null;
                    Object[] data = (Object[])issuesContParamIter.next();
                    MatrixContainer matrix = (MatrixContainer)data[0];
                    String nodeLabel = (String)data[1];
                    recBeforeUpdate = (String)data[2];
//                  Update the references and values in the matrix
                    matrix.updateMatrixReferences();
                    matrix.updateDataValues();
//                  Get the recommendation after the update
                    recAfterUpdate = matrix.getRecommendation();
//                  Compare the before and after recommendations and add the
//                  details to the results vector if there is a change
                    if (!recAfterUpdate.equals(recBeforeUpdate)) {
                        Vector newRow = new Vector(1, 1);
                        newRow.add(divs[i] + "%");
                        newRow.add(nodeLabel);
                        newRow.add(recBeforeUpdate);
                        newRow.add(recAfterUpdate);
                        vtChangeTable.add(newRow);
                    }
                }
            }
            catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        
//      Reset the parameter value to its value before the analysis
        gpTable.setValueAt(original, paramPosition, 1);
        try {
            gpTable.saveGCMatrixToFile("GlobalParameters");
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        
//      Copy the vector of changes to the string decChange so that it can be
//      copied to the clipboard if required
        Iterator changeTableIter = vtChangeTable.iterator();
        while (changeTableIter.hasNext()) {
            Vector thisRow = (Vector)changeTableIter.next();
            Iterator thisRowIter = thisRow.iterator();
            if (thisRowIter.hasNext()) {
                decChange = decChange + String.valueOf(thisRowIter.next());
            }
            while (thisRowIter.hasNext()) {
                decChange = decChange + "#" + String.valueOf(thisRowIter.next());
            }
            decChange = decChange + "\n";
        }
        
        if (vtChangeTable.size() == 0) {
//          If no changes then display this message
            JOptionPane.showMessageDialog(null, "No decisions have been changed");
        } else {
//          Else display the changes using a UIMessageDialog
//          The code for this is already available in Compendium
            UIMessageDialog changedMatrix = new UIMessageDialog(oParent, decChange);
            changedMatrix.setTitle("Senstivity Analysis of " + paramName + " varying by +/- " + range + "%");
            changedMatrix.addTable(vtChangeTable, vtChangeTableColNames);
            changedMatrix.setVisible(true);
        }
    }
    
//  Returns a Vector of all the ISSUE nodes in the current project. The Vector
//  contains a NodeSummary of the ISSUE node
    public Vector getAllIssueNodes() {
        com.compendium.core.datamodel.IModel model = com.compendium.ProjectCompendium.APP.getModel();
        com.compendium.core.datamodel.PCSession session = model.getSession();
        
//      Code to find all the authors in the project has been taken from UISearchDialog and
//      adapted for use here in this context
        Vector authors = new Vector();
        String modelName = model.getModelName();
        String userID = model.getUserProfile().getId() ;
        try {
            for(Enumeration en = (model.getUserService().getUsers(modelName, userID)).elements();en.hasMoreElements();) {
                com.compendium.core.datamodel.UserProfile up = (com.compendium.core.datamodel.UserProfile)en.nextElement();
                
                String authorName = up.getUserName();
                if (authorName.equals("")) {
                    continue;
                }
                authors.add(authorName);
	    }
        }
        catch(java.sql.SQLException ex) {
            com.compendium.ProjectCompendium.APP.displayError("Exception:" + ex.getMessage());
        }

//      Code for getting all the Issue nodes has been taken from UISearchDialog and amended for
//      use in this context. Most of the parameters sent to the 
//      model.getQueryService().searchNode() method have been fixed by the author of this class
//      so that all the returned vector will contain all the issue nodes in this project.
        Vector issueNodes = new Vector(10, 1);
        Vector nodeType = new Vector(1);
        nodeType.add(String.valueOf(com.compendium.core.ICoreConstants.ISSUE));
        Vector empty = new Vector();
        Date date = null;
        try {
            issueNodes = model.getQueryService().searchNode(session,
                    com.compendium.core.db.DBSearch.CONTEXT_ALLVIEWS,
                    "",
                    nodeType, authors, empty,
                    com.compendium.core.db.DBSearch.MATCH_ANY,
                    empty,
                    com.compendium.core.db.DBSearch.MATCH_ANY,
                    empty, date, date, date, date);
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
        return issueNodes;
    }

    public void closeButtonMessenge(){
        int cont = 0;

                    String message = "Do you want to save these valeus? " ;
            cont = JOptionPane.showConfirmDialog(null, message, "Save Check",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
           if (cont == 0){
            try {
                gpTable.saveGCMatrixToFile("GlobalParameters");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(null, "Changes have been saved.");

           }

    }
}
