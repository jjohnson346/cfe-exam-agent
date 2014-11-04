package financial.fraud.cfe.util;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * FeatureExcept encapsultes the logic for determining whether a question
 * has a stem that contains the word, "EXCEPT" (case sensitive - if the letters
 * are not upper-case, then this logic returns false).
 * 
 * @author jjohnson346
 *
 */
public class FeatureExcept implements IFeature {

	private CFEExamQuestion question;
	
	public FeatureExcept(CFEExamQuestion question) {
		this.question = question;
	}

	/**
	 * return true if the stem contains "EXCEPT" (must be upper-case).
	 */
	@Override
	public boolean exists() {
		return question.stem.indexOf("EXCEPT") != -1;
	}
}
