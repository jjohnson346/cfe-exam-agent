package financial.fraud.cfe.ir;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.manual.CFEManualSection;

/**
 * search engine class based on the Jaccard coefficient which scores documents
 * relative to queries by looking at ratio: (d intersect q) / (d union q).
 * Applies the search to only the title of the section, not the text within the
 * section.
 * 
 * @author jjohnson346
 * 
 */
public class JaccardTitleSearchEngine extends AbstractSearchEngine {

	public JaccardTitleSearchEngine(CFEManualLargeDocUnit cfeManual) {
		super(cfeManual);
	}

	/**
	 * returns priority queue of sections with scores determined by jaccard
	 * coefficients based on words in titles of the sections.
	 */
	@Override
	public PriorityQueue<CFEManualSection> queryRetrieve(String query,
			String testingArea) {
		PriorityQueue<CFEManualSection> pq = new PriorityQueue<CFEManualSection>();

		HashSet<String> wordsInQuery = new HashSet<String>();
		wordsInQuery.addAll(tokenizeQuery(query));

		HashSet<String> wordsInTitle;
		HashSet<String> setUnion;
		HashSet<String> setIntersection;

		CFEManualSection section = cfeManual.getManualSectionForQuestionSection(testingArea);

		// for (CFEManualSection section :
		// cfeManual.getManualSections(testingArea)) {
		for (CFEManualSection descendant : section.getSubTreeAsList()) {
			wordsInTitle = new HashSet<String>();
			wordsInTitle.addAll(tokenizeQuery(descendant.name));
			setUnion = new HashSet<String>(wordsInTitle);
			setUnion.addAll(wordsInQuery);
			setIntersection = new HashSet<String>(wordsInQuery);
			setIntersection.retainAll(wordsInTitle);

			pq.add(section, ((double) setIntersection.size())
					/ ((double) setUnion.size()));
		}

		double topScore = pq.getPriority();
		if (topScore == 0.0)
			return null;
		else
			return retrieveTopResults(pq, 10);
	}

	/**
	 * performs unit test based on sequence of queries against titles for
	 * sections.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();
		JaccardTitleSearchEngine cfemse = new JaccardTitleSearchEngine(
				cfeManual);
		// String query = "president bush sarbanes-oxley";

		ArrayList<String> queries = new ArrayList<String>();
		queries.add("sarbanes-oxley|Manager's and Auditor's Responsibilities");
		queries.add("president bush sarbanes-oxley|Manager's and Auditor's Responsibilities");
		queries.add("avoiding the risk|Fraud Risk Assessment");
		queries.add("mitigating the risk|Fraud Risk Assessment");
		queries.add("transferring the risk|Fraud Risk Assessment");
		queries.add("assuming the risk|Fraud Risk Assessment");
		queries.add("catharsis|Interview Theory and Application");
		queries.add("therapy|Interview Theory and Application");
		queries.add("consoling|Interview Theory and Application");
		queries.add("mobile|Covert Examinations");
		queries.add("electronic|Covert Examinations");
		queries.add("undercover|Covert Examinations");
		queries.add("covert|Covert Examinations");
		queries.add("surveillance|Covert Examinations");
		queries.add("invigilation|Covert Examinations");
		queries.add("calibration|Interview Theory and Application");
		queries.add("confirmation|Interview Theory and Application");
		queries.add("closed|Interview Theory and Application");
		queries.add("complex|Interview Theory and Application");
		queries.add("reverse|Interview Theory and Application");
		queries.add("illustrators|Interview Theory and Application");
		queries.add("paralinguistics|Interview Theory and Application");
		queries.add("manipulators|Interview Theory and Application");
		queries.add("norming|Interview Theory and Application");
		queries.add("letters rogatory|Illicit Transactions");
		queries.add("writs of production|Illicit Transactions");
		queries.add("subpoenas in absentia|Illicit Transactions");
		queries.add("copiers|Digital Forensics");
		queries.add("printers|Digital Forensics");
		queries.add("removable storage devices|Digital Forensics");
		queries.add("The CFE/client privilege|Evidence");
		queries.add("The employer/employee privilege|Evidence");
		queries.add("The investigator/client privilege|Evidence");
		queries.add("Depositions|Civil Justice System");
		queries.add("Requests for stipulations|Civil Justice System");
		queries.add("Interrogatories|Civil Justice System");
		queries.add("Requests to produce documents|Civil Justice System");

		for (String query : queries) {
			String queryText = null;
			String testingArea = null;
			Scanner scanner = new Scanner(query);
			scanner.useDelimiter("\\|");
			queryText = scanner.next();
			testingArea = scanner.next();

			System.out.println("query text: " + queryText + " Testing area: "
					+ testingArea);
			PriorityQueue<CFEManualSection> results = cfemse.queryRetrieve(
					queryText, testingArea);

			if (results == null) {
				System.out.println("no results for this query!\n\n");
				continue;
			}
			PriorityQueue<CFEManualSection> resultsClone = results.clone();
			int size = results.size();
			double priority;
			for (int i = 0; i < size; i++) {
				priority = results.getPriority();
				System.out.println(results.next().name + ": " + priority);
			}

			boolean selected = false;

			for (int i = 0; i < size; i++) {
				priority = resultsClone.getPriority();
				if (priority == 1.0) {
					System.out.println("\n" + resultsClone.next().getText());
					selected = true;
				}
			}

			System.out.println("\n\n");
		}
	}
}
