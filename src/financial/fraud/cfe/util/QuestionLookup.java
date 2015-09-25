package financial.fraud.cfe.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import financial.fraud.cfe.agent.CFEExamQuestion;

public class QuestionLookup {

	public static void main(String[] args) {
		Map<String, LinkedList<CFEExamQuestion>> qLookup = new HashMap<String, LinkedList<CFEExamQuestion>>();
		QuestionServer qs = new QuestionServer("exam questions - all");
		while(qs.hasNext()) {
			CFEExamQuestion q = qs.next();
//			System.out.println(q);
			LinkedList<CFEExamQuestion> matchingQuestions = qLookup.get(q.stem);
			if(matchingQuestions != null) {
				matchingQuestions.add(q);
			} else { 
				matchingQuestions = new LinkedList<CFEExamQuestion>();
				matchingQuestions.add(q);
				qLookup.put(q.stem, matchingQuestions);
			}
			
		}

		Scanner input = new Scanner(System.in);
		String questionStem;
		while(!(questionStem = input.nextLine()).equals("exit")) {
			List<CFEExamQuestion> matchingQuestions = qLookup.get(questionStem);
			System.out.println(matchingQuestions);
		}
	}
}
