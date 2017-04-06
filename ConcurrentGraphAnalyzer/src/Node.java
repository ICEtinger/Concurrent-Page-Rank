
/*******************

Created by Isak C. Etinger.

Last updated April 2017.

This class is the Node with which the graph will be represented. Each Node represents an email address.

It contains: a String that is the email address,
             the 6 types of centrality specified at centralityType.java
                                         (namely Standard, Reversed, Neighbor, and their versions with unweighted edges),
             the values of those centralities at the last time they were assessed (this is used for calculating the relative improvement), 
             weighted oriented edges to other Nodes, representing the emails sent or received and being implemented by 2 HashMap<Node,Integer>,
                                         where the weight is the number of emails sent or received.

*******************/

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;

public class Node {
		
		// the email address being represented in the Node.
		String address;
		
		// this contains the 6 types of centrality specified at centralityType.java 
		// (namely Standard, Reversed, Neighbor, and their versions with unweighted edges)
		float[] centrality;
		
		// each centrality before doing the last iteration.
		float[] oldCentrality;
		
		// a collection containing the addresses that send emails to this adress. Integer is number of emails sent.
		HashMap<Node,Integer> fromNodes;
		// a collection containing the addresses that receive emails from this adress. Integer is number of emails received.
		HashMap<Node,Integer> toNodes;
		
		
		// CONSTRUCTORS //////////////////////////////////////////////////////////////////////////////////////
		
		Node(String address, float centrality) {
			this.address = address;
			float tmp[] = {centrality,centrality,centrality,centrality,centrality,centrality};
			this.centrality = tmp;
			float tmp2[] = {centrality,centrality,centrality,centrality,centrality,centrality};
			this.oldCentrality = tmp2;
			fromNodes = new HashMap<Node,Integer>();
			toNodes = new HashMap<Node,Integer>();
		}
		
		// METHODS ///////////////////////////////////////////////////////////////////////////////////////////
		
		
		/**
	     * Sorts a array of Nodes by a given centrality type of its Nodes.
	     *
	     * @param arr the array to be sorted.
	     * 
	     * @param type the centrality type to use for sorting the array.
	     *
	     */
		static void sortByParameter(ArrayList<Node> arr, centralityType type) {
			
			class NodesComparator implements Comparator<Node> {
				@Override
				public int compare(Node n1, Node n2) {
					if (n1.getCentrality(type) < n2.getCentrality(type))
						return -1;
					if (n1.getCentrality(type) > n2.getCentrality(type))
						return 1;
					return 0;
				}
			}
			
			arr.sort(new NodesComparator());
		
		}
		
		/**
	     * Set a specified type of centrality.
	     *
	     * @param type the type of centrality to be set.
	     * 
	     * @param centrality the new value to be set.
	     *
	     */
		void setCentrality(centralityType type, float centrality) {
			this.centrality[type.ordinal()] = centrality;
		}
		
		/**
	     * Set a specified type of centrality.
	     *
	     * @param type the type of centrality to be set.
	     * 
	     * @param centrality the new value to be set.
	     *
	     */
		void setCentrality(centralityType type, double centrality) {
			this.centrality[type.ordinal()] = (float)centrality;
		}
		
		/**
	     * Gets a specified type of centrality.
	     *
	     * @param type the type of centrality to get.
	     * 
	     */
		float getCentrality(centralityType type) {
			return centrality[type.ordinal()];
		}
		
		/**
	     * Measures the relative improvement of the last assessment on a specified type of centrality.
	     *
	     * @param type the type of centrality to measure.
	     * 
	     */
		float getLastImprovement(centralityType type) {
			float tmp = Math.abs( centrality[type.ordinal()]/oldCentrality[type.ordinal()] - 1 );
			oldCentrality[type.ordinal()] = centrality[type.ordinal()];
			return tmp;
		}
		 
		/**
	     * Registers an additional mail being sent from this address to toAddress.
		 * Creates another mapping if it is the first mail, otherwise increases by 1 the counting of emails.
		 * Returns the previous number of emails sent from this address to toAddress, or null if there was none.
	     *
	     * @param toAddress the node to which the email is sent.
	     * 
	     */
		Integer addToNodes(Node toAddress) {
			return toNodes.put(toAddress, toNodes.getOrDefault(toAddress, 0) + 1);
		}
		
		/**
	     * Registers an additional mail being sent to this address to fromAddress.
		 * Creates another mapping if it is the first mail, otherwise increases by 1 the counting of emails.
		 * Returns the previous number of emails sent from this address to toAddress, or null if there was none.
	     *
	     * @param fromAddress the node from which the email is sent.
	     * 
	     */
		Integer addFromNodes(Node fromAddress) {
			return fromNodes.put(fromAddress, fromNodes.getOrDefault(fromAddress, 0) + 1);
		}
		
		/**
	     * Returns the total number of emails sent to this address.
	     * 
	     */
		int inDegree() {
			int tmp = 0;
			for ( Integer i : fromNodes.values() )
				tmp += i;
			return tmp;
		}

		/**
	     * Returns the total number of emails sent from this address.
	     * 
	     */
		int outDegree() {
			int tmp = 0;
			for ( Integer i : toNodes.values() )
				tmp += i;
			return tmp;
		}
		
		/**
	     * Returns the number of addresses that sent emails to this address.
	     * 
	     */
		int unweighedInDegree() {
			return fromNodes.size();
		}
		
		/**
	     * Returns the number of addresses that received emails from this address.
	     * 
	     */
		int unweighedOutDegree() {
			return toNodes.size();
		}
		
		// COMPARATORS ///////////////////////////////////////////////////////////////////////////////////////// 
		
		// compares the email addresses of 2 nodes in the alphabetical ordering (i.e.: the Unicode value of each character in the Strings).
		static class NodeComparatorAddress implements Comparator<Node> {
			@Override
			public int compare(Node n1, Node n2) {
				 return n1.address.compareTo(n2.address);
			}
		}
		
		// compares the weighted in-degree of 2 nodes.
		static class NodesComparatorInDegree implements Comparator<Node> {
			@Override
			public int compare(Node n1, Node n2) {
				return n1.inDegree() - n2.inDegree();
			}
		}
		
		// compares the weighted out-degree of 2 nodes.
		static class NodesComparatorOutDegree implements Comparator<Node> {
			@Override
			public int compare(Node n1, Node n2) {
				return n1.outDegree() - n2.outDegree();
			}
		}
		
}