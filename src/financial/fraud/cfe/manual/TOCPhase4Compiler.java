package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;

/**
 * This class combines the toc's from the various files created in phase 3, including 
 * each file from the 4 testing areas and the main toc file.  Recall that the main toc file
 * contains sections and subsections, but the toc files for each of the 4 testing areas
 * contain sub-subsections - i.e., section name for a level below that given in the 
 * main toc.  So, this class commingles this information to create a hierarchical toc 
 * format with sections and subsections (indented 1 tab) from main, and then sub-subsections
 * (indented 2 tabs) from the testing area tocs.
 * 
 * @author Joe
 *
 */
public class TOCPhase4Compiler extends TOCCompiler {

	/**
	 * assigns value of aggregate to testing area variable.
	 */
	public TOCPhase4Compiler() {
		super("Aggregate");
		phase = "4";
	}

	/**
	 * retrieves the toc input text from all of the files of the 4 testing areas.
	 */
	@Override
	protected final String getSourceTOC() {
		String sourceTOC = null;

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("2011_fem_financial_transactions_toc_phase_3.txt"));
			scanner.useDelimiter("\\Z");
			sourceTOC = scanner.next();

			scanner = new Scanner(new File("2011_fem_law_toc_phase_3.txt"));
			scanner.useDelimiter("\\Z");
			sourceTOC += "\n" + scanner.next();

			scanner = new Scanner(new File("2011_fem_investigation_toc_phase_3.txt"));
			scanner.useDelimiter("\\Z");
			sourceTOC += "\n" + scanner.next();

			scanner = new Scanner(new File("2011_fem_fraud_prevention_toc_phase_3.txt"));
			scanner.useDelimiter("\\Z");
			sourceTOC += "\n" + scanner.next();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
		return sourceTOC;
	}

	/**
	 * combines the tocs - main, and those of the 4 testing areas into a hierarchical format.
	 */
	@Override
	protected String generateTargetTOC(String sourceTOC) {
		StringBuilder targetTOC = new StringBuilder();
		Queue<String> mainTOCSections = getMainTOCSections();
		String currentMainTOCSection = mainTOCSections.poll();
		String currentSection = null;
		String contentsLine = null;
		boolean isMainTOCSectionMatched = false;

		Scanner scanner = new Scanner(sourceTOC);

		while (scanner.hasNextLine()) {
			contentsLine = scanner.nextLine();
			Scanner lineScanner = new Scanner(contentsLine);

			// specify the delimiter as a sequence of dots
			lineScanner.useDelimiter("(\\s*(\\Q.\\E){2,}\\s*)+");
			currentSection = lineScanner.next().trim();

			if (currentSection.equals(currentMainTOCSection)) {
				// this section was found in the main toc section list.
				// so, it's either a section or subsection.
				isMainTOCSectionMatched = true;

				// if current section is all upper case, this is a section - don't indent. 
				// if not all upper case, then this is a subsetion - indent one tab.
				if (currentSection.toUpperCase().equals(currentSection))
					targetTOC.append(String.format("%s\n", contentsLine));
				else
					targetTOC.append(String.format("\t%s\n", contentsLine));
				if (!mainTOCSections.isEmpty()) {
					currentMainTOCSection = mainTOCSections.poll();
					isMainTOCSectionMatched = false;
				}
			} else {
				// this section was not in the main toc section list.
				// therefore, it's not a section or subsection - it's a sub-subsection.
				targetTOC.append(String.format("\t\t%s\n", contentsLine));
			}
		}

		if (!isMainTOCSectionMatched) {
			System.out.println("Unable to match main toc section: " + currentMainTOCSection);
		} else {
			System.out.println("All main TOC sections matched successfully.");
		}

		return new String(targetTOC);
	}

	/**
	 * retrieves the main toc sections contained in the main toc output file from phase 3.
	 * 
	 * @return a queue whose elements are the entries in the main toc.
	 */
	private Queue<String> getMainTOCSections() {
		String mainTOC = null;
		Queue<String> sections = new LinkedList<String>();

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("2011_fem_main_toc_phase_3.txt"));
			scanner.useDelimiter("\\Z");

			mainTOC = scanner.next();

			scanner = new Scanner(mainTOC);
			while (scanner.hasNextLine()) {
				Scanner lineScanner = new Scanner(scanner.nextLine());

				// specify the delimiter as a sequence of dots
				lineScanner.useDelimiter("(\\s*(\\Q.\\E){2,}\\s*)+");
				sections.add(lineScanner.next().trim());
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
		return sections;
	}

	/**
	 * tests the phase 4 compiler.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TOCPhase4Compiler tocP4Compiler = null;
		int input;

		do {
			System.out.println("TOCPhase4Compiler menu:");
			System.out.println("1.  Compile (P4) TOC.");
			System.out.println("2.  Quit.");

			Scanner scanner = new Scanner(System.in);
			input = scanner.nextInt();

			switch (input) {
			case 1:
				tocP4Compiler = new TOCPhase4Compiler();
				tocP4Compiler.compile();
				break;
			case 2:
				break;
			default:
				System.out.println("Invalid option.  Please enter a valid selection.");
			}
		} while (input != 2);
	}
}
