package financial.fraud.cfe.ml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
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

import financial.fraud.cfe.ir.lucene.LuceneUtil;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;

/**
 * MLTraining4FileBuilder creates a machine learning input file, ml.training.4.txt, with the following field layout
 * given below.
 * 
 * Note this class is also used to create the test input file, as well, ml.test.4.txt.
 * 
 *
 * @author joejohnson
 *
 */
// * 1. question number
// * 2. question id <----
// * 3. question stem
// * 4. correct option
// * 5. option2
// * 6. option3
// * 7. option4
// * 8. document name
// * 8. document id
// * 9. document rank <---
// * 10. passage id
// * 11. passage
// * 12. number of words in common between question stem and passage <---
// * 13. length of maximum common word sequence <---
// * 14. is correct passage (y/n) <----

// Manual edits to output files:
// For ml.training.4.txt:
// none
//
// Note that for file counts for ml.training.4.txt, there are 8 questions for which no correct passage is found.
// This is consistent with the training set from file 3, where there are 8 such files with no matching passage.
//
// ml.test.4.txt:
// Digital forensics 7: doc id: EXAMINE AND DOCUMENT THE MACHINE’S SURROUNDINGS
// passage id: Additionally, becaus
// change isCorrectPassage to 1 (true), from 0 (false).
//
// Note that for

public class MLTraining4FileBuilder {

	private final String CFE_MANUAL_CLASS_NAME = "CFEManualSmallDocUnitRegex";

	// private final String INPUT_FILE = "ml.training.3.txt";
	// private final String OUTPUT_FILE = "ml.training.4.txt";
	private final String INPUT_FILE = "ml.test.3.txt";
	private final String OUTPUT_FILE = "ml.test.4.txt";

	private final int PASSAGE_ID_LENGTH = 25;

	protected String examSectionName;

	protected String indexQuestionSectionName;

	protected String indexDirectory;

	protected String[] elimPhrases = { "is referred to as", "are referred to as", "which of the following", "?",
			"are known as", "is known as", "are sometimes called", "is called", "would be described as" };

