
package financial.fraud.cfe.ir;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.manual.CFEManualSection;
import financial.fraud.cfe.util.TokenizerSimple;

// TODO:  Make data structure testing area-specific.
public class TFIDFSearchEngine extends AbstractSearchEngine {
	
	private CounterMap<CFEManualSection, String> tfScores;
	
	private HashMap<String, Double> idfScores;
	
	public TFIDFSearchEngine(CFEManualLargeDocUnit cfeManual) {
		super(cfeManual);
		buildTFScores();
		buildIDFScores();
	}
	
	/**
	 * builds CounterMap storing sections, words, and then normalized count of that word in the section.
	 */
	private void buildTFScores() {
		
		CFEManualSection rootSection = cfeManual.getRoot();
		List<CFEManualSection> sections = rootSection.getSubTreeAsList();
//		List<CFEManualSection> sections = cfeManual.getManualSections();
		
		tfScores = new CounterMap<CFEManualSection, String>();
		
		// for each section, add the log10(tf) to tfScores for each word that exists
		// in the key set of the token freqs data structure for the section.
		for(CFEManualSection section : sections) {
			TokenizerSimple tokenizer = section.getTokenizer();
			Map<String, Integer> tokenFreqs = tokenizer.getTokenTypeFreqs();
			for(String word : tokenFreqs.keySet()) {
				Integer tf = tokenFreqs.get(word);
				if(tf == null) {
					tfScores.setCount(section, word, 0.0);
				} else {
					//tfScores.setCount(section, word, Math.log10(tf)/Math.log10(tokenizer.getTokensCount()));
					tfScores.setCount(section, word, Math.log10(tf)/tokenizer.getTokensCount());
				}
			}
		}
	}

	/**
	 * builds a hash map in which each entry's key is a word and each value is the number of
	 * leaf sections (those with no subsections) in which that word appears.
	 * The concept of leaf section is a mechanism for approximating distinct documents
	 * within the CFE manual.  Although not perfect, it is used as an approximation.
	 * 
	 * @return leaf section count
	 */
	private void buildIDFScores() {
		ArrayList<CFEManualSection> leafSections = new ArrayList<CFEManualSection>();
		
		//add an element for each leaf section.
		CFEManualSection rootSection = cfeManual.getRoot();
		for(CFEManualSection section : rootSection.getSubTreeAsList()) {
			if(section.isLeaf())
				leafSections.add(section);
		}
		
		//record the size of the data structure.
		int totalDocCount = leafSections.size();
		
		idfScores = new HashMap<String, Double>();
		
		for(String word : vocabulary) {
			int docCount = 0;
			for(CFEManualSection section : leafSections) {
				if(section.getTokenizer().getTokenTypeFreqs().keySet().contains(word))
					docCount++;
			}
			//System.out.println("word: " + word + " --> " + docCount);
			if(docCount > 0)
				idfScores.put(word, Math.log10(totalDocCount / docCount));
		}
	}

	private double getTFIDFScore(CFEManualSection section, ArrayList<String> query) {
		double tfidfScore = 0.0;
		for(String word : query) 
			tfidfScore += tfScores.getCount(section, word) * idfScores.get(word);
		return tfidfScore;
	}

	/**
	 * loads a priority queue with sections and their respective scores as determined by the
	 * tf-idf score calculation.  Unlike BooleanSearchEngine, this class makes use of the
	 * testingArea parameter.
	 * 
	 */
	@Override
	public PriorityQueue<CFEManualSection> queryRetrieve(String query, String testingArea) {
		ArrayList<String> queryList = tokenizeQuery(query);
		
		PriorityQueue<CFEManualSection> pq = new PriorityQueue<CFEManualSection>();
		
		CFEManualSection section = cfeManual.getManualSectionForQuestionSection(testingArea);
		for(CFEManualSection descendant : section.getSubTreeAsList()) {
//		for(CFEManualSection section : cfeManual.getManualSections(testingArea)) {
			pq.add(section, getTFIDFScore(descendant, queryList));
//			pq.add(section, getTFIDFScore(section, queryList));
		}
		
		return retrieveTopResults(pq, 10);
	}
	
	/**
	 * performs unit test of TFIDFSearchEngine, executing it for the query, "president bush sarbanes-oxley".
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();
		TFIDFSearchEngine cfemse = new TFIDFSearchEngine(cfeManual);
		String query = "president bush sarbanes-oxley";
		String testingArea = "Law";
		PriorityQueue<CFEManualSection> results = cfemse.queryRetrieve(query, testingArea);
		int size = results.size();
		for(int i = 0; i < size; i++) {
			double priority = results.getPriority();
			System.out.println(results.next().name + ": " + priority);
		}
	}
}
