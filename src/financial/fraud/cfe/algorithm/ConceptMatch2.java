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
 * ConceptMatch2 is just like ConceptMatch except it compares the scores for each concept document, i.e., each document
 * that is retrieved from the search query based on each option, before tabulating them in a hash table against which
 * the document retrieved from the search on question stem are compared. For each concept doc, D, with score, S, with
 * respect to option O, if there is already an entry in the hash table for which the there is key/value pair, D=(O',S'),
 * where O' is a different question option and S' is the score for D with respect to O', then scores S and S' are
 * compared, and the option whose score is maximum is chosen. If S' > S, then the entry with D = (O',S') is left as is
 * in the hash table. On the other hand, if S' < S, then the entry is overwritten with D = (O,S).
 *
 * AlgorithmConceptMatch is an algorithm that uses the words in the stem in order to determine a detail section upon
 * which to base a max word count algorithm for selecting the correct option. First, it gets the words of the stem, then
 * it determines the likely detail section given these words from which the question was created, then it counts the
 * instances of each option phrase within this detail section and returns the option with the max count. There is no
 * incorporation of logic for all of the above and none of the above.
 * 
 * @author Joe
 *
 */
public class ConceptMatch2 implements IAlgorithm {

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
					System.out.println("Selected option based on document: " + is.doc(scoreDoc.doc).get("title") + "("
							+ scoreDoc.doc + ")");
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
		// Map<Integer, Integer> conceptDocs = new HashMap<Integer, Integer>();
		Map<Integer, OptionScore> docOptionScores = new HashMap<Integer, OptionScore>();

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

		// Bribery and Corruption 17.txt - correct!
		// examSectionName = "Financial Transactions and Fraud Schemes";
		// questionSectionName = "Bribery and Corruption";
		// questionName = "Bribery and Corruption 17.txt";

		// Consumer Fraud 29.txt - correct!
		// examSectionName = "Financial Transactions and Fraud Schemes";
		// questionSectionName = "Consumer Fraud";
		// questionName = "Consumer Fraud 29.txt";

		// Consumer Fraud 6.txt - correct!
		// examSectionName = "Financial Transactions and Fraud Schemes";
		// questionSectionName = "Consumer Fraud";
		// questionName = "Consumer Fraud 6.txt";
		
		// Contract and Procurement Fraud 14.txt - correct!
		// examSectionName = "Financial Transactions and Fraud Schemes";
		// questionSectionName = "Contract and Procurement Fraud";
		// questionName = "Contract and Procurement Fraud 14.txt";

		// Contract and Procurement Fraud 3.txt - correct!
		// examSectionName = "Financial Transactions and Fraud Schemes";
		// questionSectionName = "Contract and Procurement Fraud";
		// questionName = "Contract and Procurement Fraud 3.txt";

		// Financial Statement Fraud 9.txt
		examSectionName = "Financial Transactions and Fraud Schemes";
		questionSectionName = "Financial Statement Fraud";
		questionName = "Financial Statement Fraud 9.txt";		

		CFEExamQuestion question = new CFEExamQuestion("exam questions - all" + File.separator + examSectionName
				+ File.separator + questionSectionName + File.separator + questionName);

		System.out.println(question);

		ConceptMatch2 cm = new ConceptMatch2();
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
			System.out.println((i + 1) + ". " + doc.get("title") + "(" + matches[i].doc + ")");
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
