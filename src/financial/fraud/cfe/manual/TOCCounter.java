package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * encapsulates logic for counting the number of lines in the output file
 * to the toc compilation process.  This class is not used by the cfe agent
 * software - this is simply to provide line count info about the toc file.
 * 
 * @author jjohnson346
 *
 */
public class TOCCounter {

	/**
	 * retrieves contents of the toc compile process output file.
	 * 
	 * @return 		string containing the contents of final toc file.
	 */
	private String getSourceTOC() {

		String sourceTOC = null;

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("manual\\2011_zz_fem_toc.txt"));
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
	 * counts the number of lines in the final toc file.
	 * 
	 * @return		the integer number of lines.
	 */
	public int countLines() {
		String toc = getSourceTOC();
		Scanner scanner = new Scanner(toc);
		
		int counter = 0;
		while(scanner.hasNextLine()) {
			scanner.nextLine();
			counter++;
		}
		return counter;
	}
	
	/**
	 * instantiates, runs the TOCCounter.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TOCCounter c = new TOCCounter();
		System.out.println(c.countLines());
	}

}
