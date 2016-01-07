package financial.fraud.cfe.util;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * FeatureDefinitionNot determines whether the question is a particular type of
 * definition question, (note FeatureDefinitionNot extends FeatureDefinition),
 * namely, one that is looking for the negation, i.e., for an option that does
 * not correlate to the question stem.
 * 
 * Specifically, this class encapsulates the logic for determining whether,
 * given this is a definition question, the stem contains the word, "not",
 * (either upper or lower case).
 * 
 * @author jjohnson346
 *
 */
public class FeatureDefinitionNot extends FeatureDefinition implements IFeature {

	public FeatureDefinitionNot(CFEExamQuestion question) {
		super(question);
	}

	/**
	 * return true if the stem contains "EXCEPT" (must be upper-case).
	 */
	@Override
	public boolean exists() {
		// first, test whether this is a definition question.
		if (!super.exists())
			return false;

		return question.stem.toLowerCase().indexOf("which of the following is not") != -1;
	}
}
