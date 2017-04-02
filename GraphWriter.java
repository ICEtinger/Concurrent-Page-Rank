
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;

public class GraphWriter {

	// each boolean represents whether or not the respective value should be written.
	static boolean writeCentrality;
	static boolean writeInDegree;
	static boolean writeOutDegree;
	static boolean writeFromNodes;
	static boolean writeToNodes;
	
    public static void write(Collection<Node> collectionOfNodes, Map<String,Node> dic) {
    	
        BufferedWriter writer = null;
        try {
            //create a temporary file
            String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());
            File logFile = new File(timeLog);

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
            	if (writeCentrality)	writer.write( "," + address.getCentrality() );
            	if (writeInDegree)		writer.write( "," + address.inDegree() );
            	if (writeOutDegree)		writer.write( "," + address.outDegree() );
            	
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
    
    public static void writeCentrality(Collection<Node> collectionOfNodes) {
    	writeCentrality = true;
    	write(collectionOfNodes, null);
    }
    
    public static void write( Collection<Node> collectionOfNodes, Map<String,Node> dic, boolean writeCentrality0, boolean writeInDegree0, 
    		                  boolean writeOutDegree0, boolean writeFromNodes0, boolean writeToNodes0 ) {
    	writeCentrality = writeCentrality0;
    	writeInDegree = writeInDegree0; 
    	writeOutDegree = writeOutDegree0;
    	writeFromNodes = writeFromNodes0;
    	writeToNodes = writeToNodes0;
    	
    	write(collectionOfNodes, dic);
    }
    
    public static void write( Collection<Node> collectionOfNodes,boolean writeCentrality0, boolean writeInDegree0, boolean writeOutDegree0 ) {
    	writeCentrality = writeCentrality0;
    	writeInDegree = writeInDegree0; 
    	writeOutDegree = writeOutDegree0;
    	
    	write(collectionOfNodes, null);
    }
    
}