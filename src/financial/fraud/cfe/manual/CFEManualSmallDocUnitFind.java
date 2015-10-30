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
public class CFEManualSmallDocUnitFind extends AbstractCFEManual {

//	public CFEManualSmallDocUnit() {
//		
//		super();
//
//		System.out.println("initializing logger...");
//		logger = Logger.getInstance();
//
//		// set doc text for each cfe manual section object.
//		logger.println("Setting document bodies for manual sections...", DetailLevel.MEDIUM);
//		this.setBodiesForSections();
//		logger.println("Setting document bodies process complete.", DetailLevel.MEDIUM);
//	}
//
//	private void setBodiesForSections() {
//		setSectionBody(root);
//	}
//
//	private void setSectionBody(CFEManualSection section) {
//		section.setText();
//		for (CFEManualSection subSection : section.subSections.values()) {
//			setSectionBody(subSection);
//		}
//	}
//

	/**
	 * implements the functionality for finding the section's name in the manual text starting
	 * at begPos.  Uses a straight up text search to do this.
	 */
	@Override
	protected int getSectionBegPosition(CFEManualSection section, String manualText, int begPos) {
		return manualText.indexOf(section.name, begPos);
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