package financial.fraud.cfe.agent;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class TestHelloWorld {
	public static void main(String[] args) throws Exception {
		BufferedReader br = new BufferedReader(new FileReader("manual//2011_zz_fem_manual_text.txt"));
		try{
			StringBuilder fileContents = new StringBuilder();
			String line;
			while((line = br.readLine()) != null) {
				fileContents.append(line + "\n");
			}
			String contents = new String(fileContents);
			System.out.println(contents);
		} catch(IOException e) {
			e.printStackTrace();
		}
		
		
		
//		Scanner input = new Scanner(new File("manual//2011_zz_fem_manual_text.txt"));
//		input.useDelimiter("\\Z");
//		String test = input.next();
//		System.out.println(test);
	}

}
