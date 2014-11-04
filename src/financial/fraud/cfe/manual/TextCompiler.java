package financial.fraud.cfe.manual;

import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.StringTokenizer;

/**
 * an abstract class for the compiler classes that implement various algorithms
 * for removing erroneous blanks from the cfe manual text. The scrub() method is
 * the method where algorithm is implemented.
 * 
 * @author jjohnson346
 *
 */
public abstract class TextCompiler {

	protected TokenVerifier tokenVerifier;			// used to validate token sequences when 
													// determining optimal translations

	protected boolean isSuccess;						// holds true if the compile() method is
													// successful in translating input string

	protected Token unrecognizedToken;				// stores the token that was not recognized
													// during the scrubbing process in which 
													// tokens are verified by tokenVerifier

	protected Token priorToken;						// stores reference to token prior to error
													// encountered during tokenization

	private String wordListFileName;					// file containing list of valid words, upon which
													// generation of token sequences is based.

	/**
	 * constructor initializes the internal token verifier machinery, based on the word list file.
	 */
	public TextCompiler() {
		wordListFileName = "manual//wlist_cfe_8.txt";
		tokenVerifier = new TokenVerifier(wordListFileName);
	}

	/**
	 * performs a compile, or translation of a string in source language,
	 * text with extra, erroneous spaces inserted among the 
	 * characters by the pdf->text conversion process, to a string in 
	 * target language, i.e. text with all such erroneous spaces removed.
	 * @param source		the text string with erroneous spaces inserted
	 * @return			a text string with erroneous spaces removed
	 */
	public String compile(String source) {
		String targetText = null;

		try {
			resetInitialState();

			List<Token> tokens = tokenize(source);

			if (isSuccess) { // tokenize succeeded - concatenate tokens.
				targetText = generateTargetText(tokens);
			} else { // tokenize failed - ask user for help.
				boolean wordListChanged = processTokenizeFailure(source);
				if (wordListChanged)
					targetText = compile(source);
				else
					targetText = generateTargetText(tokens);
			}

		} catch (StringIndexOutOfBoundsException e) {
			System.out.println("Error while compiling source: " + source);
		}

		return targetText;
	}

	/**
	 * generates a string in the target language, i.e., a correct natural language 
	 * text string, from an input set of tokens that have been generated/scrubbed
	 * by other methods called earlier.  This method simply inserts spaces between
	 * the tokens to generate the output string.
	 * 
	 * @param tokens 		the list of tokens from which to generate string of correct natural language
	 * 
	 * @return				a string in correct natural language
	 */
	private String generateTargetText(List<Token> tokens) {
		StringBuilder sb = new StringBuilder();
		for (Token t : tokens) {
			sb.append(t);
			sb.append(" ");
		}
		sb.append("\n");
		return new String(sb);
	}

	/**
	 * processes a tokenization failure that occurs while attempting to compile
	 * a particular input string.  prompts user for additions/deletions to 
	 * word list in order to resolve the failure.  
	 * 
	 * @param source			the string that generated the error
	 * @return				returns true if either the _add or _remove word lists were modified during processing
	 */
	private boolean processTokenizeFailure(String source) {
		boolean wordListChanged = false;

		System.out.println("Unable to compile: " + source);
		System.out.println("Unrecognized token: " + unrecognizedToken);
		System.out.println();

		System.out.println("Would you like to add the root of this token to the word list?");
		Scanner scanner = new Scanner(System.in);
		if (scanner.nextLine().toLowerCase().equals("y")) {
			tokenVerifier.add(unrecognizedToken);
			wordListChanged = true;
		} else {
			System.out.println("Prior token: " + priorToken);
			System.out.println("Would you like to remove the root of the prior token from the word list?");
			scanner = new Scanner(System.in);
			if (scanner.nextLine().toLowerCase().equals("y")) {
				tokenVerifier.remove(priorToken);
				wordListChanged = true;
			}
		}
		writeToErrorFile(source);
		return wordListChanged;
	}

