package financial.fraud.cfe.algorithm;

import java.util.List;

/**
 * This interface is an extension of the IAlgorithm interface which includes 
 * methods concerning frequencies associated with the options of the question.
 * Typically, these frequencies reflect the frequency of occurrence of the option
 * phrase within the section in the manual corresponding to the section of the question.
 * 
 * @author jjohnson346
 *
 */
public interface IAlgorithmFrequency extends IAlgorithm {
	
	/**
	 * retrieves the option frequencies.  
	 * @return list of doubles giving the frequencies for options 0, 1, 2, and 3
	 */
	public List<Double> getOptionFrequencies();
	
	/**
	 * prints out the frequencies
	 */
	public void printOptionFrequencies();
}
