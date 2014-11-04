package financial.fraud.cfe.grammar;

public class GrammarRule {

	public final String LHS;
	public final String RHS;
	public final String RHS_LEFT;
	public final String RHS_RIGHT;
	public final double PROB;
	public final boolean IS_LEXICAL;
	
	public GrammarRule(String lhs, String rhs, double prob) {
		LHS = lhs;
		RHS = rhs;
		
		int spacePos = RHS.indexOf(' ');
		if(spacePos == -1) {
			// production is a lexical rule.
			RHS_LEFT = RHS;
			RHS_RIGHT = null;
			IS_LEXICAL = true;
		} else {
			RHS_LEFT = RHS.substring(0, RHS.indexOf(' '));
			RHS_RIGHT = RHS.substring(RHS.indexOf(' ')+1);
			IS_LEXICAL = false;
		}
			
		PROB = prob;
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(String.format("%s%s%s%s%s%s%s", "Production:  ", LHS, " --> ", RHS, "  :  ", PROB, "\n"));
		sb.append(String.format("%s%s%s", "LHS:  ", LHS, "\n"));
		sb.append(String.format("%s%s%s", "RHS:  ", RHS, "\n"));
		sb.append(String.format("%s%s%s", "RHS_LEFT:  ", RHS_LEFT, "\n"));
		sb.append(String.format("%s%s%s", "LHS_LEFT:  ", RHS_RIGHT, "\n"));
		sb.append(String.format("%s%s%s", "IS_LEXICAL:  ", IS_LEXICAL, "\n"));
		sb.append(String.format("%s%s%s", "PROB:  ", PROB, "\n"));
		return sb.toString();
	}
	
	public static void main(String[] args) {
		GrammarRule p = new GrammarRule("S", "NP VP", 1.0);
		System.out.println(p);
		p = new GrammarRule("NP", "stars", 0.18);
		System.out.println(p);
	}
	
}
