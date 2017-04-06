
/*******************

Created by Isak C. Etinger.

Last updated April 2017.

This class contains the main function. 
It builds a graph from a CSV file (by calling the class GraphBuilder) that represent emails being sent between email addresses,
calculates up to 6 types of centrality (with class PageRank), 
calculates statistical values from the graph (with class getStatsFromCollection),
and prints the results in a CSV file (with class GraphWriter).

It contains the parameters to specify which centrality types and which statistical values should be calculated, 
as well as parameters about how the PageRank algorithm should operate (namely the number of Threads, the damping factor of 4 types of 
centrality, the damping and bias factor of 2 types of centrality, and when to stop iterating the loop that calculates the centrality). 
It also contains a parameter to sort the nodes by a specified type of centrality before printing.

It can calculate the following values about each node in the graph (email addresses):
                                             the 6 types of centrality specified at centralityType.java
	                                         (namely Standard, Reversed, Neighbor, and their versions with unweighted edges),
	                                         and the in-degree and out-degree of each Node.

The statistical values it calculates about the graph is, for each email sent:
                                             the String of the email-identifier,
	                                         the values(^) of the Node sending the email, and statistical results of the Collection of the 
	                                         values(^) of the Nodes receiving the email, those statistical results are: 
	                                       { the mean, the median, the standard deviation, the harmonic mean, and the mean absolute deviation }

*******************/

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.BiFunction;

public class MainFunction {

	//////////////////////////////////////////////////////////   PARAMETERS   /////////////////////////////////////////////////////////////
	
	// the name of the CSV file from which the graph will be build by calling the GraphBuilder class.
	private static final String csvFileName = "EmlRecipients.csv";
	
	// if true: centrality(node p) = (1-d)/number of edges + d*sum[for each q pointing to p](centrality of q * weight of edge(q->p) / weighted out-degree of q).
	// if false: centrality(node p) = (1-d)/number of nodes + d*sum[for each q pointing to p](centrality of q / out-degree of q).
	private static final boolean considerWeightedEdgesForComplexity = true;	
	
	// The initial centrality used in the Page Rank algorithm.
	private static final float InitialCentrality = 1;
	// Damping factor d used in the Page Rank algorithm.
	private static final float DampingFactor = (float) 0.85;
	
	// Damping factor d used in the Neighbor-Centrality algorithm.
	private static final float neighborCentralityDampingFactor = (float) 0.85; 
	// Bias factor B used in the Neighbor-Centrality algorithm.
	private static final float neighborCentralityBias = (float) 0.5;
	
	// Time taken between each assessment on the mean relative improvement on centrality since last assessment.
	private static final long timeBetweenAssessments = 500;
	// The program will stop running if it has for n consecutive assessments all improvements on centrality lower or equal to x,
	// where n = consecutiveMinimalImprovementsOnCentralityToStop, and x = minimalImprovementOnCentralityToStop.
	private static final float minimalImprovementOnCentralityToStop = (float)0.0001;
	private static final int consecutiveMinimalImprovementsOnCentralityToStop = 5;
	
	// The number of concurrent threads running simultaneously in the Page Rank algorithm for each ranker.
	private static final int numberOfConcurrentThreads = 10;
	// The number of iterations to be executed by each thread. Set to negative for a loop without limits on iterations, 
	//                                                         that shall be stopped only after reaching a minimal improvement on centrality.
	private static final int numberOfIterations = -1;

	// Whether or not to run tests to check if the graph was properly built. 
	private static final boolean runTests = true;
	
	// By what measure of centrality the Nodes shall be sorted.
	private static final centralityType sortCollectionOfNodesByCentrality = centralityType.standard;
	
