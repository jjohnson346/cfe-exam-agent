package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;


/**
 * This class aligns the contents of the aggregated toc file created in phase 4.
 * It does much the same thing as in phase 3, except now it takes into account the 
 * entries are arranged in heirarchical fashion with tabs embedded in each line
 * to indicate position within the tree structure.
 * 
 * @author Joe
 *
 */
public class TOCPhase5Compiler extends TOCCompiler {

	/**
	 * assigns aggregate to testing area variable, phase = 5.
	 */
	public TOCPhase5Compiler() {
		super("aggregate");
		phase = "5";
	}

	/**
	 * retrieves the source toc from aggregate output file from phase 4.
	 */
	@Override
	protected final String getSourceTOC() {

		String sourceTOC = null;

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("2011_fem_aggregate_toc_phase_4.txt"));
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
	 * aligns the source toc text by calling on the alignTOC() function, a version slightly
	 * different from that in phase 3 compiler to account for indenting.
	 */
	@Override
	protected final String generateTargetTOC(String sourceTOC) {
		StringBuilder targetTOC = new StringBuilder();
		String sourceLine = null;
		String targetLine = null;

		Scanner scanner = new Scanner(sourceTOC);

		while (scanner.hasNextLine()) {
			sourceLine = scanner.nextLine();
			targetLine = alignTOC(sourceLine);
			targetTOC.append(targetLine);
		}
		return new String(targetTOC);
	}

	/**
	 * tests the phase 5 compiler.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TOCPhase5Compiler tocP5Compiler = null;
		int input;

		do {
			System.out.println("TOCPhase4Compiler menu:");
			System.out.println("1.  Compile (P5) TOC.");
			System.out.println("2.  Quit.");

			Scanner scanner = new Scanner(System.in);
			input = scanner.nextInt();

			switch (input) {
			case 1:
				tocP5Compiler = new TOCPhase5Compiler();
				tocP5Compiler.compile();
				break;
			case 2:
				break;
			default:
				System.out.println("Invalid option.  Please enter a valid selection.");
			}
		} while (input != 2);
	}
}
