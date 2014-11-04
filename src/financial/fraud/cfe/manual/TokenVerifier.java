package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashSet;
import java.util.InputMismatchException;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * encapsulates logic for verifying tokenized (gently stemmed) words against a word
 * list to make sure the words are valid (part of English language).  
 * 
 * @author jjohnson346
 *
 */
public class TokenVerifier {

	private String wordListFileName;			// stores the name of the file from which to get valid words

	private Set<String> wordList;			// set of distinct words against which to validate tokens

	/**
	 * constructor build the list of valid words against which to validate tokens
	 * by loading the contents of a word list file, removing those words that are considered
	 * valid in the file but are, from a practical perspective, really better suited as being
	 * treated as invalid (stored in an _add file), and adding those that are not considered 
	 * valid by the file, but are better suited as being treated as valid, (stored in a _remove file).
	 * 
	 * @param wordListFileName the name of the file from which to get valid words
	 */
	public TokenVerifier(String wordListFileName) {
		this.wordListFileName = wordListFileName;
		wordList = new HashSet<String>();
		Scanner scanner = null;
		int wordCount = 0;

		try {
			
			// build set of valid word from the contents of the word
			// list file.
			scanner = new Scanner(new File(wordListFileName));
			String word = null;
			while (scanner.hasNext()) {
				word = scanner.nextLine();
				wordCount++;
				wordList.add(word);
			}

			// remove words that mess up the correction process.
			String removeWordListFileName = wordListFileName.substring(0, wordListFileName.length() - 4) + "_remove.txt";
			scanner = new Scanner(new File(removeWordListFileName));
			while (scanner.hasNext()) {
				word = scanner.nextLine();
				wordCount--;
				wordList.remove(word);
			}

			// add words that are needed for the correction process.
			String addWordListFileName = wordListFileName.substring(0, wordListFileName.length() - 4) + "_add.txt";
			scanner = new Scanner(new File(addWordListFileName));
			while (scanner.hasNext()) {
				word = scanner.nextLine();
				wordCount++;
				wordList.add(word);
			}
			
			System.out.printf("%s%s\n", "Token Verifier: Using word list file: ", wordListFileName);
			System.out.printf("%s%d\n", "Token Verifier: Word count: ", wordCount);
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
	}
	
	
	public void add(Token t) {
		wordList.add(t.root);
		addTokenToWordListFile(t);
	}

	public void remove(Token t) {
		wordList.remove(t.root);
		removeTokenFromWordListFile(t);
	}

	/**
	 * inserts a token in the add word list file, the file that contains words that are not included
	 * in the official list file (from web) but should be considered valid.
	 * 
	 * @param t 		the token to add to the _add file
	 */
	private void addTokenToWordListFile(Token t) {
		String addWordListFileName = wordListFileName.substring(0, wordListFileName.length() - 4) + "_add.txt";

		try {
			FileWriter fstream = new FileWriter(addWordListFileName, true);
			fstream.write(t.root + "\n");
			fstream.close();
		} catch (IOException e) {
			System.out.println("File open failed.");
		}
	}

	/**
	 * inserts a token in the _remove file.
	 * 
	 * @param t 		the token to insert in the _remove file
	 */
	private void removeTokenFromWordListFile(Token t) {
		String addWordListFileName = wordListFileName.substring(0, wordListFileName.length() - 4) + "_remove.txt";

		try {
			FileWriter fstream = new FileWriter(addWordListFileName, true);
			fstream.write(t.root + "\n");
			fstream.close();
		} catch (IOException e) {
			System.out.println("File open failed.");
		}
	}

	/**
	 * returns whether a given token is in the valid word list.
	 * 
	 * @param t		the token to check for
	 * @return		true if the token is in the valid word list
	 */
	public boolean contains(Token t) {
		return wordList.contains(t.root);
	}

	
	/**
	 * runs a unit test of TokenVerifier - prompts the user for a word to search
	 * for and returns whether it is considered valid, or accepts entire sentence
	 * from user and validates each word of it.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		// String wordListFileName = "wlist_match2.txt";
		// String wordListFileName = "wlist_match3.txt";
		// String wordListFileName = "wlist_match4.txt";
		// String wordListFileName = "wlist_match5.txt";
		// String wordListFileName = "wlist_match6.txt";
		// String wordListFileName = "wlist_match7.txt";
		// String wordListFileName = "wlist_match8.txt";
		// String wordListFileName = "wlist_match9.txt";
		// String wordListFileName = "wlist_match10.txt";
		// String wordListFileName = "corncob_lowercase.txt";

		
		String wordListFileName = "manual compilation//wlist_cfe_8.txt";

		TokenVerifier wv = new TokenVerifier(wordListFileName);
		int response = 0;

		do {

			try {
				System.out.println("Input your selection: ");
				System.out.println("1. Search for a word.");
				System.out.println("2. Verify words in a sentence.");
				System.out.println("3. Exit.");

				Scanner scanner = new Scanner(System.in);
				response = Integer.parseInt(scanner.nextLine());

				switch (response) {
				case 1:
					System.out.print("Input the word to verify:  ");
					String word = scanner.nextLine();
					System.out.println(wv.contains(new Token(word)));
					break;
				case 2:
					System.out.print("Input sentence:  ");
					String sentence = scanner.nextLine();
					List<String> words = parseSentence(sentence);
					for (String w : words) {
						System.out.printf("%15s%15s\n", w, wv.contains(new Token(w)));
					}
					break;
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

	private static List<String> parseSentence(String sentenceText) {
		LinkedList<String> sentence = new LinkedList<String>();
		sentenceText = sentenceText.toLowerCase();
		String[] words = sentenceText.split("\\b");
		for (String word : words) {
			if (!word.equals(" ") // remove excess blanks added during parsing.
					&& !word.equals("") && !word.equals(". ") // remove period from end of sentence
					&& !word.equals(".")) {
				sentence.add(word);
			}
		}
		return sentence;
	}
}
