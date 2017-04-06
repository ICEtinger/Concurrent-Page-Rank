
/*******************

Created by Isak C. Etinger.

Last updated April 2017.

*******************/

/**
 * Enumerates the types of centrality present in the code.
 *
 * "Reversed" results the same as "Standard" in a graph whose edges have reversed orientations.
 * "UnweightedEdges" results the same as the normal weighted edges version in a graph where all edges have weight 1.
 * 
 */
public enum centralityType { 
	
	// centrality(node p) = (1-d)/number of edges + d*sum[for each q pointing to p](centrality of q * weight of edge(q->p) / weighted out-degree of q).
	standard, 
	
	// centrality(node p) = (1-d)/number of edges + d*sum[for each q pointed by p](centrality of q * weight of edge(p->q) / weighted in-degree of q).
	reversed, 
	
	// centrality(node p) = (1-d)/number of edges + d*B*sum[for each q pointing to p](centrality of q * weight of edge(q->p) / weighted out-degree of q)
	//                                            + d*(1-B)*sum[for each q pointed by p](centrality of q * weight of edge(p->q) / weighted in-degree of q).
	neighborCentrality,
	
	// centrality(node p) = (1-d)/number of nodes + d*sum[for each q pointing to p](centrality of q / out-degree of q).
	standardUnweightedEdges, 
	
	// centrality(node p) = (1-d)/number of nodes + d*sum[for each q pointed by p](centrality of q / in-degree of q).
	reversedUnweightedEdges, 
	
	// centrality(node p) = (1-d)/number of nodes + d*B*sum[for each q pointing to p](centrality of q / out-degree of q)
	//                                            + d*(1-B)*sum[for each q pointed by p](centrality of q / in-degree of q).
	neighborCentralityUnweightedEdges 
	                                           
}