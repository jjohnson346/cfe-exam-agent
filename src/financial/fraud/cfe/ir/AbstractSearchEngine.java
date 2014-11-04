package financial.fraud.cfe.ir;

import java.util.ArrayList;
import java.util.Set;

import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.manual.CFEManualSection;
import financial.fraud.cfe.util.PorterStemmer;
import financial.fraud.cfe.util.TokenizerSimple;

/**
 * provides an implementation of common functionality required by many (if not all) child search engine
 * classes (e.g., BooleanSearchEngine, TFIDFSearchEngine).
 * 
 * @author jjohnson346
 *
 */
public abstract class AbstractSearchEngine implements SearchEngine {

	protected CFEManual cfeManual;
	
	/**
	 * the set of words over which the language of the cfe manual is defined.
	 * This is found by retrieving the key set of the hash map of token freqs
	 * for the words of the text for the root node of the cfe manual.
	 */
	protected Set<String> vocabulary;
	
	protected AbstractSearchEngine(CFEManual cfeManual) {
		this.cfeManual = cfeManual;
		
		// set the vocabulary to be the set of tokens comprised in the key set in the 
		// hash map of token freqs.
		vocabulary = cfeManual.getRoot().getTokenizer().getTokenTypeFreqs().keySet();
	}

	/**
	 * tokenizes the query string, returning an array list of string (tokens).
	 * Uses the Porter Stemmer to stem the words of the query before returning the list of
	 * query tokens.
	 * 
	 * @param query						the string containing the words of the query
	 * @return array list of tokens		list of stemmed tokens from words of query
	 */
	protected ArrayList<String> tokenizeQuery(String query) {
		TokenizerSimple ts = new TokenizerSimple();
		ts.tokenize(query);
		ArrayList<String> queryList = (ArrayList<String>)ts.getTokens();
		PorterStemmer ps = new PorterStemmer();
		for(int i = 0; i < queryList.size(); i++) {
			queryList.set(i, ps.stem(queryList.get(i)));
		}
		//System.out.println(queryList);
		return queryList;
	}

	
	
	/**
	 * returns the top k manual sections in a priority queue of manual sections.  Used to filter
	 * out only the top results of the query retrieval that returned the initial priority queue
	 * of manual sections.
	 * 
	 * @param results		the priority queue of manual sections, returned by a prior call to queryRetrieve()
	 * @param size			the number of manual sections to select from the original priority queue.
	 * @return				a priority queue whose size is limited by the lesser of the size parm and the size of the orig priority queue.
	 */
	protected PriorityQueue<CFEManualSection> retrieveTopResults(PriorityQueue<CFEManualSection> results, int size) {
		PriorityQueue<CFEManualSection> topResults = new PriorityQueue<CFEManualSection>();
		
		// make sure that size is no larger than the size of the result priority queue.
		if(results.size() < size)
			size = results.size();
		
		// return only the top <size> results, where <size> is the min of input arg and
		// size of result priority queue.
		for(int i = 0; i < size; i++) {
			double priority = results.getPriority();
			topResults.add(results.next(), priority);
		}
		return topResults;
	}
	
}
