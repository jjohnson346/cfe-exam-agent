package financial.fraud.cfe.ir;

import financial.fraud.cfe.manual.CFEManualSection;

/**
 * serves as the interface to be implemented by any class implementing search engine functionality.
 * Consists of a single method, queryRetrieve, which takes two parms, the query string and the testing
 * area over which to perform search, and returns a priority queue of cfe manual section objects.
 * 
 * @author jjohnson346
 *
 */
public interface SearchEngine {
	
	
	/**
	 * returns the manual sections that are relevant to the query, specific to the testing area.
	 * 
	 * @param query			the query string 
	 * @param testingArea	the testing area over whose cfe manual sections are to be included in search
	 * @return				a priority queue of ranked manual sections
	 */
	public PriorityQueue<CFEManualSection> queryRetrieve(String query, String testingArea);
}
