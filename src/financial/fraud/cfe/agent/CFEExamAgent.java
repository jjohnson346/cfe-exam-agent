package financial.fraud.cfe.agent;

import java.io.FileNotFoundException;
import java.util.Scanner;

import financial.fraud.cfe.algorithm.AlgorithmType;
import financial.fraud.cfe.algorithm.AllOfTheAbove;
import financial.fraud.cfe.algorithm.BagOfWords;
import financial.fraud.cfe.algorithm.CompositeFrequency;
import financial.fraud.cfe.algorithm.FalseSelect;
import financial.fraud.cfe.algorithm.IAlgorithm;
import financial.fraud.cfe.algorithm.IAlgorithmFrequency;
import financial.fraud.cfe.algorithm.MaxFreqPlus;
import financial.fraud.cfe.algorithm.MaxFrequency;
import financial.fraud.cfe.algorithm.MinFrequency;
import financial.fraud.cfe.algorithm.Randomization;
import financial.fraud.cfe.algorithm.TrueSelect;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;
import financial.fraud.cfe.util.ProfileData;

/**
 * An instance of CFEExamAgent represents an agent taking a cfe exam. This is
 * the entry point class for the cfe exam agent software, in particular, the
 * main method, which instantiates the CFExamAgent and executes the takeExam()
 * method.
 * 
 * @author jjohnson346
 *
 */
public class CFEExamAgent {
	private CFEManualLargeDocUnit cfeManual; // the cfe manual object that the
												// agent will use to study
	// up for the test. That is, the agent initializes this
	// reference in its own constructor to a new instance of the
	// manual.

	private CFEExam cfeExam; // a reference to an exam object that represents
								// the exam
	// the agent will take

	private IAlgorithm[] algos; // an array containing all of the algorithms
								// from which the
								// the agent can select the most believed to be
								// the most appropriate
								// one for answering each question

	private String executionMode; // batch or interactive?

	private ProfileData profileData; // stores the probabilities of success for
										// each question profile,

	// for each algorithm

	/**
	 * constructor that initializes algos array, loads profile data (probs of
	 * success by algo, and question profile), and sets execution mode.
	 * 
	 * @param executionMode
	 *            sets whether to run in batch or interactive mode
	 */
	public CFEExamAgent(String executionMode) {
		Logger.getInstance().println("Exam Agent initializing...",
				DetailLevel.MINIMAL);

		try {
			// load the cfe manual.
			cfeManual = new CFEManualLargeDocUnit();

			// 2.0.0 - modify constructor calls so that they have no parms.
			// populate algorithms array.
			algos = new IAlgorithm[AlgorithmType.values().length];
			algos[AlgorithmType.ALL_ABOVE.ordinal()] = new AllOfTheAbove();
			algos[AlgorithmType.TRUE_SELECT.ordinal()] = new TrueSelect();
			algos[AlgorithmType.FALSE_SELECT.ordinal()] = new FalseSelect();
			// algos[AlgorithmType.MAX_FREQ.ordinal()] = new
			// MaxFrequency(cfeManual);
			algos[AlgorithmType.MAX_FREQ.ordinal()] = new MaxFrequency();
			// algos[AlgorithmType.MAX_FREQ_PLUS.ordinal()] = new MaxFreqPlus(
			// cfeManual);
			algos[AlgorithmType.MAX_FREQ_PLUS.ordinal()] = new MaxFreqPlus();
			// algos[AlgorithmType.MIN_FREQ.ordinal()] = new MinFrequency(
			// cfeManual);
			algos[AlgorithmType.MIN_FREQ.ordinal()] = new MinFrequency();
			// algos[AlgorithmType.B_OF_W.ordinal()] = new
			// BagOfWords(cfeManual);
			algos[AlgorithmType.B_OF_W.ordinal()] = new BagOfWords();
			// algos[AlgorithmType.COMP_FREQ.ordinal()] = new
			// CompositeFrequency(
			// cfeManual);
			algos[AlgorithmType.COMP_FREQ.ordinal()] = new CompositeFrequency();
			algos[AlgorithmType.RANDOM.ordinal()] = new Randomization();

			// load profile data upon which to base algo selections.
			profileData = new ProfileData();
			profileData.load();

			// set execution mode and destinations.
			this.executionMode = executionMode;

			Logger.getInstance().println(
					"CFE Exam Agent initialization complete.",
					DetailLevel.MINIMAL);
		} catch (FileNotFoundException e) {
			for (StackTraceElement s : e.getStackTrace()) {
				Logger.getInstance().printf(DetailLevel.NONE, "%s", s);
			}
			Logger.getInstance().println(
					"CFE Exam Agent terminated unsuccessfully.",
					DetailLevel.NONE);
		}

	}

