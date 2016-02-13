package financial.fraud.cfe.util;

import java.util.List;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * FeatureNoneOfTheAbove encapsulates the logic for determining whether the 
 * question has a none of the above option.
 * 
 * @author jjohnson346
 *
 */
public class FeatureCorporateFraudHandbook implements IFeature {

	private CFEExamQuestion question;
	
	public FeatureCorporateFraudHandbook(CFEExamQuestion question) {
		this.question = question;
	}
	
	public FeatureCorporateFraudHandbook() {}

	/**
	 * return true if none of the above option exists.
	 */
	@Override
	public boolean exists() {
		return question.explanation.toLowerCase().indexOf("corporate fraud handbook") != -1;
	}
	
	
	public static void main(String[] args) {
		CFEExamQuestion question = new CFEExamQuestion("exam questions - all/Financial Transactions and Fraud Schemes/Fraudulent Disbursements/Fraudulent Disbursements 27.txt");
		FeatureCorporateFraudHandbook f = new FeatureCorporateFraudHandbook(question);
		System.out.println(f.exists());
	}

	@Override
	public boolean hasFeature(CFEExamQuestion question) {
		this.question = question;
		return exists();
	}


}
