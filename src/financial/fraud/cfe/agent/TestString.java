package financial.fraud.cfe.agent;

/**
 * TestString simply shows the way in which String objects are handled in memory 
 * when two references are pointing to the similar strings, in particular, when one
 * reference is pointing to a particular string object, (in this case, "Hello World"), 
 * and another points to an object that is a substring of the first object, (in this
 * case, the first 5 letters of "Hello World", or, "Hello").
 * 
 * Notice here that the output shows that when test1 is reassigned to a new
 * object, there's no effect on the object referenced by test2.  This implies that
 * test1 and test2 are referencing two different objects when they are first assigned,
 * (as opposed to both of them
 * pointing to the same object with some termination signal for test2 indicating only
 * the first 5 letters).
 * 
 * 
 * 
 * 
 * @author jjohnson346
 *
 */
public class TestString {
	public static void main(String[] args) {
		String test1 = "Hello world";
		String test2 = test1.substring(0, 5);

		System.out.println(test1);
		System.out.println(test2);

		test1 = "Goodbye!";

		System.out.println(test1);
		System.out.println(test2);
	}
}
