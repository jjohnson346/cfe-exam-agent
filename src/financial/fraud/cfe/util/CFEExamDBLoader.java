package financial.fraud.cfe.util;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * serves as a utility for loading exam questions from files into the cfe exam db,
 * providing a simple interface for adding a question to the db.
 * 
 * @author jjohnson346
 *
 */
public class CFEExamDBLoader {
	/**
	 * adds an exam question stored in a file into the db.
	 * 
	 * @param questionFileName the name of the file
	 */
	public void addQuestion(String questionFileName) {
		
		CFEExamQuestion q = new CFEExamQuestion(questionFileName);

		// insert question record into cfe_exam_db.cfe_question table.
		String name = questionFileName.substring(0, questionFileName.length() - 4);
		CFEExamDBUtility.insertCFEExamQuestion(name, q.section, q.stem, q.correctResponse, q.explanation);
		
		// insert record for each option into cfe_exam_db.cfe_question_option table.
		for(int i = 0; i < q.options.size(); i++) {
			CFEExamDBUtility.insertCFEExamQuestionOption(name, i, q.options.get(i));
		}
	}
	
	/**
	 * runs a simple test by calling the add method, passing to it an example file.
	 * 
	 * NOTE: make sure the directory to the file is correct (question files are not 
	 * stored in the current directory to this class).
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CFEExamDBLoader dbl = new CFEExamDBLoader();
		dbl.addQuestion("Health Care Fraud 16.txt");
	}
}
