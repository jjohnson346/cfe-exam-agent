package financial.fraud.cfe.grammar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Grammar {

	public final List<GrammarRule> productions;
	private Map<String, HashMap<String, GrammarRule>> grammarRules;  // data structure for storing grammar rules 
																	// consisting of hash table of has tables for efficient retrieval
																	// of rules.  First dimension key = RHS_LEFT.  Second dimension key = 
																	// RHS_RIGHT.  Production element has RHS consisting of 
																	// RHS_LEFT RHS_RIGHT.
																	
	private Map<String, ArrayList<GrammarRule>> lexicalRules;		// data structure for storing lexical rules consisting
																	// of hash table of lists.  Hash table key = RHS_LEFT.  List 
																	// consists of all productions with the key value for RHS.
	
	
	public Grammar(List<GrammarRule> productions) {
		this.productions = productions;
		
		// build the lookup data structures, grammarRules and lexicalRules,
		// from the productions
		
		grammarRules = new HashMap<String, HashMap<String, GrammarRule>>();
		lexicalRules = new HashMap<String, ArrayList<GrammarRule>>();
		
		for(GrammarRule p : productions) {
			if(p.IS_LEXICAL) {
				// production represents lexical rule
				
				// if no list exists for the rhs of this production, create one.
				if(lexicalRules.get(p.RHS_LEFT) == null) {
					lexicalRules.put(p.RHS_LEFT, new ArrayList<GrammarRule>());
				}
				// add the production to the list for this rhs element of the hash table.
				lexicalRules.get(p.RHS_LEFT).add(p);
			} else {
				// production represents grammar rule

				// if no hash table exists for the rhs_left of this grammar rule, create one.
				if(grammarRules.get(p.RHS_LEFT) == null) {
					grammarRules.put(p.RHS_LEFT, new HashMap<String, GrammarRule>());
				}
				// add the production to the grammar rules data structure.
				grammarRules.get(p.RHS_LEFT).put(p.RHS_RIGHT, p);
			}
		}
	}
	
	public GrammarRule lookupGrammarRule(String rhsLeft, String rhsRight) {
		// if rule doesn't exist for rhsLeft, rhsRight, will return null.
		if(grammarRules.containsKey(rhsLeft) && grammarRules.get(rhsLeft).containsKey(rhsRight)) {
			return grammarRules.get(rhsLeft).get(rhsRight);
		} else {
			return null;
		}
	}
	
	public List<GrammarRule> lookupLexicalRules(String rhs) {
		// if no rules exist for terminal rhs, will return null.
		if(lexicalRules.containsKey(rhs))
			return lexicalRules.get(rhs);
		else
			return null;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(GrammarRule p : productions) {
			sb.append(p);
			sb.append("\n");
		}
		return sb.toString();
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<GrammarRule> productions = new ArrayList<GrammarRule>();
		productions.add(new GrammarRule("S", "NP VP", 1.0));
		productions.add(new GrammarRule("VP", "V NP", 0.7));
		productions.add(new GrammarRule("VP", "VP PP", 0.3));
		productions.add(new GrammarRule("PP", "P NP", 1.0));
		productions.add(new GrammarRule("P", "with", 1.0));
		productions.add(new GrammarRule("V", "saw", 1.0));
		productions.add(new GrammarRule("NP", "NP PP", 0.4));
		productions.add(new GrammarRule("NP", "astronomers", 0.1));
		productions.add(new GrammarRule("NP", "ears", 0.18));
		productions.add(new GrammarRule("NP", "saw", 0.04));
		productions.add(new GrammarRule("NP", "stars", 0.18));
		productions.add(new GrammarRule("NP", "telescopes", 0.1));
		Grammar g = new Grammar(productions);
		System.out.println(g);
		
		// Test lookup data structures for productions:
		System.out.println("Testing data structure for organizing productions...\n\n");
		
		List<GrammarRule> lr = g.lookupLexicalRules("saw");
		if(lr == null) {
			System.out.println("No rules found for terminal, saw.");
		} else {
			System.out.println("Rules for terminal, saw:");
			for(GrammarRule p : lr) {
				System.out.println(p);
			}
		}
		
		lr = g.lookupLexicalRules("testing123");
		if(lr == null) {
			System.out.println("No rules found for terminal, testing123.");
		} else {
			System.out.println("Rules for terminal, testing123:");
			for(GrammarRule p : lr) {
				System.out.println(p);
			}
		}

		GrammarRule p = g.lookupGrammarRule("VP", "PP");
		if(p == null) {
			System.out.println("No rule found for rhs.left = VP, rhs.right = PP.");
		} else {
			System.out.println("Rule for rhs.left = VP, rhs.right = PP:");
			System.out.println(p);
		}

		p = g.lookupGrammarRule("PP", "PP");
		if(p == null) {
			System.out.println("No rule found for rhs.left = PP, rhs.right = PP.");
		} else {
			System.out.println("Rule for rhs.left = PP, rhs.right = PP:");
			System.out.println(p);
		}
	}
}
