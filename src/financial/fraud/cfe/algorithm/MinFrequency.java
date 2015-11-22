package financial.fraud.cfe.algorithm;

import java.util.ArrayList;
import java.util.List;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;
import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.manual.CFEManualSection;
import financial.fraud.cfe.util.FeatureTrueFalse;

/**
 * MinFrequency is an algorithm that returns the option whose phrase occurs least frequently
 * in the question section of the manual.  This is intended for questions of the type, "...
 * all of the following EXCEPT..." The counts are based on instances of the complete
 * option phrase in the manual.  
 * 
 * @author Joe
 *
 */
public class MinFrequency implements IAlgorithmFrequency {

	// TODO: eliminate options all of the above, none of the above from option counts.
	// Considering these in the computation could throw off results relative to min/max
	// thresholds.

	protected CFEManualLargeDocUnit cfeManual;

	protected CFEExamQuestion question;
	
	protected ArrayList<Double> optionFrequencies;
	
	// 2.0.0 - removed this constructor.
	// public MinFrequency(CFEManualLargeDocUnit cfeManual) {
	// this.cfeManual = cfeManual;
	// }

	// 2.0.0 - added CFEManual as a parameter for solve.
	/**
	 * returns the index of the option which occurs least frequently in the section text.
	 */
	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		this.question = question;
		
		// if this is a true false question, this is not a good 
		// algo.  simply return -1.
		FeatureTrueFalse featureTrueFalse = new FeatureTrueFalse(question);
		if(featureTrueFalse.exists())
			return -1;

		// retrieve the relevant text in the manual for the section specified by the question.
		CFEManualSection section = cfeManual.getManualSectionForQuestionSection(question.section);
		String sectionText = section.getText();
//		String sectionText = cfeManual.getQuestionSectionText(question.section);

		
		// calculate frequency for each option of the question.
		optionFrequencies = new ArrayList<Double>();

		double frequency = 0;
		for (String phrase : question.options) {
			frequency = getPhraseCount(phrase, sectionText);
			optionFrequencies.add(frequency);
		}

		// return option with min frequency. Ties go to earliest option.
		int minIdx = 0;
		double min = 0;
		for (int i = 0; i < optionFrequencies.size(); i++) {
			if (optionFrequencies.get(i) < min) {
				min = optionFrequencies.get(i);
				minIdx = i;
			}
		}
		
		return minIdx;
	}
	
	/**
	 * returns the list of frequencies for options.
	 */
	@Override
	public List<Double> getOptionFrequencies() {
		return optionFrequencies;
	}

	/**
	 * prints out the option frequencies.
	 */
	@Override
	public void printOptionFrequencies() {
	
		int maxPhraseLength = 0;
		
		for (String phrase : question.options) {
			if (phrase.length() > maxPhraseLength)
				maxPhraseLength = phrase.length();
		}
		maxPhraseLength++;

		Logger.getInstance().printf(DetailLevel.MEDIUM, "%" + maxPhraseLength + "s%20s\n", "Phrase", "Frequency");
		for (int i = 0; i < question.options.size(); i++) {
			Logger.getInstance().printf(DetailLevel.MEDIUM, "%" + maxPhraseLength + "s%19.3f\n", question.options.get(i), optionFrequencies.get(i));
		}
		Logger.getInstance().printf(DetailLevel.MEDIUM, "\n");
	}

	/**
	 * returns the number of occurrences of an option phrase within the section of text.
	 * This method does no stemming and does not address the situation when the 
	 * option is "all of the above" or "none of the above".
	 * 
	 * TODO:  this code is duplicative.  It is also in MaxFrequency.  Therefore, should 
	 * re-factor these classes to remove the duplicative functions.
	 * 
	 * @param optionPhrase the phrase to search for in the text
	 * @param sectionText the text within which to find the number of occurrences of optionPhrase
	 * @return the number of occurrence of optionPhrase
	 */
	protected int getPhraseCount(String optionPhrase, String sectionText) {
		
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
		return "MinFrequency";
	}
}
