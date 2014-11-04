package financial.fraud.cfe.algorithm;

/**
 * an enum that provides labels for the various algos, allowing for algos to 
 * be indexed by name, instead of by a cryptic integer.
 * 
 * @author jjohnson346
 *
 */

public enum AlgorithmType {

	ALL_ABOVE, 
	TRUE_SELECT, 
	FALSE_SELECT, 
	MAX_FREQ, 
	MAX_FREQ_PLUS, 
	MIN_FREQ,
	B_OF_W, 
	COMP_FREQ, 
	RANDOM;

}
