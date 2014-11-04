package financial.fraud.cfe.agent;

import static org.junit.Assert.assertEquals;

import java.util.Map;
import java.util.logging.Handler;
import java.util.logging.Level;

import org.junit.Test;

import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.manual.CFEManualSection;

public class CFEManualTest {

	@Test
	public void testCFEManual() {
		Handler[] handlers = java.util.logging.Logger.getLogger(CFEManualLargeDocUnit.class.getName()).getHandlers();
		for(int i = 0; i < handlers.length; i++)
			handlers[i].setLevel(Level.FINE);
		java.util.logging.Logger cfeManualLogger = java.util.logging.Logger.getLogger(CFEManualLargeDocUnit.class.getName());
		cfeManualLogger.setLevel(Level.FINE);
		
		CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();

		// print out manual tree.
		System.out.println(cfeManual);

	}

	@Test
	public void testTokenTypeFreqs() {
		// check map of token freqs. - should return 79.
		CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();
		String sectionId = "2011 Fraud Examiners Manual\\Financial Transactions and Fraud Schemes\\ACCOUNTING CONCEPTS";
		CFEManualSection testSection = cfeManual.getManualSection(sectionId);
		Map<String, Integer> tokenTypeFreqs = testSection.getTokenizer().getTokenTypeFreqs();
		int companyFreq = tokenTypeFreqs.get("company");
		assertEquals("company frequency", companyFreq, 79);
	}

}
