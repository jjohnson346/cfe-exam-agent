package financial.fraud.cfe.algorithm;

import java.util.Random;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.manual.CFEManual;


/**
 * AlgorithmRandomization is an algorithm that simply returns a random response
 * for a given question.  The expected score for such an algorithm is 25% for a 
 * multiple choice exam in which each question has 4 options.
 *  
 * @author Joe
 *
 */
public class Randomization implements IAlgorithm {

	private Random randomIdx;
	
	/**
	 * constructor initializes the internal variable for implementing randomization.
	 */
	public Randomization() {
		randomIdx = new Random();
	}
	
	@Override
	public String toString() {
		return "Random";
	}
	
	// 2.0.0 - add CFEManual as a parameter for solve.
	/**
	 * returns a random selection for index as the answer to the question.
	 */
	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		return randomIdx.nextInt(question.options.size());
	}
}
