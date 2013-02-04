/*
 * LIFOLinkedList.java
 *
 * Created on 16 July 2007, 11:05
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This class provides an implementation of the LastInFirstOut
 * interface class
 *
 * @author Simon Skrzypczak
 */
import java.util.Iterator;

public class LIFOLinkedList implements LastInFirstOut {
    
    private LIFONode head;
    private int count;
    
    /** Creates a new instance of LIFOLinkedList */
    public LIFOLinkedList() {
        head = null;
    }
    
//  Add an element to the stack at the head of the stack  
    public void push(Object thisObj) {
        LIFONode temp = new LIFONode(thisObj);
        if (this.getHead() == null) {
            head = temp;
        } else {
            temp.setNext(head);
            head = temp;
        }
        count = count + 1;
    }
    
//  Return the top object from the stack and remove it from the stack
//  Return null if the stack is empty   
    public Object pop() {
        Object result;
        if (this.getHead() == null) {
            result = null;
        } else {
            LIFONode current = head;
            head = head.getNext();
            result = current.getData();
            count = count - 1;
        }        
        return result;
    }
    
//  Return an iterator for the stack  
    public Iterator iterator() {
        LIFOIterator lifoIter = new LIFOIterator(this);
        return lifoIter;
    }
    
//  Return the number of elements in the stack  
    public int count() {
        return this.count;
    }
    
//  Return the head node of the stack  
    public LIFONode getHead() {
        return this.head;
    }
    
    
//  Private class LIFONode supplies the nodes that make up the LIFOLinkedList
//  which is a stack used to hold the elements associated with the stack
    private class LIFONode {
        
        private LIFONode next;
        private Object data;
        
//      No arg constructor for a node to be part of the queue  
        public LIFONode() {
            data = "";
            next = null;
        }
        
//      Constructor for a node to be part of the queue  
        public LIFONode(Object thisData) {
            data = thisData;
            next = null;
        }
        
//      Sets the data attribute for the LIFONode
        public void setData(Object newData) {
            this.data = newData;
        }
        
//      Returns the data attribute from the LIFONode
        public Object getData() {
            return this.data;
        }
        
//      Sets the reference to the next LIFONode
        public void setNext(LIFONode thisNode) {
            this.next = thisNode;
        }
        
//      Returns a reference to the next LIFONode
        public LIFONode getNext() {
            return this.next;
        }
    }
    
//  Private class LIFOIterator is used to iterate over the LIFOLinkedList    
    private class LIFOIterator implements Iterator {
        
        private LIFONode cursor;
        
//      Constructor for the iterator for the queue  
        public LIFOIterator(LIFOLinkedList thisList) {
            cursor = new LIFONode();
            cursor.setNext(thisList.getHead());
        }
        
//      Check if there is another element in the queue  
        public boolean hasNext() {
            boolean result = false;
            if (cursor.getNext() != null) {
                result = true;
            }
            return result;
        }
        
//      Return the next element in the queue  
        public Object next() {
            cursor = cursor.getNext();
            return cursor.getData();
        }
        
        public void remove() {
        }
    }
    
}
