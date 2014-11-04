package cfe.manual.docs.collection;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DocumentCollection {

	private Map<String, Map<String, List<Document>>> docCollection; // stores the list of documents for each exam
																	// section/
																	// question section

	/**
	 * constructor sets up the data structure containing the document collection, organized by exam section, and then by
	 * question section.
	 * 
	 * @param cfeManualType
	 */
	public DocumentCollection(String cfeManualType) {

		docCollection = new HashMap<String, Map<String, List<Document>>>();

		File documentCollectionDir = new File("document collection/" + cfeManualType);

		if (!documentCollectionDir.exists()) {
			System.out.println("document collection folder does not exist.");
			System.exit(1);
		}

		try {
			// traverse exam section directories.
			for (File examSectionDir : documentCollectionDir.listFiles()) {
				if (examSectionDir.isDirectory()) {
					Map<String, List<Document>> examSectionDocCollection = new HashMap<String, List<Document>>();

					// traverse question section directories, for each exam section directory.
					for (File questionSectionDir : examSectionDir.listFiles()) {
						if (questionSectionDir.isDirectory()) {
							List<Document> questionSectionDocuments = new ArrayList<Document>();

							// traverse the doc files in the question section directory.
							// build the list of documents.
							for (File questionSectionDocumentFile : questionSectionDir.listFiles()) {
								if (questionSectionDocumentFile.isFile()) {
									questionSectionDocuments.add(new Document(questionSectionDocumentFile
											.getCanonicalPath()));
								}
							}
							examSectionDocCollection.put(questionSectionDir.getName(), questionSectionDocuments);
						}
					}
					docCollection.put(examSectionDir.getName(), examSectionDocCollection);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * returns the list of documents for a particular exam section and question section, supplied as input.
	 * 
	 * @param examSection
	 * @param questionSection
	 * @return
	 */
	public List<Document> getDocuments(String examSection, String questionSection) {
		Map<String, List<Document>> examSectionDocCollection = docCollection.get(examSection);
		return examSectionDocCollection.get(questionSection);
	}

	public static void main(String[] args) {
		DocumentCollection dc = new DocumentCollection("CFEManualSmallDocUnit");

		List<Document> bankruptcyDocs = dc.getDocuments("Financial Transactions and Fraud Schemes", "Bankruptcy Fraud");

		for (Document d : bankruptcyDocs)
			System.out.println(d.getTitle());

	}
}
