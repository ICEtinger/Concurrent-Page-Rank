
/*******************

Created by Isak C. Etinger.

Last updated April 2017.

Merges all the lines from two CSV files.
If before(line) is the substring of line before the first occurrence of "," and after(line) is the substring after that ",",
then two lines line1, line2 are said correspondent to one another iff before(line1) = before(line2), and the merging of those
lines is said to be line1+","+after(line2).
 
There are no restrictions on the line order of the CSV files.

*******************/

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

public class SingleThreadCSVMerger {
	
	/**
     * Merges all the lines from the first CSV file with its corresponding line in the second CSV file using the Map-Reduce algorithm.
     * 
     * If before(line) is the substring of line before the first occurrence of "," and after(line) is the substring after that ",",
     * then two lines line1, line2 are said correspondent to one another iff before(line1) = before(line2), and the merging of those
     * lines is said to be line1+","+after(line2).
     * 
     * There are no restrictions on the line order of the CSV files.
     * 
     * @param csvFile1 the first CSV file to be merged.
     *
     * @param csvFile2 the second CSV file to be merged.
     *
     */
	public static void merge(String csvFile1, String csvFile2) throws InterruptedException {
		
		// map1 is for the csvFile1, map2 is for the csvFile2.
		HashMap<String,String> map1 = new HashMap<String,String>();
		HashMap<String,String> map2 = new HashMap<String,String>();
		
		// variable to read files
        String line = "";
        
        int LineCounter1 = 0;
        int LineCounter2 = 0;

        
        // read file 1.
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile1))) {
	    
			while ( (line = br.readLine()) != null ) {
				// splits the String into 2 Strings: before and after the first occurrence of ",".
				String[] beforeAfter = line.split(",",2);  
				map1.put(beforeAfter[0], beforeAfter[1]);
				LineCounter1++;	
			}
			
		} catch (IOException e) { e.printStackTrace(); }
		
		// read file 2
		try (BufferedReader br = new BufferedReader(new FileReader(csvFile2))) {
			
			while ( (line = br.readLine()) != null ) {
				// splits the String into 2 Strings: before and after the first occurrence of ",".
				String[] beforeAfter = line.split(",",2);  
				map2.put(beforeAfter[0], beforeAfter[1]);
				LineCounter2++;	
			}
			
		} catch (IOException e) { e.printStackTrace(); }
		
		// prints the number of lines of each file.
		System.out.println( LineCounter1 == LineCounter2 ? ("Both files have "+LineCounter1+" lines.") :
		                                                   ("file1 has "+LineCounter1+" lines, file2 has "+LineCounter2+".") );
		
		// merges the maps.
		for ( String key : map1.keySet() ) {
			//s1 = String mapped to key in map1. s2 = String mapped to key in map2. Sets s1 to s1+","+s2.
			String tmp = map2.get(key);
			if (tmp != null)
				map1.merge(key, tmp, (s1,s2) -> s1+","+s2  );
		}
				
		// write file.
		BufferedWriter writer = null;
	    try {
	    	//create a temporary file
	    	String timeLog = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(Calendar.getInstance().getTime());
	    	File logFile = new File("MergedCSV_" + timeLog);

	    	// prints the path in which the file will be created
	    	System.out.println("File created on path: "+logFile.getCanonicalPath());

	    	writer = new BufferedWriter(new FileWriter(logFile));
	        
	    	// writes each line in the file.
	    	for ( String key : map1.keySet() ) {
	    		writer.write(key+","+map1.get(key));
	    		writer.newLine();
	    	}
	    	    
	        } catch (Exception e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                // Close the writer regardless of what happens...
	                writer.close();
	            } catch (Exception e) {}
	    }
	    
	}
		
}
