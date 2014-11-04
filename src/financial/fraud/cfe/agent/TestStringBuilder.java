package financial.fraud.cfe.agent;

/**
 * TestStringBuilder simply shows the way in which String objects are handled in memory 
 * by the StringBuilder class when two StringBuilder references are pointing to 
 * similar strings, in particular, when one
 * reference is pointing to a particular StringBuilder object, (in this case, "Hello World"), 
 * and another points to an object that built from the first, (in this case, by inserting
 * "Oh " in front front of the first StringBuilder).
 * 
 * Notice here that the output shows that when test1 is reassigned to a new
 * object, there's no effect on the object referenced by test2.  This implies that
 * test1 and test2 are referencing two different objects when they are first assigned,
 * (as opposed to both of them
 * pointing to the same object such that modifications to the object of one reference
 * are automatically reflected in the second object (since they're the same object)).
 * 
 * 
 * 
 * 
 * @author jjohnson346
 *
 */
public class TestStringBuilder {
	public static void main(String[] args) {
		StringBuilder test1 = new StringBuilder("Hello world");
		StringBuilder test2 = new StringBuilder(test1);

		System.out.println(test1);
		System.out.println(test2);

		test1.insert(0, "Oh "); 

		System.out.println(test1);
		System.out.println(test2);
		
		test2.replace(0, 2, "Joe");
		
		System.out.println(test1);
		System.out.println(test2);
	}
}
