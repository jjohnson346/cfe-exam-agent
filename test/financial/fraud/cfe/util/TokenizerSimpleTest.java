package financial.fraud.cfe.util;

import java.util.Collection;

import org.junit.Test;

import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.manual.CFEManualSection;

public class TokenizerSimpleTest {

	@Test
	public void testTokenize() {
		TokenizerSimple ts = new TokenizerSimple();

		CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();
		Collection<CFEManualSection> sections = cfeManual.getRoot().getSubTreeAsList();
		int count = 0;
		for (CFEManualSection s : sections) {
			String manualSectionText = s.getText();
			ts.tokenize(manualSectionText);

			// output type freqs.
			System.out.println(ts);
			if (++count == 1)
				break;
		}
	}

}
