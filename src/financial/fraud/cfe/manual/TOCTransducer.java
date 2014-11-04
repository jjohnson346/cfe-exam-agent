package financial.fraud.cfe.manual;

import java.util.Scanner;

/**
 * encapsulates logic for for processing toc file, using a finite state transducer.
 * 
 * @author jjohnson346
 *
 */
public class TOCTransducer {

	private int currentState;

	private StringBuilder toc;
	
	private String sectionName;

	private String multiLineName;

	public TOCTransducer() {
		reset();
	}

	public void reset() {
		toc = new StringBuilder();
		currentState = 0;
	}

	public void process(String inputLine) throws Exception {
		System.out.println("executing process() for: " + inputLine);
		System.out.println("current state: " + currentState);
		
		switch (currentState) {
		case 0:
			transitionFromState0(inputLine);
			break;
		case 1:
			transitionFromState1(inputLine);
			break;
		case 2:
			transitionFromState2(inputLine);
			break;
		case 3:
			transitionFromState3(inputLine);
			break;
		case 4:
			transitionFromState4(inputLine);
			break;
		case 5:
			transitionFromState5(inputLine);
			break;
		case 6:
			transitionFromState6(inputLine);
			break;
		case 7:
			transitionFromState7(inputLine);
			break;
		case 8:
			transitionFromState8(inputLine);
			break;
		case 9:
			transitionFromState9(inputLine);
			break;
		case 10:
			transitionFromState10(inputLine);
			break;
		}
	}

	private void transitionFromState0(String inputLine) throws Exception {

		if (isContentsLine(inputLine)) { // 0,Z -> 0, write contents line to toc

			System.out.println("0, Z -> 0 : write Z");
			currentState = 0;
			writeContentsLineToTOC(inputLine);

		} else if (isUpperCase(inputLine)) { // 0,U -> 4, set section name

			System.out.println("0, U -> 4 : sectionName = " + inputLine);
			currentState = 4;
			sectionName = inputLine;
		
		} else if (isLowerCase(inputLine)) { // 0,L -> 1, set multi-line
		
			System.out.println("0, L -> 1 : multiLine = " + inputLine);
			currentState = 1;
			multiLineName = inputLine;
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		}
	}

	private void transitionFromState1(String inputLine) throws Exception {
		
		if (isContentsLine(inputLine)) { // 1,Z -> 0 : write multi-line
		
			System.out.println("1, Z -> 0 : write multiLine");
			currentState = 0;
			writeMultiLineToTOC(inputLine);
		
		} else if (isUpperCase(inputLine)) { // 1,U -> 2
		
			System.out.println("1, U -> 2");
			currentState = 2;
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		}
	}

	private void transitionFromState2(String inputLine) throws Exception {
		
		if (isUpperCase(inputLine)) { // 2,U -> 3, set section
		
			System.out.println("2, U -> 3 : section = " + inputLine);
			currentState = 3;
			sectionName = inputLine;
		
		} else if (isLowerCase(inputLine)) { // 2,L -> 1
		
			System.out.println("2, L -> 1");
			currentState = 1;
		
		} else if (isContinue(inputLine)) { // 2,C -> 10
		
			System.out.println("2, C -> 10");
			currentState = 10;
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		}
	}

	private void transitionFromState3(String inputLine) throws Exception {
		
		if (isContentsLine(inputLine)) { // 3,Z -> 0, write contents line
		
			System.out.println("3, Z -> 0 : write section, write z.");
			currentState = 0;
			writeSectionLineToTOC(inputLine);
			writeContentsLineToTOC(inputLine);
		
		} else if (isUpperCase(inputLine)) { // 3,U -> 4, set section name
		
			System.out.println("3, U -> 4 : section = " + inputLine);
			currentState = 4;
			sectionName = inputLine;
		
		} else if (isContinue(inputLine)) { // 3,C -> 10
		
			System.out.println("3, C -> 10");
			currentState = 10;
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		}
	}

	private void transitionFromState4(String inputLine) throws Exception {
		
		if (isContentsLine(inputLine)) { // 4,Z -> 0, write section line and contents line.
		
			System.out.println("4, Z -> 0 : write section, Z.");
			currentState = 0;
			writeSectionLineToTOC(inputLine);
			writeContentsLineToTOC(inputLine);
		
		} else if (isLowerCase(inputLine)) { // 4,L -> 8
		
			System.out.println("4, L -> 8");
			currentState = 8;
		
		} else if (isUpperCase(inputLine)) { // 4,U -> 5, set section name
		
			System.out.println("4, U -> 5");
			currentState = 5;
			sectionName = inputLine;
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		
		}
	}

	private void transitionFromState5(String inputLine) throws Exception {
		
		if (isContentsLine(inputLine)) { // 5,Z -> 0, write section line and contents line.
		
			System.out.println("5, Z -> 0 : write section, Z.");
			currentState = 0;
			writeSectionLineToTOC(inputLine);
			writeContentsLineToTOC(inputLine);
		
		} else if (isUpperCase(inputLine)) { // 5,U -> 6
		
			System.out.println("5, U -> 6 : section = " + inputLine);
			currentState = 6;
			sectionName = inputLine;
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		}
	}

