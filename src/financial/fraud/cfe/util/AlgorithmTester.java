package financial.fraud.cfe.util;

import java.util.HashMap;
import java.util.Scanner;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.algorithm.ConceptMatchV2;
import financial.fraud.cfe.algorithm.ConceptMatchV3;
import financial.fraud.cfe.algorithm.IAlgorithm;
import financial.fraud.cfe.ir.lucene.LuceneUtil;
import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.manual.CFEManualSmallDocUnitRegex;

public class AlgorithmTester {

	private QuestionServer qs;

	private IAlgorithm algorithm;

	private String questionDir;

	private int questionProfile;

	public AlgorithmTester(String questionDir, int questionProfile, IAlgorithm algorithm) {
		this.questionDir = questionDir;
		this.questionProfile = questionProfile;
		this.qs = new QuestionServer(questionDir, questionProfile);
		this.algorithm = algorithm;

	}

	public void start() {
		CFEManual cfeManual = new CFEManualSmallDocUnitRegex();

		Scanner input = new Scanner(System.in);

		// print header.
		System.out.println("Algorithm test:");
		System.out.println("Question Directory: " + questionDir);
		System.out.println("Profile: " + questionProfile + " - " + Profile.getDescription(questionProfile) + "\n\n");

		int count = 0;
		int correctCount = 0;
		int negativeOneCount = 0;
		try {
			while (qs.hasNext()) {
				CFEExamQuestion question = qs.next();
				// System.out.println(q.section);
				System.out.println("\n\nQuestion " + ++count + " of " + qs.size() + ":");
				System.out.println(question);
				if (algorithm instanceof ConceptMatchV2) {
					HashMap<String, String> examSectionLookup = LuceneUtil.getExamSectionLookup();
					ConceptMatchV2 cm2 = (ConceptMatchV2) algorithm;
					cm2.setQuestionSectionName(question.section);
					String examSection = examSectionLookup.get(question.section);
					if (examSection == null)
						throw new Exception("unable to retrieve exam section for question section: " + question.section);
					cm2.setExamSectionName(examSectionLookup.get(question.section));
				}

				if (algorithm instanceof ConceptMatchV3) {
					HashMap<String, String> examSectionLookup = LuceneUtil.getExamSectionLookup();
					ConceptMatchV3 cm3 = (ConceptMatchV3) algorithm;
					cm3.setQuestionSectionName(question.section);
					String examSection = examSectionLookup.get(question.section);
					if (examSection == null)
						throw new Exception("unable to retrieve exam section for question section: " + question.section);
					cm3.setExamSectionName(examSectionLookup.get(question.section));
				}
				// allow the user to view the question before continuing to the agent's response.
				input.nextLine();
				int result = algorithm.solve(question, cfeManual);

				if (result != -1)
					System.out.println("Option selected: " + getFormattedResponse(result, question));

				if (result == question.correctResponse) {
					System.out.println("Correct!\n\n");
					correctCount++;
				} else {
					System.out.println("Incorrect.  Correct answer: " + question.options.get(question.correctResponse)
							+ "\n");
					System.out.println("Explanation: " + question.explanation);
					System.out.println("Manual page: " + question.getSourcePage());
					if (result == -1)
						negativeOneCount++;
				}

				// allow the user to view the results before continuing to next question.
				input.nextLine();
			}
			System.out.println("Total questions answered: " + count);
			System.out.println("Number question correctly answered: " + correctCount + "(" + (double) correctCount
					/ count + ")");
			System.out.println("number of questions where option selected is -1 (no selection): " + negativeOneCount);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}

	public static String getFormattedResponse(int result, CFEExamQuestion question) {
		return String.format("%s%s%s", (char) (result + 97), ") ", question.options.get(result));
	}

	public static void main(String[] args) {
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - test set", 4, new ConceptMatchV2());
		AlgorithmTester algoTester = new AlgorithmTester("exam questions - test set", 4, new ConceptMatchV3());
		algoTester.start();
	}
}
