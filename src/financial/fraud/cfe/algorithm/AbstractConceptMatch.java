package financial.fraud.cfe.algorithm;

import java.io.File;
import java.io.IOException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.util.Version;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.ir.lucene.LuceneUtil;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;
import financial.fraud.cfe.manual.CFEManual;

public class AbstractConceptMatch implements IAlgorithm {

	private final String CFE_MANUAL_CLASS_NAME = "CFEManualSmallDocUnitRegex";

	protected String examSectionName;

	protected String indexQuestionSectionName;

	protected String indexDirectory;

	protected String[] elimPhrases = { "is referred to as", "are referred to as", "which of the following", "?",
			"are known as", "is known as", "are sometimes called", "is called", "would be described as" };

	@Override
	public int solve(CFEExamQuestion question, CFEManual cfeManual) {
		return 0;
	}

	protected void setIndexDirectory(CFEExamQuestion question) throws IOException {
		examSectionName = LuceneUtil.getExamSection(question.section);
		indexQuestionSectionName = LuceneUtil.getIndexQuestionSection(question.section);
		if (examSectionName == null)
			throw new IOException("unable to retrieve exam section for question section: " + question.section);
		if (indexQuestionSectionName == null)
			throw new IOException("unable to retrieve index question section name for question section: "
					+ question.section);

		indexDirectory = "lucene index collection" + File.separator + CFE_MANUAL_CLASS_NAME + File.separator
				+ examSectionName + File.separator + indexQuestionSectionName;
	}
	
	protected TopDocs getStemDocs(IndexSearcher is, CFEExamQuestion question) throws ParseException, IOException {
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

		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Stem (lower case): " + lowerStem + "\n");

		String fieldName = "contents";
		String queryString = lowerStem;
		// String queryString =
		// "The worth of a business, if it is any good, will always be higher than the value of its hard assets. This is reflected in the accounting concept of:";
		QueryParser parser = new QueryParser(Version.LUCENE_30, fieldName, new StandardAnalyzer(Version.LUCENE_30));
		Query query = parser.parse(queryString);
		TopDocs hits = is.search(query, 10);

		// print out the doc titles as a reasonableness check.
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Doc results for stem: " + lowerStem);
		printDocTitles(is, hits);

		return hits;
	}
	
	protected void printDocTitles(IndexSearcher is, TopDocs hits) throws IOException {
		ScoreDoc[] matches = hits.scoreDocs;
		if (matches.length == 0)
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "** no docs returned **");
		for (int i = 0; i < matches.length; i++) {
			Document doc = is.doc(matches[i].doc);
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n",
					(i + 1) + ". " + doc.get("title") + "(" + matches[i].doc + ")" + "  " + matches[i].score);
		}
		Logger.getInstance().printf(DetailLevel.FULL, "%s", "\n");
	}

	
	class OptionScore {
		int option;
		double score;

		OptionScore(int option, double score) {
			this.option = option;
			this.score = score;
		}
		
		@Override
		public String toString() {
			return "option: " + option + " score: " + score;
		}
	}


}
