package financial.fraud.cfe.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class TestSetExamBuilder {
	
	private static final String TEST_SET_FILE_NAME = "Test Set Exam.txt";

	public static void main(String[] args) throws FileNotFoundException {
		String[] examSections = { "Financial Transactions and Fraud Schemes",
				"Fraud Prevention and Deterrence", "Investigation", "Law" };
		
		PrintWriter p = new PrintWriter("exams" + File.separator + TEST_SET_FILE_NAME);

		for (String examSection : examSections) {
			File examSectionDir = new File("exam questions - test set"
					+ File.separator + examSection);
			File[] questionSections = examSectionDir.listFiles();
			for (File questionSection : questionSections) {
				if (questionSection.isDirectory()) {
					File[] questionFiles = questionSection.listFiles();
					for (File questionFile : questionFiles) {
						if (questionFile.isFile()) {
							String fileName = "exam questions - test set"
									+ File.separator + examSection
									+ File.separator
									+ questionSection.getName()
									+ File.separator + questionFile.getName();
							
							System.out.println(fileName);
							
							p.println(fileName);
							p.flush();
						}

					}
				}
			}
		}
	}

}
