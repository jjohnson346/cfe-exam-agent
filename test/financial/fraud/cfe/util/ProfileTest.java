package financial.fraud.cfe.util;

import java.util.ArrayList;

import org.junit.Test;

import financial.fraud.cfe.agent.CFEExamQuestion;

public class ProfileTest {

	@Test
	public void testProfile() {
		ArrayList<CFEExamQuestion> questions = new ArrayList<CFEExamQuestion>();

		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 9.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 10.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 11.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 12.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 13.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 14.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 15.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 16.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 17.txt"));
		questions.add(new CFEExamQuestion("exam questions\\Financial Transactions and Fraud Schemes\\Health Care Fraud\\Health Care Fraud 18.txt"));

		for (CFEExamQuestion q : questions) {
			System.out.println(q);
			
			Profile p = q.getProfile();
			for (FeatureType ft : FeatureType.values()) {
				System.out.println(ft + ": " + p.getFeatures()[ft.ordinal()]);
			}
			System.out.println("Profile Index: " + p.getProfileIndex());
			System.out.printf("\n\n");
		}
	}

}
