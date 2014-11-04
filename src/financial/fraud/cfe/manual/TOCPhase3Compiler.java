package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class aligns the toc text in the file output from phase 2.
 * 
 * @author Joe
 *
 */
public class TOCPhase3Compiler extends TOCCompiler {

	/**
	 * accepts testing area value, assigns phase = 3.
	 * 
	 * @param testingArea
	 */
	public TOCPhase3Compiler(String testingArea) {
		super(testingArea);
		phase = "3";
	}

	/**
	 * retrieves the source toc from the output file from phase 2.
	 */
	@Override
	protected final String getSourceTOC() {

		String sourceTOC = null;

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("2011_fem_" + testingAreaFileSubString + "_toc_phase_2.txt"));
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
	 * calls the alignTOC() function to align each row of the source toc.
	 */
	@Override
	protected final String generateTargetTOC(String sourceTOC) {
		StringBuilder targetTOC = new StringBuilder();
		String sourceLine = null;
		String targetLine = null;
		
		Scanner scanner = new Scanner(sourceTOC);
		scanner.useDelimiter("\n");
		
		while(scanner.hasNext()) {
			sourceLine = scanner.next();
			targetLine = alignTOC(sourceLine);
			targetTOC.append(targetLine);
		}
		return new String(targetTOC);
	}


	/**
	 * tests the phase 3 compiler
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TOCPhase3Compiler tocP3Compiler = null;
		int input;

		do {
			System.out.println("TOCPhase3Compiler menu:");
			System.out.println("1.  Select CFE Manual TOC section.");
			System.out.println("2.  Compile (P3) TOC section.");
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
					tocP3Compiler = new TOCPhase3Compiler("Main");
					break;
				case 1:
					tocP3Compiler = new TOCPhase3Compiler("Financial Transactions and Fraud Schemes");
					break;
				case 2:
					tocP3Compiler = new TOCPhase3Compiler("Law");
					break;
				case 3:
					tocP3Compiler = new TOCPhase3Compiler("Investigation");
					break;
				case 4:
					tocP3Compiler = new TOCPhase3Compiler("Fraud Prevention and Deterrence");
					break;
				}
				break;
			case 2:
				tocP3Compiler.compile();
				break;
			case 3:
				break;
			default:
				System.out.println("Invalid option.  Please enter a valid selection.");
			}
		} while (input != 3);
	}
}
