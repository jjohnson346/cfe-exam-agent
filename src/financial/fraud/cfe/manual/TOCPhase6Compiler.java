package financial.fraud.cfe.manual;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

/**
 * This class inserts sections into the table of contents that are not reflected in the toc
 * file, but are found within the manual (as sub-sub-subsections) by finding headings with all capital
 * letters.  This increases the granularity of the resulting cfe manual structure that is created from
 * the toc.
 * 
 * Also, this compiler aligns the toc once again.
 * @author Joe
 *
 */
public class TOCPhase6Compiler extends TOCCompiler {

	/**
	 * assigns aggregate to testing area variable, phase = 6.
	 */
	public TOCPhase6Compiler() {
		super("aggregate");
		phase = "6";
	}

	/**
	 * retrieves the source toc from aggregate output file from phase 5.
	 */
	@Override
	protected final String getSourceTOC() {

		String sourceTOC = null;

		Scanner scanner = null;
		try {
			scanner = new Scanner(new File("manual\\2011_fem_aggregate_toc_phase_5.txt"));
			scanner.useDelimiter("\\Z");
			sourceTOC = scanner.next();
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
			System.exit(1);
		} finally {
			scanner.close();
		}
		return sourceTOC;
	}

	public String generateTargetTOC(String sourceTOC) {
		// retrieve the new sections using the section extractor and place them in a queue.
		CFEManualSectionExtractor e = new CFEManualSectionExtractor();
		Queue<String> newSectionsQueue = new LinkedList<String>(e.extractSections());
		
		//retrieve already existing sections from phase 5 toc file.
		Queue<String> existingSectionsQueue = new LinkedList<String>();
		Scanner scanner = new Scanner(sourceTOC);
		while(scanner.hasNextLine()) {
			existingSectionsQueue.add(scanner.nextLine());
		}

		//combine the contents of the 2 queues to create a new, ordered big list
		List<String> combinedSections = new ArrayList<String>();
		
		String currNewSectLine;
		String currExistingSectLine;
		int currDepth = 0;
		
		//remove intro sections from existing sections queue.
		currExistingSectLine = existingSectionsQueue.peek();
		while(getPage(currExistingSectLine).matches("I-[0-9]+")) {
			combinedSections.add(existingSectionsQueue.poll());
			currExistingSectLine = existingSectionsQueue.peek();
		}
		
		// retrieve manual text for figuring out order of sections occurring on same page.
		String manualText = getSourceManualText();
		
		while(!newSectionsQueue.isEmpty() && !existingSectionsQueue.isEmpty()) {
			
			currNewSectLine = newSectionsQueue.peek();
			currExistingSectLine = existingSectionsQueue.peek();
			
			String currNewSectPage = getPage(currNewSectLine);
			String currExistingSectPage = getPage(currExistingSectLine);
			int comp = compare(currNewSectPage, currExistingSectPage);
			
			//if(currExistingSectPage < currNewSectPage) {
			if(comp == 1) { // existing section page before new section page
				combinedSections.add(existingSectionsQueue.poll());
				currDepth = StringUtils.countMatches(currExistingSectLine, "\t");
			} else if (comp == -1) { // new section page before existing section page	
			//} else if (currExistingSectPage > currNewSectPage) {
				combinedSections.add(StringUtils.repeat("\t", currDepth + 1) +  newSectionsQueue.poll());
			} else { //comp = 0 => both sections are on same page - go to manual text to determine which if first.
				int pagePos = manualText.indexOf(String.valueOf(currNewSectPage));
				
				String currNewSectName = getName(currNewSectLine);
				String currExistingSectName = getName(currExistingSectLine);
				
				int currNewSectPos = manualText.indexOf(currNewSectName, pagePos);
				int currExistingSectPos = manualText.indexOf(currExistingSectName, pagePos);
				
				if(currNewSectPos > currExistingSectPos) {
					combinedSections.add(existingSectionsQueue.poll());
					currDepth = StringUtils.countMatches(currExistingSectLine, "\t");
				} else {
					combinedSections.add(StringUtils.repeat("\t", currDepth + 1) +  newSectionsQueue.poll());
				}
			}
		}
		
		//at least one of the queues is now empty.  Remove the elements from the remaining
		//queue.
		while(!existingSectionsQueue.isEmpty())
			combinedSections.add(existingSectionsQueue.poll());
		
		while(!newSectionsQueue.isEmpty())
			combinedSections.add(newSectionsQueue.poll());

		//align each of the toc lines
		for(int i = 0; i < combinedSections.size(); i++) {
			combinedSections.set(i, alignTOC(combinedSections.get(i)));
		}
		
		StringBuilder sb = new StringBuilder();
		for(String section : combinedSections) {
			sb.append(section);
		}
		return new String(sb);
	}
	
	private int compare(String pageA, String pageB) {
//		System.out.print("pageA:  " + pageA + " pageB:  " + pageB);
		Scanner scannerA = new Scanner(pageA);
		scannerA.useDelimiter("\\.");
		int bigPageA = Integer.parseInt(scannerA.next());
		int smallPageA = Integer.parseInt(scannerA.next());
		
		Scanner scannerB = new Scanner(pageB);
		scannerB.useDelimiter("\\.");
		int bigPageB = Integer.parseInt(scannerB.next());
		int smallPageB = Integer.parseInt(scannerB.next());
		
		if(bigPageA < bigPageB) {
			return -1;
		} else if(bigPageA > bigPageB) {
			return 1;
		} else { // big pages equal
			if(smallPageA < smallPageB) {
				return -1;
			} else if(smallPageA > smallPageB) {
				return 1;
			} else { // big and small pages equal
				return 0;
			}
		}
	}
	
	private String getPage(String line) {
		Scanner scanner = new Scanner(line);
		scanner.useDelimiter("\\.{5,}");
		scanner.next();
		return scanner.next().trim();
	}
	
	private String getName(String line) {
		Scanner scanner = new Scanner(line);
		scanner.useDelimiter("\\.{5,}");
		return scanner.next().trim();
	}
	
	/**
	 * tests the phase 6 compiler.
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		TOCPhase6Compiler tocP6Compiler = null;
		int input;

		do {
			System.out.println("TOCPhase6Compiler menu:");
			System.out.println("1.  Compile (P6) TOC.");
			System.out.println("2.  Quit.");

			Scanner scanner = new Scanner(System.in);
			input = scanner.nextInt();

			switch (input) {
			case 1:
				tocP6Compiler = new TOCPhase6Compiler();
				tocP6Compiler.compile();
				break;
			case 2:
				break;
			default:
				System.out.println("Invalid option.  Please enter a valid selection.");
			}
		} while (input != 2);
	}
}
