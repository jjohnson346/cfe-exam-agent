package financial.fraud.cfe.ir;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import cfe.manual.docs.collection.Document;

public class IREngine {

	private Map<Integer, Document> documents;

	private List<String> vocab;

	private Map<String, List<Integer>> invertedIndex;

	private CounterMap<String, Integer> tfidf; // word and document index

	private Counter<String> docFreq;

	private PorterStemmer stemmer;

	public IREngine(String docCollectionDirName) {
		documents = new HashMap<Integer, Document>();

		ArrayList<File> files = new ArrayList<File>();
		for (File f : new File(docCollectionDirName).listFiles()) {
			if (f.getName().endsWith(".txt") && !f.getName().startsWith("."))
				files.add(f);
		}

		for (File f : files) {
			Document doc = new Document(docCollectionDirName + "/" + f.getName());

			documents.put(doc.getId(), doc);

		}

		stemmer = new PorterStemmer();
	}

	public void index() {
		System.out.println("Indexing...");

		// initialize vocab.
		HashSet<String> uniqWords = new HashSet<String>();
		for (Document document : documents.values()) {
			for (String word : document.getTokens()) {
				uniqWords.add(word);
			}
		}
		vocab = new ArrayList<String>(uniqWords);

		// create the inverted index.
		invertedIndex = new HashMap<String, List<Integer>>();
		for (String word : vocab) {
			invertedIndex.put(word, new ArrayList<Integer>());
		}

		// populate the postings lists.
		for (Document d : documents.values()) {
			for (String word : d.getTokens()) {
				List<Integer> postings = invertedIndex.get(word);
				// if condition prevents duplicate values in the postings array list.
				if (postings.size() == 0 || postings.get(postings.size() - 1).intValue() != d.getId())
					invertedIndex.get(word).add(d.getId());
			}
		}

		// print out the inverted index
		// System.out.println("\nContents of inverted index:\n");
		// for(String term : invertedIndex.keySet()) {
		// System.out.print(term + ": ");
		// List<Integer> postings = invertedIndex.get(term);
		// for(Integer posting : postings)
		// System.out.print(posting + " ");
		// System.out.println();
		// }
		// System.out.print("\n\n");

		// collect the document frequencies by traversing the count of postings
		// for each term in the inverted index.
		// Store the document frequencies in a Counter object.
		docFreq = new Counter<String>();
		for (String word : invertedIndex.keySet())
			docFreq.setCount(word, invertedIndex.get(word).size());
	}

	public void computeTFIDF() {
		/**
		 * TODO: Compute and store TF-IDF values for words and documents. Recall that you can make use of the instance
		 * variables: * vocab * documents NOTE that you probably do *not* want to store a value for every word-document
		 * pair, but rather just for those pairs where a word actually occurs in the document.
		 */
		System.out.println("Computing TF-IDF...");

		tfidf = new CounterMap<String, Integer>();
		for (Document d : documents.values()) {
			ArrayList<String> documentTokens = d.getTokens();
			HashSet<String> docVocab = new HashSet<String>(documentTokens);
			ArrayList<String> docVocabList = new ArrayList<String>(docVocab);
			for (String word : docVocabList) {
				// tfidf.setCount(word, new Integer(d), getTFIDF(word, d));
				tfidf.setCount(word, d.getId(), getTFIDF(word, d.getId(), false));
			}
		}
	}

