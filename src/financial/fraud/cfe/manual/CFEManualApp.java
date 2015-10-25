package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;

import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;

/**
 * CFEManualApp is a class for testing the CFEManual class. It is not used by the CFEExamAgent software, but instead
 * merely as a tool to verify proper functioning of the CFEManual class.
 * 
 * QUESTION: Would it be more appropriate for the code of this class to be placed in the test package using JUnit?
 * 
 * @author jjohnson346
 * 
 */
public class CFEManualApp {

	private CFEManual cfeManual; // the reference to the CFEManual object against which to execute
	// functionality the user has selected

	private HashMap<String, CFEManual> cfeManuals; // stores the instances of different subclasses of CFEManual
													// against which we can execute functionality, depending on user
													// choice for CFE manual type.

	private DocumentUnit docUnit;

	private Logger logger; // a reference to the Logger instance
	
	private final String OUTPUT_PATH = "manual test output";	// the directory where any output files will be placed,
															// including section text files, manual structure files, etc.

	public CFEManualApp() {
		cfeManuals = new HashMap<String, CFEManual>();

		// initialize logger
		logger = Logger.getInstance();
		logger.setDetailLevel(DetailLevel.FULL);
	}

	public void execute() {
		int input;
		Scanner scanner = new Scanner(System.in);

		String manualSectionId = null;

		setCFEManual();

		while (true) {
			try {
				input = getUserSelection();

				switch (input) {
				case 1:
					// a simple test of creating and printing the cfe manual
					String fileName = "2011.Fraud.Examiners.Manual.Structure." + docUnit
							+ ".txt";
					outputTextToFile(fileName, OUTPUT_PATH, cfeManual.toString());
					System.out.println("2011 Fraud Examiners Manual structure written to " + fileName + ".");
					break;
				case 2:
					// prompts the user for an id (a unique string giving the
					// name of the section),
					// and retrieves the corresponding section from the cfe
					// manual, printing out to the
					// console or file.
					System.out.println("Input the id of the section to be retrieved.");
					String mainSectionName = scanner.nextLine();

					outputSectionText(mainSectionName);
					break;
				case 3:
					// prompts the user for a section id (unique name of
					// section) and prints
					// the token frequency counts for that section.
					System.out.println("Input the id of the section whose token type frequencies are to be retrieved.");
					mainSectionName = scanner.nextLine();
					outputTokenFreqs(mainSectionName);
					break;
				case 4:
					// prints ancestor path to a section.
					System.out.println("Input the id of the manual section whose ancestors are to be retrieved: ");
					manualSectionId = scanner.nextLine();
					outputAncestorsForManualSection(manualSectionId);
					break;
				case 5:
					// print out list of all sections.
					// outputSectionNamesList();
					outputSectionNames("2011 Fraud Examiners Manual");
					break;
				case 6:
					// print out list of subsections of a *question* section.
					System.out.println("Input the name of the *question* section: ");
					String questionSectionName = scanner.nextLine();
					outputSectionNames(cfeManual.getManualSectionForQuestionSection(questionSectionName).id);
					break;
				case 7:
					// prints subsection tree
					System.out.println("Input the id of the manual section whose subtree is to be retrieved: ");
					manualSectionId = scanner.nextLine();
					System.out.println(cfeManual.getManualSection(manualSectionId));
					break;
				case 8:
					// prints subsection tree as a list.
					System.out.println("Input the id of the manual section whose subtree is to be retrieved: ");
					manualSectionId = scanner.nextLine();
					CFEManualSection section = cfeManual.getManualSection(manualSectionId);
					for (CFEManualSection descendant : section.getSubTreeAsList())
						System.out.println(descendant.name);
					break;
				case 9:
					if (cfeManual.getErrors().size() > 0) {
						System.out.println("Sections for which errors were generated during manual construction:");
						for (CFEManualSection e : cfeManual.getErrors())
							System.out.println(e.name);
					} else {
						System.out.println("No errors generated during manual construction.");
					}
					break;
				case 10:
					setCFEManual();
					break;
				case 11:
					return;
				default:
					System.out.println("Invalid option.  Please enter a valid selection.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * prompts the user for for the cfe manual to use.
	 * 
	 * @return the index selected by the user.
	 */
	private void setCFEManual() {
		int menuChoice;

		while (true) {
			System.out.println("Select the CFE Manual:");
			System.out.println("1.  Small Document Unit.");
			System.out.println("2.  Small Document Unit - regex.");
			System.out.println("3.  Large Documnet Unit.");
			System.out.println("********* OR ***********");
			System.out.println("4.  Go to Main Menu");

			Scanner scanner = new Scanner(System.in);
			menuChoice = scanner.nextInt();

			switch (menuChoice) {
			case 1:
				System.out.println("Configuring CFE Manual as Small Doc Unit manual...");
				cfeManual = cfeManuals.get("sdu");
				if (cfeManual == null) {
					System.out.println("Small Doc Unit CFE Manual needs to be initialized....");
					System.out.println("Initializing Small Doc Unit manual... Please stand by.");

					cfeManual = new CFEManualSmallDocUnit();
					cfeManuals.put("sdu", cfeManual);
				}
				docUnit = DocumentUnit.SMALL;
				return;
			case 2:
				System.out.println("Configuring CFE Manual as Small Doc Unit manual...");
				cfeManual = cfeManuals.get("sduregex");
				if (cfeManual == null) {
					System.out.println("Small Doc Unit CFE Manual needs to be initialized....");
					System.out.println("Initializing Small Doc Unit manual... Please stand by.");

					cfeManual = new CFEManualSmallDocUnitRegex();
					cfeManuals.put("sduregex", cfeManual);
				}
				docUnit = DocumentUnit.SMALL_REGEX;
				return;
			case 3:
				System.out.println("Configuring CFE Manual as Large Doc Unit manual...");
				cfeManual = cfeManuals.get("ldu");
				if (cfeManual == null) {
					System.out.println("Large Doc Unit CFE Manual needs to be initialized....");
					System.out.println("Initializing Large Doc Unit manual... Please stand by.");
					cfeManual = new CFEManualLargeDocUnit();
					cfeManuals.put("ldu", cfeManual);
				}
				docUnit = DocumentUnit.LARGE;
				return;
			case 4:
				return;
			default:
				System.out.println("Invalid option.  Please enter a valid selection.");
			}
		}
	}

	/**
	 * prompts the user for for testing options.
	 * 
	 * @return the index selected by the user.
	 */
	private int getUserSelection() {
		System.out.println("CFE Manual Menu:");
		System.out.println("1.  Print CFE Manual structure.");
		System.out.println("2.  Retrieve text for a manual section, (section id required).");
		System.out.println("3.  Retrieve token frequencies for a manual section.");
		System.out.println("4.  Retrieve ancestors for a section, (section id required).");
		System.out.println("5.  Retrieve all manual sections as a list.");
		System.out.println("6.  Retrieve list of sections in the subtree for a *question* section.");
		System.out.println("7.  Retrieve subtree for a manual section, (section id required).");
		System.out.println("8.  Retrieve subtree as a list for a manual section, (section id required).");
		System.out.println("9.  Retrieve errors generated during manual construction.");
		System.out.println("10. Set CFE Manual.");
		System.out.println("11. Quit.");

		Scanner scanner = new Scanner(System.in);
		return scanner.nextInt();
	}

	/**
	 * outputs token frequency counts for a particular cfe manual section.
	 * 
	 * @param manualSectionId
	 *            the section for which to print out the frequency counts
	 */
	private void outputTokenFreqs(String manualSectionId) {
		CFEManualSection section = cfeManual.getManualSection(manualSectionId);
		String typeFreqsData = section.getTokenizer().toString();
		System.out.println(typeFreqsData);
	}

	/**
	 * outputs the CFE manual section text for a given manual section name to a file.
	 * 
	 * @param fileName
	 *            the name of the file to which to print the section content
	 * @param manualSectionId
	 *            the unique name of the section to print out to the file
	 * @throws IOException
	 *             could be thrown if can't make the file
	 */
	private void outputSectionText(String manualSectionId) {
		CFEManualSection section = cfeManual.getManualSection(manualSectionId);
		if (section == null) {
			System.out.println("no text for " + manualSectionId);
		}
		String sectionText = section.getText();
		if (sectionText != null) {
			if (sectionText.length() < 10000) {
				System.out.println("Writing to console...");
				System.out.println(sectionText);
			} else {
				try {
					System.out.println("Writing to file...");
					String fileName = new String("Section.Text." + section.name + ".txt");
					outputTextToFile(fileName, OUTPUT_PATH, sectionText);
					System.out.printf("%s%s%s\n\n", "Section text output to Section.Text.", section.name, ".txt");
				} catch (IOException e) {
					System.out.println("Error writing to file.");
				}
			}
		} else {
			System.out.println("no text for " + manualSectionId);
		}
	}

	/**
	 * acts as a helper function for the public overloaded outputSectionText methods. Creates a Writer object for
	 * writing to a file, flushing the writer buffer every 2000 characters as it does so.
	 * 
	 * @param fileName
	 *            the name of the file to which to output the text
	 * @param text
	 *            the text to output
	 * @param path
	 *            the path where the output file will be placed
	 * 
	 * @throws IOException
	 *             could be thrown if can't make file
	 */
	private void outputTextToFile(String fileName, String path, String text) throws IOException {
		// first, make sure the file name consists of only valid characters.
		fileName = fileName.replace(": ", " - ");
		fileName = fileName.replace("/", " - ");
		
		fileName = path + File.separator + fileName;

		Writer writer = new FileWriter(fileName);
		for (int i = 0; i < text.length(); i += 2000) {
			if (i + 2000 > text.length())
				writer.write(text, i, text.length() - i);
			else
				writer.write(text, i, 2000);
			writer.flush();
		}
		writer.close();
	}

	// public void outputAncestorsForManualSection(String manualSectionName) {
	// List<CFEManualSection> ancestors =
	// cfeManual.getManualSection(manualSectionName).getAncestors();
	//
	// for (CFEManualSection section : ancestors) {
	// System.out.printf("%s : ", section.name);
	// }
	// System.out.println();
	// }

	/**
	 * prints out the ancestor path for a particular section of the cfe manual.
	 * 
	 * @param manualSectionId
	 *            the section for which to retrieve the ancestor path.
	 */
	public void outputAncestorsForManualSection(String manualSectionId) {
		CFEManualSection section = cfeManual.getManualSection(manualSectionId);
		List<CFEManualSection> ancestors = section.getAncestors();

		if (ancestors == null) {
			System.out.println("This section yields no ancestors.");
		} else {
			for (CFEManualSection ancestor : ancestors) {
				System.out.printf("%s : ", ancestor.name);
			}
			System.out.println();
		}
	}

	/**
	 * prints out all of the section names identified in the cfe manual.
	 */
	// public void outputSectionNamesList() {
	// CFEManualSection rootSection = cfeManual.getRoot();
	//
	// for(CFEManualSection s : rootSection.getSubTreeAsList()) {
	// System.out.println(s.name);
	// }
	// }

	/**
	 * outputs the sub-sections of a given section (passed in as an input parm).
	 * 
	 * @param sectionName
	 *            the name of the section whose sub-section are to be retrieved
	 */
	// public void outputSectionNames(String sectionName) {
	// System.out.println("Sections in subtree for section name, " + sectionName
	// + ":");
	// for(CFEManualSection s : cfeManual.getManualSections(sectionName)) {
	// System.out.println(s.name);
	// }
	// }

	public void outputSectionNames(String manualSectionId) {
		System.out.println("Sections in subtree for section name, " + manualSectionId + ":");
		CFEManualSection section = cfeManual.getManualSection(manualSectionId);
		for (CFEManualSection s : section.getSubTreeAsList()) {
			System.out.println(s.name);
		}
	}

	private enum DocumentUnit {
		SMALL, SMALL_REGEX, LARGE
	}

	/**
	 * instantiates and runs the execute method of the CFEManualApp class, giving the user a menu of options for pieces
	 * of functionality to test. The user picks from the menu, and this class executes test of the corresponding
	 * functionality in CFEManual class.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		CFEManualApp app = new CFEManualApp();

		if (args.length != 1) {
			System.out.println("Usage: java CFEManualApp <logging detail level>");

			System.out.println("1 - NONE");
			System.out.println("2 - MINIMAL");
			System.out.println("3 - MEDIUM");
			System.out.println("4 - FULL");

			System.exit(1);
		}

		switch (Integer.parseInt(args[0])) {
		case 1:
			app.logger.setDetailLevel(DetailLevel.NONE);
			break;
		case 2:
			app.logger.setDetailLevel(DetailLevel.MINIMAL);
			break;
		case 3:
			app.logger.setDetailLevel(DetailLevel.MEDIUM);
			break;
		case 4:
			app.logger.setDetailLevel(DetailLevel.FULL);
			break;
		default:
			System.out.println("invalid input.");
			System.exit(1);
		}

		app.execute();
	}

}
