package financial.fraud.cfe.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;

/**
 * The QuestionServer class acts as an interface for retrieving CFEExamQuestion objects.
 * The class creates a private list of of CFEExamQuestion objects (typically by traversing
 * the question files in the exam questions directory), and provides a concise interface, 
 * (hasNext(), next()) for retrieving each one in sequence.  This class is currently used by 
 * the Profiler class.
 * 
 *
 * @author jjohnson346
 *
 */
public class QuestionServer {

	private List<String> questionFileNames;			// the list of question file names
	
	private LinkedHashSet<CFEExamQuestion> questions;			// the list of CFEExamQuestion objects
	
	private Iterator<CFEExamQuestion> iter;			// an iterator for traversing the questions list
	
	
	/**
	 * The no-arg constructor initializes the questions reference to a list of 
	 * CFEExamQuestions objects, created by traversing the files in the exam questions
	 * directory.
	 */
	public QuestionServer(String directory) {
		
		Logger.getInstance().println("Initializing question server...", DetailLevel.MEDIUM);
		
		questionFileNames = getQuestionFileNames(directory);
		questions = getQuestions();
		iter = questions.iterator();
		
		Logger.getInstance().println("Question server initialization complete.", DetailLevel.MEDIUM);
	}
	
	/**
	 * returns whether the iterator is at the end of the list
	 * of CFEExamQuestions objects
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

				String testAreaPathName = examQuestionsPathName + "//" + testAreaDirName;
				File testAreaDir = new File(testAreaPathName);

				for (String sectionDirName : testAreaDir.list()) {

					String sectionPathName = testAreaPathName + "//" + sectionDirName;
					File sectionDir = new File(sectionPathName);

					for (String fileName : sectionDir.list()) {

						String questionFileName = sectionPathName + "//" + fileName;
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
	 * returns the linked hash set of questions compiled by traversing the question files 
	 * in each exam section directory in the the exam questions directory.
	 * 
	 * @return a list of CFEExamQuestion objects
	 */
	private LinkedHashSet<CFEExamQuestion> getQuestions() {
		LinkedHashSet<CFEExamQuestion> questions = new LinkedHashSet<CFEExamQuestion>();
		for(String fileName : questionFileNames) {
			CFEExamQuestion q = new CFEExamQuestion(fileName);
			if(!questions.add(q)) 
				System.out.println("duplicate question: " + q);
		}
		return questions;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		QuestionServer qs = new QuestionServer("exam questions - all");

		int count = 0;
		while(qs.hasNext()) {
			CFEExamQuestion q = qs.next();
			System.out.println(q.section);
			count++;
		}
		System.out.println("count: " + count);
	}

}