	/**
	 * returns the tf-idf weighting for the given word (string) and document index
	 * 
	 * @param word
	 *            the word for which to retrieve the tf-idf score
	 * @param docId
	 *            the doc for which to retrieve the tf-idf score
	 * @param printToConsole
	 *            boolean - true if printout of intermediate results desired
	 * @return tf-idf score for word/doc
	 */
	public double getTFIDF(String word, int docId, boolean printToConsole) {

		// insert some System.outs here....
		// if(printToConsole) {
		// System.out.println("executing getTFIDF() with the following parms:");
		// System.out.println("word: " + word);
		// System.out.println("doc: " + doc);
		// }

		double tfidf_score = 0.0;

		// first, check whether the word is in the document.
		// if it is not in the document, simply return 0.

		if (new HashSet<Integer>(invertedIndex.get(word)).contains(docId)) {
			int tf = 0;
			ArrayList<String> documentTokens = documents.get(docId).getTokens();
			// count up the number of occurences of the word in the document.
			for (String w : documentTokens) {
				if (w.equals(word))
					tf++;
			}

			// calculate tfw and idf, then multiply.
			double tfw = 1 + Math.log10(tf);
			double idf = Math.log10(documents.size() / docFreq.getCount(word));
			tfidf_score = tfw * idf;

			// insert some System.outs here.
			// if(printToConsole) {
			// System.out.println("tf: " + tf);
			// System.out.println("tfw: " + tfw);
			// System.out.println("documents.size(): " + documents.size());
			// System.out.println("df: " + df);
			// System.out.println("idf: " + idf);
			// System.out.println("wordDocs contents:");
			// for(int i = 0; i < wordDocs.size(); i++) {
			// System.out.print(wordDocs.get(i) + " ");
			// }
			// System.out.println();
			// System.out.println();
			// }

		} else {
			tfidf_score = 0;
		}

		return tfidf_score;
	}

	/**
	 * returns the tf-idf score for an unstemmed word in a particular document. Stems the word and then calls
	 * getTFIDF().
	 * 
	 * @param word
	 *            an unstemmed word
	 * @param doc
	 *            the document for which to return the tf-idf score for the word
	 * @return the tf-idf score for the unstemmed word/document
	 */
	public double getTFIDFUnstemmed(String word, int doc) {
		word = stemmer.stem(word);
		// return getTFIDF(word, doc);
		return getTFIDF(word, doc, true);
	}

	/**
	 * returns the postings list for a word.
	 * 
	 * @param word
	 * @return
	 */
	public ArrayList<Integer> getPostings(String word) {
		ArrayList<Integer> postings = new ArrayList<Integer>();
		postings.addAll(invertedIndex.get(word));
		return postings;
	}

	/**
	 * returns the postings list for an unstemmed word. First, stems the word, then calls getPostings(word).
	 */
	public ArrayList<Integer> getPostingsUnstemmed(String word) {
		word = stemmer.stem(word);
		return getPostings(word);
	}

	/**
	 * returns a list of all the doc ids in which all of the words of the input query appear; i.e., an AND query.
	 */
	public ArrayList<Integer> booleanRetrieve(ArrayList<String> query) {
		HashSet<Integer> results = new HashSet<Integer>(invertedIndex.get(query.get(0)));
		for (String word : query) {
			HashSet<Integer> wordPostingsList = new HashSet<Integer>(invertedIndex.get(word));
			results.retainAll(wordPostingsList);
		}
		ArrayList<Integer> docs = new ArrayList<Integer>(results);
		Collections.sort(docs);
		return docs;
	}