	// Whether or not to write the respective values in the file.
	// Set all to false to not create any file.
	// booleans used for writing file by Nodes (addresses)
	private static final boolean writeStandard = true; // centrality(node p) = (1-d)/number of edges + d*sum[for each q pointing to p](centrality of q * weight of edge(q->p) / weighted out-degree of q).
	private static final boolean writeReversed = true; // centrality(node p) = (1-d)/number of edges + d*sum[for each q pointed by p](centrality of q * weight of edge(p->q) / weighted in-degree of q).
	private static final boolean writeNeighborCentrality = true; // centrality(node p) = (1-d)/number of edges + d*B*sum[for each q pointing to p](centrality of q * weight of edge(q->p) / weighted out-degree of q) + d*(1-B)*sum[for each q pointed by p](centrality of q * weight of edge(p->q) / weighted in-degree of q).
	private static final boolean writeStandardUnweightedEdges = true; // centrality(node p) = (1-d)/number of nodes + d*sum[for each q pointing to p](centrality of q / out-degree of q).
	private static final boolean writeReversedUnweightedEdges = true; // centrality(node p) = (1-d)/number of nodes + d*sum[for each q pointed by p](centrality of q / in-degree of q).
	private static final boolean writeNeighborCentralityUnweightedEdges = true; // centrality(node p) = (1-d)/number of nodes + d*B*sum[for each q pointing to p](centrality of q / out-degree of q) + d*(1-B)*sum[for each q pointed by p](centrality of q / in-degree of q).
	private static final boolean writeInDegree = true;
	private static final boolean writeOutDegree = true;
	private static final boolean writeFromNodes = false; // lists all Nodes (addresses) sending emails to this Node
	private static final boolean writeToNodes = false; // lists all Nodes (addresses) receiving emails from this Node
	
	// Set all to false to not create any file.
	// booleans used for writing file by emails.
	
	// information about the address sending the email.
	private static final boolean writeSenderStandard = true;
	private static final boolean writeSenderReversed = true;
	private static final boolean writeSenderNeighborCentrality = true;
	private static final boolean writeSenderStandardUnweightedEdges = true;
	private static final boolean writeSenderReversedUnweightedEdges = true;
	private static final boolean writeSenderNeighborCentralityUnweightedEdges = true;
	private static final boolean writeSenderInDegree = true;
	private static final boolean writeSenderOutDegree = true;
	private static final boolean writeSenderUnweightedInDegree = true;
	private static final boolean writeSenderUnweightedOutDegree = true;
	// information about statistical results among people receiving the email.
	private static final boolean writeStatsRecipientStandard = true;
	private static final boolean writeStatsRecipientReversed = true;
	private static final boolean writeStatsRecipientNeighborCentrality = true;
	private static final boolean writeStatsRecipientStandardUnweightedEdges = true;
	private static final boolean writeStatsRecipientReversedUnweightedEdges = true;
	private static final boolean writeStatsRecipientNeighborCentralityUnweightedEdges = true;
	private static final boolean writeStatsRecipientInDegree = true;
	private static final boolean writeStatsRecipientOutDegree = true;
	private static final boolean writeStatsRecipientUnweightedInDegree = true;
	private static final boolean writeStatsRecipientUnweightedOutDegree = true;
	// information about which statistical results will be printed.
	private static final boolean writeMedian = true;
	private static final boolean writeMean = true;
	private static final boolean writeStandardDeviation = true;
	private static final boolean writeHarmonicMean = true;
	private static final boolean writeMeanAbsoluteDeviation = true;
		
	///////////////////////////////////////////////////////////   MAIN FUNCTION   ///////////////////////////////////////////////////////////
	
