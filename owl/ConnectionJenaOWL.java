/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.compendium.ui.owl;


import com.compendium.ProjectCompendium;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.NodeIterator;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Vector;

import javax.swing.JOptionPane;


/**
 * This class reads a list of criteria from an owl file that can be selected for the
 * Options vs Criteria matrix. It replaces of use of matrix.CriteriaList class originally
 * created by Simon to read data from a plain text.
 *
 * @author Krishna Sapkota
 */
public class ConnectionJenaOWL {

    /** The name space to be used while dealing with owl file  */
    public static  String         nameSpace ;
    
    /** The first vector to contain criteria data  */
    //public static Vector<OWLData> critListAll;

    /** The first vector to contain parameter data  */
    public static Vector<OWLData> paramListAll;

    /** The first vector to contain goals data  */
    public static Vector<OWLData> dataListAll;

    /** The second vector to contain parameter */
    private static Vector<ParameterData> paramList;

    /** The second vector to contain goal  */
    private static Vector           dataList;
    
    /** It holds criteria type and list (vector) of goals  */
    private static OWLData          owlData;
    
    /** It is a jena model object to collect and hold all the information from the owl file  */
    private static Model            model;
    public static String            fileName = "";
    public static File              owlFile;
    private static String           dataType;
    private static int              openingCounter = 0;
    private static boolean          areRelated ;
    private static boolean          showNoFileWarning;
    private static String           pathName;

    /**
     * Constructor: reads owl file and fills the vectors with the relavent information.
     * The owl file is read and a Jena Model is is updated with it. A Jena Model stores information
     * in the form of triples (Subject, property and Object). SPARQL query language is used to query
     * owl or rdf file. Here in Jena (This is an old version comes with Compendium source code library,
     * triplestore.jar), instead of direct SPARQL, Jena's own methods are used.
     * Once the information is obtained in Jena Model, it will be transfered to
     * normal vector which has criteria, goals or parameters types and vectors having
     * list of criteria, goals or paramters.
     */
    public ConnectionJenaOWL()  {
        setFilePathAndName();
        useCopyOfTheFile();
        readModel(owlFile);

        // gets the namespace of the local file to work with.
        ResIterator iterNS =  model.listSubjects();
        nameSpace = iterNS.nextResource().getNameSpace();
      
}

    public  Model getModel(){
        return model;
    }

    public  void setModel(Model mdl){
        model = mdl;
    }
    
   /**
    *  sets (selects) the path and the file name to read or write.
    */
    private void setFilePathAndName(){

        showNoFileWarning = true;

        String homePath = ProjectCompendium.sHOMEPATH;                            //** Find the directory where compendium were instaled
        pathName = homePath + "/System/resources/Project Files/";                 //** go to the Project Files folder 
        // String projectName = ProjectCompendium.APP.getProjectName();           //** Get the project name 

        if (fileName.equals(""))  {
            fileName = pathName + "/compendium.owl";                              //** Add the name and format of the file
        }
        
        // If first time connecting to the owl file it displays which owl file it is using.
        if (!fileName.equals("") && openingCounter == 0){
            // Question to ask in the showConfirmDialog
            String strQuestion = "Selected ontology file = " + fileName+" \n" +
                    "If you want to use this file select \"Yes\". \n" +
                    "If you want to use a different file, please, press \"Choose an ontolology file\" to select one.";

            // It gets an integer value for yes or no from the comfirm dialog.
            int respAdd  =  JOptionPane.showConfirmDialog(null, strQuestion, "Ontology file selection", JOptionPane.YES_NO_OPTION);

            // If yes is selected in the confirm dialog, the criteria will be added to the matrix.
            // else the file name will be blank (not chosen) again.
            if (respAdd== JOptionPane.YES_OPTION) {
                openingCounter ++;
                owlFile = new File(fileName);

            } else {
               fileName = "";
               showNoFileWarning = false;
            }
        }
    }

    /**
     * This method provides options and warnings for creating a copy of working
     * OWL ontology file. It add a prefix Copy_ in front of the original file.
     */
    private void useCopyOfTheFile(){
        String fName= owlFile.getName();
        String copyfName="Copy_"+fName;
        if (!fName.startsWith("Copy_")){                                        // if a copy doesn't already exist, it enters the copy creation process
           boolean fileExists = false;
           File dir = new File(pathName);

            // Here, FilenameFilter is modified
            // to filter files which end with ".owl"
            FilenameFilter filter = new FilenameFilter() {
                public boolean accept(File dir, String name) {
                    return name.endsWith(".owl");
                }
            };

            //Now the filter is applied to dir.
            //childern is an array of all the owl files in the specified directory.
            String[] children = dir.list(filter);

                //Now we are checking, if a copy of the chosen file is already there.
                for (int i=0; i<children.length; i++) {
                    if (copyfName.equals(children[i])){
                        fileExists = true;
                    }
                }

           if (!fileExists){
               JOptionPane.showMessageDialog(null, "Copy of this file does not exist"
                       + "\nCompendium will create a copy of this file.");
               copyFile(owlFile);
           }else {
              owlFile = new File(pathName+"Copy_"+fName);
           }

           JOptionPane.showMessageDialog(null," You are working with = " + owlFile.getName()
                   + "\n You have no authority to modify the original file. "
                   +"\nAny changes will be saved in this(copy) file");
        }
    }

