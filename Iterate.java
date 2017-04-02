import java.util.ArrayList;

class Iterate implements Runnable {
	
	ArrayList<Node> arrayOfNodes;
	int startIndex;
	int endIndex;
	int numberOfIterations;
	float DampingFactor;
	// if true: centrality(node p) = (1-d)/number of edges + d*sum[for each q pointing to p](centrality of q * weight of edge(q->p) / weighted out degree of q).
	// if false: centrality(node p) = (1-d)/number of nodes + d*sum[for each q pointing to p](centrality of q / out degree of q).
	boolean considerWeightedEdgesForComplexity;
	// if STOP[1] is true, the thread should stop running.
	boolean[] STOP;
	int totalNumberOfEdges;
	
	Iterate ( ArrayList<Node> arrayOfNodes, int startIndex, int endIndex, int numberOfIterations, float DampingFactor, boolean[] STOP, 
			  boolean considerWeightedEdgesForComplexity, int totalNumberOfEdges ) {
		this.arrayOfNodes = arrayOfNodes;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
		this.numberOfIterations = numberOfIterations;
		this.DampingFactor = DampingFactor;
		this.STOP = STOP;
		this.considerWeightedEdgesForComplexity = considerWeightedEdgesForComplexity;
		this.totalNumberOfEdges = totalNumberOfEdges;
	}
	
	@Override
	public void run() {
		
		if (considerWeightedEdgesForComplexity) {
			while ( numberOfIterations -- != 0 && !STOP[0] ) {
				for ( int i = startIndex; i < endIndex; i++) {
					// centrality(node p) = (1-d)/number of edges + d*sum[for each q pointing to p](centrality of q * weight of edge(q->p) / weighted out-degree of q).
					Node currentNode = arrayOfNodes.get(i);
					float sum = 0;
					for ( Node n : currentNode.fromNodes.keySet() )
						sum += n.getCentrality() * currentNode.fromNodes.get(n) / n.outDegree();
					currentNode.setCentrality( (1 - DampingFactor)/totalNumberOfEdges + DampingFactor*sum ); 
				}
			}
		}
		else {
			while ( numberOfIterations -- != 0 && !STOP[0] ) {
				for ( int i = startIndex; i < endIndex; i++) {
					// centrality(node p) = (1-d)/number of nodes + d*sum[for each q pointing to p](centrality of q / out-degree of q).
					Node currentNode = arrayOfNodes.get(i);
					float sum = 0;
					for ( Node n : currentNode.fromNodes.keySet() )
						sum += n.getCentrality() / n.toNodes.size();
					currentNode.setCentrality( (1 - DampingFactor)/arrayOfNodes.size() + DampingFactor*sum ); 
				}
			}
		}
	
	}
	
}