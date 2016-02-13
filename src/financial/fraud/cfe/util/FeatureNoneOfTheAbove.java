package financial.fraud.cfe.util;

import java.util.List;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * FeatureNoneOfTheAbove encapsulates the logic for determining whether the 
 * question has a none of the above option.
 * 
 * @author jjohnson346
 *
 */
public class FeatureNoneOfTheAbove implements IFeature {

	private CFEExamQuestion question;
	
	public FeatureNoneOfTheAbove(CFEExamQuestion question) {
		this.question = question;
	}

	public FeatureNoneOfTheAbove() {}

	/**
	 * return true if none of the above option exists.
	 */
	@Override
	public boolean exists() {
		return (getOptionIndex() != -1) ? true : false;
	}
	
	public int getOptionIndex() {
		return getIndex("none of the above", question);
		}

	/**
	 * utility function for determining the index of an option containing the option text
	 * passed in as an input parm.
	 * 
	 * @param optionText the text of the option being searched for
	 * @param question the CFEExamQuestion for which to search for the option
	 * @return the index of the option in the question
	 */
	private int getIndex(String optionText, CFEExamQuestion question) {
		List<String> options = question.options;
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).toLowerCase().indexOf(optionText.toLowerCase()) != -1) {
				return i;
			}
		}
		return -1;
	}

	@Override
	public boolean hasFeature(CFEExamQuestion question) {
		this.question = question;
		return exists();
	}

}
