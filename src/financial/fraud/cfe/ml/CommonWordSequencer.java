package financial.fraud.cfe.ml;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CommonWordSequencer {

	/**
	 * returns a list containing the words in a text string
	 * 
	 * @param text
	 * @return a list of words
	 */
	public List<String> getWordSequenceFromText(String text) {
		String[] words = text.toLowerCase().split("[ \n\t\r.,;:!?(){}<>\"]");
		return new LinkedList<String>(Arrays.asList(words));
	}

	/**
	 * returns a hash map in which the keys are words from a text string and the values are the positions of the words
	 * within the text string. More specifically, for each word key, the value is a list whose elements are integers
	 * indicating the position(s) within the text string where that word is located.
	 * 
	 * @param text
	 * @return a hashmap of words and their positions in the text string
	 */
	public HashMap<String, List<Integer>> getWordSequenceMap(String text) {
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
	 * @param questionStemWordSeq
	 *            a list containing the words of the question stem, in order
	 * @param passageSeqMap
	 *            a hash map giving the words and their positions in the passage
	 * @return a list containing the longest common sequence of words
	 */
	public List<String> getLongestCommonWordSequence(List<String> questionStemWordSeq,
			HashMap<String, List<Integer>> passageSeqMap) {
		List<String> maxCommonWordSequence = new LinkedList<String>();

		// traverse the questionStemWordSeq, the list of words comprising the question stem.
		// For each word in the question stem, call the getLongestCommonSequenceForCurrWord() method,
		// which finds the longest common word sequence between the question stem and passage starting
		// at that particular word.
		for (int i = 0; i < questionStemWordSeq.size(); i++) {
			List<String> maxCommonWordSeqForCurrWord = getLongestCommonSequenceForCurrWord(i, questionStemWordSeq,
					passageSeqMap);

			// it might be the case that the current word in the question stem is not found
			// in the passage, in which case getLongestCommonSequenceForCurrWord() method returns
			// null. If so, move on to next word.
			if (maxCommonWordSeqForCurrWord == null)
				continue;

			// if the longest common word sequence for the current word is longer than the
			// current longest common word sequence, then replace the current longest common
			// word sequence with the new sequence.
			if (maxCommonWordSeqForCurrWord.size() > maxCommonWordSequence.size())
				maxCommonWordSequence = maxCommonWordSeqForCurrWord;
		}

		// return the longest sequence...
		return maxCommonWordSequence;

	}

	/**
	 * returns a list containing the longest sequence of words common to both the question stem and to the passage Note
	 * this method is overloaded.
	 * 
	 * @param questionStem
	 *            a string containing the question stem
	 * @param passage
	 *            a string containing the passage within which the longest sequence in common with the stem is to be
	 *            found
	 * @return a list containing the sequence of words that is in common to both the stem and to the passage
	 */
	public List<String> getLongestCommonWordSequence(String questionStem, String passage) {
		List<String> questionStemWordSequence = getWordSequenceFromText(questionStem);
		HashMap<String, List<Integer>> passageSeqMap = getWordSequenceMap(passage);
		return getLongestCommonWordSequence(questionStemWordSequence, passageSeqMap);
	}

	/**
	 * returns the longest common sequence of words starting with a given word in the question stem relative to a
	 * passage.
	 * 
	 * @param currPos
	 *            position of the first word in the question stem starting from which the longest common word sequence
	 *            is to be found
	 * @param questionStemWordSeq
	 *            the list of words that make up the question stem
	 * @param passageSeqMap
	 *            hash map containing the words of the passage and their locations in the passage
	 * @return longest matching sequence of words between question stem and passage starting with currPos word
	 */
	public List<String> getLongestCommonSequenceForCurrWord(int currPos, List<String> questionStemWordSeq,
			HashMap<String, List<Integer>> passageSeqMap) {

		// currWord is the word in the question stem at currPos, the position passed in to the method
		// the start for which we'd like to find the longest matching sequence in passage.
		String currWord = questionStemWordSeq.get(currPos);

		// retrieve the list of positions where currWord is located in passage.
		// We need to look at the word sequence in the passage at each of these locations
		// and find the longest matching word sequence among them.
		List<Integer> currWordPosList = passageSeqMap.get(currWord);

		// if the current word in the question stem does not exist in the
		// passage, currWordPosList will be null, and we should simply return
		// null from this function.
		if (currWordPosList == null)
			return null;

		// maxCommonWordSequence stores the word sequence of matching length.
		// initialize this variable to be an empty list before beginning the search.
		List<String> maxCommonWordSequence = new LinkedList<String>();

		// for each of the positions in passage where the word matches currWord,
		// determine the length of matching word sequence, and find the one of max length.
		for (int currWordPosInPassage : currWordPosList) {
			int posOffset = 0;

			// initialize current matching word sequence list to be an empty list.
			List<String> currCommonWordSequence = new LinkedList<String>();

			// currCommonWordSequence.add(currWord);
			String nextWord = currWord;

			// increment posOffset for each word in passage that matches question stem word
			// starting at currWordPosInPassage, and add that word to currCommonWordSequence
			while (passageSeqMap.get(nextWord) != null
					&& passageSeqMap.get(nextWord).contains(currWordPosInPassage + posOffset)) {
				currCommonWordSequence.add(nextWord);
				int nextWordPos = currPos + ++posOffset;

				// if we've reached the end of question stem, stop.
				if (nextWordPos >= questionStemWordSeq.size())
					break;

				// get the next word from the question stem so that it can be
				// checked against the next word in the passage in the next iteration of the loop.
				nextWord = questionStemWordSeq.get(nextWordPos);
			}

			// compare current common word sequence to max common word sequence.
			// If it's longer, replace max common word sequence.
			if (currCommonWordSequence.size() > maxCommonWordSequence.size())
				maxCommonWordSequence = currCommonWordSequence;
		}

		// return the longest common word sequence after having traversed through
		// all currWordPosInPassage candidates...
		return maxCommonWordSequence;
	}

	/**
	 * returns the words in common between a question stem and a passage (both of which are input arguments).
	 * 
	 * @param questionStem
	 * @param passage
	 * @return the number of words in common between questionStem and passage
	 * @throws IOException
	 */
	public HashSet<String> getCommonWords(String questionStem, String passage) throws IOException {

		// extract words from the passage and load into a hash set.
		HashSet<String> commonWords = getWordSetFromText(passage.toLowerCase());
		// System.out.println("passage words: " + commonWords);

		// extract words from question stem and load into a hash set.
		HashSet<String> stemWords = getWordSetFromText(questionStem.toLowerCase());
		// System.out.println("stem words: " + stemWords);

		// remove those words in passage that are not in the question stem.
		commonWords.retainAll(stemWords);
		// System.out.println("common words: " + commonWords);

		// remove function words.
		Scanner functionWordsFileScanner = new Scanner(new File("function words//function.words.txt"));
		functionWordsFileScanner.useDelimiter("\\Z");
		String fw = functionWordsFileScanner.next();
		String[] fwords = fw.split("[ \n]");
		HashSet<String> functionWords = new HashSet(Arrays.asList(fwords));
		// System.out.println("function words: " + functionWords);
		commonWords.removeAll(functionWords);

		// remove the zero-length string from the common words collection.
		commonWords.remove("");

		return commonWords;
	}

	public HashSet<String> getWordSetFromText(String passage) {
		String[] words = passage.split("[ \n\t\r.,;:!?(){}<>\"]");

		// adding words to TreeSet removes duplicates and sorts them alphabetically.
		return new HashSet<String>(Arrays.asList(words));
	}

	public static void main(String[] args) {

		// Basic Accounting Concepts 11:
		// ----------------------------
		// String passage =
		// "Historical Cost Although some exceptions exist, historical cost is, generally, the proper basis for the recording of assets, expenses, equities, etc. For example, a piece of operational machinery should be shown on the balance sheet at initial acquisition cost and not at current market value or an estimated replacement value.";
		// String questionStem =
		// "Fisher, a Certified Fraud Examiner was hired to look at certain assets of ABC Company, which may be fraudulently overvalued by management. Fisher determined that the building cost $11.5 million, has a tax value of $11,250,000, and a recent appraisal for $14 million What generally accepted accounting principle governs how this land should be carried on ABC Company's books?";
		// common words: [assets, value, cost, generally]
		// number of common words: 4
		// longest common word sequence: [should, be]
		// length of longest common word sequence: 2

		// Check and Credit Card Fraud 15:
		// -------------------------------
		// String questionStem =
		// "The majority of check fraud in the United States is committed by which of the following?";
		// String passage =
		// "Check Fraud Rings Since the late 1980s, foreign crime rings have been the cause of the majority of check fraud in the U.S. Most major financial institutions attribute more than 50 percent of all check fraud to organized crime rings. In 2007, an international task force monitored the mail in Africa, Europe, and North America and intercepted billions of dollars worth (face-value) of counterfeit checks. ";
		// common words: [majority, check, fraud]
		// number of words in common: 3
		// longest common word sequence: [the, majority, of, check, fraud, in, the]
		// length of longest common word sequence: 7

		// Check and Credit Card Fraud 11:
		// -------------------------------
		// String questionStem = "Persons who are expert at passing phony checks are sometimes called: ";
		// String passage =
		// "Paperhangers Paperhangers are the experts of phony check passing. They frequently pick a particular establishment or store and observe its security methods. Any store that scrutinizes check writers’ identification is not a good target for a paperhanger. However, they will observe and select the least experienced or most lackadaisical of store employees to whom to pass the check. The paperhanger will then ask the clerk for cash back from the transaction and make the check out for an amount greater than the price of the purchase. In some cases, the checks being written are counterfeit; however, in other cases the checks are purposefully being written on a closed account. A variation of this scam is making a fraudulent deposit at a bank and asking for cash back. |";
		// common words: [checks, phony, passing]
		// number of words in common: 3
		// longest common word sequence: [checks, are]
		// length of longest common word sequence: 2

		// Check and Credit Card Fraud 14:
		// -------------------------------
		// String questionStem =
		// "The practice of recording the deposits of transfers between multiple bank accounts before recording the disbursements is called which of the following?";
		// String passage =
		// "Check Kiting Check kiting is one of the original white-collar crimes. It continues to survive even with a financial institution’s ability to detect kiting. In a kiting scheme, multiple bank accounts are opened and money is “deposited” from account to account, although the money never exists. |";
		// common words: [bank, multiple, accounts]
		// number of words in common: 3
		// longest common word sequence: [multiple, bank, accounts]
		// length of longest common word sequence: 3

		// Computer and Internet Fraud 19:
		// -------------------------------
		// String questionStem =
		// "A computer program that contains instruction codes to attack software is commonly referred to as a: |";
		// String passage =
		// "A computer virus is a program that contains instruction codes to attack software. The attack might erase data or display a message on the screen. The computer virus can copy itself to other programs. This copy ability can affect large networks. In recent years, viruses have disrupted large networks and caused the expenditure of millions of dollars in staff and machine hours to remove these viruses and restore normal operations. |";
		// common words: [codes, software, program, contains, computer, attack, instruction, |]
		// number of words in common: 8
		// longest common word sequence: [program, that, contains, instruction, codes, to, attack, software]
		// length of longest common word sequence: 8

		// Financial Institution Fraud 13:
		// -------------------------------
		// String questionStem =
		// "The process of purchasing a home and reselling it at a higher price shortly thereafter is known as:";
		// String passage =
		// "Property flipping is the process by which an investor purchases a home and then resells it at a higher price shortly thereafter. For example, an investor buys a house in need of work for $250,000 in July, renovates the kitchen and the bathrooms and landscapes the yard at a cost of $50,000, and then resells the house two months later (the time it takes to make the renovations) for a price that is reflective of the market for a house in its updated condition. This is a legitimate business transaction, and there are numerous individuals and groups in the real estate market who make an honest living flipping properties. |";
		// common words: [price, shortly, thereafter, higher, process, home]
		// number of words in common: 6
		// longest common word sequence: [it, at, a, higher, price, shortly, thereafter]
		// length of longest common word sequence: 7

		// Sources of Information 47:
		// -------------------------------
		// String questionStem =
		// "A suspect's Social Security number can most easily be obtained through which of the following online searches?";
		// String passage =
		// "CREDIT HEADER SEARCHES Credit header searches are among the most powerful locator tools. These searches return information from credit reports on individuals. They are a valuable source because almost all people have been involved in some credit activity either under their true names or an assumed (or known as) name. It is appropriate to keep in mind that for common names, it may be necessary to use the Social Security number or date of birth to differentiate the subject from other people with the same name. The credit bureau headers offer two search mechanisms. First, the examiner can discover a current address, an address history, and Social Security number(s) associated with the target by using a past address up to seven years old. Second, once the Social Security number is in hand, the headers can be searched for matches, because the Social Security number is a national identity number. Although Social Security numbers are protected from disclosure by the Privacy Act, it has become practically impossible for individuals to avoid disclosing them on public records. |";
		// common words: [searches, number, most, security, social]
		// number of words in common: 5
		// longest common word sequence: [social, security, number]
		// length of longest common word sequence: 3

		// arbitrary example:
		// -------------------------------
		String questionStem = "This is a test.";
		String passage = "There are no words in common wiht the stem.";
		// common words: []
		// number of words in common: 0
		// question stem word sequence: [this, is, a, test]
		// passage map: {there=[0], are=[1], no=[2], words=[3], in=[4], common=[5], wiht=[6], the=[7], stem=[8]}
		// longest common word sequence: []
		// length of longest common word sequence: 0

		CommonWordSequencer commonWordSequencer = new CommonWordSequencer();

		try {
			HashSet<String> commonWords = commonWordSequencer.getCommonWords(questionStem, passage);
			System.out.println("common words: " + commonWords);
			System.out.println("number of words in common: " + commonWords.size());
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> questionStemWordSequence = commonWordSequencer.getWordSequenceFromText(questionStem);
		System.out.println("question stem word sequence: " + questionStemWordSequence);

		HashMap<String, List<Integer>> passageSeqMap = commonWordSequencer.getWordSequenceMap(passage);
		System.out.println("passage map: " + passageSeqMap);

		// List<String> maxWordSequenceFirstWord = getLongestCommonSequenceForCurrWord(0, questionStemWordSequence,
		// passageSeqMap);
		// System.out.println(maxWordSequenceFirstWord);

		List<String> maxWordSequence = commonWordSequencer.getLongestCommonWordSequence(questionStemWordSequence,
				passageSeqMap);
		System.out.println("longest common word sequence: " + maxWordSequence);
		System.out.println("length of longest common word sequence: " + maxWordSequence.size());

		// this is a test of the overloaded method, getLongestCommonWordSequence(String questionStem, String passage)
		maxWordSequence = commonWordSequencer.getLongestCommonWordSequence(questionStem, passage);
		System.out.println(maxWordSequence);
		System.out.println(maxWordSequence.size());
	}

}
