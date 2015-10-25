package financial.fraud.cfe.manual;

import java.util.List;

/**
 * The CFEManual interface is implemented by any concrete cfe manual class.  Such a concrete class structures the manual as 
 * a tree in memory, where each node represents a section, and breaksp up the manual at varying levels
 * of granularity.
 * 
 * @author joejohnson
 *
 */

public interface CFEManual {

	public CFEManualSection getRoot();
	
	public CFEManualSection getManualSection(String manualSectionId);
	
	public CFEManualSection getManualSectionForQuestionSection(String questionSectionName);
	
	public List<CFEManualSection> getErrors();
	
}