    /**
     * It makes a copy file of the chosen file,
     * which involves reading the original file and writting the copied file.
     * @param originalFile is the file to be copied.
     */
    private void copyFile(File originalFile){
        FileReader in = null;
        FileWriter out = null;
        try {
            File copyFile = new File(pathName+"Copy_"+originalFile.getName());
            in = new FileReader(originalFile);
            out = new FileWriter(copyFile);
            int c;

            // The file copying operation is acomplished within this while loop.
            while ((c = in.read()) != -1) {
                out.write(c);
            }

            //Now the working file is chosen as newly created copy file.
            owlFile= copyFile;

            //All done, now its time to close the read and write operation.
            in.close();
            out.close();
        } catch (IOException ex) {
            JOptionPane.showInputDialog(null, "Error on copying the selected file");
        } finally {
            try {
                in.close();
            } catch (IOException ex) {
                JOptionPane.showInputDialog(null, "Error on reading the selected (original) file");
            }
            try {
                out.close();
            } catch (IOException ex) {
                JOptionPane.showInputDialog(null, "Error on writing the copy of the selected file");
            }
        }

    }

    /**
     * reads the owl file and stores into an ontology model
     */
    private void readModel(File owlFile){
       InputStream  in;
       try {
            in = new FileInputStream(owlFile);

            // creates an empty model
             model = ModelFactory.createDefaultModel();

            // reads the RDF/XML file
            model.read(in, "");
        } catch (FileNotFoundException ex) {
             //if  the file is not found, it shows the message.
            if ((showNoFileWarning) && (fileName.equals(""))) {
            JOptionPane.showMessageDialog(null, "No file selected !" +
                    "\n Please, press \"Choose an ontolology file\" to selecet one. ",
                    "Criteria Warning",
                    JOptionPane.WARNING_MESSAGE);
            }
        } catch (IOException ex){
            ex.printStackTrace();
        }
    }

