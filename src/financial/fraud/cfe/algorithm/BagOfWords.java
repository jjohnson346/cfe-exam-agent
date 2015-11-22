package financial.fraud.cfe.algorithm;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;
import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.util.FeatureAllOfTheAbove;
import financial.fraud.cfe.util.FeatureNoneOfTheAbove;
import financial.fraud.cfe.util.FeatureTrueFalse;
import financial.fraud.cfe.util.TokenizerSimple;

/**
 * AlgorithmBagOfWords is an algorithm that adapts the Bag of Words spam detection algorithm
 * to determine the correct answer option.  It is based on the following idea:  The correct
 * option is the option whose word sequence has the maximum probability given the manual section
 * corpus, calculated by taking the product of the probs for the individual words.  
 * Geometric mean is used to normalize for phrases of varying length.  
 */
public class BagOfWords extends MaxFrequency {
	
	private final int NONE_OF_THE_ABOVE_MAX = 0;

	private final int ALL_OF_THE_ABOVE_MIN = 1;

	
	// 2.0.0 - removed constructor.
	// public BagOfWords(CFEManualLargeDocUnit cfeManual) {
	// super(cfeManual);
	// }
	
	/**
	 * returns the index of the option whose likelihood is highest.
	 * 2.0.0 - added CFEManual parameter.
	 * 
	 */
	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {

		// TODO: eliminate options all of the above, none of the above from option counts.
		// Considering these in the computation could throw off results relative to min/max
		// thresholds.
		
		this.question = question;

		Logger.getInstance().println("Question section: " + question.section, DetailLevel.FULL);
		
		// if this is a true false question, this is not a good 
		// algo.  simply return -1.
		FeatureTrueFalse featureTrueFalse = new FeatureTrueFalse(question);
		if(featureTrueFalse.exists())
			return -1;
		
		
		// calculate frequency for each option of the question.
		// For the bag of words algorithm, return the geometric mean of
		// the counts for each of the words in the option phrase.
		

		// first, retrieve the token type frequencies for the tokens in the manual
		// section corresponding to this question.  
		
		// TODO: Incorporate PorterStemmer in Tokenizer.
		Map<String, Integer> optionTokenTypeFreqs = cfeManual.getManualSectionForQuestionSection(question.section).getTokenizer().getTokenTypeFreqs();

		optionFrequencies = new ArrayList<Double>();
		TokenizerSimple tokenizerSimple = new TokenizerSimple();

		for (String optionPhrase : question.options) {
			
			tokenizerSimple.tokenize(optionPhrase);
			List<String> optionTokens = tokenizerSimple.getTokens();
			
			// get product of counts of all words in the option phrase.
			double frequency = 1.0;
			for(String optionToken : optionTokens) {
				if(optionTokenTypeFreqs.containsKey(optionToken)) {
					int optionTokenTypeFreq = optionTokenTypeFreqs.get(optionToken);
					frequency *= optionTokenTypeFreq;
			
					Logger.getInstance().println(optionToken + ": " + optionTokenTypeFreq, DetailLevel.FULL);
				
				} else {
					frequency = 0;
					break;
				}
			}
			
			Logger.getInstance().println("Total Frequency for Option: " + frequency, DetailLevel.FULL);
			
			// get geometric mean of frequency to normalize for different phrase lengths.
			frequency = Math.pow(frequency, 1.0 / optionTokens.size());
			
			Logger.getInstance().println("Geom Mean Frequency: " + frequency, DetailLevel.FULL);
			
			optionFrequencies.add(frequency);
		}

		// return option with max frequency. Ties go to earliest option.
		int responseIdx = 0;
		double max = 0;
		for (int i = 0; i < optionFrequencies.size(); i++) {
			if (optionFrequencies.get(i) > max) {
				max = optionFrequencies.get(i);
				responseIdx = i;
			}
		}
		
		// if this is a none-of-the-above question and max <= NONE_OF_THE_ABOVE_MAX,
		// then return none of the above.
		int noneAboveOptionIndex = new FeatureNoneOfTheAbove(question).getOptionIndex();
		if(noneAboveOptionIndex != -1 && optionFrequencies.get(responseIdx) <= NONE_OF_THE_ABOVE_MAX) {
			responseIdx = noneAboveOptionIndex;
		}

		// if this is an all-of-the-above question and all options have freq >= ALL_OF_THE_ABOVE_MIN,
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
		return "Bag Of Words";
	}
}
