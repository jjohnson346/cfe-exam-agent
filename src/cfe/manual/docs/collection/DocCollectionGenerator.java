package cfe.manual.docs.collection;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;
import org.json.simple.JSONObject;

import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.manual.CFEManualSection;
import financial.fraud.cfe.manual.CFEManualSmallDocUnit;

/**
 * DocCollectionGenerator creates a collection of Document objects each of which
 * contains a section from the cfe manual, broken down along the lines defined
 * by the CFEManual object, and then serializes these objects to disk with 
 * paths/file names matching their section names.  This class makes use of 
 * concrete classes that implement the CFEManual interface and structure the
 * manual as a tree in memory.
 * 
 * Currently, there are 2 CFEManual classes - CFEManualLargeDocUnit and
 * CFEManualSmallDocUnit. There is, in fact, a third, CFEManualSmallDocUnit2,
 * that bases its searches for section headers on regular expressions (instead
 * of simple string searches). However, it does not appear that this one is
 * actively being used as the basis for a document collection.
 * 
 * @author joejohnson
 *
 */
public class DocCollectionGenerator {

	private CFEManual cfeManual;

	private String cfeManualClassName;

	private final String DOC_DELIMITER = "\n\n\n:\n\n\n";

	public DocCollectionGenerator(CFEManual cfeManual) {
		this.cfeManual = cfeManual;
		String cfeManualFullClassName = cfeManual.getClass().getName();
		cfeManualClassName = StringUtils.substringAfterLast(
				cfeManualFullClassName, ".");
	}

