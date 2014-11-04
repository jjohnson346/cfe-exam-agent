package financial.fraud.cfe.manual;

import java.util.LinkedList;
import java.util.List;

/**
 * extends the TextCompiler class by implementing the scrub() method with logic
 * for determining the optimal sequence based on max score among the various
 * possible concatenation sequences of tokens.
 * 
 * @author jjohnson346
 *
 */
public class TextCompilerMaxScore extends TextCompiler {

	/**
	 * returns a "scrubbed" list of tokens for an input token sequence, where by 
	 * scrubbed we mean a concatenation sequence of the token sequence such that 
	 * the token sequence score is maximized.  See {@link TokenSequence#score(TokenVerifier)}
	 * for methodology on determining score.
	 */
	@Override
	protected List<Token> scrub(List<Token> tokens) {

		// get all possible concatenation sequences for the input token sequence
		// by calling the getTokenSequences() method.
		List<TokenSequence> sequences = getTokenSequences(tokens, tokens.size() - 1);

		
		// find the concatenation sequence with the max score.
		int maxScore = 0;
		int currScore = 0;
		TokenSequence maxSequence = null;

		for (TokenSequence ts : sequences) {
			currScore = ts.score(tokenVerifier);
			// System.out.println(ts + ": " + currScore);
			if (currScore > maxScore) {
				maxScore = currScore;
				maxSequence = ts;
			}
		}
		
		// return the sequence with max score.
		return maxSequence.getTokens();
	}

	/**
	 * returns a list containing all possible concatenations of token sequences for 
	 * a given token sequence.  That is, for an input sequence of n tokens, (and thus, n-1
	 * token breaks in between them), return every possible concatenation sequence.  (The
	 * total number is 2^(n-1) sequences.)
	 * 
	 * Note this method is a duplicate of the createTokenSequences() method of the 
	 * TokenSequencer class.  Not sure why it was necessary to create a copy of the code
	 * for that method here, instead of simply calling that method on an instance of the
	 * TokeSequencer class.
	 * 
	 * 
	 * @param tokens			the sequence of tokens from which to generate all sequences
	 * @param i				the parameter determining which two tokens to combine.
	 * 
	 * @return				a list of token sequences consisting of all possible concatenations of sequences
	 */
	private List<TokenSequence> getTokenSequences(List<Token> tokens, int i) {
		List<TokenSequence> currSequences = new LinkedList<TokenSequence>();

		if (i == 1) {

			List<Token> list1 = new LinkedList<Token>();
			list1.add(tokens.get(0));
			list1.add(tokens.get(1));
			currSequences.add(new TokenSequence(list1));

			List<Token> list2 = new LinkedList<Token>();
			list2.add(combineTokens(tokens, 0));
			currSequences.add(new TokenSequence(list2));

			return currSequences;

		} else {

			List<TokenSequence> prevSequences = getTokenSequences(tokens, i - 1);

			for (TokenSequence sequence : prevSequences) {

				List<Token> sequenceTokens = sequence.getTokens();
				List<Token> list1 = new LinkedList<Token>();
				for (Token t : sequenceTokens) {
					list1.add(t);
				}
				list1.add(tokens.get(i));

				currSequences.add(new TokenSequence(list1));

				List<Token> list2 = new LinkedList<Token>();
				for (Token t : sequenceTokens) {
					list2.add(t);
				}
				list2.add(tokens.get(i));
				list2.add(combineTokens(list2, list2.size() - 2));
				list2.remove(list2.size() - 2);
				list2.remove(list2.size() - 2);

				currSequences.add(new TokenSequence(list2));
			}
			return currSequences;
		}
	}
}
