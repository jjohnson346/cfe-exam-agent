package financial.fraud.cfe.ir;

import java.util.ArrayList;
import java.util.PriorityQueue;

import financial.fraud.cfe.manual.CFEManualSection;

/**
 * serves as a mechanism for comparing cfe manual sections, using built in functionality
 * for storing a score (based on a scoring algorithm -  tf-idf or cosine similarity tf-idf)
 * and then basing the compareTo() method on this score.  
 * 
 * Note that score is calculated elsewhere and passed into the constructor.  Specifically,
 * for each cfe manual section, the score is calculated in a search engine class 
 * (TFIDFSearchEngine, JaccardTitleSearchEngine, 
 * others?) and then the search engine class instantiates CFEIRNode, passing in the 
 * score it calculated, for later comparison/ranking of cfe manual sections. 
 * 
 * Note that this class is not currently being used, replaced with Counter, CounterMap
 * classes created by Dan Klein and incorporated in this package.
 * 
 * @author jjohnson346
 *
 */
public class CFEIRNode implements Comparable<CFEIRNode> {

	private CFEManualSection element;			// the cfe manual section corresponding to thsi CFEIRNode object

	private double score;						// the score assigned by a search engine class to the section

	/**
	 * 2-arg constructor requires a cfe manual section and a pre-calulated search engine score.
	 * 
	 * @param element		the cfe manual section to which this instance of CFEIRNode corresponds
	 * @param score			the score assigned by a search engine class to this cfe manual section
	 */
	public CFEIRNode(CFEManualSection element, double score) {
		this.element = element;
		this.score = score;
	}

	/**
	 * performs comparison of this CFEIRNode object to another based on the score.
	 */
	@Override
	public int compareTo(CFEIRNode n) {
		return 0 - (new Double(score)).compareTo(n.score);
	}

	/**
	 * returns string representation of CFEIRNode object, including text of the corresponding
	 * cfe manual section and the score assigned to it.
	 */
	@Override
	public String toString() {
		return new String("[" + element.toString() + ", " + score + "]");
	}

	/**
	 * performs unit test of CFEIRNode to verify that these objects can be instantiated,
	 * added to a priority queue object, as defined by Dan Klein's PriorityQueue class 
	 * (in this package).  
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<CFEIRNode> testArray = new ArrayList<CFEIRNode>();
		testArray.add(new CFEIRNode(new CFEManualSection("Joe", "Joe", "1", "10", null, "2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual"), 3.4));
		testArray.add(new CFEIRNode(new CFEManualSection("CJ", "CJ", "1", "10", null, "2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual"), 2.5));
		testArray.add(new CFEIRNode(new CFEManualSection("Naveen", "Naveen", "1", "10", null, "2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual"), 4.5));
		testArray.add(new CFEIRNode(new CFEManualSection("Courtney", "Courtney", "1", "10", null, "2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual"), 0.5));
		testArray.add(new CFEIRNode(new CFEManualSection("Selmer", "Selmer", "1", "10", null, "2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual"), 10.2));
		testArray.add(new CFEIRNode(new CFEManualSection("Kayla", "Kayla", "1", "10", null, "2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual"), 2.5));
		
		
		// print out test array, the array containing these CFEIRNode objects in unsorted order...
		System.out.println("testArray: " + testArray);

		PriorityQueue<CFEIRNode> pq = new PriorityQueue<CFEIRNode>();

		for (CFEIRNode n : testArray) {
			pq.add(n);
		}

		ArrayList<CFEIRNode> sortedArray = new ArrayList<CFEIRNode>();
		int size = pq.size();
		for (int i = 0; i < size; i++) {
			sortedArray.add(pq.poll());
		}
		
		// print out sortedArray, the array containing the CFEIRNode objects in sorted order.
		System.out.println("sortedArray: " + sortedArray);
	}
}
