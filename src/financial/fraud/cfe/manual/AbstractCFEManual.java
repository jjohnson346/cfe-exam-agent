package financial.fraud.cfe.manual;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;

/**
 * A convenience class providing a partial implementation of CFEManual, providing implementations for all functions
 * except the protected method, loadBeginningPositions() which will vary between child classes of this class, depending
 * on the size of the document unit.
 * 
 * @author jjohnson346
 * 
 */
public abstract class AbstractCFEManual implements CFEManual {

	/**
	 * root section of the manual tree, which is a tree data structure containing all manual sections and subsections
	 */
	protected CFEManualSection root;

	/**
	 * map relating manual section name to exam section name. will be one of the following: 1. Financial Transactions
	 * and Fraud Schemes 2. Law 3. Investigation 4. Fraud Prevention and Deterrence
	 * 
	 * used when constructing the cfe manual section objects of the manual - this lookup provides the means to
	 * determine/assign the exam section to which the manual section applies, and assign the member variable of the cfe
	 * manual section object accordingly.
	 */
	private Map<String, String> examSectionLookup;

	/**
	 * map relating manual section name to exam section name. used when constructing the cfe manual section objects of
	 * the manual - this lookup provides the means to determine/assign the question section to which the manual section
	 * applies, and assign the member variable of the cfe manual section object accordingly.
	 */
	private Map<String, String> questionSectionLookup;

	/**
	 * map relating exam section name to manual section name
	 */
	private Map<String, String> manualSectionLookup;

	/**
	 * map relating manual section names to manual section objects
	 */
	private Map<String, CFEManualSection> manualSectionMap;

	/**
	 * list of manual sections that yielded any errors during the load process. used for debugging
	 */
	protected List<CFEManualSection> errors;

	/**
	 * reference to the single instance of the Logger class (implements the Singleton pattern). Used for debugging, as
	 * well as for general reporting of system status.
	 */
	protected Logger logger;

	/**
	 * constructor builds the tree structure of cfe manual section objects.
	 */
	protected AbstractCFEManual() {

		logger = Logger.getInstance();

		logger.println("Reading Fraud Examiners Manual...", DetailLevel.MINIMAL);

		String manualText = getManualText();
		String toc = getTOC();

		errors = new LinkedList<CFEManualSection>();

		// build the hash map lookup for cfe manual section -> exam section name.
		logger.println("Loading manual section -> exam section lookup...", DetailLevel.MEDIUM);
		buildExamSectionLookup();
		logger.println(" manual section -> exam section load complete.", DetailLevel.MEDIUM);

		// build the hash map lookup for cfe manual section -> question section name.
		logger.println("Loading manual section -> question section lookup...", DetailLevel.MEDIUM);
		buildQuestionSectionLookup();
		logger.println("manual section -> question section load complete.", DetailLevel.MEDIUM);

		// build tree structure of cfe manual section objects.
		logger.println("Building manual tree...", DetailLevel.MEDIUM);
		buildManualTree(toc, manualText);
		logger.println("Build of manual tree completed successfully.", DetailLevel.MEDIUM);

		// initialize beginning position of each cfe manual section object.
		logger.println("Loading beginning positions of manual sections...", DetailLevel.MEDIUM);
		loadBegPositions(root, manualText, 0, 0);
		logger.println("Beginning positions load complete.", DetailLevel.MEDIUM);

		// initialize end position for each cfe manual section object.
		logger.println("Loading end positions for manual sections...", DetailLevel.MEDIUM);
		loadEndPositions(root, manualText);
		logger.println("End positions load complete.", DetailLevel.MEDIUM);

		// build the hash map lookup for cfe question section -> cfe manual section name.
		logger.println("Loading question section -> manual section lookup...", DetailLevel.MEDIUM);
		buildManualSectionLookup();
		logger.println("question section lookup -> manual section load complete.", DetailLevel.MEDIUM);

		// build the hash map lookup for cfe manual section name -> cfe manual section object.
		buildManualSectionMap();

		// set doc text for each cfe manual section object.
		logger.println("Setting document bodies for manual sections...", DetailLevel.MEDIUM);
		this.setBodiesForSections();
		logger.println("Setting document bodies process complete.", DetailLevel.MEDIUM);

		logger.println("Completed reading Fraud Examiners Manual.", DetailLevel.MINIMAL);
	}

