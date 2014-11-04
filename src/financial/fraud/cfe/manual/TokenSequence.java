package financial.fraud.cfe.manual;

import java.util.List;

/**
 * represents a sequence of tokens, and contains functionality for validating the 
 * sequence of tokens and assigning a score to the sequence of tokens for comparing
 * to other sequence of tokens (for determining the best token sequence for a given
 * text string - performed in other code).
 * 
 * @author jjohnson346
 *
 */
public class TokenSequence {
	private List<Token> tokens;		// list serving as internal store of token sequence
	
	/**
	 * constructor loads the list of tokens input as parm into internal store.
	 * 
	 * @param tokens 	the list of tokens from which to create the TokenSequence object
	 */
	public TokenSequence(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	/**
	 * determines whether the token sequence is valid according to a particular
	 * TokenVerifier.  (Note here that we can pass any TokenVerifier here, and depenidng
	 * on the word list of the tv, get a different result).
	 * 
	 * @param tv			the token verifier against which to validate the sequence
	 * 
	 * @return 			true if the token sequence is valid according to the token verifier
	 */
	public boolean isValid(TokenVerifier tv) {
		for(Token t : tokens) {
			if(!tv.contains(t))
				return false;
		}
		return true;
	}
	
	/**
	 * returns a score for the token sequence, based on a particular token
	 * verifier.  If the sequence is not considered valid, then 0 is returned.
	 * This score is relevant for comparing different token sequences for 
	 * a given string.
	 * 
	 * The score is calculated as the sum of the squares of the lengths of the 
	 * component tokens.  So, a concatenation sequence with a small number of
	 * long tokens will beat a concatenation sequence with a larger number of
	 * shorter tokens.
	 * 
	 * @param tv		the token verifier to determine validity
	 * 
	 * @return 		an integer score value
	 */
	public int score(TokenVerifier tv) {
		if (!isValid(tv)) {
			return 0;
		}
		
		int score = 0;
		for(Token t : tokens) {
			score += Math.pow(t.length(), 2);
		}
		return score;
	}
	
	/**
	 * returns the number of tokens in the sequence
	 * 
	 * @return the integer number of tokens
	 */
	public int size() {
		return tokens.size();
	}
	
	/**
	 * accessor for the list of tokens
	 * 
	 * @return list of tokens making up the sequence
	 */
	public List<Token> getTokens() {
		return tokens;
	}

	/**
	 * returns the token sequence concatenated with space delimiter.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Token t : tokens) {
			sb.append(t + " ");
		}
		return new String(sb);
	}
}
