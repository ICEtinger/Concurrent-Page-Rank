
/*******************

Created by Isak C. Etinger.

Last updated April 2017.

This class provides methods for extracting statistical results from Collections and Arrays.
It supports calculating statistical results of methods of elements in a Collection.
i.e.: statistical results from the values of all E.method() in a Collection<E>.

The statistical results are: the median, the mean, the standard deviation, the harmonic mean, the mean absolute deviation, and the k-th 
smallest element.

It receives as PARAMETER: a COLLECTION and a METHOD (that has itself as parameter elements of that Collection).
It's recommended using LAMBDA-Expressions for this.
e.g.: for a Collection<Node> graph in which each Node has a method long value(), one can either:
      * call directly getAllLong(graph,n -> n.value()), or
      * create a Function<Node,Long> value = n -> n.value(); and then call getAllLong(graph,value).
      Any of those returns an instance of the class Long4Floats having the median, the mean, the standard deviation, 
      the harmonic mean and the mean absolute deviation of the n.value()'s of the nodes in the Collection.

It calculates up to 5 statistical results at once to save the work of creating an auxiliary array for each calculation and
recalculating the mean, that is itself used to calculate the standard deviation and the mean absolute deviation.

This class presents:
1) Methods taking an array as parameter, straightforward and with low-verbose.
   There are replicate methods for each array of {int, float, long, double} as parameters, all with the same name.
2) Methods taking as parameter a Collection<E> and another method from E.
   Due to Java language specifications, any further generic type information than Function<?,?> will be erased at runtime.
   So it's impossible to differentiate e.g. Function<E,Integer> and Function<E,Float> as parameters of a method.
   (e.g. using "Function<E,Number>" as parameter and then "function instanceof Function<E,Integer>" does not work)
   So for those methods this class has a different name for each {int, float, long, double}. 
   e.g. getAllInt(Collection<E>,Function(E,Integer)) and getAllFloat(Collection<E>,Function(E,Float)).

This class also provide many methods in different levels of verbose and complexity so that users can choose exactly which statistical
values shall be calculated, and also having all methods available for {int, float, long, double} so they can be used with sufficient precision 
but without unnecessary computation and memory usage.
The median and the k-th smallest element have always the same type of the array. All other results are given in floats for {int, float, long} or double for {double}.

The k-th smallest element is calculated with the Quick Select algorithm using an auxiliary array.

*******************/

import java.util.Collection;
import java.util.function.Function;

// classes used to return multiple statistical results of different types at once.
// For example, having as parameter a Collection of Nodes (each one with an long method),
// this class can calculate the median (long), mean (float) and standard deviation (float) of the values produced by those methods,
// and return it as a reference to a newly created instance of LongFloatFloat.

class Long4Floats {
	long median;
	float mean;
	float standardDeviation;
	float harmonicMean;
	float meanAbsoluteDeviation;
	Long4Floats(long median, float mean, float standardDeviation, float harmonicMean, float meanAbsoluteDeviation) {
		this.median = median;
		this.mean = mean;
		this.standardDeviation = standardDeviation;
		this.harmonicMean = harmonicMean;
		this.meanAbsoluteDeviation = meanAbsoluteDeviation;
	}
}

class Int4Floats {
	int median;
	float mean;
	float standardDeviation;
	float harmonicMean;
	float meanAbsoluteDeviation;
	Int4Floats(int median, float mean, float standardDeviation, float harmonicMean, float meanAbsoluteDeviation) {
		this.median = median;
		this.mean = mean;
		this.standardDeviation = standardDeviation;
		this.harmonicMean = harmonicMean;
		this.meanAbsoluteDeviation = meanAbsoluteDeviation;
	}
}

class LongFloatFloat {
	long l;
	float f1;
	float f2;
	LongFloatFloat( long l, float f1, float f2) { this.l = l; this.f1 = f1; this.f2 = f2; }
}

class LongFloat {
	long l;
	float f;
	LongFloat( long l, float f ) { this.l = l; this.f = f; }
}

class IntFloatFloat {
	int i;
	float f1;
	float f2;
	IntFloatFloat( int i, float f1, float f2) { this.i = i; this.f1 = f1; this.f2 = f2; }
}

class IntFloat {
	int i;
	float f;
	IntFloat( int i, float f) { this.i = i;	this.f = f;	}
}

public class getStatsFromCollection {
	
