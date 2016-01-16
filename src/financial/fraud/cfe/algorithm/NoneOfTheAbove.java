package financial.fraud.cfe.algorithm;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.util.FeatureNoneOfTheAbove;

/**
 * AlgorithmAllOfTheAbove is an algorithm that simply returns option[3] (corresponding
 * to the last of 4 options in a 0-based array), corresponding to "none of the above"
 * for none-of-the-above questions.
 * 
 * @author Joe
 *
 */
public class NoneOfTheAbove implements IAlgorithm {

	/**
	 * returns the index for the "all of the above" option, which is typically
	 * 3, the index for the last of 4 options.  However, note, here, that instead
	 * of a hard-coded return of 3, the method calls the getOptionIndex() method
	 * on the FeatureAllOfTheAbove object.  This is in case there are some other 
	 * all-of-the-above options that may come up for which code will be encapsulated
	 * in that class.
	 */
	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		FeatureNoneOfTheAbove feature = new FeatureNoneOfTheAbove(question);
		if(feature.exists())
			return feature.getOptionIndex();
		else
			return -1;
	}

	@Override
	public String toString() {
		return "None of the Above";
	}
}
