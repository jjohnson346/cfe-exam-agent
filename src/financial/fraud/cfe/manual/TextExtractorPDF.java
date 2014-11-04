package financial.fraud.cfe.manual;

import java.io.IOException;
import java.io.PrintStream;

import org.apache.pdfbox.exceptions.CryptographyException;
import org.apache.pdfbox.exceptions.InvalidPasswordException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.util.PDFTextStripper;


/**
 * PDFTextExtractor encapsulates logic for extracting text from a pdf document.
 * It makes use of code provided by Apache Commons.
 * 
 * @author Joe
 *
 */
public class TextExtractorPDF implements TextExtractor {
	
	private PDDocument document;			// PDFBox component for converting pdf file to text

	/**
	 * constructor converts the pdf document whose name is provided
	 * as an argument
	 * 
	 * @param fileName			the name of the pdf file to be converted to text
	 * @throws IOException		could be thrown by PDFBox code
	 */
	public TextExtractorPDF(String fileName) throws IOException {
		// Create the PDFBox objects.
		document = PDDocument.load(fileName);
		if (document.isEncrypted()) {
			try {
				document.decrypt("");
			} catch (InvalidPasswordException e) {
				System.err.println("Error: Document is encrypted with a password.");
				System.exit(1);
			} catch (CryptographyException e) {
				System.err.println("Error:  Unable to decrypt pdf document.");
			}
		}
	}
	
	/**
	 * retrieves the text of the pdf document using 
	 * PDFBox API methods.
	 */
	public String getText() throws IOException {
		PDFTextStripper stripper = new PDFTextStripper();
		return stripper.getText(document);
	}


	/**
	 * instantiates and executes the getText() method of the TextExtractorPDF, passing
	 * the name of the pdf version of the cfe manual to the constructor, and saving the output
	 * to a separate text file.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			//PDFTextExtractor pde = new PDFTextExtractor("T. Rowe Price Blue Chip Growth Fund.pdf");
			TextExtractorPDF pde = new TextExtractorPDF("..//manual//2011 Fraud Examiners Manual.pdf");			
			String contents = pde.getText();
			PrintStream output  = new PrintStream("..//manual//2011 Fraud Examiners Manual.2014.txt");
			output.printf("%s", contents);
			//System.out.println(contents);
		} catch (IOException e) {
			System.out.println("File not found.");
		}
	}
}
