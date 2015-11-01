package financial.fraud.cfe.ir.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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

public class CFEManualIndexSearcher {

	private final String INDEX_DIR;
	IndexSearcher is;

	public CFEManualIndexSearcher(String CFEManualClassName, String examSectionName, String questionSectionName)
			throws IOException {
		INDEX_DIR = "lucene index collection/" + CFEManualClassName + "/" + examSectionName + "/" + questionSectionName;
		Directory dir = FSDirectory.open(new File(INDEX_DIR));
		is = new IndexSearcher(dir);
	}

	public List<Document> search(String q) throws IOException, ParseException {

		QueryParser parser = new QueryParser(Version.LUCENE_30, "contents", new StandardAnalyzer(Version.LUCENE_30));
		Query query = parser.parse(q);
		// long start = System.currentTimeMillis();
		TopDocs hits = is.search(query, 10);
		// long end = System.currentTimeMillis();

		// System.err.println("Found " + hits.totalHits + " document(s) (in " + (end - start)
		// + " milliseconds) that matched query '" + q + "':");

		List<Document> results = new ArrayList<Document>();
		for (ScoreDoc scoreDoc : hits.scoreDocs) {
			Document doc = is.doc(scoreDoc.doc);
			results.add(doc);
		}
		return results;

	}

	public void close() throws IOException {
		is.close();
	}

	public static void main(String[] args) {
		try {
			CFEManualIndexSearcher indexSearcher = new CFEManualIndexSearcher("CFEManualSmallDocUnitRegex",
					"Financial Transactions and Fraud Schemes", "Bankruptcy Fraud");
			
			List<Document> results = indexSearcher.search("court");
			
			for(Document d : results)
				System.out.println(d.get("title"));
			
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			System.out.println("unable to parse query.");
		}
	}

}
