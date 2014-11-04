package financial.fraud.cfe.util;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.junit.Test;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.algorithm.AlgorithmType;

public class ProfileDataTest {

	@Test
	public void testInsert() {
		ProfileData pd = new ProfileData();

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

			// this is a simple test to make sure data is loaded into the array
			// properly. This simply sets the even indexed values to true and odd to false.
			// This does NOT exercise the algorithms to get the values.
			boolean[] results = new boolean[AlgorithmType.values().length];
			for (int i = 0; i < AlgorithmType.values().length; i++) {
				if (i % 2 == 0)
					results[i] = true;
				else
					results[i] = false;
			}
			pd.insert(q, results);

		}
		pd.calculate();
		System.out.println(pd);
	}

	@Test
	public void testLoad() {
		try {
			ProfileData pd = new ProfileData();
			pd.load();
			System.out.println(pd);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}
