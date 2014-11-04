package financial.fraud.cfe.util;

import java.util.LinkedList;
import java.util.List;

/**
 * The QuestionText class represents the text for a particular exam question, including
 * all of the elements of a question - the section, stem, options, correct response, and
 * explanation.  There are also members for storing the beginning position and ending
 * position of the question text within a larger text from a file output from ocr software,
 * from which the instance of QuestionText was created.
 * 
 *    *****IMPORTANT******
 * This class acts as the interface to the raw ocr data from which exam question data is 
 * extracted and structured.  So, something to keep in mind here is that if a new ocr 
 * software is used, it is likely this class will need to be modified or replaced.  It 
 * would probably be wise to modify the interface of this class as little as possible, 
 * modifying only the implementations within these functions, thereby limiting the 
 * changes necessary in the code to the functions within this file.
 * 
 * @author jjohnson346
 *
 */
public class QuestionText {

	public int begPosition;			// the beginning position of the question in the raw ocr output file
	public int endPosition;			// the ending position of the question in the raw ocr output file

	private String section;			// the section of the question
	private int count;				// sequence number of the question, for naming the file for storing the question

	private String stem;				// the stem of the question
	private List<String> options;	// the multiple choice options for the question
	private int correctResponse;		// the correct choice among the options
	private String explanation;		// the text of the explanation that typically follows the question
	private String text;				// the entire text for the question, incl. all elements of the question
	private int stemBegPos;			// the position where the stem of the question begins
	private int stemEndPos;			// the position where the stem of the question ends
	private int optionsBegPos;		// the position where the options begin
	private int optionsEndPos;		// the position where the options end
	private int explanationBegPos;	// where the explanation begins
	private int explanationEndPos;	// where the explanation ends

	/**
	 * constructor initializes the section and sequence number for the question (count).
	 * 
	 * @param sectionName
	 * @param count
	 */
	public QuestionText(String sectionName, int count) {
		section = sectionName;
		this.count = count;
		options = new LinkedList<String>();
	}

	/**
	 * assigns the text to the QuestionText object, and in the process 
	 * determines the start and end locations of the stem, options, 
	 * explanation, and then sets strings corresponding to these components
	 * accordingly.
	 * 
	 * @param questionsText a string containing the entirety of the text for the question
	 */
	public void setText(String questionsText) {
		try {
			text = questionsText.substring(begPosition, endPosition);

			// first, remove a substring from the string, text, giving the
			// sequence number of the question.  The format of this substring
			// is of the form, e.g., "...of 15" or something like that.
			text = removeCountLabel(text);

			// find the start and end positions of the stem, options, explanation.
			setStemBegEndPos();
			setOptionsBegPos();
			setOptionsEndPos();
			setExplanationBegPos();
			setExplanationEndPos();

			// now, set the stem, options, explanation accordingly.
			setStem();
			setOptions();
			setCorrectResponse();
			setExplanation();

		} catch (StringIndexOutOfBoundsException e) {
			System.out.printf("%s%d%s%d\n", "ERROR: ", begPosition, " : ", endPosition);
		}
	}

	/**
	 * removes a substring within the text for the question, passed in as an 
	 * input parm, that is of the form "of xxx", such as "of 15".  This has the
	 * effect of removing extra text that no longer provides useful information
	 * about the sequence number of the question within the larger collection of
	 * questions within the file.
	 * 
	 * @param text the text for the question
	 * @return string containing text for the question with the "of xxx" removed
	 */
	private String removeCountLabel(String text) {
		String countLabelMarker = new String("of " + count);

		int countLabelMarkerPos = text.indexOf(countLabelMarker);
		if (countLabelMarkerPos != -1) {
			int countLength = 2;
			int countLabelPos = countLabelMarkerPos - countLength - 1;
			if (!text.substring(countLabelPos, countLabelPos + 1).matches("[0-9]")) {
				countLabelPos++;
				countLength = 1;
			}
			String countLabel = text.substring(countLabelPos, countLabelPos + countLength + 1 + countLabelMarker.length());
			text = text.replace(countLabel, "");
		}
		return text;
	}

	/**
	 * accessor for the entirety of the text for the question.
	 * 
	 * @return string for the text of the question
	 */
	public String getText() {
		return text;
	}

	/**
	 * mutator for the section attribute
	 * 
	 * @param section
	 */
	public void setSection(String section) {
		this.section = section;
	}

	/**
	 * determines the beginning and ending position of the stem.
	 * starts with the position just after the name of the section
	 * in text, (the text for the question), and ends with the position
	 * of the first option (signified by 'A') in the text.  
	 * 
	 * Note, there are some quirks addressed in this method - built-in
	 * functionality for handling carriage returns, \r, etc.
	 */
	private void setStemBegEndPos() {
		stemBegPos = text.indexOf(section) + section.length() + 1;
		while (text.charAt(stemBegPos) == '\n' || text.charAt(stemBegPos) == '\r')
			stemBegPos++;
		stemEndPos = optPosition('A', text) - 1;
	}

