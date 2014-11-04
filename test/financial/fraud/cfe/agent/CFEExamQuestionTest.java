package financial.fraud.cfe.agent;

import org.junit.Test;

public class CFEExamQuestionTest {

	@Test
	public void testCFEExamQuestionString() {
		CFEExamQuestion q1 = new CFEExamQuestion("Health Care Fraud 9.txt");
		System.out.println(q1);
		System.out.println("Correct response:  " + q1.getFormattedCorrectResponse());
	}

}
