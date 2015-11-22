package financial.fraud.cfe.agent;

import org.junit.Test;

import financial.fraud.cfe.algorithm.MaxFreqPlus;
import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

public class AlgorithmAllPhraseCountTest {

	@Test
	public void testSolve() {
		CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();
		CFEExamQuestion question = new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Check and Credit Card Fraud\\Check and Credit Card Fraud 1.txt");
//		CFEExamQuestion question = new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Introduction To Fraud Examination\\Introduction to Fraud Examination 1.txt");
		
		// 2.0.0 - remove cfeManual from constructor call.
		MaxFreqPlus af = new MaxFreqPlus();
		
		int response = af.solve(question, cfeManual);
		System.out.println("Response:  " + response);
	}

}
