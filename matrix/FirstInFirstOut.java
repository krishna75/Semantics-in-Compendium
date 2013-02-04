/*
 * FirstInFirstOut.java
 *
 * Created on 16 March 2007, 09:20
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
public interface FirstInFirstOut {
    
//  PRE  A queue has been instantiated
//  POST If queue empty thisObj becomes the only object in the queue
//       else the object is added to the back of the queue
    public void add(Object thisObj);
    
//  PRE  A queue has been instantiated
//  POST If queue is empty return null else
//       return the reference to the object at the front of the queue
//       and remove it from the queue
    public Object remove();
    
//  PRE  A queue has been instantiated
//  POST An iterator is created and the reference to it is returned
    public Iterator iterator();
    
//  PRE  A queue has been instantiated
//  POST Returns the count of number of elements in the queue
    public int count();
}
