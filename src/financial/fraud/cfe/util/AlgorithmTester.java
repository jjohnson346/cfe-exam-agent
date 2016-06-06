package financial.fraud.cfe.util;

import java.util.Scanner;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.algorithm.ConceptMatchV3NOTA;
import financial.fraud.cfe.algorithm.IAlgorithm;
import financial.fraud.cfe.algorithm.NoneOfTheAbove;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;
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

	public AlgorithmTester(String questionDir, IAlgorithm algorithm) {
		this.questionDir = questionDir;
		this.qs = new QuestionServer(questionDir);
		this.algorithm = algorithm;

	}

	public void start(boolean interactive) {
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
			// for(int i = 0; i < 1; i++) {
			while (qs.hasNext()) {
				CFEExamQuestion question = qs.next();
				System.out.println("\n\nQuestion " + ++count + " of " + qs.size() + ":");
				System.out.println(question);

				// allow the user to view the question before continuing to the agent's response.
				if (interactive)
					input.nextLine();

				int result = algorithm.solve(question, cfeManual);

				if (result != -1)
					System.out.println("Option selected: " + getFormattedResponse(result, question));
				else
					System.out.println("Option selected:  -1");

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
				if (interactive)
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
		Logger.getInstance().setDetailLevel(DetailLevel.FULL);

		// conceptmatchV1 on profile 4 (def) on test set: accuracy: 7 out of 20 (35%) (from 38% in v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - test set", 4, new ConceptMatchV1());

		// conceptmatchV1 on profile 4 (def) on training set: accuracy: 56 out of 150 (37.3%) (from 36.2% in v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - training set", 4, new ConceptMatchV1());

		// conceptmatchV2 on profile 4 (def) on test set: accuracy: 14 out of 20 (70%) (from 69.2% in v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - test set", 4, new ConceptMatchV2());

		// conceptmatchV2 on profile 4 (def) on training set: accuracy: 101 out of 150 (67.3%) (from 60.2% in v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - training set", 4, new ConceptMatchV2());

		// conceptmatchV3 on profile 4 (def) on test set: accuracy: 15 out of 20 (75%) (from 76.9% in v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - test set", 4, new ConceptMatchV3());

		// conceptmatchV3 on profile 4 (def) on training set: accuracy: 105 out of 150 (70%) (from 61.7% in v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - training set", 4, new ConceptMatchV3());

		// ConceptMatchV3NOTA on profile 4 (def) on test set: accuracy: 15 out of 20 (75%) (from 76.9% in v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - test set", 4, new ConceptMatchV3NOTA());

		// ConceptMatchV3NOTA on profile 4 (def) on training set: accuracy: 105 out of 150 (70%) (from 61.7% in v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - training set", 4, new
		// ConceptMatchV3NOTA());

		// ConceptMatchV3NOTA on profile 68 (def/none of the above) on test set: accuracy: 24 out of 33 (72.7%) (same as
		// v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - test set", 68, new ConceptMatchV3NOTA());

		// ConceptMatchV3NOTA on profile 68 (def/none of the above) on training set: accuracy: 106 out of 162 (65.4%)
		// (from 63.3% in v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - training set", 68, new
		// ConceptMatchV3NOTA());

		// ConceptMatchV3NOT on profile 6 (def/not) on training set: accuracy: 8 out of 27 (29.6%) (from 44.4% in
		// v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - training set", 6, new
		// ConceptMatchV3NOT());

		// ConceptMatchV3NOT on profile 36 (def/except) on training set: accuracy: 1 out of 11 (9%) (from 8.3% in
		// v.2.0.1)
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - training set", 36, new
		// ConceptMatchV3NOT());

		// NoneOfTheAbove on profile 68 (def/none-above) on training set: accuracy: 12 out of 162 (7%)
		AlgorithmTester algoTester = new AlgorithmTester("exam questions - training set", 68, new NoneOfTheAbove());

		// MLPassage algorithm
		// AlgorithmTester algoTester = new AlgorithmTester("exam questions - test set - ml", new MLPassage2());

		algoTester.start(false);
	}
}
