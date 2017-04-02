import java.util.Comparator;
import java.util.HashMap;

public class Node {
		
		float centrality;
		//the centrality before doing the last iteration.
		float oldCentrality;
		// each node represents an email address.
		String address;
		
		// a collection containing the addresses that send emails to this adress. Integer is number of emails sent.
		HashMap<Node,Integer> fromNodes;
		// a collection containing the addresses that receive emails from this adress. Integer is number of emails received.
		HashMap<Node,Integer> toNodes;
		
		
		// CONSTRUCTORS //////////////////////////////////////////////////////////////////////////////////////
		
		Node(String address, float centrality) {
			this.address = address;
			this.centrality = centrality;
			this.oldCentrality = centrality;
			fromNodes = new HashMap<Node,Integer>();
			toNodes = new HashMap<Node,Integer>();
		}
		
		// METHODS ///////////////////////////////////////////////////////////////////////////////////////////
		
		void setCentrality(float centrality) {
			this.centrality = centrality;
		}
		
		void setCentrality(double centrality) {
			this.centrality = (float)centrality;
		}
		
		float getCentrality() {
			return centrality;
		}
		
		// measures the relative improvement of the last iteration on the centrality.
		float getLastImprovement() {
			float tmp = Math.abs( centrality/oldCentrality - 1 );
			oldCentrality = centrality;
			return tmp;
		}
		
		// registers an additional mail being sent from this address to toAddress.
		// creates another mapping if it is the first mail, otherwise increases by 1 the counting of emails.
		// returns the previous number of emails sent from this address to toAddress, or null if there was none.
		Integer addToNodes(Node toAddress) {
			return toNodes.put(toAddress, toNodes.getOrDefault(toAddress, 0) + 1);
		}
		
		// register an additional mail being sent to this address from fromAddress.
		// creates another mapping if it is the first mail, otherwise increases by 1 the counting of emails.
		// returns the previous number of emails sent to this address from fromAddress, or null if there was none.
		Integer addFromNodes(Node fromAddress) {
			return fromNodes.put(fromAddress, fromNodes.getOrDefault(fromAddress, 0) + 1);
		}
		
		// returns the total number of emails sent TO this address.
		int inDegree() {
			int tmp = 0;
			for ( Integer i : fromNodes.values() )
				tmp += i;
			return tmp;
		}
		
		// returns the total number of emails sent FROM this address.
		int outDegree() {
			int tmp = 0;
			for ( Integer i : toNodes.values() )
				tmp += i;
			return tmp;
		}
		
		// COMPARATORS ///////////////////////////////////////////////////////////////////////////////////////// 
		
		// compares the centrality of 2 nodes.
		static class NodeComparatorCentrality implements Comparator<Node> {
			@Override
			public int compare(Node n1, Node n2) {
				if (n1.getCentrality() < n2.getCentrality())
					return -1;
				if (n1.getCentrality() > n2.getCentrality())
					return 1;
				return 0;
			}
		}
		
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