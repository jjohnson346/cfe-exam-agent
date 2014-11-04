package financial.fraud.cfe.util;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/**
 * The QuestionFileBuilder class provides functionality for converting the output files of the 
 * OCR software into properly formatted files suitable for input into the cfe exam agent software,
 * (CFEExamAgent, Profiler, etc.).
 * 
 * @author jjohnson346
 *
 */
public class QuestionFileBuilder {

	private String sectionName;
	private int count;
	private List<QuestionText> questions;

	/**
	 * parameterized constructor that extracts the data for a number of questions
	 * from a file prepared by ocr software, formats it (with the help of QuestionText
	 * class), and then writes the information back into individual files, one for 
	 * each question, to be consumed later by the CFEExamAgent software (profiler, agent,
	 * etc.).
	 * 
	 * @param fileName the name of the file containing the raw ocr output for a number of questions
	 * @param sectionName the section to which all of the questions in the ocr output file belong
	 * @param count the sequence number to start from when assigning names to the question files
	 */
	public QuestionFileBuilder(String fileName, String sectionName, int count) {

		this.count = count;
		this.sectionName = sectionName;

		String questionsText;

		questions = new LinkedList<QuestionText>();

		try {
			BufferedReader reader = new BufferedReader(new FileReader(fileName));
			char[] contentsArray = new char[1000000];
			int length = reader.read(contentsArray);
			questionsText = new String(contentsArray, 0, length);

			// System.out.println(questionsText);

			loadBegPositions(questionsText);
			loadEndPositions(questionsText);
			loadQuestionText(questionsText);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * outputs each question in the questions list, the list of QuestionText objects,
	 * to a file whose name is denoted by the question's section and sequence number,
	 * (initialized by startCount, an input parm to this function). 
	 * 
	 * 
	 * @param startCount the sequence number to start from when creating the question files.
	 */
	public void outputQuestions(int startCount) {

		for (QuestionText q : questions) {
			System.out.println(q);
			// String fileName = "C:\\Userdata\\Graduate School\\Ph.D\\Research\\Financial Fraud Investigation\\ACFE\\Exam Questions\\TXT Format\\Staging\\" +
			// sectionName + " " + startCount++ + ".txt";
			String fileName = sectionName + " " + startCount++ + ".txt";
			try {
				PrintStream p = new PrintStream(fileName);
				p.printf("%s", q);
				p.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * called by constructor, finds all questions in the file whose name was supplied as an input 
	 * argument to the constructor, and for each question, creates a 
	 * QuestionText object and adds the object to questions, the list 
	 * of QuestionText objects maintained privately.
	 * 
	 * @param questionsText the text of the questions file (supplied to constructor)
	 * 
	 */
	private void loadBegPositions(String questionsText) {

		int begPosition = questionsText.indexOf(sectionName);

		while (begPosition != -1) {

			QuestionText q = new QuestionText(sectionName, count);
			q.begPosition = begPosition;

			questions.add(q);

			begPosition = questionsText.indexOf(sectionName, begPosition + 1);
		}
	}

	/**
	 * called by constructor after calling loadBegPositions(),
	 * determines and assigns the ending position
	 * of each QuestionText object in the questions list, (maintained privately
	 * in this class).
	 * 
	 * @param questionsText the text of the questions file (supplied to constructor)
	 */
	private void loadEndPositions(String questionsText) {

		QuestionText curQuestion;
		QuestionText nextQuestion;
		QuestionText lastQuestion;

		for (int i = 0; i < questions.size() - 1; i++) {
			curQuestion = questions.get(i);
			nextQuestion = questions.get(i + 1);
			curQuestion.endPosition = nextQuestion.begPosition;
		}

		lastQuestion = questions.get(questions.size() - 1);
		lastQuestion.endPosition = questionsText.length() - 1;
	}

	/**
	 * called by constructor after calling loadEndPositions(),
	 * determines and assigns the text of each question through a call
	 * to the setText() method of each question object, which uses
	 * begPosition and endPosition to determine the region of questionsText
	 * from which to extract and assign the text of the question, for each
	 * question represented in questions, the list of QuestionText objects.
	 * 
	 * @param questionsText the text of the questions file (supplied to constructor)
	 */
	private void loadQuestionText(String questionsText) {

		for (QuestionText q : questions) {
			q.setText(questionsText);
		}
	}

	/**
	 * a unit test for QuestionFileBuilder, which instantiates the class, (which through
	 * the constructor loads the contents of the raw ocr file into a list of QuestionText objects)
	 * and then calls the outputQuestions() method, which writes these questions back out to 
	 * individual files, one for each question, properly formatted for processing by the 
	 * CFEExamAgent software.
	 * 
	 * @param args args[0] gives the name containing the raw ocr output for a number of questions
	 * @param args args[1] gives the name of the section for the questions in the raw ocr file
	 */
	public static void main(String[] args) {

		String inputFileName = args[0];
		String sectionName = args[1];
		int fileCount = Integer.parseInt(args[2]);
		int startCount = Integer.parseInt(args[3]);

		QuestionFileBuilder qBuilder = new QuestionFileBuilder(inputFileName, sectionName, fileCount);
		qBuilder.outputQuestions(startCount);
	}

}
