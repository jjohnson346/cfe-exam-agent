package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * a compiler component class that implements an algorithm for removing blanks
 * by performing a concatenation algorithm on each line of text - i.e., a forward 
 * concatenation of tokens when an unrecognized token
 * is found in the line, continuing to concatenate until the combination token
 * is recognized or until the end of the token sequence is reached, (at which point
 * the algorithm gives up and simply returns the original token sequence).
 * 
 * @author jjohnson346
 *
 */
public class TextCompilerFwdConcat extends TextCompiler {

	
	/**
	 * perform forward concatenation on a token sequence, i.e.,
	 * concatenate successive tokens when an unrecognized token in the
	 * sequence is encountered, until the token combo is recognized, or 
	 * until the end of the sequence is reached.
	 */
	@Override
	protected List<Token> scrub(List<Token> tokens) {
		List<Token> newTokens = new LinkedList<Token>();

		int i = 0;
		while (i < tokens.size()) {
			if (tokens.get(i).isValid(tokenVerifier)) {
				newTokens.add(new Token(tokens.get(i++)));
			} else {
				
				// record unrecognized and prior tokens, in case the attempt
				// to combine tokens is not successful.
				unrecognizedToken = tokens.get(i);
				if(i > 0)
					priorToken = tokens.get(i - 1);
				else 
					priorToken = null;
				
				// try to combine tokens on a go forward basis until either 
				// a word in the list is or until the end of the token list is reached.
				Token replaceToken = tokens.get(i++);
				while (!replaceToken.isValid(tokenVerifier) && i < tokens.size()) {
					replaceToken = combineTokens(replaceToken, tokens.get(i++));
				}
				if (replaceToken.isValid(tokenVerifier)) {
					newTokens.add(replaceToken);
				} else {
					isSuccess = false;
					return tokens;
				}
			}
		}
		return newTokens;
	}

	
	public static void main(String[] args) {
		TextCompiler tc = new TextCompilerFwdConcat();
		int response = 0;

		do {

			try {
				System.out.println("Input your selection: ");
				System.out.println("1. Compile a text sequence.");
				System.out.println("2. Compile a toc file.");
				System.out.println("3. Exit.");

				Scanner scanner = new Scanner(System.in);
				response = Integer.parseInt(scanner.nextLine());

				switch (response) {
				case 1:
					System.out.print("Input the text sequence: ");
					String text = scanner.nextLine();
					String compiledText = tc.compile(text);
					System.out.println(compiledText);
					break;
				case 2:
					try {
						System.out.print("Input the name of the TOC file: ");
						String inputTOCFileName = scanner.nextLine();
						Scanner inputTOCFile = new Scanner(new File(inputTOCFileName));
						String tocLine = null;
						String compiledTOCLine = null;

						while (inputTOCFile.hasNext()) {
							tocLine = inputTOCFile.nextLine();
							compiledTOCLine = tc.compile(tocLine);
							System.out.print(compiledTOCLine);
						}
						inputTOCFile.close();
						break;
					} catch (FileNotFoundException e) {
						System.out.println("File not found.");
					}
				case 3:
					// end program.
					break;
				}
			} catch (NumberFormatException e) {
				System.out.println("Invalid input.  Please try again.");
			} catch (InputMismatchException e) {
				System.out.println("Invalid input.  Please try again.");
			}
		} while (response != 3);
	}

	// public static void main(String[] args) {
	// TextCompiler tm = new TextCompiler();
	//
	// //List<Token> tokens =
	// tm.tokenize("Technical Surveillance Countermeasures (TSCM) Surv ey .....................................................  1.884");
	// //List<Token> tokens = tm.tokenize("T HEFT OF INTELLECTUAL PROPERTY");
	// //List<Token> tokens = tm.tokenize("this is a      t e s t.");
	// //List<Token> tokens = tm.tokenize("MANAGEMENT’S, AUDITORS’, AND FRAU D EXAMINERS’ RESPONSIBILITIES");
	// for(Token t : tokens) {
	// System.out.println(t);
	// }
	// }

}
