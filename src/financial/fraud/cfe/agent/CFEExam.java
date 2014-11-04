package financial.fraud.cfe.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * An object of CFEExam represents a cfe exam.
 * @author jjohnson346
 *
 */
public class CFEExam {
	
	/**
	 * the name of the exam, based on the file name from which exam object was initialized
	 */
	public final String NAME;					
	
	/**
	 * a list of question objects representing the questions on the exam
	 */
	private List<CFEExamQuestion> questions;	
	
	/**
	 * constructor requiring the name of a file containing the exam data to initialize
	 * the exam object
	 * 
	 * @param examFileName a string giving the name of the exam file
	 */
	public CFEExam(String examFileName) {
		
		NAME = examFileName;
		questions = new LinkedList<CFEExamQuestion>();

		Scanner scanner = null;
		String questionFileName = null;
		
		try {
			scanner = new Scanner(new File(examFileName));

			// traverse the lines of the exam file, where each line
			// consists of a name of a question file.  Then, instantiate
			// a question object from the question file.
			while(scanner.hasNext()) {
				questionFileName = scanner.nextLine();
				questions.add(new CFEExamQuestion(questionFileName));
			}
			
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + examFileName);
			System.exit(1);
		} finally {
			scanner.close();
		}
	}

	/**
	 * returns a well-formatted string for the question, including a question
	 * number at the start of the question, giving the question the appearance of being part
	 * of the larger exam.
	 * 
	 * @param i the number of the question on the exam
	 * @return a string containing the well-formatted text of the question
	 */
	public String getFormattedQuestion(int i) {
		return String.format("%d%s%s",i + 1, ". ", questions.get(i));
	}
	
	/**
	 * returns a well-formatted string for the correct response to a particular
	 * question on the exam.
	 * 
	 * @param i the question number for which to retrieve the well-formatted response string
	 * @return the well-formatted response string
	 */
	public String getFormattedCorrectResponse(int i) {
		return questions.get(i).getFormattedCorrectResponse();
	}
	
	/**
	 * returns the CFEExamQuestion object for a particular question number on the test.
	 * 
	 * @param i the question number for which to retrieve the CFEExamQuestion object
	 * @return a CFEExamQuestion object corresponding to the number passed in as input
	 */
	public CFEExamQuestion getQuestion(int i) {
		return questions.get(i);
	}
	
	/**
	 * returns the section for a particular question
	 * 
	 * @param i the question number whose section is to be retrieved
	 * @return the section for the question whose number was passed in as input
	 */
	public String getSection(int i) {
		return questions.get(i).section;
	}
	
	/**
	 * returns the stem for a particular question on the test
	 * 
	 * @param i the question number whose stem is to be retrieved
	 * @return the stem of the question whose number was passed in as input
	 */
	public String getStem(int i) {
		return questions.get(i).stem;
	}
	
	/**
	 * returns a list of options for a particular question
	 * 
	 * @param i the question number options are to be retrieved
	 * @return a list of strings containing the text for the options for the question whose number was passed in as input
	 */
	public List<String> getOptions(int i) {
		return questions.get(i).options;
	}
	
	/**
	 * returns the correct response index for a particular question on the test
	 * 
	 * @param i the question number whose correct response is to be retrieved
	 * @return the index for the correct response to the question whose number was passed in as input
	 */
	public int getCorrectResponse(int i) {
		return questions.get(i).correctResponse;
	}

	/**
	 * returns the number of questions on the test.
	 * 
	 * @return the number of questions on the test
	 */
	public int size() {
		return questions.size();
	}
	
	/**
	 * returns a well-formatted String showing the sequence of questions on the exam, 
	 * including the correct answers.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("************************************** CFE EXAM ******************************************************\n\n");
		for(int i = 0; i < questions.size(); i++) {
			sb.append(String.format("%s\n", getFormattedQuestion(i)));
			sb.append(String.format("%s\n\n\n", getFormattedCorrectResponse(i)));
		}
		sb.append("\n************************************** END CFE EXAM ***************************************************\n\n");
		return sb.toString();
		
	}
}
