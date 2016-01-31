package financial.fraud.cfe.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;

/**
 * The QuestionServer class acts as an interface for retrieving CFEExamQuestion objects. The class creates a private
 * list of of CFEExamQuestion objects (typically by traversing the question files in the exam questions directory), and
 * provides a concise interface, (hasNext(), next()) for retrieving each one in sequence. This class is currently used
 * by the Profiler class.
 * 
 *
 * @author jjohnson346
 *
 */
public class QuestionServer {

	private List<String> questionFileNames; // the list of question file names

	private LinkedHashSet<CFEExamQuestion> questions; // the list of
														// CFEExamQuestion
														// objects

	private Iterator<CFEExamQuestion> iter; // an iterator for traversing the
											// questions list

	/**
	 * The one-arg constructor initializes the questions reference to a list of CFEExamQuestions objects, created by
	 * traversing the files in the exam questions directory.
	 */
	public QuestionServer(String directory) {

		Logger.getInstance().println("Initializing question server...", DetailLevel.MEDIUM);

		questionFileNames = getQuestionFileNames(directory);
		questions = getQuestions();
		iter = questions.iterator();

		Logger.getInstance().println("Question server initialization complete.", DetailLevel.MEDIUM);
	}

	/**
	 * initializes the questions reference to a list of CFEExamQuestions objects, created by traversing the files in the
	 * exam questions directory and filtering out all those questions whose profile do not equal the profile passed in
	 * as an argument.
	 */
	public QuestionServer(String directory, int profile) {

		Logger.getInstance().println("Initializing question server...", DetailLevel.MEDIUM);

		questionFileNames = getQuestionFileNames(directory);
		questions = getQuestions(profile);
		iter = questions.iterator();

		Logger.getInstance().println("Question server initialization complete.", DetailLevel.MEDIUM);
	}

	/**
	 * returns the number of questions to be served by this object, given the directory, and, if specified, the question
	 * profile specified to the constructor
	 * 
	 * @return the count of questions
	 */
	public int count() {
		return questions.size();
	}

	/**
	 * returns whether the iterator is at the end of the list of CFEExamQuestions objects
	 * 
	 * @return true if not at the end of the list
	 */
	public boolean hasNext() {
		return iter.hasNext();
	}

	/**
	 * 
	 * @return the next CFEExamQuestion object in the questions list
	 */
	public CFEExamQuestion next() {
		return iter.next();
	}

	public int size() {
		return questions.size();
	}

	/**
	 * returns a list of all of the question files in the exam questions directory.
	 * 
	 * @return a list of strings for the question file names
	 */
	private List<String> getQuestionFileNames(String directory) {
		LinkedList<String> questionFileNames = new LinkedList<String>();

		try {
			File questionsDir = new File(directory);
			String examQuestionsPathName = questionsDir.getCanonicalPath();

			for (String testAreaDirName : questionsDir.list()) {

				String testAreaPathName = examQuestionsPathName + File.separator + testAreaDirName;
				File testAreaDir = new File(testAreaPathName);

				for (String sectionDirName : testAreaDir.list()) {

					String sectionPathName = testAreaPathName + File.separator + sectionDirName;
					File sectionDir = new File(sectionPathName);

					for (String fileName : sectionDir.list()) {

						String questionFileName = sectionPathName + File.separator + fileName;
						questionFileNames.add(questionFileName);
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return questionFileNames;
	}

	/**
	 * returns the linked hash set of questions compiled by traversing the question files in each exam section directory
	 * in the the exam questions directory.
	 * 
	 * @return a list of CFEExamQuestion objects
	 */
	private LinkedHashSet<CFEExamQuestion> getQuestions() {
		LinkedHashSet<CFEExamQuestion> questions = new LinkedHashSet<CFEExamQuestion>();
		for (String fileName : questionFileNames) {
			CFEExamQuestion q = new CFEExamQuestion(fileName);
			if (!questions.add(q))
				System.out.println("duplicate question: " + q);
		}
		return questions;
	}

	/**
	 * returns the linked hash set of questions compiled by traversing the question files in each exam section directory
	 * in the the exam questions directory, but unlike the no-arg implementation of this function, filters out those
	 * questions whose profile do not match the profile argument.
	 * 
	 * @param profile
	 *            the profile of the questions to be added to the collection
	 * 
	 * @return a list of CFEExamQuestion objects whose profile matches the profile argument
	 */
	private LinkedHashSet<CFEExamQuestion> getQuestions(int profile) {
		LinkedHashSet<CFEExamQuestion> questions = new LinkedHashSet<CFEExamQuestion>();
		for (String fileName : questionFileNames) {
			CFEExamQuestion q = new CFEExamQuestion(fileName);
			// attempt to add only those questions whose profile match the
			// profile parameter.
			if (q.getProfile().getProfileIndex() == profile)
				if (!questions.add(q))
					System.out.println("duplicate question: \n" + q);
		}
		return questions;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// 2016/01/04 - version 2.0.0 - note that current profile indices are
		// set as follows:
		//
		// AllOfTheAbove * 2^7 +
		// NoneOfTheAbove * 2^6 +
		// Except * 2^5 +
		// TrueFalse * 2^4 +
		// TrueFalseAbsolute * 2^3
		// Definition * 2^2 +
		// DefinitionNot * 2^1 +
		// HasLongOptions * 2^0

		// QuestionServer qs = new QuestionServer("exam questions - all");

		// definition questions:
		// QuestionServer qs = new QuestionServer("exam questions - training set", 4);
		// QuestionServer qs = new QuestionServer("exam questions - test set", 4);

		// definition/none of above questions:
		// QuestionServer qs = new QuestionServer("exam questions - training set", 68);
		// QuestionServer qs = new QuestionServer("exam questions - test set",68);

		// definition/not questions:
		QuestionServer qs = new QuestionServer("exam questions - training set", 134);

		Scanner input = new Scanner(System.in);

		int count = 0;
		while (qs.hasNext()) {
			CFEExamQuestion q = qs.next();
			// System.out.println(q.section);
			System.out.println("Question " + ++count + " of " + qs.size() + ":");
			System.out.println(q);
			System.out.println(q.getFormattedCorrectResponse());
			System.out.println();
			// input.nextLine();
		}
		System.out.println("count: " + count);
	}

}
