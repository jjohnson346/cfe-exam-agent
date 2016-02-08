package financial.fraud.cfe.manual;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;

/**
 * CFEManualSmallDocUnitRegex implements the CFEManual interface, sectioning the manual using regular expressions
 * instead of direct string searches, as is the case for CFEManualSmallDocUnit.
 * 
 * As of 1.9.0, this is the preferred way to decompose the manual into small documents, (as opposed to
 * CFEManualSmallDocUnitFind).
 * 
 * @author jjohnson346
 * 
 */
public class CFEManualSmallDocUnitRegex extends AbstractCFEManual {

	/**
	 * implements the functionality for finding the section's name in the manual text starting at begPos. Uses a
	 * straight up text search to do this.
	 */
	@Override
	protected int getSectionBegPosition(CFEManualSection section, String manualText, int begPos) {
		String sectionLineRegex = "^" + sectionLineRegex(section.name);

		Pattern sectionPattern = Pattern.compile(sectionLineRegex, Pattern.MULTILINE);
		Matcher sectionMatcher = sectionPattern.matcher(manualText);

		int sectionBeginPosition = 0;
		if (sectionMatcher.find(begPos)) {
			sectionBeginPosition = sectionMatcher.start();
		} else {

			System.out.println("section " + section.name + " NOT FOUND.");
			System.out.println("regex: " + sectionLineRegex);
		}

		if (sectionBeginPosition == -1) {
			errors.add(section);
		}
		return sectionBeginPosition;
	}

	private String sectionLineRegex(String sectionName) {
		String escapedSectionName = sectionName.replaceAll("\\?", "\\\\?");
		escapedSectionName = escapedSectionName.replaceAll("\\(", "\\\\(");
		escapedSectionName = escapedSectionName.replaceAll("\\)", "\\\\)");
		escapedSectionName = escapedSectionName.replaceAll("\\.", "\\\\.");
		escapedSectionName = escapedSectionName.replaceAll("\\—", "\\\\—");
		// return "^" + escapedSectionName;
		return escapedSectionName;
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

	public List<CFEManualSection> getManualSectionForPage(String pageNumber) {
		return getManualSectionForPageUtil(root, pageNumber);
	}

	private List<CFEManualSection> getManualSectionForPageUtil(CFEManualSection section, String pageNumber) {
		if (section.subSections.isEmpty())
			return null;

		List<CFEManualSection> sectionsForPage = new ArrayList<CFEManualSection>();

		for (CFEManualSection subSection : section.subSections.values()) {
			if (subSection.pageNumber.equals(pageNumber))
				sectionsForPage.add(subSection);
			List<CFEManualSection> subSectionsForPage = getManualSectionForPageUtil(subSection, pageNumber);
			if (subSectionsForPage != null)
				sectionsForPage.addAll(getManualSectionForPageUtil(subSection, pageNumber));
		}
		return sectionsForPage;

	}

	public static void main(String[] args) {
		CFEManualSmallDocUnitRegex manual = new CFEManualSmallDocUnitRegex();
		
		List<CFEManualSection> sections = manual.getManualSectionForPage("1.201");
		for(CFEManualSection section : sections)
			System.out.println(section.name);
	}

}