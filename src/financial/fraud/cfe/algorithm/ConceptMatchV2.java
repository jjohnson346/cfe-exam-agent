package financial.fraud.cfe.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;
import financial.fraud.cfe.manual.CFEManual;

/**
 * ConceptMatch2 is just like ConceptMatch except it compares the scores for each concept document, i.e., each document
 * that is retrieved from the search query based on each option, before tabulating them in a hash table against which
 * the document retrieved from the search on question stem are compared. For each concept doc, D, with score, S, with
 * respect to option O, if there is already an entry in the hash table for which the there is key/value pair, D=(O',S'),
 * where O' is a different question option and S' is the score for D with respect to O', then scores S and S' are
 * compared, and the option whose score is maximum is chosen. If S' > S, then the entry with D = (O',S') is left as is
 * in the hash table. On the other hand, if S' < S, then the entry is overwritten with D = (O,S).
 * 
 * In addition to the enhancement described above, ConceptMatch2 includes logic for handling the scenario in which
 * the options docs include *no documents* that are in the stem docs, i.e., these two document sets are disjoint.  
 * (In ConceptMatchV1, there was no logic to address this issue.)  In ConceptMatchV2, if this scenario occurs, 
 * the options docs set is rebuilt, but instead of searching on the title field, the search is performed on the contents
 * field, likely yielding more hits, and thus, a greater likelihood of docs that also occur in the stem docs set.
 *
 * AlgorithmConceptMatch is an algorithm that uses the words in the stem in order to determine a detail section upon
 * which to base a max word count algorithm for selecting the correct option. First, it gets the words of the stem, then
 * it determines the likely detail section given these words from which the question was created, then it counts the
 * instances of each option phrase within this detail section and returns the option with the max count. There is no
 * incorporation of logic for all of the above and none of the above.
 * 
 * Notes for reporting:
 * IMPORTANT EXAM QUESTIONS FOR TRACING THE DEVELOPMENT OF THIS ALGORITHM:
 * 
 * Exam Question: Bankuptcy Fraud 1 - this question serves as an example of how the rudimentary algorithm, where 
 * we find matching docs for options, optionsDocs, and we match those up with those found for the stem, stemDocs.
 * A search based on the field, "title", for options is sufficient to find the correct answer using this matching 
 * approach.
 * 
 * Exam Question: Bribery and Corruption 17 - this question serves as an example where there were multiple options with 
 * one or more matching documents in common.  The algorithm requires that each document be associated with at most one
 * option.  Which option to pick, then?  Lacking any logic to handle this, the way the algorithm is implemented is such
 * that an option further down in the question option list will overwrite an option higher up in the option list (when
 * doc id/option pairs are being added to the optionsDocs map).  Instead, we insert logic such that the option for which
 * the match score is highest is assigned to the document.
 * 
 * 
 * Exam Question:  Financial Statement Fraud 9 - this question serves as an example where there were no docs returned for 
 * any of the options because none fit closely with the title of a document.  Thus, when building the optionsDocs
 * map based on a search based on the "title" field, no option was selected (-1 was returned).  This prompted the incorporation
 * of a search based on the "contents" field as a follow-up measure in the case of such a circumstance.
 * 
 * @author Joe
 *
 */
public class ConceptMatchV2 extends AbstractConceptMatch {

	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		Map<Integer, Integer> optionsDocs = new HashMap<Integer, Integer>();
		IndexSearcher is;
		int bestOption = -1;
		
		try {
			setIndexDirectory(question);

			Directory dir = FSDirectory.open(new File(indexDirectory));
			is = new IndexSearcher(dir);

			// get the map of docs that return from a search in doc collection on each of the
			// options for the question, based on the field, title.
			optionsDocs = getOptionsDocs(is, question, "title");

			// print out the map for reasonableness.
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n","optionsDocs: " + optionsDocs + "\n");

			// get the docs that return from a search on question stem.
			TopDocs hits = getStemDocs(is, question);
			
			bestOption = getBestDocMatchOption(hits, optionsDocs, is);
			
			if(bestOption != -1)
				return bestOption;
			
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n","Search for options docs based on title field unsuccessful.");
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n","Repeating search for options docs using contents field...\n");
			// if we made it here, no success finding a matching doc between stem docs and options docs.
			// repeat search for options docs, this time using contents field instead of title.
			optionsDocs = getOptionsDocs(is, question, "contents");
			
			// print out the map for reasonableness.
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n","optionsDocs: " + optionsDocs + "\n");
			
			// repeat search for match of options doc to a stem doc.
			bestOption = getBestDocMatchOption(hits, optionsDocs, is);
			

			is.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n","unable to parse query: " + e.getMessage());
		}

