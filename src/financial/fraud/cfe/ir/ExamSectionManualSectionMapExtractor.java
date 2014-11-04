package financial.fraud.cfe.ir;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExamSectionManualSectionMapExtractor {

	public static void main(String[] args) {
		try {
			Scanner input = new Scanner(new File("manual/question_section-manual_section-lookup-code.txt"));
			
			BufferedWriter writer = new BufferedWriter(new FileWriter(new File("manual/manual_section-testing_section-lookup-code.txt")));
			String regex = "manualSectionLookup\\.put\\(\"(.+?)\", \"(.+?)\"\\);";
			
			input.useDelimiter("\\Z");
			String mapFileContents = input.next();

			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(mapFileContents);
			while(m.find()) {
				String examSection = m.group(1);
				String manualSection = m.group(2);
				System.out.println(examSection + " : " + manualSection);
				
				writer.write("questionSectionLookup.put(\"" + manualSection + "\", \"" + examSection + "\");\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
