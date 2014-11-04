package financial.fraud.cfe.util;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import financial.fraud.cfe.agent.CFEExamQuestion;

/**
 * This class encapsulates the logic for whether this is a particular type of 
 * true/false question.  (Note FeatureTrueFalseAbsolute extends FeatureTrueFalse.)
 * Clearly, if the question is not a true/false question to begin with, then the 
 * logic for this class returns false.  However, if the qeustion is a true/false 
 * question, then this class tests for the presence of certain key words in the 
 * stem of the question - "always", "never", "only", and "must".  (More words may be 
 * added easily to the logic in this class.)
 * 
 * @author jjohnson346
 *
 */
public class FeatureTrueFalseAbsolute extends FeatureTrueFalse implements IFeature {

	public FeatureTrueFalseAbsolute(CFEExamQuestion question) {
		super(question);
	}
	
	@Override
	public boolean exists() {
		// first, test whether this is a true/false question.
		if(!super.exists())
			return false;
		
		// this is a true false question, so now test whether absolute exists in stem.
		ArrayList<String> absRegExes = new ArrayList<String>();
		absRegExes.add("[^A-Za-z]always[^A-Za-z]");
		absRegExes.add("[^A-Za-z]never[^A-Za-z]");
//		absRegExes.add("(^not)[^A-Za-z]only[^A-Za-z]");
		absRegExes.add("[^A-Za-z]only[^A-Za-z]");
		absRegExes.add("[^A-Za-z]must[^A-Za-z]");
		
		for(String s : absRegExes) {
			Pattern p = Pattern.compile(s);
			Matcher m = p.matcher(question.stem);
			if(m.find())
				return true;
		}
		
		return false;
	}
}