	/**
	 * writes a string causing a compiler error to an error file.
	 * 
	 * @param source 	the string causing the compiler error
	 */
	private void writeToErrorFile(String source) {
		String errorFileName = "manual compilation//compile_failures.txt";

		try {
			FileWriter fstream = new FileWriter(errorFileName, true);
			fstream.write(source + "\n");
			fstream.close();
		} catch (IOException e) {
			System.out.println("File open failed.");
		}
	}

	/**
	 * tokenizes a text string, and then scrubs the resulting sequence of tokens.
	 * The prior "word" tokens are scrubbed every time a "non-word" token is encountered -
	 * e.g., a period at the end of a sentence.  
	 * 
	 * @param text		the text string to be tokenized, scrubbed
	 * @return			a list of scrubbed tokens
	 */
	private List<Token> tokenize(String text) {
		List<Token> tokens = tokenizeInitial(text);

		// process the initial tokens. Scrub each block of word tokens.
		// Leave all non-word tokens alone.

		List<Token> scrubbedTokens = new LinkedList<Token>();
		List<Token> unscrubbedWordTokens = new LinkedList<Token>();
		List<Token> scrubbedWordTokens = null;

		for (Token t : tokens) {
			if (!t.root.matches("[a-zA-Z]+")) { // token is a NON-WORD token.

				// first, scrub the block of word tokens that have been
				// read prior to this non-word token, and add those tokens
				// to the scrubbed tokens list.
				if (!unscrubbedWordTokens.isEmpty()) {
					scrubbedWordTokens = scrub(unscrubbedWordTokens);
					scrubbedTokens.addAll(scrubbedWordTokens);
				}
				unscrubbedWordTokens.clear();

				scrubbedTokens.add(new Token(t));

			} else { // token must be a WORD token.

				unscrubbedWordTokens.add(t);
			}
		}

		// there may be remaining unscrubbed word tokens yet to be added.
		// if so, add them to the scrubbed tokens list.
		scrubbedTokens.addAll(scrub(unscrubbedWordTokens));
		// perform scrub using forward linear search
		// return scrubForwardLinearSearch(tokens);

		return scrubbedTokens;
	}

	/**
	 * resets internal state so can be re-used for next compilation process.
	 */
	public void resetInitialState() {
		isSuccess = true;
		unrecognizedToken = null;
	}

	/**
	 * abstract method for "scrubbing" a sequence of tokens.
	 * 
	 * @param tokens 	a list of tokens to be "scrubbed"
	 * @return			a list of tokens, based on the input list, but cleaned up
	 */
	protected abstract List<Token> scrub(List<Token> tokens);

	/**
	 * overloaded method for combining tokens which takes a sequence of tokens
	 * and returns a single token, created by combining a pair within the input token sequence.
	 * 
	 * @param toks		the sequence of tokens a pair of which are to be combined into one token
	 * @param i			the index of the first of the two consecutive tokens to be combined in the sequence
	 * @return			a single token created from the concatenation of the pair of tokens in the input
	 */
	protected Token combineTokens(List<Token> toks, int i) {
		Token t1 = toks.get(i);
		Token t2 = toks.get(i + 1);
		return new Token(t1.toString() + t2.toString());
	}

	/**
	 * combines two input tokens into one token.
	 * 
	 * @param t1			the first input token
	 * @param t2			the second input token
	 * 
	 * @return			the combined token created from token 1 and 2 passed in as input
	 */
	protected Token combineTokens(Token t1, Token t2) {
		return new Token(t1.toString() + t2.toString());
	}

	/**
	 * generates a sequence of tokens based on the functionality of the java.util.StringTokenizer
	 * class.  The string tokens returned by the StringTokenizer are recast into Token objects
	 * and loaded into a list of tokens, in sequence.
	 * 
	 * @param text		an input string to tokenize, based on StringTokenizer
	 * 
	 * @return			a list of tokens, as defined by the Token class of this package
	 */
	public List<Token> tokenizeInitial(String text) {
		StringTokenizer st = new StringTokenizer(text);
		List<Token> tokens = new LinkedList<Token>();

		while (st.hasMoreTokens()) {
			Token t = new Token(st.nextToken());
			tokens.add(t);
		}

		return tokens;
	}

}
