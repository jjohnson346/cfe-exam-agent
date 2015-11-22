package financial.fraud.cfe.agent;

import org.junit.Test;

import financial.fraud.cfe.algorithm.CompositeFrequency;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

public class AlgorithmCompositeFrequencyTest {

	@Test
	public void testSolve() {

		// 2.0.0 - remove CFEManual from constructor call.
		// CompositeFrequency acf = new CompositeFrequency(new
		// CFEManualLargeDocUnit);
		CompositeFrequency acf = new CompositeFrequency();

		CFEExamQuestion q = new CFEExamQuestion(
				"Basic Accounting Concepts 6.txt");
		// 2.0.0 - add CFEManual object to solve() call.
		int response = acf.solve(q, new CFEManualLargeDocUnit());

		acf.printOptionFrequencies();
		System.out.printf("%s%d\n", "Agent response index: ", response);
		System.out.printf("%s%d\n", "Correct response index: ",
				q.correctResponse);
	}

}
