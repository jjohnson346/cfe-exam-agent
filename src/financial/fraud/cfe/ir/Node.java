package financial.fraud.cfe.ir;

import java.util.ArrayList;
import java.util.PriorityQueue;


/**
 * implements a Node with the ability to be compared to another node, based on 
 * score that as passed into constructor.  Looks suspiciously like CFEIRNode, but 
 * providing a more general implementation of the comparable functionality.
 * 
 * Note, however, that it appears this class, like the CFEIRNode, is not being
 * used in the search engine classes, which use the Counter, CounterMap classes instead
 * for storing scores along with cfe manual section objects.
 * 
 * @author jjohnson346
 *
 * @param <E>
 */
public class Node<E> implements Comparable<Node<E>> {

	private E element;

	private double score;

	/**
	 * 2-arg constructor, including element (presumably cfe manual section), and score
	 * 
	 * @param element		could be any class, but likely CFEManualSection used
	 * @param score			the score for doing comparisons, likely from search engine classes
	 */
	public Node(E element, double score) {
		this.element = element;
		this.score = score;
	}

	/**
	 * executes comparison based on score.
	 */
	@Override
	public int compareTo(Node<E> n) {
		return new Double(score).compareTo(n.score);
	}

	@Override
	public String toString() {
		return new String("[" + element.toString() + ", " + score + "]");
	}

	/**
	 * unit test for testing to making sure order is preserved as nodes are
	 * instantiated with scores and placed in priority queue.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<Node<String>> testArray = new ArrayList<Node<String>>();
		testArray.add(new Node<String>("Joe", 3.4));
		testArray.add(new Node<String>("CJ", 2.5));
		testArray.add(new Node<String>("Naveen", 4.5));
		testArray.add(new Node<String>("Courtney", 0.5));
		testArray.add(new Node<String>("Selmer", 10.2));
		testArray.add(new Node<String>("Kayla", 2.5));
		System.out.println(testArray);

		PriorityQueue<Node<String>> pq = new PriorityQueue<Node<String>>(1000, new ReverseComparator<Node<String>>());

		for (Node<String> n : testArray) {
			pq.add(n);
		}

		ArrayList<Node<String>> sortedArray = new ArrayList<Node<String>>();
		int size = pq.size();
		for (int i = 0; i < size; i++) {
			sortedArray.add(pq.poll());
		}
		System.out.println(sortedArray);
	}
}
