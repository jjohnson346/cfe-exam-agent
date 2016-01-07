package financial.fraud.cfe.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import financial.fraud.cfe.agent.CFEExamQuestion;
import financial.fraud.cfe.algorithm.AlgorithmType;

/**
 * ProfileData acts as a data store facility for the results of the test
 * performed by the Profile object. This data includes the number of trials and
 * number of successes among those trials (i.e., the number of times the correct
 * answer was selected) broken down by algorithm type and question profile
 * (i.e., by combination of features).
 * 
 * @author jjohnson346
 *
 */
public class ProfileData {

	private int[][] successCounts; // stores the number of successes for each
									// question profile,
									// for each algo.

	public final int[] trialCounts; // stores the number of trials for each
									// question profile.

	public final double[][] successProbs; // the prob of success for each algo,
											// for each question profile
											// IMPORTANT: note that it is PUBLIC

	private final int NUM_ALGOS = AlgorithmType.values().length; // the number
																	// of algos
																	// to store
																	// data for

	private final int NUM_FEATURES = FeatureType.values().length; // the number
																	// of
																	// features
																	// identified

	private final int NUM_FEATURE_COMBOS = (int) Math.pow(2.0, NUM_FEATURES); // the
																				// number
																				// of
																				// combos
																				// of
																				// features,
																				// assume
																				// true/false
																				// value
																				// for
																				// each.

	/**
	 * constructor initializes the successCounts, trialCounts and successProbs
	 * arrays according to the number of algos, number of features, and the
	 * consequent number of feature combinations.
	 */
	public ProfileData() {
		successCounts = new int[NUM_FEATURE_COMBOS][NUM_ALGOS];
		trialCounts = new int[NUM_FEATURE_COMBOS];
		successProbs = new double[NUM_FEATURE_COMBOS][NUM_ALGOS];
	}

	/**
	 * inserts the results data for a question into the data store. That is,
	 * this function inserts for each algorithm, the results of applying that
	 * algorithm on the question, as given by the array, results, passed in as
	 * an input argument. This data is inserted the location of the data store
	 * corresponding to the profile for the given question.
	 * 
	 * @param question
	 *            the question for which to insert the data, the profile is the
	 *            pertinent info, here.
	 * @param results
	 *            an array of booleans giving whether the corresponding algo was
	 *            successful on the question.
	 */
	public void insert(CFEExamQuestion question, boolean[] results) {
		// get the profile for the question passed in as an input parm.
		int profileIndex = question.getProfile().getProfileIndex();

		// increment the number of trials for the index corresponding to the
		// question's profile.
		trialCounts[profileIndex]++;

		// increment the success count in the successCounts array for those
		// algos that were successful.
		for (int j = 0; j < successCounts[profileIndex].length; j++) {
			if (results[j])
				successCounts[profileIndex][j]++;
		}
	}

	/**
	 * calculates the probability of success for a each algorithm on each
	 * question profile and stores the results in a public array, successProbs,
	 * which is used by the CFEExamAgent to determine on each question it
	 * confronts which algo to use. That is, it picks the algo with highest
	 * probability of success given the profile of the current question.
	 * 
	 */
	public void calculate() {
		for (int i = 0; i < NUM_FEATURE_COMBOS; i++) {
			for (int j = 0; j < NUM_ALGOS; j++) {
				if (trialCounts[i] != 0)
					successProbs[i][j] = (double) successCounts[i][j]
							/ trialCounts[i];
				else
					successProbs[i][j] = 0.0;
			}
		}
	}

	/**
	 * loads profile data from file, profile data.txt, and stores the contents
	 * in the public array, successProbs. successProbs is the critical array
	 * used by the CFEExamAgent for selecting the algo to use on each question
	 * it confronts.
	 */
	public void load() throws FileNotFoundException {
		// change backslashes to forward slashes for mac version.
		// Scanner scanner = new Scanner(new
		// File("profile data\\profile data.txt"));
		Scanner scanner = new Scanner(
				new File("profile data//profile data.txt"));

		// skip first 2 header lines.
		scanner.nextLine();
		scanner.nextLine();

		for (int i = 0; i < NUM_FEATURE_COMBOS; i++) {

			// skip first column showing the index.
			scanner.nextInt();

			trialCounts[i] = scanner.nextInt();

			for (int j = 0; j < NUM_ALGOS; j++) {
				successProbs[i][j] = scanner.nextDouble();
			}
		}
		scanner.close();
	}

