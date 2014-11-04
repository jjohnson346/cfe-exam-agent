package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class performs the first phase of the data scrubbing process for 2014 Fraud Examiners Manual, where the
 * scrubbing process consists of the following steps:
 * 
 * 1. Run iSkySoft PDF Converter for 2014 Fraud Examiners Manual.pdf file (manually, outside of script). 2. Delete the
 * following lines from the text file Engagement Letters (Investigation section, Appendix) Opinion Letters (also from
 * Investigation section, Appendix) 3. Run TOCPhase1Compiler on each of the TOC sections to create TOC files.
 * 
 * This class makes use of a Transducer2014 object, which is a finite state automaton that also includes an output tape to which 
 * is written the output toc, given the input contents from the manual.
 * 
 * @author Joe
 * 
 */
public class TOCPhase1Compiler2014 extends TOCCompiler2014 {

	/**
	 * stores the raw contents of the FEM.
	 */
	protected String manual;

	/**
	 * instantiates the transducer object that will process the toc contents for the particular testing area. The
	 * transducer object is a finite state automaton that also includes an output tape to which is written the output
	 * toc, given the input contents from the manual.
	 * 
	 * @param testingArea
	 *            the testing area for which to extract the input toc from the cfe manual
	 */
	public TOCPhase1Compiler2014(String testingArea) {
		super(testingArea);
		phase = "1";

		// retrieve raw text of CFE Manual.
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("manual//2014 Fraud Examiners Manual.txt"));
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
	 * retrieves the input toc from the cfe manual text, using some text identifiers for the start and end of each
	 * testing area toc section.
	 */
	@Override
	protected final String getSourceTOC() {
		int tocBegin;
		int tocEnd;

		if (testingArea.equals("Main")) {
			String startTag = "FRAUD EXAMINERS MANUAL\r\n\r\nTABLE OF CONTENTS";
			tocBegin = manual.indexOf(startTag) + startTag.length();
			tocEnd = manual.indexOf("PREFACE", tocBegin);
		} else if (testingArea.equals("Financial Transactions and Fraud Schemes")) {
			String startTag = "FINANCIAL TRANSACTIONS AND FRAUD SCHEMES\r\n\r\nTABLE OF CONTENTS";
			tocBegin = manual.indexOf(startTag) + startTag.length();
			tocEnd = manual.indexOf("1.xxx                                                    2014 Fraud Examiners Manual", tocBegin);
		} else if (testingArea.equals("Law")) {
			tocBegin = manual.indexOf("LAW\r\n\r\nTABLE OF CONTENTS");
			tocEnd = manual.indexOf("2014 Fraud Examiners Manual                                                 2.xix");
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
	 * uses a transducer to process input toc, resulting in the output toc, returned by this method
	 */
	@Override
	protected final String generateTargetTOC(String sourceTOC) {

		// traverse the lines of the sourceTOC in order to:
		// 1.  identify and include lines that are section entries
		// 2.  identify and include all line segments that are subsection entries
		// 3.  exclude the rest - header lines, cont. lines, etc.
		ArrayList<String> tocSectionAndSubSectionEntries = new ArrayList<String>();
		Scanner scanner = new Scanner(sourceTOC);
		while (scanner.hasNextLine()) {
			String tocLine = scanner.nextLine();
			if (isValidSectionEntry(tocLine))
				tocSectionAndSubSectionEntries.add(tocLine);
			else
				tocSectionAndSubSectionEntries.addAll(this.getSubsectionEntries(tocLine));
		}

		// now, traverse again on the array list created above, assigning
		// page numbers to the section entries, and doing some preliminary alignment.
		TOCTransducer2014 tocTransducer2014 = new TOCTransducer2014();

		for (String tocLine : tocSectionAndSubSectionEntries) {
			try {
				tocTransducer2014.process(tocLine);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		return tocTransducer2014.getTOC();
	}

	private boolean isValidSectionEntry(String tocLine) {
		// if not upper case or length == 0, don't bother testing any further - return false.
		if (!tocLine.toUpperCase().equals(tocLine) || tocLine.length() == 0)
			return false;

		// passed the condition above, so we know this is a viable candidate
		// i.e., tocLine is upper case, length > 0. Now need to make sure
		// it's not part of page or section heading, label for a testing area,
		// or a continue line (i.e., line with (CONT.) at the end - (thus, redundant section))
		ArrayList<String> invalidSectionRegexes = new ArrayList<String>();
		invalidSectionRegexes.add("MENU");
		invalidSectionRegexes.add("FRAUD EXAMINERS MANUAL");
		invalidSectionRegexes.add("VOLUME (I{1,3}|IV) SECTION [0-9]");
		invalidSectionRegexes.add("SECTION [0-4]");
		invalidSectionRegexes.add("FINANCIAL TRANSACTIONS AND FRAUD SCHEMES");
		invalidSectionRegexes.add("LAW");
		invalidSectionRegexes.add("INVESTIGATION");
		invalidSectionRegexes.add("FRAUD PREVENTION AND DETERRENCE");
		invalidSectionRegexes.add(".*?\\(CONT\\Q.\\E\\)");

		for (String regex : invalidSectionRegexes)
			if (tocLine.matches(regex))
				return false;

		return true;
	}

	private ArrayList<String> getSubsectionEntries(String tocLine) {
		ArrayList<String> subSectionEntries = new ArrayList<String>();
		String tocEntryRegex = "\\S.*?\\s+(\\Q.\\E)+\\s+[0-9]+\\Q.\\E[0-9]+";
		Pattern tocEntryPattern = Pattern.compile(tocEntryRegex);
		Matcher tocEntryMatcher = tocEntryPattern.matcher(tocLine);
		while (tocEntryMatcher.find())
			subSectionEntries.add(tocEntryMatcher.group());
		return subSectionEntries;
	}

	public static void main(String[] args) {
//		TOCPhase1Compiler2014 tocee = new TOCPhase1Compiler2014("Main");
//		TOCPhase1Compiler2014 tocee = new TOCPhase1Compiler2014("Financial Transactions and Fraud Schemes");
		TOCPhase1Compiler2014 tocee = new TOCPhase1Compiler2014("Law");
		
		tocee.compile();

	}
}
