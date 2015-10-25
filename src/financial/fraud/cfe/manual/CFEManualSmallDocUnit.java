package financial.fraud.cfe.manual;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;

/**
 * The CFEManualSmallDocUnit class is a concrete class implementing CFEManual interface that breaks up the sections the
 * cfe manual into relatively granular chunks, although not any smaller than the granularity offered by the index.
 * (Recall there are, I believe, discernible sections within the manual that are not reflected in the manual's index -
 * it may be fruitful to break up the manual down by that finer level of detail.)
 * 
 * @author jjohnson346
 * 
 */
public class CFEManualSmallDocUnit extends AbstractCFEManual {

	public CFEManualSmallDocUnit() {

		super();

		System.out.println("initializing logger...");
		logger = Logger.getInstance();

		// set doc text for each cfe manual section object.
		logger.println("Setting document bodies for manual sections...", DetailLevel.MEDIUM);
		this.setBodiesForSections();
		logger.println("Setting document bodies process complete.", DetailLevel.MEDIUM);
	}

	private void setBodiesForSections() {
		setSectionBody(root);
	}

	private void setSectionBody(CFEManualSection section) {
		section.setText();
		for (CFEManualSection subSection : section.subSections.values()) {
			setSectionBody(subSection);
		}
	}

	/**
	 * determines the beginning character position of for each CFEManual section object within the text of the cfe
	 * manual. For the root section, this is always 0. For the rest, it starts the search at the beginning position of
	 * the previous section, looking for the section name starting at that point.
	 * 
	 * Note: for the root node, beginning position is set in the buildManualTree() method (not here).
	 * 
	 * This function is called recursively for each cfe manual section object, where for a call to this function for a
	 * given section object, this function calls this function for each child section of the current section.
	 * 
	 * @param section
	 *            the CFEManualSection object for which to determine the beginning position
	 * @param manualText
	 *            a string containing the text of the cfe manual
	 * @param startPos
	 *            the beginning position of the prior section.
	 * @return an integer, the beginning position for the CFEManualSection object
	 */
	protected int loadBegPositions(CFEManualSection section, String manualText, int prevBegPos) {
		//	protected int loadBegPositions(CFEManualSection section, String manualText, int startPos) {
		// start search from last begPosition.
		// int pagePos = manualText.indexOf(section.pageNumber);

		int pagePos = 0;

		final String PAGE_LINE_REGEX = "(^2011 Fraud Examiners Manual\\s+?" + section.pageNumber + "\\s*?$|^"
				+ section.pageNumber + "\\s+?2011 Fraud Examiners Manual\\s+?)";

		Pattern pagePattern = Pattern.compile(PAGE_LINE_REGEX, Pattern.MULTILINE);
		Matcher pageMatcher = pagePattern.matcher(manualText);

		logger.println("Searching for page " + section.pageNumber + "...", DetailLevel.FULL);

		if (pageMatcher.find()) {
			pagePos = pageMatcher.start();
			logger.println("page " + section.pageNumber + " found at " + pagePos, DetailLevel.FULL);
		} else {
			logger.println("page " + section.pageNumber + " NOT FOUND.", DetailLevel.FULL);
		}

		// 10/24/2015 - version 1.9.0 -
		// if the page number of the section is at a position that is less than the
		// position for the previous section, start at the position of the previous
		// section. Note, here, that we've added one to prevBegPos in order to
		// address the issue of when two consecutive sections are named very similarly.
		// For example, consider these two sections:
		// 1. Computer Fraud versus Computer Crime
		// 2. Computer Fraud
		// In this case, without the +1, Computer Fraud is assigned the same begPos
		// as Computer Fraud versus Computer Crime...! +1 provides the fix.
		int begPos = pagePos < prevBegPos ? prevBegPos : pagePos;

		// find the name of section immediately after begPos.
		section.begPosition = manualText.indexOf(section.name, begPos);

//		if (section.name.toLowerCase().equals("computer and internet fraud")
//				|| section.name.toLowerCase().equals("computer fraud versus computer crime")
//				|| section.name.toLowerCase().equals("computer fraud")
//				|| section.name.toLowerCase().equals("computer crime"))
//			System.out.println(section.name + " " + pagePos + " " + (startPos + 1) + " " + section.begPosition);

		if (section.parent != null) {
			if (section.parent.name.toLowerCase().equals("Elements of White-Collar Offenses")) {
				System.out.println(section.name + " " + pagePos + " " + (prevBegPos + 1) + " " + section.begPosition);
			}
		}
		// final String SECTION_LINE_REGEX = "^" + section.name + "\\s*?$";
		//
		// Pattern sectionPattern = Pattern.compile(SECTION_LINE_REGEX, Pattern.MULTILINE);
		// Matcher sectionMatcher = sectionPattern.matcher(manualText);
		//
		// if (sectionMatcher.find(pagePos))
		// section.begPosition = sectionMatcher.start();

		if (section.begPosition == -1) {
			errors.add(section);
		}

		prevBegPos = section.begPosition + section.name.length();
		
		for (CFEManualSection s : section.subSections.values()) {
			// modified the recursive call so as to *not* use prevBegPos, but instead
			// use section.begPosition + section.name.length. For consecutive sections that are named
			// similarly, particularly where there is a parent/child relationship
			// between these sections, using prevBegPos is problematic. see comment
			// above regarding the Computer Fraud sections.
			begPos = loadBegPositions(s, manualText, prevBegPos);
			// begPos = loadBegPositions(s, manualText, prevBegPos);
			prevBegPos = begPos + s.name.length();
		}
		return section.begPosition;
	}