	private void transitionFromState6(String inputLine) throws Exception {
		
		if (isContentsLine(inputLine)) { // 6,Z -> 0, write section line and contents line.
		
			System.out.println("6, Z -> 0 : write section, Z.");
			currentState = 0;
			writeSectionLineToTOC(inputLine);
			writeContentsLineToTOC(inputLine);
		
		} else if (isUpperCase(inputLine)) { // 6,U -> 7
		
			System.out.println("6, U -> 7 : section = " + inputLine);
			currentState = 7;
			sectionName = inputLine;
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		
		}
	}
	
	private void transitionFromState7(String inputLine) throws Exception {
		
		if (isContentsLine(inputLine)) { // 7,Z -> 0, write section line and contents line.
		
			System.out.println("7, Z -> 0 : write section, Z.");
			currentState = 0;
			writeSectionLineToTOC(inputLine);
			writeContentsLineToTOC(inputLine);
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		}
	}

	private void transitionFromState8(String inputLine) throws Exception {
		
		if (isUpperCase(inputLine)) { // 8,U -> 9, set section name
		
			System.out.println("8, U -> 9 : section = " + inputLine);
			currentState = 9;
			sectionName = inputLine;
		
		} else if (isContinue(inputLine)) { // 8,C -> 10
		
			System.out.println("8, C -> 10");
			currentState = 10;
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		
		}
	}

	private void transitionFromState9(String inputLine) throws Exception {
		
		if (isContentsLine(inputLine)) { // 9,Z -> 0, write section line and contents line to toc
		
			System.out.println("9, Z -> 0 : write section, Z.");
			currentState = 0;
			writeSectionLineToTOC(inputLine);
			writeContentsLineToTOC(inputLine);
		
		} else if (isContinue(inputLine)) { // 9,C -> 10
		
			System.out.println("9, C -> 10");
			currentState = 10;
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		
		}
	}

	private void transitionFromState10(String inputLine) throws Exception {
		
		if (isContentsLine(inputLine)) { // 10,Z -> 0, write contents line to toc
		
			System.out.println("10, Z -> 0 : write Z.");
			currentState = 0;
			writeContentsLineToTOC(inputLine);
		
		} else if (isLowerCase(inputLine)) { // 10,L -> 1, set multi-line
		
			System.out.println("10, L -> 1 : multiLine = " + inputLine);
			currentState = 1;
			multiLineName = inputLine;
		
		} else {
		
			throw new Exception(inputLine + " cannot be parsed.");
		
		}
	}

	private boolean isContentsLine(String inputLine) {
		return inputLine.matches(".*(\\s*(\\Q.\\E){2,}\\s*)+.*");
	}

	private boolean isUpperCase(String inputLine) {
		return inputLine.toUpperCase().equals(inputLine) && !inputLine.toUpperCase().contains("(CONT.)");
	}

	private boolean isContinue(String inputLine) {
		return inputLine.toUpperCase().contains("(CONT.)");
	}

	private boolean isLowerCase(String inputLine) {
		return !isContentsLine(inputLine) && !isUpperCase(inputLine) && !isContinue(inputLine);
	}

	public String getTOC() {
		return new String(toc);
	}

	private void writeContentsLineToTOC(String contentsLine) {
		Scanner lineScanner = new Scanner(contentsLine);

		// specify the delimiter as a sequence of dots with some occasional spaces
		// in between - the spaces resulted from some hiccups during the pdf-text
		// conversion.

		lineScanner.useDelimiter("(\\s*(\\Q.\\E){2,}\\s*)+");

		String name = lineScanner.next();
		String pageNumber = scrubPageNumber(lineScanner.next());
		toc.append(contentsFormat(name, pageNumber));
	}

	private void writeSectionLineToTOC(String inputLine) {
		Scanner lineScanner = new Scanner(inputLine);

		// specify the delimiter as a sequence of dots with some occasional spaces
		// in between - the spaces resulted from some hiccups during the pdf-text
		// conversion.

		lineScanner.useDelimiter("(\\s*(\\Q.\\E){2,}\\s*)+");

		lineScanner.next(); // won't be using the name here, so skip it...
		String sectionPageNumber = getSectionPageNumber(scrubPageNumber(lineScanner.next()));
		toc.append(contentsFormat(sectionName, sectionPageNumber));
	}

	/**
	 * removes any spaces and any leading dots (...) from the page number.
	 * 
	 * @param rawPageNumber
	 * @return page number
	 */
	private String scrubPageNumber(String rawPageNumber) {
		String pageNumber = rawPageNumber.replace(" ", "");
		while (pageNumber.charAt(0) == '.')
			pageNumber = pageNumber.substring(1);
		return pageNumber;
	}

	/**
	 * returns the page number on which the section for the page provided as an input argument starts
	 * 
	 * @param pageNumber
	 * @return section page number
	 */
	private String getSectionPageNumber(String pageNumber) {
		return pageNumber.substring(0, pageNumber.length() - 2) + "01";
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

	private void writeMultiLineToTOC(String inputLine) {
		Scanner lineScanner = new Scanner(inputLine);

		// specify the delimiter as a sequence of dots with some occasional spaces
		// in between - the spaces resulted from some hiccups during the pdf-text
		// conversion.

		lineScanner.useDelimiter("(\\s*(\\Q.\\E){2,}\\s*)+");

		String name = multiLineName + " " + lineScanner.next().trim();
		String pageNumber = scrubPageNumber(lineScanner.next());
		toc.append(contentsFormat(name, pageNumber));
	}

	private String dotsSequence(int count) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < count; i++) {
			sb.append('.');
		}
		return new String(sb);
	}
}
