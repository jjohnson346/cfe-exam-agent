package financial.fraud.cfe.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.List;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.ir.lucene.LuceneUtil;
import financial.fraud.cfe.manual.CFEManualSection;
import financial.fraud.cfe.manual.CFEManualSmallDocUnitRegex;

public class QuestionExtractor {

	public static final int QUESTION_PROFILE = 4;
	public static final String QUESTION_SET_DIR = "exam questions - training set" + File.separator
			+ "Investigation" + File.separator + "Covert Examinations";
	public static final String OUTPUT_FILE_NAME = "covert_examinations.txt";

	public static void main(String[] args) {
		System.out.println(QUESTION_SET_DIR);
		QuestionServer qs = new QuestionServer(QUESTION_SET_DIR);
		// CFEManualSmallDocUnitRegex manual = new CFEManualSmallDocUnitRegex();

		int count = 0;
		StringBuilder sb = new StringBuilder();

		while (qs.hasNext()) {
			CFEExamQuestion q = qs.next();

			if (q.getSourcePage().toLowerCase().matches("3.5[0-9]{2}"))
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

			// List<CFEManualSection> subSections = manual.getManualSectionForPage(beginPage);
			// for (int i = 0; i < subSections.size(); i++) {
			// sb.append(subSections.get(i).name);
			// sb.append((i == subSections.size() - 1) ? " | " : "; ");
			// }

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
