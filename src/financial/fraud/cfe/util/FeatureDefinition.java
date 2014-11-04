package financial.fraud.cfe.util;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * FeatureDefinition encapsulates the information for whether the 
 * question appears to be a question about a correct definition
 * for a term or concept.  The logic of this class identifies a question
 * as having the definition feature if the stem contains such key phrases
 * as "is referred to", "is known as", "is called".  (Others may be added...)
 * 
 * Also, of note here is the fact that if the question is determined to contain
 * long-worded options, then the question is considered non-definitional in 
 * nature.
 * 
 * @author jjohnson346
 *
 */
public class FeatureDefinition implements IFeature {

	private CFEExamQuestion question;
	
	/**
	 * constructor stores the question to which the next exists() method call
	 * will apply.  
	 * 
	 * Notice this approach was taken instead of applying the 
	 * question as an input parm to exists() because the IFeature interface
	 * must be properly implemented.  One could argue the merits of adding
	 * CFEExamQuestion parameter to the exists() method, but that simply wasn't
	 * the approach taken, here.
	 * @param question
	 */
	public FeatureDefinition(CFEExamQuestion question) {
		this.question = question;
	}
	
	/**
	 * returns true if the question is considered a definition question,
	 * one which asks about the correct definition for a term or concept.
	 * It looks for key phrases: "is called", "is known as", "is referred to as".
	 * Others may be added in the future, depending on what is found in the training
	 * set.
	 */
	@Override
	public boolean exists() {
		FeatureTrueFalse ftf = new FeatureTrueFalse(question);
		
		// if this is a true/false question, return false.
		if(ftf.exists())
			return false;
		
		// if question stem contains either "is known as" or "is called", return true.
		if(question.stem.indexOf("is called") != -1 
				|| question.stem.indexOf("is known as") != -1
				|| question.stem.indexOf("is referred to as") != -1)
			return true;

		// if question has long options, return false.
		if(!new FeatureHasLongOptions(question).exists())
			return true;
		return false;
	}
}
