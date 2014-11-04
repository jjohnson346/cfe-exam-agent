package financial.fraud.cfe.manual;

import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * An instance of Token represents a token created from a string, by removing 
 * any period at the end, possessives, single quotes, double quotes, question marks,
 * etc. 
 * 
 * 
 * @author jjohnson346
 *
 */
public class Token {

	/**
	 * the string from which the token is created
	 */
	public final String text;
	
	/**
	 * the stemmed form of text after all extraneous characters removed
	 */
	public final String root;

	/**
	 * constructor accepts an input string and creates a token by removing
	 * period, single quote, double quote, exclamation point, question mark,
	 * etc.
	 * 
	 * @param s the string from which to create the token
	 */
	public Token(String s) {
		String tempRoot = s.toLowerCase();

		boolean punctRemoved = false;

		
		// repeatedly runs this loop for removing extraneous characters
		// until there are no more such characters in the text.
		
		while (!punctRemoved && tempRoot.length() > 1) {
			// remove period at the end if one exists.
			if (tempRoot.charAt(tempRoot.length() - 1) == '.' && !s.matches("[.]{2,}")) {
				tempRoot = tempRoot.substring(0, tempRoot.length() - 1);
				continue;
			}
			// remove starting parentheses, double quote, single quotes.
			if (tempRoot.charAt(0) == '(' || tempRoot.charAt(0) == '"' || tempRoot.charAt(0) == '\'') {
				tempRoot = tempRoot.substring(1, tempRoot.length());
				continue;
			}

			// remove ending parentheses, double quote, single quote, comma, question mark, exclamation point.
			if (tempRoot.charAt(tempRoot.length() - 1) == ')' 
				|| tempRoot.charAt(tempRoot.length() - 1) == '"'
				|| tempRoot.charAt(tempRoot.length() - 1) == '\'' 
				|| tempRoot.charAt(tempRoot.length() - 1) == ','
				|| tempRoot.charAt(tempRoot.length() - 1) == '?'
				|| tempRoot.charAt(tempRoot.length() - 1) == '!') {
				tempRoot = tempRoot.substring(0, tempRoot.length() - 1);
				continue;
			}

			punctRemoved = true;
		}

		// remove possessive suffix if one exists
		if (tempRoot.length() > 2 && tempRoot.substring(tempRoot.length() - 2).equals("'s"))
			tempRoot = tempRoot.substring(0, tempRoot.length() - 2);

		text = s;
		root = tempRoot;
	}

	/**
	 * a copy constructor
	 * 
	 * @param t the token object from which to make a copy
	 */
	public Token(Token t) {
		this.text = t.text;
		this.root = t.root;
	}

	/**
	 * returns the number of characters in the token
	 * 
	 * @return an integer length
	 */
	public int length() {
		return root.length();
	}

	/**
	 * returns the text (not the root) of the token
	 */
	@Override
	public String toString() {
		return text;
	}

	/**
	 * validates the token against a TokenVerifier object - making sure 
	 * it is contained in a lexicon of tokens.
	 * 
	 * @param tv the TokenVerifier object
	 * 
	 * @return true if the token is successfully validated
	 */
	boolean isValid(TokenVerifier tv) {
		return tv.contains(this);
	}

	/**
	 * runs a unit test of the Token class, showing the tokens created
	 * for given string inputs.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		int response = 0;

		do {

			try {
				System.out.println("Input your selection: ");
				System.out.println("1. Create a token.");
				System.out.println("2. Exit.");

				Scanner scanner = new Scanner(System.in);
				response = Integer.parseInt(scanner.nextLine());

				switch (response) {
				case 1:
					System.out.print("Input the text for the token:  ");
					Token t = new Token(scanner.nextLine());
					System.out.println("t.text: " + t.text);
					System.out.println("t.root: " + t.root);
					break;
				case 2:
					// end program.
					break;
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input.  Please try again.");
			} catch (InputMismatchException e) {
				System.out.println("Invalid input.  Please try again.");
			}

		} while (response != 2);
	}

}