	/**
     * Gets the statistical results specified by the boolean parameters.
     * Use with a function that returns a float. 
     * For a less verbose method, use getAllFloat or getMedianMeanStandardDeviationFloat. 
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type float will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
	 * 
	 * @implNote Only statistical values specified in the parameters are calculated.
     *
     */
	static public <E> float[] getConditionalFloat(Collection<E> collection, Function<E,Float> function, boolean getMedian, boolean getMean,
			boolean getStandardDeviation, boolean getHarmonicMean, boolean getMeanAbsoluteDeviation) {
		float[] arr = new float[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getConditional(arr, getMedian, getMean, getStandardDeviation, getHarmonicMean, getMeanAbsoluteDeviation);
	}
	
	/**
     * Gets the statistical results specified by the boolean parameters.
     * Use with a function that returns a int. 
     * For a less verbose method, use getAllInt or getMedianMeanStandardDeviationInt. 
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type int will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
	 * 
	 * @implNote Only statistical values specified in the parameters are calculated.
     *
     */
	static public <E> Int4Floats getConditionalInt(Collection<E> collection, Function<E,Integer> function, boolean getMedian, boolean getMean,
			boolean getStandardDeviation, boolean getHarmonicMean, boolean getMeanAbsoluteDeviation) {
		int[] arr = new int[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getConditional(arr, getMedian, getMean, getStandardDeviation, getHarmonicMean, getMeanAbsoluteDeviation);
	}
	
	/**
     * Gets the statistical results specified by the boolean parameters.
     * Use with a function that returns a double. 
     * For a less verbose method, use getAllDouble or getMedianMeanStandardDeviationDouble. 
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
	 * 
	 * @implNote Only statistical values specified in the parameters are calculated.
     *
     */
	static public <E> double[] getConditionalDouble(Collection<E> collection, Function<E,Double> function, boolean getMedian, boolean getMean,
			boolean getStandardDeviation, boolean getHarmonicMean, boolean getMeanAbsoluteDeviation) {
		double[] arr = new double[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getConditional(arr, getMedian, getMean, getStandardDeviation, getHarmonicMean, getMeanAbsoluteDeviation);
	}
	
	/**
     * Gets the statistical results specified by the boolean parameters.
     * Use with a function that returns a long. 
     * For a less verbose method, use getAllLong or getMedianMeanStandardDeviationLong. 
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type long will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
	 * 
	 * @implNote Only statistical values specified in the parameters are calculated.
     *
     */
	static public <E> Long4Floats getConditionalLong(Collection<E> collection, Function<E,Integer> function, boolean getMedian, boolean getMean,
			boolean getStandardDeviation, boolean getHarmonicMean, boolean getMeanAbsoluteDeviation) {
		long[] arr = new long[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getConditional(arr, getMedian, getMean, getStandardDeviation, getHarmonicMean, getMeanAbsoluteDeviation);
	}
	
	/**
     * Gets the statistical results specified by the boolean parameters.
     * For a less verbose method, use getAll or getMedianMeanStandardDeviation. 
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public float[] getConditional( float[] arr, boolean getMedian, boolean getMean,
			boolean getStandardDeviation, boolean getHarmonicMean, boolean getMeanAbsoluteDeviation) {
		float mean = ( (getMean || getStandardDeviation || getMeanAbsoluteDeviation) ? getMean(arr) : -1 );
		float median = (getMedian ? getMedian(arr) : -1);
		float harmonicMean = (getHarmonicMean ? getHarmonicMean(arr) : -1);
		float standardDeviation = (getStandardDeviation ? getStandardDeviation(arr,mean) : -1);
		float meanAbsoluteDeviation = (getMeanAbsoluteDeviation ? getMeanAbsoluteDeviation(arr,mean) : -1);
		
		float[] tmp = { median, mean, standardDeviation, harmonicMean, meanAbsoluteDeviation };
		return tmp;
	}
	
	/**
     * Gets the statistical results specified by the boolean parameters.
     * For a less verbose method, use getAll or getMedianMeanStandardDeviation. 
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public Int4Floats getConditional( int[] arr, boolean getMedian, boolean getMean,
			boolean getStandardDeviation, boolean getHarmonicMean, boolean getMeanAbsoluteDeviation) {
		float mean = ( (getMean || getStandardDeviation || getMeanAbsoluteDeviation) ? getMean(arr) : -1 );
		int median = (getMedian ? getMedian(arr) : -1);
		float harmonicMean = (getHarmonicMean ? getHarmonicMean(arr) : -1);
		float standardDeviation = (getStandardDeviation ? getStandardDeviation(arr,mean) : -1);
		float meanAbsoluteDeviation = (getMeanAbsoluteDeviation ? getMeanAbsoluteDeviation(arr,mean) : -1);
		
		return new Int4Floats( median, mean, standardDeviation, harmonicMean, meanAbsoluteDeviation );
	}
	
	/**
     * Gets the statistical results specified by the boolean parameters.
     * For a less verbose method, use getAll or getMedianMeanStandardDeviation. 
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public double[] getConditional( double[] arr, boolean getMedian, boolean getMean,
			boolean getStandardDeviation, boolean getHarmonicMean, boolean getMeanAbsoluteDeviation) {
		double mean = ( (getMean || getStandardDeviation || getMeanAbsoluteDeviation) ? getMean(arr) : -1 );
		double median = (getMedian ? getMedian(arr) : -1);
		double harmonicMean = (getHarmonicMean ? getHarmonicMean(arr) : -1);
		double standardDeviation = (getStandardDeviation ? getStandardDeviation(arr,mean) : -1);
		double meanAbsoluteDeviation = (getMeanAbsoluteDeviation ? getMeanAbsoluteDeviation(arr,mean) : -1);
		
		double[] tmp = { median, mean, standardDeviation, harmonicMean, meanAbsoluteDeviation };
		return tmp;
	}
	
	/**
     * Gets the statistical results specified by the boolean parameters.
     * For a less verbose method, use getAll or getMedianMeanStandardDeviation. 
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public Long4Floats getConditional( long[] arr, boolean getMedian, boolean getMean,
			boolean getStandardDeviation, boolean getHarmonicMean, boolean getMeanAbsoluteDeviation) {
		float mean = ( (getMean || getStandardDeviation || getMeanAbsoluteDeviation) ? getMean(arr) : -1 );
		long median = (getMedian ? getMedian(arr) : -1);
		float harmonicMean = (getHarmonicMean ? getHarmonicMean(arr) : -1);
		float standardDeviation = (getStandardDeviation ? getStandardDeviation(arr,mean) : -1);
		float meanAbsoluteDeviation = (getMeanAbsoluteDeviation ? getMeanAbsoluteDeviation(arr,mean) : -1);
		
		return new Long4Floats( median, mean, standardDeviation, harmonicMean, meanAbsoluteDeviation );
	}

	/**
     * Gets all the statistical results at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type float will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public <E> float[] getAllFloat(Collection<E> collection, Function<E,Float> function) {
		float[] arr = new float[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getAll(arr);
	}
	
	/**
     * Gets all the statistical results at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type int will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public <E> Long4Floats getAllInt(Collection<E> collection, Function<E,Integer> function) {
		int[] arr = new int[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getAll(arr);
	}
	
	/**
     * Gets all the statistical results at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public <E> double[] getAllDouble(Collection<E> collection, Function<E,Double> function) {
		double[] arr = new double[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getAll(arr);
	}
	
	/**
     * Gets all the statistical results at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type long will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public <E> Long4Floats getAllLong(Collection<E> collection, Function<E,Long> function) {
		long[] arr = new long[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getAll(arr);
	}
	
	/**
     * Gets all the statistical results at once.
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public float[] getAll(float[] arr) {
		float mean = getMean(arr);
		float[] tmp = { getMedian(arr), mean, getStandardDeviation(arr,mean), getHarmonicMean(arr), getMeanAbsoluteDeviation(arr,mean) };
		return tmp;
	}
	
	/**
     * Gets all the statistical results at once.
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public Long4Floats getAll(int[] arr) {
		float mean = getMean(arr);
		return new Long4Floats(getMedian(arr), mean, getStandardDeviation(arr,mean), getHarmonicMean(arr), getMeanAbsoluteDeviation(arr,mean));
	}
	
	/**
     * Gets all the statistical results at once.
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public double[] getAll(double[] arr) {
		double mean = getMean(arr);
		double[] tmp = { getMedian(arr), mean, getStandardDeviation(arr,mean), getHarmonicMean(arr), getMeanAbsoluteDeviation(arr,mean) };
		return tmp;
	}
	
	/**
     * Gets all the statistical results at once.
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean, Standard Deviation, Harmonic Mean, 
     * and Mean Absolute Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating 
	 * the mean multiple times. 
     *
     */
	static public Long4Floats getAll(long[] arr) {
		float mean = getMean(arr);
		return new Long4Floats(getMedian(arr), mean, getStandardDeviation(arr,mean), getHarmonicMean(arr), getMeanAbsoluteDeviation(arr,mean));
	}
	
	/**
     * Gets the median, the mean and the standard deviation at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type float will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating the mean multiple times. 
     *
     */
	static public <E> float[] getMedianMeanStandardDeviationFloat(Collection<E> collection, Function<E,Float> function) {
		float[] arr = new float[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedianMeanStandardDeviaton(arr);
	}
	
	/**
     * Gets the median, the mean and the standard deviation at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type int will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating the mean multiple times. 
     *
     */
	static public <E> IntFloatFloat getMedianMeanStandardDeviationInteger(Collection<E> collection, Function<E,Integer> function) {
		int[] arr = new int[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedianMeanStandardDeviaton(arr);
	}
	
	/**
     * Gets the median, the mean and the standard deviation at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type long will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating the mean multiple times. 
     *
     */
	static public <E> LongFloatFloat getMedianMeanStandardDeviationLong(Collection<E> collection, Function<E,Long> function) {
		long[] arr = new long[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedianMeanStandardDeviaton(arr);
	}
	
	/**
     * Gets the median, the mean and the standard deviation at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     * @implNote Getting Median, Mean and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating the mean multiple times. 
     *
     */
	static public <E> double[] getMedianMeanStandardDeviationDouble(Collection<E> collection, Function<E,Double> function) {
		double[] arr = new double[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedianMeanStandardDeviaton(arr);
	}
	
	/**
     * Gets the an array of size 3 with the values of 
     * { median(arr), mean(arr), standardDeviation(arr) }
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating the mean multiple times. 
     *
     */
	static public float[] getMedianMeanStandardDeviaton(float[] arr) {
		float[] tmp = {getMedian(arr), getMean(arr), getStandardDeviation(arr)};
		return tmp;
	}
	
	/**
     * Gets the an array of size 3 with the values of 
     * { median(arr), mean(arr), standardDeviation(arr) }
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating the mean multiple times. 
     *
     */
	static public IntFloatFloat getMedianMeanStandardDeviaton(int[] arr) {
		return new IntFloatFloat ( getMedian(arr), getMean(arr), getStandardDeviation(arr) );
	}
	
	/**
     * Gets the an array of size 3 with the values of 
     * { median(arr), mean(arr), standardDeviation(arr) }
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating the mean multiple times. 
     *
     */
	static public double[] getMedianMeanStandardDeviaton(double[] arr) {
		double[] tmp = {getMedian(arr), getMean(arr), getStandardDeviation(arr)};
		return tmp;
	}
	
	/**
     * Gets the an array of size 3 with the values of 
     * { median(arr), mean(arr), standardDeviation(arr) }
     *
     * @param arr the array from where to take the statistical results.
     *
     * @implNote Getting Median, Mean and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array and of calculating the mean multiple times. 
     *
     */
	static public LongFloatFloat getMedianMeanStandardDeviaton(long[] arr) {
		return new LongFloatFloat ( (int)getMedian(arr), getMean(arr), getStandardDeviation(arr) );
	}
	
	// gets the standard-deviation, defined by:
	// sqrt( mean( (x[i] - mean(x[i]))� ) )
	
	/**
     * Gets the standard-deviation, defined by:
	 * sqrt( mean( (x[i] - mean(x[i]))� ) )
     *
     * @param arr the array from where to take the statistical results.
     *
     */
	static public float getStandardDeviation(float[] arr) {
		float mean = getMean(arr);
		float tmp = 0;
		for ( float f : arr ) {
			tmp += (f - mean) * (f - mean);
		}
		return (float)Math.sqrt(tmp/arr.length);
	}
	
	/**
     * Gets the standard-deviation, defined by:
	 * sqrt( mean( (x[i] - mean(x[i]))� ) )
     *
     * @param arr the array from where to take the statistical results.
     *
     */
	static public float getStandardDeviation(int[] arr) {
		float mean = getMean(arr);
		float tmp = 0;
		for ( float f : arr ) {
			tmp += (f - mean) * (f - mean);
		}
		return (float)Math.sqrt(tmp/arr.length);
	}
	
	/**
     * Gets the standard-deviation, defined by:
	 * sqrt( mean( (x[i] - mean(x[i]))� ) )
     *
     * @param arr the array from where to take the statistical results.
     *
     */
	static public float getStandardDeviation(long[] arr) {
		float mean = getMean(arr);
		float tmp = 0;
		for ( float f : arr ) {
			tmp += (f - mean) * (f - mean);
		}
		return (float)Math.sqrt(tmp/arr.length);
	}
	
	/**
     * Gets the standard-deviation, defined by:
	 * sqrt( mean( (x[i] - mean(x[i]))� ) )
     *
     * @param arr the array from where to take the statistical results.
     *
     */
	static public double getStandardDeviation(double[] arr) {
		double mean = getMean(arr);
		double tmp = 0;
		for ( double f : arr ) {
			tmp += (f - mean) * (f - mean);
		}
		return Math.sqrt(tmp/arr.length);
	}
	
	// receives the already-calculated mean of the array as parameter to save the operations of calculating it,
	// thus approximately halfing the number of required operations.
	// this method is called when another method is called to calculate the standard deviation and the mean at once.
	
	/**
     * Receives the already-calculated mean of the array as parameter to save the operations of calculating it,
	 * thus approximately halfing the number of required operations.
     *
     * @param arr the array from where to take the statistical results.
     * @param mean the already-calculated mean of arr.
     *
     * @implNote this method is called when another method is called to calculate the standard deviation and the mean at once.
     *
     */
	static public float getStandardDeviation(float[] arr, float mean) {
		float tmp = 0;
		for ( float f : arr ) {
			tmp += (f - mean) * (f - mean);
		}
		return (float)Math.sqrt(tmp/arr.length);
	}
	
	/**
     * Receives the already-calculated mean of the array as parameter to save the operations of calculating it,
	 * thus approximately halfing the number of required operations.
     *
     * @param arr the array from where to take the statistical results.
     * @param mean the already-calculated mean of arr.
     *
     * @implNote this method is called when another method is called to calculate the standard deviation and the mean at once.
     *
     */
	static public float getStandardDeviation(int[] arr, float mean) {
		float tmp = 0;
		for ( float f : arr ) {
			tmp += (f - mean) * (f - mean);
		}
		return (float)Math.sqrt(tmp/arr.length);
	}
	
	/**
     * Receives the already-calculated mean of the array as parameter to save the operations of calculating it,
	 * thus approximately halfing the number of required operations.
     *
     * @param arr the array from where to take the statistical results.
     * @param mean the already-calculated mean of arr.
     *
     * @implNote this method is called when another method is called to calculate the standard deviation and the mean at once.
     *
     */
	static public float getStandardDeviation(long[] arr, float mean) {
		float tmp = 0;
		for ( float f : arr ) {
			tmp += (f - mean) * (f - mean);
		}
		return (float)Math.sqrt(tmp/arr.length);
	}
	
	/**
     * Receives the already-calculated mean of the array as parameter to save the operations of calculating it,
	 * thus approximately halfing the number of required operations.
     *
     * @param arr the array from where to take the statistical results.
     * @param mean the already-calculated mean of arr.
     *
     * @implNote this method is called when another method is called to calculate the standard deviation and the mean at once.
     *
     */
	static public double getStandardDeviation(double[] arr, double mean) {
		double tmp = 0;
		for ( double f : arr ) {
			tmp += (f - mean) * (f - mean);
		}
		return Math.sqrt(tmp/arr.length);
	}
	
	/**
     * Gets the Mean Absolute Deviation, defined by:
	 * mean( | x[i] - mean(x[i]) | )
     *
     * @param arr the array from where to take the statistical results.
     *
     */
	static public float getMeanAbsoluteDeviation(float[] arr) {
		float mean = getMean(arr);
		float tmp = 0;
		for ( float f : arr ) {
			tmp += Math.abs(f - mean);
		}
		return tmp/arr.length;
	}
	
	/**
     * Gets the Mean Absolute Deviation, defined by:
	 * mean( | x[i] - mean(x[i]) | )
     *
     * @param arr the array from where to take the statistical results.
     *
     */
	static public float getMeanAbsoluteDeviation(int[] arr) {
		float mean = getMean(arr);
		float tmp = 0;
		for ( float f : arr ) {
			tmp += Math.abs(f - mean);
		}
		return tmp/arr.length;
	}
	
	/**
     * Gets the Mean Absolute Deviation, defined by:
	 * mean( | x[i] - mean(x[i]) | )
     *
     * @param arr the array from where to take the statistical results.
     *
     */
	static public float getMeanAbsoluteDeviation(long[] arr) {
		float mean = getMean(arr);
		float tmp = 0;
		for ( float f : arr ) {
			tmp += Math.abs(f - mean);
		}
		return tmp/arr.length;
	}
	
	/**
     * Gets the Mean Absolute Deviation, defined by:
	 * mean( | x[i] - mean(x[i]) | )
     *
     * @param arr the array from where to take the statistical results.
     *
     */
	static public double getMeanAbsoluteDeviation(double[] arr) {
		double mean = getMean(arr);
		double tmp = 0;
		for ( double f : arr ) {
			tmp += Math.abs(f - mean);
		}
		return tmp/arr.length;
	}
	
	/**
     * Receives the already-calculated mean of the array as parameter to save the operations of calculating it,
	 * thus approximately halfing the number of required operations.
     *
     * @param arr the array from where to take the statistical results.
     * 
     * @param mean the already-calculated mean of arr.
     *
     * @implNote this method is called when another method is called to calculate the mean absolute deviation and the mean at once.
     *
     */
	static public float getMeanAbsoluteDeviation(float[] arr, float mean) {
		float tmp = 0;
		for ( float f : arr ) {
			tmp += Math.abs(f - mean);
		}
		return tmp/arr.length;
	}
	
	/**
     * Receives the already-calculated mean of the array as parameter to save the operations of calculating it,
	 * thus approximately halfing the number of required operations.
     *
     * @param arr the array from where to take the statistical results.
     * 
     * @param mean the already-calculated mean of arr.
     *
     * @implNote this method is called when another method is called to calculate the mean absolute deviation and the mean at once.
     *
     */
	static public float getMeanAbsoluteDeviation(int[] arr, float mean) {
		float tmp = 0;
		for ( float f : arr ) {
			tmp += Math.abs(f - mean);
		}
		return tmp/arr.length;
	}
	
	/**
     * Receives the already-calculated mean of the array as parameter to save the operations of calculating it,
	 * thus approximately halfing the number of required operations.
     *
     * @param arr the array from where to take the statistical results.
     * 
     * @param mean the already-calculated mean of arr.
     *
     * @implNote this method is called when another method is called to calculate the mean absolute deviation and the mean at once.
     *
     */
	static public float getMeanAbsoluteDeviation(long[] arr, float mean) {
		float tmp = 0;
		for ( float f : arr ) {
			tmp += Math.abs(f - mean);
		}
		return tmp/arr.length;
	}
	
	/**
     * Receives the already-calculated mean of the array as parameter to save the operations of calculating it,
	 * thus approximately halfing the number of required operations.
     *
     * @param arr the array from where to take the statistical results.
     * 
     * @param mean the already-calculated mean of arr.
     *
     * @implNote this method is called when another method is called to calculate the mean absolute deviation and the mean at once.
     *
     */
	static public double getMeanAbsoluteDeviation(double[] arr, double mean) {
		double tmp = 0;
		for ( double f : arr ) {
			tmp += Math.abs(f - mean);
		}
		return tmp/arr.length;
	}
	
	/**
     * Gets the median and the mean at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     * @implNote Getting Median and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array multiple times. 
     *
     */
	static public <E> float[] getMedianAndMeanFloat(Collection<E> collection, Function<E,Float> function) {
		float[] arr = new float[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedianAndMean(arr);
	}
	
	/**
     * Gets the median and the mean at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     * @implNote Getting Median and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array multiple times. 
     *
     */
	static public <E> IntFloat getMedianAndMeanInt(Collection<E> collection, Function<E,Integer> function) {
		int[] arr = new int[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedianAndMean(arr);
	}
	
	/**
     * Gets the median and the mean at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     * @implNote Getting Median and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array multiple times. 
     *
     */
	static public <E> double[] getMedianAndMeanDouble(Collection<E> collection, Function<E,Double> function) {
		double[] arr = new double[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedianAndMean(arr);
	}
	
	/**
     * Gets the median and the mean at once.
     *
     * @param collection a Collection of E
     *
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     * @implNote Getting Median and Standard Deviation at once is to save the work of copying 
	 * Collection<E> collection to a newly created array multiple times. 
     *
     */
	static public <E> LongFloat getMedianAndMeanLong(Collection<E> collection, Function<E,Long> function) {
		long[] arr = new long[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedianAndMean(arr);
	}
	
	/**
     * Gets the median of the results of a Function over a Collection.
     *
     * @param collection a Collection of E
     * 
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     */
	static public <E> float getMedianFloat(Collection<E> collection, Function<E,Float> function) {
		float[] arr = new float[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedian(arr);
	}
	
	/**
     * Gets the median of the results of a Function over a Collection.
     *
     * @param collection a Collection of E
     * 
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     */
	static public <E> int getMedianInt(Collection<E> collection, Function<E,Integer> function) {
		int[] arr = new int[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedian(arr);
	}
	
	/**
     * Gets the median of the results of a Function over a Collection.
     *
     * @param collection a Collection of E
     * 
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     */
	static public <E> double getMedianDouble(Collection<E> collection, Function<E,Integer> function) {
		double[] arr = new double[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedian(arr);
	}
	
	/**
     * Gets the median of the results of a Function over a Collection.
     *
     * @param collection a Collection of E
     * 
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     */
	static public <E> long getMedianLong(Collection<E> collection, Function<E,Long> function) {
		long[] arr = new long[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMedian(arr);
	}
	
	/**
     * Gets the mean of the results of a Function over a Collection.
     *
     * @param collection a Collection of E
     * 
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     */
	static public <E> float getMeanFloat(Collection<E> collection, Function<E,Float> function) {
		float[] arr = new float[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMean(arr);
	}
	
	/**
     * Gets the mean of the results of a Function over a Collection.
     *
     * @param collection a Collection of E
     * 
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     */
	static public <E> float getMeanInt(Collection<E> collection, Function<E,Integer> function) {
		int[] arr = new int[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMean(arr);
	}
	
	/**
     * Gets the mean of the results of a Function over a Collection.
     *
     * @param collection a Collection of E
     * 
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     */
	static public <E> double getMeanDouble(Collection<E> collection, Function<E,Double> function) {
		double[] arr = new double[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMean(arr);
	}
	
	/**
     * Gets the mean of the results of a Function over a Collection.
     *
     * @param collection a Collection of E
     * 
     * @param function a Function that takes E as parameter and whose results of type double will be subject to statistical analysis.
     *
     */
	static public <E> float getMeanLong(Collection<E> collection, Function<E,Long> function) {
		long[] arr = new long[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return getMean(arr);
	}
	
	/**
     * Gets the median and the mean at once.
     *
     * @param arr the array from where to take the statistical results.
     * 
     */
	static public float[] getMedianAndMean(float[] arr) {
		float[] tmp = {getMedian(arr), getMean(arr)};
		return tmp;
	}
	
	/**
     * Gets the median and the mean at once.
     *
     * @param arr the array from where to take the statistical results.
     * 
     */
	static public IntFloat getMedianAndMean(int[] arr) {
		return new IntFloat( getMedian(arr), getMean(arr) );
	}
	
	/**
     * Gets the median and the mean at once.
     *
     * @param arr the array from where to take the statistical results.
     * 
     */
	static public double[] getMedianAndMean(double[] arr) {
		double[] tmp = {getMedian(arr), getMean(arr)};
		return tmp;
	}
	
	/**
     * Gets the median and the mean at once.
     *
     * @param arr the array from where to take the statistical results.
     * 
     */
	static public LongFloat getMedianAndMean(long[] arr) {
		return new LongFloat( getMedian(arr), getMean(arr) );
	}

		
	/**
     * Gets the (k+1)th smallest element in arr.
     *
     * @param arr the array from where to select the element.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the array arr. 
     * 
     */
	public static int QuickSelect(int[] arr, int k) {
		return QuickSelect(arr, 0, arr.length - 1, k);
	}
	
	/**
     * Gets the (k+1)th smallest element in arr.
     *
     * @param arr the array from where to select the element.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the array arr. 
     * 
     */
	public static float QuickSelect(float[] arr, int k) {
		return QuickSelect(arr, 0, arr.length - 1, k);
	}
	
	/**
     * Gets the (k+1)th smallest element in arr.
     *
     * @param arr the array from where to select the element.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the array arr. 
     * 
     */
	public static double QuickSelect(double[] arr, int k) {
		return QuickSelect(arr, 0, arr.length - 1, k);
	}
	
	/**
     * Gets the (k+1)th smallest element in arr.
     *
     * @param arr the array from where to select the element.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the array arr. 
     * 
     */
	public static long QuickSelect(long[] arr, int k) {
		return QuickSelect(arr, 0, arr.length - 1, k);
	}
	
	/**
     * Gets the (k+1)th smallest number in the subarray arr[left <= i <= right]
     *
     * @param arr the array from where to select the element.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the array arr. 
     * 
     */
	public static int QuickSelect(int[] arr, int left, int right, int k) {
		// returns the element if it's the only one remaining.
		if (left == right) {
			return arr[left];
		}
		
		// select a pivotIndex between left and right
		int pivotIndex = randomPivot(left, right); 
		pivotIndex = partition(arr, left, right, pivotIndex);
		// The pivot is in its final sorted position
		if (k == pivotIndex) {
			return arr[k];
		} else if (k < pivotIndex) {
			return QuickSelect(arr, left, pivotIndex - 1, k);
		} else {
			return QuickSelect(arr, pivotIndex + 1, right, k);
		}
	}
	
	/**
     * Gets the (k+1)th smallest number in the subarray arr[left <= i <= right]
     *
     * @param arr the array from where to select the element.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the array arr. 
     * 
     */
	public static float QuickSelect(float[] arr, int left, int right, int k) {
		// returns the element if it's the only one remaining.
		if (left == right) {
			return arr[left];
		}
		
		// select a pivotIndex between left and right
		int pivotIndex = randomPivot(left, right);
		pivotIndex = partition(arr, left, right, pivotIndex);
		// The pivot is in its final sorted position
		if (k == pivotIndex) {
			return arr[k];
		} else if (k < pivotIndex) {
			return QuickSelect(arr, left, pivotIndex - 1, k);
		} else {
			return QuickSelect(arr, pivotIndex + 1, right, k);
		}
	}
	
	/**
     * Gets the (k+1)th smallest number in the subarray arr[left <= i <= right]
     *
     * @param arr the array from where to select the element.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the array arr. 
     * 
     */
	public static double QuickSelect(double[] arr, int left, int right, int k) {
		// returns the element if it's the only one remaining.
		if (left == right) {
			return arr[left];
		}
		
		// select a pivotIndex between left and right
		int pivotIndex = randomPivot(left, right); 
		pivotIndex = partition(arr, left, right, pivotIndex);
		// The pivot is in its final sorted position
		if (k == pivotIndex) {
			return arr[k];
		} else if (k < pivotIndex) {
			return QuickSelect(arr, left, pivotIndex - 1, k);
		} else {
			return QuickSelect(arr, pivotIndex + 1, right, k);
		}
	}
	
	/**
     * Gets the (k+1)th smallest number in the subarray arr[left <= i <= right]
     *
     * @param arr the array from where to select the element.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the array arr. 
     * 
     */
	public static long QuickSelect(long[] arr, int left, int right, int k) {
		// returns the element if it's the only one remaining.
		if (left == right) {
			return arr[left];
		}
		
		// select a pivotIndex between left and right
		int pivotIndex = randomPivot(left, right); 
		pivotIndex = partition(arr, left, right, pivotIndex);
		// The pivot is in its final sorted position
		if (k == pivotIndex) {
			return arr[k];
		} else if (k < pivotIndex) {
			return QuickSelect(arr, left, pivotIndex - 1, k);
		} else {
			return QuickSelect(arr, pivotIndex + 1, right, k);
		}
	}
	
	/**
     * Partitions the array arr to be used in the QuickSelect method.
     * Private use only. Only used in the QuickSelect method.
     * 
     */
	private static int partition(int[] arr, int left, int right, int pivotIndex) {
		int pivotValue = arr[pivotIndex];
		swap(arr, pivotIndex, right); // move pivot to end
		int storeIndex = left;
		for(int i = left; i < right; i++) {
			if(arr[i] < pivotValue) {
				swap(arr, storeIndex, i);
				storeIndex++;
			}
		}
		swap(arr, right, storeIndex); // Move pivot to its final place
		return storeIndex;
	}
	
	/**
     * Partitions the array arr to be used in the QuickSelect method.
     * Private use only. Only used in the QuickSelect method.
     * 
     */
	private static int partition(float[] arr, int left, int right, int pivotIndex) {
		float pivotValue = arr[pivotIndex];
		swap(arr, pivotIndex, right); // move pivot to end
		int storeIndex = left;
		for(int i = left; i < right; i++) {
			if(arr[i] < pivotValue) {
				swap(arr, storeIndex, i);
				storeIndex++;
			}
		}
		swap(arr, right, storeIndex); // Move pivot to its final place
		return storeIndex;
	}
	
	/**
     * Partitions the array arr to be used in the QuickSelect method.
     * Private use only. Only used in the QuickSelect method.
     * 
     */
	private static int partition(double[] arr, int left, int right, int pivotIndex) {
		double pivotValue = arr[pivotIndex];
		swap(arr, pivotIndex, right); // move pivot to end
		int storeIndex = left;
		for(int i = left; i < right; i++) {
			if(arr[i] < pivotValue) {
				swap(arr, storeIndex, i);
				storeIndex++;
			}
		}
		swap(arr, right, storeIndex); // Move pivot to its final place
		return storeIndex;
	}
	
	/**
     * Partitions the array arr to be used in the QuickSelect method.
     * Private use only. Only used in the QuickSelect method.
     * 
     */
	private static int partition(long[] arr, int left, int right, int pivotIndex) {
		long pivotValue = arr[pivotIndex];
		swap(arr, pivotIndex, right); // move pivot to end
		int storeIndex = left;
		for(int i = left; i < right; i++) {
			if(arr[i] < pivotValue) {
				swap(arr, storeIndex, i);
				storeIndex++;
			}
		}
		swap(arr, right, storeIndex); // Move pivot to its final place
		return storeIndex;
	}
	
	/**
     * Swaps the position of the elements arr[a] and arr[b] in the array arr.
     * 
     */
	public static void swap(int[] arr, int a, int b) {
		int tmp = arr[a];
		arr[a] = arr[b];
		arr[b] = tmp;
	}
	
	/**
     * Swaps the position of the elements arr[a] and arr[b] in the array arr.
     * 
     */
	public static void swap(float[] arr, int a, int b) {
		float tmp = arr[a];
		arr[a] = arr[b];
		arr[b] = tmp;
	}
	
	/**
     * Swaps the position of the elements arr[a] and arr[b] in the array arr.
     * 
     */
	public static void swap(double[] arr, int a, int b) {
		double tmp = arr[a];
		arr[a] = arr[b];
		arr[b] = tmp;
	}
	
	/**
     * Swaps the position of the elements arr[a] and arr[b] in the array arr.
     * 
     */
	public static void swap(long[] arr, int a, int b) {
		long tmp = arr[a];
		arr[a] = arr[b];
		arr[b] = tmp;
	}

	/**
     * Returns a random index between left and right, included.
     * 
     */
	public static int randomPivot(int left, int right) {
		return left + (int) Math.floor(Math.random() * (right - left + 1));
	}
	
	/**
     * Returns the median of the array arr.
     * 
     * @implNote uses the QuickSelect algorithm. 
     * 
     */
	static public float getMedian (float[] arr) {
		if (arr.length == 0) return -1;
		return QuickSelect(arr, arr.length >>> 1);
	}
	
	/**
     * Returns the median of the array arr.
     * 
     * @implNote uses the QuickSelect algorithm. 
     * 
     */
	static public int getMedian (int[] arr) {
		if (arr.length == 0) return -1;
		return QuickSelect(arr, arr.length >>> 1);
	}
	
	/**
     * Returns the median of the array arr.
     * 
     * @implNote uses the QuickSelect algorithm. 
     * 
     */
	static public double getMedian (double[] arr) {
		if (arr.length == 0) return -1;
		return QuickSelect(arr, arr.length >>> 1);
	}
	
	/**
     * Returns the median of the array arr.
     * 
     * @implNote uses the QuickSelect algorithm. 
     * 
     */
	static public long getMedian (long[] arr) {
		if (arr.length == 0) return -1;
		return QuickSelect(arr, arr.length >>> 1);
	}
	
	/**
     * Returns the mean of the array arr.
     * 
     */
	static public float getMean (float[] arr) {
		float tmp = 0;
		for (float f : arr)
			tmp += f;
		return tmp/arr.length;
	}
	
	/**
     * Returns the mean of the array arr.
     * 
     */
	static public float getMean (int[] arr) {
		float tmp = 0;
		for (int f : arr)
			tmp += f;
		return tmp/arr.length;
	}
	
	/**
     * Returns the mean of the array arr.
     * 
     */
	static public double getMean (double[] arr) {
		double tmp = 0;
		for (double f : arr)
			tmp += f;
		return tmp/arr.length;
	}
	
	/**
     * Returns the mean of the array arr.
     * 
     */
	static public float getMean (long[] arr) {
		float tmp = 0;
		for (long f : arr)
			tmp += f;
		return tmp/arr.length;
	}
	
	/**
     * Returns the harmonic mean of the array arr.
     * 
     */
	static public float getHarmonicMean (float[] arr) {
		float tmp = 0;
		for (float f : arr) {
			if (f == 0)
				return 0;
			tmp += 1/f;
		}
		return arr.length/tmp;
	}
	
	/**
     * Returns the harmonic mean of the array arr.
     * 
     */
	static public float getHarmonicMean (int[] arr) {
		float tmp = 0;
		for (float f : arr) {
			if (f == 0)
				return 0;
			tmp += 1/f;
		}return arr.length/tmp;
	}
	
	/**
     * Returns the harmonic mean of the array arr.
     * 
     */
	static public double getHarmonicMean (double[] arr) {
		double tmp = 0;
		for (double f : arr) {
			if (f == 0)
				return 0;
			tmp += 1/f;
		}return arr.length/tmp;
	}
	
	/**
     * Returns the harmonic mean of the array arr.
     * 
     */
	static public float getHarmonicMean (long[] arr) {
		float tmp = 0;
		for (float f : arr) {
			if (f == 0)
				return 0;
			tmp += 1/f;
		}return arr.length/tmp;
	}
	
	/**
     * Gets the (k+1)th smallest number in the results of a Function over a Collection. 
     * 
     * @param collection a collection on which to be applied the function.
     * 
     * @param function a Function to be applied over the collection and whose results will be used in Quick Select algorithm.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the collection. 
     * 
     */
	static public <E> float QuickSelectFloat (Collection<E> collection, Function<E,Float> function, int k) {
		float[] arr = new float[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return QuickSelect(arr, k);
	}
	
	/**
     * Gets the (k+1)th smallest number in the results of a Function over a Collection. 
     * 
     * @param collection a collection on which to be applied the function.
     * 
     * @param function a Function to be applied over the collection and whose results will be used in Quick Select algorithm.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the collection. 
     * 
     */
	static public <E> int QuickSelectInt (Collection<E> collection, Function<E,Integer> function, int k) {
		int[] arr = new int[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return QuickSelect(arr, k);
	}
	
	/**
     * Gets the (k+1)th smallest number in the results of a Function over a Collection. 
     * 
     * @param collection a collection on which to be applied the function.
     * 
     * @param function a Function to be applied over the collection and whose results will be used in Quick Select algorithm.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the collection. 
     * 
     */
	static public <E> double QuickSelectDouble (Collection<E> collection, Function<E,Double> function, int k) {
		double[] arr = new double[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return QuickSelect(arr, k);
	}
	
	/**
     * Gets the (k+1)th smallest number in the results of a Function over a Collection. 
     * 
     * @param collection a collection on which to be applied the function.
     * 
     * @param function a Function to be applied over the collection and whose results will be used in the Quick Select algorithm.
     * 
     * @implNote runs with O(n) complexity, where n is the length of the collection. 
     * 
     */
	static public <E> long QuickSelectLong (Collection<E> collection, Function<E,Long> function, int k) {
		long[] arr = new long[collection.size()];
		int i = 0;
		for (E n : collection) {
			arr[i++] = function.apply(n);
		}
		return QuickSelect(arr, k);
	}
	
}
