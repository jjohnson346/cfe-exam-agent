package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
* CFEManualSectionExtractor encapsulates logic for finding capitalized section names within the manual that are not
* included in the table of contents file. This functionality significantly increases the granularity and depth of the
* sections into which the cfe manual can be broken down for the purpose of information retrieval.
* 
* @author jjohnson346
*
*/

public class CFEManualSectionExtractor {

	private String cfeManualText;			// stores the text of the cfe manual (excl. toc)

	private String currentPageNumber;

	/**
	 * constructor loads the contents of the cfe manual from a text file 
	 * into a local variable.
	 */
	public CFEManualSectionExtractor() {
		cfeManualText = getManualText();
	}

	/**
	 * retrieves section names in the manual that are not already listed in the 
	 * table of contents for the manual.
	 * 
	 * @return a list of strings containing the section names and page numbers for those sections not in toc
	 */
	public List<String> extractSections() {
		ArrayList<String> sections = new ArrayList<String>();
		
		// retrieve the text of the manual.
		Scanner scanner = new Scanner(cfeManualText);

		// build the string for the pattern to use for finding unwanted sections
		String unwantedsPattern = buildUnwantedsPattern();
		
		// retrieve the existing sections in the manual by traversing the toc file.
		HashSet<String> existingSections = getManualSectionNames();

		
		// Traverse the lines of the text, where for each line of text,
		// determine if it contains a relevant section name that is not
		// already in the table of contents, and if so, 
		// append it to a list containing section names and page numbers.
		while (scanner.hasNextLine()) {
			String currentLine = scanner.nextLine();

			// update the current page number attribute if this current line
			// is a page header.
			updateCurrentPageNumber(currentLine);

			// TODO: the pattern string below appears to have been corrupted in translation
			// from the windows machine to the mac.  need to figure out what the pattern was
			// supposed to be - may want to check what this pattern looks like on the 
			// toshiba pc.
			
			
			String pattern = "[^? $•a-z\\.%][A-Z0-9 -\\(\\)\\[\\]\\.“”‘’,;?:!§-]*";
			// String pattern = "[^? $•a-z\\.%][A-Z0-9 -\\(\\)\\[\\]\\.“”‘’,;?:!§]*";
			// String pattern = "[A-Z0-9\\-\\(\\)\\[\\]“”‘’,;?:!][A-Z0-9 \\-\\(\\)\\[\\]“”‘’,;?:!]*";
			
			// if the current line isn't a capitalized section heading, go to next line.
			if (!currentLine.matches(pattern))
				continue;

			// current line is a capitalized section heading.
			// make sure it's not one of the unwanted ones.
			if (currentLine.matches(unwantedsPattern))
				continue;
			
			// TODO: like the pattern string above, this pattern also looks like it
			// was corrupted in translation.  Need to modify.  Check this line on the 
			// pc.
			if (currentLine.matches("[0-9\\-\\.\\(\\)\\[\\]\\$“”‘’',;?:! ]+[ ]*"))
				continue;
			if (currentLine.matches("((DATE:)|(SUBJECT:)|(TO:)).*"))
				continue;
			if (currentLine.matches("STEP [0-9][ ]*"))
				continue;

			// make sure the current line isn't a section name we already have.
			currentLine = currentLine.trim();
			if (existingSections.contains(currentLine))
				continue;

			// ok, passed all of the tests. so add it to the collection.
			sections.add(currentLine + " ..... " + currentPageNumber);
		}
		return sections;
	}

