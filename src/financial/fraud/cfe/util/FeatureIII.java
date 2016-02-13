package financial.fraud.cfe.util;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * encapsulates the logic for whether this question is a true/false question.  
 * True must be the first option, false must be the second option.  
 * 
 * @author jjohnson346
 *
 */
public class FeatureIII implements IFeature {

	protected CFEExamQuestion question;
	
	public FeatureIII(CFEExamQuestion question) {
		this.question = question;
	}
	
	public FeatureIII() {}
	
	/**
	 * return true if a true/false question
	 */
	@Override
	public boolean exists() {
		for(String option : question.options) {
			if(option.indexOf("III") != -1)
				return true;
		}
		return false;
	}
	
	public static void main(String[] args) {
		CFEExamQuestion question = new CFEExamQuestion("exam questions - all/Financial Transactions and Fraud Schemes/Computer and Internet Fraud/Computer and Internet Fraud 9.txt");
		FeatureIII f = new FeatureIII(question);
		System.out.println(f.exists());
	}

	@Override
	public boolean hasFeature(CFEExamQuestion question) {
		this.question = question;
		return exists();
	}

}
