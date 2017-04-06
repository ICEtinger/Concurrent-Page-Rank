
/*******************

Created by Isak C. Etinger.

Last updated April 2017.

This class supports merging all the lines from two CSV files, using either the Map-Reduce algorithm or a single-thread algorithm.
The algorithm with which the files will be merged is defined in the boolean parameter "useMultiThread".

If before(line) is the substring of line before the first occurrence of "," and after(line) is the substring after that ",",
then two lines line1, line2 are said correspondent to one another iff before(line1) = before(line2), and the merging of those
lines is said to be line1+","+after(line2). 
There are no restrictions on the line order of the CSV files.

*******************/

public class MergeCSV {
	
	private static final boolean useMultiThread = false;
	
	public static void main(String[] args) throws InterruptedException {
		
		String csvFile1 = "CSVtest1.txt"; /***** CHANGE NAME OF THE FILE HERE *****/
		String csvFile2 = "CSVtest2.txt"; /***** CHANGE NAME OF THE FILE HERE *****/
		
		long startTime = System.currentTimeMillis();
		
		if (useMultiThread)
			MapReduceCSVMerger.merge(csvFile1, csvFile2);
		else
			SingleThreadCSVMerger.merge(csvFile1, csvFile2);
		
		System.out.println("Total time taken (in seconds): "+ (float)(System.currentTimeMillis() - startTime) / 1000 );
	
	}
	
}

