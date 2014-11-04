package financial.fraud.cfe.algorithm;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.util.FeatureAllOfTheAbove;
import financial.fraud.cfe.util.FeatureNoneOfTheAbove;


/**
 * MaxFreqPlus extends MaxFreq to incorporate
 * functionality for "none of the above" and "all of the above" options in questions.
 * This algorithm includes constants for lower and upper thresholds for "none of the above"
 * and "all of the above" options, respectively.
 * 
 * @author Joe
 *
 */
public class MaxFreqPlus extends MaxFrequency {
	
	// TODO: eliminate options all of the above, none of the above from option counts.
	// Considering these in the computation could throw off results relative to min/max
	// thresholds.

	private final int NONE_OF_THE_ABOVE_MAX = 0;
	private final int ALL_OF_THE_ABOVE_MIN = 1;
	
	/**
	 * constructor accepts cfe manual object and stores it for later processing.
	 * 
	 * @param cfeManual the cfe manual object
	 */
	public MaxFreqPlus(CFEManualLargeDocUnit cfeManual) {
		super(cfeManual);
	}

	/**
	 * returns the index of the max frequency option, where for questions with 
	 * all of the above, and none of the above options, there is logic for 
	 * determining if these are the ones to pick.  Note here there are thresholds
	 * for all of the above min and none of the above max.
	 */
	@Override
	public int solve(CFEExamQuestion question) {
		
		int responseIdx = super.solve(question);
		
		// if this is a none-of-the-above question and max <= NONE_OF_THE_ABOVE_MAX,
		// then return none of the above.
		int noneAboveOptionIndex = new FeatureNoneOfTheAbove(question).getOptionIndex();
		if(noneAboveOptionIndex != -1 && optionFrequencies.get(responseIdx) <= NONE_OF_THE_ABOVE_MAX) {
			responseIdx = noneAboveOptionIndex;
		}

		// if this is an all-of-the-above question and all options have freq > ALL_OF_THE_ABOVE_MIN,
		// then return all of the above.
		
		int allAboveOptionIndex = new FeatureAllOfTheAbove(question).getOptionIndex();
		if(allAboveOptionIndex != -1) {
			boolean isAllOfTheAbove = true;
			for(int i = 0; i < optionFrequencies.size(); i++) {
				if(optionFrequencies.get(i) < ALL_OF_THE_ABOVE_MIN)
					isAllOfTheAbove = false;
			}
			if(isAllOfTheAbove)
				responseIdx = allAboveOptionIndex;
		}
		
		return responseIdx;
	}

	@Override
	public String toString() {
		return "MaxFreqPlus";
	}
}
