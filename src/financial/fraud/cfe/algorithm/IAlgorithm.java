package financial.fraud.cfe.algorithm;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.manual.CFEManual;

/**
 * serves as an interface to be implemented by all classes implementing 
 * algorithms for answering questions.
 * 
 * @author jjohnson346
 *
 */
public interface IAlgorithm {
	
	/**
	 * the one and only method for this interface, returns an index 
	 * for the option selected by the algorithm
	 * 
	 * @param question the CFEExamQuestion to which to apply the algorithm
	 * @return the index of the option selected
	 */
	public int solve(CFEExamQuestion question, CFEManual cfeManual);
}
