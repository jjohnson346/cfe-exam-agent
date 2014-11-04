package financial.fraud.cfe.ir;

import java.util.Comparator;

/**
 * used by code in Node to do sort nodes in descending order.  
 * 
 * This code is not being used by search engine classes, currently.
 * 
 * @author jjohnson346
 *
 * @param <E>
 */
public class ReverseComparator<E extends Comparable<E>> implements Comparator<E> {
	
	public int compare(E o1, E o2) {
		return 0 - o1.compareTo(o2);
	}
}
