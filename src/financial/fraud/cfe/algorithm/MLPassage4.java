package financial.fraud.cfe.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.util.Version;

import cfe.manual.docs.collection.PorterStemmer;
import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.manual.CFEManual;

/**
 * MLPassage4 uses a bag of words/bayesian algorithm to determine the relative likelihood of each option given the
 * passages extracted using the machine learning-based passage extraction algorithm. (Refer to code in the
 * financial.fraud.cfe.ml package for more detail (MLTraining1FileBuilder.java, MLTraining2FileBuilder, and so on...)).
 * 
 * This algorithm extends MLPassage3 by incorporating laplace smoothing into the frequency/probability calcs.
 * 
 */
public class MLPassage4 implements IAlgorithm {

	private final String INPUT_FILE = "ml.test.7.txt";

	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		List<PassageRecord> passages = loadPassageRecords(question);
		try {
			Directory dir = new RAMDirectory();
			IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), true,
					IndexWriter.MaxFieldLength.UNLIMITED);
			Document doc = new Document();
			doc.add(new Field("contents", passages.get(0).passage, Field.Store.YES, Field.Index.ANALYZED));
			writer.addDocument(doc);
			
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		return 0;
	}

	/**
	 * loads the passages from an external file that are the ones most likely to be the relevant passage from which the
	 * question was extracted. These passages were extracted using a machine learning algorithm implemented in R, based
	 * on a number of features including lucene/ir doc rank, number of words in common, and length of the longest common
	 * sequence of words.
	 * 
	 * @param question
	 * @return
	 */
	private List<PassageRecord> loadPassageRecords(CFEExamQuestion question) {
		List<PassageRecord> passages = new LinkedList<PassageRecord>();

		try {
			Scanner fileScanner = new Scanner(new File("machine learning" + File.separator + INPUT_FILE));

			fileScanner.nextLine();

			// Load in those records in the ml.test.7.txt file that relate to
			// the current question (using question name as the selection criterion).

			while (fileScanner.hasNextLine()) {
				// for (int i = 0; i <= 0; i++) {
				String contents = fileScanner.nextLine();

				Scanner line = new Scanner(contents);
				line.useDelimiter("\\|");
				int number = line.nextInt();
				String questionID = line.next();
				int docRank = Integer.parseInt(line.next());
				int numWordsInCommon = Integer.parseInt(line.next());
				int lengthLongestCommonSeq = Integer.parseInt(line.next());

				// isCorrectPassage is, in fact, the boolean value indicating whether
				// the current record's passage is the correct passage for the question.
				// HOWEVER, this indicator is *****NOT TO BE USED****** in this
				// algorithm, in the spirit of upholding scientific integrity.
				int isCorrectPassage = Integer.parseInt(line.next());

				String option1 = line.next();
				String option2 = line.next();
				String option3 = line.next();
				String option4 = line.next();
				String passageID = line.next();
				String passage = line.next();
				double passageProb = Double.parseDouble(line.next());

				if (questionID.equals(question.name))
					passages.add(new PassageRecord(questionID, docRank, numWordsInCommon, lengthLongestCommonSeq,
							isCorrectPassage, passageID, passage, passageProb));
				line.close();

			}

			fileScanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (InputMismatchException e) {
			e.printStackTrace();
		}

		return passages;

	}

	/**
	 * extracts the sequence of words from the FIRST/TOP passage given in the list of passage records. This list is used
	 * downstream to calculate word frequencies.
	 * 
	 * @param passages
	 * @return a list of the words in the passage, in order of occurrence
	 * @throws IOException
	 */
	private List<String> getWords(List<PassageRecord> passages) throws IOException {
		List<String> passagesWords = new LinkedList<String>();

		// just get the first passage, not any of the others...
		PassageRecord pr = passages.get(0);
		pr.passage = pr.passage.toLowerCase();
		pr.passage = stemText(pr.passage);
		String[] words = pr.passage.split("[ \n\t\r.,;:!?(){}<>\"']");
		List<String> wordList = Arrays.asList(words);
		passagesWords.addAll(wordList);
		return passagesWords;
	}

	private List<String> getWords(String text) throws IOException {
		// convert words to lower case.
		text = text.toLowerCase();
		// stem the words in the text.
		text = stemText(text);
		String[] words = text.split("[ \n\t\r.,;:!?(){}<>\"']");
		List<String> wordList = Arrays.asList(words);
		return wordList;
	}

	private Map<String, Integer> getPassagesWordCounts(List<String> passagesWords) {
		Map<String, Integer> passageWordCounts = new HashMap<String, Integer>();
		for (String key : passagesWords) {
			String lowerKey = key.toLowerCase();
			if (!passageWordCounts.containsKey(lowerKey))
				passageWordCounts.put(lowerKey, 1);
			else
				passageWordCounts.put(lowerKey, passageWordCounts.get(lowerKey) + 1);
		}
		return passageWordCounts;
	}

	private String stemText(String text) {
		Scanner input = new Scanner(text);
		PorterStemmer stemmer = new PorterStemmer();
		StringBuilder sb = new StringBuilder();

		while (input.hasNextLine()) {
			// make sure everything is lowercase
			String line = input.nextLine().toLowerCase();
			boolean emptyLine = true;

			// split on whitespace
			for (String s : line.split("\\s+")) {
				// Remove non alphanumeric characters
				s = s.replaceAll("[^a-zA-Z0-9]", "");
				// Stem word.
				s = stemmer.stem(s);
				if (!s.equals("")) {
					if (!emptyLine) {
						sb.append(" ");
					}
					sb.append(s);
					emptyLine = false;
				}
			}
			if (!emptyLine) {
				sb.append("\n");
			}
		}
		return new String(sb);
	}

	private class PassageRecord {
		private String questionID;
		private int docRank;
		private int numWordsInCommon;
		private int lengthLongestCommonSeq;
		private int isCorrectPassage;
		private String passageID;
		private String passage;
		private double passageProb;

		public PassageRecord(String questionID, int docRank, int numWordsInCommon, int lengthLongestCommonSeq,
				int isCorrectPassage, String passageID, String passage, double passageProb) {
			super();
			this.questionID = questionID;
			this.docRank = docRank;
			this.numWordsInCommon = numWordsInCommon;
			this.lengthLongestCommonSeq = lengthLongestCommonSeq;
			this.isCorrectPassage = isCorrectPassage;
			this.passageID = passageID;
			this.passage = passage;
			this.passageProb = passageProb;
		}

		@Override
		public String toString() {
			return "" + questionID + " " + docRank + " " + numWordsInCommon + " " + lengthLongestCommonSeq + " "
					+ isCorrectPassage + " " + passageProb + " " + passage;
		}
	}

}
