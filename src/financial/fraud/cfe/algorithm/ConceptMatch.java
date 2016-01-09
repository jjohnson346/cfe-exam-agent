package financial.fraud.cfe.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
 * AlgorithmConceptMatch is an algorithm that uses the words in the stem in order to determine a detail section upon
 * which to base a max word count algorithm for selecting the correct option. First, it gets the words of the stem, then
 * it determines the likely detail section given these words from which the question was created, then it counts the
 * instances of each option phrase within this detail section and returns the option with the max count. There is no
 * incorporation of logic for all of the above and none of the above.
 * 
 * @author Joe
 *
 */
public class ConceptMatch implements IAlgorithm {

	private String examSectionName;

	private String questionSectionName;

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
		Map<Integer, Integer> conceptDocs = new HashMap<Integer, Integer>();
		IndexSearcher is;
		try {
			// retrieve the IndexSearcher object for the docs for the section
			// that correspond to the question.
			// final String CFE_MANUAL_CLASS_NAME = "CFEManualSmallDocUnitRegex";

			// for Question: Bankruptcy Fraud 1
			// final String EXAM_SECTION_NAME = "Financial Transactions and Fraud Schemes";
			// final String QUESTION_SECTION_NAME = "Bankruptcy Fraud";

			// for Question: Basic Accounting Concepts 2, 12
			// final String EXAM_SECTION_NAME = "Financial Transactions and Fraud Schemes";
			// final String QUESTION_SECTION_NAME = "Basic Accounting Concepts";

			// for Question: Bribery and Corruption 17
			// final String EXAM_SECTION_NAME = "Financial Transactions and Fraud Schemes";
			// final String QUESTION_SECTION_NAME = "Bribery and Corruption";

			// final String INDEX_DIR = "lucene index collection" + File.separator + CFE_MANUAL_CLASS_NAME
			// + File.separator + EXAM_SECTION_NAME + File.separator + QUESTION_SECTION_NAME;

			String indexDir = "lucene index collection" + File.separator + CFE_MANUAL_CLASS_NAME + File.separator
					+ examSectionName + File.separator + questionSectionName;

			Directory dir = FSDirectory.open(new File(indexDir));
			is = new IndexSearcher(dir);

			// get the map of docs that return from a search on each of the
			// options for the question.
			conceptDocs = getConceptDocs(is, question);

			// print out the map for reasonableness.
			System.out.println("conceptDocs: " + conceptDocs + "\n");

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
			System.out.println("unable to parse query: " + e.getMessage());
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
			System.out.println("Doc results for: " + option);
			printDocTitles(is, hits);
		}
		return conceptDocs;
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

		// Brankruptcy Fraud 1.txt - correct!
		// examSectionName = "Financial Transactions and Fraud Schemes";
		// questionSectionName = "Bankruptcy Fraud";
		// questionName = "Bankruptcy Fraud 1.txt";

		// Basic Accounting Concepts 12.txt - correct!
		// examSectionName = "Financial Transactions and Fraud Schemes";
		// questionSectionName = "Basic Accounting Concepts";
		// questionName = "Basic Accounting Concepts 12.txt";

		// Basic Accounting Concepts 2.txt - correct!
		// examSectionName = "Financial Transactions and Fraud Schemes";
		// questionSectionName = "Basic Accounting Concepts";
		// questionName = "Basic Accounting Concepts 2.txt";

		// Bribery and Corruption 17.txt - INCORRECT - t
		examSectionName = "Financial Transactions and Fraud Schemes";
		questionSectionName = "Bribery and Corruption";
		questionName = "Bribery and Corruption 17.txt";

		CFEExamQuestion question = new CFEExamQuestion("exam questions - all" + File.separator + examSectionName
				+ File.separator + questionSectionName + File.separator + questionName);

		System.out.println(question);

		ConceptMatch cm = new ConceptMatch();
		cm.setExamSectionName(examSectionName);
		cm.setQuestionSectionName(questionSectionName);

		int result = cm.solve(question, null);
		System.out.println("option selected: " + result);
		if (result == question.correctResponse)
			System.out.println("Correct!");
		else
			System.out.println("Incorrect.  Correct answer: " + question.options.get(question.correctResponse));
	}

	private static void printDocTitles(IndexSearcher is, TopDocs hits) throws IOException {
		ScoreDoc[] matches = hits.scoreDocs;
		if (matches.length == 0)
			System.out.println("** no docs returned **");
		for (int i = 0; i < matches.length; i++) {
			Document doc = is.doc(matches[i].doc);
			System.out.println((i + 1) + ". " + doc.get("title"));
		}
		System.out.println();
	}
}
