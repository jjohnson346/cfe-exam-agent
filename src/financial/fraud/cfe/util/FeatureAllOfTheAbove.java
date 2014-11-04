package financial.fraud.cfe.util;

import java.util.List;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * FeatureAllOfTheAbove indicates whether the question contains
 * an option for "All of the above".  If so, its exists method returns
 * true, else false.
 * @author jjohnson346
 *
 */
public class FeatureAllOfTheAbove implements IFeature {

	private CFEExamQuestion question;
	
	public FeatureAllOfTheAbove(CFEExamQuestion question) {
		this.question = question;
	}

	/**
	 * returns true if an all of the above question.
	 */
	@Override
	public boolean exists() {
		// call getOptionIndex(), which looks for not only
		// all of the above, but also, I, II, III, and IV.
		return (getOptionIndex() != -1) ? true : false;
	}
	
	/**
	 * returns a non-negative one value if all of the above or
	 * I, II, III, and IV are among the options for the question.
	 * 
	 * @return the index of either all of the above or I, II, III, and IV
	 */
	public int getOptionIndex() {
		int optionIndex = getIndex("all of the above", question);
		if(optionIndex == -1) {
			optionIndex = getIndex("I, II, III, and IV", question);
		}
		return optionIndex;
	}
	
	/**
	 * a utility function for returning the index of a given option for a 
	 * question
	 * 
	 * @param optionText the text of the option being searched for
	 * @param question the CFEExamQuestion object
	 * @return the index of optionText for the question
	 */
	public int getIndex(String optionText, CFEExamQuestion question) {
		List<String> options = question.options;
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).toLowerCase().indexOf(optionText.toLowerCase()) != -1) {
				return i;
			}
		}
		return -1;
	}


}