	/**
	 * returns a ranked retrieval of documents relevant to the query. Currently set to return the *top 10* relevant
	 * documents.
	 * 
	 * NOTE: may want to parameterize the size of the result set.
	 * 
	 * @param query
	 *            the query for which to retrieve a ranked retrieval of docs.
	 * 
	 * @return a priority queue containing the *top 10* docs
	 */
	public PriorityQueue<Integer> rankRetrieve(ArrayList<String> query) {

		/*************************************************************/
		/** TODO: Implement cosine similarity. */
		/* For now just implements Jaccard similarity. */
		// HashSet<String> wordsInQuery = new HashSet<String>();
		// wordsInQuery.addAll(query);
		// HashSet<String> wordsInDoc;
		// HashSet<String> setUnion;
		// HashSet<String> setIntersection;
		// for (int d = 0; d < documents.size(); d++) {
		// wordsInDoc = new HashSet<String>();
		// wordsInDoc.addAll(documents.get(d));
		// setUnion = new HashSet<String>(wordsInDoc);
		// setUnion.addAll(wordsInQuery);
		// setIntersection = new HashSet<String>(wordsInQuery);
		// setIntersection.retainAll(wordsInDoc);
		// scores[d] = ((double) setIntersection.size()) / ((double) setUnion.size());
		// }

		// insert code for cosine similarity here.

		// first, build the tfidf vector for the query.
		Counter<String> queryTFIDF = new Counter<String>();

		// count up the tf's for each word in the query.
		for (String word : query)
			queryTFIDF.incrementCount(word, 1);

		System.out.println("queryTFIDF: " + queryTFIDF.toString());

		// scale the tfs using wt = 1 + log10(tf).
		// note: NOT using the document frequency in the weights
		// for the words of the query.
		for (String word : queryTFIDF.keySet()) {
			double tf = queryTFIDF.getCount(word);
			queryTFIDF.setCount(word, 1 + Math.log10(tf));
		}

		System.out.println("scaled queryTFIDF: " + queryTFIDF.toString());

		// now, calculate cosine similarity for each document relative to the query.
		// then, add the document to a priority queue, using the cosine similarity score
		// for the priority.
		PriorityQueue<Integer> pq = new PriorityQueue<Integer>();

		for (Document d : documents.values()) {
			double score = 0.0;
			for (String word : queryTFIDF.keySet()) {
				System.out.println("tfidf for " + word + "," + d.getId() + ": " + tfidf.getCount(word, d.getId()));
				score += queryTFIDF.getCount(word) * tfidf.getCount(word, d.getId());
			}

			// calculate the length of the current document, and adjust
			// score by doc length, accordingly.
			// Note: NOT using the length of the query in this calculation.
			double docLength = 0.0;
			for (String word : tfidf.keySet())
				docLength += Math.pow(tfidf.getCount(word, d.getId()), 2);
			docLength = Math.pow(docLength, 0.5);
			score /= docLength;
			pq.add(new Integer(d.getId()), score);
		}

		PriorityQueue<Integer> topTen = new PriorityQueue<Integer>();
		for (int d = 0; d < 10; d++) {
			double priority = pq.getPriority();
			topTen.add(pq.next(), priority);
		}

		return topTen;
	}

	/**
	 * returns an array list containing the lower case, alphanumeric, stemmed words in a query string supplied as input.
	 * 
	 * @param queryString
	 *            the query string to process
	 */
	public ArrayList<String> processQuery(String queryString) {
		// lowercase
		queryString = queryString.toLowerCase();
		ArrayList<String> query = new ArrayList<String>();
		for (String s : queryString.split("\\s+")) {
			// remove non alphanumeric characters
			s = s.replaceAll("[^a-zA-Z0-9]", "");
			// stem s
			s = stemmer.stem(s);
			if (!s.equals(""))
				query.add(s);
		}
		return query;
	}

	public Document getDocument(int docId) {
		return documents.get(docId);
	}

	/**
	 * returns the list of matching documents found by booleanRetrieve() for the query provided as an input string
	 */
	public ArrayList<Integer> queryRetrieve(String queryString) {
		ArrayList<String> query = processQuery(queryString);
		return booleanRetrieve(query);
	}

	/**
	 * returns the list of the top matching documents found by rankRetrieve() for the query string provided as input.
	 */
	public PriorityQueue<Integer> queryRank(String queryString) {
		ArrayList<String> query = processQuery(queryString);

		System.out.println("query: " + query);
		return rankRetrieve(query);
	}