	/**
	 * sets the options beginning position to be just after the end position
	 * of the stem, in text.
	 */
	private void setOptionsBegPos() {
		optionsBegPos = stemEndPos + 1;
	}

	
	/**
	 * determines ending position for options for the question by looking for 
	 * 'D', then 'C' if 'D' not found, then 'B', and so on.  if none found,
	 * set it to the end of the file.
	 */
	private void setOptionsEndPos() {

		int lastOptionPos = optPosition('D', text);

		if (lastOptionPos == -1)
			lastOptionPos = optPosition('C', text);
		if (lastOptionPos == -1)
			lastOptionPos = optPosition('B', text);
		if (lastOptionPos == -1)
			lastOptionPos = optPosition('A', text);

		if (lastOptionPos != -1) {
			optionsEndPos = text.indexOf("\n", lastOptionPos);
		} else {
			optionsEndPos = text.length() - 1;
		}
	}

	/**
	 * looks for beginning of a particular option, specified by input parm,
	 * letter, by looking for particular sequences of characters in the raw
	 * ocr output file text, (stored in text).  
	 * 
	 * @param letter the letter of the option searched for
	 * @param text the text of the question
	 * @return the position where the option starts
	 */
	private int optPosition(char letter, String text) {
		int pos = text.indexOf("C " + letter + ". ");

		if (pos == -1) {
			pos = text.indexOf(letter + ". ");
		}

		if (pos == -1) {
			pos = text.indexOf("C " + letter + " ");
		}

		if (pos == -1) {
			pos = text.indexOf(letter + ".\t");
		}
		return pos;
	}

	/**
	 * determines beginning position for explanation.  If options
	 * end is not at the end of text, then set it to just after options.
	 * Otherwise, assume there is no explanation, and just set beginning
	 * of explanation to end of file.
	 */
	private void setExplanationBegPos() {
		if (optionsEndPos != text.length() - 1)
			explanationBegPos = optionsEndPos + 1;
		else
			explanationBegPos = text.length() - 1;
	}

	/**
	 * determines end position of explanation.  If beg position is
	 * not at end of text (i.e., there actually is an explanation for this
	 * question), then set end to be start of the string, "Answered this question",
	 * undoubtedly a string that apparently is present just after end of the explanation...
	 */
	private void setExplanationEndPos() {
		if (explanationBegPos != text.length() - 1)
			explanationEndPos = text.indexOf("Answered this question ");
		else
			explanationEndPos = text.length() - 1;
	}

	/**
	 * sets the stem of the question, based on the determined values for
	 * beginning and ending position for stem.  Also, formats the roman
	 * numerals found in the options if the question is of the I only, II only,
	 * I and II, etc. type, and does other ad-hoc formats/eliminations of 
	 * erroneous strings commonly returned by the ocr software.
	 */
	private void setStem() {
		stem = text.substring(stemBegPos, stemEndPos);
		stem = stem.replace("\n", "");

		// if stem includes Roman numeral options, format them.
		if (stem.contains("III")) {
			stem = stem.replace("\rI.", " \\r\\rI.");
			stem = stem.replace("\rII.", " \\rII.");
			stem = stem.replace("\rIII.", " \\rIII.");
			stem = stem.replace("\rIV.", " \\rIV.");
		}

		// remove any random characters.
		stem = stem.replace("\r", "");
		stem = stem.replace("f7", "");
		stem = stem.replace("(*", "");

		if (stem.charAt(stem.length() - 1) == 'G' || stem.charAt(stem.length() - 1) == 'C') {
			stem = stem.substring(0, stem.length() - 1);
		}
		stem = stem.trim();
	}

	/**
	 * sets the options for the question based on the determined values for 
	 * beginning and ending position for options.  Also, does some eliminations
	 * of erroneous character sequences injected into the text by the ocr software.
	 */
	private void setOptions() {
		String optionsText = text.substring(optionsBegPos, optionsEndPos);
		optionsText = optionsText.replace("\n", "");
		optionsText = optionsText.replace("\r", "");
		optionsText = optionsText.replace("INCORRECT", "");
		char letter = 'A';

		String optionText = null;
		for (int i = 0; i < 4; i++) {
			optionText = getOptionText(optionsText, (char) (letter + i));
			if (optionText != null) {
				// if optionText contains "True", assume this is a true/false question,
				// populate options accordingly, and return.
				if (optionText.contains("True")) {
					setOptionsTrueFalse();
					return;
				}
		
				// remove random characters from the option string.
				optionText = optionText.replace(" v ", "");
				optionText = optionText.replace(" V ", "");
				optionText = optionText.replace("f7", "");
				
				
				optionText = optionText.trim();
				if (optionText.charAt(optionText.length() - 1) == 'C' || optionText.charAt(optionText.length() - 1) == 'X')
					optionText = optionText.substring(0, optionText.length() - 1);
				if(optionText.substring(optionText.length() - 2).equals(".x"))
					optionText = optionText.substring(0, optionText.length() - 1);
				if(optionText.toLowerCase().substring(optionText.length() - 3).equals("v r"))
					optionText = optionText.substring(0, optionText.length() - 3);
				if(optionText.toLowerCase().substring(optionText.length() - 2).equals("iz"))
					optionText = optionText.substring(0, optionText.length() - 2);
				if(optionText.toLowerCase().substring(optionText.length() - 3).equals("x r"))
					optionText = optionText.substring(0, optionText.length() - 3);

				optionText = optionText.trim();
				
				options.add(optionText);
			}
		}
	}
	
