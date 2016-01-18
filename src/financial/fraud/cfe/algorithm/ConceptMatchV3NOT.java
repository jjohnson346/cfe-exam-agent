package financial.fraud.cfe.algorithm;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
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
 * ConceptMatchV3NOT extends the logic of ConceptMatchV3, but turns it on its head to handle questions of the type,
 * "Which of the following is NOT ....", where for 1 of the 4 options, the phrase indicated by the ellipsis is 
 * not true, while for the other 3 it *is* true.
 * 
 * This algorithm, instead of looking for the option for which there's the greatest affinity between options doc 
 * and stem docs, attempts to find the option with the least affinity between the options doc and stem docs.
 * 
 * 
 * 
 * 
 * 
 * 
 * 
 * Notes for reporting: IMPORTANT EXAM QUESTIONS FOR TRACING THE DEVELOPMENT OF THIS ALGORITHM:
 * 
 * 
 * @author Joe
 *
 */
public class ConceptMatchV3NOT extends AbstractConceptMatch {

	private int premierStemDoc = -1;

	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		IndexSearcher is;

		try {
			setIndexDirectory(question);

			Directory dir = FSDirectory.open(new File(indexDirectory));
			is = new IndexSearcher(dir);

			// get the docs that return from a search on question stem.
			TopDocs hits = getStemDocs(is, question);

			// get the map of docs that return from a search in doc collection on each of the
			// options for the question, based on the field, title.
			Map<Integer, OptionScore> docOptionScores = getDocOptionScores(is, question, "title");

			// print out the map for reasonableness.
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "docOptionScores: " + docOptionScores + "\n");

			is.close();

			return getLeastDocMatchOption(hits, docOptionScores, is);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "unable to parse query: " + e.getMessage());
		}

		// search based on stem yielded no docs that matched those returned from
		// the concept searches. so return -1.
		return -1;
	}
	
	private int getLeastDocMatchOption(TopDocs hits, Map<Integer, OptionScore> docOptionScores, IndexSearcher is) {
		// contains a map of option/doc-score key/value pairs
		HashMap<Integer, ScoreDoc> optionScoreDocs = new HashMap<Integer, ScoreDoc>();
		
		for(ScoreDoc scoreDoc : hits.scoreDocs) {
			if(docOptionScores.containsKey(scoreDoc.doc)) {
				int currOption = docOptionScores.get(scoreDoc.doc).option;
				if(!optionScoreDocs.containsKey(currOption))
					optionScoreDocs.put(currOption, scoreDoc);
			}
		}
		
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "optionScoreDocs: " + optionScoreDocs + "\n");
		
		
		// if all options represented in optionScoreDocs, pick the one with lowest score.
		if(optionScoreDocs.size() == 4) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "All options present in optionScoreDocs.  Picking the one with the lowest score.\n");
			double minScore = 0.0;
			int minOption = -1;
			for(Entry<Integer, ScoreDoc> e : optionScoreDocs.entrySet()) {
				double score = e.getValue().score;
				if(score < minScore) {
					minScore = score;
					minOption = e.getKey();
				}
			}

			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Lowest score option: " + minOption + "\n");

			return minOption;
		}
		
		// if we made it here, at least one option is not in optionScoreDocs.  Pick one that
		// is not present.
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Not all options present in optionScoreDocs.  Picking a missing option.\n");
		
		// make a set consisting of all 4 possible options.
		HashSet<Integer> optionChoices = new HashSet<Integer>();
		optionChoices.add(0);
		optionChoices.add(1);
		optionChoices.add(2);
		optionChoices.add(3);
		
		// remove from set those that exist in the optionScoreDocs hash map.
		for(int option : optionScoreDocs.keySet()) {
			optionChoices.remove(option);
		}
		
		// since there's at least one option missing from optionScoreDocs, there's at least 
		// one element left in optionChoices.  Pick the first one.
		
		Integer[] list = new Integer[(optionChoices.size())];
		list = optionChoices.toArray(list);
		int missingOption = list[0];
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "missing option selected: " + missingOption + "\n");

		return missingOption;
		
	}
	

	/**
	 * returns a map containing the collection of entries with key = docID and value = OptionScore object
	 * where the docID is for a
	 * document found to be a search result document from searching on the string for the corresponding option, 
	 * and the OptionScore object is an encapsulation of the option for which that doc was found and the score
	 * is the score for that doc from the search based on the option.
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
	private Map<Integer, OptionScore> getDocOptionScores(IndexSearcher is, CFEExamQuestion question, String fieldName)
			throws ParseException, IOException {
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
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Doc results for: " + option);
			printDocTitles(is, hits);
		}

		return docOptionScores;
	}


	public static void main(String[] args) {
		String examSectionName;
		String questionSectionName;
		String questionName;

		examSectionName = "Financial Transactions and Fraud Schemes";
		questionSectionName = "Basic Accounting Concepts";
		questionName = "Basic Accounting Concepts 8.txt";

		Logger.getInstance().setDetailLevel(DetailLevel.FULL);

		CFEExamQuestion question = new CFEExamQuestion("exam questions - all" + File.separator + examSectionName
				+ File.separator + questionSectionName + File.separator + questionName);

		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", question);

		ConceptMatchV3NOT cm = new ConceptMatchV3NOT();

		int result = cm.solve(question, null);
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
				"Option selected: " + (char) (result + 97) +  ") " + question.options.get(result));
		if (result == question.correctResponse) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Correct!");
		} else {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
					"Incorrect.  Correct answer: " + question.options.get(question.correctResponse));
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Explanation: " + question.explanation);
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Manual page: " + question.getSourcePage());
		}
	}
	
	@Override
	public String toString() {
		return "Composite Match V3 NOT";
	}

}
