package financial.fraud.cfe.util;

import financial.fraud.cfe.agent.CFEExamQuestion;


/**
 * an interface for a feature.  All classes representing a question
 * feature must implement this interface.  This makes the system much 
 * more extensible if this convention is followed.
 * 
 * @author jjohnson346
 *
 */
public interface IFeature {
	public boolean exists();
	
	public boolean hasFeature(CFEExamQuestion question);
}
