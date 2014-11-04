package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Scanner;

/**
 * the phase 2 component compiler class of the cfe manual text scrubbing
 * system for removing the erroneous blanks inserted during the pdf-text
 * conversion process, using pdfbox.  This class instantiates the 
 * TextCompiler class that utilizes a particular algorithm for 
 * removing the blanks (TextCompilerBFS, TextCompilerFwdConcat,
 * TextCompilerMaxScore).
 * 
 * @author jjohnson346
 *
 */
public class ManualTextPhase2Compiler extends ManualTextCompiler {

	private final int MAX_HEADING_WORD_COUNT = 10;
	
	public ManualTextPhase2Compiler() {
		phase = "2";
	}

	/**
	 * attempts to remove erroneous blanks from manual text by applying
	 * compile() method of a class implementing extending the TextCompiler class
	 * that applies an algorithm for removing erroneous blanks in the text.
	 * (These classes include TextCompilerBFS, TextCompilerFwdConcat, and
	 * TextCompilerMaxScore, (currently TextCompilerFwdConcat is being used).)  
	 * Note that this method attempts to apply the
	 * algorithm to each line of manual considered *not* to be a header line.
	 * (what do we do for a header line?  correct by hand?)
	 * 
	 */
	@Override
	protected String generateTargetManualText(String sourceManualText) {
		StringBuilder targetManualText = new StringBuilder();
		TextCompiler textCompiler = new TextCompilerFwdConcat();
		String sourceLine = null;
		String targetLine = null;
		
		Scanner scanner = new Scanner(sourceManualText);
		
		while(scanner.hasNextLine()) {
			sourceLine = scanner.nextLine();
			List<Token> sourceInitialTokens = textCompiler.tokenizeInitial(sourceLine);
			if(sourceInitialTokens.size() <= MAX_HEADING_WORD_COUNT) {
				targetLine = textCompiler.compile(sourceLine);
			} else {
				targetLine = sourceLine;
			}
			targetManualText.append(targetLine);
		}
		
		return new String(targetManualText);
	}

	/**
	 * retrieves the text from the output file of the phase 1 compiler.
	 * 
	 * @return 		a string containing the text from the phase 1 compiler output file
	 */
	@Override
	protected String getSourceManualText() {
		String sourceManualText = null;

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("2011_fem_manual_text_phase_1.txt"));
			scanner.useDelimiter("\\Z");
			sourceManualText = scanner.next();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
		return sourceManualText;
	}

	/**
	 * performs unit text of phase 2 compiler.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		ManualTextPhase2Compiler manualTextP2Compiler = null;
		int input;

		do {
			System.out.println("ManualTextPhase2Compiler menu:");
			System.out.println("1.  Compile (P2) manual text.");
			System.out.println("2.  Quit.");

			Scanner scanner = new Scanner(System.in);
			input = scanner.nextInt();

			switch (input) {
			case 1:
				manualTextP2Compiler = new ManualTextPhase2Compiler();
				manualTextP2Compiler.compile();
				break;
			case 2:
				break;
			default:
				System.out.println("Invalid option.  Please enter a valid selection.");
			}
		} while (input != 2);
	}
}
