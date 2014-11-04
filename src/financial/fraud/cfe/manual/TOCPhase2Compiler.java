package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class applies a concatenation algorithm to eliminate the blanks left by the pdf text
 * extractor component.  It takes as input a text document output from phase 1 containing 
 * the contents for a particular testing area (supplied as an argument to the constructor), 
 * and outputs a file (containing phase 2 in the name) whose contents are the output from
 * applying the concatenation algorithm to the contents of the input file.
 * 
 * @author Joe
 *
 */
public class TOCPhase2Compiler extends TOCCompiler {

	private final TextCompiler textCompiler;
		
	/**
	 * calls the constructor of the super class, TOCCompiler, passing to it the 
	 * name of the testing area.  Also, instantiates the compiler object that
	 * performs the concatenation.  In this case, it instantiates the class that
	 * applies the forward concatenation algorithm, TextCompilerFwdConcat.
	 * 
	 * @param testingArea
	 */
	public TOCPhase2Compiler(String testingArea) {
		super(testingArea);
		phase = "2";

		textCompiler = new TextCompilerFwdConcat();
	}

	/**
	 * gets the source contents from the phase 1 file, calls generateTargetTOC() function,
	 * and outputs the phase 2 file.
	 */
	public void compile() {
		String sourceTOC = getSourceTOC();
		String targetTOC = generateTargetTOC(sourceTOC);
		writeToFile(targetTOC);
	}

	/**
	 * retrieves the contents of the phase 1 file for the testing area for which this 
	 * class was instantiated.
	 */
	@Override
	protected final String getSourceTOC() {

		String sourceTOC = null;

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("2011_fem_" + testingAreaFileSubString + "_toc_phase_1.txt"));
			scanner.useDelimiter("\\Z");
			sourceTOC = scanner.next();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
		return sourceTOC;
	}
	
	/**
	 * applies the compiler to the source text.  In this case, that is the 
	 * forward concatenation algorithm encapsulated in the TextCompilerFwdConcat object.
	 */
	@Override
	protected final String generateTargetTOC(String sourceTOC) {
		String targetTOC = new String();
		String sourceLine = null;
		String targetLine = null;
		
		Scanner scanner = new Scanner(sourceTOC);
		scanner.useDelimiter("\n");
		
		while(scanner.hasNext()) {
			sourceLine = scanner.next();
			
			textCompiler.resetInitialState();
			targetLine = textCompiler.compile(sourceLine);
			targetTOC += targetLine;
		}
		return targetTOC;
	}
	

	/**
	 * tests the TOCPhase2Compiler class on a phase 1 file from any of the 4 testing areas.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TOCPhase2Compiler tocP2Compiler = null;
		int input;

		do {
			System.out.println("TOCPhase2Compiler menu:");
			System.out.println("1.  Select CFE Manual TOC section.");
			System.out.println("2.  Compile (P2) TOC section.");
			System.out.println("3.  Quit.");

			Scanner scanner = new Scanner(System.in);
			input = scanner.nextInt();

			switch (input) {
			case 1:
				System.out.println("TOC Sections:");
				System.out.println("0. Main");
				System.out.println("1. Financial Transactions and Fraud Schemes");
				System.out.println("2. Law");
				System.out.println("3. Investigation");
				System.out.println("4. Fraud Prevention and Deterrence");

				int section = scanner.nextInt();
				switch (section) {
				case 0:
					tocP2Compiler = new TOCPhase2Compiler("Main");
					break;
				case 1:
					tocP2Compiler = new TOCPhase2Compiler("Financial Transactions and Fraud Schemes");
					break;
				case 2:
					tocP2Compiler = new TOCPhase2Compiler("Law");
					break;
				case 3:
					tocP2Compiler = new TOCPhase2Compiler("Investigation");
					break;
				case 4:
					tocP2Compiler = new TOCPhase2Compiler("Fraud Prevention and Deterrence");
					break;
				}
				break;
			case 2:
				tocP2Compiler.compile();
				break;
			case 3:
				break;
			default:
				System.out.println("Invalid option.  Please enter a valid selection.");
			}
		} while (input != 3);
	}
}
