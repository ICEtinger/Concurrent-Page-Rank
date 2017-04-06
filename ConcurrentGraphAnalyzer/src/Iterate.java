
/*******************

Created by Isak C. Etinger.

Last updated April 2017.

This class provides a Runnable for the PageRank class. 
It receives a centrality type and a subarray of Nodes (i.e. an array reference, the start index and the end index) determined by the PageRank, 
and repeats a loop of, for each Node in the subarray, doing an iteration on the recursive formula of the respective centrality type it received.
The loops stops either: 1) when the Thread repeats a specified number of loops, or
                        2) when the boolean STOP[0] is set to true. This is done by PageRank when it sees the relative improvement on that
                                                     centrality type is lower than a specified value for a specified number of assessments.

*******************/

import java.util.ArrayList;

class Iterate implements Runnable {
	
	ArrayList<Node> arrayOfNodes;
	int startIndex;
	int endIndex;
	int numberOfIterations;
	float DampingFactor;
	float neighborCentralityDampingFactor;
	float neighborCentralityBias;
	// if STOP[1] is true, the thread should stop running.
	boolean[] STOP;
	int totalNumberOfEdges;
	centralityType type;
	
	Iterate ( ArrayList<Node> arrayOfNodes, int startIndex, int endIndex, int numberOfIterations, float DampingFactor, 
			float neighborCentralityDampingFactor, float neighborCentralityBias, boolean[] STOP, int totalNumberOfEdges, centralityType type ) {
		this.arrayOfNodes = arrayOfNodes;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.numberOfIterations = numberOfIterations;
		this.DampingFactor = DampingFactor;
		this.neighborCentralityDampingFactor = neighborCentralityDampingFactor;
		this.neighborCentralityBias = neighborCentralityBias;
		this.STOP = STOP;
		this.totalNumberOfEdges = totalNumberOfEdges;
		this.type = type;
	}
	
	@Override
	public void run() {
		
		switch (type) {
		
			case standard: 
				while ( numberOfIterations -- != 0 && !STOP[0] ) {
					for ( int i = startIndex; i < endIndex; i++) {
						// centrality(node p) = (1-d)/number of edges + d*sum[for each q pointing to p](centrality of q * weight of edge(q->p) / weighted out-degree of q).
						Node currentNode = arrayOfNodes.get(i);
						float sum = 0;
						for ( Node n : currentNode.fromNodes.keySet() )
							sum += n.getCentrality(type) * currentNode.fromNodes.get(n) / n.outDegree();
						currentNode.setCentrality( type, (1 - DampingFactor)/totalNumberOfEdges + DampingFactor*sum ); 
					}
				}
				break;
				
			case reversed:
				while ( numberOfIterations -- != 0 && !STOP[0] ) {
					for ( int i = startIndex; i < endIndex; i++) {
						// centrality(node p) = (1-d)/number of edges + d*sum[for each q pointed by p](centrality of q * weight of edge(p->q) / weighted in-degree of q).
						Node currentNode = arrayOfNodes.get(i);
						float sum = 0;
						for ( Node n : currentNode.toNodes.keySet() )
							sum += n.getCentrality(type) * currentNode.toNodes.get(n) / n.inDegree();
						currentNode.setCentrality( type, (1 - DampingFactor)/totalNumberOfEdges + DampingFactor*sum ); 
					}
				}
				break;
		
			case standardUnweightedEdges:
				while ( numberOfIterations -- != 0 && !STOP[0] ) {
					for ( int i = startIndex; i < endIndex; i++) {
						// centrality(node p) = (1-d)/number of nodes + d*sum[for each q pointing to p](centrality of q / out-degree of q).
						Node currentNode = arrayOfNodes.get(i);
						float sum = 0;
						for ( Node n : currentNode.fromNodes.keySet() )
							sum += n.getCentrality(type) / n.toNodes.size();
						currentNode.setCentrality( type, (1 - DampingFactor)/arrayOfNodes.size() + DampingFactor*sum ); 
					}
				}
				break;
				
			case reversedUnweightedEdges:
				while ( numberOfIterations -- != 0 && !STOP[0] ) {
					for ( int i = startIndex; i < endIndex; i++) {
						// centrality(node p) = (1-d)/number of nodes + d*sum[for each q pointed by p](centrality of q / in-degree of q).
						Node currentNode = arrayOfNodes.get(i);
						float sum = 0;
						for ( Node n : currentNode.toNodes.keySet() )
							sum += n.getCentrality(type) / n.fromNodes.size();
						currentNode.setCentrality( type, (1 - DampingFactor)/arrayOfNodes.size() + DampingFactor*sum ); 
					}
				}
				break;
				
			case neighborCentrality:
				while ( numberOfIterations -- != 0 && !STOP[0] ) {
					for ( int i = startIndex; i < endIndex; i++) {
						// centrality(node p) = (1-d)/number of edges + d*B*sum[for each q pointing to p](centrality of q * weight of edge(q->p) / weighted out-degree of q)
						//                                            + d*(1-B)*sum[for each q pointed by p](centrality of q * weight of edge(p->q) / weighted in-degree of q).
						Node currentNode = arrayOfNodes.get(i);
						float sum = 0;
						for ( Node n : currentNode.fromNodes.keySet() )
							sum += neighborCentralityBias * n.getCentrality(type) * currentNode.fromNodes.get(n) / n.outDegree();
						for ( Node n : currentNode.toNodes.keySet() )
							sum += ( 1 - neighborCentralityBias ) * n.getCentrality(type) * currentNode.toNodes.get(n) / n.inDegree();
						currentNode.setCentrality( type, (1 - neighborCentralityDampingFactor)/totalNumberOfEdges + neighborCentralityDampingFactor*sum ); 
					}
				}
				break;
				
			case neighborCentralityUnweightedEdges:
				while ( numberOfIterations -- != 0 && !STOP[0] ) {
					for ( int i = startIndex; i < endIndex; i++) {
						// centrality(node p) = (1-d)/number of nodes + d*B*sum[for each q pointing to p](centrality of q / out-degree of q)
						//                                            + d*(1-B)*sum[for each q pointed by p](centrality of q / in-degree of q).
						Node currentNode = arrayOfNodes.get(i);
						float sum = 0;
						for ( Node n : currentNode.fromNodes.keySet() )
							sum += neighborCentralityBias * n.getCentrality(type) / n.toNodes.size();
						for ( Node n : currentNode.toNodes.keySet() )
							sum += ( 1 - neighborCentralityBias ) * n.getCentrality(type) / n.fromNodes.size();
						currentNode.setCentrality( type, (1 - neighborCentralityDampingFactor)/arrayOfNodes.size() + neighborCentralityDampingFactor*sum ); 
					}
				}
				break;
				
		}
	}
	
}