	private void setBodiesForSections() {
		setSectionBody(root);
	}

	private void setSectionBody(CFEManualSection section) {
		section.setText();
		for (CFEManualSection subSection : section.subSections.values()) {
			setSectionBody(subSection);
		}
	}

	/**
	 * returns a string containing the entire table of contents of the fraud examiners manual. This function requires
	 * the presence of a file called 2011_zz_fem_toc.txt in the manual subdirectory.
	 * 
	 * @return string containing table of contents
	 */
	private String getTOC() {
		String toc = null;
		try {
			toc = getFileContents("manual//2011_zz_fem_toc.txt");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return toc;

		// 2015.09.16 - code below was replace with the code above that calls a function that
		// encapsulates the logic for extracting the text from a file.

		// Scanner scanner = null;
		//
		//
		// // 2015.09.16 - replaced code using Scanner with code below using BufferedReader.
		// // not sure why, but scanner is not loading the entire file, only a small part of it.
		// try {
		// BufferedReader br = new BufferedReader(new FileReader("manual//2011_zz_fem_manual_text.txt"));
		// StringBuilder fileContents = new StringBuilder();
		// String line;
		// while ((line = br.readLine()) != null) {
		// fileContents.append(line + "\n");
		// }
		// toc = new String(fileContents);
		// } catch (IOException e) {
		// e.printStackTrace();
		// System.exit(1);
		// }
		// try {
		// // change backslashes to forward slashes for mac version.
		// // scanner = new Scanner(new File("manual\\2011_zz_fem_toc.txt"));
		// scanner = new Scanner(new File("manual//2011_zz_fem_toc.txt"));
		// scanner.useDelimiter("\\Z");
		// toc = scanner.next();
		// } catch (FileNotFoundException e) {
		// System.out.println("File not found.");
		// System.exit(1);
		// } finally {
		// scanner.close();
		// }
	}

	/**
	 * returns a string containing the entire text of the fraud examiners manual. This function requires the presence of
	 * a file called 2011_zz_manual_text.txt in the manual subdirectory.
	 * 
	 * @return string containing table of contents
	 */
	private String getManualText() {
		String manual = null;
		try {
			manual = getFileContents("manual//2011_zz_fem_manual_text.txt");
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		}
		return manual;

		// 2015.09.16 - code below was replace with the code above that calls a function that
		// encapsulates the logic for extracting the text from a file.

		// String manual = null;
		// Scanner scanner = null;

		// 2015.04.08 - replaced code using Scanner with code below using BufferedReader.
		// not sure why, but scanner is not working when executed from within jar (scanner.next())
		// throws an exception.
		// try {
		// BufferedReader br = new BufferedReader(new FileReader("manual//2011_zz_fem_manual_text.txt"));
		// StringBuilder fileContents = new StringBuilder();
		// String line;
		// while ((line = br.readLine()) != null) {
		// fileContents.append(line + "\n");
		// }
		// manual = new String(fileContents);
		// } catch (IOException e) {
		// e.printStackTrace();
		// System.exit(1);
		// }
		// // try {
		// // change backslashes to forward slashes for mac version.
		// // scanner = new Scanner(new File("manual\\2011_zz_fem_manual_text.txt"));
		// scanner = new Scanner(new File("manual//2011_zz_fem_manual_text.txt"));
		// scanner.useDelimiter("\\Z");
		// System.out.println("calling scanner.next()....");
		// System.out.println(System.getProperty("java.version"));
		// System.out.println(System.getProperty("user.dir"));
		// manual = scanner.next();
		// } catch (FileNotFoundException e) {
		// System.out.println("File not found.");
		// System.exit(1);
		// } finally {
		// scanner.close();
		// }
		// return manual;
	}

	/**
	 * returns the contents of a file as a string, using FileReader, BufferedReader instead of Scanner. Scanner has been
	 * causing problems, see comment from 04/2015. Also, in addition to the problems with the manual, using the Scanner
	 * to load the TOC is also causing issues.
	 * 
	 * @param fileName
	 */
	private String getFileContents(String fileName) throws IOException {
		String contents = null;
		BufferedReader br = null;

		// 2015.04.08 - replaced code using Scanner with code below using BufferedReader.
		// not sure why, but scanner is not working when executed from within jar (scanner.next())
		// throws an exception.
		try {
			br = new BufferedReader(new FileReader(fileName));
			StringBuilder fileContents = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) {
				fileContents.append(line + "\n");
			}
			contents = new String(fileContents);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(1);
		} finally {
			br.close();
		}
		return contents;
	}

