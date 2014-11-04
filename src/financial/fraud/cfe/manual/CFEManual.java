package financial.fraud.cfe.manual;

import java.util.List;

public interface CFEManual {

	public CFEManualSection getRoot();
	
	public CFEManualSection getManualSection(String manualSectionId);
	
	public CFEManualSection getManualSectionForQuestionSection(String questionSectionName);
	
	public List<CFEManualSection> getErrors();
	
}
