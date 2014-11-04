package financial.fraud.cfe.agent;

import org.junit.Test;

import financial.fraud.cfe.manual.CFEManualSection;

public class CFEManualSectionTest {

	CFEManualSection root;
	CFEManualSection c1;
	CFEManualSection c2;
	CFEManualSection gc1;
	CFEManualSection c3;
	
	@Test
	public void testCFEManualSection() {
		root = new CFEManualSection("root", "root", "999", "test manual", null, "2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual");
		root.begPosition = 1;
		root.endPosition = 1000;

		c1 = new CFEManualSection("root/child 1", "child 1", "999", "test manual", root, "2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual");
		c1.begPosition = 10;
		c1.endPosition = 990;

		c2 = new CFEManualSection("root/child 2", "child 2", "999", "test manual", root, "2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual");
		c2.begPosition = 20;
		c2.endPosition = 880;

		gc1 = new CFEManualSection("root/child 1/grandchild 1", "grandchild 1", "999", "test manual", c1, "2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual");
		gc1.begPosition = 20;
		gc1.endPosition = 880;

		c3 = new CFEManualSection("root/child 3", "child 3", "999", "test manual", root, "2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual");
		c3.begPosition = 40;
		c3.endPosition = 860;

		System.out.println(root);

	}

	@Test
	public void testGetAncestors() {
		System.out.println("\n\ngrandchild ancestors:");
		for (CFEManualSection section : gc1.getAncestors()) {
			System.out.printf("%s : ", section.name);
		}

	}

	@Test
	public void testGetSiblings() {
		System.out.println("\n\nchild 2 siblings:");
		for (CFEManualSection section : c2.getSiblings().values()) {
			System.out.printf("%s : ", section.name);
		}
	}

}
