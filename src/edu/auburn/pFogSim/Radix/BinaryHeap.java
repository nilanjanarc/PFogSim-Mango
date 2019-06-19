package edu.auburn.pFogSim.Radix;


import java.util.Scanner;

import org.apache.commons.math3.util.Pair;

import edu.auburn.pFogSim.netsim.ESBModel;
import edu.auburn.pFogSim.util.DataInterpreter;
import edu.boun.edgecloudsim.core.SimManager;
import edu.boun.edgecloudsim.edge_server.EdgeHost;
import edu.boun.edgecloudsim.utils.Location;
import edu.boun.edgecloudsim.utils.SimLogger;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.NoSuchElementException;



public class BinaryHeap {
	
	public class BinaryHeapElement{
		public Double distance, latency;
		public EdgeHost edgeHost;
	}
	
	public enum HeapChoice {Distance, Latency};
	
	/** The number of children each node has **/
    private static final int d = 2;
    private int heapSize;
    private BinaryHeapElement[] distanceHeap;
    private BinaryHeapElement[] latencyHeap;
    private ArrayList<EdgeHost> nodes;
    private Location ref;
 
    /** Constructor **/    
    public BinaryHeap(int capacity, Location _ref, ArrayList<EdgeHost> in)
    {
        heapSize = 0;
        distanceHeap = new BinaryHeapElement[capacity+1];
        latencyHeap = new BinaryHeapElement[capacity+1];
        
        nodes = in;
    }
    
    private void init() {
    	for (EdgeHost node : nodes) {
			this.insert(node);
		}
    }
 
    /** Function to check if heap is empty **/
    public boolean isEmpty( )
    {
        return heapSize == 0;
    }
 
    /** Check if heap is full **/
    public boolean isFull( )
    {
        return heapSize == distanceHeap.length;
    }
 
    /** Clear heap */
    public void makeEmpty( )
    {
        heapSize = 0;
    }
 
    /** Function to  get index parent of i **/
    private int parent(int i) 
    {
        return (i - 1)/d;
    }
 
    /** Function to get index of k th child of i **/
    private int kthChild(int i, int k) 
    {
        return d * i + k;
    }
 
    /** Function to insert element */
    public void insert(EdgeHost x)
    {
    	
    	BinaryHeapElement e = new BinaryHeapElement();
    	
		Location l = new Location(x.getLocation().getXPos(), x.getLocation().getYPos(), x.getLocation().getAltitude());
		e.distance = DataInterpreter.measure(ref.getXPos(), ref.getYPos(), ref.getAltitude(), l.getXPos(), l.getYPos(), l.getAltitude());
		e.latency = ((ESBModel)SimManager.getInstance().getNetworkModel()).getDleay(ref, l);

    	
    	
        if (isFull( ) )
            throw new NoSuchElementException("Overflow Exception");
        /** Percolate up **/
        distanceHeap[heapSize] = e;
        latencyHeap[heapSize++] = e;
        heapifyUp(HeapChoice.Distance, heapSize - 1);
        heapifyUp(HeapChoice.Latency, heapSize - 1);
    }
 
    /** Function to find least element 
     * @throws Exception **/
	public EdgeHost findMin( HeapChoice mode ) throws Exception
    {
        if (isEmpty() )
            throw new NoSuchElementException("Underflow Exception");           
        if (mode == HeapChoice.Distance) {
			return distanceHeap[0].edgeHost;
		}else if (mode == HeapChoice.Latency) {
			return latencyHeap[0].edgeHost;
		}else {
			throw new Exception();
		}
    }
 
    /** Function heapifyUp  **/
    private void heapifyUp(HeapChoice mode, int childInd)
    {
    	BinaryHeapElement tmp;
    	
    	switch (mode) {
		case Distance:
			tmp = distanceHeap[childInd];    
	        while (childInd > 0 && tmp.distance < distanceHeap[parent(childInd)].distance)
	        {
	        	distanceHeap[childInd] = distanceHeap[ parent(childInd) ];
	            childInd = parent(childInd);
	        }                   
	        distanceHeap[childInd] = tmp;
			break;
		case Latency:
			tmp = latencyHeap[childInd];    
	        while (childInd > 0 && tmp.latency < latencyHeap[parent(childInd)].latency)
	        {
	        	latencyHeap[childInd] = latencyHeap[ parent(childInd) ];
	            childInd = parent(childInd);
	        }                   
	        latencyHeap[childInd] = tmp;
			break;
		default:
			break;
		}
        
    }
 
    /** Function heapifyDown **/
    private void heapifyDown(HeapChoice mode, int ind)
    {
    	int child;
    	BinaryHeapElement tmp;
    	switch (mode){
		case Distance:
	        tmp = distanceHeap[ ind ];
	        while (kthChild(ind, 1) < heapSize)
	        {
	            child = minChild(mode, ind);
	            if (distanceHeap[child].distance < tmp.distance)
	            	distanceHeap[ind] = distanceHeap[child];
	            else
	                break;
	            ind = child;
	        }
	        distanceHeap[ind] = tmp;
			break;
		
		case Latency:
	        tmp = latencyHeap[ ind ];
	        while (kthChild(ind, 1) < heapSize)
	        {
	            child = minChild(mode, ind);
	            if (latencyHeap[child].latency < tmp.latency)
	            	latencyHeap[ind] = latencyHeap[child];
	            else
	                break;
	            ind = child;
	        }
	        latencyHeap[ind] = tmp;
			break;		
		default:
			break;
		}
        
    }
 
    /** Function to get smallest child **/
    private int minChild(HeapChoice mode, int ind) 
    {
    	int bestChild, k, pos;
    	switch (mode) {
		case Distance:
			bestChild = kthChild(ind, 1);
	        k = 2;
	        pos = kthChild(ind, k);
	        while ((k <= d) && (pos < heapSize)) 
	        {
	            if (latencyHeap[pos].latency < latencyHeap[bestChild].latency) 
	                bestChild = pos;
	            pos = kthChild(ind, k++);
	        }    
	        return bestChild;
		case Latency:
			bestChild = kthChild(ind, 1);
	        k = 2;
	        pos = kthChild(ind, k);
	        while ((k <= d) && (pos < heapSize)) 
	        {
	            if (latencyHeap[pos].latency < latencyHeap[bestChild].latency) 
	                bestChild = pos;
	            pos = kthChild(ind, k++);
	        }    
	        return bestChild;
		default:
			return -1;
		}
        
    }
 
    /** Function to print heap **/
    public void printHeap(HeapChoice mode)
    {
    	switch (mode) {
		case Distance:
			System.out.print("\nHeap = ");
	        for (int i = 0; i < heapSize; i++)
	            System.out.print(distanceHeap[i] +" ");
	        System.out.println();
			break;
		case Latency:
			System.out.print("\nHeap = ");
	        for (int i = 0; i < heapSize; i++)
	            System.out.print(latencyHeap[i] +" ");
	        System.out.println();
			break;

		default:
			break;
		}
        
    }     
}
