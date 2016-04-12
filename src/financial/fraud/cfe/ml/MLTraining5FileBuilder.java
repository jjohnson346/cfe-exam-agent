package financial.fraud.cfe.ml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.NoSuchElementException;
import java.util.Scanner;

/**
 * MLTraining5FileBuilder creates a machine learning input file, ml.training.5.txt, with the following field layout by
 * simply extracting these fields from ml.training.4.txt.
 * 
 *
 * @author joejohnson
 *
 */
// * 1. question id
// * 2. document rank
// * 3. number of words in common between question stem and passage
// * 4. length of maximum common word sequence
// * 5. is correct passage (y/n)

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
			String header = "rid|qid|dr|nwic|llcs|icp";
			output.write(header + "\n");
			output.flush();

			int number = 0;
			// Process each line of the ml.training.3.txt file.
			// Parse each record (using the bar | as the delimiter)
			// and load the fields into variables.
			while (fileScanner.hasNextLine()) {
				// for (int i = 0; i <= 0; i++) {
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

				// System.out.println(contents);

				// System.out.println("question ID: " + questionID);
				// System.out.println("question stem: " + questionStem);
				// System.out.println("correct option: " + correctOption);
				// System.out.println("option2: " + option2);
				// System.out.println("option3: " + option3);
				// System.out.println("option4: " + option4);
				// System.out.println("docName: " + docName);
				// System.out.println("docID: " + docID);
				// System.out.println("docRank: " + docRank);
				// System.out.println("passageID: " + passageID);
				// System.out.println("passage" + passage);
				// System.out.println("numWordsInCommon: " + numWordsInCommon);
				// System.out.println("lengthLongestCommonSequence: " + lengthLongestCommonSequence);
				// System.out.println("isCorrectPassage: " + isCorrectPassage);

				// write out the data for the current passage and current
				// question to the output file.
				StringBuilder sb = new StringBuilder();
				// String delimiter = " | ";
				String delimiter = OUTPUT_FILE_DELIMITER;
				sb.append(number + delimiter);
				sb.append(questionID + delimiter);
				sb.append(docRank + delimiter);
				sb.append(numWordsInCommon + delimiter);
				sb.append(lengthLongestCommonSequence + delimiter);
				sb.append(isCorrectPassage);
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
