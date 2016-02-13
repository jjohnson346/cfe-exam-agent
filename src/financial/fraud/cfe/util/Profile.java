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
 * questions.  And finally, yet another boolean value indicates whether the question 
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

//		features[FeatureType.CF_HANDBOOK.ordinal()] = new FeatureCorporateFraudHandbook(question).exists();
		features[FeatureType.I_II_III_IV.ordinal()] = new FeatureIII(question).exists();
		features[FeatureType.ALL_OF_THE_ABOVE.ordinal()] = new FeatureAllOfTheAbove(question).exists();
		features[FeatureType.NONE_OF_THE_ABOVE.ordinal()] = new FeatureNoneOfTheAbove(question).exists();
		features[FeatureType.EXCEPT.ordinal()] = new FeatureExcept(question).exists();
		features[FeatureType.TRUE_FALSE.ordinal()] = new FeatureTrueFalse(question).exists();
		features[FeatureType.TRUE_FALSE_ABSOLUTE.ordinal()] = new FeatureTrueFalseAbsolute(question).exists();
		features[FeatureType.DEFINITION.ordinal()] = new FeatureDefinition(question).exists();
		features[FeatureType.DEFINITION_NOT.ordinal()] = new FeatureDefinitionNot(question).exists();
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
	 * I,II,III,IV * 2^8 +
	 * AllOfTheAbove * 2^7 +
	 * NoneOfTheAbove * 2^6 +
	 * Except * 2^5 +
	 * TrueFalse * 2^4 +
	 * TrueFalseAbsolute * 2^3
	 * Definition * 2^2 +
	 * DefinitionNot * 2^1 +
	 * HasLongOptions * 2^0 
	 * 
	 * @return the index based on the formula and values in the features array
	 */
	public int getProfileIndex() {
		int profileIndex = 0;
		
		// set profile index to:
		//
		// I,II,III,IV * 2^8
		// AllOfTheAbove * 2^7 +
		// NoneOfTheAbove * 2^6 +
		// Except * 2^5 +
		// TrueFalse * 2^4 +
		// TrueFalseAbsolute * 2^3
		// Definition * 2^2 +
		// DefinitionNot * 2^1 +
		// HasLongOptions * 2^0 

		for(int i = 0; i < features.length; i++) {
			int featureExists = features[i] ? 1 : 0;
			profileIndex = 2 * profileIndex + featureExists;
		}
		return profileIndex;
	}
	
	/**
	 * returns a string giving a description for a given profile index supplied as input.
	 * 
	 * @param profileIndex
	 * @return a string giving the description of the profile
	 */
	public static String getDescription(int profileIndex) {
		
		StringBuilder description = new StringBuilder();
		
		final int MAX_EXPONENT = FeatureType.values().length - 1;
		
		description.append(" ");		// add an introductory space for formatting
//		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.CF_HANDBOOK.ordinal())) {
//			description.append(description.length() == 1 ? "" : "/").append("fraud-handbook");
//			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.CF_HANDBOOK.ordinal());
//		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.I_II_III_IV.ordinal())) {
			description.append(description.length() == 1 ? "" : "/").append("I_II_III_IV");
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.I_II_III_IV.ordinal());
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.ALL_OF_THE_ABOVE.ordinal())) {
			description.append(description.length() == 1 ? "" : "/").append("all-above");
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.ALL_OF_THE_ABOVE.ordinal());
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.NONE_OF_THE_ABOVE.ordinal())) {
			description.append(description.length() == 1 ? "" : "/").append("none-above");
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.NONE_OF_THE_ABOVE.ordinal());
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.EXCEPT.ordinal())) {
			description.append(description.length() == 1 ? "" : "/").append("except");
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.EXCEPT.ordinal());
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.TRUE_FALSE.ordinal())) {
			description.append(description.length() == 1 ? "" : "/").append("true-false");
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.TRUE_FALSE.ordinal());
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.TRUE_FALSE_ABSOLUTE.ordinal())) {
			description.append(description.length() == 1 ? "" : "/").append("absolute");
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.TRUE_FALSE_ABSOLUTE.ordinal());
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.DEFINITION.ordinal())) {
			description.append(description.length() == 1 ? "" : "/").append("def");
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.DEFINITION.ordinal());
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.DEFINITION_NOT.ordinal())) {
			description.append(description.length() == 1 ? "" : "/").append("not");
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.DEFINITION_NOT.ordinal());
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.HAS_LONG_OPTIONS.ordinal())) {
			description.append(description.length() == 1 ? "" : "/").append("long-options");
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.HAS_LONG_OPTIONS.ordinal());
		}
		return new String(description);
	}
	
	public static boolean hasFeature(int profileIndex, int feature) {
		final int MAX_EXPONENT = FeatureType.values().length - 1;
		
//		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.CF_HANDBOOK.ordinal())) {
//			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.CF_HANDBOOK.ordinal());
//			if(feature == FeatureType.CF_HANDBOOK.ordinal())
//				return true;
//		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.I_II_III_IV.ordinal())) {
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.I_II_III_IV.ordinal());
			if(feature == FeatureType.I_II_III_IV.ordinal())
				return true;
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.ALL_OF_THE_ABOVE.ordinal())) {
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.ALL_OF_THE_ABOVE.ordinal());
			if(feature == FeatureType.ALL_OF_THE_ABOVE.ordinal())
				return true;
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.NONE_OF_THE_ABOVE.ordinal())) {
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.NONE_OF_THE_ABOVE.ordinal());
			if(feature == FeatureType.NONE_OF_THE_ABOVE.ordinal())
				return true;
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.EXCEPT.ordinal())) {
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.EXCEPT.ordinal());
			if(feature == FeatureType.EXCEPT.ordinal())
				return true;
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.TRUE_FALSE.ordinal())) {
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.TRUE_FALSE.ordinal());
			if(feature == FeatureType.TRUE_FALSE.ordinal())
				return true;
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.TRUE_FALSE_ABSOLUTE.ordinal())) {
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.TRUE_FALSE_ABSOLUTE.ordinal());
			if(feature == FeatureType.TRUE_FALSE_ABSOLUTE.ordinal())
				return true;
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.DEFINITION.ordinal())) {
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.DEFINITION.ordinal());
			if(feature == FeatureType.DEFINITION.ordinal())
				return true;
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.DEFINITION_NOT.ordinal())) {
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.DEFINITION_NOT.ordinal());
			if(feature == FeatureType.DEFINITION_NOT.ordinal())
				return true;
		}
		if(profileIndex >= Math.pow(2, MAX_EXPONENT-FeatureType.HAS_LONG_OPTIONS.ordinal())) {
			profileIndex -= Math.pow(2, MAX_EXPONENT-FeatureType.HAS_LONG_OPTIONS.ordinal());
			if(feature == FeatureType.HAS_LONG_OPTIONS.ordinal())
				return true;
		}
		return false;
		
	}
}
