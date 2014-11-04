package financial.fraud.cfe.manual;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import financial.fraud.cfe.util.TokenizerSimple;

/**
 * an instance of CFEManualSection represents a section of the cfe manual.  The manual consists
 * of a tree structure of these objects, reflecting the structure of the cfe manual.
 * 
 * @author jjohnson346
 *
 */
public class CFEManualSection implements Iterable<CFEManualSection> {

	/**
	 * the name assigned to the manual section.  Note the name may not be unique.  
	 * (Lexis Nexis may be the name on multiple nodes.)
	 */
	public final String name;

	/**
	 * the unique id assigned to the manual section
	 */
	public final String id;

	/**
	 * the page number on which the section begins
	 */
	public final String pageNumber;

	/**
	 * the character position in the manual section at which the section begins
	 */
	public int begPosition;
	
	/**
	 * the character position in the manual text at which the section ends
	 */
	public int endPosition;
	
	/**
	 * the question section to which this manual section applies.  A number of 
	 * cfe manual sections will apply to a given question section.
	 */
	public final String questionSection;
	
	/**
	 * the exam section: one of the following:
	 * 1.  Financial Transactions and Fraud Schemes
	 * 2.  Law
	 * 3.  Investigation
	 * 4.  Fraud Prevention and Deterrence
	 * 
	 */
	public final String examSection;

	/**
	 * the hash map of child sections that is used to organize the sections into a tree structure,
	 * reflecting the structure of the sections in the cfe manual, itself
	 */
	public LinkedHashMap<String, CFEManualSection> subSections;

	/**
	 * the parent section to this section
	 */
	public CFEManualSection parent;

	/**
	 * a reference to the string containing the manual's text. Note, every section has a reference to the text, but
	 * there should only be one copy of the manual's text in memory, based on the logic for loading the manual.
	 * 
	 * Correction to above note: Strings are not handled in the way implied by the statement above.  Evidence
	 * suggests that when a reference to a substring of another string is created, a separate, new string is created
	 * occupying a separate location in memory, (see TestString class).  So, in fact, each manual section has its 
	 * own copy of its respective
	 * portion of the manual in memory.  Thus, considerably more memory is used for this data structure than originally 
	 * thought.  To be a bit more specific about the amount of memory used, consider that nodes of each level in the manual
	 * section hierarchy save a complete version of the manual, in aggregate.  That is, if the manual section hierarchy 
	 * is 5 layers deep, then the amount of space required for cfeManual data structure is the amount of space required
	 * to store the manual 5 times.  As more sections are created to break down the manual to a more granular level, more 
	 * space is being used.
	 */
	private String manualText;

	/**
	 * stores the text for the manual section.
	 */
	private String sectionText;

	/**
	 * a reference to a tokenizer that returns a hash map with the token counts for the section.
	 */
	private TokenizerSimple tokenizerSimple;

	/**
	 * the level within the toc of the section
	 */
	private int depth;

	
	/**
	 * constructor takes all parameters necessary to fully initialize an instance of CFEManualSection.
	 * 
	 * CFEManualSection objects are created in {@link CFEManualLargeDocUnit}.  Specifically, {@link CFEManualLargeDocUnit#CFEManual()}
	 * constructor, which internally, builds a tree of CFEManualSection objects based on the structure of the
	 * manual.  The id assigned to each CFEManaulSection is the fully qualified path as given by the structure
	 * of the index, from root node of index to the subsection represented by the CFEManualSection object.
	 * The name is just the name of the section as given by the table of contents, (without the fully-qualified 
	 * path).
	 * 
	 * @param id 			the id for the manual section - must be unique.  
	 * @param name			the name of the section 
	 * @param pageNumber		the page number of the manual on which this section begins
	 * @param manualText		a string containing the entire text of the manual
	 * @param parent			the parent section object to this section object
	 */
	public CFEManualSection(String id, String name, String pageNumber, String manualText, CFEManualSection parent, String examSection, String questionSection) {
		this.id = id;
		this.name = name;
		this.pageNumber = pageNumber;
		this.manualText = manualText;
		this.examSection = examSection;
		this.questionSection = questionSection;
		
		// set the parent section.  Notice that there are references in both
		// directions - in parent section, this node is added to hash map of children;
		// in child section, there is a reference to parent.  
		// The only exception is the root node, which has no parent.
		if (parent != null) {
			this.parent = parent;
			parent.addSubSection(this);
			this.depth = parent.depth + 1;
		} else { // parent is null, so in this case, we have a root node.
			this.depth = 0;
		}
		subSections = new LinkedHashMap<String, CFEManualSection>();
		
	}

	private void addSubSection(CFEManualSection s) {
		// only add a subsection in the event there isn't already one by the same key
		// in the collection. There are a few rare instances in which the subsections
		// by the same name are added to the same collection. Example: LexisNexis.
		// In such an instance, do NOT repeat the insert operation...
		if (!subSections.containsKey(s.name)) {
			subSections.put(s.name, s);
			s.parent = this;
		} else
			System.out.println("Attempting to add " + s.name
					+ " after it's already been added.  Aborting second addition.");
	}

