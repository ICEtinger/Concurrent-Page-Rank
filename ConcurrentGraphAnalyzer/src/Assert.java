
/*******************

Created by Isak C. Etinger.

Last updated April 2017.

*******************/

public class Assert {
	
	/**
     * Checks if the condition is met. If it is not, it prints a specified error message in the console.
     *
     * @param condition the condition to be checked.
     * 
     * @param messageError the error message to be printed in the console if the specified condition is not met.
     *
     */
	static void check(boolean condition, String messageError) {
		if (!condition)
			System.out.println("Error in checking values: "+messageError);
	}
	
	/**
     * Checks if the condition is met. If it is not, it prints a error message in the console.
     *
     * @param condition the condition to be checked.
     *
     */
	static void check(boolean condition) {
		if (!condition)
			System.out.println("Error in checking values.");
	}
	
}
