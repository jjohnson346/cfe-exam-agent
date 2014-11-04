package financial.fraud.cfe.algorithm;

import java.util.List;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.util.FeatureHasLongOptions;
import financial.fraud.cfe.util.FeatureTrueFalse;

/**
 * CompositeFrequency is an algorithm that attempts to selectively apply 
 * AlgorithmBagOfWords or AlgorithmAllPhraseCount based on whether the question
 * is marked for having long options.  If the question's HAS_LONG_OPTIONS flag 
 * is set to true, then this algorithm applies bag of words, otherwise applies  
 * all phrase count.
 * 
 * @author Joe
 *
 */
public class CompositeFrequency implements IAlgorithmFrequency {

	private MaxFrequency afreq;
	
	private CFEManualLargeDocUnit cfeManual;
	
	/**
	 * constructor accepts cfe manual object to be used for later processing.
	 * 
	 * @param cfeManual
	 */
	public CompositeFrequency(CFEManualLargeDocUnit cfeManual) {
		this.cfeManual = cfeManual;
	}
	
	/**
	 * returns the index returned by either bag of words or max freq plus, depending
	 * on the presence of long options.
	 */
	@Override
	public int solve(CFEExamQuestion question) {

		// if this is a true false question, this is not a good 
		// algo.  simply return -1.
		FeatureTrueFalse featureTrueFalse = new FeatureTrueFalse(question);
		if(featureTrueFalse.exists())
			return -1;

		boolean hasLongOptions = new FeatureHasLongOptions(question).exists();
		if(hasLongOptions) 
			afreq = new BagOfWords(cfeManual);
		else
			afreq = new MaxFreqPlus(cfeManual);
		
		return afreq.solve(question);
	}

	/**
	 * returns a list of frequencies for the options.
	 */
	@Override
	public List<Double> getOptionFrequencies() {
		return afreq.getOptionFrequencies();
	}
	
	/**
	 * prints out the list of frequencies for the respective options.
	 */
	@Override
	public void printOptionFrequencies() {
		afreq.printOptionFrequencies();
	}
	
	@Override
	public String toString() {
		return "Composite Frequency";
	}
}