	/**
	 * returns a string consisting of a regular expression pattern containing
	 * the strings for the sections of the manual considered unwanted from the cfe
	 * manual.  
	 * 
	 * Some of the strings added to this pattern are a bit vague in terms of their 
	 * origin and/or purpose in this string - e.g., WRONG, WRONG:, RIGHT, RIGHT - 
	 * why are these here?  they are unlikely to be complete titles of sections
	 * in the manual.  More study of the text of manual in the local file is needed.
	 * 
	 * @return a string containing a reg ex pattern for a set of unwanted sections
	 */
	private String buildUnwantedsPattern() {
		ArrayList<String> unwanteds = new ArrayList<String>();
		unwanteds.add("EXAMPLE");
		unwanteds.add("OR");
		unwanteds.add("EXPLANATION");
		unwanteds.add("WRONG");
		unwanteds.add("RIGHT");
		unwanteds.add("WRONG:");
		unwanteds.add("RIGHT:");
		unwanteds.add("PREFACE");
		unwanteds.add("ICOFR;");
		unwanteds.add("NO");
		unwanteds.add("APPENDIX A");
		unwanteds.add("FRAUD EXAMINATION CHECKLIST");
		unwanteds.add("PERSONAL RECORDS");
		unwanteds.add("SAMPLE EXAMINATION REPORT");
		unwanteds.add("SAMPLE FRAUD EXAMINATION REPORT");
		unwanteds.add("SAMPLE FRAUD EXAMINATION REPORTS");
		unwanteds.add("MEMORANDUM");
		unwanteds.add("TO: FILE \\(08-4422\\)");
		unwanteds.add("\\[LONG FORM\\]");
		unwanteds.add("EXECUTIVE SUMMARY");
		unwanteds.add("CONFIDENTIAL");
		unwanteds.add("INDEX TO REPORT");
		unwanteds.add("ITEM                         PAGE");
		unwanteds.add("MEMO OF PREDICATION");
		unwanteds.add("ORION");
		unwanteds.add("INVOICE");
		unwanteds.add("TERMS: NET 30 DAYS");
		unwanteds.add("SAMPLE FORMS");
		unwanteds.add("WITNESSES:");
		unwanteds.add("BAILEY BOOKS, INCORPORATED");
		unwanteds.add("UP, 1991");
		unwanteds.add("REPOSITORY");
		unwanteds.add("ENTERED");
		unwanteds.add("DEPARTED");
		unwanteds.add("MM  DD  YYYY");
		unwanteds.add("LAW                     EQUITY");
		unwanteds.add("–  ACL");
		unwanteds.add("–  IDEA");
		unwanteds.add("VP");
		unwanteds.add("450-1011 X 1 3   4");
		unwanteds.add("550-2022  X 2   2");
		unwanteds.add("650-3033 8 7 X 8 9 32");
		unwanteds.add("750-4044 1  1 X  2");
		unwanteds.add("850-5055   8  X 8");
		unwanteds.add("\\[SHORT FORM\\]");
		unwanteds.add("LINDA REED COLLINS CASE STUDY");
		unwanteds.add("FROM: LOREN D. BRIDGES, CFE");		
		unwanteds.add("20XX.");		
		unwanteds.add("DRP.");		
		unwanteds.add("CEO.");		
		unwanteds.add("M.D.”");		
		unwanteds.add("M.I.");
		unwanteds.add("2913.04 (B), (D); 2913.42; 2913.81; 2933.41(A)(7)");
		unwanteds.add("FROM: LOREN D. BRIDGES");
		unwanteds.add("FROM:  LOREN D. BRIDGES, CFE");
		unwanteds.add("FROM:  MARK W. STEINBERG, CHIEF FINANCIAL OFFICER");

		StringBuilder unwantedsPattern = new StringBuilder();
		unwantedsPattern.append("(");
		for (int i = 0; i < unwanteds.size(); i++) {
			unwantedsPattern.append("(" + unwanteds.get(i) + ")");
			if (i < unwanteds.size() - 1)
				unwantedsPattern.append("|");
		}
		unwantedsPattern.append(")[ ]*");
		return new String(unwantedsPattern);
	}

	/**
	 * retrieves the set of section names of the cfe manual from a text
	 * file containing the table of contents section of the cfe manual (excl.
	 * the text of the manual).  This method makes use of the fact that the 
	 * file containing the toc is formatted with each line as
	 * <section name> ........... <page number>.
	 * Using the dots as a delimiter, this method extracts just the first 
	 * part of each line (containing the section name).
	 * 
	 * @return a hash set of strings containing the manual section names
	 */
	private HashSet<String> getManualSectionNames() {
		HashSet<String> existingSections = new HashSet<String>();

		String toc = getTOC();

		Scanner scanner = new Scanner(toc);
		while (scanner.hasNextLine()) {
			// read next line.
			Scanner lineScanner = new Scanner(scanner.nextLine());
			lineScanner.useDelimiter("\\.{5,}");

			String sectionName = lineScanner.next().trim();
			existingSections.add(sectionName);
		}

		return existingSections;
	}

