package financial.fraud.cfe.ir.lucene;

import java.util.HashMap;

public class LuceneUtil {

	// a lookup table for exam section given question section. That is,
	// question section name is the key while exam section name is the value.
	// This is used by the lucene-based concept match algos which, given a question
	// and its associated question section, must find the exam section name so that they
	// can locate the lucene index associated with that question.
	//
	// recall that lucene index dirs are in a directory structure as follows:
	// <exam section>/<question section>/lucene index.
	private static HashMap<String, String> examSectionLookup;
	
	
	// a lookup table for index question section given question section.  That is,
	// the index dir directory structure is not quite lined up exactly the way the
	// exam section/question section structure is laid out.  The index dir structure
	// is created based on the question section assignments to the manual section objects
	// of the cfe manual object.  Each manual section has one question section field.  
	// However, there are 2!!!! question sections that are serviced by a single manual
	// section in at least one instance - the manual section, "Interview Theory and Practice"
	// supports both of the following question sections:  "Interview Theory and Practice"
	// AND "Interviewing Suspects and Signed Statements".
	private static HashMap<String, String> indexQuestionSectionLookup;

	public static HashMap<String, String> getExamSectionLookup() {
		if (examSectionLookup == null)
			buildExamSectionLookup();

		return examSectionLookup;
	}
	
	public static HashMap<String, String> getIndexQuestionSectionLookup() {
		if(indexQuestionSectionLookup == null)
			buildIndexQuestionSectionLookup();
		
		return indexQuestionSectionLookup;
	}
	
	public static String getExamSection(String questionSection) {
		if (examSectionLookup == null)
			buildExamSectionLookup();
		return examSectionLookup.get(questionSection);
		
	}
	
	public static String getIndexQuestionSection(String questionSection) {
		if(indexQuestionSectionLookup == null)
			buildIndexQuestionSectionLookup();
		return indexQuestionSectionLookup.get(questionSection);
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

		// Note - v.2.0.1 - 2016/01/13 - Note that this next row had to be added after the fact
		// because the manual section objects of the cfe manual object allow for one field value
		// for the question section field for each manual section object. Unfortunately, there
		// are two question sections ("Interview Theory and Application" and "Interviewing
		// Suspects and Signed Statements") serviced by a single manual section. So, this second
		// entry was left out of the programmatically generated lookup list. This problem also
		// precipitated the creation of the index question section lookup list down below...
		examSectionLookup.put("Interview Theory and Application", "Investigation");

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

	private static void buildIndexQuestionSectionLookup() {
		indexQuestionSectionLookup = new HashMap<String, String>();

		indexQuestionSectionLookup.put("Bankruptcy Fraud", "Bankruptcy Fraud");
		indexQuestionSectionLookup.put("Basic Accounting Concepts", "Basic Accounting Concepts");
		indexQuestionSectionLookup.put("Bribery and Corruption", "Bribery and Corruption");
		indexQuestionSectionLookup.put("Cash Receipts Schemes", "Cash Receipts Schemes");
		indexQuestionSectionLookup.put("Check and Credit Card Fraud", "Check and Credit Card Fraud");
		indexQuestionSectionLookup.put("Computer and Internet Fraud", "Computer and Internet Fraud");
		indexQuestionSectionLookup.put("Consumer Fraud", "Consumer Fraud");
		indexQuestionSectionLookup.put("Contract and Procurement Fraud", "Contract and Procurement Fraud");
		indexQuestionSectionLookup.put("Financial Institution Fraud", "Financial Institution Fraud");
		indexQuestionSectionLookup.put("Financial Statement Fraud", "Financial Statement Fraud");
		indexQuestionSectionLookup.put("Fraudulent Disbursements", "Fraudulent Disbursements");
		indexQuestionSectionLookup.put("Health Care Fraud", "Health Care Fraud");
		indexQuestionSectionLookup.put("Insurance Fraud", "Insurance Fraud");
		indexQuestionSectionLookup.put("Introduction to Fraud Examination", "Introduction to Fraud Examination");
		indexQuestionSectionLookup.put("Inventory and Other Assets", "Inventory and Other Assets");
		indexQuestionSectionLookup.put("Manager's and Auditor's Responsibilities", "Manager's and Auditor's Responsibilities");
		indexQuestionSectionLookup.put("Money Laundering", "Money Laundering");
		indexQuestionSectionLookup.put("Public Sector Fraud", "Public Sector Fraud");
		indexQuestionSectionLookup.put("Securities Fraud", "Securities Fraud");
		indexQuestionSectionLookup.put("Tax Fraud", "Tax Fraud");
		indexQuestionSectionLookup.put("Theft of Intellectual Property", "Theft of Intellectual Property");

		indexQuestionSectionLookup.put("Criminology", "Criminology");
		indexQuestionSectionLookup.put("Ethics for Fraud Examiners", "Ethics for Fraud Examiners");
		indexQuestionSectionLookup.put("Fraud Prevention Programs", "Fraud Prevention Programs");
		indexQuestionSectionLookup.put("Fraud Risk Assessment", "Fraud Risk Assessment");
		indexQuestionSectionLookup.put("Occupational Fraud", "Occupational Fraud");
		indexQuestionSectionLookup.put("Organizational Crime", "Organizational Crime");
		indexQuestionSectionLookup.put("Punishment", "Punishment");
		indexQuestionSectionLookup.put("White-Collar Crime", "White-Collar Crime");

		indexQuestionSectionLookup.put("Analyzing Documents", "Analyzing Documents");
		indexQuestionSectionLookup.put("Covert Examinations", "Covert Examinations");
		indexQuestionSectionLookup.put("Data Analysis", "Data Analysis");
		indexQuestionSectionLookup.put("Digital Forensics", "Digital Forensics");
		indexQuestionSectionLookup.put("Illicit Transactions", "Illicit Transactions");
		indexQuestionSectionLookup.put("Interviewing Suspects and Signed Statements", "Interviewing Suspects and Signed Statements");

		// Note - v.2.0.1 - 2016/01/13 - Note that unlike the rest of these entries in this table
		// this entry below maps interview theory and application to a *different* question section.
		// this is because there is no index directory whose question section directory is 
		// "Interview theory and practice".  The docs for this section are all in 
		// the index directory "Interviewing Suspects and Signed Statements".
		indexQuestionSectionLookup.put("Interview Theory and Application", "Interviewing Suspects and Signed Statements");

		indexQuestionSectionLookup.put("Sources of Information", "Sources of Information");
		indexQuestionSectionLookup.put("Written Reports", "Written Reports");

		indexQuestionSectionLookup.put("Civil Justice System", "Civil Justice System");
		indexQuestionSectionLookup.put("Criminal Prosecutions for Fraud", "Criminal Prosecutions for Fraud");
		indexQuestionSectionLookup.put("Evidence", "Evidence");
		indexQuestionSectionLookup.put("Law Related to Fraud", "Law Related to Fraud");
		indexQuestionSectionLookup.put("Legal Rights of Employees", "Legal Rights of Employees");
		indexQuestionSectionLookup.put("Overview of the Legal System", "Overview of the Legal System");
		indexQuestionSectionLookup.put("Testifying as an Expert Witness", "Testifying as an Expert Witness");

	}

}
