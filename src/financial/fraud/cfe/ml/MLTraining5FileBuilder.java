package financial.fraud.cfe.ml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * MLTraining5FileBuilder creates a machine learning input file, ml.training.5.txt, with the following field layout by
 * simply extracting these fields from ml.training.4.txt. These input files, ml.training.5.txt and ml.test.5.txt, serve
 * as input to the R script, passages.log.reg.R.
 * 
 *
 * @author joejohnson
 *
 */
// The fields above the line are those that are relevant for the
// logistic regression algorithm to be applied in R. The fields
// below the line are those that must be carried forward for the
// answer algorithm that is to be applied downstream.

// 1. question id
// 2. document rank
// 3. number of words in common between question stem and passage
// 4. length of maximum common word sequence
// 5. is correct passage (y/n)
// -----------------------------------------------------------
// 6. option 1 - (correct option)
// 7. option 2
// 8. option 3
// 9. option 4
// 10. passage id
// 11. passage
//
// **********************************************************************************************
// Note: Single Quote/Hash Tag Issue in R: There was an adjustment made to the optionXX, passage id and 
// passage fields to remove any single quotes, ', and hash tags, #, from the fields. 
// These characters messed up R such that R could not read the file properly. So, the decision was made
// to simply take them out. This may have an impact on any string matching used in the answer algorithm.
// **********************************************************************************************

public class MLTraining5FileBuilder {

	private final String CFE_MANUAL_CLASS_NAME = "CFEManualSmallDocUnitRegex";
	private final String INPUT_FILE_DELIMITER = "\\s\\|\\s*";
	private final String OUTPUT_FILE_DELIMITER = "|";

	// private final String INPUT_FILE = "ml.training.4.txt";
	// private final String OUTPUT_FILE = "ml.training.5.txt";
	private final String INPUT_FILE = "ml.test.4.txt";
	private final String OUTPUT_FILE = "ml.test.5.txt";

	protected String examSectionName;

	protected String indexQuestionSectionName;

	// protected String indexDirectory;

	protected String[] elimPhrases = { "is referred to as", "are referred to as", "which of the following", "?",
			"are known as", "is known as", "are sometimes called", "is called", "would be described as" };

	public void createFile() {
		// Logger.getInstance().setDetailLevel(DetailLevel.FULL);
		String contents = null;
		Scanner fileScanner = null;
		try {
			fileScanner = new Scanner(new File("machine learning" + File.separator + INPUT_FILE));
			PrintWriter output = new PrintWriter(new File("machine learning" + File.separator + OUTPUT_FILE));

			// create header row for processing in R.
			String header = "rid|qid|dr|nwic|llcs|icp|opt1|opt2|opt3|opt4|pid|pass";
			output.write(header + "\n");
			output.flush();

			int number = 0;
			// Process each line of the ml.training.3.txt file.
			// Parse each record (using the bar | as the delimiter)
			// and load the fields into variables.
			while (fileScanner.hasNextLine()) {
				// for (int i = 0; i <= 10; i++) {
				contents = fileScanner.nextLine();

				Scanner line = new Scanner(contents);
				// line.useDelimiter("\\s\\|\\s*");
				line.useDelimiter(INPUT_FILE_DELIMITER);
				number = line.nextInt();
				String questionID = line.next();
				String questionStem = line.next();
				String correctOption = line.next();
				String option2 = line.next();
				String option3 = line.next();
				String option4 = line.next();
				String docName = line.next();
				String docID = line.next();
				String docRank = line.next();
				String passageID = line.next();
				String passage = line.next();
				String numWordsInCommon = line.next();
				String lengthLongestCommonSequence = line.next();
				String isCorrectPassage = line.next();

				// remove any single quote from optionXX, passageID, and passage fields.
				// As noted in documentation at head of this file, single quotes
				// in these fields made it impossible for R to read this file.
				// So, to address this issue, just take them out...
				correctOption = correctOption.replaceAll("'|#", "");
				option2 = option2.replaceAll("'|#", "");
				option3 = option3.replaceAll("'|#", "");
				option4 = option4.replaceAll("'|#", "");
				passageID = passageID.replaceAll("'|#", "");
				passage = passage.replaceAll("'|#", "");

				// write out the data for the current passage and current
				// question to the output file.
				StringBuilder sb = new StringBuilder();

				sb.append(number + OUTPUT_FILE_DELIMITER);
				sb.append(questionID + OUTPUT_FILE_DELIMITER);
				sb.append(docRank + OUTPUT_FILE_DELIMITER);
				sb.append(numWordsInCommon + OUTPUT_FILE_DELIMITER);
				sb.append(lengthLongestCommonSequence + OUTPUT_FILE_DELIMITER);
				sb.append(isCorrectPassage + OUTPUT_FILE_DELIMITER);
				sb.append(correctOption + OUTPUT_FILE_DELIMITER);
				sb.append(option2 + OUTPUT_FILE_DELIMITER);
				sb.append(option3 + OUTPUT_FILE_DELIMITER);
				sb.append(option4 + OUTPUT_FILE_DELIMITER);
				sb.append(passageID + OUTPUT_FILE_DELIMITER);
				sb.append(passage);

				sb.append("\n");
				output.write(new String(sb));
				output.flush();

				if (number % 100 == 0)
					System.out.println(number + " records processed...");
			}

			fileScanner.close();
			output.close();

			System.out.println("File creation complete. " + number + " records processed.");
		} catch (IOException e) {
			System.out.println("contents: " + contents);
			e.printStackTrace();
		} catch (NoSuchElementException e) {
			System.out.println("contents: " + contents);
			System.out.println(fileScanner.nextLine());
			e.printStackTrace();

		}

	}

	public static void main(String[] args) {
		MLTraining5FileBuilder training5Builder = new MLTraining5FileBuilder();
		training5Builder.createFile();
	}
}
