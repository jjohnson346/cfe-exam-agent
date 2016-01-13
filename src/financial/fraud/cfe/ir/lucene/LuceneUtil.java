package financial.fraud.cfe.ir.lucene;

import java.util.HashMap;

public class LuceneUtil {

	// a lookup table for exam section given question section. That is,
	// question section name is the key while exam section name is the value.
	private static HashMap<String, String> examSectionLookup;

	public static HashMap<String, String> getExamSectionLookup() {
		if (examSectionLookup == null)
			buildExamSectionLookup();

		return examSectionLookup;
	}

	private static void buildExamSectionLookup() {
		examSectionLookup = new HashMap<String, String>();

		examSectionLookup.put("Bankruptcy Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Basic Accounting Concepts", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Bribery and Corruption", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Cash Receipts Schemes", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Check and Credit Card Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Computer and Internet Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Consumer Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Contract and Procurement Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Financial Institution Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Financial Statement Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Fraudulent Disbursements", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Health Care Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Insurance Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Introduction to Fraud Examination", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Inventory and Other Assets", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Manager's and Auditor's Responsibilities", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Money Laundering", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Public Sector Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Securities Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Tax Fraud", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("Theft of Intellectual Property", "Financial Transactions and Fraud Schemes");

		examSectionLookup.put("Criminology", "Fraud Prevention and Deterrence");
		examSectionLookup.put("Ethics for Fraud Examiners", "Fraud Prevention and Deterrence");
		examSectionLookup.put("Fraud Prevention Programs", "Fraud Prevention and Deterrence");
		examSectionLookup.put("Fraud Risk Assessment", "Fraud Prevention and Deterrence");
		examSectionLookup.put("Occupational Fraud", "Fraud Prevention and Deterrence");
		examSectionLookup.put("Organizational Crime", "Fraud Prevention and Deterrence");
		examSectionLookup.put("Punishment", "Fraud Prevention and Deterrence");
		examSectionLookup.put("White-Collar Crime", "Fraud Prevention and Deterrence");

		examSectionLookup.put("Analyzing Documents", "Investigation");
		examSectionLookup.put("Covert Examinations", "Investigation");
		examSectionLookup.put("Data Analysis", "Investigation");
		examSectionLookup.put("Digital Forensics", "Investigation");
		examSectionLookup.put("Illicit Transactions", "Investigation");
		examSectionLookup.put("Interviewing Suspects and Signed Statements", "Investigation");
		examSectionLookup.put("Sources of Information", "Investigation");
		examSectionLookup.put("Written Reports", "Investigation");

		examSectionLookup.put("Civil Justice System", "Law");
		examSectionLookup.put("Criminal Prosecutions for Fraud", "Law");
		examSectionLookup.put("Evidence", "Law");
		examSectionLookup.put("Law Related to Fraud", "Law");
		examSectionLookup.put("Legal Rights of Employees", "Law");
		examSectionLookup.put("Overview of the Legal System", "Law");
		examSectionLookup.put("Testifying as an Expert Witness", "Law");
	}

}
