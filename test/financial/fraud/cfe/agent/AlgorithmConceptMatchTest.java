package financial.fraud.cfe.agent;

import org.junit.Test;

import financial.fraud.cfe.algorithm.ConceptMatch;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

public class AlgorithmConceptMatchTest {

	@Test
	public void testSolve() {
		CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();
		CFEExamQuestion question = new CFEExamQuestion("Interview Theory and Application 27.txt");
		
		ConceptMatch acm = new ConceptMatch(cfeManual);
		
		int response = acm.solve(question);
		System.out.println(response);
	}

}
