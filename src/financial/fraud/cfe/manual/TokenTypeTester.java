package financial.fraud.cfe.manual;

import java.util.LinkedList;
import java.util.List;
import java.util.StringTokenizer;

/**
 * encapsulates functionality for testing the type for a token, i.e., whether
 * the token is word or non-word token, using the [a-zA-Z] regular expression.
 * 
 * This class is not used in the compiler classes, but only serves as a POC
 * that the regular expression used here can correctly distinguish word from 
 * non-word tokens.
 * 
 * @author jjohnson346
 *
 */
public class TokenTypeTester {
	
	/**
	 * prints out whether each token generated from a sample string is either
	 * a word or non-word token.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		StringTokenizer st = new StringTokenizer("This is a test4 ......");
		List<Token> tokens = new LinkedList<Token>();
		
		while (st.hasMoreTokens()) {
			Token t = new Token(st.nextToken());
			tokens.add(t);
		}

		for(Token t : tokens) {
			System.out.println(t + " " + t.root.matches("[a-zA-Z]+"));
		}
	}
}