	public void createFile() {
		CommonWordSequencer commWordSequencer = new CommonWordSequencer();

		// Logger.getInstance().setDetailLevel(DetailLevel.FULL);
		try {
			Scanner fileScanner = new Scanner(new File("machine learning" + File.separator + INPUT_FILE));
			PrintWriter output = new PrintWriter(new File("machine learning" + File.separator + OUTPUT_FILE));
			// int totalCount = 0;
			// int correctPassageFoundCount = 0;
			// int correctPassageNotFoundCount = 0;
			int newFileCount = 0;
			HashMap<String, Integer> correctPassageCounts = new HashMap<String, Integer>();

			// Process each line of the ml.training.3.txt file.
			// Parse each record (using the bar | as the delimiter)
			// and load the fields into variables.

			while (fileScanner.hasNextLine()) {
				// for (int i = 0; i <= 0; i++) {
				String contents = fileScanner.nextLine();

				Scanner line = new Scanner(contents);
				line.useDelimiter("\\s\\|\\s*");
				int number = line.nextInt();
				String questionID = line.next();
				String examSection = line.next();
				String questionSection = line.next();
				String correctDocID = line.next();
				String correctDocRank = line.next();
				String correctDocName = line.next();
				String questionStem = line.next();
				String correctOption = line.next();
				String option2 = line.next();
				String option3 = line.next();
				String option4 = line.next();
				String correctPassageID = line.next();
				String correctPassage = line.next();

				// insert an entry in the correct passage counts map.
				correctPassageCounts.put(questionID, 0);

				// execute a lucene search based on question stem.
				// Get the docs that return from the lucene search and process
				// each document. That is, conduct a search for the correct passage
				// among the passages of all the docs returned. (We conduct the search
				// by comparing the first few characters of each passage to those of the
				// correct passage as given in the ml.training.3.txt file.)
				setIndexDirectory(questionSection);
				Directory dir = FSDirectory.open(new File(indexDirectory));
				IndexSearcher is = new IndexSearcher(dir);

				// get the docs that return from a search on question stem.
				TopDocs hits = getStemDocs(is, questionStem);

				boolean correctPassageFound = false;

				// determine the number of docs returned from lucene search -
				// should be min of actual number returned and 20 (an arbitrary number).
				int returnDocsCount = Math.min(hits.scoreDocs.length, 20);

				// process each of the return docs.
				for (int j = 0; j < returnDocsCount; j++) {
					Document doc = is.doc(hits.scoreDocs[j].doc);

					// get the contents from the return doc and then
					// parse it into separate passages (using the \n \n delimiter).
					String docContents = doc.get("contents");
					Scanner docScanner = new Scanner(docContents);
					docScanner.useDelimiter("\n \n");
					String docName = doc.get("title");
					String docID = String.valueOf(hits.scoreDocs[j].doc);
					String docRank = String.valueOf(j + 1);

					// process each passage in the return doc.
					while (docScanner.hasNext()) {
						// for (int k = 0; k <= 0; k++) {
						// remove the line feeds in the passage.

						String passage = docScanner.next().replaceAll("\n", "").replaceAll("\r", "").trim();

						// need to remove some wierd non-word characters here, whose character codes are
						// 147 and 133. Not sure what these are - the eclipse editor does not render them
						// as visible characters. However, the ascii table includes foreign characters for
						// these encodings as part of its extended set. Whatever the case, the following
						// two statements gets rid of them.
						passage = StringUtils.remove(passage, (char) 147);
						passage = StringUtils.remove(passage, (char) 133);

						// if passage not even as long as the length of id, just skip it...
						if (passage.length() <= PASSAGE_ID_LENGTH)
							continue;

						String passageID = passage.substring(0, PASSAGE_ID_LENGTH).trim();

						// determine if this is the correct passage by testing whether
						// the return doc has the correct id and whether the passage has a matching
						// id to the correct passage.
						int isCorrectPassage = (docID.equals(correctDocID) && passageID.equals(correctPassageID)) ? 1
								: 0;
						if (isCorrectPassage == 1) {
							correctPassageFound = true;
							int correctPassageCount = correctPassageCounts.get(questionID);
							correctPassageCounts.put(questionID, ++correctPassageCount);
						}

						// get number of words in common between question stem and passage.
						int numWordsInCommon = commWordSequencer.getCommonWords(questionStem, passage).size();

						// get length of longest common word sequence between stem and passage.
						int lengthLongestCommonSequence = commWordSequencer.getLongestCommonWordSequence(questionStem,
								passage).size();

						// write out the data for the current passage and current
						// question to the output file.
						StringBuilder sb = new StringBuilder();
						String delimiter = " | ";
						sb.append(++newFileCount + delimiter);
						sb.append(questionID + delimiter);
						sb.append(questionStem + delimiter);
						sb.append(correctOption + delimiter);
						sb.append(option2 + delimiter);
						sb.append(option3 + delimiter);
						sb.append(option4 + delimiter);
						sb.append(docName + delimiter);
						sb.append(docID + delimiter);
						sb.append(docRank + delimiter);
						sb.append(passageID + delimiter);

						// extract only the first few characters of the passage.
						// extracting the whole passage proved problematic as there
						// are some passage with funky characters, etc.
						int passageExtractLength = Math.min(passage.length(), 60);

						sb.append(passage.substring(0, passageExtractLength) + delimiter);
						sb.append(numWordsInCommon + delimiter);
						sb.append(lengthLongestCommonSequence + delimiter);
						sb.append(isCorrectPassage + delimiter);
						sb.append("\n");
						output.write(new String(sb));
						output.flush();
					}
				}

				// if (correctPassageFound) {
				// correctPassageFoundCount++;
				// } else {
				// System.out.println(questionID + ": correct passage not found. correct doc rank: " + correctDocRank);
				// correctPassageNotFoundCount++;
				// }
				// totalCount++;
			}

			fileScanner.close();
			output.close();

			printCorrectPassageSummary(correctPassageCounts);

			// System.out.println();
			// System.out.println("passage found count: " + correctPassageFoundCount);
			// System.out.println("passage not found count: " + correctPassageNotFoundCount);
			// System.out.println("total count: " + totalCount);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "unable to parse query: " + e.getMessage());
		}

	}

