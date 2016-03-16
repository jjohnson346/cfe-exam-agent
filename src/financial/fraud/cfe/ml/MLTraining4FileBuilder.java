package financial.fraud.cfe.ml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Scanner;

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
 * MLTraining4FileBuilder creates a machine learning input file with the following field layout:
 * 
 * 1. question number 2. question id <---- 3. question stem 4. correct option 5. option2 6. option3 7. option4 8.
 * document name 8. document id 9. document rank <--- 10. passage id 11. passage 12. number of words in common between
 * question stem and passage <--- 13. length of maximum common word sequence <--- 14. is correct passage (y/n) <----
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

public class MLTraining4FileBuilder {

	private final String CFE_MANUAL_CLASS_NAME = "CFEManualSmallDocUnitRegex";

	private final String INPUT_FILE = "ml.training.3.txt";
	private final String OUTPUT_FILE = "ml.training.4.txt";
	private final int PASSAGE_ID_LENGTH = 20;

	// private final String INPUT_FILE = "ml.test.3.txt";
	// private final String OUTPUT_FILE = "ml.test.4.txt";

	protected String examSectionName;

	protected String indexQuestionSectionName;

	protected String indexDirectory;

	protected String[] elimPhrases = { "is referred to as", "are referred to as", "which of the following", "?",
			"are known as", "is known as", "are sometimes called", "is called", "would be described as" };

