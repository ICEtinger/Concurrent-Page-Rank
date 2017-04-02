import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class GraphBuilder {

	// receives dic empty and puts pairs <String, Node> such that dic.values() is a Collection of all the Nodes in the graph
	static int[] BuildGraphFromCSV(HashMap<String,Node> dic, float InitialCentrality, boolean runTests) { 
		
		// variables to read file
        String csvFile = "EmlRecipients_16_sorted2.txt";
        String line = "";
        String cvsSplitBy = ",";
        
        // variables to count data parsed from file
        int LineCounter = 0;
        int EmailCounter = 0;
        int totalNumberOfEdges = 0; // non-oriented edges
        
        // READING FILE AND BUILDING GRAPH /////////////////////////////////////////////////////////////////////
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
        	
        	String fromAddress = "";
            while ((line = br.readLine()) != null) {
            	
                // use comma as separator
                String[] currentLine = line.split(cvsSplitBy);
                
                // maps the email address to a node representing it in the dictionary if it does not already exist.
                dic.putIfAbsent(currentLine[1], new Node(currentLine[1], InitialCentrality));
                
                // registers the mail being sent in toNodes and fromNodes in the addresses nodes.
                if (currentLine[3].equals("A_from")) {
                	EmailCounter++;
                	fromAddress = currentLine[1];
                }
                else {
                	if(dic.get(currentLine[1]).addToNodes(dic.get(fromAddress)) == null)
                		totalNumberOfEdges++;
                	dic.get(fromAddress).addFromNodes(dic.get(currentLine[1]));
                }
                
                LineCounter++;
            	
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
	
}
