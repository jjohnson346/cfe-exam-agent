package financial.fraud.cfe.agent;

import org.junit.Test;

import financial.fraud.cfe.algorithm.CompositeFrequency;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

public class AlgorithmCompositeFrequencyTest {

	@Test
	public void testSolve() {
		CompositeFrequency acf = new CompositeFrequency(new CFEManualLargeDocUnit());
		
		CFEExamQuestion q = new CFEExamQuestion("Basic Accounting Concepts 6.txt");
		int response = acf.solve(q);

		acf.printOptionFrequencies();
		System.out.printf("%s%d\n", "Agent response index: ", response);
		System.out.printf("%s%d\n", "Correct response index: ", q.correctResponse);
	}

}
