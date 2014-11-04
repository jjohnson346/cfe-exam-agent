package financial.fraud.cfe.manual;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * a compiler component class that implements an algorithm for removing blanks
 * by doing a bfs that returns the first token sequence encountered that is 
 * successfully verified by an instance of TokenVerifier.
 * @author jjohnson346
 *
 */
public class TextCompilerBFS extends TextCompiler {

	/**
	 * performs bfs for a token sequence constructed from the input
	 * token sequence that passes the token verifier, i.e., that is considered
	 * a valid sequence of tokens.
	 */
	@Override
	protected List<Token> scrub(List<Token> tokens) {
		Queue<TokenSequence> queue = new LinkedList<TokenSequence>();
		TokenSequence currSequence = null;
		List<Token> currTokens = null;
		List<Token> newTokens = null;

		queue.add(new TokenSequence(tokens));

		while (!queue.isEmpty()) {
			currSequence = queue.poll();
			currTokens = currSequence.getTokens();

			for (int i = 0; i < currTokens.size(); i++) {
				System.out.print(currTokens.get(i) + " ");
			}
			System.out.println();

			if (currSequence.isValid(tokenVerifier))
				return currTokens;

			for (int i = 0; i < currSequence.size() - 1; i++) {
				newTokens = new LinkedList<Token>();
				for (int j = 0; j < i; j++) {
					newTokens.add(currTokens.get(j));
				}
				newTokens.add(combineTokens(currTokens, i));
				for (int j = i + 2; j < currSequence.size(); j++) {
					newTokens.add(currTokens.get(j));
				}
				queue.add(new TokenSequence(newTokens));
			}
		}
		return tokens;
	}
}
