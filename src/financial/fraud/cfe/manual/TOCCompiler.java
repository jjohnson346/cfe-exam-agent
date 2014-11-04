package financial.fraud.cfe.manual;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

/**
 * abstract class implemented by toc compiler component classes.
 * 
 * @author jjohnson346
 *
 */
public abstract class TOCCompiler {

	/**
	 * stores the testing area for which the subclass is instantiated.  Used to get the 
	 * proper input file and create the proper name for the output file.
	 */
	protected String testingArea;					

	/**
	 * stores the testing area file name substring, determined by the value in the
	 * testingArea variable.
	 */
	protected String testingAreaFileSubString;

	/**
	 * stores the phase for the instance of the subclass.
	 */
	protected String phase;
	
	/**
	 * retrieves the manual text.
	 * @return
	 */
	protected String getSourceManualText() {
		String sourceManualText = null;

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("manual\\2011_zz_fem_manual_text.txt"));
			scanner.useDelimiter("\\Z");
			sourceManualText = scanner.next();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
		return sourceManualText;
	}

	/**
	 * assigns the testing area value passed in to the testingArea variable, and uses the
	 * value to assign the proper value to testingAreaSubString.
	 * 
	 * @param testingArea
	 */
	protected TOCCompiler(String testingArea) {
		this.testingArea = testingArea;

		if (testingArea.equals("Main")) {
			testingAreaFileSubString = "main";
		} else if (testingArea.equals("Financial Transactions and Fraud Schemes")) {
			testingAreaFileSubString = "financial_transactions";
		} else if (testingArea.equals("Law")) {
			testingAreaFileSubString = "law";
		} else if (testingArea.equals("Investigation")) {
			testingAreaFileSubString = "investigation";
		} else if (testingArea.equals("Fraud Prevention and Deterrence")) {
			testingAreaFileSubString = "fraud_prevention";
		} else { // testing area must be Aggregate
			testingAreaFileSubString = "aggregate";
		}
	}
	
	/**
	 * makes calls to overriden functions to retrieve input file contents,
	 * apply a compilation algorithm to the input text, and write the output
	 * to an output file.
	 */
	public void compile() {
		String sourceTOC = getSourceTOC();
		String targetTOC = generateTargetTOC(sourceTOC);
		writeToFile(targetTOC);
	}

	/**
	 * abstract method that must be overriden in any concrete subclass with 
	 * logic for retrieving the contents of the input file.
	 * 
	 * @return string containing contents of input file
	 */
	protected abstract String getSourceTOC();

	/**
	 * processes the input text, supplied in sourceTOC
	 * 
	 * @param sourceTOC the input text
	 * @return the results from applying the logic in the method to the input text
	 * 
	 */
	protected abstract String generateTargetTOC(String sourceTOC);

	/**
	 * writes the ouptut text to a file whose name includes the value stored in the
	 * testingAreaFileSubString variable.
	 * 
	 * @param target
	 */
//	protected void writeToFile(String target) {
//		
//		String targetFileName = "2011_fem_" + testingAreaFileSubString + "_toc_phase_" + phase + ".txt";
//		
//		try {
//			Formatter f = new Formatter(new File(targetFileName));
//			for (int i = 0; i < target.length(); i += 2000) {
//				if (i + 2000 > target.length())
//					f.format("%s", target.substring(i, target.length()));
//				else
//					f.format("%s", target.substring(i, i + 2000));
//				f.flush();
//			}
//			f.close();
//		} catch (FileNotFoundException e) {
//			System.out.println("File creation failed.");
//		}
//	}

	protected void writeToFile(String target) {
		// modified targetFileName from \\ to // because we're now on a mac.
		String targetFileName = "manual//2011_fem_" + testingAreaFileSubString + "_toc_phase_" + phase + ".txt";
		//String targetFileName = "manual\\2011_fem_" + testingAreaFileSubString + "_toc_phase_" + phase + ".txt";
		
		try {
			BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(targetFileName));
			bufferedWriter.write(target);
			bufferedWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * aligns input toc line, accounting for indenting.
	 * 
	 * @param contentsLine 		the toc contents line
	 * @return aligned 			toc line
	 */
	protected String alignTOC(String contentsLine) {
		String name = null;
		String pageNumber = null;
	
		try {
	
			Scanner lineScanner = new Scanner(contentsLine);
	
			// specify the delimiter as a sequence of dots
			lineScanner.useDelimiter("(\\s*(\\Q.\\E){2,}\\s*)+");
	
			name = lineScanner.next();
			pageNumber = lineScanner.next().trim();
		} catch (NoSuchElementException e) {
			System.out.println(contentsLine);
		}
		return contentsFormat(name, pageNumber);
	}

	/**
	 * serves as a utility function for alignTOC() function.  Inserts the proper number 
	 * of dots between the section name and its page number in the toc line, returning 
	 * a string formatted as follows:  
	 * <section name> ......<correct no. of dots>.... <page number>
	 * 
	 * @param name the section name to include in the toc line
	 * @param pageNumber the page number to place in the toc line
	 * @return a string of format <indent(s)><section name> ....(no of dots)... <page number>
	 */
	protected String contentsFormat(String name, String pageNumber) {
		final int LINE_CHAR_COUNT = 120;
		final int MIN_DOTS_COUNT = 5;
	
		int dotsCount = LINE_CHAR_COUNT - getNameLength(name) - pageNumber.length() - 2;
		if (dotsCount < MIN_DOTS_COUNT) {
			dotsCount = MIN_DOTS_COUNT;
		}
		return name + " " + StringUtils.repeat(".", dotsCount) + " " + pageNumber + "\n";
	}

	/**
	 * determines name length, accounting for indent tabs
	 * 
	 * @param name the name whose length is to be determined, including indent tabs.
	 * @return the length of the name
	 */
	protected int getNameLength(String name) {
		int tabAdjustment = 3 * StringUtils.countMatches(name, "\t");
		return name.length() + tabAdjustment;
	}
}
