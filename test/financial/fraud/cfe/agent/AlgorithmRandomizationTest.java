package financial.fraud.cfe.agent;

import org.junit.Test;

import financial.fraud.cfe.algorithm.Randomization;
import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

public class AlgorithmRandomizationTest {

	@Test
	public void testSolve() {
		Randomization ar = new Randomization();
		
		// 2.0.0 - create cfeManual object and pass to solve().
		CFEManual cfeManual = new CFEManualLargeDocUnit();
		int response = 0;
		int[] counts = new int[4];
		int total = 0;
		
		CFEExamQuestion question = new CFEExamQuestion("Basic Accounting Concepts 1.txt");
		
		for(int i = 0; i < 100000; i++) {
			response = ar.solve(question, cfeManual);
			counts[response]++;
		}			
		System.out.println("COUNTS:");
		for(int i = 0; i < counts.length; i++) {
			System.out.println("count " + i + ":  " + counts[i]);
			total += counts[i];
		}
		System.out.println("total:  " + total);
	}

}