	/**
	 * mutator for setting the text attribute of this section object. 
	 */
	public void setText() {
		try {
			
			// set the section text to a substring of the manualText string,
			// based on beginning position and ending position values.
			// Note that when Java assigns a reference to the substring of another
			// string object, it does so by creating an entirely new String object
			// containing the characters of the substring (and does not point the
			// new reference to the original string object).  So, every time
			// we call substring to create sub-section text, we are, in fact,
			// creating new copies of manual text....
			
			sectionText = manualText.substring(begPosition, endPosition);
			
			
			// tokenize the text using the TokenizerSimple() class.
			// TODO: TokenizerSimple() needs to be enhanced (perhaps
			// replaced with a new class implementing the same 
			// interface) that performs more sophisticated tokenization,
			// such as one making use of the PorterStemmer class for doing
			// more sophisticated stemming operations.
			
			tokenizerSimple = new TokenizerSimple();
			tokenizerSimple.tokenize(sectionText);
			
		} catch (StringIndexOutOfBoundsException e) {
			System.out.printf("%s%s%s%d%s%d\n", "ERROR:  ", name, " : ", begPosition, " : ", endPosition);
		}
	}

	// public Map<String, Integer> getTokenTypeFreqs() {
	// return tokenizerSimple.getTokenTypeFreqs();
	// }

	// public String getTokenTypeFreqsData() {
	// return tokenizerSimple.toString();
	// }

	/**
	 * accessor to the text of the section.
	 * 
	 * @return a String containing the text for the section
	 */
	public String getText() {
		return sectionText;
	}

	/**
	 * returns a list of the ancestors for the section. This includes the chain of parents all of the way back to the
	 * root node. This function is recursive.
	 * 
	 * @return list of ancestors back to the root node
	 */
	public List<CFEManualSection> getAncestors() {
		List<CFEManualSection> ancestors = new LinkedList<CFEManualSection>();

		if (parent != null)
			ancestors.addAll(parent.getAncestors());
		ancestors.add(this);
		return ancestors;
	}
	
	/**
	 * returns the subtree elements in the form of a list.  This list includes a reference to the 
	 * section object for which this method was called.
	 * 
	 * @return list containing a reference to current object as well as all descendant subsection objects
	 */
	public List<CFEManualSection> getSubTreeAsList() {
		List<CFEManualSection> descendants = new LinkedList<CFEManualSection>();
		descendants.add(this);
		for(CFEManualSection subSection : subSections.values()) {
			descendants.addAll(subSection.getSubTreeAsList());
		}
		return descendants;
	}

	/**
	 * returns a map of the siblings for the section. Keys are ids for the sections and the values are the manual
	 * section objects themselves.  This map includes the section itself among the siblings.
	 * 
	 * @return maps of the siblings for the section
	 */
	public Map<String, CFEManualSection> getSiblings() {
		// parent == null is true only in the case of the root node..
		if (parent == null)
			return null;
		LinkedHashMap<String, CFEManualSection> siblings = new LinkedHashMap<String, CFEManualSection>();
		
		// QUESTION:  currently, siblings are retrieved by going to parent section and 
		// sequentially adding its children to a hash map.  Why not simply go to parent
		// and return the parent's hash map of subsections?  Isn't that the same thing?
		
		for (String s : parent.subSections.keySet()) {
			siblings.put(s, parent.subSections.get(s));
		}
		return siblings;
	}

	/**
	 * returns a formatted string that provides a representation of the section within
	 * the context of producing a table of contents of sections, including the appropriate
	 * level of indentation (based on the sections depth in the hierarchy of sections).
	 * 
	 * this is a recursive function which makes call to the sub-section objects to this 
	 * section.
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		
		// insert indent to the appropriate depth.
		for (int i = 0; i < depth; i++) {
			sb.append("\t");
		}
		
		// now, insert the the indicative data for the section - name, page, beginning position, ending position.
		sb.append(String.format("%s%s%s%s%d%s%d", name, " : ", pageNumber, " : ", begPosition, " : ", endPosition));
		
		// sb.append(String.format("%s%s%s%s%s%s%d%s%d", name, " : ", id, " : ", pageNumber, " : ", begPosition, " : ",
		// endPosition));

		// make recursive call to subsection objects' toString() methods.
		for (CFEManualSection s : subSections.values()) {
			sb.append("\n" + s.toString());
		}
		
		return new String(sb);
	}

	/**
	 * returns the tokenizer object that is responsible for producing the hash map of the words and their counts for the
	 * section. Every section has its own tokenizer object in order to optimize performance of counting algorithms.
	 * 
	 * @return tokenizer object for the section responsible for producing the hash map of words and their counts
	 */
	public TokenizerSimple getTokenizer() {
		return tokenizerSimple;
	}

	/**
	 * returns true if the section does not have any child sections.
	 * 
	 * @return true if the section has no children, otherwise false
	 */
	public boolean isLeaf() {
		return subSections.isEmpty();
	}

	/**
	 * returns an iterator for traversing the cfe manual subsections
	 * for this section.
	 */
	@Override
	public Iterator<CFEManualSection> iterator() {
		return subSections.values().iterator();
	}
}
