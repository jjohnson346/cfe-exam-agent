package financial.fraud.cfe.agent;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * AppConfig encapsulates the logic for loading the configuration settings for the
 * CFEExamAgent software from the properties file.
 * 
 * @author jjohnson346
 *
 */
public class AppConfig {

	public final String EXAM_NAME;				// the name of the exam file from which to load the 
												// question file names
	
	public final String EXECUTION_MODE;			// stores whether the execution mode should be batch
												// or interactive.  In batch mode, the software 
												// sends all output to an output file, with no prompts
												// to the user.  In interactive mode, each question
												// is sent to the console, the user is prompted to hit
												// return before the software continues on to the next
												// question.
	
	public final String LOGGING_LEVEL;			// stores the logging level - none, minimal, medium, or full 

	/**
	 * no-arg constructor loads the configuration settings from the properties file,
	 * including settings for the exam file name, execution mode (batch or interactive),
	 * and logging level (none, minimal, medium, or full).
	 * 
	 */
	public AppConfig() {
		Properties p = new Properties();

		String examNameTemp = null;
		String executionModeTemp = null;
		String loggingLevelTemp = null;

		// retrieve configuration settings from the .properties file.
		try {
			p.load(new FileInputStream("config//cfe-exam-agent.properties"));

			examNameTemp = new String(p.getProperty("Exam"));
			executionModeTemp = p.getProperty("ExecutionMode");
			loggingLevelTemp = p.getProperty("LoggingLevel");

		} catch (IOException e) {
			e.printStackTrace();
			File dir1 = new File(".");
			File dir2 = new File("..");
			try {
				System.out.println(dir1.getCanonicalPath());
			} catch (IOException e2) {
				e2.printStackTrace();
			}
			System.out.printf("%s\n",
					"Unable to load file, cfe-exam-agent.properties");
		} finally {
			EXAM_NAME = examNameTemp;
			EXECUTION_MODE = executionModeTemp;
			LOGGING_LEVEL = loggingLevelTemp;
		}
	}
}
