package financial.fraud.cfe.util;

import java.util.ArrayList;

import org.junit.Test;

import financial.fraud.cfe.agent.CFEExamQuestion;

public class FeatureExceptTest {

	@Test
	public void testExists() {

		ArrayList<CFEExamQuestion> questions = new ArrayList<CFEExamQuestion>();

		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Basic Accounting Concepts\\Basic Accounting Concepts 5.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Bribery and Corruption\\Bribery and Corruption 7.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 11.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 12.txt"));

		for (CFEExamQuestion q : questions) {
			System.out.println(q);

			FeatureExcept f = new FeatureExcept(q);
			System.out.println("Feature Except exists: " + f.exists());
			System.out.printf("\n\n");
		}
	}
}
