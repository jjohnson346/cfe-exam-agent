package financial.fraud.cfe.agent;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegExTester {
	
	private void outputPageLines() {
		String manualText = getManualText();
		String toc = getTOC();
		
		Scanner tocScanner = new Scanner(toc);
		
		while(tocScanner.hasNextLine()) {
			Scanner lineScanner = new Scanner(tocScanner.nextLine());
			lineScanner.useDelimiter("\\.{5,}");

			String sectionName = lineScanner.next();
			String pageNumber = lineScanner.next().trim();
			final String PAGE_LINE_REGEX = "(^2011 Fraud Examiners Manual\\s+?" + pageNumber + "\\s*?$|^" + pageNumber + "\\s+?2011 Fraud Examiners Manual\\s+?)";
			
			Pattern pagePattern = Pattern.compile(PAGE_LINE_REGEX, Pattern.MULTILINE);
			Matcher pageMatcher = pagePattern.matcher(manualText);

			System.out.print(sectionName + " - " + pageNumber + ": ");
			
			if(pageMatcher.find()) {
				int pagePos = pageMatcher.start();
				String pageLine = pageMatcher.group();
				System.out.println("found at " + pagePos + ": " + pageLine);
			} else {
				System.out.println("NOT FOUND.");
			}
			
		}
	}
	
	void outputSectionLine() {
		String manualText = getManualText();

		final String PAGE_LINE_REGEX = "(^2011 Fraud Examiners Manual\\s+?" + "1.512" + "\\s*?$|^" + "1.512" + "\\s+?2011 Fraud Examiners Manual\\s+?)";
		
		Pattern pagePattern = Pattern.compile(PAGE_LINE_REGEX, Pattern.MULTILINE);
		Matcher pageMatcher = pagePattern.matcher(manualText);

		int pagePos = 0;
		
		if(pageMatcher.find()) {
			pagePos = pageMatcher.start();
			String pageLine = pageMatcher.group();
			System.out.println("page found at " + pagePos + ": " + pageLine);
		} else {
			System.out.println("PAGE NOT FOUND.");
		}
		
		final String SECTION_LINE_REGEX = "^" + "To Whom Is the Check Made Payable\\?" + "\\s*?$";
		
		Pattern sectionPattern = Pattern.compile(SECTION_LINE_REGEX, Pattern.MULTILINE);
		Matcher sectionMatcher = sectionPattern.matcher(manualText);

		int sectionPos;
		
		if(sectionMatcher.find(pagePos)) {
			sectionPos = sectionMatcher.start();
			String sectionLine = sectionMatcher.group();
			System.out.println("section found at " + sectionPos + ": " + sectionLine);
		} else {
			System.out.println("SECTION NOT FOUND.");
		}

	}
	
	private String getManualText() {
		String manual = null;
		Scanner scanner = null;

		try {
			scanner = new Scanner(new File("manual//2011_zz_fem_manual_text.txt"));
			scanner.useDelimiter("\\Z");
			manual = scanner.next();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
		return manual;
	}
	
	private String getTOC() {
		String toc = null;
		Scanner scanner = null;

		try {
			scanner = new Scanner(new File("manual//2011_zz_fem_toc.txt"));
			scanner.useDelimiter("\\Z");
			toc = scanner.next();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
		return toc;
	}


	
	public static void main(String[] args) {
		RegExTester r = new RegExTester();
		r.outputPageLines();
		r.outputSectionLine();
	}

	
	
}
