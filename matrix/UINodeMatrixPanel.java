/*
 * UINodeMatrixPanel.java
 *
 * Created on 28 June 2007, 12:07
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This panel displays the Options vs Criteria matrix in the
 * UINodeContentDialog
 *
 * @author Simon Skrzypczak
 */



import com.compendium.ProjectCompendium;
import com.compendium.core.datamodel.services.ViewService;
import com.compendium.ui.*;
import com.compendium.ui.owl.UICriteriaTree;                                    //Krishna added: 
import com.compendium.ui.owl.ConnectionJenaOWL;                                  //Krishna added:
import com.compendium.ui.dialogs.UIColorChooserDialog;
import com.compendium.ui.dialogs.UINodeContentDialog;
import com.compendium.ui.toolbars.UIToolBarFormat;
import java.io.*;
import java.util.*;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;



public class UINodeMatrixPanel extends JPanel implements ActionListener, TableModelListener, Serializable {


    private JPanel northPanel;
    private JPanel northPanelContent;
    private JPanel westPanel;
    private JPanel westPanelContent;
    private JPanel southPanel;

    
    private JButton updateButton;
    private JButton chooseFileButton;                                           //Krishna added: creates a button for a file chooser
    private JButton addCriteriaButton;
    private JButton removeCriteriaButton;
    private JButton saveButton;
    private JButton closeButton;
    private JButton globalParamButton;
    private JButton goalsCritButton;
    private JButton electreButton;
    private JButton helpButton;
    
    private JRadioButton valuesRadioButton;
    private JRadioButton formulaRadioButton;
    private JRadioButton WSMRatioButton;
    private JRadioButton WPMRatioButton;
    private JRadioButton NormalizationButton;
    private JRadioButton NoNormalizationButton;

    
    private JLabel criteriaLabel;
    private JLabel matrixLabel;
    private JLabel totalform;
    
    private JTable ocTable;
    
    private OCMatrix optCritMatrix;
    
    private JScrollPane scrollTable;

    private JTextArea blankSpace1 = new JTextArea();
    private JTextArea blankSpace2 = new JTextArea();
    private JTextArea blankSpace3 = new JTextArea();
    private JTextArea blankSpace4 = new JTextArea();
    private JTextArea blankSpace5 = new JTextArea();
    private JTextArea blankSpace6 = new JTextArea();
    private JTextArea blankSpace7 = new JTextArea();
    
    private UINodeContentDialog oParentDialog;
    private UINode ouinode;

    private final String CHFILE="chfile";
    private final String ADDIT = "addit";
    private final String REMIT = "remit";
    private final String SAVIT = "savit";
    private final String CLOSIT = "closit";
    private final String GOALS = "goals";
    private final String GLOBL = "globl";
    private final String UPDAT = "updat";
    private final String VALUES = "values";
    private final String FORMULA = "formula";
    private final String WSM = "wsm";            // Have to put functionality
    private final String WPM = "wpm";            // Have to put functionality
    private final String ELECTRE = "electre";    // Have to put functionality
    private final String HELP = "help";          // Have to put functionality
    private final String NORM = "normalization";
    private final String NONORM = "noNomralization";
    private final String CCOLOUR = "cColour";
    private final String CCOLOUR2 = "cColour2";
    private final String CCOLOUR3 = "cColour3";
    private final String CCOLOUR4 = "cColour4";

    private Vector xcheck;

    private UIColorChooserDialog oColorChooserDialog = null;

    private ProjectCompendiumFrame	oParent			= null;
    private  UINode oNode;
    private UIToolBarFormat selected;
    private ViewService view;
    private MatrixContainer mc;


