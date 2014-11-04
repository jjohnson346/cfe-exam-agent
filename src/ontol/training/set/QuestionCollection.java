package ontol.training.set;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import financial.fraud.cfe.agent.CFEExamQuestion;

public class QuestionCollection {

	private Map<String, Map<String, ArrayList<CFEExamQuestion>>> questionCollection; // stores the list of questions for each
																				// exam section, question section

	/**
	 * a no-arg constructor is available so that a question collection can be built from scratch (as opposed to creating
	 * one from files in a directory, as is the case for the one-arg constructor).
	 */
	public QuestionCollection() {
		questionCollection = new HashMap<String, Map<String, ArrayList<CFEExamQuestion>>>();
	}

	/**
	 * constructor sets up the data structure containing the document collection, organized by exam section, and then by
	 * question section.
	 * 
	 * @param cfeManualType
	 */
	public QuestionCollection(String questionCollectionDirName) {

		questionCollection = new HashMap<String, Map<String, ArrayList<CFEExamQuestion>>>();

		File questionCollectionDir = new File(questionCollectionDirName);

		if (!questionCollectionDir.exists()) {
			System.out.println("question collection directory does not exist.");
			System.exit(1);
		}

		try {
			// traverse exam section directories.
			for (File examSectionDir : questionCollectionDir.listFiles()) {
				if (examSectionDir.isDirectory()) {
					Map<String, ArrayList<CFEExamQuestion>> examSectionQuestionCollection = new HashMap<String, ArrayList<CFEExamQuestion>>();

					// traverse question section directories, for each exam section directory.
					for (File questionSectionDir : examSectionDir.listFiles()) {
						if (questionSectionDir.isDirectory()) {
							ArrayList<CFEExamQuestion> questions = new ArrayList<CFEExamQuestion>();

							// traverse the doc files in the question section directory.
							// build the list of documents.
							for (File questionFile : questionSectionDir.listFiles()) {
								if (questionFile.isFile()) {
									questions.add(new CFEExamQuestion(questionFile.getCanonicalPath()));
								}
							}
							examSectionQuestionCollection.put(questionSectionDir.getName(), questions);
						}
					}
					questionCollection.put(examSectionDir.getName(), examSectionQuestionCollection);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * adds a question to the collection, placing it in the collection according to the exam section and question
	 * section arguments also passed in as input.
	 * 
	 * @param question
	 *            the question to add to the collection
	 * @param examSection
	 *            the exam section for the question
	 * @param questionSection
	 *            the question section for the question
	 */
	public void add(CFEExamQuestion question, String examSection, String questionSection) {
		// if an entry for the exam section does not already exist in the question collection,
		// add it.
		if (!questionCollection.containsKey(examSection))
			questionCollection.put(examSection, new HashMap<String, ArrayList<CFEExamQuestion>>());

		// if an entry for the question section does not already exist for the given exam
		// section, add it - along with a new (empty) array list of questions.
		Map<String, ArrayList<CFEExamQuestion>> examSectionQuestionCollection = questionCollection.get(examSection);
		if (!examSectionQuestionCollection.containsKey(questionSection))
			examSectionQuestionCollection.put(questionSection, new ArrayList<CFEExamQuestion>());
	
		questionCollection.get(examSection).get(questionSection).add(question);
	}
	
	public void addAll(Collection<CFEExamQuestion> questions, String examSection, String questionSection) {
		// if an entry for the exam section does not already exist in the question collection,
		// add it.
		if (!questionCollection.containsKey(examSection))
			questionCollection.put(examSection, new HashMap<String, ArrayList<CFEExamQuestion>>());

		// if an entry for the question section does not already exist for the given exam
		// section, add it - along with a new (empty) array list of questions.
		Map<String, ArrayList<CFEExamQuestion>> examSectionQuestionCollection = questionCollection.get(examSection);
		if (!examSectionQuestionCollection.containsKey(questionSection))
			examSectionQuestionCollection.put(questionSection, new ArrayList<CFEExamQuestion>());
		
		ArrayList<CFEExamQuestion> sectionQuestions = questionCollection.get(examSection).get(questionSection);
		sectionQuestions.addAll(questions);
	
	}

	/**
	 * returns the list of documents for a particular exam section and question section, supplied as input.
	 * 
	 * @param examSection
	 * @param questionSection
	 * @return
	 */
	public ArrayList<CFEExamQuestion> getQuestions(String examSection, String questionSection) {
		Map<String, ArrayList<CFEExamQuestion>> examSectionDocCollection = questionCollection.get(examSection);
		return examSectionDocCollection.get(questionSection);
	}

	/**
	 * returns the list of names for question sections for a given exam section
	 * 
	 * @param examSection
	 * @return
	 */
	public List<String> getQuestionSections(String examSection) {
		return new ArrayList<String>(questionCollection.get(examSection).keySet());
	}

	/**
	 * returns the list of exam section names
	 * 
	 * @return
	 */
	public List<String> getExamSections() {
		return new ArrayList<String>(questionCollection.keySet());
	}

	/**
	 * returns the number of questions in the entire collection.
	 * 
	 * @return the number of questions in the question collection
	 */
	public int size() {
		int count = 0;
		for (String examSection : questionCollection.keySet()) {
			for (String questionSection : questionCollection.get(examSection).keySet()) {
				count += questionCollection.get(examSection).get(questionSection).size();
			}
		}
		return count;
	}

	/**
	 * returns the number of questions in a given exam section.
	 * 
	 * @param examSection
	 *            the exam section for which to return the count of questions
	 * @return the number of questions in the given exam section in the collection
	 */
	public int size(String examSection) {
		int count = 0;
		for (String questionSection : questionCollection.get(examSection).keySet()) {
			count += questionCollection.get(examSection).get(questionSection).size();
		}
		return count;
	}

	/**
	 * returns the number of questions in a given exam section, question section
	 * 
	 * @param examSection
	 * @param questionSection
	 * @return the number of questions in the exam section, question section
	 */
	public int size(String examSection, String questionSection) {
		return questionCollection.get(examSection).get(questionSection).size();
	}

	/**
	 * returns count of the number of question sections in the question collection.
	 * 
	 * @return	the number of question section in the collection
	 */
	public int sectionsCount() {
		int count = 0;
		for (String examSection : questionCollection.keySet()) {
			for (String questionSection : questionCollection.get(examSection).keySet()) {
				count++;
			}
		}
		return count;
	}
	/**
	 * persists the collection of questions to a set of files, with a directory structure such that there is a directory
	 * for each exam section, and then a sub-directory for each question section.
	 * 
	 * TODO: should we be overriding the serialize method here?
	 */
	public void writeToFiles(String questionCollectionDirName) {
		cleanCollectionDir(questionCollectionDirName);

		File questionCollectionDir = new File(questionCollectionDirName);
		if (!questionCollectionDir.exists())
			questionCollectionDir.mkdir();


		for (String examSection : questionCollection.keySet()) {
			String examSectionDirName = questionCollectionDirName + "/" + examSection;
			File examSectionDir = new File(examSectionDirName);
			if (!examSectionDir.exists())
				examSectionDir.mkdir();

			for (String questionSection : questionCollection.get(examSection).keySet()) {
				String questionSectionDirName = examSectionDirName + "/" + questionSection;
				File questionSectionDir = new File(questionSectionDirName);
				if (!questionSectionDir.exists())
					questionSectionDir.mkdir();

				for (CFEExamQuestion question : questionCollection.get(examSection).get(questionSection)) {
					question.writeToFile(questionSectionDirName);
				}
			}

		}

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
		// create a question collection from a directory containing a battery of question files.
		QuestionCollection qc = new QuestionCollection("exam questions - all");

		List<CFEExamQuestion> questions = qc.getQuestions("Financial Transactions and Fraud Schemes",
				"Bankruptcy Fraud");

		for (CFEExamQuestion q : questions)
			System.out.println(q);

		System.out.println("exam sections: " + qc.getExamSections() + "\n");
		System.out.println("question sections for Fraud Prevention and Deterrence: "
				+ qc.getQuestionSections("Fraud Prevention and Deterrence") + "\n");
		System.out.println("question collection size: " + qc.size() + "\n"); // should be 1500
		System.out.println("number of questions in Fraud Prevention and Deterrence: "
				+ qc.size("Fraud Prevention and Deterrence") + "\n");
		System.out.println("number of questions in Fraud Prevention and Deterrence, Punishment: "
				+ qc.size("Fraud Prevention and Deterrence", "Punishment") + "\n"); // should be 12
		System.out.println("number of sections: " + qc.sectionsCount());

		// now create a question collection from scratch.
		qc = new QuestionCollection();
		
		qc.add(new CFEExamQuestion("exam questions - all/Fraud Prevention and Deterrence/Punishment/Punishment 1.txt"),
				"Fraud Prevention and Deterrence", "Punishment");
		qc.add(new CFEExamQuestion("exam questions - all/Fraud Prevention and Deterrence/Punishment/Punishment 2.txt"),
				"Fraud Prevention and Deterrence", "Punishment");
		qc.add(new CFEExamQuestion("exam questions - all/Fraud Prevention and Deterrence/Punishment/Punishment 3.txt"),
				"Fraud Prevention and Deterrence", "Punishment");
		qc.add(new CFEExamQuestion("exam questions - all/Financial Transactions and Fraud Schemes/Bankruptcy Fraud/Bankruptcy Fraud 1.txt"),
				"Financial Transactions and Fraud Schemes", "Bankruptcy Fraud");
		qc.add(new CFEExamQuestion("exam questions - all/Financial Transactions and Fraud Schemes/Bankruptcy Fraud/Bankruptcy Fraud 10.txt"),
				"Financial Transactions and Fraud Schemes", "Bankruptcy Fraud");
		qc.add(new CFEExamQuestion("exam questions - all/Financial Transactions and Fraud Schemes/Health Care Fraud/Health Care Fraud 2.txt"),
				"Financial Transactions and Fraud Schemes", "Health Care Fraud");
		qc.add(new CFEExamQuestion("exam questions - all/Financial Transactions and Fraud Schemes/Health Care Fraud/Health Care Fraud 5.txt"),
				"Financial Transactions and Fraud Schemes", "Health Care Fraud");

		qc.writeToFiles("exam questions - test set");
	}
}
