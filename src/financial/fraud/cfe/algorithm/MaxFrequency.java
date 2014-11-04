package financial.fraud.cfe.algorithm;

import java.util.ArrayList;
import java.util.List;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.manual.CFEManualSection;
import financial.fraud.cfe.util.FeatureTrueFalse;

/**
 * MaxFrequency is an algorithm that returns the option which occurs most frequently
 * in the question section of the manual.  The counts are based on instances of the complete
 * option phrase in the manual.  No further sophistication, (partial instances, stemming, 
 * etc.) is applied in this algorithm - not even functionality to account for "none of the
 * above" or "all of the above".
 * 
 * @author Joe
 *
 */
public class MaxFrequency implements IAlgorithmFrequency {

	// TODO: eliminate options all of the above, none of the above from option counts.
	// Considering these in the computation could throw off results relative to min/max
	// thresholds.

	protected CFEManualLargeDocUnit cfeManual;					// the cfe manual object
	
	protected CFEExamQuestion question;				// the question to apply the algorithm to
	
	protected ArrayList<Double> optionFrequencies;	// frequency counts of the options
	
	/**
	 * constructor accepts the cfe manual object to store for later processing.
	 * 
	 * @param cfeManual
	 */
	public MaxFrequency(CFEManualLargeDocUnit cfeManual) {
		this.cfeManual = cfeManual;
	}

	/**
	 * 
	 */
	@Override
	public int solve(CFEExamQuestion question) {
		// TODO: include logic for addressing options: all of the above, none of the above.
		
		
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Executing max frequency algo...");
		
		this.question = question;
		
		// if this is a true false question, this is not a good 
		// algo.  simply return -1.
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Testing whether this is a TrueFalse question...");
		FeatureTrueFalse featureTrueFalse = new FeatureTrueFalse(question);
		if(featureTrueFalse.exists()) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "This is a true false question.");
			return -1;
		} else {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "This is not a true false question.");
		}
		
		// retrieve the relevant text in the manual for the section specified by the question.
		Logger.getInstance().printf(DetailLevel.FULL, "%s%s\n", "Retrieving section text for question section, ", question.section);

		//09/13/2014 - changed the code here to use CFEManualSection
		CFEManualSection section = cfeManual.getManualSectionForQuestionSection(question.section);
		String sectionText = section.getText();
//		String sectionText = cfeManual.getQuestionSectionText(question.section);

		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Question section retrieval complete., ");

		
		// calculate frequency for each option of the question.
		optionFrequencies = new ArrayList<Double>();

		double frequency = 0;
		for (String phrase : question.options) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s%s\n", "Determining frequency for string, ", phrase);
			
			// get frequency count or current option in the section text.
			// note that there's currently no stemming here and no accounting for 
			// all of the above, none of the above.
			frequency = getPhraseCount(phrase, sectionText);
			optionFrequencies.add(frequency);
		}

		// return option with max frequency. Ties go to earliest option.
		int maxIdx = 0;
		double max = 0;
		for (int i = 0; i < optionFrequencies.size(); i++) {
			if (optionFrequencies.get(i) > max) {
				max = optionFrequencies.get(i);
				maxIdx = i;
			}
		}
		
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Max Fequency algorithm complete.");
		return maxIdx;
	}
	
	/**
	 * returns the list of option phrase frequencies for all options.
	 */
	@Override
	public List<Double> getOptionFrequencies() {
		return optionFrequencies;
	}

	/**
	 * prints out the list of option phrase frequencies.
	 */
	@Override
	public void printOptionFrequencies() {
	
		int maxPhraseLength = 0;
		
		for (String phrase : question.options) {
			if (phrase.length() > maxPhraseLength)
				maxPhraseLength = phrase.length();
		}
		maxPhraseLength++;

		Logger.getInstance().printf(DetailLevel.FULL, "%" + maxPhraseLength + "s%20s\n", "Phrase", "Frequency");
		for (int i = 0; i < question.options.size(); i++) {
			Logger.getInstance().printf(DetailLevel.FULL, "%" + maxPhraseLength + "s%19.3f\n", question.options.get(i), optionFrequencies.get(i));
		}
		Logger.getInstance().printf(DetailLevel.MEDIUM, "\n");
	}

	/**
	 * returns the number of occurrences of an option phrase within the section of text.
	 * This method does no stemming and does not address the situation when the 
	 * option is "all of the above" or "none of the above".
	 * 
	 * @param optionPhrase the phrase to search for in the text
	 * @param sectionText the text within which to find the number of occurrences of optionPhrase
	 * @return the number of occurrence of optionPhrase
	 */
	protected int getPhraseCount(String optionPhrase, String sectionText) {
		
		// TODO: should insert stemming code here
		
		int lastIndex = 0;
		int count = 0;
		do {
			lastIndex = sectionText.toLowerCase().indexOf(optionPhrase.toLowerCase(), lastIndex);
			if (lastIndex != -1) {
				count++;
				lastIndex = lastIndex + optionPhrase.length();
			}
		} while (lastIndex != -1);
		return count;
	}

	@Override
	public String toString() {
		return "MaxFrequency";
	}
}
