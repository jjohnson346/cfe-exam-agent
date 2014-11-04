package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TOCSectionFinder {
	public static void main(String[] args) {
		String manual = null;
		// retrieve raw text of CFE Manual.
		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("manual//2014 Fraud Examiners Manual.txt"));
			scanner.useDelimiter("\\Z");
			manual = scanner.next();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
		
//		Pattern tocPattern = Pattern.compile("FRAUD EXAMINERS MANUAL.*?TABLE OF CONTENTS", Pattern.MULTILINE);
		Pattern tocPattern = Pattern.compile("FRAUD EXAMINERS MANUAL\r\n\r\nTABLE OF CONTENTS", Pattern.MULTILINE);
		Matcher tocMatcher = tocPattern.matcher(manual);
		System.out.println(tocMatcher.find());
		System.out.println(tocMatcher.start());
//		System.out.println(manual.indexOf("FRAUD EXAMINERS MANUAL\\n\\nTABLE OF CONTENTS"));

	}
}
