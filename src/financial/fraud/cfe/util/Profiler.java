package financial.fraud.cfe.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Formatter;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.algorithm.AlgorithmType;
import financial.fraud.cfe.algorithm.AllOfTheAbove;
import financial.fraud.cfe.algorithm.BagOfWords;
import financial.fraud.cfe.algorithm.CompositeFrequency;
import financial.fraud.cfe.algorithm.ConceptMatchV1;
import financial.fraud.cfe.algorithm.ConceptMatchV2;
import financial.fraud.cfe.algorithm.ConceptMatchV3;
import financial.fraud.cfe.algorithm.ConceptMatchV3NOT;
import financial.fraud.cfe.algorithm.ConceptMatchV3NOTA;
import financial.fraud.cfe.algorithm.FalseSelect;
import financial.fraud.cfe.algorithm.IAlgorithm;
import financial.fraud.cfe.algorithm.MaxFreqPlus;
import financial.fraud.cfe.algorithm.MaxFrequency;
import financial.fraud.cfe.algorithm.MinFrequency;
import financial.fraud.cfe.algorithm.Randomization;
import financial.fraud.cfe.algorithm.TrueSelect;
import financial.fraud.cfe.logging.DetailLevel;
import financial.fraud.cfe.logging.Logger;
import financial.fraud.cfe.manual.CFEManual;
import financial.fraud.cfe.manual.CFEManualLargeDocUnit;

/**
 * Profiler traverses the training set of questions, and determines for each question its profile (according to some
 * predefined characteristics), and then attempts to answer it using each of the available algorithms, keeping track of
 * the success rate for each algorithm on each question profile. When it is done with this process, Profiler stores
 * summary information - i.e., the success rate for each question type for each algorithm in a file to be used later by
 * the CFEExamAgent when selecting the algorithm for each question it confronts on an exam.
 * 
 * @author jjohnson346
 *
 */
public class Profiler {

	private IAlgorithm[] algos; // the array of algorithms to apply to each
								// question

	// 2.0.0 - added this variable as a class level variable.
	private CFEManual cfeManual; // the cfe manual to be passed to each
									// algorithm

	/**
	 * constructor loads the CFEManual object into memory and loads the various algorithm objects into an array.
	 * 
	 * @param logger
	 *            the logger to which to write log messages
	 */
	public Profiler(Logger logger) {

		Logger.getInstance().println("Profiler initializing...", DetailLevel.MINIMAL);

		// instantiate the cfe manual.
		// CFEManualLargeDocUnit cfeManual = new CFEManualLargeDocUnit();
		// 2.0.0 - modified this initialization to be for the class level
		// variable.
		cfeManual = new CFEManualLargeDocUnit();

		Logger.getInstance().println("Loading algorithms...", DetailLevel.MEDIUM);

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
		// algos[AlgorithmType.MIN_FREQ.ordinal()] = new
		// MinFrequency(cfeManual);
		algos[AlgorithmType.MIN_FREQ.ordinal()] = new MinFrequency();
		// algos[AlgorithmType.B_OF_W.ordinal()] = new BagOfWords(cfeManual);
		algos[AlgorithmType.B_OF_W.ordinal()] = new BagOfWords();
		// algos[AlgorithmType.COMP_FREQ.ordinal()] = new CompositeFrequency(
		// cfeManual);
		algos[AlgorithmType.COMP_FREQ.ordinal()] = new CompositeFrequency();

		algos[AlgorithmType.CM_V1.ordinal()] = new ConceptMatchV1();
		algos[AlgorithmType.CM_V2.ordinal()] = new ConceptMatchV2();
		algos[AlgorithmType.CM_V3.ordinal()] = new ConceptMatchV3();
		algos[AlgorithmType.CM_NOTA.ordinal()] = new ConceptMatchV3NOTA();
		algos[AlgorithmType.CM_NOT.ordinal()] = new ConceptMatchV3NOT();

		algos[AlgorithmType.RANDOM.ordinal()] = new Randomization();

		Logger.getInstance().println("Algorithm load complete.", DetailLevel.MEDIUM);
		Logger.getInstance().println("System ready.", DetailLevel.MINIMAL);
	}

	/**
	 * executes each algorithm on each question made available by the QuestionServer object, passed in as an input
	 * argument. Uses a ProfileData object to record and store the results of the tests.
	 * 
	 * @param server
	 *            the QuestionServer object that serves up the questions of the training set
	 * @return a ProfileData object containing the aggregate results of the tests
	 */
	public ProfileData profile(QuestionServer server) {
		Logger.getInstance().println("Profiler commencing profile sequence...", DetailLevel.MINIMAL);

		ProfileData pd = new ProfileData();

		int count = 0;
		while (server.hasNext()) {
			CFEExamQuestion question = server.next();

			Logger.getInstance().println(
					"Currently processing " + question.name + " (item " + ++count + " of " + server.size() + ")...",
					DetailLevel.MEDIUM);
			Logger.getInstance().println("Profile Index: " + question.getProfile().getProfileIndex(),
					DetailLevel.MEDIUM);

			int numAlgos = AlgorithmType.values().length;
			boolean[] results = new boolean[numAlgos];
			try {
				for (int i = 0; i < numAlgos; i++) {
					Logger.getInstance().printf("\tTesting " + AlgorithmType.values()[i] + "... ", DetailLevel.MEDIUM);

					int response = algos[i].solve(question, cfeManual);
					results[i] = (response == question.correctResponse) ? true : false;

					Logger.getInstance().println(results[i] ? "success!" : "fail.", DetailLevel.MEDIUM);
				}
				pd.insert(question, results);
			} catch (Exception e) {
				for (StackTraceElement ste : e.getStackTrace()) {
					Logger.getInstance().println(ste.getFileName() + "." + ste.getMethodName(), DetailLevel.MINIMAL);
				}
			}
		}

		Logger.getInstance().println("Profiler complete.", DetailLevel.MINIMAL);
		return pd;

	}

	/**
	 * This is the primary method for running the entire profiling process. This function runs the Profiler on the
	 * questions offered by the instance of the QuestionServer class (which are all of the questions contained in the
	 * exam questions directory). Then it outputs the contents of the ProfileData object to a file, "profile data.txt".
	 * 
	 * @param args
	 */
	public static void main(String[] args) {

		Logger logger = Logger.getInstance();
		logger.addDestination("logs" + File.separator + "profiler.log");
		logger.setDetailLevel(DetailLevel.MEDIUM);
		
		QuestionServer qs = new QuestionServer("exam questions - training set");
		// QuestionServer qs = new QuestionServer("exam questions - test set");
		// QuestionServer qs = new QuestionServer("exam questions - all");
		
		Profiler profiler = new Profiler(logger);
		ProfileData pd = profiler.profile(qs);
		pd.calculate();

		// print summarized profile data.
		System.out.println(pd.summarize());

		// save profile data to file.

		// first, save raw data to file.  retrieved by calling the toString() method on
		// profile data object.  This serves as the source for the agent to retrieve the 
		// profile data when taking an exam.
		try {
			Formatter output = new Formatter("profile data" + File.separator + "profile.data.txt");
			output.format("%s", pd);
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		// next, save the formatted data to a file.  this data is pre-formatted for analysis, with
		// true-false questions broken out from multiple choice questions, and summarized as it formerly
		// was in an excel spreadsheet (for versions prior to 2.0.1).
		try {
			Formatter output = new Formatter("profile data" + File.separator + "profile.data.summarized.txt");
			output.format("%s", pd.summarize());
			output.flush();
			output.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
}
