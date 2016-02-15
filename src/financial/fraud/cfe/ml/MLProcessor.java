package financial.fraud.cfe.ml;

/**
 * features to consider for machine learning for passages:
 * 
 * 1.  Combine consecutive passages so that the combined passage contains phrases from both the question stem
 * and from at least one of the options.  Build the minimum size passage such that the stem and option are covered.
 * Pick the option covered by this passage.  Example:  Securities Fraud 17, Securities Fraud 28.
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
 *  
 * 
 * @author joejohnson
 *
 */
public class MLProcessor {

}
