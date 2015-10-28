package financial.fraud.cfe.manual;

import java.util.Iterator;

import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;

/**
 * The CFEManualLargeDocUnit class breaks up the manual into hierarchical sections where the parent section for a
 * subtree of subsections contains the text that subsumes the text for all of its child sections. Thus, if a term is
 * found in a child section it will also be found in the parent section.
 * 
 * @author jjohnson346
 *
 */
public class CFEManualLargeDocUnit extends AbstractCFEManual {

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

		Logger.getInstance().println("Loading section details for section, " + section.name + "...", DetailLevel.FULL);

		CFEManualSection currSection = null;
		CFEManualSection prevSection = null;

		// initialize an iterator for the child sections of the current section.
		Iterator<CFEManualSection> iterator = section.subSections.values().iterator();

		if (iterator.hasNext()) {
			currSection = iterator.next();
			prevSection = currSection;
		}

		// set the end position of the prior child section to be the beginning position of the
		// current child section.
		while (iterator.hasNext()) {
			currSection = iterator.next();
			prevSection.endPosition = currSection.begPosition;
			prevSection.setText();

			if (prevSection.endPosition < prevSection.begPosition) {
				errors.add(prevSection);
			}

			prevSection = currSection;
		}

		// for the last section, set end position to be the end position for the parent section.
		if (currSection != null) {
			currSection.endPosition = section.endPosition;
			currSection.setText();
		}

		for (CFEManualSection s : section.subSections.values()) {
			loadEndPositions(s, manualText);
		}

		Logger.getInstance().println("Section details load for section, " + section.name + " complete.",
				DetailLevel.FULL);

	}

}