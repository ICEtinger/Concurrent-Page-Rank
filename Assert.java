
public class Assert {
	
	static void check(boolean condition, String messageError) {
		if (!condition)
			System.out.println("Error in checking values: "+messageError);
	}
	
	static void check(boolean condition) {
		if (!condition)
			System.out.println("Error in checking values.");
	}
	
}