	/**
	 * retrieves the table of contents from a text file (output of compiler phase 5).
	 * 
	 * @return a string containing the table of contents of the cfe manual
	 */
	private String getTOC() {
		String toc = null;
		Scanner scanner = null;

		try {
			// modify slashes for mac vs. pc.
			scanner = new Scanner(new File("manual//2011_fem_aggregate_toc_phase_5.txt"));
			//scanner = new Scanner(new File("manual\\2011_fem_aggregate_toc_phase_5.txt"));
			scanner.useDelimiter("\\Z");
			toc = scanner.next();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
		return toc;
	}

	/**
	 * retrieves the text of the cfe manual from the manual text file (which contains
	 * only the manual text, and not the table of contents!).
	 * 
	 * @return a String object containing the text portion of the cfe manual (excluding the toc)
	 */
	private String getManualText() {
		String manual = null;
		Scanner scanner = null;

		try {
			// modify slashes for mac vs. pc.
			scanner = new Scanner(new File("manual//2011_zz_fem_manual_text.txt"));
			//scanner = new Scanner(new File("manual\\2011_zz_fem_manual_text.txt"));
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
	 * prints out the page numbers that are given at the top of each page within
	 * the cfe manual text for all pages in the manual.  This method uses 
	 * reg ex pattern matching to find those lines in the manual that serve as 
	 * page headers (containing page numbers). 
	 * 
	 * This method returns nothing, and as such, is not used by any other method in this
	 * class.  This method is used to verify that the reg ex patterns used for matching
	 * are properly specified, so that those same patterns can be used elsewhere in code
	 * for retrieving page number/header lines within the manual text, specifically, in the
	 * updateCurrentPageNumber() method.
	 */
	private void getPageNumbers() {
		Scanner scanner = new Scanner(cfeManualText);
		int count = 0;
		String oddPageHeadPattern = "(2011 Fraud Examiners Manual.*[0-9]+\\.[0-9]+[ ]*)";
		String evenPageHeadPattern = "([0-9]+\\.[0-9]+.*2011 Fraud Examiners Manual[ ]*)";
		String pageHeaderPattern = evenPageHeadPattern + "|" + oddPageHeadPattern;
		Pattern pageNumPattern = Pattern.compile("[0-9]+\\.[0-9]+");
		String pageNumber = null;

		while (scanner.hasNextLine()) {
			String line = scanner.nextLine();
			if (line.matches(pageHeaderPattern)) {
				Matcher m = pageNumPattern.matcher(line);
				if (m.find())
					pageNumber = m.group();
				System.out.println(pageNumber);
			}
			if (count++ > 2000)
				break;
		}
	}

	/**
	 * updates the currentPageNumber attribute with the page number found in the 
	 * current line of manual text, (passed in as an input parm).
	 * 
	 * @param currentLine the current line of manual text to find the page number in (if it's there)
	 */
	private void updateCurrentPageNumber(String currentLine) {
		String oddPageHeadPattern = "(2011 Fraud Examiners Manual.*[0-9]+\\.[0-9]+[ ]*)";
		String evenPageHeadPattern = "([0-9]+\\.[0-9]+.*2011 Fraud Examiners Manual[ ]*)";
		String pageHeaderPattern = evenPageHeadPattern + "|" + oddPageHeadPattern;
		Pattern pageNumPattern = Pattern.compile("[0-9]+\\.[0-9]+");

		if (currentLine.matches(pageHeaderPattern)) {
			Matcher m = pageNumPattern.matcher(currentLine);
			if (m.find())
				currentPageNumber = m.group();
		}
	}

	/**
	 * performs a unit test of the CFEManualSectionExtractor class.
	 * Instantiates the class and then calls the method, extractSections().
	 * Outputs the results of this method (a list of strings containing 
	 * the section names) to the console.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CFEManualSectionExtractor e = new CFEManualSectionExtractor();

		// e.getPageNumbers();

		List<String> sections = e.extractSections();
		for (String section : sections) {
			System.out.println(section);
		}
		System.out.println(sections.size());
	}

}
