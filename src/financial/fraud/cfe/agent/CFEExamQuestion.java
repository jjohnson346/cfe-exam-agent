package financial.fraud.cfe.agent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import financial.fraud.cfe.util.Profile;

/**
 * An instance of CFEExamQuestion represents a cfe exam question, including its components: its name (a unique string
 * id), section, stem, options, correct response, and explanation.
 * 
 * @author jjohnson346
 * 
 */
public class CFEExamQuestion {

	/**
	 * a unique identifier for the question
	 */
	public final String name;

	/**
	 * the section name for the question, e.g., investigation, or, law
	 */
	public final String section;

	/**
	 * the text of the question to be answered
	 */
	public final String stem;

	/**
	 * the set of options from which to choose
	 */
	public final List<String> options;

	/**
	 * the correct response choice
	 */
	public final int correctResponse;

	/**
	 * stores the rationale for the correct answer
	 */
	public final String explanation;

	private final Profile profile; // a profile object storing the features of the question,
									// whether it's a true/false question, an all of the above
									// question, a question with long options, etc. These features
									// are stored as an array of booleans inside the Profile object.
									// See the Profile class for more details.

	/**
	 * constructor takes all the parms necessary to initialize the question object.
	 * 
	 * @param section
	 *            the section name for the question
	 * @param stem
	 *            the text of the question
	 * @param options
	 *            a list of Strings containing the text for the possible answers
	 * @param correctResponse
	 *            the index of the the correct answer to the question
	 * @param explanation
	 *            the text giving the rationale for the correct answer
	 */
	public CFEExamQuestion(String section, String stem, List<String> options, int correctResponse, String explanation) {
		this.section = section;
		this.stem = stem;
		this.options = options;
		this.correctResponse = correctResponse;
		this.explanation = explanation;

		// TODO: there is currently no logic for assigning a name to the question
		// in this constructor. This is low priority because this constructor is not currently used,
		// only the other constructor which constructs the question from a question file (and uses the
		// name of the file) is currently being used to create question objects.
		name = "Test " + ((int)(Math.random() * 100) + 1);

		// instantiate a Profile object, passing to its constructor the question
		// object. The Profile constructor determines the profile to the question
		// such that by the time this call returns, the profile has been determined,
		// and populated.
		profile = new Profile(this);
	}

	/**
	 * one-arg constructor for that initializes components using the contents of the question file passed in as an input
	 * parm.
	 * 
	 * @param fileName
	 *            a string giving the name of the cfe exam question file
	 */
	public CFEExamQuestion(String fileName) {
		// load the file using the Properties api, (since the question files are all set up
		// to be loaded this way).
		String sectionProp = null;
		String stemProp = null;
		String optionsProp = null;
		int correctResponseProp = 0;
		String explanationProp = null;

		// get values from question file and place in temp variables.
		try {
			Properties p = new Properties();
			p.load(new FileInputStream(fileName));

			sectionProp = p.getProperty("Section");
			stemProp = p.getProperty("Stem");
			correctResponseProp = Integer.parseInt(p.getProperty("CorrectResponse"));
			optionsProp = p.getProperty("Options");
			explanationProp = p.getProperty("Explanation");

		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + fileName);
			System.exit(1);
		} catch (IOException e) {
			System.out.println("Error reading question file, " + fileName);
			System.exit(1);
		}
		// set the name of the question:
		String tempName;

		// if name has path, remove it.
		if (fileName.contains("/"))
			tempName = fileName.substring(fileName.lastIndexOf("/") + 1);
		else
			tempName = fileName;

		// initialize the name for the question using the name of the question file.
		// remove ".txt" extension.
		name = tempName.substring(0, tempName.length() - 4);

		section = sectionProp;
		stem = stemProp;
		correctResponse = correctResponseProp;
		explanation = explanationProp;

		// populate options list.
		options = new ArrayList<String>();
		Scanner s = new Scanner(optionsProp);
		s.useDelimiter(" \\| ");

		while (s.hasNext()) {
			options.add(s.next().trim());
		}

