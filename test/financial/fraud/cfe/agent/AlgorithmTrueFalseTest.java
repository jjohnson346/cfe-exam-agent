package financial.fraud.cfe.agent;

import org.junit.Test;

import financial.fraud.cfe.algorithm.TrueSelect;

public class AlgorithmTrueFalseTest {

	@Test
	public void testSolve() {
		TrueSelect atf = new TrueSelect();
		
		CFEExamQuestion q = new CFEExamQuestion("Health Care Fraud 1.txt");
		int response = atf.solve(q);

		System.out.printf("%s%d\n", "Agent response index: ", response);
		System.out.printf("%s%d\n", "Correct response index: ", q.correctResponse);
	}

}
