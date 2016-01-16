package financial.fraud.cfe.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
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
import financial.fraud.cfe.util.FeatureType;

/**
 * ConceptMatchV3NOTA leverages the logic in ConcepMatchV3 for concept matching, and extends it for addressing the case
 * in which one of the options (typically the last option) is "none of the above".
 * 
 * Here, because the none of the above option is so infrequently the correct answer (<10% based on success rate data for
 * the none-of-the-above algorithm), we simply eliminate the none-of-the-above option and consider only the remaining 3
 * options as possible answers.
 * 
 * @author Joe
 *
 */
public class ConceptMatchV3NOTA extends AbstractConceptMatch {

	private int premierStemDoc = -1;

	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		Map<Integer, Integer> optionsDocs = new HashMap<Integer, Integer>();
		IndexSearcher is;
		int bestOption = -1;

		try {
			setIndexDirectory(question);
			Directory dir = FSDirectory.open(new File(indexDirectory));
			is = new IndexSearcher(dir);

			// get the docs that return from a search on question stem.
			TopDocs hits = getStemDocs(is, question);

			// get the map of docs that return from a search in doc collection on each of the
			// options for the question, based on the field, title.
			optionsDocs = getOptionsDocs(is, question, "title");

			// if first stem doc dominates all others, assume it's the correct doc and that we must find
			// option for which a matching doc is the first stem doc.
			if (isFirstStemDocPremier(hits))
				return getBestDocMatchOptionFirstStemDocPremier(hits, optionsDocs, is, question);

			// print out the map for reasonableness.
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "optionsDocs: " + optionsDocs + "\n");

			bestOption = getBestDocMatchOption(hits, optionsDocs, is);

			if (bestOption != -1)
				return bestOption;

			Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
					"Search for options docs based on title field unsuccessful.");
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
					"Repeating search for options docs using contents field...\n");
			// if we made it here, no success finding a matching doc between stem docs and options docs.
			// repeat search for options docs, this time using contents field instead of title.
			optionsDocs = getOptionsDocs(is, question, "contents");

			// print out the map for reasonableness.
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "optionsDocs: " + optionsDocs + "\n");

			// repeat search for match of options doc to a stem doc.
			bestOption = getBestDocMatchOption(hits, optionsDocs, is);

			is.close();

		} catch (IOException e) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", e.getMessage());
		} catch (ParseException e) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "unable to parse query: " + e.getMessage());
		}

		// search based on stem yielded no docs that matched those returned from
		// the concept searches. so return -1.
		return bestOption;
	}

	private boolean isFirstStemDocPremier(TopDocs hits) {
		// case: number of hits = 0.
		if (hits.scoreDocs.length == 0)
			return false;
		
		// case: where number of hits = 1.
		if(hits.scoreDocs.length == 1) {
			premierStemDoc = hits.scoreDocs[0].doc;
			return true;
		}
		
		// case: number of hits > 1.
		if (hits.scoreDocs[0].score > 2.5 * hits.scoreDocs[1].score) {
			premierStemDoc = hits.scoreDocs[0].doc;
			return true;
		} else
			return false;
	}

	private int getBestDocMatchOptionFirstStemDocPremier(TopDocs hits, Map<Integer, Integer> optionsDocs,
			IndexSearcher is, CFEExamQuestion question) throws IOException, ParseException {
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
				"Stem doc is premier: " + is.doc(hits.scoreDocs[0].doc).get("title") + "(" + hits.scoreDocs[0] + ")");
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
				"Searching for option whose matching doc is the first stem doc.");
		if (optionsDocs.get(premierStemDoc) != null) {
			int matchingOption = optionsDocs.get(premierStemDoc);
			Logger.getInstance().printf(
					DetailLevel.FULL,
					"%s\n",
					"Search successful for option whose matching doc is first stem doc: "
							+ (char) (matchingOption + 97), ") ", question.options.get(matchingOption));
			return optionsDocs.get(premierStemDoc);
		} else {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
					"Search for options docs based on title field unsuccessful.");
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
					"Repeating search for options docs using contents field...\n");
			// if we made it here, no success finding a matching doc between stem docs and options docs.
			// repeat search for options docs, this time using contents field instead of title.
			optionsDocs = getOptionsDocs(is, question, "contents");
			if (optionsDocs.get(premierStemDoc) != null) {
				int matchingOption = optionsDocs.get(premierStemDoc);
				Logger.getInstance().printf(
						DetailLevel.FULL,
						"%s\n",
						"Search successful for option whose matching doc is first stem doc: "
								+ (char) (matchingOption + 97), ") ", question.options.get(matchingOption));
				return optionsDocs.get(premierStemDoc);
			} else {
				Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
						"Search for option whose matching doc is first stem doc failed");
				return -1;
			}
		}
	}

	/**
	 * returns the option whose for which a document returned from searching on that option appears highest in the list
	 * of documents returned from searching on the stem.
	 * 
	 * This method traverses the list of documents returned from a search on the question stem, (recall, the list of
	 * returned docs is in order of decreasing match score). As it goes down the list, it looks in the optionsDocs map
	 * for a matching document, i.e., a document also returned from a search on an option. This algorithm then returns
	 * that corresponding option.
	 * 
	 * @param hits
	 * @param optionsDocs
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private int getBestDocMatchOption(TopDocs hits, Map<Integer, Integer> optionsDocs, IndexSearcher is)
			throws IOException {
		// traverse the stem docs returned from the search on question stem,
		// starting from the beginning, in order of decreasing
		// match score. Return the option corresponding to the first
		// doc in this set that is contained in the options docs map.
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			// return the highest scoring doc which exists in our options
			// search results.
			if (optionsDocs.containsKey(scoreDoc.doc)) {
				Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
						"Options doc with best match: " + is.doc(scoreDoc.doc).get("title") + "(" + scoreDoc.doc + ")");

				return optionsDocs.get(scoreDoc.doc);
			}
		}

		// if we made it here, then there was no doc in the stem search results that is also
		// contained in the options docs. So, return -1.
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
		List<String> options = listCopy(question.options);

		if (question.getProfile().featureExists(FeatureType.NONE_OF_THE_ABOVE)) {
			Logger.getInstance()
					.printf(DetailLevel.FULL, "%s\n",
							"This is an ALL-OF-THE-ABOVE question.  Eliminating the All-OF-THE-ABOVE option for this algorithm... \n");
			options.remove(3);
		}

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
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Doc results for: " + option);
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

	private List<String> listCopy(List<String> list) {
		LinkedList<String> list2 = new LinkedList<String>();
		for (String item : list)
			list2.add(item);
		return list2;
	}


	public static void main(String[] args) {
		String examSectionName;
		String questionSectionName;
		String questionName;


		// Theft of Intellectual Property 5.txt
		examSectionName = "Financial Transactions and Fraud Schemes";
		questionSectionName = "Theft of Intellectual Property";
		questionName = "Theft of Intellectual Property 5.txt";

		Logger.getInstance().setDetailLevel(DetailLevel.FULL);

		CFEExamQuestion question = new CFEExamQuestion("exam questions - all" + File.separator + examSectionName
				+ File.separator + questionSectionName + File.separator + questionName);

		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", question);

		ConceptMatchV3NOTA cm = new ConceptMatchV3NOTA();

		int result = cm.solve(question, null);
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
				"Option selected: " + (char) (result + 97) + ") " + question.options.get(result));
		if (result == question.correctResponse) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Correct!");
		} else {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
					"Incorrect.  Correct answer: " + question.options.get(question.correctResponse));
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Explanation: " + question.explanation);
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Manual page: " + question.getSourcePage());
		}
	}

}
