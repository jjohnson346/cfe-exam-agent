package ontol.training.set;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import financial.fraud.cfe.agent.CFEExamQuestion;

public class TrainingAndTestSetsGenerator {

	private final int TEST_SET_SIZE = 200; // stores the intended size of the test set.

	private Map<String, String> examSectionLookup;

	private Map<String, Integer> testSetCounts; // stores the counts for the number of questions in each section
												// that should exist in the test set. See
												// buildTestSetCounts()

	private Map<String, Integer> questionCollectionCounts; // stores the counts for the number of question in
															// each section that exist in the total question collection
															// from which we make the test and training sets

	private Map<String, Integer> trainingSetCounts; // stores the counts for the number of question in each
													// question section that should exist in the training set.

	private QuestionCollection questionCollection;
	
	private QuestionCollection testSet;
	
	private QuestionCollection trainingSet;
	

	public TrainingAndTestSetsGenerator() {
		questionCollection = new QuestionCollection("exam questions - all");
		buildQuestionCollectionCounts();
		buildTestSetCounts();
		buildTrainingSetCounts();
	}

	public void generateTrainingAndTestSets() {

	}
	
	/**
	 * builds the questionCollectionCounts map that stores the number of questions in each question section of the
	 * question collection.
	 */
	private void buildQuestionCollectionCounts() {
		questionCollectionCounts = new HashMap<String, Integer>();
		for (String examSection : questionCollection.getExamSections()) {
			for (String questionSection : questionCollection.getQuestionSections(examSection)) {
				questionCollectionCounts.put(examSection + ".." + questionSection,
						questionCollection.size(examSection, questionSection));
			}
		}
	}

	/**
	 * builds the testSetCounts map that stores the number of questions that should be inserted into the test set from
	 * the question collection. these numbers are based on the proportionate share of questions for each section
	 * relative to the entire population of questions, (making sure every section has at least one question in the test
	 * set).
	 */
	private void buildTestSetCounts() {
		testSetCounts = new HashMap<String, Integer>();
		int sectionsCount = questionCollection.sectionsCount();
		int totalTestSetCount = 0;
		int totalTestSetCountPrior = 0;
		int totalQuestionsCount = questionCollection.size();

		// declare these traversal variables outside of for loops
		// so that we can refer to them outside of loops (in order to
		// get last values for examSection and questionSection.
		String examSection = null;
		String questionSection = null;
		for (int i = 0; i < questionCollection.getExamSections().size(); i++) {
			examSection = questionCollection.getExamSections().get(i);
			for (int j = 0; j < questionCollection.getQuestionSections(examSection).size(); j++) {
				questionSection = questionCollection.getQuestionSections(examSection).get(j);
				int sectionQuestionsCount = questionCollection.size(examSection, questionSection);

				// set count for section to be proportionate to the number of questions for section in
				// total collection, but adjust to make sure that every section has at least one question.
				int testSetCount = (int)Math.round(1 + (double)sectionQuestionsCount / totalQuestionsCount * (TEST_SET_SIZE - sectionsCount));
						testSetCounts.put(examSection + ".." + questionSection, testSetCount);
				totalTestSetCountPrior = totalTestSetCount;
				totalTestSetCount += testSetCount;

			}
		}
		testSetCounts.put(examSection + ".." + questionSection, Math.max(0, TEST_SET_SIZE - totalTestSetCountPrior));
	}

	private void buildTrainingSetCounts() {
		trainingSetCounts = new HashMap<String, Integer>();
		for (String section : questionCollectionCounts.keySet()) {
			int trainingSetSectionCount = Math.max(0,
					questionCollectionCounts.get(section) - testSetCounts.get(section));
			trainingSetCounts.put(section, trainingSetSectionCount);
		}
	}

	public Map<String, Integer> getQuestionCollectionCounts() {
		return questionCollectionCounts;
	}

	public Map<String, Integer> getTestSetCounts() {
		return testSetCounts;
	}

	private void buildExamSectionLookup() {
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
		examSectionLookup.put("Managers and Auditors Responsibilities", "Financial Transactions and Fraud Schemes");
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
		examSectionLookup.put("Interview Theory and Application", "Investigation");
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

	public void buildSets() {
		testSet = new QuestionCollection();
		trainingSet = new QuestionCollection();
		
		for (String examSection : questionCollection.getExamSections()) {
			for (String questionSection : questionCollection.getQuestionSections(examSection)) {
				ArrayList<CFEExamQuestion> questions = questionCollection.getQuestions(examSection, questionSection);
				String sectionKey = examSection + ".." + questionSection;
				
				// add randomly selected questions for this section to the test set.
				for(int i = 0; i < testSetCounts.get(sectionKey); i++) {
					int selectedIndex = (int)(Math.random() * questions.size());
					testSet.add(questions.get(selectedIndex), examSection, questionSection);
					questions.remove(selectedIndex);
				}
				
				// add the remaining questions to the training set.
				trainingSet.addAll(questions, examSection, questionSection);
				
			}
		}
	}
	
	public void writeTestSetToFiles() {
		testSet.writeToFiles("exam questions - test set");
	}
	
	public void writeTrainingSetToFiles() {
		trainingSet.writeToFiles("exam questions - training set");
	}

	public void cleanCollectionDir(String dirName) {
		File testSetDir = new File(dirName);
		try {
			delete(testSetDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
				System.out.println("Directory deleted: " + file.getName());

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					System.out.println("Directory deleted: " + file.getName());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			System.out.println("File deleted: " + file.getName());
		}
	}

	public static void main(String[] args) {
		// QuestionServer qs = new QuestionServer();
		//
		// int count = 0;
		// while(qs.hasNext()) {
		// CFEExamQuestion q = qs.next();
		// System.out.println(q.section);
		// count++;
		// }
		// System.out.println("count: " + count);

		TrainingAndTestSetsGenerator tsg = new TrainingAndTestSetsGenerator();
		printCounts(tsg);
	}

	private static void printCounts(TrainingAndTestSetsGenerator tsg) {
		int questionCollectionTotalCount = 0;
		int trainingSetTotalCount = 0;
		int testSetTotalCount = 0;

		System.out.printf("%40s%50s%10s%10s%10s\n", "Exam Section", "Question Section", "All", "Training", "Test");
		System.out.printf("%120s\n", StringUtils.repeat("-", 120));

		for (String examSection : tsg.questionCollection.getExamSections()) {
			for (String questionSection : tsg.questionCollection.getQuestionSections(examSection)) {
				String sectionKey = examSection + ".." + questionSection;

				int questionCollectionSectionCount = tsg.questionCollectionCounts.get(sectionKey);
				int trainingSetSectionCount = tsg.trainingSetCounts.get(sectionKey);
				int testSetSectionCount = tsg.testSetCounts.get(sectionKey);

				System.out.printf("%40s%50s%10d%10d%10d\n", examSection, questionSection,
						questionCollectionSectionCount, trainingSetSectionCount, testSetSectionCount);

				questionCollectionTotalCount += questionCollectionSectionCount;
				trainingSetTotalCount += trainingSetSectionCount;
				testSetTotalCount += testSetSectionCount;
			}
		}
		System.out.printf("%90s%30s\n", "", StringUtils.repeat("-", 30));
		System.out.printf("%90s%10d%10d%10d\n", "", questionCollectionTotalCount, trainingSetTotalCount,
				testSetTotalCount);
		
		tsg.buildSets();
		tsg.writeTestSetToFiles();
		tsg.writeTrainingSetToFiles();
	}

}
