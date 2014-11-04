package financial.fraud.cfe.agent;

import org.junit.Test;

public class CFEExamTest {

	@Test
	public void testCFEExam() {
		CFEExam cfeExam = new CFEExam("AA Sample Exam 1.txt");
		System.out.printf("\n\n%s", cfeExam);
		
		
		for(int i = 0; i < cfeExam.size(); i++) {
			CFEExamQuestion question = cfeExam.getQuestion(i);
			System.out.printf("%s%d%s%s\n", "Question ", i, ":  ", question);
			System.out.printf("%s%s\n", "Formatted Correct Response:  ", question.getFormattedCorrectResponse());
			System.out.printf("%s%s\n", "Section:  ", question.section);
			System.out.printf("%s%s\n", "Stem:  ", question.stem);

			System.out.printf("%s\n", "Options:  ");
			for(int j = 0; j < question.options.size(); j++) {
				System.out.printf("%d%s%s\n", j, ": ", question.options.get(j));
			}
			
			System.out.printf("%s%s\n\n\n", "Correct Response:  ", question.correctResponse);
		}
	}

}
