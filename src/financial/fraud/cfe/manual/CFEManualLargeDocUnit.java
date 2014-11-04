package financial.fraud.cfe.manual;

import java.util.Iterator;

import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;


/**
 * An instance of CFEManual represents a copy of the cfe manual, "read" by cfe agent 
 * before taking a test...
 * 
 * @author jjohnson346
 *
 */
public class CFEManualLargeDocUnit extends AbstractCFEManual {

	/**
	 * determines the beginning character position of for each CFEManual section object within
	 * the text of the cfe manual.  For the root section, this is always 0.  For the rest, it starts
	 * the search at the beginning position of the previous section, looking for the section name
	 * starting at that point.
	 * 
	 * Note:  for the root node, beginning position is set in the buildManualTree() method (not here).

	 * This function is called recursively for each cfe manual section object, where for a call
	 * to this function for a given section object, this function calls this function for each 
	 * child section of the current section.
	 * 
	 * @param section		the CFEManualSection object for which to determine the beginning position
	 * @param manualText		a string containing the text of the cfe manual
	 * @param prevBegPos		the beginning position of the prior section.
	 * @return				an integer, the beginning position for the CFEManualSection object
	 */
	protected int loadBegPositions(CFEManualSection section, String manualText, int prevBegPos) {
		// start search from last begPosition.
		int pagePos = manualText.indexOf(section.pageNumber);
		int begPos = 0;

		if (pagePos < prevBegPos)
			begPos = prevBegPos;
		else
			begPos = pagePos;

		// find the name of section immed. after begPos.
		section.begPosition = manualText.indexOf(section.name, begPos);

		if (section.begPosition == -1) {
			errors.add(section);
		}

		for (CFEManualSection s : section.subSections.values()) {
			begPos = loadBegPositions(s, manualText, prevBegPos);
			prevBegPos = begPos;
		}
		return section.begPosition;
	}

	/**
	 * determines the position of the last character for each CFEManualSection object.  
	 * This is done by iterating through the section objects of the tree, and setting 
	 * the end position of the prior section to equal the beginning position of the 
	 * current section.
	 * 
	 * Note:  for the root node, ending position is set in the buildManualTree() method (not here).
	 * 
	 * This function is called recursively for the children of each CFEManual section object.
	 * 
	 * @param section 		the CFEManualSection object for which to determine the end position
	 * @param manualText		a string containing the text of the cfe manual
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

		Logger.getInstance().println("Section details load for section, " + section.name + " complete.", DetailLevel.FULL);

	}


}