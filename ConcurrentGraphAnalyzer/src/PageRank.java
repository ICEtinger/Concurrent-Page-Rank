
/*******************

Created by Isak C. Etinger.

Last updated April 2017.

This class provides a Runnable that uses the Page Rank algorithm to calculate centralities of Nodes in a graph.
It receives a collection containing all the Nodes that should have their centralities calculated and one of the 6 types of centrality
specified at centralityType.java to calculate (namely Standard, Reversed, Neighbor, and their versions with unweighted edges).

It distributes the collection of Nodes received into subarrays and calls the Runnable in Iterate.java class to each one of them iterate over
their respective subarray of Nodes. It then periodically measures the mean relative improvement on the centrality (since the last time it 
was checked) over all nodes. And if that measure is below a specified value for a specified number of consecutive assessments, it causes
the running Iterate threads to stop. This is due to the convergence of the centrality calculated at each iteration of the Page Rank algorithm.

*******************/

import java.util.ArrayList;
import java.util.HashMap;

public class PageRank implements Runnable{
	
	HashMap<String,Node> dic;
	long timeBetweenAssessments;
	int totalNumberOfEdges;
	int numberOfConcurrentThreads;
	int numberOfIterations;
	int consecutiveMinimalImprovementsOnCentralityToStop;
	float minimalImprovementOnCentralityToStop;
	float DampingFactor;
	float neighborCentralityDampingFactor;
	float neighborCentralityBias;
	boolean considerWeightedEdgesForComplexity;
	String name; //the name of the instance of PageRank to be printed in the Console during iterations.
	centralityType type;
	
	PageRank (centralityType type, HashMap<String,Node> dic, long timeBetweenAssessments, int totalNumberOfEdges, int numberOfConcurrentThreads, 
			int numberOfIterations, int consecutiveMinimalImprovementsOnCentralityToStop, float minimalImprovementOnCentralityToStop, 
			float DampingFactor, float neighborCentralityDampingFactor, float neighborCentralityBias, 
			boolean considerWeightedEdgesForComplexity, String name) {
		
		this.type = type;
		this.dic = dic;
		this.timeBetweenAssessments = timeBetweenAssessments;
		this.totalNumberOfEdges = totalNumberOfEdges;
		this.numberOfConcurrentThreads = numberOfConcurrentThreads;
		this.numberOfIterations = numberOfIterations;
		this.consecutiveMinimalImprovementsOnCentralityToStop = consecutiveMinimalImprovementsOnCentralityToStop;
		this.minimalImprovementOnCentralityToStop = minimalImprovementOnCentralityToStop;
		this.DampingFactor = DampingFactor;
		this.considerWeightedEdgesForComplexity = considerWeightedEdgesForComplexity;
		this.name = name;
	}
	
	// without the name of the instance of PageRank to be printed in the Console during iterations.
	PageRank (HashMap<String,Node> dic, long timeBetweenAssessments, int totalNumberOfEdges, int numberOfConcurrentThreads, 
			int numberOfIterations, int consecutiveMinimalImprovementsOnCentralityToStop, float minimalImprovementOnCentralityToStop, 
			float DampingFactor,  float neighborCentralityDampingFactor, float neighborCentralityBias, boolean considerWeightedEdgesForComplexity) {
		this.dic = dic;
		this.timeBetweenAssessments = timeBetweenAssessments;
		this.totalNumberOfEdges = totalNumberOfEdges;
		this.numberOfConcurrentThreads = numberOfConcurrentThreads;
		this.numberOfIterations = numberOfIterations;
		this.consecutiveMinimalImprovementsOnCentralityToStop = consecutiveMinimalImprovementsOnCentralityToStop;
		this.minimalImprovementOnCentralityToStop = minimalImprovementOnCentralityToStop;
		this.DampingFactor = DampingFactor;
		this.considerWeightedEdgesForComplexity = considerWeightedEdgesForComplexity;
		this.name = "";
	}
	
	@Override
	public void run() {
		// if STOP[] == true, the threads created should stop running.
    	boolean[] STOP = {false};
        
    	// transforms the collection of addresses into an array to distribute them to threads.
		ArrayList<Node> arrayOfNodes = new ArrayList<Node>(dic.values());
    	
    	if (totalNumberOfEdges == 1) {
    		new Thread(new Iterate(arrayOfNodes, 0, arrayOfNodes.size(), numberOfIterations, DampingFactor, 
    				neighborCentralityDampingFactor, neighborCentralityBias, STOP, totalNumberOfEdges, type)).start();
    	}
    	else {
    		// aims at distributing nodes to threads so all of them have nearly same number of edges in their nodes, 
    		// so they should take nearly the same amount of time to run.
    		int NumberOfEdgesPerThread = totalNumberOfEdges/numberOfConcurrentThreads;
        
    		// distributes the addresses to threads to have their centrality calculated through multiple iterations.
    		int edgesInThread = 0;
    		int beginIndex = 0;
    		for ( int i = 0; i < arrayOfNodes.size(); i++ ) {
    			edgesInThread += arrayOfNodes.get(i).fromNodes.size();
    			if( edgesInThread >= NumberOfEdgesPerThread ) {
    				new Thread(new Iterate(arrayOfNodes, beginIndex, i, numberOfIterations, DampingFactor, neighborCentralityDampingFactor, 
    						neighborCentralityBias, STOP, totalNumberOfEdges, type)).start();
    				beginIndex = i;
    				edgesInThread = 0;
    			}
    		}
    		new Thread(new Iterate(arrayOfNodes, beginIndex, arrayOfNodes.size(), numberOfIterations, DampingFactor, 
    				neighborCentralityDampingFactor, neighborCentralityBias, STOP, totalNumberOfEdges, type)).start();
    	}
    	
        System.out.println(name+" Thread) running and reporting. Waiting "+timeBetweenAssessments+" miliseconds between each assessment.");
        
        // The program will stop running if it has for n consecutive assessments all improvements on centrality lower or equal to x,
    	// where n = consecutiveMinimalImprovementsOnCentralityToStop, and x = minimalImprovementOnCentralityToStop.
        int numberOfConsecutiveMinimalImprovements = 0;
        int cont = 0;
        while(numberOfConsecutiveMinimalImprovements < consecutiveMinimalImprovementsOnCentralityToStop) {
        	try {
				Thread.sleep(timeBetweenAssessments);
			} catch (InterruptedException e) { e.printStackTrace(); }
        	cont++;
        	
        	// lastImprovement measures the mean relative improvement on the centrality from the last time it was checked.
        	// e.g.: if all nodes go from centrality = 0.5 to centrality = 0.6, then lastImprovement is equal to 0.2.
        	float lastImprovement = 0;
        	for ( Node address : arrayOfNodes ) {
        		lastImprovement += address.getLastImprovement(type);
        	}
        	lastImprovement /= arrayOfNodes.size(); 
        	
        	System.out.println(name+")"+cont+") Mean relative improvement on centrality since last assessment: "+lastImprovement);
        	
        	if (lastImprovement <= minimalImprovementOnCentralityToStop)
        		numberOfConsecutiveMinimalImprovements++;
        	else
        		numberOfConsecutiveMinimalImprovements = 0;
        }
        // stops all running threads.
        STOP[0] = true;
	}
	
	
}
