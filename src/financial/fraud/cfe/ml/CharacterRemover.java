package financial.fraud.cfe.ml;

import org.apache.commons.lang3.StringUtils;

public class CharacterRemover {

	public static void main(String[] args) {
		String test = "test123";
		System.out.println("test = " + test);
		test = StringUtils.remove(test, (char)49);
		System.out.println("test = " + test);
	}
}
