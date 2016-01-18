package financial.fraud.cfe.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * AlgorithmConceptMatch is an algorithm that uses the words in the stem in order to determine a detail section upon
 * which to base a max word count algorithm for selecting the correct option. First, it gets the words of the stem, then
 * it determines the likely detail section given these words from which the question was created, then it counts the
 * instances of each option phrase within this detail section and returns the option with the max count. There is no
 * incorporation of logic for all of the above and none of the above.
 * 
 * @author Joe
 *
 */
public class ConceptMatchV1 extends AbstractConceptMatch {

	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		Map<Integer, Integer> conceptDocs = new HashMap<Integer, Integer>();
		IndexSearcher is;
		try {

			setIndexDirectory(question);
			Directory dir = FSDirectory.open(new File(indexDirectory));
			is = new IndexSearcher(dir);

			// get the map of docs that return from a search on each of the
			// options for the question.
			conceptDocs = getConceptDocs(is, question);

			// print out the map for reasonableness.
			Logger.getInstance().printf(DetailLevel.FULL, "%s%s\n","conceptDocs: ",conceptDocs);

			// get the docs that return from a search on question stem.
			TopDocs hits = getStemDocs(is, question);

			// traverse the stem docs returned from the search on question stem,
			// starting from the beginning, in order of decreasing
			// match score. Return the option corresponding to the first
			// doc in this set that is contained in the concept docs map.
			for (ScoreDoc scoreDoc : hits.scoreDocs) {
				// return the highest scoring doc which exists in our options
				// search results.
				if (conceptDocs.containsKey(scoreDoc.doc)) {
					return conceptDocs.get(scoreDoc.doc);
				}
			}
			is.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s%s\n","unable to parse query: ", e.getMessage());
		}

		// search based on stem yielded no docs that matched those returned from
		// the concept searches. so return -1.
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
	 * @return the map containing the key/value: docID/option entries
	 * @throws ParseException
	 * @throws IOException
	 */
	private Map<Integer, Integer> getConceptDocs(IndexSearcher is, CFEExamQuestion question) throws ParseException,
			IOException {
		Map<Integer, Integer> conceptDocs = new HashMap<Integer, Integer>();

		// do a search on each of the options, add the docs from each
		// search to a map against which we can compare the results from
		// a search on stem, later.
		List<String> options = question.options;
		for (int i = 0; i < options.size(); i++) {
			String option = options.get(i);

			String fieldName = "title";
			String queryString = option;
			QueryParser parser = new QueryParser(Version.LUCENE_30, fieldName, new StandardAnalyzer(Version.LUCENE_30));
			Query query = parser.parse(queryString);
			TopDocs hits = is.search(query, 10);

			for (ScoreDoc scoreDoc : hits.scoreDocs)
				conceptDocs.put(scoreDoc.doc, i);

			// print out the doc titles as a reasonableness check.
			Logger.getInstance().printf(DetailLevel.FULL, "%s%s\n","Doc results for: ", option);
			printDocTitles(is, hits);
		}
		return conceptDocs;
	}


	public static void main(String[] args) {
		String examSectionName;
		String questionSectionName;
		String questionName;


		// Bribery and Corruption 17.txt - INCORRECT - t
		examSectionName = "Financial Transactions and Fraud Schemes";
		questionSectionName = "Bribery and Corruption";
		questionName = "Bribery and Corruption 17.txt";
		
		Logger.getInstance().setDetailLevel(DetailLevel.FULL);

		CFEExamQuestion question = new CFEExamQuestion("exam questions - all" + File.separator + examSectionName
				+ File.separator + questionSectionName + File.separator + questionName);

		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", question);

		ConceptMatchV1 cm = new ConceptMatchV1();

		int result = cm.solve(question, null);
		Logger.getInstance().printf(DetailLevel.FULL, "%s%s\n", "option selected: ", result);
		if (result == question.correctResponse)
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n","Correct!");
		else
			Logger.getInstance().printf(DetailLevel.FULL, "%s%s\n","Incorrect.  Correct answer: ", question.options.get(question.correctResponse));
	}

	@Override
	public String toString() {
		return "Concept Match V1";
	}

}