	public static void runTests(IREngine irSys) {
		ArrayList<String> questions = null;
		ArrayList<String> solutions = null;

		try {
			BufferedReader input = new BufferedReader(new FileReader(new File("data/queries.txt")));
			questions = new ArrayList<String>();
			String line;
			while ((line = input.readLine()) != null) {
				questions.add(line);
			}
		} catch (IOException e) {
			System.err.println("Error reading /queries.txt: " + e.getMessage());
		}
		try {
			BufferedReader input = new BufferedReader(new FileReader(new File("data/solutions_java.txt")));
			solutions = new ArrayList<String>();
			String line;
			while ((line = input.readLine()) != null) {
				solutions.add(line);
			}
		} catch (IOException e) {
			System.err.println("Error reading ../data/solutions_java.txt: " + e.getMessage());
		}

		double epsilon = 1E-4;
		int numTests = solutions.size();
		for (int part = 0; part < numTests; part++) {

			int numCorrect = 0;
			int numTotal = 0;

			String problem = questions.get(part);
			String soln = solutions.get(part);

			if (part == 0) { // Inverted Index test
				System.out.println("Inverted Index Test");

				String[] words = problem.split(", ");
				String[] golds = soln.split("; ");

				// inserted System.outs here...
				for (int i = 0; i < words.length; i++) {
					numTotal++;
					String word = words[i];

					// System.out.println("results for word: " + word);

					HashSet<Integer> guess = new HashSet<Integer>(irSys.getPostingsUnstemmed(word));

					// inserted dump of guess
					// System.out.println("guess:");
					// Iterator<Integer> iterGuess = guess.iterator();
					// while(iterGuess.hasNext()) {
					// System.out.print(iterGuess.next() + " ");
					// }
					// System.out.println();

					String[] goldList = golds[i].split(", ");
					HashSet<Integer> goldSet = new HashSet<Integer>();
					for (String s : goldList) {
						goldSet.add(new Integer(s));
					}

					// inserted dump of gold set
					// System.out.println("gold set:");
					// Iterator<Integer> iterGold = goldSet.iterator();
					// while(iterGold.hasNext()) {
					// System.out.print(iterGold.next() + ", ");
					// }
					// System.out.println();

					if (guess.equals(goldSet)) {
						numCorrect++;
					}
				}
			} else if (part == 1) { // Boolean retrieval test
				System.out.println("Boolean Retrieval Test");

				String[] queries = problem.split(", ");
				String[] golds = soln.split("; ");
				for (int i = 0; i < queries.length; i++) {
					numTotal++;
					String query = queries[i];
					HashSet<Integer> guess = new HashSet<Integer>(irSys.queryRetrieve(query));
					String[] goldList = golds[i].split(", ");
					HashSet<Integer> goldSet = new HashSet<Integer>();
					for (String s : goldList) {
						goldSet.add(new Integer(s));
					}
					if (guess.equals(goldSet)) {
						numCorrect++;
					}
				}

			} else if (part == 2) { // TF-IDF test
				System.out.println("TF-IDF Test");

				String[] queries = problem.split("; ");
				String[] golds = soln.split(", ");

				// insert some System.outs here...

				// System.out.printf("%10s%5s%10s%10\n", "word", "doc", "guess", "gold");
				// System.out.println("-----------------------------------");

				for (int i = 0; i < queries.length; i++) {
					numTotal++;

					String[] query = queries[i].split(", ");
					double guess = irSys.getTFIDFUnstemmed(query[0], new Integer(query[1]).intValue());

					double gold = new Double(golds[i]).doubleValue();

					// insert System.out for print out row...
					// System.out.printf("%10s%5s%10.4f%10.4f\n", query[0], query[1], guess, gold);
					// System.out.println(query[0] + " " + query[1] + " " + guess + " " + gold);
					// System.out.println("query[0]: " + query[0]);
					// System.out.println("query[1]: " + query[1]);
					// System.out.println("guess: " + guess);
					// System.out.println("gold: " + gold);

					if (guess >= gold - epsilon && guess <= gold + epsilon) {
						numCorrect++;
					}
				}
			} else if (part == 3) {
				System.out.println("Cosine Similarity Test");

				String[] queries = problem.split(", ");
				String[] golds = soln.split("; ");
				for (int i = 0; i < queries.length; i++) {
					numTotal++;

					PriorityQueue<Integer> guess = irSys.queryRank(queries[i]);
					double score = guess.getPriority();
					Integer docId = guess.next();

					String[] topGold = golds[i].split(", ");
					Integer topGoldId = new Integer(topGold[0]);
					double topScore = new Double(topGold[1]).doubleValue();

					// insert System.out for print out row...
					// System.out.println("query: " + queries[i]);
					// System.out.println("docId: " + docId);
					// System.out.println("score: " + score);
					// System.out.println("topGoldId: " + topGoldId);
					// System.out.println("topScore: " + topScore);
					// System.out.println();

					if (docId.intValue() == topGoldId.intValue() && score >= topScore - epsilon
							&& score <= topScore + epsilon) {
						numCorrect++;
					}
				}
			}

			String feedback = numCorrect + "/" + numTotal + " Correct. " + "Accuracy: " + (double) numCorrect
					/ numTotal;
			int points = 0;
			if (numCorrect == numTotal)
				points = 3;
			else if (numCorrect > 0.75 * numTotal)
				points = 2;
			else if (numCorrect > 0)
				points = 1;
			else
				points = 0;

			System.out.println("    Score: " + points + " Feedback: " + feedback);
		}

	}

