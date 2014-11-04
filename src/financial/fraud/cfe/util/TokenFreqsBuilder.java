package financial.fraud.cfe.util;

import java.io.FileNotFoundException;
import java.util.Collection;
import java.util.Formatter;

import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.manual.CFEManualSection;

/**
 * TokenFreqsBuilder tokenizes each section of the cfe manual and saves the contents of the 
 * frequency counts restuls of each tokenization in a separate file.  The tokenzation step is 
 * done using the TokenizerSimple class.  
 * 
 * A critical component of this process is the CFEManual class, which contains the text of the
 * manual, organized into sections, each of which are tokenized.
 * 
 * This class is predominately used for testing purposes, to make sure the tokenization frequency
 * counts are reasonable.  This class is not used directly, however, by the cfe exam agent software.
 * 
 * @author jjohnson346
 *
 */
public class TokenFreqsBuilder {

	/**
	 * performs the tokenization for each section of the cfe manual, writing the token frequency
	 * counts to an output file into a separate file for each section.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TokenizerSimple ts = new TokenizerSimple();
		Logger logger = Logger.getInstance();
		logger.setDetailLevel(DetailLevel.MEDIUM);
		logger.addDestination("logs\\token freq builder.log");
		
		logger.println("Token Frequency Analysis system initializing...", DetailLevel.MINIMAL);
		
		CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();
		
		logger.println("TokenFreqsBuilder ready.", DetailLevel.MEDIUM);
		
//		Collection<CFEManualSection> sections = cfeManual.getManualSections();
		Collection<CFEManualSection> sections = cfeManual.getRoot().getSubTreeAsList();
		int count = 0;
		
		logger.println("Commencing tokenization sequence for all CFE Manual sections...", DetailLevel.MEDIUM);

		for (CFEManualSection s : sections) {
			
			logger.println("Processing section, " + s.name + " (item " + ++count + " of " + sections.size() + ")...", DetailLevel.MEDIUM);

			String manualSectionText = s.getText();
			ts.tokenize(manualSectionText);

			// output token freqs in file.
			try {
				Formatter output = new Formatter("token freqs\\" + s.name + " simple token freqs.txt");
				output.format("%s", ts);
				output.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			
			logger.println("Processing of section, " + s.name + " complete.", DetailLevel.MEDIUM);
			
		}
		
		logger.println("Tokenization sequence complete.", DetailLevel.MINIMAL);
		logger.println("Process terminated successfully.", DetailLevel.MINIMAL);
	}
}
