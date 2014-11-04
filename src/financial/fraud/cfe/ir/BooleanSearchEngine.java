package financial.fraud.cfe.ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.manual.CFEManualSection;

// TODO:  make data structure testing area-specific.
public class BooleanSearchEngine extends AbstractSearchEngine {

	/**
	 * is a hash map in which for each entry, the key is a token in the manual
	 * and the value is a hash set of all the sections in the manual that contain
	 * at least one occurrence of that token.
	 */
	private HashMap<String, HashSet<CFEManualSection>> invertedIndex;
	
	public BooleanSearchEngine(CFEManualLargeDocUnit cfeManual) {
		super(cfeManual);

		buildInvertedIndex();
	}

	/**
	 * the essential function in this class, responsible for constructing the 
	 * inverted index upon which the search functionality of this class is based.
	 * Recall that the inverted index is an index (here, implemented as a hash map)
	 * where the keys are the words of the cfe manual vocabulary and for each key,
	 * the value is the set (implemented as a hash set) of documents in which the 
	 * key word appears.
	 */
	private void buildInvertedIndex() {
		invertedIndex = new HashMap<String, HashSet<CFEManualSection>>();
		
		for(String word : vocabulary) {
			invertedIndex.put(word, new HashSet<CFEManualSection>());
		}
		
		for(String word : vocabulary) {
			HashSet<CFEManualSection> wordSections = invertedIndex.get(word);
			
			CFEManualSection rootSection = cfeManual.getRoot();
			for(CFEManualSection section : rootSection.getSubTreeAsList()) {
//			for(CFEManualSection section : cfeManual.getManualSections()) {
				Set<String> tokens = section.getTokenizer().getTokenTypeFreqs().keySet();
				if(tokens.contains(word)) {
					wordSections.add(section);
				}
			}
		}
	}
	
	/**
	 * retrieves the sections which each contain all of the words in the query
	 * string.
	 * 
	 * TODO:  make search incorporate testing area...
	 * 
	 * @param query
	 * @param testingArea testing area from which to select manual sections
	 * @return priority queue of cfe manual sections
	 */
	@Override
	public PriorityQueue<CFEManualSection> queryRetrieve(String query, String testingArea) {
		
		// note that testingArea not currently used in retrieval.
		
		ArrayList<String> queryList = tokenizeQuery(query);
		ArrayList<CFEManualSection> results = booleanRetrieve(queryList);
		PriorityQueue<CFEManualSection> pq = new PriorityQueue<CFEManualSection>();
		for(int i = 0; i < results.size(); i++) {
			pq.add(results.get(i), i);
		}
		return pq;
	}
	

	/**
	 * retrieves a list of sections that each contain all of the words in the query
	 * supplied as an input list of words.
	 * 
	 * TODO:  stem/normalize the words in the cfe manual sections...?
	 * 
	 * @param queryList
	 * @return array list of cfe manual sections
	 */
	public ArrayList<CFEManualSection> booleanRetrieve(ArrayList<String> queryList) {
		
		//queryWordMaps is an array list where each element is the hash set of 
		//of cfe sections containing a particular word in the query list.
		ArrayList<HashSet<CFEManualSection>> queryWordMaps = new ArrayList<HashSet<CFEManualSection>>();
		
		for(String q : queryList) {
			queryWordMaps.add(invertedIndex.get(q));
		}
		
		//Find the smallest list among all of the postings for all of the words in the query.
		int minSize = Integer.MAX_VALUE;
		HashSet<CFEManualSection> minPostings = null;
		for(HashSet<CFEManualSection> h : queryWordMaps) {
			if(h.size() < minSize) {
				minSize = h.size();
				minPostings =  h;
			}
		}
		
		//instantiate the results query.
		ArrayList<CFEManualSection> results = new ArrayList<CFEManualSection>();
		
		//Now that we have the min Postings, use it to find the documents in which 
		//all words of the query exist (implement conjunction).
		for(CFEManualSection section : minPostings) {
			boolean existsInAllSections = true;
			for(HashSet<CFEManualSection> h : queryWordMaps) {
				if(!h.contains(section)) {
					existsInAllSections = false;
					break;
				}
			}
			if(existsInAllSections)
				results.add(section);
		}
	
		return results;
	}
	
	/**
	 * performs unit test of BooleanSearchEngine, applying to this class a simple
	 * query, "president bush sarbanes-oxley".  
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();
		BooleanSearchEngine cfemse = new BooleanSearchEngine(cfeManual);
		String query = "president bush sarbanes-oxley";
		PriorityQueue<CFEManualSection> results = cfemse.queryRetrieve(query, "Law");
		int size = results.size();
		for(int i = 0; i < size; i++) {
			double priority = results.getPriority();
			System.out.println(results.next().name + ": " + priority);
		}
	}
}
