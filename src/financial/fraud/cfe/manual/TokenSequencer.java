package financial.fraud.cfe.manual;

import java.util.LinkedList;
import java.util.List;

/**
 * encapsulates logic for generating all possible concatenation sequences of a sequence of 
 * tokens.  For example, for the token sequence: ho o s e, there are the following
 * possible token sequences:
 * 
 * 1.  ho o s e
 * 2.  ho o se
 * 3.  ho os e
 * 4.  ho ose
 * 5.  hoo s e
 * 6.  hoo se
 * 7.  hoos e
 * 8.  hoose
 * 
 * The createTokenSequences() method returns all of the concatenation sequences.
 * 
 * This class is used by TextCompilerMaxScore, where it is used to generate all
 * concatenation sequences for a given token sequence, and then TextCompilerMaxScore
 * determines the optimal sequence (most likely) based on a score.
 * 
 * 
 * 
 * @author jjohnson346
 *
 */
public class TokenSequencer {

	/**
	 * runs a unit test of the TokenSequencer on the sequence, ho o s e r.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TextCompiler tc = new TextCompilerMaxScore();
		List<Token> tokens = tc.tokenizeInitial("ho o s e r");
		
		List<TokenSequence> sequences = createTokenSequences(tokens, 4);
		
		for(TokenSequence ts : sequences) {
			System.out.println(ts);
		}
	
	
	}
	
	/**
	 * returns a list containing all possible concatenations of token sequences for 
	 * a given token sequence.  That is, for an input sequence of n tokens, (and thus, n-1
	 * token breaks in between them), return every possible concatenation sequence.  (The
	 * total number is 2^(n-1) sequences.)
	 * 
	 * @param tokens			the sequence of tokens from which to generate all sequences
	 * @param i				the parameter determining which two tokens to combine.
	 * 
	 * @return				a list of token sequences consisting of all possible concatenations of sequences
	 */
	public static List<TokenSequence> createTokenSequences(List<Token> tokens, int i) {
		List<TokenSequence> currSequences = new LinkedList<TokenSequence>();

		if(i == 1) {
			
			List<Token> list1 = new LinkedList<Token>();
			list1.add(tokens.get(0));
			list1.add(tokens.get(1));
			currSequences.add(new TokenSequence(list1));
			
			List<Token> list2 = new LinkedList<Token>();
			list2.add(combineTokens(tokens, 0));
			currSequences.add(new TokenSequence(list2));
			
			return currSequences;
		
		} else {
			
			List<TokenSequence> prevSequences = createTokenSequences(tokens, i - 1);
			
			for(TokenSequence sequence : prevSequences) {
				
				List<Token> sequenceTokens = sequence.getTokens();
				List<Token> list1 = new LinkedList<Token>();
				for(Token t : sequenceTokens) {
					list1.add(t);
				}
				list1.add(tokens.get(i));
				
				currSequences.add(new TokenSequence(list1));
				
				List<Token> list2 = new LinkedList<Token>();
				for(Token t : sequenceTokens) {
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
	
	
	/**
	 * prints out a sequence of tokens
	 * 
	 * @param tokens		the sequence of tokens to print
	 */
	public static void printTokens(List<Token> tokens) {
		for(Token t : tokens) {
			System.out.print(t + " ");
		}
		System.out.println();
	}

	/**
	 * concatenates two tokens within a sequence of tokens, namely
	 * tokens i and i+1.
	 * 
	 * @param toks 		the sequence of tokens inside of which a pair of tokens are to be concatenated	
	 * @param i			the index of the first token to be concatenated with its successor
	 * 
	 * @return			the sequence of tokens with the concatenation completed
	 */
	public static Token combineTokens(List<Token> toks, int i) {
		Token t1 = toks.get(i);
		Token t2 = toks.get(i + 1);
		return new Token(t1.toString() + t2.toString());
	}
}

