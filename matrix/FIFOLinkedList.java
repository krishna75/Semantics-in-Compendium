/*
 * FIFOLinkedList.java
 *
 * Created on 16 July 2007, 11:04
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

package com.compendium.ui.matrix;

/**
 * This class provides an implementation of the FirstInFirstOut
 * interface class
 *
 * @author Simon Skrzypczak
 */
import java.util.Iterator;

public class FIFOLinkedList implements FirstInFirstOut {
    
    private FIFONode head;
    private FIFONode tail;
    private int count;
    
    /** Creates a new instance of FIFOLinkedList */
    public FIFOLinkedList() {
        head = null;
        tail = null;
        count = 0;
    }
    
//  Add the new element to the back of the queue  
    public void add(Object thisObj) {
        FIFONode temp = new FIFONode(thisObj);
        
        if (count == 0) {
            head = temp;
            tail = temp;
        } else {
            tail.setNext(temp);
            temp.setPrev(tail);
            tail = temp;
        }
        count = count + 1;
    }
//  Return the object at the head of the queue and remove it from the queue
//  Return null if the queue is empty
    public Object remove() {
        Object result = null;
        if (this.getHead() != null) {
            FIFONode temp = head;
            head = head.getNext();
            if (head != null) {
                head.setPrev(null);
            }
            result = temp.getData();
            count = count - 1;
        }
        return result;
    }
    
//  Return an iterator for the queue  
    public Iterator iterator() {
        FIFOIterator fifoIter = new FIFOIterator(this);
        return fifoIter;
    }
    
//  Return the number of elements in the queue  
    public int count() {
        return count;
    }
    
//  Return the head node of the queue  
    public FIFONode getHead() {
        return this.head;
    }
    
    
//  Private class FIFONode supplies the nodes that make up the FIFOLinkedList
//  which is a queue used to hold the elements associated with the queue
    private class FIFONode {
        
        private Object data;
        private FIFONode next;
        private FIFONode prev;
        
//      No arg constructor for a node to be part of the queue  
        public FIFONode() {
            data = "";
            next = null;
            prev = null;
        }
        
//      Constructor for a node to be part of the queue  
        public FIFONode(Object thisData) {
            data = thisData;
            next = null;
            prev = null;
        }
        
//      Sets the data attribute for the FIFONode
        public void setData(Object newData) {
           this.data = newData;
        }
    
//      Returns the data attribute from the FIFONode
        public Object getData() {
            return this.data;
        }
    
//      Sets the reference to the next FIFONode
        public void setNext(FIFONode thisNode) {
            this.next = thisNode;
        }
    
//      Returns a reference to the next FIFONode
        public FIFONode getNext() {
            return this.next;
        }
    
//      Sets the reference to the next FIFONode
        public void setPrev(FIFONode thisNode) {
            this.prev = thisNode;
        }
    
//      Returns a reference to the next FIFONode
        public FIFONode getPrev() {
            return this.prev;
        }        
    }
    
    
//  Private class FIFOIterator is used to iterate over the FIFOLinkedList
    private class FIFOIterator implements Iterator {
        
        private FIFONode cursor;
        
//      Constructor for the iterator for the queue  
        public FIFOIterator(FIFOLinkedList thisList) {
            cursor = new FIFONode();
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
            Object result;
            cursor = cursor.getNext();
            return cursor.getData();
        }
        
        public void remove() {
        }
    }
    
    
}
