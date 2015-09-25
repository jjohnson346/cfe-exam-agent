package financial.fraud.cfe.logging;

import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.LinkedList;

/**
 * The Logger acts as a simple logging mechanism for the cfe exam agent software.
 * It provides logging output to a set of output devices of the user's choosing 
 * through the addDestination() method.  Calls to the various printf/println methods
 * send output to each of these devices.
 * 
 * This class implements the Singleton design pattern.
 * 
 * @author jjohnson346
 *
 */
public class Logger {

	private DetailLevel level;						// stores the selected level of detail

	private LinkedList<PrintStream> destinations;	// a list of the output destinations

	private static Logger logger;					// static variable for implementing the Singleton 
													// design pattern

	/**
	 * constructor is private, in keeping with the Singleton design pattern.
	 */
	private Logger() {
		destinations = new LinkedList<PrintStream>();
		
		//automatically include the console as an output destination.
		destinations.add(System.out);
		
		//by default, set logging to not log.
		setDetailLevel(DetailLevel.NONE);
	}

	/**
	 * adds a destination to the internally maintained list of printstream 
	 * destinations
	 * 
	 * @param fileName the name of the destination (typically the name of a file)
	 */
	public void addDestination(String fileName) {
		try {
			PrintStream output = new PrintStream(fileName);
			destinations.add(output);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	/**
	 * returns a reference to the single instance of this class,
	 * in keeping with the Singleton design pattern.
	 * 
	 * @return the single instance of the Logger class made available to the system
	 */
	public static Logger getInstance() {
		if (logger == null) {
			logger = new Logger();
		}
		return logger;
	}

	/**
	 * mutator for setting the level of detail for the logger.
	 * 
	 * @param level an instance of the DetailLevel enum
	 */
	public void setDetailLevel(DetailLevel level) {
		this.level = level;
	}

	/**
	 * prints a line of text to each output destination
	 * in the internally stored list of destinations.
	 * Since its considered a line of text, a carriage return/line feed
	 * character is inserted at the end of the text.
	 * 
	 * @param logText the text to output
	 * @param level the detail level
	 */
	public void println(String logText, DetailLevel level) {
		if (level.ordinal() <= this.level.ordinal()) {
			for (PrintStream p : destinations) {
				p.format("%s\n", logText);
				p.flush();
			}
		}
	}

	/**
	 * prints text to each output destination.  Unlike println, does
	 * not insert a carriage return/line feed.
	 * 
	 * @param logText the text to ouptut to each destination
	 * @param level the level of detail
	 */
	public void printf(String logText, DetailLevel level) {
		if (level.ordinal() <= this.level.ordinal()) {
			for (PrintStream p : destinations) {
				p.format("%s", logText);
				p.flush();
			}
		}
	}

	/**
	 * outputs text to each destination, making use of the printf 
	 * method of the PrintStream object for each destination, which 
	 * includes parameter for formatting.
	 * 
	 * @param level the level of detail
	 * @param format a string giving the desired format for the output
	 * @param data the objects to be output, formatted according to the format string
	 */
	public void printf(DetailLevel level, String format, Object... data) {
		if (level.ordinal() <= this.level.ordinal()) {
		for (PrintStream p : destinations) {
			p.printf(format, data);
			p.flush();
		}
		}
	}

	/**
	 * closes the PrintStream object for each destination
	 */
	public void close() {
		for (PrintStream p : destinations) {
			p.close();
		}
	}
}
