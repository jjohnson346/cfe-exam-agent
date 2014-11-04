package financial.fraud.cfe.manual;

import java.io.IOException;

/**
 * TextExtractor provides the interface for uniform treatment of different
 * text extractors for files of different formats.  Implementers of this
 * interface must implement the getText() method, providing logic specific to
 * the underlying file format.
 * 
 * @author Joe
 *
 */
public interface TextExtractor {
	
	/**
	 * the sole method of the interface, returns the text found by the extractor
	 * 
	 * @return 		the string output of the extractor
	 * @throws IOException		could happen if the file containing the text has issues
	 */
	public abstract String getText() throws IOException;
}