		profile = new Profile(this);
	}

	/**
	 * returns a pretty-format String representation of the question.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s\n\n", name));
		sb.append(String.format("%s\n\n", section));
		sb.append(String.format("%s\n\n", stem));
		for (int i = 0; i < options.size(); i++)
			sb.append(String.format("%s%s%s\n", (char) (i + 97), ") ", (String) options.get(i)));
		return sb.toString();
	}

	/**
	 * returns a string representation of the question object.
	 * 
	 * this code was taken from the QuestionText class, where it was used to create the initial question files.
	 * 
	 * TODO: may want to re-factor the function names so that this is the toString() method, instead of the one above
	 * (that does not include the correct answer).
	 * 
	 * @return string representation of the question, including the correct answer
	 */
	public String toStringIncludeCorrectAnswer() {
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

	/**
	 * persists the question object to a file
	 * 
	 * TODO: should we be overriding the serialize method here?
	 * 
	 * @param fileName
	 */
	public void writeToFile(String dirName) {
		String fileName = dirName + "/" + name + ".txt";
		try {
			Writer writer = new BufferedWriter(new FileWriter(new File(fileName)));
			writer.write(this.toStringIncludeCorrectAnswer());
			writer.flush();
			writer.close();
		} catch (IOException e) {
			System.out.println("Unable to write question object to file: " + fileName);
			e.printStackTrace();
		}
	}

	/**
	 * returns a string giving the full text of the option for a particular option index. This text is preceded by a
	 * character (a, b, c, or d) giving the option character, then a close parentheses, then the full text of the
	 * option. e.g., "b) accounting fraud"
	 * 
	 * @param response
	 *            the index of the option for which the full text of the option is to be returned
	 * @return a string containing the formatted text of the option
	 */
	public String getFormattedResponse(int response) {
		return String.format("%s%s%s", (char) (response + 97), ") ", options.get(response));
	}

	/**
	 * returns the formatted text for the correct response to the question. Delegates the work of formatting the text to
	 * the getFormattedResponse() method, passing in the index of the correct option.
	 * 
	 * @return a string containing the formatted text of the correct option
	 */
	public String getFormattedCorrectResponse() {
		return getFormattedResponse(correctResponse);
	}

	/**
	 * accessor for the Profile object to this question.
	 * 
	 * @return a Profile object containing the feature info for the question
	 */
	public Profile getProfile() {
		return profile;
	}

	@Override
	/**
	 * override of equals, based on the stem member variable.
	 */
	public boolean equals(Object o) {
		if (!(o instanceof CFEExamQuestion))
			return false;

		CFEExamQuestion q = (CFEExamQuestion)o;
		if (stem.equals(q.stem) && options.equals(q.options))
			return true;
		else
			return false;
	}

	@Override
	/**
	 * override of hashCode() function, based on stem's hashCode() and options' hashCode().
	 * this is to make sure that if two questions return true for equals() they also have the
	 * same hashCode() return value.
	 */
	public int hashCode() {
		return stem.hashCode() + options.hashCode();
	}

	/**
	 * test harness to verify that questions can be compared using equals() and according to hashCode() appropriately.
	 */
	public static void main(String[] args) {
		List<String> options = Arrays.asList(new String[]{"a", "b", "c", "d"});
		List<String> trueFalse = Arrays.asList(new String[]{"true", "false"});
		CFEExamQuestion q1 = new CFEExamQuestion("Common Sense", "Does a bear crap in the woods?", trueFalse, 0, "obvious");
		CFEExamQuestion q2 = new CFEExamQuestion("Common Sense", "Does a bear crap in the woods?", trueFalse, 0, "obvious");
		CFEExamQuestion q3 = new CFEExamQuestion("Common Sense", "Does a bear crap in the woods?", options, 0, "obvious");
		CFEExamQuestion q4 = new CFEExamQuestion("Common Sense", "What is 1 + 1?", options, 0, "2");
		
		System.out.println("q1.equals(q2) should be true: " + q1.equals(q2));
		System.out.println("q1.equals(q3) should be false: " + q1.equals(q3));
		System.out.println("q1.equals(q4) should be false: " + q1.equals(q4));
		
		HashSet<CFEExamQuestion> questionSet = new HashSet<CFEExamQuestion>();
		questionSet.add(q1);
		questionSet.add(q2);
		questionSet.add(q3);
		questionSet.add(q4);
		
		// this print out should show only three entries output because of the override of equals().
		System.out.println(questionSet); 
		
		// hash codes for q1 and q2 should be equal, and for should be different from those of
		// q3 and q4 (which should be different from each other).
		System.out.println("q1.hashCode(): " + q1.hashCode());
		System.out.println("q1.hashCode(): " + q2.hashCode());
		System.out.println("q1.hashCode(): " + q3.hashCode());
		System.out.println("q1.hashCode(): " + q4.hashCode());
		
		
	}
}
