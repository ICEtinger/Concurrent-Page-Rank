
/*******************

Created by Isak C. Etinger.

Last updated April 2017.

This class provides methods for writing information from a graph into a CSV file.

It provides two types of methods:

1) METHODS FOR WRITING BY NODES (EMAIL ADDRESSES)
	Creates and writes a CSV file containing information of each Node (representing an email address) in a given collection. 
	That information contains: the String of the email address,
	                           the 6 types of centrality specified at centralityType.java and that the PageRank class supports calculating,
	                           (namely Standard, Reversed, Neighbor, and their versions with unweighted edges),
	                           the in-degree and out-degree of each Node,
	                           and the lists of every Node it has edges with (the Nodes sending emails to and receiving emails from it).  

2) METHODS FOR WRITING BY EMAILS
	Creates and writes a CSV file containing information of each email in a given collection.
	That information contains: the String of the email-identifier,
	                           the values(*) of the Node sending the email, and statistical results of the Collection of the 
	                           values(*) of the Nodes receiving the email, those statistical results are: 
	                           { the mean, the median, the standard deviation, the harmonic mean, and the mean absolute deviation }
	         
	         (*): the values of each Node are: the 6 types of centrality specified at centralityType.java
	                                           (namely Standard, Reversed, Neighbor, and their versions with unweighted edges),
	                                           and the in-degree and out-degree of each Node.


*******************/

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class GraphWriter {
	
	/////////////////////////////////   METHODS FOR WRITING BY NODES (EMAIL ADDRESSES)   ////////////////////////////////////////////////////
	
	
	/**
     * Creates and writes a CSV file containing the information of each Node in a given collection. 
     * Each boolean parameter represents whether or not its respective value should be written in the file.
     * The name of the file is the date and time it was created.
     *
     * @param collectionOfNodes a Collection of Nodes whose information will be written in the CSV file.
     *
     * @param dic the dictionary mapping the Strings of the email addresses to their respective Nodes representing them in the graph.
     *
     */
	public static void write( Collection<Node> collectionOfNodes, Map<String,Node> dic, boolean writeStandard, boolean writeReversed, 
			boolean writeNeighborCentrality, boolean writeStandardUnweightedEdges, boolean writeReversedUnweightedEdges, 
			boolean writeNeighborCentralityUnweightedEdges, boolean writeInDegree, boolean writeOutDegree, boolean writeFromNodes, 
			boolean writeToNodes ) {
		
		write( collectionOfNodes, dic, writeStandard, writeReversed, writeNeighborCentrality, writeStandardUnweightedEdges, 
				writeReversedUnweightedEdges, writeNeighborCentralityUnweightedEdges, writeInDegree, writeOutDegree, writeFromNodes, 
				writeToNodes, "" );
	}
	
	/**
     * Creates and writes a CSV file containing the information of each Node in a given collection. 
     * Each boolean parameter represents whether or not its respective value should be written in the file.
     * Additionally, the name of the file created is further specified: it is the additionalFileName parameter followed by
     * the date and time it was created.
     *
     * @param collectionOfNodes a Collection of Nodes whose information will be written in the CSV file.
     *
     * @param dic the dictionary mapping the Strings of the email addresses to their respective Nodes representing them in the graph.
     *
     * @param additonalFileName the name of the file will be the additionalFileName parameter followed by the date and time it was created.
     *
     */
	public static void write( Collection<Node> collectionOfNodes, Map<String,Node> dic, boolean writeStandard, boolean writeReversed, 
			boolean writeNeighborCentrality, boolean writeStandardUnweightedEdges, boolean writeReversedUnweightedEdges, 
			boolean writeNeighborCentralityUnweightedEdges, boolean writeInDegree, boolean writeOutDegree, boolean writeFromNodes, 
			boolean writeToNodes, String additionalFileName ) { 
    	
        BufferedWriter writer = null;
        try {
            //create a temporary file
            String timeLog = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
            File logFile = new File(additionalFileName + timeLog);

            // prints the path in which the file will be created
            System.out.println("File created on path: "+logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(logFile));
            
            // checks for invalid input before continuing.
            if ( dic == null && (writeFromNodes || writeToNodes) ) {
            	writer.write("ERROR: attempted to acess a null pointer as a mapping of email address Strings -> Nodes");
            	return;
            }
            
            for (Node address : collectionOfNodes) {
            	writer.write( address.address);
            	if (writeStandard)							writer.write( "," + address.getCentrality(centralityType.standard) );
            	if (writeReversed)							writer.write( "," + address.getCentrality(centralityType.reversed) );
            	if (writeNeighborCentrality)				writer.write( "," + address.getCentrality(centralityType.neighborCentrality) );
            	if (writeStandardUnweightedEdges)			writer.write( "," + address.getCentrality(centralityType.standardUnweightedEdges) );
            	if (writeReversedUnweightedEdges)			writer.write( "," + address.getCentrality(centralityType.reversedUnweightedEdges) );
            	if (writeNeighborCentralityUnweightedEdges)	writer.write( "," + address.getCentrality(centralityType.neighborCentralityUnweightedEdges) );
            	if (writeInDegree)							writer.write( "," + address.inDegree() );
            	if (writeOutDegree)							writer.write( "," + address.outDegree() );
            	
            	if (writeFromNodes) {
            		writer.write(", received "+address.inDegree()+" emails from: ");
            		for ( Node n : address.fromNodes.keySet() ) {
            			writer.write(n.address+"("+address.fromNodes.get(n)+");");
            		}
            	}
            	if (writeToNodes) {
            		writer.write(", sent "+address.outDegree()+" emails to: ");
            		for ( Node n : address.toNodes.keySet() ) {
            			writer.write(n.address+"("+address.toNodes.get(n)+");");
            		}
            	}
            	
            	writer.newLine();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
    
    }
    
	/**
     * Creates and writes a CSV file containing all the centrality values of each Node in a given collection. 
     * The name of the file is the date and time it was created.
     *
     * @param collectionOfNodes a Collection of Nodes whose information will be written in the CSV file.
     *
     * @param dic the dictionary mapping the Strings of the email addresses to their respective Nodes representing them in the graph.
     *
     */
    public static void writeCentrality(Collection<Node> collectionOfNodes) {
    	write(collectionOfNodes, null, true, true, true, true, true, true, false, false, false, false);
    }
    
    /**
     * Creates and writes a CSV file containing all the values (possible to obtain with the collectionOfNodes parameter) of each Node
     * in a given collection. The name of the file is the date and time it was created.
     *
     * @param collectionOfNodes a Collection of Nodes whose information will be written in the CSV file.
     *
     */
    public static void write( Collection<Node> collectionOfNodes ) {
    	write(collectionOfNodes, null, true, true, true, true, true, true, true, true, false, false);
    }
    
    /**
     * Creates and writes a CSV file containing ALL the values of each Node in a given collection. 
     * The name of the file is the date and time it was created.
     *
     * @param collectionOfNodes a Collection of Nodes whose information will be written in the CSV file.
     *
     * @param dic the dictionary mapping the Strings of the email addresses to their respective Nodes representing them in the graph.
     *
     */
    public static void write( Collection<Node> collectionOfNodes, Map<String,Node> dic ) {
    	write(collectionOfNodes, dic, true, true, true, true, true, true, true, true, true, true);
    }
    
    /////////////////////////////////////   METHODS FOR WRITING BY EMAILS   ////////////////////////////////////////////////////
    
    // writes only the information about the sender of the email.
    
    /**
     * Creates and writes a CSV file containing ALL the values of the sender of each respective email. 
     * The name of the file is the date and time it was created.
     *
     * @param emails a map in which each pair <String,List<Node> represents as key a String of an email identifier 
     * and as value a list of Nodes present in that email (either as sender or as receiver of the email).
     *
     */
    public static void writeByEmailsSender (Map<String,List<Node>> emails) {
    	writeByEmails( emails, true, true, true, true, true, true, true, true, true, true, false, false, false, false,
    			false, false, false, false, false, false, false, false, false, false, false, "" );
    }
    
    /**
     * Creates and writes a CSV file containing ALL the values of the sender of each respective email. 
     * Additionally, the name of the file created is further specified: it is the additionalFileName parameter followed by
     * the date and time it was created.
     *
     * @param emails a map in which each pair <String,List<Node> represents as key a String of an email identifier 
     * and as value a list of Nodes present in that email (either as sender or as receiver of the email).
     *
     * @param additonalFileName the name of the file will be the additionalFileName parameter followed by the date and time it was created.
     *
     */
    public static void writeByEmailsSender (Map<String,List<Node>> emails, String additionalFileName) {
    	writeByEmails( emails, true, true, true, true, true, true, true, true, true, true, false, false, false, false,
    			false, false, false, false, false, false, false, false, false, false, false, additionalFileName );
    }
    
    /**
     * Creates and writes a CSV file containing ALL the values of the sender and ALL the statistical values
     * of the recipients of each respective email. The name of the file is the date and time it was created.
     *
     * @param emails a map in which each pair <String,List<Node> represents as key a String of an email identifier 
     * and as value a list of Nodes present in that email (either as sender or as receiver of the email).
     *
     */
    public static void writeByEmails (Map<String,List<Node>> emails) {
    	writeByEmails( emails, true, true, true, true, true, true, true, true, true, true, true, true, true, true, 
    			true, true, true, true, true, true, true, true, true, true, true, "" );
    }
    
    /**
     * Creates and writes a CSV file containing ALL the values of the sender and ALL the statistical values
     * of the recipients of each respective email. 
     * Additionally, the name of the file created is further specified: it is the additionalFileName parameter followed by
     * the date and time it was created.
     *
     * @param emails a map in which each pair <String,List<Node> represents as key a String of an email identifier 
     * and as value a list of Nodes present in that email (either as sender or as receiver of the email).
     *
     * @param additonalFileName the name of the file will be the additionalFileName parameter followed by the date and time it was created.
     *
     */
    public static void writeByEmails (Map<String,List<Node>> emails, String additionalFileName) {
    	writeByEmails( emails, true, true, true, true, true, true, true, true, true, true, true, true, true, true, 
    			true, true, true, true, true, true, true, true, true, true, true, additionalFileName );
    }
    
    /**
     * Creates and writes a CSV file containing values of the sender and statistical values
     * of the recipients of each respective email, for each value this function takes a respective boolean as parameter
     * indicating whether it should be written or omitted. The name of the file is the date and time it was created.
     *
     * @param emails a map in which each pair <String,List<Node> represents as key a String of an email identifier 
     * and as value a list of Nodes present in that email (either as sender or as receiver of the email).
     *
     */
    public static void writeByEmails( Map<String,List<Node>> emails, boolean writeSenderStandard, boolean writeSenderReversed, 
    		boolean writeSenderNeighborCentrality, boolean writeSenderStandardUnweightedEdges, boolean writeSenderReversedUnweightedEdges, 
    		boolean writeSenderNeighborCentralityUnweightedEdges, boolean writeSenderInDegree, boolean writeSenderOutDegree, 
    		boolean writeSenderUnweightedInDegree, boolean writeSenderUnweightedOutDegree, boolean writeStatsRecipientStandard, 
    		boolean writeStatsRecipientReversed, boolean writeStatsRecipientNeighborCentrality, boolean writeStatsRecipientStandardUnweightedEdges, 
    		boolean writeStatsRecipientReversedUnweightedEdges, boolean writeStatsRecipientNeighborCentralityUnweightedEdges, 
    		boolean writeStatsRecipientInDegree, boolean writeStatsRecipientOutDegree, boolean writeStatsRecipientUnweightedInDegree, 
    		boolean writeStatsRecipientUnweightedOutDegree, boolean writeMedian, boolean writeMean, boolean writeStandardDeviation, 
    		boolean writeHarmonicMean, boolean writeMeanAbsoluteDeviation ) {
    	
    	writeByEmails( emails,  writeSenderStandard,  writeSenderReversed, 
       		 writeSenderNeighborCentrality,  writeSenderStandardUnweightedEdges,  writeSenderReversedUnweightedEdges, 
       		 writeSenderNeighborCentralityUnweightedEdges,  writeSenderInDegree,  writeSenderOutDegree, 
       		 writeSenderUnweightedInDegree,  writeSenderUnweightedOutDegree,  writeStatsRecipientStandard, 
       		 writeStatsRecipientReversed,  writeStatsRecipientNeighborCentrality,  writeStatsRecipientStandardUnweightedEdges, 
       		 writeStatsRecipientReversedUnweightedEdges,  writeStatsRecipientNeighborCentralityUnweightedEdges, 
       		 writeStatsRecipientInDegree,  writeStatsRecipientOutDegree,  writeStatsRecipientUnweightedInDegree, 
       		 writeStatsRecipientUnweightedOutDegree,  writeMedian,  writeMean,  writeStandardDeviation, 
       		 writeHarmonicMean,  writeMeanAbsoluteDeviation, "" );
    }
    
    /**
     * Creates and writes a CSV file containing values of the sender and statistical values
     * of the recipients of each respective email, for each value this function takes a respective boolean as parameter
     * indicating whether it should be written or omitted.
     * Additionally, the name of the file created is further specified: it is the additionalFileName parameter followed by
     * the date and time it was created.
     *
     * @param emails a map in which each pair <String,List<Node> represents as key a String of an email identifier 
     * and as value a list of Nodes present in that email (either as sender or as receiver of the email).
     *
     * @param additonalFileName the name of the file will be the additionalFileName parameter followed by the date and time it was created.
     *
     */
    public static void writeByEmails( Map<String,List<Node>> emails, boolean writeSenderStandard, boolean writeSenderReversed, 
    		boolean writeSenderNeighborCentrality, boolean writeSenderStandardUnweightedEdges, boolean writeSenderReversedUnweightedEdges, 
    		boolean writeSenderNeighborCentralityUnweightedEdges, boolean writeSenderInDegree, boolean writeSenderOutDegree, 
    		boolean writeSenderUnweightedInDegree, boolean writeSenderUnweightedOutDegree, boolean writeStatsRecipientStandard, 
    		boolean writeStatsRecipientReversed, boolean writeStatsRecipientNeighborCentrality, boolean writeStatsRecipientStandardUnweightedEdges, 
    		boolean writeStatsRecipientReversedUnweightedEdges, boolean writeStatsRecipientNeighborCentralityUnweightedEdges, 
    		boolean writeStatsRecipientInDegree, boolean writeStatsRecipientOutDegree, boolean writeStatsRecipientUnweightedInDegree, 
    		boolean writeStatsRecipientUnweightedOutDegree, boolean writeMedian, boolean writeMean, boolean writeStandardDeviation, 
    		boolean writeHarmonicMean, boolean writeMeanAbsoluteDeviation, String additionalFileName ) {
    	
    	BufferedWriter writer = null;
        try {
            //create a temporary file
            String timeLog = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
            File logFile = new File(additionalFileName + timeLog);

            // prints the path in which the file will be created
            System.out.println("File created on path: "+logFile.getCanonicalPath());

            writer = new BufferedWriter(new FileWriter(logFile));
            
            for (String email : emails.keySet()) {
            	writer.write( email );
            	
            	// the Node sending the email;
            	Node sender = emails.get(email).get(0);
            	if (writeSenderStandard)				writer.write( "," + sender.getCentrality(centralityType.standard) );
            	if (writeSenderReversed)				writer.write( "," + sender.getCentrality(centralityType.reversed) );
            	if (writeSenderNeighborCentrality)		writer.write( "," + sender.getCentrality(centralityType.neighborCentrality) );
            	if (writeSenderStandardUnweightedEdges)	writer.write( "," + sender.getCentrality(centralityType.standardUnweightedEdges) );
            	if (writeSenderReversedUnweightedEdges)	writer.write( "," + sender.getCentrality(centralityType.reversedUnweightedEdges) );
            	if (writeSenderNeighborCentralityUnweightedEdges)	writer.write( "," + 
            														sender.getCentrality(centralityType.neighborCentralityUnweightedEdges) );
            	if (writeSenderInDegree)				writer.write( "," + sender.inDegree() );
            	if (writeSenderOutDegree)				writer.write( "," + sender.outDegree() );
            	if (writeSenderUnweightedInDegree)		writer.write( "," + sender.unweighedInDegree() );
            	if (writeSenderUnweightedOutDegree)		writer.write( "," + sender.unweighedOutDegree() );
            	
            	// removes the sender so that the remaining Collection has only (and all) the Nodes RECEIVING the email. 
            	emails.get(email).remove(0);
            	List<Node> recipients = emails.get(email);
            	
            	// LAMBDA-function used the print the next 6 statistical results.
            	BiConsumer<centralityType, BufferedWriter> write1 = (type, Bwriter) -> {
            			float[] tmp = getStatsFromCollection.getConditionalFloat(recipients, n -> n.getCentrality(type), 
            						  writeMedian, writeMean, writeStandardDeviation, writeHarmonicMean, writeMeanAbsoluteDeviation);
            			for ( float f : tmp )
            				if (f != -1)
								try {
									Bwriter.write( "," + f );
								} catch (IOException e) { e.printStackTrace(); }
            	};
            	
            	if(writeStatsRecipientStandard)								write1.accept(centralityType.standard, writer);
            	if(writeStatsRecipientReversed)								write1.accept(centralityType.reversed, writer);
            	if(writeStatsRecipientNeighborCentrality)					write1.accept(centralityType.neighborCentrality, writer);
            	if(writeStatsRecipientStandardUnweightedEdges)				write1.accept(centralityType.standardUnweightedEdges, writer);
            	if(writeStatsRecipientReversedUnweightedEdges)				write1.accept(centralityType.reversedUnweightedEdges, writer);
            	if(writeStatsRecipientNeighborCentralityUnweightedEdges)	write1.accept(centralityType.neighborCentralityUnweightedEdges, writer);
            
            	// LAMBDA-function used the print the next 4 statistical results.
            	BiConsumer<Function<Node,Integer>, BufferedWriter> write2 = (function, Bwriter) -> {
            		Int4Floats tmp = getStatsFromCollection.getConditionalInt(recipients, function, writeMedian, 
            						 writeMean, writeStandardDeviation, writeHarmonicMean, writeMeanAbsoluteDeviation);
            		try {
            			if (tmp.median != -1)					Bwriter.write( "," + tmp.median );
            			if (tmp.mean != -1)					Bwriter.write( "," + tmp.mean );
            			if (tmp.standardDeviation != -1)		Bwriter.write( "," + tmp.standardDeviation );
            			if (tmp.harmonicMean != -1)				Bwriter.write( "," + tmp.harmonicMean );
            			if (tmp.meanAbsoluteDeviation != -1)	Bwriter.write( "," + tmp.meanAbsoluteDeviation );
            		} catch (IOException e) {}
            	};
            	
            	if(writeStatsRecipientInDegree)					write2.accept(n -> n.inDegree(), writer);
            	if(writeStatsRecipientOutDegree)				write2.accept(n -> n.outDegree(), writer);
            	if(writeStatsRecipientUnweightedInDegree)		write2.accept(n -> n.unweighedInDegree(), writer);
            	if(writeStatsRecipientUnweightedOutDegree)		write2.accept(n -> n.unweighedOutDegree(), writer);
            	
            	// adds the sender back to the List of Nodes in the email, in the start of the List.
            	emails.get(email).add(0, sender);
            	
            	writer.newLine();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                // Close the writer regardless of what happens...
                writer.close();
            } catch (Exception e) {
            }
        }
        
    }

}