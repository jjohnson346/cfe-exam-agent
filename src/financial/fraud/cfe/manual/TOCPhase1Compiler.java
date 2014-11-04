package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * This class performs the first phase of the data scrubbing process for 2011 Fraud Examiners Manual,
 * where the scrubbing process consists of the following steps:
 *  
 * 1. Run PDFTextExtractor.java on 2011 Fraud Examiners Manual.pdf file. 
 * 2. Delete the following lines from the text file Engagement Letters 
 * (Investigation section, Appendix) Opinion Letters (also from Investigation section,
 * Appendix) 
 * 3. Run TOCPhase1Compiler on each of the TOC sections to create TOC files.
 * 
 * @author Joe
 * 
 */
public class TOCPhase1Compiler extends TOCCompiler {

	/**
	 * transducer object for processing the toc in the manual.
	 */
	private TOCTransducer transducer;

	/**
	 * stores the raw contents of the FEM.
	 */
	protected String manual;

	/**
	 * instantiates the transducer object that will process the toc contents for the 
	 * particular testing area.  The transducer object is a finite state automaton that also
	 * includes an output tape to which is written the output toc, given the 
	 * input contents from the manual.
	 * 
	 * @param testingArea the testing area for which to extract the input toc from the cfe 
	 * manual 
	 */
	public TOCPhase1Compiler(String testingArea) {
		super(testingArea);
		phase = "1";

		transducer = new TOCTransducer();

		// retrieve raw text of CFE Manual.
		Scanner scanner = null;
		try {
			// inserted manual// into this line to accommodate new directory structure
			// and the fact we're now on a mac.
			scanner = new Scanner(new File("manual//2011 Fraud Examiners Manual.txt"));
			// scanner = new Scanner(new File("manual//2011 Fraud Examiners Manual.txt"));
			scanner.useDelimiter("\\Z");
			manual = scanner.next();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
	}

	/**
	 * retrieves the input toc from the cfe manual text, using some text identifiers for
	 * the start and end of each testing area toc section.
	 */
	@Override
	protected final String getSourceTOC() {
		int tocBegin;
		int tocEnd;

		if (testingArea.equals("Main")) {
			tocBegin = manual.indexOf("2011 Fraud Examiners Manual    i");
			tocEnd = manual.indexOf("PREFACE");
		} else if (testingArea.equals("Financial Transactions and Fraud Schemes")) {
			tocBegin = manual.indexOf("2011 Fraud Examiners Manual   1.i");
			tocEnd = manual.indexOf("2011 Fraud Examiners Manual   1.101");
		} else if (testingArea.equals("Law")) {
			tocBegin = manual.indexOf("2011 Fraud Examiners Manual    2.i");
			tocEnd = manual.indexOf("2011 Fraud Examiners Manual    2.101");
		} else if (testingArea.equals("Investigation")) {
			tocBegin = manual.indexOf("2011 Fraud Examiners Manual    3.i");
			tocEnd = manual.indexOf("2011 Fraud Examiners Manual    3.101");
		} else if (testingArea.equals("Fraud Prevention and Deterrence")) {
			tocBegin = manual.indexOf("2011 Fraud Examiners Manual    4.i");
			tocEnd = manual.indexOf("2011 Fraud Examiners Manual    4.101");
		} else {
			System.out.println("Invalid section.");
			return null;
		}

		return manual.substring(tocBegin, tocEnd);
	}

	/**
	 * calls upon the transducer to process input toc, resulting in the output toc,
	 * returned by this method
	 */
	@Override
	protected final String generateTargetTOC(String sourceTOC) {
		Scanner scanner = new Scanner(sourceTOC);
		String line = null;

		transducer.reset();
		
		try {
//			while (scanner.hasNextLine()) {
			for(int i = 0; i < 20; i++) {
				
				line = scanner.nextLine();
				if (line.trim().length() > 0)
					transducer.process(line.trim());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return transducer.getTOC();
	}

	/**
	 * tests the TOCPhase1Compiler class for any of the 4 testing areas
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TOCPhase1Compiler tocP1Compiler = null;
		int input;

		do {
			System.out.println("CFE Manual TOC Extractor Menu:");
			System.out.println("1. Select CFE Manual TOC section.");
			System.out.println("2. Compile (P1) CFE Manual TOC section.");
			System.out.println("3. Quit.");

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
					tocP1Compiler = new TOCPhase1Compiler("Main");
					break;
				case 1:
					tocP1Compiler = new TOCPhase1Compiler("Financial Transactions and Fraud Schemes");
					break;
				case 2:
					tocP1Compiler = new TOCPhase1Compiler("Law");
					break;
				case 3:
					tocP1Compiler = new TOCPhase1Compiler("Investigation");
					break;
				case 4:
					tocP1Compiler = new TOCPhase1Compiler("Fraud Prevention and Deterrence");
					break;
				}
				break;
			case 2:
				tocP1Compiler.compile();
				break;
			case 3:
				break;
			default:
				System.out.println("Invalid option.  Please enter a valid selection.");
			}
		} while (input != 3);
	}
}