	/**
	 * determines the position of the last character for each CFEManualSection object. This is done by iterating through
	 * the section objects of the tree, and setting the end position of the prior section to equal the beginning
	 * position of the current section.
	 * 
	 * Note: for the root node, ending position is set in the buildManualTree() method (not here).
	 * 
	 * This function is called recursively for the children of each CFEManual section object.
	 * 
	 * @param section
	 *            the CFEManualSection object for which to determine the end position
	 * @param manualText
	 *            a string containing the text of the cfe manual
	 */
	protected void loadEndPositions(CFEManualSection section, String manualText) {

		logger.println("Loading section details for section, " + section.name + "...", DetailLevel.FULL);

		CFEManualSection currSection = null;
		CFEManualSection prevSection = null;

		boolean hasChild = false; // stores whether there are subsections to this section
									// useful when setting ending position

		// initialize an iterator for the child sections of the current section.
		Iterator<CFEManualSection> iterator = section.subSections.values().iterator();

		if (iterator.hasNext()) {
			hasChild = true;
			currSection = iterator.next();
			prevSection = currSection;
		}

		// if hasChild = true, then currSection is currently referencing the first subsection.
		// Record the beginning position of this first subsection, as it will be used later to
		// set the end position of the parent section.
		int firstSubSectionBegPos = 0;

		if (hasChild)
			firstSubSectionBegPos = currSection.begPosition;

		// set the end position of the prior child section to be the beginning position of the
		// current child section.
		while (iterator.hasNext()) {
			currSection = iterator.next();
			prevSection.endPosition = currSection.begPosition;

			if (prevSection.endPosition < prevSection.begPosition) {
				errors.add(prevSection);
			}

			prevSection = currSection;
		}

		// for the last section, set end position to be the end position for the parent section.
		if (currSection != null) {
			currSection.endPosition = section.endPosition;
			// currSection.setText();
		}

		for (CFEManualSection s : section.subSections.values()) {
			loadEndPositions(s, manualText);
		}

		// now, reset the parent section end position (in keeping with small doc unit approach):
		// set the end position for the parent section to be the beginning position
		// of the first subsection (the value stored in firstSubSectionBegPos
		// earlier in this function).
		if (hasChild)
			section.endPosition = firstSubSectionBegPos;

		logger.println("Section details load for section, " + section.name + " complete.", DetailLevel.FULL);

	}

}