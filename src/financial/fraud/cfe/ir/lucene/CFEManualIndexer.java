package financial.fraud.cfe.ir.lucene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import cfe.manual.docs.collection.Document;
import cfe.manual.docs.collection.DocumentCollection;

public class CFEManualIndexer {

//	private IndexWriter writer;
//
//	public CFEManualIndexer(String indexDir) throws IOException {
//		Directory dir = FSDirectory.open(new File(indexDir));
//		writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30),
//
//		true, IndexWriter.MaxFieldLength.UNLIMITED);
//	}
//
//	public void close() throws IOException {
//		writer.close();
//	}

	public void buildIndexCollection(String cfeManualClassName) throws IOException {
		// String cfeManualFullClassName = cfeManual.getClass().getName();
		// String cfeManualClassName = StringUtils.substringAfterLast(cfeManualFullClassName, ".");

		File indexCollectionDir = new File("lucene index collection");
		if (!indexCollectionDir.exists())
			indexCollectionDir.mkdir();

		File cfeManualIndexDir = new File("lucene index collection/" + cfeManualClassName);
		// if doc collection already exists, return.
		if (cfeManualIndexDir.exists()) {
			System.out.println("Index collection already exists for " + cfeManualClassName);
			return;
		}

		// index collection for cfeManual does not exist, so go ahead and make it.
		cfeManualIndexDir.mkdir();

		DocumentCollection dc = new DocumentCollection(cfeManualClassName);
		Map<String, Map<String, List<Document>>> docCollection = dc.getDocuments();

		int totalDocCount = 0;
		
		for (Map.Entry<String, Map<String, List<Document>>> examSectionEntry : docCollection.entrySet()) {
			// make exam section directory if it doesn't already exist.
			String examSectionName = examSectionEntry.getKey();
			// File examSectionIndexDir = new File("lucene index collection/" + cfeManualClassName + "/"
			// + examSectionEntry.getKey());
			// if (!examSectionIndexDir.exists())
			// examSectionIndexDir.mkdir();

			Map<String, List<Document>> questionSections = examSectionEntry.getValue();

			for (Map.Entry<String, List<Document>> questionSectionEntry : questionSections.entrySet()) {
				String questionSectionName = questionSectionEntry.getKey();
				List<Document> docs = questionSectionEntry.getValue();
				int numDocs = index(cfeManualClassName, examSectionName, questionSectionName, docs);
				System.out.println(examSectionName + ": " + questionSectionName + ": " + numDocs + " documents indexed.");
				totalDocCount += numDocs;
			}
 		}
		System.out.println("Total Documents indexed: " + totalDocCount);
	}

	public int index(String cfeManualClassName, String examSectionName, String questionSectionName, List<Document> docs)
			throws IOException {
		String questionSectionIndexDirName = "lucene index collection/" + cfeManualClassName + "/" + examSectionName
				+ "/" + questionSectionName;
		File questionSectionIndexDir = new File(questionSectionIndexDirName);
		// make question section directory if it doesn't already exist.
		if (!questionSectionIndexDir.exists())
			questionSectionIndexDir.mkdir();

		Directory dir = FSDirectory.open(new File(questionSectionIndexDirName));
		IndexWriter writer = new IndexWriter(dir, new StandardAnalyzer(Version.LUCENE_30), true,
				IndexWriter.MaxFieldLength.UNLIMITED);

		try {
			for (Document d : docs) {
				System.out.println("Indexing " + d.getTitle());
				org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
				doc.add(new Field("contents", d.getRawText(), Field.Store.YES, Field.Index.ANALYZED));
				doc.add(new Field("path", d.getPath(), Field.Store.YES, Field.Index.ANALYZED));
				writer.addDocument(doc);
			}
		} finally {
			writer.close();
		}
		return writer.numDocs();

	}

	// public int index() throws Exception {
	//
	// DocumentCollection dc = new DocumentCollection("CFEManualSmallDocUnitRegex");
	//
	// List<Document> bankruptcyDocs = dc.getDocuments("Financial Transactions and Fraud Schemes", "Bankruptcy Fraud");
	//
	// for (Document d : bankruptcyDocs) {
	// System.out.println("Indexing " + d.getTitle());
	// org.apache.lucene.document.Document doc = new org.apache.lucene.document.Document();
	// doc.add(new Field("contents", d.getRawText(), Field.Store.YES, Field.Index.ANALYZED));
	// doc.add(new Field("path", d.getPath(), Field.Store.YES, Field.Index.ANALYZED));
	// writer.addDocument(doc);
	// }
	//
	// return writer.numDocs();
	// }

	// public static void main(String[] args) throws Exception {
	// String indexDir =
	// "lucene indexes/CFEManualSmallDocUnitRegex/Financial Transactions and Fraud Schemes/Bankruptcy Fraud";
	//
	// long start = System.currentTimeMillis();
	// CFEManualIndexer indexer = new CFEManualIndexer(indexDir);
	// int numIndexed;
	// try {
	// numIndexed = indexer.index();
	// } finally {
	// indexer.close();
	// }
	// long end = System.currentTimeMillis();
	//
	// System.out.println("Indexing " + numIndexed + " files took " + (end - start) + " milliseconds");
	// }
	
	public static void main(String[] args) throws Exception {
		long start = System.currentTimeMillis();
		CFEManualIndexer indexer = new CFEManualIndexer();
		indexer.buildIndexCollection("CFEManualSmallDocUnitRegex");
		long end = System.currentTimeMillis();
	}
}
