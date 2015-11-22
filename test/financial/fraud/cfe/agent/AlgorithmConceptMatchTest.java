package financial.fraud.cfe.agent;

import org.junit.Test;

import financial.fraud.cfe.algorithm.ConceptMatch;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

public class AlgorithmConceptMatchTest {

	@Test
	public void testSolve() {
		CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();
		CFEExamQuestion question = new CFEExamQuestion(
				"Interview Theory and Application 27.txt");

		// ConceptMatch acm = new ConceptMatch(cfeManual);
		// 2.0.0 - remove cfeManual from constructor call.
		ConceptMatch acm = new ConceptMatch();

		// 2.0.0 - add cfeManual to solve() call.
		// int response = acm.solve(question);
		int response = acm.solve(question, cfeManual);
		System.out.println(response);
	}

}