	// public void buildDocCollection() {
	// try {
	//
	// File docCollectionDir = new File("document collection");
	// if (!docCollectionDir.exists())
	// docCollectionDir.mkdir();
	//
	// File cfeManualDir = new File("document collection/" +
	// cfeManualClassName);
	// // if doc collection already exists, return.
	// if (cfeManualDir.exists()) {
	// System.out.println("Document collection already exists for " +
	// cfeManualClassName);
	// return;
	// }
	//
	// // doc collection for cfeManual does not exist, so go ahead and make it.
	// cfeManualDir.mkdir();
	// CFEManualSection root = cfeManual.getRoot();
	// File sectionDir = new File("document collection/" + cfeManualClassName +
	// "/" + root.id);
	// sectionDir.mkdir();
	//
	// File sectionFile = new File("document collection/" + cfeManualClassName +
	// "/" + root.id + "/" + root.name
	// + ".txt");
	// sectionFile.createNewFile();
	// outputSectionToFile(root, sectionFile);
	//
	// for (CFEManualSection section : root.subSections.values()) {
	// File subSectionDir = new File("document collection/" + cfeManualClassName
	// + "/" + root.id + "/"
	// + section.name);
	//
	// subSectionDir.mkdir();
	//
	// File subSectionFile = new File("document collection/" +
	// cfeManualClassName + "/" + root.id + "/"
	// + section.name + "/" + section.name + ".txt");
	// subSectionFile.createNewFile();
	// outputSectionToFile(section, subSectionFile);
	//
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	// public void buildDocCollection2() {
	// try {
	// String cfeManualFullClassName = cfeManual.getClass().getName();
	// String cfeManualClassName =
	// StringUtils.substringAfterLast(cfeManualFullClassName, ".");
	// //
	// cfeManualFullClassName.substring(cfeManualFullClassName.lastIndexOf(".")
	// + 1);
	//
	// File docCollectionDir = new File("document collection");
	// if (!docCollectionDir.exists())
	// docCollectionDir.mkdir();
	//
	// File cfeManualDir = new File("document collection/" +
	// cfeManualClassName);
	// // if doc collection already exists, return.
	// if (cfeManualDir.exists()) {
	// System.out.println("Document collection already exists for " +
	// cfeManualClassName);
	// return;
	// }
	//
	// // doc collection for cfeManual does not exist, so go ahead and make it.
	// cfeManualDir.mkdir();
	// CFEManualSection root = cfeManual.getRoot();
	//
	// File sectionFile = new File("document collection/" + cfeManualClassName +
	// "/" + docIdAsString(4, 1) + "-"
	// + root.name + ".txt");
	// sectionFile.createNewFile();
	// outputSectionToFile(root, sectionFile);
	//
	// int i = 1;
	// for (CFEManualSection section : root.subSections.values()) {
	// File subSectionFile = new File("document collection/" +
	// cfeManualClassName + "/"
	// + docIdAsString(4, ++i) + "--" + section.name);
	//
	// outputSectionToFile(section, subSectionFile);
	//
	// }
	// } catch (IOException e) {
	// e.printStackTrace();
	// }
	// }
	//
	public void buildDocCollection3() {
		try {
			String cfeManualFullClassName = cfeManual.getClass().getName();
			String cfeManualClassName = StringUtils.substringAfterLast(
					cfeManualFullClassName, ".");

			File docCollectionDir = new File("document collection");
			if (!docCollectionDir.exists())
				docCollectionDir.mkdir();

			File cfeManualDir = new File("document collection/"
					+ cfeManualClassName);
			// if doc collection already exists, return.
			if (cfeManualDir.exists()) {
				System.out.println("Document collection already exists for "
						+ cfeManualClassName);
				return;
			}

			// doc collection for cfeManual does not exist, so go ahead and make
			// it.
			cfeManualDir.mkdir();
			CFEManualSection root = cfeManual.getRoot();

			// create documents, recursively.
			// start with docId = 0 (not 1) so that a list of Document objects
			// are numbered consistently with their index locations in the list.
			createDocument(0, 1, root);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private int createDocument(int docId, int depth, CFEManualSection section)
			throws IOException {
		// first, make sure the file name and question section name consists of
		// only valid characters.
		String nameOfFileForSection = section.name.replace(": ", " - ")
				.replace("/", "-");
		String examSectionDirName = null;
		String questionSectionDirName = null;
		String documentFileName = null;

		try {

			// create exam section directory, if necessary.
			examSectionDirName = "document collection/" + cfeManualClassName
					+ "/" + section.examSection;
			File examSectionDir = new File(examSectionDirName);
			if (!examSectionDir.exists())
				examSectionDir.mkdir();

			// create question section directory, if necessary.
			questionSectionDirName = examSectionDirName + "/"
					+ section.questionSection;
			File questionSectionDir = new File(questionSectionDirName);
			if (!questionSectionDir.exists())
				questionSectionDir.mkdir();

			// create document file.
			documentFileName = questionSectionDirName + "/"
					+ docIdAsString(4, docId) + StringUtils.repeat("-", depth)
					+ nameOfFileForSection + ".txt";
			File documentFile = new File(documentFileName);
			documentFile.createNewFile();

			// build contents string for document.
			String documentText = buildDocumentText(section);
			// output contents to file.
			outputToFile(documentFile, documentText);

			System.out.println("File created: " + documentFile.getName());
			docId++;

			for (CFEManualSection subSection : section.subSections.values()) {
				docId = createDocument(docId, depth + 1, subSection);
			}
		} catch (IOException e) {
			System.out.println("Error writing to file: " + documentFileName);
		}
		return docId;
	}

	private String buildDocumentText(CFEManualSection section) {
		// build contents string for document.
		StringBuilder sb = new StringBuilder();
		sb.append(section.id);
		sb.append(DOC_DELIMITER + section.examSection);
		sb.append(DOC_DELIMITER + section.questionSection);
		sb.append(DOC_DELIMITER + section.getText());
		sb.append(DOC_DELIMITER + stemSectionText(section));
		// sb.append(String.format("%s%s\n\n", "Path=", section.id));
		// sb.append(String.format("%s%s\n\n", "Exam Section=",
		// section.examSection));
		// sb.append(String.format("%s%s\n\n", "Question Section=",
		// section.questionSection));
		// sb.append(String.format("%s%s\n\n", "Raw Text=", section.getText()));
		// sb.append(String.format("%s%s\n\n", "Stemmed Text=",
		// stemSectionText(section)));
		return new String(sb);
	}

	// private String buildDocumentText(CFEManualSection section) {
	// JSONObject obj = new JSONObject();
	// obj.put("id", section.id);
	// obj.put("examSection", section.examSection);
	// obj.put("questionSection", section.questionSection);
	// obj.put("rawText", section.getText());
	// obj.put("stemmedText", stemSectionText(section));
	//
	// return obj.toJSONString();
	// }

	/**
	 * outputs the CFE manual section text for a given manual section name to a
	 * file.
	 * 
	 * @param fileName
	 *            the name of the file to which to print the section content
	 * @param section
	 *            the section whose text is to be output to the file
	 */
	// private void outputSectionToFile(CFEManualSection section, File
	// sectionFile) {
	// try {
	// StringBuilder sb = new StringBuilder();
	// sb.append(section.id);
	// sb.append(DOC_DELIMITER + section.questionSection);
	// sb.append(DOC_DELIMITER + section.getText());
	// sb.append(DOC_DELIMITER + stemSectionText(section));
	//
	// outputTextToFile(sectionFile, new String(sb));
	// System.out.println("File created: " + sectionFile.getName());
	// } catch (IOException e) {
	// System.out.println("Error writing to file.");
	// }
	// }

	private void outputSectionToFile(CFEManualSection section, File sectionFile) {
		try {
			StringBuilder sb = new StringBuilder();
			sb.append(String.format("%s%s\n\n", "Id=", section.id));
			sb.append(String.format("%s%s\n\n", "Question Section=",
					section.questionSection));
			sb.append(String.format("%s%s\n\n", "Raw Text=", section.getText()));
			sb.append(String.format("%s%s\n\n", "Stemmed Text=",
					stemSectionText(section)));

			outputToFile(sectionFile, new String(sb));
			System.out.println("File created: " + sectionFile.getName());
		} catch (IOException e) {
			System.out.println("Error writing to file.");
		}
	}

	/**
	 * outputs the CFE manual section text for a given manual section name to a
	 * file.
	 * 
	 * @param fileName
	 *            the name of the file to which to print the section content
	 * @param section
	 *            the section whose text is to be output to the file
	 */
	private void outputSectionToJSONFile(CFEManualSection section,
			File sectionFile) {
		try {
			JSONObject objSection = new JSONObject();
			objSection.put("Path", section.id);
			objSection.put("RawText", section.getText());
			outputToFile(sectionFile, objSection.toJSONString());
			System.out.println("File created: " + sectionFile.getName());
		} catch (IOException e) {
			System.out.println("Error writing to file.");
		}
	}

	/**
	 * acts as a helper function for the public overloaded outputSectionText
	 * methods. Creates a Writer object for writing to a file, flushing the
	 * writer buffer every 2000 characters as it does so.
	 * 
	 * @param fileName
	 *            the name of the file to which to output the text
	 * @param text
	 *            the text to output
	 * @throws IOException
	 *             could be thrown if can't make file
	 */
	private void outputToFile(File sectionFile, String text) throws IOException {

		BufferedWriter output = new BufferedWriter(new FileWriter(sectionFile));
		for (int i = 0; i < text.length(); i += 2000) {
			if (i + 2000 > text.length())
				output.write(text, i, text.length() - i);
			else
				output.write(text, i, 2000);
			output.flush();
		}
		output.close();
	}

	// private void outputToFile(File sectionFile, String text) throws
	// IOException {
	//
	// BufferedWriter output = new BufferedWriter(new FileWriter(sectionFile));
	// Scanner scanner = new Scanner(text);
	// while (scanner.hasNextLine()) {
	// output.write(scanner.nextLine());
	// output.flush();
	// }
	// output.close();
	// }

	private String stemSectionText(CFEManualSection section) {
		Scanner input = new Scanner(section.getText());
		PorterStemmer stemmer = new PorterStemmer();
		StringBuilder sb = new StringBuilder();

		while (input.hasNextLine()) {
			// make sure everything is lowercase
			String line = input.nextLine().toLowerCase();
			boolean emptyLine = true;

			// split on whitespace
			for (String s : line.split("\\s+")) {
				// Remove non alphanumeric characters
				s = s.replaceAll("[^a-zA-Z0-9]", "");
				// Stem word.
				s = stemmer.stem(s);
				if (!s.equals("")) {
					if (!emptyLine) {
						sb.append(" ");
					}
					sb.append(s);
					emptyLine = false;
				}
			}
			if (!emptyLine) {
				sb.append("\n");
			}
		}
		return new String(sb);
	}

	public void cleanDocCollection() {
		String cfeManualFullClassName = cfeManual.getClass().getName();
		String cfeManualClassName = cfeManualFullClassName
				.substring(cfeManualFullClassName.lastIndexOf(".") + 1);
		File cfeManualDir = new File("document collection/"
				+ cfeManualClassName);
		try {
			delete(cfeManualDir);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void delete(File file) throws IOException {

		if (file.isDirectory()) {

			// directory is empty, then delete it
			if (file.list().length == 0) {

				file.delete();
				System.out.println("Directory deleted: " + file.getName());

			} else {

				// list all the directory contents
				String files[] = file.list();

				for (String temp : files) {
					// construct the file structure
					File fileDelete = new File(file, temp);

					// recursive delete
					delete(fileDelete);
				}

				// check the directory again, if empty then delete it
				if (file.list().length == 0) {
					file.delete();
					System.out.println("Directory deleted: " + file.getName());
				}
			}

		} else {
			// if file, then delete it
			file.delete();
			System.out.println("File deleted: " + file.getName());
		}
	}

	public static String docIdAsString(int targetLength, int docId) {
		if (docId == 0) {
			return StringUtils.repeat("0", targetLength);
		} else {
			int docIdLength = (int) Math.log10(docId) + 1; // stores the length
															// of docId
			return StringUtils.repeat("0", targetLength - docIdLength)
					+ String.valueOf(docId);
		}
	}

	public static void main(String[] args) {
		 DocCollectionGenerator dcu = new DocCollectionGenerator(new
		 CFEManualSmallDocUnit());
//		DocCollectionGenerator dcu = new DocCollectionGenerator(
//				new CFEManualLargeDocUnit());

		dcu.cleanDocCollection();
		dcu.buildDocCollection3();

	}
}
