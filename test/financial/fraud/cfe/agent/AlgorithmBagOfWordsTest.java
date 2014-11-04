package financial.fraud.cfe.agent;

import org.junit.Test;

import financial.fraud.cfe.algorithm.BagOfWords;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

public class AlgorithmBagOfWordsTest {

	@Test
	public void testSolve() {
		BagOfWords abw = new BagOfWords(new CFEManualLargeDocUnit());
		int response = abw.solve(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Bankruptcy Fraud\\Bankruptcy Fraud 1.txt"));
//		int response = abw.solve(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 13.txt"));
		abw.printOptionFrequencies();
		System.out.printf("%d\n", response);
	}

}
