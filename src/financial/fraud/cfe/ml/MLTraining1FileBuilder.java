package financial.fraud.cfe.ml;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.List;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.ir.lucene.LuceneUtil;
import financial.fraud.cfe.manual.CFEManualSection;
import financial.fraud.cfe.manual.CFEManualSmallDocUnitRegex;
import financial.fraud.cfe.util.QuestionServer;

/**
 * MLTraining1FileBuilder creates a file, ml.training.1.txt (or ml.test.1.txt) containing training (or test) 
 * questions with question profile of 4,
 * including question id, stem, correct option, other options, set of possible documents (based on the question
 * page number contained in the question explanation).  
 * 
 * This information is then used to manually create a new file, ml.training.2.txt, (or ml.test.2.txt) 
 * which contains the fields, correct document and correct passage, which were determined manually.
 * 
 * The ultimate goal is to create a file that has the following fields:  (note: the fields with arrows are
 * the fields we'll need for machine learning).
 * 
 * 1. question number
 * 2. question id <----
 * 3. question stem
 * 4. correct option
 * 5. option2
 * 6. option3
 * 7. option4
 * 8. correct document
 * 9. correct document rank 
 * 10. correct passage
 * 11. current document
 * 12. current document rank <---
 * 13. current passage
 * 14. number of words in common  between question stem and passage <---
 * 15. length of maximum common word sequence <---
 * 16. is correct passage (y/n) <----
 * 
 * @author joejohnson
 *
 */
public class MLTraining1FileBuilder {
	
	public static final int QUESTION_PROFILE = 4;
	public static final String QUESTION_SET_DIR = "exam questions - test set";
	public static final String OUTPUT_FILE_NAME = "ml.test.1.txt";

	public static void main(String[] args) {
		QuestionServer qs = new QuestionServer(QUESTION_SET_DIR, QUESTION_PROFILE);
		CFEManualSmallDocUnitRegex manual = new CFEManualSmallDocUnitRegex();

		int count = 0;
		StringBuilder sb = new StringBuilder();

		while (qs.hasNext()) {
			CFEExamQuestion q = qs.next();

			if (q.getSourcePage().toLowerCase().equals("no source page found in explanation."))
				continue;

			sb.append(count++ + " | ");
			sb.append(q.name + " | ");
			sb.append(q.stem + " | ");
			sb.append(q.options.get(q.correctResponse) + " | ");
			for (int i = 0; i < q.options.size(); i++)
				if (i != q.correctResponse)
					sb.append(q.options.get(i) + " | ");
			// sb.append(q.explanation + " | ");

			String beginPage = q.getSourcePage();
			sb.append(beginPage + " | ");
			
			String examSection = LuceneUtil.getExamSection(q.section);
			sb.append(examSection + " | ");
			sb.append(q.section + " | ");

			List<CFEManualSection> subSections = manual.getManualSectionForPage(beginPage);
			for (int i = 0; i < subSections.size(); i++) {
				sb.append(subSections.get(i).name);
				sb.append((i == subSections.size() - 1) ? " | " : "; ");
			}

			sb.append("\n");

		}

		String mlData = new String(sb);

		System.out.println(mlData);

		try {
			Formatter output = new Formatter("machine learning" + File.separator + OUTPUT_FILE_NAME);
			output.format("%s", mlData);
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
