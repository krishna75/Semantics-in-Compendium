/*
 * OCData.java
 *
 * Created on 19 July 2007, 15:26
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This is the data type that is used for each element in the
 * Options vs Criteria matrix
 *
 * @author Simon Skrzypczak
 */

import com.compendium.ProjectCompendium;
import javax.swing.*;

import java.util.*;
import java.io.*;
import jxl.*;
import jxl.read.biff.BiffException;
import java.net.*;
import javax.xml.parsers.*;
import org.w3c.dom.*;


public class OCData {
    
    private String value;
    private String formula;


    // Extracting stock information from the web.
       String csvString;
       URL url = null;
       URLConnection urlConn = null;
       InputStreamReader  inStream = null;
       BufferedReader buff = null;

    // Extracting weather information from the web.

          public static String weatherSite = "http://weather.unisys.com/forexml.cgi?";
          public static String observationTagName = "observation";
          public static String unknownAttribute = "?";
    
    /** Creates a new instance of OCData */
//  No-arg constructor for an instance of OCData
    public OCData() {
        value = "";
        formula = "";
    }
    
//  Constructor for OCData with given parameters  
    public OCData(String val, String form) {
        value = val;
        formula = form;
    }
    
//  Set the value
    public void setValue(String val) {
//      Only set the value if the formula attribute is blank to prevent
//      overwriting a formula that already exists with a value
        if (formula.equals("")) {
            value = val;
        }
    }
    
//  Return the value
    public String getValue() {
        return this.value;
    }
    
//  Set the formula and in turn set the value to the result of the formula  
    public void setFormula(String form) {
//      If the formula is blank then the value will also be blank
        if (form.equals("")) {
            formula = form;
            value = form;
//      If the formula is non blank then evaluate it to get the value
        } else {
            formula = form;
            value = evaluateFormula(form);
        }
    }
    
//  Return the formula  
    public String getFormula() {
        return this.formula;
    }
    
//  Evaluate the formula by splitting the expression into operands and
//  operators. Get the value of any references to Global Parameters/Excel files.
//  Validate the expression and evaluate it
    private String evaluateFormula(String form) {
        String result = "";
//      Break the expression down into operands and operators and store it in
//      a queue
        FirstInFirstOut equation = convertStringToEquationParts(form);
//      If expression is valid then convert to postfix and then evaluate
        if (validateEquation(equation)) {
            FirstInFirstOut postFixEqn = eqnToPostFix(equation);
            result = evaluatePostFix(postFixEqn);
        }
//      Present an error message if the result is null
        if (result.equals("null")) {
            JOptionPane.showMessageDialog(null, "The equation returns a null" +
                    "value.\nPlease check it and try again", "Null Result Error"
                    , JOptionPane.ERROR_MESSAGE);
            result = "";
        }
        return result;
    }
    
//  Method to break the formula/expression into its operands and operators and
//  store each part as an element in a queue
    private FirstInFirstOut convertStringToEquationParts(String formula) {
        FirstInFirstOut equation = new FIFOLinkedList();
        String operand = "";
        String operator = "";
        String last = "";
        int count = 0;
        char charNext;
        while (count < formula.length()) {
            charNext = formula.charAt(count);
//          If next character is not a minus sign then simply carry on as normal
//          by either building up the string of characters that represent the
//          number or link to a value, or add the operator to the queue as the
//          next part of the equation.
            if (charNext != '-') {
//              If next character not an operator, space or ' then add the
//              character to the operand string
                if (charNext != '(' && charNext != ')' && charNext != '^' && 
                        charNext != '+' && charNext != '*' && charNext != '/' 
                        && charNext != '\\' && charNext != ' ' && charNext != '\'') { 
                    operand = operand + charNext;
                    count = count + 1;
//              If next character is an operator
                } else if (charNext == '(' || charNext == ')' || charNext == '^' || 
                        charNext == '+' || charNext == '*' || charNext == '/'
                        || charNext == '\\') {
//                  If operand string is not empty then convert the string to a
//                  value and add to the queue
                    if (!operand.equals("")) {
                        equation.add(convertReferencesToValues(operand));
                        last = operand;
                        operand = "";
                    }
//                  Convert a \ to /
                    if (charNext == '\\') {
                        charNext = '/';
                    }
//                  Add the operator to the queue  
                    operator = operator + charNext;
                    equation.add(operator);
//                  Make last equal to the operator
                    last = operator;
//                  Reset the operator to ""
                    operator = "";
                    count = count + 1;
//              If the next character is a '
                } else if (charNext == '\'') {
                    count = count + 1;
                    boolean name = true;
//                  Build up the string using all characters until the next
//                  character encountered is another ' which signals the end of
//                  the reference
                    while (name && count < formula.length()) {
                        charNext = formula.charAt(count);
                        if (charNext != '\''){
                            operand = operand + charNext;
                        } else {
                            name = false;
                        }
                        count = count + 1;
                    }
//              If next character is a space then ignore it
                } else if (charNext == ' ') {
                    operand = operand;
                    count = count + 1;
                }
            }
//          If the next character is a minus sign then need to determine whether
//          it is a unary operator that represents a negative number or whether
//          it is a binary operator that shows a subtraction of one number from
//          another
            else if (charNext == '-') {
//              If a minus sign is encountered then it either means that it is an
//              operator or the start of a negative number. Either way, if the
//              string being built is not empty then it should be written to the
//              queue and 'last' should be given its value so that the minus sign
//              is compared to the correct 'last'. Else it is compared to the
//              item before the current string.
                if (!operand.equals("")) {
                    equation.add(convertReferencesToValues(operand));
                    last = operand;
                    operand = "";
                }
//              If 'last' is an operator then the '-' signifies a
//              negative number. Build the string to represent the number.
                if (last.equals("(") || last.equals("+") || last.equals("-") || 
                        last.equals("*") || last.equals("/") || count == 0) {
                    operand = operand + charNext;
                    count = count + 1;
//              If 'last' is not an operator then the '-' is an
//              operator in this instance.
                } else {
                    operator = operator + charNext;
                    equation.add(operator);
                    last = operator;
                    operator = "";
                    count = count + 1;
                }
            }
        }
//      End of while loop  
        
//      Add the last string to the queue if there is one. The equation may end
//      with a ) so it needs to be checked first. If the operand = "" then
//      there is nothing to add. Otherwise add the last operand to the queue.
        if (!operand.equals("")) {
            equation.add(convertReferencesToValues(operand));
        }
        return equation;
    }
    
//  Convert the strings representing an operand to a value. The string will
//  either be a representation of a number, a global parameter name or a
//  reference to an excel file.
    private String convertReferencesToValues(String value) {
        String result = null;
        boolean convert = false;
//      Try converting the string to a double value
        try {
            double dValue = Double.valueOf(value);
            result = String.valueOf(dValue);
            convert = true;
        }
        catch (NumberFormatException e) {
            convert = false;
        }
//      If not a value then try getting the reference from the global parameters file
        if (!convert) {
            try {
                double gpValue = Double.valueOf(getValueFromGlobalParameterFile(value));
                result = String.valueOf(gpValue);
                convert = true;
            }
            catch (NumberFormatException ee) {
                convert = false;
            }
            catch (NullPointerException n) {
                convert = false;
            }
        }
//      If not a value or global parameter then try getting reference from an excel file
        if (!convert) {
            try {
                double efValue = Double.valueOf(getValueFromExcelFile(value));
                result = String.valueOf(efValue);
                convert = true;
            }
            catch (NumberFormatException eee) {
                convert = false;
            }
            catch (NullPointerException nn) {
                convert = false;
            }
        }
                                                                                      // I could try to put something here to make the string red (WARNING)!!!

//      If none of the above were successful then the result returned is a
//      null value
        return result;
    }
    
//  Validate the equation to ensure that it is balanced and that there are no
//  incorrect references (null values) in the expression
    private boolean validateEquation(FirstInFirstOut equation) {
        
//      Check that the expression is balanced by comparing the number of ( and 
//      the number of ). If same then expression is balanced
        boolean resultBalance = true;
        Iterator parenthesesIter = equation.iterator();
        LastInFirstOut balanceEqn = new LIFOLinkedList();
        while (parenthesesIter.hasNext() && resultBalance) {
            String ss = String.valueOf(parenthesesIter.next());
            char c = ss.charAt(0);
            switch (c) {
                case '(': 
                    balanceEqn.push(c);
                    break;
                case ')':
                    char cPrev = '|';
                    if (balanceEqn.count() != 0) {
                        cPrev = balanceEqn.pop().toString().charAt(0);
                        resultBalance = ((cPrev == '(') && (c ==')'));
                    } else {
                        resultBalance = false;
                    }
                    break;
                
            }
        }
//      If expression not balanced display warning message to show this
        if (!(balanceEqn.count() == 0 && resultBalance)) {
            resultBalance = false;
            JOptionPane.showMessageDialog(null, "The equation is not balanced.\n" +
                    "Please check the parentheses.", "Expression Balance Error",
                    JOptionPane.WARNING_MESSAGE);
        }
        
//      Check whether there are any null values in the expression. If there are then
//      the expression contains incorrect references that need to be checked out
        boolean resultNull = true;
        Iterator checkNullIter = equation.iterator();
        while (checkNullIter.hasNext() && resultNull) {
//          Can't use toString here because if it is null then it will cause the
//          program to crash. Therefore use the String.valueOf() instead
//          This will return a string equal to "null" if the object is null
            String isNullValue = String.valueOf(checkNullIter.next());
            if (isNullValue.equals("null")) {


                resultNull = false;
                JOptionPane.showMessageDialog(null, "There are incorrect references in the " +
                        "equation entered.\nPlease check the references and try again.",
                        "Reference Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        boolean result = resultBalance && resultNull;
        return result;
    }
    
//  Convert the expression from infix to postfix notation using the Shunting Yard
//  Algorithm
    private FirstInFirstOut eqnToPostFix(FirstInFirstOut equation) {
        FirstInFirstOut postfix = new FIFOLinkedList();
        LastInFirstOut opStack = new LIFOLinkedList();
        
        Iterator thisEquation = equation.iterator();
        while (thisEquation.hasNext()) {
            String element = (String)thisEquation.next();
//          If the element is not an operator add it to the postfix queue
            if (!element.equals("(") && !element.equals(")") && !element.equals("^") && 
                    !element.equals("*") && !element.equals("/") && !element.equals("+") && 
                    !element.equals("-")) {
                
                postfix.add(element);
            }
//          If the element is an operator
            else if (element.equals("^") || element.equals("*") || element.equals("/") || 
                    element.equals("+") || element.equals("-")) {
//                If operator stack is empty then add the operator to the stack
                if (opStack.count() == 0) {
                    opStack.push(element);
                } else {
//                  If the operator has lower or equal precedence to the
//                  operator at the top of the stack, pop the operator from the
//                  stack to the postfix queue. Continue this until the operator
//                  is added to the stack
                    Iterator opStackIter = opStack.iterator();
                    boolean cont = true;
                    while (opStackIter.hasNext() && cont) {
                        String opStElement = (String)opStackIter.next();
                        
                        int elementVal = 0;
                        int opStackVal = 0;
                        
                        if (element.equals("^")) {
                            elementVal = 5;
                        } else if (element.equals("*")) {
                            elementVal = 4;
                        } else if (element.equals("/")) {
                            elementVal = 3;
                        } else if (element.equals("+")) {
                            elementVal = 2;
                        } else if (element.equals("-")) {
                            elementVal = 1;
                        }
                        
                        if (opStElement.equals("^")) {
                            opStackVal = 5;
                        } else if (opStElement.equals("*")) {
                            opStackVal = 4;
                        } else if (opStElement.equals("/")) {
                            opStackVal = 3;
                        } else if (opStElement.equals("+")) {
                            opStackVal = 2;
                        } else if (opStElement.equals("-")) {
                            opStackVal = 1;
                        }
                        
                        if (elementVal <= opStackVal) {
                            postfix.add(opStack.pop());
                        } else {                            
                            cont = false;
                        }
                            
                    }
                    opStack.push(element);
                }
            }
            else if (element.equals("(")) {
                opStack.push(element);
            }
//          If element is ) then pop stack to the postfix queue until ( is found
//          Discard the (
            else if (element.equals(")")) {
                String stackElement = (String)opStack.pop();
                while (!stackElement.equals("(")) {
                    postfix.add(stackElement);
                    stackElement = (String)opStack.pop();
                }
            }
            
        }
//      Pop any remaining operators from the stack to the postfix queue
        while (opStack.count() != 0) {
            postfix.add(opStack.pop());
        }
        return postfix;
    }
    
//  Evaluate the postfix expression
    private String evaluatePostFix(FirstInFirstOut postfix) {
        Iterator postfixIter = postfix.iterator();
        LastInFirstOut ansStack = new LIFOLinkedList();
//      While there are elements in the postfix queue
        while (postfixIter.hasNext()) {
            String element = (String)postfixIter.next();
            if (!element.equals("^") && !element.equals("/") && !element.equals("*")
            && !element.equals("+") && !element.equals("-")) {
                ansStack.push(element);
//          If next element is an operator pop the top two values from the stack
//          and apply the operator to them. Push the result back to the stack
            } else {
                String operA = (String)ansStack.pop();
                double operandA = Double.valueOf(operA);
                String operB = (String)ansStack.pop();
                double operandB = Double.valueOf(operB);
                double res = 0;
                if (element.equals("*")) {
                    res = operandB * operandA;
                } else if (element.equals("/")) {
                    res = operandB / operandA;
                } else if (element.equals("+")) {
                    res = operandB + operandA;
                } else if (element.equals("-")) {
                    res = operandB - operandA;
                } else if (element.equals("^")) {
                    res = Math.pow(operandB, operandA);
                }
                String res1 = String.valueOf(res);
                ansStack.push(res1);
            }
        }
//      Return the top element of the stack as the result
//      Use String.valueOf() to avoid problems with null values
        return String.valueOf(ansStack.pop());
    }
    
//  Get a reference from the Global Parameters file
    private String getValueFromGlobalParameterFile(String value) {
        String result = null;
//      Use the ProjectCompendium class to get the homepath of the installation
//      as this may vary depending on the platform and the way the user installed it
        String homePath = ProjectCompendium.sHOMEPATH;
        String pathName = homePath + "/System/resources/Project Files/";
        String projectName = ProjectCompendium.APP.getProjectName();
        String fileName = pathName + projectName + "/GlobalParameters.csv";
        File file = new File(fileName);

        if (file.exists()) {
            Scanner lineFromFile = null;

            try {
                lineFromFile = new Scanner(file);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
            boolean found = false;
//          Search through the file to find the parameter that matches the
//          String value passed to the method
            while (lineFromFile.hasNextLine() && !found) {
                String line = lineFromFile.nextLine();
                Scanner readLine = new Scanner(line).useDelimiter(",");
                String paramName = readLine.next();
//              If parameter found set result to be the value of the parameter






                if (paramName.equals(value)) {
                    String result0 = readLine.next();

        boolean convert = false;

        if (result0.charAt(0) == 'W'){

            if (!convert) {

            try {

                double efValue = Double.valueOf(weather(result0));
                result = String.valueOf(efValue);
                convert = true;
            }
            catch (NumberFormatException eeee) {
                convert = false;
            }
            catch (NullPointerException nnnn) {
                convert = false;
           }
            }
        }
            else{
        if (!convert) {
//      Try converting the string to a double value
        try {
            double dValue = Double.valueOf(result0);
            result = String.valueOf(dValue);
            convert = true;
        }
        catch (NumberFormatException e) {
            convert = false;
        }

//      If not a value or global parameter then try getting reference from an excel file




        if (!convert) {
            try {

                double efValue = Double.valueOf(getValueFromExcelFile(result0));
                result = String.valueOf(efValue);
                convert = true;


            }
            catch (NumberFormatException ee) {
                convert = false;
            }
            catch (NullPointerException nn) {
                convert = false;
            }
        if (!convert) {
            try {
                String names = getInformationFromYahoo(result0);
                StringTokenizer st = new StringTokenizer(names, ",");

                String ticker = (st.nextToken());
                String price = (st.nextToken());
                String tradeDate = (st.nextToken());
                String tradeTime = (st.nextToken());



                double efValue = Double.valueOf(price);
                result = String.valueOf(efValue);
                convert = true;

                 JOptionPane.showMessageDialog(null, "The value from " + ticker +
                " is $ " + price + ". Taken at " + tradeTime + " on the " + tradeDate + " (US time)" , "Price details",
                JOptionPane.WARNING_MESSAGE);

            }

            catch (NumberFormatException eee) {
                convert = false;
            }
            catch (NullPointerException nnn) {
                convert = false;
            }

            }
            }
        }
        }

             found = true;
        }
        
        
        else {
            result = null;
        }
        }
        }
        return result;
       
    }
    
//  Get a reference from an external excel file
    private String getValueFromExcelFile(String value) {
        String result = null;
        
        int valueLength = value.length();
        int count = 0;
        String path = "";
        String sheet = "";
        String cellAt = "";
        String thisPart = "";
        FirstInFirstOut excelPath = new FIFOLinkedList();
//      Split the String 'value' into three parts separated by ;
//      This will provide the file location, sheet name and cell location to
//      get the reference from
        char charNext;
        while (count < valueLength) {
            charNext = value.charAt(count);
//          ; is the delimiter between the parts of the excel reference
            if (charNext != ';') {
                thisPart = thisPart + charNext;
                count = count + 1;
            } else {
                excelPath.add(thisPart);
                thisPart = "";
                count = count + 1;
            }
        }
        if (!thisPart.equals("")) {
            excelPath.add(thisPart);
        }
        
//      Only proceed if the correct number of parts have been found in the reference
        if (excelPath.count() == 3) {
            Iterator excPathIter = excelPath.iterator();
            String[] filePathComponent = new String[3];
            int noOfComp = 0;
            while (excPathIter.hasNext()) {
                filePathComponent[noOfComp] = String.valueOf(excPathIter.next());
                noOfComp = noOfComp + 1;
            }
//          Use first part of the reference to access the excel file
            File file = new File(filePathComponent[0]);
            if (file.exists()) {
                Workbook workbook = null;
                try {
                    workbook = Workbook.getWorkbook(file);
                } catch (BiffException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
//              This code allows for the worksheet to be entered either as an integer
//              representation of the worksheet from 0 upwards or to be entered as
//              the string name of the worksheet i.e. Sheet1
                Sheet worksheet;
                try {
//                  Use second part of the reference to access the worksheet
                    int intSheet = Integer.valueOf(filePathComponent[1]);
                    worksheet = workbook.getSheet(intSheet);
                }
                catch (NumberFormatException e) {
                    worksheet = workbook.getSheet(filePathComponent[1]);
                }
//              Use the third part of the reference to access the cell
                Cell cell = worksheet.getCell(filePathComponent[2]);
                result = cell.getContents();
            } else {
                result = null;
            }
            
        } else {
            result = null;
        }
        return result;
    }

     public String getInformationFromYahoo(String stock){
      String result = null;
       try{
           url  = new
               URL("http://quote.yahoo.com/d/quotes.csv?s=" + stock + "&f=sl1d1t1c1ohgv&e=.csv" );
           urlConn = url.openConnection();
           inStream = new
               InputStreamReader(urlConn.getInputStream());
           BufferedReader buff= new BufferedReader(inStream);

           // get the quote as a csv string
           csvString =buff.readLine();

           // parse the csv string
              StringTokenizer tokenizer = new
                          StringTokenizer(csvString, ",");
              String ticker = tokenizer.nextToken();
              String price  = tokenizer.nextToken();
              String tradeDate = tokenizer.nextToken();
              String tradeTime = tokenizer.nextToken();

              result = ticker + "," + price + "," + tradeDate + "," + tradeTime;
       }
      catch(MalformedURLException e){

      }
      catch(IOException  e1){

     }

     
     return result;
   }

   public String weather(String result0){
       String result = null;

       StringTokenizer st = new StringTokenizer(result0, ":");

       String nothing = (st.nextToken());
       
       String city = (st.nextToken());
       String attrNames = (st.nextToken());


       result = processDocument(city, observationTagName, attrNames);

       return result;
   }


   public String processDocument( String city, String elementName, String attrNames ) {
       String result = null;
      DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
      try {
         DocumentBuilder db = dbf.newDocumentBuilder();


            String urlString = weatherSite + city;

            Document document = getDocument( db, urlString );

               Node [] matchNodes = getAttributes( document, elementName, attrNames );

             result = printNodes( "City=" + city, matchNodes, true );

         }
         catch ( ParserConfigurationException e ) {
         e.printStackTrace();
      }

      return result;
   } // processDocument

   /**
    * This method gets an XML document from the given URL.
    */
   public static Document getDocument( DocumentBuilder db, String urlString ) {
      try {
         URL url = new URL( urlString );
         try {
            URLConnection URLconnection = url.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection) URLconnection;
            int responseCode = httpConnection.getResponseCode();
            if ( responseCode == HttpURLConnection.HTTP_OK) {
               InputStream in = httpConnection.getInputStream();
               try {
                  Document doc = db.parse( in );
                  return doc;
               } catch( org.xml.sax.SAXException e ) {
                  e.printStackTrace();
               }
            } else
               System.out.println( "HTTP connection response != HTTP_OK" );
         } catch ( IOException e ) {
            e.printStackTrace();
         } // catch
      } catch ( MalformedURLException e ) {
         e.printStackTrace();
      } // catch
      return null;
   } // getDocument

   /**
    * Given an XML document, this method gets the named element
    * and gets Nodes matching the given attribute names.
    * Attribute names may include "all" or actual names.
    * Use getNodeName and getNodeValue to see name and value.
    */
   public static Node [] getAttributes( Document document, String elementName, String attrNames ) {

      // Get elements with the given tag name (matches on * too).
      NodeList nodes = document.getElementsByTagName( elementName );
      if ( nodes.getLength() < 1) {
         // System.out.println( "getAttributeValues, elementName=\"" + elementName + ", no element tags by this name." );
         return null;
      }

      Node firstElement = nodes.item( 0 );
      NamedNodeMap nnm = firstElement.getAttributes();
      if (nnm != null) {
         // Report the value of each attribute.
         Node [] matchNodes = new Node[ 1 ];

               matchNodes[ 0 ] = nnm.getNamedItem( attrNames );


         return matchNodes;
      }
      // else System.out.println( "getAttributeValues, elementName=\"" + elementName + ", attributes for this element are null." );
         return null;
   } // printDocumentAttrs
   /**
    * Prints a list of node names and values.
    * @param title is a descriptive title for the set.
    * @param grouped determines whether to print on one line or not.
    */
   public static String printNodes( String title, Node [] nodes, boolean grouped ) {



         Node node = nodes[ 0 ];

        String output = ( node.getNodeValue() );


      return output;

   }
            public Class getColumnClass(int col) {
            return Double.class;
        }
}