    /** Creates a new instance of UINodeMatrixPanel */
    public UINodeMatrixPanel(JFrame parent, UINode oUINode, UINodeContentDialog tabbedPane) throws IOException {
        super();

        oNode = oUINode;
        ouinode = oUINode;
        oParentDialog = tabbedPane;
        
//      BorderLayout used for the panel overall  
        this.setLayout(new BorderLayout());
        
//      North panel of the overall layout. This comprises a FlowLayout. Within
//      it is a panel which uses a GridLayout to space the components evenly.
//      There is also a blankSpace1 which just adds some space between the
//      Update button and the JRadioButtons.
        northPanel = new JPanel();
        northPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        northPanelContent = new JPanel();
        northPanelContent.setLayout(new GridLayout(1, 0, 10, 10));



                //      Display the 'values' initially

        matrixLabel = new JLabel("MCDM Option:");
        northPanelContent.add(matrixLabel);

        //        matrixLabel = new JLabel("O WSM");
        //northPanelContent.add(matrixLabel);

        //        matrixLabel = new JLabel("O WPM");
        //northPanelContent.add(matrixLabel);

        //        matrixLabel = new JLabel("O AHP");
        //northPanelContent.add(matrixLabel);

        WSMRatioButton = new JRadioButton("WSM", true);
        WSMRatioButton.addActionListener(this);
        WSMRatioButton.setActionCommand(WSM);
        northPanelContent.add(WSMRatioButton);

        WPMRatioButton = new JRadioButton("WPM");
        WPMRatioButton.addActionListener(this);
        WPMRatioButton.setActionCommand(WPM);
        northPanelContent.add(WPMRatioButton);

        ButtonGroup displayButtons = new ButtonGroup();
        displayButtons.add(WSMRatioButton);
        displayButtons.add(WPMRatioButton);

        electreButton = new JButton("ELECTRE");
        electreButton.addActionListener(this);      //LATER
        electreButton.setActionCommand(ELECTRE);
        northPanelContent.add(electreButton);



        northPanel.add(northPanelContent);
        this.add("North", northPanel);

        
//      West panel of the overall layout. This comprises a BorderLayout. Within
//      its North position is a panel which comprises a GridLayout to position
//      the Criteria label and Add and Remove buttons evenly. There are two
//      blankSpace components to space the buttons away from the Update button
        westPanel = new JPanel();
        westPanel.setLayout(new BorderLayout());
        westPanelContent = new JPanel();
        westPanelContent.setLayout(new GridLayout(0,1,5,5));
        
        blankSpace2.setEditable(false);
        blankSpace2.setBackground(this.getBackground());
        westPanelContent.add(blankSpace2);


        //      Display the 'values' initially

        matrixLabel = new JLabel("View Options");
        westPanelContent.add(matrixLabel);

        valuesRadioButton = new JRadioButton("Values", true);
        valuesRadioButton.addActionListener(this);
        valuesRadioButton.setActionCommand(VALUES);
        westPanelContent.add(valuesRadioButton);

        formulaRadioButton = new JRadioButton("Formula");
        formulaRadioButton.addActionListener(this);
        formulaRadioButton.setActionCommand(FORMULA);
        westPanelContent.add(formulaRadioButton);

        ButtonGroup displayButtons2 = new ButtonGroup();
        displayButtons2.add(valuesRadioButton);
        displayButtons2.add(formulaRadioButton);

        totalform = new JLabel("Total Normalization");
        westPanelContent.add(totalform);
        
        NoNormalizationButton = new JRadioButton("Normal", true);
        NoNormalizationButton.addActionListener(this);
        NoNormalizationButton.setActionCommand(NONORM);
        westPanelContent.add(NoNormalizationButton);

        NormalizationButton = new JRadioButton("Normalized");
        NormalizationButton.addActionListener(this);
        NormalizationButton.setActionCommand(NORM);
        westPanelContent.add(NormalizationButton);

        ButtonGroup displayButtons3 = new ButtonGroup();
        displayButtons3.add(NoNormalizationButton);
        displayButtons3.add(NormalizationButton);


        matrixLabel = new JLabel("Table");
        westPanelContent.add(matrixLabel);


        // Krishn added: adds "Choose an ontology file" button to the west panel.
        chooseFileButton = new JButton("Choose an ontology file");
        chooseFileButton.addActionListener(this);
        chooseFileButton.setActionCommand(CHFILE);
        westPanelContent.add(chooseFileButton);

        addCriteriaButton = new JButton("Add Criterion");
        addCriteriaButton.addActionListener(this);
        addCriteriaButton.setActionCommand(ADDIT);
        westPanelContent.add(addCriteriaButton);
        
        removeCriteriaButton = new JButton("Remove Criterion");
        removeCriteriaButton.addActionListener(this);
        removeCriteriaButton.setActionCommand(REMIT);
        westPanelContent.add(removeCriteriaButton);

        blankSpace7.setEditable(false);
        blankSpace7.setBackground(this.getBackground());
        westPanelContent.add(blankSpace7);


        updateButton = new JButton("Update");
        updateButton.addActionListener(this);
        updateButton.setActionCommand(UPDAT);
        westPanelContent.add(updateButton);

        saveButton = new JButton("Save");
        saveButton.addActionListener(this);
        saveButton.setActionCommand(SAVIT);
        westPanelContent.add(saveButton);
        
        closeButton = new JButton("Close");
        closeButton.addActionListener(this);
        closeButton.setActionCommand(CLOSIT);
        westPanelContent.add(closeButton);

     /*   //JUST ADDED
        changeColour = new JButton("Change Colour");
        changeColour.addActionListener(this);
        changeColour.setActionCommand(CCOLOUR);
        westPanelContent.add(changeColour);

        changeColour2 = new JButton("Change Colour2");
        changeColour2.addActionListener(this);
        changeColour2.setActionCommand(CCOLOUR2);
        westPanelContent.add(changeColour2);

        changeColour3 = new JButton("Change Colour3");
        changeColour3.addActionListener(this);
        changeColour3.setActionCommand(CCOLOUR3);
        westPanelContent.add(changeColour3);

        changeColour4 = new JButton("Change Colour4");
        changeColour4.addActionListener(this);
        changeColour4.setActionCommand(CCOLOUR4);
        westPanelContent.add(changeColour4);
*/
        
        westPanel.add("North", westPanelContent);
        this.add("West", westPanel);
        
//      Center panel of the overall layout
        optCritMatrix = new OCMatrix(ouinode);
        optCritMatrix.addTableModelListener(this); // This adds a listener to the list that's notified each time a change to the data model occurs
        
        ocTable = new JTable(optCritMatrix);       // Mkes a table of the optcritmatrix

        scrollTable = new JScrollPane(ocTable);    // It includes the optCritMatrix into a scrolltable
        
        this.add("Center", scrollTable);
        
//      South panel contains a button for the Global Parameters table
//      and one for the Goals vs Criteria table
        southPanel = new JPanel();

        globalParamButton = new JButton("Global Parameters");
        globalParamButton.addActionListener(this);
        globalParamButton.setActionCommand(GLOBL);
        southPanel.add(globalParamButton);
        
        goalsCritButton = new JButton("Goals vs Criteria");
        goalsCritButton.addActionListener(this);
        goalsCritButton.setActionCommand(GOALS);
        southPanel.add(goalsCritButton);

        blankSpace3.setEditable(false);
        blankSpace3.setBackground(this.getBackground());
        southPanel.add(blankSpace3);

        blankSpace5.setEditable(false);
        blankSpace5.setBackground(this.getBackground());
        southPanel.add(blankSpace5);

        blankSpace6.setEditable(false);
        blankSpace6.setBackground(this.getBackground());
        southPanel.add(blankSpace6);

        blankSpace4.setEditable(false);
        blankSpace4.setBackground(this.getBackground());
        southPanel.add(blankSpace4);

        blankSpace1.setEditable(false);
        blankSpace1.setBackground(this.getBackground());
        northPanelContent.add(blankSpace1);

        helpButton = new JButton("Help");
        helpButton.addActionListener(this);
        helpButton.setActionCommand(HELP);
        southPanel.add(helpButton);

        this.add("South", southPanel);

        this.setVisible(true);
    }


    

