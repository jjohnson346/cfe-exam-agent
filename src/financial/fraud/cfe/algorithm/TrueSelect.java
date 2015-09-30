package financial.fraud.cfe.algorithm;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.util.FeatureTrueFalse;



/**
 * AlgorithmTrueFalse is an algorithm simply returning options[0] (for true).  This 
 * algorithm is found to be effective on True/False questions where it seems that 
 * such questions usually have a correct answer of True.
 * 
 * @author Joe
 *
 */
public class TrueSelect implements IAlgorithm {

	/**
	 * returns true if this is a true/false question.
	 */
	@Override
	public int solve(CFEExamQuestion question) {
		FeatureTrueFalse feature = new FeatureTrueFalse(question);
		if(feature.exists())
			return 0;
		else
			return -1;
	}

	@Override
	public String toString() {
		return "True Select";
	}
}