	/**
	 * 
	 * @param toc
	 * @param manualText
	 */
	private void buildManualTree(String toc, String manualText) {
		try {
			CFEManualSection[] currSections = new CFEManualSection[100]; // stores the path of ancestors to the current
																			// node
			CFEManualSection section = null;

			root = new CFEManualSection("2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual",
					"2011 Fraud Examiners Manual", manualText, null, "ZZ_Miscellaneous", "2011 Fraud Examiners Manual");

			// set the beginning and ending position for the root. This
			// is critical for populating the beginning and ending positions
			// of all the child sections later (in loadBegPositions() and
			// loadEndPositions()).
			root.begPosition = 0;
			root.endPosition = manualText.length() - 1;
			root.setText();

			currSections[0] = root;

			// iterate through each line of the table of contents document, extracting
			// the section name and its page number, as well as its depth (by detecting
			// the number of tab indentations before the section name). Then, for
			// each section name, instantiate a section object (and thus, automatically insert
			// it into the tree structure of section objects - the constructor for cfe manual
			// section automatically does this by assigning a reference to parent, and then
			// inserting the current section into the children of the parent.
			Scanner scanner = new Scanner(toc);

			while (scanner.hasNextLine()) {
				// read next line of toc.
				Scanner lineScanner = new Scanner(scanner.nextLine());
				lineScanner.useDelimiter("\\.{5,}");

				String sectionName = lineScanner.next();
				String pageNumber = lineScanner.next().trim();

				// check its depth.
				int depth = getDepth(sectionName);

				// get the parent of this section.
				CFEManualSection parentSection = currSections[depth - 1];

				sectionName = sectionName.trim();
				String sectionId = parentSection.id + "/" + sectionName;

				// set the exam section based on the exam section lookup, or
				// if not there, that of the parent.
				String examSection = (examSectionLookup.containsKey(sectionId)) ? examSectionLookup.get(sectionId)
						: parentSection.examSection;

				// set the question section based on the question section lookup,
				// or if not in there, that of the parent.
				String questionSection = (questionSectionLookup.containsKey(sectionId)) ? questionSectionLookup
						.get(sectionId) : parentSection.questionSection;

				// create instance of CFEManualSection object, (and automatically have it
				// inserted into tree of manual objects, rooted at the root section).
				if (depth > 0 && depth < 100) {
					section = new CFEManualSection(sectionId, sectionName, pageNumber, manualText, parentSection,
							examSection, questionSection);
					currSections[depth] = section;
				} else {
					throw new Exception("Invalid depth for " + section);
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.getInstance().println("build of manual tree terminated unsuccessfully.", DetailLevel.MINIMAL);
		}
	}

	/**
	 * returns the depth of a section by counting the number of tab characters prior to the first letter of the section
	 * name in the string.
	 * 
	 * @param sectionName
	 *            the name of the section to find the depth of
	 * @return an integer representing the depth of the section
	 */
	private int getDepth(String sectionName) {
		int depth = 0;
		int tabPos = sectionName.indexOf("\t");
		while (tabPos != -1 && tabPos < sectionName.length()) {
			depth++;
			tabPos = sectionName.indexOf("\t", tabPos + 1);
		}
		// add 1 to account for fact that number of tabs in file
		// is 1 less than actual depth.
		return depth + 1;
	}

	/**
	 * abstract method for determining the beginning character position of for each CFEManual section object within the
	 * text of the cfe manual. For the root section, this is always 0. For the rest, it starts the search at the
	 * beginning position of the previous section or at the position of the page number for the section, looking for the
	 * section name starting at that point.
	 * 
	 * Note: for the root node, beginning position is set in the buildManualTree() method (not here).
	 * 
	 * This function is called recursively for each cfe manual section object, where for a call to this function for a
	 * given section object, this function calls this function for each child section of the current section.
	 * 
	 * @param section
	 *            the CFEManualSection object for which to determine the beginning position
	 * @param manualText
	 *            a string containing the text of the cfe manual
	 * @param startPos
	 *            the beginning position of the prior section.
	 * @return an integer, the beginning position for the CFEManualSection object
	 */
	protected int loadBegPositions(CFEManualSection section, String manualText, int prevBegPos, int prevPagePos) {
		int pagePos = 0;

		final String PAGE_LINE_REGEX = "(^2011 Fraud Examiners Manual\\s+?" + section.pageNumber + "\\s*?$|^"
				+ section.pageNumber + "\\s+?2011 Fraud Examiners Manual\\s+?)";

		Pattern pagePattern = Pattern.compile(PAGE_LINE_REGEX, Pattern.MULTILINE);
		Matcher pageMatcher = pagePattern.matcher(manualText);

		logger.println("Searching for page " + section.pageNumber + "...", DetailLevel.FULL);

		if (pageMatcher.find(prevPagePos)) {
			pagePos = pageMatcher.start();
			logger.println("page " + section.pageNumber + " found at " + pagePos, DetailLevel.FULL);
		} else {
			logger.println("page " + section.pageNumber + " NOT FOUND.", DetailLevel.FULL);
		}

		int begPos = pagePos < prevBegPos ? prevBegPos : pagePos;

		// find the name of section immediately after begPos.
		// section.begPosition = manualText.indexOf(section.name, begPos);
		section.begPosition = getSectionBegPosition(section, manualText, begPos);
		
		if (section.begPosition == -1) {
			errors.add(section);
		}

		prevBegPos = section.begPosition + section.name.length();
		prevPagePos = pagePos;

		for (CFEManualSection s : section.subSections.values()) {
			begPos = loadBegPositions(s, manualText, prevBegPos, prevPagePos);
			prevBegPos = begPos + s.name.length();
		}
		return section.begPosition;
	}

	protected abstract int getSectionBegPosition(CFEManualSection section, String manualText, int begPos);

	/**
	 * abstract method for determining the last character of each document to be created from the manual. This is
	 * abstract because depending on the chosen granularity, the implementation for this method will vary.
	 * 
	 * 
	 * Note: for the root node, ending position is set in the buildManualTree() method (not here).
	 * 
	 * This function is called recursively for the children of each CFEManual section object.
	 * 
	 * @param section
	 *            the CFEManualSection object for which to determine the end position
	 * @param manualText
	 *            a string containing the text of the cfe manual
	 */
	protected abstract void loadEndPositions(CFEManualSection section, String manualText);

	/**
	 * builds manual section map relating manual section name to manual section objects
	 */
	private void buildManualSectionMap() {
		manualSectionMap = new LinkedHashMap<String, CFEManualSection>();
		addToMap(root);
	}

	/**
	 * recursively loads manual section objects into the manual section hash map
	 * 
	 * @param section
	 *            the section to load into the map (and its children sections through recursive calls)
	 */
	private void addToMap(CFEManualSection section) {
		manualSectionMap.put(section.id, section);
		for (CFEManualSection subSection : section.subSections.values()) {
			addToMap(subSection);
		}
	}

	private void buildExamSectionLookup() {
		examSectionLookup = new HashMap<String, String>();

		examSectionLookup.put("2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes",
				"Financial Transactions and Fraud Schemes");
		examSectionLookup.put("2011 Fraud Examiners Manual/Introduction", "Financial Transactions and Fraud Schemes");
		examSectionLookup.put("2011 Fraud Examiners Manual/Law", "Law");
		examSectionLookup.put("2011 Fraud Examiners Manual/Investigation", "Investigation");
		examSectionLookup.put("2011 Fraud Examiners Manual/Fraud Prevention and Deterrence",
				"Fraud Prevention and Deterrence");
	}

	/**
	 * builds the question section lookup index. used to determine/assign the question section to assign to a manual
	 * section (when it's being initialized - see constructor of CFEManualSection) based on the value in id.
	 */
	private void buildQuestionSectionLookup() {

		questionSectionLookup = new HashMap<String, String>();

		// this first key-value pair is to be assigned to the root cfe manual section.
		// it does not map to any real section, but simply serves as a starting point
		// to handle the root, and the logic that follows in the implementation for
		// assigning question sections to cfe manual sections.
		questionSectionLookup.put("2011 Fraud Examiners Manual", "2011 Fraud Examiners Manual");

		// also, these section heading sections don't belong anywhere natural, either. So set up
		// a separate miscellaneous folder for each of these.
		questionSectionLookup.put("2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes",
				"ZZ_Miscellaneous");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Law", "ZZ_Miscellaneous");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation", "ZZ_Miscellaneous");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Fraud Prevention and Deterrence", "ZZ_Miscellaneous");

		// the rest are simply the reverse of the question section -> manual section pairs in
		// the manual section lookup.
		questionSectionLookup.put("2011 Fraud Examiners Manual/Introduction", "Introduction to Fraud Examination");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/ACCOUNTING CONCEPTS",
				"Basic Accounting Concepts");
		questionSectionLookup
				.put("2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/MANAGEMENT’S, AUDITORS’, AND FRAUD EXAMINERS’ RESPONSIBILITIES",
						"Manager's and Auditor's Responsibilities");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/BRIBERY AND CORRUPTION",
				"Bribery and Corruption");
		questionSectionLookup
				.put("2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/ASSET MISAPPROPRIATION: CASH RECEIPTS",
						"Cash Receipts Schemes");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/FINANCIAL STATEMENT FRAUD",
				"Financial Statement Fraud");
		questionSectionLookup
				.put("2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/ASSET MISAPPROPRIATION: FRAUDULENT DISBURSEMENTS",
						"Fraudulent Disbursements");
		questionSectionLookup
				.put("2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/ASSET MISAPPROPRIATION: INVENTORY AND OTHER ASSETS",
						"Inventory and Other Assets");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/THEFT OF INTELLECTUAL PROPERTY",
				"Theft of Intellectual Property");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/BANKRUPTCY FRAUD",
				"Bankruptcy Fraud");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/CHECK AND CREDIT CARD FRAUD",
				"Check and Credit Card Fraud");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/COMPUTER AND INTERNET FRAUD",
				"Computer and Internet Fraud");
		questionSectionLookup
				.put("2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/CONSUMER FRAUD",
						"Consumer Fraud");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/CONTRACT AND PROCUREMENT FRAUD",
				"Contract and Procurement Fraud");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/FINANCIAL INSTITUTION FRAUD",
				"Financial Institution Fraud");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/HEALTH CARE FRAUD",
				"Health Care Fraud");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/INSURANCE FRAUD",
				"Insurance Fraud");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/MONEY LAUNDERING",
				"Money Laundering");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/PUBLIC SECTOR FRAUD",
				"Public Sector Fraud");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/SECURITIES FRAUD",
				"Securities Fraud");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/TAX FRAUD",
				"Tax Fraud");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/FRAUD PREVENTION PROGRAMS",
				"Fraud Prevention Programs");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/FRAUD RISK ASSESSMENT",
				"Fraud Risk Assessment");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/ETHICS FOR FRAUD EXAMINERS",
				"Ethics for Fraud Examiners");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/ACFE CODE OF PROFESSIONAL ETHICS",
				"Ethics for Fraud Examiners");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/CFE CODE OF PROFESSIONAL STANDARDS",
				"Ethics for Fraud Examiners");
		questionSectionLookup
				.put("2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/PUNISHMENT AND THE CRIMINAL JUSTICE SYSTEM",
						"Punishment");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/THEORIES OF CRIME CAUSATION",
				"Criminology");
		questionSectionLookup.put(
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/UNDERSTANDING HUMAN BEHAVIOR",
				"Criminology");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/OCCUPATIONAL FRAUD",
				"Occupational Fraud");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/ORGANIZATIONAL CRIME",
				"Organizational Crime");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/WHITE-COLLAR CRIME",
				"White-Collar Crime");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/ANALYZING DOCUMENTS",
				"Analyzing Documents");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/DATA ANALYSIS AND REPORTING TOOLS",
				"Data Analysis");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/DIGITAL FORENSICS", "Digital Forensics");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/REPORTING STANDARDS", "Written Reports");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/FRAUD EXAMINATION CHECKLIST",
				"Written Reports");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/SAMPLE FRAUD EXAMINATION REPORTS",
				"Written Reports");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/SAMPLE FORMS", "Written Reports");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/ENGAGEMENT CONTRACTS OPINION LETTERS",
				"Written Reports");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/COVERT EXAMINATIONS",
				"Covert Examinations");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/TRACING ILLICIT TRANSACTIONS",
				"Illicit Transactions");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/SOURCES OF INFORMATION",
				"Sources of Information");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/ACCESSING INFORMATION ONLINE",
				"Sources of Information");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/INTERVIEW THEORY AND APPLICATION",
				"Interview Theory and Application");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Investigation/INTERVIEW THEORY AND APPLICATION",
				"Interviewing Suspects and Signed Statements");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Law/BASIC PRINCIPLES OF EVIDENCE", "Evidence");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Law/INDIVIDUAL RIGHTS DURING EXAMINATIONS",
				"Legal Rights of Employees");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Law/TESTIFYING AS AN EXPERT WITNESS",
				"Testifying as an Expert Witness");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Law/THE LAW RELATED TO FRAUD (PART 1)",
				"Law Related to Fraud");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Law/THE LAW RELATED TO FRAUD (PART 2)",
				"Law Related to Fraud");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Law/OVERVIEW OF THE UNITED STATES LEGAL SYSTEM",
				"Overview of the Legal System");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Law/THE CIVIL JUSTICE SYSTEM", "Civil Justice System");
		questionSectionLookup.put("2011 Fraud Examiners Manual/Law/CRIMINAL PROSECUTIONS FOR FRAUD",
				"Criminal Prosecutions for Fraud");

	}

	/**
	 * loads the manual section lookup which maps exam section names to cfe manual section names.
	 * 
	 * TODO: Change the architecture of this data structure so that the value for each key is a linked list of strings
	 * instead of a single string, in order to allow a single question section to map to multiple manual sections. In
	 * certain cases, this is necessary, such as for the question section, "The Law Related to Fraud" where there are
	 * multiple manual sections: "The Law Related to Fraud (Part 1)", AND "The Law Related to Fraud (Part 2)".
	 * 
	 * list of additions needed:
	 * 
	 * (NOTE: The sub-headings should be in all capitals when adding to table.)
	 * 
	 * The Law Related to Fraud: The Law Related to Fraud (Part 1) The Law Related to Fraud (Part 2)
	 * 
	 * Ethics for Professional Examiners: Ethics for Fraud Examiners ACFE Code of Professional Standards CFE Code of
	 * Professional Standards
	 * 
	 * Criminology: Theories of Crime Causation Understanding Human Behavior
	 * 
	 * Sources of Information: Sources of Information Accessing Information Online
	 * 
	 * Written Reports: Fraud Examination Checklist Sample Fraud Examination Reports Sample Forms Engagement Contracts
	 * and Opinion Letters
	 */
	private void buildManualSectionLookup() {

		manualSectionLookup = new HashMap<String, String>();

		manualSectionLookup.put("Introduction to Fraud Examination", "2011 Fraud Examiners Manual/Introduction");
		manualSectionLookup.put("Basic Accounting Concepts",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/ACCOUNTING CONCEPTS");
		manualSectionLookup
				.put("Manager's and Auditor's Responsibilities",
						"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/MANAGEMENT’S, AUDITORS’, AND FRAUD EXAMINERS’ RESPONSIBILITIES");
		manualSectionLookup.put("Bribery and Corruption",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/BRIBERY AND CORRUPTION");
		manualSectionLookup
				.put("Cash Receipts Schemes",
						"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/ASSET MISAPPROPRIATION: CASH RECEIPTS");
		manualSectionLookup.put("Financial Statement Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/FINANCIAL STATEMENT FRAUD");
		manualSectionLookup
				.put("Fraudulent Disbursements",
						"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/ASSET MISAPPROPRIATION: FRAUDULENT DISBURSEMENTS");
		manualSectionLookup
				.put("Inventory and Other Assets",
						"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/ASSET MISAPPROPRIATION: INVENTORY AND OTHER ASSETS");
		manualSectionLookup.put("Theft of Intellectual Property",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/THEFT OF INTELLECTUAL PROPERTY");
		manualSectionLookup.put("Bankruptcy Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/BANKRUPTCY FRAUD");
		manualSectionLookup.put("Check and Credit Card Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/CHECK AND CREDIT CARD FRAUD");
		manualSectionLookup.put("Computer and Internet Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/COMPUTER AND INTERNET FRAUD");
		manualSectionLookup.put("Consumer Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/CONSUMER FRAUD");
		manualSectionLookup.put("Contract and Procurement Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/CONTRACT AND PROCUREMENT FRAUD");
		manualSectionLookup.put("Financial Institution Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/FINANCIAL INSTITUTION FRAUD");
		manualSectionLookup.put("Health Care Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/HEALTH CARE FRAUD");
		manualSectionLookup.put("Insurance Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/INSURANCE FRAUD");
		manualSectionLookup.put("Money Laundering",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/MONEY LAUNDERING");
		manualSectionLookup.put("Public Sector Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/PUBLIC SECTOR FRAUD");
		manualSectionLookup.put("Securities Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/SECURITIES FRAUD");
		manualSectionLookup.put("Tax Fraud",
				"2011 Fraud Examiners Manual/Financial Transactions and Fraud Schemes/TAX FRAUD");
		manualSectionLookup.put("Fraud Prevention Programs",
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/FRAUD PREVENTION PROGRAMS");
		manualSectionLookup.put("Fraud Risk Assessment",
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/FRAUD RISK ASSESSMENT");
		manualSectionLookup.put("Ethics for Fraud Examiners",
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/ETHICS FOR FRAUD EXAMINERS");
		manualSectionLookup
				.put("Punishment",
						"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/PUNISHMENT AND THE CRIMINAL JUSTICE SYSTEM");
		manualSectionLookup.put("Criminology",
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/THEORIES OF CRIME CAUSATION");
		manualSectionLookup.put("Occupational Fraud",
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/OCCUPATIONAL FRAUD");
		manualSectionLookup.put("Organizational Crime",
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/ORGANIZATIONAL CRIME");
		manualSectionLookup.put("White-Collar Crime",
				"2011 Fraud Examiners Manual/Fraud Prevention and Deterrence/WHITE-COLLAR CRIME");
		manualSectionLookup.put("Analyzing Documents", "2011 Fraud Examiners Manual/Investigation/ANALYZING DOCUMENTS");
		manualSectionLookup.put("Data Analysis",
				"2011 Fraud Examiners Manual/Investigation/DATA ANALYSIS AND REPORTING TOOLS");
		manualSectionLookup.put("Digital Forensics", "2011 Fraud Examiners Manual/Investigation/DIGITAL FORENSICS");
		manualSectionLookup.put("Written Reports", "2011 Fraud Examiners Manual/Investigation/REPORTING STANDARDS");
		manualSectionLookup.put("Covert Examinations", "2011 Fraud Examiners Manual/Investigation/COVERT EXAMINATIONS");
		manualSectionLookup.put("Illicit Transactions",
				"2011 Fraud Examiners Manual/Investigation/TRACING ILLICIT TRANSACTIONS");
		manualSectionLookup.put("Sources of Information",
				"2011 Fraud Examiners Manual/Investigation/SOURCES OF INFORMATION");
		manualSectionLookup.put("Interview Theory and Application",
				"2011 Fraud Examiners Manual/Investigation/INTERVIEW THEORY AND APPLICATION");
		manualSectionLookup.put("Interviewing Suspects and Signed Statements",
				"2011 Fraud Examiners Manual/Investigation/INTERVIEW THEORY AND APPLICATION");
		manualSectionLookup.put("Evidence", "2011 Fraud Examiners Manual/Law/BASIC PRINCIPLES OF EVIDENCE");
		manualSectionLookup.put("Legal Rights of Employees",
				"2011 Fraud Examiners Manual/Law/INDIVIDUAL RIGHTS DURING EXAMINATIONS");
		manualSectionLookup.put("Testifying as an Expert Witness",
				"2011 Fraud Examiners Manual/Law/TESTIFYING AS AN EXPERT WITNESS");
		manualSectionLookup.put("Law Related to Fraud",
				"2011 Fraud Examiners Manual/Law/THE LAW RELATED TO FRAUD (PART 1)");
		manualSectionLookup.put("Overview of the Legal System",
				"2011 Fraud Examiners Manual/Law/OVERVIEW OF THE UNITED STATES LEGAL SYSTEM");
		manualSectionLookup.put("Civil Justice System", "2011 Fraud Examiners Manual/Law/THE CIVIL JUSTICE SYSTEM");
		manualSectionLookup.put("Criminal Prosecutions for Fraud",
				"2011 Fraud Examiners Manual/Law/CRIMINAL PROSECUTIONS FOR FRAUD");
	}

	/**
	 * retrieves manual section for a given manual section name
	 * 
	 * @param manualSectionId
	 * @return manual section
	 */
	public CFEManualSection getManualSection(String manualSectionId) {
		return manualSectionMap.get(manualSectionId);
	}

	/**
	 * retrieves manual section for a given question section name
	 * 
	 * @param questionSectionName
	 * @return manual section
	 */
	public CFEManualSection getManualSectionForQuestionSection(String questionSectionName) {
		String manualSectionId = manualSectionLookup.get(questionSectionName.trim());
		return manualSectionMap.get(manualSectionId);
	}

	/**
	 * retrieves question section name for a given manual section name
	 * 
	 * @param manualSectionId
	 *            the name of the section for which to retrieve the question section
	 * @return question section name
	 */
	public String getQuestionSectionForManualSection(String manualSectionId) {
		return questionSectionLookup.get(manualSectionId);
	}

	/**
	 * accessor for the root manual section object.
	 * 
	 * @return the root manual section object.
	 */
	public CFEManualSection getRoot() {
		return root;
	}

	public List<CFEManualSection> getErrors() {
		return errors;
	}

	/**
	 * toString() method is just a call to the toString() method of the root manual section object.
	 */
	@Override
	public String toString() {
		return root.toString();
	}
}