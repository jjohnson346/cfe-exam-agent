package financial.fraud.cfe.algorithm;

import java.util.ArrayList;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

/**
 * AlgorithmConceptMatch is an algorithm that uses the words in the stem in order to 
 * determine a detail section upon which to base a max word count algorithm for selecting
 * the correct option.  First, it gets the words of the stem, then it determines the likely
 * detail section given these words from which the question was created, then it counts
 * the instances of each option phrase within this detail section and returns the option
 * with the max count.  There is no incorporation of logic for all of the above and none
 * of the above.
 * 
 * @author Joe
 *
 */
public class ConceptMatch implements IAlgorithm {

	protected CFEManualLargeDocUnit cfeManual;			// the cfe manual object

	protected CFEExamQuestion question;		// the question to which to apply the algo.
	
	protected ArrayList<Integer> wordMatchCounts; 	// the count of matches among words.
	
	/**
	 * returns the cfe manual object
	 * 2.0.0 - remove the construcor.
	 * 
	 * @param cfeManual
	 */
	// public ConceptMatch(CFEManualLargeDocUnit cfeManual) {
	// this.cfeManual = cfeManual;
	// }
	
	/**
	 * current version of this is trivial - simply returning 0, always.
	 * 
	 * TODO: need to incorporate logic for this algo.
	 */
	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		return 0;
	}
	
//	@Override
//	public int solve(CFEExamQuestion question) {
//		int wordMatchCount = 0;
//		List<String> stemWords = parseStem(question);
//		String detailSectionText = null;
//
//		this.question = question;
//
//		// calculate frequency for each option of the question.
//		wordMatchCounts = new ArrayList<Integer>();
//
//		for (String phrase : question.options) {
//			detailSectionText = cfeManual.getDetailSectionText(phrase);
//			wordMatchCount = 0;
//			if (detailSectionText != null) {
//				for (String s : stemWords) {
//					if (detailSectionText.indexOf(s) != -1)
//						wordMatchCount++;
//				}
//			}
//			wordMatchCounts.add(wordMatchCount);
//		}
//
//		// return option with max word match count. Ties go to earliest option.
//		int maxIdx = 0;
//		double max = 0;
//		for (int i = 0; i < wordMatchCounts.size(); i++) {
//			if (wordMatchCounts.get(i) > max) {
//				max = wordMatchCounts.get(i);
//				maxIdx = i;
//			}
//		}
//
//		return maxIdx;
//	}

//	private List<String> parseStem(CFEExamQuestion question) {
//		LinkedList<String> stemWords = new LinkedList<String>();
//		String stemPhrase = question.stem;
//		String[] words = stemPhrase.split("\\b");
//		for (String word : words) {
//			if (!word.equals(" ") && !word.equals("") && !word.equals(".")) { // remove excess blanks added during parsing.
//				stemWords.add(word);
//			}
//		}
//		return stemWords;
//	}

//	public void printOptionWordMatchCounts(OutputWriter writer) {
//		
//		int maxPhraseLength = 0;
//		
//		for (String phrase : question.options) {
//			if (phrase.length() > maxPhraseLength)
//				maxPhraseLength = phrase.length();
//		}
//		maxPhraseLength++;
//
//		writer.write("%" + maxPhraseLength + "s%20s\n", "Phrase", "Matches");
//		for (int i = 0; i < question.options.size(); i++) {
//			writer.write("%" + maxPhraseLength + "s%10d\n", question.options.get(i), wordMatchCounts.get(i));
//		}
//		writer.write("\n");
//	}

}
