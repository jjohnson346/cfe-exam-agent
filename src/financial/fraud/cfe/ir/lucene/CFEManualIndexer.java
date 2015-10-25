package financial.fraud.cfe.ir.lucene;

import java.util.List;

import cfe.manual.docs.collection.Document;
import cfe.manual.docs.collection.DocumentCollection;

public class CFEManualIndexer {

	public static void main(String[] args) {
		DocumentCollection dc = new DocumentCollection("CFEManualSmallDocUnit");

		List<Document> bankruptcyDocs = dc.getDocuments("Financial Transactions and Fraud Schemes", "Bankruptcy Fraud");

		for (Document d : bankruptcyDocs)
			System.out.println(d.getTitle());

	}
}
