package cfe.manual.docs.collection;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class Document {
	
	private int id;
	
	private String title;

	private String path;

	private String examSection;
	
	private String questionSection;
	
	private String rawText;
	
	private ArrayList<String> tokens;
	
	private final int DOC_ID_AS_STRING_LENGTH = 4;
	

	public Document(String fileName) {
		String contents = null;
		Scanner input = null;
		
		File docFile = new File(fileName);
		id = Integer.parseInt(docFile.getName().substring(0, DOC_ID_AS_STRING_LENGTH));
		title = docFile.getName().substring(DOC_ID_AS_STRING_LENGTH).replace("-", "").replace(".txt", "");
		

		try {
			input = new Scanner(docFile);
			input.useDelimiter("\\Z");
			contents = input.next();

			String[] fields = contents.split("\n\n\n:\n\n\n");
			System.out.println(contents);
			path = fields[0];
			examSection = fields[1];
			questionSection = fields[2];
			rawText = fields[3];
			String tokensText = fields[4];
			tokens = new ArrayList<String>(Arrays.asList(tokensText.split(" |\n")));
		
		} catch (FileNotFoundException e) {
			System.out.println("File not found: " + fileName);
			System.exit(1);
		} finally {
			input.close();
		}
		
	}

	public String getPath() {
		return path;
	}

	public String getExamSection() {
		return examSection;
	}

	public String getQuestionSection() {
		return questionSection;
	}

	public String getRawText() {
		return rawText;
	}

	public ArrayList<String> getTokens() {
		return tokens;
	}
	
	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		sb.append("id: " + id + "\n\n");
		sb.append("title: " + title + "\n\n");
		sb.append("path:\n" + path + "\n\n");
		sb.append("raw text:\n" + rawText + "\n\n");
		sb.append("tokens:\n" + tokens);
		
		return new String(sb);
	}
	
	
	public static void main(String[] args) {
		Document doc = new Document("document collection/CFEManualSmallDocUnit/Financial Transactions and Fraud Schemes/Bankruptcy Fraud/1270-----Examiners.txt");
		System.out.println(doc);
		
	}
	
}