	public static void main(String[] args) {

		// SectionIRSystem irSys = new
		// SectionIRSystem("document collection/CFEManualSmallDocUnit/Financial Transactions and Fraud Schemes/Bankruptcy Fraud");
		IREngine irSys = new IREngine("document collection/CFEManualSmallDocUnit/Law/Overview of the Legal System");
		irSys.index();
		irSys.computeTFIDF();

		// print out the doc ids
		System.out.println();

		System.out.println("document ids of those in collection:");
		for (Document d : irSys.documents.values())
			System.out.print(d.getId() + " ");
		System.out.println();

		System.out.println();

		System.out.println("postings list for the query term: " + "common");
		List<Integer> postings1 = irSys.getPostingsUnstemmed("common");
		for (int posting : postings1)
			System.out.print(posting + " ");
		System.out.println();

		System.out.println("postings list for the query term: " + "precedents");
		List<Integer> postings2 = irSys.getPostingsUnstemmed("precedents");
		for (int posting : postings2)
			System.out.print(posting + " ");
		System.out.println();

		// if (args.length == 0) {
		// runTests(irSys);
		// } else {
		// String query = "";
		// boolean haveAdded = false;
		// for (String s : args) {
		// if (haveAdded) {
		// query += " ";
		// }
		// query += s;
		// haveAdded = true;
		// }
		// String query = "cases stretching back hundreds of years in United States and English courts";
		String query = "common, precedents";
		PriorityQueue<Integer> results = irSys.queryRank(query);
		System.out.println("Best matching documents to '" + query + "':");
		int numResults = results.size();
		for (int i = 0; i < numResults; i++) {
			double score = results.getPriority();
			String title = irSys.getDocument(results.next().intValue()).getTitle();
			System.out.println(title + ": " + score);
		}
		
		
		
		irSys = new IREngine("document collection/CFEManualSmallDocUnit/Financial Transactions and Fraud Schemes/Financial Institution Fraud");
		irSys.index();
		irSys.computeTFIDF();
		
		System.out.println("document ids of those in collection:");
		for (Document d : irSys.documents.values())
			System.out.print(d.getId() + " ");
		System.out.println();

		System.out.println();

		System.out.println("postings list for the query term: " + "construction");
		postings1 = irSys.getPostingsUnstemmed("construction");
		for (int posting : postings1)
			System.out.print(posting + " ");
		System.out.println();

		System.out.println("postings list for the query term: " + "loan");
		postings2 = irSys.getPostingsUnstemmed("loan");
		for (int posting : postings2)
			System.out.print(posting + " ");
		System.out.println();
		
		System.out.println("postings list for the query term: " + "fraud");
		List<Integer> postings3 = irSys.getPostingsUnstemmed("fraud");
		for (int posting : postings3)
			System.out.print(posting + " ");
		System.out.println();
		
		query = "construction, loan, fraud";
		results = irSys.queryRank(query);
		System.out.println("Best matching documents to '" + query + "':");
		numResults = results.size();
		for (int i = 0; i < numResults; i++) {
			double score = results.getPriority();
			String title = irSys.getDocument(results.next().intValue()).getTitle();
			System.out.println(title + ": " + score);
		}
		// }
	}
}