    public static void main(String[] args) throws InterruptedException {
    	
    	// start to count the total time taken to run the program.
    	long startTime = System.currentTimeMillis();
    	
    	// prints the parameters used at running the program.
    	System.out.println("Running program with: initial centrality = "+InitialCentrality+
    			           ", damping factor = "+DampingFactor+
    			           ", number of concurrent threads = "+numberOfConcurrentThreads+
    			           ", number of iterations = "+(numberOfIterations >= 0 ? numberOfIterations : "infinite")+
    			           (considerWeightedEdgesForComplexity ? ", considering edge weights for complexity evaluation":", using unweighted edges for complexity evaluation")+
    			           (runTests ? ", running safety tests on graph-building." : ".") );
    	
    	////////////////////////////////////////////////////   BUILDS GRAPH FROM FILE   ///////////////////////////////////////////////////
    	
    	// the dictionary mapping the Strings of the email addresses to their respective Nodes representing them in the graph. 
    	HashMap<String,Node> dic = new HashMap<String,Node>();
    	
    	// each pair <String,List<Node> represents as key a String of an email identifier 
    	// and as value a list of Nodes present in that email (either as sender or as receiver of the email).
    	HashMap<String,List<Node>> emails = new HashMap<String,List<Node>>();
    	
    	int[] tmp = GraphBuilder.BuildGraphFromCSV(csvFileName, dic, emails,  InitialCentrality, runTests);
    	// gets information about the graph's structure.
    	@SuppressWarnings("unused") //  those results are not used in this implementation, but are left here since they are the byproduct of 
    	//the last function (yielding no further operations) and can be used later without the need to re-run all the function.
		int LineCounter = tmp[0], EmailCounter = tmp[1], totalNumberOfEdges = tmp[2];        
    	
    	////////////////////////////////////////////////////   ANALYZES THE GRAPH   ////////////////////////////////////////////////////////
        
    	System.out.println("Started calculating the centralities of the Nodes in the graph.");
    	
    	BiFunction< centralityType, String, Thread > rankerThread = (type, text) -> new Thread( new PageRank (type, dic, timeBetweenAssessments, 
    			totalNumberOfEdges, numberOfConcurrentThreads, numberOfIterations, consecutiveMinimalImprovementsOnCentralityToStop, 
    			minimalImprovementOnCentralityToStop, DampingFactor, neighborCentralityDampingFactor, neighborCentralityBias, 
    			considerWeightedEdgesForComplexity, text) );
    	
    	Thread[] rankers = new Thread[6];
    	
    	if(writeStandard)				rankers[0] = rankerThread.apply(centralityType.standard, "Standard---");
    	if(writeReversed)				rankers[1] = rankerThread.apply(centralityType.reversed, "Reversed---");
    	if(writeNeighborCentrality)			rankers[2] = rankerThread.apply(centralityType.neighborCentrality, "Neighbor---");
    	if(writeStandardUnweightedEdges)		rankers[3] = rankerThread.apply(centralityType.standardUnweightedEdges, "Standard UE");
    	if(writeReversedUnweightedEdges)		rankers[4] = rankerThread.apply(centralityType.reversedUnweightedEdges, "Reversed UE");
    	if(writeNeighborCentralityUnweightedEdges)	rankers[5] = rankerThread.apply(centralityType.neighborCentralityUnweightedEdges, "Neighbor UE");
    	
    	// starts all ranker threads at the same time.
    	for ( Thread ranker : rankers ) {
    		if (ranker != null)
    			ranker.start();
    	}

    	// waits for all of them to be finished.
    	for ( Thread ranker : rankers ) {
    		if (ranker != null)
    			ranker.join();
    	}
    	
    	System.out.println("Finished analyzing the graph.");
    	
    	///////////////////////////////////////////////   WRITES THE RESULTING GRAPH IN A FILE   ///////////////////////////////////////////
        
    	ArrayList<Node> arrayOfNodes = new ArrayList<Node>(dic.values());
    	
    	Node.sortByParameter(arrayOfNodes, sortCollectionOfNodesByCentrality);

    	if ( writeStandard || writeReversed || writeNeighborCentrality || writeStandardUnweightedEdges || writeReversedUnweightedEdges ||
    			writeNeighborCentralityUnweightedEdges || writeInDegree || writeOutDegree || writeFromNodes || writeToNodes) {
    		
    		GraphWriter.write(arrayOfNodes, dic, writeStandard, writeReversed, writeNeighborCentrality, writeStandardUnweightedEdges, 
    				writeReversedUnweightedEdges, writeNeighborCentralityUnweightedEdges, writeInDegree, writeOutDegree, writeFromNodes, 
    				writeToNodes, "byAddresses_");
    	}
    	
    	GraphWriter.writeByEmails(emails, writeSenderStandard, writeSenderReversed, writeSenderNeighborCentrality, writeSenderStandardUnweightedEdges,
    			writeSenderReversedUnweightedEdges, writeSenderNeighborCentralityUnweightedEdges, writeSenderInDegree, writeSenderOutDegree, 
    			writeSenderUnweightedInDegree, writeSenderUnweightedOutDegree, writeStatsRecipientStandard, writeStatsRecipientReversed,
    			writeStatsRecipientNeighborCentrality, writeStatsRecipientStandardUnweightedEdges, writeStatsRecipientReversedUnweightedEdges,
    			writeStatsRecipientNeighborCentralityUnweightedEdges, writeStatsRecipientInDegree, writeStatsRecipientOutDegree, 
    			writeStatsRecipientUnweightedInDegree, writeStatsRecipientUnweightedOutDegree, writeMedian, writeMean, 
    			writeStandardDeviation, writeHarmonicMean, writeMeanAbsoluteDeviation, "byEmails_");
    	
    	System.out.println("Total time taken (in seconds): "+ (float)(System.currentTimeMillis() - startTime) / 1000 );
    	
    }

}
