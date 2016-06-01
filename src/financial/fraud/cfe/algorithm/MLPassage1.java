package financial.fraud.cfe.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;
import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.util.TokenizerSimple;

/**
 * MLPassage1 uses a bag of words/bayesian algorithm to determine the relative likelihood of each option
 * given the passages extracted using the machine learning-based passage extraction algorithm.  (Refer to
 * code in the financial.fraud.cfe.ml package for more detail (MLTraining1FileBuilder.java, MLTraining2FileBuilder,
 * and so on...)).  
 * 
 * This algorithm does NOT use porter stemmer or laplace smoothing.  Thus, if an option
 * includes a word that does not occur in the passages, that word gets a frequency count of 0, and that translates
 * to the entire option getting a frequency count of 0.  This is an issue to be addressed in future algorithms.
 * 
 * Note also that this algorithm uses all of the passages in an equally weighted manner, even though only one of them
 * is considered the correct passage.  This is also an issue to be addressed in future algorithms.
 * 
 * @author joejohnson
 *
 */
public class MLPassage1 implements IAlgorithm {

	private final String INPUT_FILE = "ml.test.7.txt";

	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		System.out.println("executing mlpassage1 algorithm for " + question.name + "...");
		try {
			List<PassageRecord> passages = loadPassageRecords(question);
			List<String> passagesWords = getWords(passages);
			Map<String, Integer> passagesWordCounts = getPassagesWordCounts(passagesWords);

			ArrayList<Double> optionFrequencies = new ArrayList<Double>();
			TokenizerSimple tokenizerSimple = new TokenizerSimple();

			for (String optionPhrase : question.options) {
				
//				tokenizerSimple.tokenize(optionPhrase);
//				List<String> optionTokens = tokenizerSimple.getTokens();
				
				List<String> optionTokens = getWords(optionPhrase);
				System.out.println("\nAnalysis of option: " + optionTokens);
				
				// get product of counts of all words in the option phrase.
				double frequency = 1.0;
				for(String optionToken : optionTokens) {
					int optionTokenTypeFreq;
					if(passagesWordCounts.containsKey(optionToken)) {
						optionTokenTypeFreq = passagesWordCounts.get(optionToken);
//						frequency *= optionTokenTypeFreq;
				
					
					} else {
						optionTokenTypeFreq = 0;
//						break;
					}
					Logger.getInstance().println(optionToken + ": " + optionTokenTypeFreq, DetailLevel.FULL);
					frequency *= optionTokenTypeFreq;
				}
				
				Logger.getInstance().println("Total Frequency for Option: " + frequency, DetailLevel.FULL);
				
				// get geometric mean of frequency to normalize for different phrase lengths.
				frequency = Math.pow(frequency, 1.0 / optionTokens.size());
				
				Logger.getInstance().println("Geom Mean Frequency: " + frequency, DetailLevel.FULL);
				
				optionFrequencies.add(frequency);
			}

			// return option with max frequency. Ties go to earliest option.
			int responseIdx = 0;
			double max = 0;
			for (int i = 0; i < optionFrequencies.size(); i++) {
				if (optionFrequencies.get(i) > max) {
					max = optionFrequencies.get(i);
					responseIdx = i;
				}
			}
			
			int rCounter = 0;
			for (PassageRecord r : passages) {
				System.out.println(++rCounter + ": " + r);
			}
//
//			// print out the contents of the tree map.
//			for (Map.Entry<String, Integer> entry : passagesWordCounts.entrySet())
//				System.out.println(entry.getKey() + ": " + entry.getValue());
			
			return responseIdx;

		} catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * loads the passages from an external file that are the ones most likely to be the relevant passage
	 * from which the question was extracted.  These passages were extracted using a machine learning
	 * algorithm implemented in R, based on a number of features including lucene/ir doc rank, number of
	 * words in common, and length of the longest common sequence of words. 
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
	 * extracts the sequence of words from the passages given in the list of passage records.  
	 * This list is used downstream to calculate word frequencies.  Note - also converts
	 * the words to lower case.
	 * 
	 * @param passages
	 * @return a list of the words 
	 * @throws IOException
	 */
	private List<String> getWords(List<PassageRecord> passages) throws IOException {
		List<String> passagesWords = new LinkedList<String>();

		for (PassageRecord pr : passages) {
			pr.passage = pr.passage.toLowerCase();
			String[] words = pr.passage.split("[ \n\t\r.,;:!?(){}<>\"']");
			List<String> wordList = Arrays.asList(words);
			passagesWords.addAll(wordList);
		}
		return passagesWords;
	}
	
	/**
	 * extracts the sequence of words from a text string as a list.  
	 * This list is used downstream to calculate word frequencies.  Note - also converts
	 * the words to lower case.
	 * 
	 * @param text
	 * @return a list of the words 
	 * @throws IOException
	 */
	private List<String> getWords(String text) throws IOException {
		text = text.toLowerCase();
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
