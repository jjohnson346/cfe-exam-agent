package financial.fraud.cfe.util;

/**
 * FeatureType is an enum that provides labels for each feature type in the CFE Agent
 * system.  This enum helps supports extensibility of the system by providing a named
 * label facility for adding more features to the system.
 * 
 * @author jjohnson346
 *
 */
public enum FeatureType {
	ALL_OF_THE_ABOVE, 
	NONE_OF_THE_ABOVE, 
	EXCEPT,
	TRUE_FALSE, 
	TRUE_FALSE_ABSOLUTE,
	DEFINITION, 
	HAS_LONG_OPTIONS; 
}
