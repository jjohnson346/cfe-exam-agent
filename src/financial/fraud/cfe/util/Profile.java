package financial.fraud.cfe.util;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * An instance of the Profile class contains feature information for a particular
 * question, which effectively serves as a profile for that question.  An instance
 * of the Profile class contains an array of booleans, where each boolean value indicates
 * whether a particular feature exists for that question.  For example, one
 * boolean value specifies whether the question with which the Profile object is associated
 * is an "all of the above" question or not (i.e., does it contain an option "all of the above").
 * As another example, another boolean value specifies whether the question is a true/false
 * questions.  And fianlly, yet another boolean value indicates whether the question 
 * contains options that are long (i.e., there is at least one option that consists of a lot of words).
 * 
 * @author jjohnson346
 *
 */
public class Profile {

	private boolean[] features;

	/**
	 * constructor takes a CFEExamQuestion as a parameter, and determines whether
	 * some prescribed features exist for these questions.  For each feature,
	 * either a true or false value is determined, depending on whether the feature
	 * exists for the question.  It then stores these values in an array of booleans.
	 * 
	 * @param question the CFEExamQuestion object
	 */
	public Profile(CFEExamQuestion question) {
		features = new boolean[FeatureType.values().length];

		features[FeatureType.ALL_OF_THE_ABOVE.ordinal()] = new FeatureAllOfTheAbove(question).exists();
		features[FeatureType.NONE_OF_THE_ABOVE.ordinal()] = new FeatureNoneOfTheAbove(question).exists();
		features[FeatureType.EXCEPT.ordinal()] = new FeatureExcept(question).exists();
		features[FeatureType.TRUE_FALSE.ordinal()] = new FeatureTrueFalse(question).exists();
		features[FeatureType.TRUE_FALSE_ABSOLUTE.ordinal()] = new FeatureTrueFalseAbsolute(question).exists();
		features[FeatureType.DEFINITION.ordinal()] = new FeatureDefinition(question).exists();
		features[FeatureType.HAS_LONG_OPTIONS.ordinal()] = new FeatureHasLongOptions(question).exists();
	}

	/**
	 * returns whether a given feature exists for the question with which the
	 * Profile object is associated.
	 * 
	 * @param ft the FeatureType for which to retrieve the value from the feature array.
	 * @return true/false, depending on whether the feature exists
	 */
	public boolean featureExists(FeatureType ft) {
		return features[ft.ordinal()];
	}

	/**
	 * retrieves the entire array of booleans for the features
	 * 
	 * @return the boolean array indicating the feature presences
	 */
	public boolean[] getFeatures() {
		return features;
	}

	/**
	 * return the profile value for the question object with which this Profile
	 * object is associated.  Use the power of 2 formula shown below
	 * AllOfTheAbove * 2^6 +
	 * NoneOfTheAbove * 2^5 +
	 * Except * 2^4 +
	 * TrueFalse * 2^3 +
	 * TrueFalseAbsolute * 2^2
	 * Definition * 2^1 +
	 * HasLongOptions * 2^0 
	 * 
	 * @return the index based on the formula and values in the features array
	 */
	public int getProfileIndex() {
		int profileIndex = 0;
		
		// set profile index to:
		//
		// AllOfTheAbove * 2^6 +
		// NoneOfTheAbove * 2^5 +
		// Except * 2^4 +
		// TrueFalse * 2^3 +
		// TrueFalseAbsolute * 2^2
		// Definition * 2^1 +
		// HasLongOptions * 2^0 

		for(int i = 0; i < features.length; i++) {
			int featureExists = features[i] ? 1 : 0;
			profileIndex = 2 * profileIndex + featureExists;
		}
		return profileIndex;
	}
}