	/**
	 * outputs questions with no correct passage found and those questions with multiple correct passages found, if they
	 * exist.
	 * 
	 * @param passageCounts
	 */
	private void printCorrectPassageSummary(Map<String, Integer> passageCounts) {
		// determine number of questions for which no correct passage found.
		int noCorrectPassageCount = 0;
		int multiCorrectPassageCount = 0;
		List<String> noPassageQuestions = new LinkedList<String>();
		List<String> multiCorrectPassageQuestions = new LinkedList<String>();

		for (Map.Entry<String, Integer> e : passageCounts.entrySet()) {
			if (e.getValue() == 0) {
				noCorrectPassageCount++;
				noPassageQuestions.add(e.getKey());
			} else if (e.getValue() > 1) {
				multiCorrectPassageCount++;
				multiCorrectPassageQuestions.add(e.getKey());
			}
		}

		System.out.println("Number of questions with no correct passage found: " + noCorrectPassageCount);
		if (!noPassageQuestions.isEmpty()) {
			for (String q : noPassageQuestions)
				System.out.println("\t" + q);
		}

		System.out.println("Number of questions with >1 correct passage found: " + multiCorrectPassageCount);
		if (!multiCorrectPassageQuestions.isEmpty()) {
			for (String q : multiCorrectPassageQuestions)
				System.out.println("\t" + q);
		}
	}

	protected void setIndexDirectory(String questionSection) throws IOException {
		examSectionName = LuceneUtil.getExamSection(questionSection);
		indexQuestionSectionName = LuceneUtil.getIndexQuestionSection(questionSection);
		if (examSectionName == null)
			throw new IOException("unable to retrieve exam section for question section: " + questionSection);
		if (indexQuestionSectionName == null)
			throw new IOException("unable to retrieve index question section name for question section: "
					+ questionSection);

		indexDirectory = "lucene index collection" + File.separator + CFE_MANUAL_CLASS_NAME + File.separator
				+ examSectionName + File.separator + indexQuestionSectionName;
	}

	protected TopDocs getStemDocs(IndexSearcher is, String questionStem) throws ParseException, IOException {
		String lowerStem = questionStem;

		// remove elimination phrases (see array initialized above).
		for (String elimPhrase : elimPhrases)
			lowerStem = lowerStem.replace(elimPhrase, "");

		// remove any colon that may be in the stem, :. This is syntax recognized
		// by the QueryParse as part of the query language syntax for lucene. The colon
		// is used to identify a field upon which to base the search. Query string,
		// "title:extreme", indicates that the query is intended as a search for the word
		// extreme in the title field, reference McCandless, Lucene in Action, page 80.
		lowerStem = lowerStem.replaceAll(":", "");

		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Stem (lower case): " + lowerStem + "\n");

		String fieldName = "contents";
		String queryString = lowerStem;
		// String queryString =
		// "The worth of a business, if it is any good, will always be higher than the value of its hard assets. This is reflected in the accounting concept of:";
		QueryParser parser = new QueryParser(Version.LUCENE_30, fieldName, new StandardAnalyzer(Version.LUCENE_30));
		Query query = parser.parse(QueryParser.escape(queryString));
		TopDocs hits = is.search(query, 20);

		// print out the doc titles as a reasonableness check.
		Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "Doc results for stem: " + lowerStem);
		// printDocTitles(is, hits);

		return hits;
	}

	protected void printDocTitles(IndexSearcher is, TopDocs hits) throws IOException {
		ScoreDoc[] matches = hits.scoreDocs;
		if (matches.length == 0)
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "** no docs returned **");
		for (int i = 0; i < matches.length; i++) {
			Document doc = is.doc(matches[i].doc);
			Logger.getInstance().printf(
					DetailLevel.FULL,
					"%s\n",
					(i + 1) + ". " + doc.get("title") + "(" + matches[i].doc + ")" + "  " + matches[i].score + " "
							+ doc.get("title").length());
		}

		Logger.getInstance().printf(DetailLevel.FULL, "%s", "\n");
	}

	public static void main(String[] args) {
		MLTraining4FileBuilder training3Builder = new MLTraining4FileBuilder();
		training3Builder.createFile();
	}
}
