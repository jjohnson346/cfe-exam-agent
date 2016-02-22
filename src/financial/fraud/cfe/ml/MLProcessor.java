package financial.fraud.cfe.ml;

/**
 * features to consider for machine learning for passages:
 * 
 * 1.  Combine consecutive passages so that the combined passage contains phrases from both the question stem
 * and from at least one of the options.  Build the minimum size passage such that the stem and option are covered.
 * Pick the option covered by this passage.  Example:  Securities Fraud 17, Securities Fraud 28, 
 * White-Collar Crime 13
 * 
 * 2.  Look at subsection titles that relate to the question stem.  Then look at passages for documents that are
 * subsections to that section.  Example: Tax Fraud 19.
 * 
 * 3.  Run an algorithm where for each option, we build a query based on a combination of the option *and* the 
 * question stem.  Example: Tax Fraud 9.
 * 
 * 4.  Find passages that are parts of subsections of sections that match well with the quest stem.  Example:
 * Tax Fraud 9.
 * 
 *  Note:  removed the following questions from the training set for definition questions:
 *  (The following occupational fraud questions relate to tables/graphics that are not included in the 
 *  text form of the manual.)
 *  1.  Occupational Fraud 12 (#67 in the result set for the MLInputFileCreator).
 *  2.  Occupational Fraud 17 (#68)
 *  3.  Occupational Fraud 4 (#70)
 *  4.  Occupational Fraud 5 (#71)
 *  5.  Covert Examinations 6 - this is an applied question that can't be looked up. (#79)
 *  6.  Data Analysis 4 (#85) - could not find the right document
 *  
 * 5.  Note: check test questions in Covert Examinations section.  These appear to be better placed in the 
 * Sources of Information category.  Done 02/22/2016. 
 * 
 * 6.  Model of Fraud Examples:
 * Civil Justice System 4 : 
 * Able sues Baker and Charlie for breach of contract. Baker files a claim against Charlie alleging 
 * that Charlie failed to pay Baker money he was owed under the same contract. Baker's claim against 
 * Charles is called a: | Cross-claim | Third-party practice | Counterclaim | Joinder of parties
 * 
 * Another example:  Law Related to Fraud 26
 * 
 * Another: Law Related to Fraud 39
 * 
 * 7.  difficult problem (refer to other resource for amendments to constitution?) Criminal Prosecutions for Fraud 75
 * 
 * 8.  Algorithm:  create a document collection in which the document unit is a paragraph, or passage, and then
 * use lucene on this collection.
 * 
 * 9.  Example of question in which none of options appear in correct passage, and in which passage must be linked
 * to another document of which it is a child:  Consumer Fraud 29 (this is in test set, not training set).
 * 
 * 
 * 
 * @author joejohnson
 *
 */
public class MLProcessor {

}
