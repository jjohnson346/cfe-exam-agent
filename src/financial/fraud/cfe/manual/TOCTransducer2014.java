package financial.fraud.cfe.manual;

import java.util.Scanner;


/**
 * encapsulates logic for for processing toc file, using a finite state transducer.
 * 
 * @author jjohnson346
 *
 */
public class TOCTransducer2014 {

	private String contentsLineRegex = "(\\S.*?)(\\s+(\\Q.\\E)+\\s+)([0-9]\\Q.\\E[0-9]+)";
	
	private int currentState;

	private StringBuilder toc;
	
	private String sectionName;
	
	public TOCTransducer2014() {
		reset();
	}

	public void reset() {
		toc = new StringBuilder();
		currentState = 0;
	}

	public void process(String inputLine) throws Exception {
		switch (currentState) {
		case 0:
			transitionFromState0(inputLine);
			break;
		case 1:
			transitionFromState1(inputLine);
			break;
		}
	}

	private void transitionFromState0(String inputLine) throws Exception {

		if (isContentsLine(inputLine)) { // 0,Z -> 0, write contents line to toc

//			System.out.println("0, Z -> 0 : write Z");
			currentState = 0;
			writeContentsLineToTOC(inputLine);

		} else if (isSectionLine(inputLine)) { // 0,S -> 1, set section name

//			System.out.println("0, U -> 4 : sectionName = " + inputLine);
			currentState = 1;
			sectionName = inputLine;
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		}
	}

	private void transitionFromState1(String inputLine) throws Exception {

		if (isContentsLine(inputLine)) { // 0,Z -> 0, write section line, contents line to toc

//			System.out.println("0, Z -> 0 : write Z");
			currentState = 0;
//			String pageNumber = getPageNumber(inputLine);
			
			writeSectionLineToTOC(inputLine);
			writeContentsLineToTOC(inputLine);
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		}
	}

	// note the only two characters for the input alphabet for this transducer are
	// 
	// 1. Z - contents line 
	// 2. S - section line
	// 
	// This is unlike the 2011 version of
	// transducer where we have lower case line, cont. line, upper case line.  This
	// represents a simplification afforded to us by the fact that the 2014 version of
	// the phase 1 compiler eliminates the header lines prior to processing by transducer.
	
	private boolean isContentsLine(String inputLine) {
		// this regex is different from the TOCTransducer 2011 version - a bit more refined, that is.
		return inputLine.matches(contentsLineRegex);
	}

	private boolean isSectionLine(String inputLine) {
		return !isContentsLine(inputLine);
	}
	
	public String getTOC() {
		return new String(toc);
	}

	private String getPageNumber(String contentsLine) {
		return contentsLine.replaceAll(contentsLineRegex, "$4");
	}
	
	private String getName(String contentsLine) {
		return contentsLine.replaceAll(contentsLineRegex, "$1");
	}
	
	private void writeContentsLineToTOC(String contentsLine) {
		String pageNumber = getPageNumber(contentsLine);
		String name = getName(contentsLine);
		toc.append(contentsFormat(name, pageNumber));
	}

	private void writeSectionLineToTOC(String contentsLine) {
		String pageNumber = getPageNumber(contentsLine);
		String sectionPageNumber =  pageNumber.substring(0, pageNumber.length() - 2) + "01";
		toc.append(contentsFormat(sectionName, sectionPageNumber));
	}

	private String contentsFormat(String name, String pageNumber) {
		final int LINE_CHAR_COUNT = 100;
		final int MIN_DOTS_COUNT = 5;

		int dotsCount = LINE_CHAR_COUNT - name.length() - pageNumber.length() - 2;
		if (dotsCount < 2) {
			dotsCount = MIN_DOTS_COUNT;
		}
		return name + " " + dotsSequence(dotsCount) + " " + pageNumber + "\n";
	}

	private String dotsSequence(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append('.');
		}
		return new String(sb);
	}
}
