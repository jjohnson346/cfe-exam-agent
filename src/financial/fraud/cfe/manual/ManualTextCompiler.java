package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;

/**
 * ManualTextCompiler is an abstract class to be extended by the compiler component classes
 * that contribute to the cleaning of the manual text data.  Recall that the manual, just after
 * conversion from pdf to text, is riddled with erroneous spaces inserted by the pdf conversion
 * process (using pdfbox).  For example, consider the following text block generated by the 
 * pdfbox component:  
 * 
 * 
 * 
 * U s e r s of Financi a l Statement s 
 * Financial statement fraud schemes are most often perpetrated by management against 
 * potential users of the statements. These users of financial statements include company 
 * ownership and management, lending organizations, investors, regulating bodies, vendors, 
 * and customers. The production of truthful financial statements plays an important role in 
 * the continued success of an organization. However, fraudulent statements can be used for a 
 * number of reasons. The most common use is to increase the apparent prosperity of an 
 * organization in the eyes of potential and current investors. (For more information, see the 
 * chapter on “Financial Statement Fraud.”)  
 *
 *
 * Financial Transactions  Accounting Concepts 
 * 2011 Fraud Examiners Manual   1.115 
 * Generally Accepted Accounting Pr inciples (GAAP) 
 * In preparing financial statements, management, accountants, and auditors are charged with
 * 
 * 
 * 
 * Notice the insertion of spaces in the phrase, "Users of Financial Statements", and in 
 * the phrase, "Generally Accepted Accounting Principles (GAAP)".  These are examples of 
 * poor results caused by an inability to properly convert text of a heading-type font.
 * Thus, the component compiler classes in this package attempt to remove the erroneous
 * spaces, (while not removing the ones that belong!), all of which provide an 
 * implementation of the following methods:  1) getSourceManualText() and 
 * 2) generateTargetManualText().  getSourceManualText() implementations depend 
 * on the the phase of the compiler (accepting the output text from the prior 
 * phase compiler.  generateTargetManualText() implementations carry out 
 * step-wise process for removing the blanks.
 *
 * @author jjohnson346
 *
 */
public abstract class ManualTextCompiler {

	protected String phase;
	
	/**
	 * performs the translation process for the compiler.
	 */
	public void compile() {
		String sourceManualText = getSourceManualText();
		String targetManualText = generateTargetManualText(sourceManualText);
		writeToFile(targetManualText);
	}

	/**
	 * abstract method to be implemented in sub-classes that implement
	 * compiler functionality
	 * 
	 * @return a string containing the source text to be translated by this compiler
	 */
	protected abstract String getSourceManualText();

	/**
	 * abstract method to be implemented in sub-classes, performs the translation
	 * of the source text to a target text.
	 * 
	 * @param sourceManualText		the source text to be translated
	 * 
	 * @return						the target code generated by the implementing class
	 */
	protected abstract String generateTargetManualText(String sourceManualText);

	protected void writeToFile(String target) {
		
		String targetFileName = "2011_fem_manual_text_phase_" + phase + ".txt";
		
		try {
			Formatter f = new Formatter(new File(targetFileName));
			for (int i = 0; i < target.length(); i += 2000) {
				if (i + 2000 > target.length())
					f.format("%s", target.substring(i, target.length()));
				else
					f.format("%s", target.substring(i, i + 2000));
				f.flush();
			}
			f.close();
		} catch (FileNotFoundException e) {
			System.out.println("File creation failed.");
		}
	}
}