    /**
     *  writes the model back into the ontology file (owl file).
     */
    private  void writeModel(){
        OutputStream out;
        try {
           out = new FileOutputStream(owlFile);
           model.write(out);
        } catch (FileNotFoundException ex) {
          JOptionPane.showMessageDialog(null, "File writing error", "Error",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * adds a new criteria type to the model.
     * @param rawClassName unformatted name of the class.
     */
    public void addCriterionType(String rawClassName){
         rawClassName = getTestedName(rawClassName);
         Resource resClass =createNewResource(rawClassName);  
         Resource resSuperClass = model.getResource(nameSpace+"Criteria");
         model.add(resClass, RDFS.subClassOf,resSuperClass );
         model.add(resClass, RDFS.label,rawClassName);
         writeModel();
    }

    /**
     * adds a criterion to the model
     * @param rawIndName name of the new individual which is not formatted.
     * @param rawClassName unformatted name of the existing class to which the new individual belongs.
     */
    public void addCriterion(String rawIndName, String rawClassName){
        rawClassName = getTestedName(rawClassName);
        Resource resClass = getExistingClassFromLabel(rawClassName);
        Resource resNewInd =createNewResource(rawIndName);
        model.add(resNewInd, RDF.type,resClass );

        Property propDescription =getExistingProperty("description");
        model.add(resNewInd, propDescription, rawIndName);
        writeModel();
    }

    /**
     * gets the vector of criteria
     * @return a vector of OWLData. OWLData has another vector of criterion.
     */
    public Vector<OWLData> getCriteria() {                             
        return getVector("Criteria");
    }

    /**
     * adds a new parameter type to the model.
     * @param rawClassName unformatted name of the class to be added.
     */
    public void addParameterType(String rawClassName){
         rawClassName= getTestedName(rawClassName);
         Resource resClass =createNewResource(rawClassName);
         Resource resSuperClass = model.getResource(nameSpace+"Parameters");
         model.add(resClass, RDFS.subClassOf,resSuperClass );
         model.add(resClass, RDFS.label,rawClassName);
         writeModel();
    }

    /**
     * adds a new parameter with relevant data to the model.
     * @param param is a ParameterData which contains relevant information to create a parameter.
     * @param rawClassName unformattted name of the class to which the parameter belongs.
     */
    public void addParameter(ParameterData param, String rawClassName){
        rawClassName = getTestedName(rawClassName);
        Resource resClass = getExistingClassFromLabel(rawClassName);
        Resource resNewInd =createNewResource(param.getName());
        model.add(resNewInd, RDF.type,resClass );
        Property propDescription = getExistingProperty("description");
        model.add(resNewInd, propDescription, param.getName());
        Property propValue = getExistingProperty("value");
        model.add(resNewInd, propValue, param.getValue());
        Property propUnit = getExistingProperty("unit");
        model.add(resNewInd, propUnit, param.getUnit());
        writeModel();    
    }

    /**
     * Return a Vector of the parameters currently available
     */
    public Vector<OWLData> getParameters() {
        return getVector("Parameters");
    }

    /**
     *  adds a new goal type to the the model.
     * @param rawClassName unformatted name of the class (goal type).
     */
     public void addGoalType(String rawClassName){
         rawClassName = getTestedName(rawClassName);
         Resource resClass = createNewResource(rawClassName);
         Resource resSuperClass = model.getResource(nameSpace + "Goals");
         model.add(resClass, RDFS.subClassOf,resSuperClass );
         model.add(resClass, RDFS.label,rawClassName);
         writeModel();     
    }

     /**
      * adds a goal (as an individual) to the specified class.
      * @param rawIndName name of the indivudual to add.
      * @param rawClassName nae of the class the individual belongs.
      */
    public void addGoal(String rawIndName, String rawClassName){
        rawClassName = getTestedName(rawClassName);
        Resource resClass = getExistingClassFromLabel(rawClassName);
        Resource resNewInd = createNewResource(rawIndName);
        model.add(resNewInd, RDF.type,resClass );

        Property propDescription = getExistingProperty("description");
        model.add(resNewInd, propDescription, rawIndName);
        writeModel();
    }

   /**
    * Return a Vector of the goals currently available
    */
    public Vector<OWLData> getGoals() {
        return getVector("Goals");
    }

    /**
     * creates a new resource
     * @param rawName unformatted name of the resource
     * @return resource
     */
    private Resource createNewResource(String rawName){
        String trimmedName = rawName.replace(" ", "_").trim();  
        return model.createResource(nameSpace + trimmedName);
    }

    /**
     *  gets an existing property from the model.
     * @param rawName
     * @return
     */
    private Property getExistingProperty(String rawName){
        String trimmedName = rawName.replace(" ", "_").trim();                  // redundant. the property name supplied should be already correct one.
        return model.getProperty(nameSpace, trimmedName);
    }

    /**
     * gets an existing class from its label.
     * @param rawLabel
     * @return
     */
    private Resource getExistingClassFromLabel(String rawLabel){
        ResIterator iterGoals = model.listSubjectsWithProperty(RDFS.label, rawLabel );
        Resource resSuperClass = iterGoals.nextResource();
        return     resSuperClass;
    }



    /**
     *  gets a resource from its description.
     * @param description is string value to describe the resource.
     * @return existing resource from the description provided.
     */
    private Resource getExistingResource(String description){
       Property propDescription = this.getExistingProperty("description");
       ResIterator iterRes = model.listSubjectsWithProperty(propDescription,description);
       return iterRes.nextResource() ;
    }

    public void addHasCriteria(String goal, String criterion){
        Resource resGoal = getExistingResource(goal);
        Resource resCrit = getExistingResource(criterion);
        Property hasCriteria= getExistingProperty("hasCriteria");
        Property isCriteriaOf= getExistingProperty("isCriteriaOf");
        model.add(resCrit, isCriteriaOf, resGoal);
        model.add(resGoal, hasCriteria, resCrit);
        writeModel();
    }

    public void removeHasCriteria(String goal, String criterion){
        Resource resGoal = getExistingResource(goal);
        Resource resCrit = getExistingResource(criterion);
        Property hasCriteria= getExistingProperty("hasCriteria");
        Property isCriteriaOf= getExistingProperty("isCriteriaOf");
        model.remove(model.listStatements(resGoal, hasCriteria, resCrit));
        model.remove(model.listStatements(resCrit, isCriteriaOf, resGoal));
        writeModel();
    }
/**
 * Checks if the class name already has suffix "related". If not it adds it.
 * @param rawClassName raw name of the class.
 * @return raw name of the class with suffix "related".
 */
    private String getTestedName(String rawClassName){
        if (!rawClassName.endsWith("related")){
           rawClassName = rawClassName +" related";
        }
    return rawClassName;
    }

    private String getLabel(Resource resClass){
        //gets  the labels of the class: It contains only one label
        NodeIterator iterClassLabel=model.listObjectsOfProperty(resClass, RDFS.label);
        // returns literal value of the property label
        return  iterClassLabel.nextNode().asNode().getLiteral().getLexicalForm();
    }

    private String getDescription(Resource resIndividual){
       // gets property called "description" from the namespace
        Property propDescription = model.getProperty(nameSpace, "description");
       //gets  the descripition of individual: It contains description of only one individual
       NodeIterator nodeIndividualDescription   =   model.listObjectsOfProperty(resIndividual, propDescription);
       //adds the description of individual to the vector.
       return nodeIndividualDescription.nextNode().asNode().getLiteral().getLexicalForm();
    }
    private String getValue(Resource resIndividual){
       // gets property called "value" from the namespace
        Property propValue = model.getProperty(nameSpace, "value");
       //gets  the descripition of individual: It contains description of only one individual
       NodeIterator nodeIndividualValue   =   model.listObjectsOfProperty(resIndividual, propValue);
       //adds the description of individual to the vector.
       return nodeIndividualValue.nextNode().asNode().getLiteral().getLexicalForm();
    }
    
    private String getUnit(Resource resIndividual){
       // gets property called "unit" from the namespace
        Property propUnit = model.getProperty(nameSpace, "unit");
       //gets  the descripition of individual: It contains description of only one individual
       NodeIterator nodeIndividualUnit   =   model.listObjectsOfProperty(resIndividual, propUnit);
       //adds the description of individual to the vector.
       return nodeIndividualUnit.nextNode().asNode().getLiteral().getLexicalForm();
    }
    
    private Vector<OWLData> getVector(String superClassName){
         // gets resource  from the namespace
       Resource resSuperClass =   model.getResource(nameSpace+superClassName);

       //gets all the resources that are sub class of superClass
       ResIterator iterClasses = model.listSubjectsWithProperty(RDFS.subClassOf, resSuperClass);
       dataListAll = new Vector<OWLData>();

       // loops through each resource that is sub class of "Goals".
       while (iterClasses.hasNext()){
          Resource resClass= iterClasses.nextResource();
          dataType =  getLabel(resClass);

           //gets list of individuals(= as a resource) of a class
           ResIterator iterIndividual= model.listSubjectsWithProperty(RDF.type,resClass);

           if (superClassName.equals("Parameters")){
               dataList = getParamList(iterIndividual);
           }else {
               dataList = getDataList(iterIndividual);
           }
           //creates OWLData from goal type and goal list
           owlData = new OWLData(dataType,dataList);

           //adds the OWLData to the goal list all.
           dataListAll.add(owlData);
       }
       return dataListAll;
    }

   /**
     * returns a vector of ParameterData.
     * @param iterIndividual iterator containing individuals
     * @return vector of ParameterData.
     */
    private Vector<ParameterData> getParamList(ResIterator iterIndividual){
       paramList = new Vector<ParameterData>();
           while (iterIndividual.hasNext()){
               Resource resIndividual =  iterIndividual.nextResource();
               String paramName = getDescription(resIndividual);
               String paramValue = getValue(resIndividual);
               String paramUnit = getUnit(resIndividual);
               ParameterData param = new ParameterData(paramName, paramValue, paramUnit);
               paramList.add(param);
           }
       return paramList;
    }

    /**
     * returns a vector of string (description of individual).
     * @param iterIndividual iterator containing individual.
     * @return vector of string.
     */
    private Vector<String> getDataList(ResIterator iterIndividual){
        dataList = new Vector<String>();

           //loops through each individual
           while (iterIndividual.hasNext()){
               Resource resIndividual =  iterIndividual.nextResource();

               //adds the description of individual to the vector.
               dataList.add( getDescription(resIndividual));
           }
        return dataList;
    }

/**
 * Checks if the criterion and goal are related.
 * @param crit string name of the criterion.
 * @param goal string goal of the goal.
 * @return true if they are related else false.
 */
    public boolean isCriteriaOfGoal(String crit, String goal){
        Resource resCrit = this.getExistingResource(crit);
        Resource resGoal = this.getExistingResource(goal);
        return isCriteriaOfGoal(resCrit,resGoal);
    }

    /**
     * Checks if the criterion and goal are related.
     * @param crit Resource of the criterion.
     * @param goal Resource of the criterion.
     * @return true if they are related else false.
     */
    public boolean isCriteriaOfGoal(Resource crit, Resource goal){
        areRelated = false;
        Property propHasCriteria = this.getExistingProperty("hasCriteria");
        NodeIterator iterCriteria = model.listObjectsOfProperty(goal, propHasCriteria);
        while (iterCriteria.hasNext()){
           Resource critA =(Resource) iterCriteria.nextNode();                  // RDFNode is casted to Resource
           if(critA.equals(crit)){
//           if (critA.getLocalName().equals(crit.getLocalName())){
                areRelated = true;
                break;
           }
        }
        return areRelated;
    }

}
