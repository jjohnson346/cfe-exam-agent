package financial.fraud.cfe.algorithm;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.util.FeatureTrueFalse;



/**
 * FalseSelect is an algorithm simply returning options[1] (for false).  This 
 * algorithm is found to be effective on True/False Absolute questions where it seems that 
 * such questions usually have a correct answer of False.
 * 
 * @author Joe
 *
 */
public class FalseSelect implements IAlgorithm {

	/**
	 * returns false if the question is a true/false question.
	 */
	@Override
	public int solve(CFEExamQuestion question) {
		FeatureTrueFalse featureTrueFalse = new FeatureTrueFalse(question);
		if(featureTrueFalse.exists())
			return 1;
		else
			return -1;
	}

	@Override
	public String toString() {
		return "False Select";
	}
}