	public void createFile() {
		// Logger.getInstance().setDetailLevel(DetailLevel.FULL);
		try {
			// Scanner fileScanner = new Scanner(new File("machine learning" + File.separator + "ml.training.2.txt"));
			Scanner fileScanner = new Scanner(new File("machine learning" + File.separator + INPUT_FILE));
			PrintWriter output = new PrintWriter(new File("machine learning" + File.separator + OUTPUT_FILE));
			int totalCount = 0;
			int rank1Count = 0;
			int rankTop3Count = 0;
			int fileNotFoundCount = 0;
			int correctPassageFoundCount = 0;
			int correctPassageNotFoundCount = 0;
			int newFileCount = 0;
			// fileScanner.useDelimiter("\\Z");

			// while (fileScanner.hasNextLine()) {
			for (int i = 0; i <= 0; i++) {
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

				// System.out.println(contents);

				// System.out.println("question ID: " + questionID);
				// System.out.println("exam section: " + examSection);
				// System.out.println("question section: " + questionSection);
				// System.out.println("correct doc id: " + correctDocID);
				// System.out.println("correct doc rank: " + correctDocRank);
				// System.out.println("correct doc: " + correctDocName);
				// System.out.println("stem: " + questionStem);
				// System.out.println("correctOption: " + correctOption);
				// System.out.println("option2: " + option2);
				// System.out.println("option3: " + option3);
				// System.out.println("option4: " + option4);
				// System.out.println("correct passage id: " + correctPassageID);
				// System.out.println("correct passage: " + correctPassage);

				setIndexDirectory(questionSection);
				Directory dir = FSDirectory.open(new File(indexDirectory));
				IndexSearcher is = new IndexSearcher(dir);

				// get the docs that return from a search on question stem.
				TopDocs hits = getStemDocs(is, questionStem);

				boolean correctPassageFound = false;

				int returnDocsCount = Math.min(hits.scoreDocs.length, 20);
				// for (int j = 0; j < returnDocsCount; j++) {
				for (int j = 0; j <= 0; j++) {
					Document doc = is.doc(hits.scoreDocs[j].doc);

					String docContents = doc.get("contents");
					Scanner docScanner = new Scanner(docContents);
					docScanner.useDelimiter("\n \n");
					String docName = doc.get("title");
					String docID = String.valueOf(hits.scoreDocs[j].doc);
					String docRank = String.valueOf(j + 1);

					int passageCount = 0;

					// while (docScanner.hasNext()) {
					for (int k = 0; k <= 0; k++) {
						String passage = docScanner.next().replaceAll("\n", "");
						if (passage.length() <= PASSAGE_ID_LENGTH)
							continue;
						String passageID = passage.substring(0, PASSAGE_ID_LENGTH);
						int isCorrectPassage = (docID.equals(correctDocID) && passageID.equals(correctPassageID)) ? 1
								: 0;
						System.out.println("passageID: " + passageID);
						// System.out.println("correctPassageID: " + correctPassageID);
						// System.out.println("passage " + passageCount++ + ":");
						System.out.println(passage + "\n");
						
						int numWordsInCommon = getNumWordsInCommon(questionStem, passage);
						
						if (isCorrectPassage == 1) {
							correctPassageFound = true;
						}

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
						sb.append(passage + delimiter);
						sb.append(numWordsInCommon + delimiter);
						sb.append(isCorrectPassage + delimiter);
						sb.append("\n");
						output.write(new String(sb));
						output.flush();

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
					}

				}

				System.out.println(questionID + ": correct passage found?: " + correctPassageFound);
				if (correctPassageFound)
					correctPassageFoundCount++;
				else
					correctPassageNotFoundCount++;
				totalCount++;
			}

			fileScanner.close();
			output.close();

			System.out.println();
			System.out.println("passage found count: " + correctPassageFoundCount);
			System.out.println("passage not found count: " + correctPassageNotFoundCount);
			System.out.println("total count: " + totalCount);

		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "unable to parse query: " + e.getMessage());
		}

	}
	
	/**
	 * returns the number of words in common between a question stem and a passage (both of which are input
	 * arguments).
	 * 
	 * @param questionStem
	 * @param passage
	 * @return the number of words in common between questionStem and passage
	 * @throws IOException
	 */
	private int getNumWordsInCommon(String questionStem, String passage) throws IOException {
		
		// extract words from the passage and load into a hash set.
		HashSet<String> commonWords = getWordSetFromText(passage.toLowerCase());
		System.out.println("passage words: " + commonWords);
		
		// extract words from question stem and load into a hash set.
		HashSet<String> stemWords = getWordSetFromText(questionStem.toLowerCase());
		System.out.println("stem words: " + stemWords);
		
		// remove those words in passage that are not in the question stem.
		commonWords.retainAll(stemWords);
		System.out.println("common words: " + commonWords);
		
		// remove function words.
		Scanner functionWordsFileScanner = new Scanner(new File("function words//function.words.txt"));
		functionWordsFileScanner.useDelimiter("\\Z");
		String fw = functionWordsFileScanner.next();
		String[] fwords = fw.split("[ \n]");
		HashSet<String> functionWords = new HashSet(Arrays.asList(fwords));
		System.out.println("function words: " + functionWords);
		commonWords.removeAll(functionWords);
		
		// remove the zero-length string from the common words collection.
		commonWords.remove("");
		
		System.out.println("common words without function words: " + commonWords);
			
		// return the size of the collection as the number of words in common.
		return commonWords.size();
	}
	
	private HashSet<String> getWordSetFromText(String passage) {
		String[] words = passage.split("[ \n\t\r.,;:!?(){}<>\"]");
		
		// adding words to TreeSet removes duplicates and sorts them alphabetically.
		return new HashSet<String>(Arrays.asList(words));
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
			// String title = doc.get("title");
			// for(int j = 0; j < title.length(); j++) {
			// System.out.print(j+1 + ": " + title.charAt(j) + " ");
			// }
			// System.out.println();
			// String skeletonTitle = title.replaceAll("\\s", "");
			// System.out.println(skeletonTitle + skeletonTitle.length());
			// System.out.println();
		}

		Logger.getInstance().printf(DetailLevel.FULL, "%s", "\n");
	}

	private int getMatchingDocRank(IndexSearcher is, TopDocs hits, String correctDocument) throws IOException {
		ScoreDoc[] matches = hits.scoreDocs;
		String modCorrectDoc = correctDocument.trim().toLowerCase().replaceAll(":", " ");
		modCorrectDoc = modCorrectDoc.replaceAll("-", "");
		// System.out.println("\n" + modCorrectDoc + " " + modCorrectDoc.length() + "\n");
		// String skeletonModCorrectDoc = modCorrectDoc.replaceAll("\\s", "");
		// System.out.println(skeletonModCorrectDoc + " " + skeletonModCorrectDoc.length());

		for (int i = 0; i < matches.length; i++) {
			Document doc = is.doc(matches[i].doc);

			// modCorrectDoc replaces the colon character, ":", in correctDocument with a space character
			// " " because
			// Lucene removes colons from doc titles, (since : has special meaning in the Lucene
			// query language, presumably. Also, do this for the dash character, "-".
			if (doc.get("title").trim().toLowerCase().equals(modCorrectDoc)) {
				// current document is the correct document.
				// return its rank.
				return i + 1;
			}
		}
		// no matching doc found
		return -1;
	}

	public static void main(String[] args) {
		MLTraining4FileBuilder training3Builder = new MLTraining4FileBuilder();
		training3Builder.createFile();
	}
}
