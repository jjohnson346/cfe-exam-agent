package financial.fraud.cfe.util;

import java.util.List;
import java.util.StringTokenizer;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * FeatureHasLongOptions encapsulates the logic for whether the question
 * has at least one answer option which is classified as a "long option", 
 * that is, as an option with a "large" number of words.  The threshold 
 * for the number of words is subject to refinement.  A private constant
 * at the top of this class is used for setting/adjusting this threshold
 * value.  
 * 
 * As of 07/22/2014, the threshold value was set to 4.
 * 
 * @author jjohnson346
 *
 */
public class FeatureHasLongOptions implements IFeature {

	private CFEExamQuestion question;
	
	private final int MIN_WORD_COUNT_FOR_LONG_OPTION = 4;

	public FeatureHasLongOptions(CFEExamQuestion question) {
		this.question = question;
	}

	@Override
	public boolean exists() {
		// set the has_long_options flag.
		boolean isLong = false;
		

		int allAboveOptionIndex = new FeatureAllOfTheAbove(question).getOptionIndex();
		int noneAboveOptionIndex = new FeatureNoneOfTheAbove(question).getOptionIndex();
		
		List<String> options = question.options;
		for(int i = 0; i < options.size(); i++) {
			int tokensCount = new StringTokenizer(options.get(i)).countTokens();
			if(tokensCount > MIN_WORD_COUNT_FOR_LONG_OPTION && i != noneAboveOptionIndex && i != allAboveOptionIndex) {
				isLong = true;
			}
		}
		return isLong;
	}

}