    //** Makes it close if I press ENTER
    public void setDefaultButton() {
	oParentDialog.getRootPane().setDefaultButton(closeButton);
    }
    
    public void actionPerformed(ActionEvent e) {
        String comm = e.getActionCommand();           //**This will get the mouse clicks

        int method;
        int norm;

//***************************************** Krishna added: ************************************************************************
        // Krishna added : the choosen file is selected as the ontology file
        if (comm.equals(CHFILE)){
            JFileChooser fc = new JFileChooser();
            int returnVal = fc.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
               ConnectionJenaOWL.fileName = fc.getSelectedFile().getPath();
              
        }}

        if (comm.equals(ADDIT)) {                     //** If click in add criterion...
            // JulPath: 1 (CriteriaList)
            // Krishna added: gets the updated matrix from the tree dialog
           UICriteriaTree critTree= new UICriteriaTree(ProjectCompendium.APP, optCritMatrix);
           optCritMatrix = critTree.getMatrix();

//***************************************** end of Krishna added: ******************************************************************
       
//            // Simons criteria list reading from a text file: disabled by Krishna;
//            CriteriaList cList = new CriteriaList();  //**Create a criteria list *********************************** I HAVE TO CREATE A CRITERIA LIST FOR EACH ISSUE THAT COMES TOGUETHER WITH THE GOALS VS CRITERIA MATRIX
//            Vector critList = cList.getCriteria();    //**This will make a vector to create the criteria list
//            int critListSize = critList.size();       //**Get the size of the criteria list
//            Object[] critArray = new Object[critListSize];
//            // JulPath: 1-5 this will turn the Vector into a critArray
//            critList.copyInto(critArray);
//            if (critListSize > 0) {
//                Object response = JOptionPane.showInputDialog(null, "Please select the criterion to add", "Criteria", JOptionPane.QUESTION_MESSAGE,
//                        null, critArray, null);        //**This produces the dialod full of different criterion to add
//                if (!response.equals(null)) {
//                    // JulPath: 1-6 it gets the criteria choosen by the array and the send it to OCMatrix.addCriteria
//                    optCritMatrix.addCriteria(response);//**It get the text from CriteriaList, so look at it in detail
//                }
//            }
        }
        if (comm.equals(REMIT)) {                       //**If click in Remove Criterion, this will take to OCMatrix
            optCritMatrix.removeCriteria();
        }
//      Save the matrix to a file in its current form
        if (comm.equals(SAVIT)) {

            try {
                optCritMatrix.saveMatrixToFile();       //**Calls the method from OCMatrix to save matrix to file
            } catch (IOException ex) {      //  ????  It is involved to saving files
                ex.printStackTrace();       //  this always come toguether with IOExeption
            }
            JOptionPane.showMessageDialog(null, "The matrix has been saved.");    //**shows the dialog when saved
        }
          
//      Close the matrix window
        if (comm.equals(CLOSIT)) {

           int cont = 0;

          String message = "Do you want to save these values? " ;
          cont = JOptionPane.showConfirmDialog(null, message, "Save Check",
                    JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
           if (cont == 0){

            try {
                optCritMatrix.saveMatrixToFile();       //**Calls the method from OCMatrix to save matrix to file
            } catch (IOException ex) {      //  ????  It is involved to saving files
                ex.printStackTrace();       //  this always come toguether with IOExeption
            }
            JOptionPane.showMessageDialog(null, "The matrix has been saved.");    //**shows the dialog when saved
           }
           oParentDialog.onCancel();
        }
//      Create a new Goals vs Criteria window  
        if (comm.equals(GOALS)) {                                                           //Check how it works in the GoalscriteriaDialog
            // JulPath: 2
                GoalsCriteriaDialog gcm = new GoalsCriteriaDialog(ProjectCompendium.APP);   // ProjectCompendium.APP involves to adding a new window, but not just a dialog
        }
//      Create a new Global Parameters window  
        if (comm.equals(GLOBL)) {
            if (!GlobalParamDialog.getState()) {                                             //check GlobalParamDialog
                GlobalParamDialog gpm = new GlobalParamDialog(ProjectCompendium.APP);
            }
            
        }
//      Update the matrix by checking the nodes that are attached to the ISSUE
//      node, checking whether there are any DECISION nodes attached to the
//      POSITION nodes, getting the current values for any references to Global
//      Parameters or Excel files and recalculating the values in the matrix.
        if (comm.equals(UPDAT)) {
            optCritMatrix.updateMatrix(ouinode);         //**check how it works in OCMatrix
        }
//      Display the values in the matrix on the screen  
        if (comm.equals(VALUES)) {                       //**chck how it works in OCMatrix
            optCritMatrix.displayValues();
        }
//      Display the formulas in the matrix on the screen  
        if (comm.equals(FORMULA)) {                      //**check how it works in OCMatrix
            optCritMatrix.displayFormula();
        }

        //      Open the ELECTRE panel
        if (comm.equals(ELECTRE)) {
           try {
                optCritMatrix.saveMatrixToFile();       //**Calls the method from OCMatrix to save matrix to file
            } catch (IOException ex) {      //  ????  It is involved to saving files
                ex.printStackTrace();       //  this always come toguether with IOExeption
            }
            try {
                optCritMatrix.electreDialog(ouinode);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
//      Open Help file
        if (comm.equals(HELP))  {       //** CHECK HOW TO OPEN THE TABLE!!!

            try {

                HelpDialog help = new HelpDialog(ProjectCompendium.APP, "HelpMatrix");
            } catch (IOException ex) {
                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
            }
        }


        if (comm.equals(WSM))   {

           method = 0;
             optCritMatrix.chooseMCDMMethod(method);
        }

        if (comm.equals(WPM))   {

           method = 1;
             optCritMatrix.chooseMCDMMethod(method);
        }


        if (comm.equals(NONORM))   {

           norm = 0;
           optCritMatrix.chooseNormalization(norm);
        }
           
        if (comm.equals(NORM)){
            
           norm = 1;
           optCritMatrix.chooseNormalization(norm);
        }
     /*   if (comm.equals(CCOLOUR)){

            /*com.compendium.core.datamodel.IModel model = com.compendium.ProjectCompendium.APP.getModel();
            com.compendium.core.datamodel.PCSession session = model.getSession();
                        int cont4 = 0;
                cont4 = JOptionPane.showConfirmDialog(null, "This is the problem", "Save Check", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            com.compendium.core.datamodel.services.FavoriteService favorite = new  FavoriteService();

              int cont3 = 0;
                cont3 = JOptionPane.showConfirmDialog(null, "This is the problem", "Save Check", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

            Vector favoriteVector = new Vector();
            try {
                favoriteVector = favorite.getFavorites(session, "Julian's PhD");
            } catch (SQLException ex) {
               Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
              int cont2 = 0;
                cont2 = JOptionPane.showConfirmDialog(null, "This is the problem", "Save Check", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            }
            String favoriteString = null;
            favoriteString = String.valueOf(favoriteVector);
            int cont = 0;

            String message = "favorite" + favoriteString;
            cont = JOptionPane.showConfirmDialog(null, message, "Save Check", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE); 
        }
        if (comm.equals(CCOLOUR2)){

                Vector vtUpdateNodes = new Vector();
                int a = 0;


                 //    NodePosition pos = oNode.getNodePosition();
                for (Enumeration en = oNode.getLinks();a<1;) {

               //JulPath: 1-6-1-3 add the Criterion from the possition nodes connected.
                UINode uINode = (UINode)en.nextElement();

                NodePosition pos = uINode.getNodePosition();

            com.compendium.core.datamodel.IModel model = com.compendium.ProjectCompendium.APP.getModel();
            com.compendium.core.datamodel.PCSession session = model.getSession();

            UIViewFrame frame = ProjectCompendium.APP.getCurrentFrame();
                          int cont2 = 0;
                cont2 = JOptionPane.showConfirmDialog(null, "This is the problem", "Save Check", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			Model oModel = (Model)ProjectCompendium.APP.getModel();

                cont2 = JOptionPane.showConfirmDialog(null, "This is the problem", "Save Check", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
			if (frame instanceof UIMapViewFrame) {
		//		Vector vtUpdateNodes = new Vector();
                vtUpdateNodes.addElement(pos);
				UIMapViewFrame oMapFrame = (UIMapViewFrame)frame;
				UIViewPane pane = oMapFrame.getViewPane();

                             
                cont2 = JOptionPane.showConfirmDialog(null, "This is the problem", "Save Check", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					try {
						((ViewService)oModel.getViewService()).setTextBackground(oModel.getSession(), pane.getView().getId(), vtUpdateNodes, 36752);
                         cont2 = JOptionPane.showConfirmDialog(null, "This is the problem", "Save Check", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
						int count = vtUpdateNodes.size();
						for (int i=0; i<count;i++) {
							pos = (NodePosition)vtUpdateNodes.elementAt(i);
							pos.setBackground(36752);
						}
					} catch (SQLException ex) {
						ProjectCompendium.APP.displayError("Unable to update label text Background due to:\n\n"+ex.getMessage());
					}
				}
                a++;
                }
        }

        if (comm.equals(CCOLOUR3)){
                String positions = null;
                int a = 0;
                Vector id = new Vector();

                 //    NodePosition pos = oNode.getNodePosition();
                for (Enumeration en = oNode.getLinks();en.hasMoreElements();) {

               //JulPath: 1-6-1-3 add the Criterion from the possition nodes connected.
                UILink uilink = (UILink)en.nextElement();
                String fromNodeId = uilink.getFromNode().getNode().getId();

                if (!fromNodeId.equals(oNode.getNode().getId())) {
                    if (uilink.getFromNode().getType() == ICoreConstants.POSITION) {
                        NodeSummary linkNode = (uilink.getFromNode()).getNode();
                       
                        positions = (linkNode.getLabel());
                        if(positions.equals("Brussels")){
                            

                   /*        try {
                                linkNode.setSource("file:/C:/Program Files/Compendium/Skins/Claret/", "position.GIF", "Julian Hunt");

                            } catch (SQLException ex) {
                                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ModelSessionException ex) {
                                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
        

                           try {

                                linkNode.setLabel("*"+positions, "Julian Hunt");
                            } catch (SQLException ex) {
                                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ModelSessionException ex) {
                                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                        
                        //JulPath: 1-6-1-4 send the ID of the position node to the nodeIdrow in the MATRIX
                        id.add(linkNode.getId());

                    }
                }
                a++;
            }
        //      Object response = JOptionPane.showInputDialog(null, "Pick the Criteria", "Choose the Criteria", JOptionPane.QUESTION_MESSAGE, null, positions, null);
        }
         if (comm.equals(CCOLOUR4)){
                  String positions = null;
                             for (Enumeration en = oNode.getLinks();en.hasMoreElements();) {

               //JulPath: 1-6-1-3 add the Criterion from the possition nodes connected.
                UILink uilink = (UILink)en.nextElement();
                String fromNodeId = uilink.getFromNode().getNode().getId();

                if (!fromNodeId.equals(oNode.getNode().getId())) {
                    if (uilink.getFromNode().getType() == ICoreConstants.POSITION) {
                        NodeSummary linkNode = (uilink.getFromNode()).getNode();
                        
                        positions = (linkNode.getLabel());
                        if(positions.equals("Brussels")){

                            //String lable = mc.getRecommendation();
                      //      try {
                      //          linkNode.setLabel("sex", "Julian Hunt");
                      //      } catch (SQLException ex) {
                      //          Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                      //      } catch (ModelSessionException ex) {
                      //          Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                      //      }
                        //    try {
                        //        linkNode.setImageSize(200, 200, "Julian Hunt");
                        //    } catch (SQLException ex) {
                        //        Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                        //    } catch (ModelSessionException ex) {
                        //        Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                        //    }
                            try {
                                linkNode.setType(52, "Julian Hunt");
                         
                            } catch (SQLException ex) {
                                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                            } catch (ModelSessionException ex) {
                                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
          //                 int cont2 = 0;
          //                 cont2 = JOptionPane.showConfirmDialog(null, "This is the problem " + asd, "Save Check", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
                       //     try {
                       //         linkNode.setType(3, "Julian Hunt");
                       //     } catch (SQLException ex) {
                       //         Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                       //     } catch (ModelSessionException ex) {
                       //         Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                    //    * "163111851229789593190"
                            }}}}

           /*    int col = 36752;
             Vector vtPositions = new Vector();
         //    linkNode.getNodeSummary("163111851229789593190").get
             vtPositions.add(oNode.getNodePosition());

				UIViewPane pane = oNode.getViewPane();
                 com.compendium.core.datamodel.IModel model = oNode.getNode().getModel();
        
                            try {
                                view.setTextBackground(model.getSession(), pane.getView().getId(), vtPositions, 36752);
                            } catch (SQLException ex) {
                                Logger.getLogger(UINodeMatrixPanel.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }}}} */

             //             int a = 0;
   

                 //    NodePosition pos = oNode.getNodePosition();
     //           for (Enumeration en = oNode.getLinks();en.hasMoreElements();) {

               //JulPath: 1-6-1-3 add the Criterion from the possition nodes connected.
       //         UILink uilink = (UILink)en.nextElement();
      //          uilink.setBackground(Color.PINK);
         //       uilink.setSelectedColor(Color.BLUE);
           //     a++;
           // }
        //      Object response = JOptionPane.showInputDialog(null, "Pick the Criteria", "Choose the Criteria", JOptionPane.QUESTION_MESSAGE, null, positions, null);
      //  }
    }



    public void tableChanged(TableModelEvent e) {          //?? Dont know what is this for
        optCritMatrix.removeTableModelListener(this);      //**TableModelListener defines the interface for an object that listens to changes in a TableModel
        int col = e.getColumn();
        int row = e.getFirstRow();
        
        if (col > -1 && row > -1) {
//          If a cell has been edited
            optCritMatrix.changeCellValues(row, col);
        } else {
//          If a cell hasn't been edited
            optCritMatrix.updateCellValues();
        }        
        optCritMatrix.addTableModelListener(this);
    }
    
}