	/**
	 * returns a pretty formatted version of the contents of number of trials
	 * and successProb for each question profile.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(toStringTrueFalse());
		sb.append("\n\n");
		sb.append(toStringMultipleChoice());
		return new String(sb);
//		StringBuilder sb = new StringBuilder();
//
//		final int COLUMN_WIDTH = 14;
//		String formatString = "%" + COLUMN_WIDTH + "s";
//
//		// make header.
//		sb.append(String.format(formatString, "Index"));
//		sb.append(String.format(formatString, "Trials"));
//
//		for (AlgorithmType t : AlgorithmType.values()) {
//			sb.append(String.format(formatString, t));
//		}
//		sb.append(String.format(formatString, "Agent"));
//		sb.append(String.format(formatString, "Description"));
//		sb.append("\n");
//		for (int i = 0; i < AlgorithmType.values().length + 4; i++)
//			sb.append(String.format(formatString, "-------------"));
//		sb.append("\n");
//
//		// display values.
//		for (int i = 0; i < NUM_FEATURE_COMBOS; i++) {
//			// 2016/01/05 - version 2.0.0 - added condition for 
//			// trial counts > 0 in order to remove the "zero" rows,
//			// i.e., rows showing profile indices where there are no 
//			// trials.
//			if (trialCounts[i] > 0) {
//				sb.append(String.format(formatString, i));
//				sb.append(String.format(formatString, trialCounts[i]));
//
//				for (int j = 0; j < AlgorithmType.values().length; j++) {
//					sb.append(String.format("%" + COLUMN_WIDTH + ".3f", successProbs[i][j]));
//				}
//				sb.append(String.format("%" + COLUMN_WIDTH + ".3f", maxSuccessProb(successProbs[i])));
//				sb.append(Profile.getDescription(i));
//				sb.append("\n");
//			}
//		}
//
//		// display total trial count
//		int totalCount = 0;
//		for (int i = 0; i < trialCounts.length; i++) {
//			totalCount += trialCounts[i];
//		}
//		sb.append(String.format("%" + COLUMN_WIDTH + "s%" + COLUMN_WIDTH
//				+ "d\n", "Total Count:", totalCount));
//
//		return new String(sb);
	}
	
	
	private String toStringMultipleChoice() {
		StringBuilder sb = new StringBuilder();

		final int COLUMN_WIDTH = 14;
		String formatString = "%" + COLUMN_WIDTH + "s";

		// make header.
		sb.append("Multiple Choice:\n----------------\n");
		sb.append(String.format(formatString, "Index"));
		sb.append(String.format(formatString, "Trials"));

		for (AlgorithmType t : AlgorithmType.values()) {
			sb.append(String.format(formatString, t));
		}
		sb.append(String.format(formatString, "Agent"));
		sb.append(String.format(formatString, "Description"));
		sb.append("\n");
		for (int i = 0; i < AlgorithmType.values().length + 4; i++)
			sb.append(String.format(formatString, "-------------"));
		sb.append("\n");

		int totalCount = 0;		// for displaying the total count at the bottom.
		double weightedAgentAccuracy = 0;  // for calculating weighted accuracy rate across all question profiles.

		// display values.
		for (int i = 0; i < NUM_FEATURE_COMBOS; i++) {
			// 2016/01/05 - version 2.0.0 - added condition for 
			// trial counts > 0 in order to remove the "zero" rows,
			// i.e., rows showing profile indices where there are no 
			// trials.
			// Also, added the !hasFeature(truefalse) condition.
			if (trialCounts[i] > 0 && !Profile.hasFeature(i,FeatureType.TRUE_FALSE.ordinal())) {
				sb.append(String.format(formatString, i));
				sb.append(String.format(formatString, trialCounts[i]));

				for (int j = 0; j < AlgorithmType.values().length; j++) {
					sb.append(String.format("%" + COLUMN_WIDTH + ".3f", successProbs[i][j]));
				}
				double agentAccuracy = maxSuccessProb(successProbs[i]);
				sb.append(String.format("%" + COLUMN_WIDTH + ".3f", maxSuccessProb(successProbs[i])));
				sb.append(Profile.getDescription(i));
				sb.append("\n");
				totalCount += trialCounts[i];
				weightedAgentAccuracy += trialCounts[i] * agentAccuracy;
			}
		}
		weightedAgentAccuracy /= totalCount;

		// display total trial count
		sb.append(String.format("%" + COLUMN_WIDTH + "s%" + COLUMN_WIDTH
				+ "d%" + COLUMN_WIDTH * 9 + "s%" + COLUMN_WIDTH + ".3f\n", "Total Count:", totalCount, " ", weightedAgentAccuracy));

		return new String(sb);
		
	}
	
	private String toStringTrueFalse() {
		StringBuilder sb = new StringBuilder();

		final int COLUMN_WIDTH = 14;
		String formatString = "%" + COLUMN_WIDTH + "s";

		// make header.
		sb.append("True-False: \n-----------\n");
		sb.append(String.format(formatString, "Index"));
		sb.append(String.format(formatString, "Trials"));

		for (AlgorithmType t : AlgorithmType.values()) {
			sb.append(String.format(formatString, t));
		}
		sb.append(String.format(formatString, "Agent"));
		sb.append(String.format(formatString, "Description"));
		sb.append("\n");
		for (int i = 0; i < AlgorithmType.values().length + 4; i++)
			sb.append(String.format(formatString, "-------------"));
		sb.append("\n");

		int totalCount = 0;		// for displaying the total count at the bottom.
		double weightedAgentAccuracy = 0;  // for calculating weighted accuracy rate across all question profiles.

		// display values.
		for (int i = 0; i < NUM_FEATURE_COMBOS; i++) {
			// 2016/01/05 - version 2.0.0 - added condition for 
			// trial counts > 0 in order to remove the "zero" rows,
			// i.e., rows showing profile indices where there are no 
			// trials.
			// Also, added the hasFeature(truefalse) condition.
			if (trialCounts[i] > 0 && Profile.hasFeature(i,FeatureType.TRUE_FALSE.ordinal())) {
				sb.append(String.format(formatString, i));
				sb.append(String.format(formatString, trialCounts[i]));

				for (int j = 0; j < AlgorithmType.values().length; j++) {
					sb.append(String.format("%" + COLUMN_WIDTH + ".3f", successProbs[i][j]));
				}
				double agentAccuracy = maxSuccessProb(successProbs[i]);
				sb.append(String.format("%" + COLUMN_WIDTH + ".3f", agentAccuracy));
				sb.append(Profile.getDescription(i));
				sb.append("\n");
				totalCount += trialCounts[i];
				weightedAgentAccuracy += trialCounts[i] * agentAccuracy;
			}
		}
		weightedAgentAccuracy /= totalCount;

		// display total trial count
//		sb.append(String.format("%" + COLUMN_WIDTH + "s%" + COLUMN_WIDTH
//				+ "d\n", "Total Count:", totalCount));
		sb.append(String.format("%" + COLUMN_WIDTH + "s%" + COLUMN_WIDTH
				+ "d%" + COLUMN_WIDTH * 9 + "s%" + COLUMN_WIDTH + ".3f\n", "Total Count:", totalCount, " ", weightedAgentAccuracy));

		return new String(sb);
		
	}
	
	private double maxSuccessProb(double[] successProbs) {
		double max = successProbs[0];
		for(int i = 1; i < successProbs.length; i++) {
			if(successProbs[i] > max)
				max = successProbs[i];
		}
		return max;
	}
	
	
}
