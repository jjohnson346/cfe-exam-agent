package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * the phase 1 compiler component of the cfe manual scrubbing system for removing 
 * blanks inserted by the pdf-text conversion process.  
 * 
 * encapsulates the logic for extracting the 4 sections of text body from the 
 * cfe manual (1. Financial Transactions, 2. Law, 3. Investigation, 4. Prevention).
 * This must be done by extracting these sections while omitting the toc's in the
 * manual, including the main toc at the beginning of the manual as well as the 
 * section toc's at the beginning of each section.
 * 
 * @author jjohnson346
 *
 */
public class ManualTextPhase1Compiler extends ManualTextCompiler {

	/**
	 * no-arg constructor initializes phase setting.
	 */
	public ManualTextPhase1Compiler() {
		phase = "1";
	}
	
	/**
	 * returns text body of the 4 sections with toc's removed.
	 */
	@Override
	protected String generateTargetManualText(String sourceManualText) {
		return sourceManualText;
	}

	/**
	 * retrieves manual text generated by the pdf-text conversion process.
	 */
	@Override
	protected String getSourceManualText() {
		String manual = getManual();
		String manualText = getManualText(manual);
		return manualText;
	}

	/**
	 * utility function for extracting text contents of file
	 * generated by the pdf-text conversion process.
	 * 
	 * @return 		string containing text of file from pdf-text conversion
	 */
	private String getManual() {
		String manual = null;
		Scanner scanner = null;

		try {
			scanner = new Scanner(new File("2011 Fraud Examiners Manual.txt"));
			scanner.useDelimiter("\\Z");

			manual = scanner.next();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
		return manual;
	}
	
	/**
	 * utility function that extracts the text body of the 4 sections while
	 * omitting the toc's at the beginning of each section as well as the
	 * main toc at the beginning of the manual
	 * 
	 * @param manual		a string containing the manual text output from the pdf-text conversion
	 * 
	 * @return			a string containing the text from the sections, no toc's
	 */
	private String getManualText(String manual) {
		int finTransBegin = manual.indexOf("2011 Fraud Examiners Manual   1.101");
		int finTransEnd = manual.indexOf("2011 Fraud Examiners Manual    2.i");
		String finTrans = manual.substring(finTransBegin, finTransEnd);

		int lawBegin = manual.indexOf("2011 Fraud Examiners Manual    2.101");
		int lawEnd = manual.indexOf("2011 Fraud Examiners Manual    3.i");
		String law = manual.substring(lawBegin, lawEnd);

		int investBegin = manual.indexOf("2011 Fraud Examiners Manual    3.101");
		int investEnd = manual.indexOf("2011 Fraud Examiners Manual    4.i");
		String invest = manual.substring(investBegin, investEnd);

		int preventBegin = manual.indexOf("2011 Fraud Examiners Manual    4.101");
		//int preventEnd = manual.length() - 1;
		int preventEnd = manual.indexOf("BIBLIOGRAPHY");
		String prevent = manual.substring(preventBegin, preventEnd);

		String manualText = finTrans + "\n" + law + "\n" + invest + "\n" + prevent;

		return manualText;
	}


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ManualTextPhase1Compiler manualTextP1Compiler = null;
		int input;

		do {
			System.out.println("ManualTextPhase1Compiler menu:");
			System.out.println("1.  Compile (P1) manual text.");
			System.out.println("2.  Quit.");

			Scanner scanner = new Scanner(System.in);
			input = scanner.nextInt();

			switch (input) {
			case 1:
				manualTextP1Compiler = new ManualTextPhase1Compiler();
				manualTextP1Compiler.compile();
				break;
			case 2:
				break;
			default:
				System.out.println("Invalid option.  Please enter a valid selection.");
			}
		} while (input != 2);
	}
}
