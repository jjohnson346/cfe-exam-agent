package financial.fraud.cfe.algorithm;

/**
 * an enum that provides labels for the various algos, allowing for algos to 
 * be indexed by name, instead of by a cryptic integer.
 * 
 * @author jjohnson346
 *
 */

public enum AlgorithmType {

//	CONCEPT_MATCH,	// 2016/01/04 - version 2.0.0 - added CONCEPT MATCH
	ALL_ABOVE, 
	TRUE_SELECT, 
	FALSE_SELECT, 
	MAX_FREQ, 
	MAX_FREQ_PLUS, 
	MIN_FREQ,
	B_OF_W, 
	COMP_FREQ,
	CONCEPT_MATCH_V1,
	CONCEPT_MATCH_V2,
	CONCEPT_MATCH_V3,
	CONCEPT_MATCH_V3_NOTA,
	RANDOM;

}
