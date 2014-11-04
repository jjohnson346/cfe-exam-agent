package financial.fraud.cfe.util;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * encapsulates the logic for whether this question is a true/false question.  
 * True must be the first option, false must be the second option.  
 * 
 * @author jjohnson346
 *
 */
public class FeatureTrueFalse implements IFeature {

	protected CFEExamQuestion question;
	
	public FeatureTrueFalse(CFEExamQuestion question) {
		this.question = question;
	}
	
	/**
	 * return true if a true/false question
	 */
	@Override
	public boolean exists() {
		if(question.options.size() != 2)
			return false;
		if(question.options.get(0).equals("True") && question.options.get(1).equals("False"))
			return true;
		return false;
	}

}
