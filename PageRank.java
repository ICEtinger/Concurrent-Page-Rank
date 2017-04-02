
/* Created by Isak Czeresnia Etinger */

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class PageRank {

	// PARAMETERS ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
	// if true: centrality(node p) = (1-d)/number of edges + d*sum[for each q pointing to p](centrality of q * weight of edge(q->p) / weighted out-degree of q).
	// if false: centrality(node p) = (1-d)/number of nodes + d*sum[for each q pointing to p](centrality of q / out-degree of q).
	private static final boolean considerWeightedEdgesForComplexity = true;	
	
	// The initial centrality used in the Page Rank algorithm.
	private static final float InitialCentrality = 1;
	// Damping factor d used in the Page Rank algorithm.
	private static final float DampingFactor = (float) 0.85;
	
	// Time taken between each assessment on the average relative improvement on centrality since last assessment.
	private static final long timeBetweenAssessments = 100;
	// The program will stop running if it has for n consecutive assessments all improvements on centrality lower or equal to x,
	// where n = consecutiveMinimalImprovementsOnCentralityToStop, and x = minimalImprovementOnCentralityToStop.
	private static final float minimalImprovementOnCentralityToStop = (float)0.0;
	private static final int consecutiveMinimalImprovementsOnCentralityToStop = 10;
	
	// The number of concurrent threads running simultaneously in the Page Rank algorithm.
	private static final int numberOfConcurrentThreads = 10;
	// The number of iterations to be executed by each thread. Set to negative for an infinite loop of iterations.
	private static final int numberOfIterations = -1;

	// Whether or not to run tests to check if the graph was properly built. 
	private static final boolean runTests = true;
	
	// Whether or not to sort the collection of nodes by centrality before writing the file.
	private static final boolean sortCollectionOfNodesByCentrality = true;
	
	// Whether or not to write the respective values in the file.
	private static final boolean writeCentrality = true;
	private static final boolean writeInDegree = true;
	private static final boolean writeOutDegree = true;
	private static final boolean writeFromNodes = false;
	private static final boolean writeToNodes = false;
	
	// MAIN FUNCTION ////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	
    public static void main(String[] args) throws InterruptedException {
    	
    	// prints the parameters used at running the program.
    	System.out.println("Running program with: initial centrality = "+InitialCentrality+
    			           ", damping factor = "+DampingFactor+
    			           ", number of concurrent threads = "+numberOfConcurrentThreads+
    			           ", number of iterations = "+(numberOfIterations >= 0 ? numberOfIterations : "infinite")+
    			           (considerWeightedEdgesForComplexity ? ", considering edge weights for complexity evaluation":", using unweighted edges for complexity evaluation")+
    			           (runTests ? ", running safety tests on graph-building." : ".") );
    	
    	// BUILDS GRAPH FROM FILE ////////////////////////////////////////////////////////////////////////////////////////////////////////
    	
    	HashMap<String,Node> dic = new HashMap<String,Node>();
    	int[] tmp = GraphBuilder.BuildGraphFromCSV(dic, InitialCentrality, runTests);
    	// gets information about the graph's structure.
    	int LineCounter = tmp[0], EmailCounter = tmp[1], totalNumberOfEdges = tmp[2];        
    	
        // STARTS ANALYZING THE GRAPH ////////////////////////////////////////////////////////////////////////////////////////////////////
        
        // if STOP[] == true, the threads created should stop running.
    	boolean[] STOP = {false};
        
        // aims at distributing nodes to threads so all of them have nearly same number of edges in their nodes, 
        // so they should take nearly the same amount of time to run.
        int NumberOfEdgesPerThread = totalNumberOfEdges/numberOfConcurrentThreads;
        
        // transforms the collection of addresses into an array to distribute them to threads.
        ArrayList<Node> arrayOfNodes = new ArrayList<Node>(dic.values());
        
        // distributes the addresses to threads to have their centrality calculated through multiple iterations.
        int edgesInThread = 0;
        int beginIndex = 0;
        for ( int i = 0; i < arrayOfNodes.size(); i++ ) {
        	edgesInThread += arrayOfNodes.get(i).fromNodes.size();
        	if( edgesInThread >= NumberOfEdgesPerThread ) {
        		new Thread(new Iterate(arrayOfNodes, beginIndex, i, numberOfIterations, DampingFactor, STOP, 
        							   considerWeightedEdgesForComplexity, totalNumberOfEdges)).start();
        		beginIndex = i;
        		edgesInThread = 0;
        	}
        }
        new Thread(new Iterate(arrayOfNodes, beginIndex, arrayOfNodes.size(), numberOfIterations, DampingFactor, STOP, 
        					   considerWeightedEdgesForComplexity, totalNumberOfEdges)).start();
  
        System.out.println("Waiting "+timeBetweenAssessments+" miliseconds between each assessment.");
        
        // The program will stop running if it has for n consecutive assessments all improvements on centrality lower or equal to x,
    	// where n = consecutiveMinimalImprovementsOnCentralityToStop, and x = minimalImprovementOnCentralityToStop.
        int numberOfConsecutiveMinimalImprovements = 0;
        while(numberOfConsecutiveMinimalImprovements < consecutiveMinimalImprovementsOnCentralityToStop) {
        	Thread.sleep(timeBetweenAssessments);
        	
        	// lastImprovement measures the average relative improvement on the centrality from the last time it was checked.
        	// e.g.: if all nodes go from centrality = 0.5 to centrality = 0.6, then lastImprovement is equal to 0.2.
        	float lastImprovement = 0;
        	for ( Node address : arrayOfNodes ) {
        		lastImprovement += address.getLastImprovement();
        	}
        	lastImprovement /= arrayOfNodes.size(); 
        	
        	System.out.println("Average relative improvement on centrality since last assessment: "+lastImprovement);
        	
        	if (lastImprovement <= minimalImprovementOnCentralityToStop)
        		numberOfConsecutiveMinimalImprovements++;
        	else
        		numberOfConsecutiveMinimalImprovements = 0;
        }
        // stops all running threads.
        STOP[0] = true;
        
        // WRITE THE RESULTING GRAPH IN A FILE ////////////////////////////////////////////////////////////////////////////////////
        
        if(sortCollectionOfNodesByCentrality) {
        	arrayOfNodes.sort( Collections.reverseOrder( new Node.NodeComparatorCentrality() ) );
        }
        
    	if (writeCentrality || writeInDegree || writeOutDegree || writeFromNodes || writeToNodes) {
    		GraphWriter.write(arrayOfNodes, dic, writeCentrality, writeInDegree, writeOutDegree, writeFromNodes, writeToNodes);
    	}
    		
    }

}
