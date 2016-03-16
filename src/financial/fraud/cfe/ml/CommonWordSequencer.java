package financial.fraud.cfe.ml;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

public class CommonWordSequencer {

	public static void main(String[] args) {
		String passage = "Historical Cost Although some exceptions exist, historical cost is, generally, the proper basis for the recording of assets, expenses, equities, etc. For example, a piece of operational machinery should be shown on the balance sheet at initial acquisition cost and not at current market value or an estimated replacement value.";
		String questionStem = "Fisher, a Certified Fraud Examiner was hired to look at certain assets of ABC Company, which may be fraudulently overvalued by management. Fisher determined that the building cost $11.5 million, has a tax value of $11,250,000, and a recent appraisal for $14 million What generally accepted accounting principle governs how this land should be carried on ABC Company's books?";

		questionStem = "The majority of check fraud in the United States is committed by which of the following?";
		passage = "Check Fraud Rings Since the late 1980s, foreign crime rings have been the cause of the majority of check fraud in the U.S. Most major financial institutions attribute more than 50 percent of all check fraud to organized crime rings. In 2007, an international task force monitored the mail in Africa, Europe, and North America and intercepted billions of dollars worth (face-value) of counterfeit checks. ";

		List<String> questionStemWordSequence = getWordSequenceFromText(questionStem);
		System.out.println(questionStemWordSequence);

		HashMap<String, List<Integer>> passageSeqMap = getWordSequenceMap(passage);
		System.out.println(passageSeqMap);

		List<String> maxWordSequenceFirstWord = getLongestCommonSequenceForCurrWord(0, questionStemWordSequence, passageSeqMap);
		System.out.println(maxWordSequenceFirstWord);
		
		List<String> maxWordSequence = getLongestCommonWordSequence(questionStemWordSequence, passageSeqMap);
		System.out.println(maxWordSequence);
	}

	/**
	 * returns a list containing the words in a text string
	 * 
	 * @param text
	 * @return a list of words
	 */
	private static List<String> getWordSequenceFromText(String text) {
		String[] words = text.toLowerCase().split("[ \n\t\r.,;:!?(){}<>\"]");
		return new LinkedList<String>(Arrays.asList(words));
	}

	/**
	 * returns a hash map in which the keys are words from a text string and the
	 * values are the positions of the words within the text string
	 * 
	 * @param text
	 * @return a hashmap of words and their positions in the text string
	 */
	private static HashMap<String, List<Integer>> getWordSequenceMap(String text) {
		String[] words = text.toLowerCase().split("[ \n\t\r.,;:!?(){}<>\"]");
		HashMap<String, List<Integer>> wordSeqMap = new LinkedHashMap<String, List<Integer>>();
		for (int i = 0; i < words.length; i++) {
			if (words[i].length() == 0)
				continue;

			// if word hasn't been added to map yet, create a list that
			// will hold the word's positions and add the word and the list
			// to the word sequence map, otherwise, add the position to the
			// list in the map for that word.
			if (wordSeqMap.get(words[i]) == null) {
				List currWordList = new LinkedList<Integer>();
				currWordList.add(i);
				wordSeqMap.put(words[i], currWordList);
			} else {
				List currWordList = wordSeqMap.get(words[i]);
				currWordList.add(i);
			}
		}
		return wordSeqMap;
	}

	/**
	 * returns a list containing the longest sequence of words common to both the question stem and to the passage
	 * 
	 * @param questionStemWordSeq a list containing the words of the question stem, in order
	 * @param passageSeqMap a hash map giving the words and their positions in the passage
	 * @return a list containing the longest common sequence of words
	 */
	private static List<String> getLongestCommonWordSequence(List<String> questionStemWordSeq, HashMap<String, List<Integer>> passageSeqMap) {
		List<String> maxCommonWordSequence = new LinkedList<String>();
		
		for(int i = 0; i < questionStemWordSeq.size(); i++) {
			List<String> maxCommonWordSeqForCurrWord = getLongestCommonSequenceForCurrWord(i, questionStemWordSeq, passageSeqMap);
			if(maxCommonWordSeqForCurrWord == null)
				continue;
			if(maxCommonWordSeqForCurrWord.size() > maxCommonWordSequence.size())
				maxCommonWordSequence = maxCommonWordSeqForCurrWord;
		}
		
		return maxCommonWordSequence;
		
	}

	/**
	 * returns the longest common sequence of words starting with a given word in the question stem relative
	 * to a passage.
	 * 
	 * @param currPos
	 * @param questionStemWordSeq
	 * @param passageSeqMap
	 * @return
	 */
	private static List<String> getLongestCommonSequenceForCurrWord(int currPos, List<String> questionStemWordSeq,
			HashMap<String, List<Integer>> passageSeqMap) {
		String currWord = questionStemWordSeq.get(currPos);
		List<Integer> currWordPosList = passageSeqMap.get(currWord);

		// if the current word in the question stem does not exist in the 
		// passage, currWordPosList will be null, and we should simply return
		// null from this function.
		if(currWordPosList == null)
			return null;
		
		List<String> maxCommonWordSequence = new LinkedList<String>();

		for (int currWordPosInPassage : currWordPosList) {
			int posOffset = 0;

			List<String> currCommonWordSequence = new LinkedList<String>();

			// currCommonWordSequence.add(currWord);
			String nextWord = currWord;

			while (passageSeqMap.get(nextWord) != null && passageSeqMap.get(nextWord).contains(currWordPosInPassage + posOffset)) {
				currCommonWordSequence.add(nextWord);
				int nextWordPos = currPos + ++posOffset;
				if(nextWordPos >= questionStemWordSeq.size())
					break;
				nextWord = questionStemWordSeq.get(nextWordPos);
			}

			if (currCommonWordSequence.size() > maxCommonWordSequence.size())
				maxCommonWordSequence = currCommonWordSequence;
		}
		return maxCommonWordSequence;
	}

}
