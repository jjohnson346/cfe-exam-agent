package financial.fraud.cfe.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * TokenizerSimple performs basic tokenization of an input string.  Its tokenize()
 * method accepts a String input and its getTokenTypeFreqs() method returns a hash
 * table of the tokens of the input string as keys and the counts of those tokens as 
 * values.  The tokenization process involves some very crude, simplistic stemming.  
 * So, improvements could likely be gained by adopting the PorterStemmer class in the 
 * tokenize() method.
 * 
 * TODO:  Implement stemming functionality based on Porter stemmer class, and thus, replace existing stemming
 * 
 * @author jjohnson346
 *
 */
public class TokenizerSimple {

	private List<String> tokens;						// stores the tokens in the order they are encountered 
													// in the input string during execution of the tokenize()
													// method.


	private Map<String, Integer> typeFreqs;			// stores the token type frequencies indexed by token type.


	/**
	 * tokenizes the input string and saves the frequency counts of each token 
	 * in a hash map where the keys are the tokens (stemmed from raw words) and 
	 * the values are the frequency counts.
	 * 
	 * Note - clears/resets the data structures for frequency counts and such.
	 * @param input
	 */
	public void tokenize(String input) {
		
		// reset the data structures for loading new data.
		// this needs to be done EVERY time tokenize() method is called!!!
		tokens = new ArrayList<String>();
		typeFreqs = new HashMap<String, Integer>();
		
		buildTokenList(input);
		buildTokenTypeFreqMap();
	}

	/**
	 * returns a list of tokens created from the input string passed in as an input parm.
	 * Currently uses a crude, simplistic stemming algorithm.  Use of the PorterStemmer class
	 * would likely provide a significant improvement....
	 * 
	 * TODO:  replace call to simpleStem with call to stem() method of PorterStemmer class.
	 * 
	 * @param input the input string for which to build the token list
	 * @return a list of tokens
	 */
	public List<String> buildTokenList(String input) {
		StringTokenizer st = new StringTokenizer(input);
		
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			token = simpleStem(token);
			tokens.add(token);
		}
		return tokens;
	}

	/**
	 * performs some basic stemming procedures, including removing the period at the end of the word 
	 * (if one exists if, for example, the word is the last word in a sentence), starting
	 * parentheses, double quotes, single quotes, ending parentheses, single quote, commas, question mark
	 * exclamation point, possessive syntax.
	 * 
	 * @param s the input string to be stemmed.  Most likely, this is a single word, with some extra characters that need removing.
	 * @return a string with the extra characters removed
	 */
	private String simpleStem(String s) {
		String stem = s.toLowerCase().trim();

		boolean punctRemoved = false;

		while (!punctRemoved && stem.length() > 1) {
			// remove period at the end if one exists.
			if (stem.charAt(stem.length() - 1) == '.' && !s.matches("[.]{2,}")) {
				stem = stem.substring(0, stem.length() - 1);
				continue;
			}
			// remove starting parentheses, double quote, single quotes.
			if (stem.charAt(0) == '(' 
				|| stem.charAt(0) == '[' 
				|| stem.charAt(0) == '"' 
				|| stem.charAt(0) == '“' 
				|| stem.charAt(0) == '\'' 
				|| stem.charAt(0) == '‘') {
				stem = stem.substring(1, stem.length());
				continue;
			}

			// remove ending parentheses, double quote, single quote, comma, question mark, exclamation point.
			if (stem.charAt(stem.length() - 1) == ')' 
				|| stem.charAt(stem.length() - 1) == ']' 
				|| stem.charAt(stem.length() - 1) == '"' 
				|| stem.charAt(stem.length() - 1) == '”' 
				|| stem.charAt(stem.length() - 1) == '’' 
				|| stem.charAt(stem.length() - 1) == '\''
				|| stem.charAt(stem.length() - 1) == ',' 
				|| stem.charAt(stem.length() - 1) == ';' 
				|| stem.charAt(stem.length() - 1) == '?'
				|| stem.charAt(stem.length() - 1) == ':'
				|| stem.charAt(stem.length() - 1) == '!') {
					stem = stem.substring(0, stem.length() - 1);
					continue;
			}

			punctRemoved = true;
		}

		// remove possessive suffix if one exists
		if (stem.length() > 2 && (stem.substring(stem.length() - 2).equals("'s") || stem.substring(stem.length() - 2).equals("’s")))
			stem = stem.substring(0, stem.length() - 2);

		return stem;
	}

	/**
	 * builds the hash table of frequency counts by traversing the token list and adding each one, in turn.
	 */
	private void buildTokenTypeFreqMap() {
		for (String s : tokens) {
			if (!typeFreqs.containsKey(s))
				typeFreqs.put(s, 1);
			else
				typeFreqs.put(s, typeFreqs.get(s) + 1);
		}
	}

	/** 
	 * accessor for list of tokens.
	 * @return list of tokens
	 */
	public List<String> getTokens() {
		return tokens;
	}

	/**
	 * accessor for retrieving the entire hash map of frequency counts.
	 * @return hash map of frequency counts
	 */
	public Map<String, Integer> getTokenTypeFreqs() {
		return typeFreqs;
	}

	/**
	 * returns the total count of tokens after summing up the number of tokens
	 * for each type.
	 * 
	 * QUESTION:  how is this different from getTokensCount(), which simply returns size of token list?
	 * @return
	 */
	public long getTokenTypeFreqsTotal() {
		long total = 0;
		for (String s : typeFreqs.keySet()) {
			total += typeFreqs.get(s);
		}
		return total;
	}

	/**
	 * returns the number of types of tokens in the hash map of frequency counts
	 * by returning the size of the key set of the map.
	 * 
	 * @return the count of token types
	 */
	public long getTokenTypeCount() {
		return typeFreqs.keySet().size();
	}

	/**
	 * return the total number of tokens in the token list.
	 * 
	 * @return the count of tokens in the token list.
	 */
	public long getTokensCount() {
		return tokens.size();
	}

	/**
	 * returns pretty format display of contents of hash map of frequency counts.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%-30s%10s\n", "Token", "Frequency"));
		sb.append(String.format("%-30s%10s\n", "-----------------------------", "---------"));
		for (String token : typeFreqs.keySet()) {
			sb.append(String.format("%-30s%10d\n", token, typeFreqs.get(token)));
		}
		sb.append(String.format("\nTotals: %22d%10d\n", getTokenTypeCount(), getTokensCount()));
		return new String(sb);
	}
}
