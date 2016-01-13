package financial.fraud.cfe.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import jdk.nashorn.internal.runtime.ParserException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
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
import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

/**
 * ConceptMatchV3NOTA 
 * @author Joe
 *
 */
public class ConceptMatchV3NOTA implements IAlgorithm {

	private String examSectionName;

	private String questionSectionName;

	private int bestMatchOptionsDoc = -1;

	private int premierStemDoc = -1;

	final String CFE_MANUAL_CLASS_NAME = "CFEManualSmallDocUnitRegex";

	public void setExamSectionName(String examSectionName) {
		this.examSectionName = examSectionName;
	}

	public void setQuestionSectionName(String questionSectionName) {
		this.questionSectionName = questionSectionName;
	}

	protected String[] elimPhrases = { "is referred to as", "are referred to as", "which of the following", "?",
			"are known as", "is known as", "are sometimes called", "is called", "would be described as" };

	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		Map<Integer, Integer> optionsDocs = new HashMap<Integer, Integer>();
		IndexSearcher is;
		int bestOption = -1;

		try {
			String indexDir = "lucene index collection" + File.separator + CFE_MANUAL_CLASS_NAME + File.separator
					+ examSectionName + File.separator + questionSectionName;

			Directory dir = FSDirectory.open(new File(indexDir));
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
			System.out.println("optionsDocs: " + optionsDocs + "\n");

			bestOption = getBestDocMatchOption(hits, optionsDocs, is);

			if (bestOption != -1)
				return bestOption;

			System.out.println("Search for options docs based on title field unsuccessful.");
			System.out.println("Repeating search for options docs using contents field...\n");
			// if we made it here, no success finding a matching doc between stem docs and options docs.
			// repeat search for options docs, this time using contents field instead of title.
			optionsDocs = getOptionsDocs(is, question, "contents");

			// print out the map for reasonableness.
			System.out.println("optionsDocs: " + optionsDocs + "\n");

			// repeat search for match of options doc to a stem doc.
			bestOption = getBestDocMatchOption(hits, optionsDocs, is);

			is.close();

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("unable to parse query: " + e.getMessage());
		}

		// search based on stem yielded no docs that matched those returned from
		// the concept searches. so return -1.
		return bestOption;
	}

	private boolean isFirstStemDocPremier(TopDocs hits) {
		if (hits.scoreDocs.length > 0) {
			if (hits.scoreDocs[0].score > 2.5 * hits.scoreDocs[1].score) {
				premierStemDoc = hits.scoreDocs[0].doc;
				return true;
			} else
				return false;
		} else {
			return false;
		}
	}

	private int getBestDocMatchOptionFirstStemDocPremier(TopDocs hits, Map<Integer, Integer> optionsDocs,
			IndexSearcher is, CFEExamQuestion question) throws IOException, ParseException {
		System.out.println("Stem doc is premier: " + is.doc(hits.scoreDocs[0].doc).get("title") + "("
				+ hits.scoreDocs[0] + ")");
		System.out.println("Searching for option whose matching doc is the first stem doc.");
		if (optionsDocs.get(premierStemDoc) != null) {
			int matchingOption = optionsDocs.get(premierStemDoc);
			System.out.println("Search successful for option whose matching doc is first stem doc: "
					+ getFormattedResponse(matchingOption, question));
			return optionsDocs.get(premierStemDoc);
		} else {
			System.out.println("Search for options docs based on title field unsuccessful.");
			System.out.println("Repeating search for options docs using contents field...\n");
			// if we made it here, no success finding a matching doc between stem docs and options docs.
			// repeat search for options docs, this time using contents field instead of title.
			optionsDocs = getOptionsDocs(is, question, "contents");
			if (optionsDocs.get(premierStemDoc) != null) {
				int matchingOption = optionsDocs.get(premierStemDoc);
				System.out.println("Search for option whose matching doc is first stem doc: "
						+ getFormattedResponse(matchingOption, question));
				return optionsDocs.get(premierStemDoc);
			} else {
				System.out.println("Search for option whose matching doc is first stem doc failed");
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
				System.out.println("Options doc with best match: " + is.doc(scoreDoc.doc).get("title") + "("
						+ scoreDoc.doc + ")");

				// store the best match doc's doc id in a class variable
				// so it can be used later...
				bestMatchOptionsDoc = scoreDoc.doc;

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
			System.out.println("Doc results for: " + option);
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

	private TopDocs getStemDocs(IndexSearcher is, CFEExamQuestion question) throws ParseException, IOException {
		String lowerStem = question.stem.toLowerCase();

		// remove elimination phrases (see array initialized above).
		for (String elimPhrase : elimPhrases)
			lowerStem = lowerStem.replace(elimPhrase, "");

		// remove any colon that may be in the stem, :. This is syntax recognized
		// by the QueryParse as part of the query language syntax for lucene. The colon
		// is used to identify a field upon which to base the search. Query string,
		// "title:extreme", indicates that the query is intended as a search for the word
		// extreme in the title field, reference McCandless, Lucene in Action, page 80.
		lowerStem = lowerStem.replace(":", "");

		System.out.println("Stem (lower case): " + lowerStem + "\n");

		String fieldName = "contents";
		String queryString = lowerStem;
		// String queryString =
		// "The worth of a business, if it is any good, will always be higher than the value of its hard assets. This is reflected in the accounting concept of:";
		QueryParser parser = new QueryParser(Version.LUCENE_30, fieldName, new StandardAnalyzer(Version.LUCENE_30));
		Query query = parser.parse(queryString);
		TopDocs hits = is.search(query, 10);

		// print out the doc titles as a reasonableness check.
		System.out.println("Doc results for stem: " + lowerStem);
		printDocTitles(is, hits);

		return hits;
	}

	public static void main(String[] args) {
		String examSectionName;
		String questionSectionName;
		String questionName;

		// Financial Statement Fraud 9.txt
		examSectionName = "Financial Transactions and Fraud Schemes";
		questionSectionName = "Financial Statement Fraud";
		questionName = "Financial Statement Fraud 9.txt";

		CFEExamQuestion question = new CFEExamQuestion("exam questions - all" + File.separator + examSectionName
				+ File.separator + questionSectionName + File.separator + questionName);

		System.out.println(question);

		ConceptMatchV3NOTA cm = new ConceptMatchV3NOTA();
		cm.setExamSectionName(examSectionName);
		cm.setQuestionSectionName(questionSectionName);

		int result = cm.solve(question, null);
		System.out.println("Option selected: " + getFormattedResponse(result, question));
		if (result == question.correctResponse) {
			System.out.println("Correct!");
		} else {
			System.out.println("Incorrect.  Correct answer: " + question.options.get(question.correctResponse));
			System.out.println("Explanation: " + question.explanation);
			System.out.println("Manual page: " + question.getSourcePage());
		}
	}

	public static String getFormattedResponse(int result, CFEExamQuestion question) {
		return String.format("%s%s%s", (char) (result + 97), ") ", question.options.get(result));
	}

	private static void printDocTitles(IndexSearcher is, TopDocs hits) throws IOException {
		ScoreDoc[] matches = hits.scoreDocs;
		if (matches.length == 0)
			System.out.println("** no docs returned **");
		for (int i = 0; i < matches.length; i++) {
			Document doc = is.doc(matches[i].doc);
			System.out
					.println((i + 1) + ". " + doc.get("title") + "(" + matches[i].doc + ")" + "  " + matches[i].score);
		}
		System.out.println();
	}

	private class OptionScore {
		int option;
		double score;

		private OptionScore(int option, double score) {
			this.option = option;
			this.score = score;
		}
	}
}
