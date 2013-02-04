/*
 * LastInFirstOut.java
 *
 * Created on 14 March 2007, 11:16
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This interface class defines the methods that should be implemented
 * by any class which implements the interface
 *
 * @author Simon Skrzypczak
 */

import java.util.Iterator;

// This interface class specifies what methods should be included in any
// class which implements the interface and specifies how each method 
// should function
public interface LastInFirstOut {

//  PRE  A stack has been instantiated
//  POST If stack empty then thisObj becomes the only element in the stack
//       else if not empty then thisObj is inserted at the head of the stack
    public void push(Object thisObj);
    
//  PRE  A stack has been instantiated
//  POST If stack empty return null else
//       return the reference at the top of the stack and remove the element
//       from the stack
    public Object pop();
    
//  PRE  A stack has been instantiated
//  POST An iterator is created and the reference to it is returned
    public Iterator iterator();
    
//  PRE  A stack has been instantiated
//  POST Return the number of objects in the stack
    public int count();
}
