package financial.fraud.cfe.manual;

import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;

/**
 * An instance of CFEManual represents a copy of the cfe manual, "read" by cfe agent before taking a test...
 * 
 * @author jjohnson346
 * 
 */
public class CFEManualSmallDocUnit2 extends AbstractCFEManual {

	protected int loadBegPositions(CFEManualSection section, String manualText, int prevBegPos) {
		// start search from last begPosition.
		// int pagePos = manualText.indexOf(section.pageNumber);

		int pagePos = 0;

		final String PAGE_LINE_REGEX = "(^2011 Fraud Examiners Manual\\s+?" + section.pageNumber + "\\s*?$|^"
				+ section.pageNumber + "\\s+?2011 Fraud Examiners Manual\\s+?)";

		Pattern pagePattern = Pattern.compile(PAGE_LINE_REGEX, Pattern.MULTILINE);
		Matcher pageMatcher = pagePattern.matcher(manualText);

//		System.out.println("Searching for page " + section.pageNumber + "...");
		if (pageMatcher.find()) {
			pagePos = pageMatcher.start();
//			System.out.println("page " + section.pageNumber + " found at " + pagePos);
		} else {
			System.out.println("page " + section.pageNumber + " NOT FOUND.");
		}

		int begPos = pagePos < prevBegPos ? prevBegPos : pagePos;

		// find the name of section immed. after begPos.
		// section.begPosition = manualText.indexOf(section.name, begPos);

//		System.out.println("building regex for " + section.name + "...");
		String regex = sectionLineRegex(section.name);
//		System.out.println(regex);
		final String SECTION_LINE_REGEX = sectionLineRegex(section.name);

		Pattern sectionPattern = Pattern.compile(SECTION_LINE_REGEX, Pattern.MULTILINE);
		Matcher sectionMatcher = sectionPattern.matcher(manualText);

		if (sectionMatcher.find(begPos)) {
			begPos = sectionMatcher.start();
			section.begPosition = begPos;
//			System.out.println("section " + section.name + " found at " + begPos);
		} else {
			
			System.out.println("section " + section.name + " NOT FOUND.");
			System.out.println("regex: " + regex);
		}

		if (section.begPosition == -1) {
			errors.add(section);
		}

		for (CFEManualSection s : section.subSections.values()) {
			begPos = loadBegPositions(s, manualText, prevBegPos);
			prevBegPos = begPos;
		}
		return section.begPosition;
	}

	private String sectionLineRegex(String sectionName) {
		String escapedSectionName = sectionName.replaceAll("\\?", "\\\\?");
		escapedSectionName = escapedSectionName.replaceAll("\\(", "\\\\(");
		escapedSectionName = escapedSectionName.replaceAll("\\)", "\\\\)");
		escapedSectionName = escapedSectionName.replaceAll("\\.", "\\\\.");
		escapedSectionName = escapedSectionName.replaceAll("\\�", "\\\\�");
		return "^" + escapedSectionName;
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

		Logger.getInstance().println("Loading section details for section, " + section.name + "...", DetailLevel.FULL);

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

		Logger.getInstance().println("Section details load for section, " + section.name + " complete.",
				DetailLevel.FULL);

	}

}