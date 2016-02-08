package financial.fraud.cfe.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;
import java.util.List;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.ir.lucene.LuceneUtil;
import financial.fraud.cfe.manual.CFEManualSection;
import financial.fraud.cfe.manual.CFEManualSmallDocUnitRegex;

public class MLInputFileCreator {

	public static void main(String[] args) {
		QuestionServer qs = new QuestionServer("exam questions - training set", 4);
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
			Formatter output = new Formatter("machine learning" + File.separator + "ml.input.txt");
			output.format("%s", mlData);
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

	}
}
