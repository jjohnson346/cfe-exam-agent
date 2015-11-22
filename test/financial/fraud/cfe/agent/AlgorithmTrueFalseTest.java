package financial.fraud.cfe.agent;

import org.junit.Test;

import financial.fraud.cfe.algorithm.TrueSelect;
import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

public class AlgorithmTrueFalseTest {

	@Test
	public void testSolve() {
		TrueSelect atf = new TrueSelect();
		
		// 2.0.0 - create cfeManual object and pass to solve() call.
		CFEManual cfeManual = new CFEManualLargeDocUnit();
		
		CFEExamQuestion q = new CFEExamQuestion("Health Care Fraud 1.txt");
		int response = atf.solve(q, cfeManual);

		System.out.printf("%s%d\n", "Agent response index: ", response);
		System.out.printf("%s%d\n", "Correct response index: ", q.correctResponse);
	}

}