	/**
	 * sets the options list to True and False.  Appropriate for those questions surmised
	 * to be true/false questions.
	 */
	private void setOptionsTrueFalse() {
		options.clear();
		options.add("True");
		options.add("False");
		
		// since this is a true/false question, make sure the stem ends with a period (for
		// a declarative statement).  If it doesn't have one, add it.
		if(!stem.substring(stem.length() - 1).equals("."))
			stem = stem + ".";
	}

	/**
	 * returns the text for a particular option, given by the input parm, letter.
	 * This function acts as a utility function for setOptions().
	 *  
	 * @param optionsText the text that contains the text for all of the options
	 * @param letter the letter of the options for which to retrieve the text
	 * @return a string for the particular option
	 */
	private String getOptionText(String optionsText, char letter) {

		int labelLength = 0;
		int optBegPos = 0;
		int optEndPos = 0;

		if (optPosition(letter, optionsText) != -1) {
			// find precise length of option label.
			int pos = optionsText.indexOf("C " + letter + ". ");
			if (pos != -1) {
				labelLength = 5;
			}
			if (pos == -1) {
				pos = optionsText.indexOf(letter + ". ");
				labelLength = 3;
			}
			if (pos == -1) {
				pos = optionsText.indexOf("C " + letter + " ");
				labelLength = 4;
			}
			
			if (pos == -1) {
				pos = optionsText.indexOf(letter + ".\t");
				labelLength = 3;
			}

			if (pos == -1) {
				labelLength = 0;
			}

			optBegPos = pos + labelLength;

			if (optBegPos != -1) {
				optEndPos = optPosition((char) (letter + 1), optionsText);
				if (optEndPos == -1)
					optEndPos = optionsText.length();
			} else {
				return null;
			}

			return optionsText.substring(optBegPos, optEndPos);

		} else {
			return null;
		}
	}

	/**
	 * sets the correctResponse private attribute.  This function uses a couple
	 * heuristics - first, if this question is an all of the above question, then
	 * set to all of the above, because it's almost always the correct answer.
	 * Otherwise, find the string, CORRECT, and set it to the option that contains
	 * that string.  Otherwise, set to 0.
	 */
	private void setCorrectResponse() {
		
		// start with correctResponse = -1.
		correctResponse = -1;

		// if this is an all of the above option, set the correctResponse variable
		// to be this option (it's usually the correct answer... haha!)
		for(int i = 0; i < options.size(); i++) {
			if(options.get(i).contains("All of the above") || options.get(i).contains("Any of the above") || options.get(i).contains("I, II, III, and IV")) {
				correctResponse = i;
				return;
			}
		}
		

		// take a stab at finding the correct response.  If no success, simply default to 0.
		for (int i = 0; i < options.size(); i++) {
			if (options.get(i).contains("CORRECT")) {
				correctResponse = i;
				String optionCorrectRemoved = options.get(i).replace("CORRECT", "");
				options.set(i, optionCorrectRemoved);
			}
		}

		if (correctResponse == -1)
			correctResponse = 0;
	}

	/**
	 * sets the explanation private attribute variable by cleaning up the
	 * text for explanation - i.e., remove erroneous character sequences 
	 * inserted by the ocr software.
	 */
	private void setExplanation() {
		explanation = text.substring(explanationBegPos, explanationEndPos);
		explanation = explanation.replace("\r", "");
		explanation = explanation.replace("\n", "");
		explanation = explanation.replace("X INCORRECTV", "");
		explanation = explanation.replace("INCORRECT", "");
		explanation = explanation.replace("CORRECT", "");
		explanation = explanation.replace(">/ ", "");
		explanation = explanation.trim();
		
		if(explanation.substring(0, 2).equals("x "))
			explanation = explanation.substring(2);
	}

	/**
	 * returns a string representation of the question, containing
	 * labeled elements, section, stem, options, etc.  This is the 
	 * method called for creating the string to be inserted into the 
	 * question file by the QuestionFileBuilder class.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();

		sb.append(String.format("%s%s\n\n", "Section=", section));
		sb.append(String.format("%s%s\n\n", "Stem=", stem));

		sb.append("Options=");
		for (int i = 0; i < options.size(); i++) {
			sb.append(options.get(i));
			if (i < options.size() - 1) {
				sb.append(" | ");
			}
		}
		sb.append("\n\n");

		sb.append(String.format("%s%s\n\n", "CorrectResponse=", correctResponse));
		sb.append(String.format("%s%s\n", "Explanation=", explanation));

		return sb.toString();
	}
}
