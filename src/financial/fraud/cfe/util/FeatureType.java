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
	// IMPORTANT: If an item is added to or deleted from this list
	// make sure to adjust the methods in the Profile class, hasFeature() and
	// getDescription(), which are tightly coupled with the structure and values
	// of the FeatureType enum.
	CF_HANDBOOK,
	I_II_III_IV,
	ALL_OF_THE_ABOVE, 
	NONE_OF_THE_ABOVE, 
	EXCEPT,
	TRUE_FALSE, 
	TRUE_FALSE_ABSOLUTE,
	DEFINITION, 
	DEFINITION_NOT, 
	HAS_LONG_OPTIONS;
}
