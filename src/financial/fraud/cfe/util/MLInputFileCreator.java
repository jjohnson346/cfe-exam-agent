package financial.fraud.cfe.util;

import financial.fraud.cfe.agent.CFEExamQuestion;

public class MLInputFileCreator {

	public static void main(String[] args) {
		QuestionServer qs = new QuestionServer("exam questions - training set", 4);

		int count = 0;
		while (qs.hasNext()) {
			CFEExamQuestion q = qs.next();
			System.out.print(count++ + " | ");
			System.out.print(q.name + " | ");
			System.out.print(q.stem + " | ");
			System.out.println(q.options.get(q.correctResponse));
			// System.out.println(q.section);
			// System.out.println("Question " + ++count + " of " + qs.size() + ":");
			// System.out.println(q);
			// System.out.println(q.getFormattedCorrectResponse());
			// System.out.println();
			// input.nextLine();
		}
		System.out.println("count: " + count);
	
	}
}