	/**
	 * initiates the taking of the test by the cfe agent. This is the primary
	 * method for the entire program.
	 * 
	 * @param examName
	 *            the name of the exam from which the exam file name will be
	 *            determined
	 */
	public void takeExam(String examName) {
		Scanner scanner = new Scanner(System.in);
		int numCorrect = 0;
		int response = 0;
		String result = null;
		CFEExamQuestion question = null;

		// change backslashes to forward slashes for mac version.
		// cfeExam = new CFEExam("exams\\" + examName + ".txt");
		cfeExam = new CFEExam("exams/" + examName + ".txt");

		Logger.getInstance().printf(DetailLevel.MINIMAL, "%s%s\n",
				"CFE EXAM:  ", cfeExam.NAME);
		Logger.getInstance().printf(DetailLevel.MINIMAL, "%s%s\n",
				"EXECUTION MODE:  ", executionMode);
		Logger.getInstance().printf(DetailLevel.MINIMAL, "\n%s\n\n",
				"Press Enter to continue.");

		// outputWriter.write("%s%s\n", "CFE EXAM:  ", cfeExam.NAME);
		// outputWriter.write("%s%s\n", "EXECUTION MODE:  ", executionMode);
		// outputWriter.write("\n%s\n\n", "Press Enter to continue.");
		scanner.nextLine();

		// attempt to solve each question in sequence through a for-loop.
		for (int i = 0; i < cfeExam.size(); i++) {
			question = cfeExam.getQuestion(i);

			Logger.getInstance().printf(DetailLevel.MINIMAL, "%s\n",
					cfeExam.getFormattedQuestion(i));
			// outputWriter.write("%s\n", cfeExam.getFormattedQuestion(i));

			if (executionMode.equals("Interactive")) {
				System.out
						.printf("\n%s\n\n", "Press Enter for agent response.");
				scanner.nextLine();
			}

			// select the algorithm for this question and use it to
			// answer the question.
			IAlgorithm bestAlgo = selectAlgorithm(question);
			response = bestAlgo.solve(question, cfeManual);

			Logger.getInstance().println(
					"Algorithm selected: " + bestAlgo.toString(),
					DetailLevel.MEDIUM);
			// System.out.println("Algorithm selected: " + bestAlgo.toString());

			if (bestAlgo instanceof IAlgorithmFrequency) {
				((IAlgorithmFrequency) bestAlgo).printOptionFrequencies();
			}

			if (response == question.correctResponse) {
				numCorrect++;
				result = "CORRECT.";
			} else {
				result = "INCORRECT. (Correct answer:  "
						+ question.getFormattedCorrectResponse() + ")";
			}

			Logger.getInstance().printf(DetailLevel.MINIMAL, "%s%s%s%s\n\n",
					"Agent response:  ",
					question.getFormattedResponse(response), " -- ", result);
			// outputWriter.write("%s%s%s%s\n\n", "Agent response:  ",
			// question.getFormattedResponse(response), " -- ", result);

			// if (executionMode.equals("Interactive") && bestAlgo instanceof
			// IAlgorithmFrequency) {
			//
			// // prompt user whether to output section to file. If yes, send it
			// to output stream.
			// System.out.println("Output the section text for this question to a file? (y/n):");
			//
			// if (scanner.nextLine().toLowerCase().equals("y")) {
			// outputSectionText(i + 1, question);
			// System.out.printf("\n%s\n\n", "Press Enter to continue.");
			// scanner.nextLine();
			// } else {
			// System.out.printf("\n\n");
			// }
			// }

			if (executionMode.equals("Interactive")) {
				System.out.printf("\n%s\n\n", "Press Enter to continue.");
				scanner.nextLine();
			}

		}

		Logger.getInstance().printf(DetailLevel.MINIMAL,
				"EXAM COMPLETE. Score:  %d out of %d\n\n\n", numCorrect,
				cfeExam.size());
	}

	/**
	 * selects the algorithm to use for a particular question, based on the
	 * profile data and the profile for the question.
	 * 
	 * @param question
	 * @return
	 */
	private IAlgorithm selectAlgorithm(CFEExamQuestion question) {
		int profileIndex = question.getProfile().getProfileIndex();

		Logger.getInstance().println("Question profile index: " + profileIndex,
				DetailLevel.MEDIUM);

		double[] algoSuccessProbs = profileData.successProbs[profileIndex];

		// log success probs selected based on this question's profile
		for (int i = 0; i < algoSuccessProbs.length; i++) {
			Logger.getInstance().printf(DetailLevel.MEDIUM, "%30s: %10.3f\n",
					algos[i].toString(), algoSuccessProbs[i]);
		}

		// TODO: the max prob algorithm remains constant for each
		// question profile. Thus, the optimal algorithm should be a hash map
		// lookup
		// (not a traversal of all probs for the question profile
		// for each question).
		int maxIdx = 0;
		for (int i = 0; i < algoSuccessProbs.length; i++) {
			if (algoSuccessProbs[i] > algoSuccessProbs[maxIdx]) {
				maxIdx = i;
			}
		}

		// return the algo corresponding to max index.
		return algos[maxIdx];
	}

	/**
	 * the entry point to the cfe exam agent software. instantiate a
	 * CFEExamAgent and kick off the takeExam() method.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		AppConfig appConfig = new AppConfig();

		Logger logger = Logger.getInstance();
		// change backward slashes to forward slashes for mac version.
		logger.addDestination("logs//cfe exam agent " + appConfig.EXAM_NAME
				+ ".log");
		// logger.addDestination("logs\\cfe exam agent " + appConfig.EXAM_NAME +
		// ".log");
		logger.setDetailLevel(DetailLevel.valueOf(appConfig.LOGGING_LEVEL));

		CFEExamAgent cfeExamAgent = new CFEExamAgent(appConfig.EXECUTION_MODE);
		cfeExamAgent.takeExam(appConfig.EXAM_NAME);
	}
}
