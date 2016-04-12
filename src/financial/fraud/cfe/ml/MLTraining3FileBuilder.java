package financial.fraud.cfe.ml;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
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
 * This class builds ml.training.3.txt from ml.training.2.txt (and also, builds ml.test.3.txt from ml.test.2.txt).
 * 
 * The new file contains the rank of the correct document in the lucene results array.
 * 
 * Note that for the following questions, the ranks were manually edited because of a strange glitch in the way Lucene
 * document titles are stored in string variables. For example, for question 15, the correct document,
 * "Employees Right Against Self-Incrimination" is stored as "Employee s Right Against SelfIncrimination" even though
 * when printed out, the title shows "Employees Right Against SelfIncrimination" (no space between the e and s in
 * Employees).
 */
// field layout for file3:
// -----------------------
// 1. question number
// 2. question id
// 3. exam section
// 4. question section
// 5. correct doc id
// 6. correct doc rank
// 7. correct doc name
// 8. question stem
// 9. correct option
// 10. option2
// 11. option3
// 12. option4
// 13. correct passage id
// 14. correct passage
//
//
// For Test set questions: 
// Question 9 Correct document: On-Us Items 
// Question 15 Correct Document: Employees Right Against Self-Incrimination
//
// Manual overrides of rank:
// -------------------------
// Training set:
// 1. Financial Institution Fraud 31 - change rank to 1, doc id to 8.
// 2. Illicit Transactions 29 - change rank to 2, doc id to 21.
//
// Note that with these edits above, we have only 8 questions for which lucene does not find the matching document.
// (i.e., where the correct doc id = -1, correct doc rank = -1.
//
// Test set:
// 1. Digital Forensics 7 - change rank to 1, doc id to 47
// 2. Illicit Transactions 40 - change rank to 1, doc id to 36
// 3. Legal Rights of Employees 43 - change rank to 3, doc id to 13

public class MLTraining3FileBuilder {

	private final String CFE_MANUAL_CLASS_NAME = "CFEManualSmallDocUnitRegex";

//	private final String INPUT_FILE = "ml.training.2.txt";
//	private final String OUTPUT_FILE = "ml.training.3.txt";

	private final String INPUT_FILE = "ml.test.2.txt";
	private final String OUTPUT_FILE = "ml.test.3.txt";
	private final int PASSAGE_ID_LENGTH = 25;

	protected String examSectionName;

	protected String indexQuestionSectionName;

	protected String indexDirectory;

	protected String[] elimPhrases = { "is referred to as", "are referred to as", "which of the following", "?",
			"are known as", "is known as", "are sometimes called", "is called", "would be described as" };

	public void createFile() {
		Logger.getInstance().setDetailLevel(DetailLevel.FULL);
		try {
			// Scanner fileScanner = new Scanner(new File("machine learning" + File.separator + "ml.training.2.txt"));
			Scanner fileScanner = new Scanner(new File("machine learning" + File.separator + INPUT_FILE));
			PrintWriter output = new PrintWriter(new File("machine learning" + File.separator + OUTPUT_FILE));
			int totalCount = 0;
			int rank1Count = 0;
			int rankTop3Count = 0;
			int fileNotFoundCount = 0;
			int newFileCount = 0;
			// fileScanner.useDelimiter("\\Z");

			while (fileScanner.hasNextLine()) {
				String contents = fileScanner.nextLine();

				Scanner line = new Scanner(contents);
				line.useDelimiter("\\s\\|\\s*");
				int number = line.nextInt();
				String questionID = line.next();
				String questionStem = line.next();
				String correctOption = line.next();
				String option2 = line.next();
				String option3 = line.next();
				String option4 = line.next();
				String manualPage = line.next();
				String examSection = line.next();
				String questionSection = line.next();
				String documents = line.next();
				String correctDocName = line.next();
				String correctPassage = line.next();

				System.out.println(contents);

				System.out.println("question ID: " + questionID);
				System.out.println("stem: " + questionStem);
				System.out.println("correctOption: " + correctOption);
				System.out.println("exam section: " + examSection);
				System.out.println("question section: " + questionSection);
				System.out.println("documents: " + documents);
				System.out.println("correct document: " + correctDocName);
				System.out.println("correct passage: " + correctPassage);

				setIndexDirectory(questionSection);
				Directory dir = FSDirectory.open(new File(indexDirectory));
				IndexSearcher is = new IndexSearcher(dir);

				// get the docs that return from a search on question stem.
				TopDocs hits = getStemDocs(is, questionStem);

				System.out.println();

				int rank = getMatchingDocRank(is, hits, correctDocName);
				int correctDocID = (rank != -1) ? hits.scoreDocs[rank - 1].doc : -1;
				String correctPassageID = correctPassage.substring(0, PASSAGE_ID_LENGTH).trim();

				if (rank == -1)
					fileNotFoundCount++;
				if (rank == 1)
					rank1Count++;
				if (rank <= 3 && rank > 0)
					rankTop3Count++;
				totalCount++;

				System.out.println(rank);
				line.close();

				StringBuilder sb = new StringBuilder();
				String delimiter = " | ";
				sb.append(++newFileCount + delimiter);
				sb.append(questionID + delimiter);
				sb.append(examSection + delimiter);
				sb.append(questionSection + delimiter);
				sb.append(correctDocID + delimiter);
				sb.append(rank + delimiter);
				sb.append(correctDocName + delimiter);
				sb.append(questionStem + delimiter);
				sb.append(correctOption + delimiter);
				sb.append(option2 + delimiter);
				sb.append(option3 + delimiter);
				sb.append(option4 + delimiter);
				sb.append(correctPassageID + delimiter);
				sb.append(correctPassage + delimiter);
				sb.append("\n");
				output.write(new String(sb));
				output.flush();
			}

			fileScanner.close();
			output.close();
			System.out.println("no file match count: " + fileNotFoundCount);
			System.out.println("rank 1 count: " + rank1Count);
			System.out.println("rank top 3 count: " + rankTop3Count);
			System.out.println("total count: " + totalCount);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			Logger.getInstance().printf(DetailLevel.FULL, "%s\n", "unable to parse query: " + e.getMessage());
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
		printDocTitles(is, hits);

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
		MLTraining3FileBuilder training3Builder = new MLTraining3FileBuilder();
		training3Builder.createFile();
	}
}