		// search based on stem yielded no docs that matched those returned from
		// the concept searches. so return -1.
		return bestOption;
	}
	
	
	/**
	 * returns the option whose for which a document returned from searching on that option appears
	 * highest in the list of documents returned from searching on the stem.  
	 * 
	 * This method traverses the list of documents returned from a search on the question stem, (recall,
	 * the list of returned docs is in order of decreasing match score).  As it goes down the list, it 
	 * looks in the optionsDocs map for a matching document, i.e., a document also returned from a 
	 * search on an option.  This algorithm then returns that corresponding option.
	 * 
	 * @param hits
	 * @param optionsDocs
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private int getBestDocMatchOption(TopDocs hits, Map<Integer,Integer> optionsDocs, IndexSearcher is) throws IOException {
		// traverse the stem docs returned from the search on question stem,
		// starting from the beginning, in order of decreasing
		// match score. Return the option corresponding to the first
		// doc in this set that is contained in the options docs map.
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			// return the highest scoring doc which exists in our options
			// search results.
			if (optionsDocs.containsKey(scoreDoc.doc)) {
				Logger.getInstance().printf(DetailLevel.FULL, "%s\n","Options doc with best match: " + is.doc(scoreDoc.doc).get("title") + "("
						+ scoreDoc.doc + ")");
				return optionsDocs.get(scoreDoc.doc);
			}
		}
		
		// if we made it here, then there was no doc in the stem search results that is also
		// contained in the options docs.  So, return -1.
		return -1;
		
	}

	/**
	 * returns a map containing the collection of entries with key = docID and value = option where the docID is for a
	 * document found to be a search result document from searching on the string for the corresponding option.
	 * 
	 * @param is
	 *            the IndexSearcher to use for the search on each option string
	 * @param question
	 *            the question whose options are to be searched against
	 * @param fileName
	 *            the name of the field upon which to apply the query for each document, usually "title" or "contents"
	 * 
	 * @return the map containing the key/value: docID/option entries
	 * @throws ParseException
	 * @throws IOException
	 */
	private Map<Integer, Integer> getOptionsDocs(IndexSearcher is, CFEExamQuestion question, String fieldName)
			throws ParseException, IOException {
		// Map<Integer, Integer> conceptDocs = new HashMap<Integer, Integer>();
		Map<Integer, OptionScore> docOptionScores = new HashMap<Integer, OptionScore>();

		// do a search on each of the options, add the docs from each
		// search to a map against which we can compare the results from
		// a search on stem, later.
		List<String> options = question.options;
		for (int i = 0; i < options.size(); i++) {
			String option = options.get(i);

			// String fieldName = "title";
			String queryString = option;
			QueryParser parser = new QueryParser(Version.LUCENE_30, fieldName, new StandardAnalyzer(Version.LUCENE_30));
			Query query = parser.parse(queryString);
			TopDocs hits = is.search(query, 10);

			// load the docs returned for the option-based query search
			// into the hash map. If a doc is already allocated to a different option,
			// pick the option for which the doc score is highest.
			for (ScoreDoc scoreDoc : hits.scoreDocs)

				// check whether doc is already in the hash map. If so, compare
				// the associated score of the associated option with that of the
				// current option. Pick the option whose score is higher.
				if (docOptionScores.get(scoreDoc.doc) != null) {
					OptionScore os = docOptionScores.get(scoreDoc.doc);
					if (scoreDoc.score > os.score)
						docOptionScores.put(scoreDoc.doc, new OptionScore(i, scoreDoc.score));
				} else {
					docOptionScores.put(scoreDoc.doc, new OptionScore(i, scoreDoc.score));
				}
			// print out the doc titles as a reasonableness check.
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n","Doc results for: " + option);
			printDocTitles(is, hits);
		}

		// load the contents of the docOptionScores hash map into another map, optionDocs, of
		// a more straight forward nature, one whose entries are <DocID, Option>, where the option
		// value is the one whose score for its associated docid is highest.
		Map<Integer, Integer> optionDocs = new HashMap<Integer, Integer>();
		for (Entry<Integer, OptionScore> entry : docOptionScores.entrySet()) {
			optionDocs.put(entry.getKey(), entry.getValue().option);
		}

		return optionDocs;
	}


	public static void main(String[] args) {
		String examSectionName;
		String questionSectionName;
		String questionName;

		// Financial Statement Fraud 9.txt
		examSectionName = "Financial Transactions and Fraud Schemes";
		questionSectionName = "Financial Statement Fraud";
		questionName = "Financial Statement Fraud 9.txt";

		// Bribery and Corruption 17.txt - INCORRECT - t
		// examSectionName = "Financial Transactions and Fraud Schemes";
		// questionSectionName = "Bribery and Corruption";
		// questionName = "Bribery and Corruption 17.txt";

		Logger.getInstance().setDetailLevel(DetailLevel.FULL);

		CFEExamQuestion question = new CFEExamQuestion("exam questions - all" + File.separator + examSectionName
				+ File.separator + questionSectionName + File.separator + questionName);

		Logger.getInstance().printf(DetailLevel.FULL, "%s\n",question);

		ConceptMatchV2 cm = new ConceptMatchV2();

		int result = cm.solve(question, null);
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n","Option selected: " + (char) (result + 97) + ") " + question.options.get(result));
		if (result == question.correctResponse) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n","Correct!");
		} else {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n","Incorrect.  Correct answer: " + question.options.get(question.correctResponse));
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n","Explanation: " + question.explanation);
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n","Manual page: " + question.getSourcePage());
		}
	}

	@Override
	public String toString() {
		return "Composite Match V2";
	}

}
