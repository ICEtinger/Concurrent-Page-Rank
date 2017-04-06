
/*******************

Created by Isak C. Etinger.

Last updated April 2017.

Reads a CSV file and builds a graph from it. 
The form of the CSV file is: each line = "email identifier, ?, email address, from/to/cc", that indicates the email "email identifier"
is sent from (if from) or to (if to/cc) the email address "email address".

Fills the dictionary mapping the Strings of the email addresses to their respective Nodes representing them in the graph (variable "dic").

Optionally, fills a map (variable named "emails") in which each pair <String,List<Node> represents as key a String of an email identifier 
and as value a list of Nodes present in that email (either as sender or as receiver of the email).

*******************/


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class GraphBuilder {

	/**
     * Reads a CSV file and builds a graph from it. 
     * The form of the CSV file is: each line = "email identifier, ?, email address, from/to/cc", that indicates the email "email identifier"
     * is sent from (if from) or to (if to/cc) the email address "email address".
     * 
     * Receives dic empty and puts pairs <String, Node> such that dic.values() is a Collection of all the Nodes in the graph.
     * This method is called without the need to return the mapping emails to Nodes.
     *
     * @param csvFileName the name of the CSV file from which the graph will be built.
     *
     * @param dic the dictionary mapping the Strings of the email addresses to their respective Nodes representing them in the graph. 
     * It is received empty and will be filled by this function.
     * 
     * @param emails a map in which each pair <String,List<Node> represents as key a String of an email identifier 
     * and as value a list of Nodes present in that email (either as sender or as receiver of the email).
     * It is received empty and will be filled by this function.
     * 
     * @param InitialCentrality the initial value for centrality assigned to all Nodes in the graph. 
     * 
     * @param runTests whether or not to run tests confirming if the graph was properly built, and printing an error message in the 
     * console otherwise.
     * 
     * @implNote this method is called without the need to return the mapping emails to Nodes, so it throws away the reference 
     * to such mapping upon returning.
     *
     */
	static int[] BuildGraphFromCSV(String csvFileName, Map<String,Node> dic, Map<String,List<Node>> emails, float InitialCentrality, boolean runTests) { 
		
        // variables to read file
        String line = "";
        String cvsSplitBy = ",";
        
        // variables to count data parsed from file
        int LineCounter = 0;
        int EmailCounter = 0;
        int totalNumberOfEdges = 0; // non-oriented edges
        
        // READING FILE AND BUILDING GRAPH /////////////////////////////////////////////////////////////////////
          
        try (BufferedReader br = new BufferedReader(new FileReader(csvFileName))) {
        	
            while ((line = br.readLine()) != null) {
            	
                // use comma as separator
                String[] currentLine = line.split(cvsSplitBy);
                
                // maps the email address to a node representing it in the dictionary if it does not already exist.
                dic.putIfAbsent(currentLine[1], new Node(currentLine[1], InitialCentrality));
                
                // if it does not already exist, maps the email-identifier String to a list of Nodes that either receive or send that email.
                emails.putIfAbsent(currentLine[0], new LinkedList<Node>());
                
                // if it is the sender of an email.
                if (currentLine[3].equals("from")) {
                	// adds the address in this line to the list of addresses that either send or receive the email.
                	// adds it in the first position because it is the sender of the email.
                	emails.get(currentLine[0]).add(0, dic.get(currentLine[1]));
                	EmailCounter++;
                }
                // if it is a receiver of an email.
                else {
                	// adds the address in this line to the list of addresses that either send or receive the email.
                	// adds it in the end of the list because it is a receiver of the email.
                	emails.get(currentLine[0]).add(dic.get(currentLine[1]));
                }
                  
                LineCounter++;
            }
            
            // registers the emails being sent in toNodes and fromNodes in the addresses nodes.
            for ( List<Node> list : emails.values() ) {
            	// initializes the Node sender as the first element of the list of addresses that took part in the email.
            	Iterator<Node> i = list.iterator();
            	Node sender = i.next();
            	// for each other address in the list of addresses that took part in the email:
            	while( i.hasNext() ) {
                    Node address = i.next();
                    // register that an email is sent from the sender to the current address in the iterator.
            		if(sender.addToNodes(address) == null)
                		totalNumberOfEdges++;
            		// register that an email is received by the current address in the iterator from the sender.
            		address.addFromNodes(sender);
                }
            }
                        
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Read "+LineCounter+" lines.");
        System.out.println("Represented "+EmailCounter+" different emails.");
        System.out.println("Represented "+dic.size()+" different email addresses");
        System.out.println("Created graph with "+dic.size()+" nodes and "+totalNumberOfEdges+" edges. Total weighted degree of graph: "+(LineCounter - EmailCounter));
        
        // checks if the graph was properly built.
        if (runTests) {
        	int contNumberOfEdges = 0; 
        	int contSumOfInDegrees = 0;
        	int contSumOfOutDegrees = 0;
        	for ( Node address : dic.values() ) {
        		contNumberOfEdges += address.toNodes.size();
        		contSumOfInDegrees += address.inDegree();
        		contSumOfOutDegrees += address.outDegree();
        	}
        	Assert.check (totalNumberOfEdges == contNumberOfEdges, "number of edges");
        	Assert.check (contSumOfInDegrees == contSumOfOutDegrees, "sum of in degrees == sum of out degrees");
        	Assert.check (contSumOfInDegrees == LineCounter - EmailCounter, "number of emails");
        }
	
        // returns information about the graph's structure.
        int[] tmp = {LineCounter, EmailCounter, totalNumberOfEdges};
        return tmp;
        
	}
	
	/**
	 * Reads a CSV file and builds a graph from it. 
     * The form of the CSV file is: each line = "email identifier, ?, email address, from/to/cc", that indicates the email "email identifier"
     * is sent from (if from) or to (if to/cc) the email address "email address".
     *
     * Receives dic empty and puts pairs <String, Node> such that dic.values() is a Collection of all the Nodes in the graph.
     * This method is called without the need to return the mapping emails to Nodes.
     *
     * @param csvFileName the name of the CSV file from which the graph will be built.
     *
     * @param dic the dictionary mapping the Strings of the email addresses to their respective Nodes representing them in the graph. 
     * It is received empty and will be filled by this function.
     * 
     * @param InitialCentrality the initial value for centrality assigned to all Nodes in the graph. 
     * 
     * @param runTests whether or not to run tests confirming if the graph was properly built, and printing an error message in the 
     * console otherwise.
     * 
     * @implNote this method is called without the need to return the mapping emails to Nodes, so it throws away the reference 
     * to such mapping upon returning.
     *
     */
	static int[] BuildGraphFromCSV (String csvFileName, Map<String,Node> dic, float InitialCentrality, boolean runTests) {
		return BuildGraphFromCSV (csvFileName, dic, new HashMap<String,List<Node>>(), InitialCentrality, runTests);
	}
	
	
}
