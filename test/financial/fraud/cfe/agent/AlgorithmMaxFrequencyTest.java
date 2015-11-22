package financial.fraud.cfe.agent;

import org.junit.Test;

import financial.fraud.cfe.algorithm.MaxFrequency;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

public class AlgorithmMaxFrequencyTest {

	@Test
	public void testSolve() {

		Logger.getInstance().setDetailLevel(DetailLevel.FULL);

		CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();
		CFEExamQuestion question = new CFEExamQuestion(
				"exam questions\\Investigation\\Covert Examinations\\Covert Examinations 12.txt");

		// 2.0.0 - remove cfeManual from constructor call.
		// MaxFrequency af = new MaxFrequency(cfeManual);
		MaxFrequency af = new MaxFrequency();

		// 2.0.0 - add cfeManual to solve call.
		// int response = af.solve(question);
		int response = af.solve(question, cfeManual);
		af.printOptionFrequencies();

		System.out.printf("%d\n", response);
	}

}
