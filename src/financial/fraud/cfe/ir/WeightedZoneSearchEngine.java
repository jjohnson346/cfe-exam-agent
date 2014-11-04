package financial.fraud.cfe.ir;

import java.util.HashMap;
import java.util.Map;

import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.manual.CFEManualSection;

public class WeightedZoneSearchEngine extends AbstractSearchEngine {

	public WeightedZoneSearchEngine(CFEManualLargeDocUnit cfeManual) {
		super(cfeManual);
	}

	@Override
	public PriorityQueue<CFEManualSection> queryRetrieve(String query,
			String testingArea) {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * retrieves each manual sub-section object in which a particular query string is found *in its name* for 
	 * a particular manual section.  This retrieval includes the manual section for which this 
	 * function is called, itself, if the query string is found in its name, as well.
	 * 
	 * 
	 * IMPORTANT TODO:  09/13/2014 - this function should probably be moved out of this class into an IR class
	 * where getting this information is relevant.  This query search functionality doesn't seem appropriate
	 * in the cfe manual....
	 * 
	 * @param section		the manual section object to be searched, and whose children sections are to be searched
	 * @param searchString	the string to look for in the manual section and sub-sections
	 * @return				a hash map containing all of the sections in which the query string was found.
	 */
	private Map<String, CFEManualSection> getDetailSubSections(CFEManualSection section, String searchString) {
		Map<String, CFEManualSection> results = new HashMap<String, CFEManualSection>();

		if (section.name.toLowerCase().indexOf(searchString.toLowerCase()) != -1)
			results.put(section.name, section);

		for (CFEManualSection subSection : section.subSections.values()) {
			Map<String, CFEManualSection> resultsSub = getDetailSubSections(subSection, searchString);
			for (CFEManualSection s : resultsSub.values())
				results.put(s.name, s);
		}
		return results;
	}

	/**
	 * retrieves each manual sub-section object in which a particular query string is found *in its name* for 
	 * a particular *question* section.  
	 * 
	 * @param questionSecitonName	the name of the question section whose corresponding manual section object 
	 * is to be searched, along with children and whose children sections
	 * @param searchString	the string to look for in the manual section and sub-sections corresponding to the question
	 * section
	 * @return				a hash map containing all of the sections in which the query string was found.
	 */
	public Map<String, CFEManualSection> getQuestionDetailSections(String questionSectionName, String searchString) {
		CFEManualSection manualSection = cfeManual.getManualSectionForQuestionSection(questionSectionName);

		if (manualSection == null)
			// if no manualSection, simply return null.
			return null;
		else
			// manual section exists - find all subsections with search string as key
			return getDetailSubSections(manualSection, searchString);
	}

	/**
	 * retrieves each manual sub-section object in which a particular query string is found *in its name* for 
	 * a particular manual section.  This retrieval includes the manual section for which this 
	 * function is called, itself, if the query string is found in its name, as well.
	 * 
	 * @param manualSectionId	the *id* of the manual section object to be searched, and whose children sections are to be searched
	 * @param searchString	the string to look for in the manual section and sub-sections
	 * @return				a hash map containing all of the sections in which the query string was found.
	 */
	public Map<String, CFEManualSection> getManualDetailSections(String manualSectionId, String searchString) {
		CFEManualSection manualSection = cfeManual.getManualSection(manualSectionId);

		if (manualSection == null)
			// if no manualSection, simply return null.
			return null;
		else
			// manual section exists - find all subsections with search string as key
			return getDetailSubSections(manualSection, searchString);
	}

	/**
	 * prints out, for a given cfe manual section, the manual sub-sections containing a particular 
	 * search string 
	 * 
	 * @param manualSectionId the section to search for sub-sections containing the search string
	 * @param searchString the search string to look for
	 */
	public void outputDetailSectionsForSearchString(String manualSectionId, String searchString) {
		Map<String, CFEManualSection> results = getManualDetailSections(manualSectionId, searchString);

		System.out.println("Query Results for " + manualSectionId + ", " + searchString);
		for (CFEManualSection s : results.values())
			System.out.println(s.name + ", " + s.pageNumber);
	}


}
